package pulse.cosmetic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import ru.pulse.cosmetic.loader.CosmeticLoader;
import ru.pulse.Pulse;

public final class LocalCosmetics {
   private static final int MAX_SCAN_INDEX = 256;
   private static final Path STORE = FabricLoader.getInstance().getGameDir().resolve("lumen").resolve("cosmetics.json");
   private static final List<LocalCosmetics.Entry> ENTRIES = loadEntries();
   private static final LinkedHashMap<String, Integer> SELECTED = new LinkedHashMap<>();
   private static final Map<Integer, ru.pulse.cosmetic.model.CosmeticModel> MODELS = new HashMap<>();

   static {
      load();
   }

   private LocalCosmetics() {
   }

   public static int size() {
      return ENTRIES.size();
   }

   public static String name(int i) {
      return entry(i).name;
   }

   public static String type(int i) {
      return entry(i).type;
   }

   public static Identifier texture(int i) {
      return Identifier.of("pulse", "textures/cosmetics/cosmetic_" + entry(i).index + ".png");
   }

   public static int selected() {
      return SELECTED.isEmpty() ? -1 : SELECTED.values().stream().reduce((a, b) -> b).orElse(-1);
   }

   public static boolean isSelected(int i) {
      return SELECTED.containsValue(i);
   }

   public static String selectedType() {
      int i = selected();
      return i < 0 ? "" : type(i);
   }

   public static Identifier selectedTexture() {
      int i = selected();
      return i < 0 ? null : texture(i);
   }

   public static Identifier selectedCapeTexture() {
      Integer i = SELECTED.get("cape");
      return i == null ? null : texture(i);
   }

   public static List<Integer> selectedIndices() {
      return List.copyOf(SELECTED.values());
   }

   public static JsonObject serializeSelection() {
      JsonObject object = new JsonObject();

      for (Map.Entry<String, Integer> entry : SELECTED.entrySet()) {
         object.addProperty(entry.getKey(), entry(entry.getValue()).index);
      }

      return object;
   }

   public static void deserializeSelection(JsonObject object) {
      SELECTED.clear();
      MODELS.clear();
      if (object == null) {
         return;
      }

      for (String type : object.keySet()) {
         int index = indexByResourceIndex(object.get(type).isJsonPrimitive() ? object.get(type).getAsInt() : -1);
         if (index >= 0) {
            SELECTED.put(type, index);
            if (!"cape".equals(type)) {
               model(index);
            }
         }
      }
   }

   public static void toggle(int i) {
      String type = type(i);
      Integer old = SELECTED.get(type);
      if (old != null && old == i) {
         SELECTED.remove(type);
      } else {
         SELECTED.put(type, i);
         if (!"cape".equals(type)) {
            model(i);
         }
      }

      save();
   }

   public static void clearAll() {
      SELECTED.clear();
      save();
   }

   public static ru.pulse.cosmetic.model.CosmeticModel modelFor(int i) {
      return model(i);
   }

   private static ru.pulse.cosmetic.model.CosmeticModel model(int i) {
      int resourceIndex = entry(i).index;
      return MODELS.computeIfAbsent(resourceIndex, k -> {
         try (InputStream in = LocalCosmetics.class.getResourceAsStream("/assets/pulse/cosmetics/models/cosmetic_" + k + ".json")) {
            return in == null ? null : CosmeticLoader.getInstance().loadFromJson(new String(in.readAllBytes(), StandardCharsets.UTF_8));
         } catch (Exception e) {
            return null;
         }
      });
   }

   private static void load() {
      try {
         if (Files.exists(STORE)) {
            String json = Files.readString(STORE);
            deserializeSelection(JsonParser.parseString(json).getAsJsonObject());
         }
      } catch (Exception e) {
         Pulse.getLOGGER().warn("Failed to load cosmetics selection", e);
      }
   }

   private static void save() {
      try {
         Files.createDirectories(STORE.getParent());
         Files.writeString(STORE, serializeSelection().toString());
      } catch (Exception e) {
         Pulse.getLOGGER().warn("Failed to save cosmetics selection", e);
      }
   }

   private static LocalCosmetics.Entry entry(int i) {
      if (i < 0 || i >= ENTRIES.size()) {
         throw new IndexOutOfBoundsException("Cosmetic index " + i + " out of " + ENTRIES.size());
      }

      return ENTRIES.get(i);
   }

   private static int indexByResourceIndex(int resourceIndex) {
      for (int i = 0; i < ENTRIES.size(); i++) {
         if (ENTRIES.get(i).index == resourceIndex) {
            return i;
         }
      }

      return -1;
   }

   private static List<LocalCosmetics.Entry> loadEntries() {
      List<LocalCosmetics.Entry> entries = new ArrayList<>();

      for (int i = 0; i < MAX_SCAN_INDEX; i++) {
         String path = "/assets/pulse/cosmetics/models/cosmetic_" + i + ".json";
         try (InputStream in = LocalCosmetics.class.getResourceAsStream(path)) {
            if (in != null) {
               String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
               JsonObject object = JsonParser.parseString(json).getAsJsonObject();
               String rawName = object.has("name") ? object.get("name").getAsString() : "Cosmetic " + (i + 1);
               String rawType = object.has("type") ? object.get("type").getAsString() : "";
               int pos = object.has("pos") ? object.get("pos").getAsInt() : -1;
               entries.add(new LocalCosmetics.Entry(i, displayName(rawName, i), classifyType(i, rawName, rawType, pos)));
            }
         } catch (Exception ignored) {
         }
      }

      entries.sort(Comparator.comparingInt(entry -> entry.index));
      return List.copyOf(entries);
   }

   private static String displayName(String rawName, int index) {
      String name = rawName == null || rawName.isBlank() ? "Cosmetic " + (index + 1) : rawName;
      if (name.startsWith("pulse_")) {
         name = name.substring("pulse_".length());
      }

      return name.replace('_', ' ').trim();
   }

   private static String classifyType(int index, String name, String rawType, int pos) {
      if (rawType != null && !rawType.isBlank()) {
         return rawType.trim().toLowerCase();
      }

      String lower = name == null ? "" : name.toLowerCase();
      if (lower.contains("cape")) {
         return "cape";
      } else if (lower.contains("wing")) {
         return "wings";
      } else if (lower.contains("pet") || lower.contains("bee") || lower.contains("radish")) {
         return "pet";
      } else if (lower.contains("hat") || lower.contains("nimb") || pos == 2) {
         return "hat";
      } else if (index <= 13) {
         return "cape";
      } else if (index <= 26) {
         return "wings";
      } else if (index <= 37) {
         return "bodywear";
      } else if (index <= 49) {
         return "pet";
      } else {
         return "bodywear";
      }
   }

   private static final class Entry {
      private final int index;
      private final String name;
      private final String type;

      private Entry(int index, String name, String type) {
         this.index = index;
         this.name = name;
         this.type = type;
      }
   }
}
