package dlc.lumen.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.utils.baritone.BaritoneAntiStuck;
import dlc.lumen.api.utils.bot.BotSessionManager;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import dlc.lumen.client.modules.impl.misc.AutoZamok;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
   @Shadow
   private ClientWorld field_3699;

   @Shadow
   public abstract PlayerListEntry method_2871(UUID var1);

   @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
   public void sendChatMessage(@NotNull String message, CallbackInfo ci) {
      if (message.startsWith(Lumen.INSTANCE.commandStorage.getPrefix())) {
         try {
            Lumen.INSTANCE
               .commandStorage
               .getDispatcher()
               .execute(message.substring(Lumen.INSTANCE.commandStorage.getPrefix().length()), Lumen.INSTANCE.commandStorage.getSource());
         } catch (CommandSyntaxException e) {
            ChatUtils.sendMessage(Formatting.RED + "Ошибка в использовании!");
         }

         ci.cancel();
      }
   }

   @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
   private void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
      EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
      event.call();
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
   private void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
      EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
      event.call();
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(method = "onEntityPositionSync", at = @At("HEAD"), cancellable = true)
   private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (this.field_3699 == null || mc.player == null || mc.world == null) {
         ci.cancel();
      }
   }

   @Inject(method = "onGameMessage", at = @At("HEAD"))
   private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
      String content = packet.content().getString();
      BaritoneAntiStuck.onGameMessage(content);
      AutoZamok.onGameMessage(content);
      AutoMine.onGameMessage(content);
   }

   @Inject(method = "onGameJoin", at = @At("HEAD"))
   private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
      BotSessionManager.finishBotConnectStage();
   }

   @Inject(method = "onPlayerList", at = @At("HEAD"))
   private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
      EnumSet<Action> actions = packet.getActions();
      if (actions.contains(Action.UPDATE_DISPLAY_NAME) && !actions.contains(Action.ADD_PLAYER)) {
         for (Entry entry : packet.getEntries()) {
            if (this.method_2871(entry.profileId()) == null) {
               AutoMine.onProtectedRegionHit();
               break;
            }
         }
      }
   }

   @Inject(method = "onUpdateSelectedSlot", at = @At("TAIL"))
   private void lumen$onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc != null && mc.interactionManager instanceof IClientInteractionManagerAccessor acc) {
         acc.lumen$setLastSelectedSlot(packet.slot());
      }
   }
}
