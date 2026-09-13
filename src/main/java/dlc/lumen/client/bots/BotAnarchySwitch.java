package dlc.lumen.client.bots;

import dlc.lumen.api.QClient;
import dlc.lumen.client.bots.core.BotLog;
import dlc.lumen.client.bots.core.BotSession;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

public final class BotAnarchySwitch implements QClient {
   private static final Pattern PATTERN = Pattern.compile("(?:#|анархия\\s*)0*(\\d{1,2})(?!\\d)");
   private static final long TIMESTAMP = 1000L;
   private static final long TIMESTAMP2 = 8000L;
   private static final long TIMESTAMP3 = 5000L;
   private static final long TIMESTAMP4 = 150L;
   private static final int INDEX = 18;
   private final BotSession botSession;
   private BotAnarchySwitch.Step step = BotAnarchySwitch.Step.IDLE;
   private int index;
   private long timestamp;
   private long timestamp2;
   private int index2 = -1;

   public BotAnarchySwitch(BotSession bot) {
      this.botSession = bot;
   }

   public boolean isRunning() {
      return this.step != BotAnarchySwitch.Step.IDLE
         && this.step != BotAnarchySwitch.Step.DONE
         && this.step != BotAnarchySwitch.Step.FAILED;
   }

   public boolean start(int number) {
      if (!this.isRunning() && number > 0) {
         this.index = number;
         this.index2 = -1;
         this.step = BotAnarchySwitch.Step.HUB;
         long var2 = System.currentTimeMillis();
         this.timestamp2 = var2 + 1000L;
         this.timestamp = var2 + 5000L;
         this.updateState6("hub");
         this.updateState8("переход на анархию #" + number + ": /hub");
         return true;
      } else {
         return false;
      }
   }

   public void cancel() {
      this.step = BotAnarchySwitch.Step.IDLE;
   }

   public void tick() {
      if (this.isRunning()) {
         if (mc.player != null && this.botSession.isJoined()) {
            long var1 = System.currentTimeMillis();
            if (var1 >= this.timestamp2) {
               switch (this.step) {
                  case HUB:
                     this.step = BotAnarchySwitch.Step.COMPASS;
                     this.timestamp = var1 + 8000L;
                     break;
                  case COMPASS:
                     this.updateState(var1);
                     break;
                  case LIGHT:
                     this.updateState2(var1);
                     break;
                  case CATEGORY:
                     this.updateState3(var1);
                     break;
                  case NUMBER:
                     this.updateState4(var1);
               }
            }
         } else {
            this.updateState7("бот не в игре");
         }
      }
   }

