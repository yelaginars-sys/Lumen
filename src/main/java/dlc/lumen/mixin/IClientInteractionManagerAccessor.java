package dlc.lumen.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerInteractionManager.class)
public interface IClientInteractionManagerAccessor {
   @Accessor("lastSelectedSlot")
   void lumen$setLastSelectedSlot(int var1);

   @Accessor("blockBreakingCooldown")
   void lumen$setBlockBreakingCooldown(int var1);

   @Accessor("blockBreakingCooldown")
   int lumen$getBlockBreakingCooldown();
}
