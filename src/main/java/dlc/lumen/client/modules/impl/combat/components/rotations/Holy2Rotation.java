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

public class Holy2Rotation extends RotationsSystem implements QClient {
   private static final float VOLUME = 180.0F;
   private static final float VOLUME2 = 1080.0F;
   private static final float VOLUME3 = 1080.0F;
   private static final float VOLUME4 = 1080.0F;
   private static final float VOLUME5 = 0.05F;
   private static final float VOLUME6 = 0.16F;
   private static final float VOLUME7 = 0.07F;
   private static final float VOLUME8 = 0.015F;
   private static final float VOLUME9 = 0.09F;
   private static final float VOLUME10 = 54.0F;
   private static final float VOLUME11 = 0.68F;
   private static final float VOLUME12 = 0.08F;
   private static final float VOLUME13 = 0.06F;
   private static final int INDEX = 0;
   private static final int INDEX2 = 1;
   private static final int INDEX3 = 2;
   private static final int INDEX4 = 3;
   private static final int INDEX5 = 4;
   private static final int INDEX6 = 5;
   private final Random random = new Random();
   private float currentFov = 180.0F;
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
   private int index2;
   private float volume6;
   private float volume7;
   private float volume8;
   private float volume9;
   private float volume10;
   private float volume11;
   private float volume12;
   private float volume13;
   private float volume14;
   private int index3;
   private boolean flag4;
   private float volume15;
   private float volume16;
   private float volume17;
   private float volume18;
   private float volume19;
   private float volume20;
   private float volume21;

   public float getCurrentFov() {
      return this.currentFov;
   }

   public void reset() {
      this.currentFov = 180.0F;
      this.updateState2();
      this.updateState5();
   }

