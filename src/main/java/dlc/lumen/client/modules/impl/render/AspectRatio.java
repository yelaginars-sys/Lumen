package dlc.lumen.client.modules.impl.render;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;

public class AspectRatio extends Module {
   public static AspectRatio INSTANCE = new AspectRatio();
   public final FloatSetting aspect = new FloatSetting("Aspect", 1.78F, 0.5F, 3.0F, 0.01F);

   public AspectRatio() {
      super("AspectRatio", "Кастомное соотношение сторон экрана", Module.ModuleCategory.RENDER);
      this.addSettings(this.aspect);
   }

   public float getAspect() {
      return this.aspect.get();
   }
}
