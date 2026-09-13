package dlc.lumen.api.utils.render.fonts.msdf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dlc.lumen.api.QClient;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class MsdfFont implements QClient {
   private final String name;
   private final AbstractTexture filter;
   private final float glId;
   private final float atlasHeight;
   private final float range;
   private final float lineHeight;
   private final float ascender;
   private final float descender;
   private final HashMap<Integer, MsdfGlyph> glyphs;
   private boolean filtered = false;

   private MsdfFont(
      String name,
      AbstractTexture texture,
      float atlasWidth,
      float atlasHeight,
      float range,
      float lineHeight,
      float ascender,
      float descender,
      HashMap<Integer, MsdfGlyph> glyphs
   ) {
      this.name = name;
      this.filter = texture;
      this.glId = atlasWidth;
      this.atlasHeight = atlasHeight;
      this.range = range;
      this.lineHeight = lineHeight;
      this.ascender = ascender;
      this.descender = descender;
      this.glyphs = glyphs;
   }

   public void setFiltered() {
      if (!this.filtered) {
         this.filter.setFilter(true, false);
         this.filtered = true;
      }
   }

   public int getTextureId() {
      return this.filter.getGlId();
   }

   public float getAtlasWidth() {
      return this.glId;
   }

   public float getAtlasHeight() {
      return this.atlasHeight;
   }

   public float getRange() {
      return this.range;
   }

   public float getLineHeight() {
      return this.lineHeight;
   }

   public float getBaselineHeight() {
      return this.lineHeight + this.descender;
   }

   public String getName() {
      return this.name;
   }

   public void applyGlyphs(
      Matrix4f matrix, VertexConsumer consumer, float size, String text, float thickness, float x, float y, float z, int red, int green, int blue, int alpha
   ) {
      text = resolveText(text);

      for (int var13 = 0; var13 < text.length(); var13++) {
         char var14 = text.charAt(var13);
         if (var14 == 167 && var13 + 1 < text.length()) {
            var13++;
         } else {
            MsdfGlyph var15 = this.glyphs.get(Integer.valueOf(var14));
            if (var15 != null) {
               x += var15.apply(matrix, consumer, size, x, y, z, red, green, blue, alpha) + thickness;
            }
         }
      }
   }

   public float getWidth(String text, float size) {
      text = resolveText(text);
      float var3 = 0.0F;

      for (int var4 = 0; var4 < text.length(); var4++) {
         char var5 = text.charAt(var4);
         if (var5 == 167 && var4 + 1 < text.length()) {
            var4++;
         } else {
            MsdfGlyph var6 = this.glyphs.get(Integer.valueOf(var5));
            if (var6 != null) {
               var3 += var6.getWidth(size);
            }
         }
      }

      return var3;
   }

   private static String resolveText(String text) {
      return text == null
         ? ""
         : text.replace("ᴀ", "A")
            .replace("ʙ", "B")
            .replace("ᴄ", "C")
            .replace("ᴅ", "D")
            .replace("ᴇ", "E")
            .replace("ғ", "F")
            .replace("ɢ", "G")
            .replace("ʜ", "H")
            .replace("ɪ", "I")
            .replace("ᴊ", "J")
            .replace("ᴋ", "K")
            .replace("ʟ", "L")
            .replace("ᴍ", "M")
            .replace("ɴ", "N")
            .replace("ᴏ", "O")
            .replace("ᴘ", "P")
            .replace("ǫ", "Q")
            .replace("ʀ", "R")
            .replace("ꜱ", "S")
            .replace("ᴛ", "T")
            .replace("ᴜ", "U")
            .replace("ᴠ", "V")
            .replace("ᴡ", "W")
            .replace("ʏ", "Y")
            .replace("ᴢ", "Z")
            .replace("ꜰ", "F");
   }

   private static String resolveIdentifier(Identifier identifier) {
      try {
         InputStream var1 = mc.getResourceManager().open(identifier);
         BufferedReader var2 = new BufferedReader(new InputStreamReader(var1));
         String var3 = var2.lines().collect(Collectors.joining("\n"));
         var2.close();
         var1.close();
         return var3;
      } catch (Exception var4) {
         throw new RuntimeException("Failed to read resource: " + identifier, var4);
      }
   }

   public static MsdfFont.Builder builder() {
      return new MsdfFont.Builder();
   }

   public static class Builder {
      private String name = "?";
      private Identifier filter;
      private Identifier glId;

      public MsdfFont.Builder name(String name) {
         this.name = name;
         return this;
      }

      public MsdfFont.Builder data(String dataFileName) {
         this.filter = Identifier.of("lumen", "fonts/msdf/" + dataFileName + "/font.json");
         return this;
      }

      public MsdfFont.Builder atlas(String atlasFileName) {
         this.glId = Identifier.of("lumen", "fonts/msdf/" + atlasFileName + "/font.png");
         return this;
      }

      public MsdfFont.Builder dataFile(String dataPath) {
         this.filter = Identifier.of("lumen", dataPath);
         return this;
      }

      public MsdfFont.Builder atlasFile(String atlasPath) {
         this.glId = Identifier.of("lumen", atlasPath);
         return this;
      }

      public MsdfFont build() {
         String var1 = MsdfFont.resolveIdentifier(this.filter);
         JsonObject var2 = JsonParser.parseString(var1).getAsJsonObject();
         JsonObject var3 = var2.getAsJsonObject("atlas");
         float var4 = var3.get("width").getAsFloat();
         float var5 = var3.get("height").getAsFloat();
         float var6 = var3.get("distanceRange").getAsFloat();
         JsonObject var7 = var2.getAsJsonObject("metrics");
         float var8 = var7.get("lineHeight").getAsFloat();
         float var9 = var7.get("ascender").getAsFloat();
         float var10 = var7.get("descender").getAsFloat();
         HashMap var11 = new HashMap();

         for (JsonElement var14 : var2.getAsJsonArray("glyphs")) {
            JsonObject var15 = var14.getAsJsonObject();
            int var16 = var15.get("unicode").getAsInt();
            float var17 = var15.get("advance").getAsFloat();
            float var18 = 0.0F;
            float var19 = 0.0F;
            float var20 = 0.0F;
            float var21 = 0.0F;
            if (var15.has("planeBounds") && !var15.get("planeBounds").isJsonNull()) {
               JsonObject var22 = var15.getAsJsonObject("planeBounds");
               var18 = var22.get("left").getAsFloat();
               var19 = var22.get("top").getAsFloat();
               var20 = var22.get("right").getAsFloat();
               var21 = var22.get("bottom").getAsFloat();
            }

            float var28 = 0.0F;
            float var23 = 0.0F;
            float var24 = 0.0F;
            float var25 = 0.0F;
            if (var15.has("atlasBounds") && !var15.get("atlasBounds").isJsonNull()) {
               JsonObject var26 = var15.getAsJsonObject("atlasBounds");
               var28 = var26.get("left").getAsFloat();
               var23 = var26.get("top").getAsFloat();
               var24 = var26.get("right").getAsFloat();
               var25 = var26.get("bottom").getAsFloat();
            }

            MsdfGlyph var29 = new MsdfGlyph(var16, var17, var18, var19, var20, var21, var28, var23, var24, var25, var4, var5);
            var11.put(var16, var29);
         }

         AbstractTexture var27 = QClient.mc.getTextureManager().getTexture(this.glId);
         return new MsdfFont(this.name, var27, var4, var5, var6, var8, var9, var10, var11);
      }
   }
}
