package dlc.lumen.client.ui.clickgui;

import dlc.lumen.Lumen;
import dlc.lumen.api.storages.implement.ClientColors;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import dlc.lumen.client.render.figura.FiguraAvatarIcons;
import dlc.lumen.client.render.figura.FiguraBridge;
import dlc.lumen.client.render.figura.FriendModelManager;
import dlc.lumen.client.render.models.CustomModelManager;
import dlc.lumen.client.render.models.CustomModelType;
import dlc.lumen.client.ui.clickgui.gif.GuiGifManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;

public class ClickGuiSettingsPanel {
   private static final float VOLUME = 20.0F;
   private static final float VOLUME2 = 9.0F;
   private static final float VOLUME3 = 9.0F;
   private static final float VOLUME4 = 35.0F;
   private static final float VOLUME5 = 61.0F;
   private static final float VOLUME6 = 87.0F;
   private static final float VOLUME7 = 150.0F;
   private static final float VOLUME8 = 12.0F;
   private static final float VOLUME9 = 24.0F;
   private static final float VOLUME10 = 4.0F;
   private static final float VOLUME11 = 104.0F;
   private static final float VOLUME12 = 12.0F;
   private static final float VOLUME13 = 22.0F;
   private static final float VOLUME14 = 20.0F;
   private int index2 = 0;
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4 = 1.0F;
   private int index3 = 0;
   private boolean flag = false;
   private boolean colorsTextFocus = false;
   private final StringBuilder stringBuilder = new StringBuilder();
   private static final float VOLUME15 = 16.0F;
   private static final float VOLUME16 = 3.0F;
   private static final float VOLUME17 = 100.0F;
   private static final float VOLUME18 = 18.0F;
   private static final float VOLUME19 = 3.0F;
   private float volume5 = 0.0F;
   private float volume6 = 0.0F;
   private float volume7 = 0.0F;
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 14.0F, Easings.CUBIC_OUT);
   private int index4 = 0;
   private static final float VOLUME20 = 150.0F;
   private static final float VOLUME21 = 11.0F;
   private static final float VOLUME22 = 88.0F;
   private static final float VOLUME23 = 10.0F;
   private static final float VOLUME24 = 26.0F;
   private static final float VOLUME25 = 17.0F;
   private final ClickGuiSettingsPanel.Slider[] value = new ClickGuiSettingsPanel.Slider[]{
      new ClickGuiSettingsPanel.Slider("Прозрачность", 0.1F, 1.0F, ClickGuiTheme::opacity, ClickGuiTheme::setOpacity),
      new ClickGuiSettingsPanel.Slider("Сила блюра", 0.0F, 8.0F, ClickGuiTheme::blurMul, ClickGuiTheme::setBlur),
      new ClickGuiSettingsPanel.Slider("Скругление", 0.0F, 1.0F, ClickGuiTheme::radiusMul, ClickGuiTheme::setRadiusMul),
      new ClickGuiSettingsPanel.Slider("Плотность", 0.5F, 2.0F, ClickGuiTheme::density, ClickGuiTheme::setDensity)
   };
   private final ClickGuiSettingsPanel.Toggle[] value2 = new ClickGuiSettingsPanel.Toggle[]{
      new ClickGuiSettingsPanel.Toggle("Анимации", ClickGuiTheme::animationsEnabled, ClickGuiTheme::setAnimations),
      new ClickGuiSettingsPanel.Toggle("Тень", ClickGuiTheme::shadowEnabled, ClickGuiTheme::setShadow)
   };
   private int index5 = -1;
   private final AutoSetPage autoSetPage = new AutoSetPage();
   private static final float VOLUME26 = 10.0F;
   private static final float VOLUME27 = 20.0F;
   private static final float VOLUME28 = 16.0F;
   private static final float VOLUME29 = 4.0F;
   private static final float VOLUME30 = 18.0F;
   private static final float VOLUME31 = 15.0F;
   private int index6 = 0;
   private String text2 = null;
   private ClickGuiSettingsPanel.SocFocus socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
   private final StringBuilder stringBuilder2 = new StringBuilder();
   private final StringBuilder stringBuilder3 = new StringBuilder();
   private final StringBuilder stringBuilder4 = new StringBuilder();
   private float volume8;
   private float volume9;
   private float volume10;
   private float volume11;
   private float volume12;
   private float volume13;
   private final AnimationUtils animationUtils2 = new AnimationUtils(0.0F, 11.0F, Easings.CUBIC_OUT);
   private int index7 = -1;
   private static final float VOLUME32 = 12.0F;
   private static final float VOLUME33 = 24.0F;
   private static final float VOLUME34 = 14.0F;
   private static final float VOLUME35 = 6.0F;

   private float helper(ClickGuiState s) {
      return this.helper16(s) + 10.0F + 150.0F + 11.0F;
   }

   private float helper2(ClickGuiState s) {
      return this.helper16(s) + this.helper17(s) - 10.0F - this.helper(s);
   }

   private float helper3(ClickGuiState s) {
      return this.helper18(s) + 8.0F;
   }

   private float helper4(ClickGuiState s) {
      return this.helper3(s) + 18.0F + 6.0F;
   }

   private float helper5(ClickGuiState s) {
      return this.helper4(s) + 88.0F;
   }

   private float[] helper6(ClickGuiState s) {
      float var2 = this.helper16(s) + 10.0F;
      float var3 = this.helper18(s) + 8.0F;
      return new float[]{var2, var3, 150.0F, this.helper19(s) - 8.0F - var3};
   }

   private float[] helper7(ClickGuiState s, int i) {
      int var3 = FiguraBridge.Category.values().length;
      float var4 = (this.helper2(s) - 3.0F * (var3 - 1)) / var3;
      float var5 = this.helper(s) + i * (var4 + 3.0F);
      return new float[]{var5, this.helper3(s), var4, 18.0F};
   }

   private ItemStack helper8(FiguraBridge.Category c) {
      return switch (c) {
         case WEAPON -> new ItemStack(Items.NETHERITE_SWORD);
         case PET -> new ItemStack(Items.BONE);
         case HEAD -> new ItemStack(Items.NETHERITE_HELMET);
         default -> new ItemStack(Items.ARMOR_STAND);
      };
   }

   private String resolveString(Font f, String s, float maxWidth) {
      if (s != null && !s.isEmpty() && !(maxWidth <= 0.0F) && !(f.getWidth(s) <= maxWidth)) {
         String var4 = "…";

         for (int var5 = s.length(); var5 > 0; var5--) {
            String var6 = s.substring(0, var5) + var4;
            if (f.getWidth(var6) <= maxWidth) {
               return var6;
            }
         }

         return "";
      } else {
         return s == null ? "" : s;
      }
   }

   private float helper9(ClickGuiState s) {
      return s.getX();
   }

   private float helper10(ClickGuiState s) {
      return s.getY() + s.getRenderOffsetY();
   }

   private float helper11(ClickGuiState s) {
      return this.helper9(s) + 9.0F;
   }

   private float helper12(ClickGuiState s) {
      return this.helper9(s) + 35.0F;
   }

   private float helper13(ClickGuiState s) {
      return this.helper9(s) + 61.0F;
   }

   private float helper14(ClickGuiState s) {
      return this.helper9(s) + 87.0F;
   }

   private float helper15(ClickGuiState s) {
      return this.helper10(s) + 9.0F;
   }

   private float helper16(ClickGuiState s) {
      return this.helper9(s) + 6.0F;
   }

   private float helper17(ClickGuiState s) {
      return s.getMenuWidth() - 12.0F;
   }

   private float helper18(ClickGuiState s) {
      return this.helper10(s) + 9.0F + 20.0F + 6.0F;
   }

   private float helper19(ClickGuiState s) {
      return this.helper10(s) + s.getMenuHeight() - 8.0F;
   }

   private float[] helper20(ClickGuiState s) {
      return new float[]{this.helper(s), this.helper4(s), this.helper2(s)};
   }

   private boolean checkCondition(ClickGuiState s, double mx, double my) {
      return HoveringUtils.isHovered(mx, my, this.helper9(s), this.helper10(s), s.getMenuWidth(), s.getMenuHeight());
   }

   private String resolveString2(MinecraftClient mc) {
      return mc != null && mc.getSession() != null && mc.getSession().getUsername() != null ? mc.getSession().getUsername() : "Player";
   }

   public void renderButton(DrawContext context, ClickGuiState state, int mouseX, int mouseY, float alphaMul) {
      MatrixStack var6 = context.getMatrices();
      int var7 = ColorUtils.clientAccent();
      float var8 = this.helper11(state);
      float var9 = this.helper12(state);
      float var10 = this.helper13(state);
      float var11 = this.helper15(state);
      boolean var12 = state.isProfilePanelOpen() || HoveringUtils.isHovered(mouseX, mouseY, var8, var11, 20.0, 20.0);
      boolean var13 = state.isSettingsPanelOpen() || HoveringUtils.isHovered(mouseX, mouseY, var9, var11, 20.0, 20.0);
      boolean var14 = state.isFriendsPanelOpen() || HoveringUtils.isHovered(mouseX, mouseY, var10, var11, 20.0, 20.0);
      this.helper23(var6, var8, var11, var12, var7, alphaMul);
      this.helper23(var6, var9, var11, var13, var7, alphaMul);
      this.helper23(var6, var10, var11, var14, var7, alphaMul);
      float var15 = this.helper14(state);
      boolean var16 = HoveringUtils.isHovered(mouseX, mouseY, var15, var11, 20.0, 20.0);
      this.helper23(var6, var15, var11, var16, var7, alphaMul);
      this.helper21(
         var6, var15, var11, var16 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 190), alphaMul)
      );
      MinecraftClient var17 = MinecraftClient.getInstance();
      String var18 = this.resolveString2(var17);
      RenderUtils.drawPlayerHead(var6, var18, var8 + 3.0F, var11 + 3.0F, 14.0F, 3.0F);
      RenderUtils.drawRoundedRect(var6, var9 + 5.0F, var11 + 5.0F, 10.0F, 10.0F, 3.0F, ColorUtils.applyAlpha(ColorUtils.clientAccent(), alphaMul));
      this.helper22(
         var6, var10, var11, var14 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 190), alphaMul)
      );
   }

   private void helper21(MatrixStack m, float bx, float by, int color) {
      float var5 = bx + 10.0F;
      float var6 = by + 10.0F;
      float var7 = 4.0F;
      float var8 = 2.0F;
      float var9 = var5 - var7 - var8 / 2.0F;
      float var10 = var6 - var7 - var8 / 2.0F;
      RenderUtils.drawRoundedRect(m, var9, var10, var7, var7, 1.2F, color);
      RenderUtils.drawRoundedRect(m, var9 + var7 + var8, var10, var7, var7, 1.2F, color);
      RenderUtils.drawRoundedRect(m, var9, var10 + var7 + var8, var7, var7, 1.2F, color);
      RenderUtils.drawRoundedRect(m, var9 + var7 + var8, var10 + var7 + var8, var7, var7, 1.2F, color);
   }

   private void helper22(MatrixStack m, float bx, float by, int color) {
      float var5 = bx + 10.0F;
      float var6 = 3.2F;
      RenderUtils.drawRoundedRect(m, var5 - var6, by + 4.5F, var6 * 2.0F, var6 * 2.0F, var6, color);
      float var7 = 9.5F;
      float var8 = 5.5F;
      RenderUtils.drawRoundedRect(m, var5 - var7 / 2.0F, by + 11.0F, var7, var8, 2.6F, color);
   }

   private void helper23(MatrixStack m, float x, float y, boolean active, int theme, float alphaMul) {
      RenderUtils.drawRoundedRect(m, x, y, 20.0F, 20.0F, 5.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 150), alphaMul));
      if (active) {
         RenderUtils.drawRoundedRect(m, x, y, 20.0F, 20.0F, 5.0F, ColorUtils.applyAlpha(theme, 0.28F * alphaMul));
      }
   }

   public void render(DrawContext context, ClickGuiState state, Window window, int mouseX, int mouseY, float alphaMul) {
      int var7 = state.isProfilePanelOpen() ? 0 : (state.isFriendsPanelOpen() ? 1 : (state.isSettingsPanelOpen() ? 2 : (state.isAutosetPanelOpen() ? 3 : -1)));
      if (var7 != this.index7) {
         this.index7 = var7;
         if (var7 != -1) {
            this.animationUtils2.setValue(0.0F);
         }
      }

      if (var7 != -1) {
         this.animationUtils2.update(1.0F);
         float var8 = MathHelper.clamp(this.animationUtils2.getValue(), 0.0F, 1.0F);
         float var9 = alphaMul * var8;
         float var10 = (1.0F - var8) * 12.0F;
         MatrixStack var11 = context.getMatrices();
         var11.push();
         var11.translate(0.0F, var10, 0.0F);
         if (var7 == 0) {
            this.helper42(context, state, mouseX, mouseY, var9);
         } else if (var7 == 1) {
            this.helper66(context, state, mouseX, mouseY, var9);
         } else if (var7 == 2) {
            this.helper39(context, state, mouseX, mouseY, var9);
         } else {
            this.autoSetPage
               .render(
                  context,
                  this.helper16(state),
                  this.helper18(state),
                  this.helper17(state),
                  this.helper19(state) - this.helper18(state),
                  mouseX,
                  mouseY,
                  var9
               );
         }

         var11.pop();
      }
   }

   private ClientColors.Slot helper24() {
      ClientColors.Slot[] var1 = ClientColors.Slot.values();
      return var1[MathHelper.clamp(this.index2, 0, var1.length - 1)];
   }

   private float[] helper25(ClickGuiState s, int i) {
      float var3 = this.helper16(s) + 8.0F;
      float var4 = this.helper18(s) + 20.0F + i * 28.0F;
      return new float[]{var3, var4, 150.0F, 24.0F};
   }

   private float helper26(ClickGuiState s) {
      return this.helper16(s) + 8.0F + 150.0F + 12.0F;
   }

   private float helper27(ClickGuiState s) {
      return this.helper16(s) + this.helper17(s) - 8.0F - this.helper26(s);
   }

   private float[] helper28(ClickGuiState s) {
      return new float[]{this.helper26(s), this.helper18(s) + 8.0F, this.helper27(s), 104.0F};
   }

   private float[] helper29(ClickGuiState s) {
      float[] var2 = this.helper28(s);
      return new float[]{var2[0], var2[1] + var2[3] + 10.0F, var2[2], 12.0F};
   }

   private float[] helper30(ClickGuiState s) {
      float[] var2 = this.helper29(s);
      return new float[]{var2[0], var2[1] + var2[3] + 8.0F, var2[2], 12.0F};
   }

   private float[] helper31(ClickGuiState s) {
      float[] var2 = this.helper30(s);
      return new float[]{var2[0], var2[1] + var2[3] + 10.0F, var2[2] - 30.0F, 22.0F};
   }

   private float[] helper32(ClickGuiState s) {
      float[] var2 = this.helper31(s);
      return new float[]{var2[0] + var2[2] + 6.0F, var2[1], 24.0F, 22.0F};
   }

   private float[] helper33(ClickGuiState s, int i) {
      float[] var3 = this.helper31(s);
      float var4 = var3[1] + var3[3] + 8.0F;
      float var5 = this.helper27(s);
      float var6 = (var5 - 18.0F) / 4.0F;
      return new float[]{this.helper26(s) + i * (var6 + 6.0F), var4, var6, 20.0F};
   }

   private float[] helper34(ClickGuiState s) {
      float[] var2 = this.helper33(s, 0);
      return new float[]{this.helper26(s), var2[1] + var2[3] + 12.0F, 26.0F, 14.0F};
   }

   private float[] helper35(ClickGuiState s) {
      float[] var2 = this.helper34(s);
      float var3 = 74.0F;
      return new float[]{this.helper26(s) + this.helper27(s) - var3, var2[1] - 3.0F, var3, 20.0F};
   }

   private float[] helper36(ClickGuiState s, int i) {
      float var3 = this.helper16(s) + 10.0F + i * 23.0F;
      float var4 = this.helper18(s) + 20.0F + ClientColors.Slot.values().length * 28.0F + 9.0F;
      return new float[]{var3, var4, 15.0F, 15.0F};
   }

   private void updateState() {
      if (!this.flag) {
         this.helper37();
         this.flag = true;
      }
   }

   private void helper37() {
      int var1 = ClientColors.raw(this.helper24());
      float[] var2 = Color.RGBtoHSB(ColorUtils.red(var1), ColorUtils.green(var1), ColorUtils.blue(var1), null);
      this.volume = var2[0];
      this.volume2 = var2[1];
      this.volume3 = var2[2];
      this.volume4 = ColorUtils.alpha(var1) / 255.0F;
      this.stringBuilder.setLength(0);
      this.stringBuilder.append(helper38(var1));
      this.colorsTextFocus = false;
   }

   private int resolveInt() {
      int var1 = Color.HSBtoRGB(this.volume, this.volume2, this.volume3) & 16777215;
      int var2 = MathHelper.clamp((int)(this.volume4 * 255.0F), 0, 255);
      return var2 << 24 | var1;
   }

   private void updateState2() {
      int var1 = this.resolveInt();
      ClientColors.set(this.helper24(), var1);
      if (!this.colorsTextFocus) {
         this.stringBuilder.setLength(0);
         this.stringBuilder.append(helper38(var1));
      }
   }

   private void updateState3() {
      try {
         Lumen.INSTANCE.configStorage.saveConfig(Lumen.INSTANCE.configStorage.currentConfig);
      } catch (Exception var2) {
      }
   }

   private static String helper38(int color) {
      return String.format("%02X%02X%02X", ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color));
   }

   private void helper39(DrawContext context, ClickGuiState state, int mouseX, int mouseY, float alphaMul) {
      this.updateState();
      MatrixStack var6 = context.getMatrices();
      int var7 = ClickGuiTheme.accent();
      int var8 = ColorUtils.rgba(255, 255, 255, 255);
      float var9 = this.helper16(state);
      float var10 = this.helper17(state);
      float var11 = this.helper18(state);
      float var12 = this.helper19(state);
      RenderUtils.drawBlur(
         var6, var9, var11, var10, var12 - var11, new Vector4f(0.0F, 9.0F, 0.0F, 9.0F), ColorUtils.applyAlpha(ColorUtils.rgba(20, 20, 20, 42), alphaMul)
      );
      ClientColors.Slot[] var13 = ClientColors.Slot.values();
      Font var14 = Fonts.getFont("suisse", 11);
      Font var15 = Fonts.getFont("suisse", 9);

      for (int var16 = 0; var16 < var13.length; var16++) {
         float[] var17 = this.helper25(state, var16);
         boolean var18 = var16 == this.index2;
         boolean var19 = HoveringUtils.isHovered(mouseX, mouseY, var17[0], var17[1], var17[2], var17[3]);
         int var20 = ClientColors.color(var13[var16]);
         int var21 = var18
            ? ColorUtils.applyAlpha(ColorUtils.clientAccent(), 0.16F * alphaMul)
            : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var19 ? 26 : 13), alphaMul);
         RenderUtils.drawRoundedRect(var6, var17[0], var17[1], var17[2], var17[3], 6.0F, var21);
         if (var18) {
            RenderUtils.drawRoundedRectOutline(
               var6,
               var17[0],
               var17[1],
               var17[2],
               var17[3],
               6.0F,
               0.1F,
               ColorUtils.applyAlpha(var7, alphaMul),
               ColorUtils.applyAlpha(ColorUtils.clientAccent(), alphaMul),
               ColorUtils.applyAlpha(var7, alphaMul),
               ColorUtils.applyAlpha(ColorUtils.clientAccent(), alphaMul)
            );
         }

         RenderUtils.drawRoundedRect(var6, var17[0] + 7.0F, var17[1] + var17[3] / 2.0F - 7.5F, 15.0F, 15.0F, 4.0F, ColorUtils.applyAlpha(var20, alphaMul));
         RenderUtils.drawRoundedRectOutline(
            var6,
            var17[0] + 7.0F,
            var17[1] + var17[3] / 2.0F - 7.5F,
            15.0F,
            15.0F,
            4.0F,
            0.1F,
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul),
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul),
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul),
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul)
         );
         int var22 = var18 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul);
         var14.draw(var6, var13[var16].label(), var17[0] + 28.0F, var17[1] + 5.0F + var17[3] / 2.0F - 6.0F, var22);
         String var23 = ClientColors.rainbow(var13[var16]) ? "RGB" : "#" + helper38(var20);
         var15.draw(
            var6,
            var23,
            var17[0] + var17[2] - var15.getWidth(var23) - 7.0F,
            var17[1] + 4.0F + var17[3] / 2.0F - 4.0F,
            ColorUtils.applyAlpha(var18 ? var7 : ColorUtils.rgba(255, 255, 255, 110), alphaMul)
         );
      }

      ClientColors.Preset[] var42 = ClientColors.Preset.values();
      float[] var43 = this.helper36(state, 0);

      for (int var44 = 0; var44 < var42.length; var44++) {
         float[] var46 = this.helper36(state, var44);
         boolean var48 = HoveringUtils.isHovered(mouseX, mouseY, var46[0], var46[1], var46[2], var46[3]);
         RenderUtils.drawRoundedRect(var6, var46[0], var46[1], var46[2], var46[3], 8.0F, ColorUtils.applyAlpha(var42[var44].accentColor(), alphaMul));
         int var50 = var48 ? -1 : ColorUtils.rgba(255, 255, 255, 60);
         RenderUtils.drawRoundedRectOutline(
            var6,
            var46[0],
            var46[1],
            var46[2],
            var46[3],
            8.0F,
            0.1F,
            ColorUtils.applyAlpha(var50, alphaMul),
            ColorUtils.applyAlpha(var50, alphaMul),
            ColorUtils.applyAlpha(var50, alphaMul),
            ColorUtils.applyAlpha(var50, alphaMul)
         );
      }

      boolean var45 = ClientColors.rainbow(this.helper24());
      float[] var47 = this.helper28(state);
      int var49 = ColorUtils.setAlphaColor(Color.HSBtoRGB(this.volume, 1.0F, 1.0F), 255);
      RenderUtils.drawGradientRect(
         var6,
         var47[0],
         var47[1],
         var47[2],
         var47[3],
         6.0F,
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 255), alphaMul),
         ColorUtils.applyAlpha(var49, alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 255), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 255), alphaMul)
      );
      float var51 = var47[0] + this.volume2 * var47[2];
      float var52 = var47[1] + (1.0F - this.volume3) * var47[3];
      RenderUtils.drawRoundCircle(var6, var51, var52, 6.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 180), alphaMul));
      RenderUtils.drawRoundCircle(var6, var51, var52, 4.5F, ColorUtils.applyAlpha(-1, alphaMul));
      float[] var53 = this.helper29(state);
      int[] var24 = new int[]{
         ColorUtils.rgba(255, 0, 0, 255),
         ColorUtils.rgba(255, 255, 0, 255),
         ColorUtils.rgba(0, 255, 0, 255),
         ColorUtils.rgba(0, 255, 255, 255),
         ColorUtils.rgba(0, 0, 255, 255),
         ColorUtils.rgba(255, 0, 255, 255),
         ColorUtils.rgba(255, 0, 0, 255)
      };
      float var25 = var53[2] / 6.0F;

      for (int var26 = 0; var26 < 6; var26++) {
         RenderUtils.drawGradientRect(
            var6,
            var53[0] + var26 * var25,
            var53[1],
            var25 + 0.5F,
            var53[3],
            0.0F,
            ColorUtils.applyAlpha(var24[var26], alphaMul),
            ColorUtils.applyAlpha(var24[var26 + 1], alphaMul),
            true
         );
      }

      float var54 = var53[0] + this.volume * var53[2];
      RenderUtils.drawRoundedRect(var6, var54 - 2.0F, var53[1] - 2.0F, 4.0F, var53[3] + 4.0F, 2.0F, ColorUtils.applyAlpha(-1, alphaMul));
      RenderUtils.drawRoundedRectOutline(
         var6,
         var54 - 2.0F,
         var53[1] - 2.0F,
         4.0F,
         var53[3] + 4.0F,
         2.0F,
         0.1F,
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 160), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 160), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 160), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 160), alphaMul)
      );
      float[] var27 = this.helper30(state);
      int var28 = ColorUtils.setAlphaColor(this.resolveInt(), 255);
      RenderUtils.drawRoundedRect(var6, var27[0], var27[1], var27[2], var27[3], 3.0F, ColorUtils.applyAlpha(ColorUtils.rgba(35, 35, 40, 255), alphaMul));
      RenderUtils.drawGradientRect(
         var6,
         var27[0],
         var27[1],
         var27[2],
         var27[3],
         3.0F,
         ColorUtils.applyAlpha(ColorUtils.setAlphaColor(var28, 0), alphaMul),
         ColorUtils.applyAlpha(var28, alphaMul),
         true
      );
      float var29 = var27[0] + this.volume4 * var27[2];
      RenderUtils.drawRoundedRect(var6, var29 - 2.0F, var27[1] - 2.0F, 4.0F, var27[3] + 4.0F, 2.0F, ColorUtils.applyAlpha(-1, alphaMul));
      float[] var30 = this.helper31(state);
      RenderUtils.drawRoundedRect(var6, var30[0], var30[1], var30[2], var30[3], 5.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 160), alphaMul));
      int var31 = this.colorsTextFocus ? var7 : ColorUtils.rgba(255, 255, 255, 40);
      RenderUtils.drawRoundedRectOutline(
         var6,
         var30[0],
         var30[1],
         var30[2],
         var30[3],
         5.0F,
         0.1F,
         ColorUtils.applyAlpha(var31, alphaMul),
         ColorUtils.applyAlpha(var31, alphaMul),
         ColorUtils.applyAlpha(var31, alphaMul),
         ColorUtils.applyAlpha(var31, alphaMul)
      );
      Font var32 = Fonts.getFont("suisse", 11);
      var32.draw(var6, "HEX", var30[0] + 8.0F, var30[1] + 3.0F + var30[3] / 2.0F - 4.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 110), alphaMul));
      String var33 = "#" + this.stringBuilder + (this.colorsTextFocus ? "|" : "");
      var32.draw(
         var6, var33, var30[0] + var30[2] - var32.getWidth(var33) - 8.0F, var30[1] + 3.0F + var30[3] / 2.0F - 4.0F, ColorUtils.applyAlpha(var8, alphaMul)
      );
      float[] var34 = this.helper32(state);
      RenderUtils.drawRoundedRect(
         var6, var34[0], var34[1], var34[2], var34[3], 5.0F, ColorUtils.applyAlpha(ClientColors.color(this.helper24()), alphaMul)
      );
      RenderUtils.drawRoundedRectOutline(
         var6,
         var34[0],
         var34[1],
         var34[2],
         var34[3],
         5.0F,
         0.1F,
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul),
         ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul)
      );
      int var35 = this.resolveInt();
      int[] var36 = new int[]{ColorUtils.red(var35), ColorUtils.green(var35), ColorUtils.blue(var35), ColorUtils.alpha(var35)};
      String[] var37 = new String[]{"R", "G", "B", "A"};
      Font var38 = Fonts.getFont("suisse", 11);

      for (int var39 = 0; var39 < 4; var39++) {
         float[] var40 = this.helper33(state, var39);
         RenderUtils.drawRoundedRect(var6, var40[0], var40[1], var40[2], var40[3], 5.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 150), alphaMul));
         var38.draw(
            var6, var37[var39], var40[0] + 6.0F, var40[1] + 3.0F + var40[3] / 2.0F - 4.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 110), alphaMul)
         );
         String var41 = String.valueOf(var36[var39]);
         var38.draw(
            var6, var41, var40[0] + var40[2] - var38.getWidth(var41) - 6.0F, var40[1] + 3.0F + var40[3] / 2.0F - 4.0F, ColorUtils.applyAlpha(var8, alphaMul)
         );
      }

      float[] var55 = this.helper35(state);
      boolean var56 = HoveringUtils.isHovered(mouseX, mouseY, var55[0], var55[1], var55[2], var55[3]);
      RenderUtils.drawRoundedRect(
         var6, var55[0], var55[1], var55[2], var55[3], 5.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var56 ? 30 : 16), alphaMul)
      );
      Fonts.getFont("suisse", 11)
         .drawCenteredString(
            var6,
            "Сбросить слот",
            var55[0] + var55[2] / 2.0F,
            var55[1] + 3.0F + var55[3] / 2.0F - 4.0F,
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 220), alphaMul)
         );
   }

   private static float helper40(double v) {
      return MathHelper.clamp((float)v, 0.0F, 1.0F);
   }

   private void updateState4(ClickGuiState s, double mx, double my) {
      float[] var6 = this.helper28(s);
      this.volume2 = helper40((mx - var6[0]) / var6[2]);
      this.volume3 = 1.0F - helper40((my - var6[1]) / var6[3]);
      this.updateState2();
   }

   private void updateState5(ClickGuiState s, double mx) {
      float[] var4 = this.helper29(s);
      this.volume = helper40((mx - var4[0]) / var4[2]);
      this.updateState2();
   }

   private void updateState6(ClickGuiState s, double mx) {
      float[] var4 = this.helper30(s);
      this.volume4 = helper40((mx - var4[0]) / var4[2]);
      this.updateState2();
   }

   private boolean checkCondition2(ClickGuiState state, double mouseX, double mouseY) {
      this.updateState();
      ClientColors.Slot[] var6 = ClientColors.Slot.values();

      for (int var7 = 0; var7 < var6.length; var7++) {
         float[] var8 = this.helper25(state, var7);
         if (HoveringUtils.isHovered(mouseX, mouseY, var8[0], var8[1], var8[2], var8[3])) {
            if (this.index2 != var7) {
               this.index2 = var7;
               this.flag = false;
               this.updateState();
            }

            this.colorsTextFocus = false;
            return true;
         }
      }

      ClientColors.Preset[] var13 = ClientColors.Preset.values();

      for (int var14 = 0; var14 < var13.length; var14++) {
         float[] var9 = this.helper36(state, var14);
         if (HoveringUtils.isHovered(mouseX, mouseY, var9[0], var9[1], var9[2], var9[3])) {
            ClientColors.applyPreset(var13[var14]);
            this.helper37();
            this.colorsTextFocus = false;
            this.updateState3();
            return true;
         }
      }

      float[] var15 = this.helper28(state);
      if (HoveringUtils.isHovered(mouseX, mouseY, var15[0], var15[1], var15[2], var15[3])) {
         this.index3 = 1;
         this.updateState4(state, mouseX, mouseY);
         this.colorsTextFocus = false;
         return true;
      } else {
         float[] var16 = this.helper29(state);
         if (HoveringUtils.isHovered(mouseX, mouseY, var16[0], var16[1] - 3.0F, var16[2], var16[3] + 6.0F)) {
            this.index3 = 2;
            this.updateState5(state, mouseX);
            this.colorsTextFocus = false;
            return true;
         } else {
            float[] var10 = this.helper30(state);
            if (HoveringUtils.isHovered(mouseX, mouseY, var10[0], var10[1] - 3.0F, var10[2], var10[3] + 6.0F)) {
               this.index3 = 3;
               this.updateState6(state, mouseX);
               this.colorsTextFocus = false;
               return true;
            } else {
               float[] var11 = this.helper31(state);
               if (HoveringUtils.isHovered(mouseX, mouseY, var11[0], var11[1], var11[2], var11[3])) {
                  this.colorsTextFocus = true;
                  return true;
               } else {
                  float[] var12 = this.helper35(state);
                  if (HoveringUtils.isHovered(mouseX, mouseY, var12[0], var12[1], var12[2], var12[3])) {
                     ClientColors.reset(this.helper24());
                     this.helper37();
                     this.updateState3();
                     return true;
                  } else if (this.checkCondition(state, mouseX, mouseY)) {
                     this.colorsTextFocus = false;
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   public boolean hasColorsTextFocus() {
      return this.colorsTextFocus;
   }

   public boolean handleColorsKey(int keyCode) {
      if (!this.colorsTextFocus) {
         return false;
      }

      if (keyCode == 256 || keyCode == 257 || keyCode == 335) {
         this.helper41();
         this.colorsTextFocus = false;
         return true;
      }

      if (keyCode == 259) {
         if (this.stringBuilder.length() > 0) {
            this.stringBuilder.deleteCharAt(this.stringBuilder.length() - 1);
            this.helper41();
         }

         return true;
      } else {
         return true;
      }
   }

   public boolean handleAutosetKey(int keyCode, int modifiers) {
      return this.autoSetPage.handleKey(keyCode, modifiers);
   }

   public boolean handleAutosetChar(char chr) {
      return this.autoSetPage.handleChar(chr);
   }

   public boolean handleColorsChar(char chr) {
      if (!this.colorsTextFocus) {
         return false;
      }

      boolean var2 = chr >= '0' && chr <= '9' || chr >= 'a' && chr <= 'f' || chr >= 'A' && chr <= 'F';
      if (var2 && this.stringBuilder.length() < 6) {
         this.stringBuilder.append(Character.toUpperCase(chr));
         this.helper41();
      }

      return true;
   }

   private void helper41() {
      if (this.stringBuilder.length() == 6) {
         try {
            int var1 = Integer.parseInt(this.stringBuilder.toString(), 16) & 16777215;
            float[] var2 = Color.RGBtoHSB(var1 >> 16 & 0xFF, var1 >> 8 & 0xFF, var1 & 0xFF, null);
            this.volume = var2[0];
            this.volume2 = var2[1];
            this.volume3 = var2[2];
            int var3 = MathHelper.clamp((int)(this.volume4 * 255.0F), 0, 255);
            ClientColors.set(this.helper24(), var3 << 24 | var1);
            this.updateState3();
         } catch (NumberFormatException var4) {
         }
      }
   }

   private void helper42(DrawContext context, ClickGuiState state, int mouseX, int mouseY, float alphaMul) {
      MatrixStack var6 = context.getMatrices();
      int var7 = ColorUtils.clientOutline();
      float var8 = this.helper16(state);
      float var9 = this.helper17(state);
      float var10 = this.helper18(state);
      float var11 = this.helper19(state);
      RenderUtils.drawBlur(
         var6, var8, var10, var9, var11 - var10, new Vector4f(0.0F, 9.0F, 0.0F, 9.0F), ColorUtils.applyAlpha(ColorUtils.rgba(20, 20, 20, 42), alphaMul)
      );
      MinecraftClient var12 = MinecraftClient.getInstance();
      float[] var13 = this.helper6(state);
      float var14 = var13[0];
      float var15 = var13[1];
      float var16 = var13[0] + var13[2];
      float var17 = var13[1] + var13[3];
      RenderUtils.drawRoundedRect(var6, var14, var15, var13[2], var13[3], 7.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 130), alphaMul));
      RenderUtils.drawRoundedRectOutline(
         var6,
         var14,
         var15,
         var13[2],
         var13[3],
         7.0F,
         1.0F,
         ColorUtils.applyAlpha(var7, alphaMul),
         ColorUtils.applyAlpha(var7, alphaMul),
         ColorUtils.applyAlpha(var7, alphaMul),
         ColorUtils.applyAlpha(var7, alphaMul)
      );
      if (var12 != null && var12.player != null) {
         int var18 = (int)(var13[3] * 0.34F);
         InventoryScreen.drawEntity(context, (int)var14, (int)var15, (int)var16, (int)(var17 - 18.0F), var18, 0.0F, mouseX, mouseY, var12.player);
      }

      String var41 = this.resolveString2(var12);
      Font var19 = Fonts.getFont("suisse", 12);
      var19.draw(
         var6,
         this.resolveString(var19, var41, var13[2] - 12.0F),
         var14 + (var13[2] - var19.getWidth(this.resolveString(var19, var41, var13[2] - 12.0F))) / 2.0F,
         var17 - 13.0F,
         ColorUtils.applyAlpha(var7, alphaMul)
      );
      float var20 = this.helper(state);
      float var21 = this.helper2(state);
      float var22 = this.helper4(state);
      float var23 = this.helper5(state);
      FiguraBridge.Category[] var24 = FiguraBridge.Category.values();
      int var25 = MathHelper.clamp(this.index4, 0, var24.length - 1);

      for (int var26 = 0; var26 < var24.length; var26++) {
         float[] var27 = this.helper7(state, var26);
         boolean var28 = var26 == var25;
         boolean var29 = HoveringUtils.isHovered(mouseX, mouseY, var27[0], var27[1], var27[2], var27[3]);
         int var30 = var28 ? ColorUtils.applyAlpha(var7, 0.9F * alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var29 ? 32 : 16), alphaMul);
         RenderUtils.drawRoundedRect(var6, var27[0], var27[1], var27[2], var27[3], 5.0F, var30);
         context.drawItem(this.helper8(var24[var26]), (int)(var27[0] + var27[2] / 2.0F - 8.0F), (int)(var27[1] + var27[3] / 2.0F - 8.0F));
      }

      float var42 = Math.max(0.0F, var23 - var22);
      List var43 = FiguraBridge.listAvatarInfos(var24[var25]);
      int var44 = 1 + var43.size();
      float var45 = var44 * 19.0F;
      this.volume7 = Math.max(0.0F, var45 - var42);
      this.volume6 = MathHelper.clamp(this.volume6, 0.0F, this.volume7);
      this.animationUtils.update(this.volume6);
      this.volume5 = MathHelper.clamp(this.animationUtils.getValue(), 0.0F, this.volume7);
      Font var46 = Fonts.getFont("suisse", 11);
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(var20, var22, var21, var42);

      for (int var31 = 0; var31 < var44; var31++) {
         float var32 = var22 + var31 * 19.0F - this.volume5;
         if (!(var32 + 16.0F < var22) && !(var32 > var23)) {
            boolean var33 = var31 == 0;
            FiguraBridge.AvatarInfo var34 = var33 ? null : (FiguraBridge.AvatarInfo)var43.get(var31 - 1);
            boolean var35 = var33 ? FiguraBridge.isNoneSelected() : FiguraBridge.isSelectedAny(var34.id());
            boolean var36 = HoveringUtils.isHovered(mouseX, mouseY, var20, var32, var21, 16.0) && mouseY >= var22 && mouseY <= var23;
            int var37 = var35 ? ColorUtils.applyAlpha(var7, 0.9F * alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var36 ? 32 : 16), alphaMul);
            RenderUtils.drawRoundedRect(var6, var20, var32, var21, 16.0F, 4.0F, var37);
            float var38 = var20 + 8.0F;
            if (!var33) {
               Identifier var39 = FiguraAvatarIcons.icon(var34);
               if (var39 != null) {
                  RenderUtils.drawImage(var6, var39, var20 + 4.0F, var32 + 8.0F - 6.0F, 12.0F, 12.0F, ColorUtils.applyAlpha(-1, alphaMul));
                  var38 = var20 + 20.0F;
               }
            }

            String var48 = var33 ? "Снять модель" : var34.name();
            int var40 = var35 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul);
            var46.draw(var6, this.resolveString(var46, var48, var21 - (var38 - var20) - 6.0F), var38, var32 + 3.0F + 8.0F - 4.0F, var40);
         }
      }

      ScissorUtils.pop();
      RenderUtils.drawRoundedRect(var6, var20, var23 + 5.0F, var21, 1.0F, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 22), alphaMul));
      float[] var47 = this.helper43(state);
      if (var47[2] > 90.0F) {
         this.helper50(var6, var7, var47, mouseX, mouseY, alphaMul);
      }
   }

   private float[] helper43(ClickGuiState s) {
      float var2 = this.helper(s);
      float var3 = this.helper5(s) + 12.0F;
      float var4 = this.helper2(s);
      float var5 = this.helper19(s) - 8.0F - var3;
      return new float[]{var2, var3, var4, var5};
   }

   private float helper44(float[] g) {
      return (g[2] - 12.0F) / 2.0F;
   }

   private float[] helper45(float[] g, int i) {
      float var3 = this.helper44(g);
      int var4 = i % 2;
      int var5 = i / 2;
      float var6 = g[0] + var4 * (var3 + 12.0F);
      float var7 = g[1] + 16.0F + var5 * 24.0F + 11.0F;
      return new float[]{var6, var7, var3, 4.0F};
   }

   private float helper46(float[] g) {
      int var2 = (this.value.length + 1) / 2;
      return g[1] + 16.0F + var2 * 24.0F + 8.0F;
   }

   private float[] helper47(float[] g, int j) {
      float var3 = this.helper44(g);
      float var4 = 20.0F;
      float var5 = 11.0F;
      float var6 = g[0] + j * (var3 + 12.0F);
      return new float[]{var6, this.helper46(g), var4, var5};
   }

   private int resolveInt2() {
      return GuiGifManager.KEYS.length + 1;
   }

   private String resolveString3(int index) {
      return index == 0 ? "Нет" : GuiGifManager.LABELS[index - 1];
   }

   private float helper48(float[] g) {
      return this.helper46(g) + 11.0F + 16.0F;
   }

   private float[] helper49(float[] g, int index) {
      int var3 = this.resolveInt2();
      float var4 = (g[2] - 6.0F * (var3 - 1)) / var3;
      float var5 = g[0] + index * (var4 + 6.0F);
      return new float[]{var5, this.helper48(g), var4, 14.0F};
   }

   private void helper50(MatrixStack m, int theme, float[] g, int mouseX, int mouseY, float alphaMul) {
      Font var7 = Fonts.getFont("suisse", 9);
      Font var8 = Fonts.getFont("suisse", 10);

      for (int var9 = 0; var9 < this.value.length; var9++) {
         ClickGuiSettingsPanel.Slider var10 = this.value[var9];
         float[] var11 = this.helper45(g, var9);
         float var12 = var10.get().get();
         float var13 = MathHelper.clamp((var12 - var10.min()) / (var10.max() - var10.min()), 0.0F, 1.0F);
         var8.draw(m, var10.name(), var11[0], var11[1] - 10.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul));
         String var14 = String.format(Locale.ROOT, "%.2f", var12);
         var8.draw(m, var14, var11[0] + var11[2] - var8.getWidth(var14), var11[1] - 10.0F, ColorUtils.applyAlpha(theme, alphaMul));
         RenderUtils.drawRoundedRect(m, var11[0], var11[1], var11[2], var11[3], 2.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 30), alphaMul));
         RenderUtils.drawRoundedRect(m, var11[0], var11[1], Math.max(1.0F, var11[2] * var13), var11[3], 2.0F, ColorUtils.applyAlpha(theme, alphaMul));
         float var15 = var11[0] + var11[2] * var13;
         RenderUtils.drawRoundedRect(m, var15 - 2.5F, var11[1] - 3.0F, 5.0F, var11[3] + 6.0F, 2.5F, ColorUtils.applyAlpha(-1, alphaMul));
      }

      for (int var16 = 0; var16 < this.value2.length; var16++) {
         ClickGuiSettingsPanel.Toggle var18 = this.value2[var16];
         float[] var20 = this.helper47(g, var16);
         boolean var22 = var18.get().get();
         int var24 = var22 ? ColorUtils.applyAlpha(theme, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(60, 60, 65, 220), alphaMul);
         RenderUtils.drawRoundedRect(m, var20[0], var20[1], var20[2], var20[3], 5.0F, var24);
         float var26 = var20[0] + 1.0F + (var20[2] - var20[3]) * (var22 ? 1.0F : 0.0F);
         RenderUtils.drawRoundedRect(m, var26, var20[1] + 1.0F, var20[3] - 2.0F, var20[3] - 2.0F, 4.0F, ColorUtils.applyAlpha(-1, alphaMul));
         var8.draw(m, var18.name(), var20[0] + var20[2] + 6.0F, var20[1] + 3.0F + 2.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul));
      }

      Font var17 = Fonts.getFont("suisse", 9);

      for (int var19 = 0; var19 < this.resolveInt2(); var19++) {
         float[] var21 = this.helper49(g, var19);
         boolean var23 = var19 == 0 ? GuiGifManager.get().isSelected(null) : GuiGifManager.get().isSelected(GuiGifManager.KEYS[var19 - 1]);
         boolean var25 = HoveringUtils.isHovered(mouseX, mouseY, var21[0], var21[1], var21[2], var21[3]);
         int var27 = var23 ? ColorUtils.applyAlpha(theme, 0.9F * alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var25 ? 34 : 16), alphaMul);
         RenderUtils.drawRoundedRect(m, var21[0], var21[1], var21[2], var21[3], 4.0F, var27);
         int var28 = var23 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul);
         var17.drawCenteredString(
            m,
            this.resolveString(var17, this.resolveString3(var19), var21[2] - 4.0F),
            var21[0] + var21[2] / 2.0F,
            var21[1] + 3.0F + var21[3] / 2.0F - 3.5F,
            var28
         );
      }
   }

   private void updateState7(int i, float[] g, double mouseX) {
      float[] var5 = this.helper45(g, i);
      float var6 = MathHelper.clamp((float)((mouseX - var5[0]) / var5[2]), 0.0F, 1.0F);
      ClickGuiSettingsPanel.Slider var7 = this.value[i];
      var7.set().accept(var7.min() + var6 * (var7.max() - var7.min()));
   }

   private float helper51(ClickGuiState s) {
      return this.helper16(s) + 10.0F;
   }

   private float helper52(ClickGuiState s) {
      return this.helper17(s) - 20.0F;
   }

   private float[] helper53(ClickGuiState s, int i) {
      float var3 = this.helper51(s);
      float var4 = this.helper52(s);
      float var5 = (var4 - 6.0F) / 2.0F;
      return new float[]{var3 + i * (var5 + 6.0F), this.helper18(s) + 8.0F, var5, 20.0F};
   }

   private float helper54(ClickGuiState s) {
      return this.helper18(s) + 8.0F + 20.0F + 10.0F;
   }

   private float[] helper55(ClickGuiState s) {
      return new float[]{this.helper51(s), this.helper54(s), this.helper52(s) - 70.0F - 6.0F, 18.0F};
   }

   private float[] helper56(ClickGuiState s) {
      return new float[]{this.helper51(s) + this.helper52(s) - 70.0F, this.helper54(s), 70.0F, 18.0F};
   }

   private float helper57(ClickGuiState s) {
      return this.helper54(s) + 18.0F + 8.0F;
   }

   private float helper58(ClickGuiState s) {
      return this.helper19(s) - 8.0F;
   }

   private float helper59(ClickGuiState s) {
      return this.helper52(s) * 0.42F;
   }

   private float helper60(ClickGuiState s) {
      return this.helper51(s) + this.helper59(s) + 10.0F;
   }

   private float helper61(ClickGuiState s) {
      return this.helper51(s) + this.helper52(s) - this.helper60(s);
   }

   private float[] helper62(ClickGuiState s) {
      return new float[]{this.helper60(s), this.helper57(s) + 12.0F, this.helper61(s), 18.0F};
   }

   private float helper63(ClickGuiState s) {
      return this.helper57(s) + 12.0F + 18.0F + 22.0F;
   }

   private float helper64(ClickGuiState s) {
      return this.helper19(s) - 8.0F - 24.0F;
   }

   private float[] helper65(ClickGuiState s) {
      return new float[]{this.helper60(s), this.helper19(s) - 8.0F - 18.0F, this.helper61(s), 18.0F};
   }

   private void helper66(DrawContext context, ClickGuiState state, int mouseX, int mouseY, float alphaMul) {
      MatrixStack var6 = context.getMatrices();
      int var7 = ColorUtils.clientOutline();
      float var8 = this.helper16(state);
      float var9 = this.helper17(state);
      float var10 = this.helper18(state);
      float var11 = this.helper19(state);
      RenderUtils.drawBlur(
         var6, var8, var10, var9, var11 - var10, new Vector4f(0.0F, 9.0F, 0.0F, 9.0F), ColorUtils.applyAlpha(ColorUtils.rgba(20, 20, 20, 42), alphaMul)
      );
      int var12 = Lumen.INSTANCE.configStorage.getAvailableConfigs().size();
      int var13 = Lumen.INSTANCE.friendStorage.getFriends().size();
      String[] var14 = new String[]{"Конфиги · " + var12, "Друзья · " + var13};

      for (int var15 = 0; var15 < var14.length; var15++) {
         float[] var16 = this.helper53(state, var15);
         boolean var17 = var15 == this.index6;
         boolean var18 = HoveringUtils.isHovered(mouseX, mouseY, var16[0], var16[1], var16[2], var16[3]);
         int var19 = var17 ? ColorUtils.applyAlpha(var7, 0.9F * alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var18 ? 34 : 16), alphaMul);
         RenderUtils.drawRoundedRect(var6, var16[0], var16[1], var16[2], var16[3], 5.0F, var19);
         int var20 = var17 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul);
         Fonts.getFont("suisse", 12).drawCenteredString(var6, var14[var15], var16[0] + var16[2] / 2.0F, var16[1] + 2.0F + var16[3] / 2.0F - 4.0F + 1.0F, var20);
      }

      if (this.index6 == 0) {
         this.helper67(context, state, mouseX, mouseY, alphaMul, var7);
      } else {
         this.helper68(context, state, mouseX, mouseY, alphaMul, var7);
      }
   }

   private void helper67(DrawContext context, ClickGuiState state, int mouseX, int mouseY, float alphaMul, int theme) {
      MatrixStack var7 = context.getMatrices();
      float[] var8 = this.helper55(state);
      this.helper69(
         var7, var8, this.stringBuilder2.toString(), "Имя конфига…", this.socialTextFocus == ClickGuiSettingsPanel.SocFocus.CONFIG_NAME, theme, alphaMul
      );
      float[] var9 = this.helper56(state);
      boolean var10 = HoveringUtils.isHovered(mouseX, mouseY, var9[0], var9[1], var9[2], var9[3]);
      this.helper70(var7, var9, "+ Сохранить", var10, theme, alphaMul);
      ArrayList var11 = new ArrayList<>(Lumen.INSTANCE.configStorage.getAvailableConfigs());
      var11.sort(String.CASE_INSENSITIVE_ORDER);
      String var12 = Lumen.INSTANCE.configStorage.currentConfig;
      float var13 = this.helper51(state);
      float var14 = this.helper52(state);
      float var15 = this.helper57(state);
      float var16 = this.helper58(state);
      float var17 = Math.max(0.0F, var16 - var15);
      float var18 = var11.size() * 20.0F;
      this.volume9 = Math.max(0.0F, var18 - var17);
      this.volume8 = MathHelper.clamp(this.volume8, 0.0F, this.volume9);
      Font var19 = Fonts.getFont("suisse", 11);
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(var13, var15, var14, var17);

      for (int var20 = 0; var20 < var11.size(); var20++) {
         float var21 = var15 + var20 * 20.0F - this.volume8;
         if (!(var21 + 16.0F < var15) && !(var21 > var16)) {
            String var22 = (String)var11.get(var20);
            boolean var23 = var22.equals(var12);
            boolean var24 = HoveringUtils.isHovered(mouseX, mouseY, var13, var21, var14, 16.0) && mouseY >= var15 && mouseY <= var16;
            int var25 = var23
               ? ColorUtils.applyAlpha(theme, 0.85F * alphaMul)
               : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var24 ? 30 : 15), alphaMul);
            RenderUtils.drawRoundedRect(var7, var13, var21, var14, 16.0F, 4.0F, var25);
            int var26 = var23 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 210), alphaMul);
            var19.draw(var7, this.resolveString(var19, var22, var14 - 16.0F - 10.0F), var13 + 7.0F, var21 + 2.0F + 8.0F - 4.0F + 1.0F, var26);
            if (!var23) {
               Font var27 = Fonts.getFont("suisse", 12);
               var27.drawCenteredString(
                  var7, "x", var13 + var14 - 8.0F, var21 + 8.0F - 2.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 120, 120, 220), alphaMul)
               );
            } else {
               RenderUtils.drawRoundCircle(var7, var13 + var14 - 8.0F, var21 + 8.0F, 5.0F, ColorUtils.applyAlpha(-1, alphaMul));
            }
         }
      }

      ScissorUtils.pop();
   }

   private void helper68(DrawContext context, ClickGuiState state, int mouseX, int mouseY, float alphaMul, int theme) {
      MatrixStack var7 = context.getMatrices();
      float[] var8 = this.helper55(state);
      this.helper69(
         var7, var8, this.stringBuilder3.toString(), "Ник игрока…", this.socialTextFocus == ClickGuiSettingsPanel.SocFocus.FRIEND_NAME, theme, alphaMul
      );
      float[] var9 = this.helper56(state);
      boolean var10 = HoveringUtils.isHovered(mouseX, mouseY, var9[0], var9[1], var9[2], var9[3]);
      this.helper70(var7, var9, "+ Добавить", var10, theme, alphaMul);
      List var11 = Lumen.INSTANCE.friendStorage.getFriends();
      float var12 = this.helper51(state);
      float var13 = this.helper59(state);
      float var14 = this.helper57(state);
      float var15 = this.helper58(state);
      float var16 = Math.max(0.0F, var15 - var14);
      float var17 = var11.size() * 20.0F;
      this.volume11 = Math.max(0.0F, var17 - var16);
      this.volume10 = MathHelper.clamp(this.volume10, 0.0F, this.volume11);
      Font var18 = Fonts.getFont("suisse", 11);
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(var12, var14, var13, var16);

      for (int var19 = 0; var19 < var11.size(); var19++) {
         float var20 = var14 + var19 * 20.0F - this.volume10;
         if (!(var20 + 16.0F < var14) && !(var20 > var15)) {
            String var21 = (String)var11.get(var19);
            boolean var22 = var21.equals(this.text2);
            boolean var23 = HoveringUtils.isHovered(mouseX, mouseY, var12, var20, var13, 16.0) && mouseY >= var14 && mouseY <= var15;
            int var24 = var22
               ? ColorUtils.applyAlpha(theme, 0.85F * alphaMul)
               : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var23 ? 30 : 15), alphaMul);
            RenderUtils.drawRoundedRect(var7, var12, var20, var13, 16.0F, 4.0F, var24);
            String var25 = Lumen.INSTANCE.friendStorage.getCustomName(var21);
            String var26 = var25 != null ? var25 : var21;
            int var27 = var22 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 210), alphaMul);
            var18.draw(var7, this.resolveString(var18, var26, var13 - 16.0F - 8.0F), var12 + 7.0F, var20 + 8.0F - 4.0F + 2.0F + 1.0F, var27);
            Font var28 = Fonts.getFont("suisse", 12);
            var28.drawCenteredString(
               var7, "x", var12 + var13 - 8.0F, var20 + 2.0F + 8.0F - 4.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 120, 120, 220), alphaMul)
            );
         }
      }

      ScissorUtils.pop();
      float var40 = this.helper60(state);
      float var41 = this.helper61(state);
      if (this.text2 != null && Lumen.INSTANCE.friendStorage.isFriend(this.text2)) {
         Font var42 = Fonts.getFont("suisse", 9);
         var42.draw(
            var7,
            "КАСТОМНОЕ ИМЯ",
            var40,
            this.helper57(state) + 1.0F + 2.0F + 1.0F,
            ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 130), alphaMul)
         );
         this.helper69(
            var7,
            this.helper62(state),
            this.stringBuilder4.toString(),
            "Как отображать…",
            this.socialTextFocus == ClickGuiSettingsPanel.SocFocus.FRIEND_CUSTOM,
            theme,
            alphaMul
         );
         var42.draw(
            var7, "МОДЕЛЬ", var40, this.helper63(state) - 11.0F + 2.0F + 1.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 130), alphaMul)
         );
         if (!FiguraBridge.isFiguraPresent()) {
            Fonts.getFont("suisse", 10)
               .draw(
                  var7,
                  "Figura не найдена",
                  var40,
                  this.helper63(state) + 2.0F + 1.0F,
                  ColorUtils.applyAlpha(ColorUtils.rgba(255, 160, 160, 200), alphaMul)
               );
         } else {
            List var43 = FiguraBridge.listAvatarInfos();
            String var45 = Lumen.INSTANCE.friendStorage.getModelId(this.text2);
            float var47 = this.helper63(state);
            float var49 = this.helper64(state);
            float var50 = Math.max(0.0F, var49 - var47);
            int var51 = 1 + var43.size();
            float var52 = var51 * 18.0F;
            this.volume13 = Math.max(0.0F, var52 - var50);
            this.volume12 = MathHelper.clamp(this.volume12, 0.0F, this.volume13);
            Font var29 = Fonts.getFont("suisse", 10);
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(var40, var47, var41, var50);

            for (int var30 = 0; var30 < var51; var30++) {
               float var31 = var47 + var30 * 18.0F - this.volume12;
               if (!(var31 + 15.0F < var47) && !(var31 > var49)) {
                  boolean var32 = var30 == 0;
                  FiguraBridge.AvatarInfo var33 = var32 ? null : (FiguraBridge.AvatarInfo)var43.get(var30 - 1);
                  boolean var34 = var32 ? var45 == null : var33.id().equals(var45);
                  boolean var35 = HoveringUtils.isHovered(mouseX, mouseY, var40, var31, var41, 15.0) && mouseY >= var47 && mouseY <= var49;
                  int var36 = var34
                     ? ColorUtils.applyAlpha(theme, 0.85F * alphaMul)
                     : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, var35 ? 28 : 14), alphaMul);
                  RenderUtils.drawRoundedRect(var7, var40, var31, var41, 15.0F, 4.0F, var36);
                  float var37 = var40 + 6.0F;
                  if (!var32) {
                     Identifier var38 = FiguraAvatarIcons.icon(var33);
                     if (var38 != null) {
                        RenderUtils.drawImage(var7, var38, var40 + 3.0F, var31 + 7.5F - 5.0F, 10.0F, 10.0F, ColorUtils.applyAlpha(-1, alphaMul));
                        var37 = var40 + 16.0F;
                     }
                  }

                  String var53 = var32 ? "Нет модели" : var33.name();
                  int var39 = var34 ? ColorUtils.applyAlpha(-1, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 205), alphaMul);
                  var29.draw(var7, this.resolveString(var29, var53, var41 - (var37 - var40) - 16.0F), var37, var31 + 2.0F + 7.5F - 4.0F + 1.0F, var39);
                  if (var34) {
                     RenderUtils.drawRoundCircle(var7, var40 + var41 - 8.0F, var31 + 7.5F, 4.5F, ColorUtils.applyAlpha(-1, alphaMul));
                  }
               }
            }

            ScissorUtils.pop();
         }

         float[] var44 = this.helper65(state);
         boolean var46 = HoveringUtils.isHovered(mouseX, mouseY, var44[0], var44[1], var44[2], var44[3]);
         int var48 = ColorUtils.applyAlpha(ColorUtils.rgba(200, 60, 60, var46 ? 230 : 170), alphaMul);
         RenderUtils.drawRoundedRect(var7, var44[0], var44[1], var44[2], var44[3], 4.0F, var48);
         Fonts.getFont("suisse", 11)
            .drawCenteredString(
               var7, "x  Удалить друга", var44[0] + var44[2] / 2.0F, var44[1] + 2.0F + var44[3] / 2.0F - 4.0F + 1.0F, ColorUtils.applyAlpha(-1, alphaMul)
            );
      } else {
         Fonts.getFont("suisse", 11)
            .draw(var7, "Выбери друга слева", var40, var14 + 4.0F + 1.0F, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 120), alphaMul));
      }
   }

   private void helper69(MatrixStack m, float[] r, String text, String placeholder, boolean focused, int theme, float alphaMul) {
      RenderUtils.drawRoundedRect(m, r[0], r[1], r[2], r[3], 4.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 160), alphaMul));
      int var8 = focused ? ColorUtils.applyAlpha(theme, alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 40), alphaMul);
      RenderUtils.drawRoundedRectOutline(m, r[0], r[1], r[2], r[3], 4.0F, 0.1F, var8, var8, var8, var8);
      Font var9 = Fonts.getFont("suisse", 11);
      boolean var10 = text.isEmpty();
      String var11 = var10 && !focused ? placeholder : text + (focused ? "|" : "");
      int var12 = var10 && !focused ? ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 90), alphaMul) : ColorUtils.applyAlpha(-1, alphaMul);
      var9.draw(m, this.resolveString(var9, var11, r[2] - 10.0F), r[0] + 6.0F, r[1] + 2.0F + r[3] / 2.0F - 4.0F + 1.0F, var12);
   }

   private void helper70(MatrixStack m, float[] r, String label, boolean hov, int theme, float alphaMul) {
      int var7 = hov ? ColorUtils.applyAlpha(theme, 0.9F * alphaMul) : ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 24), alphaMul);
      RenderUtils.drawRoundedRect(m, r[0], r[1], r[2], r[3], 4.0F, var7);
      Fonts.getFont("suisse", 11)
         .drawCenteredString(m, label, r[0] + r[2] / 2.0F, r[1] + 2.0F + r[3] / 2.0F - 4.0F + 1.0F, ColorUtils.applyAlpha(-1, alphaMul));
   }

   private boolean checkCondition3(ClickGuiState state, double mouseX, double mouseY) {
      for (int var6 = 0; var6 < 2; var6++) {
         float[] var7 = this.helper53(state, var6);
         if (HoveringUtils.isHovered(mouseX, mouseY, var7[0], var7[1], var7[2], var7[3])) {
            if (this.index6 != var6) {
               this.index6 = var6;
               this.helper78();
            }

            return true;
         }
      }

      boolean var8 = this.index6 == 0 ? this.helper71(state, mouseX, mouseY) : this.helper72(state, mouseX, mouseY);
      if (var8) {
         return true;
      } else if (this.checkCondition(state, mouseX, mouseY)) {
         this.helper78();
         return true;
      } else {
         return false;
      }
   }

   private boolean helper71(ClickGuiState state, double mouseX, double mouseY) {
      float[] var6 = this.helper55(state);
      if (HoveringUtils.isHovered(mouseX, mouseY, var6[0], var6[1], var6[2], var6[3])) {
         this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.CONFIG_NAME;
         return true;
      }

      float[] var7 = this.helper56(state);
      if (HoveringUtils.isHovered(mouseX, mouseY, var7[0], var7[1], var7[2], var7[3])) {
         this.helper76();
         return true;
      }

      ArrayList var8 = new ArrayList<>(Lumen.INSTANCE.configStorage.getAvailableConfigs());
      var8.sort(String.CASE_INSENSITIVE_ORDER);
      String var9 = Lumen.INSTANCE.configStorage.currentConfig;
      float var10 = this.helper51(state);
      float var11 = this.helper52(state);
      float var12 = this.helper57(state);
      float var13 = this.helper58(state);

      for (int var14 = 0; var14 < var8.size(); var14++) {
         float var15 = var12 + var14 * 20.0F - this.volume8;
         if (!(var15 + 16.0F < var12)
            && !(var15 > var13)
            && HoveringUtils.isHovered(mouseX, mouseY, var10, var15, var11, 16.0)
            && mouseY >= var12
            && mouseY <= var13) {
            String var16 = (String)var8.get(var14);
            boolean var17 = mouseX >= var10 + var11 - 16.0F;
            if (var17 && !var16.equals(var9)) {
               Lumen.INSTANCE.configStorage.deleteConfig(var16);
            } else {
               try {
                  Lumen.INSTANCE.configStorage.loadConfig(var16);
               } catch (Exception var19) {
               }
            }

            this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
            return true;
         }
      }

      return false;
   }

   private boolean helper72(ClickGuiState state, double mouseX, double mouseY) {
      float[] var6 = this.helper55(state);
      if (HoveringUtils.isHovered(mouseX, mouseY, var6[0], var6[1], var6[2], var6[3])) {
         this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.FRIEND_NAME;
         return true;
      }

      float[] var7 = this.helper56(state);
      if (HoveringUtils.isHovered(mouseX, mouseY, var7[0], var7[1], var7[2], var7[3])) {
         this.helper75();
         return true;
      }

      List var8 = Lumen.INSTANCE.friendStorage.getFriends();
      float var9 = this.helper51(state);
      float var10 = this.helper59(state);
      float var11 = this.helper57(state);
      float var12 = this.helper58(state);

      for (int var13 = 0; var13 < var8.size(); var13++) {
         float var14 = var11 + var13 * 20.0F - this.volume10;
         if (!(var14 + 16.0F < var11)
            && !(var14 > var12)
            && HoveringUtils.isHovered(mouseX, mouseY, var9, var14, var10, 16.0)
            && mouseY >= var11
            && mouseY <= var12) {
            String var15 = (String)var8.get(var13);
            if (mouseX >= var9 + var10 - 16.0F) {
               this.helper74(var15);
            } else {
               this.helper73(var15);
            }

            return true;
         }
      }

      if (this.text2 != null && Lumen.INSTANCE.friendStorage.isFriend(this.text2)) {
         float[] var24 = this.helper62(state);
         if (HoveringUtils.isHovered(mouseX, mouseY, var24[0], var24[1], var24[2], var24[3])) {
            this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.FRIEND_CUSTOM;
            return true;
         }

         float[] var25 = this.helper65(state);
         if (HoveringUtils.isHovered(mouseX, mouseY, var25[0], var25[1], var25[2], var25[3])) {
            this.helper74(this.text2);
            return true;
         }

         if (FiguraBridge.isFiguraPresent()) {
            List var26 = FiguraBridge.listAvatarInfos();
            float var16 = this.helper60(state);
            float var17 = this.helper61(state);
            float var18 = this.helper63(state);
            float var19 = this.helper64(state);
            int var20 = 1 + var26.size();

            for (int var21 = 0; var21 < var20; var21++) {
               float var22 = var18 + var21 * 18.0F - this.volume12;
               if (!(var22 + 15.0F < var18)
                  && !(var22 > var19)
                  && HoveringUtils.isHovered(mouseX, mouseY, var16, var22, var17, 15.0)
                  && mouseY >= var18
                  && mouseY <= var19) {
                  String var23 = var21 == 0 ? null : ((FiguraBridge.AvatarInfo)var26.get(var21 - 1)).id();
                  Lumen.INSTANCE.friendStorage.setModelId(this.text2, var23);
                  FriendModelManager.INSTANCE.syncFriend(this.text2);
                  this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void helper73(String name) {
      this.text2 = name;
      this.stringBuilder4.setLength(0);
      String var2 = Lumen.INSTANCE.friendStorage.getCustomName(name);
      if (var2 != null) {
         this.stringBuilder4.append(var2);
      }

      this.volume12 = 0.0F;
      this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
   }

   private void helper74(String name) {
      Lumen.INSTANCE.friendStorage.remove(name);
      FriendModelManager.INSTANCE.syncFriend(name);
      if (name.equals(this.text2)) {
         this.text2 = null;
         this.stringBuilder4.setLength(0);
      }

      this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
   }

   private void helper75() {
      String var1 = this.stringBuilder3.toString().trim();
      if (!var1.isEmpty() && !Lumen.INSTANCE.friendStorage.isFriend(var1)) {
         Lumen.INSTANCE.friendStorage.add(var1);
         this.helper73(var1);
      }

      this.stringBuilder3.setLength(0);
      this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
   }

   private void helper76() {
      String var1 = this.stringBuilder2.toString().trim();
      if (!var1.isEmpty()) {
         try {
            Lumen.INSTANCE.configStorage.saveConfig(var1);
         } catch (Exception var3) {
         }
      }

      this.stringBuilder2.setLength(0);
      this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
   }

   private StringBuilder helper77() {
      return switch (this.socialTextFocus) {
         case CONFIG_NAME -> this.stringBuilder2;
         case FRIEND_NAME -> this.stringBuilder3;
         case FRIEND_CUSTOM -> this.stringBuilder4;
         default -> null;
      };
   }

   private void updateState8() {
      if (this.socialTextFocus == ClickGuiSettingsPanel.SocFocus.FRIEND_CUSTOM && this.text2 != null) {
         Lumen.INSTANCE.friendStorage.setCustomName(this.text2, this.stringBuilder4.toString());
      }
   }

   private void helper78() {
      this.socialTextFocus = ClickGuiSettingsPanel.SocFocus.NONE;
   }

   public boolean handleSocialKey(int keyCode, int modifiers) {
      if (this.socialTextFocus == ClickGuiSettingsPanel.SocFocus.NONE) {
         return false;
      }

      if (keyCode == 256) {
         this.helper78();
         return true;
      }

      if (keyCode == 257 || keyCode == 335) {
         switch (this.socialTextFocus) {
            case CONFIG_NAME:
               this.helper76();
               break;
            case FRIEND_NAME:
               this.helper75();
               break;
            default:
               this.helper78();
         }

         return true;
      } else if (keyCode == 259) {
         StringBuilder var3 = this.helper77();
         if (var3 != null && var3.length() > 0) {
            var3.deleteCharAt(var3.length() - 1);
            this.updateState8();
         }

         return true;
      } else {
         return true;
      }
   }

   public boolean handleSocialChar(char chr) {
      if (this.socialTextFocus == ClickGuiSettingsPanel.SocFocus.NONE) {
         return false;
      }

      if (!Character.isISOControl(chr)) {
         StringBuilder var2 = this.helper77();
         if (var2 != null) {
            var2.append(chr);
            this.updateState8();
         }
      }

      return true;
   }

   public boolean hasSocialTextFocus() {
      return this.socialTextFocus != ClickGuiSettingsPanel.SocFocus.NONE;
   }

   public boolean handleClick(ClickGuiState state, Window window, double mouseX, double mouseY, int button) {
      if (button != 0) {
         if (state.isAutosetPanelOpen() && this.checkCondition(state, mouseX, mouseY)) {
            this.autoSetPage
               .handleClick(
                  state,
                  this.helper16(state),
                  this.helper18(state),
                  this.helper17(state),
                  this.helper19(state) - this.helper18(state),
                  mouseX,
                  mouseY,
                  button
               );
            return true;
         } else {
            return false;
         }
      } else {
         if (HoveringUtils.isHovered(mouseX, mouseY, this.helper11(state), this.helper15(state), 20.0, 20.0)) {
            state.setProfilePanelOpen(!state.isProfilePanelOpen());
            return true;
         }

         if (HoveringUtils.isHovered(mouseX, mouseY, this.helper12(state), this.helper15(state), 20.0, 20.0)) {
            boolean var20 = !state.isSettingsPanelOpen();
            state.setSettingsPanelOpen(var20);
            if (var20) {
               this.flag = false;
               this.colorsTextFocus = false;
            }

            return true;
         } else {
            if (HoveringUtils.isHovered(mouseX, mouseY, this.helper13(state), this.helper15(state), 20.0, 20.0)) {
               state.setFriendsPanelOpen(!state.isFriendsPanelOpen());
               this.helper78();
               return true;
            }

            if (HoveringUtils.isHovered(mouseX, mouseY, this.helper14(state), this.helper15(state), 20.0, 20.0)) {
               state.setAutosetPanelOpen(!state.isAutosetPanelOpen());
               return true;
            }

            if (state.isAutosetPanelOpen()) {
               if (this.autoSetPage
                  .handleClick(
                     state,
                     this.helper16(state),
                     this.helper18(state),
                     this.helper17(state),
                     this.helper19(state) - this.helper18(state),
                     mouseX,
                     mouseY,
                     button
                  )) {
                  return true;
               }

               if (this.checkCondition(state, mouseX, mouseY)) {
                  return true;
               }
            }

            if (state.isFriendsPanelOpen() && this.checkCondition3(state, mouseX, mouseY)) {
               return true;
            }

            if (state.isSettingsPanelOpen() && this.checkCondition2(state, mouseX, mouseY)) {
               return true;
            }

            if (state.isProfilePanelOpen()) {
               float[] var8 = this.helper43(state);

               for (int var9 = 0; var9 < this.value.length; var9++) {
                  float[] var10 = this.helper45(var8, var9);
                  if (mouseX >= var10[0] - 3.0F && mouseX <= var10[0] + var10[2] + 3.0F && mouseY >= var10[1] - 7.0F && mouseY <= var10[1] + var10[3] + 7.0F) {
                     this.index5 = var9;
                     this.updateState7(var9, var8, mouseX);
                     return true;
                  }
               }

               float var21 = this.helper44(var8);

               for (int var22 = 0; var22 < this.value2.length; var22++) {
                  float[] var11 = this.helper47(var8, var22);
                  if (mouseY >= var11[1] - 4.0F && mouseY <= var11[1] + var11[3] + 4.0F && mouseX >= var11[0] - 2.0F && mouseX <= var11[0] + var21) {
                     this.value2[var22].set().accept(!this.value2[var22].get().get());
                     return true;
                  }
               }

               for (int var23 = 0; var23 < this.resolveInt2(); var23++) {
                  float[] var25 = this.helper49(var8, var23);
                  if (HoveringUtils.isHovered(mouseX, mouseY, var25[0], var25[1], var25[2], var25[3])) {
                     GuiGifManager.get().select(var23 == 0 ? null : GuiGifManager.KEYS[var23 - 1]);
                     return true;
                  }
               }

               float var24 = this.helper(state);
               float var26 = this.helper2(state);
               FiguraBridge.Category[] var12 = FiguraBridge.Category.values();

               for (int var13 = 0; var13 < var12.length; var13++) {
                  float[] var14 = this.helper7(state, var13);
                  if (HoveringUtils.isHovered(mouseX, mouseY, var14[0], var14[1], var14[2], var14[3])) {
                     if (this.index4 != var13) {
                        this.index4 = var13;
                        this.volume5 = 0.0F;
                        this.volume6 = 0.0F;
                        this.animationUtils.setValue(0.0F);
                     }

                     return true;
                  }
               }

               float var27 = this.helper4(state);
               float var28 = this.helper5(state);
               int var15 = MathHelper.clamp(this.index4, 0, var12.length - 1);
               List var16 = FiguraBridge.listAvatarInfos(var12[var15]);
               int var17 = 1 + var16.size();

               for (int var18 = 0; var18 < var17; var18++) {
                  float var19 = var27 + var18 * 19.0F - this.volume5;
                  if (!(var19 + 16.0F < var27)
                     && !(var19 > var28)
                     && HoveringUtils.isHovered(mouseX, mouseY, var24, var19, var26, 16.0)
                     && mouseY >= var27
                     && mouseY <= var28) {
                     if (var18 == 0) {
                        FiguraBridge.clearAll();
                     } else {
                        FiguraBridge.applyAvatar(((FiguraBridge.AvatarInfo)var16.get(var18 - 1)).id());
                     }

                     CustomModelManager.setSelectedModel(CustomModelType.DEFAULT);
                     return true;
                  }
               }

               if (this.checkCondition(state, mouseX, mouseY)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean handleScroll(ClickGuiState state, double mouseX, double mouseY, double amount) {
      if (state.isAutosetPanelOpen() && this.checkCondition(state, mouseX, mouseY)) {
         this.autoSetPage
            .handleScroll(
               this.helper16(state),
               this.helper18(state),
               this.helper17(state),
               this.helper19(state) - this.helper18(state),
               mouseX,
               mouseY,
               amount
            );
         return true;
      }

      if (state.isSettingsPanelOpen() && this.checkCondition(state, mouseX, mouseY)) {
         return true;
      }

      if (state.isFriendsPanelOpen() && this.checkCondition(state, mouseX, mouseY)) {
         float var8 = (float)amount * 20.0F;
         if (this.index6 == 0) {
            this.volume8 = MathHelper.clamp(this.volume8 - var8, 0.0F, this.volume9);
         } else if (mouseX >= this.helper60(state)) {
            this.volume12 = MathHelper.clamp(this.volume12 - var8, 0.0F, this.volume13);
         } else {
            this.volume10 = MathHelper.clamp(this.volume10 - var8, 0.0F, this.volume11);
         }

         return true;
      } else if (state.isProfilePanelOpen() && this.checkCondition(state, mouseX, mouseY)) {
         this.volume6 = MathHelper.clamp(this.volume6 - (float)amount * 22.0F, 0.0F, this.volume7);
         return true;
      } else {
         return false;
      }
   }

   public void handleRelease() {
      this.index5 = -1;
      boolean var1 = this.index3 != 0;
      this.index3 = 0;
      if (var1) {
         this.updateState3();
      }
   }

   public boolean handleDrag(ClickGuiState state, Window window, double mouseX, double mouseY) {
      if (this.index5 >= 0 && state.isProfilePanelOpen()) {
         this.updateState7(this.index5, this.helper43(state), mouseX);
         return true;
      }

      if (this.index3 != 0 && state.isSettingsPanelOpen()) {
         switch (this.index3) {
            case 1:
               this.updateState4(state, mouseX, mouseY);
               break;
            case 2:
               this.updateState5(state, mouseX);
               break;
            case 3:
               this.updateState6(state, mouseX);
         }

         return true;
      } else {
         return (state.isProfilePanelOpen() || state.isFriendsPanelOpen() || state.isSettingsPanelOpen() || state.isAutosetPanelOpen())
            && this.checkCondition(state, mouseX, mouseY);
      }
   }

   private record Slider(String name, float min, float max, Supplier<Float> get, Consumer<Float> set) {

      private Slider(String name, float min, float max, Supplier<Float> get, Consumer<Float> set) {
         this.name = name;
         this.min = min;
         this.max = max;
         this.get = get;
         this.set = set;
      }

      public String name() {
         return this.name;
      }

      public float min() {
         return this.min;
      }

      public float max() {
         return this.max;
      }

      public Supplier<Float> get() {
         return this.get;
      }

      public Consumer<Float> set() {
         return this.set;
      }
   }

   private enum SocFocus {
      NONE,
      CONFIG_NAME,
      FRIEND_NAME,
      FRIEND_CUSTOM;
   }

   private record Toggle(String name, Supplier<Boolean> get, Consumer<Boolean> set) {

      private Toggle(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
         this.name = name;
         this.get = get;
         this.set = set;
      }

      public String name() {
         return this.name;
      }

      public Supplier<Boolean> get() {
         return this.get;
      }

      public Consumer<Boolean> set() {
         return this.set;
      }
   }
}
