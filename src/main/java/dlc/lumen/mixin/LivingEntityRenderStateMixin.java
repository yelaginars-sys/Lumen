package dlc.lumen.mixin;

import dlc.lumen.client.modules.impl.render.SeeInvisiblesRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements SeeInvisiblesRenderState {
   @Unique
   private boolean lumen$seeInvisiblesTarget;

   @Override
   public boolean lumen$isSeeInvisiblesTarget() {
      return this.lumen$seeInvisiblesTarget;
   }

   @Override
   public void lumen$setSeeInvisiblesTarget(boolean value) {
      this.lumen$seeInvisiblesTarget = value;
   }
}
