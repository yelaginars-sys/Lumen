package dlc.lumen.client.bots.core.behavior;

import net.minecraft.item.map.MapState.UpdateData;

public interface CaptchaStrategy {
   CaptchaStrategy NONE = new CaptchaStrategy() {
      @Override
      public void onJoin() {
      }

      @Override
      public void onMapData(int mapId, UpdateData data) {
      }

      @Override
      public void onText(String text) {
      }
   };

   void onJoin();

   void onMapData(int var1, UpdateData var2);

   void onText(String var1);

   default void onFrameSpawn(int entityId, double x, double y, double z, int facing) {
   }

   default void onFrameItem(int entityId, int mapId) {
   }

   default void onFrameRotation(int entityId, int rotation) {
   }

   default void onLook(double x, double y, double z, float yaw) {
   }
}
