package dlc.lumen.api.utils.render.hands;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.client.modules.impl.render.ShaderHands;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShaderHandsRenderer implements QClient {
   private static final float fvalue = 0.001F;
   private static ShaderHandsRenderer shaderHandsRenderer;
   private Framebuffer framebuffer;
   private Framebuffer framebuffer2;
   private Framebuffer framebuffer3;
   private Framebuffer framebuffer4;
   private Framebuffer framebuffer5;
   private long timeMs;
   private final List<Framebuffer> framebufferList = new ArrayList<>();
   private int count = -1;
   private int count2 = -1;
   private boolean shader;
   private boolean flag;
   private int count3 = -1;
   private int count4 = -1;

   public static ShaderHandsRenderer getInstance() {
      if (shaderHandsRenderer == null) {
         shaderHandsRenderer = new ShaderHandsRenderer();
      }

      return shaderHandsRenderer;
   }

   public void captureBeforeHands() {
      ShaderHands var1 = this.getShaderHands();
      if (!this.checkModule(var1)) {
         this.invalidateState();
      } else {
         this.handleAction();
         if (this.framebuffer != null) {
            this.handleTarget(this.framebuffer);
            this.shader = true;
         }
      }
   }

   public void captureAfterHands() {
      ShaderHands var1 = this.getShaderHands();
      if (!this.checkModule(var1)) {
         this.invalidateState();
      } else {
         this.handleAction();
         if (this.framebuffer != null && this.framebuffer2 != null && this.framebuffer3 != null) {
            if (this.shader) {
               this.handleTarget(this.framebuffer2);
               this.flag = true;
            }
         }
      }
   }

   public void renderOverlayIfPending() {
      if (this.flag) {
         this.handleAction();
         if (this.framebuffer != null && this.framebuffer2 != null && this.framebuffer3 != null) {
            ShaderHands var1 = this.getShaderHands();
            if (!this.checkModule(var1)) {
               this.invalidateState();
            } else {
               ShaderProgram var2 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsMaskDiff);
               if (var2 == null) {
                  this.invalidateState();
               } else {
                  this.framebuffer3.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  this.framebuffer3.clear();
                  this.framebuffer3.beginWrite(false);
                  RenderSystem.disableDepthTest();
                  RenderSystem.disableBlend();
                  RenderSystem.setShader(ShaderUtils.shaderHandsMaskDiff);
                  RenderSystem.setShaderTexture(0, this.framebuffer.getColorAttachment());
                  RenderSystem.setShaderTexture(1, this.framebuffer2.getColorAttachment());
                  int var3 = this.framebuffer.getDepthAttachment();
                  int var4 = this.framebuffer2.getDepthAttachment();
                  if (var3 != 0 && var3 != this.count3) {
                     this.handleDepthTex(var3);
                     this.count3 = var3;
                  }

                  if (var4 != 0 && var4 != this.count4) {
                     this.handleDepthTex(var4);
                     this.count4 = var4;
                  }

                  RenderSystem.setShaderTexture(2, var3);
                  RenderSystem.setShaderTexture(3, var4);
                  this.handleAction4();
                  RenderSystem.enableDepthTest();
                  float var5 = 1.0F;
                  float var6 = 0.45F;
                  float var7 = 1.0F;
                  float var8 = 0.1F;
                  boolean var9 = var5 > 0.001F;
                  boolean var10 = var6 > 0.001F && var7 > 0.001F;
                  int var11 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
                     ? ColorUtils.getThemeColor(0)
                     : Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
                  int var12 = var11;
                  if (var1.mode.is("Красивый")) {
                     this.handleModule4(var1, var11, var12, var5, var6, var7, var8);
                     this.invalidateState();
                  } else if (var1.mode.is("Отсталый")) {
                     this.handleModule(var1, var11);
                     this.invalidateState();
                  } else if (var1.mode.is("Космос")) {
                     this.handleModule2(var1, var11);
                     this.invalidateState();
                  } else if (var1.mode.is("Slime")) {
                     this.handleModule3(var1, var11);
                     this.invalidateState();
                  } else {
                     int var13 = 0;
                     if (var9) {
                        int var14 = Math.max(3, Math.min(8, 4 + Math.round(var8 * 0.7F)));
                        var13 = this.calcIterations(var14, this.framebuffer3.getColorAttachment());
                     }

                     mc.getFramebuffer().beginWrite(true);
                     RenderSystem.enableBlend();
                     RenderSystem.colorMask(true, true, true, false);
                     RenderSystem.disableDepthTest();
                     ShaderProgram var16 = var9 ? mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsGlow) : null;
                     if (var16 != null) {
                        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
                        RenderSystem.setShader(ShaderUtils.shaderHandsGlow);
                        RenderSystem.setShaderTexture(0, var13);
                        RenderSystem.setShaderTexture(1, this.framebuffer3.getColorAttachment());
                        this.handleShader5(var16, "color", ColorUtils.redf(var11), ColorUtils.greenf(var11), ColorUtils.bluef(var11));
                        this.handleShader5(var16, "color2", ColorUtils.redf(var12), ColorUtils.greenf(var12), ColorUtils.bluef(var12));
                        this.handleShader3(var16, "exposure", 1.0F + var5 * 1.8F);
                        this.handleShader3(var16, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
                        this.handleShader3(var16, "fireIntensity", 0.4F);
                        this.handleShader3(var16, "fireSpeed", 3.0F);
                        this.handleShader3(var16, "fireDirection", -0.6F);
                        this.handleAction4();
                     }

                     if (var10) {
                        ShaderProgram var15 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsOverlay);
                        if (var15 == null) {
                           this.handleAction3();
                           this.invalidateState();
                           return;
                        }

                        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
                        RenderSystem.setShader(ShaderUtils.shaderHandsOverlay);
                        RenderSystem.setShaderTexture(0, this.framebuffer3.getColorAttachment());
                        this.handleShader5(var15, "color", ColorUtils.redf(var11), ColorUtils.greenf(var11), ColorUtils.bluef(var11));
                        this.handleShader3(var15, "fill", var6);
                        this.handleShader3(var15, "alpha", var7);
                        this.handleAction4();
                     }

                     this.handleAction3();
                     this.invalidateState();
                  }
               }
            }
         }
      }
   }

   public void invalidateState() {
      this.shader = false;
      this.flag = false;
      this.count3 = -1;
      this.count4 = -1;
   }

   private int calcIterations(int iterations, int sourceTexture) {
      this.handleIterations(iterations);
      if (this.framebufferList.isEmpty()) {
         return sourceTexture;
      }

      int var3 = sourceTexture;
      ShaderProgram var4 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsKawaseDown);
      ShaderProgram var5 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsKawaseUp);
      if (var4 != null && var5 != null) {
         for (int var6 = 0; var6 < iterations; var6++) {
            Framebuffer var7 = this.framebufferList.get(var6);
            var7.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var7.clear();
            var7.beginWrite(true);
            RenderSystem.setShader(ShaderUtils.shaderHandsKawaseDown);
            RenderSystem.setShaderTexture(0, var3);
            this.handleShader6(var4, var7.textureWidth, var7.textureHeight, 1.0F + var6);
            this.handleAction4();
            var3 = var7.getColorAttachment();
         }

         for (int var8 = iterations - 1; var8 >= 1; var8--) {
            Framebuffer var9 = this.framebufferList.get(var8 - 1);
            var9.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var9.clear();
            var9.beginWrite(true);
            RenderSystem.setShader(ShaderUtils.shaderHandsKawaseUp);
            RenderSystem.setShaderTexture(0, var3);
            this.handleShader6(var5, var9.textureWidth, var9.textureHeight, 1.0F + var8);
            this.handleShader5(var5, "color", 1.0F, 1.0F, 1.0F);
            this.handleAction4();
            var3 = var9.getColorAttachment();
         }

         mc.getFramebuffer().beginWrite(true);
         return var3;
      } else {
         return var3;
      }
   }

   private void handleTarget(Framebuffer target) {
      int var2 = GL11.glGetInteger(36010);
      int var3 = GL11.glGetInteger(36006);
      GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
      GL30.glBindFramebuffer(36009, target.fbo);
      GL30.glBlitFramebuffer(0, 0, this.count, this.count2, 0, 0, this.count, this.count2, 16640, 9728);
      GL30.glBindFramebuffer(36008, var2);
      GL30.glBindFramebuffer(36009, var3);
      mc.getFramebuffer().beginWrite(true);
   }

   private void handleDepthTex(int depthTex) {
      RenderSystem.bindTexture(depthTex);
      GL11.glTexParameteri(3553, 34892, 0);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      RenderSystem.bindTexture(0);
   }

   private void handleAction() {
      int var1 = mc.getWindow().getFramebufferWidth();
      int var2 = mc.getWindow().getFramebufferHeight();
      if (var1 != this.count
         || var2 != this.count2
         || this.framebuffer == null
         || this.framebuffer2 == null
         || this.framebuffer3 == null) {
         if (this.framebuffer != null) {
            this.framebuffer.delete();
         }

         if (this.framebuffer2 != null) {
            this.framebuffer2.delete();
         }

         if (this.framebuffer3 != null) {
            this.framebuffer3.delete();
         }

         for (Framebuffer var4 : this.framebufferList) {
            var4.delete();
         }

         this.framebufferList.clear();
         if (this.framebuffer4 != null) {
            this.framebuffer4.delete();
            this.framebuffer4 = null;
         }

         if (this.framebuffer5 != null) {
            this.framebuffer5.delete();
            this.framebuffer5 = null;
         }

         this.timeMs = 0L;
         this.framebuffer = new SimpleFramebuffer(var1, var2, true);
         this.framebuffer2 = new SimpleFramebuffer(var1, var2, true);
         this.framebuffer3 = new SimpleFramebuffer(var1, var2, true);
         this.count = var1;
         this.count2 = var2;
         this.count3 = -1;
         this.count4 = -1;
      }
   }

   private void handleIterations(int iterations) {
      while (this.framebufferList.size() > iterations) {
         int var2 = this.framebufferList.size() - 1;
         this.framebufferList.get(var2).delete();
         this.framebufferList.remove(var2);
      }

      for (int var6 = 0; var6 < iterations; var6++) {
         int var3 = Math.max(2, this.count >> var6 + 1);
         int var4 = Math.max(2, this.count2 >> var6 + 1);
         if (var6 >= this.framebufferList.size()) {
            SimpleFramebuffer var5 = new SimpleFramebuffer(var3, var4, false);
            this.handleFb(var5);
            this.framebufferList.add(var5);
         } else {
            Framebuffer var7 = this.framebufferList.get(var6);
            if (var7.textureWidth != var3 || var7.textureHeight != var4) {
               var7.delete();
               var7 = new SimpleFramebuffer(var3, var4, false);
               this.handleFb(var7);
               this.framebufferList.set(var6, var7);
            }
         }
      }
   }

   private void handleFb(Framebuffer fb) {
      RenderSystem.bindTexture(fb.getColorAttachment());
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private ShaderHands getShaderHands() {
      return Lumen.INSTANCE != null && ModuleClass.INSTANCE != null ? ModuleClass.shaderHands : null;
   }

   private void handleModule(ShaderHands module, int themeColor) {
      ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsTrail);
      ShaderProgram var4 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsTrailComposite);
      if (var3 != null && var4 != null) {
         if (this.framebuffer4 == null || this.framebuffer5 == null) {
            this.framebuffer4 = new SimpleFramebuffer(this.count, this.count2, false);
            this.framebuffer5 = new SimpleFramebuffer(this.count, this.count2, false);
            this.handleFb(this.framebuffer4);
            this.handleFb(this.framebuffer5);
            this.handleAction2();
         }

         long var5 = System.nanoTime();
         float var7 = this.timeMs == 0L ? 0.016666668F : (float)(var5 - this.timeMs) / 1.0E9F;
         if (var7 > 0.5F) {
            this.handleAction2();
            var7 = 0.016666668F;
         }

         var7 = Math.max(0.001F, Math.min(var7, 0.1F));
         this.timeMs = var5;
         this.framebuffer5.beginWrite(true);
         RenderSystem.disableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(ShaderUtils.shaderHandsTrail);
         RenderSystem.setShaderTexture(0, this.framebuffer4.getColorAttachment());
         RenderSystem.setShaderTexture(1, this.framebuffer3.getColorAttachment());
         RenderSystem.setShaderTexture(2, this.framebuffer2.getColorAttachment());
         this.handleShader3(var3, "uDt", var7);
         this.handleShader3(var3, "uRise", module.trailRise.get() * 0.6F);
         this.handleShader3(var3, "uFade", 3.5F / Math.max(module.trailLife.get(), 0.05F));
         this.handleShader3(var3, "uDissolve", module.trailDissolve.get());
         this.handleShader3(var3, "uTime", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         this.handleAction4();
         this.framebuffer5.endWrite();
         Framebuffer var8 = this.framebuffer4;
         this.framebuffer4 = this.framebuffer5;
         this.framebuffer5 = var8;
         float var9 = module.trailGlow.get();
         int var10 = 0;
         if (var9 > 0.01F) {
            var10 = this.calcIterations(4, this.framebuffer4.getColorAttachment());
         }

         mc.getFramebuffer().beginWrite(true);
         RenderSystem.enableBlend();
         RenderSystem.colorMask(true, true, true, false);
         RenderSystem.disableDepthTest();
         if (var10 != 0) {
            RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
            RenderSystem.setShader(ShaderUtils.shaderHandsTrailComposite);
            RenderSystem.setShaderTexture(0, var10);
            RenderSystem.setShaderTexture(1, this.framebuffer3.getColorAttachment());
            this.handleShader3(var4, "uAlpha", 0.4F * var9);
            this.handleShader5(var4, "uColor", ColorUtils.redf(themeColor), ColorUtils.greenf(themeColor), ColorUtils.bluef(themeColor));
            this.handleShader4(var4, "uTexel", 1.0F / Math.max(1, this.count), 1.0F / Math.max(1, this.count2));
            this.handleAction4();
         }

         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(ShaderUtils.shaderHandsTrailComposite);
         RenderSystem.setShaderTexture(0, this.framebuffer4.getColorAttachment());
         RenderSystem.setShaderTexture(1, this.framebuffer3.getColorAttachment());
         this.handleShader3(var4, "uAlpha", 0.95F);
         this.handleShader5(var4, "uColor", ColorUtils.redf(themeColor), ColorUtils.greenf(themeColor), ColorUtils.bluef(themeColor));
         this.handleShader4(var4, "uTexel", 1.0F / Math.max(1, this.count), 1.0F / Math.max(1, this.count2));
         this.handleAction4();
         this.handleAction3();
      } else {
         this.handleAction3();
      }
   }

   private void handleModule2(ShaderHands module, int themeColor) {
      ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsNebula);
      if (var3 == null) {
         this.handleAction3();
      } else {
         int var4 = this.calcIterations(4, this.framebuffer3.getColorAttachment());
         mc.getFramebuffer().beginWrite(true);
         RenderSystem.enableBlend();
         RenderSystem.colorMask(true, true, true, false);
         RenderSystem.disableDepthTest();
         float var5 = (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
         float var6 = (float)Math.max(1, this.count) / Math.max(1, this.count2);
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(ShaderUtils.shaderHandsNebula);
         RenderSystem.setShaderTexture(0, this.framebuffer3.getColorAttachment());
         RenderSystem.setShaderTexture(1, var4);
         RenderSystem.setShaderTexture(2, this.framebuffer2.getColorAttachment());
         this.handleShader(var3, module, themeColor, var5, var6, 1.0F);
         this.handleAction4();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(ShaderUtils.shaderHandsNebula);
         RenderSystem.setShaderTexture(0, this.framebuffer3.getColorAttachment());
         RenderSystem.setShaderTexture(1, var4);
         RenderSystem.setShaderTexture(2, this.framebuffer2.getColorAttachment());
         this.handleShader(var3, module, themeColor, var5, var6, 0.0F);
         this.handleAction4();
         this.handleAction3();
      }
   }

   private void handleShader(ShaderProgram shader, ShaderHands module, int themeColor, float time, float aspect, float outer) {
      this.handleShader5(shader, "uColor", ColorUtils.redf(themeColor), ColorUtils.greenf(themeColor), ColorUtils.bluef(themeColor));
      this.handleShader4(shader, "uAspect", aspect, 1.0F);
      this.handleShader3(shader, "uTime", time);
      this.handleShader3(shader, "uSpeed", module.nebulaSpeed.get());
      this.handleShader3(shader, "uDensity", module.nebulaDensity.get());
      this.handleShader3(shader, "uStars", module.nebulaStars.get());
      this.handleShader3(shader, "uGlow", module.nebulaGlow.get());
      this.handleShader3(shader, "uBlend", module.nebulaBlend.get());
      this.handleShader3(shader, "uOuter", outer);
   }

   private void handleModule3(ShaderHands module, int themeColor) {
      ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsSlime);
      if (var3 == null) {
         this.handleAction3();
      } else {
         mc.getFramebuffer().beginWrite(true);
         RenderSystem.enableBlend();
         RenderSystem.colorMask(true, true, true, false);
         RenderSystem.disableDepthTest();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         float var4 = (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
         float var5 = (float)Math.max(1, this.count) / Math.max(1, this.count2);
         RenderSystem.setShader(ShaderUtils.shaderHandsSlime);
         RenderSystem.setShaderTexture(0, this.framebuffer3.getColorAttachment());
         this.handleShader2(var3, module, themeColor, var4, var5);
         this.handleAction4();
         this.handleAction3();
      }
   }

   private void handleShader2(ShaderProgram shader, ShaderHands module, int themeColor, float time, float aspect) {
      this.handleShader5(shader, "uColor", ColorUtils.redf(themeColor), ColorUtils.greenf(themeColor), ColorUtils.bluef(themeColor));
      this.handleShader4(shader, "uAspect", aspect, 1.0F);
      this.handleShader4(shader, "uTexel", 1.0F / Math.max(1, this.count), 1.0F / Math.max(1, this.count2));
      this.handleShader3(shader, "uTime", time);
      this.handleShader3(shader, "uSpeed", module.slimeSpeed.get());
      this.handleShader3(shader, "uGoo", module.slimeGoo.get());
   }

   private void handleAction2() {
      if (this.framebuffer4 != null && this.framebuffer5 != null) {
         this.framebuffer4.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.framebuffer4.clear();
         this.framebuffer5.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.framebuffer5.clear();
         mc.getFramebuffer().beginWrite(true);
      }
   }

   private void handleModule4(ShaderHands module, int color1, int color2, float glowValue, float fillValue, float alphaValue, float outlineValue) {
      ShaderProgram var8 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blockOverlay);
      if (var8 != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(ShaderUtils.blockOverlay);
         RenderSystem.setShaderTexture(0, this.framebuffer3.getColorAttachment());
         this.handleShader4(
            var8, "texelSize", 1.0F / Math.max(1, mc.getWindow().getFramebufferWidth()), 1.0F / Math.max(1, mc.getWindow().getFramebufferHeight())
         );
         this.handleShader5(var8, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
         this.handleShader5(var8, "color2", ColorUtils.redf(color2), ColorUtils.greenf(color2), ColorUtils.bluef(color2));
         this.handleShader3(var8, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         this.handleShader3(var8, "speed", module.waveSpeed.get());
         this.handleShader3(var8, "scale", module.waveScale.get());
         this.handleShader3(var8, "outline", outlineValue);
         this.handleShader3(var8, "glow", glowValue);
         this.handleShader3(var8, "fill", fillValue);
         this.handleShader3(var8, "alpha", alphaValue);
         this.handleShader3(var8, "outlineOnly", 0.0F);
         this.handleAction4();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         this.handleAction3();
      }
   }

   private void handleAction3() {
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.setShaderTexture(1, 0);
      RenderSystem.setShaderTexture(2, 0);
      RenderSystem.setShaderTexture(3, 0);
      mc.getFramebuffer().beginWrite(true);
   }

   private boolean checkModule(ShaderHands module) {
      return module != null && module.isEnable();
   }

   private void handleShader3(ShaderProgram shader, String name, float v) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(v);
      }
   }

   private void handleShader4(ShaderProgram shader, String name, float x, float y) {
      GlUniform var5 = shader.getUniform(name);
      if (var5 != null) {
         var5.set(x, y);
      }
   }

   private void handleShader5(ShaderProgram shader, String name, float x, float y, float z) {
      GlUniform var6 = shader.getUniform(name);
      if (var6 != null) {
         var6.set(x, y, z);
      }
   }

   private void handleShader6(ShaderProgram shader, int texWidth, int texHeight, float offset) {
      this.handleShader4(shader, "uSize", Math.max(1, texWidth), Math.max(1, texHeight));
      this.handleShader4(shader, "uOffset", offset, offset);
      this.handleShader4(shader, "uHalfPixel", 0.5F / Math.max(1, texWidth), 0.5F / Math.max(1, texHeight));
   }

   private void handleAction4() {
      float var1 = Math.max(mc.getWindow().getScaledWidth(), 1);
      float var2 = Math.max(mc.getWindow().getScaledHeight(), 1);
      BufferBuilder var3 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var3.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(0.0F, var2, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(var1, var2, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(var1, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(var3.end());
   }
}
