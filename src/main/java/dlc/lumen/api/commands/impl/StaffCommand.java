package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

public class StaffCommand extends Command {
   public StaffCommand() {
      super("staff");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(
                  this.literal("add").then(this.arg("player", StringArgumentType.word()).suggests((context, builder1) -> {
                     for (PlayerListEntry var3 : mc.getNetworkHandler().getPlayerList()) {
                        String var4 = var3.getProfile().getName();
                        if (var4.toLowerCase().startsWith(builder1.getRemaining().toLowerCase())) {
                           builder1.suggest(var4);
                        }
                     }

                     return builder1.buildFuture();
                  }).executes(context -> {
                     String var1 = (String)context.getArgument("player", String.class);
                     if (!Lumen.INSTANCE.staffStorage.isStaff(var1)) {
                        Lumen.INSTANCE.staffStorage.add(var1);
                        ChatUtils.sendMessage("Игрок " + var1 + " добавлен в список стаффов!");
                     } else {
                        ChatUtils.sendMessage("Игрок " + var1 + " уже в списке стаффов!");
                     }

                     return 1;
                  }))
               ))
               .then(
                  this.literal("remove")
                     .then(
                        this.arg("player", StringArgumentType.word())
                           .suggests(
                              (context, builder1) -> {
                                 Lumen.INSTANCE
                                    .staffStorage
                                    .getStaffs()
                                    .stream()
                                    .sorted(String::compareTo)
                                    .filter(name -> name.startsWith(builder1.getRemaining()))
                                    .forEach(builder1::suggest);
                                 return builder1.buildFuture();
                              }
                           )
                           .executes(context -> {
                              String var1 = (String)context.getArgument("player", String.class);
                              if (Lumen.INSTANCE.staffStorage.isStaff(var1)) {
                                 Lumen.INSTANCE.staffStorage.remove(var1);
                                 ChatUtils.sendMessage("Игрок " + var1 + " удалён из списка стаффов!");
                              } else {
                                 ChatUtils.sendMessage("Игрок " + var1 + " не найден в списке стаффов!");
                              }

                              return 1;
                           })
                     )
               ))
            .then(this.literal("list").executes(context -> {
               StringBuilder var1 = new StringBuilder();
               if (Lumen.INSTANCE.staffStorage.getStaffs().isEmpty()) {
                  ChatUtils.sendMessage("Список стаффов пуст!");
               } else {
                  for (int var2 = 0; var2 < Lumen.INSTANCE.staffStorage.getStaffs().size(); var2++) {
                     var1.append(Lumen.INSTANCE.staffStorage.getStaffs().get(var2));
                     if (var2 < Lumen.INSTANCE.staffStorage.getStaffs().size() - 1) {
                        var1.append(", ");
                     }
                  }

                  var1.append(".");
                  ChatUtils.sendMessage("Стаффы: " + var1);
               }

               return 1;
            })))
         .then(this.literal("clear").executes(context -> {
            if (!Lumen.INSTANCE.staffStorage.isEmpty()) {
               Lumen.INSTANCE.staffStorage.clear();
               ChatUtils.sendMessage("Список стаффов очищен!");
            } else {
               ChatUtils.sendMessage("Список стаффов пуст!");
            }

            return 1;
         }));
   }
}
