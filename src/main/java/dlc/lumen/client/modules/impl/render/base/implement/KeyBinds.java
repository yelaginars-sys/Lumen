package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

public class KeyBinds extends InterfaceProcessing {
   private final Map<Module, AnimationUtils> linkedHashMap = new LinkedHashMap<>();
   private final AnimationUtils animationUtils = new AnimationUtils(60.0F, 10.5F, Easings.QUAD_OUT);
   private static final Map<Character, Character> CHARACTERS = new HashMap<>();
   private static final float VOLUME = 15.0F;
   private static final float VOLUME2 = 13.0F;
   private static final float VOLUME3 = 2.0F;

   public KeyBinds(Draggable draggable) {
      super(draggable);
   }

   private Font helper(int size) {
      return Fonts.getFont("suisse", size);
   }

   private Font helper2(int size) {
      return Fonts.getFont("icon1", size);
   }

   private Font helper3(int size) {
      return Fonts.getFont("icon", size);
   }

   private AnimationUtils helper4(Module module) {
      return this.linkedHashMap.computeIfAbsent(module, m -> new AnimationUtils(0.0F, 10.5F, Easings.QUAD_OUT));
   }

   private String resolveString(String text) {
      StringBuilder var2 = new StringBuilder();

      for (char var6 : text.toCharArray()) {
         var2.append(CHARACTERS.getOrDefault(var6, var6));
      }

      return var2.toString();
   }

   private int resolveInt() {
      int[] var1 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().getColor();
      if (var1 != null && var1.length != 0) {
         int var2 = var1[0];
         if ((var2 >> 24 & 0xFF) == 0) {
            var2 = var2 & 16777215 | 0xFF000000;
         }

         return var2;
      } else {
         return -1;
      }
   }

   private void updateState(EventRender.Default eventRender, Font font, String text, float x, float y, int color) {
      int var7 = ColorUtils.rgba(20, 20, 20, 145);
      font.draw(eventRender.getContext().getMatrices(), text, x + 0.8F, y + 0.8F, var7);
      font.draw(eventRender.getContext().getMatrices(), text, x, y, color);
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      this.DefaultStyle(eventRender);
      super.onRender(eventRender);
   }

   private void updateState2(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
      float var7 = 4.0F;
      float var8 = 1.5F;
      float var9 = var7 * 2.0F + var8;
      int var10 = themeColor >> 24 & 0xFF;
      if (var10 == 0) {
         var10 = 255;
      }

      int var11 = var10 << 24 | themeColor & 16777215;
      RenderUtils.drawRoundedRectOutline(matrices, x - var8 / 2.0F, y - var8 / 2.0F, var9, var9, var7, 0.0F, 0.0F, var7, var8, var11, var11, var11, var11);
      RenderUtils.drawRoundedRectOutline(
         matrices, x + width - var9 + var8 / 2.0F, y - var8 / 2.0F, var9, var9, 0.0F, var7, var7, 0.0F, var8, var11, var11, var11, var11
      );
      RenderUtils.drawRoundedRectOutline(
         matrices, x - var8 / 2.0F, y + height - var9 + var8 / 2.0F, var9, var9, 0.0F, var7, var7, 0.0F, var8, var11, var11, var11, var11
      );
      RenderUtils.drawRoundedRectOutline(
         matrices, x + width - var9 + var8 / 2.0F, y + height - var9 + var8 / 2.0F, var9, var9, var7, 0.0F, 0.0F, var7, var8, var11, var11, var11, var11
      );
   }

   private float helper5(Module module) {
      String var2 = this.resolveString(KeyBoardUtils.getBindName(module.getKey()));
      float var3 = this.helper(10).getWidth(module.getDisplayName());
      float var4 = Math.max(this.helper(10).getWidth(var2) + 4.0F, 9.0F);
      return 26.0F + var3 + var4;
   }

