package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

public class ShulkerPreview extends Module {
   public static ShulkerPreview INSTANCE = new ShulkerPreview();
   private static final int INDEX = 9;
   private static final int INDEX2 = 3;
   private static final float VOLUME = 18.0F;
   private static final float VOLUME2 = 2.0F;
   private static final float VOLUME3 = 6.0F;
   private static final float VOLUME4 = 12.0F;
   private static final float VOLUME5 = 0.82F;

   public ShulkerPreview() {
      super("ShulkerPreview", "Показывает содержимое шалкера при наведении", Module.ModuleCategory.RENDER);
   }

   public static boolean isShulker(ItemStack stack) {
      return stack.getItem() instanceof BlockItem var1 && var1.getBlock() instanceof ShulkerBoxBlock;
   }

   public boolean render(DrawContext context, ItemStack stack, int mouseX, int mouseY) {
      if (this.isEnable() && stack != null && isShulker(stack)) {
         MinecraftClient var5 = MinecraftClient.getInstance();
         ContainerComponent var6 = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
         DefaultedList var7 = DefaultedList.ofSize(27, ItemStack.EMPTY);
         var6.copyTo(var7);
         float var8 = 190.0F;
         float var9 = 82.0F;
         float var10 = var8 * 0.82F;
         float var11 = var9 * 0.82F;
         float var12 = context.getScaledWindowWidth();
         float var13 = context.getScaledWindowHeight();
         float var14 = mouseX + 10.0F;
         if (var14 + var10 > var12 - 2.0F) {
            var14 = mouseX - var10 - 10.0F;
         }

         var14 = MathHelper.clamp(var14, 2.0F, Math.max(2.0F, var12 - var10 - 2.0F));
         float var15 = mouseY - var11 - 6.0F;
         if (var15 < 2.0F) {
            var15 = mouseY + 12.0F;
         }

         var15 = MathHelper.clamp(var15, 2.0F, Math.max(2.0F, var13 - var11 - 2.0F));
         MatrixStack var16 = context.getMatrices();
         var16.push();
         var16.translate(var14, var15, 0.0F);
         var16.scale(0.82F, 0.82F, 1.0F);
         InterfaceProcessing.drawHudBg(var16, 0.0F, 0.0F, var8, var9);
         Font var17 = Fonts.getFont("inter_medium", 11);
         if (var17 != null) {
            var17.draw(var16, stack.getName().getString(), 6.0F, 8.0F, -1);
         }

         float var18 = 18.0F;

         for (int var19 = 0; var19 < 3; var19++) {
            for (int var20 = 0; var20 < 9; var20++) {
               float var21 = 6.0F + var20 * 20.0F;
               float var22 = var18 + var19 * 20.0F;
               RenderUtils.drawRoundedRect(var16, var21, var22, 18.0F, 18.0F, 3.0F, ColorUtils.rgba(12, 12, 16, 140));
               ItemStack var23 = (ItemStack)var7.get(var19 * 9 + var20);
               if (!var23.isEmpty()) {
                  context.drawItem(var23, (int)(var21 + 1.0F), (int)(var22 + 1.0F));
                  context.drawStackOverlay(var5.textRenderer, var23, (int)(var21 + 1.0F), (int)(var22 + 1.0F));
               }
            }
         }

         var16.pop();
         return true;
      } else {
         return false;
      }
   }
}
