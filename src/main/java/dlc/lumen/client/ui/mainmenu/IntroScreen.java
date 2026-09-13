package dlc.lumen.client.ui.mainmenu;

import dlc.lumen.api.utils.render.RenderUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IntroScreen extends Screen {
   private static final int INDEX = 160;
   private static final int INDEX2 = 20;
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "intro_frame");
   private final Screen screen2;
   private long timestamp = -1L;
   private int index = -1;
   private NativeImageBackedTexture avatarTexture;

   public IntroScreen(Screen next) {
      super(Text.empty());
      this.screen2 = next;
   }

   @Override
   protected void init() {
      if (this.timestamp < 0L) {
         this.timestamp = System.currentTimeMillis();
      }
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      if (this.client == null) {
         this.refreshTexture2();
      } else {
         long var5 = System.currentTimeMillis() - this.timestamp;
         int var7 = (int)(var5 * 20L / 1000L);
         if (var7 >= 160) {
            this.refreshTexture2();
         } else {
            context.fill(0, 0, this.width, this.height, -16777216);
            if (this.refreshTexture(var7)) {
               float var8 = 1.7777778F;
               int var9 = this.width;
               int var10 = (int)(this.width / var8);
               if (var10 > this.height) {
                  var10 = this.height;
                  var9 = (int)(this.height * var8);
               }

               int var11 = (this.width - var9) / 2;
               int var12 = (this.height - var10) / 2;
               RenderUtils.drawTexture(context.getMatrices(), TEXTURE_ID, var11, var12, var9, var10, 0.0F, 0.0F, 1.0F, 1.0F, -1);
            }
         }
      }
   }

   private boolean refreshTexture(int frame) {
      if (frame == this.index) {
         return this.avatarTexture != null;
      }

      try (InputStream var2 = IntroScreen.class.getResourceAsStream(String.format("/assets/lumen/intro/f_%03d.jpg", frame + 1))) {
         if (var2 == null) {
            return false;
         }

         BufferedImage var3 = ImageIO.read(var2);
         if (var3 == null) {
            return false;
         }

         BufferedImage var4 = new BufferedImage(var3.getWidth(), var3.getHeight(), 2);
         var4.getGraphics().drawImage(var3, 0, 0, null);
         ByteArrayOutputStream var5 = new ByteArrayOutputStream();
         ImageIO.write(var4, "png", var5);
         NativeImage var6 = NativeImage.read(new ByteArrayInputStream(var5.toByteArray()));
         if (this.avatarTexture != null) {
            this.client.getTextureManager().destroyTexture(TEXTURE_ID);
         }

         this.avatarTexture = new NativeImageBackedTexture(var6);
         this.client.getTextureManager().registerTexture(TEXTURE_ID, this.avatarTexture);
         this.index = frame;
         return true;
      } catch (Throwable var10) {
         return false;
      }
   }

   private void refreshTexture2() {
      if (this.client != null) {
         if (this.avatarTexture != null) {
            this.client.getTextureManager().destroyTexture(TEXTURE_ID);
            this.avatarTexture = null;
         }

         this.client.setScreen(this.screen2);
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.refreshTexture2();
      return true;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      this.refreshTexture2();
      return true;
   }

   @Override
   public boolean shouldPause() {
      return false;
   }
}
