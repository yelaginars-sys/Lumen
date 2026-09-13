package dlc.lumen.client.render.models;

import dlc.lumen.mixin.LivingEntityRendererAccessor;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public final class CustomPlayerModelRenderer {
   private static final PlayerEntityModel AMOGUS_PLAYER_MODEL = new AmogusPlayerModel();
   private static final PlayerEntityModel RABBIT_PLAYER_MODEL = new RabbitPlayerModel();
   private static final PlayerEntityModel DEMON_PLAYER_MODEL = new DemonPlayerModel();
   private static final PlayerEntityModel FREDDY_PLAYER_MODEL = new FreddyPlayerModel();

   private CustomPlayerModelRenderer() {
   }

   public static void render(
      LivingEntityRendererAccessor accessor,
      PlayerEntity player,
      PlayerEntityRenderState state,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light
   ) {
      CustomModelType var6 = CustomModelManager.resolveActiveModel(player);
      if (var6.isCustom()) {
         if (var6.usesGltfModel()) {
            matrices.push();
            float var10 = state.baseScale;
            accessor.lumen$setupTransforms(state, matrices, state.bodyYaw, var10);
            accessor.lumen$scale(state, matrices);
            matrices.scale(var6.renderScaleX(), var6.renderScaleY(), var6.renderScaleZ());
            matrices.translate(0.0F, var6.renderOffsetY(), 0.0F);
            GltfPlayerModelRenderer.render(var6, state, matrices, vertexConsumers, light);
            matrices.pop();
         } else if (var6.usesStaticTextureModel()) {
            PlayerEntityModel var7 = switch (var6) {
               case AMOGUS -> AMOGUS_PLAYER_MODEL;
               case RABBIT -> RABBIT_PLAYER_MODEL;
               case WHITE_DEMON, RED_DEMON -> DEMON_PLAYER_MODEL;
               case FREDDY -> FREDDY_PLAYER_MODEL;
               default -> null;
            };
            if (var7 != null) {
               var7.setAngles(state);
               matrices.push();
               float var8 = state.baseScale;
               accessor.lumen$setupTransforms(state, matrices, state.bodyYaw, var8);
               matrices.scale(-1.0F, -1.0F, 1.0F);
               accessor.lumen$scale(state, matrices);
               matrices.translate(0.0F, -1.501F, 0.0F);
               matrices.scale(var6.renderScaleX(), var6.renderScaleY(), var6.renderScaleZ());
               matrices.translate(0.0F, var6.renderOffsetY(), 0.0F);
               VertexConsumer var9 = vertexConsumers.getBuffer(var7.getLayer(var6.texture()));
               var7.render(matrices, var9, light, OverlayTexture.DEFAULT_UV, -1);
               matrices.pop();
            }
         }
      }
   }
}
