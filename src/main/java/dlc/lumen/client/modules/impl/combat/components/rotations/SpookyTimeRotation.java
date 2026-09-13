package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SpookyTimeRotation extends RotationsSystem {
   private static final float YAW_STIFFNESS = 0.32F;
   private static final float YAW_DAMPING = 0.42F;
   private static final float PITCH_STIFFNESS = 0.30F;
   private static final float PITCH_DAMPING = 0.45F;
   private static final float YAW_CAP = 34.0F;
   private static final float PITCH_CAP = 22.0F;
   private static final double LEAD_SCALE = 0.85;
   private static final double LEAD_MAX_HORIZONTAL = 0.30;
   private static final float RATE_LIMIT = 8.0F;
   private static final float RATE_SMOOTH = 0.35F;
   private static final float RATE_GAIN = 0.25F;
   private static final int PREDICT_WARMUP_TICKS = 5;

   private float yawVelocity;
   private float pitchVelocity;
   private float filteredYawRate;
   private float previousTargetYaw;
   private LivingEntity lastTarget;
   private int predictCooldown;

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null || target == null) {
         return;
      }

      boolean newTarget = target != this.lastTarget;
      if (newTarget) {
         this.resetSpring();
         this.lastTarget = target;
         this.predictCooldown = PREDICT_WARMUP_TICKS;
      }

      Box box = this.getPredictedBox(target);
      if (this.predictCooldown > 0) {
         this.predictCooldown--;
      } else {
         Vec3d lead = target.getVelocity().multiply(LEAD_SCALE);
         double leadHorizontal = Math.sqrt(lead.x * lead.x + lead.z * lead.z);
         if (leadHorizontal > LEAD_MAX_HORIZONTAL) {
            lead = lead.multiply(LEAD_MAX_HORIZONTAL / leadHorizontal);
         }

         box = box.offset(lead);
      }

      Vec3d delta = box.getCenter().subtract(mc.player.getEyePos());
      double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
      float targetYaw = (float)(Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0);
      float targetPitch = MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(delta.y, horizontal))), -89.0F, 89.0F);

      float yawDelta = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
      float pitchDelta = targetPitch - mc.player.getPitch();
      this.yawVelocity = this.yawVelocity * YAW_DAMPING + yawDelta * YAW_STIFFNESS;
      this.pitchVelocity = this.pitchVelocity * PITCH_DAMPING + pitchDelta * PITCH_STIFFNESS;
      this.yawVelocity = MathHelper.clamp(this.yawVelocity, -YAW_CAP, YAW_CAP);
      this.pitchVelocity = MathHelper.clamp(this.pitchVelocity, -PITCH_CAP, PITCH_CAP);

      if (newTarget) {
         this.previousTargetYaw = targetYaw;
      } else {
         float targetYawRate = MathHelper.clamp(MathHelper.wrapDegrees(targetYaw - this.previousTargetYaw), -RATE_LIMIT, RATE_LIMIT);
         this.filteredYawRate = this.filteredYawRate + (targetYawRate - this.filteredYawRate) * RATE_SMOOTH;
         this.yawVelocity += this.filteredYawRate * RATE_GAIN;
         this.previousTargetYaw = targetYaw;
      }

      float newYaw = mc.player.getYaw() + this.yawVelocity;
      float newPitch = MathHelper.clamp(mc.player.getPitch() + this.pitchVelocity, -89.0F, 89.0F);
      Vec2f corrected = correctRotation(newYaw, newPitch);
      RotationStorage.update(new Rotation(corrected.x, corrected.y), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, Aura.clientLook.isState());
   }

   public void reset() {
      this.resetSpring();
      this.lastTarget = null;
      this.predictCooldown = 0;
   }

   private void resetSpring() {
      this.yawVelocity = 0.0F;
      this.pitchVelocity = 0.0F;
      this.filteredYawRate = 0.0F;
      this.previousTargetYaw = 0.0F;
   }
}
