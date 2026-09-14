package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventGameUpdate;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.api.utils.rotate.RotationUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MaceHelper extends Module {
   public static final MaceHelper INSTANCE = new MaceHelper();
   public final BooleanSetting maceAutoSwap = new BooleanSetting("Свап на булаву", true);
   private final FloatSetting floatSetting = new FloatSetting("Дист. булавы", 3.0F, 1.0F, 6.0F, 0.1F);
   private final FloatSetting floatSetting2 = new FloatSetting("Мин. высота над целью", 0.1F, 0.0F, 2.0F, 0.1F);
   private final BooleanSetting chargeSetting = new BooleanSetting("Заряд", true);
   private final FloatSetting chargeDelay = new FloatSetting("Задержка заряда", 20.0F, 5.0F, 100.0F, 1.0F);
   private LivingEntity livingEntity = null;
   private int index = 0;
   private int chargeCooldown = 0;

   public MaceHelper() {
      super("Mace Helper", "Логика булавы: свап, атака при падении, наводка", Module.ModuleCategory.COMBAT);
      this.addSettings(this.maceAutoSwap, this.floatSetting, this.floatSetting2, this.chargeSetting, this.chargeDelay);
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (this.isEnable()) {
         if (mc.player == null || mc.interactionManager == null) {
            this.livingEntity = null;
         } else if (this.index > 0) {
            this.index--;
         } else {
            boolean var2 = mc.player.isGliding();
            LivingEntity var3 = this.computeLivingEntity();
            if (this.maceAutoSwap.isState()
               && !mc.player.isOnGround()
               && !var2
               && !this.checkState()
               && this.resolveDouble() > 3.5
               && this.checkCondition(28.0)) {
               int var4 = this.resolveInt();
               if (var4 >= 0) {
                  mc.player.getInventory().selectedSlot = var4;
               }
            }

            if (mc.player.fallDistance <= 1.5F || var2) {
               this.livingEntity = null;
            } else if (!this.checkState()) {
               this.livingEntity = null;
            } else {
               this.livingEntity = var3;
               if (this.livingEntity != null) {
                  mc.interactionManager.attackEntity(mc.player, this.livingEntity);
                  mc.player.swingHand(Hand.MAIN_HAND);
                  this.index = 4;
               }
            }

            if (this.chargeCooldown > 0) {
               this.chargeCooldown--;
            } else if (this.chargeSetting.isState()) {
               this.throwChargeDown();
            }
         }
      }
   }

   @EventLink
   public void onGameUpdate(EventGameUpdate event) {
      if (this.isEnable()) {
         if (mc.player != null && mc.world != null) {
            if (this.livingEntity != null && this.checkState()) {
               this.updateState();
            }
         }
      }
   }

   private int resolveInt() {
      if (mc.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 <= 8; var1++) {
         ItemStack var2 = mc.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.getItem() instanceof MaceItem) {
            return var1;
         }
      }

      return -1;
   }

   private double resolveDouble() {
      if (mc.player != null && mc.world != null) {
         BlockPos var1 = mc.player.getBlockPos();

         for (int var2 = 1; var2 <= 24; var2++) {
            if (!mc.world.getBlockState(var1.down(var2)).isAir()) {
               return var2 - 1;
            }
         }

         return 24.0;
      } else {
         return 0.0;
      }
   }

   private boolean checkCondition(double range) {
      if (mc.player != null && mc.world != null) {
         double var3 = range * range;

         for (PlayerEntity var6 : mc.world.getPlayers()) {
            if (var6 != mc.player
               && var6.isAlive()
               && !Lumen.INSTANCE.friendStorage.isFriend(var6.getName().getString())
               && mc.player.squaredDistanceTo(var6) < var3) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private LivingEntity computeLivingEntity() {
      if (mc.player == null || mc.world == null) {
         return null;
      }

      if (!Aura.INSTANCE.isEnable()) {
         return null;
      }

      double var1 = this.floatSetting.getValue().floatValue();
      double var3 = var1 * var1;
      double var5 = this.floatSetting2.getValue().floatValue();
      boolean var7 = mc.player.fallDistance > 1.5F;
      LivingEntity var8 = Aura.INSTANCE.getTarget();
      return this.checkCondition2(var8, var3, var5, var7) ? var8 : null;
   }

   private boolean checkCondition2(LivingEntity entity, double rangeSq, double minY, boolean smashReady) {
      if (entity == null || entity == mc.player) {
         return false;
      }

      if (!entity.isAlive()) {
         return false;
      }

      if (entity instanceof ArmorStandEntity) {
         return false;
      }

      if (entity instanceof PlayerEntity && Lumen.INSTANCE.friendStorage.isFriend(entity.getName().getString())) {
         return false;
      }

      double var7 = mc.player.getY() - entity.getY();
      return !smashReady && var7 < minY ? false : mc.player.squaredDistanceTo(entity) < rangeSq;
   }

   private void throwChargeDown() {
      if (mc.player == null || mc.interactionManager == null) {
         return;
      }

      Hand hand = null;
      int slot = -1;
      if (!mc.player.getOffHandStack().isEmpty() && mc.player.getOffHandStack().isOf(Items.WIND_CHARGE)) {
         hand = Hand.OFF_HAND;
      } else {
         for (int i = 0; i <= 8; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isOf(Items.WIND_CHARGE)) {
               slot = i;
               break;
            }
         }

         if (slot < 0) {
            return;
         }

         hand = Hand.MAIN_HAND;
      }

      int prevSlot = mc.player.getInventory().selectedSlot;
      if (slot >= 0) {
         mc.player.getInventory().selectedSlot = slot;
      }

      float prevPitch = mc.player.getPitch();
      float yaw = mc.player.getYaw();
      mc.player.setPitch(90.0F);
      RotationStorage.update(new Rotation(yaw, 90.0F), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
      mc.interactionManager.interactItem(mc.player, hand);
      mc.player.swingHand(hand);
      mc.player.setPitch(prevPitch);
      if (slot >= 0) {
         mc.player.getInventory().selectedSlot = prevSlot;
      }

      this.chargeCooldown = Math.max(1, Math.round(this.chargeDelay.getValue().floatValue()));
   }

    private void updateState() {
      if (this.livingEntity != null && mc.player != null) {
         Vec3d var1 = this.livingEntity.getBoundingBox().getCenter();
         Vec2f var2 = RotationUtils.getRotations(var1);
         RotationStorage.update(new Rotation(var2.x, var2.y), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
      }
   }

   private boolean checkState() {
      if (mc.player == null) {
         return false;
      }

      ItemStack var1 = mc.player.getMainHandStack();
      return !var1.isEmpty() && var1.getItem() instanceof MaceItem;
   }

   @Override
   public void onEnable() {
      this.livingEntity = null;
      this.index = 0;
      this.chargeCooldown = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.livingEntity = null;
      this.index = 0;
      this.chargeCooldown = 0;
      super.onDisable();
   }
}
