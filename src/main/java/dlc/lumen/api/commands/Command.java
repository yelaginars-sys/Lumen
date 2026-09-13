package dlc.lumen.api.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dlc.lumen.api.QClient;
import lombok.Generated;
import net.minecraft.command.CommandSource;

public abstract class Command implements QClient {
   private final String command;

   public Command(String command) {
      this.command = command;
   }

   public abstract void execute(LiteralArgumentBuilder<CommandSource> var1);

   public void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder var2 = LiteralArgumentBuilder.literal(this.command);
      this.execute(var2);
      dispatcher.register(var2);
   }

   protected <T> RequiredArgumentBuilder<CommandSource, T> arg(String name, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   protected LiteralArgumentBuilder<CommandSource> literal(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   @Generated
   public String getCommand() {
      return this.command;
   }
}
