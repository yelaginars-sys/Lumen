package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import dlc.lumen.client.modules.impl.combat.components.interpolation.BestPoint;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class WhiteRiseRotation extends RotationsSystem implements QClient {
   private final Aura aura2;
   private LivingEntity livingEntity;
   private float volume;
   private float volume2;
   private float volume3;
   private boolean flag;
   private boolean flag2;
   private float volume4;
   private int index;

   public WhiteRiseRotation(Aura aura) {
      this.aura2 = aura;
   }

   public void reset() {
      this.livingEntity = null;
      this.volume3 = 0.0F;
      this.flag = false;
      this.volume4 = 0.0F;
      this.index = 0;
      this.flag2 = mc.player != null;
      if (mc.player != null) {
         this.volume = mc.player.getYaw();
         this.volume2 = mc.player.getPitch();
      } else {
         this.volume = 0.0F;
         this.volume2 = 0.0F;
      }
   }

   public void onAttack() {
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         if (mc.player.isBlocking()) {
            this.rotate = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
            this.volume = this.rotate.x;
            this.volume2 = this.rotate.y;
         } else {
            if (!this.flag2) {
               this.volume = mc.player.getYaw();
               this.volume2 = mc.player.getPitch();
               this.flag2 = true;
            }

            if (this.livingEntity != target) {
               this.livingEntity = target;
               this.volume3 = 0.0F;
               this.flag = false;
               this.index = 0;
            }

            this.index++;
            this.volume4 = (float)((Math.sin(this.index * 0.17) * 0.12 + (Math.random() * 0.08 - 0.04)) * 0.7F);
            Vec3d var2 = BestPoint.getMultipoint(target, 128.0);
            Vec2f var3 = RotationUtils.getRotations(var2);
            float var4 = var3.x;
            float var5 = var3.y;
            float var6 = Math.abs(MathHelper.wrapDegrees(var4 - this.volume));
            boolean var7 = mc.player.getAttackCooldownProgress(1.0F) > 0.9F && this.aura2.getWhiteRiseTicksToAttack() <= 1;
            if (!this.flag) {
               float var8 = 0.0055F;
               if (var6 > 60.0F) {
                  var8 += 0.028800001F;
               } else if (var6 > 30.0F) {
                  var8 += 0.014400001F;
               } else {
                  var8 += 0.0072000003F;
               }

               if (var7) {
                  var8 += 0.012857143F;
               }

               this.volume3 = this.volume3 + var8 * (1.6F + this.volume4);
               if (this.volume3 >= 0.22F) {
                  this.flag = true;
               }
            } else {
               float var21 = var7 ? 0.045F : 0.008F;
               this.volume3 = this.volume3 - var21 * (2.1F + this.volume4);
               if (this.volume3 <= -0.04F) {
                  this.flag = false;
               }
            }

            float var22 = MathHelper.clamp(this.volume3, 0.0F, mc.player.isGliding() ? 0.38F : 0.26F);
            if (var7) {
               var22 = Math.min(var22 + 0.1F, mc.player.isGliding() ? 0.46F : 0.34F);
            }

            var22 += this.volume4 * 0.5F;
            if (this.index % 7 == 0) {
               var22 += 0.03F;
            }

            float var9 = MathHelper.wrapDegrees(var4 - this.volume);
            float var10 = var5 - this.volume2;
            float var11 = mc.player.isGliding() ? 42.0F : (var7 ? 28.0F : 20.0F);
            float var12 = mc.player.isGliding() ? 12.0F : (var7 ? 4.5F : 2.8F);
            var9 = MathHelper.clamp(var9, -var11, var11);
            var10 = MathHelper.clamp(var10, -var12, var12);
            float var13 = var22 * 0.28F;
            float var14 = var22 * (0.85F + this.volume4 * 0.4F);
            float var15 = this.volume + var9 * var14;
            float var16 = this.volume2 + var10 * var13;
            float var17 = GCDUtil.getGCDValue();
            if (var17 > 0.0F) {
               var15 = this.volume + Math.round((var15 - this.volume) / var17) * var17;
               var16 = this.volume2 + Math.round((var16 - this.volume2) / var17) * var17;
            }

            var16 = MathHelper.clamp(var16, -89.0F, 89.0F);
            Rotation var18 = new Rotation(var15, var16);
            float var19 = mc.player.isGliding() && target.isGliding() ? 360.0F : 45.0F;
            RotationStorage.update(var18, var19, var19, var19, var19, 0, 1, Aura.clientLook.isState());
            this.rotate = new Vec2f(var18.getYaw(), var18.getPitch());
            this.volume = var18.getYaw();
            this.volume2 = var18.getPitch();
         }
      }
   }

   private Vec3d computeVec3d(LivingEntity target) {
      Vec3d var2 = BestPoint.getPoint(target);
      if (var2 == null) {
         var2 = target.getBoundingBox().getCenter();
      }

      return this.shouldUseElytraPredict(target) ? this.getPredictedPoint(target, var2) : var2;
   }
}
