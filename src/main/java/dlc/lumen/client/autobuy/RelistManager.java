package dlc.lumen.client.autobuy;

import dlc.lumen.api.QClient;
import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class RelistManager implements QClient {
   private static final String TEXT = "товары на продаже";
   private static final String TEXT2 = "продаж";
   private static final int INDEX = 10;
   private static final int INDEX2 = 300;
   private RelistManager.State state = RelistManager.State.IDLE;
   private int index = 0;
   private int index2 = 0;
   private int index3 = 0;
   private int index4 = 0;
   private volatile boolean volatileboolean = false;

   public void start() {
      this.volatileboolean = true;
   }

   public boolean isActive() {
      return this.state != RelistManager.State.IDLE;
   }

   public String statusLine() {
      return this.state == RelistManager.State.IDLE ? "Релист: ожидание" : "Релист: снято " + this.index3;
   }

   public synchronized void tick() {
      if (mc.player == null || mc.interactionManager == null) {
         this.state = RelistManager.State.IDLE;
      } else if (this.index > 0) {
         this.index--;
      } else {
         switch (this.state) {
            case IDLE:
               if (!this.volatileboolean) {
                  return;
               }

               this.volatileboolean = false;
               this.index3 = 0;
               this.index4 = 0;
               this.state = this.checkState() ? RelistManager.State.OPEN_LISTINGS : RelistManager.State.OPEN_AUCTION;
               break;
            case OPEN_AUCTION:
               if (mc.currentScreen != null) {
                  mc.player.closeHandledScreen();
                  this.index = 6;
               } else {
                  mc.player.networkHandler.sendChatCommand("ah");
                  this.state = RelistManager.State.WAIT_AUCTION;
                  this.index2 = 60;
               }
               break;
            case WAIT_AUCTION:
               if (this.checkState()) {
                  this.state = RelistManager.State.OPEN_LISTINGS;
                  this.index = 4;
               } else if (--this.index2 <= 0) {
                  this.updateState();
               }
               break;
            case OPEN_LISTINGS:
               if (!this.checkState()) {
                  this.updateState();
                  return;
               }

               GenericContainerScreenHandler var3 = ((GenericContainerScreen)mc.currentScreen).getScreenHandler();
               int var4 = this.resolveInt(var3);
               if (var4 < 0) {
                  this.updateState3("§e[Релист] Эндер-сундук «Товары на продаже» не найден");
                  this.updateState();
                  return;
               }

               this.updateState2(var3.syncId, var4, 0);
               this.state = RelistManager.State.WAIT_LISTINGS;
               this.index2 = 60;
               break;
            case WAIT_LISTINGS:
               if (this.checkState2()) {
                  this.state = RelistManager.State.COLLECT;
                  this.index = 6;
                  this.index4 = 0;
               } else if (--this.index2 <= 0) {
                  this.updateState();
               }
               break;
            case COLLECT:
               if (!this.checkState2()) {
                  this.updateState();
                  return;
               }

               GenericContainerScreenHandler var1 = ((GenericContainerScreen)mc.currentScreen).getScreenHandler();
               int var2 = this.resolveInt2(var1);
               if (var2 < 0) {
                  if (++this.index4 >= 3) {
                     this.updateState();
                  } else {
                     this.index = 4;
                  }
               } else {
                  this.index4 = 0;
                  this.updateState2(var1.syncId, var2, 1);
                  this.index3++;
                  this.index = 10;
                  if (this.index3 >= 300) {
                     this.updateState();
                  }
               }
               break;
            case FINISH:
               if (mc.currentScreen != null) {
                  mc.player.closeHandledScreen();
                  this.index = 6;
               } else {
                  if (this.index3 > 0) {
                     this.updateState3("§a[Релист] Снято лотов: §f" + this.index3 + "§a — перевыставляю");
                     AutoBuyEngine.INSTANCE.seller.triggerRelist();
                  } else {
                     this.updateState3("§7[Релист] Своих лотов из списка нет");
                  }

                  this.state = RelistManager.State.IDLE;
               }
               break;
            default:
               this.state = RelistManager.State.IDLE;
         }
      }
   }

   private void updateState() {
      this.state = RelistManager.State.FINISH;
      this.index = 4;
   }

   private boolean checkState() {
      return mc.currentScreen instanceof GenericContainerScreen var1 && AutoBuyEngine.isAuctionScreen(var1);
   }

   private boolean checkState2() {
      if (mc.currentScreen instanceof GenericContainerScreen var1) {
         String var3 = var1.getTitle().getString().toLowerCase(Locale.ROOT);
         return var3.contains("продаж");
      } else {
         return false;
      }
   }

   private int resolveInt(GenericContainerScreenHandler handler) {
      int var2 = handler.getRows() * 9;

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = handler.getSlot(var3).getStack();
         if (!var4.isEmpty()) {
            String var5 = Registries.ITEM.getId(var4.getItem()).toString();
            if (var5.equals("minecraft:ender_chest") && this.checkCondition(var4, "товары на продаже")) {
               return var3;
            }
         }
      }

      for (int var6 = 0; var6 < var2; var6++) {
         ItemStack var7 = handler.getSlot(var6).getStack();
         if (!var7.isEmpty() && this.checkCondition(var7, "товары на продаже")) {
            return var6;
         }
      }

      return -1;
   }

   private int resolveInt2(GenericContainerScreenHandler handler) {
      int var2 = handler.getRows();
      int var3 = (var2 - 1) * 9;

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = handler.getSlot(var4).getStack();
         if (!var5.isEmpty()) {
            Lot var6 = PriceParser.parseForMatch(var5);
            if (var6 != null) {
               for (ItemEntry var8 : AutoBuyConfig.cfg().entries) {
                  if (ItemMatcher.matches(var8, var6)) {
                     return var4;
                  }
               }
            }
         }
      }

      return -1;
   }

   private boolean checkCondition(ItemStack stack, String needle) {
      String var3 = stack.getName().getString().toLowerCase(Locale.ROOT);
      if (var3.contains(needle)) {
         return true;
      }

      LoreComponent var4 = stack.get(DataComponentTypes.LORE);
      if (var4 != null) {
         for (Text var6 : var4.lines()) {
            if (var6.getString().toLowerCase(Locale.ROOT).contains(needle)) {
               return true;
            }
         }
      }

      return false;
   }

   private void updateState2(int syncId, int slot, int button) {
      if (mc.interactionManager != null && mc.player != null) {
         mc.interactionManager.clickSlot(syncId, slot, button, SlotActionType.PICKUP, mc.player);
      }
   }

   private void updateState3(String text) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(text), false);
      }
   }

   private enum State {
      IDLE,
      OPEN_AUCTION,
      WAIT_AUCTION,
      OPEN_LISTINGS,
      WAIT_LISTINGS,
      COLLECT,
      FINISH;
   }
}
