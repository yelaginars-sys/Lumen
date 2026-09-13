package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class AutoBrew extends Module {
   private static final float VOLUME = 120.0F;
   private static final float VOLUME2 = 8.0F;
   private static final int INDEX = 4;
   private static final int INDEX2 = 0;
   private static final float VOLUME3 = 100.0F;
   private static final float VOLUME4 = 150.0F;
   private static final float VOLUME5 = 100.0F;
   private static final float VOLUME6 = 250.0F;
   private static final float VOLUME7 = 500.0F;
   public static final AutoBrew INSTANCE = new AutoBrew();
   private final ModeSetting potionSetting = new ModeSetting("Potion", "Strength", "Strength", "Speed", "Fire Resistance", "Invisibility");
   private final BooleanSetting improveSetting = new BooleanSetting("Improve", false);
   private final BooleanSetting autoOpenSetting = new BooleanSetting("Auto Open", true);
   private final FloatSetting radiusSetting = new FloatSetting("Radius", 4.5F, 2.0F, 8.0F, 0.5F);
   private final FloatSetting chestRadiusSetting = new FloatSetting("Chest Radius", 6.0F, 2.0F, 12.0F, 0.5F);
   private final TimerUtils timerUtils = new TimerUtils();
   private final List<BlockPos> blockPoss = new ArrayList<>();
   private final Map<BlockPos, Long> blockPoss2 = new HashMap<>();
   private AutoBrew.Action action2 = AutoBrew.Action.NONE;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private int index;
   private final List<AutoBrew.WithdrawRequest> autoBrews = new ArrayList<>();
   private int index2;
   private boolean flag;
   private int index3;
   private long timestamp;
   private String text = "";
   private long timestamp2;
   private AutoBrew.PendingMove pendingMove;
   private AutoBrew.PendingOpen pendingOpen;

   public AutoBrew() {
      super("AutoBrew", "Automates brewing across nearby stands and chests", Module.ModuleCategory.PLAYER);
      this.addSettings(this.potionSetting, this.improveSetting, this.autoOpenSetting, this.radiusSetting, this.chestRadiusSetting);
   }

   @Override
   public void onEnable() {
      this.helper11();
      this.blockPoss2.clear();
      this.helper63();
      this.timerUtils.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.helper11();
      this.blockPoss2.clear();
      this.helper63();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.helper58();
         if (!this.helper69()) {
            if (!this.helper67()) {
               this.helper59();
               if (this.timerUtils.finished(this.timestamp)) {
                  this.timestamp = this.helper64();
                  if (mc.player.currentScreenHandler instanceof BrewingStandScreenHandler var5) {
                     if (this.blockPos == null) {
                        this.blockPos = this.helper55();
                     }

                     if (this.helper41(var5)) {
                        this.timerUtils.reset();
                     } else {
                        if (this.helper(var5)) {
                           this.timerUtils.reset();
                        }
                     }
                  } else if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var2
                     && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
                     if ((this.action2 == AutoBrew.Action.WITHDRAW || this.action2 == AutoBrew.Action.DEPOSIT) && this.helper2(var2)
                        )
                      {
                        this.timerUtils.reset();
                     }
                  } else if (mc.currentScreen == null) {
                     if (this.helper5()) {
                        this.timerUtils.reset();
                     } else if (this.autoOpenSetting.isState()) {
                        if (this.action2 == AutoBrew.Action.NONE
                           && this.helper33()
                           && this.helper7(this.blockPos != null ? this.blockPos : mc.player.getBlockPos())) {
                           if (this.helper5()) {
                              this.timerUtils.reset();
                           }
                        } else if (this.action2 == AutoBrew.Action.NONE) {
                           AutoBrew.StandTask var4 = this.helper12();
                           if (var4 != null) {
                              this.blockPos = var4.VOLUME;
                              this.action2 = AutoBrew.Action.OPEN_STAND;
                              if (this.helper5()) {
                                 this.timerUtils.reset();
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean helper(BrewingStandScreenHandler handler) {
      if (this.blockPos != null && !(mc.world.getBlockEntity(this.blockPos) instanceof BrewingStandBlockEntity)) {
         this.helper11();
         this.helper54();
         return true;
      }

      this.helper57(handler);
      if (this.helper35()
         && this.helper33()
         && this.autoOpenSetting.isState()
         && this.helper7(this.blockPos != null ? this.blockPos : mc.player.getBlockPos())) {
         this.helper54();
         return true;
      }

      AutoBrew.StandMode var2 = this.helper16(handler);
      switch (var2) {
         case WAIT:
            this.helper10();
            return true;
         case COLLECT:
            for (int var9 = 0; var9 < 3; var9++) {
               if (!handler.getSlot(var9).getStack().isEmpty()) {
                  this.helper42(handler.syncId, var9);
                  return true;
               }
            }

            this.helper10();
            return true;
         case CLEAR:
            int var8 = this.helper22(handler);
            if (var8 != -1) {
               this.helper42(handler.syncId, var8);
               return true;
            }

            this.helper10();
            return true;
         case FILL_WATER:
            int var7 = this.helper21(handler);
            if (var7 == -1) {
               this.helper10();
               return true;
            } else {
               int var10 = this.helper36(handler, this::helper27);
               if (var10 != -1) {
                  return this.helper40(handler, var10, var7);
               } else {
                  if (this.helper6(handler)) {
                     this.helper54();
                     return true;
                  }

                  this.helper10();
                  return true;
               }
            }
         case FUEL:
            int var6 = this.helper36(handler, stack -> stack.isOf(Items.BLAZE_POWDER));
            if (var6 != -1) {
               return this.helper40(handler, var6, 4);
            } else {
               if (this.helper6(handler)) {
                  this.helper54();
                  return true;
               }

               this.helper10();
               return true;
            }
         case INGREDIENT:
            RegistryEntry var3 = this.helper23(this.helper19(handler));
            Item var4 = this.helper25(var3);
            if (var4 == null) {
               this.helper10();
               return true;
            } else {
               int var5 = this.helper36(handler, stack -> stack.isOf(var4));
               if (var5 != -1) {
                  return this.helper40(handler, var5, 3);
               } else {
                  if (this.helper6(handler)) {
                     this.helper54();
                     return true;
                  }

                  this.helper10();
                  return true;
               }
            }
         default:
            return false;
      }
   }

   private boolean helper2(GenericContainerScreenHandler handler) {
      return switch (this.action2) {
         case WITHDRAW -> this.helper3(handler);
         case DEPOSIT -> this.helper4(handler);
         default -> false;
      };
   }

   private boolean helper3(GenericContainerScreenHandler handler) {
      AutoBrew.WithdrawRequest var2 = this.helper49();
      if (var2 == null) {
         this.helper11();
         this.helper54();
         return true;
      }

      if (this.helper48()) {
         this.helper8();
         return true;
      }

      var2 = this.helper49();
      if (var2 == null) {
         this.helper8();
         return true;
      }

      if (this.helper35()) {
         int var3 = this.helper34();
         if (this.flag) {
            if (var3 >= this.index3) {
               this.flag = false;
               this.index3 = 0;
               if (this.helper9()) {
                  this.helper54();
                  return true;
               }

               this.helper11();
               this.helper54();
               return true;
            }

            this.flag = false;
            this.index3 = 0;
         }

         int var4 = this.helper39(handler, this::helper29);
         if (var4 != -1) {
            this.index3 = var3;
            this.flag = true;
            this.helper42(handler.syncId, var4);
            return true;
         }
      }

      this.flag = false;
      this.index3 = 0;
      int var6 = this.helper37(handler, var2::helper);
      if (var6 == -1) {
         var6 = this.helper38(handler);
      }

      if (var6 != -1) {
         this.helper42(handler.syncId, var6);
         return true;
      } else if (this.helper9()) {
         this.helper54();
         return true;
      } else {
         this.helper11();
         this.helper54();
         return true;
      }
   }

   private boolean helper4(GenericContainerScreenHandler handler) {
      if (!this.helper33()) {
         this.helper11();
         this.helper54();
         return true;
      }

      int var2 = this.helper34();
      if (this.flag) {
         if (var2 >= this.index3) {
            this.flag = false;
            this.index3 = 0;
            if (this.helper9()) {
               this.helper54();
               return true;
            }

            this.helper11();
            this.helper54();
            return true;
         }

         this.flag = false;
         this.index3 = 0;
      }

      int var3 = this.helper39(handler, this::helper29);
      if (var3 == -1) {
         this.helper11();
         this.helper54();
         return true;
      } else {
         this.index3 = var2;
         this.flag = true;
         this.helper42(handler.syncId, var3);
         return true;
      }
   }

   private boolean helper5() {
      if (this.action2 == AutoBrew.Action.OPEN_STAND && this.blockPos != null) {
         if (!(mc.world.getBlockEntity(this.blockPos) instanceof BrewingStandBlockEntity)) {
            this.helper11();
            return false;
         } else if (this.helper56(this.blockPos)) {
            this.helper10();
            return false;
         } else {
            return !this.helper61(this.blockPos) ? false : this.helper53(this.blockPos);
         }
      } else if ((this.action2 == AutoBrew.Action.WITHDRAW || this.action2 == AutoBrew.Action.DEPOSIT) && this.blockPos2 != null) {
         BlockState var1 = mc.world.getBlockState(this.blockPos2);
         if (!(var1.getBlock() instanceof ChestBlock)) {
            if (this.helper9()) {
               return false;
            }

            this.helper11();
            return false;
         } else {
            return !this.helper61(this.blockPos2) ? false : this.helper53(this.blockPos2);
         }
      } else {
         return false;
      }
   }

   private boolean helper6(BrewingStandScreenHandler handler) {
      if (!this.autoOpenSetting.isState()) {
         return false;
      } else {
         this.autoBrews.clear();
         this.index2 = 0;
         this.helper43(handler);
         this.helper47();
         if (this.autoBrews.isEmpty()) {
            return false;
         } else {
            this.helper50(this.blockPos != null ? this.blockPos : mc.player.getBlockPos());
            if (this.blockPoss.isEmpty()) {
               this.autoBrews.clear();
               return false;
            } else {
               this.action2 = AutoBrew.Action.WITHDRAW;
               this.blockPos2 = this.blockPoss.get(0);
               this.index = 0;
               this.flag = false;
               this.index3 = 0;
               return true;
            }
         }
      }
   }

   private boolean helper7(BlockPos reference) {
      if (!this.autoOpenSetting.isState()) {
         return false;
      }

      this.helper50(reference != null ? reference : mc.player.getBlockPos());
      if (this.blockPoss.isEmpty()) {
         return false;
      }

      this.action2 = AutoBrew.Action.DEPOSIT;
      this.blockPos = null;
      this.blockPos2 = this.blockPoss.get(0);
      this.index = 0;
      this.autoBrews.clear();
      this.index2 = 0;
      this.flag = false;
      this.index3 = 0;
      return true;
   }

   private void helper8() {
      this.action2 = AutoBrew.Action.OPEN_STAND;
      this.blockPos2 = null;
      this.blockPoss.clear();
      this.index = 0;
      this.autoBrews.clear();
      this.index2 = 0;
      this.flag = false;
      this.index3 = 0;
      this.pendingMove = null;
      this.pendingOpen = null;
      this.helper54();
   }

   private boolean helper9() {
      this.index++;
      if (this.index >= this.blockPoss.size()) {
         this.blockPos2 = null;
         return false;
      } else {
         this.blockPos2 = this.blockPoss.get(this.index);
         return true;
      }
   }

   private void helper10() {
      this.action2 = AutoBrew.Action.NONE;
      this.blockPos = null;
      this.autoBrews.clear();
      this.index2 = 0;
      this.blockPoss.clear();
      this.index = 0;
      this.blockPos2 = null;
      this.flag = false;
      this.index3 = 0;
      this.pendingMove = null;
      this.pendingOpen = null;
      if (this.autoOpenSetting.isState()) {
         this.helper54();
      }
   }

   private void helper11() {
      this.action2 = AutoBrew.Action.NONE;
      this.blockPos = null;
      this.blockPos2 = null;
      this.blockPoss.clear();
      this.index = 0;
      this.autoBrews.clear();
      this.index2 = 0;
      this.flag = false;
      this.index3 = 0;
      this.pendingMove = null;
      this.pendingOpen = null;
   }

   private AutoBrew.StandTask helper12() {
      BlockPos var1 = BlockPos.ofFloored(mc.player.getPos());
      int var2 = (int)Math.ceil(this.radiusSetting.get());
      AutoBrew.StandTask var3 = null;

      for (int var4 = -var2; var4 <= var2; var4++) {
         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               BlockPos var7 = var1.add(var4, var5, var6);
               if (mc.world.getBlockEntity(var7) instanceof BrewingStandBlockEntity var9 && !this.helper56(var7)) {
                  double var10 = var7.getSquaredDistance(var1);
                  if (!(var10 > this.radiusSetting.get() * this.radiusSetting.get())) {
                     AutoBrew.StandMode var12 = this.helper15(var9);
                     if (var12 != AutoBrew.StandMode.WAIT && (var3 == null || this.helper13(var12, var10, var3))) {
                        var3 = new AutoBrew.StandTask(var7.toImmutable(), var12, var10);
                     }
                  }
               }
            }
         }
      }

      return var3;
   }

   private boolean helper13(AutoBrew.StandMode mode, double distance, AutoBrew.StandTask currentBest) {
      int var5 = this.helper14(currentBest.VOLUME2);
      int var6 = this.helper14(mode);
      return var6 < var5 || var6 == var5 && distance < currentBest.INDEX;
   }

   private int helper14(AutoBrew.StandMode mode) {
      return switch (mode) {
         case WAIT -> 5;
         case COLLECT -> 0;
         case CLEAR -> 1;
         case FILL_WATER -> 3;
         case FUEL -> 2;
         case INGREDIENT -> 4;
      };
   }

   private AutoBrew.StandMode helper15(BrewingStandBlockEntity stand) {
      return this.helper17(this.helper18(stand), stand.getStack(3), stand.getStack(4).isEmpty());
   }

   private AutoBrew.StandMode helper16(BrewingStandScreenHandler handler) {
      if (handler.getBrewTime() <= 0 && !this.helper56(this.blockPos)) {
         boolean var2 = handler.getSlot(4).getStack().isEmpty() && handler.getFuel() == 0;
         return this.helper17(this.helper19(handler), handler.getSlot(3).getStack(), var2);
      } else {
         return AutoBrew.StandMode.WAIT;
      }
   }

   private AutoBrew.StandMode helper17(ItemStack[] potions, ItemStack ingredient, boolean needsFuel) {
      int var4 = this.helper20(potions);
      int var5 = 3 - var4;
      RegistryEntry var6 = this.helper23(potions);
      if (var4 > 0 && var6 == null) {
         return AutoBrew.StandMode.CLEAR;
      }

      if (var4 > 0 && this.helper26(var6)) {
         return AutoBrew.StandMode.COLLECT;
      }

      if (var4 == 0) {
         if (!ingredient.isEmpty()) {
            return AutoBrew.StandMode.CLEAR;
         } else {
            return needsFuel ? AutoBrew.StandMode.FUEL : AutoBrew.StandMode.FILL_WATER;
         }
      } else {
         Item var7 = this.helper25(var6);
         if (!ingredient.isEmpty()) {
            if (var7 != null && ingredient.isOf(var7)) {
               return needsFuel ? AutoBrew.StandMode.FUEL : AutoBrew.StandMode.WAIT;
            } else {
               return AutoBrew.StandMode.CLEAR;
            }
         } else if (needsFuel) {
            return AutoBrew.StandMode.FUEL;
         } else if (this.helper28(var6, Potions.WATER) && var5 > 0) {
            return AutoBrew.StandMode.FILL_WATER;
         } else {
            return var7 != null ? AutoBrew.StandMode.INGREDIENT : AutoBrew.StandMode.CLEAR;
         }
      }
   }

   private ItemStack[] helper18(BrewingStandBlockEntity stand) {
      return new ItemStack[]{stand.getStack(0), stand.getStack(1), stand.getStack(2)};
   }

   private ItemStack[] helper19(BrewingStandScreenHandler handler) {
      return new ItemStack[]{handler.getSlot(0).getStack(), handler.getSlot(1).getStack(), handler.getSlot(2).getStack()};
   }

   private int helper20(ItemStack[] stacks) {
      int var2 = 0;

      for (ItemStack var6 : stacks) {
         if (!var6.isEmpty()) {
            var2++;
         }
      }

      return var2;
   }

   private int resolveInt(BrewingStandScreenHandler handler) {
      int var2 = 0;

      for (int var3 = 0; var3 < 3; var3++) {
         if (handler.getSlot(var3).getStack().isEmpty()) {
            var2++;
         }
      }

      return var2;
   }

   private int helper21(BrewingStandScreenHandler handler) {
      for (int var2 = 0; var2 < 3; var2++) {
         if (handler.getSlot(var2).getStack().isEmpty()) {
            return var2;
         }
      }

      return -1;
   }

   private int helper22(BrewingStandScreenHandler handler) {
      if (!handler.getSlot(3).getStack().isEmpty()) {
         return 3;
      }

      for (int var2 = 0; var2 < 3; var2++) {
         if (!handler.getSlot(var2).getStack().isEmpty()) {
            return var2;
         }
      }

      return -1;
   }

   private RegistryEntry<Potion> helper23(ItemStack[] stacks) {
      RegistryEntry var2 = null;
      boolean var3 = false;

      for (ItemStack var7 : stacks) {
         if (!var7.isEmpty()) {
            RegistryEntry var8 = this.helper24(var7);
            if (var8 == null) {
               return null;
            }

            if (!var3) {
               var2 = var8;
               var3 = true;
            } else if (!this.helper28(var2, var8)) {
               return null;
            }
         }
      }

      return var2;
   }

   private RegistryEntry<Potion> helper24(ItemStack stack) {
      if (!stack.isOf(Items.POTION)) {
         return null;
      }

      PotionContentsComponent var2 = stack.get(DataComponentTypes.POTION_CONTENTS);
      return var2 != null && !var2.potion().isEmpty() ? var2.potion().get() : null;
   }

   private Item helper25(RegistryEntry<Potion> potionEntry) {
      if (this.helper28(potionEntry, Potions.WATER)) {
         return Items.NETHER_WART;
      }

      if (this.helper28(potionEntry, Potions.AWKWARD)) {
         if (this.potionSetting.is("Strength")) {
            return Items.BLAZE_POWDER;
         }

         if (this.potionSetting.is("Speed")) {
            return Items.SUGAR;
         }

         if (this.potionSetting.is("Fire Resistance")) {
            return Items.MAGMA_CREAM;
         }

         if (this.potionSetting.is("Invisibility")) {
            return Items.GOLDEN_CARROT;
         }
      }

      if (this.potionSetting.is("Invisibility") && this.helper28(potionEntry, Potions.NIGHT_VISION)) {
         return Items.FERMENTED_SPIDER_EYE;
      }

      if (this.improveSetting.isState()) {
         if (this.potionSetting.is("Strength") && this.helper28(potionEntry, Potions.STRENGTH)) {
            return Items.GLOWSTONE_DUST;
         }

         if (this.potionSetting.is("Speed") && this.helper28(potionEntry, Potions.SWIFTNESS)) {
            return Items.GLOWSTONE_DUST;
         }

         if (this.potionSetting.is("Fire Resistance") && this.helper28(potionEntry, Potions.FIRE_RESISTANCE)) {
            return Items.REDSTONE;
         }

         if (this.potionSetting.is("Invisibility") && this.helper28(potionEntry, Potions.INVISIBILITY)) {
            return Items.REDSTONE;
         }
      }

      return null;
   }

   private boolean helper26(RegistryEntry<Potion> potionEntry) {
      if (potionEntry == null) {
         return false;
      } else if (this.potionSetting.is("Strength")) {
         return this.helper28(potionEntry, this.improveSetting.isState() ? Potions.STRONG_STRENGTH : Potions.STRENGTH);
      } else if (this.potionSetting.is("Speed")) {
         return this.helper28(potionEntry, this.improveSetting.isState() ? Potions.STRONG_SWIFTNESS : Potions.SWIFTNESS);
      } else {
         return this.potionSetting.is("Fire Resistance")
            ? this.helper28(potionEntry, this.improveSetting.isState() ? Potions.LONG_FIRE_RESISTANCE : Potions.FIRE_RESISTANCE)
            : this.helper28(potionEntry, this.improveSetting.isState() ? Potions.LONG_INVISIBILITY : Potions.INVISIBILITY);
      }
   }

   private boolean helper27(ItemStack stack) {
      return this.helper28(this.helper24(stack), Potions.WATER);
   }

   private boolean helper28(RegistryEntry<Potion> first, RegistryEntry<Potion> second) {
      return first != null && second != null && first.value() == second.value();
   }

   private boolean helper29(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      }

      if (stack.isOf(Items.GLASS_BOTTLE)) {
         return true;
      }

      RegistryEntry var2 = this.helper24(stack);
      return var2 != null
         ? this.helper26(var2) || !this.helper30(var2)
         : this.helper31(stack) && !this.helper32(stack.getItem());
   }

   private boolean helper30(RegistryEntry<Potion> potionEntry) {
      if (potionEntry == null) {
         return false;
      } else if (this.helper28(potionEntry, Potions.WATER) || this.helper28(potionEntry, Potions.AWKWARD)) {
         return true;
      } else if (this.potionSetting.is("Strength")) {
         return this.helper28(potionEntry, Potions.STRENGTH);
      } else if (this.potionSetting.is("Speed")) {
         return this.helper28(potionEntry, Potions.SWIFTNESS);
      } else {
         return this.potionSetting.is("Fire Resistance")
            ? this.helper28(potionEntry, Potions.FIRE_RESISTANCE)
            : this.helper28(potionEntry, Potions.NIGHT_VISION) || this.helper28(potionEntry, Potions.INVISIBILITY);
      }
   }

   private boolean helper31(ItemStack stack) {
      Item var2 = stack.getItem();
      return var2 == Items.BLAZE_POWDER
         || var2 == Items.NETHER_WART
         || var2 == Items.SUGAR
         || var2 == Items.MAGMA_CREAM
         || var2 == Items.GOLDEN_CARROT
         || var2 == Items.FERMENTED_SPIDER_EYE
         || var2 == Items.GLOWSTONE_DUST
         || var2 == Items.REDSTONE;
   }

   private boolean helper32(Item item) {
      if (item == Items.BLAZE_POWDER || item == Items.NETHER_WART) {
         return true;
      } else if (item == Items.SUGAR) {
         return this.potionSetting.is("Speed");
      } else if (item == Items.MAGMA_CREAM) {
         return this.potionSetting.is("Fire Resistance");
      } else if (item == Items.GOLDEN_CARROT || item == Items.FERMENTED_SPIDER_EYE) {
         return this.potionSetting.is("Invisibility");
      } else if (item == Items.GLOWSTONE_DUST) {
         return this.improveSetting.isState() && (this.potionSetting.is("Strength") || this.potionSetting.is("Speed"));
      } else {
         return item != Items.REDSTONE
            ? false
            : this.improveSetting.isState() && (this.potionSetting.is("Fire Resistance") || this.potionSetting.is("Invisibility"));
      }
   }

   private boolean helper33() {
      return this.helper34() > 0;
   }

   private int helper34() {
      int var1 = 0;
      int var2 = Math.min(36, mc.player.getInventory().size());

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (this.helper29(var4)) {
            var1 += var4.getCount();
         }
      }

      return var1;
   }

   private int resolveInt2(Predicate<ItemStack> filter) {
      int var2 = 0;
      int var3 = Math.min(36, mc.player.getInventory().size());

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && filter.test(var5)) {
            var2 += var5.getCount();
         }
      }

      return var2;
   }

   private boolean helper35() {
      int var1 = Math.min(36, mc.player.getInventory().size());

      for (int var2 = 0; var2 < var1; var2++) {
         if (mc.player.getInventory().getStack(var2).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private int helper36(BrewingStandScreenHandler handler, Predicate<ItemStack> filter) {
      for (int var3 = 5; var3 < handler.slots.size(); var3++) {
         ItemStack var4 = handler.getSlot(var3).getStack();
         if (!var4.isEmpty() && filter.test(var4)) {
            return var3;
         }
      }

      return -1;
   }

   private int helper37(GenericContainerScreenHandler handler, Predicate<ItemStack> filter) {
      int var3 = handler.getInventory().size();

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = handler.getSlot(var4).getStack();
         if (!var5.isEmpty() && filter.test(var5)) {
            return var4;
         }
      }

      return -1;
   }

   private int helper38(GenericContainerScreenHandler handler) {
      for (int var2 = this.index2; var2 < this.autoBrews.size(); var2++) {
         AutoBrew.WithdrawRequest var3 = this.autoBrews.get(var2);
         if (this.resolveInt2(var3::helper) < var3.amount()) {
            int var4 = this.helper37(handler, var3::helper);
            if (var4 != -1) {
               return var4;
            }
         }
      }

      return -1;
   }

   private int helper39(GenericContainerScreenHandler handler, Predicate<ItemStack> filter) {
      int var3 = handler.getInventory().size();

      for (int var4 = var3; var4 < handler.slots.size(); var4++) {
         ItemStack var5 = handler.getSlot(var4).getStack();
         if (!var5.isEmpty() && filter.test(var5)) {
            return var4;
         }
      }

      return -1;
   }

   private boolean helper40(BrewingStandScreenHandler handler, int fromSlot, int toSlot) {
      if (this.pendingMove == null
         || this.pendingMove.VOLUME != handler.syncId
         || this.pendingMove.VOLUME2 != fromSlot
         || this.pendingMove.INDEX != toSlot) {
         this.pendingMove = new AutoBrew.PendingMove(handler.syncId, fromSlot, toSlot, 0);
      }

      return this.helper41(handler);
   }

   private boolean helper41(BrewingStandScreenHandler handler) {
      if (this.pendingMove == null) {
         return false;
      }

      if (handler != null && handler.syncId == this.pendingMove.VOLUME) {
         switch (this.pendingMove.INDEX2) {
            case 0:
               mc.interactionManager.clickSlot(handler.syncId, this.pendingMove.VOLUME2, 0, SlotActionType.PICKUP, mc.player);
               this.pendingMove = this.pendingMove.helper();
               this.timestamp = this.helper66();
               return true;
            case 1:
               mc.interactionManager.clickSlot(handler.syncId, this.pendingMove.INDEX, 1, SlotActionType.PICKUP, mc.player);
               this.pendingMove = this.pendingMove.helper();
               this.timestamp = this.helper66();
               return true;
            case 2:
               if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                  mc.interactionManager.clickSlot(handler.syncId, this.pendingMove.VOLUME2, 0, SlotActionType.PICKUP, mc.player);
               }

               this.pendingMove = null;
               this.timestamp = this.helper66();
               return true;
            default:
               this.pendingMove = null;
               return false;
         }
      } else {
         this.pendingMove = null;
         return false;
      }
   }

   private void helper42(int syncId, int slot) {
      this.timestamp = this.helper66();
      mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
   }

   private void helper43(BrewingStandScreenHandler handler) {
      ItemStack[] var2 = this.helper19(handler);
      int var3 = this.helper20(var2);
      int var4 = 3 - var3;
      RegistryEntry var5 = this.helper23(var2);
      boolean var6 = handler.getSlot(4).getStack().isEmpty() && handler.getFuel() == 0;
      if ((var3 == 0 || this.helper28(var5, Potions.WATER)) && var4 > 0) {
         this.helper44(var4);
      }

      if (var6) {
         this.helper45(Items.BLAZE_POWDER, 1);
      }

      if (handler.getSlot(3).getStack().isEmpty()) {
         RegistryEntry var7 = var5;
         if (var7 == null && var3 == 0) {
            var7 = Potions.WATER;
         }

         Item var8 = this.helper25(var7);
         if (var8 != null) {
            this.helper45(var8, 1);
         }
      }
   }

   private void helper44(int amount) {
      this.helper46(new AutoBrew.WithdrawRequest(null, true, amount));
   }

   private void helper45(Item item, int amount) {
      if (item != null) {
         this.helper46(new AutoBrew.WithdrawRequest(item, false, amount));
      }
   }

   private void helper46(AutoBrew.WithdrawRequest request) {
      if (request != null && request.amount() > 0) {
         for (int var2 = 0; var2 < this.autoBrews.size(); var2++) {
            AutoBrew.WithdrawRequest var3 = this.autoBrews.get(var2);
            if (var3.helper2(request)) {
               int var4 = var3.amount() + request.amount();
               this.autoBrews.set(var2, new AutoBrew.WithdrawRequest(var3.item(), var3.waterPotion(), var4));
               return;
            }
         }

         this.autoBrews.add(request);
      }
   }

   private void helper47() {
      for (int var1 = this.autoBrews.size() - 1; var1 >= 0; var1--) {
         AutoBrew.WithdrawRequest var2 = this.autoBrews.get(var1);
         if (this.resolveInt2(var2::helper) >= var2.amount()) {
            this.autoBrews.remove(var1);
         }
      }
   }

   private boolean helper48() {
      while (this.index2 < this.autoBrews.size()) {
         AutoBrew.WithdrawRequest var1 = this.autoBrews.get(this.index2);
         if (this.resolveInt2(var1::helper) < var1.amount()) {
            return false;
         }

         this.index2++;
      }

      return true;
   }

   private AutoBrew.WithdrawRequest helper49() {
      return this.index2 >= 0 && this.index2 < this.autoBrews.size() ? this.autoBrews.get(this.index2) : null;
   }

   private void helper50(BlockPos reference) {
      this.blockPoss.clear();
      int var2 = (int)Math.ceil(this.chestRadiusSetting.get());
      ArrayList<BlockPos> var3 = new ArrayList<>();

      for (int var4 = -var2; var4 <= var2; var4++) {
         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               BlockPos var7 = reference.add(var4, var5, var6);
               BlockState var8 = mc.world.getBlockState(var7);
               if (var8.getBlock() instanceof ChestBlock) {
                  double var9 = var7.getSquaredDistance(reference);
                  if (!(var9 > this.chestRadiusSetting.get() * this.chestRadiusSetting.get())) {
                     BlockPos var11 = this.helper51(var7, var8);
                     if (!var3.contains(var11)) {
                        var3.add(var11);
                     }
                  }
               }
            }
         }
      }

      var3.sort(Comparator.comparingDouble(pos -> pos.getSquaredDistance(reference)));
      this.blockPoss.addAll(var3);
   }

   private BlockPos helper51(BlockPos pos, BlockState state) {
      BlockPos var3 = this.helper52(pos, state);
      if (var3 == null) {
         return pos.toImmutable();
      } else {
         return pos.asLong() <= var3.asLong() ? pos.toImmutable() : var3.toImmutable();
      }
   }

   private BlockPos helper52(BlockPos pos, BlockState state) {
      if (!(state.getBlock() instanceof ChestBlock)) {
         return null;
      }

      ChestType var3 = state.get(ChestBlock.CHEST_TYPE);
      if (var3 == ChestType.SINGLE) {
         return null;
      }

      Direction var4 = switch ((Direction)state.get(ChestBlock.FACING)) {
         case NORTH -> var3 == ChestType.LEFT ? Direction.EAST : Direction.WEST;
         case SOUTH -> var3 == ChestType.LEFT ? Direction.WEST : Direction.EAST;
         case WEST -> var3 == ChestType.LEFT ? Direction.NORTH : Direction.SOUTH;
         case EAST -> var3 == ChestType.LEFT ? Direction.SOUTH : Direction.NORTH;
         default -> null;
      };
      return var4 == null ? null : pos.offset(var4);
   }

   private boolean helper53(BlockPos pos) {
      Vec3d var2 = Vec3d.ofCenter(pos);
      this.pendingOpen = new AutoBrew.PendingOpen(this.action2, pos.toImmutable(), System.currentTimeMillis());
      this.timestamp = this.helper65();
      mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var2, Direction.UP, pos, false));
      return true;
   }

   private void helper54() {
      if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         this.pendingOpen = null;
         this.timestamp = this.helper65();
         mc.player.closeHandledScreen();
      }
   }

   private BlockPos helper55() {
      BlockPos var1 = BlockPos.ofFloored(mc.player.getPos());
      BlockPos var2 = null;
      double var3 = this.radiusSetting.get() * this.radiusSetting.get();
      int var5 = (int)Math.ceil(this.radiusSetting.get());

      for (int var6 = -var5; var6 <= var5; var6++) {
         for (int var7 = -var5; var7 <= var5; var7++) {
            for (int var8 = -var5; var8 <= var5; var8++) {
               BlockPos var9 = var1.add(var6, var7, var8);
               BlockEntity var10 = mc.world.getBlockEntity(var9);
               if (var10 instanceof BrewingStandBlockEntity) {
                  double var11 = var9.getSquaredDistance(var1);
                  if (var11 <= var3) {
                     var3 = var11;
                     var2 = var9.toImmutable();
                  }
               }
            }
         }
      }

      return var2;
   }

   private boolean helper56(BlockPos pos) {
      if (pos == null) {
         return false;
      }

      this.helper58();
      Long var2 = this.blockPoss2.get(pos);
      return var2 != null && var2 > System.currentTimeMillis();
   }

   private void helper57(BrewingStandScreenHandler handler) {
      if (handler != null && this.blockPos != null && handler.getBrewTime() > 0) {
         this.blockPoss2.put(this.blockPos.toImmutable(), System.currentTimeMillis() + handler.getBrewTime() * 50L + 150L);
      }
   }

   private void helper58() {
      if (!this.blockPoss2.isEmpty()) {
         long var1 = System.currentTimeMillis();
         this.blockPoss2.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue() <= var1);
      }
   }

   private void helper59() {
      BlockPos var1 = this.helper60();
      if (var1 != null && RotationStorage.instance != null) {
         Vec2f var2 = RotationUtils.getRotations(this.helper62(var1));
         RotationStorage.update(new Rotation(var2.x, var2.y), 120.0F, 120.0F, 120.0F, 120.0F, 4, 0, false);
      }
   }

   private BlockPos helper60() {
      if (mc.player.currentScreenHandler instanceof BrewingStandScreenHandler && this.blockPos != null) {
         return this.blockPos;
      }

      if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)
         || mc.player.currentScreenHandler == mc.player.playerScreenHandler
         || this.blockPos2 == null
         || this.action2 != AutoBrew.Action.WITHDRAW && this.action2 != AutoBrew.Action.DEPOSIT) {
         if (this.action2 == AutoBrew.Action.OPEN_STAND && this.blockPos != null) {
            return this.blockPos;
         } else {
            return (this.action2 == AutoBrew.Action.WITHDRAW || this.action2 == AutoBrew.Action.DEPOSIT) && this.blockPos2 != null
               ? this.blockPos2
               : null;
         }
      } else {
         return this.blockPos2;
      }
   }

   private boolean helper61(BlockPos pos) {
      Vec2f var2 = RotationUtils.getRotations(this.helper62(pos));
      return new Rotation(mc.player).getDelta(new Rotation(var2.x, var2.y)) <= 8.0F;
   }

   private Vec3d helper62(BlockPos pos) {
      return Vec3d.ofCenter(pos, 0.5);
   }

   private void helper63() {
      this.timestamp = 0L;
      this.text = "";
      this.timestamp2 = System.currentTimeMillis();
      this.pendingMove = null;
      this.pendingOpen = null;
   }

   private long helper64() {
      return Math.round(100.0F);
   }

   private long helper65() {
      return Math.round(150.0F);
   }

   private long helper66() {
      return Math.round(100.0F);
   }

   private long resolveLong() {
      return Math.round(250.0F);
   }

   private long resolveLong2() {
      return Math.round(500.0F);
   }

   private boolean helper67() {
      if (this.pendingOpen == null) {
         return false;
      }

      if (this.helper68(this.pendingOpen.action())) {
         this.pendingOpen = null;
         return false;
      }

      if (mc.player.currentScreenHandler == mc.player.playerScreenHandler && mc.currentScreen == null) {
         return System.currentTimeMillis() - this.pendingOpen.startedAtMs() < this.resolveLong();
      }

      this.pendingOpen = null;
      return false;
   }

   private boolean helper68(AutoBrew.Action expectedAction) {
      if (expectedAction == AutoBrew.Action.OPEN_STAND) {
         return mc.player.currentScreenHandler instanceof BrewingStandScreenHandler;
      } else {
         return expectedAction != AutoBrew.Action.WITHDRAW && expectedAction != AutoBrew.Action.DEPOSIT
            ? false
            : mc.player.currentScreenHandler instanceof GenericContainerScreenHandler && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
      }
   }

   private boolean helper69() {
      if (!this.helper70()) {
         this.text = "";
         this.timestamp2 = System.currentTimeMillis();
         return false;
      }

      String var1 = this.helper71();
      long var2 = System.currentTimeMillis();
      if (!var1.equals(this.text)) {
         this.text = var1;
         this.timestamp2 = var2;
         return false;
      }

      if (var2 - this.timestamp2 < this.resolveLong2()) {
         return false;
      }

      this.helper73();
      this.text = "";
      this.timestamp2 = var2;
      this.timestamp = this.helper64();
      this.timerUtils.reset();
      return true;
   }

   private boolean helper70() {
      return this.pendingMove != null
         || this.pendingOpen != null
         || this.action2 != AutoBrew.Action.NONE
         || this.blockPos != null
         || this.blockPos2 != null;
   }

   private String helper71() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.action2)
         .append('|')
         .append(this.blockPos == null ? 0L : this.blockPos.asLong())
         .append('|')
         .append(this.blockPos2 == null ? 0L : this.blockPos2.asLong())
         .append('|')
         .append(this.index)
         .append('|')
         .append(this.index2)
         .append('|')
         .append(this.flag)
         .append('|')
         .append(this.index3)
         .append('|')
         .append(this.helper34());
      if (this.pendingMove != null) {
         var1.append("|pm:")
            .append(this.pendingMove.VOLUME)
            .append(':')
            .append(this.pendingMove.VOLUME2)
            .append(':')
            .append(this.pendingMove.INDEX)
            .append(':')
            .append(this.pendingMove.INDEX2);
      }

      if (this.pendingOpen != null) {
         var1.append("|po:").append(this.pendingOpen.action()).append(':').append(this.pendingOpen.pos().asLong());
      }

      if (mc.player.currentScreenHandler instanceof BrewingStandScreenHandler var2) {
         var1.append("|brew:").append(var2.syncId).append(':').append(var2.getBrewTime()).append(':').append(var2.getFuel());

         for (int var5 = 0; var5 <= 4; var5++) {
            this.helper72(var1, var2.getSlot(var5).getStack());
         }
      } else if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var3
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         var1.append("|chest:").append(var3.syncId).append(':').append(var3.getInventory().size());
         AutoBrew.WithdrawRequest var7 = this.helper49();
         if (var7 != null) {
            var1.append(':').append(this.resolveInt2(var7::helper)).append('/').append(var7.amount());
         }
      } else {
         var1.append("|world:");
      }

      this.helper72(var1, mc.player.currentScreenHandler.getCursorStack());
      return var1.toString();
   }

   private void helper72(StringBuilder snapshot, ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         snapshot.append('|').append(Item.getRawId(stack.getItem())).append(':').append(stack.getCount());
      } else {
         snapshot.append("|0");
      }
   }

   private void helper73() {
      this.pendingMove = null;
      this.flag = false;
      this.index3 = 0;
      if (this.action2 != AutoBrew.Action.WITHDRAW && this.action2 != AutoBrew.Action.DEPOSIT) {
         this.helper54();
         this.helper10();
      } else {
         this.helper54();
         if (!this.helper9()) {
            this.helper11();
         }
      }
   }

   private enum Action {
      NONE,
      OPEN_STAND,
      WITHDRAW,
      DEPOSIT;
   }

   private static final class PendingMove {
      private final int VOLUME;
      private final int VOLUME2;
      private final int INDEX;
      private final int INDEX2;

      private PendingMove(int syncId, int fromSlot, int toSlot, int step) {
         this.VOLUME = syncId;
         this.VOLUME2 = fromSlot;
         this.INDEX = toSlot;
         this.INDEX2 = step;
      }

      private AutoBrew.PendingMove helper() {
         return new AutoBrew.PendingMove(this.VOLUME, this.VOLUME2, this.INDEX, this.INDEX2 + 1);
      }
   }

   private record PendingOpen(AutoBrew.Action action, BlockPos pos, long startedAtMs) {

      private PendingOpen(AutoBrew.Action action, BlockPos pos, long startedAtMs) {
         this.action = action;
         this.pos = pos;
         this.startedAtMs = startedAtMs;
      }

      public AutoBrew.Action action() {
         return this.action;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public long startedAtMs() {
         return this.startedAtMs;
      }
   }

   private enum StandMode {
      WAIT,
      COLLECT,
      CLEAR,
      FILL_WATER,
      FUEL,
      INGREDIENT;
   }

   private static final class StandTask {
      private final BlockPos VOLUME;
      private final AutoBrew.StandMode VOLUME2;
      private final double INDEX;

      private StandTask(BlockPos pos, AutoBrew.StandMode mode, double distance) {
         this.VOLUME = pos;
         this.VOLUME2 = mode;
         this.INDEX = distance;
      }
   }

   private record WithdrawRequest(Item item, boolean waterPotion, int amount) {

      private WithdrawRequest(Item item, boolean waterPotion, int amount) {
         this.item = item;
         this.waterPotion = waterPotion;
         this.amount = amount;
      }

      private boolean helper(ItemStack stack) {
         return this.waterPotion ? AutoBrew.INSTANCE.helper27(stack) : stack.isOf(this.item);
      }

      private boolean helper2(AutoBrew.WithdrawRequest other) {
         return other != null && this.waterPotion == other.waterPotion && this.item == other.item;
      }

      public Item item() {
         return this.item;
      }

      public boolean waterPotion() {
         return this.waterPotion;
      }

      public int amount() {
         return this.amount;
      }
   }
}
