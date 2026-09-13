package dlc.lumen.client.modules.settings.implement;

import dlc.lumen.client.modules.settings.Setting;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class FloatSetting extends Setting {
   private float value2;
   private final float min2;
   private final float max2;
   private final float increment2;
   private boolean active2;

   public FloatSetting(String name, float value, float min, float max, float increment) {
      super(name);
      this.value2 = value;
      this.min2 = min;
      this.max2 = max;
      this.increment2 = increment;
   }

   public Number getValue() {
      return MathHelper.clamp(this.value2, this.getMin(), this.getMax());
   }

   public void setValue(float value) {
      this.value2 = MathHelper.clamp(value, this.getMin(), this.getMax());
   }

   public float get() {
      return this.getValue().floatValue();
   }

   public FloatSetting visible(Supplier<Boolean> state) {
      this.visible = state;
      return this;
   }

   @Generated
   public float getMin() {
      return this.min2;
   }

   @Generated
   public float getMax() {
      return this.max2;
   }

   @Generated
   public float getIncrement() {
      return this.increment2;
   }

   @Generated
   public boolean isActive() {
      return this.active2;
   }

   @Generated
   public void setActive(boolean active) {
      this.active2 = active;
   }
}
