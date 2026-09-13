package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import dlc.lumen.mixin.SlotAccessor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.Mutable;

public class AppleFarmer extends Module {
   public static AppleFarmer INSTANCE = new AppleFarmer();

   public AppleFarmer() {
      super("AppleFarmer", "Автоматически фармит яблоки", Module.ModuleCategory.MISC);
   }

   @EventLink
   public void onTick(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.age % 600 < 10) {
            if (mc.player.getMainHandStack().getItem() instanceof AxeItem) {
               mc.player.getInventory().selectedSlot = ThreadLocalRandom.current().nextInt(0, 9);
            }
         } else if (this.checkCondition5(mc.player.getMainHandStack())) {
            BlockPos var2 = this.computeBlockPos(5.0, s -> s.isIn(BlockTags.LEAVES), this::resolveDouble2);
            if (var2 != null) {
               this.updateState(var2, s -> s.getItem() instanceof HoeItem);
            } else {
               BlockPos var3 = this.computeBlockPos(5.0, s -> s.isIn(BlockTags.LOGS), Vec3i::getY);
               if (var3 != null) {
                  this.updateState(var3, s -> s.getItem() instanceof AxeItem);
               } else if (mc.currentScreen instanceof GenericContainerScreen) {
                  this.updateState3();
               } else if (this.resolveInt3(Items.BONE) > 0 && this.resolveInt3(Items.BONE_MEAL) == 0) {
                  this.updateState5();
               } else if (!this.checkState() && !this.checkState2()) {
                  BlockPos var4 = this.computeBlockPos(16.0, s -> s.isIn(BlockTags.DIRT), this::resolveDouble);
                  if (var4 != null) {
                     if (this.resolveInt3(Items.STICK) <= 128 && this.resolveInt3(Items.OAK_SAPLING) <= 128) {
                        this.updateState2(var4);
                     } else {
                        this.updateState6();
                     }
                  }
               } else {
                  this.updateState3();
               }
            }
         }
      }
   }

   private void updateState(BlockPos pos, Predicate<ItemStack> tool) {
      if (!tool.test(mc.player.getMainHandStack())) {
         this.checkCondition(tool);
      } else if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem) || !(mc.player.getAttackCooldownProgress(0.0F) <= 0.15F)) {
         Vec3d var3 = pos.toCenterPos();
         if (this.checkCondition2(var3)) {
            Vec3d var4 = new Box(pos).raycast(mc.player.getEyePos(), var3).orElse(var3);
            mc.interactionManager.updateBlockBreakingProgress(pos, Direction.getFacing(var4.subtract(var3)));
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void updateState2(BlockPos dirt) {
      boolean var2 = mc.world.getBlockState(dirt.up()).getBlock() instanceof SaplingBlock;
      Predicate<ItemStack> var3 = var2
         ? s -> s.isOf(Items.BONE_MEAL)
         : s -> s.getItem() instanceof BlockItem var1 && var1.getBlock() instanceof SaplingBlock;
      if (!var3.test(mc.player.getMainHandStack())) {
         this.checkCondition(var3);
      } else {
         Vec3d var4 = new Vec3d(dirt.getX() + 0.5, dirt.getY() + 1, dirt.getZ() + 0.5);
         if (this.checkCondition2(var4) && mc.player.age % 4 == 0) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var4, Direction.UP, var2 ? dirt.up() : dirt, false));
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void updateState3() {
      if (mc.currentScreen instanceof GenericContainerScreen var2) {
         if (mc.player.age % 50 == 0) {
            mc.player.closeHandledScreen();
         } else if (mc.player.age % 2 == 0) {
            ScreenHandler var6 = var2.getScreenHandler();
            boolean var1;
            if (var2.getTitle().getContent() instanceof TranslatableTextContent var4) {
               var1 = var4.getKey().contains("chest");
            } else {
               var1 = false;
            }

            int var7;
            if (var1) {
               var7 = this.resolveInt(var6, false, 5, s -> s.isOf(Items.APPLE) || s.isOf(Items.OAK_LOG));
            } else {
               var7 = this.checkState2() ? this.resolveInt(var6, true, 3, this::checkCondition4) : 0;
            }

            if (var7 == 0) {
               mc.player.closeHandledScreen();
            }
         }
      } else {
         this.updateState4(this.computeBlockPos(5.0, s -> s.isOf(this.checkState() ? Blocks.CHEST : Blocks.BARREL), this::resolveDouble));
      }
   }

   private void updateState4(BlockPos block) {
      if (block != null && this.checkCondition2(block.toCenterPos()) && mc.player.age % 4 == 0) {
         Vec3d var2 = block.toCenterPos();
         Vec3d var3 = new Box(block).raycast(mc.player.getEyePos(), var2).orElse(var2);
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var3, Direction.getFacing(var3.subtract(var2)), block, false));
      }
   }

   private void updateState5() {
      if (mc.player.age % 2 == 0) {
         PlayerScreenHandler var1 = mc.player.playerScreenHandler;
         if (var1.getSlot(0).getStack().isOf(Items.BONE_MEAL)) {
            this.updateState8(var1, 0, 0, SlotActionType.QUICK_MOVE);
            mc.player.closeHandledScreen();
         } else {
            int var2 = this.resolveInt2(Items.BONE);
            if (var2 >= 0) {
               this.updateState8(var1, var2, 0, SlotActionType.PICKUP);
               this.updateState8(var1, 1, 0, SlotActionType.PICKUP);
            }
         }
      }
   }

   private void updateState6() {
      this.updateState7(new Rotation(this.computefloat() * 10.0F, resolveInt4(this.computefloat() / 4.0F, -30.0F, 30.0F)));
      if (mc.player.age % 5 == 0) {
         PlayerScreenHandler var1 = mc.player.playerScreenHandler;
         int var2 = -1;
         int var3 = -1;

         for (Slot var5 : var1.slots) {
            if (this.checkCondition3(var5) && var5.getStack().isOf(Items.OAK_SAPLING) && var5.getStack().getCount() > var3) {
               var3 = var5.getStack().getCount();
               var2 = var5.id;
            }
         }

         for (Slot var7 : var1.slots) {
            if (this.checkCondition3(var7) && (var7.getStack().isOf(Items.STICK) || var7.getStack().isOf(Items.OAK_SAPLING) && var7.id != var2)) {
               this.updateState8(var1, var7.id, 1, SlotActionType.THROW);
            }
         }
      }
   }

   private boolean checkCondition(Predicate<ItemStack> match) {
      int var2 = IntStream.range(0, 36).filter(i -> match.test(mc.player.getInventory().getStack(i))).findFirst().orElse(-1);
      if (var2 < 0) {
         return false;
      }

      if (var2 < 9) {
         mc.player.getInventory().selectedSlot = var2;
         return true;
      }

      if (mc.player.age % 4 != 0) {
         return true;
      }

      int var3 = IntStream.range(0, 9).filter(i -> mc.player.getInventory().getStack(i).isEmpty()).findFirst().orElse(mc.player.getInventory().selectedSlot);
      this.updateState9(var2, var3);
      mc.player.getInventory().selectedSlot = var3;
      return true;
   }

   private boolean checkCondition2(Vec3d point) {
      Rotation var2 = computeRotation(mc.player.getEyePos(), point);
      float var3 = this.computefloat();
      this.updateState7(new Rotation(var2.getYaw() + var3 / 2.0F, resolveInt4(var2.getPitch() + var3 / 4.0F, -90.0F, 90.0F)));
      return new Rotation(mc.player.prevYaw, mc.player.prevPitch).getDelta(var2) < 20.0;
   }

   private void updateState7(Rotation rotation) {
      RotationStorage.update(rotation, 180.0F, 180.0F, 180.0F, 180.0F, 1, 1, false);
   }

   private float computefloat() {
      float var1 = mc.player.age + mc.getRenderTickCounter().getTickDelta(false);
      return (float)(Math.sin(var1 * 0.31F) * 0.5 + Math.sin(var1 * 0.73F + 1.1F) * 0.3 + Math.sin(var1 * 1.7F + 2.6F) * 0.2) * 8.0F;
   }

   private int resolveInt(ScreenHandler handler, boolean fromContainer, int limit, Predicate<ItemStack> match) {
      int var5 = 0;

      for (Slot var7 : handler.slots) {
         if (var5 >= limit) {
            break;
         }

         if (this.checkCondition3(var7) != fromContainer && match.test(var7.getStack())) {
            this.updateState8(handler, var7.id, 0, SlotActionType.QUICK_MOVE);
            var5++;
         }
      }

      return var5;
   }

   private void updateState8(ScreenHandler handler, int slot, int button, SlotActionType action) {
      mc.interactionManager.clickSlot(handler.syncId, slot, button, action, mc.player);
   }

   private boolean checkCondition3(Slot slot) {
      return ((SlotAccessor)slot).lumen$getInventory() == mc.player.getInventory();
   }

   private int resolveInt2(Item item) {
      for (Slot var3 : mc.player.playerScreenHandler.slots) {
         if (this.checkCondition3(var3) && var3.getStack().isOf(item)) {
            return var3.id;
         }
      }

      return -1;
   }

   private boolean checkCondition4(ItemStack stack) {
      return !(
         stack.getItem() instanceof BlockItem var2
            && !(var2.getBlock() instanceof SaplingBlock)
            && stack.isOf(Items.BONE_MEAL)
            && (!stack.isOf(Items.BONE) || this.resolveInt3(Items.BONE_MEAL) != 0)
      );
   }

   private boolean checkState() {
      return this.resolveInt3(Items.APPLE) > 128 || this.resolveInt3(Items.OAK_LOG) > 192;
   }

   private boolean checkState2() {
      return this.resolveInt3(Items.OAK_SAPLING) == 0 || this.resolveInt3(Items.BONE_MEAL) == 0;
   }

   private int resolveInt3(Item item) {
      int var2 = 0;

      for (ItemStack var4 : mc.player.getInventory().main) {
         if (var4.isOf(item)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private boolean checkCondition5(ItemStack stack) {
      return !stack.isEmpty() && stack.isDamageable() && stack.getMaxDamage() > 0
         ? (1.0 - (double)stack.getDamage() / stack.getMaxDamage()) * 100.0 > 10.0
         : true;
   }

   private void updateState9(int fromInv, int toInv) {
      if (SwapManager.openHiddenInventory()) {
         try {
            mc.interactionManager
               .clickSlot(
                  mc.player.currentScreenHandler.syncId,
                  SwapManager.toContainerSlot(fromInv),
                  SwapManager.toContainerSlot(toInv),
                  SlotActionType.SWAP,
                  mc.player
               );
         } finally {
            SwapManager.closeHiddenInventory();
         }
      }
   }

   private double resolveDouble(BlockPos pos) {
      return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(pos));
   }

   private double resolveDouble2(BlockPos pos) {
      Vec3d var2 = mc.player.getEyePos();
      Vec3d var3 = mc.player.getRotationVec(1.0F);
      Vec3d var4 = Vec3d.ofCenter(pos).subtract(var2);
      double var5 = var4.dotProduct(var3);
      return var5 <= 0.0 ? Double.MAX_VALUE : var4.subtract(var3.multiply(var5)).lengthSquared();
   }

   private static int resolveInt4(float value, float min, float max) {
      return (int)Math.max(min, Math.min(max, value));
   }

   private static Rotation computeRotation(Vec3d eye, Vec3d point) {
      Vec3d var2 = point.subtract(eye);
      double var3 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
      float var5 = (float)Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0F;
      float var6 = (float)(-Math.toDegrees(Math.atan2(var2.y, var3)));
      return new Rotation(MathHelper.wrapDegrees(var5), resolveInt4(var6, -90.0F, 90.0F));
   }

   private BlockPos computeBlockPos(double radius, Predicate<BlockState> match, ToDoubleFunction<BlockPos> score) {
      BlockPos var5 = mc.player.getBlockPos();
      BlockPos var6 = null;
      double var7 = Double.MAX_VALUE;
      double var9 = radius * radius;
      int var11 = (int)Math.ceil(radius);
      Mutable var12 = new Mutable();

      for (int var13 = -var11; var13 <= var11; var13++) {
         for (int var14 = -var11; var14 <= var11; var14++) {
            for (int var15 = -var11; var15 <= var11; var15++) {
               var12.set(var5.getX() + var13, var5.getY() + var14, var5.getZ() + var15);
               if (match.test(mc.world.getBlockState(var12)) && this.resolveDouble(var12) <= var9) {
                  double var16 = score.applyAsDouble(var12);
                  if (var16 < var7) {
                     var7 = var16;
                     var6 = var12.toImmutable();
                  }
               }
            }
         }
      }

      return var6;
   }
}
