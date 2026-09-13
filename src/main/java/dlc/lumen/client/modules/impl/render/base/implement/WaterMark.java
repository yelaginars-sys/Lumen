package dlc.lumen.client.modules.impl.render.base.implement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.draggable.Draggable;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.impl.render.Interface;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class WaterMark extends InterfaceProcessing {
   private boolean showFps2 = true;
   private boolean showMs2 = true;
   private boolean showServer2 = true;
   private boolean showTps2 = true;
   public static final List<String> SEGMENT_KEYS = List.of("fps", "ms", "server", "tps");
   private final List<String> segmentOrderConfig = new ArrayList<>(SEGMENT_KEYS);
   private final List<WaterMark.SegRect> waterMarks = new ArrayList<>();
   private float segRowY2;
   private float segRowH2;
   private String segmentDragging = null;
   private final Map<String, Float> strings = new HashMap<>();
   private float dragMouseX2;
   private float dragGrabOffset2;
   private long lastReorderTime2 = 0L;
   private Interface interfaceModule2;
   private static final String TEXT = "Q";
   private static final String TEXT2 = "q";
   private static final String TEXT3 = "P";
   private static final String TEXT4 = "x";
   private static final String TEXT5 = "g";
   private static final Identifier LOGO = Identifier.of("lumen", "textures/waterlogo/lumenik.png");

   public void setInterfaceModule(Interface interfaceModule) {
      this.interfaceModule2 = interfaceModule;
   }

   public static String getUsername() {
      return "Lumen DLC";
   }

   public static String getUID() {
      return "1";
   }

   public WaterMark(Draggable draggable) {
      super(draggable);
      draggable.setScaleX(1.1F);
      draggable.setScaleY(1.1F);
   }

   public boolean isSegmentShown(String key) {
      return switch (key) {
         case "fps" -> this.showFps2;
         case "ms" -> this.showMs2;
         case "server" -> this.showServer2;
         case "tps" -> this.showTps2;
         default -> false;
      };
   }

   public void setSegmentShown(String key, boolean shown) {
      if (shown) {
         switch (key) {
            case "fps":
               this.showFps2 = true;
               break;
            case "ms":
               this.showMs2 = true;
               break;
            case "server":
               this.showServer2 = true;
               break;
            case "tps":
               this.showTps2 = true;
         }
      }
   }

   public void moveSegment(String key, int dir) {
      int var3 = this.segmentOrderConfig.indexOf(key);
      if (var3 >= 0) {
         int var4 = var3 + dir;
         if (var4 >= 0 && var4 < this.segmentOrderConfig.size()) {
            Collections.swap(this.segmentOrderConfig, var3, var4);
         }
      }
   }

   public boolean isSegmentDragging() {
      return this.segmentDragging != null;
   }

   private float[] helper(double screenX, double screenY) {
      float var5 = Math.max(0.01F, this.draggable.getScaleX());
      float var6 = Math.max(0.01F, this.draggable.getScaleY());
      float var7 = (float)screenX;
      float var8 = (float)screenY;
      if (Math.abs(var5 - 1.0F) >= 0.002F) {
         var7 = this.draggable.getX() + (var7 - this.draggable.getX()) / var5;
      }

      if (Math.abs(var6 - 1.0F) >= 0.002F) {
         var8 = this.draggable.getY() + (var8 - this.draggable.getY()) / var6;
      }

      return new float[]{var7, var8};
   }

   public boolean beginSegmentDrag(double mouseX, double mouseY) {
      float[] var5 = this.helper(mouseX, mouseY);
      if (!(var5[1] < this.segRowY2) && !(var5[1] > this.segRowY2 + this.segRowH2)) {
         for (WaterMark.SegRect var7 : this.waterMarks) {
            if (var5[0] >= var7.x1 && var5[0] <= var7.x2) {
               this.segmentDragging = var7.key;
               this.dragGrabOffset2 = var5[0] - var7.x1;
               this.dragMouseX2 = var5[0];
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void updateSegmentDrag(double mouseX) {
      if (this.segmentDragging != null && !this.waterMarks.isEmpty()) {
         this.dragMouseX2 = this.helper(mouseX, 0.0)[0];
         int var3 = this.waterMarks.size() - 1;

         for (int var4 = 0; var4 < this.waterMarks.size(); var4++) {
            if (mouseX < this.waterMarks.get(var4).center()) {
               var3 = var4;
               break;
            }
         }

         ArrayList var10 = new ArrayList();

         for (String var6 : this.segmentOrderConfig) {
            if (this.isSegmentShown(var6)) {
               var10.add(var6);
            }
         }

         int var11 = var10.indexOf(this.segmentDragging);
         if (var11 >= 0 && var3 != var11) {
            var10.remove(var11);
            var10.add(Math.min(var3, var10.size()), this.segmentDragging);
            ArrayList var12 = new ArrayList();
            int var7 = 0;

            for (String var9 : this.segmentOrderConfig) {
               var12.add(this.isSegmentShown(var9) ? (String)var10.get(var7++) : var9);
            }

            if (!var12.equals(this.segmentOrderConfig)) {
               this.segmentOrderConfig.clear();
               this.segmentOrderConfig.addAll(var12);
               this.lastReorderTime2 = System.currentTimeMillis();
            }
         }
      }
   }

   public void endSegmentDrag() {
      if (this.segmentDragging != null) {
         this.lastReorderTime2 = System.currentTimeMillis();
      }

      this.segmentDragging = null;
   }

   public String getSegmentOrderConfig() {
      return String.join(",", this.segmentOrderConfig);
   }

   public void setSegmentOrderConfig(String csv) {
      if (csv != null && !csv.isEmpty()) {
         ArrayList var2 = new ArrayList();

         for (String var6 : csv.split(",")) {
            var6 = var6.trim();
            if (SEGMENT_KEYS.contains(var6) && !var2.contains(var6)) {
               var2.add(var6);
            }
         }

         for (String var8 : SEGMENT_KEYS) {
            if (!var2.contains(var8)) {
               var2.add(var8);
            }
         }

         this.segmentOrderConfig.clear();
         this.segmentOrderConfig.addAll(var2);
      }
   }

   @Override
   public void onRender(EventRender.Default eventRender) {
      if (mc != null && mc.getWindow() != null) {
         float var2 = Math.max(0.01F, this.draggable.getScaleX());
         float var3 = Math.max(0.01F, this.draggable.getScaleY());
         float var4 = Math.max(this.draggable.getWidth() * var2, 1.0F);
         float var5 = Math.max(this.draggable.getHeight() * var3, 1.0F);
         float var6 = mc.getWindow().getScaledWidth();
         float var7 = mc.getWindow().getScaledHeight();
         float var8 = (var6 - var4) * 0.5F;
         float var9 = this.interfaceModule2 != null ? this.interfaceModule2.getDynamicIslandBottomY() : this.hudTopOffset() + 3.0F;
         float var10 = var9 + 4.0F;
         var10 = Math.min(var10, var7 - var5 - 2.0F);
         this.draggable.setLocked(true);
         this.draggable.setX(var8);
         this.draggable.setY(var10);
      }

      this.DefaultStyle(eventRender);
      super.onRender(eventRender);
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

   private void updateState2(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
      float var7 = 8.0F;
      float var8 = 1.5F;
      int var9 = themeColor >> 24 & 0xFF;
      if (var9 == 0) {
         var9 = 255;
      }

      int var10 = var9 << 24 | themeColor & 16777215;
      RenderUtils.drawRect(matrices, x, y, x + var7, y + var8, var10);
      RenderUtils.drawRect(matrices, x, y, x + var8, y + var7, var10);
      RenderUtils.drawRect(matrices, x + width - var7, y, x + width, y + var8, var10);
      RenderUtils.drawRect(matrices, x + width - var8, y, x + width, y + var7, var10);
      RenderUtils.drawRect(matrices, x, y + height - var8, x + var7, y + height, var10);
      RenderUtils.drawRect(matrices, x, y + height - var7, x + var8, y + height, var10);
      RenderUtils.drawRect(matrices, x + width - var7, y + height - var8, x + width, y + height, var10);
      RenderUtils.drawRect(matrices, x + width - var8, y + height - var7, x + width, y + height, var10);
   }

   public void DefaultStyle(EventRender.Default eventRender) {
      MatrixStack var2 = eventRender.getContext().getMatrices();
      float var3 = this.draggable.getX();
      float var4 = this.draggable.getY();
      Font var5 = Fonts.getFont("icons", 13);
      if (var5 == null) {
         var5 = Fonts.getFont("icon", 13);
      }

      if (var5 == null) {
         var5 = Fonts.getFont("iconnew", 13);
      }

      Font var6 = Fonts.getFont("inter_medium", 13);
      if (var6 == null) {
         var6 = Fonts.getFont("sf_regular", 13);
      }

      if (var6 != null) {
         float var7 = 3.0F;
         float var8 = 14.0F;
         int var9 = ColorUtils.getThemeColor();
         int var10 = ColorUtils.clientIcon();
         boolean var11 = this.isUnusualRectType();
         int var12 = ColorUtils.clientText();
         float var13 = var4 - 0.5F + (var8 - var6.getHeight()) / 2.0F;
         float var14 = (var5 != null ? var13 + (var6.getHeight() - var5.getHeight()) / 2.0F : var13) + 0.5F;
         float var15 = var4 - 0.5F + var8 / 2.0F;
         float var16 = Math.max(7.0F, var6.getHeight() * 0.78F);
         String var18 = getUsername();
         int var19 = mc != null ? mc.getCurrentFps() : 0;
         String var20 = String.valueOf(var19);
         String var21 = "fps";
         int var22 = 0;
         if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
            PlayerListEntry var23 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (var23 != null) {
               var22 = var23.getLatency();
            }
         }

         String var52 = String.valueOf(var22);
         String var24 = "ms";
         String var25 = "Singleplayer";
         if (mc != null) {
            ServerInfo var26 = mc.getCurrentServerEntry();
            if (var26 != null && var26.address != null && !var26.address.isEmpty()) {
               var25 = var26.address;
            }
         }

         String var53 = this.helper6(var25);
         String var27 = this.helper8(this.helper7());
         String var28 = "tps";
         float var29 = 2.0F;
         float var30 = 3.0F;
         ArrayList<String[]> var31 = new ArrayList<>();

         for (String var33 : this.segmentOrderConfig) {
            if (this.isSegmentShown(var33)) {
               switch (var33) {
                  case "fps":
                     var31.add(new String[]{"q", var20, var21, "fps"});
                     break;
                  case "ms":
                     var31.add(new String[]{"P", var52, var24, "ms"});
                     break;
                  case "server":
                     var31.add(new String[]{"x", var53, "", "server"});
                     break;
                  case "tps":
                     var31.add(new String[]{"g", var27, var28, "tps"});
               }
            }
         }

          float var54 = var7 + 4.0F;
          float brandW = 10.0F + 4.0F;
          var54 += brandW;

         for (String[] var58 : var31) {
            var54 += 3.0F + helper3(var5, var58[0], 2.0F) + helper2(var6, var58);
         }

         var54 += var7 + 2.0F;
         float var57 = var54;
         drawHudBg(eventRender.getContext().getMatrices(), var3, var4, var57 + 2.0F, var8);
          float var59 = var3 + var7 + 4.0F;
          RenderUtils.drawImage(var2, LOGO, var59, var4 + 2.0F, 10.0F, 10.0F, -1);
         this.waterMarks.clear();
         this.segRowY2 = var4;
         this.segRowH2 = var8;
         int var60 = var31.size();
         float[] var36 = new float[var60];
         float[] var37 = new float[var60];
         float var38 = var59 + brandW;

         for (int var39 = 0; var39 < var60; var39++) {
            String[] var40 = (String[])var31.get(var39);
            var38 += 3.0F;
            var36[var39] = var38;
            float var41 = helper3(var5, var40[0], 2.0F) + helper2(var6, var40);
            var37[var39] = var41;
            var38 += var41;
            this.waterMarks.add(new WaterMark.SegRect(var40[3], var36[var39], var36[var39] + var41));
         }

         float var62 = var60 > 0 ? var36[0] : var59;
         float var63 = var3 + var57 - var7 - 2.0F;
         boolean var64 = this.segmentDragging != null || System.currentTimeMillis() - this.lastReorderTime2 < 300L;

         for (int var42 = 0; var42 < var60; var42++) {
            String[] var43 = (String[])var31.get(var42);
            String var44 = var43[3];
            float var45 = var37[var42];
            boolean var46 = var44.equals(this.segmentDragging);
            float var47 = var46 ? Math.max(var62, Math.min(this.dragMouseX2 - this.dragGrabOffset2, var63 - var45)) : var36[var42];
            float var48 = var47 - var3 + 4.0F;
            float var49 = this.strings.getOrDefault(var44, var48);
            if (var64) {
               var49 += (var48 - var49) * (var46 ? 0.6F : 0.35F);
            } else {
               var49 = var48;
            }

            this.strings.put(var44, var49);
            float var50 = var3 + var49 - 5.0F;
            if (var46) {
               RenderUtils.drawRoundedRect(var2, var50 - 1.5F, var4, var45 + 2.0F, var8 - 1.0F, 2.0F, ColorUtils.setAlphaColor(var9, 90));
            }

            float var51 = helper4(var2, var5, var43[0], var50, var14 + 5.5F, var10, 2.0F);
            if (var43[2].isEmpty()) {
               this.helper5(var2, var43[1], var51, var13 + 5.5F, var9, var12);
            } else {
               var6.drawString(var2, var43[1], var51, var13 + 5.5F, var12);
               var6.drawString(var2, var43[2], var51 + var6.getStringWidth(var43[1]) - 0.5F, var13 + 5.5F, ColorUtils.clientIcon());
            }
         }

         this.draggable.setWidth(var57 - 12.0F);
         this.draggable.setHeight(var8);
      }
   }

   private static float helper2(Font textFont, String[] seg) {
      float var2 = textFont.getStringWidth(seg[1]);
      if (!seg[2].isEmpty()) {
         var2 += textFont.getStringWidth(seg[2]) - 0.5F;
      }

      return var2;
   }

   private static void updateState3(MatrixStack matrices, float x, float centerY, float h, int color) {
      float var5 = h * 0.22F;
      float var6 = h * 0.18F;
      float var7 = h;
      float var8 = h * 0.55F;
      float var9 = var5 * 0.5F;
      RenderUtils.drawRoundedRect(matrices, x + 1.0F, centerY - var7 / 2.0F, var5, var7 - 2.0F, var9, color);
      RenderUtils.drawRoundedRect(matrices, x + (var5 + var6), centerY - var8 / 2.0F, var5, var8 + 4.0F - 2.0F, var9, color);
      RenderUtils.drawRoundedRect(matrices, x - 1.0F + 2.0F * (var5 + var6), centerY - var7 / 2.0F, var5, var7 - 2.0F, var9, color);
   }

   private static float helper3(Font font, String glyph, float gap) {
      return font == null ? 0.0F : font.getStringWidth(glyph) + gap;
   }

   private static float helper4(MatrixStack matrices, Font font, String glyph, float x, float y, int color, float gap) {
      if (font == null) {
         return x;
      }

      font.drawString(matrices, glyph, x, y, color);
      return x + font.getStringWidth(glyph) + gap;
   }

   public void WaveStyle(EventRender.Default eventRender) {
      MatrixStack var2 = eventRender.getContext().getMatrices();
      Font var3 = Fonts.getFont("wave", 30);
      if (var3 == null) {
         var3 = Fonts.getFont("inter_medium", 18);
      }

      if (var3 == null) {
         var3 = Fonts.getFont("sf_regular", 18);
      }

      if (var3 != null) {
         String var4 = "Lumen";
         int var5 = ColorUtils.getThemeColor(90);
         int var6 = ColorUtils.getThemeColor(180);
         int var7 = ColorUtils.getThemeColor(270);
         int var8 = ColorUtils.getColor(360);
         float var9 = 95.0F + var3.getStringWidth("ful");
         float var10 = Math.max(var9, var3.getStringWidth(var4));
         float var11 = this.draggable.getX();
         float var12 = this.draggable.getY();
         RenderUtils.drawShadow(var2, var11, var12, var9, 12.0F, 10.0F, 15.0F, var8, var6, var5, var7);
         var3.drawGradientStringHorizontal(var2, var4, var11, var12, var5, var6);
         this.draggable.setWidth(var10);
         this.draggable.setHeight(12.0F);
      }
   }

   private void helper5(MatrixStack matrices, String serverName, float x, float y, int themeColor, int whiteColor) {
      Font var7 = Fonts.getFont("inter_medium", 13);
      if (var7 == null) {
         var7 = Fonts.getFont("sf_regular", 13);
      }

      if (var7 != null) {
         String[] var8 = serverName.split("\\.");
         if (var8.length < 2) {
            var7.drawString(matrices, serverName, x, y, whiteColor);
         } else {
            String var9 = String.join(".", Arrays.copyOf(var8, var8.length - 1));
            String var10 = "." + var8[var8.length - 1];
            var7.drawString(matrices, var9, x, y, whiteColor);
            float var11 = x + var7.getStringWidth(var9) - 2.0F;
            var7.drawString(matrices, var10, var11, y, themeColor);
         }
      }
   }

   private String helper6(String serverName) {
      if (serverName != null && !serverName.isEmpty()) {
         String var2 = serverName;
         int var3 = var2.indexOf(58);
         if (var3 > 0) {
            var2 = var2.substring(0, var3);
         }

         String[] var4 = var2.split("\\.");
         return var4.length >= 3 ? String.join(".", Arrays.copyOfRange(var4, 1, var4.length)) : var2;
      } else {
         return "";
      }
   }

   private float helper7() {
      return Lumen.INSTANCE != null && Lumen.INSTANCE.tpsCalc != null ? Math.max(0.0F, Math.min(20.0F, Lumen.INSTANCE.tpsCalc.getTPS())) : 20.0F;
   }

   private String helper8(float value) {
      int var2 = Math.round(value * 10.0F);
      return var2 / 10 + "." + Math.abs(var2 % 10);
   }

   @Generated
   public void setShowFps(boolean showFps) {
      this.showFps2 = showFps;
   }

   @Generated
   public void setShowMs(boolean showMs) {
      this.showMs2 = showMs;
   }

   @Generated
   public void setShowServer(boolean showServer) {
      this.showServer2 = showServer;
   }

   @Generated
   public void setShowTps(boolean showTps) {
      this.showTps2 = showTps;
   }

   @Generated
   public void setSegRowY(float segRowY) {
      this.segRowY2 = segRowY;
   }

   @Generated
   public void setSegRowH(float segRowH) {
      this.segRowH2 = segRowH;
   }

   @Generated
   public void setDraggingSeg(String draggingSeg) {
      this.segmentDragging = draggingSeg;
   }

   @Generated
   public void setDragMouseX(float dragMouseX) {
      this.dragMouseX2 = dragMouseX;
   }

   @Generated
   public void setDragGrabOffset(float dragGrabOffset) {
      this.dragGrabOffset2 = dragGrabOffset;
   }

   @Generated
   public void setLastReorderTime(long lastReorderTime) {
      this.lastReorderTime2 = lastReorderTime;
   }

   @Generated
   public boolean isShowFps() {
      return this.showFps2;
   }

   @Generated
   public boolean isShowMs() {
      return this.showMs2;
   }

   @Generated
   public boolean isShowServer() {
      return this.showServer2;
   }

   @Generated
   public boolean isShowTps() {
      return this.showTps2;
   }

   @Generated
   public List<String> getSegmentOrder() {
      return this.segmentOrderConfig;
   }

   @Generated
   public List<WaterMark.SegRect> getSegmentRects() {
      return this.waterMarks;
   }

   @Generated
   public float getSegRowY() {
      return this.segRowY2;
   }

   @Generated
   public float getSegRowH() {
      return this.segRowH2;
   }

   @Generated
   public String getDraggingSeg() {
      return this.segmentDragging;
   }

   @Generated
   public Map<String, Float> getSegAnimX() {
      return this.strings;
   }

   @Generated
   public float getDragMouseX() {
      return this.dragMouseX2;
   }

   @Generated
   public float getDragGrabOffset() {
      return this.dragGrabOffset2;
   }

   @Generated
   public long getLastReorderTime() {
      return this.lastReorderTime2;
   }

   @Generated
   public Interface getInterfaceModule() {
      return this.interfaceModule2;
   }

   private static final class SegRect {
      final String key;
      final float x1;
      final float x2;

      SegRect(String key, float x1, float x2) {
         this.key = key;
         this.x1 = x1;
         this.x2 = x2;
      }

      float center() {
         return (this.x1 + this.x2) * 0.5F;
      }
   }
}
