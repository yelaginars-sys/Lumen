package dlc.lumen.api.utils.notification;

import dlc.lumen.api.utils.client.ClientSoundPlayer;
import dlc.lumen.client.modules.impl.misc.ClientSounds;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager {
   public static final long DURATION_MS = 2500L;
   private static final long timeMs = 4000L;
   private static final long timeMs2 = System.currentTimeMillis();
   private static final List<NotificationManager.Entry> entryList = new ArrayList<>();
   private static boolean flag;

   public static boolean isMuted() {
      return flag;
   }

   public static void setMuted(boolean value) {
      flag = value;
   }

   public static void push(String moduleName, String categoryIcon, boolean enabled) {
      if (!flag && moduleName != null && !moduleName.isEmpty()) {
         entryList.add(new NotificationManager.Entry(moduleName, categoryIcon, enabled, null, System.currentTimeMillis()));
         handleEnabled(enabled);
      }
   }

   public static void pushCustom(String text, String categoryIcon) {
      if (text != null && !text.isEmpty()) {
         entryList.add(new NotificationManager.Entry(text, categoryIcon, false, text, System.currentTimeMillis()));
      }
   }

   public static List<NotificationManager.Entry> getActive() {
      long var0 = System.currentTimeMillis();
      Iterator var2 = entryList.iterator();

      while (var2.hasNext()) {
         NotificationManager.Entry var3 = (NotificationManager.Entry)var2.next();
         if (var0 - var3.startTime > 2500L) {
            var2.remove();
         }
      }

      return entryList;
   }

   private static void handleEnabled(boolean enabled) {
      if (System.currentTimeMillis() - timeMs2 >= 4000L) {
         ClientSounds var1 = ClientSounds.INSTANCE;
         if (var1 != null && var1.isEnable()) {
            String var2 = var1.stateSounds.getCurrent();
            if (!"Нет".equals(var2)) {
               float var3 = enabled ? 1.0F : 0.95F;
               ClientSoundPlayer.playSound(var2 + ".wav", var1.volume.get() / var1.volume.getMax(), var3);
            }
         }
      }
   }

   public static class Entry {
      public final String moduleName;
      public final String categoryIcon;
      public final boolean enabled;
      public final String customText;
      public final long startTime;

      public Entry(String moduleName, String categoryIcon, boolean enabled, String customText, long startTime) {
         this.moduleName = moduleName;
         this.categoryIcon = categoryIcon;
         this.enabled = enabled;
         this.customText = customText;
         this.startTime = startTime;
      }

      public boolean isCustom() {
         return this.customText != null && !this.customText.isEmpty();
      }
   }
}
