package dlc.lumen.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.combat.Hitboxes;
import dlc.lumen.client.modules.impl.misc.ServerHelper;
import dlc.lumen.client.modules.impl.player.NoPush;
import dlc.lumen.client.modules.impl.render.SeeInvisibles;
import dlc.lumen.client.modules.impl.render.ShaderEsp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements QClient {
   @ModifyExpressionValue(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isControlledByPlayer()Z"))
   private boolean fixFallDistanceCalculation(boolean original) {
      return (Object)this == mc.player ? false : original;
   }

   @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
   public void pushAwayFrom(CallbackInfo ci) {
      if ((Object)this == mc.player && ModuleClass.INSTANCE != null) {
         NoPush noPush = ModuleClass.noPush;
         if (noPush != null && noPush.isEnable() && noPush.getCollisionList().is("Игроки")) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "isPushedByFluids", at = @At("RETURN"), cancellable = true)
   public void isPushedByFluids(CallbackInfoReturnable<Boolean> ci) {
      if ((Object)this == mc.player && ModuleClass.INSTANCE != null) {
         NoPush noPush = ModuleClass.noPush;
         if (noPush != null && noPush.isEnable() && noPush.getCollisionList().is("Вода")) {
            ci.setReturnValue(false);
         }
      }
   }

   @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
   private void lumen$getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
      if (ModuleClass.INSTANCE != null) {
         if (ShaderEsp.INSTANCE != null && ShaderEsp.INSTANCE.isEnable() && ShaderEsp.isEspTarget((Entity)(Object)this)) {
            cir.setReturnValue(ShaderEsp.INSTANCE.resolveMaskColor((Entity)(Object)this));
         }

         if ((Object)this instanceof PlayerEntity && ServerHelper.INSTANCE != null && ServerHelper.INSTANCE.isSnowGlowing((Entity)(Object)this)) {
            cir.setReturnValue(ServerHelper.INSTANCE.snowGlowColor());
         }
      }
   }

   @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
   private void lumen$snowGlow(CallbackInfoReturnable<Boolean> cir) {
      if (ModuleClass.INSTANCE != null) {
         if (ShaderEsp.INSTANCE != null && ShaderEsp.INSTANCE.isEnable() && ShaderEsp.isEspTarget((Entity)(Object)this)) {
            cir.setReturnValue(true);
         }

         if ((Object)this instanceof PlayerEntity && ServerHelper.INSTANCE != null && ServerHelper.INSTANCE.isSnowGlowing((Entity)(Object)this)) {
            cir.setReturnValue(true);
         }
      }
   }

   @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
   private void lumen$allowSeeInvisibles(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
      if ((Object)this instanceof PlayerEntity target && ModuleClass.INSTANCE != null) {
         SeeInvisibles seeInvisibles = ModuleClass.seeInvisibles;
         if (seeInvisibles != null && seeInvisibles.shouldRenderInvisible(target)) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
   private void lumen$hitboxes(CallbackInfoReturnable<Float> cir) {
      if (ModuleClass.INSTANCE != null) {
         if ((Object)this instanceof LivingEntity living && (Object)this != mc.player) {
            Hitboxes hitboxes = ModuleClass.hitboxes;
            if (hitboxes != null && hitboxes.isEnable() && hitboxes.shouldModifyHitbox(living)) {
               cir.setReturnValue(hitboxes.getExpand());
            }
         }
      }
   }
}
