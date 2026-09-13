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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class Potions extends InterfaceProcessing {
   private final Map<StatusEffect, AnimationUtils> linkedHashMap = new LinkedHashMap<>();
   private final Map<StatusEffect, Potions.PotionSnapshot> statusEffects = new HashMap<>();
   private final Map<StatusEffect, Integer> statusEffects2 = new HashMap<>();
   private final Set<StatusEffect> statusEffects3 = new HashSet<>();
   private final AnimationUtils animationUtils = new AnimationUtils(70.0F, 10.5F, Easings.QUAD_OUT);
   private static final float VOLUME = 15.0F;
   private static final float VOLUME2 = 13.0F;
   private static final float VOLUME3 = 2.0F;

   public Potions(Draggable draggable) {
      super(draggable);
   }

   private Font helper(int size) {
      return Fonts.getFont("suisse", size);
   }

   private Font helper2(int size) {
      return Fonts.getFont("icon1", size);
   }

   private AnimationUtils helper3(StatusEffect effect) {
      return this.linkedHashMap.computeIfAbsent(effect, e -> new AnimationUtils(0.0F, 10.5F, Easings.QUAD_OUT));
   }

   private float helper4(Potions.PotionSnapshot snapshot, StatusEffect type) {
      String var3 = snapshot.baseName != null ? snapshot.baseName : I18n.translate(type.getTranslationKey());
      String var4 = helper6(snapshot.amplifier);
      String var5 = helper7(snapshot.duration, snapshot.infinite);
      float var6 = this.helper(12).getWidth(var3);
      if (!var4.isEmpty()) {
         var6 += this.helper(10).getWidth(" LVL") + this.helper(11).getWidth(var4);
      }

      float var7 = Math.max(this.helper(10).getWidth(var5) + 4.0F, 12.0F);
      return 15.0F + var7 + 1.0F + 5.0F + var6 + 5.0F;
   }

   private float helper5() {
      return 15.0F + this.helper(11).getWidth("Potions") + 8.0F + 13.0F;
   }

   private static String helper6(int level) {
      return String.valueOf(Math.max(1, level));
   }

   private static String resolveString(StatusEffectInstance effect) {
      return helper7(effect.getDuration(), effect.isInfinite());
   }

   private static String helper7(int duration, boolean infinite) {
      if (infinite) {
         return "inf";
      }

      int var2 = Math.max(0, duration / 20);
      int var3 = var2 / 60;
      int var4 = var2 % 60;
      return (var3 < 10 ? "0" + var3 : String.valueOf(var3)) + ":" + (var4 < 10 ? "0" + var4 : String.valueOf(var4));
   }

   private void updateState(StatusEffectInstance effect) {
      StatusEffect var2 = effect.getEffectType().value();
      Potions.PotionSnapshot var3 = this.statusEffects.computeIfAbsent(var2, e -> new Potions.PotionSnapshot());
      var3.entry = effect.getEffectType();
      var3.baseName = I18n.translate(effect.getTranslationKey());
      var3.amplifier = effect.getAmplifier() + 1;
      var3.duration = effect.getDuration();
      var3.infinite = effect.isInfinite();
   }

   private List<StatusEffect> helper8(Collection<StatusEffectInstance> effects, Set<StatusEffect> active) {
      ArrayList<StatusEffect> var3 = new ArrayList<>();
      this.statusEffects3.clear();

      for (StatusEffectInstance var5 : effects) {
         StatusEffect var6 = var5.getEffectType().value();
         if (this.statusEffects3.add(var6)) {
            var3.add(var6);
         }
      }

      for (StatusEffect var8 : this.linkedHashMap.keySet()) {
         if (!active.contains(var8)) {
            var3.add(var8);
         }
      }

      return var3;
   }

   private void updateState2(EventRender.Default eventRender, RegistryEntry<StatusEffect> effect, float x, float y, int size, int alpha) {
      Sprite var7 = mc.getStatusEffectSpriteManager().getSprite(effect);
      int var8 = ColorUtils.rgba(255, 255, 255, alpha);
      RenderUtils.drawSprite(eventRender.getContext().getMatrices(), var7, x, y, size, var8);
   }

   private void updateState3(EventRender.Default eventRender, Font font, String text, float x, float y, int color) {
      int var7 = ColorUtils.rgba(20, 20, 20, 145);
      font.draw(eventRender.getContext().getMatrices(), text, x + 0.8F, y + 0.8F, var7);
      font.draw(eventRender.getContext().getMatrices(), text, x, y, color);
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      this.DefaultStyle(eventRender);
      super.onRender(eventRender);
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

      Collection<StatusEffectInstance> var5 = mc != null && mc.player != null ? mc.player.getStatusEffects() : List.of();
      if (var5.isEmpty()) {
         boolean var39 = mc.currentScreen instanceof ChatScreen;
         if (!var39) {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         } else {
            float var40 = this.helper5();
            MatrixStack var41 = eventRender.getContext().getMatrices();
            drawHudBg(var41, var2, var3, var40, 15.0F);
            this.helper(11).draw(var41, "Potions", var2 + 5.0F + 10.0F, var3 + 7.0F - 1.0F + 0.5F, -1);
            this.helper2(13).draw(var41, "d", var2 + 5.0F, var3 + 7.5F - 1.0F, var4);
            this.draggable.setWidth(var40);
            this.draggable.setHeight(15.0F);
         }
      } else {
         float var6 = 70.0F;
         float var7 = 15.0F;
         HashSet<StatusEffect> var8 = new HashSet<>();

         for (StatusEffectInstance var10 : var5) {
            StatusEffect var11 = var10.getEffectType().value();
            var8.add(var11);
            this.helper3(var11).update(1.0F);
            this.updateState(var10);
            int var12 = var10.getDuration();
            Integer var13 = this.statusEffects2.get(var11);
            if (var13 == null || var12 > var13) {
               this.statusEffects2.put(var11, var12);
            }
         }

         for (Entry var44 : this.linkedHashMap.entrySet()) {
            if (!var8.contains(var44.getKey())) {
               ((AnimationUtils)var44.getValue()).update(0.0F);
            }
         }

         List<StatusEffect> var43 = this.helper8(var5, var8);

         for (StatusEffect var47 : var43) {
            AnimationUtils var49 = this.helper3(var47);
            float var51 = var49.getValue();
            Potions.PotionSnapshot var14 = this.statusEffects.get(var47);
            if (var51 > 0.01F && var14 != null) {
               float var15 = this.helper4(var14, var47);
               if (var15 > var6) {
                  var6 = var15;
               }

               var7 += 15.0F * var51;
            }
         }

         float var46 = this.helper5();
         this.animationUtils.update(Math.max(var46, var6));
         float var48 = this.animationUtils.getValue();
         float var50 = var7;
         MatrixStack var52 = eventRender.getContext().getMatrices();
         drawHudBg(var52, var2, var3, var46, 14.0F);
         this.helper(13).draw(var52, "Potions", var2 + 5.0F + 10.0F, var3 + 7.0F - 1.0F + 1.0F - 1.5F, -1);
         this.helper2(13).draw(var52, "d", var2 + 5.0F, var3 + 8.0F - 1.0F - 1.0F, var4);
         float var53 = 17.0F;

         for (StatusEffect var16 : var43) {
            AnimationUtils var17 = this.helper3(var16);
            float var18 = var17.getValue();
            Potions.PotionSnapshot var19 = this.statusEffects.get(var16);
            if (!(var18 <= 0.01F) && var19 != null) {
               float var20 = var3 + var53;
               float var21 = this.helper4(var19, var16);
               int var22 = (int)(255.0F * var18);
               int var23 = ColorUtils.rgba(255, 255, 255, var22);
               int var24 = ColorUtils.rgba(55, 55, 55, var22);
               String var25 = helper7(var19.duration, var19.infinite);
               float var26 = Math.max(this.helper(10).getWidth(var25) + 4.0F, 12.0F);
               float var27 = 15.0F + var26;
               float var28 = var21 - var27 - 1.0F;
               drawHudBg(var52, var2, var20, var27, 13.0F);
               drawHudBg(var52, var2 + var27 + 1.0F, var20, var28 + 1.0F, 13.0F);
               float var29 = var20 + 2.0F;
               float var30 = 7.0F;
               float var31 = var2 + 5.0F;
               if (var19.entry != null) {
                  this.updateState2(eventRender, var19.entry, var31, var29 + 1.0F, (int)var30, var22);
               }

               this.helper(10).drawCenteredString(var52, var25, var2 + 13.0F + var26 / 2.0F, var29 + 3.0F + 1.0F, var23);
               String var32 = var19.baseName != null ? var19.baseName : I18n.translate(var16.getTranslationKey());
               String var33 = helper6(var19.amplifier);
               float var34 = var2 + var27 + 1.0F + 5.0F;
               float var35 = var29 + 2.0F + 1.0F;
               this.helper(12).draw(var52, var32, var34, var35, var23);
               if (!var33.isEmpty()) {
                  float var36 = this.helper(12).getWidth(var32);
                  int var37 = ColorUtils.setAlphaColor(var4, var22);
                  float var38 = var34 + var36;
                  this.helper(10).draw(var52, " LVL", var38, var35 + 1.0F, var37);
                  this.helper(11).draw(var52, var33, var38 + this.helper(11).getWidth(" LVL"), var35 + 0.5, var37);
               }

               var53 += 15.0F * var18;
            }
         }

         this.linkedHashMap.entrySet().removeIf(entry -> !var8.contains(entry.getKey()) && entry.getValue().getValue() <= 0.01F);
         this.statusEffects.keySet().removeIf(type -> !this.linkedHashMap.containsKey(type));
         this.statusEffects2.keySet().removeIf(type -> !this.linkedHashMap.containsKey(type));
         this.draggable.setWidth(var48);
         this.draggable.setHeight(var50);
      }
   }

   public void WaveStyle(EventRender.Default eventRender) {
      float var2 = this.draggable.getX();
      float var3 = this.draggable.getY();
      int var4 = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
      int var5 = ColorUtils.getThemeColor(var4);
      int var6 = ColorUtils.getThemeColor(var4 + 30);
      int var7 = ColorUtils.getThemeColor(var4 + 90);
      int var8 = ColorUtils.getThemeColor(var4 + 120);
      int var9 = ColorUtils.getThemeColor(var4 + 180);
      int var10 = ColorUtils.getThemeColor(var4 + 210);
      Collection<StatusEffectInstance> var11 = mc != null && mc.player != null ? mc.player.getStatusEffects() : List.of();
      HashSet<StatusEffect> var12 = new HashSet<>();

      for (StatusEffectInstance var14 : var11) {
         StatusEffect var15 = var14.getEffectType().value();
         var12.add(var15);
         this.helper3(var15).update(1.0F);
      }

      for (Entry var36 : this.linkedHashMap.entrySet()) {
         if (!var12.contains(var36.getKey())) {
            ((AnimationUtils)var36.getValue()).update(0.0F);
         }
      }

      float var34 = 84.0F;
      float var37 = 18.0F;
      int var38 = 0;

      for (StatusEffectInstance var17 : var11) {
         AnimationUtils var18 = this.helper3(var17.getEffectType().value());
         float var19 = var18.getValue();
         if (!(var19 <= 0.01F)) {
            var38++;
            String var20 = I18n.translate(var17.getTranslationKey());
            String var21 = helper6(var17.getAmplifier() + 1);
            String var22 = var20 + (var21.isEmpty() ? "" : " > " + var21);
            var34 = Math.max(var34, this.helper(16).getWidth(var22) + 38.0F);
            var34 = Math.max(var34, this.helper(15).getWidth(resolveString(var17)) + 38.0F);
            var37 += 18.0F * var19;
         }
      }

      float var39 = 18.0F;
      RenderUtils.drawWaveHudHeader(eventRender.getContext().getMatrices(), var2, var3, var34, 15.0F, 0.0F, 10.0F, 10.0F, var5, var6, var7, var8, var9, var10);
      String var40 = "potions";
      float var41 = var2 + (var34 - this.helper(16).getWidth(var40)) / 2.0F;
      this.updateState3(eventRender, this.helper(16), var40, var41, var3 + 5.0F, -1);
      if (var38 == 0) {
         this.draggable.setWidth(var34);
         this.draggable.setHeight(var39);
      } else {
         RenderUtils.drawWaveHudPanel(
            eventRender.getContext().getMatrices(),
            var2,
            var3,
            var34,
            var37,
            ColorUtils.rgba(25, 25, 25, 150),
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10
         );
         float var42 = 20.0F;

         for (StatusEffectInstance var44 : var11) {
            AnimationUtils var45 = this.helper3(var44.getEffectType().value());
            float var23 = var45.getValue();
            if (!(var23 <= 0.01F)) {
               ScissorUtils.push();
               ScissorUtils.setFromComponentCoordinates(var2, var3, var34 * this.draggable.getScaleX(), var37 * this.draggable.getScaleY());
               int var24 = (int)(255.0F * var23);
               int var25 = ColorUtils.rgba(255, 255, 255, var24);
               int var26 = ColorUtils.rgba(20, 185, 45, var24);
               float var27 = var2 + 5.0F;
               float var28 = var3 + var42;
               this.updateState2(eventRender, var44.getEffectType(), var27, var28, 11, var24);
               String var29 = I18n.translate(var44.getTranslationKey()).toLowerCase();
               String var30 = helper6(var44.getAmplifier() + 1);
               float var31 = var27 + 14.0F;
               this.helper(15).draw(eventRender.getContext().getMatrices(), var29 + " >", var31, var3 + var42 - 1.0F, var25);
               if (!var30.isEmpty()) {
                  float var32 = this.helper(14).getWidth(var29 + " >");
                  this.helper(14).draw(eventRender.getContext().getMatrices(), " " + var30, var31 + var32 + 2.0F, var3 + var42 - 0.5, var26);
               }

               this.helper(14).draw(eventRender.getContext().getMatrices(), resolveString(var44), var31, var3 + var42 + 7.5, var25);
               var42 += 18.0F * var23;
               ScissorUtils.pop();
               ScissorUtils.unset();
            }
         }

         this.draggable.setWidth(var34);
         this.draggable.setHeight(var37);
      }
   }

   private static final class PotionSnapshot {
      RegistryEntry<StatusEffect> entry;
      String baseName;
      int amplifier;
      int duration;
      boolean infinite;
   }
}
