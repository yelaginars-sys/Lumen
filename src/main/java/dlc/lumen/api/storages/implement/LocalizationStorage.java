package dlc.lumen.api.storages.implement;

import dlc.lumen.client.modules.Module;
import java.util.HashMap;
import java.util.Map;

public class LocalizationStorage {
   private final Map<String, String> displayName = new HashMap<>();
   private final Map<String, String> stringMap = new HashMap<>();
   private LocalizationStorage.Language language = LocalizationStorage.Language.RUSSIAN;

   public LocalizationStorage() {
      this.handleKey("Combat", "Combat", "Бій");
      this.handleKey("Movement", "Movement", "Рух");
      this.handleKey("Render", "Render", "Візуал");
      this.handleKey("Misc", "Misc", "Різне");
      this.handleKey("Player", "Player", "Гравець");
      this.handleKey("Язык", "Language", "Мова");
      this.handleKey("Русский", "Russian", "Російська");
      this.handleKey("Английский", "English", "Англійська");
      this.handleKey("Украинский", "Ukrainian", "Українська");
      this.handleKey("Sprint", "Sprint", "Спринт");
      this.handleKey("AutoTotem", "Auto Totem", "Авто тотем");
      this.handleKey("Interface", "Interface", "Інтерфейс");
      this.handleKey("InventoryWalk", "Inventory Walk", "Хода з інвентарем");
      this.handleKey("NoVignette", "No Vignette", "Без віньєтки");
      this.handleKey("Aura", "Aura", "Аура");
      this.handleKey("ElytraBoost", "Elytra Boost", "Елітра буст");
      this.handleKey("ElytraFly", "Elytra Fly", "Елітра флай");
      this.handleKey("ElytraTarget", "Elytra Target", "Таргет елітри");
      this.handleKey("FullBright", "Full Bright", "Повна яскравість");
      this.handleKey("ElytraSwap", "Elytra Swap", "Свап елітр");
      this.handleKey("PlayerFakeLags", "Fake Lag", "Фейк лаг");
      this.handleKey("Chams", "Chams", "Чамси");
      this.handleKey("ClientSounds", "Client Sounds", "Звуки клієнта");
      this.handleKey("Cosmetics", "Cosmetics", "Косметика");
      this.handleKey("ServerHelper", "Server Helper", "Сервер Хелпер");
      this.handleKey("KTLeave", "KT Leave", "КТ лів");
      this.handleKey("NoClip", "No Clip", "Без кліпу");
      this.handleKey("Particles", "Particles", "Частинки");
      this.handleKey("ElytraMotion", "Elytra Motion", "Рух елітри");
      this.handleKey("HitBubbles", "Hit Bubbles", "Бульбашки удару");
      this.handleKey("RPSpoofer", "RP Spoofer", "RP спуфер");
      this.handleKey("Projectile", "Projectile", "Снаряд");
      this.handleKey("AutoExplosion", "Auto Explosion", "Авто вибух");
      this.handleKey("PacketCriticals", "Packet Criticals", "Пакетні кріти");
      this.handleKey("EntityESP", "Entity ESP", "ESP сутностей");
      this.handleKey("NoPush", "No Push", "Без штовхання");
      this.handleKey("TPLoot", "TP Loot", "ТП лут");
      this.handleKey("CutDetector", "Cut Detector", "Детектор кату");
      this.handleKey("AirStuck", "Air Stuck", "Зависання в повітрі");
      this.handleKey("Sonar", "Sonar", "Сонар");
      this.handleKey("NoWeb", "No Web", "Без павутини");
      this.handleKey("CocoaFarm", "Cocoa Farm", "Какао ферма");
      this.handleKey("Removals", "Removals", "Видалення");
      this.handleKey("SwingAnimations", "Swing Animations", "Анімації свінгу");
      this.handleKey("ViewModel", "View Model", "Модель рук");
      this.handleKey("TargetESP", "Target ESP", "ESP цілі");
      this.handleKey("JumpCircle", "Jump Circle", "Коло стрибка");
      this.handleKey("CustomWorld", "Custom World", "Світ");
      this.handleKey("InterpolateF5", "Interpolate F5", "Плавний F5");
      this.handleKey("BlockOverlay", "Block Overlay", "Оверлей блоку");
      this.handleKey("ShaderHands", "Shader Hands", "Шейдер рук");
      this.handleKey("Ambience", "Ambience", "Атмосфера");
      this.handleKey(
         "Атмосфера: туман, шейдер неба и время суток", "Ambience: fog, sky shader and day time", "Атмосфера: туман, шейдер неба та час доби"
      );
      this.handleKey("Туман", "Fog", "Туман");
      this.handleKey("Шейдер неба", "Sky Shader", "Шейдер неба");
      this.handleKey("Звёзды", "Stars", "Зірки");
      this.handleKey("Скорость неба", "Sky Speed", "Швидкість неба");
      this.handleKey("Время суток", "Day Time", "Час доби");
      this.handleKey("Время", "Time", "Час");
      this.handleKey("Вокруг", "Around", "Навколо");
      this.handleKey("Радиус", "Radius", "Радіус");
      this.handleKey("Стиль неба", "Sky Style", "Стиль неба");
      this.handleKey("Разводы", "Swirls", "Розводи");
      this.handleKey("Кометы", "Comets", "Комети");
      this.handleKey("Галактика", "Galaxy", "Галактика");
      this.handleKey("Аврора", "Aurora", "Аврора");
      this.handleKey("Плазма", "Plasma", "Плазма");
      this.handleKey("Вихрь", "Vortex", "Вихор");
      this.handleKey("Сфера", "Sphere", "Сфера");
      this.handleKey("ДНК", "DNA", "ДНК");
      this.handleKey("Руны", "Runes", "Руни");
      this.handleKey("Режим", "Mode", "Режим");
      this.handleKey("Мод", "Mode", "Режим");
      this.handleKey("Стиль", "Style", "Стиль");
      this.handleKey("Скорость", "Speed", "Швидкість");
      this.handleKey("Скорость анимации", "Animation Speed", "Швидкість анімації");
      this.handleKey("Скорость вращения", "Rotation Speed", "Швидкість обертання");
      this.handleKey("Скорость волн", "Wave Speed", "Швидкість хвиль");
      this.handleKey("Скорость нитей", "Thread Speed", "Швидкість ниток");
      this.handleKey("Дистанция", "Distance", "Дистанція");
      this.handleKey("Размер", "Size", "Розмір");
      this.handleKey("Прозрачность", "Opacity", "Прозорість");
      this.handleKey("Свечение", "Glow", "Світіння");
      this.handleKey("Космос", "Nebula", "Космос");
      this.handleKey("Скорость космоса", "Nebula Speed", "Швидкість космосу");
      this.handleKey("Плотность туманности", "Nebula Density", "Щільність туманності");
      this.handleKey("Яркость звёзд", "Star Brightness", "Яскравість зірок");
      this.handleKey("Объём тумана", "Fog Volume", "Об'єм туману");
      this.handleKey("Свечение космоса", "Nebula Glow", "Світіння космосу");
      this.handleKey("Перекрытие руки", "Hand Coverage", "Перекриття руки");
      this.handleKey("Сила свечения", "Glow Strength", "Сила світіння");
      this.handleKey("Сила анимации", "Animation Strength", "Сила анімації");
      this.handleKey("Плавность", "Smoothness", "Плавність");
      this.handleKey("Анимация", "Animation", "Анімація");
      this.handleKey("Анимация крыльев", "Wing Animation", "Анімація крил");
      this.handleKey("Анимация свинга", "Swing Animation", "Анімація свінгу");
      this.handleKey("Плавная анимация", "Smooth Animation", "Плавна анімація");
      this.handleKey("Тип частиц", "Particle Type", "Тип частинок");
      this.handleKey("Количество", "Count", "Кількість");
      this.handleKey("Приоритет", "Priority", "Пріоритет");
      this.handleKey("Ротация", "Rotation", "Ротація");
      this.handleKey("Обход", "Bypass", "Обхід");
      this.handleKey("Сервер", "Server", "Сервер");
      this.handleKey("После лута", "After Loot", "Після луту");
      this.handleKey("Элементы", "Elements", "Елементи");
      this.handleKey("Аррай лист", "Array List", "Список модулів");
      this.handleKey("Горячие клавиши", "Key Binds", "Гарячі клавіші");
      this.handleKey("Зелья", "Potions", "Зілля");
      this.handleKey("Таргет худ", "Target HUD", "Таргет HUD");
      this.handleKey("Уведомления", "Notifications", "Сповіщення");
      this.handleKey("Стафф", "Staff", "Стаф");
      this.handleKey("Сессия", "Session", "Сесія");
      this.handleKey("КейСтроки", "Key Strokes", "Кейстроки");
      this.handleKey("Обычный", "Default", "Звичайний");
      this.handleKey("Красивый", "Fancy", "Гарний");
      this.handleKey("Шейдер", "Shader", "Шейдер");
      this.handleKey("Нитки", "Threads", "Нитки");
      this.handleKey("Разлет", "Scatter", "Розліт");
      this.handleKey("Падение", "Fall", "Падіння");
      this.handleKey("Возвращаться", "Return", "Повертатися");
      this.handleKey("Тепаться на спавн", "Teleport to Spawn", "Телепортуватись на спавн");
      this.handleKey("Картинка 1", "Image 1", "Картинка 1");
      this.handleKey("Картинка 2", "Image 2", "Картинка 2");
      this.handleKey("Призраки", "Ghosts", "Привиди");
      this.handleKey("Райдер", "Rider", "Райдер");
      this.handleKey("Души", "Souls", "Души");
      this.handleKey("Кристаллы", "Crystals", "Кристали");
      this.handleKey("Коллизия", "Collision", "Колізія");
      this.handleKey("Тест", "Test", "Тест");
      this.handleKey("Дистанция атаки", "Attack Range", "Дистанція атаки");
      this.handleKey("Только движение", "Only Movement", "Тільки рух");
      this.handleKey("Только при Aura", "Only with Aura", "Тільки з Aura");
      this.handleKey("Только с аурой", "Only with Aura", "Тільки з аурою");
      this.handleKey("Правая рука X", "Right Hand X", "Права рука X");
      this.handleKey("Правая рука Y", "Right Hand Y", "Права рука Y");
      this.handleKey("Правая рука Z", "Right Hand Z", "Права рука Z");
      this.handleKey("Левая рука X", "Left Hand X", "Ліва рука X");
      this.handleKey("Левая рука Y", "Left Hand Y", "Ліва рука Y");
      this.handleKey("Левая рука Z", "Left Hand Z", "Ліва рука Z");
      this.handleKey("Авто-взлёт", "Auto Takeoff", "Авто зліт");
      this.handleKey("Обходить Grim", "Bypass Grim", "Обходити Grim");
      this.handleKey("Крылья", "Wings", "Крила");
      this.handleKey("Крылья 2", "Wings 2", "Крила 2");
      this.handleKey("Китайская шляпа", "China Hat", "Китайський капелюх");
      this.handleKey("ChunkAnimator", "Chunk Animator", "Анімація чанків");
      this.handleKey("Анимация появления чанков", "Chunk appear animation", "Анімація появи чанків");
      this.handleKey("Скорость", "Speed", "Швидкість");
      this.handleKey("Направление", "Direction", "Напрямок");
      this.handleKey("Снизу", "From Below", "Знизу");
      this.handleKey("Сверху", "From Above", "Зверху");
   }

