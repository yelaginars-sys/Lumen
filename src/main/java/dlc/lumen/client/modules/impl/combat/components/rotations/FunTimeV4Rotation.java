package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.math.MathUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import dlc.lumen.client.modules.impl.combat.components.interpolation.BestPoint;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class FunTimeV4Rotation extends RotationsSystem implements QClient {
   private LivingEntity trackedTarget;
   private float lastYaw;
   private float lastPitch;
   private float speedAcceleration;
   private boolean back;
   private boolean initialized;
   private float jitterOffset;
   private int tickCounter;
   private float jitterFadeScale = 1.0F;
   private int hitCount;
   private int attackJitterTime;
   private float swingYawStep;
   private float swingPitchStep;

   public void reset() {
      this.trackedTarget = null;
      this.speedAcceleration = 0.0F;
      this.back = false;
      this.jitterFadeScale = Math.max(0.0F, this.jitterFadeScale - 0.08F);
      this.tickCounter = 0;
      this.initialized = mc.player != null;
      if (mc.player != null) {
         this.lastYaw = mc.player.getYaw();
         this.lastPitch = mc.player.getPitch();
      } else {
         this.lastYaw = 0.0F;
         this.lastPitch = 0.0F;
      }
   }

   public void onAttack() {
      if (mc.player != null) {
         this.hitCount = Math.min(this.hitCount + 1, 30);
         this.attackJitterTime = 120;
         float growth = 1.0F + Math.min(0.05F * this.hitCount, 1.0F);
         float randomness = 1.0F + this.hitCount / 30.0F * 0.8F;
         float totalYaw = (26.0F + ThreadLocalRandom.current().nextFloat() * 12.0F * randomness) * growth;
         float totalPitch = (5.5F + ThreadLocalRandom.current().nextFloat() * 3.0F * randomness) * growth;
         float yawDir = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
         float pitchDir = ThreadLocalRandom.current().nextInt(100) == 0
            ? -1.0F
            : (ThreadLocalRandom.current().nextFloat() < 0.8F ? 1.0F : -1.0F);
         this.swingYawStep = yawDir * totalYaw;
         this.swingPitchStep = pitchDir * totalPitch;
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         if (mc.player.isBlocking()) {
            this.rotate = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
            this.lastYaw = this.rotate.x;
            this.lastPitch = this.rotate.y;
         } else {
            if (!this.initialized) {
               this.lastYaw = mc.player.getYaw();
               this.lastPitch = mc.player.getPitch();
               this.initialized = true;
            }

            if (this.trackedTarget != target) {
               this.trackedTarget = target;
               this.speedAcceleration = 0.0F;
               this.back = false;
               this.tickCounter = 0;
               this.jitterFadeScale = 1.0F;
            }

            this.tickCounter++;
            float wave1 = (float)Math.sin(this.tickCounter * 0.13F) * 0.08F;
            float wave2 = (float)Math.cos(this.tickCounter * 0.051F) * 0.047F;
            float random = (ThreadLocalRandom.current().nextFloat() - 0.51F) * 0.02F;
            this.jitterFadeScale = Math.min(1.0F, this.jitterFadeScale + 0.1F);
            this.jitterOffset = (wave1 + wave2 + random) * this.jitterFadeScale;
            Vec3d point = BestPoint.getMultipoint(target, 128.0);
            Vec2f angle = RotationUtils.getRotations(point);
            float targetYaw = angle.x;
            float targetPitch = angle.y;
            float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - this.lastYaw));
            boolean readyToAttack = mc.player.getAttackCooldownProgress(1.0F) > 0.9F && Aura.INSTANCE.getWhiteRiseTicksToAttack() <= 1;
            if (!this.back) {
               float gain = 0.0032F;
               if (yawDiff > 60.0F) {
                  gain += 0.015F;
               } else if (yawDiff > 30.0F) {
                  gain += 0.007F;
               } else {
                  gain += 0.0028F;
               }

               if (readyToAttack) {
                  gain += 0.006F;
               }

               this.speedAcceleration = this.speedAcceleration + gain * (1.4F + this.jitterOffset);
               if (this.speedAcceleration >= 0.18F) {
                  this.back = true;
               }
            } else {
               float loss = readyToAttack ? 0.024F : 0.005F;
               this.speedAcceleration = this.speedAcceleration - loss * (1.8F + this.jitterOffset);
               if (this.speedAcceleration <= -0.04F) {
                  this.back = false;
               }
            }

            float smooth = MathHelper.clamp(this.speedAcceleration, 0.0F, mc.player.isGliding() ? 0.3F : 0.18F);
            if (readyToAttack) {
               smooth = Math.min(smooth + 0.04F, mc.player.isGliding() ? 0.35F : 0.23F);
            }

            smooth += this.jitterOffset * 0.4F;
            if (this.tickCounter % 7 == 0) {
               smooth += 0.02F;
            }

            float yawLimit = mc.player.isGliding() ? 38.0F : (readyToAttack ? 24.0F : 17.0F);
            float pitchLimit = mc.player.isGliding() ? 10.0F : (readyToAttack ? 3.8F : 2.3F);
            float deltaYaw = MathHelper.clamp(MathHelper.wrapDegrees(targetYaw - this.lastYaw), -yawLimit, yawLimit);
            float deltaPitch = MathHelper.clamp(targetPitch - this.lastPitch, -pitchLimit, pitchLimit);
            float newYaw = this.lastYaw + deltaYaw * smooth * (0.83F + this.jitterOffset * 0.22F);
            float newPitch = this.lastPitch + deltaPitch * smooth * 0.69F;
            float gcd = GCDUtil.getGCDValue();
            if (gcd > 0.0F) {
               newYaw = this.lastYaw + Math.round((newYaw - this.lastYaw) / gcd) * gcd;
               newPitch = this.lastPitch + Math.round((newPitch - this.lastPitch) / gcd) * gcd;
            }

            newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);
            if (this.attackJitterTime > 0) {
               int total = 120;
               float progress = (float)(total - this.attackJitterTime) / total;
               float prevProgress = (float)(total - this.attackJitterTime - 1) / total;
               float currPos = (float)Math.sin(Math.PI * progress);
               float prevPos = (float)Math.sin(Math.PI * Math.max(0.0F, prevProgress));
               float delta = currPos - prevPos;
               newYaw = MathHelper.wrapDegrees(newYaw + this.swingYawStep * delta);
               newPitch = MathHelper.clamp(newPitch + this.swingPitchStep * delta, -89.0F, 89.0F);
               this.attackJitterTime--;
            }

            this.lastYaw = newYaw;
            this.lastPitch = newPitch;
            Rotation finalRot = new Rotation(newYaw, newPitch);
            float rotSpeed = mc.player.isGliding() && target.isGliding() ? 360.0F : 45.0F;
            RotationStorage.update(finalRot, rotSpeed, rotSpeed, rotSpeed, rotSpeed, 0, 1, false);
            this.rotate = new Vec2f(newYaw, newPitch);
         }
      }
   }
}
