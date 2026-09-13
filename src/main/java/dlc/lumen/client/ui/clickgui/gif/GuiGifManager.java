package dlc.lumen.client.ui.clickgui.gif;

import dlc.lumen.Lumen;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public final class GuiGifManager {
   private static GuiGifManager guiGifManager;
   public static final String[] KEYS = new String[]{"ayaya", "meowbah"};
   public static final String[] LABELS = new String[]{"Ayaya", "Meowbah"};
   private String selectedKey = null;
   private GuiGif guiGif;
   private String text = null;
   private boolean flag = false;
   private GuiGif guiGif2;
   private String text2 = null;
   private static final int INDEX = 128;

   public static GuiGifManager get() {
      if (guiGifManager == null) {
         guiGifManager = new GuiGifManager();
      }

      return guiGifManager;
   }

   private GuiGifManager() {
   }

   public String getSelectedKey() {
      this.helper5();
      return this.selectedKey;
   }

   public boolean isSelected(String key) {
      this.helper5();
      return key == null ? this.selectedKey == null : key.equals(this.selectedKey);
   }

   public void select(String key) {
      this.helper5();
      this.selectedKey = key;
      this.helper6();
      this.helper3();
      this.helper2();
   }

   public void renderCircle(MatrixStack matrices, float cx, float cy, float radius, float alpha) {
      this.helper5();
      if (this.selectedKey != null) {
         if (this.guiGif2 == null || !this.selectedKey.equals(this.text2)) {
            this.helper();
         }

         if (this.guiGif2 != null) {
            float var6 = radius * 2.0F;
            this.guiGif2.render(matrices, cx - radius, cy - radius, var6, var6, alpha);
         }
      }
   }

   private void helper() {
      this.helper2();
      if (this.selectedKey != null) {
         try (InputStream var1 = GuiGifManager.class.getResourceAsStream("/assets/lumen/guigifs/" + this.selectedKey + ".gif")) {
            if (var1 != null) {
               GifDecoder var2 = GifDecoder.decode(var1);
               this.guiGif2 = GuiGif.fromCircularDecoder(var2, 128);
               this.text2 = this.selectedKey;
            }
         } catch (Throwable var6) {
            this.guiGif2 = null;
            this.text2 = null;
         }
      }
   }

   private void helper2() {
      if (this.guiGif2 != null) {
         this.guiGif2.dispose();
         this.guiGif2 = null;
      }

      this.text2 = null;
   }

   private void helper3() {
      if (this.selectedKey == null) {
         this.helper4();
      } else if (!this.selectedKey.equals(this.text) || this.guiGif == null) {
         this.helper4();

         try (InputStream var1 = GuiGifManager.class.getResourceAsStream("/assets/lumen/guigifs/" + this.selectedKey + ".gif")) {
            if (var1 != null) {
               GifDecoder var2 = GifDecoder.decode(var1);
               this.guiGif = GuiGif.fromDecoder(var2);
               this.text = this.selectedKey;
            } else {
               this.selectedKey = null;
            }
         } catch (Throwable var6) {
            this.guiGif = null;
            this.text = null;
         }
      }
   }

   private void helper4() {
      if (this.guiGif != null) {
         this.guiGif.dispose();
         this.guiGif = null;
      }

      this.text = null;
   }

   public void renderScreenBottomLeft(DrawContext context, int screenW, int screenH, float alpha) {
      this.helper5();
      if (this.selectedKey != null) {
         if (this.guiGif == null || !this.selectedKey.equals(this.text)) {
            this.helper3();
         }

         if (this.guiGif != null) {
            float var5 = 1.0F;
            float var6 = Math.min(this.guiGif.height, screenH * 0.34F);
            var6 = Math.max(var6, Math.min(this.guiGif.height, 70.0F));
            var5 = var6 / this.guiGif.height;
            float var7 = this.guiGif.width * var5;
            float var8 = screenW * 0.42F;
            if (var7 > var8) {
               var5 = var8 / this.guiGif.width;
               var7 = var8;
            }

            float var9 = this.guiGif.height * var5;
            float var10 = 0.0F;
            float var11 = screenH - var9;
            this.guiGif.render(context.getMatrices(), var10, var11, var7, var9, alpha);
         }
      }
   }

   private void helper5() {
      if (!this.flag) {
         this.flag = true;

         try {
            File var1 = this.helper7();
            if (var1 != null && var1.exists()) {
               String var2 = Files.readString(var1.toPath(), StandardCharsets.UTF_8).trim();
               if (!var2.isEmpty() && !"none".equalsIgnoreCase(var2)) {
                  for (String var6 : KEYS) {
                     if (var6.equals(var2)) {
                        this.selectedKey = var2;
                        break;
                     }
                  }
               }
            }
         } catch (Exception var7) {
         }
      }
   }

   private void helper6() {
      try {
         File var1 = this.helper7();
         if (var1 == null) {
            return;
         }

         File var2 = var1.getParentFile();
         if (var2 != null && !var2.exists()) {
            var2.mkdirs();
         }

         Files.writeString(var1.toPath(), this.selectedKey == null ? "none" : this.selectedKey, StandardCharsets.UTF_8);
      } catch (Exception var3) {
      }
   }

   private File helper7() {
      return Lumen.INSTANCE != null && Lumen.INSTANCE.configsDir != null
         ? new File(Lumen.INSTANCE.configsDir, "gui_gif.txt")
         : new File("lumen/gui_gif.txt");
   }
}
