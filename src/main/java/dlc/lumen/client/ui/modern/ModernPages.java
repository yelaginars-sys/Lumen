package dlc.lumen.client.ui.modern;

import dlc.lumen.Lumen;
import dlc.lumen.api.storages.implement.ClientColors;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.autobuy.AutoBuyConfig;
import dlc.lumen.client.autobuy.ItemDatabase;
import dlc.lumen.client.autobuy.ItemEntry;
import dlc.lumen.client.autoset.AutoSetCatalog;
import dlc.lumen.client.autoset.AutoSetCollector;
import dlc.lumen.client.autoset.AutoSetItem;
import dlc.lumen.client.autoset.AutoSetKit;
import dlc.lumen.client.autoset.AutoSetKitStorage;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import dlc.lumen.client.render.figura.FiguraAvatarIcons;
import dlc.lumen.client.render.figura.FiguraBridge;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public final class ModernPages {
   private static final Object OBJECT = "cfgname";
   private static final Object OBJECT2 = "friendnick";
   private static final Object OBJECT3 = "bannick";
   private static final Object OBJECT4 = "itemsearch";
   private static final Object OBJECT5 = "minprice";
   private static final Object OBJECT6 = "paynick";
   private static final Object OBJECT7 = "paykeep";
   private static final Object OBJECT8 = "autosetname";
   private static final Object OBJECT9 = "autosetsearch";
   private static String text2 = "";
   private static String text3 = "";
   private static String text4 = "";
   private static String text5 = "";
   private static int index = 0;
   private static int index2 = 0;
   private static int index3 = 0;
   private static String text6 = "Новый сет";
   private static String text7 = "";
   private static AutoSetKit autoSetKit = new AutoSetKit("Новый сет");
   private static float volume = 0.544F;
   private static float volume2 = 0.5F;
   private static float volume3 = 0.98F;
   private static int index4 = Integer.MIN_VALUE;
   private static int index5 = 0;

   private ModernPages() {
   }

   public static void profile(ModernGui g, float x, float y, float w, float h) {
      float var5 = g.scroll();
      float var6 = Math.min(126.0F, w * 0.36F);
      float var7 = Math.min(h, 152.0F);
      g.surface(x, y, var6, var7, ModernTheme.R_CARD(), ModernTheme.PANEL(), ModernTheme.CARD_BORDER(), 4.0F);
      float var8 = x + 7.0F;
      float var9 = y + 7.0F;
      float var10 = var6 - 14.0F;
      float var11 = var7 - 32.0F;
      if (g.client() != null && g.client().player != null) {
         int var12 = (int)(var11 * 0.34F);
         InventoryScreen.drawEntity(
            g.ctx, (int)var8, (int)var9, (int)(var8 + var10), (int)(var9 + var11 - 6.0F), var12, 0.0F, g.mouseX, g.mouseY, g.client().player
         );
         ScissorUtils.reapply();
      }

      String var29 = Lumen.INSTANCE != null && Lumen.INSTANCE.globalSocialManager != null
         ? Lumen.INSTANCE.globalSocialManager.getDisplayName()
         : (g.client() != null && g.client().getSession() != null ? g.client().getSession().getUsername() : "Player");
      Font var13 = g.font(11);
      g.textCenter(var13, g.clip(var13, var29, var10), x + var6 / 2.0F, y + 2.0F + var7 - 22.0F, ModernTheme.accent());
      int var14 = FiguraBridge.selectedCount();
      String var15 = var14 == 0 ? "ничего не надето" : "надето: " + var14;
      g.textCenter(g.font(9), var15, x + var6 / 2.0F, y + var7 - 12.0F + 2.0F, ModernTheme.TEXT_MUTED());
      float var16 = x + var6 + 8.0F;
      float var17 = w - var6 - 8.0F;
      FiguraBridge.Category[] var18 = FiguraBridge.Category.values();
      int var19 = Math.max(0, Math.min(index5, var18.length - 1));
      float var20 = (var17 - 6.0F * (var18.length - 1)) / var18.length;

      for (int var21 = 0; var21 < var18.length; var21++) {
         int var22 = var21;
         g.pill(var16 + var21 * (var20 + 6.0F), y, var20, 18.0F, var18[var21].title, null, null, var19 == var21, "mdl" + var21, button -> {
            index5 = var22;
            return true;
         });
      }

      float var30 = y + 24.0F;
      g.pushClip(var16, var30 - 2.0F, var17 + 6.0F, Math.max(0.0F, y + h - var30 + 2.0F));
      List<FiguraBridge.AvatarInfo> var31 = FiguraBridge.listAvatarInfos(var18[var19]);
      float var23 = var30 - var5;
      FiguraBridge.Category var24 = var18[var19];
      boolean var25 = FiguraBridge.getSelected(var24) == null;
      helper(g, var16, var23, var17, var24.removeTitle, null, var25, "none" + var24.name(), () -> FiguraBridge.select(var24, null));
      var23 += 22.0F;

      for (FiguraBridge.AvatarInfo var27 : var31) {
         boolean var28 = FiguraBridge.isSelectedAny(var27.id());
         helper(g, var16, var23, var17, var27.name(), FiguraAvatarIcons.icon(var27), var28, var27.id(), () -> FiguraBridge.select(var24, var27.id()));
         var23 += 22.0F;
      }

      g.popClip();
      g.setContentHeight(Math.max(var23 - (var30 - var5) + 24.0F, var7), h);
   }

   private static void helper(ModernGui g, float x, float y, float w, String label, Identifier icon, boolean active, String key, Runnable action) {
      g.row(x, y, w, 19.0F, active, "mdlrow" + key);
      float var9 = x + 9.0F;
      if (icon != null) {
         RenderUtils.drawImage(g.m, icon, x + 5.0F, y + 4.0F, 11.0F, 11.0F, g.a(-1));
         var9 = x + 20.0F;
      } else {
         g.icon("icon1", 9, "j", x + 10.0F, y + 9.5F, active ? ModernTheme.accent() : ModernTheme.ICON_IDLE());
         var9 = x + 19.0F;
      }

      Font var10 = g.font(10);
      g.text(var10, g.clip(var10, label, w - (var9 - x) - 40.0F), var9, g.textY(y, 19.0F, var10), active ? ModernTheme.TEXT() : ModernTheme.TEXT_DIM());
      if (active) {
         g.textRight(g.font(9), "выбрана", x + w - 8.0F, g.textY(y, 19.0F, g.font(9)), ModernTheme.accent());
      }

      g.hit(x, y, w, 19.0F, button -> {
         action.run();
         return true;
      });
   }

   public static void configs(ModernGui g, float x, float y, float w, float h) {
      float var5 = g.scroll();
      float var6 = y - var5;
      g.field(x, var6, w - 52.0F, 19.0F, OBJECT, "Название нового конфига", text2, "v");
      helper14(g, x + w - 48.0F, var6, 48.0F, 19.0F, "создать", () -> {
         String var0 = text2.isBlank() ? "config" : text2.trim();

         try {
            Lumen.INSTANCE.configStorage.saveConfig(var0);
            Lumen.INSTANCE.configStorage.currentConfig = var0;
         } catch (Exception var2) {
         }

         text2 = "";
      });
      var6 += 30.0F;
      ArrayList<String> var7 = new ArrayList<>(Lumen.INSTANCE.configStorage.getAvailableConfigs());
      if (var7.isEmpty()) {
         g.text(g.font(10), "конфигов пока нет", x + 2.0F, var6 + 4.0F, ModernTheme.TEXT_MUTED());
      }

      for (String var9 : var7) {
         boolean var10 = var9.equals(Lumen.INSTANCE.configStorage.currentConfig);
         g.row(x, var6, w, 26.0F, var10, "cfg" + var9);
         g.rect(x + 7.0F, var6 + 6.0F, 14.0F, 14.0F, 5.0F, ModernTheme.PILL());
         g.icon("iconnew", 10, "h", x + 14.0F, var6 + 13.0F, var10 ? ModernTheme.accent() : ModernTheme.ICON_IDLE());
         Font var11 = g.font(11);
         g.text(var11, g.clip(var11, var9, w - 90.0F), x + 26.0F, var6 + 7.0F, var10 ? ModernTheme.TEXT() : ModernTheme.TEXT_DIM());
         g.text(g.font(9), var10 ? "активный конфиг" : "сохранён", x + 26.0F, var6 + 16.0F, ModernTheme.TEXT_MUTED());
         float var12 = x + w - 23.0F;
         helper15(g, var12, var6 + 5.0F, "play", ModernTheme.accent(), "load" + var9, () -> {
            try {
               Lumen.INSTANCE.configStorage.loadConfig(var9);
               Lumen.INSTANCE.configStorage.currentConfig = var9;
            } catch (Exception var2) {
            }
         });
         var12 -= 20.0F;
         helper15(g, var12, var6 + 5.0F, "save", ModernTheme.TEXT_DIM(), "save" + var9, () -> {
            try {
               Lumen.INSTANCE.configStorage.saveConfig(var9);
            } catch (Exception var2) {
            }
         });
         var12 -= 20.0F;
         helper15(g, var12, var6 + 5.0F, "trash", ModernTheme.DANGER, "del" + var9, () -> Lumen.INSTANCE.configStorage.deleteConfig(var9));
         var6 += 30.0F;
      }

      g.setContentHeight(var6 - (y - var5), h);
   }

   public static void friends(ModernGui g, float x, float y, float w, float h) {
      float var5 = g.scroll();
      float var6 = y - var5;
      g.field(x, var6, w - 52.0F, 19.0F, OBJECT2, "Ник игрока", text3, "n");
      helper14(g, x + w - 48.0F, var6, 48.0F, 19.0F, "добавить", () -> {
         if (!text3.isBlank()) {
            Lumen.INSTANCE.friendStorage.add(text3.trim());
            text3 = "";
         }
      });
      var6 += 30.0F;
      List<String> var7 = Lumen.INSTANCE.friendStorage.getFriends();
      if (var7.isEmpty()) {
         g.text(g.font(10), "список друзей пуст", x + 2.0F, var6 + 4.0F, ModernTheme.TEXT_MUTED());
      }

      for (String var9 : var7) {
         g.row(x, var6, w, 26.0F, false, "fr" + var9);
         g.rect(x + 7.0F, var6 + 6.0F, 14.0F, 14.0F, 7.0F, ModernTheme.PILL());
         String var10 = var9.isEmpty() ? "?" : var9.substring(0, 1).toUpperCase(Locale.ROOT);
         g.textCenter(g.font(9), var10, x + 14.0F, g.textY(var6 + 6.0F, 14.0F, g.font(9)), ModernTheme.accent());
         Font var11 = g.font(11);
         g.text(var11, g.clip(var11, var9, w - 60.0F), x + 26.0F, var6 + 6.0F, ModernTheme.TEXT_SOFT());
         String var12 = Lumen.INSTANCE.friendStorage.getCustomName(var9);
         g.text(g.font(9), var12 == null ? "не атакуется аурой" : var12, x + 26.0F, var6 + 16.0F, ModernTheme.TEXT_MUTED());
         helper15(g, x + w - 23.0F, var6 + 4.0F, "trash", ModernTheme.DANGER, "rm" + var9, () -> Lumen.INSTANCE.friendStorage.remove(var9));
         var6 += 30.0F;
      }

      g.setContentHeight(var6 - (y - var5), h);
   }

   public static void themes(ModernGui g, float x, float y, float w, float h) {
      float var5 = g.scroll();
      float var6 = y - var5;
      g.surface(x, var6, w, 48.0F, ModernTheme.R_CARD(), ModernTheme.PANEL(), ModernTheme.CARD_BORDER(), 4.0F);
      g.sectionLabel(x + 9.0F, var6 + 8.0F, "Акцент");
      float var7 = x + 9.0F;

      for (int var8 = 0; var8 < ModernTheme.ACCENTS.length; var8++) {
         int var9 = var8;
         boolean var10 = ModernTheme.accentIndex() == var8;
         boolean var11 = g.hovered(var7, var6 + 21.0F, 18.0F, 18.0F);
         float var12 = ModernAnim.value("sw:" + var8, var10 ? 1.0F : (var11 ? 0.6F : 0.0F), 15.0F);
         if (var10 || var12 > 0.01F) {
            g.outline(var7 - 2.0F, var6 + 19.0F, 22.0F, 22.0F, 8.0F, 0.7F, ColorUtils.applyAlpha(ModernTheme.ACCENTS[var8], var12));
         }

         g.rect(var7, var6 + 21.0F, 18.0F, 18.0F, 6.0F, ModernTheme.ACCENTS[var8]);
         g.hit(var7, var6 + 21.0F, 18.0F, 18.0F, button -> {
            ModernTheme.setAccentIndex(var9);
            return true;
         });
         var7 += 24.0F;
      }

      var6 += 55.0F;
      int var28 = ColorUtils.replAlpha(ClientColors.raw(ClientColors.Slot.OUTLINE), 255);
      if (var28 != index4) {
         index4 = var28;
         float[] var29 = Color.RGBtoHSB(ColorUtils.r(var28), ColorUtils.g(var28), ColorUtils.b(var28), null);
         volume = helper2(var29[0]);
         volume2 = helper2(var29[1]);
         volume3 = helper2(var29[2]);
      }

      g.surface(x, var6, w, 92.0F, ModernTheme.R_CARD(), ModernTheme.PANEL(), ModernTheme.CARD_BORDER(), 4.0F);
      g.sectionLabel(x + 9.0F, var6 + 8.0F, "Свой цвет");
      float var30 = x + 9.0F;
      float var31 = var6 + 19.0F;
      float var32 = 56.0F;
      float var33 = var30 + var32 + 6.0F;
      float var13 = 12.0F;
      int var14 = ColorUtils.setAlphaColor(Color.HSBtoRGB(volume, 1.0F, 1.0F), 255);
      RenderUtils.drawGradientRect(
         g.m,
         var30,
         var31,
         var32,
         var32,
         7.0F,
         g.a(ColorUtils.rgba(255, 255, 255, 255)),
         g.a(var14),
         g.a(ColorUtils.rgba(0, 0, 0, 255)),
         g.a(ColorUtils.rgba(0, 0, 0, 255))
      );
      float var15 = var30 + volume2 * var32;
      float var16 = var31 + (1.0F - volume3) * var32;
      RenderUtils.drawFilledCircle(g.m, var15, var16, 5.5F, g.a(ColorUtils.rgba(0, 0, 0, 170)));
      RenderUtils.drawFilledCircle(g.m, var15, var16, 4.0F, g.a(-1));
      g.hit(var30 - 3.0F, var31 - 3.0F, var32 + 6.0F, var32 + 6.0F, button -> {
         ModernGui.DragHandler var5x = (dx, dy) -> {
            volume2 = helper2((float)((dx - var30) / var32));
            volume3 = 1.0F - helper2((float)((dy - var31) / var32));
            helper4();
         };
         var5x.drag(g.mouseX, g.mouseY);
         g.dragging = var5x;
         return true;
      });
      byte var17 = 72;
      float var18 = var32 / var17;

      for (int var19 = 0; var19 < var17; var19++) {
         float var20 = (float)var19 / (var17 - 1);
         int var21 = ColorUtils.setAlphaColor(Color.HSBtoRGB(var20, 1.0F, 1.0F), 255);
         float var22 = var19 == 0 ? 3.0F : 0.0F;
         float var23 = var19 == var17 - 1 ? 3.0F : 0.0F;
         RenderUtils.drawRoundedRect(g.m, var33, var31 + var19 * var18, var13, var18 + 0.4F, var22, var22, var23, var23, g.a(var21));
      }

      float var34 = var31 + volume * var32;
      g.rect(var33 - 2.0F, var34 - 2.0F, var13 + 4.0F, 4.0F, 2.0F, ModernTheme.TEXT());
      g.hit(var33 - 3.0F, var31 - 3.0F, var13 + 6.0F, var32 + 6.0F, button -> {
         ModernGui.DragHandler var4 = (dx, dy) -> {
            volume = helper2((float)((dy - var31) / var32));
            helper4();
         };
         var4.drag(g.mouseX, g.mouseY);
         g.dragging = var4;
         return true;
      });
      float var35 = var33 + var13 + 12.0F;
      g.rect(var35, var31, 26.0F, 26.0F, 7.0F, helper3());
      g.outline(var35, var31, 26.0F, 26.0F, 7.0F, 0.5F, ModernTheme.CARD_BORDER());
      String var36 = String.format(Locale.ROOT, "#%06X", 16777215 & helper3());
      g.text(g.font(9), var36, var35, var31 + 31.0F, ModernTheme.TEXT_DIM());
      g.text(g.font(8), "акцент клиента", var35, var31 + 41.0F, ModernTheme.TEXT_MUTED());
      var6 += 100.0F;
      g.surface(x, var6, w, 104.0F, ModernTheme.R_CARD(), ModernTheme.PANEL(), ModernTheme.CARD_BORDER(), 4.0F);
      float var37 = x + 9.0F;
      float var38 = w - 18.0F;
      float var24 = var6 + 9.0F;
      helper5(g, var37, var24, var38, "Скругление", ClickGuiTheme.radiusMul(), 0.0F, 1.0F, value -> ClickGuiTheme.setRadiusMul((float)value));
      var24 += 21.0F;
      helper5(g, var37, var24, var38, "Прозрачность", ClickGuiTheme.opacity(), 0.1F, 1.0F, value -> ClickGuiTheme.setOpacity((float)value));
      var24 += 21.0F;
      helper5(g, var37, var24, var38, "Блюр", ClickGuiTheme.blurMul(), 0.0F, 8.0F, value -> ClickGuiTheme.setBlur((float)value));
      var24 += 23.0F;
      helper6(g, var37, var24, var38, "Тени", ModernTheme.shadow(), "shadow", () -> ModernTheme.setShadow(!ModernTheme.shadow()));
      var24 += 16.0F;
      helper6(g, var37, var24, var38, "Анимации", ModernTheme.animations(), "anims", () -> ModernTheme.setAnimations(!ModernTheme.animations()));
      var6 += 110.0F;
      g.setContentHeight(var6 - (y - var5), h);
   }

   private static float helper2(float value) {
      return MathHelper.clamp(value, 0.0F, 1.0F);
   }

   private static int helper3() {
      return Color.HSBtoRGB(volume, volume2, volume3);
   }

   private static void helper4() {
      int var0 = ColorUtils.replAlpha(helper3(), 255);
      ClientColors.set(ClientColors.Slot.OUTLINE, var0);
      ClientColors.set(ClientColors.Slot.ICON, var0);
      ClientColors.set(ClientColors.Slot.TOGGLE, var0);
      index4 = var0;
   }

   private static void helper5(ModernGui g, float x, float y, float w, String label, float value, float min, float max, DoubleConsumer setter) {
      Font var9 = g.font(10);
      g.text(var9, label, x, g.textY(y, 11.0F, var9), ModernTheme.TEXT_DIM());
      String var10 = max > 20.0F ? String.valueOf(Math.round(value)) : String.format(Locale.ROOT, "%.2f", value);
      g.valuePill(x + w, y, var10, ModernTheme.accent());
      float var11 = max - min <= 0.0F ? 0.0F : (value - min) / (max - min);
      g.slider(x, y + 14.0F, w, var11, label, fraction -> setter.accept(min + fraction * (max - min)));
   }

   private static void helper6(ModernGui g, float x, float y, float w, String label, boolean state, String key, Runnable action) {
      Font var8 = g.font(10);
      g.text(var8, label, x, g.textY(y, 10.0F, var8), ModernTheme.TEXT_DIM());
      g.toggle(x + w - 20.0F, y, state, key);
      g.hit(x, y, w, 12.0F, button -> {
         action.run();
         return true;
      });
   }

   public static String marketSubtitle() {
      AutoBuyConfig var0 = AutoBuyConfig.cfg();
      return var0.entries.size() + " предметов · " + (var0.enabled ? "покупка вкл" : "покупка выкл");
   }

   public static void market(ModernGui g, float x, float y, float w, float h) {
      AutoBuyConfig var5 = AutoBuyConfig.cfg();
      float var6 = g.scroll();
      float var7 = y - var6;
      float var8 = (w - 12.0F) / 3.0F;
      helper16(g, x, var7, var8, "Покупка", "icon", "u", var5.enabled, "mkt0", () -> {
         var5.enabled = !var5.enabled;
         AutoBuyConfig.save();
      });
      helper16(g, x + var8 + 6.0F, var7, var8, "Парсинг", "icon", "D", var5.autoParse, "mkt1", () -> {
         var5.autoParse = !var5.autoParse;
         AutoBuyConfig.save();
      });
      helper16(g, x + (var8 + 6.0F) * 2.0F, var7, var8, "Автопродажа", "icon", "z", var5.autoSell, "mkt2", () -> {
         var5.autoSell = !var5.autoSell;
         AutoBuyConfig.save();
      });
      var7 += 25.0F;
      String[] var9 = new String[]{"Add Items", "Edit Items", "Settings", "Ban list"};
      String[] var10 = new String[]{"icon1", "icon", "icon1", "icon"};
      String[] var11 = new String[]{"v", "n", "t", "L"};
      float var12 = (w - 18.0F) / 4.0F;

      for (int var13 = 0; var13 < var9.length; var13++) {
         int var14 = var13;
         g.pill(x + var13 * (var12 + 6.0F), var7, var12, 19.0F, var9[var13], var10[var13], var11[var13], index == var13, "mtab" + var13, button -> {
            index = var14;
            return true;
         });
      }

      var7 += 25.0F;

      g.setContentHeight(switch (index) {
         case 0 -> helper7(g, var5, x, var7, w);
         case 1 -> helper9(g, var5, x, var7, w);
         case 2 -> helper10(g, var5, x, var7, w);
         default -> helper11(g, var5, x, var7, w);
      } - (y - var6), h);
   }

   private static float helper7(ModernGui g, AutoBuyConfig cfg, float x, float y, float w) {
      float var5 = y;
      g.field(x, var5, w, 19.0F, OBJECT4, "Search items", text5, "l");
      var5 += 24.0F;
      ArrayList<ItemEntry> var6 = new ArrayList<>(ItemDatabase.LIST);
      if (var6.isEmpty()) {
         g.text(g.font(10), "база предметов пуста", x + 2.0F, var5 + 4.0F, ModernTheme.TEXT_MUTED());
         return var5 + 16.0F;
      }

      String var7 = text5.toLowerCase(Locale.ROOT);
      ArrayList<ItemEntry> var8 = new ArrayList<>();

      for (ItemEntry var10 : var6) {
         if (var7.isBlank() || var10.label().toLowerCase(Locale.ROOT).contains(var7)) {
            var8.add(var10);
         }
      }

      float var21 = 28.0F;
      float var22 = 5.0F;
      int var11 = Math.max(1, (int)((w + var22) / (var21 + var22)));
      float var12 = x + (w - (var11 * var21 + (var11 - 1) * var22)) / 2.0F;

      for (int var13 = 0; var13 < var8.size(); var13++) {
         ItemEntry var14 = (ItemEntry)var8.get(var13);
         float var15 = var12 + var13 % var11 * (var21 + var22);
         float var16 = var5 + var13 / var11 * (var21 + var22);
         boolean var17 = helper8(cfg, var14);
         boolean var18 = g.hovered(var15, var16, var21, var21);
         float var19 = ModernAnim.value("tile:" + var14.itemId + var13, var18 ? 1.0F : 0.0F, 16.0F);
         g.surface(
            var15,
            var16,
            var21,
            var21,
            ModernTheme.R_PILL(),
            ColorUtils.interpolate(var17 ? ModernTheme.CARD_ON() : ModernTheme.CARD(), ModernTheme.PILL_HOVER(), var19 * 0.6F),
            var17 ? ModernTheme.accent() : ModernTheme.CARD_BORDER(),
            2.5F
         );
         helper12(g, var14, var15 + var21 / 2.0F, var16 + var21 / 2.0F);
         g.hit(var15, var16, var21, var21, button -> {
            if (var17) {
               cfg.entries.removeIf(existing -> existing.itemId != null && existing.itemId.equals(var14.itemId));
            } else {
               ItemEntry var4 = new ItemEntry(var14.itemId, var14.displayName, var14.maxUnitPrice > 0L ? var14.maxUnitPrice : 100000L);
               var4.searchQuery = var14.searchQuery;
               var4.loreKeyword = var14.loreKeyword;
               var4.matchAnySword = var14.matchAnySword;
               var4.markers = var14.markers;
               cfg.entries.add(var4);
            }

            AutoBuyConfig.save();
            return true;
         });
      }

      int var23 = (var8.size() + var11 - 1) / var11;
      return var5 + var23 * (var21 + var22);
   }

   private static boolean helper8(AutoBuyConfig cfg, ItemEntry entry) {
      for (ItemEntry var3 : cfg.entries) {
         if (var3.itemId != null && var3.itemId.equals(entry.itemId)) {
            return true;
         }
      }

      return false;
   }

   private static float helper9(ModernGui g, AutoBuyConfig cfg, float x, float y, float w) {
      float var5 = y;
      if (cfg.entries.isEmpty()) {
         g.text(g.font(10), "предметы не выбраны — добавь их во вкладке Add Items", x + 2.0F, var5 + 4.0F, ModernTheme.TEXT_MUTED());
         return var5 + 16.0F;
      }

      for (int var6 = 0; var6 < cfg.entries.size(); var6++) {
         ItemEntry var7 = cfg.entries.get(var6);
         g.row(x, var5, w, 26.0F, var7.enabled, "item" + var6);
         g.rect(x + 6.0F, var5 + 4.0F, 18.0F, 18.0F, 6.0F, ModernTheme.PILL());
         helper12(g, var7, x + 15.0F, var5 + 13.0F);
         Font var8 = g.font(11);
         g.text(var8, g.clip(var8, var7.label(), w - 130.0F), x + 29.0F, var5 + 6.0F, var7.enabled ? ModernTheme.TEXT_SOFT() : ModernTheme.TEXT_MUTED());
         g.text(g.font(9), "макс. цена", x + 29.0F, var5 + 16.0F, ModernTheme.TEXT_MUTED());
         float var9 = x + w - 50.0F;
         String var10 = helper17(var7.maxUnitPrice);
         Font var11 = g.font(9);
         float var12 = var11.getWidth(var10) + 14.0F;
         float var13 = var5 + 7.5F;
         boolean var14 = g.focus == var7;
         g.rect(var9 - var12, var13, var12, 11.0F, ModernTheme.R_SMALL(), ModernTheme.PILL());
         String var15 = var14 ? g.buffer : var10;
         g.textCenter(var11, g.clip(var11, var15, var12 - 4.0F), var9 - var12 / 2.0F, g.textY(var13, 11.0F, var11), ModernTheme.accent());
         g.hit(var9 - var12, var13, var12, 11.0F, button -> {
            g.focus = var7;
            g.buffer = String.valueOf(var7.maxUnitPrice);
            return true;
         });
         g.toggle(x + w - 46.0F, var5 + 8.0F, var7.enabled, "item" + var6);
         g.hit(x + w - 48.0F, var5 + 4.0F, 24.0F, 18.0F, button -> {
            var7.enabled = !var7.enabled;
            AutoBuyConfig.save();
            return true;
         });
         helper15(g, x + w - 23.0F, var5 + 4.0F, "trash", ModernTheme.DANGER, "delitem" + var6, () -> {
            cfg.entries.remove(var7);
            AutoBuyConfig.save();
         });
         var5 += 30.0F;
      }

      return var5;
   }

   private static float helper10(ModernGui g, AutoBuyConfig cfg, float x, float y, float w) {
      float var5 = y;
      g.surface(x, var5, w, 148.0F, ModernTheme.R_CARD(), ModernTheme.PANEL(), ModernTheme.CARD_BORDER(), 4.0F);
      float var6 = x + 9.0F;
      float var7 = w - 18.0F;
      float var8 = var5 + 9.0F;
      Font var9 = g.font(10);
      g.text(var9, "Мин. цена", var6, g.textY(var8, 15.0F, var9), ModernTheme.TEXT_DIM());
      g.field(var6 + var7 - 82.0F, var8, 82.0F, 15.0F, OBJECT5, "0", helper17(cfg.minPrice), null);
      var8 += 21.0F;
      helper5(g, var6, var8, var7, "Парсинг, %", cfg.parsePercent, 10.0F, 100.0F, value -> {
         cfg.parsePercent = (int)Math.round(value);
         AutoBuyConfig.save();
      });
      var8 += 21.0F;
      helper5(g, var6, var8, var7, "Интервал кликов, тиков", cfg.clickIntervalTicks, 0.0F, 10.0F, value -> {
         cfg.clickIntervalTicks = (int)Math.round(value);
         AutoBuyConfig.save();
      });
      var8 += 23.0F;
      helper6(g, var6, var8, var7, "Автообновление", cfg.autoRefresh, "arf", () -> {
         cfg.autoRefresh = !cfg.autoRefresh;
         AutoBuyConfig.save();
      });
      var8 += 16.0F;
      helper6(g, var6, var8, var7, "Мин. цена включена", cfg.minPriceEnabled, "mpe", () -> {
         cfg.minPriceEnabled = !cfg.minPriceEnabled;
         AutoBuyConfig.save();
      });
      var8 += 16.0F;
      helper6(g, var6, var8, var7, "Автовыплата", cfg.autoPay, "apay", () -> {
         cfg.autoPay = !cfg.autoPay;
         AutoBuyConfig.save();
      });
      var5 += 154.0F;
      g.surface(x, var5, w, 52.0F, ModernTheme.R_CARD(), ModernTheme.PANEL(), ModernTheme.CARD_BORDER(), 4.0F);
      var8 = var5 + 9.0F;
      g.text(var9, "Ник твинка", x + 9.0F, g.textY(var8, 15.0F, var9), ModernTheme.TEXT_DIM());
      g.field(x + w - 91.0F, var8, 82.0F, 15.0F, OBJECT6, "ник", cfg.autoPayNick, null);
      var8 += 21.0F;
      g.text(var9, "Держать баланс", x + 9.0F, g.textY(var8, 15.0F, var9), ModernTheme.TEXT_DIM());
      g.field(x + w - 91.0F, var8, 82.0F, 15.0F, OBJECT7, "0", helper17(cfg.autoPayKeepBalance), null);
      return var5 + 58.0F;
   }

   private static float helper11(ModernGui g, AutoBuyConfig cfg, float x, float y, float w) {
      float var5 = y;
      g.field(x, var5, w - 52.0F, 19.0F, OBJECT3, "Ник продавца", text4, "n");
      helper14(g, x + w - 48.0F, var5, 48.0F, 19.0F, "добавить", () -> {
         if (!text4.isBlank()) {
            cfg.addBan(text4.trim());
            text4 = "";
         }
      });
      var5 += 30.0F;
      if (cfg.banList.isEmpty()) {
         g.text(g.font(10), "бан-лист пуст", x + 2.0F, var5 + 4.0F, ModernTheme.TEXT_MUTED());
         return var5 + 16.0F;
      }

      for (String var7 : new ArrayList<>(cfg.banList)) {
         g.row(x, var5, w, 22.0F, false, "ban" + var7);
         Font var8 = g.font(10);
         g.text(var8, g.clip(var8, var7, w - 38.0F), x + 10.0F, g.textY(var5, 22.0F, var8), ModernTheme.TEXT_SOFT());
         helper15(g, x + w - 23.0F, var5 + 1.5F, "trash", ModernTheme.DANGER, "unban" + var7, () -> cfg.removeBan(var7));
         var5 += 25.0F;
      }

      return var5;
   }

   public static String autosetSubtitle() {
      return AutoSetCollector.INSTANCE.isRunning()
         ? "сборщик запущен · " + AutoSetCollector.INSTANCE.statusLine()
         : AutoSetKitStorage.kits().size() + " сохранённых сетов";
   }

   public static void autoset(ModernGui g, float x, float y, float w, float h) {
      float var5 = g.scroll();
      float var6 = y - var5;
      float var7 = 142.0F;
      float var8 = 8.0F;
      float var9 = x + var7 + var8;
      float var10 = w - var7 - var8;
      float var11 = var6;
      g.field(x, var11, var7, 18.0F, OBJECT9, "Поиск позиций...", text7, "l");
      var11 += 22.0F;
      String[] var12 = AutoSetCatalog.GROUP_KEYS;
      String[] var13 = AutoSetCatalog.GROUP_LABELS;
      float var14 = 46.0F;
      float var15 = 15.0F;

      for (int var16 = 0; var16 < var12.length; var16++) {
         int var17 = var16;
         boolean var18 = index3 == var16 && text7.isBlank();
         float var19 = var16 % 3;
         float var20 = var16 / 3;
         float var21 = x + var19 * (var14 + 2.0F);
         float var22 = var11 + var20 * (var15 + 2.0F);
         g.pill(var21, var22, var14, var15, var13[var16], null, null, var18, "asgrp" + var16, button -> {
            index3 = var17;
            text7 = "";
            return true;
         });
      }

      var11 += (var15 + 2.0F) * 2.0F + 4.0F;
      List var34;
      if (!text7.isBlank()) {
         String var35 = text7.toLowerCase(Locale.ROOT);
         var34 = new ArrayList();

         for (AutoSetItem var47 : AutoSetCatalog.list()) {
            if (var47.name().toLowerCase(Locale.ROOT).contains(var35) || var47.searchName().toLowerCase(Locale.ROOT).contains(var35)) {
               var34.add(var47);
            }
         }
      } else {
         var34 = AutoSetCatalog.byGroup(var12[Math.max(0, Math.min(index3, var12.length - 1))]);
      }

      if (var34.isEmpty()) {
         g.text(g.font(9), "позиции не найдены", x + 4.0F, g.textY(var11, 18.0F, g.font(9)), ModernTheme.TEXT_MUTED());
         var11 += 18.0F;
      } else {
         float var36 = 20.0F;

         for (int var45 = 0; var45 < var34.size(); var45++) {
            AutoSetItem var48 = (AutoSetItem)var34.get(var45);
            AutoSetKit.Entry var50 = autoSetKit.find(var48);
            boolean var52 = var50 != null && var50.count > 0;
            int var54 = var50 != null ? var50.count : 0;
            float var23 = var11;
            g.row(x, var23, var7, var36, var52, "astrow:" + var48.key() + var45);
            helper12(g, var48.entry, x + 14.0F, var23 + 10.0F);
            if (var52) {
               helper15(g, x + var7 - 40.0F - 5.0F, var23 + 2.0F, "-", ModernTheme.DANGER, "sub" + var48.key(), () -> autoSetKit.add(var48, -1));
               g.textCenter(g.font(9), String.valueOf(var54), x + var7 - 25.0F, g.textY(var23, var36, g.font(9)), ModernTheme.accent());
               helper15(g, x + var7 - 18.0F - 2.0F, var23 + 2.0F, "+", ModernTheme.accent(), "add" + var48.key(), () -> autoSetKit.add(var48, 1));
            } else {
               helper14(g, x + var7 - 28.0F, var23 + 2.0F, 22.0F, 16.0F, "+1", () -> autoSetKit.add(var48, 1));
            }

            Font var24 = g.font(9);
            g.text(var24, var48.name(), x + 26.0F, g.textY(var23, var36, var24), var52 ? ModernTheme.TEXT() : ModernTheme.TEXT_DIM());
            g.hit(x, var23, var7 - 42.0F, var36, button -> {
               if (button == 0) {
                  autoSetKit.add(var48, 1);
               } else if (button == 1) {
                  autoSetKit.add(var48, -1);
               }

               return true;
            });
            var11 += var36 + 3.0F;
         }
      }

      float var37 = var6;
      boolean var46 = AutoSetCollector.INSTANCE.isRunning();
      g.surface(var9, var37, var10, 22.0F, ModernTheme.R_ROW(), ModernTheme.CARD(), ModernTheme.CARD_BORDER(), 2.0F);
      int var49 = var46 ? ColorUtils.rgba(80, 220, 120, 255) : ColorUtils.rgba(220, 80, 80, 255);
      RenderUtils.drawFilledCircle(g.m, var9 + 10.0F, var37 + 11.0F, 3.0F, g.a(var49));
      Font var51 = g.font(9);
      String var53 = var46 ? "Сбор запущен" : "Сборщик выкл";
      g.text(var51, var53, var9 + 18.0F, g.textY(var37, 22.0F, var51), var46 ? ModernTheme.TEXT() : ModernTheme.TEXT_MUTED());
      helper14(g, var9 + var10 - 50.0F, var37 + 2.0F, 44.0F, 18.0F, var46 ? "стоп" : "старт", () -> {
         if (var46) {
            AutoSetCollector.INSTANCE.stop();
         } else {
            AutoSetCollector.INSTANCE.start(autoSetKit);
         }
      });
      var37 += 26.0F;
      float var55 = 42.0F;
      g.field(var9, var37, var10 - var55 - 4.0F, 18.0F, OBJECT8, "Имя сета", text6, "v");
      helper14(g, var9 + var10 - var55, var37, var55, 18.0F, "сохр.", () -> {
         String var0 = text6.isBlank() ? "Сет" : text6.trim();
         autoSetKit.name = var0;
         AutoSetKitStorage.save(autoSetKit);
      });
      var37 += 24.0F;
      Font var56 = g.font(10);
      g.text(var56, "В сете: " + autoSetKit.total() + " шт.", var9 + 4.0F, g.textY(var37, 14.0F, var56), ModernTheme.TEXT_SOFT());
      var37 += 16.0F;
      if (autoSetKit.entries.isEmpty()) {
         g.surface(var9, var37, var10, 32.0F, ModernTheme.R_ROW(), ModernTheme.CARD(), ModernTheme.CARD_BORDER(), 2.0F);
         g.text(g.font(8), "выберите предметы слева", var9 + 8.0F, g.textY(var37 + 2.0F, 14.0F, g.font(8)), ModernTheme.TEXT_MUTED());
         g.text(g.font(8), "ЛКМ: +1  ·  ПКМ: -1", var9 + 8.0F, g.textY(var37 + 16.0F, 14.0F, g.font(8)), ModernTheme.TEXT_FAINT());
         var37 += 36.0F;
      } else {
         float var57 = 20.0F;

         for (int var25 = 0; var25 < autoSetKit.entries.size(); var25++) {
            AutoSetKit.Entry var26 = autoSetKit.entries.get(var25);
            AutoSetItem var27 = AutoSetKit.resolve(var26);
            String var28 = var27 != null ? var27.name() : (var26.key != null ? var26.key : "Предмет");
            float var29 = var37;
            g.row(var9, var29, var10, var57, true, "astrowkit" + var25);
            if (var27 != null) {
               helper12(g, var27.entry, var9 + 14.0F, var29 + 10.0F);
            }

            Font var30 = g.font(9);
            helper15(g, var9 + var10 - 69.0F, var29 + 2.0F, "-", ModernTheme.DANGER, "subkit" + var25, () -> {
               if (var27 != null) {
                  autoSetKit.add(var27, -1);
               } else {
                  var26.count--;
                  if (var26.count <= 0) {
                     autoSetKit.entries.remove(var26);
                  }
               }
            });
            g.textCenter(g.font(9), String.valueOf(var26.count), var9 + var10 - 46.0F, g.textY(var29, var57, g.font(9)), ModernTheme.accent());
            helper15(g, var9 + var10 - 37.0F, var29 + 2.0F, "+", ModernTheme.accent(), "addkit" + var25, () -> {
               if (var27 != null) {
                  autoSetKit.add(var27, 1);
               } else {
                  var26.count++;
               }
            });
            helper15(g, var9 + var10 - 18.0F, var29 + 2.0F, "trash", ModernTheme.DANGER, "delentry" + var25, () -> {
               if (var27 != null) {
                  autoSetKit.remove(var27);
               } else {
                  autoSetKit.entries.remove(var26);
               }
            });
            g.text(var30, var28, var9 + 26.0F, g.textY(var29, var57, var30), ModernTheme.TEXT_SOFT());
            var37 += var57 + 3.0F;
         }
      }

      var37 += 5.0F;
      g.rect(var9 + 4.0F, var37, var10 - 8.0F, 0.5F, 0.0F, ModernTheme.LINE_SOFT());
      var37 += 8.0F;
      Font var58 = g.font(10);
      g.text(var58, "Сохранённые сеты (" + AutoSetKitStorage.kits().size() + "):", var9 + 4.0F, g.textY(var37, 14.0F, var58), ModernTheme.TEXT_MUTED());
      var37 += 16.0F;
      List var59 = AutoSetKitStorage.kits();
      if (var59.isEmpty()) {
         g.text(g.font(8), "нет сохранённых сетов", var9 + 4.0F, g.textY(var37, 14.0F, g.font(8)), ModernTheme.TEXT_FAINT());
         var37 += 16.0F;
      } else {
         float var60 = 20.0F;

         for (int var62 = 0; var62 < var59.size(); var62++) {
            AutoSetKit var63 = (AutoSetKit)var59.get(var62);
            boolean var64 = autoSetKit != null && var63.name != null && var63.name.equalsIgnoreCase(autoSetKit.name);
            float var65 = var37;
            g.row(var9, var65, var10, var60, var64, "savedkit" + var62);
            Font var31 = g.font(9);
            g.text(
               var31,
               g.clip(var31, var63.name, var10 - 60.0F),
               var9 + 8.0F,
               g.textY(var65, var60, var31),
               var64 ? ModernTheme.accent() : ModernTheme.TEXT_SOFT()
            );
            helper15(
               g, var9 + var10 - 54.0F, var65 + 2.0F, "play", ModernTheme.accent(), "runkit" + var62, () -> AutoSetCollector.INSTANCE.start(var63)
            );
            helper15(g, var9 + var10 - 36.0F, var65 + 2.0F, "save", ModernTheme.TEXT_DIM(), "editkit" + var62, () -> {
               autoSetKit = var63;
               text6 = var63.name != null ? var63.name : "";
            });
            helper15(g, var9 + var10 - 18.0F, var65 + 2.0F, "trash", ModernTheme.DANGER, "delkit" + var62, () -> AutoSetKitStorage.delete(var63));
            var37 += var60 + 3.0F;
         }
      }

      float var61 = Math.max(var11, var37);
      g.setContentHeight(var61 - (y - var5), h);
   }

   private static void helper12(ModernGui g, ItemEntry entry, float centerX, float centerY) {
      ItemStack var4 = helper13(entry.itemId);
      if (var4.isEmpty()) {
         g.icon("icon", 9, "z", centerX, centerY, ModernTheme.ICON_IDLE());
      } else {
         g.m.push();
         g.m.translate(centerX, centerY, 0.0F);
         g.m.scale(0.85F, 0.85F, 1.0F);
         g.ctx.drawItem(var4, -8, -8);
         g.m.pop();
      }
   }

   private static ItemStack helper13(String itemId) {
      if (itemId != null && !itemId.isBlank()) {
         Identifier var1 = Identifier.tryParse(itemId);
         if (var1 == null) {
            return ItemStack.EMPTY;
         }

         Item var2 = Registries.ITEM.get(var1);
         return var2 == Items.AIR ? ItemStack.EMPTY : new ItemStack(var2);
      } else {
         return ItemStack.EMPTY;
      }
   }

   private static void helper14(ModernGui g, float x, float y, float w, float h, String label, Runnable action) {
      boolean var7 = g.hovered(x, y, w, h);
      float var8 = ModernAnim.value("ab:" + label + x, var7 ? 1.0F : 0.0F, 15.0F);
      g.surface(x, y, w, h, ModernTheme.R_ROW(), ColorUtils.interpolate(ModernTheme.PILL(), ModernTheme.PILL_HOVER(), var8), ModernTheme.CARD_BORDER(), 2.5F);
      Font var9 = g.font(9);
      g.textCenter(var9, label, x + w / 2.0F, g.textY(y, h, var9), ModernTheme.accent());
      g.hit(x, y, w, h, button -> {
         action.run();
         return true;
      });
   }

   private static void helper15(ModernGui g, float x, float y, String kind, int color, String key, Runnable action) {
      float var7 = 16.0F;
      boolean var8 = g.hovered(x, y, var7, var7);
      float var9 = ModernAnim.value("mb:" + key, var8 ? 1.0F : 0.0F, 16.0F);
      g.surface(
         x, y, var7, var7, ModernTheme.R_PILL(), ColorUtils.interpolate(ModernTheme.PILL(), ModernTheme.PILL_HOVER(), var9), ModernTheme.CARD_BORDER(), 2.5F
      );
      float var10 = x + var7 / 2.0F;
      float var11 = y + var7 / 2.0F;
      switch (kind) {
         case "play":
            g.playIcon(var10 + 0.4F, var11, 3.2F, color);
            break;
         case "save":
            g.saveIcon(var10, var11, 3.4F, color);
            break;
         case "+":
            g.textCenter(g.font(10), "+", var10, y + 2.5F + 3.0F, color);
            break;
         case "-":
            g.textCenter(g.font(10), "-", var10, y + 2.5F + 3.0F, color);
            break;
         default:
            g.trashIcon(var10, var11, 3.6F, color);
      }

      g.hit(x, y, var7, var7, button -> {
         action.run();
         return true;
      });
   }

   private static void helper16(
      ModernGui g, float x, float y, float w, String label, String fontName, String glyph, boolean active, String key, Runnable action
   ) {
      g.pill(x, y, w, 16.0F, label, fontName, glyph, active, key, button -> {
         action.run();
         return true;
      });
   }

   private static String helper17(long value) {
      String var2 = String.valueOf(value);
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;

      for (int var5 = var2.length() - 1; var5 >= 0; var5--) {
         var3.append(var2.charAt(var5));
         if (++var4 % 3 == 0 && var5 > 0) {
            var3.append(' ');
         }
      }

      return var3.reverse().toString();
   }

   private static long resolveLong(String text) {
      try {
         return Long.parseLong(text.replaceAll("[^0-9]", ""));
      } catch (Exception var2) {
         return -1L;
      }
   }

   public static void commit(ModernGui g, Object target, String value) {
      if (!ModernBotsPage.commit(target, value)) {
         if (target instanceof TextSetting var8) {
            var8.setText(value);
         } else if (target instanceof ItemEntry var7) {
            long var4 = resolveLong(value);
            if (var4 >= 0L) {
               var7.maxUnitPrice = var4;
               AutoBuyConfig.save();
            }
         } else {
            if (target == OBJECT) {
               text2 = value;
            } else if (target == OBJECT2) {
               text3 = value;
            } else if (target == OBJECT3) {
               text4 = value;
            } else if (target == OBJECT4) {
               text5 = value;
            } else if (target == OBJECT5) {
               long var3 = resolveLong(value);
               if (var3 >= 0L) {
                  AutoBuyConfig.cfg().minPrice = var3;
                  AutoBuyConfig.save();
               }
            } else if (target == OBJECT7) {
               long var6 = resolveLong(value);
               if (var6 >= 0L) {
                  AutoBuyConfig.cfg().autoPayKeepBalance = var6;
                  AutoBuyConfig.save();
               }
            } else if (target == OBJECT6) {
               AutoBuyConfig.cfg().autoPayNick = value.trim();
               AutoBuyConfig.save();
            } else if (target == OBJECT8) {
               text6 = value;
               autoSetKit.name = value;
            } else if (target == OBJECT9) {
               text7 = value;
            }
         }
      }
   }

   public static void live(Object target, String value) {
      if (!ModernBotsPage.commit(target, value)) {
         if (target == OBJECT4) {
            text5 = value;
         } else if (target == OBJECT) {
            text2 = value;
         } else if (target == OBJECT2) {
            text3 = value;
         } else if (target == OBJECT3) {
            text4 = value;
         } else if (target == OBJECT8) {
            text6 = value;
            autoSetKit.name = value;
         } else if (target == OBJECT9) {
            text7 = value;
         }
      }
   }
}
