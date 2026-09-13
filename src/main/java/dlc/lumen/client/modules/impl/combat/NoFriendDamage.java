package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.client.modules.Module;
import net.minecraft.entity.player.PlayerEntity;

public class NoFriendDamage extends Module {
   public static final NoFriendDamage INSTANCE = new NoFriendDamage();

   public NoFriendDamage() {
      super("NoFriendDamage", "Не даёт ударить друга", Module.ModuleCategory.COMBAT);
   }

   @EventLink
   public void onAttack(EventAttackEntity event) {
      if (mc.player != null) {
         if (event.getPlayer() == mc.player) {
            if (event.getTarget() instanceof PlayerEntity var2) {
               if (Lumen.INSTANCE.friendStorage != null) {
                  if (Lumen.INSTANCE.friendStorage.isFriend(var2.getName().getString())) {
                     event.cancel();
                  }
               }
            }
         }
      }
   }
}
