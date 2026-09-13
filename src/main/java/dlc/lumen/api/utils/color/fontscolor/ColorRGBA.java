package dlc.lumen.api.utils.color.fontscolor;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.MathUtils;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class ColorRGBA {
   public static final ColorRGBA WHITE = new ColorRGBA(255, 255, 255);
   public static final ColorRGBA BLACK = new ColorRGBA(0, 0, 0);
   public static final ColorRGBA GREEN = new ColorRGBA(0, 255, 0);
   public static final ColorRGBA RED = new ColorRGBA(255, 0, 0);
   public static final ColorRGBA BLUE = new ColorRGBA(0, 0, 255);
   public static final ColorRGBA YELLOW = new ColorRGBA(255, 255, 0);
   public static final ColorRGBA GRAY = new ColorRGBA(88, 87, 93);
   public static final ColorRGBA TRANSPARENT = new ColorRGBA(0, 0, 0, 0);
   private transient float[] brightness;
   private final int red;
   private final int green;
   private final int blue;
   private final int alpha;
   private static final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);

   public ColorRGBA(int color) {
      this(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), ColorUtils.alpha(color));
   }

   public ColorRGBA(Color color) {
      this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
   }

   public ColorRGBA(int red, int green, int blue) {
      this(red, green, blue, 255);
   }

   public ColorRGBA(int red, int green, int blue, int alpha) {
      red = MathHelper.clamp(red, 0, 255);
      green = MathHelper.clamp(green, 0, 255);
      blue = MathHelper.clamp(blue, 0, 255);
      alpha = MathHelper.clamp(alpha, 0, 255);
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
   }

   public ColorRGBA(int red, int green, int blue, float alpha) {
      red = MathHelper.clamp(red, 0, 255);
      green = MathHelper.clamp(green, 0, 255);
      blue = MathHelper.clamp(blue, 0, 255);
      alpha = MathHelper.clamp(alpha, 0.0F, 255.0F);
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = (int)alpha;
   }

   public int getRGB() {
      int var1 = Math.round(this.calcValue(this.alpha));
      int var2 = Math.round(this.calcValue(this.red));
      int var3 = Math.round(this.calcValue(this.green));
      int var4 = Math.round(this.calcValue(this.blue));
      return (var1 & 0xFF) << 24 | (var2 & 0xFF) << 16 | (var3 & 0xFF) << 8 | var4 & 0xFF;
   }

   private int calcValue(float value) {
      return (int)Math.max(0.0F, Math.min(255.0F, value));
   }

   public static ColorRGBA fromHex(String hex) {
      String var1 = hex.startsWith("#") ? hex.substring(1) : hex;
      if (var1.length() != 6 && var1.length() != 8) {
         throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
      }

      int var2 = Integer.parseInt(var1.substring(0, 2), 16);
      int var3 = Integer.parseInt(var1.substring(2, 4), 16);
      int var4 = Integer.parseInt(var1.substring(4, 6), 16);
      int var5 = var1.length() == 8 ? Integer.parseInt(var1.substring(6, 8), 16) : 255;
      return new ColorRGBA(var2, var3, var4, var5);
   }

   public static ColorRGBA lerp(ColorRGBA startColor, ColorRGBA endColor, float delta) {
      float var3 = Math.max(0.0F, Math.min(1.0F, delta));
      int var4 = (int)(startColor.getRed() + (endColor.getRed() - startColor.getRed()) * var3);
      int var5 = (int)(startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * var3);
      int var6 = (int)(startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * var3);
      int var7 = (int)(startColor.getAlpha() + (endColor.getAlpha() - startColor.getAlpha()) * var3);
      return new ColorRGBA(var4, var5, var6, var7);
   }

   public static ColorRGBA fromInt(int colorInt) {
      int var1 = colorInt >> 24 & 0xFF;
      int var2 = colorInt >> 16 & 0xFF;
      int var3 = colorInt >> 8 & 0xFF;
      int var4 = colorInt & 0xFF;
      return new ColorRGBA(var2, var3, var4, var1);
   }

   public ColorRGBA withAlpha(float newAlpha) {
      return new ColorRGBA(this.red, this.green, this.blue, (int)newAlpha);
   }

   public ColorRGBA withAlpha(int newAlpha) {
      return new ColorRGBA(this.red, this.green, this.blue, newAlpha);
   }

   public ColorRGBA mulAlpha(float percent) {
      return this.withAlpha((int)(this.alpha * percent));
   }

   public ColorRGBA mix(ColorRGBA color2, float amount) {
      amount = Math.min(1.0F, Math.max(0.0F, amount));
      return new ColorRGBA(
         (int)MathUtils.interpolate(this.getRed(), color2.getRed(), amount),
         (int)MathUtils.interpolate(this.getGreen(), color2.getGreen(), amount),
         (int)MathUtils.interpolate(this.getBlue(), color2.getBlue(), amount),
         (int)MathUtils.interpolate(this.getAlpha(), color2.getAlpha(), amount)
      );
   }

   public ColorRGBA darker(float amount) {
      amount = MathHelper.clamp(amount, 0.0F, 1.0F);
      return new ColorRGBA(
         (int)(this.red * (1.0F - amount)),
         (int)(this.green * (1.0F - amount)),
         (int)(this.blue * (1.0F - amount)),
         this.alpha
      );
   }

   public static ColorRGBA fromHSB(float hue, float saturation, float brightness) {
      if (saturation == 0.0F) {
         int var11 = (int)(brightness * 255.0F + 0.5F);
         return new ColorRGBA(var11, var11, var11);
      }

      float var3 = (hue - (float)Math.floor(hue)) * 6.0F;
      float var4 = var3 - (float)Math.floor(var3);
      float var5 = brightness * (1.0F - saturation);
      float var6 = brightness * (1.0F - saturation * var4);
      float var7 = brightness * (1.0F - saturation * (1.0F - var4));
      float var8 = 0.0F;
      float var9 = 0.0F;
      float var10 = 0.0F;
      switch ((int)var3) {
         case 0:
            var8 = brightness;
            var9 = var7;
            var10 = var5;
            break;
         case 1:
            var8 = var6;
            var9 = brightness;
            var10 = var5;
            break;
         case 2:
            var8 = var5;
            var9 = brightness;
            var10 = var7;
            break;
         case 3:
            var8 = var5;
            var9 = var6;
            var10 = brightness;
            break;
         case 4:
            var8 = var7;
            var9 = var5;
            var10 = brightness;
            break;
         case 5:
            var8 = brightness;
            var9 = var5;
            var10 = var6;
      }

      return new ColorRGBA((int)(var8 * 255.0F), (int)(var9 * 255.0F), (int)(var10 * 255.0F));
   }

   public float getHue() {
      return this.hue()[0];
   }

   public float getSaturation() {
      return this.hue()[2];
   }

   public float getBrightness() {
      return this.hue()[1];
   }

    private float[] hue() {
       if (this.brightness == null) {
          this.brightness = this.computeHSB();
       }

       return this.brightness;
    }

    private float[] computeHSB() {
      float var1 = this.red / 255.0F;
      float var2 = this.green / 255.0F;
      float var3 = this.blue / 255.0F;
      float var4 = Math.max(var1, Math.max(var2, var3));
      float var5 = Math.min(var1, Math.min(var2, var3));
      float var6 = var4 - var5;
      float var7 = 0.0F;
      if (var6 != 0.0F) {
         if (var4 == var1) {
            var7 = (var2 - var3) / var6;
         } else if (var4 == var2) {
            var7 = (var3 - var1) / var6 + 2.0F;
         } else {
            var7 = (var1 - var2) / var6 + 4.0F;
         }

         var7 /= 6.0F;
         if (var7 < 0.0F) {
            var7++;
         }
      }

      float var8 = var4 == 0.0F ? 0.0F : var6 / var4;
      return new float[]{var7, var8, var4};
   }

   public ColorRGBA brighter(float amount) {
      amount = MathHelper.clamp(amount, 0.0F, 1.0F);
      return new ColorRGBA(
         (int)(this.red + (255.0F - this.red) * amount),
         (int)(this.green + (255.0F - this.green) * amount),
         (int)(this.blue + (255.0F - this.blue) * amount),
         this.alpha
      );
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ColorRGBA var2 = (ColorRGBA)o;
         return Float.compare(this.red, var2.red) == 0
            && Float.compare(this.green, var2.green) == 0
            && Float.compare(this.blue, var2.blue) == 0
            && Float.compare(this.alpha, var2.alpha) == 0;
      } else {
         return false;
      }
   }

   public float difference(ColorRGBA colorRGBA) {
      return Math.abs(this.getHue() - colorRGBA.getHue())
         + Math.abs(this.getBrightness() - colorRGBA.getBrightness())
         + Math.abs(this.getSaturation() - colorRGBA.getSaturation());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.red, this.green, this.blue, this.alpha);
   }

   @Generated
   public int getRed() {
      return this.red;
   }

   @Generated
   public int getGreen() {
      return this.green;
   }

   @Generated
   public int getBlue() {
      return this.blue;
   }

   @Generated
   public int getAlpha() {
      return this.alpha;
   }
}
