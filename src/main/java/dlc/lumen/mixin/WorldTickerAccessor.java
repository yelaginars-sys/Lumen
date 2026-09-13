package dlc.lumen.mixin;

import java.util.List;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldTickerAccessor {
   @Accessor("blockEntityTickers")
   List<BlockEntityTickInvoker> lumen$getBlockEntityTickers();
}
