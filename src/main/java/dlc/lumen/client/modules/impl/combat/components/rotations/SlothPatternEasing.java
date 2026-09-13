package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.utils.animation.Easing;
import dlc.lumen.api.utils.animation.Easings;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public final class SlothPatternEasing {
   private static final SlothPatternEasing.PatternOffset[] PATTERN_OFFSET = computePatternOffset();

   private SlothPatternEasing() {
   }

   public static float easeOutQuart(float progress) {
      float var1 = MathHelper.clamp(progress, 0.0F, 1.0F);
      return (float)Easings.QUART_OUT.ease(var1);
   }

   private static SlothPatternEasing.PatternOffset[] computePatternOffset() {
      short var0 = 192;
      SlothPatternEasing.PatternOffset[] var1 = new SlothPatternEasing.PatternOffset[var0];
      Random var2 = new Random(12648430L);

      for (int var3 = 0; var3 < var0; var3++) {
         double var4 = (double)var3 / (var0 - 1);
         double var6 = Math.sin(var4 * Math.PI * 2.0) * 0.55 + Math.sin(var4 * Math.PI * 6.0 + 1.2) * 0.15 + Math.sin(var4 * Math.PI * 11.0 - 0.4) * 0.06;
         double var8 = Math.cos(var4 * Math.PI * 2.5) * 0.28 + Math.cos(var4 * Math.PI * 5.0 + 0.8) * 0.09 + Math.cos(var4 * Math.PI * 9.0) * 0.04;
         var6 += var2.nextGaussian() * 0.08;
         var8 += var2.nextGaussian() * 0.05;
         var1[var3] = new SlothPatternEasing.PatternOffset((float)var6, (float)var8);
      }

      return var1;
   }

   public static float finalSpeed(PlayerEntity player, float progress) {
      float var2 = easeOutQuart(progress);
      if (player == null) {
         return var2;
      }

      double var3 = player.getVelocity().horizontalLength();
      float var5 = (float)Math.min(1.5, 1.0 + var3 * 0.5);
      if (!player.isOnGround()) {
         var5 *= MathHelper.clamp(1.0F + player.fallDistance * 0.015F, 1.0F, 1.22F);
      }

      float var6 = player.getHandSwingProgress(1.0F);
      var5 *= 1.0F + var6 * 0.08F;
      return var2 * MathHelper.clamp(var5, 0.55F, 1.6F);
   }

   public static float clampAndScale(float delta, float maxAbs, float finalSpeed) {
      return delta == 0.0F ? 0.0F : Math.min(Math.abs(delta), maxAbs) * Math.signum(delta) * finalSpeed;
   }

   public enum Curve {
      SINE_IN(Easings.SINE_IN),
      SINE_OUT(Easings.SINE_OUT),
      SINE_IN_OUT(Easings.SINE_IN_OUT),
      QUAD_IN(Easings.QUAD_IN),
      QUAD_OUT(Easings.QUAD_OUT),
      QUAD_IN_OUT(Easings.QUAD_IN_OUT),
      CUBIC_IN(Easings.CUBIC_IN),
      CUBIC_OUT(Easings.CUBIC_OUT),
      CUBIC_IN_OUT(Easings.CUBIC_IN_OUT),
      QUART_IN(Easings.QUART_IN),
      QUART_OUT(Easings.QUART_OUT),
      QUART_IN_OUT(Easings.QUART_IN_OUT),
      QUINT_IN(Easings.QUINT_IN),
      QUINT_OUT(Easings.QUINT_OUT),
      QUINT_IN_OUT(Easings.QUINT_IN_OUT),
      EXPO_IN(Easings.EXPO_IN),
      EXPO_OUT(Easings.EXPO_OUT),
      EXPO_IN_OUT(Easings.EXPO_IN_OUT),
      CIRC_IN(Easings.CIRC_IN),
      CIRC_OUT(Easings.CIRC_OUT),
      CIRC_IN_OUT(Easings.CIRC_IN_OUT),
      BACK_IN(Easings.BACK_IN),
      BACK_OUT(Easings.BACK_OUT),
      BACK_IN_OUT(Easings.BACK_IN_OUT),
      ELASTIC_IN(Easings.ELASTIC_IN),
      ELASTIC_OUT(Easings.ELASTIC_OUT),
      ELASTIC_IN_OUT(Easings.ELASTIC_IN_OUT),
      BOUNCE_IN(Easings.BOUNCE_IN),
      BOUNCE_OUT(Easings.BOUNCE_OUT),
      BOUNCE_IN_OUT(Easings.BOUNCE_IN_OUT);

      private final Easing PATTERN_OFFSET;

      Curve(Easing easing) {
         this.PATTERN_OFFSET = easing;
      }

      public double apply(double t) {
         return this.PATTERN_OFFSET.ease(MathHelper.clamp(t, 0.0, 1.0));
      }
   }

   public static final class PatternDriver {
      private static final long PATTERN_OFFSET = 340000000L;
      private static final double LEVEL = 26.0;
      private final Random random2 = new Random();
      private SlothPatternEasing.PatternOffset[] patternOffset = SlothPatternEasing.PATTERN_OFFSET;
      private int index;
      private double level;
      private SlothPatternEasing.Curve curve = SlothPatternEasing.Curve.QUART_OUT;
      private float volume = 0.55F;
      private float volume2 = 0.28F;
      private long timestamp;

      public void reset() {
         this.patternOffset = SlothPatternEasing.PATTERN_OFFSET;
         this.index = this.patternOffset.length > 0 ? this.random2.nextInt(this.patternOffset.length) : 0;
         this.level = 0.0;
         this.curve = computePatternOffset(this.random2);
         this.volume = 0.45F + this.random2.nextFloat() * 0.55F;
         this.volume2 = 0.22F + this.random2.nextFloat() * 0.28F;
         this.timestamp = 0L;
      }

      public void setPattern(SlothPatternEasing.PatternOffset[] custom) {
         if (custom != null && custom.length >= 2) {
            this.patternOffset = custom;
            this.index = this.index % this.patternOffset.length;
         }
      }

      public SlothPatternEasing.PatternOffset advance(double deltaTime, float progress) {
         long var4 = System.nanoTime();
         if (var4 - this.timestamp > 340000000L) {
            this.curve = computePatternOffset(this.random2);
            this.volume = 0.4F + this.random2.nextFloat() * 0.6F;
            this.volume2 = 0.2F + this.random2.nextFloat() * 0.3F;
            this.timestamp = var4;
         }

         for (this.level += deltaTime * 26.0; this.level >= 1.0; this.level--) {
            this.index = (this.index + 1) % this.patternOffset.length;
         }

         SlothPatternEasing.PatternOffset var6 = this.patternOffset[this.index];
         SlothPatternEasing.PatternOffset var7 = this.patternOffset[(this.index + 1) % this.patternOffset.length];
         float var8 = (float)this.level;
         float var9 = (float)this.curve.apply(var8);
         float var10 = MathHelper.lerp(var9, var6.yawOffset(), var7.yawOffset());
         float var11 = MathHelper.lerp(var9, var6.pitchOffset(), var7.pitchOffset());
         float var12 = MathHelper.clamp(progress, 0.0F, 1.0F);
         float var13 = 4.0F * var12 * (1.0F - var12);
         return new SlothPatternEasing.PatternOffset(var10 * this.volume * var13, var11 * this.volume2 * var13);
      }

      private static SlothPatternEasing.Curve computePatternOffset(Random random) {
         SlothPatternEasing.Curve[] var1 = new SlothPatternEasing.Curve[]{
            SlothPatternEasing.Curve.QUART_OUT,
            SlothPatternEasing.Curve.QUINT_OUT,
            SlothPatternEasing.Curve.CUBIC_OUT,
            SlothPatternEasing.Curve.SINE_IN_OUT,
            SlothPatternEasing.Curve.EXPO_OUT,
            SlothPatternEasing.Curve.CIRC_OUT,
            SlothPatternEasing.Curve.QUAD_IN_OUT
         };
         SlothPatternEasing.Curve[] var2 = new SlothPatternEasing.Curve[]{
            SlothPatternEasing.Curve.BACK_OUT, SlothPatternEasing.Curve.QUINT_IN_OUT, SlothPatternEasing.Curve.CUBIC_IN_OUT
         };
         return random.nextInt(10) < 9 ? var1[random.nextInt(var1.length)] : var2[random.nextInt(var2.length)];
      }
   }

   public record PatternOffset(float yawOffset, float pitchOffset) {

      public PatternOffset(float yawOffset, float pitchOffset) {
         this.yawOffset = yawOffset;
         this.pitchOffset = pitchOffset;
      }

      public float yawOffset() {
         return this.yawOffset;
      }

      public float pitchOffset() {
         return this.pitchOffset;
      }
   }
}
