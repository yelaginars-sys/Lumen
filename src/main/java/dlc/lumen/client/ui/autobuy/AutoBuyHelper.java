package dlc.lumen.client.ui.autobuy;

import lombok.Generated;

public class AutoBuyHelper {
   private AutoBuyHelper.Group server2;

   public AutoBuyHelper(AutoBuyHelper.Group group) {
      this.server2 = group;
   }

   public enum Group {
      RW("RW"),
      HW("HW"),
      FT("FT"),
      SP("SP");

      private final String server2;

      @Generated
      Group(final String server) {
         this.server2 = server;
      }

      @Generated
      public String getServer() {
         return this.server2;
      }
   }
}
