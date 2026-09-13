package dlc.lumen.mixin;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.render.chams.ChamsRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin implements QClient {
   @Inject(
      method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void lumen$hideArmor(
      MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderState state, float limbAngle, float limbDistance, CallbackInfo ci
   ) {
      if (state instanceof PlayerEntityRenderState playerState && ModuleClass.INSTANCE != null && mc.world != null) {
         ChamsRenderer chams = ChamsRenderer.getInstance();
         if (chams.isActive()) {
            if (mc.world.getEntityById(playerState.id) instanceof PlayerEntity player && chams.shouldHideItemsAndCape(player)) {
               ci.cancel();
            }
         }
      }
   }
}
