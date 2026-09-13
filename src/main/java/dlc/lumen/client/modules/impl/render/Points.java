package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.social.GlobalSocialManager;

public class Points extends Module {
   public static final Points INSTANCE = new Points();
   private final BindSetting bindSetting = new BindSetting("Бинд точки", -1);

   public Points() {
      super("Points", "Отправляет метки в пати", Module.ModuleCategory.RENDER);
      this.addSettings(this.bindSetting);
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (this.bindSetting.getKey() != -1 && event.getKey() == this.bindSetting.getKey()) {
            if (GlobalSocialManager.INSTANCE.createPointAtPlayer()) {
               ChatUtils.sendMessage("Точка отправлена в пати.");
            }
         }
      }
   }
}
