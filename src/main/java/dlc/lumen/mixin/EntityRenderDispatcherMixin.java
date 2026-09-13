package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.render.Removals;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
   @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
   private static void lumen$renderShadow(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      EntityRenderState renderState,
      float opacity,
      float tickDelta,
      WorldView world,
      float radius,
      CallbackInfo ci
   ) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Тени")) {
            ci.cancel();
         }
      }
   }
}
