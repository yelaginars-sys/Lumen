package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.FreeLookStorage;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.MultipointUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.interpolation.BestPoint;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class HelixWaveRotation extends RotationsSystem implements QClient {
   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null || target == null) {
         return;
      }

      Vec3d point = BestPoint.getNearestPoint(target);
      if (point == null) {
         point = MultipointUtils.getClosestPoint(target);
      }

      if (point == null) {
         point = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0);
      }

      float currentYaw = FreeLookStorage.isActive() ? FreeLookStorage.getFreeYaw() : mc.player.getYaw();
      float currentPitch = FreeLookStorage.isActive()
         ? MathHelper.clamp(FreeLookStorage.getFreePitch(), -89.0F, 89.0F)
         : MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F);
      Vec2f aim = RotationUtils.getRotations(point);
      float targetYaw = MathHelper.wrapDegrees(aim.x);
      float targetPitch = MathHelper.clamp(aim.y, -89.0F, 89.0F);
      float diffYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
      float diffPitch = targetPitch - currentPitch;
      float baseYaw = currentYaw + diffYaw;
      float basePitch = currentPitch + diffPitch;
      double now = System.currentTimeMillis() / 1000.0;
      float helixYaw = (float)(Math.sin(now * 18.0) * 14.0 + Math.cos(now * 9.0) * 6.0);
      float helixPitch = (float)(Math.cos(now * 18.0) * 10.0 + Math.sin(now * 9.0) * 5.0);
      float yaw = MathHelper.wrapDegrees(baseYaw + helixYaw);
      float pitch = MathHelper.clamp(basePitch + helixPitch, -89.9F, 89.9F);
      RotationStorage.update(new Rotation(yaw, pitch), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
   }
}
