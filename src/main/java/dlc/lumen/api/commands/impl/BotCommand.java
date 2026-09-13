package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.client.ui.modern.ModernGui;
import net.minecraft.command.CommandSource;

public class BotCommand extends Command {
   public BotCommand() {
      super("bot");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)builder.executes(context -> calcValue())).then(this.literal("menu").executes(context -> calcValue()));
   }

   private static int calcValue() {
      mc.send(() -> {
         ModernGui.setPage(ModernGui.Page.BOTS);
         mc.setScreen(new ModernGui());
      });
      return 1;
   }
}
