package dlc.lumen.api.utils.render.particles;

import net.minecraft.util.math.Vec3d;

public class HitParticle extends ParticleBase {
   private static final long timeMs = 150L;
   private static final long timeMs2 = 250L;
   private float gravity = 0.04F;
   private float damping = 0.98F;
   private boolean collide = true;
   private boolean spin = true;
   private int count;

   public HitParticle(Vec3d position, Vec3d velocity, int color, float size, float lifeSeconds) {
      super(position.x, position.y, position.z, color, size, (long)(lifeSeconds * 1000.0F), 150L, 250L);
      this.vx = velocity.x;
      this.vy = velocity.y;
      this.vz = velocity.z;
   }

   public HitParticle gravity(float gravity) {
      this.gravity = gravity;
      return this;
   }

   public HitParticle damping(float damping) {
      this.damping = damping;
      return this;
   }

   public HitParticle collide(boolean collide) {
      this.collide = collide;
      return this;
   }

   public HitParticle spin(boolean spin) {
      this.spin = spin;
      return this;
   }

   public HitParticle delay(int ticks) {
      this.count = Math.max(0, ticks);
      return this;
   }

   public HitParticle shape(ParticleShape shape) {
      this.shape = shape.resolve();
      return this;
   }

   @Override
   public void tick() {
      this.savePrevious();
      if (this.count > 0) {
         this.count--;
      } else {
         this.vy = this.vy - this.gravity;
         if (this.collide && mc.world != null) {
            if (isSolid(this.x + this.vx, this.y, this.z)) {
               this.vx *= -0.8;
            } else {
               this.x = this.x + this.vx;
            }

            if (isSolid(this.x, this.y + this.vy, this.z)) {
               this.vx *= 0.82;
               this.vz *= 0.82;
               this.vy *= -0.62;
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

         this.vx = this.vx * this.damping;
         this.vz = this.vz * this.damping;
      }

      if (this.spin) {
         this.roll += 2.0F;
      }

      this.updateFade();
   }
}
