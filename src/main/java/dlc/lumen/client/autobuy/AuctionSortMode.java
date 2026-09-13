package dlc.lumen.client.autobuy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

enum AuctionSortMode {
   CHEAPEST("сначала дешевые"),
   EXPENSIVE("сначала дорогие"),
   CHEAPEST_PER_UNIT("сначала дешевые за ед. товара"),
   EXPENSIVE_PER_UNIT("сначала дорогие за ед. товара"),
   NEWEST("сначала новые"),
   OLDEST("сначала старые");

   static final String SORT_KEYWORD = "сортировка";
   private static final String TEXT = "minecraft:hopper";
   private static final String TEXT2 = "✔✓☑➜▶►»›>*";
   private static final String TEXT3 = "✖✕✗✘×-";
   private static final AuctionSortMode[] AUCTION_SORT_MODE = values();
   private static final AuctionSortMode[] AUCTION_SORT_MODE2 = new AuctionSortMode[]{CHEAPEST_PER_UNIT, EXPENSIVE_PER_UNIT, CHEAPEST, EXPENSIVE, NEWEST, OLDEST};
   private final String text2;
   private final String text3;

   AuctionSortMode(String label) {
      this.text2 = label;
      this.text3 = TextNormalizer.normalize(label);
   }

   int clicksTo(AuctionSortMode target) {
      return (target.ordinal() - this.ordinal() + AUCTION_SORT_MODE.length) % AUCTION_SORT_MODE.length;
   }

   String label() {
      return this.text2;
   }

   static int findButton(GenericContainerScreenHandler handler) {
      int var1 = handler.getRows() * 9;

      for (int var2 = 0; var2 < var1; var2++) {
         ItemStack var3 = handler.getSlot(var2).getStack();
         if (!var3.isEmpty()) {
            String var4 = Registries.ITEM.getId(var3.getItem()).toString();
            if (var4.equals("minecraft:hopper") && checkCondition(var3)) {
               return var2;
            }
         }
      }

      for (int var5 = 0; var5 < var1; var5++) {
         ItemStack var6 = handler.getSlot(var5).getStack();
         if (!var6.isEmpty() && checkCondition(var6)) {
            return var5;
         }
      }

      return -1;
   }

   static AuctionSortMode current(GenericContainerScreenHandler handler) {
      int var1 = findButton(handler);
      return var1 < 0 ? null : current(handler.getSlot(var1).getStack());
   }

   static AuctionSortMode current(ItemStack stack) {
      ArrayList<AuctionSortMode.ModeLine> var1 = new ArrayList<>();
      LoreComponent var2 = stack.get(DataComponentTypes.LORE);
      if (var2 == null) {
         return null;
      }

      for (Text var4 : var2.lines()) {
         String var5 = var4.getString();
         AuctionSortMode var6 = computeAuctionSortMode(var5);
         if (var6 != null) {
            var1.add(new AuctionSortMode.ModeLine(var6, var5, computechar(var5)));
         }
      }

      if (var1.isEmpty()) {
         return null;
      }

      HashMap<Character, Integer> var10 = new HashMap<>();

      for (AuctionSortMode.ModeLine var13 : var1) {
         if (var13.TEXT3 != 0) {
            var10.merge(var13.TEXT3, 1, Integer::sum);
         }
      }

      AuctionSortMode var12 = null;
      int var14 = Integer.MIN_VALUE;
      boolean var15 = false;

      for (AuctionSortMode.ModeLine var8 : var1) {
         int var9 = resolveInt(var8, var10, var1.size());
         if (var9 > var14) {
            var12 = var8.TEXT;
            var14 = var9;
            var15 = false;
         } else if (var9 == var14) {
            var15 = true;
         }
      }

      return !var15 && var14 > 0 ? var12 : null;
   }

   private static boolean checkCondition(ItemStack stack) {
      if (TextNormalizer.keywordIn(stack.getName().getString(), "сортировка")) {
         return true;
      }

      LoreComponent var1 = stack.get(DataComponentTypes.LORE);
      if (var1 == null) {
         return false;
      }

      for (Text var3 : var1.lines()) {
         if (TextNormalizer.keywordIn(var3.getString(), "сортировка")) {
            return true;
         }
      }

      return false;
   }

   private static AuctionSortMode computeAuctionSortMode(String raw) {
      String var1 = TextNormalizer.normalize(raw);
      if (var1.isEmpty()) {
         return null;
      }

      for (AuctionSortMode var5 : AUCTION_SORT_MODE2) {
         if (var1.contains(var5.text3)) {
            return var5;
         }
      }

      return null;
   }

   private static char computechar(String raw) {
      String var1 = raw == null ? "" : raw.trim();
      if (var1.isEmpty()) {
         return '\u0000';
      }

      char var2 = var1.charAt(0);
      return Character.isLetterOrDigit(var2) ? '\u0000' : var2;
   }

   private static int resolveInt(AuctionSortMode.ModeLine line, Map<Character, Integer> markerCounts, int totalLines) {
      byte var3 = 1;
      if (line.TEXT3 != 0) {
         int var4 = markerCounts.getOrDefault(line.TEXT3, 0);
         if (var4 == 1 && totalLines > 1 && markerCounts.size() > 1) {
            var3 += 20;
         }

         if ("✔✓☑➜▶►»›>*".indexOf(line.TEXT3) >= 0) {
            var3 += 20;
         }

         if ("✖✕✗✘×-".indexOf(line.TEXT3) >= 0) {
            var3 -= 20;
         }
      }

      return var3;
   }

   private static final class ModeLine {
      private final AuctionSortMode TEXT;
      private final String TEXT2;
      private final char TEXT3;

      private ModeLine(AuctionSortMode mode, String raw, char marker) {
         this.TEXT = mode;
         this.TEXT2 = raw;
         this.TEXT3 = marker;
      }
   }
}
