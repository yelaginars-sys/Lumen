package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.Locale;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class CalculatorHUD extends InterfaceProcessing {
   private static final float VOLUME = 108.0F;
   private static final float VOLUME2 = 5.0F;
   private static final float VOLUME3 = 26.0F;
   private static final float VOLUME4 = 2.0F;
   private static final int INDEX = 4;
   private static final int INDEX2 = 5;
   private static final float VOLUME5 = 23.0F;
   private static final float VOLUME6 = 12.0F;
   private static final float VOLUME7 = 106.0F;
   private static final String[] animationUtils = new String[]{
      "C", "(", ")", "/", "7", "8", "9", "*", "4", "5", "6", "-", "1", "2", "3", "+", "0", ".", "<", "="
   };
   private final AnimationUtils[] animationUtils2 = new AnimationUtils[animationUtils.length];
   private boolean flag;
   private String text = "";
   private String text2 = "0";
   private boolean flag2;

   public CalculatorHUD(Draggable draggable) {
      super(draggable);

      for (int var2 = 0; var2 < this.animationUtils2.length; var2++) {
         this.animationUtils2[var2] = new AnimationUtils(0.0F, 12.0F, Easings.QUAD_OUT);
      }
   }

   private Font helper(int size) {
      return Fonts.getFont("inter_medium", size);
   }

   private static int resolveInt() {
      return !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtils.getThemeColor();
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      Font var2 = this.helper(8);
      Font var3 = this.helper(13);
      Font var4 = this.helper(9);
      if (var2 != null && var3 != null && var4 != null) {
         float var5 = this.draggable.getX();
         float var6 = this.draggable.getY();
         int var7 = resolveInt();
         MatrixStack var8 = eventRender.getContext().getMatrices();
         drawHudBg(var8, var5, var6, 108.0F, 106.0F);
         this.helper2(var8, var2, var3, var5, var6, var7);
         this.helper3(var8, var4, var5, var6, var7);
         this.draggable.setWidth(108.0F);
         this.draggable.setHeight(106.0F);
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }

   private void helper2(MatrixStack matrices, Font expressionFont, Font resultFont, float x, float y, int accent) {
      float var7 = x + 5.0F;
      float var8 = y + 5.0F;
      float var9 = 98.0F;
      RenderUtils.drawRoundedRect(matrices, var7, var8, var9, 26.0F, 3.0F, ColorUtils.rgba(255, 255, 255, 14));
      RenderUtils.drawRoundedRectOutline(
         matrices,
         var7,
         var8,
         var9,
         26.0F,
         3.0F,
         0.45F,
         ColorUtils.replAlpha(accent, 55),
         ColorUtils.replAlpha(accent, 55),
         ColorUtils.replAlpha(accent, 55),
         ColorUtils.replAlpha(accent, 55)
      );
      String var10 = this.text.isEmpty() ? " " : this.text;
      expressionFont.drawStringWithFade(matrices, var10, var7 + 4.0F, var8 + 6.0F, var9 - 8.0F, ColorUtils.rgba(160, 166, 178, 255));
      int var11 = this.flag2 ? ColorUtils.rgba(235, 96, 96, 255) : ColorUtils.rgba(255, 255, 255, 255);
      float var12 = resultFont.getWidth(this.text2);
      float var13 = var12 > var9 - 8.0F ? var7 + 4.0F : var7 + var9 - 4.0F - var12;
      resultFont.drawString(matrices, this.text2, var13, var8 + 16.0F, var11);
   }

   private void helper3(MatrixStack matrices, Font keyFont, float x, float y, int accent) {
      double var6 = this.helper11();
      double var8 = this.helper12();

      for (int var10 = 0; var10 < animationUtils.length; var10++) {
         float var11 = helper4(x, var10);
         float var12 = helper5(y, var10);
         boolean var13 = mc.currentScreen != null && HoveringUtils.isHovered(var6, var8, var11, var12, 23.0, 12.0);
         this.animationUtils2[var10].update(var13 ? 1.0F : 0.0F);
         float var14 = MathHelper.clamp(this.animationUtils2[var10].getValue(), 0.0F, 1.0F);
         String var15 = animationUtils[var10];
         boolean var16 = helper6(var15) || var15.equals("=");
         int var17 = var16 ? ColorUtils.replAlpha(accent, 40 + (int)(60.0F * var14)) : ColorUtils.rgba(255, 255, 255, 12 + (int)(26.0F * var14));
         RenderUtils.drawRoundedRect(matrices, var11, var12, 23.0F, 12.0F, 2.5F, var17);
         if (var14 > 0.01F) {
            int var18 = ColorUtils.replAlpha(accent, (int)(120.0F * var14));
            RenderUtils.drawRoundedRectOutline(matrices, var11, var12, 23.0F, 12.0F, 2.5F, 0.5F, var18, var18, var18, var18);
         }

         int var19 = var15.equals("C") ? ColorUtils.rgba(235, 120, 120, 255) : ColorUtils.rgba(255, 255, 255, 235);
         keyFont.drawCenteredString(matrices, var15, var11 + 11.5F, var12 + 6.0F + 1.5F - keyFont.getSize() * 0.25F, var19);
      }
   }

   private static float helper4(float x, int index) {
      return x + 5.0F + index % 4 * 25.0F;
   }

   private static float helper5(float y, int index) {
      return y + 5.0F + 26.0F + 2.0F + index / 4 * 14.0F;
   }

   private static boolean helper6(String label) {
      return label.length() == 1 && "+-*/".contains(label);
   }

   public void blur() {
      this.flag = false;
   }

   public boolean handleClick(double mouseX, double mouseY, int button) {
      if (this.draggable.getWidth() <= 1.0F) {
         this.flag = false;
         return false;
      }

      float var6 = this.draggable.getX();
      float var7 = this.draggable.getY();
      if (!HoveringUtils.isHovered(mouseX, mouseY, var6, var7, 108.0, 106.0)) {
         this.flag = false;
         return false;
      }

      if (button != 0) {
         return false;
      }

      this.flag = true;

      for (int var8 = 0; var8 < animationUtils.length; var8++) {
         if (HoveringUtils.isHovered(mouseX, mouseY, helper4(var6, var8), helper5(var7, var8), 23.0, 12.0)) {
            this.helper7(animationUtils[var8]);
            return true;
         }
      }

      return false;
   }

   public boolean handleKey(int keyCode) {
      if (keyCode == 256) {
         this.flag = false;
         return false;
      }

      if (this.flag && !(this.draggable.getWidth() <= 1.0F)) {
         switch (keyCode) {
            case 257:
            case 335:
               this.helper7("=");
               break;
            case 259:
               this.helper7("<");
               break;
            case 261:
               this.helper7("C");
               break;
            default:
               return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean handleChar(char character) {
      if (this.flag && !(this.draggable.getWidth() <= 1.0F)) {
         switch (character) {
            case '(':
            case ')':
            case '*':
            case '+':
            case '-':
            case '/':
               this.helper7(String.valueOf(character));
               break;
            case ',':
               this.helper7(".");
               break;
            case '.':
            default:
               if (character >= '0' && character <= '9' || character == '.') {
                  this.helper7(String.valueOf(character));
               }
         }

         return true;
      } else {
         return false;
      }
   }

   private void helper7(String key) {
      switch (key) {
         case "C":
            this.text = "";
            this.text2 = "0";
            this.flag2 = false;
            break;
         case "<":
            if (!this.text.isEmpty()) {
               this.text = this.text.substring(0, this.text.length() - 1);
            }

            this.flag2 = false;
            break;
         case "=":
            this.helper9();
            break;
         default:
            if (this.flag2) {
               this.text = "";
               this.flag2 = false;
            }

            this.text = this.text + key;
      }

      if (!key.equals("=") && !this.flag2) {
         this.helper8();
      }
   }

   private void helper8() {
      if (this.text.isEmpty()) {
         this.text2 = "0";
      } else {
         try {
            this.text2 = helper10(new CalculatorHUD.ExpressionParser(this.text).helper());
         } catch (Exception var2) {
         }
      }
   }

   private void helper9() {
      if (!this.text.isEmpty()) {
         try {
            double var1 = new CalculatorHUD.ExpressionParser(this.text).helper();
            this.text2 = helper10(var1);
            this.text = this.text2;
            this.flag2 = false;
         } catch (Exception var3) {
            this.text2 = "Ошибка";
            this.flag2 = true;
         }
      }
   }

   private static String helper10(double value) {
      if (!Double.isFinite(value)) {
         return "Ошибка";
      }

      if (value == Math.rint(value) && Math.abs(value) < 1.0E15) {
         return String.valueOf((long)value);
      }

      String var2 = String.format(Locale.ROOT, "%.8f", value);
      return var2.replaceAll("0+$", "").replaceAll("\\.$", "");
   }

   private double helper11() {
      return mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth();
   }

   private double helper12() {
      return mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight();
   }

   private static final class ExpressionParser {
      private final String VOLUME;
      private int VOLUME2;

      private ExpressionParser(String input) {
         this.VOLUME = input.replace(" ", "");
      }

      private double helper() {
         double var1 = this.resolveInt();
         if (this.VOLUME2 < this.VOLUME.length()) {
            throw new IllegalArgumentException("лишний символ на позиции " + this.VOLUME2);
         } else {
            return var1;
         }
      }

      private double resolveInt() {
         double var1 = this.helper2();

         while (this.VOLUME2 < this.VOLUME.length()) {
            char var3 = this.VOLUME.charAt(this.VOLUME2);
            if (var3 != '+' && var3 != '-') {
               break;
            }

            this.VOLUME2++;
            double var4 = this.helper2();
            var1 = var3 == '+' ? var1 + var4 : var1 - var4;
         }

         return var1;
      }

      private double helper2() {
         double var1 = this.helper3();

         while (this.VOLUME2 < this.VOLUME.length()) {
            char var3 = this.VOLUME.charAt(this.VOLUME2);
            if (var3 != '*' && var3 != '/') {
               break;
            }

            this.VOLUME2++;
            double var4 = this.helper3();
            if (var3 == '/' && var4 == 0.0) {
               throw new ArithmeticException("деление на ноль");
            }

            var1 = var3 == '*' ? var1 * var4 : var1 / var4;
         }

         return var1;
      }

      private double helper3() {
         if (this.VOLUME2 < this.VOLUME.length() && this.VOLUME.charAt(this.VOLUME2) == '-') {
            this.VOLUME2++;
            return -this.helper3();
         }

         if (this.VOLUME2 < this.VOLUME.length() && this.VOLUME.charAt(this.VOLUME2) == '+') {
            this.VOLUME2++;
         }

         return this.helper4();
      }

      private double helper4() {
         if (this.VOLUME2 >= this.VOLUME.length()) {
            throw new IllegalArgumentException("выражение обрывается");
         }

         if (this.VOLUME.charAt(this.VOLUME2) == '(') {
            this.VOLUME2++;
            double var3 = this.resolveInt();
            if (this.VOLUME2 < this.VOLUME.length() && this.VOLUME.charAt(this.VOLUME2) == ')') {
               this.VOLUME2++;
               return var3;
            } else {
               throw new IllegalArgumentException("не закрыта скобка");
            }
         } else {
            int var1 = this.VOLUME2;

            while (
               this.VOLUME2 < this.VOLUME.length()
                  && (Character.isDigit(this.VOLUME.charAt(this.VOLUME2)) || this.VOLUME.charAt(this.VOLUME2) == '.')
            ) {
               this.VOLUME2++;
            }

            if (var1 == this.VOLUME2) {
               throw new IllegalArgumentException("ожидалось число на позиции " + this.VOLUME2);
            } else {
               return Double.parseDouble(this.VOLUME.substring(var1, this.VOLUME2));
            }
         }
      }
   }
}
