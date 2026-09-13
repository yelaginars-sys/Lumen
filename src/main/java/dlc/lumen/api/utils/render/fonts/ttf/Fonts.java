package dlc.lumen.api.utils.render.fonts.ttf;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.util.Identifier;

public class Fonts {
   private static final String text = "lumen";
   private static final Map<String, Map<Float, MCFontRenderer>> stringMap = new HashMap<>();
   private static final Map<String, Map<Float, GradientFontRenderer>> stringMap2 = new HashMap<>();
   public static MCFontRenderer comfortaa16;
   public static MCFontRenderer comfortaa18;
   public static MCFontRenderer comfortaa20;
   public static GradientFontRenderer comfortaaGradient18;
   public static MCFontRenderer roboto16;
   public static MCFontRenderer roboto18;
   public static MCFontRenderer roboto20;
   public static GradientFontRenderer robotoGradient18;
   public static MCFontRenderer montserrat16;
   public static MCFontRenderer montserrat18;
   public static MCFontRenderer montserrat20;
   public static GradientFontRenderer montserratGradient18;
   private static boolean flag = false;

   public static void init() {
      if (!flag) {
         comfortaa16 = getFont("comfortaa.ttf", 16.0F);
         comfortaa18 = getFont("comfortaa.ttf", 18.0F);
         comfortaa20 = getFont("comfortaa.ttf", 20.0F);
         comfortaaGradient18 = getGradientFont("comfortaa.ttf", 18.0F);
         roboto16 = getFont("roboto.ttf", 16.0F);
         roboto18 = getFont("roboto.ttf", 18.0F);
         roboto20 = getFont("roboto.ttf", 20.0F);
         robotoGradient18 = getGradientFont("roboto.ttf", 18.0F);
         montserrat16 = getFont("montserrat.ttf", 16.0F);
         montserrat18 = getFont("montserrat.ttf", 18.0F);
         montserrat20 = getFont("montserrat.ttf", 20.0F);
         montserratGradient18 = getGradientFont("montserrat.ttf", 18.0F);
         flag = true;
      }
   }

   public static MCFontRenderer getFont(String fontName, float size) {
      stringMap.computeIfAbsent(fontName, k -> new HashMap<>());
      Map var2 = stringMap.get(fontName);
      if (var2.containsKey(size)) {
         return (MCFontRenderer)var2.get(size);
      }

      Font var3 = FontUtil.getFontFromTTF(Identifier.of("lumen", "fonts/ttf/" + fontName), size, 0);
      if (var3 == null) {
         var3 = new Font("Arial", 0, (int)size);
      }

      MCFontRenderer var4 = new MCFontRenderer(var3, true, true);
      var2.put(size, var4);
      return var4;
   }

   public static GradientFontRenderer getGradientFont(String fontName, float size) {
      stringMap2.computeIfAbsent(fontName, k -> new HashMap<>());
      Map var2 = stringMap2.get(fontName);
      if (var2.containsKey(size)) {
         return (GradientFontRenderer)var2.get(size);
      }

      Font var3 = FontUtil.getFontFromTTF(Identifier.of("lumen", "fonts/" + fontName), size, 0);
      if (var3 == null) {
         var3 = new Font("Arial", 0, (int)size);
      }

      GradientFontRenderer var4 = new GradientFontRenderer(var3, true, true);
      var2.put(size, var4);
      return var4;
   }

   public static void drawStringWithFade(MCFontRenderer font, String text, float x, float y, float maxWidth, int color) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         float var6 = x;
         float var7 = Math.min(22.0F, Math.max(8.0F, maxWidth * 0.35F));
         float var8 = x + maxWidth - var7;
         int var9 = color >> 24 & 0xFF;

         for (int var10 = 0; var10 < text.length(); var10++) {
            String var11 = String.valueOf(text.charAt(var10));
            float var12 = font.getStringWidth(var11);
            if (var6 > x + maxWidth && var10 > 0) {
               break;
            }

            int var13 = color;
            if (var6 > var8) {
               float var14 = (var6 - var8) / var7;
               var14 = Math.max(0.0F, Math.min(1.0F, var14));
               float var15 = (float)Math.cos(var14 * Math.PI / 2.0);
               int var16 = (int)(var9 * var15);
               var13 = color & 16777215 | var16 << 24;
            }

            if ((var13 >> 24 & 0xFF) > 4) {
               font.drawString(var11, var6, y, var13);
            }

            var6 += var12;
         }
      }
   }

   public static MCFontRenderer getSystemFont(String fontName, float size) {
      String var2 = "system_" + fontName;
      stringMap.computeIfAbsent(var2, k -> new HashMap<>());
      Map var3 = stringMap.get(var2);
      if (var3.containsKey(size)) {
         return (MCFontRenderer)var3.get(size);
      }

      Font var4 = new Font(fontName, 0, (int)size);
      MCFontRenderer var5 = new MCFontRenderer(var4, true, true);
      var3.put(size, var5);
      return var5;
   }

   public static MCFontRenderer getSystemFont(String fontName, float size, int style) {
      String var3 = "system_" + fontName + "_" + style;
      stringMap.computeIfAbsent(var3, k -> new HashMap<>());
      Map var4 = stringMap.get(var3);
      if (var4.containsKey(size)) {
         return (MCFontRenderer)var4.get(size);
      }

      Font var5 = new Font(fontName, style, (int)size);
      MCFontRenderer var6 = new MCFontRenderer(var5, true, true);
      var4.put(size, var6);
      return var6;
   }

   public static void clearCache() {
      stringMap.clear();
      stringMap2.clear();
      flag = false;
   }

   public static void clearCache(String fontName) {
      stringMap.remove(fontName);
      stringMap2.remove(fontName);
   }

   @Generated
   public static boolean isInitialized() {
      return flag;
   }
}
