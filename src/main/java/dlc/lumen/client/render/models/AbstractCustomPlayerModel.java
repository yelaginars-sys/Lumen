package dlc.lumen.client.render.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

abstract class AbstractCustomPlayerModel extends PlayerEntityModel {
   private final ModelPart modelPart;

   protected AbstractCustomPlayerModel(ModelPart root, String customRootName) {
      super(root, false);
      this.modelPart = root.getChild(customRootName);
   }

   @Override
   public void setAngles(PlayerEntityRenderState state) {
      super.setAngles(state);
      this.hideVanillaParts();
      this.updateCustomAngles(state);
   }

   protected abstract void updateCustomAngles(PlayerEntityRenderState var1);

   protected final void sync(ModelPart target, ModelPart source) {
      target.pitch = source.pitch;
      target.yaw = source.yaw;
      target.roll = source.roll;
   }

   protected final void hideVanillaParts() {
      this.setVisible(false);
      this.head.visible = false;
      this.hat.visible = false;
      this.body.visible = false;
      this.leftArm.visible = false;
      this.rightArm.visible = false;
      this.leftLeg.visible = false;
      this.rightLeg.visible = false;
      this.modelPart.visible = true;
   }

   protected final ModelPart customRoot() {
      return this.modelPart;
   }
}
