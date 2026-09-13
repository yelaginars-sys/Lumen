package dlc.lumen.client.ui.clickgui.gif;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NodeList;

public final class GifDecoder {
   public final List<BufferedImage> frames = new ArrayList<>();
   public final List<Integer> delaysMs = new ArrayList<>();
   public int width;
   public int height;

   private GifDecoder() {
   }

   public static GifDecoder decode(InputStream stream) throws Exception {
      GifDecoder var1 = new GifDecoder();
      ImageReader var2 = ImageIO.getImageReadersByFormatName("gif").next();

      try (ImageInputStream var3 = ImageIO.createImageInputStream(stream)) {
         var2.setInput(var3, false);
         int var4 = var2.getNumImages(true);
         ArrayList var5 = new ArrayList();
         int[] var6 = new int[var4];
         int[] var7 = new int[var4];
         int[] var8 = new int[var4];
         String[] var9 = new String[var4];
         int var10 = 0;
         int var11 = 0;

         for (int var12 = 0; var12 < var4; var12++) {
            BufferedImage var13 = var2.read(var12);
            var5.add(var13);
            int var14 = 0;
            int var15 = 0;
            int var16 = 10;
            String var17 = "none";

            try {
               IIOMetadata var18 = var2.getImageMetadata(var12);
               IIOMetadataNode var19 = (IIOMetadataNode)var18.getAsTree("javax_imageio_gif_image_1.0");
               IIOMetadataNode var20 = helper2(var19, "ImageDescriptor");
               if (var20 != null) {
                  var14 = helper3(var20, "imageLeftPosition", 0);
                  var15 = helper3(var20, "imageTopPosition", 0);
               }

               IIOMetadataNode var21 = helper2(var19, "GraphicControlExtension");
               if (var21 != null) {
                  var16 = helper3(var21, "delayTime", 10) * 10;
                  String var22 = var21.getAttribute("disposalMethod");
                  if (var22 != null && !var22.isEmpty()) {
                     var17 = var22;
                  }
               }
            } catch (Exception var29) {
            }

            if (var16 <= 0) {
               var16 = 100;
            }

            var6[var12] = var14;
            var7[var12] = var15;
            var8[var12] = var16;
            var9[var12] = var17;
            var10 = Math.max(var10, var14 + var13.getWidth());
            var11 = Math.max(var11, var15 + var13.getHeight());
         }

         var1.width = Math.max(1, var10);
         var1.height = Math.max(1, var11);
         BufferedImage var32 = new BufferedImage(var1.width, var1.height, 2);
         Graphics2D var33 = var32.createGraphics();
         BufferedImage var34 = null;

         for (int var35 = 0; var35 < var4; var35++) {
            if ("restoreToPrevious".equals(var9[var35])) {
               var34 = helper(var32);
            }

            var33.drawImage((Image)var5.get(var35), var6[var35], var7[var35], null);
            var1.frames.add(helper(var32));
            var1.delaysMs.add(var8[var35]);
            switch (var9[var35]) {
               case "restoreToBackgroundColor":
                  Composite var38 = var33.getComposite();
                  var33.setComposite(AlphaComposite.Clear);
                  var33.fillRect(var6[var35], var7[var35], ((BufferedImage)var5.get(var35)).getWidth(), ((BufferedImage)var5.get(var35)).getHeight());
                  var33.setComposite(var38);
                  break;
               case "restoreToPrevious":
                  if (var34 != null) {
                     var33.dispose();
                     var32 = var34;
                     var33 = var32.createGraphics();
                  }
            }
         }

         var33.dispose();
      } finally {
         var2.dispose();
      }

      if (var1.frames.isEmpty()) {
         throw new IllegalStateException("gif has no frames");
      } else {
         return var1;
      }
   }

   private static BufferedImage helper(BufferedImage src) {
      BufferedImage var1 = new BufferedImage(src.getWidth(), src.getHeight(), 2);
      Graphics2D var2 = var1.createGraphics();
      var2.drawImage(src, 0, 0, null);
      var2.dispose();
      return var1;
   }

   private static IIOMetadataNode helper2(IIOMetadataNode root, String name) {
      if (root == null) {
         return null;
      }

      NodeList var2 = root.getElementsByTagName(name);
      return var2.getLength() > 0 ? (IIOMetadataNode)var2.item(0) : null;
   }

   private static int helper3(IIOMetadataNode node, String attr, int def) {
      try {
         String var3 = node.getAttribute(attr);
         return var3 != null && !var3.isEmpty() ? Integer.parseInt(var3) : def;
      } catch (Exception var4) {
         return def;
      }
   }
}
