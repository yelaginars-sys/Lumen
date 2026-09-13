package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.cmd.macro.Macro;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import java.lang.reflect.Field;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class MacroCommand extends Command {
   public MacroCommand() {
      super("macro");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(
                  this.literal("add")
                     .then(this.arg("name", StringArgumentType.word()).then(this.arg("bind", StringArgumentType.word()).suggests((context, builder1) -> {
                        for (Field var5 : GLFW.class.getDeclaredFields()) {
                           String var6 = var5.getName();
                           if (var6.startsWith("GLFW_KEY_")) {
                              String var7 = var6.replace("GLFW_KEY_", "");
                              if (var7.startsWith(builder1.getRemaining())) {
                                 builder1.suggest(var7);
                              }
                           }
                        }

                        if ("NONE".startsWith(builder1.getRemaining().toUpperCase())) {
                           builder1.suggest("NONE");
                        }

                        return builder1.buildFuture();
                     }).then(this.arg("command", StringArgumentType.greedyString()).executes(context -> {
                        String var1 = (String)context.getArgument("name", String.class);
                        String var2 = ((String)context.getArgument("bind", String.class)).toUpperCase();
                        String var3 = (String)context.getArgument("command", String.class);
                        if (Lumen.INSTANCE.macroStorage.getMacro(var1) != null) {
                           ChatUtils.sendMessage("Макрос " + var1 + " уже существует!");
                           return 1;
                        }

                        try {
                           int var4 = "NONE".equals(var2) ? -1 : GLFW.class.getField("GLFW_KEY_" + var2).getInt(null);
                           Lumen.INSTANCE.macroStorage.add(new Macro(var1, var3, new BindSetting("bind", var4)));
                           ChatUtils.sendMessage("Макрос " + var1 + " был добавлен!");
                        } catch (Exception var5) {
                           ChatUtils.sendMessage("Неверный бинд: " + var2);
                        }

                        return 1;
                     }))))
               ))
               .then(this.literal("remove").then(this.arg("name", StringArgumentType.word()).suggests((context, builder1) -> {
                  Lumen.INSTANCE.macroStorage.getNames().stream().filter(name -> name.startsWith(builder1.getRemaining())).forEach(builder1::suggest);
                  return builder1.buildFuture();
               }).executes(context -> {
                  String var1 = (String)context.getArgument("name", String.class);
                  if (Lumen.INSTANCE.macroStorage.isEmpty()) {
                     ChatUtils.sendMessage("Список макросов пуст!");
                     return 1;
                  } else {
                     Macro var2 = Lumen.INSTANCE.macroStorage.getMacro(var1);
                     if (var2 == null) {
                        ChatUtils.sendMessage("Макрос " + var1 + " не найден!");
                        return 1;
                     } else {
                        Lumen.INSTANCE.macroStorage.remove(var2);
                        ChatUtils.sendMessage("Макрос " + var1 + " был удалён!");
                        return 1;
                     }
                  }
               }))))
            .then(this.literal("list").executes(context -> {
               StringBuilder var1 = new StringBuilder();
               if (Lumen.INSTANCE.macroStorage.getNames().isEmpty()) {
                  ChatUtils.sendMessage("Список макросов пуст!");
               } else {
                  for (int var2 = 0; var2 < Lumen.INSTANCE.macroStorage.getNames().size(); var2++) {
                     var1.append(Lumen.INSTANCE.macroStorage.getNames().get(var2));
                     if (var2 < Lumen.INSTANCE.macroStorage.getNames().size() - 1) {
                        var1.append(", ");
                     }
                  }

                  var1.append(".");
                  ChatUtils.sendMessage("Макросы: " + var1);
               }

               return 1;
            })))
         .then(this.literal("clear").executes(context -> {
            if (!Lumen.INSTANCE.macroStorage.isEmpty()) {
               Lumen.INSTANCE.macroStorage.clear();
               ChatUtils.sendMessage("Все макросы были удалены!");
            } else {
               ChatUtils.sendMessage("Список макросов пуст!");
            }

            return 1;
         }));
   }
}
