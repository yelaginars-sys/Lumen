package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

public class FriendCommand extends Command {
   public FriendCommand() {
      super("friend");
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
                     if (!Lumen.INSTANCE.friendStorage.isFriend(var1)) {
                        Lumen.INSTANCE.friendStorage.add(var1);
                        ChatUtils.sendMessage("Игрок " + var1 + " добавлен в друзья!");
                     } else {
                        ChatUtils.sendMessage("Игрок " + var1 + " уже в списке друзей!");
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
                                    .friendStorage
                                    .getFriends()
                                    .stream()
                                    .sorted(String::compareTo)
                                    .filter(name -> name.startsWith(builder1.getRemaining()))
                                    .forEach(builder1::suggest);
                                 return builder1.buildFuture();
                              }
                           )
                           .executes(context -> {
                              String var1 = (String)context.getArgument("player", String.class);
                              if (Lumen.INSTANCE.friendStorage.isFriend(var1)) {
                                 Lumen.INSTANCE.friendStorage.remove(var1);
                                 ChatUtils.sendMessage("Игрок " + var1 + " удалён из друзей!");
                              } else {
                                 ChatUtils.sendMessage("Игрок " + var1 + " не найден в списке друзей!");
                              }

                              return 1;
                           })
                     )
               ))
            .then(this.literal("list").executes(context -> {
               if (Lumen.INSTANCE.friendStorage.getFriends().isEmpty()) {
                  ChatUtils.sendMessage("Список друзей пуст!");
               } else {
                  StringBuilder var1 = new StringBuilder();

                  for (int var2 = 0; var2 < Lumen.INSTANCE.friendStorage.getFriends().size(); var2++) {
                     var1.append(Lumen.INSTANCE.friendStorage.getFriends().get(var2));
                     if (var2 < Lumen.INSTANCE.friendStorage.getFriends().size() - 1) {
                        var1.append(", ");
                     }
                  }

                  ChatUtils.sendMessage("Друзья: " + var1);
               }

               return 1;
            })))
         .then(this.literal("clear").executes(context -> {
            if (!Lumen.INSTANCE.friendStorage.isEmpty()) {
               Lumen.INSTANCE.friendStorage.clear();
               ChatUtils.sendMessage("Список друзей очищен!");
            } else {
               ChatUtils.sendMessage("Список друзей пуст!");
            }

            return 1;
         }));
   }
}
