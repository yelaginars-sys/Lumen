package dlc.lumen.client.render.figura;

import java.lang.reflect.Field;

public final class FiguraBotGuard {
   private static final String AVATAR_MANAGER_CLASS_NAME = "org.figuramc.figura.avatar.AvatarManager";
   private static Field field;
   private static boolean flag;
   private static boolean flag2;

   private FiguraBotGuard() {
   }

   public static void suspend() {
      Field var0 = helper();
      if (var0 != null) {
         try {
            flag2 = var0.getBoolean(null);
            if (!flag2) {
               var0.setBoolean(null, true);
            }
         } catch (Throwable var2) {
         }
      }
   }

   public static void resume() {
      Field var0 = helper();
      if (var0 != null) {
         try {
            var0.setBoolean(null, flag2);
         } catch (Throwable var2) {
         }
      }
   }

   private static Field helper() {
      if (flag) {
         return field;
      }

      flag = true;

      try {
         Class var0 = Class.forName("org.figuramc.figura.avatar.AvatarManager", false, FiguraBotGuard.class.getClassLoader());
         field = var0.getField("panic");
      } catch (Throwable var1) {
         field = null;
      }

      return field;
   }
}
