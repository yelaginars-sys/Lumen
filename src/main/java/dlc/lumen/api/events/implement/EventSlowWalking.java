package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;

public class EventSlowWalking extends Event {
   private boolean cancelled;

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
