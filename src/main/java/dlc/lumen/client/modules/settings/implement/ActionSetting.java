package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.client.modules.settings.Setting;
import java.util.function.Supplier;

public class ActionSetting extends Setting {
   private final Runnable runnable;

   public ActionSetting(String name, Runnable action) {
      super(name);
      this.runnable = action;
   }

   public void run() {
      if (this.runnable != null) {
         this.runnable.run();
      }
   }

   public ActionSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }
}
