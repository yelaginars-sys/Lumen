package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.movement.Sprint;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.mixin.IClientInteractionManagerAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class ClickPearl extends Module {
   public static final ClickPearl INSTANCE = new ClickPearl();
   private final BindSetting bindSetting = new BindSetting("Бинд", -1);
   private boolean flag = false;
   private boolean flag2 = false;
   private int index = 0;
   private int index2 = 0;
   private int index3 = -1;
   private int index4 = -1;
   private int index5 = -1;
   private boolean flag3 = false;
   private boolean flag4 = false;

   public ClickPearl() {
      super("ClickPearl", "Быстрый бросок жемчуга по бинду", Module.ModuleCategory.PLAYER);
      this.addSettings(this.bindSetting);
   }

   @Override
   public void onDisable() {
      this.helper3();
      this.flag = false;
      if (this.flag2) {
         this.helper7();
      }

      this.helper5();
      super.onDisable();
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null && mc.currentScreen == null) {
         if (this.bindSetting.getKey() != -1 && event.getKey() == this.bindSetting.getKey() && !this.flag) {
            if (!mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.ENDER_PEARL))) {
               this.helper();
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (this.flag && mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.helper5();
         this.index++;
         switch (this.index2) {
            case 0:
               if (this.index >= 2) {
                  if (this.flag4) {
                     mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
                     mc.player.swingHand(Hand.OFF_HAND);
                     this.helper2();
                  } else {
                     if (this.flag3) {
                        SwapManager.swapInventorySlotHidden(this.index5, this.index4);
                     }

                     this.helper4(this.index4);
                     this.index2 = 1;
                     this.index = 0;
                  }
               }
               break;
            case 1:
               if (this.index >= 2) {
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                  mc.player.swingHand(Hand.MAIN_HAND);
                  this.index2 = 2;
                  this.index = 0;
               }
               break;
            case 2:
               if (this.index >= 2) {
                  this.helper2();
               }
         }
      } else {
         if (this.flag) {
            this.flag = false;
            this.helper3();
         }
      }
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if (this.flag && mc.player != null) {
         mc.player.setSprinting(false);
         event.setForward(0.0F);
         event.setStrafe(0.0F);
         event.setJump(false);
         event.setSneak(false);
         event.setSprint(false);
      }
   }

   private void helper() {
      this.index3 = mc.player.getInventory().selectedSlot;
      this.flag3 = false;
      this.flag4 = false;
      this.index5 = -1;
      this.index4 = -1;
      this.index2 = 0;
      this.index = 0;
      if (mc.player.getOffHandStack().isOf(Items.ENDER_PEARL)) {
         this.flag4 = true;
      } else {
         int var1 = this.helper8();
         if (var1 != -1) {
            this.index4 = var1;
         } else {
            int var2 = this.helper9();
            if (var2 == -1) {
               return;
            }

            this.index5 = var2;
            this.index4 = this.index3;
            this.flag3 = true;
         }
      }

      this.flag = true;
      this.helper6();
   }

   private void helper2() {
      this.flag = false;
      this.helper3();
   }

   private void helper3() {
      if (this.flag3 && this.index5 != -1 && this.index4 != -1 && mc.interactionManager != null && mc.player != null) {
         SwapManager.swapInventorySlotHidden(this.index5, this.index4);
      }

      if (this.index3 >= 0 && this.index3 <= 8) {
         this.helper4(this.index3);
      }

      this.helper7();
      this.helper5();
      this.flag3 = false;
      this.index5 = -1;
      this.index4 = -1;
      this.index3 = -1;
      this.flag4 = false;
   }

   private void helper4(int slot) {
      if (mc.player != null && slot >= 0 && slot <= 8) {
         if (mc.player.getInventory().selectedSlot != slot) {
            mc.player.getInventory().selectedSlot = slot;
            if (mc.player.networkHandler != null) {
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }

            if (mc.interactionManager instanceof IClientInteractionManagerAccessor var2) {
               var2.lumen$setLastSelectedSlot(slot);
            }
         }
      }
   }

   private void helper5() {
      if (mc.player != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.backKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
         mc.options.sprintKey.setPressed(false);
         mc.player.setSprinting(false);
      }
   }

   private void helper6() {
      if (!this.flag2) {
         Sprint.pushPause(0L);
         this.flag2 = true;
      }
   }

   private void helper7() {
      if (this.flag2) {
         Sprint.popPause();
         this.flag2 = false;
      }
   }

   private int helper8() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isOf(Items.ENDER_PEARL)) {
            return var1;
         }
      }

      return -1;
   }

   private int helper9() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isOf(Items.ENDER_PEARL)) {
            return var1;
         }
      }

      return -1;
   }
}
