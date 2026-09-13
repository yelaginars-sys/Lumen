package dlc.lumen.client.modules.impl.render.base;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.client.ui.modern.ModernTheme;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class InterfaceProcessing implements QClient {
   protected static final float HUD_DEFAULT_PANEL_RADIUS = 6.0F;
   protected static final float HUD_DEFAULT_PANEL_CORNER_SMOOTHNESS = 2.0F;
   protected static final float HUD_ISLAND_PANEL_RADIUS = 8.0F;
   protected static final float HUD_ISLAND_PANEL_RADIUS_GROWTH = 11.0F;
   protected static final float HUD_ISLAND_PANEL_CORNER_SMOOTHNESS = 2.0F;
   protected static final float HUD_ISLAND_PANEL_CORNER_SMOOTHNESS_GROWTH = 5.0F;
   public final Draggable draggable;
   private boolean unusualRectType2 = true;
   private boolean flag;
   private long timestamp;
   private long timestamp2 = -1L;
   public static final float STRIP_ICON_W = 13.0F;
   public static final float STRIP_ROW_H = 12.0F;
   public static final float STRIP_PAD_V = 2.0F;
   public static final float STRIP_PAD = 5.0F;
   public static final float STRIP_ICON_SLOT = 11.0F;
   public static final float STRIP_CAP_GAP = 5.0F;
   private static final float VOLUME = 8.0F;

   public void updateAppearState() {
      long var1 = System.nanoTime();
      if (var1 - this.timestamp > 400000000L) {
         this.flag = false;
      }

      this.timestamp = var1;
      boolean var3 = this.draggable.getWidth() > 2.0F && this.draggable.getHeight() > 2.0F;
      if (var3 && !this.flag) {
         this.timestamp2 = var1;
      } else if (!var3) {
         this.timestamp2 = -1L;
      }

      this.flag = var3;
   }

   public float appearProgress(float durationSeconds) {
      if (this.timestamp2 < 0L) {
         return 1.0F;
      }

      float var2 = (float)(System.nanoTime() - this.timestamp2) / 1.0E9F / Math.max(durationSeconds, 0.05F);
      return MathHelper.clamp(var2, 0.0F, 1.0F);
   }

   protected int bossBarCount() {
      return 0;
   }

   protected float hudTopOffset() {
      int var1 = this.bossBarCount();
      return var1 > 0 ? 12.0F + var1 * 19.0F : 4.0F;
   }

   public boolean isUnusualRectType() {
      return this.unusualRectType2;
   }

   public void setUnusualRectType(boolean unusualRectType) {
      this.unusualRectType2 = unusualRectType;
   }

   public void onUpdate(EventUpdate eventUpdate) {
   }

   public void onRender(EventRender.Default eventRender) {
   }

   protected static float animateProgress(AnimationUtils animation, float target) {
      float var2 = MathHelper.clamp(target, 0.0F, 1.0F);
      animation.update(var2);
      return MathHelper.clamp(animation.getValue(), 0.0F, 1.0F);
   }

   protected static float animateTrailingProgress(AnimationUtils animation, float leadProgress, float riseLerp) {
      float var3 = MathHelper.clamp(leadProgress, 0.0F, 1.0F);
      float var4 = MathHelper.clamp(animation.getValue(), 0.0F, 1.0F);
      if (var3 > var4) {
         animation.setValue(MathHelper.lerp(MathHelper.clamp(riseLerp, 0.0F, 1.0F), var4, var3));
      } else {
         animation.update(var3);
      }

      return MathHelper.clamp(animation.getValue(), 0.0F, 1.0F);
   }

   public static void drawHudBg(MatrixStack m, float x, float y, float w, float h) {
      drawHudBg(m, x, y, w, h, 1.0F, false);
   }

   public static void drawHudBg2(MatrixStack m, float x, float y, float w, float h) {
      drawHudBg2(m, x, y, w, h, 1.0F, false);
   }

   public static void drawHudCardBg(MatrixStack m, float x, float y, float w, float h, float alpha) {
      drawHudBg(m, x, y, w, h, alpha, true);
   }

   public static void drawHudBg(MatrixStack m, float x, float y, float w, float h, float alpha, boolean card) {
      helper(m, x, y, w, h, alpha, 0.0F, card, false, 6.0F, 0.0F, 2.0F, 0.0F);
   }

   public static void drawHudBg2(MatrixStack m, float x, float y, float w, float h, float alpha, boolean card) {
      helper(m, x, y, w, h, alpha, 0.0F, card, true, 6.0F, 0.0F, 2.0F, 0.0F);
   }

   protected static void drawIslandHudPanel(MatrixStack m, float x, float y, float w, float h, float alpha, float morph, boolean card, boolean leftShade) {
      helper(m, x, y, w, h, alpha, morph, card, leftShade, 8.0F, 11.0F, 2.0F, 5.0F);
   }

   private static void helper(
      MatrixStack m,
      float x,
      float y,
      float w,
      float h,
      float alpha,
      float morph,
      boolean card,
      boolean leftShade,
      float baseRadius,
      float radiusGrowth,
      float baseCornerSmoothness,
      float cornerSmoothnessGrowth
   ) {
      int var13 = (int)(255.0F * MathHelper.clamp(alpha, 0.0F, 1.0F));
      float var14 = x + 0.25F;
      float var15 = y;
      float var16 = Math.max(0.0F, w - 0.5F);
      float var17 = Math.max(0.0F, h);
      if (var13 > 0 && !(var16 <= 0.5F) && !(var17 <= 0.5F)) {
         float var18 = MathHelper.clamp(morph, 0.0F, 1.0F);
         float var19 = baseRadius + radiusGrowth * var18;
         float var20 = baseCornerSmoothness + cornerSmoothnessGrowth * var18;
         boolean var21 = false;
         if (ModuleClass.INSTANCE != null && ModuleClass.interfaceModule != null) {
            var21 = "Minimalism".equals(ModuleClass.interfaceModule.style.getCurrent());
         }

         int var22 = ColorUtils.replAlpha(ColorUtils.getThemeColor(), var13);
         int var23 = ColorUtils.replAlpha(ColorUtils.clientBackground(), Math.min(var13, 164));
         int var24 = ColorUtils.replAlpha(ColorUtils.clientBackground(), Math.min(var13, (card ? 92 : 78) + (int)(var18 * 16.0F)));
         int var25 = ColorUtils.rgba(255, 255, 255, Math.min(var13, 40 + (int)(var18 * 16.0F)));
         if (var21) {
            var24 = ColorUtils.rgba(0, 0, 0, 180);
            var25 = ColorUtils.rgba(255, 255, 255, Math.min(var13, 70 + (int)(var18 * 20.0F)));
         }

         if (!var21) {
            RenderUtils.drawBlur(m, var14, var15, var16, var17, var19, 5.0F, var22);
            RenderUtils.drawBlur(m, var14, var15, var16, var17, var19, 5.0F, var23);
         }

         RenderUtils.drawHudLiquidGlass(
            m,
            var14 - 1.0F,
            var15 - 1.0F,
            var16 + 2.0F,
            var17 + 2.0F,
            var19,
            var19,
            var19,
            var19,
            ColorUtils.rgba(255, 255, 255, var13),
            1.0F,
            32.0F,
            ModernTheme.accent(),
            0.88F,
            true,
            0.0F,
            0.06F,
            var20,
            false
         );
         if (leftShade) {
            float var26 = Math.min(16.0F, Math.max(0.0F, var16 - 2.0F));
            if (var26 > 1.0F) {
               RenderUtils.drawBlur(m, var14 + 1.0F, var15, var26, var17, Math.min(var19, baseRadius), 5.0F, ColorUtils.rgba(0, 0, 0, 45));
            }
         }

         RenderUtils.drawRoundedRect(m, var14, var15, var16, var17, var19, var24);
         RenderUtils.drawRoundedRectOutline(m, var14, var15, var16, var17, var19, 0.45F, var25, var25, var25, var25);
      }
   }

   public static float stripContentX(float x) {
      return x + 13.0F + 1.0F;
   }

   public static float stripWidth(float widestRow) {
      return 14.0F + widestRow;
   }

   public static float stripHeight(float rowsHeight) {
      return 4.0F + rowsHeight;
   }

   public static float stripTextY(float rowY, float fontSize) {
      return rowY + 6.0F + 1.5F - fontSize * 0.25F;
   }

   public static void drawStrip(MatrixStack m, Font iconFont, String glyph, float x, float y, float w, float h, int accent) {
      drawStrip(m, iconFont, glyph, x, y, w, h, accent, 0.0F);
   }

   public static void drawStrip(MatrixStack m, Font iconFont, String glyph, float x, float y, float w, float h, int accent, float dividerInset) {
      drawHudBg2(m, x, y, w, h);
      iconFont.drawCenteredString(m, glyph, x + 2.0F + 6.5F, y + 1.0F + h / 2.0F + 1.5F - iconFont.getSize() * 0.25F, ColorUtils.clientIcon());
   }

   public static void drawStripSeparator(MatrixStack m, float contentX, float rowY, float rowsW, float alpha) {
      int var5 = (int)(42.0F * MathHelper.clamp(alpha, 0.0F, 1.0F));
      if (var5 > 0) {
         ;
      }
   }

   public static float stripCapWidth(Font f, String text) {
      return Math.max(f.getWidth(text) + 6.0F, 11.0F);
   }

   public static void drawStripCap(MatrixStack m, Font f, String text, float x, float rowY, int accent, int alpha) {
      float var7 = stripCapWidth(f, text);
      float var8 = rowY + 2.0F;
      int var9 = ColorUtils.setAlphaColor(accent, alpha);
      RenderUtils.drawRoundedRectOutline(m, x, var8 - 0.2F, var7, 8.0F, 2.0F, 0.5F, var9, var9, var9, var9);
      f.drawCenteredString(m, text, x + var7 / 2.0F, stripTextY(rowY + 0.1F, f.getSize()), ColorUtils.replAlpha(ColorUtils.clientText(), alpha));
   }

   @Generated
   public InterfaceProcessing(Draggable draggable) {
      this.draggable = draggable;
   }
}
