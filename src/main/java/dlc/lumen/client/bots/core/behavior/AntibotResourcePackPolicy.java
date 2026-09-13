package dlc.lumen.client.bots.core.behavior;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class AntibotResourcePackPolicy implements ResourcePackPolicy {
   @Override
   public void onResourcePackSend(ResourcePackSendS2CPacket packet, ClientConnection connection, ScheduledExecutorService scheduler) {
      UUID var4 = packet.id();
      connection.send(new ResourcePackStatusC2SPacket(var4, Status.ACCEPTED));
      long var5 = 600L + ThreadLocalRandom.current().nextLong(900L);
      long var7 = var5 + 300L + ThreadLocalRandom.current().nextLong(600L);
      scheduler.schedule(() -> updateState(connection, var4, Status.DOWNLOADED), var5, TimeUnit.MILLISECONDS);
      scheduler.schedule(() -> updateState(connection, var4, Status.SUCCESSFULLY_LOADED), var7, TimeUnit.MILLISECONDS);
   }

   private static void updateState(ClientConnection connection, UUID id, Status status) {
      if (connection.isOpen()) {
         connection.send(new ResourcePackStatusC2SPacket(id, status));
      }
   }
}
