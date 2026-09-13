package dlc.lumen.client.bots.core.behavior;

import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public interface ResourcePackPolicy {
   void onResourcePackSend(ResourcePackSendS2CPacket var1, ClientConnection var2, ScheduledExecutorService var3);
}
