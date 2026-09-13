package dlc.lumen.api.utils.chat;

import dlc.lumen.client.modules.impl.misc.NameProtect;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;

public final class ChatInputRenderer implements BiFunction<String, Integer, OrderedText> {
   private static final long timeMs = 170L;
   private static final int count = 2830392;
   private static final int count2 = 14737632;
   private static final char ch = '█';
   private final TextFieldWidget field;
   private final BiFunction<String, Integer, OrderedText> delegate;
   private String text2 = "";
   private long[] timeMsArray = new long[0];

   public ChatInputRenderer(TextFieldWidget field, BiFunction<String, Integer, OrderedText> delegate) {
      this.field = field;
      this.delegate = delegate;
   }

   public OrderedText apply(String visible, Integer firstCharacterIndex) {
      if (this.field != null && visible != null && !visible.isEmpty()) {
         String var3 = this.field.getText();
         if (var3 == null) {
            return this.text(visible, firstCharacterIndex);
         }

         this.handleText(var3);
         NameProtect var4 = NameProtect.INSTANCE;
         List var5 = var4.isEnable() ? var4.chatMaskRanges(var3) : List.of();
         long var6 = System.currentTimeMillis();
         int var8 = firstCharacterIndex == null ? 0 : firstCharacterIndex;
         if (var5.isEmpty() && !this.checkLength(visible.length(), var8, var6)) {
            return this.text(visible, firstCharacterIndex);
         }

         ArrayList var9 = new ArrayList();
         StringBuilder var10 = new StringBuilder();
         int var11 = Integer.MIN_VALUE;

         for (int var12 = 0; var12 < visible.length(); var12++) {
            int var13 = var8 + var12;
            boolean var14 = checkRanges(var5, var13);
            int var15 = var14 ? 0 : this.calcIndex(var13, var6);
            char var16 = var14 ? '█' : visible.charAt(var12);
            if (var10.length() > 0 && var15 != var11) {
               var9.add(getText(var10.toString(), var11));
               var10.setLength(0);
            }

            var11 = var15;
            var10.append(var16);
         }

         if (var10.length() > 0) {
            var9.add(getText(var10.toString(), var11));
         }

         return OrderedText.innerConcat(var9);
      } else {
         return this.text(visible, firstCharacterIndex);
      }
   }

   private OrderedText text(String visible, Integer firstCharacterIndex) {
      return this.delegate != null
         ? this.delegate.apply(visible, firstCharacterIndex)
         : OrderedText.styledForwardsVisitedString(visible == null ? "" : visible, Style.EMPTY);
   }

   private static OrderedText getText(String text, int rgb) {
      return OrderedText.styledForwardsVisitedString(text, Style.EMPTY.withColor(TextColor.fromRgb(rgb)));
   }

   private boolean checkLength(int length, int offset, long now) {
      for (int var5 = 0; var5 < length; var5++) {
         int var6 = offset + var5;
         if (var6 >= 0 && var6 < this.timeMsArray.length && now - this.timeMsArray[var6] < 170L) {
            return true;
         }
      }

      return false;
   }

   private int calcIndex(int index, long now) {
      if (index >= 0 && index < this.timeMsArray.length) {
         float var4 = MathHelper.clamp((float)(now - this.timeMsArray[index]) / 170.0F, 0.0F, 1.0F);
         if (var4 >= 1.0F) {
            return 14737632;
         }

         float var5 = 1.0F - (1.0F - var4) * (1.0F - var4);
         return calcFrom(2830392, 14737632, var5);
      } else {
         return 14737632;
      }
   }

   private static int calcFrom(int from, int to, float t) {
      int var3 = MathHelper.lerp(t, from >> 16 & 0xFF, to >> 16 & 0xFF);
      int var4 = MathHelper.lerp(t, from >> 8 & 0xFF, to >> 8 & 0xFF);
      int var5 = MathHelper.lerp(t, from & 0xFF, to & 0xFF);
      return var3 << 16 | var4 << 8 | var5;
   }

   private void handleText(String text) {
      if (!text.equals(this.text2)) {
         long var2 = System.currentTimeMillis();
         int var4 = Math.min(this.text2.length(), text.length());
         int var5 = 0;

         while (var5 < var4 && this.text2.charAt(var5) == text.charAt(var5)) {
            var5++;
         }

         int var6 = 0;

         while (var6 < var4 - var5 && this.text2.charAt(this.text2.length() - 1 - var6) == text.charAt(text.length() - 1 - var6)) {
            var6++;
         }

         long[] var7 = new long[text.length()];

         for (int var8 = 0; var8 < var5 && var8 < this.timeMsArray.length; var8++) {
            var7[var8] = this.timeMsArray[var8];
         }

         for (int var10 = 0; var10 < var6; var10++) {
            int var9 = this.text2.length() - 1 - var10;
            if (var9 >= 0 && var9 < this.timeMsArray.length) {
               var7[text.length() - 1 - var10] = this.timeMsArray[var9];
            } else {
               var7[text.length() - 1 - var10] = var2;
            }
         }

         for (int var11 = var5; var11 < text.length() - var6; var11++) {
            var7[var11] = var2;
         }

         this.timeMsArray = var7;
         this.text2 = text;
      }
   }

   private static boolean checkRanges(List<int[]> ranges, int index) {
      for (int[] var3 : ranges) {
         if (index >= var3[0] && index < var3[1]) {
            return true;
         }
      }

      return false;
   }
}
