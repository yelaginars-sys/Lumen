package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class AutoFish extends Module {
   public static AutoFish INSTANCE = new AutoFish();
   private final BooleanSetting booleanSetting = new BooleanSetting("Автоматически брать удочку", true);
   private boolean flag = false;
   private boolean flag2 = false;
   private int index = -1;
   private long timestamp = 0L;
   private long timestamp2 = 0L;

   public AutoFish() {
      super("AutoFish", "Автоматизирует процесс рыбалки", Module.ModuleCategory.PLAYER);
      this.addSettings(this.booleanSetting);
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.flag2 = false;
      this.index = -1;
      this.timestamp = 0L;
      this.timestamp2 = 0L;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.booleanSetting.isState() && this.index == -1) {
            this.helper2();
         }

         if (this.index != -1 && mc.player.getInventory().selectedSlot != this.index) {
            mc.player.getInventory().selectedSlot = this.index;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.index));
         }

         long var2 = System.currentTimeMillis();
         if (this.flag && var2 - this.timestamp2 >= 600L) {
            this.helper();
            this.flag = false;
            this.flag2 = true;
            this.timestamp = var2;
         }

         if (this.flag2 && var2 - this.timestamp >= 300L) {
            this.helper();
            this.flag2 = false;
            this.timestamp = var2;
         }
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (mc.player != null && mc.world != null) {
         if (event.getPacket() instanceof PlaySoundS2CPacket var2 && var2.getSound().value() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
            this.flag = true;
            this.timestamp2 = System.currentTimeMillis();
         }
      }
   }

   private void helper() {
      if (mc.player != null && mc.interactionManager != null) {
         if (this.index != -1 && this.index < 9) {
            ItemStack var1 = mc.player.getInventory().getStack(this.index);
            if (var1.getItem() instanceof FishingRodItem) {
               if (mc.player.getInventory().selectedSlot != this.index) {
                  mc.player.getInventory().selectedSlot = this.index;
                  mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.index));
               }

               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
         }
      }
   }

   private void helper2() {
      if (mc.player != null) {
         int var1 = -1;
         int var2 = -1;

         for (int var3 = 0; var3 < 9; var3++) {
            ItemStack var4 = mc.player.getInventory().getStack(var3);
            if (var4.getItem() instanceof FishingRodItem) {
               int var5 = EnchantmentHelper.getEnchantments(var4).getSize();
               if (var5 > var2) {
                  var2 = var5;
                  var1 = var3;
               }
            }
         }

         if (var1 != -1) {
            this.index = var1;
         }
      }
   }
}
