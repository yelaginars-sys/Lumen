package dlc.lumen.client.bots.core;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public final class BotViewRenderer implements QClient {
   public static final BotViewRenderer INSTANCE = new BotViewRenderer();
   public static volatile boolean inBotPip;
   private static final int INDEX = 6;
   private static final int INDEX2 = 120;
   private static final float VOLUME = 70.0F;
   private static final int INDEX3 = 15728880;
   private final Camera camera = new Camera();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Matrix4f matrix4f2 = new Matrix4f();
   private final Quaternionf quaternionf = new Quaternionf();
   private final Map<BotSession, BotViewRenderer.View> linkedHashMap = new LinkedHashMap<>();
   private HeldItemRenderer heldItemRenderer;
   private boolean flag;

   private BotViewRenderer() {
   }

   public boolean hasFrame(BotSession bot) {
      BotViewRenderer.View var2 = this.linkedHashMap.get(bot);
      return var2 != null && var2.fbo != null;
   }

   public int texture(BotSession bot) {
      BotViewRenderer.View var2 = this.linkedHashMap.get(bot);
      return var2 != null && var2.fbo != null ? var2.fbo.getColorAttachment() : -1;
   }

   public void render(BotSession bot, int width, int height) {
      if (!inBotPip && bot != null && bot.canRenderView()) {
         ClientWorld var4 = bot.visualWorld();
         ClientPlayerEntity var5 = bot.visualPlayer();
         Entity var6 = bot.visualCamera();
         ClientPlayerInteractionManager var7 = bot.visualInteraction();
         if (var4 != null && var6 != null) {
            width = Math.max(1, width);
            height = Math.max(1, height);
            BotViewRenderer.View var8 = this.computeView(bot, var4, width, height);
            if (var8 != null && var8.renderer != null && var8.fbo != null) {
               var8.idleFrames = 0;
               ClientWorld var9 = mc.world;
               ClientPlayerEntity var10 = mc.player;
               ClientPlayerInteractionManager var11 = mc.interactionManager;
               Entity var12 = mc.getCameraEntity();
               Framebuffer var13 = mc.getFramebuffer();
               IMinecraftClientAccessor var14 = (IMinecraftClientAccessor)mc;
               inBotPip = true;
               BotSession var15 = BotContext.enter(bot);
               RenderSystem.backupProjectionMatrix();

               try {
                  mc.world = var4;
                  mc.player = var5;
                  mc.interactionManager = var7;
                  mc.setCameraEntity(var6);
                  var14.lumen$setFramebuffer(var8.fbo);
                  float var16 = mc.getRenderTickCounter().getTickDelta(false);
                  this.camera.update(var4, var6, false, false, var16);

                  for (int var17 = 0; var17 < 16; var17++) {
                     this.camera.updateEyeHeight();
                  }

                  this.camera.update(var4, var6, false, false, var16);
                  var8.renderer.tick();
                  float var26 = (float)width / height;
                  float var18 = Math.max(mc.gameRenderer.getFarPlaneDistance(), 16.0F);
                  this.matrix4f.identity().perspective(1.2217305F, var26, 0.05F, var18);
                  this.matrix4f2.rotation(this.camera.getRotation().conjugate(this.quaternionf));
                  RenderSystem.setProjectionMatrix(this.matrix4f, ProjectionType.PERSPECTIVE);
                  var8.fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  var8.fbo.clear();
                  var8.fbo.beginWrite(true);
                  var8.renderer.setupFrustum(this.camera.getPos(), this.matrix4f2, this.matrix4f);
                  var8.renderer
                     .render(
                        ObjectAllocator.TRIVIAL,
                        mc.getRenderTickCounter(),
                        false,
                        this.camera,
                        mc.gameRenderer,
                        this.matrix4f2,
                        this.matrix4f
                     );
                  this.updateState(var5, var16);
               } catch (Throwable var22) {
                  if (!this.flag) {
                     this.flag = true;
                     System.err.println("[BotViewRenderer] render failed:");
                     var22.printStackTrace();
                  }
               } finally {
                  BotContext.exit(var15);
                  RenderSystem.restoreProjectionMatrix();
                  mc.world = var9;
                  mc.player = var10;
                  mc.interactionManager = var11;
                  mc.setCameraEntity(var12);
                  var14.lumen$setFramebuffer(var13);
                  var13.beginWrite(true);
                  inBotPip = false;
               }
            }
         }
      }
   }

   public void renderScreenOverlay(BotSession bot, DrawContext context, float x, float y, float width, float height) {
      if (bot != null) {
         Screen var7 = bot.visualScreen();
         if (var7 != null) {
            bot.runWithVisualScreen(() -> this.updateState2(var7, context, x, y, width, height));
         }
      }
   }

   public void endFrame() {
      ArrayList<BotSession> var1 = new ArrayList<>();

      for (Entry var3 : this.linkedHashMap.entrySet()) {
         if (++((BotViewRenderer.View)var3.getValue()).idleFrames > 120) {
            var1.add((BotSession)var3.getKey());
         }
      }

      for (BotSession var5 : var1) {
         this.free(var5);
      }
   }

   public void free(BotSession bot) {
      BotViewRenderer.View var2 = this.linkedHashMap.remove(bot);
      if (var2 != null) {
         var2.free();
      }
   }

   public void freeAll() {
      for (BotViewRenderer.View var2 : this.linkedHashMap.values()) {
         var2.free();
      }

      this.linkedHashMap.clear();
   }

   private BotViewRenderer.View computeView(BotSession bot, ClientWorld world, int width, int height) {
      BotViewRenderer.View var5 = this.linkedHashMap.get(bot);
      if (var5 == null) {
         if (this.linkedHashMap.size() >= 6) {
            BotSession var6 = null;
            int var7 = -1;

            for (Entry var9 : this.linkedHashMap.entrySet()) {
               if (((BotViewRenderer.View)var9.getValue()).idleFrames > var7) {
                  var7 = ((BotViewRenderer.View)var9.getValue()).idleFrames;
                  var6 = (BotSession)var9.getKey();
               }
            }

            if (var6 == null) {
               return null;
            }

            this.free(var6);
         }

         var5 = new BotViewRenderer.View();
         this.linkedHashMap.put(bot, var5);
      }

      boolean var10 = var5.fbo == null || var5.fbo.textureWidth != width || var5.fbo.textureHeight != height;
      if (var10) {
         if (var5.fbo != null) {
            var5.fbo.delete();
         }

         var5.fbo = new SimpleFramebuffer(width, height, true);
      }

      boolean var11 = var5.world != world || var5.renderer == null;
      if (var5.renderer == null) {
         var5.renderer = new WorldRenderer(
            mc, mc.getEntityRenderDispatcher(), mc.getBlockEntityRenderDispatcher(), new BufferBuilderStorage(Runtime.getRuntime().availableProcessors())
         );
      }

      if (var11) {
         var5.renderer.setWorld(world);
         var5.renderer.reload();
         var5.world = world;
      }

      if (var10 || var11) {
         var5.renderer.onResized(width, height);
      }

      return var5;
   }

   private void updateState(ClientPlayerEntity player, float tickDelta) {
      if (player != null) {
         if (this.heldItemRenderer == null) {
            this.heldItemRenderer = new HeldItemRenderer(mc, mc.getEntityRenderDispatcher(), mc.getItemRenderer(), mc.getItemModelManager());
         }

         this.heldItemRenderer.updateHeldItems();
         MatrixStack var3 = new MatrixStack();
         var3.multiplyPositionMatrix(new Matrix4f(this.matrix4f2).invert());
         Matrix4fStack var4 = RenderSystem.getModelViewStack();
         var4.pushMatrix().mul(this.matrix4f2);
         LightmapTextureManager var5 = mc.gameRenderer.getLightmapTextureManager();
         var5.enable();
         Immediate var6 = mc.getBufferBuilders().getEntityVertexConsumers();
         this.heldItemRenderer.renderItem(tickDelta, var3, var6, player, 15728880);
         var6.draw();
         var5.disable();
         var4.popMatrix();
      }
   }

   private void updateState2(Screen screen, DrawContext context, float x, float y, float width, float height) {
      float var7 = mc.getWindow().getScaledWidth();
      float var8 = mc.getWindow().getScaledHeight();
      float var9 = Math.min(width / var7, height / var8);
      float var10 = x + (width - var7 * var9) * 0.5F;
      float var11 = y + (height - var8 * var9) * 0.5F;
      context.enableScissor((int)x, (int)y, (int)(x + width), (int)(y + height));
      MatrixStack var12 = context.getMatrices();
      var12.push();
      var12.translate(var10, var11, 0.0F);
      var12.scale(var9, var9, 1.0F);

      try {
         screen.render(context, -10000, -10000, mc.getRenderTickCounter().getTickDelta(false));
      } finally {
         var12.pop();
         context.disableScissor();
      }
   }

   private static final class View {
      SimpleFramebuffer fbo;
      WorldRenderer renderer;
      ClientWorld world;
      int idleFrames;

      void free() {
         if (this.renderer != null) {
            try {
               this.renderer.setWorld(null);
               this.renderer.close();
            } catch (Throwable var2) {
            }

            this.renderer = null;
         }

         if (this.fbo != null) {
            this.fbo.delete();
            this.fbo = null;
         }

         this.world = null;
      }
   }
}
