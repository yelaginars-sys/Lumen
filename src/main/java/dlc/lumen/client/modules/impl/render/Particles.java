package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.events.implement.EventPopTotem;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.particles.HitParticle;
import dlc.lumen.api.utils.render.particles.ParticleRenderer;
import dlc.lumen.api.utils.render.particles.ParticleShape;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.Vec3d;

public class Particles extends Module {
   public static Particles INSTANCE = new Particles();
   private static final int[] floatSetting = new int[]{
      -65536, -33024, -256, -16711936, -16711681, -16776961, -7667713, -65281, -60269, -1, -16711681, -40121
   };
   private static final int[] INDEX = new int[]{-8585472, -10496, -13369550, -23296, -16711936, -5374161};
   private static final int INDEX2 = 20;
   private static final float VOLUME = 7.5F;
   private final ModeSetting modeSetting = new ModeSetting(
      "Режим", "Звезда", "Кубы", "Корона", "Куб", "Доллар", "Сердце", "Молния", "Линия", "Ромб", "Снежинка", "Звезда", "Звезда 2", "Треугольник", "Рандом"
   );
   private final ModeSetting modeSetting2 = new ModeSetting("Свечение", "Bloom Sample", "Bloom", "Bloom Sample", "Оба");
   private final ListSetting listSetting = new ListSetting(
      "Триггеры",
      new BooleanSetting("Удар", true),
      new BooleanSetting("Тотем", true),
      new BooleanSetting("Ходьба", true),
      new BooleanSetting("Бросаемый предмет", true)
   );
   private final FloatSetting floatSetting2 = new FloatSetting("Количество", 40.0F, 10.0F, 40.0F, 1.0F).visible(() -> this.listSetting.is("Удар"));
   private final FloatSetting floatSetting3 = new FloatSetting("Кол-во при ходьбе", 30.0F, 10.0F, 30.0F, 1.0F)
      .visible(() -> this.listSetting.is("Ходьба"));
   private final FloatSetting floatSetting4 = new FloatSetting("Разброс", 1.0F, 0.5F, 3.0F, 0.1F);
   private final FloatSetting floatSetting5 = new FloatSetting("Скорость", 2.0F, 0.1F, 3.0F, 0.1F);
   private final FloatSetting floatSetting6 = new FloatSetting("Время жизни", 2.5F, 0.5F, 10.0F, 0.1F);
   private final FloatSetting floatSetting7 = new FloatSetting("Размер", 1.0F, 0.1F, 1.0F, 0.05F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Физика", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Рандомный цвет", false);
   private final List<HitParticle> hitParticles = new ArrayList<>();
   private final List<Particles.TotemBurst> particless = new ArrayList<>();
   private final Queue<PlayerEntity> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private float volume;

   public Particles() {
      super("Particles", "Частицы при ударах, тотемах, ходьбе и снарядах", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.modeSetting,
         this.modeSetting2,
         this.listSetting,
         this.floatSetting2,
         this.floatSetting3,
         this.floatSetting4,
         this.floatSetting5,
         this.floatSetting6,
         this.floatSetting7,
         this.booleanSetting,
         this.booleanSetting2
      );
   }

   @Override
   public void onDisable() {
      this.hitParticles.clear();
      this.particless.clear();
      this.concurrentLinkedQueue.clear();
      this.volume = 0.0F;
      super.onDisable();
   }

   @EventLink
   public void onAttack(EventAttackEntity event) {
      if (mc.player != null && mc.world != null) {
         if (this.listSetting.is("Удар") && event.getTarget() != null) {
            Entity var2 = event.getTarget();
            float var3 = this.floatSetting4.get() * 0.15F;
            float var4 = this.floatSetting5.get();
            int var5 = (int)this.floatSetting2.get();

            for (int var6 = 0; var6 < var5; var6++) {
               Vec3d var7 = new Vec3d(var2.getX(), var2.getY() + Math.random() * var2.getHeight(), var2.getZ());
               Vec3d var8 = new Vec3d(
                  (Math.random() - 0.5) * 2.0 * var3 * var4, (Math.random() - 0.5) * 2.0 * var3 * var4, (Math.random() - 0.5) * 2.0 * var3 * var4
               );
               this.helper4(var7, var8, this.helper5(), this.floatSetting7.get(), this.floatSetting6.get(), 0.99F);
            }
         }
      }
   }

