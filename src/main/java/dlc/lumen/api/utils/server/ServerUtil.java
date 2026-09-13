package dlc.lumen.api.utils.server;

import dlc.lumen.api.QClient;
import dlc.lumen.mixin.BossBarHudAccessor;
import dlc.lumen.mixin.PlayerListHudAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;

public final class ServerUtil implements QClient {
   public static String header() {
      if (mc.player != null && mc.player.networkHandler != null && mc.inGameHud != null && mc.inGameHud.getPlayerListHud() != null) {
         Text var0 = ((PlayerListHudAccessor)mc.inGameHud.getPlayerListHud()).getHeader();
         return var0 == null ? "" : var0.getString();
      } else {
         return "";
      }
   }

   public static int anarchyNumber() {
      String var0 = header();
      if (var0 != null && var0.contains("Анархия-")) {
         try {
            return Integer.parseInt(var0.split("Анархия-")[1].trim());
         } catch (NumberFormatException var2) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean isPvPZone() {
      if (mc.player != null && mc.inGameHud != null && mc.inGameHud.getBossBarHud() != null) {
         if (!(mc.inGameHud.getBossBarHud() instanceof BossBarHudAccessor var0)) {
            return false;
         } else {
            for (ClientBossBar var2 : var0.lumen$getBossBars().values()) {
               String var3 = var2.getName().getString().toLowerCase();
               if (var3.contains("pvp") || var3.contains("пвп") || var3.contains("дуэль")) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static int pvpTime() {
      if (mc.player != null && mc.inGameHud != null && mc.inGameHud.getBossBarHud() != null) {
         if (!(mc.inGameHud.getBossBarHud() instanceof BossBarHudAccessor var0)) {
            return -1;
         } else {
            if (var0.lumen$getBossBars().isEmpty()) {
               return -1;
            }

            for (ClientBossBar var3 : var0.lumen$getBossBars().values()) {
               String var4 = var3.getName().getString().toLowerCase();
               if (var4.contains("pvp") || var4.contains("пвп")) {
                  Matcher var5 = Pattern.compile("(\\d+):(\\d+)").matcher(var4);
                  if (var5.find()) {
                     return Integer.parseInt(var5.group(1)) * 60 + Integer.parseInt(var5.group(2));
                  }

                  var5 = Pattern.compile("(\\d+)").matcher(var4);
                  if (var5.find()) {
                     return Integer.parseInt(var5.group(1));
                  }
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static String address() {
      if (mc.player != null && mc.player.networkHandler != null) {
         return mc.player.networkHandler.getServerInfo() == null ? "" : mc.player.networkHandler.getServerInfo().address;
      } else {
         return "";
      }
   }

   public static boolean isFuntime() {
      return header().toLowerCase().contains("funtime") || address().toLowerCase().contains("funtime");
   }

   public static boolean isSpookyTime() {
      return header().toLowerCase().contains("spookytime") || address().toLowerCase().contains("spookytime");
   }

   public static boolean isHolyWorld() {
      return header().toLowerCase().contains("holyworld") || address().toLowerCase().contains("holyworld");
   }

   public static boolean isReallyWorld() {
      return header().toLowerCase().contains("reallyworld") || address().toLowerCase().contains("reallyworld");
   }

   @Generated
   private ServerUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
