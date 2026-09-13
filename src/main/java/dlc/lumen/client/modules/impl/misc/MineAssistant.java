package dlc.lumen.client.modules.impl.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.server.ServerUtil;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class MineAssistant extends Module {
   public static MineAssistant INSTANCE = new MineAssistant();
   private final BooleanSetting booleanSetting = new BooleanSetting("Алмазная", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Редстоуновая", false);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Железная", false);
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Лазуритовая", false);
   private final BooleanSetting booleanSetting5 = new BooleanSetting("Золотая", true);
   private final BooleanSetting booleanSetting6 = new BooleanSetting("Древние", true);
   private final BooleanSetting booleanSetting7 = new BooleanSetting("Угольная", false);
   private final List<MineAssistant.OreInfo> mineAssistants = Arrays.asList(
      new MineAssistant.OreInfo(Blocks.DIAMOND_ORE, Color.CYAN.getRGB(), "Алмазная"),
      new MineAssistant.OreInfo(Blocks.DEEPSLATE_DIAMOND_ORE, Color.CYAN.getRGB(), "Алмазная"),
      new MineAssistant.OreInfo(Blocks.REDSTONE_ORE, Color.RED.getRGB(), "Редстоуновая"),
      new MineAssistant.OreInfo(Blocks.DEEPSLATE_REDSTONE_ORE, Color.RED.getRGB(), "Редстоуновая"),
      new MineAssistant.OreInfo(Blocks.IRON_ORE, Color.LIGHT_GRAY.getRGB(), "Железная"),
      new MineAssistant.OreInfo(Blocks.DEEPSLATE_IRON_ORE, Color.LIGHT_GRAY.getRGB(), "Железная"),
      new MineAssistant.OreInfo(Blocks.LAPIS_ORE, Color.BLUE.getRGB(), "Лазуритовая"),
      new MineAssistant.OreInfo(Blocks.DEEPSLATE_LAPIS_ORE, Color.BLUE.getRGB(), "Лазуритовая"),
      new MineAssistant.OreInfo(Blocks.GOLD_ORE, Color.YELLOW.getRGB(), "Золотая"),
      new MineAssistant.OreInfo(Blocks.DEEPSLATE_GOLD_ORE, Color.YELLOW.getRGB(), "Золотая"),
      new MineAssistant.OreInfo(Blocks.ANCIENT_DEBRIS, new Color(148, 51, 0).getRGB(), "Древние"),
      new MineAssistant.OreInfo(Blocks.COAL_ORE, Color.DARK_GRAY.getRGB(), "Угольная"),
      new MineAssistant.OreInfo(Blocks.DEEPSLATE_COAL_ORE, Color.DARK_GRAY.getRGB(), "Угольная")
   );
   private Box box;
   private final Map<BlockPos, Integer> blockPoss = new HashMap<>();
   private long timestamp;

   public MineAssistant() {
      super("MineAssistant", "Помощник, упрощающий добычу ресурсов в шахте под FunTime/SpookyTime", Module.ModuleCategory.MISC);
      this.addSettings(
         this.booleanSetting,
         this.booleanSetting2,
         this.booleanSetting3,
         this.booleanSetting4,
         this.booleanSetting5,
         this.booleanSetting6,
         this.booleanSetting7
      );
   }

   @EventLink
   public void onTick(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (!ServerUtil.isFuntime() && !ServerUtil.isSpookyTime()) {
            this.box = null;
            this.blockPoss.clear();
         } else {
            this.helper();
         }
      }
   }

   private void helper() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.timestamp >= 500L) {
         this.timestamp = var1;

         for (ArmorStandEntity var4 : mc.world.getEntitiesByClass(ArmorStandEntity.class, mc.player.getBoundingBox().expand(256.0), e -> true)) {
            if (var4.getName().getString().contains("Авто-Шахта")) {
               if (this.box == null || this.box.getAverageSideLength() <= 15.0) {
                  int var5 = (int)Math.floor(var4.getY()) - 2;
                  int var6 = (int)Math.floor(var4.getX());
                  int var7 = (int)Math.floor(var4.getZ());
                  int var8 = var5 - 8;
                  int var9 = var5 + 1;
                  int var10 = var6;
                  int var11 = var6;
                  int var12 = var7;
                  int var13 = var7;

                  while (this.helper5(var10 - 1, var7, var5, var5 + 1)) {
                     var10--;
                  }

                  while (this.helper5(var11 + 1, var7, var5, var5 + 1)) {
                     var11++;
                  }

                  while (this.helper5(var6, var12 - 1, var5, var5 + 1)) {
                     var12--;
                  }

                  while (this.helper5(var6, var13 + 1, var5, var5 + 1)) {
                     var13++;
                  }

                  Box var14 = new Box(var10, var8, var12, var11 + 1, var9 + 1, var13 + 1);
                  if (!var14.equals(this.box)) {
                     this.box = var14;
                     this.blockPoss.clear();
                  }
               }

               return;
            }
         }

         this.box = null;
         this.blockPoss.clear();
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (this.box != null && mc.world != null) {
         if (this.blockPoss.isEmpty() || mc.player.age % 20 == 0) {
            this.helper2();
         }

         if (!this.blockPoss.isEmpty()) {
            Vec3d var2 = event.getCamera().getPos();
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

            for (Entry var8 : this.blockPoss.entrySet()) {
               helper6(var6, var4, (BlockPos)var8.getKey(), (Integer)var8.getValue(), 0.25F);
            }

            BufferRenderer.drawWithGlobalProgram(var6.end());
            RenderSystem.lineWidth(1.0F);
            BufferBuilder var10 = var5.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            for (Entry var9 : this.blockPoss.entrySet()) {
               helper7(var10, var4, (BlockPos)var9.getKey(), (Integer)var9.getValue(), 0.8F);
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

   private void helper2() {
      this.blockPoss.clear();

      for (int var1 = (int)this.box.minX; var1 <= (int)this.box.maxX; var1++) {
         for (int var2 = (int)this.box.minY; var2 <= (int)this.box.maxY; var2++) {
            for (int var3 = (int)this.box.minZ; var3 <= (int)this.box.maxZ; var3++) {
               BlockPos var4 = new BlockPos(var1, var2, var3);
               MineAssistant.OreInfo var5 = this.helper4(mc.world.getBlockState(var4).getBlock());
               if (var5 != null && var5.b != -1 && this.helper3(var5.c)) {
                  this.blockPoss.put(var4, var5.b);
               }
            }
         }
      }
   }

   private boolean helper3(String name) {
      switch (name) {
         case "Алмазная":
            return this.booleanSetting.getValue();
         case "Редстоуновая":
            return this.booleanSetting2.getValue();
         case "Железная":
            return this.booleanSetting3.getValue();
         case "Лазуритовая":
            return this.booleanSetting4.getValue();
         case "Золотая":
            return this.booleanSetting5.getValue();
         case "Древние":
            return this.booleanSetting6.getValue();
         default:
            return this.booleanSetting7.getValue();
      }
   }

   private MineAssistant.OreInfo helper4(Block block) {
      for (MineAssistant.OreInfo var3 : this.mineAssistants) {
         if (var3.a == block) {
            return var3;
         }
      }

      return null;
   }

   private boolean helper5(int x, int z, int yMin, int yMax) {
      for (int var5 = yMin; var5 <= yMax; var5++) {
         if (mc.world.getBlockState(new BlockPos(x, var5, z)).isAir()) {
            return true;
         }
      }

      return false;
   }

   private static void helper6(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, int rgb, float a) {
      float var5 = (rgb >> 16 & 0xFF) / 255.0F;
      float var6 = (rgb >> 8 & 0xFF) / 255.0F;
      float var7 = (rgb & 0xFF) / 255.0F;
      float var8 = pos.getX();
      float var9 = pos.getY();
      float var10 = pos.getZ();
      float var11 = var8 + 1.0F;
      float var12 = var9 + 1.0F;
      float var13 = var10 + 1.0F;
      buffer.vertex(matrix, var8, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var13).color(var5, var6, var7, a);
   }

   private static void helper7(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, int rgb, float a) {
      float var5 = (rgb >> 16 & 0xFF) / 255.0F;
      float var6 = (rgb >> 8 & 0xFF) / 255.0F;
      float var7 = (rgb & 0xFF) / 255.0F;
      float var8 = pos.getX();
      float var9 = pos.getY();
      float var10 = pos.getZ();
      float var11 = var8 + 1.0F;
      float var12 = var9 + 1.0F;
      float var13 = var10 + 1.0F;
      buffer.vertex(matrix, var8, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var10).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var11, var12, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var9, var13).color(var5, var6, var7, a);
      buffer.vertex(matrix, var8, var12, var13).color(var5, var6, var7, a);
   }

   private static final class OreInfo {
      final Block a;
      final int b;
      final String c;

      OreInfo(Block block, int color, String name) {
         this.a = block;
         this.b = color;
         this.c = name;
      }
   }
}
