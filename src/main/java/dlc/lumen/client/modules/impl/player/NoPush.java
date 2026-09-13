package dlc.lumen.client.modules.impl.player;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import lombok.Generated;

public class NoPush extends Module {
   public static NoPush INSTANCE = new NoPush();
   private ListSetting collisionList2 = new ListSetting(
      "Коллизия", new BooleanSetting("Блоки", true), new BooleanSetting("Вода", false), new BooleanSetting("Удочик", true), new BooleanSetting("Игроки", true)
   );

   public NoPush() {
      super("NoPush", "Отключает коллизию", Module.ModuleCategory.MISC);
      this.addSettings(this.collisionList2);
   }

   @Generated
   public ListSetting getCollisionList() {
      return this.collisionList2;
   }

   @Generated
   public void setCollisionList(ListSetting collisionList) {
      this.collisionList2 = collisionList;
   }
}
