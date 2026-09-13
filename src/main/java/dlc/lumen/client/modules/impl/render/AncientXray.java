package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.client.modules.Module;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class AncientXray extends Module {
   public static AncientXray INSTANCE = new AncientXray();
   private final Set<BlockPos> set = ConcurrentHashMap.newKeySet();

   public Set<BlockPos> snapshot() {
      return Set.copyOf(this.set);
   }

   public AncientXray() {
      super("AncientXray", "Подсвечивает древние обломки, вскрытые взрывом TNT", Module.ModuleCategory.RENDER);
   }

   @Override
   public void onDisable() {
      this.set.clear();
      super.onDisable();
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE) {
         Packet var2 = event.getPacket();
         if (var2 instanceof ChunkDeltaUpdateS2CPacket var3) {
            var3.visitUpdates((pos, state) -> {
               if (state.getBlock() == Blocks.ANCIENT_DEBRIS) {
                  this.set.add(pos.toImmutable());
               }
            });
         } else if (var2 instanceof BlockUpdateS2CPacket var4) {
            if (var4.getState().getBlock() == Blocks.ANCIENT_DEBRIS) {
               this.set.add(var4.getPos().toImmutable());
            } else {
               this.set.remove(var4.getPos());
            }
         }
      }
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      if (mc.world != null && mc.player != null && !this.set.isEmpty()) {
         this.set.removeIf(pos -> mc.world.getBlockState(pos).getBlock() != Blocks.ANCIENT_DEBRIS);
         if (!this.set.isEmpty()) {
            Vec3d var2 = mc.gameRenderer.getCamera().getPos();
            MatrixStack var3 = event.getMatrices();
            var3.push();
            var3.translate(-var2.x, -var2.y, -var2.z);
            Matrix4f var4 = var3.peek().getPositionMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            Tessellator var5 = Tessellator.getInstance();
            BufferBuilder var6 = var5.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            for (BlockPos var8 : this.set) {
               this.helper(var6, var4, var8, 1.0F, 0.1F, 0.1F, 0.22F);
            }

            BufferRenderer.drawWithGlobalProgram(var6.end());
            RenderSystem.lineWidth(2.0F);
            BufferBuilder var10 = var5.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            for (BlockPos var9 : this.set) {
               this.helper2(var10, var4, var9, 1.0F, 0.1F, 0.1F, 1.0F);
            }

            BufferRenderer.drawWithGlobalProgram(var10.end());
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            var3.pop();
         }
      }
   }

   private void helper(BufferBuilder b, Matrix4f m, BlockPos pos, float r, float g, float bl, float a) {
      float var8 = pos.getX();
      float var9 = pos.getY();
      float var10 = pos.getZ();
      float var11 = var8 + 1.0F;
      float var12 = var9 + 1.0F;
      float var13 = var10 + 1.0F;
      b.vertex(m, var8, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var10).color(r, g, bl, a);
      b.vertex(m, var8, var12, var13).color(r, g, bl, a);
      b.vertex(m, var11, var12, var13).color(r, g, bl, a);
      b.vertex(m, var11, var12, var10).color(r, g, bl, a);
      b.vertex(m, var8, var9, var10).color(r, g, bl, a);
      b.vertex(m, var8, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var10).color(r, g, bl, a);
      b.vertex(m, var8, var9, var13).color(r, g, bl, a);
      b.vertex(m, var11, var9, var13).color(r, g, bl, a);
      b.vertex(m, var11, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var9, var10).color(r, g, bl, a);
      b.vertex(m, var8, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var13).color(r, g, bl, a);
      b.vertex(m, var11, var9, var13).color(r, g, bl, a);
   }

   private void helper2(BufferBuilder b, Matrix4f m, BlockPos pos, float r, float g, float bl, float a) {
      float var8 = pos.getX();
      float var9 = pos.getY();
      float var10 = pos.getZ();
      float var11 = var8 + 1.0F;
      float var12 = var9 + 1.0F;
      float var13 = var10 + 1.0F;
      b.vertex(m, var8, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var13).color(r, g, bl, a);
      b.vertex(m, var11, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var9, var10).color(r, g, bl, a);
      b.vertex(m, var8, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var13).color(r, g, bl, a);
      b.vertex(m, var11, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var10).color(r, g, bl, a);
      b.vertex(m, var8, var9, var10).color(r, g, bl, a);
      b.vertex(m, var8, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var10).color(r, g, bl, a);
      b.vertex(m, var11, var12, var10).color(r, g, bl, a);
      b.vertex(m, var11, var9, var13).color(r, g, bl, a);
      b.vertex(m, var11, var12, var13).color(r, g, bl, a);
      b.vertex(m, var8, var9, var13).color(r, g, bl, a);
      b.vertex(m, var8, var12, var13).color(r, g, bl, a);
   }
}
