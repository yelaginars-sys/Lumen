package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.utils.bot.BotSessionManager;
import dlc.lumen.client.modules.Module;
import java.util.UUID;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class RPSpoofer extends Module {
   public static RPSpoofer INSTANCE = new RPSpoofer();

   public RPSpoofer() {
      super("RPSpoofer", "Убирает ресурс-пак сервера", Module.ModuleCategory.PLAYER);
   }

   @EventLink
   public void onReceivePacket(EventPacket e) {
      if (e.getPacket() instanceof ResourcePackSendS2CPacket var2 && (this.isEnable() || BotSessionManager.shouldBypassResourcePacks())) {
         UUID var4 = var2.id();
         mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(var4, Status.ACCEPTED));
         mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(var4, Status.SUCCESSFULLY_LOADED));
         e.setCancelled(true);
      }
   }
}
