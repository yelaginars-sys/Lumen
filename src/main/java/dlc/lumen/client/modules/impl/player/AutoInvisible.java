package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoInvisible extends Module {
   public static final AutoInvisible INSTANCE = new AutoInvisible();
   private final FloatSetting floatSetting = new FloatSetting("Перепивать за, сек", 8.0F, 0.0F, 60.0F, 1.0F);
   private boolean drinking;
   private long timestamp = 0L;
   private long timestamp2 = 0L;
   private int index = -1;
   private int index2 = -1;
   private int index3 = -1;
   private boolean flag;
   private static final long TIMESTAMP = 5000L;
   private static final long TIMESTAMP2 = 1500L;

   public AutoInvisible() {
      super("AutoInvisible", "Пьёт зелье невидимости и перепивает по истечении эффекта", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting);
   }

   public boolean isDrinking() {
      return this.isEnable() && this.drinking;
   }

   @Override
   public void onEnable() {
      this.drinking = false;
      this.timestamp = 0L;
      this.timestamp2 = 0L;
      this.index = -1;
      this.index2 = -1;
      this.index3 = -1;
      this.flag = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.helper8();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      long var2 = System.currentTimeMillis();
      if (mc.player == null || mc.world == null || mc.interactionManager == null) {
         this.drinking = false;
      } else if (mc.player.getAbilities().creativeMode || mc.player.isSpectator()) {
         this.helper8();
      } else if (mc.currentScreen != null) {
         if (this.drinking) {
            this.helper8();
         }
      } else if (this.drinking) {
         this.helper(var2);
      } else if (this.helper5()) {
         if (var2 - this.timestamp2 >= 1500L) {
            if (this.helper4() != -1) {
               this.drinking = true;
               this.timestamp = var2;
               this.timestamp2 = var2;
               this.index = mc.player.getInventory().selectedSlot;
               this.index2 = -1;
               this.index3 = -1;
               this.flag = false;
            }
         }
      }
   }

   private void helper(long now) {
      ClientPlayerEntity var3 = mc.player;
      if (now - this.timestamp > 5000L) {
         this.helper8();
      } else if (this.helper6()) {
         this.helper8();
      } else {
         if (mc.options != null) {
            mc.options.attackKey.setPressed(false);
         }

         Hand var4 = null;
         if (this.helper7(var3.getMainHandStack())) {
            var4 = Hand.MAIN_HAND;
         } else if (this.helper7(var3.getOffHandStack())) {
            var4 = Hand.OFF_HAND;
         }

         if (var4 == null) {
            this.helper2(var3);
         } else {
            if (mc.options != null) {
               mc.options.useKey.setPressed(true);
            }

            if (!var3.isUsingItem() || var3.getActiveHand() != var4) {
               ((IMinecraftClientAccessor)mc).setItemUseCooldown(0);
               mc.interactionManager.interactItem(var3, var4);
            }
         }
      }
   }

   private void helper2(ClientPlayerEntity player) {
      if (this.index2 == -1) {
         int var2 = this.helper4();
         if (var2 == -1 || var2 == 40) {
            this.helper8();
            return;
         }

         if (var2 < 9) {
            this.index2 = var2;
            this.helper9(this.index2);
            return;
         }

         int var3 = this.helper3();
         if (var3 == -1) {
            var3 = this.index >= 0 ? this.index : player.getInventory().selectedSlot;
         }

         this.index2 = var3;
         this.index3 = SwapManager.toContainerSlot(var2);
         this.helper9(var3);
      }

      if (!this.flag) {
         mc.interactionManager.clickSlot(player.playerScreenHandler.syncId, this.index3, this.index2, SlotActionType.SWAP, player);
         this.flag = true;
      }
   }

   private int helper3() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private int helper4() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.helper7(mc.player.getInventory().getStack(var1))) {
            return var1;
         }
      }

      return this.helper7(mc.player.getOffHandStack()) ? 40 : -1;
   }

   private boolean helper5() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      if (var1 == null) {
         return true;
      }

      int var2 = (int)(this.floatSetting.get() * 20.0F);
      return var1.getDuration() <= var2;
   }

   private boolean helper6() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      if (var1 == null) {
         return false;
      }

      int var2 = (int)(this.floatSetting.get() * 20.0F);
      return var1.getDuration() > var2 + 20;
   }

   private boolean helper7(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.isOf(Items.POTION)) {
         PotionContentsComponent var2 = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         }

         for (StatusEffectInstance var4 : var2.getEffects()) {
            if (var4.getEffectType().equals(StatusEffects.INVISIBILITY)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void helper8() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }

      if (mc.player != null && mc.interactionManager != null) {
         if (this.index3 >= 0 && this.index2 >= 0) {
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, this.index3, this.index2, SlotActionType.SWAP, mc.player);
         }

         if (this.index >= 0) {
            this.helper9(this.index);
         }
      }

      this.drinking = false;
      this.index = -1;
      this.index2 = -1;
      this.index3 = -1;
      this.flag = false;
   }

   private void helper9(int slot) {
      if (mc.player != null && slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         if (mc.player.networkHandler != null) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
         }
      }
   }
}
