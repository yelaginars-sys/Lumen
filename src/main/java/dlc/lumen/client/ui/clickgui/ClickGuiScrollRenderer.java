package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.Module;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ClickGuiScrollRenderer {
   private static final int INDEX = 4;
   private static final float VOLUME = 12.0F;
   private static final float VOLUME2 = 0.12F;
   private final ClickGuiState state2;

   public ClickGuiScrollRenderer(ClickGuiState state) {
      this.state2 = state;
   }

   public void renderModulesWithScroll(
      MatrixStack matrices,
      List<Module> modules,
      float moduleX,
      float moduleY,
      float moduleWidth,
      float visibleHeight,
      int colorTheme,
      float alphaMul,
      int mouseX,
      int mouseY
   ) {
      this.state2.updateScrollAnimation();
      float var11 = this.state2.getAnimatedScrollValue();
      float var12 = this.state2.getLastScrollDelta();
      float var13 = MathHelper.clamp(Math.abs(var12) * 0.12F, 0.0F, 0.6F);
      float var14 = MathHelper.clamp(var12 * 0.25F, -12.0F, 12.0F);
      if (var13 > 0.05F && Math.abs(var12) > 0.5F) {
         int var15 = Math.min(4, (int)(var13 * 8.0F) + 1);

         for (int var16 = var15; var16 >= 1; var16--) {
            float var17 = (float)var16 / (var15 + 1);
            float var18 = var13 * (1.0F - var17) * 0.4F;
            float var19 = var14 * var17 * (var12 > 0.0F ? 1 : -1);
            matrices.push();
            matrices.translate(0.0F, -var19, 0.0F);
            this.helper(matrices, modules, moduleX, moduleY, moduleWidth, visibleHeight, var11, colorTheme, alphaMul * (1.0F - var18), mouseX, mouseY);
            matrices.pop();
         }
      }

      this.helper(matrices, modules, moduleX, moduleY, moduleWidth, visibleHeight, var11, colorTheme, alphaMul, mouseX, mouseY);
   }

   private void helper(
      MatrixStack matrices,
      List<Module> modules,
      float moduleX,
      float moduleY,
      float moduleWidth,
      float visibleHeight,
      float scrollOffset,
      int colorTheme,
      float alphaMul,
      int mouseX,
      int mouseY
   ) {
      float var12 = -scrollOffset;
      float var13 = 34.0F;
      float var14 = 41.0F;

      for (Module var16 : modules) {
         float var17 = moduleY + var12;
         if (var17 + var13 >= moduleY - var13 && var17 <= moduleY + visibleHeight + var13) {
            float var18 = 1.0F;
            float var19 = var17 - moduleY;
            float var20 = moduleY + visibleHeight - (var17 + var13);
            if (var19 < 20.0F) {
               var18 = MathHelper.clamp(var19 / 20.0F, 0.3F, 1.0F);
            }

            if (var20 < 20.0F) {
               var18 = Math.min(var18, MathHelper.clamp(var20 / 20.0F, 0.3F, 1.0F));
            }

            RenderUtils.drawRoundedRect(
               matrices, moduleX, var17, moduleWidth, var13, 7.0F, ColorUtils.applyAlpha(ColorUtils.rgba(30, 30, 35, 77), alphaMul * var18)
            );
            String var21 = "";
            if (this.state2.getBindingModule() == var16) {
               var21 = " [...]";
            } else if (var16.getKey() != -1) {
               var21 = " [" + this.state2.toEnglish(KeyBoardUtils.getBindName(var16.getKey())) + "]";
            }

            Font var22 = Fonts.getFont("suisse", 12);
            boolean var23 = HoveringUtils.isHovered(mouseX, mouseY, moduleX, var17, moduleWidth, var13);
            float var24 = var23 ? 1.0F : 0.0F;
            int var25 = ColorUtils.applyAlpha(var16.isEnable() ? -1 : ColorUtils.rgba(255, 255, 255, 191), (0.75F + 0.25F * var24) * alphaMul);
            var22.draw(matrices, var16.getName(), moduleX + 8.0F, var17 + 10.0F, var25);
            if (!var21.isEmpty()) {
               float var26 = var22.getWidth(var16.getName());
               Font var27 = Fonts.getFont("suisse", 10);
               var27.draw(matrices, var21, moduleX + 8.0F + var26, var17 + 12.0F - 2.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 128), alphaMul));
            }

            String var31 = var16.getDisplayDescription();
            if (var31 != null && !var31.isBlank() && !"NULLABLE".equalsIgnoreCase(var31)) {
               Font var32 = Fonts.getFont("suisse", 11);
               var32.draw(matrices, var31, moduleX + 8.0F, var17 + 22.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 128), 0.5F * alphaMul));
            }

            float var33 = moduleX + moduleWidth - 30.0F;
            float var28 = var17 + 13.0F;
            int var29 = ColorUtils.applyAlpha(var16.isEnable() ? colorTheme : ColorUtils.rgba(50, 50, 55, 200), alphaMul);
            RenderUtils.drawRoundedRect(matrices, var33, var28, 17.0F, 8.0F, 3.0F, var29);
            float var30 = var33 + 1.0F + 6.0F * (var16.isEnable() ? 1 : 0);
            RenderUtils.drawRoundedRect(matrices, var30, var28 + 1.0F, 9.0F, 6.0F, 2.0F, ColorUtils.applyAlpha(-1, alphaMul));
         }

         var12 += var14;
      }
   }
}
