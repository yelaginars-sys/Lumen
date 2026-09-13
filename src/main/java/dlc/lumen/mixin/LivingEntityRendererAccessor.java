package dlc.lumen.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {
   @Invoker("addFeature")
   boolean lumen$addFeature(FeatureRenderer<?, ?> var1);

   @Invoker("setupTransforms")
   void lumen$setupTransforms(LivingEntityRenderState var1, MatrixStack var2, float var3, float var4);

   @Invoker("scale")
   void lumen$scale(LivingEntityRenderState var1, MatrixStack var2);
}
