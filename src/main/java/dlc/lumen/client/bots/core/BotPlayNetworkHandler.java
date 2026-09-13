package dlc.lumen.client.bots.core;

import dlc.lumen.client.bots.BotTasks;
import dlc.lumen.client.bots.core.behavior.CaptchaStrategy;
import dlc.lumen.client.bots.core.behavior.ServerBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class BotPlayNetworkHandler extends BotCommonNetworkHandler implements ClientPlayPacketListener, TickablePacketListener {
   private final ClientConnectionState state2;
   private final CaptchaStrategy captchaStrategy;
   private final BotVisualWorld botVisualWorld;
   private final BotInputController botInputController;
   private boolean flag;
   private long timestamp;

   BotPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState state, BotSession session, ServerBehavior behavior) {
      super(client, connection, state, session, behavior);
      this.state2 = state;
      this.captchaStrategy = behavior.createCaptcha(session);
      this.botVisualWorld = new BotVisualWorld(client, connection, state, session);
      this.botInputController = new BotInputController(client, session.inputState());
   }

   @Override
   public void tick() {
      this.botVisualWorld.tickVisualHandler();
      this.updateState();
      if (this.flag) {
         this.botVisualWorld.markPlayerLoaded();
      }

      if (!BotManager.INSTANCE.isViewingBot(this.session) && this.session.isJoined()) {
         this.botVisualWorld.ensureReady();
         if (this.botVisualWorld.hasPlayerAndWorld()) {
            this.botInputController.setUse(false);

            try {
               this.botInputController
                  .runWithKeyState(
                     () -> this.botVisualWorld
                        .runBackgroundTick(
                           this.botInputController.backgroundInput(),
                           () -> BotViewRenderer.INSTANCE.hasFrame(this.session),
                           () -> BotTasks.INSTANCE.tick(this.session),
                           this.botInputController::handleActions
                        )
                  );
            } catch (Throwable var2) {
               BotLog.log("[" + this.session.getName() + "] сбой в тике: " + var2);
            }
         }

         this.sendPacket(ClientTickEndC2SPacket.INSTANCE);
      }
   }

   @Override
   public void onGameJoin(GameJoinS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      if (BotManager.INSTANCE.getControlled() == this.session) {
         BotManager.INSTANCE.suspendControlForTransition(this.session);
      }

      this.botVisualWorld.clearState();
      this.botVisualWorld.setChunkLoadDistance(packet.viewDistance());
      this.botVisualWorld.setJoinPacket(packet);
      this.session.setState(BotSession.State.PLAY);
      this.session.logChat("joined");
      this.captchaStrategy.onJoin();
      this.flag = false;
      this.timestamp = System.currentTimeMillis();
      this.botVisualWorld.ensureReady();
   }

   private void updateState() {
      if (!this.flag && this.session.isJoined() && this.botVisualWorld.hasServerPosition()) {
         if (System.currentTimeMillis() - this.timestamp >= 500L) {
            this.sendPacket(new PlayerLoadedC2SPacket());
            this.flag = true;
         }
      }
   }

   @Override
   public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      Vec3d var2 = packet.change().position();
      this.captchaStrategy.onLook(var2.x, var2.y, var2.z, packet.change().yaw());
      if (!this.botVisualWorld.isReady()) {
         PlayerPosition var3 = this.botVisualWorld.applyServerPositionLook(packet);
         Vec3d var4 = var3.position();
         this.sendPacket(new TeleportConfirmC2SPacket(packet.teleportId()));
         this.sendPacket(new Full(var4.x, var4.y, var4.z, var3.yaw(), var3.pitch(), false, false));
      } else {
         this.updateState2(packet);
      }
   }

   boolean enterView() {
      return this.botVisualWorld.enterView();
   }

   void exitView() {
      this.botVisualWorld.exitView();
   }

   ClientWorld visualWorld() {
      return this.botVisualWorld.visualWorld();
   }

   ClientPlayerEntity visualPlayer() {
      return this.botVisualWorld.visualPlayer();
   }

   Entity visualCamera() {
      return this.botVisualWorld.visualCamera();
   }

   ClientPlayerInteractionManager visualInteraction() {
      return this.botVisualWorld.visualInteraction();
   }

   Screen visualScreen() {
      return this.botVisualWorld.visualScreen();
   }

   boolean runWithVisualScreen(Runnable action) {
      return this.botVisualWorld.runWithVisualScreen(action);
   }

   boolean isVisualHandler(Object handler) {
      return this.botVisualWorld.isVisualHandler(handler);
   }

   private void updateState2(Packet<? super ClientPlayPacketListener> packet) {
      this.updateState4(packet, false);
   }

   private void updateState3(Packet<? super ClientPlayPacketListener> packet) {
      if (BotManager.INSTANCE.getControlled() == this.session) {
         this.updateState2(packet);
      }
   }

   private void updateState4(Packet<? super ClientPlayPacketListener> packet, boolean restoreRenderer) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      if (this.botVisualWorld.isReady() && this.botVisualWorld.hasPlayerAndWorld()) {
         this.botVisualWorld.apply(packet, restoreRenderer);
      } else {
         this.botVisualWorld.queue(packet);
      }
   }

   boolean runAsBot(Runnable action) {
      return this.botVisualWorld.runAsBot(action);
   }

   @Override
   public void onPlayerRotation(PlayerRotationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      if (!this.botVisualWorld.isReady()) {
         this.botVisualWorld.applyServerRotation(packet);
      } else {
         this.updateState2(packet);
      }
   }

   @Override
   public void onGameMessage(GameMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.session.logChat(packet.content().getString());
      this.session.addHudMessage(packet.content());
      this.captchaStrategy.onText(packet.content().getString());
      this.updateState5(packet.content());
      if (BotManager.INSTANCE.getControlled() == this.session) {
         this.updateState2(packet);
      }
   }

   @Override
   public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      this.session.logChat(packet.message().getString());
      this.session.addHudMessage(packet.message());
      this.captchaStrategy.onText(packet.message().getString());
      this.updateState5(packet.message());
      if (BotManager.INSTANCE.getControlled() == this.session) {
         this.updateState2(packet);
      }
   }

   private void updateState5(Text message) {
      if (message != null) {
         this.session.fireChat(message);
      }
   }

   @Override
   public void onChatMessage(ChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      if (packet.unsignedContent() != null) {
         this.session.logChat(packet.unsignedContent().getString());
         this.session.addHudMessage(packet.unsignedContent());
      }

      if (BotManager.INSTANCE.getControlled() == this.session) {
         this.updateState2(packet);
      }
   }

   @Override
   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPingResult(PingResultS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntitySpawn(EntitySpawnS2CPacket packet) {
      if (packet.getEntityType() == EntityType.ITEM_FRAME || packet.getEntityType() == EntityType.GLOW_ITEM_FRAME) {
         this.captchaStrategy.onFrameSpawn(packet.getEntityId(), packet.getX(), packet.getY(), packet.getZ(), packet.getEntityData());
      }

      this.updateState2(packet);
   }

   @Override
   public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityAnimation(EntityAnimationS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onDamageTilt(DamageTiltS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onStatistics(StatisticsS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onRecipeBookAdd(RecipeBookAddS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onBlockEvent(BlockEventS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onBlockUpdate(BlockUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onRemoveMessage(RemoveMessageS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onMapUpdate(MapUpdateS2CPacket packet) {
      this.captchaStrategy.onMapData(packet.mapId().id(), packet.updateData().orElse(null));
      this.updateState2(packet);
   }

   @Override
   public void onCloseScreen(CloseScreenS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onInventory(InventoryS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onOpenHorseScreen(OpenHorseScreenS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityStatus(EntityStatusS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityAttach(EntityAttachS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onExplosion(ExplosionS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onGameStateChange(GameStateChangeS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onChunkData(ChunkDataS2CPacket packet) {
      this.updateState2(packet);
      if (this.client.isOnThread()) {
         ClientWorld var2 = this.botVisualWorld.visualWorld();
         BotLog.log(
            "["
               + this.session.getName()
               + "] chunk "
               + packet.getChunkX()
               + ","
               + packet.getChunkZ()
               + " applied, loaded="
               + (var2 == null ? "no-world" : var2.getChunkManager().getLoadedChunkCount())
         );
      }
   }

   @Override
   public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onUnloadChunk(UnloadChunkS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldEvent(WorldEventS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntity(EntityS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onParticle(ParticleS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlayerRemove(PlayerRemoveS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlayerList(PlayerListS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
      this.updateState4(packet, true);
   }

   @Override
   public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
      for (SerializedEntry var3 : packet.trackedValues()) {
         if (var3.value() instanceof ItemStack var4) {
            MapIdComponent var6 = var4.get(DataComponentTypes.MAP_ID);
            if (var6 != null) {
               this.captchaStrategy.onFrameItem(packet.id(), var6.id());
            }
         }
      }

      this.updateState2(packet);
   }

   @Override
   public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onHealthUpdate(HealthUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onTeam(TeamS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlaySound(PlaySoundS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityPositionSync(EntityPositionSyncS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityPosition(EntityPositionS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onUpdateTickRate(UpdateTickRateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onTickStep(TickStepS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityAttributes(EntityAttributesS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEndCombat(EndCombatS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEnterCombat(EnterCombatS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onDeathMessage(DeathMessageS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onDifficulty(DifficultyS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onBossBar(BossBarS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onVehicleMove(VehicleMoveS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onAdvancements(AdvancementUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onCommandTree(CommandTreeS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onStopSound(StopSoundS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onLookAt(LookAtS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onLightUpdate(LightUpdateS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onOpenScreen(OpenScreenS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
      this.botVisualWorld.setChunkLoadDistance(packet.getDistance());
      this.updateState2(packet);
   }

   @Override
   public void onSimulationDistance(SimulationDistanceS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onOverlayMessage(OverlayMessageS2CPacket packet) {
      this.captchaStrategy.onText(packet.text().getString());
      this.updateState3(packet);
   }

   @Override
   public void onSubtitle(SubtitleS2CPacket packet) {
      this.captchaStrategy.onText(packet.text().getString());
      this.updateState3(packet);
   }

   @Override
   public void onTitle(TitleS2CPacket packet) {
      this.captchaStrategy.onText(packet.text().getString());
      this.updateState3(packet);
   }

   @Override
   public void onTitleFade(TitleFadeS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onTitleClear(ClearTitleS2CPacket packet) {
      this.updateState3(packet);
   }

   @Override
   public void onServerMetadata(ServerMetadataS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onChatSuggestions(ChatSuggestionsS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onBundle(BundleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);

      for (Packet var3 : packet.getPackets()) {
         var3.apply(this);
      }
   }

   @Override
   public void onEntityDamage(EntityDamageS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onEnterReconfiguration(EnterReconfigurationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client);
      if (BotManager.INSTANCE.getControlled() == this.session) {
         BotManager.INSTANCE.suspendControlForTransition(this.session);
      }

      this.botVisualWorld.clearState();
      this.session.setState(BotSession.State.CONFIG);
      BotConfigurationNetworkHandler var2 = new BotConfigurationNetworkHandler(this.client, this.connection, this.state2, this.session, this.behavior);
      this.connection.transitionInbound(ConfigurationStates.S2C, var2);
      this.connection.send(AcknowledgeReconfigurationC2SPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationStates.C2S);
   }

   @Override
   public void onStartChunkSend(StartChunkSendS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onChunkSent(ChunkSentS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onDebugSample(DebugSampleS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onProjectilePower(ProjectilePowerS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSetCursorItem(SetCursorItemS2CPacket packet) {
      this.updateState2(packet);
   }

   @Override
   public void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet) {
      this.updateState2(packet);
   }
}
