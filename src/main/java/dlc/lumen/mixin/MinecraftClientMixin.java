package dlc.lumen.mixin;

import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventGameUpdate;
import dlc.lumen.api.events.implement.EventTickPost;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.baritone.BaritoneAntiStuck;
import dlc.lumen.api.utils.player.Counter;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
   @Unique
   private long lastHookTime = Util.getMeasuringTimeNano();
   @Unique
   private int accumulatedCalls = 0;

   @Inject(method = "tick", at = @At("HEAD"))
   public void tick(CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (EventInvoker.hasListeners(EventTickPre.class)) {
         EventTickPre event = new EventTickPre();
         EventInvoker.invoke(event);
      }

      Counter.updateFPS();
   }

   @Inject(method = "tick", at = @At("RETURN"))
   public void tickEnd(CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (EventInvoker.hasListeners(EventTickPost.class)) {
         EventTickPost event = new EventTickPost();
         EventInvoker.invoke(event);
      }

      BaritoneAntiStuck.tick();
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void render(boolean tick, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (!EventInvoker.hasListeners(EventGameUpdate.class)) {
         this.lastHookTime = Util.getMeasuringTimeNano();
         this.accumulatedCalls = 0;
      } else {
         long now = Util.getMeasuringTimeNano();
         long delta = now - this.lastHookTime;
         this.accumulatedCalls += (int)(delta / 4166666L);
         this.lastHookTime = this.lastHookTime + this.accumulatedCalls * 4166666L;

         for (this.accumulatedCalls = Math.min(this.accumulatedCalls, 8); this.accumulatedCalls > 0; this.accumulatedCalls--) {
            EventInvoker.invoke(new EventGameUpdate());
         }
      }
   }

   @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
   private void lumen$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleClass.INSTANCE != null) {
         ;
      }
   }

   @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;isCursorLocked()Z"))
   private boolean lumen$autoMineAllowMiningUnfocused(Mouse mouse) {
      return AutoMine.INSTANCE != null && AutoMine.INSTANCE.isMiningActive() ? true : mouse.isCursorLocked();
   }
}
