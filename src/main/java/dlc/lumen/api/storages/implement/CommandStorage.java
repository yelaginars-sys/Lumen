package dlc.lumen.api.storages.implement;

import com.mojang.brigadier.CommandDispatcher;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.commands.impl.BindCommand;
import dlc.lumen.api.commands.impl.BotCommand;
import dlc.lumen.api.commands.impl.CocoaCommand;
import dlc.lumen.api.commands.impl.ConfigCommand;
import dlc.lumen.api.commands.impl.FriendCommand;
import dlc.lumen.api.commands.impl.GPSCommand;
import dlc.lumen.api.commands.impl.GlobalCommand;
import dlc.lumen.api.commands.impl.IrcCommand;
import dlc.lumen.api.commands.impl.MacroCommand;
import dlc.lumen.api.commands.impl.NeuroCommand;
import dlc.lumen.api.commands.impl.RCTCommand;
import dlc.lumen.api.commands.impl.RegionCommand;
import dlc.lumen.api.commands.impl.StaffCommand;
import dlc.lumen.api.commands.impl.VClipCommand;
import dlc.lumen.api.commands.impl.VoiceCommand;
import dlc.lumen.client.render.figura.FiguraCommand;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

public class CommandStorage {
   private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher();
   private final List<Command> commands = new ArrayList<>();
   private String prefix = ".";

    public CommandStorage() {
       this.registerCommands();
    }

    private void registerCommands() {
      this.handleCommand(
         new GlobalCommand(),
         new VoiceCommand(),
         new IrcCommand(),
         new FriendCommand(),
         new ConfigCommand(),
         new MacroCommand(),
         new BotCommand(),
         new GPSCommand(),
         new BindCommand(),
         new RCTCommand(),
         new StaffCommand(),
         new VClipCommand(),
         new RegionCommand(),
         new CocoaCommand(),
         new NeuroCommand(),
         new FiguraCommand()
      );
   }

   public CommandSource getSource() {
      return new ClientCommandSource(null, MinecraftClient.getInstance());
   }

   private void handleCommand(Command... command) {
      for (Command var5 : command) {
         var5.register(this.dispatcher);
         this.commands.add(var5);
      }
   }

   @Generated
   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   @Generated
   public List<Command> getCommands() {
      return this.commands;
   }

   @Generated
   public String getPrefix() {
      return this.prefix;
   }

   @Generated
   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}
