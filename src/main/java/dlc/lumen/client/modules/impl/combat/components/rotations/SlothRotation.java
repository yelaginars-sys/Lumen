package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SlothRotation extends RotationsSystem implements QClient {
   private static final long TIMESTAMP = 500000L;
   private static final long TIMESTAMP2 = 200000000L;
   private static final long TIMESTAMP3 = 120000000L;
   private static final long TIMESTAMP4 = 30000000L;
   private static final long TIMESTAMP5 = 150000000L;
   private static final double LEVEL = 0.05;
   private static final int INDEX = 1;
   private static final int INDEX2 = 4;
   private static final int INDEX3 = 20150;
   private static final float VOLUME = 0.8F;
   private static final float VOLUME2 = 1.2F;
   private final Random random = new Random();
   private final double[] level = new double[4];
   private final double[] level2 = new double[4];
   private final double[] level3 = new double[4];
   private final int[] index = new int[4];
   private LivingEntity boost2;
   private SlothRotation.AimPhase boost3 = SlothRotation.AimPhase.REACTION;
   private Vec3d boost4 = Vec3d.ZERO;
   private float volume;
   private float volume2;
   private float boost5;
   private float boost6;
   private float boost7;
   private float boost8;
   private float boost9;
   private float boost10;
   private float boost11;
   private float boost12;
   private boolean boost13;
   private double boost14 = 0.5;
   private double boost15 = 0.62;
   private double boost16 = 0.5;
   private double boost17 = 0.5;
   private double boost18 = 0.62;
   private double boost19 = 0.5;
   private double boost20 = 0.5;
   private double boost21 = 0.62;
   private double boost22 = 0.5;
   private double boost23 = 0.5;
   private double boost24 = 0.62;
   private double boost25 = 0.5;
   private double boost26 = 0.5;
   private double boost27 = 0.62;
   private double boost28 = 0.5;
   private double boost29 = 0.5;
   private double boost30 = 0.62;
   private double boost31 = 0.5;
   private double boost32 = 0.5;
   private double boost33 = 0.62;
   private double boost34 = 0.5;
   private double boost35;
   private double boost36;
   private double boost37;
   private double boost38;
   private double boost39 = 2.0;
   private double boost40 = 2.0;
   private double boost41 = 2.8;
   private double boost42 = 0.75;
   private double boost43 = 0.58;
   private double boost44;
   private double boost45;
   private double level4;
   private double level5;
   private double level6;
   private double level7;
   private float volume3;
   private float volume4;
   private float volume5;
   private float volume6;
   private float precision2;
   private float precision3;
   private float volume7;
   private float volume8;
   private float volume9;
   private float volume10;
   private float volume11;
   private float volume12 = 0.06F;
   private float volume13 = 0.1F;
   private float volume14 = 1.0F;
   private float volume15 = 1.0F;
   private float volume16 = 1.0F;
   private float volume17 = 1.0F;
   private float volume18 = 1.0F;
   private float volume19 = 1.0F;
   private float volume20;
   private float volume21;
   private float volume22;
   private float volume23;
   private float volume24 = 1.0F;
   private float volume25 = 1.0F;
   private float volume26 = 0.28F;
   private float volume27 = 2.4F;
   private float volume28;
   private float volume29;
   private float volume30;
   private float volume31;
   private float volume32;
   private float volume33;
   private float volume34;
   private float volume35;
   private long timestamp;
   private long timestamp2;
   private long timestamp3;
   private long timestamp4;
   private long timestamp5;
   private long timestamp6;
   private long timestamp7;
   private long timestamp8;
   private long timestamp9;
   private long timestamp10;
   private long timestamp11;
   private long timestamp12;
   private long timestamp13;
   private long timestamp14;
   private long timestamp15;
   private long timestamp16;
   private long precision4;
   private long precision5;
   private long timestamp17;
   private long timestamp18;
   private long timestamp19;
   private long timestamp20;
   private int index2 = 20150;
   private int index3 = 20150;
   private int index4 = Integer.MIN_VALUE;
   private int index5;
   private int index6;
   private int index7;
   private int index8;
   private int index9 = Integer.MIN_VALUE;
   private int index10 = Integer.MIN_VALUE;
   private boolean flag;
   private float volume36 = 1.0F;
   private boolean precision6;
   private boolean flag2;
   private boolean flag3;
   private int index11;
   private int index12;
   private int index13 = -1;
   private int index14 = -1;
   private int index15;
   private SlothRotation.PathModel precision7 = SlothRotation.PathModel.MINIMUM_JERK;
   private SlothRotation.PathModel pathModel = SlothRotation.PathModel.MINIMUM_JERK;
   private int index16;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private boolean flag7;
   private float volume37;
   private float volume38;
   private float volume39;
   private float volume40;
   private float volume41;
   private float volume42;
   private float volume43;
   private long timestamp21;
   private long timestamp22;
   private float volume44;
   private float volume45 = 1.0F;
   private boolean precision8;
   private int index17 = Integer.MIN_VALUE;
   private int index18;
   private boolean flag8;
   private int index19 = Integer.MIN_VALUE;
   private float volume46 = 1.0F;
   private float volume47 = 1.0F;
   private static final int INDEX4 = 6;
   private int index20 = Integer.MIN_VALUE;
   private int index21;
   private Vec3d vec3d = Vec3d.ZERO;
    private float volume48 = 1.0F;
    private float volume49;
    private float volume50;
    private float volume51;
    private float volume52;
    private float volume53;
    private float volume54;
    private float volume55;
    private long timestamp24;
   private long timestamp23;
   private final SlothPatternEasing.PatternDriver slothPatternEasing = new SlothPatternEasing.PatternDriver();
   private float boost46 = 1.0F;

   public void setSpeedBoost(float boost) {
      this.boost46 = Math.max(0.1F, boost);
   }

   public void reset() {
      this.boost2 = null;
      this.boost3 = SlothRotation.AimPhase.REACTION;
      this.boost4 = Vec3d.ZERO;
      this.boost5 = 0.0F;
      this.boost6 = 0.0F;
      this.boost7 = 0.0F;
      this.boost8 = 0.0F;
      this.boost9 = 0.0F;
      this.boost10 = 0.0F;
      this.boost11 = 0.0F;
      this.boost12 = 0.0F;
      this.boost13 = false;
      this.boost14 = this.boost17 = 0.5;
      this.boost15 = this.boost18 = 0.62;
      this.boost16 = this.boost19 = 0.5;
      this.boost20 = this.boost23 = this.boost26 = 0.5;
      this.boost21 = this.boost24 = this.boost27 = 0.62;
      this.boost22 = this.boost25 = this.boost28 = 0.5;
      this.boost29 = this.boost32 = 0.5;
      this.boost30 = this.boost33 = 0.62;
      this.boost31 = this.boost34 = 0.5;
      this.boost35 = this.boost36 = this.boost37 = 0.0;
      this.boost38 = 0.0;
      this.boost39 = this.boost40 = 2.0;
      this.boost41 = 2.8;
      this.boost42 = 0.75;
      this.boost43 = 0.58;
      this.boost44 = this.boost45 = this.level4 = 0.0;
      this.level5 = this.level6 = this.level7 = 0.0;
      this.volume3 = this.volume4 = 0.0F;
      this.volume5 = this.volume6 = 0.0F;
      this.precision2 = this.precision3 = 0.0F;
      this.volume7 = this.volume8 = 0.0F;
      this.volume9 = this.volume10 = 0.0F;
      this.volume11 = 0.0F;
      this.volume12 = 0.06F;
      this.volume13 = 0.1F;
      this.volume14 = this.volume15 = 1.0F;
      this.volume16 = this.volume17 = 1.0F;
      this.volume18 = this.volume19 = 1.0F;
      this.volume20 = this.volume21 = 0.0F;
      this.volume22 = this.volume23 = 0.0F;
      this.volume24 = this.volume25 = 1.0F;
      this.volume26 = 0.28F;
      this.volume27 = 2.4F;
      this.timestamp = 0L;
      this.timestamp2 = 0L;
      this.timestamp3 = 0L;
      this.timestamp4 = 0L;
      this.timestamp5 = 0L;
      this.timestamp6 = 0L;
      this.timestamp7 = 0L;
      this.timestamp8 = 0L;
      this.timestamp9 = 0L;
      this.timestamp10 = 0L;
      this.timestamp11 = 0L;
      this.timestamp12 = 0L;
      this.timestamp13 = 0L;
      this.timestamp14 = 0L;
      this.timestamp15 = 0L;
      this.timestamp16 = 0L;
      this.precision4 = 0L;
      this.precision5 = 0L;
      this.timestamp17 = 0L;
      this.timestamp18 = 0L;
      this.timestamp19 = 0L;
      this.timestamp20 = 0L;
      this.index2 = 20150;
      this.index3 = 20150;
      this.index4 = Integer.MIN_VALUE;
      this.index5 = 0;
      this.index6 = 0;
      this.index7 = 0;
      this.index8 = 0;
      this.index9 = Integer.MIN_VALUE;
      this.index10 = Integer.MIN_VALUE;
      this.flag = false;
      this.volume36 = 1.0F;
      this.precision6 = false;
      this.flag2 = false;
      this.flag3 = mc.player != null && !mc.player.isOnGround();
      this.updateState22();
      this.index15 = 0;
      this.precision7 = SlothRotation.PathModel.MINIMUM_JERK;
      this.pathModel = SlothRotation.PathModel.MINIMUM_JERK;
      this.index16 = 0;
      this.flag4 = false;
      this.flag5 = false;
      this.flag6 = false;
      this.flag7 = false;
      this.volume37 = 0.0F;
      this.volume38 = 0.0F;
      this.volume39 = 0.0F;
      this.volume40 = 0.0F;
      this.volume41 = 0.0F;
      this.volume42 = 0.0F;
      this.volume43 = 0.0F;
      this.timestamp21 = 0L;
      this.timestamp22 = 0L;
      this.volume44 = 0.0F;
      this.volume45 = 1.0F;
      this.index17 = Integer.MIN_VALUE;
      this.index18 = 0;
      this.flag8 = false;
      this.index19 = Integer.MIN_VALUE;
      this.volume46 = 1.0F;
      this.volume47 = 1.0F;
      this.index20 = Integer.MIN_VALUE;
      this.index21 = 0;
      this.vec3d = Vec3d.ZERO;
      this.volume48 = 1.0F;
      this.volume49 = 0.0F;
      this.volume50 = this.volume51 = this.volume52 = 0.0F;
      this.volume53 = this.volume54 = this.volume55 = 0.0F;
      this.timestamp24 = 0L;
      this.timestamp23 = 0L;
      this.slothPatternEasing.reset();
      if (mc.player != null) {
         this.volume = mc.player.getYaw();
         this.volume2 = mc.player.getPitch();
      } else {
         this.volume = 0.0F;
         this.volume2 = 0.0F;
      }
   }

   public void setPrecisionLock(boolean precision) {
      long var2 = System.nanoTime();
      if (precision && !this.precision6) {
         this.updateState20(var2);
         if (this.boost2 != null && var2 - this.precision5 >= 150000000L) {
            this.updateState14(var2, true);
         }

         this.precision5 = var2;
      }

      this.precision6 = precision;
      if (precision) {
         this.precision4 = Math.max(this.precision4, var2 + 120000000L);
      }
   }

   public void setVisibleAim(boolean visible) {
      this.precision8 = visible;
   }

   public void onAttack() {
      if (mc.player != null && this.boost2 != null) {
         long var1 = System.nanoTime();
         this.precision4 = Math.max(this.precision4, var1 + 100000000L);
         if (this.random.nextDouble() < 0.32) {
            this.precision2 = this.computefloat8(1.5F, 4.5F);
         } else {
            this.precision2 = this.computefloat8(0.04F, 0.13F);
         }

         double var3 = this.random.nextDouble();
         this.precision3 = var3 < 0.25
            ? this.computefloat9(0.8F, 2.2F)
            : (var3 < 0.63 ? this.computefloat9(0.12F, 0.42F) : (var3 < 0.83 ? -this.computefloat9(0.08F, 0.28F) : 0.0F));
         boolean var5 = this.precision7 == SlothRotation.PathModel.EMPIRICAL
            && this.flag4
            && var1 >= this.timestamp4
            && var1 < this.timestamp5;
         if (!var5) {
            this.updateState14(var1, true);
         }

         this.boost3 = SlothRotation.AimPhase.RECOVER;
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         long var2 = System.nanoTime();
         RotationStorage var4 = RotationStorage.instance;
         if (var4 != null && var4.currentPriority() > 1) {
            this.updateState12(var2, true);
            this.flag2 = true;
         } else {
            if (this.boost2 != target) {
               this.updateState(target, var2);
            }

            if (this.flag2) {
               this.updateState12(var2, true);
               this.flag2 = false;
            } else {
               long var5 = this.timestamp == 0L ? 4166667L : var2 - this.timestamp;
               if (var5 >= 500000L) {
                  if (var5 > 200000000L) {
                     this.updateState12(var2, true);
                     this.boost3 = SlothRotation.AimPhase.ACQUIRE;
                  } else {
                     this.timestamp = var2;
                     double var7 = Math.min(var5 / 1.0E9, 0.05);
                     boolean var9 = this.precision6 || var2 < this.precision4;
                     boolean var10 = !mc.player.isOnGround();
                     if (var10 && !this.flag3 && mc.player.getVelocity().y > 0.035) {
                        this.updateState15(var2, var9);
                     }

                     if (var10 && this.flag7 && mc.player.getVelocity().y < -0.07) {
                        this.flag7 = false;
                        this.updateState14(var2, var9);
                     } else if (!var10) {
                        this.flag7 = false;
                     }

                     this.flag3 = var10;
                     this.updateState2(var2, var7, var9);
                     this.updateState4(target, var2, var7);
                     Vec3d var11 = this.computeVec3d(target, var7, var9);
                     Vec3d var12 = var11.subtract(mc.player.getEyePos());
                     float var13 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var12.z, var12.x)) - 90.0);
                     float var14 = (float)(-Math.toDegrees(Math.atan2(var12.y, var12.horizontalLength())));
                     float var15 = this.computefloat5(target, var12.length());
                     float var16 = this.computefloat6(target, var12.length());
                      float var17 = MathHelper.clamp(this.volume37, -var15 * (var9 ? 0.38F : 0.85F), var15 * (var9 ? 0.38F : 0.85F));
                      float var18 = MathHelper.clamp(this.volume38, -var16 * (var9 ? 0.31F : 0.72F), var16 * (var9 ? 0.31F : 0.72F));
                      float var19 = MathHelper.clamp(this.computefloat3(var2), -var16 * (var9 ? 0.25F : 0.78F), var16 * (var9 ? 0.25F : 0.78F));
                     this.updateState19(var2, var9, var15, var16);
                     float var20 = this.computefloat4(var2);
                      float var21 = (var9 ? 0.25F : (this.boost3 == SlothRotation.AimPhase.ACQUIRE ? 0.35F : 1.0F)) * this.volume13;
                     float var22 = MathHelper.clamp(var15 * 0.35F, 0.12F, 1.05F) * var21;
                     float var23 = MathHelper.clamp(var15 * 0.22F, 0.09F, 0.75F) * var21;
                     this.precision2 = this.precision2 * (float)Math.exp(-var7 / 0.075);
                     this.precision3 = this.precision3 * (float)Math.exp(-var7 / 0.09);
                     float var24 = var9 ? 0.52F : 1.0F;
                     var13 = MathHelper.wrapDegrees(
                        var13
                           + var17
                           + this.volume3 * var22
                           + this.volume7 * this.volume11 * var24
                           + this.volume22 * var20
                           + this.precision2
                     );
                     var14 = MathHelper.clamp(
                        var14
                           + var18
                           + var19
                           + this.volume4 * var23
                           + this.volume8 * this.volume11 * var24
                           + this.volume23 * var20
                           + this.precision3,
                        -89.0F,
                        89.0F
                     );
                     float var25 = MathHelper.wrapDegrees(var13 - this.volume);
                     float var26 = var14 - this.volume2;
                     float var27 = (float)Math.hypot(var25, var26);
                     if (this.checkCondition2(var27, var9, var2 < this.timestamp2)) {
                        this.updateState9(var13, var14, var7);
                     } else if (this.checkCondition3(var2, var9, var2 < this.timestamp2)) {
                        this.updateState9(var13, var14, var7);
                     } else {
                        this.updateState6(var13, var14, var7);
                        if (var2 < this.timestamp2 && !var9) {
                           this.boost3 = SlothRotation.AimPhase.REACTION;
                           this.boost5 = this.computefloat10(this.boost5, 0.0F, this.volume32 * (float)var7);
                           this.boost6 = this.computefloat10(this.boost6, 0.0F, this.volume33 * (float)var7);
                        } else {
                           if (!var9) {
                              this.boost3 = var27 > 10.0F ? SlothRotation.AimPhase.ACQUIRE : SlothRotation.AimPhase.TRACK;
                           } else {
                              this.boost3 = var2 >= this.precision4
                                    || !(Math.abs(this.precision2) > 0.01F) && !(Math.abs(this.precision3) > 0.01F)
                                 ? SlothRotation.AimPhase.FOCUS
                                 : SlothRotation.AimPhase.RECOVER;
                           }

                           this.updateState5(var25, var26, var7, var9, mc.player.isGliding());
                        }

                        float var28 = mc.player.age == this.index9 ? this.volume36 : 1.0F;
                        this.updateState10(var9);
                        float var29 = this.boost5 * (float)var7 * var28;
                        float var30 = this.boost6 * (float)var7 * var28;
                        float var31 = var29 * this.volume46;
                        float var32 = var30 * this.volume47;
                        if (var31 * var25 > 0.0F && Math.abs(var31) > Math.abs(var25)) {
                           boolean var33 = Math.abs(var29) > Math.abs(var25);
                           if (Math.abs(this.boost9) > 8.0F) {
                              var31 = var25 + MathHelper.clamp(this.boost9 * (float)var7 * 0.58F, -0.45F, 0.45F);
                              if (var33) {
                                 this.boost5 = this.boost9 * 0.58F;
                                 this.boost7 = 0.0F;
                              }
                           } else {
                              var31 = var25 * this.computefloat9(0.82F, 0.98F);
                              if (var33) {
                                 this.boost5 = 0.0F;
                                 this.boost7 = 0.0F;
                              }
                           }
                        }

                        if (var32 * var26 > 0.0F && Math.abs(var32) > Math.abs(var26)) {
                           boolean var46 = Math.abs(var30) > Math.abs(var26);
                           if (Math.abs(this.boost10) > 6.0F) {
                              var32 = var26 + MathHelper.clamp(this.boost10 * (float)var7 * 0.52F, -0.32F, 0.32F);
                              if (var46) {
                                 this.boost6 = this.boost10 * 0.52F;
                                 this.boost8 = 0.0F;
                              }
                           } else {
                              var32 = var26 * this.computefloat9(0.82F, 0.98F);
                              if (var46) {
                                 this.boost6 = 0.0F;
                                 this.boost8 = 0.0F;
                              }
                           }
                        }

                        float var47;
                        if (this.timestamp5 > this.timestamp4) {
                           var47 = (float)this.resolveDouble14(
                              (double)(var2 - this.timestamp4) / (this.timestamp5 - this.timestamp4), 0.0, 1.0
                           );
                        } else {
                           var47 = 0.5F;
                        }

                        SlothPatternEasing.PatternOffset var34 = this.slothPatternEasing.advance(var7, var47);
                        var31 += var34.yawOffset();
                        var32 += var34.pitchOffset();
                        float var35 = MathHelper.clamp(0.85F + SlothPatternEasing.finalSpeed(mc.player, var47) * 0.35F, 0.85F, 1.35F);
                        var31 = SlothPatternEasing.clampAndScale(var31, 60.0F, var35);
                        var32 = SlothPatternEasing.clampAndScale(var32, 24.0F, var35);
                        float var36 = this.computefloat9(62.0F, 70.0F);
                        float var37 = this.computefloat9(9.0F, 11.0F);
                        var31 = MathHelper.clamp(var31, -var36, var36);
                        var32 = MathHelper.clamp(var32, -var37, var37);
                        if (Math.abs(var31) + Math.abs(var32) >= GCDUtil.getGCDValue()) {
                           this.index21 = 0;
                        }

                        this.volume = MathHelper.wrapDegrees(this.volume + var31);
                        this.volume2 = MathHelper.clamp(this.volume2 + var32, -89.0F, 89.0F);
                        if (Float.isFinite(this.volume) && Float.isFinite(this.volume2)) {
                           this.updateState7(var2, var9);
                        } else {
                           this.updateState12(var2, true);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void updateState(LivingEntity target, long now) {
      this.boost2 = target;
      this.updateState12(now, true);
      this.timestamp18 = now;
      this.timestamp19 = 0L;
      this.timestamp20 = 0L;
      this.index2 = 20150;
      this.index3 = 20150;
      this.index4 = Integer.MIN_VALUE;
      this.index6 = this.resolveInt();
      this.index5 = this.index6 - 1;
      this.index7 = 0;
      this.index8 = 0;
      this.index9 = Integer.MIN_VALUE;
      this.index10 = Integer.MIN_VALUE;
      this.flag = false;
      this.volume36 = 1.0F;
      this.index17 = Integer.MIN_VALUE;
      this.index18 = 0;
      this.flag8 = false;
      this.index19 = Integer.MIN_VALUE;
      this.index20 = Integer.MIN_VALUE;
      this.index21 = 0;
      this.vec3d = target.getVelocity();
      this.volume48 = 1.0F;
      this.volume49 = 0.0F;
      this.timestamp23 = 0L;
      this.slothPatternEasing.reset();
      this.boost4 = target.getVelocity();
      this.boost14 = this.boost17 = this.resolveDouble13(0.4, 0.6);
      this.boost15 = this.boost18 = this.resolveDouble13(0.64, 0.82);
      this.boost16 = this.boost19 = this.resolveDouble13(0.4, 0.6);
      this.boost20 = this.boost23 = this.boost26 = this.boost14;
      this.boost21 = this.boost24 = this.boost27 = this.boost15;
      this.boost22 = this.boost25 = this.boost28 = this.boost16;
      this.boost29 = this.boost32 = this.boost14;
      this.boost30 = this.boost33 = this.boost15;
      this.boost31 = this.boost34 = this.boost16;
      this.updateState22();
      this.volume3 = this.volume4 = 0.0F;
      this.volume5 = this.volume6 = 0.0F;
      this.precision2 = this.precision3 = 0.0F;
      this.volume7 = this.volume8 = 0.0F;
      this.volume9 = this.volume10 = 0.0F;
      this.volume11 = 0.0F;
      this.volume12 = SlothDatasetProfile.pathNoiseScale(this.random);
      this.volume13 = 0.1F;
      this.volume14 = this.volume15 = SlothDatasetProfile.initialSpeedScale(this.random);
      this.volume16 = this.volume17 = 1.0F;
      this.volume18 = this.volume19 = SlothDatasetProfile.perceptionScale(this.random);
      this.volume20 = this.volume21 = SlothDatasetProfile.perceptionBias(this.random);
      this.volume22 = this.volume23 = 0.0F;
      this.volume24 = this.volume25 = 1.0F;
      this.volume26 = 0.28F;
      this.volume27 = 2.4F;
      this.level5 = this.level6 = this.level7 = 0.0;
      this.timestamp6 = now;
       this.volume28 = SlothDatasetProfile.maximumYawSpeed(this.random);
       this.volume29 = SlothDatasetProfile.maximumPitchSpeed(this.random);
       this.volume30 = SlothDatasetProfile.yawAcceleration(this.random);
       this.volume31 = SlothDatasetProfile.pitchAcceleration(this.random);
       this.volume32 = SlothDatasetProfile.yawDeceleration(this.random);
       this.volume33 = SlothDatasetProfile.pitchDeceleration(this.random);
       this.volume34 = SlothDatasetProfile.yawJerkLimit(this.random);
       this.volume35 = SlothDatasetProfile.pitchJerkLimit(this.random);
      float var4 = this.computefloat2(target);
      long var5 = SlothDatasetProfile.reactionMillis(this.random, var4, this.precision6);
      this.timestamp2 = now + var5 * 1000000L;
      this.updateState13(now, this.precision6, false);
      this.timestamp7 = now + this.resolveLong(260L, 720L);
      this.timestamp8 = now + this.resolveLong(240L, 800L);
      this.timestamp10 = now + this.resolveLong(110L, 340L);
      this.timestamp11 = now + this.resolveLong(320L, 920L);
      this.timestamp12 = now + this.resolveLong(500L, 1300L);
      this.timestamp15 = now + this.resolveLong(1000L, 2800L);
      this.flag3 = !mc.player.isOnGround();
      this.boost3 = var5 > 0L ? SlothRotation.AimPhase.REACTION : SlothRotation.AimPhase.ACQUIRE;
   }

   private void updateState2(long now, double deltaTime, boolean precision) {
      this.updateState3(deltaTime);
      this.updateState17(now);
      if (now >= this.timestamp3) {
         this.updateState13(now, precision, false);
         this.updateState17(now);
      }

      if (now >= this.timestamp7) {
         float var6 = this.computefloat7();
         this.volume5 = MathHelper.clamp(this.computefloat7() * 0.78F + var6 * 0.22F, -1.0F, 1.0F);
         this.volume6 = MathHelper.clamp(this.computefloat7() * 0.82F + var6 * 0.18F, -1.0F, 1.0F);
         this.timestamp7 = now + this.resolveLong(260L, 760L);
      }

      double var18 = 1.0 - Math.exp(-deltaTime / 0.38);
      double var8 = 1.0 - Math.exp(-deltaTime / 0.48);
      this.volume3 = this.volume3 + (this.volume5 - this.volume3) * (float)var18;
      this.volume4 = this.volume4 + (this.volume6 - this.volume4) * (float)var8;
      if (now >= this.timestamp10) {
         this.volume15 = SlothDatasetProfile.driftingSpeedScale(this.random);
         this.timestamp10 = now + this.resolveLong(110L, 360L);
      }

      double var10 = 1.0 - Math.exp(-deltaTime / 0.16);
      this.volume14 = this.volume14 + (this.volume15 - this.volume14) * (float)var10;
      double var12 = 1.0 - Math.exp(-deltaTime / 0.09);
      this.volume16 = this.volume16 + (this.volume17 - this.volume16) * (float)var12;
      if (now >= this.timestamp11) {
         this.volume19 = SlothDatasetProfile.perceptionScale(this.random);
         this.volume21 = SlothDatasetProfile.perceptionBias(this.random);
         this.timestamp11 = now + this.resolveLong(340L, 1150L);
      }

      double var14 = 1.0 - Math.exp(-deltaTime / 0.22);
      this.volume18 = this.volume18 + (this.volume19 - this.volume18) * (float)var14;
      this.volume20 = this.volume20 + (this.volume21 - this.volume20) * (float)var14;
      if (precision) {
         this.volume25 = 1.0F;
         if (this.timestamp16 > 0L) {
            this.timestamp16 = 0L;
            this.timestamp15 = now + this.resolveLong(700L, 2400L);
         }
      } else if (this.timestamp16 > 0L && now >= this.timestamp16) {
         this.volume25 = 1.0F;
         this.timestamp16 = 0L;
         this.timestamp15 = now + this.resolveLong(700L, 2400L);
      } else if (this.timestamp16 == 0L && now >= this.timestamp15) {
         this.volume25 = this.computefloat9(0.78F, 0.94F);
         this.timestamp16 = now + this.resolveLong(15L, 54L);
      }

      double var16 = 1.0 - Math.exp(-deltaTime / (this.volume25 < this.volume24 ? 0.022 : 0.045));
      this.volume24 = this.volume24 + (this.volume25 - this.volume24) * (float)var16;
      this.updateState18(now, deltaTime);
   }

   private void updateState3(double deltaTime) {
      float var3;
      if (mc.player.isOnGround()) {
         var3 = 1.0F;
      } else {
         double var4 = mc.player.getVelocity().y;
         if (var4 > 0.04) {
            var3 = 0.82F;
         } else if (var4 > -0.1) {
            var3 = 0.74F;
         } else {
            var3 = MathHelper.clamp(1.0F + (float)Math.abs(var4) * 0.34F, 1.04F, 1.18F);
         }
      }

      float var6 = (float)(1.0 - Math.exp(-deltaTime / 0.065));
      this.volume45 = this.volume45 + (var3 - this.volume45) * var6;
   }

   private void updateState4(LivingEntity target, long now, double deltaTime) {
      Vec3d var6 = target.getVelocity();
      double var7 = var6.horizontalLength();
      double var9 = this.vec3d.horizontalLength();
      if (var7 > 0.08 && var9 > 0.08) {
         double var11 = (var6.x * this.vec3d.x + var6.z * this.vec3d.z) / (var7 * var9);
         long var13 = this.volume49 > 0.6F ? 110000000L : 250000000L;
         if (var11 < 0.5 && now - this.timestamp23 > var13) {
            this.timestamp23 = now;
            this.volume49 = Math.min(1.0F, this.volume49 + 0.45F);
            if (this.random.nextDouble() < 0.85) {
               this.volume48 = Math.max(this.volume48, this.computefloat9(1.28F, 1.68F));
            }
         }
      }

      this.vec3d = var6;
      float var15 = (float)(1.0 - Math.exp(-deltaTime / 0.3));
      this.volume48 = this.volume48 + (1.0F - this.volume48) * var15;
      float var12 = (float)(1.0 - Math.exp(-deltaTime / 1.1));
      this.volume49 = this.volume49 + (0.0F - this.volume49) * var12;
      if (this.volume49 > 0.35F) {
         this.volume48 = Math.max(this.volume48, 1.0F + 0.28F * this.volume49);
      }
   }

   private Vec3d computeVec3d(LivingEntity target, double deltaTime, boolean precision) {
      Vec3d var5 = target.getVelocity();
      if (var5.lengthSquared() > 2.25) {
         var5 = var5.normalize().multiply(1.5);
      }

      double var6 = var5.subtract(this.boost4).horizontalLength();
      double var8 = this.resolveDouble14(0.12 - var6 * 0.35, 0.045, 0.12);
      double var10 = 1.0 - Math.exp(-deltaTime / var8);
      this.boost4 = this.boost4.lerp(var5, var10);
      Box var12 = target.getBoundingBox();
      Vec3d var13 = new Vec3d(
         var12.minX + var12.getLengthX() * this.boost14,
         var12.minY + var12.getLengthY() * this.boost15,
         var12.minZ + var12.getLengthZ() * this.boost16
      );
      this.updateMultipoint(deltaTime);
      var13 = var13.add(
         var12.getLengthX() * this.volume50, var12.getLengthY() * this.volume51, var12.getLengthZ() * this.volume52
      );
      double var14 = mc.player.getEyePos().distanceTo(var13);
      double var16;
      if (precision) {
         var16 = this.resolveDouble14(
            (0.16 + var14 * 0.025 + this.boost4.horizontalLength() * 0.09) * this.volume18 + this.volume20 * 0.18, 0.1, 0.42
         );
      } else if (!mc.player.isGliding() && !target.isGliding()) {
         var16 = this.resolveDouble14((0.52 + var14 * 0.09) * this.volume18 + this.volume20, -0.2, 1.35);
      } else {
         var16 = this.resolveDouble14((0.95 + var14 * 0.08) * this.volume18 + this.volume20, 0.15, 1.85);
      }

      if (this.volume49 > 0.2F) {
         var16 *= 1.0 - 0.45 * this.volume49;
      }

      Vec3d var18 = this.boost4.multiply(var16);
      Vec3d var19 = var13.add(var18);
      if (!precision) {
         return var19;
      }

      double var20 = Math.min(var12.getLengthX(), var12.getLengthZ()) * 0.12;
      double var22 = var12.getLengthY() * 0.1;
      double var24 = Math.min(
         this.resolveDouble(var13.x, var18.x, var12.minX + var20, var12.maxX - var20),
         Math.min(
            this.resolveDouble(var13.y, var18.y, var12.minY + var22, var12.maxY - var22),
            this.resolveDouble(var13.z, var18.z, var12.minZ + var20, var12.maxZ - var20)
         )
      );
      return var13.add(var18.multiply(var24));
   }

    private void updateMultipoint(double deltaTime) {
       long now = System.nanoTime();
       if (now >= this.timestamp24) {
          int pick = this.random.nextInt(5);
          float tx = 0.0F;
          float ty = 0.0F;
          float tz = 0.0F;
          if (pick == 0) {
             ty = 0.22F;
          } else if (pick == 1) {
             ty = -0.08F;
          } else if (pick == 2) {
             ty = -0.42F;
          } else if (pick == 3) {
             tx = -0.3F;
          } else {
             tx = 0.3F;
          }

          tx += (this.random.nextFloat() - 0.5F) * 0.24F;
          ty += (this.random.nextFloat() - 0.5F) * 0.16F;
          tz += (this.random.nextFloat() - 0.5F) * 0.24F;
          if (this.random.nextFloat() < 0.22F) {
             float push = (this.random.nextBoolean() ? 1.0F : -1.0F) * (0.15F + this.random.nextFloat() * 0.15F);
             int axis = this.random.nextInt(3);
             if (axis == 0) {
                tx += push;
             } else if (axis == 1) {
                ty += push;
             } else {
                tz += push;
             }
          }

          this.volume53 = tx;
          this.volume54 = ty;
          this.volume55 = tz;
          this.timestamp24 = now + (500L + this.random.nextInt(1000)) * 1000000L;
       }

       float rate = (float)(1.0 - Math.exp(-deltaTime / 0.12));
       this.volume50 += (this.volume53 - this.volume50) * rate;
       this.volume51 += (this.volume54 - this.volume51) * rate;
       this.volume52 += (this.volume55 - this.volume52) * rate;
    }

    private double resolveDouble(double value, double delta, double minimum, double maximum) {
      if (Math.abs(delta) < 1.0E-8) {
         return 1.0;
      }

      double var9 = delta > 0.0 ? (maximum - value) / delta : (minimum - value) / delta;
      return this.resolveDouble14(var9, 0.0, 1.0);
   }

   private void updateState5(float yawError, float pitchError, double deltaTime, boolean precision, boolean flying) {
      float var7 = this.volume14
         * this.volume16
         * this.volume24
         * this.volume45
         * this.volume48
         * (flying ? 1.12F : 1.0F);
      if (precision) {
         var7 *= 1.272F;
      }

      float var8 = Math.abs(yawError);
      float var9 = Math.abs(pitchError);
      float var10 = flying ? 1400.0F : (var8 < 18.0F ? 800.0F : (var8 < 65.0F ? 1150.0F : 1400.0F));
      float var11 = flying ? 520.0F : (var9 < 8.0F ? 230.0F : (var9 < 24.0F ? 330.0F : 430.0F));
      float var12 = this.boost46;
      float var13 = Math.min(var10, this.volume28 * var7 * var12);
      float var14 = Math.min(var11, this.volume29 * var7 * var12);
      float var15 = 0.22F + 0.78F * (float)Math.exp(-Math.abs(yawError) / 28.0F);
      float var16 = 0.2F + 0.8F * (float)Math.exp(-Math.abs(pitchError) / 16.0F);
      float var17 = this.computefloat(yawError, this.volume32, var13) + this.boost9 * (precision ? 0.92F : 0.78F) * var15;
      float var18 = this.computefloat(pitchError, this.volume33, var14) + this.boost10 * (precision ? 0.82F : 0.66F) * var16;
      var17 = MathHelper.clamp(var17, -var13, var13);
      var18 = MathHelper.clamp(var18, -var14, var14);
      float var19 = 1.0F + MathHelper.clamp((var8 - 30.0F) / 80.0F, 0.0F, 0.55F);
      float var20 = 1.0F + MathHelper.clamp((var9 - 18.0F) / 42.0F, 0.0F, 0.3F);
      boolean var21 = this.boost5 * yawError < 0.0F && Math.abs(this.boost5) > 12.0F;
      float var22 = var21 ? 1.55F : 1.0F;
      float var23 = (this.checkCondition(this.boost5, var17) ? this.volume32 : this.volume30) * var19 * var22;
      float var24 = (this.checkCondition(this.boost6, var18) ? this.volume33 : this.volume31) * var20;
      float var25 = precision ? 1.3440001F : 1.0F;
      float var26 = precision ? 1.32F : 1.0F;
      float var27 = MathHelper.clamp((var17 - this.boost5) / (float)deltaTime, -var23 * var25, var23 * var25);
      float var28 = MathHelper.clamp((var18 - this.boost6) / (float)deltaTime, -var24 * var26, var24 * var26);
      this.boost7 = this.computefloat10(
         this.boost7, var27, this.volume34 * var25 * var22 * (float)Math.sqrt(var19) * (float)deltaTime
      );
      this.boost8 = this.computefloat10(this.boost8, var28, this.volume35 * var26 * (float)Math.sqrt(var20) * (float)deltaTime);
      float var29 = this.boost5 + this.boost7 * (float)deltaTime;
      float var30 = this.boost6 + this.boost8 * (float)deltaTime;
      if ((var17 - this.boost5) * (var17 - var29) <= 0.0F) {
         var29 = var17;
         this.boost7 *= 0.22F;
      }

      if ((var18 - this.boost6) * (var18 - var30) <= 0.0F) {
         var30 = var18;
         this.boost8 *= 0.22F;
      }

      this.boost5 = MathHelper.clamp(var29, -var13, var13);
      this.boost6 = MathHelper.clamp(var30, -var14, var14);
   }

   private void updateState6(float wantedYaw, float wantedPitch, double deltaTime) {
      if (!this.boost13) {
         this.boost11 = wantedYaw;
         this.boost12 = wantedPitch;
         this.boost9 = 0.0F;
         this.boost10 = 0.0F;
         this.boost13 = true;
      } else {
         float var5 = MathHelper.wrapDegrees(wantedYaw - this.boost11) / (float)deltaTime;
         float var6 = (wantedPitch - this.boost12) / (float)deltaTime;
         var5 = MathHelper.clamp(var5, -1100.0F, 1100.0F);
         var6 = MathHelper.clamp(var6, -620.0F, 620.0F);
         float var7 = (float)(1.0 - Math.exp(-deltaTime / 0.055));
         this.boost9 = this.boost9 + (var5 - this.boost9) * var7;
         this.boost10 = this.boost10 + (var6 - this.boost10) * var7;
         this.boost11 = wantedYaw;
         this.boost12 = wantedPitch;
      }
   }

   private boolean checkCondition(float currentVelocity, float desiredVelocity) {
      return Math.abs(currentVelocity) < 0.001F ? false : currentVelocity * desiredVelocity <= 0.0F || Math.abs(desiredVelocity) < Math.abs(currentVelocity);
   }

   private float computefloat(float error, float deceleration, float maximumSpeed) {
      if (Math.abs(error) < 0.001F) {
         return 0.0F;
      }

      float var4 = (float)Math.sqrt(2.0F * deceleration * Math.abs(error));
      return Math.copySign(Math.min(maximumSpeed, var4), error);
   }

   private void updateState7(long now, boolean precision) {
      boolean var4 = precision && mc.player.age != this.index10;
      boolean var5 = var4 || mc.player.age == this.index9;
      long var6 = this.timestamp20 == 0L ? Long.MAX_VALUE : now - this.timestamp20;
      this.timestamp20 = now;
      long var8 = this.timestamp19 == 0L ? Long.MAX_VALUE : now - this.timestamp19;
      if (var5 || var6 >= 12500000L || now >= this.timestamp18) {
         if (this.timestamp19 != 0L) {
            int var10 = (int)this.resolveDouble14(var8 / 1000.0, 12500.0, 150000.0);
            this.index3 = this.index2;
            this.index2 = var10;
         }

         int var20 = SlothDatasetProfile.mouseReportIntervalMicros(this.random);
         double var11 = 20150.0
            - (this.index2 - 20150) * 0.19
            + (this.index3 - 20150) * 0.275
            + (var20 - 20150) * 0.95
            + this.resolveDouble13(-260.0, 260.0);
         int var13 = (int)this.resolveDouble14(var11, 11500.0, 75000.0);
         double var14 = var6 < 12500000L ? var6 / 2000.0 : 0.0;
         long var16 = (long)Math.max(8000.0, var13 - var14);
         this.timestamp19 = now;
         this.timestamp18 = now + var16 * 1000L;
         if (precision) {
            this.index10 = mc.player.age;
         }

          RotationStorage.update(
             new Rotation(this.computeTremorYaw(this.volume, precision), this.computeTremorPitch(this.volume2, precision)),
             360.0F,
             360.0F,
             30.0F,
             22.0F,
             2,
             1,
             this.precision8
          );
         float var18 = Math.abs(MathHelper.wrapDegrees(this.volume - mc.player.getYaw()));
         float var19 = Math.abs(this.volume2 - mc.player.getPitch());
         if (var18 > 4.0F || var19 > 4.0F) {
            this.updateState12(now, true);
         }
      }
   }

   private boolean checkCondition2(float totalError, boolean precision, boolean reaction) {
      int var4 = mc.player.age;
      if (var4 == this.index4) {
         return this.flag;
      }

      this.index4 = var4;
      if (this.index21 >= 6) {
         boolean var12 = this.flag;
         this.flag = false;
         this.index6 = Math.max(3, this.resolveInt());
         this.index5 = this.index6 - 1;
         this.index9 = var12 ? var4 : Integer.MIN_VALUE;
         this.volume36 = 1.0F;
         if (var12) {
            this.updateState8();
         }

         return false;
      } else {
         float var5 = (float)Math.hypot(this.boost9, this.boost10);
         float var6 = (float)Math.hypot(this.boost5, this.boost6);
         float var7 = (float)Math.hypot(MathHelper.wrapDegrees(this.volume - mc.player.getYaw()), this.volume2 - mc.player.getPitch());
         boolean var8 = !precision && !reaction && totalError < 1.6F && var5 < 38.0F && var6 < 50.0F && var7 < 0.6F;
         boolean var9 = precision && !reaction && totalError < 0.48F && var5 < 18.0F && var6 < 26.0F && var7 < 0.22F;
         boolean var10 = var8 || var9;
         if (!var10) {
            boolean var11 = this.flag;
            this.flag = false;
            this.index6 = Math.max(3, this.resolveInt());
            this.index5 = this.index6 - 1;
            this.index9 = var11 ? var4 : Integer.MIN_VALUE;
            this.volume36 = var11 ? (precision ? this.computefloat9(0.76F, 0.9F) : this.computefloat9(0.58F, 0.82F)) : 1.0F;
            if (var11) {
               this.updateState8();
            }

            return false;
         } else {
            if (precision && !this.flag && this.index5 > 1) {
               this.index5 = this.random.nextBoolean() ? 0 : 1;
            }

            if (this.index5 > 0) {
               this.index5--;
               return this.flag;
            }

            if (this.flag) {
               this.index7 = this.index6;
               this.flag = false;
               this.index6 = this.resolveInt();
               if (this.index7 == 1 && this.index8 == 1 && this.index6 == 1) {
                  this.index6 = 2;
               }

               this.index5 = this.index6 - 1;
               this.index9 = var4;
               this.volume36 = precision ? this.computefloat9(0.82F, 0.95F) : this.computefloat9(0.58F, 0.82F);
               this.updateState8();
               return false;
            } else {
               this.index8 = this.index6;
               this.flag = true;
               this.index6 = precision ? 1 : this.resolveInt2();
               if (this.index7 == 1 && this.index8 == 1 && this.index6 == 1) {
                  this.index6 = 2;
               }

               this.index5 = this.index6 - 1;
               return true;
            }
         }
      }
   }

   private int resolveInt() {
      int var1 = (int)Math.round(Math.exp(1.05 + this.random.nextGaussian() * 0.52));
      if (this.random.nextDouble() < 0.08) {
         var1 += this.random.nextInt(4, 13);
      }

      return MathHelper.clamp(var1, 2, 18);
   }

   private int resolveInt2() {
      double var1 = this.random.nextDouble();
      return var1 < 0.55 ? 1 : (var1 < 0.9 ? 2 : 3);
   }

   private void updateState8() {
      this.timestamp18 = 0L;
      this.timestamp19 = 0L;
      this.timestamp20 = 0L;
   }

   private void updateState9(float wantedYaw, float wantedPitch, double deltaTime) {
      if (mc.player.age != this.index20) {
         this.index20 = mc.player.age;
         this.index21++;
      }

      this.boost11 = wantedYaw;
      this.boost12 = wantedPitch;
      this.boost13 = true;
      this.boost5 = this.computefloat10(this.boost5, 0.0F, this.volume32 * (float)deltaTime);
      this.boost6 = this.computefloat10(this.boost6, 0.0F, this.volume33 * (float)deltaTime);
      this.boost7 = this.computefloat10(this.boost7, 0.0F, this.volume34 * (float)deltaTime);
      this.boost8 = this.computefloat10(this.boost8, 0.0F, this.volume35 * (float)deltaTime);
      this.updateState11();
   }

   private boolean checkCondition3(long now, boolean precision, boolean reaction) {
      int var5 = mc.player.age;
      if (var5 == this.index17) {
         return this.flag8;
      } else {
         this.index17 = var5;
         if (this.index21 >= 6) {
            this.index18 = 0;
            this.flag8 = false;
            return false;
         } else if (this.index18 > 0) {
            this.index18--;
            this.flag8 = true;
            return true;
         } else {
            boolean var6 = this.precision7 == SlothRotation.PathModel.EMPIRICAL && this.flag5
               || this.volume44 != 0.0F && now >= this.timestamp21 && now < this.timestamp22;
            if (!precision && !reaction && !var6 && this.volume48 < 1.15F && this.random.nextDouble() < 0.09) {
               this.index18 = this.random.nextDouble() < 0.75 ? 0 : 1;
               this.flag8 = true;
               return true;
            } else {
               this.flag8 = false;
               return false;
            }
         }
      }
   }

   private void updateState10(boolean precision) {
      int var2 = mc.player.age;
      if (var2 != this.index19) {
         this.index19 = var2;
         float var3 = precision ? 0.16F : 0.55F;
         this.volume46 = MathHelper.clamp((float)Math.exp(this.random.nextGaussian() * var3), 0.35F, 2.3F);
         this.volume47 = MathHelper.clamp((float)Math.exp(this.random.nextGaussian() * var3 * 0.9F), 0.35F, 2.15F);
      }
   }

    private void updateState11() {
       RotationStorage.update(
          new Rotation(this.computeTremorYaw(mc.player.getYaw(), this.precision6), this.computeTremorPitch(mc.player.getPitch(), this.precision6)),
          360.0F,
          360.0F,
          30.0F,
          22.0F,
          2,
          1,
          this.precision8
       );
    }

    private float computeTremorYaw(float value, boolean precision) {
       if (!precision && this.random.nextFloat() < 0.6F) {
          float step = GCDUtil.getGCDValue();
          return value + (this.random.nextBoolean() ? 1.0F : -1.0F) * step * (this.random.nextFloat() < 0.85F ? 1.0F : 2.0F);
       }

       return value;
    }

    private float computeTremorPitch(float value, boolean precision) {
       if (!precision && this.random.nextFloat() < 0.6F) {
          float step = GCDUtil.getGCDValue();
          return value + (this.random.nextBoolean() ? 1.0F : -1.0F) * step * (this.random.nextFloat() < 0.85F ? 1.0F : 2.0F);
       }

       return value;
    }

   private void updateState12(long now, boolean clearVelocity) {
      if (mc.player != null) {
         this.volume = MathHelper.wrapDegrees(mc.player.getYaw());
         this.volume2 = MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F);
      }

      if (clearVelocity) {
         this.boost5 = 0.0F;
         this.boost6 = 0.0F;
         this.boost7 = 0.0F;
         this.boost8 = 0.0F;
         this.boost9 = 0.0F;
         this.boost10 = 0.0F;
         this.boost13 = false;
         this.volume37 = 0.0F;
         this.volume38 = 0.0F;
         this.volume39 = 0.0F;
         this.volume40 = 0.0F;
         this.precision2 = 0.0F;
         this.precision3 = 0.0F;
      }

      this.timestamp = now;
   }

   private float computefloat2(LivingEntity target) {
      Vec3d var2 = target.getBoundingBox().getCenter().subtract(mc.player.getEyePos());
      float var3 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0);
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, var2.horizontalLength())));
      return Math.abs(MathHelper.wrapDegrees(var3 - mc.player.getYaw())) + Math.abs(var4 - mc.player.getPitch());
   }

   private void updateState13(long now, boolean precision, boolean action) {
      boolean var5 = this.flag6;
      this.flag6 = false;
      this.volume39 = this.volume37;
      this.volume40 = this.volume38;
      this.boost20 = this.boost14;
      this.boost21 = this.boost15;
      this.boost22 = this.boost16;
      Vec3d var6 = this.computeVec3d3(precision);
      this.boost17 = var6.x;
      this.boost18 = var6.y;
      this.boost19 = var6.z;
      if (var5 && this.random.nextDouble() < 0.6) {
         this.precision7 = SlothRotation.PathModel.EMPIRICAL;
         if (this.pathModel == SlothRotation.PathModel.EMPIRICAL) {
            this.index15++;
         } else {
            this.pathModel = SlothRotation.PathModel.EMPIRICAL;
            this.index15 = 1;
         }
      } else {
         this.precision7 = this.computePathModel(action);
      }

      if (this.precision7 == SlothRotation.PathModel.EMPIRICAL) {
         this.flag4 = action;
         this.flag5 = var5;
         this.index16 = var5 ? SlothDatasetProfile.chooseJump(this.random) : SlothDatasetProfile.choose(this.random, action);
      } else {
         this.flag4 = false;
         this.flag5 = false;
      }

      this.boost38 = this.resolveDouble13(-0.16, 0.2);
      this.boost39 = this.resolveDouble13(1.82, 2.48);
      this.boost40 = this.resolveDouble13(1.78, 2.42);
      this.boost41 = this.resolveDouble13(2.45, 3.25);
      this.volume12 = SlothDatasetProfile.pathNoiseScale(this.random);
      double var7 = SlothDatasetProfile.pathCurve(this.random, precision);
      this.boost35 = this.resolveDouble12(var7);
      this.boost36 = this.resolveDouble12(var7 * 0.78);
      this.boost37 = this.resolveDouble12(var7);
      if (this.precision7 == SlothRotation.PathModel.EMPIRICAL) {
         this.boost35 = 0.0;
         this.boost36 = 0.0;
         this.boost37 = 0.0;
         this.updateState16();
      } else {
         this.volume41 = 0.0F;
         this.volume42 = 0.0F;
         this.volume43 = 0.0F;
      }

      this.boost23 = this.resolveDouble14(
         this.resolveDouble11(this.boost20, this.boost17, 0.28) + this.resolveDouble12(var7), 0.14, 0.86
      );
      this.boost24 = this.resolveDouble14(
         this.resolveDouble11(this.boost21, this.boost18, 0.28) + this.resolveDouble12(var7), 0.24, 0.9
      );
      this.boost25 = this.resolveDouble14(
         this.resolveDouble11(this.boost22, this.boost19, 0.28) + this.resolveDouble12(var7), 0.14, 0.86
      );
      double var9 = this.resolveDouble13(0.36, 0.67);
      this.boost29 = this.resolveDouble14(
         this.resolveDouble11(this.boost20, this.boost17, var9) + this.boost35, 0.14, 0.86
      );
      this.boost30 = this.resolveDouble14(
         this.resolveDouble11(this.boost21, this.boost18, var9) + this.boost36, 0.24, 0.9
      );
      this.boost31 = this.resolveDouble14(
         this.resolveDouble11(this.boost22, this.boost19, var9) + this.boost37, 0.14, 0.86
      );
      this.boost26 = this.resolveDouble14(this.boost29 + this.boost29 - this.boost23, 0.14, 0.86);
      this.boost27 = this.resolveDouble14(this.boost30 + this.boost30 - this.boost24, 0.24, 0.9);
      this.boost28 = this.resolveDouble14(this.boost31 + this.boost31 - this.boost25, 0.14, 0.86);
      double var11 = this.precision7 == SlothRotation.PathModel.FLICK_SETTLE
         ? this.resolveDouble13(0.025, 0.085)
         : this.resolveDouble13(0.008, 0.045);
      this.boost32 = this.resolveDouble14(
         this.boost17 + (this.boost17 - this.boost20) * var11 + this.resolveDouble12(var7 * 0.32), 0.16, 0.84
      );
      this.boost33 = this.resolveDouble14(
         this.boost18 + (this.boost18 - this.boost21) * var11 + this.resolveDouble12(var7 * 0.22), 0.27, 0.88
      );
      this.boost34 = this.resolveDouble14(
         this.boost19 + (this.boost19 - this.boost22) * var11 + this.resolveDouble12(var7 * 0.32), 0.16, 0.84
      );
      if (this.precision7 == SlothRotation.PathModel.EMPIRICAL) {
         this.boost42 = 0.75;
         this.boost43 = 0.58;
         this.volume17 = this.computefloat9(0.9F, 1.12F);
      } else if (this.precision7 == SlothRotation.PathModel.FLICK_SETTLE) {
         this.boost42 = this.resolveDouble13(0.42, 0.6);
         this.boost43 = this.resolveDouble13(0.31, 0.48);
         this.volume17 = this.computefloat9(1.3F, 1.72F);
      } else if (this.precision7 == SlothRotation.PathModel.CORRECTIVE) {
         this.boost42 = this.resolveDouble13(0.7, 0.86);
         this.boost43 = this.resolveDouble13(0.52, 0.69);
         this.volume17 = this.computefloat9(0.78F, 1.24F);
      } else {
         this.boost42 = 0.75;
         this.boost43 = 0.58;
         this.volume17 = this.computefloat9(0.74F, 1.3F);
      }

      double var13 = this.resolveDouble4(
         this.boost20, this.boost21, this.boost22, this.boost17, this.boost18, this.boost19
      );
      double var15 = this.resolveDouble14(0.72 + var13 * 0.9, 0.78, 1.28);
      long var17;
      if (this.precision7 == SlothRotation.PathModel.EMPIRICAL) {
         long var19 = this.flag5
            ? SlothDatasetProfile.jumpDuration(this.index16)
            : SlothDatasetProfile.duration(this.flag4, this.index16);
         var17 = (long)(var19 * var15 * this.resolveDouble13(0.9, 1.12));
      } else {
         long var21;
         long var25;
         if (this.precision7 == SlothRotation.PathModel.FLICK_SETTLE) {
            var25 = 85L;
            var21 = 190L;
         } else if (this.precision7 == SlothRotation.PathModel.RANDOM_SPLINE) {
            var25 = 170L;
            var21 = 390L;
         } else if (this.precision7 == SlothRotation.PathModel.CORRECTIVE) {
            var25 = 145L;
            var21 = 350L;
         } else {
            var25 = 110L;
            var21 = 290L;
         }

         var17 = (long)(this.resolveDouble13(var25, var21) * var15);
      }

      if (precision && this.precision7 != SlothRotation.PathModel.EMPIRICAL) {
         var17 = Math.min(var17, action ? 245L : 310L);
      }

      double var26 = var17 / 1000.0;
      this.boost44 = this.resolveDouble14(this.level5 * var26 * 0.24, -0.12, 0.12);
      this.boost45 = this.resolveDouble14(this.level6 * var26 * 0.24, -0.09, 0.09);
      this.level4 = this.resolveDouble14(this.level7 * var26 * 0.24, -0.12, 0.12);
      this.timestamp4 = now;
      this.timestamp5 = now + var17 * 1000000L;
      double var27 = this.flag5 ? 1.0 : (precision ? (action ? 0.9 : 0.78) : (action ? 0.93 : 0.84));
      if (this.random.nextDouble() < var27) {
         double var23 = this.precision7 == SlothRotation.PathModel.EMPIRICAL
            ? (
               this.flag5
                  ? this.resolveDouble13(0.92, 0.985)
                  : (action ? this.resolveDouble13(0.78, 0.94) : this.resolveDouble13(0.76, 0.93))
            )
            : (action ? this.resolveDouble13(0.55, 0.84) : this.resolveDouble13(0.62, 0.89));
         this.timestamp3 = this.timestamp4 + (long)((this.timestamp5 - this.timestamp4) * var23);
      } else {
         long var28 = this.random.nextDouble() < 0.08
            ? this.resolveLong(140L, 620L)
            : (precision ? this.resolveLong(17L, 88L) : this.resolveLong(20L, 125L));
         this.timestamp3 = this.timestamp5 + var28;
      }
   }

   private void updateState14(long now, boolean precision) {
      if (now - this.timestamp17 >= 30000000L) {
         this.timestamp17 = now;
         this.updateState17(now);
         this.updateState13(now, precision, true);
      }
   }

   private void updateState15(long now, boolean precision) {
      this.updateState17(now);
      this.flag6 = true;
      this.flag7 = true;
      this.updateState13(now, precision, false);
      this.timestamp21 = now;
      this.timestamp22 = now + this.resolveLong(435L, 525L);
      double var4 = this.random.nextDouble();
      if (var4 < 0.52) {
         this.volume44 = this.computefloat9(6.0F, 18.0F);
      } else if (var4 < 0.75) {
         this.volume44 = this.computefloat9(1.5F, 6.0F);
      } else if (var4 < 0.9) {
         this.volume44 = 0.0F;
      } else {
         this.volume44 = -this.computefloat9(1.5F, 6.0F);
      }
   }

   private float computefloat3(long now) {
      if (this.timestamp22 > this.timestamp21 && now > this.timestamp21 && now < this.timestamp22 && this.volume44 != 0.0F) {
         double var3 = (double)(now - this.timestamp21) / (this.timestamp22 - this.timestamp21);
         double var5;
         if (var3 < 0.2) {
            var5 = this.resolveDouble9(var3 / 0.2) * 0.58;
         } else if (var3 < 0.44) {
            var5 = this.resolveDouble11(0.58, 1.0, this.resolveDouble9((var3 - 0.2) / 0.24));
         } else if (var3 < 0.63) {
            var5 = this.resolveDouble11(1.0, 0.84, this.resolveDouble9((var3 - 0.44) / 0.19));
         } else if (var3 < 0.92) {
            var5 = this.resolveDouble11(0.84, 0.0, this.resolveDouble9((var3 - 0.63) / 0.29));
         } else {
            var5 = 0.0;
         }

         return -this.volume44 * (float)var5;
      } else {
         return 0.0F;
      }
   }

   private void updateState16() {
      Vec3d var1 = this.computeVec3d2(this.boost20, this.boost21, this.boost22);
      Vec3d var2 = this.computeVec3d2(this.boost17, this.boost18, this.boost19);
      float var3 = MathHelper.wrapDegrees((float)(var2.x - var1.x));
      float var4 = (float)(var2.y - var1.y);
      float var5 = (float)Math.hypot(var3, var4);
      if (var5 < 0.001F) {
         this.volume41 = 0.0F;
         this.volume42 = 0.0F;
         this.volume43 = 0.0F;
      } else {
         this.volume41 = -var4 / var5;
         this.volume42 = var3 / var5;
         this.volume43 = Math.min(var5, 18.0F);
      }
   }

   private Vec3d computeVec3d2(double x, double y, double z) {
      if (this.boost2 != null && mc.player != null) {
         Box var7 = this.boost2.getBoundingBox();
         Vec3d var8 = new Vec3d(var7.minX + var7.getLengthX() * x, var7.minY + var7.getLengthY() * y, var7.minZ + var7.getLengthZ() * z);
         Vec3d var9 = var8.subtract(mc.player.getEyePos());
         double var10 = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var9.z, var9.x)) - 90.0);
         double var12 = -Math.toDegrees(Math.atan2(var9.y, var9.horizontalLength()));
         return new Vec3d(var10, var12, 0.0);
      } else {
         return Vec3d.ZERO;
      }
   }

   private void updateState17(long now) {
      if (this.timestamp5 > this.timestamp4) {
         double var3 = this.resolveDouble14((double)(now - this.timestamp4) / (this.timestamp5 - this.timestamp4), 0.0, 1.0);
         double var5 = this.boost14;
         double var7 = this.boost15;
         double var9 = this.boost16;
         double var11 = this.resolveDouble14(var3 + this.boost38 * var3 * (1.0 - var3), 0.0, 1.0);
         if (this.precision7 == SlothRotation.PathModel.EMPIRICAL) {
            double var13 = this.flag5 ? this.resolveDouble11(var3, var11, 0.38) : var11;
            double var15 = this.flag5
               ? SlothDatasetProfile.jumpProgress(this.index16, var13)
               : SlothDatasetProfile.progress(this.flag4, this.index16, var13);
            this.boost14 = this.resolveDouble11(this.boost20, this.boost17, var15);
            this.boost15 = this.resolveDouble11(this.boost21, this.boost18, var15);
            this.boost16 = this.resolveDouble11(this.boost22, this.boost19, var15);
            double var17 = this.flag5
               ? SlothDatasetProfile.jumpLateral(this.index16, var13)
               : SlothDatasetProfile.lateral(this.flag4, this.index16, var13);
            float var19 = (float)(1.0 - this.resolveDouble9(var13));
            this.volume37 = this.volume39 * var19 + this.volume41 * this.volume43 * (float)var17;
            this.volume38 = this.volume40 * var19 + this.volume42 * this.volume43 * (float)var17;
         } else if (this.precision7 == SlothRotation.PathModel.RANDOM_SPLINE) {
            double var20 = this.resolveDouble7(var11);
            if (var20 < 0.5) {
               double var24 = var20 * 2.0;
               this.boost14 = this.resolveDouble10(this.boost20, this.boost23, this.boost29, var24);
               this.boost15 = this.resolveDouble10(this.boost21, this.boost24, this.boost30, var24);
               this.boost16 = this.resolveDouble10(this.boost22, this.boost25, this.boost31, var24);
            } else {
               double var25 = (var20 - 0.5) * 2.0;
               this.boost14 = this.resolveDouble10(this.boost29, this.boost26, this.boost17, var25);
               this.boost15 = this.resolveDouble10(this.boost30, this.boost27, this.boost18, var25);
               this.boost16 = this.resolveDouble10(this.boost31, this.boost28, this.boost19, var25);
            }
         } else if (this.precision7 == SlothRotation.PathModel.FLICK_SETTLE) {
            this.boost14 = this.resolveDouble6(this.boost20, this.boost32, this.boost17, var11);
            this.boost15 = this.resolveDouble6(this.boost21, this.boost33, this.boost18, var11);
            this.boost16 = this.resolveDouble6(this.boost22, this.boost34, this.boost19, var11);
         } else if (this.precision7 == SlothRotation.PathModel.CORRECTIVE) {
            this.boost14 = this.resolveDouble5(this.boost20, this.boost32, this.boost17, var11);
            this.boost15 = this.resolveDouble5(this.boost21, this.boost33, this.boost18, var11);
            this.boost16 = this.resolveDouble5(this.boost22, this.boost34, this.boost19, var11);
         } else {
            double var21 = this.resolveDouble9(var11);
            double var26 = 16.0 * var21 * var21 * (1.0 - var21) * (1.0 - var21);
            this.boost14 = this.resolveDouble11(this.boost20, this.boost17, var21) + this.boost35 * var26;
            this.boost15 = this.resolveDouble11(this.boost21, this.boost18, var21) + this.boost36 * var26;
            this.boost16 = this.resolveDouble11(this.boost22, this.boost19, var21) + this.boost37 * var26;
         }

         if (this.precision7 != SlothRotation.PathModel.EMPIRICAL) {
            float var22 = (float)(1.0 - this.resolveDouble9(var3));
            this.volume37 = this.volume39 * var22;
            this.volume38 = this.volume40 * var22;
         }

         double var23 = 4.0 * var3 * Math.pow(1.0 - var3, 3.0);
         this.boost14 = this.boost14 + this.boost44 * var23;
         this.boost15 = this.boost15 + this.boost45 * var23;
         this.boost16 = this.boost16 + this.level4 * var23;
         this.boost14 = this.resolveDouble14(this.boost14, 0.12, 0.88);
         this.boost15 = this.resolveDouble14(this.boost15, 0.22, 0.92);
         this.boost16 = this.resolveDouble14(this.boost16, 0.12, 0.88);
         if (this.timestamp6 > 0L && now > this.timestamp6) {
            double var27 = (now - this.timestamp6) / 1.0E9;
            if (var27 <= 0.1) {
               double var28 = 1.0 - Math.exp(-var27 / 0.045);
               this.level5 = this.level5 + ((this.boost14 - var5) / var27 - this.level5) * var28;
               this.level6 = this.level6 + ((this.boost15 - var7) / var27 - this.level6) * var28;
               this.level7 = this.level7 + ((this.boost16 - var9) / var27 - this.level7) * var28;
            }
         }

         this.timestamp6 = now;
      }
   }

   private void updateState18(long now, double deltaTime) {
      Vec3d var5 = mc.player.getVelocity();
      boolean var6 = mc.player.isOnGround() && var5.x * var5.x + var5.z * var5.z < 0.0025;
      boolean var7 = this.checkCondition4(now);
      if (var6 && !var7) {
         if (this.timestamp9 > 0L && now >= this.timestamp9) {
            this.volume9 = 0.0F;
            this.volume10 = 0.0F;
            this.timestamp9 = 0L;
            this.timestamp8 = now + this.resolveLong(200L, 700L);
         }

         if (this.timestamp9 == 0L && now >= this.timestamp8) {
            float var8 = Math.max(0.001F, GCDUtil.getGCDValue());
            double var9 = this.random.nextDouble();
            int var11 = var9 < 0.72 ? 1 : (var9 < 0.96 ? 2 : 3);
            double var12 = this.random.nextDouble();
            int var14 = var12 < 0.6 ? 0 : (var12 < 0.92 ? 1 : 2);
            float var15 = this.random.nextBoolean() ? 1.0F : -1.0F;
            float var16 = this.random.nextBoolean() ? 1.0F : -1.0F;
            this.volume9 = var11 * var8 * var15;
            this.volume10 = var14 * var8 * var16;
            this.timestamp9 = now + this.resolveLong(24L, 70L);
         }
      } else {
         this.volume9 = 0.0F;
         this.volume10 = 0.0F;
         this.timestamp9 = 0L;
         if (now >= this.timestamp8) {
            this.timestamp8 = now + this.resolveLong(240L, 800L);
         }
      }

      boolean var17 = this.timestamp9 > now;
      double var18 = 1.0 - Math.exp(-deltaTime / (var17 ? 0.018 : 0.035));
      this.volume7 = this.volume7 + (this.volume9 - this.volume7) * (float)var18;
      this.volume8 = this.volume8 + (this.volume10 - this.volume8) * (float)var18;
      float var19 = var6 && !var7 ? 1.0F : 0.0F;
      double var20 = 1.0 - Math.exp(-deltaTime / (var19 > this.volume11 ? 0.08 : 0.045));
      this.volume11 = this.volume11 + (var19 - this.volume11) * (float)var20;
      double var21 = 1.0 - Math.exp(-deltaTime / (var7 ? 0.12 : 0.3));
      float var22 = var7 ? this.volume12 : 0.1F;
      this.volume13 = this.volume13 + (var22 - this.volume13) * (float)var21;
   }

   private boolean checkCondition4(long now) {
      double var3 = Math.sqrt(
         this.level5 * this.level5 + this.level6 * this.level6 + this.level7 * this.level7
      );
      double var5 = Math.hypot(this.boost5, this.boost6);
      return now < this.timestamp5 && var3 > 0.12 || var5 > 24.0;
   }

   private void updateState19(long now, boolean precision, float yawMargin, float pitchMargin) {
      if (now >= this.timestamp12 && now >= this.timestamp14) {
         if (precision) {
            this.timestamp12 = now + this.resolveLong(700L, 1800L);
         } else if (this.volume48 > 1.2F) {
            this.timestamp12 = now + this.resolveLong(150L, 320L);
         } else {
            float var6 = Math.min(yawMargin + MathHelper.clamp(yawMargin * this.computefloat9(0.25F, 1.1F), 1.5F, 8.0F), 24.0F);
            float var7 = Math.min(pitchMargin * 0.5F + MathHelper.clamp(pitchMargin * this.computefloat9(0.35F, 0.8F), 1.2F, 6.0F), 14.0F);
            float var8 = this.random.nextBoolean() ? 1.0F : -1.0F;
            float var9 = this.random.nextBoolean() ? 1.0F : -1.0F;
            if (this.boost2 != null && mc.player != null) {
               Vec3d var10 = this.boost2.getBoundingBox().getCenter().subtract(mc.player.getEyePos());
               float var11 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var10.z, var10.x)) - 90.0);
               float var12 = (float)(-Math.toDegrees(Math.atan2(var10.y, var10.horizontalLength())));
               float var13 = MathHelper.wrapDegrees(this.volume - var11);
               float var14 = this.volume2 - var12;
               if (Math.abs(var13) > 0.15F) {
                  var8 = Math.signum(var13);
               }

               if (Math.abs(var14) > 0.15F) {
                  var9 = Math.signum(var14);
               }
            }

            double var15 = this.random.nextDouble();
            if (var15 < 0.46) {
               this.volume22 = var8 * var6;
               this.volume23 = this.computefloat8(0.08F, 0.24F) * var7;
            } else if (var15 < 0.72) {
               this.volume22 = this.computefloat8(0.12F, 0.34F) * var6;
               this.volume23 = var9 * var7;
            } else {
               this.volume22 = var8 * var6 * this.computefloat9(0.66F, 0.96F);
               this.volume23 = var9 * var7 * this.computefloat9(0.42F, 0.76F);
            }

            this.volume26 = this.computefloat9(0.28F, 0.45F);
            this.volume27 = this.computefloat9(2.0F, 2.8F);
            this.timestamp13 = now;
            this.timestamp14 = now + this.resolveLong(240L, 480L);
            this.timestamp12 = this.timestamp14 + this.resolveLong(480L, 1500L);
         }
      }
   }

   private void updateState20(long now) {
      float var3 = this.computefloat4(now);
      if (var3 <= 0.0F) {
         this.timestamp13 = 0L;
         this.timestamp14 = 0L;
         this.volume22 = 0.0F;
         this.volume23 = 0.0F;
      } else {
         this.volume22 *= var3;
         this.volume23 *= var3;
         this.volume26 = 0.0F;
         this.timestamp13 = now;
         this.timestamp14 = now + this.resolveLong(70L, 105L);
      }

      this.timestamp12 = now + this.resolveLong(900L, 2400L);
   }

   private float computefloat4(long now) {
      if (this.timestamp14 > this.timestamp13 && now >= this.timestamp13 && now < this.timestamp14) {
         double var3 = this.resolveDouble14((double)(now - this.timestamp13) / (this.timestamp14 - this.timestamp13), 0.0, 1.0);
         if (var3 < this.volume26) {
            double var7 = var3 / this.volume26;
            return (float)(1.0 - Math.pow(1.0 - var7, this.volume27));
         } else {
            double var5 = (var3 - this.volume26) / (1.0 - this.volume26);
            return (float)(1.0 - this.resolveDouble9(var5));
         }
      } else {
         return 0.0F;
      }
   }

   private SlothRotation.PathModel computePathModel(boolean action) {
      SlothRotation.PathModel var2 = this.pathModel;

      for (int var3 = 0; var3 < 8; var3++) {
         double var4 = this.random.nextDouble();
         if (action) {
            var2 = var4 < 0.5
               ? SlothRotation.PathModel.EMPIRICAL
               : (
                  var4 < 0.68
                     ? SlothRotation.PathModel.MINIMUM_JERK
                     : (var4 < 0.85 ? SlothRotation.PathModel.CORRECTIVE : SlothRotation.PathModel.RANDOM_SPLINE)
               );
         } else {
            var2 = var4 < 0.48
               ? SlothRotation.PathModel.EMPIRICAL
               : (
                  var4 < 0.69
                     ? SlothRotation.PathModel.MINIMUM_JERK
                     : (var4 < 0.86 ? SlothRotation.PathModel.CORRECTIVE : SlothRotation.PathModel.RANDOM_SPLINE)
               );
         }

         if ((var2 != SlothRotation.PathModel.FLICK_SETTLE || this.pathModel != SlothRotation.PathModel.FLICK_SETTLE)
            && (var2 != this.pathModel || this.index15 < 2 || var2 == SlothRotation.PathModel.EMPIRICAL)) {
            break;
         }
      }

      if (var2 == this.pathModel) {
         this.index15++;
      } else {
         this.pathModel = var2;
         this.index15 = 1;
      }

      return var2;
   }

   private Vec3d computeVec3d3(boolean precision) {
      int var2 = precision ? 24 : 28;
      Vec3d[] var3 = new Vec3d[var2];
      int[] var4 = new int[var2];
      double[] var5 = new double[var2];
      double[] var6 = new double[var2];
      double[] var7 = new double[var2];
      double var8 = precision ? 0.05 : 0.07;
      double var10 = precision ? 0.02 : 0.025;

      for (int var12 = 0; var12 < var2; var12++) {
         Vec3d var13 = this.computeVec3d4(precision);
         int var14 = this.resolveInt4(var13);
         double var15 = this.index11 == 0
            ? this.resolveDouble3(var13.x, var13.y, var13.z, this.boost14, this.boost15, this.boost16)
            : Double.MAX_VALUE;
         double var17 = this.index11 == 0
            ? this.resolveDouble4(var13.x, var13.y, var13.z, this.boost14, this.boost15, this.boost16)
            : Double.MAX_VALUE;
         double var19 = 0.0;

         for (int var21 = 0; var21 < this.index11; var21++) {
            int var22 = (this.index12 - 1 - var21 + 4) % 4;
            var15 = Math.min(
               var15,
               this.resolveDouble3(var13.x, var13.y, var13.z, this.level[var22], this.level2[var22], this.level3[var22])
            );
            var17 = Math.min(
               var17,
               this.resolveDouble4(var13.x, var13.y, var13.z, this.level[var22], this.level2[var22], this.level3[var22])
            );
            if (this.index[var22] == var14) {
               var19 += 0.025 / (var21 + 1.0);
            }
         }

         double var27 = this.resolveDouble3(var13.x, var13.y, var13.z, this.boost14, this.boost15, this.boost16);
         double var23 = var15 * 0.22 + var17 * 0.35 + var27 * 0.06 + this.random.nextDouble() * 0.75 - var19;
         if (var14 == this.index13) {
            var23 -= 0.08;
         }

         if (var14 == this.index14) {
            var23 -= 0.035;
         }

         var3[var12] = var13;
         var4[var12] = var14;
         var5[var12] = var23;
         var6[var12] = var15;
         var7[var12] = var17;
      }

      int var25 = this.resolveInt3(var5, var6, var7, var8, var10);
      if (var25 < 0) {
         var25 = this.resolveInt3(var5, var6, var7, var8 * 0.55, var10 * 0.55);
      }

      if (var25 < 0) {
         var25 = this.resolveInt3(var5, var6, var7, 0.018, 0.008);
      }

      if (var25 < 0) {
         return new Vec3d(this.boost14, this.boost15, this.boost16);
      }

      Vec3d var26 = var3[var25];
      this.updateState21(var26, var4[var25]);
      return var26;
   }

   private int resolveInt3(
      double[] scores, double[] screenDistances, double[] normalizedDistances, double minimumScreenDistance, double minimumNormalizedDistance
   ) {
      double var8 = 2.0;
      double var10 = -Double.MAX_VALUE;

      for (int var12 = 0; var12 < scores.length; var12++) {
         if (!(screenDistances[var12] < minimumScreenDistance) && !(normalizedDistances[var12] < minimumNormalizedDistance)) {
            var10 = Math.max(var10, scores[var12]);
         }
      }

      if (var10 == -Double.MAX_VALUE) {
         return -1;
      }

      double var17 = 0.0;

      for (int var14 = 0; var14 < scores.length; var14++) {
         if (!(screenDistances[var14] < minimumScreenDistance) && !(normalizedDistances[var14] < minimumNormalizedDistance) && !(scores[var14] < var10 - var8)) {
            var17 += 0.3 + Math.exp((scores[var14] - var10) * 0.6);
         }
      }

      if (var17 <= 0.0) {
         return -1;
      }

      double var18 = this.random.nextDouble() * var17;

      for (int var16 = 0; var16 < scores.length; var16++) {
         if (!(screenDistances[var16] < minimumScreenDistance) && !(normalizedDistances[var16] < minimumNormalizedDistance) && !(scores[var16] < var10 - var8)) {
            var18 -= 0.3 + Math.exp((scores[var16] - var10) * 0.6);
            if (var18 <= 0.0) {
               return var16;
            }
         }
      }

      return -1;
   }

   private Vec3d computeVec3d4(boolean precision) {
      double var2 = this.random.nextDouble();
      double var4;
      double var6;
      double var8;
      if (var2 < 0.7) {
         double var10 = this.random.nextGaussian();
         double var12 = this.random.nextGaussian();
         double var14 = this.boost14 * 0.68 + 0.16;
         double var16 = this.boost15 * 0.62 + 0.2622;
         double var18 = this.boost16 * 0.68 + 0.16;
         double var20 = precision ? 0.075 : 0.11;
         var4 = var14 + var10 * var20;
         var8 = var18 + var12 * var20;
         var6 = var16 + this.random.nextGaussian() * (precision ? 0.075 : 0.11);
      } else if (var2 < 0.92) {
         double var25 = this.random.nextGaussian();
         double var27 = this.random.nextGaussian();
         var4 = 0.5 + var25 * (precision ? 0.14 : 0.19);
         var8 = 0.5 + var27 * (precision ? 0.14 : 0.19);
         var6 = 0.66 + this.random.nextGaussian() * (precision ? 0.12 : 0.17);
      } else {
         double var26 = this.random.nextDouble() * Math.PI * 2.0;
         double var28 = precision ? this.resolveDouble13(0.2, 0.34) : this.resolveDouble13(0.24, 0.4);
         var4 = 0.5 + Math.cos(var26) * var28;
         var8 = 0.5 + Math.sin(var26) * var28;
         var6 = this.resolveDouble13(precision ? 0.42 : 0.32, precision ? 0.88 : 0.92);
      }

      if (precision) {
         var4 = this.resolveDouble2(var4, 0.16, 0.84);
         var6 = this.resolveDouble2(var6, 0.36, 0.9);
         var8 = this.resolveDouble2(var8, 0.16, 0.84);
      } else {
         var4 = this.resolveDouble2(var4, 0.12, 0.88);
         var6 = this.resolveDouble2(var6, 0.29, 0.92);
         var8 = this.resolveDouble2(var8, 0.12, 0.88);
      }

      return new Vec3d(var4, var6, var8);
   }

   private double resolveDouble2(double value, double minimum, double maximum) {
      double var7 = value;

      for (int var9 = 0; var9 < 3 && (var7 < minimum || var7 > maximum); var9++) {
         var7 = var7 < minimum ? minimum + (minimum - var7) : maximum - (var7 - maximum);
      }

      return this.resolveDouble14(var7, minimum, maximum);
   }

   private int resolveInt4(Vec3d point) {
      int var2 = point.y < 0.49 ? 0 : (point.y < 0.68 ? 1 : 2);
      double var3 = Math.atan2(point.z - 0.5, point.x - 0.5);
      if (var3 < 0.0) {
         var3 += Math.PI * 2;
      }

      int var5 = Math.min(7, (int)(var3 / (Math.PI / 4)));
      return var2 * 8 + var5;
   }

   private void updateState21(Vec3d point, int zone) {
      this.level[this.index12] = point.x;
      this.level2[this.index12] = point.y;
      this.level3[this.index12] = point.z;
      this.index[this.index12] = zone;
      this.index12 = (this.index12 + 1) % 4;
      this.index11 = Math.min(this.index11 + 1, 4);
      this.index14 = this.index13;
      this.index13 = zone;
   }

   private void updateState22() {
      this.index11 = 0;
      this.index12 = 0;
      this.index13 = -1;
      this.index14 = -1;

      for (int var1 = 0; var1 < 4; var1++) {
         this.level[var1] = 0.0;
         this.level2[var1] = 0.0;
         this.level3[var1] = 0.0;
         this.index[var1] = -1;
      }
   }

   private double resolveDouble3(double firstX, double firstY, double firstZ, double secondX, double secondY, double secondZ) {
      if (this.boost2 != null && mc.player != null) {
         Box var13 = this.boost2.getBoundingBox();
         Vec3d var14 = mc.player.getEyePos();
         double var15 = var13.minX + var13.getLengthX() * firstX - var14.x;
         double var17 = var13.minY + var13.getLengthY() * firstY - var14.y;
         double var19 = var13.minZ + var13.getLengthZ() * firstZ - var14.z;
         double var21 = var13.minX + var13.getLengthX() * secondX - var14.x;
         double var23 = var13.minY + var13.getLengthY() * secondY - var14.y;
         double var25 = var13.minZ + var13.getLengthZ() * secondZ - var14.z;
         float var27 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var19, var15)) - 90.0);
         float var28 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var25, var21)) - 90.0);
         float var29 = (float)(-Math.toDegrees(Math.atan2(var17, Math.hypot(var15, var19))));
         float var30 = (float)(-Math.toDegrees(Math.atan2(var23, Math.hypot(var21, var25))));
         double var31 = MathHelper.wrapDegrees(var27 - var28);
         double var33 = var29 - var30;
         return Math.hypot(var31, var33);
      } else {
         return this.resolveDouble4(firstX, firstY, firstZ, secondX, secondY, secondZ);
      }
   }

   private double resolveDouble4(double firstX, double firstY, double firstZ, double secondX, double secondY, double secondZ) {
      double var13 = firstX - secondX;
      double var15 = (firstY - secondY) * 1.25;
      double var17 = firstZ - secondZ;
      return Math.sqrt(var13 * var13 + var15 * var15 + var17 * var17);
   }

   private double resolveDouble5(double start, double overshoot, double end, double progress) {
      double var9 = this.resolveDouble8(this.resolveDouble14(progress / this.boost42, 0.0, 1.0));
      double var11 = this.resolveDouble7(this.resolveDouble14((progress - this.boost43) / (1.0 - this.boost43), 0.0, 1.0));
      return this.resolveDouble11(start, overshoot, var9) + (end - overshoot) * var11;
   }

   private double resolveDouble6(double start, double overshoot, double end, double progress) {
      double var9 = this.resolveDouble14(progress / this.boost42, 0.0, 1.0);
      double var11 = 1.0 - Math.pow(1.0 - var9, this.boost41);
      double var13 = this.resolveDouble9(this.resolveDouble14((progress - this.boost43) / (1.0 - this.boost43), 0.0, 1.0));
      return this.resolveDouble11(start, overshoot, var11) + (end - overshoot) * var13;
   }

   private double resolveDouble7(double progress) {
      if (progress <= 0.0) {
         return 0.0;
      }

      if (progress >= 1.0) {
         return 1.0;
      }

      double var3 = Math.pow(progress, this.boost39);
      double var5 = Math.pow(1.0 - progress, this.boost40);
      return var3 / (var3 + var5);
   }

   private double resolveDouble8(double progress) {
      return progress - Math.sin((Math.PI * 2) * progress) / (Math.PI * 2);
   }

   private double resolveDouble9(double progress) {
      double var3 = progress * progress;
      double var5 = var3 * progress;
      return 10.0 * var5 - 15.0 * var5 * progress + 6.0 * var5 * var3;
   }

   private double resolveDouble10(double start, double control, double end, double progress) {
      double var9 = 1.0 - progress;
      return var9 * var9 * start + 2.0 * var9 * progress * control + progress * progress * end;
   }

   private double resolveDouble11(double start, double end, double progress) {
      return start + (end - start) * progress;
   }

   private double resolveDouble12(double magnitude) {
      return this.resolveDouble13(-magnitude, magnitude);
   }

   private float computefloat5(LivingEntity target, double distance) {
      Box var4 = target.getBoundingBox();
      double var5 = Math.max(var4.getLengthX(), var4.getLengthZ()) * 0.5;
      return (float)Math.toDegrees(Math.atan2(var5, Math.max(0.35, distance)));
   }

   private float computefloat6(LivingEntity target, double distance) {
      double var4 = target.getBoundingBox().getLengthY() * 0.5;
      return (float)Math.toDegrees(Math.atan2(var4, Math.max(0.35, distance)));
   }

   private float computefloat7() {
      return MathHelper.clamp((float)this.random.nextGaussian() * 0.48F, -1.0F, 1.0F);
   }

   private float computefloat8(float minimum, float maximum) {
      return (this.random.nextBoolean() ? 1.0F : -1.0F) * this.computefloat9(minimum, maximum);
   }

   private float computefloat9(float minimum, float maximum) {
      return minimum + this.random.nextFloat() * (maximum - minimum);
   }

   private double resolveDouble13(double minimum, double maximum) {
      return minimum + this.random.nextDouble() * (maximum - minimum);
   }

   private long resolveLong(long minimumMillis, long maximumMillis) {
      long var5 = minimumMillis + (long)(this.random.nextDouble() * (maximumMillis - minimumMillis));
      return var5 * 1000000L;
   }

   private float computefloat10(float current, float target, float maximumChange) {
      return current < target ? Math.min(current + maximumChange, target) : Math.max(current - maximumChange, target);
   }

   private double resolveDouble14(double value, double minimum, double maximum) {
      return Math.max(minimum, Math.min(maximum, value));
   }

   private enum AimPhase {
      REACTION,
      ACQUIRE,
      TRACK,
      FOCUS,
      RECOVER;
   }

   private enum PathModel {
      EMPIRICAL,
      MINIMUM_JERK,
      RANDOM_SPLINE,
      FLICK_SETTLE,
      CORRECTIVE;
   }
}
