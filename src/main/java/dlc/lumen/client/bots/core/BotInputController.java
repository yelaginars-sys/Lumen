package dlc.lumen.client.bots.core;

import dlc.lumen.mixin.IMinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;

final class BotInputController {
   private static final PlayerInput PLAYER_INPUT = new PlayerInput(false, false, false, false, false, false, false);
   private final MinecraftClient minecraftClient;
   private final BotInputState state;
   private final Input value = new Input() {
      @Override
      public void tick() {
         PlayerInput var1 = BotInputController.this.computePlayerInput();
         this.playerInput = var1 == null ? BotInputController.PLAYER_INPUT : var1;
         this.movementForward = BotInputController.computefloat(var1.forward(), var1.backward());
         this.movementSideways = BotInputController.computefloat(var1.left(), var1.right());
      }
   };

   BotInputController(MinecraftClient client, BotInputState keyState) {
      this.minecraftClient = client;
      this.state = keyState;
   }

   Input backgroundInput() {
      return this.value;
   }

   void setUse(boolean use) {
      this.state.setUse(use);
   }

   void runWithKeyState(Runnable action) {
      BotInputState var2 = BotInputState.capture(this.minecraftClient);
      this.state.apply(this.minecraftClient);

      try {
         action.run();
      } finally {
         this.state.read(this.minecraftClient);
         var2.apply(this.minecraftClient);
      }
   }

   private static float computefloat(boolean positive, boolean negative) {
      if (positive == negative) {
         return 0.0F;
      } else {
         return positive ? 1.0F : -1.0F;
      }
   }

   private PlayerInput computePlayerInput() {
      return new PlayerInput(
         this.minecraftClient.options.forwardKey.isPressed(),
         this.minecraftClient.options.backKey.isPressed(),
         this.minecraftClient.options.leftKey.isPressed(),
         this.minecraftClient.options.rightKey.isPressed(),
         this.minecraftClient.options.jumpKey.isPressed(),
         this.minecraftClient.options.sneakKey.isPressed(),
         this.minecraftClient.options.sprintKey.isPressed()
      );
   }

   void handleActions() {
      if (this.minecraftClient.currentScreen == null) {
         this.minecraftClient.gameRenderer.updateCrosshairTarget(1.0F);
         if (this.minecraftClient.options.attackKey.isPressed()) {
            IMinecraftClientAccessor var1 = (IMinecraftClientAccessor)this.minecraftClient;
            int var2 = var1.lumen$getAttackCooldown();
            var1.lumen$setAttackCooldown(0);

            try {
               var1.lumen$doAttack();
            } finally {
               var1.lumen$setAttackCooldown(var2);
            }
         }

         if (this.minecraftClient.options.useKey.isPressed()) {
            ((IMinecraftClientAccessor)this.minecraftClient).lumen$doItemUse();
         }
      }
   }
}
