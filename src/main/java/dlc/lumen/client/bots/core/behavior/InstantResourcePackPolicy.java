package dlc.lumen.client.bots.core.behavior;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class InstantResourcePackPolicy implements ResourcePackPolicy {
   @Override
   public void onResourcePackSend(ResourcePackSendS2CPacket packet, ClientConnection connection, ScheduledExecutorService scheduler) {
      UUID var4 = packet.id();
      if (connection.isOpen()) {
         connection.send(new ResourcePackStatusC2SPacket(var4, Status.ACCEPTED));
         connection.send(new ResourcePackStatusC2SPacket(var4, Status.DOWNLOADED));
         connection.send(new ResourcePackStatusC2SPacket(var4, Status.SUCCESSFULLY_LOADED));
      }
   }
}
