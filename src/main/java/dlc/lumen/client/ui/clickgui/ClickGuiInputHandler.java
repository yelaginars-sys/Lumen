package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import dlc.lumen.client.render.figura.FiguraBridge;
import java.util.List;
import net.minecraft.client.util.Window;

public class ClickGuiInputHandler implements QClient {
   private final ClickGuiState state2;
   private final ClickGuiSettingsPanel clickGuiSettingsPanel;
   private static final float VOLUME = 20.0F;
   private static final float VOLUME2 = 200.0F;
   private static final float VOLUME3 = 10.0F;
   private static final float VOLUME4 = 38.0F;
   private static final float VOLUME5 = 22.0F;
   private static final float VOLUME6 = 8.0F;
   private static final float VOLUME7 = 16.0F;
   private static final float VOLUME8 = 52.0F;
   private static final float VOLUME9 = 8.0F;
   private static final float VOLUME10 = 7.0F;
   private static final float VOLUME11 = 34.0F;

   public ClickGuiInputHandler(ClickGuiState state, ClickGuiSettingsPanel settingsPanel, ClickGuiSettingRenderer settingRenderer) {
      this.state2 = state;
      this.clickGuiSettingsPanel = settingsPanel;
   }

   public ClickGuiInputHandler(ClickGuiState state, ClickGuiSettingsPanel settingsPanel) {
      this(state, settingsPanel, new ClickGuiSettingRenderer());
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, Window screenWindow) {
      float var7 = this.state2.getX();
      float var8 = this.state2.getY() + this.state2.getRenderOffsetY();
      float var9 = this.state2.getMenuWidth();
      float var10 = this.state2.getMenuHeight();
      if (button >= 2) {
         if (this.state2.getBindingSetting() != null) {
            this.state2.getBindingSetting().setKey(KeyBoardUtils.createMouseBind(button));
            this.state2.setBindingSetting(null);
            return true;
         }

         if (this.state2.getBindingModule() != null) {
            this.state2.getBindingModule().setKey(KeyBoardUtils.createMouseBind(button));
            this.state2.setBindingModule(null);
            return true;
         }

         if (this.state2.getBindingBooleanSetting() != null) {
            this.state2.getBindingBooleanSetting().setKey(KeyBoardUtils.createMouseBind(button));
            this.state2.setBindingBooleanSetting(null);
            return true;
         }
      }

      if (this.state2.getBindPopupSetting() != null) {
         float[] var31 = this.state2.bindPopupRect();
         if (HoveringUtils.isHovered(mouseX, mouseY, var31[0], var31[1], var31[2], var31[3])) {
            float[] var32 = this.state2.bindPopupClearRect();
            float[] var33 = this.state2.bindPopupPillRect();
            if (HoveringUtils.isHovered(mouseX, mouseY, var32[0], var32[1], var32[2], var32[3])) {
               this.state2.getBindPopupSetting().setKey(-1);
               this.state2.setBindingBooleanSetting(null);
               return true;
            } else if (HoveringUtils.isHovered(mouseX, mouseY, var33[0], var33[1], var33[2], var33[3])) {
               this.state2.setBindingBooleanSetting(this.state2.getBindPopupSetting());
               return true;
            } else {
               return true;
            }
         } else {
            this.state2.closeBindPopup();
            this.state2.setBindingBooleanSetting(null);
            return true;
         }
      } else {
         if (this.clickGuiSettingsPanel.handleClick(this.state2, screenWindow, mouseX, mouseY, button)) {
            return true;
         }

         float var11 = var7 + 10.0F;
         float var12 = var8 + 38.0F;
         float var13 = var9 - 20.0F;
         float var14 = var10 - 38.0F - 52.0F;
         if (HoveringUtils.isHovered(mouseX, mouseY, var11, var12, var13, var14)) {
            int var15 = this.state2.getModuleColumns();
            float var16 = (var13 - 8.0F * (var15 - 1)) / var15;
            float var17 = this.state2.getRecordedCellWidth();
            List<Module> var18 = this.state2.getModules(this.state2.getCurrentCategory());
            float var19 = this.state2.getRecordedScroll();
            boolean var20 = !this.state2.getSearchText().isBlank();
            if (!var20 && this.state2.getCurrentCategory() == Module.ModuleCategory.AUTOBUY) {
               AutoBuyTab.mouseClicked(this.state2, var11, var12, var13, var19, mouseX, mouseY, button);
               return true;
            }

            if (!var20 && this.state2.getCurrentCategory() == Module.ModuleCategory.MODELS) {
               if (mouseY < var12 + 11.0F) {
                  return true;
               }

               if (button == 0) {
                  List var47 = FiguraBridge.listAvatarInfos();
                  int var49 = Math.max(1, var15);
                  int var50 = var47.size() + 1;

                  for (int var51 = 0; var51 < var50; var51++) {
                     float[] var52 = ClickGuiModelsLayout.gridCardRect(var51, var11, var12, var16, var17, var19, var49);
                     if (HoveringUtils.isHovered(mouseX, mouseY, var52[0], var52[1], var52[2], var52[3])) {
                        if (var51 == 0) {
                           FiguraBridge.clearAll();
                        } else {
                           FiguraBridge.applyAvatar(((FiguraBridge.AvatarInfo)var47.get(var51 - 1)).id());
                        }

                        return true;
                     }
                  }
               }

               return true;
            }

            for (Module var22 : var18) {
               float[] var23 = this.state2.getModuleBounds(var22);
               if (var23 != null) {
                  float var24 = var23[0];
                  float var25 = var23[1];
                  float var26 = var23[2];
                  float var27 = var23[3];
                  float var28 = var23[4];
                  if (!(var25 + var27 < var12) && !(var25 > var12 + var14)) {
                     if (HoveringUtils.isHovered(mouseX, mouseY, var24, var25, var26, 34.0)) {
                        if (button == 0) {
                           var22.toggle();
                        } else if (button == 1) {
                           if (var22.getSettings() != null && !var22.getSettings().isEmpty()) {
                              var22.setOpen(!var22.isOpen());
                           }
                        } else if (button == 2) {
                           this.state2.setBindingModule(var22);
                        }

                        return true;
                     }

                     if (var28 > 0.01F && (button == 0 || button == 2)) {
                        float var29 = var25 + 34.0F;
                        float var30 = var27 - 34.0F;
                        if (var30 > 0.0F
                           && HoveringUtils.isHovered(mouseX, mouseY, var24, var29, var26, var30)
                           && this.helper(var22, var24, ClickGuiLayout.getInlineSettingsOriginY(var25), var26, mouseX, mouseY, button)) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         if (button == 0) {
            List var34 = ClickGuiLayout.CATEGORY_ORDER;
            float var37 = 30.0F;
            float var40 = var34.size() * var37 - 8.0F;
            float var42 = var7 + (var9 - var40) / 2.0F;
            float var44 = var8 + var10 - 22.0F - 16.0F;

            for (int var45 = 0; var45 < var34.size(); var45++) {
               Module.ModuleCategory var46 = (Module.ModuleCategory)var34.get(var45);
               float var48 = var42 + var45 * var37;
               if (HoveringUtils.isHovered(mouseX, mouseY, var48, var44, 22.0, 22.0)) {
                  if (this.state2.getCurrentCategory() != var46) {
                     this.state2.setCurrentCategoryWithTransition(var46);
                     this.state2.setSearchText("");
                  }

                  return true;
               }
            }
         }

         if (button == 0) {
            float var35 = var7 + (var9 - 200.0F) / 2.0F;
            float var38 = var8 + 10.0F;
            float var41 = 200.0F;
            float var43 = 20.0F;
            if (HoveringUtils.isHovered(mouseX, mouseY, var35, var38, var41, var43)) {
               this.state2.setSearchActive(true);
               return true;
            }

            this.state2.setSearchActive(false);
         }

         if (button == 0 && this.state2.getResizeHandleSize() > 0.0F) {
            float var36 = var7 + this.state2.getMenuWidth() - this.state2.getResizeHandleSize() - 5.0F;
            float var39 = var8 + this.state2.getMenuHeight() - this.state2.getResizeHandleSize() - 5.0F;
            if (HoveringUtils.isHovered(
               mouseX, mouseY, var36 - 2.0F, var39 - 2.0F, this.state2.getResizeHandleSize() + 6.0F, this.state2.getResizeHandleSize() + 6.0F
            )) {
               this.state2.beginResize(mouseX, mouseY);
               return true;
            }
         }

         if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var7, var8, this.state2.getMenuWidth(), this.state2.getMenuHeight())) {
            this.state2.setDragging(true);
            this.state2.setDragX((float)(mouseX - var7));
            this.state2.setDragY((float)(mouseY - var8));
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.state2.stopSearchSelection();
      this.state2.setDragging(false);
      this.state2.endResize();
      this.clickGuiSettingsPanel.handleRelease();
      if (button == 0) {
         for (Module var7 : this.state2.getAllModules()) {
            List<Setting> var8 = var7.getSettings();
            if (var8 != null) {
               for (Setting var10 : var8) {
                  if (var10 instanceof FloatSetting var11) {
                     var11.setActive(false);
                     this.state2.endSliderDrag(var11);
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button) {
      if (this.clickGuiSettingsPanel.handleDrag(this.state2, mc == null ? null : mc.getWindow(), mouseX, mouseY)) {
         return true;
      }

      if (this.state2.isResizing()) {
         this.state2.updateResize(mouseX, mouseY, mc == null ? null : mc.getWindow());
         return true;
      }

      for (Module var7 : this.state2.getAllModules()) {
         List<Setting> var8 = var7.getSettings();
         if (var8 != null) {
            for (Setting var10 : var8) {
               if (var10 instanceof FloatSetting var11 && var11.isActive()) {
                  var11.setValue(this.state2.updateActiveSliderValue(var11, mouseX));
                  return true;
               }
            }
         }
      }

      if (this.state2.isDragging()) {
         this.state2.setX((float)mouseX - this.state2.getDragX());
         this.state2.setY((float)mouseY - this.state2.getDragY());
         if (mc != null) {
            this.state2.clampToWindow(mc.getWindow());
         }

         return true;
      } else if (button == 0 && this.state2.isSearchActive() && this.state2.isSearchDragging()) {
         int var12 = Module.ModuleCategory.values().length;
         float var13 = ClickGuiLayout.getSearchX(this.state2.getX(), var12, this.toBooleanOrDefault3());
         this.state2.updateSearchSelection(this.toBooleanOrDefault2(mouseX, var13));
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
      if (this.clickGuiSettingsPanel.handleScroll(this.state2, mouseX, mouseY, verticalAmount)) {
         return true;
      }

      float var7 = this.state2.getX();
      float var8 = this.state2.getY();
      float var9 = this.state2.getMenuWidth();
      float var10 = this.state2.getMenuHeight();
      float var11 = var7 + 10.0F;
      float var12 = var8 + 38.0F;
      float var13 = var9 - 20.0F;
      float var14 = var10 - 38.0F - 52.0F;
      float var15 = var8 + var10 - 22.0F - 16.0F - 10.0F;
      float var16 = 40.0F;
      if (HoveringUtils.isHovered(mouseX, mouseY, var7, var15, var9, var16)) {
         this.state2.cycleCategory(verticalAmount > 0.0 ? -1 : 1);
         return true;
      }

      if (HoveringUtils.isHovered(mouseX, mouseY, var11, var12, var13, var14)) {
         boolean var17 = !this.state2.getSearchText().isBlank();
         float var18;
         if (!var17 && this.state2.getCurrentCategory() == Module.ModuleCategory.MODELS) {
            var18 = ClickGuiModelsLayout.calculateTotalHeight(this.state2, var13);
         } else if (!var17 && this.state2.getCurrentCategory() == Module.ModuleCategory.AUTOBUY) {
            var18 = AutoBuyTab.calcTotalHeight(var13);
         } else {
            var18 = this.toBooleanOrDefault(this.state2.getModules(this.state2.getCurrentCategory()), this.state2.getModuleColumns());
         }

         float var19 = Math.max(0.0F, var18 - var14);
         this.state2.setMaxScrollValue(var19);
         float var20 = this.state2.getTargetScrollValue();
         float var21 = var20 - (float)(verticalAmount * 20.0);
         var21 = Math.max(0.0F, Math.min(var19, var21));
         this.state2.setTargetScrollValue(var21);
         return true;
      } else {
         return false;
      }
   }

   private boolean helper(Module module, float panelX, float moduleY, float panelWidth, double mouseX, double mouseY, int button) {
      List<Setting> var10 = module.getSettings();
      if (var10 != null && !var10.isEmpty()) {
         float var11 = 20.0F;

         for (Setting var13 : var10) {
            if (var13 != null && var13.visible()) {
               float var14 = moduleY + var11 + 4.0F;
               if (var13 instanceof BooleanSetting var15) {
                  float var33 = ClickGuiLayout.getToggleX(panelX, panelWidth);
                  boolean var38 = HoveringUtils.isHovered(mouseX, mouseY, panelX, var14 - 3.0F, panelWidth, 12.0);
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var33, var14 - 2.0F, 18.0, 10.0)) {
                     var15.setState(!var15.isState());
                     return true;
                  }

                  if (button == 1 && var38) {
                     this.state2.setBindingBooleanSetting(var15);
                     return true;
                  }

                  if (button == 2 && var38) {
                     this.state2.openBindPopup(var15, (float)mouseX + 4.0F, (float)mouseY + 4.0F);
                     return true;
                  }

                  var11 += 12.0F;
               } else if (var13 instanceof TextSetting var16) {
                  float var32 = ClickGuiLayout.getSettingControlLeft(panelX, panelWidth);
                  float var37 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
                  float var42 = Math.max(44.0F, var37 - var32);
                  float var45 = var37 - var42;
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var45, var14 - 2.5F, var42, 10.0)) {
                     this.state2.setSearchActive(false);
                     this.state2.stopSearchSelection();
                     this.state2.setEditingTextSetting(var16);
                     return true;
                  }

                  var11 += 22.0F;
               } else if (var13 instanceof FloatSetting var17) {
                  float var31 = Fonts.getFont("suisse", 12).getWidth(this.helper2(var17));
                  float var36 = ClickGuiLayout.getSettingRight(panelX, panelWidth) - var31;
                  float var41 = ClickGuiLayout.getSettingControlLeft(panelX, panelWidth);
                  float var44 = Math.max(28.0F, var36 - 8.0F - var41);
                  float var47 = var14 + 8.35F - 7.0F;
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var41, var47 - 1.75F, var44, 6.5)) {
                     var17.setActive(true);
                     var17.setValue(this.state2.getSliderValue(var17, var41, mouseX, var44));
                     this.state2.beginSliderDrag(var17, mouseX);
                     return true;
                  }

                  var11 += 12.0F;
               } else if (var13 instanceof ModeSetting var18) {
                  float var30 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
                  float var35 = this.helper3(var18);
                  float var40 = var30 - var35;
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var40, var14 - 2.5F, var35, 10.0)) {
                     if (!var18.isExpanded()) {
                        this.helper4();
                     }

                     var18.setExpanded(!var18.isExpanded());
                     return true;
                  }

                  if (var18.isExpanded() && this.state2.getRecordedDropdownProgress(var18) > 0.99F) {
                     float var43 = var14 + 9.5F + 3.0F;
                     float var46 = var43 + 2.0F;

                     for (String var49 : var18.getMods()) {
                        if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var40, var46 - 1.0F, var35, 10.0)) {
                           var18.set(var49);
                           var18.setExpanded(false);
                           return true;
                        }

                        var46 += 11.0F;
                     }
                  }

                  var11 += ClickGuiLayout.calculateModeSettingHeight(var18, this.state2.getModeDropdownProgress(var18));
               } else if (var13 instanceof ListSetting var19) {
                  String var29 = this.helper6(var19);
                  float var34 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
                  float var39 = this.helper5(var19, var29);
                  float var24 = var34 - var39;
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var24, var14 - 2.5F, var39, 10.0)) {
                     if (!var19.isExpanded()) {
                        this.helper4();
                     }

                     var19.setExpanded(!var19.isExpanded());
                     return true;
                  }

                  if (var19.isExpanded() && this.state2.getRecordedDropdownProgress(var19) > 0.99F) {
                     float var25 = var14 + 9.5F + 3.0F;
                     float var26 = var25 + 2.0F;

                     for (BooleanSetting var28 : var19.getSettings()) {
                        if (var28.visible()) {
                           if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var24, var26 - 1.0F, var39, 10.0)) {
                              var28.setState(!var28.isState());
                              return true;
                           }

                           var26 += 11.0F;
                        }
                     }
                  }

                  var11 += ClickGuiLayout.calculateListSettingHeight(var19, this.state2.getListDropdownProgress(var19));
               } else if (var13 instanceof BindSetting var20) {
                  String var21 = this.state2.getBindingSetting() == var20
                     ? "..."
                     : this.state2.toEnglish(KeyBoardUtils.getBindName(var20.getKey()));
                  float var22 = Math.max(34.0F, Fonts.getFont("suisse", 12).getWidth(var21) + 8.0F);
                  float var23 = ClickGuiLayout.getSettingRight(panelX, panelWidth) - var22;
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var23, var14 - 2.5F, var22, 10.0)) {
                     this.state2.setBindingSetting(var20);
                     return true;
                  }

