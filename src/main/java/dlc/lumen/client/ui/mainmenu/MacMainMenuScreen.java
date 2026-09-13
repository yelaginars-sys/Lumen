package dlc.lumen.client.ui.mainmenu;

import dlc.lumen.api.utils.client.ClientSoundPlayer;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.ui.mainmenu.account.AccountGuiScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class MacMainMenuScreen extends Screen implements QClient {
   private static boolean flag = false;
   private final List<MacMainMenuScreen.MenuButton> macMainMenuScreens = new ArrayList<>();
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 7.5F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils2 = new AnimationUtils(0.0F, 6.5F, Easings.CUBIC_OUT);

   public MacMainMenuScreen() {
      super(Text.empty());
   }

   @Override
   protected void init() {
      super.init();
      if (!flag) {
         flag = true;
         AccountGuiScreen.MANAGER.restoreLastSession();
      }

      this.animationUtils2.setValue(0.0F);
      float var1 = 8.0F;
      float var2 = 172.0F;
      float var3 = 26.0F;
      float var4 = this.width / 2.0F - var2 / 2.0F;
      float var5 = this.height / 2.0F + 34.0F;
      this.macMainMenuScreens.clear();
      this.macMainMenuScreens
         .add(new MacMainMenuScreen.MenuButton(var4, var5, var2, var3, "Singleplayer", button -> this.client.setScreen(new SelectWorldScreen(this))));
      this.macMainMenuScreens
         .add(
            new MacMainMenuScreen.MenuButton(
               var4, var5 + (var3 + var1), var2, var3, "Multiplayer", button -> this.client.setScreen(new MultiplayerScreen(this))
            )
         );
      this.macMainMenuScreens
         .add(
            new MacMainMenuScreen.MenuButton(
               var4,
               var5 + (var3 + var1) * 2.0F,
               var2 / 2.0F - var1 / 2.0F,
               var3,
               "Options",
               button -> this.client.setScreen(new OptionsScreen(this, mc.options))
            )
         );
      this.macMainMenuScreens
         .add(
            new MacMainMenuScreen.MenuButton(
               var4 + var2 / 2.0F + var1 / 2.0F,
               var5 + (var3 + var1) * 2.0F,
               var2 / 2.0F - var1 / 2.0F,
               var3,
               "Accounts",
               button -> this.client.setScreen(new AccountGuiScreen(this))
            )
         );
      this.macMainMenuScreens
         .add(
            new MacMainMenuScreen.MenuButton(var4, var5 + (var3 + var1) * 3.0F, var2, var3, "Exit", button -> this.client.scheduleStop())
               .helper(true)
         );
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.animationUtils2.update(1.0F);
      float var5 = MathHelper.clamp(this.animationUtils2.getValue(), 0.0F, 1.0F);
      this.helper(context, var5);
      this.helper2(context, mouseX, mouseY, var5);

      for (int var6 = 0; var6 < this.macMainMenuScreens.size(); var6++) {
         float var7 = MathHelper.clamp((var5 - var6 * 0.09F) / Math.max(0.2F, 1.0F - var6 * 0.09F), 0.0F, 1.0F);
         this.macMainMenuScreens.get(var6).helper2(context, mouseX, mouseY, var7);
      }

      this.helper3(context, var5);
      super.render(context, mouseX, mouseY, delta);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      for (MacMainMenuScreen.MenuButton var7 : this.macMainMenuScreens) {
         if (var7.helper3(mouseX, mouseY, button)) {
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   private void helper(DrawContext context, float reveal) {
      float var3 = 1.0F - (float)Math.pow(1.0F - reveal, 3.0);
      int var4 = ColorUtils.rgb(12, 18, 32);
      int var5 = ColorUtils.rgb(28, 40, 63);
      context.fillGradient(0, 0, this.width, this.height, var4, var5);
      context.draw();
      int var6 = helper5();
      RenderUtils.drawRoundCircle(
         context.getMatrices(),
         this.width * 0.18F,
         this.height * 0.2F,
         180.0F,
         helper7(ColorUtils.interpolateColor(var6, ColorUtils.rgb(255, 255, 255), 0.45F), (int)(24.0F * var3))
      );
      RenderUtils.drawRoundCircle(
         context.getMatrices(),
         this.width * 0.82F,
         this.height * 0.28F,
         220.0F,
         helper7(ColorUtils.interpolateColor(helper6(90), ColorUtils.rgb(130, 160, 255), 0.25F), (int)(22.0F * var3))
      );
      RenderUtils.drawRoundCircle(
         context.getMatrices(), this.width * 0.75F, this.height * 0.82F, 260.0F, helper7(ColorUtils.rgb(255, 255, 255), (int)(12.0F * var3))
      );
      float var7 = this.width / 2.0F - 152.0F;
      float var8 = this.height / 2.0F - 98.0F;
      float var9 = 0.97F + var3 * 0.03F;
      context.getMatrices().push();
      context.getMatrices().translate(this.width / 2.0F, this.height / 2.0F, 0.0F);
      context.getMatrices().scale(var9, var9, 1.0F);
      context.getMatrices().translate(-this.width / 2.0F, -this.height / 2.0F, 0.0F);
      RenderUtils.drawShadow(context.getMatrices(), var7, var8, 304.0F, 244.0F, 16.0F, 26.0F, helper7(ColorUtils.rgb(4, 7, 14), (int)(115.0F * var3)));
      RenderUtils.drawGradientRect(
         context.getMatrices(),
         var7,
         var8,
         304.0F,
         244.0F,
         16.0F,
         helper7(ColorUtils.darken(helper6(45), 0.18F), (int)(210.0F * var3)),
         helper7(ColorUtils.darken(helper6(180), 0.12F), (int)(232.0F * var3))
      );
      RenderUtils.drawRoundedRect(
         context.getMatrices(), var7 + 1.0F, var8 + 1.0F, 302.0F, 242.0F, 15.0F, helper7(ColorUtils.rgb(9, 13, 22), (int)(82.0F * var3))
      );
      RenderUtils.drawRoundedRectOutline(
         context.getMatrices(),
         var7,
         var8,
         304.0F,
         244.0F,
         16.0F,
         16.0F,
         16.0F,
         16.0F,
         0.9F,
         helper7(var6, (int)(155.0F * var3)),
         helper7(helper6(90), (int)(84.0F * var3)),
         helper7(helper6(180), (int)(66.0F * var3)),
         helper7(var6, (int)(138.0F * var3))
      );
      context.getMatrices().pop();
   }

   private void helper2(DrawContext context, int mouseX, int mouseY, float reveal) {
      Font var5 = helper4("sf_regular", 14);
      Font var6 = helper4("sf_regular", 12);
      Font var7 = helper4("suisse", 40);
      Font var8 = helper4("iconnew", 72);
      float var9 = this.width / 2.0F;
      float var10 = 1.0F - (float)Math.pow(1.0F - reveal, 3.0);
      float var11 = (1.0F - var10) * 16.0F;
      float var12 = Math.max(74.0F, this.height / 2.0F - 118.0F) + var11;
      float var13 = var8 != null ? var8.getHeight() : 0.0F;
      float var14 = var8 != null ? var8.getWidth("A") : 0.0F;
      boolean var15 = HoveringUtils.isHovered(mouseX, mouseY, var9 - var14 / 2.0F, var12, var14, var13);
      this.animationUtils.update(var15 ? 1.0F : 0.0F);
      if (var8 != null) {
         float var16 = 0.96F + var10 * 0.04F + 0.055F * MathHelper.clamp(this.animationUtils.getValue(), 0.0F, 1.0F);
         context.getMatrices().push();
         context.getMatrices().translate(var9, var12 + var13 / 2.0F, 0.0F);
         context.getMatrices().scale(var16, var16, 1.0F);
         var8.drawCenteredString(context.getMatrices(), "A", 0.0F, -var13 / 2.0F, helper7(helper5(), (int)(255.0F * var10)));
         context.getMatrices().pop();
      }

      if (var7 != null) {
         var7.drawCenteredString(context.getMatrices(), "Lumen", var9, var12 + 66.0F, helper7(helper5(), (int)(255.0F * var10)));
         float var21 = var7.getWidth("Lumen") * 0.58F;
         RenderUtils.drawRoundedRect(
            context.getMatrices(), var9 - var21 / 2.0F, var12 + 96.0F, var21, 1.4F, 0.7F, helper7(helper5(), (int)(88.0F * var10))
         );
      }

      String var22 = "User";
      if (var5 != null) {
         String var17 = "Hello, ";
         float var18 = var5.getWidth(var17);
         float var19 = var5.getWidth(var22);
         float var20 = var9 - (var18 + var19) / 2.0F;
         var5.draw(context.getMatrices(), var17, var20, var12 + 110.0F, helper7(-1, (int)(255.0F * var10)));
         var5.draw(context.getMatrices(), var22, var20 + var18, var12 + 110.0F, helper7(helper5(), (int)(255.0F * var10)));
      }

      if (var6 != null) {
         var6.drawCenteredString(
            context.getMatrices(), "offline workspace and account manager", var9, var12 + 128.0F, helper7(helper6(90), (int)(190.0F * var10))
         );
      }
   }

   private void helper3(DrawContext context, float reveal) {
      Font var3 = helper4("sf_regular", 12);
      if (var3 != null) {
         var3.drawCenteredString(
            context.getMatrices(),
            "Custom main menu with separate account manager",
            this.width / 2.0F,
            this.height - var3.getHeight() - 8.0F,
            helper7(helper6(90), (int)(130.0F * reveal))
         );
      }
   }

   private static Font helper4(String name, int size) {
      return Fonts.getFont(name, size);
   }

   private static int helper5() {
      try {
         return ColorUtils.getThemeColor();
      } catch (Exception var1) {
         return ColorUtils.rgb(106, 145, 255);
      }
   }

   private static int helper6(int index) {
      try {
         return ColorUtils.getThemeColor(index);
      } catch (Exception var2) {
         return helper5();
      }
   }

   private static int helper7(int color, int alpha) {
      return ColorUtils.setAlphaColor(color, MathHelper.clamp(alpha, 1, 255));
   }

   private static void updateState() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null) {
         ClientSoundPlayer.playGuiClick();
      }
   }

   private static final class MenuButton {
      private final float flag;
      private final float macMainMenuScreens;
      private final float animationUtils;
      private final float animationUtils2;
      private final String text2;
      private final MacMainMenuScreen.Pressable pressable;
      private final AnimationUtils animationUtils3 = new AnimationUtils(0.0F, 7.5F, Easings.CUBIC_OUT);
      private boolean flag2;

      private MenuButton(float x, float y, float width, float height, String text, MacMainMenuScreen.Pressable action) {
         this.flag = x;
         this.macMainMenuScreens = y;
         this.animationUtils = width;
         this.animationUtils2 = height;
         this.text2 = text;
         this.pressable = action;
      }

      private MacMainMenuScreen.MenuButton helper(boolean danger) {
         this.flag2 = danger;
         return this;
      }

      private void helper2(DrawContext context, int mouseX, int mouseY, float revealProgress) {
         float var5 = 1.0F - (float)Math.pow(1.0F - revealProgress, 3.0);
         float var6 = this.macMainMenuScreens + (1.0F - var5) * 14.0F;
         float var7 = var5;
         float var8 = 0.96F + var5 * 0.04F;
         boolean var9 = HoveringUtils.isHovered(mouseX, mouseY, this.flag, var6, this.animationUtils, this.animationUtils2);
         this.animationUtils3.update(var9 ? 1.0F : 0.0F);
         float var10 = MathHelper.clamp(this.animationUtils3.getValue(), 0.0F, 1.0F);
         int var11 = this.flag2 ? ColorUtils.rgb(255, 120, 126) : MacMainMenuScreen.helper5();
         int var12 = ColorUtils.interpolateColor(
            MacMainMenuScreen.helper7(ColorUtils.darken(MacMainMenuScreen.helper6(45), 0.18F), (int)(218.0F * var7)),
            MacMainMenuScreen.helper7(ColorUtils.darken(var11, 0.28F), (int)(236.0F * var7)),
            var10
         );
         int var13 = ColorUtils.interpolateColor(
            MacMainMenuScreen.helper7(ColorUtils.darken(MacMainMenuScreen.helper6(180), 0.14F), (int)(232.0F * var7)),
            MacMainMenuScreen.helper7(ColorUtils.darken(var11, 0.18F), (int)(246.0F * var7)),
            var10
         );
         int var14 = MacMainMenuScreen.helper7(
            ColorUtils.interpolateColor(ColorUtils.rgb(255, 255, 255), var11, var10 * 0.3F), (int)((14.0F + var10 * 18.0F) * var7)
         );
         context.getMatrices().push();
         context.getMatrices().translate(this.flag + this.animationUtils / 2.0F, var6 + this.animationUtils2 / 2.0F, 0.0F);
         context.getMatrices().scale(var8, var8, 1.0F);
         context.getMatrices().translate(-(this.flag + this.animationUtils / 2.0F), -(var6 + this.animationUtils2 / 2.0F), 0.0F);
         RenderUtils.drawGradientRect(context.getMatrices(), this.flag, var6, this.animationUtils, this.animationUtils2, 7.0F, var12, var13);
         RenderUtils.drawRoundedRect(
            context.getMatrices(), this.flag + 1.0F, var6 + 1.0F, this.animationUtils - 2.0F, this.animationUtils2 - 2.0F, 6.0F, var14
         );
         RenderUtils.drawRoundedRectOutline(
            context.getMatrices(),
            this.flag,
            var6,
            this.animationUtils,
            this.animationUtils2,
            7.0F,
            7.0F,
            7.0F,
            7.0F,
            0.85F,
            MacMainMenuScreen.helper7(var11, (int)((126.0F + var10 * 46.0F) * var7)),
            MacMainMenuScreen.helper7(MacMainMenuScreen.helper6(90), (int)((72.0F + var10 * 20.0F) * var7)),
            MacMainMenuScreen.helper7(MacMainMenuScreen.helper6(180), (int)((60.0F + var10 * 18.0F) * var7)),
            MacMainMenuScreen.helper7(var11, (int)((118.0F + var10 * 38.0F) * var7))
         );
         Font var15 = Fonts.getFont("suisse", 14);
         if (var15 != null) {
            int var16 = ColorUtils.interpolateColor(
               MacMainMenuScreen.helper7(-1, (int)(255.0F * var7)), MacMainMenuScreen.helper7(var11, (int)(255.0F * var7)), var10 * 0.3F
            );
            var15.drawCenteredString(context.getMatrices(), this.text2, this.flag + this.animationUtils / 2.0F, var6 + 7.8F, var16);
         }

         context.getMatrices().pop();
      }

      private boolean helper3(double mouseX, double mouseY, int button) {
         if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, this.flag, this.macMainMenuScreens, this.animationUtils, this.animationUtils2)) {
            MacMainMenuScreen.updateState();
            this.pressable.onPress(this);
            return true;
         } else {
            return false;
         }
      }
   }

   private interface Pressable {
      void onPress(MacMainMenuScreen.MenuButton var1);
   }
}