   public void onAttack() {
      this.currentFov = 180.0F;
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
            this.currentFov = 180.0F;
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
               float var5 = MathHelper.clamp(var3.y, -89.0F, 89.0F);
               if (!this.checkCondition2(var4)) {
                  this.updateState2();
                  this.updateState5();
               } else {
                  long var6 = System.nanoTime();
                  this.updateState3(target, var6, var4, var5);
                  Rotation var8 = this.flag ? this.computeRotation(var6) : new Rotation(var4, var5);
                  RotationStorage.update(var8, 1080.0F, 1080.0F, 1080.0F, 1080.0F, 0, 1, false);
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
         float var5 = MathHelper.clamp(var3.y, -89.0F, 89.0F);
         RotationStorage.update(new Rotation(var4, var5), 1080.0F, 1080.0F, 1080.0F, 1080.0F, 0, 1, false);
         this.updateState(target, var4, var5);
         this.currentFov = 180.0F;
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
      this.index2 = 0;
      this.index3 = 0;
      this.flag4 = false;
      this.volume15 = 0.0F;
      this.volume16 = 0.0F;
      this.volume17 = 0.0F;
      this.volume18 = 0.0F;
      this.volume19 = 0.0F;
      this.volume20 = 0.0F;
      this.volume21 = 0.0F;
   }

   private void updateState3(LivingEntity target, long now, float targetYaw, float targetPitch) {
      Rotation var6 = this.flag ? this.computeRotation(now) : this.computeRotation2();
      float var7 = var6.getYaw();
      float var8 = var6.getPitch();
      float var9 = (float)Math.hypot(MathHelper.wrapDegrees(targetYaw - var7), targetPitch - var8);
      float var10 = this.livingEntity == target
         ? (float)Math.hypot(MathHelper.wrapDegrees(targetYaw - this.volume2), targetPitch - this.volume4)
         : var9;
      float var11 = this.flag3 ? Math.abs(MathHelper.wrapDegrees(targetYaw - this.volume5)) : 0.0F;
      double var12 = this.resolveDouble(now);
      boolean var14 = this.livingEntity != target;
      boolean var15 = !this.flag && (var14 || var9 > 0.05F || var10 > 0.05F);
      boolean var16 = this.flag
         && (var12 >= 0.999 || var10 > 0.16F && var12 >= 0.015F || var9 > 0.19999999F && var12 >= 0.09F || var11 > 0.07F && var12 >= 0.015F);
      if (var15 || var16) {
         boolean var17 = var9 > 2.0F || var11 > 0.07F;
         this.updateState4(target, now, var6.getYaw(), var6.getPitch(), targetYaw, targetPitch, var17);
      }

      this.volume5 = targetYaw;
      this.flag3 = true;
   }

   private void updateState4(LivingEntity target, long now, float startYaw, float startPitch, float targetYaw, float targetPitch, boolean action) {
      this.livingEntity = target;
      this.flag = true;
      this.flag2 = action;
      this.index = SlothDatasetProfile.choose(this.random, action);
      float var9 = this.computefloat3();
      this.flag4 = var9 > 0.08F;
      this.index3 = this.resolveInt(action, var9);
      this.volume15 = this.computefloat4();
      this.volume16 = this.computefloat7(this.flag4 ? 0.34F : 0.08F, this.flag4 ? 0.82F : 0.24F) * Math.max(0.25F, var9);
      this.volume17 = this.computefloat7(this.flag4 ? 0.24F : 0.06F, this.flag4 ? 0.58F : 0.16F) * Math.max(0.2F, var9);
      this.timestamp = now;
      this.volume = MathHelper.wrapDegrees(startYaw);
      this.volume3 = MathHelper.clamp(startPitch, -89.0F, 89.0F);
      this.volume2 = MathHelper.wrapDegrees(targetYaw);
      this.volume4 = MathHelper.clamp(targetPitch, -89.0F, 89.0F);
      float var10 = Math.abs(MathHelper.wrapDegrees(this.volume2 - this.volume));
      float var11 = Math.abs(this.volume4 - this.volume3);
      if (this.flag4) {
         this.index2 = action ? this.random.nextInt(3) + 1 : this.random.nextInt(2) + 1;
      } else {
         this.index2 = action ? this.random.nextInt(5) + 2 : this.random.nextInt(4) + 1;
      }

      if (this.index3 == 5) {
         this.index2 = this.index2 + (this.flag4 ? 1 : 2);
      } else if (this.index3 == 1 && this.random.nextFloat() < 0.55F) {
         this.index2++;
      }

      if (var10 > 120.0F && this.random.nextFloat() < 0.78F) {
         this.index2++;
      }

      float var12 = Math.signum(MathHelper.wrapDegrees(this.volume2 - this.volume));
      if (var12 == 0.0F) {
         var12 = this.random.nextBoolean() ? 1.0F : -1.0F;
      }

      this.volume6 = this.random.nextFloat() < 0.42F ? -var12 : var12;
      this.volume7 = this.computefloat7(action ? 42.0F : 28.0F, action ? 140.0F : 104.0F);
      this.volume8 = this.computefloat7(action ? 14.0F : 9.0F, action ? 56.0F : 34.0F);
      this.volume18 = this.computefloat7(this.flag4 ? 14.0F : 8.0F, this.flag4 ? 42.0F : 30.0F);
      this.volume19 = this.computefloat7(this.flag4 ? 6.0F : 4.0F, this.flag4 ? 24.0F : 16.0F);
      this.volume20 = this.computefloat7(action ? 8.0F : 4.0F, action ? 28.0F : 16.0F);
      this.volume21 = this.computefloat7(action ? 10.0F : 5.0F, action ? 38.0F : 20.0F);
      this.volume9 = this.random.nextFloat() * (float) (Math.PI * 2);
      this.volume10 = this.computefloat7(action ? 3.2F : 2.5F, action ? 6.6F : 4.7F);
      this.volume11 = this.computefloat7(action ? 1.7F : 1.3F, action ? 3.2F : 2.5F);
      this.volume12 = this.computefloat7(this.flag4 ? 0.82F : 1.28F, this.flag4 ? 1.62F : 2.8F);
      this.volume13 = this.computefloat7(this.flag4 ? 4.2F : 3.0F, this.flag4 ? 9.8F : 7.2F);
      this.volume14 = this.computefloat7(this.flag4 ? 2.5F : 1.8F, this.flag4 ? 6.4F : 4.9F);
      float var13 = MathHelper.clamp(0.38F + var10 / 115.0F + var11 / 80.0F, 0.42F, 1.18F);
      float var14 = this.computefloat7(action ? 18.0F : 24.0F, action ? 56.0F : 74.0F);
      if (this.flag4) {
         var14 *= this.computefloat7(0.58F, 0.84F);
      }

      if (this.index3 == 3) {
         var14 *= 0.76F;
      } else if (this.index3 == 5) {
         var14 *= 1.08F;
      }

      long var15 = (long)(var14 * var13);
      if (this.index2 >= 4) {
         var15 += this.flag4 ? 4L : 8L;
      }

      long var17 = this.flag4 ? 84L : 108L;
      this.timestamp2 = Math.max(12L, Math.min(var15, var17)) * 1000000L;
   }

   private Rotation computeRotation(long now) {
      if (this.flag && this.timestamp2 > 0L) {
         double var3 = this.resolveDouble(now);
         double var5 = SlothDatasetProfile.progress(this.flag2, this.index, var3);
         double var7 = this.computefloat6((float)var5, (float)this.resolveDouble3(var3, this.volume10), 0.58F);
         double var9 = this.resolveDouble3(var3, this.volume11);
         double var11 = SlothDatasetProfile.lateral(this.flag2, this.index, var3);
         float var13 = MathHelper.wrapDegrees(this.volume2 - this.volume);
         float var14 = this.volume4 - this.volume3;
         float var15 = MathHelper.wrapDegrees(this.volume + var13 * (float)var7);
         float var16 = MathHelper.clamp(this.volume3 + var14 * (float)var7, -89.0F, 89.0F);
         float var17 = Math.min((float)Math.hypot(var13, var14), 54.0F);
         float var18 = var13 == 0.0F ? this.volume6 : Math.signum(var13);
         float var19 = var18 * var17 * (float)(var11 * this.volume12);
         float var20 = (float)(-var18 * var17 * var11 * 0.68F);
         float var21 = this.volume6 * this.index2 * 360.0F * (float)var9;
         float var22 = (float)Math.pow(Math.max(0.0, 1.0 - Math.abs(var3 * 2.0 - 1.0)), 0.3);
         float var23 = (float)Math.pow(Math.max(0.0, 1.0 - var3), 0.18);
         float var24 = this.volume7 * var22 * (float)Math.sin(var3 * Math.PI * this.volume13 + this.volume9);
         float var25 = this.volume8 * var22 * (float)Math.cos(var3 * Math.PI * this.volume14 + this.volume9 * 0.71F);
         float var26 = this.volume18
            * var23
            * this.computefloat5(var3 * Math.PI * (this.volume13 * 1.65F + 1.2F) + this.volume9 * 1.4F);
         float var27 = this.volume19 * var23 * (float)Math.sin(var3 * Math.PI * (this.volume14 * 1.85F + 0.9F) + this.volume9 * 1.1F);
         float var28;
         float var29;
         switch (this.index3) {
            case 1:
               float var41 = (float)Math.pow(Math.sin(var3 * Math.PI), 0.72);
               float var43 = -this.volume6 * this.volume21 * var41;
               var28 = var21 * 0.82F + var19 * 1.34F + var24 * 0.72F + var43 + var26;
               var29 = var20 * 0.48F + var25 * 0.86F + var27;
               break;
            case 2:
               float var40 = this.computefloat5(var3 * Math.PI * (this.volume13 * 0.78F) + this.volume9);
               var28 = var21 * 0.46F + var19 * 1.18F + var40 * this.volume7 + var26 * 0.9F;
               var29 = var20 * 0.82F + var40 * this.volume8 * 0.62F + var25 * 0.38F + var27;
               break;
            case 3:
               float var39 = (float)Math.sin(var3 * Math.PI * (this.volume13 * 0.58F) + this.volume9);
               var28 = var21 * 0.32F + var19 * 0.92F + var39 * this.volume7 * 0.68F + var26 * 0.75F;
               var29 = var20 * 0.34F + var25 * 0.26F + var27 * 0.45F;
               break;
            case 4:
               float var38 = (float)Math.sin(var3 * Math.PI * (this.volume13 * 1.35F) + this.volume9 * 1.4F);
               var28 = var21 * 0.74F + var19 * 1.52F + var24 * 0.85F + var38 * this.volume18 * 1.45F;
               var29 = var20 + var25 * 0.55F - var38 * this.volume8 * 0.7F + var27;
               break;
            case 5:
               float var30 = (float)Math.sin(var3 * Math.PI * (this.volume13 * 0.92F) + this.volume9);
               float var31 = (float)(1.0 - Math.pow(1.0 - var3, 2.4));
               var28 = var21 * 1.18F + var19 * 1.12F + var24 * 0.58F + var26 * 1.1F + var30 * this.volume7 * 0.42F;
               var29 = var20 + var25 + var27 + (var31 - 0.35F) * this.volume20;
               break;
            default:
               var28 = var21 + var19 + var24 + var26 * 0.55F;
               var29 = var20 + var25 + var27 * 0.65F;
         }

         float var42 = this.computefloat3();
         float var44 = this.computefloat6(this.volume15, this.computefloat4(), 0.42F + var42 * 0.38F);
         float var32 = (float)Math.pow(Math.max(0.0, 1.0 - var3), this.flag4 ? 0.42F : 0.9F);
         float var33 = MathHelper.wrapDegrees(var44 - var15) * this.volume16 * var32 * (0.35F + var42 * 0.75F);
         float var34 = (this.computefloat2() - var16) * this.volume17 * var32 * (0.28F + var42 * 0.6F);
         float var35 = -var18 * this.volume21 * (float)Math.pow(var3, 1.65F) * (float)Math.pow(Math.max(0.0, 1.0 - var3), 0.18F);
         float var36 = MathHelper.wrapDegrees(var15 + var28 + var33 - var35);
         float var37 = MathHelper.clamp(var16 + var29 + var34, -89.0F, 89.0F);
         if (var3 >= 0.999) {
            this.flag = false;
            var36 = this.volume2;
            var37 = this.volume4;
         }

         return new Rotation(var36, var37);
      } else {
         return new Rotation(this.volume2, this.volume4);
      }
   }

   private int resolveInt(boolean action, float movementIntensity) {
      double var3 = this.random.nextDouble();
      if (movementIntensity > 0.45F) {
         if (var3 < 0.3) {
            return 3;
         } else if (var3 < 0.54) {
            return 2;
         } else if (var3 < 0.74) {
            return 1;
         } else {
            return var3 < 0.9 ? 4 : 5;
         }
      } else if (action) {
         if (var3 < 0.22) {
            return 5;
         } else if (var3 < 0.42) {
            return 1;
         } else if (var3 < 0.6) {
            return 4;
         } else {
            return var3 < 0.8 ? 0 : 2;
         }
      } else if (var3 < 0.18) {
         return 0;
      } else if (var3 < 0.38) {
         return 2;
      } else if (var3 < 0.56) {
         return 3;
      } else if (var3 < 0.74) {
         return 1;
      } else {
         return var3 < 0.88 ? 4 : 5;
      }
   }

   private Rotation computeRotation2() {
      return new Rotation(MathHelper.wrapDegrees(mc.player.getYaw()), MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F));
   }

