package dlc.lumen.client.ui.modern;

import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.client.ClientSoundPlayer;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.social.GlobalProfileAvatar;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector4f;

public class ModernGui extends Screen implements QClient {
   public static final float W = 434.0F;
   public static final float H = 318.0F;
   public static final float SIDE_W = 116.0F;
   public static final float HEADER_H = 32.0F;
    public static final float PAD = 9.0F;
    private static final Identifier TEXTURE_ID = Identifier.of("lumen", "textures/waterlogo/lumenik.png");
    private static boolean flag;
   private static Module.ModuleCategory moduleCategory = Module.ModuleCategory.COMBAT;
   private static ModernGui.Page page2 = ModernGui.Page.MODULES;
   private static String text2 = "";
   public MatrixStack m;
   public DrawContext ctx;
   public float alpha = 1.0F;
   public int mouseX;
   public int mouseY;
   public float x;
   public float y;
   private float volume;
   private boolean flag2;
   private boolean flag3;
   private float volume2 = 1.0F;
   private float volume3;
   private float volume4;
   private final List<ModernGui.Hit> modernGuis = new ArrayList<>();
   private final List<float[]> floats = new ArrayList<>();
   ModernGui.DragHandler dragging;
   public Object focus;
   public String buffer = "";
   public Module bindingModule;
   public BindSetting bindingBind;
   public BooleanSetting bindingBoolean;
   private static final Map<Object, float[]> OBJECTS = new HashMap<>();
   private float volume5;
   private float volume6;
   private static final String TEXT2 = "йцукенгшщзхъфывапролджэячсмитьбю";
   private static final String TEXT3 = "qwertyuiop[]asdfghjkl;'zxcvbnm,.";
   public static final Object FOCUS_SEARCH = "search";
   public static final float TOGGLE_W = 20.0F;
   public static final float TOGGLE_H = 10.0F;
   public static final float KNOB = 8.0F;
   public static final float CHECK = 12.0F;
   public static final float PILL_H = 11.0F;

   public ModernGui() {
      super(Text.of("lumen"));
      ModernAnim.resetPrefix("appear:");
      ClientSoundPlayer.playGuiOpen();
   }

