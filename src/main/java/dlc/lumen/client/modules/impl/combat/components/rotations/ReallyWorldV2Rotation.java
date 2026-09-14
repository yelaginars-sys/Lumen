package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ReallyWorldV2Rotation extends RotationsSystem implements QClient {
   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null || target == null) {
         return;
      }

      if (!mc.player.isGliding()) {
         Vec3d relativePos = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0).subtract(mc.player.getEyePos());
         float yaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90.0);
         float pitch = (float)(-Math.toDegrees(Math.atan2(relativePos.y, Math.hypot(relativePos.x, relativePos.z))));
         RotationStorage.update(new Rotation(yaw, pitch), 180.0F, 180.0F, 25.0F, 20.0F, 1, 1, false);
      } else {
         Vec3d interpolatedRotation = Vec3d.fromPolar(target.getLerpTargetPitch(), target.getLerpTargetYaw());
         Vec3d rotationVector = target.getRotationVector();
         Vec3d relativePos = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0).subtract(mc.player.getEyePos());
         Vec3d blendedDirection = interpolatedRotation.normalize().lerp(rotationVector, interpolatedRotation.length());
         float yaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90.0);
         float pitch = (float)(-Math.toDegrees(Math.atan2(relativePos.y, Math.hypot(relativePos.x, relativePos.z))));
         RotationStorage.update(new Rotation(yaw, pitch), 180.0F, 180.0F, 25.0F, 20.0F, 1, 1, false);
      }
   }
}
