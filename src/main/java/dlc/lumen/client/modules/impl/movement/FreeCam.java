package dlc.lumen.client.modules.impl.movement;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventMove;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.client.modules.Module;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class FreeCam extends Module {
   public static FreeCam INSTANCE = new FreeCam();
   public Vec3d pos;

   public FreeCam() {
      super("FreeCam", "Обзор местности за фейк игрока", Module.ModuleCategory.MOVEMENT);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player != null) {
         this.pos = mc.player.getPos();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player != null && this.pos != null) {
         mc.player.setPosition(this.pos);
      }
   }

   @EventLink
   public void onEvent(EventPacket event) {
      Packet var2 = event.getPacket();
      if (var2 instanceof PlayerMoveC2SPacket) {
         event.cancel();
      } else if (var2 instanceof PlayerRespawnS2CPacket || var2 instanceof GameJoinS2CPacket) {
         this.toggle();
      }
   }

   @EventLink
   public void onEvent(Event3DRender event) {
      if (this.pos != null && mc.player != null) {
         float var2 = mc.player.getWidth() / 2.0F;
         float var3 = mc.player.getHeight();
         Box var4 = new Box(this.pos.x - var2, this.pos.y, this.pos.z - var2, this.pos.x + var2, this.pos.y + var3, this.pos.z + var2);
         this.helper(event.getMatrices(), var4, event.getCamera().getPos());
      }
   }

   private void helper(MatrixStack matrices, Box box, Vec3d camera) {
      double var4 = box.minX - camera.x;
      double var6 = box.minY - camera.y;
      double var8 = box.minZ - camera.z;
      double var10 = box.maxX - camera.x;
      double var12 = box.maxY - camera.y;
      double var14 = box.maxZ - camera.z;
      Matrix4f var16 = matrices.peek().getPositionMatrix();
      Tessellator var17 = Tessellator.getInstance();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      BufferBuilder var18 = var17.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      float var19 = 1.0F;
      float var20 = 1.0F;
      float var21 = 1.0F;
      float var22 = 1.0F;
      var18.vertex(var16, (float)var4, (float)var6, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var6, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var6, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var6, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var6, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var6, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var6, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var6, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var12, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var12, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var12, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var12, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var12, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var12, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var12, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var12, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var6, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var12, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var6, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var12, (float)var8).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var6, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var10, (float)var12, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var6, (float)var14).color(var19, var20, var21, var22);
      var18.vertex(var16, (float)var4, (float)var12, (float)var14).color(var19, var20, var21, var22);
      BufferRenderer.drawWithGlobalProgram(var18.end());
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   @EventLink
   public void onEvent(EventMove event) {
      if (mc.player != null) {
         mc.player.noClip = true;
         double var2 = 1.0;
         double var4 = mc.player.input.movementForward;
         double var6 = mc.player.input.movementSideways;
         double var8 = Math.toRadians(mc.player.getYaw());
         double var10 = 0.0;
         double var12 = 0.0;
         if (var4 != 0.0 || var6 != 0.0) {
            double var14 = var8 + Math.atan2(-var6, var4);
            var10 = -Math.sin(var14) * var2;
            var12 = Math.cos(var14) * var2;
         }

         double var16 = 0.0;
         if (mc.options.jumpKey.isPressed()) {
            var16 = var2;
         } else if (mc.options.sneakKey.isPressed()) {
            var16 = -var2;
         }

         event.setMovePos(new Vec3d(var10, var16, var12));
      }
   }

   @EventLink
   public void onEvent(EventMoveInput event) {
      if (mc.player != null) {
         if (mc.player.getPose() == EntityPose.CROUCHING || mc.player.getPose() == EntityPose.SWIMMING) {
            event.setStrafe(event.getStrafe() * 5.0F);
         }
      }
   }
}
