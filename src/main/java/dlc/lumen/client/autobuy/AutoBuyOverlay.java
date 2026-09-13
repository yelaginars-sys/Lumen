package dlc.lumen.client.autobuy;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class AutoBuyOverlay {
   private static final float VOLUME = 16.0F;
   private static final float VOLUME2 = 4.0F;

   private AutoBuyOverlay() {
   }

   public static boolean shouldShow(HandledScreen<?> screen) {
      if (!(screen instanceof GenericContainerScreen var1)) {
         return false;
      } else {
         if (AutoBuyEngine.isAuctionScreen(var1)) {
            return true;
         }

         if (var1.getScreenHandler().getRows() != 6) {
            return false;
         }

         String var2 = var1.getTitle().getString();
         return var2 != null && var2.toLowerCase(Locale.ROOT).contains("аукцион");
      }
   }

   public static void render(DrawContext context, int gx, int gy, int gw, int gh, int mouseX, int mouseY) {
      MatrixStack var7 = context.getMatrices();
      AutoBuyConfig var8 = AutoBuyConfig.cfg();
      boolean var9 = AutoBuyEngine.INSTANCE.parser.isActive();
      int var10 = ColorUtils.getThemeColor();
      int var11 = ColorUtils.rgba(10, 10, 10, 255);
      float var12 = (gw - 8.0F) / 3.0F;
      float var13 = gy - 16.0F - 4.0F;
      float var14 = gx;
      float var15 = gx + var12 + 4.0F;
      float var16 = gx + (var12 + 4.0F) * 2.0F;
      handleEvent(
         var7,
         var14,
         var13,
         var12,
         var8.enabled ? "AutoBuy ВКЛ" : "AutoBuy ВЫКЛ",
         var8.enabled ? ColorUtils.getThemeColor() : var11,
         HoveringUtils.isHovered(mouseX, mouseY, var14, var13, var12, 16.0)
      );
      handleEvent(
         var7,
         var15,
         var13,
         var12,
         var8.autoSell ? "AutoSell ВКЛ" : "AutoSell ВЫКЛ",
         var8.autoSell ? ColorUtils.getThemeColor() : var11,
         HoveringUtils.isHovered(mouseX, mouseY, var15, var13, var12, 16.0)
      );
      handleEvent(
         var7,
         var16,
         var13,
         var12,
         var9 ? "Парсинг СТОП" : "Парсить цены",
         var9 ? ColorUtils.getThemeColor() : var11,
         HoveringUtils.isHovered(mouseX, mouseY, var16, var13, var12, 16.0)
      );
      float var17 = (gw - 4.0F) / 2.0F;
      float var18 = gx;
      float var19 = gx + var17 + 4.0F;
      float var20 = var13 - 16.0F - 4.0F;
      handleEvent(
         var7,
         var18,
         var20,
         var17,
         var8.autoRelist ? "Релист ВКЛ" : "Релист ВЫКЛ",
         var8.autoRelist ? ColorUtils.getThemeColor() : var11,
         HoveringUtils.isHovered(mouseX, mouseY, var18, var20, var17, 16.0)
      );
      handleEvent(
         var7,
         var19,
         var20,
         var17,
         var8.autoParse ? "Автопарс ВКЛ" : "Автопарс ВЫКЛ",
         var8.autoParse ? ColorUtils.getThemeColor() : var11,
         HoveringUtils.isHovered(mouseX, mouseY, var19, var20, var17, 16.0)
      );
      if (var9) {
         Font var21 = Fonts.getFont("suisse", 9);
         String var22 = AutoBuyEngine.INSTANCE.parser.statusLine();
         var21.draw(var7, var22, gx + 4.0F, gy + gh + 4.0F + 3.0F, var10);
      }

      handleEvent2(context, gx, gy, gw, gh, var10);
   }

   private static void handleEvent(MatrixStack m, float x, float y, float w, String label, int color, boolean hover) {
      int var7 = (int)(255.0F * (hover ? 1.0F : 0.8F));
      int var8 = ColorUtils.replAlpha(color, var7);
      int var9 = ColorUtils.replAlpha(ColorUtils.clientBackground(), Math.min(var7, 160));
      int var10 = ColorUtils.replAlpha(ColorUtils.clientBackground(), Math.min(var7, 92));
      int var11 = ColorUtils.rgba(255, 255, 255, Math.min(var7, hover ? 64 : 40));
      RenderUtils.drawBlur(m, x + 0.25F, y + 0.25F, w - 0.5F, 15.5F, 6.0F, 5.0F, var8);
      RenderUtils.drawBlur(m, x + 0.25F, y + 0.25F, w - 0.5F, 15.5F, 6.0F, 5.0F, var9);
      RenderUtils.drawHudLiquidGlass(
         m,
         x - 1.0F,
         y - 1.0F,
         w + 2.0F,
         18.0F,
         6.0F,
         6.0F,
         6.0F,
         6.0F,
         ColorUtils.replAlpha(ColorUtils.rgba(255, 255, 255, 255), var7),
         1.0F,
         32.0F,
         ColorUtils.getThemeColor(),
         0.88F,
         true,
         0.0F,
         0.06F,
         2.0F,
         false
      );
      RenderUtils.drawRoundedRect(m, x + 0.25F, y + 0.25F, w - 0.5F, 15.5F, 6.0F, var10);
      RenderUtils.drawRoundedRectOutline(m, x + 0.25F, y + 0.25F, w - 0.5F, 15.5F, 6.0F, 0.45F, var11, var11, var11, var11);
      Font var12 = Fonts.getFont("suisse", 9);
      String var13 = resolveString2(var12, label, w - 4.0F);
      float var14 = var12.getWidth(var13);
      var12.draw(m, var13, x + w / 2.0F - var14 / 2.0F, y + 8.0F - 3.0F + 2.0F + 1.0F, -1);
   }

   private static void handleEvent2(DrawContext context, int gx, int gy, int gw, int gh, int theme) {
      MatrixStack var6 = context.getMatrices();
      float var7 = 172.0F;
      float var8 = gx + gw + 6.0F;
      int var9 = MinecraftClient.getInstance().getWindow().getScaledWidth();
      if (var8 + var7 > var9) {
         var8 = gx - var7 - 6.0F;
      }

      if (!(var8 < 0.0F)) {
         List var10 = AutoBuyConfig.cfg().history;
         Font var11 = Fonts.getFont("suisse", 9);
         String var12 = String.valueOf(var10.size());
         var11.draw(var6, var12, var8 + var7 - 8.0F - var11.getWidth(var12), gy + 7.0F, ColorUtils.rgba(255, 255, 255, 140));
         float var13 = 21.0F;
         float var14 = gy + 24.0F;
         int var15 = (int)Math.max(0.0F, (gh - 28.0F) / var13);
         Font var16 = Fonts.getFont("suisse", 9);
         if (var10.isEmpty()) {
            var16.draw(var6, "пусто", var8 + 8.0F, var14 + 4.0F, ColorUtils.rgba(255, 255, 255, 110));
         } else {
            int var17 = Math.min(var15, var10.size());
            float var18 = var8 + 6.0F;
            float var19 = var8 + 26.0F;
            float var20 = var8 + var7 - 8.0F;

            for (int var21 = 0; var21 < var17; var21++) {
               PurchaseRecord var22 = (PurchaseRecord)var10.get(var21);
               float var23 = var14 + var21 * var13;
               if ((var21 & 1) == 0) {
               }

               ItemStack var24 = computeItemStack(var22.itemId);
               if (!var24.isEmpty()) {
                  context.drawItem(var24, (int)var18, (int)(var23 + (var13 - 2.0F - 16.0F) / 2.0F));
               }

               int var25 = var22.success ? ColorUtils.rgba(90, 210, 110, 255) : ColorUtils.rgba(220, 90, 90, 255);
               String var26 = (var22.success ? "✓ " : "✗ ") + (var22.label == null ? "?" : var22.label);
               var16.draw(var6, resolveString2(var16, var26, var20 - var19), var19, var23 + 3.0F, var25);
               var16.draw(var6, "×" + var22.count, var19, var23 + 12.0F, ColorUtils.rgba(165, 165, 175, 255));
               String var27 = resolveString(var22.totalPrice) + "$";
               var16.draw(var6, var27, var20 - var16.getWidth(var27) - 100.0F, var23 + 12.0F, ColorUtils.rgba(215, 215, 220, 255));
            }
         }
      }
   }

   private static ItemStack computeItemStack(String itemId) {
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

   private static String resolveString(long v) {
      if (v <= 0L) {
         return "0";
      }

      StringBuilder var2 = new StringBuilder(Long.toString(v));

      for (int var3 = var2.length() - 3; var3 > 0; var3 -= 3) {
         var2.insert(var3, ' ');
      }

      return var2.toString();
   }

   private static String resolveString2(Font font, String text, float maxWidth) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F) && !(font.getWidth(text) <= maxWidth)) {
         for (int var3 = text.length(); var3 > 0; var3--) {
            String var4 = text.substring(0, var3) + "…";
            if (font.getWidth(var4) <= maxWidth) {
               return var4;
            }
         }

         return "";
      } else {
         return text == null ? "" : text;
      }
   }

   public static boolean mouseClicked(double mouseX, double mouseY, int button, int gx, int gy, int gw, int gh) {
      if (button != 0) {
         return false;
      } else {
         AutoBuyConfig var9 = AutoBuyConfig.cfg();
         float var10 = (gw - 8.0F) / 3.0F;
         float var11 = gy - 16.0F - 4.0F;
         float var12 = gx;
         float var13 = gx + var10 + 4.0F;
         float var14 = gx + (var10 + 4.0F) * 2.0F;
         if (HoveringUtils.isHovered(mouseX, mouseY, var12, var11, var10, 16.0)) {
            var9.enabled = !var9.enabled;
            AutoBuyConfig.save();
            return true;
         } else if (HoveringUtils.isHovered(mouseX, mouseY, var13, var11, var10, 16.0)) {
            var9.autoSell = !var9.autoSell;
            AutoBuyConfig.save();
            return true;
         } else if (HoveringUtils.isHovered(mouseX, mouseY, var14, var11, var10, 16.0)) {
            AutoBuyEngine.INSTANCE.parser.start();
            return true;
         } else {
            float var15 = (gw - 4.0F) / 2.0F;
            float var16 = gx;
            float var17 = gx + var15 + 4.0F;
            float var18 = var11 - 16.0F - 4.0F;
            if (HoveringUtils.isHovered(mouseX, mouseY, var16, var18, var15, 16.0)) {
               var9.autoRelist = !var9.autoRelist;
               AutoBuyConfig.save();
               return true;
            } else if (HoveringUtils.isHovered(mouseX, mouseY, var17, var18, var15, 16.0)) {
               var9.autoParse = !var9.autoParse;
               AutoBuyConfig.save();
               return true;
            } else {
               return false;
            }
         }
      }
   }
}