   @EventLink
   public void onPopTotem(EventPopTotem event) {
      if (this.listSetting.is("Тотем") && event.getPlayer() != null) {
         this.concurrentLinkedQueue.add(event.getPlayer());
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (this.listSetting.is("Ходьба")) {
            this.helper();
         }

         if (this.listSetting.is("Бросаемый предмет")) {
            this.helper2();
         }

         PlayerEntity var2;
         while ((var2 = this.concurrentLinkedQueue.poll()) != null) {
            this.particless.add(new Particles.TotemBurst(var2));
         }

         Iterator var3 = this.particless.iterator();

         while (var3.hasNext()) {
            Particles.TotemBurst var4 = (Particles.TotemBurst)var3.next();
            var4.INDEX++;
            if (var4.helper()) {
               this.helper3(var4.floatSetting, var4.helper2());
            } else {
               var3.remove();
            }
         }

         this.hitParticles.removeIf(particle -> {
            particle.tick();
            return particle.isDead();
         });
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      ParticleRenderer.render(
         this.hitParticles, event.getMatrices(), event.getTickDelta(), 0.25F, true, 7.5F, this.helper7(), this.booleanSetting.isState(), false
      );
   }

   private void helper() {
      boolean var1 = mc.player.getVelocity().lengthSquared() > 1.0E-4 && !mc.player.isSneaking();
      if (!var1) {
         this.volume = 0.0F;
      } else {
         this.volume = this.volume + this.floatSetting3.get() / 20.0F;
         int var2 = (int)this.volume;
         this.volume -= var2;
         if (var2 > 0) {
            double var3 = Math.toRadians(mc.player.getYaw() + 90.0F);
            double var5 = Math.cos(var3) * 0.5;
            double var7 = Math.sin(var3) * 0.5;
            float var9 = this.floatSetting4.get() * 0.05F;
            float var10 = this.floatSetting5.get();

            for (int var11 = 0; var11 < var2; var11++) {
               Vec3d var12 = new Vec3d(
                  mc.player.getX() - var5 + (Math.random() - 0.5) * 0.3,
                  mc.player.getY() + 0.3 + Math.random() * (mc.player.getHeight() - 0.3),
                  mc.player.getZ() - var7 + (Math.random() - 0.5) * 0.3
               );
               Vec3d var13 = new Vec3d((Math.random() - 0.5) * var9 * var10, (Math.random() - 0.5) * var9 * 0.5 * var10, (Math.random() - 0.5) * var9 * var10);
               this.helper4(var12, var13, this.helper5(), this.floatSetting7.get() * 0.6F, this.floatSetting6.get() * 0.5F, 0.99F);
            }
         }
      }
   }

   private void helper2() {
      float var1 = this.floatSetting4.get() * 0.03F;
      float var2 = this.floatSetting5.get();

      for (Entity var4 : mc.world.getEntities()) {
         if (var4 instanceof ThrownEntity || var4 instanceof ArrowEntity || var4 instanceof TridentEntity) {
            ProjectileEntity var5 = (ProjectileEntity)var4;
            boolean var6 = Math.abs(var5.getX() - var5.prevX) > 0.01 || Math.abs(var5.getY() - var5.prevY) > 0.01 || Math.abs(var5.getZ() - var5.prevZ) > 0.01;
            if (var6 || !(var5.getVelocity().lengthSquared() <= 0.01)) {
               for (int var7 = 0; var7 < 2; var7++) {
                  Vec3d var8 = new Vec3d(
                     var5.getX() + (Math.random() - 0.5) * 0.5, var5.getY() + Math.random() * var5.getHeight(), var5.getZ() + (Math.random() - 0.5) * 0.5
                  );
                  Vec3d var9 = new Vec3d(
                     (Math.random() - 0.5) * 2.0 * var1 * var2, (Math.random() - 0.5) * 2.0 * var1 * var2, (Math.random() - 0.5) * 2.0 * var1 * var2
                  );
                  this.helper4(var8, var9, this.helper5(), this.floatSetting7.get() * 0.5F, this.floatSetting6.get() * 0.3F, 0.99F);
               }
            }
         }
      }
   }

   private void helper3(Entity entity, float progress) {
      if (entity != null && !entity.isRemoved()) {
         float var3 = 1.0F - progress * 0.5F;
         float var4 = this.floatSetting4.get();
         float var5 = this.floatSetting5.get();

         for (int var6 = 0; var6 < 4; var6++) {
            double var7 = Math.random() * 2.0 - 1.0;
            double var9 = Math.random() * 2.0 - 1.0;
            double var11 = Math.random() * 2.0 - 1.0;
            if (!(var7 * var7 + var9 * var9 + var11 * var11 > 1.0)) {
               Vec3d var13 = new Vec3d(
                  entity.getX() + var7 * entity.getWidth() * 0.5,
                  entity.getBodyY(0.5) + var9 * entity.getHeight() * 0.5,
                  entity.getZ() + var11 * entity.getWidth() * 0.5
               );
               double var14 = var4 * 0.18 * var3 * var5;
               double var16 = Math.random() < 0.4 ? (0.15 + Math.random() * 0.2) * var5 : (0.03 + Math.random() * 0.07) * var5;
               Vec3d var18 = new Vec3d(var7 * var14, var16, var11 * var14);
               int var19 = INDEX[ThreadLocalRandom.current().nextInt(INDEX.length)];
               this.helper4(var13, var18, var19, this.floatSetting7.get() * 0.8F, this.floatSetting6.get() * 0.8F, 0.98F);
            }
         }
      }
   }

   private void helper4(Vec3d position, Vec3d velocity, int color, float particleSize, float life, float damping) {
      this.hitParticles
         .add(
            new HitParticle(position, velocity, color, particleSize, life)
               .gravity(this.booleanSetting.isState() ? 0.04F : 0.0F)
               .collide(this.booleanSetting.isState())
               .damping(damping)
               .shape(this.helper6())
         );
   }

   private int helper5() {
      return this.booleanSetting2.isState() ? floatSetting[ThreadLocalRandom.current().nextInt(floatSetting.length)] : ColorUtils.getThemeColor();
   }

   private ParticleShape helper6() {
      return switch (this.modeSetting.getCurrent()) {
         case "Кубы" -> ParticleShape.CUBES;
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
         case "Рандом" -> ParticleShape.RANDOM;
         default -> ParticleShape.STAR;
      };
   }

   private ParticleRenderer.GlowStyle helper7() {
      return switch (this.modeSetting2.getCurrent()) {
         case "Bloom" -> ParticleRenderer.GlowStyle.SOFT;
         case "Bloom Sample" -> ParticleRenderer.GlowStyle.BRIGHT;
         default -> ParticleRenderer.GlowStyle.BOTH;
      };
   }

   private static final class TotemBurst {
      private final Entity floatSetting;
      private int INDEX;

      private TotemBurst(Entity entity) {
         this.floatSetting = entity;
      }

      private boolean helper() {
         return this.INDEX < 20 && this.floatSetting != null && !this.floatSetting.isRemoved();
      }

      private float helper2() {
         return this.INDEX / 20.0F;
      }
   }
}
