package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

public class BlockESP extends Module {
   public static BlockESP INSTANCE = new BlockESP();
   private static final float VOLUME = 2.0F;
   private static final float VOLUME2 = 0.18F;
   private static final float VOLUME3 = 0.1F;
   private static final float VOLUME4 = 1.0F;
   private static final float VOLUME5 = 0.15F;
   private static final long TIMESTAMP = 50L;
   private static final int INDEX = 2;
   private final FloatSetting floatSetting = new FloatSetting("Дистанция", 60.0F, 10.0F, 120.0F, 1.0F);
   private final Set<String> trackedBlocks = ConcurrentHashMap.newKeySet();
   private final Map<BlockPos, String> blockPoss = new ConcurrentHashMap<>();
   private final Set<ChunkPos> set2 = ConcurrentHashMap.newKeySet();
   private ChunkPos chunkPos2;
   private int index = -1;
   private long timestamp;

   public BlockESP() {
      super("BlockESP", "Показывает выбранные блоки через стену", Module.ModuleCategory.RENDER);
      this.addSettings(this.floatSetting);
   }

   @Override
   public void onEnable() {
      this.helper9();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.helper9();
      super.onDisable();
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      if (mc.world != null && mc.player != null && !this.trackedBlocks.isEmpty()) {
         int var2 = this.helper7();
         ChunkPos var3 = new ChunkPos(mc.player.getBlockPos());
         if (var2 != this.index) {
            this.helper9();
            this.index = var2;
         }

         if (this.chunkPos2 == null || !this.chunkPos2.equals(var3)) {
            this.set2.clear();
            this.chunkPos2 = var3;
         }

         long var4 = System.currentTimeMillis();
         if (var4 - this.timestamp >= 50L) {
            this.helper(var2);
            this.timestamp = var4;
         }

         this.helper3(mc.player.getPos(), var2);
         this.helper4(event.getMatrices());
      }
   }

   private void helper(int scanRadius) {
      if (mc.world != null && mc.player != null) {
         BlockPos var2 = mc.player.getBlockPos();
         int var3 = var2.getX() >> 4;
         int var4 = var2.getZ() >> 4;
         int var5 = (scanRadius >> 4) + 2;
         ArrayList<ChunkPos> var6 = new ArrayList<>();

         for (int var7 = -var5; var7 <= var5; var7++) {
            for (int var8 = -var5; var8 <= var5; var8++) {
               ChunkPos var9 = new ChunkPos(var3 + var7, var4 + var8);
               if (!this.set2.contains(var9)) {
                  var6.add(var9);
               }
            }
         }

         var6.sort((a, b) -> {
            long var5x = this.helper8(a, var3, var4);
            long var7x = this.helper8(b, var3, var4);
            return Long.compare(var5x, var7x);
         });
         int var11 = 0;

         for (ChunkPos var13 : var6) {
            if (var11 >= 2) {
               break;
            }

            WorldChunk var10 = mc.world.getChunk(var13.x, var13.z);
            if (var10 != null) {
               this.helper2(var10, var2, scanRadius);
               this.set2.add(var13);
               var11++;
            }
         }
      }
   }

   private void helper2(WorldChunk chunk, BlockPos playerPos, int scanRadius) {
      int var4 = chunk.getPos().getStartX();
      int var5 = chunk.getPos().getStartZ();
      int var6 = var4 + 15;
      int var7 = var5 + 15;
      int var8 = Math.max(mc.world.getBottomY(), playerPos.getY() - scanRadius);
      int var9 = Math.min(mc.world.getTopYInclusive(), playerPos.getY() + scanRadius);
      int var10 = scanRadius * scanRadius;
      Mutable var11 = new Mutable();

      for (int var12 = var4; var12 <= var6; var12++) {
         for (int var13 = var5; var13 <= var7; var13++) {
            for (int var14 = var8; var14 <= var9; var14++) {
               var11.set(var12, var14, var13);
               if (!(var11.getSquaredDistance(playerPos) > var10)) {
                  BlockState var15 = chunk.getBlockState(var11);
                  if (!var15.isAir()) {
                     String var16 = Registries.BLOCK.getId(var15.getBlock()).getPath().toLowerCase();
                     if (this.trackedBlocks.contains(var16)) {
                        this.blockPoss.put(var11.toImmutable(), var16);
                     }
                  }
               }
            }
         }
      }
   }

