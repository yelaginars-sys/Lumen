package dlc.lumen.client.render.models;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.client.social.GlobalSocialManager;
import java.util.UUID;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public final class CosmeticWorldRenderer implements QClient {
   public static final CosmeticWorldRenderer INSTANCE = new CosmeticWorldRenderer();

   private CosmeticWorldRenderer() {
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (mc.player != null && mc.world != null && mc.options != null) {
         if (!mc.options.getPerspective().isFirstPerson()) {
            this.helper(mc.player, CosmeticManager.getSelectedSword(), event);
         }

         for (GlobalSocialManager.PartyMemberSnapshot var3 : GlobalSocialManager.INSTANCE.getVisiblePartyMembers()) {
            PlayerEntity var4 = this.helper2(var3.playerUuid());
            if (var4 != null && var4 != mc.player) {
               this.helper(var4, CosmeticSword.fromId(var3.swordId()), event);
            }
         }
      }
   }

   private void helper(PlayerEntity player, CosmeticSword sword, Event3DRender event) {
      if (player != null && sword != null && sword.isCustom() && sword.modelResource() != null) {
         if (!player.getMainHandStack().isEmpty() && player.getMainHandStack().getItem() instanceof SwordItem) {
            float var4 = event.getTickDelta();
            Vec3d var5 = player.getLerpedPos(var4);
            float var6 = player.getYaw();
            double var7 = Math.toRadians(var6);
            Vec3d var9 = new Vec3d(-Math.sin(var7), 0.0, Math.cos(var7));
            Vec3d var10 = new Vec3d(Math.cos(var7), 0.0, Math.sin(var7));
            Vec3d var11 = var5.add(0.0, 0.95, 0.0).add(var10.multiply(0.38)).add(var9.multiply(-0.02));
            float var12 = MathHelper.sin(player.getHandSwingProgress(var4) * (float) Math.PI);
            Vec3d var13 = event.getCamera().getPos();
            MatrixStack var14 = event.getMatrices();
            var14.push();
            var14.translate(var11.x - var13.x, var11.y - var13.y, var11.z - var13.z);
            var14.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var6));
            var14.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var12 * -55.0F));
            var14.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var12 * 18.0F));
            var14.scale(0.8F, 0.8F, 0.8F);
            var14.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            Immediate var15 = mc.getBufferBuilders().getEntityVertexConsumers();
            GltfPlayerModelRenderer.renderStatic(sword.modelResource(), sword.id(), var14, var15, 15728880);
            var15.draw();
            var14.pop();
         }
      }
   }

   private PlayerEntity helper2(UUID uuid) {
      if (uuid != null && mc.world != null) {
         for (PlayerEntity var3 : mc.world.getPlayers()) {
            if (uuid.equals(var3.getUuid())) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
