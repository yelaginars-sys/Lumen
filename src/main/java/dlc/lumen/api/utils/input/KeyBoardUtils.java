package dlc.lumen.api.utils.input;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.ui.modern.ModernGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Generated;
import org.lwjgl.glfw.GLFW;

public final class KeyBoardUtils implements QClient {
   public static final int MOUSE_BUTTON_OFFSET = 1000;
   public static final int MOUSE_SCROLL_UP = 2000;
   public static final int MOUSE_SCROLL_DOWN = 2001;
   public static final int MOUSE_BUTTON_LEFT = 0;
   public static final int MOUSE_BUTTON_RIGHT = 1;
   public static final int MOUSE_BUTTON_MIDDLE = 2;
   public static final int MOUSE_BUTTON_4 = 3;
   public static final int MOUSE_BUTTON_5 = 4;
   public static final int MOUSE_BUTTON_6 = 5;
   public static final int MOUSE_BUTTON_7 = 6;
   public static final int MOUSE_BUTTON_8 = 7;

   public static void call(int key, int action) {
      if (key > -1) {
         if (action == 1) {
            if (key == 344) {
               mc.setScreen(new ModernGui());
            }

            new EventBinding(key, EventBinding.BindType.KEYBOARD).call();
            ObjectArrayList var2 = ModuleClass.INSTANCE.getObject();
            int var3 = 0;

            for (int var4 = var2.size(); var3 < var4; var3++) {
               Module var5 = (Module)var2.get(var3);
               if (var5.getKey() == key) {
                  var5.toggle();
               }
            }

            handleKey(key);
         }
      }
   }

   private static void handleKey(int key) {
      if (key > -1) {
         ObjectArrayList var1 = ModuleClass.INSTANCE.getObject();
         int var2 = 0;

         for (int var3 = var1.size(); var2 < var3; var2++) {
            for (Setting var5 : ((Module)var1.get(var2)).getSettings()) {
               if (var5 instanceof BooleanSetting var6 && var6.getKey() == key) {
                  var6.setState(!var6.isState());
               }
            }
         }
      }
   }

   public static String getKeyName(int keyCode) {
      if (keyCode == -1) {
         return "None";
      }

      if (keyCode == 2000) {
         return "Scroll Up";
      }

      if (keyCode == 2001) {
         return "Scroll Down";
      }

      if (keyCode >= 1000) {
         return resolveButton(keyCode - 1000);
      }

      String var1 = GLFW.glfwGetKeyName(keyCode, 0);
      if (var1 != null) {
         return var1.toUpperCase();
      }

      return switch (keyCode) {
         case 32 -> "SPACE";
         default -> "KEY" + keyCode;
         case 256 -> "ESC";
         case 257 -> "ENTER";
         case 258 -> "TAB";
         case 259 -> "BACKSPACE";
         case 260 -> "INSERT";
         case 261 -> "DELETE";
         case 262 -> "RIGHT";
         case 263 -> "LEFT";
         case 264 -> "DOWN";
         case 265 -> "UP";
         case 266 -> "PAGEUP";
         case 267 -> "PAGEDOWN";
         case 268 -> "HOME";
         case 269 -> "END";
         case 280 -> "CAPS";
         case 281 -> "SCROLL";
         case 282 -> "NUMLOCK";
         case 283 -> "PRTSC";
         case 284 -> "PAUSE";
         case 290 -> "F1";
         case 291 -> "F2";
         case 292 -> "F3";
         case 293 -> "F4";
         case 294 -> "F5";
         case 295 -> "F6";
         case 296 -> "F7";
         case 297 -> "F8";
         case 298 -> "F9";
         case 299 -> "F10";
         case 300 -> "F11";
         case 301 -> "F12";
         case 302 -> "F13";
         case 303 -> "F14";
         case 304 -> "F15";
         case 305 -> "F16";
         case 306 -> "F17";
         case 307 -> "F18";
         case 308 -> "F19";
         case 309 -> "F20";
         case 310 -> "F21";
         case 311 -> "F22";
         case 312 -> "F23";
         case 313 -> "F24";
         case 314 -> "F25";
         case 320 -> "NUM0";
         case 321 -> "NUM1";
         case 322 -> "NUM2";
         case 323 -> "NUM3";
         case 324 -> "NUM4";
         case 325 -> "NUM5";
         case 326 -> "NUM6";
         case 327 -> "NUM7";
         case 328 -> "NUM8";
         case 329 -> "NUM9";
         case 330 -> "NUM.";
         case 331 -> "NUM/";
         case 332 -> "NUM*";
         case 333 -> "NUM-";
         case 334 -> "NUM+";
         case 335 -> "NUMENTER";
         case 336 -> "NUM=";
         case 340 -> "LSHIFT";
         case 341 -> "LCTRL";
         case 342 -> "LALT";
         case 343 -> "LWIN";
         case 344 -> "RSHIFT";
         case 345 -> "RCTRL";
         case 346 -> "RALT";
         case 347 -> "RWIN";
         case 348 -> "MENU";
      };
   }

   public static void callMouse(int button, int action) {
      if (mc.currentScreen == null) {
         if (button >= 0) {
            if (action == 1) {
               int var2 = 1000 + button;
               new EventBinding(var2, EventBinding.BindType.MOUSE).call();
               ObjectArrayList var3 = ModuleClass.INSTANCE.getObject();
               int var4 = 0;

               for (int var5 = var3.size(); var4 < var5; var4++) {
                  Module var6 = (Module)var3.get(var4);
                  if (var6.getKey() == var2) {
                     var6.toggle();
                  }
               }

               handleKey(var2);
            }
         }
      }
   }

