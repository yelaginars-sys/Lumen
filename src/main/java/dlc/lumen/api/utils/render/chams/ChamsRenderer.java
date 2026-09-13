package dlc.lumen.api.utils.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.api.utils.render.blur.BlurProgram;
import dlc.lumen.client.modules.impl.render.ShaderEsp;
import dlc.lumen.mixin.LivingEntityRendererAccessor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ChamsRenderer implements QClient {
   private static ChamsRenderer chamsRenderer;
   private static final boolean flag = true;
   private static final boolean flag2 = true;
   private static final boolean flag3 = false;
   private static final float fvalue = 0.7F;
   private static final float fvalue2 = 0.45F;
   private static final float fvalue3 = 0.8F;
   private static final float fvalue4 = 0.85F;
   private static final float fvalue5 = 0.8F;
   private static final int count = 130;
   private static final float fvalue6 = 1.18F;
   private static final float fvalue7 = 1.12F;
   private static final int count2 = new Color(85, 255, 85, 60).getRGB();
   private final long timeMs = System.currentTimeMillis();
   private Framebuffer active;
   private int count3 = -1;
   private int count4 = -1;

   public static ChamsRenderer getInstance() {
      if (chamsRenderer == null) {
         chamsRenderer = new ChamsRenderer();
      }

      return chamsRenderer;
   }

   public boolean isActive() {
      ShaderEsp var1 = ShaderEsp.INSTANCE;
      return var1 != null && var1.isEnable() && var1.isChamsEnabled();
   }

   public void invalidate() {
      if (this.active != null) {
         this.active.delete();
         this.active = null;
         this.count3 = -1;
         this.count4 = -1;
      }
   }

   public void render(Event3DRender event) {
      if (this.isActive() && mc.world != null && mc.player != null) {
         this.handleEvent(event);
      }
   }

   private List<AbstractClientPlayerEntity> getList() {
      ArrayList var1 = new ArrayList();

      for (PlayerEntity var3 : mc.world.getPlayers()) {
         if (this.affects(var3)
            && (var3 != mc.player || mc.options.getPerspective() != Perspective.FIRST_PERSON)
            && var3 instanceof AbstractClientPlayerEntity var4) {
            var1.add(var4);
         }
      }

      return var1;
   }

   private void handleEvent(Event3DRender event) {
      List<AbstractClientPlayerEntity> var2 = this.getList();
      if (!var2.isEmpty()) {
         ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.chamsGlass);
         ShaderProgram var4 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.chamsComposite);
         int var5 = BlurProgram.getTexture();
         if (var3 != null && var4 != null && var5 != 0) {
            Framebuffer var6 = this.getFramebuffer();
            var6.clear();
            var6.beginWrite(true);
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            float var7 = (float)(System.currentTimeMillis() - this.timeMs) / 1000.0F;
            RenderSystem.setShader(ShaderUtils.chamsGlass);
            this.handleShader(var3, "time", var7);
            RenderSystem.setShaderTexture(0, var5);
            this.handleShader(var3, "distortStrength", 0.7F);
            this.handleShader(var3, "roughness", 0.45F);
            this.handleShader(var3, "reflectStrength", 0.8F);
            this.handleShader(var3, "glassAlpha", 0.85F);
            this.handleShader(var3, "fresnelStrength", 0.8F);
            GlUniform var8 = var3.getUniform("ScreenSize");
            if (var8 != null) {
               var8.set((float)mc.getWindow().getFramebufferWidth(), (float)mc.getWindow().getFramebufferHeight());
            }

            BufferBuilder var9 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            for (AbstractClientPlayerEntity var11 : var2) {
               this.handleEvent2(event, var11, var9);
            }

            BuiltBuffer var13 = var9.endNullable();
            if (var13 != null) {
               BufferRenderer.drawWithGlobalProgram(var13);
            }

            var6.endWrite();
            mc.getFramebuffer().beginWrite(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(ShaderUtils.chamsComposite);
            RenderSystem.setShaderTexture(0, var6.getColorAttachment());
            this.handleShader(var4, "GlowStrength", 0.0F);
            GlUniform var14 = var4.getUniform("TexelSize");
            if (var14 != null) {
               var14.set(1.0F / mc.getWindow().getFramebufferWidth(), 1.0F / mc.getWindow().getFramebufferHeight());
            }

            BufferBuilder var12 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            var12.vertex(-1.0F, -1.0F, 0.0F).texture(0.0F, 0.0F);
            var12.vertex(1.0F, -1.0F, 0.0F).texture(1.0F, 0.0F);
            var12.vertex(1.0F, 1.0F, 0.0F).texture(1.0F, 1.0F);
            var12.vertex(-1.0F, 1.0F, 0.0F).texture(0.0F, 1.0F);
            BufferRenderer.drawWithGlobalProgram(var12.end());
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
         } else {
            this.handleEvent3(event, var2);
         }
      }
   }

   private void handleEvent2(Event3DRender event, AbstractClientPlayerEntity clientPlayer, BufferBuilder builder) {
      if (mc.getEntityRenderDispatcher().getRenderer(clientPlayer) instanceof PlayerEntityRenderer var5) {
         PlayerEntityRenderState var6 = var5.createRenderState();
         var5.updateRenderState(clientPlayer, var6, event.getTickDelta());
         PlayerEntityModel var7 = var5.getModel();
         var7.setAngles(var6);
         MatrixStack var8 = event.getMatrices();
         var8.push();
         this.handleAction(var8, var6, var5, event.getCamera().getPos(), clientPlayer, event.getTickDelta());
         int var9 = this.resolveFillColor(clientPlayer);
         ModelPart var10 = var7.getRootPart();
         this.handleAction3(var8, builder, var10, var7.head, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, var9);
         this.handleAction3(var8, builder, var10, var7.body, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, var9);
         this.handleAction3(var8, builder, var10, var7.rightArm, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, var9);
         this.handleAction3(var8, builder, var10, var7.leftArm, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, var9);
         this.handleAction3(var8, builder, var10, var7.rightLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, var9);
         this.handleAction3(var8, builder, var10, var7.leftLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, var9);
         var8.pop();
      }
   }

   private void handleEvent3(Event3DRender event, List<AbstractClientPlayerEntity> targets) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);

      for (AbstractClientPlayerEntity var4 : targets) {
         if (mc.getEntityRenderDispatcher().getRenderer(var4) instanceof PlayerEntityRenderer var6) {
            PlayerEntityRenderState var7 = var6.createRenderState();
            var6.updateRenderState(var4, var7, event.getTickDelta());
            PlayerEntityModel var8 = var6.getModel();
            var8.setAngles(var7);
            MatrixStack var9 = event.getMatrices();
            var9.push();
            this.handleAction(var9, var7, var6, event.getCamera().getPos(), var4, event.getTickDelta());
            this.handleMatrices(var9, var8, 0.0F, this.resolveFillColor(var4));
            var9.pop();
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private Framebuffer getFramebuffer() {
      int var1 = mc.getWindow().getFramebufferWidth();
      int var2 = mc.getWindow().getFramebufferHeight();
      if (this.active == null || this.count3 != var1 || this.count4 != var2) {
         if (this.active != null) {
            this.active.delete();
         }

         this.active = new SimpleFramebuffer(var1, var2, true);
         this.active.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.count3 = var1;
         this.count4 = var2;
      }

      return this.active;
   }

   private void handleAction(
      MatrixStack matrices, PlayerEntityRenderState state, PlayerEntityRenderer renderer, Vec3d cameraPos, PlayerEntity player, float tickDelta
   ) {
      Vec3d var7 = player.getLerpedPos(tickDelta);
      double var8 = var7.x - cameraPos.x;
      double var10 = var7.y - cameraPos.y;
      double var12 = var7.z - cameraPos.z;
      matrices.translate(var8, var10, var12);
      if (state.sleepingDirection != null) {
         float var14 = state.standingEyeHeight - 0.1F;
         matrices.translate(-state.sleepingDirection.getOffsetX() * var14, 0.0F, -state.sleepingDirection.getOffsetZ() * var14);
      }

      float var16 = state.baseScale;
      matrices.scale(var16, var16, var16);
      LivingEntityRendererAccessor var15 = (LivingEntityRendererAccessor)renderer;
      var15.lumen$setupTransforms(state, matrices, state.bodyYaw, var16);
      matrices.scale(-1.0F, -1.0F, 1.0F);
      var15.lumen$scale(state, matrices);
      matrices.translate(0.0F, -1.501F, 0.0F);
   }

   private void handleMatrices(MatrixStack matrices, BipedEntityModel<?> model, float expand, int color) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      ModelPart var6 = model.getRootPart();
      this.handleAction2(matrices, var5, var6, model.head, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, expand, color);
      this.handleAction2(matrices, var5, var6, model.body, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, expand, color);
      this.handleAction2(matrices, var5, var6, model.rightArm, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
      this.handleAction2(matrices, var5, var6, model.leftArm, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
      this.handleAction2(matrices, var5, var6, model.rightLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
      this.handleAction2(matrices, var5, var6, model.leftLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, expand, color);
      BufferRenderer.drawWithGlobalProgram(var5.end());
   }

   private void handleAction2(
      MatrixStack baseStack,
      BufferBuilder buffer,
      ModelPart root,
      ModelPart part,
      float offX,
      float offY,
      float offZ,
      float width,
      float height,
      float depth,
      float expand,
      int color
   ) {
      baseStack.push();
      root.rotate(baseStack);
      part.rotate(baseStack);
      Matrix4f var13 = baseStack.peek().getPositionMatrix();
      float var14 = 0.0625F;
      float var15 = expand * var14;
      float var16 = offX * var14 - var15;
      float var17 = offY * var14 - var15;
      float var18 = offZ * var14 - var15;
      float var19 = (offX + width) * var14 + var15;
      float var20 = (offY + height) * var14 + var15;
      float var21 = (offZ + depth) * var14 + var15;
      this.handleAction5(buffer, var13, var16, var20, var18, var16, var20, var21, var19, var20, var21, var19, var20, var18, color);
      this.handleAction5(buffer, var13, var16, var17, var21, var16, var17, var18, var19, var17, var18, var19, var17, var21, color);
      this.handleAction5(buffer, var13, var16, var17, var18, var16, var20, var18, var19, var20, var18, var19, var17, var18, color);
      this.handleAction5(buffer, var13, var19, var17, var21, var19, var20, var21, var16, var20, var21, var16, var17, var21, color);
      this.handleAction5(buffer, var13, var16, var17, var21, var16, var20, var21, var16, var20, var18, var16, var17, var18, color);
      this.handleAction5(buffer, var13, var19, var17, var18, var19, var20, var18, var19, var20, var21, var19, var17, var21, color);
      baseStack.pop();
   }

   private void handleAction3(
      MatrixStack baseStack,
      BufferBuilder buffer,
      ModelPart root,
      ModelPart part,
      float offX,
      float offY,
      float offZ,
      float width,
      float height,
      float depth,
      float expand,
      int color
   ) {
      baseStack.push();
      root.rotate(baseStack);
      part.rotate(baseStack);
      Matrix4f var13 = baseStack.peek().getPositionMatrix();
      float var14 = 0.0625F;
      float var15 = expand * var14;
      float var16 = offX * var14 - var15;
      float var17 = offY * var14 - var15;
      float var18 = offZ * var14 - var15;
      float var19 = (offX + width) * var14 + var15;
      float var20 = (offY + height) * var14 + var15;
      float var21 = (offZ + depth) * var14 + var15;
      this.handleAction4(buffer, var13, var16, var20, var18, var16, var20, var21, var19, var20, var21, var19, var20, var18, color);
      this.handleAction4(buffer, var13, var16, var17, var21, var16, var17, var18, var19, var17, var18, var19, var17, var21, color);
      this.handleAction4(buffer, var13, var16, var17, var18, var16, var20, var18, var19, var20, var18, var19, var17, var18, color);
      this.handleAction4(buffer, var13, var19, var17, var21, var19, var20, var21, var16, var20, var21, var16, var17, var21, color);
      this.handleAction4(buffer, var13, var16, var17, var21, var16, var20, var21, var16, var20, var18, var16, var17, var18, color);
      this.handleAction4(buffer, var13, var19, var17, var18, var19, var20, var18, var19, var20, var21, var19, var17, var21, color);
      baseStack.pop();
   }

   private void handleAction4(
      BufferBuilder buffer,
      Matrix4f matrix,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float x4,
      float y4,
      float z4,
      int color
   ) {
      int var16 = ColorUtils.red(color);
      int var17 = ColorUtils.green(color);
      int var18 = ColorUtils.blue(color);
      int var19 = ColorUtils.alpha(color);
      buffer.vertex(matrix, x1, y1, z1).texture(this.calcX(x1, y1, z1), this.calcX2(x1, y1, z1)).color(var16, var17, var18, var19);
      buffer.vertex(matrix, x2, y2, z2).texture(this.calcX(x2, y2, z2), this.calcX2(x2, y2, z2)).color(var16, var17, var18, var19);
      buffer.vertex(matrix, x3, y3, z3).texture(this.calcX(x3, y3, z3), this.calcX2(x3, y3, z3)).color(var16, var17, var18, var19);
      buffer.vertex(matrix, x4, y4, z4).texture(this.calcX(x4, y4, z4), this.calcX2(x4, y4, z4)).color(var16, var17, var18, var19);
   }

   private void handleAction5(
      BufferBuilder buffer,
      Matrix4f matrix,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float x4,
      float y4,
      float z4,
      int color
   ) {
      int var16 = ColorUtils.red(color);
      int var17 = ColorUtils.green(color);
      int var18 = ColorUtils.blue(color);
      int var19 = ColorUtils.alpha(color);
      buffer.vertex(matrix, x1, y1, z1).color(var16, var17, var18, var19);
      buffer.vertex(matrix, x2, y2, z2).color(var16, var17, var18, var19);
      buffer.vertex(matrix, x3, y3, z3).color(var16, var17, var18, var19);
      buffer.vertex(matrix, x4, y4, z4).color(var16, var17, var18, var19);
   }

   private float calcX(float x, float y, float z) {
      return x * 1.15F + z * 0.72F;
   }

   private float calcX2(float x, float y, float z) {
      return y * 1.05F - z * 0.38F + x * 0.18F;
   }

   private void handleShader(ShaderProgram shader, String name, float value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   public boolean affects(PlayerEntity player) {
      if (!this.isActive() || player == null || !player.isAlive()) {
         return false;
      } else if (player == mc.player) {
         return false;
      } else {
         return this.checkPlayer(player) ? true : true;
      }
   }

   public boolean shouldHideBaseModel(PlayerEntity player) {
      return this.affects(player);
   }

   public boolean shouldHideItemsAndCape(PlayerEntity player) {
      return false;
   }

   public int resolveFillColor(PlayerEntity player) {
      return this.checkPlayer(player) ? count2 : this.calcColor(ColorUtils.getThemeColor(), 1.18F, 1.12F, 130);
   }

   private int calcColor(int color, float saturationBoost, float brightnessBoost, int alpha) {
      float[] var5 = Color.RGBtoHSB(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), null);
      float var6 = MathHelper.clamp(var5[1] * saturationBoost, 0.0F, 1.0F);
      float var7 = MathHelper.clamp(Math.max(var5[2], 0.8F) * brightnessBoost, 0.0F, 1.0F);
      int var8 = Color.HSBtoRGB(var5[0], var6, var7);
      return ColorUtils.rgba(ColorUtils.red(var8), ColorUtils.green(var8), ColorUtils.blue(var8), alpha);
   }

   private boolean checkPlayer(PlayerEntity player) {
      return Lumen.INSTANCE != null && Lumen.INSTANCE.friendStorage != null && Lumen.INSTANCE.friendStorage.isFriend(player.getName().getString());
   }
}
