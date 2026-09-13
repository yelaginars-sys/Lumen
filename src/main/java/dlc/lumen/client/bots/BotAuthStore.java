package dlc.lumen.client.bots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dlc.lumen.client.bots.core.BotSession;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class BotAuthStore {
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   private static final SecureRandom SECURE_RANDOM = new SecureRandom();
   private static final String TEXT = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
   private static final int INDEX = 12;
   private static final Map<String, String> HASH_MAP = new HashMap<>();
   private static boolean flag;

   private BotAuthStore() {
   }

   public static synchronized String get(BotSession bot) {
      handleEvent();
      return HASH_MAP.get(resolveString2(bot));
   }

   public static synchronized String getOrCreate(BotSession bot) {
      handleEvent();
      return HASH_MAP.computeIfAbsent(resolveString2(bot), name -> {
         String var1 = resolveString();
         handleEvent2();
         return var1;
      });
   }

   private static String resolveString() {
      StringBuilder var0 = new StringBuilder(12);

      for (int var1 = 0; var1 < 12; var1++) {
         var0.append(
            "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789"
               .charAt(SECURE_RANDOM.nextInt("abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789".length()))
         );
      }

      return var0.toString();
   }

   private static String resolveString2(BotSession bot) {
      String var1 = bot.getName() == null ? "" : bot.getName().toLowerCase(Locale.ROOT);
      return var1 + "@" + bot.getHost() + ":" + bot.getPort();
   }

   private static File getFile() {
      return new File(BotRegistry.directory(), "auth.json");
   }

   private static void handleEvent() {
      if (!flag) {
         flag = true;

         try {
            File var0 = getFile();
            if (!var0.isFile()) {
               return;
            }

            String var1 = new String(Files.readAllBytes(var0.toPath()), StandardCharsets.UTF_8);
            Map var2 = (Map)GSON_BUILDER.fromJson(var1, Map.class);
            if (var2 != null) {
               HASH_MAP.putAll(var2);
            }
         } catch (Throwable var3) {
         }
      }
   }

   private static void handleEvent2() {
      try {
         File var0 = getFile();
         Files.createDirectories(var0.getParentFile().toPath());
         Files.write(var0.toPath(), GSON_BUILDER.toJson(HASH_MAP).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var1) {
      }
   }
}
