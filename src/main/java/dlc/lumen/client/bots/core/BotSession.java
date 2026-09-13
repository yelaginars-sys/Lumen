package dlc.lumen.client.bots.core;

import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.bots.core.behavior.ServerBehavior;
import dlc.lumen.client.bots.core.behavior.ServerBehaviorRegistry;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.message.LastSeenMessageList.Acknowledgment;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.state.LoginStates;
import net.minecraft.text.Text;

public final class BotSession {
   private final String host2;
   private final String host3;
   private final int host4;
   private final ServerBehavior serverBehavior;
   private volatile BotSession.State host5 = BotSession.State.NEW;
   private volatile ClientConnection volatileClientConnection;
   private volatile BotPlayNetworkHandler volatileBotPlayNetworkHandler;
   private volatile boolean volatileboolean;
   private static final int INDEX = 100;
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
   private final List<ChatMessage> chatLog = new ArrayList<>();
   private final List<Text> chatLog2 = new ArrayList<>();
   private final BotInputState chatLog3 = new BotInputState();
   private final Session session2;
   private volatile String chatLog4 = "";

   BotSession(String name, String host, int port) {
      this.host2 = name;
      this.host3 = host;
      this.host4 = port;
      this.serverBehavior = ServerBehaviorRegistry.resolve(host);
      this.session2 = new Session(name, this.serverBehavior.identity().offlineUuid(name), "0", Optional.empty(), Optional.empty(), AccountType.LEGACY);
   }

   public Session session() {
      return this.session2;
   }

   public void connect() {
      this.serverBehavior.identity().beforeConnect();
      this.host5 = BotSession.State.CONNECTING;
      new Thread(this::updateState).start();
   }

   private void updateState() {
      MinecraftClient var1 = MinecraftClient.getInstance();

      try {
         InetSocketAddress var2 = new InetSocketAddress(InetAddress.getByName(this.host3), this.host4);
         ClientConnection var3 = new ClientConnection(NetworkSide.CLIENTBOUND);
         ClientConnection.connect(var2, false, var3).syncUninterruptibly();
         this.volatileClientConnection = var3;
         BotLoginNetworkHandler var4 = new BotLoginNetworkHandler(var3, var1, this, this.serverBehavior);
         var3.connect(this.host3, this.host4, LoginStates.C2S, LoginStates.S2C, var4, false);
         Thread.sleep(500L);
         var3.send(new LoginHelloC2SPacket(this.host2, this.serverBehavior.identity().offlineUuid(this.host2)));
         this.host5 = BotSession.State.LOGIN;
      } catch (Exception var5) {
         this.setState(BotSession.State.DISCONNECTED);
         var5.printStackTrace();
      }
   }

   public void tick() {
      ClientConnection var1 = this.volatileClientConnection;
      if (var1 != null) {
         if (var1.isOpen()) {
            var1.tick();
         } else if (!this.volatileboolean) {
            this.volatileboolean = true;
            var1.handleDisconnection();
         }
      }
   }

   public boolean sendChat(String message) {
      if (this.volatileBotPlayNetworkHandler != null && this.volatileClientConnection != null && this.volatileClientConnection.isOpen()) {
         if (message.startsWith("/")) {
            this.volatileClientConnection.send(new CommandExecutionC2SPacket(message.substring(1)));
         } else {
            this.volatileClientConnection
               .send(new ChatMessageC2SPacket(message, Instant.now(), ThreadLocalRandom.current().nextLong(), null, new Acknowledgment(0, new BitSet())));
         }

         return true;
      } else {
         return false;
      }
   }

   public void disconnect(String reason) {
      ClientConnection var2 = this.volatileClientConnection;
      if (var2 != null && var2.isOpen()) {
         var2.disconnect(Text.literal(reason));
      }

      this.chatLog3.clear();
      this.setState(BotSession.State.DISCONNECTED);
   }

   void setState(BotSession.State state) {
      BotSession.State var2 = this.host5;
      this.host5 = state;
      BotLifecycle.fireStateChanged(this, var2, state);
   }

   void setPlayHandler(BotPlayNetworkHandler playHandler) {
      this.volatileBotPlayNetworkHandler = playHandler;
   }

