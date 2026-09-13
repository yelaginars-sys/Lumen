package dlc.lumen.client.modules.impl.combat.components.neuro;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NeuroRotation extends RotationsSystem implements QClient {
   private static final int INDEX = 1;
   private static final int INDEX2 = 40;
   private static final float VOLUME = 0.034064F;
   private static final float VOLUME2 = 0.6F;
   private static final float VOLUME3 = 3.0F;
   private static final float VOLUME4 = 12.0F;
   private final Random random = new Random();
   private float[] volume = new float[20];
   private final float[] volume2 = new float[2];
   private NeuroPolicy neuroPolicy;
   private NeuroWeights neuroWeights;
   private boolean flag;
   private boolean flag2 = true;
   private LivingEntity livingEntity;
   private int index = Integer.MIN_VALUE;
   private int index2;
   private int index3;
   private float volume3;
   private float volume4;
   private boolean flag3;
   private float volume5;
   private float volume6;
   private float volume7;
   private float volume8;
   private float volume9 = 0.6F;
   private float volume10 = 3.0F;
   private float volume11 = 12.0F;
   private float volume12;
   private float volume13;
   private float volume14 = 0.55F;

   public boolean isAvailable() {
      this.updateState();
      return this.neuroPolicy != null;
   }

   private void updateState() {
      NeuroWeights var1 = NeuroStyles.activeWeights();
      if (var1 == null) {
         if (this.flag) {
            return;
         }

         var1 = NeuroWeights.get();
         if (var1 == null) {
            this.flag = true;
            return;
         }
      }

      if (this.neuroPolicy == null || this.neuroWeights != var1) {
         this.neuroPolicy = new NeuroPolicy(var1, this.random);
         this.neuroWeights = var1;
         if (this.volume.length != var1.obs) {
            this.volume = new float[var1.obs];
         }
      }
   }

   public void reset() {
      this.livingEntity = null;
      this.index = Integer.MIN_VALUE;
      this.index2 = 0;
      this.index3 = 40;
      this.flag3 = false;
      this.volume5 = this.volume6 = this.volume7 = this.volume8 = 0.0F;
      this.volume9 = 0.6F;
      this.volume10 = 3.0F;
      this.volume11 = 12.0F;
      if (this.neuroPolicy != null) {
         this.neuroPolicy.reseed();
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      this.updateState();
      if (this.neuroPolicy != null && mc.player != null && target != null) {
         RotationStorage var2 = RotationStorage.instance;
         if (var2 == null || var2.currentPriority() <= 1) {
            int var3 = mc.player.age;
            if (var3 != this.index) {
               this.index = var3;
               if (this.livingEntity != target) {
                  this.livingEntity = target;
                  this.index3 = 0;
                  this.flag3 = false;
                  this.neuroPolicy.reseed();
               } else if (this.index3 < 40) {
                  this.index3++;
               }

               Vec3d var4 = this.computeVec3d(target);
               Vec3d var5 = mc.player.getEyePos();
               double var6 = var4.x - var5.x;
               double var8 = var4.y - var5.y;
               double var10 = var4.z - var5.z;
               double var12 = Math.sqrt(var6 * var6 + var10 * var10);
               float var14 = (float)(Math.toDegrees(Math.atan2(var10, var6)) - 90.0);
               float var15 = (float)(-Math.toDegrees(Math.atan2(var8, var12)));
               float var16 = mc.player.getYaw();
               float var17 = mc.player.getPitch();
               float var18 = MathHelper.wrapDegrees(var14 - var16);
               float var19 = var15 - var17;
               float var20 = computefloat(var18, var19, var17);
               float var21 = 0.0F;
               float var22 = 0.0F;
               if (this.flag3) {
                  var21 = MathHelper.wrapDegrees(var14 - this.volume3);
                  var22 = var15 - this.volume4;
               }

               this.volume3 = var14;
               this.volume4 = var15;
               this.flag3 = true;
               this.updateState2(target, var18, var19, var20, var21, var22, (float)Math.sqrt(var12 * var12 + var8 * var8));
               this.neuroPolicy.step(this.volume);
               this.neuroPolicy.sample(this.volume2, GCDUtil.getGCDValue());
               this.flag2 = !this.neuroWeights.hasAttackHead() || this.neuroPolicy.sampleAttack();
               float var23 = var16 + this.volume2[0];
               float var24 = MathHelper.clamp(var17 + this.volume2[1], -89.0F, 89.0F);
               RotationStorage.update(new Rotation(var23, var24), 360.0F, 360.0F, 30.0F, 22.0F, 2, 1, false);
               float var25 = computefloat(MathHelper.wrapDegrees(var14 - var23), var15 - var24, var24);
               this.volume7 = this.volume5;
               this.volume8 = this.volume6;
               this.volume5 = this.volume2[0];
               this.volume6 = this.volume2[1];
               this.volume9 = this.volume9 + 0.034064F * ((this.neuroPolicy.moved ? 1.0F : 0.0F) - this.volume9);
               this.volume10 = this.volume10 + 0.034064F * (Math.abs(this.volume2[0]) - this.volume10);
               this.volume11 = this.volume11 + 0.034064F * (var25 - this.volume11);
               this.index2++;
            }
         }
      }
   }

   public boolean attackConsent() {
      return this.neuroPolicy == null || this.flag2;
   }

   private void updateState2(LivingEntity target, float errorYaw, float errorPitch, float offset, float rateYaw, float ratePitch, float distance) {
      float var8 = (float)target.getVelocity().horizontalLength();
      double var9 = (Math.PI * 2) * (this.index2 % 40) / 40.0;
      this.volume[0] = computefloat2(errorYaw / 30.0F, -6.0F, 6.0F);
      this.volume[1] = computefloat2(errorPitch / 15.0F, -6.0F, 6.0F);
      this.volume[2] = computefloat3(errorYaw / 8.0F);
      this.volume[3] = computefloat3(errorPitch / 5.0F);
      this.volume[4] = computefloat2(offset / 30.0F, 0.0F, 6.0F);
      this.volume[5] = computefloat3(offset / 6.0F);
      this.volume[6] = computefloat2(rateYaw / 8.0F, -6.0F, 6.0F);
      this.volume[7] = computefloat2(ratePitch / 4.0F, -6.0F, 6.0F);
      this.volume[8] = computefloat2(this.volume5 / 8.0F, -6.0F, 6.0F);
      this.volume[9] = computefloat2(this.volume6 / 4.0F, -6.0F, 6.0F);
      this.volume[10] = computefloat2(this.volume7 / 8.0F, -6.0F, 6.0F);
      this.volume[11] = computefloat2(this.volume8 / 4.0F, -6.0F, 6.0F);
      this.volume[12] = computefloat2(distance / 6.0F, 0.0F, 2.0F);
      this.volume[13] = computefloat2(var8 / 0.34F, 0.0F, 2.0F);
      this.volume[14] = computefloat2(this.index3 / 40.0F, 0.0F, 1.0F);
      this.volume[15] = (float)Math.sin(var9);
      this.volume[16] = (float)Math.cos(var9);
      this.volume[17] = this.volume9;
      this.volume[18] = computefloat2(this.volume10 / 12.0F, 0.0F, 6.0F);
      this.volume[19] = computefloat2(this.volume11 / 25.0F, 0.0F, 6.0F);
      if (this.volume.length >= 28) {
         this.volume[20] = computefloat2((float)mc.player.getVelocity().horizontalLength() / 0.34F, 0.0F, 2.0F);
         this.volume[21] = computefloat3((float)mc.player.getVelocity().y / 0.5F);
         this.volume[22] = mc.player.isOnGround() ? 1.0F : 0.0F;
         this.volume[23] = mc.player.isTouchingWater() ? 1.0F : 0.0F;
         this.volume[24] = mc.player.isSprinting() ? 1.0F : 0.0F;
         this.volume[25] = mc.player.isSneaking() ? 1.0F : 0.0F;
         this.volume[26] = computefloat2(mc.player.getAttackCooldownProgress(0.0F), 0.0F, 1.0F);
         this.volume[27] = computefloat2(target.hurtTime / 10.0F, 0.0F, 1.0F);
      }
   }

   private static float computefloat(float errorYaw, float errorPitch, float pitch) {
      float var3 = Math.max((float)Math.cos(Math.toRadians(pitch)), 0.05F);
      float var4 = errorYaw * var3;
      return (float)Math.sqrt(var4 * var4 + errorPitch * errorPitch);
   }

   private Vec3d computeVec3d(LivingEntity target) {
      Box var2 = this.getPredictedBox(target);
      if (this.neuroWeights != null && this.neuroWeights.obs >= 28) {
         return var2.getCenter();
      }

      this.volume12 = this.volume12 + (0.035F + this.random.nextFloat() * 0.02F);
      this.volume13 = this.volume13 + (this.random.nextFloat() - 0.5F) * 0.14F;
      this.volume14 = this.volume14 + ((float)Math.sin(this.volume12 * 0.31F) * 0.14F + 0.55F - this.volume14) * 0.08F;
      double var3 = var2.getLengthX() * 0.3 * (0.4 + 0.6 * Math.abs(Math.sin(this.volume12 * 0.17F)));
      Vec3d var5 = var2.getCenter();
      return new Vec3d(
         var5.x + Math.cos(this.volume13) * var3,
         var2.minY + var2.getLengthY() * MathHelper.clamp(this.volume14, 0.25F, 0.85F),
         var5.z + Math.sin(this.volume13) * var3
      );
   }

   private static float computefloat2(float v, float lo, float hi) {
      return v < lo ? lo : (v > hi ? hi : v);
   }

   private static float computefloat3(float x) {
      if (x > 8.0F) {
         return 1.0F;
      }

      if (x < -8.0F) {
         return -1.0F;
      }

      float var1 = (float)Math.exp(2.0F * x);
      return (var1 - 1.0F) / (var1 + 1.0F);
   }
}
