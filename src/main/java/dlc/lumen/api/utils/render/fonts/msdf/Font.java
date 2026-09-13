package dlc.lumen.api.utils.render.fonts.msdf;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.render.ShaderUtils;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class Font implements QClient {
   private static final char ch = '§';
   private final MsdfFont font;
   private final float stringWidth;

   public Font(MsdfFont font, float size) {
      this.font = font;
      this.stringWidth = size;
   }

   public Font(String name, float size) {
      this.font = MsdfFont.builder().atlas(name).data(name).build();
      this.stringWidth = size;
   }

   public void drawString(MatrixStack matrixStack, String text, double x, double y, int color) {
      this.draw(matrixStack, text, (float)x, (float)y, color);
   }

   public void drawString(MatrixStack matrixStack, String text, float x, float y, int color) {
      this.draw(matrixStack, text, x, y, color);
   }

   public void drawString(String text, float x, float y, int color) {
      MatrixStack var5 = new MatrixStack();
      this.draw(var5, text, x, y, color);
   }

   public void drawCenteredString(MatrixStack matrixStack, String text, double x, double y, int color) {
      this.draw(matrixStack, text, (float)(x - this.getStringWidth(text) / 2.0), (float)y, color);
   }

   public void drawCenteredString(MatrixStack matrixStack, String text, float x, float y, int color) {
      this.draw(matrixStack, text, x - this.getStringWidth(text) / 2.0F, y, color);
   }

   public void drawRight(MatrixStack matrixStack, String text, double x, double y, int color) {
      this.draw(matrixStack, text, (float)(x - this.getStringWidth(text)), (float)y, color);
   }

   public void drawRight(MatrixStack matrixStack, String text, float x, float y, int color) {
      this.draw(matrixStack, text, x - this.getStringWidth(text), y, color);
   }

   public void draw(MatrixStack stack, String text, double x, double y, int color) {
      this.draw(stack, text, (float)x, (float)y, color);
   }

   public void draw(MatrixStack stack, String text, float x, float y, int color) {
      if (text != null && !text.isEmpty()) {
         float var6 = this.stringWidth * 0.5F;
         if (this.checkText(text, var6)) {
            y -= 1.5F;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            ShaderProgram var7 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.fontsMsdf);
            if (var7 != null) {
               this.handleShader(var7, color);
               RenderSystem.setShaderTexture(0, this.font.getTextureId());
               this.font.setFiltered();
               Matrix4f var8 = stack.peek().getPositionMatrix();
               BufferBuilder var9 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               this.font.applyGlyphs(var8, var9, var6, text, 0.0F, x, y + this.font.getBaselineHeight() * var6, 0.0F, 255, 255, 255, 255);
               RenderSystem.setShader(ShaderUtils.fontsMsdf);
               BufferRenderer.drawWithGlobalProgram(var9.end());
               RenderSystem.setShaderTexture(0, 0);
               RenderSystem.enableCull();
               RenderSystem.disableBlend();
            }
         }
      }
   }

   public void drawGradientStringHorizontal(String text, float x, float y, int leftColor, int rightColor) {
      MatrixStack var6 = new MatrixStack();
      this.drawGradientStringHorizontal(var6, text, x, y, leftColor, rightColor);
   }

   public void drawGradientStringHorizontal(MatrixStack stack, String text, float x, float y, int leftColor, int rightColor) {
      if (text != null && !text.isEmpty()) {
         float var7 = this.getStringWidth(text);
         float var8 = x;

         for (int var9 = 0; var9 < text.length(); var9++) {
            char var10 = text.charAt(var9);
            String var11 = String.valueOf(var10);
            float var12 = this.getStringWidth(var11);
            float var13 = var7 > 0.0F ? (var8 - x) / var7 : 0.0F;
            int var14 = interpolateColor(leftColor, rightColor, var13);
            this.draw(stack, var11, var8, y, var14);
            var8 += var12;
         }
      }
   }

   public void drawGradientStringHorizontal(
      MatrixStack stack, String text, float x, float y, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor
   ) {
      if (text != null && !text.isEmpty()) {
         float var9 = this.getStringWidth(text);
         float var10 = x;

         for (int var11 = 0; var11 < text.length(); var11++) {
            char var12 = text.charAt(var11);
            String var13 = String.valueOf(var12);
            float var14 = this.getStringWidth(var13);
            float var15 = var9 > 0.0F ? (var10 - x) / var9 : 0.0F;
            int var16 = interpolateColor(topLeftColor, topRightColor, var15);
            int var17 = interpolateColor(bottomLeftColor, bottomRightColor, var15);
            int var18 = interpolateColor(var16, var17, 0.5F);
            this.draw(stack, var13, var10, y, var18);
            var10 += var14;
         }
      }
   }

   public void drawGradientStringVertical(MatrixStack stack, String text, float x, float y, int topColor, int bottomColor) {
      if (text != null && !text.isEmpty()) {
         int var7 = interpolateColor(topColor, bottomColor, 0.5F);
         this.draw(stack, text, x, y, var7);
      }
   }

   public void drawStringWithFade(MatrixStack stack, String text, float x, float y, float maxWidth, int color) {
      if (text != null && !text.isEmpty()) {
         if (!(maxWidth <= 1.0F)) {
            int var7 = color >>> 24 & 0xFF;
            if (var7 == 0) {
               var7 = 255;
            }

            if (var7 > 4) {
               float var8 = this.stringWidth * 0.5F;
               y -= 1.5F;
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableCull();
               ShaderProgram var9 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.fontsMsdf);
               if (var9 != null) {
                  GlUniform var10 = var9.getUniform("TextureSize");
                  GlUniform var11 = var9.getUniform("Range");
                  GlUniform var12 = var9.getUniform("Thickness");
                  GlUniform var13 = var9.getUniform("EdgeStrength");
                  GlUniform var14 = var9.getUniform("Color");
                  GlUniform var15 = var9.getUniform("Outline");
                  GlUniform var16 = var9.getUniform("OutlineThickness");
                  GlUniform var17 = var9.getUniform("OutlineColor");
                  if (var10 != null) {
                     var10.set(this.font.getAtlasWidth(), this.font.getAtlasHeight());
                  }

                  if (var11 != null) {
                     var11.set(this.font.getRange());
                  }

                  if (var12 != null) {
                     var12.set(0.0F);
                  }

                  if (var13 != null) {
                     var13.set(0.5F);
                  }

                  if (var15 != null) {
                     var15.set(0);
                  }

                  if (var16 != null) {
                     var16.set(0.0F);
                  }

                  if (var17 != null) {
                     var17.set(1.0F, 1.0F, 1.0F, 1.0F);
                  }

                  RenderSystem.setShaderTexture(0, this.font.getTextureId());
                  this.font.setFiltered();
                  float var18 = x;
                  float var19 = 25.0F;
                  float var20 = x + maxWidth - var19;

                  for (int var21 = 0; var21 < text.length(); var21++) {
                     String var22 = String.valueOf(text.charAt(var21));
                     float var23 = this.getStringWidth(var22);
                     if (var18 > x + maxWidth && var21 > 0) {
                        break;
                     }

                     int var24 = color;
                     if (var18 > var20) {
                        float var25 = (var18 - var20) / var19;
                        var25 = Math.max(0.0F, Math.min(1.0F, var25));
                        float var26 = (float)Math.cos(var25 * Math.PI / 2.0);
                        int var27 = (int)(var7 * var26);
                        var24 = color & 16777215 | var27 << 24;
                     }

                     if (!var22.isBlank() && (var24 >>> 24 & 0xFF) > 4) {
                        float[] var31 = this.getColor(var24);
                        if (var14 != null) {
                           var14.set(var31[0], var31[1], var31[2], var31[3]);
                        }

                        Matrix4f var32 = stack.peek().getPositionMatrix();
                        BufferBuilder var33 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                        this.font
                           .applyGlyphs(var32, var33, var8, var22, 0.0F, var18, y + this.font.getBaselineHeight() * var8, 0.0F, 255, 255, 255, 255);
                        RenderSystem.setShader(ShaderUtils.fontsMsdf);
                        BuiltBuffer var28 = var33.endNullable();
                        if (var28 != null) {
                           BufferRenderer.drawWithGlobalProgram(var28);
                        }
                     }

                     var18 += var23;
                  }

                  RenderSystem.setShaderTexture(0, 0);
                  RenderSystem.enableCull();
                  RenderSystem.disableBlend();
               }
            }
         }
      }
   }

   public void drawAnimatedGradientStringHorizontal(String text, float x, float y, int leftColor, int rightColor, float speed) {
      MatrixStack var7 = new MatrixStack();
      this.drawAnimatedGradientStringHorizontal(var7, text, x, y, leftColor, rightColor, speed, 1.15F);
   }

   public void drawAnimatedGradientStringHorizontal(MatrixStack stack, String text, float x, float y, int leftColor, int rightColor, float speed) {
      this.drawAnimatedGradientStringHorizontal(stack, text, x, y, leftColor, rightColor, speed, 1.15F);
   }

   public void drawAnimatedGradientStringHorizontal(String text, float x, float y, int leftColor, int rightColor, float speed, float waveScale) {
      MatrixStack var8 = new MatrixStack();
      this.drawAnimatedGradientStringHorizontal(var8, text, x, y, leftColor, rightColor, speed, waveScale);
   }

   public void drawAnimatedGradientStringHorizontal(
      MatrixStack stack, String text, float x, float y, int leftColor, int rightColor, float speed, float waveScale
   ) {
      if (text != null && !text.isEmpty()) {
         float var9 = this.getStringWidth(text);
         float var10 = x;
         double var11 = System.currentTimeMillis() * 0.001 * Math.max(0.01F, speed) % 2.0;
         float var13 = Math.max(0.01F, waveScale);

         for (int var14 = 0; var14 < text.length(); var14++) {
            char var15 = text.charAt(var14);
            String var16 = String.valueOf(var15);
            float var17 = this.getStringWidth(var16);
            float var18 = var9 > 0.0F ? (var10 - x) / var9 : 0.0F;
            float var19 = calcValue(var18 * var13 + (float)var11);
            int var20 = interpolateColor(leftColor, rightColor, var19);
            this.draw(stack, var16, var10, y, var20);
            var10 += var17;
         }
      }
   }

   public void drawStringWithOutline(MatrixStack stack, String text, float x, float y, int color, int outlineColor) {
      if (text != null && !text.isEmpty()) {
         this.draw(stack, text, x - 1.0F, y, outlineColor);
         this.draw(stack, text, x + 1.0F, y, outlineColor);
         this.draw(stack, text, x, y - 1.0F, outlineColor);
         this.draw(stack, text, x, y + 1.0F, outlineColor);
         this.draw(stack, text, x, y, color);
      }
   }

   public void drawStringWithShadow(MatrixStack stack, String text, float x, float y, int color) {
      if (text != null && !text.isEmpty()) {
         int var6 = 1426063360;
         this.draw(stack, text, x + 1.0F, y + 1.0F, var6);
         this.draw(stack, text, x, y, color);
      }
   }

   public void drawParagraph(MatrixStack stack, String text, double x, double y, int defaultColor) {
      this.drawParagraph(stack, text, (float)x, (float)y, defaultColor);
   }

   public void drawParagraph(MatrixStack stack, String text, float x, float y, int defaultColor) {
      if (text != null && !text.isEmpty()) {
         float var6 = this.stringWidth * 0.5F;
         y -= 1.5F;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         ShaderProgram var7 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.fontsMsdf);
         if (var7 != null) {
            GlUniform var8 = var7.getUniform("TextureSize");
            GlUniform var9 = var7.getUniform("Range");
            GlUniform var10 = var7.getUniform("Thickness");
            GlUniform var11 = var7.getUniform("EdgeStrength");
            GlUniform var12 = var7.getUniform("Color");
            if (var8 != null) {
               var8.set(this.font.getAtlasWidth(), this.font.getAtlasHeight());
            }

            if (var9 != null) {
               var9.set(this.font.getRange());
            }

            if (var10 != null) {
               var10.set(0.0F);
            }

            if (var11 != null) {
               var11.set(0.5F);
            }

            RenderSystem.setShaderTexture(0, this.font.getTextureId());
            this.font.setFiltered();
            float var13 = x;
            int var14 = defaultColor;
            StringBuilder var15 = new StringBuilder();

            for (int var16 = 0; var16 < text.length(); var16++) {
               char var17 = text.charAt(var16);
               if (var17 == 167 && var16 + 1 < text.length()) {
                  if (!var15.isEmpty()) {
                     this.handleStack(stack, var12, var15.toString(), var13, y + this.font.getBaselineHeight() * var6, var6, var14);
                     var13 += this.getStringWidth(var15.toString());
                     var15.setLength(0);
                  }

                  char var18 = text.charAt(var16 + 1);
                  int var19 = this.calcCode(var18, defaultColor);
                  if (var19 != -1) {
                     var14 = var19;
                  }

                  var16++;
               } else {
                  var15.append(var17);
               }
            }

            if (!var15.isEmpty()) {
               this.handleStack(stack, var12, var15.toString(), var13, y + this.font.getBaselineHeight() * var6, var6, var14);
            }

            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
         }
      }
   }

   private void handleStack(MatrixStack stack, GlUniform colorUniform, String text, float x, float y, float size, int color) {
      if (this.checkText(text, size)) {
         float[] var8 = this.getColor(color);
         if (colorUniform != null) {
            colorUniform.set(var8[0], var8[1], var8[2], var8[3]);
         }

         Matrix4f var9 = stack.peek().getPositionMatrix();
         BufferBuilder var10 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         this.font.applyGlyphs(var9, var10, size, text, 0.0F, x, y, 0.0F, 255, 255, 255, 255);
         RenderSystem.setShader(ShaderUtils.fontsMsdf);
         BufferRenderer.drawWithGlobalProgram(var10.end());
      }
   }

   private boolean checkText(String text, float renderSize) {
      return text != null && !text.isEmpty() && this.font.getWidth(text, renderSize) > 0.0F;
   }

   private void handleShader(ShaderProgram shader, int color) {
      GlUniform var3 = shader.getUniform("TextureSize");
      GlUniform var4 = shader.getUniform("Range");
      GlUniform var5 = shader.getUniform("Thickness");
      GlUniform var6 = shader.getUniform("EdgeStrength");
      GlUniform var7 = shader.getUniform("Color");
      GlUniform var8 = shader.getUniform("Outline");
      GlUniform var9 = shader.getUniform("OutlineThickness");
      GlUniform var10 = shader.getUniform("OutlineColor");
      if (var3 != null) {
         var3.set(this.font.getAtlasWidth(), this.font.getAtlasHeight());
      }

      if (var4 != null) {
         var4.set(this.font.getRange());
      }

      if (var5 != null) {
         var5.set(0.0F);
      }

      if (var6 != null) {
         var6.set(0.5F);
      }

      if (var8 != null) {
         var8.set(0);
      }

      if (var9 != null) {
         var9.set(0.0F);
      }

      if (var10 != null) {
         var10.set(0.0F, 0.0F, 0.0F, 1.0F);
      }

      float[] var11 = this.getColor(color);
      if (var7 != null) {
         var7.set(var11[0], var11[1], var11[2], var11[3]);
      }
   }

   private int calcCode(char code, int defaultColor) {
      int var3 = defaultColor >> 24 & 0xFF;
      if (var3 == 0) {
         var3 = 255;
      }
      return switch (code) {
         case '0' -> var3 << 24 | 0;
         case '1' -> var3 << 24 | 170;
         case '2' -> var3 << 24 | 43520;
         case '3' -> var3 << 24 | 43690;
         case '4' -> var3 << 24 | 11141120;
         case '5' -> var3 << 24 | 11141290;
         case '6' -> var3 << 24 | 16755200;
         case '7' -> var3 << 24 | 11184810;
         case '8' -> var3 << 24 | 5592405;
         case '9' -> var3 << 24 | 5592575;
         default -> -1;
         case 'A', 'a' -> var3 << 24 | 5635925;
         case 'B', 'b' -> var3 << 24 | 5636095;
         case 'C', 'c' -> var3 << 24 | 16733525;
         case 'D', 'd' -> var3 << 24 | 16733695;
         case 'E', 'e' -> var3 << 24 | 16777045;
         case 'F', 'f' -> var3 << 24 | 16777215;
         case 'R', 'r' -> defaultColor;
      };
   }

   private float[] getColor(int color) {
      int var2 = color >> 24 & 0xFF;
      int var3 = color >> 16 & 0xFF;
      int var4 = color >> 8 & 0xFF;
      int var5 = color & 0xFF;
      if (var2 == 0) {
         var2 = 255;
      }

      return new float[]{var3 / 255.0F, var4 / 255.0F, var5 / 255.0F, var2 / 255.0F};
   }

   public static int interpolateColor(int color1, int color2, float progress) {
      progress = Math.max(0.0F, Math.min(1.0F, progress));
      int var3 = color1 >> 24 & 0xFF;
      int var4 = color1 >> 16 & 0xFF;
      int var5 = color1 >> 8 & 0xFF;
      int var6 = color1 & 0xFF;
      int var7 = color2 >> 24 & 0xFF;
      int var8 = color2 >> 16 & 0xFF;
      int var9 = color2 >> 8 & 0xFF;
      int var10 = color2 & 0xFF;
      if (var3 == 0) {
         var3 = 255;
      }

      if (var7 == 0) {
         var7 = 255;
      }

      int var11 = (int)(var3 + (var7 - var3) * progress);
      int var12 = (int)(var4 + (var8 - var4) * progress);
      int var13 = (int)(var5 + (var9 - var5) * progress);
      int var14 = (int)(var6 + (var10 - var6) * progress);
      return var11 << 24 | var12 << 16 | var13 << 8 | var14;
   }

   private static float calcValue(float value) {
      float var1 = value % 2.0F;
      if (var1 < 0.0F) {
         var1 += 2.0F;
      }

      return var1 > 1.0F ? 2.0F - var1 : var1;
   }

   public float getStringWidth(String text) {
      return text == null ? 0.0F : this.font.getWidth(this.resolveText(text), this.stringWidth) / 2.0F;
   }

   public float getWidth(String text) {
      return this.getStringWidth(text);
   }

   public float getHeight() {
      return this.stringWidth;
   }

   public float getFontHeight() {
      return this.stringWidth;
   }

   public MsdfFont getFont() {
      return this.font;
   }

   public float getSize() {
      return this.stringWidth;
   }

   private String resolveText(String text) {
      if (text != null && text.indexOf(167) >= 0) {
         StringBuilder var2 = new StringBuilder(text.length());

         for (int var3 = 0; var3 < text.length(); var3++) {
            char var4 = text.charAt(var3);
            if (var4 == 167 && var3 + 1 < text.length()) {
               var3++;
            } else {
               var2.append(var4);
            }
         }

         return var2.toString();
      } else {
         return text;
      }
   }
}
