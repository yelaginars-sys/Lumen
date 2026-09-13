package dlc.lumen.client.bots.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public final class ClientStateSnapshot {
   private final ClientWorld clientWorld;
   private final ClientPlayerEntity clientPlayerEntity;
   private final ClientPlayerInteractionManager clientPlayerInteractionManager;
   private final Entity entity2;
   private final Screen screen2;

   private ClientStateSnapshot(
      ClientWorld world, ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, Entity cameraEntity, Screen screen
   ) {
      this.clientWorld = world;
      this.clientPlayerEntity = player;
      this.clientPlayerInteractionManager = interactionManager;
      this.entity2 = cameraEntity;
      this.screen2 = screen;
   }

   public static ClientStateSnapshot capture(MinecraftClient client) {
      return new ClientStateSnapshot(client.world, client.player, client.interactionManager, client.getCameraEntity(), client.currentScreen);
   }

   public void restore(MinecraftClient client, boolean updateRenderer) {
      if (client.world != this.clientWorld) {
         client.world = this.clientWorld;
         if (this.clientWorld != null) {
            client.particleManager.setWorld(this.clientWorld);
         }

         if (updateRenderer) {
            client.worldRenderer.setWorld(this.clientWorld);
         }
      }

      client.player = this.clientPlayerEntity;
      client.interactionManager = this.clientPlayerInteractionManager;
      client.setCameraEntity(this.entity2 != null ? this.entity2 : this.clientPlayerEntity);
      client.currentScreen = this.screen2;
   }

   public ClientWorld world() {
      return this.clientWorld;
   }

   public ClientPlayerEntity player() {
      return this.clientPlayerEntity;
   }

   public ClientPlayerInteractionManager interactionManager() {
      return this.clientPlayerInteractionManager;
   }

   public Entity cameraEntity() {
      return this.entity2;
   }

   public Screen screen() {
      return this.screen2;
   }
}
