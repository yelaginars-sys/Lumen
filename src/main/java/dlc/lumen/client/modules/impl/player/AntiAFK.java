package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.util.Hand;

public class AntiAFK extends Module {
   public static AntiAFK INSTANCE = new AntiAFK();
   private final BooleanSetting booleanSetting = new BooleanSetting("Крутить камеру", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Махать рукой", true);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Прыгать", false);
   private int index = 0;

   public AntiAFK() {
      super("AntiAFK", "Имитирует активность против AFK-кика", Module.ModuleCategory.PLAYER);
      this.addSettings(this.booleanSetting, this.booleanSetting2, this.booleanSetting3);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         this.index++;
         if (this.booleanSetting.isState()) {
            float var2 = this.index / 20 % 2 == 0 ? 0.6F : -0.6F;
            mc.player.setYaw(mc.player.getYaw() + var2);
         }

         if (this.booleanSetting3.isState() && mc.player.isOnGround() && this.index % 40 == 0) {
            mc.player.jump();
         }

         if (this.booleanSetting2.isState() && this.index % 60 == 0) {
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   @Override
   public void onEnable() {
      this.index = 0;
      super.onEnable();
   }
}
