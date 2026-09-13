package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;

public class ClickGuiState {
   private static final Map<Character, Character> CHARACTERS = new HashMap<>();
   private final Map<Module, Float> modules2 = new HashMap<>();
   private final Map<Module, AnimationUtils> modules3 = new HashMap<>();
   private final Map<BooleanSetting, AnimationUtils> booleanSettings = new HashMap<>();
   private final Map<BooleanSetting, AnimationUtils> booleanSettings2 = new HashMap<>();
   private final Map<FloatSetting, AnimationUtils> floatSettings = new HashMap<>();
   private final Map<FloatSetting, Double> floatSettings2 = new HashMap<>();
   private final Map<FloatSetting, Double> floatSettings3 = new HashMap<>();
   private final Map<String, AnimationUtils> strings = new HashMap<>();
   private final Map<String, AnimationUtils> strings2 = new HashMap<>();
   private final Map<String, AnimationUtils> strings3 = new HashMap<>();
   private final Map<ModeSetting, AnimationUtils> modeSettings = new HashMap<>();
   private final Map<ListSetting, AnimationUtils> listSettings = new HashMap<>();
   private final Map<String, AnimationUtils> strings4 = new HashMap<>();
   private final Map<String, Float> strings5 = new HashMap<>();
   private final Map<String, Boolean> strings6 = new HashMap<>();
   private final Map<String, Boolean> strings7 = new HashMap<>();
   private final Map<Module.ModuleCategory, Float> enumMap = new EnumMap<>(Module.ModuleCategory.class);
   private final Map<Module.ModuleCategory, AnimationUtils> enumMap2 = new EnumMap<>(Module.ModuleCategory.class);
   private final Map<Module, AnimationUtils> modules4 = new HashMap<>();
   private final Map<Module, AnimationUtils> modules5 = new HashMap<>();
   private final Map<Module, float[]> modules6 = new HashMap<>();
   private float recordedScroll = 0.0F;
   private float recordedCellWidth = 0.0F;
   private final Map<Object, Float> objects = new HashMap<>();
   private final Map<Module.ModuleCategory, List<Module>> enumMap3 = new EnumMap<>(Module.ModuleCategory.class);
   private final List<Module> allModules = new ArrayList<>();
   private final List<ModernSettingWindow> settingWindows = new LinkedList<>();
   private Module.ModuleCategory moduleCategory = Module.ModuleCategory.COMBAT;
   private final Map<Module.ModuleCategory, AnimationUtils> enumMap4 = new EnumMap<>(Module.ModuleCategory.class);
   private AnimationUtils animatedScrollValue;
   private float lastScrollDelta = 0.0F;
   private long lastScrollDelta2 = 0L;
   private float maxScrollValue = 0.0F;
   private float scrollValue2 = 0.0F;
   private AnimationUtils categoryTransitionProgress;
   private Module.ModuleCategory moduleCategory2;
   private final Map<Module, AnimationUtils> modules7 = new HashMap<>();
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 11.0F, Easings.CUBIC_OUT);
   private boolean flag;
   private int categoryTransitionDirection = 1;
   private AnimationUtils animatedBackgroundY;
   private float animatedBackgroundY2 = 0.0F;
   private final AnimationUtils animationUtils2 = new AnimationUtils(0.0F, 12.0F, Easings.CUBIC_OUT);
   private boolean flag2 = false;
   private float recordedCategoryIndex = 0.0F;
   private float dragX2;
   private float dragY2;
   private boolean dragging2;
   private boolean closing2;
   private float x2;
   private float y2;
   public static final float FIXED_MENU_WIDTH = 480.0F;
   public static final float FIXED_MENU_HEIGHT = 300.0F;
   private float menuWidth2 = 480.0F;
   private float menuHeight2 = 300.0F;
   private boolean x3;
   private boolean resizing;
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4;
   private boolean settingsPanelOpen2;
   private boolean profilePanelOpen2;
   private boolean friendsPanelOpen2;
   private boolean autosetPanelOpen2;
   private int moduleColumns2 = 2;
   private BindSetting bindingSetting2;
   private TextSetting editingTextSetting2;
   private Module bindingModule2;
   private float renderOffsetY2;
   private boolean searchActive2;
   private String searchText2 = "";
   private String text2 = "";
   private int searchCursor = 0;
   private int searchSelectionStart = 0;
   private int searchSelectionStart2 = 0;
   private boolean searchDragging;
   private static final float VOLUME = 5.5F;
   private final Map<Module, AnimationUtils> modules8 = new HashMap<>();
   private BooleanSetting bindingBooleanSetting2;
   public static final float BIND_POPUP_W = 52.0F;
   public static final float BIND_POPUP_H = 31.0F;
   private static final float VOLUME2 = 5.0F;
   private static final float VOLUME3 = 11.0F;
   private static final float VOLUME4 = 11.0F;
   private BooleanSetting bindPopupSetting;
   private float bindPopupX;
   private float bindPopupY;

   public ClickGuiState() {
      this.refreshModules();

      for (Module.ModuleCategory var4 : Module.ModuleCategory.values()) {
         this.enumMap4.put(var4, new AnimationUtils(var4 == this.moduleCategory ? 1.0F : 0.0F, 500.0F, Easings.CUBIC_OUT));
      }

      this.animatedScrollValue = new AnimationUtils(0.0F, 10.0F, Easings.CUBIC_OUT);
      this.categoryTransitionProgress = new AnimationUtils(0.0F, 12.0F, Easings.CUBIC_OUT);
      this.animatedBackgroundY = new AnimationUtils(0.0F, 10.0F, Easings.CUBIC_OUT);
      this.moduleCategory2 = this.moduleCategory;
   }

   public void refreshModules() {
      this.allModules.clear();
      this.allModules.addAll(ModuleClass.INSTANCE.getObject().stream().filter(module -> !"AutoForest".equals(module.getName())).toList());

      for (Module.ModuleCategory var4 : Module.ModuleCategory.values()) {
         this.enumMap3.put(var4, this.allModules.stream().filter(module -> module.getCategory() == var4).toList());
         this.enumMap.putIfAbsent(var4, 0.0F);
         this.enumMap2.putIfAbsent(var4, new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT));
      }
   }

   public void beginScreenReveal() {
      this.moduleCategory2 = this.moduleCategory;
      this.categoryTransitionProgress.setValue(1.0F);
      this.categoryTransitionProgress.update(1.0F);
      this.categoryTransitionDirection = 1;
      this.resetModuleAppearAnimation();

      for (Module var2 : this.getModules(this.moduleCategory)) {
         AnimationUtils var3 = this.modules7.computeIfAbsent(var2, key -> new AnimationUtils(0.0F, 7.5F, Easings.CUBIC_OUT));
         var3.setSpeed(7.5F);
         var3.setEasing(Easings.CUBIC_OUT);
         var3.setValue(0.0F);
         var3.update(1.0F);
      }
   }

   public void updatePosition(Window window, int categoryCount) {
      this.x2 = window.getScaledWidth() / 2.0F - this.menuWidth2 / 2.0F;
      this.y2 = window.getScaledHeight() / 2.0F - this.menuHeight2 / 2.0F;
      this.x3 = true;
   }

   public void setCurrentCategoryWithTransition(Module.ModuleCategory newCategory) {
      if (newCategory == Module.ModuleCategory.MODELS) {
         newCategory = Module.ModuleCategory.COMBAT;
      }

      if (this.moduleCategory != newCategory) {
         List var2 = ClickGuiLayout.CATEGORY_ORDER;
         int var3 = Math.max(0, var2.indexOf(this.moduleCategory));
         int var4 = Math.max(0, var2.indexOf(newCategory));
         int var5 = var4 - var3;
         if (!var2.isEmpty() && Math.abs(var5) > var2.size() / 2) {
            var5 = var5 > 0 ? var5 - var2.size() : var5 + var2.size();
         }

         this.categoryTransitionDirection = var5 >= 0 ? 1 : -1;
         this.moduleCategory2 = this.moduleCategory;
         this.moduleCategory = newCategory;
         this.categoryTransitionProgress.setValue(0.0F);
         this.categoryTransitionProgress.setSpeed(10.0F);
         this.categoryTransitionProgress.setEasing(Easings.CUBIC_OUT);
         this.categoryTransitionProgress.update(1.0F);
         this.setTargetScrollValue(0.0F);

         for (Module var8 : this.getModules(newCategory)) {
            AnimationUtils var9 = this.modules7.computeIfAbsent(var8, m -> new AnimationUtils(0.0F, 7.5F, Easings.CUBIC_OUT));
            var9.setSpeed(7.5F);
            var9.setEasing(Easings.CUBIC_OUT);
            var9.setValue(0.0F);
            var9.update(1.0F);
         }
      }
   }

   public float getCategoryTransitionProgress() {
      this.categoryTransitionProgress.update(1.0F);
      return MathHelper.clamp(this.categoryTransitionProgress.getValue(), 0.0F, 1.0F);
   }

   public int getCategoryTransitionDirection() {
      return this.categoryTransitionDirection;
   }

   public void cycleCategory(int dir) {
      List var2 = ClickGuiLayout.CATEGORY_ORDER;
      int var3 = var2.size();
      if (var3 != 0) {
         int var4 = Math.max(0, var2.indexOf(this.moduleCategory));
         int var5 = ((var4 + dir) % var3 + var3) % var3;
         if (var5 != var4) {
            this.setCurrentCategoryWithTransition((Module.ModuleCategory)var2.get(var5));
            this.setSearchText("");
         }
      }
   }

   public float getAnimatedCategoryIndex(float targetIndex) {
      if (!this.flag2) {
         this.animationUtils2.setValue(targetIndex);
         this.flag2 = true;
      } else {
         this.animationUtils2.update(targetIndex);
      }

      return this.animationUtils2.getValue();
   }

   public void recordCategoryIndex(float value) {
      this.recordedCategoryIndex = value;
   }

   public float getRecordedCategoryIndex() {
      return this.recordedCategoryIndex;
   }

   public Module.ModuleCategory getPreviousCategory() {
      return this.moduleCategory2;
   }

   public float getModuleAppearProgress(Module module) {
      AnimationUtils var2 = this.modules7.computeIfAbsent(module, key -> new AnimationUtils(0.0F, 7.5F, Easings.CUBIC_OUT));
      var2.setSpeed(7.5F);
      var2.setEasing(Easings.CUBIC_OUT);
      var2.update(1.0F);
      return MathHelper.clamp(var2.getValue(), 0.0F, 1.0F);
   }

   public void resetModuleAppearAnimation() {
      this.modules7.clear();
   }

   public void setTargetBackgroundY(float y) {
      this.animatedBackgroundY2 = y;
      this.animatedBackgroundY.update(y);
   }

   public float getAnimatedBackgroundY() {
      this.animatedBackgroundY.update(this.animatedBackgroundY2);
      return this.animatedBackgroundY.getValue();
   }

   public void updateScrollAnimation() {
      this.animatedScrollValue.update(this.animatedScrollValue.getTarget());
   }

   public void setTargetScrollValue(float value) {
      float var2 = this.animatedScrollValue.getValue();
      this.lastScrollDelta = value - var2;
      this.lastScrollDelta2 = System.currentTimeMillis();
      this.animatedScrollValue.update(value);
   }

   public float getAnimatedScrollValue() {
      return this.animatedScrollValue.getValue();
   }

   public float getLastScrollDelta() {
      float var1 = this.lastScrollDelta;
      long var2 = System.currentTimeMillis() - this.lastScrollDelta2;
      if (var2 > 150L) {
         var1 *= Math.max(0.0F, 1.0F - (float)(var2 - 150L) / 250.0F);
      }

      return var1;
   }

   public float getTargetScrollValue() {
      return this.animatedScrollValue.getTarget();
   }

   public void setMaxScrollValue(float max) {
      this.maxScrollValue = max;
   }

   public float getMaxScrollValue() {
      return this.maxScrollValue;
   }

   public void resetScrollDelta() {
      this.lastScrollDelta = 0.0F;
   }

   public List<ModernSettingWindow> getSettingWindows() {
      return this.settingWindows;
   }

   public void addSettingWindow(ModernSettingWindow window) {
      this.settingWindows.add(window);
   }

   public void removeSettingWindow(ModernSettingWindow window) {
      this.settingWindows.remove(window);
   }

   public Module.ModuleCategory getCurrentCategory() {
      return this.moduleCategory;
   }

   public void setCurrentCategory(Module.ModuleCategory currentCategory) {
      this.moduleCategory = currentCategory == Module.ModuleCategory.MODELS ? Module.ModuleCategory.COMBAT : currentCategory;
   }

   public AnimationUtils getCategorySelectAnimation(Module.ModuleCategory category) {
      return this.enumMap4
         .computeIfAbsent(category, k -> new AnimationUtils(category == this.moduleCategory ? 1.0F : 0.0F, 300.0F, Easings.CUBIC_OUT));
   }

   public float getScrollValue() {
      return this.scrollValue2;
   }

   public void setScrollValue(float scrollValue) {
      this.scrollValue2 = scrollValue;
   }

   public float getDragX() {
      return this.dragX2;
   }

   public void setDragX(float dragX) {
      this.dragX2 = dragX;
   }

   public float getDragY() {
      return this.dragY2;
   }

   public void setDragY(float dragY) {
      this.dragY2 = dragY;
   }

   public boolean isDragging() {
      return this.dragging2;
   }

   public void setDragging(boolean dragging) {
      this.dragging2 = false;
   }

   public boolean isClosing() {
      return this.closing2;
   }

   public void setClosing(boolean closing) {
      this.closing2 = closing;
   }

   public float getX() {
      return this.x2;
   }

   public void setX(float x) {
      this.x2 = x;
      this.x3 = true;
   }

   public float getY() {
      return this.y2;
   }

   public void setY(float y) {
      this.y2 = y;
      this.x3 = true;
   }

   public float getMenuWidth() {
      return this.menuWidth2;
   }

   public void setMenuWidth(float menuWidth) {
      this.menuWidth2 = 480.0F;
   }

   public float getMenuHeight() {
      return this.menuHeight2;
   }

   public void setMenuHeight(float menuHeight) {
      this.menuHeight2 = 300.0F;
   }

   public float getMinMenuWidth() {
      return 480.0F;
   }

   public float getLeftPanelWidth() {
      return MathHelper.clamp(this.menuWidth2 * 0.359375F, 100.0F, 190.0F);
   }

   public float getSearchBarWidth() {
      return Math.max(98.0F, this.getLeftPanelWidth() - 30.0F);
   }

   public float getCategoryWidth() {
      return Math.max(104.0F, this.getLeftPanelWidth() - 24.0F);
   }

   public float getModuleColumnX() {
      return this.x2 + this.getLeftPanelWidth() + 14.0F;
   }

   public float getModuleColumnWidth() {
      return Math.max(140.0F, this.menuWidth2 - this.getLeftPanelWidth() - 34.0F);
   }

   public int getModuleColumns() {
      return 3;
   }

   public void setModuleColumns(int moduleColumns) {
      this.moduleColumns2 = MathHelper.clamp(moduleColumns, 1, 3);
   }

   public float getAnimatedModuleCellWidth(float targetWidth) {
      if (!this.flag) {
         this.animationUtils.setValue(targetWidth);
         this.flag = true;
      } else {
         this.animationUtils.update(targetWidth);
      }

      return this.animationUtils.getValue();
   }

   public float getAnimatedModuleLocalX(Module module, float targetX) {
      AnimationUtils var3 = this.modules4.computeIfAbsent(module, key -> {
         AnimationUtils var2 = new AnimationUtils(targetX, 11.0F, Easings.CUBIC_OUT);
         var2.setValue(targetX);
         return var2;
      });
      var3.update(targetX);
      return var3.getValue();
   }

   public float getAnimatedModuleLocalY(Module module, float targetY) {
      AnimationUtils var3 = this.modules5.computeIfAbsent(module, key -> {
         AnimationUtils var2 = new AnimationUtils(targetY, 11.0F, Easings.CUBIC_OUT);
         var2.setValue(targetY);
         return var2;
      });
      var3.update(targetY);
      return var3.getValue();
   }

   public float getVisibleModulesHeight() {
      return this.menuHeight2 - 50.0F - 46.0F;
   }

   public float getResizeHandleSize() {
      return 0.0F;
   }

   public void beginResize(double mouseX, double mouseY) {
   }

   public void updateResize(double mouseX, double mouseY, Window window) {
      if (this.resizing) {
         float var6 = this.volume3 + (float)mouseX - this.volume;
         float var7 = this.volume4 + (float)mouseY - this.volume2;
         this.menuWidth2 = MathHelper.clamp(var6, 280.0F, 900.0F);
         this.menuHeight2 = MathHelper.clamp(var7, 180.0F, 600.0F);
         if (window != null) {
            this.clampToWindow(window);
         }
      }
   }

   public void endResize() {
      this.resizing = false;
   }

   public boolean isResizing() {
      return this.resizing;
   }

   public boolean isSettingsPanelOpen() {
      return this.settingsPanelOpen2;
   }

   public void setSettingsPanelOpen(boolean settingsPanelOpen) {
      this.settingsPanelOpen2 = settingsPanelOpen;
      if (settingsPanelOpen) {
         this.profilePanelOpen2 = false;
         this.friendsPanelOpen2 = false;
         this.autosetPanelOpen2 = false;
      }
   }

   public boolean isProfilePanelOpen() {
      return this.profilePanelOpen2;
   }

   public void setProfilePanelOpen(boolean profilePanelOpen) {
      this.profilePanelOpen2 = profilePanelOpen;
      if (profilePanelOpen) {
         this.settingsPanelOpen2 = false;
         this.friendsPanelOpen2 = false;
         this.autosetPanelOpen2 = false;
      }
   }

   public boolean isFriendsPanelOpen() {
      return this.friendsPanelOpen2;
   }

   public void setFriendsPanelOpen(boolean friendsPanelOpen) {
      this.friendsPanelOpen2 = friendsPanelOpen;
      if (friendsPanelOpen) {
         this.settingsPanelOpen2 = false;
         this.profilePanelOpen2 = false;
         this.autosetPanelOpen2 = false;
      }
   }

   public boolean isAutosetPanelOpen() {
      return this.autosetPanelOpen2;
   }

   public void setAutosetPanelOpen(boolean autosetPanelOpen) {
      this.autosetPanelOpen2 = autosetPanelOpen;
      if (autosetPanelOpen) {
         this.settingsPanelOpen2 = false;
         this.profilePanelOpen2 = false;
         this.friendsPanelOpen2 = false;
      }
   }

   public void clampToWindow(Window window) {
      if (window != null) {
         float var2 = Math.max(6.0F, window.getScaledWidth() - this.menuWidth2 - 6.0F);
         float var3 = Math.max(6.0F, window.getScaledHeight() - this.menuHeight2 - 6.0F);
         this.x2 = MathHelper.clamp(this.x2, 6.0F, var2);
         this.y2 = MathHelper.clamp(this.y2, 6.0F, var3);
      }
   }

   public float getRenderOffsetY() {
      return this.renderOffsetY2;
   }

   public void setRenderOffsetY(float renderOffsetY) {
      this.renderOffsetY2 = renderOffsetY;
   }

   public List<Module> getModules(Module.ModuleCategory category) {
      if (this.searchText2.isBlank()) {
         return this.enumMap3.getOrDefault(category, List.of());
      }

      String var2 = this.searchText2.toLowerCase(Locale.ROOT);
      return this.allModules.stream().filter(module -> this.helper(module, var2)).toList();
   }

   private boolean helper(Module module, String query) {
      if (this.helper2(module, query)) {
         return true;
      }

      String var3 = this.toEnglish(query).toLowerCase(Locale.ROOT);
      return !var3.equals(query) && this.helper2(module, var3);
   }

   private boolean helper2(Module module, String query) {
      if (query.isBlank()) {
         return false;
      }

      String var3 = module.getName() == null ? "" : module.getName();
      String var4 = module.getDisplayName() == null ? "" : module.getDisplayName();
      String var5 = module.getDisplayDescription() == null ? "" : module.getDisplayDescription();
      return var3.toLowerCase(Locale.ROOT).contains(query) || var4.toLowerCase(Locale.ROOT).contains(query) || var5.toLowerCase(Locale.ROOT).contains(query);
   }

   public List<Module> getAllModules() {
      return this.allModules;
   }

   public String toEnglish(String text) {
      StringBuilder var2 = new StringBuilder();

      for (char var6 : text.toCharArray()) {
         var2.append(CHARACTERS.getOrDefault(var6, var6));
      }

      return var2.toString();
   }

   public float getSliderPos(FloatSetting setting) {
      float var2 = setting.getMax() - setting.getMin();
      return (setting.get() - setting.getMin()) / var2;
   }

   public float getSliderValue(FloatSetting setting, float posX, double mouseX) {
      return this.getSliderValue(setting, posX, mouseX, 79.0F);
   }

   public float getSliderValue(FloatSetting setting, float posX, double mouseX, float sliderWidth) {
      float var6 = setting.getMax() - setting.getMin();
      float var7 = (float)mouseX - posX;
      float var8 = Math.max(1.0F, sliderWidth);
      float var9 = Math.max(0.0F, Math.min(1.0F, var7 / var8));
      float var10 = setting.getMin() + var6 * var9;
      float var11 = setting.getIncrement();
      var10 = Math.round(var10 / var11) * var11;
      return Math.max(setting.getMin(), Math.min(setting.getMax(), var10));
   }

   public void beginSliderDrag(FloatSetting setting, double mouseX) {
      this.floatSettings2.put(setting, mouseX);
      this.floatSettings3.put(setting, 0.0);
   }

   public void endSliderDrag(FloatSetting setting) {
      this.floatSettings2.remove(setting);
      this.floatSettings3.remove(setting);
   }

   public float updateActiveSliderValue(FloatSetting setting, double mouseX) {
      double var4 = this.floatSettings2.getOrDefault(setting, mouseX);
      this.floatSettings2.put(setting, mouseX);
      double var6 = mouseX - var4;
      if (Math.abs(var6) < 1.0E-4) {
         return setting.get();
      }

      float var8 = setting.getMax() - setting.getMin();
      float var9 = setting.getIncrement();
      if (!(var8 <= 0.0F) && !(var9 <= 0.0F)) {
         double var10 = var8 / var9;
         if (var10 <= 0.0) {
            return setting.get();
         } else {
            double var12 = 79.0 / var10;
            if (var12 <= 0.0) {
               return setting.get();
            } else {
               double var14 = this.floatSettings3.getOrDefault(setting, 0.0) + var6;
               int var16 = (int)(var14 / var12);
               if (var16 == 0) {
                  this.floatSettings3.put(setting, var14);
                  return setting.get();
               } else {
                  this.floatSettings3.put(setting, var14 - var16 * var12);
                  float var17 = setting.get() + var16 * var9;
                  var17 = Math.round(var17 / var9) * var9;
                  return Math.max(setting.getMin(), Math.min(setting.getMax(), var17));
               }
            }
         }
      } else {
         return setting.get();
      }
   }

   public float getScroll(Module.ModuleCategory category) {
      AnimationUtils var2 = this.enumMap2.computeIfAbsent(category, key -> new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT));
      var2.update(this.enumMap.getOrDefault(category, 0.0F));
      return var2.getValue();
   }

   public void clampScroll(Module.ModuleCategory category, float contentHeight) {
      float var3 = this.getTotalModulesHeight(category);
      float var4 = Math.min(0.0F, contentHeight - var3);
      float var5 = this.enumMap.getOrDefault(category, 0.0F);
      if (var5 < var4 || var5 > 0.0F) {
         this.enumMap.put(category, Math.max(var4, Math.min(0.0F, var5)));
      }
   }

   public void addScroll(Module.ModuleCategory category, double verticalAmount, float contentHeight) {
      float var5 = this.getTotalModulesHeight(category);
      float var6 = Math.min(0.0F, contentHeight - var5);
      float var7 = this.enumMap.getOrDefault(category, 0.0F);
      float var8 = var7 + (float)(verticalAmount * 20.0);
      this.enumMap.put(category, Math.max(var6, Math.min(0.0F, var8)));
   }

   public float getTotalModulesHeight(Module.ModuleCategory category) {
      float var2 = 0.0F;

      for (Module var4 : this.getModules(category)) {
         var2 += 34.0F + ClickGuiLayout.getModuleHeight(var4, this.getOpenProgress(var4), this);
      }

      return var2;
   }

   public float getOpenProgress(Module module) {
      boolean var2 = module.getSettings() != null && !module.getSettings().isEmpty();
      AnimationUtils var3 = this.modules3
         .computeIfAbsent(module, key -> new AnimationUtils(!var2 && !module.isOpen() ? 0.0F : 1.0F, 5.5F, Easings.QUART_OUT));
      var3.setSpeed(5.5F * ClickGuiTheme.animSpeedMul());
      var3.setEasing(Easings.QUART_OUT);
      var3.update(!var2 && !module.isOpen() ? 0.0F : 1.0F);
      return var3.getValue();
   }

   public void recordModuleBounds(Module module, float x, float y, float width, float height, float openProgress) {
      float[] var7 = this.modules6.get(module);
      if (var7 == null) {
         var7 = new float[5];
         this.modules6.put(module, var7);
      }

      var7[0] = x;
      var7[1] = y;
      var7[2] = width;
      var7[3] = height;
      var7[4] = openProgress;
   }

   public float[] getModuleBounds(Module module) {
      return this.modules6.get(module);
   }

   public void recordScroll(float v) {
      this.recordedScroll = v;
   }

   public float getRecordedScroll() {
      return this.recordedScroll;
   }

   public void recordCellWidth(float v) {
      this.recordedCellWidth = v;
   }

   public float getRecordedCellWidth() {
      return this.recordedCellWidth;
   }

   public void recordDropdownProgress(Object setting, float p) {
      this.objects.put(setting, p);
   }

   public float getRecordedDropdownProgress(Object setting) {
      return this.objects.getOrDefault(setting, 0.0F);
   }

   public float updateDotsRotation(Module module, float targetAngle) {
      float var3 = this.modules2.getOrDefault(module, targetAngle);
      var3 += (targetAngle - var3) * 0.06F;
      if (Math.abs(targetAngle - var3) < 0.001F) {
         var3 = targetAngle;
      }

      this.modules2.put(module, var3);
      return var3;
   }

   public AnimationUtils getBooleanBackgroundAnimation(BooleanSetting setting) {
      return this.booleanSettings.computeIfAbsent(setting, key -> new AnimationUtils(setting.isState() ? 1.0F : 0.0F, 15.0F, Easings.CUBIC_OUT));
   }

   public AnimationUtils getBooleanCircleAnimation(BooleanSetting setting) {
      return this.booleanSettings2.computeIfAbsent(setting, key -> new AnimationUtils(setting.isState() ? 1.0F : 0.0F, 8.2F, Easings.BACK_OUT));
   }

   public AnimationUtils getSliderAnimation(FloatSetting setting) {
      return this.floatSettings.computeIfAbsent(setting, key -> new AnimationUtils(this.getSliderPos(setting), 12.0F, Easings.CUBIC_OUT));
   }

   public AnimationUtils getModeAnimation(String key, boolean selected) {
      return this.strings.computeIfAbsent(key, unused -> new AnimationUtils(selected ? 1.0F : 0.0F, 10.0F, Easings.CUBIC_OUT));
   }

   public float getModeDropdownProgress(ModeSetting setting) {
      AnimationUtils var2 = this.modeSettings
         .computeIfAbsent(setting, key -> new AnimationUtils(setting.isExpanded() ? 1.0F : 0.0F, 4.0F, Easings.QUART_OUT));
      var2.setSpeed(4.0F);
      var2.setEasing(Easings.QUART_OUT);
      var2.update(setting.isExpanded() ? 1.0F : 0.0F);
      return MathHelper.clamp(var2.getValue(), 0.0F, 1.0F);
   }

   public AnimationUtils getListAnimation(String key, boolean selected) {
      return this.strings2.computeIfAbsent(key, unused -> new AnimationUtils(selected ? 1.0F : 0.0F, 10.0F, Easings.CUBIC_OUT));
   }

   public float getListDropdownProgress(ListSetting setting) {
      AnimationUtils var2 = this.listSettings
         .computeIfAbsent(setting, key -> new AnimationUtils(setting.isExpanded() ? 1.0F : 0.0F, 4.0F, Easings.QUART_OUT));
      var2.setSpeed(4.0F);
      var2.setEasing(Easings.QUART_OUT);
      var2.update(setting.isExpanded() ? 1.0F : 0.0F);
      return MathHelper.clamp(var2.getValue(), 0.0F, 1.0F);
   }

   public AnimationUtils getBindAnimation(String key, boolean binding) {
      return this.strings3.computeIfAbsent(key, unused -> new AnimationUtils(binding ? 1.0F : 0.0F, 10.0F, Easings.CUBIC_OUT));
   }

   public AnimationUtils getTextHoverAnimation(String key, boolean hovered) {
      return this.strings4.computeIfAbsent(key, unused -> new AnimationUtils(hovered ? 1.0F : 0.0F, 9.0F, Easings.CUBIC_OUT));
   }

   public float getModuleToggleProgress(Module module) {
      AnimationUtils var2 = this.modules8.computeIfAbsent(module, m -> new AnimationUtils(m.isEnable() ? 1.0F : 0.0F, 12.0F, Easings.CUBIC_OUT));
      var2.update(module.isEnable() ? 1.0F : 0.0F);
      return MathHelper.clamp(var2.getValue(), 0.0F, 1.0F);
   }

   public float advanceTextScrollPhase(String key, boolean hovered) {
      float var3 = this.strings5.getOrDefault(key, 0.0F);
      boolean var4 = this.strings7.getOrDefault(key, false);
      boolean var5 = this.strings6.getOrDefault(key, false);
      if (hovered) {
         var3 += 0.004F;
         if (var3 > 1.0F) {
            var3--;
         }

         var5 = false;
      } else {
         if (var4 && var3 > 0.0F) {
            var5 = true;
         }

         if (var5) {
            var3 += 0.004F;
            if (var3 >= 1.0F) {
               var3 = 0.0F;
               var5 = false;
            }
         }
      }

      this.strings7.put(key, hovered);
      this.strings6.put(key, var5);
      this.strings5.put(key, var3);
      return var3;
   }

   public boolean isTextScrollActive(String key, boolean hovered) {
      return hovered || this.strings6.getOrDefault(key, false);
   }

   public BindSetting getBindingSetting() {
      return this.bindingSetting2;
   }

   public void setBindingSetting(BindSetting bindingSetting) {
      this.bindingSetting2 = bindingSetting;
   }

   public BooleanSetting getBindingBooleanSetting() {
      return this.bindingBooleanSetting2;
   }

   public void setBindingBooleanSetting(BooleanSetting bindingBooleanSetting) {
      this.bindingBooleanSetting2 = bindingBooleanSetting;
   }

   public BooleanSetting getBindPopupSetting() {
      return this.bindPopupSetting;
   }

   public float getBindPopupX() {
      return this.bindPopupX;
   }

   public float getBindPopupY() {
      return this.bindPopupY;
   }

   public void openBindPopup(BooleanSetting setting, float px, float py) {
      Window var4 = MinecraftClient.getInstance().getWindow();
      float var5 = var4.getScaledWidth() - 52.0F - 2.0F;
      float var6 = var4.getScaledHeight() - 31.0F - 2.0F;
      this.bindPopupX = Math.max(2.0F, Math.min(px, var5));
      this.bindPopupY = Math.max(2.0F, Math.min(py, var6));
      this.bindPopupSetting = setting;
   }

   public void closeBindPopup() {
      this.bindPopupSetting = null;
   }

   public float[] bindPopupRect() {
      return new float[]{this.bindPopupX, this.bindPopupY, 52.0F, 31.0F};
   }

   private float helper3() {
      return this.bindPopupY + 31.0F - 5.0F - 11.0F;
   }

   public float[] bindPopupClearRect() {
      return new float[]{this.bindPopupX + 52.0F - 5.0F - 11.0F, this.helper3(), 11.0F, 11.0F};
   }

   public float[] bindPopupPillRect() {
      float var1 = this.bindPopupX + 5.0F;
      float var2 = 27.0F;
      return new float[]{var1, this.helper3(), var2, 11.0F};
   }

   public float bindPopupPad() {
      return 5.0F;
   }

   public Module getBindingModule() {
      return this.bindingModule2;
   }

   public void setBindingModule(Module bindingModule) {
      this.bindingModule2 = bindingModule;
   }

   public TextSetting getEditingTextSetting() {
      return this.editingTextSetting2;
   }

   public void setEditingTextSetting(TextSetting editingTextSetting) {
      this.editingTextSetting2 = editingTextSetting;
   }

   public boolean isSearchActive() {
      return this.searchActive2;
   }

   public void setSearchActive(boolean searchActive) {
      this.searchActive2 = searchActive;
   }

   public String getSearchText() {
      return this.searchText2;
   }

   public void appendSearchChar(char chr) {
      if (!Character.isISOControl(chr) && (this.searchText2.length() < 24 || this.hasSearchSelection())) {
         this.replaceSearchSelection(String.valueOf(chr));
      }
   }

   public void removeLastSearchChar() {
      if (this.hasSearchSelection()) {
         this.replaceSearchSelection("");
      } else {
         if (this.searchCursor > 0) {
            this.helper6();
            this.searchText2 = this.searchText2.substring(0, this.searchCursor - 1) + this.searchText2.substring(this.searchCursor);
            this.searchCursor--;
            this.helper4();
         }
      }
   }

   public void clearSearchText() {
      this.helper6();
      this.searchText2 = "";
      this.searchCursor = 0;
      this.helper4();
   }

   public void setSearchText(String searchText) {
      this.helper6();
      this.searchText2 = this.helper7(searchText);
      this.searchCursor = this.searchText2.length();
      this.helper4();
   }

   public void restoreSearchUndo() {
      String var1 = this.searchText2;
      this.searchText2 = this.text2 == null ? "" : this.text2;
      this.text2 = var1;
      this.searchCursor = this.searchText2.length();
      this.helper4();
   }

   public int getSearchCursor() {
      return this.searchCursor;
   }

   public int getSearchSelectionStart() {
      return Math.min(this.searchSelectionStart, this.searchSelectionStart2);
   }

   public int getSearchSelectionEnd() {
      return Math.max(this.searchSelectionStart, this.searchSelectionStart2);
   }

   public boolean hasSearchSelection() {
      return this.getSearchSelectionStart() != this.getSearchSelectionEnd();
   }

   public String getSelectedSearchText() {
      return !this.hasSearchSelection() ? "" : this.searchText2.substring(this.getSearchSelectionStart(), this.getSearchSelectionEnd());
   }

   public void selectAllSearchText() {
      this.searchSelectionStart = 0;
      this.searchSelectionStart2 = this.searchText2.length();
      this.searchCursor = this.searchText2.length();
   }

   public void setSearchCursor(int cursor, boolean keepSelection) {
      this.searchCursor = this.helper5(cursor);
      if (keepSelection) {
         this.searchSelectionStart2 = this.searchCursor;
      } else {
         this.searchSelectionStart = this.searchCursor;
         this.searchSelectionStart2 = this.searchCursor;
      }
   }

   public void startSearchSelection(int index) {
      this.searchCursor = this.helper5(index);
      this.searchSelectionStart = this.searchCursor;
      this.searchSelectionStart2 = this.searchCursor;
      this.searchDragging = true;
   }

   public void updateSearchSelection(int index) {
      if (this.searchDragging) {
         this.searchCursor = this.helper5(index);
         this.searchSelectionStart2 = this.searchCursor;
      }
   }

   public void stopSearchSelection() {
      this.searchDragging = false;
   }

   public boolean isSearchDragging() {
      return this.searchDragging;
   }

   public void replaceSearchSelection(String text) {
      this.helper6();
      String var2 = this.helper7(text);
      int var3 = this.getSearchSelectionStart();
      int var4 = this.getSearchSelectionEnd();
      if (!this.hasSearchSelection()) {
         var3 = this.searchCursor;
         var4 = this.searchCursor;
      }

      int var5 = Math.max(0, 24 - (this.searchText2.length() - (var4 - var3)));
      if (var2.length() > var5) {
         var2 = var2.substring(0, var5);
      }

      this.searchText2 = this.searchText2.substring(0, var3) + var2 + this.searchText2.substring(var4);
      this.searchCursor = var3 + var2.length();
      this.helper4();
   }

   private void helper4() {
      this.searchSelectionStart = this.searchCursor;
      this.searchSelectionStart2 = this.searchCursor;
      this.searchDragging = false;
   }

   private int helper5(int index) {
      return Math.max(0, Math.min(this.searchText2.length(), index));
   }

   private void helper6() {
      this.text2 = this.searchText2;
   }

   private String helper7(String text) {
      if (text != null && !text.isEmpty()) {
         StringBuilder var2 = new StringBuilder();

         for (int var3 = 0; var3 < text.length() && var2.length() < 24; var3++) {
            char var4 = text.charAt(var3);
            if (!Character.isISOControl(var4)) {
               var2.append(var4);
            }
         }

         return var2.toString();
      } else {
         return "";
      }
   }

   static {
      String var0 = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
      String var1 = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
      int var2 = Math.min(var0.length(), var1.length());

      for (int var3 = 0; var3 < var2; var3++) {
         CHARACTERS.put(var0.charAt(var3), var1.charAt(var3));
      }
   }
}
