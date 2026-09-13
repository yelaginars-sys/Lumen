package dlc.lumen.api.utils.player;

import dlc.lumen.api.utils.input.MovingUtil;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MoveUtils {
   private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

   public static void setMotion(double motion) {
      if (minecraftClient.player != null) {
         double var2 = minecraftClient.player.input.movementForward;
         double var4 = minecraftClient.player.input.movementSideways;
         float var6 = minecraftClient.player.getYaw();
         if (var2 == 0.0 && var4 == 0.0) {
            minecraftClient.player.setVelocity(0.0, minecraftClient.player.getVelocity().y, 0.0);
         } else {
            if (var2 != 0.0) {
               if (var4 > 0.0) {
                  var6 += var2 > 0.0 ? -45 : 45;
               } else if (var4 < 0.0) {
                  var6 += var2 > 0.0 ? 45 : -45;
               }

               var4 = 0.0;
               if (var2 > 0.0) {
                  var2 = 1.0;
               } else if (var2 < 0.0) {
                  var2 = -1.0;
               }
            }

            double var7 = var2 * motion * MathHelper.cos((float)Math.toRadians(var6 + 90.0F))
               + var4 * motion * MathHelper.sin((float)Math.toRadians(var6 + 90.0F));
            double var9 = var2 * motion * MathHelper.sin((float)Math.toRadians(var6 + 90.0F))
               - var4 * motion * MathHelper.cos((float)Math.toRadians(var6 + 90.0F));
            minecraftClient.player.setVelocity(var7, minecraftClient.player.getVelocity().y, var9);
         }
      }
   }

   public static double getSpeed() {
      if (minecraftClient.player == null) {
         return 0.0;
      }

      Vec3d var0 = minecraftClient.player.getVelocity();
      return Math.sqrt(var0.x * var0.x + var0.z * var0.z);
   }

   public static void setVelocity(double velocity) {
      double[] var2 = MovingUtil.calculateDirection(velocity);
      Objects.requireNonNull(minecraftClient.player).setVelocity(var2[0], minecraftClient.player.getVelocity().getY(), var2[1]);
   }

   public static void setVelocity(double velocity, double y) {
      double[] var4 = MovingUtil.calculateDirection(velocity);
      Objects.requireNonNull(minecraftClient.player).setVelocity(var4[0], y, var4[1]);
   }

   public static void strafe() {
      strafe(getSpeed());
   }

   public static void strafe(double speed) {
      if (minecraftClient.player != null) {
         float var2 = minecraftClient.player.getYaw();
         double var3 = minecraftClient.player.input.movementForward;
         double var5 = minecraftClient.player.input.movementSideways;
         if (var3 == 0.0 && var5 == 0.0) {
            minecraftClient.player.setVelocity(0.0, minecraftClient.player.getVelocity().y, 0.0);
         } else {
            if (var3 != 0.0) {
               if (var5 > 0.0) {
                  var2 += var3 > 0.0 ? -45.0F : 45.0F;
               } else if (var5 < 0.0) {
                  var2 += var3 > 0.0 ? 45.0F : -45.0F;
               }

               var5 = 0.0;
               var3 = var3 > 0.0 ? 1.0 : -1.0;
            }

            double var7 = Math.toRadians(var2 + 90.0F);
            double var9 = var3 * speed * Math.cos(var7) + var5 * speed * Math.sin(var7);
            double var11 = var3 * speed * Math.sin(var7) - var5 * speed * Math.cos(var7);
            minecraftClient.player.setVelocity(var9, minecraftClient.player.getVelocity().y, var11);
         }
      }
   }

   public static boolean isMoving() {
      return minecraftClient.player == null
         ? false
         : minecraftClient.player.input.movementForward != 0.0F || minecraftClient.player.input.movementSideways != 0.0F;
   }
}
