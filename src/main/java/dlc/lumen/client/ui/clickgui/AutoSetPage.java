package dlc.lumen.client.ui.clickgui;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.autoset.AutoSetCatalog;
import dlc.lumen.client.autoset.AutoSetCollector;
import dlc.lumen.client.autoset.AutoSetItem;
import dlc.lumen.client.autoset.AutoSetKit;
import dlc.lumen.client.autoset.AutoSetKitStorage;
import dlc.lumen.client.autoset.AutoSetTextInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class AutoSetPage {
   private static final String[] TEXT = AutoSetCatalog.GROUP_KEYS;
   private static final String[] TEXT2 = AutoSetCatalog.GROUP_LABELS;
   private static final float VOLUME = 6.0F;
   private static final float VOLUME2 = 15.0F;
   private static final float VOLUME3 = 24.0F;
   private static final float VOLUME4 = 188.0F;
   private static final float VOLUME5 = 20.0F;
   private static final float VOLUME6 = 18.0F;
   private static final float VOLUME7 = 16.0F;
   private static final float VOLUME8 = 20.0F;
   private static final float VOLUME9 = 50.0F;
   private static final float VOLUME10 = 4.0F;
   private static final float VOLUME11 = 92.0F;
   private static AutoSetKit autoSetKit = new AutoSetKit("");
   private static int index = 0;
   private static float volume = 0.0F;
   private static float volume2 = 0.0F;
   private static float volume3 = 0.0F;
   private static final AutoSetTextInput AUTO_SET_TEXT_INPUT = new AutoSetTextInput("Поиск...", false, 40);
   private static final AutoSetTextInput AUTO_SET_TEXT_INPUT2 = new AutoSetTextInput("Имя кита", false, 32);
   private static final Map<String, AutoSetTextInput[]> STRINGS = new HashMap<>();
   private float volume4;
   private float volume5;
   private float volume6;
   private float volume7;
   private float volume8 = 1.0F;

   private void updateState(float px, float py, float pw, float ph) {
      this.volume4 = px;
      this.volume5 = py;
      this.volume6 = pw;
      this.volume7 = ph;
   }

   private int resolveInt(int color) {
      return ColorUtils.applyAlpha(color, this.volume8);
   }

   private static float helper(Font font, float top, float h) {
      return top + h / 2.0F - font.getHeight() * 0.14F;
   }

   private float helper2() {
      return this.volume5 + 15.0F + 4.0F;
   }

   private float helper3() {
      return this.volume6 - 188.0F - 6.0F;
   }

   private float helper4() {
      return this.volume4 + this.helper3() + 6.0F;
   }

   private float helper5() {
      return this.volume5 + this.volume7 - 6.0F;
   }

   private float helper6() {
      return this.helper5() - 50.0F - 4.0F - 20.0F - 4.0F - 16.0F - 4.0F - 18.0F;
   }

   private float helper7() {
      return this.helper6() + 18.0F + 4.0F;
   }

   private float helper8() {
      return this.helper7() + 16.0F + 4.0F;
   }

   private float helper9() {
      return this.helper5() - 50.0F;
   }

   private float helper10() {
      return this.helper2() + 14.0F;
   }

   private float helper11() {
      return this.helper6() - 4.0F;
   }

   private int resolveInt2() {
      return Math.max(1, (int)((this.helper3() - 12.0F) / 24.0F));
   }

   public void render(DrawContext context, float px, float py, float pw, float ph, int mouseX, int mouseY, float alpha) {
      this.updateState(px, py, pw, ph);
      this.volume8 = alpha;
      MatrixStack var9 = context.getMatrices();
      this.helper12(context, mouseX, mouseY);
      this.helper13(context, mouseX, mouseY);
      this.helper14(context, mouseX, mouseY);
      this.helper19(context, mouseX, mouseY);
      this.helper20(context, this.helper22(), mouseX, mouseY);
   }

   private void helper12(DrawContext context, int mouseX, int mouseY) {
      MatrixStack var4 = context.getMatrices();
      Font var5 = Fonts.getFont("suisse", 10);
      float var6 = this.volume4;
      float var7 = this.volume5;
      float var8 = this.volume6 - 92.0F - 8.0F;

      for (int var9 = 0; var9 < TEXT.length; var9++) {
         String var10 = TEXT2[var9];
         float var11 = var5.getWidth(var10) + 14.0F;
         if (var6 + var11 > this.volume4 + var8) {
            break;
         }

         boolean var12 = var9 == index;
         boolean var13 = HoveringUtils.isHovered(mouseX, mouseY, var6, var7, var11, 15.0);
         int var14 = var12
            ? ColorUtils.applyAlpha(ColorUtils.getThemeColor(), 0.9F * this.volume8)
            : this.resolveInt(ColorUtils.rgba(255, 255, 255, var13 ? 25 : 12));
         RenderUtils.drawRoundedRect(var4, var6, var7, var11, 15.0F, 7.5F, var14);
         int var15 = var12 ? this.resolveInt(-1) : this.resolveInt(ColorUtils.rgba(255, 255, 255, var13 ? 200 : 130));
         var5.draw(var4, var10, var6 + 7.0F, helper(var5, var7, 15.0F), var15);
         var6 += var11 + 4.0F;
      }

      AUTO_SET_TEXT_INPUT.setBounds(this.volume4 + this.volume6 - 92.0F, this.volume5, 92.0F, 15.0F);
      AUTO_SET_TEXT_INPUT.render(var4, mouseX, mouseY);
   }

   private void helper13(DrawContext context, int mouseX, int mouseY) {
      MatrixStack var4 = context.getMatrices();
      float var5 = this.volume4;
      float var6 = this.helper2();
      float var7 = this.helper3();
      float var8 = this.helper5() - var6;
      RenderUtils.drawRoundedRect(var4, var5, var6, var7, var8, 6.0F, this.resolveInt(ColorUtils.rgba(0, 0, 0, 100)));
      List var9 = this.helper22();
      int var10 = this.resolveInt2();
      int var11 = (int)Math.ceil((float)var9.size() / var10);
      float var12 = var11 * 24.0F;
      float var13 = var8 - 12.0F;
      volume = helper34(volume, 0.0F, Math.max(0.0F, var12 - var13));
      int var14 = (int)(var5 + 6.0F);
      int var15 = (int)(var6 + 6.0F);
      int var16 = (int)(var5 + var7 - 6.0F);
      int var17 = (int)(var6 + var8 - 6.0F);
      context.enableScissor(var14, var15, var16, var17);

      for (int var18 = 0; var18 < var9.size(); var18++) {
         int var19 = var18 % var10;
         int var20 = var18 / var10;
         float var21 = var5 + 6.0F + var19 * 24.0F;
         float var22 = var6 + 6.0F + var20 * 24.0F - volume;
         if (!(var22 + 24.0F < var15) && !(var22 > var17)) {
            AutoSetItem var23 = (AutoSetItem)var9.get(var18);
            boolean var24 = HoveringUtils.isHovered(mouseX, mouseY, var21, var22, 21.0, 21.0);
            int var25 = this.helper24(var23);
            int var26 = var25 > 0
               ? ColorUtils.applyAlpha(ColorUtils.getThemeColor(), (var24 ? 0.55F : 0.35F) * this.volume8)
               : this.resolveInt(ColorUtils.rgba(255, 255, 255, var24 ? 30 : 10));
            RenderUtils.drawRoundedRect(var4, var21, var22, 21.0F, 21.0F, 4.0F, var26);
            ItemStack var27 = helper32(var23);
            if (!var27.isEmpty()) {
               context.drawItem(var27, (int)(var21 + 2.5F), (int)(var22 + 2.5F));
            }
         }
      }

      context.disableScissor();
      this.helper21(var4, var5 + var7 - 3.0F, var6 + 6.0F, var13, var12, volume);
   }

   private void helper14(DrawContext context, int mouseX, int mouseY) {
      MatrixStack var4 = context.getMatrices();
      float var5 = this.helper4();
      float var6 = 188.0F;
      float var7 = this.helper2();
      Font var8 = Fonts.getFont("suisse", 10);
      var8.draw(var4, "Текущий кит", var5 + 2.0F, helper(var8, var7, 12.0F), this.resolveInt(-1));
      Font var9 = Fonts.getFont("suisse", 9);
      String var10 = autoSetKit.total() + " шт.";
      var9.draw(
         var4,
         var10,
         var5 + var6 - var9.getWidth(var10) - 2.0F,
         helper(var9, var7, 12.0F),
         this.resolveInt(ColorUtils.rgba(255, 255, 255, 130))
      );
      float var11 = this.helper10();
      float var12 = this.helper11();
      RenderUtils.drawRoundedRect(var4, var5, var11, var6, var12 - var11, 6.0F, this.resolveInt(ColorUtils.rgba(0, 0, 0, 100)));
      float var13 = var12 - var11 - 6.0F;
      float var14 = autoSetKit.entries.size() * 20.0F;
      volume2 = helper34(volume2, 0.0F, Math.max(0.0F, var14 - var13));
      context.enableScissor((int)var5, (int)(var11 + 3.0F), (int)(var5 + var6), (int)(var12 - 3.0F));
      if (autoSetKit.entries.isEmpty()) {
         Font var15 = Fonts.getFont("suisse", 9);
         var15.draw(var4, "Клик по каталогу добавляет", var5 + 6.0F, var11 + 8.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 90)));
         var15.draw(var4, "ЛКМ +1 · ПКМ −1", var5 + 6.0F, var11 + 20.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 60)));
      }

      ArrayList var19 = new ArrayList<>(autoSetKit.entries);

      for (int var16 = 0; var16 < var19.size(); var16++) {
         AutoSetKit.Entry var17 = (AutoSetKit.Entry)var19.get(var16);
         float var18 = var11 + 3.0F + var16 * 20.0F - volume2;
         if (!(var18 + 20.0F < var11) && !(var18 > var12)) {
            this.helper15(context, var17, var5, var18, var6, mouseX, mouseY);
         }
      }

      context.disableScissor();
      this.helper21(var4, var5 + var6 - 3.0F, var11 + 3.0F, var13, var14, volume2);
      this.helper17(context, mouseX, mouseY);
   }

   private void helper15(DrawContext context, AutoSetKit.Entry e, float x, float ry, float w, int mouseX, int mouseY) {
      MatrixStack var8 = context.getMatrices();
      AutoSetItem var9 = AutoSetKit.resolve(e);
      Font var10 = Fonts.getFont("suisse", 10);
      float var11 = x + 4.0F;
      float var12 = w - 8.0F;
      float var13 = var11 + var12;
      boolean var14 = HoveringUtils.isHovered(mouseX, mouseY, var11, ry, var12, 18.0);
      if (var14) {
         RenderUtils.drawRoundedRect(var8, var11, ry, var12, 18.0F, 3.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 12)));
      }

      ItemStack var15 = var9 == null ? ItemStack.EMPTY : helper32(var9);
      if (!var15.isEmpty()) {
         var8.push();
         var8.scale(0.72F, 0.72F, 1.0F);
         context.drawItem(var15, (int)((var11 + 1.0F) / 0.72F), (int)((ry + 3.5F) / 0.72F));
         var8.pop();
      }

      float var16 = var13 - 11.0F;
      float var17 = var16 - 2.0F - 22.0F;
      float var18 = var17 - 2.0F - 22.0F;
      float var19 = var18 - 2.0F - 42.0F;
      float var20 = var19 - (var11 + 16.0F) - 4.0F;
      String var21 = var9 == null ? "?" : var9.name();
      var10.draw(
         var8,
         helper33(var10, var21, var20),
         var11 + 16.0F,
         helper(var10, ry, 18.0F),
         var9 == null ? this.resolveInt(ColorUtils.rgba(230, 90, 90, 230)) : this.resolveInt(ColorUtils.rgba(255, 255, 255, 220))
      );
      float var22 = ry + 2.5F;
      float var23 = 13.0F;
      AutoSetTextInput[] var24 = this.helper16(e);
      var24[0].setBounds(var19, var22, 42.0F, var23);
      var24[1].setBounds(var18, var22, 22.0F, var23);
      var24[2].setBounds(var17, var22, 22.0F, var23);
      var24[0].render(var8, mouseX, mouseY);
      var24[1].render(var8, mouseX, mouseY);
      var24[2].render(var8, mouseX, mouseY);
      boolean var25 = HoveringUtils.isHovered(mouseX, mouseY, var16, var22, 11.0, var23);
      var10.draw(
         var8,
         "×",
         var16 + 5.5F - var10.getWidth("×") / 2.0F,
         helper(var10, var22, var23),
         var25 ? this.resolveInt(ColorUtils.rgba(230, 90, 90, 255)) : this.resolveInt(ColorUtils.rgba(255, 255, 255, 120))
      );
   }

   private AutoSetTextInput[] helper16(AutoSetKit.Entry e) {
      return STRINGS.computeIfAbsent(e.key, k -> {
         AutoSetTextInput var2 = new AutoSetTextInput("цена", true, 12);
         AutoSetTextInput var3 = new AutoSetTextInput("40+", true, 3);
         AutoSetTextInput var4 = new AutoSetTextInput("шт", true, 4);
         var2.setText(e.maxUnitPrice > 0L ? String.valueOf(e.maxUnitPrice) : "");
         var3.setText(e.minDurabilityPercent > 0 ? String.valueOf(e.minDurabilityPercent) : "");
         var4.setText(e.count > 0 ? String.valueOf(e.count) : "");
         var2.onChange(v -> e.maxUnitPrice = var2.asLong());
         var3.onChange(v -> e.minDurabilityPercent = Math.min(100, var3.asInt()));
         var4.onChange(v -> e.count = Math.max(0, var4.asInt()));
         return new AutoSetTextInput[]{var2, var3, var4};
      });
   }

   private void helper17(DrawContext context, int mouseX, int mouseY) {
      MatrixStack var4 = context.getMatrices();
      float var5 = this.helper4();
      float var6 = 188.0F;
      AUTO_SET_TEXT_INPUT2.setBounds(var5, this.helper6(), var6, 18.0F);
      AUTO_SET_TEXT_INPUT2.render(var4, mouseX, mouseY);
      float var7 = this.helper7();
      float var8 = (var6 - 4.0F) / 2.0F;
      this.helper18(context, var5, var7, var8, "Сохранить", mouseX, mouseY);
      this.helper18(context, var5 + var8 + 4.0F, var7, var8, "Очистить", mouseX, mouseY);
      AutoSetCollector var9 = AutoSetCollector.INSTANCE;
      Font var10 = Fonts.getFont("suisse", 9);
      String var11 = var9.isRunning() ? helper33(var10, "Стоп · " + var9.statusLine(), var6 - 12.0F) : "Собрать сет";
      boolean var12 = var9.isRunning() || !autoSetKit.isEmpty();
      boolean var13 = var12 && HoveringUtils.isHovered(mouseX, mouseY, var5, this.helper8(), var6, 20.0);
      int var14 = var9.isRunning() ? ColorUtils.rgba(180, 60, 60, 255) : ColorUtils.getThemeColor();
      int var15 = var12
         ? ColorUtils.applyAlpha(var14, (var13 ? 1.0F : 0.9F) * this.volume8)
         : this.resolveInt(ColorUtils.rgba(255, 255, 255, 12));
      RenderUtils.drawRoundedRect(var4, var5, this.helper8(), var6, 20.0F, 5.0F, var15);
      Font var16 = Fonts.getFont("suisse", 10);
      var16.draw(
         var4,
         var11,
         var5 + var6 / 2.0F - var16.getWidth(var11) / 2.0F,
         helper(var16, this.helper8(), 20.0F),
         var12 ? this.resolveInt(-1) : this.resolveInt(ColorUtils.rgba(255, 255, 255, 90))
      );
   }

   private void helper18(DrawContext context, float x, float y, float w, String label, int mouseX, int mouseY) {
      MatrixStack var8 = context.getMatrices();
      boolean var9 = HoveringUtils.isHovered(mouseX, mouseY, x, y, w, 16.0);
      RenderUtils.drawRoundedRect(var8, x, y, w, 16.0F, 4.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, var9 ? 28 : 14)));
      Font var10 = Fonts.getFont("suisse", 10);
      var10.draw(
         var8,
         label,
         x + w / 2.0F - var10.getWidth(label) / 2.0F,
         helper(var10, y, 16.0F),
         var9 ? this.resolveInt(-1) : this.resolveInt(ColorUtils.rgba(255, 255, 255, 190))
      );
   }

   private void helper19(DrawContext context, int mouseX, int mouseY) {
      MatrixStack var4 = context.getMatrices();
      float var5 = this.helper4();
      float var6 = this.helper9();
      float var7 = 188.0F;
      RenderUtils.drawRoundedRect(var4, var5, var6, var7, 50.0F, 6.0F, this.resolveInt(ColorUtils.rgba(0, 0, 0, 100)));
      Font var8 = Fonts.getFont("suisse", 9);
      var8.draw(var4, "Сохранённые киты", var5 + 6.0F, helper(var8, var6, 14.0F), this.resolveInt(ColorUtils.rgba(255, 255, 255, 130)));
      List var9 = AutoSetKitStorage.kits();
      float var10 = var6 + 15.0F;
      float var11 = var6 + 50.0F - 3.0F;
      float var12 = 15.0F;
      float var13 = var11 - var10;
      float var14 = var9.size() * var12;
      volume3 = helper34(volume3, 0.0F, Math.max(0.0F, var14 - var13));
      context.enableScissor((int)var5, (int)var10, (int)(var5 + var7), (int)var11);
      Font var15 = Fonts.getFont("suisse", 10);
      if (var9.isEmpty()) {
         Font var16 = Fonts.getFont("suisse", 9);
         var16.draw(var4, "Пока пусто", var5 + 6.0F, var10 + 3.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 70)));
      }

      for (int var23 = 0; var23 < var9.size(); var23++) {
         AutoSetKit var17 = (AutoSetKit)var9.get(var23);
         float var18 = var10 + var23 * var12 - volume3;
         if (!(var18 + var12 < var10) && !(var18 > var11)) {
            boolean var19 = HoveringUtils.isHovered(mouseX, mouseY, var5 + 3.0F, var18, var7 - 22.0F, var12 - 2.0F);
            if (var19) {
               RenderUtils.drawRoundedRect(
                  var4, var5 + 3.0F, var18, var7 - 22.0F, var12 - 2.0F, 3.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 14))
               );
            }

            String var20 = (var17.name == null ? "?" : var17.name) + " · " + var17.total();
            var15.draw(
               var4,
               helper33(var15, var20, var7 - 34.0F),
               var5 + 6.0F,
               helper(var15, var18, var12 - 2.0F),
               var19 ? this.resolveInt(-1) : this.resolveInt(ColorUtils.rgba(255, 255, 255, 190))
            );
            float var21 = var5 + var7 - 15.0F;
            boolean var22 = HoveringUtils.isHovered(mouseX, mouseY, var21, var18, 12.0, var12 - 2.0F);
            var15.draw(
               var4,
               "×",
               var21 + 6.0F - var15.getWidth("×") / 2.0F,
               helper(var15, var18, var12 - 2.0F),
               var22 ? this.resolveInt(ColorUtils.rgba(230, 90, 90, 255)) : this.resolveInt(ColorUtils.rgba(255, 255, 255, 100))
            );
         }
      }

      context.disableScissor();
      this.helper21(var4, var5 + var7 - 3.0F, var10, var13, var14, volume3);
   }

   private void helper20(DrawContext context, List<AutoSetItem> items, int mouseX, int mouseY) {
      AutoSetItem var5 = this.helper23(items, mouseX, mouseY);
      if (var5 != null) {
         MatrixStack var6 = context.getMatrices();
         Font var7 = Fonts.getFont("suisse", 10);
         String var8 = var5.name();
         float var9 = var7.getWidth(var8) + 14.0F;
         float var10 = 16.0F;
         float var11 = Math.min(mouseX + 9.0F, this.volume4 + this.volume6 - var9 - 2.0F);
         float var12 = Math.min(mouseY + 12.0F, this.volume5 + this.volume7 - var10 - 2.0F);
         RenderUtils.drawRoundedRect(var6, var11, var12, var9, var10, 4.0F, this.resolveInt(ColorUtils.rgba(10, 10, 12, 245)));
         var7.draw(var6, var8, var11 + 7.0F, helper(var7, var12, var10), this.resolveInt(-1));
      }
   }

   private void helper21(MatrixStack m, float x, float y, float viewH, float contentH, float scroll) {
      if (!(contentH <= viewH)) {
         float var7 = Math.max(16.0F, viewH * (viewH / contentH));
         float var8 = y + (viewH - var7) * (scroll / Math.max(1.0F, contentH - viewH));
         RenderUtils.drawRoundedRect(m, x, y, 2.0F, viewH, 1.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 15)));
         RenderUtils.drawRoundedRect(m, x, var8, 2.0F, var7, 1.0F, this.resolveInt(ColorUtils.rgba(255, 255, 255, 80)));
      }
   }

   public boolean handleClick(ClickGuiState state, float px, float py, float pw, float ph, double mouseX, double mouseY, int button) {
      this.updateState(px, py, pw, ph);
      AutoSetTextInput var11 = null;

      for (AutoSetTextInput var13 : this.helper30()) {
         if (var13.mouseClicked(mouseX, mouseY)) {
            var11 = var13;
         }
      }

      if (var11 != null) {
         for (AutoSetTextInput var24 : this.helper30()) {
            if (var24 != var11) {
               var24.setFocused(false);
            }
         }

         return true;
      } else {
         Font var21 = Fonts.getFont("suisse", 10);
         float var23 = px;
         float var14 = pw - 92.0F - 8.0F;

         for (int var15 = 0; var15 < TEXT.length; var15++) {
            float var16 = var21.getWidth(TEXT2[var15]) + 14.0F;
            if (var23 + var16 > px + var14) {
               break;
            }

            if (HoveringUtils.isHovered(mouseX, mouseY, var23, py, var16, 15.0)) {
               if (index != var15) {
                  index = var15;
                  volume = 0.0F;
               }

               return true;
            }

            var23 += var16 + 4.0F;
         }

         AutoSetItem var25 = this.helper23(this.helper22(), mouseX, mouseY);
         if (var25 != null) {
            autoSetKit.add(var25, button == 1 ? -1 : 1);
            this.helper25(var25);
            return true;
         }

         if (this.helper26(mouseX, mouseY)) {
            return true;
         }

         float var26 = this.helper4();
         float var17 = 188.0F;
         float var18 = (var17 - 4.0F) / 2.0F;
         float var19 = this.helper7();
         if (HoveringUtils.isHovered(mouseX, mouseY, var26, var19, var18, 16.0)) {
            this.helper29();
            return true;
         }

         if (HoveringUtils.isHovered(mouseX, mouseY, var26 + var18 + 4.0F, var19, var18, 16.0)) {
            autoSetKit = new AutoSetKit("");
            AUTO_SET_TEXT_INPUT2.setText("");
            STRINGS.clear();
            return true;
         }

         if (HoveringUtils.isHovered(mouseX, mouseY, var26, this.helper8(), var17, 20.0)) {
            AutoSetCollector var20 = AutoSetCollector.INSTANCE;
            if (var20.isRunning()) {
               var20.stop();
            } else if (!autoSetKit.isEmpty() && var20.start(autoSetKit)) {
               state.setAutosetPanelOpen(false);
               MinecraftClient.getInstance().setScreen(null);
            }

            return true;
         } else {
            return this.helper27(mouseX, mouseY);
         }
      }
   }

   public boolean handleScroll(float px, float py, float pw, float ph, double mouseX, double mouseY, double amount) {
      this.updateState(px, py, pw, ph);
      float var11 = 16.0F;
      if (mouseX >= px && mouseX <= px + this.helper3() && mouseY >= this.helper2() && mouseY <= this.helper5()) {
         volume -= (float)amount * var11;
         return true;
      }

      if (mouseX >= this.helper4() && mouseX <= this.helper4() + 188.0F) {
         if (mouseY >= this.helper10() && mouseY <= this.helper11()) {
            volume2 -= (float)amount * var11;
            return true;
         }

         if (mouseY >= this.helper9() && mouseY <= this.helper9() + 50.0F) {
            volume3 -= (float)amount * var11;
            return true;
         }
      }

      return false;
   }

   public boolean handleKey(int keyCode, int modifiers) {
      AutoSetTextInput var3 = this.helper31();
      return var3 != null && var3.keyPressed(keyCode, modifiers);
   }

   public boolean handleChar(char chr) {
      AutoSetTextInput var2 = this.helper31();
      return var2 != null && var2.charTyped(chr);
   }

   public boolean isCapturingKeyboard() {
      return this.helper31() != null;
   }

   private List<AutoSetItem> helper22() {
      List<AutoSetItem> var1 = AutoSetCatalog.byGroup(TEXT[index]);
      String var2 = AUTO_SET_TEXT_INPUT.getText().trim().toLowerCase(Locale.ROOT);
      if (var2.isEmpty()) {
         return var1;
      }

      ArrayList<AutoSetItem> var3 = new ArrayList<>();

      for (AutoSetItem var5 : var1) {
         String var6 = var5.name() == null ? "" : var5.name().toLowerCase(Locale.ROOT);
         String var7 = var5.alias() == null ? "" : var5.alias().toLowerCase(Locale.ROOT);
         if (var6.contains(var2) || var7.contains(var2)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private AutoSetItem helper23(List<AutoSetItem> items, double mouseX, double mouseY) {
      float var6 = this.volume4;
      float var7 = this.helper2();
      float var8 = var7 + 6.0F;
      float var9 = this.helper5() - 6.0F;
      if (!(mouseY < var8) && !(mouseY > var9) && !(mouseX < var6) && !(mouseX > var6 + this.helper3())) {
         int var10 = this.resolveInt2();

         for (int var11 = 0; var11 < items.size(); var11++) {
            int var12 = var11 % var10;
            int var13 = var11 / var10;
            float var14 = var6 + 6.0F + var12 * 24.0F;
            float var15 = var7 + 6.0F + var13 * 24.0F - volume;
            if (HoveringUtils.isHovered(mouseX, mouseY, var14, var15, 21.0, 21.0)) {
               return (AutoSetItem)items.get(var11);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private int helper24(AutoSetItem item) {
      AutoSetKit.Entry var2 = autoSetKit.find(item);
      return var2 == null ? 0 : var2.count;
   }

   private void helper25(AutoSetItem item) {
      AutoSetKit.Entry var2 = autoSetKit.find(item);
      if (var2 == null) {
         STRINGS.remove(AutoSetKit.keyOf(item));
      } else {
         AutoSetTextInput[] var3 = STRINGS.get(var2.key);
         if (var3 != null) {
            var3[2].setText(var2.count > 0 ? String.valueOf(var2.count) : "");
         }
      }
   }

   private boolean helper26(double mouseX, double mouseY) {
      float var5 = this.helper4();
      float var6 = 188.0F;
      float var7 = this.helper10();
      float var8 = this.helper11();
      if (!(mouseY < var7) && !(mouseY > var8)) {
         float var9 = var5 + 4.0F + (var6 - 8.0F);
         ArrayList var10 = new ArrayList<>(autoSetKit.entries);

         for (int var11 = 0; var11 < var10.size(); var11++) {
            AutoSetKit.Entry var12 = (AutoSetKit.Entry)var10.get(var11);
            float var13 = var7 + 3.0F + var11 * 20.0F - volume2;
            if (HoveringUtils.isHovered(mouseX, mouseY, var9 - 11.0F, var13 + 2.5F, 11.0, 13.0)) {
               autoSetKit.entries.remove(var12);
               STRINGS.remove(var12.key);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean helper27(double mouseX, double mouseY) {
      List var5 = AutoSetKitStorage.kits();
      float var6 = this.helper4();
      float var7 = 188.0F;
      float var8 = this.helper9() + 15.0F;
      float var9 = this.helper9() + 50.0F - 3.0F;
      float var10 = 15.0F;
      if (!(mouseY < var8) && !(mouseY > var9)) {
         for (int var11 = 0; var11 < var5.size(); var11++) {
            AutoSetKit var12 = (AutoSetKit)var5.get(var11);
            float var13 = var8 + var11 * var10 - volume3;
            float var14 = var6 + var7 - 15.0F;
            if (HoveringUtils.isHovered(mouseX, mouseY, var14, var13, 12.0, var10 - 2.0F)) {
               AutoSetKitStorage.delete(var12);
               return true;
            }

            if (HoveringUtils.isHovered(mouseX, mouseY, var6 + 3.0F, var13, var7 - 22.0F, var10 - 2.0F)) {
               this.helper28(var12);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void helper28(AutoSetKit source) {
      AutoSetKit var2 = new AutoSetKit(source.name);

      for (AutoSetKit.Entry var4 : source.entries) {
         AutoSetKit.Entry var5 = new AutoSetKit.Entry(var4.key, var4.count);
         var5.maxUnitPrice = var4.maxUnitPrice;
         var5.minDurabilityPercent = var4.minDurabilityPercent;
         var2.entries.add(var5);
      }

      autoSetKit = var2;
      AUTO_SET_TEXT_INPUT2.setText(var2.name);
      STRINGS.clear();
      volume2 = 0.0F;
   }

   private void helper29() {
      if (autoSetKit.name != null && !autoSetKit.name.isBlank()) {
         AutoSetKitStorage.save(autoSetKit);
      } else {
         AUTO_SET_TEXT_INPUT2.setFocused(true);
      }
   }

   private List<AutoSetTextInput> helper30() {
      ArrayList var1 = new ArrayList();
      var1.add(AUTO_SET_TEXT_INPUT);
      var1.add(AUTO_SET_TEXT_INPUT2);

      for (AutoSetKit.Entry var3 : autoSetKit.entries) {
         AutoSetTextInput[] var4 = STRINGS.get(var3.key);
         if (var4 != null) {
            var1.add(var4[0]);
            var1.add(var4[1]);
            var1.add(var4[2]);
         }
      }

      return var1;
   }

   private AutoSetTextInput helper31() {
      for (AutoSetTextInput var2 : this.helper30()) {
         if (var2.isFocused()) {
            return var2;
         }
      }

      return null;
   }

   private static ItemStack helper32(AutoSetItem it) {
      if (it != null && it.id() != null) {
         Identifier var1 = Identifier.tryParse(it.id());
         if (var1 == null) {
            return ItemStack.EMPTY;
         }

         Item var2 = Registries.ITEM.get(var1);
         return var2 == Items.AIR ? ItemStack.EMPTY : new ItemStack(var2);
      } else {
         return ItemStack.EMPTY;
      }
   }

   private static String helper33(Font font, String text, float maxWidth) {
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

   private static float helper34(float v, float lo, float hi) {
      return Math.max(lo, Math.min(hi, v));
   }

   static {
      AUTO_SET_TEXT_INPUT2.onChange(v -> autoSetKit.name = v);
   }
}
