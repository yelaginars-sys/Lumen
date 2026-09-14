package dlc.lumen.mixin;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.render.blur.BlurProgram;
import dlc.lumen.client.modules.impl.render.HealHelper;
import dlc.lumen.client.modules.impl.render.Interface;
import dlc.lumen.client.modules.impl.render.MotionFps;
import dlc.lumen.client.modules.impl.render.Removals;
import dlc.lumen.client.modules.impl.render.base.implement.ScoreBoardHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameGuiMixin implements QClient {
   @Shadow
   @Final
   private MinecraftClient field_2035;

   @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
   private void renderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
      Removals removals = ModuleClass.removals;
      if (removals != null && removals.isVignetteDisabled()) {
         ci.cancel();
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      MotionFps.INSTANCE.onHudStart();
      BlurProgram.getInstance().beginFrame();
      if (EventInvoker.hasListeners(EventRender.Default.class)) {
         new EventRender.Default(context, tickCounter.getTickDelta(true)).call();
      }
   }

   @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
   private void lumen$renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null && ModuleClass.customCrosshair != null && ModuleClass.customCrosshair.isEnable()) {
         ci.cancel();
      }
   }

   @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
   private void lumen$renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null && ModuleClass.interfaceModule != null) {
         Interface interfaceModule = ModuleClass.interfaceModule;
         if (interfaceModule.isEnable() && interfaceModule.isHudElementVisible("potions")) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
   private void lumen$renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null && ModuleClass.interfaceModule != null) {
         Interface interfaceModule = ModuleClass.interfaceModule;
         if (interfaceModule.isEnable() && interfaceModule.isHudElementVisible("hotbarHUD")) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "renderHotbar", at = @At("TAIL"))
   private void lumen$healHelperHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      PlayerEntity player = this.method_1737();
      if (player != null) {
         HealHelper heal = HealHelper.INSTANCE;
         if (heal.isEnable()) {
            int i = context.getScaledWindowWidth() / 2;
            int y = context.getScaledWindowHeight() - 19;

            for (int slot = 0; slot < 9; slot++) {
               int x = i - 90 + slot * 20 + 2;
               heal.renderHighlight(context, player.getInventory().getStack(slot), x, y, 16);
            }
         }
      }
   }

   @Shadow
   private PlayerEntity method_1737() {
      return null;
   }

   @Inject(
      method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void lumen$renderPatchedScoreboard(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
      if (this.lumen$shouldUseCustomScoreboardHud()) {
         ci.cancel();
      } else if (this.lumen$shouldPatchScoreboard()) {
         ScoreBoardHUD.renderVanillaAnchored(drawContext, this.field_2035, objective);
         ci.cancel();
      }
   }

   private boolean lumen$shouldUseCustomScoreboardHud() {
      return ModuleClass.INSTANCE != null
         && ModuleClass.interfaceModule != null
         && ModuleClass.interfaceModule.isEnable()
         && ModuleClass.interfaceModule.isHudElementVisible("scoreBoardHUD");
   }

   private boolean lumen$shouldPatchScoreboard() {
      return ModuleClass.INSTANCE != null && ModuleClass.nameProtect != null && ModuleClass.nameProtect.isEnable();
   }
}
