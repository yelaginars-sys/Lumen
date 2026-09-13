package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;

public class VClipCommand extends Command {
   public VClipCommand() {
      super("vclip");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(this.arg("Y", IntegerArgumentType.integer()).executes(context -> {
         int var1 = (Integer)context.getArgument("Y", Integer.class);
         mc.player.setPosition(mc.player.getX(), mc.player.getY() + var1, mc.player.getZ());
         return 1;
      }));
      builder.then(this.literal("up").executes(context -> {
         this.handleUp(true);
         return 1;
      }));
      builder.then(this.literal("down").executes(context -> {
         this.handleUp(false);
         return 1;
      }));
   }

   private void handleUp(boolean up) {
      if (mc.player != null && mc.world != null) {
         int var2 = mc.player.getBlockY();
         int var3 = mc.world.getBottomY();
         int var4 = mc.world.getTopYInclusive() - 2;
         int var5 = up ? 1 : -1;
         int var6 = up ? var2 + 1 : var2 - 1;
         int var7 = up ? var4 : var3;

         for (int var8 = var6; up ? var8 <= var7 : var8 >= var7; var8 += var5) {
            if (this.checkY(var8)) {
               VoxelShape var9 = mc.world
                  .getBlockState(new BlockPos(mc.player.getBlockX(), var8 - 1, mc.player.getBlockZ()))
                  .getCollisionShape(mc.world, new BlockPos(mc.player.getBlockX(), var8 - 1, mc.player.getBlockZ()));
               double var10 = var9.isEmpty() ? 0.0 : var9.getMax(Axis.Y);
               mc.player.setPosition(mc.player.getX(), var8 + var10, mc.player.getZ());
               return;
            }
         }
      }
   }

   private boolean checkY(int y) {
      BlockPos var2 = new BlockPos(mc.player.getBlockX(), y - 1, mc.player.getBlockZ());
      BlockPos var3 = var2.up();
      BlockPos var4 = var3.up();
      BlockState var5 = mc.world.getBlockState(var2);
      return var5.getCollisionShape(mc.world, var2).isEmpty()
         ? false
         : mc.world.getBlockState(var3).getCollisionShape(mc.world, var3).isEmpty() && mc.world.getBlockState(var4).getCollisionShape(mc.world, var4).isEmpty();
   }
}
