package dlc.lumen.mixin;

import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventMoveInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   @Inject(method = "tick", at = @At("TAIL"))
   private void onTickTail(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventMoveInput.class)) {
         EventMoveInput eventInput = new EventMoveInput(
            this.movementForward, this.movementSideways, this.playerInput.jump(), this.playerInput.sneak(), this.playerInput.sprint()
         );
         eventInput.call();
         float forward = eventInput.getForward();
         float strafe = eventInput.getStrafe();
         this.playerInput = new PlayerInput(
            forward > 0.0F, forward < 0.0F, strafe > 0.0F, strafe < 0.0F, eventInput.isJump(), eventInput.isSneak(), eventInput.isSprint()
         );
         this.movementForward = forward;
         this.movementSideways = strafe;
      }
   }
}
