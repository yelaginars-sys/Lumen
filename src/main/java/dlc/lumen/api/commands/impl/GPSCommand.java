package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.cmd.waypoint.Waypoint;
import net.minecraft.command.CommandSource;

public class GPSCommand extends Command {
   public GPSCommand() {
      super("gps");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.executes(context -> {
         this.handleAction();
         return 1;
      })).then(this.literal("zamok").executes(context -> {
         Lumen.INSTANCE.waypointStorage.set(new Waypoint(0.0, 100.0, 0.0, "Замок"));
         ChatUtils.sendMessage("Метка замка поставлена: 0, 100, 0");
         return 1;
      }))).then(((LiteralArgumentBuilder)this.literal("remove").executes(context -> {
         if (!Lumen.INSTANCE.waypointStorage.isEmpty()) {
            Lumen.INSTANCE.waypointStorage.clear();
            ChatUtils.sendMessage("Все метки удалены!");
         } else {
            ChatUtils.sendMessage("Меток не было");
         }

         return 1;
      })).then(this.arg("name", StringArgumentType.greedyString()).executes(context -> {
         String var1 = StringArgumentType.getString(context, "name").trim();
         if (Lumen.INSTANCE.waypointStorage.removeByName(var1)) {
            ChatUtils.sendMessage("Метка «" + var1 + "» удалена!");
         } else {
            ChatUtils.sendMessage("§cМетка «" + var1 + "» не найдена");
         }

         return 1;
      })))).then(this.arg("args", StringArgumentType.greedyString()).executes(context -> {
         this.handleRaw(StringArgumentType.getString(context, "args"));
         return 1;
      }));
   }

   private void handleRaw(String raw) {
      String[] var2 = raw == null ? new String[0] : raw.trim().split("\\s+");
      if (var2.length < 4) {
         this.handleAction();
      } else {
         int var3 = var2.length;

         try {
            int var4 = Integer.parseInt(var2[var3 - 3]);
            int var5 = Integer.parseInt(var2[var3 - 2]);
            int var6 = Integer.parseInt(var2[var3 - 1]);
            StringBuilder var7 = new StringBuilder();

            for (int var8 = 0; var8 < var3 - 3; var8++) {
               if (var8 > 0) {
                  var7.append(' ');
               }

               var7.append(var2[var8]);
            }

            String var10 = var7.toString();
            Lumen.INSTANCE.waypointStorage.set(new Waypoint(var4, var5, var6, var10));
            ChatUtils.sendMessage("Метка «" + var10 + "» поставлена: " + var4 + ", " + var5 + ", " + var6);
         } catch (NumberFormatException var9) {
            this.handleAction();
         }
      }
   }

   private void handleAction() {
      ChatUtils.sendMessage("§cНеверный формат. Используй: §f.gps <имя> <x> <y> <z> §7(напр. §f.gps База 100 64 -200§7)");
   }
}
