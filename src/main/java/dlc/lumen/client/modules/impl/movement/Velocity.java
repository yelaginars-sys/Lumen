package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Velocity extends Module {
   public static final Velocity INSTANCE = new Velocity();
   private static final double LEVEL = 8000.0;
   private final FloatSetting floatSetting = new FloatSetting("Вертикальная", 50.0F, 0.0F, 100.0F, 1.0F);
   private final FloatSetting floatSetting2 = new FloatSetting("Горизонтальная", 100.0F, 0.0F, 100.0F, 1.0F);

   public Velocity() {
      super("Velocity", "Уменьшает отдачу от урона", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.floatSetting, this.floatSetting2);
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (mc.player != null && mc.world != null) {
         if (event.getType() == EventPacket.Type.RECEIVE) {
            if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket var2) {
               if (var2.getEntityId() == mc.player.getId()) {
                  double var13 = this.floatSetting2.get() / 100.0;
                  double var5 = this.floatSetting.get() / 100.0;
                  double var7 = var2.getVelocityX() / 8000.0 * var13;
                  double var9 = var2.getVelocityY() / 8000.0 * var5;
                  double var11 = var2.getVelocityZ() / 8000.0 * var13;
                  event.cancel();
                  mc.execute(() -> {
                     if (mc.player != null) {
                        mc.player.setVelocityClient(var7, var9, var11);
                     }
                  });
               }
            }
         }
      }
   }
}
