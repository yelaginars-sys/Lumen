package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class ChestStealer extends Module {
   public static ChestStealer INSTANCE = new ChestStealer();
   private final FloatSetting floatSetting = new FloatSetting("Задержка", 100.0F, 0.0F, 1000.0F, 1.0F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Рандомизация", false);
   private long timestamp = 0L;

   public ChestStealer() {
      super("ChestStealer", "Автоматически открывает сундуки и забирает из них предметы", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting, this.booleanSetting);
   }

   @EventLink
   private void updateState(EventUpdate event) {
      if (mc.player != null && mc.interactionManager != null) {
         ScreenHandler var2 = mc.player.currentScreenHandler;
         if (var2 != null && var2 != mc.player.playerScreenHandler) {
            if (var2 instanceof GenericContainerScreenHandler || var2 instanceof HopperScreenHandler) {
               long var3 = System.currentTimeMillis();
               long var5 = (long)this.floatSetting.get();
               if (var3 - this.timestamp >= var5) {
                  DefaultedList<Slot> var7 = var2.slots;
                  this.helper(var7, var2).ifPresent(slot -> {
                     if (mc.player.currentScreenHandler == var2) {
                        mc.interactionManager.clickSlot(var2.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                        this.timestamp = var3;
                     }
                  });
               }
            }
         }
      }
   }

   private Optional<Slot> helper(List<Slot> slots, ScreenHandler handler) {
      int var3 = this.helper2(handler);
      if (var3 > 0 && var3 <= slots.size()) {
         List<Slot> var4 = slots.subList(0, var3);
         ArrayList<Slot> var5 = new ArrayList<>();

         for (Slot var7 : var4) {
            if (var7.hasStack() && !var7.getStack().isEmpty() && !mc.player.getItemCooldownManager().isCoolingDown(var7.getStack())) {
               var5.add(var7);
            }
         }

         if (var5.isEmpty()) {
            return Optional.empty();
         } else if (this.booleanSetting.isState()) {
            int var8 = ThreadLocalRandom.current().nextInt(var5.size());
            return Optional.of((Slot)var5.get(var8));
         } else {
            return Optional.of((Slot)var5.get(0));
         }
      } else {
         return Optional.empty();
      }
   }

   private int helper2(ScreenHandler handler) {
      if (handler instanceof GenericContainerScreenHandler var2) {
         Inventory var3 = var2.getInventory();
         return var3.size();
      } else {
         return handler instanceof HopperScreenHandler ? 5 : 0;
      }
   }

   @Override
   public void onDisable() {
      this.timestamp = 0L;
      super.onDisable();
   }
}
