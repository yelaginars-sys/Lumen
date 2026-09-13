package dlc.lumen.client.bots.core.behavior;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.text.Text;

public interface TransferPolicy {
   default void onTransfer(ServerTransferS2CPacket packet, ClientConnection connection) {
      connection.disconnect(Text.literal("Bot transfer ignored"));
   }
}
