package dlc.lumen.client.modules.impl.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

final class MinePathNavigator {
   private static final double LEVEL = 3.5;
   private static final int INDEX = 3000;
   private static final int INDEX2 = 40;
   private static final long TIMESTAMP = 1400L;
   private static final long TIMESTAMP2 = 500L;
   private final Random random = new Random();
   private final MinePathNavigator.Output minePathNavigator = new MinePathNavigator.Output();
   private List<BlockPos> blockPoss = List.of();
   private int index;
   private BlockPos blockPos;
   private long timestamp;
   private long timestamp2;
   private double level;
   private double level2;
   private int index2;
   private long timestamp3;
   private static final int[][] RESOLUTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

   void reset() {
      this.blockPoss = List.of();
      this.index = 0;
      this.blockPos = null;
      this.timestamp = 0L;
      this.timestamp2 = 0L;
      this.index2 = 0;
      this.timestamp3 = 0L;
   }

   MinePathNavigator.Output tick(ClientPlayerEntity player, World world, BlockPos target, double acceptRadius, long now) {
      BlockPos var8 = player.getBlockPos();
      boolean var9 = this.blockPos == null || target.getSquaredDistance(this.blockPos) > 2.25;
      boolean var10 = !this.blockPoss.isEmpty() && this.helper2(player) > 2.8;
      if (var9 || var10 || now >= this.timestamp || this.index >= this.blockPoss.size()) {
         this.blockPos = target.toImmutable();
         this.blockPoss = helper5(world, var8, this.blockPos, acceptRadius);
         this.index = 0;
         this.timestamp = now + 1400L + this.random.nextInt(700);
      }

      if (this.blockPoss.isEmpty()) {
         return null;
      }

      this.helper4(player, now);
      if (this.blockPoss.isEmpty()) {
         return null;
      }

      while (
         this.index < this.blockPoss.size() - 1
            && (
               helper3(player, this.blockPoss.get(this.index)) < 0.42
                  || helper3(player, this.blockPoss.get(this.index)) < 2.25
                     && helper3(player, this.blockPoss.get(this.index + 1))
                        < helper3(player, this.blockPoss.get(this.index))
            )
      ) {
         this.index++;
      }

      BlockPos var11 = this.blockPoss.get(this.index);
      double var12 = var11.getX() + 0.5 - player.getX();
      double var14 = var11.getZ() + 0.5 - player.getZ();
      double var16 = Math.sqrt(var12 * var12 + var14 * var14);
      if (var16 < 0.001) {
         if (this.index < this.blockPoss.size() - 1) {
            this.index++;
         }

         this.minePathNavigator.forward = 0.4F;
         this.minePathNavigator.strafe = 0.0F;
         this.minePathNavigator.jump = false;
         return this.minePathNavigator;
      } else {
         var12 /= var16;
         var14 /= var16;
         float var18 = player.getYaw() * (float) (Math.PI / 180.0);
         double var19 = -MathHelper.sin(var18);
         double var21 = MathHelper.cos(var18);
         double var23 = MathHelper.cos(var18);
         double var25 = MathHelper.sin(var18);
         float var27 = (float)(var12 * var19 + var14 * var21);
         float var28 = (float)(var12 * var23 + var14 * var25);
         float var29 = Math.max(1.0F, Math.max(Math.abs(var27), Math.abs(var28)));
         this.minePathNavigator.forward = var27 / var29;
         this.minePathNavigator.strafe = var28 / var29;
         this.minePathNavigator.jump = now < this.timestamp3
            || var11.getY() > var8.getY() && var16 < 1.7 && player.isOnGround()
            || player.horizontalCollision && player.isOnGround();
         this.helper(player, var11, var12, var14);
         return this.minePathNavigator;
      }
   }

