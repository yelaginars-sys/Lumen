package dlc.lumen.api.utils.render.fonts.ttf;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Font;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public class GradientFontRenderer extends MCFontRenderer {
   public GradientFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      super(font, antiAlias, fractionalMetrics);
   }

   public int drawGradientString(String text, float x, float y, int topColor, int bottomColor, boolean dropShadow, boolean horizontal) {
      int var9;
      if (dropShadow) {
         var9 = this.calcText(text, x + 1.0F, y + 1.0F, topColor, bottomColor, true, horizontal);
         var9 = Math.max(var9, this.calcText(text, x, y, topColor, bottomColor, false, horizontal));
      } else {
         var9 = this.calcText(text, x, y, topColor, bottomColor, false, horizontal);
      }

      return var9;
   }

   private int calcText(String text, float x, float y, int startColor, int endColor, boolean dropShadow, boolean horizontal) {
      if (text == null) {
         return 0;
      }

      if ((startColor & -67108864) == 0) {
         startColor |= -16777216;
      }

      if ((endColor & -67108864) == 0) {
         endColor |= -16777216;
      }

      if (dropShadow) {
         startColor = (startColor & 16579836) >> 2 | startColor & 0xFF000000;
         endColor = (endColor & 16579836) >> 2 | endColor & 0xFF000000;
      }

      float var8 = x;
      float var9 = y;
      return this.calcText2(text, var8, var9, dropShadow, startColor, endColor, horizontal);
   }

   private int calcText2(String text, float posX, float posY, boolean shadow, int startColor, int endColor, boolean horizontal) {
      float var8 = this.getStringWidth(text);
      float var9 = 0.0F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Matrix4f var10 = new Matrix4f();

      for (int var11 = 0; var11 < text.length(); var11++) {
         char var12 = text.charAt(var11);
         if (var12 == ' ' || var12 == 160) {
            posX += 4.0F;
         } else if (var12 < this.charData.length && this.charData[var12] != null) {
            float var13 = this.charData[var12].width - 8 + this.charOffset;
            if (horizontal) {
               float var14 = var9 / var8;
               float var15 = (var9 + var13) / var8;
               int var16 = this.calcStartColor(startColor, endColor, var14);
               int var17 = this.calcStartColor(startColor, endColor, var15);
               this.handleCh(var12, posX, posY, var16, var17, true, var10);
               var9 += var13;
            } else {
               this.handleCh(var12, posX, posY, startColor, endColor, false, var10);
            }

            posX += var13;
         }
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      return (int)posX;
   }

   private int calcStartColor(int startColor, int endColor, float mix) {
      float var4 = (startColor >> 24 & 0xFF) / 255.0F;
      float var5 = (startColor >> 16 & 0xFF) / 255.0F;
      float var6 = (startColor >> 8 & 0xFF) / 255.0F;
      float var7 = (startColor & 0xFF) / 255.0F;
      float var8 = (endColor >> 24 & 0xFF) / 255.0F;
      float var9 = (endColor >> 16 & 0xFF) / 255.0F;
      float var10 = (endColor >> 8 & 0xFF) / 255.0F;
      float var11 = (endColor & 0xFF) / 255.0F;
      int var12 = (int)(((1.0F - mix) * var4 + mix * var8) * 255.0F);
      int var13 = (int)(((1.0F - mix) * var5 + mix * var9) * 255.0F);
      int var14 = (int)(((1.0F - mix) * var6 + mix * var10) * 255.0F);
      int var15 = (int)(((1.0F - mix) * var7 + mix * var11) * 255.0F);
      return var12 << 24 | var13 << 16 | var14 << 8 | var15;
   }

   private void handleCh(char ch, float posX, float posY, int startColor, int endColor, boolean horizontal, Matrix4f matrix) {
      if (ch < this.charData.length && this.charData[ch] != null) {
         float var8 = (startColor >> 24 & 0xFF) / 255.0F;
         float var9 = (startColor >> 16 & 0xFF) / 255.0F;
         float var10 = (startColor >> 8 & 0xFF) / 255.0F;
         float var11 = (startColor & 0xFF) / 255.0F;
         float var12 = (endColor >> 24 & 0xFF) / 255.0F;
         float var13 = (endColor >> 16 & 0xFF) / 255.0F;
         float var14 = (endColor >> 8 & 0xFF) / 255.0F;
         float var15 = (endColor & 0xFF) / 255.0F;
         CFont.CharData var16 = this.charData[ch];
         float var17 = var16.storedX;
         float var18 = var16.storedY;
         int var19 = var16.width;
         float var20 = var19 - 0.01F;
         float var21 = var17 / 512.0F;
         float var22 = var18 / 512.0F;
         float var23 = (var17 + var20 - 1.0F) / 512.0F;
         float var24 = (var18 + 7.99F) / 512.0F;
         RenderSystem.setShaderTexture(0, this.glTextureId);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         Tessellator var25 = Tessellator.getInstance();
         BufferBuilder var26 = var25.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         if (horizontal) {
            var26.vertex(matrix, posX, posY, 0.0F).texture(var21, var22).color(var9, var10, var11, var8);
            var26.vertex(matrix, posX, posY + 7.99F, 0.0F).texture(var21, var24).color(var9, var10, var11, var8);
            var26.vertex(matrix, posX + var20 - 1.0F, posY + 7.99F, 0.0F).texture(var23, var24).color(var13, var14, var15, var12);
            var26.vertex(matrix, posX + var20 - 1.0F, posY, 0.0F).texture(var23, var22).color(var13, var14, var15, var12);
         } else {
            var26.vertex(matrix, posX, posY, 0.0F).texture(var21, var22).color(var9, var10, var11, var8);
            var26.vertex(matrix, posX, posY + 7.99F, 0.0F).texture(var21, var24).color(var13, var14, var15, var12);
            var26.vertex(matrix, posX + var20 - 1.0F, posY + 7.99F, 0.0F).texture(var23, var24).color(var13, var14, var15, var12);
            var26.vertex(matrix, posX + var20 - 1.0F, posY, 0.0F).texture(var23, var22).color(var9, var10, var11, var8);
         }

         BufferRenderer.drawWithGlobalProgram(var26.end());
      }
   }
}
