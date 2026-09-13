package dlc.lumen.api.utils.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NbtDumpManager {
   private static final File file = new File(System.getProperty("user.dir"), "nbt_dumps.json");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final List<NbtDumpManager.DumpEntry> dumpEntryList = new ArrayList<>();
   private static boolean flag = false;
   public static final List<String> TARGET_NAMES = List.of(
      "Шлем Eternity",
      "Поножи Eternity",
      "Ботинки Eternity",
      "Нагрудник Eternity",
      "Зелье Победителя",
      "Зелье Черепашей мощи",
      "Зелье Черепашьей мощи",
      "Шлем Infinity",
      "Нагрудник Infinity",
      "Поножи Infinity",
      "Ботинки Infinity",
      "Меч Eternity",
      "Меч Infinity",
      "Сфера Цербера",
      "Мифическая сфера",
      "Трапка",
      "Стан",
      "Легендарная сфера",
      "Улучшенное зелье силы",
      "Сфера armortality",
      "Ледяная волна",
      "Взрывная штучка",
      "Динамит B",
      "Динамит A",
      "Динамит Б2",
      "Талисман infinity",
      "Талисман eternity",
      "Кирка dragon",
      "Взрывная трапка",
      "Шар Мидаса",
      "Обычный талисман"
   );

   public static synchronized void ensureLoaded() {
      if (!flag) {
         flag = true;
         dumpEntryList.clear();
         if (file.exists()) {
            try (FileReader var0 = new FileReader(file, StandardCharsets.UTF_8)) {
               Type var1 = (new TypeToken<ArrayList<NbtDumpManager.DumpEntry>>() {}).getType();
               List var2 = (List)gson.fromJson(var0, var1);
               if (var2 != null) {
                  dumpEntryList.addAll(var2);
               }
            } catch (Throwable var5) {
               var5.printStackTrace();
            }
         }
      }
   }

   public static synchronized void save() {
      try {
         File var0 = file.getParentFile();
         if (var0 != null && !var0.exists()) {
            var0.mkdirs();
         }

         try (FileWriter var1 = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(dumpEntryList, var1);
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }
   }

   public static String findTargetNameInStack(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String var1 = stack.getName().getString();
         String var2 = Formatting.strip(var1).toLowerCase();
         String var3 = extractLore(stack);
         String var4 = var3 != null ? Formatting.strip(var3).toLowerCase() : "";
         String var5 = extractCustomDataNbt(stack);
         String var6 = var5 != null ? var5.toLowerCase() : "";
         if (var6.contains("цербер") || var6.contains("cerberus") || var2.contains("цербер") || var4.contains("цербер")) {
            return "§r§#BF0000С§r§#CF0000ф§r§#DF0000е§r§#EF0000р§r§#FF0000а §r§#FF0000Ц§r§#EF0000е§r§#DF0000р§r§#CF0000б§r§#BF0000е§r§#AF0000р§r§#9F0000а";
         }

         if (var6.contains("мифическая_сфера") || var6.contains("mythical") || var2.contains("мифическая сфера")) {
            return "§r§#BF0000М§r§#CF0000и§r§#DF0000ф§r§#EF0000и§r§#FF0000ч§r§#FF0000е§r§#FF0000с§r§#FF0000к§r§#FF0000а§r§#FF0000я§r§#FF0000 §r§#FF0000с§r§#EF0000ф§r§#DF0000е§r§#CF0000р§r§#BF0000а";
         }

         if (var6.contains("арморталити") || var6.contains("armortality") || var2.contains("armortality")) {
            return "§r§#283451С§r§#2B3856ф§r§#2F3C5Bе§r§#324060р§r§#354365а §r§#39476Aᴀ§r§#3C4B6Fʀ§r§#3F4F74ᴍ§r§#3F4F74ᴏ§r§#3C4B6Fʀ§r§#39476Aᴛ§r§#354365ᴀ§r§#324060ʟ§r§#2F3C5Bɪ§r§#2B3856ᴛ§r§#283451ʏ";
         }

         if (!var6.contains("midas") && !var2.contains("мидас")) {
            if (var6.contains("обычная_сфера_броня")
               || var6.contains("легендарная_сфера")
               || var2.contains("легендарная сфера")
               || var2.contains("обычный талисман")) {
               if (stack.isOf(Items.PLAYER_HEAD)) {
                  return "§r§#0082FDЛегендарная сфера";
               }

               if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                  return "§r§#F699FDОбычный талисман";
               }
            }

            if (!stack.isOf(Items.NETHERITE_SWORD) || !var6.contains("rich-enchant-custom\",lvl:1") && !var4.contains("богач i") && !var2.contains("eternity")) {
               if (!stack.isOf(Items.NETHERITE_SWORD)
                  || !var6.contains("rich-enchant-custom\",lvl:6") && !var4.contains("богач vi") && !var2.contains("infinity")) {
                  if (!var6.contains("талисман_infinity") && !var6.contains("name:\"infinity\"") && !var2.contains("infinity") && !var2.contains("ɪɴғɪɴɪᴛʏ")) {
                     if (!var6.contains("сфера_eternity") && !var6.contains("name:\"eternity\"") && !var2.contains("eternity") && !var2.contains("ᴇᴛᴇʀɴɪᴛʏ")) {
                        if (!stack.isOf(Items.NETHERITE_HELMET)
                           || !var6.contains("impenetrable") && !var4.contains("непробиваемый") && !var2.contains("eternity")) {
                           if (!stack.isOf(Items.NETHERITE_CHESTPLATE)
                              || !var6.contains("impenetrable") && !var4.contains("непробиваемый") && !var2.contains("eternity")) {
                              if (!stack.isOf(Items.NETHERITE_PICKAXE)
                                 || !var6.contains("foundry") && !var4.contains("автоплавка") && !var2.contains("dragon") && !var2.contains("infinity")) {
                                 if (var6.contains("win-potion") || var6.contains("custompotioncolor:33461") || var2.contains("победител")) {
                                    return "§r§#0092CCЗ§r§#0099D6е§r§#00A0E0л§r§#00A8EAь§r§#00AFF4е §r§#00B7FFп§r§#00B7FFо§r§#00B7FFб§r§#00B7FFе§r§#00B7FFд§r§#00B0F6и§r§#00AAEEт§r§#00A4E5е§r§#009EDDл§r§#0098D4я";
                                 }

                                 if (!var2.contains("улучшенное зелье силы") && (!var6.contains("strength") || !var6.contains("duration:3600"))) {
                                    if (!var6.contains("explosive_trap")
                                       && !var2.contains("взрывная трапка")
                                       && (!stack.isOf(Items.PRISMARINE_SHARD) || !var6.contains("trap") && !var2.contains("трап"))) {
                                       if (var6.contains("alternative_trap") || var2.contains("трапка")) {
                                          return "§r§#C15BD3Т§r§#C96ED8р§r§#D081DDа§r§#D081DDп§r§#C96ED8к§r§#C15BD3а";
                                       }

                                       if (var6.contains("stun_star") || var2.contains("стан")) {
                                          return "§r§#EFEFEFС§r§#E6E6E6т§r§#DCDCDCа§r§#D3D3D3н";
                                       }

                                       if (var6.contains("pyrotechnic-item:{name:\"ice\"}") || var2.contains("ледяная волна")) {
                                          return "§r§#A4FFFAЛедяная волна";
                                       }

                                       if (var6.contains("explosivestuff") || var2.contains("взрывная штучка")) {
                                          return "§r§#C82700Взрывная штучка";
                                       }

                                       if (var6.contains("pyrotechnic-item:{name:\"b\"}") || var2.contains("динамит b")) {
                                          return "§r§#BD00FDДинамит B";
                                       }

                                       if (var6.contains("pyrotechnic-item:{name:\"a\"}") || var2.contains("динамит a")) {
                                          return "§r§#FD8A00Динамит A";
                                       }

                                       if (var6.contains("pyrotechnic-item:{name:\"b2\"}") || var2.contains("динамит б2")) {
                                          return "§r§#FF0000Динамит Б2";
                                       }

                                       if (var6.contains("snowball") || var2.contains("ком снега")) {
                                          return "§r§#00BCCCКом снега";
                                       }

                                       if (var6.contains("123433") || var2.contains("универсальный ключ")) {
                                          return "§r§#FFAA00Универсальный ключ";
                                       }

                                       if (!stack.isOf(Items.TRIDENT)
                                          || !var6.contains("trident-aim") && !var4.contains("самонаводка") && !var2.contains("eternity")) {
                                          for (String var8 : TARGET_NAMES) {
                                             String var9 = var8.toLowerCase();
                                             if (var2.contains(var9) || var4.contains(var9) || var6.contains(var9)) {
                                                return var8;
                                             }
                                          }

                                          return null;
                                       } else {
                                          return "§r§#CA0076- Трезубец ᴇᴛᴇʀɴɪᴛʏ -";
                                       }
                                    } else {
                                       return "§r§#FF4500В§r§#FF5555з§r§#FF6565р§r§#FF7575ы§r§#FF8585в§r§#FF9595н§r§#FFA5A5а§r§#FFB5B5я §r§#C15BD3Т§r§#C96ED8р§r§#D081DDа§r§#D081DDп§r§#C96ED8к§r§#C15BD3а";
                                    }
                                 } else {
                                    return "§r§#CC0000Улучшенное зелье силы";
                                 }
                              } else {
                                 return "§r§#A600BDКирка §r§#E000FFᴅʀᴀɢᴏɴ";
                              }
                           } else {
                              return "§r§#CA0076- §r§#D00079Нагрудник §r§#FF0095ᴇᴛᴇʀɴɪᴛʏ §r§#CA0076-";
                           }
                        } else {
                           return "§r§#CA0076- §r§#D2007BШлем §r§#FB0093ᴇᴛᴇʀɴɪᴛʏ §r§#CA0076-";
                        }
                     } else {
                        return stack.isOf(Items.TOTEM_OF_UNDYING)
                           ? "§r§#CA0076- §r§#D0007AТалисман §r§#FC0093ᴇᴛᴇʀɴɪᴛʏ §r§#CA0076-"
                           : "§r§#CA0076- §r§#D0007AСфера §r§#FC0093ᴇᴛᴇʀɴɪᴛʏ §r§#CA0076-";
                     }
                  } else {
                     return stack.isOf(Items.TOTEM_OF_UNDYING)
                        ? "§r§#00B612- §r§#00C013Талисман §r§#00FA19ɪɴғɪɴɪᴛʏ §r§#00B612-"
                        : "§r§#00B612- §r§#00C013Сфера §r§#00FA19ɪɴғɪɴɪᴛʏ §r§#00B612-";
                  }
               } else {
                  return "§r§#24B500- §r§#27C400Меч §r§#30F200Iɴғɪɴɪᴛʏ §r§#24B500-";
               }
            } else {
               return "§r§#CA0076- §r§#D3007BМеч §r§#FF0095ᴇᴛᴇʀɴɪᴛʏ §r§#CA0076-";
            }
         } else {
            return "§r§#FFB000Ш§r§#FFB800а§r§#FFC000р §r§#FFD100М§r§#FFD900и§r§#FFCF00д§r§#FFC500а§r§#FFBA00с§r§#FFB000а";
         }
      } else {
         return null;
      }
   }

   public static boolean isDefaultItemName(ItemStack stack, String name) {
      if (stack != null && !stack.isEmpty() && name != null) {
         try {
            String var2 = stack.getItem().getName().getString();
            return Formatting.strip(var2).equalsIgnoreCase(Formatting.strip(name));
         } catch (Throwable var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static NbtDumpManager.DumpEntry dumpItem(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         ensureLoaded();
         String var1 = Registries.ITEM.getId(stack.getItem()).toString();
         String var2 = stack.getName().getString();
         String var3 = extractProfileName(stack);
         String var4 = extractCustomDataNbt(stack);
         String var5 = extractLore(stack);
         String var6 = extractEnchantments(stack);
         String var7 = "dump_" + System.currentTimeMillis();
         String var8 = var2;
         String var9 = findTargetNameInStack(stack);
         if (var9 != null) {
            var8 = var9;
         } else if (var3 != null && !var3.isEmpty()) {
            var8 = "Голова " + var3;
         }

         NbtDumpManager.DumpEntry var10 = new NbtDumpManager.DumpEntry(var7, var1, var2, var8, var3, var4, var5, var6, System.currentTimeMillis());
         dumpEntryList.add(var10);
         save();
         return var10;
      } else {
         return null;
      }
   }

   public static String getCustomDumpedName(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         ensureLoaded();
         String var1 = Registries.ITEM.getId(stack.getItem()).toString();
         String var2 = extractProfileName(stack);
         String var3 = extractCustomDataNbt(stack);
         String var4 = stack.getName().getString();
         String var5 = Formatting.strip(var4);
         String var6 = extractLore(stack);
         String var7 = var6 != null ? Formatting.strip(var6) : "";

         for (NbtDumpManager.DumpEntry var9 : dumpEntryList) {
            if (var9.itemId.equalsIgnoreCase(var1)) {
               if (var9.profileName == null || var9.profileName.isEmpty()) {
                  if (var9.customDataNbt != null && !var9.customDataNbt.isEmpty() && var3 != null && checkTargetNbt(var9.customDataNbt, var3)) {
                     return resolveEntry(var9);
                  }

                  if (var9.lore != null && !var9.lore.isEmpty() && !var7.isEmpty() && var7.toLowerCase().contains(Formatting.strip(var9.lore).toLowerCase())) {
                     return resolveEntry(var9);
                  }

                  if (var9.displayName != null && !var9.displayName.isEmpty()) {
                     String var10 = Formatting.strip(var9.displayName);
                     if (!isDefaultItemName(stack, var10)
                        && (var4.equalsIgnoreCase(var9.displayName) || var5 != null && var10 != null && var5.equalsIgnoreCase(var10))) {
                        return resolveEntry(var9);
                     }
                  }
               } else if (var2 != null && var2.equalsIgnoreCase(var9.profileName)) {
                  return resolveEntry(var9);
               }
            }
         }

         String var11 = findTargetNameInStack(stack);
         return var11 != null ? var11 : null;
      } else {
         return null;
      }
   }

   private static boolean checkTargetNbt(String targetNbt, String currentNbt) {
      if (targetNbt == null || currentNbt == null) {
         return false;
      }

      if (targetNbt.equalsIgnoreCase(currentNbt)) {
         return true;
      }

      String var2 = targetNbt.replaceAll("\\s+", "");
      String var3 = currentNbt.replaceAll("\\s+", "");
      return var2.equals(var3);
   }

   private static String resolveEntry(NbtDumpManager.DumpEntry entry) {
      return entry.customDumpName != null && !entry.customDumpName.isEmpty() ? entry.customDumpName : entry.displayName;
   }

   public static List<NbtDumpManager.DumpEntry> getDumps() {
      ensureLoaded();
      return dumpEntryList;
   }

   public static String extractProfileName(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         try {
            ProfileComponent var1 = stack.get(DataComponentTypes.PROFILE);
            if (var1 != null) {
               if (var1.name().isPresent()) {
                  return var1.name().get();
               }

               if (var1.gameProfile() != null && var1.gameProfile().getName() != null) {
                  return var1.gameProfile().getName();
               }
            }
         } catch (Throwable var5) {
         }

         try {
            NbtComponent var6 = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (var6 != null && !var6.isEmpty()) {
               NbtCompound var2 = var6.copyNbt();
               if (var2 != null && var2.contains("SkullOwner")) {
                  NbtElement var3 = var2.get("SkullOwner");
                  if (var3 != null) {
                     return var3.asString();
                  }
               }
            }
         } catch (Throwable var4) {
         }

         return null;
      } else {
         return null;
      }
   }

   public static String extractCustomDataNbt(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         try {
            NbtComponent var1 = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (var1 != null && !var1.isEmpty()) {
               NbtCompound var2 = var1.copyNbt();
               if (var2 != null) {
                  return var2.toString();
               }
            }
         } catch (Throwable var3) {
         }

         return null;
      } else {
         return null;
      }
   }

   public static String extractLore(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         try {
            LoreComponent var1 = stack.get(DataComponentTypes.LORE);
            if (var1 != null && !var1.lines().isEmpty()) {
               StringBuilder var2 = new StringBuilder();

               for (Text var4 : var1.lines()) {
                  var2.append(var4.getString()).append("\n");
               }

               return var2.toString().trim();
            }
         } catch (Throwable var5) {
         }

         return null;
      } else {
         return null;
      }
   }

   public static String extractEnchantments(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         try {
            ItemEnchantmentsComponent var1 = stack.get(DataComponentTypes.ENCHANTMENTS);
            if (var1 != null && !var1.isEmpty()) {
               return var1.toString();
            }
         } catch (Throwable var2) {
         }

         return null;
      } else {
         return null;
      }
   }

   public static String stripFormatting(String input) {
      if (input == null) {
         return "";
      }

      String var1 = input.replaceAll("(?i)[§&]#[0-9a-fA-F]{6}", "");
      var1 = var1.replaceAll("(?i)[§&]x([§&][0-9a-fA-F]){6}", "");
      var1 = var1.replaceAll("(?i)[§&][0-9a-fA-FK-ORk-or]", "");
      return var1.trim();
   }

   public static class DumpEntry {
      public String id;
      public String itemId;
      public String displayName;
      public String customDumpName;
      public String profileName;
      public String customDataNbt;
      public String lore;
      public String enchantments;
      public long dumpTime;

      public DumpEntry() {
      }

      public DumpEntry(
         String id,
         String itemId,
         String displayName,
         String customDumpName,
         String profileName,
         String customDataNbt,
         String lore,
         String enchantments,
         long dumpTime
      ) {
         this.id = id;
         this.itemId = itemId;
         this.displayName = displayName;
         this.customDumpName = customDumpName;
         this.profileName = profileName;
         this.customDataNbt = customDataNbt;
         this.lore = lore;
         this.enchantments = enchantments;
         this.dumpTime = dumpTime;
      }
   }
}
