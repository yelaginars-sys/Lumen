package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.impl.player.CocoaFarm;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

public class CocoaCommand extends Command {
   public CocoaCommand() {
      super("cocoa");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(this.literal("1").executes(context -> {
         BlockPos var2 = this.getBlockPos();
         if (var2 == null) {
            return 1;
         }

         CocoaFarm.INSTANCE.setFirst(var2);
         ChatUtils.sendMessage("Cocoa точка 1: " + this.resolvePos(var2));
         this.handleAction();
         return 1;
      }));
      builder.then(this.literal("2").executes(context -> {
         BlockPos var2 = this.getBlockPos();
         if (var2 == null) {
            return 1;
         }

         CocoaFarm.INSTANCE.setSecond(var2);
         ChatUtils.sendMessage("Cocoa точка 2: " + this.resolvePos(var2));
         this.handleAction();
         return 1;
      }));
      builder.then(this.literal("clear").executes(context -> {
         CocoaFarm.INSTANCE.clearRegion();
         ChatUtils.sendMessage("Область CocoaFarm сброшена");
         return 1;
      }));
      builder.then(this.literal("info").executes(context -> {
         this.handleAction();
         return 1;
      }));
      builder.executes(context -> {
         ChatUtils.sendMessage("Использование: .cocoa 1 | 2 | clear | info");
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
      CocoaFarm var1 = CocoaFarm.INSTANCE;
      if (!var1.isRegionComplete()) {
         ChatUtils.sendMessage("Область CocoaFarm неполная: отметьте " + (var1.getFirst() == null ? "точку 1" : "точку 2"));
      } else {
         int var2 = var1.maxX() - var1.minX() + 1;
         int var3 = var1.maxY() - var1.minY() + 1;
         int var4 = var1.maxZ() - var1.minZ() + 1;
         ChatUtils.sendMessage("Область CocoaFarm " + var2 + "x" + var3 + "x" + var4 + " готова");
      }
   }

   private String resolvePos(BlockPos pos) {
      return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
   }
}
