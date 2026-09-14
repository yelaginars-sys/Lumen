package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.combat.AimAssistant;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.misc.NameProtect;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import dlc.lumen.client.ui.modern.ModernTheme;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TargetHud extends InterfaceProcessing {
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 9.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils2 = new AnimationUtils(0.0F, 4.6F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils3 = new AnimationUtils(1.0F, 9.2F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils4 = new AnimationUtils(1.0F, 7.4F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils5 = new AnimationUtils(20.0F, 7.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils6 = new AnimationUtils(0.0F, 7.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils7 = new AnimationUtils(0.0F, 9.2F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils8 = new AnimationUtils(0.0F, 7.4F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils9 = new AnimationUtils(0.0F, 9.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils10 = new AnimationUtils(0.0F, 11.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils11 = new AnimationUtils(0.0F, 9.5F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils12 = new AnimationUtils(1.0F, 5.6F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils13 = new AnimationUtils(1.0F, 3.2F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils14 = new AnimationUtils(0.0F, 8.0F, Easings.QUAD_OUT);
   private final AnimationUtils[] animationUtils15 = new AnimationUtils[6];
   private String text2 = "";
   private float volume = -1.0F;
   private final AnimationUtils animationUtils16 = new AnimationUtils(0.0F, 15.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils17 = new AnimationUtils(0.0F, 12.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils18 = new AnimationUtils(0.0F, 12.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils19 = new AnimationUtils(1.0F, 15.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils20 = new AnimationUtils(1.0F, 15.0F, Easings.QUAD_OUT);
   private final AnimationUtils animationUtils21 = new AnimationUtils(0.0F, 10.0F, Easings.QUAD_OUT);
   private final List<TargetHud.HeadParticle> objectArrayList = new ObjectArrayList();
   private LivingEntity livingEntity;
   private float volume2 = 20.0F;
   private boolean headParticlesEnabled2 = true;
   private boolean healthBarStyleEnabled2 = false;
   private long timestamp = System.nanoTime();
   private LivingEntity livingEntity2;
   private int index2 = 0;
   private int index3 = ColorUtils.rgba(124, 91, 242, 255);
   private int index4 = ColorUtils.rgba(93, 67, 175, 255);
   private Framebuffer framebuffer;
   private int index5 = -1;
   private int index6 = -1;
   private final ItemStack[] itemStack = new ItemStack[6];
   private final ItemStack[] itemStack2 = new ItemStack[4];
   private boolean flag = false;
   private long timestamp2 = 0L;
   private float volume3 = 0.0F;
   private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
   private static final String TEXT2 = "?";
   private final AnimationUtils animationUtils22 = new AnimationUtils(0.0F, 12.0F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils23 = new AnimationUtils(1.0F, 8.0F, Easings.BACK_IN);
   private boolean flag2 = false;

   private AnimationUtils helper(int index) {
      if (this.animationUtils15[index] == null) {
         this.animationUtils15[index] = new AnimationUtils(0.0F, 10.0F, Easings.BACK_OUT);
      }

      return this.animationUtils15[index];
   }

   public TargetHud(Draggable draggable) {
      super(draggable);
   }

   private Font helper2(int size) {
      return Fonts.getFont("inter_medium", size);
   }

   private LivingEntity helper3(Aura aura) {
      LivingEntity var2 = aura != null ? aura.getTarget() : null;
      if (var2 != null) {
         return var2;
      } else if (!ModuleClass.interfaceModule.isTargetHudHoverEnabled()) {
         return null;
      } else {
         AimAssistant var3 = ModuleClass.aimAssistant;
         if (var3 != null && var3.isEnable() && var3.getCurrentTarget() != null) {
            return var3.getCurrentTarget();
         } else {
            return mc.targetedEntity instanceof LivingEntity var4 && var4 != mc.player && var4.isAlive() ? var4 : null;
         }
      }
   }

   public boolean isHeadParticlesEnabled() {
      return this.headParticlesEnabled2;
   }

   public void setHeadParticlesEnabled(boolean headParticlesEnabled) {
      this.headParticlesEnabled2 = headParticlesEnabled;
      if (!headParticlesEnabled) {
         this.objectArrayList.clear();
      }
   }

   public boolean isHealthBarStyleEnabled() {
      return this.healthBarStyleEnabled2;
   }

   public void setHealthBarStyleEnabled(boolean healthBarStyleEnabled) {
      this.healthBarStyleEnabled2 = healthBarStyleEnabled;
   }

   private void updateState(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float healthTrailProgress,
      float healthProgress,
      float absorptionTrailProgress,
      float absorptionProgress,
      int themeColor,
      int themeColor2,
      float alpha,
      float goldenAlpha
   ) {
      RenderUtils.drawTargetHudHealthBars(matrices, x, y, width, healthTrailProgress, healthProgress, themeColor, themeColor2, alpha);
      if (goldenAlpha > 0.01F && absorptionProgress > 0.005F) {
         RenderUtils.drawTargetHudGoldenBars(matrices, x, y, width, 5.5F, absorptionTrailProgress, absorptionProgress, alpha, goldenAlpha);
      }
   }

   private int resolveInt(int color1, int color2, float ratio) {
      ratio = MathHelper.clamp(ratio, 0.0F, 1.0F);
      int var4 = color1 >> 16 & 0xFF;
      int var5 = color1 >> 8 & 0xFF;
      int var6 = color1 & 0xFF;
      int var7 = color1 >> 24 & 0xFF;
      int var8 = color2 >> 16 & 0xFF;
      int var9 = color2 >> 8 & 0xFF;
      int var10 = color2 & 0xFF;
      int var11 = color2 >> 24 & 0xFF;
      int var12 = (int)(var4 + (var8 - var4) * ratio);
      int var13 = (int)(var5 + (var9 - var5) * ratio);
      int var14 = (int)(var6 + (var10 - var6) * ratio);
      int var15 = (int)(var7 + (var11 - var7) * ratio);
      return var15 << 24 | var12 << 16 | var13 << 8 | var14;
   }

   private void updateState2() {
      if (mc != null && mc.getWindow() != null) {
         int var1 = mc.getWindow().getFramebufferWidth();
         int var2 = mc.getWindow().getFramebufferHeight();
         if (this.framebuffer == null || this.index5 != var1 || this.index6 != var2) {
            if (this.framebuffer != null) {
               this.framebuffer.delete();
            }

            this.framebuffer = new SimpleFramebuffer(var1, var2, true);
            this.index5 = var1;
            this.index6 = var2;
         }
      }
   }

   private int resolveInt2(LivingEntity target) {
      int var2 = 0;

      for (ItemStack var4 : target.getArmorItems()) {
         if (var2 < this.itemStack2.length) {
            this.itemStack2[var2++] = var4;
         }
      }

      int var6 = 0;

      for (int var8 = this.itemStack2.length - 1; var8 >= 0; var8--) {
         ItemStack var5 = var8 < var2 && this.itemStack2[var8] != null ? this.itemStack2[var8] : ItemStack.EMPTY;
         this.itemStack[var6++] = var5;
         this.itemStack2[var8] = ItemStack.EMPTY;
      }

      this.itemStack[var6++] = target.getMainHandStack();
      ItemStack var9 = target.getOffHandStack();
      if (!var9.isEmpty() && var6 < this.itemStack.length) {
         this.itemStack[var6++] = var9;
      }

      return var6;
   }

   private boolean checkCondition(boolean unusualAnimation) {
      if (!unusualAnimation) {
         return false;
      }

      this.updateState2();
      if (this.framebuffer == null) {
         return false;
      }

      this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.framebuffer.clear();
      this.framebuffer.beginWrite(false);
      return true;
   }

   private void updateState3(MatrixStack matrices, float x, float y, float width, float height, float scaleX, float scaleY) {
      float var8 = x + width * 0.5F;
      float var9 = y + height * 0.5F;
      matrices.translate(var8, var9, 0.0F);
      matrices.scale(scaleX, scaleY, 1.0F);
      matrices.translate(-var8, -var9, 0.0F);
   }

   private static float helper4(Font font, String glyph, float gap) {
      return font == null ? 0.0F : font.getStringWidth(glyph) + gap;
   }

   private static float helper5(MatrixStack matrices, Font font, String glyph, float x, float y, int color, float gap) {
      if (font == null) {
         return x;
      }

      font.drawString(matrices, glyph, x, y, color);
      return x + font.getStringWidth(glyph) + gap;
   }

   private void updateState4(MatrixStack matrices, LivingEntity target, float headX, float headY, float headSize, float alpha, int themeColor) {
      if (target != null && !(alpha <= 0.02F)) {
         long var8 = System.nanoTime();
         float var10 = MathHelper.clamp((float)(var8 - this.timestamp) / 1.0E9F * 60.0F, 0.2F, 3.0F);
         this.timestamp = var8;
         if (this.livingEntity2 != target) {
            this.objectArrayList.clear();
            this.livingEntity2 = target;
            this.index2 = Math.max(0, target.hurtTime);
         }

         ThreadLocalRandom var11 = ThreadLocalRandom.current();
         float var12 = headX + headSize * 0.5F;
         float var13 = headY + headSize * 0.5F;
         int var14 = Math.max(0, target.hurtTime);
         boolean var15 = var14 > 0 && (var14 > this.index2 || var14 % 3 == 0);
         this.index2 = var14;
         if (var15) {
            int var16 = 1 + var11.nextInt(2);

            for (int var17 = 0; var17 < var16 && this.objectArrayList.size() < 14; var17++) {
               float var18 = (float)(var11.nextDouble() * Math.PI * 2.0);
               float var19 = var11.nextFloat() * headSize * 0.24F;
               float var20 = (float)(var11.nextDouble() * Math.PI * 2.0);
               float var21 = 0.58F + var11.nextFloat() * 0.9F;
               TargetHud.HeadParticle var22 = new TargetHud.HeadParticle();
               var22.x = var12 + MathHelper.cos(var18) * var19;
               var22.y = var13 + MathHelper.sin(var18) * var19;
               var22.vx = MathHelper.cos(var20) * var21 + (var22.x - var12) * 0.025F;
               var22.vy = MathHelper.sin(var20) * var21 + (var22.y - var13) * 0.025F;
               var22.size = 3.8F + var11.nextFloat() * 1.4F;
               var22.age = 0.0F;
               var22.maxAge = 74.0F + var11.nextFloat() * 42.0F;
               this.objectArrayList.add(var22);
            }
         }

         for (int var24 = this.objectArrayList.size() - 1; var24 >= 0; var24--) {
            TargetHud.HeadParticle var25 = this.objectArrayList.get(var24);
            var25.age += var10;
            if (var25.age >= var25.maxAge) {
               this.objectArrayList.remove(var24);
            } else {
               var25.x = var25.x + var25.vx * var10;
               var25.y = var25.y + var25.vy * var10;
               var25.vx = var25.vx * (float)Math.pow(0.975F, var10);
               var25.vy = var25.vy * (float)Math.pow(0.975F, var10);
               var25.vy += 0.0012F * var10;
               float var26 = 1.0F - var25.age / var25.maxAge;
               float var27 = var26 * var26 * (3.0F - 2.0F * var26);
               float var28 = alpha * var27;
               if (!(var28 <= 0.02F)) {
                  float var29 = var25.x - var25.size * 0.5F;
                  float var30 = var25.y - var25.size * 0.5F;
                  int var23 = ColorUtils.applyAlpha(themeColor, var28 * 0.58F);
                  RenderUtils.drawRoundedRect(matrices, var29, var30, var25.size, var25.size, var25.size * 0.45F, var23);
               }
            }
         }
      } else {
         this.objectArrayList.clear();
         this.livingEntity2 = target;
         this.index2 = 0;
      }
   }

    private void updateState5(EventRender.Default eventRender, MatrixStack matrices, ItemStack stack, float slotX, float slotY, float itemScale) {
       if (!stack.isEmpty()) {
          matrices.push();
          matrices.translate(slotX, slotY, 0.0F);
          matrices.scale(itemScale, itemScale, 1.0F);
          eventRender.getContext().drawItem(stack, 0, 0);
          matrices.pop();
          if (stack.hasGlint()) {
             float size = 16.0F * itemScale;
             RenderUtils.drawRoundedRectOutline(
                matrices, slotX - 1.0F, slotY - 1.0F, size + 2.0F, size + 2.0F, 2.0F, 0.6F,
                ColorUtils.rgba(170, 120, 255, 200),
                ColorUtils.rgba(170, 120, 255, 200),
                ColorUtils.rgba(170, 120, 255, 200),
                ColorUtils.rgba(170, 120, 255, 200)
             );
          }
       }
    }

   private void updateState6(MatrixStack matrices, float cx, float cy, float size, int color) {
      Font var6 = Fonts.getFont("iconz", 11);
      if (var6 == null) {
         var6 = Fonts.getFont("icons", 11);
      }

      if (var6 == null) {
         var6 = Fonts.getFont("icon", 11);
      }

      if (var6 != null) {
         String var7 = "M";
         float var8 = var6.getStringWidth(var7);
         var6.draw(matrices, var7, cx - var8 * 0.5F, cy - 1.5F, color);
      }
   }

   private void updateState7(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
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

   private void updateState8(boolean visible) {
      if (visible && !this.flag) {
         this.flag = true;
         this.timestamp2 = System.currentTimeMillis();
         this.volume3 = 0.0F;
         this.animationUtils16.setValue(1.0F);
         this.animationUtils17.setValue(0.0F);
         this.animationUtils18.setValue(0.0F);
         this.animationUtils19.setValue(0.8F);
         this.animationUtils20.setValue(0.8F);
         this.animationUtils21.setValue(0.0F);
         this.animationUtils16.update(0.0F);
         this.animationUtils17.update(0.0F);
         this.animationUtils18.update(0.0F);
         this.animationUtils19.update(1.0F);
         this.animationUtils20.update(1.0F);
         this.animationUtils21.update(0.0F);
      } else if (!visible && this.flag) {
         this.flag = false;
      }

      if (visible) {
         float var2 = (float)(System.currentTimeMillis() - this.timestamp2) / 1000.0F;
         this.volume3 += 0.016F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         float var5 = 0.0F;
         float var6 = 1.0F;
         float var7 = 1.0F;
         float var8 = 0.0F;
         if (var2 < 0.1F) {
            float var9 = var2 / 0.1F;
            var3 = 1.0F;
            var4 = (this.threadLocalRandom.nextFloat() - 0.5F) * 25.0F * (1.0F - var9 * 0.3F);
            var5 = (this.threadLocalRandom.nextFloat() - 0.5F) * 15.0F * (1.0F - var9 * 0.3F);
            var6 = 0.8F + 0.2F * var9;
            var7 = 0.8F + 0.2F * var9;
            var8 = 0.5F + this.threadLocalRandom.nextFloat() * 0.5F;
         } else if (var2 < 0.3F) {
            float var14 = (var2 - 0.1F) / 0.2F;
            var3 = 1.0F - var14 * 0.8F;
            if (this.threadLocalRandom.nextFloat() < 0.4F * (1.0F - var14)) {
               var4 = (this.threadLocalRandom.nextFloat() - 0.5F) * 20.0F * (1.0F - var14);
               var5 = (this.threadLocalRandom.nextFloat() - 0.5F) * 12.0F * (1.0F - var14);
            }

            var6 = 1.0F;
            var7 = 1.0F;
            if (this.threadLocalRandom.nextFloat() < 0.2F * (1.0F - var14)) {
               var8 = 0.3F + this.threadLocalRandom.nextFloat() * 0.4F;
            }
         } else if (var2 < 0.6F) {
            float var15 = (var2 - 0.3F) / 0.3F;
            float var10 = 0.3F * (1.0F - var15) + 0.15F * (float)Math.sin(this.volume3 * 6.0F) * (1.0F - var15);
            var3 = Math.max(0.0F, var10);
            if (this.threadLocalRandom.nextFloat() < 0.08F * (1.0F - var15)) {
               var4 = (this.threadLocalRandom.nextFloat() - 0.5F) * 15.0F;
               var3 = Math.min(1.0F, var3 + 0.6F);
               var8 = 0.2F + this.threadLocalRandom.nextFloat() * 0.3F;
            }

            var6 = 1.0F;
            var7 = 1.0F;
         } else {
            var3 = 0.0F;
            var4 = 0.0F;
            var5 = 0.0F;
            var6 = 1.0F;
            var7 = 1.0F;
            var8 = 0.0F;
         }

         this.animationUtils16.update(var3);
         this.animationUtils17.update(var4);
         this.animationUtils18.update(var5);
         this.animationUtils19.update(var6);
         this.animationUtils20.update(var7);
         this.animationUtils21.update(var8);
      } else {
         this.animationUtils16.update(0.0F);
         this.animationUtils17.update(0.0F);
         this.animationUtils18.update(0.0F);
         this.animationUtils19.update(1.0F);
         this.animationUtils20.update(1.0F);
         this.animationUtils21.update(0.0F);
      }
   }

   private void updateState9(MatrixStack matrices, float x, float y, float width, float height) {
      float var6 = this.animationUtils16.getValue();
      float var7 = this.animationUtils17.getValue();
      float var8 = this.animationUtils18.getValue();
      float var9 = this.animationUtils19.getValue();
      float var10 = this.animationUtils20.getValue();
      float var11 = this.animationUtils21.getValue();
      if (!(var6 < 0.01F) || !(Math.abs(var7) < 0.1F) || !(Math.abs(var8) < 0.1F) || !(Math.abs(var9 - 1.0F) < 0.01F) || !(Math.abs(var10 - 1.0F) < 0.01F)) {
         float var12 = x + width * 0.5F;
         float var13 = y + height * 0.5F;
         if (Math.abs(var7) > 0.1F || Math.abs(var8) > 0.1F) {
            matrices.translate(var7, var8, 0.0F);
         }

         if (Math.abs(var9 - 1.0F) > 0.005F || Math.abs(var10 - 1.0F) > 0.005F) {
            matrices.translate(var12, var13, 0.0F);
            matrices.scale(var9, var10, 1.0F);
            matrices.translate(-var12, -var13, 0.0F);
         }

         if (var11 > 0.05F && var6 > 0.1F) {
            float var14 = y + height * (0.2F + this.threadLocalRandom.nextFloat() * 0.6F);
            float var15 = 2.0F + this.threadLocalRandom.nextFloat() * 6.0F * var6;
            float var16 = (this.threadLocalRandom.nextFloat() - 0.5F) * 30.0F * var6 * var11;
            matrices.push();
            matrices.translate(var16, 0.0F, 0.0F);
            int var17 = ColorUtils.rgba(100, 100, 255, (int)(80.0F * var6 * var11));
            RenderUtils.drawRoundedRect(matrices, x, var14, width, var15, 1.0F, var17);
            matrices.pop();
            int var18 = ColorUtils.rgba(255, 255, 255, (int)(60.0F * var6 * var11));
            RenderUtils.drawRoundedRect(matrices, x, var14, width, 1.0F, 0.5F, var18);
         }

         if (var6 > 0.2F) {
            float var21 = var6 * 0.25F;
            int var23 = ColorUtils.rgba(255, 0, 0, (int)(80.0F * var6));
            int var25 = ColorUtils.rgba(0, 0, 255, (int)(80.0F * var6));
            matrices.push();
            matrices.translate(var12, var13, 0.0F);
            matrices.scale(1.0F + var21 * 0.03F, 1.0F, 1.0F);
            matrices.translate(-var12, -var13, 0.0F);
            RenderUtils.drawRoundedRect(matrices, x, y, width, height, 4.0F, var23);
            matrices.pop();
            matrices.push();
            matrices.translate(var12, var13, 0.0F);
            matrices.scale(1.0F - var21 * 0.03F, 1.0F, 1.0F);
            matrices.translate(-var12, -var13, 0.0F);
            RenderUtils.drawRoundedRect(matrices, x, y, width, height, 4.0F, var25);
            matrices.pop();
         }

         if (var6 > 0.3F) {
            int var22 = (int)(20.0F * var6);

            for (int var24 = 0; var24 < var22; var24++) {
               float var26 = x + this.threadLocalRandom.nextFloat() * width;
               float var27 = y + this.threadLocalRandom.nextFloat() * height;
               float var28 = 1.0F + this.threadLocalRandom.nextFloat() * 2.0F;
               int var19 = (int)(150.0F * var6 * this.threadLocalRandom.nextFloat());
               int var20 = ColorUtils.rgba(
                  150 + this.threadLocalRandom.nextInt(105), 150 + this.threadLocalRandom.nextInt(105), 200 + this.threadLocalRandom.nextInt(55), var19
               );
               RenderUtils.drawRoundedRect(matrices, var26, var27, var28, var28, 0.5F, var20);
            }
         }
      }
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      this.DefaultStyle(eventRender);
      super.onRender(eventRender);
   }

   public void DefaultStyle(EventRender.Default eventRender) {
      if (mc.player == null) {
         this.objectArrayList.clear();
         this.index2 = 0;
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      } else {
         Aura var2 = ModuleClass.aura;
         boolean var3 = mc.currentScreen instanceof ChatScreen;
         LivingEntity var4 = this.helper3(var2);
         boolean var5 = var3 || var4 != null;
         this.animationUtils.setSpeed(var5 ? 9.0F : 5.0F);
         this.animationUtils.update(var5 ? 1.0F : 0.0F);
         float var6 = MathHelper.clamp(this.animationUtils.getValue(), 0.0F, 1.0F);
         float var7 = var6;
         this.updateState8(var5);
         if (var5) {
            this.livingEntity = var3 ? mc.player : var4;
         }

         LivingEntity var8 = var5 ? (var3 ? mc.player : var4) : this.livingEntity;
         if (var8 != null && !(var7 <= 0.01F)) {
            float var9 = var8.getAbsorptionAmount();
            boolean var10 = var9 > 0.0F;
            this.animationUtils9.setSpeed(var10 ? 9.0F : 5.0F);
            this.animationUtils9.update(var10 ? 1.0F : 0.0F);
            float var11 = MathHelper.clamp(this.animationUtils9.getValue(), 0.0F, 1.0F);
            float var12 = this.draggable.getX();
            float var13 = this.draggable.getY();
            int var14;
            if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
               var14 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
            } else {
               var14 = ModernTheme.accent();
            }

            int var15;
            if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
               var15 = ColorUtils.darken(Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0], 0.4F);
            } else {
               var15 = ModernTheme.accent();
            }

            if (var5) {
               this.index3 = var14;
               this.index4 = var15;
            }

            int var16 = var5 ? var14 : this.index3;
            int var17 = var5 ? var15 : this.index4;
            float var18 = Math.max(1.0F, var8.getMaxHealth());
            float var19 = Math.max(1.0F, this.volume2);
            float var20 = var5 ? var8.getHealth() : 0.0F;
            float var21 = var5 ? var9 : 0.0F;
            this.animationUtils5.update(var20);
            this.animationUtils6.update(var21);
            float var22 = MathHelper.clamp(this.animationUtils5.getValue(), 0.0F, var18);
            float var23 = MathHelper.clamp(this.animationUtils6.getValue(), 0.0F, var19);
            float var24 = MathHelper.clamp(var20 / var18, 0.0F, 1.0F);
            float var25 = animateProgress(this.animationUtils3, var24);
            float var26 = animateTrailingProgress(this.animationUtils4, var25, 0.78F);
            boolean var27 = !var5;
            if (var27) {
               this.animationUtils4.setValue(var25);
               var26 = var25;
            }

            float var28 = MathHelper.clamp(var21 / var19, 0.0F, 1.0F);
            float var29 = animateProgress(this.animationUtils7, var28);
            float var30 = animateTrailingProgress(this.animationUtils8, var29, 0.78F);
            if (var27 || !var10) {
               this.animationUtils8.setValue(var29);
               var30 = var29;
            }

            Font var31 = this.helper2(13);
            Font var32 = this.helper2(11);
            if (var31 != null && var32 != null) {
               String var33 = NameProtect.INSTANCE.patch(var8.getName().getString());
               String var34 = var33.length() > 12 ? var33.substring(0, 12) + "..." : var33;
               String var35 = String.valueOf(Math.round(var22 + var23));
               boolean var36 = this.healthBarStyleEnabled2;
               float var37 = var36 ? 36.0F : 30.0F;
               float var38 = 6.0F;
               float var39 = 5.0F;
               float var40 = 5.0F;
               float var41 = 5.0F;
               float var42 = 18.0F;
               float var43 = 17.0F;
               float var44 = 2.0F;
               float var45 = 9.0F;
               float var46 = 2.5F;
               int var47 = this.resolveInt2(var8);
               float var48 = var47 > 0 ? var47 * 9.0F + (var47 - 1) * 2.5F : 0.0F;
               float var49 = var36
                  ? Math.max(Math.max(var48, 68.0F), var31.getStringWidth(var34) + var32.getStringWidth(var35) + 12.0F)
                  : Math.max(var31.getStringWidth(var34), var48);
               float var50 = 28.0F + var49 + (var36 ? 5.0F : 27.0F);
               if (this.animationUtils10.getValue() < 1.0F) {
                  this.animationUtils10.setValue(var50);
               }

               this.animationUtils10.update(var50);
               float var51 = this.animationUtils10.getValue();
               if (!var34.equals(this.text2)) {
                  this.text2 = var34;
                  this.animationUtils12.setValue(0.0F);
               }

               this.animationUtils12.update(1.0F);
               this.animationUtils11.update(var5 ? 1.0F : 0.0F);
               float var52 = MathHelper.clamp(this.animationUtils11.getValue(), 0.0F, 1.2F);
               this.animationUtils13.setSpeed(var25 > this.animationUtils13.getValue() ? 9.0F : 3.2F);
               this.animationUtils13.update(var25);
               float var53 = MathHelper.clamp(this.animationUtils13.getValue(), 0.0F, 1.0F);
               float var54 = var22 + var23;
               if (this.volume >= 0.0F && var54 < this.volume - 0.05F) {
                  this.animationUtils14.setValue(1.0F);
               }

               this.volume = var54;
               this.animationUtils14.update(0.0F);
               float var55 = MathHelper.clamp(this.animationUtils14.getValue(), 0.0F, 1.0F);
               MatrixStack var56 = eventRender.getContext().getMatrices();
               float var57 = var6;
               int var58 = (int)(255.0F * var57);
               int var59 = ColorUtils.setAlphaColor(var16, var58);
               var56.push();
               float var60 = 0.9F + 0.1F * var52;
               float var61 = var12 + var51 / 2.0F;
               float var62 = var13 + var37 / 2.0F;
               var56.translate(var61, var62, 0.0F);
               var56.scale(var60, var60, 1.0F);
               var56.translate(-var61, -var62, 0.0F);
               InterfaceProcessing.drawHudBg(var56, var12, var13, var51, var37);
               float var63 = var12 + 5.0F;
               float var64 = var13 + (var37 - 18.0F) / 2.0F;
               if (this.headParticlesEnabled2) {
                  this.updateState4(var56, var8, var63, var64, 18.0F, var57, var16);
               } else {
                  this.objectArrayList.clear();
               }

               if (var8 instanceof PlayerEntity var65) {
                  RenderUtils.drawPlayerHead(var56, var65.getUuid(), var63, var64, 18.0F, 4.5F, var57, 0.0F);
               } else {
                  this.helper2(18)
                     .drawCenteredString(var56, "?", var63 + 9.0F, var64 + 2.0F + 9.0F - 4.5F, ColorUtils.replAlpha(ModernTheme.TEXT(), var58));
               }

               float var76 = var12 + 5.0F + 18.0F + 5.0F;
               float var66 = var12 + var51 - 5.0F;
               float var67 = var37 / 2.0F;
               float var68 = var36 ? var13 + 5.5F : (var47 > 0 ? helper6(var13, var67, var31) : helper6(var13, var37, var31)) + 3.0F;
               this.helper7(var56, var31, var34, var76 + 1.0F, var68, ModernTheme.TEXT(), var57, this.animationUtils12.getValue());
               if (var36) {
                  float var69 = var66 - var32.getStringWidth(var35);
                  float var70 = var13 + 6.5F;
                  var32.draw(var56, var35, var69, var70, ColorUtils.replAlpha(ModernTheme.TEXT(), var58));
               }

               float var77 = var36 ? var13 + 16.0F : var13 + var67 + (var67 - 9.0F) / 2.0F - 2.0F;

               for (int var78 = 0; var78 < var47; var78++) {
                  AnimationUtils var71 = this.helper(var78);
                  ItemStack var72 = this.itemStack[var78];
                  var71.update(1.0F);
                  float var73 = MathHelper.clamp(var71.getValue(), 0.0F, 1.0F);
                  float var74 = var76 + var78 * 11.5F;
                  float var75 = var77 + (1.0F - var73) * 3.0F;
                  if (var72.isEmpty()) {
                     this.updateState6(
                        var56, var74 + 4.5F, var75 + 4.5F, 9.0F, ColorUtils.setAlphaColor(ModernTheme.TEXT_MUTED(), (int)(var58 * 0.55F * var73))
                     );
                  } else if (var73 > 0.02F) {
                     this.updateState5(eventRender, var56, var72, var74, var75, 0.5625F * var57 * var73);
                  }

                  this.itemStack[var78] = ItemStack.EMPTY;
               }

               for (int var79 = var47; var79 < this.itemStack.length; var79++) {
                  this.helper(var79).update(0.0F);
                  this.itemStack[var79] = ItemStack.EMPTY;
               }

               if (var36) {
                  float var80 = var76;
                  float var82 = var13 + var37 - 8.5F;
                  float var84 = Math.max(40.0F, var66 - var80);
                  this.updateState(var56, var80, var82, var84, var26, var25, var30, var29, var16, var17, var57, var11);
               } else {
                  float var81 = var12 + var51 - 5.0F - 17.0F;
                  float var83 = var13 + (var37 - 17.0F) / 2.0F;
                  RenderUtils.drawRingArc(var56, var81, var83, 17.0F, 2.0F, 0.0F, 360.0F, ColorUtils.setAlphaColor(ColorUtils.rgba(90, 90, 90, 255), var58));
                  if (var53 > var25 + 0.004F) {
                     RenderUtils.drawRingArc(
                        var56, var81, var83, 17.0F, 2.0F, -90.0F, -90.0F + 360.0F * var53, ColorUtils.setAlphaColor(var16, (int)(var58 * 0.35F))
                     );
                  }

                  if (var25 > 0.005F) {
                     RenderUtils.drawRingArc(var56, var81, var83, 17.0F, 2.0F, -90.0F, -90.0F + 360.0F * var25, var59);
                  }

                  if (var11 > 0.01F && var29 > 0.005F) {
                     int var85 = ColorUtils.rgba(236, 183, 39, (int)(var58 * var11));
                     RenderUtils.drawRingArc(var56, var81, var83, 17.0F, 2.0F, -90.0F, -90.0F + 360.0F * var29, var85);
                  }

                  float var86 = var81 + 8.5F;
                  float var87 = var83 + 8.5F;
                  float var88 = 1.0F + 0.22F * var55;
                  var56.push();
                  var56.translate(var86, var87, 0.0F);
                  var56.scale(var88, var88, 1.0F);
                  var56.translate(-var86, -var87, 0.0F);
                  var32.drawCenteredString(var56, var35, var86, helper6(var83, 17.0F, var32), ColorUtils.replAlpha(ModernTheme.TEXT(), var58));
                  var56.pop();
               }

               var56.pop();
               this.draggable.setWidth(var51);
               this.draggable.setHeight(var37);
            }
         } else {
            this.objectArrayList.clear();
            this.index2 = 0;
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
            this.animationUtils9.setValue(0.0F);
            this.animationUtils6.setValue(0.0F);
            this.animationUtils7.setValue(0.0F);
            this.animationUtils8.setValue(0.0F);
         }
      }
   }

   private static float helper6(float boxY, float boxH, Font font) {
      return boxY + (boxH - font.getSize() * 0.5F) / 2.0F + 1.5F;
   }

   private void helper7(MatrixStack matrices, Font font, String text, float x, float y, int color, float alpha, float progress) {
      int var9 = text.length();
      if (var9 != 0 && !(alpha <= 0.01F)) {
         float var10 = 0.075F;
         float var11 = MathHelper.clamp(progress, 0.0F, 1.0F) * (1.0F + (var9 - 1) * 0.075F);
         float var12 = x;

         for (int var13 = 0; var13 < var9; var13++) {
            String var14 = String.valueOf(text.charAt(var13));
            float var15 = MathHelper.clamp(var11 - var13 * 0.075F, 0.0F, 1.0F);
            if (var15 > 0.001F) {
               float var16 = 1.0F - (1.0F - var15) * (1.0F - var15);
               int var17 = (int)(255.0F * alpha * var16);
               font.drawString(matrices, var14, var12, y + (1.0F - var16) * 3.5F, ColorUtils.replAlpha(color, var17));
            }

            var12 += font.getStringWidth(var14);
         }
      }
   }

   public void WaveStyle(EventRender.Default eventRender) {
      if (mc.player == null) {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      } else {
         Aura var2 = ModuleClass.aura;
         boolean var3 = mc.currentScreen instanceof ChatScreen;
         LivingEntity var4 = this.helper3(var2);
         boolean var5 = var3 || var4 != null;
         this.animationUtils.update(var5 ? 1.0F : 0.0F);
         if (var5 && !this.flag2) {
            this.animationUtils22.setValue(0.0F);
            this.animationUtils23.setValue(1.0F);
            this.flag2 = true;
         } else if (!var5 && this.flag2) {
            this.animationUtils23.setValue(1.0F);
            this.flag2 = false;
         }

         if (var5) {
            this.animationUtils22.update(1.0F);
            this.livingEntity = var3 ? mc.player : var4;
         } else {
            this.animationUtils23.update(0.0F);
         }

         this.updateState8(var5);
         float var6;
         if (var5) {
            var6 = MathHelper.clamp(this.animationUtils22.getValue(), 0.0F, 1.0F);
         } else {
            var6 = MathHelper.clamp(this.animationUtils23.getValue(), 0.0F, 1.0F);
         }

         float var7 = var6;
         LivingEntity var8 = var5 ? (var3 ? mc.player : var4) : this.livingEntity;
         if (var8 != null && !(var7 <= 0.01F)) {
            float var9 = this.draggable.getX();
            float var10 = this.draggable.getY();
            float var11 = 3.0F;
            boolean var12 = this.healthBarStyleEnabled2;
            float var13 = var12 ? 118.0F : 110.0F;
            float var14 = var12 ? 52.0F : 46.0F;
            float var15 = var8.getHealth();
            float var16 = Math.max(1.0F, var8.getMaxHealth());
            float var17 = var8.getAbsorptionAmount();
            float var18 = Math.max(1.0F, this.volume2);
            boolean var19 = var17 > 0.0F;
            this.animationUtils9.setSpeed(var19 ? 9.0F : 5.0F);
            this.animationUtils9.update(var19 ? 1.0F : 0.0F);
            float var20 = MathHelper.clamp(this.animationUtils9.getValue(), 0.0F, 1.0F);
            this.animationUtils5.update(var15);
            this.animationUtils6.update(var17);
            float var21 = MathHelper.clamp(this.animationUtils5.getValue(), 0.0F, var16);
            float var22 = MathHelper.clamp(this.animationUtils6.getValue(), 0.0F, var18);
            float var23 = MathHelper.clamp(var15 / var16, 0.0F, 1.0F);
            float var24 = animateProgress(this.animationUtils3, var23);
            float var25 = animateTrailingProgress(this.animationUtils4, var24, 0.78F);
            float var26 = MathHelper.clamp(var17 / var18, 0.0F, 1.0F);
            float var27 = animateProgress(this.animationUtils7, var26);
            float var28 = animateTrailingProgress(this.animationUtils8, var27, 0.78F);
            if (!var19) {
               this.animationUtils8.setValue(var27);
               var28 = var27;
            }

            int var29 = !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
               ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
               : ModernTheme.accent();
            int var30 = !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
               ? ColorUtils.darken(Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0], 0.4F)
               : ModernTheme.accent();
            MatrixStack var31 = eventRender.getContext().getMatrices();
            var31.push();
            this.updateState3(var31, var9, var10, var13, var14, var6, var6);
            this.updateState9(var31, var9, var10, var13, var14);
            float var32 = var7;
            int var33 = (int)(255.0F * var32);
            float var34 = 36.0F;
            RenderUtils.drawTargetHudWaveFrame(var31, var9, var10, var13, var14, var11, var34, var32);
            var31.pop();
            int var35 = (int)(var9 + var11 + 3.0F);
            int var36 = (int)(var10 + var11 + 3.0F);
            int var37 = (int)(var9 + var11 + 3.0F + var34 - 2.0F);
            int var38 = (int)(var10 + var11 + 3.0F + var34 - 2.0F);
            int var39 = (int)(15.0F * var7);
            float var40 = var9 + var11 + 3.0F + (var34 - 2.0F) / 2.0F;
            float var41 = var10 + var11 + 3.0F + (var34 - 2.0F) / 3.0F;
            float var42 = var8.bodyYaw;
            float var43 = var8.getPitch();
            double var44 = Math.toRadians(var42 + 180.0F);
            float var46 = var40 + (float)(Math.sin(var44) * 50.0);
            float var47 = var41 - var43;
            if (var7 > 0.5F) {
               InventoryScreen.drawEntity(eventRender.getContext(), var35, var36, var37, var38, var39, 0.0F, var46, var47, var8);
            }

            var31.push();
            this.updateState3(var31, var9, var10, var13, var14, var6, var6);
            String var48 = NameProtect.INSTANCE.patch(var8.getName().getString());
            float var49 = var9 + var11 + var34 + 6.0F;
            float var50 = Math.max(8.0F, var9 + var13 - var11 - 4.0F - var49);

            try {
               this.helper2(14).drawStringWithFade(var31, var48, var49, var10 + var11 + 5.0F, var50, ColorUtils.replAlpha(ModernTheme.TEXT(), var33));
            } catch (Exception var66) {
               this.helper2(14).drawString(var31, var48, var49, var10 + var11 + 5.0F, ColorUtils.replAlpha(ModernTheme.TEXT(), var33));
            }

            try {
               this.helper2(14)
                  .draw(
                     var31,
                     "HP: " + String.format("%.1f", var21 + var22) + " | Dist: " + (int)var8.distanceTo(mc.player),
                     var49,
                     var12 ? var10 + var11 + 16.0F : var10 + var11 + 20.0F,
                     ColorUtils.replAlpha(ModernTheme.TEXT(), var33)
                  );
            } catch (Exception var65) {
            }

            if (var12) {
               float var51 = var49;
               float var52 = var10 + var11 + 27.0F;
               float var53 = Math.max(40.0F, var9 + var13 - var11 - 4.0F - var51);
               this.updateState(var31, var51, var52, var53, var25, var24, var28, var27, var29, var30, var32, var20);
            } else {
               float var67 = var49;
               float var69 = var10 + var11 + 15.0F;
               float var71 = 5.0F;
               float var54 = 0.5F;
               byte var55 = 10;
               float var56 = var16 / var55;
               float var57 = var15;
               int var58;
               if (var15 <= var16 * 0.25F) {
                  var58 = ColorUtils.rgba(255, 50, 50, var33);
               } else if (var15 <= var16 * 0.5F) {
                  var58 = ColorUtils.rgba(255, 220, 0, var33);
               } else {
                  var58 = ColorUtils.rgba(0, 255, 0, var33);
               }

               int var59 = ColorUtils.applyAlpha(var58, var32 * 0.5F);

               for (int var60 = 0; var60 < var55; var60++) {
                  float var61 = var67 + var60 * (var71 + var54);
                  float var62 = var69;
                  RenderUtils.drawTargetHudHeartBase(var31, var61, var62 - 3.0F, var32);
                  if (var57 > 0.0F) {
                     float var63 = MathHelper.clamp(var57 / var56, 0.0F, 1.0F);
                     float var64 = var71 * var63;
                     if (var64 > 0.0F) {
                        RenderUtils.drawTargetHudHeartFill(var31, var61, var62 - 3.0F, var64, var58, var59);
                     }

                     var57 -= var56;
                  }
               }
            }

            float var68 = var49 - 1.0F;
            float var70 = var12 ? var10 + var11 + 35.0F : var10 + var11 + 28.0F;
            float var72 = 10.0F;
            int var73 = ColorUtils.applyAlpha(ColorUtils.rgba(50, 50, 50, 255), var32);
            int var74 = ColorUtils.applyAlpha(ColorUtils.darken(var29, 0.15F), var32);
            int var75 = ColorUtils.applyAlpha(ColorUtils.darken(var29, 0.05F), var32);
            int var76 = this.resolveInt2(var8);
            if (var76 > 0) {
               float var77 = var68 - 0.85F;
               float var79 = var70 - 0.85F;
               float var81 = (var76 - 1) * var72 + 9.8F;
               float var83 = 9.8F;
            }

            float var78 = 0.5F;

            for (int var80 = 0; var80 < var76; var80++) {
               ItemStack var82 = this.itemStack[var80];
               if (!var82.isEmpty()) {
                  float var84 = var68 + var80 * var72;
                  this.updateState5(eventRender, var31, var82, var84, var70, var78);
               }

               this.itemStack[var80] = ItemStack.EMPTY;
            }

            var31.pop();
            this.draggable.setWidth(var13);
            this.draggable.setHeight(var14);
         } else {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         }
      }
   }

   private static final class HeadParticle {
      float x;
      float y;
      float vx;
      float vy;
      float size;
      float age;
      float maxAge;
   }
}
