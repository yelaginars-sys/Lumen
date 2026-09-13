package dlc.lumen.api.events.implement;

import dlc.lumen.api.events.Event;
import net.minecraft.client.gui.DrawContext;

public class EventRender2D extends Event {
   private final DrawContext drawContext;
   private final float partialTicks;

   public EventRender2D(DrawContext drawContext, float partialTicks) {
      this.drawContext = drawContext;
      this.partialTicks = partialTicks;
   }

   public DrawContext getDrawContext() {
      return this.drawContext;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
