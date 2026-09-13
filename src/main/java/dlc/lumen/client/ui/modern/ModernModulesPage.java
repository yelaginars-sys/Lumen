package dlc.lumen.client.ui.modern;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.ActionSetting;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.math.MathHelper;

public final class ModernModulesPage {
   private static final float VOLUME = 7.0F;
   private static final float VOLUME2 = 29.0F;
   private static final float VOLUME3 = 7.0F;
   private static final float VOLUME4 = 6.0F;
   private static final float VOLUME5 = 11.0F;
   private static final float VOLUME6 = 11.0F;
   private static final float VOLUME7 = 16.0F;

   private ModernModulesPage() {
   }

   public static void render(ModernGui g, float x, float y, float w, float h) {
      List var5 = g.visibleModules();
      float var6 = g.scroll();
      float var7 = (w - 7.0F) / 2.0F;
      float[] var8 = new float[]{y - var6, y - var6};
      if (var5.isEmpty()) {
         g.text(g.font(11), "ничего не найдено", x + 2.0F, y + 8.0F, ModernTheme.TEXT_MUTED());
         g.setContentHeight(0.0F, h);
      } else {
         for (int var9 = 0; var9 < var5.size(); var9++) {
            Module var10 = (Module)var5.get(var9);
            int var11 = var9 % 2;
            float var12 = x + var11 * (var7 + 7.0F);
            float var13 = var8[var11];
            float var14 = helper("appear:" + var10.getName(), var9 * 0.055F);
            float var15 = helper3(g, var10);
            float var16 = 29.0F + var15;
            if (var13 + var16 > y - 30.0F && var13 < y + h + 30.0F && var14 > 0.01F) {
               helper2(g, var10, var12, var13 + (1.0F - var14) * 6.0F, var7, var16, var15, var14);
            }

            var8[var11] += var16 + 7.0F;
         }

         float var17 = Math.max(var8[0], var8[1]) - (y - var6);
         g.setContentHeight(var17, h);
      }
   }

   private static float helper(String key, float delay) {
      if (ModernAnim.get(key, -999.0F) == -999.0F) {
         ModernAnim.set(key, -delay);
      }

      return MathHelper.clamp(ModernAnim.value(key, 1.0F, 7.5F), 0.0F, 1.0F);
   }

   private static void helper2(ModernGui g, Module module, float x, float y, float w, float h, float bodyHeight, float appear) {
      boolean var8 = module.isEnable();
      boolean var9 = g.hovered(x, y, w, h);
      float var10 = ModernAnim.value("cardhover:" + module.getName(), var9 ? 1.0F : 0.0F, 15.0F);
      float var11 = ModernAnim.value("cardon:" + module.getName(), var8 ? 1.0F : 0.0F, 14.0F);
      float var12 = g.alpha;
      g.alpha = var12 * appear;
      int var13 = ColorUtils.interpolate(ModernTheme.CARD(), ModernTheme.CARD_ON(), var11);
      int var14 = ColorUtils.interpolate(ModernTheme.CARD_HOVER(), ModernTheme.CARD_ON_HOVER(), var11);
      g.surface(
         x,
         y,
         w,
         h,
         ModernTheme.R_CARD(),
         ColorUtils.interpolate(var13, var14, var10),
         ColorUtils.interpolate(ModernTheme.CARD_BORDER(), ModernTheme.CARD_BORDER_ON(), var11),
         4.0F
      );
      int var15 = ColorUtils.interpolate(ModernTheme.ICON_IDLE(), ModernTheme.accent(), var11);
      g.icon("icon", 17, module.getCategory().getIcons(), x + 12.0F, y + 14.5F, var15);
      Font var16 = g.font(12);
      float var17 = w - 23.0F - 40.0F;
      g.text(
         var16, g.clip(var16, module.getDisplayName(), var17), x + 23.0F, y + 7.0F, ColorUtils.interpolate(ModernTheme.TEXT_DIM(), ModernTheme.TEXT(), var11)
      );
      Font var18 = g.font(10);
      String var19 = module.getDisplayDescription();
      if (var19 != null && !var19.isBlank() && !"NULLABLE".equalsIgnoreCase(var19)) {
         g.text(var18, g.clip(var18, var19, var17), x + 23.0F, y + 17.5F, ModernTheme.TEXT_MUTED());
      }

      float var20 = x + w - 11.0F - 20.0F;
      g.toggle(var20, y + 9.5F, var8, module.getName());
      if (module.getKey() != -1 || g.isBinding(module)) {
         String var21 = g.isBinding(module) ? "..." : KeyBoardUtils.getBindName(module.getKey());
         g.textRight(g.font(9), var21, var20 - 6.0F, g.textY(y, 29.0F, g.font(9)), ModernTheme.TEXT_FAINT());
      }

      g.hit(x, y, w, 29.0F, button -> {
         if (button == 1 || button == 2) {
            g.startBind(module);
            return true;
         } else {
            module.toggle();
            return true;
         }
      });
      if (bodyHeight > 0.5F) {
         g.rect(x + 7.0F, y + 29.0F - 0.5F, w - 14.0F, 0.5F, 0.0F, ModernTheme.LINE_SOFT());
         float var21 = y + 29.0F + 7.0F - 1.0F;
         float var22 = x + 7.0F;
         float var23 = w - 14.0F;

         for (Setting var25 : module.getSettings()) {
            if (var25 != null && var25.visible()) {
               var21 += toBooleanOrDefault2(g, var25, var22, var21, var23) + 6.0F;
            }
         }
      }

      g.alpha = var12;
   }

