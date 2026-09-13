package dlc.lumen.client.autoset;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import java.util.function.Consumer;
import net.minecraft.client.util.math.MatrixStack;

public class AutoSetTextInput implements QClient {
   private final Font font = Fonts.getFont("suisse", 11);
   private final String text;
   private final boolean flag;
   private final int index;
   private Consumer<String> consumer;
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4;
   private String text2 = "";
   private boolean text3;

   public AutoSetTextInput(String placeholder, boolean numeric, int maxLength) {
      this.text = placeholder;
      this.flag = numeric;
      this.index = maxLength;
   }

   public AutoSetTextInput onChange(Consumer<String> listener) {
      this.consumer = listener;
      return this;
   }

   public void setBounds(float x, float y, float w, float h) {
      this.volume = x;
      this.volume2 = y;
      this.volume3 = w;
      this.volume4 = h;
   }

   public String getText() {
      return this.text2;
   }

   public void setText(String value) {
      this.text2 = value == null ? "" : value;
   }

   public boolean isFocused() {
      return this.text3;
   }

   public void setFocused(boolean focused) {
      this.text3 = focused;
   }

   public long asLong() {
      try {
         return this.text2.isBlank() ? 0L : Long.parseLong(this.text2.trim());
      } catch (NumberFormatException var2) {
         return 0L;
      }
   }

   public int asInt() {
      long var1 = this.asLong();
      return var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1;
   }

   public void render(MatrixStack m, int mouseX, int mouseY) {
      boolean var4 = HoveringUtils.isHovered(mouseX, mouseY, this.volume, this.volume2, this.volume3, this.volume4);
      if (this.text3) {
         RenderUtils.drawRoundedRect(
            m,
            this.volume - 0.5F,
            this.volume2 - 0.5F,
            this.volume3 + 1.0F,
            this.volume4 + 1.0F,
            4.5F,
            ColorUtils.applyAlpha(ColorUtils.getThemeColor(), 0.9F)
         );
      }

      RenderUtils.drawRoundedRect(
         m,
         this.volume,
         this.volume2,
         this.volume3,
         this.volume4,
         4.0F,
         ColorUtils.rgba(12, 12, 15, this.text3 ? 255 : (var4 ? 235 : 210))
      );
      float var5 = this.volume2 + this.volume4 / 2.0F - this.font.getHeight() * 0.14F;
      if (this.text2.isEmpty() && !this.text3) {
         this.font.draw(m, this.resolveString(this.text), this.volume + 5.0F, var5, ColorUtils.rgba(255, 255, 255, 90));
      } else {
         String var6 = this.resolveString(this.text2);
         this.font.draw(m, var6, this.volume + 5.0F, var5, ColorUtils.rgba(255, 255, 255, 230));
         if (this.text3 && System.currentTimeMillis() / 500L % 2L == 0L) {
            float var7 = this.volume + 5.0F + this.font.getWidth(var6) + 1.5F;
            float var8 = this.font.getHeight() * 0.62F;
            RenderUtils.drawRoundedRect(m, var7, this.volume2 + (this.volume4 - var8) / 2.0F, 1.0F, var8, 0.5F, -1);
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY) {
      this.text3 = HoveringUtils.isHovered(mouseX, mouseY, this.volume, this.volume2, this.volume3, this.volume4);
      return this.text3;
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      if (!this.text3) {
         return false;
      }

      if (keyCode == 256 || keyCode == 257 || keyCode == 335) {
         this.text3 = false;
         return true;
      }

      if (keyCode == 259) {
         if (!this.text2.isEmpty()) {
            this.text2 = (modifiers & 2) != 0 ? "" : this.text2.substring(0, this.text2.length() - 1);
            this.updateState();
         }

         return true;
      } else if (keyCode == 86 && (modifiers & 2) != 0) {
         String var3 = mc.keyboard.getClipboard();
         if (var3 != null) {
            for (char var7 : var3.toCharArray()) {
               this.checkCondition(var7);
            }

            this.updateState();
         }

         return true;
      } else {
         return true;
      }
   }

   public boolean charTyped(char chr) {
      if (!this.text3) {
         return false;
      }

      if (this.checkCondition(chr)) {
         this.updateState();
      }

      return true;
   }

   private boolean checkCondition(char c) {
      if (this.text2.length() >= this.index) {
         return false;
      }

      if (this.flag) {
         if (!Character.isDigit(c)) {
            return false;
         }
      } else if (c < ' ') {
         return false;
      }

      this.text2 = this.text2 + c;
      return true;
   }

   private void updateState() {
      if (this.consumer != null) {
         this.consumer.accept(this.text2);
      }
   }

   private String resolveString(String value) {
      if (value == null) {
         return "";
      }

      float var2 = this.volume3 - 12.0F;
      String var3 = value;

      while (var3.length() > 1 && this.font.getWidth(var3) > var2) {
         var3 = var3.substring(1);
      }

      return var3;
   }
}
