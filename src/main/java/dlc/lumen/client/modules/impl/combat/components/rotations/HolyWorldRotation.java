package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.FreeLookStorage;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.MultipointUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.interpolation.BestPoint;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class HolyWorldRotation extends RotationsSystem implements QClient {
   private static final float VOLUME = 40.0F;
   private static final float VOLUME2 = 360.0F;
   private static final float VOLUME3 = 360.0F;
   private static final float VOLUME4 = 360.0F;
   private static final float VOLUME5 = 0.35F;
   private static final float VOLUME6 = 0.9F;
   private static final float VOLUME7 = 0.7F;
   private static final float VOLUME8 = 0.18F;
   private static final float VOLUME9 = 0.52F;
   private static final float VOLUME10 = 18.0F;
   private static final float VOLUME11 = 0.14F;
   private final Random random = new Random();
   private float currentFov = 40.0F;
   private LivingEntity livingEntity;
   private boolean flag;
   private boolean flag2;
   private int index;
   private long timestamp;
   private long timestamp2;
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4;
   private float volume5;
   private boolean flag3;

   public float getCurrentFov() {
      return this.currentFov;
   }

   public void reset() {
      this.currentFov = 40.0F;
      this.updateState2();
      this.updateState5();
   }

   public void onAttack() {
      this.currentFov = 40.0F;
   }

   public boolean prepareAttack(LivingEntity target) {
      return !this.isInFov(target) ? false : this.checkCondition(target);
   }

   public boolean isInFov(LivingEntity target) {
      if (mc.player != null && target != null) {
         Vec3d var2 = this.computeVec3d(target);
         return var2 == null ? false : this.checkCondition2(MathHelper.wrapDegrees(RotationUtils.getRotations(var2).x));
      } else {
         return false;
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null) {
         if (target == null) {
            this.currentFov = 40.0F;
            this.updateState2();
            this.updateState5();
         } else {
            Vec3d var2 = this.computeVec3d(target);
            if (var2 == null) {
               this.updateState2();
               this.updateState5();
            } else {
               Vec2f var3 = RotationUtils.getRotations(var2);
               float var4 = MathHelper.wrapDegrees(var3.x);
               if (!this.checkCondition2(var4)) {
                  this.updateState2();
                  this.updateState5();
               } else {
                  long var5 = System.nanoTime();
                  float var7 = this.computefloat2();
                  this.updateState3(target, var5, var4, var7);
                  Rotation var8 = this.flag ? this.computeRotation(var5) : new Rotation(var4, var7);
                  RotationStorage.update(var8, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
               }
            }
         }
      }
   }

   private boolean checkCondition(LivingEntity target) {
      if (mc.player != null && target != null) {
         Vec3d var2 = this.computeVec3d(target);
         if (var2 == null) {
            return false;
         }

         Vec2f var3 = RotationUtils.getRotations(var2);
         float var4 = MathHelper.wrapDegrees(var3.x);
         float var5 = this.computefloat2();
         RotationStorage.update(new Rotation(var4, var5), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
         this.updateState(target, var4, var5);
         this.currentFov = 40.0F;
         return true;
      } else {
         return false;
      }
   }

   private Vec3d computeVec3d(LivingEntity target) {
      Vec3d var2 = BestPoint.getNearestPoint(target);
      if (var2 == null) {
         var2 = MultipointUtils.getClosestPoint(target);
      }

      if (var2 == null) {
         var2 = this.getPredictedBox(target).getCenter();
      }

      return var2;
   }

   private float computefloat() {
      return FreeLookStorage.isActive() ? FreeLookStorage.getFreeYaw() : mc.player.getYaw();
   }

   private float computefloat2() {
      return FreeLookStorage.isActive()
         ? MathHelper.clamp(FreeLookStorage.getFreePitch(), -89.0F, 89.0F)
         : MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F);
   }

   private boolean checkCondition2(float targetYaw) {
      return Math.abs(MathHelper.wrapDegrees(targetYaw - this.computefloat())) <= this.currentFov;
   }

   private void updateState(LivingEntity target, float yaw, float pitch) {
      this.livingEntity = target;
      this.flag = false;
      this.volume2 = MathHelper.wrapDegrees(yaw);
      this.volume4 = MathHelper.clamp(pitch, -89.0F, 89.0F);
      this.volume5 = this.volume2;
      this.flag3 = true;
   }

   private void updateState2() {
      this.livingEntity = null;
      this.flag = false;
      this.flag3 = false;
   }

   private void updateState3(LivingEntity target, long now, float targetYaw, float targetPitch) {
      Rotation var6 = this.flag ? this.computeRotation(now) : this.computeRotation2();
      float var7 = var6.getYaw();
      float var8 = Math.abs(MathHelper.wrapDegrees(targetYaw - var7));
      float var9 = this.livingEntity == target ? Math.abs(MathHelper.wrapDegrees(targetYaw - this.volume2)) : var8;
      float var10 = this.flag3 ? Math.abs(MathHelper.wrapDegrees(targetYaw - this.volume5)) : 0.0F;
      double var11 = this.resolveDouble(now);
      boolean var13 = this.livingEntity != target;
      boolean var14 = !this.flag && (var13 || var8 > 0.35F || var9 > 0.35F);
      boolean var15 = this.flag
         && (var11 >= 0.999 || var9 > 0.9F && var11 >= 0.18F || var8 > 1.3499999F && var11 >= 0.52F || var10 > 0.7F && var11 >= 0.18F);
      if (var14 || var15) {
         boolean var16 = var8 > 5.5F || var10 > 0.7F;
         this.updateState4(target, now, var6.getYaw(), var6.getPitch(), targetYaw, targetPitch, var16);
      }

      this.volume5 = targetYaw;
      this.flag3 = true;
   }

   private void updateState4(LivingEntity target, long now, float startYaw, float startPitch, float targetYaw, float targetPitch, boolean action) {
      this.livingEntity = target;
      this.flag = true;
      this.flag2 = action;
      this.index = SlothDatasetProfile.choose(this.random, action);
      this.timestamp = now;
      this.volume = MathHelper.wrapDegrees(startYaw);
      this.volume3 = MathHelper.clamp(startPitch, -89.0F, 89.0F);
      this.volume2 = MathHelper.wrapDegrees(targetYaw);
      this.volume4 = MathHelper.clamp(targetPitch, -89.0F, 89.0F);
      float var9 = Math.abs(MathHelper.wrapDegrees(this.volume2 - this.volume));
      float var10 = MathHelper.clamp(0.72F + var9 / 28.0F, 0.8F, 1.24F);
      long var11 = (long)((float)SlothDatasetProfile.duration(action, this.index) * var10 * this.computefloat3(0.92F, 1.08F));
      long var13 = action ? 260L : 320L;
      var11 = Math.max(75L, Math.min(var11, var13));
      this.timestamp2 = var11 * 1000000L;
   }

   private Rotation computeRotation(long now) {
      if (this.flag && this.timestamp2 > 0L) {
         double var3 = this.resolveDouble(now);
         double var5 = SlothDatasetProfile.progress(this.flag2, this.index, var3);
         double var7 = SlothDatasetProfile.lateral(this.flag2, this.index, var3);
         float var9 = MathHelper.wrapDegrees(this.volume2 - this.volume);
         float var10 = this.volume4 - this.volume3;
         float var11 = MathHelper.wrapDegrees(this.volume + var9 * (float)var5);
         float var12 = MathHelper.clamp(this.volume3 + var10 * (float)var5, -89.0F, 89.0F);
         float var13 = Math.min((float)Math.hypot(var9, var10), 18.0F);
         float var14 = var9 == 0.0F ? 1.0F : Math.signum(var9);
         float var15 = (float)(var14 * var13 * var7);
         float var16 = (float)(-var14 * var13 * var7 * 0.14F);
         float var17 = MathHelper.wrapDegrees(var11 + var15);
         float var18 = MathHelper.clamp(var12 + var16, -89.0F, 89.0F);
         if (var3 >= 0.999) {
            this.flag = false;
            var17 = this.volume2;
            var18 = this.volume4;
         }

         return new Rotation(var17, var18);
      } else {
         return new Rotation(this.volume2, this.volume4);
      }
   }

   private Rotation computeRotation2() {
      return new Rotation(MathHelper.wrapDegrees(mc.player.getYaw()), MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F));
   }

   private double resolveDouble(long now) {
      return this.flag && this.timestamp2 > 0L
         ? Math.max(0.0, Math.min(1.0, (double)(now - this.timestamp) / this.timestamp2))
         : 1.0;
   }

   private float computefloat3(float minimum, float maximum) {
      return minimum + (maximum - minimum) * this.random.nextFloat();
   }

   private void updateState5() {
      if (RotationStorage.instance != null && RotationStorage.instance.isRotating()) {
         RotationStorage.instance.stopRotation();
      }
   }
}
