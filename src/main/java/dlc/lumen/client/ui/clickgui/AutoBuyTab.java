package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.autobuy.AutoBuyConfig;
import dlc.lumen.client.autobuy.ItemEntry;
import dlc.lumen.client.modules.impl.render.ClickGuiTheme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class AutoBuyTab {
   private static final String[] STRING = new String[]{"Items", "Settings", "Utils"};
   private static int index = 0;
   private static final Object OBJECT = "MINPRICE";
   private static final Object OBJECT2 = "PARSEPCT";
   private static final Object OBJECT3 = "ADDITEM";
   private static final Object OBJECT4 = "BAN";
   private static final Object OBJECT5 = "APAY_KEEP";
   private static final Object OBJECT6 = "APAY_INT";
   private static final Object OBJECT7 = "APAY_NICK";
   private static Object object = null;
   private static String text2 = "";
   private static float volume = 0.0F;
   private static final float VOLUME = 2.0F;
   private static final float VOLUME2 = 3.0F;
   private static final float VOLUME3 = 14.0F;
   private static final float VOLUME4 = 16.0F;
   private static final float VOLUME5 = 20.0F;
   private static final float VOLUME6 = 18.0F;
   private static final float VOLUME7 = 16.0F;
   private static final float VOLUME8 = 22.0F;

   private AutoBuyTab() {
   }

   private static List<AutoBuyTab.El> helper(float moduleX, float moduleY, float moduleWidth, float scrollOffset) {
      ArrayList var4 = new ArrayList();
      AutoBuyConfig var5 = AutoBuyConfig.cfg();
      float var6 = moduleX + 2.0F;
      float var7 = moduleWidth - 4.0F;
      float var8 = moduleX + moduleWidth - 2.0F;
      float var9 = (var7 - 4.0F) / 2.0F;
      float var10 = 26.0F;
      switch (index) {
         case 0:
            for (ItemEntry var29 : var5.entries) {
               float var31 = moduleY + var10 - scrollOffset;
               float var32 = 62.0F;
               float var33 = var8 - var32;
               float var34 = var6 + 42.0F;
               var4.add(new AutoBuyTab.El("ITEM_BG", var6, var31, var7, 20.0F, var29));
               var4.add(new AutoBuyTab.El("ITEM_ICON", var6, var31, 18.0F, 20.0F, var29));
               var4.add(new AutoBuyTab.El("ITEM_T", var6 + 20.0F, var31, 18.0F, 20.0F, var29));
               var4.add(new AutoBuyTab.El("ITEM_LABEL", var34, var31, var33 - var34 - 4.0F, 20.0F, var29));
               var4.add(new AutoBuyTab.El("ITEM_PRICE", var33, var31, var32, 20.0F, var29));
               var10 += 23.0F;
            }

            float var28 = 64.0F;
            float var30 = var7 - var28 - 4.0F;
            var4.add(new AutoBuyTab.El("ADDITEM_F", var6, moduleY + var10 - scrollOffset, var30, 18.0F, null));
            var4.add(new AutoBuyTab.El("ADDITEM_B", var6 + var30 + 4.0F, moduleY + var10 - scrollOffset, var28, 18.0F, null));
            var10 += 21.0F;
            break;
         case 1:
            var4.add(new AutoBuyTab.El("AUTOREFRESH", var6, moduleY + var10 - scrollOffset, var7, 16.0F, null));
            var10 += 19.0F;
            var4.add(new AutoBuyTab.El("MINPRICE_T", var6, moduleY + var10 - scrollOffset, var9, 16.0F, null));
            var4.add(new AutoBuyTab.El("MINPRICE_F", var6 + var9 + 4.0F, moduleY + var10 - scrollOffset, var9, 16.0F, null));
            var10 += 19.0F;
            var4.add(new AutoBuyTab.El("LABEL", var6, moduleY + var10 - scrollOffset, var9, 16.0F, "Парсинг, %"));
            var4.add(new AutoBuyTab.El("PARSE_F", var6 + var9 + 4.0F, moduleY + var10 - scrollOffset, var9, 16.0F, null));
            var10 += 19.0F;
            var4.add(new AutoBuyTab.El("AUTOSELL", var6, moduleY + var10 - scrollOffset, var7, 16.0F, null));
            var10 += 19.0F;
            break;
         case 2:
            var4.add(new AutoBuyTab.El("SECTION", var6, moduleY + var10 - scrollOffset, var7, 14.0F, "AutoPay"));
            var10 += 14.0F;
            var4.add(new AutoBuyTab.El("AUTOPAY", var6, moduleY + var10 - scrollOffset, var7, 16.0F, null));
            var10 += 19.0F;
            var4.add(new AutoBuyTab.El("LABEL", var6, moduleY + var10 - scrollOffset, var9, 16.0F, "Удерживать баланс"));
            var4.add(new AutoBuyTab.El("APAY_KEEP_F", var6 + var9 + 4.0F, moduleY + var10 - scrollOffset, var9, 16.0F, null));
            var10 += 19.0F;
            var4.add(new AutoBuyTab.El("LABEL", var6, moduleY + var10 - scrollOffset, var9, 16.0F, "Интервал, сек"));
            var4.add(new AutoBuyTab.El("APAY_INT_F", var6 + var9 + 4.0F, moduleY + var10 - scrollOffset, var9, 16.0F, null));
            var10 += 19.0F;
            var4.add(new AutoBuyTab.El("LABEL", var6, moduleY + var10 - scrollOffset, var9, 16.0F, "Ник твинка"));
            var4.add(new AutoBuyTab.El("APAY_NICK_F", var6 + var9 + 4.0F, moduleY + var10 - scrollOffset, var9, 16.0F, null));
            var10 += 23.0F;
            var4.add(new AutoBuyTab.El("SECTION", var6, moduleY + var10 - scrollOffset, var7, 14.0F, "Бан-лист продавцов"));
            var10 += 14.0F;
            float var11 = 64.0F;
            float var12 = var7 - var11 - 4.0F;
            var4.add(new AutoBuyTab.El("BAN_F", var6, moduleY + var10 - scrollOffset, var12, 18.0F, null));
            var4.add(new AutoBuyTab.El("BAN_B", var6 + var12 + 4.0F, moduleY + var10 - scrollOffset, var11, 18.0F, null));
            var10 += 21.0F;

            for (String var14 : var5.banList) {
               float var15 = moduleY + var10 - scrollOffset;
               float var16 = var8 - 18.0F;
               var4.add(new AutoBuyTab.El("BAN_LABEL", var6, var15, var16 - var6 - 4.0F, 16.0F, var14));
               var4.add(new AutoBuyTab.El("BAN_DEL", var16, var15, 18.0F, 16.0F, var14));
               var10 += 19.0F;
            }
      }

      var10 += 6.0F;
      volume = var10;
      return var4;
   }

   public static float calcTotalHeight(float moduleWidth) {
      helper(0.0F, 0.0F, moduleWidth, 0.0F);
      return volume;
   }

   public static void render(
      DrawContext context,
      float moduleX,
      float moduleY,
      float moduleWidth,
      float visibleHeight,
      float scrollOffset,
      int themeColor,
      float alpha,
      int mouseX,
      int mouseY
   ) {
      MatrixStack var10 = context.getMatrices();
      AutoBuyConfig var11 = AutoBuyConfig.cfg();
      float var12 = moduleY + 22.0F;
      float var13 = moduleY + visibleHeight;
      float[] var14 = helper2(0, moduleX, moduleY, moduleWidth);
      float[] var15 = helper2(STRING.length - 1, moduleX, moduleY, moduleWidth);
      float var16 = var14[0];
      float var17 = var15[0] + var15[2];
      float var18 = var17 - var16;
      RenderUtils.drawLiquidGlass(
         var10,
         var16 - 6.0F + 2.0F,
         moduleY - 1.0F,
         var18 + 12.0F - 4.0F,
         14.0F,
         4.0F,
         4.0F,
         4.0F,
         4.0F,
         ColorUtils.rgba(10, 10, 10, 255),
         1.0F,
         1.35F,
         ColorUtils.rgba(255, 255, 255, 255),
         0.98F,
         false,
         0.32F,
         0.22F,
         0.8F,
         false
      );
      Font var19 = Fonts.getFont("suisse", 11);

      for (int var20 = 0; var20 < STRING.length; var20++) {
         float[] var21 = helper2(var20, moduleX, moduleY, moduleWidth);
         boolean var22 = var20 == index;
         boolean var23 = HoveringUtils.isHovered(mouseX, mouseY, var21[0], var21[1], var21[2], var21[3]);
         float var24 = ClickGuiTheme.radius(3.0F);
         if (var22) {
            RenderUtils.drawGradientRect(
               var10,
               var21[0],
               var21[1],
               var21[2],
               var21[3],
               var24,
               ColorUtils.applyAlpha(themeColor, 0.95F * alpha),
               ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.45F), 0.95F * alpha)
            );
         } else {
            RenderUtils.drawRoundedRect(
               var10, var21[0], var21[1], var21[2], var21[3], var24, ColorUtils.applyAlpha(ColorUtils.rgba(35, 35, 35, 255), (var23 ? 0.95F : 0.55F) * alpha)
            );
         }

         float var25 = var19.getWidth(STRING[var20]);
         int var26 = var22 ? ColorUtils.applyAlpha(-1, alpha) : ColorUtils.applyAlpha(ColorUtils.rgba(210, 214, 226, 235), (var23 ? 1.0F : 0.8F) * alpha);
         var19.draw(var10, STRING[var20], var21[0] + var21[2] / 2.0F - var25 / 2.0F, var21[1] + var21[3] / 2.0F - 1.5F, var26);
      }

      for (AutoBuyTab.El var28 : helper(moduleX, moduleY, moduleWidth, scrollOffset)) {
         if (!(var28.y() + var28.h() < var12) && !(var28.y() > var13)) {
            boolean var29 = HoveringUtils.isHovered(mouseX, mouseY, var28.x(), var28.y(), var28.w(), var28.h());
            switch (var28.type()) {
               case "AUTOREFRESH":
                  helper3(var10, var28, "Автообновление", var11.autoRefresh, themeColor, alpha);
                  break;
               case "MINPRICE_T":
                  helper3(var10, var28, "Мин. цена", var11.minPriceEnabled, themeColor, alpha);
                  break;
               case "AUTOSELL":
                  helper3(var10, var28, "AutoSell", var11.autoSell, themeColor, alpha);
                  break;
               case "AUTOPAY":
                  helper3(var10, var28, "AutoPay", var11.autoPay, themeColor, alpha);
                  break;
               case "MINPRICE_F":
                  helper5(
                     var10,
                     var28,
                     object == OBJECT ? text2 : helper16(var11.minPrice),
                     object == OBJECT,
                     "мин",
                     alpha
                  );
                  break;
               case "PARSE_F":
                  helper5(
                     var10,
                     var28,
                     object == OBJECT2 ? text2 : String.valueOf(var11.parsePercent),
                     object == OBJECT2,
                     "%",
                     alpha
                  );
                  break;
               case "APAY_KEEP_F":
                  helper5(
                     var10,
                     var28,
                     object == OBJECT5 ? text2 : helper16(var11.autoPayKeepBalance),
                     object == OBJECT5,
                     "баланс",
                     alpha
                  );
                  break;
               case "APAY_INT_F":
                  helper5(
                     var10,
                     var28,
                     object == OBJECT6 ? text2 : String.valueOf(var11.autoPayIntervalSeconds),
                     object == OBJECT6,
                     "сек",
                     alpha
                  );
                  break;
               case "APAY_NICK_F":
                  helper5(
                     var10,
                     var28,
                     object == OBJECT7 ? text2 : var11.autoPayNick,
                     object == OBJECT7,
                     "ник твинка",
                     alpha
                  );
                  break;
               case "LABEL":
                  helper4(var10, var28, (String)var28.ref(), alpha);
                  break;
               case "SECTION":
                  helper7(var10, var28, (String)var28.ref(), alpha);
                  break;
               case "ITEM_BG":
                  newItemEntry(var10, var28, alpha, ((ItemEntry)var28.ref()).enabled, themeColor);
                  break;
               case "ITEM_ICON":
                  newItemEntry3(context, var28, (ItemEntry)var28.ref());
                  break;
               case "ITEM_T":
                  newItemEntry2(var10, var28.x(), var28.y() + 5.0F, ((ItemEntry)var28.ref()).enabled, themeColor, alpha);
                  break;
               case "ITEM_LABEL":
                  newItemEntry4(var10, var28, (ItemEntry)var28.ref(), alpha);
                  break;
               case "ITEM_PRICE":
                  ItemEntry var32 = (ItemEntry)var28.ref();
                  boolean var33 = object == var32;
                  helper5(var10, var28, var33 ? text2 : helper16(var32.maxUnitPrice), var33, "цена", alpha);
                  break;
               case "ADDITEM_F":
                  helper5(
                     var10,
                     var28,
                     object == OBJECT3 ? text2 : "",
                     object == OBJECT3,
                     "айди или айди|название",
                     alpha
                  );
                  break;
               case "ADDITEM_B":
                  helper6(var10, var28, "Добавить", var29, themeColor, alpha);
                  break;
               case "BAN_F":
                  helper5(
                     var10, var28, object == OBJECT4 ? text2 : "", object == OBJECT4, "ник продавца", alpha
                  );
                  break;
               case "BAN_B":
                  helper6(var10, var28, "Добавить", var29, themeColor, alpha);
                  break;
               case "BAN_LABEL":
                  helper9(var10, var28, (String)var28.ref(), alpha);
                  break;
               case "BAN_DEL":
                  helper6(var10, var28, "x", var29, ColorUtils.rgba(200, 70, 70, 255), alpha);
            }
         }
      }
   }

   private static float[] helper2(int i, float moduleX, float moduleY, float moduleWidth) {
      float var4 = 40.0F;
      float var5 = 8.0F;
      float var6 = 4.0F;
      float var7 = STRING.length * var4 + (STRING.length - 1) * var6;
      float var8 = moduleX + (moduleWidth - var7) / 2.0F;
      float var9 = var8 + i * (var4 + var6);
      float var10 = moduleY + 1.5F;
      return new float[]{var9, var10, var4, var5};
   }

   private static void newItemEntry(MatrixStack m, AutoBuyTab.El el, float alpha, boolean accentStrip, int theme) {
      float var5 = ClickGuiTheme.opacity();
      float var6 = ClickGuiTheme.radius(5.0F);
      if (accentStrip) {
      }
   }

   private static void helper3(MatrixStack m, AutoBuyTab.El el, String label, boolean on, int theme, float alpha) {
      newItemEntry(m, el, alpha, on, theme);
      Fonts.getFont("suisse", 11)
         .draw(
            m, label, el.x() + (on ? 10.0F : 8.0F), el.y() + el.h() / 2.0F - 1.5F, ColorUtils.applyAlpha(on ? -1 : ColorUtils.rgba(208, 212, 224, 235), alpha)
         );
      newItemEntry2(m, el.x() + el.w() - 28.0F, el.y() + el.h() / 2.0F - 5.0F, on, ColorUtils.clientAccent(), alpha);
   }

   private static void newItemEntry2(MatrixStack m, float x, float y, boolean on, int theme, float alpha) {
      float var6 = 20.0F;
      float var7 = 10.0F;
      if (on) {
         RenderUtils.drawGradientRect(
            m, x, y, var6, var7, 4.0F, ColorUtils.applyAlpha(theme, alpha), ColorUtils.applyAlpha(ColorUtils.darken(ColorUtils.clientAccent(), 0.4F), alpha)
         );
      } else {
         RenderUtils.drawRoundedRect(m, x, y, var6, var7, 4.0F, ColorUtils.applyAlpha(ColorUtils.rgba(54, 57, 66, 220), alpha));
      }

      float var8 = var7 - 2.0F;
      float var9 = x + 1.0F + (var6 - var8 - 2.0F) * (on ? 1.0F : 0.0F);
      RenderUtils.drawRoundedRect(m, var9, y + 1.0F, var8, var8, var8 / 2.0F, ColorUtils.applyAlpha(-1, alpha));
   }

   private static void helper4(MatrixStack m, AutoBuyTab.El el, String label, float alpha) {
      newItemEntry(m, el, alpha, false, 0);
      Fonts.getFont("suisse", 11)
         .draw(m, label, el.x() + 8.0F, el.y() + el.h() / 2.0F - 1.5F, ColorUtils.applyAlpha(ColorUtils.rgba(208, 212, 224, 235), alpha));
   }

   private static void helper5(MatrixStack m, AutoBuyTab.El el, String text, boolean focused, String placeholder, float alpha) {
      float var6 = ClickGuiTheme.opacity();
      float var7 = ClickGuiTheme.radius(4.0F);
      int var8 = ClickGuiTheme.accent();
      if (focused) {
         RenderUtils.drawRoundedRect(m, el.x() - 0.8F, el.y() + 1.2F, el.w() + 1.6F, el.h() - 2.4F, var7 + 0.8F, ColorUtils.applyAlpha(var8, 0.85F * alpha));
      }

      RenderUtils.drawRoundedRect(
         m, el.x(), el.y() + 2.0F, el.w(), el.h() - 4.0F, var7, ColorUtils.applyAlpha(ColorUtils.rgba(15, 16, 21, (int)(210.0F * var6)), alpha)
      );
      Font var9 = Fonts.getFont("suisse", 11);
      String var10 = text == null ? "" : text;
      int var11;
      if (var10.isEmpty() && !focused) {
         var10 = placeholder;
         var11 = ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 90), alpha);
      } else {
         var11 = ColorUtils.applyAlpha(-1, alpha);
      }

      if (focused) {
         var10 = var10 + "_";
      }

      var10 = helper17(var9, var10, el.w() - 10.0F);
      var9.draw(m, var10, el.x() + 6.0F, el.y() + el.h() / 2.0F - 1.5F, var11);
   }

   private static void helper6(MatrixStack m, AutoBuyTab.El el, String label, boolean hovered, int color, float alpha) {
      float var6 = ClickGuiTheme.radius(4.0F);
      if (hovered) {
         RenderUtils.drawRoundedRect(m, el.x() - 1.0F, el.y() + 1.0F, el.w() + 2.0F, el.h() - 2.0F, var6 + 1.0F, ColorUtils.applyAlpha(color, 0.3F * alpha));
      }

      RenderUtils.drawGradientRect(
         m,
         el.x(),
         el.y() + 2.0F,
         el.w(),
         el.h() - 4.0F,
         var6,
         ColorUtils.applyAlpha(color, (hovered ? 1.0F : 0.85F) * alpha),
         ColorUtils.applyAlpha(ColorUtils.darken(color, 0.45F), (hovered ? 1.0F : 0.85F) * alpha)
      );
      Font var7 = Fonts.getFont("suisse", 11);
      float var8 = var7.getWidth(label);
      var7.draw(m, label, el.x() + el.w() / 2.0F - var8 / 2.0F, el.y() + el.h() / 2.0F - 1.5F, ColorUtils.applyAlpha(-1, alpha));
   }

   private static void helper7(MatrixStack m, AutoBuyTab.El el, String label, float alpha) {
      int var4 = ClickGuiTheme.accent();
      Font var5 = Fonts.getFont("suisse", 10);
      var5.draw(m, label, el.x() + 2.0F, el.y() + 5.0F, ColorUtils.applyAlpha(ColorUtils.clientAccent(), 0.95F * alpha));
      float var6 = el.x() + 4.0F + var5.getWidth(label) + 6.0F;
      float var7 = el.x() + el.w() - var6;
      if (var7 > 4.0F) {
         RenderUtils.drawGradientRect(
            m,
            var6,
            el.y() + 5.0F,
            var7,
            1.0F,
            0.0F,
            ColorUtils.applyAlpha(ColorUtils.clientAccent(), 0.45F * alpha),
            ColorUtils.applyAlpha(ColorUtils.clientAccent(), 0.0F)
         );
      }
   }

   private static void newItemEntry3(DrawContext context, AutoBuyTab.El el, ItemEntry e) {
      ItemStack var3 = helper8(e.itemId);
      if (!var3.isEmpty()) {
         context.drawItem(var3, (int)el.x(), (int)(el.y() + el.h() / 2.0F - 8.0F));
      }
   }

   private static ItemStack helper8(String itemId) {
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

   private static void newItemEntry4(MatrixStack m, AutoBuyTab.El el, ItemEntry e, float alpha) {
      Font var4 = Fonts.getFont("suisse", 11);
      String var5 = helper17(var4, e.label(), el.w());
      var4.draw(m, var5, el.x(), el.y() + el.h() / 2.0F - 3.5F + 2.0F, ColorUtils.applyAlpha(e.enabled ? -1 : ColorUtils.rgba(255, 255, 255, 150), alpha));
   }

   private static void helper9(MatrixStack m, AutoBuyTab.El el, String nick, float alpha) {
      newItemEntry(m, el, alpha, false, 0);
      Font var4 = Fonts.getFont("suisse", 11);
      var4.draw(m, helper17(var4, nick, el.w() - 8.0F), el.x() + 6.0F, el.y() + el.h() / 2.0F - 1.5F, ColorUtils.applyAlpha(-1, alpha));
   }

   public static boolean mouseClicked(
      ClickGuiState state, float moduleX, float moduleY, float moduleWidth, float scrollOffset, double mouseX, double mouseY, int button
   ) {
      if (button != 0) {
         return false;
      }

      for (int var10 = 0; var10 < STRING.length; var10++) {
         float[] var11 = helper2(var10, moduleX, moduleY, moduleWidth);
         if (HoveringUtils.isHovered(mouseX, mouseY, var11[0], var11[1], var11[2], var11[3])) {
            if (index != var10) {
               index = var10;
               if (state != null) {
                  state.setTargetScrollValue(0.0F);
               }
            }

            clearFocus();
            return true;
         }
      }

      if (mouseY < moduleY + 22.0F) {
         clearFocus();
         return true;
      }

      AutoBuyConfig var16 = AutoBuyConfig.cfg();

      for (AutoBuyTab.El var12 : helper(moduleX, moduleY, moduleWidth, scrollOffset)) {
         if (HoveringUtils.isHovered(mouseX, mouseY, var12.x(), var12.y(), var12.w(), var12.h())) {
            switch (var12.type()) {
               case "AUTOREFRESH":
                  var16.autoRefresh = !var16.autoRefresh;
                  AutoBuyConfig.save();
                  clearFocus();
                  return true;
               case "MINPRICE_T":
                  var16.minPriceEnabled = !var16.minPriceEnabled;
                  AutoBuyConfig.save();
                  clearFocus();
                  return true;
               case "AUTOSELL":
                  var16.autoSell = !var16.autoSell;
                  AutoBuyConfig.save();
                  clearFocus();
                  return true;
               case "AUTOPAY":
                  var16.autoPay = !var16.autoPay;
                  AutoBuyConfig.save();
                  clearFocus();
                  return true;
               case "MINPRICE_F":
                  helper10(OBJECT, String.valueOf(var16.minPrice));
                  return true;
               case "PARSE_F":
                  helper10(OBJECT2, String.valueOf(var16.parsePercent));
                  return true;
               case "APAY_KEEP_F":
                  helper10(OBJECT5, String.valueOf(var16.autoPayKeepBalance));
                  return true;
               case "APAY_INT_F":
                  helper10(OBJECT6, String.valueOf(var16.autoPayIntervalSeconds));
                  return true;
               case "APAY_NICK_F":
                  helper10(OBJECT7, var16.autoPayNick == null ? "" : var16.autoPayNick);
                  return true;
               case "ITEM_T":
                  ItemEntry var18 = (ItemEntry)var12.ref();
                  var18.enabled = !var18.enabled;
                  AutoBuyConfig.save();
                  clearFocus();
                  return true;
               case "ITEM_PRICE":
                  ItemEntry var15 = (ItemEntry)var12.ref();
                  helper10(var15, var15.maxUnitPrice > 0L ? String.valueOf(var15.maxUnitPrice) : "");
                  return true;
               case "ADDITEM_F":
                  helper10(OBJECT3, "");
                  return true;
               case "ADDITEM_B":
                  helper13();
                  return true;
               case "BAN_F":
                  helper10(OBJECT4, "");
                  return true;
               case "BAN_B":
                  helper14();
                  return true;
               case "BAN_DEL":
                  var16.removeBan((String)var12.ref());
                  clearFocus();
                  return true;
            }
         }
      }

      clearFocus();
      return false;
   }

   public static boolean hasFocus() {
      return object != null;
   }

   public static boolean charTyped(char chr) {
      if (object == null) {
         return false;
      } else if (Character.isISOControl(chr)) {
         return true;
      } else {
         boolean var1 = object instanceof ItemEntry
            || object == OBJECT
            || object == OBJECT2
            || object == OBJECT5
            || object == OBJECT6;
         if (!var1 || chr >= '0' && chr <= '9') {
            text2 = text2 + chr;
            helper11();
            return true;
         } else {
            return true;
         }
      }
   }

   public static boolean keyPressed(int keyCode) {
      if (object == null) {
         return false;
      }

      if (keyCode == 259) {
         if (!text2.isEmpty()) {
            text2 = text2.substring(0, text2.length() - 1);
            helper11();
         }

         return true;
      } else {
         if (keyCode != 257 && keyCode != 335 && keyCode != 256) {
            return true;
         }

         helper12();
         return true;
      }
   }

   private static void helper10(Object target, String initial) {
      object = target;
      text2 = initial == null ? "" : initial;
   }

   public static void clearFocus() {
      if (object != null) {
         helper12();
      }
   }

   private static void helper11() {
      AutoBuyConfig var0 = AutoBuyConfig.cfg();
      if (object instanceof ItemEntry var1) {
         var1.maxUnitPrice = helper15(text2);
      } else if (object == OBJECT) {
         var0.minPrice = helper15(text2);
      } else if (object == OBJECT2) {
         var0.parsePercent = (int)Math.max(1L, Math.min(100L, helper15(text2)));
      } else if (object == OBJECT5) {
         var0.autoPayKeepBalance = helper15(text2);
      } else if (object == OBJECT6) {
         var0.autoPayIntervalSeconds = (int)Math.max(10L, helper15(text2));
      } else if (object == OBJECT7) {
         var0.autoPayNick = text2;
      }
   }

   private static void helper12() {
      if (object == OBJECT3) {
         helper13();
      } else if (object == OBJECT4) {
         helper14();
      } else {
         helper11();
         AutoBuyConfig.save();
         object = null;
         text2 = "";
      }
   }

   private static void helper13() {
      String var0 = text2.trim();
      object = null;
      text2 = "";
      if (!var0.isEmpty()) {
         String var2 = null;
         int var3 = var0.indexOf(124);
         String var1;
         if (var3 >= 0) {
            var1 = var0.substring(0, var3).trim();
            var2 = var0.substring(var3 + 1).trim();
            if (var2.isEmpty()) {
               var2 = null;
            }
         } else {
            var1 = var0;
         }

         if (!var1.contains(":")) {
            var1 = "minecraft:" + var1;
         }

         ItemEntry var4 = new ItemEntry(var1, var2, 0L);
         var4.enabled = true;
         AutoBuyConfig.cfg().entries.add(var4);
         AutoBuyConfig.save();
      }
   }

   private static void helper14() {
      String var0 = text2.trim();
      object = null;
      text2 = "";
      if (!var0.isEmpty()) {
         AutoBuyConfig.cfg().addBan(var0);
      }
   }

   private static long helper15(String s) {
      if (s != null && !s.isEmpty()) {
         try {
            return Long.parseLong(s.replaceAll("\\D", ""));
         } catch (NumberFormatException var2) {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   private static String helper16(long v) {
      if (v <= 0L) {
         return "0";
      }

      StringBuilder var2 = new StringBuilder(Long.toString(v));

      for (int var3 = var2.length() - 3; var3 > 0; var3 -= 3) {
         var2.insert(var3, ' ');
      }

      return var2.toString();
   }

   private static String helper17(Font font, String text, float maxWidth) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F) && !(font.getWidth(text) <= maxWidth)) {
         String var3 = "…";

         for (int var4 = text.length(); var4 > 0; var4--) {
            String var5 = text.substring(0, var4) + var3;
            if (font.getWidth(var5) <= maxWidth) {
               return var5;
            }
         }

         return "";
      } else {
         return text == null ? "" : text;
      }
   }

   private record El(String type, float x, float y, float w, float h, Object ref) {

      private El(String type, float x, float y, float w, float h, Object ref) {
         this.type = type;
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.ref = ref;
      }

      public String type() {
         return this.type;
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

      public Object ref() {
         return this.ref;
      }
   }
}
