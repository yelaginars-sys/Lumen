package dlc.lumen.client.autobuy;

import dlc.lumen.api.QClient;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;

public class AutoSeller implements QClient {
   private static final int INDEX = 600;
   private AutoSeller.State state = AutoSeller.State.IDLE;
   private int index = 0;
   private int index2 = 0;
   private int index3 = 0;
   private int index4 = 0;
   private String text = null;
   private ItemEntry itemEntry = null;
   private int index5 = 1;
   private boolean flag = false;
   private final Set<String> hashSet = new HashSet<>();
   private volatile boolean volatileboolean = false;
   private volatile boolean volatileboolean2 = false;
   private volatile boolean volatileboolean3 = false;
   private volatile boolean volatileboolean4 = false;
   private volatile boolean volatileboolean5 = false;
   private volatile boolean volatileboolean6 = false;

   public void stop() {
      this.state = AutoSeller.State.IDLE;
      this.index = 0;
      this.index2 = 0;
      this.index3 = 0;
      this.index4 = 0;
      this.text = null;
      this.itemEntry = null;
      this.index5 = 1;
      this.flag = false;
      this.hashSet.clear();
      this.volatileboolean = false;
      this.volatileboolean2 = false;
      this.volatileboolean3 = false;
      this.volatileboolean4 = false;
      this.volatileboolean5 = false;
      this.volatileboolean6 = false;
   }

   public void trigger() {
      this.volatileboolean = true;
      this.volatileboolean2 = false;
   }

   public void triggerRelist() {
      this.volatileboolean = true;
      this.volatileboolean2 = true;
   }

   public boolean isActive() {
      return this.state != AutoSeller.State.IDLE;
   }

   public void onListed() {
      if (this.state == AutoSeller.State.WAIT) {
         this.volatileboolean4 = true;
      }
   }

   public void onAuctionFull() {
      if (this.state == AutoSeller.State.SELL_AUTO || this.state == AutoSeller.State.WAIT) {
         this.volatileboolean5 = true;
      }
   }

   public void onSold() {
      if (this.volatileboolean3) {
         this.volatileboolean3 = false;
         this.volatileboolean = true;
      }
   }

   public void onCommandCooldown() {
      if (this.state == AutoSeller.State.REOPEN || this.state == AutoSeller.State.WAIT_REOPEN) {
         this.volatileboolean6 = true;
      }
   }

