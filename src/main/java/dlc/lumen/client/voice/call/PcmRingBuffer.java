package dlc.lumen.client.voice.call;

public final class PcmRingBuffer {
   private static final int FADE_SAMPLES = 96;
   private final short[] buffer;
   private int readPos;
   private int writePos;
   private int availableCount;
   private boolean fadeActive;
   private long underrunCount;
   private long droppedSampleCount;

   public PcmRingBuffer(int capacitySamples) {
      this.buffer = new short[capacitySamples];
   }

   public synchronized int available() {
      return this.availableCount;
   }

   public synchronized int capacity() {
      return this.buffer.length;
   }

   public long getUnderruns() {
      return this.underrunCount;
   }

   public long getDroppedSamples() {
      return this.droppedSampleCount;
   }

   public synchronized void write(short[] samples) {
      if (samples != null && samples.length != 0) {
         int var2 = Math.min(samples.length, this.buffer.length);
         int var3 = this.availableCount + var2 - this.buffer.length;
         if (var3 > 0) {
            this.readPos = (this.readPos + var3) % this.buffer.length;
            this.availableCount -= var3;
            this.droppedSampleCount += var3;
         }

         for (int var4 = 0; var4 < var2; var4++) {
            this.buffer[this.writePos] = samples[var4];
            this.writePos = (this.writePos + 1) % this.buffer.length;
         }

         this.availableCount += var2;
      }
   }

   public synchronized int read(short[] out) {
      int var2 = Math.min(this.availableCount, out.length);

      for (int var3 = 0; var3 < var2; var3++) {
         out[var3] = this.buffer[this.readPos];
         this.readPos = (this.readPos + 1) % this.buffer.length;
      }

      this.availableCount -= var2;
      if (var2 < out.length) {
         this.underrunCount++;
         int var7 = Math.min(96, var2);

         for (int var4 = 0; var4 < var7; var4++) {
            int var5 = var2 - var7 + var4;
            float var6 = 1.0F - (float)var4 / var7;
            out[var5] = (short)(out[var5] * var6);
         }

         for (int var9 = var2; var9 < out.length; var9++) {
            out[var9] = 0;
         }

         this.fadeActive = true;
      } else if (this.fadeActive) {
         int var8 = Math.min(96, out.length);

         for (int var10 = 0; var10 < var8; var10++) {
            out[var10] = (short)(out[var10] * ((float)var10 / var8));
         }

         this.fadeActive = false;
      }

      return var2;
   }

   public synchronized void clear() {
      this.readPos = 0;
      this.writePos = 0;
      this.availableCount = 0;
      this.fadeActive = false;
   }
}
