package dlc.lumen.client.ui.clickgui.gif;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GuiGif {
   private static int index = 0;
   private final List<Identifier> identifiers = new ArrayList<>();
   private final int[] index2;
   private final long timestamp;
   public final int width;
   public final int height;
   private final long timestamp2 = System.currentTimeMillis();

   private GuiGif(List<Identifier> frames, int[] delays, int w, int h) {
      this.identifiers.addAll(frames);
      this.index2 = delays;
      this.width = w;
      this.height = h;
      long var5 = 0L;

      for (int var10 : delays) {
         var5 += var10;
      }

      this.timestamp = Math.max(1L, var5);
   }

   public static GuiGif fromDecoder(GifDecoder dec) {
      ArrayList var1 = new ArrayList();
      int[] var2 = new int[dec.frames.size()];

      for (int var3 = 0; var3 < dec.frames.size(); var3++) {
         Identifier var4 = refreshTexture2(dec.frames.get(var3));
         var1.add(var4);
         var2[var3] = dec.delaysMs.get(var3);
      }

      return new GuiGif(var1, var2, dec.width, dec.height);
   }

   public static GuiGif fromCircularDecoder(GifDecoder dec, int size) {
      ArrayList var2 = new ArrayList();
      int[] var3 = new int[dec.frames.size()];

      for (int var4 = 0; var4 < dec.frames.size(); var4++) {
         var2.add(refreshTexture2(refreshTexture(dec.frames.get(var4), size)));
         var3[var4] = dec.delaysMs.get(var4);
      }

      return new GuiGif(var2, var3, size, size);
   }

   private static BufferedImage refreshTexture(BufferedImage src, int size) {
      int var2 = src.getWidth();
      int var3 = src.getHeight();
      int var4 = Math.min(var2, var3);
      int var5 = (var2 - var4) / 2;
      int var6 = (var3 - var4) / 2;
      BufferedImage var7 = new BufferedImage(size, size, 2);
      Graphics2D var8 = var7.createGraphics();
      var8.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      var8.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var8.drawImage(src, 0, 0, size, size, var5, var6, var5 + var4, var6 + var4, null);
      var8.dispose();
      float var9 = size / 2.0F;
      float var10 = size / 2.0F;

      for (int var11 = 0; var11 < size; var11++) {
         for (int var12 = 0; var12 < size; var12++) {
            float var13 = var12 + 0.5F - var9;
            float var14 = var11 + 0.5F - var9;
            float var15 = var10 - (float)Math.sqrt(var13 * var13 + var14 * var14);
            float var16 = var15 >= 1.0F ? 1.0F : (var15 <= 0.0F ? 0.0F : var15);
            if (!(var16 >= 1.0F)) {
               int var17 = var7.getRGB(var12, var11);
               int var18 = Math.round((var17 >>> 24) * var16);
               var7.setRGB(var12, var11, var18 << 24 | var17 & 16777215);
            }
         }
      }

      return var7;
   }

   private static Identifier refreshTexture2(BufferedImage argb) {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         ImageIO.write(argb, "png", var1);
         NativeImage var2 = NativeImage.read(new ByteArrayInputStream(var1.toByteArray()));
         NativeImageBackedTexture var3 = new NativeImageBackedTexture(var2);
         Identifier var4 = Identifier.of("lumen", "guigif/" + index++);
         MinecraftClient.getInstance().getTextureManager().registerTexture(var4, var3);
         return var4;
      } catch (Exception var5) {
         return null;
      }
   }

   private Identifier releaseTexture() {
      long var1 = (System.currentTimeMillis() - this.timestamp2) % this.timestamp;
      long var3 = 0L;

      for (int var5 = 0; var5 < this.identifiers.size(); var5++) {
         var3 += this.index2[var5];
         if (var1 < var3) {
            return this.identifiers.get(var5);
         }
      }

      return this.identifiers.get(this.identifiers.size() - 1);
   }

   public void render(MatrixStack matrices, float x, float y, float drawW, float drawH, float alpha) {
      Identifier var7 = this.releaseTexture();
      if (var7 != null) {
         RenderUtils.drawImage(matrices, var7, x, y, drawW, drawH, ColorUtils.applyAlpha(-1, alpha));
      }
   }

   public void dispose() {
      TextureManager var1 = MinecraftClient.getInstance().getTextureManager();

      for (Identifier var3 : this.identifiers) {
         if (var3 != null) {
            try {
               var1.destroyTexture(var3);
            } catch (Exception var5) {
            }
         }
      }

      this.identifiers.clear();
   }
}
