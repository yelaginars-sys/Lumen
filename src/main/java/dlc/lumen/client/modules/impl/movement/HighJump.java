package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;

public class HighJump extends Module {
   public static HighJump INSTANCE = new HighJump();
   private final ModeSetting modeSetting = new ModeSetting("Режим", "Shulker", "Shulker", "Slime", "Boat");
   private final FloatSetting floatSetting = new FloatSetting("Множитель", 2.0F, 1.1F, 5.0F, 0.1F);
   private boolean flag;
   private double level;
   private int index;

   public HighJump() {
      super("HighJump", "Высокий прыжок от различных источников", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting, this.floatSetting);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.level = 0.0;
      this.index = 0;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.flag = false;
      this.level = 0.0;
      this.index = 0;
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.index > 0) {
            this.index--;
         }

         if (this.modeSetting.is("Shulker")) {
            this.helper();
         }

         if (this.modeSetting.is("Slime")) {
            this.helper2();
         }

         if (this.modeSetting.is("Boat")) {
            this.helper3();
         }
      }
   }

   private void helper() {
      if (mc.currentScreen instanceof ShulkerBoxScreen) {
         BlockPos var1 = mc.player.getBlockPos();
         BlockPos[] var2 = new BlockPos[]{var1.down(), var1, var1.north(), var1.south(), var1.east(), var1.west()};
         boolean var3 = false;

         for (BlockPos var7 : var2) {
            BlockState var8 = mc.world.getBlockState(var7);
            if (var8.getBlock() instanceof ShulkerBoxBlock) {
               var3 = true;
               break;
            }
         }

         if (var3) {
            mc.player.setVelocity(mc.player.getVelocity().x, 2.0, mc.player.getVelocity().z);
            mc.player.closeHandledScreen();
         }
      }
   }

   private void helper2() {
      double var1 = mc.player.getVelocity().y;
      BlockPos var3 = mc.player.getBlockPos().down();
      BlockPos var4 = mc.player.getBlockPos().down(2);
      boolean var5 = mc.world.getBlockState(var3).isOf(Blocks.SLIME_BLOCK) || mc.world.getBlockState(var4).isOf(Blocks.SLIME_BLOCK);
      if (this.level < -0.1 && var1 > 0.1 && var5 && this.index == 0) {
         double var6 = var1 * this.floatSetting.get();
         mc.player.setVelocity(mc.player.getVelocity().x, var6, mc.player.getVelocity().z);
         this.index = 5;
      }

      this.level = var1;
   }

   private void helper3() {
      boolean var1 = mc.player.getVehicle() instanceof BoatEntity;
      if (this.flag && !var1 && this.index == 0) {
         mc.player.setVelocity(mc.player.getVelocity().x, 1.5, mc.player.getVelocity().z);
         this.index = 20;
      }

      this.flag = var1;
   }
}
