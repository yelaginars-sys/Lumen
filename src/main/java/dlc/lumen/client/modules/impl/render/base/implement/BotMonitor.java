package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.bots.BotAutoJoin;
import dlc.lumen.client.bots.BotRegistry;
import dlc.lumen.client.bots.BotStatus;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

public class BotMonitor extends InterfaceProcessing {
   private static final float VOLUME = 22.0F;
   private static final float VOLUME2 = 3.0F;
   private final List<BotStatus> botStatuss = new ArrayList<>();
   private final Map<String, AnimationUtils> strings = new HashMap<>();
   private final Map<String, AnimationUtils> strings2 = new HashMap<>();
   private float volume;
   private float volume2;
   private float volume3;

   public BotMonitor(Draggable draggable) {
      super(draggable);
   }

   private Font helper(int size) {
      return Fonts.getFont("inter_medium", size);
   }

   private Font helper2(int size) {
      return Fonts.getFont("icon1", size);
   }

   private String resolveString(BotStatus bot) {
      String var2 = bot.name == null ? "" : bot.name.toLowerCase(Locale.ROOT);
      return (bot.self ? "self:" : "bot:") + var2;
   }

   private AnimationUtils helper3(String key) {
      return this.strings.computeIfAbsent(key, unused -> new AnimationUtils(0.0F, 10.0F, Easings.QUAD_OUT));
   }

   private AnimationUtils helper4(String key) {
      return this.strings2.computeIfAbsent(key, unused -> new AnimationUtils(0.0F, 7.0F, Easings.QUAD_OUT));
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      this.helper5(eventRender);
      super.onRender(eventRender);
   }

   private void helper5(EventRender.Default eventRender) {
      List<BotStatus> var2 = BotRegistry.INSTANCE.monitorRows();
      this.botStatuss.clear();
      this.botStatuss.addAll(var2);
       if (var2.size() <= 1) {
          this.botStatuss.clear();
          if (mc != null && mc.currentScreen instanceof ChatScreen) {
             float var3 = this.draggable.getX();
             float var4 = this.draggable.getY();
             MatrixStack var5 = eventRender.getContext().getMatrices();
             Font var7 = this.helper(11);
             float var11 = var7.getStringWidth("Боты");
             float var23 = stripWidth(var11);
             float var24 = stripHeight(12.0F);
             drawStrip(var5, this.helper2(13), "a", var3, var4, var23, var24 + 2.0F, ColorUtils.getThemeColor(), 3.0F);
             var7.drawString(var5, "Боты", stripContentX(var3), var4 + 4.0F, ColorUtils.clientText());
             this.draggable.setWidth(var23);
             this.draggable.setHeight(var24);
          } else {
             this.draggable.setWidth(0.0F);
             this.draggable.setHeight(0.0F);
          }
       } else {
         float var3 = this.draggable.getX();
         float var4 = this.draggable.getY();
         MatrixStack var5 = eventRender.getContext().getMatrices();
         int var6;
         if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            var6 = Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0];
         } else {
            var6 = ColorUtils.getThemeColor();
         }

         Font var7 = this.helper(11);
         Font var8 = this.helper(9);
         long var9 = System.currentTimeMillis();
         float var11 = 0.0F;

         for (BotStatus var13 : var2) {
            var11 = Math.max(var11, this.helper8(var13, var7, var8, var9));
         }

         float var23 = stripWidth(var11);
         float var24 = stripHeight(var2.size() * 12.0F);
         float var14 = stripContentX(var3);
         float var15 = var23 - 13.0F - 1.0F;
         drawStrip(var5, this.helper2(13), "a", var3, var4, var23, var24 + 2.0F, var6, 3.0F);
         boolean var16 = BotAutoJoin.INSTANCE.isActive();
         RenderUtils.drawRoundedRect(var5, var3 + 6.5F + 1.0F, var4 + var24 - 3.0F, 2.5F, 2.5F, 1.25F, var16 ? var6 : ColorUtils.rgba(90, 90, 98, 200));
         float var17 = var4 + 2.0F + 1.0F;
         this.volume3 = var17;
         boolean var18 = true;
         HashSet var19 = new HashSet();

         for (BotStatus var21 : var2) {
            if (!var18) {
               drawStripSeparator(var5, var14, var17, var15, 1.0F);
            }

            String var22 = this.resolveString(var21);
            var19.add(var22);
            this.helper6(var5, var21, var22, var14, var17, var15, var7, var8, var6, var9, var21.self);
            var18 = false;
            var17 += 12.0F;
         }

