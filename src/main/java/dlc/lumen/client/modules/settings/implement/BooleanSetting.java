package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.client.modules.settings.Setting;
import java.util.function.Supplier;
import lombok.Generated;

public class BooleanSetting extends Setting {
   private boolean value;
   private int key2 = -1;

   public BooleanSetting(String name, boolean state) {
      super(name);
      this.value = state;
   }

   public static BooleanSetting of(String name, boolean state) {
      return new BooleanSetting(name, state);
   }

   public BooleanSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }

   public boolean getValue() {
      return this.value;
   }

   @Generated
   public boolean isState() {
      return this.value;
   }

   @Generated
   public int getKey() {
      return this.key2;
   }

   @Generated
   public void setState(boolean state) {
      this.value = state;
   }

   @Generated
   public void setKey(int key) {
      this.key2 = key;
   }
}
