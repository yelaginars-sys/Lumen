package dlc.lumen.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import dlc.lumen.Lumen;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
   @Final
   @Shadow
   TextFieldWidget field_21599;
   @Shadow
   boolean field_21614;
   @Shadow
   private ParseResults<CommandSource> field_21610;
   @Shadow
   private CompletableFuture<Suggestions> field_21611;
   @Shadow
   private SuggestionWindow field_21612;

   @Shadow
   public abstract void method_23920(boolean var1);

   @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true)
   public void refresh(CallbackInfo ci, @Local StringReader reader) {
      String prefix = Lumen.INSTANCE.commandStorage.getPrefix();
      if (reader.canRead(prefix.length()) && reader.getString().startsWith(prefix, reader.getCursor())) {
         reader.setCursor(reader.getCursor() + prefix.length());
         CommandDispatcher<CommandSource> dispatcher = Lumen.INSTANCE.commandStorage.getDispatcher();
         if (this.field_21610 == null) {
            this.field_21610 = dispatcher.parse(reader, Lumen.INSTANCE.commandStorage.getSource());
         }

         int cursor;
         if ((cursor = this.field_21599.getCursor()) >= 1 && (this.field_21612 == null || !this.field_21614)) {
            this.field_21611 = dispatcher.getCompletionSuggestions(this.field_21610, cursor);
            this.field_21611.thenRun(() -> {
               if (this.field_21611.isDone()) {
                  this.method_23920(false);
               }
            });
         }

         ci.cancel();
      }
   }
}
