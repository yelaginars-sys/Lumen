package dlc.lumen.client.ui.modern;

import dlc.lumen.api.storages.implement.ClientColors;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import java.awt.Color;

public final class ModernTheme {
   public static final int[] ACCENTS = new int[]{
      ColorUtils.rgba(177, 140, 255, 255),
      ColorUtils.rgba(125, 217, 250, 255),
      ColorUtils.rgba(222, 155, 53, 255),
      ColorUtils.rgba(75, 105, 255, 255),
      ColorUtils.rgba(226, 87, 76, 255),
      ColorUtils.rgba(105, 205, 130, 255)
   };
   private static int index2;
   private static float volume;
   public static final int DANGER = ColorUtils.rgba(214, 106, 128, 255);
   public static final float FONT_SCALE = 1.3F;
   private static boolean animations2 = true;
   private static boolean shadow2 = true;

   private ModernTheme() {
   }

   public static int accent() {
      return ClientColors.outline();
   }

   public static int accentIndex() {
      int var0 = ColorUtils.replAlpha(accent(), 255);

      for (int var1 = 0; var1 < ACCENTS.length; var1++) {
         if (ColorUtils.replAlpha(ACCENTS[var1], 255) == var0) {
            return var1;
         }
      }

      return -1;
   }

   public static void setAccentIndex(int index) {
      int var1 = ACCENTS[Math.floorMod(index, ACCENTS.length)];
      ClientColors.set(ClientColors.Slot.OUTLINE, var1);
      ClientColors.set(ClientColors.Slot.ICON, var1);
      ClientColors.set(ClientColors.Slot.TOGGLE, var1);
   }

   private static float helper() {
      int var0 = accent();
      if (index2 != var0) {
         float[] var1 = Color.RGBtoHSB(ColorUtils.r(var0), ColorUtils.g(var0), ColorUtils.b(var0), null);
         volume = var1[0];
         index2 = var0;
      }

      return volume;
   }

   private static int resolveInt(float saturation, float brightness) {
      int var2 = Color.HSBtoRGB(helper(), saturation, brightness);
      return ColorUtils.rgba(var2 >> 16 & 0xFF, var2 >> 8 & 0xFF, var2 & 0xFF, 255);
   }

   public static int BG() {
      return resolveInt(0.3F, 0.105F);
   }

   public static int BG_BORDER() {
      return resolveInt(0.26F, 0.29F);
   }

   public static int SIDEBAR() {
      return resolveInt(0.34F, 0.075F);
   }

   public static int LINE() {
      return resolveInt(0.22F, 0.24F);
   }

   public static int LINE_SOFT() {
      return resolveInt(0.22F, 0.27F);
   }

   public static int CARD() {
      return resolveInt(0.26F, 0.175F);
   }

   public static int CARD_HOVER() {
      return resolveInt(0.25F, 0.235F);
   }

   public static int CARD_ON() {
      return resolveInt(0.32F, 0.225F);
   }

   public static int CARD_ON_HOVER() {
      return resolveInt(0.32F, 0.285F);
   }

   public static int CARD_BORDER() {
      return resolveInt(0.22F, 0.275F);
   }

   public static int CARD_BORDER_ON() {
      return resolveInt(0.3F, 0.39F);
   }

   public static int PANEL() {
      return resolveInt(0.28F, 0.15F);
   }

   public static int FIELD() {
      return resolveInt(0.29F, 0.155F);
   }

   public static int PILL() {
      return resolveInt(0.25F, 0.22F);
   }

   public static int PILL_HOVER() {
      return resolveInt(0.26F, 0.29F);
   }

   public static int NAV_ACTIVE() {
      return resolveInt(0.27F, 0.25F);
   }

   public static int NAV_HOVER() {
      return resolveInt(0.24F, 0.2F);
   }

   public static int TRACK() {
      return resolveInt(0.25F, 0.27F);
   }

   public static int TOGGLE_OFF() {
      return resolveInt(0.26F, 0.31F);
   }

   public static int TEXT() {
      return resolveInt(0.05F, 0.95F);
   }

   public static int TEXT_SOFT() {
      return resolveInt(0.08F, 0.89F);
   }

   public static int TEXT_DIM() {
      return resolveInt(0.14F, 0.71F);
   }

   public static int TEXT_MUTED() {
      return resolveInt(0.18F, 0.54F);
   }

   public static int TEXT_FAINT() {
      return resolveInt(0.2F, 0.44F);
   }

   public static int ICON_IDLE() {
      return resolveInt(0.16F, 0.59F);
   }

   public static int KNOB_OFF() {
      return resolveInt(0.16F, 0.59F);
   }

   public static boolean animations() {
      return animations2;
   }

   public static void setAnimations(boolean value) {
      animations2 = value;
   }

   public static boolean shadow() {
      return shadow2;
   }

   public static void setShadow(boolean value) {
      shadow2 = value;
   }

   public static float radius(float base) {
      return base * (0.35F + 0.65F * ClickGuiTheme.radiusMul());
   }

   public static float R_PANEL() {
      return radius(8.0F);
   }

   public static float R_CARD() {
      return radius(7.0F);
   }

   public static float R_ROW() {
      return radius(6.5F);
   }

   public static float R_PILL() {
      return radius(5.0F);
   }

   public static float R_SMALL() {
      return radius(3.5F);
   }

   public static int DARK_BLUR_TINT() {
      return resolveInt(0.24F, 0.42F);
   }

   public static int LIGHT_BLUR_TINT() {
      return resolveInt(0.1F, 0.66F);
   }

   public static float windowOpacity() {
      return 0.55F + 0.33F * ClickGuiTheme.opacity();
   }

   public static float surfaceAlpha() {
      return 0.62F + 0.3F * ClickGuiTheme.opacity();
   }

   public static float blur() {
      return 3.5F + ClickGuiTheme.blurMul() * 1.6F;
   }
}
