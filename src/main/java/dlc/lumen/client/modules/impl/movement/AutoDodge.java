package dlc.lumen.client.modules.impl.movement;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.player.InventoryUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AutoDodge extends Module {
   public static AutoDodge INSTANCE = new AutoDodge();
   private final Map<Integer, AutoDodge.TrackedPotion> integers = new HashMap<>();
   private int index = 0;
   private boolean flag;

   public AutoDodge() {
      super("AutoDodge", "Автоматически уклоняется от выбранных целей", Module.ModuleCategory.MOVEMENT);
   }

   @Override
   public void onEnable() {
      this.integers.clear();
      super.onEnable();
   }

   @EventLink
   public void onTick(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         Iterator var2 = this.integers.entrySet().iterator();

         while (var2.hasNext()) {
            if (mc.world.getEntityById((Integer)((Entry)var2.next()).getKey()) == null) {
               var2.remove();
            }
         }

         Box var3 = mc.player.getBoundingBox().expand(2.0);
         int var4 = mc.options.getViewDistance().getValue();

         for (PotionEntity var6 : mc.world.getEntitiesByClass(PotionEntity.class, mc.player.getBoundingBox().expand(var4 * 16), p -> true)) {
            AutoDodge.TrackedPotion var7 = this.integers.get(var6.getId());
            if (var7 != null) {
               boolean var8 = helper3(var6, var3);
               int var9 = 0xFF000000 | var7.a & 16777215;
               if (!var8 || Lumen.INSTANCE.friendStorage.isFriend(var7.b)) {
                  return;
               }

               if (var9 != -13447886 && var9 != -16776961 || !(mc.player.distanceTo(var6) > 2.3) || this.index < 0) {
                  return;
               }

               ItemStack var10 = Items.DRIED_KELP.getDefaultStack();
               if (!mc.player.getItemCooldownManager().isCoolingDown(var10) && helper6() != -1) {
                  if (!this.flag) {
                     if (new Rotation(mc.player.prevYaw, mc.player.prevPitch).getDelta(new Rotation(mc.player.getYaw(), mc.player.getPitch())) < 20.0) {
                        this.index++;
                     }

                     if (this.index >= 2) {
                        this.index = 5;
                        this.flag = true;
                        InventoryUtils.swapAndUseHvH(Items.DRIED_KELP);
                     }
                  }

                  Rotation var11 = helper7(mc.player.getEyePos(), var6.getEyePos());
                  ThreadLocalRandom var12 = ThreadLocalRandom.current();
                  RotationStorage.update(
                     new Rotation(var11.getYaw() + var12.nextFloat(-3.0F, 3.0F), var11.getPitch() + var12.nextFloat(-3.0F, 3.0F)),
                     180.0F,
                     180.0F,
                     180.0F,
                     180.0F,
                     1,
                     1,
                     false
                  );
                  break;
               }
            }
         }

         this.index++;
         this.flag = false;
         return;
      }
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE && mc.player != null && mc.world != null) {
         this.helper(event.getPacket());
         if (event.getPacket() instanceof GameMessageS2CPacket var2 && var2.content().getString().equals("На этой анархии этот предмет не работает")) {
            this.index = -50;
         }
      }
   }

   private void helper(Packet<?> packet) {
      if (packet instanceof BundlePacket var15) {
         for (Object var17 : var15.getPackets()) {
            this.helper((Packet<?>)var17);
         }
      } else {
         if (packet instanceof EntitySpawnS2CPacket var2) {
            if (var2.getEntityType() != EntityType.POTION) {
               return;
            }

            Map<String, AutoDodge.PotionHolder> var3 = this.helper2();
            Vec3d var4 = new Vec3d(var2.getX(), var2.getY(), var2.getZ());
            Vec3d var5 = new Vec3d(var2.getVelocityX(), var2.getVelocityY(), var2.getVelocityZ());
            double var6 = Double.MAX_VALUE;
            String var8 = null;
            int var9 = 0;

            for (Entry<String, AutoDodge.PotionHolder> var11 : var3.entrySet()) {
               AutoDodge.PotionHolder var12 = var11.getValue();
               double var13 = var4.distanceTo(var12.b);
               if (var13 <= 25.0) {
                  if (var12.b.y - var4.y > 2.0) {
                     if (new Vec3d(var4.x - var12.b.x, 0.0, var4.z - var12.b.z).length() < 15.0 && var13 < var6) {
                        var6 = var13;
                        var9 = var12.a;
                        var8 = (String)var11.getKey();
                     }
                  } else if ((var5.lengthSquared() <= 1.0E-12 || var5.normalize().dotProduct(var12.c.normalize()) > 0.1) && var13 < var6) {
                     var6 = var13;
                     var9 = var12.a;
                     var8 = (String)var11.getKey();
                  }
               }
            }

            if (var8 != null) {
               this.index = 0;
               this.integers.put(var2.getEntityId(), new AutoDodge.TrackedPotion(var9, var8));
            }
         }
      }
   }

   private Map<String, AutoDodge.PotionHolder> helper2() {
      HashMap<String, AutoDodge.PotionHolder> var1 = new HashMap<>();

      for (AbstractClientPlayerEntity var3 : mc.world.getPlayers()) {
         ItemStack var4 = var3.getMainHandStack();
         ItemStack var5 = var3.getOffHandStack();
         int var6 = var4.getItem() == Items.SPLASH_POTION ? helper5(var4) : (var5.getItem() == Items.SPLASH_POTION ? helper5(var5) : -1);
         if (var6 >= 0 && var3 != mc.player && mc.player.squaredDistanceTo(var3) <= 400.0) {
            var1.put(var3.getName().getString(), new AutoDodge.PotionHolder(var6, var3.getPos(), var3.getRotationVec(1.0F)));
         }
      }

      return var1;
   }

   private static boolean helper3(PotionEntity potionEntity, Box expandedPlayer) {
      Vec3d var2 = potionEntity.getVelocity();
      Vec3d var3 = potionEntity.getPos();

      for (int var4 = 0;
         var4 < 70 && var2.lengthSquared() >= 1.0E-12 && var3.y >= mc.world.getBottomY() && var3.y <= mc.world.getBottomY() + mc.world.getHeight();
         var4++
      ) {
         double var5 = mc.world.getFluidState(BlockPos.ofFloored(var3)).isIn(FluidTags.WATER) ? 0.8 : 0.99;
         var2 = new Vec3d(var2.x * var5, (var2.y - 0.05) * var5, var2.z * var5);
         Vec3d var7 = var3.add(var2);
         RaycastContext var8 = new RaycastContext(var3, var7, ShapeType.COLLIDER, FluidHandling.NONE, potionEntity);
         BlockHitResult var9 = mc.world.raycast(var8);
         if (var9.getType() == Type.BLOCK) {
            return helper4(expandedPlayer, var3, var9.getPos());
         }

         if (helper4(expandedPlayer, var3, var7)) {
            return true;
         }

         var3 = var7;
      }

      return false;
   }

   private static boolean helper4(Box box, Vec3d first, Vec3d second) {
      return new Box(
            Math.min(first.x, second.x),
            Math.min(first.y, second.y),
            Math.min(first.z, second.z),
            Math.max(first.x, second.x),
            Math.max(first.y, second.y),
            Math.max(first.z, second.z)
         )
         .expand(0.12)
         .intersects(box);
   }

   private static int helper5(ItemStack itemStack) {
      PotionContentsComponent var1 = itemStack.get(DataComponentTypes.POTION_CONTENTS);
      return var1 != null ? var1.getColor() & 16777215 : 0;
   }

   private static int helper6() {
      for (int var0 = 0; var0 < 36; var0++) {
         if (mc.player.getInventory().getStack(var0).isOf(Items.DRIED_KELP)) {
            return var0;
         }
      }

      return -1;
   }

   private static Rotation helper7(Vec3d eye, Vec3d point) {
      Vec3d var2 = point.subtract(eye);
      double var3 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
      float var5 = (float)Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0F;
      float var6 = (float)(-Math.toDegrees(Math.atan2(var2.y, var3)));
      return new Rotation(MathHelper.wrapDegrees(var5), MathHelper.clamp(var6, -90.0F, 90.0F));
   }

   private static final class PotionHolder {
      final int a;
      final Vec3d b;
      final Vec3d c;

      PotionHolder(int color, Vec3d position, Vec3d lookDirection) {
         this.a = color;
         this.b = position;
         this.c = lookDirection;
      }
   }

   private static final class TrackedPotion {
      final int a;
      final String b;

      TrackedPotion(int color, String nick) {
         this.a = color;
         this.b = nick;
      }
   }
}
