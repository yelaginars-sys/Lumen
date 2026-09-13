package dlc.lumen.client.ui.modern;

import java.util.HashMap;
import java.util.Map;

public final class ModernAnim {
   private static final Map<Object, float[]> OBJECTS = new HashMap<>();
   private static long timestamp = System.nanoTime();
   private static float volume = 0.016666668F;

   private ModernAnim() {
   }

   public static void beginFrame() {
      long var0 = System.nanoTime();
      float var2 = (float)(var0 - timestamp) / 1.0E9F;
      timestamp = var0;
      volume = Math.max(1.0E-4F, Math.min(0.1F, var2));
   }

   public static float delta() {
      return volume;
   }

   public static float approach(float current, float target, float speed) {
      if (!ModernTheme.animations()) {
         return target;
      }

      float var3 = 1.0F - (float)Math.exp(-speed * volume);
      float var4 = current + (target - current) * var3;
      return Math.abs(target - var4) < 5.0E-4F ? target : var4;
   }

   public static float value(Object key, float target, float speed) {
      float[] var3 = OBJECTS.get(key);
      if (var3 == null) {
         var3 = new float[]{target};
         OBJECTS.put(key, var3);
         return target;
      } else {
         var3[0] = approach(var3[0], target, speed);
         return var3[0];
      }
   }

   public static void set(Object key, float value) {
      OBJECTS.computeIfAbsent(key, k -> new float[1])[0] = value;
   }

   public static float get(Object key, float fallback) {
      float[] var2 = OBJECTS.get(key);
      return var2 == null ? fallback : var2[0];
   }

   public static void resetPrefix(String prefix) {
      OBJECTS.keySet().removeIf(key -> key instanceof String var2 && var2.startsWith(prefix));
   }

   public static float ease(float t) {
      float var1 = Math.max(0.0F, Math.min(1.0F, t));
      return 1.0F - (float)Math.pow(1.0F - var1, 3.0);
   }
}
