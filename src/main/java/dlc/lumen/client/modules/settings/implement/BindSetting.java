package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.client.modules.settings.Setting;
import java.util.function.Supplier;
import lombok.Generated;

public class BindSetting extends Setting {
   private int key2;

   public BindSetting(String name, int keyDefault) {
      super(name);
      this.key2 = keyDefault;
   }

   public BindSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }

   @Generated
   public int getKey() {
      return this.key2;
   }

   @Generated
   public void setKey(int key) {
      this.key2 = key;
   }
}
