package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.render.Ambience;
import dlc.lumen.client.modules.impl.render.Removals;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
   @Inject(
      method = "getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;",
      at = @At("HEAD"),
      cancellable = true
   )
   private static void lumen$getFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> cir) {
      if (isNegativeEffectsFogRemovalEnabled()) {
         cir.setReturnValue(null);
      }
   }

   @Inject(
      method = "getFogColor(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)Lorg/joml/Vector4f;",
      at = @At("RETURN"),
      cancellable = true
   )
   private static void lumen$getFogColor(
      Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir
   ) {
      Ambience ambience = getAmbienceSafely();
      if (ambience != null && ambience.shouldApply(camera)) {
         cir.setReturnValue(ambience.getFogColor());
      }
   }

   @Inject(
      method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;Lorg/joml/Vector4f;FZF)Lnet/minecraft/client/render/Fog;",
      at = @At("RETURN"),
      cancellable = true
   )
   private static void lumen$applyFog(
      Camera camera, FogType fogType, Vector4f color, float viewDistance, boolean thickFog, float tickProgress, CallbackInfoReturnable<Fog> cir
   ) {
      Ambience ambience = getAmbienceSafely();
      if (ambience != null && ambience.shouldApply(camera)) {
         Fog fog = (Fog)cir.getReturnValue();
         Fog custom = ambience.applyTo(fog);
         if (custom != null) {
            cir.setReturnValue(custom);
         }
      }
   }

   @Inject(
      method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;Lorg/joml/Vector4f;FZF)Lnet/minecraft/client/render/Fog;",
      at = @At("RETURN"),
      cancellable = true
   )
   private static void lumen$noFog(
      Camera camera, FogType fogType, Vector4f color, float viewDistance, boolean thickFog, float tickProgress, CallbackInfoReturnable<Fog> cir
   ) {
      if (ModuleClass.INSTANCE != null) {
         Fog orig = (Fog)cir.getReturnValue();
         if (orig != null) {
            cir.setReturnValue(new Fog(1.0E7F, 1.0E7F, orig.shape(), orig.red(), orig.green(), orig.blue(), orig.alpha()));
         }
      }
   }

   private static boolean isNegativeEffectsFogRemovalEnabled() {
      try {
         if (ModuleClass.INSTANCE == null) {
            return false;
         }

         Removals removals = ModuleClass.removals;
         return removals != null && removals.isNegativeEffectsDisabled();
      } catch (Throwable ignored) {
         return false;
      }
   }

   private static Ambience getAmbienceSafely() {
      try {
         return ModuleClass.INSTANCE == null ? null : ModuleClass.ambience;
      } catch (Throwable ignored) {
         return null;
      }
   }
}
