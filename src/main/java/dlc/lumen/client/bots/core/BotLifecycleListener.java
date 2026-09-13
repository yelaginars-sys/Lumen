package dlc.lumen.client.bots.core;

import net.minecraft.text.Text;

public interface BotLifecycleListener {
   default void onStateChanged(BotSession bot, BotSession.State from, BotSession.State to) {
   }

   default void onDisconnected(BotSession bot, String reason) {
   }

   default void onChat(BotSession bot, Text message) {
   }
}
