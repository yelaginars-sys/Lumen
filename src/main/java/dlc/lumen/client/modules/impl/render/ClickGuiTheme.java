package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.utils.color.ColorUtils;

public final class ClickGuiTheme {
   private static final int INDEX = ColorUtils.rgba(125, 217, 250, 255);
   private static float radiusMul2 = 0.75F;
   private static float opacity2 = 0.75F;
   private static float blur = 2.5F;
   private static float density2 = 1.1F;
   private static final float VOLUME = 2.5F;
   private static boolean animations = false;
   private static boolean glass2 = true;
   private static boolean hudGlass2 = false;
   private static boolean shadow = true;

   private ClickGuiTheme() {
   }

   public static int accent() {
      return INDEX;
   }

   public static float radiusMul() {
      return radiusMul2;
   }

   public static void setRadiusMul(float value) {
      radiusMul2 = helper(value, 0.0F, 1.0F);
   }

   public static float radius(float base) {
      return base * radiusMul2;
   }

   public static float opacity() {
      return opacity2;
   }

   public static void setOpacity(float value) {
      opacity2 = helper(value, 0.1F, 1.0F);
   }

   public static float blurMul() {
      return blur;
   }

   public static void setBlur(float value) {
      blur = helper(value, 0.0F, 8.0F);
   }

   public static float density() {
      return density2;
   }

   public static void setDensity(float value) {
      density2 = helper(value, 0.5F, 2.0F);
   }

   public static boolean animationsEnabled() {
      return animations;
   }

   public static void setAnimations(boolean value) {
      animations = value;
   }

   public static boolean glass() {
      return glass2;
   }

   public static void setGlass(boolean value) {
      glass2 = value;
   }

   public static boolean hudGlass() {
      return hudGlass2;
   }

   public static void setHudGlass(boolean value) {
      hudGlass2 = value;
   }

   public static boolean shadowEnabled() {
      return shadow;
   }

   public static void setShadow(boolean value) {
      shadow = value;
   }

   public static float animSpeedMul() {
      return animations ? 2.5F : 100.0F;
   }

   private static float helper(float v, float min, float max) {
      return v < min ? min : (v > max ? max : v);
   }
}
