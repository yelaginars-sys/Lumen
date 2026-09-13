package dlc.lumen.mixin;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.bots.core.BotViewRenderer;
import dlc.lumen.client.modules.impl.render.Optimizer;
import dlc.lumen.client.modules.impl.render.Removals;
import dlc.lumen.client.modules.impl.render.ShaderEsp;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements QClient {
   @Inject(method = "renderParticles", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderParticles(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, Fog fog, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Частицы")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesParticles()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderWeather(FrameGraphBuilder frameGraphBuilder, Vec3d pos, float tickDelta, Fog fog, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Погода")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesWeather()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "addWeatherParticlesAndSound", at = @At("HEAD"), cancellable = true)
   private void wonderful$addWeatherParticlesAndSound(Camera camera, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Погода")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesWeather()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderClouds(
      FrameGraphBuilder frameGraphBuilder,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CloudRenderMode renderMode,
      Vec3d cameraPos,
      float ticks,
      int color,
      float cloudHeight,
      CallbackInfo ci
   ) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Облака")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesClouds()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "renderBlockEntities", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderBlockEntities(
      MatrixStack matrices, Immediate mainConsumers, Immediate translucentConsumers, Camera camera, float tickDelta, CallbackInfo ci
   ) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Блок-сущности")) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void render(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      if (!BotViewRenderer.inBotPip) {
         ShaderEsp.INSTANCE.setOutlineFramebuffer(((WorldRendererAccessor)this).lumen$getEntityOutlineFramebufferRaw());
         boolean has3DListeners = EventInvoker.hasListeners(Event3DRender.class);
         if (has3DListeners) {
            Profilers.get().swap("wonderful_renderWorld");
            MatrixStack matrices = new MatrixStack();
            matrices.multiplyPositionMatrix(positionMatrix);
            if (has3DListeners) {
               new Event3DRender(matrices, positionMatrix, projectionMatrix, camera, tickCounter.getTickDelta(false)).call();
            }
         }
      }
   }

   @Inject(method = "drawEntityOutlinesFramebuffer", at = @At("HEAD"), cancellable = true)
   private void wonderful$drawEntityOutlinesFramebuffer(CallbackInfo ci) {
      ShaderEsp esp = ShaderEsp.INSTANCE;
      if (esp != null && esp.isEnable() && esp.hasTargets()) {
         ci.cancel();
      }
   }

   @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
   public void onDrawBlockOutline(CallbackInfo ci) {
      if (ModuleClass.blockOverlay.isEnable()) {
         ci.cancel();
      }
   }
}
