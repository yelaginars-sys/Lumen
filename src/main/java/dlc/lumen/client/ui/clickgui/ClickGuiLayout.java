package dlc.lumen.client.ui.clickgui;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.ActionSetting;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import java.util.ArrayList;
import java.util.List;

public final class ClickGuiLayout {
   public static final float MENU_WIDTH = 320.0F;
   public static final float MENU_HEIGHT = 243.0F;
   public static final float LEFT_PANEL_WIDTH = 115.0F;
   public static final float MODULE_COLUMN_X = 127.0F;
   public static final float MODULE_COLUMN_WIDTH = 177.0F;
   public static final float MODULE_HEIGHT = 28.0F;
   public static final float MODULE_GAP = 34.0F;
   public static final float CATEGORY_WIDTH = 95.0F;
   public static final float CATEGORY_HEIGHT = 16.0F;
   public static final float CATEGORY_GAP = 18.0F;
   public static final List<Module.ModuleCategory> MAIN_CATEGORIES = List.of(
      Module.ModuleCategory.COMBAT, Module.ModuleCategory.MOVEMENT, Module.ModuleCategory.PLAYER, Module.ModuleCategory.RENDER, Module.ModuleCategory.MISC
   );
   public static final List<Module.ModuleCategory> CATEGORY_ORDER = helper();
   public static final int CATEGORY_GROUP_BREAK = MAIN_CATEGORIES.size();
   public static final float CATEGORY_GROUP_GAP = 13.0F;
   public static final float SEARCH_X = 13.0F;
   public static final float SEARCH_Y = 13.0F;
   public static final float SEARCH_WIDTH = 90.0F;
   public static final float SEARCH_HEIGHT = 14.0F;
   public static final float WIDTH = 100.0F;
   public static final float HEIGHT = 275.0F;
   public static final float CATEGORY_PANEL_STEP = 108.0F;
   public static final float THEME_PANEL_Y = 100.0F;
   public static final float THEME_PANEL_H = 15.0F;
   public static final float THEME_BOX_SIZE = 8.0F;
   public static final float THEME_BOX_GAP = 4.0F;
   public static final float THEME_BOX_RADIUS = 2.0F;
   public static final float THEME_SIDE_PADDING = 4.0F;
   public static final float MODULE_PADDING = 3.0F;
   public static final float MODULE_HEADER_HEIGHT = 20.0F;
   public static final float INLINE_MODULE_HEADER_HEIGHT = 34.0F;
   public static final float INLINE_MODULE_VERTICAL_GAP = 7.0F;
   public static final float MODULE_INNER_WIDTH = 93.5F;
   public static final float SETTING_START_Y = 20.0F;
   public static final float SETTING_PADDING = 4.0F;
   public static final float SETTING_BOTTOM_PADDING = 3.0F;
   public static final float SETTING_LEFT = 10.0F;
   public static final float SETTING_RIGHT = 89.0F;
   public static final float SLIDER_WIDTH = 79.0F;
   public static final float TEXT_SETTING_WIDTH = 42.0F;
   public static final float CLICKABLE_WIDTH = 79.0F;
   public static final float SETTING_SIDE_PADDING = 10.0F;
   public static final float SETTING_CONTROL_MIN_LEFT = 78.0F;
   public static final float SETTING_CONTROL_WIDTH_RATIO = 0.48F;
   public static final float SETTING_VALUE_GAP = 8.0F;
   public static final float SETTING_TOGGLE_WIDTH = 18.0F;
   public static final float SETTING_TOGGLE_HEIGHT = 10.0F;
   public static final float SETTING_TOGGLE_KNOB = 7.0F;
   public static final float SETTING_PILL_HEIGHT = 10.0F;
   public static final float SETTING_PILL_MIN_WIDTH = 34.0F;
   public static final float FLOAT_SETTING_HEIGHT = 12.0F;
   public static final float MODE_DROPDOWN_ROW_HEIGHT = 11.0F;
   public static final float MODE_DROPDOWN_TOP_GAP = 3.0F;
   public static final float MODE_DROPDOWN_BOTTOM_GAP = 4.0F;
   public static final int SEARCH_MAX_CHARS = 24;
   public static final float SEARCH_GAP = 8.0F;
   public static final float SEARCH_ICON_X = 3.5F;
   public static final float SEARCH_TEXT_X = 19.0F;
   public static final float SEARCH_RIGHT_PADDING = 8.0F;

   private static List<Module.ModuleCategory> helper() {
      ArrayList var0 = new ArrayList<>(MAIN_CATEGORIES);

      for (Module.ModuleCategory var4 : Module.ModuleCategory.values()) {
         if (var4 != Module.ModuleCategory.MODELS && !MAIN_CATEGORIES.contains(var4)) {
            var0.add(var4);
         }
      }

      return List.copyOf(var0);
   }