   private float helper6() {
      return 15.0F + this.helper(11).getWidth("Binds") + 8.0F + 13.0F;
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

      ArrayList<Module> var5 = new ArrayList<>();
      ObjectListIterator var6 = ModuleClass.INSTANCE.getObject().iterator();

      while (var6.hasNext()) {
         Module var7 = (Module)var6.next();
         if (var7.getKey() != -1) {
            AnimationUtils var8 = this.helper4(var7);
            var8.update(var7.isEnable() ? 1.0F : 0.0F);
            if (var8.getValue() > 0.01F) {
               var5.add(var7);
            }
         }
      }

      if (var5.isEmpty()) {
         boolean var28 = mc.currentScreen instanceof ChatScreen;
         if (!var28) {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         } else {
            float var30 = this.helper6();
            MatrixStack var33 = eventRender.getContext().getMatrices();
            drawHudBg(var33, var2, var3, var30, 15.0F);
            this.helper(11).draw(var33, "Binds", var2 + 5.0F + 10.0F, var3 + 7.0F - 1.0F + 0.5F, -1);
            this.helper2(13).draw(var33, "f", var2 + 5.0F, var3 + 7.5F - 1.0F, var4);
            this.draggable.setWidth(var30);
            this.draggable.setHeight(15.0F);
         }
      } else {
         float var27 = 0.0F;
         float var29 = 15.0F;

         for (Module var9 : var5) {
            float var10 = this.helper4(var9).getValue();
            if (var10 > 0.01F) {
               float var11 = this.helper5(var9);
               if (var11 > var27) {
                  var27 = var11;
               }

               var29 += 15.0F * var10;
            }
         }

         float var32 = this.helper6();
         this.animationUtils.update(Math.max(var32, var27));
         float var34 = this.animationUtils.getValue();
         float var35 = var29;
         MatrixStack var36 = eventRender.getContext().getMatrices();
         drawHudBg(var36, var2, var3, var32, 14.0F);
         this.helper(13).draw(var36, "Binds", var2 + 5.0F + 10.0F, var3 + 7.0F - 1.0F + 1.0F - 1.5F, -1);
         this.helper2(13).draw(var36, "f", var2 + 5.0F, var3 + 8.0F - 1.0F - 1.0F, var4);
         float var12 = 17.0F;

         for (Module var14 : var5) {
            float var15 = this.helper4(var14).getValue();
            if (!(var15 <= 0.01F)) {
               float var16 = var3 + var12;
               float var17 = this.helper5(var14);
               int var18 = (int)(255.0F * var15);
               int var19 = ColorUtils.rgba(255, 255, 255, var18);
               int var20 = ColorUtils.setAlphaColor(var4, var18);
               String var21 = this.resolveString(KeyBoardUtils.getBindName(var14.getKey()));
               float var22 = Math.max(this.helper(10).getWidth(var21) + 4.0F, 9.0F);
               float var23 = 15.0F + var22;
               float var24 = var17 - var23 - 1.0F;
               drawHudBg(var36, var2, var16, var23, 13.0F);
               float var25 = var16 + 2.0F + 2.0F;
               String var26 = var14.getCategory().getIcons();
               this.helper3(13).draw(var36, var26, var2 + 5.0F, var25 + 1.0F + 1.0F, var20);
               this.helper(10).drawCenteredString(var36, var21, var2 + 13.0F + var22 / 2.0F, var25 + 3.0F - 1.0F, var19);
               this.helper(10)
                  .drawCenteredString(var36, "|", var2 + 13.0F + var22 / 2.0F - 4.0F, var25 + 3.0F - 1.0F, ColorUtils.rgba(120, 120, 120, 255));
               drawHudBg(var36, var2 + var23 + 1.0F, var16, var24, 13.0F);
               this.helper(10).draw(var36, var14.getDisplayName(), var2 + var23 + 1.0F + 5.0F, var25 + 2.0F, var19);
               ScissorUtils.pop();
               ScissorUtils.unset();
               var12 += 15.0F * var15;
            }
         }

         this.linkedHashMap.entrySet().removeIf(entry -> {
            Module var1 = entry.getKey();
            return var1.getKey() == -1 && entry.getValue().getValue() <= 0.01F;
         });
         this.draggable.setWidth(var34);
         this.draggable.setHeight(var35);
      }
   }

   private int resolveInt2() {
      return !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtils.getThemeColor();
   }

