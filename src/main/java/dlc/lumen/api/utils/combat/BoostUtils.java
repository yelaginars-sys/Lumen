package dlc.lumen.api.utils.combat;

import dlc.lumen.api.QClient;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public final class BoostUtils implements QClient {
   public static double getBoost() {
      int[] var1 = new int[]{-45, 45, 135, -135};
      int[] var2 = new int[]{-90, 90, 180, -180, 0};
      int[] var3 = new int[]{-45, 45};
      float var4 = mc.player.prevYaw;
      float var5 = mc.player.prevPitch;
      int var6 = calcLastYaw(var4, var1);
      float var7 = Math.abs(MathHelper.wrapDegrees(var4) - var1[var6]);
      int var8 = calcLastYaw(var4, var2);
      float var9 = Math.abs(MathHelper.wrapDegrees(var4) - var2[var8]);
      float var0 = var6 == -1 ? 1.5F : 1.95F - var7 * 0.56F / 45.0F;
      if (var9 < 10.0F) {
         var0 += 0.1F - 0.1F * var9 / 10.0F;
      }

      int var10 = calcLastYaw(var5, var3);
      float var11 = Math.abs(Math.abs(var5) - Math.abs(var3[var10]));
      if (var11 < 26.0F) {
         var0 = Math.max(1.94F, var0);
         var0 += 0.05F - var11 * 0.05F / 26.0F;
      }

      var0 = Math.min(2.045F, var0);
      if (mc.player.prevPitch > -55.0F && mc.player.prevPitch < -19.0F) {
         var0 = 1.91F;
      } else if (mc.player.prevPitch < -55.0F) {
         var0 = 1.54F;
      }

      if (mc.player.prevPitch > 19.0F && mc.player.prevPitch < 55.0F) {
         var0 = 1.8F;
      } else if (mc.player.prevPitch > 55.0F) {
         var0 = 1.54F;
      }

      return var0;
   }

   private static int calcLastYaw(float lastYaw, int[] vectors) {
      int var2 = 0;
      int var3 = -1;
      float var4 = Float.MAX_VALUE;

      for (int var8 : vectors) {
         float var9 = Math.abs(MathHelper.wrapDegrees(lastYaw) - var8);
         if (var9 < var4) {
            var4 = var9;
            var3 = var2;
         }

         var2++;
      }

      return var3;
   }

   @Generated
   private BoostUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
