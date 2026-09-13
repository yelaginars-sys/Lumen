package dlc.lumen.mixin;

import dlc.lumen.client.modules.impl.render.ViewArmorDurability;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
   @Unique
   private ItemStack lumen$stack = ItemStack.EMPTY;

   @Inject(
      method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
      at = @At("HEAD")
   )
   private void lumen$captureStack(
      LayerType layerType,
      RegistryKey<EquipmentAsset> asset,
      Model model,
      ItemStack stack,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      Identifier texture,
      CallbackInfo ci
   ) {
      this.lumen$stack = stack;
   }

   @ModifyArg(
      method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
      ),
      index = 4
   )
   private int lumen$durabilityTint(int color) {
      ViewArmorDurability module = ViewArmorDurability.INSTANCE;
      return module != null && module.shouldTint(this.lumen$stack) ? module.barColor(this.lumen$stack) : color;
   }
}
