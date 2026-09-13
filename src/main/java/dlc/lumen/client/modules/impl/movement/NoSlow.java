package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventSlowWalking;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.player.ViaProtocolUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class NoSlow extends Module {
   public static NoSlow INSTANCE = new NoSlow();
   private final ModeSetting modeSetting = new ModeSetting("Мод", "Grim Old", "Grim Old", "Grim Last", "MultiServer");
   private final BooleanSetting booleanSetting = new BooleanSetting("Спринт", true);
   private int useCounter;

   public NoSlow() {
      super("NoSlow", "Убирает замедление во время еды", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting, this.booleanSetting);
   }

   @EventLink
   public void onClientTick(EventTickPre event) {
      if (mc.player != null && this.modeSetting.is("MultiServer") && !mc.player.isGliding()) {
         if (mc.player.isUsingItem()) {
            this.useCounter++;
         } else {
            this.useCounter = 0;
         }
      }
   }

   @EventLink
   public void onSlowDown(EventSlowWalking event) {
      if (mc.player != null && mc.player.isUsingItem()) {
         if (this.modeSetting.is("MultiServer") && !mc.player.isGliding()) {
            if (this.useCounter == 1 || this.useCounter == 2) {
               event.setCancelled(true);
            }

            if (this.useCounter >= 2) {
               this.useCounter = 0;
               if (this.booleanSetting.isState()) {
                  mc.player.setSprinting(
                     (ModuleClass.sprint.isEnable() && Sprint.isSprinting() || mc.options.sprintKey.isPressed())
                        && mc.player.input.movementForward > 0.0F
                        && !mc.player.isGliding()
                  );
               }
            }
         } else if (this.modeSetting.is("Grim Last") && mc.player.getItemUseTime() % 2 == 0) {
            event.setCancelled(true);
         } else if (this.modeSetting.is("Grim Old")) {
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
