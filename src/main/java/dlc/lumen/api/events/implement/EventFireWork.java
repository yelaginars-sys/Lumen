package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;
import net.minecraft.entity.projectile.FireworkRocketEntity;

public class EventFireWork extends Event {
   private final FireworkRocketEntity firework;

   @Generated
   public FireworkRocketEntity getFirework() {
      return this.firework;
   }

   @Generated
   public EventFireWork(FireworkRocketEntity firework) {
      this.firework = firework;
   }
}
