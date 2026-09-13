package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.player.MoveUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;

public class WaterSpeed extends Module {
   public static WaterSpeed INSTANCE = new WaterSpeed();
   private final FloatSetting floatSetting = new FloatSetting("Сила буста", 0.015F, 0.005F, 0.03F, 0.001F);
   private final FloatSetting floatSetting2 = new FloatSetting("Лимит", 0.19F, 0.13F, 0.25F, 0.005F);
   private int index = 0;

   public WaterSpeed() {
      super("WaterSpeed", "Ускорение в воде", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.floatSetting, this.floatSetting2);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null) {
         if (mc.player.isTouchingWater() && MoveUtils.isMoving()) {
            double var2 = Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z);
            if (!(var2 < 0.001)) {
               double var4 = this.floatSetting2.getValue().doubleValue();
               if (!(var2 >= var4)) {
                  this.index++;
                  if (this.index % 2 == 0) {
                     double var6 = this.floatSetting.getValue().doubleValue();
                     double var8 = Math.min(var2 + var6, var4);
                     if (var8 > var2) {
                        float var10 = mc.player.getYaw();
                        float var11 = mc.player.getPitch();
                        double var12 = mc.player.forwardSpeed;
                        double var14 = mc.player.forwardSpeed;
                        double var16 = -Math.sin(Math.toRadians(var10)) * var8;
                        double var18 = Math.cos(Math.toRadians(var10)) * var8;
                        if (Math.abs(var14) > 0.01) {
                           double var20 = Math.toRadians(var10 + (var14 > 0.0 ? 90 : -90));
                           var16 += Math.cos(var20) * var8 * 0.5;
                           var18 += Math.sin(var20) * var8 * 0.5;
                        }

                        double var22 = Math.hypot(var16, var18);
                        if (var22 > 0.001) {
                           var16 = var16 / var22 * var8;
                           var18 = var18 / var22 * var8;
                        }

                        mc.player.setVelocity(var16, mc.player.getVelocity().y, var18);
                     }
                  }
               }
            }
         } else {
            this.index = 0;
         }
      }
   }
}
