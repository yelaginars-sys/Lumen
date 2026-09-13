package dlc.lumen.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
   @ModifyVariable(
      method = "getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private BlockPos lumen$normalizeBlockEntityPos(BlockPos pos) {
      return pos != null && pos.getClass() != BlockPos.class && !(pos instanceof Mutable) ? new BlockPos(pos.getX(), pos.getY(), pos.getZ()) : pos;
   }
}
