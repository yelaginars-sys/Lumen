package dlc.lumen.mixin;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.implement.EventChunkReload;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public class KeyboardMixin implements QClient {
   @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
   public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
      if (mc.currentScreen == null) {
         KeyBoardUtils.call(key, action);
         if (key == 344 && action == 1 && mc.currentScreen != null) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
   private void lumen$widgetChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
      if (mc.currentScreen instanceof ChatScreen) {
         if (ModuleClass.interfaceModule != null) {
            if (Character.isBmpCodePoint(codePoint) && ModuleClass.interfaceModule.handleWidgetChar((char)codePoint)) {
               ci.cancel();
            }
         }
      }
   }

   @Inject(method = "processF3", at = @At("RETURN"))
   private void processF3(int key, CallbackInfoReturnable<Boolean> cir) {
      if (key == 65 && (Boolean)cir.getReturnValue()) {
         new EventChunkReload().call();
      }
   }
}
