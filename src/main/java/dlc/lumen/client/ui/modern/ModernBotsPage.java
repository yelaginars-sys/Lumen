package dlc.lumen.client.ui.modern;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.client.bots.BotAutoJoin;
import dlc.lumen.client.bots.BotModuleProfiles;
import dlc.lumen.client.bots.BotTasks;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.core.BotSession;
import dlc.lumen.client.bots.world.BotScreenBoards;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

public final class ModernBotsPage {
   public static final Object FOCUS_NICK = "bots.nick";
   public static final Object FOCUS_ADDRESS = "bots.address";
   public static final Object FOCUS_SAY = "bots.say";
   private static String text2 = "";
   private static String text3 = "";
   private static String text4 = "";
   private static String text5 = "";
   private static final float VOLUME = 30.0F;
   private static final float VOLUME2 = 6.0F;

   private ModernBotsPage() {
   }

   public static void render(ModernGui g, float x, float y, float w, float h) {
      float var5 = g.scroll();
      float var6 = y - var5;
      var6 = helper(g, x, var6, w);
      var6 += 4.0F;
      var6 = helper2(g, x, var6, w);
      var6 += 4.0F;
      var6 = helper3(g, x, var6, w);
      g.setContentHeight(var6 - (y - var5), h);
   }

   public static String subtitle() {
      int var0 = BotManager.INSTANCE.joined().size();
      int var1 = BotManager.INSTANCE.bots().size();
      return var1 == 0 ? "ботов нет" : var0 + " в игре · " + var1 + " всего";
   }

   private static float helper(ModernGui g, float x, float cursor, float w) {
      g.sectionLabel(x + 2.0F, cursor, "Сессии");
      cursor += 12.0F;
      List<BotSession> var4 = BotManager.INSTANCE.bots();
      if (var4.isEmpty()) {
         g.row(x, cursor, w, 26.0F, false, "bots.empty");
         g.text(g.font(10), "ботов нет — заведи первого ниже", x + 10.0F, g.textY(cursor, 26.0F, g.font(10)), ModernTheme.TEXT_MUTED());
         cursor += 32.0F;
      }

      for (BotSession var6 : var4) {
         boolean var7 = var6.getName().equalsIgnoreCase(helper6());
         g.row(x, cursor, w, 30.0F, var7, "bot." + var6.getName());
         int var8 = var6.needsHelp() ? ModernTheme.DANGER : (var6.isJoined() ? ModernTheme.accent() : ModernTheme.TEXT_FAINT());
         g.rect(x + 7.0F, cursor + 8.0F, 14.0F, 14.0F, 7.0F, ModernTheme.PILL());
         String var9 = var6.getName().isEmpty() ? "?" : var6.getName().substring(0, 1).toUpperCase(Locale.ROOT);
         g.textCenter(g.font(9), var9, x + 14.0F, g.textY(cursor + 8.0F, 14.0F, g.font(9)), var8);
         Font var10 = g.font(11);
         g.text(var10, g.clip(var10, var6.getName(), w - 96.0F), x + 26.0F, cursor + 7.0F, var7 ? ModernTheme.TEXT() : ModernTheme.TEXT_SOFT());
         g.text(
            g.font(9),
            g.clip(g.font(9), helper7(var6), w - 96.0F),
            x + 26.0F,
            cursor + 17.5F,
            var6.needsHelp() ? ModernTheme.DANGER : ModernTheme.TEXT_MUTED()
         );
         boolean var11 = BotManager.INSTANCE.getControlled() == var6;
         helper10(
            g, x + w - 44.0F, cursor + 7.0F, var11 ? "-" : "play", var11 ? ModernTheme.TEXT_DIM() : ModernTheme.accent(), "view." + var6.getName(), () -> {
               if (var11) {
                  BotManager.INSTANCE.releaseControl();
               } else {
                  helper4(g, var6);
               }
            }
         );
         helper10(
            g, x + w - 23.0F, cursor + 7.0F, "trash", ModernTheme.DANGER, "rm." + var6.getName(), () -> BotManager.INSTANCE.remove(var6.getName())
         );
         g.hit(x, cursor, w - 48.0F, 30.0F, button -> {
            text5 = var6.getName();
            return true;
         });
         cursor += 33.0F;
      }

      cursor += 2.0F;
      float var15 = (w - 6.0F) / 2.0F;
      g.field(x, cursor, var15, 19.0F, FOCUS_NICK, "Ник бота", text2, "n");
      g.field(x + var15 + 6.0F, cursor, var15, 19.0F, FOCUS_ADDRESS, "Адрес сервера", text3, "s");
      cursor += 24.0F;
      float var16 = (w - 6.0F) / 2.0F;
      helper8(g, x, cursor, var16, 19.0F, "завести", () -> {
         if (!text2.isBlank() && !text3.isBlank()) {
            BotManager.INSTANCE.connect(text2.trim(), text3.trim());
            text5 = text2.trim();
            text2 = "";
         }
      });
      helper8(g, x + var16 + 6.0F, cursor, var16, 19.0F, "в автозаход", () -> {
         if (!text2.isBlank() && !text3.isBlank()) {
            BotAutoJoin.INSTANCE.add(text2.trim(), text3.trim());
            text2 = "";
         }
      });
      return cursor + 19.0F + 6.0F;
   }

