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

public class ReallyWorldRotation extends RotationsSystem implements QClient {
   private final Random random = new Random();
   private int counter;

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null) {
         return;
      }

      if (target == null) {
         this.counter = 0;
         return;
      }

      this.counter++;
      Vec3d var2 = this.computeVec3d(target);
      float var3 = this.computeYaw();
      float var4 = this.computePitch();
      Vec2f var5 = RotationUtils.getRotations(var2);
      float var6 = MathHelper.wrapDegrees(var5.x);
      float var7 = MathHelper.clamp(var5.y, -89.0F, 89.0F);
      float var8 = MathHelper.wrapDegrees(var6 - var3);
      float var9 = var7 - var4;
      float var10 = (float)Math.hypot(Math.abs(var8), Math.abs(var9));
      float var11 = MathHelper.clamp(var10 * 0.5F, 1.0F, 40.0F);
      float var12 = var10 > 0.0F ? var8 / var10 * var11 : 0.0F;
      float var13 = var10 > 0.0F ? var9 / var10 * var11 : 0.0F;
      boolean var14 = var10 > 15.0F;
      float var15 = var14 ? (float)(-6.0 * Math.cos((double)System.currentTimeMillis() / 90.0)) : 0.0F;
      float var16 = var14 ? (float)(6.0 * Math.sin((double)System.currentTimeMillis() / 90.0)) : 0.0F;
      float var17 = MathHelper.wrapDegrees(var3 + var12 + var15);
      float var18 = MathHelper.clamp(var4 + var13 + var16, -89.0F, 89.0F);
      if (this.counter % 50 == 0 && !var14) {
         var18 = MathHelper.clamp(var4 + (-89.0F - var4) * 0.55F, -89.0F, 89.0F);
      }

      RotationStorage.update(new Rotation(var17, var18), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
   }

   private Vec3d computeVec3d(LivingEntity target) {
      Vec3d var2 = BestPoint.getNearestPoint(target);
      if (var2 == null) {
         var2 = MultipointUtils.getClosestPoint(target);
      }

      if (var2 == null) {
         var2 = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0);
      }

      return var2;
   }

   private float computeYaw() {
      return FreeLookStorage.isActive() ? FreeLookStorage.getFreeYaw() : mc.player.getYaw();
   }

   private float computePitch() {
      return FreeLookStorage.isActive()
         ? MathHelper.clamp(FreeLookStorage.getFreePitch(), -89.0F, 89.0F)
         : MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F);
   }
}
