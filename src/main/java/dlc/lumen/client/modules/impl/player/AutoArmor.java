package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
   public static AutoArmor INSTANCE = new AutoArmor();
   private final FloatSetting floatSetting = new FloatSetting("Задержка", 25.0F, 1.0F, 1000.0F, 1.0F);
   private long timestamp = 0L;

   public AutoArmor() {
      super("AutoArmor", "Автоматически одевает броню", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting);
   }

   @EventLink
   public void onEvent(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (!this.helper()) {
            long var2 = System.currentTimeMillis();
            if (!((float)(var2 - this.timestamp) < this.floatSetting.get())) {
               for (int var4 = 0; var4 < 4; var4++) {
                  ItemStack var5 = mc.player.getInventory().getArmorStack(var4);
                  if (var5.isEmpty()) {
                     for (int var6 = 0; var6 < 36; var6++) {
                        ItemStack var7 = mc.player.getInventory().getStack(var6);
                        if (!var7.isEmpty() && var7.getItem() instanceof ArmorItem var8 && this.helper2(var8) == var4) {
                           int var10 = var6;
                           if (var6 < 9) {
                              var10 = var6 + 36;
                           }

                           mc.interactionManager.clickSlot(0, var10, 0, SlotActionType.QUICK_MOVE, mc.player);
                           this.timestamp = var2;
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean helper() {
      return mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F;
   }

   private int helper2(ArmorItem armor) {
      String var2 = armor.toString().toLowerCase();
      if (var2.contains("helmet") || var2.contains("skull")) {
         return 3;
      } else if (var2.contains("chestplate") || var2.contains("tunic")) {
         return 2;
      } else if (var2.contains("leggings") || var2.contains("pants")) {
         return 1;
      } else {
         return !var2.contains("boots") && !var2.contains("shoes") ? 0 : 0;
      }
   }
}
