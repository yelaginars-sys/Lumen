package dlc.lumen.api.storages.implement;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventKeyboardInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class RotationStorage implements QClient {
   public static RotationStorage instance;
   private RotationStorage.RotationTask rotating = RotationStorage.RotationTask.IDLE;
   private float currentYawSpeed;
   private float currentPitchSpeed;
   private float currentYawReturnSpeed;
   private float currentPitchReturnSpeed;
   private int currentPriority;
   private int currentTimeout;
   private int idleTicks;
   private Rotation rotation;

   public RotationStorage() {
      instance = this;
      EventInvoker.register(this);
   }

   public static double direction(float rotationYaw, float moveForward, float moveStrafing) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float var3 = 1.0F;
      if (moveForward < 0.0F) {
         var3 = -0.5F;
      }

      if (moveForward > 0.0F) {
         var3 = 0.5F;
      }

      if (moveStrafing > 0.0F) {
         rotationYaw -= 90.0F * var3;
      }

      if (moveStrafing < 0.0F) {
         rotationYaw += 90.0F * var3;
      }

      return Math.toRadians(rotationYaw);
   }

   public static void fixMovement(EventKeyboardInput event, float yaw) {
      float var2 = event.getMovementForward();
      float var3 = event.getMovementSideways();
      if (var2 != 0.0F || var3 != 0.0F) {
         double var4 = MathHelper.wrapDegrees(Math.toDegrees(direction(yaw, var2, var3)));
         float var6 = 0.0F;
         float var7 = 0.0F;
         float var8 = Float.MAX_VALUE;

         for (float var9 = -1.0F; var9 <= 1.0F; var9++) {
            for (float var10 = -1.0F; var10 <= 1.0F; var10++) {
               if (var9 != 0.0F || var10 != 0.0F) {
                  double var11 = MathHelper.wrapDegrees(Math.toDegrees(direction(yaw, var9, var10)));
                  float var13 = Math.abs(MathHelper.wrapDegrees((float)(var4 - var11)));
                  if (var13 < var8) {
                     var8 = var13;
                     var6 = var9;
                     var7 = var10;
                  }
               }
            }
         }

         event.setMovementForward(var6);
         event.setMovementSideways(var7);
      }
   }

   @EventLink
   public void onInput(EventKeyboardInput event) {
      if (this.isRotating()) {
         fixMovement(event, MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getYaw()));
      }
   }

   private void handleAction() {
      Rotation var1 = new Rotation(FreeLookStorage.getFreeYaw(), FreeLookStorage.getFreePitch());
      ThreadLocalRandom var2 = ThreadLocalRandom.current();
      float var3 = this.currentYawReturnSpeed() * (0.6F + var2.nextFloat() * 0.7F);
      float var4 = this.currentPitchReturnSpeed() * (0.55F + var2.nextFloat() * 0.75F);
      if (this.checkTargetRotation(var1, var3, var4)) {
         this.stopRotation();
      }
   }

   @EventLink
   public void onEventTick(EventUpdate event) {
      if (this.currentTask().equals(RotationStorage.RotationTask.AIM) && this.idleTicks() > this.currentTimeout()) {
         this.currentTask(RotationStorage.RotationTask.RESET);
      }

      if (this.currentTask().equals(RotationStorage.RotationTask.RESET)) {
         this.handleAction();
      }

      this.idleTicks++;
   }

   public static void update(
      Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation
   ) {
      RotationStorage var8 = instance;
      if (mc.player != null) {
         if (var8.currentPriority() <= priority) {
            FreeLookStorage.setActive(!clientRotation);
            var8.currentYawSpeed(yawSpeed);
            var8.currentPitchSpeed(pitchSpeed);
            var8.currentYawReturnSpeed(yawReturnSpeed);
            var8.currentPitchReturnSpeed(pitchReturnSpeed);
            var8.currentTimeout(timeout);
            var8.currentPriority(priority);
            var8.currentTask(RotationStorage.RotationTask.AIM);
            var8.targetRotation(target);
            var8.checkTargetRotation(target, yawSpeed, pitchSpeed);
         }
      }
   }

   public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
      update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
   }

   public static void update(Rotation targetRotation, float yawSpeed, float pitchSpeed, float returnSpeed, int timeout, int priority) {
      update(targetRotation, yawSpeed, pitchSpeed, returnSpeed, returnSpeed, timeout, priority, false);
   }

   private boolean checkTargetRotation(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
      if (mc.player == null) {
         return false;
      }

      Rotation var4 = new Rotation(mc.player);
      float var5 = MathHelper.wrapDegrees(targetRotation.getYaw() - var4.getYaw());
      float var6 = targetRotation.getPitch() - var4.getPitch();
      float var7 = Math.min(Math.abs(var5), yawSpeed);
      float var8 = Math.min(Math.abs(var6), pitchSpeed);
      float var9 = mc.player.getYaw();
      var9 += GCDUtil.getFixedRotation(MathHelper.clamp(var5, -var7, var7));
      mc.player.setYaw(var9);
      mc.player.setPitch(MathHelper.clamp(mc.player.getPitch() + GCDUtil.getFixedRotation(MathHelper.clamp(var6, -var8, var8)), -90.0F, 90.0F));
      this.idleTicks(0);
      return new Rotation(mc.player).getDelta(targetRotation) < 1.0F;
   }

   public void stopRotation() {
      this.currentTask(RotationStorage.RotationTask.IDLE);
      this.currentPriority(0);
      this.targetRotation(null);
      FreeLookStorage.setActive(false);
   }

   public boolean isRotating() {
      return !this.rotating.equals(RotationStorage.RotationTask.IDLE);
   }

   @Generated
   public RotationStorage.RotationTask currentTask() {
      return this.rotating;
   }

   @Generated
   public float currentYawSpeed() {
      return this.currentYawSpeed;
   }

   @Generated
   public float currentPitchSpeed() {
      return this.currentPitchSpeed;
   }

   @Generated
   public float currentYawReturnSpeed() {
      return this.currentYawReturnSpeed;
   }

   @Generated
   public float currentPitchReturnSpeed() {
      return this.currentPitchReturnSpeed;
   }

   @Generated
   public int currentPriority() {
      return this.currentPriority;
   }

   @Generated
   public int currentTimeout() {
      return this.currentTimeout;
   }

   @Generated
   public int idleTicks() {
      return this.idleTicks;
   }

   @Generated
   public Rotation targetRotation() {
      return this.rotation;
   }

   @Generated
   public RotationStorage currentTask(RotationStorage.RotationTask currentTask) {
      this.rotating = currentTask;
      return this;
   }

   @Generated
   public RotationStorage currentYawSpeed(float currentYawSpeed) {
      this.currentYawSpeed = currentYawSpeed;
      return this;
   }

   @Generated
   public RotationStorage currentPitchSpeed(float currentPitchSpeed) {
      this.currentPitchSpeed = currentPitchSpeed;
      return this;
   }

   @Generated
   public RotationStorage currentYawReturnSpeed(float currentYawReturnSpeed) {
      this.currentYawReturnSpeed = currentYawReturnSpeed;
      return this;
   }

   @Generated
   public RotationStorage currentPitchReturnSpeed(float currentPitchReturnSpeed) {
      this.currentPitchReturnSpeed = currentPitchReturnSpeed;
      return this;
   }

   @Generated
   public RotationStorage currentPriority(int currentPriority) {
      this.currentPriority = currentPriority;
      return this;
   }

   @Generated
   public RotationStorage currentTimeout(int currentTimeout) {
      this.currentTimeout = currentTimeout;
      return this;
   }

   @Generated
   public RotationStorage idleTicks(int idleTicks) {
      this.idleTicks = idleTicks;
      return this;
   }

   @Generated
   public RotationStorage targetRotation(Rotation targetRotation) {
      this.rotation = targetRotation;
      return this;
   }

   public enum RotationTask {
      AIM,
      RESET,
      IDLE;
   }
}
