package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.Theme;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class BlockOverlay extends Module {
   public static BlockOverlay INSTANCE = new BlockOverlay();
   private final ModeSetting modeSetting = new ModeSetting("Режим", "Шейдер", "Шейдер", "Космос");
   private final FloatSetting floatSetting = new FloatSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F).visible(() -> this.modeSetting.is("Шейдер"));
   private final FloatSetting floatSetting2 = new FloatSetting("Частота волн", 1.0F, 1.0F, 3.0F, 0.1F).visible(() -> this.modeSetting.is("Шейдер"));
   private final FloatSetting floatSetting3 = new FloatSetting("Скорость космоса", 1.0F, 0.1F, 3.0F, 0.05F).visible(() -> this.modeSetting.is("Космос"));
   private final FloatSetting floatSetting4 = new FloatSetting("Плотность туманности", 1.0F, 0.3F, 2.5F, 0.05F)
      .visible(() -> this.modeSetting.is("Космос"));
   private final FloatSetting floatSetting5 = new FloatSetting("Яркость звёзд", 1.0F, 0.0F, 2.0F, 0.05F).visible(() -> this.modeSetting.is("Космос"));
   private final FloatSetting floatSetting6 = new FloatSetting("Объём тумана", 0.35F, 0.05F, 1.0F, 0.05F).visible(() -> this.modeSetting.is("Космос"));
   private final FloatSetting floatSetting7 = new FloatSetting("Скорость нитей", 1.4F, 0.1F, 5.0F, 0.1F).visible(() -> this.modeSetting.is("Нитки"));
   private final FloatSetting floatSetting8 = new FloatSetting("Изгиб нитей", 0.55F, 0.0F, 1.5F, 0.01F).visible(() -> this.modeSetting.is("Нитки"));
   private final FloatSetting floatSetting9 = new FloatSetting("Ширина обводки", 1.1F, 0.1F, 5.0F, 0.1F);
   private final FloatSetting floatSetting10 = new FloatSetting("Сила свечения", 1.0F, 0.0F, 5.0F, 0.1F);
   private final FloatSetting floatSetting11 = new FloatSetting("Заливка", 0.6F, 0.0F, 1.0F, 0.01F);
   private final FloatSetting floatSetting12 = new FloatSetting("Прозрачность", 1.0F, 0.0F, 1.0F, 0.01F);
   private final FloatSetting floatSetting13 = new FloatSetting("Плавность", 0.24F, 0.05F, 0.6F, 0.01F);
   private Framebuffer framebuffer;
   private int index = -1;
   private int index2 = -1;
   private boolean flag;
   private BlockPos blockPos;
   private Box box2;
   private Box box3;
   private int index3 = -1;
   private int index4 = -1;
   private Vec3d vec3d = Vec3d.ZERO;
   private Vec3d vec3d2 = new Vec3d(1.0, 1.0, 1.0);
   private final Matrix4f matrix4f = new Matrix4f();

   public BlockOverlay() {
      super("BlockOverlay", "Block overlay shader", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.modeSetting,
         this.floatSetting,
         this.floatSetting2,
         this.floatSetting3,
         this.floatSetting4,
         this.floatSetting5,
         this.floatSetting6,
         this.floatSetting7,
         this.floatSetting8,
         this.floatSetting9,
         this.floatSetting10,
         this.floatSetting11,
         this.floatSetting12,
         this.floatSetting13
      );
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.blockPos = null;
      this.box2 = null;
      this.box3 = null;
      super.onDisable();
   }

   @EventLink(priority = -100)
   public void onRender3D(Event3DRender event) {
      if (mc != null && mc.world != null && mc.player != null) {
         Box var2 = this.helper7();
         if (var2 == null) {
            this.flag = false;
            this.blockPos = null;
            this.box2 = null;
            this.box3 = null;
         } else {
            if (this.box2 != null && this.box3 != null && this.blockPos != null) {
               this.box3 = var2;
               this.box2 = this.helper8(this.box2, this.box3, this.floatSetting13.get());
            } else {
               this.box2 = var2;
               this.box3 = var2;
            }

            this.blockPos = BlockPos.ofFloored(var2.minX, var2.minY, var2.minZ);
            this.helper13();
            Vec3d var3 = event.getCamera().getPos();
            Box var4 = this.box2.offset(-var3.x, -var3.y, -var3.z);
            Matrix4f var5 = event.getMatrices().peek().getPositionMatrix();
            this.helper6();
            if (this.framebuffer != null) {
               this.flag = true;
               this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               this.framebuffer.clear();
               this.helper10();
               this.framebuffer.beginWrite(false);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableCull();
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
               if (this.modeSetting.is("Космос")) {
                  this.vec3d = new Vec3d(-var4.minX, -var4.minY, -var4.minZ);
                  this.vec3d2 = new Vec3d(var4.maxX - var4.minX, var4.maxY - var4.minY, var4.maxZ - var4.minZ);
                  this.matrix4f.set(event.getProjectionMatrix()).mul(event.getPositionMatrix()).invert();
                  this.helper24(var5, var4.expand(this.floatSetting6.get()));
               } else {
                  this.helper24(var5, var4);
               }

               RenderSystem.depthMask(true);
               RenderSystem.disableDepthTest();
               RenderSystem.enableCull();
               RenderSystem.disableBlend();
               mc.getFramebuffer().beginWrite(false);
               if (this.modeSetting.is("Нитки")) {
                  this.helper9(var5, var4);
               }
            }
         }
      }
   }

   @EventLink(priority = 200)
   public void onRender2D(EventRender.Default event) {
      if (this.flag && this.framebuffer != null) {
         if (!this.modeSetting.is("Нитки")) {
            if (this.modeSetting.is("Космос")) {
               this.helper();
            } else {
               ShaderProgram var2 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blockOverlay);
               if (var2 != null) {
                  boolean var3 = this.modeSetting.is("Нитки");
                  int var4 = this.index3;
                  int var5 = this.index4;
                  mc.getFramebuffer().beginWrite(false);
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.enableDepthTest();
                  RenderSystem.setShader(ShaderUtils.blockOverlay);
                  RenderSystem.setShaderTexture(0, this.framebuffer.getColorAttachment());
                  this.helper4(
                     var2, "texelSize", 1.0F / Math.max(1, mc.getWindow().getFramebufferWidth()), 1.0F / Math.max(1, mc.getWindow().getFramebufferHeight())
                  );
                  this.helper5(var2, "color", ColorUtils.redf(var4), ColorUtils.greenf(var4), ColorUtils.bluef(var4));
                  this.helper5(var2, "color2", ColorUtils.redf(var5), ColorUtils.greenf(var5), ColorUtils.bluef(var5));
                  this.helper3(var2, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
                  this.helper3(var2, "speed", this.floatSetting.get());
                  this.helper3(var2, "scale", this.floatSetting2.get());
                  this.helper3(var2, "outline", this.floatSetting9.get());
                  this.helper3(var2, "glow", var3 ? 0.0F : this.floatSetting10.get());
                  this.helper3(var2, "fill", var3 ? 0.0F : this.floatSetting11.get());
                  this.helper3(var2, "alpha", var3 ? 1.0F : this.floatSetting12.get());
                  this.helper3(var2, "outlineOnly", var3 ? 1.0F : 0.0F);
                  this.helper25();
                  RenderSystem.enableDepthTest();
                  RenderSystem.disableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.setShaderTexture(0, 0);
               }
            }
         }
      }
   }

   private void helper() {
      ShaderProgram var1 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blockOverlayNebula);
      if (var1 != null) {
         int var2 = this.index3;
         int var3 = this.index4;
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         RenderSystem.setShader(ShaderUtils.blockOverlayNebula);
         RenderSystem.setShaderTexture(0, this.framebuffer.getColorAttachment());
         this.helper2(var1, "uInvViewProj", this.matrix4f);
         this.helper5(var1, "uCamW", (float)this.vec3d.x, (float)this.vec3d.y, (float)this.vec3d.z);
         this.helper5(var1, "uBoxSize", (float)this.vec3d2.x, (float)this.vec3d2.y, (float)this.vec3d2.z);
         this.helper3(var1, "uMargin", this.floatSetting6.get());
         this.helper5(var1, "uColor", ColorUtils.redf(var2), ColorUtils.greenf(var2), ColorUtils.bluef(var2));
         this.helper5(var1, "uColor2", ColorUtils.redf(var3), ColorUtils.greenf(var3), ColorUtils.bluef(var3));
         this.helper3(var1, "uTime", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         this.helper3(var1, "uSpeed", this.floatSetting3.get());
         this.helper3(var1, "uDensity", this.floatSetting4.get());
         this.helper3(var1, "uStars", this.floatSetting5.get());
         this.helper3(var1, "uGlow", this.floatSetting10.get());
         this.helper3(var1, "uOutline", this.floatSetting9.get());
         this.helper3(var1, "uFill", this.floatSetting11.get());
         this.helper3(var1, "uAlpha", this.floatSetting12.get());
         this.helper25();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderTexture(0, 0);
      }
   }

   private void helper2(ShaderProgram shader, String name, Matrix4f value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   private void helper3(ShaderProgram shader, String name, float value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   private void helper4(ShaderProgram shader, String name, float x, float y) {
      GlUniform var5 = shader.getUniform(name);
      if (var5 != null) {
         var5.set(x, y);
      }
   }

   private void helper5(ShaderProgram shader, String name, float x, float y, float z) {
      GlUniform var6 = shader.getUniform(name);
      if (var6 != null) {
         var6.set(x, y, z);
      }
   }

   private void helper6() {
      int var1 = mc.getWindow().getFramebufferWidth();
      int var2 = mc.getWindow().getFramebufferHeight();
      if (this.framebuffer == null || this.index != var1 || this.index2 != var2) {
         if (this.framebuffer != null) {
            this.framebuffer.delete();
         }

         this.framebuffer = new SimpleFramebuffer(var1, var2, true);
         this.index = var1;
         this.index2 = var2;
      }
   }

   private Box helper7() {
      HitResult var1 = mc.crosshairTarget;
      if (var1 instanceof BlockHitResult var2 && var1.getType() == Type.BLOCK) {
         BlockPos var3 = var2.getBlockPos();
         if (var3 == null) {
            return null;
         }

         if (mc.world.getBlockState(var3).isAir()) {
            return null;
         }

         VoxelShape var4 = mc.world.getBlockState(var3).getOutlineShape(mc.world, var3);
         Box var5 = var4.isEmpty() ? new Box(var3) : var4.getBoundingBox().offset(var3);
         return var5.expand(0.002);
      } else {
         return null;
      }
   }

   private Box helper8(Box a, Box b, float t) {
      return new Box(
         a.minX + (b.minX - a.minX) * t,
         a.minY + (b.minY - a.minY) * t,
         a.minZ + (b.minZ - a.minZ) * t,
         a.maxX + (b.maxX - a.maxX) * t,
         a.maxY + (b.maxY - a.maxY) * t,
         a.maxZ + (b.maxZ - a.maxZ) * t
      );
   }

   private void helper9(Matrix4f matrix, Box box) {
      byte var3 = 5;
      byte var4 = 18;
      float var5 = (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.floatSetting7.get();
      float var6 = 0.0025F;
      float var7 = 0.06F + this.floatSetting8.get() * 0.2F;
      int var8 = Math.max(20, Math.min(255, (int)(this.floatSetting12.get() * 210.0F)));
      int var9 = this.index3;
      long var10 = this.blockPos != null ? this.blockPos.asLong() : 1L;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      this.helper23(matrix, box, ColorUtils.setAlphaColor(var9, (int)(this.floatSetting12.get() * this.floatSetting11.get() * 170.0F)));

      for (int var12 = 0; var12 < 6; var12++) {
         int[] var13 = this.helper14(var12);

         for (int var14 = 0; var14 < var3; var14++) {
            int var15 = var12 * 1000 + var14 * 53;
            int var16 = var13[var14 % var13.length];
            double var17 = var5 * (0.95 + this.helper20(var10, var15 + 1) * 0.55) + var14 * 0.83 + var12 * 1.11;
            double var19 = this.helper22(0.5 + Math.sin(var17 * 1.37 + this.helper20(var10, var15 + 2) * 6.2831853) * 0.38);
            Vec3d var21 = this.helper17(box, var12, var16, var19, 0.0015);
            Vec3d var22 = this.helper19(
               box,
               var12,
               this.helper22(0.5 + (this.helper20(var10, var15 + 3) - 0.5) * 0.46),
               this.helper22(0.5 + (this.helper20(var10, var15 + 4) - 0.5) * 0.46),
               0.0015
            );
            Vec3d var23 = this.helper19(
               box,
               var16,
               this.helper22(0.5 + (this.helper20(var10, var15 + 5) - 0.5) * 0.46),
               this.helper22(0.5 + (this.helper20(var10, var15 + 6) - 0.5) * 0.46),
               0.0015
            );
            Vec3d[] var24 = this.helper15(var12);
            Vec3d[] var25 = this.helper15(var16);
            Vec3d var26 = this.helper16(var12);
            Vec3d var27 = this.helper16(var16);
            double var28 = var7
               * (0.7 + this.helper20(var10, var15 + 7))
               * Math.sin(var17 * 1.9 + this.helper20(var10, var15 + 8) * 6.2831853);
            double var30 = var7
               * (0.7 + this.helper20(var10, var15 + 9))
               * Math.cos(var17 * 1.7 + this.helper20(var10, var15 + 10) * 6.2831853);
            Vec3d var32 = var21.subtract(var22);
            Vec3d var33 = var22.add(var32.multiply(0.38)).add(var24[0].multiply(var28)).add(var24[1].multiply(-var28 * 0.55));
            Vec3d var34 = var22.add(var32.multiply(0.76)).add(var24[0].multiply(-var28 * 0.65)).add(var24[1].multiply(var28 * 0.4));
            Vec3d var35 = var23.subtract(var21);
            Vec3d var36 = var21.add(var35.multiply(0.24)).add(var25[0].multiply(var30)).add(var25[1].multiply(var30 * 0.45));
            Vec3d var37 = var21.add(var35.multiply(0.62)).add(var25[0].multiply(-var30 * 0.7)).add(var25[1].multiply(-var30 * 0.35));
            int var38 = Math.max(18, Math.min(255, (int)(var8 * (0.74 + 0.26 * Math.sin(var17 * 2.6)))));
            int var39 = ColorUtils.setAlphaColor(var9, var38);
            this.helper12(matrix, var22, var33, var34, var21, var26, var4, var39, var6);
            this.helper12(matrix, var21, var36, var37, var23, var27, var4, var39, var6);
         }
      }

      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private void helper10() {
      if (this.framebuffer != null) {
         int var1 = GL11.glGetInteger(36010);
         int var2 = GL11.glGetInteger(36006);
         int var3 = mc.getWindow().getFramebufferWidth();
         int var4 = mc.getWindow().getFramebufferHeight();
         GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
         GL30.glBindFramebuffer(36009, this.framebuffer.fbo);
         GL30.glBlitFramebuffer(0, 0, var3, var4, 0, 0, var3, var4, 256, 9728);
         GL30.glBindFramebuffer(36008, var1);
         GL30.glBindFramebuffer(36009, var2);
      }
   }

   private Vec3d helper11(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, float t) {
      double var6 = 1.0 - t;
      double var8 = var6 * var6;
      double var10 = t * t;
      return p0.multiply(var8 * var6).add(p1.multiply(3.0 * var8 * t)).add(p2.multiply(3.0 * var6 * var10)).add(p3.multiply(var10 * t));
   }

   private void helper12(Matrix4f matrix, Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d faceNormal, int samples, int color, float halfWidth) {
      Vec3d[] var10 = new Vec3d[samples + 1];

      for (int var11 = 0; var11 <= samples; var11++) {
         float var12 = (float)var11 / samples;
         var10[var11] = this.helper11(p0, p1, p2, p3, var12);
      }

      BufferBuilder var21 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int var22 = 0; var22 < samples; var22++) {
         Vec3d var13 = var10[var22];
         Vec3d var14 = var10[var22 + 1];
         Vec3d var15 = var14.subtract(var13);
         if (!(var15.lengthSquared() < 1.0E-6)) {
            Vec3d var16 = faceNormal.crossProduct(var15).normalize().multiply(halfWidth);
            Vec3d var17 = var13.add(var16);
            Vec3d var18 = var13.subtract(var16);
            Vec3d var19 = var14.add(var16);
            Vec3d var20 = var14.subtract(var16);
            var21.vertex(matrix, (float)var17.x, (float)var17.y, (float)var17.z).color(color);
            var21.vertex(matrix, (float)var18.x, (float)var18.y, (float)var18.z).color(color);
            var21.vertex(matrix, (float)var20.x, (float)var20.y, (float)var20.z).color(color);
            var21.vertex(matrix, (float)var19.x, (float)var19.y, (float)var19.z).color(color);
         }
      }

      BufferRenderer.drawWithGlobalProgram(var21.end());
   }

   private void helper13() {
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.themeStorage != null && Lumen.INSTANCE.themeStorage.getThemes() != null) {
         Theme var1 = Lumen.INSTANCE.themeStorage.getThemes().getTheme();
         if (var1 == null) {
            this.index3 = ColorUtils.getThemeColor(0);
            this.index4 = ColorUtils.getThemeColor(180);
         } else {
            if (!"Rainbow".equals(var1.getName())) {
               int var2 = var1.color != null && var1.color.length > 0 ? var1.color[0] : ColorUtils.getThemeColor(0);
               this.index3 = var2;
               this.index4 = var2;
            } else {
               this.index3 = ColorUtils.getThemeColor();
               this.index4 = ColorUtils.getThemeColor(180);
            }
         }
      } else {
         this.index3 = ColorUtils.getThemeColor(0);
         this.index4 = ColorUtils.getThemeColor(180);
      }
   }

   private int[] helper14(int face) {
      return switch (face) {
         case 0, 1 -> new int[]{2, 3, 4, 5};
         case 2, 3 -> new int[]{0, 1, 4, 5};
         default -> new int[]{0, 1, 2, 3};
      };
   }

   private Vec3d[] helper15(int face) {
      return switch (face) {
         case 0, 1 -> new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0)};
         case 2, 3 -> new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, 0.0)};
         default -> new Vec3d[]{new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 0.0)};
      };
   }

   private Vec3d helper16(int face) {
      return switch (face) {
         case 0 -> new Vec3d(0.0, 1.0, 0.0);
         case 1 -> new Vec3d(0.0, -1.0, 0.0);
         case 2 -> new Vec3d(0.0, 0.0, -1.0);
         case 3 -> new Vec3d(0.0, 0.0, 1.0);
         case 4 -> new Vec3d(-1.0, 0.0, 0.0);
         default -> new Vec3d(1.0, 0.0, 0.0);
      };
   }

   private Vec3d helper17(Box box, int faceA, int faceB, double t, double inset) {
      double var8 = Double.NaN;
      double var10 = Double.NaN;
      double var12 = Double.NaN;
      double[] var14 = this.helper18(box, faceA, inset);
      if (!Double.isNaN(var14[0])) {
         var8 = var14[0];
      }

      if (!Double.isNaN(var14[1])) {
         var10 = var14[1];
      }

      if (!Double.isNaN(var14[2])) {
         var12 = var14[2];
      }

      double[] var15 = this.helper18(box, faceB, inset);
      if (!Double.isNaN(var15[0])) {
         var8 = var15[0];
      }

      if (!Double.isNaN(var15[1])) {
         var10 = var15[1];
      }

      if (!Double.isNaN(var15[2])) {
         var12 = var15[2];
      }

      double var16 = this.helper22(t);
      if (Double.isNaN(var8)) {
         var8 = this.helper21(box.minX, box.maxX, var16);
      }

      if (Double.isNaN(var10)) {
         var10 = this.helper21(box.minY, box.maxY, var16);
      }

      if (Double.isNaN(var12)) {
         var12 = this.helper21(box.minZ, box.maxZ, var16);
      }

      return new Vec3d(var8, var10, var12);
   }

   private double[] helper18(Box box, int face, double inset) {
      return switch (face) {
         case 0 -> new double[]{Double.NaN, box.maxY - inset, Double.NaN};
         case 1 -> new double[]{Double.NaN, box.minY + inset, Double.NaN};
         case 2 -> new double[]{Double.NaN, Double.NaN, box.minZ + inset};
         case 3 -> new double[]{Double.NaN, Double.NaN, box.maxZ - inset};
         case 4 -> new double[]{box.minX + inset, Double.NaN, Double.NaN};
         default -> new double[]{box.maxX - inset, Double.NaN, Double.NaN};
      };
   }

   private Vec3d helper19(Box box, int face, double u, double v, double inset) {
      u = this.helper22(u);
      v = this.helper22(v);

      return switch (face) {
         case 0 -> new Vec3d(this.helper21(box.minX, box.maxX, u), box.maxY - inset, this.helper21(box.minZ, box.maxZ, v));
         case 1 -> new Vec3d(this.helper21(box.minX, box.maxX, u), box.minY + inset, this.helper21(box.minZ, box.maxZ, v));
         case 2 -> new Vec3d(this.helper21(box.minX, box.maxX, u), this.helper21(box.minY, box.maxY, v), box.minZ + inset);
         case 3 -> new Vec3d(this.helper21(box.minX, box.maxX, u), this.helper21(box.minY, box.maxY, v), box.maxZ - inset);
         case 4 -> new Vec3d(box.minX + inset, this.helper21(box.minY, box.maxY, v), this.helper21(box.minZ, box.maxZ, u));
         default -> new Vec3d(box.maxX - inset, this.helper21(box.minY, box.maxY, v), this.helper21(box.minZ, box.maxZ, u));
      };
   }

   private double helper20(long seed, int salt) {
      long var4 = seed + -7046029254386353131L * (salt + 1L);
      var4 ^= var4 >>> 30;
      var4 *= -4658895280553007687L;
      var4 ^= var4 >>> 27;
      var4 *= -7723592293110705685L;
      var4 ^= var4 >>> 31;
      return (var4 & 16777215L) / 1.6777216E7;
   }

   private double helper21(double a, double b, double t) {
      return a + (b - a) * t;
   }

   private double helper22(double v) {
      return Math.max(0.0, Math.min(1.0, v));
   }

   private void helper23(Matrix4f matrix, Box box, int color) {
      BufferBuilder var4 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color);
      BufferRenderer.drawWithGlobalProgram(var4.end());
   }

   private void helper24(Matrix4f matrix, Box box) {
      BufferBuilder var3 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      byte var4 = -1;
      var3.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(var4);
      var3.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(var4);
      BufferRenderer.drawWithGlobalProgram(var3.end());
   }

   private void helper25() {
      BufferBuilder var1 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float var2 = Math.max(mc.getWindow().getScaledWidth(), 1);
      float var3 = Math.max(mc.getWindow().getScaledHeight(), 1);
      var1.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var1.vertex(0.0F, var3, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var1.vertex(var2, var3, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var1.vertex(var2, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(var1.end());
   }
}
