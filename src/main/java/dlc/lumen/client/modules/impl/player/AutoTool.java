package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.hit.BlockHitResult;

public class AutoTool extends Module {
   public static AutoTool INSTANCE = new AutoTool();
   private final BooleanSetting booleanSetting = new BooleanSetting("Пакетный", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Видно только для других людей", false);
   private int index = -1;

   public AutoTool() {
      super("AutoTool", "При копании берет лучший предмет", Module.ModuleCategory.PLAYER);
      this.addSettings(this.booleanSetting, this.booleanSetting2);
   }

   @EventLink
   public void onEvent(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null && !mc.player.isCreative()) {
         if (mc.interactionManager.isBreakingBlock()) {
            if (this.index == -1) {
               this.index = mc.player.getInventory().selectedSlot;
            }

            int var2 = this.helper2();
            if (var2 != -1) {
               this.helper(var2);
            }
         } else if (this.index != -1) {
            this.helper(this.index);
            this.index = -1;
         }
      } else {
         this.index = -1;
      }
   }

   private void helper(int slot) {
      if (slot >= 0 && slot <= 8) {
         if (mc.player.getInventory().selectedSlot != slot) {
            if (this.booleanSetting2.isState()) {
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            } else if (this.booleanSetting.isState()) {
               mc.player.getInventory().selectedSlot = slot;
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            } else {
               mc.player.getInventory().selectedSlot = slot;
            }
         }
      }
   }

   private int helper2() {
      if (mc.crosshairTarget instanceof BlockHitResult var2) {
         BlockState var3 = mc.world.getBlockState(var2.getBlockPos());
         return this.helper3(var3);
      } else {
         return -1;
      }
   }

   private int helper3(BlockState blockState) {
      int var2 = -1;
      float var3 = 1.0F;

      for (int var4 = 0; var4 < 9; var4++) {
         float var5 = mc.player.getInventory().getStack(var4).getMiningSpeedMultiplier(blockState);
         if (var5 > var3) {
            var3 = var5;
            var2 = var4;
         }
      }

      return var2;
   }

   @Override
   public void onDisable() {
      this.index = -1;
      super.onDisable();
   }
}
