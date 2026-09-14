package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SpookyTimeV3_116Rotation extends RotationsSystem implements QClient {
   private LivingEntity trackedTarget;
   private float currentYaw;
   private float currentPitch;
   private boolean initialized;
   private int tickCounter;
   private float seedX;
   private float seedY;
   private float seedZ;
   private float smoothedSpeed = 0.12F;

   public SpookyTimeV3_116Rotation() {
      this.randomizeSeeds();
   }

   private void randomizeSeeds() {
      this.seedX = ThreadLocalRandom.current().nextFloat() * 100.0F;
      this.seedY = ThreadLocalRandom.current().nextFloat() * 100.0F;
      this.seedZ = ThreadLocalRandom.current().nextFloat() * 100.0F;
   }

   public void reset() {
      this.trackedTarget = null;
      this.tickCounter = 0;
      this.initialized = false;
      this.smoothedSpeed = 0.12F;
      this.randomizeSeeds();
      if (mc.player != null) {
         this.currentYaw = mc.player.getYaw();
         this.currentPitch = mc.player.getPitch();
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         if (mc.player.isBlocking()) {
            this.rotate = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
            this.currentYaw = this.rotate.x;
            this.currentPitch = this.rotate.y;
         } else {
            if (!this.initialized) {
               this.currentYaw = mc.player.getYaw();
               this.currentPitch = mc.player.getPitch();
               this.initialized = true;
            }

            if (this.trackedTarget != target) {
               this.trackedTarget = target;
               this.tickCounter = 0;
               this.smoothedSpeed = 0.1F;
               this.randomizeSeeds();
            }

            this.tickCounter++;
            double timeSec = System.currentTimeMillis() / 1000.0;
            double offsetX = Math.sin(timeSec * 1.4 + this.seedX) * 0.22;
            double offsetY = 0.52 + Math.sin(timeSec * 0.9 + this.seedY) * 0.18 + Math.cos(timeSec * 1.8 + this.seedZ) * 0.06;
            double offsetZ = Math.cos(timeSec * 1.4 + this.seedZ) * 0.22;
            Box box = target.getBoundingBox();
            double aimX = target.getX() + box.getLengthX() * offsetX;
            double aimY = target.getY() + MathHelper.clamp(box.getLengthY() * offsetY, 0.25, box.getLengthY() * 0.85);
            double aimZ = target.getZ() + box.getLengthZ() * offsetZ;
            Vec3d aimVector = new Vec3d(aimX, aimY, aimZ);
            Vec2f targetRot = RotationUtils.getRotations(aimVector);
            float targetYaw = targetRot.x;
            float targetPitch = targetRot.y;
            float yawDelta = MathHelper.wrapDegrees(targetYaw - this.currentYaw);
            float pitchDelta = targetPitch - this.currentPitch;
            double totalDistance = Math.hypot(yawDelta, pitchDelta);
            boolean readyToAttack = mc.player.getAttackCooldownProgress(1.0F) > 0.9F && Aura.INSTANCE.getWhiteRiseTicksToAttack() <= 1;
            float baseTargetSpeed = readyToAttack ? 0.22F : 0.14F;
            if (totalDistance > 45.0) {
               baseTargetSpeed += 0.08F;
            } else if (totalDistance < 3.0) {
               baseTargetSpeed *= 0.65F;
               float speedOrganicNoise = (float)(Math.sin(timeSec * 2.5 + this.seedX) * 0.025);
               float desiredSpeed = MathHelper.clamp(baseTargetSpeed + speedOrganicNoise, 0.06F, 0.32F);
               this.smoothedSpeed = this.smoothedSpeed + (desiredSpeed - this.smoothedSpeed) * 0.08F;
               float maxStepYaw = readyToAttack ? 21.0F : 15.5F;
               float maxStepPitch = readyToAttack ? 3.2F : 2.1F;
               if (mc.player.isGliding()) {
                  maxStepYaw = 38.0F;
                  maxStepPitch = 9.0F;
               }

               float stepYaw = MathHelper.clamp(yawDelta * this.smoothedSpeed, -maxStepYaw, maxStepYaw);
               float stepPitch = MathHelper.clamp(pitchDelta * (this.smoothedSpeed * 0.48F), -maxStepPitch, maxStepPitch);
               float nextYaw = this.currentYaw + stepYaw;
               float nextPitch = MathHelper.clamp(this.currentPitch + stepPitch, -89.0F, 89.0F);
               float gcd = GCDUtil.getGCDValue();
               if (gcd > 0.0F) {
                  nextYaw = this.currentYaw + Math.round((nextYaw - this.currentYaw) / gcd) * gcd;
                  nextPitch = this.currentPitch + Math.round((nextPitch - this.currentPitch) / gcd) * gcd;
               }

               this.currentYaw = nextYaw;
               this.currentPitch = nextPitch;
               Rotation finalRot = new Rotation(this.currentYaw, this.currentPitch);
               RotationStorage.update(finalRot, 180.0F, 180.0F, 180.0F, 180.0F, 0, 1, false);
               this.rotate = new Vec2f(this.currentYaw, this.currentPitch);
            }
         }
      }
   }
}