   public static void callScroll(double scrollY) {
      if (mc.currentScreen == null) {
         int var2 = scrollY > 0.0 ? 2000 : 2001;
         new EventBinding(var2, EventBinding.BindType.MOUSE).call();
         ObjectArrayList var3 = ModuleClass.INSTANCE.getObject();
         int var4 = 0;

         for (int var5 = var3.size(); var4 < var5; var4++) {
            Module var6 = (Module)var3.get(var4);
            if (var6.getKey() == var2) {
               var6.toggle();
            }
         }

         handleKey(var2);
      }
   }

   public static boolean isBindHeld(int key) {
      if (key == -1) {
         return false;
      } else {
         long var1 = mc.getWindow().getHandle();
         if (key == 2000 || key == 2001) {
            return false;
         } else if (key >= 1000) {
            int var3 = key - 1000;
            return var3 <= 7 ? GLFW.glfwGetMouseButton(var1, var3) == 1 : false;
         } else {
            return GLFW.glfwGetKey(var1, key) == 1;
         }
      }
   }

   public static boolean isBindPressed(int key) {
      return isBindHeld(key);
   }

   public static String getBindName(int key) {
      if (key == -1) {
         return "n/a";
      } else if (key == 2000) {
         return "Scroll Up";
      } else if (key == 2001) {
         return "Scroll Down";
      } else {
         return key >= 1000 ? resolveButton(key - 1000) : resolveKey(key);
      }
   }

   private static String resolveButton(int button) {
      return switch (button) {
         case 0 -> "ЛКМ";
         case 1 -> "ПКМ";
         case 2 -> "СКМ";
         case 3 -> "MOUSE4";
         case 4 -> "MOUSE5";
         case 5 -> "MOUSE6";
         case 6 -> "MOUSE7";
         case 7 -> "MOUSE8";
         default -> "MOUSE" + (button + 1);
      };
   }

   private static String resolveKey(int key) {
      if (key >= 65 && key <= 90) {
         return String.valueOf((char)(65 + (key - 65)));
      }

      if (key >= 48 && key <= 57) {
         return String.valueOf((char)(48 + (key - 48)));
      }

      String var1 = switch (key) {
         case 39 -> "'";
         case 44 -> ",";
         case 45 -> "-";
         case 46 -> ".";
         case 47 -> "/";
         case 59 -> ";";
         case 61 -> "=";
         case 91 -> "[";
         case 92 -> "\\";
         case 93 -> "]";
         case 96 -> "`";
         default -> null;
      };
      if (var1 != null) {
         return var1;
      }

      return switch (key) {
         case 32 -> "SPACE";
         default -> "KEY" + key;
         case 256 -> "ESC";
         case 257 -> "ENTER";
         case 258 -> "TAB";
         case 259 -> "BACKSPACE";
         case 260 -> "INSERT";
         case 261 -> "DELETE";
         case 262 -> "RIGHT";
         case 263 -> "LEFT";
         case 264 -> "DOWN";
         case 265 -> "UP";
         case 266 -> "PAGEUP";
         case 267 -> "PAGEDOWN";
         case 268 -> "HOME";
         case 269 -> "END";
         case 280 -> "CAPS";
         case 281 -> "SCROLL";
         case 282 -> "NUMLOCK";
         case 283 -> "PRTSC";
         case 284 -> "PAUSE";
         case 290 -> "F1";
         case 291 -> "F2";
         case 292 -> "F3";
         case 293 -> "F4";
         case 294 -> "F5";
         case 295 -> "F6";
         case 296 -> "F7";
         case 297 -> "F8";
         case 298 -> "F9";
         case 299 -> "F10";
         case 300 -> "F11";
         case 301 -> "F12";
         case 302 -> "F13";
         case 303 -> "F14";
         case 304 -> "F15";
         case 305 -> "F16";
         case 306 -> "F17";
         case 307 -> "F18";
         case 308 -> "F19";
         case 309 -> "F20";
         case 310 -> "F21";
         case 311 -> "F22";
         case 312 -> "F23";
         case 313 -> "F24";
         case 314 -> "F25";
         case 320 -> "NUM0";
         case 321 -> "NUM1";
         case 322 -> "NUM2";
         case 323 -> "NUM3";
         case 324 -> "NUM4";
         case 325 -> "NUM5";
         case 326 -> "NUM6";
         case 327 -> "NUM7";
         case 328 -> "NUM8";
         case 329 -> "NUM9";
         case 330 -> "NUM.";
         case 331 -> "NUM/";
         case 332 -> "NUM*";
         case 333 -> "NUM-";
         case 334 -> "NUM+";
         case 335 -> "NUMENTER";
         case 336 -> "NUM=";
         case 340 -> "LSHIFT";
         case 341 -> "LCTRL";
         case 342 -> "LALT";
         case 343 -> "LWIN";
         case 344 -> "RSHIFT";
         case 345 -> "RCTRL";
         case 346 -> "RALT";
         case 347 -> "RWIN";
         case 348 -> "MENU";
      };
   }

   public static boolean isMouseButton(int key) {
      return key >= 1000 || key == 2000 || key == 2001;
   }

   public static boolean isScrollButton(int key) {
      return key == 2000 || key == 2001;
   }

   public static int getMouseButtonFromKey(int key) {
      return key >= 1000 ? key - 1000 : -1;
   }

   public static int createMouseBind(int mouseButton) {
      return 1000 + mouseButton;
   }

   public static int createScrollBind(boolean up) {
      return up ? 2000 : 2001;
   }

   @Generated
   private KeyBoardUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
