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

public final class FreddyPlayerModel extends AbstractCustomPlayerModel {
   private static final String TEXT = "freddy_root";
   private final ModelPart modelPart;
   private final ModelPart modelPart2;
   private final ModelPart modelPart3;
   private final ModelPart modelPart4;
   private final ModelPart modelPart5;
   private final ModelPart modelPart6;

   public FreddyPlayerModel() {
      super(helper(), "freddy_root");
      ModelPart var1 = this.customRoot();
      this.modelPart = var1.getChild("fredbody");
      this.modelPart2 = this.modelPart.getChild("arm_left");
      this.modelPart3 = this.modelPart.getChild("arm_right");
      this.modelPart4 = this.modelPart.getChild("leg_left");
      this.modelPart5 = this.modelPart.getChild("leg_right");
      this.modelPart6 = this.modelPart.getChild("fredhead");
   }

   private static ModelPart helper() {
      ModelData var0 = PlayerEntityModel.getTexturedModelData(new Dilation(0.0F), false);
      ModelPartData var1 = var0.getRoot();
      ModelPartData var2 = var1.addChild("freddy_root", ModelPartBuilder.create(), ModelTransform.NONE);
      ModelPartData var3 = var2.addChild(
         "fredbody", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -14.0F, -1.0F, 2.0F, 24.0F, 2.0F), ModelTransform.pivot(0.0F, -9.0F, 0.0F)
      );
      var3.addChild(
         "torso",
         ModelPartBuilder.create().uv(8, 0).cuboid(-6.0F, -9.0F, -4.0F, 12.0F, 18.0F, 8.0F),
         ModelTransform.of(0.0F, 0.0F, 0.0F, (float) (Math.PI / 180.0), 0.0F, 0.0F)
      );
      var3.addChild("crotch", ModelPartBuilder.create().uv(56, 0).cuboid(-5.5F, 0.0F, -3.5F, 11.0F, 3.0F, 7.0F), ModelTransform.pivot(0.0F, 9.5F, 0.0F));
      ModelPartData var4 = var3.addChild(
         "leg_right", ModelPartBuilder.create().uv(90, 8).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F), ModelTransform.pivot(-3.3F, 12.5F, 0.0F)
      );
      var4.addChild("leg_right_pad", ModelPartBuilder.create().uv(73, 33).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      ModelPartData var5 = var4.addChild(
         "leg_right_2",
         ModelPartBuilder.create().uv(20, 35).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F),
         ModelTransform.of(0.0F, 9.6F, 0.0F, 0.034906585F, 0.0F, 0.0F)
      );
      var5.addChild("leg_right_pad_2", ModelPartBuilder.create().uv(0, 39).cuboid(-2.5F, 0.0F, -3.0F, 5.0F, 7.0F, 6.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      var5.addChild(
         "foot_right",
         ModelPartBuilder.create().uv(22, 39).cuboid(-2.5F, 0.0F, -6.0F, 5.0F, 3.0F, 8.0F),
         ModelTransform.of(0.0F, 8.0F, 0.0F, -0.034906585F, 0.0F, 0.0F)
      );
      ModelPartData var6 = var3.addChild(
         "leg_left", ModelPartBuilder.create().uv(54, 10).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F), ModelTransform.pivot(3.3F, 12.5F, 0.0F)
      );
      var6.addChild("leg_left_pad", ModelPartBuilder.create().uv(48, 39).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      ModelPartData var7 = var6.addChild(
         "leg_left_2",
         ModelPartBuilder.create().uv(72, 48).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F),
         ModelTransform.of(0.0F, 9.6F, 0.0F, 0.034906585F, 0.0F, 0.0F)
      );
      var7.addChild("leg_left_pad_2", ModelPartBuilder.create().uv(16, 50).cuboid(-2.5F, 0.0F, -3.0F, 5.0F, 7.0F, 6.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      var7.addChild(
         "foot_left",
         ModelPartBuilder.create().uv(72, 50).cuboid(-2.5F, 0.0F, -6.0F, 5.0F, 3.0F, 8.0F),
         ModelTransform.of(0.0F, 8.0F, 0.0F, -0.034906585F, 0.0F, 0.0F)
      );
      ModelPartData var8 = var3.addChild(
         "arm_left",
         ModelPartBuilder.create().uv(62, 10).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F),
         ModelTransform.of(6.5F, -8.0F, 0.0F, 0.0F, 0.0F, (float) (-Math.PI / 12))
      );
      var8.addChild("arm_left_pad", ModelPartBuilder.create().uv(38, 54).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 9.0F, 5.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      ModelPartData var9 = var8.addChild(
         "arm_left_2",
         ModelPartBuilder.create().uv(90, 48).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F),
         ModelTransform.of(0.0F, 9.6F, 0.0F, (float) (-Math.PI / 18), 0.0F, 0.0F)
      );
      var9.addChild("arm_left_pad_2", ModelPartBuilder.create().uv(0, 58).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 7.0F, 5.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      var9.addChild(
         "hand_left",
         ModelPartBuilder.create().uv(58, 56).cuboid(-1.0F, 0.0F, -2.5F, 4.0F, 4.0F, 5.0F),
         ModelTransform.of(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.05235988F)
      );
      ModelPartData var10 = var3.addChild(
         "arm_right",
         ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F),
         ModelTransform.of(-6.5F, -8.0F, 0.0F, 0.0F, 0.0F, (float) (Math.PI / 12))
      );
      var10.addChild("arm_right_pad", ModelPartBuilder.create().uv(70, 10).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 9.0F, 5.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
      ModelPartData var11 = var10.addChild(
         "arm_right_2",
         ModelPartBuilder.create().uv(90, 20).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F),
         ModelTransform.of(0.0F, 9.6F, 0.0F, (float) (-Math.PI / 18), 0.0F, 0.0F)
      );
      var11.addChild(
         "arm_right_pad_2", ModelPartBuilder.create().uv(0, 26).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 7.0F, 5.0F), ModelTransform.pivot(0.0F, 0.5F, 0.0F)
      );
      var11.addChild(
         "hand_right",
         ModelPartBuilder.create().uv(20, 26).cuboid(-2.0F, 0.0F, -2.5F, 4.0F, 4.0F, 5.0F),
         ModelTransform.of(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.05235988F)
      );
      ModelPartData var12 = var3.addChild(
         "fredhead", ModelPartBuilder.create().uv(39, 22).cuboid(-5.5F, -8.0F, -4.5F, 11.0F, 8.0F, 9.0F), ModelTransform.pivot(0.0F, -13.0F, -0.5F)
      );
      var12.addChild(
         "jaw",
         ModelPartBuilder.create().uv(49, 65).cuboid(-5.0F, 0.0F, -4.5F, 10.0F, 3.0F, 9.0F),
         ModelTransform.of(0.0F, 0.5F, 0.0F, 0.08726646F, 0.0F, 0.0F)
      );
      var12.addChild("frednose", ModelPartBuilder.create().uv(17, 67).cuboid(-4.0F, -2.0F, -3.0F, 8.0F, 4.0F, 3.0F), ModelTransform.pivot(0.0F, -2.0F, -4.5F));
      ModelPartData var13 = var12.addChild(
         "ear_right",
         ModelPartBuilder.create().uv(8, 0).cuboid(-1.0F, -3.0F, -0.5F, 2.0F, 3.0F, 1.0F),
         ModelTransform.of(-4.5F, -5.5F, 0.0F, 0.05235988F, 0.0F, (float) (-Math.PI / 3))
      );
      var13.addChild(
         "ear_right_pad", ModelPartBuilder.create().uv(85, 0).cuboid(-2.0F, -5.0F, -1.0F, 4.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -1.0F, 0.0F)
      );
      ModelPartData var14 = var12.addChild(
         "ear_left",
         ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, -3.0F, -0.5F, 2.0F, 3.0F, 1.0F),
         ModelTransform.of(4.5F, -5.5F, 0.0F, 0.05235988F, 0.0F, (float) (Math.PI / 3))
      );
      var14.addChild(
         "ear_left_pad", ModelPartBuilder.create().uv(40, 39).cuboid(-2.0F, -5.0F, -1.0F, 4.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -1.0F, 0.0F)
      );
      ModelPartData var15 = var12.addChild(
         "hat",
         ModelPartBuilder.create().uv(70, 24).cuboid(-3.0F, -0.5F, -3.0F, 6.0F, 1.0F, 6.0F),
         ModelTransform.of(0.0F, -8.4F, 0.0F, (float) (-Math.PI / 180.0), 0.0F, 0.0F)
      );
      var15.addChild(
         "hat_top",
         ModelPartBuilder.create().uv(78, 61).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F),
         ModelTransform.of(0.0F, 0.1F, 0.0F, (float) (-Math.PI / 180.0), 0.0F, 0.0F)
      );
      return TexturedModelData.of(var0, 100, 80).createModel();
   }

   @Override
   protected void updateCustomAngles(PlayerEntityRenderState state) {
      this.sync(this.modelPart6, this.head);
      this.sync(this.modelPart2, this.leftArm);
      this.sync(this.modelPart3, this.rightArm);
      this.sync(this.modelPart4, this.leftLeg);
      this.sync(this.modelPart5, this.rightLeg);
      this.modelPart.pitch = this.body.pitch * 0.08F;
      this.modelPart.yaw = this.body.yaw * 0.08F;
   }
}
