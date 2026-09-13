package dlc.lumen.mixin;

import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
   @Shadow
   private int field_3716;

   @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
   public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
      try {
         if (player != null && target != null) {
            EventAttackEntity event = new EventAttackEntity(player, target);
            EventInvoker.invoke(event);
            if (event.isCancelled()) {
               ci.cancel();
            }
         }
      } catch (Exception var5) {
      }
   }

   @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
   private void lumen$blockContainerInteraction(ClientPlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
      if (ModuleClass.noInteract != null && ModuleClass.noInteract.blocksContainers() && player != null && hit != null && player.getWorld() != null) {
         if (player.getWorld().getBlockState(hit.getBlockPos()).createScreenHandlerFactory(player.getWorld(), hit.getBlockPos()) != null) {
            cir.setReturnValue(ActionResult.PASS);
         }
      }
   }

   @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
   private void lumen$armorStandInteraction(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
      if (ModuleClass.noInteract != null && ModuleClass.noInteract.blocksArmorStands() && entity instanceof ArmorStandEntity) {
         cir.setReturnValue(ActionResult.FAIL);
      }
   }

   @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
   private void lumen$armorStandInteractionAtLocation(
      PlayerEntity player, Entity entity, EntityHitResult hit, Hand hand, CallbackInfoReturnable<ActionResult> cir
   ) {
      if (ModuleClass.noInteract != null && ModuleClass.noInteract.blocksArmorStands() && entity instanceof ArmorStandEntity) {
         cir.setReturnValue(ActionResult.FAIL);
      }
   }

   @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
   private void lumen$autoMineNoBreakCooldown(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
      if (AutoMine.INSTANCE != null && AutoMine.INSTANCE.isMiningActive()) {
         this.field_3716 = 0;
      }
   }
}
