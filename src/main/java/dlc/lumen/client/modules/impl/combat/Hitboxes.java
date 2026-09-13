package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.Lumen;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class Hitboxes extends Module {
   public static Hitboxes INSTANCE = new Hitboxes();
   private final FloatSetting floatSetting = new FloatSetting("Размер", 0.3F, 0.0F, 1.0F, 0.1F);
   private final ListSetting value = new ListSetting(
      "Цели",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Животные", true),
      new BooleanSetting("Мобы", true),
      new BooleanSetting("Невидимки", true),
      new BooleanSetting("Голые игроки", true),
      new BooleanSetting("Друзья", false)
   );

   public Hitboxes() {
      super("Hitboxes", "Увеличение хитбоксов сущностей для облегчения попаданий", Module.ModuleCategory.COMBAT);
      this.addSettings(this.floatSetting, this.value);
   }

   public boolean shouldModifyHitbox(LivingEntity entity) {
      if (entity == null || entity == mc.player || !entity.isAlive()) {
         return false;
      }

      if (entity.isInvisible() && !this.value.is("Невидимки")) {
         return false;
      }

      if (entity instanceof PlayerEntity var2) {
         if (Lumen.INSTANCE.friendStorage.isFriend(var2.getName().getString()) && !this.value.is("Друзья")) {
            return false;
         } else {
            return this.checkCondition(var2) ? this.value.is("Голые игроки") : this.value.is("Игроки");
         }
      } else if (entity instanceof AnimalEntity) {
         return this.value.is("Животные");
      } else {
         return entity instanceof MobEntity ? this.value.is("Мобы") : false;
      }
   }

   public float getExpand() {
      return this.floatSetting.get();
   }

   private boolean checkCondition(PlayerEntity player) {
      return player.getInventory().armor.stream().allMatch(stack -> stack.isEmpty() || stack.isOf(Items.ELYTRA));
   }
}
