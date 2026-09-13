package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.bots.world.BotScreenBoards;
import dlc.lumen.client.modules.impl.render.AspectRatio;
import dlc.lumen.client.modules.impl.render.Removals;
import dlc.lumen.client.modules.impl.render.Zoom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
   @Inject(method = "renderWorld", at = @At("RETURN"))
   private void lumen$renderBotScreens(RenderTickCounter tickCounter, CallbackInfo ci) {
      BotScreenBoards.INSTANCE.renderRequestedFrames();
   }

   @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
   private void lumen$hideTotemAnimation(ItemStack stack, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null && stack != null && stack.isOf(Items.TOTEM_OF_UNDYING)) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isTotemAnimationDisabled()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
   private void lumen$noHurtCam(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
      Removals removals = ModuleClass.removals;
      if (removals != null && removals.isHurtCamDisabled()) {
         ci.cancel();
      }
   }

   @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
   private void lumen$zoomFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
      if (changingFov) {
         Zoom zoom = Zoom.INSTANCE;
         if (zoom != null) {
            float factor = zoom.advanceAndGetFovFactor();
            if (factor < 0.9999F) {
               cir.setReturnValue(cir.getReturnValueF() * factor);
            }
         }
      }
   }

   @ModifyArg(
      method = "getBasicProjectionMatrix",
      at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;perspective(FFFF)Lorg/joml/Matrix4f;"),
      index = 1
   )
   private float lumen$overrideAspect(float aspect) {
      AspectRatio ratio = AspectRatio.INSTANCE;
      if (ratio != null && ratio.isEnable()) {
         return ratio.getAspect();
      } else {
         return aspect;
      }
   }
}
