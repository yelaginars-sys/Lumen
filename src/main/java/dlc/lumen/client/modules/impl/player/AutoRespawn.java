package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn extends Module {
   public static AutoRespawn INSTANCE = new AutoRespawn();
   private final FloatSetting floatSetting = new FloatSetting("Задержка, сек", 0.2F, 0.0F, 3.0F, 0.05F);
   private long timestamp = 0L;

   public AutoRespawn() {
      super("AutoRespawn", "Автоматически возрождается при смерти", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null) {
         boolean var2 = mc.currentScreen instanceof DeathScreen;
         boolean var3 = var2 || !mc.player.isAlive();
         if (!var3) {
            this.timestamp = 0L;
         } else {
            long var4 = System.currentTimeMillis();
            if (this.timestamp == 0L) {
               this.timestamp = var4;
            } else if (var4 - this.timestamp >= (long)(this.floatSetting.getValue().floatValue() * 1000.0F)) {
               mc.player.requestRespawn();
               if (var2) {
                  mc.setScreen(null);
               }

               this.timestamp = 0L;
            }
         }
      }
   }
}
