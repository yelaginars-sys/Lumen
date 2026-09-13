package dlc.lumen.api.utils.render.font;

import dlc.lumen.api.utils.color.ColorUtils;
import java.util.HashMap;
import java.util.Map;

public class ReplaceSymbols {
   private static final Map<Integer, String> integerMap = new HashMap<>();
   private static final Map<Integer, Integer> integerMap2 = new HashMap<>();
   private static final int[] countArray = new int[]{
      42240,
      42244,
      42248,
      42258,
      42262,
      42272,
      42276,
      42280,
      42336,
      42290,
      42294,
      42308,
      42326,
      42312,
      42304,
      42322,
      42249,
      42259,
      42263,
      42273,
      42277,
      42281,
      42291,
      42295,
      42241,
      42245,
      42313,
      4144,
      4138,
      4132,
      4134,
      4140,
      4139,
      4151,
      4130,
      4148,
      4141,
      4152,
      4133,
      4131,
      4149,
      4150,
      4137,
      4129,
      4145,
      4143,
      4146,
      4135,
      4153,
      4126
   };

   public static String replaceCodePoint(int codePoint) {
      return integerMap.get(codePoint);
   }

   public static int getGradientColorForReplacement(int codePoint, int charIndex, int totalChars, float alpha, int currentColor) {
      if (checkCodePoint(codePoint)) {
         Integer var5 = integerMap2.get(codePoint);
         if (var5 == null) {
            return calcColor(currentColor, alpha);
         }

         int var6 = ColorUtils.darken(var5, 0.8F);
         float var7 = totalChars <= 1 ? 1.0F : (float)charIndex / (totalChars - 1);
         int var8 = ColorUtils.interpolateColor(var6, var5, var7);
         return calcColor(var8, alpha);
      } else {
         return calcColor(currentColor, alpha);
      }
   }

   private static boolean checkCodePoint(int codePoint) {
      for (int var4 : countArray) {
         if (var4 == codePoint) {
            return true;
         }
      }

      return false;
   }

   private static int calcColor(int color, float alpha) {
      int var2 = Math.max(0, Math.min(255, (int)(alpha * 255.0F)));
      return ColorUtils.setAlphaColor(color, var2);
   }

