package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.item.NbtDumpManager;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

public class HeldItemsHUD extends InterfaceProcessing {
   private static final float VOLUME = 5.0F;
   private static final float VOLUME2 = 16.0F;

   public HeldItemsHUD(Draggable draggable) {
      super(draggable);
   }

   private Font helper(int size) {
      return Fonts.getFont("inter_medium", size);
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      if (mc != null && mc.player != null) {
         ItemStack var2 = mc.player.getMainHandStack();
         ItemStack var3 = mc.player.getOffHandStack();
         if (var2.isEmpty() && var3.isEmpty()) {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         } else {
            Font var4 = this.helper(12);
            String var5 = null;
            if (!var2.isEmpty()) {
               String var6 = NbtDumpManager.getCustomDumpedName(var2);
               var5 = var6 != null ? var6 : var2.getName().getString();
            }

            String var15 = null;
            if (!var3.isEmpty()) {
               String var7 = NbtDumpManager.getCustomDumpedName(var3);
               var15 = var7 != null ? var7 : var3.getName().getString();
            }

            float var16 = 60.0F;
            if (var5 != null) {
               var16 = Math.max(var16, var4.getWidth(Formatting.strip(var5)));
            }

            if (var15 != null) {
               var16 = Math.max(var16, var4.getWidth(Formatting.strip(var15)));
            }

            float var8 = 32.0F + var16;
            int var9 = (!var2.isEmpty() ? 1 : 0) + (!var3.isEmpty() ? 1 : 0);
            float var10 = 10.0F + var9 * 18.0F - (var9 > 1 ? 2.0F : 0.0F);
            float var11 = this.draggable.getX();
            float var12 = this.draggable.getY();
            MatrixStack var13 = eventRender.getContext().getMatrices();
            drawHudBg(var13, var11, var12, var8, var10);
            float var14 = var12 + 5.0F;
            if (!var2.isEmpty()) {
               eventRender.getContext().drawItem(var2, (int)(var11 + 5.0F), (int)var14 + 2);
               eventRender.getContext().drawStackOverlay(mc.textRenderer, var2, (int)(var11 + 5.0F), (int)var14);
               if (var4 != null) {
                  var4.draw(var13, var5, var11 + 5.0F + 16.0F + 4.0F, var14 + 8.0F, ColorUtils.clientText());
               }

               var14 += 18.0F;
            }

            if (!var3.isEmpty()) {
               eventRender.getContext().drawItem(var3, (int)(var11 + 5.0F), (int)var14 + 2);
               eventRender.getContext().drawStackOverlay(mc.textRenderer, var3, (int)(var11 + 5.0F), (int)var14);
               if (var4 != null) {
                  var4.draw(var13, var15, var11 + 5.0F + 16.0F + 4.0F, var14 + 8.0F, ColorUtils.clientText());
               }
            }

            this.draggable.setWidth(var8);
            this.draggable.setHeight(var10);
            super.onRender(eventRender);
         }
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }
}
