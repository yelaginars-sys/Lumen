package dlc.lumen.client.autoset;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.autobuy.AutoBuyConfig;
import dlc.lumen.client.autobuy.ItemEntry;
import dlc.lumen.client.autobuy.ItemMatcher;
import dlc.lumen.client.autobuy.Lot;
import dlc.lumen.client.autobuy.PriceParser;
import dlc.lumen.client.autobuy.PurchaseRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public final class AutoSetCollector implements QClient {
   public static final AutoSetCollector INSTANCE = new AutoSetCollector();
   private static final int INDEX = 40;
   private static final int INDEX2 = 70;
   private static final int INDEX3 = 50;
   private static final int INDEX4 = 35;
   private static final int INDEX5 = 5;
   private static final int INDEX6 = 3;
   private final List<AutoSetCollector.Task> items = new ArrayList<>();
   private boolean running;
   private int index;
   private AutoSetCollector.Stage stage = AutoSetCollector.Stage.SEARCH;
   private int index2;
   private int index3;
   private int index4;
   private int index5;
   private Lot lot2;
   private int index6;
   private int index7;

   private AutoSetCollector() {
   }

   public boolean isRunning() {
      return this.running;
   }

   public String statusLine() {
      if (!this.running) {
         return "";
      }

      AutoSetCollector.Task var1 = this.computeTask();
      if (var1 == null) {
         return "завершение...";
      }

      int var2 = mc.player == null ? var1.targetCount : Math.max(0, this.resolveInt3(var1));
      return this.index + 1 + "/" + this.items.size() + " · " + var1.item.name() + " (нужно ещё " + var2 + ")";
   }

   public boolean start(AutoSetKit kit) {
      if (!this.running && kit != null && mc.player != null) {
         this.items.clear();

         for (AutoSetKit.Entry var3 : kit.entries) {
            AutoSetItem var4 = AutoSetKit.resolve(var3);
            if (var4 != null && var3.count > 0) {
               int var5 = "armor".equals(var4.group) ? 40 : 0;
               int var6 = Math.max(var5, var3.minDurabilityPercent);
               this.items.add(new AutoSetCollector.Task(var4, var6, Math.max(0L, var3.maxUnitPrice), var3.count));
            }
         }

         if (this.items.isEmpty()) {
            return false;
         }

         this.running = true;
         this.index = 0;
         this.updateState6();
         this.updateState10("§a[AutoSet] Сбор начат: §f" + this.items.size() + " позиций");
         return true;
      } else {
         return false;
      }
   }

   public void stop() {
      if (this.running) {
         this.running = false;
         if (mc.player != null && mc.currentScreen instanceof HandledScreen) {
            mc.player.closeHandledScreen();
         }

         int var1 = 0;

         for (AutoSetCollector.Task var3 : this.items) {
            var1 += var3.bought;
         }

         this.updateState10("§e[AutoSet] Сбор остановлен. Куплено: §f" + var1 + " шт.");
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (this.running && mc.player != null && mc.interactionManager != null) {
         if (this.index2 > 0) {
            this.index2--;
         } else {
            AutoSetCollector.Task var2 = this.computeTask();
            if (var2 == null) {
               this.updateState8();
            } else if (this.resolveInt3(var2) <= 0) {
               this.updateState10("§a[AutoSet] ✓ §f" + var2.item.name() + " §7— набрано (" + var2.targetCount + ")");
               this.updateState7();
            } else {
               switch (this.stage) {
                  case SEARCH:
                     this.updateState(var2);
                     break;
                  case SCAN:
                     this.updateState2(var2);
                     break;
                  case CONFIRM:
                     this.updateState3(var2);
                     break;
                  case WATCH:
                     this.updateState5(var2);
               }
            }
         }
      }
   }

   private void updateState(AutoSetCollector.Task task) {
      if (mc.currentScreen != null) {
         mc.player.closeHandledScreen();
         this.index2 = 5;
      } else {
         mc.player.networkHandler.sendChatCommand("ah search " + task.item.searchName());
         this.stage = AutoSetCollector.Stage.SCAN;
         this.index3 = 0;
         this.index4 = 1;
         this.index2 = 5;
      }
   }

   private void updateState2(AutoSetCollector.Task task) {
      if (mc.currentScreen instanceof HandledScreen var3 && this.checkCondition2(var3)) {
         ScreenHandler var4 = var3.getScreenHandler();
         int var5 = this.resolveInt(var4);
         int var6 = var5 > 9 ? var5 - 9 : var5;
         int var7 = this.resolveInt3(task);
         if (var7 > 0) {
            Lot var8 = null;
            Lot var9 = null;

            for (int var10 = 0; var10 < var6; var10++) {
               ItemStack var11 = var4.getSlot(var10).getStack();
               if (!var11.isEmpty()) {
                  Lot var12 = PriceParser.parse(var10, var11);
                  if (var12 != null && !var12.barrier && this.checkCondition(task, var12)) {
                     if (task.wholeStack && var12.count > var7) {
                        if (var9 == null || var12.unitPrice < var9.unitPrice) {
                           var9 = var12;
                        }
                     } else if (var8 == null || var12.unitPrice < var8.unitPrice) {
                        var8 = var12;
                     }
                  }
               }
            }

            boolean var13 = task.wholeStack && var8 != null;
            if (var8 == null) {
               var8 = var9;
            }

            if (var8 != null) {
               this.lot2 = var8;
               mc.interactionManager.clickSlot(var4.syncId, var8.slot, var13 ? 0 : 1, SlotActionType.PICKUP, mc.player);
               this.stage = AutoSetCollector.Stage.CONFIRM;
               this.index5 = 0;
               this.index2 = 5;
            } else {
               if (++this.index3 > 70) {
                  if (this.index4 < 3 && this.checkCondition3(var4)) {
                     this.index4++;
                     this.index3 = 0;
                     this.index2 = 10;
                  } else {
                     this.updateState10(
                        "§e[AutoSet] "
                           + task.item.name()
                           + ": подходящих лотов нет (в инвентаре "
                           + this.resolveInt2(task)
                           + "/"
                           + task.targetCount
                           + ")"
                     );
                     this.updateState7();
                  }
               }
            }
         }
      } else {
         if (++this.index3 > 70) {
            this.updateState10("§e[AutoSet] " + task.item.name() + ": аукцион не открылся, пропуск");
            this.updateState7();
         }
      }
   }

   private void updateState3(AutoSetCollector.Task task) {
      if (mc.currentScreen instanceof HandledScreen var3 && this.checkCondition4(var3)) {
         ScreenHandler var4 = var3.getScreenHandler();
         if (this.resolveInt4(var4) >= 0) {
            int var7 = this.resolveInt6(task, var4);
            if (var7 >= 0) {
               this.updateState4(task, var4, var7);
            } else {
               if (++this.index5 > 50) {
                  this.updateState9(var4);
                  this.updateState6();
               }
            }
         } else {
            int var5 = this.resolveInt5(var4);
            if (var5 >= 0) {
               this.updateState4(task, var4, var5);
            } else {
               int var6 = this.resolveInt6(task, var4);
               if (var6 >= 0) {
                  this.updateState4(task, var4, var6);
               } else {
                  if (++this.index5 > 50) {
                     this.updateState9(var4);
                     this.updateState6();
                  }
               }
            }
         }
      } else {
         if (++this.index5 > 50) {
            this.updateState6();
         }
      }
   }

   private int resolveInt(ScreenHandler handler) {
      if (mc.player == null) {
         return 0;
      }

      PlayerInventory var2 = mc.player.getInventory();
      int var3 = 0;

      for (Slot var5 : handler.slots) {
         if (var5.inventory != var2) {
            var3++;
         }
      }

      return var3;
   }

   private void updateState4(AutoSetCollector.Task task, ScreenHandler handler, int slot) {
      this.index6 = this.resolveInt2(task);
      mc.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
      this.stage = AutoSetCollector.Stage.WATCH;
      this.index7 = 35;
      this.index2 = 5;
   }

   private void updateState5(AutoSetCollector.Task task) {
      int var2 = this.resolveInt2(task);
      if (var2 > this.index6) {
         int var3 = var2 - this.index6;
         task.bought += var3;
         if (this.lot2 != null) {
            long var4 = this.lot2.totalPrice > 0L
               ? this.lot2.totalPrice
               : this.lot2.unitPrice * Math.max(1, this.lot2.count);
            AutoBuyConfig.cfg()
               .addHistory(
                  new PurchaseRecord(
                     System.currentTimeMillis(),
                     this.lot2.itemId,
                     task.item.name(),
                     this.lot2.seller,
                     this.lot2.unitPrice,
                     var4,
                     var3
                  )
               );
         }

         int var6 = Math.max(0, task.targetCount - var2);
         this.updateState10("§a[AutoSet] Куплено: §f" + task.item.name() + " §7(в инвентаре " + var2 + "/" + task.targetCount + ")");
         this.lot2 = null;
         if (var6 <= 0) {
            this.updateState7();
         } else {
            this.updateState6();
         }
      } else {
         if (--this.index7 <= 0) {
            this.lot2 = null;
            this.updateState6();
         }
      }
   }

   private AutoSetCollector.Task computeTask() {
      return this.index >= 0 && this.index < this.items.size() ? this.items.get(this.index) : null;
   }

   private void updateState6() {
      this.stage = AutoSetCollector.Stage.SEARCH;
      this.index3 = 0;
      this.index5 = 0;
      this.index2 = 5;
   }

   private void updateState7() {
      this.index++;
      if (this.index >= this.items.size()) {
         this.updateState8();
      } else {
         this.updateState6();
      }
   }

   private void updateState8() {
      this.running = false;
      int var1 = 0;
      int var2 = 0;

      for (AutoSetCollector.Task var4 : this.items) {
         var1 += var4.bought;
         var2 += Math.max(0, this.resolveInt3(var4));
      }

      if (mc.player != null && mc.currentScreen instanceof HandledScreen) {
         mc.player.closeHandledScreen();
      }

      this.updateState10("§a[AutoSet] Сбор завершён. Куплено: §f" + var1 + " шт." + (var2 > 0 ? " §7(не найдено: " + var2 + ")" : ""));
   }

   private boolean checkCondition(AutoSetCollector.Task task, Lot lot) {
      if (!ItemMatcher.matches(task.entry, lot)) {
         return false;
      }

      if (lot.unitPrice <= 0L) {
         return false;
      }

      if (task.maxUnitPrice > 0L && lot.unitPrice > task.maxUnitPrice) {
         return false;
      }

      if (lot.durability < task.minDurability) {
         return false;
      }

      String var3 = mc.player.getGameProfile().getName();
      return var3 == null || !var3.equalsIgnoreCase(lot.seller);
   }

   private int resolveInt2(AutoSetCollector.Task task) {
      if (mc.player == null) {
         return 0;
      }

      PlayerInventory var2 = mc.player.getInventory();
      int var3 = 0;

      for (int var4 = 0; var4 < var2.size(); var4++) {
         ItemStack var5 = var2.getStack(var4);
         if (!var5.isEmpty()) {
            Lot var6 = PriceParser.parseForMatch(var5);
            if (var6 != null && var6.durability >= task.minDurability && ItemMatcher.matches(task.entry, var6)) {
               var3 += var5.getCount();
            }
         }
      }

      return var3;
   }

   private int resolveInt3(AutoSetCollector.Task task) {
      return task.targetCount - this.resolveInt2(task);
   }

   private boolean checkCondition2(HandledScreen<?> hs) {
      if (this.resolveInt(hs.getScreenHandler()) < 27) {
         return false;
      }

      String var2 = hs.getTitle().getString();
      return var2 != null && var2.toLowerCase(Locale.ROOT).contains("аукцион");
   }

   private boolean checkCondition3(ScreenHandler handler) {
      int var2 = this.resolveInt(handler);

      for (int var3 = Math.max(0, var2 - 9); var3 < var2; var3++) {
         ItemStack var4 = handler.getSlot(var3).getStack();
         if (!var4.isEmpty() && var4.getItem() == Items.PAPER && var4.getName().getString().toLowerCase(Locale.ROOT).contains("след")) {
            mc.interactionManager.clickSlot(handler.syncId, var3, 0, SlotActionType.PICKUP, mc.player);
            return true;
         }
      }

      return false;
   }

   private boolean checkCondition4(HandledScreen<?> hs) {
      String var2 = hs.getTitle().getString();
      String var3 = var2 == null ? "" : var2.toLowerCase(Locale.ROOT);
      if (var3.startsWith("аукцион")) {
         return false;
      } else {
         return !var3.contains("покупка предмета") && !var3.contains("подтверждение") ? this.resolveInt4(hs.getScreenHandler()) >= 0 : true;
      }
   }

   private int resolveInt4(ScreenHandler handler) {
      int var2 = this.resolveInt(handler);

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = handler.getSlot(var3).getStack();
         if (!var4.isEmpty()) {
            String var5 = var4.getName().getString().toLowerCase(Locale.ROOT);
            if (var5.contains("увеличить") && var5.contains("на 1 ед")) {
               return var3;
            }
         }
      }

      return -1;
   }

   private int resolveInt5(ScreenHandler handler) {
      int var2 = this.resolveInt(handler);

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = handler.getSlot(var3).getStack();
         if (!var4.isEmpty()) {
            String var5 = Registries.ITEM.getId(var4.getItem()).toString();
            boolean var6 = var5.contains("lime_") || var5.contains("green_");
            if (var6) {
               String var7 = var4.getName().getString().toLowerCase(Locale.ROOT);
               if (!var7.contains("увеличить") && !var7.contains("уменьшить")) {
                  return var3;
               }
            }
         }
      }

      return -1;
   }

   private int resolveInt6(AutoSetCollector.Task task, ScreenHandler handler) {
      int var3 = this.resolveInt(handler);

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = handler.getSlot(var4).getStack();
         if (!var5.isEmpty()) {
            Lot var6 = PriceParser.parseForMatch(var5);
            if (var6 != null && ItemMatcher.matches(task.entry, var6)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private void updateState9(ScreenHandler handler) {
      int var2 = this.resolveInt(handler);

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = handler.getSlot(var3).getStack();
         if (!var4.isEmpty()) {
            String var5 = Registries.ITEM.getId(var4.getItem()).toString();
            if (var5.contains("red_")) {
               String var6 = var4.getName().getString().toLowerCase(Locale.ROOT);
               if (!var6.contains("уменьшить") && !var6.contains("увеличить")) {
                  mc.interactionManager.clickSlot(handler.syncId, var3, 0, SlotActionType.PICKUP, mc.player);
                  this.index2 = 5;
                  return;
               }
            }
         }
      }

      if (mc.player != null) {
         mc.player.closeHandledScreen();
      }

      this.index2 = 5;
   }

   private void updateState10(String text) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(text), false);
      }
   }

   private enum Stage {
      SEARCH,
      SCAN,
      CONFIRM,
      WATCH;
   }

   private static final class Task {
      final AutoSetItem item;
      final ItemEntry entry;
      final int minDurability;
      final long maxUnitPrice;
      final int targetCount;
      final boolean wholeStack;
      int bought;

      Task(AutoSetItem item, int minDurability, long maxUnitPrice, int targetCount) {
         this.item = item;
         this.entry = item.entry;
         this.minDurability = minDurability;
         this.maxUnitPrice = maxUnitPrice;
         this.targetCount = targetCount;
         this.wholeStack = item.wholeStack;
      }
   }
}