   public void WaveStyle(EventRender.Default eventRender) {
      float var2 = this.draggable.getX();
      float var3 = this.draggable.getY();
      ArrayList<Module> var4 = new ArrayList<>();
      ObjectListIterator var5 = ModuleClass.INSTANCE.getObject().iterator();

      while (var5.hasNext()) {
         Module var6 = (Module)var5.next();
         if (var6.getKey() <= 0) {
            var6.getAnimka().update(0.0F);
         } else {
            var6.getAnimka().update(var6.isEnable() ? 1.0F : 0.0F);
            if (var6.getAnimka().getValue() > 0.01F) {
               var4.add(var6);
            }
         }
      }

      int var52 = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
      int var53 = ColorUtils.getThemeColor(var52);
      int var7 = ColorUtils.getThemeColor(var52 + 30);
      int var8 = ColorUtils.getThemeColor(var52 + 90);
      int var9 = ColorUtils.getThemeColor(var52 + 120);
      int var10 = ColorUtils.getThemeColor(var52 + 180);
      int var11 = ColorUtils.getThemeColor(var52 + 210);
      float var12 = 0.0F;
      float var13 = 0.0F;

      for (Module var15 : var4) {
         float var16 = var15.getAnimka().getValue();
         if (!(var16 <= 0.01F)) {
            String var17 = this.resolveString(KeyBoardUtils.getBindName(var15.getKey()));
            String var18 = var15.getDisplayName().toLowerCase();
            float var19 = this.helper(15).getWidth(var17) + 16.0F;
            float var20 = this.helper(15).getWidth(var18) + 16.0F;
            if (var19 > var12) {
               var12 = var19;
            }

            if (var20 > var13) {
               var13 = var20;
            }
         }
      }

      float var56 = 50.0F;
      float var57 = 60.0F;
      var12 = Math.max(var12, var56);
      var13 = Math.max(var13, var57);
      float var58 = 4.0F;
      float var59 = var12 + var13 + var58 + 10.0F;
      float var60 = 18.0F;
      float var61 = 20.0F;
      float var62 = var60;
      int var21 = 0;

      for (Module var23 : var4) {
         float var24 = var23.getAnimka().getValue();
         if (!(var24 <= 0.01F)) {
            var21++;
            var62 += var61 * var24;
         }
      }

      RenderUtils.drawWaveHudHeader(eventRender.getContext().getMatrices(), var2, var3, var59, 15.0F, 0.0F, 10.0F, 10.0F, var53, var7, var8, var9, var10, var11);
      String var63 = "binds";
      float var64 = var2 + (var59 - this.helper(16).getWidth(var63)) / 2.0F;
      this.updateState(eventRender, this.helper(16), var63, var64, var3 + 5.0F, -1);
      if (var21 == 0) {
         this.draggable.setWidth(var59);
         this.draggable.setHeight(var60);
      } else {
         RenderUtils.drawWaveHudPanel(
            eventRender.getContext().getMatrices(),
            var2,
            var3,
            var59,
            var62,
            ColorUtils.rgba(25, 25, 25, 150),
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            var53,
            var7,
            var8,
            var9,
            var10,
            var11
         );
         float var65 = var2 + var12 + var58 / 2.0F + 5.0F;
         RenderUtils.drawRect(
            eventRender.getContext().getMatrices(), var65, var3 + var60 + 2.0F, 1.0F, var62 - var60 - 4.0F, ColorUtils.rgba(255, 255, 255, 40)
         );
         String var25 = "bind";
         String var26 = "function";
         float var27 = var2 + 5.0F + (var12 - this.helper(12).getWidth(var25)) / 2.0F;
         float var28 = var65 + 5.0F + (var13 - this.helper(12).getWidth(var26)) / 2.0F;
         this.helper(12).draw(eventRender.getContext().getMatrices(), var25, var27, var3 + var60 + 2.0F, ColorUtils.rgba(180, 180, 180, 255));
         this.helper(12).draw(eventRender.getContext().getMatrices(), var26, var28, var3 + var60 + 2.0F, ColorUtils.rgba(180, 180, 180, 255));
         float var29 = var60 + 14.0F;

         for (Module var31 : var4) {
            float var32 = var31.getAnimka().getValue();
            if (!(var32 <= 0.01F)) {
               ScissorUtils.push();
               ScissorUtils.setFromComponentCoordinates(var2, var3, var59 * this.draggable.getScaleX(), var62 * this.draggable.getScaleY());
               int var33 = (int)(255.0F * var32);
               String var34 = this.resolveString(KeyBoardUtils.getBindName(var31.getKey()));
               String var35 = var31.getDisplayName().toLowerCase();
               float var36 = var2 + 3.0F;
               float var37 = var3 + var29 - 1.0F;
               float var38 = var12 + 4.0F;
               float var39 = var61 - 2.0F;
               int var40 = ColorUtils.rgba(30, 30, 30, (int)(180.0F * var32));
               int var41 = ColorUtils.setAlphaColor(this.resolveInt2(), (int)(100.0F * var32));
               RenderUtils.drawRoundedRect(eventRender.getContext().getMatrices(), var36, var37, var38, var39, 3.0F, var40);
               RenderUtils.drawRect(eventRender.getContext().getMatrices(), var36, var37, 2.0F, var39, var41);
               int var42 = ColorUtils.rgba(255, 255, 255, var33);
               float var43 = var36 + (var38 - this.helper(15).getWidth(var34)) / 2.0F;
               float var44 = var65 + 3.0F;
               float var45 = var3 + var29 - 1.0F;
               float var46 = var13 + 4.0F;
               float var47 = var61 - 2.0F;
               int var48 = ColorUtils.rgba(30, 30, 30, (int)(180.0F * var32));
               int var49 = ColorUtils.setAlphaColor(this.resolveInt2(), (int)(100.0F * var32));
               RenderUtils.drawRoundedRect(eventRender.getContext().getMatrices(), var44, var45, var46, var47, 3.0F, var48);
               RenderUtils.drawRect(eventRender.getContext().getMatrices(), var44, var45, 2.0F, var47, var49);
               int var50 = ColorUtils.rgba(255, 255, 255, var33);
               float var51 = var44 + (var46 - this.helper(15).getWidth(var35)) / 2.0F;
               var29 += var61 * var32;
               ScissorUtils.unset();
               ScissorUtils.pop();
            }
         }

         this.draggable.setWidth(var59);
         this.draggable.setHeight(var62);
      }
   }

   static {
      String var0 = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
      String var1 = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
      int var2 = Math.min(var0.length(), var1.length());

      for (int var3 = 0; var3 < var2; var3++) {
         CHARACTERS.put(var0.charAt(var3), var1.charAt(var3));
      }
   }
}
