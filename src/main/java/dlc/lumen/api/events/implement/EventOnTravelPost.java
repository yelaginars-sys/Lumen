package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import net.minecraft.util.math.Vec3d;

public class EventOnTravelPost extends Event {
   private Vec3d oldVelocity;

   public EventOnTravelPost(Vec3d oldVelocity) {
      this.oldVelocity = oldVelocity;
   }

   public Vec3d getOldVelocity() {
      return this.oldVelocity;
   }

   public void setOldVelocity(Vec3d oldVelocity) {
      this.oldVelocity = oldVelocity;
   }
}
