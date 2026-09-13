package dlc.lumen.client.autobuy;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;

public final class TextNormalizer {
   private TextNormalizer() {
   }

   public static String normalize(String s) {
      if (s == null) {
         return "";
      }

      String var1 = Normalizer.normalize(s, Form.NFKC);
      var1 = resolveString(var1);
      var1 = var1.toLowerCase(Locale.ROOT);
      var1 = var1.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
      return var1.replaceAll("\\s+", " ").trim();
   }

   private static String resolveString(String s) {
      StringBuilder var1 = new StringBuilder(s.length());

      for (int var2 = 0; var2 < s.length(); var2++) {
         var1.append(computechar(s.charAt(var2)));
      }

      return var1.toString();
   }

   private static char computechar(char c) {
      return switch (c) {
         case 'ǫ' -> 'Q';
         case 'ɢ' -> 'G';
         case 'ɪ' -> 'I';
         case 'ɴ' -> 'N';
         case 'ʀ' -> 'R';
         case 'ʏ' -> 'Y';
         case 'ʙ' -> 'B';
         case 'ʜ' -> 'H';
         case 'ʟ' -> 'L';
         case 'ғ' -> 'F';
         case 'ᴀ' -> 'A';
         case 'ᴄ' -> 'C';
         case 'ᴅ' -> 'D';
         case 'ᴇ' -> 'E';
         case 'ᴊ' -> 'J';
         case 'ᴋ' -> 'K';
         case 'ᴍ' -> 'M';
         case 'ᴏ' -> 'O';
         case 'ᴘ' -> 'P';
         case 'ᴛ' -> 'T';
         case 'ᴜ' -> 'U';
         case 'ᴠ' -> 'V';
         case 'ᴡ' -> 'W';
         case 'ᴢ' -> 'Z';
         case 'ꜰ' -> 'F';
         case 'ꜱ' -> 'S';
         default -> c;
      };
   }

   public static boolean nameMatches(String expected, String actual) {
      if (expected == null || expected.isBlank()) {
         return true;
      }

      if (actual == null) {
         return false;
      }

      String var2 = normalize(expected);
      String var3 = normalize(actual);
      return var2.isEmpty() ? true : var2.equals(var3) || checkCondition(var3, var2);
   }

   public static boolean keywordIn(String haystack, String needle) {
      if (needle == null || needle.isBlank()) {
         return true;
      }

      if (haystack == null) {
         return false;
      }

      String var2 = normalize(haystack);
      String var3 = normalize(needle);
      return var3.isEmpty() || checkCondition(var2, var3);
   }

   private static boolean checkCondition(String text, String sub) {
      if (text.equals(sub)) {
         return true;
      }

      int var2 = 0;

      while (true) {
         int var3 = text.indexOf(sub, var2);
         if (var3 < 0) {
            return false;
         }

         boolean var4 = var3 == 0 || text.charAt(var3 - 1) == ' ';
         int var5 = var3 + sub.length();
         boolean var6 = var5 == text.length() || text.charAt(var5) == ' ';
         if (var4 && var6) {
            return true;
         }

         var2 = var3 + 1;
      }
   }
}
