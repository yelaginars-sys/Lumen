package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.modules.impl.misc.NameProtect;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextNameProtectMixin {
   @ModifyVariable(
      method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private String lumen$patchStringShadow(String text) {
      return this.patch(text);
   }

   @ModifyVariable(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private String lumen$patchString(String text) {
      return this.patch(text);
   }

   private String patch(String text) {
      if (ModuleClass.INSTANCE == null) {
         return text;
      }

      NameProtect nameProtect = ModuleClass.nameProtect;
      return nameProtect != null && nameProtect.isEnable() ? nameProtect.patchIncomingText(text) : text;
   }
}
