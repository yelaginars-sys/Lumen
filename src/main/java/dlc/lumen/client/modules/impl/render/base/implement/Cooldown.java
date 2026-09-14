package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Cooldown extends InterfaceProcessing {
   private static final Item[] value = new Item[]{
      Items.ENDER_PEARL,
      Items.CHORUS_FRUIT,
      Items.TOTEM_OF_UNDYING,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.GOLDEN_APPLE,
      Items.FIREWORK_ROCKET,
      Items.SHIELD,
      Items.TRIDENT,
      Items.CROSSBOW,
      Items.GOAT_HORN,
      Items.ENDER_EYE,
      Items.NETHERITE_SCRAP,
      Items.DRIED_KELP,
      Items.SUGAR,
      Items.PHANTOM_MEMBRANE,
      Items.NETHER_STAR,
      Items.PRISMARINE_SHARD,
      Items.POPPED_CHORUS_FRUIT,
      Items.FIRE_CHARGE,
      Items.SNOWBALL,
      Items.POTION
   };
   private final Map<Item, AnimationUtils> linkedHashMap = new LinkedHashMap<>();
   private final Map<Item, Float> items = new HashMap<>();
   private final Map<Item, Integer> items2 = new HashMap<>();
   private final Set<Item> items3 = new HashSet<>();
   private final AnimationUtils animationUtils = new AnimationUtils(70.0F, 10.5F, Easings.QUAD_OUT);
   private static final float VOLUME = 15.0F;
   private static final float VOLUME2 = 13.0F;
   private static final float VOLUME3 = 2.0F;

   public Cooldown(Draggable draggable) {
      super(draggable);
   }

   private Font helper(int size) {
      return Fonts.getFont("suisse", size);
   }

   private Font helper2(int size) {
      return Fonts.getFont("icon1", size);
   }

   private AnimationUtils helper3(Item item) {
      return this.linkedHashMap.computeIfAbsent(item, i -> new AnimationUtils(0.0F, 10.5F, Easings.QUAD_OUT));
   }

   private String resolveString(Item item) {
      if (item == Items.ENDER_PEARL) {
         return "Эндер-перл";
      } else if (item == Items.CHORUS_FRUIT) {
         return "Хорус";
      } else if (item == Items.TOTEM_OF_UNDYING) {
         return "Тотем";
      } else if (item == Items.ENCHANTED_GOLDEN_APPLE) {
         return "Энч. яблоко";
      } else if (item == Items.GOLDEN_APPLE) {
         return "Золотое яблоко";
      } else if (item == Items.FIREWORK_ROCKET) {
         return "Фейерверк";
      } else if (item == Items.SHIELD) {
         return "Щит";
      } else if (item == Items.TRIDENT) {
         return "Трезубец";
      } else if (item == Items.CROSSBOW) {
         return "Арбалет";
      } else if (item == Items.GOAT_HORN) {
         return "Козий рог";
      } else if (item == Items.ENDER_EYE) {
         return "Дезориентация";
      } else if (item == Items.NETHERITE_SCRAP) {
         return "Трапка";
      } else if (item == Items.DRIED_KELP) {
         return "Пласт";
      } else if (item == Items.SUGAR) {
         return "Явная пыль";
      } else if (item == Items.SNOWBALL) {
         return "Ком снега";
      } else if (item == Items.PHANTOM_MEMBRANE) {
         return "Божья аура";
      } else if (item == Items.NETHER_STAR) {
         return "Стан";
      } else if (item == Items.PRISMARINE_SHARD) {
         return "Взрывная трапка";
      } else if (item == Items.POPPED_CHORUS_FRUIT) {
         return "Трапка";
      } else if (item == Items.FIRE_CHARGE) {
         return "Взрывная штучка";
      } else {
         return item == Items.POTION ? "Исцеление" : item.getName().getString();
      }
   }

   private int resolveInt(Item item) {
      if (item == Items.ENDER_PEARL) {
         return 300;
      } else if (item == Items.CHORUS_FRUIT) {
         return 200;
      } else if (item == Items.TOTEM_OF_UNDYING) {
         return 100;
      } else if (item == Items.ENCHANTED_GOLDEN_APPLE) {
         return 400;
      } else if (item == Items.GOLDEN_APPLE) {
         return 100;
      } else if (item == Items.FIREWORK_ROCKET) {
         return 200;
      } else if (item == Items.SHIELD) {
         return 100;
      } else if (item == Items.TRIDENT) {
         return 200;
      } else if (item == Items.CROSSBOW) {
         return 100;
      } else if (item == Items.GOAT_HORN) {
         return 100;
      } else if (item == Items.ENDER_EYE) {
         return 160;
      } else if (item == Items.NETHERITE_SCRAP) {
         return 200;
      } else if (item == Items.DRIED_KELP) {
         return 100;
      } else if (item == Items.SUGAR) {
         return 100;
      } else if (item == Items.SNOWBALL) {
         return 60;
      } else if (item == Items.PHANTOM_MEMBRANE) {
         return 200;
      } else if (item == Items.NETHER_STAR) {
         return 240;
      } else if (item == Items.PRISMARINE_SHARD) {
         return 160;
      } else if (item == Items.POPPED_CHORUS_FRUIT) {
         return 200;
      } else if (item == Items.FIRE_CHARGE) {
         return 160;
      } else {
         return item == Items.POTION ? 60 : 200;
      }
   }

   private static String resolveString2(float seconds) {
      int var1 = Math.max(0, (int)Math.ceil(seconds));
      int var2 = var1 / 60;
      int var3 = var1 % 60;
      return (var2 < 10 ? "0" + var2 : String.valueOf(var2)) + ":" + (var3 < 10 ? "0" + var3 : String.valueOf(var3));
   }

   private void updateState() {
      if (mc != null && mc.player != null) {
         ItemCooldownManager var1 = mc.player.getItemCooldownManager();

         for (Item var5 : value) {
            ItemStack var6 = new ItemStack(var5);
            float var7 = var1.getCooldownProgress(var6, 0.0F);
            if (var7 > 0.0F && var7 < 1.0F) {
               this.items.put(var5, var7);
               int var8 = this.resolveInt(var5);
               Integer var9 = this.items2.get(var5);
               if (var9 == null || var8 > var9) {
                  this.items2.put(var5, var8);
               }
            } else {
               this.items.remove(var5);
            }
         }
      }
   }

   private List<Item> helper4(Set<Item> active) {
      ArrayList<Item> var2 = new ArrayList<>();
      this.items3.clear();

      for (Item var6 : value) {
         if (active.contains(var6) && this.items3.add(var6)) {
            var2.add(var6);
         }
      }

      for (Item var8 : this.linkedHashMap.keySet()) {
         if (!active.contains(var8)) {
            var2.add(var8);
         }
      }

      return var2;
   }

   private void updateState2(EventRender.Default eventRender, Font font, String text, float x, float y, int color) {
      int var7 = ColorUtils.rgba(20, 20, 20, 145);
      font.draw(eventRender.getContext().getMatrices(), text, x + 0.8F, y + 0.8F, var7);
      font.draw(eventRender.getContext().getMatrices(), text, x, y, color);
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      this.updateState();
      this.DefaultStyle(eventRender);
      super.onRender(eventRender);
   }

   private float helper5(Item item) {
      String var2 = this.resolveString(item);
      float var3 = this.items.getOrDefault(item, 0.0F);
      float var4 = var3 * this.resolveInt(item) / 20.0F;
      String var5 = resolveString2(var4);
      float var6 = this.helper(12).getWidth(var2);
      float var7 = Math.max(this.helper(10).getWidth(var5) + 4.0F, 12.0F);
      return 15.0F + var7 + 1.0F + 5.0F + var6 + 5.0F;
   }

   private float helper6() {
      return 15.0F + this.helper(11).getWidth("Cooldowns") + 8.0F + 13.0F;
   }

   public void DefaultStyle(EventRender.Default eventRender) {
      float var2 = this.draggable.getX();
      float var3 = this.draggable.getY();
      int var4;
      if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
         var4 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
      } else {
         var4 = ColorUtils.getThemeColor();
      }

      if (mc != null && mc.player != null) {
         Set var5 = this.items.keySet();

         for (Item var9 : value) {
            if (var5.contains(var9)) {
               this.helper3(var9).update(1.0F);
            } else {
               this.helper3(var9).update(0.0F);
            }
         }

         if (var5.isEmpty()) {
            boolean var32 = mc.currentScreen instanceof ChatScreen;
            if (!var32) {
               this.draggable.setWidth(0.0F);
               this.draggable.setHeight(0.0F);
            } else {
               float var34 = this.helper6();
               MatrixStack var36 = eventRender.getContext().getMatrices();
               drawHudBg(var36, var2, var3, var34, 15.0F);
               this.helper(11).draw(var36, "Cooldowns", var2 + 5.0F + 10.0F, var3 + 7.0F - 1.0F + 0.5F, -1);
               this.helper2(13).draw(var36, "f", var2 + 5.0F, var3 + 7.5F - 1.0F, var4);
               this.draggable.setWidth(var34);
               this.draggable.setHeight(15.0F);
            }
         } else {
             float var31 = 0.0F;
             float var33 = 18.0F;
            List<Item> var35 = this.helper4(var5);

            for (Item var10 : var35) {
               float var11 = this.helper3(var10).getValue();
               if (var11 > 0.01F) {
                  float var12 = this.helper5(var10);
                  if (var12 > var31) {
                     var31 = var12;
                  }

                  var33 += 15.0F * var11;
               }
            }

            float var38 = this.helper6();
            this.animationUtils.update(Math.max(var38, var31));
            float var39 = this.animationUtils.getValue();
            float var40 = var33;
            MatrixStack var41 = eventRender.getContext().getMatrices();
            drawHudBg(var41, var2, var3, var38, 14.0F);
            this.helper(11).draw(var41, "Cooldowns", var2 + 5.0F + 10.0F, var3 + 7.0F - 1.0F + 0.5F - 0.5F, -1);
            this.helper2(13).draw(var41, "f", var2 + 5.0F, var3 + 7.5F - 1.0F, var4);
            float var13 = 17.0F;

            for (Item var15 : var35) {
               float var16 = this.helper3(var15).getValue();
               if (!(var16 <= 0.01F)) {
                  float var17 = var3 + var13;
                  float var18 = this.helper5(var15);
                  int var19 = (int)(255.0F * var16);
                  int var20 = ColorUtils.rgba(255, 255, 255, var19);
                  float var21 = this.items.getOrDefault(var15, 0.0F);
                  float var22 = var21 * this.resolveInt(var15) / 20.0F;
                  String var23 = resolveString2(var22);
                  float var24 = Math.max(this.helper(10).getWidth(var23) + 4.0F, 12.0F);
                  float var25 = 15.0F + var24;
                  float var26 = var18 - var25 - 1.0F;
                  drawHudBg(var41, var2, var17, var25, 13.0F);
                  drawHudBg(var41, var2 + var25 + 1.0F, var17, var26, 13.0F);
                  ScissorUtils.push();
                  ScissorUtils.setFromComponentCoordinates(var2, var17, var18 * this.draggable.getScaleX(), 13.0F * this.draggable.getScaleY());
                  float var27 = var17 + 2.0F + 1.0F;
                  float var28 = var2 + 5.0F;
                  ItemStack var29 = new ItemStack(var15);
                  var41.push();
                  var41.translate(var28, var27, 0.0F);
                  var41.scale(0.45F, 0.45F, 1.0F);
                  eventRender.getContext().drawItem(var29, 0, 0);
                  var41.pop();
                  this.helper(10).drawCenteredString(var41, var23, var2 + 13.0F + var24 / 2.0F, var27 + 3.0F, var20);
                  String var30 = this.resolveString(var15);
                  this.helper(12).draw(var41, var30, var2 + var25 + 1.0F + 5.0F, var27 + 2.0F, var20);
                  ScissorUtils.pop();
                  ScissorUtils.unset();
                  var13 += 15.0F * var16;
               }
            }

            this.linkedHashMap.entrySet().removeIf(entry -> !var5.contains(entry.getKey()) && entry.getValue().getValue() <= 0.01F);
            this.items2.keySet().removeIf(item -> !this.linkedHashMap.containsKey(item));
            this.draggable.setWidth(var39);
            this.draggable.setHeight(var40);
         }
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }

   public void WaveStyle(EventRender.Default eventRender) {
      float var2 = this.draggable.getX();
      float var3 = this.draggable.getY();
      if (mc != null && mc.player != null) {
         Set var4 = this.items.keySet();

         for (Item var8 : value) {
            if (var4.contains(var8)) {
               this.helper3(var8).update(1.0F);
            } else {
               this.helper3(var8).update(0.0F);
            }
         }

         MatrixStack var35 = eventRender.getContext().getMatrices();
         int var36 = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
         int var37 = ColorUtils.getThemeColor(var36);
         int var38 = ColorUtils.getThemeColor(var36 + 30);
         int var9 = ColorUtils.getThemeColor(var36 + 90);
         int var10 = ColorUtils.getThemeColor(var36 + 120);
         int var11 = ColorUtils.getThemeColor(var36 + 180);
         int var12 = ColorUtils.getThemeColor(var36 + 210);
         List<Item> var13 = this.helper4(var4);
         float var14 = 100.0F;
         float var15 = 18.0F;
         int var16 = 0;

         for (Item var18 : var13) {
            AnimationUtils var19 = this.helper3(var18);
            float var20 = var19.getValue();
            if (!(var20 <= 0.01F)) {
               var16++;
               String var21 = this.resolveString(var18);
               float var22 = this.items.getOrDefault(var18, 0.0F);
               float var23 = var22 * this.resolveInt(var18) / 20.0F;
               String var24 = resolveString2(var23);
               var14 = Math.max(var14, this.helper(16).getWidth(var21 + " " + var24) + 38.0F);
               var15 += 18.0F * var20;
            }
         }

         float var39 = 18.0F;
         RenderUtils.drawWaveHudHeader(var35, var2, var3, var14, 15.0F, 0.0F, 10.0F, 10.0F, var37, var38, var9, var10, var11, var12);
         String var40 = "cooldowns";
         float var41 = var2 + (var14 - this.helper(16).getWidth(var40)) / 2.0F;
         this.updateState2(eventRender, this.helper(16), var40, var41, var3 + 5.0F, -1);
         if (var16 == 0) {
            this.draggable.setWidth(var14);
            this.draggable.setHeight(var39);
         } else {
            RenderUtils.drawWaveHudPanel(
               var35, var2, var3, var14, var15, ColorUtils.rgba(25, 25, 25, 150), 15.0F, 0.0F, 10.0F, 10.0F, var37, var38, var9, var10, var11, var12
            );
            float var42 = 20.0F;

            for (Item var44 : var13) {
               AnimationUtils var45 = this.helper3(var44);
               float var46 = var45.getValue();
               if (!(var46 <= 0.01F)) {
                  ScissorUtils.push();
                  ScissorUtils.setFromComponentCoordinates(var2, var3, var14 * this.draggable.getScaleX(), var15 * this.draggable.getScaleY());
                  int var25 = (int)(255.0F * var46);
                  int var26 = ColorUtils.rgba(255, 255, 255, var25);
                  float var27 = var2 + 5.0F;
                  float var28 = var3 + var42;
                  ItemStack var29 = new ItemStack(var44);
                  var35.push();
                  var35.translate(var27, var28, 0.0F);
                  var35.scale(0.7F, 0.7F, 1.0F);
                  eventRender.getContext().drawItem(var29, 0, 0);
                  var35.pop();
                  String var30 = this.resolveString(var44);
                  float var31 = var27 + 14.0F;
                  this.helper(15).draw(var35, var30, var31, var3 + var42 - 1.0F, var26);
                  float var32 = this.items.getOrDefault(var44, 0.0F);
                  float var33 = var32 * this.resolveInt(var44) / 20.0F;
                  String var34 = resolveString2(var33);
                  this.helper(14).draw(var35, var34, var31, var3 + var42 + 7.5, var26);
                  var42 += 18.0F * var46;
                  ScissorUtils.unset();
                  ScissorUtils.pop();
               }
            }

            this.linkedHashMap.entrySet().removeIf(entry -> !var4.contains(entry.getKey()) && entry.getValue().getValue() <= 0.01F);
            this.draggable.setWidth(var14);
            this.draggable.setHeight(var15);
         }
      } else {
         this.draggable.setWidth(80.0F);
         this.draggable.setHeight(18.0F);
      }
   }
}
