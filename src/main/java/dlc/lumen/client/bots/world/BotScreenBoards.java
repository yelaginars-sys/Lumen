package dlc.lumen.client.bots.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventMouse;
import dlc.lumen.api.events.implement.EventScroll;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.bots.BotRegistry;
import dlc.lumen.client.bots.core.BotManager;
import dlc.lumen.client.bots.core.BotSession;
import dlc.lumen.client.bots.core.BotViewRenderer;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public enum BotScreenBoards implements QClient {
   INSTANCE;

   private static final int INDEX = 480;
   private static final int INDEX2 = 270;
   private static final double LEVEL = 48.0;
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   private String text2 = "";
   private float volume = 3.2F;
   private long timestamp;
   private final List<BotScreenBoards.Board> items = new ArrayList<>();
   private final Map<BotSession, Boolean> linkedHashMap = new LinkedHashMap<>();
   private boolean flag;

   public List<BotScreenBoards.Board> boards() {
      this.updateState8();
      return this.items;
   }

   public boolean has(String bot) {
      return this.find(bot) != null;
   }

   public BotScreenBoards.Board find(String bot) {
      this.updateState8();
      if (bot == null) {
         return null;
      }

      for (BotScreenBoards.Board var3 : this.items) {
         if (var3.bot != null && var3.bot.equalsIgnoreCase(bot)) {
            return var3;
         }
      }

      return null;
   }

   public BotScreenBoards.Board place(String bot, float width) {
      this.updateState8();
      if (mc.player == null) {
         return null;
      }

      float var3 = this.computefloat();
      Vec3d var4 = this.computeVec3d(var3);
      float var5 = width <= 0.0F ? 3.2F : width;
      BotScreenBoards.Board var6 = this.find(bot);
      if (var6 == null) {
         var6 = new BotScreenBoards.Board(bot, var4, var3, var5);
         this.items.add(var6);
      } else {
         var6.x = var4.x;
         var6.y = var4.y;
         var6.z = var4.z;
         var6.yaw = var3;
         var6.width = var5;
      }

      this.updateState9();
      return var6;
   }

   public void beginPlacing(String bot) {
      this.updateState8();
      if (bot != null && !bot.isBlank()) {
         BotScreenBoards.Board var2 = this.find(bot);
         this.text2 = bot;
         this.volume = var2 == null ? 3.2F : var2.width;
         this.timestamp = System.currentTimeMillis();
         ChatUtils.sendMessage("§7[Bot] §fЭкран §e" + bot + "§f: §7ЛКМ — поставить, ПКМ — отмена, колесо — размер");
      }
   }

   public boolean isPlacing() {
      return !this.text2.isEmpty();
   }

   public void cancelPlacing() {
      this.text2 = "";
   }

   @EventLink
   public void onMouse(EventMouse event) {
      if (this.isPlacing() && event.isPressed() && mc.currentScreen == null) {
         if (System.currentTimeMillis() - this.timestamp >= 250L) {
            if (event.getButton() == 0) {
               BotScreenBoards.Board var2 = this.place(this.text2, this.volume);
               if (var2 != null) {
                  ChatUtils.sendMessage(String.format("§7[Bot] §fЭкран §e%s §fпоставлен: %.1f, %.1f, %.1f", this.text2, var2.x, var2.y, var2.z));
               }

               this.cancelPlacing();
               event.setCancelled(true);
            } else if (event.getButton() == 1) {
               ChatUtils.sendMessage("§7[Bot] §fУстановка отменена");
               this.cancelPlacing();
               event.setCancelled(true);
            }
         }
      }
   }

   @EventLink
   public void onScroll(EventScroll event) {
      if (this.isPlacing()) {
         this.volume = MathHelper.clamp(this.volume + (event.getAmount() > 0.0 ? 0.4F : -0.4F), 1.0F, 16.0F);
         event.cancel();
      }
   }

   private Vec3d computeVec3d(float yaw) {
      HitResult var2 = mc.crosshairTarget;
      Vec3d var3 = var2 != null && var2.getType() != Type.MISS ? var2.getPos() : mc.player.getEyePos().add(mc.player.getRotationVec(1.0F).multiply(3.0));
      return var3.add(computeVec3d2(yaw).multiply(0.05));
   }

   private float computefloat() {
      return mc.player.getYaw() + 180.0F;
   }

   public boolean remove(String bot) {
      this.updateState8();
      boolean var2 = this.items.removeIf(board -> board.bot != null && board.bot.equalsIgnoreCase(bot));
      if (var2) {
         this.updateState9();
      }

      return var2;
   }

   public void clear() {
      this.updateState8();
      this.items.clear();
      this.updateState9();
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      this.updateState8();
      if (mc.world != null && !BotViewRenderer.inBotPip) {
         if (!this.items.isEmpty() || this.isPlacing()) {
            this.linkedHashMap.clear();
            Camera var2 = event.getCamera();
            Vec3d var3 = var2.getPos();
            Matrix4f var4 = event.getMatrices().peek().getPositionMatrix();
            if (this.isPlacing()) {
               this.updateState(var4, var3);
            }

            for (BotScreenBoards.Board var6 : this.items) {
               Vec3d var7 = var6.pos();
               if (!(var7.squaredDistanceTo(var3) > 2304.0)) {
                  BotSession var8 = BotManager.INSTANCE.find(var6.bot);
                  int var9 = var8 == null ? -1 : BotViewRenderer.INSTANCE.texture(var8);
                  this.updateState5(var6, var4, var3, var9);
                  if (var8 != null && var8.canRenderView()) {
                     this.linkedHashMap.put(var8, Boolean.TRUE);
                  }

                  this.updateState7(var6, var3, var8, var9);
               }
            }
         }
      }
   }

   private void updateState(Matrix4f matrix, Vec3d eye) {
      if (mc.player != null) {
         BotScreenBoards.Board var3 = new BotScreenBoards.Board(
            this.text2, this.computeVec3d(this.computefloat()), this.computefloat(), this.volume
         );
         BotSession var4 = BotManager.INSTANCE.find(this.text2);
         int var5 = var4 == null ? -1 : BotViewRenderer.INSTANCE.texture(var4);
         this.updateState5(var3, matrix, eye, var5);
         this.updateState2(var3, matrix, eye, ColorUtils.replAlpha(ColorUtils.getThemeColor(), 200));
         if (var4 != null && var4.canRenderView()) {
            this.linkedHashMap.put(var4, Boolean.TRUE);
         }

         this.updateState4(var3, eye);
      }
   }

   private void updateState2(BotScreenBoards.Board board, Matrix4f matrix, Vec3d eye, int color) {
      float var5 = board.width;
      float var6 = board.height();
      Vec3d var7 = computeVec3d3(board.yaw);
      Vec3d var8 = new Vec3d(0.0, 1.0, 0.0);
      Vec3d var9 = board.pos().subtract(eye);
      float var10 = 0.035F;
      this.updateState3(matrix, var9.add(var8.multiply(var6 / 2.0F)), var7, var8, var5, var10, color);
      this.updateState3(matrix, var9.add(var8.multiply(-var6 / 2.0F)), var7, var8, var5, var10, color);
      this.updateState3(matrix, var9.add(var7.multiply(-var5 / 2.0F)), var8, var7, var6, var10, color);
      this.updateState3(matrix, var9.add(var7.multiply(var5 / 2.0F)), var8, var7, var6, var10, color);
   }

   private void updateState3(Matrix4f matrix, Vec3d center, Vec3d along, Vec3d across, float length, float thickness, int color) {
      Vec3d var8 = center.add(along.multiply(-length / 2.0F)).add(across.multiply(-thickness));
      Vec3d var9 = center.add(along.multiply(length / 2.0F)).add(across.multiply(-thickness));
      Vec3d var10 = center.add(along.multiply(length / 2.0F)).add(across.multiply(thickness));
      Vec3d var11 = center.add(along.multiply(-length / 2.0F)).add(across.multiply(thickness));
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder var12 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      handleEvent2(var12, matrix, var8, color);
      handleEvent2(var12, matrix, var11, color);
      handleEvent2(var12, matrix, var10, color);
      handleEvent2(var12, matrix, var9, color);
      BufferRenderer.drawWithGlobalProgram(var12.end());
   }

   private void updateState4(BotScreenBoards.Board ghost, Vec3d eye) {
      TextRenderer var3 = mc.textRenderer;
      if (var3 != null) {
         String var4 = String.format(Locale.ROOT, "%s · %.1f блока", ghost.bot, ghost.width);
         Vec3d var5 = ghost.pos().subtract(eye).add(0.0, ghost.height() / 2.0F + 0.22F, 0.0);
         MatrixStack var6 = new MatrixStack();
         var6.translate(var5.x, var5.y, var5.z);
         var6.multiply(mc.getEntityRenderDispatcher().getRotation());
         float var7 = 0.025F;
         var6.scale(-var7, -var7, var7);
         Immediate var8 = mc.getBufferBuilders().getEntityVertexConsumers();
         var3.draw(
            Text.literal(var4),
            -var3.getWidth(var4) / 2.0F,
            0.0F,
            ColorUtils.getThemeColor(),
            false,
            var6.peek().getPositionMatrix(),
            var8,
            TextLayerType.SEE_THROUGH,
            ColorUtils.rgba(0, 0, 0, 140),
            15728880
         );
         var8.draw();
      }
   }

   public void renderRequestedFrames() {
      if (this.linkedHashMap.isEmpty()) {
         BotViewRenderer.INSTANCE.endFrame();
      } else {
         for (BotSession var2 : this.linkedHashMap.keySet()) {
            BotViewRenderer.INSTANCE.render(var2, 480, 270);
         }

         this.linkedHashMap.clear();
         BotViewRenderer.INSTANCE.endFrame();
      }
   }

   private void updateState5(BotScreenBoards.Board board, Matrix4f matrix, Vec3d eye, int texture) {
      float var5 = board.width;
      float var6 = board.height();
      Vec3d var7 = computeVec3d3(board.yaw);
      Vec3d var8 = new Vec3d(0.0, 1.0, 0.0);
      Vec3d var9 = board.pos().subtract(eye);
      Vec3d var10 = var9.add(var7.multiply(-var5 / 2.0F)).add(var8.multiply(var6 / 2.0F));
      Vec3d var11 = var9.add(var7.multiply(var5 / 2.0F)).add(var8.multiply(var6 / 2.0F));
      Vec3d var12 = var9.add(var7.multiply(var5 / 2.0F)).add(var8.multiply(-var6 / 2.0F));
      Vec3d var13 = var9.add(var7.multiply(-var5 / 2.0F)).add(var8.multiply(-var6 / 2.0F));
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      this.updateState6(matrix, board, var9, var7, var8);
      if (texture > 0) {
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, texture);
         BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         handleEvent(var14, matrix, var10, 0.0F, 1.0F, -1);
         handleEvent(var14, matrix, var13, 0.0F, 0.0F, -1);
         handleEvent(var14, matrix, var12, 1.0F, 0.0F, -1);
         handleEvent(var14, matrix, var11, 1.0F, 1.0F, -1);
         BufferRenderer.drawWithGlobalProgram(var14.end());
         RenderSystem.setShaderTexture(0, 0);
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private void updateState6(Matrix4f matrix, BotScreenBoards.Board board, Vec3d center, Vec3d right, Vec3d up) {
      float var6 = board.width * 1.03F;
      float var7 = board.height() * 1.05F;
      Vec3d var8 = center.add(right.multiply(-var6 / 2.0F)).add(up.multiply(var7 / 2.0F));
      Vec3d var9 = center.add(right.multiply(var6 / 2.0F)).add(up.multiply(var7 / 2.0F));
      Vec3d var10 = center.add(right.multiply(var6 / 2.0F)).add(up.multiply(-var7 / 2.0F));
      Vec3d var11 = center.add(right.multiply(-var6 / 2.0F)).add(up.multiply(-var7 / 2.0F));
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder var12 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int var13 = ColorUtils.rgba(12, 12, 16, 220);
      handleEvent2(var12, matrix, var8, var13);
      handleEvent2(var12, matrix, var11, var13);
      handleEvent2(var12, matrix, var10, var13);
      handleEvent2(var12, matrix, var9, var13);
      BufferRenderer.drawWithGlobalProgram(var12.end());
   }

   private void updateState7(BotScreenBoards.Board board, Vec3d eye, BotSession bot, int texture) {
      TextRenderer var5 = mc.textRenderer;
      if (var5 != null) {
         String var6 = board.bot;
         if (bot == null) {
            var6 = var6 + " · нет бота";
         } else if (!bot.isJoined()) {
            var6 = var6 + " · " + bot.getStatus();
         } else if (texture <= 0) {
            var6 = var6 + " · кадр...";
         }

         Vec3d var7 = board.pos().subtract(eye).add(0.0, -board.height() / 2.0F - 0.18F, 0.0);
         MatrixStack var8 = new MatrixStack();
         var8.translate(var7.x, var7.y, var7.z);
         var8.multiply(mc.getEntityRenderDispatcher().getRotation());
         float var9 = 0.025F;
         var8.scale(-var9, -var9, var9);
         Immediate var10 = mc.getBufferBuilders().getEntityVertexConsumers();
         float var11 = var5.getWidth(var6) / 2.0F;
         var5.draw(
            Text.literal(var6),
            -var11,
            0.0F,
            ColorUtils.rgba(235, 235, 240, 255),
            false,
            var8.peek().getPositionMatrix(),
            var10,
            TextLayerType.SEE_THROUGH,
            ColorUtils.rgba(0, 0, 0, 120),
            15728880
         );
         var10.draw();
      }
   }

   private static void handleEvent(BufferBuilder builder, Matrix4f matrix, Vec3d p, float u, float v, int color) {
      builder.vertex(matrix, (float)p.x, (float)p.y, (float)p.z).texture(u, v).color(color);
   }

   private static void handleEvent2(BufferBuilder builder, Matrix4f matrix, Vec3d p, int color) {
      builder.vertex(matrix, (float)p.x, (float)p.y, (float)p.z).color(color);
   }

   private static Vec3d computeVec3d2(float yaw) {
      float var1 = yaw * (float) (Math.PI / 180.0);
      return new Vec3d(-MathHelper.sin(var1), 0.0, MathHelper.cos(var1));
   }

   private static Vec3d computeVec3d3(float yaw) {
      float var1 = yaw * (float) (Math.PI / 180.0);
      return new Vec3d(MathHelper.cos(var1), 0.0, MathHelper.sin(var1));
   }

   private File getFile() {
      return new File(BotRegistry.directory(), "screens.json");
   }

   private void updateState8() {
      if (!this.flag) {
         this.flag = true;

         try {
            File var1 = this.getFile();
            if (!var1.isFile()) {
               return;
            }

            String var2 = new String(Files.readAllBytes(var1.toPath()), StandardCharsets.UTF_8);
            BotScreenBoards.Board[] var3 = (BotScreenBoards.Board[])GSON_BUILDER.fromJson(var2, BotScreenBoards.Board[].class);
            if (var3 == null) {
               return;
            }

            for (BotScreenBoards.Board var7 : var3) {
               if (var7 != null && var7.bot != null && !var7.bot.isBlank()) {
                  this.items.removeIf(existing -> existing.bot.equalsIgnoreCase(var7.bot));
                  this.items.add(var7);
               }
            }
         } catch (Throwable var8) {
         }
      }
   }

   private void updateState9() {
      try {
         File var1 = this.getFile();
         Files.createDirectories(var1.getParentFile().toPath());
         Files.write(var1.toPath(), GSON_BUILDER.toJson(this.items.toArray(new BotScreenBoards.Board[0])).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var2) {
      }
   }

   public static final class Board {
      public String bot = "";
      public double x;
      public double y;
      public double z;
      public float yaw;
      public float width = 3.2F;

      public Board() {
      }

      Board(String bot, Vec3d pos, float yaw, float width) {
         this.bot = bot;
         this.x = pos.x;
         this.y = pos.y;
         this.z = pos.z;
         this.yaw = yaw;
         this.width = width;
      }

      float height() {
         return this.width * 270.0F / 480.0F;
      }

      Vec3d pos() {
         return new Vec3d(this.x, this.y, this.z);
      }
   }
}
