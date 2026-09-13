package dlc.lumen.api.utils.render.fonts.ttf;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class CFont {
   protected static final int IMG_SIZE = 512;
   protected CFont.CharData[] charData = new CFont.CharData[1104];
   protected Font font;
   protected boolean antiAlias;
   protected boolean fractionalMetrics;
   protected int fontHeight = -1;
   protected int charOffset = 0;
   protected Identifier textureId;
   protected int glTextureId;
   private static int count = 0;

   public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
      this.font = font;
      this.antiAlias = antiAlias;
      this.fractionalMetrics = fractionalMetrics;
      this.setupTexture(font, antiAlias, fractionalMetrics, this.charData);
   }

   protected void setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CFont.CharData[] chars) {
      BufferedImage var5 = this.generateFontImage(font, antiAlias, fractionalMetrics, chars);

      try {
         NativeImage var6 = new NativeImage(var5.getWidth(), var5.getHeight(), false);

         for (int var7 = 0; var7 < var5.getHeight(); var7++) {
            for (int var8 = 0; var8 < var5.getWidth(); var8++) {
               int var9 = var5.getRGB(var8, var7);
               int var10 = var9 >> 24 & 0xFF;
               int var11 = var9 >> 16 & 0xFF;
               int var12 = var9 >> 8 & 0xFF;
               int var13 = var9 & 0xFF;
               var6.setColorArgb(var8, var7, var10 << 24 | var11 << 16 | var12 << 8 | var13);
            }
         }

         NativeImageBackedTexture var15 = new NativeImageBackedTexture(var6);
         this.glTextureId = var15.getGlId();
         String var16 = "cfont_" + count++;
         this.textureId = Identifier.of("customfont", var16);
         MinecraftClient.getInstance().getTextureManager().registerTexture(this.textureId, var15);
      } catch (Exception var14) {
         var14.printStackTrace();
      }
   }

   protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CFont.CharData[] chars) {
      BufferedImage var5 = new BufferedImage(512, 512, 2);
      Graphics2D var6 = (Graphics2D)var5.getGraphics();
      var6.setFont(font);
      var6.setColor(new Color(255, 255, 255, 0));
      var6.fillRect(0, 0, 512, 512);
      var6.setColor(Color.WHITE);
      var6.setRenderingHint(
         RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
      );
      var6.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      var6.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
      FontMetrics var7 = var6.getFontMetrics();
      int var8 = 0;
      int var9 = 0;
      int var10 = 1;

      for (int var11 = 0; var11 < chars.length; var11++) {
         char var12 = (char)var11;
         if (var12 > 1039 && var12 < 1104 || var12 < 256) {
            CFont.CharData var13 = new CFont.CharData();
            Rectangle2D var14 = var7.getStringBounds(String.valueOf(var12), var6);
            var13.width = var14.getBounds().width + 8;
            var13.height = var14.getBounds().height;
            if (var9 + var13.width >= 512) {
               var9 = 0;
               var10 += var8;
               var8 = 0;
            }

            if (var13.height > var8) {
               var8 = var13.height;
            }

            var13.storedX = var9;
            var13.storedY = var10;
            if (var13.height > this.fontHeight) {
               this.fontHeight = var13.height;
            }

            chars[var11] = var13;
            var6.drawString(String.valueOf(var12), var9 + 2, var10 + var7.getAscent());
            var9 += var13.width;
         }
      }

      return var5;
   }

   public void drawChar(CFont.CharData[] chars, char c, float x, float y, Matrix4f matrix, BufferBuilder buffer) {
      try {
         if (chars[c] == null) {
            return;
         }

         this.drawQuad(x, y, chars[c].width, chars[c].height, chars[c].storedX, chars[c].storedY, chars[c].width, chars[c].height, matrix, buffer);
      } catch (Exception var8) {
      }
   }

   protected void drawQuad(
      float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight, Matrix4f matrix, BufferBuilder buffer
   ) {
      float var11 = srcX / 512.0F;
      float var12 = srcY / 512.0F;
      float var13 = srcWidth / 512.0F;
      float var14 = srcHeight / 512.0F;
      buffer.vertex(matrix, x + width, y, 0.0F).texture(var11 + var13, var12);
      buffer.vertex(matrix, x, y, 0.0F).texture(var11, var12);
      buffer.vertex(matrix, x, y + height, 0.0F).texture(var11, var12 + var14);
      buffer.vertex(matrix, x, y + height, 0.0F).texture(var11, var12 + var14);
      buffer.vertex(matrix, x + width, y + height, 0.0F).texture(var11 + var13, var12 + var14);
      buffer.vertex(matrix, x + width, y, 0.0F).texture(var11 + var13, var12);
   }

   public int getStringHeight(String text) {
      return this.getFontHeight();
   }

   public int getFontHeight() {
      return (this.fontHeight - 8) / 2;
   }

   public int getStringWidth(String text) {
      int var2 = 0;

      for (char var6 : text.toCharArray()) {
         if (var6 < this.charData.length && this.charData[var6] != null) {
            var2 += this.charData[var6].width - 8 + this.charOffset;
         }
      }

      return var2 / 2;
   }

   public void setAntiAlias(boolean antiAlias) {
      if (this.antiAlias != antiAlias) {
         this.antiAlias = antiAlias;
         this.setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
      }
   }

   public void setFractionalMetrics(boolean fractionalMetrics) {
      if (this.fractionalMetrics != fractionalMetrics) {
         this.fractionalMetrics = fractionalMetrics;
         this.setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
      }
   }

   public void setFont(Font font) {
      this.font = font;
      this.setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
   }

   @Generated
   public Font getFont() {
      return this.font;
   }

   @Generated
   public boolean isAntiAlias() {
      return this.antiAlias;
   }

   @Generated
   public boolean isFractionalMetrics() {
      return this.fractionalMetrics;
   }

   @Generated
   public Identifier getTextureId() {
      return this.textureId;
   }

   @Generated
   public int getGlTextureId() {
      return this.glTextureId;
   }

   protected static class CharData {
      public int width;
      public int height;
      public int storedX;
      public int storedY;
   }
}
