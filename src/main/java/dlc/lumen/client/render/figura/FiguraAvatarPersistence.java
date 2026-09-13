package dlc.lumen.client.render.figura;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPost;
import dlc.lumen.client.bots.core.BotManager;
import net.minecraft.client.world.ClientWorld;

public enum FiguraAvatarPersistence implements QClient {
   INSTANCE;

   private static final int INDEX = 40;
   private ClientWorld clientWorld;
   private int index = -1;

   @EventLink
   public void onTick(EventTickPost event) {
      ClientWorld var2 = mc.world;
      if (var2 == null || mc.player == null) {
         this.clientWorld = null;
         this.index = -1;
      } else if (BotManager.INSTANCE.getControlled() != null) {
         this.clientWorld = null;
         this.index = -1;
      } else {
         if (var2 != this.clientWorld) {
            this.clientWorld = var2;
            this.index = 40;
         }

         if (this.index > 0) {
            this.index--;
            if (this.index == 0) {
               FiguraBridge.reapplySelected();
            }
         }
      }
   }
}
