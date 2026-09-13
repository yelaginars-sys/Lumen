package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.world.ClientWorld;

public class ChunkAnimator extends Module {
   public static final ChunkAnimator INSTANCE = new ChunkAnimator();
   private static final double LEVEL = 16.0;
   private final FloatSetting floatSetting = new FloatSetting("Скорость", 0.6F, 0.1F, 2.0F, 0.05F);
   private final ModeSetting modeSetting = new ModeSetting("Направление", "Снизу", "Снизу", "Сверху");
   private final Map<Long, Long> longs = new ConcurrentHashMap<>();
   private final Set<Long> set = ConcurrentHashMap.newKeySet();
   private ClientWorld clientWorld;

   private ChunkAnimator() {
      super("ChunkAnimator", "Анимация появления чанков", Module.ModuleCategory.RENDER);
      this.addSettings(this.floatSetting, this.modeSetting);
   }

   @Override
   public void onDisable() {
      this.longs.clear();
      this.set.clear();
      this.clientWorld = null;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      this.helper();
      if (mc.world != this.clientWorld) {
         this.clientWorld = mc.world;
         this.longs.clear();
         this.set.clear();
      }
   }

   public void onChunkBuilt(long sectionPos) {
      if (this.isEnable() && mc.world != null) {
         if (this.set.add(sectionPos)) {
            this.longs.putIfAbsent(sectionPos, System.currentTimeMillis());
         }

         this.helper();
      }
   }

   public double getOffsetY(long sectionPos) {
      if (!this.isEnable()) {
         return 0.0;
      } else {
         Long var3 = this.longs.get(sectionPos);
         if (var3 == null) {
            return 0.0;
         } else {
            double var4 = (System.currentTimeMillis() - var3) / (this.floatSetting.get() * 1000.0);
            if (var4 >= 1.0) {
               this.longs.remove(sectionPos);
               return 0.0;
            } else {
               double var6 = 1.0 - Math.pow(1.0 - var4, 3.0);
               double var8 = (1.0 - var6) * 16.0;
               return this.modeSetting.is("Сверху") ? var8 : -var8;
            }
         }
      }
   }

   private void helper() {
      long var1 = (long)(this.floatSetting.get() * 1000.0);
      long var3 = System.currentTimeMillis();
      this.longs.entrySet().removeIf(entry -> var3 - entry.getValue() >= var1);
   }
}
