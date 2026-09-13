package dlc.lumen.client.bots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.core.BotSession;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public enum BotRegistry implements QClient {
   INSTANCE;

   private static final Gson GSON_BUILDER = new GsonBuilder().create();
   private static final long TIMESTAMP = 1000L;
   private static final long TIMESTAMP2 = 1000L;
   private final BotStatus botStatus = new BotStatus();
   private final List<BotStatus> botStatuss = new ArrayList<>();
   private long timestamp;
   private long timestamp2;
   private long timestamp3 = System.currentTimeMillis();
   private String text = "";

   public List<BotStatus> others() {
      return this.botStatuss;
   }

   public BotStatus own() {
      return this.botStatus;
   }

   public void requestHelp(String reason) {
      this.text = reason == null ? "" : reason;
   }

   public List<BotStatus> monitorRows() {
      long var1 = System.currentTimeMillis();
      ArrayList var3 = new ArrayList();
      this.botStatus.self = true;
      this.botStatus.local = true;
      var3.add(this.botStatus);

      for (BotSession var5 : BotManager.INSTANCE.bots()) {
         var3.add(this.computeBotStatus(var5, var1));
      }

      for (BotAutoJoin.Entry var7 : BotAutoJoin.INSTANCE.entries()) {
         if (var7.name != null && !var7.name.isBlank() && !BotAutoJoin.INSTANCE.isConnected(var7.name)) {
            var3.add(this.computeBotStatus2(var7, var1));
         }
      }

      var3.addAll(this.botStatuss);
      return var3;
   }

   private BotStatus computeBotStatus(BotSession bot, long now) {
      BotStatus var4 = new BotStatus();
      var4.name = bot.getName();
      var4.server = bot.getAddress();
      var4.stamp = now;
      var4.local = true;
      var4.inGame = bot.isJoined() && bot.player() != null && bot.world() != null;
      if (bot.player() != null) {
         var4.health = bot.player().getHealth();
         var4.food = bot.player().getHungerManager().getFoodLevel();
         var4.inventory = computereturn(bot.player());
      }

      var4.needsHelp = bot.needsHelp();
      var4.helpReason = bot.helpReason();
      String var5 = BotTasks.INSTANCE.statusOf(bot.getName());
      if (BotTasks.INSTANCE.isSwitchingAnarchy(bot.getName())) {
         var4.activity = "меняет анархию";
      } else if (!var5.isEmpty()) {
         var4.activity = var5;
         var4.profit = BotTasks.INSTANCE.profitOf(bot.getName());
         var4.anarchy = BotTasks.INSTANCE.anarchyOf(bot.getName());
      } else if (BotManager.INSTANCE.isViewingBot(bot)) {
         var4.activity = "смотришь";
      } else if (bot.isJoined()) {
         var4.activity = "простой";
      } else {
         var4.activity = bot.getStatus();
      }

      return var4;
   }

   private BotStatus computeBotStatus2(BotAutoJoin.Entry entry, long now) {
      BotStatus var4 = new BotStatus();
      var4.name = entry.name;
      var4.server = entry.address == null ? "" : entry.address;
      var4.stamp = now;
      var4.pending = true;
      var4.inGame = false;
      var4.activity = BotAutoJoin.INSTANCE.statusOf(entry);
      return var4;
   }

   @EventLink
   public void onUpdate(EventTickPre event) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.timestamp >= 1000L) {
         this.timestamp = var2;
         this.updateState(var2);
      }

      if (var2 - this.timestamp2 >= 1000L) {
         this.timestamp2 = var2;
         this.updateState3(var2);
      }
   }

   private void updateState(long now) {
      this.botStatus.name = this.resolveString2();
      this.botStatus.server = this.resolveString3();
      this.botStatus.stamp = now;
      this.botStatus.uptimeMs = now - this.timestamp3;
      this.botStatus.inGame = mc.player != null && mc.world != null;
      this.botStatus.helpReason = this.text;
      this.botStatus.needsHelp = !this.text.isEmpty();
      if (this.botStatus.inGame) {
         this.botStatus.health = mc.player.getHealth();
         this.botStatus.food = mc.player.getHungerManager().getFoodLevel();
         this.botStatus.inventory = this.computefloat();
      } else {
         this.botStatus.health = 0.0F;
         this.botStatus.food = 0;
         this.botStatus.inventory = 0.0F;
      }

      AutoMine var3 = AutoMine.INSTANCE;
      if (var3 != null && var3.isEnable()) {
         this.botStatus.activity = var3.statusLine();
         this.botStatus.anarchy = var3.currentAnarchy();
         this.botStatus.profit = var3.getProfit();
         this.botStatus.hooks = var3.getHooksObtained();
      } else {
         this.botStatus.activity = this.resolveString();
         this.botStatus.anarchy = 0;
         this.botStatus.profit = 0L;
         this.botStatus.hooks = 0;
      }

      this.updateState2(GSON_BUILDER.toJson(this.botStatus));
   }

   private String resolveString() {
      if (mc.currentScreen instanceof DisconnectedScreen) {
         return "отключён";
      } else if (mc.world == null || mc.player == null) {
         return "не в игре";
      } else {
         return ModuleClass.INSTANCE != null && ModuleClass.cocoaFarm != null && ModuleClass.cocoaFarm.isEnable() ? "какао" : "простой";
      }
   }

   private float computefloat() {
      return computereturn(mc.player);
   }

   private static float computereturn(PlayerEntity player) {
      if (player == null) {
         return 0.0F;
      }

      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = player.getInventory().getStack(var2);
         if (!var3.isEmpty()) {
            var1++;
         }
      }

      return var1 / 36.0F;
   }

   private String resolveString2() {
      return mc.getSession() != null && mc.getSession().getUsername() != null ? mc.getSession().getUsername() : "?";
   }

   private String resolveString3() {
      if (mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address != null) {
         return mc.getCurrentServerEntry().address;
      } else {
         return mc.isInSingleplayer() ? "одиночная" : "";
      }
   }

   private void updateState2(String json) {
      try {
         File var2 = directory();
         Files.createDirectories(var2.toPath());
         Path var3 = new File(var2, this.resolveString4()).toPath();
         Path var4 = new File(var2, this.resolveString4() + ".tmp").toPath();
         Files.write(var4, json.getBytes(StandardCharsets.UTF_8));

         try {
            Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (Throwable var6) {
            Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Throwable var7) {
      }
   }

   private String resolveString4() {
      return computereturn2(this.resolveString2()) + "_" + ProcessHandle.current().pid() + ".json";
   }

   private void updateState3(long now) {
      this.botStatuss.clear();
      File var3 = directory();
      File[] var4 = var3.listFiles((d, name) -> name.endsWith(".json"));
      if (var4 != null) {
         String var5 = this.resolveString4();

         for (File var9 : var4) {
            if (!var9.getName().equals(var5)) {
               try {
                  String var10 = new String(Files.readAllBytes(var9.toPath()), StandardCharsets.UTF_8);
                  BotStatus var11 = (BotStatus)GSON_BUILDER.fromJson(var10, BotStatus.class);
                  if (var11 != null && var11.name != null) {
                     if (now - var11.stamp > 60000L) {
                        var9.delete();
                     } else {
                        this.botStatuss.add(var11);
                     }
                  }
               } catch (Throwable var12) {
               }
            }
         }

         this.botStatuss.sort(Comparator.comparing(s -> s.name == null ? "" : s.name));
      }
   }

   public void shutdown() {
      try {
         new File(directory(), this.resolveString4()).delete();
      } catch (Throwable var2) {
      }
   }

   public static File directory() {
      File var0 = Lumen.INSTANCE != null && Lumen.INSTANCE.globalsDir != null ? Lumen.INSTANCE.globalsDir : new File("lumen");
      return new File(var0, "bots");
   }

   private static String computereturn2(String name) {
      String var1 = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
      return var1.isEmpty() ? "bot" : var1;
   }
}
