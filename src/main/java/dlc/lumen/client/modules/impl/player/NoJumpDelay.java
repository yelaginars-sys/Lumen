package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.mixin.ILivingEntity;

public class NoJumpDelay extends Module {
   public static NoJumpDelay INSTANCE = new NoJumpDelay();

   public NoJumpDelay() {
      super("NoJumpDelay", "Убирает задержку на прыжок", Module.ModuleCategory.PLAYER);
   }

   @EventLink
   public void onEvent(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         ((ILivingEntity)mc.player).setJumpingCooldown(0);
      }
   }
}
