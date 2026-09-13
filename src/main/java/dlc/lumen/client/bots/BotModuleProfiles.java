package dlc.lumen.client.bots;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.notification.NotificationManager;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.core.BotSession;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class BotModuleProfiles implements QClient {
   private static final String TEXT = "__owner__";
   private static final Map<String, Set<String>> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
   private static String text = "__owner__";

   private BotModuleProfiles() {
   }

   public static void switchTo(String botName) {
      String var1 = resolveString(botName);
      if (!var1.equals(text)) {
         List var2 = computereturn();
         if (var2.isEmpty()) {
            text = var1;
         } else {
            Set var3 = computeSet(var2);
            CONCURRENT_HASH_MAP.put(text, var3);
            handleEvent(text, var3);
            handleEvent3(var2, CONCURRENT_HASH_MAP.computeIfAbsent(var1, name -> new HashSet<>()));
            handleEvent2(var1);
            text = var1;
         }
      }
   }

   public static void switchToOwner() {
      switchTo(null);
   }

   public static List<String> enabledOf(String botName) {
      String var1 = resolveString(botName);
      if (var1.equals(text)) {
         return computereturn().stream().filter(Module::isEnable).map(Module::getName).sorted().toList();
      }

      Set var2 = CONCURRENT_HASH_MAP.get(var1);
      return var2 == null ? List.of() : var2.stream().sorted().toList();
   }

   public static void forget(String botName) {
      CONCURRENT_HASH_MAP.remove(resolveString(botName));
   }

   private static void handleEvent(String sessionKey, Set<String> enabled) {
      if (!"__owner__".equals(sessionKey)) {
         BotSession var2 = BotManager.INSTANCE.find(sessionKey);
         if (var2 != null) {
            BotTasks.INSTANCE.setMining(var2.getName(), enabled.contains(AutoMine.INSTANCE.getName()));
         }
      }
   }

   private static void handleEvent2(String sessionKey) {
      if (!"__owner__".equals(sessionKey)) {
         BotSession var1 = BotManager.INSTANCE.find(sessionKey);
         if (var1 != null) {
            BotTasks.INSTANCE.setMining(var1.getName(), false);
         }
      }
   }

   private static Set<String> computeSet(List<Module> modules) {
      HashSet var1 = new HashSet();

      for (Module var3 : modules) {
         if (var3.isEnable()) {
            var1.add(var3.getName());
         }
      }

      return var1;
   }

   private static void handleEvent3(List<Module> modules, Set<String> enabled) {
      boolean var2 = NotificationManager.isMuted();
      NotificationManager.setMuted(true);

      try {
         for (Module var4 : modules) {
            boolean var5 = enabled.contains(var4.getName());
            if (var4.isEnable() != var5) {
               var4.setEnabled(var5);
            }
         }
      } finally {
         NotificationManager.setMuted(var2);
      }
   }

   private static List<Module> computereturn() {
      return ModuleClass.INSTANCE == null
         ? List.of()
         : ModuleClass.INSTANCE
            .getObject()
            .stream()
            .filter(module -> module.getCategory() != Module.ModuleCategory.RENDER && module.getCategory() != Module.ModuleCategory.MODELS)
            .toList();
   }

   private static String resolveString(String botName) {
      return botName != null && !botName.isBlank() ? botName.toLowerCase(Locale.ROOT) : "__owner__";
   }
}
