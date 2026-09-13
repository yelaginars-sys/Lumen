package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.client.modules.Module;

public class NoWeb extends Module {
   public static final NoWeb INSTANCE = new NoWeb();

   public NoWeb() {
      super("NoWeb", "Убирает замедление от паутины", Module.ModuleCategory.MOVEMENT);
   }
}
