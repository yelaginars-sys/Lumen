package dlc.lumen.client.ui.mainmenu;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public final class MenuParticles {
   private static final List<MenuParticles.P> PARTICLES = new ArrayList<>();
   private static final int MAX_PARTICLES = 320;
   private static double lastMouseX = -1.0;
   private static double lastMouseY = -1.0;
   private static double spawnDebt;
   private static double ambientDebt;

   private MenuParticles() {
   }

   public static void render(DrawContext context, int width, int height, double mouseX, double mouseY, float frameDelta, float alphaScale) {
      double dt = MathHelper.clamp(frameDelta, 0.001, 0.05);
      MatrixStack matrices = context.getMatrices();
      int theme;
      try {
         theme = ColorUtils.getThemeColor();
      } catch (Exception var22) {
         theme = -1;
      }

      if (lastMouseX >= 0.0) {
         double dx = mouseX - lastMouseX;
         double dy = mouseY - lastMouseY;
         double dist = Math.sqrt(dx * dx + dy * dy);
         spawnDebt += dist / 4.0;

         while (spawnDebt >= 1.0 && PARTICLES.size() < MAX_PARTICLES) {
            spawnDebt -= 1.0;
            PARTICLES.add(new MenuParticles.P(
               mouseX + (Math.random() - 0.5) * 6.0,
               mouseY + (Math.random() - 0.5) * 6.0,
               dx * 1.4 + (Math.random() - 0.5) * 34.0,
               dy * 1.4 + (Math.random() - 0.5) * 34.0 - 10.0,
               0.35 + Math.random() * 0.55,
               0.7 + Math.random() * 1.4,
               Math.random() < 0.25 ? -1 : theme
            ));
         }
      }

      lastMouseX = mouseX;
      lastMouseY = mouseY;
      ambientDebt += dt * 10.0;

      while (ambientDebt >= 1.0 && PARTICLES.size() < MAX_PARTICLES) {
         ambientDebt -= 1.0;
         PARTICLES.add(new MenuParticles.P(
            Math.random() * width,
            height + 4.0,
            (Math.random() - 0.5) * 14.0,
            -16.0 - Math.random() * 20.0,
            1.2 + Math.random() * 1.6,
            0.6 + Math.random() * 1.1,
            Math.random() < 0.3 ? -1 : theme
         ));
      }

      Iterator<MenuParticles.P> iterator = PARTICLES.iterator();

      while (iterator.hasNext()) {
         MenuParticles.P p = iterator.next();
         p.life -= dt;
         if (p.life <= 0.0 || p.y < -14.0) {
            iterator.remove();
            continue;
         }

         p.x += p.vx * dt;
         p.y += p.vy * dt;
         p.vx *= 1.0 - Math.min(1.0, 2.2 * dt);
         p.vy *= 1.0 - Math.min(1.0, 1.6 * dt);
         p.vy -= 7.0 * dt;
         float progress = (float)(p.life / p.maxLife);
         float fade = MathHelper.clamp(MathHelper.sin(progress * (float)Math.PI), 0.0F, 1.0F);
         int alpha = (int)(fade * 175.0F * alphaScale);
         if (alpha > 3) {
            float radius = (float)(p.size * (0.55 + 0.45 * (1.0 - progress)));
            RenderUtils.drawRoundCircle(matrices, (float)p.x, (float)p.y, radius * 2.4F, ColorUtils.setAlphaColor(p.color, alpha / 4));
            RenderUtils.drawRoundCircle(matrices, (float)p.x, (float)p.y, radius, ColorUtils.setAlphaColor(p.color, alpha));
         }
      }
   }

   private static final class P {
      private double x;
      private double y;
      private double vx;
      private double vy;
      private double life;
      private final double maxLife;
      private final double size;
      private final int color;

      private P(double x, double y, double vx, double vy, double life, double size, int color) {
         this.x = x;
         this.y = y;
         this.vx = vx;
         this.vy = vy;
         this.life = life;
         this.maxLife = life;
         this.size = size;
         this.color = color;
      }
   }
}
