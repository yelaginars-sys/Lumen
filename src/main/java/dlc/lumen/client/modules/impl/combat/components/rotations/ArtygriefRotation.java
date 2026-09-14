package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.FreeLookStorage;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.math.MathUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ArtygriefRotation extends RotationsSystem implements QClient {
   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player == null || target == null) {
         return;
      }

      Box box = target.getBoundingBox();
      Vec3d chest = new Vec3d(box.getCenter().x, MathHelper.lerp(0.55, box.minY, box.maxY), box.getCenter().z);
      Vec2f aim = RotationUtils.getRotations(chest);
      float chestYaw = MathHelper.wrapDegrees(aim.x);
      float chestPitch = MathHelper.clamp(aim.y, -90.0F, 90.0F);
      float currentYaw = FreeLookStorage.isActive() ? FreeLookStorage.getFreeYaw() : mc.player.getYaw();
      float currentPitch = FreeLookStorage.isActive()
         ? MathHelper.clamp(FreeLookStorage.getFreePitch(), -90.0F, 90.0F)
         : MathHelper.clamp(mc.player.getPitch(), -90.0F, 90.0F);
      float speed = MathUtils.random(0.35F, 0.6F);
      float finalYaw = currentYaw + MathHelper.wrapDegrees(chestYaw - currentYaw) * speed;
      float finalPitch = currentPitch + (chestPitch - currentPitch) * speed;
      float[] fixed = RotationUtils.correctRotation(new float[]{finalYaw, MathHelper.clamp(finalPitch, -90.0F, 90.0F)});
      RotationStorage.update(new Rotation(fixed[0], fixed[1]), 180.0F, 180.0F, 180.0F, 180.0F, 1, 1, false);
   }
}
