package dlc.lumen.mixin;

import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventLook;
import dlc.lumen.api.events.implement.EventMouse;
import dlc.lumen.api.events.implement.EventScroll;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.client.modules.impl.render.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.Smoother;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
   @Shadow
   @Final
   private MinecraftClient field_1779;
   @Shadow
   private double field_1789;
   @Shadow
   private double field_1787;
   @Shadow
   private Smoother field_1793;
   @Shadow
   private Smoother field_1782;

   @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
   private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
      try {
         if (this.field_1779.player == null) {
            return;
         }

         int buttonId = button;
         int actionId = action == 1 ? 1 : 0;
         KeyBoardUtils.callMouse(buttonId, actionId);
         EventMouse event = new EventMouse(buttonId, actionId, mods);
         EventInvoker.invoke(event);
         if (event.isCancelled()) {
            ci.cancel();
         }
      } catch (Exception var10) {
      }
   }

   @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
   private void onMouseScrollHook(long window, double horizontal, double vertical, CallbackInfo ci) {
      try {
         if (vertical != 0.0) {
            KeyBoardUtils.callScroll(vertical);
            if (this.field_1779.currentScreen == null) {
               EventScroll scroll = new EventScroll(vertical);
               scroll.call();
               if (scroll.isCancelled()) {
                  ci.cancel();
               }
            }
         }
      } catch (Exception var9) {
      }
   }

   @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
   private void onUpdateMouse(double timeDelta, CallbackInfo ci) {
      try {
         if (this.field_1779.player == null) {
            return;
         }

         double sensitivity = this.field_1779.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
         double scaled = sensitivity * sensitivity * sensitivity * 8.0;
         double i;
         double j;
         if (this.field_1779.options.smoothCameraEnabled) {
            i = this.field_1793.smooth(this.field_1789 * scaled, timeDelta * scaled);
            j = this.field_1782.smooth(this.field_1787 * scaled, timeDelta * scaled);
         } else if (this.field_1779.options.getPerspective().isFirstPerson() && this.field_1779.player.isUsingSpyglass()) {
            this.field_1793.clear();
            this.field_1782.clear();
            i = this.field_1789 * sensitivity * sensitivity * sensitivity;
            j = this.field_1787 * sensitivity * sensitivity * sensitivity;
         } else {
            this.field_1793.clear();
            this.field_1782.clear();
            i = this.field_1789 * scaled;
            j = this.field_1787 * scaled;
         }

         double zoomScale = Zoom.INSTANCE.getMouseSensitivityScale();
         if (zoomScale != 1.0) {
            i *= zoomScale;
            j *= zoomScale;
         }

         int invert = this.field_1779.options.getInvertYMouse().getValue() ? -1 : 1;
         EventLook event = new EventLook(i, j * invert);
         EventInvoker.invoke(event);
         if (!event.isCancelled()) {
            this.field_1779.getTutorialManager().onUpdateMouse(event.getYaw(), event.getPitch());
            this.field_1779.player.changeLookDirection(event.getYaw(), event.getPitch());
         }

         this.field_1789 = 0.0;
         this.field_1787 = 0.0;
         ci.cancel();
      } catch (Exception var16) {
      }
   }
}
