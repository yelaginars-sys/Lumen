package dlc.lumen;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.storages.InitializeStorage;
import dlc.lumen.api.storages.implement.CommandStorage;
import dlc.lumen.api.storages.implement.ConfigStorage;
import dlc.lumen.api.storages.implement.DragStorage;
import dlc.lumen.api.storages.implement.FreeLookStorage;
import dlc.lumen.api.storages.implement.FriendStorage;
import dlc.lumen.api.storages.implement.LocalizationStorage;
import dlc.lumen.api.storages.implement.MacroStorage;
import dlc.lumen.api.storages.implement.ModuleStorage;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.storages.implement.ServerStorage;
import dlc.lumen.api.storages.implement.StaffStorage;
import dlc.lumen.api.storages.implement.ThemeStorage;
import dlc.lumen.api.storages.implement.WaypointStorage;
import dlc.lumen.api.utils.client.UserInfo;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.item.ItemPickupTracker;
import dlc.lumen.api.utils.rpc.DiscordManager;
import dlc.lumen.api.utils.tps.TPSCalc;
import dlc.lumen.client.autobuy.AutoBuyConfig;
import dlc.lumen.client.autobuy.AutoBuyEngine;
import dlc.lumen.client.autobuy.ItemDatabase;
import dlc.lumen.client.autoset.AutoSetCollector;
import dlc.lumen.client.bots.BotAutoJoin;
import dlc.lumen.client.bots.BotRegistry;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.world.BotScreenBoards;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.render.figura.FiguraAvatarPersistence;
import dlc.lumen.client.render.figura.FiguraBridge;
import dlc.lumen.client.render.figura.FiguraKeybindGuard;
import dlc.lumen.client.render.figura.FriendModelManager;
import dlc.lumen.client.render.models.CosmeticWorldRenderer;
import dlc.lumen.client.render.models.PetManager;
import dlc.lumen.client.social.GlobalSocialManager;
import dlc.lumen.client.social.GlobalSocialStorage;
import dlc.lumen.client.voice.call.VoiceCallManager;
import java.io.File;
import lombok.Generated;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Start;
import org.lwjgl.glfw.GLFW;

public enum Lumen implements ModInitializer, QClient {
   INSTANCE;

   public boolean isServer;
   private static double prevTime = 0.0;
   public static double deltaTime = 0.0;
   public InitializeStorage initializer;
   public ModuleStorage moduleStorage;
   public ThemeStorage themeStorage;
   public TPSCalc tpsCalc;
   public ServerStorage serverStorage;
   public RotationStorage rotationStorage;
   public FreeLookStorage freeLookStorage;
   public CommandStorage commandStorage;
   public LocalizationStorage localizationStorage;
   public ConfigStorage configStorage;
   public FriendStorage friendStorage;
   public MacroStorage macroStorage;
   public StaffStorage staffStorage;
   public WaypointStorage waypointStorage;
   public GlobalSocialStorage globalSocialStorage;
   public GlobalSocialManager globalSocialManager;
   public DiscordManager discordManager;
   public UserInfo userInfo = UserInfo.empty();
   public File globalsDir;
   public File configsDir;
   public File abItemsDir;

   public void onInitialize() {
      this.initStorage();
      WorldRenderEvents.START.register((Start)client -> {
         double currentTime = GLFW.glfwGetTime();
         deltaTime = currentTime - prevTime;
         prevTime = currentTime;
         deltaTime = mc.isPaused() ? 0.0 : Math.min(0.05, deltaTime);
      });
   }

   private void initStorage() {
      this.globalsDir = new File("C:\\lumenClient", "lumen");
      this.configsDir = new File(this.globalsDir, "configs");
      this.abItemsDir = new File(this.globalsDir, "abitems");
      EventInvoker.register(this);
      this.createDirs(this.globalsDir, this.configsDir, this.abItemsDir);
      this.initializer = new InitializeStorage();
      this.initializer.onInitialize();
      this.discordManager = new DiscordManager().start();
      ItemDatabase.reload();
      AutoBuyConfig.load();
      EventInvoker.register(AutoBuyEngine.INSTANCE);
      EventInvoker.register(AutoSetCollector.INSTANCE);
      EventInvoker.register(ItemPickupTracker.INSTANCE);
      EventInvoker.register(PetManager.INSTANCE);
      EventInvoker.register(CosmeticWorldRenderer.INSTANCE);
      FiguraBridge.installBundledAvatars();
      FiguraBridge.loadPersisted();
      EventInvoker.register(FiguraAvatarPersistence.INSTANCE);
      EventInvoker.register(FiguraKeybindGuard.INSTANCE);
      EventInvoker.register(FriendModelManager.INSTANCE);
      EventInvoker.register(VoiceCallManager.INSTANCE);
      EventInvoker.register(BotManager.INSTANCE);
      EventInvoker.register(BotRegistry.INSTANCE);
      EventInvoker.register(BotAutoJoin.INSTANCE);
      EventInvoker.register(BotScreenBoards.INSTANCE);
   }

   private void createDirs(File... file) {
      for (File f : file) {
         f.mkdirs();
      }
   }

   public void closeMinecraft() {
      try {
         this.configStorage.saveConfig(this.configStorage.currentConfig);
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (this.globalSocialStorage != null) {
         this.globalSocialStorage.save();
      }

      if (this.discordManager != null) {
         this.discordManager.stopRPC();
      }

      BotRegistry.INSTANCE.shutdown();
      BotManager.INSTANCE.disconnectAll();
   }

   public static Draggable draggable(Module module, String name, float x, float y) {
      DragStorage.draggables.put(name, new Draggable(module, name, x, y));
      return DragStorage.draggables.get(name);
   }

   public void setUserInfo(UserInfo userInfo) {
      this.userInfo = userInfo == null ? UserInfo.empty() : userInfo;
   }

   @Generated
   public UserInfo getUserInfo() {
      return this.userInfo;
   }
}
