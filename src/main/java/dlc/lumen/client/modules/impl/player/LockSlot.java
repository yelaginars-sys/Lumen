package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.mixin.SlotAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class LockSlot extends Module {
   public static LockSlot INSTANCE = new LockSlot();
   private final ListSetting listSetting = new ListSetting(
      "Слоты",
      new BooleanSetting("1", false),
      new BooleanSetting("2", false),
      new BooleanSetting("3", false),
      new BooleanSetting("4", false),
      new BooleanSetting("5", false),
      new BooleanSetting("6", false),
      new BooleanSetting("7", false),
      new BooleanSetting("8", false),
      new BooleanSetting("9", false)
   );

   public LockSlot() {
      super("LockSlot", "Блокирует выброс предметов из выбранных слотов", Module.ModuleCategory.PLAYER);
      this.addSettings(this.listSetting);
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (mc.player != null && event.getType() == EventPacket.Type.SEND) {
         if (!(mc.currentScreen instanceof HandledScreen)) {
            if (event.getPacket() instanceof PlayerActionC2SPacket var4) {
               if (var4.getAction() == Action.DROP_ITEM || var4.getAction() == Action.DROP_ALL_ITEMS) {
                  if (this.isCurrentSlotLockedForDrop()) {
                     event.cancel();
                     this.helper3(mc.player.getInventory().selectedSlot);
                  }
               }
            } else {
               if (event.getPacket() instanceof ClickSlotC2SPacket var2 && var2.getActionType() == SlotActionType.THROW) {
                  int var6 = this.helper2(var2.getSlot());
                  if (var6 >= 0 && this.helper(var6)) {
                     event.cancel();
                     this.helper3(var6);
                  }
               }
            }
         }
      }
   }

   public boolean isCurrentSlotLockedForDrop() {
      if (!this.isEnable() || mc.player == null || mc.player.getMainHandStack().isEmpty()) {
         return false;
      } else {
         return mc.currentScreen instanceof HandledScreen ? false : this.helper(mc.player.getInventory().selectedSlot);
      }
   }

   private boolean helper(int slot) {
      return slot >= 0 && slot < this.listSetting.getSettings().size() ? this.listSetting.getSettings().get(slot).isState() : false;
   }

   private int helper2(int slotId) {
      if (mc.player != null && slotId >= 0 && slotId < mc.player.currentScreenHandler.slots.size()) {
         Slot var2 = mc.player.currentScreenHandler.getSlot(slotId);
         SlotAccessor var3 = (SlotAccessor)var2;
         int var4 = var3.lumen$getIndex();
         return var3.lumen$getInventory() == mc.player.getInventory() && var4 >= 0 && var4 <= 8 ? var4 : -1;
      } else {
         return -1;
      }
   }

   private void helper3(int slot) {
      ChatUtils.sendMessage("Выброс предмета из слота " + (slot + 1) + " заблокирован");
   }
}
