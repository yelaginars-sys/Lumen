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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import ru.pulse.cosmetic.render.CosmeticRenderer;

public class CosmeticFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   private static final float halfWidth = 0.3125F;
   private static double lastX = Double.NaN;
   private static double lastY = Double.NaN;
   private static double lastZ = Double.NaN;
   private static long lastNano = 0L;
   private static double smoothVX;
   private static double smoothVY;
   private static double smoothVZ;

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
            if (!player.isGliding()) {
               this.renderCape(matrices, vertexConsumers, player, LocalCosmetics.texture(selectedIndex), light);
            }

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

   private void renderCape(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      AbstractClientPlayerEntity player,
      Identifier texture,
      int light
   ) {
      double[] velocity = this.trackVelocity(player);
      float vx = (float)velocity[0];
      float vy = (float)velocity[1];
      float vz = (float)velocity[2];
      float horizontal = MathHelper.sqrt(vx * vx + vz * vz);
      float yawRad = player.getYaw() * (float)(Math.PI / 180.0);
      float sideSlip = vx * MathHelper.cos(yawRad) - vz * MathHelper.sin(yawRad);

      float runLift = MathHelper.clamp(horizontal * 0.55F, 0.0F, 1.1F);
      float fallFlare = MathHelper.clamp(-vy * 0.35F, -0.4F, 1.3F);
      float walkSway = MathHelper.sin(player.age * 0.6F) * horizontal * 1.5F;
      float rotX = 0.06F + runLift + fallFlare + walkSway;
      rotX = MathHelper.clamp(rotX, -0.35F, 1.5F);
      float rotZ = MathHelper.clamp(sideSlip * -1.2F, -0.25F, 0.25F);

      matrices.push();
      matrices.translate(0.0F, 0.0F, 0.13F);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX * 57.29578F));
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ * 57.29578F));
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
      MatrixStack.Entry entry = matrices.peek();
      this.quad(vertexConsumer, entry, halfWidth, 0.625F, 0.0F, 0.0F, 10.0F / 64.0F, 10.0F / 32.0F, light);
      matrices.push();
      matrices.translate(0.0F, 0.625F, 0.0F);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX * 28.0F));
      entry = matrices.peek();
      this.quad(vertexConsumer, entry, halfWidth, 0.375F, 0.0F, 10.0F / 32.0F, 10.0F / 64.0F, 16.0F / 32.0F, light);
      matrices.pop();
      matrices.pop();
   }

   private double[] trackVelocity(AbstractClientPlayerEntity player) {
      long now = System.nanoTime();
      if (Double.isNaN(lastX)) {
         lastX = player.getX();
         lastY = player.getY();
         lastZ = player.getZ();
         lastNano = now;
         return new double[]{0.0, 0.0, 0.0};
      }

      double dt = (now - lastNano) / 1.0E9;
      if (dt < 1.0E-4) {
         return new double[]{smoothVX, smoothVY, smoothVZ};
      }

      double dx = player.getX() - lastX;
      double dy = player.getY() - lastY;
      double dz = player.getZ() - lastZ;
      lastX = player.getX();
      lastY = player.getY();
      lastZ = player.getZ();
      lastNano = now;
      if (Math.abs(dx) > 1.0 || Math.abs(dy) > 1.0 || Math.abs(dz) > 1.0) {
         return new double[]{0.0, 0.0, 0.0};
      }

      double blend = MathHelper.clamp(dt * 12.0, 0.0, 1.0);
      smoothVX += (dx / dt - smoothVX) * blend;
      smoothVY += (dy / dt - smoothVY) * blend;
      smoothVZ += (dz / dt - smoothVZ) * blend;
      return new double[]{smoothVX, smoothVY, smoothVZ};
   }

   private void quad(
      VertexConsumer vertexConsumer,
      MatrixStack.Entry entry,
      float halfWidth,
      float length,
      float u0,
      float v0,
      float u1,
      float v1,
      int light
   ) {
      Matrix4f position = entry.getPositionMatrix();
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