   private void helper(ClientPlayerEntity player, BlockPos waypoint, double dirX, double dirZ) {
      double var7 = player.getX();
      double var9 = player.getZ();
      double var11 = player.getY();
      double var13 = 3.5;

      for (int var15 = this.index; var15 < this.blockPoss.size() && var13 > 1.0E-6; var15++) {
         BlockPos var16 = this.blockPoss.get(var15);
         double var17 = var16.getX() + 0.5;
         double var19 = var16.getZ() + 0.5;
         double var21 = Math.hypot(var17 - var7, var19 - var9);
         if (var21 >= var13) {
            double var23 = var13 / Math.max(1.0E-6, var21);
            var7 += (var17 - var7) * var23;
            var9 += (var19 - var9) * var23;
            var11 = var16.getY();
            var13 = 0.0;
            break;
         }

         var7 = var17;
         var9 = var19;
         var11 = var16.getY();
         var13 -= var21;
      }

      double var26 = var7 - player.getX();
      double var27 = var9 - player.getZ();
      this.minePathNavigator.hasLook = Math.hypot(var26, var27) > 1.2;
      this.minePathNavigator.lookX = var7;
      this.minePathNavigator.lookY = var11;
      this.minePathNavigator.lookZ = var9;
      double var28 = dirZ * var26 - dirX * var27;
      this.minePathNavigator.bendSign = var28 > 0.35 ? 1 : (var28 < -0.35 ? -1 : 0);
   }

   private double helper2(ClientPlayerEntity player) {
      double var2 = Double.MAX_VALUE;

      for (int var4 = Math.max(0, this.index - 1); var4 < Math.min(this.blockPoss.size(), this.index + 4); var4++) {
         var2 = Math.min(var2, Math.sqrt(helper3(player, this.blockPoss.get(var4))));
      }

      return var2;
   }

   private static double helper3(ClientPlayerEntity player, BlockPos p) {
      double var2 = p.getX() + 0.5 - player.getX();
      double var4 = p.getZ() + 0.5 - player.getZ();
      return var2 * var2 + var4 * var4;
   }

   private void helper4(ClientPlayerEntity player, long now) {
      if (this.timestamp2 == 0L) {
         this.timestamp2 = now;
         this.level = player.getX();
         this.level2 = player.getZ();
      } else if (now - this.timestamp2 >= 500L) {
         double var4 = Math.hypot(player.getX() - this.level, player.getZ() - this.level2);
         this.timestamp2 = now;
         this.level = player.getX();
         this.level2 = player.getZ();
         if (var4 > 0.15) {
            this.index2 = 0;
         } else {
            this.index2++;
            this.timestamp = 0L;
            if (this.index2 >= 2) {
               this.timestamp3 = now + 250L;
            }

            if (this.index2 >= 4) {
               this.blockPoss = List.of();
               this.index2 = 0;
            }
         }
      }
   }

