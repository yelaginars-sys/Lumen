package dlc.lumen.client.autobuy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lot {
   public final int slot;
   public final String itemId;
   public final String name;
   public final String lore;
   public final String seller;
   public final int count;
   public final long unitPrice;
   public final long totalPrice;
   public final int durability;
   public final boolean barrier;
   public final boolean hasMarkers;
   public final Map<String, String> pbv;
   public final Set<String> customEnch;
   public final Map<String, String> customData;
   public Set<String> potionEffects = Collections.emptySet();

   public Lot(
      int slot,
      String itemId,
      String name,
      String lore,
      String seller,
      int count,
      long unitPrice,
      long totalPrice,
      int durability,
      boolean barrier,
      boolean hasMarkers,
      Map<String, String> pbv,
      Set<String> customEnch,
      Map<String, String> customData
   ) {
      this.slot = slot;
      this.itemId = itemId;
      this.name = name;
      this.lore = lore == null ? "" : lore;
      this.seller = seller == null ? "" : seller;
      this.count = count;
      this.unitPrice = unitPrice;
      this.totalPrice = totalPrice;
      this.durability = durability;
      this.barrier = barrier;
      this.hasMarkers = hasMarkers;
      this.pbv = pbv == null ? Collections.emptyMap() : pbv;
      this.customEnch = customEnch == null ? Collections.emptySet() : customEnch;
      this.customData = customData == null ? Collections.emptyMap() : customData;
   }

   public static Lot barrier(int slot) {
      return new Lot(slot, "minecraft:barrier", null, "", "", 0, 0L, 0L, 100, true, false, new HashMap<>(), new HashSet<>(), new HashMap<>());
   }
}
