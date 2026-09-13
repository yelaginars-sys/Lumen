package dlc.lumen.client.bots.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.util.math.Vec3d;

final class BotVisualWorld {
   private static final int INDEX = 4096;
   private static final PlayerPosition PLAYER_POSITION = new PlayerPosition(Vec3d.ZERO, Vec3d.ZERO, 0.0F, 0.0F);
   private final MinecraftClient minecraftClient;
   private final ClientConnection clientConnection;
   private final ClientConnectionState state2;
   private final BotSession botSession;
   private final Deque<Packet<? super ClientPlayPacketListener>> arrayDeque = new ArrayDeque<>();
   private GameJoinS2CPacket gameJoinS2CPacket;
   private ClientWorld clientWorld;
   private ClientPlayerEntity clientPlayerEntity;
   private ClientPlayerInteractionManager clientPlayerInteractionManager;
   private ClientPlayNetworkHandler clientPlayNetworkHandler;
   private Entity entity2;
   private Screen screen2;
   private PlayerPosition playerPosition = PLAYER_POSITION;
   private int index = 3;
   private boolean flag;
   private boolean flag2;
   private long timestamp;

   BotVisualWorld(MinecraftClient client, ClientConnection connection, ClientConnectionState state, BotSession session) {
      this.minecraftClient = client;
      this.clientConnection = connection;
      this.state2 = state;
      this.botSession = session;
   }

   boolean isReady() {
      return this.clientPlayNetworkHandler != null;
   }

   boolean hasPlayerAndWorld() {
      return this.clientPlayerEntity != null && this.clientWorld != null;
   }

   boolean hasServerPosition() {
      return this.flag;
   }

   void setJoinPacket(GameJoinS2CPacket packet) {
      this.gameJoinS2CPacket = packet;
   }

   void setChunkLoadDistance(int distance) {
      this.index = distance;
   }

   void queue(Packet<? super ClientPlayPacketListener> packet) {
      if (this.arrayDeque.size() >= 4096) {
         this.arrayDeque.pollFirst();
      }

      this.arrayDeque.add(packet);
   }

   void apply(Packet<? super ClientPlayPacketListener> packet, boolean restoreRenderer) {
      this.updateState2(() -> packet.apply(this.clientPlayNetworkHandler), restoreRenderer);
   }

   boolean ensureReady() {
      if (this.clientPlayNetworkHandler != null) {
         return true;
      }

      if (this.gameJoinS2CPacket == null) {
         return false;
      }

      if (System.currentTimeMillis() < this.timestamp) {
         return false;
      }

      PlayerPosition var1 = this.playerPosition;
      boolean var2 = this.flag;
      this.clientPlayNetworkHandler = new ClientPlayNetworkHandler(this.minecraftClient, this.clientConnection, this.state2);

      try {
         this.apply(this.gameJoinS2CPacket, true);
      } catch (Exception var7) {
         this.clientPlayNetworkHandler = null;
         this.timestamp = System.currentTimeMillis() + 2000L;
         BotLog.log("[" + this.botSession.getName() + "] visual join failed: " + var7);
         return false;
      }

      if (this.clientWorld != null && this.clientPlayerEntity != null) {
         this.playerPosition = var1;
         this.flag = var2;
         this.updateState5();

         for (Packet var4 : List.copyOf(this.arrayDeque)) {
            try {
               this.apply(var4, false);
            } catch (Exception var6) {
               BotLog.log("[" + this.botSession.getName() + "] visual replay failed: " + var4.getClass().getSimpleName() + ": " + var6);
            }
         }

         this.arrayDeque.clear();
         return this.clientWorld != null && this.clientPlayerEntity != null && this.clientPlayerInteractionManager != null;
      } else {
         this.clientPlayNetworkHandler = null;
         this.timestamp = System.currentTimeMillis() + 2000L;
         BotLog.log(
            "["
               + this.botSession.getName()
               + "] visual join incomplete: world="
               + (this.clientWorld != null)
               + " player="
               + (this.clientPlayerEntity != null)
         );
         return false;
      }
   }

