package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;

public class EventScroll extends Event {
   private final double amount;

   public EventScroll(double amount) {
      this.amount = amount;
   }

   public double getAmount() {
      return this.amount;
   }
}
