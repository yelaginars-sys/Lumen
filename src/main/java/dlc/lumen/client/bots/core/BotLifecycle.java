package dlc.lumen.client.bots.core;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.text.Text;

public final class BotLifecycle {
   private static final CopyOnWriteArrayList<BotLifecycleListener> BOT_LIFECYCLE_LISTENERS = new CopyOnWriteArrayList<>();

   private BotLifecycle() {
   }

   public static void register(BotLifecycleListener listener) {
      BOT_LIFECYCLE_LISTENERS.addIfAbsent(listener);
   }

   public static void unregister(BotLifecycleListener listener) {
      BOT_LIFECYCLE_LISTENERS.remove(listener);
   }

   static void fireStateChanged(BotSession bot, BotSession.State from, BotSession.State to) {
      for (BotLifecycleListener var4 : BOT_LIFECYCLE_LISTENERS) {
         var4.onStateChanged(bot, from, to);
      }
   }

   static void fireChat(BotSession bot, Text message) {
      for (BotLifecycleListener var3 : BOT_LIFECYCLE_LISTENERS) {
         var3.onChat(bot, message);
      }
   }

   static void fireDisconnected(BotSession bot, String reason) {
      for (BotLifecycleListener var3 : BOT_LIFECYCLE_LISTENERS) {
         var3.onDisconnected(bot, reason);
      }
   }
}
