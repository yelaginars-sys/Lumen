package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.Lumen;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import dlc.lumen.mixin.ChatScreenAccessor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NameProtect extends Module {
   public static final NameProtect INSTANCE = new NameProtect();
   private final BooleanSetting booleanSetting = new BooleanSetting("Скрывать друзей", true);
   private final TextSetting groupRaw = new TextSetting("Группа", "Мл.Сотрудник", 32);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Скрывать гриф", false);
   private final TextSetting replacementName = new TextSetting("Никнейм", "lumen", 32);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Скрывать статистику", true);
   private static final String TEXT = "§e";
   private static final String TEXT2 = "§r";
   private static final String TEXT3 = "§6";
   private static final String TEXT4 = "§f";
   private static final String TEXT5 = "§l";
   private static final String TEXT6 = "cheatgarden.net";
   private static final String TEXT7 = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ";
   private static final String TEXT8 = "(?:§[0-9A-FK-ORXa-fk-orx])*+";
   private static final Style STYLE = Style.EMPTY.withColor(Formatting.YELLOW).withBold(true);
   private static final Style STYLE2 = Style.EMPTY.withColor(Formatting.WHITE);
   private static final Style STYLE3 = Style.EMPTY.withColor(Formatting.GOLD);
   private static final Pattern PATTERN = helper("Ник");
   private static final Pattern PATTERN2 = helper("Группа");
   private static final Pattern PATTERN3 = helper("Убийст\\p{L}*");
   private static final Pattern PATTERN4 = helper("Смерт\\p{L}*");
   private static final Pattern PATTERN5 = helper("Пинг");
   private static final Pattern PATTERN6 = helper("Сапфир\\p{L}*");
   private static final Pattern PATTERN7 = helper("Монет\\p{L}*");
   private static final Pattern PATTERN8 = Pattern.compile("(?iu)(?:Клан|Соло|Дуо|Трио|Гриф|Анархия)[ \\-]*(?:Лайт|PvP|ФУ|Гул)?[ \\-]*#?\\s*\\d+");
   private static final Pattern PATTERN9 = Pattern.compile("#\\s*\\d+\\s*✖?");
   private static final Map<String, Pattern> STRINGS = new ConcurrentHashMap<>();
   private String text2;
   private Pattern pattern2;
   private static final int INDEX = 512;
   private final Map<String, String> linkedHashMap = new LinkedHashMap<String, String>(512, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<String, String> eldest) {
         return this.size() > 512;
      }
   };

   private static Pattern helper(String label) {
      return Pattern.compile("(?iu)" + label + "\\s*[:.][ \\t]*([^\\n]*)");
   }

   private NameProtect() {
      super("NameProtect", "Скрывает никнеймы и информацию", Module.ModuleCategory.MISC);
      this.addSettings(this.booleanSetting, this.booleanSetting2, this.replacementName, this.groupRaw, this.booleanSetting3);
   }

   public String patch(String text) {
      if (text == null) {
         return null;
      }

      if (!this.helper14()) {
         return text;
      }

      String var2 = this.helper13(text);
      String var3 = this.linkedHashMap.get(var2);
      if (var3 != null) {
         return var3;
      }

      String var4 = text;
      String var5 = this.getReplacementName();
      String var6 = this.getGroupReplacement();
      String var7 = mc.getSession().getUsername();
      var4 = this.helper10(var4, var7, var5);
      if (this.booleanSetting.isState() && Lumen.INSTANCE != null && Lumen.INSTANCE.friendStorage != null) {
         for (String var9 : Lumen.INSTANCE.friendStorage.getFriends()) {
            var4 = this.helper10(var4, var9, var5);
         }
      }

      if (this.booleanSetting2.isState()) {
         var4 = this.helper12(var4, var5, var6);
      }

      this.linkedHashMap.put(var2, var4);
      return var4;
   }

   public String patchIncomingText(String text) {
      return this.patch(text);
   }

   public Text patchText(Text text) {
      if (text == null) {
         return null;
      }

      if (!this.helper14()) {
         return text;
      }

      List var2 = helper2(text);
      if (var2.isEmpty()) {
         return text;
      }

      boolean var3 = false;
      String var4 = this.getReplacementName();
      var3 |= this.helper11(var2, mc.getSession().getUsername(), var4);
      if (this.booleanSetting.isState() && Lumen.INSTANCE != null && Lumen.INSTANCE.friendStorage != null) {
         for (String var6 : Lumen.INSTANCE.friendStorage.getFriends()) {
            var3 |= this.helper11(var2, var6, var4);
         }
      }

      if (this.booleanSetting2.isState()) {
         var3 |= this.helper5(var2, var4);
      }

      return var3 ? helper3(var2) : text;
   }

   private static List<NameProtect.StyledChar> helper2(Text text) {
      ArrayList var1 = new ArrayList();
      text.visit((style, string) -> {
         for (int var3 = 0; var3 < string.length(); var3++) {
            var1.add(new NameProtect.StyledChar(string.charAt(var3), style));
         }

         return Optional.empty();
      }, Style.EMPTY);
      return var1;
   }

   private static String resolveString(List<NameProtect.StyledChar> chars) {
      StringBuilder var1 = new StringBuilder(chars.size());

      for (NameProtect.StyledChar var3 : chars) {
         var1.append(var3.c());
      }

      return var1.toString();
   }

   private static Text helper3(List<NameProtect.StyledChar> chars) {
      MutableText var1 = Text.empty();
      if (chars.isEmpty()) {
         return var1;
      }

      StringBuilder var2 = new StringBuilder();
      Style var3 = ((NameProtect.StyledChar)chars.get(0)).style();

      for (NameProtect.StyledChar var5 : chars) {
         if (!var5.style().equals(var3)) {
            var1.append(Text.literal(var2.toString()).setStyle(var3));
            var2.setLength(0);
            var3 = var5.style();
         }

         var2.append(var5.c());
      }

      var1.append(Text.literal(var2.toString()).setStyle(var3));
      return var1;
   }

   private static Style helper4(List<NameProtect.StyledChar> chars, int index) {
      if (index < chars.size()) {
         return ((NameProtect.StyledChar)chars.get(index)).style();
      } else {
         return index > 0 && index - 1 < chars.size() ? ((NameProtect.StyledChar)chars.get(index - 1)).style() : Style.EMPTY;
      }
   }

   private static void updateState(List<NameProtect.StyledChar> chars, int start, int end, String replacement, Style style) {
      ArrayList var5 = new ArrayList(replacement.length());

      for (int var6 = 0; var6 < replacement.length(); var6++) {
         var5.add(new NameProtect.StyledChar(replacement.charAt(var6), style));
      }

      chars.subList(start, end).clear();
      chars.addAll(start, var5);
   }

   private static boolean checkCondition(List<NameProtect.StyledChar> chars, Pattern pattern, int group, String replacement, Style style) {
      Matcher var5 = pattern.matcher(resolveString(chars));
      ArrayList var6 = new ArrayList();

      while (var5.find()) {
         if (var5.start(group) >= 0) {
            var6.add(new int[]{var5.start(group), var5.end(group)});
         }
      }

      if (var6.isEmpty()) {
         return false;
      }

      boolean var7 = false;

      for (int var8 = var6.size() - 1; var8 >= 0; var8--) {
         int[] var9 = (int[])var6.get(var8);
         String var10 = resolveString(chars).substring(var9[0], var9[1]);
         if (!var10.equals(replacement)) {
            updateState(chars, var9[0], var9[1], replacement, style != null ? style : helper4(chars, var9[0]));
            var7 = true;
         }
      }

      return var7;
   }

   private boolean helper5(List<NameProtect.StyledChar> chars, String nicknameReplacement) {
      boolean var3 = false;
      var3 |= checkCondition(chars, PATTERN, 1, nicknameReplacement, STYLE2);
      var3 |= checkCondition(chars, PATTERN2, 1, this.getGroupRaw(), STYLE);
      if (this.booleanSetting3.isState()) {
         var3 |= checkCondition(chars, PATTERN3, 1, "0", STYLE2);
         var3 |= checkCondition(chars, PATTERN4, 1, "0", STYLE2);
         var3 |= checkCondition(chars, PATTERN5, 1, "0 ms", STYLE2);
         var3 |= checkCondition(chars, PATTERN6, 1, "0", STYLE2);
         var3 |= checkCondition(chars, PATTERN7, 1, "милионер", STYLE3);
      }

      var3 |= checkCondition(chars, PATTERN8, 0, "cheatgarden.net", STYLE3);
      return var3 | checkCondition(chars, PATTERN9, 0, "cheatgarden.net", STYLE3);
   }

   public List<int[]> chatMaskRanges(String input) {
      if (input != null && !input.isEmpty() && this.isEnable()) {
         Matcher var2 = this.helper6().matcher(input);
         return !var2.find() ? List.of() : List.of(new int[]{var2.start(1), var2.end(1)});
      } else {
         return List.of();
      }
   }

   private Pattern helper6() {
      String var1 = Lumen.INSTANCE != null && Lumen.INSTANCE.commandStorage != null ? Lumen.INSTANCE.commandStorage.getPrefix() : ".";
      if (this.pattern2 == null || !var1.equals(this.text2)) {
         this.text2 = var1;
         this.pattern2 = Pattern.compile("(?i)^\\s*" + Pattern.quote(var1) + "rct\\s+(\\d+)");
      }

      return this.pattern2;
   }

   public String hiddenColor() {
      return this.isEnable() ? "§0" : "§6";
   }

   public String maskedNumber(int number) {
      return !this.isEnable() ? String.valueOf(number) : this.hiddenColor() + "█".repeat(String.valueOf(number).length());
   }

   private static Pattern helper7(String name) {
      return STRINGS.computeIfAbsent(name, NameProtect::helper8);
   }

   private static Pattern helper8(String name) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < name.length(); var2++) {
         if (var2 > 0) {
            var1.append("(?:§[0-9A-FK-ORXa-fk-orx])*+");
         }

         char var3 = name.charAt(var2);
         char var4 = Character.toUpperCase(var3);
         char var5 = Character.toLowerCase(var3);
         var1.append('[').append(helper9(var5));
         if (var4 != var5) {
            var1.append(helper9(var4));
         }

         if (var4 >= 'A' && var4 <= 'Z') {
            var1.append(helper9("ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ".charAt(var4 - 'A')));
         }

         var1.append(']');
      }

      return Pattern.compile(var1.toString());
   }

   private static String helper9(char c) {
      return "\\x{" + Integer.toHexString(c) + "}";
   }

   private String helper10(String text, String name, String replacement) {
      return name != null && !name.isBlank() ? helper7(name).matcher(text).replaceAll(Matcher.quoteReplacement(replacement)) : text;
   }

   private boolean helper11(List<NameProtect.StyledChar> chars, String name, String replacement) {
      return name != null && !name.isBlank() ? checkCondition(chars, helper7(name), 0, replacement, null) : false;
   }

   public String getReplacementName() {
      String var1 = this.replacementName.get();
      return var1 != null && !var1.isBlank() ? var1 : "lumen";
   }

   public String getGroupRaw() {
      String var1 = this.groupRaw.get();
      return var1 != null && !var1.isBlank() ? var1 : "Мл.Сотрудник";
   }

   public String getGroupReplacement() {
      return "§e§l" + this.getGroupRaw() + "§r";
   }

   public boolean shouldHideGrief() {
      return this.booleanSetting2.isState();
   }

   private String helper12(String text, String nicknameReplacement, String groupReplacement) {
      if (text == null) {
         return null;
      }

      String var4 = text;
      var4 = var4.replaceAll("(?iu)Ник\\s*[:.][ \\t]*(?=(?:§[0-9A-FK-ORXa-fk-orx])*+\\S)[^\\n]*", "Ник: §f" + nicknameReplacement + "§r");
      var4 = var4.replaceAll("(" + Pattern.quote(nicknameReplacement) + ")\\s*\\1", "$1");
      var4 = var4.replaceAll("(?iu)Группа\\s*[:.][ \\t]*(?=(?:§[0-9A-FK-ORXa-fk-orx])*+\\S)[^\\n]*", Matcher.quoteReplacement("Группа: " + groupReplacement));
      var4 = var4.replaceAll("(?m)^\\s*Нет\\s*$", "");
      var4 = var4.replaceAll("(?i)Убыли", "Убийство");
      var4 = var4.replaceAll("(?i)Убывство", "Убийство");
      var4 = var4.replaceAll("(?i)Убылист", "Убийство");
      var4 = var4.replaceAll("(?i)Смертель", "Смертей");
      var4 = var4.replaceAll("(?i)Смертен", "Смертей");
      var4 = var4.replaceAll("(?i)Санфиров", "Сапфиров");
      if (this.booleanSetting3.isState()) {
         var4 = var4.replaceAll("(?iu)Убийст\\p{L}*\\s*[:.]\\s*\\d+\\s*[^\\n]*", "§fУбийство: 0§r");
         var4 = var4.replaceAll("(?iu)Смерт\\p{L}*\\s*[:.]\\s*\\d+\\s*\\+?\\s*[^\\n]*", "§fСмертей: 0§r");
         var4 = var4.replaceAll("(?iu)Пинг\\s*[:.]\\s*\\d+\\s*[ms]?\\s*[^\\n]*", "§fПинг: 0 ms§r");
         var4 = var4.replaceAll("(?iu)Сапфир\\p{L}*\\s*[:.]\\s*\\d+\\s*[^\\n]*", "§fСапфиров: 0§r");
         var4 = var4.replaceAll("(?iu)Монет\\p{L}*\\s*[:.]\\s*[\\d,\\s]+[^\\n]*", "§fМонеток: §r");
      }

      var4 = PATTERN8.matcher(var4).replaceAll(Matcher.quoteReplacement("§6cheatgarden.net§r"));
      var4 = PATTERN9.matcher(var4).replaceAll(Matcher.quoteReplacement("§6cheatgarden.net§r"));
      var4 = var4.replaceAll("Соло-Лайт", "");
      var4 = var4.replaceAll("Клан-PvP", "");
      var4 = var4.replaceAll("Дуо-Лайт", "");
      var4 = var4.replaceAll("Трио-Лайт", "");
      var4 = var4.replaceAll("СолоЛайт", "");
      var4 = var4.replaceAll("КланPvP", "");
      var4 = var4.replaceAll("ДуоЛайт", "");
      var4 = var4.replaceAll("ТриоЛайт", "");
      var4 = var4.replaceAll("КланЛайт", "");
      var4 = var4.replaceAll("✖", "");
      var4 = var4.replaceAll("✕", "");
      var4 = var4.replaceAll("×", "");
      var4 = var4.replaceAll("⚠️", "");
      var4 = var4.replaceAll("•", "");
      var4 = var4.replaceAll("[+\\-]\\s*", "");
      var4 = var4.replaceAll("←+", "");
      var4 = var4.replaceAll("→+", "");
      var4 = var4.replaceAll("↔+", "");
      var4 = var4.replaceAll(" {3,}", "  ");
      var4 = var4.replaceAll(" \\n", "\n");
      var4 = var4.replaceAll("\\n ", "\n");
      var4 = var4.replaceAll("(?m)^  ", " ");
      return var4.replaceAll("\\n{3,}", "\n\n");
   }

   private String helper13(String text) {
      String var2 = mc != null && mc.getSession() != null ? mc.getSession().getUsername() : "";
      int var3 = 0;
      if (this.booleanSetting.isState() && Lumen.INSTANCE != null && Lumen.INSTANCE.friendStorage != null) {
         List var4 = Lumen.INSTANCE.friendStorage.getFriends();
         var3 = var4.hashCode();
      }

      return var2
         + this.getReplacementName()
         + this.getGroupReplacement()
         + this.booleanSetting.isState()
         + this.booleanSetting2.isState()
         + this.booleanSetting3.isState()
         + var3
         + text;
   }

   private boolean helper14() {
      return this.isEnable() && mc != null && mc.player != null && mc.world != null && !this.helper15();
   }

   private boolean helper15() {
      if (!(mc.currentScreen instanceof ChatScreen var1)) {
         return false;
      } else {
         TextFieldWidget var6 = ((ChatScreenAccessor)var1).lumen$getChatField();
         if (var6 == null) {
            return false;
         }

         String var3 = var6.getText();
         if (var3 == null) {
            return false;
         }

         String var4 = var3.trim().toLowerCase();
         String var5 = Lumen.INSTANCE != null && Lumen.INSTANCE.commandStorage != null ? Lumen.INSTANCE.commandStorage.getPrefix().toLowerCase() : ".";
         return var4.startsWith(var5 + "friend remove");
      }
   }

   private record StyledChar(char c, Style style) {

      private StyledChar(char c, Style style) {
         this.c = c;
         this.style = style;
      }

      public char c() {
         return this.c;
      }

      public Style style() {
         return this.style;
      }
   }
}
