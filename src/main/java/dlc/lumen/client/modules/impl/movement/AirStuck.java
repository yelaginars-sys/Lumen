package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMove;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.utils.network.NetworkUtils;
import dlc.lumen.api.utils.player.InventoryUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Vec3d;

public class AirStuck extends Module {
   public static AirStuck INSTANCE = new AirStuck();
   private final ModeSetting modeSetting = new ModeSetting("Мод", "Обычный", "Обычный");
   private final BooleanSetting booleanSetting = new BooleanSetting("Отменять пакеты", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Свапать элитру", true);
   private Vec3d vec3d = Vec3d.ZERO;
   private boolean flag = false;

   public AirStuck() {
      super("AirStuck", "Зависает в воздухе", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.modeSetting, this.booleanSetting, this.booleanSetting2);
   }

   @Override
   public void onEnable() {
      this.flag = false;
      if (mc.player != null && this.booleanSetting2.isState()) {
         this.helper();
      }

      if (mc.player != null && this.modeSetting.is("Обычный")) {
         this.vec3d = mc.player.getPos();
         this.flag = true;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }

   private void helper() {
      ItemStack var1 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      if (var1.isOf(Items.ELYTRA)) {
         int var2 = InventoryUtils.findBestChestplateSlot();
         if (var2 != -1) {
            this.helper2(var2);
         }
      }
   }

   private void helper2(int slot) {
      if (slot >= 0 && slot < 9) {
         mc.interactionManager.clickSlot(0, 6, slot, SlotActionType.SWAP, mc.player);
      } else {
         mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, mc.player);
         mc.interactionManager.clickSlot(0, 6, 0, SlotActionType.SWAP, mc.player);
         mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, mc.player);
      }

      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
   }

   @EventLink
   public void onMove(EventMove e) {
      if (mc.player != null) {
         if (this.modeSetting.is("LonyGrief") && !this.flag && mc.player.fallDistance > 0.0F && mc.player.getVelocity().y < 0.0) {
            this.vec3d = mc.player.getPos();
            this.flag = true;
         }

         if (this.flag) {
            e.setMovePos(Vec3d.ZERO);
            mc.player.setPosition(this.vec3d.x, this.vec3d.y, this.vec3d.z);
            mc.player.setVelocity(0.0, 0.0, 0.0);
         }
      }
   }

   @EventLink
   public void onPacket(EventPacket e) {
      if (this.flag && e.getType() == EventPacket.Type.SEND) {
         if (e.getPacket() instanceof PlayerMoveC2SPacket var2) {
            if (this.booleanSetting.isState()) {
               e.cancel();
            } else {
               e.cancel();
               NetworkUtils.sendSilentPacket(this.helper3(var2));
            }
         }
      }
   }

   private PlayerMoveC2SPacket helper3(PlayerMoveC2SPacket packet) {
      boolean var2 = packet.isOnGround();
      boolean var3 = packet.horizontalCollision();
      if (packet.changesPosition() && packet.changesLook()) {
         return new Full(
            this.vec3d.x,
            this.vec3d.y,
            this.vec3d.z,
            packet.getYaw(mc.player.getYaw()),
            packet.getPitch(mc.player.getPitch()),
            var2,
            var3
         );
      } else if (packet.changesPosition()) {
         return new PositionAndOnGround(this.vec3d.x, this.vec3d.y, this.vec3d.z, var2, var3);
      } else {
         return packet.changesLook()
            ? new LookAndOnGround(packet.getYaw(mc.player.getYaw()), packet.getPitch(mc.player.getPitch()), var2, var3)
            : new OnGroundOnly(var2, var3);
      }
   }
}