   private void updateState(long now) {
      if (this.computeGenericContainerScreen() != null) {
         this.step = BotAnarchySwitch.Step.LIGHT;
         this.timestamp = now + 5000L;
      } else if (now > this.timestamp) {
         this.updateState7("не дождались компаса в хабе");
      } else {
         int var3 = this.resolveInt3();
         if (var3 >= 0) {
            mc.player.getInventory().selectedSlot = var3;
            if (mc.currentScreen != null) {
               mc.currentScreen.close();
            }

            if (mc.interactionManager != null) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }

            this.timestamp2 = now + 400L;
         }
      }
   }

   private void updateState2(long now) {
      GenericContainerScreen var3 = this.computeGenericContainerScreen2();
      if (var3 == null) {
         if (now > this.timestamp) {
            this.updateState7("не открылось меню хаба");
         }
      } else {
         int var4 = checkCondition(var3, 12) ? 12 : resolveInt(var3, "лайт");
         if (var4 < 0) {
            var4 = resolveInt(var3, "анарх");
         }

         if (var4 < 0) {
            if (now > this.timestamp) {
               this.updateState7("нет головы «Лайт Анархия»");
            }
         } else {
            this.updateState5(var3, var4, now);
            this.step = BotAnarchySwitch.Step.CATEGORY;
            this.timestamp = now + 5000L;
         }
      }
   }

   private void updateState3(long now) {
      GenericContainerScreen var3 = this.computeGenericContainerScreen2();
      if (var3 == null) {
         if (now > this.timestamp) {
            this.updateState7("не открылось меню анархий");
         }
      } else {
         int var4 = resolveInt(var3, resolveString2(this.index));
         if (var4 < 0) {
            var4 = resolveInt2(this.index);
         }

         if (!checkCondition(var3, var4)) {
            if (now > this.timestamp) {
               this.updateState7("нет категории анархии");
            }
         } else {
            this.updateState5(var3, var4, now);
            this.step = BotAnarchySwitch.Step.NUMBER;
            this.timestamp = now + 5000L;
         }
      }
   }

   private void updateState4(long now) {
      GenericContainerScreen var3 = this.computeGenericContainerScreen2();
      if (var3 == null) {
         if (now > this.timestamp) {
            this.updateState7("не открылось меню номеров");
         }
      } else {
         DefaultedList var4 = var3.getScreenHandler().slots;

         for (int var5 = 18; var5 < var4.size(); var5++) {
            ItemStack var6 = ((Slot)var4.get(var5)).getStack();
            if (!var6.isEmpty() && var6.getItem() == Items.PLAYER_HEAD && checkCondition2(var6, this.index)) {
               this.updateState5(var3, var5, now);
               this.step = BotAnarchySwitch.Step.DONE;
               this.updateState8("анархия #" + this.index + " выбрана");
               return;
            }
         }

         if (now > this.timestamp) {
            this.updateState7("не нашли голову анархии #" + this.index);
         }
      }
   }

   private GenericContainerScreen computeGenericContainerScreen() {
      return mc.currentScreen instanceof GenericContainerScreen var1 ? var1 : null;
   }

   private GenericContainerScreen computeGenericContainerScreen2() {
      GenericContainerScreen var1 = this.computeGenericContainerScreen();
      if (var1 != null && var1.getScreenHandler().syncId != this.index2) {
         return computereturn(var1) > 0 ? var1 : null;
      } else {
         return null;
      }
   }

   private void updateState5(GenericContainerScreen screen, int slot, long now) {
      this.index2 = screen.getScreenHandler().syncId;
      if (mc.interactionManager != null) {
         mc.interactionManager.clickSlot(this.index2, slot, 0, SlotActionType.PICKUP, mc.player);
      }

      this.timestamp2 = now + 150L;
   }

   private static int computereturn(GenericContainerScreen screen) {
      DefaultedList var1 = screen.getScreenHandler().slots;
      int var2 = screen.getScreenHandler().getRows() * 9;
      int var3 = 0;

      for (int var4 = 0; var4 < Math.min(var2, var1.size()); var4++) {
         if (!((Slot)var1.get(var4)).getStack().isEmpty()) {
            var3++;
         }
      }

      return var3;
   }

   private static boolean checkCondition(GenericContainerScreen screen, int slot) {
      DefaultedList var2 = screen.getScreenHandler().slots;
      return slot >= 0 && slot < var2.size() && !((Slot)var2.get(slot)).getStack().isEmpty();
   }

   private static int resolveInt(GenericContainerScreen screen, String keyword) {
      DefaultedList var2 = screen.getScreenHandler().slots;
      int var3 = screen.getScreenHandler().getRows() * 9;
      String var4 = keyword.toLowerCase(Locale.ROOT).replace('ё', 'е');

      for (int var5 = 0; var5 < Math.min(var3, var2.size()); var5++) {
         ItemStack var6 = ((Slot)var2.get(var5)).getStack();
         if (!var6.isEmpty() && resolveString(var6).contains(var4)) {
            return var5;
         }
      }

      return -1;
   }

   private static String resolveString(ItemStack stack) {
      StringBuilder var1 = new StringBuilder(stack.getName().getString());
      LoreComponent var2 = stack.get(DataComponentTypes.LORE);
      if (var2 != null && var2.lines() != null) {
         for (Text var4 : var2.lines()) {
            var1.append(' ').append(var4.getString());
         }
      }

      return var1.toString().toLowerCase(Locale.ROOT).replace('ё', 'е');
   }

   private static boolean checkCondition2(ItemStack stack, int number) {
      Matcher var2 = PATTERN.matcher(resolveString(stack));

      while (var2.find()) {
         try {
            if (Integer.parseInt(var2.group(1)) == number) {
               return true;
            }
         } catch (NumberFormatException var4) {
         }
      }

      return false;
   }

   private static String resolveString2(int number) {
      if (number <= 17) {
         return "соло";
      } else if (number <= 38) {
         return "дуо";
      } else {
         return number <= 57 ? "трио" : "клан";
      }
   }

   private static int resolveInt2(int number) {
      if (number <= 17) {
         return 0;
      } else if (number <= 38) {
         return 1;
      } else {
         return number <= 57 ? 2 : 3;
      }
   }

   private int resolveInt3() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).getItem() == Items.COMPASS) {
            return var1;
         }
      }

      return -1;
   }

   private void updateState6(String command) {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendCommand(command);
      }
   }

   private void updateState7(String reason) {
      this.step = BotAnarchySwitch.Step.FAILED;
      this.updateState8("переход сорвался: " + reason);
   }

   private void updateState8(String message) {
      BotLog.log("[" + this.botSession.getName() + "] rct: " + message);
   }

   private enum Step {
      IDLE,
      HUB,
      COMPASS,
      LIGHT,
      CATEGORY,
      NUMBER,
      DONE,
      FAILED;
   }
}
