package dlc.lumen.api.utils.render.particles;

import dlc.lumen.api.utils.color.ColorUtils;
import java.util.concurrent.ThreadLocalRandom;

public class WorldParticle extends ParticleBase {
   private static final long timeMs = 600L;
   private static final long timeMs2 = 400L;
   private static final long timeMs3 = 7000L;
   private static final int[] countArray = new int[]{
      -65536, -33024, -256, -16711936, -16711681, -16776961, -7667713, -65281, -60269, -1, -16711681, -40121
   };
   private final long timeMs4 = System.currentTimeMillis();
   private final int count = countArray[ThreadLocalRandom.current().nextInt(countArray.length)];
   private final float fvalue = (float)(Math.random() * 1.5 + 0.5);
   private boolean gravity = true;
   private int color = -1;
   private boolean random;
   private boolean whiteOnSpawn;

   public WorldParticle(double x, double y, double z, double vx, double vy, double vz, long lifeMs) {
      super(x, y, z, -1, 0.5F, lifeMs, 600L, 400L);
      this.vx = vx;
      this.vy = vy;
      this.vz = vz;
   }

   public WorldParticle gravity(boolean gravity) {
      this.gravity = gravity;
      return this;
   }

   public WorldParticle size(float size) {
      this.size = size;
      return this;
   }

   public WorldParticle shape(ParticleShape shape) {
      this.shape = shape.resolve();
      return this;
   }

   public void tint(int color, boolean random, boolean whiteOnSpawn) {
      this.color = color;
      this.random = random;
      this.whiteOnSpawn = whiteOnSpawn;
   }

   @Override
   public int renderColor() {
      if (this.random) {
         return ColorUtils.applyAlpha(this.count, this.getFade());
      }

      if (this.whiteOnSpawn) {
         long var1 = System.currentTimeMillis() - this.timeMs4;
         if (var1 < 7000L) {
            float var3 = (float)var1 / 7000.0F;
            int var4 = (int)(255.0F + (ColorUtils.r(this.color) - 255) * var3);
            int var5 = (int)(255.0F + (ColorUtils.g(this.color) - 255) * var3);
            int var6 = (int)(255.0F + (ColorUtils.b(this.color) - 255) * var3);
            int var7 = ColorUtils.a(this.color) << 24 | var4 << 16 | var5 << 8 | var6;
            return ColorUtils.applyAlpha(var7, this.getFade());
         }
      }

      return ColorUtils.applyAlpha(this.color, this.getFade());
   }

   @Override
   public void tick() {
      this.savePrevious();
      if (mc.world == null) {
         this.updateFade();
      } else {
         if (this.vx * this.vx + this.vy * this.vy + this.vz * this.vz > 1.0E-4) {
            if (isSolid(this.x + this.vx, this.y, this.z)) {
               this.vx *= -0.8;
            } else {
               this.x = this.x + this.vx;
            }

            if (isSolid(this.x, this.y + this.vy, this.z)) {
               this.vy *= -0.8;
            } else {
               this.y = this.y + this.vy;
            }

            if (isSolid(this.x, this.y, this.z + this.vz)) {
               this.vz *= -0.8;
            } else {
               this.z = this.z + this.vz;
            }
         } else {
            this.x = this.x + this.vx;
            this.y = this.y + this.vy;
            this.z = this.z + this.vz;
         }

         this.vx *= 0.99;
         this.vy *= 0.99;
         this.vz *= 0.99;
         if (this.gravity) {
            this.vy -= 2.0E-4;
         }

         this.roll = this.roll + this.fvalue;
         this.updateFade();
      }
   }
}
