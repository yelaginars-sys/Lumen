package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.client.modules.Module;
import java.util.Random;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class PacketCriticals extends Module {
   public static final PacketCriticals INSTANCE = new PacketCriticals();
   private static final Random RANDOM = new Random();
   private int index = 0;
   private int index2 = 0;

   public PacketCriticals() {
      super("PacketCriticals", "Бьёт критами с обходом античитов", Module.ModuleCategory.COMBAT);
   }

   @EventLink
   public void onAttack(EventAttackEntity event) {
      if (mc.player != null && mc.world != null) {
         if (!mc.player.isOnGround()) {
            if (mc.player.hurtTime <= 0) {
               double var2 = mc.player.getX();
               double var4 = mc.player.getY();
               double var6 = mc.player.getZ();
               double var8 = mc.player.getYaw();
               double var10 = mc.player.getPitch();
               this.index = RANDOM.nextInt(7);
               switch (this.index) {
                  case 0:
                     double var20 = 0.001 + RANDOM.nextDouble() * 0.005;
                     this.updateState(var2, var4 + var20, var6, false);
                     this.updateState(var2, var4, var6, false);
                     break;
                  case 1:
                     double var19 = 0.0025 + RANDOM.nextDouble() * 0.003;
                     this.updateState(var2, var4 + var19, var6, false);
                     this.updateState(var2, var4 - 0.001, var6, false);
                     this.updateState(var2, var4, var6, false);
                     break;
                  case 2:
                     double var18 = -0.1 + RANDOM.nextDouble() * 0.2;
                     this.updateState2(var2, var4 + 0.002, var6, var8 + 1.0, var10 + var18, false);
                     this.updateState2(var2, var4 - 0.001, var6, var8 - 1.0, var10 - var18, false);
                     this.updateState2(var2, var4, var6, var8, var10, false);
                     break;
                  case 3:
                     for (int var17 = 0; var17 < 3; var17++) {
                        double var13 = (0.001 + RANDOM.nextDouble() * 0.002) * (var17 + 1);
                        this.updateState(var2, var4 + var13, var6, false);
                     }

                     this.updateState(var2, var4, var6, false);
                     break;
                  case 4:
                     double var16 = 7.0E-4;

                     for (int var21 = 0; var21 < 6; var21++) {
                        this.updateState(var2, var4 + var16 * var21, var6, false);
                     }

                     this.updateState(var2, var4, var6, false);
                     break;
                  case 5:
                     this.updateState(var2, var4 + 0.001, var6, true);
                     this.updateState(var2, var4 + 0.0015, var6, false);
                     this.updateState(var2, var4, var6, true);
                     this.updateState(var2, var4, var6, false);
                     break;
                  case 6:
                     double var12 = (RANDOM.nextDouble() - 0.5) * 1.0E-4;
                     double var14 = (RANDOM.nextDouble() - 0.5) * 1.0E-4;
                     this.updateState(var2 + var12, var4 + 0.0015, var6 + var14, false);
                     this.updateState(var2 - var12, var4 + 5.0E-4, var6 - var14, false);
                     this.updateState(var2, var4, var6, false);
               }

               this.index2++;
               if (this.index2 % 10 == 0) {
                  this.index = RANDOM.nextInt(7);
               }
            }
         }
      }
   }

   private void updateState(double x, double y, double z, boolean onGround) {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendPacket(new PositionAndOnGround(x, y, z, onGround, false));
      }
   }

   private void updateState2(double x, double y, double z, double yaw, double pitch, boolean onGround) {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendPacket(new Full(x, y, z, (float)yaw, (float)pitch, onGround, false));
      }
   }
}
