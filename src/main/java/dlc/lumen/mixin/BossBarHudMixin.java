package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   private void lumen$renderBossBar(CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null
         && ModuleClass.interfaceModule != null
         && ModuleClass.interfaceModule.isEnable()
         && ModuleClass.interfaceModule.isHudElementVisible("dynamicIslandHUD")) {
         ci.cancel();
      }
   }
}
