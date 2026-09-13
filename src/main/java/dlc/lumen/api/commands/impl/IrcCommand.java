package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.social.GlobalSocialManager;
import net.minecraft.command.CommandSource;

public class IrcCommand extends Command {
   public IrcCommand() {
      super("irc");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)builder.then(this.arg("message", StringArgumentType.greedyString()).executes(context -> {
         GlobalSocialManager.INSTANCE.sendGlobalChat(StringArgumentType.getString(context, "message"));
         return 1;
      }))).executes(context -> {
         ChatUtils.sendMessage("Использование: .irc <сообщение>");
         return 1;
      });
   }
}
