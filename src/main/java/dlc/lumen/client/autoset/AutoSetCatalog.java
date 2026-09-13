package dlc.lumen.client.autoset;

import dlc.lumen.client.autobuy.ItemEntry;
import dlc.lumen.client.autobuy.Marker;
import java.util.ArrayList;
import java.util.List;

public final class AutoSetCatalog {
   public static final String[] GROUP_KEYS = new String[]{"armor", "weapons", "tools", "potions", "spheres", "misc"};
   public static final String[] GROUP_LABELS = new String[]{"Броня", "Оружие", "Кирки", "Зелья", "Сферы", "Прочее"};
   private static final List<AutoSetItem> ITEMS = new ArrayList<>();

   private AutoSetCatalog() {
   }

   public static List<AutoSetItem> list() {
      return ITEMS;
   }

   public static List<AutoSetItem> byGroup(String groupKey) {
      ArrayList var1 = new ArrayList();
      if (groupKey == null) {
         return var1;
      }

      for (AutoSetItem var3 : ITEMS) {
         if (groupKey.equals(var3.group)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public static AutoSetItem byKey(String key) {
      if (key == null) {
         return null;
      }

      for (AutoSetItem var2 : ITEMS) {
         if (key.equals(var2.key())) {
            return var2;
         }
      }

      return null;
   }

   private static void registerItem(String group, String display, String search, ItemEntry entry) {
      addCatalogItem(group, display, search, entry, false);
   }

   private static void registerStackItem(String group, String display, String search, ItemEntry entry) {
      addCatalogItem(group, display, search, entry, true);
   }

   private static void addCatalogItem(String group, String display, String search, ItemEntry entry, boolean wholeStack) {
      entry.searchQuery = search;
      ITEMS.add(new AutoSetItem(entry, group, display, search, wholeStack));
   }

   private static ItemEntry newEntry(String id) {
      ItemEntry var1 = new ItemEntry();
      var1.itemId = id;
      return var1;
   }

   private static ItemEntry newNamedEntry(String id, String name) {
      ItemEntry var2 = newEntry(id);
      var2.displayName = name;
      return var2;
   }

   private static ItemEntry newPotionEntry(String id, String effectId) {
      ItemEntry var2 = newEntry(id);
      var2.potionEffect = effectId;
      return var2;
   }

   private static ItemEntry newCustomDataEntry(String id, String markerKey, String markerValue) {
      ItemEntry var3 = newEntry(id);
      var3.requireCustom = true;
      var3.markers.add(Marker.customData(markerKey, markerValue));
      return var3;
   }

   private static ItemEntry newEnchantedEntry(String id, String keyword, String enchId) {
      ItemEntry var3 = newEntry(id);
      var3.loreKeyword = keyword;
      var3.requireCustom = true;
      var3.markers.add(Marker.customEnch(enchId));
      return var3;
   }

   private static ItemEntry newSwordEntry(String keyword, String enchId) {
      ItemEntry var2 = newEntry("minecraft:netherite_sword");
      var2.loreKeyword = keyword;
      var2.matchAnySword = true;
      var2.requireCustom = true;
      var2.markers.add(Marker.customEnch(enchId));
      return var2;
   }

   static {
      registerItem("armor", "Шлем ETERNITY", "шлем eternity", newNamedEntry("minecraft:netherite_helmet", "Шлем ETERNITY"));
      registerItem("armor", "Нагрудник ETERNITY", "нагрудник eternity", newNamedEntry("minecraft:netherite_chestplate", "Нагрудник ETERNITY"));
      registerItem("armor", "Поножи ETERNITY", "поножи eternity", newNamedEntry("minecraft:netherite_leggings", "Поножи ETERNITY"));
      registerItem("armor", "Ботинки ETERNITY", "ботинки eternity", newNamedEntry("minecraft:netherite_boots", "Ботинки ETERNITY"));
      registerItem("armor", "Шлем INFINITY", "шлем infinity", newNamedEntry("minecraft:netherite_helmet", "Шлем INFINITY"));
      registerItem("armor", "Нагрудник INFINITY", "нагрудник infinity", newNamedEntry("minecraft:netherite_chestplate", "Нагрудник INFINITY"));
      registerItem("armor", "Поножи INFINITY", "поножи infinity", newNamedEntry("minecraft:netherite_leggings", "Поножи INFINITY"));
      registerItem("armor", "Ботинки INFINITY", "ботинки infinity", newNamedEntry("minecraft:netherite_boots", "Ботинки INFINITY"));
      registerItem("weapons", "Меч ETERNITY", "меч eternity", newSwordEntry("ETERNITY", "enchantments:rich-enchant-custom"));
      registerItem("weapons", "Меч INFINITY", "меч infinity", newSwordEntry("Infinity", "enchantments:rich-enchant-custom"));
      registerItem("weapons", "Меч Цербера", "меч цербера", newSwordEntry("Цербер", "enchantments:rich-enchant-custom"));
      registerItem("weapons", "Меч Фармер", "фармер", newSwordEntry("Фармер", "enchantments:mob-farmer-enchant"));
      registerItem("weapons", "Трезубец ETERNITY", "трезубец eternity", newNamedEntry("minecraft:trident", "Трезубец ETERNITY"));
      registerItem("weapons", "Трезубец INFINITY", "трезубец infinity", newNamedEntry("minecraft:trident", "Трезубец INFINITY"));
      registerItem(
         "tools", "Кирка INFINITY", "кирка infinity", newEnchantedEntry("minecraft:netherite_pickaxe", "INFINITY", "enchantments:foundry-enchant-custom")
      );
      registerItem(
         "tools", "Кирка STINGER", "кирка стингер", newEnchantedEntry("minecraft:netherite_pickaxe", "STINGER", "enchantments:foundry-enchant-custom")
      );
      registerItem(
         "tools", "Кирка ETERNITY", "кирка eternity", newEnchantedEntry("minecraft:netherite_pickaxe", "ETERNITY", "enchantments:foundry-enchant-custom")
      );
      registerItem(
         "tools", "Золотая кирка Джейка", "золотая кирка джейка", newEnchantedEntry("minecraft:golden_pickaxe", null, "enchantments:spawner-getter-enchant")
      );
      registerItem("potions", "Зелье силы III", "зелье сила", newNamedEntry("minecraft:potion", "Улучшенное зелье силы"));
      registerItem("potions", "Зелье скорости III", "зелье скорость", newNamedEntry("minecraft:potion", "Улучшенное зелье скорости"));
      registerItem("potions", "Зелье исцеления", "зелье исцеление", newNamedEntry("minecraft:potion", "Зелье исцеления"));
      registerItem("potions", "Зелье победителя", "зелье победителя", newNamedEntry("minecraft:potion", "Зелье победителя"));
      registerItem("potions", "Зелье невидимости", "зелье невидимости", newPotionEntry("minecraft:potion", "minecraft:invisibility"));
      registerItem("potions", "Зелье черепашьей мощи", "зелье черепашьей мощи", newNamedEntry("minecraft:potion", "Зелье черепашьей мощи"));
      registerItem("spheres", "Сфера ARMORTALITY", "сфера armortality", newNamedEntry("minecraft:player_head", "Сфера ARMORTALITY"));
      registerItem("spheres", "Сфера IMMORTALITY", "сфера immortality", newNamedEntry("minecraft:player_head", "Сфера IMMORTALITY"));
      registerItem("spheres", "Сфера ETERNITY", "сфера eternity", newNamedEntry("minecraft:player_head", "Сфера ETERNITY"));
      registerItem("spheres", "Мифическая сфера", "мифическая сфера", newNamedEntry("minecraft:player_head", "Мифическая сфера"));
      registerItem("spheres", "Легендарная сфера", "легендарная сфера", newNamedEntry("minecraft:player_head", "Легендарная сфера"));
      registerItem("spheres", "Сфера Цербера", "сфера цербера", newNamedEntry("minecraft:player_head", "Сфера Цербера"));
      registerItem("spheres", "Талисман INFINITY", "талисман infinity", newNamedEntry("minecraft:totem_of_undying", "Талисман INFINITY"));
      registerItem("spheres", "Талисман ETERNITY", "талисман eternity", newNamedEntry("minecraft:totem_of_undying", "Талисман ETERNITY"));
      registerItem("spheres", "Тотем бессмертия", "тотем бессмертия", newNamedEntry("minecraft:totem_of_undying", "тотем"));
      registerItem("misc", "Стан", "стан", newNamedEntry("minecraft:nether_star", "Стан"));
      registerItem("misc", "Взрывная штучка", "взрывная штучка", newNamedEntry("minecraft:fire_charge", "Взрывная штучка"));
      registerItem("misc", "Трапка", "трапка", newCustomDataEntry("minecraft:popped_lumen_fruit", "pyrotechnic-item.name", "ALTERNATIVE_TRAP"));
      registerItem("misc", "Взрывная трапка", "взрывная трапка", newCustomDataEntry("minecraft:prismarine_shard", "pyrotechnic-item.name", "EXPLOSIVE_TRAP"));
      registerStackItem("misc", "Эндер-жемчуг", "эндер-жемчуг", newEntry("minecraft:ender_pearl"));
      registerStackItem("misc", "Плод хоруса", "плод хоруса", newEntry("minecraft:lumen_fruit"));
      registerStackItem("misc", "Золотая морковь", "золотая морковь", newEntry("minecraft:golden_carrot"));
      registerItem("misc", "Снежок", "снежок", newEntry("minecraft:snowball"));
      registerItem("misc", "Золотое яблоко", "золотое яблоко", newEntry("minecraft:golden_apple"));
      registerItem("misc", "Зачар. золотое яблоко", "зачарованное золотое яблоко", newEntry("minecraft:enchanted_golden_apple"));
   }
}
