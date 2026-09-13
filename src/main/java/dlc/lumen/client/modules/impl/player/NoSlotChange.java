package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.client.modules.Module;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class NoSlotChange extends Module {
   public static final NoSlotChange INSTANCE = new NoSlotChange();

   public NoSlotChange() {
      super("NoSlotChange", "Не даёт серверу менять выбранный слот", Module.ModuleCategory.PLAYER);
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE) {
         if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket) {
            event.cancel();
         }
      }
   }
}
