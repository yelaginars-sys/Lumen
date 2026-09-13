package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.SidebarEntry;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.misc.NameProtect;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ScoreBoardHUD extends InterfaceProcessing {
   private static final float VOLUME = 7.0F;
   private static final float VOLUME2 = 6.0F;
   private static final float VOLUME3 = 5.0F;
   private static final float VOLUME4 = 9.0F;
   private static final float VOLUME5 = 72.0F;

   public ScoreBoardHUD(Draggable draggable) {
      super(draggable);
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
       ScoreBoardHUD.Layout var2 = helper2(mc, getSidebarObjective(mc));
       if (var2 == null) {
          if (mc != null && mc.currentScreen instanceof ChatScreen) {
             float var5 = this.draggable.getX();
             float var6 = this.draggable.getY();
             MatrixStack preview = eventRender.getContext().getMatrices();
             Font previewFont = Fonts.getFont("inter_medium", 12);
             float previewW = 90.0F;
             float previewH = 30.0F;
             drawHudBg(preview, var5, var6, previewW, previewH);
             if (previewFont != null) {
                previewFont.drawString(preview, "Scoreboard", var5 + 8.0F, var6 + 9.0F, ColorUtils.clientText());
             }

             this.draggable.setWidth(previewW);
             this.draggable.setHeight(previewH);
          } else {
             this.draggable.setWidth(0.0F);
             this.draggable.setHeight(0.0F);
          }
       } else {
         int var3 = !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
            ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
            : ColorUtils.getThemeColor();
         float var4 = helper();
         float var5 = this.draggable.getX();
         float var6 = this.draggable.getY();
         MatrixStack var7 = eventRender.getContext().getMatrices();
         var7.push();
         var7.translate(var5, var6, 0.0F);
         var7.scale(var4, var4, 1.0F);
         var7.translate(-var5, -var6, 0.0F);
         helper3(eventRender.getContext(), mc, var5, var6, var2, var3);
         var7.pop();
         this.draggable.setWidth(var2.width() * var4);
         this.draggable.setHeight(var2.height() * var4);
      }
   }

   private static float helper() {
      return ModuleClass.INSTANCE != null && ModuleClass.interfaceModule != null ? Math.max(0.1F, ModuleClass.interfaceModule.getScoreBoardScale()) : 1.0F;
   }

   public static ScoreboardObjective getSidebarObjective(MinecraftClient client) {
      if (client != null && client.player != null && client.world != null) {
         Scoreboard var1 = client.player.getScoreboard();
         Team var2 = var1.getScoreHolderTeam(client.player.getNameForScoreboard());
         if (var2 != null) {
            Formatting var3 = var2.getColor();
            if (var3 != null) {
               ScoreboardDisplaySlot var4 = ScoreboardDisplaySlot.fromFormatting(var3);
               if (var4 != null) {
                  ScoreboardObjective var5 = var1.getObjectiveForSlot(var4);
                  if (var5 != null) {
                     return var5;
                  }
               }
            }
         }

         return var1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      } else {
         return null;
      }
   }

   public static void renderVanillaAnchored(DrawContext drawContext, MinecraftClient client, ScoreboardObjective objective) {
      ScoreBoardHUD.Layout var3 = helper2(client, objective);
      if (var3 != null) {
         int var4 = !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
            ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
            : ColorUtils.getThemeColor();
         float var5 = helper();
         float var6 = drawContext.getScaledWindowWidth() - var3.width() * var5 - 6.0F;
         float var7 = Math.max(6.0F, drawContext.getScaledWindowHeight() * 0.35F - var3.height() * var5 * 0.5F);
         MatrixStack var8 = drawContext.getMatrices();
         var8.push();
         var8.translate(var6, var7, 0.0F);
         var8.scale(var5, var5, 1.0F);
         var8.translate(-var6, -var7, 0.0F);
         helper3(drawContext, client, var6, var7, var3, var4);
         var8.pop();
      }
   }

   private static ScoreBoardHUD.Layout helper2(MinecraftClient client, ScoreboardObjective objective) {
      if (client != null && client.textRenderer != null && objective != null) {
         Scoreboard var2 = objective.getScoreboard();
         NumberFormat var3 = objective.getNumberFormatOr(StyledNumberFormat.RED);
         int var4 = client.textRenderer.getWidth(": ");
         List<SidebarEntry> var5 = var2.getScoreboardEntries(objective)
            .stream()
            .filter(entry -> !entry.hidden())
            .sorted(Comparator.comparing(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER))
            .limit(15L)
            .map(entry -> {
               Team var4x = var2.getScoreHolderTeam(entry.owner());
               Text var5x = helper4(Team.decorateName(var4x, entry.name()));
               MutableText var6x = entry.formatted(var3);
               int var7x = client.textRenderer.getWidth(var6x);
               return new SidebarEntry(var5x, var6x, var7x);
            })
            .toList();
         Text var6 = helper4(objective.getDisplayName());
         int var7 = client.textRenderer.getWidth(var6);

         for (SidebarEntry var9 : var5) {
            int var10 = client.textRenderer.getWidth(var9.name);
            if (var9.scoreWidth > 0) {
               var10 += var4 + var9.scoreWidth;
            }

            var7 = Math.max(var7, var10);
         }

         float var11 = Math.max(72.0F, var7 + 14.0F);
         float var12 = 12.0F + 9.0F;
         if (!var5.isEmpty()) {
            var12 += 5.0F + var5.size() * 9.0F;
         }

         return new ScoreBoardHUD.Layout(var6, var5, var11, var12);
      } else {
         return null;
      }
   }

   private static void helper3(DrawContext drawContext, MinecraftClient client, float x, float y, ScoreBoardHUD.Layout layout, int themeColor) {
      MatrixStack var6 = drawContext.getMatrices();
      InterfaceProcessing.drawHudBg(var6, x, y, layout.width(), layout.height());
      int var7 = client.textRenderer.getWidth(layout.title());
      float var8 = x + (layout.width() - var7) * 0.5F;
      float var9 = y + 6.0F;
      drawContext.drawText(client.textRenderer, layout.title(), (int)var8, (int)var9, -1, false);
      if (!layout.lines().isEmpty()) {
         float var10 = var9 + 9.0F + 2.0F;
         RenderUtils.drawRoundedRect(var6, x + 7.0F, var10, layout.width() - 14.0F, 1.0F, 0.5F, ColorUtils.rgba(255, 255, 255, 35));
         int var11 = ColorUtils.replAlpha(ColorUtils.clientText(), 190);

         for (int var12 = 0; var12 < layout.lines().size(); var12++) {
            SidebarEntry var13 = layout.lines().get(var12);
            float var14 = y + 6.0F + 9.0F + 5.0F + var12 * 9.0F;
            drawContext.drawText(client.textRenderer, var13.name, (int)(x + 7.0F), (int)var14, -1, false);
            if (var13.scoreWidth > 0) {
               drawContext.drawText(client.textRenderer, var13.score, (int)(x + layout.width() - 7.0F - var13.scoreWidth), (int)var14, var11, false);
            }
         }
      }
   }

   private static Text helper4(Text text) {
      if (text != null && ModuleClass.INSTANCE != null && ModuleClass.nameProtect != null) {
         NameProtect var1 = ModuleClass.nameProtect;
         return !var1.isEnable() ? text : var1.patchText(text);
      } else {
         return text;
      }
   }

   private record Layout(Text title, List<SidebarEntry> lines, float width, float height) {

      private Layout(Text title, List<SidebarEntry> lines, float width, float height) {
         this.title = title;
         this.lines = lines;
         this.width = width;
         this.height = height;
      }

      public Text title() {
         return this.title;
      }

      public List<SidebarEntry> lines() {
         return this.lines;
      }

      public float width() {
         return this.width;
      }

      public float height() {
         return this.height;
      }
   }
}