   private void handleKey(String key, String english, String ukrainian) {
      this.displayName.put(key, english);
      this.stringMap.put(key, ukrainian);
   }

   public LocalizationStorage.Language getLanguage() {
      return this.language;
   }

   public void setLanguage(LocalizationStorage.Language language) {
      this.language = language == null ? LocalizationStorage.Language.RUSSIAN : language;
   }

   public void cycleLanguage() {
      this.language = this.language.next();
   }

   public String translateCategory(Module.ModuleCategory category) {
      return this.translate(category.getName());
   }

   public String translate(String key) {
      if (key != null && !key.isEmpty()) {
         return switch (this.language) {
            case RUSSIAN -> key;
            case ENGLISH -> (String)this.displayName.getOrDefault(key, this.resolveKey(key));
            case UKRAINIAN -> (String)this.stringMap.getOrDefault(key, this.resolveKey2(key));
         };
      } else {
         return key;
      }
   }

   private String resolveKey(String key) {
      return key.chars().allMatch(ch -> ch < 128) ? this.resolveKey3(key) : key;
   }

   private String resolveKey2(String key) {
      if (this.stringMap.containsKey(key)) {
         return this.stringMap.get(key);
      } else {
         return key.chars().allMatch(ch -> ch < 128) ? this.resolveKey3(key) : key;
      }
   }

   private String resolveKey3(String key) {
      if (key.indexOf(32) >= 0) {
         return key;
      }

      String var2 = key.replaceAll("([a-z])([A-Z])", "$1 $2");
      var2 = var2.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
      return var2.trim();
   }

   public enum Language {
      RUSSIAN("Русский"),
      ENGLISH("English"),
      UKRAINIAN("Українська");

      private final String displayName;

      Language(String displayName) {
         this.displayName = displayName;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public LocalizationStorage.Language next() {
         LocalizationStorage.Language[] var1 = values();
         return var1[(this.ordinal() + 1) % var1.length];
      }
   }
}
