package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;

public class NoInteract extends Module {
   public static final NoInteract INSTANCE = new NoInteract();
    private final BooleanSetting booleanSetting = new BooleanSetting("Контейнеры", true);
    private final BooleanSetting booleanSetting2 = new BooleanSetting("Стойки для брони", true);
    private final BooleanSetting booleanSetting3 = new BooleanSetting("Все", false);

    private NoInteract() {
       super("NoInteract", "Не открывает контейнеры и игнорирует стойки для брони", Module.ModuleCategory.MISC);
       this.addSettings(this.booleanSetting, this.booleanSetting2, this.booleanSetting3);
    }

    public boolean blocksContainers() {
       return this.isEnable() && this.booleanSetting.isState();
    }

    public boolean blocksArmorStands() {
       return this.isEnable() && this.booleanSetting2.isState();
    }

    public boolean blocksAll() {
       return this.isEnable() && this.booleanSetting3.isState();
    }
}
