package dlc.lumen.api.storages.implement;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventPopTotem;
import dlc.lumen.api.events.implement.EventTickPre;
import java.lang.reflect.InvocationTargetException;
import lombok.Generated;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class ServerStorage implements QClient {
   private int yaw;
   private float x;
   private float serverPitch;
   private float y;
   private double serverX;
   private double serverY;
   private double serverZ;
   private boolean serverOnGround;
   private boolean cancelled;
   private boolean serverSneaking;
   private boolean serverHorizontalCollision;

   public void ServerManager() {
      EventInvoker.register(this);
   }

   @EventLink
   public void onTick(EventTickPre e) {
      if (mc.player != null && mc.world != null) {
         double var2 = mc.player.prevY - mc.player.getY();
         if (mc.player.isOnGround()) {
            this.y = 0.0F;
         } else if (var2 > 0.0) {
            this.y += (float)var2;
         }
      }
   }

   @EventLink
   public void onPacketSend(EventPacket e) {
      if (mc.player != null && mc.world != null) {
         if (e.getPacket() instanceof PlayerMoveC2SPacket var2) {
            if (var2.changesPosition()) {
               this.serverX = var2.getX(mc.player.getX());
               this.serverY = var2.getY(mc.player.getY());
               this.serverZ = var2.getZ(mc.player.getZ());
            }

            if (var2.changesLook()) {
               this.x = var2.getYaw(mc.player.getYaw());
               this.serverPitch = var2.getPitch(mc.player.getPitch());
            }

            this.serverOnGround = var2.isOnGround();
            this.serverHorizontalCollision = var2.horizontalCollision();
         }

         if (e.getPacket() instanceof UpdateSelectedSlotC2SPacket var4) {
            this.yaw = var4.getSelectedSlot();
         }

         if (e.getPacket() instanceof ClientCommandC2SPacket var5) {
            switch (var5.getMode()) {
               case START_SPRINTING:
                  this.cancelled = true;
                  break;
               case STOP_SPRINTING:
                  e.setCancelled(!this.cancelled);
                  if (!e.isCancelled()) {
                     this.cancelled = false;
                  }
                  break;
               case PRESS_SHIFT_KEY:
                  this.serverSneaking = true;
                  break;
               case RELEASE_SHIFT_KEY:
                  this.serverSneaking = false;
            }
         }
      }
   }

   @EventLink
   public void onPacketReceive(EventPacket e) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (mc.player != null && mc.world != null) {
         if (e.getPacket() instanceof EntityStatusS2CPacket var2 && var2.getStatus() == 35) {
            if (!(var2.getEntity(mc.world) instanceof PlayerEntity var5)) {
               return;
            }

            EventInvoker.invoke(new EventPopTotem(var5));
         }
      }
   }

   @Generated
   public int getServerSlot() {
      return this.yaw;
   }

   @Generated
   public float getServerYaw() {
      return this.x;
   }

   @Generated
   public float getServerPitch() {
      return this.serverPitch;
   }

   @Generated
   public float getFallDistance() {
      return this.y;
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
   public boolean isServerOnGround() {
      return this.serverOnGround;
   }

   @Generated
   public boolean isServerSprinting() {
      return this.cancelled;
   }

   @Generated
   public boolean isServerSneaking() {
      return this.serverSneaking;
   }

   @Generated
   public boolean isServerHorizontalCollision() {
      return this.serverHorizontalCollision;
   }
}