                  var11 += 12.0F;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private float toBooleanOrDefault(List<Module> modules, int columns) {
      if (modules.isEmpty()) {
         return 0.0F;
      }

      float[] var3 = new float[Math.max(1, columns)];

      for (int var4 = 0; var4 < modules.size(); var4++) {
         Module var5 = (Module)modules.get(var4);
         int var6 = var4 % Math.max(1, columns);
         float var7 = ClickGuiLayout.getInlineModuleHeight(var5, this.state2.getOpenProgress(var5), this.state2);
         var3[var6] += var7 + 7.0F;
      }

      float var9 = 0.0F;

      for (float var8 : var3) {
         var9 = Math.max(var9, var8);
      }

      return Math.max(0.0F, var9 - 7.0F);
   }

   private String helper2(FloatSetting setting) {
      float var2 = setting.get();
      float var3 = setting.getIncrement();
      if (var3 >= 1.0F) {
         return String.valueOf((int)var2);
      } else {
         return var3 >= 0.1F ? String.format("%.1f", var2) : String.format("%.2f", var2);
      }
   }

   private float helper3(ModeSetting setting) {
      float var2 = Math.max(56.0F, Fonts.getFont("suisse", 12).getWidth(setting.displayCurrent()) + 16.0F);

      for (String var4 : setting.getMods()) {
         var2 = Math.max(var2, Fonts.getFont("suisse", 12).getWidth(setting.displayMode(var4)) + 16.0F);
      }

      return var2;
   }

