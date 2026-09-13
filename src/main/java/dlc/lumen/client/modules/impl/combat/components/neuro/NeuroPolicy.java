package dlc.lumen.client.modules.impl.combat.components.neuro;

import java.util.Arrays;
import java.util.Random;

public final class NeuroPolicy {
   private final NeuroWeights neuroWeights;
   private final Random random2;
   private final float[] volume;
   private final float[] volume2;
   private final float[] volume3;
   private final float[] volume4;
   private final float[] volume5;
   private final float[] volume6;
   private final float[] volume7;
   public float muYaw;
   public float muPitch;
   public float sigmaYaw;
   public float sigmaPitch;
   public float moveProbability;
   public float attackProbability;
   public boolean moved;

   public NeuroPolicy(NeuroWeights weights, Random random) {
      this.neuroWeights = weights;
      this.random2 = random;
      this.volume = new float[weights.hidden];
      this.volume2 = new float[weights.input];
      this.volume3 = new float[3 * weights.hidden];
      this.volume4 = new float[3 * weights.hidden];
      this.volume5 = new float[weights.head];
      this.volume6 = new float[weights.output];
      this.volume7 = new float[weights.latent];
      this.reseed();
   }

   public int observationSize() {
      return this.neuroWeights.obs;
   }

   public void reseed() {
      Arrays.fill(this.volume, 0.0F);
      if (this.neuroWeights.zeroLatent()) {
         Arrays.fill(this.volume7, 0.0F);
      } else {
         for (int var1 = 0; var1 < this.volume7.length; var1++) {
            this.volume7[var1] = (float)this.random2.nextGaussian();
         }
      }
   }

   public void resetState() {
      Arrays.fill(this.volume, 0.0F);
   }

   public void setLatent(float[] values) {
      System.arraycopy(values, 0, this.volume7, 0, Math.min(values.length, this.volume7.length));
      Arrays.fill(this.volume, 0.0F);
   }

   public void step(float[] observation) {
      int var2 = this.neuroWeights.hidden;
      int var3 = this.neuroWeights.input;
      System.arraycopy(observation, 0, this.volume2, 0, this.neuroWeights.obs);
      System.arraycopy(this.volume7, 0, this.volume2, this.neuroWeights.obs, this.neuroWeights.latent);
      float[] var4 = this.neuroWeights.weightIh;
      float[] var5 = this.neuroWeights.weightHh;

      for (int var6 = 0; var6 < 3 * var2; var6++) {
         int var7 = var6 * var3;
         float var8 = this.neuroWeights.biasIh[var6];

         for (int var9 = 0; var9 < var3; var9++) {
            var8 += var4[var7 + var9] * this.volume2[var9];
         }

         this.volume3[var6] = var8;
         int var21 = var6 * var2;
         float var10 = this.neuroWeights.biasHh[var6];

         for (int var11 = 0; var11 < var2; var11++) {
            var10 += var5[var21 + var11] * this.volume[var11];
         }

         this.volume4[var6] = var10;
      }

      for (int var12 = 0; var12 < var2; var12++) {
         float var15 = computefloat4(this.volume3[var12] + this.volume4[var12]);
         float var18 = computefloat4(this.volume3[var2 + var12] + this.volume4[var2 + var12]);
         float var22 = computefloat5(this.volume3[2 * var2 + var12] + var15 * this.volume4[2 * var2 + var12]);
         this.volume[var12] = (1.0F - var18) * var22 + var18 * this.volume[var12];
      }

      for (int var13 = 0; var13 < this.neuroWeights.head; var13++) {
         int var16 = var13 * var2;
         float var19 = this.neuroWeights.headBias[var13];

         for (int var23 = 0; var23 < var2; var23++) {
            var19 += this.neuroWeights.headWeight[var16 + var23] * this.volume[var23];
         }

         this.volume5[var13] = computefloat5(var19);
      }

      for (int var14 = 0; var14 < this.neuroWeights.output; var14++) {
         int var17 = var14 * this.neuroWeights.head;
         float var20 = this.neuroWeights.outBias[var14];

         for (int var24 = 0; var24 < this.neuroWeights.head; var24++) {
            var20 += this.neuroWeights.outWeight[var17 + var24] * this.volume5[var24];
         }

         this.volume6[var14] = var20;
      }

      this.muYaw = computefloat2(this.volume6[0], this.neuroWeights.yawScale, this.neuroWeights.yawMax);
      this.muPitch = computefloat2(this.volume6[1], this.neuroWeights.pitchScale, this.neuroWeights.pitchMax);
      this.sigmaYaw = computefloat3(computefloat6(this.volume6[2]), this.neuroWeights.sigmaMin, this.neuroWeights.sigmaMax);
      this.sigmaPitch = computefloat3(computefloat6(this.volume6[3]), this.neuroWeights.sigmaMin, this.neuroWeights.sigmaMax);
      this.moveProbability = computefloat4(this.volume6[4]);
      this.attackProbability = this.neuroWeights.hasAttackHead() ? computefloat4(this.volume6[5]) : 0.0F;
   }

   public void sample(float[] result, float gcd) {
      this.moved = this.random2.nextFloat() < this.moveProbability;
      if (!this.moved) {
         result[0] = 0.0F;
         result[1] = 0.0F;
      } else {
         float var3 = this.muYaw + this.sigmaYaw * this.computefloat();
         float var4 = this.muPitch + this.sigmaPitch * this.computefloat();
         result[0] = Math.round(var3 / gcd) * gcd;
         result[1] = Math.round(var4 / gcd) * gcd;
      }
   }

   public boolean sampleAttack() {
      return this.neuroWeights.hasAttackHead() && this.random2.nextFloat() < this.attackProbability;
   }

   private float computefloat() {
      float var1 = (float)this.random2.nextGaussian();
      return var1 < -2.0F ? -2.0F : (var1 > 2.0F ? 2.0F : var1);
   }

   private static float computefloat2(float raw, float scale, float maximum) {
      return maximum * computefloat5(raw * (scale / maximum));
   }

   private static float computefloat3(float v, float lo, float hi) {
      return v < lo ? lo : (v > hi ? hi : v);
   }

   private static float computefloat4(float x) {
      if (x >= 0.0F) {
         float var2 = (float)Math.exp(-x);
         return 1.0F / (1.0F + var2);
      } else {
         float var1 = (float)Math.exp(x);
         return var1 / (1.0F + var1);
      }
   }

   private static float computefloat5(float x) {
      if (x > 8.0F) {
         return 1.0F;
      }

      if (x < -8.0F) {
         return -1.0F;
      }

      float var1 = (float)Math.exp(2.0F * x);
      return (var1 - 1.0F) / (var1 + 1.0F);
   }

   private static float computefloat6(float x) {
      return x > 20.0F ? x : (float)Math.log1p(Math.exp(x));
   }
}
