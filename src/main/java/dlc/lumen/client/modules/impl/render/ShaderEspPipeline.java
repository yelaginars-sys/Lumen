package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.render.ShaderUtils;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class ShaderEspPipeline implements QClient {
   private static final int INDEX = 5;
   private final Framebuffer[] framebuffer2 = new Framebuffer[5];
   private final int[] index = new int[5];
   private final int[] index2 = new int[5];
   private int index3 = -1;
   private int index4 = -1;
   private final Matrix4f matrix4f = new Matrix4f();

   public void render(Framebuffer maskFbo, int width, int height, ShaderEspPipeline.Params p) {
      if (maskFbo != null && maskFbo.getColorAttachment() != 0) {
         if (p.debugRawMask) {
            mc.getFramebuffer().beginWrite(true);
            maskFbo.drawInternal(width, height);
         } else {
            ShaderProgram var5 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderEspDown);
            ShaderProgram var6 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderEspUp);
            ShaderProgram var7 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderEspCombine);
            if (var5 != null && var6 != null && var7 != null) {
               this.helper(width, height);
               this.helper2(maskFbo);
               float var8 = helper7(p.glowRadius, 0.0F, 1.0F);
               int var9 = Math.max(1, Math.min(5, 1 + Math.round(var8 * 4.0F)));
               float var10 = 0.5F + var8 * 3.5F;
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.disableCull();
               this.matrix4f.set(RenderSystem.getProjectionMatrix());
               RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, width, height, 0.0F, 1000.0F, 3000.0F), ProjectionType.ORTHOGRAPHIC);
               Framebuffer var11 = maskFbo;
               int var12 = width;
               int var13 = height;

               for (int var14 = 0; var14 < var9; var14++) {
                  Framebuffer var15 = this.framebuffer2[var14];
                  var15.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  var15.clear();
                  var15.beginWrite(true);
                  RenderSystem.setShader(ShaderUtils.shaderEspDown);
                  var11.beginRead();
                  RenderSystem.setShaderTexture(0, var11.getColorAttachment());
                  this.helper3(var5, var12, var13, 1.0F);
                  this.helper9();
                  var11.endRead();
                  var15.endWrite();
                  var11 = var15;
                  var12 = this.index[var14];
                  var13 = this.index2[var14];
               }

               for (int var16 = var9 - 1; var16 > 0; var16--) {
                  Framebuffer var17 = this.framebuffer2[var16 - 1];
                  var17.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  var17.clear();
                  var17.beginWrite(true);
                  RenderSystem.setShader(ShaderUtils.shaderEspUp);
                  var11.beginRead();
                  RenderSystem.setShaderTexture(0, var11.getColorAttachment());
                  this.helper3(var6, this.index[var16], this.index2[var16], var10);
                  this.helper9();
                  var11.endRead();
                  var17.endWrite();
                  var11 = var17;
               }

               mc.getFramebuffer().beginWrite(true);
               RenderSystem.enableBlend();
               RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
               RenderSystem.setShader(ShaderUtils.shaderEspCombine);
               maskFbo.beginRead();
               RenderSystem.setShaderTexture(0, maskFbo.getColorAttachment());
               RenderSystem.setShaderTexture(1, this.framebuffer2[0].getColorAttachment());
               this.helper4(var7, width, height, p);
               this.helper9();
               maskFbo.endRead();
               RenderSystem.setShaderTexture(0, 0);
               RenderSystem.setShaderTexture(1, 0);
               RenderSystem.setProjectionMatrix(this.matrix4f, ProjectionType.ORTHOGRAPHIC);
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(true);
               RenderSystem.enableCull();
            }
         }
      }
   }

   public void close() {
      for (int var1 = 0; var1 < 5; var1++) {
         if (this.framebuffer2[var1] != null) {
            this.framebuffer2[var1].delete();
            this.framebuffer2[var1] = null;
         }
      }

      this.index3 = -1;
      this.index4 = -1;
   }

   private void helper(int width, int height) {
      if (width != this.index3 || height != this.index4 || this.framebuffer2[0] == null) {
         this.close();
         int var3 = width;
         int var4 = height;

         for (int var5 = 0; var5 < 5; var5++) {
            var3 = Math.max(1, var3 / 2);
            var4 = Math.max(1, var4 / 2);
            this.index[var5] = var3;
            this.index2[var5] = var4;
            this.framebuffer2[var5] = new SimpleFramebuffer(var3, var4, false);
            this.helper2(this.framebuffer2[var5]);
         }

         this.index3 = width;
         this.index4 = height;
      }
   }

   private void helper2(Framebuffer framebuffer) {
      RenderSystem.bindTexture(framebuffer.getColorAttachment());
      GL30.glTexParameteri(3553, 10241, 9729);
      GL30.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private void helper3(ShaderProgram shader, int texWidth, int texHeight, float offset) {
      GlUniform var5 = shader.getUniform("EspKawaseParams");
      if (var5 != null) {
         var5.set(0.5F / texWidth, 0.5F / texHeight, offset, 0.0F);
      }
   }

   private void helper4(ShaderProgram shader, int width, int height, ShaderEspPipeline.Params p) {
      this.helper5(
         shader,
         "mainColor",
         helper6(p.color, 16),
         helper6(p.color, 8),
         helper6(p.color, 0),
         helper7(p.opacity, 0.0F, 1.0F)
      );
      this.helper5(
         shader,
         "friendColor",
         helper6(p.friendColor, 16),
         helper6(p.friendColor, 8),
         helper6(p.friendColor, 0),
         p.friendEnabled ? 1.0F : 0.0F
      );
      this.helper5(shader, "params1", 1.0F / width, 1.0F / height, Math.max(0, Math.min(5, p.outlineWidth)), p.outlineMode);
      this.helper5(
         shader, "params2", Math.max(0.0F, p.glowStrength), Math.max(0.05F, p.glowFalloff), Math.max(0.0F, p.fillOpacity), Math.max(0.0F, p.innerGlow)
      );
      this.helper5(
         shader,
         "params3",
         Math.max(0.0F, p.outlineStrength),
         helper7(p.outlineWhite, 0.0F, 1.0F),
         p.glowEnabled ? 1.0F : 0.0F,
         p.fillEnabled ? 1.0F : 0.0F
      );
      this.helper5(
         shader,
         "params4",
         helper8(p.shimmerPeriodSec),
         p.shimmerEnabled ? 1.0F : 0.0F,
         Math.max(0.005F, p.shimmerWidth),
         Math.max(0.0F, p.shimmerBrightness)
      );
      this.helper5(
         shader,
         "params5",
         p.pulseEnabled ? helper7(p.pulseAmount, 0.0F, 1.0F) : 0.0F,
         helper8(1.0F / Math.max(0.05F, p.pulseSpeed)),
         Math.max(0.0F, p.saturation),
         p.outlineEnabled ? 1.0F : 0.0F
      );
   }

   private void helper5(ShaderProgram shader, String name, float x, float y, float z, float w) {
      GlUniform var7 = shader.getUniform(name);
      if (var7 != null) {
         var7.set(x, y, z, w);
      }
   }

   private static float helper6(int argb, int shift) {
      return (argb >> shift & 0xFF) / 255.0F;
   }

   private static float helper7(float v, float min, float max) {
      return v < min ? min : Math.min(v, max);
   }

   private static float helper8(float periodSec) {
      long var1 = Math.max(1L, (long)(periodSec * 1000.0F));
      return (float)(System.currentTimeMillis() % var1) / (float)var1;
   }

   private void helper9() {
      BufferBuilder var1 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE_COLOR);
      var1.vertex(-1.0F, -1.0F, 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255);
      var1.vertex(1.0F, -1.0F, 0.0F).texture(1.0F, 0.0F).color(255, 255, 255, 255);
      var1.vertex(1.0F, 1.0F, 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255);
      var1.vertex(-1.0F, -1.0F, 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255);
      var1.vertex(1.0F, 1.0F, 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255);
      var1.vertex(-1.0F, 1.0F, 0.0F).texture(0.0F, 1.0F).color(255, 255, 255, 255);
      BufferRenderer.drawWithGlobalProgram(var1.end());
   }

   public static final class Params {
      public int color = -16711681;
      public int friendColor = -11141291;
      public boolean friendEnabled = true;
      public float opacity = 1.0F;
      public float saturation = 1.0F;
      public boolean glowEnabled = true;
      public float glowRadius = 0.55F;
      public float glowStrength = 1.6F;
      public float glowFalloff = 1.1F;
      public boolean fillEnabled = true;
      public float fillOpacity = 0.45F;
      public float innerGlow = 0.6F;
      public boolean outlineEnabled = true;
      public int outlineWidth = 2;
      public int outlineMode = 0;
      public float outlineStrength = 1.8F;
      public float outlineWhite = 0.45F;
      public boolean pulseEnabled = false;
      public float pulseSpeed = 1.0F;
      public float pulseAmount = 0.3F;
      public boolean shimmerEnabled = false;
      public float shimmerWidth = 0.04F;
      public float shimmerPeriodSec = 5.0F;
      public float shimmerBrightness = 0.8F;
      public boolean debugRawMask = false;
   }
}
