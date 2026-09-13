package dlc.lumen.client.render.models;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

public final class AmogusPlayerModel extends AbstractCustomPlayerModel {
   private static final String TEXT = "amogus_root";
   private final ModelPart modelPart;
   private final ModelPart modelPart2;
   private final ModelPart modelPart3;
   private final ModelPart modelPart4;

   public AmogusPlayerModel() {
      super(helper(), "amogus_root");
      ModelPart var1 = this.customRoot();
      this.modelPart = var1.getChild("body_shell");
      this.modelPart2 = var1.getChild("visor");
      this.modelPart3 = var1.getChild("left_leg");
      this.modelPart4 = var1.getChild("right_leg");
   }

   private static ModelPart helper() {
      ModelData var0 = PlayerEntityModel.getTexturedModelData(new Dilation(0.0F), false);
      ModelPartData var1 = var0.getRoot();
      ModelPartData var2 = var1.addChild("amogus_root", ModelPartBuilder.create(), ModelTransform.NONE);
      var2.addChild(
         "body_shell",
         ModelPartBuilder.create()
            .uv(34, 8)
            .cuboid(-4.0F, 6.0F, -3.0F, 8.0F, 12.0F, 6.0F)
            .uv(15, 10)
            .cuboid(-3.0F, 9.0F, 3.0F, 6.0F, 8.0F, 3.0F)
            .uv(26, 0)
            .cuboid(-3.0F, 5.0F, -3.0F, 6.0F, 1.0F, 6.0F),
         ModelTransform.NONE
      );
      var2.addChild("visor", ModelPartBuilder.create().uv(0, 10).cuboid(-3.0F, 7.0F, -4.0F, 6.0F, 4.0F, 1.0F), ModelTransform.NONE);
      var2.addChild("left_leg", ModelPartBuilder.create().uv(0, 0).cuboid(0.9F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), ModelTransform.pivot(2.0F, 18.0F, 0.0F));
      var2.addChild("right_leg", ModelPartBuilder.create().uv(13, 0).cuboid(-3.9F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), ModelTransform.pivot(-2.0F, 18.0F, 0.0F));
      return TexturedModelData.of(var0, 64, 64).createModel();
   }

   @Override
   protected void updateCustomAngles(PlayerEntityRenderState state) {
      this.modelPart.pitch = this.body.pitch * 0.5F;
      this.modelPart.yaw = this.body.yaw * 0.35F;
      this.modelPart2.pitch = this.head.pitch * 0.35F;
      this.modelPart2.yaw = this.head.yaw * 0.45F;
      this.modelPart2.roll = this.head.roll * 0.2F;
      this.modelPart3.pitch = this.leftLeg.pitch;
      this.modelPart3.yaw = this.leftLeg.yaw;
      this.modelPart3.roll = this.leftLeg.roll;
      this.modelPart4.pitch = this.rightLeg.pitch;
      this.modelPart4.yaw = this.rightLeg.yaw;
      this.modelPart4.roll = this.rightLeg.roll;
   }
}
