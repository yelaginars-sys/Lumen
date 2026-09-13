package dlc.lumen.client.autobuy;

import dlc.lumen.api.QClient;
import net.minecraft.text.Text;

public class AutoPay implements QClient {
   private int index = 0;
   private boolean flag = false;
   private int index2 = 0;
   private boolean flag2 = false;

   public void tick() {
      if (mc.player != null) {
         AutoBuyConfig var1 = AutoBuyConfig.cfg();
         if (!var1.autoPay) {
            this.flag2 = false;
            this.flag = false;
         } else {
            if (!this.flag2) {
               this.flag2 = true;
               this.index = this.resolveInt(var1);
            }

            if (this.flag) {
               this.index2--;
               if (this.index2 <= 0) {
                  this.flag = false;
                  this.index = this.resolveInt(var1);
               }
            } else if (this.index > 0) {
               this.index--;
            } else {
               mc.player.networkHandler.sendChatCommand("money");
               this.flag = true;
               this.index2 = 100;
            }
         }
      }
   }

   public void onBalance(long balance) {
      if (this.flag) {
         this.flag = false;
         AutoBuyConfig var3 = AutoBuyConfig.cfg();
         this.index = this.resolveInt(var3);
         if (var3.autoPayNick != null && !var3.autoPayNick.isBlank()) {
            if (balance > var3.autoPayKeepBalance) {
               long var4 = balance - var3.autoPayKeepBalance;
               mc.player.networkHandler.sendChatCommand("pay " + var3.autoPayNick.trim() + " " + var4);
               if (mc.player != null) {
                  mc.player.sendMessage(Text.literal("§a[AutoPay] Переведено §f" + var4 + "§a игроку §f" + var3.autoPayNick.trim()), false);
               }
            }
         }
      }
   }

   private int resolveInt(AutoBuyConfig cfg) {
      return Math.max(60, cfg.autoPayIntervalSeconds * 20);
   }
}
