package dlc.lumen.client.modules.impl.movement.utilitus;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public class MovementManager {
   private static MovementManager movementManager;
   private final List<String> movementLocked = new ArrayList<>();
   private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
   private boolean[] flag = new boolean[4];

   private MovementManager() {
   }

   public static MovementManager getInstance() {
      if (movementManager == null) {
         movementManager = new MovementManager();
      }

      return movementManager;
   }

   public void lockMovement(String reason) {
      if (!this.movementLocked.contains(reason)) {
         this.movementLocked.add(reason);
      }

      if (this.minecraftClient.player != null) {
         this.minecraftClient.options.forwardKey.setPressed(false);
         this.minecraftClient.options.backKey.setPressed(false);
         this.minecraftClient.options.leftKey.setPressed(false);
         this.minecraftClient.options.rightKey.setPressed(false);
         this.minecraftClient.options.jumpKey.setPressed(false);
         this.minecraftClient.options.sprintKey.setPressed(false);
         if (this.minecraftClient.player.isSprinting()) {
            this.minecraftClient.player.setSprinting(false);
         }
      }
   }

   public void unlockMovement(String reason) {
      this.movementLocked.remove(reason);
   }

   public boolean isMovementLocked() {
      return !this.movementLocked.isEmpty();
   }

   public boolean isLocked(String reason) {
      return this.movementLocked.contains(reason);
   }

   public void clearAllLocks() {
      this.movementLocked.clear();
   }

   public void forceStopMovement() {
      if (this.isMovementLocked() && this.minecraftClient.player != null) {
         this.minecraftClient.options.forwardKey.setPressed(false);
         this.minecraftClient.options.backKey.setPressed(false);
         this.minecraftClient.options.leftKey.setPressed(false);
         this.minecraftClient.options.rightKey.setPressed(false);
         this.minecraftClient.options.jumpKey.setPressed(false);
         this.minecraftClient.options.sprintKey.setPressed(false);
      }
   }
}
