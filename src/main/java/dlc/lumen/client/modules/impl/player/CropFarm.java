package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.player.RegionSelection;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.potion.Potions;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;

public abstract class CropFarm extends Module {
   private static final int INDEX = 20;
   private static final int INDEX2 = 53;
   private static final int INDEX3 = 4;
   protected final BooleanSetting autoSell = new BooleanSetting("Продавать", true);
   protected final TextSetting sellCommand = new TextSetting("Команда скупщика", "buyer", 32).visible(this.autoSell::isState);
   protected final TextSetting sellTitle = new TextSetting("Название скупщика", "Скупщик", 32).visible(this.autoSell::isState);
   protected final BooleanSetting autoEat = new BooleanSetting("Есть", true);
   protected final BooleanSetting autoInvisible = new BooleanSetting("Невидимость", false);
   protected final FloatSetting scanRadius = new FloatSetting("Радиус поиска", 16.0F, 4.0F, 48.0F, 1.0F);
   protected final FloatSetting rotationSpeed = new FloatSetting("Скорость поворота", 25.0F, 5.0F, 180.0F, 1.0F);
   private final TimerUtils timerUtils = new TimerUtils();
   private final TimerUtils timerUtils2 = new TimerUtils();
   private final TimerUtils timerUtils3 = new TimerUtils();
   private final TimerUtils timerUtils4 = new TimerUtils();
   private final Set<Integer> integers = new HashSet<>();
   private int index = -1;
   private boolean flag;

   protected CropFarm(String name, String description) {
      super(name, description, Module.ModuleCategory.PLAYER);
      this.addSettings(this.autoSell, this.sellCommand, this.sellTitle, this.autoEat, this.autoInvisible, this.scanRadius, this.rotationSpeed);
   }

   protected abstract Block crop();

   protected abstract int age(BlockState var1);

   protected abstract int matureAge();

   protected abstract Item seed();

   protected abstract boolean isSoil(BlockState var1);

   protected abstract boolean harvestByUse();

