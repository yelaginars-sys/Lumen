package dlc.lumen.mixin;

import dlc.lumen.client.modules.impl.render.ChunkAnimator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.ScopedProfiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public class WorldRendererChunkAnimatorMixin {
   @Inject(method = "addBuiltChunk", at = @At("HEAD"))
   private void lumen$chunkBuilt(BuiltChunk builtChunk, CallbackInfo ci) {
      ChunkAnimator.INSTANCE.onChunkBuilt(builtChunk.getSectionPos());
   }

   @Inject(
      method = "renderLayer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;bind()V"),
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void lumen$chunkLayer(
      RenderLayer layer,
      double camX,
      double camY,
      double camZ,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci,
      ScopedProfiler profiler,
      boolean translucent,
      ObjectListIterator<?> iterator,
      ShaderProgram shader,
      GlUniform modelOffset,
      BuiltChunk builtChunk,
      VertexBuffer buffer
   ) {
      if (modelOffset != null) {
         double y = ChunkAnimator.INSTANCE.getOffsetY(builtChunk.getSectionPos());
         if (y != 0.0) {
            long sectionPos = builtChunk.getSectionPos();
            modelOffset.set(
               (float)(ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(sectionPos)) - camX),
               (float)(ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(sectionPos)) - camY) + (float)y,
               (float)(ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(sectionPos)) - camZ)
            );
            modelOffset.upload();
         }
      }
   }
}
