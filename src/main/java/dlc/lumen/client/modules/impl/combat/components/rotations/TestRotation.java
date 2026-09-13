package dlc.lumen.client.modules.impl.combat.components.rotations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dlc.lumen.api.QClient;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.impl.combat.components.RotationsSystem;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class TestRotation extends RotationsSystem implements QClient {
   private static final Path PATH = Path.of(System.getProperty("user.home"), "Desktop", "data.json");
   private final List<TestRotation.DatasetFrame> items = new ArrayList<>();
   private LivingEntity livingEntity;
   private LivingEntity livingEntity2;
   private Vec3d vec3d;
   private Vec3d vec3d2;
   private long lastModified = Long.MIN_VALUE;
   private long timestamp;
   private boolean flag;
   private int index;
   private int index2;
   private int index3;
   private int index4;
   private float volume;
   private float volume2;
   private float volume3;
   private float volume4;
   private float volume5 = 1.0F;
   private float volume6 = 1.0F;
   private boolean flag2;

   public void reset() {
      this.livingEntity = null;
      this.livingEntity2 = null;
      this.vec3d = null;
      this.vec3d2 = null;
      this.index = 0;
      this.index2 = 0;
      this.index3 = 0;
      this.index4 = 0;
      this.volume = 0.0F;
      this.volume2 = 0.0F;
      this.volume3 = 0.0F;
      this.volume4 = 0.0F;
      this.volume5 = 1.0F;
      this.volume6 = 1.0F;
      this.flag2 = false;
   }

   @Override
   public void updateRotations(LivingEntity target) {
      if (mc.player != null && target != null) {
         boolean var2 = this.checkState();
         this.updateState();
         Vec3d var3 = this.computeVec3d(target, var2);
         Vec2f var4 = RotationUtils.getRotations(var3);
         if (this.flag && !this.items.isEmpty()) {
            float var5 = mc.player.getYaw();
            float var6 = mc.player.getPitch();
            this.updateState3(target, var5, var6);
            float var7 = MathHelper.wrapDegrees(var4.x - this.volume3);
            float var8 = var4.y - this.volume4;
            TestRotation.DatasetFrame var9 = this.computeDatasetFrame2(var7, var8, var2);
            this.updateState2(var9, var7, var8, var2);
            float var10 = Math.max(GCDUtil.getGCDValue(), 1.0E-4F);
            float var11 = this.computefloat(var7, var9, true, var2);
            float var12 = this.computefloat(var8, var9, false, var2);
            var11 += this.computefloat2(var9, var7, true, var10);
            var12 += this.computefloat2(var9, var8, false, var10);
            this.volume = this.computefloat3(this.volume, var11, var7, true, var2);
            this.volume2 = this.computefloat3(this.volume2, var12, var8, false, var2);
            float var13 = this.computefloat4(this.volume, var7, var10, true);
            float var14 = this.computefloat4(this.volume2, var8, var10, false);
            this.volume3 = MathHelper.wrapDegrees(this.volume3 + var13);
            this.volume4 = MathHelper.clamp(this.volume4 + var14, -89.0F, 89.0F);
            RotationStorage.update(new Rotation(this.volume3, this.volume4), 360.0F, 360.0F, 45.0F, 45.0F, 0, 1, Aura.clientLook.isState());
         } else {
            RotationStorage.update(new Rotation(var4.x, MathHelper.clamp(var4.y, -89.0F, 89.0F)), 360.0F, 360.0F, 45.0F, 45.0F, 0, 1, Aura.clientLook.isState());
         }
      }
   }

   private boolean checkState() {
      float var1 = mc.player.getAttackCooldownProgress(1.5F);
      boolean var2 = !mc.player.isOnGround() && mc.player.getVelocity().y < 0.0 && mc.player.fallDistance > 0.0F;
      return var1 >= 0.88F || var2;
   }

   private void updateState() {
      long var1 = System.currentTimeMillis();
      if (this.checkCondition(var1)) {
         this.timestamp = var1;
         long var3 = this.resolveLong();
         if (!this.flag || var3 != this.lastModified) {
            this.items.clear();
            this.flag = false;
            if (!Files.exists(PATH)) {
               this.lastModified = Long.MIN_VALUE;
            } else {
               try (BufferedReader var5 = Files.newBufferedReader(PATH)) {
                  for (JsonElement var8 : JsonParser.parseReader(var5).getAsJsonArray()) {
                     if (var8.isJsonObject()) {
                        TestRotation.DatasetFrame var9 = this.computeDatasetFrame(var8.getAsJsonObject());
                        if (var9 != null) {
                           this.items.add(var9);
                        }
                     }
                  }

                  this.flag = !this.items.isEmpty();
                  this.lastModified = var3;
                  this.reset();
               } catch (IOException | IllegalStateException var12) {
                  this.flag = false;
                  this.lastModified = Long.MIN_VALUE;
                  this.reset();
               }
            }
         }
      }
   }

   private boolean checkCondition(long now) {
      return this.flag && !this.items.isEmpty() ? now - this.timestamp >= 3000L : now - this.timestamp >= 1500L;
   }

   private long resolveLong() {
      try {
         return Files.exists(PATH) ? Files.getLastModifiedTime(PATH).toMillis() : Long.MIN_VALUE;
      } catch (IOException var2) {
         return Long.MIN_VALUE;
      }
   }

   private TestRotation.DatasetFrame computeDatasetFrame(JsonObject object) {
      float var2 = this.computefloat5(object, "fromYaw");
      float var3 = this.computefloat5(object, "toYaw");
      float var4 = this.computefloat5(object, "fromPitch");
      float var5 = this.computefloat5(object, "toPitch");
      float var6 = MathHelper.wrapDegrees(var3 - var2);
      float var7 = var5 - var4;
      float var8 = Math.abs(var6);
      float var9 = Math.abs(var7);
      float var10 = Math.max(this.computefloat5(object, "deltaYaw"), var8);
      float var11 = Math.max(this.computefloat5(object, "deltaPitch"), var9);
      if (var10 <= 0.0F && var11 <= 0.0F) {
         return null;
      }

      TestRotation.DatasetFrame var12 = new TestRotation.DatasetFrame();
      var12.deltaYaw = var10;
      var12.deltaPitch = var11;
      var12.signedYaw = var6 != 0.0F ? var6 : Math.signum(this.computefloat5(object, "jitterYawDir")) * var10;
      var12.signedPitch = var7 != 0.0F ? var7 : Math.signum(this.computefloat5(object, "jitterPitchDir")) * var11;
      var12.rotationSpeed = Math.max(this.computefloat5(object, "rotationSpeed"), 0.0F);
      var12.jitterScore = Math.max(this.computefloat5(object, "jitterScore"), 0.0F);
      var12.jitterYawSpeed = Math.max(this.computefloat5(object, "jitterYawSpeed"), 0.0F);
      var12.jitterPitchSpeed = Math.max(this.computefloat5(object, "jitterPitchSpeed"), 0.0F);
      var12.isJittering = this.checkCondition2(object, "isJittering");
      var12.attacking = this.checkCondition2(object, "attacking");
      var12.combatFrame = this.checkCondition2(object, "isCombatFrame");
      var12.instantSnap = this.checkCondition2(object, "isInstantSnap");
      var12.timeDeltaMs = Math.max(1L, object.has("timeDeltaMs") ? object.get("timeDeltaMs").getAsLong() : 50L);
      return var12;
   }

   private TestRotation.DatasetFrame computeDatasetFrame2(float remainingYaw, float remainingPitch, boolean focus) {
      float var4 = Math.abs(remainingYaw) + Math.abs(remainingPitch) * 0.82F;
      int var5 = this.items.size();
      int var6 = Math.min(var5, focus ? 78 : 56);
      int var7 = this.index % var5;
      float var8 = Float.MAX_VALUE;

      for (int var9 = 0; var9 < var6; var9++) {
         int var10 = (this.index + var9) % var5;
         TestRotation.DatasetFrame var11 = this.items.get(var10);
         float var12 = var11.deltaYaw + var11.deltaPitch * 0.82F;
         float var13 = Math.abs(var12 - var4);
         if (focus) {
            if (!var11.isCombatLike()) {
               var13 += 3.0F;
            }

            if (var11.instantSnap) {
               var13 -= 0.5F;
            }
         } else if (var11.isCombatLike()) {
            var13 += 1.6F;
         }

         if (var4 < 10.0F && var11.isJittering) {
            var13 -= Math.min(var11.jitterScore, 2.6F) * 0.2F;
         }

         var13 += var9 * 0.032F;
         if (var13 < var8) {
            var8 = var13;
            var7 = var10;
         }
      }

      this.index = (var7 + 1) % var5;
      return this.items.get(var7);
   }

   private void updateState2(TestRotation.DatasetFrame frame, float remainingYaw, float remainingPitch, boolean focus) {
      if (this.index4 > 0) {
         this.index4--;
      } else {
         ThreadLocalRandom var5 = ThreadLocalRandom.current();
         float var6 = MathHelper.clamp((Math.abs(remainingYaw) + Math.abs(remainingPitch)) / 32.0F, 0.0F, 1.0F);
         float var7 = MathHelper.clamp((float)frame.timeDeltaMs / 120.0F, 0.0F, 1.0F);
         float var8 = focus ? 0.94F : 0.86F;
         float var9 = focus ? 1.12F : 1.04F;
         float var10 = focus ? 0.92F : 0.84F;
         float var11 = focus ? 1.08F : 1.0F;
         this.volume5 = var5.nextFloat(var8, var9 + var6 * 0.08F + var7 * 0.04F);
         this.volume6 = var5.nextFloat(var10, var11 + var6 * 0.06F + var7 * 0.03F);
         if (frame.isCombatLike()) {
            this.volume5 *= 1.02F;
            this.volume6 *= 1.015F;
         }

         this.index4 = var5.nextInt(focus ? 2 : 3, focus ? 6 : 8);
      }
   }

   private float computefloat(float remaining, TestRotation.DatasetFrame frame, boolean yawAxis, boolean focus) {
      float var5 = Math.abs(remaining);
      if (var5 <= 1.0E-4F) {
         return 0.0F;
      }

      float var6 = yawAxis ? frame.deltaYaw : frame.deltaPitch;
      float var7 = 0.3F + MathHelper.clamp(frame.rotationSpeed * (yawAxis ? 3.4F : 2.8F), 0.0F, yawAxis ? 0.2F : 0.16F);
      float var8 = MathHelper.clamp(var5 / (yawAxis ? 105.0F : 82.0F), 0.09F, yawAxis ? 0.52F : 0.46F);
      float var9 = Math.max(var6 * Math.max(var7, var8), yawAxis ? 0.03F : 0.024F);
      if (frame.instantSnap) {
         var9 = Math.max(var9, var5 * (yawAxis ? 0.085F : 0.065F));
      }

      if (frame.attacking || frame.combatFrame) {
         var9 *= yawAxis ? 1.02F : 1.015F;
      }

      var9 *= yawAxis ? 0.5F : 0.46F;
      float var10 = yawAxis ? 6.0F : 4.0F;
      if (var5 < var10) {
         float var11 = 1.0F + (var10 - var5) / var10 * 0.18F;
         var9 *= var11;
      }

      float var15 = yawAxis ? Math.max(0.48F, var5 * (frame.instantSnap ? 0.11F : 0.065F)) : Math.max(0.34F, var5 * (frame.instantSnap ? 0.09F : 0.058F));
      var9 = Math.min(var9, var15);
      var9 = Math.min(var9, var5);
      return Math.signum(remaining) * var9;
   }

   private float computefloat2(TestRotation.DatasetFrame frame, float remaining, boolean yawAxis, float gcd) {
      float var5 = Math.abs(remaining);
      if (var5 > (yawAxis ? 6.5F : 4.8F)) {
         return 0.0F;
      }

      float var6 = yawAxis ? frame.jitterYawSpeed : frame.jitterPitchSpeed;
      float var7 = gcd * MathHelper.clamp(frame.jitterScore * 0.01F, 0.0F, yawAxis ? 0.15F : 0.11F);
      var7 += gcd * MathHelper.clamp(var6 * (yawAxis ? 1.3F : 1.0F), 0.0F, yawAxis ? 0.1F : 0.07F);
      if (frame.isJittering) {
         var7 *= 1.05F;
      }

      if (var7 <= 0.0F) {
         return 0.0F;
      }

      float var8 = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
      float var9 = var7 * ThreadLocalRandom.current().nextFloat(0.3F, 0.95F) * var8;
      if (Math.abs(var9) > var5 && Math.signum(var9) == Math.signum(remaining)) {
         var9 = remaining;
      }

      return var9;
   }

   private void updateState3(LivingEntity target, float currentYaw, float currentPitch) {
      if (!this.flag2 || this.livingEntity2 != target) {
         this.livingEntity2 = target;
         this.volume3 = currentYaw;
         this.volume4 = currentPitch;
         this.volume = 0.0F;
         this.volume2 = 0.0F;
         this.volume5 = 1.0F;
         this.volume6 = 1.0F;
         this.index4 = 0;
         this.flag2 = true;
      }
   }

   private float computefloat3(float currentStep, float desiredStep, float remaining, boolean yawAxis, boolean focus) {
      float var6 = Math.abs(remaining);
      if (var6 <= 1.0E-4F) {
         return 0.0F;
      }

      float var7 = yawAxis ? (focus ? 0.092F : 0.06F) : (focus ? 0.082F : 0.055F);
      float var8 = var7 * (yawAxis ? this.volume5 : this.volume6);
      float var9 = currentStep + (desiredStep - currentStep) * MathHelper.clamp(var8, 0.025F, 0.16F);
      float var10 = yawAxis ? 0.13F : 0.1F;
      float var11 = yawAxis ? (focus ? 0.056F : 0.036F) : (focus ? 0.046F : 0.032F);
      float var12 = yawAxis ? this.volume5 : this.volume6;
      float var13 = var10 + var6 * var11 * MathHelper.clamp(var12, 0.88F, 1.18F);
      float var14 = yawAxis ? 5.5F : 3.8F;
      if (var6 < var14) {
         var13 *= 1.12F;
      }

      var9 = MathHelper.clamp(var9, -var13, var13);
      if (Math.abs(remaining) < Math.abs(var9) && Math.signum(remaining) == Math.signum(var9)) {
         var9 = remaining;
      }

      return var9;
   }

   private float computefloat4(float wantedDelta, float remaining, float gcd, boolean yawAxis) {
      float var5 = wantedDelta;
      if (Math.abs(remaining) < Math.abs(var5) && Math.signum(remaining) == Math.signum(var5)) {
         var5 = remaining;
      }

      float var6 = Math.round(var5 / gcd) * gcd;
      if (var6 == 0.0F && Math.abs(var5) >= gcd * 0.2F) {
         var6 = Math.signum(var5) * gcd;
      }

      if (Math.abs(remaining) < Math.abs(var6) && Math.signum(remaining) == Math.signum(var6)) {
         var6 = remaining;
      }

      if (!yawAxis) {
         var6 = MathHelper.clamp(var6, -89.0F, 89.0F);
      }

      return var6;
   }

   private Vec3d computeVec3d(LivingEntity target, boolean focus) {
      if (this.livingEntity == target && this.vec3d != null && this.vec3d2 != null) {
         if (this.index2++ >= this.index3) {
            this.vec3d2 = this.computeVec3d2(target, focus);
            this.index2 = 0;
            this.index3 = this.resolveInt(focus);
         }

         float var3 = focus ? 0.06F : 0.04F;
         this.vec3d = new Vec3d(
            MathHelper.lerp(var3, this.vec3d.x, this.vec3d2.x),
            MathHelper.lerp(var3, this.vec3d.y, this.vec3d2.y),
            MathHelper.lerp(var3, this.vec3d.z, this.vec3d2.z)
         );
         return this.vec3d;
      } else {
         this.livingEntity = target;
         this.vec3d2 = this.computeVec3d2(target, focus);
         this.vec3d = this.vec3d2;
         this.index2 = 0;
         this.index3 = this.resolveInt(focus);
         return this.vec3d;
      }
   }

   private int resolveInt(boolean focus) {
      return ThreadLocalRandom.current().nextInt(focus ? 7 : 10, focus ? 13 : 18);
   }

   private Vec3d computeVec3d2(LivingEntity target, boolean focus) {
      Box var3 = this.getPredictedBox(target);
      ThreadLocalRandom var4 = ThreadLocalRandom.current();
      double var5 = MathHelper.lerp(var4.nextDouble(0.45, 0.55), var3.minX, var3.maxX);
      double var7 = MathHelper.lerp(var4.nextDouble(focus ? 0.53 : 0.49, focus ? 0.7 : 0.76), var3.minY, var3.maxY);
      double var9 = MathHelper.lerp(var4.nextDouble(0.45, 0.55), var3.minZ, var3.maxZ);
      return new Vec3d(var5, var7, var9);
   }

   private float computefloat5(JsonObject object, String key) {
      return object.has(key) ? object.get(key).getAsFloat() : 0.0F;
   }

   private boolean checkCondition2(JsonObject object, String key) {
      return object.has(key) && object.get(key).getAsBoolean();
   }

   private static class DatasetFrame {
      float deltaYaw;
      float deltaPitch;
      float signedYaw;
      float signedPitch;
      float rotationSpeed;
      float jitterScore;
      float jitterYawSpeed;
      float jitterPitchSpeed;
      long timeDeltaMs;
      boolean isJittering;
      boolean attacking;
      boolean combatFrame;
      boolean instantSnap;

      boolean isCombatLike() {
         return this.attacking || this.combatFrame || this.instantSnap;
      }
   }
}
