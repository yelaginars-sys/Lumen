package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.math.MathUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ArtygriefRotation extends RotationsSystem implements QClient {
   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null || target == null) {
         return;
      }

      Box box = target.getBoundingBox();
      Vec3d chest = new Vec3d(box.getCenter().x, MathHelper.lerp(0.55, box.minY, box.maxY), box.getCenter().z);
      Vec3d eye = mc.player.getEyePos();
      Vec3d delta = chest.subtract(eye);
      float chestYaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0);
      float chestPitch = (float)(-Math.toDegrees(Math.atan2(delta.y, Math.hypot(delta.x, delta.z))));
      float speed = MathUtils.random(0.35F, 0.6F);
      float finalYaw = mc.player.getYaw() + MathHelper.wrapDegrees(chestYaw - mc.player.getYaw()) * speed;
      float finalPitch = mc.player.getPitch() + (chestPitch - mc.player.getPitch()) * speed;
      RotationStorage.update(new Rotation(finalYaw, MathHelper.clamp(finalPitch, -90.0F, 90.0F)), 180.0F, 180.0F, 180.0F, 180.0F, 1, 1, false);
   }
}
