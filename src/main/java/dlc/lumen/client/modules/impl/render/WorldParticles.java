package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.StopWatch;
import dlc.lumen.api.utils.render.particles.ParticleRenderer;
import dlc.lumen.api.utils.render.particles.ParticleShape;
import dlc.lumen.api.utils.render.particles.WorldParticle;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public class WorldParticles extends Module {
   public static WorldParticles INSTANCE = new WorldParticles();
   private static final double LEVEL = 3.0;
   private static final double LEVEL2 = 60.0;
   private static final double LEVEL3 = 4225.0;
   private static final long TIMESTAMP = 40L;
   private final ModeSetting modeSetting = new ModeSetting(
      "Режим",
      "Звезда",
      "3D Кубы",
      "Корона",
      "Куб",
      "Доллар",
      "Сердце",
      "Молния",
      "Линия",
      "Ромб",
      "Снежинка",
      "Звезда",
      "Звезда 2",
      "Треугольник",
      "Свечение",
      "Рандом"
   );
   private final FloatSetting floatSetting = new FloatSetting("Количество", 100.0F, 10.0F, 500.0F, 10.0F);
   private final FloatSetting floatSetting2 = new FloatSetting("Время жизни", 10.0F, 2.0F, 60.0F, 1.0F);
   private final FloatSetting floatSetting3 = new FloatSetting("Размер", 1.5F, 0.1F, 1.5F, 0.05F);
   private final FloatSetting floatSetting4 = new FloatSetting("Свечение", 3.0F, 0.1F, 5.0F, 0.1F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Физика", false);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Рандомный цвет", false);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Белые при спавне", true);
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Белый центр", false).visible(() -> !this.modeSetting.is("3D Кубы"));
   private final List<WorldParticle> worldParticles = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   private Vec3d vec3d = Vec3d.ZERO;
   private boolean flag;
   private Vec3d vec3d2 = Vec3d.ZERO;
   private double level;

   public WorldParticles() {
      super("WorldParticles", "Фоновые частицы вокруг игрока", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.modeSetting,
         this.floatSetting,
         this.floatSetting2,
         this.floatSetting3,
         this.floatSetting4,
         this.booleanSetting,
         this.booleanSetting2,
         this.booleanSetting3,
         this.booleanSetting4
      );
   }

   @Override
   public void onDisable() {
      this.worldParticles.clear();
      this.vec3d = Vec3d.ZERO;
      this.flag = false;
      this.vec3d2 = Vec3d.ZERO;
      this.level = 0.0;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         Vec3d var2 = mc.player.getPos();
         if (this.flag) {
            this.vec3d2 = var2.subtract(this.vec3d);
            this.level = this.vec3d2.horizontalLength();
         }

         this.vec3d = var2;
         this.flag = true;
         this.worldParticles.removeIf(particle -> {
            particle.tick();
            return particle.isDead();
         });

         for (WorldParticle var4 : this.worldParticles) {
            if (!var4.isDying() && var4.horizontalSquaredDistanceTo(var2.x, var2.z) > 4225.0) {
               var4.kill();
            }
         }

         int var9 = (int)this.floatSetting.get();
         if (this.worldParticles.size() < var9 && this.stopWatch.isReached(this.helper())) {
            int var10 = this.helper2(var9);
            long var5 = (long)(this.floatSetting2.get() * 1000.0F);
            ParticleShape var7 = this.helper4();

            for (int var8 = 0; var8 < var10 && this.worldParticles.size() < var9; var8++) {
               this.worldParticles.add(this.helper3(var2, var5, var7));
            }

            this.stopWatch.reset();
         }
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (!this.worldParticles.isEmpty()) {
         int var2 = ColorUtils.getThemeColor();
         boolean var3 = this.booleanSetting2.isState();
         boolean var4 = this.booleanSetting3.isState();

         for (WorldParticle var6 : this.worldParticles) {
            var6.tint(var2, var3, var4);
         }

         ParticleRenderer.render(
            this.worldParticles,
            event.getMatrices(),
            event.getTickDelta(),
            0.5F,
            false,
            this.floatSetting4.get(),
            ParticleRenderer.GlowStyle.BOTH,
            false,
            this.booleanSetting4.isState()
         );
      }
   }

   private long helper() {
      if (this.level <= 0.05) {
         return 40L;
      }

      double var1 = Math.min(this.level * 5.0, 4.0);
      return Math.max(8L, (long)(40.0 / (1.0 + var1)));
   }

   private int helper2(int limit) {
      if (this.level <= 0.1) {
         return 1;
      }

      int var2 = Math.min(8, limit - this.worldParticles.size());
      return Math.max(1, (int)(var2 * Math.min(this.level * 5.0, 1.0)));
   }

   private WorldParticle helper3(Vec3d position, long life, ParticleShape current) {
      double var5 = 3.0 + Math.random() * 57.0;
      double var7 = Math.random() * Math.PI * 2.0;
      double var9 = position.x;
      double var11 = position.z;
      if (this.level > 0.05 && this.vec3d2.horizontalLength() > 0.01) {
         Vec3d var13 = this.vec3d2.normalize();
         double var14 = Math.PI * 2.0 / 5.0;
         var7 = Math.atan2(var13.z, var13.x) + (Math.random() - 0.5) * var14 * 2.0;
         double var16 = var5 * 0.7 * Math.min(this.level * 8.0, 1.0);
         var9 += var13.x * var16;
         var11 += var13.z * var16;
      }

      double var19 = var9 + Math.cos(var7) * var5;
      double var15 = var11 + Math.sin(var7) * var5;
      double var17 = position.y - 5.0 + Math.random() * 25.0;
      return new WorldParticle(var19, var17, var15, (Math.random() - 0.5) * 0.08, (Math.random() - 0.5) * 0.02, (Math.random() - 0.5) * 0.08, life)
         .gravity(this.booleanSetting.isState())
         .size(this.floatSetting3.get())
         .shape(current);
   }

   private ParticleShape helper4() {
      return switch (this.modeSetting.getCurrent()) {
         case "3D Кубы" -> ParticleShape.CUBES;
         case "Корона" -> ParticleShape.CROWN;
         case "Куб" -> ParticleShape.CUBE_BLAST;
         case "Доллар" -> ParticleShape.DOLLAR;
         case "Сердце" -> ParticleShape.HEART;
         case "Молния" -> ParticleShape.LIGHTNING;
         case "Линия" -> ParticleShape.LINE;
         case "Ромб" -> ParticleShape.RHOMBUS;
         case "Снежинка" -> ParticleShape.SNOWFLAKE;
         case "Звезда 2" -> ParticleShape.STAR_ALT;
         case "Треугольник" -> ParticleShape.TRIANGLE;
         case "Свечение" -> ParticleShape.GLOW;
         case "Рандом" -> ParticleShape.RANDOM;
         default -> ParticleShape.STAR;
      };
   }
}
