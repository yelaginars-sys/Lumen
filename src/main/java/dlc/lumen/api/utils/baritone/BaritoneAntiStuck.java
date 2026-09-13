package dlc.lumen.api.utils.baritone;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class BaritoneAntiStuck {
   private static final String text = "Извините, но вы не можете сломать блок здесь";
   private static final long timeMs = 7000L;
   private static final double dvalue = 1.0;
   private static final int count = 12;
   private static final double dvalue2 = 2500.0;
   private static final long timeMs2 = 25000L;
   private static final double dvalue3 = 0.95;
   private static final double dvalue4 = 0.35;
   private static final String text2 = "baritone.api.BaritoneAPI";
   private static final String text3 = "baritone.api.utils.input.Input";
   private static Vec3d vec;
   private static long timeMs3;
   private static int count2;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static boolean flag4;
   private static Vec3d vec2;
   private static long timeMs4;

   private BaritoneAntiStuck() {
   }

   public static void onGameMessage(String message) {
      if (message != null && message.contains("Извините, но вы не можете сломать блок здесь")) {
         flag2 = true;
      }
   }

   public static void tick() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         try {
            Object var1 = getObject();
            if (var1 == null) {
               handleAction2();
               return;
            }

            Object var2 = getTarget(var1, "getPathingBehavior");
            Object var3 = getTarget(var1, "getInputOverrideHandler");
            if (var2 == null || var3 == null || !Boolean.TRUE.equals(getTarget(var2, "isPathing"))) {
               handleInput(var3);
               handleAction();
               return;
            }

            long var4 = System.currentTimeMillis();
            Vec3d var6 = var0.player.getPos();
            if (vec == null) {
               vec = var6;
               timeMs3 = var4;
            }

            if (flag2 && checkMc(var0, var3)) {
               handleMc(var0, var6);
               flag2 = false;
            }

            if (flag3) {
               if (!(var6.squaredDistanceTo(vec2) >= 2500.0) && var4 - timeMs4 < 25000L) {
                  handleMc3(var0, var3);
                  vec = var6;
                  timeMs3 = var4;
                  return;
               }

               handleInputOverrideHandler2(var3);
               flag3 = false;
               vec = var6;
               timeMs3 = var4;
               return;
            }

            if (count2 > 0) {
               handleMc2(var0, var3);
               count2--;
               if (count2 <= 0) {
                  handleInputOverrideHandler2(var3);
                  vec = var0.player.getPos();
                  timeMs3 = var4;
               }

               return;
            }

            if (checkMc(var0, var3)) {
               vec = var6;
               timeMs3 = var4;
               return;
            }

            if (!checkInput(var3)) {
               vec = var6;
               timeMs3 = var4;
               return;
            }

            if (var6.squaredDistanceTo(vec) >= 1.0) {
               vec = var6;
               timeMs3 = var4;
               return;
            }

            if (var4 - timeMs3 < 7000L) {
               return;
            }

            count2 = 12;
            flag = checkMc2(var0, flag, true);
            handleMc2(var0, var3);
            vec = var6;
            timeMs3 = var4;
         } catch (Throwable var7) {
            handleAction2();
         }
      } else {
         handleAction2();
      }
   }

   private static Object getObject() throws ReflectiveOperationException {
      Class var0 = Class.forName("baritone.api.BaritoneAPI");
      Object var1 = var0.getMethod("getProvider").invoke(null);
      return var1 == null ? null : var1.getClass().getMethod("getPrimaryBaritone").invoke(var1);
   }

   private static boolean checkMc(MinecraftClient mc, Object input) throws ReflectiveOperationException {
      return mc.interactionManager != null && mc.interactionManager.isBreakingBlock() || checkInputOverrideHandler(input, "CLICK_LEFT");
   }

   private static boolean checkInput(Object input) throws ReflectiveOperationException {
      return checkInputOverrideHandler(input, "MOVE_FORWARD")
         || checkInputOverrideHandler(input, "MOVE_BACK")
         || checkInputOverrideHandler(input, "MOVE_LEFT")
         || checkInputOverrideHandler(input, "MOVE_RIGHT")
         || checkInputOverrideHandler(input, "JUMP");
   }

   private static void handleMc(MinecraftClient mc, Vec3d currentPos) {
      flag3 = true;
      vec2 = currentPos;
      timeMs4 = System.currentTimeMillis();
      flag4 = checkMc2(mc, flag4, false);
   }

   private static void handleMc2(MinecraftClient mc, Object input) throws ReflectiveOperationException {
      handleInputOverrideHandler2(input);
      handleInputOverrideHandler(input, "MOVE_FORWARD", true);
      handleInputOverrideHandler(input, flag ? "MOVE_RIGHT" : "MOVE_LEFT", true);
      if (mc.player != null && mc.player.isOnGround()) {
         handleInputOverrideHandler(input, "JUMP", true);
      }
   }

   private static void handleMc3(MinecraftClient mc, Object input) throws ReflectiveOperationException {
      handleInputOverrideHandler2(input);
      handleInputOverrideHandler(input, "MOVE_BACK", true);
      handleInputOverrideHandler(input, flag4 ? "MOVE_RIGHT" : "MOVE_LEFT", true);
      if (mc.player != null && mc.player.isOnGround()) {
         handleInputOverrideHandler(input, "JUMP", true);
      }
   }

   private static boolean checkMc2(MinecraftClient mc, boolean fallbackRight, boolean moveForward) {
      if (mc.player == null) {
         return fallbackRight;
      }

      double var3 = Math.toRadians(mc.player.getYaw());
      Vec3d var5 = new Vec3d(-MathHelper.sin((float)var3), 0.0, MathHelper.cos((float)var3));
      Vec3d var6 = new Vec3d(var5.z, 0.0, -var5.x);
      Vec3d var7 = var6.multiply(-1.0);
      Vec3d var8 = moveForward ? var5 : var5.multiply(-1.0);
      double var9 = calcMc(mc, var6.multiply(0.95).add(var8.multiply(0.35)));
      double var11 = calcMc(mc, var7.multiply(0.95).add(var8.multiply(0.35)));
      return var9 == var11 ? fallbackRight : var11 > var9;
   }

   private static double calcMc(MinecraftClient mc, Vec3d offset) {
      Box var2 = mc.player.getBoundingBox().offset(offset);
      double var3 = 0.0;
      if (mc.world.isSpaceEmpty(mc.player, var2)) {
         var3++;
      }

      if (mc.world.isSpaceEmpty(mc.player, var2.offset(0.0, 1.0, 0.0))) {
         var3 += 0.35;
      }

      return var3;
   }

   private static void handleInput(Object input) {
      if (count2 > 0 && input != null) {
         try {
            handleInputOverrideHandler2(input);
         } catch (ReflectiveOperationException var3) {
         }
      }

      count2 = 0;
      if (flag3 && input != null) {
         try {
            handleInputOverrideHandler2(input);
         } catch (ReflectiveOperationException var2) {
         }
      }

      flag3 = false;
      flag2 = false;
   }

   private static void handleAction() {
      vec = null;
      timeMs3 = 0L;
   }

   private static void handleAction2() {
      count2 = 0;
      vec = null;
      timeMs3 = 0L;
      flag2 = false;
      flag3 = false;
      vec2 = null;
      timeMs4 = 0L;
   }

   private static boolean checkInputOverrideHandler(Object inputOverrideHandler, String inputName) throws ReflectiveOperationException {
      Object var2 = getInputName(inputName);
      Object var3 = inputOverrideHandler.getClass().getMethod("isInputForcedDown", var2.getClass()).invoke(inputOverrideHandler, var2);
      return Boolean.TRUE.equals(var3);
   }

   private static void handleInputOverrideHandler(Object inputOverrideHandler, String inputName, boolean forced) throws ReflectiveOperationException {
      Object var3 = getInputName(inputName);
      inputOverrideHandler.getClass().getMethod("setInputForceState", var3.getClass(), boolean.class).invoke(inputOverrideHandler, var3, forced);
   }

   private static void handleInputOverrideHandler2(Object inputOverrideHandler) throws ReflectiveOperationException {
      inputOverrideHandler.getClass().getMethod("clearAllKeys").invoke(inputOverrideHandler);
   }

   private static Object getInputName(String inputName) throws ReflectiveOperationException {
      Class var1 = Class.forName("baritone.api.utils.input.Input");
      return Enum.valueOf(var1.asSubclass(Enum.class), inputName);
   }

   private static Object getTarget(Object target, String methodName) throws ReflectiveOperationException {
      return target.getClass().getMethod(methodName).invoke(target);
   }
}
