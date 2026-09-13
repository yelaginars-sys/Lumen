package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.player.RegionSelection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

public class RegionCommand extends Command {
   public RegionCommand() {
      super("region");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(this.literal("pos1").executes(context -> {
         BlockPos var2 = this.getBlockPos();
         if (var2 == null) {
            return 1;
         }

         RegionSelection.setFirst(var2);
         ChatUtils.sendMessage("Точка 1: " + this.resolvePos(var2));
         this.handleAction();
         return 1;
      }));
      builder.then(this.literal("pos2").executes(context -> {
         BlockPos var2 = this.getBlockPos();
         if (var2 == null) {
            return 1;
         }

         RegionSelection.setSecond(var2);
         ChatUtils.sendMessage("Точка 2: " + this.resolvePos(var2));
         this.handleAction();
         return 1;
      }));
      builder.then(this.literal("clear").executes(context -> {
         RegionSelection.clear();
         ChatUtils.sendMessage("Область сброшена");
         return 1;
      }));
      builder.then(this.literal("info").executes(context -> {
         this.handleAction();
         return 1;
      }));
      builder.executes(context -> {
         ChatUtils.sendMessage("Использование: .region pos1 | pos2 | clear | info");
         return 1;
      });
   }

   private BlockPos getBlockPos() {
      if (mc.player == null) {
         ChatUtils.sendMessage("Нет игрока");
         return null;
      } else {
         return mc.player.getBlockPos();
      }
   }

   private void handleAction() {
      if (!RegionSelection.isComplete()) {
         ChatUtils.sendMessage("Область неполная: отметьте " + (RegionSelection.getFirst() == null ? "pos1" : "pos2"));
      } else {
         int var1 = RegionSelection.maxX() - RegionSelection.minX() + 1;
         int var2 = RegionSelection.maxY() - RegionSelection.minY() + 1;
         int var3 = RegionSelection.maxZ() - RegionSelection.minZ() + 1;
         ChatUtils.sendMessage("Область " + var1 + "x" + var2 + "x" + var3 + " готова");
      }
   }

   private String resolvePos(BlockPos pos) {
      return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
   }
}
