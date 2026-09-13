package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
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

public class ModernSettingWindow {
   private final Module module2;
   private float x2;
   private float y2;
   private final float volume = 100.0F;
   private float height2;
   private boolean showing2 = true;
   private final AnimationUtils animation2;
   private final ClickGuiSettingRenderer clickGuiSettingRenderer;
   private final ClickGuiState state2;
   private float volume2;
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT);
   private boolean flag;
   private float volume3;
   private float volume4;
   private final AnimationUtils animationUtils2 = new AnimationUtils(0.0F, 9.0F, Easings.CUBIC_OUT);
   private boolean showing3;
   private long timestamp;
   private static final long TIMESTAMP = 350L;

   public ModernSettingWindow(Module module, float x, float y, ClickGuiState state, ClickGuiSettingRenderer settingRenderer) {
      this.module2 = module;
      this.x2 = x;
      this.y2 = y;
      this.state2 = state;
      this.clickGuiSettingRenderer = settingRenderer;
      this.animation2 = new AnimationUtils(0.0F, 8.5F, Easings.BACK_OUT);
      this.animation2.setValue(0.0F);
      this.height2 = 100.0F;
   }

   public void render(DrawContext context, int mouseX, int mouseY, int colorTheme, float alphaMul) {
      if (this.showing3) {
         long var6 = System.currentTimeMillis() - this.timestamp;
         float var8 = 1.0F - Math.min(1.0F, (float)var6 / 350.0F);
         if (var6 >= 350L) {
            this.showing2 = false;
            this.showing3 = false;
            this.animation2.setValue(0.0F);
            return;
         }

         this.animation2.setValue(var8);
      } else {
         this.animation2.update(this.showing2 ? 1.0F : 0.0F);
      }

      float var27 = MathHelper.clamp(this.animation2.getValue(), 0.0F, 1.0F);
      float var7 = this.showing3 ? (1.0F - var27) * 10.0F : (1.0F - var27) * 16.0F;
      float var28 = this.x2 + var7;
      float var9 = this.y2 + (1.0F - var27) * 8.0F;
      float var10 = this.showing3 ? 0.94F + var27 * 0.06F : 0.965F + var27 * 0.035F;
      this.animationUtils.update(this.volume2);
      float var11 = this.animationUtils.getValue();
      float var12 = ClickGuiLayout.calculateSettingsHeight(this.module2, this.state2);
      float var13 = Math.max(0.0F, var12 - (this.height2 - 20.0F));
      this.volume2 = Math.max(0.0F, Math.min(var13, this.volume2));
      MatrixStack var14 = context.getMatrices();
      var14.push();
      var14.translate(var28 + 50.0F, var9 + this.height2 / 2.0F, 0.0F);
      var14.scale(var10, var10, 1.0F);
      var14.translate(-(var28 + 50.0F), -(var9 + this.height2 / 2.0F), 0.0F);
      this.helper(context.getMatrices(), var28 - 5.0F, var9 - 4.0F, 110.0F, this.height2 + 8.0F, 8.0F, colorTheme, var27 * alphaMul);
      RenderUtils.drawRoundedRect(
         context.getMatrices(),
         var28 - 5.0F,
         var9 - 4.0F,
         110.0F,
         this.height2 + 8.0F,
         8.0F,
         ColorUtils.applyAlpha(ColorUtils.rgba(19, 18, 24, 240), var27 * alphaMul)
      );
      RenderUtils.drawRoundedRect(
         context.getMatrices(), var28, var9 + 20.0F, 100.0F, 0.5F, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 35), var27 * alphaMul)
      );
      Fonts.getFont("suisse", 14)
         .draw(context.getMatrices(), this.module2.getName(), var28 + 3.0F + 1.0F, var9 + 8.0F, ColorUtils.applyAlpha(-1, var27 * alphaMul));
      float var15 = var28 + 100.0F - 13.0F;
      float var16 = var9 + 5.0F;
      float var17 = 8.0F;
      boolean var18 = HoveringUtils.isHovered(mouseX, mouseY, var15 - 3.0F, var16 - 3.0F, var17 + 6.0F, var17 + 6.0F);
      this.animationUtils2.update(var18 ? 1.0F : 0.0F);
      float var19 = this.animationUtils2.getValue();
      if (var19 > 0.01F) {
         RenderUtils.drawRoundCircle(
            context.getMatrices(),
            var15 + var17 / 2.0F,
            var16 + var17 / 2.0F,
            7.0F + 1.5F * var19,
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 70, 70, (int)(80.0F * var19)), var27 * alphaMul * var19)
         );
      }

      float var20 = 0.7F + 0.3F * var19;
      int var21 = ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, (int)(200.0F + 55.0F * var19)), var27 * alphaMul * var20);
      float var22 = 1.2F + 0.4F * var19;
      float var23 = 2.0F;
      RenderUtils.drawLine(context.getMatrices(), var15 + var23, var16 + var23, var15 + var17 - var23, var16 + var17 - var23, var22, var21);
      RenderUtils.drawLine(context.getMatrices(), var15 + var17 - var23, var16 + var23, var15 + var23, var16 + var17 - var23, var22, var21);
      List var24 = this.module2.getSettings();
      if (var24 != null && !var24.isEmpty() && ClickGuiLayout.hasVisibleSettings(var24)) {
         float var25 = var9 + 20.0F;
         float var26 = this.height2 - 20.0F;
         ScissorUtils.push();
         ScissorUtils.setFromComponentCoordinates(var28, var25, 100.0, var26);
         this.clickGuiSettingRenderer.render(context, this.module2, var28, var9 - var11, 100.0F, 1.0F, colorTheme, mouseX, mouseY, this.state2);
         ScissorUtils.pop();
      }

      float var29 = 20.0F + var12;
      this.height2 = Math.min(200.0F, Math.max(50.0F, var29));
      var14.pop();
   }

   public boolean handleClick(double mouseX, double mouseY, int button) {
      if (!this.isHovered(mouseX, mouseY)) {
         return false;
      } else {
         float var6 = this.x2 + 100.0F - 13.0F;
         float var7 = this.y2 + 5.0F;
         if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var6 - 3.0F, var7 - 3.0F, 14.0, 14.0)) {
            this.toBooleanOrDefault();
            return true;
         } else if (button == 0 && mouseY <= this.y2 + 20.0F && mouseX < this.x2 + 100.0F - 16.0F) {
            this.flag = true;
            this.volume3 = (float)(mouseX - this.x2);
            this.volume4 = (float)(mouseY - this.y2);
            return true;
         } else {
            List var8 = this.module2.getSettings();
            return var8 != null && !var8.isEmpty() ? this.toBooleanOrDefault2(mouseX, mouseY, button) : false;
         }
      }
   }

   private void toBooleanOrDefault() {
      if (!this.showing3) {
         this.showing3 = true;
         this.timestamp = System.currentTimeMillis();
      }
   }

   private boolean toBooleanOrDefault2(double mouseX, double mouseY, int button) {
      List<Setting> var6 = this.module2.getSettings();
      if (var6 != null && !var6.isEmpty()) {
         float var7 = this.animationUtils.getValue();
         float var8 = 20.0F;

         for (Setting var10 : var6) {
            if (var10 != null && var10.visible()) {
               float var11 = this.y2 + var8 + 4.0F - var7;
               if (var10 instanceof BooleanSetting var12) {
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, this.x2 + 75.0F, var11 - 2.0F, 16.0, 10.0)) {
                     var12.setState(!var12.isState());
                     return true;
                  }

                  var8 += 12.0F;
               } else if (var10 instanceof TextSetting var13) {
                  float var23 = 42.0F;
                  float var26 = this.x2 + 49.0F;
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var26, var11 - 2.5F, var23, 9.0)) {
                     this.state2.setSearchActive(false);
                     this.state2.stopSearchSelection();
                     this.state2.setEditingTextSetting(var13);
                     return true;
                  }

                  var8 += 12.0F;
               } else if (var10 instanceof FloatSetting var14) {
                  if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, this.x2 + 10.0F, var11 + 9.0F, 79.0, 6.0)) {
                     var14.setActive(true);
                     var14.setValue(this.state2.getSliderValue(var14, this.x2 + 10.0F, mouseX));
                     this.state2.beginSliderDrag(var14, mouseX);
                     return true;
                  }

                  var8 += 12.0F;
               } else if (var10 instanceof ModeSetting var15) {
                  float var22 = var11 + 10.0F;

                  for (String var28 : var15.getMods()) {
                     if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, this.x2 + 10.0F, var22 - 2.0F, 79.0, 10.0)) {
                        var15.set(var28);
                        return true;
                     }

                     var22 += 10.0F;
                  }

                  var8 += ClickGuiLayout.calculateModeSettingHeight(var15, this.state2.getModeDropdownProgress(var15));
               } else if (!(var10 instanceof ListSetting var16)) {
                  if (var10 instanceof BindSetting var17) {
                     String var21 = this.state2.getBindingSetting() == var17
                        ? "..."
                        : this.state2.toEnglish(KeyBoardUtils.getBindName(var17.getKey()));
                     float var24 = Fonts.getFont("suisse", 12).getWidth(var21) + 6.0F;
                     float var27 = this.x2 + 89.0F - var24;
                     if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var27, var11 - 2.5F, var24, 9.0)) {
                        this.state2.setBindingSetting(var17);
                        return true;
                     }

                     var8 += 12.0F;
                  }
               } else {
                  float var18 = var11 + 10.0F;

                  for (BooleanSetting var20 : var16.getSettings()) {
                     if (var20.visible()) {
                        if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, this.x2 + 10.0F, var18 - 2.0F, 79.0, 10.0)) {
                           var20.setState(!var20.isState());
                           return true;
                        }

                        var18 += 10.0F;
                     }
                  }

                  var8 += ClickGuiLayout.calculateListSettingHeight(var16, this.state2.getListDropdownProgress(var16));
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void onMouseReleased(double mouseX, double mouseY, int button) {
      this.flag = false;
      if (button == 0) {
         List<Setting> var6 = this.module2.getSettings();
         if (var6 != null) {
            for (Setting var8 : var6) {
               if (var8 instanceof FloatSetting var9) {
                  var9.setActive(false);
                  this.state2.endSliderDrag(var9);
               }
            }
         }
      }
   }

   public void onMouseDragged(double mouseX, double mouseY) {
      if (this.flag && !this.showing3) {
         this.x2 = (float)mouseX - this.volume3;
         this.y2 = (float)mouseY - this.volume4;
      } else {
         List<Setting> var5 = this.module2.getSettings();
         if (var5 != null) {
            for (Setting var7 : var5) {
               if (var7 instanceof FloatSetting var8 && var8.isActive()) {
                  var8.setValue(this.state2.updateActiveSliderValue(var8, mouseX));
               }
            }
         }
      }
   }

   public void onScroll(double verticalAmount) {
      float var3 = ClickGuiLayout.calculateSettingsHeight(this.module2, this.state2);
      float var4 = Math.max(0.0F, var3 - (this.height2 - 20.0F));
      this.volume2 -= (float)(verticalAmount * 15.0);
      this.volume2 = Math.max(0.0F, Math.min(var4, this.volume2));
   }

   public boolean isHovered(double mouseX, double mouseY) {
      return !this.showing3 && HoveringUtils.isHovered(mouseX, mouseY, this.x2, this.y2, 100.0, this.height2);
   }

   private void helper(MatrixStack matrices, float x, float y, float width, float height, float radius, int colorTheme, float alphaMul) {
      int var9 = ColorUtils.applyAlpha(colorTheme, alphaMul);
      RenderUtils.drawLiquidGlass(matrices, x, y, width, height, radius, radius, radius, radius, var9, 1.0F, 0.5F, var9, 0.3F, false, 0.15F, 0.5F, 0.2F, false);
      RenderUtils.drawBlur(matrices, x + 3.0F, y + 3.0F, width - 6.0F, height - 6.0F, 2.8F, 3.3F, var9);
   }

   public Module getModule() {
      return this.module2;
   }

   public float getX() {
      return this.x2;
   }

   public void setX(float x) {
      this.x2 = x;
   }

   public float getY() {
      return this.y2;
   }

   public void setY(float y) {
      this.y2 = y;
   }

   public float getWidth() {
      return 100.0F;
   }

   public float getHeight() {
      return this.height2;
   }

   public boolean isShowing() {
      return this.showing2 && !this.showing3;
   }

   public void setShowing(boolean showing) {
      this.showing2 = showing;
      if (!showing) {
         this.toBooleanOrDefault();
      }
   }

   public AnimationUtils getAnimation() {
      return this.animation2;
   }
}
