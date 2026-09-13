package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

class CubeParticle implements QClient {
   double x;
   double y;
   double z;
   double worldX;
   double worldY;
   double worldZ;
   long time;
   LivingEntity entity;
   boolean fading;
   long fadeStartTime;
   float vx;
   float vy;
   float vz;
   float rotX;
   float rotY;
   float rotZ;
   float rotSpeedX;
   float rotSpeedY;
   float rotSpeedZ;

   public CubeParticle(LivingEntity entity, double x, double y, double z) {
      this.entity = entity;
      this.x = x;
      this.y = y;
      this.z = z;
      this.time = System.currentTimeMillis();
      this.rotX = (float)(Math.random() * 360.0);
      this.rotY = (float)(Math.random() * 360.0);
      this.rotZ = (float)(Math.random() * 360.0);
      this.rotSpeedX = 1.4F + (float)Math.random() * 3.4F;
      this.rotSpeedY = 1.4F + (float)Math.random() * 3.4F;
      this.rotSpeedZ = 1.4F + (float)Math.random() * 3.4F;
      this.vx = (float)((Math.random() - 0.5) * 0.0022);
      this.vy = 0.031F + (float)Math.random() * 0.02F;
      this.vz = (float)((Math.random() - 0.5) * 0.0022);
   }

   public void update(float dt, long now, LivingEntity currentTarget) {
      float var5 = dt * 60.0F;
      this.rotX = this.rotX + this.rotSpeedX * var5;
      this.rotY = this.rotY + this.rotSpeedY * var5;
      this.rotZ = this.rotZ + this.rotSpeedZ * var5;
      if (!this.fading) {
         this.x = this.x + this.vx * var5;
         this.y = this.y + this.vy * var5;
         this.z = this.z + this.vz * var5;
         this.vx *= 0.992F;
         this.vz *= 0.992F;
         this.vy *= 0.989F;
         if (this.entity != null) {
            double var6 = Math.max(2.2, this.entity.getHeight() * 1.85);
            if (this.y >= var6) {
               this.y = var6;
               this.helper(now);
               return;
            }
         }

         boolean var8 = currentTarget == null || this.entity == null || !this.entity.isAlive() || this.entity != currentTarget;
         if (var8 || now - this.time >= 560L) {
            this.helper(now);
         }
      }
   }

   public boolean shouldRemove(long now) {
      return this.fading && now - this.fadeStartTime >= 320L;
   }

   public int getRenderColor(int baseColor, int redColor, float hurtPC, long now) {
      float var6 = this.helper2(now);
      if (var6 <= 0.001F) {
         return 0;
      }

      int var7 = ColorUtils.replAlpha(baseColor, (int)(var6 * 255.0F));
      int var8 = ColorUtils.replAlpha(redColor, (int)(var6 * 255.0F));
      return TargetESP.INSTANCE.overCol(var7, var8, hurtPC);
   }

