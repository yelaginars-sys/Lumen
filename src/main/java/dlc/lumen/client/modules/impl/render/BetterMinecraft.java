package dlc.lumen.client.modules.impl.render;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;

public class BetterMinecraft extends Module {
   public static final BetterMinecraft INSTANCE = new BetterMinecraft();
   private static final long TIMESTAMP = 130L;
   private static final long TIMESTAMP2 = 110L;
   private static final float VOLUME = 0.86F;
   private final BooleanSetting booleanSetting = new BooleanSetting("Хранилища", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Инвентарь", true);

   private BetterMinecraft() {
      super("BetterMinecraft", "Анимация открытия инвентаря и хранилищ", Module.ModuleCategory.RENDER);
      this.addSettings(this.booleanSetting, this.booleanSetting2);
   }

   public boolean animates(boolean inventoryScreen) {
      if (!this.isEnable()) {
         return false;
      } else {
         return inventoryScreen ? this.booleanSetting2.isState() : this.booleanSetting.isState();
      }
   }

   public float openScale(long openedAt) {
      return 0.86F + 0.13999999F * this.helper2(this.helper(openedAt, 130L));
   }

   public float closeScale(long closedAt, float startScale) {
      return startScale * (1.0F - this.helper2(this.helper(closedAt, 110L)));
   }

   public boolean closeFinished(long closedAt) {
      return System.currentTimeMillis() - closedAt >= 110L;
   }

   private float helper(long since, long duration) {
      return Math.clamp((float)(System.currentTimeMillis() - since) / (float)duration, 0.0F, 1.0F);
   }

   private float helper2(float t) {
      return t < 0.5F ? 2.0F * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 2.0) / 2.0F;
   }
}