   private static float helper2(ModernGui g, float x, float cursor, float w) {
      BotSession var4 = BotManager.INSTANCE.find(helper6());
      if (var4 == null) {
         return cursor;
      }

      g.sectionLabel(x + 2.0F, cursor, var4.getName());
      cursor += 12.0F;
      g.row(x, cursor, w, 34.0F, false, "bots.card");
      Font var5 = g.font(11);
      g.text(var5, g.clip(var5, var4.getAddress(), w - 100.0F), x + 10.0F, cursor + 8.0F, ModernTheme.TEXT_SOFT());
      g.text(
         g.font(9),
         g.clip(g.font(9), helper7(var4), w - 100.0F),
         x + 10.0F,
         cursor + 19.0F,
         var4.needsHelp() ? ModernTheme.DANGER : ModernTheme.TEXT_MUTED()
      );
      if (!var4.needsHelp() && var4.player() != null) {
         String var6 = (int)var4.player().getHealth() + " hp";
         g.textRight(g.font(10), var6, x + w - 12.0F, g.textY(cursor, 34.0F, g.font(10)), ModernTheme.TEXT_DIM());
      }

      cursor += 40.0F;
      List var16 = BotModuleProfiles.enabledOf(var4.getName());
      String var7 = var16.isEmpty() ? "ничего не включено" : "включено: " + String.join(", ", var16);
      g.text(g.font(9), g.clip(g.font(9), var7, w - 4.0F), x + 2.0F, cursor, var16.isEmpty() ? ModernTheme.TEXT_FAINT() : ModernTheme.TEXT_MUTED());
      cursor += 12.0F;
      float var8 = (w - 6.0F) / 2.0F;
      if (var4.needsHelp()) {
         helper9(g, x, cursor, var8, 20.0F, "ввести код за него", () -> helper5(var4));
         helper8(g, x + var8 + 6.0F, cursor, var8, 20.0F, "он справился", () -> var4.requestHelp(""));
         cursor += 26.0F;
      }

      float var9 = (w - 6.0F) / 2.0F;
      boolean var10 = BotManager.INSTANCE.getControlled() == var4;
      boolean var11 = BotScreenBoards.INSTANCE.has(var4.getName());
      g.pill(x, cursor, var9, 19.0F, var10 ? "вернуть экран" : "смотреть", "icon1", "a", var10, "bots.watch", button -> {
         if (var10) {
            BotManager.INSTANCE.releaseControl();
         } else {
            helper4(g, var4);
         }

         return true;
      });
      g.pill(x + var9 + 6.0F, cursor, var9, 19.0F, var11 ? "монитор: убрать" : "поставить монитор", "icon", "d", var11, "bots.board", button -> {
         if (var11) {
            BotScreenBoards.INSTANCE.remove(var4.getName());
         } else {
            g.close();
            BotScreenBoards.INSTANCE.beginPlacing(var4.getName());
         }

         return true;
      });
      cursor += 25.0F;
      g.field(x, cursor, w - 58.0F, 19.0F, FOCUS_SAY, "Сообщение в чат", text4, "m");
      helper8(g, x + w - 54.0F, cursor, 54.0F, 19.0F, "отправить", () -> {
         if (!text4.isBlank() && var4.sendChat(text4)) {
            text4 = "";
         }
      });
      return cursor + 19.0F + 6.0F;
   }

