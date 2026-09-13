package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.baritone.BaritoneControl;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.combat.components.rotations.SlothRotation;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;

public class AutoZamok extends Module {
   public static final AutoZamok INSTANCE = new AutoZamok();
   private static final double[][] value = new double[][]{
      {-12.5, 64.0, 0.4},
      {-5.5, 64.0, -5.0},
      {-0.5, 64.0, -12.5},
      {6.5, 64.0, -5.5},
      {6.3, 64.0, 6.7},
      {6.5, 83.0, 6.4},
      {15.6, 79.0, 15.6},
      {6.5, 83.0, -5.5},
      {15.6, 79.0, -14.7},
      {-5.8, 83.0, 6.6},
      {-14.5, 79.0, 15.6},
      {13.2, 64.0, 0.5},
      {-5.6, 64.0, 6.7},
      {-14.3, 79.0, -14.5},
      {-5.5, 83.0, -5.4},
      {-19.5, 54.0, 6.5},
      {-12.0, 55.0, 34.0},
      {13.0, 57.0, 26.5},
      {-2.6, 57.0, 8.7},
      {-15.3, 64.0, -15.4},
      {16.4, 64.0, -15.3}
   };
   private static final BlockPos BLOCK_POS = new BlockPos(10, 64, 10);
   private static final String[] STRING = new String[]{"инвиз", "невид", "invis"};
   private static final String[] STRING2 = new String[]{"кирк", "кирки", "pick"};
   private static final String[] STRING3 = new String[]{"склад", "store", "sklad"};
   private static final long TIMESTAMP = 350L;
   private static final long IDLE_TIMEOUT_MS = 4000L;
   private static final long TIMESTAMP2 = 8000L;
   private static final long TIMESTAMP3 = 6000L;
   private static final long TIMESTAMP4 = 1000L;
   private static final long TIMESTAMP5 = 1200L;
   private static final long TIMESTAMP6 = 200L;
   private static final long TIMESTAMP7 = 500L;
   private static final long TIMESTAMP8 = 12000L;
   private static final long TIMESTAMP9 = 15000L;
   private static final long TIMESTAMP10 = 60000L;
   private static final long TIMESTAMP11 = 15000L;
   private static final double LEVEL = 144.0;
   private static final long TIMESTAMP12 = 300000L;
   private static final long TIMESTAMP13 = 20000L;
   private static final int INDEX = 3;
   private static final double LEVEL2 = 3.0;
   private static final long TIMESTAMP14 = 20000L;
   private static final int INDEX2 = 10;
   private static final int INDEX3 = 2;
   private static final int INDEX4 = 200;
   private static final int INDEX5 = 17;
   private static final long TIMESTAMP15 = 20000L;
   private static final double LEVEL3 = 30.0;
   private static final long TIMESTAMP16 = 30000L;
   private static final long TIMESTAMP17 = 12000L;
   private static final long TIMESTAMP18 = 120L;
   private static final long TIMESTAMP19 = 180L;
   private static final double LEVEL4 = 35.0;
   private final FloatSetting floatSetting = new FloatSetting("Боксов за вылазку", 1.0F, 1.0F, 9.0F, 1.0F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Отладка в чат", true);
   private AutoZamok.State state2 = AutoZamok.State.TP_TO_SKLAD;
   private long timestamp;
   private long timestamp2;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private Vec3d vec3d;
   private int index2;
   private long timestamp3;
   private long timestamp4;
   private int index3;
   private int index4;
   private int index5;
   private int index6;
   private int index7;
   private int index8;
   private boolean flag;
   private float volume;
   private boolean flag2;
   private SlothRotation slothRotation;
   private BlockPos blockPos3 = null;
   private double level = 0.5;
   private double level2 = 0.5;
   private double level3 = 0.5;
   private float volume2 = 1.6F;
   private LivingEntity armorItems;
   private BlockPos blockPos4;
   private long timestamp5;
   private boolean flag3;
   private float volume3;
   private long timestamp6;
   private BlockPos blockPos5;
   private Vec3d vec3d2;
   private long timestamp7;
   private long timestamp8;
   private final Map<BlockPos, Long> hashMap = new HashMap<>();
   private final Map<BlockPos, Long> hashMap2 = new HashMap<>();
   private int index9;
   private int index10;
   private boolean flag4;
   private volatile BlockPos volatileBlockPos;
   private volatile long volatilelong;
   private BlockPos blockPos6;
   private int index11;
   private int index12;
   private int index13;
   private BlockPos blockPos7;
   private long timestamp9;
   private long timestamp10;
   private long timestamp11;
   private BlockPos blockPos8;
   private long timestamp12;
   private long timestamp13;
   private long timestamp14;
   private volatile long volatilelong2;
   private volatile int volatileint;
   private volatile long volatilelong3;
   private volatile boolean volatileboolean;
   private boolean flag5;
   private boolean flag6;
   private long timestamp15;
   private Boolean presenceCached;
   private Boolean presenceCached2;
   private Boolean presenceCached3;
   private boolean flag7;

   public AutoZamok() {
      super("AutoZamok", "Автофарм шалкербоксов на замке через Baritone", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting, this.booleanSetting);
   }

   @Override
   public void onEnable() {
      this.updateState34(AutoZamok.State.TP_TO_SKLAD);
      this.timestamp4 = 0L;
      this.volatilelong2 = 0L;
      this.volume = 0.0F;
      this.flag2 = false;
      this.flag5 = false;
      this.flag6 = false;
      this.index11 = 0;
      this.vec3d2 = null;
      this.volatileBlockPos = null;
      this.blockPos6 = null;
      this.hashMap2.clear();
      this.flag7 = false;
      this.toBooleanOrDefault6();
      if (this.slothRotation == null) {
         this.slothRotation = new SlothRotation();
      }

      this.slothRotation.reset();
      this.slothRotation.setVisibleAim(false);
      this.blockPos3 = null;
      this.volume2 = 1.6F;
      this.slothRotation.setSpeedBoost(this.volume2);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.updateState33();
      this.toBooleanOrDefault7();
      this.updateState37();
      this.updateState3();
      this.updateState36();
      if (mc.options != null) {
         mc.options.jumpKey.setPressed(false);
         if (this.flag2) {
            mc.options.sprintKey.setPressed(false);
            this.flag2 = false;
         }
      }

      if (mc.getNetworkHandler() != null) {
         this.updateState20();
      }

      this.flag3 = false;
      this.blockPos = null;
      this.blockPos2 = null;
      this.vec3d = null;
      this.blockPos4 = null;
      this.hashMap.clear();
      this.hashMap2.clear();
      this.volatileBlockPos = null;
      this.blockPos6 = null;
      if (this.slothRotation != null) {
         this.slothRotation.reset();
      }

      RotationStorage.instance.stopRotation();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null && mc.interactionManager != null) {
         long var2 = System.currentTimeMillis();
         if (this.state2 != AutoZamok.State.DRINK_INVIS && !this.flag5 && !this.flag6) {
            this.updateState36();
         }

         if (this.flag3 && this.state2 != AutoZamok.State.BREAK_SHULKER) {
            this.updateState20();
         }

         if (this.checkState11()) {
            this.updateState37();
            this.updateState3();
            if (var2 - this.timestamp8 >= 1000L) {
               this.timestamp8 = var2;
               mc.player.requestRespawn();
               if (mc.currentScreen instanceof DeathScreen) {
                  mc.setScreen(null);
               }
            }

            if (this.state2 != AutoZamok.State.TP_TO_SKLAD && this.state2 != AutoZamok.State.WAIT_SKLAD) {
               this.updateState41("Смерть — возвращаемся за снаряжением");
               this.updateState33();
               this.updateState34(AutoZamok.State.TP_TO_SKLAD);
            }
         } else {
            float var4 = mc.player.getHealth();
            if (this.volume > 0.0F && var4 < this.volume - 0.01F && this.checkState3()) {
               PlayerEntity var5 = this.computePlayerEntity(12.0);
               if (var5 != null) {
                  this.volume = var4;
                  this.updateState12(var2, var5);
                  return;
               }
            }

            this.volume = var4;
            if (!this.checkCondition2(var2) && !this.checkCondition3(var2)) {
               this.updateState7();
               this.updateState8(var2);
               switch (this.state2) {
                  case TP_TO_SKLAD:
                     if (this.toBooleanOrDefault4(var2) && this.toBooleanOrDefault(var2, "home sklad")) {
                        this.updateState41("/home sklad");
                        this.updateState34(AutoZamok.State.WAIT_SKLAD);
                     }
                     break;
                  case WAIT_SKLAD:
                     this.toBooleanOrDefault2(var2, AutoZamok.State.REFILL_INVIS, AutoZamok.State.TP_TO_SKLAD, true);
                     break;
                  case REFILL_INVIS:
                     this.updateState(var2, STRING, AutoZamok.State.REFILL_PICK, true);
                     break;
                  case REFILL_PICK:
                     this.updateState(var2, STRING2, AutoZamok.State.DRINK_INVIS, false);
                     break;
                  case DRINK_INVIS:
                     this.updateState2(var2);
                     break;
                  case TP_TO_ZAMOK:
                     this.updateState38();
                     if (this.toBooleanOrDefault4(var2) && this.toBooleanOrDefault(var2, "home zamok")) {
                        this.updateState41("/home zamok");
                        this.updateState34(AutoZamok.State.WAIT_ZAMOK);
                     }
                     break;
                  case WAIT_ZAMOK:
                     this.toBooleanOrDefault2(var2, AutoZamok.State.GO_ENTRY, AutoZamok.State.TP_TO_ZAMOK, false);
                     break;
                  case GO_ENTRY:
                     this.updateState4(var2);
                     break;
                  case GO_SHULKER:
                     this.updateState6(var2);
                     break;
                  case BREAK_SHULKER:
                     this.updateState17(var2);
                     break;
                  case COLLECT:
                     this.updateState24(var2);
                     break;
                  case GO_EXIT:
                     this.updateState16(var2);
                     break;
                  case FLEE:
                     this.updateState14(var2);
                     break;
                  case TP_TO_DEPOSIT:
                     mc.interactionManager.cancelBlockBreaking();
                     if (this.toBooleanOrDefault4(var2) && this.toBooleanOrDefault(var2, "home sklad")) {
                        this.updateState41("/home sklad (сдача)");
                        this.updateState34(AutoZamok.State.WAIT_DEPOSIT);
                     }
                     break;
                  case WAIT_DEPOSIT:
                     this.toBooleanOrDefault2(var2, AutoZamok.State.DEPOSIT, AutoZamok.State.TP_TO_DEPOSIT, true);
                     break;
                  case DEPOSIT:
                     this.updateState25(var2);
               }

               if (this.checkState8()) {
                  this.updateState30(var2);
               }
            } else {
               this.timestamp7 = var2;
            }
         }
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE && event.getPacket() instanceof ExplosionS2CPacket var2) {
         BlockPos var4 = BlockPos.ofFloored(var2.center());
         if (checkCondition(var4)) {
            this.volatileBlockPos = var4;
            this.volatilelong = System.currentTimeMillis();
            this.timestamp12 = 0L;
         }
      }
   }

