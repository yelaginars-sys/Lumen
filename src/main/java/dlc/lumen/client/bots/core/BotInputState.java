package dlc.lumen.client.bots.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public final class BotInputState {
   private boolean use2;
   private boolean use3;
   private boolean use4;
   private boolean use5;
   private boolean use6;
   private boolean use7;
   private boolean use8;
   private boolean use9;
   private boolean use10;

   static BotInputState capture(MinecraftClient client) {
      BotInputState var1 = new BotInputState();
      var1.read(client);
      return var1;
   }

   void read(MinecraftClient client) {
      this.use2 = checkCondition(client.options.forwardKey);
      this.use3 = checkCondition(client.options.backKey);
      this.use4 = checkCondition(client.options.leftKey);
      this.use5 = checkCondition(client.options.rightKey);
      this.use6 = checkCondition(client.options.jumpKey);
      this.use7 = checkCondition(client.options.sneakKey);
      this.use8 = checkCondition(client.options.sprintKey);
      this.use9 = checkCondition(client.options.useKey);
      this.use10 = checkCondition(client.options.attackKey);
   }

   void apply(MinecraftClient client) {
      client.options.forwardKey.setPressed(this.use2);
      client.options.backKey.setPressed(this.use3);
      client.options.leftKey.setPressed(this.use4);
      client.options.rightKey.setPressed(this.use5);
      client.options.jumpKey.setPressed(this.use6);
      client.options.sneakKey.setPressed(this.use7);
      client.options.sprintKey.setPressed(this.use8);
      client.options.useKey.setPressed(this.use9);
      client.options.attackKey.setPressed(this.use10);
   }

   public void setUse(boolean use) {
      this.use9 = use;
   }

   public void setForward(boolean value) {
      this.use2 = value;
   }

   public void setBackward(boolean value) {
      this.use3 = value;
   }

   public void setLeft(boolean value) {
      this.use4 = value;
   }

   public void setRight(boolean value) {
      this.use5 = value;
   }

   public void setJump(boolean value) {
      this.use6 = value;
   }

   public void setSneak(boolean value) {
      this.use7 = value;
   }

   public void setSprint(boolean value) {
      this.use8 = value;
   }

   public void setAttack(boolean value) {
      this.use10 = value;
   }

   void copyFrom(BotInputState other) {
      if (other == null) {
         this.clear();
      } else {
         this.use2 = other.use2;
         this.use3 = other.use3;
         this.use4 = other.use4;
         this.use5 = other.use5;
         this.use6 = other.use6;
         this.use7 = other.use7;
         this.use8 = other.use8;
         this.use9 = other.use9;
         this.use10 = other.use10;
      }
   }

   public void clear() {
      this.use2 = false;
      this.use3 = false;
      this.use4 = false;
      this.use5 = false;
      this.use6 = false;
      this.use7 = false;
      this.use8 = false;
      this.use9 = false;
      this.use10 = false;
   }

   private static boolean checkCondition(KeyBinding keyBinding) {
      return keyBinding != null && keyBinding.isPressed();
   }
}
