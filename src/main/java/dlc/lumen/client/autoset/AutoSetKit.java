package dlc.lumen.client.autoset;

import java.util.ArrayList;
import java.util.List;

public class AutoSetKit {
   public String name = "";
   public List<AutoSetKit.Entry> entries = new ArrayList<>();

   public AutoSetKit() {
   }

   public AutoSetKit(String name) {
      this.name = name;
   }

   public static String keyOf(AutoSetItem item) {
      return item == null ? "" : item.key();
   }

   public static AutoSetItem resolve(AutoSetKit.Entry entry) {
      return entry == null ? null : AutoSetCatalog.byKey(entry.key);
   }

   public AutoSetKit.Entry find(AutoSetItem item) {
      String var2 = keyOf(item);

      for (AutoSetKit.Entry var4 : this.entries) {
         if (var2.equals(var4.key)) {
            return var4;
         }
      }

      return null;
   }

   public void add(AutoSetItem item, int delta) {
      if (item != null && delta != 0) {
         AutoSetKit.Entry var3 = this.find(item);
         if (var3 != null) {
            var3.count += delta;
            if (var3.count <= 0) {
               this.entries.remove(var3);
            }
         } else if (delta > 0) {
            this.entries.add(new AutoSetKit.Entry(keyOf(item), delta));
         }
      }
   }

   public void remove(AutoSetItem item) {
      String var2 = keyOf(item);
      this.entries.removeIf(e -> var2.equals(e.key));
   }

   public int total() {
      int var1 = 0;

      for (AutoSetKit.Entry var3 : this.entries) {
         var1 += Math.max(0, var3.count);
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public static class Entry {
      public String key;
      public int count;
      public long maxUnitPrice;
      public int minDurabilityPercent;

      public Entry() {
      }

      public Entry(String key, int count) {
         this.key = key;
         this.count = count;
      }
   }
}
