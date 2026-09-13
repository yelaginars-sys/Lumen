package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.player.PlayerIntersectionUtil;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;

public class ElytraFly extends Module {
   public static final ElytraFly INSTANCE = new ElytraFly();
   private boolean flag = false;

   public ElytraFly() {
      super("ElytraFly", "Elytra Fly", Module.ModuleCategory.MOVEMENT);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.flag = false;
      if (RotationStorage.instance != null) {
         RotationStorage.instance.stopRotation();
      }
   }

   private boolean checkCondition() {
      return mc.player == null
         ? false
         : mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)
            || mc.player.getMainHandStack().isOf(Items.ELYTRA)
            || mc.player.getOffHandStack().isOf(Items.ELYTRA);
   }

   @EventLink
   public void onInput(EventMoveInput event) {
      if (mc.player != null) {
         if (this.checkCondition()) {
            if (mc.player.isOnGround()) {
               this.flag = true;
            } else if (!mc.player.isGliding()) {
               if (!this.flag) {
                  PlayerIntersectionUtil.startFallFlying();
               } else {
                  this.flag = false;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.checkCondition()) {
            if (!mc.player.isGliding()) {
               if (!mc.player.isOnGround()) {
                  PlayerIntersectionUtil.startFallFlying();
               }
            } else {
               RotationStorage.update(new Rotation(mc.player.getYaw(), 0.0F), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
               mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y + 0.0305, mc.player.getVelocity().z);
            }
         }
      }
   }
}
