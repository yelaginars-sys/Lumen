package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
   public static FullBright INSTANCE = new FullBright();

   public FullBright() {
      super("FullBright", "Всегда светло", Module.ModuleCategory.RENDER);
   }

   @EventLink
   public void onUpdate(EventUpdate ignored) {
      if (mc.player != null && mc.world != null) {
         mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 777, 1));
      }
   }

   @Override
   public void onDisable() {
      if (mc.player != null && mc.world != null) {
         mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         super.onDisable();
      }
   }
}
