package dlc.lumen.client.modules.impl.misc;

import com.mojang.authlib.GameProfile;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import java.util.UUID;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FakePlayer extends Module {
   public static FakePlayer INSTANCE = new FakePlayer();
   private final BindSetting bindSetting = new BindSetting("Кнопка спавна", -1);
   private final BooleanSetting booleanSetting = new BooleanSetting("Урон и смерть", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Авто-респавн", true).visible(() -> this.booleanSetting.isState());
   private OtherClientPlayerEntity fakePlayer = null;
   private boolean flag = false;
   private int index = 0;
   private int index2 = 0;

   public FakePlayer() {
      super("FakePlayer", "Спавнит фейкового игрока для тренировки", Module.ModuleCategory.MISC);
      this.addSettings(this.bindSetting, this.booleanSetting, this.booleanSetting2);
   }

   @EventLink
   public void onEvent(EventBinding event) {
      if (mc.player != null && mc.world != null) {
         if (event.getKey() == this.bindSetting.getKey()) {
            this.flag = true;
            this.index = 2;
         }
      }
   }

   @EventLink
   public void onEvent(EventAttackEntity event) {
      if (this.fakePlayer != null && event.getTarget() == this.fakePlayer) {
         if (mc.player != null && mc.world != null) {
            event.cancel();
            mc.player.resetLastAttackedTicks();
            double var2 = this.fakePlayer.getX() - mc.player.getX();
            double var4 = this.fakePlayer.getZ() - mc.player.getZ();
            float var6 = (float)(MathHelper.atan2(-var4, -var2) * (180.0 / Math.PI)) - this.fakePlayer.getYaw();
            this.fakePlayer.animateDamage(var6);
            double var7 = mc.player.isSprinting() ? 0.9 : 0.4;
            this.fakePlayer.takeKnockback(var7, -var2, -var4);
            if (this.booleanSetting.isState()) {
               float var9 = this.helper();
               float var10 = this.fakePlayer.getHealth() - var9;
               if (var10 <= 0.0F) {
                  this.helper2();
               } else {
                  this.fakePlayer.setHealth(var10);
               }
            }
         }
      }
   }

   private float helper() {
      float var1 = (float)mc.player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
      float var2 = mc.player.getAttackCooldownProgress(0.5F);
      return Math.max(0.5F, var1 * (0.2F + var2 * var2 * 0.8F));
   }

   private void helper2() {
      if (this.fakePlayer != null && mc.world != null) {
         this.removeFakePlayer();
         if (this.booleanSetting2.isState()) {
            this.index2 = 12;
         }
      }
   }

   @EventLink
   public void onEvent(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.index2 > 0 && --this.index2 == 0) {
            this.helper3();
         }

         if (this.flag) {
            if (this.index > 0) {
               this.index--;
            } else {
               this.flag = false;
               this.index = 0;
               this.helper3();
            }
         }
      }
   }

   private void helper3() {
      if (mc.player != null && mc.world != null) {
         this.removeFakePlayer();
         GameProfile var1 = new GameProfile(UUID.randomUUID(), "FakePlayer_" + System.currentTimeMillis() % 10000L);
         this.fakePlayer = new OtherClientPlayerEntity(mc.world, var1);
         double var2 = Math.toRadians(mc.player.getYaw());
         double var4 = mc.player.getX() - Math.sin(var2) * 2.0;
         double var6 = mc.player.getZ() + Math.cos(var2) * 2.0;
         double var8 = mc.player.getY();
         BlockPos var10 = new BlockPos((int)var4, (int)var8, (int)var6);
         if (!mc.world.getBlockState(var10.down()).isAir()) {
            this.fakePlayer.refreshPositionAndAngles(var4, var8, var6, mc.player.getYaw() + 180.0F, 0.0F);
         } else {
            BlockPos var11 = var10;

            while (var11.getY() > 0 && mc.world.getBlockState(var11.down()).isAir()) {
               var11 = var11.down();
            }

            this.fakePlayer.refreshPositionAndAngles(var4, var11.getY(), var6, mc.player.getYaw() + 180.0F, 0.0F);
         }

         this.fakePlayer.getInventory().clone(mc.player.getInventory());
         this.fakePlayer.setHealth(20.0F);
         this.fakePlayer.setInvulnerable(false);
         mc.world.addEntity(this.fakePlayer);
      }
   }

   public void removeFakePlayer() {
      if (this.fakePlayer != null && mc.world != null) {
         mc.world.removeEntity(this.fakePlayer.getId(), RemovalReason.DISCARDED);
         this.fakePlayer = null;
      }
   }

   public PlayerEntity getFakePlayer() {
      return this.fakePlayer;
   }

   public boolean isFakePlayerAlive() {
      return this.fakePlayer != null && this.fakePlayer.isAlive();
   }

   @Override
   public void onDisable() {
      this.removeFakePlayer();
      this.flag = false;
      this.index = 0;
      this.index2 = 0;
      super.onDisable();
   }
}