   void markPlayerLoaded() {
      if (this.clientPlayerEntity != null) {
         this.clientPlayerEntity.setLoaded(true);
      }
   }

   void tickVisualHandler() {
      if (this.clientPlayNetworkHandler != null && this.clientWorld != null) {
         this.updateState(this.clientPlayNetworkHandler::tick);
      }
   }

   void runBackgroundTick(Input backgroundInput, BooleanSupplier hasFrame, Runnable moduleTick, Runnable inputActions) {
      this.updateState(() -> {
         this.clientPlayerEntity.input = backgroundInput;
         this.minecraftClient.currentScreen = this.computeScreen();

         try {
            moduleTick.run();
         } finally {
            this.screen2 = computereturn(this.minecraftClient.currentScreen);
            this.minecraftClient.currentScreen = this.flag2 ? this.computeScreen() : null;
         }

         this.clientWorld.runQueuedChunkUpdates();
         this.clientWorld.tick(() -> true);
         if (hasFrame.getAsBoolean()) {
            this.clientWorld.tickEntities();
         } else {
            this.clientWorld.tickEntity(this.clientPlayerEntity);
         }

         inputActions.run();
      });
   }

   boolean runAsBot(Runnable action) {
      if (this.clientPlayerEntity != null && this.clientWorld != null) {
         if (this.checkState()) {
            action.run();
         } else {
            this.updateState(action);
         }

         return true;
      } else {
         return false;
      }
   }

   boolean enterView() {
      if (!this.ensureReady()) {
         return false;
      }

      if (this.clientWorld != null && this.clientPlayerEntity != null && this.clientPlayerInteractionManager != null) {
         this.minecraftClient.world = this.clientWorld;
         this.minecraftClient.particleManager.setWorld(this.clientWorld);
         this.minecraftClient.worldRenderer.setWorld(this.clientWorld);
         this.minecraftClient.worldRenderer.reload();
         this.minecraftClient.player = this.clientPlayerEntity;
         this.minecraftClient.interactionManager = this.clientPlayerInteractionManager;
         this.minecraftClient.setCameraEntity(this.entity2 != null ? this.entity2 : this.clientPlayerEntity);
         this.clientPlayerEntity.input = new KeyboardInput(this.minecraftClient.options);
         this.minecraftClient.currentScreen = this.computeScreen();
         this.minecraftClient.options.setServerViewDistance(this.index);
         this.flag2 = true;
         int var1 = 0;

         for (Entity var3 : this.clientWorld.getEntities()) {
            var1++;
         }

         BotLog.log(
            "["
               + this.botSession.getName()
               + "] enterView pos="
               + (int)this.clientPlayerEntity.getX()
               + ","
               + (int)this.clientPlayerEntity.getY()
               + ","
               + (int)this.clientPlayerEntity.getZ()
               + " chunks="
               + this.clientWorld.getChunkManager().getLoadedChunkCount()
               + " entities="
               + var1
               + " viewDist="
               + this.index
         );
         return true;
      } else {
         return false;
      }
   }

   void exitView() {
      this.flag2 = false;
   }

   ClientWorld visualWorld() {
      return this.clientWorld;
   }

   ClientPlayerEntity visualPlayer() {
      return this.clientPlayerEntity;
   }

   Entity visualCamera() {
      return this.entity2 != null ? this.entity2 : this.clientPlayerEntity;
   }

   ClientPlayerInteractionManager visualInteraction() {
      return this.clientPlayerInteractionManager;
   }

   Screen visualScreen() {
      return this.computeScreen();
   }

   boolean runWithVisualScreen(Runnable action) {
      if (this.ensureReady() && this.clientWorld != null && this.clientPlayerEntity != null) {
         this.updateState2(() -> {
            this.minecraftClient.currentScreen = this.computeScreen();
            action.run();
            this.screen2 = computereturn(this.minecraftClient.currentScreen);
         }, false);
         return true;
      } else {
         return false;
      }
   }

   boolean isVisualHandler(Object handler) {
      return handler != null && handler == this.clientPlayNetworkHandler;
   }

