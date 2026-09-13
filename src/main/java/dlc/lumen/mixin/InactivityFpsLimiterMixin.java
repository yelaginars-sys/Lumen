package dlc.lumen.mixin;

import dlc.lumen.api.commands.impl.RCTCommand;
import dlc.lumen.client.modules.impl.misc.AutoCore;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import dlc.lumen.client.modules.impl.misc.AutoZamok;
import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InactivityFpsLimiter.class)
public class InactivityFpsLimiterMixin {
   @Inject(method = "update", at = @At("HEAD"), cancellable = true)
   private void lumen$noThrottleWhileFarming(CallbackInfoReturnable<Integer> cir) {
      boolean farming = AutoMine.INSTANCE != null && AutoMine.INSTANCE.isEnable()
         || AutoCore.INSTANCE != null && AutoCore.INSTANCE.isEnable()
         || AutoZamok.INSTANCE != null && AutoZamok.INSTANCE.isEnable();
      if (farming || RCTCommand.RUNNING) {
         cir.setReturnValue(60);
      }
   }
}
