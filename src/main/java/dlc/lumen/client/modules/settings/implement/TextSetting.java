package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.client.modules.settings.Setting;
import java.util.function.Supplier;
import lombok.Generated;

public class TextSetting extends Setting {
   private String text2;
   private final int maxLength2;

   public TextSetting(String name, String text) {
      this(name, text, 32);
   }

   public TextSetting(String name, String text, int maxLength) {
      super(name);
      this.maxLength2 = Math.max(1, maxLength);
      this.setText(text);
   }

   public void setText(String text) {
      if (text == null) {
         this.text2 = "";
      } else {
         StringBuilder var2 = new StringBuilder();

         for (int var3 = 0; var3 < text.length() && var2.length() < this.maxLength2; var3++) {
            char var4 = text.charAt(var3);
            if (!Character.isISOControl(var4)) {
               var2.append(var4);
            }
         }

         this.text2 = var2.toString();
      }
   }

   public String get() {
      return this.text2;
   }

   public TextSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }

   @Generated
   public String getText() {
      return this.text2;
   }

   @Generated
   public int getMaxLength() {
      return this.maxLength2;
   }
}
