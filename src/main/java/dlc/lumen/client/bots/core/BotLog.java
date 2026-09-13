package dlc.lumen.client.bots.core;

public final class BotLog {
   private static final int INDEX = 300;

   private BotLog() {
   }

   public static void log(String message) {
      System.out.println("[lumen/bot] " + message);
   }

   public static String trim(String text) {
      if (text == null) {
         return "";
      }

      String var1 = text.replace('\n', ' ');
      return var1.length() <= 300 ? var1 : var1.substring(0, 300) + "...";
   }
}
