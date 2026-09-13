package dlc.lumen.client.autoset;

import dlc.lumen.client.autobuy.ItemEntry;

public class AutoSetItem {
   public final ItemEntry entry;
   public final String group;
   private final String text;
   private final String text2;
   public final boolean wholeStack;

   public AutoSetItem(ItemEntry entry, String group, String display, String search, boolean wholeStack) {
      this.entry = entry;
      this.group = group;
      this.text = display;
      this.text2 = search;
      this.wholeStack = wholeStack;
   }

   public String id() {
      return this.entry.itemId;
   }

   public String name() {
      return this.text != null ? this.text : this.entry.itemId;
   }

   public String alias() {
      return this.text2;
   }

   public String searchName() {
      return this.text2 != null && !this.text2.isBlank() ? this.text2 : this.name();
   }

   public String key() {
      return this.group + "|" + this.name();
   }
}
