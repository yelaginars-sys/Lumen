package dlc.lumen.client.modules.impl.misc;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.impl.RCTCommand;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.baritone.BaritoneControl;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.autoset.AutoSetCatalog;
import dlc.lumen.client.autoset.AutoSetCollector;
import dlc.lumen.client.autoset.AutoSetItem;
import dlc.lumen.client.autoset.AutoSetKit;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoCore extends Module {
   public static final AutoCore INSTANCE = new AutoCore();
   private static final long IDLE_TIMEOUT_MS = 4000L;
   private static final long TIMESTAMP = 8000L;
   private static final double LEVEL = 400.0;
   private static final long TIMESTAMP2 = 1200L;
   private static final int INDEX = 32;
   private static final int INDEX2 = 12;
   private static final long TIMESTAMP3 = 1000L;
   private static final long TIMESTAMP4 = 45000L;
   private static final long TIMESTAMP5 = 12000L;
   private static final long TIMESTAMP6 = 3000L;
   private static final float VOLUME = 50.0F;
   private static final long TIMESTAMP7 = 350L;
   private static final int INDEX3 = 200;
   private static final long TIMESTAMP8 = 8000L;
   private static final int INDEX4 = 17;
   private static final long TIMESTAMP9 = 6000L;
   private static final long TIMESTAMP10 = 30000L;
   private static final long TIMESTAMP11 = 7000L;
   private static final double LEVEL2 = 2.25;
   private static final long TIMESTAMP12 = 2000L;
   private static final long TIMESTAMP13 = 15000L;
   private static final long TIMESTAMP14 = 3000L;
   private static final long TIMESTAMP15 = 30000L;
   private static final long TIMESTAMP16 = 15000L;
   private static final double LEVEL3 = 2.0;
   private static final long TIMESTAMP17 = 60000L;
   private static final long TIMESTAMP18 = 60000L;
   private static final String TEXT = "potions|Зелье невидимости";
   private static final double LEVEL4 = 3.0;
   private static final long TIMESTAMP19 = 700L;
   private static final int INDEX5 = 74;
   private final FloatSetting floatSetting = new FloatSetting("Радиус обхода", 3.0F, 1.0F, 9.0F, 1.0F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Паника", true);
   private final FloatSetting floatSetting2 = new FloatSetting("Радиус паники", 6.0F, 1.0F, 7.0F, 1.0F).visible(this.booleanSetting::isState);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Авто инвиз", true);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Автопокупка инвиза", true).visible(this.booleanSetting2::isState);
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Автоеда", true);
   private final BooleanSetting booleanSetting5 = new BooleanSetting("Бить голых", true);
   private final FloatSetting floatSetting3 = new FloatSetting("Радиус атаки", 4.0F, 1.0F, 6.0F, 0.5F).visible(this.booleanSetting5::isState);
   private final FloatSetting floatSetting4 = new FloatSetting("КД ПвП, сек", 20.0F, 5.0F, 40.0F, 1.0F);
   private final BooleanSetting booleanSetting6 = new BooleanSetting("Отладка в чат", true);
   private AutoCore.State state = AutoCore.State.WARP;
   private long timestamp;
   private long timestamp2;
   private Vec3d vec3d;
   private boolean flag;
   private long timestamp3;
   private BlockPos blockPos;
   private long timestamp4;
   private long timestamp5;
   private double level;
   private double level2;
   private boolean flag2;
   private boolean flag3;
   private Vec3d vec3d2;
   private long timestamp6;
   private boolean flag4;
   private boolean flag5;
   private Object object;
   private long timestamp7;
   private long timestamp8;
   private int index;
   private int index2;
   private boolean flag6;
   private long timestamp9;
   private BlockPos blockPos2;
   private long timestamp10;
   private long timestamp11;
   private long timestamp12;
   private int index3 = -1;
   private long timestamp13;
   private float volume;
   private long timestamp14;
   private long timestamp15;
   private long timestamp16;
   private long timestamp17;
   private boolean flag7;
   private long timestamp18;
   private boolean flag8;
   private long timestamp19;
   private Boolean presenceCached;
   private Boolean presenceCached2;
   private Boolean presenceCached3;
   private boolean flag9;

   public AutoCore() {
      super("AutoCore", "Кемпит заряженный якорь возрождения на /warp pvp", Module.ModuleCategory.MISC);
      this.addSettings(
         this.floatSetting,
         this.booleanSetting,
         this.floatSetting2,
         this.booleanSetting2,
         this.booleanSetting3,
         this.booleanSetting4,
         this.booleanSetting5,
         this.floatSetting3,
         this.floatSetting4,
         this.booleanSetting6
      );
   }

   @Override
   public void onEnable() {
      this.updateState14(AutoCore.State.WARP);
      this.timestamp2 = 0L;
      this.timestamp12 = 0L;
      this.volume = 0.0F;
      this.timestamp14 = 0L;
      this.blockPos = null;
      this.flag6 = false;
      this.flag9 = false;
      this.toBooleanOrDefault();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.updateState13();
      this.toBooleanOrDefault2();
      this.updateState15();
      this.updateState16();
      this.updateState17();
      this.blockPos = null;
      this.flag6 = false;
      super.onDisable();
   }

   private void toBooleanOrDefault() {
      if (this.presenceCached == null) {
         this.presenceCached = toBooleanOrDefault3(BaritoneControl.getSetting("allowBreak"));
      }

      if (this.presenceCached2 == null) {
         this.presenceCached2 = toBooleanOrDefault3(BaritoneControl.getSetting("allowPlace"));
      }

      if (this.presenceCached3 == null) {
         this.presenceCached3 = toBooleanOrDefault3(BaritoneControl.getSetting("allowParkourPlace"));
      }

      boolean var1 = BaritoneControl.forceBoolSetting("allowBreak", false);
      BaritoneControl.forceBoolSetting("allowPlace", false);
      BaritoneControl.forceBoolSetting("allowParkourPlace", false);
      if (!this.flag9) {
         this.flag9 = true;
         if (var1) {
            this.updateState23("§aBaritone: allowBreak=false — ничего не копаем");
         } else {
            this.updateState23("§eBaritone API не ответил (" + BaritoneControl.getLastSettingError() + ") — запретил копать через #set");
         }
      }
   }

   private void toBooleanOrDefault2() {
      if (this.presenceCached != null) {
         BaritoneControl.setSetting("allowBreak", this.presenceCached);
      }

      if (this.presenceCached2 != null) {
         BaritoneControl.setSetting("allowPlace", this.presenceCached2);
      }

      if (this.presenceCached3 != null) {
         BaritoneControl.setSetting("allowParkourPlace", this.presenceCached3);
      }

      this.presenceCached = null;
      this.presenceCached2 = null;
      this.presenceCached3 = null;
   }

   private static Boolean toBooleanOrDefault3(Object o) {
      return o instanceof Boolean var1 ? var1 : null;
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
         long var2 = System.currentTimeMillis();
         if (RCTCommand.RUNNING && this.state != AutoCore.State.WAIT_RCT) {
            this.updateState15();
            this.updateState16();
            this.updateState17();
         } else if (AutoSetCollector.INSTANCE.isRunning() && this.state != AutoCore.State.BUY_INVIS) {
            this.updateState15();
            this.updateState16();
            this.updateState17();
         } else if (this.checkState6()) {
            if (var2 - this.timestamp13 >= 1000L) {
               this.timestamp13 = var2;
               mc.player.requestRespawn();
               if (mc.currentScreen instanceof DeathScreen) {
                  mc.setScreen(null);
               }

               this.timestamp16 = 0L;
            }

            if (this.state != AutoCore.State.WARP) {
               this.updateState23("Смерть — заново на /warp pvp");
               this.updateState13();
               this.updateState14(AutoCore.State.WARP);
            }
         } else {
            float var4 = mc.player.getHealth();
            if (this.volume > 0.0F && var4 < this.volume - 0.01F && this.checkState()) {
               PlayerEntity var5 = this.computePlayerEntity(12.0);
               if (var5 != null) {
                  this.volume = var4;
                  this.updateState3(var2, var5);
                  return;
               }
            }

            this.volume = var4;
            if ((
                  this.state == AutoCore.State.WARP
                     || this.state == AutoCore.State.SEARCH
                     || this.state == AutoCore.State.GO_ANCHOR
                     || this.state == AutoCore.State.PATROL
               )
               && this.checkCondition(var2)
               && this.checkCondition2(var2)) {
               this.updateState13();
               this.updateState14(AutoCore.State.BUY_INVIS);
            } else if (!this.checkCondition8(var2) && !this.checkCondition9(var2)) {
               if (this.checkState3()) {
                  this.updateState18(var2);
               }

               if (this.booleanSetting.isState() && this.state == AutoCore.State.PATROL && var2 - this.timestamp12 >= 15000L) {
                  PlayerEntity var11 = this.computePlayerEntity3(this.floatSetting2.get());
                  if (var11 != null) {
                     this.timestamp12 = var2;
                     this.updateState23("§cПаника: рядом " + var11.getGameProfile().getName() + " — меняю анархию");
                     this.updateState13();
                     this.updateState10(this.resolveInt());
                     this.updateState14(AutoCore.State.WAIT_RCT);
                     return;
                  }
               }

               switch (this.state) {
                  case WARP:
                     if (var2 - this.timestamp2 < 4000L) {
                        return;
                     }

                     this.updateState13();
                     this.timestamp2 = var2;
                     this.vec3d = mc.player.getPos();
                     this.flag = false;
                     mc.getNetworkHandler().sendChatCommand("warp pvp");
                     this.updateState23("/warp pvp");
                     this.updateState14(AutoCore.State.WAIT_TP);
                     break;
                  case WAIT_TP:
                     if (!this.flag && this.vec3d != null && mc.player.getPos().squaredDistanceTo(this.vec3d) >= 400.0) {
                        this.flag = true;
                        this.timestamp3 = var2;
                     }

                     if (this.flag && var2 - this.timestamp3 >= 1200L) {
                        this.updateState23("Телепорт есть — бегу вперёд, ищу якорь");
                        this.updateState5(true);
                        return;
                     }

                     if (var2 - this.timestamp > 8000L) {
                        this.updateState23("Телепорт не подтвердился — сканирую на месте");
                        this.updateState5(false);
                        this.flag5 = true;
                     }
                     break;
                  case SEARCH:
                     if (this.flag5 && this.vec3d != null && mc.player.getPos().squaredDistanceTo(this.vec3d) >= 400.0) {
                        this.updateState23("Поздний телепорт — бегу вперёд, ищу якорь");
                        this.updateState5(true);
                        return;
                     }

                     if (var2 - this.timestamp4 >= 1000L) {
                        this.timestamp4 = var2;
                        BlockPos var16 = this.computeBlockPos();
                        if (var16 != null) {
                           this.blockPos = var16;
                           this.updateState23("Якорь найден: " + var16.toShortString());
                           this.updateState14(AutoCore.State.GO_ANCHOR);
                           return;
                        }

                        long var6 = this.flag4 ? 45000L : 12000L;
                        if (var2 - this.timestamp > var6) {
                           this.updateState23("Якорь не нашёлся — заново /warp pvp");
                           this.updateState13();
                           this.updateState14(AutoCore.State.WARP);
                           return;
                        }
                     }

                     if (!this.flag4) {
                        return;
                     }

                     if (!this.flag2) {
                        Vec3d var17 = Vec3d.fromPolar(0.0F, mc.player.getYaw());
                        if (var17.horizontalLengthSquared() < 1.0E-4) {
                           var17 = new Vec3d(0.0, 0.0, 1.0);
                        }

                        this.level = var17.x;
                        this.level2 = var17.z;
                        this.flag2 = true;
                        this.vec3d2 = mc.player.getPos();
                        this.timestamp6 = var2;
                     }

                     this.updateState2(var2);
                     break;
                  case GO_ANCHOR:
                     if (!this.checkCondition3(var2)) {
                        return;
                     }

                     double var15 = mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(this.blockPos));
                     double var18 = this.floatSetting.get();
                     if (var15 <= var18 * var18) {
                        this.updateState13();
                        this.flag6 = false;
                        this.updateState23("У якоря — начинаю обход");
                        this.updateState14(AutoCore.State.PATROL);
                        return;
                     }

                     if (var2 - this.timestamp > 30000L) {
                        this.updateState23("Не дошёл до якоря — пересканирую");
                        this.updateState13();
                        this.blockPos = null;
                        this.updateState5(false);
                        return;
                     }

                     this.updateState11(this.blockPos, var2);
                     break;
                  case PATROL:
                     if (!this.checkCondition3(var2)) {
                        return;
                     }

                     if (this.booleanSetting5.isState()) {
                        PlayerEntity var13 = this.computePlayerEntity2(this.floatSetting3.get());
                        if (var13 != null) {
                           this.updateState7(var13, var2);
                        }
                     }

                     double var14 = this.floatSetting.get() + 2.0;
                     if (mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(this.blockPos)) > var14 * var14) {
                        this.updateState23("Вышел за радиус обхода — возвращаюсь к якорю");
                        this.updateState13();
                        this.flag6 = false;
                        this.updateState14(AutoCore.State.GO_ANCHOR);
                        return;
                     }

                     if (!this.flag6) {
                        this.updateState6();
                        this.timestamp9 = var2;
                     }

                     double var7 = mc.player.getX() - (this.index + 0.5);
                     double var9 = mc.player.getZ() - (this.index2 + 0.5);
                     if (var7 * var7 + var9 * var9 <= 2.25 || var2 - this.timestamp9 > 7000L) {
                        this.updateState6();
                        this.timestamp9 = var2;
                     }

                     this.updateState12(this.index, this.index2, var2);
                     break;
                  case FLEE:
                     if (var2 >= this.timestamp14) {
                        this.updateState15();
                        this.updateState23("КД ПвП вышел — меняю анархию");
                        this.updateState10(this.resolveInt());
                        this.updateState14(AutoCore.State.WAIT_RCT);
                        return;
                     }

                     PlayerEntity var12 = this.computePlayerEntity(24.0);
                     if (var12 != null && var2 - this.timestamp15 >= 1500L) {
                        this.updateState4(var12);
                        this.timestamp15 = var2;
                     }

                     this.updateState2(var2);
                     break;
                  case BUY_INVIS:
                     if (AutoSetCollector.INSTANCE.isRunning()) {
                        if (var2 - this.timestamp > 60000L) {
                           this.updateState23("§eПокупка инвиза затянулась — прекращаю");
                           AutoSetCollector.INSTANCE.stop();
                           this.updateState();
                        }

                        return;
                     }

                     this.updateState23(
                        this.resolveInt2(this::checkCondition11) != -1
                           ? "§aИнвиз куплен — продолжаю"
                           : "§eИнвиз не куплен (лотов нет?) — продолжаю без него"
                     );
                     this.updateState();
                     break;
                  case WAIT_RCT:
                     if (RCTCommand.RUNNING) {
                        if (var2 - this.timestamp > 30000L) {
                           this.updateState23("§c.rct завис — пробую /warp pvp здесь");
                           this.updateState();
                        }

                        return;
                     }

                     if (this.object == null) {
                        this.object = mc.world;
                        this.timestamp7 = var2;
                        this.timestamp8 = 0L;
                        return;
                     }

                     if (this.timestamp8 == 0L) {
                        if (mc.world != this.object) {
                           this.timestamp8 = var2;
                        } else if (var2 - this.timestamp7 > 15000L) {
                           this.updateState23("Перенос не подтвердился — /warp pvp на текущей");
                           this.updateState();
                        }

                        return;
                     }

                     if (var2 - this.timestamp8 >= 3000L) {
                        this.updateState23("Новая анархия — цикл заново");
                        this.updateState();
                     }
               }
            }
         }
      }
   }

   private void updateState() {
      this.blockPos = null;
      this.timestamp2 = 0L;
      this.updateState14(AutoCore.State.WARP);
   }

   private void updateState2(long now) {
      Vec3d var3 = mc.player.getPos();
      if (this.vec3d2 == null || var3.squaredDistanceTo(this.vec3d2) > 0.25) {
         this.vec3d2 = var3;
         this.timestamp6 = now;
      } else if (now - this.timestamp6 > 3000L) {
         float var4 = ThreadLocalRandom.current().nextBoolean() ? 50.0F : -50.0F;
         double var5 = Math.toRadians(var4);
         double var7 = this.level * Math.cos(var5) - this.level2 * Math.sin(var5);
         double var9 = this.level * Math.sin(var5) + this.level2 * Math.cos(var5);
         this.level = var7;
         this.level2 = var9;
         this.timestamp6 = now;
         this.updateState23("Упёрся — сворачиваю");
      }

      mc.player.setYaw((float)(Math.toDegrees(Math.atan2(this.level2, this.level)) - 90.0));
      mc.options.forwardKey.setPressed(true);
      mc.options.sprintKey.setPressed(true);
      mc.options.jumpKey.setPressed(mc.player.horizontalCollision || mc.player.isTouchingWater());
      this.flag3 = true;
   }

   private boolean checkCondition(long now) {
      return this.booleanSetting2.isState()
         && this.booleanSetting3.isState()
         && now - this.timestamp16 >= 60000L
         && this.checkState5()
         && this.resolveInt2(this::checkCondition11) == -1;
   }

   private boolean checkCondition2(long now) {
      AutoSetItem var3 = AutoSetCatalog.byKey("potions|Зелье невидимости");
      if (var3 == null) {
         return false;
      }

      this.timestamp16 = now;
      AutoSetKit var4 = new AutoSetKit("AutoCore: инвиз");
      var4.entries.add(new AutoSetKit.Entry(var3.key(), 1));
      if (!AutoSetCollector.INSTANCE.start(var4)) {
         return false;
      }

      this.updateState23("Зелий нет — покупаю 1 инвиз на аукционе");
      return true;
   }

   private boolean checkState() {
      return switch (this.state) {
         case WAIT_TP, SEARCH, GO_ANCHOR, PATROL, FLEE -> true;
         default -> false;
      };
   }

   private void updateState3(long now, PlayerEntity attacker) {
      this.updateState16();
      this.updateState13();
      this.timestamp14 = now + (long)(this.floatSetting4.get() * 1000.0F);
      this.updateState4(attacker);
      this.timestamp15 = now;
      if (this.state != AutoCore.State.FLEE) {
         this.updateState23("§cПолучил урон от " + attacker.getGameProfile().getName() + " — убегаю до конца КД ПвП");
         this.updateState14(AutoCore.State.FLEE);
      }
   }

   private void updateState4(PlayerEntity attacker) {
      Vec3d var2 = mc.player.getPos();
      Vec3d var3 = new Vec3d(var2.x - attacker.getX(), 0.0, var2.z - attacker.getZ());
      if (var3.horizontalLengthSquared() < 0.01) {
         var3 = Vec3d.fromPolar(0.0F, mc.player.getYaw());
      }

      var3 = var3.normalize();
      this.level = var3.x;
      this.level2 = var3.z;
      this.flag2 = true;
      this.vec3d2 = null;
   }

   private void updateState5(boolean allowRun) {
      this.timestamp4 = 0L;
      this.flag2 = false;
      this.flag4 = allowRun;
      this.flag5 = false;
      this.updateState14(AutoCore.State.SEARCH);
   }

   private boolean checkCondition3(long now) {
      if (this.blockPos == null) {
         this.updateState5(false);
         return false;
      }

      if (now - this.timestamp5 < 2000L) {
         return true;
      }

      this.timestamp5 = now;
      if (!mc.world.isChunkLoaded(this.blockPos.getX() >> 4, this.blockPos.getZ() >> 4)) {
         return true;
      }

      if (this.checkCondition4(this.blockPos)) {
         return true;
      }

      this.updateState23("Якорь пропал или разряжен — ищу новый");
      this.updateState13();
      this.blockPos = null;
      this.updateState5(false);
      return false;
   }

   private boolean checkCondition4(BlockPos pos) {
      BlockState var2 = mc.world.getBlockState(pos);
      return var2.getBlock() instanceof RespawnAnchorBlock && var2.get(RespawnAnchorBlock.CHARGES) >= 1;
   }

   private BlockPos computeBlockPos() {
      BlockPos var1 = mc.player.getBlockPos();

      for (BlockPos var3 : BlockPos.iterateOutwards(var1, 32, 12, 32)) {
         BlockState var4 = mc.world.getBlockState(var3);
         if (var4.getBlock() instanceof RespawnAnchorBlock && var4.get(RespawnAnchorBlock.CHARGES) >= 1) {
            return var3.toImmutable();
         }
      }

      return null;
   }

   private void updateState6() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();
      double var2 = var1.nextDouble(Math.PI * 2);
      double var4 = Math.max(1.0, this.floatSetting.get() - 0.5);
      double var6 = var4 <= 1.0 ? 1.0 : var1.nextDouble(1.0, var4);
      this.index = MathHelper.floor(this.blockPos.getX() + 0.5 + Math.cos(var2) * var6);
      this.index2 = MathHelper.floor(this.blockPos.getZ() + 0.5 + Math.sin(var2) * var6);
      this.flag6 = true;
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

   private boolean checkState2() {
      return this.booleanSetting5.isState() && this.resolveInt2(this::checkCondition7) != -1;
   }

   private boolean checkCondition5(PlayerEntity p) {
      return this.checkCondition6(p) || p.isInvisible();
   }

   private boolean checkCondition6(PlayerEntity p) {
      return p.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
         && p.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
         && p.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
         && p.getEquippedStack(EquipmentSlot.FEET).isEmpty();
   }

   private PlayerEntity computePlayerEntity2(double range) {
      PlayerEntity var3 = null;
      double var4 = range * range;

      for (PlayerEntity var7 : mc.world.getPlayers()) {
         if (var7 != mc.player && !var7.isSpectator() && var7.isAlive() && this.checkCondition5(var7)) {
            double var8 = var7.squaredDistanceTo(mc.player);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private PlayerEntity computePlayerEntity3(double range) {
      boolean var3 = this.checkState2();
      PlayerEntity var4 = null;
      double var5 = range * range;

      for (PlayerEntity var8 : mc.world.getPlayers()) {
         if (var8 != mc.player && !var8.isSpectator() && (!var3 || !this.checkCondition5(var8))) {
            double var9 = var8.squaredDistanceTo(mc.player);
            if (var9 < var5) {
               var5 = var9;
               var4 = var8;
            }
         }
      }

      return var4;
   }

   private boolean checkCondition7(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.getItem() instanceof SwordItem;
   }

   private void updateState7(PlayerEntity victim, long now) {
      if (now - this.timestamp17 >= 700L) {
         if (!(mc.player.squaredDistanceTo(victim) > 9.0)) {
            int var4 = this.resolveInt2(this::checkCondition7);
            if (var4 != -1) {
               if (var4 > 8) {
                  this.updateState19(var4, now);
               } else {
                  this.updateState9(victim);
                  this.updateState20(var4);
                  mc.interactionManager.attackEntity(mc.player, victim);
                  mc.player.swingHand(Hand.MAIN_HAND);
                  this.timestamp17 = now;
                  this.updateState8();
               }
            }
         }
      }
   }

   private void updateState8() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            this.updateState20(var1);
            return;
         }
      }
   }

   private void updateState9(PlayerEntity target) {
      Vec3d var2 = mc.player.getEyePos();
      Vec3d var3 = new Vec3d(target.getX(), target.getBodyY(0.6), target.getZ());
      double var4 = var3.x - var2.x;
      double var6 = var3.y - var2.y;
      double var8 = var3.z - var2.z;
      double var10 = Math.sqrt(var4 * var4 + var8 * var8);
      mc.player.setYaw(MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(var8, var4)) - 90.0F));
      mc.player.setPitch(MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(var6, var10))), -90.0F, 90.0F));
   }

   private int resolveInt() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();

      int var2;
      do {
         var2 = var1.nextInt(1, 75);
      } while (var2 == this.index3);

      this.index3 = var2;
      return var2;
   }

   private void updateState10(int number) {
      try {
         Lumen.INSTANCE.commandStorage.getDispatcher().execute("rct " + number, Lumen.INSTANCE.commandStorage.getSource());
      } catch (CommandSyntaxException var3) {
         this.updateState23("§c.rct не удался: " + var3.getMessage());
      }
   }

   private void updateState11(BlockPos target, long now) {
      if (!target.equals(this.blockPos2) || now - this.timestamp10 >= 6000L) {
         this.toBooleanOrDefault();
         BaritoneControl.gotoPos(target);
         this.blockPos2 = target;
         this.timestamp10 = now;
      }
   }

   private void updateState12(int x, int z, long now) {
      BlockPos var5 = new BlockPos(x, 0, z);
      if (!var5.equals(this.blockPos2) || now - this.timestamp10 >= 6000L) {
         this.toBooleanOrDefault();
         BaritoneControl.gotoXZ(x, z);
         this.blockPos2 = var5;
         this.timestamp10 = now;
      }
   }

   private void updateState13() {
      long var1 = System.currentTimeMillis();
      if (this.blockPos2 != null && var1 - this.timestamp11 >= 1000L) {
         BaritoneControl.stop();
         this.timestamp11 = var1;
         this.blockPos2 = null;
      } else {
         this.blockPos2 = null;
      }
   }

   private void updateState14(AutoCore.State next) {
      this.updateState15();
      this.object = null;
      this.timestamp8 = 0L;
      this.state = next;
      this.timestamp = System.currentTimeMillis();
   }

   private void updateState15() {
      if (this.flag3 && mc.options != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.sprintKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
      }

      this.flag3 = false;
   }

   private boolean checkCondition8(long now) {
      boolean var3 = this.state == AutoCore.State.WARP
         || this.state == AutoCore.State.SEARCH
         || this.state == AutoCore.State.GO_ANCHOR
         || this.state == AutoCore.State.PATROL;
      if (!this.booleanSetting2.isState() || !var3) {
         this.updateState16();
         return false;
      }

      if (!this.flag7 && !this.checkState5()) {
         return false;
      }

      if (!this.flag7 || (this.checkState5() || mc.player.isUsingItem()) && now - this.timestamp18 <= 8000L) {
         int var4 = this.resolveInt2(this::checkCondition11);
         if (var4 == -1) {
            this.updateState16();
            return false;
         }

         if (!this.flag7) {
            this.flag7 = true;
            this.timestamp18 = now;
            this.updateState23("Пью невидимость");
         }

         this.updateState13();
         this.updateState15();
         if (var4 > 8) {
            this.updateState19(var4, now);
            return true;
         } else {
            this.updateState20(var4);
            mc.player.setPitch(-90.0F);
            this.updateState21();
            return true;
         }
      } else {
         this.updateState16();
         return false;
      }
   }

   private void updateState16() {
      if (this.flag7) {
         this.updateState22();
         this.flag7 = false;
         this.vec3d2 = null;
      }
   }

   private boolean checkCondition9(long now) {
      boolean var3 = this.state == AutoCore.State.SEARCH
         || this.state == AutoCore.State.GO_ANCHOR
         || this.state == AutoCore.State.PATROL;
      if (this.booleanSetting4.isState() && var3) {
         boolean var4 = mc.player.getHungerManager().getFoodLevel() <= 17;
         if (!this.flag8 && !var4) {
            return false;
         } else if (this.flag8 && !var4 && !mc.player.isUsingItem()) {
            this.updateState17();
            return false;
         } else {
            int var5 = this.resolveInt2(this::checkCondition10);
            if (var5 == -1) {
               this.updateState17();
               return false;
            } else {
               this.flag8 = true;
               this.updateState13();
               this.updateState15();
               if (var5 > 8) {
                  this.updateState19(var5, now);
                  return true;
               } else {
                  this.updateState20(var5);
                  mc.player.setPitch(-90.0F);
                  this.updateState21();
                  return true;
               }
            }
         }
      } else {
         this.updateState17();
         return false;
      }
   }

   private void updateState17() {
      if (this.flag8) {
         this.updateState22();
         this.flag8 = false;
         this.vec3d2 = null;
      }
   }

   private boolean checkCondition10(ItemStack stack) {
      return stack != null && !stack.isEmpty() && !stack.isOf(Items.CHORUS_FRUIT) && stack.getUseAction() == UseAction.EAT;
   }

   private boolean checkState3() {
      if (this.booleanSetting2.isState() && !this.flag8 && !this.flag7) {
         return switch (this.state) {
            case WAIT_TP, SEARCH, GO_ANCHOR, PATROL, FLEE -> true;
            default -> false;
         };
      } else {
         return false;
      }
   }

   private void updateState18(long now) {
      if (!mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).isEmpty()) {
         for (int var3 = 0; var3 < 9; var3++) {
            if (mc.player.getInventory().getStack(var3).isEmpty()) {
               this.updateState20(var3);
               return;
            }
         }

         if (now - this.timestamp19 >= 350L && this.checkState4()) {
            int var6 = -1;

            for (int var4 = 0; var4 < 9; var4++) {
               ItemStack var5 = mc.player.getInventory().getStack(var4);
               if (!this.checkCondition11(var5) && !this.checkCondition10(var5)) {
                  var6 = var4;
                  break;
               }
            }

            if (var6 == -1) {
               var6 = 0;
            }

            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 36 + var6, 0, SlotActionType.QUICK_MOVE, mc.player);
            this.timestamp19 = now;
         }
      }
   }

   private boolean checkState4() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private boolean checkState5() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      return var1 == null ? true : !var1.isInfinite() && var1.getDuration() <= 200;
   }

   private boolean checkCondition11(ItemStack stack) {
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

   private int resolveInt2(Predicate<ItemStack> filter) {
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

   private void updateState19(int slot, long now) {
      if (now - this.timestamp19 >= 350L) {
         mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slot, 8, SlotActionType.SWAP, mc.player);
         this.timestamp19 = now;
      }
   }

   private void updateState20(int slot) {
      if (slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
      }
   }

   private void updateState21() {
      mc.options.useKey.setPressed(true);
      if (!mc.player.isUsingItem() || mc.player.getActiveHand() != Hand.MAIN_HAND) {
         mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      }
   }

   private void updateState22() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }
   }

   private boolean checkState6() {
      return mc.currentScreen instanceof DeathScreen || !mc.player.isAlive();
   }

   private void updateState23(String message) {
      if (this.booleanSetting6.isState()) {
         ChatUtils.sendMessage("[AutoCore] " + message);
      }
   }

   private enum State {
      WARP,
      WAIT_TP,
      SEARCH,
      GO_ANCHOR,
      PATROL,
      FLEE,
      BUY_INVIS,
      WAIT_RCT;
   }
}
