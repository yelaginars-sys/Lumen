package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.Module;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;

public class Sprint extends Module {
   public static Sprint INSTANCE = new Sprint();
   private static final MinecraftClient MINECRAFT_CLIENT = MinecraftClient.getInstance();
   private static boolean sprinting2;
   private static long time2 = 0L;
   private static int index = 0;
   private static boolean flag = false;

   public Sprint() {
      super("Sprint", "Автоматический бег", Module.ModuleCategory.MOVEMENT);
   }

   @Override
   public void onEnable() {
      helper4();
      sprinting2 = true;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      helper4();
      sprinting2 = false;
      this.helper();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.options != null) {
         if (index <= 0 && System.currentTimeMillis() >= time2 && sprinting2) {
            mc.options.sprintKey.setPressed(true);
         } else {
            this.helper();
         }
      } else {
         this.helper();
      }
   }

   public boolean shouldKeepSprintInWater() {
      return false;
   }

   public static void pushPause(long delayMs) {
      flag = flag | helper3();
      index++;
      time2 = Math.max(time2, System.currentTimeMillis() + Math.max(0L, delayMs));
      sprinting2 = false;
      helper2();
   }

   public static void popPause() {
      if (index > 0) {
         index--;
      }

      if (index <= 0) {
         time2 = 0L;
         sprinting2 = flag;
         flag = false;
      }
   }

   private void helper() {
      helper2();
   }

   private static void helper2() {
      if (MINECRAFT_CLIENT.options != null) {
         MINECRAFT_CLIENT.options.sprintKey.setPressed(false);
      }

      if (MINECRAFT_CLIENT.player != null) {
         MINECRAFT_CLIENT.player.setSprinting(false);
      }
   }

   private static boolean helper3() {
      return MINECRAFT_CLIENT.player != null && MINECRAFT_CLIENT.player.isSprinting()
         ? true
         : ModuleClass.sprint != null && ModuleClass.sprint.isEnable() && sprinting2;
   }

   private static void helper4() {
      index = 0;
      flag = false;
      time2 = 0L;
   }

   @Generated
   public static boolean isSprinting() {
      return sprinting2;
   }

   @Generated
   public static void setSprinting(boolean sprinting) {
      sprinting2 = sprinting;
   }

   @Generated
   public static long getTime() {
      return time2;
   }

   @Generated
   public static void setTime(long time) {
      time2 = time;
   }
}
