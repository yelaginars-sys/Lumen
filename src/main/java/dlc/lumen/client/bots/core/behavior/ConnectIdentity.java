package dlc.lumen.client.bots.core.behavior;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface ConnectIdentity {
   default UUID offlineUuid(String name) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
   }

   default void beforeConnect() {
   }

   default boolean isOffline(String host, int port) {
      return port < 0 || "No Server".equalsIgnoreCase(host);
   }
}
