package dlc.lumen.client.voice.call;

public final class AudioSafety {
   public static final float MAX_GAIN = 1.0F;
   public static final float DEFAULT_GAIN = 0.55F;
   private static final float VOLUME = 0.65F;
   private static final double LEVEL = 0.42;
   private static final double LEVEL2 = 3000.0;
   private static final int INDEX = 15;
   private int index;
   private boolean muted;

   public boolean isGarbage(short[] frame) {
      if (frame != null && frame.length >= 2) {
         int var2 = 0;
         double var3 = 0.0;

         for (int var5 = 0; var5 < frame.length; var5++) {
            var3 += (double)frame[var5] * frame[var5];
            if (var5 > 0 && frame[var5] < 0 != frame[var5 - 1] < 0) {
               var2++;
            }
         }

         double var10 = Math.sqrt(var3 / frame.length);
         double var7 = (double)var2 / (frame.length - 1);
         boolean var9 = var10 > 3000.0 && var7 > 0.42;
         if (var9) {
            this.index++;
            if (this.index >= 15) {
               this.muted = true;
            }
         } else {
            this.index = Math.max(0, this.index - 2);
            if (this.muted && this.index == 0) {
               this.muted = false;
            }
         }

         return this.muted;
      } else {
         return this.muted;
      }
   }

   public boolean isMuted() {
      return this.muted;
   }

   public void reset() {
      this.index = 0;
      this.muted = false;
   }

   public static float clampGain(float gain) {
      return Math.max(0.0F, Math.min(1.0F, gain));
   }

   public static int limit(float sample) {
      float var1 = sample / 32768.0F;
      float var2 = Math.abs(var1);
      if (var2 > 0.65F) {
         float var3 = var2 - 0.65F;
         float var4 = 0.65F + 0.35000002F * (var3 / (var3 + 0.35000002F));
         var1 = Math.signum(var1) * var4;
      }

      int var5 = Math.round(var1 * 32767.0F);
      return Math.max(-32768, Math.min(32767, var5));
   }
}