   @Override
   public void onEnable() {
      this.integers.clear();
      this.index = -1;
      this.flag = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.helper8();
      this.helper9();
      this.integers.clear();
      this.flag = false;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      this.flag = false;
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!this.autoSell.isState() || !this.helper()) {
            if (mc.currentScreen == null) {
               if (!RegionSelection.isComplete()) {
                  if (this.timerUtils4.finished(4000L)) {
                     this.timerUtils4.reset();
                     ChatUtils.sendMessage("Отметьте область: .region pos1 и .region pos2");
                  }
               } else if (!this.autoEat.isState() || !this.helper4()) {
                  if (!this.autoInvisible.isState() || !this.helper5()) {
                     this.helper8();
                     this.helper9();
                     this.helper11();
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if (this.flag) {
         event.setForward(1.0F);
      }
   }

   private boolean helper() {
      boolean var1 = this.helper2();
      if (!var1 && this.helper3() < 20) {
         this.integers.clear();
         return false;
      }

      if (!this.timerUtils.finished(50L)) {
         return true;
      }

      this.timerUtils.reset();
      if (!var1) {
         if (mc.currentScreen == null && this.timerUtils2.finished(3000L) && mc.getNetworkHandler() != null) {
            this.timerUtils2.reset();
            this.integers.clear();
            mc.getNetworkHandler().sendCommand(this.sellCommand.get());
         }

         return true;
      } else {
         ScreenHandler var2 = mc.player.currentScreenHandler;
         int var3 = ((GenericContainerScreenHandler)var2).getInventory().size();

         for (Slot var5 : var2.slots) {
            if (var5.id >= var3 && !this.integers.contains(var5.id) && var5.getStack().isOf(this.seed())) {
               this.integers.add(var5.id);
               mc.interactionManager.clickSlot(var2.syncId, var5.id, 0, SlotActionType.QUICK_MOVE, mc.player);
               return true;
            }
         }

         mc.interactionManager.clickSlot(var2.syncId, 53, 0, SlotActionType.QUICK_MOVE, mc.player);
         mc.player.closeHandledScreen();
         this.integers.clear();
         return true;
      }
   }

   private boolean helper2() {
      return mc.currentScreen instanceof GenericContainerScreen var1
         && var1.getTitle().getString().contains(this.sellTitle.get())
         && mc.player.currentScreenHandler instanceof GenericContainerScreenHandler;
   }

   private int helper3() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (mc.player.getInventory().getStack(var2).isOf(this.seed())) {
            var1++;
         }
      }

      return var1;
   }

   private boolean helper4() {
      if (mc.player.getHungerManager().getFoodLevel() > 18) {
         return false;
      }

      int var1 = this.helper10(stack -> stack.getUseAction() == UseAction.EAT);
      if (var1 == -1) {
         return false;
      }

      this.helper7(var1);
      return true;
   }

   private boolean helper5() {
      if (mc.player.isInvisible()) {
         return false;
      }

      int var1 = this.helper10(CropFarm::helper6);
      if (var1 == -1) {
         return false;
      }

      this.helper7(var1);
      return true;
   }

   private static boolean helper6(ItemStack stack) {
      PotionContentsComponent var1 = stack.get(DataComponentTypes.POTION_CONTENTS);
      if (var1 == null) {
         return false;
      }

      if (var1.potion().isPresent() && var1.potion().get().value().equals(Potions.INVISIBILITY.value())) {
         return true;
      }

      for (StatusEffectInstance var3 : var1.getEffects()) {
         if (var3.getEffectType().equals(StatusEffects.INVISIBILITY)) {
            return true;
         }
      }

      return false;
   }

   private void helper7(int slot) {
      if (this.index == -1) {
         this.index = mc.player.getInventory().selectedSlot;
      }

      mc.player.getInventory().selectedSlot = slot;
      if (mc.options != null) {
         mc.options.useKey.setPressed(true);
      }
   }

   private void helper8() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }
   }

   private void helper9() {
      if (this.index >= 0 && this.index <= 8 && mc.player != null) {
         mc.player.getInventory().selectedSlot = this.index;
      }

      this.index = -1;
   }

   private int helper10(Predicate<ItemStack> predicate) {
      for (int var2 = 0; var2 < 9; var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && predicate.test(var3)) {
            return var2;
         }
      }

      return -1;
   }

   private void helper11() {
      BlockPos var1 = this.helper14(true);
      BlockPos var2 = var1 != null ? var1 : this.helper14(false);
      if (var2 != null) {
         boolean var3 = var1 != null;
         Vec3d var4 = var3 ? Vec3d.ofCenter(var2) : Vec3d.ofCenter(var2).add(0.0, 0.5, 0.0);
         this.helper16(var4);
         double var5 = mc.player.getBlockInteractionRange();
         if (mc.player.getEyePos().squaredDistanceTo(var4) > var5 * var5) {
            this.flag = true;
         } else if (this.timerUtils3.finished(var3 && !this.harvestByUse() ? 0L : 120L)) {
            this.timerUtils3.reset();
            BlockHitResult var7 = new BlockHitResult(var4, Direction.UP, var2, false);
            if (var3) {
               this.helper12(var2, var7);
            } else {
               this.helper13(var7);
            }
         }
      }
   }

   private void helper12(BlockPos pos, BlockHitResult hit) {
      if (this.harvestByUse()) {
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
      } else {
         mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
      }

      mc.player.swingHand(Hand.MAIN_HAND);
   }

   private void helper13(BlockHitResult hit) {
      if (!mc.player.getOffHandStack().isOf(this.seed())) {
         int var2 = SwapManager.findInInventory(stack -> stack.isOf(this.seed()));
         if (var2 != -1) {
            SwapManager.swapToOffhand(var2);
         }
      } else {
         mc.interactionManager.interactBlock(mc.player, Hand.OFF_HAND, hit);
         mc.player.swingHand(Hand.OFF_HAND);
      }
   }

   private BlockPos helper14(boolean ripe) {
      int var2 = (int)this.scanRadius.get();
      BlockPos var3 = mc.player.getBlockPos();
      int var4 = Math.max(RegionSelection.minX(), var3.getX() - var2);
      int var5 = Math.min(RegionSelection.maxX(), var3.getX() + var2);
      int var6 = Math.max(RegionSelection.minY(), var3.getY() - var2);
      int var7 = Math.min(RegionSelection.maxY(), var3.getY() + var2);
      int var8 = Math.max(RegionSelection.minZ(), var3.getZ() - var2);
      int var9 = Math.min(RegionSelection.maxZ(), var3.getZ() + var2);
      BlockPos var10 = null;
      double var11 = Double.MAX_VALUE;
      Mutable var13 = new Mutable();

      for (int var14 = var4; var14 <= var5; var14++) {
         for (int var15 = var6; var15 <= var7; var15++) {
            for (int var16 = var8; var16 <= var9; var16++) {
               var13.set(var14, var15, var16);
               if (this.helper15(var13, ripe)) {
                  double var17 = mc.player.squaredDistanceTo(Vec3d.ofCenter(var13));
                  if (var17 < var11) {
                     var11 = var17;
                     var10 = var13.toImmutable();
                  }
               }
            }
         }
      }

      return var10;
   }

   private boolean helper15(BlockPos pos, boolean ripe) {
      BlockState var3 = mc.world.getBlockState(pos);
      return ripe ? var3.isOf(this.crop()) && this.age(var3) == this.matureAge() : this.isSoil(var3) && mc.world.getBlockState(pos.up()).isAir();
   }

   private void helper16(Vec3d target) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = target.x - var2.x;
      double var5 = target.y - var2.y;
      double var7 = target.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)(Math.toDegrees(Math.atan2(var7, var3)) - 90.0);
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      float var13 = this.rotationSpeed.get();
      RotationStorage.update(new Rotation(var11, var12), var13, var13, 0, 4);
   }
}