   public static float categoryYOffset(int index) {
      return 10.0F + index * 18.0F + (index >= CATEGORY_GROUP_BREAK ? 13.0F : 0.0F);
   }

   private ClickGuiLayout() {
   }

   public static float getTotalCategoriesWidth(int categoryCount) {
      return 100.0F * categoryCount + 8.0F * (categoryCount - 1);
   }

   public static float getCategoryPanelX(float x, int index) {
      return x + index * 108.0F;
   }

   public static float getContentY(float y) {
      return y + 25.0F;
   }

   public static float getContentHeight() {
      return 245.0F;
   }

   public static float getSearchX(float x, int categoryCount) {
      return x + getTotalCategoriesWidth(categoryCount) / 2.0F - 45.0F;
   }

   public static float getSearchX(float x, int categoryCount, float searchWidth) {
      return x + getTotalCategoriesWidth(categoryCount) / 2.0F - searchWidth / 2.0F;
   }

   public static float getSearchY(float y) {
      return y + 275.0F + 8.0F;
   }

   public static boolean hasVisibleSettings(List<Setting> settings) {
      for (Setting var2 : settings) {
         if (var2 != null && var2.visible()) {
            return true;
         }
      }

      return false;
   }

   public static float calculateModeSettingHeight(ModeSetting modeSetting, float openProgress) {
      float var2 = 12.0F;
      return openProgress <= 0.001F ? var2 : var2 + (3.0F + modeSetting.getMods().size() * 11.0F + 4.0F) * openProgress;
   }

   public static float calculateListSettingHeight(ListSetting listSetting, float openProgress) {
      float var2 = 12.0F;
      if (openProgress <= 0.001F) {
         return var2;
      }

      int var3 = 0;

      for (BooleanSetting var5 : listSetting.getSettings()) {
         if (var5.visible()) {
            var3++;
         }
      }

      return var2 + (3.0F + var3 * 11.0F + 4.0F) * openProgress;
   }

   public static float calculateSettingsHeight(Module module, ClickGuiState state) {
      float var2 = 0.0F;
      List<Setting> var3 = module.getSettings();
      if (var3 != null && !var3.isEmpty()) {
         boolean var4 = false;

         for (Setting var6 : var3) {
            if (var6 != null && var6.visible()) {
               var4 = true;
               if (var6 instanceof BooleanSetting || var6 instanceof BindSetting || var6 instanceof ActionSetting) {
                  var2 += 12.0F;
               } else if (var6 instanceof TextSetting) {
                  var2 += 12.0F;
               } else if (var6 instanceof FloatSetting) {
                  var2 += 12.0F;
               } else if (var6 instanceof ModeSetting var7) {
                  var2 += calculateModeSettingHeight(var7, state.getModeDropdownProgress(var7));
               } else if (var6 instanceof ListSetting var8) {
                  var2 += calculateListSettingHeight(var8, state.getListDropdownProgress(var8));
               }
            }
         }

         if (var4) {
            var2 += 3.0F;
         }

         return var2;
      } else {
         return 0.0F;
      }
   }

   public static float getModuleHeight(Module module, float openProgress, ClickGuiState state) {
      return 20.0F + calculateSettingsHeight(module, state) * openProgress;
   }

   public static float getInlineModuleHeight(Module module, float openProgress, ClickGuiState state) {
      return 34.0F + calculateSettingsHeight(module, state) * openProgress;
   }

   public static float getInlineSettingsOriginY(float moduleY) {
      return moduleY + 14.0F;
   }

   public static float getSettingLeft(float panelX) {
      return panelX + 10.0F;
   }

   public static float getSettingRight(float panelX, float moduleWidth) {
      return panelX + moduleWidth - 10.0F;
   }

   public static float getSettingControlLeft(float panelX, float moduleWidth) {
      return Math.max(getSettingLeft(panelX) + 78.0F, panelX + moduleWidth * 0.48F);
   }

   public static float getSettingControlWidth(float panelX, float moduleWidth) {
      return Math.max(34.0F, getSettingRight(panelX, moduleWidth) - getSettingControlLeft(panelX, moduleWidth));
   }

   public static float getSliderWidth(float panelX, float moduleWidth) {
      return Math.max(34.0F, getSettingControlWidth(panelX, moduleWidth) - 28.0F);
   }

   public static float getToggleX(float panelX, float moduleWidth) {
      return getSettingRight(panelX, moduleWidth) - 18.0F;
   }
}