   private void updateState() {
      for (Module var2 : this.state2.getAllModules()) {
         List<Setting> var3 = var2.getSettings();
         if (var3 != null) {
            for (Setting var5 : var3) {
               if (var5 instanceof ModeSetting var6) {
                  var6.setExpanded(false);
               }

               if (var5 instanceof ListSetting var7) {
                  var7.setExpanded(false);
               }
            }
         }
      }
   }

   private void helper4() {
      this.updateState();
   }

   private float helper5(ListSetting setting, String summary) {
      float var3 = Math.max(56.0F, Fonts.getFont("suisse", 12).getWidth(summary) + 16.0F);

      for (BooleanSetting var5 : setting.getSettings()) {
         if (var5.visible()) {
            var3 = Math.max(var3, Fonts.getFont("suisse", 12).getWidth(var5.name()) + 16.0F);
         }
      }

      return var3;
   }

   private String helper6(ListSetting setting) {
      String var2 = null;
      int var3 = 0;

      for (BooleanSetting var5 : setting.getSettings()) {
         if (var5.visible() && var5.isState()) {
            if (var2 == null) {
               var2 = var5.name();
            }

            var3++;
         }
      }

      if (var3 == 0) {
         return "Ничего";
      } else if (var3 == 1 && var2 != null) {
         return var2;
      } else {
         return var2 == null ? var3 + " выбрано" : var2 + " +" + (var3 - 1);
      }
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      if (this.state2.isFriendsPanelOpen() && this.clickGuiSettingsPanel.handleSocialKey(keyCode, modifiers)) {
         return true;
      }

      if (this.state2.isSettingsPanelOpen() && this.clickGuiSettingsPanel.handleColorsKey(keyCode)) {
         return true;
      }

      if (this.state2.isAutosetPanelOpen() && this.clickGuiSettingsPanel.handleAutosetKey(keyCode, modifiers)) {
         return true;
      }

      if (this.state2.getCurrentCategory() == Module.ModuleCategory.AUTOBUY && AutoBuyTab.hasFocus()) {
         return AutoBuyTab.keyPressed(keyCode);
      }

      if (this.state2.getBindPopupSetting() != null && this.state2.getBindingBooleanSetting() == null && keyCode == 256) {
         this.state2.closeBindPopup();
         return true;
      }

      if (this.state2.getEditingTextSetting() != null) {
         TextSetting var3 = this.state2.getEditingTextSetting();
         if (keyCode == 256 || keyCode == 257 || keyCode == 335) {
            this.state2.setEditingTextSetting(null);
            return true;
         }

         if (keyCode == 259) {
            String var4 = var3.get();
            if (var4 != null && !var4.isEmpty()) {
               var3.setText(var4.substring(0, var4.length() - 1));
            }

            return true;
         } else {
            return true;
         }
      } else {
         if (this.state2.isSearchActive()) {
            if ((modifiers & 2) != 0) {
               if (keyCode == 65) {
                  this.state2.selectAllSearchText();
                  return true;
               }

               if (keyCode == 67) {
                  if (this.state2.hasSearchSelection() && mc != null && mc.keyboard != null) {
                     mc.keyboard.setClipboard(this.state2.getSelectedSearchText());
                  }

                  return true;
               }

               if (keyCode == 86) {
                  if (mc != null && mc.keyboard != null) {
                     this.state2.replaceSearchSelection(mc.keyboard.getClipboard());
                  }

                  return true;
               }

               if (keyCode == 90) {
                  this.state2.restoreSearchUndo();
                  return true;
               }
            }

            if (keyCode == 256 || keyCode == 257 || keyCode == 335) {
               this.state2.setSearchActive(false);
               return true;
            }

            if (keyCode == 259) {
               this.state2.removeLastSearchChar();
               return true;
            }

            if (keyCode == 261) {
               this.state2.clearSearchText();
               return true;
            }

            if (keyCode == 263) {
               this.state2.setSearchCursor(this.state2.getSearchCursor() - 1, (modifiers & 1) != 0);
               return true;
            }

            if (keyCode == 262) {
               this.state2.setSearchCursor(this.state2.getSearchCursor() + 1, (modifiers & 1) != 0);
               return true;
            }
         }

         if (this.state2.getBindingModule() != null) {
            if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
               this.state2.getBindingModule().setKey(keyCode);
               this.state2.setBindingModule(null);
            } else {
               this.state2.getBindingModule().setKey(-1);
               this.state2.setBindingModule(null);
            }

            return true;
         } else if (this.state2.getBindingSetting() != null) {
            if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
               this.state2.getBindingSetting().setKey(keyCode);
               this.state2.setBindingSetting(null);
            } else {
               this.state2.getBindingSetting().setKey(-1);
               this.state2.setBindingSetting(null);
            }

            return true;
         } else {
            if (this.state2.getBindingBooleanSetting() == null) {
               return false;
            }

            if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
               this.state2.getBindingBooleanSetting().setKey(keyCode);
               this.state2.setBindingBooleanSetting(null);
            } else {
               this.state2.getBindingBooleanSetting().setKey(-1);
               this.state2.setBindingBooleanSetting(null);
            }

