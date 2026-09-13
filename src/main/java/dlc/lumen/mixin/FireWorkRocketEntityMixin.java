package dlc.lumen.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dlc.lumen.api.events.implement.EventFireWork;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireWorkRocketEntityMixin extends ProjectileEntity {
   @Unique
   private Vec3d lumen$rotation;
   @Unique
   private boolean lumen$boostApplied = false;
   @Shadow
   private LivingEntity field_7616;

   protected FireWorkRocketEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(method = "tick", at = @At("HEAD"))
   public void onTick(CallbackInfo ci) {
      FireworkRocketEntity self = (FireworkRocketEntity)(Object)this;
      EventFireWork event = new EventFireWork(self);
      event.call();
   }

   @ModifyExpressionValue(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;", ordinal = 0)
   )
   public Vec3d captureRotation(Vec3d original) {
      return original;
   }
}
