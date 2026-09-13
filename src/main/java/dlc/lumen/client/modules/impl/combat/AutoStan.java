package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.movement.Sprint;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.mixin.IClientInteractionManagerAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class AutoStan extends Module {
   public static final AutoStan INSTANCE = new AutoStan();
   private final FloatSetting floatSetting = new FloatSetting("Дистанция", 12.0F, 1.0F, 16.0F, 0.5F);
   private final FloatSetting floatSetting2 = new FloatSetting("Порог ХП", 10.0F, 1.0F, 20.0F, 0.5F);
   private boolean flag = false;
   private boolean flag2 = false;
   private int index = 0;
   private int index2 = 0;
   private int index3 = -1;
   private int index4 = -1;
   private int index5 = -1;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private boolean flag5 = false;

   public AutoStan() {
      super("AutoStan", "Стан по цели Aura с низким ХП и жемчугом", Module.ModuleCategory.COMBAT);
      this.addSettings(this.floatSetting, this.floatSetting2);
   }

   @Override
   public void onDisable() {
      this.updateState3();
      this.flag = false;
      this.flag5 = false;
      this.flag4 = false;
      if (this.flag2) {
         this.updateState7();
      }

      this.updateState5();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (this.flag5) {
            this.updateState5();
            this.index++;
            if (this.index >= 2) {
               this.flag5 = false;
               if (!this.checkState()) {
                  this.updateState3();
                  return;
               }

               this.flag = true;
            }
         } else if (this.flag) {
            this.updateState5();
            this.index++;
            switch (this.index2) {
               case 0:
                  if (this.index >= 2) {
                     if (this.flag3) {
                        SwapManager.swapInventorySlotHidden(this.index5, this.index4);
                     }

                     this.updateState4(this.index4);
                     this.index2 = 1;
                     this.index = 0;
                  }
                  break;
               case 1:
                  if (this.index >= 2) {
                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     mc.player.swingHand(Hand.MAIN_HAND);
                     this.updateState2();
                  }
            }
         } else if (!this.flag && !this.flag5) {
            if (Aura.INSTANCE.getTarget() instanceof PlayerEntity var3 && var3.isAlive() && var3 != mc.player) {
               double var4 = this.floatSetting.get();
               if (!(mc.player.squaredDistanceTo(var3) > var4 * var4)) {
                  if (!(this.computefloat(var3) >= this.floatSetting2.get())) {
                     if (!this.checkCondition(var3)) {
                        this.flag4 = false;
                     } else if (!this.flag4) {
                        this.flag4 = true;
                        this.updateState();
                     }
                  }
               }
            } else {
               this.flag4 = false;
            }
         }
      } else {
         if (this.flag || this.flag5) {
            this.flag = false;
            this.flag5 = false;
            this.updateState3();
         }
      }
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if ((this.flag || this.flag5) && mc.player != null) {
         mc.player.setSprinting(false);
         event.setForward(0.0F);
         event.setStrafe(0.0F);
         event.setJump(false);
         event.setSneak(false);
         event.setSprint(false);
      }
   }

   private boolean checkCondition(PlayerEntity player) {
      return player.getMainHandStack().isOf(Items.ENDER_PEARL) || player.getOffHandStack().isOf(Items.ENDER_PEARL);
   }

   private float computefloat(LivingEntity entity) {
      return entity.getHealth() + entity.getAbsorptionAmount();
   }

   private void updateState() {
      this.index3 = mc.player.getInventory().selectedSlot;
      this.flag3 = false;
      this.index5 = -1;
      this.index4 = -1;
      this.updateState6();
      this.updateState5();
      this.flag5 = true;
      this.index = 0;
   }

   private boolean checkState() {
      int var1 = this.resolveInt();
      if (var1 != -1) {
         this.index4 = var1;
      } else {
         int var2 = this.resolveInt2();
         if (var2 == -1) {
            return false;
         }

         this.index5 = var2;
         this.index4 = this.index3;
         this.flag3 = true;
      }

      this.index2 = 0;
      this.index = 0;
      return true;
   }

   private void updateState2() {
      this.flag = false;
      this.updateState3();
   }

   private void updateState3() {
      if (this.flag3 && this.index5 != -1 && this.index4 != -1 && mc.interactionManager != null && mc.player != null) {
         SwapManager.swapInventorySlotHidden(this.index5, this.index4);
      }

      if (this.index3 >= 0 && this.index3 <= 8 && mc.player != null) {
         this.updateState4(this.index3);
      }

      this.updateState7();
      this.updateState5();
      this.flag3 = false;
      this.index5 = -1;
      this.index4 = -1;
      this.index3 = -1;
   }

   private void updateState4(int slot) {
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

   private void updateState5() {
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

   private void updateState6() {
      if (!this.flag2) {
         Sprint.pushPause(0L);
         this.flag2 = true;
      }
   }

   private void updateState7() {
      if (this.flag2) {
         Sprint.popPause();
         this.flag2 = false;
      }
   }

   private int resolveInt() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isOf(Items.NETHER_STAR)) {
            return var1;
         }
      }

      return -1;
   }

   private int resolveInt2() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isOf(Items.NETHER_STAR)) {
            return var1;
         }
      }

      return -1;
   }
}
