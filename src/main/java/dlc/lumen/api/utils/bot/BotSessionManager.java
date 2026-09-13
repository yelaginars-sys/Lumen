package dlc.lumen.api.utils.bot;

import dlc.lumen.mixin.IMinecraftClientAccessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class BotSessionManager {
   private static final List<BotSessionManager.BotConnection> connections = new CopyOnWriteArrayList<>();
   private static volatile boolean address;
   private static volatile boolean connection;

   public static List<BotSessionManager.BotConnection> getConnections() {
      handleAction();
      return new ArrayList<>(connections);
   }

   public static boolean shouldBypassResourcePacks() {
      return connection;
   }

   public static void finishBotConnectStage() {
      connection = false;
   }

   public static String getCurrentSessionName() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0.getSession() == null ? "" : var0.getSession().getUsername();
   }

   public static List<String> getSessionNames(boolean includeCurrent) {
      handleAction();
      LinkedHashSet var1 = new LinkedHashSet();
      if (includeCurrent) {
         String var2 = getCurrentSessionName();
         if (!var2.isBlank()) {
            var1.add(var2);
         }
      }

      for (BotSessionManager.BotConnection var3 : connections) {
         if (var3.name() != null && !var3.name().isBlank()) {
            var1.add(var3.name());
         }
      }

      return new ArrayList<>(var1);
   }

   public static boolean toggleIgnoreBotMessages() {
      address = !address;
      return address;
   }

   public static boolean isIgnoreBotMessages() {
      return address;
   }

   public static void connect(String name, String address) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.getSession() != null && name != null && !name.isBlank() && address != null && !address.isBlank()) {
         Session var3 = var2.getSession();
         ServerInfo var4 = var2.getCurrentServerEntry();
         handleAction();
         calcName(name, Text.literal("Replaced"));
         BotSessionManager.BotConnection var5 = createBotConnection();
         ((IMinecraftClientAccessor)var2).setSession(getCurrent(var2.getSession(), name));
         connection = true;
         var2.execute(
            () -> {
               try {
                  ConnectScreen.connect(
                     new MultiplayerScreen(new TitleScreen()),
                     var2,
                     ServerAddress.parse(address),
                     new ServerInfo(address, address, ServerType.OTHER),
                     false,
                     null
                  );
               } catch (Exception var6) {
                  connection = false;
                  handleMc3(var2, var5, var3, var4);
               }
            }
         );
      }
   }

   public static void pulseBots(boolean rightClick) {
      for (BotSessionManager.BotConnection var2 : connections) {
         if (checkBot2(var2)) {
            if (rightClick) {
               var2.handler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, var2.player().getYaw(), var2.player().getPitch()));
            } else {
               var2.handler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
         }
      }
   }

   public static void sayAll(String message) {
      for (BotSessionManager.BotConnection var2 : connections) {
         if (checkBot2(var2)) {
            if (message.startsWith("/")) {
               var2.handler().sendChatCommand(message.substring(1));
            } else {
               var2.handler().sendChatMessage(message);
            }
         }
      }
   }

   public static boolean control(String name) {
      if (name != null && !name.isBlank()) {
         handleAction();
         MinecraftClient var1 = MinecraftClient.getInstance();
         return var1.player != null && var1.world != null && name.equalsIgnoreCase(getCurrentSessionName())
            ? true
            : connections.stream().filter(bot -> checkLeft(bot.name(), name)).findFirst().map(bot -> {
               if (!checkBot2(bot)) {
                  connections.remove(bot);
                  return false;
               }

               BotSessionManager.BotConnection var1x = createBotConnection();
               if (!checkBot(bot)) {
                  if (var1x != null && checkBot(var1x)) {
                     connections.remove(var1x);
                  }

                  return false;
               } else {
                  connections.remove(bot);
                  return true;
               }
            }).orElse(false);
      } else {
         return false;
      }
   }

   public static boolean say(String name, String message) {
      handleAction();
      return connections.stream().filter(bot -> checkLeft(bot.name(), name)).findFirst().map(bot -> {
         if (!checkBot2(bot)) {
            connections.remove(bot);
            return false;
         }

         if (message.startsWith("/")) {
            bot.handler().sendChatCommand(message.substring(1));
         } else {
            bot.handler().sendChatMessage(message);
         }

         return true;
      }).orElse(false);
   }

   public static boolean remove(String name) {
      return name != null && !name.isBlank() ? calcName(name, Text.literal("Removed")) > 0 : false;
   }

   public static boolean restore() {
      return restore(null);
   }

   public static boolean restore(String name) {
      handleAction();
      String var1 = name != null && !name.isBlank() ? name : (connections.isEmpty() ? "" : connections.get(connections.size() - 1).name());
      return !var1.isBlank() && control(var1);
   }

   private static BotSessionManager.BotConnection createBotConnection() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.getNetworkHandler() != null && var0.world != null && var0.player != null) {
         ClientPlayNetworkHandler var1 = var0.getNetworkHandler();
         handleHandler(var1, var0.getSession().getUsername(), var0.player);
         BotSessionManager.BotConnection var2 = new BotSessionManager.BotConnection(
            var0.getSession().getUsername(),
            var0.getCurrentServerEntry() != null ? var0.getCurrentServerEntry().address : "",
            var1.getConnection(),
            var1,
            var0.world,
            var0.player,
            var0.interactionManager,
            var0.getSession(),
            var0.getCurrentServerEntry()
         );
         handleConnection(var2);
         handleMc(var0);
         return var2;
      } else {
         return null;
      }
   }

   private static boolean checkBot(BotSessionManager.BotConnection bot) {
      if (!checkBot2(bot)) {
         return false;
      }

      MinecraftClient var1 = MinecraftClient.getInstance();
      IMinecraftClientAccessor var2 = (IMinecraftClientAccessor)var1;
      Channel var3 = getConnection(bot.connection());
      if (var3 != null && var3.pipeline().get("bot_filter") != null) {
         var3.pipeline().remove("bot_filter");
      }

      try {
         handleMc2(var1, ClientPlayNetworkHandler.class, bot.handler());
         var2.setSession(bot.session() != null ? bot.session() : getCurrent(var1.getSession(), bot.name()));
         handleMc2(var1, ServerInfo.class, bot.serverInfo() != null ? bot.serverInfo() : getName(bot.name(), bot.address()));
         var2.setItemUseCooldown(0);
         var1.world = bot.world();
         var1.player = bot.player();
         var1.cameraEntity = bot.player();
         var1.interactionManager = bot.interactionManager();
         if (var1.worldRenderer != null) {
            var1.worldRenderer.setWorld(bot.world());
         }

         bot.handler()
            .sendPacket(
               new Full(
                  bot.player().getX(),
                  bot.player().getY(),
                  bot.player().getZ(),
                  bot.player().getYaw(),
                  bot.player().getPitch(),
                  bot.player().isOnGround(),
                  bot.player().horizontalCollision
               )
            );
         var1.setScreen(null);
         return true;
      } catch (Exception var5) {
         return false;
      }
   }

   private static void handleMc(MinecraftClient mc) {
      IMinecraftClientAccessor var1 = (IMinecraftClientAccessor)mc;
      handleMc2(mc, ClientPlayNetworkHandler.class, null);
      var1.setItemUseCooldown(0);
      mc.world = null;
      mc.player = null;
      mc.cameraEntity = null;
      mc.interactionManager = null;
      if (mc.worldRenderer != null) {
         mc.worldRenderer.setWorld(null);
      }
   }

   private static void handleConnection(BotSessionManager.BotConnection connection) {
      calcName(connection.name(), Text.literal("Replaced"));
         connections.add(connection);
   }

   private static void handleHandler(ClientPlayNetworkHandler handler, String name, ClientPlayerEntity botPlayer) {
      Channel var3 = getConnection(handler.getConnection());
      if (var3 != null) {
         if (var3.pipeline().get("bot_filter") != null) {
            var3.pipeline().remove("bot_filter");
         }

         if (var3.pipeline().get("packet_handler") != null) {
            var3.pipeline()
               .addBefore(
                  "packet_handler",
                  "bot_filter",
                  new ChannelDuplexHandler() {
                     public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof KeepAliveS2CPacket var9) {
                           handler.getConnection().send(new KeepAliveC2SPacket(var9.getId()));
                           if (botPlayer != null) {
                              handler.sendPacket(new OnGroundOnly(botPlayer.isOnGround(), botPlayer.horizontalCollision));
                           }

                           ReferenceCountUtil.release(msg);
                        } else if (msg instanceof CommonPingS2CPacket var8) {
                           handler.getConnection().send(new CommonPongC2SPacket(var8.getParameter()));
                           ReferenceCountUtil.release(msg);
                        } else if (msg instanceof ResourcePackSendS2CPacket var7) {
                           handler.sendPacket(new ResourcePackStatusC2SPacket(var7.id(), Status.ACCEPTED));
                           handler.sendPacket(new ResourcePackStatusC2SPacket(var7.id(), Status.SUCCESSFULLY_LOADED));
                           ReferenceCountUtil.release(msg);
                        } else if (msg instanceof PlayerPositionLookS2CPacket var6) {
                           BotSessionManager.handleBotPlayer(botPlayer, var6);
                           handler.sendPacket(new TeleportConfirmC2SPacket(var6.teleportId()));
                           if (botPlayer != null) {
                              handler.sendPacket(
                                 new Full(
                                    botPlayer.getX(),
                                    botPlayer.getY(),
                                    botPlayer.getZ(),
                                    botPlayer.getYaw(),
                                    botPlayer.getPitch(),
                                    botPlayer.isOnGround(),
                                    botPlayer.horizontalCollision
                                 )
                              );
                           }

                           ReferenceCountUtil.release(msg);
                        } else if (msg instanceof EntityPositionSyncS2CPacket var5) {
                           BotSessionManager.handleBotPlayer2(botPlayer, var5);
                           ReferenceCountUtil.release(msg);
                        } else if (msg instanceof HealthUpdateS2CPacket var4) {
                           if (botPlayer != null) {
                              botPlayer.setHealth(var4.getHealth());
                           }

                           ReferenceCountUtil.release(msg);
                        } else if (msg instanceof DisconnectS2CPacket) {
                           BotSessionManager.connections.removeIf(bot -> BotSessionManager.checkLeft(bot.name(), name));
                           ctx.close();
                           ReferenceCountUtil.release(msg);
                        } else {
                           String var3x = msg.getClass().getSimpleName();
                           if (!var3x.contains("Sound")
                              && !var3x.contains("Particle")
                              && !var3x.contains("Screen")
                              && (!BotSessionManager.address || !BotSessionManager.checkPacketName(var3x))
                              && !var3x.contains("Explosion")
                              && !var3x.contains("BossBar")
                              && !var3x.contains("Scoreboard")
                              && !var3x.contains("OverlayMessage")) {
                              super.channelRead(ctx, msg);
                           } else {
                              ReferenceCountUtil.release(msg);
                           }
                        }
                     }

                     public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        BotSessionManager.connections.removeIf(bot -> BotSessionManager.checkLeft(bot.name(), name));
                        super.channelInactive(ctx);
                     }

                     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        BotSessionManager.connections.removeIf(bot -> BotSessionManager.checkLeft(bot.name(), name));
                        ctx.close();
                     }
                  }
               );
         }
      }
   }

   private static boolean checkPacketName(String packetName) {
      return packetName.contains("Chat") || packetName.contains("Message") || packetName.contains("Title") || packetName.contains("Overlay");
   }

   private static Channel getConnection(ClientConnection connection) {
      try {
         for (Field var4 : ClientConnection.class.getDeclaredFields()) {
            if (Channel.class.isAssignableFrom(var4.getType())) {
               var4.setAccessible(true);
               return (Channel)var4.get(connection);
            }
         }
      } catch (Exception var5) {
      }

      return null;
   }

   private static void handleBotPlayer(ClientPlayerEntity botPlayer, PlayerPositionLookS2CPacket packet) {
      if (botPlayer != null && packet != null) {
         double var2 = calcPacket(packet, "x", botPlayer.getX());
         double var4 = calcPacket(packet, "y", botPlayer.getY());
         double var6 = calcPacket(packet, "z", botPlayer.getZ());
         float var8 = (float)calcPacket(packet, "yaw", botPlayer.getYaw());
         float var9 = (float)calcPacket(packet, "pitch", botPlayer.getPitch());
         Object var10 = getPacket(packet, "change");
         if (var10 == null) {
            var10 = getPacket(packet, "flags");
         }

         if (checkFlags(var10, "X")) {
            var2 += botPlayer.getX();
         }

         if (checkFlags(var10, "Y")) {
            var4 += botPlayer.getY();
         }

         if (checkFlags(var10, "Z")) {
            var6 += botPlayer.getZ();
         }

         if (checkFlags(var10, "Y_ROT")) {
            var8 += botPlayer.getYaw();
         }

         if (checkFlags(var10, "X_ROT")) {
            var9 += botPlayer.getPitch();
         }

         var9 = MathHelper.clamp(var9, -90.0F, 90.0F);
         botPlayer.refreshPositionAndAngles(var2, var4, var6, var8, var9);
         botPlayer.setYaw(var8);
         botPlayer.setPitch(var9);
      }
   }

   private static void handleBotPlayer2(ClientPlayerEntity botPlayer, EntityPositionSyncS2CPacket packet) {
      if (botPlayer != null && packet != null && packet.id() == botPlayer.getId() && packet.values() != null) {
         Vec3d var2 = packet.values().position();
         if (var2 != null) {
            float var3 = packet.values().yaw();
            float var4 = MathHelper.clamp(packet.values().pitch(), -90.0F, 90.0F);
            botPlayer.refreshPositionAndAngles(var2.x, var2.y, var2.z, var3, var4);
            botPlayer.setYaw(var3);
            botPlayer.setPitch(var4);
            if (packet.values().deltaMovement() != null) {
               botPlayer.setVelocity(packet.values().deltaMovement());
            }

            botPlayer.setOnGround(packet.onGround());
         }
      }
   }

   private static double calcPacket(Object packet, String name, double fallback) {
      return getPacket(packet, name) instanceof Number var5 ? var5.doubleValue() : fallback;
   }

   private static Object getPacket(Object packet, String name) {
      if (packet != null && name != null && !name.isBlank()) {
         try {
            Method var13 = packet.getClass().getMethod(name);
            var13.setAccessible(true);
            return var13.invoke(packet);
         } catch (Exception var10) {
            try {
               Method var12 = packet.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
               var12.setAccessible(true);
               return var12.invoke(packet);
            } catch (Exception var9) {
               try {
                  RecordComponent[] var2 = packet.getClass().getRecordComponents();
                  if (var2 != null) {
                     for (RecordComponent var6 : var2) {
                        if (name.equals(var6.getName())) {
                           return var6.getAccessor().invoke(packet);
                        }
                     }
                  }
               } catch (Exception var8) {
               }

               try {
                  for (Field var16 : packet.getClass().getDeclaredFields()) {
                     if (name.equalsIgnoreCase(var16.getName())) {
                        var16.setAccessible(true);
                        return var16.get(packet);
                     }
                  }
               } catch (Exception var7) {
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static boolean checkFlags(Object flags, String flagName) {
      if (flags instanceof Iterable var2 && flagName != null) {
         for (Object var4 : var2) {
            if (var4 instanceof Enum var5 && flagName.equals(var5.name())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static Session getCurrent(Session current, String name) {
      try {
         Constructor var2 = Session.class.getDeclaredConstructor(String.class, UUID.class, String.class, Optional.class, Optional.class, AccountType.class);
         var2.setAccessible(true);
         return (Session)var2.newInstance(
            name, UUID.randomUUID(), current == null ? "" : current.getAccessToken(), Optional.empty(), Optional.empty(), AccountType.MOJANG
         );
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private static void handleMc2(MinecraftClient mc, Class<?> fieldType, Object value) {
      try {
         for (Field var6 : MinecraftClient.class.getDeclaredFields()) {
            if (var6.getType() == fieldType) {
               var6.setAccessible(true);
               var6.set(mc, value);
               return;
            }
         }
      } catch (Exception var7) {
      }
   }

   private static ServerInfo getName(String name, String address) {
      String var2 = address == null ? "" : address;
      String var3 = name != null && !name.isBlank() ? name : var2;
      return new ServerInfo(var3, var2, ServerType.OTHER);
   }

   private static void handleMc3(MinecraftClient mc, BotSessionManager.BotConnection previous, Session originalSession, ServerInfo originalServerInfo) {
      try {
         connection = false;
         if (previous != null && checkBot(previous)) {
            connections.remove(previous);
            return;
         }

         IMinecraftClientAccessor var4 = (IMinecraftClientAccessor)mc;
         var4.setSession(originalSession);
         handleMc2(mc, ServerInfo.class, originalServerInfo);
      } catch (Exception var5) {
      }
   }

   private static int calcName(String name, Text reason) {
      if (name != null && !name.isBlank()) {
         int var2 = 0;

         for (BotSessionManager.BotConnection var4 : new ArrayList<>(connections)) {
            if (checkLeft(var4.name(), name)) {
               connections.remove(var4);
               var2++;

               try {
                  if (var4.connection() != null) {
                     var4.connection().disconnect(reason);
                  }
               } catch (Exception var6) {
               }
            }
         }

         return var2;
      } else {
         return 0;
      }
   }

   private static void handleAction() {
      connections.removeIf(bot -> !checkBot2(bot));
   }

   private static boolean checkBot2(BotSessionManager.BotConnection bot) {
      if (bot == null || bot.name() == null || bot.name().isBlank()) {
         return false;
      }

      if (bot.connection() != null && bot.handler() != null && bot.world() != null && bot.player() != null) {
         if (bot.player().networkHandler != bot.handler()) {
            return false;
         }

         Channel var1 = getConnection(bot.connection());
         return var1 == null || var1.isOpen();
      } else {
         return false;
      }
   }

   private static boolean checkLeft(String left, String right) {
      return left != null && right != null && left.equalsIgnoreCase(right);
   }

   public static final class BotConnection {
      private final String name;
      private final String address;
      private final ClientConnection connection;
      private final ClientPlayNetworkHandler handler;
      private final ClientWorld world;
      private final ClientPlayerEntity player;
      private final ClientPlayerInteractionManager interactionManager;
      private final Session session;
      private final ServerInfo serverInfo;

      public BotConnection(
         String name,
         String address,
         ClientConnection connection,
         ClientPlayNetworkHandler handler,
         ClientWorld world,
         ClientPlayerEntity player,
         ClientPlayerInteractionManager interactionManager,
         Session session,
         ServerInfo serverInfo
      ) {
         this.name = name;
         this.address = address;
         this.connection = connection;
         this.handler = handler;
         this.world = world;
         this.player = player;
         this.interactionManager = interactionManager;
         this.session = session;
         this.serverInfo = serverInfo;
      }

      public String name() {
         return this.name;
      }

      public String address() {
         return this.address;
      }

      public ClientConnection connection() {
         return this.connection;
      }

      public ClientPlayNetworkHandler handler() {
         return this.handler;
      }

      public ClientWorld world() {
         return this.world;
      }

      public ClientPlayerEntity player() {
         return this.player;
      }

      public ClientPlayerInteractionManager interactionManager() {
         return this.interactionManager;
      }

      public Session session() {
         return this.session;
      }

      public ServerInfo serverInfo() {
         return this.serverInfo;
      }
   }
}
