package dlc.lumen.mixin;

import com.mojang.authlib.GameProfile;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventCloseInv;
import dlc.lumen.api.events.implement.EventMove;
import dlc.lumen.api.events.implement.EventSlowWalking;
import dlc.lumen.api.events.implement.EventSprint;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.events.implement.EventUpdatePost;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.player.ViaProtocolUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements QClient {
   @Shadow
   @Final
   public ClientPlayNetworkHandler field_3944;

   public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
      super(world, pos, yaw, gameProfile);
   }

   @Shadow
   public abstract void method_3137();

   @Inject(method = "tick", at = @At(value = "HEAD", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"))
   private void onTick(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventUpdate.class)) {
         new EventUpdate().call();
      }
   }

   @Inject(method = "tick", at = @At(value = "TAIL", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"))
   private void onTickPost(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventUpdatePost.class)) {
         new EventUpdatePost().call();
      }

      if (this.shouldSyncRotation()) {
         this.headYaw = this.getYaw();
         this.prevHeadYaw = this.getYaw();
         this.bodyYaw = this.getYaw();
         this.prevBodyYaw = this.getYaw();
      }
   }

   @Unique
   private boolean shouldSyncRotation() {
      if (ModuleClass.aura.isEnable()) {
      }

      return false;
   }

   @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1), require = 0)
   private boolean onSprintKeyPressed(KeyBinding instance) {
      if (!ViaProtocolUtils.isTargetProtocolBelowOneNineteen() || !this.horizontalCollision && !this.collidedSoftly) {
         EventSprint event = new EventSprint();
         event.call();
         return event.isCancelled() ? false : instance.isPressed();
      } else {
         return false;
      }
   }

   @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
   private boolean onSlowDownRedirect(ClientPlayerEntity player) {
      if (player.isUsingItem()) {
         EventSlowWalking event = new EventSlowWalking();
         event.call();
         return player.isUsingItem() && player.getVehicle() == null && !event.isCancelled();
      } else {
         return player.isUsingItem() && player.getVehicle() == null;
      }
   }

   @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
   public void pushOutOfBlocks(double x, double z, CallbackInfo ci) {
      if (ModuleClass.noPush.isEnable() && ModuleClass.noPush.getCollisionList().is("Блоки")) {
         ci.cancel();
      }
   }

   @Inject(method = "move", at = @At("HEAD"), cancellable = true)
   private void onMoveHook(MovementType movementType, Vec3d movement, @NotNull CallbackInfo ci) {
      EventMove event = new EventMove(movement);
      event.call();
      if (event.isCancelled() || !event.getMovePos().equals(movement)) {
         if (event.isCancelled()) {
            ci.cancel();
         } else {
            double d = this.getX();
            double e = this.getZ();
            super.move(movementType, event.getMovePos());
            float f = (float)Math.sqrt(Math.pow(this.getX() - d, 2.0) + Math.pow(this.getZ() - e, 2.0));
            this.updateLimbs(f);
            ci.cancel();
         }
      }
   }

   @Inject(method = "closeHandledScreen", at = @At("HEAD"), cancellable = true)
   private void onCloseHandledScreen(CallbackInfo ci) {
      int syncId = this.currentScreenHandler.syncId;
      EventCloseInv event = new EventCloseInv(syncId);
      event.call();
      if (!event.isCancelled()) {
         this.field_3944.sendPacket(new CloseHandledScreenC2SPacket(syncId));
      }

      this.method_3137();
      ci.cancel();
   }

   @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
   private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleClass.lockSlot != null && ModuleClass.lockSlot.isCurrentSlotLockedForDrop()) {
         cir.setReturnValue(false);
      }
   }
}
