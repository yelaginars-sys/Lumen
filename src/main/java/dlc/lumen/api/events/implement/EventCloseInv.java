package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;

public class EventCloseInv extends Event {
   public int windowId;

   @Generated
   public EventCloseInv(int windowId) {
      this.windowId = windowId;
   }
}
