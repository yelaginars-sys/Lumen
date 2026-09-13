package dlc.lumen.mixin;

import dlc.lumen.client.modules.impl.misc.Proxy;
import dlc.lumen.client.ui.proxy.ProxyConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
   @Inject(method = "init", at = @At("TAIL"))
   private void lumen$addProxyButton(CallbackInfo ci) {
      Screen self = (Screen)(Object)this;
      ButtonWidget button = ButtonWidget.builder(
            Text.literal(Proxy.INSTANCE.buttonLabel()), b -> MinecraftClient.getInstance().setScreen(new ProxyConfigScreen(self))
         )
         .dimensions(5, 5, 120, 20)
         .build();
      ((ScreenInvoker)this).lumen$addDrawableChild(button);
   }
}