   static {
      integerMap.put(9889, "");
      integerMap.put(9733, "");
      integerMap.put(42240, "PLAYER");
      integerMap.put(42244, "HERO");
      integerMap.put(42248, "TITAN");
      integerMap.put(42258, "AVENGER");
      integerMap.put(42262, "OVERLORD");
      integerMap.put(42272, "MAGISTER");
      integerMap.put(42276, "IMPERATOR");
      integerMap.put(42280, "DRAGON");
      integerMap.put(42336, "D.HELPER");
      integerMap.put(42290, "BULL");
      integerMap.put(42294, "TIGER");
      integerMap.put(42308, "VAMPIRE");
      integerMap.put(42326, "BUNNY");
      integerMap.put(42312, "COBRA");
      integerMap.put(42304, "HYDRA");
      integerMap.put(42322, "RABBIT");
      integerMap.put(42249, "HELPER");
      integerMap.put(42259, "ML.MODER");
      integerMap.put(42263, "MODER");
      integerMap.put(42273, "MODER+");
      integerMap.put(42277, "ST.MODER");
      integerMap.put(42281, "GL.MODER");
      integerMap.put(42291, "ML.ADMIN");
      integerMap.put(42295, "ADMIN");
      integerMap.put(42241, "MEDIA");
      integerMap.put(42245, "YT");
      integerMap.put(42305, "GOD");
      integerMap.put(4144, "HERO");
      integerMap.put(4138, "TITAN");
      integerMap.put(4132, "PRINCE");
      integerMap.put(4134, "PHOENIX");
      integerMap.put(4140, "OVERLORD");
      integerMap.put(4139, "GUARDIAN");
      integerMap.put(4151, "KRATOS");
      integerMap.put(4130, "PHANTOM");
      integerMap.put(4148, "CUSTOM");
      integerMap.put(4141, "WINTER");
      integerMap.put(4152, "SAKURA");
      integerMap.put(4133, "SUMMER");
      integerMap.put(4131, "HALLOWEEN");
      integerMap.put(4149, "TIKTOK");
      integerMap.put(4150, "TIKTOK+");
      integerMap.put(4137, "MEDIA");
      integerMap.put(4129, "YOUTUBE");
      integerMap.put(4145, "HELPER");
      integerMap.put(4143, "ML.ADMIN");
      integerMap.put(4146, "MODER");
      integerMap.put(4135, "CURATOR");
      integerMap.put(4153, "SPECTATOR");
      integerMap.put(4126, "DEVELOPER");
      integerMap.put(7424, "A");
      integerMap.put(665, "B");
      integerMap.put(7428, "C");
      integerMap.put(7429, "D");
      integerMap.put(7431, "E");
      integerMap.put(42800, "F");
      integerMap.put(610, "G");
      integerMap.put(668, "H");
      integerMap.put(618, "I");
      integerMap.put(7434, "J");
      integerMap.put(7435, "K");
      integerMap.put(671, "L");
      integerMap.put(7437, "M");
      integerMap.put(628, "N");
      integerMap.put(7439, "O");
      integerMap.put(7448, "P");
      integerMap.put(491, "Q");
      integerMap.put(640, "R");
      integerMap.put(7451, "T");
      integerMap.put(7452, "U");
      integerMap.put(42801, "S");
      integerMap.put(7456, "V");
      integerMap.put(7457, "W");
      integerMap.put(7521, "X");
      integerMap.put(655, "Y");
      integerMap.put(7458, "Z");
      integerMap2.put(42240, ColorUtils.rgb(141, 143, 141));
      integerMap2.put(42244, ColorUtils.rgb(100, 113, 251));
      integerMap2.put(42248, ColorUtils.rgb(245, 220, 29));
      integerMap2.put(42258, ColorUtils.rgb(79, 201, 83));
      integerMap2.put(42262, ColorUtils.rgb(85, 255, 255));
      integerMap2.put(42272, ColorUtils.rgb(224, 138, 52));
      integerMap2.put(42276, ColorUtils.rgb(202, 60, 60));
      integerMap2.put(42280, ColorUtils.rgb(245, 51, 238));
      integerMap2.put(42336, ColorUtils.rgb(214, 200, 42));
      integerMap2.put(42290, ColorUtils.rgb(121, 81, 202));
      integerMap2.put(42294, ColorUtils.rgb(202, 130, 60));
      integerMap2.put(42308, ColorUtils.rgb(202, 60, 60));
      integerMap2.put(42326, ColorUtils.rgb(68, 65, 66));
      integerMap2.put(42312, ColorUtils.rgb(127, 214, 86));
      integerMap2.put(42304, ColorUtils.rgb(92, 120, 7));
      integerMap2.put(42322, ColorUtils.rgb(230, 232, 230));
      integerMap2.put(42249, ColorUtils.rgb(214, 200, 42));
      integerMap2.put(42259, ColorUtils.rgb(100, 113, 251));
      integerMap2.put(42263, ColorUtils.rgb(100, 113, 251));
      integerMap2.put(42273, ColorUtils.rgb(121, 81, 202));
      integerMap2.put(42277, ColorUtils.rgb(100, 113, 251));
      integerMap2.put(42281, ColorUtils.rgb(121, 81, 202));
      integerMap2.put(42291, ColorUtils.rgb(64, 151, 214));
      integerMap2.put(42295, ColorUtils.rgb(202, 60, 60));
      integerMap2.put(42241, ColorUtils.rgb(121, 81, 202));
      integerMap2.put(42245, ColorUtils.rgb(255, 255, 255));
      integerMap2.put(42305, ColorUtils.rgb(245, 198, 29));
      integerMap2.put(42313, ColorUtils.rgb(202, 130, 60));
      integerMap2.put(4144, ColorUtils.rgb(13, 176, 209));
      integerMap2.put(4138, ColorUtils.rgb(21, 232, 24));
      integerMap2.put(4132, ColorUtils.rgb(232, 169, 21));
      integerMap2.put(4134, ColorUtils.rgb(237, 215, 19));
      integerMap2.put(4140, ColorUtils.rgb(64, 163, 152));
      integerMap2.put(4139, ColorUtils.rgb(86, 196, 99));
      integerMap2.put(4151, ColorUtils.rgb(147, 46, 230));
      integerMap2.put(4130, ColorUtils.rgb(230, 46, 46));
      integerMap2.put(4148, ColorUtils.rgb(16, 35, 179));
      integerMap2.put(4141, ColorUtils.rgb(55, 154, 184));
      integerMap2.put(4152, ColorUtils.rgb(184, 39, 159));
      integerMap2.put(4133, ColorUtils.rgb(255, 182, 56));
      integerMap2.put(4131, ColorUtils.rgb(232, 60, 30));
      integerMap2.put(4149, ColorUtils.rgb(0, 0, 0));
      integerMap2.put(4150, ColorUtils.rgb(0, 0, 0));
      integerMap2.put(4137, ColorUtils.rgb(37, 232, 30));
      integerMap2.put(4129, ColorUtils.rgb(232, 30, 30));
      integerMap2.put(4145, ColorUtils.rgb(30, 134, 232));
      integerMap2.put(4143, ColorUtils.rgb(89, 167, 227));
      integerMap2.put(4146, ColorUtils.rgb(62, 137, 194));
      integerMap2.put(4135, ColorUtils.rgb(56, 235, 74));
      integerMap2.put(4153, ColorUtils.rgb(173, 184, 174));
      integerMap2.put(4126, ColorUtils.rgb(255, 0, 25));
   }
}