            return true;
         }
      }
   }

   public boolean charTyped(char chr) {
      if (this.state2.isFriendsPanelOpen() && this.clickGuiSettingsPanel.handleSocialChar(chr)) {
         return true;
      }

      if (this.state2.isSettingsPanelOpen() && this.clickGuiSettingsPanel.handleColorsChar(chr)) {
         return true;
      }

      if (this.state2.isAutosetPanelOpen() && this.clickGuiSettingsPanel.handleAutosetChar(chr)) {
         return true;
      }

      if (this.state2.getCurrentCategory() == Module.ModuleCategory.AUTOBUY && AutoBuyTab.hasFocus()) {
         return AutoBuyTab.charTyped(chr);
      }

      if (this.state2.getEditingTextSetting() != null) {
         if (!Character.isISOControl(chr)) {
            TextSetting var2 = this.state2.getEditingTextSetting();
            var2.setText(var2.get() + chr);
         }

         return true;
      } else {
         if (!this.state2.isSearchActive()) {
            return false;
         }

         this.state2.appendSearchChar(chr);
         return true;
      }
   }

   private int toBooleanOrDefault2(double mouseX, float searchX) {
      String var4 = this.state2.getSearchText();
      float var5 = searchX + 19.0F;
      float var6 = (float)mouseX - var5;
      if (!(var6 <= 0.0F) && !var4.isEmpty()) {
         for (int var7 = 1; var7 <= var4.length(); var7++) {
            float var8 = Fonts.getFont("suisse", 14).getWidth(var4.substring(0, var7 - 1));
            float var9 = Fonts.getFont("suisse", 14).getWidth(var4.substring(0, var7));
            float var10 = var8 + (var9 - var8) * 0.5F;
            if (var6 < var10) {
               return var7 - 1;
            }
         }

         return var4.length();
      } else {
         return 0;
      }
   }

   private float toBooleanOrDefault3() {
      String var1 = this.state2.getSearchText();
      String var2 = var1.isEmpty() ? "Search..." : var1;
      float var3 = 19.0F + Fonts.getFont("suisse", 14).getWidth(var2) + 8.0F;
      return Math.max(this.state2.getSearchBarWidth(), var3);
   }
}
