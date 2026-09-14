package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
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

public class SpookyTimeV3Rotation extends RotationsSystem implements QClient {
   private LivingEntity trackedTarget;
   private float lastYaw;
   private float lastPitch;
   private float speedAcceleration;
   private boolean back;
   private boolean initialized;
   private float jitterOffset;
   private int tickCounter;
   private float jitterFadeScale = 1.0F;

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
               float gain = 0.0042F;
               if (yawDiff > 60.0F) {
                  gain += 0.02F;
               } else if (yawDiff > 30.0F) {
                  gain += 0.01F;
               } else {
                  gain += 0.0038F;
               }

               if (readyToAttack) {
                  gain += 0.01F;
               }

               this.speedAcceleration = this.speedAcceleration + gain * (1.6F + this.jitterOffset);
               if (this.speedAcceleration >= 0.22F) {
                  this.back = true;
               }
            } else {
               float loss = readyToAttack ? 0.032F : 0.007F;
               this.speedAcceleration = this.speedAcceleration - loss * (2.1F + this.jitterOffset);
               if (this.speedAcceleration <= -0.04F) {
                  this.back = false;
               }
            }

            float smooth = MathHelper.clamp(this.speedAcceleration, 0.0F, mc.player.isGliding() ? 0.34F : 0.22F);
            if (readyToAttack) {
               smooth = Math.min(smooth + 0.06F, mc.player.isGliding() ? 0.4F : 0.27F);
            }

            smooth += this.jitterOffset * 0.5F;
            if (this.tickCounter % 7 == 0) {
               smooth += 0.03F;
            }

            float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
            float deltaPitch = targetPitch - this.lastPitch;
            float yawLimit = mc.player.isGliding() ? 38.0F : (readyToAttack ? 24.0F : 17.0F);
            float pitchLimit = mc.player.isGliding() ? 10.0F : (readyToAttack ? 3.8F : 2.3F);
            deltaYaw = MathHelper.clamp(deltaYaw, -yawLimit, yawLimit);
            deltaPitch = MathHelper.clamp(deltaPitch, -pitchLimit, pitchLimit);
            float pitchSpeed = smooth * 0.26F;
            float yawSpeed = smooth * (0.83F + this.jitterOffset * 0.22F);
            float newYaw = this.lastYaw + deltaYaw * yawSpeed;
            float newPitch = this.lastPitch + deltaPitch * pitchSpeed;
            float gcd = GCDUtil.getGCDValue();
            if (gcd > 0.0F) {
               newYaw = this.lastYaw + Math.round((newYaw - this.lastYaw) / gcd) * gcd;
               newPitch = this.lastPitch + Math.round((newPitch - this.lastPitch) / gcd) * gcd;
            }

            newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);
            Rotation finalRot = new Rotation(newYaw, newPitch);
            float rotSpeed = mc.player.isGliding() && target.isGliding() ? 360.0F : 45.0F;
            RotationStorage.update(finalRot, rotSpeed, rotSpeed, rotSpeed, rotSpeed, 0, 1, false);
            this.rotate = new Vec2f(finalRot.getYaw(), finalRot.getPitch());
            this.lastYaw = finalRot.getYaw();
            this.lastPitch = finalRot.getPitch();
         }
      }
   }
}
