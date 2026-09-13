package dlc.lumen.client.autobuy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemEntry {
   public String itemId;
   public String displayName;
   public String searchQuery;
   public String loreKeyword;
   public String potionEffect;
   public long maxUnitPrice;
   public long marketPrice;
   public long minUnitPrice;
   public int minDurabilityPercent;
   public boolean isArmor;
   public boolean matchAnySword;
   public boolean requireCustom;
   public boolean enabled = true;
   public List<Marker> markers = new ArrayList<>();

   public ItemEntry() {
   }

   public ItemEntry(String itemId, long maxUnitPrice) {
      this.itemId = itemId;
      this.maxUnitPrice = maxUnitPrice;
   }

   public ItemEntry(String itemId, String displayName, long maxUnitPrice) {
      this.itemId = itemId;
      this.displayName = displayName;
      this.maxUnitPrice = maxUnitPrice;
   }

   public boolean matches(String otherItemId) {
      if (otherItemId == null) {
         return false;
      } else {
         return this.matchAnySword ? otherItemId.endsWith("_sword") : otherItemId.equals(this.itemId);
      }
   }

   public String label() {
      String var1 = this.displayName != null && !this.displayName.isBlank() ? this.displayName : this.itemId;
      if (this.loreKeyword != null && !this.loreKeyword.isBlank()) {
         var1 = var1 + " [" + this.loreKeyword + "]";
      }

      if (this.matchAnySword) {
         var1 = "Меч+" + var1;
      }

      return var1;
   }

   public String query() {
      if (this.searchQuery != null && !this.searchQuery.isBlank()) {
         return this.searchQuery;
      } else {
         return this.displayName != null && !this.displayName.isBlank() ? this.displayName : this.itemId;
      }
   }

   public List<Marker> resolveMarkers() {
      if (this.markers != null && !this.markers.isEmpty()) {
         return this.markers;
      }

      String var1 = this.displayName == null ? "" : this.displayName.trim().toLowerCase(Locale.ROOT);
      String var2 = this.searchQuery == null ? "" : this.searchQuery.trim().toLowerCase(Locale.ROOT);

      for (ItemEntry var4 : ItemDatabase.LIST) {
         if (var4.markers != null && !var4.markers.isEmpty()) {
            String var5 = var4.displayName == null ? "" : var4.displayName.toLowerCase(Locale.ROOT);
            String var6 = var4.searchQuery == null ? "" : var4.searchQuery.toLowerCase(Locale.ROOT);
            boolean var7 = (var4.matchAnySword || var4.itemId != null && var4.itemId.equals(this.itemId))
               && (!var4.matchAnySword || this.itemId != null && this.itemId.endsWith("_sword"));
            if (var7) {
               if (!var1.isEmpty() && var1.equals(var5)) {
                  return var4.markers;
               }

               if (!var2.isEmpty() && var2.equals(var6)) {
                  return var4.markers;
               }
            }
         }
      }

      return this.markers != null ? this.markers : new ArrayList<>();
   }
}
