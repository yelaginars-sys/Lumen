package dlc.lumen.api.utils.media;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class MusicArtwork {
   private static final Identifier id = Identifier.of("lumen", "music/artwork");
   private static int count;
   private static boolean flag;
   private static float fvalue;
   private static float fvalue2;
   private static float fvalue3 = 1.0F;
   private static float fvalue4 = 1.0F;

   private MusicArtwork() {
   }

   public static Identifier update(MusicSession.Snapshot snapshot) {
      int var1 = snapshot.artworkId();
      if (var1 == count) {
         return flag ? id : null;
      }

      count = var1;
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null && var2.getTextureManager() != null) {
         handleClient(var2);
         byte[] var3 = snapshot.artwork();
         if (var3 == null) {
            return null;
         }

         try {
            NativeImage var4 = NativeImage.read(var3);
            handleWidth(var4.getWidth(), var4.getHeight());
            var2.getTextureManager().registerTexture(id, new NativeImageBackedTexture(var4));
            flag = true;
            return id;
         } catch (Throwable var5) {
            flag = false;
            return null;
         }
      } else {
         return null;
      }
   }

   public static float u1() {
      return fvalue;
   }

   public static float v1() {
      return fvalue2;
   }

   public static float u2() {
      return fvalue3;
   }

   public static float v2() {
      return fvalue4;
   }

   private static void handleWidth(int width, int height) {
      fvalue = 0.0F;
      fvalue2 = 0.0F;
      fvalue3 = 1.0F;
      fvalue4 = 1.0F;
      if (width > 0 && height > 0) {
         if (width > height) {
            float var2 = (float)height / width;
            fvalue = (1.0F - var2) * 0.5F;
            fvalue3 = 1.0F - fvalue;
         } else if (height > width) {
            float var3 = (float)width / height;
            fvalue2 = (1.0F - var3) * 0.5F;
            fvalue4 = 1.0F - fvalue2;
         }
      }
   }

   private static void handleClient(MinecraftClient client) {
      if (flag) {
         client.getTextureManager().destroyTexture(id);
         flag = false;
      }
   }
}
