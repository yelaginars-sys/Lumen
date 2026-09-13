package dlc.lumen.api.utils.render;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.ui.modern.ModernTheme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public final class ItemCircleIndicator {
   private ItemCircleIndicator() {
   }

   public static void draw(DrawContext context, float cx, float cy, float radius, ItemStack icon, float progress, int ringColor) {
      MatrixStack var7 = context.getMatrices();
      int var8 = (int)(255.0F * MathHelper.clamp(255.0F, 0.0F, 1.0F));
      int var9 = ColorUtils.replAlpha(ColorUtils.clientBackground(), Math.min(var8, 78));
      int var10 = ColorUtils.rgba(255, 255, 255, Math.min(var8, 40));
      float var11 = radius * 2.0F;
      RenderUtils.drawBlur(var7, cx - radius, cy - radius, var11, var11, radius, 5.0F, ColorUtils.getThemeColor());
      RenderUtils.drawBlur(var7, cx - radius, cy - radius, var11, var11, radius, 5.0F, ColorUtils.replAlpha(ColorUtils.clientBackground(), 160));
      RenderUtils.drawHudLiquidGlass(
         var7,
         cx - radius,
         cy - radius,
         var11,
         var11,
         radius,
         radius,
         radius,
         radius,
         ColorUtils.rgba(255, 255, 255, 255),
         1.0F,
         32.0F,
         ModernTheme.accent(),
         0.88F,
         true,
         0.0F,
         0.06F,
         2.0F,
         false
      );
      RenderUtils.drawRoundedRect(var7, cx - radius, cy - radius, var11, var11, radius, var9);
      RenderUtils.drawRoundedRectOutline(var7, cx - radius, cy - radius, var11, var11, radius, 0.45F, var10, var10, var10, var10);
      float var12 = radius + 1.5F;
      float var13 = radius + 3.5F;
      handleCx(var7, cx, cy, var12, var13, -90.0F, 360.0F, ColorUtils.rgba(255, 255, 255, 40));
      float var14 = Math.max(0.0F, Math.min(1.0F, progress)) * 360.0F;
      handleCx(var7, cx, cy, var12, var13, -90.0F, var14, ringColor);
      if (icon != null && !icon.isEmpty()) {
         float var15 = radius * 1.55F;
         float var16 = var15 / 16.0F;
         var7.push();
         var7.translate(cx - var15 / 2.0F, cy - var15 / 2.0F, 0.0F);
         var7.scale(var16, var16, 1.0F);
         context.drawItem(icon, 0, 0);
         var7.pop();
      }
   }

   private static void handleCx(MatrixStack m, float cx, float cy, float rIn, float rOut, float startDeg, float sweepDeg, int color) {
      if (!(sweepDeg <= 0.0F)) {
         int var8 = Math.max(1, Math.round(sweepDeg / 6.0F));
         float var9 = (float)Math.toRadians(startDeg);
         float var10 = (float)Math.toRadians(sweepDeg) / var8;

         for (int var11 = 0; var11 < var8; var11++) {
            float var12 = var9 + var11 * var10;
            float var13 = var9 + (var11 + 1) * var10;
            float var14 = (float)Math.cos(var12);
            float var15 = (float)Math.sin(var12);
            float var16 = (float)Math.cos(var13);
            float var17 = (float)Math.sin(var13);
            float var18 = cx + rIn * var14;
            float var19 = cy + rIn * var15;
            float var20 = cx + rOut * var14;
            float var21 = cy + rOut * var15;
            float var22 = cx + rIn * var16;
            float var23 = cy + rIn * var17;
            float var24 = cx + rOut * var16;
            float var25 = cy + rOut * var17;
            RenderUtils.drawTriangle(m, var18, var19, var20, var21, var24, var25, color);
            RenderUtils.drawTriangle(m, var18, var19, var24, var25, var22, var23, color);
         }
      }
   }
}
