package dlc.lumen.client.render.figura;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final class FiguraCombiner {
   static final String COMBINED_DIR = ".lumen_combined";
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private FiguraCombiner() {
   }

   static Path build(Path avatarsRoot, Map<FiguraBridge.Category, String> selection) {
      try {
         Path var2 = avatarsRoot.toAbsolutePath().normalize();
         Path var3 = var2.resolve(".lumen_combined");
         FiguraBridge.deleteRecursivelyQuietly(var3);
         Files.createDirectories(var3);
         JsonObject var4 = null;
         ArrayList<String> var5 = new ArrayList<>();
         ArrayList<String> var6 = new ArrayList<>();
         boolean var7 = false;
         JsonArray var8 = new JsonArray();
         JsonArray var9 = new JsonArray();
         JsonObject var10 = new JsonObject();

         for (FiguraBridge.Category var14 : FiguraBridge.Category.values()) {
            String var15 = (String)selection.get(var14);
            if (var15 != null) {
               try {
                  Path var16 = var2.resolve(var15).normalize();
                  if (var16.startsWith(var2) && Files.isDirectory(var16)) {
                     String var17 = var14 == FiguraBridge.Category.MODEL ? null : var14.slot;
                     helper(var16, var17 == null ? var3 : var3.resolve(var17), var17);
                     JsonObject var18 = helper3(var16.resolve("avatar.json"));
                     var5.add(helper4(var18, var15));
                     List<String> var19 = helper5(var18.get("autoScripts"));
                     if (var19 != null) {
                        var7 = true;
                     }

                     for (String var21 : var19 != null ? var19 : helper2(var16)) {
                        var6.add(var17 == null ? var21 : var17 + "/" + var21);
                     }

                     if (var17 == null) {
                        var4 = var18;
                     } else {
                        helper6(var18, var17, var8, var9, var10);
                     }
                  }
               } catch (Throwable var22) {
               }
            }
         }

         if (var5.isEmpty()) {
            return null;
         }

         JsonObject var24 = var4 != null ? var4 : new JsonObject();
         var24.addProperty("name", "Lumen Loadout");
         var24.addProperty("description", String.join(" + ", var5));
         if (var7) {
            JsonArray var25 = new JsonArray();

            for (String var27 : var6) {
               var25.add(var27);
            }

            var24.add("autoScripts", var25);
         } else {
            var24.remove("autoScripts");
         }

         helper7(var24, "autoAnims", var8);
         helper7(var24, "ignoredTextures", var9);
         helper8(var24, "customizations", var10);
         Files.writeString(var3.resolve("avatar.json"), GSON_BUILDER.toJson(var24), StandardCharsets.UTF_8);
         return var3;
      } catch (Throwable var23) {
         return null;
      }
   }

   private static void helper(Path src, Path dst, String sub) throws IOException {
      Path var3 = src.toAbsolutePath().normalize();
      Path var4 = var3.resolve("avatar.json");

      try (Stream<Path> var5 = Files.walk(var3)) {
         for (Path var7 : (Iterable<Path>)var5::iterator) {
            if (Files.isRegularFile(var7) && !var7.equals(var4)) {
               Path var8 = dst.resolve(var3.relativize(var7).toString());
               Files.createDirectories(var8.getParent());
               if (sub != null && var7.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".lua")) {
                  readStringOrEmpty(var7, var8, sub);
               } else {
                  Files.copy(var7, var8, StandardCopyOption.REPLACE_EXISTING);
               }
            }
         }
      }
   }

   private static void readStringOrEmpty(Path in, Path out, String sub) {
      try {
         String var3 = new String(Files.readAllBytes(in), StandardCharsets.UTF_8);
         Files.write(out, rewriteLua(var3, sub).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var6) {
         try {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
         } catch (Throwable var5) {
         }
      }
   }

   static String rewriteLua(String text, String sub) {
      String var2 = text;
      var2 = var2.replaceAll("(?<![\\w.])animations\\s*\\[\\s*\"", "animations[\"" + sub + ".");
      var2 = var2.replaceAll("(?<![\\w.\"'])models(?=\\s*[.\\[])", "models." + sub);
      var2 = readStringOrEmpty2(var2, sub);
      var2 = var2.replaceAll("(?<![\\w.])textures\\[\\s*\"", "textures[\"" + sub + ".");
      var2 = var2.replaceAll("(?<![\\w.])sounds\\[\\s*\"", "sounds[\"" + sub + ".");
      var2 = var2.replaceAll("(?<![\\w.])sounds\\s*:\\s*playSound\\(\\s*\"([^\"'.:]+)\"", "sounds:playSound(\"" + sub + ".$1\"");
      var2 = var2.replaceAll("(?<![\\w.])require\\s*\\(\\s*\"", "require(\"" + sub + ".");
      return var2.replaceAll("(?<![\\w.])require\\s*\\(\\s*'", "require('" + sub + ".");
   }

   private static String readStringOrEmpty2(String text, String sub) {
      Matcher var2 = Pattern.compile("(?<![\\w.\"'])animations\\s*\\.\\s*(?!getPlaying\\b|getAnimations\\b|stopAll\\b)([A-Za-z_]\\w*)").matcher(text);
      StringBuilder var3 = new StringBuilder(text.length() + 64);

      int var4;
      for (var4 = 0; var2.find(); var4 = var2.end()) {
         var3.append(text, var4, var2.start());
         if (readStringOrEmpty3(text, var2.start())) {
            var3.append(var2.group());
         } else {
            var3.append("animations[\"").append(sub).append('.').append(var2.group(1)).append("\"]");
         }
      }

      var3.append(text, var4, text.length());
      return var3.toString();
   }

   private static boolean readStringOrEmpty3(String text, int index) {
      int var2 = text.lastIndexOf(10, index - 1) + 1;
      boolean var3 = false;
      boolean var4 = false;

      for (int var5 = var2; var5 < index; var5++) {
         char var6 = text.charAt(var5);
         if (var6 == '\\') {
            var5++;
         } else if (var6 == '"' && !var4) {
            var3 = !var3;
         } else if (var6 == '\'' && !var3) {
            var4 = !var4;
         }
      }

      return var3 || var4;
   }

   private static List<String> helper2(Path src) throws IOException {
      Path var1 = src.toAbsolutePath().normalize();
      ArrayList<String> var2 = new ArrayList<>();

      try (Stream<Path> var3 = Files.walk(var1)) {
         var3.filter(x$0 -> Files.isRegularFile(x$0))
            .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".lua"))
            .map(p -> var1.relativize(p).toString().replace('\\', '/'))
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .forEach(var2::add);
      }

      return var2;
   }

   private static JsonObject helper3(Path file) {
      try {
         String var1 = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
         if (!var1.isEmpty() && var1.charAt(0) == '\ufeff') {
            var1 = var1.substring(1);
         }

         JsonElement var2 = JsonParser.parseString(var1);
         if (var2.isJsonObject()) {
            return var2.getAsJsonObject();
         }
      } catch (Throwable var3) {
      }

      return new JsonObject();
   }

   private static String helper4(JsonObject json, String id) {
      JsonElement var2 = json.get("name");
      if (var2 != null && var2.isJsonPrimitive()) {
         String var3 = var2.getAsString().trim();
         if (!var3.isEmpty()) {
            return var3;
         }
      }

      return id.contains("/") ? id.substring(id.lastIndexOf(47) + 1) : id;
   }

   private static List<String> helper5(JsonElement el) {
      if (el != null && el.isJsonArray()) {
         ArrayList var1 = new ArrayList();

         for (JsonElement var3 : el.getAsJsonArray()) {
            if (var3.isJsonPrimitive()) {
               var1.add(var3.getAsString());
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   private static void helper6(JsonObject json, String sub, JsonArray anims, JsonArray ignored, JsonObject customizations) {
      JsonElement var5 = json.get("autoAnims");
      if (var5 != null && var5.isJsonArray()) {
         for (JsonElement var7 : var5.getAsJsonArray()) {
            if (var7.isJsonPrimitive()) {
               anims.add(sub + "." + var7.getAsString());
            }
         }
      }

      JsonElement var13 = json.get("ignoredTextures");
      if (var13 != null && var13.isJsonArray()) {
         for (JsonElement var8 : var13.getAsJsonArray()) {
            if (var8.isJsonPrimitive()) {
               String var9 = var8.getAsString();
               ignored.add(var9.startsWith("textures.") ? "textures." + sub + "." + var9.substring("textures.".length()) : sub + "." + var9);
            }
         }
      }

      JsonElement var15 = json.get("customizations");
      if (var15 != null && var15.isJsonObject()) {
         for (Entry var17 : var15.getAsJsonObject().entrySet()) {
            JsonElement var10 = ((JsonElement)var17.getValue()).deepCopy();
            if (var10.isJsonObject()) {
               JsonObject var11 = var10.getAsJsonObject();
               JsonElement var12 = var11.get("moveTo");
               if (var12 != null && var12.isJsonPrimitive()) {
                  var11.addProperty("moveTo", readStringOrEmpty4(var12.getAsString(), sub));
               }
            }

            customizations.add(readStringOrEmpty4((String)var17.getKey(), sub), var10);
         }
      }
   }

   private static String readStringOrEmpty4(String path, String sub) {
      return path.startsWith("models.") ? "models." + sub + "." + path.substring("models.".length()) : sub + "." + path;
   }

   private static void helper7(JsonObject out, String key, JsonArray extra) {
      if (!extra.isEmpty()) {
         JsonElement var3 = out.get(key);
         JsonArray var4 = var3 != null && var3.isJsonArray() ? var3.getAsJsonArray() : new JsonArray();

         for (JsonElement var6 : extra) {
            var4.add(var6);
         }

         out.add(key, var4);
      }
   }

   private static void helper8(JsonObject out, String key, JsonObject extra) {
      if (!extra.entrySet().isEmpty()) {
         JsonElement var3 = out.get(key);
         JsonObject var4 = var3 != null && var3.isJsonObject() ? var3.getAsJsonObject() : new JsonObject();

         for (Entry var6 : extra.entrySet()) {
            var4.add((String)var6.getKey(), (JsonElement)var6.getValue());
         }

         out.add(key, var4);
      }
   }
}
