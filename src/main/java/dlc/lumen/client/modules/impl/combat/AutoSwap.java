package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.movement.Sprint;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class AutoSwap extends Module {
   public static final AutoSwap INSTANCE = new AutoSwap();
   private static final String TEXT = "Голова";
   private static final String TEXT2 = "Тотем";
   private static final String TEXT3 = "Гапл";
   private static final String TEXT4 = "Щит";
   private static final String TEXT5 = "Талисман";
   private static final String TEXT6 = "Легит";
   private static final String TEXT7 = "Мгновенно";
   private static final int INDEX = 10;
   private final ModeSetting modeSetting = new ModeSetting("Предмет", "Тотем", "Голова", "Тотем", "Гапл", "Щит", "Талисман");
   private final ModeSetting modeSetting2 = new ModeSetting("Второй предмет", "Гапл", "Голова", "Тотем", "Гапл", "Щит", "Талисман");
   private final ModeSetting modeSetting3 = new ModeSetting("Режим", "Легит", "Легит", "Мгновенно");
   private final FloatSetting value = new FloatSetting("Задержка из инвентаря", 2.0F, 0.0F, 5.0F, 1.0F)
      .visible(() -> this.modeSetting3.is("Легит"));
   private final BindSetting bindSetting = new BindSetting("Бинд свапа", -1);
   private final BindSetting bindSetting2 = new BindSetting("Бинд сферы", -1);
   private final TextSetting textSetting = new TextSetting("Имя сферы", "Сфера Цербера", 64).visible(() -> this.bindSetting2.getKey() != -1);
   private Item item2;
   private boolean flag;
   private boolean flag2;
   private int index;
   private boolean flag3;
   private int index2 = -1;
   private boolean flag4;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int index3 = -1;
   private boolean flag5;
   private ItemStack itemStack2 = ItemStack.EMPTY;
   private int index4;
   private int index5;
   private boolean flag6;

   public AutoSwap() {
      super("AutoSwap", "Свап предмета в левую руку по бинду", Module.ModuleCategory.COMBAT);
      this.addSettings(
         this.modeSetting,
         this.modeSetting2,
         this.modeSetting3,
         this.value,
         this.bindSetting,
         this.bindSetting2,
         this.textSetting
      );
   }

   @Override
   public void onDisable() {
      this.updateState12();
      this.itemStack2 = ItemStack.EMPTY;
      this.index4 = 0;
      this.index5 = 0;
      this.itemStack = ItemStack.EMPTY;
      this.index3 = -1;
      this.flag5 = false;
      this.updateState16();
      super.onDisable();
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
            if (!this.checkState()) {
               int var2 = event.getKey();
               if (var2 != -1) {
                  if (var2 == this.bindSetting.getKey()) {
                     this.itemStack = ItemStack.EMPTY;
                     this.index3 = -1;
                     this.updateState();
                  } else if (var2 == this.bindSetting2.getKey()) {
                     this.updateState2();
                  }
               }
            }
         }
      }
   }

   private void updateState() {
      Item var1 = this.computeItem(this.modeSetting);
      Item var2 = this.computeItem(this.modeSetting2);
      boolean var3 = this.modeSetting.is("Талисман");
      boolean var4 = this.modeSetting2.is("Талисман");
      boolean var5 = this.checkCondition6(this.computeItemStack()) || this.checkCondition5(this.computeItemStack(), var1, var3);
      Item var6 = var5 ? var2 : var1;
      Item var7 = var5 ? var1 : var2;
      boolean var8 = var5 ? var4 : var3;
      boolean var9 = var5 ? var3 : var4;
      if (!this.checkCondition(var6, false, var8)) {
         this.checkCondition(var7, false, var9);
      }
   }

   private void updateState2() {
      if (this.checkCondition6(this.computeItemStack())) {
         this.updateState4();
      } else {
         if (this.checkCondition(Items.PLAYER_HEAD, true, false)) {
            this.updateState3();
         }
      }
   }

   private void updateState3() {
      if (mc.player != null) {
         this.itemStack = this.computeItemStack().copy();
         this.index3 = this.resolveInt6();
      }
   }

   private void updateState4() {
      if (this.resolveInt() == -1) {
         this.updateState();
      } else {
         this.flag5 = true;
         this.index = this.modeSetting3.is("Легит") ? (int)this.value.get() : 0;
      }
   }

   private int resolveInt() {
      if (mc.player != null && !this.itemStack.isEmpty()) {
         int var1 = -1;

         for (int var2 = 0; var2 < 36; var2++) {
            ItemStack var3 = mc.player.getInventory().getStack(var2);
            if (!var3.isEmpty() && var3.isOf(this.itemStack.getItem()) && !this.checkCondition6(var3)) {
               if (var2 == this.index3) {
                  return var2;
               }

               if (var1 == -1) {
                  var1 = var2;
               }
            }
         }

         return var1;
      } else {
         return -1;
      }
   }

   private boolean checkCondition(Item item, boolean sphere, boolean talisman) {
      if (item == Items.AIR) {
         return false;
      }

      if (sphere
         || (
            talisman
               ? !this.checkCondition2(this.computeItemStack())
               : item == Items.PLAYER_HEAD || !this.computeItemStack().isOf(item) || this.checkCondition6(this.computeItemStack())
         )) {
         if (this.resolveInt2(item, sphere, talisman) == -1) {
            return false;
         }

         this.item2 = item;
         this.flag = sphere;
         this.flag2 = talisman;
         this.index = this.modeSetting3.is("Легит") ? (int)this.value.get() : 0;
         return true;
      } else {
         return false;
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.updateState10();
         this.updateState14();
         if (this.flag3) {
            this.flag3 = false;
            SwapManager.swapSelectedWithOffhand();
         } else if (this.index2 != -1) {
            this.updateState6(this.index2);
            this.index2 = -1;
         } else if (this.flag4) {
            this.flag4 = false;
            this.updateState8();
         } else if (this.item2 != null || this.flag5) {
            if (mc.currentScreen == null && mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
               int var2 = this.flag5
                  ? this.resolveInt()
                  : this.resolveInt2(this.item2, this.flag, this.flag2);
               if (var2 == -1) {
                  this.updateState12();
               } else {
                  if (var2 < 9) {
                     this.updateState5(var2);
                  } else {
                     this.updateState7(var2);
                  }
               }
            } else {
               this.updateState12();
            }
         }
      } else {
         this.updateState12();
      }
   }

   private void updateState5(int slot) {
      this.updateState9(slot);
      this.flag5 = false;
      if (mc.player.getInventory().selectedSlot != slot) {
         this.index2 = mc.player.getInventory().selectedSlot;
         this.updateState6(slot);
         this.flag3 = true;
      } else {
         SwapManager.swapSelectedWithOffhand();
      }

      this.updateState11();
   }

   private void updateState6(int slot) {
      if (mc.player != null && mc.player.networkHandler != null) {
         if (slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
            mc.player.getInventory().selectedSlot = slot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
         }
      }
   }

   private void updateState7(int slot) {
      boolean var2 = this.modeSetting3.is("Легит");
      if (var2) {
         this.updateState13();
         if (this.index > 0) {
            this.index--;
            return;
         }
      }

      this.updateState9(slot);
      this.flag5 = false;
      this.updateState13();
      if (!SwapManager.openHiddenInventory()) {
         this.updateState12();
      } else {
         mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SwapManager.toContainerSlot(slot), 40, SlotActionType.SWAP, mc.player);
         if (var2) {
            this.index5 = 2;
            this.flag4 = true;
         } else {
            this.index5 = 2;
            this.flag4 = true;
         }

         this.updateState11();
      }
   }

   private void updateState8() {
      SwapManager.closeHiddenInventory();
   }

   private int resolveInt2(Item item, boolean sphere, boolean talisman) {
      if (mc.player == null) {
         return -1;
      }

      if (sphere) {
         return this.resolveInt6();
      }

      if (talisman) {
         return this.resolveInt4();
      }

      if (item == Items.PLAYER_HEAD) {
         return this.resolveInt3();
      }

      int var4 = this.resolveInt6();
      int var5 = -1;
      int var6 = -1;

      for (int var7 = 0; var7 < 36; var7++) {
         if (var7 != var4) {
            ItemStack var8 = mc.player.getInventory().getStack(var7);
            if (!var8.isEmpty() && var8.isOf(item)) {
               int var9 = this.resolveInt5(var8, var7, item);
               if (var9 > var6) {
                  var6 = var9;
                  var5 = var7;
               }
            }
         }
      }

      return var5;
   }

   private int resolveInt3() {
      if (mc.player == null) {
         return -1;
      }

      ItemStack var1 = this.computeItemStack();
      int var2 = -1;
      int var3 = -1;

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (this.checkCondition3(var5) && !this.checkCondition4(var5, var1)) {
            int var6 = var4 < 9 ? 1 : 0;
            if (var6 > var3) {
               var3 = var6;
               var2 = var4;
            }
         }
      }

      return var2;
   }

   private int resolveInt4() {
      if (mc.player == null) {
         return -1;
      }

      int var1 = -1;
      int var2 = -1;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && this.checkCondition2(var4)) {
            int var5 = var3 < 9 ? 1 : 0;
            if (var5 > var2) {
               var2 = var5;
               var1 = var3;
            }
         }
      }

      return var1;
   }

   private boolean checkCondition2(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isOf(Items.TOTEM_OF_UNDYING) && stack.hasEnchantments();
   }

   private boolean checkCondition3(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isOf(Items.PLAYER_HEAD) && !this.checkCondition6(stack);
   }

   private boolean checkCondition4(ItemStack first, ItemStack second) {
      return this.checkCondition3(first) && this.checkCondition3(second) && first.getName().getString().equals(second.getName().getString());
   }

   private boolean checkCondition5(ItemStack stack, Item item, boolean talisman) {
      return stack != null && !stack.isEmpty() && stack.isOf(item) ? !talisman || this.checkCondition2(stack) : false;
   }

   private int resolveInt5(ItemStack stack, int slot, Item item) {
      boolean var4 = item != Items.TOTEM_OF_UNDYING;
      int var5 = stack.hasEnchantments() == var4 ? 2 : 0;
      return var5 + (slot < 9 ? 1 : 0);
   }

   private int resolveInt6() {
      if (mc.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 36; var1++) {
         if (this.checkCondition6(mc.player.getInventory().getStack(var1))) {
            return var1;
         }
      }

      return -1;
   }

   private boolean checkCondition6(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.isOf(Items.PLAYER_HEAD)) {
         String var2 = this.textSetting.get();
         return var2 != null && !var2.isBlank() && stack.getName().getString().contains(var2);
      } else {
         return false;
      }
   }

   private Item computeItem(ModeSetting setting) {
      return switch (setting.getCurrent()) {
         case "Голова" -> Items.PLAYER_HEAD;
         case "Тотем" -> Items.TOTEM_OF_UNDYING;
         case "Гапл" -> Items.GOLDEN_APPLE;
         case "Щит" -> Items.SHIELD;
         case "Талисман" -> Items.TOTEM_OF_UNDYING;
         default -> Items.AIR;
      };
   }

   private boolean checkState() {
      return this.item2 != null
         || this.flag5
         || this.flag3
         || this.index2 != -1
         || this.flag4
         || this.index4 > 0;
   }

   private void updateState9(int slot) {
      this.itemStack2 = mc.player.getInventory().getStack(slot).copy();
      this.index4 = 10;
   }

   private void updateState10() {
      if (this.index4 > 0) {
         this.index4--;
         if (SwapManager.matches(mc.player.getOffHandStack(), this.itemStack2)) {
            this.index4 = 0;
         }

         if (this.index4 == 0) {
            this.itemStack2 = ItemStack.EMPTY;
         }
      }
   }

   private ItemStack computeItemStack() {
      if (!this.itemStack2.isEmpty()) {
         return this.itemStack2;
      } else {
         return mc.player == null ? ItemStack.EMPTY : mc.player.getOffHandStack();
      }
   }

   private void updateState11() {
      this.item2 = null;
      this.flag = false;
      this.flag2 = false;
      this.index = 0;
   }

   private void updateState12() {
      this.updateState11();
      this.flag5 = false;
      this.flag3 = false;
      this.index2 = -1;
      this.flag4 = false;
      SwapManager.closeHiddenInventory();
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if (this.index5 > 0 && mc.player != null) {
         mc.player.setSprinting(false);
         event.setSprint(false);
         if (mc.player.isOnGround()) {
            event.setForward(0.0F);
            event.setStrafe(0.0F);
            event.setJump(false);
            event.setSneak(false);
         }
      }
   }

   private void updateState13() {
      this.index5 = Math.max(this.index5, 2);
      this.updateState15();
   }

   private void updateState14() {
      if (this.index5 > 0) {
         this.index5--;
         if (this.index5 == 0) {
            this.updateState16();
         }
      }
   }

   private void updateState15() {
      if (!this.flag6) {
         Sprint.pushPause(0L);
         this.flag6 = true;
      }
   }

   private void updateState16() {
      if (this.flag6) {
         Sprint.popPause();
         this.flag6 = false;
      }
   }
}