   private void helper3(Vec3d playerPos, int renderDistance) {
      if (mc.world == null) {
         this.blockPoss.clear();
      } else {
         int var3 = renderDistance * renderDistance;
         this.blockPoss.entrySet().removeIf(entry -> {
            BlockPos var4 = entry.getKey();
            BlockState var5 = mc.world.getBlockState(var4);
            if (var5.isAir()) {
               return true;
            }

            String var6 = Registries.BLOCK.getId(var5.getBlock()).getPath().toLowerCase();
            return !this.trackedBlocks.contains(var6) ? true : var4.getSquaredDistance(playerPos) > var3;
         });
      }
   }

   private void helper4(MatrixStack matrices) {
      if (!this.blockPoss.isEmpty()) {
         Vec3d var2 = mc.gameRenderer.getCamera().getPos();
         matrices.push();
         matrices.translate(-var2.x, -var2.y, -var2.z);
         Matrix4f var3 = matrices.peek().getPositionMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         Tessellator var4 = Tessellator.getInstance();
         BufferBuilder var5 = var4.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

         for (BlockPos var7 : this.blockPoss.keySet()) {
            this.helper5(var5, var3, var7, 0.1F, 1.0F, 0.15F, 0.18F);
         }

         BufferRenderer.drawWithGlobalProgram(var5.end());
         RenderSystem.lineWidth(2.0F);
         BufferBuilder var9 = var4.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

         for (BlockPos var8 : this.blockPoss.keySet()) {
            this.helper6(var9, var3, var8, 0.1F, 1.0F, 0.15F, 1.0F);
         }

         BufferRenderer.drawWithGlobalProgram(var9.end());
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         matrices.pop();
      }
   }

   private void helper5(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
      float var8 = pos.getX();
      float var9 = pos.getY();
      float var10 = pos.getZ();
      float var11 = var8 + 1.0F;
      float var12 = var9 + 1.0F;
      float var13 = var10 + 1.0F;
      buffer.vertex(matrix, var8, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var13).color(r, g, b, a);
   }

   private void helper6(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
      float var8 = pos.getX();
      float var9 = pos.getY();
      float var10 = pos.getZ();
      float var11 = var8 + 1.0F;
      float var12 = var9 + 1.0F;
      float var13 = var10 + 1.0F;
      buffer.vertex(matrix, var8, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var10).color(r, g, b, a);
      buffer.vertex(matrix, var11, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var11, var12, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var9, var13).color(r, g, b, a);
      buffer.vertex(matrix, var8, var12, var13).color(r, g, b, a);
   }

   public void addBlock(String blockName) {
      this.trackedBlocks.add(blockName.toLowerCase());
      this.set2.clear();
      this.blockPoss.clear();
   }

   public void removeBlock(String blockName) {
      this.trackedBlocks.remove(blockName.toLowerCase());
      this.blockPoss.entrySet().removeIf(entry -> entry.getValue().equalsIgnoreCase(blockName));
   }

   public void clearBlocks() {
      this.trackedBlocks.clear();
      this.helper9();
   }

   public Set<String> getTrackedBlocks() {
      return new HashSet<>(this.trackedBlocks);
   }

   public boolean isTracking(String blockName) {
      return this.trackedBlocks.contains(blockName.toLowerCase());
   }

   private int helper7() {
      return Math.round(this.floatSetting.get());
   }

   private long helper8(ChunkPos chunkPos, int playerChunkX, int playerChunkZ) {
      long var4 = chunkPos.x - playerChunkX;
      long var6 = chunkPos.z - playerChunkZ;
      return var4 * var4 + var6 * var6;
   }

   private void helper9() {
      this.blockPoss.clear();
      this.set2.clear();
      this.chunkPos2 = null;
      this.timestamp = 0L;
      this.index = -1;
   }
}
