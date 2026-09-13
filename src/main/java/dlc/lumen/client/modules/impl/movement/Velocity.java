package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Velocity extends Module {
   public static final Velocity INSTANCE = new Velocity();
   private static final double LEVEL = 8000.0;
   private final ModeSetting modeSetting = new ModeSetting("Мод", "Lumen", "Lumen", "New Grim", "NewGrimV2");
   private final FloatSetting floatSetting = new FloatSetting("Вертикальная", 50.0F, 0.0F, 100.0F, 1.0F);
   private final FloatSetting floatSetting2 = new FloatSetting("Горизонтальная", 100.0F, 0.0F, 100.0F, 1.0F);
   private boolean flag;
   private int ccCooldown;

   public Velocity() {
      super("Velocity", "Уменьшает отдачу от урона", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting, this.floatSetting, this.floatSetting2);
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (mc.player != null && mc.world != null) {
         if (event.getType() == EventPacket.Type.RECEIVE) {
            if (this.modeSetting.is("NewGrimV2") && event.getPacket() instanceof PlayerPositionLookS2CPacket) {
               this.ccCooldown = 5;
            }

            if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket var2) {
               if (var2.getEntityId() == mc.player.getId()) {
                  if (this.modeSetting.is("NewGrimV2")) {
                     if (!mc.player.isTouchingWater() && !mc.player.isSubmergedInWater() && !mc.player.isInLava()) {
                        if (this.ccCooldown > 0) {
                           this.ccCooldown--;
                        } else {
                           event.cancel();
                           this.flag = true;
                        }
                     }
                  } else if (this.modeSetting.is("New Grim")) {
                     event.cancel();
                     this.flag = true;
                  } else {
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

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (this.modeSetting.is("NewGrimV2")) {
         this.handleNewGrimV2Tick();
      } else {
         this.handleNewGrimTick();
      }
   }

   private void handleNewGrimV2Tick() {
      if (!this.modeSetting.is("NewGrimV2") || mc.player == null || mc.world == null) {
         return;
      }

      if (mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) {
         return;
      }

      if (this.flag) {
         if (this.ccCooldown <= 0) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
               mc.player.getX(),
               mc.player.getY(),
               mc.player.getZ(),
               mc.player.getYaw(),
               mc.player.getPitch(),
               mc.player.isOnGround(),
               false
            ));
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
               PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
               BlockPos.ofFloored(mc.player.getPos()),
               Direction.DOWN
            ));
         }

         this.flag = false;
      }
   }

   private void handleNewGrimTick() {
      if (!this.modeSetting.is("New Grim") || mc.player == null || mc.world == null) {
         return;
      }

      if (this.ccCooldown > 0) {
         this.ccCooldown--;
      }

      if (this.flag && this.ccCooldown <= 0) {
         mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
            mc.player.getX(),
            mc.player.getY(),
            mc.player.getZ(),
            mc.player.getYaw(),
            mc.player.getPitch(),
            mc.player.isOnGround(),
            false
         ));
         mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
            BlockPos.ofFloored(mc.player.getPos()),
            Direction.DOWN
         ));
         this.ccCooldown = 10;
      }

      this.flag = false;
   }
}
