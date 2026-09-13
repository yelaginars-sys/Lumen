package dlc.lumen.client.modules.impl.player;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;

public class ItemScroller extends Module {
   public static ItemScroller INSTANCE = new ItemScroller();
   public final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 200.0F, 1.0F);
   private long timestamp;

   public ItemScroller() {
      super("ItemScroller", "Убирает задержку перемещения предметов", Module.ModuleCategory.PLAYER);
      this.addSettings(this.delay);
   }

   public boolean canQuickMove() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.timestamp < (long)this.delay.get()) {
         return false;
      }

      this.timestamp = var1;
      return true;
   }

   public void resetTimer() {
      this.timestamp = 0L;
   }

   @Override
   public void onDisable() {
      this.resetTimer();
      super.onDisable();
   }
}
