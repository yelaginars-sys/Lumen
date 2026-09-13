package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import java.io.File;
import java.util.Set;
import net.minecraft.command.CommandSource;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super("config");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(
                  this.literal("save").then(this.arg("config", StringArgumentType.word()).suggests((context, builder1) -> {
                     for (String var4 : this.getSet()) {
                        builder1.suggest(var4);
                     }

                     return builder1.buildFuture();
                  }).executes(context -> {
                     String var1 = (String)context.getArgument("config", String.class);

                     try {
                        Lumen.INSTANCE.configStorage.saveConfig(var1);
                        ChatUtils.sendMessage("Конфиг" + var1 + " успешно сохранен!");
                     } catch (Exception var3) {
                        var3.printStackTrace();
                     }

                     return 1;
                  }))
               ))
               .then(this.literal("load").then(this.arg("config", StringArgumentType.word()).suggests((context, builder1) -> {
                  for (String var4 : this.getSet()) {
                     builder1.suggest(var4);
                  }

                  return builder1.buildFuture();
               }).executes(context -> {
                  String var1 = (String)context.getArgument("config", String.class);

                  try {
                     Lumen.INSTANCE.configStorage.loadConfig(var1);
                     ChatUtils.sendMessage("Конфиг " + var1 + " успешно загружен!");
                  } catch (Exception var3) {
                     ChatUtils.sendMessage("Ошибка загрузки " + var1 + "!");
                     var3.printStackTrace();
                  }

                  return 1;
               }))))
            .then(this.literal("list").executes(context -> {
               Set<String> var2 = this.getSet();
               if (var2.isEmpty()) {
                  ChatUtils.sendMessage("Список конфигов!");
               } else {
                  StringBuilder var3 = new StringBuilder();
                  int var4 = 0;

                  for (String var6 : var2) {
                     var3.append(var6);
                     if (var4 < var2.size() - 1) {
                        var3.append(", ");
                     }

                     var4++;
                  }

                  ChatUtils.sendMessage("Конфиги: " + var3);
               }

               return 1;
            })))
         .then(this.literal("dir").executes(context -> {
            try {
               File var1 = new File(Lumen.INSTANCE.globalsDir, "configs");
               if (!var1.exists()) {
                  var1.mkdirs();
               }

               new ProcessBuilder("explorer.exe", var1.getAbsolutePath()).start();
               ChatUtils.sendMessage("Открыта папка с конфигами!");
            } catch (Exception var2) {
               ChatUtils.sendMessage("Ошибка открытия папки с конфигами!");
               var2.printStackTrace();
            }

            return 1;
         }));
   }

   private Set<String> getSet() {
      return Lumen.INSTANCE.configStorage.getAvailableConfigs();
   }
}
