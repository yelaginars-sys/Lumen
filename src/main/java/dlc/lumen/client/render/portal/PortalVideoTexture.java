package dlc.lumen.client.render.portal;

import dlc.lumen.client.voice.call.CallVideo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.util.Identifier;

public final class PortalVideoTexture implements AutoCloseable {
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "portal/call_video");
   private NativeImageBackedTexture frame2;
   private int width2;
   private int height2;
   private int index = -1;
   private long timestamp;

   public Identifier update(CallVideo.DecodedFrame frame) {
      if (frame != null && frame.width() > 0 && frame.height() > 0) {
         if (frame.frameId() == this.index) {
            return TEXTURE_ID;
         }

         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && var2.getTextureManager() != null) {
            if (this.frame2 == null || this.width2 != frame.width() || this.height2 != frame.height()) {
               this.close();
               this.width2 = frame.width();
               this.height2 = frame.height();
               NativeImage var3 = new NativeImage(Format.RGBA, this.width2, this.height2, false);
               this.frame2 = new NativeImageBackedTexture(var3);
               var2.getTextureManager().registerTexture(TEXTURE_ID, this.frame2);
            }

            NativeImage var8 = this.frame2.getImage();
            if (var8 == null) {
               return null;
            }

            int[] var4 = frame.pixels();

            for (int var5 = 0; var5 < this.height2; var5++) {
               int var6 = var5 * this.width2;

               for (int var7 = 0; var7 < this.width2; var7++) {
                  var8.setColorArgb(var7, var5, 0xFF000000 | var4[var6 + var7] & 16777215);
               }
            }

            this.frame2.upload();
            this.index = frame.frameId();
            this.timestamp = System.currentTimeMillis();
            return TEXTURE_ID;
         } else {
            return null;
         }
      } else {
         return this.frame2 == null ? null : TEXTURE_ID;
      }
   }

   public boolean hasFrame() {
      return this.frame2 != null;
   }

   public long millisSinceUpdate() {
      return this.timestamp == 0L ? Long.MAX_VALUE : System.currentTimeMillis() - this.timestamp;
   }

   public int getWidth() {
      return this.width2;
   }

   public int getHeight() {
      return this.height2;
   }

   @Override
   public void close() {
      if (this.frame2 != null) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null && var1.getTextureManager() != null) {
            var1.getTextureManager().destroyTexture(TEXTURE_ID);
         }

         this.frame2.close();
         this.frame2 = null;
         this.width2 = 0;
         this.height2 = 0;
         this.index = -1;
         this.timestamp = 0L;
      }
   }
}
