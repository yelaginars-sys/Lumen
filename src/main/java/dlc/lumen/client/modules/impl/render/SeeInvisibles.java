package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class SeeInvisibles extends Module {
   public static final float INVISIBLE_ALPHA = 0.7F;
   public static final int INVISIBLE_COLOR = Math.round(178.5F) << 24 | 16777215;
   public static SeeInvisibles INSTANCE = new SeeInvisibles();

   public SeeInvisibles() {
      super("SeeInvisibles", "Показывает невидимых игроков", Module.ModuleCategory.RENDER);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         for (PlayerEntity var3 : mc.world.getPlayers()) {
            if (this.shouldRenderInvisible(var3)) {
               var3.setInvisible(false);
            }
         }
      }
   }

   public boolean shouldRenderInvisible(PlayerEntity player) {
      return this.isEnable()
         && mc.player != null
         && player != null
         && player != mc.player
         && (player.isInvisible() || player.hasStatusEffect(StatusEffects.INVISIBILITY));
   }
}
