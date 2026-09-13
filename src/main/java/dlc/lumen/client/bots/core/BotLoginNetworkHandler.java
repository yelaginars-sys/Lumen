package dlc.lumen.client.bots.core;

import com.mojang.authlib.GameProfile;
import dlc.lumen.client.bots.core.behavior.ServerBehavior;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.ServerLinks;

public final class BotLoginNetworkHandler extends ClientLoginNetworkHandler {
   private final ClientConnection clientConnection;
   private final MinecraftClient minecraftClient;
   private final BotSession botSession;
   private final ServerBehavior serverBehavior;

   BotLoginNetworkHandler(ClientConnection connection, MinecraftClient client, BotSession session, ServerBehavior behavior) {
      super(connection, client, null, null, false, null, text -> session.setState(BotSession.State.LOGIN), null);
      this.clientConnection = connection;
      this.minecraftClient = client;
      this.botSession = session;
      this.serverBehavior = behavior;
   }

   @Override
   public void onSuccess(LoginSuccessS2CPacket packet) {
      this.botSession.setState(BotSession.State.CONFIG);
      GameProfile var2 = packet.profile();
      ClientConnectionState var3 = new ClientConnectionState(
         var2,
         this.minecraftClient.getTelemetryManager().createWorldSession(false, null, null),
         ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(),
         FeatureFlags.DEFAULT_ENABLED_FEATURES,
         null,
         null,
         null,
         new HashMap<>(),
         null,
         Map.of(),
         ServerLinks.EMPTY
      );
      BotConfigurationNetworkHandler var4 = new BotConfigurationNetworkHandler(
         this.minecraftClient, this.clientConnection, var3, this.botSession, this.serverBehavior
      );
      this.clientConnection.transitionInbound(ConfigurationStates.S2C, var4);
      this.clientConnection.send(EnterConfigurationC2SPacket.INSTANCE);
      this.clientConnection.transitionOutbound(ConfigurationStates.C2S);
      this.clientConnection.send(new CustomPayloadC2SPacket(new BrandCustomPayload(ClientBrandRetriever.getClientModName())));
      this.clientConnection.send(new ClientOptionsC2SPacket(this.minecraftClient.options.getSyncedOptions()));
   }

   @Override
   public void onDisconnected(DisconnectionInfo info) {
      this.botSession.onDisconnected(info.reason().getString());
   }

   @Override
   public void onDisconnect(LoginDisconnectS2CPacket packet) {
      this.clientConnection.disconnect(packet.getReason());
   }
}
