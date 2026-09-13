package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import lombok.Generated;

public class EventBinding extends Event {
   private final int keyDown;
   private final EventBinding.BindType bindType;

   public boolean isKeyDown(int button) {
      return this.keyDown == button;
   }

   @Generated
   public EventBinding(int key, EventBinding.BindType bindType) {
      this.keyDown = key;
      this.bindType = bindType;
   }

   @Generated
   public int getKey() {
      return this.keyDown;
   }

   @Generated
   public EventBinding.BindType getBindType() {
      return this.bindType;
   }

   public enum BindType {
      KEYBOARD,
      MOUSE;
   }
}
