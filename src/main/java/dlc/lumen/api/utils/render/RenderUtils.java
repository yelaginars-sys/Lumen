package dlc.lumen.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.blur.BlurProgram;
import dlc.lumen.api.utils.render.glow.GlowCallback;
import dlc.lumen.api.utils.render.glow.GlowProgram;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import java.awt.Color;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class RenderUtils implements QClient {
   private static final ConcurrentHashMap<String, Identifier> stringMap = new ConcurrentHashMap<>();
   private static final UUID id = new UUID(0L, 0L);
   private static float fvalue = 1.0F;
   private static float fvalue2 = 1.0F;
   private static boolean flag = false;

   public static void beginHudRenderScale(float alpha, float blurMul) {
      fvalue = alpha < 0.0F ? 0.0F : (alpha > 1.0F ? 1.0F : alpha);
      fvalue2 = blurMul < 0.0F ? 0.0F : blurMul;
      flag = true;
   }

   public static void endHudRenderScale() {
      flag = false;
      fvalue = 1.0F;
      fvalue2 = 1.0F;
   }

   private static int calcColor(int color) {
      if (flag && !(fvalue >= 0.999F)) {
         int var1 = color >> 24 & 0xFF;
         if (var1 == 0) {
            var1 = 255;
         }

         int var2 = Math.round(var1 * fvalue);
         var2 = var2 < 0 ? 0 : (var2 > 255 ? 255 : var2);
         return color & 16777215 | var2 << 24;
      } else {
         return color;
      }
   }

   private static float calcStrength(float strength) {
      return flag ? strength * fvalue2 : strength;
   }

   public static void drawHudItem(DrawContext context, ItemStack stack, float x, float y, float scale, float z) {
      if (context != null && stack != null && !stack.isEmpty()) {
         MatrixStack var6 = context.getMatrices();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         var6.push();
         var6.translate(x, y, z);
         var6.scale(scale, scale, 1.0F);
         context.drawItem(stack, 0, 0);
         var6.pop();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(true);
      }
   }

   public static void drawGradient6Rect(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      int leftTopColor,
      int leftBottomColor,
      int centerTopColor,
      int centerBottomColor,
      int rightTopColor,
      int rightBottomColor
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram var12 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.gradient6Rect);
      Matrix4f var13 = matrices.peek().getPositionMatrix();
      GlUniform var14 = var12.getUniform("Size");
      GlUniform var15 = var12.getUniform("Radius");
      GlUniform var16 = var12.getUniform("Smoothness");
      GlUniform var17 = var12.getUniform("LeftTopColor");
      GlUniform var18 = var12.getUniform("LeftBottomColor");
      GlUniform var19 = var12.getUniform("CenterTopColor");
      GlUniform var20 = var12.getUniform("CenterBottomColor");
      GlUniform var21 = var12.getUniform("RightTopColor");
      GlUniform var22 = var12.getUniform("RightBottomColor");
      if (var14 != null) {
         var14.set(width, height);
      }

      if (var15 != null) {
         var15.set(radius, radius, radius, radius);
      }

      if (var16 != null) {
         var16.set(1.0F);
      }

      if (var17 != null) {
         int var23 = leftTopColor >> 24 & 0xFF;
         if (var23 == 0) {
            var23 = 255;
         }

         var17.set((leftTopColor >> 16 & 0xFF) / 255.0F, (leftTopColor >> 8 & 0xFF) / 255.0F, (leftTopColor & 0xFF) / 255.0F, var23 / 255.0F);
      }

      if (var18 != null) {
         int var24 = leftBottomColor >> 24 & 0xFF;
         if (var24 == 0) {
            var24 = 255;
         }

         var18.set((leftBottomColor >> 16 & 0xFF) / 255.0F, (leftBottomColor >> 8 & 0xFF) / 255.0F, (leftBottomColor & 0xFF) / 255.0F, var24 / 255.0F);
      }

      if (var19 != null) {
         int var25 = centerTopColor >> 24 & 0xFF;
         if (var25 == 0) {
            var25 = 255;
         }

         var19.set((centerTopColor >> 16 & 0xFF) / 255.0F, (centerTopColor >> 8 & 0xFF) / 255.0F, (centerTopColor & 0xFF) / 255.0F, var25 / 255.0F);
      }

      if (var20 != null) {
         int var26 = centerBottomColor >> 24 & 0xFF;
         if (var26 == 0) {
            var26 = 255;
         }

         var20.set((centerBottomColor >> 16 & 0xFF) / 255.0F, (centerBottomColor >> 8 & 0xFF) / 255.0F, (centerBottomColor & 0xFF) / 255.0F, var26 / 255.0F);
      }

      if (var21 != null) {
         int var27 = rightTopColor >> 24 & 0xFF;
         if (var27 == 0) {
            var27 = 255;
         }

         var21.set((rightTopColor >> 16 & 0xFF) / 255.0F, (rightTopColor >> 8 & 0xFF) / 255.0F, (rightTopColor & 0xFF) / 255.0F, var27 / 255.0F);
      }

      if (var22 != null) {
         int var28 = rightBottomColor >> 24 & 0xFF;
         if (var28 == 0) {
            var28 = 255;
         }

         var22.set((rightBottomColor >> 16 & 0xFF) / 255.0F, (rightBottomColor >> 8 & 0xFF) / 255.0F, (rightBottomColor & 0xFF) / 255.0F, var28 / 255.0F);
      }

      BufferBuilder var29 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      var29.vertex(var13, x, y, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var29.vertex(var13, x, y + height, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var29.vertex(var13, x + width, y + height, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var29.vertex(var13, x + width, y, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShader(ShaderUtils.gradient6Rect);
      BufferRenderer.drawWithGlobalProgram(var29.end());
      RenderSystem.disableBlend();
   }

   public static void drawShadow(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float softness,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      topLeftColor = calcColor(topLeftColor);
      topRightColor = calcColor(topRightColor);
      bottomLeftColor = calcColor(bottomLeftColor);
      bottomRightColor = calcColor(bottomRightColor);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram var11 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shadowRect);
      Matrix4f var12 = matrices.peek().getPositionMatrix();
      float var13 = width + softness * 2.0F;
      float var14 = height + softness * 2.0F;
      float var15 = x - softness;
      float var16 = y - softness;
      GlUniform var17 = var11.getUniform("Size");
      GlUniform var18 = var11.getUniform("Softness");
      GlUniform var19 = var11.getUniform("Radius");
      GlUniform var20 = var11.getUniform("TopLeftColor");
      GlUniform var21 = var11.getUniform("TopRightColor");
      GlUniform var22 = var11.getUniform("BottomLeftColor");
      GlUniform var23 = var11.getUniform("BottomRightColor");
      if (var17 != null) {
         var17.set(var13, var14);
      }

      if (var18 != null) {
         var18.set(softness);
      }

      if (var19 != null) {
         var19.set(radius);
      }

      if (var20 != null) {
         int var24 = topLeftColor >> 24 & 0xFF;
         if (var24 == 0) {
            var24 = 255;
         }

         var20.set((topLeftColor >> 16 & 0xFF) / 255.0F, (topLeftColor >> 8 & 0xFF) / 255.0F, (topLeftColor & 0xFF) / 255.0F, var24 / 255.0F);
      }

      if (var21 != null) {
         int var29 = topRightColor >> 24 & 0xFF;
         if (var29 == 0) {
            var29 = 255;
         }

         var21.set((topRightColor >> 16 & 0xFF) / 255.0F, (topRightColor >> 8 & 0xFF) / 255.0F, (topRightColor & 0xFF) / 255.0F, var29 / 255.0F);
      }

      if (var22 != null) {
         int var30 = bottomLeftColor >> 24 & 0xFF;
         if (var30 == 0) {
            var30 = 255;
         }

         var22.set((bottomLeftColor >> 16 & 0xFF) / 255.0F, (bottomLeftColor >> 8 & 0xFF) / 255.0F, (bottomLeftColor & 0xFF) / 255.0F, var30 / 255.0F);
      }

      if (var23 != null) {
         int var31 = bottomRightColor >> 24 & 0xFF;
         if (var31 == 0) {
            var31 = 255;
         }

         var23.set((bottomRightColor >> 16 & 0xFF) / 255.0F, (bottomRightColor >> 8 & 0xFF) / 255.0F, (bottomRightColor & 0xFF) / 255.0F, var31 / 255.0F);
      }

      BufferBuilder var32 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      var32.vertex(var12, var15, var16, 0.0F).texture(0.0F, 0.0F);
      var32.vertex(var12, var15, var16 + var14, 0.0F).texture(0.0F, 1.0F);
      var32.vertex(var12, var15 + var13, var16 + var14, 0.0F).texture(1.0F, 1.0F);
      var32.vertex(var12, var15 + var13, var16, 0.0F).texture(1.0F, 0.0F);
      RenderSystem.setShader(ShaderUtils.shadowRect);
      BufferRenderer.drawWithGlobalProgram(var32.end());
      RenderSystem.disableBlend();
   }

   public static void drawShadow(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      drawShadow(matrices, x, y, width, height, radius, 10.0F, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }

   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int color) {
      drawShadow(matrices, x, y, width, height, radius, softness, color, color, color, color);
   }

   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
      drawShadow(matrices, x, y, width, height, radius, 10.0F, color, color, color, color);
   }

   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, int color) {
      drawShadow(matrices, x, y, width, height, 0.0F, 10.0F, color, color, color, color);
   }

   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int topColor, int bottomColor) {
      drawShadow(matrices, x, y, width, height, radius, softness, topColor, topColor, bottomColor, bottomColor);
   }

   public static void drawShadowHorizontal(
      MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int leftColor, int rightColor
   ) {
      drawShadow(matrices, x, y, width, height, radius, softness, leftColor, rightColor, leftColor, rightColor);
   }

   public static void drawShadow(
      MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, float offsetX, float offsetY, int color
   ) {
      drawShadow(matrices, x + offsetX, y + offsetY, width, height, radius, softness, color, color, color, color);
   }

   public static void drawShadow(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float softness,
      float offsetX,
      float offsetY,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      drawShadow(matrices, x + offsetX, y + offsetY, width, height, radius, softness, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }

   public static void drawShadow6(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float softness,
      int leftTopColor,
      int leftBottomColor,
      int centerTopColor,
      int centerBottomColor,
      int rightTopColor,
      int rightBottomColor
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram var13 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shadow6Rect);
      Matrix4f var14 = matrices.peek().getPositionMatrix();
      float var15 = width + softness * 2.0F;
      float var16 = height + softness * 2.0F;
      float var17 = x - softness;
      float var18 = y - softness;
      GlUniform var19 = var13.getUniform("Size");
      GlUniform var20 = var13.getUniform("Softness");
      GlUniform var21 = var13.getUniform("Radius");
      GlUniform var22 = var13.getUniform("LeftTopColor");
      GlUniform var23 = var13.getUniform("LeftBottomColor");
      GlUniform var24 = var13.getUniform("CenterTopColor");
      GlUniform var25 = var13.getUniform("CenterBottomColor");
      GlUniform var26 = var13.getUniform("RightTopColor");
      GlUniform var27 = var13.getUniform("RightBottomColor");
      if (var19 != null) {
         var19.set(var15, var16);
      }

      if (var20 != null) {
         var20.set(softness);
      }

      if (var21 != null) {
         var21.set(radius);
      }

      if (var22 != null) {
         int var28 = leftTopColor >> 24 & 0xFF;
         if (var28 == 0) {
            var28 = 255;
         }

         var22.set((leftTopColor >> 16 & 0xFF) / 255.0F, (leftTopColor >> 8 & 0xFF) / 255.0F, (leftTopColor & 0xFF) / 255.0F, var28 / 255.0F);
      }

      if (var23 != null) {
         int var29 = leftBottomColor >> 24 & 0xFF;
         if (var29 == 0) {
            var29 = 255;
         }

         var23.set((leftBottomColor >> 16 & 0xFF) / 255.0F, (leftBottomColor >> 8 & 0xFF) / 255.0F, (leftBottomColor & 0xFF) / 255.0F, var29 / 255.0F);
      }

      if (var24 != null) {
         int var30 = centerTopColor >> 24 & 0xFF;
         if (var30 == 0) {
            var30 = 255;
         }

         var24.set((centerTopColor >> 16 & 0xFF) / 255.0F, (centerTopColor >> 8 & 0xFF) / 255.0F, (centerTopColor & 0xFF) / 255.0F, var30 / 255.0F);
      }

      if (var25 != null) {
         int var31 = centerBottomColor >> 24 & 0xFF;
         if (var31 == 0) {
            var31 = 255;
         }

         var25.set((centerBottomColor >> 16 & 0xFF) / 255.0F, (centerBottomColor >> 8 & 0xFF) / 255.0F, (centerBottomColor & 0xFF) / 255.0F, var31 / 255.0F);
      }

      if (var26 != null) {
         int var32 = rightTopColor >> 24 & 0xFF;
         if (var32 == 0) {
            var32 = 255;
         }

         var26.set((rightTopColor >> 16 & 0xFF) / 255.0F, (rightTopColor >> 8 & 0xFF) / 255.0F, (rightTopColor & 0xFF) / 255.0F, var32 / 255.0F);
      }

      if (var27 != null) {
         int var33 = rightBottomColor >> 24 & 0xFF;
         if (var33 == 0) {
            var33 = 255;
         }

         var27.set((rightBottomColor >> 16 & 0xFF) / 255.0F, (rightBottomColor >> 8 & 0xFF) / 255.0F, (rightBottomColor & 0xFF) / 255.0F, var33 / 255.0F);
      }

      BufferBuilder var34 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      var34.vertex(var14, var17, var18, 0.0F).texture(0.0F, 0.0F);
      var34.vertex(var14, var17, var18 + var16, 0.0F).texture(0.0F, 1.0F);
      var34.vertex(var14, var17 + var15, var18 + var16, 0.0F).texture(1.0F, 1.0F);
      var34.vertex(var14, var17 + var15, var18, 0.0F).texture(1.0F, 0.0F);
      RenderSystem.setShader(ShaderUtils.shadow6Rect);
      BufferRenderer.drawWithGlobalProgram(var34.end());
      RenderSystem.disableBlend();
   }

   public static void drawTexture(
      MatrixStack matrices, Identifier texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2, int color
   ) {
      color = calcColor(color);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderTexture(0, texture);
      Matrix4f var11 = matrices.peek().getPositionMatrix();
      int var12 = color >> 24 & 0xFF;
      if (var12 == 0) {
         var12 = 255;
      }

      float var13 = (color >> 16 & 0xFF) / 255.0F;
      float var14 = (color >> 8 & 0xFF) / 255.0F;
      float var15 = (color & 0xFF) / 255.0F;
      float var16 = var12 / 255.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder var17 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var17.vertex(var11, x, y, 0.0F).texture(u1, v1).color(var13, var14, var15, var16);
      var17.vertex(var11, x, y + height, 0.0F).texture(u1, v2).color(var13, var14, var15, var16);
      var17.vertex(var11, x + width, y + height, 0.0F).texture(u2, v2).color(var13, var14, var15, var16);
      var17.vertex(var11, x + width, y, 0.0F).texture(u2, v1).color(var13, var14, var15, var16);
      BufferRenderer.drawWithGlobalProgram(var17.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.disableBlend();
   }

   public static void drawImage(MatrixStack matrices, Identifier texture, float x, float y, float width, float height, int color) {
      drawTexture(matrices, texture, x, y, width, height, 0.0F, 0.0F, 1.0F, 1.0F, color);
   }

   public static void drawImage(MatrixStack matrices, String namespace, String path, float x, float y, float width, float height, int color) {
      drawImage(matrices, Identifier.of(namespace, path), x, y, width, height, color);
   }

   public static void drawSprite(MatrixStack matrices, Sprite sprite, float x, float y, float size, int color) {
      drawTexture(matrices, sprite.getAtlasId(), x, y, size, size, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), color);
   }

   public static void drawPlayerHead(MatrixStack matrices, PlayerEntity player, float x, float y, float size, float radius, float hurtPercent) {
      if (player != null) {
         Identifier var7 = getPlayer(player);
         handleAction(matrices, var7, x, y, size, radius, 1.0F, hurtPercent);
      }
   }

   public static void drawPlayerHead(MatrixStack matrices, String username, float x, float y, float size, float radius) {
      drawPlayerHead(matrices, username, x, y, size, radius, 1.0F, 0.0F);
   }

   public static void drawPlayerHead(MatrixStack matrices, String username, float x, float y, float size, float radius, float alpha, float hurtPercent) {
      if (username != null && !username.isEmpty()) {
         Identifier var8 = getUsername(username);
         handleAction(matrices, var8, x, y, size, radius, alpha, hurtPercent);
      }
   }

   public static void drawPlayerHead(MatrixStack matrices, UUID uuid, float x, float y, float size, float radius) {
      drawPlayerHead(matrices, uuid, x, y, size, radius, 1.0F, 0.0F);
   }

   public static void drawPlayerHead(MatrixStack matrices, UUID uuid, float x, float y, float size, float radius, float alpha, float hurtPercent) {
      if (uuid != null) {
         Identifier var8 = getUuid(uuid);
         handleAction(matrices, var8, x, y, size, radius, alpha, hurtPercent);
      }
   }

   public static void drawPlayerHead(MatrixStack matrices, PlayerListEntry entry, float x, float y, float size, float radius) {
      drawPlayerHead(matrices, entry, x, y, size, radius, 1.0F, 0.0F);
   }

   public static void drawPlayerHead(MatrixStack matrices, PlayerListEntry entry, float x, float y, float size, float radius, float alpha, float hurtPercent) {
      if (entry != null) {
         Identifier var8 = entry.getSkinTextures().texture();
         if (var8 == null) {
            var8 = DefaultSkinHelper.getSkinTextures(entry.getProfile().getId()).texture();
         }

         handleAction(matrices, var8, x, y, size, radius, alpha, hurtPercent);
      }
   }

   public static void drawPlayerHead(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius) {
      handleAction(matrices, skinTexture, x, y, size, radius, 1.0F, 0.0F);
   }

   private static void handleAction(
      MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius, float alpha, float hurtPercent
   ) {
      if (skinTexture == null) {
         skinTexture = DefaultSkinHelper.getSkinTextures(id).texture();
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderTexture(0, skinTexture);
      ShaderProgram var8 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.face);
      Matrix4f var9 = matrices.peek().getPositionMatrix();
      GlUniform var10 = var8.getUniform("location");
      GlUniform var11 = var8.getUniform("size");
      GlUniform var12 = var8.getUniform("radius");
      GlUniform var13 = var8.getUniform("alpha");
      GlUniform var14 = var8.getUniform("u");
      GlUniform var15 = var8.getUniform("v");
      GlUniform var16 = var8.getUniform("w");
      GlUniform var17 = var8.getUniform("h");
      GlUniform var18 = var8.getUniform("hurtPercent");
      if (var10 != null) {
         var10.set(x, y);
      }

      if (var11 != null) {
         var11.set(size, size);
      }

      if (var12 != null) {
         var12.set(radius);
      }

      if (var13 != null) {
         var13.set(flag ? alpha * fvalue : alpha);
      }

      if (var14 != null) {
         var14.set(0.125F);
      }

      if (var15 != null) {
         var15.set(0.125F);
      }

      if (var16 != null) {
         var16.set(0.125F);
      }

      if (var17 != null) {
         var17.set(0.125F);
      }

      if (var18 != null) {
         var18.set(hurtPercent);
      }

      BufferBuilder var19 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      var19.vertex(var9, x, y, 0.0F).texture(0.0F, 0.0F);
      var19.vertex(var9, x, y + size, 0.0F).texture(0.0F, 1.0F);
      var19.vertex(var9, x + size, y + size, 0.0F).texture(1.0F, 1.0F);
      var19.vertex(var9, x + size, y, 0.0F).texture(1.0F, 0.0F);
      RenderSystem.setShader(ShaderUtils.face);
      BufferRenderer.drawWithGlobalProgram(var19.end());
      handleAction2(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.disableBlend();
   }

   private static void handleAction2(
      MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius, float alpha, float hurtPercent
   ) {
      RenderSystem.setShaderTexture(0, skinTexture);
      ShaderProgram var8 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.face);
      Matrix4f var9 = matrices.peek().getPositionMatrix();
      GlUniform var10 = var8.getUniform("location");
      GlUniform var11 = var8.getUniform("size");
      GlUniform var12 = var8.getUniform("radius");
      GlUniform var13 = var8.getUniform("alpha");
      GlUniform var14 = var8.getUniform("u");
      GlUniform var15 = var8.getUniform("v");
      GlUniform var16 = var8.getUniform("w");
      GlUniform var17 = var8.getUniform("h");
      GlUniform var18 = var8.getUniform("hurtPercent");
      if (var10 != null) {
         var10.set(x, y);
      }

      if (var11 != null) {
         var11.set(size, size);
      }

      if (var12 != null) {
         var12.set(radius);
      }

      if (var13 != null) {
         var13.set(flag ? alpha * fvalue : alpha);
      }

      if (var14 != null) {
         var14.set(0.625F);
      }

      if (var15 != null) {
         var15.set(0.125F);
      }

      if (var16 != null) {
         var16.set(0.125F);
      }

      if (var17 != null) {
         var17.set(0.125F);
      }

      if (var18 != null) {
         var18.set(hurtPercent);
      }

      BufferBuilder var19 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      var19.vertex(var9, x, y, 0.0F).texture(0.0F, 0.0F);
      var19.vertex(var9, x, y + size, 0.0F).texture(0.0F, 1.0F);
      var19.vertex(var9, x + size, y + size, 0.0F).texture(1.0F, 1.0F);
      var19.vertex(var9, x + size, y, 0.0F).texture(1.0F, 0.0F);
      RenderSystem.setShader(ShaderUtils.face);
      BufferRenderer.drawWithGlobalProgram(var19.end());
   }

   private static Identifier getPlayer(PlayerEntity player) {
      if (mc.getNetworkHandler() == null) {
         return DefaultSkinHelper.getSkinTextures(player.getUuid()).texture();
      }

      PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
      return var1 != null ? var1.getSkinTextures().texture() : DefaultSkinHelper.getSkinTextures(player.getUuid()).texture();
   }

   private static Identifier getUsername(String username) {
      String var1 = username.toLowerCase(Locale.ROOT);
      Identifier var2 = stringMap.get(var1);
      if (var2 != null) {
         return var2;
      }

      if (mc.getNetworkHandler() != null) {
         for (PlayerListEntry var4 : mc.getNetworkHandler().getPlayerList()) {
            if (var4.getProfile().getName().equalsIgnoreCase(username)) {
               Identifier var5 = var4.getSkinTextures().texture();
               stringMap.put(var1, var5);
               return var5;
            }
         }
      }

      if (mc.world != null) {
         for (PlayerEntity var8 : mc.world.getPlayers()) {
            if (var8.getName().getString().equalsIgnoreCase(username)) {
               Identifier var9 = getPlayer(var8);
               stringMap.put(var1, var9);
               return var9;
            }
         }
      }

      Identifier var7 = DefaultSkinHelper.getSkinTextures(UUID.nameUUIDFromBytes(username.getBytes())).texture();
      stringMap.put(var1, var7);
      return var7;
   }

   private static Identifier getUuid(UUID uuid) {
      String var1 = uuid.toString();
      if (stringMap.containsKey(var1)) {
         return stringMap.get(var1);
      }

      if (mc.getNetworkHandler() != null) {
         PlayerListEntry var2 = mc.getNetworkHandler().getPlayerListEntry(uuid);
         if (var2 != null) {
            Identifier var5 = var2.getSkinTextures().texture();
            stringMap.put(var1, var5);
            return var5;
         }
      }

      if (mc.world != null) {
         PlayerEntity var4 = mc.world.getPlayerByUuid(uuid);
         if (var4 != null) {
            Identifier var3 = getPlayer(var4);
            stringMap.put(var1, var3);
            return var3;
         }
      }

      return DefaultSkinHelper.getSkinTextures(uuid).texture();
   }

   public static void clearSkinCache() {
      stringMap.clear();
   }

   public static void removeSkinFromCache(String username) {
      stringMap.remove(username.toLowerCase(Locale.ROOT));
   }

   public static void drawDefaultHudElementRects(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
      drawDefaultHudElementRects(matrices, x, y, width, height, themeColor, true);
   }

   public static void drawDefaultHudElementRects(MatrixStack matrices, float x, float y, float width, float height, int themeColor, boolean drawPattern) {
      drawDefaultHudThemedPanel(matrices, x, y, width, height, 3.0F, 3.5F, themeColor);
      if (drawPattern) {
         drawHudSquarePattern(matrices, x, y, width, height, themeColor);
      }

      drawRoundedRect(matrices, x + width - 14.5F, y + 3.0F, 10.0F, 10.0F, 2.0F, ColorUtils.darken(themeColor, 0.4F));
   }

   public static void drawHudSquarePattern(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
      if (!(width <= 6.0F) && !(height <= 6.0F)) {
         float var6 = x - 1.0F;
         float var7 = y + 1.0F;
         float var8 = Math.max(1.0F, width - 2.0F);
         float var9 = Math.max(1.0F, height - 2.0F);
         float var10 = (themeColor >>> 24 & 0xFF) / 255.0F;
         if (!(var10 <= 0.001F)) {
            if (var9 <= 20.0F) {
               float[][] var33 = new float[][]{
                  {0.05F, 0.08F, 8.6F},
                  {0.92F, 0.1F, 8.8F},
                  {0.16F, 0.78F, 6.3F},
                  {0.77F, 0.8F, 6.5F},
                  {0.31F, 0.18F, 6.0F},
                  {0.58F, 0.74F, 5.8F},
                  {0.45F, 0.45F, 5.1F},
                  {0.86F, 0.46F, 5.3F},
                  {0.23F, 0.52F, 4.9F},
                  {0.67F, 0.3F, 5.0F},
                  {0.11F, 0.34F, 5.5F},
                  {0.38F, 0.7F, 5.2F},
                  {0.72F, 0.16F, 5.7F},
                  {0.95F, 0.68F, 5.1F}
               };
               float var34 = Math.min(var33.length, 3.7F + Math.max(0.0F, (var8 - 84.0F) / 32.0F));
               int var35 = ColorUtils.setAlphaColor(ColorUtils.darken(themeColor, 0.62F), Math.max(0, Math.min(255, (int)(82.0F * var10))));
               ScissorUtils.push();
               ScissorUtils.setFromComponentCoordinates(var6, var7, var8, var9);

               try {
                  for (int var36 = 0; var36 < var33.length; var36++) {
                     float var38 = var34 - var36;
                     if (!(var38 <= 0.0F)) {
                        float var39 = Math.max(0.0F, Math.min(1.0F, var38));
                        var39 = var39 * var39 * (3.0F - 2.0F * var39);
                        if (!(var39 <= 0.02F)) {
                           float var41 = var33[var36][2];
                           float var42 = var6 + 0.8F + var33[var36][0] * Math.max(1.0F, var8 - var41 + 1.6F);
                           float var45 = var7 - 1.2F + var33[var36][1] * Math.max(1.0F, var9 - var41 + 2.4F);
                           int var46 = Math.max(0, Math.min(255, (int)(86.0F * var39 * var10)));
                           if (var46 > 0) {
                              int var47 = ColorUtils.setAlphaColor(var35, var46);
                              drawRoundedRectOutline(matrices, var42, var45, var41, var41, 0.0F, 0.5F, var47, var47, var47, var47);
                           }
                        }
                     }
                  }
               } finally {
                  ScissorUtils.unset();
                  ScissorUtils.pop();
               }
            } else {
               float[][] var11 = new float[][]{
                  {0.05F, 4.0F, 9.6F},
                  {0.87F, 4.0F, 9.2F},
                  {0.5F, 8.0F, 7.4F},
                  {0.18F, 13.0F, 6.2F},
                  {0.72F, 13.0F, 6.0F},
                  {0.07F, 21.0F, 5.6F},
                  {0.91F, 21.0F, 5.8F},
                  {0.24F, 30.0F, 5.4F},
                  {0.66F, 30.0F, 5.5F},
                  {0.04F, 38.0F, 6.8F},
                  {0.9F, 38.0F, 7.0F},
                  {0.15F, 47.0F, 5.4F},
                  {0.78F, 47.0F, 5.5F},
                  {0.08F, 56.0F, 5.1F},
                  {0.92F, 56.0F, 5.2F},
                  {0.23F, 65.0F, 5.8F},
                  {0.69F, 65.0F, 5.9F},
                  {0.52F, 71.0F, 7.2F},
                  {0.06F, 74.0F, 7.6F},
                  {0.88F, 74.0F, 7.4F},
                  {0.14F, 85.0F, 5.7F},
                  {0.82F, 85.0F, 5.8F},
                  {0.09F, 97.0F, 6.5F},
                  {0.9F, 98.0F, 6.6F}
               };
               byte var12 = 10;
               float var13 = Math.max(0.0F, var9 - 24.0F);
               float var14 = Math.min(var11.length, var12 + var13 / 10.0F);
               float var15 = Math.max(0.0F, Math.min(1.0F, (var9 - 10.0F) / 16.0F));
               var15 = var15 * var15 * (3.0F - 2.0F * var15);
               int var16 = ColorUtils.setAlphaColor(ColorUtils.darken(themeColor, 0.72F), Math.max(0, Math.min(255, (int)(40.0F * var10))));
               ScissorUtils.push();
               ScissorUtils.setFromComponentCoordinates(var6, var7, var8, var9);

               try {
                  for (int var17 = 0; var17 < var11.length; var17++) {
                     float var18 = var14 - var17;
                     if (!(var18 <= 0.0F)) {
                        float var19 = Math.max(0.0F, Math.min(1.0F, var18));
                        var19 = var19 * var19 * (3.0F - 2.0F * var19);
                        var19 *= var15;
                        if (!(var19 <= 0.015F)) {
                           float var20 = var11[var17][2];
                           float var21 = var6 + 2.0F + var11[var17][0] * Math.max(1.0F, var8 - var20 - 4.0F);
                           float var22 = var7 + var11[var17][1];
                           float var23 = var7 + var9 - 1.0F;
                           if (!(var22 >= var23)) {
                              if (var22 + var20 > var23) {
                                 float var24 = Math.max(0.0F, Math.min(1.0F, (var23 - var22) / Math.max(1.0F, var20)));
                                 var24 = var24 * var24 * (3.0F - 2.0F * var24);
                                 var19 *= var24;
                                 if (var19 <= 0.015F) {
                                    continue;
                                 }
                              }

                              int var49 = Math.max(0, Math.min(255, (int)(58.0F * var19 * var10)));
                              if (var49 > 0) {
                                 int var25 = ColorUtils.setAlphaColor(var16, var49);
                                 drawRoundedRectOutline(matrices, var21, var22, var20, var20, 0.0F, 0.55F, var25, var25, var25, var25);
                              }
                           }
                        }
                     }
                  }
               } finally {
                  ScissorUtils.unset();
                  ScissorUtils.pop();
               }
            }
         }
      }
   }

   public static void drawDefaultHudInfoBox(MatrixStack matrices, float x, float y, float width, int outerColor, int innerColor) {
      drawRoundedRect(matrices, x - 0.25F, y - 1.25F, width + 0.5F, 9.0F, 1.3F, outerColor);
      drawRoundedRect(matrices, x, y - 1.0F, width, 8.5F, 1.0F, innerColor);
   }

   public static void drawDefaultHudPanel(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float gradientRadius,
      float borderRadius,
      int borderColor,
      int topColor,
      int bottomColor
   ) {
      drawRoundedRect(matrices, x - 0.5F, y - 0.5F, width + 1.0F, height + 1.0F, borderRadius, borderColor);
      drawGradientRect(matrices, x, y, width, height, gradientRadius, topColor, bottomColor);
   }

   public static void drawDefaultHudThemedPanel(
      MatrixStack matrices, float x, float y, float width, float height, float gradientRadius, float borderRadius, int themeColor
   ) {
      drawDefaultHudPanel(
         matrices,
         x,
         y,
         width,
         height,
         gradientRadius,
         borderRadius,
         ColorUtils.rgba(50, 50, 50, 255),
         ColorUtils.darken(themeColor, 0.15F),
         ColorUtils.darken(themeColor, 0.05F)
      );
   }

   public static void drawWaveHudHeader(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float shadowRadius,
      float shadowSoftness,
      int leftTop,
      int leftBottom,
      int centerTop,
      int centerBottom,
      int rightTop,
      int rightBottom
   ) {
      drawShadow6(matrices, x, y, width, height, shadowRadius, shadowSoftness, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
      drawGradient6Rect(matrices, x, y, width, height, radius, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
   }

   public static void drawWaveHudPanel(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      int bgColor,
      float headerHeight,
      float headerRadius,
      float shadowRadius,
      float shadowSoftness,
      int leftTop,
      int leftBottom,
      int centerTop,
      int centerBottom,
      int rightTop,
      int rightBottom
   ) {
      drawRoundedRect(matrices, x, y, width, height, 0.0F, bgColor);
      drawWaveHudHeader(
         matrices, x, y, width, headerHeight, headerRadius, shadowRadius, shadowSoftness, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom
      );
   }

   public static void drawTargetHudWaveFrame(MatrixStack matrices, float x, float y, float width, float height, float padding, float entityBoxSize, float alpha) {
      drawRoundedRect(matrices, x, y, width, height, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 40, 255), alpha));
      drawRoundedRect(
         matrices,
         x + padding,
         y + padding,
         width - padding * 2.0F,
         height - padding * 2.0F,
         0.0F,
         ColorUtils.applyAlpha(ColorUtils.rgba(20, 20, 20, 255), alpha)
      );
      drawRoundedRect(
         matrices, x + padding + 2.0F, y + padding + 2.0F, entityBoxSize, entityBoxSize, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 40, 255), alpha)
      );
      drawRoundedRect(
         matrices,
         x + padding + 3.0F,
         y + padding + 3.0F,
         entityBoxSize - 2.0F,
         entityBoxSize - 2.0F,
         0.0F,
         ColorUtils.applyAlpha(ColorUtils.rgba(25, 25, 25, 255), alpha)
      );
   }

   public static void drawTargetHudDefaultPlaceholder(MatrixStack matrices, float x, float y, float alpha) {
      drawRoundedRect(matrices, x - 1.0F, y - 1.0F, 22.0F, 22.0F, 1.0F, ColorUtils.applyAlpha(ColorUtils.rgba(21, 21, 21, 255), alpha));
   }

   public static void drawTargetHudHealthBars(
      MatrixStack matrices, float x, float y, float width, float trailProgress, float progress, int themeColor, int themecolor2, float alpha
   ) {
      drawRoundedRect(matrices, x, y, width, 5.5F, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.5F), alpha * 0.8F));
      drawRoundedRect(matrices, x, y, width * trailProgress, 5.5F, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.8F), alpha * 0.8F));
      drawGradientRect(matrices, x, y, width * progress, 5.5F, 1.25F, ColorUtils.applyAlpha(themeColor, alpha), ColorUtils.applyAlpha(themecolor2, alpha));
   }

   public static void drawTargetHudGoldenBars(
      MatrixStack matrices, float x, float y, float width, float height, float trailProgress, float progress, float alpha, float goldenAlpha
   ) {
      int var9 = ColorUtils.rgba(255, 215, 0, 255);
      drawRoundedRect(matrices, x, y, width * trailProgress, height, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(var9, 0.65F), alpha * goldenAlpha * 0.8F));
      drawGradientRect(
         matrices,
         x,
         y,
         width * progress,
         height,
         1.25F,
         ColorUtils.applyAlpha(ColorUtils.darken(var9, 0.55F), alpha * goldenAlpha),
         ColorUtils.applyAlpha(var9, alpha * goldenAlpha)
      );
   }

   public static void drawTargetHudHeartBase(MatrixStack matrices, float x, float y, float alpha) {
      drawRoundedRect(matrices, x, y, 6.2F, 4.5F, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 255), alpha));
   }

   public static void drawTargetHudHeartFill(MatrixStack matrices, float x, float y, float width, int heartColor, int shadowColor) {
      drawShadow(matrices, x + 1.0F, y + 1.0F, width, 2.0F, 0.0F, 8.0F, shadowColor);
      drawRoundedRect(matrices, x, y, width + 1.2F, 4.5F, 0.0F, heartColor);
   }

   public static void drawKeyStrokeRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
      drawRoundedRect(matrices, x, y, width, height, radius, color);
   }

   public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
      drawRoundedRect(matrices, x, y, width, height, radius, radius, radius, radius, color);
   }

   public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, Vector4f radius, int color) {
      drawRoundedRect(matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, color);
   }

   public static void drawRoundedRect(
      MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color
   ) {
      color = calcColor(color);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram var10 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedRect);
      Matrix4f var11 = matrices.peek().getPositionMatrix();
      GlUniform var12 = var10.getUniform("Size");
      GlUniform var13 = var10.getUniform("Radius");
      if (var12 != null) {
         var12.set(width, height);
      }

      if (var13 != null) {
         var13.set(topLeft, topRight, bottomRight, bottomLeft);
      }

      BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int var15 = color >> 24 & 0xFF;
      if (var15 == 0) {
         var15 = 255;
      }

      float var16 = (color >> 16 & 0xFF) / 255.0F;
      float var17 = (color >> 8 & 0xFF) / 255.0F;
      float var18 = (color & 0xFF) / 255.0F;
      float var19 = var15 / 255.0F;
      var14.vertex(var11, x, y, 0.0F).color(var16, var17, var18, var19);
      var14.vertex(var11, x, y + height, 0.0F).color(var16, var17, var18, var19);
      var14.vertex(var11, x + width, y + height, 0.0F).color(var16, var17, var18, var19);
      var14.vertex(var11, x + width, y, 0.0F).color(var16, var17, var18, var19);
      RenderSystem.setShader(ShaderUtils.roundedRect);
      BufferRenderer.drawWithGlobalProgram(var14.end());
      RenderSystem.disableBlend();
   }

   public static void drawRoundCircle(MatrixStack matrices, float x, float y, float radius, int color) {
      Matrix4f var5 = matrices.peek().getPositionMatrix();
      drawRoundedRect(matrices, x - radius / 2.0F, y - radius / 2.0F, radius, radius, radius / 2.0F - 0.5F, color);
   }

   public static void drawRingArc(MatrixStack matrices, float x, float y, float size, float thickness, float startDeg, float endDeg, int color) {
      if (!(size <= 0.0F) && !(thickness <= 0.0F)) {
         float var8 = size / 2.0F;
         float var9 = (float)Math.toRadians(startDeg);
         float var10 = (float)Math.toRadians(endDeg);
         float var11 = (float) (Math.PI * 2);
         if (var9 < 0.0F) {
            var9 += var11;
         }

         if (var10 < 0.0F) {
            var10 += var11;
         }

         while (var10 < var9) {
            var10 += var11;
         }

         if (var10 - var9 <= 1.0E-4F) {
            var10 = var9 + var11;
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ShaderProgram var12 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.ringArc);
         GlUniform var13 = var12.getUniform("Size");
         GlUniform var14 = var12.getUniform("Radius");
         GlUniform var15 = var12.getUniform("Thickness");
         GlUniform var16 = var12.getUniform("StartAngle");
         GlUniform var17 = var12.getUniform("EndAngle");
         GlUniform var18 = var12.getUniform("Smoothness");
         GlUniform var19 = var12.getUniform("ColorModulator");
         if (var13 != null) {
            var13.set(size, size);
         }

         if (var14 != null) {
            var14.set(var8);
         }

         if (var15 != null) {
            var15.set(thickness);
         }

         if (var16 != null) {
            var16.set(var9);
         }

         if (var17 != null) {
            var17.set(var10);
         }

         if (var18 != null) {
            var18.set(Math.min(1.0F, thickness * 0.5F));
         }

         if (var19 != null) {
            var19.set(1.0F, 1.0F, 1.0F, 1.0F);
         }

         Matrix4f var20 = matrices.peek().getPositionMatrix();
         BufferBuilder var21 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         int var22 = color >> 24 & 0xFF;
         if (var22 == 0) {
            var22 = 255;
         }

         float var23 = (color >> 16 & 0xFF) / 255.0F;
         float var24 = (color >> 8 & 0xFF) / 255.0F;
         float var25 = (color & 0xFF) / 255.0F;
         float var26 = var22 / 255.0F;
         var21.vertex(var20, x, y, 0.0F).color(var23, var24, var25, var26);
         var21.vertex(var20, x, y + size, 0.0F).color(var23, var24, var25, var26);
         var21.vertex(var20, x + size, y + size, 0.0F).color(var23, var24, var25, var26);
         var21.vertex(var20, x + size, y, 0.0F).color(var23, var24, var25, var26);
         RenderSystem.setShader(ShaderUtils.ringArc);
         BufferRenderer.drawWithGlobalProgram(var21.end());
         RenderSystem.disableBlend();
      }
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      Vector4f radius,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      drawGradientRect(matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      topLeftColor = calcColor(topLeftColor);
      topRightColor = calcColor(topRightColor);
      bottomLeftColor = calcColor(bottomLeftColor);
      bottomRightColor = calcColor(bottomRightColor);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram var13 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.gradientRect);
      Matrix4f var14 = matrices.peek().getPositionMatrix();
      GlUniform var15 = var13.getUniform("Size");
      GlUniform var16 = var13.getUniform("Radius");
      GlUniform var17 = var13.getUniform("Smoothness");
      GlUniform var18 = var13.getUniform("ColorModulator");
      GlUniform var19 = var13.getUniform("TopLeftColor");
      GlUniform var20 = var13.getUniform("BottomLeftColor");
      GlUniform var21 = var13.getUniform("TopRightColor");
      GlUniform var22 = var13.getUniform("BottomRightColor");
      if (var15 != null) {
         var15.set(width, height);
      }

      if (var16 != null) {
         var16.set(topLeft, topRight, bottomRight, bottomLeft);
      }

      if (var17 != null) {
         var17.set(1.0F);
      }

      if (var18 != null) {
         var18.set(1.0F, 1.0F, 1.0F, 1.0F);
      }

      int var23 = topLeftColor >> 24 & 0xFF;
      if (var23 == 0) {
         var23 = 255;
      }

      if (var19 != null) {
         var19.set((topLeftColor >> 16 & 0xFF) / 255.0F, (topLeftColor >> 8 & 0xFF) / 255.0F, (topLeftColor & 0xFF) / 255.0F, var23 / 255.0F);
      }

      int var24 = bottomLeftColor >> 24 & 0xFF;
      if (var24 == 0) {
         var24 = 255;
      }

      if (var20 != null) {
         var20.set((bottomLeftColor >> 16 & 0xFF) / 255.0F, (bottomLeftColor >> 8 & 0xFF) / 255.0F, (bottomLeftColor & 0xFF) / 255.0F, var24 / 255.0F);
      }

      int var25 = topRightColor >> 24 & 0xFF;
      if (var25 == 0) {
         var25 = 255;
      }

      if (var21 != null) {
         var21.set((topRightColor >> 16 & 0xFF) / 255.0F, (topRightColor >> 8 & 0xFF) / 255.0F, (topRightColor & 0xFF) / 255.0F, var25 / 255.0F);
      }

      int var26 = bottomRightColor >> 24 & 0xFF;
      if (var26 == 0) {
         var26 = 255;
      }

      if (var22 != null) {
         var22.set((bottomRightColor >> 16 & 0xFF) / 255.0F, (bottomRightColor >> 8 & 0xFF) / 255.0F, (bottomRightColor & 0xFF) / 255.0F, var26 / 255.0F);
      }

      BufferBuilder var27 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var27.vertex(var14, x, y, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var27.vertex(var14, x, y + height, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var27.vertex(var14, x + width, y + height, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var27.vertex(var14, x + width, y, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShader(ShaderUtils.gradientRect);
      BufferRenderer.drawWithGlobalProgram(var27.end());
      RenderSystem.disableBlend();
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }

   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, Vector4f radius, int topColor, int bottomColor) {
      drawGradientRect(matrices, x, y, width, height, radius, topColor, topColor, bottomColor, bottomColor);
   }

   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int topColor, int bottomColor) {
      drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, topColor, topColor, bottomColor, bottomColor);
   }

   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, int topColor, int bottomColor) {
      drawGradientRect(matrices, x, y, width, height, 0.0F, 0.0F, 0.0F, 0.0F, topColor, topColor, bottomColor, bottomColor);
   }

   public static void drawGradientRect(
      MatrixStack matrices, float x, float y, float width, float height, float radius, int leftColor, int rightColor, boolean horizontal
   ) {
      if (horizontal) {
         drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, leftColor, rightColor, leftColor, rightColor);
      } else {
         drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, leftColor, leftColor, rightColor, rightColor);
      }
   }

   public static void drawGradientRect(
      MatrixStack matrices, float x, float y, float width, float height, Vector4f radius, int leftColor, int rightColor, boolean horizontal
   ) {
      if (horizontal) {
         drawGradientRect(matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, leftColor, rightColor, leftColor, rightColor);
      } else {
         drawGradientRect(matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, leftColor, leftColor, rightColor, rightColor);
      }
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      float outline,
      int outlineColor
   ) {
      drawRoundedRectOutline(
         matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, outline, outlineColor, outlineColor, outlineColor, outlineColor
      );
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float outline,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      drawRoundedRectOutline(
         matrices, x, y, width, height, radius, radius, radius, radius, outline, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor
      );
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      Vector4f radius,
      float outline,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      drawRoundedRectOutline(
         matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, outline, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor
      );
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      float outline,
      int topLeftColor,
      int topRightColor,
      int bottomLeftColor,
      int bottomRightColor
   ) {
      if (!(outline <= 0.0F)) {
         topLeftColor = calcColor(topLeftColor);
         topRightColor = calcColor(topRightColor);
         bottomLeftColor = calcColor(bottomLeftColor);
         bottomRightColor = calcColor(bottomRightColor);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ShaderProgram var14 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedRectOutline);
         Matrix4f var15 = matrices.peek().getPositionMatrix();
         GlUniform var16 = var14.getUniform("Size");
         GlUniform var17 = var14.getUniform("Radius");
         GlUniform var18 = var14.getUniform("Smoothness");
         GlUniform var19 = var14.getUniform("ColorModulator");
         GlUniform var20 = var14.getUniform("Outline");
         GlUniform var21 = var14.getUniform("TopLeftColor");
         GlUniform var22 = var14.getUniform("BottomLeftColor");
         GlUniform var23 = var14.getUniform("TopRightColor");
         GlUniform var24 = var14.getUniform("BottomRightColor");
         if (var16 != null) {
            var16.set(width, height);
         }

         if (var17 != null) {
            var17.set(topLeft, topRight, bottomRight, bottomLeft);
         }

         if (var18 != null) {
            var18.set(1.0F);
         }

         if (var19 != null) {
            var19.set(1.0F, 1.0F, 1.0F, 1.0F);
         }

         if (var20 != null) {
            var20.set(outline);
         }

         if (var21 != null) {
            int var25 = topLeftColor >> 24 & 0xFF;
            if (var25 == 0) {
               var25 = 255;
            }

            var21.set((topLeftColor >> 16 & 0xFF) / 255.0F, (topLeftColor >> 8 & 0xFF) / 255.0F, (topLeftColor & 0xFF) / 255.0F, var25 / 255.0F);
         }

         if (var22 != null) {
            int var30 = bottomLeftColor >> 24 & 0xFF;
            if (var30 == 0) {
               var30 = 255;
            }

            var22.set((bottomLeftColor >> 16 & 0xFF) / 255.0F, (bottomLeftColor >> 8 & 0xFF) / 255.0F, (bottomLeftColor & 0xFF) / 255.0F, var30 / 255.0F);
         }

         if (var23 != null) {
            int var31 = topRightColor >> 24 & 0xFF;
            if (var31 == 0) {
               var31 = 255;
            }

            var23.set((topRightColor >> 16 & 0xFF) / 255.0F, (topRightColor >> 8 & 0xFF) / 255.0F, (topRightColor & 0xFF) / 255.0F, var31 / 255.0F);
         }

         if (var24 != null) {
            int var32 = bottomRightColor >> 24 & 0xFF;
            if (var32 == 0) {
               var32 = 255;
            }

            var24.set((bottomRightColor >> 16 & 0xFF) / 255.0F, (bottomRightColor >> 8 & 0xFF) / 255.0F, (bottomRightColor & 0xFF) / 255.0F, var32 / 255.0F);
         }

         BufferBuilder var33 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         var33.vertex(var15, x, y, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
         var33.vertex(var15, x, y + height, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
         var33.vertex(var15, x + width, y + height, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
         var33.vertex(var15, x + width, y, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(ShaderUtils.roundedRectOutline);
         BufferRenderer.drawWithGlobalProgram(var33.end());
         RenderSystem.disableBlend();
      }
   }

   public static void drawBlur(
      MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color
   ) {
      color = calcColor(color);
      if (BlurProgram.getBuffer2() != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Matrix4f var10 = matrices.peek().getPositionMatrix();
         ShaderProgram var11 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedTexture);
         GlUniform var12 = var11.getUniform("Size");
         GlUniform var13 = var11.getUniform("Radius");
         GlUniform var14 = var11.getUniform("Smoothness");
         GlUniform var15 = var11.getUniform("ColorModulator");
         if (var12 != null) {
            var12.set(width, height);
         }

         if (var13 != null) {
            var13.set(topLeft, topRight, bottomRight, bottomLeft);
         }

         if (var14 != null) {
            var14.set(0.5F);
         }

         if (var15 != null) {
            var15.set(1.0F, 1.0F, 1.0F, 1.0F);
         }

         RenderSystem.setShaderTexture(0, BlurProgram.getTexture());
         RenderSystem.setShader(ShaderUtils.roundedTexture);
         int var16 = mc.getWindow().getScaledWidth();
         int var17 = mc.getWindow().getScaledHeight();
         float var18 = x / var16;
         float var19 = (var17 - y) / var17;
         float var20 = (x + width) / var16;
         float var21 = (var17 - y - height) / var17;
         int var22 = color >> 24 & 0xFF;
         if (var22 == 0) {
            var22 = 255;
         }

         float var23 = (color >> 16 & 0xFF) / 255.0F;
         float var24 = (color >> 8 & 0xFF) / 255.0F;
         float var25 = (color & 0xFF) / 255.0F;
         float var26 = var22 / 255.0F;
         BufferBuilder var27 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var27.vertex(var10, x, y, 0.0F).texture(var18, var19).color(var23, var24, var25, var26);
         var27.vertex(var10, x, y + height, 0.0F).texture(var18, var21).color(var23, var24, var25, var26);
         var27.vertex(var10, x + width, y + height, 0.0F).texture(var20, var21).color(var23, var24, var25, var26);
         var27.vertex(var10, x + width, y, 0.0F).texture(var20, var19).color(var23, var24, var25, var26);
         BufferRenderer.drawWithGlobalProgram(var27.end());
         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.disableBlend();
      }
   }

   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
      drawBlur(matrices, x, y, width, height, radius, radius, radius, radius, color);
   }

   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, Vector4f radius, int color) {
      drawBlur(matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, color);
   }

   public static void startGlow(float radius, int color, GlowCallback callback, MatrixStack matrices) {
      int var4 = color >> 24 & 0xFF;
      int var5 = color >> 16 & 0xFF;
      int var6 = color >> 8 & 0xFF;
      int var7 = color & 0xFF;
      if (var4 == 0) {
         var4 = 255;
      }

      GlowProgram.getInstance().begin(radius, new Color(var5, var6, var7, var4));
      callback.render();
      GlowProgram.getInstance().end(matrices, callback);
   }

   public static void startGlow(float radius, float intensity, int color, GlowCallback callback, MatrixStack matrices) {
      int var5 = color >> 24 & 0xFF;
      int var6 = color >> 16 & 0xFF;
      int var7 = color >> 8 & 0xFF;
      int var8 = color & 0xFF;
      if (var5 == 0) {
         var5 = 255;
      }

      GlowProgram.getInstance().begin(radius, intensity, new Color(var6, var7, var8, var5));
      callback.render();
      GlowProgram.getInstance().end(matrices, callback);
   }

   public static void drawBlur(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      float blurStrength,
      int color
   ) {
      color = calcColor(color);
      blurStrength = calcStrength(blurStrength);
      BlurProgram.getInstance().request();
      if (BlurProgram.getBuffer2() != null) {
         BlurProgram.getInstance().setBlurOffset(blurStrength);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Matrix4f var11 = matrices.peek().getPositionMatrix();
         ShaderProgram var12 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedTexture);
         GlUniform var13 = var12.getUniform("Size");
         GlUniform var14 = var12.getUniform("Radius");
         GlUniform var15 = var12.getUniform("Smoothness");
         GlUniform var16 = var12.getUniform("ColorModulator");
         if (var13 != null) {
            var13.set(width, height);
         }

         if (var14 != null) {
            var14.set(topLeft, topRight, bottomRight, bottomLeft);
         }

         if (var15 != null) {
            var15.set(0.5F);
         }

         if (var16 != null) {
            var16.set(1.0F, 1.0F, 1.0F, 1.0F);
         }

         RenderSystem.setShaderTexture(0, BlurProgram.getTexture());
         RenderSystem.setShader(ShaderUtils.roundedTexture);
         int var17 = mc.getWindow().getScaledWidth();
         int var18 = mc.getWindow().getScaledHeight();
         float var19 = x / var17;
         float var20 = (var18 - y) / var18;
         float var21 = (x + width) / var17;
         float var22 = (var18 - y - height) / var18;
         int var23 = color >> 24 & 0xFF;
         if (var23 == 0) {
            var23 = 255;
         }

         float var24 = (color >> 16 & 0xFF) / 255.0F;
         float var25 = (color >> 8 & 0xFF) / 255.0F;
         float var26 = (color & 0xFF) / 255.0F;
         float var27 = var23 / 255.0F;
         BufferBuilder var28 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var28.vertex(var11, x, y, 0.0F).texture(var19, var20).color(var24, var25, var26, var27);
         var28.vertex(var11, x, y + height, 0.0F).texture(var19, var22).color(var24, var25, var26, var27);
         var28.vertex(var11, x + width, y + height, 0.0F).texture(var21, var22).color(var24, var25, var26, var27);
         var28.vertex(var11, x + width, y, 0.0F).texture(var21, var20).color(var24, var25, var26, var27);
         BufferRenderer.drawWithGlobalProgram(var28.end());
         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.disableBlend();
      }
   }

   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, float blurStrength, int color) {
      drawBlur(matrices, x, y, width, height, radius, radius, radius, radius, blurStrength, color);
   }

   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, Vector4f radius, float blurStrength, int color) {
      drawBlur(matrices, x, y, width, height, radius.x, radius.y, radius.z, radius.w, blurStrength, color);
   }

   public static void drawLiquidGlass(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean
   ) {
      drawLiquidGlass(
         matrices,
         x,
         y,
         width,
         height,
         topLeft,
         topRight,
         bottomRight,
         bottomLeft,
         color,
         globalAlpha,
         fresnelPower,
         fresnelColor,
         baseAlpha,
         fresnelInvert,
         fresnelMix,
         distortStrength,
         squirt,
         clean,
         1.0F
      );
   }

   public static void drawLiquidGlass(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean,
      float shineStrength
   ) {
      handleAction3(
         ShaderUtils.liquidGlass,
         true,
         matrices,
         x,
         y,
         width,
         height,
         topLeft,
         topRight,
         bottomRight,
         bottomLeft,
         color,
         globalAlpha,
         fresnelPower,
         fresnelColor,
         baseAlpha,
         fresnelInvert,
         fresnelMix,
         distortStrength,
         squirt,
         clean,
         shineStrength
      );
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean
   ) {
      handleAction3(
         ShaderUtils.hudLiquidGlass,
         false,
         matrices,
         x,
         y,
         width,
         height,
         topLeft,
         topRight,
         bottomRight,
         bottomLeft,
         color,
         globalAlpha,
         fresnelPower,
         fresnelColor,
         baseAlpha,
         fresnelInvert,
         fresnelMix,
         distortStrength,
         squirt,
         clean,
         0.0F
      );
   }

   private static void handleAction3(
      ShaderProgramKey shaderKey,
      boolean supportsShine,
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      float topLeft,
      float topRight,
      float bottomRight,
      float bottomLeft,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean,
      float shineStrength
   ) {
      if (flag && fvalue < 0.999F) {
         globalAlpha *= fvalue;
         baseAlpha *= fvalue;
      }

      int var22;
      if (clean) {
         var22 = mc.getFramebuffer().getColorAttachment();
      } else {
         BlurProgram.getInstance().request();
         if (BlurProgram.getBuffer1() == null) {
            return;
         }

         var22 = BlurProgram.getTexture();
         if (var22 == 0) {
            return;
         }
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, var22);
      ShaderProgram var23 = mc.getShaderLoader().getOrCreateProgram(shaderKey);
      GlUniform var24 = var23.getUniform("GlobalAlpha");
      GlUniform var25 = var23.getUniform("Size");
      GlUniform var26 = var23.getUniform("Radius");
      GlUniform var27 = var23.getUniform("Smoothness");
      GlUniform var28 = var23.getUniform("FresnelPower");
      GlUniform var29 = var23.getUniform("FresnelColor");
      GlUniform var30 = var23.getUniform("FresnelAlpha");
      GlUniform var31 = var23.getUniform("BaseAlpha");
      GlUniform var32 = var23.getUniform("FresnelInvert");
      GlUniform var33 = var23.getUniform("FresnelMix");
      GlUniform var34 = var23.getUniform("DistortStrength");
      GlUniform var35 = var23.getUniform("CornerSmoothness");
      GlUniform var36 = supportsShine ? var23.getUniform("ShineStrength") : null;
      GlUniform var37 = var23.getUniform("Time");
      if (var24 != null) {
         var24.set(globalAlpha);
      }

      if (var25 != null) {
         var25.set(width, height);
      }

      if (var26 != null) {
         var26.set(topLeft, topRight, bottomRight, bottomLeft);
      }

      if (var27 != null) {
         var27.set(0.5F);
      }

      if (var28 != null) {
         var28.set(fresnelPower);
      }

      int var38 = fresnelColor >> 24 & 0xFF;
      if (var38 == 0) {
         var38 = 255;
      }

      if (var29 != null) {
         var29.set((fresnelColor >> 16 & 0xFF) / 255.0F, (fresnelColor >> 8 & 0xFF) / 255.0F, (fresnelColor & 0xFF) / 255.0F);
      }

      if (var30 != null) {
         var30.set(var38 / 255.0F);
      }

      if (var31 != null) {
         var31.set(baseAlpha);
      }

      if (var32 != null) {
         var32.set(fresnelInvert ? 1 : 0);
      }

      if (var33 != null) {
         var33.set(fresnelMix);
      }

      if (var34 != null) {
         var34.set(distortStrength);
      }

      if (var35 != null) {
         var35.set(squirt);
      }

      if (var36 != null) {
         var36.set(shineStrength);
      }

      if (var37 != null) {
         float var39 = (float)(System.currentTimeMillis() % 600000L) / 1000.0F;
         var37.set(var39);
      }

      float var57 = mc.getWindow().getFramebufferWidth();
      float var40 = mc.getWindow().getFramebufferHeight();
      float var41 = (float)mc.getWindow().getScaleFactor();
      float var42 = x * var41;
      float var43 = y * var41;
      float var44 = width * var41;
      float var45 = height * var41;
      float var46 = var42 / var57;
      float var47 = 1.0F - var43 / var40;
      float var48 = (var42 + var44) / var57;
      float var49 = 1.0F - (var43 + var45) / var40;
      int var50 = color >> 24 & 0xFF;
      if (var50 == 0) {
         var50 = 255;
      }

      float var51 = (color >> 16 & 0xFF) / 255.0F;
      float var52 = (color >> 8 & 0xFF) / 255.0F;
      float var53 = (color & 0xFF) / 255.0F;
      float var54 = var50 / 255.0F;
      RenderSystem.setShader(shaderKey);
      Matrix4f var55 = matrices.peek().getPositionMatrix();
      BufferBuilder var56 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var56.vertex(var55, x, y, 0.0F).texture(var46, var47).color(var51, var52, var53, var54);
      var56.vertex(var55, x, y + height, 0.0F).texture(var46, var49).color(var51, var52, var53, var54);
      var56.vertex(var55, x + width, y + height, 0.0F).texture(var48, var49).color(var51, var52, var53, var54);
      var56.vertex(var55, x + width, y, 0.0F).texture(var48, var47).color(var51, var52, var53, var54);
      BufferRenderer.drawWithGlobalProgram(var56.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public static void drawLiquidGlass(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      Vector4f radius,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean
   ) {
      drawLiquidGlass(
         matrices,
         x,
         y,
         width,
         height,
         radius.x,
         radius.y,
         radius.z,
         radius.w,
         color,
         globalAlpha,
         fresnelPower,
         fresnelColor,
         baseAlpha,
         fresnelInvert,
         fresnelMix,
         distortStrength,
         squirt,
         clean
      );
   }

   public static void drawLiquidGlass(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      Vector4f radius,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean,
      float shineStrength
   ) {
      drawLiquidGlass(
         matrices,
         x,
         y,
         width,
         height,
         radius.x,
         radius.y,
         radius.z,
         radius.w,
         color,
         globalAlpha,
         fresnelPower,
         fresnelColor,
         baseAlpha,
         fresnelInvert,
         fresnelMix,
         distortStrength,
         squirt,
         clean,
         shineStrength
      );
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      Vector4f radius,
      int color,
      float globalAlpha,
      float fresnelPower,
      int fresnelColor,
      float baseAlpha,
      boolean fresnelInvert,
      float fresnelMix,
      float distortStrength,
      float squirt,
      boolean clean
   ) {
      drawHudLiquidGlass(
         matrices,
         x,
         y,
         width,
         height,
         radius.x,
         radius.y,
         radius.z,
         radius.w,
         color,
         globalAlpha,
         fresnelPower,
         fresnelColor,
         baseAlpha,
         fresnelInvert,
         fresnelMix,
         distortStrength,
         squirt,
         clean
      );
   }

   public static void drawLine(MatrixStack matrices, float x1, float y1, float x2, float y2, float thickness, int color) {
      color = calcColor(color);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Matrix4f var7 = matrices.peek().getPositionMatrix();
      int var8 = color >> 24 & 0xFF;
      if (var8 == 0) {
         var8 = 255;
      }

      float var9 = (color >> 16 & 0xFF) / 255.0F;
      float var10 = (color >> 8 & 0xFF) / 255.0F;
      float var11 = (color & 0xFF) / 255.0F;
      float var12 = var8 / 255.0F;
      float var13 = x2 - x1;
      float var14 = y2 - y1;
      float var15 = (float)Math.sqrt(var13 * var13 + var14 * var14);
      if (!(var15 < 0.001F)) {
         float var16 = (float)Math.atan2(var14, var13);
         float var17 = (float)Math.cos(var16);
         float var18 = (float)Math.sin(var16);
         float var19 = thickness / 2.0F;
         float var20 = x1 - var18 * var19;
         float var21 = y1 + var17 * var19;
         float var22 = x1 + var18 * var19;
         float var23 = y1 - var17 * var19;
         float var24 = x2 - var18 * var19;
         float var25 = y2 + var17 * var19;
         float var26 = x2 + var18 * var19;
         float var27 = y2 - var17 * var19;
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder var28 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         var28.vertex(var7, var20, var21, 0.0F).color(var9, var10, var11, var12);
         var28.vertex(var7, var24, var25, 0.0F).color(var9, var10, var11, var12);
         var28.vertex(var7, var26, var27, 0.0F).color(var9, var10, var11, var12);
         var28.vertex(var7, var22, var23, 0.0F).color(var9, var10, var11, var12);
         BufferRenderer.drawWithGlobalProgram(var28.end());
         RenderSystem.disableBlend();
      }
   }

   public static void drawSmoothLine(MatrixStack matrices, float x1, float y1, float x2, float y2, float thickness, int color) {
      float var7 = x2 - x1;
      float var8 = y2 - y1;
      float var9 = (float)Math.sqrt(var7 * var7 + var8 * var8);
      if (!(var9 < 0.001F) && !(thickness <= 0.0F)) {
         float var10 = 3.0F;
         matrices.push();
         matrices.translate(x1, y1, 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float)Math.atan2(var8, var7)));
         matrices.scale(1.0F / var10, 1.0F / var10, 1.0F);
         float var11 = thickness * var10 / 2.0F;
         drawRoundedRect(matrices, -var11, -var11, var9 * var10 + thickness * var10, thickness * var10, var11, color);
         matrices.pop();
      }
   }

   public static void drawCheckMark(MatrixStack matrices, float centerX, float centerY, float size, float thickness, float progress, int color) {
      float var7 = progress < 0.0F ? 0.0F : (progress > 1.0F ? 1.0F : progress);
      if (!(var7 <= 0.001F)) {
         float var8 = centerX - size;
         float var9 = centerY;
         float var10 = centerX - size * 0.25F;
         float var11 = centerY + size * 0.7F;
         float var12 = centerX + size;
         float var13 = centerY - size * 0.75F;
         float var14 = (float)Math.hypot(var10 - var8, var11 - var9);
         float var15 = (float)Math.hypot(var12 - var10, var13 - var11);
         float var16 = (var14 + var15) * var7;
         float var17 = Math.min(1.0F, var16 / var14);
         drawSmoothLine(matrices, var8, var9, var8 + (var10 - var8) * var17, var9 + (var11 - var9) * var17, thickness, color);
         if (var16 > var14) {
            float var18 = Math.min(1.0F, (var16 - var14) / var15);
            drawSmoothLine(matrices, var10, var11, var10 + (var12 - var10) * var18, var11 + (var13 - var11) * var18, thickness, color);
         }
      }
   }

   public static void drawFilledCircle(MatrixStack matrices, float cx, float cy, float radius, int color) {
      byte var5 = 64;
      float var6 = (float)((Math.PI * 2) / var5);

      for (int var7 = 0; var7 < var5; var7++) {
         float var8 = var7 * var6;
         float var9 = (var7 + 1) * var6;
         float var10 = cx + radius * (float)Math.cos(var8);
         float var11 = cy + radius * (float)Math.sin(var8);
         float var12 = cx + radius * (float)Math.cos(var9);
         float var13 = cy + radius * (float)Math.sin(var9);
         drawTriangle(matrices, cx, cy, var10, var11, var12, var13, color);
      }
   }

   public static void drawTriangle(MatrixStack matrices, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
      color = calcColor(color);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Matrix4f var8 = matrices.peek().getPositionMatrix();
      int var9 = color >> 24 & 0xFF;
      if (var9 == 0) {
         var9 = 255;
      }

      float var10 = (color >> 16 & 0xFF) / 255.0F;
      float var11 = (color >> 8 & 0xFF) / 255.0F;
      float var12 = (color & 0xFF) / 255.0F;
      float var13 = var9 / 255.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
      var14.vertex(var8, x1, y1, 0.0F).color(var10, var11, var12, var13);
      var14.vertex(var8, x2, y2, 0.0F).color(var10, var11, var12, var13);
      var14.vertex(var8, x3, y3, 0.0F).color(var10, var11, var12, var13);
      BufferRenderer.drawWithGlobalProgram(var14.end());
      RenderSystem.disableBlend();
   }

   public static void drawCorner(MatrixStack matrices, float x, float y, float radius, float thickness, int color, int corner) {
      if (!(radius <= 0.0F) && !(thickness <= 0.0F)) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Matrix4f var7 = matrices.peek().getPositionMatrix();
         int var8 = color >> 24 & 0xFF;
         if (var8 == 0) {
            var8 = 255;
         }

         float var9 = (color >> 16 & 0xFF) / 255.0F;
         float var10 = (color >> 8 & 0xFF) / 255.0F;
         float var11 = (color & 0xFF) / 255.0F;
         float var12 = var8 / 255.0F;
         int var13 = Math.max(8, (int)(radius * 1.5F));
         float var14 = (float)((Math.PI / 2) / var13);
         float var15;
         float var17;
         float var18;
         switch (corner) {
            case 0:
               var17 = x + radius;
               var18 = y + radius;
               var15 = (float) Math.PI;
               float var45 = (float) (Math.PI * 3.0 / 2.0);
               boolean var48 = true;
               break;
            case 1:
               var17 = x - radius;
               var18 = y + radius;
               var15 = (float) (Math.PI * 3.0 / 2.0);
               float var44 = (float) (Math.PI * 2);
               boolean var47 = false;
               break;
            case 2:
               var17 = x - radius;
               var18 = y - radius;
               var15 = 0.0F;
               float var43 = (float) (Math.PI / 2);
               boolean var46 = true;
               break;
            case 3:
               var17 = x + radius;
               var18 = y - radius;
               var15 = (float) (Math.PI / 2);
               float var16 = (float) Math.PI;
               boolean var19 = false;
               break;
            default:
               return;
         }

         float[] var20 = new float[var13 + 1];
         float[] var21 = new float[var13 + 1];

         for (int var22 = 0; var22 <= var13; var22++) {
            float var23 = var15 + var22 * var14;
            var20[var22] = var17 + radius * (float)Math.cos(var23);
            var21[var22] = var18 + radius * (float)Math.sin(var23);
         }

         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

         for (int var49 = 0; var49 < var13; var49++) {
            float var51 = var20[var49];
            float var24 = var21[var49];
            float var25 = var20[var49 + 1];
            float var26 = var21[var49 + 1];
            float var27 = var25 - var51;
            float var28 = var26 - var24;
            float var29 = (float)Math.sqrt(var27 * var27 + var28 * var28);
            if (!(var29 < 0.001F)) {
               float var30 = (float)Math.atan2(var28, var27);
               float var31 = (float)Math.cos(var30);
               float var32 = (float)Math.sin(var30);
               float var33 = thickness / 2.0F;
               float var34 = var51 - var32 * var33;
               float var35 = var24 + var31 * var33;
               float var36 = var51 + var32 * var33;
               float var37 = var24 - var31 * var33;
               float var38 = var25 - var32 * var33;
               float var39 = var26 + var31 * var33;
               float var40 = var25 + var32 * var33;
               float var41 = var26 - var31 * var33;
               BufferBuilder var42 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
               var42.vertex(var7, var34, var35, 0.0F).color(var9, var10, var11, var12);
               var42.vertex(var7, var38, var39, 0.0F).color(var9, var10, var11, var12);
               var42.vertex(var7, var40, var41, 0.0F).color(var9, var10, var11, var12);
               var42.vertex(var7, var36, var37, 0.0F).color(var9, var10, var11, var12);
               BufferRenderer.drawWithGlobalProgram(var42.end());
            }
         }

         float var50 = radius * 0.7F;
         switch (corner) {
            case 0:
               float var58 = x + radius;
               float var66 = y + radius;
               float var74 = x + radius;
               float var82 = y - var50;
               handleMatrices(matrices, var58, var66, var74, var82, thickness, color);
               var58 = x + radius;
               var66 = y + radius;
               var74 = x - var50;
               var82 = y + radius;
               handleMatrices(matrices, var58, var66, var74, var82, thickness, color);
               break;
            case 1:
               float var56 = x - radius;
               float var64 = y + radius;
               float var72 = x - radius;
               float var80 = y - var50;
               handleMatrices(matrices, var56, var64, var72, var80, thickness, color);
               var56 = x - radius;
               var64 = y + radius;
               var72 = x + var50;
               var80 = y + radius;
               handleMatrices(matrices, var56, var64, var72, var80, thickness, color);
               break;
            case 2:
               float var54 = x - radius;
               float var62 = y - radius;
               float var70 = x - radius;
               float var78 = y + var50;
               handleMatrices(matrices, var54, var62, var70, var78, thickness, color);
               var54 = x - radius;
               var62 = y - radius;
               var70 = x + var50;
               var78 = y - radius;
               handleMatrices(matrices, var54, var62, var70, var78, thickness, color);
               break;
            case 3:
               float var52 = x + radius;
               float var60 = y - radius;
               float var68 = x + radius;
               float var76 = y + var50;
               handleMatrices(matrices, var52, var60, var68, var76, thickness, color);
               var52 = x + radius;
               var60 = y - radius;
               var68 = x - var50;
               var76 = y - radius;
               handleMatrices(matrices, var52, var60, var68, var76, thickness, color);
         }

         RenderSystem.disableBlend();
      }
   }

   private static void handleMatrices(MatrixStack matrices, float x1, float y1, float x2, float y2, float thickness, int color) {
      if (!(thickness <= 0.0F)) {
         Matrix4f var7 = matrices.peek().getPositionMatrix();
         int var8 = color >> 24 & 0xFF;
         if (var8 == 0) {
            var8 = 255;
         }

         float var9 = (color >> 16 & 0xFF) / 255.0F;
         float var10 = (color >> 8 & 0xFF) / 255.0F;
         float var11 = (color & 0xFF) / 255.0F;
         float var12 = var8 / 255.0F;
         float var13 = x2 - x1;
         float var14 = y2 - y1;
         float var15 = (float)Math.sqrt(var13 * var13 + var14 * var14);
         if (!(var15 < 0.001F)) {
            float var16 = (float)Math.atan2(var14, var13);
            float var17 = (float)Math.cos(var16);
            float var18 = (float)Math.sin(var16);
            float var19 = thickness / 2.0F;
            float var20 = x1 - var18 * var19;
            float var21 = y1 + var17 * var19;
            float var22 = x1 + var18 * var19;
            float var23 = y1 - var17 * var19;
            float var24 = x2 - var18 * var19;
            float var25 = y2 + var17 * var19;
            float var26 = x2 + var18 * var19;
            float var27 = y2 - var17 * var19;
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder var28 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            var28.vertex(var7, var20, var21, 0.0F).color(var9, var10, var11, var12);
            var28.vertex(var7, var24, var25, 0.0F).color(var9, var10, var11, var12);
            var28.vertex(var7, var26, var27, 0.0F).color(var9, var10, var11, var12);
            var28.vertex(var7, var22, var23, 0.0F).color(var9, var10, var11, var12);
            BufferRenderer.drawWithGlobalProgram(var28.end());
         }
      }
   }

   public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int color) {
      color = calcColor(color);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Matrix4f var6 = matrices.peek().getPositionMatrix();
      int var7 = color >> 24 & 0xFF;
      if (var7 == 0) {
         var7 = 255;
      }

      float var8 = (color >> 16 & 0xFF) / 255.0F;
      float var9 = (color >> 8 & 0xFF) / 255.0F;
      float var10 = (color & 0xFF) / 255.0F;
      float var11 = var7 / 255.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder var12 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      var12.vertex(var6, x, y, 0.0F).color(var8, var9, var10, var11);
      var12.vertex(var6, x, y + height, 0.0F).color(var8, var9, var10, var11);
      var12.vertex(var6, x + width, y + height, 0.0F).color(var8, var9, var10, var11);
      var12.vertex(var6, x + width, y, 0.0F).color(var8, var9, var10, var11);
      BufferRenderer.drawWithGlobalProgram(var12.end());
      RenderSystem.disableBlend();
   }

   @Generated
   private RenderUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
