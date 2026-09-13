package dlc.lumen.api.events;

import java.lang.reflect.InvocationTargetException;
import lombok.Generated;

public class Event {
   private boolean cancelled;

   public void cancel() {
      this.cancelled = true;
   }

   public void call() {
      try {
         EventInvoker.invoke(this);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var2) {
         throw new RuntimeException("Failed to Invoke Method", var2);
      }
   }

   @Generated
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   @Generated
   public boolean isCancelled() {
      return this.cancelled;
   }
}
