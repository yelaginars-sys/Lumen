package dlc.lumen.client.ui.mainmenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dlc.lumen.Lumen;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class MenuAccountStorage {
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   private static final String TEXT = "menu_accounts.json";

   private MenuAccountStorage() {
   }

   public static List<MenuAccountStorage.MenuAccount> load() {
      Path var0 = readStringOrEmpty2();
      if (!Files.exists(var0)) {
         return new ArrayList<>();
      }

      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         JsonElement var2 = (JsonElement)GSON_BUILDER.fromJson(var1, JsonElement.class);
         if (var2 instanceof JsonArray var3) {
            ArrayList var16 = new ArrayList();

            for (JsonElement var6 : var3) {
               if (var6 instanceof JsonObject var7) {
                  String var8 = readStringOrEmpty(var7, "name");
                  String var9 = readStringOrEmpty(var7, "uuid");
                  if (!var8.isBlank()) {
                     UUID var10;
                     try {
                        var10 = var9.isBlank() ? UUID.randomUUID() : UUID.fromString(var9);
                     } catch (IllegalArgumentException var13) {
                        var10 = UUID.randomUUID();
                     }

                     var16.add(new MenuAccountStorage.MenuAccount(var8, var10));
                  }
               }
            }

            return var16;
         } else {
            return new ArrayList<>();
         }
      } catch (IOException var15) {
         return new ArrayList<>();
      }
   }

   public static void save(List<MenuAccountStorage.MenuAccount> accounts) {
      Path var1 = readStringOrEmpty2();

      try {
         Files.createDirectories(var1.getParent());
         JsonArray var2 = new JsonArray();

         for (MenuAccountStorage.MenuAccount var4 : accounts) {
            JsonObject var5 = new JsonObject();
            var5.addProperty("name", var4.name());
            var5.addProperty("uuid", var4.uuid().toString());
            var2.add(var5);
         }

         try (BufferedWriter var9 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8)) {
            GSON_BUILDER.toJson(var2, var9);
         }
      } catch (IOException var8) {
      }
   }

   public static MenuAccountStorage.MenuAccount createRandom() {
      String[] var0 = new String[]{"Nova", "Pixel", "Lime", "Quartz", "Echo", "Frost", "Cloud", "Orbit", "Cinder", "Velvet"};
      String[] var1 = new String[]{"Fox", "Bloom", "Wave", "Mint", "Leaf", "Spark", "Core", "Shift", "Star", "Byte"};
      int var2 = Math.floorMod(UUID.randomUUID().hashCode(), var0.length);
      int var3 = Math.floorMod((int)(UUID.randomUUID().getMostSignificantBits() >>> 8), var1.length);
      int var4 = 100 + Math.floorMod(UUID.randomUUID().hashCode(), 900);
      return new MenuAccountStorage.MenuAccount(var0[var2] + var1[var3] + var4, UUID.randomUUID());
   }

   public static MenuAccountStorage.MenuAccount createManual(String rawName) {
      String var1 = rawName == null ? "" : rawName.trim().replaceAll("[^A-Za-z0-9_]", "");
      if (var1.isBlank()) {
         var1 = "Player" + (100 + Math.floorMod(UUID.randomUUID().hashCode(), 900));
      }

      if (var1.length() > 16) {
         var1 = var1.substring(0, 16);
      }

      return new MenuAccountStorage.MenuAccount(var1, UUID.randomUUID());
   }

   private static String readStringOrEmpty(JsonObject object, String key) {
      JsonElement var2 = object.get(key);
      return var2 == null ? "" : var2.getAsString();
   }

   private static Path readStringOrEmpty2() {
      Path var0 = Lumen.INSTANCE.globalsDir != null
         ? Lumen.INSTANCE.globalsDir.toPath()
         : Path.of(System.getProperty("user.home"), "lumenClient", "lumen");
      return var0.resolve("menu_accounts.json");
   }

   public record MenuAccount(String name, UUID uuid) {

      public MenuAccount(String name, UUID uuid) {
         this.name = name;
         this.uuid = uuid;
      }

      public boolean matches(String username) {
         return username != null && username.toLowerCase(Locale.ROOT).equals(this.name.toLowerCase(Locale.ROOT));
      }

      public String name() {
         return this.name;
      }

      public UUID uuid() {
         return this.uuid;
      }
   }
}
