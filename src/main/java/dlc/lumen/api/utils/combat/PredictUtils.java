package dlc.lumen.api.utils.combat;

import dlc.lumen.api.QClient;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PredictUtils implements QClient {
   private static final Map<UUID, PredictUtils.PositionData> serverX = new ConcurrentHashMap<>();

   public static void updateEntity(LivingEntity entity) {
      PredictUtils.PositionData var1 = serverX.computeIfAbsent(entity.getUuid(), k -> new PredictUtils.PositionData());
      var1.update(entity.getX(), entity.getY(), entity.getZ());
   }

   public static PredictUtils.PositionData getData(LivingEntity entity) {
      return serverX.get(entity.getUuid());
   }

   public static Vec3d predict(LivingEntity entity, int ticks, float extraForward, boolean isMeFlying) {
      PredictUtils.PositionData var4 = getData(entity);
      Vec3d var5 = new Vec3d(entity.getX(), entity.getY() + entity.getStandingEyeHeight() / 2.0F, entity.getZ());
      if (var4 == null) {
         return predictElytraPhysics(entity, var5, ticks);
      }

      Vec3d var6 = var4.getResolvedForward();
      double var7 = var4.getLastSpeed();
      boolean var9 = var4.isSpeedChanged();
      if (entity.isGliding()) {
         double var10 = Math.hypot(var6.x, var6.z) * 20.0;
         double var12 = Math.abs(var6.y) * 20.0;
         if (var10 <= 5.0 && var12 <= 5.0) {
            return var5;
         }

         boolean var14 = isMeFlying && entity.isGliding() && var9;
         float var15 = var14 ? ticks + 2 + extraForward : ticks;
         Vec3d var16 = var5.add(var6.multiply(var15, var15, var15));
         Vec3d var17 = predictElytraPhysics(entity, var5, ticks);
         double var18 = MathHelper.clamp(var7 / 50.0, 0.3, 0.9);
         return new Vec3d(MathHelper.lerp(var18, var17.x, var16.x), MathHelper.lerp(var18, var17.y, var16.y), MathHelper.lerp(var18, var17.z, var16.z));
      } else {
         return var7 > 1.0 ? var5.add(var6.multiply(ticks, ticks, ticks)) : var5;
      }
   }

   public static Vec3d predict(LivingEntity entity, Vec3d pos, int ticks) {
      PredictUtils.PositionData var3 = getData(entity);
      if (var3 != null && entity.isGliding()) {
         Vec3d var4 = var3.getResolvedForward();
         double var5 = Math.hypot(var4.x, var4.z) * 20.0;
         double var7 = Math.abs(var4.y) * 20.0;
         return var5 <= 5.0 && var7 <= 5.0 ? pos : pos.add(var4.multiply(ticks, ticks, ticks));
      } else {
         return predictElytraPhysics(entity, pos, ticks);
      }
   }

   public static Vec3d predictElytraPhysics(LivingEntity entity, Vec3d pos, int ticks) {
      Vec3d var3 = entity.getVelocity();
      if (!entity.isGliding()) {
         return pos.add(var3.multiply(ticks, ticks, ticks));
      }

      double var4 = Math.hypot(entity.prevX - entity.getX(), entity.prevZ - entity.getZ()) * 20.0;
      double var6 = Math.abs(entity.getY() - entity.prevY) * 20.0;
      if (var4 <= 5.0 && var6 <= 5.0) {
         return pos;
      }

      for (int var8 = 0; var8 < ticks; var8++) {
         Vec3d var9 = entity.getRotationVector();
         float var10 = (float)Math.toRadians(entity.getPitch());
         double var11 = Math.sqrt(var3.x * var3.x + var3.z * var3.z);
         double var13 = var3.length();
         float var15 = MathHelper.cos(var10);
         var15 = (float)(var15 * var15 * Math.min(1.0, var9.length() / 0.4));
         var3 = var3.add(0.0, -0.08 * (-1.0 + var15 * 0.75), 0.0);
         if (var3.y < 0.0 && var11 > 0.0) {
            double var16 = var3.y * -0.1 * var15;
            var3 = var3.add(var9.x * var16 / var11, var16, var9.z * var16 / var11);
         }

         if (var10 < 0.0F && var11 > 0.0) {
            double var20 = var13 * -MathHelper.sin(var10) * 0.04;
            var3 = var3.add(-var9.x * var20 / var11, var20 * 3.2, -var9.z * var20 / var11);
         }

         if (var11 > 0.0) {
            var3 = var3.add((var9.x / var11 * var13 - var3.x) * 0.1, 0.0, (var9.z / var11 * var13 - var3.z) * 0.1);
         }

         var3 = var3.multiply(0.99, 0.98, 0.99);
         pos = pos.add(var3);
      }

      return pos;
   }

   public static Vec3d bypasselytrahacking(LivingEntity target) {
      Vec3d var1 = Vec3d.fromPolar(target.getLerpTargetPitch(), target.getLerpTargetYaw());
      Vec3d var2 = target.getRotationVector();
      Vec3d var3 = target.getPos().add(0.0, target.getHeight() * 0.6F, 0.0).subtract(mc.player.getEyePos());
      Vec3d var4 = var1.normalize().lerp(var2, var1.length());
      return var1;
   }

   public static void cleanup() {
      long var0 = System.currentTimeMillis();
      serverX.entrySet().removeIf(e -> var0 - e.getValue().getLastUpdate() > 10000L);
   }

   public static void clear() {
      serverX.clear();
   }

   public static class PositionData {
      private double serverX;
      private double serverY;
      private double serverZ;
      private double prevServerX;
      private double prevServerY;
      private double prevServerZ;
      private double backUpX;
      private double backUpY;
      private double backUpZ;
      private double resolvedForward;
      private double prevSpeed;
      private long lastUpdate;

      public Vec3d getResolvedPos() {
         return new Vec3d(this.serverX, this.serverY, this.serverZ);
      }

      public Vec3d getResolvedForward() {
         return new Vec3d(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
      }

      public void update(double x, double y, double z) {
         this.backUpX = this.prevServerX;
         this.backUpY = this.prevServerY;
         this.backUpZ = this.prevServerZ;
         this.prevServerX = this.serverX;
         this.prevServerY = this.serverY;
         this.prevServerZ = this.serverZ;
         this.serverX = x;
         this.serverY = y;
         this.serverZ = z;
         this.prevSpeed = this.resolvedForward;
         this.resolvedForward = this.getResolvedForward().length() * 20.0;
         this.lastUpdate = System.currentTimeMillis();
      }

      public boolean isSpeedChanged() {
         return this.resolvedForward >= 20.0 || this.resolvedForward != this.prevSpeed && this.resolvedForward == 0.0;
      }

      @Generated
      public double getServerX() {
         return this.serverX;
      }

      @Generated
      public double getServerY() {
         return this.serverY;
      }

      @Generated
      public double getServerZ() {
         return this.serverZ;
      }

      @Generated
      public double getPrevServerX() {
         return this.prevServerX;
      }

      @Generated
      public double getPrevServerY() {
         return this.prevServerY;
      }

      @Generated
      public double getPrevServerZ() {
         return this.prevServerZ;
      }

      @Generated
      public double getBackUpX() {
         return this.backUpX;
      }

      @Generated
      public double getBackUpY() {
         return this.backUpY;
      }

      @Generated
      public double getBackUpZ() {
         return this.backUpZ;
      }

      @Generated
      public double getLastSpeed() {
         return this.resolvedForward;
      }

      @Generated
      public double getPrevSpeed() {
         return this.prevSpeed;
      }

      @Generated
      public long getLastUpdate() {
         return this.lastUpdate;
      }
   }
}
