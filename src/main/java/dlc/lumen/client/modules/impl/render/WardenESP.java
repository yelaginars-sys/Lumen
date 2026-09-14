package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.Module;
import dlc.lumen.mixin.WorldTickerAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WardenESP extends Module implements QClient {
   public static final WardenESP INSTANCE = new WardenESP();
   private static final Pattern TIMER = Pattern.compile("(\\d{2}):(\\d{2})");
   private static final ItemStack CHEST_ICON = new ItemStack(Items.CHEST);
   private final List<WardenESP.TrackedChest> tracked = new ArrayList<>();
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.ZERO;
   private boolean hasProjection;

   public WardenESP() {
      super("WardenESP", "Отображает сундуки в городе варденов с таймером возрождения", Module.ModuleCategory.RENDER);
   }

   @Override
   public void onDisable() {
      this.tracked.clear();
      this.hasProjection = false;
      super.onDisable();
   }

   @EventLink
   public void onTick(EventTickPre event) {
      if (mc.world != null && mc.player != null && "minecraft:overworld".equals(mc.world.getRegistryKey().getValue().toString())) {
         this.updateTimers(this.scanChestsRaw());
      }
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      if (mc.world != null && mc.player != null) {
         this.hasProjection = true;
         this.lastProjectionMatrix.set(event.getProjectionMatrix());
         this.lastCameraRotation.set(event.getCamera().getRotation());
         this.lastCameraPos = event.getCamera().getPos();
         if ("minecraft:overworld".equals(mc.world.getRegistryKey().getValue().toString())) {
            this.drawChests(event.getMatrices(), this.scanChestsRaw());
         }
      }
   }

   @EventLink
   public void onRender2D(EventRender.Default event) {
      if (this.hasProjection && mc.world != null && mc.player != null) {
         if ("minecraft:overworld".equals(mc.world.getRegistryKey().getValue().toString())) {
            this.drawHud(event.getContext(), this.scanChestsRaw());
         }
      }
   }

   public long getRemainingTime(BlockPos pos) {
      if (mc.world != null && mc.player != null && pos != null) {
         for (ArmorStandEntity stand : mc.world.getEntitiesByClass(ArmorStandEntity.class, mc.player.getBoundingBox().expand(256.0), e -> true)) {
            if (stand.getBlockPos().getX() == pos.getX() && stand.getBlockPos().getZ() == pos.getZ()) {
               Matcher matcher = TIMER.matcher(stand.getName().getString());
               if (matcher.find()) {
                  return (Integer.parseInt(matcher.group(1)) * 60L + Integer.parseInt(matcher.group(2))) * 1000L;
               }
            }
         }

         WardenESP.TrackedChest info = this.findTracked(pos);
         return info == null ? -1L : info.getRemainingTime();
      } else {
         return -1L;
      }
   }

   public List<BlockPos> scanChests() {
      return this.scanChestsRaw();
   }

   private List<BlockPos> scanChestsRaw() {
      List<BlockPos> result = new ArrayList<>();
      if (mc.world != null) {
         try {
            for (BlockEntityTickInvoker ticker : ((WorldTickerAccessor)mc.world).lumen$getBlockEntityTickers()) {
               BlockPos pos = ticker.getPos();
               if (!ticker.isRemoved() && pos.getY() >= -60 && pos.getY() <= -35 && pos.getX() >= -2070 && pos.getX() <= -1921 && pos.getZ() >= -2076 && pos.getZ() <= -1929) {
                  BlockEntity blockEntity = mc.world.getBlockEntity(pos);
                  if (blockEntity != null) {
                     BlockEntityType<?> type = blockEntity.getType();
                     if (type == BlockEntityType.CHEST || type == BlockEntityType.TRAPPED_CHEST) {
                        result.add(pos);
                     }
                  }
               }
            }
         } catch (Throwable e) {
         }
      }

      return result;
   }

   private void updateTimers(List<BlockPos> chests) {
      if (mc.world != null && mc.player != null) {
         for (ArmorStandEntity stand : mc.world.getEntitiesByClass(ArmorStandEntity.class, mc.player.getBoundingBox().expand(256.0), e -> true)) {
            Matcher matcher = TIMER.matcher(stand.getName().getString());
            if (matcher.find()) {
               int minutes = Integer.parseInt(matcher.group(1));
               int seconds = Integer.parseInt(matcher.group(2));
               long ms = (minutes * 60L + seconds) * 1000L;
               BlockPos nearest = this.findNearestChest(chests, stand.getBlockPos());
               if (nearest != null) {
                  WardenESP.TrackedChest existing = this.findTracked(nearest);
                  if (existing != null) {
                     existing.updateTimer(ms);
                  } else {
                     this.tracked.add(new WardenESP.TrackedChest(nearest, ms));
                  }
               }
            }
         }

         this.tracked.removeIf(info -> info.getRemainingTime() <= 0L);
      }
   }

   private BlockPos findNearestChest(List<BlockPos> chests, BlockPos standPos) {
      for (BlockPos coord : chests) {
         if (standPos.getX() == coord.getX() && standPos.getZ() == coord.getZ()) {
            return coord;
         }
      }

      return null;
   }

   private WardenESP.TrackedChest findTracked(BlockPos pos) {
      for (WardenESP.TrackedChest info : this.tracked) {
         if (info.chestPos.equals(pos)) {
            return info;
         }
      }

      return null;
   }

   private void drawChests(MatrixStack matrices, List<BlockPos> chests) {
      List<BlockPos> ready = new ArrayList<>();
      for (BlockPos coord : chests) {
         if (this.findTracked(coord) == null) {
            ready.add(coord);
         }
      }

      if (!ready.isEmpty()) {
         Vec3d camera = mc.gameRenderer.getCamera().getPos();
         matrices.push();
         matrices.translate(-camera.x, -camera.y, -camera.z);
         Matrix4f matrix = matrices.peek().getPositionMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder lineBuffer = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

         for (BlockPos pos : ready) {
            this.addOutlinedBox(lineBuffer, matrix, pos, 1.0F, 0.39215687F, 0.39215687F, 1.0F);
         }

         BufferRenderer.drawWithGlobalProgram(lineBuffer.end());
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         matrices.pop();
      }
   }

   private void addOutlinedBox(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
      float minX = pos.getX();
      float minY = pos.getY();
      float minZ = pos.getZ();
      float maxX = minX + 1.0F;
      float maxY = minY + 1.0F;
      float maxZ = minZ + 1.0F;
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
   }

   private void drawHud(DrawContext context, List<BlockPos> chests) {
      Font font = Fonts.getFont("sf_regular", 13);
      if (font != null) {
         MatrixStack matrices = context.getMatrices();
         int themeColor = ColorUtils.getThemeColor();

         for (BlockPos coord : chests) {
            WardenESP.TrackedChest info = this.findTracked(coord);
            if (info != null) {
               Vec3d screen = this.worldToScreen(Vec3d.ofCenter(coord).add(0.0, 0.5, 0.0));
               if (screen != null) {
                  int totalSec = (int)(info.getRemainingTime() / 1000L);
                  String text = String.format(Locale.US, "%02d:%02d", totalSec / 60, totalSec % 60);
                  float textWidth = font.getStringWidth(text);
                  float iconSize = 10.0F;
                  float gap = 3.0F;
                  float boxWidth = iconSize + gap + textWidth + 8.0F;
                  float boxHeight = 12.5F;
                  float boxX = (float)screen.x - boxWidth * 0.5F;
                  float boxY = (float)screen.y - 6.0F;
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderUtils.drawDefaultHudThemedPanel(matrices, boxX, boxY, boxWidth, boxHeight, 2.0F, 3.0F, themeColor);
                  this.drawItemIcon(context, matrices, boxX + 4.0F, boxY + 1.25F, 0.62F);
                  font.drawString(matrices, text, boxX + 4.0F + iconSize + gap, boxY + 4.55F, -1);
                  RenderSystem.disableBlend();
               }
            }
         }
      }
   }

   private void drawItemIcon(DrawContext context, MatrixStack matrices, float x, float y, float scale) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(scale, scale, 1.0F);
      context.drawItem(CHEST_ICON, 0, 0);
      matrices.pop();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }

   private Vec3d worldToScreen(Vec3d worldPos) {
      if (mc.getWindow() == null) {
         return null;
      } else {
         Vector3f relative = new Vector3f(
            (float)(worldPos.x - this.lastCameraPos.x), (float)(worldPos.y - this.lastCameraPos.y), (float)(worldPos.z - this.lastCameraPos.z)
         );
         Quaternionf invCameraRot = new Quaternionf(this.lastCameraRotation).conjugate();
         relative.rotate(invCameraRot);
         Vector4f clip = new Vector4f(relative.x, relative.y, relative.z, 1.0F);
         this.lastProjectionMatrix.transform(clip);
         float w = clip.w;
         if (w <= 9.999999747378752E-6F) {
            return null;
         } else {
            float ndcX = clip.x / w;
            float ndcY = clip.y / w;
            float screenX = (ndcX * 0.5F + 0.5F) * mc.getWindow().getScaledWidth();
            float screenY = (1.0F - (ndcY * 0.5F + 0.5F)) * mc.getWindow().getScaledHeight();
            return !Float.isNaN(screenX) && !Float.isNaN(screenY) ? new Vec3d(screenX, screenY, clip.z / w) : null;
         }
      }
   }

   private static final class WardenCounter {
      private long start = System.currentTimeMillis();

      void reset() {
         this.start = System.currentTimeMillis();
      }

      long passed() {
         return System.currentTimeMillis() - this.start;
      }
   }

   public static class TrackedChest {
      private final BlockPos chestPos;
      private final WardenESP.WardenCounter counter = new WardenESP.WardenCounter();
      private long time;

      public TrackedChest(BlockPos chestPos, long current) {
         this.chestPos = chestPos;
         this.time = current;
         this.counter.reset();
      }

      public void updateTimer(long current) {
         if (Math.abs(current / 1000L - this.getRemainingTime() / 1000L) > 5L) {
            this.time = current;
            this.counter.reset();
         }
      }

      public long getRemainingTime() {
         return Math.max(0L, this.time - this.counter.passed());
      }
   }
}
