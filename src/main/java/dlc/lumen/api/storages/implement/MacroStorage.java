package dlc.lumen.api.storages.implement;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.cmd.macro.Macro;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.util.Formatting;

public class MacroStorage implements QClient {
   private final List<Macro> empty = new ArrayList<>();
   private final List<String> names = new ArrayList<>();

   public MacroStorage() {
      EventInvoker.register(this);
   }

   public void add(Macro macro) {
      if (macro != null && macro.getName() != null && !macro.getName().isBlank() && this.getMacro(macro.getName()) == null) {
         this.empty.add(macro);
         this.names.add(macro.getName());
      }
   }

   public void remove(Macro macro) {
      if (macro != null) {
         this.empty.remove(macro);
         this.names.remove(macro.getName());
      }
   }

   public void clear() {
      if (!this.empty.isEmpty()) {
         this.empty.clear();
      }

      if (!this.names.isEmpty()) {
         this.names.clear();
      }
   }

   public boolean isEmpty() {
      return this.empty.isEmpty();
   }

   public Macro getMacro(String name) {
      for (Macro var3 : this.empty) {
         if (var3.getName().equalsIgnoreCase(name)) {
            return var3;
         }
      }

      return null;
   }

   @EventLink
   public void onKey(EventBinding e) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null && mc.player.networkHandler != null && !this.empty.isEmpty()) {
         for (Macro var3 : this.empty) {
            if (var3 != null && var3.getBind() != null && var3.getBind().getKey() == e.getKey()) {
               this.handleMacro(var3);
            }
         }
      }
   }

   private void handleMacro(Macro macro) {
      String var2 = macro.getCommand();
      if (var2 != null && !var2.isBlank()) {
         if (var2.startsWith("/")) {
            mc.player.networkHandler.sendChatCommand(var2.substring(1));
         } else {
            String var3 = Lumen.INSTANCE.commandStorage.getPrefix();
            if (var3 != null && !var3.isEmpty() && var2.startsWith(var3)) {
               try {
                  Lumen.INSTANCE.commandStorage.getDispatcher().execute(var2.substring(var3.length()), Lumen.INSTANCE.commandStorage.getSource());
               } catch (CommandSyntaxException var5) {
                  ChatUtils.sendMessage(Formatting.RED + "Ошибка в использовании макроса " + macro.getName() + "!");
               }
            } else {
               mc.player.networkHandler.sendChatMessage(var2);
            }
         }
      }
   }

   @Generated
   public List<Macro> getMacros() {
      return this.empty;
   }

   @Generated
   public List<String> getNames() {
      return this.names;
   }
}
