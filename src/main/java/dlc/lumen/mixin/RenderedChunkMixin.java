package dlc.lumen.mixin;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.render.chunk.RenderedChunk")
public class RenderedChunkMixin {
   @Redirect(
      method = "<init>",
      at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;")
   )
   private ImmutableMap<BlockPos, BlockEntity> lumen$safeCopyBlockEntities(Map<BlockPos, BlockEntity> blockEntities) {
      if (blockEntities.isEmpty()) {
         return ImmutableMap.of();
      }

      Map<BlockPos, BlockEntity> normalized = new LinkedHashMap<>(blockEntities.size());

      for (Entry<BlockPos, BlockEntity> entry : blockEntities.entrySet()) {
         BlockPos key = entry.getKey();
         if (key.getClass() != BlockPos.class) {
            key = new BlockPos(key.getX(), key.getY(), key.getZ());
         }

         normalized.put(key, entry.getValue());
      }

      return ImmutableMap.copyOf(normalized);
   }
}