         this.strings.keySet().removeIf(key -> !var19.contains(key));
         this.strings2.keySet().removeIf(key -> !var19.contains(key));
         this.volume = var23;
         this.volume2 = var24;
         this.draggable.setWidth(var23);
         this.draggable.setHeight(var24);
      }
   }

   private void helper6(
      MatrixStack matrices,
      BotStatus bot,
      String inventoryKey,
      float contentX,
      float rowY,
      float rowsW,
      Font nameFont,
      Font smallFont,
      int accent,
      long now,
      boolean self
   ) {
      boolean var13 = !bot.pending && bot.isStale(now) && !self;
      int var14;
      if (bot.needsHelp) {
         var14 = ColorUtils.rgba(235, 90, 90, 255);
      } else if (var13 || bot.pending) {
         var14 = ColorUtils.rgba(120, 120, 128, 255);
      } else if (self) {
         var14 = accent;
      } else {
         var14 = ColorUtils.clientText();
      }

      float var15 = contentX + 5.0F;
      nameFont.draw(matrices, bot.name == null ? "?" : bot.name, var15, stripTextY(rowY, 11.0F), var14);
      float var16 = var15 + nameFont.getWidth(bot.name == null ? "?" : bot.name) + 6.0F;
      String var17 = this.helper7(bot, var13);
      smallFont.draw(
         matrices, var17, var16, stripTextY(rowY, 9.0F), !var13 && !bot.pending ? ColorUtils.rgba(157, 157, 166, 255) : ColorUtils.rgba(110, 110, 118, 255)
      );
      float var18 = contentX + rowsW - 5.0F;
      String var19 = bot.profit != 0L ? helper10(bot.profit) : "";
      if (!var19.isEmpty()) {
         smallFont.draw(matrices, var19, var18 - smallFont.getWidth(var19), stripTextY(rowY, 9.0F), ColorUtils.rgba(120, 200, 120, 255));
         var18 -= smallFont.getWidth(var19) + 6.0F;
      }

      if (bot.inGame && !var13) {
         float var20 = var18 - 22.0F;
         float var21 = rowY + 4.5F;
         RenderUtils.drawRoundedRect(matrices, var20, var21, 22.0F, 3.0F, 1.5F, ColorUtils.rgba(60, 60, 66, 200));
         float var22 = Math.max(0.0F, Math.min(1.0F, bot.inventory));
         float var23 = animateProgress(this.helper3(inventoryKey), var22);
         float var24 = animateTrailingProgress(this.helper4(inventoryKey), var23, 0.72F);
         if (var24 > 0.01F) {
            RenderUtils.drawRoundedRect(matrices, var20, var21, 22.0F * var24, 3.0F, 1.5F, ColorUtils.rgba(255, 255, 255, 42));
         }

         if (var23 > 0.01F) {
            int var25 = var22 > 0.9F ? ColorUtils.rgba(235, 140, 90, 255) : accent;
            RenderUtils.drawRoundedRect(matrices, var20, var21, 22.0F * var23, 3.0F, 1.5F, var25);
         }
      }
   }

   private String helper7(BotStatus bot, boolean dead) {
      if (dead) {
         return "нет ответа";
      } else if (!bot.needsHelp) {
         String var3 = bot.activity == null ? "" : bot.activity;
         return bot.anarchy > 0 ? var3 + " #" + bot.anarchy : var3;
      } else {
         return bot.helpReason != null && !bot.helpReason.isEmpty() ? bot.helpReason : "нужен ты";
      }
   }

   private float helper8(BotStatus bot, Font nameFont, Font smallFont, long now) {
      boolean var6 = !bot.pending && bot.isStale(now) && !bot.self;
      float var7 = 5.0F + nameFont.getWidth(bot.name == null ? "?" : bot.name) + 6.0F + smallFont.getWidth(this.helper7(bot, var6)) + 10.0F;
      String var8 = bot.profit != 0L ? helper10(bot.profit) : "";
      if (!var8.isEmpty()) {
         var7 += smallFont.getWidth(var8) + 6.0F;
      }

      if (bot.inGame) {
         var7 += 22.0F;
      }

      return var7 + 5.0F;
   }

   public boolean handleClick(double mouseX, double mouseY, int button) {
      if (button == 0 && !this.botStatuss.isEmpty() && !(this.draggable.getWidth() <= 1.0F)) {
         float var6 = this.draggable.getX();
         float var7 = this.draggable.getY();
         if (HoveringUtils.isHovered(mouseX, mouseY, var6, var7, 13.0, this.volume2)) {
            BotAutoJoin.INSTANCE.toggle();
            return true;
         }

         if (!HoveringUtils.isHovered(mouseX, mouseY, var6, var7, this.volume, this.volume2)) {
            return false;
         }

         int var8 = (int)((mouseY - this.volume3) / 12.0);
         return var8 >= 0 && var8 < this.botStatuss.size() ? this.helper9(this.botStatuss.get(var8)) : false;
      } else {
         return false;
      }
   }

   private boolean helper9(BotStatus row) {
      if (row == null || row.name == null || row.name.isBlank()) {
         return false;
      }

      if (row.self) {
         BotManager.INSTANCE.releaseControl();
         return true;
      }

      if (row.local) {
         BotManager.INSTANCE.control(row.name);
         return true;
      }

      if (row.pending) {
         BotAutoJoin.Entry var2 = BotAutoJoin.INSTANCE.find(row.name);
         if (var2 == null) {
            return false;
         }

         if (!var2.enabled) {
            BotAutoJoin.INSTANCE.toggleEntry(var2.name);
         } else {
            var2.nextAttempt = 0L;
            var2.failures = 0;
            var2.note = "";
         }

         return true;
      } else {
         return false;
      }
   }

   private static String helper10(long coins) {
      if (Math.abs(coins) >= 1000000L) {
         return String.format(Locale.ROOT, "%+.1fM", coins / 1000000.0);
      } else {
         return Math.abs(coins) >= 1000L ? String.format(Locale.ROOT, "%+.1fk", coins / 1000.0) : String.format(Locale.ROOT, "%+d", coins);
      }
   }
}
