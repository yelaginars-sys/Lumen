package dlc.lumen.api.utils.network;

import dlc.lumen.api.QClient;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.packet.Packet;

public final class NetworkUtils implements QClient {
   private static final List<Packet<?>> packetList = new ArrayList<>();

   public static void sendSilentPacket(Packet<?> packet) {
      packetList.add(packet);
      mc.getNetworkHandler().sendPacket(packet);
   }

   public static void sendPacket(Packet<?> packet) {
      mc.getNetworkHandler().sendPacket(packet);
   }

   @Generated
   private NetworkUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static List<Packet<?>> getSilentPackets() {
      return packetList;
   }
}
