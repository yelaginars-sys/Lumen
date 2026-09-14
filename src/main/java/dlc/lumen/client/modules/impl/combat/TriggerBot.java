package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.combat.IdealHitUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import lombok.Generated;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class TriggerBot extends Module {
   public static TriggerBot INSTANCE = new TriggerBot();
    private final BooleanSetting booleanSetting = new BooleanSetting("Только криты", true);
    private final BooleanSetting sprintReset = new BooleanSetting("Сброс спринта", true);
    private final FloatSetting minDelay = new FloatSetting("Мин. задержка", 25.0F, 0.0F, 500.0F, 5.0F);
    private final FloatSetting maxDelay = new FloatSetting("Макс. задержка", 90.0F, 0.0F, 500.0F, 5.0F);
   private final ListSetting value = new ListSetting(
      "Цели",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Животные", true),
      new BooleanSetting("Мобы", true),
      new BooleanSetting("Невидимки", true),
      new BooleanSetting("Голые игроки", true),
      new BooleanSetting("Друзья", false)
   );
    private LivingEntity target;
    private final Random random = new Random();
    private long nextAttackMs;

    public TriggerBot() {
       super("TriggerBot", "Автоматически атакует при наведении на цель", Module.ModuleCategory.COMBAT);
       this.addSettings(this.booleanSetting, this.sprintReset, this.minDelay, this.maxDelay, this.value);
    }

    @Override
    public void onEnable() {
       this.target = null;
       this.nextAttackMs = 0L;
       super.onEnable();
    }

    @Override
    public void onDisable() {
       this.target = null;
       this.nextAttackMs = 0L;
       super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
       if (mc.player != null && mc.world != null && mc.interactionManager != null) {
          if (mc.targetedEntity instanceof LivingEntity var2 && this.checkCondition2(var2)) {
             this.target = var2;
             if (this.checkCondition(var2)) {
                long now = System.currentTimeMillis();
                if (this.nextAttackMs == 0L) {
                   this.nextAttackMs = now + this.randomDelay();
                }

                if (now >= this.nextAttackMs) {
                   if (this.sprintReset.isState() && mc.player.isSprinting()) {
                      mc.player.setSprinting(false);
                   }

                   mc.interactionManager.attackEntity(mc.player, var2);
                   mc.player.swingHand(Hand.MAIN_HAND);
                   this.nextAttackMs = System.currentTimeMillis() + this.randomDelay();
                }
             }
          } else {
             this.target = null;
             this.nextAttackMs = 0L;
          }
       } else {
          this.target = null;
          this.nextAttackMs = 0L;
       }
    }

    private long randomDelay() {
       float min = Math.min(this.minDelay.get(), this.maxDelay.get());
       float max = Math.max(this.minDelay.get(), this.maxDelay.get());
       return (long)(min + this.random.nextFloat() * (max - min));
    }

   private boolean checkCondition(LivingEntity entity) {
      if (mc.player == null) {
         return false;
      } else {
         return mc.player.getAttackCooldownProgress(0.5F) <= 0.93F ? false : !this.booleanSetting.isState() || IdealHitUtils.canCritical(entity);
      }
   }

   private boolean checkCondition2(LivingEntity entity) {
      if (entity == null || entity == mc.player || !entity.isAlive() || entity.getHealth() <= 0.0F) {
         return false;
      }

      if (entity instanceof ArmorStandEntity) {
         return false;
      }

      if (entity.distanceTo(mc.player) > 3.0F) {
         return false;
      }

      if (entity.isInvisible() && !this.value.is("Невидимки")) {
         return false;
      }

      if (entity instanceof PlayerEntity var2) {
         if (Lumen.INSTANCE.friendStorage.isFriend(var2.getName().getString()) && !this.value.is("Друзья")) {
            return false;
         } else {
            return this.checkCondition3(var2) ? this.value.is("Голые игроки") : this.value.is("Игроки");
         }
      } else if (entity instanceof PassiveEntity || entity instanceof CodEntity) {
         return this.value.is("Животные");
      } else {
         return entity instanceof HostileEntity ? this.value.is("Мобы") : false;
      }
   }

   private boolean checkCondition3(PlayerEntity player) {
      return player.getInventory().armor.stream().allMatch(stack -> stack.isEmpty() || stack.isOf(Items.ELYTRA));
   }

   @Generated
   public LivingEntity getTarget() {
      return this.target;
   }
}
