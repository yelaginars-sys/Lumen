package dlc.lumen.client.render.portal;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

public final class PortalScreenCapture implements AutoCloseable {
   private SimpleFramebuffer simpleFramebuffer;
   private int index;
   private int index2;
   private final BufferedImage[] bufferedImage = new BufferedImage[2];
   private final int[][] resolutions = new int[2][];
   private int index3;
   private long timestamp;
   private String failure = "";

   public String getFailure() {
      return this.failure;
   }

   public BufferedImage capture(int targetWidth, int targetHeight, long minIntervalMs) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      if (var5 != null && targetWidth > 0 && targetHeight > 0) {
         long var6 = System.currentTimeMillis();
         if (var6 - this.timestamp < minIntervalMs) {
            return null;
         }

         Framebuffer var8 = var5.getFramebuffer();
         if (var8 == null) {
            return null;
         }

         try {
            this.helper(targetWidth, targetHeight);
            this.simpleFramebuffer.beginWrite(true);
            var8.draw(targetWidth, targetHeight);
            this.simpleFramebuffer.endWrite();
            var8.beginWrite(true);
            NativeImage var9 = ScreenshotRecorder.takeScreenshot(this.simpleFramebuffer);
            if (var9 == null) {
               return null;
            }

            try {
               if (var9.getWidth() != this.index || var9.getHeight() != this.index2) {
                  return null;
               }

               this.index3 = (this.index3 + 1) % this.bufferedImage.length;
               int[] var10 = this.resolutions[this.index3];
               int[] var11 = var9.copyPixelsArgb();
               System.arraycopy(var11, 0, var10, 0, Math.min(var11.length, var10.length));
            } finally {
               var9.close();
            }

            this.timestamp = var6;
            this.failure = "";
            return this.bufferedImage[this.index3];
         } catch (Throwable var16) {
            this.failure = helper2(var16);
            return null;
         }
      } else {
         return null;
      }
   }

   private void helper(int targetWidth, int targetHeight) {
      if (this.simpleFramebuffer == null || this.index != targetWidth || this.index2 != targetHeight) {
         if (this.simpleFramebuffer != null) {
            this.simpleFramebuffer.delete();
         }

         this.index = targetWidth;
         this.index2 = targetHeight;
         this.simpleFramebuffer = new SimpleFramebuffer(this.index, this.index2, false);

         for (int var3 = 0; var3 < this.bufferedImage.length; var3++) {
            this.bufferedImage[var3] = new BufferedImage(this.index, this.index2, 1);
            this.resolutions[var3] = ((DataBufferInt)this.bufferedImage[var3].getRaster().getDataBuffer()).getData();
         }
      }
   }

   @Override
   public void close() {
      if (this.simpleFramebuffer != null) {
         this.simpleFramebuffer.delete();
         this.simpleFramebuffer = null;
      }

      this.index = 0;
      this.index2 = 0;
      this.timestamp = 0L;
   }

   private static String helper2(Throwable throwable) {
      String var1 = throwable.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : throwable.getClass().getSimpleName();
   }
}
