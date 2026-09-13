package dlc.lumen.client.bots.core.behavior;

import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.bots.BotAuthStore;
import dlc.lumen.client.bots.core.BotLog;
import dlc.lumen.client.bots.core.BotSession;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.item.map.MapState.UpdateData;

public final class BotCaptchaWatch implements CaptchaStrategy {
   private static final long TIMESTAMP = 20000L;
   private static final String[] value = new String[]{
      "капч",
      "captcha",
      "введите код",
      "введи код",
      "код с карты",
      "введите номер",
      "номер с картин",
      "с картинки",
      "картинке",
      "введите число",
      "подтвердите",
      "проверка на бота"
   };
   private static final String[] STRING = new String[]{"зарегистрир", "/register", "register", "регистрац"};
   private static final String[] STRING2 = new String[]{"/login", "войдите", "авторизуй", "введите пароль", "login"};
   private static final String[] STRING3 = new String[]{"успешн", "passed", "проверка пройдена", "верно", "добро пожаловать"};
   private static final long TIMESTAMP2 = 3000L;
   private static final int INDEX = 5;
   private static final Pattern value2 = Pattern.compile(
      "(?:введ[^ ]*|подтверд[^ ]*|напиш[^ ]*|ответ[^ ]*|код|цифр[^ ]*|число)[^0-9]{0,20}([0-9]{1,8})", 2
   );
   private final BotSession botSession;
   private final Set<Integer> hashSet = new HashSet<>();
   private volatile long volatilelong;
   private volatile boolean volatileboolean;
   private volatile long volatilelong2;
   private volatile int volatileint;
   private volatile boolean volatileboolean2;
   private volatile boolean volatileboolean3;

   public BotCaptchaWatch(BotSession session) {
      this.botSession = session;
   }

   @Override
   public void onJoin() {
      this.hashSet.clear();
      this.volatilelong = System.currentTimeMillis();
      this.volatileboolean = false;
      this.volatilelong2 = 0L;
      this.volatileint = 0;
      this.volatileboolean2 = false;
      this.volatileboolean3 = false;
      this.botSession.requestHelp("");
   }

   @Override
   public void onText(String text) {
      if (text != null && !text.isEmpty()) {
         String var2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');

         for (String var6 : STRING3) {
            if (var2.contains(var6)) {
               this.updateState2("сервер сказал: " + BotLog.trim(text));
               return;
            }
         }

         for (String var16 : STRING) {
            if (var2.contains(var16)) {
               if (this.checkState()) {
                  return;
               }

               this.updateState("нужна регистрация");
               return;
            }
         }

         for (String var17 : STRING2) {
            if (var2.contains(var17)) {
               if (this.checkState2()) {
                  return;
               }

               this.updateState("нужен вход");
               return;
            }
         }

         for (String var18 : value) {
            if (var2.contains(var18)) {
               if (this.checkCondition(text)) {
                  return;
               }

               this.updateState("капча");
               return;
            }
         }
      }
   }

   private boolean checkState() {
      if (this.volatileboolean3 && !this.checkState3()) {
         return false;
      }

      if (!this.checkCondition2("регистрация")) {
         return false;
      }

      String var1 = BotAuthStore.getOrCreate(this.botSession);
      if (!this.botSession.sendChat("/register " + var1 + " " + var1)) {
         return false;
      }

      this.volatileboolean3 = true;
      BotLog.log("[" + this.botSession.getName() + "] /register отправлен");
      return true;
   }

   private boolean checkState2() {
      if (this.volatileboolean2 && !this.checkState3()) {
         return false;
      }

      String var1 = BotAuthStore.get(this.botSession);
      if (var1 == null) {
         var1 = BotAuthStore.getOrCreate(this.botSession);
      }

      if (!this.checkCondition2("вход")) {
         return false;
      }

      if (!this.botSession.sendChat("/login " + var1)) {
         return false;
      }

      this.volatileboolean2 = true;
      BotLog.log("[" + this.botSession.getName() + "] /login отправлен");
      return true;
   }

   private boolean checkCondition(String text) {
      Matcher var2 = value2.matcher(text);
      if (!var2.find()) {
         return false;
      }

      if (!this.checkCondition2("цифры")) {
         return false;
      }

      String var3 = var2.group(1);
      if (!this.botSession.sendChat(var3)) {
         return false;
      }

      BotLog.log("[" + this.botSession.getName() + "] отправил цифры: " + var3);
      return true;
   }

   private boolean checkCondition2(String label) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.volatilelong2 < 3000L) {
         return false;
      } else if (this.volatileint >= 5) {
         BotLog.log("[" + this.botSession.getName() + "] авто-ответы кончились: " + label);
         return false;
      } else {
         this.volatilelong2 = var2;
         this.volatileint++;
         return true;
      }
   }

   private boolean checkState3() {
      return System.currentTimeMillis() - this.volatilelong2 >= 3000L;
   }

   @Override
   public void onMapData(int mapId, UpdateData data) {
      if (data != null) {
         if (System.currentTimeMillis() - this.volatilelong <= 20000L) {
            this.hashSet.add(mapId);
            if (this.hashSet.size() >= 2) {
               this.updateState("капча на карте");
            }
         }
      }
   }

   @Override
   public void onFrameItem(int entityId, int mapId) {
      if (System.currentTimeMillis() - this.volatilelong <= 20000L) {
         this.hashSet.add(mapId);
         if (this.hashSet.size() >= 2) {
            this.updateState("капча на карте");
         }
      }
   }

   private void updateState(String reason) {
      if (!this.volatileboolean) {
         this.volatileboolean = true;
         this.botSession.requestHelp(reason);
         BotLog.log("[" + this.botSession.getName() + "] " + reason + " — нужен человек");
         ChatUtils.sendMessage("§7[Bot] §e" + this.botSession.getName() + ": " + reason + " §7— §f.bot menu §7→ «ввести код за него»");
      }
   }

   private void updateState2(String why) {
      if (this.volatileboolean) {
         this.volatileboolean = false;
         this.botSession.requestHelp("");
         BotLog.log("[" + this.botSession.getName() + "] проверка снята: " + why);
         ChatUtils.sendMessage("§7[Bot] §a" + this.botSession.getName() + " §7прошёл проверку");
      }
   }
}
