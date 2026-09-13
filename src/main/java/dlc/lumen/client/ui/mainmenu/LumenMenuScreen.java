package dlc.lumen.client.ui.mainmenu;

import dlc.lumen.api.utils.client.ClientSoundPlayer;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easing;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.api.utils.render.blur.BlurProgram;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.social.GlobalProfileAvatar;
import dlc.lumen.client.ui.mainmenu.account.Account;
import dlc.lumen.client.ui.mainmenu.account.AccountGuiScreen;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class LumenMenuScreen extends Screen implements QClient {
   private static final int EXIT_RED = ColorUtils.rgb(255, 82, 82);
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "textures/mainmenu/menu_bg.png");
   private static final Identifier TEXTURE_ID2 = Identifier.of("lumen", "textures/mainmenu/single_bg.png");
   private static final Identifier TEXTURE_ID3 = Identifier.of("lumen", "textures/mainmenu/multi_bg.png");
   private static final Identifier TEXTURE_ID4 = Identifier.of("lumen", "textures/waterlogo/lumenik.png");
   private static final String[] STRING = new String[]{"", "", ""};
   private static final String[] STRING2 = new String[]{"web", "tg", "yt"};
   private static final float VOLUME = 200.0F;
   private static final float VOLUME2 = 26.0F;
   private static final float VOLUME3 = 178.0F;
   private static final float VOLUME4 = 18.0F;
   private static final float VOLUME5 = 22.0F;
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 2.8F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils2 = new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils3 = new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils4 = new AnimationUtils(0.0F, 12.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils5 = new AnimationUtils(0.0F, 12.0F, Easings.CUBIC_OUT);
   private float volume;
   private long timestamp = Util.getMeasuringTimeMs();
   private float volume2;
   private boolean flag2;
   private boolean flag3;
   private final float[] volume3 = new float[4];
   private final float[] volume5 = new float[4];
   private final float[] volume6 = new float[4];
   private final float[] volume7 = new float[4];
   private static final String TEXT = "h";
   private final float[][] value = new float[][]{new float[4], new float[4], new float[4]};
   private final float[] volume8 = new float[4];
   private final float[] volume9 = new float[4];
   private static boolean flag4 = false;

   public LumenMenuScreen() {
      super(Text.empty());
   }

   @Override
   protected void init() {
      super.init();
      this.animationUtils.setValue(0.0F);
      if (!flag4) {
         flag4 = true;
         AccountGuiScreen.MANAGER.restoreLastSession();
      }
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.animationUtils.update(1.0F);
      float var5 = MathHelper.clamp(this.animationUtils.getValue(), 0.0F, 1.0F);
      long var6 = Util.getMeasuringTimeMs();
      float var8 = Math.min(0.1F, (float)(var6 - this.timestamp) / 1000.0F);
      this.timestamp = var6;
      float var9 = (float)var6 / 1000.0F;
      this.helper2(context, var5, mouseX, mouseY);
      BlurProgram.getInstance().forceDraw();
      if (!this.flag2 && !this.flag3 && this.volume2 > 0.0F) {
         this.volume2 = Math.max(0.0F, this.volume2 - 0.08F);
      }

      float var10 = helper17(var5, 0.0F, 0.45F);
      float var11 = helper17(var5, 0.1F, 0.45F);
      float var12 = helper17(var5, 0.16F, 0.45F);
      float var13 = helper17(var5, 0.24F, 0.52F);
      float var14 = helper17(var5, 0.34F, 0.52F);
      this.helper4(context, mouseX, mouseY, var11, var12, var8);
      this.helper3(context, var10, var9);
      float var18 = MathHelper.clamp(this.height * 0.5F - 48.0F + this.height * 0.035F, this.height * 0.16F, this.height - 96.0F);
      this.helper8(context, mouseX, mouseY, var13, var18 + (1.0F - var13) * 20.0F, var8);
      float var19 = var18 + 68.0F;
      this.helper9(context, mouseX, mouseY, var14, var19 + (1.0F - var14) * 20.0F, var8);
      super.render(context, mouseX, mouseY, delta);
   }

   private void helper2(DrawContext context, float r, double mouseX, double mouseY) {
      context.draw();
      int width = this.width;
      int height = this.height;
      MatrixStack matrices = context.getMatrices();
      if (mc.getResourceManager().getResource(TEXTURE_ID).isPresent()) {
         float scale = Math.max(width / 2560.0F, height / 1440.0F);
         float dw = 2560.0F * scale;
         float dh = 1440.0F * scale;
         RenderUtils.drawImage(matrices, TEXTURE_ID, (width - dw) / 2.0F, (height - dh) / 2.0F, dw, dh, -1);
         RenderUtils.drawRoundedRect(matrices, 0.0F, 0.0F, width, height, 0.0F, ColorUtils.setAlphaColor(ColorUtils.rgb(8, 9, 14), (int)(165.0F * r)));
      } else {
         RenderUtils.drawRoundedRect(matrices, 0.0F, 0.0F, width, height, 0.0F, ColorUtils.rgb(9, 10, 15));
      }

      int theme = ColorUtils.getThemeColor();
      RenderUtils.drawRoundCircle(matrices, width * 0.5F, height * 0.40F, height * 0.62F, ColorUtils.setAlphaColor(theme, (int)(24.0F * r)));
      MenuParticles.render(context, width, height, mouseX, mouseY, r);
   }

   private void updateState(DrawContext context, float x, float y, float w, float h) {
      MatrixStack var6 = context.getMatrices();
      RenderUtils.drawBlur(var6, x + 0.25F, y, w - 0.5F, h, 8.0F, 5.0F, ColorUtils.getThemeColor());
      RenderUtils.drawBlur(var6, x + 0.25F, y, w - 0.5F, h, 8.0F, 5.0F, ColorUtils.rgba(0, 0, 0, 195));
   }

   private static void updateState2(MatrixStack matrices, float x, float centerY, float h, int color) {
      float var5 = h * 0.22F;
      float var6 = h * 0.18F;
      float var7 = h;
      float var8 = h * 0.55F;
      float var9 = var5 * 0.5F;
      RenderUtils.drawImage(matrices, TEXTURE_ID4, x, centerY - var7 / 2.0F, var7, var7, color);
   }

   private void helper3(DrawContext context, float r, float time) {
      float var4 = this.width / 2.0F;
      float var5 = (float)Math.sin(time * 1.6F) * 1.8F;
      float var6 = Math.max(34.0F, this.height * 0.13F) + var5 + (1.0F - r) * -8.0F;
      float var7 = 40.0F * (0.9F + 0.1F * r);
      float var8 = var7 * 1.02F;
      updateState2(context.getMatrices(), var4 - var8 / 2.0F, var6 + var7 / 2.0F, var7, helper21(helper20(), (int)(255.0F * r)));
      var6 += var7 + 6.0F;
      Font var9 = helper19("suisse", 19);
      Font var10 = helper19("sf_regular", 10);
      if (var9 != null) {
         var9.drawCenteredString(context.getMatrices(), "Lumen Client", var4, var6, helper21(-1, (int)(255.0F * r)));
         var6 += var9.getHeight() + 2.0F;
      }

      if (var10 != null) {
         var10.drawCenteredString(context.getMatrices(), "1.21.4", var4, var6, helper21(helper20(), (int)(200.0F * r)));
      }
   }

   private void helper4(DrawContext context, int mouseX, int mouseY, float aLeft, float aRight, float dt) {
      float var7 = 150.0F;
      float var8 = 100.0F;
      float var9 = this.height / 2.0F - var8 / 2.0F;
      float var10 = Math.max(12.0F, this.width * 0.5F - 300.0F);
      float var11 = Math.min(this.width - 12.0F - var7, this.width * 0.5F + 300.0F - var7);
      helper22(this.volume8, var10, var9, var7, var8);
      helper22(this.volume9, var11, var9, var7, var8);
      float var12 = (1.0F - aLeft) * -34.0F;
      float var13 = (1.0F - aRight) * 34.0F;
      this.helper5(
         context, this.volume8, var12, "Одиночная", "Singleplayer", TEXTURE_ID2, mouseX, mouseY, aLeft, this.animationUtils2, dt
      );
      this.helper5(context, this.volume9, var13, "Сетевая", "Multiplayer", TEXTURE_ID3, mouseX, mouseY, aRight, this.animationUtils3, dt);
   }

   private void helper5(
      DrawContext context, float[] rc, float dx, String title, String sub, Identifier bg, int mouseX, int mouseY, float r, AnimationUtils hoverAnim, float dt
   ) {
      boolean var12 = helper23(mouseX, mouseY, rc) && !this.flag3;
      hoverAnim.update(var12 ? 1.0F : 0.0F);
      float var13 = hoverAnim.getValue();
      float var14 = 5.0F * var13;
      float var15 = 6.0F * var13;
      float[] var16 = new float[]{rc[0] + dx - var14, rc[1] - var14 - var15, rc[2] + var14 * 2.0F, rc[3] + var14 * 2.0F};
      MatrixStack var17 = context.getMatrices();
      if (var13 > 0.001F) {
         RenderUtils.drawRoundedRect(
            var17, var16[0] - 3.0F, var16[1] - 3.0F, var16[2] + 6.0F, var16[3] + 6.0F, 15.0F, helper21(-1, (int)(70.0F * var13 * r))
         );
      }

      this.updateState(context, var16[0], var16[1], var16[2], var16[3]);
      this.helper6(context, var16, bg, (int)((225.0F + 30.0F * var13) * r));
      if (var13 > 0.001F) {
         RenderUtils.drawRoundedRect(
            var17, var16[0] + 4.0F, var16[1] + 4.0F, var16[2] - 8.0F, var16[3] - 8.0F, 8.0F, helper21(-1, (int)(40.0F * var13 * r))
         );
      }

      Font var18 = helper19("suisse", 15);
      Font var19 = helper19("sf_regular", 10);
      float var20 = var16[0] + var16[2] / 2.0F;
      if (var18 != null) {
         var18.drawCenteredString(var17, title, var20, var16[1] + var16[3] - 28.0F, helper21(-1, (int)(255.0F * r)));
      }

      if (var19 != null) {
         var19.drawCenteredString(var17, sub, var20, var16[1] + var16[3] - 13.0F, helper21(-1, (int)(220.0F * r)));
      }
   }

   private void helper6(DrawContext context, float[] rc, Identifier tex, int alpha) {
      if (tex != null && mc.getResourceManager().getResource(tex).isPresent()) {
         float var5 = 4.0F;
         float var6 = rc[0] + var5;
         float var7 = rc[1] + var5;
         float var8 = rc[2] - var5 * 2.0F;
         float var9 = rc[3] - var5 * 2.0F;
         float var10 = 1.7777778F;
         float var11 = var8 / var9;
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = 1.0F;
         float var15 = 1.0F;
         if (var11 > var10) {
            float var16 = var10 / var11;
            var13 = (1.0F - var16) / 2.0F;
            var15 = 1.0F - var13;
         } else {
            float var17 = var11 / var10;
            var12 = (1.0F - var17) / 2.0F;
            var14 = 1.0F - var12;
         }

         this.helper7(context, tex, var6, var7, var8, var9, var12, var13, var14, var15, 8.0F, helper21(-1, alpha));
      }
   }

   private void helper7(
      DrawContext context, Identifier tex, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float radius, int color
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Matrix4f var13 = context.getMatrices().peek().getPositionMatrix();
      ShaderProgram var14 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedTexture);
      GlUniform var15 = var14.getUniform("Size");
      GlUniform var16 = var14.getUniform("Radius");
      GlUniform var17 = var14.getUniform("Smoothness");
      GlUniform var18 = var14.getUniform("ColorModulator");
      if (var15 != null) {
         var15.set(w, h);
      }

      if (var16 != null) {
         var16.set(radius, radius, radius, radius);
      }

      if (var17 != null) {
         var17.set(0.5F);
      }

      if (var18 != null) {
         var18.set(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RenderSystem.setShaderTexture(0, tex);
      RenderSystem.setShader(ShaderUtils.roundedTexture);
      int var19 = color >> 24 & 0xFF;
      if (var19 == 0) {
         var19 = 255;
      }

      float var20 = (color >> 16 & 0xFF) / 255.0F;
      float var21 = (color >> 8 & 0xFF) / 255.0F;
      float var22 = (color & 0xFF) / 255.0F;
      float var23 = var19 / 255.0F;
      BufferBuilder var24 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var24.vertex(var13, x, y, 0.0F).texture(u1, v1).color(var20, var21, var22, var23);
      var24.vertex(var13, x, y + h, 0.0F).texture(u1, v2).color(var20, var21, var22, var23);
      var24.vertex(var13, x + w, y + h, 0.0F).texture(u2, v2).color(var20, var21, var22, var23);
      var24.vertex(var13, x + w, y, 0.0F).texture(u2, v1).color(var20, var21, var22, var23);
      BufferRenderer.drawWithGlobalProgram(var24.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.disableBlend();
   }

   private float helper8(DrawContext context, int mouseX, int mouseY, float r, float top, float dt) {
      float var9 = 170.0F;
      float var10 = 32.0F;
      float var11 = this.width / 2.0F;
      float var12 = var11 - var9 / 2.0F;
      float var13 = top;
      this.updateState(context, var12, var13, var9, var10);
      if (!GlobalProfileAvatar.drawRounded(context, var12 + 7.0F, var13 + 5.0F, 22.0F, 22.0F, 11.0F, helper21(-1, (int)(255.0F * r)))) {
         Font var14 = helper19("icon", 14);
         if (var14 != null) {
            var14.draw(context.getMatrices(), "e", var12 + 12.0F, var13 + 8.0F, helper21(helper20(), (int)(255.0F * r)));
         }
      }

      Font var47 = helper19("sf_regular", 10);
      if (var47 != null) {
         var47.drawCenteredString(
            context.getMatrices(), "Выбранный аккаунт", var12 + var9 / 2.0F, var13 + 3.0F, helper21(-1, (int)(150.0F * r))
         );
      }

      Font var48 = helper19("suisse", 14);
      if (var48 != null) {
         var48.drawCenteredString(
            context.getMatrices(),
            this.helper24(var48, this.helper15(), var9 - 40.0F),
            var12 + var9 / 2.0F,
            var13 + 15.0F,
            helper21(-1, (int)(255.0F * r))
         );
      }

      float var15 = var13 + var10 + 6.0F;
      float var16 = 18.0F;
      helper22(this.volume3, var12, var15, var9, var16);
      boolean var17 = helper23(mouseX, mouseY, this.volume3) && !this.flag3;
      this.animationUtils5.update(var17 ? 1.0F : 0.0F);
      float var18 = this.animationUtils5.getValue();
      float var19 = 1.5F * var18;
      this.updateState(context, var12, var15, var9, var16);
      RenderUtils.drawBlur(
         context.getMatrices(),
         var12 - var19,
         var15 - var19,
         var9 + var19 * 2.0F,
         var16 + var19 * 2.0F,
         5.0F,
         helper21(helper20(), (int)(200.0F + 45.0F * var18))
      );
      Font var20 = helper19("sf_regular", 10);
      if (var20 != null) {
         var20.drawCenteredString(
            context.getMatrices(), "Сменить аккаунт", var12 + var9 / 2.0F, var15 + 2.0F + var16 / 2.0F - 3.5F, helper21(-1, (int)((150.0F + 105.0F * var18) * r))
         );
      }

      return var15 + var16;
   }

   private float helper9(DrawContext context, int mouseX, int mouseY, float r, float top, float dt) {
      float var7 = 16.0F;
      float var8 = 150.0F;
      float var9 = var7;
      float var10 = 7.0F;
      float var11 = var9 + var10 + var8;
      float var12 = this.width / 2.0F - var11 / 2.0F;
      float var13 = top;
      helper22(this.volume7, var12, var13, var9, var7);
      boolean var14 = helper23(mouseX, mouseY, this.volume7);
      this.animationUtils4.update(var14 ? 1.0F : 0.0F);
      float var15 = this.animationUtils4.getValue();
      if (var14) {
         this.volume += dt * 160.0F;
      }

      this.updateState(context, var12, var13, var9, var7);
      Font var16 = helper19("icon", 12);
      if (var16 != null) {
         MatrixStack var17 = context.getMatrices();
         float var18 = var12 + var9 / 2.0F;
         float var19 = var13 + var7 / 2.0F;
         var17.push();
         var17.translate(var18, var19, 0.0F);
         var17.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.volume));
         float var20 = 1.0F + 0.1F * var15;
         var17.scale(var20, var20, 1.0F);
         var17.translate(-var18, -var19, 0.0F);
         var16.drawCenteredString(
            var17, "h", var18, var13 + 2.0F + var7 / 2.0F - 5.0F + 2.0F, helper21(EXIT_RED, (int)(220.0F + 35.0F * var15))
         );
         var17.pop();
      }

      float var23 = var12 + var9 + var10;
      helper22(this.volume5, var23, var13, var8, var7);
      this.updateState(context, var23, var13, var8, var7);
      float var24 = var7 + (var8 - var7) * this.volume2;
      RenderUtils.drawRoundedRect(
         context.getMatrices(),
         var23,
         var13,
         var24,
         var7,
         var7 / 2.0F,
         helper21(-1, (int)((90.0F + 120.0F * this.volume2) * r))
      );
      Font var25 = helper19("sf_regular", 9);
      if (var25 != null) {
         var25.drawCenteredString(
            context.getMatrices(),
            "Протяни, чтобы выйти",
            var23 + var8 / 2.0F,
            var13 + 2.0F + var7 / 2.0F - 3.0F,
            helper21(EXIT_RED, (int)((150.0F - 90.0F * this.volume2) * r))
         );
      }

      float var26 = var7 - 4.0F;
      float var21 = var23 + 2.0F + (var8 - var7) * this.volume2;
      float var22 = var13 + 2.0F;
      helper22(this.volume6, var21, var22, var26, var26);
      RenderUtils.drawRoundedRect(
         context.getMatrices(),
         var21 - 2.0F,
         var22 - 2.0F,
         var26 + 4.0F,
         var26 + 4.0F,
         (var26 + 4.0F) / 2.0F,
         helper21(-1, (int)((55.0F + 120.0F * this.volume2) * r))
      );
      RenderUtils.drawRoundedRect(context.getMatrices(), var21, var22, var26, var26, var26 / 2.0F, helper21(helper20(), (int)(255.0F * r)));
      return var13 + var7;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0) {
         if (helper23(mouseX, mouseY, this.volume6)
            || helper23(mouseX, mouseY, this.volume5) && this.volume2 > 0.0F) {
            this.flag2 = true;
            this.helper10(mouseX);
            return true;
         }

         if (helper23(mouseX, mouseY, this.volume7)) {
            helper25();
            this.client.setScreen(new OptionsScreen(this, this.client.options));
            return true;
         }

         if (helper23(mouseX, mouseY, this.volume8)) {
            helper25();
            this.client.setScreen(new SelectWorldScreen(this));
            return true;
         }

         if (helper23(mouseX, mouseY, this.volume9)) {
            helper25();
            this.client.setScreen(new MultiplayerScreen(this));
            return true;
         }

         for (int var6 = 0; var6 < this.value.length; var6++) {
            if (helper23(mouseX, mouseY, this.value[var6])) {
               this.helper16(var6);
               return true;
            }
         }

         if (helper23(mouseX, mouseY, this.volume3)) {
            helper25();
            this.client.setScreen(new AccountGuiScreen(this));
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.flag2) {
         this.helper10(mouseX);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.flag2) {
         this.flag2 = false;
         if (this.volume2 >= 0.97F) {
            this.flag3 = true;
            this.client.scheduleStop();
         }

         return true;
      } else {
         return super.mouseReleased(mouseX, mouseY, button);
      }
   }

   private void helper10(double mouseX) {
      float var3 = this.volume5[0] + 2.5F;
      float var4 = this.volume5[2] - this.volume5[3];
      this.volume2 = MathHelper.clamp((float)(mouseX - var3) / Math.max(1.0F, var4), 0.0F, 1.0F);
      if (this.volume2 >= 0.97F && !this.flag3) {
         this.flag3 = true;
         this.client.scheduleStop();
      }
   }

   private String helper14() {
      try {
         return this.client.getSession().getUsername();
      } catch (Exception var2) {
         return "Player";
      }
   }

   private String helper15() {
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.globalSocialManager != null) {
         String var1 = Lumen.INSTANCE.globalSocialManager.getDisplayName();
         if (var1 != null && !var1.isBlank()) {
            return var1;
         }
      }

      return this.helper14();
   }

   private void helper16(int i) {
      helper25();
      if (i >= 0 && i < STRING.length && STRING[i] != null && !STRING[i].isBlank()) {
         try {
            Util.getOperatingSystem().open(STRING[i]);
         } catch (Exception var3) {
         }
      }
   }

   private static float helper17(float reveal, float start, float len) {
      return helper18(Easings.CUBIC_OUT, (reveal - start) / len);
   }

   private static float helper18(Easing e, float x) {
      return (float)e.ease(MathHelper.clamp(x, 0.0F, 1.0F));
   }

   private static Font helper19(String name, int size) {
      return Fonts.getFont(name, size);
   }

   private static int helper20() {
      try {
         return ColorUtils.getThemeColor();
      } catch (Exception var1) {
         return ColorUtils.rgb(106, 145, 255);
      }
   }

   private static int resolveInt(int i) {
      try {
         return ColorUtils.getThemeColor(i);
      } catch (Exception var2) {
         return helper20();
      }
   }

   private static int helper21(int color, int a) {
      return ColorUtils.setAlphaColor(color, MathHelper.clamp(a, 1, 255));
   }

   private static void helper22(float[] r, float x, float y, float w, float h) {
      r[0] = x;
      r[1] = y;
      r[2] = w;
      r[3] = h;
   }

   private static boolean helper23(double mx, double my, float[] r) {
      return HoveringUtils.isHovered(mx, my, r[0], r[1], r[2], r[3]);
   }

   private String helper24(Font f, String s, float maxW) {
      if (s == null) {
         return "";
      }

      if (f.getWidth(s) <= maxW) {
         return s;
      }

      while (s.length() > 0 && f.getWidth(s + "…") > maxW) {
         s = s.substring(0, s.length() - 1);
      }

      return s + "…";
   }

   private static void helper25() {
      try {
         ClientSoundPlayer.playGuiClick();
      } catch (Exception var1) {
      }
   }
}
