package dlc.lumen.client.social;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.utils.render.ShaderUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class GlobalProfileAvatar {
   private static final Identifier AVATAR_TEXTURE_ID = Identifier.of("lumen", "global_profile_avatar");
   private static final int AVATAR_SIZE = 256;
   private static NativeImageBackedTexture avatarTexture;
   private static long avatarLastModified = Long.MIN_VALUE;

   private GlobalProfileAvatar() {
   }

   public static synchronized boolean hasSavedAvatar() {
      File var0 = avatarFile();
      return var0.isFile() && var0.length() > 0L;
   }

   public static synchronized boolean saveFromFile(File source) {
      if (source != null && source.isFile()) {
         try {
            BufferedImage var1 = ImageIO.read(source);
            if (var1 != null && var1.getWidth() > 0 && var1.getHeight() > 0) {
               int var2 = Math.min(var1.getWidth(), var1.getHeight());
               int var3 = (var1.getWidth() - var2) / 2;
               int var4 = (var1.getHeight() - var2) / 2;
               BufferedImage var5 = new BufferedImage(256, 256, 2);
               Graphics2D var6 = var5.createGraphics();
               var6.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
               var6.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               var6.drawImage(var1, 0, 0, 256, 256, var3, var4, var3 + var2, var4 + var2, null);
               var6.dispose();
               File var7 = avatarFile();
               File var8 = var7.getParentFile();
               if (var8 != null) {
                  var8.mkdirs();
               }

               ImageIO.write(var5, "png", var7);
               releaseAvatarTexture();
               return true;
            } else {
               return false;
            }
         } catch (Exception var9) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static synchronized void clear() {
      File var0 = avatarFile();
      if (var0.exists()) {
         var0.delete();
      }

      releaseAvatarTexture();
   }

   public static synchronized Identifier textureId() {
      ensureAvatarTexture();
      return avatarTexture == null ? null : AVATAR_TEXTURE_ID;
   }

   public static synchronized boolean drawRounded(DrawContext context, float x, float y, float w, float h, float radius, int color) {
      Identifier var7 = textureId();
      if (context != null && var7 != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Matrix4f var8 = context.getMatrices().peek().getPositionMatrix();
         MinecraftClient var9 = MinecraftClient.getInstance();
         ShaderProgram var10 = var9.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedTexture);
         GlUniform var11 = var10.getUniform("Size");
         GlUniform var12 = var10.getUniform("Radius");
         GlUniform var13 = var10.getUniform("Smoothness");
         GlUniform var14 = var10.getUniform("ColorModulator");
         if (var11 != null) {
            var11.set(w, h);
         }

         if (var12 != null) {
            var12.set(radius, radius, radius, radius);
         }

         if (var13 != null) {
            var13.set(0.5F);
         }

         if (var14 != null) {
            var14.set(1.0F, 1.0F, 1.0F, 1.0F);
         }

         RenderSystem.setShaderTexture(0, var7);
         RenderSystem.setShader(ShaderUtils.roundedTexture);
         int var15 = color >> 24 & 0xFF;
         if (var15 == 0) {
            var15 = 255;
         }

         float var16 = (color >> 16 & 0xFF) / 255.0F;
         float var17 = (color >> 8 & 0xFF) / 255.0F;
         float var18 = (color & 0xFF) / 255.0F;
         float var19 = var15 / 255.0F;
         BufferBuilder var20 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var20.vertex(var8, x, y, 0.0F).texture(0.0F, 0.0F).color(var16, var17, var18, var19);
         var20.vertex(var8, x, y + h, 0.0F).texture(0.0F, 1.0F).color(var16, var17, var18, var19);
         var20.vertex(var8, x + w, y + h, 0.0F).texture(1.0F, 1.0F).color(var16, var17, var18, var19);
         var20.vertex(var8, x + w, y, 0.0F).texture(1.0F, 0.0F).color(var16, var17, var18, var19);
         BufferRenderer.drawWithGlobalProgram(var20.end());
         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.disableBlend();
         return true;
      } else {
         return false;
      }
   }

   private static File avatarFile() {
      File var0 = Lumen.INSTANCE != null && Lumen.INSTANCE.globalsDir != null ? Lumen.INSTANCE.globalsDir : new File("C:\\lumenClient", "lumen");
      return new File(var0, "global-avatar.png");
   }

   private static void ensureAvatarTexture() {
      File var0 = avatarFile();
      long var1 = var0.isFile() ? var0.lastModified() : Long.MIN_VALUE;
      if (!var0.isFile()) {
         releaseAvatarTexture();
      } else if (avatarTexture == null || avatarLastModified != var1) {
         releaseAvatarTexture();
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3 != null) {
            try (InputStream var4 = Files.newInputStream(var0.toPath())) {
               NativeImage var5 = NativeImage.read(var4);
               avatarTexture = new NativeImageBackedTexture(var5);
               var3.getTextureManager().registerTexture(AVATAR_TEXTURE_ID, avatarTexture);
               avatarLastModified = var1;
            } catch (Exception var9) {
               releaseAvatarTexture();
            }
         }
      }
   }

   private static void releaseAvatarTexture() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null) {
         var0.getTextureManager().destroyTexture(AVATAR_TEXTURE_ID);
      }

      if (avatarTexture != null) {
         avatarTexture = null;
      }

      avatarLastModified = Long.MIN_VALUE;
   }
}
