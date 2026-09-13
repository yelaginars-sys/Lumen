package dlc.lumen.mixin;

import dlc.lumen.client.autobuy.AutoBuyEngine;
import dlc.lumen.client.autobuy.AutoBuyOverlay;
import dlc.lumen.client.modules.impl.misc.AuctionHelper;
import dlc.lumen.client.modules.impl.misc.InventoryButtons;
import dlc.lumen.client.modules.impl.player.ItemScroller;
import dlc.lumen.client.modules.impl.render.BetterMinecraft;
import dlc.lumen.client.modules.impl.render.HealHelper;
import dlc.lumen.client.modules.impl.render.ShulkerPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
abstract class HandledScreenMixin {
   @Shadow
   protected int field_2776;
   @Shadow
   protected int field_2800;
   @Shadow
   protected int field_2792;
   @Shadow
   protected int field_2779;
   @Unique
   private long lumen$openedAt = System.currentTimeMillis();
   @Unique
   private long lumen$closedAt;
   @Unique
   private float lumen$closeStartScale = 1.0F;
   @Unique
   private boolean lumen$closing;
   @Unique
   private boolean lumen$forceClose;
   @Unique
   private boolean lumen$scaleApplied;

   @Shadow
   @Nullable
   protected abstract Slot method_64240(double var1, double var3);

   @Shadow
   protected abstract void method_2383(@Nullable Slot var1, int var2, int var3, SlotActionType var4);

   @Shadow
   public abstract void method_25419();

   @Inject(method = "init", at = @At("HEAD"))
   private void lumen$resetOpenAnimation(CallbackInfo ci) {
      this.lumen$openedAt = System.currentTimeMillis();
      this.lumen$closedAt = 0L;
      this.lumen$closeStartScale = 1.0F;
      this.lumen$closing = false;
      this.lumen$forceClose = false;
      this.lumen$scaleApplied = false;
   }

