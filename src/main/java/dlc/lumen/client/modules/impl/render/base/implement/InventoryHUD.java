package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class InventoryHUD extends InterfaceProcessing {
   private static final float VOLUME = 12.0F;
   private static final float VOLUME2 = 1.5F;
   private static final float VOLUME3 = 0.75F;
   private static final float VOLUME4 = 4.0F;
   private static final int INDEX = 9;
   private static final int INDEX2 = 3;
   private static final float VOLUME5 = 0.7F;

   public InventoryHUD(Draggable draggable) {
      super(draggable);
   }

    @Override
    public void onRender(EventRender.Default eventRender) {
       if (mc != null && mc.player != null) {
          boolean hasItems = false;

          for (int slot = 9; slot < 36; slot++) {
             if (!mc.player.getInventory().getStack(slot).isEmpty()) {
                hasItems = true;
                break;
             }
          }

          if (!hasItems && !(mc.currentScreen instanceof ChatScreen)) {
             this.draggable.setWidth(0.0F);
             this.draggable.setHeight(0.0F);
             return;
          }

          float var2 = this.draggable.getX();
         float var3 = this.draggable.getY();
         float var4 = 128.0F;
         float var5 = 47.0F;
         if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            int var10000 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
         } else {
            ColorUtils.getThemeColor();
         }

         MatrixStack var7 = eventRender.getContext().getMatrices();
         drawHudBg(var7, var2, var3, var4, var5);

         for (int var8 = 0; var8 < 3; var8++) {
            for (int var9 = 0; var9 < 9; var9++) {
               float var10 = var2 + 4.0F + var9 * 13.5F;
               float var11 = var3 + 4.0F + var8 * 13.5F;
               int var12 = 9 + var8 * 9 + var9;
               ItemStack var13 = mc.player.getInventory().getStack(var12);
               if (!var13.isEmpty()) {
                  var7.push();
                  float var14 = var10 + 6.0F;
                  float var15 = var11 + 6.0F;
                  var7.translate(var14, var15, 0.0F);
                  var7.scale(0.7F, 0.7F, 1.0F);
                  var7.translate(-8.0F, -8.0F, 0.0F);
                  eventRender.getContext().drawItem(var13, 0, 0);
                  eventRender.getContext().drawStackOverlay(mc.textRenderer, var13, 0, 0);
                  var7.pop();
               }
            }
         }

         float var16 = var2 + 4.0F;
         float var17 = var3 + 4.0F;
         float var18 = 120.0F;
         float var19 = 39.0F;
         int var20 = ColorUtils.rgba(255, 255, 255, 35);

         for (int var21 = 0; var21 < 8; var21++) {
            float var23 = var16 + (var21 + 1) * 13.5F - 0.375F;
            RenderUtils.drawRoundedRect(var7, var23, var17, 0.75F, var19, 0.0F, var20);
         }

         for (int var22 = 0; var22 < 2; var22++) {
            float var24 = var17 + (var22 + 1) * 13.5F - 0.375F;
            RenderUtils.drawRoundedRect(var7, var16, var24, var18, 0.75F, 0.0F, var20);
         }

         this.draggable.setWidth(var4);
         this.draggable.setHeight(var5);
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }
}
