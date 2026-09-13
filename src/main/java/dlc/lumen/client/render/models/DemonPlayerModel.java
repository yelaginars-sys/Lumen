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

public final class DemonPlayerModel extends AbstractCustomPlayerModel {
   private static final String TEXT = "demon_root";
   private final ModelPart modelPart;
   private final ModelPart modelPart2;
   private final ModelPart modelPart3;
   private final ModelPart modelPart4;
   private final ModelPart modelPart5;
   private final ModelPart modelPart6;

   public DemonPlayerModel() {
      super(helper(), "demon_root");
      ModelPart var1 = this.customRoot();
      this.modelPart = var1.getChild("head");
      this.modelPart2 = var1.getChild("body");
      this.modelPart3 = var1.getChild("left_arm");
      this.modelPart4 = var1.getChild("right_arm");
      this.modelPart5 = var1.getChild("left_leg");
      this.modelPart6 = var1.getChild("right_leg");
   }

   private static ModelPart helper() {
      ModelData var0 = PlayerEntityModel.getTexturedModelData(new Dilation(0.0F), false);
      ModelPartData var1 = var0.getRoot();
      ModelPartData var2 = var1.addChild("demon_root", ModelPartBuilder.create(), ModelTransform.NONE);
      ModelPartData var3 = var2.addChild(
         "head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -3.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, -6.0F, -1.0F)
      );
      var3.addChild(
         "left_horn",
         ModelPartBuilder.create()
            .uv(32, 8)
            .cuboid(13.4346F, -5.2071F, 2.7071F, 6.0F, 2.0F, 2.0F, new Dilation(0.1F))
            .uv(0, 0)
            .cuboid(17.4346F, -10.4071F, 2.7071F, 2.0F, 5.0F, 2.0F, new Dilation(0.1F)),
         ModelTransform.of(-8.0F, 8.0F, 0.0F, -0.3927F, 0.3927F, -0.5236F)
      );
      var3.addChild(
         "right_horn",
         ModelPartBuilder.create()
            .uv(32, 8)
            .mirrored()
            .cuboid(-19.4346F, -5.2071F, 2.7071F, 6.0F, 2.0F, 2.0F, new Dilation(0.1F))
            .uv(0, 0)
            .mirrored()
            .cuboid(-19.4346F, -10.4071F, 2.7071F, 2.0F, 5.0F, 2.0F, new Dilation(0.1F)),
         ModelTransform.of(8.0F, 8.0F, 0.0F, -0.3927F, -0.3927F, 0.5236F)
      );
      ModelPartData var4 = var2.addChild(
         "body",
         ModelPartBuilder.create().uv(0, 16).cuboid(-4.5F, -1.7028F, 1.4696F, 8.0F, 12.0F, 4.0F),
         ModelTransform.of(0.5F, -0.1F, -3.5F, 0.1745F, 0.0F, 0.0F)
      );
      var4.addChild(
         "left_wing",
         ModelPartBuilder.create().uv(40, 12).cuboid(-7.0072F, -0.5972F, 0.7515F, 12.0F, 13.0F, 0.0F),
         ModelTransform.of(8.25F, -2.0F, 10.0F, 0.0873F, -0.829F, 0.1745F)
      );
      var4.addChild(
         "right_wing",
         ModelPartBuilder.create().uv(40, 12).mirrored().cuboid(-4.9928F, -0.5972F, 0.7515F, 12.0F, 13.0F, 0.0F),
         ModelTransform.of(-9.25F, -2.0F, 10.0F, 0.0873F, 0.829F, -0.1745F)
      );
      var2.addChild(
         "left_arm",
         ModelPartBuilder.create().uv(24, 16).cuboid(-1.1F, -1.05F, 0.0F, 4.0F, 14.0F, 4.0F),
         ModelTransform.of(5.4F, -1.25F, -2.0F, 0.0F, 0.0F, -0.2182F)
      );
      var2.addChild(
         "right_arm",
         ModelPartBuilder.create().uv(24, 16).mirrored().cuboid(-2.9F, -1.05F, 0.0F, 4.0F, 14.0F, 4.0F),
         ModelTransform.of(-5.4F, -1.25F, -2.0F, 0.0F, 0.0F, 0.2182F)
      );
      ModelPartData var5 = var2.addChild(
         "left_leg", ModelPartBuilder.create().uv(48, 22).cuboid(-3.25F, -2.25F, -1.0F, 4.0F, 9.0F, 4.0F), ModelTransform.pivot(3.0F, 10.0F, 0.0F)
      );
      ModelPartData var6 = var5.addChild(
         "left_leg_1",
         ModelPartBuilder.create().uv(34, 34).cuboid(0.95F, 4.6F, 8.0511F, 3.0F, 5.0F, 3.0F),
         ModelTransform.of(-1.7F, -0.1F, -3.55F, -0.5236F, 0.0F, 0.0F)
      );
      var6.addChild(
         "left_foot",
         ModelPartBuilder.create().uv(26, 0).cuboid(-0.7F, -1.15F, 9.3F, 4.0F, 2.0F, 4.0F).uv(40, 0).cuboid(-0.7F, -1.15F, 7.3F, 4.0F, 2.0F, 2.0F),
         ModelTransform.of(1.4F, 15.0F, 0.25F, 0.5236F, 0.0F, 0.0F)
      );
      ModelPartData var7 = var6.addChild("left_claw_root", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 0.0F, -2.0F, 0.0F, -0.0873F, -0.2618F));
      var7.addChild(
         "left_claw",
         ModelPartBuilder.create()
            .uv(16, 34)
            .cuboid(-0.7911F, -10.1159F, 8.0029F, 4.0F, 4.0F, 5.0F)
            .uv(0, 32)
            .cuboid(-0.7911F, -15.1159F, 4.0029F, 4.0F, 9.0F, 4.0F),
         ModelTransform.pivot(1.9F, 12.0F, 0.25F)
      );
      ModelPartData var8 = var2.addChild(
         "right_leg", ModelPartBuilder.create().uv(48, 22).mirrored().cuboid(-0.75F, -2.25F, -1.0F, 4.0F, 9.0F, 4.0F), ModelTransform.pivot(-3.0F, 10.0F, 0.0F)
      );
      ModelPartData var9 = var8.addChild(
         "right_leg_1",
         ModelPartBuilder.create().uv(34, 34).mirrored().cuboid(-3.95F, 4.6F, 8.0511F, 3.0F, 5.0F, 3.0F),
         ModelTransform.of(1.7F, -0.1F, -3.55F, -0.5236F, 0.0F, 0.0F)
      );
      var9.addChild(
         "right_foot",
         ModelPartBuilder.create()
            .uv(26, 0)
            .mirrored()
            .cuboid(-3.3F, -1.15F, 9.3F, 4.0F, 2.0F, 4.0F)
            .uv(40, 0)
            .mirrored()
            .cuboid(-3.3F, -1.15F, 7.3F, 4.0F, 2.0F, 2.0F),
         ModelTransform.of(-1.4F, 15.0F, 0.25F, 0.5236F, 0.0F, 0.0F)
      );
      ModelPartData var10 = var9.addChild("right_claw_root", ModelPartBuilder.create(), ModelTransform.of(1.0F, 0.0F, -2.0F, 0.0F, 0.0873F, 0.2618F));
      var10.addChild(
         "right_claw",
         ModelPartBuilder.create()
            .uv(16, 34)
            .mirrored()
            .cuboid(-3.2089F, -10.1159F, 8.0029F, 4.0F, 4.0F, 5.0F)
            .uv(0, 32)
            .mirrored()
            .cuboid(-3.2089F, -15.1159F, 4.0029F, 4.0F, 9.0F, 4.0F),
         ModelTransform.pivot(-1.9F, 12.0F, 0.25F)
      );
      return TexturedModelData.of(var0, 64, 64).createModel();
   }

   @Override
   protected void updateCustomAngles(PlayerEntityRenderState state) {
      this.sync(this.modelPart, this.head);
      this.sync(this.modelPart3, this.leftArm);
      this.sync(this.modelPart4, this.rightArm);
      this.sync(this.modelPart5, this.leftLeg);
      this.sync(this.modelPart6, this.rightLeg);
      this.modelPart2.pitch = 0.1745F + this.body.pitch * 0.35F;
      this.modelPart2.yaw = this.body.yaw * 0.1F;
   }
}