   private static boolean checkCondition(BlockPos pos) {
      for (double[] var4 : value) {
         if (pos.getSquaredDistance(var4[0], var4[1], var4[2]) <= 144.0) {
            return true;
         }
      }

      return false;
   }

   public static void onGameMessage(String message) {
      if (message != null && INSTANCE != null && INSTANCE.isEnable() && INSTANCE.toBooleanOrDefault3()) {
         String var1 = message.toLowerCase(Locale.ROOT);
         long var2 = System.currentTimeMillis();
         if (!var1.contains("времени до следующей телепортаци") && (!var1.contains("ошибк") || !var1.contains("телепортаци"))) {
            if (!var1.contains("не двигайтесь") && !var1.contains("начнется через") && !var1.contains("начнётся через")) {
               if (var1.contains("телепортирование") || var1.contains("вы телепортированы")) {
                  INSTANCE.volatileint = 1;
                  INSTANCE.volatilelong3 = var2;
               }
            } else {
               INSTANCE.volatileboolean = true;
            }
         } else {
            long var4 = toBooleanOrDefault5(var1);
            INSTANCE.volatilelong2 = var2 + (var4 > 0L ? var4 * 1000L : 1500L) + 700L;
            INSTANCE.volatileint = -1;
            INSTANCE.volatilelong3 = var2;
         }
      }
   }

