package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.util.math.Vec3d;

public class Flight extends Module {
   public static Flight INSTANCE = new Flight();
   private final FloatSetting floatSetting = new FloatSetting("Скорость", 2.0F, 0.1F, 10.0F, 0.1F);

   public Flight() {
      super("Flight", "Полёт", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.floatSetting);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null) {
         double var2 = this.floatSetting.get();
         float var4 = (float)Math.toRadians(mc.player.getYaw());
         double var5 = 0.0;
         double var7 = 0.0;
         double var9 = 0.0;
         double var11 = 0.0;
         double var13 = 0.0;
         if (mc.options.forwardKey.isPressed()) {
            var11++;
         }

         if (mc.options.backKey.isPressed()) {
            var11--;
         }

         if (mc.options.leftKey.isPressed()) {
            var13++;
         }

         if (mc.options.rightKey.isPressed()) {
            var13--;
         }

         if (var11 != 0.0 || var13 != 0.0) {
            double var15 = Math.atan2(var11, var13) - (Math.PI / 2);
            var5 = -Math.sin(var4 + var15) * var2;
            var9 = Math.cos(var4 + var15) * var2;
         }

         if (mc.options.jumpKey.isPressed()) {
            var7 = var2;
         } else if (mc.options.sneakKey.isPressed()) {
            var7 = -var2;
         }

         mc.player.setVelocity(new Vec3d(var5, var7, var9));
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player != null) {
         mc.player.setVelocity(Vec3d.ZERO);
      }
   }
}
