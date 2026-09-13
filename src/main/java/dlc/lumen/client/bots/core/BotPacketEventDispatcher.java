package dlc.lumen.client.bots.core;

import net.minecraft.network.packet.Packet;

public interface BotPacketEventDispatcher {
   Packet<?> handleSendPacketEvent(Packet<?> var1);

   void handlePostSendPacketEvent(Packet<?> var1);
}
