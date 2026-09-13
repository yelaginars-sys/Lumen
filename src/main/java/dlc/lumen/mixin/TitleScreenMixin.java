package dlc.lumen.mixin;

import dlc.lumen.client.ui.mainmenu.LumenMenuScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
   @Inject(method = "init", at = @At("HEAD"), cancellable = true)
   private void lumen$replaceWithCustomMenu(CallbackInfo ci) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof LumenMenuScreen)) {
         client.setScreen(new LumenMenuScreen());
      }

      ci.cancel();
   }
}
