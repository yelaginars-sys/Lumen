package dlc.lumen.mixin;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityCollisionPredictionInvoker {
   @Invoker("findCollisionsForMovement")
   static List<VoxelShape> invokeFindCollisionsForMovement(Entity entity, World world, List<VoxelShape> reusable, Box box) {
      throw new AssertionError();
   }

   @Invoker("adjustMovementForCollisions")
   static Vec3d invokeAdjustMovementForCollisions(Entity entity, Vec3d movement, Box boundingBox, World world, List<VoxelShape> collisions) {
      throw new AssertionError();
   }
}