   private float computefloat3() {
      if (mc.player != null && mc.player.input != null) {
         float var1 = MathHelper.clamp(Math.abs(mc.player.input.movementForward) + Math.abs(mc.player.input.movementSideways) * 0.85F, 0.0F, 1.0F);
         Vec3d var2 = mc.player.getVelocity();
         double var3 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
         float var5 = MathHelper.clamp((float)(var3 / 0.23), 0.0F, 1.0F);
         float var6 = mc.player.isSprinting() ? 0.14F : 0.0F;
         return MathHelper.clamp(Math.max(var1, var5) + var6, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   private float computefloat4() {
      float var1 = this.computefloat();
      if (mc.player != null && mc.player.input != null) {
         float var2 = mc.player.input.movementForward;
         float var3 = mc.player.input.movementSideways;
         if (var2 == 0.0F && var3 == 0.0F) {
            Vec3d var4 = mc.player.getVelocity();
            double var5 = Math.sqrt(var4.x * var4.x + var4.z * var4.z);
            return var5 > 0.06F ? (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var4.z, var4.x)) - 90.0) : var1;
         } else {
            return MathHelper.wrapDegrees((float)Math.toDegrees(this.resolveDouble2(var1, var2, var3)));
         }
      } else {
         return var1;
      }
   }

   private double resolveDouble(long now) {
      return this.flag && this.timestamp2 > 0L
         ? Math.max(0.0, Math.min(1.0, (double)(now - this.timestamp) / this.timestamp2))
         : 1.0;
   }

   private double resolveDouble2(float rotationYaw, float moveForward, float moveStrafing) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float var4 = 1.0F;
      if (moveForward < 0.0F) {
         var4 = -0.5F;
      } else if (moveForward > 0.0F) {
         var4 = 0.5F;
      }

      if (moveStrafing > 0.0F) {
         rotationYaw -= 90.0F * var4;
      } else if (moveStrafing < 0.0F) {
         rotationYaw += 90.0F * var4;
      }

      return Math.toRadians(rotationYaw);
   }

   private double resolveDouble3(double progress, float exponent) {
      return 1.0 - Math.pow(1.0 - progress, exponent);
   }

   private float computefloat5(double phase) {
      return Math.signum((float)Math.sin(phase));
   }

   private float computefloat6(float start, float end, float progress) {
      return start + (end - start) * MathHelper.clamp(progress, 0.0F, 1.0F);
   }

   private float computefloat7(float minimum, float maximum) {
      return minimum + (maximum - minimum) * this.random.nextFloat();
   }

   private void updateState5() {
      if (RotationStorage.instance != null && RotationStorage.instance.isRotating()) {
         RotationStorage.instance.stopRotation();
      }
   }
}
