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

public final class RabbitPlayerModel extends AbstractCustomPlayerModel {
   private static final String TEXT = "rabbit_root";
   private final ModelPart modelPart = this.customRoot();
   private final ModelPart modelPart2 = this.modelPart.getChild("head");
   private final ModelPart modelPart3 = this.modelPart.getChild("left_arm");
   private final ModelPart modelPart4 = this.modelPart.getChild("right_arm");
   private final ModelPart modelPart5 = this.modelPart.getChild("left_leg");
   private final ModelPart modelPart6 = this.modelPart.getChild("right_leg");

   public RabbitPlayerModel() {
      super(helper(), "rabbit_root");
   }

   private static ModelPart helper() {
      ModelData var0 = PlayerEntityModel.getTexturedModelData(new Dilation(0.0F), false);
      ModelPartData var1 = var0.getRoot();
      ModelPartData var2 = var1.addChild("rabbit_root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
      var2.addChild("body", ModelPartBuilder.create().uv(28, 45).cuboid(-5.0F, -13.0F, -5.0F, 10.0F, 11.0F, 8.0F), ModelTransform.NONE);
      var2.addChild("left_arm", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, -2.0F, 2.0F, 8.0F, 4.0F), ModelTransform.pivot(5.0F, -13.0F, -1.0F));
      var2.addChild(
         "right_arm", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 2.0F, 8.0F, 4.0F), ModelTransform.pivot(-5.0F, -13.0F, -1.0F)
      );
      var2.addChild("left_leg", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F), ModelTransform.pivot(3.0F, -2.0F, -1.0F));
      var2.addChild("right_leg", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F), ModelTransform.pivot(-3.0F, -2.0F, -1.0F));
      var2.addChild(
         "head",
         ModelPartBuilder.create()
            .uv(0, 0)
            .cuboid(-3.0F, 0.0F, -4.0F, 6.0F, 1.0F, 6.0F)
            .uv(56, 0)
            .cuboid(-5.0F, -9.0F, -5.0F, 2.0F, 3.0F, 2.0F)
            .uv(56, 0)
            .mirrored()
            .cuboid(3.0F, -9.0F, -5.0F, 2.0F, 3.0F, 2.0F)
            .uv(0, 45)
            .cuboid(-4.0F, -11.0F, -4.0F, 8.0F, 11.0F, 8.0F)
            .uv(46, 0)
            .cuboid(1.0F, -20.0F, 0.0F, 3.0F, 9.0F, 1.0F)
            .uv(46, 0)
            .cuboid(-4.0F, -20.0F, 0.0F, 3.0F, 9.0F, 1.0F),
         ModelTransform.pivot(0.0F, -14.0F, -1.0F)
      );
      return TexturedModelData.of(var0, 64, 64).createModel();
   }

   @Override
   protected void updateCustomAngles(PlayerEntityRenderState state) {
      this.modelPart.pitch = this.body.pitch * 0.35F;
      this.modelPart.yaw = this.body.yaw * 0.2F;
      this.sync(this.modelPart2, this.head);
      this.sync(this.modelPart3, this.leftArm);
      this.modelPart3.roll -= 0.0873F;
      this.sync(this.modelPart4, this.rightArm);
      this.modelPart4.roll += 0.0873F;
      this.sync(this.modelPart5, this.leftLeg);
      this.sync(this.modelPart6, this.rightLeg);
   }
}
