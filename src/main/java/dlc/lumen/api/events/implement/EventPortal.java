package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;

public class EventPortal extends Event {
   private boolean inPortal;

   @Generated
   public boolean isInPortal() {
      return this.inPortal;
   }

   @Generated
   public void setInPortal(boolean inPortal) {
      this.inPortal = inPortal;
   }

   @Generated
   public EventPortal(boolean inPortal) {
      this.inPortal = inPortal;
   }
}
