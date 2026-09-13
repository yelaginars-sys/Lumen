package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.movement.Sprint;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class AutoEat extends Module {
   public static final AutoEat INSTANCE = new AutoEat();
   private static final String BARITONE_APICLASS_NAME = "baritone.api.BaritoneAPI";
   private final FloatSetting floatSetting = new FloatSetting("Плашки голода", 6.0F, 1.0F, 10.0F, 1.0F);
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private int index = -1;
   private int index2 = -1;

   public AutoEat() {
      super("AutoEat", "Автоматически ест при низком голоде", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting);
   }

   public static boolean shouldSuppressCombat() {
      return INSTANCE != null && INSTANCE.isEnable() && INSTANCE.flag;
   }

   @Override
   public void onDisable() {
      this.helper11();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player == null || mc.world == null || mc.interactionManager == null) {
         this.helper11();
      } else if (mc.currentScreen != null) {
         this.helper11();
      } else if (!mc.player.getAbilities().creativeMode && !mc.player.isSpectator()) {
         if (!this.flag) {
            if (!this.helper2()) {
               return;
            }

            this.flag = true;
            this.index = mc.player.getInventory().selectedSlot;
         }

         this.helper();
      } else {
         this.helper11();
      }
   }

   private void helper() {
      ClientPlayerEntity var1 = mc.player;
      if (var1 == null) {
         this.helper11();
      } else {
         this.helper14();
         if (!this.flag2) {
            Sprint.pushPause(0L);
            this.flag2 = true;
         }

         mc.options.attackKey.setPressed(false);
         if (!this.helper3()) {
            if (!var1.isUsingItem()) {
               this.helper11();
            }
         } else if (!this.helper5()) {
            this.helper11();
         } else {
            Hand var2 = this.helper6(var1);
            if (var2 == null) {
               this.helper11();
            } else {
               mc.options.useKey.setPressed(true);
               if (!var1.isUsingItem() || var1.getActiveHand() != var2) {
                  mc.interactionManager.interactItem(var1, var2);
               }
            }
         }
      }
   }

   private boolean helper2() {
      return this.helper3() && !mc.player.isUsingItem() && (this.helper8(mc.player.getOffHandStack()) || this.helper7() != -1);
   }

   private boolean helper3() {
      return mc.player != null && mc.player.getHungerManager().getFoodLevel() < 20 && mc.player.getHungerManager().getFoodLevel() <= this.helper4();
   }

   private int helper4() {
      return Math.round(this.floatSetting.get()) * 2;
   }

   private boolean helper5() {
      ClientPlayerEntity var1 = mc.player;
      if (var1 == null) {
         return false;
      } else if (this.helper8(var1.getOffHandStack())) {
         return true;
      } else if (this.helper8(var1.getMainHandStack())) {
         return true;
      } else {
         int var2 = this.helper7();
         if (var2 == -1) {
            return false;
         } else if (var2 < 9) {
            this.flag3 = false;
            this.index2 = -1;
            this.helper9(var2);
            return this.helper8(var1.getMainHandStack());
         } else {
            this.helper9(this.index == -1 ? var1.getInventory().selectedSlot : this.index);
            this.helper10(var2, var1.getInventory().selectedSlot);
            this.flag3 = true;
            this.index2 = var2;
            return this.helper8(var1.getMainHandStack());
         }
      }
   }

   private Hand helper6(ClientPlayerEntity player) {
      if (player == null) {
         return null;
      } else if (this.helper8(player.getOffHandStack())) {
         return Hand.OFF_HAND;
      } else {
         return this.helper8(player.getMainHandStack()) ? Hand.MAIN_HAND : null;
      }
   }

   private int helper7() {
      ClientPlayerEntity var1 = mc.player;
      if (var1 == null) {
         return -1;
      }

      int var2 = var1.getInventory().selectedSlot;
      if (this.helper8(var1.getInventory().getStack(var2))) {
         return var2;
      }

      for (int var3 = 0; var3 < 9; var3++) {
         if (var3 != var2 && this.helper8(var1.getInventory().getStack(var3))) {
            return var3;
         }
      }

      for (int var4 = 9; var4 < 36; var4++) {
         if (this.helper8(var1.getInventory().getStack(var4))) {
            return var4;
         }
      }

      return -1;
   }

   private boolean helper8(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      } else {
         return !stack.isOf(Items.GOLDEN_APPLE) && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE) && !stack.isOf(Items.CHORUS_FRUIT)
            ? stack.getUseAction() == UseAction.EAT
            : false;
      }
   }

   private void helper9(int slot) {
      if (mc.player != null && slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
         }
      }
   }

   private void helper10(int inventorySlot, int hotbarSlot) {
      if (mc.player != null && mc.interactionManager != null && inventorySlot >= 9 && inventorySlot <= 35 && hotbarSlot >= 0 && hotbarSlot <= 8) {
         SwapManager.swapInventorySlotHidden(inventorySlot, hotbarSlot);
      }
   }

   private void helper11() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }

      if (this.flag2) {
         Sprint.popPause();
         this.flag2 = false;
      }

      this.helper12();
      this.flag = false;
   }

   private void helper12() {
      if (mc.player != null && mc.interactionManager != null) {
         if (this.flag3 && this.index2 != -1) {
            int var1 = this.index == -1 ? mc.player.getInventory().selectedSlot : this.index;
            this.helper9(var1);
            this.helper10(this.index2, var1);
         }

         if (this.index != -1) {
            this.helper9(this.index);
         }

         this.helper13();
      } else {
         this.helper13();
      }
   }

   private void helper13() {
      this.flag3 = false;
      this.index2 = -1;
      this.index = -1;
   }

   private void helper14() {
      try {
         Object var1 = helper16();
         if (var1 == null) {
            this.helper15();
            return;
         }

         Object var2 = helper17(var1, "getPathingBehavior");
         if (var2 == null || !Boolean.TRUE.equals(helper17(var2, "hasPath"))) {
            this.helper15();
            return;
         }

         Object var3 = helper17(var1, "getInputOverrideHandler");
         if (var3 != null) {
            var3.getClass().getMethod("clearAllKeys").invoke(var3);
            Object var4 = var3.getClass().getMethod("getBlockBreakHelper").invoke(var3);
            if (var4 != null) {
               var4.getClass().getMethod("stopBreakingBlock").invoke(var4);
            }
         }

         var2.getClass().getMethod("requestPause").invoke(var2);
         this.helper15();
      } catch (Throwable var5) {
         this.helper15();
      }
   }

   private void helper15() {
      try {
         if (mc.interactionManager != null) {
            mc.interactionManager.cancelBlockBreaking();
         }
      } catch (Throwable var2) {
      }
   }

   private static Object helper16() throws ReflectiveOperationException {
      Class var0 = Class.forName("baritone.api.BaritoneAPI");
      Object var1 = var0.getMethod("getProvider").invoke(null);
      return var1 == null ? null : var1.getClass().getMethod("getPrimaryBaritone").invoke(var1);
   }

   private static Object helper17(Object target, String methodName) throws ReflectiveOperationException {
      return target.getClass().getMethod(methodName).invoke(target);
   }
}
