package dlc.lumen.api.utils.baritone;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public final class BaritoneControl {
   private static final String text = "baritone.api.BaritoneAPI";
   private static String text2 = "";
   private static String text3 = "";

   private BaritoneControl() {
   }

   private static void handleCommand(String command) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.getNetworkHandler() != null) {
         var1.getNetworkHandler().sendChatMessage(command);
      }
   }

   public static void gotoPos(BlockPos pos) {
      handleCommand("#goto " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
   }

   public static void gotoXZ(int x, int z) {
      handleCommand("#goto " + x + " " + z);
   }

   public static void mine(String blockId) {
      handleCommand("#mine " + blockId);
   }

   public static void raw(String baritoneCommand) {
      handleCommand("#" + baritoneCommand);
   }

   public static void clearArea(int x1, int y1, int z1, int x2, int y2, int z2) {
      raw("sel clear");
      raw("sel pos1 " + x1 + " " + y1 + " " + z1);
      raw("sel pos2 " + x2 + " " + y2 + " " + z2);
      raw("sel cleararea");
   }

   public static void stop() {
      handleCommand("#stop");
   }

   public static Object getSetting(String name) {
      try {
         Object var1 = getName(name);
         return var1 == null ? null : var1.getClass().getField("value").get(var1);
      } catch (Throwable var2) {
         return null;
      }
   }

   public static boolean setSetting(String name, Object value) {
      try {
         Object var2 = getName(name);
         if (var2 == null) {
            text2 = "settings/setting '" + name + "' == null (Baritone не готов?)";
            return false;
         } else {
            var2.getClass().getField("value").set(var2, value);
            text2 = "";
            return true;
         }
      } catch (Throwable var3) {
         text2 = var3.getClass().getSimpleName() + ": " + var3.getMessage();
         return false;
      }
   }

   public static String getLastSettingError() {
      return text2;
   }

   public static boolean forceBoolSetting(String name, boolean value) {
      setSetting(name, value);
      if (Boolean.valueOf(value).equals(getSetting(name))) {
         return true;
      }

      raw("set " + name + " " + value);
      return false;
   }

   private static Object getName(String name) throws ReflectiveOperationException {
      Class var1 = Class.forName("baritone.api.BaritoneAPI");
      Object var2 = var1.getMethod("getSettings").invoke(null);
      return var2 == null ? null : var2.getClass().getField(name).get(var2);
   }

   public static boolean isPathing() {
      try {
         Object var0 = getObject();
         if (var0 == null) {
            return false;
         }

         Object var1 = var0.getClass().getMethod("getPathingBehavior").invoke(var0);
         Object var2 = var1.getClass().getMethod("isPathing").invoke(var1);
         return Boolean.TRUE.equals(var2);
      } catch (Throwable var3) {
         return false;
      }
   }

   public static boolean isMining() {
      try {
         Object var0 = getObject();
         if (var0 == null) {
            return false;
         }

         Object var1 = var0.getClass().getMethod("getMineProcess").invoke(var0);
         Object var2 = var1.getClass().getMethod("isActive").invoke(var1);
         return Boolean.TRUE.equals(var2);
      } catch (Throwable var3) {
         return false;
      }
   }

   public static boolean isBusy() {
      return isPathing() || isMining();
   }

   public static boolean isPresent() {
      try {
         if (FabricLoader.getInstance().isModLoaded("baritone")) {
            return true;
         }
      } catch (Throwable var3) {
      }

      try {
         Class.forName("baritone.api.BaritoneAPI");
         return true;
      } catch (Throwable var2) {
         try {
            Class.forName("baritone.api.IBaritoneProvider");
            return true;
         } catch (Throwable var1) {
            return false;
         }
      }
   }

   public static void setSettingByCommand(String name, Object value) {
      raw("set " + name + " " + value);
   }

   public static void resetSettingByCommand(String name) {
      raw("set reset " + name);
   }

   public static boolean isAvailable() {
      try {
         if (getObject() != null) {
            text3 = "";
            return true;
         } else {
            text3 = "провайдер вернул null (Baritone ещё не инициализирован?)";
            return false;
         }
      } catch (ClassNotFoundException var1) {
         text3 = "классы API не найдены — мод Baritone не загружен (" + var1.getMessage() + ")";
         return false;
      } catch (Throwable var2) {
         text3 = var2.getClass().getSimpleName() + ": " + var2.getMessage();
         return false;
      }
   }

   public static String getLastError() {
      return text3;
   }

   public static boolean executeCommand(String command) {
      try {
         Object var1 = getObject();
         if (var1 == null) {
            return false;
         }

         Object var2 = var1.getClass().getMethod("getCommandManager").invoke(var1);
         if (var2 == null) {
            return false;
         }

         var2.getClass().getMethod("execute", String.class).invoke(var2, command);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   private static Object getObject() throws ReflectiveOperationException {
      Class var0 = Class.forName("baritone.api.BaritoneAPI");
      Object var1 = var0.getMethod("getProvider").invoke(null);
      return var1 == null ? null : var1.getClass().getMethod("getPrimaryBaritone").invoke(var1);
   }
}