   private static float helper3(ModernGui g, Module module) {
      float var2 = 13.0F;

      for (Setting var4 : module.getSettings()) {
         if (var4 != null && var4.visible()) {
            var2 += toBooleanOrDefault(var4) + 6.0F;
         }
      }

      return var2 + 10.0F;
   }

   private static float toBooleanOrDefault(Setting setting) {
      if (setting instanceof FloatSetting) {
         return 16.0F;
      } else if (setting instanceof ModeSetting var6) {
         float var7 = ModernAnim.get("drop:" + System.identityHashCode(var6), 0.0F);
         return 11.0F + (var6.getMods().size() * 11.0F + 3.0F) * MathHelper.clamp(var7, 0.0F, 1.0F);
      } else if (setting instanceof ListSetting var1) {
         float var2 = ModernAnim.get("drop:" + System.identityHashCode(var1), 0.0F);
         int var3 = 0;

         for (BooleanSetting var5 : var1.getSettings()) {
            if (var5.visible()) {
               var3++;
            }
         }

         return 11.0F + (var3 * 11.0F + 3.0F) * MathHelper.clamp(var2, 0.0F, 1.0F);
      } else {
         return !(setting instanceof ActionSetting) && !(setting instanceof TextSetting) ? 11.0F : 15.0F;
      }
   }

   private static float toBooleanOrDefault2(ModernGui g, Setting setting, float x, float y, float w) {
      if (setting instanceof FloatSetting var11) {
         return toBooleanOrDefault3(g, var11, x, y, w);
      } else if (setting instanceof BooleanSetting var10) {
         return helper4(g, var10, x, y, w);
      } else if (setting instanceof ModeSetting var9) {
         return helper5(g, var9, x, y, w);
      } else if (setting instanceof ListSetting var8) {
         return helper6(g, var8, x, y, w);
      } else if (setting instanceof BindSetting var7) {
         return helper7(g, var7, x, y, w);
      } else if (setting instanceof TextSetting var6) {
         return helper8(g, var6, x, y, w);
      } else {
         return setting instanceof ActionSetting var5 ? helper9(g, var5, x, y, w) : 0.0F;
      }
   }

   private static float toBooleanOrDefault3(ModernGui g, FloatSetting setting, float x, float y, float w) {
      Font var5 = g.font(11);
      String var6 = helper11(setting.get(), setting.getIncrement());
      float var7 = g.font(10).getWidth(var6) + 11.0F;
      g.text(var5, g.clip(var5, setting.displayName(), w - var7 - 6.0F), x, g.textY(y, 11.0F, var5), ModernTheme.TEXT_DIM());
      g.valuePill(x + w, y, var6, ModernTheme.accent());
      float var8 = setting.getMin();
      float var9 = setting.getMax();
      float var10 = var9 - var8 <= 0.0F ? 0.0F : (setting.get() - var8) / (var9 - var8);
      g.slider(x, y + 12.5F, w, var10, setting, fraction -> {
         float var5x = var8 + (float)fraction * (var9 - var8);
         float var6x = setting.getIncrement();
         float var7x = var6x > 0.0F ? Math.round(var5x / var6x) * var6x : var5x;
         setting.setValue(var7x);
      });
      return 16.0F;
   }

   private static float helper4(ModernGui g, BooleanSetting setting, float x, float y, float w) {
      boolean var5 = g.hovered(x, y, w, 11.0F);
      Font var6 = g.font(11);
      g.text(var6, g.clip(var6, setting.displayName(), w - 16.0F), x, g.textY(y, 11.0F, var6), var5 ? ModernTheme.TEXT_SOFT() : ModernTheme.TEXT_DIM());
      g.checkbox(x + w - 12.0F, y + -0.5F, setting.isState(), setting);
      g.hit(x, y, w, 11.0F, button -> {
         if (button == 2) {
            g.startBind(setting);
            return true;
         } else {
            setting.setState(!setting.isState());
            return true;
         }
      });
      if (setting.getKey() != -1 || g.isBinding(setting)) {
         String var7 = g.isBinding(setting) ? "..." : KeyBoardUtils.getBindName(setting.getKey());
         g.textRight(g.font(9), var7, x + w - 12.0F - 5.0F, g.textY(y, 11.0F, g.font(9)), ModernTheme.TEXT_FAINT());
      }

      return 11.0F;
   }

