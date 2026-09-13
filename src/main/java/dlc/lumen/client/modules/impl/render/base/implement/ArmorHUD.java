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
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.ArrayList;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ArmorHUD extends InterfaceProcessing {
   private boolean showHelmet2 = true;
   private boolean showChestplate2 = true;
   private boolean showLeggings2 = true;
   private boolean showBoots2 = true;
   private boolean showDurability2 = true;
   private static final float VOLUME = 16.0F;
   private static final float VOLUME2 = 3.0F;
   private static final float VOLUME3 = 4.0F;
   private static final float VOLUME4 = 1.0F;
   private static final float VOLUME5 = 2.0F;
   private final AnimationUtils[] durabilityAnimations = new AnimationUtils[4];
   private final AnimationUtils[] durabilityTrailAnimations = new AnimationUtils[4];

   public ArmorHUD(Draggable draggable) {
      super(draggable);
   }

   private AnimationUtils helper(int slotIndex) {
      if (this.durabilityAnimations[slotIndex] == null) {
         this.durabilityAnimations[slotIndex] = new AnimationUtils(0.0F, 10.0F, Easings.QUAD_OUT);
      }

      return this.durabilityAnimations[slotIndex];
   }

   private AnimationUtils helper2(int slotIndex) {
      if (this.durabilityTrailAnimations[slotIndex] == null) {
         this.durabilityTrailAnimations[slotIndex] = new AnimationUtils(0.0F, 7.0F, Easings.QUAD_OUT);
      }

      return this.durabilityTrailAnimations[slotIndex];
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      this.DefaultStyle(eventRender);
      super.onRender(eventRender);
   }

   private void updateState(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
      float var7 = 4.0F;
      float var8 = 1.5F;
      float var9 = var7 * 2.0F + var8;
      int var10 = themeColor >> 24 & 0xFF;
      if (var10 == 0) {
         var10 = 255;
      }

      int var11 = var10 << 24 | themeColor & 16777215;
      RenderUtils.drawRoundedRectOutline(matrices, x - var8 / 2.0F, y - var8 / 2.0F, var9, var9, var7, 0.0F, 0.0F, var7, var8, var11, var11, var11, var11);
      RenderUtils.drawRoundedRectOutline(
         matrices, x + width - var9 + var8 / 2.0F, y - var8 / 2.0F, var9, var9, 0.0F, var7, var7, 0.0F, var8, var11, var11, var11, var11
      );
      RenderUtils.drawRoundedRectOutline(
         matrices, x - var8 / 2.0F, y + height - var9 + var8 / 2.0F, var9, var9, 0.0F, var7, var7, 0.0F, var8, var11, var11, var11, var11
      );
      RenderUtils.drawRoundedRectOutline(
         matrices, x + width - var9 + var8 / 2.0F, y + height - var9 + var8 / 2.0F, var9, var9, var7, 0.0F, 0.0F, var7, var8, var11, var11, var11, var11
      );
   }

   public void DefaultStyle(EventRender.Default eventRender) {
      if (mc != null && mc.player != null) {
         MatrixStack var2 = eventRender.getContext().getMatrices();
         float var3 = this.draggable.getX();
         float var4 = this.draggable.getY();
         if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            int var5 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
         } else {
            int var24 = ColorUtils.getThemeColor();
         }

          boolean var6 = this.isUnusualRectType();
          ItemStack[] var7 = this.helper4();
          boolean hasArmor = false;

          for (ItemStack armor : var7) {
             if (!armor.isEmpty()) {
                hasArmor = true;
                break;
             }
          }

          if (var7.length != 0 && (hasArmor || mc.currentScreen instanceof ChatScreen)) {
            int var8 = var7.length;
            float var9 = 21.0F;
            float var10 = 3.0F;
            float var11 = var10 + 4.0F + var8 * 16.0F + (var8 - 1) * 3.0F + var10 + 4.0F;
            float var12 = var4 + 2.0F;
            drawHudBg(var2, var3, var4, var11, var9);
            float var13 = var3 + var10 + 4.0F;
            int var14 = 0;

            for (ItemStack var18 : var7) {
               if (var18.isEmpty()) {
                  String var19 = this.helper3(var14);
                  Font var20 = Fonts.getFont("inter_medium", 12);
                  if (var20 != null && var19 != null) {
                     float var21 = var20.getStringWidth(var19);
                     float var22 = var13 + 10.0F - var21 * 0.5F;
                     float var23 = var4 + 9.0F;
                     var20.draw(var2, var19, var22 - 3.0F, var23, ColorUtils.replAlpha(ColorUtils.clientText(), 170));
                  }
               } else {
                  this.helper5(eventRender, var2, var18, var13, var12, 16.0F, var14);
               }

               var13 += 19.0F;
               var14++;
            }

             this.draggable.setWidth(var11);
             this.draggable.setHeight(var9);
          } else {
             this.draggable.setWidth(0.0F);
             this.draggable.setHeight(0.0F);
          }
       }
    }

   private String helper3(int index) {
      if (index == 0 && this.showHelmet2) {
         return "1";
      } else if (index == 1 && this.showChestplate2) {
         return "2";
      } else if (index == 2 && this.showLeggings2) {
         return "3";
      } else {
         return index == 3 && this.showBoots2 ? "4" : null;
      }
   }

   private ItemStack[] helper4() {
      ArrayList<ItemStack> var1 = new ArrayList<>();
      if (this.showHelmet2) {
         var1.add(mc.player.getInventory().getArmorStack(3));
      }

      if (this.showChestplate2) {
         var1.add(mc.player.getInventory().getArmorStack(2));
      }

      if (this.showLeggings2) {
         var1.add(mc.player.getInventory().getArmorStack(1));
      }

      if (this.showBoots2) {
         var1.add(mc.player.getInventory().getArmorStack(0));
      }

      return var1.toArray(new ItemStack[0]);
   }

   private void helper5(EventRender.Default eventRender, MatrixStack matrices, ItemStack armor, float x, float y, float size, int slotIndex) {
      matrices.push();
      matrices.translate(x, y, 0.0F);
      eventRender.getContext().drawItem(armor, 0, 0);
      matrices.pop();
      if (this.showDurability2) {
         float var8 = y + size + 2.0F - 2.0F;
         this.helper6(matrices, armor, x, var8, size, 1.0F, slotIndex);
      }
   }

   private void helper6(MatrixStack matrices, ItemStack armor, float x, float y, float width, float height, int slotIndex) {
      int var8 = armor.getMaxDamage();
      if (var8 > 0) {
         int var9 = var8 - armor.getDamage();
         float var10 = (float)var9 / var8;
         float var11 = animateProgress(this.helper(slotIndex), var10);
         float var12 = animateTrailingProgress(this.helper2(slotIndex), var11, 0.72F);
         int var13 = this.helper7(var10);
         RenderUtils.drawRoundedRect(matrices, x + 1.0F, y - 4.0F, width - 2.0F, height + 1.0F, 0.0F, ColorUtils.rgba(0, 0, 0, 120));
         if (var12 > 0.02F) {
            RenderUtils.drawRoundedRect(matrices, x + 1.0F, y - 4.0F, (width - 2.0F) * var12, height + 1.0F, 0.0F, ColorUtils.rgba(255, 255, 255, 42));
         }

         if (var11 > 0.02F) {
            RenderUtils.drawRoundedRect(matrices, x + 1.0F, y - 4.0F, (width - 2.0F) * var11, height + 1.0F, 0.0F, var13);
         }
      }
   }

   private int helper7(float percent) {
      if (percent > 0.6F) {
         return ColorUtils.rgba(0, 255, 100, 255);
      } else if (percent > 0.3F) {
         return ColorUtils.rgba(255, 200, 0, 255);
      } else {
         return percent > 0.1F ? ColorUtils.rgba(255, 100, 0, 255) : ColorUtils.rgba(255, 50, 50, 255);
      }
   }

   public void WaveStyle(EventRender.Default eventRender) {
      if (mc != null && mc.player != null) {
         float var2 = this.draggable.getX();
         float var3 = this.draggable.getY();
         MatrixStack var4 = eventRender.getContext().getMatrices();
         Font var5 = Fonts.getFont("wave", 30);
         if (var5 == null) {
            var5 = Fonts.getFont("inter_medium", 18);
         }

         if (var5 == null) {
            var5 = Fonts.getFont("sf_regular", 18);
         }

         if (var5 != null) {
            int var6 = 0;
            int var7 = 0;
            int var8 = 0;

            for (ItemStack var10 : mc.player.getInventory().armor) {
               if (!var10.isEmpty()) {
                  var6++;
                  var7 += var10.getMaxDamage() - var10.getDamage();
                  var8 += var10.getMaxDamage();
               }
            }

            String var15;
            if (var8 > 0) {
               float var16 = (float)var7 / var8 * 100.0F;
               var15 = String.format("Armor: %d/4 (%.0f%%)", var6, var16);
            } else {
               var15 = "Armor: " + var6 + "/4";
            }

            int var17 = ColorUtils.getThemeColor(90);
            int var11 = ColorUtils.getThemeColor(180);
            int var12 = ColorUtils.getThemeColor(270);
            int var13 = ColorUtils.getColor(360);
            float var14 = 95.0F + var5.getStringWidth("Armor:");
            RenderUtils.drawShadow(var4, var2, var3, var14, 12.0F, 10.0F, 15.0F, var13, var11, var17, var12);
            var5.drawGradientStringHorizontal(var4, var15, var2, var3, var17, var11);
            this.draggable.setWidth(Math.max(var14, var5.getStringWidth(var15)));
            this.draggable.setHeight(12.0F);
         }
      }
   }

   @Generated
   public void setShowHelmet(boolean showHelmet) {
      this.showHelmet2 = showHelmet;
   }

   @Generated
   public void setShowChestplate(boolean showChestplate) {
      this.showChestplate2 = showChestplate;
   }

   @Generated
   public void setShowLeggings(boolean showLeggings) {
      this.showLeggings2 = showLeggings;
   }

   @Generated
   public void setShowBoots(boolean showBoots) {
      this.showBoots2 = showBoots;
   }

   @Generated
   public void setShowDurability(boolean showDurability) {
      this.showDurability2 = showDurability;
   }

   @Generated
   public boolean isShowHelmet() {
      return this.showHelmet2;
   }

   @Generated
   public boolean isShowChestplate() {
      return this.showChestplate2;
   }

   @Generated
   public boolean isShowLeggings() {
      return this.showLeggings2;
   }

   @Generated
   public boolean isShowBoots() {
      return this.showBoots2;
   }

   @Generated
   public boolean isShowDurability() {
      return this.showDurability2;
   }

   @Generated
   public AnimationUtils[] getDurabilityAnimations() {
      return this.durabilityAnimations;
   }

   @Generated
   public AnimationUtils[] getDurabilityTrailAnimations() {
      return this.durabilityTrailAnimations;
   }
}