   private static List<BlockPos> helper5(World world, BlockPos start, BlockPos goal, double acceptRadius) {
      BlockPos var5 = start;

      for (int var6 = 0; var6 < 4 && !helper9(world, var5.getX(), var5.getY(), var5.getZ()); var6++) {
         var5 = var5.down();
      }

      if (!helper9(world, var5.getX(), var5.getY(), var5.getZ())) {
         return List.of();
      }

      double var37 = acceptRadius * acceptRadius;
      HashMap<BlockPos, Double> var8 = new HashMap<>();
      PriorityQueue<MinePathNavigator.Node> var9 = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
      MinePathNavigator.Node var10 = new MinePathNavigator.Node(var5, null, 0.0, Math.sqrt(helper7(var5, goal)));
      var9.add(var10);
      var8.put(var5, 0.0);
      MinePathNavigator.Node var11 = var10;
      double var12 = var10.f;
      int var14 = 0;

      while (!var9.isEmpty() && var14 < 3000) {
         MinePathNavigator.Node var15 = (MinePathNavigator.Node)var9.poll();
         Double var16 = (Double)var8.get(var15.pos);
         if (var16 == null || !(var15.g > var16 + 1.0E-6)) {
            var14++;
            double var17 = helper7(var15.pos, goal);
            if (var17 <= var37) {
               return helper6(var15);
            }

            double var19 = Math.sqrt(var17);
            if (var19 < var12) {
               var12 = var19;
               var11 = var15;
            }

            int var21 = var15.pos.getX();
            int var22 = var15.pos.getY();
            int var23 = var15.pos.getZ();

            for (int[] var27 : RESOLUTIONS) {
               int var28 = var21 + var27[0];
               int var29 = var23 + var27[1];
               if (Math.abs(var28 - var5.getX()) <= 40 && Math.abs(var29 - var5.getZ()) <= 40) {
                  boolean var30 = var27[0] != 0 && var27[1] != 0;
                  if (!var30
                     || helper8(world, var21 + var27[0], var22, var23)
                        && helper8(world, var21 + var27[0], var22 + 1, var23)
                        && helper8(world, var21, var22, var23 + var27[1])
                        && helper8(world, var21, var22 + 1, var23 + var27[1])) {
                     BlockPos var31 = null;
                     double var32 = 0.0;
                     if (helper9(world, var28, var22, var29)) {
                        var31 = new BlockPos(var28, var22, var29);
                     } else if (!var30) {
                        if (helper8(world, var21, var22 + 2, var23) && helper9(world, var28, var22 + 1, var29)) {
                           var31 = new BlockPos(var28, var22 + 1, var29);
                           var32 = 0.6;
                        } else if (helper8(world, var28, var22, var29) && helper8(world, var28, var22 + 1, var29)) {
                           for (int var34 = 1; var34 <= 3; var34++) {
                              if (helper9(world, var28, var22 - var34, var29)) {
                                 var31 = new BlockPos(var28, var22 - var34, var29);
                                 var32 = 0.25 * var34;
                                 break;
                              }

                              if (!helper8(world, var28, var22 - var34, var29)) {
                                 break;
                              }
                           }
                        }
                     }

                     if (var31 != null) {
                        double var39 = var15.g + (var30 ? 1.4142 : 1.0) + var32;
                        Double var36 = (Double)var8.get(var31);
                        if (var36 == null || !(var36 <= var39 + 1.0E-6)) {
                           var8.put(var31, var39);
                           var9.add(new MinePathNavigator.Node(var31, var15, var39, var39 + Math.sqrt(helper7(var31, goal))));
                        }
                     }
                  }
               }
            }
         }
      }

      List var38 = helper6(var11);
      return var38.size() > 1 ? var38 : List.of();
   }

   private static List<BlockPos> helper6(MinePathNavigator.Node node) {
      ArrayList var1 = new ArrayList();

      for (MinePathNavigator.Node var2 = node; var2 != null; var2 = var2.parent) {
         var1.add(var2.pos);
      }

      Collections.reverse(var1);
      return var1;
   }

   private static double helper7(BlockPos feet, BlockPos goal) {
      double var2 = feet.getX() - goal.getX();
      double var4 = feet.getY() + 1.6 - (goal.getY() + 0.5);
      double var6 = feet.getZ() - goal.getZ();
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   private static boolean helper8(World world, int x, int y, int z) {
      BlockPos var4 = new BlockPos(x, y, z);
      BlockState var5 = world.getBlockState(var4);
      return var5.getCollisionShape(world, var4).isEmpty() && var5.getFluidState().isEmpty();
   }

   private static boolean checkCondition(World world, int x, int y, int z) {
      BlockPos var4 = new BlockPos(x, y, z);
      return !world.getBlockState(var4).getCollisionShape(world, var4).isEmpty();
   }

   private static boolean helper9(World world, int x, int y, int z) {
      return helper8(world, x, y, z) && helper8(world, x, y + 1, z) && checkCondition(world, x, y - 1, z);
   }

   private static final class Node {
      final BlockPos pos;
      final MinePathNavigator.Node parent;
      final double g;
      final double f;

      Node(BlockPos pos, MinePathNavigator.Node parent, double g, double f) {
         this.pos = pos;
         this.parent = parent;
         this.g = g;
         this.f = f;
      }
   }

   static final class Output {
      float forward;
      float strafe;
      boolean jump;
      boolean hasLook;
      double lookX;
      double lookY;
      double lookZ;
      int bendSign;
   }
}
