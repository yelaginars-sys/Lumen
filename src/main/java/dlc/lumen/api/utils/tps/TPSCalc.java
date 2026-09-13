package dlc.lumen.api.utils.tps;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class TPSCalc {
   private float fvalue = 20.0F;
   private float adjustTicks = 0.0F;
   private long timestamp;
   private long lastPacketTime;
   private static final int count = 20;
   private final float[] tpsSamples = new float[20];
   private int sampleIndex = 0;

   @EventLink
   public void onPacket(EventPacket e) {
      if (e.getType() == EventPacket.Type.RECEIVE && e.getPacket() instanceof WorldTimeUpdateS2CPacket) {
         this.getFvalue();
      }
   }

   public float getTPS() {
      if (this.lastPacketTime == 0L) {
         return this.fvalue;
      }

      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.getNetworkHandler() != null && System.currentTimeMillis() - this.lastPacketTime <= 3500L ? this.fvalue : 20.0F;
   }

   private void getFvalue() {
      long var1 = System.nanoTime();
      this.lastPacketTime = System.currentTimeMillis();
      if (this.timestamp == 0L) {
         this.timestamp = var1;
      } else {
         long var3 = var1 - this.timestamp;
         this.timestamp = var1;
         if (var3 > 0L) {
            float var5 = 20.0F;
            float var6 = var5 * (1.0E9F / (float)var3);
            float var7 = MathHelper.clamp(var6, 0.0F, var5);
            this.tpsSamples[this.sampleIndex % 20] = var7;
            this.sampleIndex++;
            int var8 = Math.min(this.sampleIndex, 20);
            float var9 = 0.0F;

            for (int var10 = 0; var10 < var8; var10++) {
               float var11 = this.tpsSamples[var10];
               var9 += var11;
            }

            this.fvalue = (float)this.round(var9 / var8);
            this.adjustTicks = this.fvalue - var5;
         }
      }
   }

   public double round(double input) {
      return Math.round(input * 10.0) / 10.0;
   }

   @Generated
   public float getAdjustTicks() {
      return this.adjustTicks;
   }

   @Generated
   public long getTimestamp() {
      return this.timestamp;
   }

   @Generated
   public long getLastPacketTime() {
      return this.lastPacketTime;
   }

   @Generated
   public float[] getTpsSamples() {
      return this.tpsSamples;
   }

   @Generated
   public int getSampleIndex() {
      return this.sampleIndex;
   }
}
