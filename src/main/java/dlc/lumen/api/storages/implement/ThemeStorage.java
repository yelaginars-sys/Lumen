package dlc.lumen.api.storages.implement;

import dlc.lumen.api.storages.implement.helpertstorages.Theme;
import dlc.lumen.api.utils.color.ColorUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import lombok.Generated;

public class ThemeStorage {
   private ObjectArrayList<ThemeStorage.Themes> customTheme = new ObjectArrayList();
   private ThemeStorage.Themes themes;

   public ThemeStorage() {
      this.handleAction();
   }

   public ThemeStorage.Themes getThemes() {
      ThemeStorage.Themes var1 = this.themes;
      if (var1 != null) {
         Theme var2 = var1.getTheme();
         if (var2 != null && var2.color != null) {
            int var3 = ClientColors.outline();
            if (var2.color.length >= 1) {
               var2.color[0] = var3;
            }

            if (var2.color.length >= 2) {
               var2.color[1] = ColorUtils.darken(var3, 0.35F);
            }
         }
      }

      return var1;
   }

   private void handleAction() {
      this.customTheme
         .addAll(
            Arrays.asList(
               ThemeStorage.Themes.Custom,
               ThemeStorage.Themes.Purple,
               ThemeStorage.Themes.Red,
               ThemeStorage.Themes.Blue,
               ThemeStorage.Themes.Green,
               ThemeStorage.Themes.Pink,
               ThemeStorage.Themes.Orange,
               ThemeStorage.Themes.Blues,
               ThemeStorage.Themes.Yellows
            )
         );
      this.themes = (ThemeStorage.Themes)this.customTheme.get(1);
   }

   public Theme getCustomTheme() {
      return ThemeStorage.Themes.Custom.getTheme();
   }

   public int getCustomPrimaryColor() {
      return this.getCustomTheme().color != null && this.getCustomTheme().color.length > 0
         ? this.getCustomTheme().color[0]
         : ColorUtils.rgba(255, 255, 255, 255);
   }

   public int getCustomSecondaryColor() {
      return this.getCustomTheme().color != null && this.getCustomTheme().color.length > 1
         ? this.getCustomTheme().color[1]
         : ColorUtils.rgba(125, 217, 250, 255);
   }

   public void setCustomPrimaryColor(int color) {
      this.getCustomTheme().setColor(0, color);
   }

   public void setCustomSecondaryColor(int color) {
      this.getCustomTheme().setColor(1, color);
   }

   public boolean isCustomAnimated() {
      return this.getCustomTheme().isAnimated();
   }

   public void setCustomAnimated(boolean animated) {
      this.getCustomTheme().setAnimated(animated);
   }

   @Generated
   public ObjectArrayList<ThemeStorage.Themes> getThemeList() {
      return this.customTheme;
   }

   @Generated
   public void setThemeList(ObjectArrayList<ThemeStorage.Themes> themeList) {
      this.customTheme = themeList;
   }

   @Generated
   public void setThemes(ThemeStorage.Themes themes) {
      this.themes = themes;
   }

   public enum Themes {
      Custom(new Theme("Rainbow", true, ColorUtils.rgba(255, 255, 255, 255), ColorUtils.rgba(125, 217, 250, 255))),
      Purple(new Theme("Lavender", ColorUtils.rgba(190, 143, 255, 255), ColorUtils.darken(ColorUtils.rgba(190, 143, 255, 255), 0.35F))),
      Red(new Theme("Blood", ColorUtils.rgba(230, 50, 57, 255), ColorUtils.darken(ColorUtils.rgba(230, 50, 57, 255), 0.35F))),
      Blue(new Theme("Ocean", ColorUtils.rgba(95, 113, 191, 255), ColorUtils.darken(ColorUtils.rgba(95, 113, 191, 255), 0.35F))),
      Green(new Theme("Emerald", ColorUtils.rgba(60, 220, 140, 255), ColorUtils.darken(ColorUtils.rgba(60, 220, 140, 255), 0.35F))),
      Pink(new Theme("Rose", ColorUtils.rgba(255, 120, 190, 255), ColorUtils.darken(ColorUtils.rgba(255, 120, 190, 255), 0.35F))),
      Orange(new Theme("Gold", ColorUtils.rgba(252, 192, 88, 255), ColorUtils.darken(ColorUtils.rgba(252, 192, 88, 255), 0.35F))),
      Blues(new Theme("Diamond", ColorUtils.rgba(125, 217, 250, 255), ColorUtils.darken(ColorUtils.rgba(125, 217, 250, 255), 0.35F))),
      Yellows(new Theme("Sun", ColorUtils.rgba(252, 231, 88, 255), ColorUtils.darken(ColorUtils.rgba(252, 231, 88, 255), 0.35F)));

      final Theme theme;

      @Generated
      Themes(final Theme theme) {
         this.theme = theme;
      }

      @Generated
      public Theme getTheme() {
         return this.theme;
      }
   }
}
