package dlc.lumen.client.render.figura;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.loader.api.FabricLoader;

public final class FiguraBridge {
   private static final String TEXT = "/figura_avatars.zip";
   private static final String TEXT2 = ".lumen_zenit_installed";
   private static final String AVATAR_MANAGER_CLASS_NAME = "org.figuramc.figura.avatar.AvatarManager";
   private static final String LOCAL_AVATAR_FETCHER_CLASS_NAME = "org.figuramc.figura.avatar.local.LocalAvatarFetcher";
   private static volatile boolean flag = false;
   private static volatile List<FiguraBridge.AvatarInfo> figuraBridges = null;
   private static final Map<FiguraBridge.Category, String> FIGURA_BRIDGES = new ConcurrentHashMap<>();
   private static final String TEXT3 = ".lumen_selected_avatar";
   private static volatile boolean flag2 = false;

   private FiguraBridge() {
   }

   public static boolean isFiguraPresent() {
      try {
         Class.forName("org.figuramc.figura.avatar.AvatarManager", false, FiguraBridge.class.getClassLoader());
         return true;
      } catch (Throwable var1) {
         return false;
      }
   }

   private static Path helper() {
      try {
         return FabricLoader.getInstance().getGameDir().resolve("figura").resolve("avatars");
      } catch (Throwable var1) {
         return null;
      }
   }

   public static Path avatarDir() {
      try {
         Class var0 = Class.forName("org.figuramc.figura.avatar.local.LocalAvatarFetcher", true, FiguraBridge.class.getClassLoader());
         if (var0.getMethod("getLocalAvatarDirectory").invoke(null) instanceof Path var2) {
            return var2;
         }
      } catch (Throwable var3) {
      }

      return helper();
   }

   public static void installBundledAvatars() {
      if (!flag) {
         flag = true;

         try {
            Path var0 = helper();
            if (var0 != null) {
               Files.createDirectories(var0);
               Path var1 = var0.resolve(".lumen_zenit_installed");
               if (!Files.exists(var1)) {
                  try (InputStream var2 = FiguraBridge.class.getResourceAsStream("/figura_avatars.zip")) {
                     if (var2 == null) {
                        return;
                     }

                     helper2(var2, var0);
                  }

                  Files.writeString(var1, "zenit cosmetics installed by lumen");
               }
            }
         } catch (Throwable var7) {
         }
      }
   }

   private static void helper2(InputStream in, Path destDir) throws IOException {
      Path var2 = destDir.toAbsolutePath().normalize();

      ZipEntry var4;
      try (ZipInputStream var3 = new ZipInputStream(in)) {
         while ((var4 = var3.getNextEntry()) != null) {
            Path var5 = var2.resolve(var4.getName()).normalize();
            if (var5.startsWith(var2)) {
               if (var4.isDirectory()) {
                  Files.createDirectories(var5);
               } else {
                  Path var6 = var5.getParent();
                  if (var6 != null) {
                     Files.createDirectories(var6);
                  }

                  Files.copy(var3, var5, StandardCopyOption.REPLACE_EXISTING);
               }

               var3.closeEntry();
            }
         }
      }
   }

   static void deleteRecursivelyQuietly(Path target) {
      if (target != null && Files.exists(target)) {
         try (Stream<Path> var1 = Files.walk(target)) {
            var1.sorted(Collections.reverseOrder()).forEach(p -> {
               try {
                  Files.deleteIfExists(p);
               } catch (Throwable var2) {
               }
            });
         } catch (Throwable var6) {
         }
      }
   }

