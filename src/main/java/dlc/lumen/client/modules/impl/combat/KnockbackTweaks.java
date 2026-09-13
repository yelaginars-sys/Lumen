package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;

public class KnockbackTweaks extends Module {
   public static KnockbackTweaks INSTANCE = new KnockbackTweaks();
   private final BooleanSetting booleanSetting = new BooleanSetting("Фейк спринт", false);
   private boolean flag;
   private boolean flag2;
   private Entity entity2;
   private int index;
   private boolean flag3;

   public KnockbackTweaks() {
      super("KnockbackTweaks", "Подкручивает спринт и поворот во время удара", Module.ModuleCategory.COMBAT);
      this.addSettings(this.booleanSetting);
   }

   @Override
   public void onDisable() {
      this.updateState2();
      super.onDisable();
   }

   @EventLink
   public void onAttack(EventAttackEntity event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (event.getPlayer() == mc.player && event.getTarget() != null && !this.flag3) {
            if (Aura.INSTANCE == null || !Aura.INSTANCE.isHolyWorldAttackInProgress()) {
               if (this.booleanSetting.isState()) {
                  this.updateState3(event.getTarget());
                  this.flag2 = true;
               } else if (this.checkState()) {
                  event.cancel();
                  this.updateState4(event.getTarget());
               }
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      this.updateState();
   }

   private void updateState() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (Aura.INSTANCE != null && Aura.INSTANCE.isHolyWorldActive()) {
            this.entity2 = null;
            this.index = 0;
            this.flag = false;
            this.flag2 = false;
         }

         if (!this.booleanSetting.isState()) {
            this.updateState6();
            this.updateState5();
         }

         if (this.flag) {
            if (mc.player.networkHandler == null) {
               this.flag = false;
               return;
            }

            this.flag = false;
            mc.player
               .networkHandler
               .sendPacket(new LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround(), mc.player.horizontalCollision));
         }

         if (this.flag2) {
            if (mc.player.networkHandler == null) {
               this.flag2 = false;
               return;
            }

            this.flag2 = false;
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));
         }
      }
   }

   private void updateState2() {
      this.flag = false;
      this.flag2 = false;
      this.entity2 = null;
      this.index = 0;
      this.flag3 = false;
   }

   private void updateState3(Entity entity) {
      if (mc.player != null && mc.player.networkHandler != null) {
         double var2 = entity.getX() - mc.player.getX();
         double var4 = entity.getZ() - mc.player.getZ();
         float var6 = (float)(Math.toDegrees(Math.atan2(var4, var2)) - 90.0);
         mc.player.networkHandler.sendPacket(new LookAndOnGround(var6, mc.player.getPitch(), mc.player.isOnGround(), mc.player.horizontalCollision));
         this.flag = true;
      }
   }

   private void updateState4(Entity entity) {
      this.entity2 = entity;
      this.index = 1;
   }

   private void updateState5() {
      if (this.entity2 != null && mc.player != null) {
         if (this.index > 0) {
            this.index--;
         } else if (!this.entity2.isAlive()) {
            this.entity2 = null;
         } else if (mc.player.networkHandler == null) {
            this.entity2 = null;
         } else {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));
            this.flag3 = true;

            try {
               mc.interactionManager.attackEntity(mc.player, this.entity2);
               mc.player.swingHand(Hand.MAIN_HAND);
            } finally {
               this.flag3 = false;
               this.entity2 = null;
            }
         }
      }
   }

   private void updateState6() {
      if (this.checkState()) {
         if (mc.options != null) {
            mc.options.sprintKey.setPressed(true);
         }

         mc.player.setSprinting(true);
      }
   }

   private boolean checkState() {
      return mc.player != null
         && !mc.player.isSneaking()
         && !mc.player.isUsingItem()
         && !mc.player.isClimbing()
         && !mc.player.isTouchingWater()
         && !mc.player.isSubmergedInWater()
         && (mc.player.getHungerManager().getFoodLevel() > 6 || mc.player.getAbilities().allowFlying);
   }
}
