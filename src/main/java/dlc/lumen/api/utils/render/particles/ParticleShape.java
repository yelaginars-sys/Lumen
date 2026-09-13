package dlc.lumen.api.utils.render.particles;

import java.util.concurrent.ThreadLocalRandom;

public enum ParticleShape {
   CUBES,
   CROWN,
   CUBE_BLAST,
   DOLLAR,
   HEART,
   LIGHTNING,
   LINE,
   RHOMBUS,
   SNOWFLAKE,
   STAR,
   STAR_ALT,
   TRIANGLE,
   GLOW,
   RANDOM;

   private static final ParticleShape[] particleShapeArray = new ParticleShape[]{
      CUBES, CROWN, CUBE_BLAST, DOLLAR, HEART, LIGHTNING, LINE, RHOMBUS, SNOWFLAKE, STAR, STAR_ALT, TRIANGLE, GLOW
   };

   public ParticleShape resolve() {
      return this == RANDOM ? particleShapeArray[ThreadLocalRandom.current().nextInt(particleShapeArray.length)] : this;
   }
}
