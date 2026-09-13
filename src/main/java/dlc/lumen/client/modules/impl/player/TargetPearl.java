package dlc.lumen.client.modules.impl.player;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.math.TimerUtils;
import dlc.lumen.api.utils.player.InventoryUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.Comparator;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class TargetPearl extends Module {
   private static final double LEVEL = 256.0;
   private static final double LEVEL2 = 11.0;
   private static final long TIMESTAMP = 2500L;
   private static final float VOLUME = -25.0F;
   private static final float VOLUME2 = 35.0F;
   private static final float VOLUME3 = 0.25F;
   public static final TargetPearl INSTANCE = new TargetPearl();
   private final ModeSetting modeSetting = new ModeSetting("Тип", "Автоматический", "По бинду", "Автоматический");
   private final BindSetting bindSetting = new BindSetting("Бинд", -1).visible(() -> this.modeSetting.is("По бинду"));
   private final BooleanSetting booleanSetting = new BooleanSetting("Только за противником", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Игнорировать друзей", true);
   private final TimerUtils timerUtils = new TimerUtils();
   private EnderPearlEntity enderPearlEntity;
   private int index = -1;
   private long timestamp;
   private boolean flag;
   private Vec2f vec2f;

   public TargetPearl() {
      super("TargetPearl", "Автоматически бросает жемчуг в цель", Module.ModuleCategory.PLAYER);
      this.addSettings(this.modeSetting, this.bindSetting, this.booleanSetting, this.booleanSetting2);
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (this.modeSetting.is("По бинду") && event.getKey() == this.bindSetting.getKey()) {
            if (this.helper()) {
               this.helper2();
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.index != -1 && !(mc.world.getEntityById(this.index) instanceof EnderPearlEntity var3 && var3.isAlive())) {
            this.index = -1;
         }

         if (this.modeSetting.is("Автоматический") && this.helper()) {
            this.helper2();
         }
      } else {
         this.helper17();
      }
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if (this.isEnable() && this.flag && this.vec2f != null) {
         float var2 = event.getForward();
         float var3 = event.getStrafe();
         if (var2 != 0.0F || var3 != 0.0F) {
            double var4 = MathHelper.wrapDegrees(Math.toDegrees(helper18(this.vec2f.x, var2, var3)));
            float var6 = 0.0F;
            float var7 = 0.0F;
            float var8 = Float.MAX_VALUE;

            for (float var9 = -1.0F; var9 <= 1.0F; var9++) {
               for (float var10 = -1.0F; var10 <= 1.0F; var10++) {
                  if (var9 != 0.0F || var10 != 0.0F) {
                     double var11 = MathHelper.wrapDegrees(Math.toDegrees(helper18(this.vec2f.x, var9, var10)));
                     float var13 = Math.abs(MathHelper.wrapDegrees((float)(var4 - var11)));
                     if (var13 < var8) {
                        var8 = var13;
                        var6 = var9;
                        var7 = var10;
                     }
                  }
               }
            }

            event.setForward(var6);
            event.setStrafe(var7);
         }
      }
   }

   @Override
   public void onDisable() {
      this.helper17();
      this.index = -1;
      this.timestamp = 0L;
      this.timerUtils.reset();
      super.onDisable();
   }

   private boolean helper() {
      return System.currentTimeMillis() < this.timestamp
         ? false
         : !mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.ENDER_PEARL)) && this.timerUtils.finished(1000L);
   }

   private void helper2() {
      Vec3d var1 = this.helper3();
      if (var1 == null) {
         this.helper17();
      } else {
         float[] var2 = this.helper10(var1);
         if (var2 != null && !Float.isNaN(var2[0]) && !Float.isNaN(var2[1])) {
            Vec3d var3 = this.helper13(var2[0], var2[1]);
            double var4 = Math.max(3.0, mc.player.getPos().distanceTo(var1) * 0.12);
            if (var3 == null || var1.distanceTo(var3) > var4) {
               this.helper17();
            } else if (!this.helper16()) {
               this.helper17();
            } else {
               float var6 = mc.player.getYaw();
               float var7 = mc.player.getPitch();
               this.flag = true;
               this.vec2f = new Vec2f(var2[0], var2[1]);

               try {
                  mc.player.setYaw(var2[0]);
                  mc.player.setPitch(var2[1]);
                  mc.player.networkHandler.sendPacket(new LookAndOnGround(var2[0], var2[1], mc.player.isOnGround(), mc.player.horizontalCollision));
                  InventoryUtils.swapAndUseHvH(Items.ENDER_PEARL);
                  this.timerUtils.reset();
                  this.timestamp = System.currentTimeMillis() + 2500L;
                  if (this.enderPearlEntity != null) {
                     this.index = this.enderPearlEntity.getId();
                  }
               } finally {
                  mc.player.setYaw(var6);
                  mc.player.setPitch(var7);
                  this.helper17();
               }
            }
         } else {
            this.helper17();
         }
      }
   }

   private Vec3d helper3() {
      this.enderPearlEntity = this.helper4();
      if (this.enderPearlEntity != null && this.enderPearlEntity.isAlive()) {
         Vec3d var1 = this.helper7(this.enderPearlEntity);
         return var1 != null && this.helper9(var1) ? var1 : null;
      } else {
         return null;
      }
   }

   private EnderPearlEntity helper4() {
      Box var1 = mc.player.getBoundingBox().expand(256.0);
      LivingEntity var2 = ModuleClass.INSTANCE != null ? ModuleClass.aura.getTarget() : null;
      return mc.world
         .getOtherEntities(
            mc.player,
            var1,
            entity -> entity instanceof EnderPearlEntity var3
               && var3.isAlive()
               && var3.getOwner() != mc.player
               && var3.getId() != this.index
               && !this.helper5(var3.getOwner())
               && (!this.booleanSetting.isState() || var2 != null && var3.getOwner() == var2)
         )
         .stream()
         .map(entity -> (EnderPearlEntity)entity)
         .filter(pearl -> this.helper6(pearl) <= 256.0)
         .min(Comparator.comparingDouble(this::helper6))
         .orElse(null);
   }

   private boolean helper5(Entity owner) {
      return this.booleanSetting2.isState() && owner instanceof PlayerEntity var2
         ? Lumen.INSTANCE != null && Lumen.INSTANCE.friendStorage != null && Lumen.INSTANCE.friendStorage.isFriend(var2.getName().getString())
         : false;
   }

   private double helper6(EnderPearlEntity pearl) {
      Vec3d var2 = mc.player.getPos();
      Vec3d var3 = pearl.getPos();
      double var4 = var3.x - var2.x;
      double var6 = var3.z - var2.z;
      return Math.sqrt(var4 * var4 + var6 * var6);
   }

   private Vec3d helper7(EnderPearlEntity pearl) {
      Vec3d var2 = pearl.getPos();
      Vec3d var3 = pearl.getVelocity();
      Vec3d var4 = var2;

      for (int var5 = 0; var5 < 200; var5++) {
         var4 = var2;
         var2 = var2.add(var3);
         if (this.helper14(var4, var2) || var2.y <= mc.world.getBottomY()) {
            return new Vec3d(MathHelper.floor(var4.x) + 0.5, MathHelper.floor(var4.y), MathHelper.floor(var4.z) + 0.5);
         }

         var3 = this.helper8(var3, var2);
      }

      return new Vec3d(MathHelper.floor(var4.x) + 0.5, MathHelper.floor(var4.y), MathHelper.floor(var4.z) + 0.5);
   }

   private Vec3d helper8(Vec3d motion, Vec3d position) {
      BlockPos var3 = BlockPos.ofFloored(position);
      return mc.world.getBlockState(var3).isOf(Blocks.WATER) ? motion.multiply(0.8).add(0.0, -0.03, 0.0) : motion.multiply(0.99).add(0.0, -0.03, 0.0);
   }

   private boolean helper9(Vec3d landingPos) {
      double var2 = mc.player.getPos().distanceTo(landingPos);
      return var2 >= 11.0 && var2 <= 256.0;
   }

   private float[] helper10(Vec3d targetPosition) {
      Vec3d var2 = mc.player.getPos();
      double var3 = targetPosition.x - var2.x;
      double var5 = targetPosition.y - mc.player.getEyeY();
      double var7 = targetPosition.z - var2.z;
      float var9 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      double var10 = Math.sqrt(var3 * var3 + var7 * var7);
      double var12 = Math.max(1.5, mc.player.getPos().distanceTo(targetPosition) * 0.08);
      TargetPearl.TrajectoryCandidate var14 = this.helper11(targetPosition, var9, -25.0F, 35.0F, var12, true);
      if (var14 != null) {
         return new float[]{var9, MathHelper.clamp(var14.LEVEL, -90.0F, 90.0F)};
      } else {
         TargetPearl.TrajectoryCandidate var15 = this.helper11(targetPosition, var9, -85.0F, 85.0F, var12, false);
         if (var15 == null) {
            double var16 = -Math.toDegrees(Math.atan2(var5, var10)) + 5.0;
            return new float[]{var9, MathHelper.clamp((float)var16, -90.0F, 90.0F)};
         } else {
            return new float[]{var9, MathHelper.clamp(var15.LEVEL, -90.0F, 90.0F)};
         }
      }
   }

   private TargetPearl.TrajectoryCandidate helper11(
      Vec3d targetPosition, float yaw, float minPitch, float maxPitch, double allowedError, boolean preferDirect
   ) {
      Vec3d var8 = mc.player.getPos();
      double var9 = 1.5;
      TargetPearl.TrajectoryCandidate var11 = null;

      for (float var12 = minPitch; var12 <= maxPitch; var12 += 0.25F) {
         float var13 = (float)Math.toRadians(var12);
         double var14 = -MathHelper.sin((float)Math.toRadians(yaw)) * MathHelper.cos(var13) * var9;
         double var16 = -MathHelper.sin(var13) * var9;
         double var18 = MathHelper.cos((float)Math.toRadians(yaw)) * MathHelper.cos(var13) * var9;
         Vec3d var20 = new Vec3d(var8.x, mc.player.getEyeY(), var8.z);
         Vec3d var21 = new Vec3d(var14, var16, var18);
         int var22 = 0;

         for (int var23 = 0; var23 < 200; var23++) {
            Vec3d var24 = var20;
            var20 = var20.add(var21);
            var21 = this.helper8(var21, var20);
            var22++;
            if (this.helper15(var24, var20)) {
               break;
            }

            if (this.helper14(var24, var20) || !(var20.y > mc.world.getBottomY())) {
               double var25 = var20.distanceTo(targetPosition);
               TargetPearl.TrajectoryCandidate var27 = new TargetPearl.TrajectoryCandidate(var12, var25, var22, var20);
               if (this.helper12(var27, var11, allowedError, preferDirect)) {
                  var11 = var27;
               }
               break;
            }
         }
      }

      return var11 != null && !(var11.LEVEL2 > allowedError) ? var11 : null;
   }

   private boolean helper12(
      TargetPearl.TrajectoryCandidate candidate, TargetPearl.TrajectoryCandidate currentBest, double allowedError, boolean preferDirect
   ) {
      if (currentBest == null) {
         return true;
      }

      boolean var6 = candidate.LEVEL2 <= allowedError;
      boolean var7 = currentBest.LEVEL2 <= allowedError;
      if (var6 != var7) {
         return var6;
      }

      if (preferDirect && var6 && var7) {
         float var8 = Math.abs(candidate.LEVEL);
         float var9 = Math.abs(currentBest.LEVEL);
         if (Math.abs(var8 - var9) > 0.01F) {
            return var8 < var9;
         }

         if (candidate.TIMESTAMP != currentBest.TIMESTAMP) {
            return candidate.TIMESTAMP < currentBest.TIMESTAMP;
         }
      }

      if (Math.abs(candidate.LEVEL2 - currentBest.LEVEL2) > 0.01) {
         return candidate.LEVEL2 < currentBest.LEVEL2;
      } else if (candidate.TIMESTAMP != currentBest.TIMESTAMP) {
         return candidate.TIMESTAMP < currentBest.TIMESTAMP;
      } else {
         return !preferDirect ? Math.abs(candidate.LEVEL) < Math.abs(currentBest.LEVEL) : false;
      }
   }

   private Vec3d helper13(float yaw, float pitch) {
      float var3 = (float)Math.toRadians(yaw);
      float var4 = (float)Math.toRadians(pitch);
      double var5 = 1.5;
      double var7 = mc.player.getX() - MathHelper.cos(var3) * 0.16F;
      double var9 = mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - 0.1;
      double var11 = mc.player.getZ() - MathHelper.sin(var3) * 0.16F;
      double var13 = -MathHelper.sin(var3) * MathHelper.cos(var4) * var5;
      double var15 = -MathHelper.sin(var4) * var5;
      double var17 = MathHelper.cos(var3) * MathHelper.cos(var4) * var5;
      Vec3d var19 = new Vec3d(var7, var9, var11);
      Vec3d var20 = new Vec3d(var13, var15, var17);

      for (int var21 = 0; var21 <= 200; var21++) {
         Vec3d var22 = var19;
         var19 = var19.add(var20);
         var20 = this.helper8(var20, var19);
         if (this.helper15(var22, var19)) {
            return null;
         }

         if (this.helper14(var22, var19) || var19.y <= mc.world.getBottomY()) {
            return new Vec3d(MathHelper.floor(var19.x) + 0.5, MathHelper.floor(var19.y), MathHelper.floor(var19.z) + 0.5);
         }
      }

      return null;
   }

   private boolean helper14(Vec3d from, Vec3d to) {
      return mc.world.raycast(new RaycastContext(from, to, ShapeType.COLLIDER, FluidHandling.NONE, mc.player)).getType() == Type.BLOCK;
   }

   private boolean helper15(Vec3d from, Vec3d to) {
      Box var3 = new Box(from, to).expand(0.3);

      for (Entity var5 : mc.world.getOtherEntities(mc.player, var3, entity -> {
         if (!entity.isAlive() || entity.isSpectator() || entity.noClip) {
            return false;
         } else {
            return entity == this.enderPearlEntity ? false : !(entity instanceof EnderPearlEntity);
         }
      })) {
         if (var5.getBoundingBox().expand(0.25).raycast(from, to).isPresent()) {
            return true;
         }
      }

      return false;
   }

   private boolean helper16() {
      return mc.player.getMainHandStack().isOf(Items.ENDER_PEARL)
         || mc.player.getOffHandStack().isOf(Items.ENDER_PEARL)
         || InventoryUtils.find(Items.ENDER_PEARL, 0, 8) != -1
         || InventoryUtils.find(Items.ENDER_PEARL, 9, 45) != -1;
   }

   private void helper17() {
      this.flag = false;
      this.enderPearlEntity = null;
      this.vec2f = null;
   }

   private static double helper18(float rotationYaw, float moveForward, float moveStrafing) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float var3 = 1.0F;
      if (moveForward < 0.0F) {
         var3 = -0.5F;
      } else if (moveForward > 0.0F) {
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

   private static final class TrajectoryCandidate {
      private final float LEVEL;
      private final double LEVEL2;
      private final int TIMESTAMP;
      private final Vec3d VOLUME;

      private TrajectoryCandidate(float pitch, double distanceToTarget, int ticks, Vec3d landingPos) {
         this.LEVEL = pitch;
         this.LEVEL2 = distanceToTarget;
         this.TIMESTAMP = ticks;
         this.VOLUME = landingPos;
      }
   }
}
