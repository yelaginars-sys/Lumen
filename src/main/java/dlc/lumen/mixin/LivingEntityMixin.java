package dlc.lumen.mixin;

import dlc.lumen.api.events.implement.EventOnTravelPost;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.render.SwingAnimations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
   @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
   private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
      if ((Object)this == MinecraftClient.getInstance().player) {
         if (ModuleClass.INSTANCE != null) {
            SwingAnimations tweaks = ModuleClass.swingAnimations;
            if (tweaks != null && tweaks.isEnable() && tweaks.smoothEnabled.isState()) {
               cir.setReturnValue((int)tweaks.slowAnimationSpeed.get());
            }
         }
      }
   }

   @Inject(method = "travel", at = @At("RETURN"))
   private void onTravelPost(Vec3d movementInput, CallbackInfo ci) {
      if ((Object)this == MinecraftClient.getInstance().player) {
         LivingEntity entity = (LivingEntity)(Object)this;
         EventOnTravelPost event = new EventOnTravelPost(entity.getVelocity());
         event.call();
         if (!event.isCancelled()) {
            entity.setVelocity(event.getOldVelocity());
         }
      }
   }
}
