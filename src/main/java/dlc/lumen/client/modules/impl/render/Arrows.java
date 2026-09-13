package dlc.lumen.client.modules.impl.render;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;

public class Arrows extends Module {
   public static final Arrows INSTANCE = new Arrows();
   private final BooleanSetting booleanSetting = new BooleanSetting("Показывать вне мира", true);

   public Arrows() {
      super("Arrows", "Стрелки участников пати", Module.ModuleCategory.RENDER);
      this.addSettings(this.booleanSetting);
   }

   public boolean showsCrossWorld() {
      return this.booleanSetting.isState();
   }
}
