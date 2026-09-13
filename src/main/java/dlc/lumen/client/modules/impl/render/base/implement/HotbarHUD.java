package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.render.HealHelper;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import dlc.lumen.client.ui.modern.ModernTheme;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class HotbarHUD extends InterfaceProcessing {
   private static final float VOLUME = 182.0F;
   private static final float VOLUME2 = 22.0F;
   private static final float VOLUME3 = 20.0F;
   private static final float VOLUME4 = 2.0F;
   private static final float VOLUME5 = 2.0F;
   private static final int INDEX = 9;
   private final AnimationUtils[] animationUtils = new AnimationUtils[9];

   public HotbarHUD(Draggable draggable) {
      super(draggable);
      draggable.setResizable(false);
      draggable.setScaleX(1.0F);
      draggable.setScaleY(1.0F);
   }

   private AnimationUtils helper(int slot) {
      if (this.animationUtils[slot] == null) {
         this.animationUtils[slot] = new AnimationUtils(0.0F, 11.0F, Easings.QUAD_OUT);
      }

      return this.animationUtils[slot];
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      if (mc != null && mc.player != null) {
         this.draggable.setScaleX(1.0F);
         this.draggable.setScaleY(1.0F);
         float var2 = (mc.getWindow().getScaledWidth() - 182.0F) * 0.5F;
         float var3 = mc.getWindow().getScaledHeight() - 22.0F;
         if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            int var10000 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
         } else {
            ColorUtils.getThemeColor();
         }

         MatrixStack var5 = eventRender.getContext().getMatrices();
         drawHudBg(var5, var2, var3, 182.0F, 21.0F);
         int var6 = Math.max(0, Math.min(8, mc.player.getInventory().selectedSlot));
         float var7 = var2 + var6 * 20.0F;
         float var8 = var3;
         RenderUtils.drawRoundedRect(var5, var7 + 0.4F, var8, 20.0F, 20.0F, 6.0F, ColorUtils.rgba(0, 0, 0, 105));
         RenderUtils.drawRoundedRect(var5, var7 + 5.0F + 0.4F, var8 + 18.5F, 10.0F, 2.0F, 1.0F, ModernTheme.accent());

         for (int var9 = 0; var9 < 9; var9++) {
            float var10 = var2 + 2.0F + var9 * 20.0F;
            float var11 = var3 + 2.0F;
            ItemStack var12 = mc.player.getInventory().getStack(var9);
            if (!var12.isEmpty()) {
               HealHelper.INSTANCE.renderHighlight(eventRender.getContext(), var12, (int)var10, (int)var11, 16);
               eventRender.getContext().drawItem(var12, (int)var10, (int)var11);
               eventRender.getContext().drawStackOverlay(mc.textRenderer, var12, (int)var10, (int)var11);
               float var13 = mc.player.getItemCooldownManager().getCooldownProgress(var12, 0.0F);
               float var14 = animateProgress(this.helper(var9), Math.min(1.0F, var13));
               if (var14 > 0.01F) {
                  float var15 = 16.0F * var14;
                  RenderUtils.drawRect(var5, var10, var11 + (16.0F - var15), 16.0F, var15, ColorUtils.rgba(0, 0, 0, 160));
               }
            } else {
               animateProgress(this.helper(var9), 0.0F);
            }

            if (var12.isEmpty()) {
               Font var18 = Fonts.getFont("inter_medium", 12);
               if (var18 != null) {
                  String var19 = String.valueOf(var9 + 1);
                  float var20 = var18.getStringWidth(var19);
                  float var16 = var2 + var9 * 20.0F + 10.0F - var20 * 0.5F;
                  float var17 = var3 + 9.0F;
                  var18.draw(var5, var19, var16, var17, ColorUtils.replAlpha(ColorUtils.clientText(), 170));
               }
            }
         }

         this.draggable.setX(var2);
         this.draggable.setY(var3);
         this.draggable.setWidth(182.0F);
         this.draggable.setHeight(22.0F);
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }
}
