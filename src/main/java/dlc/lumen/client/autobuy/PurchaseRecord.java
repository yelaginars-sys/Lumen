package dlc.lumen.client.autobuy;

public class PurchaseRecord {
   public long time;
   public String itemId;
   public String label;
   public String seller;
   public long pricePerUnit;
   public long totalPrice;
   public int count;
   public boolean success = true;
   public String failReason;

   public PurchaseRecord() {
   }

   public PurchaseRecord(long time, String itemId, String label, String seller, long pricePerUnit, long totalPrice, int count) {
      this.time = time;
      this.itemId = itemId;
      this.label = label;
      this.seller = seller;
      this.pricePerUnit = pricePerUnit;
      this.totalPrice = totalPrice;
      this.count = count;
   }
}
