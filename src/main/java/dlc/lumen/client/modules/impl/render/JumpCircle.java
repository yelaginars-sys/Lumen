package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class JumpCircle extends Module {
   public static JumpCircle INSTANCE = new JumpCircle();
   private static final float VOLUME = 1850.0F;
   private static final float VOLUME2 = 120.0F;
   private static final float VOLUME3 = 7.0F;
   private static final float VOLUME4 = 0.06F;
   private static final float VOLUME5 = 0.12F;
   private static final int INDEX = 8;
   private final FloatSetting floatSetting = new FloatSetting("Радиус", 1.85F, 0.5F, 4.0F, 0.1F);
   private final FloatSetting floatSetting2 = new FloatSetting("Скорость", 1.2F, 1.0F, 5.0F, 0.1F);
   private final FloatSetting floatSetting3 = new FloatSetting("Скорость исчезновения", 1.5F, 1.0F, 5.0F, 0.5F);
   private final List<JumpCircle.CircleData> jumpCircles = new ArrayList<>();
   private final Identifier textureId = Identifier.of("lumen", "textures/jumpcircle/circle.png");
   private boolean flag = true;

   public JumpCircle() {
      super("JumpCircle", "Круг при прыжке", Module.ModuleCategory.RENDER);
      this.addSettings(this.floatSetting, this.floatSetting2, this.floatSetting3);
   }

   @Override
   public void onEnable() {
      if (mc.player != null) {
         this.flag = mc.player.isOnGround();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.jumpCircles.clear();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         boolean var2 = mc.player.isOnGround();
         if (this.flag && !var2) {
            Vec3d var3 = new Vec3d(mc.player.getX(), Math.floor(mc.player.getY()) + 0.001, mc.player.getZ());
            this.jumpCircles.add(new JumpCircle.CircleData(var3, System.currentTimeMillis()));

            while (this.jumpCircles.size() > 8) {
               this.jumpCircles.remove(0);
            }
         }

         this.flag = var2;
         long var8 = System.currentTimeMillis();
         float var5 = this.helper();
         Iterator var6 = this.jumpCircles.iterator();

         while (var6.hasNext()) {
            JumpCircle.CircleData var7 = (JumpCircle.CircleData)var6.next();
            if (var8 - var7.startTimeMs() > (long)var5) {
               var6.remove();
            }
         }
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (!this.jumpCircles.isEmpty()) {
         long var2 = System.currentTimeMillis();
         Vec3d var4 = event.getCamera().getPos();
         MatrixStack var5 = event.getMatrices();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.textureId);

         for (JumpCircle.CircleData var7 : this.jumpCircles) {
            float var8 = this.helper2(var2, var7);
            if (!(var8 >= 1.0F)) {
               float var9 = this.helper3(var8);
               if (!(var9 <= 0.01F)) {
                  this.helper4(var5, var4, var7, var8, var9, var2);
               }
            }
         }

         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private float helper() {
      return 1850.0F / Math.max(0.25F, this.floatSetting2.get());
   }

   private float helper2(long now, JumpCircle.CircleData circle) {
      return (float)(now - circle.startTimeMs()) / this.helper();
   }

   private float helper3(float progress) {
      float var2 = MathHelper.clamp(progress * this.floatSetting3.get(), 0.0F, 1.0F);
      return 1.0F - var2;
   }

   private void helper4(MatrixStack matrices, Vec3d cameraPos, JumpCircle.CircleData circle, float progress, float alpha, long now) {
      float var8 = (float)(now - circle.startTimeMs()) / 1000.0F;
      float var9 = helper8(progress);
      float var10 = Math.min(var9 * this.floatSetting.get(), this.floatSetting.get());
      float var11 = var8 * 120.0F * this.floatSetting2.get();
      var11 += (float)Math.sin(progress * Math.PI * 2.0) * 30.0F;
      float var12 = (float)Math.sin(var8 * 7.0F * this.floatSetting2.get());
      float var13 = 1.0F + var12 * 0.06F;
      float var14 = MathHelper.clamp(alpha * (1.0F + var12 * 0.12F), 0.0F, 1.0F);
      float var15 = MathHelper.clamp(var14 * 1.25F, 0.0F, 1.0F);
      float var16 = var10 * var13;
      int var17 = this.helper6();
      int var18 = this.helper7();
      int var19 = ColorUtils.setAlphaColor(var17, (int)(255.0F * var15));
      int var20 = ColorUtils.setAlphaColor(var18, (int)(255.0F * var15));
      int var21 = ColorUtils.setAlphaColor(ColorUtils.darken(var17, 0.65F), (int)(255.0F * MathHelper.clamp(var15 * 0.9F, 0.0F, 1.0F)));
      int var22 = ColorUtils.setAlphaColor(ColorUtils.darken(var18, 0.65F), (int)(255.0F * MathHelper.clamp(var15 * 0.9F, 0.0F, 1.0F)));
      matrices.push();
      matrices.translate(circle.pos().x - cameraPos.x, circle.pos().y - cameraPos.y, circle.pos().z - cameraPos.z);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var11));
      Matrix4f var23 = matrices.peek().getPositionMatrix();
      float var24 = var16 * 0.5F;
      float var25 = var16 * 1.08F;
      float var26 = var25 * 0.5F;
      BufferBuilder var27 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      this.helper5(var27, var23, -var24, -var24, var24, var24, var19, var20);
      this.helper5(var27, var23, -var26, -var26, var26, var26, var21, var22);
      BufferRenderer.drawWithGlobalProgram(var27.end());
      matrices.pop();
   }

   private void helper5(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, int colorA, int colorB) {
      int var9 = colorA >> 16 & 0xFF;
      int var10 = colorA >> 8 & 0xFF;
      int var11 = colorA & 0xFF;
      int var12 = colorA >> 24 & 0xFF;
      int var13 = colorB >> 16 & 0xFF;
      int var14 = colorB >> 8 & 0xFF;
      int var15 = colorB & 0xFF;
      int var16 = colorB >> 24 & 0xFF;
      buffer.vertex(matrix, x1, y1, 0.0F).texture(0.0F, 1.0F).color(var9, var10, var11, var12);
      buffer.vertex(matrix, x1, y2, 0.0F).texture(0.0F, 0.0F).color(var13, var14, var15, var16);
      buffer.vertex(matrix, x2, y2, 0.0F).texture(1.0F, 0.0F).color(var13, var14, var15, var16);
      buffer.vertex(matrix, x2, y1, 0.0F).texture(1.0F, 1.0F).color(var9, var10, var11, var12);
   }

   private int helper6() {
      return !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtils.getThemeColor();
   }

   private int helper7() {
      return !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtils.getThemeColor(180);
   }

   private static float helper8(float t) {
      float var1 = 1.0F - t;
      return 1.0F - var1 * var1 * var1;
   }

   private record CircleData(Vec3d pos, long startTimeMs) {

      private CircleData(Vec3d pos, long startTimeMs) {
         this.pos = pos;
         this.startTimeMs = startTimeMs;
      }

      public Vec3d pos() {
         return this.pos;
      }

      public long startTimeMs() {
         return this.startTimeMs;
      }
   }
}
