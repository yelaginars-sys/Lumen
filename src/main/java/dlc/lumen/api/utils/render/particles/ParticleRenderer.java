package dlc.lumen.api.utils.render.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class ParticleRenderer implements QClient {
   private static final float fvalue = 80.0F;
   private static final float fvalue2 = 140.0F;
   private static final float fvalue3 = -Float.MAX_VALUE;

   private ParticleRenderer() {
   }

   public static void render(
      List<? extends ParticleBase> particles,
      MatrixStack matrices,
      float tickDelta,
      float cubeFactor,
      boolean scaleWithFade,
      float glowScale,
      ParticleRenderer.GlowStyle glowStyle,
      boolean clipGlow,
      boolean whiteCore
   ) {
      if (!particles.isEmpty() && mc.world != null) {
         Camera var9 = mc.gameRenderer.getCamera();
         Vec3d var10 = var9.getPos();
         double var11 = Math.toRadians(var9.getYaw());
         double var13 = Math.toRadians(var9.getPitch());
         float var15 = (float)Math.cos(var11);
         float var16 = (float)Math.sin(var11);
         float var17 = (float)Math.cos(var13);
         float var18 = (float)Math.sin(var13);
         float var19 = var15;
         float var20 = var16;
         float var21 = -var16 * var18;
         float var22 = var17;
         float var23 = var15 * var18;
         Matrix4f var24 = matrices.peek().getPositionMatrix();
         Tessellator var25 = Tessellator.getInstance();
         float var26 = (float)(System.currentTimeMillis() % 9000L) / 9000.0F * 360.0F;
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder var27 = var25.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

         for (ParticleBase var29 : particles) {
            if (!(var29.getFade() <= 0.0F) && var29.getShape() == ParticleShape.CUBES) {
               handleAction8(var27, var24, var29, var10, tickDelta, cubeFactor, scaleWithFade, var26, true);
            }
         }

         handleBuffer(var27);
         RenderSystem.lineWidth(1.5F);
         BufferBuilder var37 = var25.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

         for (ParticleBase var30 : particles) {
            if (!(var30.getFade() <= 0.0F) && var30.getShape() == ParticleShape.CUBES) {
               handleAction8(var37, var24, var30, var10, tickDelta, cubeFactor, scaleWithFade, var26, false);
            }
         }

         handleBuffer(var37);
         BufferBuilder var39 = var25.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

         for (ParticleBase var31 : particles) {
            if (!(var31.getFade() <= 0.0F)) {
               float[] var32 = ParticleGeometry.polygon(var31.getShape());
               if (var32 != null) {
                  handleAction2(var39, var24, var31, var32, var10, tickDelta, scaleWithFade, var19, var20, var21, var22, var23);
               }
            }
         }

         handleBuffer(var39);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

         for (Identifier var33 : ParticleGeometry.TEXTURES) {
            BufferBuilder var34 = var25.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            for (ParticleBase var36 : particles) {
               if (!(var36.getFade() <= 0.0F) && ParticleGeometry.texture(var36.getShape()) == var33) {
                  handleAction(var34, var24, var36, var10, tickDelta, scaleWithFade, whiteCore, var19, var20, var21, var22, var23);
               }
            }

            BuiltBuffer var48 = var34.endNullable();
            if (var48 != null) {
               RenderSystem.setShaderTexture(0, var33);
               BufferRenderer.drawWithGlobalProgram(var48);
            }
         }

         BufferBuilder var42 = var25.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (ParticleBase var47 : particles) {
            if (!(var47.getFade() <= 0.0F)) {
               handleAction3(
                  var42, var24, var47, var10, tickDelta, cubeFactor, scaleWithFade, glowScale, glowStyle, clipGlow, var19, var20, var21, var22, var23
               );
            }
         }

         BuiltBuffer var45 = var42.endNullable();
         if (var45 != null) {
            RenderSystem.setShaderTexture(0, ParticleGeometry.BLOOM);
            BufferRenderer.drawWithGlobalProgram(var45);
         }

         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private static void handleBuffer(BufferBuilder buffer) {
      BuiltBuffer var1 = buffer.endNullable();
      if (var1 != null) {
         BufferRenderer.drawWithGlobalProgram(var1);
      }
   }

   private static float calcParticle(ParticleBase particle, float factor, boolean scaleWithFade) {
      float var3 = particle.getSize() * factor;
      return scaleWithFade ? var3 * (0.35F + 0.65F * particle.getFade()) : var3;
   }

   private static void handleAction(
      BufferBuilder buffer,
      Matrix4f matrix,
      ParticleBase particle,
      Vec3d cameraPos,
      float tickDelta,
      boolean scaleWithFade,
      boolean whiteCore,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ
   ) {
      float var12 = calcParticle(particle, 0.5F, scaleWithFade) / 2.0F;
      float var13 = (float)(particle.lerpX(tickDelta) - cameraPos.x);
      float var14 = (float)(particle.lerpY(tickDelta) - cameraPos.y);
      float var15 = (float)(particle.lerpZ(tickDelta) - cameraPos.z);
      double var16 = Math.toRadians(particle.getRoll());
      float var18 = (float)Math.sin(var16);
      float var19 = (float)Math.cos(var16);
      int var20 = particle.renderColor();
      int var21 = ColorUtils.r(var20);
      int var22 = ColorUtils.g(var20);
      int var23 = ColorUtils.b(var20);
      int var24 = ColorUtils.a(var20);
      handleAction5(buffer, matrix, var13, var14, var15, var12, var18, var19, rightX, rightZ, upX, upY, upZ, var21, var22, var23, var24);
      if (whiteCore) {
         int var25 = (int)(200.0F * particle.getFade());
         handleAction5(buffer, matrix, var13, var14, var15, var12 * 0.5F, var18, var19, rightX, rightZ, upX, upY, upZ, 255, 255, 255, var25);
      }
   }

   private static void handleAction2(
      BufferBuilder buffer,
      Matrix4f matrix,
      ParticleBase particle,
      float[] shape,
      Vec3d cameraPos,
      float tickDelta,
      boolean scaleWithFade,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ
   ) {
      float var12 = calcParticle(particle, 0.5F, scaleWithFade);
      float var13 = (float)(particle.lerpX(tickDelta) - cameraPos.x);
      float var14 = (float)(particle.lerpY(tickDelta) - cameraPos.y);
      float var15 = (float)(particle.lerpZ(tickDelta) - cameraPos.z);
      double var16 = Math.toRadians(particle.getRoll());
      float var18 = (float)Math.sin(var16);
      float var19 = (float)Math.cos(var16);
      int var20 = particle.renderColor();
      int var21 = ColorUtils.r(var20);
      int var22 = ColorUtils.g(var20);
      int var23 = ColorUtils.b(var20);
      int var24 = ColorUtils.a(var20);

      for (byte var25 = 0; var25 < shape.length; var25 += 2) {
         float var26 = shape[var25] * var12;
         float var27 = shape[var25 + 1] * var12;
         float var28 = var26 * var19 - var27 * var18;
         float var29 = var26 * var18 + var27 * var19;
         buffer.vertex(matrix, var13 + var28 * rightX + var29 * upX, var14 + var29 * upY, var15 + var28 * rightZ + var29 * upZ)
            .color(var21, var22, var23, var24);
      }
   }

   private static void handleAction3(
      BufferBuilder buffer,
      Matrix4f matrix,
      ParticleBase particle,
      Vec3d cameraPos,
      float tickDelta,
      float cubeFactor,
      boolean scaleWithFade,
      float glowScale,
      ParticleRenderer.GlowStyle style,
      boolean clipGlow,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ
   ) {
      float var15;
      float var16;
      if (particle.getShape() == ParticleShape.CUBES) {
         float var17 = calcParticle(particle, cubeFactor, scaleWithFade);
         var15 = var17 * glowScale;
         var16 = var17 * glowScale / 3.0F;
      } else {
         float var33 = calcParticle(particle, 0.5F, scaleWithFade);
         var15 = var33 * glowScale * 0.5F;
         var16 = var33 * glowScale * 0.2F;
      }

      double var34 = particle.lerpX(tickDelta);
      double var19 = particle.lerpY(tickDelta);
      double var21 = particle.lerpZ(tickDelta);
      float var23 = (float)(var34 - cameraPos.x);
      float var24 = (float)(var19 - cameraPos.y);
      float var25 = (float)(var21 - cameraPos.z);
      int var26 = particle.renderColor();
      int var27 = ColorUtils.r(var26);
      int var28 = ColorUtils.g(var26);
      int var29 = ColorUtils.b(var26);
      float var30 = particle.getFade();
      double var31 = clipGlow ? ParticleBase.groundBelow(var34, var19, var21) : Double.NaN;
      if (style != ParticleRenderer.GlowStyle.BRIGHT) {
         handleAction4(
            buffer,
            matrix,
            var23,
            var24,
            var25,
            var15,
            calcParticleY(var19, var31, var15),
            rightX,
            rightZ,
            upX,
            upY,
            upZ,
            var27,
            var28,
            var29,
            (int)(80.0F * var30)
         );
      }

      if (style != ParticleRenderer.GlowStyle.SOFT) {
         handleAction4(
            buffer,
            matrix,
            var23,
            var24,
            var25,
            var16,
            calcParticleY(var19, var31, var16),
            rightX,
            rightZ,
            upX,
            upY,
            upZ,
            var27,
            var28,
            var29,
            (int)(140.0F * var30)
         );
      }
   }

   private static float calcParticleY(double particleY, double ground, float size) {
      if (Double.isNaN(ground)) {
         return -Float.MAX_VALUE;
      }

      double var5 = particleY - size / 2.0;
      return var5 < ground ? (float)(ground - particleY) : -Float.MAX_VALUE;
   }

   private static void handleAction4(
      BufferBuilder buffer,
      Matrix4f matrix,
      float px,
      float py,
      float pz,
      float size,
      float clip,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ,
      int r,
      int g,
      int b,
      int a
   ) {
      if (a > 0 && !(size <= 0.0F)) {
         float var16 = size / 2.0F;
         float var17 = Math.min(var16, Math.max(-var16, clip));
         if (!(var17 >= var16)) {
            boolean var18 = clip != -Float.MAX_VALUE;
            float var19 = var18 ? 0.0F : upX;
            float var20 = var18 ? 1.0F : upY;
            float var21 = var18 ? 0.0F : upZ;
            float var22 = 1.0F - (var17 + var16) / size;
            handleAction7(buffer, matrix, px, py, pz, -var16, var17, rightX, rightZ, var19, var20, var21, 0.0F, var22, r, g, b, a);
            handleAction7(buffer, matrix, px, py, pz, -var16, var16, rightX, rightZ, var19, var20, var21, 0.0F, 0.0F, r, g, b, a);
            handleAction7(buffer, matrix, px, py, pz, var16, var16, rightX, rightZ, var19, var20, var21, 1.0F, 0.0F, r, g, b, a);
            handleAction7(buffer, matrix, px, py, pz, var16, var17, rightX, rightZ, var19, var20, var21, 1.0F, var22, r, g, b, a);
         }
      }
   }

   private static void handleAction5(
      BufferBuilder buffer,
      Matrix4f matrix,
      float px,
      float py,
      float pz,
      float half,
      float sin,
      float cos,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ,
      int r,
      int g,
      int b,
      int a
   ) {
      if (a > 0) {
         handleAction6(buffer, matrix, px, py, pz, -half, -half, sin, cos, rightX, rightZ, upX, upY, upZ, 0.0F, 1.0F, r, g, b, a);
         handleAction6(buffer, matrix, px, py, pz, -half, half, sin, cos, rightX, rightZ, upX, upY, upZ, 0.0F, 0.0F, r, g, b, a);
         handleAction6(buffer, matrix, px, py, pz, half, half, sin, cos, rightX, rightZ, upX, upY, upZ, 1.0F, 0.0F, r, g, b, a);
         handleAction6(buffer, matrix, px, py, pz, half, -half, sin, cos, rightX, rightZ, upX, upY, upZ, 1.0F, 1.0F, r, g, b, a);
      }
   }

   private static void handleAction6(
      BufferBuilder buffer,
      Matrix4f matrix,
      float px,
      float py,
      float pz,
      float lx,
      float ly,
      float sin,
      float cos,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ,
      float u,
      float v,
      int r,
      int g,
      int b,
      int a
   ) {
      handleAction7(buffer, matrix, px, py, pz, lx * cos - ly * sin, lx * sin + ly * cos, rightX, rightZ, upX, upY, upZ, u, v, r, g, b, a);
   }

   private static void handleAction7(
      BufferBuilder buffer,
      Matrix4f matrix,
      float px,
      float py,
      float pz,
      float lx,
      float ly,
      float rightX,
      float rightZ,
      float upX,
      float upY,
      float upZ,
      float u,
      float v,
      int r,
      int g,
      int b,
      int a
   ) {
      buffer.vertex(matrix, px + lx * rightX + ly * upX, py + ly * upY, pz + lx * rightZ + ly * upZ).texture(u, v).color(r, g, b, a);
   }

   private static void handleAction8(
      BufferBuilder buffer,
      Matrix4f matrix,
      ParticleBase particle,
      Vec3d cameraPos,
      float tickDelta,
      float cubeFactor,
      boolean scaleWithFade,
      float spinAngle,
      boolean solid
   ) {
      float var9 = calcParticle(particle, cubeFactor, scaleWithFade) / 2.0F;
      float var10 = (float)(particle.lerpX(tickDelta) - cameraPos.x);
      float var11 = (float)(particle.lerpY(tickDelta) - cameraPos.y);
      float var12 = (float)(particle.lerpZ(tickDelta) - cameraPos.z);
      int var13 = ColorUtils.applyAlpha(particle.renderColor(), solid ? 0.2F : 0.4F);
      int var14 = ColorUtils.r(var13);
      int var15 = ColorUtils.g(var13);
      int var16 = ColorUtils.b(var13);
      int var17 = ColorUtils.a(var13);
      if (var17 > 0) {
         double var18 = Math.toRadians(spinAngle + particle.getSpinPhase());
         double var20 = Math.toRadians(spinAngle * 0.5F);
         float var22 = (float)Math.cos(var18);
         float var23 = (float)Math.sin(var18);
         float var24 = (float)Math.cos(var20);
         float var25 = (float)Math.sin(var20);
         float[] var26 = new float[24];
         int var27 = 0;

         for (byte var28 = 1; var28 >= -1; var28 -= 2) {
            for (byte var29 = -1; var29 <= 1; var29 += 2) {
               for (byte var30 = -1; var30 <= 1; var30 += 2) {
                  float var31 = var30 * var9;
                  float var32 = var28 * var9;
                  float var33 = var29 * var9;
                  float var34 = var32 * var24 - var33 * var25;
                  float var35 = var32 * var25 + var33 * var24;
                  var26[var27++] = var10 + var31 * var22 + var35 * var23;
                  var26[var27++] = var11 + var34;
                  var26[var27++] = var12 - var31 * var23 + var35 * var22;
               }
            }
         }

         byte var38 = 0;
         byte var39 = 1;
         byte var40 = 2;
         byte var41 = 3;
         byte var42 = 4;
         byte var43 = 5;
         byte var44 = 6;
         byte var45 = 7;
         if (solid) {
            handleAction9(buffer, matrix, var26, var38, var40, var41, var39, var14, var15, var16, var17);
            handleAction9(buffer, matrix, var26, var42, var43, var45, var44, var14, var15, var16, var17);
            handleAction9(buffer, matrix, var26, var40, var44, var45, var41, var14, var15, var16, var17);
            handleAction9(buffer, matrix, var26, var38, var39, var43, var42, var14, var15, var16, var17);
            handleAction9(buffer, matrix, var26, var38, var42, var44, var40, var14, var15, var16, var17);
            handleAction9(buffer, matrix, var26, var39, var41, var45, var43, var14, var15, var16, var17);
         } else {
            handleBuffer2(buffer, matrix, var26, var42, var43, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var43, var45, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var45, var44, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var44, var42, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var38, var39, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var39, var41, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var41, var40, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var40, var38, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var42, var38, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var43, var39, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var45, var41, var14, var15, var16, var17);
            handleBuffer2(buffer, matrix, var26, var44, var40, var14, var15, var16, var17);
         }
      }
   }

   private static void handleAction9(
      BufferBuilder buffer, Matrix4f matrix, float[] corners, int first, int second, int third, int fourth, int r, int g, int b, int a
   ) {
      handleBuffer3(buffer, matrix, corners, first, r, g, b, a);
      handleBuffer3(buffer, matrix, corners, second, r, g, b, a);
      handleBuffer3(buffer, matrix, corners, third, r, g, b, a);
      handleBuffer3(buffer, matrix, corners, fourth, r, g, b, a);
   }

   private static void handleBuffer2(BufferBuilder buffer, Matrix4f matrix, float[] corners, int from, int to, int r, int g, int b, int a) {
      handleBuffer3(buffer, matrix, corners, from, r, g, b, a);
      handleBuffer3(buffer, matrix, corners, to, r, g, b, a);
   }

   private static void handleBuffer3(BufferBuilder buffer, Matrix4f matrix, float[] corners, int index, int r, int g, int b, int a) {
      int var8 = index * 3;
      buffer.vertex(matrix, corners[var8], corners[var8 + 1], corners[var8 + 2]).color(r, g, b, a);
   }

   public enum GlowStyle {
      SOFT,
      BRIGHT,
      BOTH;
   }
}
