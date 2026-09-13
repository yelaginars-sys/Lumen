package dlc.lumen.mixin;

import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.utils.network.NetworkUtils;
import dlc.lumen.api.utils.network.ProxyManager;
import dlc.lumen.client.bots.core.BotPacketEventDispatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.PacketSizeLogger;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
   @Unique
   private static final String LUMEN_BOT_PACKAGE = "dlc.lumen.client.bots.core.";
   @Shadow
   private PacketListener field_11652;

   @Inject(method = "addHandlers", at = @At("HEAD"))
   private static void lumen$applyProxy(ChannelPipeline pipeline, NetworkSide side, boolean local, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
      try {
         if (side != NetworkSide.CLIENTBOUND) {
            return;
         }

         if (!(pipeline.channel() instanceof SocketChannel)) {
            return;
         }

         if (pipeline.get("lumen_proxy") != null) {
            return;
         }

         ChannelHandler handler = ProxyManager.buildHandler();
         if (handler != null) {
            pipeline.addFirst("lumen_proxy", handler);
         }
      } catch (Throwable var6) {
      }
   }

   @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (!this.lumen$isBotConnection()) {
         EventPacket eventReceive = new EventPacket(packet, EventPacket.Type.RECEIVE);
         EventInvoker.invoke(eventReceive);
         if (eventReceive.isCancelled()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
   public void send(Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (this.field_11652 instanceof BotPacketEventDispatcher dispatcher) {
         if (dispatcher.handleSendPacketEvent(packet) == null) {
            ci.cancel();
         } else {
            dispatcher.handlePostSendPacketEvent(packet);
         }
      } else if (!this.lumen$isBotConnection()) {
         if (NetworkUtils.getSilentPackets().contains(packet)) {
            NetworkUtils.getSilentPackets().remove(packet);
         } else {
            EventPacket eventSend = new EventPacket(packet, EventPacket.Type.SEND);
            EventInvoker.invoke(eventSend);
            if (eventSend.isCancelled()) {
               ci.cancel();
            }

            if (!eventSend.isCancelled()) {
               this.lumen$syncSelectedSlot(packet);
            }
         }
      }
   }

   @Unique
   private boolean lumen$isBotConnection() {
      return this.field_11652 != null && this.field_11652.getClass().getName().startsWith("dlc.lumen.client.bots.core.");
   }

   private void lumen$syncSelectedSlot(Packet<?> packet) {
      if (packet instanceof UpdateSelectedSlotC2SPacket slotPacket) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc != null && mc.player != null && mc.interactionManager != null) {
            int slot = slotPacket.getSelectedSlot();
            if (mc.player.getInventory().selectedSlot == slot) {
               if (mc.interactionManager instanceof IClientInteractionManagerAccessor acc) {
                  acc.lumen$setLastSelectedSlot(slot);
               }
            }
         }
      }
   }
}
