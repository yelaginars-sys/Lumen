package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.render.figura.FiguraAvatarIcons;
import dlc.lumen.client.render.figura.FiguraBridge;
import dlc.lumen.client.ui.clickgui.gif.GuiGifManager;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ClickGuiRenderer {
   private final ClickGuiState state2;
   private final ClickGuiSettingRenderer clickGuiSettingRenderer;
   private final ClickGuiSettingsPanel clickGuiSettingsPanel;
   private static final float VOLUME = 34.0F;
   private static final float VOLUME2 = 7.0F;
   private static final float VOLUME3 = 8.0F;
   private static final float VOLUME4 = 20.0F;
   private static final float VOLUME5 = 200.0F;
   private static final float VOLUME6 = 10.0F;
   private static final float VOLUME7 = 38.0F;
   private static final float VOLUME8 = 6.0F;
   private static final float VOLUME9 = 22.0F;
   private static final float VOLUME10 = 8.0F;
   private static final float VOLUME11 = 16.0F;
   private static final float VOLUME12 = 52.0F;
   private static final float VOLUME13 = 14.0F;

   public static float getCategoryIconSize() {
      return 22.0F;
   }

   public static float getCategoryIconGap() {
      return 8.0F;
   }

   public static float getCategoriesTop() {
      return 8.0F;
   }

   public ClickGuiRenderer(ClickGuiState state, ClickGuiSettingRenderer settingRenderer, ClickGuiSettingsPanel settingsPanel) {
      this.state2 = state;
      this.clickGuiSettingRenderer = settingRenderer;
      this.clickGuiSettingsPanel = settingsPanel;
   }

   public void render(DrawContext context, int mouseX, int mouseY, Window screenWindow, float animationProgress) {
      if (screenWindow != null) {
         float var6 = MathHelper.clamp(animationProgress, 0.0F, 1.0F);
         int var7 = ColorUtils.clientAccent();
         GuiGifManager.get().renderScreenBottomLeft(context, screenWindow.getScaledWidth(), screenWindow.getScaledHeight(), var6);
         float var8 = this.state2.getCategoryTransitionProgress();
         float var9 = this.helper10(var6);
         float var10 = this.state2.getMenuWidth();
         float var11 = this.state2.getMenuHeight();
         float var12 = this.state2.getX();
         float var13 = this.state2.getY() + this.state2.getRenderOffsetY();
         float var14 = var12 + var10 / 2.0F;
         float var15 = var13 + var11 / 2.0F;
         float var16 = 0.955F + 0.045F * var9;
         float var17 = this.helper9(var6, 0.08F, 0.52F);
         float var18 = this.helper9(var6, 0.14F, 0.58F);
         float var19 = this.helper9(var6, 0.18F, 0.68F);
         float var20 = ClickGuiTheme.opacity();
         MatrixStack var21 = context.getMatrices();
         var21.push();
         var21.translate(var14, var15, 0.0F);
         var21.scale(var16, var16, 1.0F);
         var21.translate(-var14, -var15, 0.0F);
         boolean var22 = true;
         float var23 = ClickGuiTheme.opacity();
         boolean var24 = ClickGuiTheme.glass();
         float var25 = ClickGuiTheme.blurMul();
         int var26 = MathHelper.clamp((int)(206.0F * var23), 0, 255);
         RenderUtils.drawShadow(
            context.getMatrices(),
            var12 + 3.0F,
            var13 + 3.0F,
            var10 - 6.0F,
            var11 - 6.0F,
            ClickGuiTheme.radius(9.0F),
            3.3F,
            ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 45, 255), var6)
         );
         if (var24) {
            RenderUtils.drawBlur(
               context.getMatrices(),
               var12 + 3.0F,
               var13 + 3.0F,
               var10 - 6.0F,
               var11 - 6.0F,
               ClickGuiTheme.radius(9.0F),
               2.0F * var25,
               ColorUtils.applyAlpha(ColorUtils.rgba(60, 60, 55, 255), var6)
            );
         }

         RenderUtils.drawRoundedRect(
            context.getMatrices(),
            var12 + 3.0F,
            var13 + 3.0F,
            var10 - 6.0F,
            var11 - 6.0F,
            ClickGuiTheme.radius(9.0F),
            ColorUtils.applyAlpha(ColorUtils.clientAccent(), var6)
         );
         if (var24) {
            RenderUtils.drawBlur(
               context.getMatrices(),
               var12 + 3.0F,
               var13 + 3.0F,
               var10 - 6.0F,
               var11 - 6.0F,
               ClickGuiTheme.radius(9.0F),
               2.0F * var25,
               ColorUtils.applyAlpha(ColorUtils.rgba(60, 60, 60, 215), var6)
            );
         }

         RenderUtils.drawRoundedRect(
            context.getMatrices(),
            var12 + 3.0F,
            var13 + 3.0F,
            var10 - 6.0F,
            var11 - 6.0F,
            ClickGuiTheme.radius(9.0F),
            ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, var26), var6)
         );
         RenderUtils.drawRoundedRectOutline(
            context.getMatrices(),
            var12 + 3.0F,
            var13 + 3.0F,
            var10 - 6.0F,
            var11 - 6.0F,
            ClickGuiTheme.radius(9.0F),
            0.1F,
            ColorUtils.applyAlpha(var7, var6),
            ColorUtils.applyAlpha(var7, var6),
            ColorUtils.applyAlpha(var7, var6),
            ColorUtils.applyAlpha(var7, var6)
         );
         if (!this.state2.isProfilePanelOpen()
            && !this.state2.isFriendsPanelOpen()
            && !this.state2.isSettingsPanelOpen()
            && !this.state2.isAutosetPanelOpen()) {
            this.helper3(context, var12, var13, var10, var7, var6, var17);
            this.helper2(context, var12, var13, var10, var11, mouseX, mouseY, var7, var6, var8, var18);
            this.helper5(context, var12, var13, mouseX, mouseY, var7, var6, var8, var10, var11, var19);
         }

         this.clickGuiSettingsPanel.renderButton(context, this.state2, mouseX, mouseY, var6);
         this.clickGuiSettingsPanel.render(context, this.state2, screenWindow, mouseX, mouseY, var6);
         this.helper12(context, var12, var13, var10, var11, var6);
         var21.pop();
         this.helper(context, mouseX, mouseY, var7);
      }
   }

   private void helper(DrawContext context, int mouseX, int mouseY, int colorTheme) {
      BooleanSetting var5 = this.state2.getBindPopupSetting();
      if (var5 != null) {
         MatrixStack var6 = context.getMatrices();
         float[] var7 = this.state2.bindPopupRect();
         float var8 = var7[0];
         float var9 = var7[1];
         float var10 = var7[2];
         float var11 = var7[3];
         float var12 = this.state2.bindPopupPad();
         boolean var13 = this.state2.getBindingBooleanSetting() == var5;
         float[] var14 = this.state2.bindPopupPillRect();
         boolean var15 = HoveringUtils.isHovered(mouseX, mouseY, var14[0], var14[1], var14[2], var14[3]);
         RenderUtils.drawRoundedRect(
            var6,
            var14[0],
            var14[1],
            var14[2],
            var14[3],
            3.0F,
            ColorUtils.rgba(!var15 && !var13 ? 40 : 52, !var15 && !var13 ? 40 : 52, !var15 && !var13 ? 46 : 60, 255)
         );
         RenderUtils.drawRoundedRectOutline(var6, var14[0], var14[1], var14[2], var14[3], 3.0F, 0.5F, colorTheme, colorTheme, colorTheme, colorTheme);
         Font var16 = Fonts.getFont("suisse", 8);
         String var17 = var13 ? "..." : this.state2.toEnglish(KeyBoardUtils.getBindName(var5.getKey()));
         var16.draw(
            var6,
            var17,
            var14[0] + (var14[2] - var16.getWidth(var17)) / 2.0F,
            var14[1] + 4.0F + (var14[3] - var16.getHeight()) / 2.0F,
            ColorUtils.rgba(235, 235, 240, 255)
         );
      }
   }

   private void helper2(
      DrawContext context,
      float x,
      float y,
      float menuWidth,
      float menuHeight,
      int mouseX,
      int mouseY,
      int colorTheme,
      float alphaMul,
      float transitionProgress,
      float revealProgress
   ) {
      MatrixStack var12 = context.getMatrices();
      List var13 = ClickGuiLayout.CATEGORY_ORDER;
      float var14 = 30.0F;
      float var15 = var13.size() * var14 - 8.0F;
      float var16 = x + (menuWidth - var15) / 2.0F;
      float var17 = y + menuHeight - 22.0F - 16.0F + (1.0F - revealProgress) * 10.0F;
      int var18 = Math.max(0, var13.indexOf(this.state2.getCurrentCategory()));
      float var19 = this.state2.getAnimatedCategoryIndex(var18);
      this.state2.recordCategoryIndex(var19);
      float var20 = var16 + var19 * var14;
      float var21 = this.state2.getCategoryTransitionDirection() * (1.0F - this.helper10(transitionProgress)) * 12.0F;
      var20 += var21;
      float var22 = var20 + 11.0F;
      RenderUtils.drawRoundedRect(
         var12, var16 - 10.0F, var17 - 4.0F, var15 + 20.0F, 30.0F, ClickGuiTheme.radius(8.0F), ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 150), alphaMul)
      );
      RenderUtils.drawRoundedRectOutline(
         context.getMatrices(),
         var16 - 10.0F,
         var17 - 4.0F,
         var15 + 20.0F,
         30.0F,
         ClickGuiTheme.radius(9.0F),
         0.1F,
         ColorUtils.applyAlpha(colorTheme, alphaMul),
         ColorUtils.applyAlpha(colorTheme, alphaMul),
         ColorUtils.applyAlpha(colorTheme, alphaMul),
         ColorUtils.applyAlpha(colorTheme, alphaMul)
      );
      RenderUtils.drawRoundedRectOutline(
         var12,
         var20 - 5.0F,
         var17 - 3.0F,
         32.0F,
         28.0F,
         ClickGuiTheme.radius(6.0F),
         0.1F,
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 22), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 22), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 22), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 22), alphaMul)
      );
      RenderUtils.drawLiquidGlass(
         var12,
         var20 - 5.0F,
         var17 - 3.0F,
         32.0F,
         28.0F,
         4.0F,
         4.0F,
         4.0F,
         4.0F,
         ClickGuiTheme.accent(),
         1.0F,
         1.35F,
         ColorUtils.rgba(255, 255, 255, 255),
         0.98F,
         false,
         0.62F,
         0.1F,
         0.2F,
         false
      );
      float var23 = var17 - 7.0F;
      float var24 = 3.5F;
      int var25 = ColorUtils.applyAlpha(colorTheme, alphaMul);
      Font var26 = Fonts.getFont("icon", 18);

      for (int var27 = 0; var27 < var13.size(); var27++) {
         Module.ModuleCategory var28 = (Module.ModuleCategory)var13.get(var27);
         float var29 = var16 + var27 * var14 + var21 * 0.35F;
         boolean var30 = var27 == var18;
         boolean var31 = HoveringUtils.isHovered(mouseX, mouseY, var29, var17, 22.0, 22.0);
         int var32 = var30 ? colorTheme : (var31 ? ColorUtils.rgba(255, 255, 255, 220) : ColorUtils.rgba(255, 255, 255, 150));
         int var33 = ColorUtils.applyAlpha(var32, alphaMul);
         var26.draw(var12, var28.getIcons(), var29 + 11.0F - 7.0F + 2.0F, var17 + 11.0F - 7.0F + 4.0F + 1.0F, var33);
      }
   }

   private void helper3(DrawContext context, float x, float y, float menuWidth, int colorTheme, float alphaMul, float revealProgress) {
      float var8 = x + (menuWidth - 200.0F) / 2.0F;
      float var9 = y + 10.0F + (1.0F - revealProgress) * 12.0F;
      float var10 = 200.0F;
      float var11 = 20.0F;
      RenderUtils.drawRoundedRect(
         context.getMatrices(),
         var8 + 1.0F,
         var9 + 1.0F,
         var10 - 2.0F,
         var11 - 2.0F,
         ClickGuiTheme.radius(9.0F),
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 180), alphaMul)
      );
      RenderUtils.drawRoundedRectOutline(
         context.getMatrices(),
         var8 + 1.0F,
         var9 + 1.0F,
         var10 - 2.0F,
         var11 - 2.0F,
         ClickGuiTheme.radius(9.0F),
         0.1F,
         ColorUtils.applyAlpha(colorTheme, alphaMul),
         ColorUtils.applyAlpha(colorTheme, alphaMul),
         ColorUtils.applyAlpha(colorTheme, alphaMul),
         ColorUtils.applyAlpha(colorTheme, alphaMul)
      );
      String var12 = this.state2.getSearchText();
      String var13;
      if (!var12.isEmpty()) {
         var13 = var12;
      } else if (this.state2.isSearchActive()) {
         var13 = "Search" + ".".repeat(this.helper4());
      } else {
         var13 = "Search...";
      }

      int var14 = ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 160), alphaMul);
      Font var15 = Fonts.getFont("icon1", 12);
      var15.draw(context.getMatrices(), "l", var8 + 8.0F, var9 + 7.0F + 2.0F, ColorUtils.applyAlpha(colorTheme, 0.5F * alphaMul));
      Font var16 = Fonts.getFont("suisse", 10);
      var16.draw(context.getMatrices(), var13, var8 + 22.0F, var9 + 7.0F + 2.0F, var14);
      if (!var12.isEmpty()) {
         Font var17 = Fonts.getFont("icon1", 11);
         var17.draw(context.getMatrices(), "m", var8 + var10 - 16.0F, var9 + 7.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 100), alphaMul));
      }
   }

   private int helper4() {
      int[] var1 = new int[]{1, 2, 3, 2};
      int var2 = (int)(System.currentTimeMillis() / 350L % var1.length);
      return var1[var2];
   }

   private void helper5(
      DrawContext context,
      float x,
      float y,
      int mouseX,
      int mouseY,
      int colorTheme,
      float alphaMul,
      float transitionProgress,
      float menuWidth,
      float menuHeight,
      float revealProgress
   ) {
      float var12 = this.helper10(transitionProgress);
      float var13 = (1.0F - revealProgress) * 18.0F;
      float var14 = this.state2.getCategoryTransitionDirection() * (1.0F - var12) * 20.0F;
      float var15 = x + 10.0F + var14;
      float var16 = y + 38.0F + var13;
      float var17 = menuWidth - 20.0F;
      float var18 = menuHeight - 38.0F - 52.0F;
      int var19 = this.state2.getModuleColumns();
      float var20 = (var17 - 8.0F * (var19 - 1)) / var19;
      float var21 = this.state2.getAnimatedModuleCellWidth(var20);
      this.state2.recordCellWidth(var21);
      List var22 = this.state2.getModules(this.state2.getCurrentCategory());
      boolean var23 = !this.state2.getSearchText().isBlank();
      float var24;
      if (!var23 && this.state2.getCurrentCategory() == Module.ModuleCategory.MODELS) {
         var24 = ClickGuiModelsLayout.calculateTotalHeight(this.state2, var17);
      } else if (!var23 && this.state2.getCurrentCategory() == Module.ModuleCategory.AUTOBUY) {
         var24 = AutoBuyTab.calcTotalHeight(var17);
      } else {
         var24 = this.helper11(var22, var19);
      }

      float var25 = Math.max(0.0F, var24 - var18);
      this.state2.setMaxScrollValue(var25);
      this.state2.updateScrollAnimation();
      float var26 = this.state2.getAnimatedScrollValue();
      var26 = MathHelper.clamp(var26, 0.0F, var25);
      this.state2.recordScroll(var26);
      MatrixStack var27 = context.getMatrices();
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(var15, var16, var17, var18);
      if (!var23 && this.state2.getCurrentCategory() == Module.ModuleCategory.MODELS) {
         this.helper6(context, var27, var15, var16, var17, var20, var21, var18, var26, colorTheme, alphaMul, mouseX, mouseY);
      } else if (!var23 && this.state2.getCurrentCategory() == Module.ModuleCategory.AUTOBUY) {
         AutoBuyTab.render(context, var15, var16, var17, var18, var26, colorTheme, alphaMul, mouseX, mouseY);
      } else {
         this.helper7(
            context, var27, var22, var15, var16, var17, var20, var21, var19, var18, var26, colorTheme, alphaMul, mouseX, mouseY, transitionProgress
         );
      }

      ScissorUtils.pop();
   }

   private void helper6(
      DrawContext context,
      MatrixStack matrices,
      float moduleX,
      float moduleY,
      float moduleWidth,
      float targetCellWidth,
      float cellWidth,
      float visibleHeight,
      float scrollOffset,
      int colorTheme,
      float alphaMul,
      int mouseX,
      int mouseY
   ) {
      int var14 = (int)moduleX;
      int var15 = (int)(moduleY + 10.0F);
      int var16 = (int)(moduleX + moduleWidth);
      int var17 = (int)(moduleY + visibleHeight);
      List var18 = FiguraBridge.listAvatarInfos();
      int var19 = Math.max(1, this.state2.getModuleColumns());
      int var20 = var18.size() + 1;

      for (int var21 = 0; var21 < var20; var21++) {
         float[] var22 = ClickGuiModelsLayout.gridCardRect(var21, moduleX, moduleY, targetCellWidth, cellWidth, scrollOffset, var19);
         float var23 = var22[0];
         float var24 = var22[1];
         float var25 = var22[2];
         float var26 = var22[3];
         if (!(var24 + var26 < moduleY + 11.0F) && !(var24 > moduleY + visibleHeight)) {
            boolean var27 = var21 == 0;
            FiguraBridge.AvatarInfo var28 = var27 ? null : (FiguraBridge.AvatarInfo)var18.get(var21 - 1);
            boolean var29 = var27 ? FiguraBridge.isNoneSelected() : FiguraBridge.isSelectedAny(var28.id());
            float[] var30 = this.helper13(matrices, var23, var24, var25, var26, var29, colorTheme, alphaMul);
            float var31 = var30[0];
            float var32 = var30[1];
            float var33 = var30[2];
            float var34 = var30[3];
            float var35 = var31 + var33 * 0.5F;
            if (var27) {
               MatrixStack var36 = context.getMatrices();
               var36.push();
               var36.translate(var35, var32 + var34 * 0.5F, 0.0F);
               var36.scale(2.6F, 2.6F, 1.0F);
               context.drawItem(new ItemStack(Items.BARRIER), -8, -8);
               var36.pop();
            } else {
               Identifier var38 = FiguraAvatarIcons.icon(var28);
               if (var38 != null) {
                  float var37 = Math.min(var33, var34) - 6.0F;
                  context.enableScissor(var14, var15, var16, var17);
                  RenderUtils.drawImage(
                     matrices, var38, var35 - var37 / 2.0F, var32 + var34 * 0.5F - var37 / 2.0F, var37, var37, ColorUtils.applyAlpha(-1, alphaMul)
                  );
                  context.disableScissor();
                  ScissorUtils.reapply();
               } else {
                  MatrixStack var40 = context.getMatrices();
                  var40.push();
                  var40.translate(var35, var32 + var34 * 0.5F, 0.0F);
                  var40.scale(2.2F, 2.2F, 1.0F);
                  context.drawItem(new ItemStack(Items.ARMOR_STAND), -8, -8);
                  var40.pop();
               }
            }

            Font var39 = Fonts.getFont("suisse", 12);
            String var41 = var27 ? "Снять" : var28.name();
            var39.draw(matrices, var41, var23 + 8.0F, var24 + 95.0F, ColorUtils.applyAlpha(-1, alphaMul));
            this.helper14(matrices, var23, var24, var29, colorTheme, alphaMul);
         }
      }
   }

   private void helper7(
      DrawContext context,
      MatrixStack matrices,
      List<Module> modules,
      float moduleX,
      float moduleY,
      float moduleWidth,
      float targetCellWidth,
      float cellWidth,
      int columns,
      float visibleHeight,
      float scrollOffset,
      int colorTheme,
      float alphaMul,
      int mouseX,
      int mouseY,
      float transitionProgress
   ) {
      int var17 = 0;
      float[] var18 = new float[Math.max(1, columns)];

      for (Module var20 : modules) {
         int var21 = var17 % columns;
         float var22 = var21 * (targetCellWidth + 8.0F);
         float var23 = this.state2.getOpenProgress(var20);
         float var24 = ClickGuiLayout.getInlineModuleHeight(var20, var23, this.state2);
         float var25 = var18[var21];
         float var26 = moduleX + this.state2.getAnimatedModuleLocalX(var20, var22);
         float var27 = moduleY + this.state2.getAnimatedModuleLocalY(var20, var25) - scrollOffset;
         var18[var21] += var24 + 7.0F * ClickGuiTheme.density();
         this.state2.recordModuleBounds(var20, var26, var27, cellWidth, var24, var23);
         float var28 = this.state2.getModuleAppearProgress(var20);
         float var29 = Math.min(0.55F, var17 * 0.03F);
         float var30 = MathHelper.clamp((var28 - var29) / Math.max(0.15F, 1.0F - var29), 0.0F, 1.0F);
         if (Float.isNaN(var30)) {
            var30 = 1.0F;
         }

         var30 = this.helper10(var30);
         float var31 = 0.9F + var30 * 0.1F;
         float var32 = 0.94F + var30 * 0.06F;
         float var33 = var30;
         float var34 = 0.72F + 0.28F * this.helper10(transitionProgress);
         float var35 = alphaMul * var33 * var34;
         if (var27 + var24 >= moduleY - 50.0F && var27 <= moduleY + visibleHeight + 50.0F && var35 > 0.005F) {
            matrices.push();
            if (var30 < 0.99F) {
               matrices.translate(var26 + cellWidth / 2.0F, var27 + var24 / 2.0F, 0.0F);
               matrices.scale(var32, var31, 1.0F);
               matrices.translate(-(var26 + cellWidth / 2.0F), -(var27 + var24 / 2.0F), 0.0F);
            }

            RenderUtils.drawRoundedRect(
               matrices, var26, var27, cellWidth, var24, ClickGuiTheme.radius(7.0F), ColorUtils.applyAlpha(ColorUtils.rgba(30, 30, 35, 77), var35)
            );
            String var36 = "";
            if (this.state2.getBindingModule() == var20) {
               var36 = " [...]";
            } else if (var20.getKey() != -1) {
               var36 = " [" + this.state2.toEnglish(KeyBoardUtils.getBindName(var20.getKey())) + "]";
            }

            Font var37 = Fonts.getFont("suisse", 12);
            boolean var38 = HoveringUtils.isHovered(mouseX, mouseY, var26, var27, cellWidth, var24);
            float var39 = var38 ? 1.0F : 0.0F;
            int var40 = ColorUtils.applyAlpha(var20.isEnable() ? -1 : ColorUtils.rgba(255, 255, 255, 191), (0.75F + 0.25F * var39) * var35);
            int var41 = ColorUtils.applyAlpha(var20.isEnable() ? colorTheme : ColorUtils.rgba(255, 255, 255, 191), (0.75F + 0.25F * var39) * var35);
            Font var42 = Fonts.getFont("icon", 12);
            var42.draw(matrices, var20.getCategory().getIcons(), var26 + 8.0F, var27 + 8.5F + 2.0F, var41);
            var37.draw(matrices, var20.getName(), var26 + 20.0F - 3.0F, var27 + 10.0F, var40);
            if (!var36.isEmpty()) {
               float var43 = var37.getWidth(var20.getName());
               Font var44 = Fonts.getFont("suisse", 10);
               var44.draw(matrices, var36, var26 + 20.0F + var43, var27 + 12.0F - 2.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 128), var35));
            }

            String var50 = var20.getDisplayDescription();
            if (var50 != null && !var50.isBlank() && !"NULLABLE".equalsIgnoreCase(var50)) {
               Font var51 = Fonts.getFont("suisse", 11);
               float var45 = Math.max(20.0F, cellWidth - 18.0F - 26.0F);
               int var46 = ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 128), 0.5F * var35);
               this.helper8(matrices, var51, var50, var26 + 8.0F, var27 + 22.0F, var45, var46, var38, "moduledesc_" + var20.getName());
            }

            if (var23 > 0.01F && var20.getSettings() != null && !var20.getSettings().isEmpty()) {
               float var52 = var23 * var23 * (3.0F - 2.0F * var23);
               float var54 = var27 + 34.0F - 0.5F;
               RenderUtils.drawRoundedRect(
                  matrices,
                  var26 + 6.0F,
                  var54,
                  Math.max(0.0F, cellWidth - 12.0F),
                  0.75F,
                  0.0F,
                  ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 28), var35 * var52)
               );
               this.clickGuiSettingRenderer
                  .render(
                     context, var20, var26, ClickGuiLayout.getInlineSettingsOriginY(var27), cellWidth, var23, colorTheme, mouseX, mouseY, this.state2
                  );
            }

            float var53 = var26 + cellWidth - 30.0F;
            float var55 = var27 + 13.0F;
            float var56 = this.state2.getModuleToggleProgress(var20);
            int var47 = ColorUtils.applyAlpha(ColorUtils.interpolate(ColorUtils.rgba(50, 50, 55, 200), colorTheme, var56), var35);
            RenderUtils.drawRoundedRect(matrices, var53, var55, 17.0F, 8.0F, 3.0F, var47);
            float var48 = var53 + 1.0F + 6.0F * var56;
            RenderUtils.drawRoundedRect(matrices, var48, var55 + 1.0F, 9.0F, 6.0F, 2.0F, ColorUtils.applyAlpha(-1, var35));
            matrices.pop();
         }

         var17++;
      }
   }

   private void helper8(MatrixStack matrices, Font font, String text, float x, float y, float maxWidth, int color, boolean hovered, String key) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         float var10 = font.getWidth(text);
         if (var10 <= maxWidth) {
            font.draw(matrices, text, x, y, color);
         } else {
            float var11 = var10 - maxWidth;
            float var12 = this.state2.advanceTextScrollPhase(key, hovered);
            boolean var13 = this.state2.isTextScrollActive(key, hovered);
            AnimationUtils var14 = this.state2.getTextHoverAnimation(key, var13);
            var14.update(var13 ? 1.0F : 0.0F);
            float var15 = var14.getValue();
            float var16 = var12 < 0.5F ? var12 * 2.0F : 2.0F - var12 * 2.0F;
            float var17 = var16 * var16 * (3.0F - 2.0F * var16);
            float var18 = var11 * var17 * var15;
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, y - 2.0F, maxWidth, font.getHeight() + 4.0F);
            font.draw(matrices, text, x - var18, y, color);
            ScissorUtils.pop();
         }
      }
   }

   private float helper9(float progress, float start, float span) {
      return this.helper10(MathHelper.clamp((progress - start) / Math.max(0.001F, span), 0.0F, 1.0F));
   }

   private float helper10(float value) {
      float var2 = MathHelper.clamp(value, 0.0F, 1.0F);
      return 1.0F - (float)Math.pow(1.0F - var2, 3.0);
   }

   private float helper11(List<Module> modules, int columns) {
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

      return Math.max(0.0F, var9);
   }

   private int resolveInt() {
      return ClickGuiTheme.accent();
   }

   private void helper12(DrawContext context, float x, float y, float menuWidth, float menuHeight, float alphaMul) {
      float var7 = this.state2.getResizeHandleSize();
      if (!(var7 <= 0.0F)) {
         float var8 = x + menuWidth - var7 - 5.0F;
         float var9 = y + menuHeight - var7 - 5.0F;
         int var10 = ColorUtils.applyAlpha(ClickGuiTheme.accent(), alphaMul);
         RenderUtils.drawLine(context.getMatrices(), var8 + 2.0F - 1.0F, var9 + var7 - 1.0F - 1.0F, var8 + var7 - 1.0F - 1.0F, var9 + 2.0F - 1.0F, 1.0F, var10);
         RenderUtils.drawLine(context.getMatrices(), var8 + 4.5F - 1.0F, var9 + var7 - 1.0F - 1.0F, var8 + var7 - 1.0F - 1.0F, var9 + 4.5F - 1.0F, 1.0F, var10);
         RenderUtils.drawLine(context.getMatrices(), var8 + 7.0F - 1.0F, var9 + var7 - 1.0F - 1.0F, var8 + var7 - 1.0F - 1.0F, var9 + 7.0F - 1.0F, 1.0F, var10);
      }
   }

   private float[] helper13(MatrixStack matrices, float cx, float cy, float cw, float ch, boolean active, int colorTheme, float alphaMul) {
      this.helper15(matrices, cx, cy, cw, ch, 8.0F, colorTheme, alphaMul * (active ? 1.0F : 0.88F));
      float var9 = cx + 8.0F;
      float var10 = cy + 8.0F;
      float var11 = cw - 16.0F;
      float var12 = 76.0F;
      RenderUtils.drawRoundedRect(matrices, var9, var10, var11, var12, 6.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 18), alphaMul));
      return new float[]{var9, var10, var11, var12};
   }

   private void helper14(MatrixStack matrices, float cx, float cy, boolean active, int colorTheme, float alphaMul) {
      Font var7 = Fonts.getFont("suisse", 10);
      var7.draw(
         matrices,
         active ? "Selected" : "Click to use",
         cx + 8.0F,
         cy + 108.0F,
         ColorUtils.applyAlpha(active ? colorTheme : ColorUtils.rgba(255, 255, 255, 128), alphaMul)
      );
   }

   private void updateState(MatrixStack matrices, float x, float y, float width, float height, float radius, int colorTheme, float alphaMul) {
      int var9 = ColorUtils.applyAlpha(colorTheme, alphaMul);
      int var10 = ColorUtils.rgba(10, 10, 10, 150);
      float var11 = 3.5F * ClickGuiTheme.blurMul();
      RenderUtils.drawBlur(matrices, x + 4.0F, y + 4.0F, width - 8.0F, height - 8.0F, 8.0F, var11, var9);
      RenderUtils.drawLiquidGlass(
         matrices,
         x + 4.0F,
         y + 4.0F,
         width - 8.0F,
         height - 8.0F,
         4.0F,
         4.0F,
         4.0F,
         4.0F,
         ClickGuiTheme.accent(),
         1.0F,
         1.35F,
         ColorUtils.rgba(255, 255, 255, 255),
         0.98F,
         false,
         0.62F,
         0.1F,
         0.2F,
         false
      );
      RenderUtils.drawRoundedRect(matrices, x + 4.0F, y + 4.0F, width - 8.0F, height - 8.0F, 8.0F, var10);
   }

   private void helper15(MatrixStack matrices, float x, float y, float width, float height, float radius, int colorTheme, float alphaMul) {
      int var9 = ColorUtils.applyAlpha(colorTheme, alphaMul);
      int var10 = ColorUtils.rgba(10, 10, 10, 150);
      float var11 = 3.5F * ClickGuiTheme.blurMul();
      float var12 = ClickGuiTheme.opacity();
      RenderUtils.drawBlur(matrices, x + 4.0F, y + 4.0F, width - 8.0F, height - 8.0F, 4.0F, var11, var9);
      RenderUtils.drawLiquidGlass(
         matrices,
         x + 4.0F,
         y + 4.0F,
         width - 8.0F,
         height - 8.0F,
         4.0F,
         4.0F,
         4.0F,
         4.0F,
         ClickGuiTheme.accent(),
         1.0F,
         1.35F,
         ColorUtils.rgba(255, 255, 255, 255),
         0.98F,
         false,
         0.62F,
         0.1F,
         0.2F,
         false
      );
      RenderUtils.drawRoundedRect(matrices, x + 4.0F, y + 4.0F, width - 8.0F, height - 8.0F, 4.0F, var10);
      boolean var13 = true;
      RenderUtils.drawRoundedRect(
         matrices,
         x + 4.0F,
         y + 4.0F,
         width - 8.0F,
         height - 8.0F,
         4.0F,
         ColorUtils.applyAlpha(var13 ? ColorUtils.rgba(20, 20, 25, (int)(248.0F * var12)) : ColorUtils.rgba(240, 240, 245, (int)(242.0F * var12)), alphaMul)
      );
   }
}
