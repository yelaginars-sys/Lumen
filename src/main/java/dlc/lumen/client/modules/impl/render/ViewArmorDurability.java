package dlc.lumen.client.modules.impl.render;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.item.ItemStack;

public class ViewArmorDurability extends Module {
   public static final ViewArmorDurability INSTANCE = new ViewArmorDurability();
   private final BooleanSetting booleanSetting = new BooleanSetting("На себе", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("На других", true);
   private static boolean renderingSelf;

   private ViewArmorDurability() {
      super("ViewArmorDurability", "Показывает прочность брони", Module.ModuleCategory.RENDER);
      this.addSettings(this.booleanSetting, this.booleanSetting2);
   }

   public static void setRenderingSelf(boolean value) {
      renderingSelf = value;
   }

   public boolean shouldTint(ItemStack stack) {
      if (this.isEnable() && stack != null && !stack.isEmpty() && stack.isDamageable()) {
         return renderingSelf ? this.booleanSetting.isState() : this.booleanSetting2.isState();
      } else {
         return false;
      }
   }

   public int barColor(ItemStack stack) {
      return 0xFF000000 | stack.getItemBarColor();
   }
}