   private static float helper5(ModernGui g, ModeSetting setting, float x, float y, float w) {
      String var5 = "drop:" + System.identityHashCode(setting);
      float var6 = MathHelper.clamp(ModernAnim.value(var5, setting.isExpanded() ? 1.0F : 0.0F, 14.0F), 0.0F, 1.0F);
      Font var7 = g.font(11);
      Font var8 = g.font(10);
      String var9 = setting.displayCurrent();
      float var10 = Math.min(w * 0.62F, var8.getWidth(var9) + 18.0F);
      g.text(var7, g.clip(var7, setting.displayName(), w - var10 - 6.0F), x, g.textY(y, 11.0F, var7), ModernTheme.TEXT_DIM());
      float var11 = x + w - var10;
      float var12 = y + 0.0F;
      g.rect(var11, var12, var10, 11.0F, ModernTheme.R_SMALL(), ModernTheme.PILL());
      g.outline(var11, var12, var10, 11.0F, ModernTheme.R_SMALL(), 0.5F, ModernTheme.CARD_BORDER());
      g.text(var8, g.clip(var8, var9, var10 - 15.0F), var11 + 5.0F, g.textY(var12, 11.0F, var8), ModernTheme.TEXT_SOFT());
      g.chevron(var11 + var10 - 5.0F, var12 + 5.5F, 2.3F, 180.0F * var6, ModernTheme.accent());
      g.hit(x, y, w, 11.0F, button -> {
         setting.setExpanded(!setting.isExpanded());
         return true;
      });
      if (var6 > 0.01F) {
         float var13 = (setting.getMods().size() * 11.0F + 3.0F) * var6;
         g.pushClip(x, y + 11.0F, w, var13);
         float var14 = y + 11.0F + 2.0F;

         for (String var16 : setting.getMods()) {
            boolean var17 = setting.is(var16);
            boolean var18 = g.hovered(x, var14, w, 11.0F);
            float var19 = ModernAnim.value("mode:" + System.identityHashCode(setting) + var16, var17 ? 1.0F : (var18 ? 0.6F : 0.0F), 15.0F);
            if (var19 > 0.01F) {
               g.rect(x, var14, w, 11.0F, ModernTheme.R_SMALL(), ModernTheme.NAV_ACTIVE(), var19);
            }

            g.rect(x + 5.0F, var14 + 5.5F - 1.0F, 2.0F, 2.0F, 1.0F, var17 ? ModernTheme.accent() : ModernTheme.TEXT_FAINT());
            Font var20 = g.font(10);
            g.text(
               var20,
               g.clip(var20, setting.displayMode(var16), w - 16.0F),
               x + 11.0F,
               g.textY(var14, 11.0F, var20),
               var17 ? ModernTheme.accent() : ModernTheme.TEXT_DIM()
            );
            g.hit(x, var14, w, 11.0F, button -> {
               setting.set(var16);
               setting.setExpanded(false);
               return true;
            });
            var14 += 11.0F;
         }

         g.popClip();
         return 11.0F + var13;
      } else {
         return 11.0F;
      }
   }

