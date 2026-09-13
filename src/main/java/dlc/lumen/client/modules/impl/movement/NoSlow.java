package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventSlowWalking;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.player.ViaProtocolUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class NoSlow extends Module {
   public static NoSlow INSTANCE = new NoSlow();
   private final ModeSetting modeSetting = new ModeSetting("Мод", "Grim Old", "Grim Old", "Grim Last");
   private final BooleanSetting booleanSetting = new BooleanSetting("Спринт", true);

   public NoSlow() {
      super("NoSlow", "Убирает замедление во время еды", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting, this.booleanSetting);
   }

   @EventLink
   public void onSlowDown(EventSlowWalking event) {
      if (mc.player != null && mc.player.isUsingItem()) {
         if (this.modeSetting.is("Grim Last") && mc.player.getItemUseTime() % 2 == 0) {
            event.setCancelled(true);
         }

         if (this.modeSetting.is("Grim Old")) {
            Hand var2 = mc.player.getActiveHand();
            boolean var3 = ViaProtocolUtils.isTargetProtocolBelowOneNineteen();
            if (this.booleanSetting.isState()) {
               mc.player
                  .setSprinting(
                     (ModuleClass.sprint.isEnable() && Sprint.isSprinting() || mc.options.sprintKey.isPressed())
                        && mc.player.input.movementForward > 0.0F
                        && (!var3 || !mc.player.horizontalCollision && !mc.player.collidedSoftly)
                        && !mc.player.isGliding()
                  );
            }

            Hand var4 = var2 == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(var4, 0, mc.player.getYaw(), mc.player.getPitch()));
            event.setCancelled(true);
         }
      }
   }
}
