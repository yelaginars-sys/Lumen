package dlc.lumen.api.utils.render.blur;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.render.ShaderUtils;
import lombok.Generated;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.lwjgl.opengl.GL30;

public class BlurProgram implements QClient {
   private static BlurProgram blurProgram;
   private static Framebuffer framebuffer;
   private static Framebuffer framebuffer2;
   private int count = -1;
   private int count2 = -1;
   private long timeMs = 0L;
   private boolean flag = true;
   private float blurOffset = 1.0F;
   private final int count3 = 4;

   public static BlurProgram getInstance() {
      if (blurProgram == null) {
         blurProgram = new BlurProgram();
      }

      return blurProgram;
   }

   public void beginFrame() {
      boolean var1 = this.flag;
      this.flag = false;
      if (var1) {
         this.handleAction();
      }
   }

   public void request() {
      this.flag = true;
   }

   public void forceDraw() {
      this.timeMs = 0L;
      this.handleAction();
   }

   private void handleAction() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.timeMs >= 16L) {
         this.timeMs = var1;
         int var3 = mc.getWindow().getFramebufferWidth();
         int var4 = mc.getWindow().getFramebufferHeight();
         int var5 = Math.max(1, var3 / 2);
         int var6 = Math.max(1, var4 / 2);
         if (framebuffer == null || framebuffer2 == null || this.count != var3 || this.count2 != var4) {
            if (framebuffer != null) {
               framebuffer.delete();
            }

            if (framebuffer2 != null) {
               framebuffer2.delete();
            }

            framebuffer = new SimpleFramebuffer(var5, var6, false);
            framebuffer2 = new SimpleFramebuffer(var5, var6, false);
            this.handleFramebuffer(framebuffer);
            this.handleFramebuffer(framebuffer2);
            this.count = var3;
            this.count2 = var4;
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ShaderProgram var7 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.kawaseDown);
         ShaderProgram var8 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.kawaseUp);
         framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         framebuffer.clear();
         framebuffer.beginWrite(true);
         RenderSystem.setShader(ShaderUtils.kawaseDown);
         mc.getFramebuffer().beginRead();
         RenderSystem.setShaderTexture(0, mc.getFramebuffer().getColorAttachment());
         this.handleShader(var7, var3, var4);
         this.handleWidth(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
         mc.getFramebuffer().endRead();
         framebuffer.endWrite();
         Framebuffer[] var9 = new Framebuffer[]{framebuffer, framebuffer2};

         for (int var10 = 1; var10 < 4; var10++) {
            int var11 = (var10 + 1) % 2;
            int var12 = var10 % 2;
            Framebuffer var13 = var9[var11];
            Framebuffer var14 = var9[var12];
            var14.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var14.clear();
            var14.beginWrite(true);
            RenderSystem.setShader(ShaderUtils.kawaseDown);
            var13.beginRead();
            RenderSystem.setShaderTexture(0, var13.getColorAttachment());
            this.handleShader(var7, var13.textureWidth, var13.textureHeight);
            this.handleWidth(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
            var13.endRead();
            var14.endWrite();
         }

         for (int var15 = 0; var15 < 4; var15++) {
            int var16 = var15 % 2;
            int var17 = (var15 + 1) % 2;
            Framebuffer var18 = var9[var16];
            Framebuffer var19 = var9[var17];
            var19.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var19.clear();
            var19.beginWrite(true);
            RenderSystem.setShader(ShaderUtils.kawaseUp);
            var18.beginRead();
            RenderSystem.setShaderTexture(0, var18.getColorAttachment());
            this.handleShader(var8, var18.textureWidth, var18.textureHeight);
            this.handleWidth(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
            var18.endRead();
            var19.endWrite();
         }

         RenderSystem.disableBlend();
         mc.getFramebuffer().beginWrite(true);
         RenderSystem.setShaderTexture(0, 0);
      }
   }

   private void handleFramebuffer(Framebuffer framebuffer) {
      RenderSystem.bindTexture(framebuffer.getColorAttachment());
      GL30.glTexParameteri(3553, 10241, 9729);
      GL30.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private void handleShader(ShaderProgram shader, int texWidth, int texHeight) {
      GlUniform var4 = shader.getUniform("Resolution");
      GlUniform var5 = shader.getUniform("Offset");
      GlUniform var6 = shader.getUniform("Saturation");
      GlUniform var7 = shader.getUniform("TintIntensity");
      GlUniform var8 = shader.getUniform("TintColor");
      if (var4 != null) {
         var4.set(1.0F / texWidth, 1.0F / texHeight);
      }

      if (var5 != null) {
         var5.set(this.blurOffset);
      }

      if (var6 != null) {
         var6.set(1.0F);
      }

      if (var7 != null) {
         var7.set(0.0F);
      }

      if (var8 != null) {
         var8.set(1.0F, 1.0F, 1.0F);
      }
   }

   private void handleWidth(float width, float height) {
      BufferBuilder var3 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var3.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(0.0F, height, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(width, height, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(width, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(var3.end());
   }

   public static int getTexture() {
      getInstance().request();
      return framebuffer != null ? framebuffer.getColorAttachment() : 0;
   }

   @Generated
   public static Framebuffer getBuffer1() {
      return framebuffer;
   }

   @Generated
   public static Framebuffer getBuffer2() {
      return framebuffer2;
   }

   @Generated
   public void setBlurOffset(float blurOffset) {
      this.blurOffset = blurOffset;
   }
}
