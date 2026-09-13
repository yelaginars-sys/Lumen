package dlc.lumen.api.storages.implement;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventLook;
import dlc.lumen.api.events.implement.EventRotation;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class FreeLookStorage implements QClient {
   private static boolean flag;
   private static float fvalue;
   private static float fvalue2;

   public FreeLookStorage() {
      EventInvoker.register(this);
   }

   public static boolean isActive() {
      return flag;
   }

   @EventLink
   public void onLook(EventLook event) {
      if (flag) {
         this.handleTargetYaw(event.getYaw(), event.getPitch());
         event.cancel();
      }
   }

   @EventLink
   public void onRotation(EventRotation event) {
      if (flag) {
         event.setYaw(fvalue);
         event.setPitch(fvalue2);
      } else {
         fvalue = event.getYaw();
         fvalue2 = event.getPitch();
      }
   }

   private void handleTargetYaw(double targetYaw, double targetPitch) {
      fvalue2 = MathHelper.clamp((float)(fvalue2 + targetPitch * 0.15), -90.0F, 90.0F);
      fvalue = (float)(fvalue + targetYaw * 0.15);
   }

   @Generated
   public static void setActive(boolean active) {
      flag = active;
   }

   @Generated
   public static float getFreeYaw() {
      return fvalue;
   }

   @Generated
   public static float getFreePitch() {
      return fvalue2;
   }

   @Generated
   public static void setFreeYaw(float freeYaw) {
      fvalue = freeYaw;
   }

   @Generated
   public static void setFreePitch(float freePitch) {
      fvalue2 = freePitch;
   }
}
