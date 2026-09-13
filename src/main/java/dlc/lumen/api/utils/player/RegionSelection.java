package dlc.lumen.api.utils.player;

import lombok.Generated;
import net.minecraft.util.math.BlockPos;

public final class RegionSelection {
   private static BlockPos pos;
   private static BlockPos pos2;

   public static void setFirst(BlockPos pos) {
      pos = pos;
   }

   public static void setSecond(BlockPos pos) {
      pos2 = pos;
   }

   public static BlockPos getFirst() {
      return pos;
   }

   public static BlockPos getSecond() {
      return pos2;
   }

   public static void clear() {
      pos = null;
      pos2 = null;
   }

   public static boolean isComplete() {
      return pos != null && pos2 != null;
   }

   public static boolean contains(BlockPos pos) {
      return !isComplete()
         ? false
         : pos.getX() >= Math.min(pos.getX(), pos2.getX())
            && pos.getX() <= Math.max(pos.getX(), pos2.getX())
            && pos.getY() >= Math.min(pos.getY(), pos2.getY())
            && pos.getY() <= Math.max(pos.getY(), pos2.getY())
            && pos.getZ() >= Math.min(pos.getZ(), pos2.getZ())
            && pos.getZ() <= Math.max(pos.getZ(), pos2.getZ());
   }

   public static int minX() {
      return Math.min(pos.getX(), pos2.getX());
   }

   public static int maxX() {
      return Math.max(pos.getX(), pos2.getX());
   }

   public static int minY() {
      return Math.min(pos.getY(), pos2.getY());
   }

   public static int maxY() {
      return Math.max(pos.getY(), pos2.getY());
   }

   public static int minZ() {
      return Math.min(pos.getZ(), pos2.getZ());
   }

   public static int maxZ() {
      return Math.max(pos.getZ(), pos2.getZ());
   }

   @Generated
   private RegionSelection() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
