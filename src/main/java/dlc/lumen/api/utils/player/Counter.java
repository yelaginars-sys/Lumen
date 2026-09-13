package dlc.lumen.api.utils.player;

import dlc.lumen.api.QClient;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public final class Counter implements QClient {
   private static int count;

   public static void updateFPS() {
      int var0 = mc.getCurrentFps();
      count = MathHelper.lerp(0.5F, var0, count);
   }

   @Generated
   private Counter() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static int getCurrentFPS() {
      return count;
   }
}