   PlayerPosition applyServerPositionLook(PlayerPositionLookS2CPacket packet) {
      this.playerPosition = PlayerPosition.apply(this.playerPosition, packet.change(), packet.relatives());
      this.flag = true;
      return this.playerPosition;
   }

   void applyServerRotation(PlayerRotationS2CPacket packet) {
      this.playerPosition = new PlayerPosition(this.playerPosition.position(), this.playerPosition.deltaMovement(), packet.yRot(), packet.xRot());
   }

   private void updateState(Runnable action) {
      this.updateState2(action, false);
   }

   private void updateState2(Runnable action, boolean restoreRenderer) {
      if (this.checkState()) {
         this.updateState4();
      }

      ClientStateSnapshot var3 = ClientStateSnapshot.capture(this.minecraftClient);
      boolean var4 = this.flag2;
      boolean var5 = restoreRenderer || var4;
      this.updateState3(var5);
      BotSession var6 = BotContext.enter(this.botSession);

      try {
         action.run();
         this.updateState4();
      } finally {
         BotContext.exit(var6);
         if (!var4 && !this.flag2) {
            var3.restore(this.minecraftClient, restoreRenderer);
         } else {
            this.updateState3(true);
         }
      }
   }

   private void updateState3(boolean updateRenderer) {
      if (this.minecraftClient.world != this.clientWorld) {
         this.minecraftClient.world = this.clientWorld;
         if (this.clientWorld != null) {
            this.minecraftClient.particleManager.setWorld(this.clientWorld);
         }

         if (updateRenderer) {
            this.minecraftClient.worldRenderer.setWorld(this.clientWorld);
         }
      }

      this.minecraftClient.player = this.clientPlayerEntity;
      this.minecraftClient.interactionManager = this.clientPlayerInteractionManager;
      this.minecraftClient.setCameraEntity(this.entity2 != null ? this.entity2 : this.clientPlayerEntity);
      this.minecraftClient.currentScreen = this.flag2 ? this.computeScreen() : null;
   }

   private void updateState4() {
      this.clientWorld = this.minecraftClient.world;
      this.clientPlayerEntity = this.minecraftClient.player;
      this.clientPlayerInteractionManager = this.minecraftClient.interactionManager;
      this.entity2 = this.minecraftClient.getCameraEntity();
      this.screen2 = computereturn(this.minecraftClient.currentScreen);
      if (this.clientPlayerEntity != null) {
         if (!(this.clientPlayerEntity.input instanceof KeyboardInput)) {
            this.clientPlayerEntity.input = new KeyboardInput(this.minecraftClient.options);
         }

         this.playerPosition = PlayerPosition.fromEntity(this.clientPlayerEntity);
         this.flag = true;
      }
   }

   private void updateState5() {
      if (this.flag && this.clientPlayerEntity != null) {
         Vec3d var1 = this.playerPosition.position();
         this.clientPlayerEntity.updatePositionAndAngles(var1.x, var1.y, var1.z, this.playerPosition.yaw(), this.playerPosition.pitch());
         this.clientPlayerEntity.setVelocity(this.playerPosition.deltaMovement());
         this.clientPlayerEntity.updatePrevAngles();
      }
   }

   private Screen computeScreen() {
      return computereturn(this.screen2);
   }

   private static Screen computereturn(Screen screen) {
      return screen instanceof DownloadingTerrainScreen ? null : screen;
   }

   private boolean checkState() {
      return this.minecraftClient.world == this.clientWorld
         && this.minecraftClient.player == this.clientPlayerEntity
         && this.minecraftClient.interactionManager == this.clientPlayerInteractionManager;
   }

   void clearState() {
      this.flag2 = false;
      this.clientWorld = null;
      this.clientPlayerEntity = null;
      this.clientPlayerInteractionManager = null;
      this.clientPlayNetworkHandler = null;
      this.gameJoinS2CPacket = null;
      this.arrayDeque.clear();
      this.entity2 = null;
      this.screen2 = null;
      this.updateState6();
   }

   private void updateState6() {
      this.playerPosition = PLAYER_POSITION;
      this.flag = false;
   }
}
