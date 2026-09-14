package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class TargetLightningV2 implements QClient {
   private static final Identifier BLOOM = Identifier.of("lumen", "textures/targetesp/bloom.png");
   private static final int FALLBACK_COLOR = 0xFF55FFFF;
   private static final int RED_COLOR = 0xFFFF3C3C;
   private final ArrayList<TargetLightningV2.Bolt> bolts = new ArrayList<>();
   private long lastBoltSpawn = 0L;
   private float lightningCount = 4.0F;
   private float lightningWidth = 1.0F;
   private boolean hurtColor = true;
   private int baseColor = FALLBACK_COLOR;
   private float appearValue = 0.0F;
   private LivingEntity lastTarget = null;
   private Vec3d lastTargetPos = null;
   private long lastAttackTime = 0L;
   private LivingEntity lastAttackedEntity = null;

   public void setBaseColor(int baseColor) {
      this.baseColor = baseColor == 0 ? FALLBACK_COLOR : baseColor;
   }

   public void setHurtColor(boolean hurtColor) {
      this.hurtColor = hurtColor;
   }

   public void onAttack(LivingEntity entity) {
      this.lastAttackTime = System.currentTimeMillis();
      this.lastAttackedEntity = entity;
   }

   public void clear() {
      this.bolts.clear();
      this.appearValue = 0.0F;
      this.lastTarget = null;
      this.lastTargetPos = null;
      this.lastAttackedEntity = null;
   }

   public void onRender3D(MatrixStack matrices, float tickDelta, LivingEntity currentTarget) {
      if (mc.player == null || mc.world == null) {
         return;
      }

      boolean hasTarget = currentTarget != null && currentTarget.isAlive();
      this.appearValue = animateTo(this.appearValue, hasTarget ? 1.0F : 0.0F, 0.05F);
      if (hasTarget) {
         this.lastTarget = currentTarget;
         this.lastTargetPos = new Vec3d(
            MathHelper.lerp(tickDelta, currentTarget.lastRenderX, currentTarget.getX()),
            MathHelper.lerp(tickDelta, currentTarget.lastRenderY, currentTarget.getY()),
            MathHelper.lerp(tickDelta, currentTarget.lastRenderZ, currentTarget.getZ())
         );
      }

      if (this.appearValue <= 0.001F) {
         this.bolts.clear();
         if (!hasTarget) {
            this.lastTarget = null;
            this.lastTargetPos = null;
         }

         return;
      }

      this.renderLightning(matrices, tickDelta, hasTarget);
   }

   private void renderLightning(MatrixStack matrices, float partialTicks, boolean hasTarget) {
      if (this.lastTarget == null && this.lastTargetPos == null) {
         return;
      }

      long now = System.currentTimeMillis();
      int maxBolts = Math.max(4, Math.round(this.lightningCount) * 2);
      long spawnInterval = (long)Math.max(15, 65.0F / (this.lightningCount / 3.0F));
      if (hasTarget && this.lastTarget != null && this.lastTarget.isAlive() && now - this.lastBoltSpawn > spawnInterval && this.bolts.size() < maxBolts) {
         this.bolts.add(this.spawnBolt(this.lastTarget));
         this.lastBoltSpawn = now;
      }

      this.bolts.removeIf(bolt -> now - bolt.spawn > bolt.life);
      if (this.bolts.isEmpty()) {
         return;
      }

      Vec3d basePos;
      if (this.lastTarget != null && this.lastTarget.isAlive()) {
         basePos = new Vec3d(
            MathHelper.lerp(partialTicks, this.lastTarget.lastRenderX, this.lastTarget.getX()),
            MathHelper.lerp(partialTicks, this.lastTarget.lastRenderY, this.lastTarget.getY()),
            MathHelper.lerp(partialTicks, this.lastTarget.lastRenderZ, this.lastTarget.getZ())
         );
      } else {
         basePos = this.lastTargetPos;
      }

      if (basePos == null) {
         return;
      }

      Camera camera = mc.gameRenderer.getCamera();
      Vec3d cam = camera.getPos();
      float hurtPC = this.getHurtPC(this.lastTarget);
      int glowColor = blendColor(this.baseColor, RED_COLOR, hurtPC);
      int coreColor = blendColor(0xFFFFFFFF, RED_COLOR, hurtPC);
      float widthScale = this.lightningWidth;
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      boolean hasOuter = false;
      BufferBuilder outerBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (TargetLightningV2.Bolt bolt : this.bolts) {
         float alpha = this.boltAlpha(bolt, now) * this.appearValue;
         if (!(alpha <= 0.02F)) {
            int outerA = (int)(alpha * 45.0F);
            if (outerA > 0 && appendRibbon(outerBuffer, matrices, basePos, cam, bolt.points, 0.045F * widthScale, setAlpha(glowColor, outerA))) {
               hasOuter = true;
            }

            for (List<Vec3d> branch : bolt.branches) {
               if (outerA > 0 && appendRibbon(outerBuffer, matrices, basePos, cam, branch, 0.03F * widthScale, setAlpha(glowColor, (int)(outerA * 0.85F)))) {
                  hasOuter = true;
               }
            }
         }
      }

      if (hasOuter) {
         BufferRenderer.drawWithGlobalProgram(outerBuffer.end());
      }

      boolean hasGlow = false;
      BufferBuilder glowBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (TargetLightningV2.Bolt bolt : this.bolts) {
         float alpha = this.boltAlpha(bolt, now) * this.appearValue;
         if (!(alpha <= 0.02F)) {
            int glowA = (int)(alpha * 95.0F);
            if (glowA > 0 && appendRibbon(glowBuffer, matrices, basePos, cam, bolt.points, 0.018F * widthScale, setAlpha(glowColor, glowA))) {
               hasGlow = true;
            }

            for (List<Vec3d> branch : bolt.branches) {
               if (glowA > 0 && appendRibbon(glowBuffer, matrices, basePos, cam, branch, 0.012F * widthScale, setAlpha(glowColor, (int)(glowA * 0.8F)))) {
                  hasGlow = true;
               }
            }
         }
      }

      if (hasGlow) {
         BufferRenderer.drawWithGlobalProgram(glowBuffer.end());
      }

      boolean hasCore = false;
      BufferBuilder coreBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (TargetLightningV2.Bolt bolt : this.bolts) {
         float alpha = this.boltAlpha(bolt, now) * this.appearValue;
         if (!(alpha <= 0.02F)) {
            int coreA = (int)(alpha * 235.0F);
            if (coreA > 0 && appendRibbon(coreBuffer, matrices, basePos, cam, bolt.points, 0.006F * widthScale, setAlpha(coreColor, coreA))) {
               hasCore = true;
            }

            for (List<Vec3d> branch : bolt.branches) {
               if (coreA > 0 && appendRibbon(coreBuffer, matrices, basePos, cam, branch, 0.004F * widthScale, setAlpha(coreColor, (int)(coreA * 0.85F)))) {
                  hasCore = true;
               }
            }
         }
      }

      if (hasCore) {
         BufferRenderer.drawWithGlobalProgram(coreBuffer.end());
      }

      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, BLOOM);

      for (TargetLightningV2.Bolt bolt : this.bolts) {
         float alpha = this.boltAlpha(bolt, now) * this.appearValue;
         if (!(alpha <= 0.03F)) {
            int wideBloomA = (int)(alpha * 45.0F);
            if (wideBloomA > 0) {
               int wideColor = setAlpha(glowColor, wideBloomA);

               for (int i = 0; i < bolt.points.size(); i += 2) {
                  Vec3d p = basePos.add(bolt.points.get(i));
                  drawBillboard(matrices, cam, p.x, p.y, p.z, 0.28F * widthScale, wideColor);
               }
            }

            int nodeA = (int)(alpha * 120.0F);
            if (nodeA > 0) {
               int nodeColor = setAlpha(coreColor, nodeA);

               for (int i = 0; i < bolt.points.size(); i++) {
                  Vec3d p = basePos.add(bolt.points.get(i));
                  drawBillboard(matrices, cam, p.x, p.y, p.z, 0.08F * widthScale, nodeColor);
               }
            }
         }
      }

      RenderSystem.enableCull();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }

   private TargetLightningV2.Bolt spawnBolt(LivingEntity target) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      float radius = target.getWidth() * 0.5F;
      float height = target.getHeight();
      double startAngle = random.nextDouble() * Math.PI * 2.0;
      double startY = 0.15 + random.nextDouble() * Math.max(0.2, height - 0.4);
      Vec3d start = new Vec3d(Math.cos(startAngle) * radius * 0.7, startY, Math.sin(startAngle) * radius * 0.7);
      double theta = random.nextDouble() * Math.PI * 2.0;
      double phi = Math.toRadians((random.nextDouble() - 0.5) * 150.0);
      Vec3d dir = new Vec3d(Math.cos(phi) * Math.cos(theta), Math.sin(phi) * 0.8 + 0.25, Math.cos(phi) * Math.sin(theta)).normalize();
      double length = 0.35 + random.nextDouble() * 0.45;
      Vec3d end = start.add(dir.multiply(length));
      ArrayList<Vec3d> points = new ArrayList<>(List.of(start, end));
      points = displacePoints(points, 0.09, 3);
      ArrayList<List<Vec3d>> branches = new ArrayList<>();
      int branchCount = 1 + random.nextInt(3);

      for (int i = 0; i < branchCount; i++) {
         int anchorIndex = 1 + random.nextInt(Math.max(1, points.size() - 2));
         Vec3d anchor = points.get(anchorIndex);
         Vec3d branchDir = randomPerpendicular(dir);
         if (random.nextBoolean()) {
            branchDir = branchDir.multiply(-1.0);
         }

         double branchLength = 0.12 + random.nextDouble() * 0.18;
         Vec3d branchEnd = anchor.add(branchDir.multiply(branchLength));
         ArrayList<Vec3d> branchPoints = new ArrayList<>(List.of(anchor, branchEnd));
         branches.add(displacePoints(branchPoints, 0.05, 2));
      }

      return new TargetLightningV2.Bolt(points, branches, 140L + random.nextLong(160L), random.nextFloat() * 6.28F);
   }

   private static ArrayList<Vec3d> displacePoints(ArrayList<Vec3d> input, double amount, int passes) {
      ArrayList<Vec3d> points = new ArrayList<>(input);
      double amt = amount;

      for (int pass = 0; pass < passes; pass++) {
         ArrayList<Vec3d> next = new ArrayList<>(points.size() * 2);

         for (int i = 0; i < points.size() - 1; i++) {
            Vec3d a = points.get(i);
            Vec3d b = points.get(i + 1);
            next.add(a);
            Vec3d dir = b.subtract(a);
            if (dir.lengthSquared() < 1.0E-7) {
               next.add(a.add(b).multiply(0.5));
            } else {
               Vec3d perp = randomPerpendicular(dir.normalize());
               double offset = (ThreadLocalRandom.current().nextDouble() - 0.5) * 2.0 * amt;
               next.add(a.add(b).multiply(0.5).add(perp.multiply(offset)));
            }
         }

         next.add(points.get(points.size() - 1));
         points = next;
         amt *= 0.5;
      }

      return points;
   }

   private static Vec3d randomPerpendicular(Vec3d dir) {
      Vec3d arbitrary = Math.abs(dir.y) < 0.9 ? new Vec3d(0.0, 1.0, 0.0) : new Vec3d(1.0, 0.0, 0.0);
      Vec3d perp1 = dir.crossProduct(arbitrary).normalize();
      Vec3d perp2 = dir.crossProduct(perp1).normalize();
      double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0;
      return perp1.multiply(Math.cos(angle)).add(perp2.multiply(Math.sin(angle)));
   }

   private static boolean appendRibbon(
      BufferBuilder buffer, MatrixStack matrices, Vec3d base, Vec3d cam, List<Vec3d> points, float width, int color
   ) {
      int a = color >> 24 & 0xFF;
      if (a <= 0 || points.size() < 2) {
         return false;
      }

      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      Matrix4f matrix = matrices.peek().getPositionMatrix();
      float half = width * 0.5F;
      int appended = 0;

      for (int i = 0; i < points.size() - 1; i++) {
         Vec3d p1 = base.add(points.get(i));
         Vec3d p2 = base.add(points.get(i + 1));
         Vec3d segDir = p2.subtract(p1);
         if (segDir.lengthSquared() < 1.0E-8) {
            continue;
         }

         Vec3d viewDir = p1.add(p2).multiply(0.5).subtract(cam);
         if (viewDir.lengthSquared() < 1.0E-8) {
            continue;
         }

         Vec3d side = segDir.crossProduct(viewDir);
         double sideLen = side.length();
         if (sideLen < 1.0E-8 || !Double.isFinite(sideLen)) {
            continue;
         }

         side = side.multiply(1.0 / sideLen);
         float x1 = (float)(p1.x - cam.x);
         float y1 = (float)(p1.y - cam.y);
         float z1 = (float)(p1.z - cam.z);
         float x2 = (float)(p2.x - cam.x);
         float y2 = (float)(p2.y - cam.y);
         float z2 = (float)(p2.z - cam.z);
         float sx = (float)side.x * half;
         float sy = (float)side.y * half;
         float sz = (float)side.z * half;
         buffer.vertex(matrix, x1 - sx, y1 - sy, z1 - sz).color(r, g, b, a);
         buffer.vertex(matrix, x1 + sx, y1 + sy, z1 + sz).color(r, g, b, a);
         buffer.vertex(matrix, x2 + sx, y2 + sy, z2 + sz).color(r, g, b, a);
         buffer.vertex(matrix, x2 - sx, y2 - sy, z2 - sz).color(r, g, b, a);
         appended++;
      }

      return appended > 0;
   }

   private void drawBillboard(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float worldSize, int color) {
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      int a = color >> 24 & 0xFF;
      if (a <= 0) {
         return;
      }

      float half = worldSize * 0.5F;
      matrices.push();
      matrices.translate(worldX - cameraPos.x, worldY - cameraPos.y, worldZ - cameraPos.z);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
      Matrix4f matrix = matrices.peek().getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(matrix, -half, -half, 0.0F).texture(0.0F, 1.0F).color(r, g, b, a);
      buffer.vertex(matrix, half, -half, 0.0F).texture(1.0F, 1.0F).color(r, g, b, a);
      buffer.vertex(matrix, half, half, 0.0F).texture(1.0F, 0.0F).color(r, g, b, a);
      buffer.vertex(matrix, -half, half, 0.0F).texture(0.0F, 0.0F).color(r, g, b, a);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
      matrices.pop();
   }

   private float boltAlpha(TargetLightningV2.Bolt bolt, long now) {
      float life = MathHelper.clamp((now - bolt.spawn) / (float)bolt.life, 0.0F, 1.0F);
      float fade = life < 0.18F ? life / 0.18F : 1.0F - (life - 0.18F) / 0.82F;
      float flicker = 0.78F + 0.22F * (float)Math.sin(now * 0.045F + bolt.phase);
      return MathHelper.clamp(fade, 0.0F, 1.0F) * flicker;
   }

   private float getHurtPC(LivingEntity target) {
      if (!this.hurtColor) {
         return 0.0F;
      }

      float fromHurtTime = 0.0F;
      if (target != null && target.hurtTime > 0) {
         float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
         float hurtTicks = MathHelper.clamp(target.hurtTime - partialTicks, 0.0F, 10.0F);
         fromHurtTime = hurtTicks / 10.0F;
      }

      float fromAttack = 0.0F;
      long elapsed = System.currentTimeMillis() - this.lastAttackTime;
      if (elapsed < 420L && (target == null || target == this.lastAttackedEntity || target == this.lastTarget)) {
         fromAttack = 1.0F - (float)elapsed / 420.0F;
      }

      float progress = Math.max(fromHurtTime, fromAttack);
      return MathHelper.clamp(progress * progress * (3.0F - 2.0F * progress), 0.0F, 1.0F);
   }

   private static int blendColor(int color1, int color2, float factor) {
      factor = MathHelper.clamp(factor, 0.0F, 1.0F);
      int r1 = color1 >> 16 & 0xFF;
      int g1 = color1 >> 8 & 0xFF;
      int b1 = color1 & 0xFF;
      int a1 = color1 >> 24 & 0xFF;
      int r2 = color2 >> 16 & 0xFF;
      int g2 = color2 >> 8 & 0xFF;
      int b2 = color2 & 0xFF;
      int a2 = color2 >> 24 & 0xFF;
      int r = (int)(r1 + (r2 - r1) * factor);
      int g = (int)(g1 + (g2 - g1) * factor);
      int b = (int)(b1 + (b2 - b1) * factor);
      int a = (int)(a1 + (a2 - a1) * factor);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int setAlpha(int color, int alpha) {
      alpha = Math.max(0, Math.min(255, alpha));
      return alpha << 24 | color & 0xFFFFFF;
   }

   private static float animateTo(float current, float target, float delta) {
      if (current < target) {
         return Math.min(current + delta, target);
      } else {
         return current > target ? Math.max(current - delta, target) : current;
      }
   }

   private static final class Bolt {
      final List<Vec3d> points;
      final List<List<Vec3d>> branches;
      final long spawn;
      final long life;
      final float phase;

      Bolt(List<Vec3d> points, List<List<Vec3d>> branches, long life, float phase) {
         this.points = points;
         this.branches = branches;
         this.spawn = System.currentTimeMillis();
         this.life = life;
         this.phase = phase;
      }
   }
}
