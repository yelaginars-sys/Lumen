package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class SpookyRotation extends RotationsSystem {
   private final Random random = new Random(System.nanoTime());
   private LivingEntity lastTarget;
   private float lastYaw;
   private float lastPitch;
   private float preciseYaw;
   private float precisePitch;
   private boolean hasTarget;
   private int returnTicksLeft;
   private long invisibleSinceMs;
   private long nextPointMs;
   private int pointIdx;
   private long nextLookawayMs;
   private long lookawayUntilMs;
   private Vec3d lookawayPoint = Vec3d.ZERO;
   private long patternUntilMs;
   private float speedMul = 1.0F;
   private float yawJitterAmp = 0.5F;
   private float yawJitterRnd = 0.35F;
   private float jitterPhase;

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null || target == null) {
         return;
      }

      long now = System.nanoTime() / 1000000L;
      if (target != this.lastTarget) {
         this.lastTarget = target;
         this.preciseYaw = mc.player.getYaw();
         this.precisePitch = mc.player.getPitch();
         this.invisibleSinceMs = 0L;
         this.nextPointMs = 0L;
         this.nextLookawayMs = now + 600L + this.random.nextInt(1200);
         this.lookawayUntilMs = 0L;
         this.rollPattern(now);
      }

      if (now >= this.patternUntilMs) {
         this.rollPattern(now);
      }

      Box box = this.getPredictedBox(target);
      Vec3d eye = mc.player.getEyePos();
      if (now >= this.nextPointMs) {
         this.pointIdx = this.random.nextInt(5);
         this.nextPointMs = now + 100L + this.random.nextInt(400);
      }

      Vec3d aim = this.computeAimPoint(box, target);
      boolean visible = this.isVisible(eye, aim);
      if (visible) {
         this.invisibleSinceMs = 0L;
      } else if (this.invisibleSinceMs == 0L) {
         this.invisibleSinceMs = now;
      }

      boolean lookaway = false;
      if (now >= this.nextLookawayMs) {
         this.lookawayPoint = aim.add(
            (this.random.nextDouble() - 0.5) * 2.4, (this.random.nextDouble() - 0.5) * 1.2, (this.random.nextDouble() - 0.5) * 2.4
         );
         this.lookawayUntilMs = now + 10L + this.random.nextInt(110);
         this.nextLookawayMs = now + 600L + this.random.nextInt(1200);
      }

      Vec3d goal = aim;
      if (now < this.lookawayUntilMs) {
         goal = this.lookawayPoint;
         lookaway = true;
      }

      boolean exact = !lookaway && this.isExact(eye, target);
      Vec3d toGoalForErr = goal.subtract(eye);
      double horForErr = Math.sqrt(toGoalForErr.x * toGoalForErr.x + toGoalForErr.z * toGoalForErr.z);
      float errYaw = Math.abs(
         MathHelper.wrapDegrees((float)(Math.toDegrees(Math.atan2(toGoalForErr.z, toGoalForErr.x)) - 90.0) - this.preciseYaw)
      );
      float errPitch = Math.abs(
         MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(toGoalForErr.y, horForErr))), -89.0F, 89.0F) - this.precisePitch
      );
      float yawSpeed;
      float pitchSpeed;
      if (visible && !lookaway) {
         if (exact) {
            yawSpeed = 0.6F + this.random.nextFloat() * 0.8F;
            pitchSpeed = 0.0F;
         } else {
            yawSpeed = Math.min(18.0F, Math.max(2.5F, errYaw * 0.55F)) + this.random.nextFloat() * 1.5F;
            pitchSpeed = Math.min(10.0F, Math.max(1.0F, errPitch * 0.45F)) + this.random.nextFloat();
         }
      } else {
         long unseen = now - this.invisibleSinceMs;
         if (lookaway) {
            unseen = Math.max(unseen, 120L);
         }

         if (unseen < 400L) {
            yawSpeed = 8.0F + 20.0F * (unseen / 400.0F);
         } else {
            yawSpeed = 15.0F;
         }

         if (unseen < 600L) {
            pitchSpeed = 4.0F + 11.0F * (unseen / 600.0F);
         } else {
            pitchSpeed = 8.0F + this.random.nextFloat() * 4.0F;
         }
      }

      yawSpeed *= this.speedMul;
      pitchSpeed *= this.speedMul;
      if (lookaway) {
         yawSpeed = Math.min(yawSpeed, 12.0F);
         pitchSpeed = Math.min(pitchSpeed, 8.0F);
      }

      double dist = eye.distanceTo(aim);
      if (dist < 0.9 && target.getVelocity().horizontalLength() > 0.05) {
         yawSpeed = Math.min(yawSpeed, 12.0F + this.random.nextFloat() * 6.0F);
      }

      Vec3d toGoal = goal.subtract(eye);
      double horizontal = Math.sqrt(toGoal.x * toGoal.x + toGoal.z * toGoal.z);
      float idealYaw = (float)(Math.toDegrees(Math.atan2(toGoal.z, toGoal.x)) - 90.0);
      float idealPitch = MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(toGoal.y, horizontal))), -89.0F, 89.0F);
      float dYaw = MathHelper.wrapDegrees(idealYaw - this.preciseYaw);
      float dPitch = idealPitch - this.precisePitch;
      float stepYaw = MathHelper.clamp(dYaw, -yawSpeed, yawSpeed);
      float stepPitch = MathHelper.clamp(dPitch, -pitchSpeed, pitchSpeed);
      double time = now / 1000.0;
      float yawJitter = (float)(Math.sin(time * 6.0 + this.jitterPhase) * this.yawJitterAmp + (this.random.nextFloat() - 0.5F) * this.yawJitterRnd);
      float pitchJitter = (float)(Math.sin(time * 5.0 + this.jitterPhase * 1.7) * this.yawJitterAmp * 0.4 + (this.random.nextFloat() - 0.5F) * this.yawJitterRnd * 0.4);
      float newYaw = MathHelper.wrapDegrees(this.preciseYaw + stepYaw + yawJitter);
      float newPitch = MathHelper.clamp(this.precisePitch + stepPitch + pitchJitter, -89.0F, 89.0F);
      this.preciseYaw = newYaw;
      this.precisePitch = newPitch;
      float roll = this.random.nextFloat();
      Vec2f out;
      if (roll < 0.05F) {
         out = new Vec2f(newYaw, newPitch);
      } else {
         out = correctRotation(newYaw, newPitch);
         if (roll >= 0.95F) {
            float gcdStep = (float)(this.getGcd() * 0.15);
            out = new Vec2f(out.x + Math.signum(stepYaw) * gcdStep, out.y);
         }
      }

      RotationStorage.update(
         new Rotation(out.x, out.y),
         Math.abs(stepYaw) + 8.0F,
         Math.abs(stepPitch) + 6.0F,
         30.0F,
         20.0F,
         10,
         5,
         Aura.clientLook.isState()
      );
      this.lastYaw = out.x;
      this.lastPitch = out.y;
      this.hasTarget = true;
      this.returnTicksLeft = 20;
   }

   public void tickReturn() {
      if (!this.hasTarget || this.returnTicksLeft <= 0 || mc.player == null) {
         return;
      }

      float dYaw = MathHelper.wrapDegrees(this.lastYaw - mc.player.getYaw());
      float dPitch = this.lastPitch - mc.player.getPitch();
      if (Math.abs(dYaw) < 1.0F && Math.abs(dPitch) < 1.0F) {
         this.returnTicksLeft = 0;
         return;
      }

      float stepYaw = MathHelper.clamp(dYaw, -30.0F, 30.0F);
      float stepPitch = MathHelper.clamp(dPitch, -20.0F, 20.0F);
      Vec2f out = correctRotation(MathHelper.wrapDegrees(mc.player.getYaw() + stepYaw), MathHelper.clamp(mc.player.getPitch() + stepPitch, -89.0F, 89.0F));
      this.preciseYaw = out.x;
      this.precisePitch = out.y;
      RotationStorage.update(
         new Rotation(out.x, out.y), Math.abs(stepYaw) + 8.0F, Math.abs(stepPitch) + 6.0F, 30.0F, 20.0F, 10, 5, Aura.clientLook.isState()
      );
      this.returnTicksLeft--;
   }

   public void reset() {
      this.lastTarget = null;
      this.hasTarget = false;
      this.returnTicksLeft = 0;
      this.invisibleSinceMs = 0L;
      this.nextPointMs = 0L;
      this.nextLookawayMs = 0L;
      this.lookawayUntilMs = 0L;
   }

   private void rollPattern(long now) {
      this.speedMul = 0.85F + this.random.nextFloat() * 0.35F;
      this.yawJitterAmp = 0.3F + this.random.nextFloat() * 0.6F;
      this.yawJitterRnd = 0.2F + this.random.nextFloat() * 0.4F;
      this.jitterPhase = this.random.nextFloat() * (float)(Math.PI * 2.0);
      this.patternUntilMs = now + 10000L + this.random.nextInt(10000);
   }

   private Vec3d computeAimPoint(Box box, LivingEntity target) {
      double xFrac = 0.5;
      double yFrac = 0.75;
      double zFrac = 0.5;
      if (this.pointIdx == 1) {
         yFrac = 0.82;
      } else if (this.pointIdx == 2) {
         yFrac = 0.88;
      } else if (this.pointIdx == 3 || this.pointIdx == 4) {
         xFrac = 0.5 + (this.random.nextDouble() - 0.5) * 0.3;
         yFrac = 0.55 + (this.random.nextDouble() - 0.5) * 0.4;
         zFrac = 0.5 + (this.random.nextDouble() - 0.5) * 0.3;
      }

      double x = box.minX + box.getLengthX() * xFrac;
      double y = box.minY + box.getLengthY() * yFrac;
      double z = box.minZ + box.getLengthZ() * zFrac;
      Vec3d lead = target.getVelocity().multiply(0.85);
      double leadHorizontal = Math.sqrt(lead.x * lead.x + lead.z * lead.z);
      if (leadHorizontal > 0.3) {
         lead = lead.multiply(0.3 / leadHorizontal);
      }

      return new Vec3d(x, y, z).add(lead);
   }

   private boolean isVisible(Vec3d eye, Vec3d aim) {
      if (mc.world == null) {
         return true;
      }

      return mc.world
         .raycast(new RaycastContext(eye, aim, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player))
         .getType() == HitResult.Type.MISS;
   }

   private boolean isExact(Vec3d eye, LivingEntity target) {
      Vec3d look = mc.player.getRotationVector();
      double reach = eye.distanceTo(target.getBoundingBox().getCenter()) + 1.0;
      return target.getBoundingBox().raycast(eye, eye.add(look.multiply(reach))).isPresent();
   }

   private double getGcd() {
      double s = 0.5;
      if (mc != null && mc.options != null) {
         s = mc.options.getMouseSensitivity().getValue();
      }

      double d = s * 0.6 + 0.2;
      return d * d * d * 8.0;
   }

}
