package dlc.lumen.client.bots.core.behavior;

import dlc.lumen.client.bots.core.BotSession;

public interface ServerBehavior {
   String id();

   boolean matches(String var1);

   CaptchaStrategy createCaptcha(BotSession var1);

   ResourcePackPolicy resourcePack();

   ConnectIdentity identity();

   TransferPolicy transfer();
}