   private void updateState(long now, String[] keywords, AutoZamok.State nextState, boolean invis) {
      if (invis ? !this.checkState7() : !this.checkState6()) {
         if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var7)) {
            if (this.blockPos == null) {
               this.blockPos = this.computeBlockPos3(keywords, now, false);
               if (this.blockPos == null) {
                  if (now - this.timestamp > 6000L) {
                     this.updateState41("Не найден сундук с табличкой " + keywords[0]);
                     this.updateState34(nextState);
                  }

                  return;
               }
            }

            if (now - this.timestamp2 >= 350L) {
               this.updateState28(this.blockPos);
               this.timestamp2 = now;
            }
         } else if (now - this.timestamp2 >= 350L) {
            Slot var8 = this.computeSlot(var7, invis ? this::checkCondition8 : this::checkCondition7);
            if (var8 != null) {
               mc.interactionManager.clickSlot(var7.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, mc.player);
               this.timestamp2 = now;
               this.updateState41("Взял предмет из сундука " + keywords[0]);
               if (invis && !this.checkState7() && !this.checkState5()) {
                  return;
               }
            } else if (!invis || this.resolveInt2(this::checkCondition8) == 0) {
               var8 = this.computeSlot(var7, stack -> true);
               if (var8 != null) {
                  mc.interactionManager.clickSlot(var7.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                  this.timestamp2 = now;
                  this.updateState41("Взял предмет из сундука " + keywords[0]);
               }
            }

            this.updateState38();
            this.updateState34(nextState);
         }
      } else {
         this.updateState38();
         this.updateState34(nextState);
      }
   }

   private void updateState2(long now) {
      if (mc.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
         this.updateState36();
         this.updateState34(AutoZamok.State.TP_TO_ZAMOK);
      } else {
         int var3 = this.resolveInt4(this::checkCondition8);
         if (var3 == -1 || now - this.timestamp > 5000L) {
            this.updateState36();
            this.updateState34(AutoZamok.State.TP_TO_ZAMOK);
         } else if (var3 > 8) {
            this.updateState29(var3, now);
         } else {
            this.updateState39(var3);
            mc.player.setPitch(-90.0F);
            this.updateState35();
         }
      }
   }

   private boolean checkCondition2(long now) {
      if (!this.checkState2() || this.flag5) {
         this.updateState3();
         return false;
      }

      if (!this.flag6 && !this.checkState()) {
         return false;
      }

      if (!this.flag6 || (this.checkState() || mc.player.isUsingItem()) && now - this.timestamp15 <= 8000L) {
         int var3 = this.resolveInt4(this::checkCondition8);
         if (var3 == -1) {
            this.updateState3();
            return false;
         }

         if (!this.flag6) {
            this.flag6 = true;
            this.timestamp15 = now;
            this.updateState41("Обновляю невидимость");
         }

         this.updateState33();
         if (var3 > 8) {
            this.updateState29(var3, now);
            return true;
         } else {
            this.updateState39(var3);
            mc.player.setPitch(-90.0F);
            this.updateState35();
            return true;
         }
      } else {
         this.updateState3();
         return false;
      }
   }

   private void updateState3() {
      if (this.flag6) {
         this.updateState36();
         this.flag6 = false;
      }
   }

   private boolean checkState() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      return var1 == null ? true : !var1.isInfinite() && var1.getDuration() <= 200;
   }

   private boolean checkState2() {
      return this.state2 == AutoZamok.State.GO_ENTRY
         || this.state2 == AutoZamok.State.GO_SHULKER
         || this.state2 == AutoZamok.State.BREAK_SHULKER
         || this.state2 == AutoZamok.State.COLLECT
         || this.state2 == AutoZamok.State.GO_EXIT;
   }

   private boolean checkCondition3(long now) {
      if (!this.checkState2()) {
         this.updateState37();
         return false;
      } else {
         boolean var3 = mc.player.getHungerManager().getFoodLevel() <= 17;
         if (!this.flag5 && !var3) {
            return false;
         } else if (this.flag5 && !var3 && !mc.player.isUsingItem()) {
            this.updateState37();
            return false;
         } else {
            int var4 = this.resolveInt4(this::checkCondition9);
            if (var4 == -1) {
               this.updateState37();
               return false;
            } else {
               this.flag5 = true;
               this.updateState33();
               if (var4 > 8) {
                  this.updateState29(var4, now);
                  return true;
               } else {
                  this.updateState39(var4);
                  mc.player.setPitch(-90.0F);
                  this.updateState35();
                  return true;
               }
            }
         }
      }
   }

   private void updateState4(long now) {
      if (!(mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(BLOCK_POS)) <= 9.0)
         && (now - this.timestamp <= 2000L || this.computeBlockPos(now) == null)
         && now - this.timestamp <= 20000L) {
         if (now - this.timestamp > 1000L) {
            this.updateState31(BLOCK_POS, now);
         }
      } else {
         this.updateState5(now);
      }
   }

   private void updateState5(long now) {
      this.index2 = 0;
      this.timestamp3 = now;
      this.blockPos4 = null;
      this.flag = false;
      this.updateState34(AutoZamok.State.GO_SHULKER);
   }

   private void updateState6(long now) {
      BlockPos var3 = this.computeBlockPos(now);
      if (var3 != null) {
         this.flag = false;
         if (!var3.equals(this.blockPos4)) {
            this.blockPos4 = var3;
            this.timestamp5 = now;
         }

         if (mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var3)) <= 9.0) {
            this.updateState33();
            this.blockPos2 = var3;
            this.timestamp11 = 0L;
            this.flag3 = false;
            this.volume3 = 0.0F;
            this.timestamp6 = 0L;
            this.updateState21();
            this.index12 = this.resolveInt2(null);
            this.index13 = this.resolveInt2(this::checkCondition5);
            this.state2 = AutoZamok.State.BREAK_SHULKER;
            this.timestamp = now;
         } else if (now - this.timestamp5 > 15000L) {
            this.updateState41("Бокс недоступен, пропускаю");
            this.hashMap.put(var3, now);
            this.blockPos4 = null;
            this.timestamp12 = 0L;
            this.updateState33();
         } else {
            this.updateState31(var3, now);
         }
      } else {
         this.blockPos4 = null;
         BlockPos var4 = this.volatileBlockPos;
         if (var4 != null) {
            if (now - this.volatilelong <= 15000L && !(mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(var4)) <= 9.0)) {
               if (!var4.equals(this.blockPos6)) {
                  this.blockPos6 = var4;
                  this.updateState41("Взрыв у точки спавна — иду туда");
               }

               this.timestamp3 = now;
               this.updateState31(var4, now);
               return;
            }

            this.volatileBlockPos = null;
         }

         int var5 = this.resolveInt();
         if (var5 == -1) {
            this.timestamp7 = now;
            if (now - this.timestamp3 > 30000L) {
               this.timestamp3 = now;
               this.flag = false;
               this.updateState10(now);
            } else {
               if (!this.flag) {
                  this.updateState11();
                  this.updateState41("Боксов нет — жду спавн в укрытии");
               }

               double var10 = mc.player.getX() - (this.index7 + 0.5);
               double var8 = mc.player.getZ() - (this.index8 + 0.5);
               if (var10 * var10 + var8 * var8 <= 9.0) {
                  this.updateState33();
               } else {
                  this.updateState32(this.index7, this.index8, now);
               }
            }
         } else {
            this.flag = false;
            if (var5 != this.index2) {
               this.index2 = var5;
               this.timestamp3 = now;
            }

            double[] var6 = value[this.index2];
            BlockPos var7 = BlockPos.ofFloored(var6[0], var6[1], var6[2]);
            if (!(mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(var7)) <= 9.0) && now - this.timestamp3 <= 12000L) {
               this.updateState31(var7, now);
            } else {
               this.updateState9(now);
            }
         }
      }
   }

   private void updateState7() {
      boolean var1 = mc.player.isTouchingWater() && this.checkState10();
      if (var1) {
         mc.options.sprintKey.setPressed(true);
         this.flag2 = true;
      } else if (this.flag2) {
         mc.options.sprintKey.setPressed(false);
         this.flag2 = false;
      }
   }

   private void updateState8(long now) {
      boolean var3 = this.state2 == AutoZamok.State.GO_ENTRY
         || this.state2 == AutoZamok.State.GO_SHULKER
         || this.state2 == AutoZamok.State.GO_EXIT;
      Vec3d var4 = mc.player.getPos();
      if (var3 && this.vec3d2 != null && !(var4.squaredDistanceTo(this.vec3d2) > 0.25)) {
         if (now - this.timestamp7 > 20000L) {
            this.updateState41("Застрял — повторяю заход на замок");
            if (this.blockPos4 != null) {
               this.hashMap.put(this.blockPos4, now);
               this.blockPos4 = null;
            }

            this.vec3d2 = null;
            this.timestamp7 = now;
            this.updateState33();
            this.updateState34(this.state2 == AutoZamok.State.GO_EXIT ? AutoZamok.State.TP_TO_DEPOSIT : AutoZamok.State.TP_TO_ZAMOK);
         }
      } else {
         this.vec3d2 = var4;
         this.timestamp7 = now;
      }
   }

   private void updateState9(long now) {
      this.updateState33();
      this.timestamp3 = now;
      if (++this.index2 >= value.length) {
         this.index2 = 0;
         this.updateState10(now);
      }
   }

   private void updateState10(long now) {
      if (this.index11 <= 0 && !this.checkState4()) {
         this.hashMap.clear();
      } else {
         this.updateState41("Обход завершён, боксов выбито: " + this.index11);
         this.updateState15(now);
      }
   }

   private void updateState11() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();
      double var2 = -1.0;

      for (int var4 = 0; var4 < 8; var4++) {
         double var5 = var1.nextDouble(Math.PI * 2);
         double var7 = BLOCK_POS.getX() + 0.5 + Math.cos(var5) * 35.0;
         double var9 = BLOCK_POS.getZ() + 0.5 + Math.sin(var5) * 35.0;
         double var11 = Double.MAX_VALUE;

         for (double[] var16 : value) {
            double var17 = var7 - var16[0];
            double var19 = var9 - var16[2];
            var11 = Math.min(var11, var17 * var17 + var19 * var19);
         }

         if (var11 > var2) {
            var2 = var11;
            this.index7 = MathHelper.floor(var7);
            this.index8 = MathHelper.floor(var9);
         }
      }

      this.flag = true;
   }

   private boolean checkCondition4(int index) {
      double[] var2 = value[index];
      return mc.world.isChunkLoaded(MathHelper.floor(var2[0]) >> 4, MathHelper.floor(var2[2]) >> 4);
   }

   private int resolveInt() {
      for (int var1 = 0; var1 < value.length; var1++) {
         int var2 = (this.index2 + var1) % value.length;
         if (!this.checkCondition4(var2)) {
            return var2;
         }
      }

      return -1;
   }

   private boolean checkState3() {
      return switch (this.state2) {
         case TP_TO_SKLAD, WAIT_SKLAD, TP_TO_ZAMOK, WAIT_ZAMOK, GO_ENTRY, GO_SHULKER, BREAK_SHULKER, COLLECT, GO_EXIT, FLEE, TP_TO_DEPOSIT, WAIT_DEPOSIT -> true;
         default -> false;
      };
   }

   private void updateState12(long now, PlayerEntity attacker) {
      this.updateState37();
      this.updateState3();
      this.updateState20();
      if (mc.options != null) {
         mc.options.jumpKey.setPressed(false);
      }

      this.volatileBlockPos = null;
      this.updateState13(attacker);
      if (this.state2 != AutoZamok.State.FLEE) {
         this.updateState41("Получил урон от игрока — убегаю");
         this.updateState34(AutoZamok.State.FLEE);
      } else {
         this.timestamp = now;
      }
   }

   private void updateState13(PlayerEntity attacker) {
      Vec3d var2 = mc.player.getPos();
      Vec3d var3 = new Vec3d(var2.x - attacker.getX(), 0.0, var2.z - attacker.getZ());
      if (var3.lengthSquared() < 0.01) {
         var3 = new Vec3d(1.0, 0.0, 0.0);
      }

      var3 = var3.normalize();
      this.index5 = MathHelper.floor(var2.x + var3.x * 25.0);
      this.index6 = MathHelper.floor(var2.z + var3.z * 25.0);
   }

   private void updateState14(long now) {
      PlayerEntity var3 = this.computePlayerEntity(20.0);
      if (var3 != null && now - this.timestamp <= 12000L) {
         double var4 = mc.player.getX() - this.index5;
         double var6 = mc.player.getZ() - this.index6;
         if (var4 * var4 + var6 * var6 <= 9.0) {
            this.updateState13(var3);
         }

         this.updateState32(this.index5, this.index6, now);
      } else {
         this.updateState33();
         this.updateState34(AutoZamok.State.TP_TO_DEPOSIT);
      }
   }

   private PlayerEntity computePlayerEntity(double range) {
      PlayerEntity var3 = null;
      double var4 = range * range;

      for (PlayerEntity var7 : mc.world.getPlayers()) {
         if (var7 != mc.player && !var7.isSpectator()) {
            double var8 = var7.squaredDistanceTo(mc.player);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private void updateState15(long now) {
      Vec3d var3 = mc.player.getPos();
      Vec3d var4 = Vec3d.ofCenter(BLOCK_POS);
      Vec3d var5 = new Vec3d(var3.x - var4.x, 0.0, var3.z - var4.z);
      if (var5.lengthSquared() < 1.0) {
         var5 = new Vec3d(1.0, 0.0, 0.0);
      }

      var5 = var5.normalize();
      this.index3 = MathHelper.floor(var4.x + var5.x * 30.0);
      this.index4 = MathHelper.floor(var4.z + var5.z * 30.0);
      this.updateState41("Ухожу из замка перед телепортом");
      this.updateState34(AutoZamok.State.GO_EXIT);
   }

   private void updateState16(long now) {
      double var3 = mc.player.getX() - this.index3;
      double var5 = mc.player.getZ() - this.index4;
      if (!(var3 * var3 + var5 * var5 <= 16.0) && now - this.timestamp <= 20000L) {
         this.updateState32(this.index3, this.index4, now);
      } else {
         this.updateState33();
         this.updateState34(AutoZamok.State.TP_TO_DEPOSIT);
      }
   }

   private void updateState17(long now) {
      BlockPos var3 = this.blockPos2;
      if (var3 == null) {
         this.updateState20();
         this.updateState34(AutoZamok.State.COLLECT);
      } else {
         boolean var4 = now - this.timestamp >= 20000L;
         if (this.checkCondition6(var3)) {
            this.timestamp11 = 0L;
            double var5 = 3.5;
            if (mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var3)) > var5 * var5) {
               this.updateState20();
               this.updateState34(AutoZamok.State.GO_SHULKER);
               return;
            }

            if (!this.checkCondition10(now)) {
               return;
            }

            this.updateState22(var3);
            if (!this.flag3) {
               if (now >= this.timestamp6) {
                  this.updateState18(var3);
               }
            } else {
               this.volume3 = this.volume3 + mc.world.getBlockState(var3).calcBlockBreakingDelta(mc.player, mc.world, var3);
               mc.player.swingHand(Hand.MAIN_HAND);
               if (this.volume3 >= 1.0F) {
                  this.updateState19(now);
               }
            }
         } else {
            this.flag3 = false;
            if (this.timestamp11 == 0L) {
               this.timestamp11 = now;
            }

            if (now - this.timestamp11 >= 1200L) {
               this.vec3d = Vec3d.ofCenter(var3);
               this.updateState34(AutoZamok.State.COLLECT);
               return;
            }
         }

         if (var4) {
            this.updateState20();
            this.vec3d = Vec3d.ofCenter(var3);
            this.updateState34(AutoZamok.State.COLLECT);
         }
      }
   }

   private void updateState18(BlockPos pos) {
      Direction var2 = this.computeDirection(pos);
      mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, var2, sequence));
      this.flag3 = true;
      this.blockPos5 = pos;
      this.volume3 = 0.0F;
      mc.player.swingHand(Hand.MAIN_HAND);
   }

   private void updateState19(long now) {
      BlockPos var3 = this.blockPos5;
      Direction var4 = this.computeDirection(var3);
      mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var3, var4, sequence));
      this.flag3 = false;
      this.timestamp6 = now + 120L + ThreadLocalRandom.current().nextLong(180L);
      this.updateState21();
   }

   private void updateState20() {
      if (this.flag3) {
         mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.blockPos5, Direction.DOWN));
         this.flag3 = false;
      }

      if (this.slothRotation != null) {
         this.slothRotation.reset();
      }

      RotationStorage.instance.stopRotation();
      this.blockPos3 = null;
   }

   private void updateState21() {
      this.blockPos3 = null;
   }

   private void updateState22(BlockPos pos) {
      if (mc.player != null && this.slothRotation != null) {
         this.updateState23(pos);
         Vec3d var2 = new Vec3d(pos.getX() + this.level, pos.getY() + this.level2, pos.getZ() + this.level3);
         LivingEntity var3 = this.computeLivingEntity(var2);
         this.slothRotation.setPrecisionLock(false);
         this.slothRotation.updateRotations(var3);
      }
   }

   private void updateState23(BlockPos pos) {
      if (!pos.equals(this.blockPos3)) {
         this.blockPos3 = pos.toImmutable();
         ThreadLocalRandom var2 = ThreadLocalRandom.current();
         this.level = 0.32 + var2.nextDouble() * 0.36;
         this.level2 = 0.28 + var2.nextDouble() * 0.44;
         this.level3 = 0.32 + var2.nextDouble() * 0.36;
         this.volume2 = var2.nextFloat() < 0.08F ? 0.95F + var2.nextFloat() * 0.25F : 1.3F + var2.nextFloat() * 0.6F;
         this.slothRotation.setSpeedBoost(this.volume2);
      }
   }

   private LivingEntity computeLivingEntity(Vec3d pos) {
      if (this.armorItems == null) {
         this.armorItems = new LivingEntity(EntityType.ARMOR_STAND, mc.world) {
            @Override
            public Iterable<ItemStack> getArmorItems() {
               return Collections.emptyList();
            }

            @Override
            public ItemStack getEquippedStack(EquipmentSlot slot) {
               return ItemStack.EMPTY;
            }

            @Override
            public void equipStack(EquipmentSlot slot, ItemStack stack) {
            }

            @Override
            public Hand getActiveHand() {
               return Hand.MAIN_HAND;
            }

            @Override
            public Arm getMainArm() {
               return Arm.RIGHT;
            }
         };
         this.armorItems.setInvisible(true);
         this.armorItems.setNoGravity(true);
         this.armorItems.setInvulnerable(true);
      }

      this.armorItems.setPosition(pos);
      this.armorItems.setBoundingBox(new Box(pos.x - 0.3, pos.y - 0.3, pos.z - 0.3, pos.x + 0.3, pos.y + 0.3, pos.z + 0.3));
      return this.armorItems;
   }

   private Direction computeDirection(BlockPos box) {
      Vec3d var2 = mc.player.getEyePos().subtract(Vec3d.ofCenter(box));
      double var3 = Math.abs(var2.x);
      double var5 = Math.abs(var2.y);
      double var7 = Math.abs(var2.z);
      if (var3 >= var5 && var3 >= var7) {
         return var2.x >= 0.0 ? Direction.EAST : Direction.WEST;
      } else if (var7 >= var3 && var7 >= var5) {
         return var2.z >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      } else {
         return var2.y >= 0.0 ? Direction.UP : Direction.DOWN;
      }
   }

   private void updateState24(long now) {
      if (this.vec3d != null && now - this.timestamp < 500L) {
         this.updateState31(BlockPos.ofFloored(this.vec3d), now);
      }

      if (now - this.timestamp >= 3000L) {
         this.updateState33();
         boolean var3 = this.resolveInt2(this::checkCondition5) > this.index13 || this.resolveInt2(null) > this.index12;
         if (var3) {
            this.index11++;
            this.updateState41("Бокс выбит (" + this.index11 + "/" + (int)this.floatSetting.get() + ")");
         } else {
            this.updateState41("Бокс достался не нам, продолжаю обход");
         }

         if ((!var3 || this.index11 < (int)this.floatSetting.get()) && !this.checkState5() && !this.checkState4()) {
            this.timestamp3 = now;
            this.blockPos4 = null;
            this.updateState34(AutoZamok.State.GO_SHULKER);
         } else {
            this.updateState15(now);
         }
      }
   }

   private boolean checkState4() {
      return this.resolveInt4(this::checkCondition7) == -1 ? true : this.checkState() && this.resolveInt4(this::checkCondition8) == -1;
   }

   private boolean checkState5() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private int resolveInt2(Predicate<ItemStack> filter) {
      int var2 = 0;
      int var3 = mc.player.getInventory().size();

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && (filter == null || filter.test(var5))) {
            var2 += var5.getCount();
         }
      }

      return var2;
   }

   private boolean checkCondition5(ItemStack stack) {
      return stack != null && !stack.isEmpty() && Registries.ITEM.getId(stack.getItem()).getPath().endsWith("shulker_box");
   }

   private void updateState25(long now) {
      if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var4) {
         if (now - this.timestamp2 >= 350L) {
            int var5 = this.resolveInt2(null);
            if (this.flag4) {
               this.flag4 = false;
               if (var5 == this.index10) {
                  if (++this.index9 >= 2) {
                     this.updateState41("Сундук забит — ищу другой");
                     this.updateState27(this.blockPos, now);
                     this.updateState38();
                     this.updateState34(AutoZamok.State.DEPOSIT);
                     return;
                  }
               } else {
                  this.index9 = 0;
               }
            }

            int var6 = var4.getInventory().size();

            for (Slot var8 : var4.slots) {
               if (var8.id >= var6) {
                  ItemStack var9 = var8.getStack();
                  if (!var9.isEmpty() && !this.checkCondition7(var9) && !this.checkCondition8(var9) && !this.checkCondition9(var9)) {
                     mc.interactionManager.clickSlot(var4.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                     this.timestamp2 = now;
                     this.index10 = var5;
                     this.flag4 = true;
                     return;
                  }
               }
            }

            this.updateState41("Ресурсы сложены");
            this.updateState38();
            this.updateState26();
         }
      } else {
         if (this.blockPos == null) {
            this.blockPos = this.computeBlockPos3(STRING3, now, true);
            if (this.blockPos == null) {
               if (now - this.timestamp > 6000L) {
                  this.updateState41(this.hashMap2.isEmpty() ? "Не найден сундук 'склад'" : "Все сундуки 'склад' забиты");
                  this.updateState26();
               }

               return;
            }
         }

         if (now - this.timestamp2 >= 350L) {
            this.updateState28(this.blockPos);
            this.timestamp2 = now;
         }
      }
   }

   private void updateState26() {
      this.updateState38();
      this.index11 = 0;
      this.hashMap.clear();
      this.updateState34(AutoZamok.State.REFILL_INVIS);
   }

   private BlockPos computeBlockPos(long now) {
      if (now - this.timestamp12 >= 200L) {
         this.timestamp12 = now;
         this.hashMap.values().removeIf(time -> now - time > 60000L);
         this.blockPos8 = this.computeBlockPos2();
      }

      return this.blockPos8;
   }

   private BlockPos computeBlockPos2() {
      byte var1 = 3;
      double var2 = var1 * 1.7321 + 1.0;
      Vec3d var4 = mc.player.getEyePos();
      Mutable var5 = new Mutable();
      BlockPos var6 = null;
      double var7 = Double.MAX_VALUE;

      for (double[] var12 : value) {
         double var13 = Math.sqrt(var4.squaredDistanceTo(var12[0], var12[1], var12[2])) - var2;
         if (!(var13 > 0.0) || !(var13 * var13 >= var7)) {
            int var15 = MathHelper.floor(var12[0]);
            int var16 = MathHelper.floor(var12[1]);
            int var17 = MathHelper.floor(var12[2]);

            for (int var18 = -var1; var18 <= var1; var18++) {
               for (int var19 = -var1; var19 <= var1; var19++) {
                  for (int var20 = -var1; var20 <= var1; var20++) {
                     var5.set(var15 + var18, var16 + var19, var17 + var20);
                     if (mc.world.getBlockState(var5).getBlock() instanceof ShulkerBoxBlock && !this.hashMap.containsKey(var5)) {
                        double var21 = var4.squaredDistanceTo(var5.getX() + 0.5, var5.getY() + 0.5, var5.getZ() + 0.5);
                        if (var21 < var7) {
                           var7 = var21;
                           var6 = var5.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }

      return var6;
   }

   private boolean checkCondition6(BlockPos pos) {
      return mc.world.getBlockState(pos).getBlock() instanceof ShulkerBoxBlock;
   }

   private BlockPos computeBlockPos3(String[] keywords, long now, boolean ordered) {
      if (now - this.timestamp13 < 500L) {
         return null;
      }

      this.timestamp13 = now;
      this.hashMap2.values().removeIf(time -> now - time > 300000L);
      byte var5 = 10;
      double var6 = var5 * var5;
      BlockPos var8 = mc.player.getBlockPos();
      BlockPos var9 = null;
      double var10 = Double.MAX_VALUE;
      int var12 = Integer.MAX_VALUE;
      int var13 = (var5 >> 4) + 1;
      int var14 = mc.player.getChunkPos().x;
      int var15 = mc.player.getChunkPos().z;

      for (int var16 = var14 - var13; var16 <= var14 + var13; var16++) {
         for (int var17 = var15 - var13; var17 <= var15 + var13; var17++) {
            if (mc.world.isChunkLoaded(var16, var17)) {
               WorldChunk var18 = mc.world.getChunk(var16, var17);
               if (var18 != null) {
                  for (BlockEntity var20 : var18.getBlockEntities().values()) {
                     if (var20 instanceof SignBlockEntity var21 && !(var8.getSquaredDistance(var21.getPos()) > var6)) {
                        int var22 = this.resolveInt3(var21, keywords);
                        if (var22 >= 0) {
                           if (!ordered) {
                              var22 = 0;
                           }

                           BlockPos var23 = this.computeBlockPos4(var21.getPos());
                           if (var23 != null && !this.hashMap2.containsKey(var23)) {
                              double var24 = var8.getSquaredDistance(var23);
                              if (var22 < var12 || var22 == var12 && var24 < var10) {
                                 var12 = var22;
                                 var10 = var24;
                                 var9 = var23;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var9;
   }

   private int resolveInt3(SignBlockEntity sign, String[] keywords) {
      StringBuilder var3 = new StringBuilder();

      for (int var4 = 0; var4 < 4; var4++) {
         var3.append(sign.getFrontText().getMessage(var4, false).getString()).append(' ');
         var3.append(sign.getBackText().getMessage(var4, false).getString()).append(' ');
      }

      String var13 = var3.toString().toLowerCase(Locale.ROOT);

      for (String var8 : keywords) {
         int var9 = var13.indexOf(var8);
         if (var9 != -1) {
            int var10 = var9 + var8.length();

            while (var10 < var13.length() && !Character.isDigit(var13.charAt(var10)) && !Character.isLetter(var13.charAt(var10))) {
               var10++;
            }

            int var11 = 0;
            boolean var12 = false;

            while (var10 < var13.length() && Character.isDigit(var13.charAt(var10))) {
               var11 = var11 * 10 + (var13.charAt(var10) - '0');
               var12 = true;
               var10++;
            }

            return var12 ? var11 : 1;
         }
      }

      return -1;
   }

   private BlockPos computeBlockPos4(BlockPos signPos) {
      BlockState var2 = mc.world.getBlockState(signPos);
      if (var2.getBlock() instanceof WallSignBlock) {
         BlockPos var3 = signPos.offset(var2.get(WallSignBlock.FACING).getOpposite());
         if (mc.world.getBlockState(var3).getBlock() instanceof ChestBlock) {
            return var3;
         }
      } else if (var2.getBlock() instanceof SignBlock) {
         BlockPos var9 = signPos.down();
         if (mc.world.getBlockState(var9).getBlock() instanceof ChestBlock) {
            return var9;
         }
      }

      if (var2.getBlock() instanceof ChestBlock) {
         return signPos.toImmutable();
      }

      for (Direction var6 : Direction.values()) {
         BlockPos var7 = signPos.offset(var6);
         if (mc.world.getBlockState(var7).getBlock() instanceof ChestBlock) {
            return var7;
         }

         BlockPos var8 = var7.down();
         if (mc.world.getBlockState(var8).getBlock() instanceof ChestBlock) {
            return var8;
         }
      }

      return null;
   }

   private void updateState27(BlockPos pos, long now) {
      this.hashMap2.put(pos, now);
      BlockState var4 = mc.world.getBlockState(pos);
      BlockPos var5 = this.computeBlockPos5(pos, var4);
      if (var5 != null) {
         this.hashMap2.put(var5, now);
      }
   }

   private BlockPos computeBlockPos5(BlockPos pos, BlockState state) {
      if (!(state.getBlock() instanceof ChestBlock)) {
         return null;
      }

      ChestType var3 = state.get(ChestBlock.CHEST_TYPE);
      if (var3 == ChestType.SINGLE) {
         return null;
      }

      Direction var4 = switch ((Direction)state.get(ChestBlock.FACING)) {
         case NORTH -> var3 == ChestType.LEFT ? Direction.EAST : Direction.WEST;
         case SOUTH -> var3 == ChestType.LEFT ? Direction.WEST : Direction.EAST;
         case WEST -> var3 == ChestType.LEFT ? Direction.NORTH : Direction.SOUTH;
         case EAST -> var3 == ChestType.LEFT ? Direction.SOUTH : Direction.NORTH;
         default -> null;
      };
      return var4 == null ? null : pos.offset(var4);
   }

   private void updateState28(BlockPos pos) {
      Vec3d var2 = Vec3d.ofCenter(pos);
      this.updateState40(var2);
      mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var2, Direction.UP, pos, false));
      mc.player.swingHand(Hand.MAIN_HAND);
   }

   private Slot computeSlot(GenericContainerScreenHandler handler, Predicate<ItemStack> filter) {
      int var3 = handler.getInventory().size();

      for (Slot var5 : handler.slots) {
         if (var5.id >= var3) {
            break;
         }

         if (var5.hasStack() && filter.test(var5.getStack())) {
            return var5;
         }
      }

      return null;
   }

   private boolean checkState6() {
      return this.resolveInt4(this::checkCondition7) != -1;
   }

   private boolean checkCondition7(ItemStack stack) {
      return stack != null && !stack.isEmpty() && Registries.ITEM.getId(stack.getItem()).getPath().contains("pickaxe");
   }

   private boolean checkCondition8(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof PotionItem) {
         PotionContentsComponent var2 = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         }

         for (StatusEffectInstance var4 : var2.getEffects()) {
            if (var4.getEffectType().equals(StatusEffects.INVISIBILITY)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean checkCondition9(ItemStack stack) {
      return stack != null && !stack.isEmpty() && !stack.isOf(Items.CHORUS_FRUIT) && stack.getUseAction() == UseAction.EAT;
   }

   private boolean checkState7() {
      int var1 = 2;
      if (!mc.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
         var1++;
      }

      return this.resolveInt2(this::checkCondition8) >= var1;
   }

   private int resolveInt4(Predicate<ItemStack> filter) {
      int var2 = mc.player.getInventory().size();

      for (int var3 = 0; var3 < 9 && var3 < var2; var3++) {
         if (filter.test(mc.player.getInventory().getStack(var3))) {
            return var3;
         }
      }

      for (int var4 = 9; var4 < var2; var4++) {
         if (filter.test(mc.player.getInventory().getStack(var4))) {
            return var4;
         }
      }

      return -1;
   }

   private boolean checkCondition10(long now) {
      int var3 = this.resolveInt4(this::checkCondition7);
      if (var3 == -1) {
         return false;
      } else if (var3 <= 8) {
         this.updateState39(var3);
         return true;
      } else {
         this.updateState29(var3, now);
         return false;
      }
   }

   private void updateState29(int slot, long now) {
      if (now - this.timestamp2 >= 350L) {
         mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slot, 8, SlotActionType.SWAP, mc.player);
         this.timestamp2 = now;
      }
   }

   private boolean checkState8() {
      if (!this.flag5 && !this.flag6) {
         return switch (this.state2) {
            case TP_TO_SKLAD, WAIT_SKLAD, TP_TO_ZAMOK, WAIT_ZAMOK, GO_ENTRY, GO_SHULKER, COLLECT, GO_EXIT, FLEE, TP_TO_DEPOSIT, WAIT_DEPOSIT -> true;
            default -> false;
         };
      } else {
         return false;
      }
   }

   private void updateState30(long now) {
      if (!mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).isEmpty()) {
         for (int var3 = 0; var3 < 9; var3++) {
            if (mc.player.getInventory().getStack(var3).isEmpty()) {
               this.updateState39(var3);
               return;
            }
         }

         if (now - this.timestamp2 >= 350L && this.checkState9()) {
            int var6 = -1;

            for (int var4 = 0; var4 < 9; var4++) {
               ItemStack var5 = mc.player.getInventory().getStack(var4);
               if (!this.checkCondition7(var5) && !this.checkCondition8(var5) && !this.checkCondition9(var5)) {
                  var6 = var4;
                  break;
               }
            }

            if (var6 == -1) {
               for (int var7 = 0; var7 < 9; var7++) {
                  if (!this.checkCondition7(mc.player.getInventory().getStack(var7))) {
                     var6 = var7;
                     break;
                  }
               }
            }

            if (var6 != -1) {
               mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 36 + var6, 0, SlotActionType.QUICK_MOVE, mc.player);
               this.timestamp2 = now;
            }
         }
      }
   }

   private boolean checkState9() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private boolean toBooleanOrDefault(long now, String command) {
      if (now >= this.volatilelong2 && now - this.timestamp4 >= 4000L) {
         this.volatileint = 0;
         this.volatilelong3 = 0L;
         this.volatileboolean = false;
         this.timestamp14 = now;
         this.timestamp4 = now;
         mc.getNetworkHandler().sendChatCommand(command);
         return true;
      } else {
         return false;
      }
   }

   private void toBooleanOrDefault2(long now, AutoZamok.State onSuccess, AutoZamok.State onRetry, boolean nudgeOnArrive) {
      if (this.volatileint == 1) {
         if (nudgeOnArrive) {
            mc.options.jumpKey.setPressed(now - this.volatilelong3 < 300L);
         }

         if (now - this.volatilelong3 >= 1000L) {
            this.updateState34(onSuccess);
         }
      } else if (this.volatileint == -1) {
         if (now >= this.volatilelong2 && now - this.timestamp4 >= 4000L) {
            this.updateState34(onRetry);
         }
      } else {
         if (now - this.timestamp14 > (this.volatileboolean ? 12000L : 8000L)) {
            this.updateState34(onRetry);
         }
      }
   }

   private boolean toBooleanOrDefault3() {
      return this.state2 == AutoZamok.State.WAIT_SKLAD
         || this.state2 == AutoZamok.State.WAIT_ZAMOK
         || this.state2 == AutoZamok.State.WAIT_DEPOSIT;
   }

   private boolean toBooleanOrDefault4(long now) {
      this.updateState33();
      Vec3d var3 = mc.player.getVelocity();
      return Math.abs(var3.x) < 0.05 && Math.abs(var3.z) < 0.05 || now - this.timestamp > 2000L;
   }

   private static long toBooleanOrDefault5(String text) {
      long var1 = 0L;
      boolean var3 = false;

      for (int var4 = 0; var4 < text.length(); var4++) {
         char var5 = text.charAt(var4);
         if (var5 >= '0' && var5 <= '9') {
            var1 = var1 * 10L + (var5 - '0');
            var3 = true;
         } else if (var3) {
            break;
         }
      }

      return var3 ? var1 : 0L;
   }

   private void toBooleanOrDefault6() {
      if (this.presenceCached == null) {
         this.presenceCached = toBooleanOrDefault9(BaritoneControl.getSetting("allowBreak"));
      }

      if (this.presenceCached2 == null) {
         this.presenceCached2 = toBooleanOrDefault9(BaritoneControl.getSetting("allowPlace"));
      }

      if (this.presenceCached3 == null) {
         this.presenceCached3 = toBooleanOrDefault9(BaritoneControl.getSetting("allowParkourPlace"));
      }

      boolean var1 = BaritoneControl.forceBoolSetting("allowBreak", false);
      BaritoneControl.forceBoolSetting("allowPlace", false);
      BaritoneControl.forceBoolSetting("allowParkourPlace", false);
      if (!this.flag7) {
         this.flag7 = true;
         if (var1) {
            this.updateState41("§aBaritone: allowBreak=false — путь копать не будет");
         } else {
            this.updateState41("§eBaritone API не ответил (" + BaritoneControl.getLastSettingError() + ") — запретил копать через #set");
         }
      }
   }

   private void toBooleanOrDefault7() {
      this.toBooleanOrDefault8("allowBreak", this.presenceCached);
      this.toBooleanOrDefault8("allowPlace", this.presenceCached2);
      this.toBooleanOrDefault8("allowParkourPlace", this.presenceCached3);
      this.presenceCached = null;
      this.presenceCached2 = null;
      this.presenceCached3 = null;
      this.flag7 = false;
   }

   private void toBooleanOrDefault8(String name, Boolean previous) {
      if (previous != null) {
         BaritoneControl.setSettingByCommand(name, previous);
      } else {
         BaritoneControl.resetSettingByCommand(name);
      }
   }

   private static Boolean toBooleanOrDefault9(Object o) {
      return o instanceof Boolean var1 ? var1 : null;
   }

   private void updateState31(BlockPos target, long now) {
      if (!target.equals(this.blockPos7) || !this.checkState10() && now - this.timestamp9 >= 6000L) {
         this.toBooleanOrDefault6();
         BaritoneControl.gotoPos(target);
         this.blockPos7 = target;
         this.timestamp9 = now;
      }
   }

   private void updateState32(int x, int z, long now) {
      BlockPos var5 = new BlockPos(x, 0, z);
      if (!var5.equals(this.blockPos7) || !this.checkState10() && now - this.timestamp9 >= 6000L) {
         this.toBooleanOrDefault6();
         BaritoneControl.gotoXZ(x, z);
         this.blockPos7 = var5;
         this.timestamp9 = now;
      }
   }

   private void updateState33() {
      long var1 = System.currentTimeMillis();
      if (this.blockPos7 != null || this.checkState10()) {
         if (var1 - this.timestamp10 >= 1000L) {
            BaritoneControl.stop();
            this.timestamp10 = var1;
            this.blockPos7 = null;
         }
      }
   }

   private boolean checkState10() {
      return BaritoneControl.isPathing();
   }

   private void updateState34(AutoZamok.State next) {
      this.state2 = next;
      this.timestamp = System.currentTimeMillis();
      this.blockPos = null;
      this.blockPos2 = null;
      this.timestamp12 = 0L;
      this.timestamp13 = 0L;
      this.index9 = 0;
      this.flag4 = false;
   }

   private boolean checkState11() {
      return mc.currentScreen instanceof DeathScreen || !mc.player.isAlive();
   }

   private void updateState35() {
      mc.options.useKey.setPressed(true);
      if (!mc.player.isUsingItem() || mc.player.getActiveHand() != Hand.MAIN_HAND) {
         mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      }
   }

   private void updateState36() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }
   }

   private void updateState37() {
      if (this.flag5) {
         this.updateState36();
         this.flag5 = false;
      }
   }

   private void updateState38() {
      if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }
   }

   private void updateState39(int slot) {
      if (slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
      }
   }

   private void updateState40(Vec3d target) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = target.x - var2.x;
      double var5 = target.y - var2.y;
      double var7 = target.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      mc.player.setYaw(MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F));
      mc.player.setPitch(MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(var5, var9))), -90.0F, 90.0F));
   }

   private void updateState41(String message) {
      if (this.booleanSetting.isState()) {
         ChatUtils.sendMessage("[AutoZamok] " + message);
      }
   }

   private enum State {
      TP_TO_SKLAD,
      WAIT_SKLAD,
      REFILL_INVIS,
      REFILL_PICK,
      DRINK_INVIS,
      TP_TO_ZAMOK,
      WAIT_ZAMOK,
      GO_ENTRY,
      GO_SHULKER,
      BREAK_SHULKER,
      COLLECT,
      GO_EXIT,
      FLEE,
      TP_TO_DEPOSIT,
      WAIT_DEPOSIT,
      DEPOSIT;
   }
}
