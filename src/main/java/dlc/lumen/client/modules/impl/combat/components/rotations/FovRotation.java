package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.math.MathUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class FovRotation extends RotationsSystem implements QClient {
   public static float rawDeltaYaw = 0.0F;
   public static float rawDeltaPitch = 0.0F;
   private LivingEntity currentTarget;
   private float currentFov = 45.0F;
   private float virtualYaw = 0.0F;
   private float virtualPitch = 0.0F;
   private float targetYaw = 0.0F;
   private float targetPitch = 0.0F;
   private float currentYaw = 0.0F;
   private float currentPitch = 0.0F;
   private float smoothFactor = 0.6F;
   private int smoothUpdateTimer = 0;
   private boolean isApplyingNoise = false;
   private int noiseCooldownTimer = 0;
   private int noiseActiveTimer = 0;
   private float currentNoiseYaw = 0.0F;
   private float currentNoisePitch = 0.0F;
   private float targetNoiseYaw = 0.0F;
   private float targetNoisePitch = 0.0F;

   public float getCurrentFov() {
      return this.currentFov;
   }

   public void reset() {
      this.currentTarget = null;
      this.isApplyingNoise = false;
      this.noiseCooldownTimer = (int)MathUtils.random(20.0F, 80.0F);
      this.noiseActiveTimer = 0;
      this.currentNoiseYaw = 0.0F;
      this.currentNoisePitch = 0.0F;
      this.targetNoiseYaw = 0.0F;
      this.targetNoisePitch = 0.0F;
      this.smoothFactor = MathUtils.random(0.55F, 0.7F);
      this.smoothUpdateTimer = (int)MathUtils.random(40.0F, 80.0F);
      rawDeltaYaw = 0.0F;
      rawDeltaPitch = 0.0F;
      if (mc.player != null) {
         this.virtualYaw = mc.player.getYaw();
         this.virtualPitch = mc.player.getPitch();
         this.targetYaw = this.virtualYaw;
         this.targetPitch = this.virtualPitch;
         this.currentYaw = this.virtualYaw;
         this.currentPitch = this.virtualPitch;
      }
   }

   public boolean isInFov(LivingEntity target) {
      if (mc.player != null && target != null && mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         Vec3d targetCenter = target.getBoundingBox().getCenter();
         Vec2f rotToTarget = RotationUtils.getRotations(targetCenter);
         float cameraYaw = mc.gameRenderer.getCamera().getYaw();
         float cameraPitch = mc.gameRenderer.getCamera().getPitch();
         float yawDiff = Math.abs(MathHelper.wrapDegrees(rotToTarget.x - cameraYaw));
         float pitchDiff = Math.abs(rotToTarget.y - cameraPitch);
         return Math.hypot(yawDiff, pitchDiff) <= this.currentFov;
      } else {
         return false;
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         if (this.currentTarget != target) {
            this.currentTarget = target;
            this.reset();
         }

         float deltaYaw = rawDeltaYaw;
         float deltaPitch = rawDeltaPitch;
         rawDeltaYaw = 0.0F;
         rawDeltaPitch = 0.0F;
         this.virtualYaw += deltaYaw;
         this.virtualPitch = MathHelper.clamp(this.virtualPitch + deltaPitch, -89.0F, 89.0F);
         Vec3d eyePos = mc.player.getEyePos();
         Box box = this.getPredictedBox(target);
         Vec3d center = box.getCenter();
         Vec2f centerRot = RotationUtils.getRotations(center);
         float distance = Math.max(0.1F, (float)eyePos.distanceTo(center));
         float halfWidth = (float)(box.maxX - box.minX) / 2.0F;
         float halfHeight = (float)(box.maxY - box.minY) / 2.0F;
         float yawRatio = Math.min(1.0F, halfWidth * 0.75F / distance);
         float pitchRatio = Math.min(1.0F, halfHeight * 0.75F / distance);
         float yawSpan = (float)Math.toDegrees(Math.asin(yawRatio));
         float pitchSpan = (float)Math.toDegrees(Math.asin(pitchRatio));
         float yawDiffFromCenter = MathHelper.wrapDegrees(this.virtualYaw - centerRot.x);
         float pitchDiffFromCenter = this.virtualPitch - centerRot.y;
         this.targetYaw = centerRot.x + MathHelper.clamp(yawDiffFromCenter, -yawSpan, yawSpan);
         this.targetPitch = centerRot.y + MathHelper.clamp(pitchDiffFromCenter, -pitchSpan, pitchSpan);
         this.smoothUpdateTimer--;
         if (this.smoothUpdateTimer <= 0) {
            this.smoothUpdateTimer = (int)MathUtils.random(30.0F, 70.0F);
            this.smoothFactor = MathUtils.random(0.5F, 0.72F);
         }

         float mouseSpeed = (float)Math.hypot(deltaYaw, deltaPitch);
         float adaptiveFactor;
         if (mouseSpeed > 5.0F) {
            adaptiveFactor = MathUtils.random(0.8F, 0.95F);
         } else if (mouseSpeed > 1.5F) {
            adaptiveFactor = this.smoothFactor + mouseSpeed * 0.03F;
         } else {
            adaptiveFactor = this.smoothFactor;
         }

         adaptiveFactor = Math.min(adaptiveFactor, 0.95F);
         float yawDelta = MathHelper.wrapDegrees(this.targetYaw - this.currentYaw);
         float pitchDelta = this.targetPitch - this.currentPitch;
         this.currentYaw += yawDelta * adaptiveFactor;
         this.currentPitch += pitchDelta * adaptiveFactor;
         if (mouseSpeed < 2.5F) {
            if (!this.isApplyingNoise) {
               this.noiseCooldownTimer--;
               if (this.noiseCooldownTimer <= 0) {
                  this.isApplyingNoise = true;
                  this.noiseActiveTimer = (int)MathUtils.random(35.0F, 45.0F);
               }
            } else {
               this.noiseActiveTimer--;
               if (this.noiseActiveTimer % MathUtils.random(4.0F, 7.0F) == 0.0F) {
                  this.targetNoiseYaw = (float)((Math.random() - 0.5) * 1.2);
                  this.targetNoisePitch = (float)((Math.random() - 0.5) * 0.8);
               }

               if (this.noiseActiveTimer <= 0) {
                  this.isApplyingNoise = false;
                  this.noiseCooldownTimer = (int)MathUtils.random(20.0F, 80.0F);
                  this.targetNoiseYaw = 0.0F;
                  this.targetNoisePitch = 0.0F;
               }
            }
         } else {
            this.targetNoiseYaw = 0.0F;
            this.targetNoisePitch = 0.0F;
         }

         this.currentNoiseYaw = (float)MathHelper.lerp(0.12, this.currentNoiseYaw, this.targetNoiseYaw);
         this.currentNoisePitch = (float)MathHelper.lerp(0.12, this.currentNoisePitch, this.targetNoisePitch);
         float newYaw = this.currentYaw + this.currentNoiseYaw;
         float newPitch = this.currentPitch + this.currentNoisePitch;
         float gcd = GCDUtil.getGCD();
         if (gcd > 0.0F) {
            float dYaw = MathHelper.wrapDegrees(newYaw - this.currentYaw);
            float dPitch = newPitch - this.currentPitch;
            dYaw -= dYaw % gcd;
            dPitch -= dPitch % gcd;
            newYaw = this.currentYaw + dYaw + this.currentNoiseYaw;
            newPitch = this.currentPitch + dPitch + this.currentNoisePitch;
         }

         newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);
         RotationStorage.update(new Rotation(newYaw, newPitch), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
      }
   }
}
