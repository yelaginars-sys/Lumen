package dlc.lumen.client.modules.impl.combat.components.rotations;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventLook;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LegitRotation extends RotationsSystem implements QClient {
   private static final double LEVEL = 0.15;
   private static final double LEVEL2 = 0.5;
   private static final double LEVEL3 = 0.15;
   private static final double LEVEL4 = 5.0;
   private static final double LEVEL5 = 3.2;
   private static final float VOLUME = 0.09F;
   private static final float VOLUME2 = 0.005F;
   private static final float VOLUME3 = 0.16F;
   private static final float VOLUME4 = 0.0055F;
   private static final float VOLUME5 = 0.1F;
   private static final float VOLUME6 = 0.0035F;
   private final Random random = new Random();
   private LivingEntity livingEntity;
   private long timestamp;
   private float volume;
   private float volume2;
   private float volume3;
    private float volume4;
    private float volume5;
    private float volume6;
    private float volume7 = 0.09F;
    private float volume8 = 3.2F;

   public LegitRotation() {
      EventInvoker.register(this);
   }

   @Override
   public void updateRotations(LivingEntity target) {
      this.livingEntity = target;
      this.timestamp = System.nanoTime();
   }

    public void reset() {
       this.livingEntity = null;
       this.volume = 0.0F;
       this.volume2 = 0.0F;
       this.volume7 = 0.05F + this.random.nextFloat() * 0.07F;
       this.volume8 = 2.5F + this.random.nextFloat() * 1.5F;
    }

   @EventLink(priority = -100)
   public void onLook(EventLook event) {
      if (!event.isCancelled()) {
         if (mc.player != null && mc.world != null && mc.currentScreen == null) {
            if (this.checkState()) {
               LivingEntity var2 = this.computeLivingEntity();
               if (var2 == null) {
                  this.volume = 0.0F;
                  this.volume2 = 0.0F;
               } else {
                  double var3 = event.getYaw();
                  double var5 = event.getPitch();
                  boolean var7 = Math.abs(var3) >= 0.5 || Math.abs(var5) >= 0.5;
                  if (!var7) {
                     Vec3d var32 = this.getPredictedBox(var2).getCenter();
                     Vec3d var33 = mc.player.getEyePos();
                     double var34 = var32.x - var33.x;
                     double var35 = var32.y - var33.y;
                     double var36 = var32.z - var33.z;
                     float var37 = (float)(Math.toDegrees(Math.atan2(var36, var34)) - 90.0);
                     float var38 = MathHelper.wrapDegrees(var37 - mc.player.getYaw());
                      if (Math.abs(var38) > this.volume8) {
                         this.volume2 = 0.0F;
                         this.volume7 = 0.05F + this.random.nextFloat() * 0.07F;
                         this.volume8 = 2.5F + this.random.nextFloat() * 1.5F;
                      } else {
                         this.volume2 = Math.min(this.volume2 + 0.002F + this.random.nextFloat() * 0.008F, this.volume7);
                         this.updateState(event, var38 * this.volume2);
                      }
                  } else {
                     this.volume2 = 0.0F;
                     Vec3d var8 = this.computeVec3d(var2);
                     Vec3d var9 = mc.player.getEyePos();
                     double var10 = var8.x - var9.x;
                     double var12 = var8.z - var9.z;
                     float var14 = (float)(Math.toDegrees(Math.atan2(var12, var10)) - 90.0);
                     float var15 = MathHelper.wrapDegrees(var14 - mc.player.getYaw());
                     float var16 = Math.abs(var15);
                      if (var16 < 0.1 + this.random.nextFloat() * 0.15) {
                         this.volume = 0.0F;
                      } else {
                         boolean var17 = var3 * var15 > 0.0;
                         if (!var17) {
                            this.volume *= 0.85F + this.random.nextFloat() * 0.1F;
                           float var39 = 1.0F - Math.min(1.0F, (float)(var16 / 3.2));
                           float var40 = 0.75F - 0.3F * var39;
                           event.setYaw(var3 * var40);
                        } else {
                            this.volume4 = this.volume4 + (0.03F + this.random.nextFloat() * 0.08F);
                            float var18 = (var16 < 5.0 ? 0.1F : 0.16F) * (0.7F + this.random.nextFloat() * 0.7F);
                            float var19 = (var16 < 5.0 ? 0.0035F : 0.0055F) * (0.5F + this.random.nextFloat());
                            this.volume = Math.min(this.volume + var19, var18);
                            float var20 = (float)(Math.sin(this.volume4 * 0.5) * (0.002F + this.random.nextFloat() * 0.004F));
                           float var21 = Math.max(0.0F, this.volume + var20);
                           double var22 = var15 * var21;
                           double var24 = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
                           double var26 = var24 * var24 * var24;
                           double var28 = var26 * 1.2;
                           long var30 = Math.round(var22 / var28);
                           if (var30 != 0L) {
                              event.setYaw(var3 + var30 * var26 * 8.0);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void updateState(EventLook event, double desiredYawDeg) {
      double var4 = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      double var6 = var4 * var4 * var4;
      double var8 = var6 * 1.2;
      long var10 = Math.round(desiredYawDeg / var8);
      if (var10 != 0L) {
         event.setYaw(event.getYaw() + var10 * var6 * 8.0);
      }
   }

    private Vec3d computeVec3d(LivingEntity entity) {
       this.volume3 = this.volume3 + (0.03F + this.random.nextFloat() * 0.12F);
       Box var2 = this.getPredictedBox(entity);
       float var3 = (float)(Math.sin(this.volume3 * 0.3) * 0.4 + 0.5 + (this.random.nextFloat() - 0.5F) * 0.4F);
       if (this.random.nextFloat() < 0.12F) {
          this.volume5 = this.random.nextFloat() * (float)(Math.PI * 2);
       } else {
          this.volume5 = this.volume5 + (this.random.nextFloat() - 0.5F) * (0.3F + this.random.nextFloat() * 0.9F);
       }
      if (this.volume5 > (float) (Math.PI * 2)) {
         this.volume5 -= (float) (Math.PI * 2);
      }

      if (this.volume5 < 0.0F) {
         this.volume5 += (float) (Math.PI * 2);
      }

       float var4 = this.random.nextFloat() * (float)var2.getLengthX() * (0.3F + this.random.nextFloat() * 1.4F);
       this.volume6 = this.volume6 + (var4 - this.volume6) * (0.15F + this.random.nextFloat() * 0.3F);
      Vec3d var5 = var2.getCenter();
      double var6 = var5.x + Math.cos(this.volume5) * this.volume6;
      double var8 = var5.z + Math.sin(this.volume5) * this.volume6;
      double var10 = var2.minY + var2.getLengthY() * var3;
      return new Vec3d(var6, var10, var8);
   }

   private LivingEntity computeLivingEntity() {
      if (this.livingEntity != null && this.livingEntity.isAlive() && !this.livingEntity.isRemoved()) {
         return System.nanoTime() - this.timestamp > 400000000L ? null : this.livingEntity;
      } else {
         return null;
      }
   }

   private boolean checkState() {
      Aura var1 = Aura.INSTANCE;
      return var1 != null && var1.isEnable() && var1.rotationType.is("HolyLegit");
   }
}
