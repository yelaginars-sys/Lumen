package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import java.lang.reflect.Field;
import java.util.Optional;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class BindCommand extends Command {
   public BindCommand() {
      super("bind");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(
         this.literal("add")
            .then(
               this.arg("module", StringArgumentType.word())
                  .suggests(
                     (context, suggestionsBuilder) -> {
                        String var2 = suggestionsBuilder.getRemaining().toLowerCase();
                        ModuleClass.INSTANCE
                           .getObject()
                           .stream()
                           .map(Module::getName)
                           .filter(name -> name.toLowerCase().startsWith(var2))
                           .forEach(suggestionsBuilder::suggest);
                        return suggestionsBuilder.buildFuture();
                     }
                  )
                  .then(this.arg("key", StringArgumentType.word()).suggests((context, suggestionsBuilder) -> {
                     String var2 = suggestionsBuilder.getRemaining().toUpperCase();

                     for (Field var6 : GLFW.class.getDeclaredFields()) {
                        String var7 = var6.getName();
                        if (var7.startsWith("GLFW_KEY_")) {
                           String var8 = var7.replace("GLFW_KEY_", "");
                           if (var8.startsWith(var2)) {
                              suggestionsBuilder.suggest(var8);
                           }
                        }
                     }

                     if ("NONE".startsWith(var2)) {
                        suggestionsBuilder.suggest("NONE");
                     }

                     return suggestionsBuilder.buildFuture();
                  }).executes(ctx -> {
                     String var2 = (String)ctx.getArgument("module", String.class);
                     Optional var3 = this.findModuleName(var2);
                     if (var3.isEmpty()) {
                        ChatUtils.sendMessage("Модуль " + var2 + " не найден");
                        return 1;
                     }

                     Module var4 = (Module)var3.get();
                     String var5 = ((String)ctx.getArgument("key", String.class)).toUpperCase();
                     int var6 = this.calcKeyName(var5);
                     if (var6 == -1) {
                        ChatUtils.sendMessage("Клавиша " + var5 + " не найдена");
                     } else {
                        var4.setKey(var6);
                        ChatUtils.sendMessage("Модуль " + var4.getName() + " привязан к клавише " + var5);
                     }

                     return 1;
                  }))
            )
      );
      builder.then(this.literal("remove").then(this.arg("module", StringArgumentType.word()).executes(ctx -> {
         String var2 = (String)ctx.getArgument("module", String.class);
         Optional var3 = this.findModuleName(var2);
         if (var3.isEmpty()) {
            ChatUtils.sendMessage("Модуль " + var2 + " не найден");
            return 1;
         } else {
            Module var4 = (Module)var3.get();
            var4.setKey(-1);
            ChatUtils.sendMessage("Привязка клавиши для модуля " + var4.getName() + " удалена");
            return 1;
         }
      })));
      builder.then(this.literal("clear").executes(ctx -> {
         ModuleClass.INSTANCE.getObject().forEach(module -> module.setKey(-1));
         ChatUtils.sendMessage("Все привязки клавиш удалены");
         return 1;
      }));
      builder.then(
         this.literal("list")
            .executes(
               ctx -> {
                  StringBuilder var1 = new StringBuilder("Список привязанных модулей: ");
                  boolean var2 = ModuleClass.INSTANCE
                     .getObject()
                     .stream()
                     .filter(module -> module.getKey() != -1)
                     .peek(module -> var1.append("Модуль: ").append(module.getName()).append(" -> Клавиша: ").append(module.getKey()).append("\n"))
                     .findAny()
                     .isPresent();
                  if (!var2) {
                     ChatUtils.sendMessage("Нет привязанных модулей");
                  } else {
                     ChatUtils.sendMessage(var1.toString());
                  }

                  return 1;
               }
            )
      );
   }

   private Optional<Module> findModuleName(String moduleName) {
      return ModuleClass.INSTANCE.getObject().stream().filter(module -> module.getName().equalsIgnoreCase(moduleName)).findFirst();
   }

   private int calcKeyName(String keyName) {
      if ("NONE".equalsIgnoreCase(keyName)) {
         return -1;
      }

      try {
         return GLFW.class.getField("GLFW_KEY_" + keyName).getInt(null);
      } catch (NoSuchFieldException | IllegalAccessException var3) {
         return -1;
      }
   }
}
