package dlc.lumen.client.autobuy;

import java.util.List;

public final class ItemMatcher {
   private ItemMatcher() {
   }

   public static boolean matches(ItemEntry entry, Lot lot) {
      if (entry != null && lot != null) {
         if (entry.matchAnySword) {
            if (lot.itemId == null || !lot.itemId.endsWith("_sword")) {
               return false;
            }
         } else if (entry.itemId == null || !entry.itemId.equals(lot.itemId)) {
            return false;
         }

         List<Marker> var2 = entry.resolveMarkers();
         if (var2 != null && !var2.isEmpty()) {
            boolean var3 = false;

            for (Marker var5 : var2) {
               if (markerMatches(var5, lot)) {
                  var3 = true;
                  break;
               }
            }

            if (!var3) {
               return false;
            }
         } else if (entry.requireCustom) {
            return false;
         }

         if (entry.minDurabilityPercent > 0 && lot.durability < entry.minDurabilityPercent) {
            return false;
         }

         if (entry.loreKeyword != null && !entry.loreKeyword.isBlank()) {
            boolean var6 = TextNormalizer.keywordIn(lot.lore, entry.loreKeyword);
            boolean var8 = TextNormalizer.keywordIn(lot.name, entry.loreKeyword);
            if (!var6 && !var8) {
               return false;
            }
         }

         if (entry.potionEffect != null && !entry.potionEffect.isBlank()) {
            return lot.potionEffects.contains(entry.potionEffect);
         }

         boolean var7 = var2 != null && !var2.isEmpty();
         boolean var9 = entry.displayName != null && !entry.displayName.isBlank();
         boolean var10 = entry.loreKeyword == null || entry.loreKeyword.isBlank();
         return !entry.matchAnySword && var9 && !TextNormalizer.nameMatches(entry.displayName, lot.name)
            ? false
            : !entry.matchAnySword || !var9 || !var7 || !var10 || TextNormalizer.nameMatches(entry.displayName, lot.name);
      } else {
         return false;
      }
   }

   public static boolean markerMatches(Marker marker, Lot lot) {
      if (marker != null && marker.type != null && marker.key != null && lot != null) {
         switch (marker.type) {
            case PBV:
               String var3 = lot.pbv.get(marker.key);
               if (var3 == null) {
                  return false;
               }

               return marker.value == null || marker.value.isEmpty() || var3.equals(marker.value);
            case CUSTOM_ENCH:
               return lot.customEnch.contains(marker.key);
            case CUSTOM_DATA:
               String var2 = lot.customData.get(marker.key);
               if (var2 == null) {
                  return false;
               }

               return marker.value == null || marker.value.isEmpty() || var2.equals(marker.value);
            default:
               return false;
         }
      } else {
         return false;
      }
   }
}