   public boolean appendCubeFaces(BufferBuilder faceBuilder, MatrixStack ms, Vec3d cam, float partialTicks, int color) {
      float var6 = (color >> 24 & 0xFF) / 255.0F;
      if (var6 <= 0.001F) {
         return false;
      }

      Vec3d var7 = this.helper3(partialTicks);
      if (var7 == null) {
         return false;
      }

      float var8 = this.fading
         ? MathHelper.lerp(MathHelper.clamp((float)(System.currentTimeMillis() - this.fadeStartTime) / 320.0F, 0.0F, 1.0F), 1.0F, 0.45F)
         : 1.0F;
      float var9 = 0.12F * var8;
      ms.push();
      ms.translate(var7.x - cam.x, var7.y - cam.y, var7.z - cam.z);
      ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.rotX));
      ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.rotY));
      ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotZ));
      ms.scale(var9, var9, var9);
      Matrix4f var10 = ms.peek().getPositionMatrix();
      this.helper4(faceBuilder, var10, color);
      ms.pop();
      return true;
   }

   public boolean appendCubeLines(BufferBuilder lineBuilder, MatrixStack ms, Vec3d cam, float partialTicks, int color) {
      float var6 = (color >> 24 & 0xFF) / 255.0F;
      if (var6 <= 0.001F) {
         return false;
      }

      Vec3d var7 = this.helper3(partialTicks);
      if (var7 == null) {
         return false;
      }

      float var8 = this.fading
         ? MathHelper.lerp(MathHelper.clamp((float)(System.currentTimeMillis() - this.fadeStartTime) / 320.0F, 0.0F, 1.0F), 1.0F, 0.45F)
         : 1.0F;
      float var9 = 0.12F * var8;
      ms.push();
      ms.translate(var7.x - cam.x, var7.y - cam.y, var7.z - cam.z);
      ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.rotX));
      ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.rotY));
      ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotZ));
      ms.scale(var9, var9, var9);
      Matrix4f var10 = ms.peek().getPositionMatrix();
      this.helper5(lineBuilder, var10, ColorUtils.replAlpha(color, Math.max(1, (int)((color >> 24 & 0xFF) * 0.7F))));
      ms.pop();
      return true;
   }

   public boolean appendBloom(BufferBuilder builder, MatrixStack ms, Vec3d camPos, float camYaw, float camPitch, float partialTicks, int colorInt, long now) {
      float var10 = this.helper2(now);
      if (var10 <= 0.001F) {
         return false;
      }

      Vec3d var11 = this.helper3(partialTicks);
      if (var11 == null) {
         return false;
      }

      float var12 = this.fading ? MathHelper.lerp(MathHelper.clamp((float)(now - this.fadeStartTime) / 320.0F, 0.0F, 1.0F), 1.0F, 0.55F) : 1.0F;
      float var13 = 0.95F * var12;
      int var14 = (int)(var10 * 0.15F * 255.0F);
      if (var14 <= 0) {
         return false;
      }

      int var15 = colorInt >> 16 & 0xFF;
      int var16 = colorInt >> 8 & 0xFF;
      int var17 = colorInt & 0xFF;
      ms.push();
      ms.translate(var11.x - camPos.x, var11.y - camPos.y, var11.z - camPos.z);
      ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camYaw));
      ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camPitch));
      ms.scale(var13, var13, var13);
      Matrix4f var18 = ms.peek().getPositionMatrix();
      builder.vertex(var18, -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(var15, var16, var17, var14);
      builder.vertex(var18, 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(var15, var16, var17, var14);
      builder.vertex(var18, 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(var15, var16, var17, var14);
      builder.vertex(var18, -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(var15, var16, var17, var14);
      ms.pop();
      return true;
   }

   private void helper(long now) {
      if (!this.fading) {
         Vec3d var3 = this.helper3(1.0F);
         if (var3 != null) {
            this.worldX = var3.x;
            this.worldY = var3.y;
            this.worldZ = var3.z;
         }

         this.fadeStartTime = now;
         this.fading = true;
         this.entity = null;
      }
   }

   private float helper2(long now) {
      if (!this.fading) {
         float var3 = MathHelper.clamp((float)(now - this.time) / 140.0F, 0.0F, 1.0F);
         float var4 = 1.0F - MathHelper.clamp((float)(now - this.time - 440L) / 120.0F, 0.0F, 0.35F);
         return var3 * var4;
      } else {
         return 1.0F - MathHelper.clamp((float)(now - this.fadeStartTime) / 320.0F, 0.0F, 1.0F);
      }
   }

   private Vec3d helper3(float partialTicks) {
      return !this.fading && this.entity != null
         ? new Vec3d(
            MathHelper.lerp(partialTicks, this.entity.lastRenderX, this.entity.getX()) + this.x,
            MathHelper.lerp(partialTicks, this.entity.lastRenderY, this.entity.getY()) + this.y,
            MathHelper.lerp(partialTicks, this.entity.lastRenderZ, this.entity.getZ()) + this.z
         )
         : new Vec3d(this.worldX, this.worldY, this.worldZ);
   }

   private void helper4(BufferBuilder fb, Matrix4f m, int color) {
      float var4 = -0.5F;
      float var5 = 0.5F;
      int var6 = ColorUtils.replAlpha(color, Math.max(1, (int)((color >> 24 & 0xFF) * 0.16F)));
      this.helper6(fb, m, var4, var4, var4, var5, var5, var5, var6);
   }

   private void helper5(BufferBuilder buf, Matrix4f m, int color) {
      for (byte[] var7 : TargetESP.CUBE_EDGES) {
         buf.vertex(m, var7[0] * 0.5F, var7[1] * 0.5F, var7[2] * 0.5F).color(color);
         buf.vertex(m, var7[3] * 0.5F, var7[4] * 0.5F, var7[5] * 0.5F).color(color);
      }
   }

   private void helper6(BufferBuilder buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
      buf.vertex(m, x1, y1, z1).color(color);
      buf.vertex(m, x2, y1, z1).color(color);
      buf.vertex(m, x2, y1, z2).color(color);
      buf.vertex(m, x1, y1, z2).color(color);
      buf.vertex(m, x1, y2, z1).color(color);
      buf.vertex(m, x1, y2, z2).color(color);
      buf.vertex(m, x2, y2, z2).color(color);
      buf.vertex(m, x2, y2, z1).color(color);
      buf.vertex(m, x1, y1, z1).color(color);
      buf.vertex(m, x1, y2, z1).color(color);
      buf.vertex(m, x2, y2, z1).color(color);
      buf.vertex(m, x2, y1, z1).color(color);
      buf.vertex(m, x1, y1, z2).color(color);
      buf.vertex(m, x2, y1, z2).color(color);
      buf.vertex(m, x2, y2, z2).color(color);
      buf.vertex(m, x1, y2, z2).color(color);
      buf.vertex(m, x1, y1, z1).color(color);
      buf.vertex(m, x1, y1, z2).color(color);
      buf.vertex(m, x1, y2, z2).color(color);
      buf.vertex(m, x1, y2, z1).color(color);
      buf.vertex(m, x2, y1, z1).color(color);
      buf.vertex(m, x2, y2, z1).color(color);
      buf.vertex(m, x2, y2, z2).color(color);
      buf.vertex(m, x2, y1, z2).color(color);
   }
}
