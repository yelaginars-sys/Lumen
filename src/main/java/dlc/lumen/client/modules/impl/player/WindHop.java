package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;

public class WindHop extends Module {
   public static WindHop INSTANCE = new WindHop();
   private final BooleanSetting booleanSetting = new BooleanSetting("Поворачивать голову вниз", true);
   private int index = -1;
   private float volume;
   private float volume2;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private Hand hand;

   public WindHop() {
      super("WindHop", "Автоматически прыгает после использования заряда ветра", Module.ModuleCategory.PLAYER);
      this.addSettings(this.booleanSetting);
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.SEND) {
         if (event.getPacket() instanceof PlayerInteractItemC2SPacket var2) {
            if (mc.player != null) {
               if (mc.player.getStackInHand(var2.getHand()).isOf(Items.WIND_CHARGE)) {
                  this.index = 2;
                  if (this.booleanSetting.getValue() && !this.flag3) {
                     this.hand = var2.getHand();
                     this.flag2 = true;
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onTick(EventUpdate event) {
      if (mc.player != null && RotationStorage.instance != null) {
         if (this.flag2 && this.hand != null) {
            this.flag2 = false;
            this.flag3 = true;
            mc.player.networkHandler.sendPacket(new LookAndOnGround(mc.player.getYaw(), 90.0F, mc.player.isOnGround(), mc.player.horizontalCollision));
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(this.hand, 0, mc.player.getYaw(), 90.0F));
            this.flag3 = false;
         }

         if (this.index > 0) {
            this.index--;
         }

         boolean var2 = mc.player.getMainHandStack().isOf(Items.WIND_CHARGE);
         if (this.booleanSetting.getValue() && mc.options.useKey.isPressed() && var2) {
            if (!this.flag) {
               this.volume = mc.player.getYaw();
               this.volume2 = mc.player.getPitch();
               this.flag = true;
            }

            RotationStorage.update(new Rotation(mc.player.getYaw(), 90.0F), 360.0F, 360.0F, 360.0F, 360.0F, 2, 6, false);
         } else if (this.flag) {
            this.flag = false;
            RotationStorage.instance.stopRotation();
            mc.player.setYaw(this.volume);
            mc.player.setPitch(this.volume2);
            this.volume = 0.0F;
            this.volume2 = 0.0F;
         }
      }
   }

   @Override
   public void onDisable() {
      if (mc.player != null && RotationStorage.instance != null && this.flag) {
         RotationStorage.instance.stopRotation();
         mc.player.setYaw(this.volume);
         mc.player.setPitch(this.volume2);
      }

      this.flag = false;
      this.index = -1;
      this.flag2 = false;
      this.flag3 = false;
      this.hand = null;
      super.onDisable();
   }

   @EventLink
   public void onInput(EventMoveInput event) {
      if (this.index == 0) {
         event.setJump(true);
         this.index = -1;
      }
   }
}
