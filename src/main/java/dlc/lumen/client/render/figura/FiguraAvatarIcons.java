package dlc.lumen.client.render.figura;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class FiguraAvatarIcons {
   private static final Map<String, Identifier> STRINGS = new HashMap<>();
   private static final Set<String> STRINGS2 = new HashSet<>();
   private static int index = 0;
   private static final int INDEX = 128;

   private FiguraAvatarIcons() {
   }

   public static Identifier icon(FiguraBridge.AvatarInfo info) {
      if (info == null) {
         return null;
      }

      Identifier var1 = STRINGS.get(info.id());
      if (var1 != null) {
         return var1;
      }

      if (STRINGS2.contains(info.id())) {
         return null;
      }

      NativeImage var2 = ensureTexture(info.dir());
      if (var2 == null) {
         var2 = ensureTexture2(info.dir());
      }

      if (var2 == null) {
         STRINGS2.add(info.id());
         return null;
      }

      try {
         NativeImageBackedTexture var3 = new NativeImageBackedTexture(var2);
         Identifier var4 = Identifier.of("lumen", "figura_icon/" + index++);
         MinecraftClient.getInstance().getTextureManager().registerTexture(var4, var3);
         STRINGS.put(info.id(), var4);
         return var4;
      } catch (Throwable var5) {
         var2.close();
         STRINGS2.add(info.id());
         return null;
      }
   }

   private static NativeImage ensureTexture(Path dir) {
      Path var1 = dir.resolve("avatar.png");
      if (!Files.isRegularFile(var1)) {
         return null;
      }

      NativeImage var2 = null;

      try (InputStream var3 = Files.newInputStream(var1)) {
         var2 = NativeImage.read(var3);
      } catch (Throwable var8) {
      }

      if (var2 == null) {
         var2 = helper(var1);
      }

      if (var2 == null) {
         return null;
      }

      if (var2.getWidth() >= 48 && var2.getHeight() >= 48) {
         return var2;
      }

      var2.close();
      return null;
   }

   private static NativeImage ensureTexture2(Path dir) {
      try {
         int[] var1 = BbModelThumbnail.renderArgb(dir, 128);
         if (var1 == null) {
            return null;
         }

         BufferedImage var2 = new BufferedImage(128, 128, 2);
         var2.setRGB(0, 0, 128, 128, var1, 0, 128);
         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         ImageIO.write(var2, "png", var3);
         return NativeImage.read(new ByteArrayInputStream(var3.toByteArray()));
      } catch (Throwable var4) {
         return null;
      }
   }

   private static NativeImage helper(Path png) {
      try {
         BufferedImage var1 = ImageIO.read(png.toFile());
         if (var1 == null) {
            return null;
         }

         BufferedImage var2 = new BufferedImage(var1.getWidth(), var1.getHeight(), 2);
         Graphics var3 = var2.getGraphics();
         var3.drawImage(var1, 0, 0, null);
         var3.dispose();
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         ImageIO.write(var2, "png", var4);
         return NativeImage.read(new ByteArrayInputStream(var4.toByteArray()));
      } catch (Throwable var5) {
         return null;
      }
   }
}
