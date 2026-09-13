package pulse.cosmetic;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import ru.pulse.cosmetic.render.CosmeticRenderer;

public class CosmeticFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   public CosmeticFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
      super(context);
   }

   @Override
   public void render(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      PlayerEntityRenderState state,
      float limbAngle,
      float limbDistance
   ) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world == null || state == null || state.id < 0) {
         return;
      }

      if (!(client.world.getEntityById(state.id) instanceof AbstractClientPlayerEntity player) || player.isSpectator()) {
         return;
      }

      if (player != client.player) {
         return;
      }

      List<Integer> selected = LocalCosmetics.selectedIndices();
      if (selected.isEmpty()) {
         return;
      }

      for (Integer selectedIndex : selected) {
         if (selectedIndex == null) {
            continue;
         }

         if ("cape".equals(LocalCosmetics.type(selectedIndex))) {
            this.renderCape(matrices, vertexConsumers, LocalCosmetics.texture(selectedIndex), light);
            continue;
         }

         ru.pulse.cosmetic.model.CosmeticModel model = LocalCosmetics.modelFor(selectedIndex);
         if (model == null || model.getTextureId() == null) {
            continue;
         }

         RenderLayer layer = RenderLayer.getEntityCutoutNoCull(model.getTextureId());
         VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);
         CosmeticRenderer.getInstance()
            .renderCosmetic(model, player, matrices, vertexConsumer, light, this.getContextModel(), limbDistance);
      }
   }

   private void renderCape(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, int light) {
      matrices.push();
      matrices.translate(0.0F, 0.0F, 0.13F);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(8.0F));
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
      MatrixStack.Entry entry = matrices.peek();
      Matrix4f position = entry.getPositionMatrix();
      float halfWidth = 0.3125F;
      float length = 1.0F;
      float u0 = 0.0F;
      float v0 = 0.0F;
      float u1 = 10.0F / 64.0F;
      float v1 = 16.0F / 32.0F;
      this.quad(vertexConsumer, position, entry, halfWidth, length, u0, v0, u1, v1, light);
      matrices.pop();
   }

   private void quad(
      VertexConsumer vertexConsumer,
      Matrix4f position,
      MatrixStack.Entry entry,
      float halfWidth,
      float length,
      float u0,
      float v0,
      float u1,
      float v1,
      int light
   ) {
      vertexConsumer.vertex(position, -halfWidth, 0.0F, 0.0F).color(255, 255, 255, 255).texture(u0, v0)
         .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, 0.0F, 1.0F);
      vertexConsumer.vertex(position, halfWidth, 0.0F, 0.0F).color(255, 255, 255, 255).texture(u1, v0)
         .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, 0.0F, 1.0F);
      vertexConsumer.vertex(position, halfWidth, length, 0.0F).color(255, 255, 255, 255).texture(u1, v1)
         .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, 0.0F, 1.0F);
      vertexConsumer.vertex(position, -halfWidth, length, 0.0F).color(255, 255, 255, 255).texture(u0, v1)
         .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, 0.0F, 1.0F);
   }
}
