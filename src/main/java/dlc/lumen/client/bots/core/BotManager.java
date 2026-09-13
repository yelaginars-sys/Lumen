package dlc.lumen.client.bots.core;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.client.bots.BotModuleProfiles;
import dlc.lumen.client.bots.BotTasks;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

public enum BotManager implements QClient {
   INSTANCE;

   private final List<BotSession> botSessions = new CopyOnWriteArrayList<>();
   private final List<BotSession> botSessions2 = new CopyOnWriteArrayList<>();
   private volatile BotSession controlled;
   private volatile BotSession volatileBotSession;
   private ClientStateSnapshot state;
   private Input input2;
   private BotInputState state2;
   private int index;
   private Session session2;
   private boolean flag;

   public String ownerName() {
      Session var1 = this.session2 != null ? this.session2 : mc.getSession();
      return var1 != null && var1.getUsername() != null ? var1.getUsername() : "";
   }

   public List<BotSession> bots() {
      return List.copyOf(this.botSessions);
   }

   public List<BotSession> joined() {
      ArrayList var1 = new ArrayList();

      for (BotSession var3 : this.botSessions) {
         if (var3.isJoined()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public BotSession find(String name) {
      if (name == null) {
         return null;
      }

      for (BotSession var3 : this.botSessions) {
         if (var3.getName().equalsIgnoreCase(name)) {
            return var3;
         }
      }

      for (BotSession var5 : this.botSessions2) {
         if (var5.getName().equalsIgnoreCase(name)) {
            return var5;
         }
      }

      return null;
   }

   public boolean isConnected(String name) {
      BotSession var2 = this.find(name);
      return var2 != null && var2.isJoined();
   }

   public BotSession connect(String name, String address) {
      String var3 = address;
      int var4 = 25565;
      int var5 = address.lastIndexOf(58);
      if (var5 > 0) {
         var3 = address.substring(0, var5);

         try {
            var4 = Integer.parseInt(address.substring(var5 + 1).trim());
         } catch (NumberFormatException var7) {
            var3 = address;
         }
      }

      return this.connect(name, var3, var4);
   }

   public BotSession connect(String name, String host, int port) {
      this.botSessions.removeIf(bot -> !bot.isJoined() && bot.getName().equalsIgnoreCase(name));
      BotSession var4 = new BotSession(name, host, port);
      this.botSessions2.add(var4);
      var4.connect();
      BotLog.log("[" + name + "] заход на " + host + ":" + port);
      return var4;
   }

   public boolean remove(String name) {
      BotSession var2 = this.find(name);
      if (var2 == null) {
         return false;
      }

      var2.disconnect("removed");
      this.botSessions.remove(var2);
      this.botSessions2.remove(var2);
      this.releaseIfControlling(var2);
      BotViewRenderer.INSTANCE.free(var2);
      BotTasks.INSTANCE.forget(name);
      BotModuleProfiles.forget(name);
      return true;
   }

   public void disconnectAll() {
      for (BotSession var2 : this.botSessions2) {
         var2.disconnect("shutdown");
      }

      for (BotSession var4 : this.botSessions) {
         var4.disconnect("shutdown");
      }

      this.botSessions2.clear();
      this.botSessions.clear();
      this.releaseControl();
      BotViewRenderer.INSTANCE.freeAll();
   }

   @EventLink
   public void onUpdate(EventTickPre event) {
      this.tick();
   }

   public void tick() {
      for (BotSession var2 : this.botSessions2) {
         var2.tick();
      }

      for (BotSession var5 : this.botSessions) {
         var5.tick();
      }

      if (this.volatileBotSession != null && this.volatileBotSession.isJoined() && !this.flag) {
         this.control(this.volatileBotSession);
      }

      BotSession var4 = this.controlled;
      if (var4 != null && var4.isJoined()) {
         BotTasks.INSTANCE.tickViewed(var4);
      }
   }

   void markJoined(BotSession session) {
      this.botSessions2.remove(session);
      this.botSessions.removeIf(bot -> bot != session && bot.getName().equalsIgnoreCase(session.getName()));
      if (!this.botSessions.contains(session)) {
         this.botSessions.add(session);
      }
   }

   void markDisconnected(BotSession session) {
      this.botSessions2.remove(session);
      if (this.volatileBotSession == session) {
         this.volatileBotSession = null;
      }

      if (this.find(session.getName()) == null) {
         this.botSessions.add(session);
      }
   }

   public BotSession getControlled() {
      return this.controlled;
   }

   public boolean isControlling() {
      return this.controlled != null && this.controlled.isJoined();
   }

   public boolean isViewingBot(BotSession session) {
      return this.flag && this.controlled == session;
   }

   public boolean isControlledChatHandler(Object handler) {
      return this.controlled != null && this.controlled.ownsNetworkHandler(handler);
   }

   public boolean control(String name) {
      BotSession var2 = this.find(name);
      return var2 != null && this.control(var2);
   }

   public boolean control(BotSession bot) {
      if (bot != null && bot.isJoined()) {
         MinecraftClient var2 = mc;
         BotInputState var3 = BotInputState.capture(var2);
         if (!this.flag) {
            this.state = ClientStateSnapshot.capture(var2);
            this.input2 = var2.player != null ? var2.player.input : null;
            this.state2 = BotInputState.capture(var2);
            this.index = var2.options.getClampedViewDistance();
            this.session2 = var2.getSession();
            this.flag = true;
         } else if (this.controlled != null) {
            this.controlled.inputState().clear();
            this.controlled.exitView();
            this.controlled = null;
         }

         if (bot.enterView()) {
            bot.inputState().copyFrom(var3);
            bot.inputState().apply(var2);
            ((IMinecraftClientAccessor)var2).setSession(bot.session());
            BotModuleProfiles.switchTo(bot.getName());
            this.controlled = bot;
            this.volatileBotSession = null;
            this.updateState();
            return true;
         } else {
            this.updateState2();
            return false;
         }
      } else {
         return false;
      }
   }

   public void releaseControl() {
      MinecraftClient var1 = mc;
      if (this.controlled != null) {
         this.state2 = BotInputState.capture(var1);
         this.controlled.inputState().clear();
         this.controlled.exitView();
      }

      this.updateState2();
      this.controlled = null;
      this.volatileBotSession = null;
      this.updateState();
   }

   private void updateState() {
      if (mc.inGameHud != null) {
         mc.inGameHud.clearTitle();
         mc.inGameHud.setOverlayMessage(Text.empty(), false);
         if (mc.inGameHud.getBossBarHud() != null) {
            mc.inGameHud.getBossBarHud().clear();
         }
      }
   }

   void releaseIfControlling(BotSession bot) {
      if (this.controlled == bot) {
         this.releaseControl();
      }
   }

   void suspendControlForTransition(BotSession session) {
      if (this.controlled == session) {
         this.releaseControl();
         this.volatileBotSession = session;
      }
   }

   private void updateState2() {
      if (this.flag) {
         MinecraftClient var1 = mc;
         ClientStateSnapshot var2 = this.state;
         if (var2 != null) {
            ClientWorld var3 = var2.world();
            if (var1.world != var3) {
               var1.world = var3;
               if (var3 != null) {
                  var1.particleManager.setWorld(var3);
                  var1.worldRenderer.setWorld(var3);
               }
            }

            ClientPlayerEntity var4 = var2.player();
            var1.player = var4;
            if (var4 != null) {
               var4.input = this.input2 != null ? this.input2 : new KeyboardInput(var1.options);
            }

            var1.interactionManager = var2.interactionManager();
            var1.setCameraEntity(var2.cameraEntity() != null ? var2.cameraEntity() : var4);
            var1.currentScreen = var2.screen();
            var1.options.setServerViewDistance(this.index);
         }

         if (this.session2 != null) {
            ((IMinecraftClientAccessor)var1).setSession(this.session2);
            this.session2 = null;
         }

         BotModuleProfiles.switchToOwner();
         this.state = null;
         this.input2 = null;
         if (this.state2 != null) {
            this.state2.apply(var1);
            this.state2 = null;
         }

         this.index = 0;
         this.flag = false;
      }
   }

   static {
      BotLifecycle.register(new BotLifecycleListener() {
         @Override
         public void onStateChanged(BotSession bot, BotSession.State from, BotSession.State to) {
            if (to == BotSession.State.PLAY) {
               BotManager.INSTANCE.markJoined(bot);
            } else if (to == BotSession.State.DISCONNECTED) {
               BotManager.INSTANCE.markDisconnected(bot);
            }
         }

         @Override
         public void onDisconnected(BotSession bot, String reason) {
            BotManager.INSTANCE.releaseIfControlling(bot);
         }
      });
   }
}
