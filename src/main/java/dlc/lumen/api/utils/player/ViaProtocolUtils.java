package dlc.lumen.api.utils.player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ViaProtocolUtils {
   private static final int count = 759;
   private static final long timeMs = 1500L;
   private static final Pattern pattern = Pattern.compile("1\\.(\\d+)");
   private static long timeMs2;
   private static boolean flag;

   private ViaProtocolUtils() {
   }

   public static boolean isTargetProtocolBelowOneNineteen() {
      long var0 = System.currentTimeMillis();
      if (var0 < timeMs2) {
         return flag;
      }

      flag = checkState();
      timeMs2 = var0 + 1500L;
      return flag;
   }

   private static boolean checkState() {
      try {
         Class var0 = Class.forName("com.viaversion.viafabricplus.ViaFabricPlus");
         Object var1 = var0.getMethod("getImpl").invoke(null);
         if (var1 == null) {
            return false;
         }

         Object var2 = getInstance(var1, "getTargetVersion");
         if (var2 == null) {
            return false;
         }

         Integer var3 = getTargetVersion(var2);
         return var3 != null && var3 < 759;
      } catch (Throwable var4) {
         return false;
      }
   }

   private static Object getInstance(Object instance, String methodName) {
      try {
         Method var2 = instance.getClass().getMethod(methodName);
         return Modifier.isPublic(var2.getModifiers()) && var2.getParameterCount() == 0 ? var2.invoke(instance) : null;
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Integer getTargetVersion(Object targetVersion) {
      try {
         Method var1 = targetVersion.getClass().getMethod("getVersion");
         if (var1.invoke(targetVersion) instanceof Number var15) {
            return var15.intValue();
         }
      } catch (Throwable var9) {
      }

      try {
         for (Method var4 : targetVersion.getClass().getMethods()) {
            if (Modifier.isPublic(var4.getModifiers()) && var4.getParameterCount() == 0) {
               Class var5 = var4.getReturnType();
               if (var5 == int.class || var5 == Integer.class) {
                  String var6 = var4.getName().toLowerCase();
                  if ((var6.contains("version") || var6.contains("protocol") || var6.contains("id")) && var4.invoke(targetVersion) instanceof Number var8) {
                     return var8.intValue();
                  }
               }
            }
         }
      } catch (Throwable var10) {
      }

      Matcher var12 = pattern.matcher(String.valueOf(targetVersion));
      if (var12.find()) {
         int var14 = Integer.parseInt(var12.group(1));
         return var14 >= 19 ? 759 : 758;
      } else {
         return null;
      }
   }
}
