package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;
import net.minecraft.util.math.BlockPos;

public class EventBlockCollide extends Event {
   private final BlockPos pos;

   public EventBlockCollide(BlockPos pos) {
      this.pos = pos;
   }

   @Generated
   public BlockPos getPos() {
      return this.pos;
   }
}
