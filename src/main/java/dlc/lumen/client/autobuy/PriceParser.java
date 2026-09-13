package dlc.lumen.client.autobuy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public final class PriceParser {
   private static final Pattern PATTERN = Pattern.compile("Цена за 1 ед\\.?:\\s*([\\d\\s\\u00A0]+)");
   private static final Pattern PATTERN2 = Pattern.compile("(?:^|\\n|\\r)Цена:\\s*([\\d\\s\\u00A0]+)");
   private static final Pattern PATTERN3 = Pattern.compile("(?:Прочность|Прочка|Durability)[^\\d]*([\\d]+)\\s*%");
   private static final Pattern PATTERN4 = Pattern.compile("(minecraft|[a-z_]+):([a-z0-9_]+)");
   private static final Pattern PATTERN5 = Pattern.compile("(?:Продавец|Seller|Игрок|Владелец)[:\\s]+([A-Za-z0-9_]{2,32})");

   private PriceParser() {
   }

   public static Lot parse(int slot, ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         if (stack.getItem() == Items.BARRIER) {
            return Lot.barrier(slot);
         }

         String var2 = stack.getName().getString();
         StringBuilder var3 = new StringBuilder();
         LoreComponent var4 = stack.get(DataComponentTypes.LORE);
         if (var4 != null) {
            for (Text var6 : var4.lines()) {
               var3.append(var6.getString()).append('\n');
            }
         }

         String var20 = var3.toString();
         long var21 = resolveLong(PATTERN, var20);
         long var8 = resolveLong(PATTERN2, var20);
         String var10 = resolveString2(var20);
         if (var10 == null) {
            var10 = Registries.ITEM.getId(stack.getItem()).toString();
         }

         int var11 = stack.getCount();
         if (var21 <= 0L && var8 > 0L && var11 > 0) {
            var21 = var8 / var11;
         }

         if (var21 <= 0L) {
            return null;
         }

         int var12 = resolveInt(stack, var20);
         String var13 = "";
         Matcher var14 = PATTERN5.matcher(var20);
         if (var14.find()) {
            var13 = var14.group(1);
         }

         HashMap var15 = new HashMap();
         HashSet var16 = new HashSet();
         HashMap var17 = new HashMap();
         boolean var18 = checkCondition(stack, var15, var16, var17);
         Lot var19 = new Lot(slot, var10, var2, var20, var13, var11, var21, var8, var12, false, var18, var15, var16, var17);
         var19.potionEffects = computeSet(stack);
         return var19;
      } else {
         return null;
      }
   }

   public static Lot parseForMatch(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.getItem() != Items.BARRIER) {
         String var1 = stack.getName().getString();
         StringBuilder var2 = new StringBuilder();
         LoreComponent var3 = stack.get(DataComponentTypes.LORE);
         if (var3 != null) {
            for (Text var5 : var3.lines()) {
               var2.append(var5.getString()).append('\n');
            }
         }

         String var12 = var2.toString();
         String var13 = Registries.ITEM.getId(stack.getItem()).toString();
         int var6 = resolveInt(stack, var12);
         HashMap var7 = new HashMap();
         HashSet var8 = new HashSet();
         HashMap var9 = new HashMap();
         boolean var10 = checkCondition(stack, var7, var8, var9);
         Lot var11 = new Lot(-1, var13, var1, var12, "", stack.getCount(), 0L, 0L, var6, false, var10, var7, var8, var9);
         var11.potionEffects = computeSet(stack);
         return var11;
      } else {
         return null;
      }
   }

   private static Set<String> computeSet(ItemStack stack) {
      HashSet var1 = new HashSet();

      try {
         PotionContentsComponent var2 = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 != null) {
            for (StatusEffectInstance var4 : var2.getEffects()) {
               var1.add(var4.getEffectType().getIdAsString());
            }
         }
      } catch (Throwable var5) {
      }

      return var1;
   }

   private static boolean checkCondition(ItemStack stack, Map<String, String> pbv, Set<String> ench, Map<String, String> custom) {
      boolean var4 = false;

      try {
         if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
            var4 = true;
         }
      } catch (Throwable var17) {
      }

      try {
         ItemEnchantmentsComponent var5 = stack.get(DataComponentTypes.ENCHANTMENTS);
         if (var5 != null && !var5.isEmpty()) {
            var4 = true;
         }
      } catch (Throwable var16) {
      }

      try {
         NbtComponent var19 = stack.get(DataComponentTypes.CUSTOM_DATA);
         if (var19 == null || var19.isEmpty()) {
            return var4;
         }

         var4 = true;
         NbtCompound var6 = var19.copyNbt();
         if (var6 == null) {
            return var4;
         }

         if (var6.get("PublicBukkitValues") instanceof NbtCompound var8) {
            for (String var10 : var8.getKeys()) {
               pbv.put(var10, resolveString(var8.get(var10)));
            }
         }

         NbtElement var20 = var6.get("Enchantments");
         if (var20 instanceof NbtList) {
            for (NbtElement var11 : (NbtList)var20) {
               if (var11 instanceof NbtCompound var12) {
                  NbtElement var13 = var12.get("id");
                  if (var13 != null) {
                     String var14 = resolveString(var13);
                     if (var14 != null && !var14.isEmpty() && !var14.startsWith("minecraft:")) {
                        ench.add(var14);
                     }
                  }
               }
            }
         }

         for (String var24 : var6.getKeys()) {
            if (!var24.equals("PublicBukkitValues")
               && !var24.equals("Enchantments")
               && !var24.equals("display")
               && !var24.equals("HideFlags")
               && !var24.startsWith("VV|")) {
               NbtElement var25 = var6.get(var24);
               if (var25 != null) {
                  if (var25 instanceof NbtCompound var26) {
                     for (String var28 : var26.getKeys()) {
                        NbtElement var15 = var26.get(var28);
                        if (var15 != null && !(var15 instanceof NbtCompound) && !(var15 instanceof NbtList)) {
                           custom.put(var24 + "." + var28, resolveString(var15));
                        }
                     }
                  } else if (!(var25 instanceof NbtList)) {
                     custom.put(var24, resolveString(var25));
                  }
               }
            }
         }
      } catch (Throwable var18) {
      }

      return var4;
   }

   private static String resolveString(NbtElement el) {
      if (el == null) {
         return "";
      }

      String var1 = el.toString();
      if (!(el instanceof NbtString)) {
         if (!var1.isEmpty()) {
            char var2 = var1.charAt(var1.length() - 1);
            if ("bBsSlLfFdD".indexOf(var2) >= 0 && var1.length() > 1) {
               String var3 = var1.substring(0, var1.length() - 1);
               if (var3.matches("-?\\d+(\\.\\d+)?")) {
                  return var3;
               }
            }
         }

         return var1;
      } else {
         if (var1.length() >= 2 && var1.charAt(0) == '"' && var1.charAt(var1.length() - 1) == '"') {
            var1 = var1.substring(1, var1.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
         } else if (var1.length() >= 2 && var1.charAt(0) == '\'' && var1.charAt(var1.length() - 1) == '\'') {
            var1 = var1.substring(1, var1.length() - 1).replace("\\'", "'").replace("\\\\", "\\");
         }

         return var1;
      }
   }

   private static int resolveInt(ItemStack stack, String lore) {
      Matcher var2 = PATTERN3.matcher(lore);
      if (var2.find()) {
         try {
            return computereturn(Integer.parseInt(var2.group(1)));
         } catch (NumberFormatException var4) {
         }
      }

      if (stack.isDamageable() && stack.getMaxDamage() > 0) {
         int var3 = stack.getMaxDamage() - stack.getDamage();
         return computereturn((int)Math.round(var3 * 100.0 / stack.getMaxDamage()));
      } else {
         return 100;
      }
   }

   private static int computereturn(int v) {
      return Math.max(0, Math.min(100, v));
   }

   private static long resolveLong(Pattern pattern, String text) {
      Matcher var2 = pattern.matcher(text);
      if (!var2.find()) {
         return -1L;
      }

      String var3 = var2.group(1).replaceAll("[\\s\\u00A0]+", "");
      if (var3.isEmpty()) {
         return -1L;
      }

      try {
         return Long.parseLong(var3);
      } catch (NumberFormatException var5) {
         return -1L;
      }
   }

   private static String resolveString2(String text) {
      Matcher var1 = PATTERN4.matcher(text);
      return var1.find() ? var1.group(0) : null;
   }
}
