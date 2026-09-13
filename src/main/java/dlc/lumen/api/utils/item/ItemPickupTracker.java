package dlc.lumen.api.utils.item;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.utils.notification.NotificationManager;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.util.Formatting;

public class ItemPickupTracker implements QClient {
   public static final ItemPickupTracker INSTANCE = new ItemPickupTracker();
   private static final List<String> stringList = List.of(
      "шлем eternity",
      "поножи eternity",
      "ботинки eternity",
      "нагрудник eternity",
      "зелье победителя",
      "зелье черепашей мощи",
      "зелье черепашьей мощи",
      "шлем infinity",
      "нагрудник infinity",
      "поножи infinity",
      "ботинки infinity",
      "меч eternity",
      "меч infinity",
      "сфера цербера",
      "мифическая сфера",
      "взрывная трапка",
      "трапка"
   );

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE) {
         if (mc.world != null && mc.player != null) {
            if (event.getPacket() instanceof ItemPickupAnimationS2CPacket var2) {
               try {
                  Entity var16 = mc.world.getEntityById(var2.getEntityId());
                  Entity var4 = mc.world.getEntityById(var2.getCollectorEntityId());
                  if (var16 instanceof ItemEntity var5 && var4 != null) {
                     ItemStack var6 = var5.getStack();
                     if (var6 == null || var6.isEmpty()) {
                        return;
                     }

                     String var7 = NbtDumpManager.getCustomDumpedName(var6);
                     String var8 = var6.getName().getString();
                     String var9 = Formatting.strip(var8).toLowerCase();
                     boolean var10 = false;
                     String var11 = var8;
                     if (var7 != null) {
                        var10 = true;
                        var11 = var7;
                     } else {
                        for (String var13 : stringList) {
                           if (var9.contains(var13)) {
                              var10 = true;
                              break;
                           }
                        }
                     }

                     if (var10) {
                        String var17 = NbtDumpManager.stripFormatting(var11);
                        if (var4.getId() == mc.player.getId()) {
                           NotificationManager.pushCustom("Вы подобрали " + var17, "?");
                        } else if (var4 instanceof PlayerEntity var18) {
                           String var14 = var18.getName().getString();
                           NotificationManager.pushCustom(var14 + " подобрал " + var17, "?");
                        }
                     }
                  }
               } catch (Throwable var15) {
               }
            }
         }
      }
   }
}
