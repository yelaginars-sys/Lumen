package dlc.lumen.client.modules.impl.player.components;

import dlc.lumen.client.modules.impl.combat.components.rotations.SlothDatasetProfile;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class LegitRotationController {
   private static final Random RANDOM = new Random();
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4;
   private float volume5;
   private float volume6;
   private float volume7;
   private float volume8;
   private float volume9;
   private float volume10;
   private float volume11;
   private float volume12;
   private float volume13;
   private float volume14;
   private long timestamp;
   private long timestamp2;
   private int index;
   private boolean flag;
   private boolean active;
   private double level;
   private float volume15;
   private float volume16;
   private float volume17;

   public LegitRotationController() {
      this.reset();
   }

   public void reset() {
      this.active = false;
      this.volume3 = 0.0F;
      this.volume4 = 0.0F;
      this.volume = 0.0F;
      this.volume2 = 0.0F;
      this.volume5 = 0.0F;
      this.volume6 = 0.0F;
      this.level = 0.0;
      this.timestamp2 = 0L;
      this.index = 0;
      this.flag = false;
      this.volume15 = 0.015F;
      this.volume16 = 2.3F;
      this.volume17 = RANDOM.nextFloat() * 6.283F;
   }

   public void startRotation(float fromYaw, float fromPitch, float toYaw, float toPitch, boolean isAction) {
      this.volume5 = MathHelper.wrapDegrees(fromYaw);
      this.volume6 = MathHelper.clamp(fromPitch, -89.0F, 89.0F);
      this.volume = MathHelper.wrapDegrees(toYaw);
      this.volume2 = MathHelper.clamp(toPitch, -89.0F, 89.0F);
      this.volume3 = this.volume5;
      this.volume4 = this.volume6;
      this.flag = isAction;
      this.active = true;
      this.level = 0.0;
      this.timestamp = System.currentTimeMillis();
      this.index = SlothDatasetProfile.choose(RANDOM, isAction);
      this.timestamp2 = SlothDatasetProfile.duration(isAction, this.index);
      this.timestamp2 = (long)(this.timestamp2 * (0.85 + RANDOM.nextFloat() * 0.3));
      float var6 = this.helper(fromYaw, fromPitch, toYaw, toPitch);
      if (var6 < 5.0F) {
         this.timestamp2 = (long)(this.timestamp2 * 0.5);
      }

      this.volume7 = SlothDatasetProfile.maximumYawSpeed(RANDOM);
      this.volume8 = SlothDatasetProfile.maximumPitchSpeed(RANDOM);
      this.volume9 = SlothDatasetProfile.yawAcceleration(RANDOM);
      this.volume10 = SlothDatasetProfile.pitchAcceleration(RANDOM);
      this.volume11 = SlothDatasetProfile.yawDeceleration(RANDOM);
      this.volume12 = SlothDatasetProfile.pitchDeceleration(RANDOM);
      this.volume13 = SlothDatasetProfile.yawJerkLimit(RANDOM);
      this.volume14 = SlothDatasetProfile.pitchJerkLimit(RANDOM);
      this.volume15 = SlothDatasetProfile.pathNoiseScale(RANDOM);
      this.volume16 = 1.7F + RANDOM.nextFloat() * 1.2F;
      this.volume17 = RANDOM.nextFloat() * 6.283F;
   }

   public LegitRotationController.RotationStep update() {
      if (!this.active) {
         return new LegitRotationController.RotationStep(this.volume3, this.volume4, false);
      }

      long var1 = System.currentTimeMillis() - this.timestamp;
      this.level = MathHelper.clamp((double)var1 / this.timestamp2, 0.0, 1.0);
      double var3 = SlothDatasetProfile.progress(this.flag, this.index, this.level);
      double var5 = SlothDatasetProfile.lateral(this.flag, this.index, this.level);
      float var7 = this.volume - this.volume5;
      float var8 = this.volume2 - this.volume6;
      float var9 = this.volume5 + (float)(var7 * var3);
      float var10 = this.volume6 + (float)(var8 * var3);
      float var11 = (float)(Math.sin(this.volume16 * 2.0F * Math.PI * this.level + this.volume17) * this.volume15);
      float var12 = (float)(
         Math.sin(this.volume16 * 3.0F * Math.PI * this.level + this.volume17 + 1.2) * this.volume15 * 0.5
      );
      float var13 = (float)(var5 * 4.0);
      float var14 = this.volume7 / 20.0F;
      float var15 = this.volume8 / 20.0F;
      float var16 = this.volume9 / 400.0F;
      float var17 = this.volume10 / 400.0F;
      float var18 = var9 + var13 + var11;
      float var19 = var10 + var12;
      var18 = MathHelper.wrapDegrees(var18);
      var19 = MathHelper.clamp(var19, -89.0F, 89.0F);
      float var20 = MathHelper.wrapDegrees(var18 - this.volume3);
      float var21 = var19 - this.volume4;
      float var22 = MathHelper.clamp(var20, -var14, var14);
      float var23 = MathHelper.clamp(var21, -var15, var15);
      if (this.level > 0.85 && Math.abs(var20) < 0.5) {
         var22 = (float)(var22 * 0.2);
      }

      if (this.level > 0.85 && Math.abs(var21) < 0.5) {
         var23 = (float)(var23 * 0.2);
      }

      float var24 = 1.0F;
      if (this.level > 0.9) {
         var24 = 1.0F - (float)((this.level - 0.9) / 0.1) * 0.8F;
      }

      this.volume3 += var22 * var24;
      this.volume4 += var23 * var24;
      this.volume3 = MathHelper.wrapDegrees(this.volume3);
      this.volume4 = MathHelper.clamp(this.volume4, -89.0F, 89.0F);
      boolean var25 = this.level >= 1.0;
      if (var25) {
         float var26 = MathHelper.wrapDegrees(this.volume - this.volume3);
         float var27 = this.volume2 - this.volume4;
         if (Math.abs(var26) > 0.1) {
            this.volume3 += var26 * 0.5F;
         }

         if (Math.abs(var27) > 0.1) {
            this.volume4 += var27 * 0.5F;
         }

         this.volume3 = MathHelper.wrapDegrees(this.volume3);
         this.volume4 = MathHelper.clamp(this.volume4, -89.0F, 89.0F);
         this.active = false;
      }

      return new LegitRotationController.RotationStep(this.volume3, this.volume4, var25);
   }

   public boolean isActive() {
      return this.active;
   }

   private float helper(float yaw1, float pitch1, float yaw2, float pitch2) {
      float var5 = MathHelper.wrapDegrees(yaw2 - yaw1);
      float var6 = pitch2 - pitch1;
      return (float)Math.sqrt(var5 * var5 + var6 * var6);
   }

   public static class RotationStep {
      public final float yaw;
      public final float pitch;
      public final boolean complete;

      public RotationStep(float yaw, float pitch, boolean complete) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.complete = complete;
      }
   }
}
