package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.client.modules.impl.player.LockSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryButtons {
   public static boolean hasContainerSlots(HandledScreen<?> screen) {
      if (screen != null && screen.getScreenHandler() != null && MinecraftClient.getInstance().player != null) {
         PlayerInventory var1 = MinecraftClient.getInstance().player.getInventory();

         for (Slot var3 : screen.getScreenHandler().slots) {
            if (var3.inventory != var1) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static void takeAll(HandledScreen<?> screen, InventoryButtons.SlotClicker clicker) {
      if (screen != null && screen.getScreenHandler() != null && MinecraftClient.getInstance().player != null) {
         PlayerInventory var2 = MinecraftClient.getInstance().player.getInventory();

         for (Slot var4 : screen.getScreenHandler().slots) {
            if (var4.inventory != var2 && var4.hasStack()) {
               clicker.click(var4, var4.id, 0, SlotActionType.QUICK_MOVE);
            }
         }
      }
   }

   public static void dropAll(HandledScreen<?> screen, InventoryButtons.SlotClicker clicker) {
      if (screen != null && screen.getScreenHandler() != null && MinecraftClient.getInstance().player != null) {
         PlayerInventory var2 = MinecraftClient.getInstance().player.getInventory();
         boolean var3 = LockSlot.INSTANCE != null && LockSlot.INSTANCE.isEnable();

         for (Slot var5 : screen.getScreenHandler().slots) {
            if (var5.inventory == var2 && var5.hasStack()) {
               int var6 = var5.getIndex();
               if (!var3 || var6 < 0 || var6 > 8 || !LockSlot.INSTANCE.isCurrentSlotLockedForDrop() || var2.selectedSlot != var6) {
                  clicker.click(var5, var5.id, 1, SlotActionType.THROW);
               }
            }
         }
      }
   }

   @FunctionalInterface
   public interface SlotClicker {
      void click(Slot var1, int var2, int var3, SlotActionType var4);
   }
}