   public static List<Module.ModuleCategory> categories() {
      List<Module.ModuleCategory> var0 = List.of(
         Module.ModuleCategory.COMBAT, Module.ModuleCategory.MOVEMENT, Module.ModuleCategory.PLAYER, Module.ModuleCategory.RENDER, Module.ModuleCategory.MISC
      );
      ArrayList<Module.ModuleCategory> var1 = new ArrayList<>();

      for (Module.ModuleCategory var3 : var0) {
         if (!modules(var3).isEmpty()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public static List<Module> modules(Module.ModuleCategory category) {
      ArrayList<Module> var1 = new ArrayList<>();
      ObjectListIterator var2 = ModuleClass.INSTANCE.getObject().iterator();

      while (var2.hasNext()) {
         Module var3 = (Module)var2.next();
         if (var3.getCategory() == category && !"AutoForest".equals(var3.getName())) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public List<Module> visibleModules() {
      if (text2.isBlank()) {
         return modules(moduleCategory);
      }

      String var1 = text2.toLowerCase(Locale.ROOT);
      ArrayList<Module> var2 = new ArrayList<>();
      ObjectListIterator var3 = ModuleClass.INSTANCE.getObject().iterator();

      while (var3.hasNext()) {
         Module var4 = (Module)var3.next();
         if (!"AutoForest".equals(var4.getName()) && (helper(var4, var1) || helper(var4, toEnglish(var1)))) {
            var2.add(var4);
         }
      }

      return var2;
   }

   private static boolean helper(Module module, String query) {
      String var2 = module.getName() == null ? "" : module.getName().toLowerCase(Locale.ROOT);
      String var3 = module.getDisplayName() == null ? "" : module.getDisplayName().toLowerCase(Locale.ROOT);
      String var4 = module.getDisplayDescription() == null ? "" : module.getDisplayDescription().toLowerCase(Locale.ROOT);
      return var2.contains(query) || var3.contains(query) || var4.contains(query);
   }

   public static String toEnglish(String text) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : text.toCharArray()) {
         int var6 = "йцукенгшщзхъфывапролджэячсмитьбю".indexOf(Character.toLowerCase(var5));
         var1.append(var6 >= 0 ? "qwertyuiop[]asdfghjkl;'zxcvbnm,.".charAt(var6) : var5);
      }

      return var1.toString();
   }

   public static ModernGui.Page page() {
      return page2;
   }

   public static void setPage(ModernGui.Page value) {
      page2 = value;
   }

   public static Module.ModuleCategory category() {
      return moduleCategory;
   }

   public static String search() {
      return text2;
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      Window var5 = mc == null ? null : mc.getWindow();
      if (var5 != null) {
         ModernAnim.beginFrame();
         this.volume = ModernAnim.approach(this.volume, this.flag2 ? 0.0F : 1.0F, this.flag2 ? 13.0F : 11.0F);
         if (this.flag2 && this.volume < 0.01F) {
            mc.setScreen(null);
         } else {
            float var6 = ModernAnim.ease(this.volume);
            this.alpha = MathHelper.clamp(var6, 0.0F, 1.0F);
            float var7 = var5.getScaledWidth();
            float var8 = var5.getScaledHeight();
            this.volume3 = var7 / 2.0F;
            this.volume4 = var8 / 2.0F;
            float var9 = Math.min((var7 - 12.0F) / 434.0F, (var8 - 12.0F) / 318.0F);
            this.volume2 = MathHelper.clamp(var9, 0.3F, 1.0F);
            float var10 = this.volume2 * (0.965F + 0.035F * var6);
            this.x = this.volume3 - 217.0F;
            this.y = this.volume4 - 159.0F + (1.0F - var6) * 10.0F;
            this.mouseX = Math.round(this.volume3 + (mouseX - this.volume3) / var10);
            this.mouseY = Math.round(this.volume4 + (mouseY - this.volume4) / var10);
            this.ctx = context;
            this.m = context.getMatrices();
            this.modernGuis.clear();
            this.floats.clear();
            RenderUtils.drawRect(this.m, 0.0F, 0.0F, var7, var8, ColorUtils.applyAlpha(ColorUtils.rgba(10, 7, 14, 120), this.alpha));
            this.m.push();
            this.m.translate(this.volume3, this.volume4, 0.0F);
            this.m.scale(var10, var10, 1.0F);
            this.m.translate(-this.volume3, -this.volume4, 0.0F);
            ScissorUtils.setViewTransform(var10, this.volume3, this.volume4);
            this.helper2();
            ScissorUtils.resetViewTransform();
            this.m.pop();
         }
      }
   }

   private void helper2() {
      this.darkBlur(this.x, this.y, 434.0F, 318.0F, ModernTheme.R_PANEL());
      this.darkBlur(this.x, this.y, 434.0F, 318.0F, ModernTheme.R_PANEL());
      this.rect(this.x, this.y, 434.0F, 318.0F, ModernTheme.R_PANEL(), ColorUtils.applyAlpha(ModernTheme.BG(), ModernTheme.windowOpacity()));
      this.glass(this.x - 1.0F, this.y - 1.0F, 436.0F, 320.0F, ModernTheme.R_PANEL(), 0.78F, 0.06F, 0.0F);
      this.outline(this.x, this.y, 434.0F, 318.0F, ModernTheme.R_PANEL(), 0.5F, ModernTheme.BG_BORDER());
      this.helper3();
      this.helper6();
      float var1 = this.x + 116.0F;
      float var2 = this.y + 32.0F;
      float var3 = 318.0F;
      float var4 = 286.0F;
      this.pushClip(var1, var2, var3, var4);
      switch (page2) {
         case MODULES:
            ModernModulesPage.render(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case PROFILE:
            ModernPages.profile(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case CONFIGS:
            ModernPages.configs(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case FRIENDS:
            ModernPages.friends(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case THEMES:
            ModernPages.themes(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case MARKET:
            ModernPages.market(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case AUTOSET:
            ModernPages.autoset(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
            break;
         case BOTS:
            ModernBotsPage.render(this, var1 + 9.0F, var2 + 9.0F, var3 - 18.0F, var4 - 18.0F);
      }

      this.popClip();
      this.helper10(var1 + var3 - 4.5F, var2 + 9.0F, var4 - 18.0F);
   }

   private void helper3() {
      float var1 = ModernTheme.R_PANEL();
      RenderUtils.drawRoundedRect(
         this.m,
         this.x,
         this.y,
         116.0F,
         318.0F,
         new Vector4f(var1, var1, 0.0F, 0.0F),
         this.a(ColorUtils.applyAlpha(ModernTheme.SIDEBAR(), ModernTheme.windowOpacity() * 0.55F))
      );
      this.rect(this.x + 116.0F - 0.5F, this.y + 6.0F, 0.5F, 306.0F, 0.0F, ModernTheme.LINE());
      float var2 = this.x + 8.0F;
      float var3 = 100.0F;
      float var4 = 30.0F;
      float var5 = var4 * 1.02F;
      float var6 = var2 + (var3 - var5) / 2.0F;
      float var7 = this.y + 31.0F;
      this.helper7(var6, var7, var4, this.a(ModernTheme.accent()));
      float var8 = this.y + 56.0F;
      boolean var9 = this.focus == FOCUS_SEARCH;
      this.rect(var2, var8, var3, 19.0F, 6.5F, ModernTheme.FIELD());
      if (var9) {
         this.outline(var2, var8, var3, 19.0F, 6.5F, 0.5F, ModernTheme.accent());
      }

      this.icon("icon1", 14, "l", var2 + 10.0F, var8 + 9.5F, var9 ? ModernTheme.accent() : ModernTheme.TEXT_MUTED());
      String var10 = var9 ? this.buffer : text2;
      Font var11 = this.font(11);
      if (var10.isEmpty()) {
         this.text(var11, "Search", var2 + 16.0F, this.textY(var8, 19.0F, var11), ModernTheme.TEXT_FAINT());
      } else {
         this.text(var11, this.clip(var11, var10, var3 - 24.0F), var2 + 16.0F, this.textY(var8, 19.0F, var11), ModernTheme.TEXT_SOFT());
         if (var9 && this.caretVisible()) {
            float var12 = var2 + 16.0F + var11.getWidth(this.clip(var11, var10, var3 - 24.0F));
            this.rect(var12 + 0.8F, var8 + 5.0F, 0.7F, 9.0F, 0.0F, ModernTheme.accent());
         }
      }

      this.hit(var2, var8, var3, 19.0F, button -> {
         this.focus = FOCUS_SEARCH;
         this.buffer = text2;
         return true;
      });
      float var26 = this.y + 81.0F;

      for (Module.ModuleCategory var15 : categories()) {
         boolean var16 = page2 == ModernGui.Page.MODULES && var15 == moduleCategory && text2.isBlank();
         this.helper5(var2, var26, var3, var15.getName(), "icon", var15.getIcons(), String.valueOf(modules(var15).size()), var16, button -> {
            moduleCategory = var15;
            page2 = ModernGui.Page.MODULES;
            text2 = "";
            if (this.focus == FOCUS_SEARCH) {
               this.focus = null;
            }

            ModernAnim.resetPrefix("appear:");
            this.helper9();
            return true;
         });
         var26 += 16.5F;
      }

      this.rect(var2 + 4.0F, var26 + 3.0F, var3 - 8.0F, 0.5F, 0.0F, ModernTheme.LINE_SOFT());
      var26 += 9.0F;
      float var28 = 18.0F;
      float var29 = this.y + 318.0F - 12.0F - var28;
      List<NavEntry> var30 = List.of(
         new NavEntry("Profile", "icon1", "a", null, ModernGui.Page.PROFILE),
         new NavEntry("Configs", "iconnew", "h", null, ModernGui.Page.CONFIGS),
         new NavEntry("Friends", "icon", "y", null, ModernGui.Page.FRIENDS),
         new NavEntry("Themes", "icon", "d", null, ModernGui.Page.THEMES),
         new NavEntry("Auto Market", "icon", "u", null, ModernGui.Page.MARKET),
         new NavEntry("AutoSet", "icon1", "v", null, ModernGui.Page.AUTOSET),
         new NavEntry("Боты", "icon1", "a", String.valueOf(BotManager.INSTANCE.bots().size()), ModernGui.Page.BOTS)
      );
      float var17 = var29 - 11.0F;
      float var18 = Math.min(16.5F, (var17 - var26) / var30.size());

      for (NavEntry var20 : var30) {
         ModernGui.Page var21 = var20.target();
         this.helper5(
            var2, var26, var3, var20.label(), var20.iconFont(), var20.glyph(), var20.badge(), page2 == var21, button -> this.helper4(var21)
         );
         var26 += var18;
      }

      float var31 = var29 + var28 / 2.0F;
      this.rect(var2 + 4.0F, var29 - 9.0F, var3 - 8.0F, 0.5F, 0.0F, ModernTheme.LINE_SOFT());
      this.rect(var2, var31 - 9.0F, 18.0F, 18.0F, 9.0F, ModernTheme.PILL());
      if (!GlobalProfileAvatar.drawRounded(this.ctx, var2, var31 - 9.0F, 18.0F, 18.0F, 9.0F, this.a(-1))) {
         this.userIcon(var2 + 9.0F, var31, 4.6F, ModernTheme.ICON_IDLE());
      }

      Font var32 = this.font(11);
      Font var33 = this.font(9);
      float var22 = 2.0F;
      float var23 = var32.getSize() * 0.35F + var22 + var33.getSize() * 0.35F;
      float var24 = var31 - var23 / 2.0F;
      String var25 = this.helper15();
      this.text(var32, this.clip(var32, var25, var3 - 28.0F), var2 + 24.0F, var24, ModernTheme.TEXT_DIM());
      this.text(var33, "t.me/lumen", var2 + 24.0F, var24 + var32.getSize() * 0.35F + var22, ModernTheme.TEXT_MUTED());
   }

   private boolean helper4(ModernGui.Page target) {
      page2 = target;
      text2 = "";
      if (this.focus == FOCUS_SEARCH) {
         this.focus = null;
      }

      this.helper9();
      return true;
   }

   private void helper5(
      float itemX, float itemY, float width, String label, String iconFont, String glyph, String badge, boolean active, ModernGui.Click action
   ) {
      float var10 = 17.0F;
      boolean var11 = this.hovered(itemX, itemY, width, var10);
      float var12 = ModernAnim.value("nav:" + label, active ? 1.0F : (var11 ? 0.55F : 0.0F), 14.0F);
      if (var12 > 0.01F) {
         this.rect(itemX, itemY, width, var10, ModernTheme.R_ROW(), ColorUtils.interpolate(ModernTheme.NAV_HOVER(), ModernTheme.NAV_ACTIVE(), var12), var12);
      }

      float var13 = ModernAnim.value("navbar:" + label, active ? 1.0F : 0.0F, 16.0F);
      if (var13 > 0.01F) {
         float var14 = 9.5F * var13;
         this.rect(itemX - 6.0F, itemY + var10 / 2.0F - var14 / 2.0F, 2.0F, var14, 1.0F, ColorUtils.applyAlpha(ModernTheme.accent(), var13));
      }

      int var18 = active ? ModernTheme.accent() : ColorUtils.interpolate(ModernTheme.ICON_IDLE(), ModernTheme.TEXT_DIM(), var12);
      this.icon(iconFont, 15, glyph, itemX + 10.0F, itemY + var10 / 2.0F, var18);
      Font var15 = this.font(11);
      int var16 = active ? ModernTheme.TEXT() : ColorUtils.interpolate(ColorUtils.rgba(130, 138, 154, 255), ModernTheme.TEXT_DIM(), var12);
      float var17 = badge == null ? 0.0F : this.font(9).getWidth(badge) + 6.0F;
      this.text(var15, this.clip(var15, label, width - 22.0F - var17), itemX + 18.0F, this.textY(itemY, var10, var15), var16);
      if (badge != null) {
         this.textRight(this.font(9), badge, itemX + width - 5.0F, this.textY(itemY, var10, this.font(9)), ModernTheme.TEXT_FAINT());
      }

      this.hit(itemX, itemY, width, var10, action);
   }

   private void helper6() {
      float var1 = this.x + 116.0F + 9.0F;
      this.rect(this.x + 116.0F, this.y + 32.0F - 0.5F, 314.0F, 0.5F, 0.0F, ModernTheme.LINE());
      String var2;
      String var3;
      if (page2 == ModernGui.Page.MODULES) {
         List<Module> var4 = this.visibleModules();
         int var5 = 0;

         for (Module var7 : var4) {
            if (var7.isEnable()) {
               var5++;
            }
         }

         var2 = text2.isBlank() ? moduleCategory.getName() : "Поиск";
         var3 = var4.size() + " модулей · " + var5 + " активно";
      } else if (page2 == ModernGui.Page.PROFILE) {
         var2 = "Profile";
         var3 = "внешний вид персонажа";
      } else if (page2 == ModernGui.Page.CONFIGS) {
         var2 = "Configs";
         var3 = Lumen.INSTANCE.configStorage.getAvailableConfigs().size() + " конфигов";
      } else if (page2 == ModernGui.Page.FRIENDS) {
         var2 = "Friends";
         var3 = Lumen.INSTANCE.friendStorage.getFriends().size() + " в списке";
      } else if (page2 == ModernGui.Page.THEMES) {
         var2 = "Themes";
         var3 = "оформление клиента";
      } else if (page2 == ModernGui.Page.AUTOSET) {
         var2 = "AutoSet";
         var3 = ModernPages.autosetSubtitle();
      } else if (page2 == ModernGui.Page.BOTS) {
         var2 = "Боты";
         var3 = ModernBotsPage.subtitle();
      } else {
         var2 = "Auto Market";
         var3 = ModernPages.marketSubtitle();
      }

      Font var8 = this.font(14);
      this.text(var8, var2, var1, this.textY(this.y + 9.0F, 15.0F, var8), ModernTheme.TEXT());
      float var9 = var1 + var8.getWidth(var2) + 7.0F;
      this.chevron(var9 + 2.0F, this.y + 16.5F, 2.8F, -90.0F, ModernTheme.TEXT_FAINT());
      this.text(this.font(10), var3, var9 + 8.0F, this.textY(this.y + 9.0F, 15.0F, this.font(10)), ModernTheme.TEXT_MUTED());
   }

   public Font font(int size) {
      return Fonts.getFont("suisse", Math.round(size * 1.3F));
   }

   public int a(int color) {
      return ColorUtils.applyAlpha(color, this.alpha);
   }

   public MinecraftClient client() {
      return mc;
   }

   public void rect(float rx, float ry, float rw, float rh, float radius, int color) {
      RenderUtils.drawRoundedRect(this.m, rx, ry, rw, rh, radius, this.a(color));
   }

   public void rect(float rx, float ry, float rw, float rh, float topLeft, float topRight, float bottomRight, float bottomLeft, int color) {
      RenderUtils.drawRoundedRect(this.m, rx, ry, rw, rh, topLeft, topRight, bottomRight, bottomLeft, this.a(color));
   }

   public void rect(float rx, float ry, float rw, float rh, float radius, int color, float extraAlpha) {
      RenderUtils.drawRoundedRect(this.m, rx, ry, rw, rh, radius, this.a(ColorUtils.applyAlpha(color, extraAlpha)));
   }

   public void outline(float rx, float ry, float rw, float rh, float radius, float thickness, int color) {
      int var8 = this.a(color);
      RenderUtils.drawRoundedRectOutline(this.m, rx, ry, rw, rh, radius, thickness, var8, var8, var8, var8);
   }

   public void darkBlur(float bx, float by, float bw, float bh, float radius) {
      RenderUtils.drawBlur(this.m, bx, by, bw, bh, radius, ModernTheme.blur(), this.a(ModernTheme.DARK_BLUR_TINT()));
   }

   public void lightBlur(float bx, float by, float bw, float bh, float radius) {
      RenderUtils.drawBlur(this.m, bx, by, bw, bh, radius, ModernTheme.blur(), this.a(ModernTheme.LIGHT_BLUR_TINT()));
   }

   public void glass(float gx, float gy, float gw, float gh, float radius, float baseAlpha, float distortStrength, float shineStrength) {
      if (ClickGuiTheme.glass()) {
         RenderUtils.drawLiquidGlass(
            this.m,
            gx,
            gy,
            gw,
            gh,
            radius,
            radius,
            radius,
            radius,
            ColorUtils.rgba(255, 255, 255, 255),
            this.alpha,
            24.0F,
            ColorUtils.rgba(255, 255, 255, 255),
            baseAlpha,
            true,
            0.0F,
            distortStrength,
            2.0F,
            false,
            shineStrength
         );
      }
   }

   public void shadow(float sx, float sy, float sw, float sh, float radius, float softness, int color) {
      if (ModernTheme.shadow() && !(softness <= 0.0F)) {
         int var8 = this.a(color);
      }
   }

   public void surface(float sx, float sy, float sw, float sh, float radius, int background, int border, float shadowSoftness) {
      this.shadow(sx, sy, sw, sh, radius, shadowSoftness, ColorUtils.rgba(0, 0, 0, 160));
      this.lightBlur(sx, sy, sw, sh, radius);
      this.rect(sx, sy, sw, sh, radius, ColorUtils.applyAlpha(background, ModernTheme.surfaceAlpha()));
   }

   public void text(Font f, String s, float tx, float ty, int color) {
      f.draw(this.m, s, tx, ty, this.a(color));
   }

   public void textRight(Font f, String s, float rightX, float ty, int color) {
      f.draw(this.m, s, rightX - f.getWidth(s), ty, this.a(color));
   }

   public void textCenter(Font f, String s, float centerX, float ty, int color) {
      f.draw(this.m, s, centerX - f.getWidth(s) / 2.0F, ty, this.a(color));
   }

   public float textY(float top, float height, Font f) {
      return top + (height - f.getSize() * 0.35F) / 2.0F + 1.0F;
   }

   public String clip(Font f, String s, float maxWidth) {
      if (s == null) {
         return "";
      }

      if (f.getWidth(s) <= maxWidth) {
         return s;
      }

      StringBuilder var4 = new StringBuilder();

      for (char var8 : s.toCharArray()) {
         if (f.getWidth(var4.toString() + var8 + "..") > maxWidth) {
            break;
         }

         var4.append(var8);
      }

      return var4 + "..";
   }

   public void icon(String fontName, int size, String glyph, float centerX, float centerY, int color) {
      Font var7 = Fonts.getFont(fontName, size);
      var7.draw(this.m, glyph, centerX - var7.getWidth(glyph) / 2.0F, centerY - size * 0.35F / 2.0F + 1.0F, this.a(color));
   }

   public void chevron(float centerX, float centerY, float size, float rotation, int color) {
      this.m.push();
      this.m.translate(centerX, centerY, 0.0F);
      this.m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
      int var6 = this.a(color);
      RenderUtils.drawSmoothLine(this.m, -size, -size * 0.5F, 0.0F, size * 0.5F, 1.0F, var6);
      RenderUtils.drawSmoothLine(this.m, 0.0F, size * 0.5F, size, -size * 0.5F, 1.0F, var6);
      this.m.pop();
   }

    private void helper7(float x, float centerY, float h, int color) {
       RenderUtils.drawImage(this.m, TEXTURE_ID, x, centerY - h / 2.0F, h, h, color);
    }

    public void toggle(float tx, float ty, boolean on, Object key) {
      float var5 = ModernAnim.value("tgl:" + key, on ? 1.0F : 0.0F, 16.0F);
      this.rect(tx, ty, 20.0F, 10.0F, 4.0F, ColorUtils.interpolate(ModernTheme.TOGGLE_OFF(), ModernTheme.accent(), var5));
      float var6 = tx + 1.0F + 10.0F * var5;
      this.rect(var6, ty + 1.0F, 8.0F, 8.0F, 4.0F, ColorUtils.interpolate(ModernTheme.KNOB_OFF(), ModernTheme.BG(), var5));
   }

   public void checkbox(float bx, float by, boolean on, Object key) {
      float var5 = ModernAnim.value("chk:" + key, on ? 1.0F : 0.0F, 16.0F);
      this.rect(bx, by, 12.0F, 12.0F, ModernTheme.radius(4.0F), ColorUtils.interpolate(ModernTheme.TRACK(), ModernTheme.accent(), var5));
      if (var5 > 0.05F) {
         this.checkMark(bx + 6.0F, by + 6.0F, 3.1F, ColorUtils.applyAlpha(ModernTheme.BG(), Math.min(1.0F, var5 * 2.0F)), var5);
      }
   }

   public void checkMark(float centerX, float centerY, float size, int color) {
      this.checkMark(centerX, centerY, size, color, 1.0F);
   }

   public void checkMark(float centerX, float centerY, float size, int color, float progress) {
      RenderUtils.drawCheckMark(this.m, centerX, centerY, size, Math.max(1.1F, size * 0.42F), progress, this.a(color));
   }

   public void trashIcon(float centerX, float centerY, float size, int color) {
      float var5 = size * 0.9F;
      Font var6 = this.font(10);
      this.text(var6, "X", centerX - 1.0F - var5 / 2.0F, centerY - size * 0.72F + 1.0F, ModernTheme.accent());
   }

   public void saveIcon(float centerX, float centerY, float size, int color) {
      this.rect(centerX - size, centerY - size, size * 2.0F, size * 2.0F, size * 0.35F, color);
      int var5 = ColorUtils.applyAlpha(ModernTheme.BG(), 0.85F);
      this.rect(centerX - size * 0.5F, centerY - size * 0.85F, size, size * 0.6F, size * 0.15F, var5);
      this.rect(centerX - size * 0.6F, centerY + size * 0.15F, size * 1.2F, size * 0.7F, size * 0.15F, var5);
   }

   public void userIcon(float centerX, float centerY, float size, int color) {
      float var5 = size * 0.68F;
      this.rect(centerX - var5 / 2.0F, centerY - size * 0.78F, var5, var5, var5 / 2.0F, color);
      float var6 = size * 1.18F;
      float var7 = size * 0.62F;
      this.rect(centerX - var6 / 2.0F, centerY + size * 0.1F, var6, var7, var7 * 0.75F, var7 * 0.75F, 0.0F, 0.0F, color);
   }

   public void playIcon(float centerX, float centerY, float size, int color) {
      RenderUtils.drawTriangle(this.m, centerX - size * 0.6F, centerY - size, centerX - size * 0.6F, centerY + size, centerX + size, centerY, this.a(color));
   }

   public float valuePill(float rightX, float py, String value, int color) {
      Font var5 = this.font(10);
      float var6 = var5.getWidth(value) + 11.0F;
      this.rect(rightX - var6, py, var6, 11.0F, ModernTheme.R_SMALL(), ModernTheme.PILL());
      this.textCenter(var5, value, rightX - var6 / 2.0F, this.textY(py, 11.0F, var5), color);
      return var6;
   }

   public void slider(float sx, float sy, float sw, float progress, Object key, DoubleConsumer setter) {
      float var7 = MathHelper.clamp(ModernAnim.value("sld:" + key, progress, 22.0F), 0.0F, 1.0F);
      this.rect(sx, sy, sw, 3.0F, 1.5F, ModernTheme.TRACK());
      if (var7 > 0.001F) {
         this.rect(sx, sy, Math.max(3.0F, sw * var7), 3.0F, 1.5F, ModernTheme.accent());
      }

      float var8 = sx + sw * var7;
      this.rect(var8 - 3.2F, sy - 1.7F, 6.4F, 7.0F, 3.2F, ModernTheme.TEXT());
      this.hit(sx - 3.0F, sy - 6.0F, sw + 6.0F, 15.0F, button -> {
         ModernGui.DragHandler var5 = (dragX, dragY) -> setter.accept(MathHelper.clamp((dragX - sx) / sw, 0.0, 1.0));
         var5.drag(this.mouseX, this.mouseY);
         this.dragging = var5;
         return true;
      });
   }

   public void iconButton(float bx, float by, String kind, String fontName, String glyph, int color, String tooltip, ModernGui.Click action) {
      float var9 = 19.0F;
      boolean var10 = this.hovered(bx, by, var9, var9);
      float var11 = ModernAnim.value("ib:" + kind + tooltip, var10 ? 1.0F : 0.0F, 16.0F);
      this.rect(bx, by, var9, var9, ModernTheme.radius(6.0F), ColorUtils.interpolate(ModernTheme.FIELD(), ModernTheme.PILL_HOVER(), var11));
      float var12 = bx + var9 / 2.0F;
      float var13 = by + var9 / 2.0F;
      switch (kind) {
         case "play":
            RenderUtils.drawTriangle(this.m, var12 - 2.6F, var13 - 3.8F, var12 - 2.6F, var13 + 3.8F, var12 + 3.8F, var13, this.a(color));
            break;
         case "pause":
            this.rect(var12 - 3.4F, var13 - 3.6F, 2.4F, 7.2F, 1.0F, color);
            this.rect(var12 + 1.0F, var13 - 3.6F, 2.4F, 7.2F, 1.0F, color);
            break;
         case "fold":
            this.chevron(var12, var13 - 2.6F, 2.7F, 180.0F, color);
            this.chevron(var12, var13 + 2.6F, 2.7F, 0.0F, color);
            break;
         default:
            this.icon(fontName, 11, glyph, var12, var13, color);
      }

      this.hit(bx, by, var9, var9, action);
   }

   public boolean field(float fx, float fy, float fw, float fh, Object key, String placeholder, String value, String iconGlyph) {
      boolean var9 = this.focus == key;
      this.surface(fx, fy, fw, fh, ModernTheme.R_ROW(), ModernTheme.FIELD(), var9 ? ModernTheme.accent() : ModernTheme.CARD_BORDER(), 2.5F);
      float var10 = fx + 9.0F;
      if (iconGlyph != null) {
         this.icon("icon1", 10, iconGlyph, fx + 9.0F, fy + 1.0F + fh / 2.0F, var9 ? ModernTheme.accent() : ModernTheme.TEXT_MUTED());
         var10 = fx + 17.0F;
      }

      Font var11 = this.font(10);
      String var12 = var9 ? this.buffer : value;
      if (var12 != null && !var12.isEmpty()) {
         String var13 = this.clip(var11, var12, fw - (var10 - fx) - 6.0F);
         this.text(var11, var13, var10, this.textY(fy, fh, var11), ModernTheme.TEXT_SOFT());
         if (var9 && this.caretVisible()) {
            this.rect(var10 + var11.getWidth(var13) + 0.8F, fy + fh / 2.0F - 3.5F, 0.6F, 7.0F, 0.0F, ModernTheme.accent());
         }
      } else {
         this.text(var11, this.clip(var11, placeholder, fw - (var10 - fx) - 6.0F), var10, this.textY(fy, fh, var11), ModernTheme.TEXT_FAINT());
      }

      this.hit(fx, fy, fw, fh, button -> {
         this.focus = key;
         this.buffer = value == null ? "" : value;
         return true;
      });
      return var9;
   }

   public void pill(float px, float py, float pw, float ph, String label, String fontName, String glyph, boolean active, Object key, ModernGui.Click action) {
      boolean var11 = this.hovered(px, py, pw, ph);
      float var12 = ModernAnim.value("pill:" + key, active ? 1.0F : (var11 ? 0.5F : 0.0F), 15.0F);
      this.surface(
         px,
         py,
         pw,
         ph,
         ModernTheme.R_ROW(),
         ColorUtils.interpolate(ModernTheme.CARD(), ModernTheme.NAV_ACTIVE(), var12),
         active ? ModernTheme.accent() : ModernTheme.CARD_BORDER(),
         2.5F
      );
      float var13 = px + 9.0F;
      if (glyph != null) {
         this.icon(fontName, 10, glyph, px + 10.0F, py + 1.0F + ph / 2.0F, active ? ModernTheme.accent() : ModernTheme.ICON_IDLE());
         var13 = px + 18.0F;
      }

      Font var14 = this.font(10);
      this.text(
         var14, this.clip(var14, label, pw - (var13 - px) - 6.0F), var13, this.textY(py, ph, var14), active ? ModernTheme.TEXT() : ModernTheme.TEXT_DIM()
      );
      this.hit(px, py, pw, ph, action);
   }

   public boolean row(float rx, float ry, float rw, float rh, boolean active, Object key) {
      boolean var7 = this.hovered(rx, ry, rw, rh);
      float var8 = ModernAnim.value("row:" + key, var7 ? 1.0F : 0.0F, 15.0F);
      int var9 = active ? ModernTheme.CARD_ON() : ModernTheme.CARD();
      int var10 = active ? ModernTheme.CARD_ON_HOVER() : ModernTheme.CARD_HOVER();
      this.surface(
         rx,
         ry,
         rw,
         rh,
         ModernTheme.R_CARD(),
         ColorUtils.interpolate(var9, var10, var8),
         active ? ModernTheme.CARD_BORDER_ON() : ModernTheme.CARD_BORDER(),
         3.5F
      );
      return var7;
   }

   public void sectionLabel(float lx, float ly, String label) {
      this.text(this.font(9), label.toUpperCase(Locale.ROOT), lx, ly, ModernTheme.TEXT_FAINT());
   }

   public boolean caretVisible() {
      return System.currentTimeMillis() / 500L % 2L == 0L;
   }

   public void hit(float hx, float hy, float hw, float hh, ModernGui.Click action) {
      float[] var6 = this.floats.isEmpty() ? null : this.floats.get(this.floats.size() - 1);
      this.modernGuis.add(new ModernGui.Hit(hx, hy, hw, hh, var6, action));
   }

   public boolean hovered(float hx, float hy, float hw, float hh) {
      if (!HoveringUtils.isHovered(this.mouseX, this.mouseY, hx, hy, hw, hh)) {
         return false;
      }

      float[] var5 = this.floats.isEmpty() ? null : this.floats.get(this.floats.size() - 1);
      return var5 == null || HoveringUtils.isHovered(this.mouseX, this.mouseY, var5[0], var5[1], var5[2], var5[3]);
   }

   public void pushClip(float cx, float cy, float cw, float ch) {
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(cx, cy, cw, ch);
      this.floats.add(new float[]{cx, cy, cw, ch});
   }

   public void popClip() {
      ScissorUtils.pop();
      if (!this.floats.isEmpty()) {
         this.floats.remove(this.floats.size() - 1);
      }
   }

   public float scroll() {
      float[] var1 = OBJECTS.computeIfAbsent(this.helper8(), key -> new float[2]);
      var1[1] = ModernAnim.approach(var1[1], var1[0], 16.0F);
      return var1[1];
   }

   public void setContentHeight(float height, float visible) {
      this.volume5 = height;
      this.volume6 = visible;
      float[] var3 = OBJECTS.computeIfAbsent(this.helper8(), key -> new float[2]);
      var3[0] = MathHelper.clamp(var3[0], 0.0F, Math.max(0.0F, height - visible));
   }

   private Object helper8() {
      return page2 == ModernGui.Page.MODULES ? "modules:" + moduleCategory.name() + ":" + !text2.isBlank() : page2.name();
   }

   private void helper9() {
      float[] var1 = OBJECTS.computeIfAbsent(this.helper8(), key -> new float[2]);
      var1[0] = 0.0F;
   }

   private void helper10(float barX, float barY, float barHeight) {
      float var4 = Math.max(0.0F, this.volume5 - this.volume6);
      float var5 = ModernAnim.value("scrollbar", var4 > 1.0F ? 1.0F : 0.0F, 12.0F);
      if (!(var5 < 0.02F) && !(this.volume5 <= 0.0F)) {
         float var6 = Math.max(14.0F, barHeight * MathHelper.clamp(this.volume6 / this.volume5, 0.05F, 1.0F));
         float[] var7 = OBJECTS.computeIfAbsent(this.helper8(), key -> new float[2]);
         float var8 = var4 <= 0.0F ? 0.0F : MathHelper.clamp(var7[1] / var4, 0.0F, 1.0F);
         float var9 = barY + (barHeight - var6) * var8;
         this.rect(barX, barY, 2.5F, barHeight, 1.25F, ColorUtils.applyAlpha(ModernTheme.TRACK(), var5 * 0.7F));
         this.rect(barX, var9, 2.5F, var6, 1.25F, ColorUtils.applyAlpha(ModernTheme.ICON_IDLE(), var5));
         this.hit(barX - 3.0F, barY, 8.5F, barHeight, button -> {
            ModernGui.DragHandler var7x = (dragX, dragY) -> {
               float var9x = MathHelper.clamp((float)((dragY - barY - var6 / 2.0F) / (barHeight - var6)), 0.0F, 1.0F);
               var7[0] = var9x * var4;
            };
            var7x.drag(this.mouseX, this.mouseY);
            this.dragging = var7x;
            return true;
         });
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.flag2) {
         return true;
      }

      if (this.bindingModule == null && this.bindingBind == null && this.bindingBoolean == null) {
         for (int var6 = this.modernGuis.size() - 1; var6 >= 0; var6--) {
            ModernGui.Hit var7 = this.modernGuis.get(var6);
            if (this.helper11(var7, this.mouseX, this.mouseY) && var7.action() != null && var7.action().click(button)) {
               return true;
            }
         }

         if (this.focus != null) {
            this.helper12();
         }

         return true;
      } else {
         this.helper13(1000 + button);
         return true;
      }
   }

   private boolean helper11(ModernGui.Hit h, float px, float py) {
      if (!HoveringUtils.isHovered(px, py, h.x(), h.y(), h.w(), h.h())) {
         return false;
      }

      float[] var4 = h.clip();
      return var4 == null || HoveringUtils.isHovered(px, py, var4[0], var4[1], var4[2], var4[3]);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.dragging = null;
      return super.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.dragging != null) {
         this.dragging.drag(this.mouseX, this.mouseY);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      float var9 = Math.max(0.0F, this.volume5 - this.volume6);
      float[] var10 = OBJECTS.computeIfAbsent(this.helper8(), key -> new float[2]);
      var10[0] = MathHelper.clamp(var10[0] - (float)verticalAmount * 26.0F, 0.0F, var9);
      return true;
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      if (this.focus != null && !Character.isISOControl(chr)) {
         this.buffer = this.buffer + chr;
         if (this.focus == FOCUS_SEARCH) {
            text2 = this.buffer;
            this.helper9();
         } else {
            ModernPages.live(this.focus, this.buffer);
         }

         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.bindingModule == null && this.bindingBind == null && this.bindingBoolean == null) {
         if (this.focus != null) {
            switch (keyCode) {
               case 256:
                  this.focus = null;
                  this.buffer = "";
                  return true;
               case 257:
               case 335:
                  this.helper12();
                  return true;
               case 259:
                  if (!this.buffer.isEmpty()) {
                     this.buffer = this.buffer.substring(0, this.buffer.length() - 1);
                     if (this.focus == FOCUS_SEARCH) {
                        text2 = this.buffer;
                        this.helper9();
                     } else {
                        ModernPages.live(this.focus, this.buffer);
                     }
                  }

                  return true;
               default:
                  return true;
            }
         } else if (keyCode == 256 || keyCode == 344) {
            this.helper14();
            return true;
         } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
         }
      } else {
         this.helper13(keyCode != 256 && keyCode != 261 ? keyCode : -1);
         return true;
      }
   }

   private void helper12() {
      Object var1 = this.focus;
      String var2 = this.buffer;
      this.focus = null;
      this.buffer = "";
      if (var1 != null && var1 != FOCUS_SEARCH) {
         ModernPages.commit(this, var1, var2);
      }
   }

   public void startBind(Module module) {
      this.bindingModule = module;
      this.bindingBind = null;
      this.bindingBoolean = null;
   }

   public void startBind(BindSetting setting) {
      this.bindingBind = setting;
      this.bindingModule = null;
      this.bindingBoolean = null;
   }

   public void startBind(BooleanSetting setting) {
      this.bindingBoolean = setting;
      this.bindingModule = null;
      this.bindingBind = null;
   }

   private void helper13(int key) {
      if (this.bindingModule != null) {
         this.bindingModule.setKey(key);
      } else if (this.bindingBind != null) {
         this.bindingBind.setKey(key);
      } else if (this.bindingBoolean != null) {
         this.bindingBoolean.setKey(key);
      }

      this.bindingModule = null;
      this.bindingBind = null;
      this.bindingBoolean = null;
   }

   public boolean isBinding(Object target) {
      return target != null && (target == this.bindingModule || target == this.bindingBind || target == this.bindingBoolean);
   }

   @Override
   public void close() {
      this.helper14();
   }

   private void helper14() {
      if (!this.flag2) {
         this.flag2 = true;
         this.focus = null;
         if (!this.flag3) {
            this.flag3 = true;
            ClientSoundPlayer.playGuiClose();
         }
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   private String helper15() {
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.globalSocialManager != null) {
         String var1 = Lumen.INSTANCE.globalSocialManager.getDisplayName();
         if (var1 != null && !var1.isBlank()) {
            return var1;
         }
      }

      return mc != null && mc.getSession() != null ? mc.getSession().getUsername() : "Player";
   }

   public interface Click {
      boolean click(int var1);
   }

   public interface DragHandler {
      void drag(double var1, double var3);
   }

   private record NavEntry(String label, String iconFont, String glyph, String badge, ModernGui.Page target) {
   }

   private record Hit(float x, float y, float w, float h, float[] clip, ModernGui.Click action) {

      private Hit(float x, float y, float w, float h, float[] clip, ModernGui.Click action) {
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.clip = clip;
         this.action = action;
      }

      public float x() {
         return this.x;
      }

      public float y() {
         return this.y;
      }

      public float w() {
         return this.w;
      }

      public float h() {
         return this.h;
      }

      public float[] clip() {
         return this.clip;
      }

      public ModernGui.Click action() {
         return this.action;
      }
   }

   public enum Page {
      MODULES,
      PROFILE,
      CONFIGS,
      FRIENDS,
      THEMES,
      MARKET,
      AUTOSET,
      BOTS;
   }
}
