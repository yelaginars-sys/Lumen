package dlc.lumen.api.utils.color;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.storages.implement.ClientColors;
import dlc.lumen.api.utils.math.MathUtils;
import java.awt.Color;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ColorUtils {
   public static final Color green = new Color(36, 218, 118);
   public static final Color yellow = new Color(255, 196, 67);
   public static final Color orange = new Color(255, 134, 0);
   public static final Color red = new Color(239, 72, 54);
   public static final Color Blues = new Color(125, 217, 250);

   public static int red(int c) {
      return c >> 16 & 0xFF;
   }

   public static int green(int c) {
      return c >> 8 & 0xFF;
   }

   public static float redf(int c) {
      return red(c) / 255.0F;
   }

   public static float greenf(int c) {
      return green(c) / 255.0F;
   }

   public static float bluef(int c) {
      return blue(c) / 255.0F;
   }

   public static float alphaf(int c) {
      return alpha(c) / 255.0F;
   }

   public static int getColor(int brightness, int alpha) {
      return getColor(brightness, brightness, brightness, alpha);
   }

   public static int gradient(int color1, int color2, float amount) {
      amount = MathHelper.clamp(amount, 0.0F, 1.0F);
      int var3 = MathHelper.lerp(amount, red(color1), red(color2));
      int var4 = MathHelper.lerp(amount, green(color1), green(color2));
      int var5 = MathHelper.lerp(amount, blue(color1), blue(color2));
      int var6 = MathHelper.lerp(amount, alpha(color1), alpha(color2));
      return rgba(var3, var4, var5, var6);
   }

   public static int toColor(String hexColor) {
      if (hexColor != null && hexColor.length() == 7 && hexColor.startsWith("#")) {
         try {
            int var1 = Integer.parseInt(hexColor.substring(1), 16);
            return 0xFF000000 | var1;
         } catch (NumberFormatException var2) {
            return -16777216;
         }
      } else {
         return -16777216;
      }
   }

   public static int applyAlpha(int color, float alphaMul) {
      int var2 = color >> 24 & 0xFF;
      int var3 = (int)(var2 * Math.max(0.0F, Math.min(1.0F, alphaMul)));
      return color & 16777215 | var3 << 24;
   }

   public static int r(int color) {
      return color >> 16 & 0xFF;
   }

   public static int g(int color) {
      return color >> 8 & 0xFF;
   }

   public static int b(int color) {
      return color & 0xFF;
   }

   public static int a(int color) {
      return color >> 24 & 0xFF;
   }

   public static int hexToRgb(String hex) {
      if (hex.startsWith("#")) {
         hex = hex.substring(1);
      }

      if (hex.length() != 6) {
         throw new IllegalArgumentException("Недопустимый формат HEX: " + hex);
      }

      int var1 = Integer.parseInt(hex.substring(0, 2), 16);
      int var2 = Integer.parseInt(hex.substring(2, 4), 16);
      int var3 = Integer.parseInt(hex.substring(4, 6), 16);
      return rgb(var1, var2, var3);
   }

   public static int getThemeColor() {
      return ClientColors.outline();
   }

   public static int getThemeColor(int index) {
      int var1 = ClientColors.outline();
      return index <= 0 ? var1 : darken(var1, index == 1 ? 0.65F : Math.max(0.2F, 0.65F - index * 0.12F));
   }

   public static int getThemeStaticColor() {
      return ClientColors.raw(ClientColors.Slot.OUTLINE);
   }

   public static int clientAccent() {
      return ClientColors.outline();
   }

   public static int clientOutline() {
      return ClientColors.outline();
   }

   public static int clientBackground() {
      return ClientColors.background();
   }

   public static int clientElementBg() {
      return ClientColors.element();
   }

   public static int clientText() {
      return ClientColors.text();
   }

   public static int clientTextSecondary() {
      return ClientColors.textSecondary();
   }

   public static int clientIcon() {
      return ClientColors.icon();
   }

   public static int clientToggle() {
      return ClientColors.toggle();
   }

   public static int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
      int var5 = (int)((System.currentTimeMillis() / speed + index) % 360L);
      float var6 = var5 / 360.0F;
      int var7 = Color.HSBtoRGB(var6, saturation, brightness);
      return getColor(red(var7), green(var7), blue(var7), Math.max(0, Math.min(255, (int)(opacity * 255.0F))));
   }

   public static int interpolate(int color1, int color2, double amount) {
      amount = Math.max(0.0, Math.min(1.0, amount));
      int var4 = color1 >> 16 & 0xFF;
      int var5 = color1 >> 8 & 0xFF;
      int var6 = color1 & 0xFF;
      int var7 = color1 >> 24 & 0xFF;
      int var8 = color2 >> 16 & 0xFF;
      int var9 = color2 >> 8 & 0xFF;
      int var10 = color2 & 0xFF;
      int var11 = color2 >> 24 & 0xFF;
      int var12 = (int)(var4 + (var8 - var4) * amount);
      int var13 = (int)(var5 + (var9 - var5) * amount);
      int var14 = (int)(var6 + (var10 - var6) * amount);
      int var15 = (int)(var7 + (var11 - var7) * amount);
      return var15 << 24 | var12 << 16 | var13 << 8 | var14;
   }

   public static int[] genGradientForText(int color1, int color2, int length) {
      int[] var3 = new int[length];

      for (int var4 = 0; var4 < length; var4++) {
         double var5 = (double)var4 / (length - 1);
         var3[var4] = interpolate(color1, color2, var5);
      }

      return var3;
   }

   public static int blue(int c) {
      return c & 0xFF;
   }

   public static int overCol(int c1, int c2, float pc01) {
      return getColor(
         red(c1) * (1.0F - pc01) + red(c2) * pc01,
         green(c1) * (1.0F - pc01) + green(c2) * pc01,
         blue(c1) * (1.0F - pc01) + blue(c2) * pc01,
         alpha(c1) * (1.0F - pc01) + alpha(c2) * pc01
      );
   }

   public static int darken(int color, float factor) {
      int var2 = color >> 24 & 0xFF;
      int var3 = Math.max(0, Math.min(255, (int)((color >> 16 & 0xFF) * factor)));
      int var4 = Math.max(0, Math.min(255, (int)((color >> 8 & 0xFF) * factor)));
      int var5 = Math.max(0, Math.min(255, (int)((color & 0xFF) * factor)));
      return var2 << 24 | var3 << 16 | var4 << 8 | var5;
   }

   public static int multDark(int c, float brpc) {
      return getColor(red(c) * brpc, green(c) * brpc, blue(c) * brpc, alpha(c));
   }

   public static int overCol(int c1, int c2) {
      return overCol(c1, c2, 0.5F);
   }

   public static int alpha(int c) {
      return c >> 24 & 0xFF;
   }

   public static int multAlpha(int c, float apc) {
      return getColor(red(c), green(c), blue(c), alpha(c) * apc);
   }

   public static int replAlpha(int color, int alpha) {
      alpha = Math.max(0, Math.min(255, alpha));
      return alpha << 24 | color & 16777215;
   }

   public static Color random() {
      return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.75 + Math.random() / 4.0), (float)(0.75 + Math.random() / 4.0)));
   }

   public static int getColor(float r, float g, float b, float a) {
      int var4 = Math.max(0, Math.min(255, (int)r));
      int var5 = Math.max(0, Math.min(255, (int)g));
      int var6 = Math.max(0, Math.min(255, (int)b));
      int var7 = Math.max(0, Math.min(255, (int)a));
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   public static float[] getRGBAf(int c) {
      return new float[]{redf(c), greenf(c), bluef(c), alphaf(c)};
   }

   public static float[] getRGBAf1(int c) {
      return new float[]{red(c) / 255.0F, green(c) / 255.0F, blue(c) / 255.0F, alpha(c) / 255.0F};
   }

   public static Color interpolateTwoColors(int speed, int index, Color start, Color end, boolean trueColor) {
      int var5 = 0;
      if (speed == 0) {
         var5 = index % 360;
      } else {
         var5 = (int)((System.currentTimeMillis() / speed + index) % 360L);
      }

      var5 = (var5 >= 180 ? 360 - var5 : var5) * 2;
      boolean var6 = trueColor;
      return var6 ? interpolateColorHue(start, end, var5 / 360.0F) : interpolateColorC(start, end, var5 / 360.0F);
   }

   public static Color interpolateTwoColors(int speed, int index, Color start, Color end) {
      return interpolateTwoColors(speed, index, start, end, false);
   }

   public static Color astolfo(float yDist, float yTotal, float saturation, float speedt) {
      float var4 = 1800.0F;
      float var5 = (float)(System.currentTimeMillis() % (int)var4) + (yTotal - yDist) * speedt;

      while (var5 > var4) {
         var5 -= var4;
      }

      var5 /= var4;
      if (var5 > 1.0F) {
         var5 = 1.0F - (var5 - 1.0F);
      }

      return Color.getHSBColor(++var5, saturation, 1.0F);
   }

   private static int calcDivisor(int divisor, int offset) {
      long var2 = System.currentTimeMillis();
      long var4 = (var2 / divisor + offset) % 360L;
      return (int)var4;
   }

   public static void setColor(Color color, float alpha) {
      float var2 = color.getRed() / 255.0F;
      float var3 = color.getGreen() / 255.0F;
      float var4 = color.getBlue() / 255.0F;
      RenderSystem.setShaderColor(var2, var3, var4, alpha);
   }

   public static int rgb(int r, int g, int b) {
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   public static int rgba(int r, int g, int b, int a) {
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static float[] rgba(int color) {
      return new float[]{(color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F};
   }

   public static int rgba(double r, double g, double b, double a) {
      return rgba((int)r, (int)g, (int)b, (int)a);
   }

   public static int getRed(int hex) {
      return hex >> 16 & 0xFF;
   }

   public static int getGreen(int hex) {
      return hex >> 8 & 0xFF;
   }

   public static int interpolate(int start, int end, float value) {
      float[] var3 = rgba(start);
      float[] var4 = rgba(end);
      return rgba(
         (int)MathUtils.interpolate(var3[0] * 255.0F, var4[0] * 255.0F, value),
         (int)MathUtils.interpolate(var3[1] * 255.0F, var4[1] * 255.0F, value),
         (int)MathUtils.interpolate(var3[2] * 255.0F, var4[2] * 255.0F, value),
         (int)MathUtils.interpolate(var3[3] * 255.0F, var4[3] * 255.0F, value)
      );
   }

   public static int interpolateColor(int color1, int color2, float amount) {
      amount = Math.min(1.0F, Math.max(0.0F, amount));
      int var3 = getRed(color1);
      int var4 = getGreen(color1);
      int var5 = getBlue(color1);
      int var6 = getAlpha(color1);
      int var7 = getRed(color2);
      int var8 = getGreen(color2);
      int var9 = getBlue(color2);
      int var10 = getAlpha(color2);
      int var11 = interpolateInt(var3, var7, amount);
      int var12 = interpolateInt(var4, var8, amount);
      int var13 = interpolateInt(var5, var9, amount);
      int var14 = interpolateInt(var6, var10, amount);
      return var14 << 24 | var11 << 16 | var12 << 8 | var13;
   }

   public static MutableText gradient(String message, int first, int end) {
      MutableText var3 = Text.empty();

      for (int var4 = 0; var4 < message.length(); var4++) {
         int var5 = interpolateColor(first, end, (float)var4 / message.length());
         MutableText var6 = Text.literal(String.valueOf(message.charAt(var4))).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(var5)));
         var3.append(var6);
      }

      return var3;
   }

   public static Text replace(Text original, String find, String replaceWith) {
      if (original != null && find != null && replaceWith != null) {
         String var3 = original.getString();
         String var4 = var3.replace(find, replaceWith);
         return Text.literal(var4);
      } else {
         return original;
      }
   }

   public static int gradient(int speed, int index, int... colors) {
      int var3 = (int)((System.currentTimeMillis() / speed + index) % 360L);
      var3 = (var3 > 180 ? 360 - var3 : var3) + 180;
      int var4 = (int)(var3 / 360.0F * colors.length);
      if (var4 == colors.length) {
         var4--;
      }

      int var5 = colors[var4];
      int var6 = colors[var4 == colors.length - 1 ? 0 : var4 + 1];
      return interpolateColor(var5, var6, var3 / 360.0F * colors.length - var4);
   }

   public static int themeGradient(int speed, int index, float darkenFactor) {
      int var3 = getThemeColor();
      return gradient(speed, index, var3, darken(var3, darkenFactor));
   }

   public static int getBlue(int hex) {
      return hex & 0xFF;
   }

   public static int getAlpha(int hex) {
      return hex >> 24 & 0xFF;
   }

   public static int getColor(int red, int green, int blue, int alpha) {
      int var4 = 0;
      var4 |= alpha << 24;
      var4 |= red << 16;
      var4 |= green << 8;
      return var4 | blue;
   }

   public static int getColor(int bright) {
      return getColor(bright, bright, bright, 255);
   }

   public static float[] getColorA(int color) {
      return new float[]{red(color) / 255.0F, green(color) / 255.0F, blue(color) / 255.0F, alphaf(color)};
   }

   public static float[] getColorT(int color) {
      return new float[]{red(color) / 255.0F, green(color) / 255.0F, blue(color) / 255.0F, alphaf(color)};
   }

   public static void setColor(double red, double green, double blue, double alpha) {
      GL11.glColor4d(red, green, blue, alpha);
   }

   public static int setAlphaColor(int color, int alpha) {
      return color & 16777215 | alpha << 24;
   }

   public static float lerp(float a, float b, float f) {
      return a + f * (b - a);
   }

   public static Color interpolateColorC(Color color1, Color color2, float amount) {
      amount = Math.min(1.0F, Math.max(0.0F, amount));
      return new Color(
         interpolateInt(color1.getRed(), color2.getRed(), amount),
         interpolateInt(color1.getGreen(), color2.getGreen(), amount),
         interpolateInt(color1.getBlue(), color2.getBlue(), amount),
         interpolateInt(color1.getAlpha(), color2.getAlpha(), amount)
      );
   }

   public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
      return oldValue + (newValue - oldValue) * interpolationValue;
   }

   public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
      return interpolate(oldValue, newValue, (float)interpolationValue).floatValue();
   }

   public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
      return interpolate(oldValue, newValue, (float)interpolationValue);
   }

   public static Color interpolateColorHue(Color color1, Color color2, float amount) {
      amount = Math.min(1.0F, Math.max(0.0F, amount));
      float[] var3 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
      float[] var4 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
      Color var5 = Color.getHSBColor(
         interpolateFloat(var3[0], var4[0], amount), interpolateFloat(var3[1], var4[1], amount), interpolateFloat(var3[2], var4[2], amount)
      );
      return new Color(var5.getRed(), var5.getGreen(), var5.getBlue(), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
   }

   public static void setColor(Color color) {
      if (color == null) {
         color = Color.white;
      }

      setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
   }

   public static void setColor(int color) {
      setColor(color, (color >> 24 & 0xFF) / 255.0F);
   }

   public static void setColor(int color, float alpha) {
      float var2 = (color >> 16 & 0xFF) / 255.0F;
      float var3 = (color >> 8 & 0xFF) / 255.0F;
      float var4 = (color & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(var2, var3, var4, alpha);
   }

   public static int applyOpacity(int color, float alpha) {
      return rgba(getRed(color), getGreen(color), getBlue(color), getAlpha(color) * alpha / 255.0F);
   }

   public static int reFactorColor(int color, float factor) {
      return rgba(extractRedf(color) * factor, extractGreenf(color) * factor, extractBluef(color) * factor, extractAlphaf(color));
   }

   public static float extractRedf(int color) {
      return (color >> 16 & 0xFF) / 255.0F;
   }

   public static int extractRed(int color) {
      return color >> 16 & 0xFF;
   }

   public static float extractBluef(int color) {
      return (color & 0xFF) / 255.0F;
   }

   public static int extractBlue(int color) {
      return color & 0xFF;
   }

   public static float extractGreenf(int color) {
      return (color >> 8 & 0xFF) / 255.0F;
   }

   public static int extractGreen(int color) {
      return color >> 8 & 0xFF;
   }

   public static float extractAlphaf(int color) {
      return (color >> 24 & 0xFF) / 255.0F;
   }

   public static int extractAlpha(int color) {
      return color >> 24 & 0xFF;
   }
}
