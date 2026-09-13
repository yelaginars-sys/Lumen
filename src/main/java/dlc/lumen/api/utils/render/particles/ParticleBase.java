package dlc.lumen.api.utils.render.particles;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class ParticleBase implements QClient {
   protected double x;
   protected double y;
   protected double z;
   protected double prevX;
   protected double prevY;
   protected double prevZ;
   protected double vx;
   protected double vy;
   protected double vz;
   protected int color;
   protected float size;
   protected ParticleShape shape = ParticleShape.CUBES;
   protected float roll;
   protected final float spinPhase = (float)(Math.random() * 100.0);
   private final long timeMs = System.currentTimeMillis();
   private final long lifeMs;
   private final long timeMs2;
   private final long timeMs3;
   private boolean dying;
   private long timeMs4;
   private float fvalue;
   private float fade;

   protected ParticleBase(double x, double y, double z, int color, float size, long lifeMs, long fadeInMs, long fadeOutMs) {
      this.x = this.prevX = x;
      this.y = this.prevY = y;
      this.z = this.prevZ = z;
      this.color = color;
      this.size = size;
      this.lifeMs = lifeMs;
      this.timeMs2 = Math.max(1L, fadeInMs);
      this.timeMs3 = Math.max(1L, fadeOutMs);
      this.roll = (float)(Math.random() * 360.0);
   }

   public abstract void tick();

   public int renderColor() {
      return ColorUtils.applyAlpha(this.color, this.fade);
   }

   public void kill() {
      if (!this.dying) {
         this.dying = true;
         this.timeMs4 = System.currentTimeMillis();
         this.fvalue = this.fade;
      }
   }

   public boolean isDying() {
      return this.dying;
   }

   public boolean isDead() {
      return this.dying && this.fade <= 0.0F;
   }

   public float getFade() {
      return this.fade;
   }

   public float getSize() {
      return this.size;
   }

   public float getRoll() {
      return this.roll;
   }

   public float getSpinPhase() {
      return this.spinPhase;
   }

   public ParticleShape getShape() {
      return this.shape;
   }

   public double lerpX(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevX, this.x);
   }

   public double lerpY(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevY, this.y);
   }

   public double lerpZ(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevZ, this.z);
   }

   public double squaredDistanceTo(double px, double py, double pz) {
      double var7 = this.x - px;
      double var9 = this.y - py;
      double var11 = this.z - pz;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double horizontalSquaredDistanceTo(double px, double pz) {
      double var5 = this.x - px;
      double var7 = this.z - pz;
      return var5 * var5 + var7 * var7;
   }

   protected void updateFade() {
      long var1 = System.currentTimeMillis();
      if (!this.dying && var1 - this.timeMs > this.lifeMs) {
         this.dying = true;
         this.timeMs4 = var1;
         this.fvalue = this.fade;
      }

      if (this.dying) {
         float var3 = (float)(var1 - this.timeMs4) / (float)this.timeMs3;
         this.fade = Math.max(0.0F, this.fvalue * (1.0F - var3));
      } else {
         this.fade = Math.min(1.0F, (float)(var1 - this.timeMs) / (float)this.timeMs2);
      }
   }

   protected void savePrevious() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
   }

   protected static boolean isSolid(double px, double py, double pz) {
      if (mc.world == null) {
         return false;
      }

      BlockPos var6 = BlockPos.ofFloored(px, py, pz);
      return mc.world.getBlockState(var6).isFullCube(mc.world, var6);
   }

   protected static double groundBelow(double px, double py, double pz) {
      if (mc.world == null) {
         return Double.NaN;
      }

      int var6 = (int)Math.floor(py + 0.05);

      for (int var7 = 0; var7 <= 3; var7++) {
         BlockPos var8 = BlockPos.ofFloored(px, var6 - var7, pz);
         if (mc.world.getBlockState(var8).isFullCube(mc.world, var8)) {
            return var8.getY() + 1.0;
         }
      }

      return Double.NaN;
   }
}
