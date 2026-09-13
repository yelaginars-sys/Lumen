package dlc.lumen.api.utils.render.glow;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GlowProgram {
   private static GlowProgram glowProgram;
   private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
   private Framebuffer framebuffer;
   private int count;
   private int count2;
   private float radius = 10.0F;
   private float intensity = 1.0F;
   private float fvalue = 1.0F;
   private float fvalue2 = 1.0F;
   private float fvalue3 = 1.0F;
   private float fvalue4 = 1.0F;
   private final Matrix4f matrix = new Matrix4f();
   private int count3;
   private static final int count4 = 6;
   private static final int count5 = 12;
   private final float[] fvalueArray = new float[6];
   private static final float[] fvalueArray2 = new float[12];
   private static final float[] fvalueArray3 = new float[12];
   private static final float[] fvalueArray4 = new float[12];
   private static final float[] fvalueArray5 = new float[12];

   public static GlowProgram getInstance() {
      if (glowProgram == null) {
         glowProgram = new GlowProgram();
      }

      return glowProgram;
   }

   private void handleAction() {
      int var1 = minecraftClient.getWindow().getFramebufferWidth();
      int var2 = minecraftClient.getWindow().getFramebufferHeight();
      if (this.framebuffer == null || this.count != var1 || this.count2 != var2) {
         if (this.framebuffer != null) {
            this.framebuffer.delete();
         }

         this.framebuffer = new SimpleFramebuffer(var1, var2, false);
         this.count = var1;
         this.count2 = var2;
      }
   }

   public void begin(float radius, Color color) {
      this.begin(radius, 1.0F, color);
   }

   public void begin(float radius, float intensity, Color color) {
      this.begin(radius, intensity, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
   }

   public void begin(float radius, float intensity, float r, float g, float b, float a) {
      this.handleAction();
      this.radius = radius;
      this.intensity = intensity;
      this.fvalue = r;
      this.fvalue2 = g;
      this.fvalue3 = b;
      this.fvalue4 = a;
      this.matrix.set(RenderSystem.getProjectionMatrix());
      this.count3 = GL11.glGetInteger(36006);
      GL30.glBindFramebuffer(36160, this.framebuffer.fbo);
      GL11.glViewport(0, 0, this.count, this.count2);
      RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      RenderSystem.clear(16384);
      RenderSystem.setProjectionMatrix(this.matrix, ProjectionType.ORTHOGRAPHIC);
   }

   public void end(MatrixStack matrices, GlowCallback contentCallback) {
      GL30.glBindFramebuffer(36160, this.count3);
      GL11.glViewport(0, 0, minecraftClient.getWindow().getFramebufferWidth(), minecraftClient.getWindow().getFramebufferHeight());
      RenderSystem.setProjectionMatrix(this.matrix, ProjectionType.ORTHOGRAPHIC);
      this.handleMatrices(matrices);
      if (contentCallback != null) {
         contentCallback.render();
      }
   }

   private float calcX(float x, float sigma) {
      return (float)Math.exp(-(x * x) / (2.0F * sigma * sigma));
   }

   private void handleMatrices(MatrixStack matrices) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(770, 1);
      RenderSystem.disableDepthTest();
      int var2 = minecraftClient.getWindow().getScaledWidth();
      int var3 = minecraftClient.getWindow().getScaledHeight();
      RenderSystem.setShaderTexture(0, this.framebuffer.getColorAttachment());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      Matrix4f var4 = matrices.peek().getPositionMatrix();
      float var5 = this.fvalue;
      float var6 = this.fvalue2;
      float var7 = this.fvalue3;
      float var8 = this.fvalue4 * this.intensity;
      float var9 = this.radius * 0.4F;
      float var10 = 0.0F;

      for (int var11 = 0; var11 < 6; var11++) {
         float var12 = this.radius * (var11 + 1) / 6.0F;
         this.fvalueArray[var11] = this.calcX(var12, var9);
         var10 += this.fvalueArray[var11];
      }

      for (int var22 = 0; var22 < 6; var22++) {
         this.fvalueArray[var22] = this.fvalueArray[var22] / var10;
      }

      BufferBuilder var23 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int var24 = 0; var24 < 6; var24++) {
         float var13 = this.radius * (var24 + 1) / 6.0F;
         float var14 = var8 * this.fvalueArray[var24] * 0.7F;
         if (!(var14 < 0.001F)) {
            var14 = Math.min(var14, 1.0F);

            for (int var15 = 0; var15 < 12; var15++) {
               float var16 = fvalueArray2[var15] * var13;
               float var17 = fvalueArray3[var15] * var13;
               var23.vertex(var4, var16, var17, 0.0F).texture(0.0F, 1.0F).color(var5, var6, var7, var14);
               var23.vertex(var4, var16, var3 + var17, 0.0F).texture(0.0F, 0.0F).color(var5, var6, var7, var14);
               var23.vertex(var4, var2 + var16, var3 + var17, 0.0F).texture(1.0F, 0.0F).color(var5, var6, var7, var14);
               var23.vertex(var4, var2 + var16, var17, 0.0F).texture(1.0F, 1.0F).color(var5, var6, var7, var14);
               if (var24 > 0) {
                  float var18 = var13 * 0.6F;
                  float var19 = fvalueArray4[var15] * var18;
                  float var20 = fvalueArray5[var15] * var18;
                  float var21 = var14 * 0.5F;
                  var23.vertex(var4, var19, var20, 0.0F).texture(0.0F, 1.0F).color(var5, var6, var7, var21);
                  var23.vertex(var4, var19, var3 + var20, 0.0F).texture(0.0F, 0.0F).color(var5, var6, var7, var21);
                  var23.vertex(var4, var2 + var19, var3 + var20, 0.0F).texture(1.0F, 0.0F).color(var5, var6, var7, var21);
                  var23.vertex(var4, var2 + var19, var20, 0.0F).texture(1.0F, 1.0F).color(var5, var6, var7, var21);
               }
            }
         }
      }

      BufferRenderer.drawWithGlobalProgram(var23.end());
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   public static void startGlow(float radius, int color, GlowCallback callback, MatrixStack matrices) {
      startGlow(radius, 1.0F, color, callback, matrices);
   }

   public static void startGlow(float radius, float intensity, int color, GlowCallback callback, MatrixStack matrices) {
      int var5 = color >> 24 & 0xFF;
      int var6 = color >> 16 & 0xFF;
      int var7 = color >> 8 & 0xFF;
      int var8 = color & 0xFF;
      if (var5 == 0) {
         var5 = 255;
      }

      GlowProgram var9 = getInstance();
      var9.begin(radius, intensity, var6 / 255.0F, var7 / 255.0F, var8 / 255.0F, var5 / 255.0F);
      callback.render();
      var9.end(matrices, callback);
   }

   public void cleanup() {
      if (this.framebuffer != null) {
         this.framebuffer.delete();
         this.framebuffer = null;
      }
   }

   static {
      for (int var0 = 0; var0 < 12; var0++) {
         float var1 = (float)(var0 * 2.0 * Math.PI) / 12.0F;
         fvalueArray2[var0] = (float)Math.cos(var1);
         fvalueArray3[var0] = (float)Math.sin(var1);
         float var2 = (float)((var0 + 0.5) * 2.0 * Math.PI) / 12.0F;
         fvalueArray4[var0] = (float)Math.cos(var2);
         fvalueArray5[var0] = (float)Math.sin(var2);
      }
   }
}
