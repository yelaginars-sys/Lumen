package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.Lumen;
import dlc.lumen.client.modules.settings.Setting;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;

public class ModeSetting extends Setting {
   private List<String> mods2;
   private String current2;
   private int index2;
   private boolean expanded2;

   public ModeSetting(String name, String current, String... modes) {
      super(name);
      this.mods2 = Arrays.asList(modes);
      this.index2 = this.mods2.indexOf(current);
      if (this.index2 < 0) {
         this.index2 = 0;
      }

      this.current2 = this.mods2.get(this.index2);
   }

   public void set(String selected) {
      int var2 = this.mods2.indexOf(selected);
      if (var2 >= 0) {
         this.current2 = selected;
         this.index2 = var2;
      }
   }

   public boolean is(String mode) {
      return this.current2.equals(mode);
   }

   public String displayMode(String mode) {
      return Lumen.INSTANCE.localizationStorage == null ? mode : Lumen.INSTANCE.localizationStorage.translate(mode);
   }

   public String displayCurrent() {
      return this.displayMode(this.current2);
   }

   public ModeSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }

   @Generated
   public List<String> getMods() {
      return this.mods2;
   }

   @Generated
   public String getCurrent() {
      return this.current2;
   }

   @Generated
   public int getIndex() {
      return this.index2;
   }

   @Generated
   public boolean isExpanded() {
      return this.expanded2;
   }

   @Generated
   public void setMods(List<String> mods) {
      this.mods2 = mods;
   }

   @Generated
   public void setCurrent(String current) {
      this.current2 = current;
   }

   @Generated
   public void setIndex(int index) {
      this.index2 = index;
   }

   @Generated
   public void setExpanded(boolean expanded) {
      this.expanded2 = expanded;
   }
}
