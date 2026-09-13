package dlc.lumen.client.render.models;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.client.social.GlobalSocialManager;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public final class PetManager implements QClient {
   public static final PetManager INSTANCE = new PetManager();
   private volatile CosmeticPet selectedPet = CosmeticPet.NONE;
   private Vec3d pet2 = null;
   private float volume = 0.0F;
   private long timestamp = 0L;

   private PetManager() {
   }

   public CosmeticPet getSelectedPet() {
      return this.selectedPet;
   }

   public void setSelectedPet(CosmeticPet pet) {
      this.selectedPet = pet == null ? CosmeticPet.NONE : pet;
      this.pet2 = null;
   }

   public List<CosmeticPet> pets() {
      return Arrays.asList(CosmeticPet.values());
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (mc.player != null && mc.world != null) {
         CosmeticPet var2 = this.selectedPet;
         if (var2 != null && var2.isCustom() && var2.obj() != null) {
            this.helper3(event.getTickDelta());
            if (this.pet2 != null) {
               this.helper2(event, var2, this.pet2, this.volume);
            }
         }

         this.helper(event);
      }
   }

   private void helper(Event3DRender event) {
      for (GlobalSocialManager.PartyMemberSnapshot var3 : GlobalSocialManager.INSTANCE.getVisiblePartyMembers()) {
         CosmeticPet var4 = CosmeticPet.fromId(var3.petId());
         if (var4.isCustom() && var4.obj() != null) {
            Vec3d var5 = GlobalSocialManager.INSTANCE.getRenderPosition(var3);
            if (var5 != null) {
               double var6 = Math.toRadians(var3.yaw());
               Vec3d var8 = new Vec3d(Math.sin(var6), 0.0, -Math.cos(var6));
               Vec3d var9 = new Vec3d(Math.cos(var6), 0.0, Math.sin(var6)).multiply(0.35);
               Vec3d var10 = this.helper4(var5.x + var8.x * 1.25 + var9.x, var5.y + 1.0, var5.z + var8.z * 1.25 + var9.z);
               this.helper2(event, var4, var10, var3.yaw());
            }
         }
      }
   }

   private void helper2(Event3DRender event, CosmeticPet pet, Vec3d position, float yaw) {
      Vec3d var5 = event.getCamera().getPos();
      MatrixStack var6 = event.getMatrices();
      var6.push();
      var6.translate(position.x - var5.x, position.y - var5.y, position.z - var5.z);
      var6.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
      var6.scale(pet.scale(), pet.scale(), pet.scale());
      float var7 = ObjModelRenderer.modelMinY(pet.obj(), pet.texture());
      var6.translate(0.0F, -var7, 0.0F);
      Immediate var8 = mc.getBufferBuilders().getEntityVertexConsumers();
      ObjModelRenderer.render(pet.obj(), pet.texture(), var6, var8, 15728880);
      var8.draw();
      var6.pop();
   }

   private void helper3(float tickDelta) {
      ClientPlayerEntity var2 = mc.player;
      Vec3d var3 = var2.getLerpedPos(tickDelta);
      long var4 = System.nanoTime();
      float var6 = this.timestamp > 0L ? (float)(var4 - this.timestamp) / 1.0E9F : 0.016F;
      this.timestamp = var4;
      var6 = MathHelper.clamp(var6, 0.001F, 0.1F);
      if (this.pet2 != null && !(this.pet2.squaredDistanceTo(var3) > 144.0)) {
         double var7 = var3.x - this.pet2.x;
         double var9 = var3.z - this.pet2.z;
         double var11 = Math.sqrt(var7 * var7 + var9 * var9);
         double var13 = 1.8;
         if (var11 > var13) {
            double var15 = Math.min(4.5 * var6, var11 - var13 * 0.6);
            double var17 = this.pet2.x + var7 / var11 * var15;
            double var19 = this.pet2.z + var9 / var11 * var15;
            this.pet2 = this.helper4(var17, var3.y + 1.0, var19);
            float var21 = (float)Math.toDegrees(Math.atan2(-var7, var9));
            this.volume = this.volume + MathHelper.wrapDegrees(var21 - this.volume) * Math.min(1.0F, 10.0F * var6);
         } else {
            this.pet2 = this.helper4(this.pet2.x, var3.y + 1.0, this.pet2.z);
         }
      } else {
         this.pet2 = this.helper4(var3.x + 1.0, var3.y, var3.z);
      }
   }

   private Vec3d helper4(double x, double yTop, double z) {
      if (mc.world == null) {
         return new Vec3d(x, yTop, z);
      }

      int var7 = (int)Math.floor(x);
      int var8 = (int)Math.floor(z);
      int var9 = (int)Math.floor(yTop) + 1;

      for (int var10 = var9; var10 > var9 - 8; var10--) {
         BlockPos var11 = new BlockPos(var7, var10, var8);
         boolean var12 = !mc.world.getBlockState(var11).isAir() && !mc.world.getBlockState(var11).getCollisionShape(mc.world, var11).isEmpty();
         if (var12 && mc.world.getBlockState(var11.up()).getCollisionShape(mc.world, var11.up()).isEmpty()) {
            return new Vec3d(x, var10 + 1.0, z);
         }
      }

      return new Vec3d(x, yTop, z);
   }

   public void renderPetInGui(DrawContext context, CosmeticPet pet, float centerX, float centerY, float size, float spinDeg) {
      if (pet != null && pet.isCustom() && pet.obj() != null) {
         ObjModelRenderer.renderInGui(context, pet.obj(), pet.texture(), centerX, centerY, size, spinDeg);
      }
   }
}
