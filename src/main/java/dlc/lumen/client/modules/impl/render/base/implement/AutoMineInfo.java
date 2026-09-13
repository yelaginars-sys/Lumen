package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import dlc.lumen.client.modules.impl.render.Interface;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.Locale;
import net.minecraft.client.util.math.MatrixStack;

public class AutoMineInfo extends InterfaceProcessing {
   public AutoMineInfo(Draggable draggable) {
      super(draggable);
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      if (AutoMine.INSTANCE != null && AutoMine.INSTANCE.isEnable()) {
         this.helper2(eventRender);
         super.onRender(eventRender);
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }

   private float helper() {
      Interface var1 = ModuleClass.interfaceModule;
      return var1 != null && var1.isWaterMarkShown() ? var1.getWaterMarkBottomY() + 3.0F : this.hudTopOffset();
   }

   private void helper2(EventRender.Default eventRender) {
      MatrixStack var2 = eventRender.getContext().getMatrices();
      Font var3 = Fonts.getFont("inter_medium", 13);
      if (var3 == null) {
         var3 = Fonts.getFont("sf_regular", 13);
      }

      if (var3 != null) {
         int var4;
         if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            var4 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
         } else {
            var4 = ColorUtils.getThemeColor();
         }

         byte var5 = -1;
         AutoMine var6 = AutoMine.INSTANCE;
         String var7 = "Уники ";
         String var8 = "+" + var6.getHooksObtained() + " (" + var6.getTotalHookCount() + ")";
         String var9 = "Время ";
         String var10 = this.helper4(var6.getSessionElapsedMs());
         String var11 = "Профит ";
         String var12 = var6.hasProfit() ? this.helper5(var6.getProfit()) : "—";
         float var13 = 4.0F;
         float var14 = 7.0F;
         float var15 = 15.0F;
         float var16 = 13.0F;
         float var17 = var3.getWidth(var7) + var3.getWidth(var8);
         float var18 = var3.getWidth(var9) + var3.getWidth(var10);
         float var19 = var3.getWidth(var11) + var3.getWidth(var12);
         float var20 = 4.0F + var17 + 7.0F + var18 + 7.0F + var19 + 4.0F;
         this.draggable.setLocked(true);
         float var21 = mc.getWindow().getScaledWidth() / 2.0F - var20 / 2.0F;
         float var22 = this.helper();
         this.draggable.setX(var21);
         this.draggable.setY(var22);
         drawHudBg(var2, var21, var22, var20, var16);
         float var23 = var22 + (var16 - var3.getHeight()) / 2.0F + 0.5F + 4.5F;
         float var24 = var21 + 4.0F;
         var24 = this.helper3(var2, var3, var7, var8, var24, var23, var4, var5);
         var24 += 7.0F;
         var24 = this.helper3(var2, var3, var9, var10, var24, var23, var4, var5);
         var24 += 7.0F;
         this.helper3(var2, var3, var11, var12, var24, var23, var4, var5);
         this.draggable.setWidth(var20);
         this.draggable.setHeight(var15);
      }
   }

   private float helper3(MatrixStack m, Font font, String label, String value, float x, float y, int labelColor, int valueColor) {
      font.draw(m, label, x, y, labelColor);
      float var9 = x + font.getWidth(label);
      font.draw(m, value, var9, y, valueColor);
      return var9 + font.getWidth(value);
   }

   private String helper4(long ms) {
      long var3 = ms / 1000L;
      long var5 = var3 / 3600L;
      long var7 = var3 % 3600L / 60L;
      long var9 = var3 % 60L;
      return var5 > 0L ? String.format(Locale.ROOT, "%d:%02d:%02d", var5, var7, var9) : String.format(Locale.ROOT, "%02d:%02d", var7, var9);
   }

   private String helper5(long cents) {
      String var3 = cents > 0L ? "+" : (cents < 0L ? "-" : "");
      long var4 = Math.abs(cents) / 100L;
      return var3 + "$" + this.helper6(var4);
   }

   private String helper6(long v) {
      return String.format(Locale.ROOT, "%,d", v).replace(',', ' ');
   }
}
