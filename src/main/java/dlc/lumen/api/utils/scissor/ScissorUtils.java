package dlc.lumen.api.utils.scissor;

import com.google.common.collect.Lists;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL30;

public class ScissorUtils {
   private static ScissorUtils.State state = new ScissorUtils.State();
   private static final List<ScissorUtils.State> stateList = Lists.newArrayList();
   private static float fvalue = 1.0F;
   private static float fvalue2 = 0.0F;
   private static float fvalue3 = 0.0F;

   public static void setViewTransform(float scale, float pivotX, float pivotY) {
      fvalue = scale;
      fvalue2 = pivotX;
      fvalue3 = pivotY;
   }

   public static void resetViewTransform() {
      fvalue = 1.0F;
      fvalue2 = 0.0F;
      fvalue3 = 0.0F;
   }

   public static void push() {
      stateList.add(state.clone());
   }

   public static void pop() {
      if (!stateList.isEmpty()) {
         state = stateList.remove(stateList.size() - 1);
         if (state.enabled) {
            GL30.glEnable(3089);
            GL30.glScissor(state.x, state.y, state.width, state.height);
         } else {
            GL30.glDisable(3089);
         }
      }
   }

   public static void unset() {
      GL30.glDisable(3089);
      state.enabled = false;
   }

   public static void reapply() {
      if (state.enabled) {
         GL30.glEnable(3089);
         GL30.glScissor(state.x, state.y, state.width, state.height);
      } else {
         GL30.glDisable(3089);
      }
   }

   private static Window getWindow() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 == null ? null : var0.getWindow();
   }

   private static double calcValue() {
      Window var0 = getWindow();
      return var0 == null ? 1.0 : var0.getScaleFactor();
   }

   public static void setFromComponentCoordinates(int x, int y, int width, int height) {
      setFromComponentCoordinates((double)x, (double)y, (double)width, (double)height);
   }

   public static void setFromComponentCoordinates(double x, double y, double width, double height) {
      Window var8 = getWindow();
      if (var8 != null) {
         double var9 = calcValue();
         double var11 = fvalue2 + (x - fvalue2) * fvalue;
         double var13 = fvalue3 + (y - fvalue3) * fvalue;
         double var15 = width * fvalue;
         double var17 = height * fvalue;
         int var19 = (int)(var11 * var9);
         int var20 = (int)(var13 * var9);
         int var21 = (int)(var15 * var9);
         int var22 = (int)(var17 * var9);
         var20 = var8.getHeight() - var20 - var22;
         set(var19, var20, var21, var22);
      }
   }

   public static void setFromComponentCoordinates(double x, double y, double width, double height, float scale) {
      Window var9 = getWindow();
      if (var9 != null) {
         double var10 = calcValue();
         float var12 = scale;
         float var13 = (1.0F - var12) / 2.0F;
         double var14 = x + width * var13;
         double var16 = y + height * var13;
         double var18 = width * var12;
         double var20 = height * var12;
         var14 = var14 * var12 + (var9.getScaledWidth() - var18) * var13;
         int var22 = (int)(var14 * var10);
         int var23 = (int)(var16 * var10);
         int var24 = (int)(var18 * var10);
         int var25 = (int)(var20 * var10);
         var23 = var9.getHeight() - var23 - var25;
         set(var22, var23, var24, var25);
      }
   }

   public static void set(int x, int y, int width, int height) {
      Window var4 = getWindow();
      if (var4 != null) {
         Rectangle var5 = new Rectangle(0, 0, var4.getWidth(), var4.getHeight());
         Rectangle var6;
         if (state.enabled) {
            var6 = new Rectangle(state.x, state.y, state.width, state.height);
         } else {
            var6 = var5;
         }

         Rectangle var7 = new Rectangle(x + state.transX, y + state.transY, width, height);
         Rectangle var8 = var6.intersection(var7);
         var8 = var8.intersection(var5);
         if (var8.width < 0) {
            var8.width = 0;
         }

         if (var8.height < 0) {
            var8.height = 0;
         }

         state.enabled = true;
         state.x = var8.x;
         state.y = var8.y;
         state.width = var8.width;
         state.height = var8.height;
         GL30.glEnable(3089);
         GL30.glScissor(var8.x, var8.y, var8.width, var8.height);
      }
   }

   public static void translate(int x, int y) {
      state.transX = x;
      state.transY = y;
   }

   public static void translateFromComponentCoordinates(int x, int y) {
      Window var2 = getWindow();
      if (var2 != null) {
         int var3 = var2.getScaledHeight();
         double var4 = calcValue();
         int var6 = (int)(x * var4);
         int var7 = (int)(y * var4);
         var7 = (int)(var3 * var4) - var7;
         translate(var6, var7);
      }
   }

   private static class State implements Cloneable {
      public boolean enabled;
      public int transX;
      public int transY;
      public int x;
      public int y;
      public int width;
      public int height;

      public ScissorUtils.State clone() {
         try {
            return (ScissorUtils.State)super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new AssertionError(var2);
         }
      }
   }
}
