package dlc.lumen.client.bots.core.behavior;

import dlc.lumen.client.bots.core.BotSession;
import java.util.Locale;

public class FuntimeServerBehavior implements ServerBehavior {
   private final ConnectIdentity offlineConnectIdentity = new OfflineConnectIdentity();
   private final ResourcePackPolicy antibotResourcePackPolicy = new AntibotResourcePackPolicy();
   private final TransferPolicy rejectTransferPolicy = new RejectTransferPolicy();

   @Override
   public String id() {
      return "funtime";
   }

   @Override
   public boolean matches(String host) {
      if (host == null) {
         return false;
      }

      String var2 = host.toLowerCase(Locale.ROOT);
      return var2.contains("funtime") || var2.contains("skytime") || var2.contains("space-times") || var2.contains("funsky") || var2.contains("holyworld");
   }

   @Override
   public CaptchaStrategy createCaptcha(BotSession session) {
      return new BotCaptchaWatch(session);
   }

   @Override
   public ResourcePackPolicy resourcePack() {
      return this.antibotResourcePackPolicy;
   }

   @Override
   public ConnectIdentity identity() {
      return this.offlineConnectIdentity;
   }

   @Override
   public TransferPolicy transfer() {
      return this.rejectTransferPolicy;
   }
}
