package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.animation.Easings;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public class TargetSoulsV2 implements QClient {
   private static final Identifier CELESTIAL_GLOW_TEXTURE = Identifier.of("lumen", "textures/targetesp/bloom.png");

   private static final double CELESTIAL_ARC = Math.toRadians(60.0);
   private static final double CELESTIAL_VERTICAL_AMPLITUDE = 0.7;
   private static final double CELESTIAL_TWO_PI = Math.PI * 2.0;

   public static final int DEFAULT_COLOR_A = 0xFF5B9AFF; // (91, 154, 255)
   public static final int DEFAULT_COLOR_B = 0xFFC45CFF; // (196, 92, 255)
   private static final int RED_HURT_COLOR = 0xFFFF3C3C;

   private LivingEntity celestialHeldTarget = null;
   private float celestialFade = 0.0F;
   private float celestialLastTime = -1.0F;

   private int colorA = DEFAULT_COLOR_A;
   private int colorB = DEFAULT_COLOR_B;
   private boolean hurtColor = true;

   public void setColors(int colorA, int colorB) {
      this.colorA = colorA == 0 ? DEFAULT_COLOR_A : colorA;
      this.colorB = colorB == 0 ? DEFAULT_COLOR_B : colorB;
   }

   public void setHurtColor(boolean hurtColor) {
      this.hurtColor = hurtColor;
   }

   public void clear() {
      this.resetCelestialState();
   }

   public void resetCelestialState() {
      this.celestialHeldTarget = null;
      this.celestialFade = 0.0F;
      this.celestialLastTime = -1.0F;
   }

   private float updateCelestialFade(boolean hasTarget, float time) {
      float delta;
      if (this.celestialLastTime < 0.0F) {
         delta = 0.0F;
      } else {
         delta = MathHelper.clamp(time - this.celestialLastTime, 0.0F, 0.1F);
      }
      this.celestialLastTime = time;

      float direction = hasTarget ? 1.0F : -1.0F;
      this.celestialFade = MathHelper.clamp(this.celestialFade + direction * delta / 0.5F, 0.0F, 1.0F);
      return this.celestialFade;
   }

   private int celestialGradient(int segment, int totalSegments, double time, int alpha, float hurtPC) {
      int originalIndex = segment * totalSegments;
      double phase = time * 1.5 + originalIndex * 0.035;
      float mix = (float) (0.5 + 0.5 * Math.sin(phase));

      int cA = this.colorA;
      int cB = this.colorB;

      if (this.hurtColor && hurtPC > 0.001F) {
         cA = blendColor(cA, RED_HURT_COLOR, hurtPC);
         cB = blendColor(cB, RED_HURT_COLOR, hurtPC);
      }

      int redA = (cA >> 16) & 0xFF;
      int greenA = (cA >> 8) & 0xFF;
      int blueA = cA & 0xFF;

      int redB = (cB >> 16) & 0xFF;
      int greenB = (cB >> 8) & 0xFF;
      int blueB = cB & 0xFF;

      int red = (int) (redA + (redB - redA) * mix);
      int green = (int) (greenA + (greenB - greenA) * mix);
      int blue = (int) (blueA + (blueB - blueA) * mix);

      return (MathHelper.clamp(alpha, 0, 255) << 24) |
             (MathHelper.clamp(red, 0, 255) << 16) |
             (MathHelper.clamp(green, 0, 255) << 8) |
             MathHelper.clamp(blue, 0, 255);
   }

   private static int blendColor(int base, int overlay, float factor) {
      factor = MathHelper.clamp(factor, 0.0F, 1.0F);
      int r = MathHelper.lerp(factor, (base >> 16) & 0xFF, (overlay >> 16) & 0xFF);
      int g = MathHelper.lerp(factor, (base >> 8) & 0xFF, (overlay >> 8) & 0xFF);
      int b = MathHelper.lerp(factor, base & 0xFF, overlay & 0xFF);
      int a = MathHelper.lerp(factor, (base >> 24) & 0xFF, (overlay >> 24) & 0xFF);
      return (a << 24) | (r << 16) | (g << 8) | b;
   }

   public void renderCelestial(MatrixStack matrices, float tickDelta, LivingEntity renderTarget, float alphaProgress,
                               float speed, float sizeScale, int arms, int segments) {
      if (mc.player == null || mc.world == null) {
         return;
      }

      float appear = (float) Easings.SINE_IN_OUT.ease(MathHelper.clamp(alphaProgress, 0.0F, 1.0F));
      if (appear <= 0.001F && renderTarget == null) {
         this.resetCelestialState();
         return;
      }

      if (renderTarget != null && renderTarget.isAlive()) {
         this.celestialHeldTarget = renderTarget;
      }

      float frameTime = (mc.player.age + tickDelta) / 20.0F;
      float fade = this.updateCelestialFade(renderTarget != null && renderTarget.isAlive(), frameTime);

      if (fade <= 0.001F) {
         this.celestialHeldTarget = null;
         return;
      }

      LivingEntity target = this.celestialHeldTarget;
      if (target == null) {
         return;
      }

      Camera camera = mc.gameRenderer.getCamera();
      Vec3d cameraPos = camera.getPos();
      Vec3d position = new Vec3d(
         MathHelper.lerp(tickDelta, target.lastRenderX, target.getX()),
         MathHelper.lerp(tickDelta, target.lastRenderY, target.getY()),
         MathHelper.lerp(tickDelta, target.lastRenderZ, target.getZ())
      );

      double centerX = position.x - cameraPos.x;
      double centerY = position.y - cameraPos.y;
      double centerZ = position.z - cameraPos.z;

      double time = frameTime;
      double spin = (time * speed) % CELESTIAL_TWO_PI;
      int safeArms = Math.max(1, arms);
      int safeSegments = Math.max(1, segments);
      double arcStep = CELESTIAL_ARC / (double) safeSegments;
      double radius = target.getWidth() * 1.2;
      double verticalCenter = target.getHeight() / 2.0 + 0.2;
      int alpha = (int) (255.0 * fade * appear);
      if (alpha <= 0) {
         return;
      }

      float hurtPC = 0.0F;
      if (target.hurtTime > 0) {
         float v3 = MathHelper.clamp(target.hurtTime - tickDelta, 0.0F, 10.0F);
         float v4 = v3 / 10.0F;
         hurtPC = v4 * v4 * (3.0F - 2.0F * v4);
      }

      Quaternionf cameraRotation = camera.getRotation();

      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, CELESTIAL_GLOW_TEXTURE);

      BufferBuilder consumer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int arm = 0; arm < safeArms; arm++) {
         double verticalTime = time + arm * 15.0;
         double armPhase = arm * (CELESTIAL_TWO_PI / (double) safeArms);

         for (int segment = 0; segment <= safeSegments; segment++) {
            double arc = segment * arcStep;
            double angle = arc + spin + armPhase;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + Math.sin(verticalTime + arc + arm) * CELESTIAL_VERTICAL_AMPLITUDE + verticalCenter;
            double z = centerZ + radius * Math.sin(angle);

            float progress = (float) segment / (float) safeSegments;
            float size = 0.4F * (0.5F + progress) * sizeScale;
            int color = this.celestialGradient(segment, safeSegments, time, alpha, hurtPC);

            matrices.push();
            matrices.translate(x, y, z);
            matrices.multiply(cameraRotation);
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            float halfSize = size / 2.0F;
            consumer.vertex(matrix, -halfSize, -halfSize, 0.0F).texture(0.0F, 1.0F).color(color);
            consumer.vertex(matrix, halfSize, -halfSize, 0.0F).texture(1.0F, 1.0F).color(color);
            consumer.vertex(matrix, halfSize, halfSize, 0.0F).texture(1.0F, 0.0F).color(color);
            consumer.vertex(matrix, -halfSize, halfSize, 0.0F).texture(0.0F, 0.0F).color(color);

            matrices.pop();
         }
      }

      BufferRenderer.drawWithGlobalProgram(consumer.end());

      RenderSystem.enableCull();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }
}
