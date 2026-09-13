package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventCloseInv;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.player.MoveUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class InventoryWalk extends Module {
   public static InventoryWalk INSTANCE = new InventoryWalk();
   public ModeSetting mode = new ModeSetting("Обход", "Обычный", "Обычный", "Legit");
   public ModeSetting grimVersion = new ModeSetting("Версия свапа", "1.21.4", "1.21.4", "1.16.5").visible(() -> this.mode.is("Legit"));
   public int tick = 0;
   private final List<ClickSlotC2SPacket> clickSlotC2SPackets = new ArrayList<>();
   private CloseHandledScreenC2SPacket closeHandledScreenC2SPacket = null;
   private boolean flag = false;
   private boolean flag2 = false;
   private int index = -1;
   private boolean flag3 = false;

   public InventoryWalk() {
      super("InventoryWalk", "Ходьба с открытым инвентарём", Module.ModuleCategory.MOVEMENT);
      this.addSettings(this.mode, this.grimVersion);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null) {
         KeyBinding[] var2 = new KeyBinding[]{
            mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey
         };
         if (this.mode.is("Legit") && this.grimVersion.is("1.21.4") && this.flag2 && !MoveUtils.isMoving()) {
            this.helper3(true);
            this.flag2 = false;
            this.tick = 3;
         }

         if (this.mode.is("Legit") && this.grimVersion.is("1.16.5") && this.index >= 0) {
            if (this.index == 0) {
               this.helper3(true);
               this.index = -1;
               this.tick = 1;
            } else {
               this.index--;
            }
         }

         if (this.tick == 0 && !this.clickSlotC2SPackets.isEmpty() && mc.currentScreen == null && !this.flag2) {
            this.helper2();
         }

         if (this.tick != 0) {
            for (KeyBinding var15 : var2) {
               var15.setPressed(false);
            }

            this.tick--;
            if (this.tick == 0 && this.flag) {
               this.flag = false;
               Sprint.popPause();
            }
         } else if (!(mc.currentScreen instanceof ChatScreen) && !(mc.currentScreen instanceof SignEditScreen)) {
            if (!this.mode.is("Legit") || !(mc.currentScreen instanceof HandledScreen) || mc.currentScreen instanceof InventoryScreen) {
               if (this.flag2) {
                  for (KeyBinding var14 : var2) {
                     var14.setPressed(false);
                  }
               } else {
                  for (KeyBinding var6 : var2) {
                     boolean var7 = InputUtil.isKeyPressed(mc.getWindow().getHandle(), var6.getDefaultKey().getCode());
                     var6.setPressed(var7);
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.SEND && !this.flag3) {
         Packet var2 = event.getPacket();
         if (this.mode.is("Legit") && MoveUtils.isMoving() && mc.currentScreen instanceof InventoryScreen) {
            if (var2 instanceof ClickSlotC2SPacket var4) {
               this.clickSlotC2SPackets.add(var4);
               event.cancel();
               this.helper();
               this.tick = 1;
            } else {
               if (var2 instanceof CloseHandledScreenC2SPacket var3) {
                  this.closeHandledScreenC2SPacket = var3;
                  if (this.grimVersion.is("1.16.5")) {
                     this.index = 1;
                     this.flag2 = false;
                  } else {
                     this.flag2 = true;
                  }

                  this.helper();
                  event.cancel();
               }
            }
         }
      }
   }

   @EventLink
   public void onCloseInv(EventCloseInv eventCloseInv) {
      if (this.mode.is("Legit") && this.grimVersion.is("1.16.5") && MoveUtils.isMoving() && mc.currentScreen instanceof InventoryScreen) {
         this.closeHandledScreenC2SPacket = new CloseHandledScreenC2SPacket(eventCloseInv.windowId);
         this.index = 1;
         this.helper();
         this.tick = 1;
         eventCloseInv.cancel();
      } else {
         if (this.mode.is("Legit") && !this.flag2) {
            this.helper();
            this.tick = 1;
         }
      }
   }

   private void helper() {
      if (!this.flag) {
         Sprint.pushPause(0L);
         this.flag = true;
      }
   }

   private void helper2() {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         this.flag3 = true;

         try {
            for (ClickSlotC2SPacket var2 : this.clickSlotC2SPackets) {
               mc.getNetworkHandler().sendPacket(var2);
            }
         } finally {
            this.flag3 = false;
         }

         this.clickSlotC2SPackets.clear();
      } else {
         this.clickSlotC2SPackets.clear();
      }
   }

   private void helper3(boolean includeClose) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         this.helper2();
         if (includeClose && this.closeHandledScreenC2SPacket != null) {
            this.flag3 = true;

            try {
               mc.getNetworkHandler().sendPacket(this.closeHandledScreenC2SPacket);
            } finally {
               this.flag3 = false;
            }

            this.closeHandledScreenC2SPacket = null;
         }
      } else {
         this.clickSlotC2SPackets.clear();
         this.closeHandledScreenC2SPacket = null;
      }
   }

   public static void stopTick(int ticks) {
      InventoryWalk var1 = ModuleClass.inventoryWalk;
      if (var1 != null && var1.isEnable()) {
         var1.tick = Math.max(var1.tick, ticks);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.helper3(true);
      if (this.flag) {
         this.flag = false;
         Sprint.popPause();
      }

      this.flag2 = false;
      this.index = -1;
      this.flag3 = false;
      this.tick = 0;
   }
}
