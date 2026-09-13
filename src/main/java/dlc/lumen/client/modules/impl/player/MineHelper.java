package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class MineHelper extends Module {
   public static final MineHelper INSTANCE = new MineHelper();
   private final BooleanSetting savePickaxeSetting = new BooleanSetting("Save Pickaxe", true);
   private final FloatSetting durabilitySetting = new FloatSetting("Durability", 10.0F, 1.0F, 70.0F, 1.0F);
   private final BooleanSetting autoReplaceSetting = new BooleanSetting("Auto Replace", true);
   private final BooleanSetting autoRepairSetting = new BooleanSetting("Auto Repair", false);
   private final BindSetting repairBindSetting = new BindSetting("Repair Bind", -1);
   private final TimerUtils timerUtils = new TimerUtils();

   public MineHelper() {
      super("MineHelper", "Protects, swaps and repairs pickaxes", Module.ModuleCategory.PLAYER);
      this.addSettings(this.savePickaxeSetting, this.durabilitySetting, this.autoReplaceSetting, this.autoRepairSetting, this.repairBindSetting);
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null && mc.currentScreen == null) {
         if (this.repairBindSetting.getKey() != -1 && event.getKey() == this.repairBindSetting.getKey()) {
            this.helper2();
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         ItemStack var2 = mc.player.getMainHandStack();
         if (this.savePickaxeSetting.isState() && this.helper9(var2) && mc.interactionManager.isBreakingBlock()) {
            if (!(this.helper10(var2) > this.durabilitySetting.get())) {
               mc.options.attackKey.setPressed(false);
               this.helper(var2);
            }
         }
      }
   }

   private void helper(ItemStack currentStack) {
      if (this.timerUtils.finished(250L)) {
         if (this.autoReplaceSetting.isState() && this.helper4(currentStack)) {
            this.timerUtils.reset();
         } else {
            if (this.autoRepairSetting.isState() && this.helper2()) {
               this.timerUtils.reset();
            }
         }
      }
   }

   private boolean helper2() {
      ItemStack var1 = mc.player.getMainHandStack();
      if (!this.helper9(var1) || var1.getDamage() <= 0) {
         return false;
      }

      if (!this.helper3()) {
         return false;
      }

      ((IMinecraftClientAccessor)mc).setItemUseCooldown(0);
      mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, sequence, mc.player.getYaw(), 90.0F));
      mc.player.swingHand(Hand.OFF_HAND);
      return true;
   }

   private boolean helper3() {
      if (mc.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
         return true;
      }

      int var1 = this.helper6(Items.EXPERIENCE_BOTTLE);
      if (var1 == -1) {
         return false;
      }

      this.helper7(var1);
      return mc.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE);
   }

   private boolean helper4(ItemStack currentStack) {
      int var2 = this.helper5(currentStack);
      if (var2 == -1) {
         return false;
      } else if (var2 < 9) {
         this.helper8(var2);
         return true;
      } else {
         int var3 = mc.player.getInventory().selectedSlot;
         return SwapManager.swapInventorySlotHidden(var2, var3);
      }
   }

   private int helper5(ItemStack currentStack) {
      double var2 = this.helper10(currentStack);
      int var4 = -1;
      double var5 = var2;

      for (int var7 = 0; var7 < 36; var7++) {
         if (var7 != mc.player.getInventory().selectedSlot) {
            ItemStack var8 = mc.player.getInventory().getStack(var7);
            if (this.helper9(var8)) {
               double var9 = this.helper10(var8);
               if (var9 > var5) {
                  var5 = var9;
                  var4 = var7;
               }
            }
         }
      }

      return var4;
   }

   private int helper6(Item item) {
      for (int var2 = 0; var2 < 36; var2++) {
         if (mc.player.getInventory().getStack(var2).isOf(item)) {
            return var2;
         }
      }

      return -1;
   }

   private void helper7(int slot) {
      SwapManager.swapInventorySlotHidden(slot, 40);
   }

   private void helper8(int slot) {
      if (slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         if (mc.player.networkHandler != null) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
         }
      }
   }

   private int resolveInt(int slot) {
      return slot < 9 ? slot + 36 : slot;
   }

   private boolean helper9(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isDamageable() && stack.getItem() instanceof PickaxeItem;
   }

   private double helper10(ItemStack stack) {
      return (double)(stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage() * 100.0;
   }
}
