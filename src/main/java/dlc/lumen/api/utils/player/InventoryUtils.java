package dlc.lumen.api.utils.player;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import lombok.Generated;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;

public final class InventoryUtils implements QClient {
   public static int getItemSlot(Item input) {
      for (ItemStack var2 : mc.player.getArmorItems()) {
         if (var2.getItem() == input) {
            return -2;
         }
      }

      int var4 = -1;

      for (int var5 = 0; var5 < 36; var5++) {
         ItemStack var3 = mc.player.getInventory().getStack(var5);
         if (var3.getItem() == input) {
            var4 = var5;
            break;
         }
      }

      if (var4 < 9 && var4 != -1) {
         var4 += 36;
      }

      return var4;
   }

   public static int getEnchantmentLevel(ItemStack stack, RegistryKey<Enchantment> enchantmentKey) {
      ItemEnchantmentsComponent var2 = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

      for (RegistryEntry var4 : var2.getEnchantments()) {
         if (var4.matchesKey(enchantmentKey)) {
            return var2.getLevel(var4);
         }
      }

      return 0;
   }

   public static int findBestElytraSlot() {
      return findBestElytraSlot(0, 35);
   }

   public static int findBestElytraSlot(int from, int to) {
      if (mc.player == null) {
         return -1;
      }

      int var2 = -1;
      double var3 = -1.0;

      for (int var5 = from; var5 <= to; var5++) {
         ItemStack var6 = mc.player.getInventory().getStack(var5);
         if (var6.getItem() == Items.ELYTRA) {
            int var7 = getEnchantmentLevel(var6, Enchantments.PROTECTION);
            int var8 = getEnchantmentLevel(var6, Enchantments.UNBREAKING);
            int var9 = getEnchantmentLevel(var6, Enchantments.MENDING);
            int var10 = var6.getMaxDamage();
            int var11 = var6.getDamage();
            double var12 = (double)(var10 - var11) / var10;
            double var14 = var7 * 100 + var8 * 10 + (var9 > 0 ? 1 : 0) + var12 * 10.0;
            if (var14 > var3) {
               var3 = var14;
               var2 = var5;
            }
         }
      }

      return var2;
   }

   public static int findBestChestplateSlot() {
      return findBestChestplateSlot(0, 35);
   }

   public static int findBestChestplateSlot(int from, int to) {
      if (mc.player == null) {
         return -1;
      }

      int var2 = -1;
      double var3 = -1.0;

      for (int var5 = from; var5 <= to; var5++) {
         ItemStack var6 = mc.player.getInventory().getStack(var5);
         if (var6.getItem() instanceof ArmorItem var7) {
            EquippableComponent var19 = var6.get(DataComponentTypes.EQUIPPABLE);
            if (var19 != null && var19.slot() == EquipmentSlot.CHEST) {
               int var9 = getEnchantmentLevel(var6, Enchantments.PROTECTION);
               int var10 = getEnchantmentLevel(var6, Enchantments.UNBREAKING);
               int var11 = getEnchantmentLevel(var6, Enchantments.MENDING);
               int var12 = getChestplatePriority(var7);
               int var13 = var6.getMaxDamage();
               int var14 = var6.getDamage();
               double var15 = var13 == 0 ? 1.0 : (double)(var13 - var14) / var13;
               double var17 = var12 * 10000.0 + var9 * 100.0 + var10 * 10.0 + (var11 > 0 ? 1 : 0) + var15 * 10.0;
               if (var17 > var3) {
                  var3 = var17;
                  var2 = var5;
               }
            }
         }
      }

      return var2;
   }

   public static int getChestplatePriority(Item item) {
      if (item == Items.NETHERITE_CHESTPLATE) {
         return 5;
      } else if (item == Items.DIAMOND_CHESTPLATE) {
         return 4;
      } else if (item == Items.IRON_CHESTPLATE) {
         return 3;
      } else if (item == Items.GOLDEN_CHESTPLATE) {
         return 2;
      } else if (item == Items.CHAINMAIL_CHESTPLATE) {
         return 2;
      } else {
         return item == Items.LEATHER_CHESTPLATE ? 1 : 0;
      }
   }

   public static int find(Item item, int start, int end) {
      if (mc.player != null) {
         for (int var3 = end; var3 >= start; var3--) {
            if (mc.player.currentScreenHandler.syncId != 0 && mc.player.currentScreenHandler.getSlot(var3).getStack().getItem() == item) {
               return var3;
            }

            if (mc.player.currentScreenHandler.syncId == 0 && mc.player.getInventory().getStack(var3).getItem() == item) {
               return var3;
            }
         }
      }

      return -1;
   }

   public static void swapAndUseHvH(Item item) {
      int var1 = find(item, 9, 45);
      int var2 = find(item, 0, 8);
      int var3 = mc.player.getInventory().selectedSlot;
      boolean var4 = mc.player.isUsingItem();
      if (mc.player.getMainHandStack().getItem() == item) {
         if (!var4) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         }
      } else if (mc.player.getOffHandStack().getItem() == item) {
         mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
      } else if (var4) {
         if (var2 != -1) {
            SwapManager.swapInventorySlotHidden(var2, 40);
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            SwapManager.swapInventorySlotHidden(var2, 40);
         } else if (var1 != -1) {
            SwapManager.swapInventorySlotHidden(var1, 40);
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            SwapManager.swapInventorySlotHidden(var1, 40);
         }
      } else if (var2 != -1) {
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var2));
         mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var3));
      } else {
         if (var1 != -1) {
            int var5 = -1;

            for (int var6 = 0; var6 < 8; var6++) {
               ItemStack var7 = mc.player.getInventory().getStack(var6);
               if (var7.isEmpty()) {
                  var5 = var6;
                  break;
               }

               UseAction var8 = var7.getUseAction();
               if (var8 == UseAction.NONE) {
                  var5 = var6;
               }
            }

            boolean var9 = false;
            if (mc.player.isSprinting()) {
               mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
               mc.player.setSprinting(false);
               mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
               if (!ModuleClass.sprint.isEnable()) {
                  mc.options.sprintKey.setPressed(false);
               }

               var9 = true;
            }

            if (var5 == -1) {
               SwapManager.swapInventorySlotHidden(var1, 8);
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(8));
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var3));
            } else {
               SwapManager.swapInventorySlotHidden(var1, var5);
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var5));
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var3));
               SwapManager.swapInventorySlotHidden(var1, var5);
            }

            if (var9) {
               mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
            }
         }
      }
   }

   @Generated
   private InventoryUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
