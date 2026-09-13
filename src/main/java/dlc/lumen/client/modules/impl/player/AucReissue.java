package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.server.ServerUtil;
import dlc.lumen.client.modules.Module;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class AucReissue extends Module {
   public static final AucReissue INSTANCE = new AucReissue();
   private boolean flag;
   private int index;

   public AucReissue() {
      super("Auc Reissue", "Автоматически перевыставляет предметы на аукционе", Module.ModuleCategory.PLAYER);
   }

   @Override
   public void onEnable() {
      this.flag = false;
      this.index = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.index = 0;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!ServerUtil.isPvPZone()
            && ServerUtil.anarchyNumber() != -1
            && mc.player.age >= 220
            && !mc.player.getItemCooldownManager().isCoolingDown(Items.CLOCK.getDefaultStack())) {
            if (this.index > 0) {
               this.index--;
            } else {
               if (mc.currentScreen instanceof HandledScreen var2) {
                  if (var2 instanceof GenericContainerScreen) {
                     String var4 = var2.getTitle().getString();
                     if (mc.player.age % 5 == 0) {
                        if (var4.matches(".*А.*у.*к.*ц.*и.*о.*н.*")) {
                           mc.interactionManager.clickSlot(var2.getScreenHandler().syncId, 46, 1, SlotActionType.PICKUP, mc.player);
                        } else if (var4.matches(".*Х.*р.*а.*н.*и.*л.*и.*щ.*е.*")) {
                           mc.interactionManager.clickSlot(var2.getScreenHandler().syncId, 52, 1, SlotActionType.PICKUP, mc.player);
                        }
                     }
                  } else if (mc.player.age % 20 == 0) {
                     mc.player.networkHandler.sendChatCommand("ah");
                  }
               } else if (mc.player.age % 20 == 0) {
                  mc.player.networkHandler.sendChatCommand("ah");
               }

               if (this.flag && mc.currentScreen instanceof GenericContainerScreen) {
                  mc.player.closeHandledScreen();
                  this.flag = false;
               }
            }
         } else {
            if (this.flag && mc.currentScreen instanceof GenericContainerScreen) {
               mc.player.closeHandledScreen();
               this.flag = false;
            }
         }
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE && mc.player != null && mc.world != null) {
         if (!ServerUtil.isPvPZone() && ServerUtil.anarchyNumber() != -1 && mc.player.age >= 220) {
            if (event.getPacket() instanceof GameMessageS2CPacket var2) {
               String var6 = var2.content().getString();
               if (var6.equals("Данная команда недоступна в режиме AFK")) {
                  this.index = 10;
               }

               if (var6.equals("[☃] В хранилище отсутствуют предметы для перевыставления.")) {
                  mc.player.sendMessage(Text.literal("Авто-выключение: в хранилище отсутствуют предметы для перевыставления"), false);
                  this.setEnabled(false);
               }

               if (var6.contains("[☃] Предметы успешно перевыставлены ") || var6.contains("[✔] Предметы успешно перевыставлены!")) {
                  mc.player.getItemCooldownManager().set(Items.CLOCK.getDefaultStack(), 1200);
                  this.flag = true;
               }

               if (var6.contains("[☃] Вы можете переставлять предметы раз в минуту! Подождите ")) {
                  try {
                     int var4 = Integer.parseInt(var6.replaceAll(".*Подождите (\\d+) сек\\..*", "$1"));
                     mc.player.getItemCooldownManager().set(Items.CLOCK.getDefaultStack(), var4 * 20 + 20);
                  } catch (NumberFormatException var5) {
                  }

                  this.flag = true;
               }
            }
         }
      }
   }
}
