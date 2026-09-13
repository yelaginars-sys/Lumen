package dlc.lumen.client.ui.clickgui;

import dlc.lumen.client.render.figura.FiguraBridge;

public final class ClickGuiModelsLayout {
   public static final float CARD_HEIGHT = 122.0F;
   public static final float CARD_GAP = 8.0F;
   public static final float TAB_BAR_H = 11.0F;
   public static final String[] TABS = new String[]{"Модели", "Оружие", "Питомцы", "Головы"};
   private static int tab2 = 0;
   public static final float COSMETIC_ROW_H = 20.0F;

   public static FiguraBridge.Category category(int tab) {
      return switch (tab) {
         case 1 -> FiguraBridge.Category.WEAPON;
         case 2 -> FiguraBridge.Category.PET;
         case 3 -> FiguraBridge.Category.HEAD;
         default -> FiguraBridge.Category.MODEL;
      };
   }

   private ClickGuiModelsLayout() {
   }

   public static int getActiveTab() {
      return tab2;
   }

   public static void setActiveTab(int tab) {
      tab2 = Math.max(0, Math.min(TABS.length - 1, tab));
   }

   public static float[] tabRect(int i, float moduleX, float moduleY, float moduleWidth) {
      float var4 = 40.0F;
      float var5 = 8.0F;
      float var6 = 4.0F;
      float var7 = TABS.length * var4 + (TABS.length - 1) * var6;
      float var8 = moduleX + (moduleWidth - var7) / 2.0F;
      float var9 = var8 + i * (var4 + var6);
      float var10 = moduleY + 1.5F;
      return new float[]{var9, var10, var4, var5};
   }

   public static float[] cosmeticRowRect(int i, float moduleX, float moduleY, float moduleWidth, float scrollOffset) {
      float var5 = 2.0F;
      float var6 = moduleY + 11.0F + 4.0F + i * 23.0F - scrollOffset;
      return new float[]{moduleX + var5, var6, moduleWidth - var5 * 2.0F, 20.0F};
   }

   public static float[] gridCardRect(int index, float moduleX, float moduleY, float targetCellWidth, float cellWidth, float scrollOffset, int columns) {
      int var7 = Math.max(1, columns);
      int var8 = index % var7;
      int var9 = index / var7;
      float var10 = moduleX + var8 * (targetCellWidth + 8.0F);
      float var11 = moduleY + 11.0F + 4.0F + var9 * 130.0F - scrollOffset;
      return new float[]{var10, var11, cellWidth, 122.0F};
   }

   public static float gridHeight(int count, int columns) {
      int var2 = Math.max(1, columns);
      int var3 = (count + var2 - 1) / var2;
      return 11.0F + Math.max(0.0F, var3 * 130.0F - 8.0F) + 4.0F;
   }

   public static float calculateTotalHeight(ClickGuiState state, float moduleWidth) {
      int var2 = 1 + FiguraBridge.listAvatarInfos().size();
      return gridHeight(var2, state.getModuleColumns());
   }
}
