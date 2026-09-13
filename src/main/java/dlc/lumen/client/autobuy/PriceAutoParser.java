package dlc.lumen.client.autobuy;

import dlc.lumen.api.QClient;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class PriceAutoParser implements QClient {
   private static final int INDEX = 25;
   private static final int INDEX2 = 6;
   private static final int INDEX3 = 4;
   private static final int INDEX4 = 2;
   private static final int INDEX5 = 14;
   private static final int INDEX6 = 2;
   private PriceAutoParser.State state = PriceAutoParser.State.IDLE;
   private List<ItemEntry> itemEntrys = new ArrayList<>();
   private int index = 0;
   private int index2 = 0;
   private long timestamp = -1L;
   private int index3 = 0;
   private long timestamp2 = 0L;
   private int index4 = 0;
   private int index5 = 0;
   private String text2 = "";
   private long timestamp3 = 0L;
   private int index6 = 0;
   private AuctionSortMode auctionSortMode = null;

   public synchronized void start() {
      if (this.state != PriceAutoParser.State.IDLE) {
         this.stop();
      } else if (mc.player != null) {
         this.itemEntrys = new ArrayList<>();

         for (ItemEntry var2 : AutoBuyConfig.cfg().entries) {
            if (var2.enabled) {
               this.itemEntrys.add(var2);
            }
         }

         if (this.itemEntrys.isEmpty()) {
            this.updateState17("§e[AutoBuy] Нет включённых предметов для парсинга");
         } else {
            this.index = 0;
            this.index2 = 0;
            this.index4 = 0;
            this.index5 = 0;
            this.text2 = "сортировка";
            this.state = PriceAutoParser.State.PRE_OPEN;
            AutoBuyConfig.cfg().enabled = false;
            this.updateState17("§e[AutoBuy] Парсинг цен: " + this.itemEntrys.size() + " предмет(ов)");
         }
      }
   }

   public synchronized void stop() {
      this.state = PriceAutoParser.State.IDLE;
      this.itemEntrys.clear();
      this.index = 0;
      this.text2 = "";
      this.auctionSortMode = null;
   }

   public boolean isActive() {
      return this.state != PriceAutoParser.State.IDLE;
   }

   public String statusLine() {
      if (this.state == PriceAutoParser.State.IDLE) {
         return "Парсер цен: ожидание";
      }

      int var1 = Math.min(this.index + 1, Math.max(1, this.itemEntrys.size()));
      return "Парсер: " + var1 + "/" + this.itemEntrys.size() + " — " + this.text2;
   }

   public synchronized void tick() {
      if (this.state != PriceAutoParser.State.IDLE) {
         if (mc.player == null) {
            this.stop();
         } else if (this.index2 > 0) {
            this.index2--;
         } else {
            switch (this.state) {
               case PRE_OPEN:
                  this.updateState();
                  break;
               case PRE_WAIT:
                  this.updateState2();
                  break;
               case PRE_SORT:
                  this.updateState3();
                  break;
               case NEXT:
                  this.updateState8();
                  break;
               case WAIT_SCREEN:
                  this.updateState9();
                  break;
               case SCAN:
                  this.updateState11();
                  break;
               case WAIT_PAGE:
                  this.updateState12();
                  break;
               case ADVANCE:
                  this.updateState13();
                  break;
               case POST_OPEN:
                  this.updateState4();
                  break;
               case POST_WAIT:
                  this.updateState5();
                  break;
               case POST_SORT:
                  this.updateState6();
            }
         }
      }
   }

   private void updateState() {
      if (this.checkState()) {
         this.updateState15(AuctionSortMode.CHEAPEST_PER_UNIT, 4, PriceAutoParser.State.PRE_SORT);
      } else if (mc.currentScreen != null) {
         mc.player.closeHandledScreen();
         this.index2 = 6;
      } else {
         mc.player.networkHandler.sendChatCommand("ah");
         this.state = PriceAutoParser.State.PRE_WAIT;
         this.index4 = 60;
      }
   }

   private void updateState2() {
      if (this.checkState()) {
         this.updateState15(AuctionSortMode.CHEAPEST_PER_UNIT, 4, PriceAutoParser.State.PRE_SORT);
      } else if (--this.index4 <= 0) {
         this.state = PriceAutoParser.State.NEXT;
         this.index2 = 8;
      }
   }

   private void updateState3() {
      if (!this.checkState()) {
         this.state = PriceAutoParser.State.NEXT;
         this.index2 = 8;
      } else {
         GenericContainerScreenHandler var1 = this.computeGenericContainerScreenHandler();
         int var2 = AuctionSortMode.findButton(var1);
         if (var2 < 0) {
            this.updateState17("§e[AutoBuy] Воронка «Сортировка» не найдена");
            this.state = PriceAutoParser.State.NEXT;
            this.index2 = 8;
         } else {
            AuctionSortMode var3 = AuctionSortMode.current(var1.getSlot(var2).getStack());
            if (this.auctionSortMode != null && var3 == this.auctionSortMode) {
               this.state = PriceAutoParser.State.NEXT;
               this.index2 = 8;
            } else if (this.index5 <= 0) {
               this.state = PriceAutoParser.State.NEXT;
               this.index2 = 8;
            } else {
               this.updateState16(var1.syncId, var2);
               this.index5--;
               this.index2 = 6;
            }
         }
      }
   }

   private void updateState4() {
      this.text2 = "сортировка";
      if (this.checkState()) {
         this.updateState15(AuctionSortMode.NEWEST, 2, PriceAutoParser.State.POST_SORT);
      } else if (mc.currentScreen != null) {
         mc.player.closeHandledScreen();
         this.index2 = 6;
      } else {
         mc.player.networkHandler.sendChatCommand("ah");
         this.state = PriceAutoParser.State.POST_WAIT;
         this.index4 = 60;
      }
   }

   private void updateState5() {
      if (this.checkState()) {
         this.updateState15(AuctionSortMode.NEWEST, 2, PriceAutoParser.State.POST_SORT);
      } else if (--this.index4 <= 0) {
         this.updateState7();
      }
   }

   private void updateState6() {
      if (!this.checkState()) {
         this.updateState7();
      } else {
         GenericContainerScreenHandler var1 = this.computeGenericContainerScreenHandler();
         int var2 = AuctionSortMode.findButton(var1);
         if (var2 < 0) {
            this.updateState7();
         } else {
            AuctionSortMode var3 = AuctionSortMode.current(var1.getSlot(var2).getStack());
            if (this.auctionSortMode != null && var3 == this.auctionSortMode) {
               this.updateState7();
            } else if (this.index5 <= 0) {
               this.updateState7();
            } else {
               this.updateState16(var1.syncId, var2);
               this.index5--;
               this.index2 = 6;
            }
         }
      }
   }

   private void updateState7() {
      this.updateState17("§a[AutoBuy] Парсинг окончен");
      AutoBuyConfig.save();
      this.stop();
   }

   private void updateState8() {
      if (this.index >= this.itemEntrys.size()) {
         this.state = PriceAutoParser.State.POST_OPEN;
         this.index4 = 60;
         this.index2 = 4;
      } else {
         ItemEntry var1 = this.itemEntrys.get(this.index);
         String var2 = var1.displayName != null && !var1.displayName.isBlank() ? var1.displayName : var1.itemId;
         if (var1.loreKeyword != null && !var1.loreKeyword.isBlank()) {
            var2 = var2 + " [" + var1.loreKeyword + "]";
         }

         this.text2 = var2;
         this.timestamp = -1L;
         this.index3 = 0;
         this.timestamp2 = 0L;
         if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            this.index2 = 4;
         } else {
            String var3 = var1.query().replaceAll("[«»\"']", "").trim();
            mc.player.networkHandler.sendChatCommand("ah search " + var3);
            this.state = PriceAutoParser.State.WAIT_SCREEN;
            this.index2 = 0;
            this.index4 = 60;
         }
      }
   }

   private void updateState9() {
      this.index4--;
      if (this.index4 <= 0) {
         this.updateState14();
      } else {
         if (mc.currentScreen instanceof GenericContainerScreen var1 && AutoBuyEngine.isAuctionScreen(var1)) {
            this.updateState10();
         }
      }
   }

   private void updateState10() {
      this.state = PriceAutoParser.State.SCAN;
      this.index4 = 14;
      this.timestamp3 = 0L;
      this.index6 = 0;
   }

   private void updateState11() {
      if (mc.currentScreen instanceof GenericContainerScreen var1 && AutoBuyEngine.isAuctionScreen(var1)) {
         GenericContainerScreenHandler var12 = var1.getScreenHandler();
         int var3 = var12.getRows();
         int var4 = (var3 - 1) * 9;
         ItemEntry var5 = this.itemEntrys.get(this.index);
         int var6 = 0;

         for (int var7 = 0; var7 < var4; var7++) {
            ItemStack var8 = var12.getSlot(var7).getStack();
            if (!var8.isEmpty()) {
               var6++;
            }

            Lot var9 = PriceParser.parse(var7, var8);
            if (var9 != null && !var9.barrier && ItemMatcher.matches(var5, var9) && (this.timestamp < 0L || var9.unitPrice < this.timestamp)) {
               this.timestamp = var9.unitPrice;
            }
         }

         long var13 = this.resolveLong(var12, var4);
         if (var13 == this.timestamp3) {
            this.index6++;
         } else {
            this.index6 = 0;
            this.timestamp3 = var13;
         }

         this.index4--;
         boolean var14 = var6 > 0 && this.index6 >= 2;
         if (var14 || this.index4 <= 0) {
            boolean var10 = this.checkCondition(var5) && this.timestamp < 0L && this.index3 + 1 < 25;
            if (!var10) {
               this.updateState14();
            } else {
               int var11 = this.resolveInt(var12, var3);
               if (var11 < 0) {
                  this.updateState14();
               } else {
                  this.timestamp2 = this.resolveLong(var12, var4);
                  this.updateState16(var12.syncId, var11);
                  this.state = PriceAutoParser.State.WAIT_PAGE;
                  this.index2 = 3;
                  this.index4 = 30;
                  this.index3++;
               }
            }
         }
      } else {
         this.updateState14();
      }
   }

   private void updateState12() {
      if (mc.currentScreen instanceof GenericContainerScreen var1 && AutoBuyEngine.isAuctionScreen(var1)) {
         GenericContainerScreenHandler var5 = var1.getScreenHandler();
         int var3 = var5.getRows();
         int var4 = (var3 - 1) * 9;
         if (this.resolveLong(var5, var4) != this.timestamp2) {
            this.updateState10();
         } else {
            this.index4--;
            if (this.index4 <= 0) {
               this.updateState14();
            }
         }
      } else {
         this.updateState14();
      }
   }

   private void updateState13() {
      if (mc.currentScreen != null) {
         mc.player.closeHandledScreen();
         this.index2 = 4;
      } else {
         this.index++;
         this.state = PriceAutoParser.State.NEXT;
         this.index2 = 4;
      }
   }

   private void updateState14() {
      ItemEntry var1 = this.itemEntrys.get(this.index);
      AutoBuyConfig var2 = AutoBuyConfig.cfg();
      int var3 = Math.max(1, Math.min(100, var2.parsePercent));
      long var4 = this.timestamp > 0L ? this.timestamp * var3 / 100L : 0L;
      var1.maxUnitPrice = var4;
      if (this.timestamp > 0L) {
         var1.marketPrice = this.timestamp;
      }

      AutoBuyConfig.save();
      this.state = PriceAutoParser.State.ADVANCE;
      this.index2 = 2;
   }

   private void updateState15(AuctionSortMode target, int fallbackClicks, PriceAutoParser.State nextState) {
      this.auctionSortMode = target;
      this.index5 = fallbackClicks;
      AuctionSortMode var4 = AuctionSortMode.current(this.computeGenericContainerScreenHandler());
      if (var4 != null) {
         this.index5 = var4.clicksTo(target);
      }

      this.state = nextState;
      this.index2 = 6;
   }

   private boolean checkState() {
      return mc.currentScreen instanceof GenericContainerScreen var1 && AutoBuyEngine.isAuctionScreen(var1);
   }

   private GenericContainerScreenHandler computeGenericContainerScreenHandler() {
      return ((GenericContainerScreen)mc.currentScreen).getScreenHandler();
   }

   private boolean checkCondition(ItemEntry e) {
      if (e != null && e.itemId != null) {
         String var2 = e.itemId;
         return var2.endsWith("_helmet") || var2.endsWith("_chestplate") || var2.endsWith("_leggings") || var2.endsWith("_boots");
      } else {
         return false;
      }
   }

   private int resolveInt(GenericContainerScreenHandler handler, int rows) {
      int var3 = (rows - 1) * 9;
      int var4 = rows * 9;
      int var5 = -1;

      for (int var6 = var3; var6 < var4; var6++) {
         ItemStack var7 = handler.getSlot(var6).getStack();
         if (!var7.isEmpty()) {
            String var8 = Registries.ITEM.getId(var7.getItem()).toString();
            if (var8.equals("minecraft:lime_dye") || var8.equals("minecraft:green_dye")) {
               var5 = var6;
            }
         }
      }

      return var5;
   }

   private long resolveLong(GenericContainerScreenHandler handler, int count) {
      long var3 = 1L;

      for (int var5 = 0; var5 < count; var5++) {
         ItemStack var6 = handler.getSlot(var5).getStack();
         var3 = var3 * 31L + (var6.isEmpty() ? 0L : Registries.ITEM.getRawId(var6.getItem()) * 113L + var6.getCount());
      }

      return var3;
   }

   private void updateState16(int syncId, int slot) {
      if (mc.interactionManager != null && mc.player != null) {
         mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
      }
   }

   private void updateState17(String text) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(text), false);
      }
   }

   private enum State {
      IDLE,
      PRE_OPEN,
      PRE_WAIT,
      PRE_SORT,
      NEXT,
      WAIT_SCREEN,
      SCAN,
      WAIT_PAGE,
      ADVANCE,
      POST_OPEN,
      POST_WAIT,
      POST_SORT;
   }
}
