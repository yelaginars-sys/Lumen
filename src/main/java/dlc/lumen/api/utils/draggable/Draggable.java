package dlc.lumen.api.utils.draggable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.math.MathUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.client.modules.Module;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class Draggable implements QClient {
   @Expose
   @SerializedName("x")
   private float x;
   @Expose
   @SerializedName("y")
   private float y;
   public float initialXVal;
   public float initialYVal;
   private float fvalue;
   private float fvalue2;
   private boolean inRegion;
   private boolean flag;
   private float width2;
   private float height;
   private float scaleX = 1.0F;
   private float scaleY = 1.0F;
   private boolean resizable = false;
   private boolean locked = false;
   @Expose
   @SerializedName("name")
   private String name;
   private final Module module;
   private float width;
   private float scaledWidth2;
   private static final float fvalue3 = 1.0F;
   private static final float fvalue4 = 10.0F;
   private float fvalue5 = 0.0F;
   private long timeMs;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private boolean flag7;
   private boolean flag8;
   private boolean flag9;
   private boolean flag10;
   private boolean flag11;
   private static final float fvalue6 = 0.19F;
   private static final float fvalue7 = 25.0F;
   private static final float fvalue8 = 4.0F;
   private static final float fvalue9 = 0.14F;
   private static final float fvalue10 = 0.1F;
   private static final float fvalue11 = 0.18F;
   private static final float fvalue12 = 0.22F;
   private static final float fvalue13 = 0.18F;
   private static final float fvalue14 = 1.01F;
   private static final float fvalue15 = 0.1F;
   private static final float fvalue16 = 0.02F;
   private static final float fvalue17 = 7.0F;
   private static final float fvalue18 = 0.4F;
   private static final float fvalue19 = 0.55F;
   private static final float fvalue20 = 2.75F;
   private float fvalue21;
   private float fvalue22;
   private float fvalue23;
   private float fvalue24;
   private boolean flag12;
   private boolean flag13;
   private float fvalue25 = 1.0F;
   private float scaledWidth = 1.0F;
   private float fvalue26;
   private float fvalue27;

   public Draggable(Module module, String name, float initialXVal, float initialYVal) {
      this.module = module;
      this.name = name;
      this.x = initialXVal;
      this.y = initialYVal;
      this.initialXVal = initialXVal;
      this.initialYVal = initialYVal;
   }

   public float getX() {
      return this.x;
   }

   public void setX(float x) {
      this.x = x;
   }

   public float getY() {
      return this.y;
   }

   public void setY(float y) {
      this.y = y;
   }

   public final void onDraw(int mouseX, int mouseY, Window res, MatrixStack ms) {
      if (this.locked) {
         this.inRegion = false;
         this.flag = false;
      } else {
         float var5 = res.getScaledWidth() / 2.0F;
         float var6 = res.getScaledHeight() / 2.0F;
         float var7 = res.getScaledWidth() / 4.0F;
         float var8 = res.getScaledHeight() / 4.0F;
         float var9 = res.getScaledWidth() / 8.0F;
         float var10 = res.getScaledHeight() / 8.0F;
         float var11 = res.getScaledWidth() / 1.15F;
         float var12 = res.getScaledHeight() / 1.15F;
         float var13 = res.getScaledWidth() / 1.35F;
         float var14 = res.getScaledHeight() / 1.35F;
         this.flag2 = this.flag3 = this.flag4 = this.flag5 = this.flag6 = this.flag7 = this.flag8 = this.flag9 = this.flag10 = this.flag11 = false;
         if (this.inRegion) {
            this.scaledWidth = 1.01F;
            this.fvalue22 = 0.0F;
            this.fvalue23 = 0.0F;
            this.flag12 = false;
            float var15 = this.getWidth();
            float var16 = this.getHeight();
            this.width = mouseX - this.fvalue;
            this.scaledWidth2 = mouseY - this.fvalue2;
            boolean var17 = false;
            if (Math.abs(this.width + var15 / 2.0F - var5) < 10.0F) {
               this.width = var5 - var15 / 2.0F;
               this.flag3 = true;
               var17 = true;
            }

            if (Math.abs(this.scaledWidth2 + var16 / 2.0F - var6) < 10.0F) {
               this.scaledWidth2 = var6 - var16 / 2.0F;
               this.flag2 = true;
               var17 = true;
            }

            if (Math.abs(this.width + var15 / 2.0F - var7) < 10.0F) {
               this.width = var7 - var15 / 2.0F;
               this.flag4 = true;
               var17 = true;
            }

            if (Math.abs(this.scaledWidth2 + var16 / 2.0F - var8) < 10.0F) {
               this.scaledWidth2 = var8 - var16 / 2.0F;
               this.flag8 = true;
               var17 = true;
            }

            if (Math.abs(this.width + var15 / 2.0F - var9) < 10.0F) {
               this.width = var9 - var15 / 2.0F;
               this.flag5 = true;
               var17 = true;
            }

            if (Math.abs(this.scaledWidth2 + var16 / 2.0F - var10) < 10.0F) {
               this.scaledWidth2 = var10 - var16 / 2.0F;
               this.flag9 = true;
               var17 = true;
            }

            if (Math.abs(this.width + var15 / 2.0F - var11) < 10.0F) {
               this.width = var11 - var15 / 2.0F;
               this.flag6 = true;
               var17 = true;
            }

            if (Math.abs(this.scaledWidth2 + var16 / 2.0F - var12) < 10.0F) {
               this.scaledWidth2 = var12 - var16 / 2.0F;
               this.flag10 = true;
               var17 = true;
            }

            if (Math.abs(this.width + var15 / 2.0F - var13) < 10.0F) {
               this.width = var13 - var15 / 2.0F;
               this.flag7 = true;
               var17 = true;
            }

            if (Math.abs(this.scaledWidth2 + var16 / 2.0F - var14) < 10.0F) {
               this.scaledWidth2 = var14 - var16 / 2.0F;
               this.flag11 = true;
               var17 = true;
            }

            if (this.width + var15 > res.getScaledWidth()) {
               this.width = res.getScaledWidth() - var15;
            }

            if (this.scaledWidth2 + var16 > res.getScaledHeight()) {
               this.scaledWidth2 = res.getScaledHeight() - var16;
            }

            if (this.width < 0.0F) {
               this.width = 0.0F;
            }

            if (this.scaledWidth2 < 0.0F) {
               this.scaledWidth2 = 0.0F;
            }

            this.x = MathUtils.lerp(this.x, this.width, 0.19F);
            this.y = MathUtils.lerp(this.y, this.scaledWidth2, 0.19F);
            this.handleActive(var17);
         } else if (this.flag) {
            if (!this.resizable) {
               this.flag = false;
               this.scaledWidth = 1.0F;
               this.fvalue22 = 0.0F;
               this.flag12 = false;
            } else {
               this.scaledWidth = 1.0F;
               this.fvalue22 = 0.0F;
               this.flag12 = false;
               float var22 = Math.max(1.0F, this.width2);
               float var23 = Math.max(1.0F, this.height);
               float var24 = (float)Math.sqrt(var22 * var22 + var23 * var23);
               float var18 = Math.max(1.0F, mouseX - this.x);
               float var19 = Math.max(1.0F, mouseY - this.y);
               float var20 = (float)Math.sqrt(var18 * var18 + var19 * var19);
               float var21 = Math.max(0.55F, Math.min(2.75F, var20 / Math.max(1.0F, var24)));
               this.scaleX = MathUtils.lerp(this.scaleX, var21, 0.32F);
               this.scaleY = MathUtils.lerp(this.scaleY, var21, 0.32F);
            }

            this.handleActive(false);
         } else {
            this.scaledWidth = 1.0F;
            this.fvalue22 = 0.0F;
            this.fvalue23 = MathUtils.lerp(this.fvalue23, 0.0F, 0.18F);
            this.flag12 = false;
            this.handleActive(false);
         }

         this.handleAction();
         this.handleMs(ms, res);
         this.handleMs2(ms);
      }
   }

   private void handleAction() {
      this.fvalue21 = 0.0F;
      float var1 = this.inRegion ? 0.1F : 0.02F;
      this.fvalue25 = MathUtils.lerp(this.fvalue25, this.scaledWidth, var1);
      if (!this.inRegion && Math.abs(this.fvalue25 - 1.0F) < 0.002F) {
         this.fvalue25 = 1.0F;
      }
   }

   public void beginRenderTilt(MatrixStack ms) {
      this.handleAction();
      this.flag13 = false;
      float var2 = Math.max(0.01F, this.scaleX * this.fvalue25);
      float var3 = Math.max(0.01F, this.scaleY * this.fvalue25);
      boolean var4 = Math.abs(var2 - 1.0F) >= 0.002F || Math.abs(var3 - 1.0F) >= 0.002F;
      boolean var5 = false;
      if (var5 || var4) {
         ms.push();
         if (var4) {
            ms.translate(this.x, this.y, 0.0F);
            ms.scale(var2, var3, 1.0F);
            ms.translate(-this.x, -this.y, 0.0F);
         }

         this.flag13 = true;
      }
   }

   public void endRenderTilt(MatrixStack ms) {
      if (this.flag13) {
         ms.pop();
         this.flag13 = false;
      }
   }

   private void handleActive(boolean active) {
      long var2 = System.currentTimeMillis();
      float var4 = (float)(var2 - this.timeMs) / 1000.0F;
      this.timeMs = var2;
      float var5 = 2.0F;
      float var6 = 2.0F;
      if (active) {
         this.fvalue5 += var4 * var5;
         if (this.fvalue5 > 1.0F) {
            this.fvalue5 = 1.0F;
         }
      } else {
         this.fvalue5 -= var4 * var6;
         if (this.fvalue5 < 0.0F) {
            this.fvalue5 = 0.0F;
         }
      }
   }

   private void handleMs(MatrixStack ms, Window res) {
      if (this.fvalue5 > 0.0F) {
         float var3 = res.getScaledWidth() / 2.0F;
         float var4 = res.getScaledHeight() / 2.0F;
         float var5 = res.getScaledWidth() / 4.0F;
         float var6 = res.getScaledHeight() / 4.0F;
         float var7 = res.getScaledWidth() / 8.0F;
         float var8 = res.getScaledHeight() / 8.0F;
         float var9 = res.getScaledWidth() / 1.15F;
         float var10 = res.getScaledHeight() / 1.15F;
         float var11 = res.getScaledWidth() / 1.35F;
         float var12 = res.getScaledHeight() / 1.35F;
         int var13 = (int)(this.fvalue5 * 255.0F) << 24 | 16777215;
         if (this.flag3) {
            RenderUtils.drawRoundedRect(ms, var3 - 0.33333334F, 0.0F, 1.0F, res.getScaledHeight(), 1.0F, var13);
         }

         if (this.flag2) {
            RenderUtils.drawRoundedRect(ms, 0.0F, var4 - 0.33333334F, res.getScaledWidth(), 1.0F, 1.0F, var13);
         }

         if (this.flag4) {
            RenderUtils.drawRoundedRect(ms, var5 - 0.33333334F, 0.0F, 1.0F, res.getScaledHeight(), 1.0F, var13);
         }

         if (this.flag8) {
            RenderUtils.drawRoundedRect(ms, 0.0F, var6 - 0.33333334F, res.getScaledWidth(), 1.0F, 1.0F, var13);
         }

         if (this.flag5) {
            RenderUtils.drawRoundedRect(ms, var7 - 0.33333334F, 0.0F, 1.0F, res.getScaledHeight(), 1.0F, var13);
         }

         if (this.flag9) {
            RenderUtils.drawRoundedRect(ms, 0.0F, var8 - 0.33333334F, res.getScaledWidth(), 1.0F, 1.0F, var13);
         }

         if (this.flag6) {
            RenderUtils.drawRoundedRect(ms, var9 - 0.33333334F, 0.0F, 1.0F, res.getScaledHeight(), 1.0F, var13);
         }

         if (this.flag10) {
            RenderUtils.drawRoundedRect(ms, 0.0F, var10 - 0.33333334F, res.getScaledWidth(), 1.0F, 1.0F, var13);
         }

         if (this.flag7) {
            RenderUtils.drawRoundedRect(ms, var11 - 0.33333334F, 0.0F, 1.0F, res.getScaledHeight(), 1.0F, var13);
         }

         if (this.flag11) {
            RenderUtils.drawRoundedRect(ms, 0.0F, var12 - 0.33333334F, res.getScaledWidth(), 1.0F, 1.0F, var13);
         }
      }
   }

   public final boolean onClick(double mouseX, double mouseY, int button) {
      if (button != 0 || this.locked) {
         return false;
      } else if (this.getWidth2() && this.checkMouseX(mouseX, mouseY)) {
         this.flag = true;
         this.inRegion = false;
         float var6 = Math.max(0.55F, Math.min(2.75F, (this.scaleX + this.scaleY) * 0.5F));
         this.scaleX = var6;
         this.scaleY = var6;
         this.fvalue26 = this.scaleX;
         this.fvalue27 = this.scaleY;
         this.scaledWidth = 1.0F;
         return true;
      } else if (HoveringUtils.isInRegion(mouseX, mouseY, this.x, this.y, this.getWidth(), this.getHeight())) {
         this.inRegion = true;
         this.scaledWidth = 1.01F;
         this.fvalue = (int)(mouseX - this.x);
         this.fvalue2 = (int)(mouseY - this.y);
         this.fvalue23 = 0.0F;
         this.flag12 = false;
         this.timeMs = System.currentTimeMillis();
         return true;
      } else {
         return false;
      }
   }

   public final void onRelease(int button) {
      if (button == 0) {
         this.inRegion = false;
         this.flag = false;
         this.scaledWidth = 1.0F;
         this.fvalue22 = 0.0F;
         this.fvalue23 = 0.0F;
         this.flag12 = false;
      }
   }

   public float getWidth() {
      return this.width2 * this.scaleX;
   }

   public float getHeight() {
      return this.height * this.scaleY;
   }

   private boolean getWidth2() {
      return mc != null && this.resizable && mc.currentScreen instanceof ChatScreen && this.width2 > 1.0F && this.height > 1.0F;
   }

   private boolean checkMouseX(double mouseX, double mouseY) {
      float var5 = this.getHandleX();
      float var6 = this.getHandleY();
      return HoveringUtils.isHovered(mouseX, mouseY, var5 - 3.0F, var6 - 3.0F, 13.0, 13.0);
   }

   private void handleMs2(MatrixStack ms) {
      if (this.getWidth2()) {
         int var2 = ColorUtils.rgba(255, 255, 255, this.flag ? 128 : 92);
         int var3 = ColorUtils.rgba(255, 255, 255, this.flag ? 235 : 205);
         float var4 = this.x;
         float var5 = this.y;
         float var6 = this.getWidth();
         float var7 = this.getHeight();
         float var8 = this.getHandleX();
         float var9 = this.getHandleY();
         RenderUtils.drawRoundCircle(ms, var8 + 3.5F, var9 + 3.5F, 5.0F, var3);
      }
   }

    private float getHandleX() {
       return this.x + this.getWidth() + 0.4F - 3.5F;
    }

    private float getHandleY() {
      return this.y + this.getHeight() + 0.4F - 3.5F;
   }

   @Generated
   public void setWidth(float width) {
      this.width2 = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   public void setScaleX(float scaleX) {
      this.scaleX = scaleX;
   }

   @Generated
   public float getScaleX() {
      return this.scaleX;
   }

   @Generated
   public void setScaleY(float scaleY) {
      this.scaleY = scaleY;
   }

   @Generated
   public float getScaleY() {
      return this.scaleY;
   }

   @Generated
   public void setResizable(boolean resizable) {
      this.resizable = resizable;
   }

   @Generated
   public boolean isResizable() {
      return this.resizable;
   }

   @Generated
   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   @Generated
   public boolean isLocked() {
      return this.locked;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public Module getModule() {
      return this.module;
   }
}
