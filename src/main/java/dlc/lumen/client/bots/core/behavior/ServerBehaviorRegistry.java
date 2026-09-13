package dlc.lumen.client.bots.core.behavior;

import java.util.concurrent.CopyOnWriteArrayList;

public final class ServerBehaviorRegistry {
   private static final CopyOnWriteArrayList<ServerBehavior> SERVER_BEHAVIORS = new CopyOnWriteArrayList<>();
   private static final ServerBehavior DEFAULT_SERVER_BEHAVIOR = new DefaultServerBehavior();

   private ServerBehaviorRegistry() {
   }

   public static void register(ServerBehavior behavior) {
      SERVER_BEHAVIORS.addIfAbsent(behavior);
   }

   public static ServerBehavior resolve(String host) {
      for (ServerBehavior var2 : SERVER_BEHAVIORS) {
         if (var2.matches(host)) {
            return var2;
         }
      }

      return DEFAULT_SERVER_BEHAVIOR;
   }

   static {
      register(new FuntimeServerBehavior());
   }
}
