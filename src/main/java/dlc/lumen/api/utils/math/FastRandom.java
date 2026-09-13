package dlc.lumen.api.utils.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FastRandom extends Random {
   private final ThreadLocalRandom threadRandom = ThreadLocalRandom.current();
   private Random random = null;
   private volatile boolean useSeeded;
   private volatile boolean seedDirty;
   private volatile long seed;

   private void ensureSeededRandom() {
      if (this.random == null) {
         this.random = new Random(this.seed);
         this.seedDirty = false;
      } else if (this.seedDirty) {
         this.random.setSeed(this.seed);
         this.seedDirty = false;
      }
   }

   public static long mix(long left, long right) {
      left *= left * 6364136223846793005L + 1442695040888963407L;
      return left + right;
   }

   @Override
   public void setSeed(long seed) {
      this.seed = seed;
      this.useSeeded = true;
      this.seedDirty = true;
   }

   @Override
   public void nextBytes(byte[] bytes) {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         this.random.nextBytes(bytes);
      } else {
         this.threadRandom.nextBytes(bytes);
      }
   }

   @Override
   public int nextInt() {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextInt();
      } else {
         return this.threadRandom.nextInt();
      }
   }

   @Override
   public int nextInt(int bound) {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextInt(bound);
      } else {
         return this.threadRandom.nextInt(bound);
      }
   }

   @Override
   public long nextLong() {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextLong();
      } else {
         return this.threadRandom.nextLong();
      }
   }

   @Override
   public boolean nextBoolean() {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextBoolean();
      } else {
         return this.threadRandom.nextBoolean();
      }
   }

   @Override
   public float nextFloat() {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextFloat();
      } else {
         return this.threadRandom.nextFloat();
      }
   }

   @Override
   public double nextDouble() {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextDouble();
      } else {
         return this.threadRandom.nextDouble();
      }
   }

   @Override
   public double nextGaussian() {
      if (this.useSeeded) {
         this.ensureSeededRandom();
         return this.random.nextGaussian();
      } else {
         return this.threadRandom.nextGaussian();
      }
   }
}
