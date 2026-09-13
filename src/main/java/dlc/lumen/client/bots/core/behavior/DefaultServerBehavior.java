package dlc.lumen.client.bots.core.behavior;

import dlc.lumen.client.bots.core.BotSession;

public class DefaultServerBehavior implements ServerBehavior {
   private final ConnectIdentity offlineConnectIdentity = new OfflineConnectIdentity();
   private final ResourcePackPolicy instantResourcePackPolicy = new InstantResourcePackPolicy();
   private final TransferPolicy rejectTransferPolicy = new RejectTransferPolicy();

   @Override
   public String id() {
      return "default";
   }

   @Override
   public boolean matches(String host) {
      return true;
   }

   @Override
   public CaptchaStrategy createCaptcha(BotSession session) {
      return CaptchaStrategy.NONE;
   }

   @Override
   public ResourcePackPolicy resourcePack() {
      return this.instantResourcePackPolicy;
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