   public synchronized void tick() {
      if (mc.player != null) {
         if (this.index > 0) {
            this.index--;
         } else {
            switch (this.state) {
               case IDLE:
                  if (this.volatileboolean3) {
                     if (--this.index4 > 0) {
                        return;
                     }

                     this.volatileboolean3 = false;
                     this.volatileboolean = true;
                     this.volatileboolean2 = this.flag;
                  }

                  if (!this.volatileboolean) {
                     return;
                  }

                  this.volatileboolean = false;
                  this.flag = this.volatileboolean2;
                  this.volatileboolean2 = false;
                  this.hashSet.clear();
                  this.state = AutoSeller.State.CLOSE;
                  break;
               case CLOSE:
                  if (mc.currentScreen != null) {
                     mc.player.closeHandledScreen();
                     this.index = 6;
                  } else {
                     this.state = AutoSeller.State.SWEEP;
                  }
                  break;
               case SWEEP:
                  int var1 = this.resolveInt();
                  if (var1 < 0) {
                     this.index3 = 0;
                     this.state = AutoSeller.State.REOPEN;
                  } else {
                     ItemStack var2 = mc.player.getInventory().getStack(var1);
                     this.text = Registries.ITEM.getId(var2.getItem()).toString();
                     this.itemEntry = this.newItemEntry(var2);
                     this.index5 = Math.max(1, var2.getCount());
                     this.updateState(var1);
                     this.state = AutoSeller.State.SELL_AUTO;
                     this.index = 8;
                  }
                  break;
               case SELL_AUTO:
                  this.volatileboolean4 = false;
                  this.volatileboolean5 = false;
                  mc.player.networkHandler.sendChatCommand(this.resolveString());
                  this.state = AutoSeller.State.WAIT;
                  this.index2 = 100;
                  break;
               case WAIT:
                  if (this.volatileboolean5) {
                     this.volatileboolean5 = false;
                     this.volatileboolean3 = true;
                     this.index4 = 600;
                     this.index3 = 0;
                     this.state = AutoSeller.State.REOPEN;
                  } else if (this.volatileboolean4) {
                     this.volatileboolean4 = false;
                     this.state = AutoSeller.State.SWEEP;
                     this.index = 6;
                  } else {
                     this.index2--;
                     if (this.index2 <= 0) {
                        if (this.text != null) {
                           this.hashSet.add(this.text);
                        }

                        this.state = AutoSeller.State.SWEEP;
                        this.index = 4;
                     }
                  }
                  break;
               case REOPEN:
                  this.volatileboolean6 = false;
                  mc.player.networkHandler.sendChatCommand("ah");
                  this.index3++;
                  this.state = AutoSeller.State.WAIT_REOPEN;
                  this.index2 = 40;
                  break;
               case WAIT_REOPEN:
                  if (this.checkState()) {
                     this.index3 = 0;
                     this.state = AutoSeller.State.IDLE;
                     this.index = 10;
                  } else if (this.volatileboolean6) {
                     this.volatileboolean6 = false;
                     this.state = AutoSeller.State.REOPEN;
                     this.index = 25;
                  } else {
                     this.index2--;
                     if (this.index2 <= 0) {
                        if (this.index3 < 6) {
                           this.state = AutoSeller.State.REOPEN;
                           this.index = 10;
                        } else {
                           this.index3 = 0;
                           this.state = AutoSeller.State.IDLE;
                        }
                     }
                  }
                  break;
               default:
                  this.state = AutoSeller.State.IDLE;
            }
         }
      }
   }

   private int resolveInt() {
      if (mc.player == null) {
         return -1;
      }

      PlayerInventory var1 = mc.player.getInventory();

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = var1.getStack(var2);
         if (!var3.isEmpty()) {
            String var4 = Registries.ITEM.getId(var3.getItem()).toString();
            if (!this.hashSet.contains(var4) && this.checkCondition(var3)) {
               return var2;
            }
         }
      }

      return -1;
   }

   private boolean checkCondition(ItemStack stack) {
      return this.newItemEntry(stack) != null;
   }

   private ItemEntry newItemEntry(ItemStack stack) {
      Lot var2 = PriceParser.parseForMatch(stack);
      if (var2 == null) {
         return null;
      }

      for (ItemEntry var4 : AutoBuyConfig.cfg().entries) {
         if (ItemMatcher.matches(var4, var2)) {
            return var4;
         }
      }

      return null;
   }

   private String resolveString() {
      long var1 = this.itemEntry != null ? this.itemEntry.marketPrice : 0L;
      if (var1 <= 0L) {
         return "ah sell auto";
      }

      AutoBuyConfig var3 = AutoBuyConfig.cfg();
      int var4 = this.flag ? var3.relistDiscountPercent : var3.sellDiscountPercent;
      var4 = Math.max(0, Math.min(99, var4));
      long var5 = var1 * Math.max(1, this.index5) * (100L - var4) / 100L;
      if (var5 < 1L) {
         var5 = 1L;
      }

      return "ah sell " + var5;
   }

   private boolean checkState() {
      return mc.currentScreen instanceof GenericContainerScreen var1 && AutoBuyEngine.isAuctionScreen(var1);
   }

   private void updateState(int slot) {
      if (slot < 9) {
         this.updateState2(slot);
      } else {
         int var2 = mc.player.getInventory().selectedSlot;
         if (mc.interactionManager != null) {
            mc.interactionManager.clickSlot(0, slot, var2, SlotActionType.SWAP, mc.player);
         }

         this.updateState2(var2);
      }
   }

   private void updateState2(int slot) {
      mc.player.getInventory().selectedSlot = slot;
      mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
   }

   private enum State {
      IDLE,
      CLOSE,
      SWEEP,
      SELL_AUTO,
      WAIT,
      REOPEN,
      WAIT_REOPEN;
   }
}
