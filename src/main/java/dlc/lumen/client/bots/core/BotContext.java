package dlc.lumen.client.bots.core;

import dlc.lumen.client.render.figura.FiguraBotGuard;

public final class BotContext {
   private static volatile BotSession volatileBotSession;

   private BotContext() {
   }

   public static BotSession current() {
      return volatileBotSession;
   }

   public static boolean isBot() {
      return volatileBotSession != null;
   }

   static BotSession enter(BotSession session) {
      BotSession var1 = volatileBotSession;
      volatileBotSession = session;
      if (var1 == null) {
         FiguraBotGuard.suspend();
      }

      return var1;
   }

   static void exit(BotSession previous) {
      volatileBotSession = previous;
      if (previous == null) {
         FiguraBotGuard.resume();
      }
   }
}
