package dlc.lumen.client.render.figura;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import java.util.List;
import net.minecraft.command.CommandSource;

public class FiguraCommand extends Command {
   public FiguraCommand() {
      super("figura");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(this.literal("install").executes(ctx -> {
         if (!this.helper()) {
            return 1;
         }

         FiguraBridge.installBundledAvatars();
         ChatUtils.sendMessage("§aFigura: аватары распакованы в папку Figura. Открой гардероб Figura или §f.figura set <имя>");
         return 1;
      }));
      builder.then(this.literal("list").executes(ctx -> {
         FiguraBridge.installBundledAvatars();
         List var1 = FiguraBridge.listAvatars();
         if (var1.isEmpty()) {
            ChatUtils.sendMessage("§eFigura: аватаров не найдено");
         } else {
            ChatUtils.sendMessage("§aFigura аватары (" + var1.size() + "): §f" + String.join(", ", var1));
         }

         return 1;
      }));
      builder.then(this.literal("set").then(this.arg("name", StringArgumentType.greedyString()).executes(ctx -> {
         if (!this.helper()) {
            return 1;
         }

         FiguraBridge.installBundledAvatars();
         String var2 = StringArgumentType.getString(ctx, "name");
         String var3 = this.helper2(var2);
         if (var3 == null) {
            ChatUtils.sendMessage("§cFigura: аватар «§f" + var2 + "§c» не найден. §7.figura list");
            return 1;
         }

         if (FiguraBridge.applyAvatar(var3)) {
            ChatUtils.sendMessage("§aFigura: надет «§f" + var3 + "§a»");
         } else {
            ChatUtils.sendMessage("§cFigura: не удалось применить «§f" + var3 + "§c»");
         }

         return 1;
      })));
      builder.then(this.literal("off").executes(ctx -> {
         if (!this.helper()) {
            return 1;
         }

         FiguraBridge.clearAvatar();
         ChatUtils.sendMessage("§aFigura: аватар снят");
         return 1;
      }));
   }

   private boolean helper() {
      if (!FiguraBridge.isFiguraPresent()) {
         ChatUtils.sendMessage("§cFigura не загружен (вложенный мод не активен)");
         return false;
      } else {
         return true;
      }
   }

   private String helper2(String name) {
      List<String> var2 = FiguraBridge.listAvatars();

      for (String var4 : var2) {
         if (var4.equalsIgnoreCase(name)) {
            return var4;
         }
      }

      for (String var8 : var2) {
         String var5 = var8.contains("/") ? var8.substring(var8.lastIndexOf(47) + 1) : var8;
         if (var5.equalsIgnoreCase(name)) {
            return var8;
         }
      }

      String var7 = name.toLowerCase();

      for (String var10 : var2) {
         if (var10.toLowerCase().contains(var7)) {
            return var10;
         }
      }

      return null;
   }
}
