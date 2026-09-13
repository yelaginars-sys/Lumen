package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.render.Ambience;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
   @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
   private void lumen$getTimeOfDay(CallbackInfoReturnable<Long> cir) {
      if (ModuleClass.INSTANCE != null) {
         Ambience ambience = ModuleClass.ambience;
         if (ambience != null && ambience.isTimeLocked()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && (Object)this == mc.world) {
               cir.setReturnValue(ambience.getLockedTime());
            }
         }
      }
   }
}
