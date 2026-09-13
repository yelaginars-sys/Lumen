package dlc.lumen.api.utils.animation;

import net.minecraft.util.math.MathHelper;

public class AnimationUtils {
   private float done;
   private float target;
   private float initialValue;
   private float speed;
   private Easing easing;
   private long timeMs;
   private double dvalue;
   private boolean flag;

   public AnimationUtils(float initialValue, float speed, Easing easing) {
      this.done = initialValue;
      this.target = initialValue;
      this.initialValue = initialValue;
      this.speed = speed;
      this.easing = easing != null ? easing : Easings.LINEAR;
      this.timeMs = 0L;
      this.dvalue = 0.0;
      this.flag = false;
   }

   public AnimationUtils(float initialValue, float speed) {
      this(initialValue, speed, Easings.LINEAR);
   }

   public void update(float target) {
      if (this.target != target || !this.flag) {
         this.target = target;
         this.initialValue = this.done;
         this.timeMs = System.nanoTime();
         this.dvalue = 1.0 / this.speed * 2.0;
         this.flag = true;
      }

      if (this.isDone()) {
         this.done = this.target;
         this.flag = false;
      } else {
         double var2 = this.done2();
         float var4 = (float)this.easing.ease(var2);
         this.done = MathHelper.lerp(var4, this.initialValue, this.target);
      }
   }

   private double done2() {
      if (!this.flag) {
         return 1.0;
      }

      long var1 = System.nanoTime();
      double var3 = (var1 - this.timeMs) / 1.0E9;
      return MathHelper.clamp(var3 / this.dvalue, 0.0, 1.0);
   }

   public float getValue() {
      return this.done;
   }

   public void setValue(float value) {
      this.done = value;
      this.target = value;
      this.initialValue = value;
      this.flag = false;
   }

   public float getTarget() {
      return this.target;
   }

   public void setSpeed(float speed) {
      this.speed = speed;
      this.dvalue = 1.0 / speed;
   }

   public void setEasing(Easing easing) {
      this.easing = easing != null ? easing : Easings.LINEAR;
   }

   public boolean isDone() {
      return this.done2() >= 1.0;
   }

   public boolean isAlive() {
      return !this.isDone();
   }
}
