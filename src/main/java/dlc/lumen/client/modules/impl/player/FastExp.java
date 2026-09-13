package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FastExp extends Module {
   public static FastExp INSTANCE = new FastExp();

   public FastExp() {
      super("FastExp", "Позволяет бросать пузырьки опыта без задержки", Module.ModuleCategory.PLAYER);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null) {
         ItemStack var2 = mc.player.getMainHandStack();
         if (var2.isOf(Items.EXPERIENCE_BOTTLE)) {
            ((IMinecraftClientAccessor)mc).setItemUseCooldown(0);
         }
      }
   }
}
