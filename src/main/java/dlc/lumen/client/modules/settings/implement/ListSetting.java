package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.client.modules.settings.Setting;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;

public class ListSetting extends Setting {
   public List<BooleanSetting> settings;
   private boolean expanded2;

   public ListSetting(String name, BooleanSetting... settings) {
      super(name);
      this.settings = List.of(settings);
   }

   public ListSetting of(String name, BooleanSetting... settings) {
      return new ListSetting(name, settings);
   }

   public boolean is(String name) {
      BooleanSetting var2 = this.helper(name);
      return var2 != null && var2.isState();
   }

   public void set(String name, boolean value) {
      BooleanSetting var3 = this.helper(name);
      if (var3 != null) {
         var3.setState(value);
      }
   }

   public ListSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }

   private BooleanSetting helper(String name) {
      for (BooleanSetting var3 : this.settings) {
         if (var3.name().equalsIgnoreCase(name)) {
            return var3;
         }
      }

      return null;
   }

   @Generated
   public List<BooleanSetting> getSettings() {
      return this.settings;
   }

   @Generated
   public boolean isExpanded() {
      return this.expanded2;
   }

   @Generated
   public void setSettings(List<BooleanSetting> settings) {
      this.settings = settings;
   }

   @Generated
   public void setExpanded(boolean expanded) {
      this.expanded2 = expanded;
   }
}
