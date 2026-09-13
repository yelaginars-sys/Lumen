package dlc.lumen.client.ui.mainmenu.account;

import dlc.lumen.client.ui.mainmenu.MenuParticles;
import net.minecraft.util.Identifier;
import dlc.lumen.api.utils.client.ClientSoundPlayer;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.ui.mainmenu.MenuBackground;
import dlc.lumen.client.ui.mainmenu.account.generator.MainGenerator;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public final class AccountGuiScreen extends Screen implements QClient {
   public static final AccountManager MANAGER = new AccountManager();
   private static final float VOLUME = 400.0F;
   private static final float VOLUME2 = 160.0F;
   private static final float VOLUME3 = 10.0F;
   private static final float VOLUME4 = 25.0F;
   private static final Identifier MENU_BG = Identifier.of("lumen", "textures/mainmenu/menu_bg.png");
   private final Map<String, Float> strings = new HashMap<>();
   private final Screen screen2;
   private final AccountGuiScreen.TextField accountGuiScreen = new AccountGuiScreen.TextField("Nickname", "icon", "e");
   private final AccountGuiScreen.TextField accountGuiScreen2 = new AccountGuiScreen.TextField("Search", "icon1", "l");
   private Account account2;
   private float volume;
   private float volume2;
   private float volume3;

   public AccountGuiScreen(Screen parent) {
      super(Text.empty());
      this.screen2 = parent;
      this.helper12();
   }

   private void helper(Account account) {
      this.account2 = account;
      MANAGER.saveLastSelected(account != null ? account.name() : null);
   }

   @Override
   protected void init() {
      super.init();
      this.helper12();
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      MatrixStack var5 = context.getMatrices();
      if (mc.getResourceManager().getResource(MENU_BG).isPresent()) {
         float bgScale = Math.max(this.width / 2560.0F, this.height / 1440.0F);
         float bgW = 2560.0F * bgScale;
         float bgH = 1440.0F * bgScale;
         RenderUtils.drawImage(var5, MENU_BG, (this.width - bgW) / 2.0F, (this.height - bgH) / 2.0F, bgW, bgH, -1);
         RenderUtils.drawRoundedRect(var5, 0.0F, 0.0F, this.width, this.height, 0.0F, ColorUtils.setAlphaColor(ColorUtils.rgb(8, 9, 14), 175));
      } else {
         RenderUtils.drawRoundedRect(var5, 0.0F, 0.0F, this.width, this.height, 0.0F, ColorUtils.rgb(9, 10, 15));
      }

      int lumenGlow = ColorUtils.getThemeColor();
      RenderUtils.drawRoundCircle(var5, this.width * 0.5F, this.height * 0.35F, this.height * 0.7F, ColorUtils.setAlphaColor(lumenGlow, 26));
      MenuParticles.render(context, this.width, this.height, mouseX, mouseY, 1.0F);
      float var6 = this.width / 2.0F - 200.0F;
      float var7 = this.height / 2.0F - 80.0F + 40.0F;
      float var8 = 150.94339F;
      float var9 = var6 + var8 + 10.0F;
      float var10 = 400.0F - var8;
      float var11 = 75.0F;
      this.volume = MathHelper.lerp(0.18F * delta, this.volume, this.volume2);
      this.helper5(var5, var6, var7, var8, var11);
      this.helper5(var5, var6, var7 + var11 + 10.0F, var8, var11);
      this.helper5(var5, var9, var7, var10, 160.0F);
      this.helper3(var5, mouseX, mouseY, var9, var7, var10, 160.0F);
      this.helper2(var5, mouseX, mouseY, var6, var7, var8, var11);
      Font var12 = helper21("suisse", 15);
      helper23(
         var12, var5, "© Lumen Client 2026", this.width / 2.0F, this.height - helper25(var12) - 4.0F, helper20(-1, 127)
      );
      super.render(context, mouseX, mouseY, delta);
   }

   private void helper2(MatrixStack matrices, int mouseX, int mouseY, float panelX, float panelY, float leftWidth, float panelPart) {
      float var8 = panelX + 10.0F;
      float var9 = leftWidth - 20.0F;
      float var10 = 27.0F;
      float var11 = 8.0F;
      float var12 = (panelPart - var10 * 2.0F - var11) / 2.0F;
      float var13 = panelY + var12;
      float var14 = var13 + var10 + var11;
      this.helper6(matrices, var8, var13, var9, var10, mouseX, mouseY);
      this.helper6(matrices, var8, var14, var9, var10, mouseX, mouseY);
      this.accountGuiScreen.helper(var8 + 5.0F, var13, var9 - 10.0F, var10);
      this.accountGuiScreen2.helper(var8 + 5.0F, var14, var9 - 10.0F, var10);
      this.accountGuiScreen.helper2(matrices);
      this.accountGuiScreen2.helper2(matrices);
      float var15 = panelY + panelPart + 20.0F;
      float var16 = leftWidth - 20.0F;
      float var17 = var16 / 2.0F - 6.0F;
      this.helper7(matrices, var8, var15, var16, 25.0F, "Add", false, mouseX, mouseY);
      this.helper7(matrices, var8, var15 + 25.0F + 5.0F, var17, 25.0F, "Generate", false, mouseX, mouseY);
      this.helper7(matrices, var8 + var17 + 12.0F, var15 + 25.0F + 5.0F, var17, 25.0F, "Clear all", true, mouseX, mouseY);
   }

   private void helper3(MatrixStack matrices, int mouseX, int mouseY, float x2, float y2, float width, float height) {
      List<Account> var8 = this.helper13();
      float var9 = (width - 30.0F) / 2.0F;
      float var10 = 0.0F;
      int var11 = 0;
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(x2, y2 + 10.0F, width, height - 20.0F);

      for (Account var13 : var8) {
         float var14 = x2 + 10.0F + (var9 + 10.0F) * var11;
         float var15 = y2 + 10.0F + this.volume + var10;
         this.helper4(matrices, var13, var14, var15, var9, 28.0F, mouseX, mouseY);
         if (++var11 > 1) {
            var11 = 0;
            var10 += 34.0F;
         }
      }

      if (var8.isEmpty()) {
         Font var16 = helper21("suisse", 13);
         helper23(
            var16,
            matrices,
            "No accounts",
            x2 + width / 2.0F,
            y2 + height / 2.0F - helper25(var16) / 2.0F,
            helper20(-1, 180)
         );
      }

      ScissorUtils.pop();
      if (var11 != 0) {
         var10 += 34.0F;
      }

      float var17 = var10 > 0.0F ? var10 - 10.0F : 0.0F;
      this.volume3 = Math.min(0.0F, height - var17 - 20.0F);
      this.volume2 = MathHelper.clamp(this.volume2, this.volume3, 0.0F);
      this.volume = MathHelper.clamp(this.volume, this.volume3, 0.0F);
   }

   private void helper4(MatrixStack matrices, Account account, float x2, float y2, float width, float height, int mouseX, int mouseY) {
      boolean var9 = HoveringUtils.isHovered(mouseX, mouseY, x2, y2, width, height);
      boolean var10 = this.helper14(account);
      boolean var11 = this.account2 == account;
      int var12 = ColorUtils.interpolateColor(
         helper20(ColorUtils.darken(helper19(180), 0.13F), 166),
         helper20(ColorUtils.darken(helper19(45), 0.2F), 196),
         var9 ? 1.0F : 0.0F
      );
      RenderUtils.drawRoundedRect(matrices, x2, y2, width, height, 4.0F, var12);
      if (var10 || var11) {
         RenderUtils.drawRoundedRectOutline(
            matrices,
            x2,
            y2,
            width,
            height,
            4.0F,
            1.2F,
            helper20(-1, 210),
            helper20(-1, 130),
            helper20(-1, 130),
            helper20(-1, 210)
         );
      }

      RenderUtils.drawPlayerHead(matrices, account.name(), x2 + 5.0F, y2 + (height - 18.0F) / 2.0F, 18.0F, 3.0F);
      Font var13 = helper21("suisse", 12);
      Font var14 = helper21("suisse", 10);
      float var15 = x2 + 26.0F;
      float var16 = width - height - 26.0F;
      int var17 = -1;
      this.helper26(var13, matrices, account.name(), var15, y2 + 5.0F, var16, var17);
      helper22(
         var14,
         matrices,
         account.creationDate().format(DateTimeFormatter.ofPattern("dd MMMM HH:mm", Locale.ENGLISH)),
         var15,
         y2 + 16.0F,
         helper20(-1, 76)
      );
      Font var18 = helper21("icon", 16);
      int var19 = account.favorite() ? ColorUtils.rgba(255, 160, 102, 255) : helper20(-1, 95);
      helper22(var18, matrices, "D", x2 + width - 15.0F, y2 + 5.5F, var19);
      helper22(var18, matrices, "C", x2 + width - 15.0F, y2 + height - 13.5F, ColorUtils.rgba(255, 101, 104, 230));
   }

   private void helper5(MatrixStack matrices, float x2, float y2, float width, float height) {
      int var6 = ColorUtils.rgba(13, 14, 20, 190);
      int var7 = ColorUtils.rgba(19, 21, 30, 205);
      RenderUtils.drawGradientRect(matrices, x2, y2, width, height, 8.0F, var6, var7);
      RenderUtils.drawRoundedRectOutline(
         matrices,
         x2,
         y2,
         width,
         height,
         8.0F,
         0.9F,
         helper20(-1, 150),
         helper20(-1, 80),
         helper20(-1, 60),
         helper20(-1, 140)
      );
   }

   private void helper6(MatrixStack matrices, float x2, float y2, float width, float height, int mouseX, int mouseY) {
      boolean var8 = HoveringUtils.isHovered(mouseX, mouseY, x2, y2, width, height);
      RenderUtils.drawRoundedRect(
         matrices, x2, y2, width, height, 5.0F, ColorUtils.rgba(19, 21, 30, var8 ? 205 : 170)
      );
      RenderUtils.drawRoundedRectOutline(matrices, x2, y2, width, height, 5.0F, 5.0F, 5.0F, 5.0F, 1.0F, helper20(-1, var8 ? 190 : 80));
   }

   private void helper7(MatrixStack matrices, float x2, float y2, float width, float height, String text, boolean danger, int mouseX, int mouseY) {
      boolean var10 = HoveringUtils.isHovered(mouseX, mouseY, x2, y2, width, height);
      int var11 = danger ? ColorUtils.rgba(255, 101, 104, 255) : -1;
      int var12 = helper20(ColorUtils.darken(var11, var10 ? 0.24F : 0.15F), var10 ? 205 : 150);
      RenderUtils.drawRoundedRect(matrices, x2, y2, width, height, 6.0F, var12);
      this.helper8(matrices, x2, y2, width, height, var10 ? 1.0F : 0.0F);
      Font var13 = helper21("suisse", 16);
      helper23(
         var13,
         matrices,
         text,
         x2 + width / 2.0F,
         y2 + height / 1.5F - helper25(var13) / 2.0F + 2.0F,
         -1
      );
   }

   private void helper8(MatrixStack matrices, float x2, float y2, float width, float height, float hoverProgress) {
      float var7 = (float)((Math.sin(System.currentTimeMillis() * 0.0016) + 1.0) * 0.5);
      int var8 = (int)(112.0F + 42.0F * hoverProgress + 12.0F * var7);
      int var9 = (int)(18.0F + 18.0F * hoverProgress);
      int var10 = (int)(42.0F + 22.0F * hoverProgress);
      int var11 = helper20(helper18(), var8);
      int var12 = helper20(helper19(90), var10);
      int var13 = helper20(ColorUtils.darken(helper19(180), 0.55F), var9);
      int var14 = helper20(helper19(180), var8);
      int var15 = helper20(helper19(45), (int)(24.0F + 26.0F * hoverProgress));
      var11 = ColorUtils.interpolateColor(var11, var15, var7 * 0.18F);
      var14 = ColorUtils.interpolateColor(var14, var15, (1.0F - var7) * 0.18F);
      RenderUtils.drawRoundedRectOutline(matrices, x2, y2, width, height, 6.0F, 6.0F, 6.0F, 6.0F, 0.8F, var11, var12, var13, var14);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      float var6 = this.width / 2.0F - 200.0F;
      float var7 = this.height / 2.0F - 80.0F + 40.0F;
      float var8 = 150.94339F;
      float var9 = var6 + var8 + 10.0F;
      float var10 = 400.0F - var8;
      float var11 = 75.0F;
      float var12 = var7 + var11 + 20.0F;
      float var13 = var8 - 20.0F;
      float var14 = var13 / 2.0F - 6.0F;
      this.accountGuiScreen.helper3(mouseX, mouseY, button);
      this.accountGuiScreen2.helper3(mouseX, mouseY, button);
      if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var6 + 10.0F, var12, var13, 25.0)) {
         helper17();
         this.helper10(this.accountGuiScreen.helper8());
         return true;
      } else if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var6 + 10.0F, var12 + 25.0F + 5.0F, var14, 25.0)) {
         helper17();
         this.helper11(MainGenerator.generate(), false);
         return true;
      } else if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var6 + 10.0F + var14 + 12.0F, var12 + 25.0F + 5.0F, var14, 25.0)) {
         helper17();
         MANAGER.clearAccounts();
         this.helper(null);
         return true;
      } else {
         return HoveringUtils.isHovered(mouseX, mouseY, var9, var7, var10, 160.0) && this.helper9(mouseX, mouseY, button, var9, var7, var10)
            ? true
            : super.mouseClicked(mouseX, mouseY, button);
      }
   }

   private boolean helper9(double mouseX, double mouseY, int button, float listX, float panelY, float listWidth) {
      List<Account> var9 = this.helper13();
      float var10 = (listWidth - 30.0F) / 2.0F;
      float var11 = 0.0F;
      int var12 = 0;

      for (Account var14 : var9) {
         float var15 = listX + 10.0F + (var10 + 10.0F) * var12;
         float var16 = panelY + 10.0F + this.volume + var11;
         if (HoveringUtils.isHovered(mouseX, mouseY, var15 + var10 - 29.0F, var16 + 7.0F, 18.0, 16.0)) {
            var14.toggleFavorite();
            MANAGER.save();
            helper17();
            return true;
         }

         if (HoveringUtils.isHovered(mouseX, mouseY, var15 + var10 - 20.0F, var16 + 24.0F, 16.0, 16.0)
            || button == 1 && HoveringUtils.isHovered(mouseX, mouseY, var15, var16, var10, 40.0)) {
            MANAGER.removeAccount(var14.name());
            if (this.account2 == var14) {
               this.helper(MANAGER.stream().findFirst().orElse(null));
            }

            helper17();
            return true;
         }

         if (HoveringUtils.isHovered(mouseX, mouseY, var15, var16, var10, 40.0)) {
            if (button == 0) {
               helper15(var14.name());
               this.helper(var14);
               MANAGER.save();
               helper17();
            }

            return true;
         }

         if (++var12 > 1) {
            var12 = 0;
            var11 += 50.0F;
         }
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.volume2 = MathHelper.clamp(this.volume2 + (float)verticalAmount * 17.5F, this.volume3, 0.0F);
      return true;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.accountGuiScreen.helper4(keyCode)) {
         if (keyCode == 257) {
            this.helper10(this.accountGuiScreen.helper8());
            this.accountGuiScreen.helper9(false);
         }

         return true;
      } else if (this.accountGuiScreen2.helper4(keyCode)) {
         if (keyCode == 257) {
            this.accountGuiScreen2.helper9(false);
         }

         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      boolean var3 = this.accountGuiScreen.helper5(chr);
      var3 = this.accountGuiScreen2.helper5(chr) || var3;
      return var3 || super.charTyped(chr, modifiers);
   }

   @Override
   public void close() {
      MANAGER.save();
      if (mc != null) {
         mc.setScreen(this.screen2);
      }
   }

   private void helper10(String name) {
      this.helper11(name, true);
   }

   private void helper11(String name, boolean switchTo) {
      if (name != null) {
         String var3 = name.trim();
         if (var3.length() >= 3 && var3.length() <= 16 && var3.matches("[A-Za-z0-9_]+")) {
            Account var4 = MANAGER.getAccount(var3).orElseGet(() -> {
               Account var1 = new Account(LocalDateTime.now(), var3);
               MANAGER.addAccount(var1);
               return var1;
            });
            if (switchTo || this.account2 == null) {
               this.helper(var4);
               helper15(var4.name());
            }

            this.accountGuiScreen.helper10();
            MANAGER.save();
         }
      }
   }

   private void helper12() {
      if (this.account2 == null) {
         String var2 = MANAGER.file().getLast();
         Optional<Account> var3 = MANAGER.getAccount(var2);
         Account var1;
         this.account2 = var1 = var3.orElseGet(() -> MANAGER.stream().findFirst().orElse(null));
         if (var1 != null && !var2.isEmpty()) {
            helper15(var1.name());
         }
      }
   }

   private List<Account> helper13() {
      String var1 = this.accountGuiScreen2.helper8().trim().toLowerCase(Locale.ROOT);
      Comparator<Account> var2 = Comparator.<Account, Boolean>comparing(account -> !account.favorite())
         .thenComparing(a -> a.creationDate(), Comparator.reverseOrder());
      return MANAGER.stream().filter(account -> var1.isEmpty() || account.name().toLowerCase(Locale.ROOT).contains(var1)).sorted(var2).toList();
   }

   private boolean helper14(Account account) {
      return mc.getSession() != null && mc.getSession().getUsername().equalsIgnoreCase(account.name());
   }

   private static void helper15(String name) {
      ((IMinecraftClientAccessor)mc).setSession(helper16(name));
   }

   private static Session helper16(String name) {
      try {
         Constructor var1 = Session.class.getDeclaredConstructor(String.class, UUID.class, String.class, Optional.class, Optional.class, AccountType.class);
         var1.setAccessible(true);
         return (Session)var1.newInstance(
            name,
            UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()),
            mc.getSession() == null ? "" : mc.getSession().getAccessToken(),
            Optional.empty(),
            Optional.empty(),
            AccountType.MOJANG
         );
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   private static void helper17() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null) {
         ClientSoundPlayer.playGuiClick();
      }
   }

   private static int helper18() {
      try {
         return ColorUtils.getThemeColor();
      } catch (Exception var1) {
         return -9073971;
      }
   }

   private static int helper19(int index) {
      try {
         return ColorUtils.getThemeColor(index);
      } catch (Exception var2) {
         return helper18();
      }
   }

   private static int helper20(int color, int alpha) {
      return ColorUtils.setAlphaColor(color, MathHelper.clamp(alpha, 1, 255));
   }

   private static Font helper21(String name, int size) {
      return Fonts.getFont(name, size);
   }

   private static void helper22(Font font, MatrixStack matrices, String text, float x2, float y2, int color) {
      if (font != null) {
         font.draw(matrices, text, x2, y2, color);
      }
   }

   private static void helper23(Font font, MatrixStack matrices, String text, float x2, float y2, int color) {
      if (font != null) {
         font.drawCenteredString(matrices, text, x2, y2, color);
      }
   }

   private static float helper24(Font font, String text) {
      return font == null ? 0.0F : font.getWidth(text);
   }

   private static float helper25(Font font) {
      return font == null ? 0.0F : font.getHeight();
   }

   private void helper26(Font font, MatrixStack matrices, String text, float x2, float y2, float maxWidth, int color) {
      if (font != null && text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         float var8 = font.getWidth(text);
         if (var8 <= maxWidth) {
            font.draw(matrices, text, x2, y2, color);
         } else {
            float var9 = var8 - maxWidth;
            float var10 = this.strings.getOrDefault(text, 0.0F);
            if ((var10 = var10 + 0.003F) > 1.0F) {
               var10--;
            }

            this.strings.put(text, var10);
            float var11 = var10 < 0.5F ? var10 * 2.0F : 2.0F - var10 * 2.0F;
            float var12 = var11 * var11 * (3.0F - 2.0F * var11);
            float var13 = var9 * var12;
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x2, y2 - 2.0F, maxWidth, font.getHeight() + 4.0F);
            font.draw(matrices, text, x2 - var13, y2, color);
            ScissorUtils.pop();
         }
      }
   }

   private static final class TextField {
      private final String VOLUME;
      private final String VOLUME2;
      private final String VOLUME3;
      private String VOLUME4 = "";
      private boolean strings;
      private int screen2;
      private float accountGuiScreen;
      private float accountGuiScreen2;
      private float account2;
      private float volume;
      private float volume2;

      private TextField(String placeholder, String iconFont, String icon) {
         this.VOLUME = placeholder;
         this.VOLUME2 = iconFont;
         this.VOLUME3 = icon;
      }

      private void helper(float x2, float y2, float width, float height) {
         this.accountGuiScreen2 = x2;
         this.account2 = y2;
         this.volume = width;
         this.volume2 = height;
      }

      private void helper2(MatrixStack matrices) {
         Font var3 = Fonts.getFont("suisse", 16);
         Font var4 = Fonts.getFont(this.VOLUME2, 16);
         float var5 = this.accountGuiScreen2 + 5.0F;
         if (var4 != null) {
            AccountGuiScreen.helper22(
               var4,
               matrices,
               this.VOLUME3,
               var5,
               this.account2 + this.volume2 / 2.0F - var4.getHeight() / 2.0F + 5.0F,
               -1
            );
            var5 += var4.getWidth(this.VOLUME3) + 8.0F;
         }

         boolean var6 = this.VOLUME4.isEmpty() && !this.strings;
         String var2 = var6 ? this.VOLUME : this.VOLUME4;
         if (!var2.isEmpty()) {
            int var8 = var6 ? AccountGuiScreen.helper20(-1, 125) : -1;
            AccountGuiScreen.helper22(
               var3, matrices, var2, var5, this.account2 + this.volume2 / 2.0F - AccountGuiScreen.helper25(var3) / 2.0F + 5.0F, var8
            );
         }

         if (this.strings && var3 != null) {
            String var13 = this.VOLUME4.substring(0, MathHelper.clamp(this.screen2, 0, this.VOLUME4.length()));
            float var9 = var5 + AccountGuiScreen.helper24(var3, var13) + 1.0F;
            if (this.accountGuiScreen == 0.0F) {
               this.accountGuiScreen = var9;
            }

            this.accountGuiScreen = MathHelper.lerp(0.35F, this.accountGuiScreen, var9);
            float var10 = (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1.0) * 0.5);
            float var11 = (this.volume2 - 14.0F) * (0.88F + var10 * 0.12F);
            float var12 = this.account2 + (this.volume2 - var11) / 2.0F;
            RenderUtils.drawRoundedRect(
               matrices,
               this.accountGuiScreen,
               var12,
               0.8F,
               var11,
               0.0F,
               AccountGuiScreen.helper20(AccountGuiScreen.helper18(), (int)(95.0F + 160.0F * var10))
            );
         }
      }

      private void helper3(double mouseX, double mouseY, int button) {
         if (button == 0) {
            this.strings = HoveringUtils.isHovered(
               mouseX, mouseY, this.accountGuiScreen2 - 5.0F, this.account2, this.volume + 10.0F, this.volume2
            );
            if (this.strings) {
               this.helper10();
            }
         }
      }

      private boolean helper4(int keyCode) {
         if (!this.strings) {
            return false;
         }

         boolean var2 = InputUtil.isKeyPressed(QClient.mc.getWindow().getHandle(), 341) || InputUtil.isKeyPressed(QClient.mc.getWindow().getHandle(), 345);
         if (var2 && keyCode == 86) {
            this.helper6(QClient.mc.keyboard.getClipboard());
         } else if (keyCode == 259) {
            if (this.screen2 > 0) {
               this.VOLUME4 = this.VOLUME4.substring(0, this.screen2 - 1) + this.VOLUME4.substring(this.screen2);
               this.screen2--;
            }
         } else if (keyCode == 261) {
            if (this.screen2 < this.VOLUME4.length()) {
               this.VOLUME4 = this.VOLUME4.substring(0, this.screen2) + this.VOLUME4.substring(this.screen2 + 1);
            }
         } else if (keyCode == 263) {
            this.screen2 = Math.max(0, this.screen2 - 1);
         } else if (keyCode == 262) {
            this.screen2 = Math.min(this.VOLUME4.length(), this.screen2 + 1);
         } else if (keyCode == 268) {
            this.screen2 = 0;
         } else if (keyCode == 269) {
            this.helper10();
         } else if (keyCode == 256) {
            this.strings = false;
         }

         return true;
      }

      private boolean helper5(char chr) {
         if (this.strings && this.VOLUME4.length() < 16 && helper7(chr)) {
            this.helper6(String.valueOf(chr));
            return true;
         } else {
            return false;
         }
      }

      private void helper6(String value) {
         if (value != null && !value.isEmpty()) {
            String var2 = value.chars()
               .filter(code -> helper7((char)code))
               .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
               .toString();
            if (!var2.isEmpty()) {
               int var3 = Math.max(0, 16 - this.VOLUME4.length());
               if (var2.length() > var3) {
                  var2 = var2.substring(0, var3);
               }

               this.VOLUME4 = this.VOLUME4.substring(0, this.screen2) + var2 + this.VOLUME4.substring(this.screen2);
               this.screen2 = this.screen2 + var2.length();
            }
         }
      }

      private static boolean helper7(char chr) {
         return chr < 128 && (Character.isLetterOrDigit(chr) || chr == '_' || chr == '-');
      }

      private String helper8() {
         return this.VOLUME4;
      }

      private void helper9(boolean selected) {
         this.strings = selected;
      }

      private void helper10() {
         this.screen2 = this.VOLUME4.length();
         this.accountGuiScreen = 0.0F;
      }
   }
}
