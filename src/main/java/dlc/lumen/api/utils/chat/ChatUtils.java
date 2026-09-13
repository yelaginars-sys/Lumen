package dlc.lumen.api.utils.chat;

import dlc.lumen.api.utils.color.ColorUtils;
import java.awt.Color;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public final class ChatUtils {
   public static void sendMessage(Object message) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.player == null) {
         System.out.println("[lumen] " + message);
      } else {
         var1.player.sendMessage(getMessage(message), false);
      }
   }

   public static void sendMessage(Object message, int ttlTicks) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.player == null) {
         System.out.println("[lumen] " + message);
      } else {
         MutableText var3 = getMessage(message);
         var2.player.sendMessage(var3, false);
         if (ttlTicks > 0 && var2.inGameHud != null) {
            ((IChatHud)var2.inGameHud.getChatHud()).lumen$scheduleIrcRemoval(var3.getString(), var2.inGameHud.getTicks() + ttlTicks);
         }
      }
   }

   private static MutableText getMessage(Object message) {
      MutableText var1 = Text.literal("");
      String var2 = "lumen";

      for (int var3 = 0; var3 < var2.length(); var3++) {
         var1.append(
            Text.literal(String.valueOf(var2.charAt(var3)))
               .setStyle(
                  Style.EMPTY
                     .withBold(true)
                     .withColor(TextColor.fromRgb(ColorUtils.gradient(ColorUtils.getThemeColor(0), ColorUtils.getThemeColor(90), (float)var3 / var2.length())))
               )
         );
      }

      var1.append(Text.literal(" ⇨ ").setStyle(Style.EMPTY.withBold(false).withColor(TextColor.fromRgb(new Color(200, 200, 200).getRGB()))));
      var1.append(Text.literal(String.valueOf(message)).setStyle(Style.EMPTY.withBold(false).withColor(TextColor.fromRgb(new Color(200, 200, 200).getRGB()))));
      return var1;
   }

   @Generated
   private ChatUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