   private static float helper6(ModernGui g, ListSetting setting, float x, float y, float w) {
      String var5 = "drop:" + System.identityHashCode(setting);
      float var6 = MathHelper.clamp(ModernAnim.value(var5, setting.isExpanded() ? 1.0F : 0.0F, 14.0F), 0.0F, 1.0F);
      int var7 = 0;
      int var8 = 0;

      for (BooleanSetting var10 : setting.getSettings()) {
         if (var10.visible()) {
            var8++;
            if (var10.isState()) {
               var7++;
            }
         }
      }

      Font var21 = g.font(11);
      Font var22 = g.font(10);
      String var11 = var7 + "/" + var8;
      float var12 = var22.getWidth(var11) + 18.0F;
      g.text(var21, g.clip(var21, setting.displayName(), w - var12 - 6.0F), x, g.textY(y, 11.0F, var21), ModernTheme.TEXT_DIM());
      float var13 = x + w - var12;
      float var14 = y + 0.0F;
      g.rect(var13, var14, var12, 11.0F, ModernTheme.R_SMALL(), ModernTheme.PILL());
      g.outline(var13, var14, var12, 11.0F, ModernTheme.R_SMALL(), 0.5F, ModernTheme.CARD_BORDER());
      g.text(var22, var11, var13 + 5.0F, g.textY(var14, 11.0F, var22), ModernTheme.TEXT_SOFT());
      g.chevron(var13 + var12 - 5.0F, var14 + 5.5F, 2.3F, 180.0F * var6, ModernTheme.accent());
      g.hit(x, y, w, 11.0F, button -> {
         setting.setExpanded(!setting.isExpanded());
         return true;
      });
      if (var6 > 0.01F) {
         float var15 = (var8 * 11.0F + 3.0F) * var6;
         g.pushClip(x, y + 11.0F, w, var15);
         float var16 = y + 11.0F + 2.0F;

         for (BooleanSetting var18 : setting.getSettings()) {
            if (var18.visible()) {
               boolean var19 = g.hovered(x, var16, w, 11.0F);
               if (var19) {
                  g.rect(x, var16, w, 11.0F, ModernTheme.R_SMALL(), ModernTheme.NAV_ACTIVE(), 0.7F);
               }

               Font var20 = g.font(10);
               g.text(
                  var20,
                  g.clip(var20, var18.displayName(), w - 20.0F),
                  x + 5.0F,
                  g.textY(var16, 11.0F, var20),
                  var18.isState() ? ModernTheme.TEXT_SOFT() : ModernTheme.TEXT_MUTED()
               );
               g.checkbox(x + w - 12.0F, var16 + -0.5F, var18.isState(), var18);
               g.hit(x, var16, w, 11.0F, button -> {
                  var18.setState(!var18.isState());
                  return true;
               });
               var16 += 11.0F;
            }
         }

         g.popClip();
         return 11.0F + var15;
      } else {
         return 11.0F;
      }
   }

   private static float helper7(ModernGui g, BindSetting setting, float x, float y, float w) {
      Font var5 = g.font(11);
      g.text(var5, setting.displayName(), x, g.textY(y, 11.0F, var5), ModernTheme.TEXT_DIM());
      String var6 = g.isBinding(setting) ? "..." : KeyBoardUtils.getBindName(setting.getKey());
      helper10(g, x + w, y, var6, setting.getKey() != -1 || g.isBinding(setting));
      g.hit(x, y, w, 11.0F, button -> {
         g.startBind(setting);
         return true;
      });
      return 11.0F;
   }

   private static float helper8(ModernGui g, TextSetting setting, float x, float y, float w) {
      Font var5 = g.font(11);
      float var6 = Math.min(w * 0.55F, 78.0F);
      g.text(var5, g.clip(var5, setting.displayName(), w - var6 - 6.0F), x, g.textY(y, 14.0F, var5), ModernTheme.TEXT_DIM());
      g.field(x + w - var6, y + 0.5F, var6, 13.0F, setting, "текст", setting.get(), null);
      return 15.0F;
   }

   private static float helper9(ModernGui g, ActionSetting setting, float x, float y, float w) {
      float var5 = 14.0F;
      boolean var6 = g.hovered(x, y, w, var5);
      float var7 = ModernAnim.value("act:" + System.identityHashCode(setting), var6 ? 1.0F : 0.0F, 15.0F);
      g.surface(x, y, w, var5, ModernTheme.R_ROW(), ColorUtils.interpolate(ModernTheme.PILL(), ModernTheme.PILL_HOVER(), var7), ModernTheme.CARD_BORDER(), 2.0F);
      Font var8 = g.font(11);
      float var9 = var8.getWidth(setting.displayName());
      g.icon("icon", 9, "D", x + w / 2.0F - var9 / 2.0F - 7.0F, y + var5 / 2.0F, ModernTheme.accent());
      g.text(var8, setting.displayName(), x + w / 2.0F - var9 / 2.0F + 2.0F, g.textY(y, var5, var8), ModernTheme.TEXT_SOFT());
      g.hit(x, y, w, var5, button -> {
         setting.run();
         return true;
      });
      return 15.0F;
   }

   private static void helper10(ModernGui g, float rightX, float y, String key, boolean active) {
      Font var5 = g.font(10);
      float var6 = var5.getWidth(key) + 12.0F;
      float var7 = y + 0.0F;
      g.rect(rightX - var6, var7, var6, 11.0F, ModernTheme.R_SMALL(), ModernTheme.PILL());
      g.outline(rightX - var6, var7, var6, 11.0F, ModernTheme.R_SMALL(), 0.5F, ModernTheme.CARD_BORDER());
      g.textCenter(var5, key, rightX - var6 / 2.0F, g.textY(var7, 11.0F, var5), active ? ModernTheme.accent() : ModernTheme.TEXT_MUTED());
   }

   private static String helper11(float value, float increment) {
      if (increment >= 1.0F) {
         return String.valueOf(Math.round(value));
      } else {
         return increment >= 0.1F ? String.format(Locale.ROOT, "%.1f", value) : String.format(Locale.ROOT, "%.2f", value);
      }
   }
}
