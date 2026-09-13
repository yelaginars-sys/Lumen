package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.ActionSetting;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ClickGuiSettingRenderer {
   private static final float VOLUME = 6.0F;

   public void render(
      DrawContext context,
      Module module,
      float panelX,
      float moduleY,
      float panelWidth,
      float openProgress,
      int colorTheme,
      double mouseX,
      double mouseY,
      ClickGuiState state
   ) {
      List<Setting> var13 = module.getSettings();
      if (var13 != null && !var13.isEmpty() && !(openProgress <= 0.01F)) {
         float var14 = ClickGuiLayout.calculateSettingsHeight(module, state);
         float var15 = moduleY + 20.0F;
         float var16 = var14 * openProgress;
         float var17 = Math.max(0.0F, panelWidth - 6.0F);
         float var18 = MathHelper.clamp(openProgress, 0.0F, 1.0F);
         float var19 = (1.0F - var18) * -5.0F;
         ScissorUtils.push();
         ScissorUtils.setFromComponentCoordinates(panelX + 3.0F, var15, var17, var16);
         float var20 = 20.0F;
         int var21 = 0;

         for (Setting var23 : var13) {
            if (var23 != null && var23.visible()) {
               float var24 = this.toBooleanOrDefault2(var18, var21);
               float var25 = moduleY + var20 + 4.0F + var19 + (1.0F - var24) * 5.0F;
               int var26 = Math.max(0, Math.min(255, (int)(255.0F * var24)));
               if (var23 instanceof BooleanSetting var27) {
                  this.helper(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var27, state);
                  var20 += 12.0F;
               } else if (var23 instanceof ActionSetting var28) {
                  this.toBooleanOrDefault(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var28, state);
                  var20 += 12.0F;
               } else if (var23 instanceof TextSetting var29) {
                  this.helper3(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var29, state);
                  var20 += 22.0F;
               } else if (var23 instanceof FloatSetting var30) {
                  this.helper2(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var30, state);
                  var20 += 12.0F;
               } else if (var23 instanceof ModeSetting var31) {
                  this.helper4(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var31, state);
                  var20 += ClickGuiLayout.calculateModeSettingHeight(var31, state.getModeDropdownProgress(var31));
               } else if (var23 instanceof ListSetting var32) {
                  this.helper5(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var32, state);
                  var20 += ClickGuiLayout.calculateListSettingHeight(var32, state.getListDropdownProgress(var32));
               } else if (var23 instanceof BindSetting var33) {
                  this.helper6(context, panelX, var25, panelWidth, var26, colorTheme, mouseX, mouseY, var33, state);
                  var20 += 12.0F;
               }

               var21++;
            }
         }

         ScissorUtils.pop();
      }
   }

   private void helper(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      BooleanSetting booleanSetting,
      ClickGuiState state
   ) {
      AnimationUtils var13 = state.getBooleanBackgroundAnimation(booleanSetting);
      AnimationUtils var14 = state.getBooleanCircleAnimation(booleanSetting);
      var13.update(booleanSetting.isState() ? 1.0F : 0.0F);
      var14.update(booleanSetting.isState() ? 1.0F : 0.0F);
      float var15 = var13.getValue();
      float var16 = var14.getValue();
      int var17 = ColorUtils.darken(colorTheme, 0.05F);
      int var18 = colorTheme;
      int var19 = (int)((var17 >> 16 & 0xFF) + ((var18 >> 16 & 0xFF) - (var17 >> 16 & 0xFF)) * var15);
      int var20 = (int)((var17 >> 8 & 0xFF) + ((var18 >> 8 & 0xFF) - (var17 >> 8 & 0xFF)) * var15);
      int var21 = (int)((var17 & 0xFF) + ((var18 & 0xFF) - (var17 & 0xFF)) * var15);
      int var22 = (int)((var17 >> 24 & 0xFF) + ((var18 >> 24 & 0xFF) - (var17 >> 24 & 0xFF)) * var15);
      int var23 = var22 << 24 | var19 << 16 | var20 << 8 | var21;
      float var24 = ClickGuiLayout.getSettingLeft(panelX);
      float var25 = ClickGuiLayout.getToggleX(panelX, panelWidth);
      float var26 = var25 - 8.0F - var24;
      this.helper12(
         this.helper24(12),
         context.getMatrices(),
         booleanSetting.name(),
         var24,
         settingY,
         var26,
         this.helper13(alpha),
         mouseX,
         mouseY,
         state,
         this.helper23(booleanSetting)
      );
      RenderUtils.drawRoundedRect(
         context.getMatrices(), var25, settingY - 2.0F, 18.0F, 10.0F, 4.0F, ColorUtils.rgba(var23 >> 16 & 0xFF, var23 >> 8 & 0xFF, var23 & 0xFF, alpha)
      );
      float var27 = 8.0F;
      float var28 = var25 + 1.5F + var16 * var27;
      RenderUtils.drawRoundCircle(context.getMatrices(), var28 + 3.5F, settingY + 3.0F, 7.0F, ColorUtils.rgba(255, 255, 255, alpha));
      boolean var29 = state.getBindingBooleanSetting() == booleanSetting;
      if (var29 || booleanSetting.getKey() != -1) {
         String var30 = var29 ? "..." : "[" + state.toEnglish(KeyBoardUtils.getBindName(booleanSetting.getKey())) + "]";
         Font var31 = this.helper24(11);
         float var32 = var31.getWidth(var30);
         var31.draw(
            context.getMatrices(),
            var30,
            var25 - 6.0F - var32,
            settingY,
            ColorUtils.setAlphaColor(var29 ? colorTheme : ColorUtils.rgba(150, 156, 170, 255), alpha)
         );
      }
   }

   private void toBooleanOrDefault(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      ActionSetting actionSetting,
      ClickGuiState state
   ) {
      float var13 = ClickGuiLayout.getSettingLeft(panelX);
      float var14 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
      float var15 = ClickGuiLayout.getSettingControlLeft(panelX, panelWidth);
      float var16 = Math.max(56.0F, var14 - var15);
      float var17 = var14 - var16;
      float var18 = settingY - 2.5F;
      boolean var19 = this.helper14(var17, var18, var16, 10.0F, mouseX, mouseY);
      int var20 = var19 ? ColorUtils.setAlphaColor(colorTheme, alpha) : ColorUtils.rgba(74, 80, 96, alpha);
      RenderUtils.drawRoundedRect(context.getMatrices(), var17, var18, var16, 10.0F, 2.5F, var20);
      this.helper24(12).drawCenteredString(context.getMatrices(), "Open", var17 + var16 / 2.0F, settingY + 1.0F, ColorUtils.setAlphaColor(-1, alpha));
   }

   private void helper2(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      FloatSetting floatSetting,
      ClickGuiState state
   ) {
      if (floatSetting.isActive()) {
         floatSetting.setValue(state.updateActiveSliderValue(floatSetting, mouseX));
      }

      AnimationUtils var13 = state.getSliderAnimation(floatSetting);
      var13.update(state.getSliderPos(floatSetting));
      float var14 = var13.getValue();
      String var15 = this.helper11(floatSetting);
      float var16 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
      float var17 = var16 - this.helper24(12).getWidth(var15);
      float var18 = ClickGuiLayout.getSettingControlLeft(panelX, panelWidth);
      float var19 = Math.max(28.0F, var17 - 8.0F - var18);
      float var20 = var18 - 8.0F - ClickGuiLayout.getSettingLeft(panelX);
      this.helper12(
         this.helper24(12),
         context.getMatrices(),
         floatSetting.name(),
         ClickGuiLayout.getSettingLeft(panelX),
         settingY + 1.0F,
         var20,
         this.helper13(alpha),
         mouseX,
         mouseY,
         state,
         this.helper23(floatSetting)
      );
      this.helper24(12).drawString(context.getMatrices(), var15, var17, settingY + 1.0F, ColorUtils.setAlphaColor(colorTheme, alpha));
      float var21 = settingY + 8.35F - 7.0F;
      int var22 = ColorUtils.rgba(93, 99, 115, alpha);
      RenderUtils.drawRoundedRect(context.getMatrices(), var18, var21, var19, 3.0F, 1.0F, var22);
      int var23 = ColorUtils.setAlphaColor(colorTheme, alpha);
      RenderUtils.drawRoundedRect(context.getMatrices(), var18, var21, var14 * var19, 3.0F, 1.0F, var23);
      RenderUtils.drawRoundCircle(context.getMatrices(), var18 + var14 * var19, var21 + 1.5F, 5.0F, ColorUtils.setAlphaColor(-1, alpha));
   }

   private void helper3(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      TextSetting textSetting,
      ClickGuiState state
   ) {
      String var13 = textSetting.get();
      boolean var14 = state.getEditingTextSetting() == textSetting;
      String var15 = var13 != null && !var13.isEmpty() ? var13 : "...";
      String var16 = var14 ? var15 + "_" : var15;
      float var17 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
      float var18 = ClickGuiLayout.getSettingControlLeft(panelX, panelWidth);
      float var19 = Math.max(44.0F, var17 - var18);
      float var20 = var17 - var19;
      this.helper12(
         this.helper24(12),
         context.getMatrices(),
         textSetting.name(),
         ClickGuiLayout.getSettingLeft(panelX),
         settingY,
         var20 - 8.0F - ClickGuiLayout.getSettingLeft(panelX),
         this.helper13(alpha),
         mouseX,
         mouseY,
         state,
         this.helper23(textSetting)
      );
      if (var14) {
         ColorUtils.setAlphaColor(colorTheme, alpha);
      } else {
         ColorUtils.rgba(74, 80, 96, alpha);
      }

      int var22 = ColorUtils.setAlphaColor(-1, alpha);
      float var23 = settingY - 2.5F;
      RenderUtils.drawRoundedRect(context.getMatrices(), var20, var23, var19, 10.0F, 3.5F, ColorUtils.rgba(16, 18, 24, alpha - 160));
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(var20 + 3.0F, var23 + 1.0F, var19 - 6.0F, 8.0);
      this.helper24(12).drawString(context.getMatrices(), var16, var20 + 3.0F, settingY + 1.0F, var22);
      ScissorUtils.pop();
   }

   private void helper4(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      ModeSetting modeSetting,
      ClickGuiState state
   ) {
      float var13 = state.getModeDropdownProgress(modeSetting);
      state.recordDropdownProgress(modeSetting, var13);
      String var14 = modeSetting.displayCurrent();
      float var15 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
      float var16 = this.helper7(modeSetting);
      float var17 = var15 - var16;
      float var18 = var17 + 5.0F;
      float var19 = settingY - 2.5F;
      this.helper12(
         this.helper24(12),
         context.getMatrices(),
         modeSetting.name(),
         ClickGuiLayout.getSettingLeft(panelX),
         settingY + 1.0F,
         var17 - 8.0F - ClickGuiLayout.getSettingLeft(panelX),
         this.helper13(alpha),
         mouseX,
         mouseY,
         state,
         this.helper23(modeSetting)
      );
      float var20 = 10.0F;
      if (var13 > 0.001F) {
         var20 += (3.0F + modeSetting.getMods().size() * 11.0F + 4.0F) * var13;
      }

      RenderUtils.drawRoundedRect(context.getMatrices(), var17, var19, var16, var20, 3.5F, ColorUtils.rgba(16, 18, 24, alpha - 160));
      this.helper24(12).drawString(context.getMatrices(), var14, var18, settingY + 1.0F, ColorUtils.rgba(194, 198, 213, alpha));
      if (!(var13 <= 0.001F)) {
         float var21 = settingY + 9.5F + 3.0F;
         int var22 = Math.max(0, Math.min(255, (int)(alpha * var13)));
         float var23 = (1.0F - var13) * -4.0F;
         float var24 = var21 + 2.0F + var23;

         for (String var26 : modeSetting.getMods()) {
            boolean var27 = modeSetting.getCurrent().equals(var26);
            String var28 = modeSetting.displayMode(var26);
            int var29 = var27 ? ColorUtils.setAlphaColor(-1, var22) : ColorUtils.rgba(170, 176, 188, var22);
            this.helper24(12).drawString(context.getMatrices(), var28, var17 + 5.0F, var24 + 1.0F, var29);
            if (var27) {
               this.helper16(context, var15 - 6.0F, var24 + 4.2F, 5.2F, colorTheme, var22);
            }

            var24 += 11.0F;
         }
      }
   }

   private void helper5(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      ListSetting listSetting,
      ClickGuiState state
   ) {
      float var13 = state.getListDropdownProgress(listSetting);
      state.recordDropdownProgress(listSetting, var13);
      String var14 = this.helper9(listSetting);
      float var15 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
      float var16 = this.helper8(listSetting, var14);
      float var17 = var15 - var16;
      float var18 = settingY - 2.5F;
      this.helper12(
         this.helper24(12),
         context.getMatrices(),
         listSetting.name(),
         ClickGuiLayout.getSettingLeft(panelX),
         settingY + 1.0F,
         var17 - 8.0F - ClickGuiLayout.getSettingLeft(panelX),
         this.helper13(alpha),
         mouseX,
         mouseY,
         state,
         this.helper23(listSetting)
      );
      int var19 = 0;

      for (BooleanSetting var21 : listSetting.getSettings()) {
         if (var21.visible()) {
            var19++;
         }
      }

      float var29 = 10.0F;
      if (var13 > 0.001F) {
         var29 += (3.0F + var19 * 11.0F + 4.0F) * var13;
      }

      RenderUtils.drawRoundedRect(context.getMatrices(), var17, var18, var16, var29, 3.5F, ColorUtils.rgba(16, 18, 24, alpha - 160));
      this.helper24(12).drawString(context.getMatrices(), var14, var17 + 5.0F, settingY + 1.0F, ColorUtils.rgba(194, 198, 213, alpha));
      if (!(var13 <= 0.001F)) {
         float var30 = settingY + 9.5F + 3.0F;
         int var22 = Math.max(0, Math.min(255, (int)(alpha * var13)));
         float var23 = (1.0F - var13) * -4.0F;
         float var24 = var30 + 2.0F + var23;

         for (BooleanSetting var26 : listSetting.getSettings()) {
            if (var26.visible()) {
               boolean var27 = var26.isState();
               int var28 = var27 ? ColorUtils.setAlphaColor(-1, var22) : ColorUtils.rgba(170, 176, 188, var22);
               this.helper24(12).drawString(context.getMatrices(), var26.name(), var17 + 5.0F, var24 + 1.0F, var28);
               if (var27) {
                  this.helper16(context, var15 - 6.0F, var24 + 4.2F, 5.2F, colorTheme, var22);
               } else {
                  this.helper17(context, var15 - 6.0F, var24 + 4.2F, 5.0F, var22);
               }

               var24 += 11.0F;
            }
         }
      }
   }

   private void helper6(
      DrawContext context,
      float panelX,
      float settingY,
      float panelWidth,
      int alpha,
      int colorTheme,
      double mouseX,
      double mouseY,
      BindSetting bindSetting,
      ClickGuiState state
   ) {
      boolean var13 = state.getBindingSetting() == bindSetting;
      AnimationUtils var14 = state.getBindAnimation(this.helper10(bindSetting), var13);
      var14.update(var13 ? 1.0F : 0.0F);
      float var15 = var14.getValue();
      String var16 = var13 ? "..." : state.toEnglish(KeyBoardUtils.getBindName(bindSetting.getKey()));
      Font var17 = this.helper24(12);
      float var18 = var17.getWidth(var16);
      float var19 = Math.max(34.0F, var18 + 8.0F);
      float var20 = ClickGuiLayout.getSettingRight(panelX, panelWidth);
      float var21 = var20 - var19;
      float var22 = settingY - 2.5F;
      int var23 = ColorUtils.setAlphaColor(ColorUtils.interpolateColor(ColorUtils.rgb(35, 35, 35), colorTheme, var15), 150);
      int var24 = ColorUtils.setAlphaColor(ColorUtils.interpolateColor(ColorUtils.rgb(140, 139, 145), -1, var15), alpha);
      RenderUtils.drawRoundedRect(context.getMatrices(), var21, var22, var19, 10.0F, 3.5F, ColorUtils.rgba(16, 18, 24, alpha - 160));
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(var21, var22, var19, 10.0);
      var17.drawCenteredString(context.getMatrices(), var16, var21 + var19 / 2.0F, settingY + 1.0F, var24);
      ScissorUtils.pop();
      this.helper12(
         var17,
         context.getMatrices(),
         bindSetting.name(),
         ClickGuiLayout.getSettingLeft(panelX),
         settingY + 1.0F,
         var21 - 8.0F - ClickGuiLayout.getSettingLeft(panelX),
         this.helper13(alpha),
         mouseX,
         mouseY,
         state,
         this.helper23(bindSetting)
      );
   }

   private String resolveString(ModeSetting setting, String mode) {
      return System.identityHashCode(setting) + "_mode_" + mode;
   }

   private float helper7(ModeSetting setting) {
      float var2 = Math.max(56.0F, this.helper24(12).getWidth(setting.displayCurrent()) + 16.0F);

      for (String var4 : setting.getMods()) {
         var2 = Math.max(var2, this.helper24(12).getWidth(setting.displayMode(var4)) + 16.0F);
      }

      return var2;
   }

   private String resolveString2(ListSetting setting, BooleanSetting entry) {
      return setting.hashCode() + "_list_" + entry.name();
   }

   private float helper8(ListSetting setting, String summary) {
      float var3 = Math.max(56.0F, this.helper24(12).getWidth(summary) + 16.0F);

      for (BooleanSetting var5 : setting.getSettings()) {
         if (var5.visible()) {
            var3 = Math.max(var3, this.helper24(12).getWidth(var5.name()) + 16.0F);
         }
      }

      return var3;
   }

   private String helper9(ListSetting setting) {
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

   private String helper10(BindSetting setting) {
      return setting.hashCode() + "_bind";
   }

   private String helper11(FloatSetting setting) {
      float var2 = setting.get();
      float var3 = setting.getIncrement();
      if (var3 >= 1.0F) {
         return String.valueOf((int)var2);
      } else {
         return var3 >= 0.1F ? String.format("%.1f", var2) : String.format("%.2f", var2);
      }
   }

   private void helper12(
      Font font,
      MatrixStack matrix,
      String text,
      float x,
      float y,
      float maxWidth,
      int color,
      double mouseX,
      double mouseY,
      ClickGuiState state,
      String animationKey
   ) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         float var14 = font.getWidth(text);
         float var15 = var14 - maxWidth;
         if (var15 <= 6.0F) {
            font.draw(matrix, text, x, y, color);
         } else {
            boolean var16 = this.helper14(x, y, maxWidth, font.getHeight(), mouseX, mouseY);
            float var17 = state.advanceTextScrollPhase(animationKey, var16);
            boolean var18 = state.isTextScrollActive(animationKey, var16);
            AnimationUtils var19 = state.getTextHoverAnimation(animationKey, var18);
            var19.update(var18 ? 1.0F : 0.0F);
            float var20 = var19.getValue();
            float var21 = this.helper15(var15, var17) * var20;
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, y - 2.0F, maxWidth, font.getHeight() + 4.0F);
            font.draw(matrix, text, x - var21, y, color);
            ScissorUtils.pop();
         }
      }
   }

   private int helper13(int alpha) {
      return ColorUtils.rgba(245, 245, 248, alpha);
   }

   private int resolveInt(int alpha) {
      return ColorUtils.rgba(186, 186, 194, alpha);
   }

   private boolean helper14(float x, float y, float width, float height, double mouseX, double mouseY) {
      return mouseX >= x && mouseX <= x + width && mouseY >= y - 2.0F && mouseY <= y + height + 2.0F;
   }

   private float helper15(float maxOffset, float phase) {
      if (maxOffset <= 0.0F) {
         return 0.0F;
      }

      float var3 = phase < 0.5F ? phase * 2.0F : 2.0F - phase * 2.0F;
      float var4 = var3 * var3 * (3.0F - 2.0F * var3);
      return maxOffset * var4;
   }

   private void helper16(DrawContext context, float centerX, float centerY, float size, int colorTheme, int alpha) {
      int var7 = ColorUtils.setAlphaColor(colorTheme, alpha);
      this.helper18(context, centerX, centerY - 2.0F, size * 0.33F, ColorUtils.setAlphaColor(-1, alpha));
   }

   private void helper17(DrawContext context, float centerX, float centerY, float size, int alpha) {
      int var6 = ColorUtils.rgba(96, 102, 118, alpha);
      int var7 = ColorUtils.rgba(16, 18, 24, alpha);
      this.helper19(context, centerX, centerY - 1.0F, size * 0.22F + 0.5F, ColorUtils.rgba(150, 156, 170, alpha));
   }

   private void helper18(DrawContext context, float centerX, float centerY, float size, int color) {
      RenderUtils.drawCheckMark(context.getMatrices(), centerX, centerY, size, 1.1F, 1.0F, color);
   }

   private void helper19(DrawContext context, float centerX, float centerY, float size, int color) {
      RenderUtils.drawLine(context.getMatrices(), centerX - size, centerY - size, centerX + size, centerY + size, 1.05F, color);
      RenderUtils.drawLine(context.getMatrices(), centerX + size, centerY - size, centerX - size, centerY + size, 1.05F, color);
   }

   private float helper20(float start, float end, float progress) {
      return start + (end - start) * progress;
   }

   private float toBooleanOrDefault2(float revealProgress, int index) {
      float var3 = Math.min(0.22F, index * 0.04F);
      if (revealProgress <= var3) {
         return 0.0F;
      }

      float var4 = (revealProgress - var3) / (1.0F - var3);
      return this.helper21(MathHelper.clamp(var4, 0.0F, 1.0F));
   }

   private float helper21(float value) {
      float var2 = MathHelper.clamp(value, 0.0F, 1.0F);
      float var3 = 1.0F - var2;
      return 1.0F - var3 * var3 * var3;
   }

   private float helper22(float value) {
      float var2 = MathHelper.clamp(value, 0.0F, 1.0F);
      return var2 < 0.5F ? 4.0F * var2 * var2 * var2 : 1.0F - (float)Math.pow(-2.0F * var2 + 2.0F, 3.0) / 2.0F;
   }

   private String helper23(Setting setting) {
      return "setting_text_" + System.identityHashCode(setting);
   }

   private Font helper24(int size) {
      return Fonts.getFont("suisse", size);
   }
}
