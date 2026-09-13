package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.events.implement.EventUpdatePost;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;

public class CocoaFarm extends Module {
   public static final CocoaFarm INSTANCE = new CocoaFarm();
   private static final double LEVEL = 0.1;
   private static final double LEVEL2 = 0.36;
   private static final int INDEX = 4;
   private static final float VOLUME = 900.0F;
   private static final float VOLUME2 = 600.0F;
   private static final float VOLUME3 = 2600.0F;
   private static final float VOLUME4 = 2000.0F;
   private static final float VOLUME5 = 900.0F;
   private static final float VOLUME6 = 700.0F;
   private static final double LEVEL3 = 3.0;
   private static final int INDEX2 = 16;
   private static final int INDEX3 = 12;
   private static final int POLL_INTERVAL_MS = 6000;
   private static final int INDEX4 = 12;
   private static final int INDEX5 = 4;
   private static final int IDLE_TIMEOUT_MS = 4000;
   private static final int INDEX6 = 64;
   private static final int INDEX7 = 8;
   private static final int INDEX8 = 2;
   private static final int INDEX9 = 16;
   private static final int INDEX10 = 6;
   private static final long TIMESTAMP = 12000L;
   private static final Direction[] DIRECTION = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private final TimerUtils timerUtils = new TimerUtils();
   private final TimerUtils timerUtils2 = new TimerUtils();
   private final Set<BlockPos> blockPoss = new HashSet<>();
   private final Map<BlockPos, Long> blockPoss2 = new HashMap<>();
   private BlockPos first;
   private BlockPos second;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private BlockPos blockPos4;
   private BlockPos blockPos5;
   private Vec3d vec3d;
   private List<BlockPos> blockPoss3 = Collections.emptyList();
   private int index;
   private int index2;
   private int index3;
   private int index4;
   private int index5;
   private int index6;
   private int index7;
   private int index8;
   private int index9;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private final Random random = new Random();
   private float volume;
   private float volume2;
   private long timestamp;
   private float volume3 = 16.0F;
   private float volume4 = 16.0F;
   private long timestamp2;
   private long timestamp3;
   private float volume5;
   private long timestamp4;
   private long timestamp5;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6 = true;
   private long timestamp6;
   private long timestamp7;
   private long timestamp8;
   private long timestamp9;
   private long timestamp10;
   private long timestamp11;
   private Vec3d vec3d2;

   private CocoaFarm() {
      super("CocoaFarm", "Фармит созревшие какао-бобы в отмеченной области", Module.ModuleCategory.PLAYER);
   }

