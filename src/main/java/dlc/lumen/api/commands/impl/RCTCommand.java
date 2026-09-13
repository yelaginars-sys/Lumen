package dlc.lumen.api.commands.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dlc.lumen.api.commands.Command;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import dlc.lumen.client.modules.impl.misc.NameProtect;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

public class RCTCommand extends Command {
   private static final long timeMs = 10L;
   private static final long timeMs2 = 50L;
   private static final long timeMs3 = 10L;
   private static final long timeMs4 = 5L;
   private static final long timeMs5 = 8000L;
   private static final long timeMs6 = 2000L;
   private static final long timeMs7 = 1000L;
   private static final int count = 3;
   public static volatile boolean RUNNING = false;
   private final AtomicBoolean activeFlag = new AtomicBoolean(false);
   private int integer;
   private int number = -1;
   private int count2 = -1;
   private static final long timeMs8 = 15L;
   private static final long timeMs9 = 5000L;
   private static final Pattern pattern = Pattern.compile("(?:#|анархия\\s*)0*(\\d{1,2})(?!\\d)");
   private static final int count3 = 0;
   private static final int count4 = 1;
   private static final int count5 = 2;
   private static final int count6 = 3;
   private static final int count7 = 18;

   public RCTCommand() {
      super("rct");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)builder.executes(context -> {
         if (this.activeFlag.get()) {
            ChatUtils.sendMessage("§cКоманда уже выполняется! Подождите...");
            return 1;
         } else {
            int var2 = this.number > 0 ? this.number : AutoMine.INSTANCE.currentAnarchy();
            if (var2 <= 0) {
               ChatUtils.sendMessage("§cНе удалось определить анархию — укажи номер: .rct <число>");
               return 1;
            } else {
               this.handleNumber(var2);
               return 1;
            }
         }
      })).then(RequiredArgumentBuilder.argument("number", IntegerArgumentType.integer(1, 51)).executes(context -> {
         if (this.activeFlag.get()) {
            ChatUtils.sendMessage("§cКоманда уже выполняется! Подождите...");
            return 1;
         } else {
            this.handleNumber(IntegerArgumentType.getInteger(context, "number"));
            return 1;
         }
      }));
   }

   private void handleNumber(int number) {
      this.integer = number;
      this.number = number;
      ChatUtils.sendMessage("§eЗапуск подключения к анархии " + NameProtect.INSTANCE.maskedNumber(this.integer));
      this.activeFlag.set(true);
      RUNNING = true;
      new Thread(() -> {
         try {
            Thread.sleep(10L);
            this.handleAction();
         } catch (InterruptedException var2) {
            var2.printStackTrace();
         }
      }).start();
   }

   private void handleAction() {
      int var1 = 0;
      boolean var2 = false;

      while (var1 < 3 && !var2) {
         try {
            this.count2 = -1;
            this.handleCommand("hub");
            this.handleMs(50L);
            this.handleAction2();
            this.handleAction3();
            this.handleNumber2(this.integer);
            long var3 = System.currentTimeMillis();
            this.handleAction4();
            boolean var5 = this.checkTargetNumber(this.integer);
            if (var5) {
               var2 = true;
               ChatUtils.sendMessage("§aУспешно зашли на анархию " + NameProtect.INSTANCE.maskedNumber(this.integer));
            } else if (++var1 < 3) {
               ChatUtils.sendMessage("§eНе удалось зайти на анархию. Попытка " + (var1 + 1) + "/3");
               this.handleMs(1000L);
            }
         } catch (Exception var6) {
            var6.printStackTrace();
            if (++var1 < 3) {
               ChatUtils.sendMessage("§eОшибка при подключении. Попытка " + (var1 + 1) + "/3");
               this.handleMs(1000L);
            }
         }
      }

      if (!var2) {
         ChatUtils.sendMessage("§cНе удалось подключиться к анархии после 3 попыток");
      }

      this.handleAction5();
   }

   private boolean checkTargetNumber(int targetNumber) {
      long var2 = System.currentTimeMillis();

      while (System.currentTimeMillis() - var2 < 2000L) {
         if (mc.currentScreen instanceof GenericContainerScreen) {
            this.handleMs(5L);
         } else if (mc.player != null) {
            int var4 = this.calcValue();
            if (var4 < 0) {
               return true;
            }

            this.handleMs(5L);
         } else {
            this.handleMs(5L);
         }
      }

      return false;
   }

   private GenericContainerScreen getTimeoutMs(long timeoutMs) {
      long var3 = System.currentTimeMillis();
      int var5 = -1;
      long var6 = 0L;

      while (System.currentTimeMillis() - var3 < timeoutMs) {
         if (mc.currentScreen instanceof GenericContainerScreen var8 && var8.getScreenHandler().syncId != this.count2) {
            int var13 = this.calcScreen(var8);
            long var10 = System.currentTimeMillis();
            if (var13 <= 0 || var13 != var5) {
               var5 = var13;
               var6 = 0L;
            } else if (var6 == 0L) {
               var6 = var10;
            } else if (var10 - var6 >= 15L) {
               return var8;
            }
         } else {
            var5 = -1;
            var6 = 0L;
         }

         this.handleMs(5L);
      }

      return mc.currentScreen instanceof GenericContainerScreen var12 ? var12 : null;
   }

   private int calcScreen(GenericContainerScreen screen) {
      DefaultedList var2 = screen.getScreenHandler().slots;
      int var3 = screen.getScreenHandler().getRows() * 9;
      int var4 = 0;

      for (int var5 = 0; var5 < Math.min(var3, var2.size()); var5++) {
         if (!((Slot)var2.get(var5)).getStack().isEmpty()) {
            var4++;
         }
      }

      return var4;
   }

   private boolean checkScreen(GenericContainerScreen screen, int slot) {
      DefaultedList var3 = screen.getScreenHandler().slots;
      return slot >= 0 && slot < var3.size() && !((Slot)var3.get(slot)).getStack().isEmpty();
   }

   private void handleScreen(GenericContainerScreen screen, int slot) {
      int var3 = screen.getScreenHandler().syncId;
      this.count2 = var3;
      mc.execute(() -> {
         if (mc.interactionManager != null && mc.player != null) {
            mc.interactionManager.clickSlot(var3, slot, 0, SlotActionType.PICKUP, mc.player);
         }
      });
      this.handleMs(10L);
   }

   private String resolveStack(ItemStack stack) {
      return (stack.getName().getString() + " " + this.resolveStack2(stack)).toLowerCase(Locale.ROOT).replace('ё', 'е');
   }

   private int calcScreen2(GenericContainerScreen screen, String keyword) {
      DefaultedList var3 = screen.getScreenHandler().slots;
      int var4 = screen.getScreenHandler().getRows() * 9;
      String var5 = keyword.toLowerCase(Locale.ROOT).replace('ё', 'е');

      for (int var6 = 0; var6 < Math.min(var4, var3.size()); var6++) {
         ItemStack var7 = ((Slot)var3.get(var6)).getStack();
         if (!var7.isEmpty() && this.resolveStack(var7).contains(var5)) {
            return var6;
         }
      }

      return -1;
   }

   private void handleAction2() {
      long var1 = System.currentTimeMillis() + 8000L;

      while (System.currentTimeMillis() < var1) {
         if (mc.currentScreen instanceof GenericContainerScreen) {
            return;
         }

         int var3 = this.calcValue();
         if (var3 < 0) {
            this.handleMs(5L);
         } else {
            this.handleSlot(var3);
            long var4 = System.currentTimeMillis();

            while (System.currentTimeMillis() - var4 < 400L) {
               if (mc.currentScreen instanceof GenericContainerScreen) {
                  return;
               }

               this.handleMs(5L);
            }
         }
      }
   }

   private int calcValue() {
      if (mc.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).getItem() == Items.COMPASS) {
            return var1;
         }
      }

      return -1;
   }

   private void handleSlot(int slot) {
      mc.execute(() -> {
         if (mc.player != null) {
            mc.player.getInventory().selectedSlot = slot;
            if (mc.currentScreen != null) {
               mc.currentScreen.close();
            }

            if (mc.interactionManager != null) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
         }
      });
      this.handleMs(10L);
   }

   private void handleAction3() {
      GenericContainerScreen var1 = this.getTimeoutMs(5000L);
      if (var1 != null) {
         int var2 = this.checkScreen(var1, 12) ? 12 : this.calcScreen2(var1, "лайт");
         if (var2 < 0) {
            var2 = this.calcScreen2(var1, "анарх");
         }

         if (var2 >= 0) {
            this.handleScreen(var1, var2);
         }
      }
   }

   private void handleNumber2(int number) {
      GenericContainerScreen var2 = this.getTimeoutMs(5000L);
      if (var2 != null) {
         int var3 = this.calcScreen2(var2, this.resolveNumber(number));
         if (var3 < 0) {
            var3 = this.calcNumber(number);
         }

         if (this.checkScreen(var2, var3)) {
            this.handleScreen(var2, var3);
         }
      }
   }

   private String resolveNumber(int number) {
      if (number <= 12) {
         return "соло";
      } else if (number <= 26) {
         return "дуо";
      } else {
         return number <= 39 ? "трио" : "клан";
      }
   }

   private void handleAction4() {
      GenericContainerScreen var1 = this.getTimeoutMs(5000L);
      if (var1 != null) {
         DefaultedList var2 = var1.getScreenHandler().slots;

         for (int var3 = 18; var3 < var2.size(); var3++) {
            ItemStack var4 = ((Slot)var2.get(var3)).getStack();
            if (!var4.isEmpty() && var4.getItem() == Items.PLAYER_HEAD && this.checkStack(var4, this.integer)) {
               this.handleScreen(var1, var3);
               return;
            }
         }
      }
   }

   private boolean checkStack(ItemStack stack, int number) {
      Matcher var3 = pattern.matcher(this.resolveStack(stack));

      while (var3.find()) {
         try {
            if (Integer.parseInt(var3.group(1)) == number) {
               return true;
            }
         } catch (NumberFormatException var5) {
         }
      }

      return false;
   }

   private void handleAction5() {
      this.activeFlag.set(false);
      RUNNING = false;
   }

   private void handleMs(long ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
      }
   }

   private void handleCommand(String command) {
      if (mc.getNetworkHandler() != null && mc.player != null) {
         mc.player.networkHandler.sendCommand(command);
      }
   }

   private int calcNumber(int number) {
      if (number <= 12) {
         return 0;
      } else if (number <= 26) {
         return 1;
      } else {
         return number <= 39 ? 2 : 3;
      }
   }

   private String resolveStack2(ItemStack stack) {
      StringBuilder var2 = new StringBuilder();
      LoreComponent var3 = stack.get(DataComponentTypes.LORE);
      if (var3 != null && var3.lines() != null) {
         for (Text var5 : var3.lines()) {
            var2.append(var5.getString()).append(" ");
         }
      }

      return var2.toString();
   }
}
