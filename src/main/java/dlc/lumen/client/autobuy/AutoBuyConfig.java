package dlc.lumen.client.autobuy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dlc.lumen.Lumen;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoBuyConfig {
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   public static AutoBuyConfig INSTANCE = new AutoBuyConfig();
   public boolean enabled = false;
   public int clickIntervalTicks = 1;
   public boolean autoRefresh = true;
   public int refreshIntervalTicks = -1;
   public int refreshSlot = -1;
   public boolean minPriceEnabled = true;
   public long minPrice = 15000L;
   public boolean autoSell = false;
   public int sellDiscountPercent = 5;
   public int relistDiscountPercent = 8;
   public boolean autoRelist = false;
   public int relistIntervalSeconds = 180;
   public boolean autoParse = true;
   public int parseIntervalSeconds = 600;
   public boolean autoPay = false;
   public long autoPayKeepBalance = 5000000L;
   public String autoPayNick = "";
   public int autoPayIntervalSeconds = 300;
   public int parsePercent = 80;
   public long defaultPriceIfMissing = 50000000L;
   public float delayPoint0 = 0.1F;
   public float delayPoint1 = 0.14F;
   public float delayPoint2 = 0.3F;
   public float delayPoint3 = 0.28F;
   public float refreshRandomizationPoint0 = 0.0F;
   public float refreshRandomizationPoint1 = 0.33F;
   public float refreshRandomizationPoint2 = 0.66F;
   public float refreshRandomizationPoint3 = 1.0F;
   public List<ItemEntry> entries = new ArrayList<>();
   public List<String> banList = new ArrayList<>();
   public List<PurchaseRecord> history = new ArrayList<>();
   public int historyMaxSize = 500;

   public static AutoBuyConfig cfg() {
      return INSTANCE;
   }

   public ItemEntry find(String itemId) {
      if (itemId == null) {
         return null;
      }

      for (ItemEntry var3 : this.entries) {
         if (var3.enabled && var3.maxUnitPrice > 0L && var3.matches(itemId)) {
            return var3;
         }
      }

      return null;
   }

   public boolean ignored(String nick) {
      if (nick != null && !nick.isBlank()) {
         for (String var3 : this.banList) {
            if (var3 != null && var3.equalsIgnoreCase(nick)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void addBan(String nick) {
      if (nick != null) {
         String var2 = nick.trim();
         if (!var2.isEmpty() && !this.ignored(var2)) {
            this.banList.add(var2);
            save();
         }
      }
   }

   public void removeBan(String nick) {
      this.banList.removeIf(s -> s != null && s.equalsIgnoreCase(nick));
      save();
   }

   public void addHistory(PurchaseRecord record) {
      if (record != null) {
         this.history.add(0, record);

         while (this.history.size() > Math.max(1, this.historyMaxSize)) {
            this.history.remove(this.history.size() - 1);
         }

         save();
      }
   }

   public void resetToDefaults() {
      this.enabled = false;
      this.autoSell = false;
      this.autoRelist = false;
      this.autoParse = true;
      this.autoPay = false;
      this.resetDelayCurveDefaults();
      this.resetRefreshRandomizationDefaults();
      save();
   }

   public void resetDelayCurveDefaults() {
      this.delayPoint0 = 0.1F;
      this.delayPoint1 = 0.14F;
      this.delayPoint2 = 0.3F;
      this.delayPoint3 = 0.28F;
   }

   public void resetRefreshRandomizationDefaults() {
      this.refreshRandomizationPoint0 = 0.0F;
      this.refreshRandomizationPoint1 = 0.33F;
      this.refreshRandomizationPoint2 = 0.66F;
      this.refreshRandomizationPoint3 = 1.0F;
   }

   public void clampDelayCurve() {
      this.delayPoint0 = computefloat(this.delayPoint0);
      this.delayPoint1 = computefloat(this.delayPoint1);
      this.delayPoint2 = computefloat(this.delayPoint2);
      this.delayPoint3 = computefloat(this.delayPoint3);
   }

   public void clampRefreshRandomization() {
      this.refreshRandomizationPoint0 = computefloat(this.refreshRandomizationPoint0);
      this.refreshRandomizationPoint1 = computefloat(this.refreshRandomizationPoint1);
      this.refreshRandomizationPoint2 = computefloat(this.refreshRandomizationPoint2);
      this.refreshRandomizationPoint3 = computefloat(this.refreshRandomizationPoint3);
   }

   public float getDelayPoint(int index) {
      return switch (index) {
         case 0 -> this.delayPoint0;
         case 1 -> this.delayPoint1;
         case 2 -> this.delayPoint2;
         default -> this.delayPoint3;
      };
   }

   public void setDelayPoint(int index, float value) {
      float var3 = computefloat(value);
      switch (index) {
         case 0:
            this.delayPoint0 = var3;
            break;
         case 1:
            this.delayPoint1 = var3;
            break;
         case 2:
            this.delayPoint2 = var3;
            break;
         default:
            this.delayPoint3 = var3;
      }
   }

   public float sampleDelayCurve(float progress) {
      this.clampDelayCurve();
      float var2 = computefloat(progress);
      float var3 = var2 * 3.0F;
      int var4 = Math.min(2, (int)Math.floor(var3));
      float var5 = var3 - var4;
      float var6 = this.getDelayPoint(var4);
      float var7 = this.getDelayPoint(var4 + 1);
      return var6 + (var7 - var6) * var5;
   }

   public int delayMsForStage(float progress, int fastMs, int slowMs) {
      return Math.round(fastMs + (slowMs - fastMs) * this.sampleDelayCurve(progress));
   }

   public float getRefreshRandomizationPoint(int index) {
      return switch (index) {
         case 0 -> this.refreshRandomizationPoint0;
         case 1 -> this.refreshRandomizationPoint1;
         case 2 -> this.refreshRandomizationPoint2;
         default -> this.refreshRandomizationPoint3;
      };
   }

   public void setRefreshRandomizationPoint(int index, float value) {
      float var3 = computefloat(value);
      switch (index) {
         case 0:
            this.refreshRandomizationPoint0 = var3;
            break;
         case 1:
            this.refreshRandomizationPoint1 = var3;
            break;
         case 2:
            this.refreshRandomizationPoint2 = var3;
            break;
         default:
            this.refreshRandomizationPoint3 = var3;
      }

      save();
   }

   public int getRefreshDelayMs(float progress) {
      this.clampRefreshRandomization();
      float var2 = computefloat(progress);
      float var3 = var2 * 3.0F;
      int var4 = Math.min(2, (int)Math.floor(var3));
      float var5 = var3 - var4;
      float var6 = this.getRefreshRandomizationPoint(var4);
      float var7 = this.getRefreshRandomizationPoint(var4 + 1);
      float var8 = var6 + (var7 - var6) * var5;
      return 50 + (int)(var8 * 950.0F);
   }

   private static Path computePath() {
      File var0 = Lumen.INSTANCE.globalsDir != null ? Lumen.INSTANCE.globalsDir : new File(System.getProperty("user.home"), ".lumen");
      return new File(var0, "autobuy.json").toPath();
   }

   public static void save() {
      try {
         Path var0 = computePath();
         Files.createDirectories(var0.getParent());
         Files.writeString(var0, GSON_BUILDER.toJson(INSTANCE));
      } catch (Exception var1) {
         System.err.println("[AutoBuy] save failed: " + var1);
      }
   }

   public static void load() {
      try {
         Path var0 = computePath();
         if (Files.exists(var0)) {
            String var1 = Files.readString(var0);
            AutoBuyConfig var2 = (AutoBuyConfig)GSON_BUILDER.fromJson(var1, AutoBuyConfig.class);
            if (var2 != null) {
               INSTANCE = var2;
               if (INSTANCE.entries == null) {
                  INSTANCE.entries = new ArrayList<>();
               }

               if (INSTANCE.banList == null) {
                  INSTANCE.banList = new ArrayList<>();
               }

               if (INSTANCE.history == null) {
                  INSTANCE.history = new ArrayList<>();
               }

               if (INSTANCE.autoPayNick == null) {
                  INSTANCE.autoPayNick = "";
               }

               INSTANCE.clampDelayCurve();
               INSTANCE.clampRefreshRandomization();
               INSTANCE.enabled = false;
               INSTANCE.autoSell = false;
               INSTANCE.autoRelist = false;
               INSTANCE.autoPay = false;
            }
         }
      } catch (Exception var3) {
         System.err.println("[AutoBuy] load failed: " + var3);
      }

      INSTANCE.reconcileWithDatabase();
      save();
   }

   public void reconcileWithDatabase() {
      for (ItemEntry var2 : ItemDatabase.LIST) {
         ItemEntry var3 = this.newItemEntry(var2);
         if (var3 == null) {
            ItemEntry var4 = new ItemEntry();
            var4.itemId = var2.itemId;
            var4.displayName = var2.displayName;
            var4.searchQuery = var2.searchQuery;
            var4.loreKeyword = var2.loreKeyword;
            var4.requireCustom = var2.requireCustom;
            var4.matchAnySword = var2.matchAnySword;
            var4.markers = new ArrayList<>(var2.markers);
            var4.enabled = false;
            var4.maxUnitPrice = 0L;
            this.entries.add(var4);
         } else {
            var3.requireCustom = var2.requireCustom;
            var3.matchAnySword = var2.matchAnySword;
            if (var3.loreKeyword == null) {
               var3.loreKeyword = var2.loreKeyword;
            }

            if (var3.searchQuery == null) {
               var3.searchQuery = var2.searchQuery;
            }

            if (var3.markers == null || var3.markers.isEmpty()) {
               var3.markers = new ArrayList<>(var2.markers);
            }
         }
      }
   }

   private ItemEntry newItemEntry(ItemEntry db) {
      for (ItemEntry var3 : this.entries) {
         boolean var4 = var3.itemId != null && var3.itemId.equals(db.itemId);
         boolean var5 = resolveString(var3.displayName).equals(resolveString(db.displayName));
         boolean var6 = resolveString(var3.loreKeyword).equals(resolveString(db.loreKeyword));
         if (var4 && var5 && var6 && var3.matchAnySword == db.matchAnySword) {
            return var3;
         }
      }

      return null;
   }

   private static String resolveString(String s) {
      return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
   }

   private static float computefloat(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }
}
