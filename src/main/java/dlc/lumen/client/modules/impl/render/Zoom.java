package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventScroll;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.util.math.MathHelper;

public class Zoom extends Module {
   public static Zoom INSTANCE = new Zoom();
   private final FloatSetting floatSetting = new FloatSetting("Приближение", 4.0F, 1.0F, 15.0F, 0.25F);
   private final FloatSetting floatSetting2 = new FloatSetting("Плавность", 0.5F, 0.0F, 1.0F, 0.05F);
   private final FloatSetting floatSetting3 = new FloatSetting("Шаг колеса", 1.0F, 0.25F, 5.0F, 0.25F);
   private final BooleanSetting mouseSensitivityScale = new BooleanSetting("Замедлять мышь", true);
   private double mouseSensitivityScale2 = 1.0;
   private long timestamp = 0L;

   public Zoom() {
      super("Zoom", "Плавно приближает обзор, кратность крутится колесом", Module.ModuleCategory.RENDER);
      this.addSettings(this.floatSetting, this.floatSetting2, this.floatSetting3, this.mouseSensitivityScale);
   }

   @EventLink
   public void onScroll(EventScroll event) {
      if (this.isEnable()) {
         float var2 = this.floatSetting3.get() * (event.getAmount() > 0.0 ? 1.0F : -1.0F);
         this.floatSetting.setValue(this.floatSetting.get() + var2);
         event.cancel();
      }
   }

   public float advanceAndGetFovFactor() {
      double var1 = this.isEnable() ? 1.0 / Math.max(1.0, this.floatSetting.get()) : 1.0;
      long var3 = System.nanoTime();
      double var5 = this.timestamp == 0L ? 0.0 : (var3 - this.timestamp) / 1.0E9;
      this.timestamp = var3;
      if (var5 < 0.0) {
         var5 = 0.0;
      }

      if (var5 > 0.1) {
         var5 = 0.1;
      }

      double var7 = MathHelper.clamp(this.floatSetting2.get(), 0.0F, 1.0F);
      double var9 = 25.0 - 22.0 * var7;
      double var11 = 1.0 - Math.exp(-var9 * var5);
      this.mouseSensitivityScale2 = this.mouseSensitivityScale2 + (var1 - this.mouseSensitivityScale2) * var11;
      if (Math.abs(this.mouseSensitivityScale2 - var1) < 1.0E-4) {
         this.mouseSensitivityScale2 = var1;
      }

      return (float)this.mouseSensitivityScale2;
   }

   public double getMouseSensitivityScale() {
      return !this.mouseSensitivityScale.getValue() ? 1.0 : MathHelper.clamp(this.mouseSensitivityScale2, 0.05, 1.0);
   }

   @Override
   public void onEnable() {
      this.timestamp = 0L;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.timestamp = 0L;
      super.onDisable();
   }
}
