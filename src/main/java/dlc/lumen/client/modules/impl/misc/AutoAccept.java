package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import java.util.Locale;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoAccept extends Module {
   public static AutoAccept INSTANCE = new AutoAccept();
   private final BooleanSetting booleanSetting = new BooleanSetting("Только друзья", true);

   public AutoAccept() {
      super("AutoAccept", "Автоматически принимает телепорт", Module.ModuleCategory.MISC);
      this.addSettings(this.booleanSetting);
   }

   @EventLink
   public void onEvent(EventPacket event) {
      if (mc.player != null && mc.world != null) {
         if (event.getType() == EventPacket.Type.RECEIVE) {
            if (event.getPacket() instanceof GameMessageS2CPacket var3) {
               String var4 = var3.content().getString().toLowerCase(Locale.ROOT);
               if (var4.contains("телепортироваться") || var4.contains("has requested teleport") || var4.contains("просит к вам телепортироваться")) {
                  if (this.booleanSetting.isState()) {
                     boolean var5 = false;
                     if (Lumen.INSTANCE.friendStorage != null) {
                        for (String var7 : Lumen.INSTANCE.friendStorage.getFriends()) {
                           if (var4.contains(var7.toLowerCase(Locale.ROOT))) {
                              var5 = true;
                              break;
                           }
                        }
                     }

                     if (!var5) {
                        return;
                     }
                  }

                  mc.player.networkHandler.sendChatCommand("tpaccept");
               }
            }
         }
      }
   }
}
