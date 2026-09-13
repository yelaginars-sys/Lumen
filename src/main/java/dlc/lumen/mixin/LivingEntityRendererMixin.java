package dlc.lumen.mixin;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.render.SeeInvisibles;
import dlc.lumen.client.modules.impl.render.SeeInvisiblesRenderState;
import dlc.lumen.client.modules.impl.render.ViewArmorDurability;
import dlc.lumen.client.render.models.CustomModelManager;
import dlc.lumen.client.render.models.CustomPlayerModelRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> implements QClient {
   @Inject(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void lumen$renderCustomPlayerModel(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
      PlayerEntity player = this.lumen$resolvePlayer(state);
      if (player != null) {
         if (CustomModelManager.shouldRenderCustomModel(player)) {
            CustomPlayerModelRenderer.render((LivingEntityRendererAccessor)this, player, (PlayerEntityRenderState)state, matrices, vertexConsumers, light);
            ci.cancel();
         }
      }
   }

   @Inject(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At("HEAD")
   )
   private void lumen$markArmorOwner(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
      PlayerEntity player = this.lumen$resolvePlayer(state);
      ViewArmorDurability.setRenderingSelf(player != null && player == mc.player);
   }

   @Inject(
      method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
      at = @At("TAIL")
   )
   private void lumen$updateSeeInvisiblesState(T entity, S state, float tickDelta, CallbackInfo ci) {
      boolean shouldRenderInvisible = this.lumen$shouldRenderInvisible(entity);
      ((SeeInvisiblesRenderState)state).lumen$setSeeInvisiblesTarget(shouldRenderInvisible);
      if (shouldRenderInvisible) {
         state.invisible = true;
         state.invisibleToPlayer = false;
      }
   }

   @ModifyConstant(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      constant = @Constant(intValue = 654311423)
   )
   private int lumen$changeInvisibleAlpha(int original, S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      return ((SeeInvisiblesRenderState)state).lumen$isSeeInvisiblesTarget() ? SeeInvisibles.INVISIBLE_COLOR : original;
   }

   @Unique
   private boolean lumen$shouldRenderInvisible(T entity) {
      if (entity instanceof PlayerEntity player && ModuleClass.INSTANCE != null) {
         SeeInvisibles seeInvisibles = ModuleClass.seeInvisibles;
         return seeInvisibles != null && seeInvisibles.shouldRenderInvisible(player);
      } else {
         return false;
      }
   }

   @Unique
   private PlayerEntity lumen$resolvePlayer(S state) {
      if (state instanceof PlayerEntityRenderState playerState && mc.world != null) {
         return mc.world.getEntityById(playerState.id) instanceof PlayerEntity player ? player : null;
      } else {
         return null;
      }
   }
}