   @Inject(
      method = "renderBackground",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V")
   )
   private void lumen$pushScreenScale(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      this.lumen$scaleApplied = false;
      if (this.lumen$animates()) {
         float scale = this.lumen$scale();
         if (!(Math.abs(scale - 1.0F) < 0.001F)) {
            Screen self = (Screen)(Object)this;
            context.getMatrices().push();
            context.getMatrices().translate(self.width / 2.0F, self.height / 2.0F, 0.0F);
            context.getMatrices().scale(scale, scale, 1.0F);
            context.getMatrices().translate(-self.width / 2.0F, -self.height / 2.0F, 0.0F);
            this.lumen$scaleApplied = true;
         }
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void lumen$popScreenScale(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.lumen$scaleApplied) {
         context.getMatrices().pop();
         this.lumen$scaleApplied = false;
      }

      if (this.lumen$closing && BetterMinecraft.INSTANCE.closeFinished(this.lumen$closedAt)) {
         this.lumen$forceClose = true;
         this.method_25419();
         this.lumen$forceClose = false;
      }
   }

   @Inject(method = "close", at = @At("HEAD"), cancellable = true)
   private void lumen$animateClose(CallbackInfo ci) {
      if (!this.lumen$forceClose && this.lumen$animates()) {
         if (!this.lumen$closing) {
            this.lumen$closeStartScale = this.lumen$scale();
            this.lumen$closedAt = System.currentTimeMillis();
            this.lumen$closing = true;
         }

         ci.cancel();
      }
   }

   @Unique
   private boolean lumen$animates() {
      return BetterMinecraft.INSTANCE.animates((Object)this instanceof InventoryScreen);
   }

   @Unique
   private float lumen$scale() {
      return this.lumen$closing
         ? BetterMinecraft.INSTANCE.closeScale(this.lumen$closedAt, this.lumen$closeStartScale)
         : BetterMinecraft.INSTANCE.openScale(this.lumen$openedAt);
   }

   @Inject(method = "drawSlot", at = @At("HEAD"))
   private void lumen$healHelperHighlight(DrawContext context, Slot slot, CallbackInfo ci) {
      if (slot != null && slot.hasStack()) {
         HealHelper.INSTANCE.renderHighlight(context, slot.getStack(), slot.x, slot.y, 16);
      }
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void lumen$addVanillaInventoryButtons(CallbackInfo ci) {
      HandledScreen<?> self = (HandledScreen<?>)(Object)this;
      if (MinecraftClient.getInstance().player != null) {
         if (!(self instanceof GenericContainerScreen gcs && AutoBuyEngine.isAuctionScreen(gcs))) {
            boolean isChest = InventoryButtons.hasContainerSlots(self);
            int btnY = this.field_2800 - 22;
            int btnHeight = 20;
            if (isChest) {
               int btnWidth = 80;
               int gap = 4;
               int totalWidth = btnWidth * 2 + gap;
               int startX = this.field_2776 + (this.field_2792 - totalWidth) / 2;
               ButtonWidget btnTake = ButtonWidget.builder(Text.literal("Взять всё"), b -> InventoryButtons.takeAll(self, this::method_2383))
                  .dimensions(startX, btnY, btnWidth, btnHeight)
                  .build();
               ButtonWidget btnDrop = ButtonWidget.builder(Text.literal("Выкинуть всё"), b -> InventoryButtons.dropAll(self, this::method_2383))
                  .dimensions(startX + btnWidth + gap, btnY, btnWidth, btnHeight)
                  .build();
               ((ScreenInvoker)this).lumen$addDrawableChild(btnTake);
               ((ScreenInvoker)this).lumen$addDrawableChild(btnDrop);
            } else {
               int btnWidth = 95;
               int startX = this.field_2776 + (this.field_2792 - btnWidth) / 2;
               ButtonWidget btnDrop = ButtonWidget.builder(Text.literal("Выкинуть всё"), b -> InventoryButtons.dropAll(self, this::method_2383))
                  .dimensions(startX, btnY, btnWidth, btnHeight)
                  .build();
               ((ScreenInvoker)this).lumen$addDrawableChild(btnDrop);
            }
         }
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void lumen$renderAutoBuyOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      HandledScreen<?> self = (HandledScreen<?>)(Object)this;
      if (AutoBuyOverlay.shouldShow(self)) {
         AutoBuyOverlay.render(context, this.field_2776, this.field_2800, this.field_2792, this.field_2779, mouseX, mouseY);
      }

      AuctionHelper.render(context, self, this.field_2776, this.field_2800);
   }

   @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
   private void lumen$shulkerPreview(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
      HandledScreen<?> self = (HandledScreen<?>)(Object)this;
      if (self.getScreenHandler().getCursorStack().isEmpty()) {
         Slot slot = this.method_64240(mouseX, mouseY);
         if (slot != null && slot.hasStack()) {
            if (ShulkerPreview.INSTANCE.render(context, slot.getStack(), mouseX, mouseY)) {
               ci.cancel();
            }
         }
      }
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void lumen$autoBuyOverlayClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      HandledScreen<?> self = (HandledScreen<?>)(Object)this;
      if (AutoBuyOverlay.shouldShow(self)
         && AutoBuyOverlay.mouseClicked(mouseX, mouseY, button, this.field_2776, this.field_2800, this.field_2792, this.field_2779)) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void onRenderQuickMove(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ItemScroller itemScroller = ItemScroller.INSTANCE;
      if (itemScroller.isEnable() && mc.player != null && mc.interactionManager != null) {
         long window = mc.getWindow().getHandle();
         boolean leftMousePressed = GLFW.glfwGetMouseButton(window, 0) == 1;
         boolean shiftPressed = GLFW.glfwGetKey(window, 340) == 1 || GLFW.glfwGetKey(window, 344) == 1;
         if (leftMousePressed && shiftPressed) {
            Slot slot = this.method_64240(mouseX, mouseY);
            if (slot != null && slot.hasStack()) {
               if (itemScroller.canQuickMove()) {
                  this.method_2383(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
               }
            }
         } else {
            itemScroller.resetTimer();
         }
      }
   }
}
