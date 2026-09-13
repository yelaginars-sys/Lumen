package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.media.MusicArtwork;
import dlc.lumen.api.utils.media.MusicLyrics;
import dlc.lumen.api.utils.media.MusicSession;
import dlc.lumen.api.utils.notification.NotificationManager;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.server.ServerUtil;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class DynamicIslandHUD extends InterfaceProcessing {
   private static final float VOLUME = 48.0F;
   private static final float VOLUME2 = 15.0F;
   private static final float VOLUME3 = 164.0F;
   private static final float VOLUME4 = 76.0F;
   private static final float VOLUME5 = 10.0F;
   private static final float VOLUME6 = 8.0F;
   private static final float VOLUME7 = 26.0F;
   private static final float VOLUME8 = 16.0F;
   private static final float VOLUME9 = 16.0F;
   private static final float VOLUME10 = 4.0F;
   private static final float VOLUME11 = 10.0F;
   private static final float VOLUME12 = 10.0F;
   private static final float VOLUME13 = 8.0F;
   private static final float VOLUME14 = 4.0F;
   private static final float VOLUME15 = 2.0F;
   private static final float VOLUME16 = 4.0F;
   private static final float VOLUME17 = 14.0F;
   private static final float VOLUME18 = 1.5F;
   private static final float VOLUME19 = 12.0F;
   private static final float VOLUME20 = 22.0F;
   private static final float VOLUME21 = 46.0F;
   private static final float VOLUME22 = 56.0F;
   private static final float VOLUME23 = 8.5F;
   private static final long POLL_INTERVAL_MS = 1600L;
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "textures/dynamicisland/music/previous.png");
   private static final Identifier TEXTURE_ID2 = Identifier.of("lumen", "textures/dynamicisland/music/play.png");
   private static final Identifier TEXTURE_ID3 = Identifier.of("lumen", "textures/dynamicisland/music/pause.png");
   private static final Identifier TEXTURE_ID4 = Identifier.of("lumen", "textures/dynamicisland/music/next.png");
   private static final Identifier TEXTURE_ID5 = Identifier.of("lumen", "textures/dynamicisland/music/no_image.png");
   private static final Identifier LOGO_ID = Identifier.of("lumen", "textures/waterlogo/lumenik.png");
   private final AnimationUtils animationUtils = new AnimationUtils(48.0F, 6.4F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils2 = new AnimationUtils(15.0F, 6.4F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils3 = new AnimationUtils(0.0F, 10.0F, Easings.LINEAR);
   private final AnimationUtils animationUtils4 = new AnimationUtils(1.0F, 7.2F, Easings.CUBIC_OUT);
   private final AnimationUtils[] animationUtils5 = new AnimationUtils[DynamicIslandHUD.Action.values().length];
   private MusicSession.Snapshot snapshot2 = MusicSession.Snapshot.EMPTY;
   private long timestamp;
   private long timestamp2;
   private float volume;
   private float volume2;
   private long timestamp3;
   private String text2 = "";
   private boolean flag;

   public DynamicIslandHUD(Draggable draggable) {
      super(draggable);
      draggable.setLocked(true);

      for (int var2 = 0; var2 < this.animationUtils5.length; var2++) {
         this.animationUtils5[var2] = new AnimationUtils(0.0F, 8.5F, Easings.CUBIC_OUT);
      }
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      if (mc != null && mc.getWindow() != null) {
         Font var2 = this.helper26(13);
         Font var3 = this.helper26(11);
         Font var4 = this.helper26(9);
         Font var5 = this.helper27(10);
         if (var2 != null && var3 != null && var4 != null) {
            MusicSession.ensureStarted();
            DynamicIslandHUD.NotificationState var6 = this.helper19();
            MusicSession.Snapshot var7 = this.helper20();
            DynamicIslandHUD.MusicState var8 = this.helper21(var7);
            DynamicIslandHUD.StatusType var9 = this.helper(var6, var8);
            if (var9 != DynamicIslandHUD.StatusType.MUSIC || !(mc.currentScreen instanceof ChatScreen)) {
               this.flag = false;
            }

            float var10 = this.helper2(var9, var6, var8, var2, var4);
            float var11 = this.helper3(var9, var8);
            this.animationUtils.update(var10);
            this.animationUtils2.update(var11);
            this.animationUtils3.update(this.flag ? 1.0F : 0.0F);
            float var12 = this.animationUtils.getValue();
            float var13 = this.animationUtils2.getValue();
            float var14 = MathHelper.clamp(this.animationUtils3.getValue(), 0.0F, 1.0F);
            float var15 = (mc.getWindow().getScaledWidth() - var12) * 0.5F;
            float var16 = this.hudTopOffset() + 3.0F;
            this.draggable.setLocked(true);
            this.draggable.setX(var15);
            this.draggable.setY(var16);
            this.draggable.setWidth(var12);
            this.draggable.setHeight(var13);
            MatrixStack var17 = eventRender.getContext().getMatrices();
            this.helper52(var17, var15, var16, var12, var13, var14);
            switch (var9) {
               case NOTIFICATION:
                  this.helper6(var17, var15, var16, var12, var13, var6, var2, var5);
                  break;
               case PVP:
                  this.helper7(var17, var15, var16, var12, var13, var2, var4);
                  break;
               case AUTO_MINE:
                  this.helper8(var17, var15, var16, var12, var13, var2, var4);
                  break;
               case MUSIC:
                  this.helper9(var17, var15, var16, var12, var13, var7, var8, var2, var3, var14);
                  break;
               case DEFAULT:
                  this.helper4(var17, var15, var16, var12, var13, var2);
            }
         } else {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         }
      } else {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      }
   }

   public boolean handleClick(double mouseX, double mouseY, int button) {
      if (button == 0 && !(this.draggable.getWidth() <= 1.0F) && !(this.draggable.getHeight() <= 1.0F)) {
         MusicSession.Snapshot var6 = this.helper20();
         boolean var7 = var6.isPresent();
         float var8 = this.draggable.getX();
         float var9 = this.draggable.getY();
         float var10 = this.draggable.getWidth();
         float var11 = this.draggable.getHeight();
         float var12 = MathHelper.clamp(this.animationUtils3.getValue(), 0.0F, 1.0F);
         boolean var13 = HoveringUtils.isHovered(mouseX, mouseY, var8, var9, var10, var11);
         if (mc.currentScreen instanceof ChatScreen && var7) {
            DynamicIslandHUD.StatusType var14 = this.helper(this.helper19(), this.helper21(var6));
            if (var14 != DynamicIslandHUD.StatusType.MUSIC) {
               this.flag = false;
               return false;
            }

            if (!this.flag) {
               if (var13) {
                  this.flag = true;
                  return true;
               } else {
                  return false;
               }
            } else {
               if (!var13) {
                  this.flag = false;
                  return true;
               }

               float var15 = helper44(var12);
               float var16 = helper45(var8, var12);
               float var17 = helper46(var9, var15, var12);
               if (HoveringUtils.isHovered(mouseX, mouseY, var16, var17, var15, var15)) {
                  MusicSession.playPause();
                  return true;
               }

               if (helper55(var12)) {
                  for (int var18 = 0; var18 < DynamicIslandHUD.Action.values().length; var18++) {
                     if (HoveringUtils.isHovered(mouseX, mouseY, helper43(var8, var10, var18), helper42(var9, var11), 16.0, 16.0)) {
                        this.helper56(DynamicIslandHUD.Action.values()[var18]);
                        return true;
                     }
                  }
               }

               return true;
            }
         } else {
            this.flag = false;
            return false;
         }
      } else {
         return false;
      }
   }

   private DynamicIslandHUD.StatusType helper(DynamicIslandHUD.NotificationState notification, DynamicIslandHUD.MusicState music) {
      if (notification != null) {
         return DynamicIslandHUD.StatusType.NOTIFICATION;
      } else if (ServerUtil.isPvPZone() || ServerUtil.pvpTime() >= 0) {
         return DynamicIslandHUD.StatusType.PVP;
      } else if (AutoMine.INSTANCE != null && AutoMine.INSTANCE.isEnable()) {
         return DynamicIslandHUD.StatusType.AUTO_MINE;
      } else {
         return music.hasTrack() ? DynamicIslandHUD.StatusType.MUSIC : DynamicIslandHUD.StatusType.DEFAULT;
      }
   }

   private float helper2(
      DynamicIslandHUD.StatusType status, DynamicIslandHUD.NotificationState notification, DynamicIslandHUD.MusicState music, Font titleFont, Font pillFont
   ) {
      return switch (status) {
         case NOTIFICATION -> {
            float var11 = helper30(titleFont, notification.text(), 160.0F);
            yield helper29(16.0F + var11 + 8.0F, 70.0F, 200.0F);
         }
         case PVP -> {
            float var10 = helper28(pillFont, this.helper22());
            float var12 = titleFont.getWidth("PvP Mode");
            float var13 = 4.0F + var10 + 4.0F + var12 + 4.0F;
            yield helper29(var13, 60.0F, 120.0F);
         }
         case AUTO_MINE -> {
            float var9 = helper28(pillFont, this.helper23());
            float var7 = titleFont.getWidth(this.helper24());
            float var8 = 4.0F + var9 + 4.0F + var7 + 4.0F;
            yield helper29(var8, 60.0F, 140.0F);
         }
         case MUSIC -> this.flag ? 164.0F : helper41(titleFont, music.collapsedText());
         case DEFAULT -> {
            float var6 = helper30(titleFont, "LumenDLC", 96.0F);
            yield helper29(30.0F + var6 + 4.0F, 52.0F, 104.0F);
         }
      };
   }

   private float helper3(DynamicIslandHUD.StatusType status, DynamicIslandHUD.MusicState music) {
      return status == DynamicIslandHUD.StatusType.MUSIC && this.flag ? 76.0F + (music.lyric().isBlank() ? 0.0F : 10.0F) : 20.0F;
   }

   private void helper4(MatrixStack matrices, float x, float y, float width, float height, Font font) {
      float var7 = height - 2.0F;
      float var8 = x + 4.0F;
      float var9 = y + (height - var7) * 0.5F;
      helper5(matrices, var8, var9, var7);
      this.helper37(matrices, font, "LumenDLC", var8 + var7 + 4.0F, helper31(y, height, font), width - 30.0F, ColorUtils.clientText());
   }

   private static void helper5(MatrixStack matrices, float x, float y, float h) {
      int var4 = ColorUtils.getThemeColor();
      float var5 = h * 0.26F;
      float var6 = h * 0.16F;
      float var7 = h * 0.52F;
      float var8 = var5 * 0.5F;
      RenderUtils.drawImage(matrices, LOGO_ID, x + 1.0F, y, h, h, var4);
   }

   private void helper6(
      MatrixStack matrices, float x, float y, float width, float height, DynamicIslandHUD.NotificationState notification, Font titleFont, Font iconFont
   ) {
      float var9 = x + 4.0F;
      float var10 = y + (height - 8.0F) * 0.5F;
      RenderUtils.drawRoundCircle(matrices, var9 + 4.0F, var10 + 4.0F, 2.6F, notification.badgeColor());
      this.helper37(
         matrices, titleFont, notification.text(), x + 16.0F, helper31(y, height, titleFont), width - 22.0F, ColorUtils.clientText()
      );
   }

   private void helper7(MatrixStack matrices, float x, float y, float width, float height, Font titleFont, Font pillFont) {
      int var8 = ColorUtils.rgba(185, 28, 28, 255);
      String var9 = this.helper22();
      float var10 = x + 4.0F;
      float var11 = this.helper25(matrices, pillFont, var9, var10, y + (height - 8.0F) * 0.5F, var8);
      titleFont.drawString(matrices, "PvP Mode", var10 + var11 + 4.0F, helper31(y, height, titleFont), ColorUtils.clientText());
   }

   private void helper8(MatrixStack matrices, float x, float y, float width, float height, Font titleFont, Font pillFont) {
      int var8 = ColorUtils.getThemeColor();
      String var9 = this.helper23();
      float var10 = x + 4.0F;
      float var11 = this.helper25(matrices, pillFont, var9, var10, y + (height - 8.0F) * 0.5F, var8);
      titleFont.drawString(matrices, this.helper24(), var10 + var11 + 4.0F, helper31(y, height, titleFont), ColorUtils.clientText());
   }

   private void helper9(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      float height,
      MusicSession.Snapshot snapshot,
      DynamicIslandHUD.MusicState music,
      Font titleFont,
      Font bodyFont,
      float extend
   ) {
      float var11 = MathHelper.clamp(extend, 0.0F, 1.0F);
      float var12 = helper44(var11);
      float var13 = helper45(x, var11);
      float var14 = helper46(y, var12, var11);
      float var15 = 1.5F + var11 * 4.5F;
      float var16 = 1.0F - helper53(var11, 0.04F, 0.26F);
      float var17 = helper53(var11, 0.1F, 0.52F);
      float var18 = helper53(var11, 0.28F, 0.66F);
      float var19 = helper53(var11, 0.38F, 0.76F);
      this.helper18(matrices, snapshot, var13, var14, var12, var15);
      if (var16 > 0.01F) {
         this.helper10(matrices, x, y, width, music, titleFont, var13, var12, var11, var16);
      }

      if (var17 > 0.01F) {
         this.helper11(matrices, x, y, width, snapshot, music, titleFont, bodyFont, var13, var14, var12, var17, var18);
      }

      if (var19 > 0.01F) {
         this.helper12(matrices, x, y, width, height, music.playing(), var19);
      }
   }

   private void helper10(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      DynamicIslandHUD.MusicState music,
      Font titleFont,
      float coverX,
      float coverSize,
      float extend,
      float alpha
   ) {
      float var11 = x + width - helper40(extend) - MathHelper.lerp(extend, 4.0F, 5.0F);
      this.helper17(matrices, var11, y + 4.0F - extend * 0.35F, music.playing(), alpha, extend);
      float var12 = coverX + coverSize + MathHelper.lerp(extend, 4.0F, 6.0F);
      float var13 = Math.max(18.0F, var11 - var12 - MathHelper.lerp(extend, 2.0F, 4.0F));
      float var14 = MathHelper.lerp(extend, helper31(y, 15.0F, titleFont), y + 8.5F);
      titleFont.drawStringWithFade(matrices, music.collapsedText(), var12, var14, var13, helper54(ColorUtils.clientText(), alpha));
   }

   private void helper11(
      MatrixStack matrices,
      float x,
      float y,
      float width,
      MusicSession.Snapshot snapshot,
      DynamicIslandHUD.MusicState music,
      Font titleFont,
      Font bodyFont,
      float coverX,
      float coverY,
      float coverSize,
      float alpha,
      float footerAlpha
   ) {
      float var14 = coverX + coverSize + 8.0F;
      float var15 = Math.max(30.0F, x + width - var14 - 14.0F);
      String var16 = !music.subtitle().isBlank() ? music.subtitle() : music.collapsedMeta();
      float var17 = coverY + 1.5F;
      float var18 = coverY + 12.0F;
      float var19 = coverY + 22.0F;
      DynamicIslandHUD.TrackProgressVisual var20 = this.helper49(snapshot);
      titleFont.drawStringWithFade(matrices, music.title(), var14, var17, var15, helper54(ColorUtils.clientText(), alpha));
      bodyFont.drawStringWithFade(matrices, var16, var14, var18, var15, helper54(ColorUtils.clientTextSecondary(), alpha * 0.92F));
      if (!music.lyric().isBlank()) {
         bodyFont.drawStringWithFade(matrices, music.lyric(), var14, var19, var15, helper54(ColorUtils.clientTextSecondary(), alpha * 0.88F));
      }

      long var21 = this.helper47(snapshot);
      String var23 = snapshot.duration() > 0L
         ? helper33(var21) + " / " + helper33(snapshot.duration())
         : (music.playing() ? "Воспроизведение" : "На паузе");
      float var24 = Math.min(alpha, footerAlpha);
      float var25 = x + 10.0F;
      float var26 = width - 20.0F;
      float var27 = y + (music.lyric().isBlank() ? 46.0F : 56.0F);
      float var28 = var27 - 8.5F;
      float var29 = (1.0F - footerAlpha) * 10.0F;
      var27 += var29;
      var28 += var29;
      bodyFont.drawRight(matrices, var23, x + width - 9.0F, var28, helper54(ColorUtils.clientTextSecondary(), var24 * 0.88F));
      RenderUtils.drawRoundedRect(
         matrices, var25, var27, var26, 3.0F, 1.25F, helper54(ColorUtils.replAlpha(ColorUtils.clientTextSecondary(), 58), var24)
      );
      if (music.hasDuration()) {
         float var30 = var26 * var20.trail();
         if (var30 > 0.35F) {
            RenderUtils.drawRoundedRect(
               matrices, var25, var27, var30, 3.0F, 1.25F, helper54(ColorUtils.replAlpha(ColorUtils.getThemeColor(), 102), var24 * 0.44F)
            );
         }

         float var31 = var26 * var20.fill();
         if (var31 > 0.35F) {
            int var32 = helper54(ColorUtils.getThemeColor(), var24);
            int var33 = helper54(ColorUtils.getThemeColor(1), var24);
            RenderUtils.drawGradientRect(matrices, var25, var27, var31, 3.0F, 1.25F, var32, var33, true);
         }
      }
   }

   private void helper12(MatrixStack matrices, float x, float y, float width, float height, boolean playing, float visibility) {
      boolean var8 = visibility > 0.05F && mc.currentScreen instanceof ChatScreen;
      double var9 = var8 ? this.helper57() : -1.0;
      double var11 = var8 ? this.helper58() : -1.0;
      float var13 = playing ? 1.0F : 0.0F;
      this.animationUtils4.update(var13);

      for (int var14 = 0; var14 < DynamicIslandHUD.Action.values().length; var14++) {
         float var15 = helper43(x, width, var14);
         float var16 = helper42(y, height);
         boolean var17 = var8 && HoveringUtils.isHovered(var9, var11, var15, var16, 16.0, 16.0);
         this.animationUtils5[var14].update(var17 ? 1.0F : 0.0F);
         float var18 = MathHelper.clamp(this.animationUtils5[var14].getValue(), 0.0F, 1.0F);
         float var19 = 1.0F + var18 * 0.06F * visibility;
         float var20 = 5.0F + visibility * 2.0F;
         int var21 = ColorUtils.getThemeColor();
         int var22 = var14 == DynamicIslandHUD.Action.PLAY_PAUSE.ordinal()
            ? ColorUtils.interpolate(
               ColorUtils.replAlpha(ColorUtils.clientElementBg(), (int)MathHelper.lerp(var18, 118.0F, 156.0F)),
               ColorUtils.replAlpha(var21, (int)MathHelper.lerp(var18, 90.0F, 142.0F)),
               0.76
            )
            : ColorUtils.interpolate(
               ColorUtils.replAlpha(ColorUtils.clientElementBg(), (int)MathHelper.lerp(var18, 104.0F, 140.0F)),
               ColorUtils.replAlpha(var21, (int)MathHelper.lerp(var18, 42.0F, 96.0F)),
               0.36
            );
         int var23 = ColorUtils.replAlpha(ColorUtils.clientText(), (int)MathHelper.lerp(var18, 26.0F, 72.0F));
         int var24 = ColorUtils.interpolateColor(ColorUtils.clientTextSecondary(), ColorUtils.clientText(), MathHelper.clamp(0.35F + var18 * 0.65F, 0.0F, 1.0F));
         float var25 = var15 + 8.0F;
         float var26 = var16 + 8.0F;
         matrices.push();
         matrices.translate(var25, var26, 0.0F);
         matrices.scale(var19, var19, 1.0F);
         matrices.translate(-var25, -var26, 0.0F);
         RenderUtils.drawRoundedRect(matrices, var15, var16, 16.0F, 16.0F, var20, helper54(var22, visibility));
         RenderUtils.drawRoundedRectOutline(
            matrices,
            var15,
            var16,
            16.0F,
            16.0F,
            var20,
            0.45F,
            helper54(var23, visibility),
            helper54(var23, visibility),
            helper54(var23, visibility),
            helper54(var23, visibility)
         );
         this.helper13(matrices, DynamicIslandHUD.Action.values()[var14], var25, var26, helper54(var24, visibility));
         matrices.pop();
      }
   }

   private void helper13(MatrixStack matrices, DynamicIslandHUD.Action action, float cx, float cy, int color) {
      switch (action) {
         case PREVIOUS:
            this.helper16(matrices, TEXTURE_ID, cx, cy, 8.5F, color);
            break;
         case PLAY_PAUSE:
            this.helper14(matrices, cx, cy, 9.0F, color);
            break;
         case NEXT:
            this.helper16(matrices, TEXTURE_ID4, cx, cy, 8.5F, color);
      }
   }

   private void helper14(MatrixStack matrices, float cx, float cy, float size, int color) {
      float var6 = MathHelper.clamp(this.animationUtils4.getValue(), 0.0F, 1.0F);
      float var7 = Math.max(0.001F, 1.0F - var6);
      float var8 = Math.max(0.001F, var6);
      this.helper15(matrices, TEXTURE_ID2, cx, cy, size, helper54(color, 1.0F - var6), var7, 90.0F * var6);
      this.helper15(matrices, TEXTURE_ID3, cx, cy, size, helper54(color, var6), var8, -90.0F + 90.0F * var6);
   }

   private void helper15(MatrixStack matrices, Identifier texture, float cx, float cy, float size, int color, float scale, float rotation) {
      if (!(scale <= 0.02F)) {
         matrices.push();
         matrices.translate(cx, cy, 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
         matrices.scale(scale, scale, 1.0F);
         matrices.translate(-cx, -cy, 0.0F);
         this.helper16(matrices, texture, cx, cy, size, color);
         matrices.pop();
      }
   }

   private void helper16(MatrixStack matrices, Identifier texture, float cx, float cy, float size, int color) {
      RenderUtils.drawImage(matrices, texture, cx - size * 0.5F, cy - size * 0.5F, size, size, color);
   }

   private void helper17(MatrixStack matrices, float x, float y, boolean playing, float alpha, float extend) {
      int var7 = helper54(ColorUtils.getThemeColor(), alpha * 0.92F);
      double var8 = System.currentTimeMillis() / 190.0;
      float var10 = 1.0F + extend;
      float var11 = 1.15F + extend * 0.35F;

      for (int var12 = 0; var12 < 4; var12++) {
         float var13 = playing
            ? 2.2F + (float)Math.abs(Math.sin(var8 + var12 * 0.82)) * (2.45F + extend * 1.9F + var12 * 0.22F)
            : 2.5F + var12 * 0.28F + extend * 0.25F;
         RenderUtils.drawRoundedRect(matrices, x + var12 * (var10 + var11), y + 6.0F - var13, var10, var13, 0.65F + extend * 0.3F, var7);
      }
   }

   private void helper18(MatrixStack matrices, MusicSession.Snapshot snapshot, float x, float y, float size, float radius) {
      int var7 = ColorUtils.replAlpha(ColorUtils.clientElementBg(), 192);
      int var8 = ColorUtils.replAlpha(ColorUtils.clientText(), 38);
      RenderUtils.drawRoundedRect(matrices, x, y, size, size, radius, var7);
      Identifier var9 = snapshot.isPresent() ? MusicArtwork.update(snapshot) : null;
      if (var9 != null) {
         RenderUtils.drawTexture(
            matrices, var9, x, y, size, size, MusicArtwork.u1(), MusicArtwork.v1(), MusicArtwork.u2(), MusicArtwork.v2(), ColorUtils.rgba(255, 255, 255, 255)
         );
      } else {
         float var10 = size * 0.48F;
         RenderUtils.drawImage(
            matrices,
            TEXTURE_ID5,
            x + (size - var10) * 0.5F,
            y + (size - var10) * 0.5F,
            var10,
            var10,
            ColorUtils.replAlpha(ColorUtils.clientTextSecondary(), 228)
         );
      }

      RenderUtils.drawRoundedRectOutline(matrices, x, y, size, size, radius, 0.45F, var8, var8, var8, var8);
   }

   private DynamicIslandHUD.NotificationState helper19() {
      ArrayList var1 = new ArrayList<>(NotificationManager.getActive());
      if (var1.isEmpty()) {
         return null;
      }

      NotificationManager.Entry var2 = (NotificationManager.Entry)var1.get(var1.size() - 1);
      String var3;
      if (var2.isCustom()) {
         var3 = helper35(var2.customText);
      } else {
         var3 = helper35(var2.moduleName) + (var2.enabled ? " включен" : " выключен");
      }

      if (var3.isBlank()) {
         return null;
      }

      int var4 = var2.isCustom() ? ColorUtils.getThemeColor() : (var2.enabled ? ColorUtils.rgba(36, 218, 118, 255) : ColorUtils.rgba(220, 76, 76, 255));
      String var5 = var2.categoryIcon == null ? "" : var2.categoryIcon;
      return new DynamicIslandHUD.NotificationState(var3, var5, var4);
   }

   private MusicSession.Snapshot helper20() {
      MusicSession.Snapshot var1 = MusicSession.get();
      long var2 = System.currentTimeMillis();
      if (var1.isPresent()) {
         this.snapshot2 = var1;
         this.timestamp = var2;
         this.timestamp2 = System.nanoTime();
         return var1;
      }

      if (this.snapshot2.isPresent() && var2 - this.timestamp <= 1600L) {
         return this.snapshot2;
      }

      this.snapshot2 = MusicSession.Snapshot.EMPTY;
      return this.snapshot2;
   }

   private DynamicIslandHUD.MusicState helper21(MusicSession.Snapshot snapshot) {
      if (snapshot != null && snapshot.isPresent()) {
         String var2 = helper35(snapshot.title());
         String var3 = helper35(snapshot.artist());
         String var4 = helper34(var2, var3, "Неизвестный трек");
         String var5 = !var3.isBlank() && !var3.equalsIgnoreCase(var4) ? var3 : "";
         String var6 = snapshot.playing() ? "Воспроизведение" : "На паузе";
         MusicLyrics.Result var7 = MusicLyrics.resolve(snapshot);
         MusicLyrics.Track var8 = var7.track();
         String var9 = var8.isPresent() ? helper35(var8.textAt(var8.currentIndex(snapshot.position()))) : "";
         String var10 = !var9.isBlank() ? var9 : var4;
         String var11 = !var5.isBlank() ? var5 : var6;
         if (!var9.isBlank()) {
            var11 = var4;
         } else if (var7.status() == MusicLyrics.Status.LOADING) {
            var11 = "Ищу текст";
         } else if (var7.status() == MusicLyrics.Status.ERROR) {
            var11 = "Текст недоступен";
         }

         return new DynamicIslandHUD.MusicState(var10, var11, var4, var5, var9, snapshot.playing(), true, snapshot.progress(), snapshot.duration() > 0L);
      } else {
         return new DynamicIslandHUD.MusicState("", "", "", "", "", false, false, 0.0F, false);
      }
   }

   private String helper22() {
      int var1 = ServerUtil.pvpTime();
      return var1 >= 0 ? helper32(var1) : "ON";
   }

   private String helper23() {
      AutoMine var1 = AutoMine.INSTANCE;
      if (var1 == null || !var1.isEnable()) {
         return "--";
      } else {
         return var1.getHooksObtained() > 0 ? "+" + var1.getHooksObtained() : helper32((int)(var1.getSessionElapsedMs() / 1000L));
      }
   }

   private String helper24() {
      AutoMine var1 = AutoMine.INSTANCE;
      return var1 != null && var1.isEnable() ? helper36(var1.statusLine()) : "Автомайн";
   }

   private float helper25(MatrixStack matrices, Font font, String text, float x, float y, int color) {
      float var7 = helper28(font, text);
      int var8 = ColorUtils.applyAlpha(color, 0.22F);
      int var9 = ColorUtils.applyAlpha(color, 0.65F);
      RenderUtils.drawRoundedRect(matrices, x, y, var7, 8.0F, 3.5F, var8);
      RenderUtils.drawRoundedRectOutline(matrices, x, y, var7, 8.0F, 3.5F, 0.45F, var9, var9, var9, var9);
      font.drawCenteredString(matrices, text, x + var7 * 0.5F, helper31(y, 8.0F, font), ColorUtils.clientText());
      return var7;
   }

   private Font helper26(int size) {
      Font var2 = Fonts.getFont("inter_medium", size);
      if (var2 == null) {
         var2 = Fonts.getFont("sf_regular", size);
      }

      return var2;
   }

   private Font helper27(int size) {
      Font var2 = Fonts.getFont("icons", size);
      if (var2 == null) {
         var2 = Fonts.getFont("icon", size);
      }

      if (var2 == null) {
         var2 = Fonts.getFont("iconnew", size);
      }

      return var2;
   }

   private static float helper28(Font font, String text) {
      return Math.max(12.0F, font.getWidth(text) + 6.0F);
   }

   private static float helper29(float width, float min, float max) {
      return MathHelper.clamp(Math.max(48.0F, width), min, max);
   }

   private static float helper30(Font font, String text, float maxWidth) {
      return Math.min(font.getWidth(text), maxWidth);
   }

   private static float helper31(float y, float height, Font font) {
      return y + height * 0.5F + 1.5F - font.getSize() * 0.25F;
   }

   private static String helper32(int totalSeconds) {
      int var1 = Math.max(0, totalSeconds);
      int var2 = var1 / 60;
      int var3 = var1 % 60;
      return String.format(Locale.ROOT, "%d:%02d", var2, var3);
   }

   private static String helper33(long totalMs) {
      long var2 = Math.max(0L, totalMs / 1000L);
      return String.format(Locale.ROOT, "%d:%02d", var2 / 60L, var2 % 60L);
   }

   private static String helper34(String first, String second, String fallback) {
      if (first != null && !first.isBlank()) {
         return first;
      } else {
         return second != null && !second.isBlank() ? second : fallback;
      }
   }

   private static String helper35(String value) {
      return value != null && !value.isBlank() ? value.replace('\n', ' ').replace('\r', ' ').replaceAll("\\s+", " ").trim() : "";
   }

   private static String helper36(String value) {
      if (value != null && !value.isBlank()) {
         return value.length() == 1 ? value.toUpperCase(Locale.ROOT) : Character.toUpperCase(value.charAt(0)) + value.substring(1);
      } else {
         return "";
      }
   }

   private void helper37(MatrixStack matrices, Font font, String text, float x, float y, float maxWidth, int color) {
      font.drawString(matrices, helper38(font, text, maxWidth), x, y, color);
   }

   private static String helper38(Font font, String text, float maxWidth) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         if (font.getWidth(text) <= maxWidth) {
            return text;
         }

         String var3 = "...";
         float var4 = font.getWidth(var3);
         if (var4 > maxWidth) {
            return "";
         }

         StringBuilder var5 = new StringBuilder();
         float var6 = 0.0F;

         for (int var7 = 0; var7 < text.length(); var7++) {
            String var8 = String.valueOf(text.charAt(var7));
            float var9 = font.getWidth(var8);
            if (var6 + var9 + var4 > maxWidth) {
               break;
            }

            var5.append(text.charAt(var7));
            var6 += var9;
         }

         return var5.isEmpty() ? var3 : var5.append(var3).toString();
      } else {
         return "";
      }
   }

   private static float helper39() {
      return 80.0F;
   }

   private static float helper40(float extend) {
      float var1 = 1.0F + extend;
      float var2 = 1.15F + extend * 0.35F;
      return 4.0F * var1 + 3.0F * var2;
   }

   private static float helper41(Font font, String text) {
      float var2 = helper30(font, text, 96.0F);
      float var3 = 16.0F + var2 + 2.0F + helper40(0.0F) + 4.0F;
      return helper29(var3, 76.0F, 140.0F);
   }

   private static float helper42(float y, float height) {
      return y + height - 16.0F - 6.0F;
   }

   private static float helper43(float x, float width, int index) {
      return x + width * 0.5F - helper39() * 0.5F + index * 32.0F;
   }

   private static float helper44(float extend) {
      return MathHelper.lerp(extend, 8.0F, 26.0F);
   }

   private static float helper45(float x, float extend) {
      return x + MathHelper.lerp(extend, 4.0F, 10.0F);
   }

   private static float helper46(float y, float size, float extend) {
      float var3 = y + (15.0F - size) * 0.5F;
      float var4 = y + 10.0F;
      return MathHelper.lerp(extend, var3, var4);
   }

   private long helper47(MusicSession.Snapshot snapshot) {
      if (snapshot != null && snapshot.isPresent()) {
         long var2 = Math.max(0L, snapshot.position());
         long var4 = snapshot.duration();
         if (snapshot.playing() && var4 > 0L && this.timestamp2 > 0L) {
            long var6 = Math.max(0L, (System.nanoTime() - this.timestamp2) / 1000000L);
            var2 += var6;
         }

         if (var4 > 0L) {
            var2 = Math.min(var2, var4);
         }

         return var2;
      } else {
         return 0L;
      }
   }

   private float helper48(MusicSession.Snapshot snapshot) {
      long var2 = snapshot == null ? 0L : snapshot.duration();
      return var2 <= 0L ? 0.0F : MathHelper.clamp((float)this.helper47(snapshot) / (float)var2, 0.0F, 1.0F);
   }

   private DynamicIslandHUD.TrackProgressVisual helper49(MusicSession.Snapshot snapshot) {
      float var2 = this.helper48(snapshot);
      long var3 = System.nanoTime();
      if (snapshot != null && snapshot.isPresent() && snapshot.duration() > 0L) {
         String var5 = snapshot.title() + "\u0000" + snapshot.artist() + "\u0000" + snapshot.duration();
         if (var5.equals(this.text2) && !(var2 + 0.12F < this.volume)) {
            float var6 = this.timestamp3 > 0L ? MathHelper.clamp((float)(var3 - this.timestamp3) / 1.0E9F, 0.0F, 0.12F) : 0.0F;
            this.timestamp3 = var3;
            this.text2 = var5;
            if (!snapshot.playing()) {
               this.volume = var2;
               this.volume2 = var2;
               return new DynamicIslandHUD.TrackProgressVisual(var2, var2);
            } else {
               this.volume = helper51(this.volume, var2, var6, 22.0F);
               this.volume2 = helper51(
                  this.volume2, this.volume, var6, this.volume >= this.volume2 ? 10.0F : 22.0F
               );
               return new DynamicIslandHUD.TrackProgressVisual(this.volume, this.volume2);
            }
         } else {
            this.helper50(var2, var3, var5);
            return new DynamicIslandHUD.TrackProgressVisual(this.volume, this.volume2);
         }
      } else {
         this.helper50(0.0F, var3, "");
         return new DynamicIslandHUD.TrackProgressVisual(0.0F, 0.0F);
      }
   }

   private void helper50(float value, long now, String trackKey) {
      this.volume = value;
      this.volume2 = value;
      this.timestamp3 = now;
      this.text2 = trackKey;
   }

   private static float helper51(float current, float target, float deltaSeconds, float responsiveness) {
      if (deltaSeconds <= 0.0F) {
         return target;
      }

      float var4 = 1.0F - (float)Math.exp(-Math.max(0.0F, responsiveness) * deltaSeconds);
      return MathHelper.lerp(MathHelper.clamp(var4, 0.0F, 1.0F), current, target);
   }

   private void helper52(MatrixStack matrices, float x, float y, float width, float height, float extend) {
      drawIslandHudPanel(matrices, x, y, width, height, 1.0F, extend, false, false);
   }

   private static float helper53(float value, float start, float end) {
      if (end <= start) {
         return value >= end ? 1.0F : 0.0F;
      }

      float var3 = MathHelper.clamp((value - start) / (end - start), 0.0F, 1.0F);
      return (float)Easings.SINE_OUT.ease(var3);
   }

   private static int helper54(int color, float alpha) {
      int var2 = ColorUtils.alpha(color);
      if (var2 <= 0) {
         var2 = 255;
      }

      return ColorUtils.replAlpha(color, (int)(var2 * MathHelper.clamp(alpha, 0.0F, 1.0F)));
   }

   private static boolean helper55(float extend) {
      return extend >= 0.48F;
   }

   private void helper56(DynamicIslandHUD.Action action) {
      switch (action) {
         case PREVIOUS:
            MusicSession.previous();
            break;
         case PLAY_PAUSE:
            MusicSession.playPause();
            break;
         case NEXT:
            MusicSession.next();
      }
   }

   private double helper57() {
      return mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth();
   }

   private double helper58() {
      return mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight();
   }

   private enum Action {
      PREVIOUS,
      PLAY_PAUSE,
      NEXT;
   }

   private record MusicState(
      String collapsedText,
      String collapsedMeta,
      String title,
      String subtitle,
      String lyric,
      boolean playing,
      boolean hasTrack,
      float progress,
      boolean hasDuration
   ) {

      private MusicState(
         String collapsedText,
         String collapsedMeta,
         String title,
         String subtitle,
         String lyric,
         boolean playing,
         boolean hasTrack,
         float progress,
         boolean hasDuration
      ) {
         this.collapsedText = collapsedText;
         this.collapsedMeta = collapsedMeta;
         this.title = title;
         this.subtitle = subtitle;
         this.lyric = lyric;
         this.playing = playing;
         this.hasTrack = hasTrack;
         this.progress = progress;
         this.hasDuration = hasDuration;
      }

      public String collapsedText() {
         return this.collapsedText;
      }

      public String collapsedMeta() {
         return this.collapsedMeta;
      }

      public String title() {
         return this.title;
      }

      public String subtitle() {
         return this.subtitle;
      }

      public String lyric() {
         return this.lyric;
      }

      public boolean playing() {
         return this.playing;
      }

      public boolean hasTrack() {
         return this.hasTrack;
      }

      public float progress() {
         return this.progress;
      }

      public boolean hasDuration() {
         return this.hasDuration;
      }
   }

   private record NotificationState(String text, String iconGlyph, int badgeColor) {

      private NotificationState(String text, String iconGlyph, int badgeColor) {
         this.text = text;
         this.iconGlyph = iconGlyph;
         this.badgeColor = badgeColor;
      }

      public String text() {
         return this.text;
      }

      public String iconGlyph() {
         return this.iconGlyph;
      }

      public int badgeColor() {
         return this.badgeColor;
      }
   }

   private enum StatusType {
      NOTIFICATION,
      PVP,
      AUTO_MINE,
      MUSIC,
      DEFAULT;
   }

   private record TrackProgressVisual(float fill, float trail) {

      private TrackProgressVisual(float fill, float trail) {
         this.fill = fill;
         this.trail = trail;
      }

      public float fill() {
         return this.fill;
      }

      public float trail() {
         return this.trail;
      }
   }
}
