package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class MotionFps extends Module {
   public static final MotionFps INSTANCE = new MotionFps();
   private final FloatSetting floatSetting = new FloatSetting("Размытие", 0.5F, 0.1F, 1.0F, 0.05F);
   private Framebuffer framebuffer2;
   private Framebuffer framebuffer3;
   private int index = -1;
   private int index2 = -1;
   private boolean flag;

   private MotionFps() {
      super("MotionFps", "Плавность картинки за счёт кадрового блендинга", Module.ModuleCategory.RENDER);
      this.addSettings(this.floatSetting);
   }

   @Override
   public void onDisable() {
      this.helper3();
      super.onDisable();
   }

   public void onHudStart() {
      if (this.isEnable() && mc.world != null && mc.player != null) {
         int var1 = mc.getWindow().getFramebufferWidth();
         int var2 = mc.getWindow().getFramebufferHeight();
         if (var1 > 0 && var2 > 0) {
            if (this.framebuffer2 == null || this.index != var1 || this.index2 != var2) {
               this.helper3();
               this.framebuffer2 = new SimpleFramebuffer(var1, var2, false);
               this.framebuffer3 = new SimpleFramebuffer(var1, var2, false);
               this.helper2(this.framebuffer2);
               this.helper2(this.framebuffer3);
               this.index = var1;
               this.index2 = var2;
            }

            RenderSystem.disableBlend();
            this.framebuffer3.beginWrite(true);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
            RenderSystem.setShaderTexture(0, mc.getFramebuffer().getColorAttachment());
            this.helper();
            this.framebuffer3.endWrite();
            mc.getFramebuffer().beginWrite(true);
            if (this.flag) {
               float var3 = 0.12F + this.floatSetting.get() * 0.33F;
               RenderSystem.enableBlend();
               GL14.glBlendColor(1.0F, 1.0F, 1.0F, var3);
               RenderSystem.blendFunc(SrcFactor.CONSTANT_ALPHA, DstFactor.ONE_MINUS_CONSTANT_ALPHA);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
               RenderSystem.setShaderTexture(0, this.framebuffer2.getColorAttachment());
               this.helper();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
            }

            Framebuffer var4 = this.framebuffer2;
            this.framebuffer2 = this.framebuffer3;
            this.framebuffer3 = var4;
            RenderSystem.setShaderTexture(0, 0);
            this.flag = true;
         }
      } else {
         this.flag = false;
      }
   }

   private void helper() {
      double var1 = mc.getWindow().getScaleFactor();
      float var3 = (float)(mc.getWindow().getFramebufferWidth() / var1);
      float var4 = (float)(mc.getWindow().getFramebufferHeight() / var1);
      BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      var5.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F);
      var5.vertex(0.0F, var4, 0.0F).texture(0.0F, 0.0F);
      var5.vertex(var3, var4, 0.0F).texture(1.0F, 0.0F);
      var5.vertex(var3, 0.0F, 0.0F).texture(1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(var5.end());
   }

   private void helper2(Framebuffer framebuffer) {
      RenderSystem.bindTexture(framebuffer.getColorAttachment());
      GL30.glTexParameteri(3553, 10241, 9729);
      GL30.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private void helper3() {
      if (this.framebuffer2 != null) {
         this.framebuffer2.delete();
         this.framebuffer2 = null;
      }

      if (this.framebuffer3 != null) {
         this.framebuffer3.delete();
         this.framebuffer3 = null;
      }

      this.index = -1;
      this.index2 = -1;
      this.flag = false;
   }
}
