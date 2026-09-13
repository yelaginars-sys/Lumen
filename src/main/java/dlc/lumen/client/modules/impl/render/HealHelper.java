package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HealHelper extends Module {
   public static final HealHelper INSTANCE = new HealHelper();

   private HealHelper() {
      super("HealHelper", "Подсветка лечащих предметов", Module.ModuleCategory.RENDER);
   }

   public boolean isHealItem(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         Item var2 = stack.getItem();
         if (var2 != Items.GOLDEN_CARROT && var2 != Items.GOLDEN_APPLE && var2 != Items.ENCHANTED_GOLDEN_APPLE) {
            if (var2 == Items.POTION || var2 == Items.SPLASH_POTION || var2 == Items.LINGERING_POTION) {
               PotionContentsComponent var3 = stack.get(DataComponentTypes.POTION_CONTENTS);
               if (var3 != null) {
                  for (StatusEffectInstance var5 : var3.getEffects()) {
                     if (var5.getEffectType().equals(StatusEffects.INSTANT_HEALTH)) {
                        return true;
                     }
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private float helper() {
      double var1 = System.currentTimeMillis() % 1000L / 1000.0;
      return 0.5F + 0.5F * (float)Math.sin(var1 * Math.PI * 2.0);
   }

   private int resolveInt() {
      int var1 = (int)(45.0F + 55.0F * this.helper());
      return ColorUtils.rgba(60, 220, 90, var1);
   }

   private int resolveInt2() {
      int var1 = (int)(120.0F + 135.0F * this.helper());
      return ColorUtils.rgba(70, 255, 110, var1);
   }

   public void renderHighlight(DrawContext context, ItemStack stack, int x, int y, int size) {
      if (this.isEnable() && this.isHealItem(stack)) {
         context.fill(x, y, x + size, y + size, this.resolveInt());
         context.drawBorder(x, y, size, size, this.resolveInt2());
      }
   }
}
