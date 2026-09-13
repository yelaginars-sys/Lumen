package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.render.Ambience;
import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Properties.class)
public class ClientWorldPropertiesMixin {
   @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
   private void lumen$getTimeOfDay(CallbackInfoReturnable<Long> cir) {
      if (lumen$locked()) {
         cir.setReturnValue(ModuleClass.ambience.getLockedTime());
      }
   }

   @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
   private void lumen$setTimeOfDay(long timeOfDay, CallbackInfo ci) {
      if (lumen$locked()) {
         ci.cancel();
      }
   }

   @Unique
   private static boolean lumen$locked() {
      if (ModuleClass.INSTANCE == null) {
         return false;
      }

      Ambience ambience = ModuleClass.ambience;
      return ambience != null && ambience.isTimeLocked();
   }
}
