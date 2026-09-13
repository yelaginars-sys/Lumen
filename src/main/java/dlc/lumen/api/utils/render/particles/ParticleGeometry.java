package dlc.lumen.api.utils.render.particles;

import net.minecraft.util.Identifier;

public final class ParticleGeometry {
   public static final Identifier BLOOM = getBloom("bloom");
   private static final Identifier id = getBloom("star");
   private static final Identifier id2 = getBloom("dollar");
   private static final Identifier id3 = getBloom("heart");
   private static final Identifier id4 = getBloom("snowflake");
   private static final Identifier id5 = getBloom("sparkle");
   public static final Identifier[] TEXTURES = new Identifier[]{id, id2, id3, id4, id5};
   private static final float[] fvalueArray = new float[]{-0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F};
   private static final float[] fvalueArray2 = new float[]{-0.09F, -0.5F, -0.09F, 0.5F, 0.09F, 0.5F, 0.09F, -0.5F};
   private static final float[] fvalueArray3 = new float[]{0.0F, -0.5F, -0.5F, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F};
   private static final float[] fvalueArray4 = new float[]{0.0F, 0.5F, -0.5F, -0.4F, 0.5F, -0.4F, 0.5F, -0.4F};
   private static final float[] fvalueArray5 = new float[]{
      0.0F, -0.5F, -0.13F, 0.0F, 0.0F, 0.5F, 0.13F, 0.0F, -0.5F, 0.0F, 0.0F, 0.13F, 0.5F, 0.0F, 0.0F, -0.13F
   };
   private static final float[] fvalueArray6 = new float[]{
      0.42F, 0.5F, 0.1F, 0.5F, -0.3F, 0.02F, 0.02F, 0.02F, 0.3F, -0.02F, -0.02F, -0.02F, -0.42F, -0.5F, -0.1F, -0.5F
   };
   private static final float[] fvalueArray7 = getCenterX(
      0.0F, -0.15F, new float[]{-0.45F, -0.35F, 0.45F, -0.35F, 0.45F, 0.35F, 0.22F, 0.02F, 0.0F, 0.45F, -0.22F, 0.02F, -0.45F, 0.35F}
   );

   private ParticleGeometry() {
   }

   public static Identifier texture(ParticleShape shape) {
      return switch (shape) {
         case STAR -> id;
         case DOLLAR -> id2;
         case HEART -> id3;
         case SNOWFLAKE -> id4;
         case GLOW -> id5;
         default -> null;
      };
   }

   public static float[] polygon(ParticleShape shape) {
      return switch (shape) {
         case CUBE_BLAST -> fvalueArray;
         case LINE -> fvalueArray2;
         case RHOMBUS -> fvalueArray3;
         case TRIANGLE -> fvalueArray4;
         case STAR_ALT -> fvalueArray5;
         case LIGHTNING -> fvalueArray6;
         case CROWN -> fvalueArray7;
         default -> null;
      };
   }

   private static Identifier getBloom(String name) {
      return Identifier.of("lumen", "textures/particle/" + name + ".png");
   }

   private static float[] getCenterX(float centerX, float centerY, float[] points) {
      int var3 = points.length / 2;
      float[] var4 = new float[var3 * 8];

      for (int var5 = 0; var5 < var3; var5++) {
         int var6 = (var5 + 1) % var3;
         int var7 = var5 * 8;
         var4[var7] = centerX;
         var4[var7 + 1] = centerY;
         var4[var7 + 2] = points[var5 * 2];
         var4[var7 + 3] = points[var5 * 2 + 1];
         var4[var7 + 4] = points[var6 * 2];
         var4[var7 + 5] = points[var6 * 2 + 1];
         var4[var7 + 6] = points[var6 * 2];
         var4[var7 + 7] = points[var6 * 2 + 1];
      }

      return var4;
   }
}
