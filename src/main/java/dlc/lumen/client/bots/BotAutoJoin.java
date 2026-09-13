package dlc.lumen.client.bots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.client.bots.core.BotLifecycle;
import dlc.lumen.client.bots.core.BotLifecycleListener;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.core.BotSession;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public enum BotAutoJoin implements QClient {
   INSTANCE;

   private static final long IDLE_TIMEOUT_MS = 4000L;
   private static final long TIMESTAMP = 60000L;
   private static final long TIMESTAMP2 = 10000L;
   private static final long TIMESTAMP3 = 300000L;
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   private final List<BotAutoJoin.Entry> items = new ArrayList<>();
   private boolean active2;
   private boolean flag;
   private BotAutoJoin.Entry active3;
   private long timestamp;
   private long timestamp2;

   public List<BotAutoJoin.Entry> entries() {
      this.updateState4();
      return this.items;
   }

   public boolean isActive() {
      this.updateState4();
      return this.active2;
   }

   public void setActive(boolean value) {
      this.updateState4();
      if (this.active2 != value) {
         this.active2 = value;
         if (this.active2) {
            for (BotAutoJoin.Entry var3 : this.items) {
               var3.nextAttempt = 0L;
               var3.failures = 0;
               var3.note = "";
            }
         } else {
            this.active3 = null;
         }

         this.updateState5();
      }
   }

   public boolean toggle() {
      this.setActive(!this.isActive());
      return this.active2;
   }

   public BotAutoJoin.Entry add(String name, String address) {
      this.updateState4();
      BotAutoJoin.Entry var3 = this.find(name);
      if (var3 == null) {
         var3 = new BotAutoJoin.Entry(name, address);
         this.items.add(var3);
      } else {
         var3.address = address;
      }

      var3.enabled = true;
      var3.nextAttempt = 0L;
      var3.failures = 0;
      var3.note = "";
      this.updateState5();
      return var3;
   }

   public boolean remove(String name) {
      this.updateState4();
      BotAutoJoin.Entry var2 = this.find(name);
      if (var2 == null) {
         return false;
      }

      this.items.remove(var2);
      if (this.active3 == var2) {
         this.active3 = null;
      }

      BotManager.INSTANCE.remove(name);
      this.updateState5();
      return true;
   }

   public boolean toggleEntry(String name) {
      this.updateState4();
      BotAutoJoin.Entry var2 = this.find(name);
      if (var2 == null) {
         return false;
      }

      var2.enabled = !var2.enabled;
      var2.nextAttempt = 0L;
      var2.failures = 0;
      var2.note = var2.enabled ? "" : "выкл";
      if (!var2.enabled) {
         if (this.active3 == var2) {
            this.active3 = null;
         }

         BotManager.INSTANCE.remove(name);
      }

      this.updateState5();
      return var2.enabled;
   }

   public BotAutoJoin.Entry find(String name) {
      this.updateState4();
      if (name == null) {
         return null;
      }

      for (BotAutoJoin.Entry var3 : this.items) {
         if (var3.name != null && var3.name.equalsIgnoreCase(name)) {
            return var3;
         }
      }

      return null;
   }

   public boolean isConnected(String name) {
      return BotManager.INSTANCE.isConnected(name);
   }

   public String statusOf(BotAutoJoin.Entry entry) {
      if (entry == null) {
         return "";
      }

      if (!entry.enabled) {
         return "выкл";
      }

      if (this.active3 == entry) {
         return "заходит";
      }

      if (!this.active2) {
         return "автозаход выкл";
      }

      BotSession var2 = BotManager.INSTANCE.find(entry.name);
      if (var2 != null && !var2.isJoined() && var2.getState() != BotSession.State.DISCONNECTED) {
         return var2.getStatus();
      }

      long var3 = entry.nextAttempt - System.currentTimeMillis();
      if (var3 <= 0L) {
         return "в очереди";
      }

      String var5 = entry.note != null && !entry.note.isEmpty() ? entry.note + ", ждёт" : "ждёт";
      return var5 + " " + Math.max(1L, var3 / 1000L) + "с";
   }

   public boolean isJoining(String name) {
      return this.active3 != null && this.active3.name != null && this.active3.name.equalsIgnoreCase(name);
   }

   @EventLink
   public void onUpdate(EventTickPre event) {
      this.updateState4();
      if (this.active2) {
         long var2 = System.currentTimeMillis();
         if (this.active3 != null) {
            if (this.isConnected(this.active3.name)) {
               this.active3 = null;
            } else if (var2 >= this.timestamp) {
               this.updateState3(this.active3, var2, "не зашёл");
               this.active3 = null;
            }
         } else if (var2 >= this.timestamp2) {
            for (BotAutoJoin.Entry var5 : this.items) {
               if (var5.enabled && var5.name != null && !var5.name.isBlank() && var5.address != null && !var5.address.isBlank() && var2 >= var5.nextAttempt) {
                  if (!this.isConnected(var5.name)) {
                     this.active3 = var5;
                     this.timestamp = var2 + 60000L;
                     this.timestamp2 = var2 + 4000L;
                     var5.note = "";
                     BotManager.INSTANCE.connect(var5.name, var5.address);
                     return;
                  }

                  var5.failures = 0;
                  var5.note = "";
               }
            }
         }
      }
   }

   private void updateState(String name) {
      BotAutoJoin.Entry var2 = this.find(name);
      if (var2 != null) {
         var2.failures = 0;
         var2.note = "";
         var2.nextAttempt = 0L;
      }

      if (this.active3 != null && this.active3.name.equalsIgnoreCase(name)) {
         this.active3 = null;
      }
   }

   private void updateState2(String name, String reason) {
      BotAutoJoin.Entry var3 = this.find(name);
      if (var3 != null && var3.enabled) {
         if (this.active3 == var3) {
            this.active3 = null;
         }

         this.updateState3(var3, System.currentTimeMillis(), resolveString(reason));
      }
   }

   private void updateState3(BotAutoJoin.Entry entry, long now, String reason) {
      entry.failures++;
      entry.note = reason;
      entry.nextAttempt = now + resolveLong(entry.failures);
   }

   private static String resolveString(String reason) {
      if (reason != null && !reason.isBlank()) {
         String var1 = reason.replace('\n', ' ').trim();
         return var1.length() <= 24 ? var1 : var1.substring(0, 24) + "…";
      } else {
         return "кик";
      }
   }

   private static long resolveLong(int failures) {
      long var1 = 10000L << Math.min(failures - 1, 5);
      return Math.min(var1, 300000L);
   }

   private File getFile() {
      String var1 = BotManager.INSTANCE.ownerName();
      String var2 = var1.replaceAll("[^a-zA-Z0-9_-]", "_");
      return new File(BotRegistry.directory(), "autojoin_" + (var2.isEmpty() ? "default" : var2) + ".json");
   }

   private void updateState4() {
      if (!this.flag) {
         this.flag = true;

         try {
            File var1 = this.getFile();
            if (!var1.isFile()) {
               return;
            }

            String var2 = new String(Files.readAllBytes(var1.toPath()), StandardCharsets.UTF_8);
            BotAutoJoin.Save var3 = (BotAutoJoin.Save)GSON_BUILDER.fromJson(var2, BotAutoJoin.Save.class);
            if (var3 == null) {
               return;
            }

            if (var3.entries != null) {
               for (BotAutoJoin.Entry var5 : var3.entries) {
                  if (var5 != null && var5.name != null && !var5.name.isBlank()) {
                     var5.note = "";
                     this.items.add(var5);
                  }
               }
            }

            this.active2 = var3.active;
         } catch (Throwable var6) {
         }
      }
   }

   private void updateState5() {
      try {
         BotAutoJoin.Save var1 = new BotAutoJoin.Save();
         var1.entries = new ArrayList<>(this.items);
         var1.active = this.active2;
         File var2 = this.getFile();
         Files.createDirectories(var2.getParentFile().toPath());
         Files.write(var2.toPath(), GSON_BUILDER.toJson(var1).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var3) {
      }
   }

   static {
      BotLifecycle.register(new BotLifecycleListener() {
         @Override
         public void onStateChanged(BotSession bot, BotSession.State from, BotSession.State to) {
            if (to == BotSession.State.PLAY) {
               BotAutoJoin.INSTANCE.updateState(bot.getName());
            }
         }

         @Override
         public void onDisconnected(BotSession bot, String reason) {
            BotAutoJoin.INSTANCE.updateState2(bot.getName(), reason);
         }
      });
   }

   public static final class Entry {
      public String name = "";
      public String address = "";
      public boolean enabled = true;
      public transient long nextAttempt;
      public transient int failures;
      public transient String note = "";

      public Entry() {
      }

      public Entry(String name, String address) {
         this.name = name;
         this.address = address;
      }
   }

   private static final class Save {
      List<BotAutoJoin.Entry> entries = new ArrayList<>();
      boolean active;
   }
}