   @Override
   public void onEnable() {
      this.helper();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.helper();
      if (RotationStorage.instance != null && RotationStorage.instance.currentPriority() == 4) {
         RotationStorage.instance.stopRotation();
      }

      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      boolean var2 = this.flag;
      this.flag = false;
      this.flag2 = false;
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (mc.currentScreen == null) {
            this.helper35(var2);
            if (!this.isRegionComplete()) {
               if (this.timerUtils.finished(4000L)) {
                  this.timerUtils.reset();
                  ChatUtils.sendMessage("Отметьте область: .cocoa 1 и .cocoa 2");
               }
            } else {
               this.helper8();
               this.helper7();
               if (this.helper5()) {
                  if (!this.helper6()) {
                     BlockPos var3 = this.helper10();
                     if (var3 == null) {
                        this.index2 = 0;
                        this.helper4();
                        this.helper15();
                     } else {
                        Vec3d var4 = Vec3d.ofCenter(var3);
                        double var5 = mc.player.getBlockInteractionRange();
                        if (mc.player.getEyePos().squaredDistanceTo(var4) > var5 * var5) {
                           this.index2 = 0;
                           this.helper4();
                           this.helper14(var3);
                        } else {
                           this.helper3();
                           this.vec3d = var4;
                           this.helper36(var4);
                           if (this.helper39(var4)) {
                              this.index2++;
                           } else {
                              this.index2 = 0;
                           }

                           if (this.index2 >= 2) {
                              if (!var3.equals(this.blockPos4)) {
                                 this.blockPos4 = var3;
                                 this.index3 = 0;
                              }

                              Direction var7 = mc.world.getBlockState(var3).get(CocoaBlock.FACING).getOpposite();
                              mc.interactionManager.updateBlockBreakingProgress(var3, var7);
                              mc.player.swingHand(Hand.MAIN_HAND);
                              this.index3++;
                              if (this.index3 >= 16 && this.helper11(var3)) {
                                 this.helper34(var3, System.currentTimeMillis());
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

   @EventLink(priority = -200)
   public void onMoveInput(EventMoveInput event) {
      if (this.flag) {
         long var2 = System.currentTimeMillis();
         event.setForward(1.0F);
         float var4 = 0.0F;
         if (var2 < this.timestamp4) {
            var4 = this.volume5;
         } else if (var2 >= this.timestamp5) {
            if (this.random.nextFloat() < 0.3F) {
               this.volume5 = this.random.nextBoolean() ? 1.0F : -1.0F;
               this.timestamp4 = var2 + 100L + this.random.nextInt(140);
            }

            this.timestamp5 = var2 + 550L + this.random.nextInt(700);
         }

         event.setStrafe(var4);
         event.setJump(this.flag2);
         boolean var5 = var2 >= this.timestamp3;
         if (var5 && this.random.nextFloat() < 0.004F) {
            this.timestamp3 = var2 + 280L + this.random.nextInt(320);
            var5 = false;
         }

         event.setSprint(var5);
      }
   }

   @EventLink
   public void onUpdatePost(EventUpdatePost event) {
      if (mc.player != null && this.vec3d != null) {
         mc.player.headYaw = mc.player.getYaw();
         mc.player.bodyYaw = mc.player.getYaw();
      }
   }

   public void setFirst(BlockPos pos) {
      this.first = pos == null ? null : pos.toImmutable();
      this.helper2();
   }

   public void setSecond(BlockPos pos) {
      this.second = pos == null ? null : pos.toImmutable();
      this.helper2();
   }

   public BlockPos getFirst() {
      return this.first;
   }

   public BlockPos getSecond() {
      return this.second;
   }

   public void clearRegion() {
      this.first = null;
      this.second = null;
      this.helper2();
   }

   public boolean isRegionComplete() {
      return this.first != null && this.second != null;
   }

   public int minX() {
      return Math.min(this.first.getX(), this.second.getX());
   }

   public int maxX() {
      return Math.max(this.first.getX(), this.second.getX());
   }

   public int minY() {
      return Math.min(this.first.getY(), this.second.getY());
   }

   public int maxY() {
      return this.minY() + 16 - 1;
   }

   public int minZ() {
      return Math.min(this.first.getZ(), this.second.getZ());
   }

   public int maxZ() {
      return Math.max(this.first.getZ(), this.second.getZ());
   }

   private void helper() {
      this.flag = false;
      this.flag2 = false;
      this.volume = 0.0F;
      this.volume2 = 0.0F;
      this.timestamp = 0L;
      this.volume3 = 16.0F;
      this.volume4 = 16.0F;
      this.timestamp2 = 0L;
      this.timestamp3 = 0L;
      this.volume5 = 0.0F;
      this.timestamp4 = 0L;
      this.timestamp5 = 0L;
      this.blockPos = null;
      this.blockPos3 = null;
      this.vec3d = null;
      this.index2 = 0;
      this.index4 = 0;
      this.index5 = 0;
      this.index6 = 0;
      this.flag3 = false;
      this.vec3d2 = null;
      this.helper4();
      this.helper2();
   }

   private void helper2() {
      this.blockPoss.clear();
      this.blockPoss2.clear();
      this.blockPos = null;
      this.blockPos3 = null;
      this.flag4 = false;
      this.timestamp6 = 0L;
      this.timestamp7 = 0L;
      this.index8 = 0;
      this.index9 = 0;
      this.blockPos5 = null;
      this.flag6 = true;
      this.timestamp8 = 0L;
      this.timestamp9 = 0L;
      this.timestamp10 = 0L;
      this.timestamp11 = 0L;
      this.helper3();
   }

   private void helper3() {
      this.blockPoss3 = Collections.emptyList();
      this.index = 0;
      this.blockPos2 = null;
      this.flag5 = false;
   }

   private void helper4() {
      this.blockPos4 = null;
      this.index3 = 0;
   }

   private boolean helper5() {
      if (mc.player.getMainHandStack().getItem() instanceof AxeItem) {
         return true;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).getItem() instanceof AxeItem) {
            mc.player.getInventory().selectedSlot = var1;
            return true;
         }
      }

      if (!this.timerUtils2.finished(250L)) {
         return false;
      }

      for (int var2 = 9; var2 < 36; var2++) {
         if (mc.player.getInventory().getStack(var2).getItem() instanceof AxeItem) {
            this.timerUtils2.reset();
            mc.interactionManager
               .clickSlot(
                  mc.player.playerScreenHandler.syncId,
                  SwapManager.toContainerSlot(var2),
                  mc.player.getInventory().selectedSlot,
                  SlotActionType.SWAP,
                  mc.player
               );
            return false;
         }
      }

      if (this.timerUtils.finished(4000L)) {
         this.timerUtils.reset();
         ChatUtils.sendMessage("Для CocoaFarm нужен топор");
      }

      return false;
   }

   private boolean helper6() {
      ItemStack var1 = mc.player.getMainHandStack();
      if (var1.isDamageable() && var1.getMaxDamage() > 0) {
         double var2 = 1.0 - (double)var1.getDamage() / var1.getMaxDamage();
         if (var2 >= 0.1) {
            this.flag3 = false;
            return false;
         }

         if (this.flag3) {
            return true;
         }

         if (mc.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
            this.helper37(mc.player.getYaw(), 78.0F);
            if (mc.player.getPitch() < 55.0F) {
               return true;
            }

            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
            this.flag3 = true;
            return true;
         } else {
            if (!this.timerUtils2.finished(250L)) {
               return true;
            }

            int var4 = SwapManager.findInInventory(stack -> stack.isOf(Items.EXPERIENCE_BOTTLE));
            if (var4 == -1) {
               if (this.timerUtils.finished(4000L)) {
                  this.timerUtils.reset();
                  ChatUtils.sendMessage("Топор почти сломан, пузырьков опыта нет");
               }

               return true;
            } else {
               this.timerUtils2.reset();
               SwapManager.swapToOffhand(var4);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void helper7() {
      long var1 = System.currentTimeMillis();
      int var3 = this.maxX() - this.minX() + 1;
      int var4 = this.maxZ() - this.minZ() + 1;
      long var5 = (long)var3 * var4;
      if (!this.flag4) {
         if (var1 < this.timestamp8) {
            return;
         }

         this.flag4 = true;
         this.timestamp6 = 0L;
         this.index7 = this.minY();
         int var7 = Math.max(this.minX(), Math.min(this.maxX(), mc.player.getBlockPos().getX()));
         int var8 = Math.max(this.minZ(), Math.min(this.maxZ(), mc.player.getBlockPos().getZ()));
         this.timestamp7 = (long)(var7 - this.minX()) * var4 + var8 - this.minZ();
      }

      for (int var13 = 0; var13 < 6000 && this.flag4; var13++) {
         long var14 = (this.timestamp7 + this.timestamp6) % var5;
         int var10 = this.minX() + (int)(var14 / var4);
         int var11 = this.minZ() + (int)(var14 % var4);
         BlockPos var12 = new BlockPos(var10, this.index7, var11);
         if (this.helper11(var12)) {
            this.blockPoss.add(var12);
         }

         this.helper9(var1, var5);
      }
   }

   private void helper8() {
      long var1 = System.currentTimeMillis();
      if (var1 >= this.timestamp9) {
         this.timestamp9 = var1 + 150L;
         BlockPos var3 = mc.player.getBlockPos();
         int var4 = Math.max(this.minX(), var3.getX() - 12);
         int var5 = Math.min(this.maxX(), var3.getX() + 12);
         int var6 = Math.max(this.minZ(), var3.getZ() - 12);
         int var7 = Math.min(this.maxZ(), var3.getZ() + 12);
         Mutable var8 = new Mutable();
         BlockPos var9 = null;
         double var10 = Double.MAX_VALUE;

         for (int var12 = var4; var12 <= var5; var12++) {
            for (int var13 = var6; var13 <= var7; var13++) {
               for (int var14 = this.minY(); var14 <= this.maxY(); var14++) {
                  var8.set(var12, var14, var13);
                  if (this.helper11(var8)) {
                     BlockPos var15 = var8.toImmutable();
                     this.blockPoss.add(var15);
                     double var16 = mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var15));
                     if (var16 < var10) {
                        var10 = var16;
                        var9 = var15;
                     }
                  }
               }
            }
         }

         this.blockPos3 = var9;
      }
   }

   private void helper9(long now, long totalColumns) {
      this.index7++;
      if (this.index7 > this.maxY()) {
         this.index7 = this.minY();
         this.timestamp6++;
         if (this.timestamp6 >= totalColumns) {
            this.flag4 = false;
            this.timestamp8 = now + 750L;
         }
      }
   }

   private BlockPos helper10() {
      long var1 = System.currentTimeMillis();
      this.blockPoss2.entrySet().removeIf(entry -> entry.getValue() <= var1);
      BlockPos var3 = this.blockPos3;
      if (var3 != null
         && !this.blockPoss2.containsKey(var3)
         && this.helper11(var3)
         && this.helper13(var3)
         && !var3.equals(this.blockPos)) {
         this.blockPos = var3;
         this.index2 = 0;
         this.index5 = 0;
         this.index6 = 0;
         this.helper4();
         this.helper3();
         return this.blockPos;
      }

      if (this.blockPos != null && !this.blockPoss2.containsKey(this.blockPos) && this.helper11(this.blockPos)) {
         return this.blockPos;
      }

      if (this.blockPos != null) {
         this.blockPoss.remove(this.blockPos);
         this.blockPos = null;
         this.index2 = 0;
         this.index5 = 0;
         this.index6 = 0;
         this.helper4();
         this.helper3();
      }

      if (var1 < this.timestamp11) {
         return null;
      }

      this.timestamp11 = var1 + 150L;
      ArrayList<BlockPos> var4 = new ArrayList<>();
      ArrayList<BlockPos> var5 = new ArrayList<>();

      for (BlockPos var7 : this.blockPoss) {
         if (!this.helper11(var7)) {
            var4.add(var7);
         } else if (!this.blockPoss2.containsKey(var7)) {
            var5.add(var7);
         }
      }

      this.blockPoss.removeAll(var4);
      var5.sort(Comparator.comparingDouble(pos -> mc.player.squaredDistanceTo(Vec3d.ofCenter(pos))));
      if (var5.size() > 64) {
         var5 = new ArrayList<>(var5.subList(0, 64));
      }

      if (var5.isEmpty()) {
         return null;
      }

      CocoaFarm.PathPlan var11 = this.helper20(var5);
      if (var11 != null) {
         this.blockPos = var11.target();
         this.blockPos2 = var11.target();
         this.blockPoss3 = var11.path();
         this.index = 0;
         this.flag5 = false;
         this.index4 = 0;
         this.index6 = 0;
         return this.blockPos;
      }

      long var12 = var1 + 12000L;

      for (BlockPos var10 : var5) {
         this.blockPoss2.put(var10, var12);
      }

      return null;
   }

   private boolean helper11(BlockPos pos) {
      if (pos != null && this.helper12(pos)) {
         BlockState var2 = mc.world.getBlockState(pos);
         return var2.isOf(Blocks.COCOA) && var2.get(CocoaBlock.AGE) >= 2;
      } else {
         return false;
      }
   }

   private boolean helper12(BlockPos pos) {
      return pos.getX() >= this.minX()
         && pos.getX() <= this.maxX()
         && pos.getY() >= this.minY()
         && pos.getY() <= this.maxY()
         && pos.getZ() >= this.minZ()
         && pos.getZ() <= this.maxZ();
   }

   private boolean helper13(BlockPos target) {
      double var2 = mc.player.getBlockInteractionRange();
      return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(target)) <= var2 * var2;
   }

   private void helper14(BlockPos target) {
      long var2 = System.currentTimeMillis();
      boolean var4 = this.flag5 || !target.equals(this.blockPos2) || this.blockPoss3.isEmpty();
      if (this.index4 >= 8) {
         this.index4 = 0;
         this.index6 = 0;
         this.index5++;
         if (this.index5 >= 2) {
            this.helper34(target, var2);
            return;
         }

         var4 = true;
      }

      if (var4) {
         if (var2 < this.timestamp10) {
            return;
         }

         CocoaFarm.PathPlan var5 = this.helper20(List.of(target));
         this.timestamp10 = var2 + 150L;
         if (var5 == null) {
            this.helper34(target, var2);
            return;
         }

         this.blockPoss3 = var5.path();
         this.index = 0;
         this.blockPos2 = target;
         this.flag5 = false;
      }

      if (this.index >= this.blockPoss3.size()) {
         this.helper34(target, var2);
      } else {
         BlockPos var6 = this.helper18();
         if (var6 == null) {
            this.helper34(target, var2);
         } else {
            this.helper16(var6);
         }
      }
   }

   private void helper15() {
      long var1 = System.currentTimeMillis();
      if (this.index4 >= 8) {
         this.index4 = 0;
         this.index5 = 0;
         this.index6 = 0;
         this.helper31();
         this.helper3();
      }

      if (this.index >= this.blockPoss3.size()) {
         if (var1 < this.timestamp10) {
            return;
         }

         BlockPos var3 = this.helper29();
         this.timestamp10 = var1 + 200L;
         if (var3 == null) {
            return;
         }

         List var4 = this.helper22(var3);
         if (var4.isEmpty()) {
            this.helper31();
            return;
         }

         this.blockPoss3 = var4;
         this.index = 0;
         this.blockPos2 = var3;
         this.flag5 = true;
      }

      BlockPos var5 = this.helper18();
      if (var5 == null) {
         this.helper3();
      } else {
         this.helper16(var5);
      }
   }

   private void helper16(BlockPos waypoint) {
      long var2 = System.currentTimeMillis();
      if (var2 >= this.timestamp2) {
         this.volume4 = 10.0F + this.random.nextFloat() * 12.0F;
         this.timestamp2 = var2 + 800L + this.random.nextInt(900);
      }

      this.volume3 = this.volume3 + (this.volume4 - this.volume3) * 0.12F;
      Vec3d var4 = this.helper17(waypoint);
      this.vec3d = var4;
      double var5 = var4.x - mc.player.getX();
      double var7 = var4.z - mc.player.getZ();
      double var9 = Math.hypot(var5, var7);
      if (var9 > 1.2) {
         float var11 = (float)(Math.toDegrees(Math.atan2(var7, var5)) - 90.0);
         double var12 = var4.y - mc.player.getY();
         float var14 = (float)(-Math.toDegrees(Math.atan2(var12, Math.max(1.5, var9))));
         float var15 = MathHelper.clamp(this.volume3 + var14 * 0.6F, -35.0F, 50.0F);
         this.helper37(var11, var15);
      }

      this.flag = true;
      this.flag2 = waypoint.getY() > mc.player.getBlockPos().getY() || mc.player.horizontalCollision && mc.player.isOnGround();
   }

   private Vec3d helper17(BlockPos fallback) {
      double var2 = mc.player.getX();
      double var4 = mc.player.getZ();
      double var6 = fallback.getY();
      double var8 = 3.0;

      for (int var10 = this.index; var10 < this.blockPoss3.size() && var8 > 1.0E-6; var10++) {
         BlockPos var11 = this.blockPoss3.get(var10);
         double var12 = var11.getX() + 0.5;
         double var14 = var11.getZ() + 0.5;
         double var16 = Math.hypot(var12 - var2, var14 - var4);
         if (var16 >= var8) {
            double var18 = var8 / Math.max(1.0E-6, var16);
            var2 += (var12 - var2) * var18;
            var4 += (var14 - var4) * var18;
            var6 = var11.getY();
            var8 = 0.0;
            break;
         }

         var2 = var12;
         var4 = var14;
         var6 = var11.getY();
         var8 -= var16;
      }

      return Math.hypot(var2 - mc.player.getX(), var4 - mc.player.getZ()) < 1.0
         ? new Vec3d(fallback.getX() + 0.5, fallback.getY(), fallback.getZ() + 0.5)
         : new Vec3d(var2, var6, var4);
   }

   private BlockPos helper18() {
      while (this.index < this.blockPoss3.size()) {
         BlockPos var1 = this.blockPoss3.get(this.index);
         double var2 = this.helper19(var1);
         double var4 = this.index == this.blockPoss3.size() - 1 ? 0.09 : 0.36;
         boolean var6 = var2 <= var4 && Math.abs(var1.getY() - mc.player.getBlockPos().getY()) == 0;
         boolean var7 = this.index + 1 < this.blockPoss3.size()
            && this.helper19(this.blockPoss3.get(this.index + 1)) < var2;
         if (!var6 && !var7) {
            return var1;
         }

         this.index++;
      }

      return null;
   }

   private double helper19(BlockPos waypoint) {
      double var2 = waypoint.getX() + 0.5 - mc.player.getX();
      double var4 = waypoint.getZ() + 0.5 - mc.player.getZ();
      return var2 * var2 + var4 * var4;
   }

   private CocoaFarm.PathPlan helper20(List<BlockPos> targets) {
      BlockPos var2 = mc.player.getBlockPos();
      double var3 = Math.max(1.0, mc.player.getBlockInteractionRange() - 0.65);
      double var5 = var3 * var3;
      double var7 = mc.player.getStandingEyeHeight();
      int var9 = Math.min(this.minX() - 12, var2.getX() - 12);
      int var10 = Math.max(this.maxX() + 12, var2.getX() + 12);
      int var11 = Math.min(this.minY() - 4, var2.getY() - 4);
      int var12 = Math.max(this.maxY() + 4, var2.getY() + 4);
      int var13 = Math.min(this.minZ() - 12, var2.getZ() - 12);
      int var14 = Math.max(this.maxZ() + 12, var2.getZ() + 12);
      ArrayDeque<BlockPos> var15 = new ArrayDeque<>();
      HashMap<BlockPos, BlockPos> var16 = new HashMap<>();
      HashMap<BlockPos, Boolean> var17 = new HashMap<>();
      HashSet<BlockPos> var18 = new HashSet<>();
      var15.add(var2);
      var18.add(var2);
      int var19 = 0;

      while (!var15.isEmpty() && var19++ < 4000) {
         BlockPos var20 = (BlockPos)var15.removeFirst();
         BlockPos var21 = this.helper21(var20, targets, var7, var5);
         if (var21 != null) {
            List<BlockPos> var24 = this.helper28(var20, var2, var16);
            if (var24.isEmpty()) {
               var24 = List.of(var2);
            }

            return new CocoaFarm.PathPlan(var21, var24);
         }

         for (BlockPos var23 : this.helper23(var20, var17)) {
            if (var23.getX() >= var9
               && var23.getX() <= var10
               && var23.getY() >= var11
               && var23.getY() <= var12
               && var23.getZ() >= var13
               && var23.getZ() <= var14
               && var18.add(var23)) {
               var16.put(var23, var20);
               var15.addLast(var23);
            }
         }
      }

      return null;
   }

   private BlockPos helper21(BlockPos feet, List<BlockPos> targets, double eyeHeight, double reachSquared) {
      BlockPos var7 = null;
      double var8 = Double.MAX_VALUE;

      for (BlockPos var11 : targets) {
         double var12 = var11.getX() - feet.getX();
         double var14 = var11.getY() + 0.5 - feet.getY() - eyeHeight;
         double var16 = var11.getZ() - feet.getZ();
         double var18 = var12 * var12 + var14 * var14 + var16 * var16;
         if (!(var18 > reachSquared) && !(var18 >= var8)) {
            var8 = var18;
            var7 = var11;
         }
      }

      return var7;
   }

   private List<BlockPos> helper22(BlockPos destination) {
      BlockPos var2 = mc.player.getBlockPos();
      if (var2.equals(destination)) {
         return List.of(var2);
      }

      int var3 = Math.min(this.minX() - 12, var2.getX() - 12);
      int var4 = Math.max(this.maxX() + 12, var2.getX() + 12);
      int var5 = Math.min(this.minY() - 4, var2.getY() - 4);
      int var6 = Math.max(this.maxY() + 4, var2.getY() + 4);
      int var7 = Math.min(this.minZ() - 12, var2.getZ() - 12);
      int var8 = Math.max(this.maxZ() + 12, var2.getZ() + 12);
      PriorityQueue<CocoaFarm.PathNode> var9 = new PriorityQueue<>(Comparator.comparingDouble(CocoaFarm.PathNode::score));
      HashMap<BlockPos, Double> var10 = new HashMap<>();
      HashMap<BlockPos, BlockPos> var11 = new HashMap<>();
      HashMap<BlockPos, Boolean> var12 = new HashMap<>();
      HashSet<BlockPos> var13 = new HashSet<>();
      var10.put(var2, 0.0);
      var9.add(new CocoaFarm.PathNode(var2, 0.0, this.helper27(var2, destination)));
      int var14 = 0;

      while (!var9.isEmpty() && var14++ < 4000) {
         CocoaFarm.PathNode var15 = (CocoaFarm.PathNode)var9.poll();
         BlockPos var16 = var15.pos();
         if (var13.add(var16)) {
            if (var16.equals(destination)) {
               return this.helper28(var16, var2, var11);
            }

            for (BlockPos var18 : this.helper23(var16, var12)) {
               if (var18.getX() >= var3
                  && var18.getX() <= var4
                  && var18.getY() >= var5
                  && var18.getY() <= var6
                  && var18.getZ() >= var7
                  && var18.getZ() <= var8
                  && !var13.contains(var18)) {
                  double var19 = var15.cost() + 1.0;
                  if (!(var19 >= var10.getOrDefault(var18, Double.MAX_VALUE))) {
                     var10.put(var18, var19);
                     var11.put(var18, var16);
                     var9.add(new CocoaFarm.PathNode(var18, var19, var19 + this.helper27(var18, destination)));
                  }
               }
            }
         }
      }

      return Collections.emptyList();
   }

   private List<BlockPos> helper23(BlockPos current, Map<BlockPos, Boolean> cache) {
      ArrayList<BlockPos> var3 = new ArrayList<>(12);

      for (Direction var7 : DIRECTION) {
         BlockPos var8 = current.offset(var7);
         if (this.helper24(var8, cache)) {
            var3.add(var8);
         }

         BlockPos var9 = var8.up();
         if (this.helper24(var9, cache) && this.helper26(current.up(2))) {
            var3.add(var9);
         }

         BlockPos var10 = var8.down();
         if (this.helper24(var10, cache) && this.helper26(var8)) {
            var3.add(var10);
         }
      }

      return var3;
   }

   private boolean helper24(BlockPos feet, Map<BlockPos, Boolean> cache) {
      return cache.computeIfAbsent(feet, this::helper25);
   }

   private boolean helper25(BlockPos feet) {
      BlockPos var2 = feet.down();
      BlockState var3 = mc.world.getBlockState(var2);
      return this.helper26(feet) && this.helper26(feet.up()) && var3.isSideSolidFullSquare(mc.world, var2, Direction.UP);
   }

   private boolean helper26(BlockPos pos) {
      return mc.world.getBlockState(pos).getCollisionShape(mc.world, pos).isEmpty();
   }

   private double helper27(BlockPos pos, BlockPos target) {
      int var3 = Math.abs(target.getX() - pos.getX()) + Math.abs(target.getZ() - pos.getZ());
      int var4 = Math.abs(target.getY() - pos.getY());
      return Math.max(var3, var4);
   }

   private List<BlockPos> helper28(BlockPos end, BlockPos start, Map<BlockPos, BlockPos> parents) {
      ArrayList var4 = new ArrayList();
      BlockPos var5 = end;

      while (!var5.equals(start)) {
         var4.add(var5);
         var5 = (BlockPos)parents.get(var5);
         if (var5 == null) {
            return Collections.emptyList();
         }
      }

      Collections.reverse(var4);
      return var4;
   }

   private BlockPos helper29() {
      for (int var1 = 0; var1 < 12; var1++) {
         if (this.blockPos5 == null) {
            this.blockPos5 = this.helper30();
         }

         double var2 = this.blockPos5.getX() + 0.5 - mc.player.getX();
         double var4 = this.blockPos5.getZ() + 0.5 - mc.player.getZ();
         if (var2 * var2 + var4 * var4 <= 4.0) {
            this.blockPos5 = null;
            this.index9 = 0;
         } else {
            BlockPos var6 = this.helper33(this.helper32(this.blockPos5));
            if (var6 == null) {
               this.blockPos5 = null;
            } else {
               double var7 = var6.getX() + 0.5 - mc.player.getX();
               double var9 = var6.getZ() + 0.5 - mc.player.getZ();
               if (var7 * var7 + var9 * var9 > 4.0) {
                  return var6;
               }

               this.blockPos5 = null;
            }
         }
      }

      return null;
   }

   private BlockPos helper30() {
      int var1 = mc.player.getBlockPos().getY();
      if (this.flag6) {
         int var6 = Math.max(1, (this.maxZ() - this.minZ()) / 6 + 1);
         int var7 = this.index8++;
         int var8 = Math.min(this.maxZ(), this.minZ() + var7 * 6);
         int var9 = (var7 & 1) == 0 ? this.maxX() + 2 : this.minX() - 2;
         if (this.index8 >= var6) {
            this.index8 = 0;
            this.flag6 = false;
         }

         return new BlockPos(var9, var1, var8);
      } else {
         int var2 = Math.max(1, (this.maxX() - this.minX()) / 6 + 1);
         int var3 = this.index8++;
         int var4 = Math.min(this.maxX(), this.minX() + var3 * 6);
         int var5 = (var3 & 1) == 0 ? this.maxZ() + 2 : this.minZ() - 2;
         if (this.index8 >= var2) {
            this.index8 = 0;
            this.flag6 = true;
         }

         return new BlockPos(var4, var1, var5);
      }
   }

   private void helper31() {
      this.index9++;
      if (this.index9 >= 2) {
         this.index9 = 0;
         this.blockPos5 = null;
      }
   }

   private BlockPos helper32(BlockPos anchor) {
      BlockPos var2 = mc.player.getBlockPos();
      int var3 = anchor.getX() - var2.getX();
      int var4 = anchor.getZ() - var2.getZ();
      int var5 = Math.max(Math.abs(var3), Math.abs(var4));
      if (var5 <= 20) {
         return anchor;
      }

      double var6 = 20.0 / var5;
      return new BlockPos(var2.getX() + (int)Math.round(var3 * var6), anchor.getY(), var2.getZ() + (int)Math.round(var4 * var6));
   }

   private BlockPos helper33(BlockPos anchor) {
      for (int var2 = 0; var2 <= 5; var2++) {
         for (int var3 = -var2; var3 <= var2; var3++) {
            for (int var4 = -var2; var4 <= var2; var4++) {
               if (var2 <= 0 || Math.abs(var3) == var2 || Math.abs(var4) == var2) {
                  for (int var8 : new int[]{0, 1, -1}) {
                     BlockPos var9 = anchor.add(var3, var8, var4);
                     if (this.helper25(var9)) {
                        return var9;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private void helper34(BlockPos target, long now) {
      if (target != null) {
         this.blockPoss2.put(target, now + 12000L);
      }

      this.blockPos = null;
      this.index2 = 0;
      this.index4 = 0;
      this.index5 = 0;
      this.index6 = 0;
      this.timestamp11 = now + 100L;
      this.helper4();
      this.helper3();
   }

   private void helper35(boolean wasWalking) {
      Vec3d var2 = mc.player.getPos();
      if (wasWalking && this.vec3d2 != null) {
         double var3 = var2.x - this.vec3d2.x;
         double var5 = var2.z - this.vec3d2.z;
         if (!(var3 * var3 + var5 * var5 < 0.0025) && !mc.player.horizontalCollision) {
            this.index4 = 0;
            this.index6++;
            if (this.index6 >= 40) {
               this.index6 = 0;
               this.index5 = 0;
            }
         } else {
            this.index4++;
            this.index6 = 0;
         }

         this.vec3d2 = var2;
      } else {
         this.index4 = 0;
         this.vec3d2 = var2;
      }
   }

   private void helper36(Vec3d target) {
      Rotation var2 = this.helper40(target);
      this.helper37(var2.getYaw(), var2.getPitch());
   }

   private void helper37(float targetYaw, float targetPitch) {
      if (mc.player != null) {
         long var3 = System.nanoTime();
         double var5 = this.timestamp == 0L ? 0.05 : (var3 - this.timestamp) / 1.0E9;
         this.timestamp = var3;
         var5 = Math.max(0.005, Math.min(0.1, var5));
         float var7 = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
         float var8 = MathHelper.wrapDegrees(targetPitch - mc.player.getPitch());
         this.volume = this.helper38(this.volume, var7, 900.0F, 2600.0F, 900.0F, var5);
         this.volume2 = this.helper38(this.volume2, var8, 600.0F, 2000.0F, 700.0F, var5);
         float var9 = (float)(this.volume * var5);
         float var10 = (float)(this.volume2 * var5);
         if (var9 * var7 >= 0.0F && Math.abs(var9) > Math.abs(var7)) {
            var9 = var7;
            this.volume *= 0.2F;
         }

         if (var10 * var8 >= 0.0F && Math.abs(var10) > Math.abs(var8)) {
            var10 = var8;
            this.volume2 *= 0.2F;
         }

         float var11 = Math.max(0.05F, Math.abs(var9));
         float var12 = Math.max(0.05F, Math.abs(var10));
         RotationStorage.update(new Rotation(targetYaw, targetPitch), var11, var12, 0.0F, 0.0F, 20, 4, false);
         mc.player.headYaw = mc.player.getYaw();
         mc.player.bodyYaw = mc.player.getYaw();
      }
   }

   private float helper38(float vel, float error, float maxSpeed, float accel, float brake, double dt) {
      float var8 = Math.abs(error);
      float var9 = 1.0F + MathHelper.clamp((var8 - 25.0F) / 60.0F, 0.0F, 1.2F);
      float var10;
      if (var8 < 0.001F) {
         var10 = 0.0F;
      } else {
         var10 = (float)Math.copySign(Math.min(maxSpeed, Math.sqrt(2.0 * brake * var9 * var8)), error);
      }

      float var11 = var10 - vel;
      float var12 = (float)(accel * var9 * dt);
      if (var11 > var12) {
         var11 = var12;
      } else if (var11 < -var12) {
         var11 = -var12;
      }

      return vel + var11;
   }

   private boolean helper39(Vec3d target) {
      Rotation var2 = this.helper40(target);
      float var3 = MathHelper.wrapDegrees(var2.getYaw() - mc.player.getYaw());
      float var4 = var2.getPitch() - mc.player.getPitch();
      return Math.hypot(var3, var4) < 1.0;
   }

   private Rotation helper40(Vec3d target) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = target.x - var2.x;
      double var5 = target.y - var2.y;
      double var7 = target.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)(Math.toDegrees(Math.atan2(var7, var3)) - 90.0);
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      return new Rotation(var11, var12);
   }

   private record PathNode(BlockPos pos, double cost, double score) {

      private PathNode(BlockPos pos, double cost, double score) {
         this.pos = pos;
         this.cost = cost;
         this.score = score;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public double cost() {
         return this.cost;
      }

      public double score() {
         return this.score;
      }
   }

   private record PathPlan(BlockPos target, List<BlockPos> path) {

      private PathPlan(BlockPos target, List<BlockPos> path) {
         this.target = target;
         this.path = path;
      }

      public BlockPos target() {
         return this.target;
      }

      public List<BlockPos> path() {
         return this.path;
      }
   }
}
