package dlc.lumen.client.ui.mainmenu;

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
   private final Map<String, AnimationUtils> strings = new HashMap<>();
   private float volume;
   private long timestamp = Util.getMeasuringTimeMs();
   private boolean flag;
   private final StringBuilder stringBuilder = new StringBuilder();
   private float volume2;
   private boolean flag2;
   private boolean flag3;
   private final float[] volume3 = new float[4];
   private final float[] volume4 = new float[4];
   private final float[] volume5 = new float[4];
   private final float[] volume6 = new float[4];
   private final float[] volume7 = new float[4];
   private static final String TEXT = "h";
   private final float[][] value = new float[][]{new float[4], new float[4], new float[4]};
   private final float[] volume8 = new float[4];
   private final float[] volume9 = new float[4];
   private final List<float[]> floats = new ArrayList<>();
   private final List<String> strings2 = new ArrayList<>();
   private static final int INDEX = 3;
   private int index = 0;
   private final float[] volume10 = new float[4];
   private final float[] volume11 = new float[4];
   private String text2 = null;
   private static boolean flag4 = false;

   private static float helper(int rows) {
      return 40.0F + rows * 22.0F;
   }

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
      this.helper2(context, var5);
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
      ArrayList var15 = new ArrayList<>(AccountGuiScreen.MANAGER);
      int var16 = Math.min(var15.size(), 3);
      float var17 = 32.0F + helper(var16) + 12.0F + 18.0F + 12.0F + 22.0F;
      float var18 = MathHelper.clamp(this.height * 0.5F - var17 / 2.0F + this.height * 0.035F, this.height * 0.16F, this.height - var17 - 8.0F);
      this.helper8(context, mouseX, mouseY, var13, var18 + (1.0F - var13) * 20.0F, var15, var16, var8);
      float var19 = var18 + 26.0F + 6.0F + helper(var16) + 12.0F;
      this.helper9(context, mouseX, mouseY, var14, var19 + (1.0F - var14) * 20.0F, var8);
      super.render(context, mouseX, mouseY, delta);
   }

   private void helper2(DrawContext context, float r) {
      context.draw();
      if (mc.getResourceManager().getResource(TEXTURE_ID).isPresent()) {
         RenderUtils.drawImage(context.getMatrices(), TEXTURE_ID, 0.0F, 0.0F, this.width, this.height, -1);
         RenderUtils.drawRoundedRect(
            context.getMatrices(), 0.0F, 0.0F, this.width, this.height, 0.0F, ColorUtils.setAlphaColor(ColorUtils.rgb(0, 0, 0), (int)(95.0F * r))
         );
      } else {
         MenuBackground.render(context, this.width, this.height);
      }
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
      float var7 = 24.0F * (0.9F + 0.1F * r);
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
      float var7 = 184.0F;
      float var8 = 124.0F;
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
            var17, var16[0] - 3.0F, var16[1] - 3.0F, var16[2] + 6.0F, var16[3] + 6.0F, 15.0F, helper21(helper20(), (int)(70.0F * var13 * r))
         );
      }

      this.updateState(context, var16[0], var16[1], var16[2], var16[3]);
      this.helper6(context, var16, bg, (int)((225.0F + 30.0F * var13) * r));
      if (var13 > 0.001F) {
         RenderUtils.drawRoundedRect(
            var17, var16[0] + 4.0F, var16[1] + 4.0F, var16[2] - 8.0F, var16[3] - 8.0F, 8.0F, helper21(helper20(), (int)(40.0F * var13 * r))
         );
      }

      Font var18 = helper19("suisse", 17);
      Font var19 = helper19("sf_regular", 11);
      float var20 = var16[0] + var16[2] / 2.0F;
      if (var18 != null) {
         var18.drawCenteredString(var17, title, var20, var16[1] + var16[3] - 34.0F, helper21(-1, (int)(255.0F * r)));
      }

      if (var19 != null) {
         var19.drawCenteredString(var17, sub, var20, var16[1] + var16[3] - 16.0F, helper21(helper20(), (int)(220.0F * r)));
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

   private float helper8(DrawContext context, int mouseX, int mouseY, float r, float top, List<Account> accounts, int rows, float dt) {
      float var9 = 200.0F;
      float var10 = 26.0F;
      float var11 = this.width / 2.0F;
      float var12 = var11 - var9 / 2.0F;
      float var13 = top;
      this.updateState(context, var12, var13, var9, var10);
      if (!GlobalProfileAvatar.drawRounded(context, var12 + 6.0F, var13 + 4.0F, 18.0F, 18.0F, 9.0F, helper21(-1, (int)(255.0F * r)))) {
         Font var14 = helper19("icon", 12);
         if (var14 != null) {
            var14.draw(context.getMatrices(), "e", var12 + 10.0F, var13 + 4.0F + var10 / 2.0F - 5.0F, helper21(helper20(), (int)(255.0F * r)));
         }
      }

      Font var47 = helper19("suisse", 13);
      if (var47 != null) {
         var47.draw(
            context.getMatrices(),
            this.helper24(var47, this.helper15(), var9 - 38.0F),
            var12 + 30.0F,
            var13 + 2.0F + var10 / 2.0F - 4.5F,
            helper21(-1, (int)(255.0F * r))
         );
      }

      float var15 = var13 + var10 + 6.0F;
      float var16 = helper(rows);
      this.updateState(context, var12, var15, var9, var16);
      Font var17 = helper19("sf_regular", 11);
      float var18 = var12 + 8.0F;
      float var19 = var15 + 8.0F;
      float var20 = var9 - 16.0F - 36.0F;
      float var21 = 20.0F;
      helper22(this.volume3, var18, var19, var20, var21);
      RenderUtils.drawBlur(context.getMatrices(), var18, var19, var20, var21, 5.0F, helper21(helper20(), 70));
      if (var17 != null) {
         String var22 = this.stringBuilder.length() == 0 && !this.flag
            ? "Введите ник…"
            : this.stringBuilder + (this.flag ? "|" : "");
         int var23 = this.stringBuilder.length() == 0 && !this.flag ? ColorUtils.rgba(255, 255, 255, 90) : -1;
         var17.draw(context.getMatrices(), this.helper24(var17, var22, var20 - 8.0F), var18 + 5.0F, var19 + 2.0F + var21 / 2.0F - 3.5F, var23);
      }

      float var48 = 32.0F;
      float var49 = var12 + var9 - 8.0F - var48;
      helper22(this.volume4, var49, var19, var48, var21);
      boolean var24 = helper23(mouseX, mouseY, this.volume4);
      this.animationUtils5.update(var24 ? 1.0F : 0.0F);
      float var25 = this.animationUtils5.getValue();
      float var26 = 1.5F * var25;
      RenderUtils.drawBlur(
         context.getMatrices(),
         var49 - var26,
         var19 - var26,
         var48 + var26 * 2.0F,
         var21 + var26 * 2.0F,
         5.0F,
         helper21(helper20(), (int)(200.0F + 45.0F * var25))
      );
      if (var17 != null) {
         var17.drawCenteredString(context.getMatrices(), "+", var49 + var48 / 2.0F, var19 + 2.0F + var21 / 2.0F - 3.5F, -1);
      }

      int var27 = accounts.size();
      int var28 = Math.max(0, var27 - rows);
      this.index = MathHelper.clamp(this.index, 0, var28);
      float var29 = var19 + var21 + 6.0F;
      helper22(this.volume10, var12 + 8.0F, var29, var9 - 16.0F, rows * 22.0F - 2.0F);
      this.floats.clear();
      this.strings2.clear();
      this.text2 = null;
      helper22(this.volume11, 0.0F, 0.0F, 0.0F, 0.0F);
      float var30 = var29;

      for (int var31 = 0; var31 < rows; var31++) {
         int var32 = this.index + var31;
         if (var32 >= var27) {
            break;
         }

         Account var33 = (Account)accounts.get(var32);
         float var34 = var9 - 16.0F - (var27 > rows ? 6.0F : 0.0F);
         float[] var35 = new float[]{var12 + 8.0F, var30, var34, 20.0F};
         boolean var36 = helper23(mouseX, mouseY, var35);
         boolean var37 = var33.name().equalsIgnoreCase(this.helper14());
         AnimationUtils var38 = this.strings.computeIfAbsent(var33.name(), n -> new AnimationUtils(0.0F, 10.0F, Easings.CUBIC_OUT));
         var38.update(var36 ? 1.0F : 0.0F);
         float var39 = var38.getValue();
         int var40 = var37 ? 200 : (int)(60.0F + 70.0F * var39);
         RenderUtils.drawBlur(context.getMatrices(), var35[0], var35[1], var35[2], var35[3], 5.0F, helper21(helper20(), var40));
         float var41 = Math.max(var37 ? 1.0F : 0.0F, var39);
         if (var41 > 0.001F) {
            float var42 = (var35[3] - 6.0F) * var41;
            RenderUtils.drawRoundedRect(
               context.getMatrices(),
               var35[0] + 1.5F,
               var35[1] + (var35[3] - var42) / 2.0F,
               2.0F,
               var42,
               1.0F,
               helper21(helper20(), (int)(235.0F * (var37 ? 1.0F : var41)))
            );
         }

         float var59 = var35[1] + 2.0F + var35[3] / 2.0F - 3.5F;
         float var43 = var35[0] + 7.0F + 3.0F * var39;
         float var44 = var35[2] - 12.0F - (var36 ? 16.0F : 0.0F);
         if (var17 != null) {
            var17.draw(
               context.getMatrices(),
               this.helper24(var17, var33.name(), var44),
               var43,
               var59,
               var37 ? helper20() : helper21(-1, (int)(200.0F + 40.0F * var39))
            );
         }

         if (var36) {
            this.text2 = var33.name();
            float var45 = var35[0] + var35[2] - 13.0F;
            helper22(this.volume11, var45 - 6.0F, var35[1], 15.0F, var35[3]);
            boolean var46 = helper23(mouseX, mouseY, this.volume11);
            if (var17 != null) {
               var17.draw(context.getMatrices(), "x", var45, var59, ColorUtils.rgba(255, var46 ? 90 : 130, var46 ? 90 : 130, var46 ? 255 : 210));
            }
         }

         this.floats.add(var35);
         this.strings2.add(var33.name());
         var30 += 22.0F;
      }

      if (!this.strings.isEmpty()) {
         HashSet var50 = new HashSet();

         for (Account var54 : accounts) {
            var50.add(var54.name());
         }

         this.strings.keySet().retainAll(var50);
      }

      if (var27 > rows) {
         float var51 = var12 + var9 - 8.0F - 3.0F;
         float var53 = var29;
         float var55 = rows * 22.0F - 2.0F;
         float var56 = 3.0F;
         RenderUtils.drawRoundedRect(context.getMatrices(), var51, var53, var56, var55, 1.5F, ColorUtils.rgba(255, 255, 255, 30));
         float var57 = var55 * rows / var27;
         float var58 = var53 + (var55 - var57) * (var28 == 0 ? 0.0F : (float)this.index / var28);
         RenderUtils.drawRoundedRect(context.getMatrices(), var51, var58, var56, var57, 1.5F, helper21(helper20(), 220));
      }

      return var15 + var16;
   }

   private float helper9(DrawContext context, int mouseX, int mouseY, float r, float top, float dt) {
      float var7 = 18.0F;
      float var8 = 178.0F;
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
            var17, "h", var18, var13 + 2.0F + var7 / 2.0F - 5.0F + 2.0F, helper21(helper20(), (int)(220.0F + 35.0F * var15))
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
         helper21(helper20(), (int)((90.0F + 120.0F * this.volume2) * r))
      );
      Font var25 = helper19("sf_regular", 9);
      if (var25 != null) {
         var25.drawCenteredString(
            context.getMatrices(),
            "Протяни, чтобы выйти",
            var23 + var8 / 2.0F,
            var13 + 2.0F + var7 / 2.0F - 3.0F,
            helper21(-1, (int)((150.0F - 90.0F * this.volume2) * r))
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
         helper21(helper20(), (int)((55.0F + 120.0F * this.volume2) * r))
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
            this.flag = true;
            return true;
         }

         if (helper23(mouseX, mouseY, this.volume4)) {
            this.helper11(this.stringBuilder.toString());
            return true;
         }

         if (this.text2 != null && helper23(mouseX, mouseY, this.volume11)) {
            this.helper13(this.text2);
            helper25();
            return true;
         }

         for (int var7 = 0; var7 < this.floats.size(); var7++) {
            if (helper23(mouseX, mouseY, this.floats.get(var7))) {
               this.helper12(this.strings2.get(var7));
               helper25();
               return true;
            }
         }

         this.flag = false;
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
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (helper23(mouseX, mouseY, this.volume10)) {
         int var9 = AccountGuiScreen.MANAGER.size();
         int var10 = Math.max(0, var9 - 3);
         this.index = MathHelper.clamp(this.index - (int)Math.signum(verticalAmount), 0, var10);
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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

   @Override
   public boolean charTyped(char chr, int modifiers) {
      if (this.flag && !Character.isISOControl(chr) && this.stringBuilder.length() < 24) {
         this.stringBuilder.append(chr);
         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.flag) {
         if (keyCode == 259) {
            if (this.stringBuilder.length() > 0) {
               this.stringBuilder.deleteCharAt(this.stringBuilder.length() - 1);
            }

            return true;
         }

         if (keyCode == 257 || keyCode == 335) {
            this.helper11(this.stringBuilder.toString());
            return true;
         }

         if (keyCode == 256) {
            this.flag = false;
            return true;
         }
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   private void helper11(String name) {
      if (name != null) {
         name = name.trim();
         if (!name.isEmpty()) {
            if (!AccountGuiScreen.MANAGER.isAccount(name)) {
               AccountGuiScreen.MANAGER.addAccount(new Account(LocalDateTime.now(), name));
            }

            this.helper12(name);
            this.stringBuilder.setLength(0);
            this.flag = false;
         }
      }
   }

   private void helper12(String name) {
      AccountGuiScreen.MANAGER.saveLastSelected(name);
      AccountGuiScreen.MANAGER.restoreLastSession();
      AccountGuiScreen.MANAGER.save();
   }

   private void helper13(String name) {
      AccountGuiScreen.MANAGER.removeAccount(name);
      AccountGuiScreen.MANAGER.save();
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
         MinecraftClient var0 = MinecraftClient.getInstance();
         var0.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } catch (Exception var1) {
      }
   }
}
