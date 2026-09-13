package dlc.lumen.api.utils.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class PlayerIntersectionUtil {
   public static void startFallFlying() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.player.networkHandler != null) {
         var0.player.networkHandler.sendPacket(new ClientCommandC2SPacket(var0.player, Mode.START_FALL_FLYING));
      }
   }
}