   boolean ownsNetworkHandler(Object handler) {
      return this.volatileBotPlayNetworkHandler != null && this.volatileBotPlayNetworkHandler.isVisualHandler(handler);
   }

   boolean enterView() {
      return this.volatileBotPlayNetworkHandler != null && this.volatileBotPlayNetworkHandler.enterView();
   }

   void exitView() {
      if (this.volatileBotPlayNetworkHandler != null) {
         this.volatileBotPlayNetworkHandler.exitView();
      }
   }

   void logChat(String message) {
      this.chatLog.add(new ChatMessage(LocalTime.now().format(DATE_TIME_FORMATTER), message));

      while (this.chatLog.size() > 100) {
         this.chatLog.remove(0);
      }
   }

   public List<ChatMessage> getChatLog() {
      return this.chatLog;
   }

   void addHudMessage(Text text) {
      if (text != null) {
         this.chatLog2.add(text);

         while (this.chatLog2.size() > 100) {
            this.chatLog2.remove(0);
         }
      }
   }

   List<Text> getHudHistory() {
      return this.chatLog2;
   }

   void onDisconnected(String reason) {
      this.chatLog3.clear();
      BotLifecycle.fireDisconnected(this, reason);
      this.setState(BotSession.State.DISCONNECTED);
      BotLog.log("[" + this.host2 + "] отключён: " + BotLog.trim(reason));
      ChatUtils.sendMessage("§7[Bot] §f" + this.host2 + " отключён: " + BotLog.trim(reason));
   }

   BotInputState inputState() {
      return this.chatLog3;
   }

   public BotInputState input() {
      return this.chatLog3;
   }

   void fireChat(Text message) {
      BotLifecycle.fireChat(this, message);
   }

   public void requestHelp(String reason) {
      this.chatLog4 = reason == null ? "" : reason;
   }

   public boolean needsHelp() {
      return !this.chatLog4.isEmpty();
   }

   public String helpReason() {
      return this.chatLog4;
   }

   public boolean runAsBot(Runnable action) {
      return this.volatileBotPlayNetworkHandler != null && this.volatileBotPlayNetworkHandler.runAsBot(action);
   }

   public ClientPlayerEntity player() {
      return this.visualPlayer();
   }

   public ClientWorld world() {
      return this.visualWorld();
   }

   public String getHost() {
      return this.host3;
   }

   public int getPort() {
      return this.host4;
   }

   public String getName() {
      return this.host2;
   }

   public String getAddress() {
      return this.serverBehavior.identity().isOffline(this.host3, this.host4)
         ? "No Server"
         : this.host3 + ":" + this.host4;
   }

   public String getStatus() {
      return this.host5.name().toLowerCase();
   }

   public BotSession.State getState() {
      return this.host5;
   }

   public boolean isJoined() {
      return this.host5 == BotSession.State.PLAY;
   }

   ClientWorld visualWorld() {
      return this.volatileBotPlayNetworkHandler != null ? this.volatileBotPlayNetworkHandler.visualWorld() : null;
   }

   ClientPlayerEntity visualPlayer() {
      return this.volatileBotPlayNetworkHandler != null ? this.volatileBotPlayNetworkHandler.visualPlayer() : null;
   }

   Entity visualCamera() {
      return this.volatileBotPlayNetworkHandler != null ? this.volatileBotPlayNetworkHandler.visualCamera() : null;
   }

   ClientPlayerInteractionManager visualInteraction() {
      return this.volatileBotPlayNetworkHandler != null ? this.volatileBotPlayNetworkHandler.visualInteraction() : null;
   }

   Screen visualScreen() {
      return this.volatileBotPlayNetworkHandler != null ? this.volatileBotPlayNetworkHandler.visualScreen() : null;
   }

   boolean runWithVisualScreen(Runnable action) {
      return this.volatileBotPlayNetworkHandler != null && this.volatileBotPlayNetworkHandler.runWithVisualScreen(action);
   }

   public boolean canRenderView() {
      return this.isJoined() && this.visualWorld() != null && this.visualCamera() != null;
   }

   public enum State {
      NEW,
      CONNECTING,
      LOGIN,
      CONFIG,
      PLAY,
      DISCONNECTED;
   }
}
