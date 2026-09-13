package dlc.lumen.client.bots;

import dlc.lumen.api.QClient;
import dlc.lumen.client.bots.core.BotLifecycle;
import dlc.lumen.client.bots.core.BotLifecycleListener;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.core.BotSession;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.text.Text;

public enum BotTasks implements QClient {
   INSTANCE;

   private final Map<String, AutoMine> concurrentHashMap = new ConcurrentHashMap<>();
   private final Map<String, BotAnarchySwitch> concurrentHashMap2 = new ConcurrentHashMap<>();

   public boolean isMining(String botName) {
      AutoMine var2 = this.concurrentHashMap.get(resolveString(botName));
      return var2 != null && var2.isEnable();
   }

   public boolean setMining(String botName, boolean on) {
      BotSession var3 = BotManager.INSTANCE.find(botName);
      if (var3 == null) {
         return false;
      }

      AutoMine var4 = this.concurrentHashMap.computeIfAbsent(resolveString(botName), name -> {
         AutoMine var1 = new AutoMine();
         var1.copySettingsFrom(AutoMine.INSTANCE);
         return var1;
      });
      if (var4.isEnable() == on) {
         return true;
      }

      var3.runAsBot(on ? var4::startAsBotWorker : var4::stopAsBotWorker);
      return true;
   }

   public String statusOf(String botName) {
      AutoMine var2 = this.concurrentHashMap.get(resolveString(botName));
      return var2 != null && var2.isEnable() ? var2.statusLine() : "";
   }

   public long profitOf(String botName) {
      AutoMine var2 = this.concurrentHashMap.get(resolveString(botName));
      return var2 != null && var2.isEnable() ? var2.getProfit() : 0L;
   }

   public int anarchyOf(String botName) {
      AutoMine var2 = this.concurrentHashMap.get(resolveString(botName));
      return var2 != null && var2.isEnable() ? var2.currentAnarchy() : 0;
   }

   public boolean switchAnarchy(String botName, int number) {
      BotSession var3 = BotManager.INSTANCE.find(botName);
      return var3 != null && var3.isJoined()
         ? this.concurrentHashMap2.computeIfAbsent(resolveString(botName), name -> new BotAnarchySwitch(var3)).start(number)
         : false;
   }

   public void cancelAnarchy(String botName) {
      BotAnarchySwitch var2 = this.concurrentHashMap2.get(resolveString(botName));
      if (var2 != null) {
         var2.cancel();
      }
   }

   public boolean isSwitchingAnarchy(String botName) {
      BotAnarchySwitch var2 = this.concurrentHashMap2.get(resolveString(botName));
      return var2 != null && var2.isRunning();
   }

   public void tickViewed(BotSession bot) {
      AutoMine var2 = this.concurrentHashMap.get(resolveString(bot.getName()));
      if (var2 != null && var2.isEnable() && !bot.needsHelp()) {
         try {
            var2.onUpdate(null);
         } catch (Throwable var4) {
            var2.setEnable(false);
            var4.printStackTrace();
         }
      }
   }

   public void tick(BotSession bot) {
      if (!bot.needsHelp()) {
         BotAnarchySwitch var2 = this.concurrentHashMap2.get(resolveString(bot.getName()));
         if (var2 != null && var2.isRunning()) {
            try {
               var2.tick();
            } catch (Throwable var5) {
               var2.cancel();
               var5.printStackTrace();
            }
         } else {
            AutoMine var3 = this.concurrentHashMap.get(resolveString(bot.getName()));
            if (var3 != null && var3.isEnable()) {
               try {
                  var3.onUpdate(null);
               } catch (Throwable var6) {
                  var3.setEnable(false);
                  var6.printStackTrace();
               }
            }
         }
      }
   }

   public void forget(String botName) {
      this.concurrentHashMap.remove(resolveString(botName));
      this.concurrentHashMap2.remove(resolveString(botName));
   }

   private void updateState(BotSession bot, Text message) {
      AutoMine var3 = this.concurrentHashMap.get(resolveString(bot.getName()));
      if (var3 != null && var3.isEnable() && message != null) {
         var3.onBotMessage(message.getString());
      }
   }

   private static String resolveString(String name) {
      return name == null ? "" : name.toLowerCase(Locale.ROOT);
   }

   static {
      BotLifecycle.register(new BotLifecycleListener() {
         @Override
         public void onChat(BotSession bot, Text message) {
            BotTasks.INSTANCE.updateState(bot, message);
         }

         @Override
         public void onDisconnected(BotSession bot, String reason) {
            AutoMine var3 = BotTasks.INSTANCE.concurrentHashMap.get(BotTasks.resolveString(bot.getName()));
            if (var3 != null) {
               var3.setEnable(false);
            }
         }
      });
   }
}
