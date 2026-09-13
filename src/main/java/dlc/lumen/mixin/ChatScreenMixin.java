package dlc.lumen.mixin;

import dlc.lumen.Lumen;
import dlc.lumen.api.storages.implement.DragStorage;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.chat.ChatInputRenderer;
import dlc.lumen.api.utils.draggable.Draggable;
import java.util.function.BiFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.OrderedText;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
   @Unique
   private boolean lumen$leftPressed;
   @Unique
   private static final float CHORUS_OPEN_MS = 160.0F;
   @Unique
   private static final float CHORUS_OPEN_SLIDE = 14.0F;
   @Unique
   private long lumen$openTime;

   @Inject(method = "init", at = @At("TAIL"))
   private void lumen$onInit(CallbackInfo ci) {
      this.lumen$openTime = System.currentTimeMillis();
      this.lumen$installInputMask();
   }

   @Unique
   private void lumen$installInputMask() {
      TextFieldWidget chatField = ((ChatScreenAccessor)this).lumen$getChatField();
      if (chatField != null) {
         BiFunction<String, Integer, OrderedText> current = ((TextFieldWidgetAccessor)chatField).lumen$getRenderTextProvider();
         if (!(current instanceof ChatInputRenderer)) {
            chatField.setRenderTextProvider(new ChatInputRenderer(chatField, current));
         }
      }
   }

   @Unique
   private float lumen$openOffset() {
      float p = (float)(System.currentTimeMillis() - this.lumen$openTime) / 160.0F;
      if (p >= 1.0F) {
         return 0.0F;
      }

      if (p < 0.0F) {
         p = 0.0F;
      }

      float ease = 1.0F - (1.0F - p) * (1.0F - p);
      return (1.0F - ease) * 14.0F;
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void lumen$widgetKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleClass.interfaceModule.handleWidgetKey(keyCode, scanCode, modifiers)) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleClass.interfaceModule.handleMusicClick(mouseX, mouseY, button)) {
         cir.setReturnValue(true);
      } else if (ModuleClass.interfaceModule.handleWidgetClick(mouseX, mouseY, button)) {
         cir.setReturnValue(true);
      } else if (ModuleClass.interfaceModule.handleHudContextClick(mouseX, mouseY, button)) {
         cir.setReturnValue(true);
      } else {
         for (Draggable draggable : DragStorage.draggables.values()) {
            if (draggable.getModule().isEnable() && draggable.onClick(mouseX, mouseY, button)) {
               cir.setReturnValue(true);
               return;
            }
         }
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      Window window = mc.getWindow();
      boolean leftPressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) == 1;
      if (this.lumen$leftPressed && !leftPressed) {
         for (Draggable draggable : DragStorage.draggables.values()) {
            draggable.onRelease(0);
         }

         ModuleClass.interfaceModule.releaseHudContextSlider();
      }

      this.lumen$leftPressed = leftPressed;

      for (Draggable draggable : DragStorage.draggables.values()) {
         if (draggable.getModule().isEnable()) {
            draggable.onDraw(mouseX, mouseY, window, context.getMatrices());
         }
      }

      ModuleClass.interfaceModule.renderHudContextMenu(context, mouseX, mouseY);
      context.getMatrices().push();
      context.getMatrices().translate(0.0F, this.lumen$openOffset(), 0.0F);
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void lumen$popOpenAnim(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      context.getMatrices().pop();
   }

   @Inject(method = "removed", at = @At("HEAD"))
   private void onRemoved(CallbackInfo ci) {
      this.lumen$leftPressed = false;

      for (Draggable draggable : DragStorage.draggables.values()) {
         draggable.onRelease(0);
      }

      ModuleClass.interfaceModule.releaseHudContextSlider();
      ModuleClass.interfaceModule.releaseWidgetFocus();

      try {
         Lumen.INSTANCE.configStorage.saveConfig(Lumen.INSTANCE.configStorage.currentConfig);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
