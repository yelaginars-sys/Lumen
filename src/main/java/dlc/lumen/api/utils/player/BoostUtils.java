package dlc.lumen.api.utils.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class BoostUtils {
   private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
   private static final float fvalue = 1.61F;
   private static final float fvalue2 = 1.5F;
   private static final float[] fvalueArray = new float[]{
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.62F,
      1.62F,
      1.62F,
      1.63F,
      1.63F,
      1.64F,
      1.65F,
      1.65F,
      1.66F,
      1.67F,
      1.68F,
      1.69F,
      1.7F,
      1.71F,
      1.72F,
      1.73F,
      1.73F,
      1.75F,
      1.76F,
      1.78F,
      1.79F,
      1.81F,
      1.83F,
      1.85F,
      1.87F,
      1.89F,
      1.91F,
      1.93F,
      1.95F,
      1.98F,
      2.01F,
      2.03F,
      2.06F,
      2.09F,
      2.12F,
      2.16F,
      2.19F,
      2.23F,
      2.27F,
      2.31F,
      2.35F,
      2.31F,
      2.27F,
      2.23F,
      2.19F,
      2.16F,
      2.12F,
      2.09F,
      2.06F,
      2.03F,
      2.01F,
      1.98F,
      1.95F,
      1.93F,
      1.89F,
      1.87F,
      1.85F,
      1.83F,
      1.81F,
      1.79F,
      1.78F,
      1.76F,
      1.75F,
      1.73F,
      1.72F,
      1.71F,
      1.7F,
      1.69F,
      1.68F,
      1.67F,
      1.66F,
      1.65F,
      1.64F,
      1.63F,
      1.63F,
      1.63F,
      1.62F,
      1.62F,
      1.62F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F
   };
   private static final float[] fvalueArray2 = new float[]{
      1.61F,
      1.61F,
      1.61F,
      1.62F,
      1.62F,
      1.62F,
      1.63F,
      1.63F,
      1.64F,
      1.65F,
      1.65F,
      1.66F,
      1.67F,
      1.68F,
      1.69F,
      1.7F,
      1.71F,
      1.72F,
      1.73F,
      1.73F,
      1.75F,
      1.76F,
      1.78F,
      1.79F,
      1.81F,
      1.83F,
      1.85F,
      1.87F,
      1.89F,
      1.91F,
      1.93F,
      1.95F,
      1.98F,
      2.01F,
      2.03F,
      2.06F,
      2.09F,
      2.12F,
      2.16F,
      2.19F,
      2.23F,
      2.24F,
      2.21F,
      2.21F,
      2.21F,
      2.23F,
      2.23F,
      2.19F,
      2.16F,
      2.12F,
      2.09F,
      2.06F,
      2.03F,
      2.01F,
      1.98F,
      1.95F,
      1.93F,
      1.89F,
      1.87F,
      1.85F,
      1.83F,
      1.81F,
      1.79F,
      1.78F,
      1.76F,
      1.75F,
      1.73F,
      1.72F,
      1.71F,
      1.7F,
      1.69F,
      1.68F,
      1.67F,
      1.66F,
      1.65F,
      1.64F,
      1.63F,
      1.63F,
      1.63F,
      1.62F,
      1.62F,
      1.62F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F
   };

   public static Vec3d getBoost(LivingEntity entity) {
      float var1 = calcEntity(entity);
      Vec3d var2 = entity.getRotationVector();
      Vec3d var3 = Vec3d.fromPolar(entity.getPitch(), entity.getYaw()).multiply(var1);
      float var4 = entity.getPitch() * (float) (Math.PI / 180.0);
      double var5 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
      double var7 = var3.horizontalLength();
      boolean var9 = entity.getVelocity().y <= 0.0;
      double var10 = var9 && entity.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(entity.getFinalGravity(), 0.01) : entity.getFinalGravity();
      double var12 = MathHelper.square(Math.cos(var4));
      var3 = var3.add(0.0, var10 * (-1.0 + var12 * 0.75), 0.0);
      if (var3.y < 0.0 && var5 > 0.0) {
         double var14 = var3.y * -0.1 * var12;
         var3 = var3.add(var2.x * var14 / var5, var14, var2.z * var14 / var5);
      }

      if (var4 < 0.0F && var5 > 0.0) {
         double var19 = var7 * -MathHelper.sin(var4) * 0.04;
         var3 = var3.add(-var2.x * var19 / var5, var19 * 3.2, -var2.z * var19 / var5);
      }

      if (var5 > 0.0) {
         var3 = var3.add((var2.x / var5 * var7 - var3.x) * 0.1, 0.0, (var2.z / var5 * var7 - var3.z) * 0.1);
      }

      double var16 = var3.length();
      return new Vec3d(var16, var16, var16).multiply(0.99, 0.98, 0.99);
   }

   private static float calcEntity(LivingEntity entity) {
      float var1 = Math.abs(MathHelper.wrapDegrees(entity.getYaw()));
      float var2 = calcYawAbs(var1);
      float var3 = Math.abs(calcPitch(entity.getPitch()));
      if (var3 >= 70.0F && var3 <= 90.0F) {
         return 1.615F;
      }

      float var4 = fvalueArray[Math.min((int)Math.ceil(var2), 90)];
      int var5 = Math.min((int)Math.ceil(var3), fvalueArray2.length - 1);
      float var6 = fvalueArray2[var5];
      float var7 = var3 >= 75.0F ? var6 : Math.max(var4, var6);
      return Math.max(var7, var3 >= 75.0F ? 1.5F : 1.61F);
   }

   private static float calcYawAbs(float yawAbs) {
      float var1 = yawAbs > 180.0F ? 360.0F - yawAbs : yawAbs;
      return var1 > 90.0F ? 180.0F - var1 : var1;
   }

   private static float calcPitch(float pitch) {
      return Math.max(-90.0F, Math.min(90.0F, pitch));
   }

   public static Vec3d getBoostAntiTarget(LivingEntity entity, float speedSetting) {
      float var2 = Math.abs((entity.getYaw() - 360.0F) % 360.0F);
      float var3 = entity.getPitch();
      float var4 = Math.abs(var3);
      float var5 = speedSetting;
      float var6 = 0.0F;
      if (var4 >= 30.0F && var4 <= 50.0F) {
         var6 = 0.15F;
      } else if (var4 >= 25.0F && var4 <= 55.0F) {
         var6 = 0.1F;
      } else if (var4 >= 20.0F && var4 <= 60.0F) {
         var6 = 0.05F;
      }

      float var7 = var5 + var6;
      float[] var8 = new float[]{45.0F, 135.0F, 225.0F, 315.0F};
      float var9 = 9999.0F;

      for (float var13 : var8) {
         float var14 = Math.abs(var2 - var13);
         if (var14 < var9) {
            var9 = var14;
         }
      }

      if (var9 < 15.0F) {
         var7 += 0.1F;
      } else if (var9 < 25.0F) {
         var7 += 0.05F;
      }

      var7 = Math.min(var7, 2.8F);
      return new Vec3d(var7, var7, var7);
   }

   public static Vec3d getBoostAntiTargetFast(LivingEntity entity) {
      float var1 = Math.abs((entity.getYaw() - 360.0F) % 360.0F);
      float var2 = entity.getPitch();
      float var3 = Math.abs(var2);
      float var4 = 2.5F;
      float var5 = 2.3F;
      if (var3 >= 35.0F && var3 <= 50.0F) {
         var4 = 2.7F;
         var5 = 2.5F;
      } else if (var3 >= 30.0F && var3 <= 55.0F) {
         var4 = 2.6F;
         var5 = 2.4F;
      }

      float[] var6 = new float[]{45.0F, 135.0F, 225.0F, 315.0F};
      float var7 = 9999.0F;

      for (float var11 : var6) {
         float var12 = Math.abs(var1 - var11);
         if (var12 < var7) {
            var7 = var12;
         }
      }

      if (var7 < 20.0F) {
         var4 += 0.15F;
      }

      return new Vec3d(var4, var5, var4);
   }

   public static Vec3d getBoostAntiTargetWithAura(LivingEntity entity, float auraRotatePitch, float auraRotateYaw, float speedSetting) {
      float var4 = Math.abs(auraRotatePitch);
      float var5 = speedSetting;
      float var6;
      if (var4 >= 38.0F && var4 <= 52.0F) {
         var5 = Math.min(speedSetting + 0.2F, 2.7F);
         var6 = Math.min(speedSetting + 0.15F, 2.5F);
      } else if (var4 >= 30.0F && var4 <= 60.0F) {
         var5 = Math.min(speedSetting + 0.1F, 2.6F);
         var6 = Math.min(speedSetting + 0.1F, 2.4F);
      } else if (var4 >= 25.0F && var4 <= 65.0F) {
         var6 = speedSetting - 0.05F;
      } else {
         var5 = speedSetting - 0.1F;
         var6 = speedSetting - 0.15F;
      }

      return new Vec3d(var5, var6, var5);
   }

   public static Vec3d getBoostslime(LivingEntity entity) {
      return getBoostCustom(entity, 42.0F);
   }

   public static Vec3d getBoostbravo(LivingEntity entity) {
      return getBoostCustom(entity, 39.0F);
   }

   public static Vec3d getBoostrw(LivingEntity entity) {
      return getBoostCustom(entity, 33.2F);
   }

   public static Vec3d getBoostCustom(LivingEntity entity, float targetBps) {
      float var2 = targetBps / 20.0F;
      float var3 = Math.abs((entity.getYaw() - 360.0F) % 360.0F);
      float var4 = entity.getPitch();
      float var5 = Math.min(var2 * 0.7F, 1.67F);
      float[] var6 = new float[]{45.0F, 135.0F, 225.0F, 315.0F};
      float var7 = 9999.0F;

      for (float var11 : var6) {
         float var12 = Math.abs(var3 - var11);
         if (var12 < var7) {
            var7 = var12;
         }
      }

      float var28 = 1.0F - var7 / 45.0F;
      var28 = Math.max(0.0F, Math.min(1.0F, var28));
      float var30 = calcPitch2(var4);
      float var31 = var28 * var30;
      float var32 = var5 + (var2 - var5) * var31;
      Vec3d var33 = entity.getRotationVector();
      Vec3d var13 = Vec3d.fromPolar(var4, entity.getYaw()).multiply(var32);
      float var14 = var4 * (float) (Math.PI / 180.0);
      double var15 = Math.sqrt(var33.x * var33.x + var33.z * var33.z);
      double var17 = var13.horizontalLength();
      boolean var19 = entity.getVelocity().y <= 0.0;
      double var20 = var19 && entity.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(entity.getFinalGravity(), 0.01) : entity.getFinalGravity();
      double var22 = MathHelper.square(Math.cos(var14));
      var13 = var13.add(0.0, var20 * (-1.0 + var22 * 0.75), 0.0);
      if (var13.y < 0.0 && var15 > 0.0) {
         double var24 = var13.y * -0.1 * var22;
         var13 = var13.add(var33.x * var24 / var15, var24, var33.z * var24 / var15);
      }

      if (var14 < 0.0F && var15 > 0.0) {
         double var35 = var17 * -MathHelper.sin(var14) * 0.04;
         var13 = var13.add(-var33.x * var35 / var15, var35 * 3.2, -var33.z * var35 / var15);
      }

      if (var15 > 0.0) {
         var13 = var13.add((var33.x / var15 * var17 - var13.x) * 0.1, 0.0, (var33.z / var15 * var17 - var13.z) * 0.1);
      }

      double var26 = var13.length();
      return new Vec3d(var26, var26, var26).multiply(0.99, 0.98, 0.99);
   }

   public static Vec3d getBoostFixedBps(LivingEntity entity, float targetBps) {
      float var2 = targetBps / 20.0F;
      return new Vec3d(var2, var2, var2).multiply(0.99, 0.98, 0.99);
   }

   private static float calcPitch2(float pitch) {
      float var1 = Math.abs(pitch);
      if (var1 <= 5.0F) {
         return 1.0F;
      } else if (var1 <= 15.0F) {
         return 0.95F;
      } else if (var1 <= 25.0F) {
         return 0.85F;
      } else if (var1 <= 35.0F) {
         return 0.75F;
      } else if (var1 <= 45.0F) {
         return 0.65F;
      } else if (var1 <= 55.0F) {
         return 0.55F;
      } else if (var1 <= 65.0F) {
         return 0.45F;
      } else {
         return var1 <= 75.0F ? 0.35F : 0.25F;
      }
   }

   private BoostUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