   public static List<String> listAvatars() {
      Path var0 = avatarDir();
      if (var0 != null && Files.isDirectory(var0)) {
         try {
            Path var1 = var0.toAbsolutePath().normalize();
            ArrayList<String> var2 = new ArrayList<>();
            Files.walk(var1)
               .filter(p -> p.getFileName() != null && p.getFileName().toString().equals("avatar.json"))
               .map(p -> var1.relativize(p.getParent()).toString().replace('\\', '/'))
               .filter(s -> !s.isBlank() && !s.startsWith("."))
               .forEach(var2::add);
            HashSet<String> var3 = new HashSet<>(var2);
            ArrayList<String> var4 = new ArrayList<>();

            for (String var6 : var2) {
               if (!helper3(var6, var3) && !helper4(var6)) {
                  var4.add(var6);
               }
            }

            var4.sort(String.CASE_INSENSITIVE_ORDER);
            return var4;
         } catch (Throwable var7) {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }

   private static boolean helper3(String id, Set<String> set) {
      int var2 = id.lastIndexOf(47);

      while (var2 > 0) {
         String var3 = id.substring(0, var2);
         if (set.contains(var3)) {
            return true;
         }

         var2 = var3.lastIndexOf(47);
      }

      return false;
   }

   public static List<FiguraBridge.AvatarInfo> listAvatarInfos() {
      List var0 = figuraBridges;
      if (var0 != null) {
         return var0;
      }

      Path var1 = avatarDir();
      ArrayList var2 = new ArrayList();
      if (var1 != null && Files.isDirectory(var1)) {
         Path var3 = var1.toAbsolutePath().normalize();

         for (String var5 : listAvatars()) {
            Path var6 = var3.resolve(var5).normalize();
            String var7 = var5.contains("/") ? var5.substring(var5.lastIndexOf(47) + 1) : var5;
            var2.add(new FiguraBridge.AvatarInfo(var5, var7, var6));
         }
      }

      figuraBridges = var2;
      return var2;
   }

   public static FiguraBridge.Category categoryOf(String id) {
      String var1 = id.toLowerCase(Locale.ROOT);
      if (var1.startsWith("weapon")) {
         return FiguraBridge.Category.WEAPON;
      } else if (var1.startsWith("head")
         || helper5(
            var1, "hat", "crown", "halo", "helmet", "traffic cone", "cap", "glasses", "cat ears", "catears", "ushanka", "astronaut", "flower hat", "isha"
         )) {
         return FiguraBridge.Category.HEAD;
      } else if (helper5(
         var1, "sword", "scythe", "staff", "halberd", "axe", "blade", "punch", "коса", "оружие", "молоток", "меч", "war axe", "dragon", "capitano"
      )) {
         return FiguraBridge.Category.WEAPON;
      } else {
         return !helper5(var1, "companion", "goose", "goat", "drone", "mothli", "turtle") && !var1.matches(".*\\bpet\\b.*")
            ? FiguraBridge.Category.MODEL
            : FiguraBridge.Category.PET;
      }
   }

   private static boolean helper4(String id) {
      return id != null && id.toLowerCase(Locale.ROOT).contains("wing");
   }

   private static boolean helper5(String s, String... keys) {
      for (String var5 : keys) {
         if (s.contains(var5)) {
            return true;
         }
      }

      return false;
   }

   public static List<FiguraBridge.AvatarInfo> listAvatarInfos(FiguraBridge.Category category) {
      ArrayList var1 = new ArrayList();

      for (FiguraBridge.AvatarInfo var3 : listAvatarInfos()) {
         if (categoryOf(var3.id()) == category) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public static String getSelectedId() {
      for (FiguraBridge.Category var3 : FiguraBridge.Category.values()) {
         String var4 = FIGURA_BRIDGES.get(var3);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   public static void loadPersisted() {
      try {
         Path var0 = helper6();
         if (var0 == null || !Files.isRegularFile(var0)) {
            return;
         }

         for (String var2 : Files.readAllLines(var0)) {
            var2 = var2.trim();
            if (!var2.isEmpty()) {
               int var3 = var2.indexOf(61);
               String var4 = var3 > 0 ? var2.substring(var3 + 1).trim() : var2;
               if (!var4.isBlank()) {
                  if (helper4(var4)) {
                     flag2 = true;
                  } else {
                     FIGURA_BRIDGES.put(categoryOf(var4), var4);
                  }
               }
            }
         }
      } catch (Throwable var5) {
      }
   }

   public static void reapplySelected() {
      if (!FIGURA_BRIDGES.isEmpty()) {
         helper7();
      } else if (flag2) {
         flag2 = false;
         helper7();
      }
   }

   private static Path helper6() {
      Path var0 = helper();
      return var0 == null ? null : var0.resolve(".lumen_selected_avatar");
   }

   private static void updateState() {
      try {
         Path var0 = helper6();
         if (var0 == null) {
            return;
         }

         Files.createDirectories(var0.getParent());
         if (FIGURA_BRIDGES.isEmpty()) {
            Files.deleteIfExists(var0);
         } else {
            StringBuilder var1 = new StringBuilder();

            for (FiguraBridge.Category var5 : FiguraBridge.Category.values()) {
               String var6 = FIGURA_BRIDGES.get(var5);
               if (var6 != null) {
                  var1.append(var5.name()).append('=').append(var6).append('\n');
               }
            }

            Files.writeString(var0, var1.toString());
         }
      } catch (Throwable var7) {
      }
   }

   public static String getSelected(FiguraBridge.Category c) {
      return FIGURA_BRIDGES.get(c);
   }

   public static boolean isSelected(FiguraBridge.Category c, String id) {
      return id != null && id.equals(FIGURA_BRIDGES.get(c));
   }

   public static int selectedCount() {
      return FIGURA_BRIDGES.size();
   }

   public static boolean isNoneSelected() {
      return FIGURA_BRIDGES.isEmpty();
   }

   public static boolean isSelectedAny(String id) {
      return id != null && FIGURA_BRIDGES.containsValue(id);
   }

   public static void select(FiguraBridge.Category c, String id) {
      if (id == null) {
         if (FIGURA_BRIDGES.remove(c) == null) {
            return;
         }
      } else {
         FIGURA_BRIDGES.put(categoryOf(id), id);
      }

      helper7();
   }

   public static void clearAll() {
      FIGURA_BRIDGES.clear();
      helper7();
   }

   public static boolean applyAvatar(String relativePath) {
      if (relativePath == null) {
         return false;
      }

      FIGURA_BRIDGES.put(categoryOf(relativePath), relativePath);
      helper7();
      return true;
   }

   public static boolean clearAvatar() {
      clearAll();
      return true;
   }

   private static void helper7() {
      updateState();

      try {
         if (FIGURA_BRIDGES.isEmpty()) {
            helper8();
            return;
         }

         Path var0 = avatarDir();
         if (var0 == null) {
            return;
         }

         Path var1 = var0.toAbsolutePath().normalize();
         Path var2;
         if (FIGURA_BRIDGES.size() != 1) {
            var2 = FiguraCombiner.build(var1, new EnumMap<>(FIGURA_BRIDGES));
         } else {
            Path var3 = var1.resolve(FIGURA_BRIDGES.values().iterator().next()).normalize();
            var2 = var3.startsWith(var1) && Files.isDirectory(var3) ? var3 : null;
         }

         if (var2 == null) {
            return;
         }

         Class var5 = Class.forName("org.figuramc.figura.avatar.AvatarManager", true, FiguraBridge.class.getClassLoader());
         var5.getMethod("loadLocalAvatar", Path.class).invoke(null, var2);
      } catch (Throwable var4) {
      }
   }

   private static void helper8() {
      try {
         Class var0 = Class.forName("org.figuramc.figura.avatar.AvatarManager", true, FiguraBridge.class.getClassLoader());
         var0.getMethod("clearAllAvatars").invoke(null);
      } catch (Throwable var1) {
      }
   }

   public record AvatarInfo(String id, String name, Path dir) {

      public AvatarInfo(String id, String name, Path dir) {
         this.id = id;
         this.name = name;
         this.dir = dir;
      }

      public String id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public Path dir() {
         return this.dir;
      }
   }

   public enum Category {
      MODEL("Модели", "Снять модель", "model"),
      WEAPON("Оружие", "Снять оружие", "weapon"),
      PET("Питомцы", "Снять питомца", "pet"),
      HEAD("Головы", "Снять голову", "head");

      public final String title;
      public final String removeTitle;
      public final String slot;

      Category(String title, String removeTitle, String slot) {
         this.title = title;
         this.removeTitle = removeTitle;
         this.slot = slot;
      }
   }
}
