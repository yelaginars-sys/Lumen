package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.player.InventoryUtils;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.mixin.IClientInteractionManagerAccessor;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class ElytraSwap extends Module {
   public static final ElytraSwap INSTANCE = new ElytraSwap();
   private static final String TEXT = "Легит";
   private static final String TEXT2 = "Мгновенно";
   private final BindSetting bindSetting = new BindSetting("Бинд свапа", -1);
   private final BindSetting bindSetting2 = new BindSetting("Бинд фейерверка", -1);
   private final ModeSetting modeSetting = new ModeSetting("Режим", "Легит", "Легит", "Мгновенно");
   private final FloatSetting floatSetting = new FloatSetting("Задержка", 2.0F, 0.0F, 5.0F, 1.0F).visible(() -> this.modeSetting.is("Легит"));
   private final BooleanSetting booleanSetting = new BooleanSetting("Только хотбар", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Авто нагрудник", true);
   private boolean flag;
   private boolean flag2;
   private int index;
   private boolean flag3;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int index2;
   private boolean flag4;
   private final TimerUtils timerUtils = new TimerUtils();
   private long timestamp = -1L;
   private boolean flag5;
   private final TimerUtils timerUtils2 = new TimerUtils();
   private ElytraSwap.FW fW = ElytraSwap.FW.NONE;
   private int index3;
   private long timestamp2;
   private final TimerUtils timerUtils3 = new TimerUtils();
   private int index4 = -1;
   private Hand hand2 = Hand.MAIN_HAND;
   private int index5 = -1;
   private int index6 = -1;
   private boolean flag6;

   public ElytraSwap() {
      super("ElytraSwap", "Легитный свап элитры и фейерверк как AutoSwap", Module.ModuleCategory.PLAYER);
      this.addSettings(this.bindSetting, this.bindSetting2, this.modeSetting, this.floatSetting, this.booleanSetting, this.booleanSetting2);
   }

   @Override
   public void onDisable() {
      this.helper9();
      this.itemStack = ItemStack.EMPTY;
      this.index2 = 0;
      this.flag4 = false;
      this.timestamp = -1L;
      this.flag5 = false;
      this.timerUtils2.reset();
      this.helper16();
      super.onDisable();
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
            if (!this.helper8()) {
               int var2 = event.getKey();
               if (var2 != -1) {
                  if (this.bindSetting.getKey() != -1 && var2 == this.bindSetting.getKey()) {
                     this.helper();
                  } else if (this.bindSetting2.getKey() != -1 && var2 == this.bindSetting2.getKey()) {
                     this.flag5 = true;
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (mc.player.isGliding()) {
            this.flag4 = true;
         }

         this.helper6();
         if (this.flag3) {
            this.flag3 = false;
            SwapManager.closeHiddenInventory();
         } else if (this.flag) {
            if (mc.currentScreen == null && mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
               int var2 = this.helper3(this.flag2);
               if (var2 == -1) {
                  this.helper9();
               } else if (this.modeSetting.is("Легит") && this.index > 0) {
                  this.index--;
               } else {
                  this.helper5(var2);
                  int var3 = this.helper4();
                  if (var3 != -1 && SwapManager.openHiddenInventory()) {
                     int var4 = SwapManager.toContainerSlot(var2);
                     mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var4, 0, SlotActionType.PICKUP, mc.player);
                     mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var3, 0, SlotActionType.PICKUP, mc.player);
                     mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var4, 0, SlotActionType.PICKUP, mc.player);
                     this.flag3 = true;
                     this.flag = false;
                  } else {
                     this.helper9();
                  }
               }
            } else {
               this.helper9();
            }
         } else if (this.fW != ElytraSwap.FW.NONE) {
            this.helper12();
         } else if (mc.currentScreen != null) {
            this.flag5 = false;
            this.timestamp = -1L;
         } else if (this.flag5 && this.timerUtils2.finished(150L)) {
            this.flag5 = false;
            this.helper10();
         } else {
            if (!this.booleanSetting2.isState()
               || !this.flag4
               || !mc.player.isOnGround()
               || !mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
               this.timestamp = -1L;
            } else if (this.timestamp < 0L) {
               this.timestamp = this.helper20(220L, 480L);
               this.timerUtils.reset();
            } else if (this.timerUtils.finished(this.timestamp)) {
               this.timestamp = -1L;
               if (this.helper2(false)) {
                  this.index = this.modeSetting.is("Легит") ? (int)this.floatSetting.get() : 0;
               }
            }
         }
      } else {
         this.helper9();
      }
   }

   private void helper() {
      boolean var1 = this.helper7().isOf(Items.ELYTRA);
      if (!this.helper2(!var1)) {
         this.helper2(var1);
      }
   }

   private boolean helper2(boolean elytra) {
      if (this.helper3(elytra) == -1) {
         return false;
      }

      this.flag2 = elytra;
      this.index = this.modeSetting.is("Легит") ? (int)this.floatSetting.get() : 0;
      this.flag = true;
      return true;
   }

   private int helper3(boolean elytra) {
      if (mc.player == null) {
         return -1;
      } else if (elytra) {
         int var3 = InventoryUtils.findBestElytraSlot(0, 8);
         return var3 != -1 ? var3 : InventoryUtils.findBestElytraSlot(9, 35);
      } else {
         int var2 = InventoryUtils.findBestChestplateSlot(0, 8);
         return var2 != -1 ? var2 : InventoryUtils.findBestChestplateSlot(9, 35);
      }
   }

   private int helper4() {
      if (mc.player == null) {
         return -1;
      }

      PlayerInventory var1 = mc.player.getInventory();
      int var2 = 36 + EquipmentSlot.CHEST.getEntitySlotId();

      for (Slot var4 : mc.player.playerScreenHandler.slots) {
         if (var4.inventory == var1 && var4.getIndex() == var2) {
            return var4.id;
         }
      }

      return -1;
   }

   private void helper5(int slot) {
      this.itemStack = mc.player.getInventory().getStack(slot).copy();
      this.index2 = 10;
   }

   private void helper6() {
      if (this.index2 > 0) {
         this.index2--;
         if (SwapManager.matches(mc.player.getEquippedStack(EquipmentSlot.CHEST), this.itemStack)) {
            this.index2 = 0;
         }

         if (this.index2 == 0) {
            this.itemStack = ItemStack.EMPTY;
         }
      }
   }

   private ItemStack helper7() {
      if (!this.itemStack.isEmpty()) {
         return this.itemStack;
      } else {
         return mc.player == null ? ItemStack.EMPTY : mc.player.getEquippedStack(EquipmentSlot.CHEST);
      }
   }

   private boolean helper8() {
      return this.flag || this.flag3 || this.index2 > 0 || this.fW != ElytraSwap.FW.NONE;
   }

   private void helper9() {
      this.flag = false;
      this.index = 0;
      this.flag3 = false;
      SwapManager.closeHiddenInventory();
   }

   private void helper10() {
      if (mc.player.getOffHandStack().isOf(Items.FIREWORK_ROCKET)) {
         this.helper11(-1, Hand.OFF_HAND);
      } else {
         int var1 = this.helper19(Items.FIREWORK_ROCKET, 0, 8);
         if (var1 != -1) {
            this.helper11(var1, Hand.MAIN_HAND);
         } else {
            if (!this.booleanSetting.isState()) {
               int var2 = this.helper19(Items.FIREWORK_ROCKET, 9, 35);
               if (var2 != -1) {
                  this.helper11(var2, Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   private void helper11(int slot, Hand hand) {
      if (this.fW == ElytraSwap.FW.NONE) {
         this.fW = ElytraSwap.FW.RUN;
         this.index3 = 1;
         this.timestamp2 = 0L;
         this.timerUtils3.reset();
         this.index4 = slot;
         this.hand2 = hand;
         this.index5 = mc.player.getInventory().selectedSlot;
         this.index6 = -1;
         this.flag6 = false;
      }
   }

   private void helper12() {
      if (this.timerUtils3.finished(this.timestamp2)) {
         if (this.index3 == 1) {
            if (!this.helper13()) {
               this.helper16();
               return;
            }

            this.index3 = 2;
            this.timestamp2 = this.modeSetting.is("Мгновенно") ? this.helper20(35L, 80L) : this.helper20(70L, 150L);
            this.timerUtils3.reset();
         } else if (this.index3 == 2) {
            if (!this.helper14()) {
               this.helper16();
               return;
            }

            this.index3 = 3;
            this.timestamp2 = this.modeSetting.is("Мгновенно") ? this.helper20(20L, 50L) : this.helper20(60L, 130L);
            this.timerUtils3.reset();
         } else {
            this.helper15();
         }
      }
   }

   private boolean helper13() {
      if (this.hand2 == Hand.OFF_HAND) {
         return mc.player.getOffHandStack().isOf(Items.FIREWORK_ROCKET);
      }

      if (this.index4 < 0 || this.index4 > 35) {
         return false;
      }

      if (this.index4 < 9) {
         this.helper18(this.index4);
         return true;
      }

      int var1 = this.helper17();
      if (var1 == -1) {
         return false;
      }

      SwapManager.swapInventorySlotHidden(this.index4, var1);
      this.flag6 = true;
      this.index6 = var1;
      this.helper18(var1);
      return true;
   }

   private boolean helper14() {
      ItemStack var1 = this.hand2 == Hand.OFF_HAND ? mc.player.getOffHandStack() : mc.player.getMainHandStack();
      if (!var1.isOf(Items.FIREWORK_ROCKET)) {
         return false;
      }

      mc.interactionManager.interactItem(mc.player, this.hand2);
      mc.player.swingHand(this.hand2);
      this.timerUtils2.reset();
      return true;
   }

   private void helper15() {
      if (this.flag6 && this.index6 != -1 && this.index4 != -1) {
         SwapManager.swapInventorySlotHidden(this.index4, this.index6);
      }

      if (this.index5 >= 0 && this.index5 <= 8) {
         this.helper18(this.index5);
      }

      this.helper16();
   }

   private void helper16() {
      this.fW = ElytraSwap.FW.NONE;
      this.index3 = 0;
      this.timestamp2 = 0L;
      this.index4 = -1;
      this.hand2 = Hand.MAIN_HAND;
      this.index5 = -1;
      this.index6 = -1;
      this.flag6 = false;
   }

   private int helper17() {
      int var1 = -1;

      for (int var2 = 0; var2 < 9; var2++) {
         if (mc.player.getInventory().getStack(var2).isEmpty()) {
            return var2;
         }

         if (var2 != this.index5 && var1 == -1) {
            var1 = var2;
         }
      }

      return var1;
   }

   private void helper18(int slot) {
      if (slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
         if (mc.interactionManager instanceof IClientInteractionManagerAccessor var2) {
            var2.lumen$setLastSelectedSlot(slot);
         }
      }
   }

   private int helper19(Item item, int from, int to) {
      for (int var4 = from; var4 <= to; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && var5.isOf(item)) {
            return var4;
         }
      }

      return -1;
   }

   private long helper20(long min, long max) {
      if (min > max) {
         long var5 = min;
         min = max;
         max = var5;
      }

      return ThreadLocalRandom.current().nextLong(min, max + 1L);
   }

   private enum FW {
      NONE,
      RUN;
   }
}
