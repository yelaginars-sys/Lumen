package dlc.lumen.client.bots.core;

import dlc.lumen.client.bots.core.behavior.ServerBehavior;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomReportDetailsS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Identifier;

abstract class BotCommonNetworkHandler implements ClientCommonPacketListener, BotPacketEventDispatcher {
   private static final ScheduledExecutorService value = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread var1 = new Thread(runnable, "bot-resourcepack");
      var1.setDaemon(true);
      return var1;
   });
   protected final MinecraftClient client;
   protected final ClientConnection connection;
   protected final ServerInfo serverInfo;
   protected final WorldSession worldSession;
   protected final Screen postDisconnectScreen;
   protected final Map<Identifier, byte[]> serverCookies;
   protected final BotSession session;
   protected final ServerBehavior behavior;
   protected String brand;
   protected Map<String, String> customReportDetails;
   protected ServerLinks serverLinks;

   BotCommonNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState state, BotSession session, ServerBehavior behavior) {
      this.client = client;
      this.connection = connection;
      this.serverInfo = state.serverInfo();
      this.brand = state.serverBrand();
      this.worldSession = state.worldSession();
      this.postDisconnectScreen = state.postDisconnectScreen();
      this.serverCookies = state.serverCookies();
      this.customReportDetails = state.customReportDetails();
      this.serverLinks = state.serverLinks();
      this.session = session;
      this.behavior = behavior;
   }

   @Override
   public boolean isConnectionOpen() {
      return this.connection.isOpen();
   }

   @Override
   public void onKeepAlive(KeepAliveS2CPacket packet) {
      this.sendPacket(new KeepAliveC2SPacket(packet.getId()));
   }

   @Override
   public void onPing(CommonPingS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.sendPacket(new CommonPongC2SPacket(packet.getParameter()));
   }

   @Override
   public void onCustomPayload(CustomPayloadS2CPacket packet) {
      CustomPayload var2 = packet.payload();
      if (!(var2 instanceof UnknownCustomPayload)) {
         NetworkThreadUtils.forceMainThread(packet, this, this.client);
         if (var2 instanceof BrandCustomPayload var3) {
            this.brand = var3.brand();
            this.worldSession.setBrand(var3.brand());
         }
      }
   }

   @Override
   public void onDisconnect(DisconnectS2CPacket packet) {
      this.connection.disconnect(packet.reason());
   }

   @Override
   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
   }

   @Override
   public void onCookieRequest(CookieRequestS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.connection.send(new CookieResponseC2SPacket(packet.key(), this.serverCookies.get(packet.key())));
   }

   @Override
   public void onStoreCookie(StoreCookieS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.serverCookies.put(packet.key(), packet.payload());
   }

   @Override
   public void onCustomReportDetails(CustomReportDetailsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.customReportDetails = packet.details();
   }

   @Override
   public void onServerLinks(ServerLinksS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.serverLinks = ServerLinks.EMPTY;
   }

   @Override
   public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.behavior.resourcePack().onResourcePackSend(packet, this.connection, value);
   }

   @Override
   public void onResourcePackRemove(ResourcePackRemoveS2CPacket packet) {
   }

   @Override
   public void onServerTransfer(ServerTransferS2CPacket packet) {
      this.behavior.transfer().onTransfer(packet, this.connection);
   }

   @Override
   public void onDisconnected(DisconnectionInfo info) {
      this.worldSession.onUnload();
      this.session.onDisconnected(info.reason().getString());
   }

   public void sendPacket(Packet<?> packet) {
      this.connection.send(packet);
   }

   @Override
   public Packet<?> handleSendPacketEvent(Packet<?> packet) {
      return packet;
   }

   @Override
   public void handlePostSendPacketEvent(Packet<?> packet) {
   }
}
