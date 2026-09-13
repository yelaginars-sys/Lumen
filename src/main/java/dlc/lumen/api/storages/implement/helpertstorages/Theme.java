package dlc.lumen.api.storages.implement.helpertstorages;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import lombok.Generated;

public class Theme implements QClient {
   private String alphaColor;
   public int[] color;
   private boolean animated;

   public Theme(String name, int... color) {
      this(name, "Rainbow".equals(name), color);
   }

   public Theme(String name, boolean animated, int... color) {
      this.alphaColor = name;
      this.animated = animated;
      this.color = color;
   }

   public int getColor(int index) {
      if (this.alphaColor.equals("Rainbow") && this.animated) {
         return ColorUtils.rainbow(10, index, 0.6F, 1.0F, 1.0F);
      } else if (this.color != null && this.color.length != 0) {
         return this.color.length == 1 ? this.color[0] : ColorUtils.gradient(5, index, this.color);
      } else {
         return ColorUtils.rgba(255, 255, 255, 255);
      }
   }

   public void setColor(int index, int value) {
      if (index >= 0) {
         if (this.color == null || this.color.length <= index) {
            int[] var3 = new int[Math.max(index + 1, 2)];
            if (this.color != null) {
               System.arraycopy(this.color, 0, var3, 0, this.color.length);
            }

            for (int var4 = 0; var4 < var3.length; var4++) {
               if (var3[var4] == 0) {
                  var3[var4] = ColorUtils.rgba(255, 255, 255, 255);
               }
            }

            this.color = var3;
         }

         this.color[index] = ColorUtils.setAlphaColor(value, 255);
      }
   }

   @Generated
   public String getName() {
      return this.alphaColor;
   }

   @Generated
   public int[] getColor() {
      return this.color;
   }

   @Generated
   public boolean isAnimated() {
      return this.animated;
   }

   @Generated
   public void setName(String name) {
      this.alphaColor = name;
   }

   @Generated
   public void setColor(int[] color) {
      this.color = color;
   }

   @Generated
   public void setAnimated(boolean animated) {
      this.animated = animated;
   }
}
