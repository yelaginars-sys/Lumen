package ru.pulse.mixin;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.cosmetic.CosmeticFeatureRenderer;

@Mixin(LivingEntityRenderer.class)
public abstract class CosmeticRendererMixin {
   @Shadow
   protected abstract boolean addFeature(FeatureRenderer var1);

   @Inject(require = 0, method = "<init>", at = @At("RETURN"))
   private void onInit(Context class_5618Var, EntityModel<?> model, float shadowRadius, CallbackInfo callbackInfo) {
      if ((Object)this instanceof PlayerEntityRenderer) {
         this.addFeature(new CosmeticFeatureRenderer((FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>)this));
      }
   }
}
