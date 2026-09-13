package dlc.lumen.api.utils.render.fonts.ttf;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.awt.Font;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public class MCFontRenderer extends CFont {
   private final int[] countArray = new int[32];
   protected CFont.CharData[] boldChars = new CFont.CharData[1104];
   protected CFont.CharData[] italicChars = new CFont.CharData[1104];
   protected CFont.CharData[] boldItalicChars = new CFont.CharData[1104];
   protected int texBold;
   protected int texItalic;
   protected int texItalicBold;

   public MCFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      super(font, antiAlias, fractionalMetrics);
      this.handleAction();

      for (int var4 = 0; var4 < 32; var4++) {
         int var5 = (var4 >> 3 & 1) * 85;
         int var6 = (var4 >> 2 & 1) * 170 + var5;
         int var7 = (var4 >> 1 & 1) * 170 + var5;
         int var8 = (var4 & 1) * 170 + var5;
         if (var4 == 6) {
            var6 += 85;
         }

         if (var4 >= 16) {
            var6 /= 4;
            var7 /= 4;
            var8 /= 4;
         }

         this.countArray[var4] = (var6 & 0xFF) << 16 | (var7 & 0xFF) << 8 | var8 & 0xFF;
      }
   }

   public float drawStringWithShadow(String text, double x, double y, int color) {
      float var7 = this.drawString(text, x + 0.5, y + 0.5, color, true);
      return Math.max(var7, this.drawString(text, x, y, color, false));
   }

   public float drawGradientString(String text, float x, float y, int topColor, int bottomColor) {
      if (text == null) {
         return 0.0F;
      }

      x--;
      if ((topColor & -67108864) == 0) {
         topColor |= -16777216;
      }

      if ((bottomColor & -67108864) == 0) {
         bottomColor |= -16777216;
      }

      float var6 = (topColor >> 24 & 0xFF) / 255.0F;
      float var7 = (topColor >> 16 & 0xFF) / 255.0F;
      float var8 = (topColor >> 8 & 0xFF) / 255.0F;
      float var9 = (topColor & 0xFF) / 255.0F;
      float var10 = (bottomColor >> 24 & 0xFF) / 255.0F;
      float var11 = (bottomColor >> 16 & 0xFF) / 255.0F;
      float var12 = (bottomColor >> 8 & 0xFF) / 255.0F;
      float var13 = (bottomColor & 0xFF) / 255.0F;
      double var14 = x * 2.0;
      double var16 = (y - 3.0) * 2.0;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Matrix4f var18 = new Matrix4f();
      var18.scale(0.5F, 0.5F, 0.5F);
      CFont.CharData[] var19 = this.charData;
      int var20 = text.length();
      RenderSystem.setShaderTexture(0, this.glTextureId);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      Tessellator var21 = Tessellator.getInstance();
      BufferBuilder var22 = var21.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int var23 = 0; var23 < var20; var23++) {
         char var24 = text.charAt(var23);
         if (var24 < var19.length && var19[var24] != null) {
            CFont.CharData var25 = var19[var24];
            float var26 = var25.storedX;
            float var27 = var25.storedY;
            float var28 = var25.width;
            float var29 = var25.height;
            float var30 = var26 / 512.0F;
            float var31 = var27 / 512.0F;
            float var32 = (var26 + var28) / 512.0F;
            float var33 = (var27 + var29) / 512.0F;
            var22.vertex(var18, (float)var14, (float)var16, 0.0F).texture(var30, var31).color(var7, var8, var9, var6);
            var22.vertex(var18, (float)var14, (float)var16 + var29, 0.0F).texture(var30, var33).color(var11, var12, var13, var10);
            var22.vertex(var18, (float)var14 + var28, (float)var16 + var29, 0.0F).texture(var32, var33).color(var11, var12, var13, var10);
            var22.vertex(var18, (float)var14 + var28, (float)var16, 0.0F).texture(var32, var31).color(var7, var8, var9, var6);
            var14 += var25.width - 8 + this.charOffset;
         } else if (var24 == ' ' || var24 == 160) {
            var14 += 8.0;
         }
      }

      BufferRenderer.drawWithGlobalProgram(var22.end());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      return (float)var14 / 2.0F;
   }

   public float drawGradientStringHorizontal(String text, float x, float y, int leftColor, int rightColor) {
      if (text == null) {
         return 0.0F;
      }

      x--;
      if ((leftColor & -67108864) == 0) {
         leftColor |= -16777216;
      }

      if ((rightColor & -67108864) == 0) {
         rightColor |= -16777216;
      }

      double var6 = x * 2.0;
      double var8 = (y - 3.0) * 2.0;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Matrix4f var10 = new Matrix4f();
      var10.scale(0.5F, 0.5F, 0.5F);
      CFont.CharData[] var11 = this.charData;
      int var12 = text.length();
      float var13 = this.getStringWidth(text) * 2.0F;
      float var14 = 0.0F;
      RenderSystem.setShaderTexture(0, this.glTextureId);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      Tessellator var15 = Tessellator.getInstance();
      BufferBuilder var16 = var15.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int var17 = 0; var17 < var12; var17++) {
         char var18 = text.charAt(var17);
         if (var18 < var11.length && var11[var18] != null) {
            CFont.CharData var19 = var11[var18];
            float var20 = var19.storedX;
            float var21 = var19.storedY;
            float var22 = var19.width;
            float var23 = var19.height;
            float var24 = var19.width - 8 + this.charOffset;
            float var25 = var20 / 512.0F;
            float var26 = var21 / 512.0F;
            float var27 = (var20 + var22) / 512.0F;
            float var28 = (var21 + var23) / 512.0F;
            float var29 = var13 <= 0.0F ? 0.0F : var14 / var13;
            float var30 = var13 <= 0.0F ? 1.0F : (var14 + var24) / var13;
            int var31 = this.calcStartColor(leftColor, rightColor, var29);
            int var32 = this.calcStartColor(leftColor, rightColor, var30);
            float var33 = (var31 >> 24 & 0xFF) / 255.0F;
            float var34 = (var31 >> 16 & 0xFF) / 255.0F;
            float var35 = (var31 >> 8 & 0xFF) / 255.0F;
            float var36 = (var31 & 0xFF) / 255.0F;
            float var37 = (var32 >> 24 & 0xFF) / 255.0F;
            float var38 = (var32 >> 16 & 0xFF) / 255.0F;
            float var39 = (var32 >> 8 & 0xFF) / 255.0F;
            float var40 = (var32 & 0xFF) / 255.0F;
            var16.vertex(var10, (float)var6, (float)var8, 0.0F).texture(var25, var26).color(var34, var35, var36, var33);
            var16.vertex(var10, (float)var6, (float)var8 + var23, 0.0F).texture(var25, var28).color(var34, var35, var36, var33);
            var16.vertex(var10, (float)var6 + var22, (float)var8 + var23, 0.0F).texture(var27, var28).color(var38, var39, var40, var37);
            var16.vertex(var10, (float)var6 + var22, (float)var8, 0.0F).texture(var27, var26).color(var38, var39, var40, var37);
            var6 += var24;
            var14 += var24;
         } else if (var18 == ' ' || var18 == 160) {
            var6 += 8.0;
            var14 += 8.0F;
         }
      }

      BufferRenderer.drawWithGlobalProgram(var16.end());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      return (float)var6 / 2.0F;
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

   public float drawString(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, false);
   }

   public float drawCenteredString(String text, float x, float y, int color) {
      return this.drawString(text, x - this.getStringWidth(text) / 2.0F, y, color);
   }

   public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
      return this.drawStringWithShadow(text, x - this.getStringWidth(text) / 2.0F, y, color);
   }

   public float drawString(String text, double x, double y, int color, boolean shadow) {
      x--;
      if (text == null) {
         return 0.0F;
      }

      if (color == 553648127) {
         color = 16777215;
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (shadow) {
         color = (color & 16579836) >> 2 | color & -938208236;
      }

      CFont.CharData[] var8 = this.charData;
      float var9 = (color >> 24 & 0xFF) / 255.0F;
      boolean var10 = false;
      boolean var11 = false;
      boolean var12 = false;
      boolean var13 = false;
      x *= 2.0;
      y = (y - 3.0) * 2.0;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, var9);
      Matrix4f var14 = new Matrix4f();
      var14.scale(0.5F, 0.5F, 0.5F);
      int var15 = text.length();
      int var16 = this.glTextureId;
      RenderSystem.setShaderTexture(0, var16);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

      for (int var17 = 0; var17 < var15; var17++) {
         char var18 = text.charAt(var17);
         if (var18 == 167 && var17 < var15 - 1) {
            int var25 = 21;

            try {
               var25 = "0123456789abcdefklmnor".indexOf(text.charAt(var17 + 1));
            } catch (Exception var21) {
               var21.printStackTrace();
            }

            if (var25 >= 16) {
               if (var25 == 17) {
                  var10 = true;
                  if (var11) {
                     var16 = this.texItalicBold;
                     var8 = this.boldItalicChars;
                  } else {
                     var16 = this.texBold;
                     var8 = this.boldChars;
                  }
               } else if (var25 == 18) {
                  var12 = true;
               } else if (var25 == 19) {
                  var13 = true;
               } else if (var25 == 20) {
                  var11 = true;
                  if (var10) {
                     var16 = this.texItalicBold;
                     var8 = this.boldItalicChars;
                  } else {
                     var16 = this.texItalic;
                     var8 = this.italicChars;
                  }
               } else if (var25 == 21) {
                  var10 = false;
                  var11 = false;
                  var13 = false;
                  var12 = false;
                  RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, var9);
                  var16 = this.glTextureId;
                  var8 = this.charData;
               }
            } else {
               var10 = false;
               var11 = false;
               var13 = false;
               var12 = false;
               var16 = this.glTextureId;
               var8 = this.charData;
               if (var25 < 0 || var25 > 15) {
                  var25 = 15;
               }

               if (shadow) {
                  var25 += 16;
               }

               int var26 = this.countArray[var25];
               RenderSystem.setShaderColor((var26 >> 16 & 0xFF) / 255.0F, (var26 >> 8 & 0xFF) / 255.0F, (var26 & 0xFF) / 255.0F, var9);
            }

            var17++;
         } else if (var18 < var8.length && var8[var18] != null) {
            RenderSystem.setShaderTexture(0, var16);
            Tessellator var19 = Tessellator.getInstance();
            BufferBuilder var20 = var19.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE);
            this.drawChar(var8, var18, (float)x, (float)y, var14, var20);
            BufferRenderer.drawWithGlobalProgram(var20.end());
            if (var12) {
               this.handleX(x, y + var8[var18].height / 2.0F, x + var8[var18].width - 8.0, y + var8[var18].height / 2.0F, 1.0F, var14);
            }

            if (var13) {
               this.handleX(x, y + var8[var18].height - 2.0, x + var8[var18].width - 8.0, y + var8[var18].height - 2.0, 1.0F, var14);
            }

            x += var8[var18].width - 8 + this.charOffset;
         }
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      return (float)x / 2.0F;
   }

   @Override
   public int getStringWidth(String text) {
      int var2 = 0;
      CFont.CharData[] var3 = this.charData;
      boolean var4 = false;
      boolean var5 = false;
      int var6 = text.length();

      for (int var7 = 0; var7 < var6; var7++) {
         char var8 = text.charAt(var7);
         if (var8 == 167 && var7 < var6 - 1) {
            int var9 = "0123456789abcdefklmnor".indexOf(text.charAt(var7 + 1));
            if (var9 < 16) {
               var4 = false;
               var5 = false;
            } else if (var9 == 17) {
               var4 = true;
               var3 = var5 ? this.boldItalicChars : this.boldChars;
            } else if (var9 == 20) {
               var5 = true;
               var3 = var4 ? this.boldItalicChars : this.italicChars;
            } else if (var9 == 21) {
               var4 = false;
               var5 = false;
               var3 = this.charData;
            }

            var7++;
         } else if (var8 < var3.length && var3[var8] != null) {
            var2 += var3[var8].width - 8 + this.charOffset;
         }
      }

      return var2 / 2;
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
      this.handleAction();
   }

   @Override
   public void setAntiAlias(boolean antiAlias) {
      super.setAntiAlias(antiAlias);
      this.handleAction();
   }

   @Override
   public void setFractionalMetrics(boolean fractionalMetrics) {
      super.setFractionalMetrics(fractionalMetrics);
      this.handleAction();
   }

   private void handleAction() {
      CFont var1 = new CFont(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics);
      this.texBold = var1.getGlTextureId();
      this.boldChars = var1.charData;
      CFont var2 = new CFont(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics);
      this.texItalic = var2.getGlTextureId();
      this.italicChars = var2.charData;
      CFont var3 = new CFont(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics);
      this.texItalicBold = var3.getGlTextureId();
      this.boldItalicChars = var3.charData;
   }

   private void handleX(double x, double y, double x1, double y1, float width, Matrix4f matrix) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION);
      RenderSystem.lineWidth(width);
      Tessellator var11 = Tessellator.getInstance();
      BufferBuilder var12 = var11.begin(DrawMode.LINES, VertexFormats.POSITION);
      var12.vertex(matrix, (float)x, (float)y, 0.0F);
      var12.vertex(matrix, (float)x1, (float)y1, 0.0F);
      BufferRenderer.drawWithGlobalProgram(var12.end());
   }

   public void drawStringWithOutline(String text, double x, double y, int color) {
      this.drawString(text, x - 0.5, y, Color.BLACK.getRGB(), false);
      this.drawString(text, x + 0.5, y, Color.BLACK.getRGB(), false);
      this.drawString(text, x, y - 0.5, Color.BLACK.getRGB(), false);
      this.drawString(text, x, y + 0.5, Color.BLACK.getRGB(), false);
      this.drawString(text, x, y, color, false);
   }

   public void drawCenteredStringWithOutline(String text, float x, float y, int color) {
      this.drawCenteredString(text, x - 0.5F, y, Color.BLACK.getRGB());
      this.drawCenteredString(text, x + 0.5F, y, Color.BLACK.getRGB());
      this.drawCenteredString(text, x, y - 0.5F, Color.BLACK.getRGB());
      this.drawCenteredString(text, x, y + 0.5F, Color.BLACK.getRGB());
      this.drawCenteredString(text, x, y, color);
   }
}
