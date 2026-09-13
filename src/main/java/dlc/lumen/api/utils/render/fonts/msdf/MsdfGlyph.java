package dlc.lumen.api.utils.render.fonts.msdf;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class MsdfGlyph {
   private final int spaceChar2;
   private final float fvalue;
   private final float fvalue2;
   private final float fvalue3;
   private final float fvalue4;
   private final float advance;
   private final float planeTop;
   private final float spaceChar;
   private final float fvalue5;

   public MsdfGlyph(
      int unicode,
      float advance,
      float planeLeft,
      float planeTop,
      float planeRight,
      float planeBottom,
      float atlasLeft,
      float atlasTop,
      float atlasRight,
      float atlasBottom,
      float atlasWidth,
      float atlasHeight
   ) {
      this.spaceChar2 = unicode;
      this.advance = advance;
      if (atlasLeft == 0.0F && atlasRight == 0.0F && atlasTop == 0.0F && atlasBottom == 0.0F) {
         this.fvalue = 0.0F;
         this.fvalue2 = 0.0F;
         this.fvalue3 = 0.0F;
         this.fvalue4 = 0.0F;
      } else {
         this.fvalue = atlasLeft / atlasWidth;
         this.fvalue2 = atlasRight / atlasWidth;
         this.fvalue3 = 1.0F - atlasTop / atlasHeight;
         this.fvalue4 = 1.0F - atlasBottom / atlasHeight;
      }

      if (planeLeft == 0.0F && planeRight == 0.0F && planeTop == 0.0F && planeBottom == 0.0F) {
         this.spaceChar = 0.0F;
         this.fvalue5 = 0.0F;
         this.planeTop = 0.0F;
      } else {
         this.spaceChar = planeRight - planeLeft;
         this.fvalue5 = planeTop - planeBottom;
         this.planeTop = planeTop;
      }
   }

   public float apply(Matrix4f matrix, VertexConsumer consumer, float size, float x, float y, float z, int red, int green, int blue, int alpha) {
      y -= this.planeTop * size;
      y--;
      float var11 = this.spaceChar * size;
      float var12 = this.fvalue5 * size;
      consumer.vertex(matrix, x, y, z).color(red, green, blue, alpha).texture(this.fvalue, this.fvalue3);
      consumer.vertex(matrix, x, y + var12, z).color(red, green, blue, alpha).texture(this.fvalue, this.fvalue4);
      consumer.vertex(matrix, x + var11, y + var12, z).color(red, green, blue, alpha).texture(this.fvalue2, this.fvalue4);
      consumer.vertex(matrix, x + var11, y, z).color(red, green, blue, alpha).texture(this.fvalue2, this.fvalue3);
      return this.spaceChar * (size - 1.0F) + (Character.isSpaceChar(this.spaceChar2) ? this.advance * size : 0.0F);
   }

   public float getWidth(float size) {
      return this.spaceChar * (size - 1.0F) + (Character.isSpaceChar(this.spaceChar2) ? this.advance * size : 0.0F);
   }

   public int getCharCode() {
      return this.spaceChar2;
   }
}
