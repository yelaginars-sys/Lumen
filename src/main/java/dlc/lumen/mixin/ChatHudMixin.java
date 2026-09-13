package dlc.lumen.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dlc.lumen.api.utils.chat.IChatHud;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud {
   @Shadow
   @Final
   private List<ChatHudLine> field_2061;
   @Unique
   private static final int CHORUS_ANIM_TICKS = 5;
   @Unique
   private static final float CHORUS_SLIDE_PX = 6.0F;
   @Unique
   private static final int IRC_FADE_TICKS = 20;
   @Unique
   private static final Map<String, Integer> lumen$ircRemovalTicks = new HashMap<>();
   @Unique
   private String lumen$lastKey = null;
   @Unique
   private int lumen$dupCount = 1;

   @Shadow
   private void method_44813() {
      throw new AssertionError();
   }

   @Override
   public void lumen$scheduleIrcRemoval(String plain, int removalTick) {
      lumen$ircRemovalTicks.put(plain, removalTick);
   }

   @Unique
   private static String lumen$orderedTextToString(OrderedText text) {
      StringBuilder builder = new StringBuilder();
      text.accept((index, style, codePoint) -> {
         builder.appendCodePoint(codePoint);
         return true;
      });
      return builder.toString();
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void lumen$removeExpiredIrcLines(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
      if (!lumen$ircRemovalTicks.isEmpty()) {
         List<String> expired = new ArrayList<>();
         lumen$ircRemovalTicks.entrySet().removeIf(entry -> {
            if (entry.getValue() <= currentTick) {
               expired.add(entry.getKey());
               return true;
            } else {
               return false;
            }
         });
         if (!expired.isEmpty()) {
            boolean changed = this.field_2061.removeIf(line -> expired.contains(line.content().getString()));
            if (changed) {
               this.method_44813();
            }
         }
      }
   }

   @Inject(
      method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void lumen$onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
      if (message != null) {
         String plain = message.getString();
         String lower = plain.toLowerCase(Locale.ROOT);
         if (lower.contains("figura networking api")
            || lower.contains("fetch locale")
            || lower.contains("fetch default locale")
            || lower.contains("cannot proceed to localize")) {
            ci.cancel();
         } else if (!this.field_2061.isEmpty() && plain.equals(this.lumen$lastKey)) {
            this.lumen$dupCount++;
            this.field_2061.remove(0);
            int tick = MinecraftClient.getInstance().inGameHud.getTicks();
            MutableText stacked = message.copy().append(Text.literal(" ").append(Text.literal(this.lumen$dupCount + "x").formatted(Formatting.GRAY)));
            this.field_2061.add(0, new ChatHudLine(tick, stacked, signature, indicator));
            this.method_44813();
            ci.cancel();
         } else {
            this.lumen$lastKey = plain;
            this.lumen$dupCount = 1;
         }
      }
   }

   @WrapOperation(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"
      )
   )
   private int lumen$animateChatLine(
      DrawContext context,
      TextRenderer textRenderer,
      OrderedText text,
      int x,
      int y,
      int color,
      Operation<Integer> original,
      @Local Visible line,
      @Local(argsOnly = true, ordinal = 0) int currentTick
   ) {
      int age = currentTick - line.addedTime();
      if (age >= 0 && age < 5) {
         float t = age / 5.0F;
         float ease = 1.0F - (1.0F - t) * (1.0F - t);
         x -= Math.round((1.0F - ease) * 6.0F);
         int alpha = Math.round((color >>> 24 & 0xFF) * ease);
         color = color & 16777215 | alpha << 24;
      }

      Integer removalTick = lumen$ircRemovalTicks.get(lumen$orderedTextToString(text));
      if (removalTick != null) {
         int remaining = removalTick - currentTick;
         if (remaining > 0 && remaining < 20) {
            int alpha = Math.round((color >>> 24 & 0xFF) * (remaining / 20.0F));
            color = color & 16777215 | alpha << 24;
         }

         if (remaining <= 0) {
            return 0;
         }
      }

      return (Integer)original.call(new Object[]{context, textRenderer, text, x, y, color});
   }
}
