package dlc.lumen.client.autobuy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ItemDatabase {
   public static final List<ItemEntry> LIST = new ArrayList<>();

   private ItemDatabase() {
   }

   public static void reload() {
      LIST.clear();
      LIST.addAll(readStringOrEmpty());
   }

   private static List<ItemEntry> readStringOrEmpty() {
      ArrayList var0 = new ArrayList();

      try (InputStream var1 = ItemDatabase.class.getResourceAsStream("/assets/lumen/autobuy-items.json")) {
         if (var1 == null) {
            return var0;
         }

         String var2 = new String(var1.readAllBytes(), StandardCharsets.UTF_8);
         JsonObject var3 = JsonParser.parseString(var2).getAsJsonObject();
         if (var3.has("items") && var3.get("items").isJsonArray()) {
            for (JsonElement var6 : var3.getAsJsonArray("items")) {
               JsonObject var7 = var6.getAsJsonObject();
               ItemEntry var8 = new ItemEntry();
               var8.itemId = readStringOrEmpty2(var7, "itemId", "");
               var8.displayName = readStringOrEmpty2(var7, "displayName", null);
               var8.searchQuery = readStringOrEmpty2(var7, "searchQuery", null);
               var8.loreKeyword = readStringOrEmpty2(var7, "loreKeyword", null);
               var8.requireCustom = checkCondition(var7, "custom");
               var8.matchAnySword = checkCondition(var7, "matchAnySword");
               if (var7.has("markers") && var7.get("markers").isJsonArray()) {
                  for (JsonElement var10 : var7.getAsJsonArray("markers")) {
                     JsonObject var11 = var10.getAsJsonObject();
                     String var12 = readStringOrEmpty2(var11, "type", "");
                     String var13 = readStringOrEmpty2(var11, "key", "");
                     String var14 = readStringOrEmpty2(var11, "value", "");
                     switch (var12) {
                        case "PBV":
                           var8.markers.add(Marker.pbv(var13, var14));
                           break;
                        case "CUSTOM_DATA":
                           var8.markers.add(Marker.customData(var13, var14));
                           break;
                        case "CUSTOM_ENCH":
                           var8.markers.add(Marker.customEnch(var13));
                     }
                  }
               }

               var0.add(var8);
            }

            return var0;
         } else {
            return var0;
         }
      } catch (Throwable var19) {
         return new ArrayList<>();
      }
   }

   private static String readStringOrEmpty2(JsonObject o, String key, String def) {
      return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : def;
   }

   private static boolean checkCondition(JsonObject o, String key) {
      return o.has(key) && !o.get(key).isJsonNull() && o.get(key).getAsBoolean();
   }
}
