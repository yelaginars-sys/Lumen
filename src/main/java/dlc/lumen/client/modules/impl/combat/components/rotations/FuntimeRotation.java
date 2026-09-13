package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FuntimeRotation extends RotationsSystem implements QClient {
   private static final long TIMESTAMP = 500000L;
   private static final long TIMESTAMP2 = 200000000L;
   private static final long TIMESTAMP3 = 120000000L;
   private static final long TIMESTAMP4 = 30000000L;
   private static final double LEVEL = 0.05;
   private static final int INDEX = 1;
   private static final int INDEX2 = 12;
   private static final int INDEX3 = 20150;
   private static final float VOLUME = 0.8F;
   private static final float VOLUME2 = 1.2F;
   private static final float VOLUME3 = 1.5F;
   private static final float VOLUME4 = 60.0F;
   private static final float VOLUME5 = 24.0F;
   private static final float VOLUME6 = 0.55F;
   private static final float VOLUME7 = 1.25F;
   private final Random random = new Random();
   private final double[] level = new double[12];
   private final double[] level2 = new double[12];
   private final double[] level3 = new double[12];
   private final int[] index = new int[12];
   private LivingEntity livingEntity;
   private FuntimeRotation.AimPhase precision2 = FuntimeRotation.AimPhase.REACTION;
   private Vec3d vec3d = Vec3d.ZERO;
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4;
   private float volume5;
   private float volume6;
   private float volume7;
   private float volume8;
   private float volume9;
   private float volume10;
   private boolean flag;
   private double level4 = 0.5;
   private double level5 = 0.62;
   private double level6 = 0.5;
   private double level7 = 0.5;
   private double level8 = 0.62;
   private double level9 = 0.5;
   private double level10 = 0.5;
   private double level11 = 0.62;
   private double level12 = 0.5;
   private double level13 = 0.5;
   private double level14 = 0.62;
   private double level15 = 0.5;
   private double level16 = 0.5;
   private double level17 = 0.62;
   private double level18 = 0.5;
   private double level19 = 0.5;
   private double level20 = 0.62;
   private double level21 = 0.5;
   private double level22 = 0.5;
   private double level23 = 0.62;
   private double level24 = 0.5;
   private double level25;
   private double level26;
   private double level27;
   private double level28;
   private double level29 = 2.0;
   private double level30 = 2.0;
   private double level31 = 2.8;
   private double level32 = 0.75;
   private double level33 = 0.58;
   private double level34;
   private double level35;
   private double level36;
   private double level37;
   private double level38;
   private double level39;
   private float volume11;
   private float volume12;
   private float volume13;
   private float volume14;
   private float precision3;
   private float precision4;
   private float volume15;
   private float volume16;
   private float volume17;
   private float volume18;
   private float volume19;
   private float volume20 = 0.06F;
   private float volume21 = 0.1F;
   private float volume22 = 1.0F;
   private float volume23 = 1.0F;
   private float volume24 = 1.0F;
   private float volume25 = 1.0F;
   private float volume26 = 1.0F;
   private float volume27 = 1.0F;
   private float volume28;
   private float volume29;
   private float volume30;
   private float volume31;
   private float volume32 = 1.0F;
   private float volume33 = 1.0F;
   private float volume34 = 0.28F;
   private float volume35 = 2.4F;
   private float volume36;
   private float volume37;
   private float volume38;
   private float volume39;
   private float volume40;
   private float volume41;
   private float volume42;
   private float volume43;
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
   private long precision5;
   private long timestamp17;
   private long timestamp18;
   private long timestamp19;
   private long timestamp20;
   private long timestamp21;
   private long timestamp22;
   private int index2 = 20150;
   private int index3 = 20150;
   private boolean precision6;
   private boolean visible2;
   private boolean flag2;
   private int index4;
   private int index5;
   private int index6 = -1;
   private int index7 = -1;
   private int index8;
   private FuntimeRotation.PathModel precision7 = FuntimeRotation.PathModel.MINIMUM_JERK;
   private FuntimeRotation.PathModel pathModel = FuntimeRotation.PathModel.MINIMUM_JERK;
   private FuntimeRotation.PathEase pathEase = FuntimeRotation.PathEase.MINIMUM_JERK;
   private FuntimeRotation.PathEase pathEase2 = FuntimeRotation.PathEase.MINIMUM_JERK;
   private int index9;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private float volume44;
   private float volume45;
   private float volume46;
   private float volume47;
   private float volume48;
   private float volume49;
   private float volume50;
   private long timestamp23;
   private long timestamp24;
   private float volume51;
   private float volume52 = 1.0F;
   private boolean precision8;

   public void reset() {
      this.livingEntity = null;
      this.precision2 = FuntimeRotation.AimPhase.REACTION;
      this.vec3d = Vec3d.ZERO;
      this.volume3 = 0.0F;
      this.volume4 = 0.0F;
      this.volume5 = 0.0F;
      this.volume6 = 0.0F;
      this.volume7 = 0.0F;
      this.volume8 = 0.0F;
      this.volume9 = 0.0F;
      this.volume10 = 0.0F;
      this.flag = false;
      this.level4 = this.level7 = 0.5;
      this.level5 = this.level8 = 0.62;
      this.level6 = this.level9 = 0.5;
      this.level10 = this.level13 = this.level16 = 0.5;
      this.level11 = this.level14 = this.level17 = 0.62;
      this.level12 = this.level15 = this.level18 = 0.5;
      this.level19 = this.level22 = 0.5;
      this.level20 = this.level23 = 0.62;
      this.level21 = this.level24 = 0.5;
      this.level25 = this.level26 = this.level27 = 0.0;
      this.level28 = 0.0;
      this.level29 = this.level30 = 2.0;
      this.level31 = 2.8;
      this.level32 = 0.75;
      this.level33 = 0.58;
      this.level34 = this.level35 = this.level36 = 0.0;
      this.level37 = this.level38 = this.level39 = 0.0;
      this.volume11 = this.volume12 = 0.0F;
      this.volume13 = this.volume14 = 0.0F;
      this.precision3 = this.precision4 = 0.0F;
      this.volume15 = this.volume16 = 0.0F;
      this.volume17 = this.volume18 = 0.0F;
      this.volume19 = 0.0F;
      this.volume20 = 0.06F;
      this.volume21 = 0.1F;
      this.volume22 = this.volume23 = 1.0F;
      this.volume24 = this.volume25 = 1.0F;
      this.volume26 = this.volume27 = 1.0F;
      this.volume28 = this.volume29 = 0.0F;
      this.volume30 = this.volume31 = 0.0F;
      this.volume32 = this.volume33 = 1.0F;
      this.volume34 = 0.28F;
      this.volume35 = 2.4F;
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
      this.precision5 = 0L;
      this.timestamp17 = 0L;
      this.timestamp18 = 0L;
      this.timestamp19 = 0L;
      this.timestamp20 = 0L;
      this.timestamp21 = 0L;
      this.timestamp22 = 0L;
      this.index2 = 20150;
      this.index3 = 20150;
      this.precision6 = false;
      this.visible2 = false;
      this.flag2 = mc.player != null && !mc.player.isOnGround();
      this.updateState16();
      this.index8 = 0;
      this.precision7 = FuntimeRotation.PathModel.MINIMUM_JERK;
      this.pathModel = FuntimeRotation.PathModel.MINIMUM_JERK;
      this.pathEase = FuntimeRotation.PathEase.MINIMUM_JERK;
      this.pathEase2 = FuntimeRotation.PathEase.MINIMUM_JERK;
      this.index9 = 0;
      this.flag3 = false;
      this.flag4 = false;
      this.flag5 = false;
      this.flag6 = false;
      this.volume44 = 0.0F;
      this.volume45 = 0.0F;
      this.volume46 = 0.0F;
      this.volume47 = 0.0F;
      this.volume48 = 0.0F;
      this.volume49 = 0.0F;
      this.volume50 = 0.0F;
      this.timestamp23 = 0L;
      this.timestamp24 = 0L;
      this.volume51 = 0.0F;
      this.volume52 = 1.0F;
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
      if (precision && !this.precision6 && this.livingEntity != null) {
         this.updateState9(var2, true);
      }

      this.precision6 = precision;
      if (precision) {
         this.precision5 = Math.max(this.precision5, var2 + 120000000L);
      }
   }

   public void setVisibleAim(boolean visible) {
      this.precision8 = visible;
   }

   public void onAttack() {
      if (mc.player != null && this.livingEntity != null) {
         long var1 = System.nanoTime();
         this.precision5 = Math.max(this.precision5, var1 + 100000000L);
         this.precision3 = this.computefloat8(0.04F, 0.13F);
         double var3 = this.random.nextDouble();
         this.precision4 = var3 < 0.63 ? this.computefloat9(0.12F, 0.42F) : (var3 < 0.83 ? -this.computefloat9(0.08F, 0.28F) : 0.0F);
         boolean var5 = this.precision7 == FuntimeRotation.PathModel.EMPIRICAL
            && this.flag3
            && var1 >= this.timestamp4
            && var1 < this.timestamp5;
         if (!var5) {
            this.updateState9(var1, true);
         }

         this.precision2 = FuntimeRotation.AimPhase.RECOVER;
      }
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         long var2 = System.nanoTime();
         RotationStorage var4 = RotationStorage.instance;
         if (var4 != null && var4.currentPriority() > 1) {
            this.updateState7(var2, true);
            this.visible2 = true;
         } else {
            if (this.livingEntity != target) {
               this.updateState(target, var2);
            }

            if (this.visible2) {
               this.updateState7(var2, true);
               this.visible2 = false;
            } else {
               long var5 = this.timestamp == 0L ? 4166667L : var2 - this.timestamp;
               if (var5 >= 500000L) {
                  if (var5 > 200000000L) {
                     this.updateState7(var2, true);
                     this.precision2 = FuntimeRotation.AimPhase.ACQUIRE;
                  } else {
                     this.timestamp = var2;
                     double var7 = Math.min(var5 / 1.0E9, 0.05);
                     boolean var9 = this.precision6 || var2 < this.precision5;
                     boolean var10 = this.checkCondition3(var2, var9 || var2 < this.timestamp2);
                     boolean var11 = !mc.player.isOnGround();
                     if (var11 && !this.flag2 && mc.player.getVelocity().y > 0.035) {
                        this.updateState10(var2, var9);
                     }

                     if (var11 && this.flag6 && mc.player.getVelocity().y < -0.07) {
                        this.flag6 = false;
                        this.updateState9(var2, var9);
                     } else if (!var11) {
                        this.flag6 = false;
                     }

                     this.flag2 = var11;
                     this.updateState2(var2, var7, var9);
                     Vec3d var12 = this.computeVec3d(target, var7, var9);
                     Vec3d var13 = var12.subtract(mc.player.getEyePos());
                     float var14 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var13.z, var13.x)) - 90.0);
                     float var15 = (float)(-Math.toDegrees(Math.atan2(var13.y, var13.horizontalLength())));
                     float var16 = this.computefloat5(target, var13.length());
                     float var17 = this.computefloat6(target, var13.length());
                     float var18 = MathHelper.clamp(this.volume44, -var16 * (var9 ? 0.272F : 0.85F), var16 * (var9 ? 0.272F : 0.85F));
                     float var19 = MathHelper.clamp(this.volume45, -var17 * (var9 ? 0.224F : 0.72F), var17 * (var9 ? 0.224F : 0.72F));
                     float var20 = MathHelper.clamp(this.computefloat3(var2), -var17 * (var9 ? 0.176F : 0.78F), var17 * (var9 ? 0.176F : 0.78F));
                     this.updateState14(var2, var9, var16, var17);
                     float var21 = this.computefloat4(var2) * (var9 ? 0.14400001F : 1.0F);
                     float var22 = (var9 ? 0.176F : (this.precision2 == FuntimeRotation.AimPhase.ACQUIRE ? 0.35F : 1.0F)) * this.volume21;
                     float var23 = MathHelper.clamp(var16 * 0.14F, 0.035F, 0.28F) * var22;
                     float var24 = MathHelper.clamp(var16 * 0.08F, 0.025F, 0.18F) * var22;
                     this.precision3 = this.precision3 * (float)Math.exp(-var7 / 0.075);
                     this.precision4 = this.precision4 * (float)Math.exp(-var7 / 0.09);
                     float var25 = var9 ? 0.52F : 1.0F;
                     var14 = MathHelper.wrapDegrees(
                        var14
                           + var18
                           + this.volume11 * var23
                           + this.volume15 * this.volume19 * var25
                           + this.volume30 * var21
                           + this.precision3
                     );
                     var15 = MathHelper.clamp(
                        var15
                           + var19
                           + var20
                           + this.volume12 * var24
                           + this.volume16 * this.volume19 * var25
                           + this.volume31 * var21
                           + this.precision4,
                        -89.0F,
                        89.0F
                     );
                     this.updateState5(var14, var15, var7);
                     float var26 = MathHelper.wrapDegrees(var14 - this.volume);
                     float var27 = var15 - this.volume2;
                     float var28 = (float)Math.hypot(var26, var27);
                     if (var2 < this.timestamp2 && !var9) {
                        this.precision2 = FuntimeRotation.AimPhase.REACTION;
                        this.volume3 = this.computefloat10(this.volume3, 0.0F, this.volume40 * (float)var7);
                        this.volume4 = this.computefloat10(this.volume4, 0.0F, this.volume41 * (float)var7);
                     } else {
                        if (!var9) {
                           this.precision2 = var28 > 10.0F ? FuntimeRotation.AimPhase.ACQUIRE : FuntimeRotation.AimPhase.TRACK;
                        } else {
                           this.precision2 = var2 >= this.precision5
                                 || !(Math.abs(this.precision3) > 0.01F) && !(Math.abs(this.precision4) > 0.01F)
                              ? FuntimeRotation.AimPhase.FOCUS
                              : FuntimeRotation.AimPhase.RECOVER;
                        }

                        this.updateState4(var26, var27, var7, var9, mc.player.isGliding());
                     }

                     if (var10) {
                        this.volume3 = 0.0F;
                        this.volume4 = 0.0F;
                        this.volume5 = 0.0F;
                        this.volume6 = 0.0F;
                     }

                     float var29 = (float)Math.min(1.5, 1.0 + mc.player.getVelocity().horizontalLength() * 0.5);
                     if (!mc.player.isOnGround()) {
                        var29 *= MathHelper.clamp(1.0F + mc.player.fallDistance * 0.015F, 1.0F, 1.25F);
                     }

                     double var30 = this.timestamp5 > this.timestamp4
                        ? this.resolveDouble12((double)(var2 - this.timestamp4) / (this.timestamp5 - this.timestamp4), 0.0, 1.0)
                        : 0.5;
                     float var32 = (float)this.resolveDouble9(0.55F, 1.25, this.pathEase.evaluate(var30, this)) * var29;
                     float var33 = var10 ? 0.0F : this.volume3 * (float)var7 * var32;
                     float var34 = var10 ? 0.0F : this.volume4 * (float)var7 * var32;
                     if (var33 * var26 > 0.0F && Math.abs(var33) > Math.abs(var26)) {
                        if (Math.abs(this.volume7) > 8.0F) {
                           var33 = var26 + MathHelper.clamp(this.volume7 * (float)var7 * 0.58F, -0.45F, 0.45F);
                           this.volume3 = this.volume7 * 0.58F;
                           this.volume5 = 0.0F;
                        } else {
                           var33 = var26;
                           this.volume3 = 0.0F;
                           this.volume5 = 0.0F;
                        }
                     }

                     if (var34 * var27 > 0.0F && Math.abs(var34) > Math.abs(var27)) {
                        if (Math.abs(this.volume8) > 6.0F) {
                           var34 = var27 + MathHelper.clamp(this.volume8 * (float)var7 * 0.52F, -0.32F, 0.32F);
                           this.volume4 = this.volume8 * 0.52F;
                           this.volume6 = 0.0F;
                        } else {
                           var34 = var27;
                           this.volume4 = 0.0F;
                           this.volume6 = 0.0F;
                        }
                     }

                     var33 = Math.min(Math.abs(var33), 60.0F) * Math.signum(var33);
                     var34 = Math.min(Math.abs(var34), 24.0F) * Math.signum(var34);
                     this.volume = MathHelper.wrapDegrees(this.volume + var33);
                     this.volume2 = MathHelper.clamp(this.volume2 + var34, -89.0F, 89.0F);
                     if (Float.isFinite(this.volume) && Float.isFinite(this.volume2)) {
                        this.updateState6(var2);
                     } else {
                        this.updateState7(var2, true);
                     }
                  }
               }
            }
         }
      }
   }

   private void updateState(LivingEntity target, long now) {
      this.livingEntity = target;
      this.updateState7(now, true);
      this.timestamp20 = now;
      this.timestamp21 = 0L;
      this.timestamp22 = 0L;
      this.index2 = 20150;
      this.index3 = 20150;
      this.vec3d = target.getVelocity();
      this.level4 = this.level7 = this.resolveDouble11(0.4, 0.6);
      this.level5 = this.level8 = this.resolveDouble11(0.64, 0.82);
      this.level6 = this.level9 = this.resolveDouble11(0.4, 0.6);
      this.level10 = this.level13 = this.level16 = this.level4;
      this.level11 = this.level14 = this.level17 = this.level5;
      this.level12 = this.level15 = this.level18 = this.level6;
      this.level19 = this.level22 = this.level4;
      this.level20 = this.level23 = this.level5;
      this.level21 = this.level24 = this.level6;
      this.updateState16();
      this.volume11 = this.volume12 = 0.0F;
      this.volume13 = this.volume14 = 0.0F;
      this.precision3 = this.precision4 = 0.0F;
      this.volume15 = this.volume16 = 0.0F;
      this.volume17 = this.volume18 = 0.0F;
      this.volume19 = 0.0F;
      this.volume20 = this.computefloat9(0.03F, 0.09F);
      this.volume21 = 0.1F;
      this.volume22 = this.volume23 = this.computefloat9(0.86F, 1.16F);
      this.volume24 = this.volume25 = 1.0F;
      this.volume26 = this.volume27 = this.computefloat9(0.72F, 1.12F);
      this.volume28 = this.volume29 = this.computefloat9(-0.12F, 0.08F);
      this.volume30 = this.volume31 = 0.0F;
      this.volume32 = this.volume33 = 1.0F;
      this.volume34 = 0.28F;
      this.volume35 = 2.4F;
      this.level37 = this.level38 = this.level39 = 0.0;
      this.timestamp6 = now;
      this.volume36 = this.computefloat9(610.0F, 830.0F);
      this.volume37 = this.computefloat9(280.0F, 410.0F);
      this.volume38 = this.computefloat9(2800.0F, 5200.0F);
      this.volume39 = this.computefloat9(1200.0F, 2400.0F);
      this.volume40 = this.computefloat9(3600.0F, 6500.0F);
      this.volume41 = this.computefloat9(1800.0F, 3400.0F);
      this.volume42 = this.computefloat9(36000.0F, 72000.0F);
      this.volume43 = this.computefloat9(16000.0F, 36000.0F);
      float var4 = this.computefloat2(target);
      long var5;
      if (!this.precision6 && !(var4 < 6.0F)) {
         var5 = Math.round(MathHelper.clamp(18.0F + var4 * 0.43F + (float)this.random.nextGaussian() * 7.0F, 16.0F, 105.0F));
      } else {
         var5 = Math.round(this.computefloat9(0.0F, 12.0F));
      }

      this.timestamp2 = now + var5 * 1000000L;
      this.updateState8(now, this.precision6, false);
      this.timestamp7 = now + this.resolveLong(260L, 720L);
      this.timestamp8 = now + this.resolveLong(480L, 1200L);
      this.timestamp10 = now + this.resolveLong(150L, 480L);
      this.timestamp11 = now + this.resolveLong(320L, 920L);
      this.timestamp12 = now + this.resolveLong(650L, 1900L);
      this.timestamp15 = now + this.resolveLong(650L, 1900L);
      this.timestamp18 = now + this.resolveLong(850L, 2600L);
      this.timestamp19 = 0L;
      this.flag2 = !mc.player.isOnGround();
      this.precision2 = var5 > 0L ? FuntimeRotation.AimPhase.REACTION : FuntimeRotation.AimPhase.ACQUIRE;
   }

   private void updateState2(long now, double deltaTime, boolean precision) {
      this.updateState3(deltaTime);
      this.updateState12(now);
      if (now >= this.timestamp3) {
         this.updateState8(now, precision, false);
         this.updateState12(now);
      }

      if (now >= this.timestamp7) {
         float var6 = this.computefloat7();
         this.volume13 = MathHelper.clamp(this.computefloat7() * 0.78F + var6 * 0.22F, -1.0F, 1.0F);
         this.volume14 = MathHelper.clamp(this.computefloat7() * 0.82F + var6 * 0.18F, -1.0F, 1.0F);
         this.timestamp7 = now + this.resolveLong(260L, 760L);
      }

      double var18 = 1.0 - Math.exp(-deltaTime / 0.38);
      double var8 = 1.0 - Math.exp(-deltaTime / 0.48);
      this.volume11 = this.volume11 + (this.volume13 - this.volume11) * (float)var18;
      this.volume12 = this.volume12 + (this.volume14 - this.volume12) * (float)var8;
      if (now >= this.timestamp10) {
         this.volume23 = this.computefloat9(0.68F, 1.36F);
         this.timestamp10 = now + this.resolveLong(150L, 520L);
      }

      double var10 = 1.0 - Math.exp(-deltaTime / 0.13);
      this.volume22 = this.volume22 + (this.volume23 - this.volume22) * (float)var10;
      double var12 = 1.0 - Math.exp(-deltaTime / 0.09);
      this.volume24 = this.volume24 + (this.volume25 - this.volume24) * (float)var12;
      if (now >= this.timestamp11) {
         this.volume27 = this.computefloat9(0.48F, 1.24F);
         this.volume29 = this.computefloat9(-0.22F, 0.13F);
         this.timestamp11 = now + this.resolveLong(340L, 1150L);
      }

      double var14 = 1.0 - Math.exp(-deltaTime / 0.22);
      this.volume26 = this.volume26 + (this.volume27 - this.volume26) * (float)var14;
      this.volume28 = this.volume28 + (this.volume29 - this.volume28) * (float)var14;
      if (precision) {
         this.volume33 = 1.0F;
         if (this.timestamp16 > 0L) {
            this.timestamp16 = 0L;
            this.timestamp15 = now + this.resolveLong(700L, 2400L);
         }
      } else if (this.timestamp16 > 0L && now >= this.timestamp16) {
         this.volume33 = 1.0F;
         this.timestamp16 = 0L;
         this.timestamp15 = now + this.resolveLong(700L, 2400L);
      } else if (this.timestamp16 == 0L && now >= this.timestamp15) {
         this.volume33 = this.computefloat9(0.62F, 0.9F);
         this.timestamp16 = now + this.resolveLong(20L, 85L);
      }

      double var16 = 1.0 - Math.exp(-deltaTime / (this.volume33 < this.volume32 ? 0.022 : 0.045));
      this.volume32 = this.volume32 + (this.volume33 - this.volume32) * (float)var16;
      this.updateState13(now, deltaTime);
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
      this.volume52 = this.volume52 + (var3 - this.volume52) * var6;
   }

   private Vec3d computeVec3d(LivingEntity target, double deltaTime, boolean precision) {
      Vec3d var5 = target.getVelocity();
      if (var5.lengthSquared() > 2.25) {
         var5 = var5.normalize().multiply(1.5);
      }

      double var6 = 1.0 - Math.exp(-deltaTime / 0.12);
      this.vec3d = this.vec3d.lerp(var5, var6);
      Box var8 = target.getBoundingBox();
      Vec3d var9 = new Vec3d(
         var8.minX + var8.getLengthX() * this.level4,
         var8.minY + var8.getLengthY() * this.level5,
         var8.minZ + var8.getLengthZ() * this.level6
      );
      double var10 = mc.player.getEyePos().distanceTo(var9);
      double var12;
      if (precision) {
         var12 = 0.05;
      } else if (!mc.player.isGliding() && !target.isGliding()) {
         var12 = this.resolveDouble12((0.52 + var10 * 0.09) * this.volume26 + this.volume28, -0.2, 1.35);
      } else {
         var12 = this.resolveDouble12((0.95 + var10 * 0.08) * this.volume26 + this.volume28, 0.15, 1.85);
      }

      Vec3d var14 = var9.add(this.vec3d.multiply(var12));
      if (!precision) {
         return var14;
      }

      double var15 = Math.min(var8.getLengthX(), var8.getLengthZ()) * 0.12;
      double var17 = var8.getLengthY() * 0.1;
      return new Vec3d(
         MathHelper.clamp(var14.x, var8.minX + var15, var8.maxX - var15),
         MathHelper.clamp(var14.y, var8.minY + var17, var8.maxY - var17),
         MathHelper.clamp(var14.z, var8.minZ + var15, var8.maxZ - var15)
      );
   }

   private void updateState4(float yawError, float pitchError, double deltaTime, boolean precision, boolean flying) {
      float var7 = this.volume22 * this.volume24 * this.volume32 * this.volume52 * (flying ? 1.12F : 1.0F);
      if (precision) {
         var7 *= 1.272F;
      }

      float var8 = Math.abs(yawError);
      float var9 = Math.abs(pitchError);
      float var10 = flying ? 1100.0F : (var8 < 18.0F ? 520.0F : (var8 < 65.0F ? 720.0F : 980.0F));
      float var11 = flying ? 520.0F : (var9 < 8.0F ? 230.0F : (var9 < 24.0F ? 330.0F : 430.0F));
      float var12 = Math.min(var10, this.volume36 * var7);
      float var13 = Math.min(var11, this.volume37 * var7);
      float var14 = (float)Math.exp(-Math.abs(yawError) / 18.0F);
      float var15 = (float)Math.exp(-Math.abs(pitchError) / 13.0F);
      float var16 = this.computefloat(yawError, this.volume40, var12) + this.volume7 * (precision ? 0.86F : 0.7F) * var14;
      float var17 = this.computefloat(pitchError, this.volume41, var13) + this.volume8 * (precision ? 0.82F : 0.66F) * var15;
      var16 = MathHelper.clamp(var16, -var12, var12);
      var17 = MathHelper.clamp(var17, -var13, var13);
      float var18 = 1.0F + MathHelper.clamp((var8 - 35.0F) / 90.0F, 0.0F, 0.45F);
      float var19 = 1.0F + MathHelper.clamp((var9 - 18.0F) / 42.0F, 0.0F, 0.3F);
      float var20 = (this.checkCondition(this.volume3, var16) ? this.volume40 : this.volume38) * var18;
      float var21 = (this.checkCondition(this.volume4, var17) ? this.volume41 : this.volume39) * var19;
      float var22 = precision ? 1.3440001F : 1.0F;
      float var23 = precision ? 1.32F : 1.0F;
      float var24 = MathHelper.clamp((var16 - this.volume3) / (float)deltaTime, -var20 * var22, var20 * var22);
      float var25 = MathHelper.clamp((var17 - this.volume4) / (float)deltaTime, -var21 * var23, var21 * var23);
      this.volume5 = this.computefloat10(this.volume5, var24, this.volume42 * var22 * (float)Math.sqrt(var18) * (float)deltaTime);
      this.volume6 = this.computefloat10(this.volume6, var25, this.volume43 * var23 * (float)Math.sqrt(var19) * (float)deltaTime);
      float var26 = this.volume3 + this.volume5 * (float)deltaTime;
      float var27 = this.volume4 + this.volume6 * (float)deltaTime;
      if ((var16 - this.volume3) * (var16 - var26) <= 0.0F) {
         var26 = var16;
         this.volume5 *= 0.22F;
      }

      if ((var17 - this.volume4) * (var17 - var27) <= 0.0F) {
         var27 = var17;
         this.volume6 *= 0.22F;
      }

      this.volume3 = MathHelper.clamp(var26, -var12, var12);
      this.volume4 = MathHelper.clamp(var27, -var13, var13);
   }

   private void updateState5(float wantedYaw, float wantedPitch, double deltaTime) {
      if (!this.flag) {
         this.volume9 = wantedYaw;
         this.volume10 = wantedPitch;
         this.volume7 = 0.0F;
         this.volume8 = 0.0F;
         this.flag = true;
      } else {
         float var5 = MathHelper.wrapDegrees(wantedYaw - this.volume9) / (float)deltaTime;
         float var6 = (wantedPitch - this.volume10) / (float)deltaTime;
         var5 = MathHelper.clamp(var5, -1100.0F, 1100.0F);
         var6 = MathHelper.clamp(var6, -620.0F, 620.0F);
         float var7 = (float)(1.0 - Math.exp(-deltaTime / 0.055));
         this.volume7 = this.volume7 + (var5 - this.volume7) * var7;
         this.volume8 = this.volume8 + (var6 - this.volume8) * var7;
         this.volume9 = wantedYaw;
         this.volume10 = wantedPitch;
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

   private void updateState6(long now) {
      long var3 = this.timestamp22 == 0L ? Long.MAX_VALUE : now - this.timestamp22;
      this.timestamp22 = now;
      long var5 = this.timestamp21 == 0L ? Long.MAX_VALUE : now - this.timestamp21;
      if (var3 >= 12500000L || now >= this.timestamp20) {
         if (this.timestamp21 != 0L) {
            int var7 = (int)this.resolveDouble12(var5 / 1000.0, 12500.0, 35000.0);
            this.index3 = this.index2;
            this.index2 = var7;
         }

         int var17 = FuntimeMotorProfile.mouseReportIntervalMicros(this.random);
         double var8 = 20150.0
            - (this.index2 - 20150) * 0.19
            + (this.index3 - 20150) * 0.275
            + (var17 - 20150) * 0.95
            + this.resolveDouble11(-260.0, 260.0);
         int var10 = (int)this.resolveDouble12(var8, 11500.0, 35000.0);
         double var11 = var3 < 12500000L ? var3 / 2000.0 : 0.0;
         long var13 = (long)Math.max(8000.0, var10 - var11);
         this.timestamp21 = now;
         this.timestamp20 = now + var13 * 1000L;
         RotationStorage.update(new Rotation(this.volume, this.volume2), 360.0F, 360.0F, 45.0F, 45.0F, 0, 1, this.precision8);
         float var15 = Math.abs(MathHelper.wrapDegrees(this.volume - mc.player.getYaw()));
         float var16 = Math.abs(this.volume2 - mc.player.getPitch());
         if (var15 > 4.0F || var16 > 4.0F) {
            this.updateState7(now, true);
         }
      }
   }

   private void updateState7(long now, boolean clearVelocity) {
      if (mc.player != null) {
         this.volume = MathHelper.wrapDegrees(mc.player.getYaw());
         this.volume2 = MathHelper.clamp(mc.player.getPitch(), -89.0F, 89.0F);
      }

      if (clearVelocity) {
         this.volume3 = 0.0F;
         this.volume4 = 0.0F;
         this.volume5 = 0.0F;
         this.volume6 = 0.0F;
         this.volume7 = 0.0F;
         this.volume8 = 0.0F;
         this.flag = false;
         this.volume44 = 0.0F;
         this.volume45 = 0.0F;
         this.volume46 = 0.0F;
         this.volume47 = 0.0F;
      }

      this.timestamp = now;
   }

   private float computefloat2(LivingEntity target) {
      Vec3d var2 = target.getBoundingBox().getCenter().subtract(mc.player.getEyePos());
      float var3 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0);
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, var2.horizontalLength())));
      return Math.abs(MathHelper.wrapDegrees(var3 - mc.player.getYaw())) + Math.abs(var4 - mc.player.getPitch());
   }

   private void updateState8(long now, boolean precision, boolean action) {
      boolean var5 = this.flag5;
      this.flag5 = false;
      this.volume46 = this.volume44;
      this.volume47 = this.volume45;
      this.level10 = this.level4;
      this.level11 = this.level5;
      this.level12 = this.level6;
      Vec3d var6 = this.computeVec3d3(precision);
      this.level7 = var6.x;
      this.level8 = var6.y;
      this.level9 = var6.z;
      double var7 = this.resolveDouble(
         this.level10, this.level11, this.level12, this.level7, this.level8, this.level9
      );
      if (var5 && this.random.nextDouble() < 0.96) {
         this.precision7 = FuntimeRotation.PathModel.EMPIRICAL;
         if (this.pathModel == FuntimeRotation.PathModel.EMPIRICAL) {
            this.index8++;
         } else {
            this.pathModel = FuntimeRotation.PathModel.EMPIRICAL;
            this.index8 = 1;
         }
      } else {
         this.precision7 = this.computePathModel(action, var7);
      }

      if (this.precision7 == FuntimeRotation.PathModel.EMPIRICAL) {
         this.flag3 = action;
         this.flag4 = var5;
         this.index9 = var5 ? FuntimeMotorProfile.chooseJump(this.random) : FuntimeMotorProfile.choose(this.random, action);
      } else {
         this.flag3 = false;
         this.flag4 = false;
      }

      this.level28 = this.resolveDouble11(-0.28, 0.34);
      this.pathEase = this.computePathEase();
      this.level29 = this.resolveDouble11(1.65, 2.9);
      this.level30 = this.resolveDouble11(1.65, 2.9);
      this.level31 = this.resolveDouble11(2.15, 3.75);
      this.volume20 = this.computefloat9(0.03F, 0.09F);
      double var9 = precision ? this.resolveDouble11(0.025, 0.075) : this.resolveDouble11(0.045, 0.14);
      this.level25 = this.resolveDouble10(var9);
      this.level26 = this.resolveDouble10(var9 * 0.78);
      this.level27 = this.resolveDouble10(var9);
      if (this.precision7 == FuntimeRotation.PathModel.EMPIRICAL) {
         this.level25 = 0.0;
         this.level26 = 0.0;
         this.level27 = 0.0;
         this.updateState11();
      } else {
         this.volume48 = 0.0F;
         this.volume49 = 0.0F;
         this.volume50 = 0.0F;
      }

      this.level13 = this.resolveDouble12(
         this.resolveDouble9(this.level10, this.level7, 0.28) + this.resolveDouble10(var9), 0.14, 0.86
      );
      this.level14 = this.resolveDouble12(
         this.resolveDouble9(this.level11, this.level8, 0.28) + this.resolveDouble10(var9), 0.24, 0.9
      );
      this.level15 = this.resolveDouble12(
         this.resolveDouble9(this.level12, this.level9, 0.28) + this.resolveDouble10(var9), 0.14, 0.86
      );
      double var11 = this.resolveDouble11(0.36, 0.67);
      this.level19 = this.resolveDouble12(
         this.resolveDouble9(this.level10, this.level7, var11) + this.level25, 0.14, 0.86
      );
      this.level20 = this.resolveDouble12(
         this.resolveDouble9(this.level11, this.level8, var11) + this.level26, 0.24, 0.9
      );
      this.level21 = this.resolveDouble12(
         this.resolveDouble9(this.level12, this.level9, var11) + this.level27, 0.14, 0.86
      );
      this.level16 = this.resolveDouble12(this.level19 + this.level19 - this.level13, 0.14, 0.86);
      this.level17 = this.resolveDouble12(this.level20 + this.level20 - this.level14, 0.24, 0.9);
      this.level18 = this.resolveDouble12(this.level21 + this.level21 - this.level15, 0.14, 0.86);
      double var13 = this.precision7 == FuntimeRotation.PathModel.FLICK_SETTLE
         ? this.resolveDouble11(0.025, 0.085)
         : this.resolveDouble11(0.008, 0.045);
      this.level22 = this.resolveDouble12(
         this.level7 + (this.level7 - this.level10) * var13 + this.resolveDouble10(var9 * 0.32), 0.16, 0.84
      );
      this.level23 = this.resolveDouble12(
         this.level8 + (this.level8 - this.level11) * var13 + this.resolveDouble10(var9 * 0.22), 0.27, 0.88
      );
      this.level24 = this.resolveDouble12(
         this.level9 + (this.level9 - this.level12) * var13 + this.resolveDouble10(var9 * 0.32), 0.16, 0.84
      );
      if (this.precision7 == FuntimeRotation.PathModel.EMPIRICAL) {
         this.level32 = 0.75;
         this.level33 = 0.58;
         this.volume25 = this.computefloat9(0.9F, 1.12F);
      } else if (this.precision7 == FuntimeRotation.PathModel.FLICK_SETTLE) {
         this.level32 = this.resolveDouble11(0.42, 0.6);
         this.level33 = this.resolveDouble11(0.31, 0.48);
         this.volume25 = this.computefloat9(1.3F, 1.72F);
      } else if (this.precision7 == FuntimeRotation.PathModel.CORRECTIVE) {
         this.level32 = this.resolveDouble11(0.7, 0.86);
         this.level33 = this.resolveDouble11(0.52, 0.69);
         this.volume25 = this.computefloat9(0.78F, 1.24F);
      } else {
         this.level32 = 0.75;
         this.level33 = 0.58;
         this.volume25 = this.computefloat9(0.74F, 1.3F);
      }

      double var15 = this.resolveDouble2(
         this.level10, this.level11, this.level12, this.level7, this.level8, this.level9
      );
      double var17 = this.resolveDouble12(0.72 + var15 * 0.9, 0.78, 1.28);
      long var19;
      if (this.precision7 == FuntimeRotation.PathModel.EMPIRICAL) {
         long var21 = this.flag4
            ? FuntimeMotorProfile.jumpDuration(this.index9)
            : FuntimeMotorProfile.duration(this.flag3, this.index9);
         var19 = (long)(var21 * var17 * this.resolveDouble11(0.92, 1.08));
      } else {
         long var23;
         long var27;
         if (this.precision7 == FuntimeRotation.PathModel.FLICK_SETTLE) {
            var27 = 85L;
            var23 = 190L;
         } else if (this.precision7 == FuntimeRotation.PathModel.RANDOM_SPLINE) {
            var27 = 170L;
            var23 = 390L;
         } else if (this.precision7 == FuntimeRotation.PathModel.CORRECTIVE) {
            var27 = 145L;
            var23 = 350L;
         } else {
            var27 = 110L;
            var23 = 290L;
         }

         var19 = (long)(this.resolveDouble11(var27, var23) * var17);
      }

      if (precision && this.precision7 != FuntimeRotation.PathModel.EMPIRICAL) {
         var19 = Math.min(var19, action ? 245L : 310L);
      }

      double var28 = var19 / 1000.0;
      this.level34 = this.resolveDouble12(this.level37 * var28 * 0.24, -0.12, 0.12);
      this.level35 = this.resolveDouble12(this.level38 * var28 * 0.24, -0.09, 0.09);
      this.level36 = this.resolveDouble12(this.level39 * var28 * 0.24, -0.12, 0.12);
      this.timestamp4 = now;
      this.timestamp5 = now + var19 * 1000000L;
      double var29 = this.flag4 ? 1.0 : (precision ? (action ? 0.9 : 0.78) : (action ? 0.93 : 0.84));
      if (this.random.nextDouble() < var29) {
         double var25 = this.precision7 == FuntimeRotation.PathModel.EMPIRICAL
            ? (
               this.flag4
                  ? this.resolveDouble11(0.92, 0.985)
                  : (action ? this.resolveDouble11(0.78, 0.94) : this.resolveDouble11(0.76, 0.93))
            )
            : (action ? this.resolveDouble11(0.55, 0.84) : this.resolveDouble11(0.62, 0.89));
         this.timestamp3 = this.timestamp4 + (long)((this.timestamp5 - this.timestamp4) * var25);
      } else {
         long var30 = this.random.nextDouble() < 0.08
            ? this.resolveLong(140L, 620L)
            : (precision ? this.resolveLong(17L, 88L) : this.resolveLong(20L, 125L));
         this.timestamp3 = this.timestamp5 + var30;
      }
   }

   private void updateState9(long now, boolean precision) {
      if (now - this.timestamp17 >= 30000000L) {
         this.timestamp17 = now;
         this.updateState12(now);
         this.updateState8(now, precision, true);
      }
   }

   private void updateState10(long now, boolean precision) {
      this.updateState12(now);
      this.flag5 = true;
      this.flag6 = true;
      this.updateState8(now, precision, false);
      this.timestamp23 = now;
      this.timestamp24 = now + this.resolveLong(435L, 525L);
      double var4 = this.random.nextDouble();
      if (var4 < 0.52) {
         this.volume51 = this.computefloat9(6.0F, 18.0F);
      } else if (var4 < 0.75) {
         this.volume51 = this.computefloat9(1.5F, 6.0F);
      } else if (var4 < 0.9) {
         this.volume51 = 0.0F;
      } else {
         this.volume51 = -this.computefloat9(1.5F, 6.0F);
      }
   }

   private float computefloat3(long now) {
      if (this.timestamp24 > this.timestamp23 && now > this.timestamp23 && now < this.timestamp24 && this.volume51 != 0.0F) {
         double var3 = (double)(now - this.timestamp23) / (this.timestamp24 - this.timestamp23);
         double var5;
         if (var3 < 0.2) {
            var5 = this.resolveDouble7(var3 / 0.2) * 0.58;
         } else if (var3 < 0.44) {
            var5 = this.resolveDouble9(0.58, 1.0, this.resolveDouble7((var3 - 0.2) / 0.24));
         } else if (var3 < 0.63) {
            var5 = this.resolveDouble9(1.0, 0.84, this.resolveDouble7((var3 - 0.44) / 0.19));
         } else if (var3 < 0.92) {
            var5 = this.resolveDouble9(0.84, 0.0, this.resolveDouble7((var3 - 0.63) / 0.29));
         } else {
            var5 = 0.0;
         }

         return -this.volume51 * (float)var5;
      } else {
         return 0.0F;
      }
   }

   private void updateState11() {
      Vec3d var1 = this.computeVec3d2(this.level10, this.level11, this.level12);
      Vec3d var2 = this.computeVec3d2(this.level7, this.level8, this.level9);
      float var3 = MathHelper.wrapDegrees((float)(var2.x - var1.x));
      float var4 = (float)(var2.y - var1.y);
      float var5 = (float)Math.hypot(var3, var4);
      if (var5 < 0.001F) {
         this.volume48 = 0.0F;
         this.volume49 = 0.0F;
         this.volume50 = 0.0F;
      } else {
         this.volume48 = -var4 / var5;
         this.volume49 = var3 / var5;
         this.volume50 = Math.min(var5, 18.0F);
      }
   }

   private Vec3d computeVec3d2(double x, double y, double z) {
      if (this.livingEntity != null && mc.player != null) {
         Box var7 = this.livingEntity.getBoundingBox();
         Vec3d var8 = new Vec3d(var7.minX + var7.getLengthX() * x, var7.minY + var7.getLengthY() * y, var7.minZ + var7.getLengthZ() * z);
         Vec3d var9 = var8.subtract(mc.player.getEyePos());
         double var10 = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var9.z, var9.x)) - 90.0);
         double var12 = -Math.toDegrees(Math.atan2(var9.y, var9.horizontalLength()));
         return new Vec3d(var10, var12, 0.0);
      } else {
         return Vec3d.ZERO;
      }
   }

   private void updateState12(long now) {
      if (this.timestamp5 > this.timestamp4) {
         double var3 = this.resolveDouble12((double)(now - this.timestamp4) / (this.timestamp5 - this.timestamp4), 0.0, 1.0);
         double var5 = this.level4;
         double var7 = this.level5;
         double var9 = this.level6;
         double var11 = this.resolveDouble12(var3 + this.level28 * var3 * (1.0 - var3), 0.0, 1.0);
         if (this.precision7 == FuntimeRotation.PathModel.EMPIRICAL) {
            double var13 = this.pathEase.evaluate(var11, this);
            double var15 = this.flag4 ? this.resolveDouble9(var3, var13, 0.38) : var13;
            double var17 = this.flag4
               ? FuntimeMotorProfile.jumpProgress(this.index9, var15)
               : FuntimeMotorProfile.progress(this.flag3, this.index9, var15);
            this.level4 = this.resolveDouble9(this.level10, this.level7, var17);
            this.level5 = this.resolveDouble9(this.level11, this.level8, var17);
            this.level6 = this.resolveDouble9(this.level12, this.level9, var17);
            double var19 = this.flag4
               ? FuntimeMotorProfile.jumpLateral(this.index9, var15)
               : FuntimeMotorProfile.lateral(this.flag3, this.index9, var15);
            float var21 = (float)(1.0 - this.resolveDouble7(var15));
            this.volume44 = this.volume46 * var21 + this.volume48 * this.volume50 * (float)var19;
            this.volume45 = this.volume47 * var21 + this.volume49 * this.volume50 * (float)var19;
         } else if (this.precision7 == FuntimeRotation.PathModel.RANDOM_SPLINE) {
            double var22 = this.pathEase.evaluate(var11, this);
            if (var22 < 0.5) {
               double var26 = var22 * 2.0;
               this.level4 = this.resolveDouble8(this.level10, this.level13, this.level19, var26);
               this.level5 = this.resolveDouble8(this.level11, this.level14, this.level20, var26);
               this.level6 = this.resolveDouble8(this.level12, this.level15, this.level21, var26);
            } else {
               double var27 = (var22 - 0.5) * 2.0;
               this.level4 = this.resolveDouble8(this.level19, this.level16, this.level7, var27);
               this.level5 = this.resolveDouble8(this.level20, this.level17, this.level8, var27);
               this.level6 = this.resolveDouble8(this.level21, this.level18, this.level9, var27);
            }
         } else if (this.precision7 == FuntimeRotation.PathModel.FLICK_SETTLE) {
            this.level4 = this.resolveDouble4(this.level10, this.level22, this.level7, var11);
            this.level5 = this.resolveDouble4(this.level11, this.level23, this.level8, var11);
            this.level6 = this.resolveDouble4(this.level12, this.level24, this.level9, var11);
         } else if (this.precision7 == FuntimeRotation.PathModel.CORRECTIVE) {
            this.level4 = this.resolveDouble3(this.level10, this.level22, this.level7, var11);
            this.level5 = this.resolveDouble3(this.level11, this.level23, this.level8, var11);
            this.level6 = this.resolveDouble3(this.level12, this.level24, this.level9, var11);
         } else {
            double var23 = this.pathEase.evaluate(var11, this);
            double var28 = 16.0 * var23 * var23 * (1.0 - var23) * (1.0 - var23);
            this.level4 = this.resolveDouble9(this.level10, this.level7, var23) + this.level25 * var28;
            this.level5 = this.resolveDouble9(this.level11, this.level8, var23) + this.level26 * var28;
            this.level6 = this.resolveDouble9(this.level12, this.level9, var23) + this.level27 * var28;
         }

         if (this.precision7 != FuntimeRotation.PathModel.EMPIRICAL) {
            float var24 = (float)(1.0 - this.resolveDouble7(var3));
            this.volume44 = this.volume46 * var24;
            this.volume45 = this.volume47 * var24;
         }

         double var25 = 4.0 * var3 * Math.pow(1.0 - var3, 3.0);
         this.level4 = this.level4 + this.level34 * var25;
         this.level5 = this.level5 + this.level35 * var25;
         this.level6 = this.level6 + this.level36 * var25;
         this.level4 = this.resolveDouble12(this.level4, 0.12, 0.88);
         this.level5 = this.resolveDouble12(this.level5, 0.22, 0.92);
         this.level6 = this.resolveDouble12(this.level6, 0.12, 0.88);
         if (this.timestamp6 > 0L && now > this.timestamp6) {
            double var29 = (now - this.timestamp6) / 1.0E9;
            if (var29 <= 0.1) {
               double var30 = 1.0 - Math.exp(-var29 / 0.045);
               this.level37 = this.level37 + ((this.level4 - var5) / var29 - this.level37) * var30;
               this.level38 = this.level38 + ((this.level5 - var7) / var29 - this.level38) * var30;
               this.level39 = this.level39 + ((this.level6 - var9) / var29 - this.level39) * var30;
            }
         }

         this.timestamp6 = now;
      }
   }

   private void updateState13(long now, double deltaTime) {
      Vec3d var5 = mc.player.getVelocity();
      boolean var6 = mc.player.isOnGround() && var5.x * var5.x + var5.z * var5.z < 0.0025;
      boolean var7 = this.checkCondition2(now);
      if (var6 && !var7) {
         if (this.timestamp9 > 0L && now >= this.timestamp9) {
            this.volume17 = 0.0F;
            this.volume18 = 0.0F;
            this.timestamp9 = 0L;
            this.timestamp8 = now + this.resolveLong(480L, 1400L);
         }

         if (this.timestamp9 == 0L && now >= this.timestamp8) {
            float var8 = Math.max(0.001F, GCDUtil.getGCD());
            int var9 = this.random.nextDouble() < 0.94 ? 1 : 2;
            int var10 = this.random.nextDouble() < 0.78 ? 0 : 1;
            float var11 = this.random.nextBoolean() ? 1.0F : -1.0F;
            float var12 = this.random.nextBoolean() ? 1.0F : -1.0F;
            this.volume17 = var9 * var8 * var11;
            this.volume18 = var10 * var8 * var12;
            this.timestamp9 = now + this.resolveLong(18L, 44L);
         }
      } else {
         this.volume17 = 0.0F;
         this.volume18 = 0.0F;
         this.timestamp9 = 0L;
         if (now >= this.timestamp8) {
            this.timestamp8 = now + this.resolveLong(480L, 1200L);
         }
      }

      boolean var17 = this.timestamp9 > now;
      double var18 = 1.0 - Math.exp(-deltaTime / (var17 ? 0.018 : 0.035));
      this.volume15 = this.volume15 + (this.volume17 - this.volume15) * (float)var18;
      this.volume16 = this.volume16 + (this.volume18 - this.volume16) * (float)var18;
      float var19 = var6 && !var7 ? 1.0F : 0.0F;
      double var20 = 1.0 - Math.exp(-deltaTime / (var19 > this.volume19 ? 0.08 : 0.045));
      this.volume19 = this.volume19 + (var19 - this.volume19) * (float)var20;
      double var14 = 1.0 - Math.exp(-deltaTime / (var7 ? 0.12 : 0.3));
      float var16 = var7 ? this.volume20 : 0.1F;
      this.volume21 = this.volume21 + (var16 - this.volume21) * (float)var14;
   }

   private boolean checkCondition2(long now) {
      double var3 = Math.sqrt(
         this.level37 * this.level37 + this.level38 * this.level38 + this.level39 * this.level39
      );
      double var5 = Math.hypot(this.volume3, this.volume4);
      return now < this.timestamp5 && var3 > 0.12 || var5 > 24.0;
   }

   private boolean checkCondition3(long now, boolean protectedWindow) {
      if (protectedWindow) {
         this.timestamp19 = 0L;
         if (now >= this.timestamp18) {
            this.timestamp18 = now + this.resolveLong(650L, 1800L);
         }

         return false;
      } else {
         if (now < this.timestamp19) {
            return true;
         }

         if (now < this.timestamp18) {
            return false;
         }

         double var4 = this.random.nextDouble();
         long var6 = var4 < 0.65
            ? this.resolveLong(17L, 43L)
            : (var4 < 0.92 ? this.resolveLong(44L, 90L) : (var4 < 0.985 ? this.resolveLong(91L, 190L) : this.resolveLong(220L, 620L)));
         this.timestamp19 = now + var6;
         this.timestamp18 = this.timestamp19 + this.resolveLong(750L, 2400L);
         return true;
      }
   }

   private void updateState14(long now, boolean precision, float yawMargin, float pitchMargin) {
      if (now >= this.timestamp12 && now >= this.timestamp14) {
         if (precision) {
            this.timestamp12 = now + this.resolveLong(260L, 720L);
         } else {
            float var6 = MathHelper.clamp(yawMargin * this.computefloat9(0.78F, 1.24F), 0.85F, 12.0F);
            float var7 = MathHelper.clamp(pitchMargin * this.computefloat9(0.58F, 0.96F), 1.2F, 16.0F);
            float var8 = this.random.nextBoolean() ? 1.0F : -1.0F;
            float var9 = this.random.nextBoolean() ? 1.0F : -1.0F;
            if (this.livingEntity != null && mc.player != null) {
               Vec3d var10 = this.livingEntity.getBoundingBox().getCenter().subtract(mc.player.getEyePos());
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
               this.volume30 = var8 * var6;
               this.volume31 = this.computefloat8(0.08F, 0.24F) * var7;
            } else if (var15 < 0.72) {
               this.volume30 = this.computefloat8(0.12F, 0.34F) * var6;
               this.volume31 = var9 * var7;
            } else {
               this.volume30 = var8 * var6 * this.computefloat9(0.66F, 0.96F);
               this.volume31 = var9 * var7 * this.computefloat9(0.42F, 0.76F);
            }

            this.volume34 = this.computefloat9(0.18F, 0.38F);
            this.volume35 = this.computefloat9(2.0F, 2.8F);
            this.timestamp13 = now;
            this.timestamp14 = now + this.resolveLong(125L, 340L);
            this.timestamp12 = this.timestamp14 + this.resolveLong(900L, 3400L);
         }
      }
   }

   private float computefloat4(long now) {
      if (this.timestamp14 > this.timestamp13 && now >= this.timestamp13 && now < this.timestamp14) {
         double var3 = this.resolveDouble12((double)(now - this.timestamp13) / (this.timestamp14 - this.timestamp13), 0.0, 1.0);
         if (var3 < this.volume34) {
            double var7 = var3 / this.volume34;
            return (float)(1.0 - Math.pow(1.0 - var7, this.volume35));
         } else {
            double var5 = (var3 - this.volume34) / (1.0 - this.volume34);
            return (float)(1.0 - this.resolveDouble7(var5));
         }
      } else {
         return 0.0F;
      }
   }

   private FuntimeRotation.PathModel computePathModel(boolean action, double screenDelta) {
      FuntimeRotation.PathModel var4 = this.pathModel;

      for (int var5 = 0; var5 < 8; var5++) {
         double var6 = this.random.nextDouble();
         if (action) {
            var4 = var6 < 0.9
               ? FuntimeRotation.PathModel.EMPIRICAL
               : (
                  var6 < 0.93
                     ? FuntimeRotation.PathModel.MINIMUM_JERK
                     : (
                        var6 < 0.97
                           ? FuntimeRotation.PathModel.CORRECTIVE
                           : (
                              var6 < 0.99
                                 ? FuntimeRotation.PathModel.RANDOM_SPLINE
                                 : (screenDelta >= 2.8 ? FuntimeRotation.PathModel.FLICK_SETTLE : FuntimeRotation.PathModel.CORRECTIVE)
                           )
                     )
               );
         } else {
            var4 = var6 < 0.92
               ? FuntimeRotation.PathModel.EMPIRICAL
               : (
                  var6 < 0.95
                     ? FuntimeRotation.PathModel.MINIMUM_JERK
                     : (var6 < 0.99 ? FuntimeRotation.PathModel.CORRECTIVE : FuntimeRotation.PathModel.RANDOM_SPLINE)
               );
         }

         if ((var4 != FuntimeRotation.PathModel.FLICK_SETTLE || this.pathModel != FuntimeRotation.PathModel.FLICK_SETTLE)
            && (var4 != this.pathModel || this.index8 < 2 || var4 == FuntimeRotation.PathModel.EMPIRICAL)) {
            break;
         }
      }

      if (var4 == this.pathModel) {
         this.index8++;
      } else {
         this.pathModel = var4;
         this.index8 = 1;
      }

      return var4;
   }

   private FuntimeRotation.PathEase computePathEase() {
      FuntimeRotation.PathEase[] var1 = FuntimeRotation.PathEase.values();
      FuntimeRotation.PathEase var2 = this.pathEase2;

      for (int var3 = 0; var3 < 6 && var2 == this.pathEase2; var3++) {
         var2 = var1[this.random.nextInt(var1.length)];
      }

      this.pathEase2 = var2;
      return var2;
   }

   private Vec3d computeVec3d3(boolean precision) {
      int var2 = precision ? 44 : 56;
      Vec3d[] var3 = new Vec3d[var2];
      int[] var4 = new int[var2];
      double[] var5 = new double[var2];
      double[] var6 = new double[var2];
      double[] var7 = new double[var2];
      double var8 = precision ? 0.36 : 0.48;
      double var10 = precision ? 0.135 : 0.19;

      for (int var12 = 0; var12 < var2; var12++) {
         Vec3d var13 = this.computeVec3d4(precision);
         int var14 = this.resolveInt2(var13);
         double var15 = this.index4 == 0
            ? this.resolveDouble(var13.x, var13.y, var13.z, this.level4, this.level5, this.level6)
            : Double.MAX_VALUE;
         double var17 = this.index4 == 0
            ? this.resolveDouble2(var13.x, var13.y, var13.z, this.level4, this.level5, this.level6)
            : Double.MAX_VALUE;
         double var19 = 0.0;

         for (int var21 = 0; var21 < this.index4; var21++) {
            int var22 = (this.index5 - 1 - var21 + 12) % 12;
            var15 = Math.min(
               var15,
               this.resolveDouble(var13.x, var13.y, var13.z, this.level[var22], this.level2[var22], this.level3[var22])
            );
            var17 = Math.min(
               var17,
               this.resolveDouble2(var13.x, var13.y, var13.z, this.level[var22], this.level2[var22], this.level3[var22])
            );
            if (this.index[var22] == var14 && var21 < 6) {
               var19 += 0.08 - var21 * 0.01;
            }
         }

         double var27 = this.resolveDouble(var13.x, var13.y, var13.z, this.level4, this.level5, this.level6);
         double var23 = var15 * 1.35 + var17 * 1.1 + var27 * 0.42 + this.random.nextDouble() * 0.08 - var19;
         if (var14 == this.index6) {
            var23 -= 0.28;
         }

         if (var14 == this.index7) {
            var23 -= 0.12;
         }

         var3[var12] = var13;
         var4[var12] = var14;
         var5[var12] = var23;
         var6[var12] = var15;
         var7[var12] = var17;
      }

      int var25 = this.resolveInt(var5, var6, var7, var8, var10);
      if (var25 < 0) {
         var25 = this.resolveInt(var5, var6, var7, var8 * 0.68, var10 * 0.7);
      }

      if (var25 < 0) {
         var25 = this.resolveInt(var5, var6, var7, 0.24, 0.075);
      }

      if (var25 < 0) {
         return new Vec3d(this.level4, this.level5, this.level6);
      }

      Vec3d var26 = var3[var25];
      this.updateState15(var26, var4[var25]);
      return var26;
   }

   private int resolveInt(
      double[] scores, double[] screenDistances, double[] normalizedDistances, double minimumScreenDistance, double minimumNormalizedDistance
   ) {
      double var8 = 1.1;
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
            var17 += 0.16 + Math.exp((scores[var14] - var10) * 1.15);
         }
      }

      if (var17 <= 0.0) {
         return -1;
      }

      double var18 = this.random.nextDouble() * var17;

      for (int var16 = 0; var16 < scores.length; var16++) {
         if (!(screenDistances[var16] < minimumScreenDistance) && !(normalizedDistances[var16] < minimumNormalizedDistance) && !(scores[var16] < var10 - var8)) {
            var18 -= 0.16 + Math.exp((scores[var16] - var10) * 1.15);
            if (var18 <= 0.0) {
               return var16;
            }
         }
      }

      return -1;
   }

   private Vec3d computeVec3d4(boolean precision) {
      int var2 = this.random.nextInt(8);
      double var3 = (var2 + this.resolveDouble11(0.1, 0.9)) * Math.PI / 4.0;
      double var5 = precision ? this.resolveDouble11(0.16, 0.34) : this.resolveDouble11(0.18, 0.39);
      double var7 = this.random.nextDouble();
      double var9;
      double var11;
      if (precision) {
         if (var7 < 0.1) {
            var9 = 0.36;
            var11 = 0.47;
         } else if (var7 < 0.37) {
            var9 = 0.47;
            var11 = 0.68;
         } else if (var7 < 0.89) {
            var9 = 0.68;
            var11 = 0.86;
         } else {
            var9 = 0.86;
            var11 = 0.9;
         }
      } else if (var7 < 0.15) {
         var9 = 0.29;
         var11 = 0.47;
      } else if (var7 < 0.43) {
         var9 = 0.47;
         var11 = 0.67;
      } else if (var7 < 0.89) {
         var9 = 0.67;
         var11 = 0.86;
      } else {
         var9 = 0.86;
         var11 = 0.92;
      }

      double var13 = 0.5 + Math.cos(var3) * var5;
      double var15 = this.resolveDouble11(var9, var11);
      double var17 = 0.5 + Math.sin(var3) * var5;
      if (precision) {
         var13 = this.resolveDouble12(var13, 0.16, 0.84);
         var15 = this.resolveDouble12(var15, 0.36, 0.9);
         var17 = this.resolveDouble12(var17, 0.16, 0.84);
      } else {
         var13 = this.resolveDouble12(var13, 0.12, 0.88);
         var15 = this.resolveDouble12(var15, 0.29, 0.92);
         var17 = this.resolveDouble12(var17, 0.12, 0.88);
      }

      return new Vec3d(var13, var15, var17);
   }

   private int resolveInt2(Vec3d point) {
      int var2 = point.y < 0.49 ? 0 : (point.y < 0.68 ? 1 : 2);
      double var3 = Math.atan2(point.z - 0.5, point.x - 0.5);
      if (var3 < 0.0) {
         var3 += Math.PI * 2;
      }

      int var5 = Math.min(7, (int)(var3 / (Math.PI / 4)));
      return var2 * 8 + var5;
   }

   private void updateState15(Vec3d point, int zone) {
      this.level[this.index5] = point.x;
      this.level2[this.index5] = point.y;
      this.level3[this.index5] = point.z;
      this.index[this.index5] = zone;
      this.index5 = (this.index5 + 1) % 12;
      this.index4 = Math.min(this.index4 + 1, 12);
      this.index7 = this.index6;
      this.index6 = zone;
   }

   private void updateState16() {
      this.index4 = 0;
      this.index5 = 0;
      this.index6 = -1;
      this.index7 = -1;

      for (int var1 = 0; var1 < 12; var1++) {
         this.level[var1] = 0.0;
         this.level2[var1] = 0.0;
         this.level3[var1] = 0.0;
         this.index[var1] = -1;
      }
   }

   private double resolveDouble(double firstX, double firstY, double firstZ, double secondX, double secondY, double secondZ) {
      if (this.livingEntity != null && mc.player != null) {
         Box var13 = this.livingEntity.getBoundingBox();
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
         return this.resolveDouble2(firstX, firstY, firstZ, secondX, secondY, secondZ);
      }
   }

   private double resolveDouble2(double firstX, double firstY, double firstZ, double secondX, double secondY, double secondZ) {
      double var13 = firstX - secondX;
      double var15 = (firstY - secondY) * 1.25;
      double var17 = firstZ - secondZ;
      return Math.sqrt(var13 * var13 + var15 * var15 + var17 * var17);
   }

   private double resolveDouble3(double start, double overshoot, double end, double progress) {
      double var9 = this.resolveDouble6(this.resolveDouble12(progress / this.level32, 0.0, 1.0));
      double var11 = this.resolveDouble5(this.resolveDouble12((progress - this.level33) / (1.0 - this.level33), 0.0, 1.0));
      return this.resolveDouble9(start, overshoot, var9) + (end - overshoot) * var11;
   }

   private double resolveDouble4(double start, double overshoot, double end, double progress) {
      double var9 = this.resolveDouble12(progress / this.level32, 0.0, 1.0);
      double var11 = 1.0 - Math.pow(1.0 - var9, this.level31);
      double var13 = this.resolveDouble7(this.resolveDouble12((progress - this.level33) / (1.0 - this.level33), 0.0, 1.0));
      return this.resolveDouble9(start, overshoot, var11) + (end - overshoot) * var13;
   }

   private double resolveDouble5(double progress) {
      if (progress <= 0.0) {
         return 0.0;
      }

      if (progress >= 1.0) {
         return 1.0;
      }

      double var3 = Math.pow(progress, this.level29);
      double var5 = Math.pow(1.0 - progress, this.level30);
      return var3 / (var3 + var5);
   }

   private double resolveDouble6(double progress) {
      return progress - Math.sin((Math.PI * 2) * progress) / (Math.PI * 2);
   }

   private double resolveDouble7(double progress) {
      double var3 = progress * progress;
      double var5 = var3 * progress;
      return 10.0 * var5 - 15.0 * var5 * progress + 6.0 * var5 * var3;
   }

   private double resolveDouble8(double start, double control, double end, double progress) {
      double var9 = 1.0 - progress;
      return var9 * var9 * start + 2.0 * var9 * progress * control + progress * progress * end;
   }

   private double resolveDouble9(double start, double end, double progress) {
      return start + (end - start) * progress;
   }

   private double resolveDouble10(double magnitude) {
      return this.resolveDouble11(-magnitude, magnitude);
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

   private double resolveDouble11(double minimum, double maximum) {
      return minimum + this.random.nextDouble() * (maximum - minimum);
   }

   private long resolveLong(long minimumMillis, long maximumMillis) {
      long var5 = minimumMillis + (long)(this.random.nextDouble() * (maximumMillis - minimumMillis));
      return var5 * 1000000L;
   }

   private float computefloat10(float current, float target, float maximumChange) {
      return current < target ? Math.min(current + maximumChange, target) : Math.max(current - maximumChange, target);
   }

   private double resolveDouble12(double value, double minimum, double maximum) {
      return Math.max(minimum, Math.min(maximum, value));
   }

   private enum AimPhase {
      REACTION,
      ACQUIRE,
      TRACK,
      FOCUS,
      RECOVER;
   }

   private enum PathEase {
      MINIMUM_JERK,
      EASE_OUT_QUAD,
      EASE_OUT_CUBIC,
      EASE_OUT_QUART,
      EASE_IN_OUT_SINE,
      EASE_IN_OUT_CIRC,
      EASE_IN_OUT_CUBIC,
      EASE_IN_OUT_QUINT,
      EASE_IN_OUT_EXPO;

      double evaluate(double progress, FuntimeRotation owner) {
         switch (this) {
            case EASE_OUT_QUAD:
               return Easings.QUAD_OUT.ease(progress);
            case EASE_OUT_CUBIC:
               return Easings.CUBIC_OUT.ease(progress);
            case EASE_OUT_QUART:
               return Easings.QUART_OUT.ease(progress);
            case EASE_IN_OUT_SINE:
               return Easings.SINE_IN_OUT.ease(progress);
            case EASE_IN_OUT_CIRC:
               return Easings.CIRC_IN_OUT.ease(progress);
            case EASE_IN_OUT_CUBIC:
               return Easings.CUBIC_IN_OUT.ease(progress);
            case EASE_IN_OUT_QUINT:
               return Easings.QUINT_IN_OUT.ease(progress);
            case EASE_IN_OUT_EXPO:
               return Easings.EXPO_IN_OUT.ease(progress);
            default:
               return owner.resolveDouble7(progress);
         }
      }
   }

   private enum PathModel {
      EMPIRICAL,
      MINIMUM_JERK,
      RANDOM_SPLINE,
      FLICK_SETTLE,
      CORRECTIVE;
   }
}
