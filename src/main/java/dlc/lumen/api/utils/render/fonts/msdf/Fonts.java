package dlc.lumen.api.utils.render.fonts.msdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.util.math.MatrixStack;

public class Fonts {
   private static final HashMap<String, MsdfFont> stringMap = new HashMap<>();
   private static final HashMap<String, Font[]> stringMap2 = new HashMap<>();
   private static final HashMap<String, Long> stringMap3 = new HashMap<>();
   private static final Set<String> stringSet = new HashSet<>();
   private static final Map<String, String> stringMap4 = Map.of(
      "lumen", "lumen", "inter_medium", "lumen/inter_medium", "icons", "lumen/icons", "iconz", "lumen/iconz"
   );
   private static final long timeMs = 1000L;
   private static boolean flag = false;

   public static void init() {
      if (!flag) {
         flag = true;
         handleName("sf_regular");
         handleName("energy");
         handleName("wave");
         handleName("icon");
         handleName("icon1");
         handleName("iconnew");
         handleName("logo");
         handleName("suisse");
         handleName("lumen");
         handleName("inter_medium");
         handleName("icons");
         handleName("iconz");
         handleName("narezka");
      }
   }

   private static void handleName(String name) {
      String var1 = resolveName(name);
      if (!stringMap.containsKey(var1)) {
         long var2 = System.currentTimeMillis();
         long var4 = stringMap3.getOrDefault(var1, 0L);
         if (var2 >= var4) {
            try {
               MsdfFont var6;
               if (var1.contains("/")) {
                  var6 = MsdfFont.builder().name(var1).dataFile("fonts/msdf/" + var1 + ".json").atlasFile("fonts/msdf/" + var1 + ".png").build();
               } else {
                  var6 = MsdfFont.builder().atlas(var1).data(var1).build();
               }

               stringMap.put(var1, var6);
               Font[] var7 = new Font[100];

               for (int var8 = 8; var8 < 100; var8++) {
                  var7[var8] = new Font(var6, var8);
               }

               stringMap2.put(var1, var7);
               stringMap3.remove(var1);
               stringSet.remove(var1);
            } catch (Exception var9) {
               stringMap3.put(var1, var2 + 1000L);
               if (stringSet.add(var1)) {
                  System.err.println("[Fonts] Failed to load " + var1 + ": " + var9.getMessage());
               }
            }
         }
      }
   }

   public static Font getFont(String name, int size) {
      if (!flag) {
         init();
      }

      String var2 = resolveName(name);
      if (size < 8) {
         size = 8;
      }

      if (size >= 100) {
         size = 99;
      }

      Font[] var3 = stringMap2.get(var2);
      if (var3 != null && var3[size] != null) {
         return var3[size];
      }

      if (!stringMap.containsKey(var2)) {
         handleName(var2);
      }

      var3 = stringMap2.get(var2);
      if (var3 != null && var3[size] != null) {
         return var3[size];
      }

      String var4 = resolveName("sf_regular");
      if (!var4.equals(var2)) {
         Font[] var5 = stringMap2.get(var4);
         if (var5 == null) {
            handleName(var4);
            var5 = stringMap2.get(var4);
         }

         if (var5 != null && var5[size] != null) {
            return var5[size];
         }
      }

      return null;
   }

   public static void drawStringWithFade(Font font, String text, float x, float y, float maxWidth, int color) {
      if (font != null) {
         MatrixStack var6 = new MatrixStack();
         font.drawStringWithFade(var6, text, x, y, maxWidth, color);
      }
   }

   private static String resolveName(String name) {
      String var1 = name.replace(".ttf", "");
      return stringMap4.getOrDefault(var1, var1);
   }
}
