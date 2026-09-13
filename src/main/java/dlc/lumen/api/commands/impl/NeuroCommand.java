package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.impl.combat.components.neuro.NeuroStyles;
import dlc.lumen.client.modules.impl.combat.components.neuro.NeuroTrainerLauncher;
import dlc.lumen.client.modules.impl.combat.components.neuro.NeuroWeights;
import dlc.lumen.client.modules.impl.combat.components.neuro.StyleRecorder;
import java.util.List;
import java.util.Locale;
import net.minecraft.command.CommandSource;

public class NeuroCommand extends Command {
   public NeuroCommand() {
      super("neuro");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(this.literal("record").then(this.arg("name", StringArgumentType.greedyString()).executes(c -> {
         String var1 = StyleRecorder.get().start(resolveValue((String)c.getArgument("name", String.class)));
         if (var1 != null) {
            ChatUtils.sendMessage("§7Neuro: " + var1);
         }

         return 1;
      })));
      builder.then(this.literal("stop").executes(c -> {
         String var1 = StyleRecorder.get().stop();
         if (var1 != null) {
            ChatUtils.sendMessage("§7Neuro: " + var1);
         }

         return 1;
      }));
      builder.then(this.literal("train").then(this.arg("name", StringArgumentType.greedyString()).executes(c -> {
         NeuroTrainerLauncher.get().start(resolveValue((String)c.getArgument("name", String.class)));
         return 1;
      })));
      builder.then(
         this.literal("play")
            .then(
               this.arg("name", StringArgumentType.greedyString())
                  .executes(
                     c -> {
                        String var1 = resolveValue((String)c.getArgument("name", String.class));
                        NeuroWeights var2 = NeuroStyles.activate(var1);
                        if (var2 == null) {
                           ChatUtils.sendMessage("§cNeuro: модель §f" + var1 + "§c не найдена или битая. Обучи её: §f.neuro train " + var1);
                        } else {
                           ChatUtils.sendMessage(
                              String.format(
                                 Locale.ROOT,
                                 "§aNeuro: играю стилем §f%s§a (obs %d, атака: %s). Режим Ауры — §fNeuro§a.",
                                 NeuroStyles.activeName(),
                                 var2.obs,
                                 var2.hasAttackHead() ? "твоя" : "нет"
                              )
                           );
                        }

                        return 1;
                     }
                  )
            )
      );
      builder.then(this.literal("off").executes(c -> {
         NeuroStyles.deactivate();
         ChatUtils.sendMessage("§aNeuro: стиль снят, дальше играет встроенная модель.");
         return 1;
      }));
      builder.then(this.literal("list").executes(c -> {
         List<String> var1 = NeuroStyles.listTrained();
         List<String> var2 = NeuroStyles.listRecorded();
         if (var1.isEmpty() && var2.isEmpty()) {
            ChatUtils.sendMessage("§7Neuro: стилей пока нет. Начни с §f.neuro record <имя>");
            return 1;
         }

         for (String var4 : var2) {
            long var5 = NeuroStyles.recordedBytes(var4) / 1048576L;
            boolean var7 = var1.contains(var4);
            ChatUtils.sendMessage(String.format(Locale.ROOT, "§7- §f%s§7: дорожки %d МБ, модель %s", var4, var5, var7 ? "§aесть" : "§cнет"));
         }

         for (String var9 : var1) {
            if (!var2.contains(var9)) {
               ChatUtils.sendMessage("§7- §f" + var9 + "§7: только модель (дорожки удалены)");
            }
         }

         return 1;
      }));
      builder.then(
         this.literal("status")
            .executes(
               c -> {
                  StyleRecorder var1 = StyleRecorder.get();
                  ChatUtils.sendMessage("§7Запись: " + (var1.isRecording() ? "§aидёт (§f" + var1.styleName() + "§a)" : "§7нет"));
                  ChatUtils.sendMessage(
                     "§7Обучение: " + (NeuroTrainerLauncher.get().isRunning() ? "§aидёт (§f" + NeuroTrainerLauncher.get().trainingStyle() + "§a)" : "§7нет")
                  );
                  String var2 = NeuroStyles.activeName();
                  ChatUtils.sendMessage(
                     "§7Играет: " + (var2 != null ? "§fстиль " + var2 : "§fвстроенная модель" + (NeuroWeights.get() == null ? " §c(весов нет!)" : ""))
                  );
                  return 1;
               }
            )
      );
   }

   private static String resolveValue(String value) {
      String var1 = value.trim();
      return var1.length() >= 2 && var1.startsWith("\"") && var1.endsWith("\"") ? var1.substring(1, var1.length() - 1) : var1;
   }
}
