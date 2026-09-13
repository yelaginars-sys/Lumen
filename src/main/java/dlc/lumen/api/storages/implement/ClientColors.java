package dlc.lumen.api.storages.implement;

import dlc.lumen.api.utils.color.ColorUtils;

public final class ClientColors {
   private static final int label = 10;
   private static final int[] colors = new int[ClientColors.Slot.values().length];
   private static final boolean[] flagArray = new boolean[ClientColors.Slot.values().length];

   private ClientColors() {
   }

   public static void applyPreset(ClientColors.Preset preset) {
      ClientColors.Slot[] var1 = ClientColors.Slot.values();

      for (int var2 = 0; var2 < var1.length && var2 < preset.colors.length; var2++) {
         colors[var1[var2].ordinal()] = preset.colors[var2];
         flagArray[var1[var2].ordinal()] = false;
      }
   }

   public static int color(ClientColors.Slot slot) {
      int var1 = colors[slot.ordinal()];
      if (flagArray[slot.ordinal()]) {
         int var2 = ColorUtils.rainbow(10, slot.ordinal() * 40, 0.7F, 1.0F, 1.0F);
         return ColorUtils.setAlphaColor(var2, ColorUtils.alpha(var1));
      } else {
         return var1;
      }
   }

   public static int raw(ClientColors.Slot slot) {
      return colors[slot.ordinal()];
   }

   public static boolean rainbow(ClientColors.Slot slot) {
      return flagArray[slot.ordinal()];
   }

   public static void set(ClientColors.Slot slot, int color) {
      colors[slot.ordinal()] = color;
   }

   public static void setRainbow(ClientColors.Slot slot, boolean value) {
      flagArray[slot.ordinal()] = value;
   }

   public static void reset(ClientColors.Slot slot) {
      colors[slot.ordinal()] = slot.defaultColor();
      flagArray[slot.ordinal()] = false;
   }

   public static void resetAll() {
      for (ClientColors.Slot var3 : ClientColors.Slot.values()) {
         reset(var3);
      }
   }

   public static int outline() {
      return color(ClientColors.Slot.OUTLINE);
   }

   public static int background() {
      return color(ClientColors.Slot.BACKGROUND);
   }

   public static int element() {
      return color(ClientColors.Slot.ELEMENT);
   }

   public static int text() {
      return color(ClientColors.Slot.TEXT);
   }

   public static int textSecondary() {
      return color(ClientColors.Slot.TEXT_SECONDARY);
   }

   public static int icon() {
      return color(ClientColors.Slot.ICON);
   }

   public static int toggle() {
      return color(ClientColors.Slot.TOGGLE);
   }

   static {
      resetAll();
   }

   public enum Preset {
      DARK(
         "Тёмная",
         new int[]{
            ColorUtils.rgba(125, 217, 250, 255),
            ColorUtils.rgba(8, 8, 10, 255),
            ColorUtils.rgba(22, 22, 26, 255),
            ColorUtils.rgba(255, 255, 255, 255),
            ColorUtils.rgba(255, 255, 255, 150),
            ColorUtils.rgba(125, 217, 250, 255),
            ColorUtils.rgba(125, 217, 250, 255)
         }
      ),
      GRAY(
         "Серая",
         new int[]{
            ColorUtils.rgba(155, 155, 162, 255),
            ColorUtils.rgba(20, 20, 22, 255),
            ColorUtils.rgba(42, 42, 46, 255),
            ColorUtils.rgba(255, 255, 255, 255),
            ColorUtils.rgba(255, 255, 255, 150),
            ColorUtils.rgba(178, 178, 185, 255),
            ColorUtils.rgba(155, 155, 162, 255)
         }
      ),
      BLUE(
         "Синяя",
         new int[]{
            ColorUtils.rgba(60, 140, 255, 255),
            ColorUtils.rgba(8, 10, 16, 255),
            ColorUtils.rgba(24, 28, 40, 255),
            ColorUtils.rgba(255, 255, 255, 255),
            ColorUtils.rgba(255, 255, 255, 150),
            ColorUtils.rgba(60, 140, 255, 255),
            ColorUtils.rgba(60, 140, 255, 255)
         }
      ),
      PURPLE(
         "Фиолетовая",
         new int[]{
            ColorUtils.rgba(170, 120, 255, 255),
            ColorUtils.rgba(14, 10, 20, 255),
            ColorUtils.rgba(34, 26, 46, 255),
            ColorUtils.rgba(255, 255, 255, 255),
            ColorUtils.rgba(255, 255, 255, 150),
            ColorUtils.rgba(170, 120, 255, 255),
            ColorUtils.rgba(170, 120, 255, 255)
         }
      );

      private final String label;
      private final int[] colors;

      Preset(String label, int[] colors) {
         this.label = label;
         this.colors = colors;
      }

      public String label() {
         return this.label;
      }

      public int accentColor() {
         return this.colors[ClientColors.Slot.OUTLINE.ordinal()];
      }
   }

   public enum Slot {
      OUTLINE("Обводка", ColorUtils.rgba(125, 217, 250, 255)),
      BACKGROUND("Фон панели", ColorUtils.rgba(13, 13, 16, 255)),
      ELEMENT("Фон элементов", ColorUtils.rgba(30, 30, 35, 255)),
      TEXT("Текст", ColorUtils.rgba(255, 255, 255, 255)),
      TEXT_SECONDARY("Второст. текст", ColorUtils.rgba(255, 255, 255, 160)),
      ICON("Иконки", ColorUtils.rgba(125, 217, 250, 255)),
      TOGGLE("Тумблеры", ColorUtils.rgba(125, 217, 250, 255));

      private final String label;
      private final int colors;

      Slot(String label, int defaultColor) {
         this.label = label;
         this.colors = defaultColor;
      }

      public String label() {
         return this.label;
      }

      public int defaultColor() {
         return this.colors;
      }
   }
}
