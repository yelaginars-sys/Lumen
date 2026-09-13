package dlc.lumen.client.bots.core;

import com.mojang.authlib.GameProfile;
import dlc.lumen.client.bots.core.behavior.ServerBehavior;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud.ChatState;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public final class BotConfigurationNetworkHandler extends BotCommonNetworkHandler implements ClientConfigurationPacketListener, TickablePacketListener {
   private final ClientRegistries clientRegistries = new ClientRegistries();
   private final GameProfile gameProfile;
   private final Immutable immutable;
   private FeatureSet featureSet;
   private ClientDataPackManager clientDataPackManager;
   private ChatState state2;

   BotConfigurationNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState state, BotSession session, ServerBehavior behavior) {
      super(client, connection, state, session, behavior);
      this.gameProfile = state.localGameProfile();
      this.immutable = state.receivedRegistries();
      this.featureSet = state.enabledFeatures() == null ? FeatureFlags.DEFAULT_ENABLED_FEATURES : state.enabledFeatures();
      this.state2 = state.chatState();
   }

   @Override
   public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
   }

   @Override
   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.clientRegistries.putTags(packet.getGroups());
   }

   @Override
   public void onFeatures(FeaturesS2CPacket packet) {
      this.featureSet = FeatureFlags.FEATURE_MANAGER.featureSetOf(packet.features());
   }

   @Override
   public void onSelectKnownPacks(SelectKnownPacksS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      if (this.clientDataPackManager == null) {
         this.clientDataPackManager = new ClientDataPackManager();
      }

      List var2 = this.clientDataPackManager.getCommonKnownPacks(packet.knownPacks());
      this.sendPacket(new SelectKnownPacksC2SPacket(var2));
   }

   @Override
   public void onResetChat(ResetChatS2CPacket packet) {
      this.state2 = null;
   }

   @Override
   public void onReady(ReadyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      Immutable var2 = this.helper(factory -> this.clientRegistries.createRegistryManager(factory, this.immutable, this.connection.isLocal()));
      BotPlayNetworkHandler var3 = new BotPlayNetworkHandler(
         this.client,
         this.connection,
         new ClientConnectionState(
            this.gameProfile,
            this.worldSession,
            var2,
            this.featureSet,
            this.brand,
            this.serverInfo,
            this.postDisconnectScreen,
            this.serverCookies,
            this.state2,
            this.customReportDetails,
            this.serverLinks
         ),
         this.session,
         this.behavior
      );
      this.session.setPlayHandler(var3);
      this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(var2)), var3);
      this.connection.send(ReadyC2SPacket.INSTANCE);
      this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(var2)));
   }

   private <T> T helper(Function<ResourceFactory, T> opener) {
      if (this.clientDataPackManager == null) {
         return (T)opener.apply(ResourceFactory.MISSING);
      }

      try (LifecycledResourceManager var2 = this.clientDataPackManager.createResourceManager()) {
         return (T)opener.apply(var2);
      }
   }

   @Override
   public void tick() {
   }
}