   private static float helper3(ModernGui g, float x, float cursor, float w) {
      g.sectionLabel(x + 2.0F, cursor, "Автозаход");
      cursor += 12.0F;
      boolean var4 = BotAutoJoin.INSTANCE.isActive();
      g.row(x, cursor, w, 26.0F, var4, "bots.auto");
      g.text(g.font(11), "Поднимать ботов самому", x + 10.0F, cursor + 4.5F, ModernTheme.TEXT_SOFT());
      g.text(g.font(9), "заводит из списка и возвращает после вылета", x + 10.0F, cursor + 14.5F, ModernTheme.TEXT_MUTED());
      g.toggle(x + w - 10.0F - 20.0F, cursor + 8.0F, var4, "bots.autotoggle");
      g.hit(x, cursor, w, 26.0F, button -> {
         BotAutoJoin.INSTANCE.toggle();
         return true;
      });
      cursor += 29.0F;
      List<BotAutoJoin.Entry> var5 = BotAutoJoin.INSTANCE.entries();
      if (var5.isEmpty()) {
         g.text(g.font(10), "список пуст — добавь бота кнопкой «в автозаход»", x + 2.0F, cursor + 3.0F, ModernTheme.TEXT_MUTED());
         return cursor + 16.0F;
      }

      for (BotAutoJoin.Entry var7 : var5) {
         g.row(x, cursor, w, 30.0F, var7.enabled, "auto." + var7.name);
         Font var8 = g.font(11);
         g.text(var8, g.clip(var8, var7.name, w - 96.0F), x + 10.0F, cursor + 7.0F, var7.enabled ? ModernTheme.TEXT_SOFT() : ModernTheme.TEXT_DIM());
         String var9 = BotAutoJoin.INSTANCE.isConnected(var7.name) ? "в игре · " + var7.address : BotAutoJoin.INSTANCE.statusOf(var7) + " · " + var7.address;
         g.text(g.font(9), g.clip(g.font(9), var9, w - 96.0F), x + 10.0F, cursor + 17.5F, ModernTheme.TEXT_MUTED());
         g.toggle(x + w - 44.0F - 20.0F, cursor + 10.0F, var7.enabled, "autotg." + var7.name);
         g.hit(x + w - 46.0F - 20.0F, cursor + 4.0F, 26.0F, 22.0F, button -> {
            BotAutoJoin.INSTANCE.toggleEntry(var7.name);
            return true;
         });
         helper10(g, x + w - 23.0F, cursor + 7.0F, "trash", ModernTheme.DANGER, "autorm." + var7.name, () -> BotAutoJoin.INSTANCE.remove(var7.name));
         cursor += 33.0F;
      }

      return cursor;
   }

   private static void helper4(ModernGui g, BotSession bot) {
      if (BotManager.INSTANCE.control(bot.getName())) {
         g.close();
      }
   }

   private static void helper5(BotSession bot) {
      if (BotManager.INSTANCE.control(bot.getName())) {
         MinecraftClient.getInstance().setScreen(new ChatScreen(""));
      }
   }

