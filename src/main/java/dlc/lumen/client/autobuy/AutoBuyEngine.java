package dlc.lumen.client.autobuy;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AutoBuyEngine implements QClient {
   public static final AutoBuyEngine INSTANCE = new AutoBuyEngine();
   private static final Random RANDOM = ThreadLocalRandom.current();
   public final PriceAutoParser parser = new PriceAutoParser();
   public final AutoSeller seller = new AutoSeller();
   public final RelistManager relist = new RelistManager();
   public final AutoPay autoPay = new AutoPay();
   private int index = -1;
   private int index2 = -1;
   private boolean flag = false;
   private static final Pattern value = Pattern.compile(
      "(?iu)(?:баланс|на\\s+счету|на\\s+счёте|на\\s+счете|ваш\\s+счёт|ваш\\s+счет)[^\\d\\-]*([\\d\\s\\u00A0.,]+)"
   );
   private int index3 = 0;
   private long timestamp = 0L;
   private int index4 = 0;
   private int index5 = 0;
   private int index6 = 0;
   private int index7 = 0;
   private volatile String volatileString = null;
   private volatile String volatileString2 = null;
   private volatile String volatileString3 = null;
   private volatile long volatilelong = 0L;
   private volatile boolean volatileboolean = false;
   private volatile List<Marker> markers = null;
   private int index8 = 0;
   private boolean flag2 = false;
   private String text2 = null;
   private int index9 = 0;
   private int index10 = 0;
   private PurchaseRecord purchaseRecord = null;
   private static final int INDEX = 20;
   private static final int INDEX2 = 6;
   private float volume = Float.NaN;
   private float volume2 = Float.NaN;
   private int index11 = 0;
   private int index12 = 0;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private static final float VOLUME = 8.0F;
   private float volume3 = 8.0F;
   private int index13 = 0;
   private int index14 = -1;
   private int index15 = 0;
   private int index16 = 0;
   private int index17 = 0;
   private float volume4 = 1.0F;
   private int index18 = 0;
   private int index19 = 0;
   private static final int INDEX3 = 100;

   private AutoBuyEngine() {
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.interactionManager != null) {
         AutoBuyConfig var2 = AutoBuyConfig.cfg();
         this.index18++;
         if (this.index18 >= 3) {
            this.index18 = 0;
            this.volume4 = 0.9F + RANDOM.nextFloat() * 0.3F;
         }

         if (this.index17 > 0) {
            this.index17--;
         } else {
            try {
               this.parser.tick();
            } catch (Throwable var11) {
            }

            if (!this.parser.isActive()) {
               if (var2.enabled) {
                  if (!this.flag4) {
                     this.index12 = resolveInt10(2, 20);
                     this.volume3 = 8.0F * (0.9F + RANDOM.nextFloat() * 0.2F) * this.volume4;
                     this.flag4 = true;
                  }

                  this.updateState3();
               } else if (this.flag4) {
                  this.flag3 = false;
                  this.volume = Float.NaN;
                  this.volume2 = Float.NaN;
                  this.flag4 = false;
               }

               if (var2.enabled) {
                  if (this.index13 > 0) {
                     this.index13--;
                  } else {
                     if (var2.autoPay && RANDOM.nextInt(2) == 0) {
                        try {
                           this.autoPay.tick();
                        } catch (Throwable var10) {
                        }
                     }

                     try {
                        if (RANDOM.nextInt(2) == 0) {
                           this.relist.tick();
                        }
                     } catch (Throwable var9) {
                     }

                     if (!this.relist.isActive()) {
                        try {
                           if (RANDOM.nextInt(2) == 0) {
                              this.seller.tick();
                           }
                        } catch (Throwable var8) {
                        }

                        if (!this.seller.isActive()) {
                           try {
                              this.updateState5();
                           } catch (Throwable var7) {
                           }

                           try {
                              this.updateState6();
                           } catch (Throwable var6) {
                           }

                           try {
                              this.updateState9();
                           } catch (Throwable var5) {
                           }

                           try {
                              this.updateState();
                           } catch (Throwable var4) {
                           }

                           this.updateState2();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void updateState() {
      AutoBuyConfig var1 = AutoBuyConfig.cfg();
      if (var1.autoRefresh) {
         if (mc.currentScreen instanceof GenericContainerScreen var3) {
            if (isAuctionScreen(var3)) {
               GenericContainerScreenHandler var4 = var3.getScreenHandler();
               int var5 = (var4.getRows() - 1) * 9;
               long var6 = this.resolveLong(var4, var5);
               this.index19++;
               if (this.index19 >= -1) {
                  this.index19 = 0;
                  if (var6 != this.timestamp) {
                     this.timestamp = var6;
                     this.index4 = 0;
                     if (this.index5 <= 0) {
                        this.updateState8(var4, var4.syncId, var4.getRows());
                        this.index5 = this.resolveInt6(AutoBuyConfig.cfg());
                     }
                  } else {
                     this.index4++;
                     if (this.index4 >= 1 + RANDOM.nextInt(2) && this.index5 <= 0) {
                        this.updateState8(var4, var4.syncId, var4.getRows());
                        this.index5 = this.resolveInt6(AutoBuyConfig.cfg());
                        this.index4 = 0;
                     }
                  }
               }
            }
         }
      }
   }

   private void updateState2() {
      if (this.index16 > 0) {
         this.index16--;
         if (this.index16 == 0) {
            this.index14 = -1;
            this.index15 = 0;
         }
      } else if (RANDOM.nextInt(40) == 0) {
         this.index16 = resolveInt10(1, 2);
      }
   }

   private void updateState3() {
      if (mc.player != null) {
         if (RANDOM.nextInt(4) == 0) {
            this.volume3 = 8.0F * (0.8F + RANDOM.nextFloat() * 0.4F) * this.volume4;
         }

         if (this.flag3) {
            if (this.index11 > 0) {
               this.index11--;
               float var1 = mc.player.getYaw();
               float var2 = mc.player.getPitch();
               float var3 = MathHelper.wrapDegrees(this.volume - var1);
               float var4 = MathHelper.wrapDegrees(this.volume2 - var2);
               float var5 = 0.9F + RANDOM.nextFloat() * 0.2F;
               float var6 = Math.min(Math.abs(var3), this.volume3 * var5) * Math.signum(var3);
               float var7 = Math.min(Math.abs(var4), this.volume3 * 0.9F * var5) * Math.signum(var4);
               if (Math.abs(var3) < 0.5F && Math.abs(var4) < 0.5F) {
                  this.flag3 = false;
                  this.index12 = resolveInt10(2, 30);
               } else {
                  mc.player.setYaw(var1 + var6);
                  mc.player.setPitch(var2 + var7);
               }
            } else {
               this.flag3 = false;
               this.index12 = resolveInt10(2, 30);
            }
         } else if (this.index12 > 0) {
            this.index12--;
         } else {
            this.updateState4();
         }
      }
   }

   private void updateState4() {
      if (mc.player != null) {
         float var1 = mc.player.getYaw();
         float var2 = mc.player.getPitch();
         this.volume = var1 + (RANDOM.nextFloat() * 160.0F - 80.0F);
         this.volume2 = MathHelper.clamp(var2 + (RANDOM.nextFloat() * 60.0F - 30.0F), -80.0F, 80.0F);
         this.index11 = resolveInt10(1, 10);
         this.flag3 = true;
         this.volume3 = 8.0F * (0.8F + RANDOM.nextFloat() * 0.4F) * this.volume4;
      }
   }

   private void updateState5() {
      AutoBuyConfig var1 = AutoBuyConfig.cfg();
      if (!var1.enabled) {
         this.flag = false;
         this.index2 = -1;
         this.index = -1;
      } else {
         if (this.flag) {
            this.flag = false;
            var1.enabled = true;
         }

         if (var1.autoRelist) {
            if (this.index < 0) {
               this.index = Math.max(5, var1.relistIntervalSeconds * 10) + resolveInt10(-20, 50);
            }

            if (--this.index <= 0) {
               int var2 = Math.max(5, var1.relistIntervalSeconds * 10);
               this.index = var2 + resolveInt10(-var2 / 4, var2 / 3);
               if (RANDOM.nextInt(3) == 0) {
                  this.index = this.index + resolveInt10(5, 20);
               } else {
                  this.relist.start();
               }

               return;
            }
         } else {
            this.index = -1;
         }

         this.index13 = Math.max(0, this.resolveInt7(var1) - 1);
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE) {
         if (event.getPacket() instanceof GameMessageS2CPacket var3) {
            AutoBuyConfig var4 = AutoBuyConfig.cfg();
            String var5 = var3.content().getString().toLowerCase(Locale.ROOT);
            if (var5.contains("недостаточно средств")) {
               this.onInsufficientFunds();
            }

            if (var5.contains("не можете больше выставлять")) {
               this.seller.onAuctionFull();
            }

            if (var5.contains("купил у вас")) {
               this.seller.onSold();
            }

            if (var5.contains("успешно выставили")) {
               this.seller.onListed();
            }

            if (var5.contains("подождите") && var5.contains("для использования этой команды")) {
               this.seller.onCommandCooldown();
               this.index6 = Math.max(this.index6, this.resolveInt8(var4));
            }

            if (!var5.contains("пополнен") && !var5.contains("купил у вас")) {
               Long var6 = computeLong(var5);
               if (var6 != null) {
                  this.autoPay.onBalance(var6);
               }
            }
         }
      }
   }

   private void updateState6() {
      AutoBuyConfig var1 = AutoBuyConfig.cfg();
      if (this.index3 > 0) {
         this.index3--;
      }

      if (this.index5 > 0) {
         this.index5--;
      }

      if (this.index6 > 0) {
         this.index6--;
      }

      if (RANDOM.nextInt(20) == 0) {
         this.index17 = resolveInt10(1, 3);
      } else {
         Screen var2 = mc.currentScreen;
         if (!(var2 instanceof GenericContainerScreen var3)) {
            this.index4 = 0;
            if (var2 == null) {
               this.index7++;
               if (this.index7 >= Math.max(1, this.resolveInt8(var1) / 2)) {
                  this.updateState7();
               }
            } else {
               this.index7 = 0;
            }
         } else {
            this.index7 = 0;
            if (!isAuctionScreen(var3)) {
               this.index4 = 0;
            } else if (this.index3 <= 0) {
               GenericContainerScreenHandler var4 = var3.getScreenHandler();
               int var5 = var4.syncId;
               int var6 = var4.getRows();
               int var7 = (var6 - 1) * 9;
               if (!this.checkCondition(var4, var5)) {
                  long var8 = this.resolveLong(var4, var7);
                  boolean var10 = var8 != this.timestamp;
                  if (var10) {
                     this.index4 = 0;
                     this.timestamp = var8;
                  } else {
                     this.index4++;
                  }

                  if (RANDOM.nextInt(8) == 0) {
                     this.index14 = RANDOM.nextInt(var7);
                     this.index15 = resolveInt10(1, 2);
                  }

                  if (this.index15 > 0) {
                     this.index15--;
                  }

                  ArrayList<Lot> var11 = new ArrayList<>();

                  for (int var12 = 0; var12 < var7; var12++) {
                     ItemStack var13 = var4.getSlot(var12).getStack();
                     Lot var14 = PriceParser.parse(var12, var13);
                     if (var14 != null && !var14.barrier) {
                        var11.add(var14);
                     }
                  }

                  Lot var17 = null;
                  ItemEntry var18 = null;

                  for (Lot var15 : var11) {
                     ItemEntry var16 = this.newNamedEntry(var1, var15);
                     if (var16 != null) {
                        var17 = var15;
                        var18 = var16;
                        break;
                     }
                  }

                  if (var17 != null && var18 != null) {
                     this.volatileString = var17.itemId;
                     this.volatileString2 = var18.displayName;
                     this.volatileString3 = var18.loreKeyword;
                     this.volatilelong = var18.maxUnitPrice;
                     this.volatileboolean = var18.requireCustom;
                     this.markers = var18.resolveMarkers();
                     this.index3 = this.resolveInt5(var1);
                     if (RANDOM.nextInt(8) == 0) {
                        this.index3 = this.index3 + Math.max(1, this.resolveInt7(var1));
                        this.volatileString = null;
                        return;
                     }

                     this.updateState12(var5, var17.slot);
                  }
               }
            }
         }
      }
   }

   private boolean checkCondition(GenericContainerScreenHandler handler, int syncId) {
      int var3 = AuctionSortMode.findButton(handler);
      if (var3 < 0) {
         return false;
      } else {
         AuctionSortMode var4 = AuctionSortMode.current(handler.getSlot(var3).getStack());
         if (var4 != null && var4 != AuctionSortMode.NEWEST) {
            this.updateState12(syncId, var3);
            this.index3 = Math.max(this.index3, 6);
            this.timestamp = 0L;
            this.index4 = 0;
            return true;
         } else {
            return false;
         }
      }
   }

   private void updateState7() {
      AutoBuyConfig var1 = AutoBuyConfig.cfg();
      if (this.index6 <= 0 && mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendChatCommand("ah");
         this.index6 = this.resolveInt8(var1);
      }
   }

   private ItemEntry newNamedEntry(AutoBuyConfig cfg, Lot lot) {
      String var3 = mc.player.getGameProfile().getName();
      if (var3 != null && lot.seller != null && var3.equalsIgnoreCase(lot.seller)) {
         return null;
      }

      if (cfg.ignored(lot.seller)) {
         return null;
      }

      if (cfg.minPriceEnabled && cfg.minPrice > 0L && lot.unitPrice < cfg.minPrice) {
         return null;
      }

      ItemEntry var4 = null;
      int var5 = -1;
      ArrayList<ItemEntry> var6 = new ArrayList<>(cfg.entries);
      if (RANDOM.nextInt(2) == 0) {
         Collections.shuffle(var6, RANDOM);
      }

      for (ItemEntry var8 : var6) {
         if (var8.enabled && var8.maxUnitPrice > 0L && ItemMatcher.matches(var8, lot) && lot.unitPrice <= var8.maxUnitPrice) {
            int var9 = (var8.displayName != null ? var8.displayName.length() * 2 : 0)
               + (var8.loreKeyword != null && !var8.loreKeyword.isBlank() ? resolveInt10(15, 50) : 0)
               + (var8.matchAnySword ? -resolveInt10(10, 30) : 0)
               + (var8.requireCustom ? resolveInt10(10, 30) : 0)
               + (RANDOM.nextInt(20) - 10);
            if (var9 > var5) {
               var5 = var9;
               var4 = var8;
            }
         }
      }

      return var4;
   }

   public static boolean isAuctionScreen(GenericContainerScreen gcs) {
      GenericContainerScreenHandler var1 = gcs.getScreenHandler();
      if (var1.getRows() != 6) {
         return false;
      }

      String var2 = gcs.getTitle().getString();
      return var2.startsWith("Аукцион");
   }

   private void updateState8(GenericContainerScreenHandler handler, int syncId, int rows) {
      if (this.index5 <= 0) {
         int var4 = rows * 9;
         int var5 = -1;
         int var6 = AutoBuyConfig.cfg().refreshSlot;
         if (var6 >= 0 && var6 < var4) {
            ItemStack var7 = handler.getSlot(var6).getStack();
            if (!var7.isEmpty()) {
               String var8 = Registries.ITEM.getId(var7.getItem()).toString();
               if (var8.equals("minecraft:emerald")) {
                  var5 = var6;
               }
            }
         }

         if (var5 < 0) {
            var5 = this.resolveInt(handler, (rows - 1) * 9, var4, "minecraft:emerald");
         }

         if (var5 >= 0) {
            this.updateState12(syncId, var5);
            this.index5 = this.resolveInt6(AutoBuyConfig.cfg());
            this.timestamp = 0L;
            this.index4 = 0;
         }
      }
   }

   private int resolveInt(GenericContainerScreenHandler handler, int from, int to, String itemId) {
      ArrayList<Integer> var5 = new ArrayList<>();

      for (int var6 = from; var6 < to; var6++) {
         var5.add(var6);
      }

      if (RANDOM.nextInt(2) == 0) {
         Collections.shuffle(var5, RANDOM);
      }

      for (int var7 : var5) {
         ItemStack var8 = handler.getSlot(var7).getStack();
         if (!var8.isEmpty()) {
            String var9 = Registries.ITEM.getId(var8.getItem()).toString();
            if (var9.equals(itemId)) {
               return var7;
            }
         }
      }

      return -1;
   }

   private long resolveLong(GenericContainerScreenHandler handler, int count) {
      long var3 = 1L;

      for (int var5 = 0; var5 < count; var5++) {
         ItemStack var6 = handler.getSlot(var5).getStack();
         var3 = var3 * 31L + (var6.isEmpty() ? 0L : Registries.ITEM.getRawId(var6.getItem()) * 113L + var6.getCount());
      }

      return var3;
   }

   private void updateState9() {
      AutoBuyConfig var1 = AutoBuyConfig.cfg();
      if (this.flag2) {
         this.index10--;
         if (this.text2 != null && this.resolveInt4(this.text2) > this.index9) {
            String var2 = this.text2;
            if (this.purchaseRecord != null) {
               this.purchaseRecord.success = true;
               AutoBuyConfig.cfg().addHistory(this.purchaseRecord);
            }

            this.updateState14();
            boolean var3 = AutoBuyConfig.cfg().autoSell;
            this.updateState13();
            if (var3 && var2 != null) {
               this.seller.trigger();
            }
         } else if (this.index10 <= 0) {
            this.updateState13();
         }
      }

      if (this.index8 > 0) {
         this.index8--;
      } else if (mc.currentScreen instanceof GenericContainerScreen var18 && isConfirmScreen(var18)) {
         if (var1.enabled) {
            GenericContainerScreenHandler var4 = var18.getScreenHandler();
            int var5 = var4.syncId;
            Lot var6 = this.computeLot(var4);
            String var7 = this.volatileString;
            long var8 = this.volatilelong;
            if (var7 == null || var8 <= 0L) {
               this.updateState10(var4, var5);
            } else if (var6 != null && var7.equals(var6.itemId)) {
               String var10 = this.volatileString3;
               String var11 = this.volatileString2;
               if (var10 != null && !var10.isBlank()) {
                  boolean var12 = TextNormalizer.keywordIn(var6.lore, var10);
                  boolean var13 = TextNormalizer.keywordIn(var6.name, var10);
                  if (!var12 && !var13) {
                     this.updateState10(var4, var5);
                     this.volatileString = null;
                     return;
                  }

                  if (this.volatileboolean && var6.name != null && var6.name.toLowerCase(Locale.ROOT).contains(var10.toLowerCase(Locale.ROOT))) {
                     this.updateState10(var4, var5);
                     this.volatileString = null;
                     return;
                  }
               } else if (var11 != null && !var11.isBlank() && !TextNormalizer.nameMatches(var11, var6.name)) {
                  this.updateState10(var4, var5);
                  this.volatileString = null;
                  return;
               }

               if (this.volatileboolean && !var6.hasMarkers) {
                  this.updateState10(var4, var5);
                  this.volatileString = null;
               } else {
                  List<Marker> var19 = this.markers;
                  if (var19 != null && !var19.isEmpty()) {
                     boolean var20 = var19.stream().anyMatch(m -> ItemMatcher.markerMatches(m, var6));
                     if (!var20) {
                        this.updateState10(var4, var5);
                        this.volatileString = null;
                        this.markers = null;
                        return;
                     }
                  } else if (this.volatileboolean) {
                     this.updateState10(var4, var5);
                     this.volatileString = null;
                     this.markers = null;
                     return;
                  }

                  if (var6.unitPrice > var8) {
                     this.updateState10(var4, var5);
                     this.volatileString = null;
                  } else {
                     int var21 = this.resolveInt2(var4);
                     if (var21 >= 0) {
                        this.index8 = this.resolveInt7(var1);
                        if (RANDOM.nextInt(10) == 0) {
                           this.updateState10(var4, var5);
                           this.volatileString = null;
                           return;
                        }

                        this.updateState12(var5, var21);
                        this.flag2 = true;
                        this.text2 = var6.itemId;
                        this.index9 = this.resolveInt4(var6.itemId);
                        this.index10 = 20 + RANDOM.nextInt(10);
                        long var14 = var6.totalPrice > 0L ? var6.totalPrice : var6.unitPrice * Math.max(1, var6.count);
                        String var16 = var6.name != null && !var6.name.isBlank() ? var6.name : var6.itemId;
                        this.purchaseRecord = new PurchaseRecord(
                           System.currentTimeMillis(), var6.itemId, var16, var6.seller, var6.unitPrice, var14, var6.count
                        );
                        this.volatileString = null;
                        this.volatilelong = 0L;
                        this.markers = null;
                     }
                  }
               }
            } else {
               this.updateState10(var4, var5);
               this.volatileString = null;
            }
         }
      }
   }

   private void updateState10(GenericContainerScreenHandler handler, int syncId) {
      this.index8 = this.resolveInt7(AutoBuyConfig.cfg());
      this.updateState11(handler, syncId);
   }

   public static boolean isConfirmScreen(GenericContainerScreen gcs) {
      String var1 = gcs.getTitle().getString();
      return var1.contains("Покупка предмета") || var1.contains("Подтверждение");
   }

   private Lot computeLot(GenericContainerScreenHandler handler) {
      int var2 = handler.getRows() * 9;
      int var3 = RANDOM.nextInt(Math.max(1, var2 / 2));

      for (int var4 = var3; var4 < var2; var4++) {
         ItemStack var5 = handler.getSlot(var4).getStack();
         if (!var5.isEmpty()) {
            String var6 = Registries.ITEM.getId(var5.getItem()).toString();
            if (!var6.contains("stained_glass") && !var6.contains("concrete") && !var6.contains("wool")) {
               Lot var7 = PriceParser.parse(var4, var5);
               if (var7 != null && !var7.barrier) {
                  return var7;
               }
            }
         }
      }

      for (int var8 = 0; var8 < var3; var8++) {
         ItemStack var9 = handler.getSlot(var8).getStack();
         if (!var9.isEmpty()) {
            String var10 = Registries.ITEM.getId(var9.getItem()).toString();
            if (!var10.contains("stained_glass") && !var10.contains("concrete") && !var10.contains("wool")) {
               Lot var11 = PriceParser.parse(var8, var9);
               if (var11 != null && !var11.barrier) {
                  return var11;
               }
            }
         }
      }

      return null;
   }

   private int resolveInt2(GenericContainerScreenHandler handler) {
      int var2 = handler.getRows() * 9;
      int var3 = RANDOM.nextInt(Math.max(1, var2));

      for (int var4 = var3; var4 < var2; var4++) {
         String var5 = Registries.ITEM.getId(handler.getSlot(var4).getStack().getItem()).toString();
         if (var5.contains("lime_stained_glass")
            || var5.contains("green_stained_glass")
            || var5.contains("lime_concrete")
            || var5.contains("green_wool")
            || var5.contains("lime_wool")) {
            return var4;
         }
      }

      for (int var6 = 0; var6 < var3; var6++) {
         String var7 = Registries.ITEM.getId(handler.getSlot(var6).getStack().getItem()).toString();
         if (var7.contains("lime_stained_glass")
            || var7.contains("green_stained_glass")
            || var7.contains("lime_concrete")
            || var7.contains("green_wool")
            || var7.contains("lime_wool")) {
            return var6;
         }
      }

      return -1;
   }

   private int resolveInt3(GenericContainerScreenHandler handler) {
      int var2 = handler.getRows() * 9;

      for (int var3 = 0; var3 < var2; var3++) {
         String var4 = Registries.ITEM.getId(handler.getSlot(var3).getStack().getItem()).toString();
         if (var4.contains("red_stained_glass") || var4.contains("red_concrete") || var4.contains("red_wool")) {
            return var3;
         }
      }

      return -1;
   }

   private void updateState11(GenericContainerScreenHandler handler, int syncId) {
      int var3 = this.resolveInt3(handler);
      if (var3 < 0) {
         mc.player.closeHandledScreen();
      } else {
         this.updateState12(syncId, var3);
      }
   }

   public synchronized void onInsufficientFunds() {
      if (this.flag2) {
         if (this.purchaseRecord != null) {
            this.purchaseRecord.success = false;
            this.purchaseRecord.failReason = "недостаточно средств";
            AutoBuyConfig.cfg().addHistory(this.purchaseRecord);
         }

         this.updateState15("§c[AutoBuy] Недостаточно средств — покупка отменена");
         this.updateState13();
      }
   }

   private int resolveInt4(String itemId) {
      if (mc.player != null && itemId != null) {
         PlayerInventory var2 = mc.player.getInventory();
         int var3 = 0;
         int var4 = var2.size();
         int var5 = RANDOM.nextInt(Math.max(1, var4 / 2));

         for (int var6 = var5; var6 < var4; var6++) {
            ItemStack var7 = var2.getStack(var6);
            if (!var7.isEmpty() && Registries.ITEM.getId(var7.getItem()).toString().equals(itemId)) {
               var3 += var7.getCount();
            }
         }

         for (int var8 = 0; var8 < var5; var8++) {
            ItemStack var9 = var2.getStack(var8);
            if (!var9.isEmpty() && Registries.ITEM.getId(var9.getItem()).toString().equals(itemId)) {
               var3 += var9.getCount();
            }
         }

         return var3;
      } else {
         return 0;
      }
   }

   private void updateState12(int syncId, int slot) {
      if (mc.interactionManager != null && mc.player != null) {
         mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
      }
   }

   private void updateState13() {
      this.flag2 = false;
      this.text2 = null;
      this.index9 = 0;
      this.index10 = 0;
      this.purchaseRecord = null;
   }

   private void updateState14() {
      String var1 = this.purchaseRecord != null ? this.purchaseRecord.label : (this.text2 == null ? "?" : this.text2);
      this.updateState15("§a[AutoBuy] Куплено: §f" + var1);
   }

   private void updateState15(String text) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(text), false);
      }
   }

   private static Long computeLong(String raw) {
      Matcher var1 = value.matcher(raw);
      if (!var1.find()) {
         return null;
      }

      String var2 = var1.group(1).replaceAll("[\\s\\u00A0]+", "");
      int var3 = var2.indexOf(46);
      int var4 = var2.indexOf(44);
      int var5 = var3 < 0 ? var4 : (var4 < 0 ? var3 : Math.min(var3, var4));
      if (var5 >= 0) {
         var2 = var2.substring(0, var5);
      }

      var2 = var2.replaceAll("\\D", "");
      if (var2.isEmpty()) {
         return null;
      }

      try {
         return Long.parseLong(var2);
      } catch (NumberFormatException var7) {
         return null;
      }
   }

   public void forceRefresh() {
      this.index5 = 0;
      this.timestamp = 0L;
      this.index4 = 999;
      this.index19 = 999;
      if (mc.currentScreen instanceof GenericContainerScreen var2 && isAuctionScreen(var2)) {
         GenericContainerScreenHandler var3 = var2.getScreenHandler();
         this.updateState8(var3, var3.syncId, var3.getRows());
      }
   }

   private int resolveInt5(AutoBuyConfig cfg) {
      int var2 = cfg.delayMsForStage(0.18F, 28, 210);
      int var3 = Math.max(0, cfg.clickIntervalTicks) * 16;
      return Math.max(1, this.resolveInt9((var2 + var3 + RANDOM.nextInt(28)) / 100));
   }

   private int resolveInt6(AutoBuyConfig cfg) {
      if (cfg.refreshIntervalTicks > 0) {
         return Math.max(1, cfg.refreshIntervalTicks / 100);
      }

      int var2 = cfg.delayMsForStage(0.45F, 18, 160);
      return Math.max(0, this.resolveInt9((var2 + RANDOM.nextInt(20)) / 100));
   }

   private int resolveInt7(AutoBuyConfig cfg) {
      int var2 = cfg.delayMsForStage(0.7F, 10, 130);
      return Math.max(0, this.resolveInt9((var2 + RANDOM.nextInt(18)) / 100));
   }

   private int resolveInt8(AutoBuyConfig cfg) {
      int var2 = cfg.delayMsForStage(1.0F, 200, 1150);
      return Math.max(1, this.resolveInt9(var2 + RANDOM.nextInt(60)));
   }

   private int resolveInt9(int millis) {
      return (int)Math.ceil(Math.max(0, millis) / 50.0);
   }

   private static int resolveInt10(int min, int max) {
      return min >= max ? min : min + RANDOM.nextInt(max - min + 1);
   }
}
