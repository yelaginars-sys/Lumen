package dlc.lumen.api.storages;

import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.storages.implement.CommandStorage;
import dlc.lumen.api.storages.implement.ConfigStorage;
import dlc.lumen.api.storages.implement.FreeLookStorage;
import dlc.lumen.api.storages.implement.FriendStorage;
import dlc.lumen.api.storages.implement.LocalizationStorage;
import dlc.lumen.api.storages.implement.MacroStorage;
import dlc.lumen.api.storages.implement.ModuleStorage;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.storages.implement.StaffStorage;
import dlc.lumen.api.storages.implement.ThemeStorage;
import dlc.lumen.api.storages.implement.WaypointStorage;
import dlc.lumen.api.utils.tps.TPSCalc;
import dlc.lumen.client.render.WorldTagRenderer;
import dlc.lumen.client.social.GlobalSocialManager;
import dlc.lumen.client.social.GlobalSocialStorage;

public class InitializeStorage implements QClient {
   public void onInitialize() {
      EventInvoker.register(this);
      this.initStorages();
   }

   public void initStorages() {
      Lumen.INSTANCE.moduleStorage = new ModuleStorage();
      Lumen.INSTANCE.themeStorage = new ThemeStorage();
      Lumen.INSTANCE.tpsCalc = new TPSCalc();
      EventInvoker.register(Lumen.INSTANCE.tpsCalc);
      Lumen.INSTANCE.localizationStorage = new LocalizationStorage();
      Lumen.INSTANCE.freeLookStorage = new FreeLookStorage();
      Lumen.INSTANCE.rotationStorage = new RotationStorage();
      Lumen.INSTANCE.friendStorage = new FriendStorage();
      Lumen.INSTANCE.macroStorage = new MacroStorage();
      Lumen.INSTANCE.staffStorage = new StaffStorage();
      Lumen.INSTANCE.waypointStorage = new WaypointStorage();
      Lumen.INSTANCE.globalSocialStorage = new GlobalSocialStorage();
      Lumen.INSTANCE.globalSocialManager = GlobalSocialManager.INSTANCE;
      Lumen.INSTANCE.globalSocialManager.initialize(Lumen.INSTANCE.globalSocialStorage);
      EventInvoker.register(Lumen.INSTANCE.globalSocialManager);
      Lumen.INSTANCE.commandStorage = new CommandStorage();
      Lumen.INSTANCE.configStorage = new ConfigStorage();
      EventInvoker.register(WorldTagRenderer.INSTANCE);
   }
}
