package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.events.implement.EventGameUpdate;
import dlc.lumen.api.events.implement.EventKeyboardInput;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.events.implement.EventUpdatePost;
import dlc.lumen.api.storages.implement.FreeLookStorage;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.combat.IdealHitUtils;
import dlc.lumen.api.utils.combat.PredictUtils;
import dlc.lumen.api.utils.input.MovingUtil;
import dlc.lumen.api.utils.math.MathUtils;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.player.HotbarUtil;
import dlc.lumen.api.utils.player.SlotSearchResult;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.api.utils.rotate.MultipointUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.interpolation.BestPoint;
import dlc.lumen.client.modules.impl.combat.components.neuro.NeuroRotation;
import dlc.lumen.client.modules.impl.combat.components.neuro.StyleRecorder;
import dlc.lumen.client.modules.impl.combat.components.rotations.FuntimeRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.Holy2Rotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.HolyWorldRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.ReallyWorldRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.ReallyWorldV2Rotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.SpookyTimeV3Rotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.SpookyTimeV3_116Rotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.SpookyTimeV3DuelsRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.HelixWaveRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.ArtygriefRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.FovRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.FunTimeV4Rotation;import dlc.lumen.client.modules.impl.combat.components.rotations.LegitRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.SlothRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.SpookyTimeRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.SpookyRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.TestRotation;
import dlc.lumen.client.modules.impl.combat.components.rotations.WhiteRiseRotation;
import dlc.lumen.client.modules.impl.movement.Sprint;
import dlc.lumen.client.modules.impl.player.AutoEat;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.mixin.ILivingEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class Aura extends Module {
   public static Aura INSTANCE = new Aura();
   public final ModeSetting rotationType = new ModeSetting("Ротация", "HolyLegit", "Smooth", "HolyLegit", "Funtime", "SpookyTime", "ReallyWorld", "HelixWave", "Artygrief", "Sloth", "Spooky", "FunTimeV4", "SpookyTimeV3", "FOV", "ReallyWorldV2");
   private final ListSetting value = new ListSetting(
      "Таргеты",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Голые", true),
      new BooleanSetting("Невидимки", true),
      new BooleanSetting("Мирные", false),
      new BooleanSetting("Мобы", true)
   );
   private final FloatSetting floatSetting = new FloatSetting("Дистанция атаки", 3.0F, 0.0F, 6.0F, 0.05F);
   private final FloatSetting floatSetting2 = new FloatSetting("Дистанция наводки", 3.0F, 0.0F, 6.0F, 0.05F);
   private final FloatSetting floatSetting3 = new FloatSetting("Дистанция на элитрах", 50.0F, 10.0F, 100.0F, 0.05F);
    public final BooleanSetting smartCrit = new BooleanSetting("Умные криты", true);
    private final BooleanSetting booleanSetting = new BooleanSetting("Бить через стены", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Отжимать щит", false);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Ломать щит", true);
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Не бить когда ешь", true);
   public static BooleanSetting clientLook = new BooleanSetting("Наводка от первого лица", false);
   private final ModeSetting modeSetting = new ModeSetting("Коррекция", "Нет", "Нет", "Свободная", "Сфокусированная", "Полная");
   private final ModeSetting modeSetting2 = new ModeSetting("Приоритет", "Дистанция", "Дистанция", "Здоровье", "Угол", "Никакой");
   private final BooleanSetting booleanSetting5 = new BooleanSetting("Авто цербер", false);
   private final FloatSetting floatSetting4 = new FloatSetting("Задержка свапа", 4.0F, 2.0F, 10.0F, 1.0F).visible(() -> this.booleanSetting5.isState());
   private LivingEntity target2;
   private Vec2f target3 = new Vec2f(0.0F, 0.0F);
   private Vec2f target4 = new Vec2f(0.0F, 0.0F);
   private final TimerUtils target5 = new TimerUtils();
   private final BooleanSetting booleanSetting6 = new BooleanSetting("Обход рв стен", false);
   private final BooleanSetting booleanSetting7 = new BooleanSetting("Смотреть вниз", false).visible(this.booleanSetting6::isState);
   private final TestRotation testRotation = new TestRotation();
   private final FuntimeRotation funtimeRotation = new FuntimeRotation();
   private final SlothRotation slothRotation = new SlothRotation();
   private final HolyWorldRotation holyWorldRotation = new HolyWorldRotation();
   private final Holy2Rotation holy2Rotation = new Holy2Rotation();
   private final WhiteRiseRotation whiteRiseRotation = new WhiteRiseRotation(this);
   private final LegitRotation legitRotation = new LegitRotation();
    private final SpookyTimeRotation spookyTimeRotation = new SpookyTimeRotation();
    private final SpookyRotation spookyRotation = new SpookyRotation();
    private final ReallyWorldRotation reallyWorldRotation = new ReallyWorldRotation();
    private final HelixWaveRotation helixWaveRotation = new HelixWaveRotation();
    private final ArtygriefRotation artygriefRotation = new ArtygriefRotation();
    private final FunTimeV4Rotation funTimeV4Rotation = new FunTimeV4Rotation();
    private final SpookyTimeV3Rotation spookyTimeV3Rotation = new SpookyTimeV3Rotation();
    private final SpookyTimeV3_116Rotation spookyTimeV3_116Rotation = new SpookyTimeV3_116Rotation();
    private final SpookyTimeV3DuelsRotation spookyTimeV3DuelsRotation = new SpookyTimeV3DuelsRotation();
    public final ModeSetting spookyV3Profile = new ModeSetting("Профиль SpookyTimeV3", "Спуки 1.21", "Спуки 1.21", "Спуки 1.16", "Спуки-дуэли")
       .visible(() -> this.rotationType.is("SpookyTimeV3"));
    private final FovRotation fovRotation = new FovRotation();
    private final ReallyWorldV2Rotation reallyWorldV2Rotation = new ReallyWorldV2Rotation();
   private final NeuroRotation neuroRotation = new NeuroRotation();
   private boolean flag;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private float volume;
   private float volume2;
   private boolean flag5;
   private LivingEntity livingEntity;
   private boolean flag6;
   private long timestamp;
   private final TimerUtils timerUtils = new TimerUtils();
   private TpsSync tpsSync;
   private static final int INDEX = 3;
   private static final int INDEX2 = 13;
   private int index = 0;
   private int whiteRiseTicksToAttack = 0;
   private int index2 = -1;
   private boolean flag7 = false;
   private LivingEntity livingEntity2 = null;
   private LivingEntity livingEntity3 = null;
   private boolean flag8 = false;
   private int index3 = -1;
   private boolean flag9 = false;
   private int index4 = 0;
   private boolean flag10 = false;
   private boolean flag11 = false;
   private int index5 = 0;
   private boolean flag12 = false;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int index6 = 0;
   private float volume3 = 0.0F;
   private float volume4 = 0.0F;
   public static float adjYaw;
   public static float adjPitch;
   public static float otvodkaYaw;
   public static float otvodkaPitch;
   public boolean isRotated;

   public Aura() {
      super("AttackAura", "Автоматически наводиться и бьёт таргета", Module.ModuleCategory.COMBAT);
      this.addSettings(
         this.rotationType,
         this.spookyV3Profile,
         this.value,
         this.floatSetting,
         this.floatSetting2,
         this.floatSetting3,
         this.smartCrit,
         this.booleanSetting4,
         this.booleanSetting,
         this.booleanSetting6,
         this.booleanSetting7,
         this.booleanSetting2,
         this.booleanSetting3,
         this.booleanSetting5,
         this.floatSetting4,
         clientLook,
         this.modeSetting,
         this.modeSetting2
      );
   }

   @EventLink
    public void onPlayerTick(EventUpdate e) {
       if (mc.player != null && mc.world != null) {
          this.volume3++;
          this.updateState7();
          this.updateState19();
          if (this.target2 == null && this.rotationType.is("Spooky")) {
             this.spookyRotation.tickReturn();
          }
       }
    }

    @EventLink
    public void onAttackEntity(EventAttackEntity event) {
       if (mc.player != null && mc.world != null) {
          if (event.getPlayer() == mc.player) {
             if (this.rotationType.is("FunTimeV4")) {
                this.funTimeV4Rotation.onAttack();
             }

             if (event.getTarget() instanceof LivingEntity var2) {
               if (this.checkCondition7(var2)) {
                  this.target2 = var2;
               }
            }
         }
      }
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if (this.index5 > 0) {
         if (mc.player != null) {
            mc.player.setSprinting(false);
         }

         event.setForward(0.0F);
         event.setStrafe(0.0F);
         event.setJump(false);
         event.setSneak(false);
         event.setSprint(false);
      } else {
         this.updateState(event);
      }
   }

   private void updateState(EventMoveInput event) {
      if (mc.player != null && this.target2 != null && this.modeSetting.getIndex() != 0) {
         if (this.modeSetting.getIndex() == 1) {
            MovingUtil.fixMovementFree(event);
         }
      }
   }

   @EventLink
   public void onKeyboardInput(EventKeyboardInput event) {
      if (mc.player != null && mc.world != null && this.target2 != null) {
         float var2 = this.computefloat();
         if (this.modeSetting.getIndex() == 2) {
            event.setYaw(var2, mc.player.getYaw());
         } else if (this.modeSetting.getIndex() == 3) {
            event.setYaw(var2, this.computefloat2());
         }
      }
   }

   private float computefloat() {
      return RotationStorage.instance != null && RotationStorage.instance.targetRotation() != null
         ? RotationStorage.instance.targetRotation().getYaw()
         : mc.player.getYaw();
   }

   private float computefloat2() {
      return RotationUtils.getRotations(this.target2.getBoundingBox().getCenter()).x;
   }

   private void updateState2(EventMoveInput event, float yaw, float directionYaw) {
      float var4 = event.getForward();
      float var5 = event.getStrafe();
      if (var4 != 0.0F || var5 != 0.0F) {
         double var6 = MathHelper.wrapDegrees(Math.toDegrees(MovingUtil.direction(directionYaw, var4, var5)));
         float var8 = 0.0F;
         float var9 = 0.0F;
         float var10 = Float.MAX_VALUE;

         for (float var11 = -1.0F; var11 <= 1.0F; var11++) {
            for (float var12 = -1.0F; var12 <= 1.0F; var12++) {
               if (var11 != 0.0F || var12 != 0.0F) {
                  double var13 = MathHelper.wrapDegrees(Math.toDegrees(MovingUtil.direction(yaw, var11, var12)));
                  double var15 = Math.abs(var6 - var13);
                  if (var15 < var10) {
                     var10 = (float)var15;
                     var8 = var11;
                     var9 = var12;
                  }
               }
            }
         }

         event.setForward(var8);
         event.setStrafe(var9);
      }
   }

   @EventLink
   private void updateState3(EventGameUpdate e) {
      if (mc.player != null && mc.world != null && this.target2 != null) {
         this.updateState5();
      }
   }

   @EventLink
   public void onTick(EventUpdate e) {
      if (mc.player != null && mc.world != null) {
         if (this.whiteRiseTicksToAttack > 0) {
            this.whiteRiseTicksToAttack--;
         }

         if (this.index > 0) {
            this.index--;
         }

         boolean var2 = ModuleClass.packetCriticals.isEnable() && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
         if (!var2) {
            this.updateState4();
         }
      }
   }

   @EventLink
   public void onPost(EventUpdatePost e) {
      if (mc.player != null && mc.world != null) {
         boolean var2 = ModuleClass.packetCriticals.isEnable() && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
         if (var2 && mc.player.fallDistance > 0.0F && mc.player.fallDistance < 1.0F) {
            this.updateState4();
         }
      }
   }

   @EventLink(priority = -200)
   public void onPacket(EventPacket e) {
      if (!e.isCancelled()) {
         if (e.getType() == EventPacket.Type.SEND && e.getPacket() instanceof PlayerMoveC2SPacket var2) {
            if (var2.changesLook() && mc.player != null) {
               this.volume = var2.getYaw(mc.player.getYaw());
               this.volume2 = var2.getPitch(mc.player.getPitch());
               this.flag5 = true;
            }
         }
      }
   }

   private LivingEntity computeLivingEntity() {
      LivingEntity var1 = null;
      double var2 = 100.0;
      Vec3d var4 = mc.player.getEyePos();

      for (Entity var6 : mc.world.getEntities()) {
         if (var6 instanceof LivingEntity var7 && var7 != mc.player && var7.isAlive() && !(var7.getHealth() <= 0.0F) && !(var7 instanceof ArmorStandEntity)) {
            double var8 = var4.squaredDistanceTo(var7.getBoundingBox().getCenter());
            if (!(var8 > var2)) {
               var2 = var8;
               var1 = var7;
            }
         }
      }

      return var1;
   }

   private void updateState4() {
      this.updateState7();
      if (this.target2 != null) {
         boolean var1 = !this.checkState12() && this.checkState3();
         boolean var2 = !this.rotationType.is("Neuro") || this.neuroRotation.attackConsent();
         boolean var3 = this.booleanSetting4.isState() && (this.checkState8() || AutoEat.shouldSuppressCombat());
         if (this.checkState12() && this.index <= 0 && var2 && !var3) {
            this.updateState13(this.target2);
         }

         if ((this.checkState10() || var1) && this.index <= 0 && var2) {
            if (var3) {
               return;
            }

            if (this.checkState() && !this.checkState2()) {
               return;
            }

            this.updateState8();
            if (this.checkState12()) {
               this.updateState14();
            }

            this.updateState28();
         }
      } else {
         this.index = 0;
         this.timerUtils.reset();
         adjPitch = 0.0F;
         adjYaw = 0.0F;
         this.testRotation.reset();
         this.updateState16();
         this.updateState12();
         this.updateState18();
         this.updateState17();
         this.whiteRiseRotation.reset();
         this.legitRotation.reset();
         this.spookyTimeRotation.reset();
         this.livingEntity3 = null;
         this.whiteRiseTicksToAttack = 0;
         this.updateState28();
      }
   }

   public void Rotate() {
      this.updateState5();
   }

   private void updateState5() {
      if (this.target2 != null) {
         if (!this.rotationType.is("Funtime")) {
            this.updateState12();
         }

         if (!this.checkState12()) {
            this.updateState16();
         }

         if (!this.rotationType.is("Sloth")) {
            this.updateState18();
         }

         if (!this.rotationType.is("Neuro")) {
            this.updateState17();
         }

         if (this.checkState()) {
            this.updateState6(this.target2);
         } else {
            RotationsSystem var1;
            if (this.rotationType.is("Smooth")) {
               var1 = new RotationsSystem() {
                  @Override
                  public void updateRotations(LivingEntity target) {
                     if (!mc.player.isGliding()) {
                        Vec3d var2 = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0).subtract(mc.player.getEyePos());
                        float var3 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0);
                        float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))));
                        RotationStorage.update(new Rotation(var3, var4), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, Aura.clientLook.isState());
                     } else {
                        Vec3d var8 = Vec3d.fromPolar(target.getLerpTargetPitch(), target.getLerpTargetYaw());
                        Vec3d var9 = target.getRotationVector();
                        Vec3d var10 = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0).subtract(mc.player.getEyePos());
                        Vec3d var5 = var8.normalize().lerp(var9, var8.length());
                        float var6 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var10.z, var10.x)) - 90.0);
                        float var7 = (float)(-Math.toDegrees(Math.atan2(var10.y, Math.hypot(var10.x, var10.z))));
                        RotationStorage.update(new Rotation(var6, var7), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, Aura.clientLook.isState());
                     }
                  }
               };
            } else if (this.rotationType.is("HolyLegit")) {
               var1 = this.legitRotation;
            } else if (this.checkState12()) {
               if (!this.flag4) {
                  this.updateState15();
                  this.flag4 = true;
               }

               if (this.livingEntity != this.target2) {
                  this.livingEntity = this.target2;
                  this.flag5 = false;
                  this.timestamp = 0L;
               }

               var1 = this.computeRotationsSystem();
            } else if (this.rotationType.is("Test")) {
               var1 = this.testRotation;
            } else if (this.rotationType.is("Funtime")) {
               if (!this.flag2) {
                  this.funtimeRotation.reset();
                  this.flag2 = true;
               }

               this.funtimeRotation.setVisibleAim(clientLook.isState());
               this.funtimeRotation.setPrecisionLock(this.checkCondition10(this.target2));
               var1 = this.funtimeRotation;
            } else if (this.rotationType.is("Sloth")) {
               if (!this.flag3) {
                  this.slothRotation.reset();
                  this.flag3 = true;
               }

               if (this.livingEntity != this.target2) {
                  this.livingEntity = this.target2;
                  this.flag5 = false;
                  this.timestamp = 0L;
               }

               this.slothRotation.setVisibleAim(false);
               this.slothRotation.setPrecisionLock(this.checkCondition10(this.target2));
               var1 = this.slothRotation;
            } else if (this.rotationType.is("Neuro")) {
               if (!this.flag) {
                  this.neuroRotation.reset();
                  this.flag = true;
               }

               if (!this.neuroRotation.isAvailable()) {
                  return;
               }

               var1 = this.neuroRotation;
            } else if (this.rotationType.is("Data")) {
               var1 = new RotationsSystem() {
                  @Override
                  public void updateRotations(LivingEntity target) {
                     boolean var2 = Aura.this.checkState7();
                     Vec3d var3 = Aura.this.computeVec3d2(target);
                     Vec2f var4 = RotationUtils.getRotations(var3);
                     float var5 = mc.player.getYaw();
                     float var6 = mc.player.getPitch();
                     float var7 = Math.abs(MathHelper.wrapDegrees(var4.x - var5));
                     float var8 = Math.abs(var4.y - var6);
                     boolean var9 = var7 > 70.0F || var7 > 42.0F && mc.player.squaredDistanceTo(target) < 9.0;
                     Rotation var10 = null;
                     if (var10 == null || var9) {
                        var10 = new Rotation(var4.x, var4.y);
                     }

                     Aura.this.target4 = new Vec2f(var10.getYaw(), var10.getPitch());
                     Aura.this.target3 = new Vec2f(var5, var6);
                     float var11 = var9 ? Math.max(95.0F, Math.min(180.0F, var7 * 1.45F)) : (var2 ? 24.0F : 11.5F);
                     float var12 = var9 ? Math.max(55.0F, Math.min(110.0F, Math.max(18.0F, var8 * 1.35F))) : (var2 ? 18.0F : 9.0F);
                     float var13 = var9 ? var11 : (var2 ? 18.0F : 9.0F);
                     float var14 = var9 ? var12 : (var2 ? 14.0F : 7.0F);
                     RotationStorage.update(var10, var11, var12, var13, var14, 1, 1, Aura.clientLook.isState());
                  }
               };
             } else if (this.rotationType.is("SpookyTime")) {
                var1 = this.spookyTimeRotation;
             } else if (this.rotationType.is("Spooky")) {
                var1 = this.spookyRotation;
            } else if (this.rotationType.is("ReallyWorld")) {
               var1 = this.reallyWorldRotation;
            } else if (this.rotationType.is("HelixWave")) {
               var1 = this.helixWaveRotation;
            } else if (this.rotationType.is("Artygrief")) {
               var1 = this.artygriefRotation;
            } else if (this.rotationType.is("FunTimeV4")) {
               var1 = this.funTimeV4Rotation;
            } else if (this.rotationType.is("SpookyTimeV3")) {
               if (this.spookyV3Profile.is("Спуки-дуэли")) {
                  var1 = this.spookyTimeV3DuelsRotation;
               } else if (this.spookyV3Profile.is("Спуки 1.16")) {
                  var1 = this.spookyTimeV3_116Rotation;
               } else {
                  var1 = this.spookyTimeV3Rotation;
               }
            } else if (this.rotationType.is("FOV")) {
               var1 = this.fovRotation;
            } else if (this.rotationType.is("ReallyWorldV2")) {
               var1 = this.reallyWorldV2Rotation;
            } else {
               final Vec2f var2 = RotationUtils.getRotations(this.computeVec3d(this.target2, this.target2.getLeashPos(1.0F)));
               var1 = new RotationsSystem() {
                  @Override
                  public void updateRotations(LivingEntity target) {
                     Aura.this.target3 = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
                     RotationStorage.update(new Rotation(var2.x, var2.y), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, Aura.clientLook.isState());
                  }
               };
            }

            var1.updateRotations(this.target2);
         }
      }
   }

   private void updateState6(LivingEntity target) {
      Vec3d var2 = MultipointUtils.getClosestPoint(target);
      if (var2 == null) {
         var2 = target.getBoundingBox().getCenter();
      }

      Vec3d var3 = this.computeVec3d(target, var2);
      Vec2f var4 = RotationUtils.getRotations(var3);
      this.target4 = var4;
      this.target3 = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
      boolean var5 = mc.player.age <= this.index2;
      float var6 = var4.x;
      float var7 = var4.y;
      if (!var5) {
         if (this.checkState6()) {
            var7 = 90.0F;
         } else {
            var6 = FreeLookStorage.getFreeYaw();
            var7 = FreeLookStorage.getFreePitch();
         }
      }

      RotationStorage.update(new Rotation(var6, var7), 360.0F, 360.0F, 360.0F, 360.0F, 0, 6, clientLook.isState());
   }

   private boolean checkState() {
      return !this.rotationType.is("Sloth") && this.checkState5();
   }

   private boolean checkState2() {
      if (!this.flag7) {
         this.flag7 = true;
         this.index2 = mc.player.age + 1;
         this.livingEntity2 = this.target2;
         return false;
      } else if (mc.player.age > this.index2) {
         this.updateState28();
         return false;
      } else {
         return this.checkState4();
      }
   }

   private boolean checkState3() {
      if (!this.flag7 || mc.player == null || this.target2 == null || this.target2 != this.livingEntity2) {
         return false;
      } else if (mc.player.age > this.index2 + 1) {
         this.updateState28();
         return false;
      } else {
         return mc.player.age >= this.index2;
      }
   }

   private boolean checkState4() {
      if (this.target2 != null && mc.player != null) {
         float var1 = Math.abs(MathHelper.wrapDegrees(this.target4.x - mc.player.getYaw()));
         float var2 = Math.abs(this.target4.y - mc.player.getPitch());
         boolean var3 = this.checkState5() || mc.player.isGliding() && this.target2.isGliding();
         if (!var3) {
            EntityHitResult var4 = this.computeEntityHitResult();
            var3 = var4 != null && var4.getEntity() == this.target2;
         }

         return var1 <= 3.0F && var2 <= 2.5F && var3;
      } else {
         return false;
      }
   }

   private boolean checkState5() {
      return this.booleanSetting6.isState() && this.target2 != null && this.checkCondition(this.target2);
   }

   private boolean checkState6() {
      return this.checkState5() && this.booleanSetting7.isState();
   }

   private EntityHitResult computeEntityHitResult() {
      return this.computeEntityHitResult2(mc.player.getYaw(), mc.player.getPitch());
   }

   private EntityHitResult computeEntityHitResult2(float yaw, float pitch) {
      Vec3d var3 = mc.player.getCameraPosVec(1.0F);
      Vec3d var4 = Vec3d.fromPolar(pitch, yaw);
      float var5 = this.floatSetting.getValue().floatValue() * 2.0F;
      Vec3d var6 = var3.add(var4.multiply(var5));
      return ProjectileUtil.raycast(mc.player, var3, var6, mc.player.getBoundingBox().expand(var5), ex -> ex != mc.player && ex.isAlive(), var5 * var5);
   }

   private boolean checkCondition(LivingEntity entity) {
      return entity != null && mc.player != null && mc.world != null ? !mc.player.canSee(entity) || this.checkCondition2(entity) : false;
   }

   private boolean checkCondition2(LivingEntity entity) {
      if (entity != null && mc.player != null && mc.world != null && this.booleanSetting6.isState()) {
         Vec3d var2 = mc.player.getEyePos();
         Box var3 = entity.getBoundingBox();
         double var4 = var3.getCenter().x;
         double var6 = var3.getCenter().z;
         Vec3d[] var8 = new Vec3d[]{
            var3.getCenter(),
            this.computeVec3d3(entity),
            new Vec3d(var4, var3.maxY - 0.08, var6),
            new Vec3d(var4, var3.minY + 0.12, var6),
            new Vec3d(var3.minX + 0.04, var3.minY + var3.getLengthY() * 0.55, var6),
            new Vec3d(var3.maxX - 0.04, var3.minY + var3.getLengthY() * 0.55, var6),
            new Vec3d(var4, var3.minY + var3.getLengthY() * 0.55, var3.minZ + 0.04),
            new Vec3d(var4, var3.minY + var3.getLengthY() * 0.55, var3.maxZ - 0.04)
         };
         int var9 = 0;
         int var10 = 0;

         for (Vec3d var14 : var8) {
            BlockHitResult var15 = mc.world.raycast(new RaycastContext(var2, var14, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
            if (var15 != null && var15.getType() == Type.BLOCK) {
               var9++;
            } else {
               var10++;
            }
         }

         return var10 > 0 && var9 >= var10;
      } else {
         return false;
      }
   }

   private Vec3d computeVec3d(LivingEntity target, Vec3d point) {
      return mc.player != null && target != null && mc.player.isGliding() && target.isGliding() ? PredictUtils.bypasselytrahacking(target) : point;
   }

   private Vec3d computeVec3d2(LivingEntity target) {
      if (this.checkCondition3(target)) {
         return this.computeVec3d(target, this.computeVec3d3(target));
      }

      Vec3d var2 = BestPoint.getNearestPoint(target);
      if (var2 == null) {
         var2 = MultipointUtils.getClosestPoint(target);
      }

      if (var2 == null) {
         var2 = this.computeVec3d3(target);
      }

      return this.computeVec3d(target, var2);
   }

   private boolean checkCondition3(LivingEntity target) {
      if (mc.player != null && target != null) {
         Vec3d var2 = mc.player.getEyePos();
         Box var3 = target.getBoundingBox();
         if (var3.expand(0.12).contains(var2)) {
            return true;
         }

         Vec3d var4 = this.computeVec3d3(target);
         return var2.squaredDistanceTo(var4) <= 2.25;
      } else {
         return false;
      }
   }

   private Vec3d computeVec3d3(LivingEntity target) {
      Box var2 = target.getBoundingBox();
      return new Vec3d(var2.getCenter().x, var2.minY + var2.getLengthY() * 0.72, var2.getCenter().z);
   }

    private LivingEntity computeLivingEntity2() {
       if (mc.player.age % 4 != 0) {
          return this.target2 != null && this.checkCondition7(this.target2) ? this.target2 : null;
       }

       float range = this.computefloat3();
       double rangeSq = (double)range * (double)range;
       ArrayList<LivingEntity> nearby = new ArrayList<>();
       for (Entity var3 : mc.world.getEntities()) {
          if (!(var3 instanceof LivingEntity var4) || var4 == mc.player) {
             continue;
          }

          if (!var4.isAlive() || var4.getHealth() <= 0.0F || var4 instanceof ArmorStandEntity) {
             continue;
          }

          if (var4.squaredDistanceTo(mc.player) > rangeSq) {
             continue;
          }

          nearby.add(var4);
       }

       nearby.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player)));
       ArrayList<LivingEntity> var1 = new ArrayList<>();
       int checked = 0;
       for (LivingEntity var4 : nearby) {
          if (checked++ >= 12) {
             break;
          }

          if (this.checkCondition7(var4)) {
             var1.add(var4);
          }
       }

       if (!var1.isEmpty() && this.isEnable()) {
         switch (this.modeSetting2.getCurrent()) {
            case "Дистанция":
               var1.sort(Comparator.comparingDouble(entity -> entity.getBoundingBox().getCenter().squaredDistanceTo(mc.player.getEyePos())));
               break;
            case "Здоровье":
               var1.sort(Comparator.comparingDouble(LivingEntity::getHealth));
               break;
            case "Угол":
               var1.sort(Comparator.comparingDouble(entity -> {
                  Vec2f var1x = RotationUtils.getRotations(entity.getBoundingBox().getCenter());
                  double var2 = Math.abs(MathHelper.wrapDegrees(var1x.x - mc.player.getYaw()));
                  double var4x = Math.abs(MathHelper.wrapDegrees(var1x.y - mc.player.getPitch()));
                  return var2 + var4x;
               }));
            case "Никакой":
         }

         return var1.isEmpty() ? null : (LivingEntity)var1.get(0);
      } else {
         return null;
      }
   }

   private void updateState7() {
      if (!this.isEnable()) {
         this.target2 = null;
         this.updateState21();
      } else if (this.target2 == null || !this.checkCondition7(this.target2)) {
         if (this.target2 != null && !this.checkCondition7(this.target2)) {
            this.updateState21();
         }

         this.target2 = this.computeLivingEntity2();
         if (this.target2 == null) {
            this.updateState21();
         }
      }
   }

   private boolean checkState7() {
      float var1 = mc.player.getAttackCooldownProgress(1.5F);
      float var2 = Math.max(0.82F, IdealHitUtils.getAICooldown() - 0.08F);
      boolean var3 = var1 >= var2;
      boolean var4 = !mc.player.isOnGround() && mc.player.getVelocity().y < 0.0 && mc.player.fallDistance > 0.0F;
      return var3 || var4;
   }

   private void updateState8() {
      Hand var1 = null;
      if (this.booleanSetting2.isState()) {
         var1 = this.computeHand();
         if (var1 != null) {
            mc.interactionManager.stopUsingItem(mc.player);
         }
      }

      this.updateState9();
      StyleRecorder.get().noteModuleAttack();
      this.flag6 = true;

      try {
         boolean var2 = false;
         if (this.target2 instanceof PlayerEntity var3 && var3.isBlocking() && this.booleanSetting3.isState()) {
            var2 = this.checkCondition4(var3);
         }

         if (!var2) {
            mc.interactionManager.attackEntity(mc.player, this.target2);
         }
      } finally {
         this.flag6 = false;
      }

      mc.player.swingHand(Hand.MAIN_HAND);
      if (var1 != null) {
         this.updateState11(var1);
      }

      if (this.rotationType.is("Funtime")) {
         this.funtimeRotation.onAttack();
      }

      if (this.rotationType.is("WhiteRise")) {
         this.whiteRiseRotation.onAttack();
      }

      this.index = this.resolveInt();
      this.whiteRiseTicksToAttack = this.index;
      this.target5.reset();
   }

    private int resolveInt() {
       int var1 = ThreadLocalRandom.current().nextInt(3, 14);
       if (this.tpsSync != null && this.tpsSync.isEnable()) {
          long var2 = (long)((float)this.tpsSync.getAdjustedCooldown(var1 * 50L) * 1.1F);
          var1 = MathHelper.clamp(Math.round((float)var2 / 50.0F), 3, 13);
       }

       if (ThreadLocalRandom.current().nextFloat() < 0.1F) {
          var1 += ThreadLocalRandom.current().nextInt(10, 31);
       }

       return var1;
    }

   private void updateState9() {
      if (this.booleanSetting6.isState() && this.target2 != null && mc.player != null && mc.world != null) {
         if (!mc.player.canSee(this.target2)) {
            if (mc.player.networkHandler != null) {
               Vec3d var1 = mc.player.getEyePos();
               Vec3d var2 = this.target2.getBoundingBox().getCenter();
               BlockHitResult var3 = mc.world.raycast(new RaycastContext(var1, var2, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
               if (var3 != null && var3.getType() == Type.BLOCK) {
                  BlockPos var4 = var3.getBlockPos();
                  if (!mc.world.getBlockState(var4).isAir()) {
                     if (!(mc.world.getBlockState(var4).getHardness(mc.world, var4) < 0.0F)) {
                        Direction var5 = var3.getSide() == null ? Direction.UP : var3.getSide();
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var4, var5));
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var4, var5));
                     }
                  }
               }
            }
         }
      }
   }

   private boolean checkCondition4(PlayerEntity entity) {
      SlotSearchResult var2 = HotbarUtil.getAxe();
      if (!var2.found()) {
         return false;
      }

      int var3 = mc.player.getInventory().selectedSlot;
      if (var2.slot() == var3) {
         mc.interactionManager.attackEntity(mc.player, entity);
         return true;
      }

      if (var2.isInHotBar()) {
         return this.checkCondition5(entity, var2.slot(), var3);
      }

      if (mc.player.currentScreenHandler.syncId != 0) {
         return false;
      }

      int var4 = this.resolveInt2(var3);
      if (var4 == -1) {
         return false;
      }

      this.updateState10(var2.slot(), var4);
      boolean var5 = false;

      try {
         var5 = this.checkCondition5(entity, var4, var3);
      } finally {
         this.updateState10(var2.slot(), var4);
      }

      return var5;
   }

   private boolean checkCondition5(PlayerEntity entity, int attackSlot, int previousSlot) {
      if (mc.player != null && mc.player.networkHandler != null && mc.interactionManager != null) {
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(attackSlot));

         try {
            mc.interactionManager.attackEntity(mc.player, entity);
            return true;
         } finally {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
         }
      } else {
         return false;
      }
   }

   private void updateState10(int inventorySlot, int hotbarSlot) {
      if (mc.player != null && mc.interactionManager != null && mc.player.networkHandler != null) {
         SwapManager.swapInventorySlotHidden(inventorySlot, hotbarSlot);
      }
   }

   private int resolveInt2(int previousSlot) {
      for (int var2 = 8; var2 >= 0; var2--) {
         if (var2 != previousSlot) {
            return var2;
         }
      }

      return previousSlot >= 0 && previousSlot < 9 ? previousSlot : -1;
   }

   private Hand computeHand() {
      if (mc.player != null && mc.player.isBlocking()) {
         Hand var1 = mc.player.getActiveHand();
         if (var1 == null) {
            return null;
         } else {
            return this.checkCondition6(mc.player.getStackInHand(var1)) ? var1 : null;
         }
      } else {
         return null;
      }
   }

   private boolean checkState8() {
      return mc.player != null && mc.player.isUsingItem() ? !this.booleanSetting2.isState() || this.computeHand() == null : false;
   }

   private void updateState11(Hand hand) {
      if (mc.player != null && mc.interactionManager != null && hand != null) {
         if (this.checkCondition6(mc.player.getStackInHand(hand)) && !mc.player.isUsingItem()) {
            mc.interactionManager.interactItem(mc.player, hand);
         }
      }
   }

   private boolean checkCondition6(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof ShieldItem;
   }

   private boolean checkState9() {
      Item var1 = mc.player.getMainHandStack().getItem();
      return var1 != Items.AIR
         && (
            var1 instanceof SwordItem
               || var1 instanceof PickaxeItem
               || var1 instanceof AxeItem
               || var1 instanceof HoeItem
               || var1 instanceof ShovelItem
               || var1 instanceof MaceItem
               || var1 == Items.MACE
         );
   }

   private boolean checkCondition7(LivingEntity entity) {
      if (entity != null && entity != mc.player) {
         if (AntiBot.checkBot(entity)) {
            return false;
         }

         if (!entity.isAlive() || entity.getHealth() <= 0.0F) {
            return false;
         }

         if (entity instanceof ArmorStandEntity) {
            return false;
         }

         if (!(entity instanceof IronGolemEntity) && !(entity instanceof BatEntity)) {
            if (entity instanceof PlayerEntity var2) {
               if (!this.value.is("Игроки")) {
                  return false;
               }

               if (this.checkCondition8(var2)) {
                  return false;
               }

               if (this.checkCondition9(var2) && !this.value.is("Голые")) {
                  return false;
               }

               if (var2.hasStatusEffect(StatusEffects.INVISIBILITY) && !this.value.is("Невидимки")) {
                  return false;
               }
            } else if (!(entity instanceof PassiveEntity) && !(entity instanceof CodEntity)) {
               if (!(entity instanceof HostileEntity)) {
                  return false;
               }

               if (!this.value.is("Мобы")) {
                  return false;
               }
            } else if (!this.value.is("Мирные")) {
               return false;
            }

            Vec3d var5 = BestPoint.getNearestPoint(entity);
            if (var5 == null) {
               var5 = MultipointUtils.getClosestPoint(entity);
            }

            if (mc.player.getEyePos().distanceTo(var5) > this.computefloat3()) {
               return false;
            }

            if (this.checkState12()) {
               if (!this.checkCondition11(entity)) {
                  return false;
               }

               if (mc.player.canSee(entity)) {
                  if (entity == this.target2) {
                     this.timestamp = 0L;
                  }
               } else {
                  if (entity != this.target2) {
                     return false;
                  }

                  long var3 = System.nanoTime();
                  if (this.timestamp == 0L) {
                     this.timestamp = var3;
                  }

                  if (var3 - this.timestamp > 180000000L) {
                     return false;
                  }
               }
            }

            return this.booleanSetting.isState() || this.booleanSetting6.isState() || mc.player.canSee(entity);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean checkCondition8(PlayerEntity player) {
      if (player != null && Lumen.INSTANCE.friendStorage != null) {
         return player.getName() != null && Lumen.INSTANCE.friendStorage.isFriend(player.getName().getString())
            ? true
            : player.getGameProfile() != null && Lumen.INSTANCE.friendStorage.isFriend(player.getGameProfile().getName());
      } else {
         return false;
      }
   }

   private boolean checkCondition9(PlayerEntity player) {
      for (ItemStack var3 : player.getArmorItems()) {
         if (!var3.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private boolean checkState10() {
      if (mc.player.getAttackCooldownProgress(1.5F) < IdealHitUtils.getAICooldown()) {
         return false;
      }

      EntityHitResult var1 = this.computeEntityHitResult();
      if (this.checkState5()
         || var1 != null && var1.getEntity() == this.target2
         || this.rotationType.is("ReallyWorld") && this.checkState11()) {
         if (this.rotationType.is("Data") && !this.checkState5() && !this.checkCondition14(var1)) {
            return false;
         }

         if (mc.player.isGliding() && this.target2.isGliding()) {
            Vec3d var5 = this.target2.getPos().add(0.0, this.target2.getHeight() / 2.0, 0.0);
            byte var3 = 0;
            Vec3d var6 = PredictUtils.predict(this.target2, var5, var3);
            if (mc.player.getEyePos().distanceTo(var6) > 5.0) {
               return false;
            }
         } else {
            double var2 = mc.player.getEyePos().distanceTo(this.target2.getBoundingBox().getCenter());
            Vec3d var4 = var2 > 3.0 ? BestPoint.getNearestPoint(this.target2) : this.target2.getBoundingBox().getCenter();
            if (var4 == null) {
               var4 = MultipointUtils.getClosestPoint(this.target2);
            }

            if (mc.player.getEyePos().distanceTo(var4) > this.floatSetting.getValue().floatValue()) {
               return false;
            }
         }

         return IdealHitUtils.canCritical(this.target2);
      } else {
         return false;
      }
   }

   private boolean checkState11() {
      EntityHitResult var1 = this.computeEntityHitResult2(this.volume, this.volume2);
      if (var1 != null && var1.getEntity() == this.target2) {
         Vec3d var2 = mc.player.getEyePos();
         Vec3d var3 = this.target2.getBoundingBox().getCenter().subtract(var2);
         if (var3.lengthSquared() < 1.0E-8) {
            return false;
         }

         Vec3d var4 = Vec3d.fromPolar(this.volume2, this.volume);
         double var5 = MathHelper.clamp(var4.normalize().dotProduct(var3.normalize()), -1.0, 1.0);
         return Math.toDegrees(Math.acos(var5)) <= 22.0;
      } else {
         return false;
      }
   }

   private boolean checkCondition10(LivingEntity entity) {
      if (entity != null && entity == this.target2 && mc.player != null) {
         float var2 = Math.max(0.62F, IdealHitUtils.getAICooldown() - 0.18F);
         if (this.index > 3 && mc.player.getAttackCooldownProgress(1.5F) < var2) {
            return false;
         }

         Box var3 = entity.getBoundingBox();
         Vec3d var4 = mc.player.getEyePos();
         double var5 = MathHelper.clamp(var4.x, var3.minX, var3.maxX);
         double var7 = MathHelper.clamp(var4.y, var3.minY, var3.maxY);
         double var9 = MathHelper.clamp(var4.z, var3.minZ, var3.maxZ);
         double var11 = var4.x - var5;
         double var13 = var4.y - var7;
         double var15 = var4.z - var9;
         double var17 = this.floatSetting.getValue().floatValue() + 0.4F;
         return var11 * var11 + var13 * var13 + var15 * var15 <= var17 * var17;
      } else {
         return false;
      }
   }

   private void updateState12() {
      if (this.flag2) {
         this.flag2 = false;
         this.funtimeRotation.reset();
      }
   }

   private boolean checkState12() {
      return this.rotationType.is("HolyWorld") || this.rotationType.is("Holy2");
   }

   private RotationsSystem computeRotationsSystem() {
      return this.rotationType.is("Holy2") ? this.holy2Rotation : this.holyWorldRotation;
   }

   private void updateState13(LivingEntity entity) {
      if (this.rotationType.is("Holy2")) {
         this.holy2Rotation.prepareAttack(entity);
      } else {
         this.holyWorldRotation.prepareAttack(entity);
      }
   }

   private void updateState14() {
      if (this.rotationType.is("Holy2")) {
         this.holy2Rotation.onAttack();
      } else {
         this.holyWorldRotation.onAttack();
      }
   }

   private boolean checkCondition11(LivingEntity entity) {
      return this.rotationType.is("Holy2") ? this.holy2Rotation.isInFov(entity) : this.holyWorldRotation.isInFov(entity);
   }

   private void updateState15() {
      this.holyWorldRotation.reset();
      this.holy2Rotation.reset();
   }

   private void updateState16() {
      if (this.flag4) {
         this.flag4 = false;
         this.updateState15();
         this.livingEntity = null;
         this.flag5 = false;
         this.timestamp = 0L;
         this.flag6 = false;
      }
   }

   private void updateState17() {
      if (this.flag) {
         this.flag = false;
         this.neuroRotation.reset();
      }
   }

   private void updateState18() {
      if (this.flag3) {
         this.flag3 = false;
         this.slothRotation.reset();
      }

      this.livingEntity = null;
      this.flag5 = false;
      this.timestamp = 0L;
   }

   public boolean isHolyWorldActive() {
      return this.isEnable() && this.checkState12();
   }

   public boolean isHolyWorldAttackInProgress() {
      return this.isHolyWorldActive() && this.flag6;
   }

   private void updateState19() {
      if (mc.player != null && mc.interactionManager != null) {
         this.updateState24();
         this.updateState25();
         if (this.flag11) {
            this.flag11 = false;
            SwapManager.closeHiddenInventory();
         } else if (this.flag9) {
            if (this.index3 >= 0 && this.index3 < 36) {
               this.index4--;
               if (this.index4 <= 0) {
                  this.flag9 = false;
                  this.updateState22();
               }
            } else {
               this.updateState23();
            }
         } else if (!this.booleanSetting5.isState()) {
            this.updateState21();
         } else if (this.target2 != null && this.checkCondition7(this.target2)) {
            if (this.checkState13()) {
               this.updateState20();
            } else {
               this.updateState21();
            }
         } else {
            this.updateState21();
         }
      } else {
         this.updateState23();
      }
   }

   private boolean checkState13() {
      if (mc.player != null && mc.world != null) {
         double var1 = 25.0;
         int var3 = 0;
         boolean var4 = false;

         for (PlayerEntity var6 : mc.world.getPlayers()) {
            if (var6 != mc.player && var6.isAlive() && !var6.isSpectator() && !(mc.player.squaredDistanceTo(var6) > var1)) {
               var3++;
               if (this.checkCondition12(var6)) {
                  var4 = true;
               }
            }
         }

         return (var3 == 1 || var3 == 2) && !var4;
      } else {
         return false;
      }
   }

   private boolean checkCondition12(PlayerEntity player) {
      ItemStack var2 = player.getMainHandStack();
      return var2 != null && !var2.isEmpty() && Registries.ITEM.getId(var2.getItem()).getPath().endsWith("_sword");
   }

   private void updateState20() {
      if (this.target2 == null || !this.checkCondition7(this.target2)) {
         this.updateState21();
      } else if (!this.checkCondition13(this.computeItemStack())) {
         if (!this.flag8 || this.index3 < 0) {
            int var1 = this.resolveInt3();
            if (var1 != -1) {
               this.flag9 = true;
               this.index4 = (int)this.floatSetting4.get();
               this.flag10 = false;
               this.index3 = var1;
               this.updateState26();
            }
         }
      }
   }

   private void updateState21() {
      if (this.flag8) {
         if (mc.player == null || mc.interactionManager == null) {
            this.updateState23();
         } else if (this.index3 >= 0 && this.index3 < 36 && this.checkCondition13(this.computeItemStack())) {
            this.flag9 = true;
            this.index4 = (int)this.floatSetting4.get();
            this.flag10 = true;
            this.updateState26();
         } else {
            this.updateState23();
         }
      }
   }

   private void updateState22() {
      if (mc.player == null || mc.interactionManager == null) {
         this.updateState23();
      } else if (this.index3 < 0 || this.index3 >= 36) {
         this.updateState23();
      } else if (!SwapManager.openHiddenInventory()) {
         this.updateState23();
      } else {
         mc.interactionManager
            .clickSlot(mc.player.currentScreenHandler.syncId, SwapManager.toContainerSlot(this.index3), 40, SlotActionType.SWAP, mc.player);
         this.flag11 = true;
         this.index5 = Math.max(this.index5, 2);
         this.itemStack = mc.player.getInventory().getStack(this.index3).copy();
         this.index6 = 10;
         if (this.flag10) {
            this.flag8 = false;
            this.index3 = -1;
            this.flag10 = false;
            this.flag9 = false;
            this.index4 = 0;
         } else {
            this.flag8 = true;
         }
      }
   }

   private void updateState23() {
      this.flag8 = false;
      this.index3 = -1;
      this.flag10 = false;
      this.flag9 = false;
      this.index4 = 0;
      this.flag11 = false;
      this.itemStack = ItemStack.EMPTY;
      this.index6 = 0;
   }

   private void updateState24() {
      if (this.index6 > 0) {
         this.index6--;
         if (SwapManager.matches(mc.player.getOffHandStack(), this.itemStack)) {
            this.index6 = 0;
         }

         if (this.index6 == 0) {
            this.itemStack = ItemStack.EMPTY;
         }
      }
   }

   private void updateState25() {
      if (this.index5 > 0) {
         this.index5--;
         if (this.index5 == 0) {
            this.updateState27();
         }
      }
   }

   private void updateState26() {
      this.index5 = Math.max(this.index5, 2);
      if (!this.flag12) {
         Sprint.pushPause(0L);
         this.flag12 = true;
      }
   }

   private void updateState27() {
      if (this.flag12) {
         Sprint.popPause();
         this.flag12 = false;
      }
   }

   private ItemStack computeItemStack() {
      if (!this.itemStack.isEmpty()) {
         return this.itemStack;
      } else {
         return mc.player == null ? ItemStack.EMPTY : mc.player.getOffHandStack();
      }
   }

   private int resolveInt3() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = mc.player.getInventory().getStack(var1);
         if (this.checkCondition13(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private boolean checkCondition13(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isOf(Items.PLAYER_HEAD) && stack.getName().getString().contains("Сфера Цербера");
   }

   public int getWhiteRiseTicksToAttack() {
      return this.whiteRiseTicksToAttack;
   }

   private boolean checkCondition14(EntityHitResult result) {
      float var2 = Math.abs(MathHelper.wrapDegrees(this.target4.x - mc.player.getYaw()));
      float var3 = Math.abs(this.target4.y - mc.player.getPitch());
      boolean var4 = var2 <= 1.15F && var3 <= 0.9F;
      boolean var5 = result != null && result.getEntity() == this.target2;
      return var4 && var5;
   }

   public boolean isAboveWater() {
      BlockPos var1 = BlockPos.ofFloored(mc.player.getPos().add(0.0, -0.4, 0.0));
      return !mc.player.isSubmergedInWater() && mc.world.getBlockState(var1).isOf(Blocks.WATER);
   }

   public float getAttackCooldown() {
      return MathHelper.clamp(((ILivingEntity)mc.player).getLastAttackedTicks() / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
   }

   public float getAttackCooldownProgressPerTick() {
      return (float)(1.0 / mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED) * 20.0);
   }

   private float computefloat3() {
      return mc.player.isGliding()
         ? this.floatSetting3.getValue().floatValue()
         : this.floatSetting.getValue().floatValue() + this.floatSetting2.getValue().floatValue();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.flag8
         && mc.player != null
         && mc.interactionManager != null
         && mc.player.networkHandler != null
         && this.index3 >= 0
         && this.index3 < 36
         && this.checkCondition13(mc.player.getOffHandStack())
         && mc.player.currentScreenHandler.syncId == 0) {
         SwapManager.swapInventorySlotHidden(this.index3, 40);
      }

      this.updateState23();
      this.index5 = 0;
      if (this.flag12) {
         Sprint.popPause();
         this.flag12 = false;
      }

      if (this.target2 != null) {
         this.timerUtils.reset();
      }

      this.target2 = null;
      this.testRotation.reset();
      this.updateState15();
      this.flag4 = false;
      this.funtimeRotation.reset();
      this.funTimeV4Rotation.reset();
      this.spookyTimeV3Rotation.reset();
      this.spookyTimeV3_116Rotation.reset();
      this.spookyTimeV3DuelsRotation.reset();
      this.fovRotation.reset();
      this.flag2 = false;
      this.slothRotation.reset();
      this.spookyRotation.reset();
      this.flag3 = false;
      this.flag5 = false;
      this.livingEntity = null;
      this.flag6 = false;
      this.timestamp = 0L;
      this.whiteRiseRotation.reset();
      this.legitRotation.reset();
      this.livingEntity3 = null;
      this.whiteRiseTicksToAttack = 0;
      this.index = 0;
      this.updateState28();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.updateState23();
      this.testRotation.reset();
      this.updateState15();
      this.flag4 = false;
      this.funtimeRotation.reset();
      this.flag2 = false;
      this.slothRotation.reset();
      this.spookyRotation.reset();
      this.flag3 = false;
      this.flag5 = false;
      this.livingEntity = null;
      this.flag6 = false;
      this.timestamp = 0L;
      this.whiteRiseRotation.reset();
      this.legitRotation.reset();
      this.livingEntity3 = null;
      this.whiteRiseTicksToAttack = 0;
      this.index = 0;
      this.updateState28();
      if (mc.player != null) {
         this.target3 = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
      }
   }

   private void updateState28() {
      this.index2 = -1;
      this.flag7 = false;
      this.livingEntity2 = null;
   }

   @Generated
   public LivingEntity getTarget() {
      return this.target2;
   }

   @Generated
   public Vec2f getCurrentRotations() {
      return this.target3;
   }

   @Generated
   public Vec2f getTargetRotations() {
      return this.target4;
   }

   @Generated
   public TimerUtils getAttackTimer() {
      return this.target5;
   }
}
