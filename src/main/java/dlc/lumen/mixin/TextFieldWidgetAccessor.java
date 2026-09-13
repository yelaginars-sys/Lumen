package dlc.lumen.mixin;

import java.util.function.BiFunction;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldWidget.class)
public interface TextFieldWidgetAccessor {
   @Accessor("renderTextProvider")
   BiFunction<String, Integer, OrderedText> lumen$getRenderTextProvider();
}
