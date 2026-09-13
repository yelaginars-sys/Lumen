package dlc.lumen.api.utils.rotate;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HumanBreakRotation implements QClient {
   private static final double dvalue = 0.16;
   private static final float fvalue = 3.2F;
   private static final float fvalue2 = 2.4F;
   private final Random random = new Random();
   private long timeMs = Long.MIN_VALUE;
   private double dvalue2;
   private double dvalue3;
   private double dvalue4;
   private double dvalue5;
   private float fvalue3;
   private float fvalue4;
   private float wantYaw;
   private float wantPitch;
   private boolean onTarget;
   private long now;
   private long timeMs2;
   private long timeMs3;
   private boolean flag;
   private float yaw = 1.0F;
   private float fvalue5;
   private float fvalue6;
   private long timeMs4;

   public void reset() {
      this.timeMs = Long.MIN_VALUE;
      this.onTarget = false;
      this.now = 0L;
      this.timeMs2 = 0L;
      this.timeMs3 = 0L;
      this.flag = false;
      this.yaw = 1.0F;
      this.fvalue5 = 0.0F;
      this.fvalue6 = 0.0F;
      this.timeMs4 = 0L;
   }

   public void aimAt(BlockPos pos, Direction face) {
      if (mc.player != null && pos != null) {
         long var3 = System.nanoTime();
         if (this.timeMs != pos.asLong()) {
            this.handlePos(pos, var3);
         }

         Vec3d var5 = this.getPos(pos, face, var3);
         Vec3d var6 = mc.player.getEyePos();
         double var7 = var5.x - var6.x;
         double var9 = var5.y - var6.y;
         double var11 = var5.z - var6.z;
         double var13 = Math.sqrt(var7 * var7 + var11 * var11);
         float var15 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var11, var7)) - 90.0);
         float var16 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var9, var13)), -89.0, 89.0);
         if (var3 < this.timeMs3) {
            this.handleYaw(mc.player.getYaw(), mc.player.getPitch());
         } else {
            if (!this.onTarget) {
               this.handleWantYaw(var15, var16, var3, this.calcPos(pos));
            } else if (var3 >= this.timeMs2 && !this.flag) {
               float var17 = this.calcYawA(mc.player.getYaw(), mc.player.getPitch(), var15, var16);
               if (var17 > 3.2F) {
                  this.handleWantYaw(var15, var16, var3, this.calcPos(pos));
               } else {
                  this.wantYaw = var15;
                  this.wantPitch = var16;
               }
            } else {
               this.wantYaw = var15;
               this.wantPitch = var16;
            }

            float var18;
            float var24;
            if (var3 < this.timeMs2) {
               double var19 = this.timeMs2 - this.now;
               double var21 = var19 <= 0.0 ? 1.0 : (var3 - this.now) / var19;
               float var23 = (float)this.calcValue2(MathHelper.clamp(var21, 0.0, 1.0)) * this.yaw;
               var24 = this.fvalue3 + MathHelper.wrapDegrees(this.wantYaw - this.fvalue3) * var23;
               var18 = this.fvalue4 + (this.wantPitch - this.fvalue4) * var23;
            } else {
               if (this.flag) {
                  this.flag = false;
                  this.yaw = 1.0F;
                  this.fvalue3 = mc.player.getYaw();
                  this.fvalue4 = mc.player.getPitch();
                  this.now = var3;
                  this.timeMs2 = var3 + this.calcMinMillis(70L, 145L);
                  this.handleYaw(this.fvalue3, this.fvalue4);
                  return;
               }

               float var27 = 0.32F + this.random.nextFloat() * 0.16F;
               var24 = mc.player.getYaw() + MathHelper.wrapDegrees(this.wantYaw - mc.player.getYaw()) * var27;
               var18 = mc.player.getPitch() + (this.wantPitch - mc.player.getPitch()) * var27;
               this.handleNow(var3);
               var24 += this.fvalue5;
               var18 += this.fvalue6;
            }

            this.handleYaw(var24, var18);
         }
      }
   }

   public boolean isOnTarget() {
      if (mc.player != null && this.onTarget) {
         return System.nanoTime() < this.timeMs3
            ? false
            : this.calcYawA(mc.player.getYaw(), mc.player.getPitch(), this.wantYaw, this.wantPitch) <= 2.4F;
      } else {
         return false;
      }
   }

   private void handlePos(BlockPos pos, long now) {
      this.timeMs = pos.asLong();
      this.dvalue2 = this.calcValue(0.5 + this.random.nextGaussian() * 0.17);
      this.dvalue3 = this.calcValue(0.5 + this.random.nextGaussian() * 0.17);
      this.dvalue4 = this.random.nextDouble() * Math.PI * 2.0;
      this.dvalue5 = this.random.nextDouble() * Math.PI * 2.0;
      this.timeMs3 = now + this.calcMinMillis(55L, 165L);
      this.onTarget = false;
      this.flag = false;
      this.fvalue5 = 0.0F;
      this.fvalue6 = 0.0F;
   }

   private void handleWantYaw(float wantYaw, float wantPitch, long now, double blockDistance) {
      this.fvalue3 = mc.player.getYaw();
      this.fvalue4 = mc.player.getPitch();
      this.wantYaw = wantYaw;
      this.wantPitch = wantPitch;
      this.onTarget = true;
      float var7 = this.calcYawA(this.fvalue3, this.fvalue4, wantYaw, wantPitch);
      double var8 = Math.toDegrees(Math.atan2(0.5, Math.max(0.6, blockDistance))) * 2.0;
      double var10 = Math.log(1.0 + var7 / Math.max(1.2, var8)) / Math.log(2.0);
      long var12 = (long)((85.0 + 132.0 * var10) * (0.85 + this.random.nextDouble() * 0.3));
      this.now = now;
      this.timeMs2 = now + var12 * 1000000L;
      if (var7 > 22.0F && this.random.nextDouble() < 0.72) {
         this.yaw = 0.86F + this.random.nextFloat() * 0.1F;
         this.flag = true;
      } else {
         this.yaw = 1.0F;
         this.flag = false;
      }
   }

   private Vec3d getPos(BlockPos pos, Direction face, long now) {
      double var5 = now / 1.0E9;
      double var7 = this.calcValue(
         this.dvalue2 + Math.sin(var5 * 0.9 + this.dvalue4) * 0.035 + Math.sin(var5 * 2.3 + this.dvalue5) * 0.012
      );
      double var9 = this.calcValue(
         this.dvalue3 + Math.cos(var5 * 1.1 + this.dvalue5) * 0.035 + Math.sin(var5 * 1.7 + this.dvalue4) * 0.012
      );
      double var11 = pos.getX();
      double var13 = pos.getY();
      double var15 = pos.getZ();

      return switch (face) {
         case UP -> new Vec3d(var11 + var7, var13 + 1.0, var15 + var9);
         case DOWN -> new Vec3d(var11 + var7, var13, var15 + var9);
         case NORTH -> new Vec3d(var11 + var7, var13 + var9, var15);
         case SOUTH -> new Vec3d(var11 + var7, var13 + var9, var15 + 1.0);
         case WEST -> new Vec3d(var11, var13 + var9, var15 + var7);
         case EAST -> new Vec3d(var11 + 1.0, var13 + var9, var15 + var7);
      };
   }

   private void handleNow(long now) {
      if (now >= this.timeMs4) {
         float var3 = Math.max(0.001F, GCDUtil.getGCDValue());
         int var4 = this.random.nextDouble() < 0.7 ? 1 : 2;
         int var5 = this.random.nextDouble() < 0.55 ? 0 : 1;
         this.fvalue5 = var4 * var3 * (this.random.nextBoolean() ? 1.0F : -1.0F);
         this.fvalue6 = var5 * var3 * (this.random.nextBoolean() ? 1.0F : -1.0F);
         this.timeMs4 = now + this.calcMinMillis(90L, 260L);
      }
   }

   private void handleYaw(float yaw, float pitch) {
      RotationStorage.update(new Rotation(MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -89.0F, 89.0F)), 360.0F, 360.0F, 28.0F, 20.0F, 2, 1, true);
   }

   private double calcPos(BlockPos pos) {
      return mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos));
   }

   private float calcYawA(float yawA, float pitchA, float yawB, float pitchB) {
      float var5 = MathHelper.wrapDegrees(yawB - yawA);
      float var6 = pitchB - pitchA;
      return (float)Math.sqrt(var5 * var5 + var6 * var6);
   }

   private double calcValue(double value) {
      return MathHelper.clamp(value, 0.16, 0.84);
   }

   private double calcValue2(double t) {
      double var3 = t * t * t;
      return 10.0 * var3 - 15.0 * var3 * t + 6.0 * var3 * t * t;
   }

   private long calcMinMillis(long minMillis, long maxMillis) {
      long var5 = minMillis + (long)(this.random.nextDouble() * (maxMillis - minMillis));
      return var5 * 1000000L;
   }
}
