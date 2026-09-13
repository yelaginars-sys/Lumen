package dlc.lumen.client.modules.impl.render.base.implement;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.input.KeyBoardUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.misc.ServerHelper;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class HelperBinds extends InterfaceProcessing {
   private final AnimationUtils animationUtils = new AnimationUtils(80.0F, 10.5F, Easings.QUAD_OUT);
   private final Map<Item, AnimationUtils> items = new HashMap<>();

   public HelperBinds(Draggable draggable) {
      super(draggable);
   }

   private Font helper2(int size) {
      return Fonts.getFont("inter_medium", size);
   }

   private AnimationUtils helper3(Item item) {
      return this.items.computeIfAbsent(item, unused -> new AnimationUtils(0.0F, 11.0F, Easings.QUAD_OUT));
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      List var2 = this.helper4();
      this.helper5(eventRender, var2);
      super.onRender(eventRender);
   }

   private List<ServerHelper.HelperBind> helper4() {
      ServerHelper var1 = ServerHelper.INSTANCE;
      ArrayList var2 = new ArrayList();
      if (var1 == null) {
         return var2;
      }

      List<ServerHelper.HelperBind> var3;
      if (var1.isSpookyMode()) {
         var3 = var1.getSpookyHelperBinds();
      } else if (var1.isHolyWorldMode()) {
         var3 = var1.getHolyWorldHelperBinds();
      } else {
         var3 = new ArrayList<>();
      }

      for (ServerHelper.HelperBind var5 : var3) {
         if (var5.bind().getKey() != -1) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private void updateState(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
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

   private void helper5(EventRender.Default eventRender, List<ServerHelper.HelperBind> binds) {
      MatrixStack var3 = eventRender.getContext().getMatrices();
      float var4 = this.draggable.getX();
      float var5 = this.draggable.getY();
      int var6 = this.helper10();
      Font var7 = Fonts.getFont("icon", 13);
      if (var7 == null) {
         var7 = Fonts.getFont("iconnew", 13);
      }

      Font var8 = this.helper2(13);
      if (var8 == null) {
         var8 = Fonts.getFont("sf_regular", 13);
      }

      if (var8 != null) {
         float var9 = 13.0F;
         float var10 = 3.0F;
         float var11 = var5 + (var9 - var8.getHeight()) / 2.0F;
         float var12 = var11 + 0.5F;
         boolean var13 = this.isUnusualRectType();
         String var14 = "f";
         float var15 = 2.0F;
         float var16 = 5.0F;
         if (binds.isEmpty()) {
            float var28 = var10 + 4.0F + helper8(var7, var14, var15) + var8.getStringWidth("Helper") + var10 + 4.0F;
            this.animationUtils.update(var28);
            float var30 = this.animationUtils.getValue();
            drawHudBg(var3, var4, var5, var30, var9);
            float var32 = var4 + var10 + 4.0F;
            var8.drawString(var3, "none", var32 + 7.0F, var11 + 5.0F, ColorUtils.clientText());
            this.draggable.setWidth(var30);
            this.draggable.setHeight(var9);
         } else {
            float var17 = var10 + 4.0F;

            for (int var18 = 0; var18 < binds.size(); var18++) {
               ServerHelper.HelperBind var19 = (ServerHelper.HelperBind)binds.get(var18);
               String var20 = KeyBoardUtils.getBindName(var19.bind().getKey());
               var17 += 10.0F + var15 + var8.getStringWidth(var20);
               if (var18 < binds.size() - 1) {
                  var17 += var16;
               }
            }

            var17 += var10 + 4.0F;
            float var29 = var17;
            this.animationUtils.update(var29);
            float var31 = this.animationUtils.getValue();
            drawHudBg(var3, var4, var5, var31, var9);
            float var33 = var4 + var10 + 4.0F;
            float var21 = 0.61F;
            float var22 = 9.8F;
            float var23 = var5 + (var9 - var22) * 0.5F;

            for (int var24 = 0; var24 < binds.size(); var24++) {
               ServerHelper.HelperBind var25 = (ServerHelper.HelperBind)binds.get(var24);
               String var26 = KeyBoardUtils.getBindName(var25.bind().getKey());
               this.helper6(eventRender.getContext(), new ItemStack(var25.item()), var33, var23, var21);
               this.helper7(var3, var25.item(), var33, var23, var22);
               var33 += var22 + var15;
               var8.drawString(var3, var26, var33, var11 + 5.0F, ColorUtils.clientText());
               var33 += var8.getStringWidth(var26);
               if (var24 < binds.size() - 1) {
                  var33 += var16;
               }
            }

            this.draggable.setWidth(var31);
            this.draggable.setHeight(var9);
         }
      }
   }

   private void updateState2(EventRender.Default eventRender, List<ServerHelper.HelperBind> binds) {
      float var3 = this.draggable.getX();
      float var4 = this.draggable.getY();
      MatrixStack var5 = eventRender.getContext().getMatrices();
      int var6 = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
      int var7 = ColorUtils.getThemeColor(var6);
      int var8 = ColorUtils.getThemeColor(var6 + 30);
      int var9 = ColorUtils.getThemeColor(var6 + 90);
      int var10 = ColorUtils.getThemeColor(var6 + 120);
      int var11 = ColorUtils.getThemeColor(var6 + 180);
      int var12 = ColorUtils.getThemeColor(var6 + 210);
      Font var13 = this.helper2(14);
      if (var13 == null) {
         var13 = Fonts.getFont("sf_regular", 14);
      }

      if (var13 != null) {
         float var14 = 22.0F;
         float var15 = 3.0F;
         float var16 = 6.0F;
         float var17 = 11.0F;
         float var18 = 0.69F;
         float var19 = 3.5F;
         float var20 = var4 + (var14 - var17) * 0.5F;
         float var21 = var15 + 4.0F;

         for (int var22 = 0; var22 < binds.size(); var22++) {
            ServerHelper.HelperBind var23 = (ServerHelper.HelperBind)binds.get(var22);
            String var24 = KeyBoardUtils.getBindName(var23.bind().getKey());
            var21 += var17 + var19 + var13.getStringWidth(var24);
            if (var22 < binds.size() - 1) {
               var21 += var16;
            }
         }

         var21 += var15 + 4.0F;
         float var30 = var21;
         this.animationUtils.update(var30);
         float var31 = this.animationUtils.getValue();
         if (binds.isEmpty()) {
            RenderUtils.drawWaveHudHeader(var5, var3, var4, var31, 15.0F, 0.0F, 10.0F, 10.0F, var7, var8, var9, var10, var11, var12);
            String var34 = "helper";
            float var35 = var3 + (var31 - this.helper2(15).getWidth(var34)) / 2.0F;
            this.helper2(15).drawStringWithShadow(var5, var34, var35, var4 + 5.0F, ColorUtils.clientText());
            this.draggable.setWidth(var31);
            this.draggable.setHeight(18.0F);
         } else {
            RenderUtils.drawWaveHudPanel(
               var5, var3, var4, var31, var14, ColorUtils.rgba(25, 25, 25, 150), 3.5F, 0.0F, 10.0F, 10.0F, var7, var8, var9, var10, var11, var12
            );
            float var32 = var3 + var15 + 4.0F;
            float var25 = var4 + 9.5F;

            for (int var26 = 0; var26 < binds.size(); var26++) {
               ServerHelper.HelperBind var27 = (ServerHelper.HelperBind)binds.get(var26);
               String var28 = KeyBoardUtils.getBindName(var27.bind().getKey());
               this.helper6(eventRender.getContext(), new ItemStack(var27.item()), var32, var20, var18);
               this.helper7(var5, var27.item(), var32, var20, var17);
               var32 += var17 + var19;
               var13.drawString(var5, var28, var32, var25, ColorUtils.replAlpha(ColorUtils.clientText(), 240));
               var32 += var13.getStringWidth(var28);
               if (var26 < binds.size() - 1) {
                  var32 += var16;
               }
            }

            this.draggable.setWidth(var31);
            this.draggable.setHeight(var14);
         }
      }
   }

   private void helper6(DrawContext context, ItemStack stack, float x, float y, float scale) {
      MatrixStack var6 = context.getMatrices();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      var6.push();
      var6.translate(x, y, 0.0F);
      var6.scale(scale, scale, 1.0F);
      context.drawItem(stack, 0, 0);
      var6.pop();
      RenderSystem.depthMask(false);
      RenderSystem.disableDepthTest();
   }

   private void helper7(MatrixStack matrices, Item item, float x, float y, float size) {
      if (mc != null && mc.player != null && item != null) {
         float var6 = mc.player.getItemCooldownManager().getCooldownProgress(item.getDefaultStack(), 0.0F);
         float var7 = animateProgress(this.helper3(item), MathHelper.clamp(var6, 0.0F, 1.0F));
         if (!(var7 <= 0.01F)) {
            float var8 = size * var7;
            RenderUtils.drawRect(matrices, x, y + (size - var8), size, var8, ColorUtils.rgba(255, 255, 255, 120));
         }
      }
   }

   private static float helper8(Font font, String glyph, float gap) {
      return font == null ? 0.0F : font.getStringWidth(glyph) + gap;
   }

   private static float helper9(MatrixStack matrices, Font font, String glyph, float x, float y, int color, float gap) {
      if (font == null) {
         return x;
      }

      font.drawString(matrices, glyph, x, y, color);
      return x + font.getStringWidth(glyph) + gap;
   }

   private int helper10() {
      return !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtils.getThemeColor();
   }
}
