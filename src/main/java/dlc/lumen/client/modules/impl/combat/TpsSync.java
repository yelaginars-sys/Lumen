package dlc.lumen.client.modules.impl.combat;

import dlc.lumen.Lumen;
import dlc.lumen.client.modules.Module;
import net.minecraft.util.math.MathHelper;

public class TpsSync extends Module {
   public static TpsSync INSTANCE = new TpsSync();

   public TpsSync() {
      super("TpsSync", "Синхронизация с TPS сервера", Module.ModuleCategory.COMBAT);
   }

   public float getCurrentTPS() {
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.tpsCalc != null) {
         float var1 = Lumen.INSTANCE.tpsCalc.getTPS();
         return MathHelper.clamp(var1, 0.1F, 20.0F);
      } else {
         return 20.0F;
      }
   }

   public long getAdjustedCooldown(long baseCooldown) {
      if (!this.isEnable()) {
         return baseCooldown;
      }

      float var3 = this.getCurrentTPS();
      if (var3 >= 20.0F) {
         return baseCooldown;
      }

      float var4 = 20.0F / var3;
      float var5 = 1.0F + (20.0F - var3) * 0.05F;
      long var6 = (long)((float)baseCooldown * var4 * var5);
      return Math.min(var6, 3000L);
   }
}
