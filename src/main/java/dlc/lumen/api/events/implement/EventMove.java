package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;
import net.minecraft.util.math.Vec3d;

public class EventMove extends Event {
   private Vec3d movePos;

   @Generated
   public Vec3d getMovePos() {
      return this.movePos;
   }

   @Generated
   public void setMovePos(Vec3d movePos) {
      this.movePos = movePos;
   }

   @Generated
   public EventMove() {
   }

   @Generated
   public EventMove(Vec3d movePos) {
      this.movePos = movePos;
   }
}
