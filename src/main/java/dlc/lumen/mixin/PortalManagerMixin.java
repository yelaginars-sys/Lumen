package dlc.lumen.mixin;

import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventPortal;
import net.minecraft.world.dimension.PortalManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalManager.class)
public abstract class PortalManagerMixin {
   @Inject(method = "isInPortal", at = @At("RETURN"), cancellable = true)
   private void onIsInPortal(CallbackInfoReturnable<Boolean> cir) {
      if (EventInvoker.hasListeners(EventPortal.class)) {
         EventPortal event = new EventPortal((Boolean)cir.getReturnValue());
         event.call();
         cir.setReturnValue(event.isInPortal());
      }
   }
}
