package dlc.lumen.client.autoset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dlc.lumen.Lumen;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class AutoSetKitStorage {
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   private static final List<AutoSetKit> AUTO_SET_KITS = new ArrayList<>();
   private static boolean flag;

   private AutoSetKitStorage() {
   }

   public static List<AutoSetKit> kits() {
      if (!flag) {
         loadAll();
      }

      return AUTO_SET_KITS;
   }

   public static void loadAll() {
      flag = true;
      AUTO_SET_KITS.clear();
      File var0 = getFile();
      if (var0 != null && var0.isDirectory()) {
         File[] var1 = var0.listFiles((f, n) -> n.toLowerCase(Locale.ROOT).endsWith(".json"));
         if (var1 != null) {
            for (File var5 : var1) {
               try {
                  String var6 = new String(Files.readAllBytes(var5.toPath()), StandardCharsets.UTF_8);
                  AutoSetKit var7 = (AutoSetKit)GSON_BUILDER.fromJson(var6, AutoSetKit.class);
                  if (var7 != null) {
                     if (var7.name == null || var7.name.isBlank()) {
                        var7.name = resolveString(var5.getName());
                     }

                     if (var7.entries == null) {
                        var7.entries = new ArrayList<>();
                     }

                     AUTO_SET_KITS.add(var7);
                  }
               } catch (Throwable var8) {
               }
            }

            AUTO_SET_KITS.sort(Comparator.comparing(k -> k.name == null ? "" : k.name.toLowerCase(Locale.ROOT)));
         }
      }
   }

   public static boolean save(AutoSetKit kit) {
      if (kit != null && kit.name != null && !kit.name.isBlank()) {
         File var1 = getFile();
         if (var1 == null) {
            return false;
         }

         try {
            Files.createDirectories(var1.toPath());
            Path var2 = new File(var1, sanitize(kit.name) + ".json").toPath();
            Files.writeString(var2, GSON_BUILDER.toJson(kit), StandardCharsets.UTF_8);
            if (!AUTO_SET_KITS.contains(kit)) {
               AUTO_SET_KITS.add(kit);
               AUTO_SET_KITS.sort(Comparator.comparing(k -> k.name == null ? "" : k.name.toLowerCase(Locale.ROOT)));
            }

            return true;
         } catch (Exception var3) {
            System.err.println("[AutoSet] save failed: " + var3);
            return false;
         }
      } else {
         return false;
      }
   }

   public static void delete(AutoSetKit kit) {
      if (kit != null) {
         File var1 = getFile();
         if (var1 != null) {
            File var2 = new File(var1, sanitize(kit.name) + ".json");
            if (var2.exists() && !var2.delete()) {
               System.err.println("[AutoSet] could not delete " + var2);
            }
         }

         AUTO_SET_KITS.remove(kit);
      }
   }

   private static File getFile() {
      File var0 = Lumen.INSTANCE.configsDir;
      return var0 == null ? null : new File(var0, "autoset");
   }

   static String sanitize(String name) {
      if (name != null && !name.isBlank()) {
         StringBuilder var1 = new StringBuilder(name.length());

         for (int var2 = 0; var2 < name.length(); var2++) {
            char var3 = name.charAt(var2);
            if (!Character.isLetterOrDigit(var3) && var3 != ' ' && var3 != '-' && var3 != '_' && var3 != '.') {
               var1.append('_');
            } else {
               var1.append(var3);
            }
         }

         String var4 = var1.toString().trim();
         return var4.isEmpty() ? "kit" : var4;
      } else {
         return "kit";
      }
   }

   private static String resolveString(String fileName) {
      return fileName.toLowerCase(Locale.ROOT).endsWith(".json") ? fileName.substring(0, fileName.length() - 5) : fileName;
   }
}