   private static String helper6() {
      if (!text5.isBlank() && BotManager.INSTANCE.find(text5) != null) {
         return text5;
      }

      List var0 = BotManager.INSTANCE.bots();
      return var0.isEmpty() ? "" : ((BotSession)var0.get(0)).getName();
   }

   private static String helper7(BotSession bot) {
      if (bot.needsHelp()) {
         return bot.helpReason();
      } else if (!bot.isJoined()) {
         return bot.getStatus();
      } else if (BotTasks.INSTANCE.isSwitchingAnarchy(bot.getName())) {
         return "меняет анархию";
      } else {
         String var1 = BotTasks.INSTANCE.statusOf(bot.getName());
         if (!var1.isEmpty()) {
            return var1;
         } else {
            return BotManager.INSTANCE.getControlled() == bot ? "смотришь" : "в игре";
         }
      }
   }

   private static void helper8(ModernGui g, float x, float y, float w, float h, String label, Runnable action) {
      boolean var7 = g.hovered(x, y, w, h);
      float var8 = ModernAnim.value("bots.ab:" + label + x + y, var7 ? 1.0F : 0.0F, 15.0F);
      g.surface(x, y, w, h, ModernTheme.R_ROW(), ColorUtils.interpolate(ModernTheme.PILL(), ModernTheme.PILL_HOVER(), var8), ModernTheme.CARD_BORDER(), 2.5F);
      Font var9 = g.font(9);
      g.textCenter(var9, label, x + w / 2.0F, g.textY(y, h, var9), ModernTheme.accent());
      g.hit(x, y, w, h, button -> {
         action.run();
         return true;
      });
   }

   private static void helper9(ModernGui g, float x, float y, float w, float h, String label, Runnable action) {
      boolean var7 = g.hovered(x, y, w, h);
      float var8 = ModernAnim.value("bots.acc:" + label, var7 ? 1.0F : 0.0F, 15.0F);
      int var9 = ColorUtils.replAlpha(ModernTheme.accent(), (int)(170.0F + 60.0F * var8));
      g.surface(x, y, w, h, ModernTheme.R_ROW(), var9, ModernTheme.accent(), 3.5F);
      Font var10 = g.font(10);
      g.textCenter(var10, label, x + w / 2.0F, g.textY(y, h, var10), ModernTheme.BG());
      g.hit(x, y, w, h, button -> {
         action.run();
         return true;
      });
   }

   private static void helper10(ModernGui g, float x, float y, String kind, int color, String key, Runnable action) {
      float var7 = 16.0F;
      boolean var8 = g.hovered(x, y, var7, var7);
      float var9 = ModernAnim.value("bots.mb:" + key, var8 ? 1.0F : 0.0F, 16.0F);
      g.surface(
         x, y, var7, var7, ModernTheme.R_PILL(), ColorUtils.interpolate(ModernTheme.PILL(), ModernTheme.PILL_HOVER(), var9), ModernTheme.CARD_BORDER(), 2.5F
      );
      float var10 = x + var7 / 2.0F;
      float var11 = y + var7 / 2.0F;
      switch (kind) {
         case "play":
            g.playIcon(var10 + 0.4F, var11, 3.2F, color);
            break;
         case "+":
            g.textCenter(g.font(10), "+", var10, y + 5.5F, color);
            break;
         case "-":
            g.textCenter(g.font(10), "-", var10, y + 5.5F, color);
            break;
         default:
            g.trashIcon(var10, var11, 3.6F, color);
      }

      g.hit(x, y, var7, var7, button -> {
         action.run();
         return true;
      });
   }

   public static boolean commit(Object target, String value) {
      if (target == FOCUS_NICK) {
         text2 = value.trim();
      } else if (target == FOCUS_ADDRESS) {
         text3 = value.trim();
      } else {
         if (target != FOCUS_SAY) {
            return false;
         }

         text4 = value;
      }

      return true;
   }
}
