package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;

public class EventMouse extends Event {
   private final int button;
   private final int action;
   private final int mods;
   private boolean cancelled = false;

   public EventMouse(int button, int action, int mods) {
      this.button = button;
      this.action = action;
      this.mods = mods;
   }

   public int getButton() {
      return this.button;
   }

   public int getAction() {
      return this.action;
   }

   public int getMods() {
      return this.mods;
   }

   public boolean isPressed() {
      return this.action == 1;
   }

   public boolean isReleased() {
      return this.action == 0;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
