package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class Spider extends Module {
   public static Spider INSTANCE = new Spider();
   private final ModeSetting modeSetting = new ModeSetting("Мод", "Вода", "HolyWorld(Вода)", "SpookyTime");
   private final BooleanSetting booleanSetting = new BooleanSetting("Легит", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Авто-включение", true);
   private int index = -1;
   private boolean flag = false;
   private int index2 = -1;
   private int index3;
   private int index4 = -1;
   private boolean flag2;
   private boolean flag3 = false;
   private int index5 = 0;
   private boolean flag4 = false;
   private double level = 0.0;
   private int index6 = 0;
   private boolean flag5 = false;
   private boolean flag6 = false;
   private int index7 = 0;

   public Spider() {
      super("Spider", "Позволяет взбираться по стенам", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting, this.booleanSetting, this.booleanSetting2);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag6 = false;
      this.index7 = 0;
      if (mc.player != null && mc.player.isOnGround()) {
         mc.player.jump();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player != null) {
         if (this.index != -1 && this.booleanSetting.isState()) {
            mc.player.getInventory().selectedSlot = this.index;
         }

         this.index = -1;
         this.index2 = -1;
         this.flag = false;
         this.index3 = 0;
         this.index4 = -1;
         this.flag2 = false;
         this.flag3 = false;
         this.index5 = 0;
         this.flag4 = false;
         this.flag5 = false;
         this.index6 = 0;
         this.flag6 = false;
         this.index7 = 0;
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.booleanSetting2.isState() && !this.flag6 && mc.player.isOnGround()) {
            this.index7++;
            if (this.index7 > 5) {
               this.setEnabled(true);
               this.flag6 = true;
               mc.player.jump();
               return;
            }
         }

         boolean var2 = mc.player.horizontalCollision;
         if (var2 && !this.flag4) {
            this.level = mc.player.getY();
            this.index6 = 0;
            this.flag5 = false;
         }

         if (var2) {
            double var3 = mc.player.getY();
            double var5 = var3 - this.level;
            if (var5 >= 0.9 && var5 <= 1.1) {
               this.flag5 = true;
               this.index6++;
            } else {
               if (var5 > 0.05) {
                  this.flag5 = true;
               }

               this.index6 = 0;
            }

            this.level = var3;
         }

         if (this.flag5 && this.index6 > 0) {
            float var8 = 45.0F;
            RotationStorage.update(new Rotation(mc.player.getYaw(), var8), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
            if (!var2 && this.flag4 && this.flag5) {
               this.helper();
            }
         } else if (var2) {
            if (!mc.player.horizontalCollision) {
               this.helper2();
               return;
            }

            this.flag = true;
            float var7 = 75.0F;
            RotationStorage.update(new Rotation(mc.player.getYaw(), var7), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
            if (!this.flag3 && mc.player.isOnGround()) {
               mc.player.jump();
               this.flag3 = true;
            }

            if (this.index5 % 1 == 0) {
               this.helper3();
            }

            this.index5++;
            if (this.modeSetting.is("SpookyTime")) {
               this.helper4();
            }
         } else {
            this.flag4 = false;
            this.flag5 = false;
            this.index6 = 0;
         }

         this.flag4 = var2;
      }
   }

   private void helper() {
      RotationStorage.update(new Rotation(mc.player.getYaw(), mc.player.getPitch()), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
      this.flag5 = false;
      this.index6 = 0;
   }

   private void helper2() {
      if (this.index != -1 && this.booleanSetting.isState()) {
         mc.player.getInventory().selectedSlot = this.index;
         this.index = -1;
      }

      if (this.index2 != -1) {
         mc.interactionManager.clickSlot(0, this.index2, 0, SlotActionType.QUICK_MOVE, mc.player);
         this.index2 = -1;
      }

      this.flag = false;
      this.index3 = 0;
      this.index4 = -1;
      this.flag2 = false;
      this.flag3 = false;
      this.index5 = 0;
      this.flag4 = false;
      this.flag5 = false;
      this.index6 = 0;
   }

   private void helper3() {
      int var1 = this.helper8(false);
      if (var1 != -1) {
         this.helper5(var1, this.booleanSetting.isState());
      }
   }

   private void helper4() {
      int var1 = this.helper8(true);
      boolean var2 = this.index3 % 5 == 0;
      boolean var3 = this.index3 % 4 != 3;
      this.helper6();
      if (var1 != -1 && var2) {
         this.helper5(var1, false);
         this.helper6();
      }

      double var4 = var3 ? 0.18 : 0.03;
      mc.player.setVelocity(mc.player.getVelocity().x, var4, mc.player.getVelocity().z);
      this.index3++;
   }

   private void helper5(int bucketSlot, boolean legitMode) {
      if (!legitMode) {
         int var5 = mc.player.getInventory().selectedSlot;
         boolean var4 = bucketSlot >= 9 && bucketSlot <= 35;
         if (var4) {
            mc.interactionManager.clickSlot(0, bucketSlot, var5, SlotActionType.SWAP, mc.player);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.interactionManager.clickSlot(0, bucketSlot, var5, SlotActionType.SWAP, mc.player);
         } else {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bucketSlot));
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var5));
         }
      } else {
         boolean var3 = bucketSlot >= 9 && bucketSlot <= 35;
         if (var3) {
            mc.interactionManager.clickSlot(0, bucketSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
            this.index2 = bucketSlot;
         } else if (mc.player.getInventory().selectedSlot != bucketSlot) {
            if (this.index == -1) {
               this.index = mc.player.getInventory().selectedSlot;
            }

            mc.player.getInventory().selectedSlot = bucketSlot;
         }

         mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      }
   }

   private void helper6() {
      if (this.helper11(mc.player.getOffHandStack())) {
         if (!this.flag2 || this.index3 % 12 == 0) {
            this.helper7(Hand.OFF_HAND);
         }

         this.flag2 = true;
      } else {
         if (this.index4 == -1 || !this.helper11(mc.player.getInventory().getStack(this.index4))) {
            this.index4 = this.helper9();
            this.flag2 = false;
         }

         if (this.index4 != -1) {
            if (mc.player.getInventory().selectedSlot != this.index4) {
               mc.player.getInventory().selectedSlot = this.index4;
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.index4));
               this.flag2 = false;
            }

            if (!this.flag2 || this.index3 % 12 == 0) {
               this.helper7(Hand.MAIN_HAND);
            }

            this.flag2 = true;
         }
      }
   }

   private void helper7(Hand hand) {
      mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand, 0, mc.player.getYaw(), mc.player.getPitch()));
   }

   private int helper8(boolean allowLava) {
      for (int var2 = 0; var2 < 9; var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (this.helper10(var3, allowLava)) {
            return var2;
         }
      }

      if (!this.booleanSetting.isState() || this.modeSetting.is("SpookyTime")) {
         for (int var4 = 9; var4 < 36; var4++) {
            ItemStack var5 = mc.player.getInventory().getStack(var4);
            if (this.helper10(var5, allowLava)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private int helper9() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (this.helper11(mc.player.getInventory().getStack(var1))) {
            return var1;
         }
      }

      return -1;
   }

   private boolean helper10(ItemStack stack, boolean allowLava) {
      return stack.getItem() == Items.WATER_BUCKET || allowLava && stack.getItem() == Items.LAVA_BUCKET;
   }

   private boolean helper11(ItemStack stack) {
      return stack.getItem() instanceof BowItem || stack.getItem() instanceof TridentItem;
   }
}
