package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.social.GlobalSocialManager;
import java.util.List;
import net.minecraft.command.CommandSource;

public class GlobalCommand extends Command {
   public GlobalCommand() {
      super("global");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.executes(
                                          context -> {
                                             handleAction();
                                             return 1;
                                          }
                                       ))
                                       .then(((LiteralArgumentBuilder)this.literal("name").executes(context -> {
                                          String var1 = GlobalSocialManager.INSTANCE.getNickname();
                                          ChatUtils.sendMessage(
                                             var1.isBlank() ? "Глобальный ник не задан. Используйте .global name \"ник\"" : "Ваш глобальный ник: " + var1
                                          );
                                          return 1;
                                       })).then(this.arg("nickname", StringArgumentType.string()).executes(context -> {
                                          GlobalSocialManager.INSTANCE.requestNickname(StringArgumentType.getString(context, "nickname"));
                                          return 1;
                                       }))))
                                    .then(((LiteralArgumentBuilder)this.literal("api").executes(context -> {
                                       ChatUtils.sendMessage("Global API: " + GlobalSocialManager.INSTANCE.getApiBaseUrl());
                                       return 1;
                                    })).then(this.arg("url", StringArgumentType.greedyString()).executes(context -> {
                                       GlobalSocialManager.INSTANCE.setApiBaseUrl(StringArgumentType.getString(context, "url"));
                                       return 1;
                                    }))))
                                 .then(this.literal("invite").then(this.arg("nickname", StringArgumentType.string()).suggests((context, suggestions) -> {
                                    String var2 = GlobalSocialManager.INSTANCE.getNickname();

                                    for (String var4 : GlobalSocialManager.INSTANCE.getKnownNicknames()) {
                                       if (!var4.equalsIgnoreCase(var2) && var4.toLowerCase().startsWith(suggestions.getRemainingLowerCase())) {
                                          suggestions.suggest(var4);
                                       }
                                    }

                                    return suggestions.buildFuture();
                                 }).executes(context -> {
                                    GlobalSocialManager.INSTANCE.invite(StringArgumentType.getString(context, "nickname"));
                                    return 1;
                                 }))))
                              .then(this.literal("accept").then(this.arg("nickname", StringArgumentType.string()).suggests((context, suggestions) -> {
                                 for (String var3 : GlobalSocialManager.INSTANCE.getPendingInviteSenders()) {
                                    if (var3.toLowerCase().startsWith(suggestions.getRemainingLowerCase())) {
                                       suggestions.suggest(var3);
                                    }
                                 }

                                 return suggestions.buildFuture();
                              }).executes(context -> {
                                 GlobalSocialManager.INSTANCE.acceptInvite(StringArgumentType.getString(context, "nickname"));
                                 return 1;
                              }))))
                           .then(this.literal("deny").then(this.arg("nickname", StringArgumentType.string()).suggests((context, suggestions) -> {
                              for (String var3 : GlobalSocialManager.INSTANCE.getPendingInviteSenders()) {
                                 if (var3.toLowerCase().startsWith(suggestions.getRemainingLowerCase())) {
                                    suggestions.suggest(var3);
                                 }
                              }

                              return suggestions.buildFuture();
                           }).executes(context -> {
                              GlobalSocialManager.INSTANCE.denyInvite(StringArgumentType.getString(context, "nickname"));
                              return 1;
                           }))))
                        .then(this.literal("leave").executes(context -> {
                           GlobalSocialManager.INSTANCE.leaveParty();
                           return 1;
                        })))
                     .then(this.literal("sync").executes(context -> {
                        GlobalSocialManager.INSTANCE.forceNextSync();
                        ChatUtils.sendMessage("Принудительная синхронизация запущена.");
                        return 1;
                     })))
                  .then(this.literal("party").executes(context -> {
                     handleAction2();
                     return 1;
                  })))
               .then(this.literal("list").executes(context -> {
                  handleAction2();
                  return 1;
               })))
            .then(this.literal("status").executes(context -> {
               handleAction2();
               return 1;
            })))
         .then(this.literal("help").executes(context -> {
            handleAction();
            return 1;
         }));
   }

   private static void handleAction() {
      ChatUtils.sendMessage("Global nick: " + resolveValue(GlobalSocialManager.INSTANCE.getNickname()));
      ChatUtils.sendMessage("API: " + resolveValue(GlobalSocialManager.INSTANCE.getApiBaseUrl()));
      ChatUtils.sendMessage(".global name \"ник\" - установить глобальный ник");
      ChatUtils.sendMessage(".global invite <ник> - пригласить в пати");
      ChatUtils.sendMessage(".global accept <ник> / deny <ник> - ответить на инвайт");
      ChatUtils.sendMessage(".global party - показать пати и инвайты");
      ChatUtils.sendMessage(".global api <url> - сменить адрес API");
      ChatUtils.sendMessage(".irc <текст> - написать в глобальный IRC");
   }

   private static void handleAction2() {
      GlobalSocialManager var0 = GlobalSocialManager.INSTANCE;
      if (!var0.isInParty()) {
         ChatUtils.sendMessage("Сейчас вы не в пати.");
      }

      List var1 = var0.getVisiblePartyMembers();
      if (var1.isEmpty()) {
         ChatUtils.sendMessage("Участников пати рядом не найдено.");
      } else {
         StringBuilder var2 = new StringBuilder();

         for (int var3 = 0; var3 < var1.size(); var3++) {
            GlobalSocialManager.PartyMemberSnapshot var4 = (GlobalSocialManager.PartyMemberSnapshot)var1.get(var3);
            var2.append(var4.nickname()).append(" [").append(var4.mcName()).append("]");
            if (var3 < var1.size() - 1) {
               var2.append(", ");
            }
         }

         ChatUtils.sendMessage("Пати: " + var0.getNickname() + " + " + var2);
      }

      List var5 = var0.getPendingInviteSenders();
      if (var5.isEmpty()) {
         ChatUtils.sendMessage("Ожидающих инвайтов нет.");
      } else {
         ChatUtils.sendMessage("Инвайты: " + String.join(", ", var5));
      }
   }

   private static String resolveValue(String value) {
      return value != null && !value.isBlank() ? value : "не задан";
   }
}
