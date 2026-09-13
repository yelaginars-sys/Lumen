package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.client.gui.DrawContext;

public class CustomCrosshair extends Module {
   public static final CustomCrosshair INSTANCE = new CustomCrosshair();
   private final ModeSetting modeSetting = new ModeSetting("Стиль", "Крест", "Крест", "Крест+Точка", "Точка", "Круг");
   private final FloatSetting floatSetting = new FloatSetting("Длина", 4.0F, 1.0F, 12.0F, 1.0F);
   private final FloatSetting floatSetting2 = new FloatSetting("Отступ", 3.0F, 0.0F, 12.0F, 1.0F);
   private final FloatSetting floatSetting3 = new FloatSetting("Толщина", 1.0F, 1.0F, 5.0F, 1.0F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Обводка", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Цвет темы", true);

   public CustomCrosshair() {
      super("CustomCrosshair", "Свой прицел по центру экрана", Module.ModuleCategory.RENDER);
      this.addSettings(this.modeSetting, this.floatSetting, this.floatSetting2, this.floatSetting3, this.booleanSetting, this.booleanSetting2);
   }

   @EventLink
   public void onRender(EventRender.Default event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null && mc.options != null) {
         if (mc.options.getPerspective().isFirstPerson()) {
            DrawContext var2 = event.getContext();
            int var3 = mc.getWindow().getScaledWidth() / 2;
            int var4 = mc.getWindow().getScaledHeight() / 2;
            int var5 = Math.round(this.floatSetting.get());
            int var6 = Math.round(this.floatSetting2.get());
            int var7 = Math.max(1, Math.round(this.floatSetting3.get()));
            int var8 = var7 / 2;
            boolean var9 = this.booleanSetting.getValue();
            int var10 = this.booleanSetting2.getValue() ? ColorUtils.getThemeColor() : -1;
            var10 = this.helper2(var10);
            boolean var11 = this.modeSetting.is("Крест") || this.modeSetting.is("Крест+Точка");
            boolean var12 = this.modeSetting.is("Точка") || this.modeSetting.is("Крест+Точка");
            boolean var13 = this.modeSetting.is("Круг");
            if (var11) {
               this.helper(var2, var3 - var6 - var5, var4 - var8, var5, var7, var10, var9);
               this.helper(var2, var3 + var6 + 1, var4 - var8, var5, var7, var10, var9);
               this.helper(var2, var3 - var8, var4 - var6 - var5, var7, var5, var10, var9);
               this.helper(var2, var3 - var8, var4 + var6 + 1, var7, var5, var10, var9);
            }

            if (var13) {
               int var14 = var6 + var5;

               for (byte var15 = 0; var15 < 360; var15 += 3) {
                  double var16 = Math.toRadians(var15);
                  int var18 = var3 + (int)Math.round(Math.cos(var16) * var14);
                  int var19 = var4 + (int)Math.round(Math.sin(var16) * var14);
                  this.helper(var2, var18 - var8, var19 - var8, var7, var7, var10, var9);
               }
            }

            if (var12) {
               int var21 = var7 + 1;
               this.helper(var2, var3 - var21 / 2, var4 - var21 / 2, var21, var21, var10, var9);
            }
         }
      }
   }

   private void helper(DrawContext ctx, int x, int y, int w, int h, int color, boolean outline) {
      if (w > 0 && h > 0) {
         if (outline) {
            ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, -16777216);
         }

         ctx.fill(x, y, x + w, y + h, color);
      }
   }

   private int helper2(int color) {
      return color >>> 24 == 0 ? color | 0xFF000000 : color;
   }
}
