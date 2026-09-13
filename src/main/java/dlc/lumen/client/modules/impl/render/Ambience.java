package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.api.utils.render.blur.BlurProgram;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Ambience extends Module {
   public static final Ambience INSTANCE = new Ambience();
   private final BooleanSetting skyEnabled = new BooleanSetting("Шейдер неба", false);
   private final ModeSetting modeSetting = new ModeSetting("Стиль неба", "Лента", "Лента", "Туманность").visible(this.skyEnabled::getValue);
   private final FloatSetting floatSetting = new FloatSetting("Качество неба", 1.0F, 0.3F, 1.5F, 0.05F)
      .visible(() -> this.skyEnabled.getValue() && !this.modeSetting.is("Лента"));
   private final BooleanSetting blurFogEnabled = new BooleanSetting("Туман", true);
   private final FloatSetting floatSetting2 = new FloatSetting("Дистанция", 96.0F, 8.0F, 512.0F, 1.0F).visible(this.blurFogEnabled::getValue);
   private final FloatSetting floatSetting3 = new FloatSetting("Начало", 0.2F, 0.0F, 0.95F, 0.01F).visible(this.blurFogEnabled::getValue);
   private final BooleanSetting blurFogEnabled2 = new BooleanSetting("Блюр", false).visible(this.blurFogEnabled::getValue);
   private final FloatSetting floatSetting4 = new FloatSetting("Сила блюра", 1.0F, 0.0F, 6.0F, 0.05F)
      .visible(() -> this.blurFogEnabled.getValue() && this.blurFogEnabled2.getValue());
   private final BooleanSetting timeLocked = new BooleanSetting("Время суток", false);
   private final FloatSetting lockedTime = new FloatSetting("Время", 6000.0F, 0.0F, 24000.0F, 100.0F).visible(this.timeLocked::getValue);
   private SimpleFramebuffer simpleFramebuffer;
   private int index = -1;
   private int index2 = -1;
   private boolean flag = false;
   private float volume = 0.05F;
   private float volume2 = 1000.0F;
   private final Matrix4f matrix4f = new Matrix4f();

   private Ambience() {
      super("Ambience", "Атмосфера: туман, шейдер неба и время суток", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.blurFogEnabled,
         this.floatSetting2,
         this.floatSetting3,
         this.blurFogEnabled2,
         this.floatSetting4,
         this.skyEnabled,
         this.modeSetting,
         this.floatSetting,
         this.timeLocked,
         this.lockedTime
      );
   }

   public boolean shouldApply(Camera camera) {
      if (!this.isEnable() || !this.blurFogEnabled.getValue() || camera == null) {
         return false;
      } else {
         return this.blurFogEnabled2.getValue() ? false : camera.getSubmersionType() == CameraSubmersionType.NONE;
      }
   }

   public Vector4f getFogColor() {
      int var1 = this.helper();
      return new Vector4f(ColorUtils.redf(var1), ColorUtils.greenf(var1), ColorUtils.bluef(var1), 1.0F);
   }

   public Fog applyTo(Fog originalFog) {
      if (originalFog == null) {
         return null;
      }

      float var2 = Math.max(2.0F, this.floatSetting2.get());
      float var3 = Math.max(0.0F, Math.min(var2 - 0.5F, var2 * this.floatSetting3.get()));
      Vector4f var4 = this.getFogColor();
      return new Fog(var3, var2, originalFog.shape(), var4.x, var4.y, var4.z, originalFog.alpha());
   }

   private int helper() {
      return ColorUtils.getThemeColor();
   }

   @EventLink(priority = -200)
   public void onWorldCapture(Event3DRender event) {
      if (this.isBlurFogEnabled() && mc.world != null) {
         if (!this.helper2()) {
            int var2 = mc.getWindow().getFramebufferWidth();
            int var3 = mc.getWindow().getFramebufferHeight();
            if (var2 > 0 && var3 > 0) {
               this.helper4(var2, var3);
               if (this.simpleFramebuffer != null) {
                  this.helper5(this.simpleFramebuffer, var2, var3, 256);
                  Matrix4f var4 = event.getProjectionMatrix();
                  if (var4 != null) {
                     float var5 = var4.m22();
                     float var6 = var4.m32();
                     float var7 = var5 - 1.0F;
                     float var8 = var5 + 1.0F;
                     if (var7 != 0.0F && var8 != 0.0F) {
                        this.volume = var6 / var7;
                        this.volume2 = var6 / var8;
                     }
                  }

                  this.flag = true;
               }
            }
         }
      }
   }

   @EventLink(priority = 200)
   public void onRender2D(EventRender.Default event) {
      if (this.isBlurFogEnabled() && mc.world != null && mc.player != null && this.flag) {
         this.flag = false;
         if (!this.helper2()) {
            this.helper3();
         }
      }
   }

   public boolean isBlurFogEnabled() {
      return this.isEnable() && this.blurFogEnabled.getValue() && this.blurFogEnabled2.getValue();
   }

   private boolean helper2() {
      return mc.gameRenderer != null && mc.gameRenderer.getCamera() != null && mc.gameRenderer.getCamera().getSubmersionType() != CameraSubmersionType.NONE;
   }

   private void helper3() {
      if (this.simpleFramebuffer != null) {
         int var1 = mc.getWindow().getFramebufferWidth();
         int var2 = mc.getWindow().getFramebufferHeight();
         if (var1 > 0 && var2 > 0) {
            ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.fogBlur);
            if (var3 != null) {
               this.helper5(this.simpleFramebuffer, var1, var2, 16384);
               BlurProgram.getInstance().forceDraw();
               Framebuffer var4 = BlurProgram.getBuffer1();
               if (var4 != null) {
                  mc.getFramebuffer().beginWrite(true);
                  RenderSystem.disableBlend();
                  RenderSystem.disableDepthTest();
                  RenderSystem.depthMask(false);
                  RenderSystem.colorMask(true, true, true, true);
                  RenderSystem.setShader(ShaderUtils.fogBlur);
                  RenderSystem.setShaderTexture(0, this.simpleFramebuffer.getColorAttachment());
                  RenderSystem.setShaderTexture(1, var4.getColorAttachment());
                  int var5 = this.simpleFramebuffer.getDepthAttachment();
                  this.helper6(var5);
                  RenderSystem.setShaderTexture(2, var5);
                  float var6 = Math.max(2.0F, this.floatSetting2.get());
                  float var7 = Math.max(0.0F, Math.min(var6 - 0.5F, var6 * this.floatSetting3.get()));
                  int var8 = this.helper();
                  this.helper9(var3, "fogStart", var7);
                  this.helper9(var3, "fogEnd", var6);
                  this.helper9(var3, "blurStrength", this.floatSetting4.get());
                  this.helper9(var3, "near", this.volume);
                  this.helper9(var3, "far", this.volume2);
                  this.helper10(var3, "fogColor", ColorUtils.redf(var8), ColorUtils.greenf(var8), ColorUtils.bluef(var8));
                  this.helper7();
                  RenderSystem.setShaderTexture(0, 0);
                  RenderSystem.setShaderTexture(1, 0);
                  RenderSystem.setShaderTexture(2, 0);
                  RenderSystem.depthMask(true);
                  RenderSystem.enableDepthTest();
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  mc.getFramebuffer().beginWrite(true);
               }
            }
         }
      }
   }

   private void helper4(int w, int h) {
      if (this.simpleFramebuffer == null || this.index != w || this.index2 != h) {
         if (this.simpleFramebuffer != null) {
            this.simpleFramebuffer.delete();
         }

         this.simpleFramebuffer = new SimpleFramebuffer(w, h, true);
         this.index = w;
         this.index2 = h;
      }
   }

   private void helper5(Framebuffer target, int w, int h, int mask) {
      int var5 = GL11.glGetInteger(36010);
      int var6 = GL11.glGetInteger(36006);
      GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
      GL30.glBindFramebuffer(36009, target.fbo);
      GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, mask, 9728);
      GL30.glBindFramebuffer(36008, var5);
      GL30.glBindFramebuffer(36009, var6);
      mc.getFramebuffer().beginWrite(true);
   }

   private void helper6(int depthTex) {
      if (depthTex != 0) {
         RenderSystem.bindTexture(depthTex);
         GL11.glTexParameteri(3553, 34892, 0);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
         RenderSystem.bindTexture(0);
      }
   }

   private void helper7() {
      float var1 = Math.max(mc.getWindow().getScaledWidth(), 1);
      float var2 = Math.max(mc.getWindow().getScaledHeight(), 1);
      BufferBuilder var3 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var3.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(0.0F, var2, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(var1, var2, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(var1, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(var3.end());
   }

   @Override
   public void onDisable() {
      if (this.simpleFramebuffer != null) {
         this.simpleFramebuffer.delete();
         this.simpleFramebuffer = null;
         this.index = this.index2 = -1;
      }

      this.flag = false;
      super.onDisable();
   }

   public boolean isTimeLocked() {
      return this.isEnable() && this.timeLocked.getValue();
   }

   public long getLockedTime() {
      return (long)this.lockedTime.get();
   }

   public boolean isSkyEnabled() {
      return this.isEnable() && this.skyEnabled.getValue();
   }

   @EventLink(priority = 200)
   public void onRender3D(Event3DRender event) {
      if (this.isSkyEnabled() && mc.world != null && mc.player != null) {
         ShaderProgram var2 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.sky);
         if (var2 != null) {
            this.matrix4f.set(event.getProjectionMatrix()).mul(event.getPositionMatrix()).invert();
            int var3 = ColorUtils.getThemeColor();
            float var4 = (float)(System.currentTimeMillis() % 1000000L) / 1000.0F * 3.0F;
            Framebuffer var5 = mc.getFramebuffer();
            var5.beginWrite(false);
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515);
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(ShaderUtils.sky);
            this.helper8(var2, "InvViewProj", this.matrix4f);
            this.helper9(var2, "Time", var4);
            this.helper10(var2, "Theme", ColorUtils.redf(var3), ColorUtils.greenf(var3), ColorUtils.bluef(var3));
            this.helper9(var2, "Style", this.modeSetting.getIndex());
            this.helper9(var2, "Quality", this.floatSetting.get());
            BufferBuilder var6 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            var6.vertex(-1.0F, -1.0F, 0.0F).texture(0.0F, 0.0F);
            var6.vertex(-1.0F, 1.0F, 0.0F).texture(0.0F, 1.0F);
            var6.vertex(1.0F, 1.0F, 0.0F).texture(1.0F, 1.0F);
            var6.vertex(1.0F, -1.0F, 0.0F).texture(1.0F, 0.0F);
            BufferRenderer.drawWithGlobalProgram(var6.end());
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   private void helper8(ShaderProgram shader, String name, Matrix4f value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   private void helper9(ShaderProgram shader, String name, float value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   private void helper10(ShaderProgram shader, String name, float x, float y, float z) {
      GlUniform var6 = shader.getUniform(name);
      if (var6 != null) {
         var6.set(x, y, z);
      }
   }
}
