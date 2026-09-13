package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.combat.AimAssistant;
import dlc.lumen.client.modules.impl.combat.Aura;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TargetESP extends Module {
   public static TargetESP INSTANCE = new TargetESP();
   private static final float VOLUME = 0.6F;
   private static final float VOLUME2 = 1.2F;
   private static final float VOLUME3 = 0.007F;
   static final long CUBE_ATTACH_LIFE_MS = 560L;
   static final long CUBE_FADE_LIFE_MS = 320L;
   static final int MAX_CUBE_PARTICLES = 72;
   static final byte[][] CUBE_EDGES = new byte[][]{
      {-1, -1, -1, 1, -1, -1},
      {1, -1, -1, 1, -1, 1},
      {1, -1, 1, -1, -1, 1},
      {-1, -1, 1, -1, -1, -1},
      {-1, 1, -1, 1, 1, -1},
      {1, 1, -1, 1, 1, 1},
      {1, 1, 1, -1, 1, 1},
      {-1, 1, 1, -1, 1, -1},
      {-1, -1, -1, -1, 1, -1},
      {1, -1, -1, 1, 1, -1},
      {1, -1, 1, 1, 1, 1},
      {-1, -1, 1, -1, 1, 1}
   };
   private final ModeSetting modeSetting = new ModeSetting("Режим", "Души", "Кольцо 2", "Души", "Молнии", "Зако", "Орбиты", "Ромб");
   private final FloatSetting floatSetting = new FloatSetting("Размер", 1.15F, 0.6F, 2.5F, 0.05F);
   private final FloatSetting floatSetting2 = new FloatSetting("Радиус кольца", 0.5F, 0.3F, 1.5F, 0.05F);
   private final FloatSetting floatSetting3 = new FloatSetting("Скорость кольца", 1.0F, 0.3F, 3.0F, 0.1F);
   private final FloatSetting floatSetting4 = new FloatSetting("Скорость вращения", 1.2F, 0.2F, 4.0F, 0.05F);
   private final FloatSetting floatSetting5 = new FloatSetting("Радиус", 0.7F, 0.3F, 2.0F, 0.05F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Окрашивание при ударе", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("При наводке", true);
   private final FloatSetting floatSetting6 = new FloatSetting("Кол-во призраков", 3.0F, 2.0F, 5.0F, 1.0F);
   private final FloatSetting floatSetting7 = new FloatSetting("Время жизни (мс)", 350.0F, 150.0F, 500.0F, 25.0F);
   private final FloatSetting xZSetting = new FloatSetting("Цикл XZ", 2000.0F, 1000.0F, 5000.0F, 100.0F);
   private final FloatSetting ySetting = new FloatSetting("Цикл Y", 1700.0F, 1000.0F, 5000.0F, 100.0F);
   private float volume = 0.0F;
   private float volume2 = 0.0F;
   private float volume3 = 0.0F;
   private float volume4 = -280.0F;
   private float volume5 = 280.0F;
   private long timestamp = System.currentTimeMillis();
   private LivingEntity livingEntity = null;
   private LivingEntity livingEntity2 = null;
   private Vec3d vec3d = null;
   private float volume6 = 1.8F;
   private float volume7 = 0.6F;
   private final CopyOnWriteArrayList<TargetESP.GlowPoint> targetESPs = new CopyOnWriteArrayList<>();
   private float volume8 = 0.0F;
   private float volume9 = 0.0F;
   private float volume10 = 0.0F;
   private long timestamp2 = 0L;
   private final ArrayList<CubeParticle> cubeParticles = new ArrayList<>();
   private final ArrayList<CubeParticle> cubeParticles2 = new ArrayList<>();
   private static final float VOLUME4 = 0.022F;
   private static final int INDEX = 1;
   private static final int INDEX2 = 5;
   private static final int INDEX3 = 18;
   private final ArrayList<TargetESP.LightningBolt> targetESPs2 = new ArrayList<>();
   private static final int INDEX4 = 28;
   private static final int INDEX5 = 16;
   private static final int INDEX6 = 24;
   private static final int INDEX7 = 90;
   private static final float[] VOLUME5 = new float[28];
   private static final float[] VOLUME6 = new float[28];
   private static final float[] VOLUME7 = new float[28];
   private static final float[] VOLUME8 = new float[28];
   private static final float[] VOLUME9 = new float[28];
   private static final float[] VOLUME10 = new float[28];
   private static final float[] VOLUME11 = new float[28];
   private static final float[] VOLUME12 = new float[28];
   private static final float[][] VALUE = new float[90][3];
   private static final float[][] VALUE2 = new float[90][3];
   private static final float[] VOLUME13 = new float[90];
   private static final float[] VOLUME14 = new float[90];
   private static final float[] VOLUME15 = new float[90];
   private static int index = 20260810;
   private float volume11;
   private float volume12;
   private float volume13;
   private float volume14;
   private float volume15;
   private float volume16;
   private double level;
   private double level2;
   private double level3;
   private final float[] volume17 = new float[3];
   private final float[] volume18 = new float[3];
   private static final int INDEX8 = 64;
   private final float[] volume19 = new float[64];
   private final float[] volume20 = new float[64];
   private final float[] volume21 = new float[64];
   private final float[] volume22 = new float[64];
   private final int[] index2 = new int[64];
   private static final float[] VOLUME16;
   private static final float[] VOLUME17;
   private static final int INDEX9 = 20;
   private static final int INDEX10 = 350;
   private static final int INDEX11 = 8;
   private static final float[] VOLUME18;
   private static final float[] VOLUME19;
   private final List<double[]> doubles = new ArrayList<>();
   private long timestamp3;
   private float volume23 = 0.016666668F;
   private float volume24;
   private static final Identifier TEXTURE_ID;

   public TargetESP() {
      super("TargetESP", "Отображения таргета", Module.ModuleCategory.RENDER);
      this.floatSetting.visible(this::helper);
      this.floatSetting4.visible(() -> this.helper() || this.helper24());
      this.floatSetting5.visible(this::helper24);
      this.floatSetting6.visible(() -> this.modeSetting.is("Райдер"));
      this.floatSetting7.visible(() -> this.modeSetting.is("Райдер"));
      this.xZSetting.visible(() -> this.modeSetting.is("Райдер"));
      this.ySetting.visible(() -> this.modeSetting.is("Райдер"));
      this.floatSetting2.visible(() -> this.modeSetting.is("Кольцо") || this.modeSetting.is("Кольцо 2"));
      this.floatSetting3.visible(() -> this.modeSetting.is("Кольцо") || this.modeSetting.is("Кольцо 2"));
      this.addSettings(
         this.modeSetting,
         this.floatSetting,
         this.floatSetting4,
         this.floatSetting5,
         this.booleanSetting,
         this.booleanSetting2,
         this.floatSetting2,
         this.floatSetting3,
         this.floatSetting6,
         this.floatSetting7,
         this.xZSetting,
         this.ySetting
      );
   }

   @Override
   public void onDisable() {
      this.volume = 0.0F;
      this.volume2 = 0.0F;
      this.livingEntity = null;
      this.livingEntity2 = null;
      this.vec3d = null;
      this.volume3 = 0.0F;
      this.volume4 = -280.0F;
      this.volume5 = 280.0F;
      this.targetESPs.clear();
      this.volume8 = 0.0F;
      this.volume9 = 0.0F;
      this.volume10 = 0.0F;
      this.timestamp2 = 0L;
      this.cubeParticles.clear();
      this.cubeParticles2.clear();
      this.doubles.clear();
      this.timestamp3 = 0L;
      this.volume24 = 0.0F;
      this.targetESPs2.clear();
      super.onDisable();
   }

   private boolean helper() {
      return this.modeSetting.is("Картинка 1") || this.modeSetting.is("Картинка 2") || this.helper2();
   }

   private boolean helper2() {
      return this.modeSetting.is("Зако");
   }

   private Identifier helper3() {
      if (this.modeSetting.is("Зако")) {
         return Identifier.of("lumen", "textures/targetesp/zako.png");
      } else {
         return this.modeSetting.is("Картинка 2")
            ? Identifier.of("lumen", "textures/targetesp/targetesp_3.png")
            : Identifier.of("lumen", "textures/targetesp/targetesp_2.png");
      }
   }

   private Identifier helper4() {
      return Identifier.of("lumen", "textures/targetesp/bloom.png");
   }

   private int resolveInt() {
      int var1 = ColorUtils.getThemeColor();
      if ((var1 >> 24 & 0xFF) == 0) {
         var1 |= -16777216;
      }

      return var1;
   }

   private LivingEntity helper5() {
      if (mc == null) {
         return null;
      } else {
         return mc.targetedEntity instanceof LivingEntity var1 && var1 != mc.player && var1.isAlive() ? var1 : null;
      }
   }

   private float helper6(float current, float target, float delta) {
      if (current < target) {
         current = Math.min(current + delta, target);
      } else if (current > target) {
         current = Math.max(current - delta, target);
      }

      return current;
   }

   private float helper7(Vec3d cameraPos, double worldX, double worldY, double worldZ) {
      double var8 = worldX - cameraPos.x;
      double var10 = worldY - cameraPos.y;
      double var12 = worldZ - cameraPos.z;
      double var14 = Math.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
      return (float)Math.max(0.1, var14 * 0.007F);
   }

   @EventLink(priority = -100)
   public void onRender3D(Event3DRender event) {
      if (mc != null && mc.player != null && mc.world != null) {
         Aura var2 = ModuleClass.aura;
         boolean var3 = var2 != null && var2.isEnable();
         LivingEntity var4 = var3 ? var2.getTarget() : null;
         if (var4 == null) {
            AimAssistant var5 = AimAssistant.INSTANCE;
            if (var5 != null && var5.isEnable()) {
               var4 = var5.getCurrentTarget();
            }
         }

         if (var4 == null && this.booleanSetting2.isState()) {
            var4 = this.helper5();
         }

         boolean var8 = var4 != null && var4.isAlive();
         float var6 = 0.05F;
         this.volume = this.helper6(this.volume, var8 ? 1.0F : 0.0F, var6);
         this.volume2 = this.helper6(this.volume2, var8 ? 1.0F : 0.5F, var6);
         if (var8) {
            this.livingEntity = var4;
            this.livingEntity2 = var4;
         }

         if (this.modeSetting.is("Кристаллы")) {
            float var7 = var8 ? 0.07F : 0.045F;
            this.volume9 = this.helper6(this.volume9, var8 ? 1.0F : 0.0F, var7);
            if (var8) {
               this.volume8 += 0.8F;
            }
         }

         if (this.modeSetting.is("Кольцо 2")) {
            this.helper39(event);
         }

         if (!(this.volume <= 0.001F) || var8 || this.modeSetting.is("Кристаллы") && !(this.volume9 <= 0.001F)) {
            if (var8 && var4 != null) {
               float var9 = event.getTickDelta();
               this.vec3d = new Vec3d(
                  MathHelper.lerp(var9, var4.lastRenderX, var4.getX()),
                  MathHelper.lerp(var9, var4.lastRenderY, var4.getY()),
                  MathHelper.lerp(var9, var4.lastRenderZ, var4.getZ())
               );
               this.volume6 = var4.getHeight();
               this.volume7 = var4.getWidth();
            }

            if (this.vec3d != null) {
               if (this.modeSetting.is("Райдер")) {
                  if (var8 && var4 != null) {
                     this.helper45(
                        var4,
                        event.getTickDelta(),
                        Math.max(1, Math.round(this.floatSetting6.getValue().floatValue())),
                        Math.max(1, Math.round(this.floatSetting7.getValue().floatValue())),
                        this.resolveInt()
                     );
                  }

                  this.targetESPs.removeIf(TargetESP.GlowPoint::shouldRemove);
                  this.helper46(event);
               } else if (this.modeSetting.is("Кристаллы")) {
                  LivingEntity var10 = var8 ? var4 : this.livingEntity;
                  if ((var10 != null || this.vec3d != null) && this.volume9 > 0.01F) {
                     this.helper48(event.getMatrices(), var10, event.getTickDelta());
                  }
               } else {
                  if (this.helper()) {
                     this.helper42(event);
                  }

                  if (this.modeSetting.is("Души")) {
                     this.helper44(event);
                  }

                  if (this.modeSetting.is("Призраки")) {
                     this.helper47(event);
                  }

                  if (this.modeSetting.is("Кольцо") || this.modeSetting.is("Кольцо 2")) {
                     this.helper36(event);
                  }

                  if (this.modeSetting.is("Кубы")) {
                     this.helper16(event, var4, var8);
                  }

                  if (this.modeSetting.is("Вихрь")) {
                     this.helper31(event);
                  }

                  if (this.modeSetting.is("Сфера")) {
                     this.helper32(event);
                  }

                  if (this.modeSetting.is("ДНК")) {
                     this.helper33(event);
                  }

                  if (this.modeSetting.is("Руны")) {
                     this.helper34(event);
                  }

                  if (this.modeSetting.is("Молнии")) {
                     this.helper12(event);
                  }

                  if (this.modeSetting.is("Орбиты")) {
                     this.helper18(event);
                  }

                  if (this.modeSetting.is("Ромб")) {
                     this.helper43(event);
                  }
               }
            }
         } else {
            this.livingEntity = null;
            this.vec3d = null;
         }
      }
   }

   private TargetESP.LightningBolt helper8(float height, float width, ThreadLocalRandom rnd) {
      double var4 = Math.max(0.35, width * 0.7);
      double var6 = rnd.nextDouble() * Math.PI * 2.0;
      double var8 = (rnd.nextBoolean() ? 1 : -1) * Math.PI * (1.1 + rnd.nextDouble() * 1.4);
      float var10 = (float)rnd.nextDouble() * height;
      float var11 = (float)rnd.nextDouble() * height;
      int var12 = 16 + rnd.nextInt(8);
      float[] var13 = new float[var12 + 1];
      float[] var14 = new float[var12 + 1];
      float[] var15 = new float[var12 + 1];

      for (int var16 = 0; var16 <= var12; var16++) {
         double var17 = (double)var16 / var12;
         double var19 = var6 + var8 * var17;
         double var21 = (var16 % 2 == 0 ? 1 : -1) * (0.05 + rnd.nextDouble() * 0.14);
         double var23 = var4 * (1.0 + var21) + (rnd.nextDouble() - 0.5) * 0.1;
         var13[var16] = (float)(Math.cos(var19) * var23);
         var15[var16] = (float)(Math.sin(var19) * var23);
         var14[var16] = (float)(var10 + (var11 - var10) * var17 + (rnd.nextDouble() - 0.5) * 0.22);
      }

      float[][] var25 = helper10(var13, var14, var15, 2);
      return new TargetESP.LightningBolt(var25[0], var25[1], var25[2], 500 + rnd.nextInt(400), (float)(rnd.nextDouble() * Math.PI * 2.0), 0);
   }

   private TargetESP.LightningBolt helper9(TargetESP.LightningBolt parent, int dir, float height, float width, ThreadLocalRandom rnd) {
      int var6 = parent.xs.length;
      float var7 = parent.xs[var6 - 1];
      float var8 = parent.ys[var6 - 1];
      float var9 = parent.zs[var6 - 1];
      double var10 = Math.max(0.35, width * 0.7);
      double var12 = Math.atan2(var9, var7);
      double var14 = dir * Math.PI * (0.5 + rnd.nextDouble() * 0.8);
      float var16 = MathHelper.clamp(var8 + (float)((rnd.nextDouble() - 0.5) * height * 0.9), 0.0F, height);
      int var17 = 10 + rnd.nextInt(5);
      float[] var18 = new float[var17 + 1];
      float[] var19 = new float[var17 + 1];
      float[] var20 = new float[var17 + 1];
      var18[0] = var7;
      var19[0] = var8;
      var20[0] = var9;

      for (int var21 = 1; var21 <= var17; var21++) {
         double var22 = (double)var21 / var17;
         double var24 = var12 + var14 * var22;
         double var26 = (var21 % 2 == 0 ? 1 : -1) * (0.05 + rnd.nextDouble() * 0.14);
         double var28 = var10 * (1.0 + var26) + (rnd.nextDouble() - 0.5) * 0.1;
         var18[var21] = (float)(Math.cos(var24) * var28);
         var20[var21] = (float)(Math.sin(var24) * var28);
         var19[var21] = (float)(var8 + (var16 - var8) * var22 + (rnd.nextDouble() - 0.5) * 0.22);
      }

      float[][] var30 = helper10(var18, var19, var20, 2);
      return new TargetESP.LightningBolt(var30[0], var30[1], var30[2], 400 + rnd.nextInt(300), (float)(rnd.nextDouble() * Math.PI * 2.0), parent.depth + 1);
   }

   private static float[][] helper10(float[] xs, float[] ys, float[] zs, int subdiv) {
      int var4 = xs.length;
      int var5 = (var4 - 1) * subdiv + 1;
      float[] var6 = new float[var5];
      float[] var7 = new float[var5];
      float[] var8 = new float[var5];
      int var9 = 0;

      for (int var10 = 0; var10 < var4 - 1; var10++) {
         int var11 = Math.max(0, var10 - 1);
         int var12 = Math.min(var4 - 1, var10 + 2);

         for (int var13 = 0; var13 < subdiv; var13++) {
            float var14 = (float)var13 / subdiv;
            var6[var9] = helper11(xs[var11], xs[var10], xs[var10 + 1], xs[var12], var14);
            var7[var9] = helper11(ys[var11], ys[var10], ys[var10 + 1], ys[var12], var14);
            var8[var9] = helper11(zs[var11], zs[var10], zs[var10 + 1], zs[var12], var14);
            var9++;
         }
      }

      var6[var9] = xs[var4 - 1];
      var7[var9] = ys[var4 - 1];
      var8[var9] = zs[var4 - 1];
      return new float[][]{var6, var7, var8};
   }

   private static float helper11(float p0, float p1, float p2, float p3, float t) {
      float var5 = t * t;
      float var6 = var5 * t;
      return 0.5F * (2.0F * p1 + (-p0 + p2) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * var5 + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * var6);
   }

   private void helper12(Event3DRender event) {
      if (!(this.volume <= 0.001F)) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            boolean var4 = this.livingEntity != null && this.livingEntity.isAlive();
            float var5 = this.helper26();
            float var6 = var4 ? this.livingEntity.getWidth() : this.volume7;
            long var7 = System.currentTimeMillis();
            ThreadLocalRandom var9 = ThreadLocalRandom.current();
            this.targetESPs2.removeIf(bolt -> var7 - bolt.spawnTime > bolt.lifeMs);
            int var10 = 0;

            for (TargetESP.LightningBolt var12 : this.targetESPs2) {
               if (var12.depth == 0) {
                  var10++;
               }
            }

            while (var4 && var10 < 5 && this.targetESPs2.size() < 18) {
               this.targetESPs2.add(this.helper8(var5, var6, var9));
               var10++;
            }

            if (var4 && this.targetESPs2.size() + 2 <= 18) {
               ArrayList var26 = new ArrayList();

               for (TargetESP.LightningBolt var13 : this.targetESPs2) {
                  if (var13.depth == 0 && !var13.hasSplit) {
                     float var14 = (float)(var7 - var13.spawnTime) / (float)var13.lifeMs;
                     if (!(var14 < 0.45F)) {
                        var13.hasSplit = true;
                        var26.add(this.helper9(var13, 1, var5, var6, var9));
                        var26.add(this.helper9(var13, -1, var5, var6, var9));
                        if (this.targetESPs2.size() + var26.size() + 2 > 18) {
                           break;
                        }
                     }
                  }
               }

               this.targetESPs2.addAll(var26);
            }

            if (!this.targetESPs2.isEmpty()) {
               Vec3d var27 = mc.gameRenderer.getCamera().getPos();
               double var29 = var3.x - var27.x;
               double var30 = var3.y - var27.y;
               double var16 = var3.z - var27.z;
               int var18 = this.helper27();
               int var19 = this.overCol(var18, -1, 0.45F);
               Matrix4f var20 = event.getMatrices().peek().getPositionMatrix();
               this.helper29();

               for (TargetESP.LightningBolt var22 : this.targetESPs2) {
                  float var23 = MathHelper.clamp((float)(var7 - var22.spawnTime) / (float)var22.lifeMs, 0.0F, 1.0F);
                  float var24 = helper14(var23);
                  float var25 = this.helper15(var22, var23, var7);
                  if (!(var25 <= 0.01F) && !(var24 <= 0.02F)) {
                     this.helper13(var20, var29, var30, var16, var22, var18, var25, var22.depth > 0 ? 0.055F : 0.085F, var24, var7);
                  }
               }

               this.helper30();
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            }
         }
      }
   }

   private void helper13(
      Matrix4f m, double x, double y, double z, TargetESP.LightningBolt bolt, int color, float baseAlpha, float halfWidth, float reveal, long now
   ) {
      int var15 = bolt.xs.length;
      if (var15 >= 2) {
         ShaderProgram var16 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.lightning);
         if (var16 != null) {
            RenderSystem.setShader(ShaderUtils.lightning);
            GlUniform var17 = var16.getUniform("Time");
            if (var17 != null) {
               var17.set((float)(now % 100000L) / 1000.0F + bolt.flickerSeed);
            }

            GlUniform var18 = var16.getUniform("Reveal");
            if (var18 != null) {
               var18.set(reveal);
            }

            BufferBuilder var19 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);

            for (int var20 = 0; var20 < var15; var20++) {
               float var21 = (float)(x + bolt.xs[var20]);
               float var22 = (float)(y + bolt.ys[var20]);
               float var23 = (float)(z + bolt.zs[var20]);
               int var24 = Math.max(0, var20 - 1);
               int var25 = Math.min(var15 - 1, var20 + 1);
               float var26 = bolt.xs[var25] - bolt.xs[var24];
               float var27 = bolt.ys[var25] - bolt.ys[var24];
               float var28 = bolt.zs[var25] - bolt.zs[var24];
               float var29 = (float)Math.sqrt(var26 * var26 + var27 * var27 + var28 * var28);
               if (var29 < 1.0E-5F) {
                  var26 = 0.0F;
                  var27 = 1.0F;
                  var28 = 0.0F;
                  var29 = 1.0F;
               }

               var26 /= var29;
               var27 /= var29;
               var28 /= var29;
               float var30 = (float)Math.sqrt(var21 * var21 + var22 * var22 + var23 * var23);
               float var31 = var30 < 1.0E-5F ? 0.0F : var21 / var30;
               float var32 = var30 < 1.0E-5F ? 0.0F : var22 / var30;
               float var33 = var30 < 1.0E-5F ? 1.0F : var23 / var30;
               float var34 = var27 * var33 - var28 * var32;
               float var35 = var28 * var31 - var26 * var33;
               float var36 = var26 * var32 - var27 * var31;
               float var37 = (float)Math.sqrt(var34 * var34 + var35 * var35 + var36 * var36);
               if (var37 < 1.0E-5F) {
                  var34 = 1.0F;
                  var35 = 0.0F;
                  var36 = 0.0F;
                  var37 = 1.0F;
               }

               var34 = var34 / var37 * halfWidth;
               var35 = var35 / var37 * halfWidth;
               var36 = var36 / var37 * halfWidth;
               float var38 = (float)var20 / (var15 - 1);
               int var39 = this.helper37(color, baseAlpha);
               var19.vertex(m, var21 - var34, var22 - var35, var23 - var36).texture(var38, -1.0F).color(var39);
               var19.vertex(m, var21 + var34, var22 + var35, var23 + var36).texture(var38, 1.0F).color(var39);
            }

            BufferRenderer.drawWithGlobalProgram(var19.end());
         }
      }
   }

   private static float helper14(float age) {
      float var1 = MathHelper.clamp(age / 0.4F, 0.0F, 1.0F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private float helper15(TargetESP.LightningBolt bolt, float age, long now) {
      float var5 = age > 0.7F ? 1.0F - (age - 0.7F) / 0.3F : 1.0F;
      var5 = var5 * var5 * (3.0F - 2.0F * var5);
      float var6 = 0.88F + 0.12F * (float)Math.sin(now * 0.02 + bolt.flickerSeed);
      float var7 = this.volume * var5 * var6;
      return bolt.depth > 0 ? var7 * 0.85F : var7;
   }

   private void helper16(Event3DRender event, LivingEntity target, boolean hasTarget) {
      long var4 = System.currentTimeMillis();
      if (this.timestamp2 == 0L) {
         this.timestamp2 = var4;
      }

      float var6 = Math.min((float)(var4 - this.timestamp2) / 1000.0F, 0.1F);
      this.timestamp2 = var4;
      if (Float.isFinite(var6) && mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         if (hasTarget && target != null) {
            this.livingEntity = target;
            this.volume10 += var6;

            while (this.volume10 >= 0.022F) {
               this.volume10 -= 0.022F;
               if (this.cubeParticles.size() >= 72) {
                  break;
               }

               for (int var7 = 0; var7 < 1; var7++) {
                  double var8 = Math.random() * 360.0;
                  double var10 = Math.cos(Math.toRadians(var8)) * 0.7;
                  double var12 = 0.02 + Math.random() * 0.1;
                  double var14 = Math.sin(Math.toRadians(var8)) * 0.7;
                  this.cubeParticles.add(new CubeParticle(target, var10, var12, var14));
               }
            }
         } else {
            this.volume10 = 0.0F;
         }

         this.cubeParticles2.clear();

         for (int var30 = this.cubeParticles.size() - 1; var30 >= 0; var30--) {
            CubeParticle var32 = this.cubeParticles.get(var30);

            try {
               var32.update(var6, var4, hasTarget ? target : null);
               if (var32.shouldRemove(var4)) {
                  this.cubeParticles.remove(var30);
               } else {
                  this.cubeParticles2.add(var32);
               }
            } catch (Throwable var29) {
               this.cubeParticles.remove(var30);
            }
         }

         if (!this.cubeParticles2.isEmpty()) {
            float var31 = event.getTickDelta();
            MatrixStack var33 = event.getMatrices();
            Vec3d var9 = mc.gameRenderer.getCamera().getPos();
            LivingEntity var34 = hasTarget ? target : this.livingEntity;
            float var11 = this.helper40(var34);
            int var35 = this.resolveInt();
            int var13 = ColorUtils.rgb(255, 3, 3);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder var36 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            boolean var15 = false;
            int var16 = 0;

            for (int var17 = this.cubeParticles2.size(); var16 < var17; var16++) {
               CubeParticle var18 = this.cubeParticles2.get(var16);

               try {
                  int var19 = var18.getRenderColor(var35, var13, var11, var4);
                  if ((var19 >> 24 & 0xFF) > 0 && var18.appendCubeFaces(var36, var33, var9, var31, var19)) {
                     var15 = true;
                  }
               } catch (Throwable var28) {
               }
            }

            if (var15) {
               BufferRenderer.drawWithGlobalProgram(var36.end());
            }

            BufferBuilder var37 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            boolean var38 = false;
            int var39 = 0;

            for (int var41 = this.cubeParticles2.size(); var39 < var41; var39++) {
               CubeParticle var20 = this.cubeParticles2.get(var39);

               try {
                  int var21 = var20.getRenderColor(var35, var13, var11, var4);
                  if ((var21 >> 24 & 0xFF) > 0 && var20.appendCubeLines(var37, var33, var9, var31, var21)) {
                     var38 = true;
                  }
               } catch (Throwable var27) {
               }
            }

            if (var38) {
               BufferRenderer.drawWithGlobalProgram(var37.end());
            }

            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, this.helper4());
            BufferBuilder var40 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            boolean var42 = false;
            float var43 = mc.gameRenderer.getCamera().getYaw();
            float var44 = mc.gameRenderer.getCamera().getPitch();
            int var22 = 0;

            for (int var23 = this.cubeParticles2.size(); var22 < var23; var22++) {
               CubeParticle var24 = this.cubeParticles2.get(var22);

               try {
                  int var25 = var24.getRenderColor(var35, var13, var11, var4);
                  if (var24.appendBloom(var40, var33, var9, var43, var44, var31, var25, var4)) {
                     var42 = true;
                  }
               } catch (Throwable var26) {
               }
            }

            if (var42) {
               BufferRenderer.drawWithGlobalProgram(var40.end());
            }

            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
         }
      }
   }

   private static float helper17() {
      int var0 = index;
      var0 ^= var0 << 13;
      var0 ^= var0 >> 17;
      var0 ^= var0 << 5;
      index = var0;
      return (float)((var0 & 4294967295L) / 4.2949673E9F);
   }

   private static void updateState(float ta, float tb, float[] outU, float[] outV) {
      float var4 = (float)(Math.sin(ta) * Math.cos(tb));
      float var5 = (float)Math.cos(ta);
      float var6 = (float)(Math.sin(ta) * Math.sin(tb));
      float var7 = 0.0F;
      float var8 = 1.0F;
      float var9 = 0.0F;
      if (Math.abs(var5) > 0.94F) {
         var7 = 1.0F;
         var8 = 0.0F;
      }

      float var10 = var5 * var9 - var6 * var8;
      float var11 = var6 * var7 - var4 * var9;
      float var12 = var4 * var8 - var5 * var7;
      float var13 = (float)Math.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
      if (var13 < 1.0E-6F) {
         var13 = 1.0F;
      }

      var10 /= var13;
      var11 /= var13;
      var12 /= var13;
      outU[0] = var10;
      outU[1] = var11;
      outU[2] = var12;
      outV[0] = var5 * var12 - var6 * var11;
      outV[1] = var6 * var10 - var4 * var12;
      outV[2] = var4 * var11 - var5 * var10;
   }

   private void updateState2(double lx, double ly, double lz) {
      double var7 = lx * this.volume11 + lz * this.volume12;
      double var9 = -lx * this.volume12 + lz * this.volume11;
      double var11 = ly * this.volume13 - var9 * this.volume14;
      double var13 = ly * this.volume14 + var9 * this.volume13;
      double var15 = var7 * this.volume15 - var11 * this.volume16;
      double var17 = var7 * this.volume16 + var11 * this.volume15;
      this.level = var15;
      this.level2 = -var17;
      this.level3 = var13;
   }

   private void helper18(Event3DRender event) {
      if (!(this.volume <= 0.001F)) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            float var4 = this.helper26();
            Vec3d var5 = mc.gameRenderer.getCamera().getPos();
            double var6 = var3.x - var5.x;
            double var8 = var3.y - var5.y + var4 * 0.5;
            double var10 = var3.z - var5.z;
            float var12 = (float)(System.currentTimeMillis() % 1000000L) / 1000.0F * this.floatSetting4.get();
            float var13 = this.floatSetting5.get() * 1.4F;
            float var14 = this.volume;
            int var15 = this.helper27();
            float var16 = var12 * 0.12F;
            float var17 = -0.42F;
            float var18 = 0.26F;
            this.volume11 = (float)Math.cos(var16);
            this.volume12 = (float)Math.sin(var16);
            this.volume13 = (float)Math.cos(var17);
            this.volume14 = (float)Math.sin(var17);
            this.volume15 = (float)Math.cos(var18);
            this.volume16 = (float)Math.sin(var18);
            float var19 = mc.gameRenderer.getCamera().getYaw();
            float var20 = mc.gameRenderer.getCamera().getPitch();
            MatrixStack var21 = event.getMatrices();
            Matrix4f var22 = var21.peek().getPositionMatrix();
            this.helper29();
            float var23 = 0.86F + 0.14F * (float)Math.sin(var12 * 2.4F);
            this.helper22(var21, var6, var8, var10, var19, var20, var13 * 0.9F * var23, this.helper23(var15, 0.85F, 0.5F * var14), 0);

            for (int var24 = 0; var24 < 2; var24++) {
               float var25 = (var12 * 0.42F + var24 * 0.5F) % 1.0F;
               float var26 = var13 * (0.32F + 1.25F * var25);
               float var27 = (float)Math.pow(1.0F - var25, 2.1) * 0.6F * var14;
               if (!(var27 <= 0.003F)) {
                  byte var28 = 48;

                  for (int var29 = 0; var29 <= var28; var29++) {
                     float var30 = (float)var29 / var28 * 6.2832F;
                     this.updateState2(var26 * Math.cos(var30), 0.0, var26 * Math.sin(var30));
                     this.volume19[var29] = (float)(var6 + this.level);
                     this.volume20[var29] = (float)(var8 + this.level2);
                     this.volume21[var29] = (float)(var10 + this.level3);
                     this.volume22[var29] = var13 * 0.045F;
                     this.index2[var29] = this.helper37(this.helper23(var15, 0.35F, 1.0F), var27 * 0.3F);
                  }

                  this.helper20(
                     var22, var28 + 1, this.volume19, this.volume20, this.volume21, this.volume22, this.index2
                  );

                  for (int var48 = 0; var48 <= var28; var48++) {
                     this.volume22[var48] = var13 * 0.012F;
                     this.index2[var48] = this.helper37(this.helper23(var15, 0.45F, 1.0F), var27);
                  }

                  this.helper20(
                     var22, var28 + 1, this.volume19, this.volume20, this.volume21, this.volume22, this.index2
                  );
                  var22 = var21.peek().getPositionMatrix();
               }
            }

            for (int var37 = 0; var37 < 16; var37++) {
               int var39 = var37 % 28;
               updateState(
                  VOLUME6[var39], VOLUME7[var39] + var12 * VOLUME8[var39], this.volume17, this.volume18
               );
               float var41 = var13 * VOLUME5[var39];
               float var43 = VOLUME11[var39] + var12 * VOLUME9[var39];

               for (int var45 = 0; var45 < 24; var45++) {
                  float var49 = var45 / 23.0F;
                  float var52 = var43 - VOLUME10[var39] * (1.0F - var49);
                  float var31 = (float)Math.cos(var52);
                  float var32 = (float)Math.sin(var52);
                  this.updateState2(
                     var41 * (this.volume17[0] * var31 + this.volume18[0] * var32),
                     var41 * (this.volume17[1] * var31 + this.volume18[1] * var32),
                     var41 * (this.volume17[2] * var31 + this.volume18[2] * var32)
                  );
                  this.volume19[var45] = (float)(var6 + this.level);
                  this.volume20[var45] = (float)(var8 + this.level2);
                  this.volume21[var45] = (float)(var10 + this.level3);
                  this.volume22[var45] = var13 * (0.05F + 0.2F * (float)Math.pow(var49, 1.2)) * VOLUME12[var39];
                  this.index2[var45] = this.helper37(this.helper23(var15, 0.25F, 1.0F), (float)Math.pow(var49, 1.6) * 0.32F * var14);
               }

               this.helper20(
                  var22, 24, this.volume19, this.volume20, this.volume21, this.volume22, this.index2
               );

               for (int var46 = 0; var46 < 24; var46++) {
                  float var50 = var46 / 23.0F;
                  this.volume22[var46] = var13 * (0.01F + 0.055F * (float)Math.pow(var50, 1.5)) * VOLUME12[var39];
                  this.index2[var46] = this.helper37(
                     this.helper23(var15, 0.2F + 0.65F * var50, 1.0F), (float)Math.pow(var50, 1.3) * 0.95F * var14
                  );
               }

               this.helper20(
                  var22, 24, this.volume19, this.volume20, this.volume21, this.volume22, this.index2
               );
               this.helper22(
                  var21,
                  this.volume19[23],
                  this.volume20[23],
                  this.volume21[23],
                  var19,
                  var20,
                  var13 * 0.22F * VOLUME12[var39],
                  this.helper23(var15, 0.92F, 0.85F * var14),
                  0
               );
               var22 = var21.peek().getPositionMatrix();
            }

            byte var38 = 5;

            for (int var40 = 0; var40 < 90; var40++) {
               float var42 = 0.90999997F;
               float var44 = (var12 * 0.26F * VOLUME15[var40] + VOLUME14[var40]) % var42;
               float var47 = 1.05F - var44;
               float var51 = VOLUME13[var40] + var12 * 1.9F * VOLUME15[var40];
               float var53 = 0.6F * var14 * Math.min(1.0F, 1.3F - var47 * 0.6F);
               if (!(var53 <= 0.01F)) {
                  for (int var54 = 0; var54 < 5; var54++) {
                     float var55 = var54 / 4.0F;
                     float var33 = var13 * (var47 + (1.0F - var55) * 0.14F);
                     float var34 = var51 - (1.0F - var55) * 0.22F;
                     float var35 = (float)Math.cos(var34);
                     float var36 = (float)Math.sin(var34);
                     this.updateState2(
                        var33 * (VALUE[var40][0] * var35 + VALUE2[var40][0] * var36),
                        var33 * (VALUE[var40][1] * var35 + VALUE2[var40][1] * var36),
                        var33 * (VALUE[var40][2] * var35 + VALUE2[var40][2] * var36)
                     );
                     this.volume19[var54] = (float)(var6 + this.level);
                     this.volume20[var54] = (float)(var8 + this.level2);
                     this.volume21[var54] = (float)(var10 + this.level3);
                     this.volume22[var54] = var13 * (0.006F + 0.02F * var55);
                     this.index2[var54] = this.helper37(this.helper23(var15, 0.4F + 0.4F * var55, 1.0F), var53 * var55);
                  }

                  this.helper20(
                     var22, 5, this.volume19, this.volume20, this.volume21, this.volume22, this.index2
                  );
               }
            }

            this.helper19(var21, var6, var8, var10, var13 * 0.86F, 1.5708F, var12 * 0.35F, var15, 0.42F * var14);
            this.helper19(var21, var6, var8, var10, var13 * 0.54F, var12 * 0.3F, -var12 * 0.5F + 0.6F, var15, 0.7F * var14);
            this.helper19(var21, var6, var8, var10, var13 * 0.32F, var12 * 0.55F, var12 * 0.9F, var15, var14);
            this.helper30();
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         }
      }
   }

   private void helper19(MatrixStack matrices, double cx, double cy, double cz, float s, float rxA, float ryA, int main, float glow) {
      if (!(glow <= 0.003F)) {
         float var13 = (float)Math.cos(rxA);
         float var14 = (float)Math.sin(rxA);
         float var15 = (float)Math.cos(ryA);
         float var16 = (float)Math.sin(ryA);
         float[][] var17 = new float[][]{{-s, -s}, {s, -s}, {s, s}, {-s, s}};
         double[] var18 = new double[4];
         double[] var19 = new double[4];
         double[] var20 = new double[4];

         for (int var21 = 0; var21 < 4; var21++) {
            float var22 = var17[var21][0];
            float var23 = var17[var21][1];
            float var24 = var23 * var13;
            float var25 = var23 * var14;
            this.updateState2(var22 * var15 + var25 * var16, var24, -var22 * var16 + var25 * var15);
            var18[var21] = cx + this.level;
            var19[var21] = cy + this.level2;
            var20[var21] = cz + this.level3;
         }

         Matrix4f var26 = matrices.peek().getPositionMatrix();

         for (int var27 = 0; var27 < 5; var27++) {
            int var31 = var27 % 4;
            this.volume19[var27] = (float)var18[var31];
            this.volume20[var27] = (float)var19[var31];
            this.volume21[var27] = (float)var20[var31];
         }

         for (int var28 = 0; var28 < 5; var28++) {
            this.volume22[var28] = s * 0.06F;
            this.index2[var28] = this.helper37(this.helper23(main, 0.3F, 1.0F), 0.25F * glow);
         }

         this.helper20(var26, 5, this.volume19, this.volume20, this.volume21, this.volume22, this.index2);

         for (int var29 = 0; var29 < 5; var29++) {
            this.volume22[var29] = s * 0.018F;
            this.index2[var29] = this.helper37(this.helper23(main, 0.5F, 1.0F), 0.9F * glow);
         }

         this.helper20(var26, 5, this.volume19, this.volume20, this.volume21, this.volume22, this.index2);
         float var30 = mc.gameRenderer.getCamera().getYaw();
         float var32 = mc.gameRenderer.getCamera().getPitch();

         for (int var33 = 0; var33 < 4; var33++) {
            this.helper22(
               matrices, var18[var33], var19[var33], var20[var33], var30, var32, s * 0.55F, this.helper23(main, 0.9F, 0.85F * glow), 0
            );
         }
      }
   }

   private void helper20(Matrix4f m, int n, float[] rx, float[] ry, float[] rz, float[] hw, int[] col) {
      if (n >= 2) {
         this.helper21(m, n, rx, ry, rz, hw, col, -1);
         this.helper21(m, n, rx, ry, rz, hw, col, 1);
      }
   }

   private void helper21(Matrix4f m, int n, float[] rx, float[] ry, float[] rz, float[] hw, int[] col, int sideSign) {
      BufferBuilder var9 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

      for (int var10 = 0; var10 < n; var10++) {
         float var11 = rx[var10];
         float var12 = ry[var10];
         float var13 = rz[var10];
         int var14 = Math.max(0, var10 - 1);
         int var15 = Math.min(n - 1, var10 + 1);
         float var16 = rx[var15] - rx[var14];
         float var17 = ry[var15] - ry[var14];
         float var18 = rz[var15] - rz[var14];
         float var19 = (float)Math.sqrt(var16 * var16 + var17 * var17 + var18 * var18);
         if (var19 < 1.0E-5F) {
            var16 = 0.0F;
            var17 = 1.0F;
            var18 = 0.0F;
            var19 = 1.0F;
         }

         var16 /= var19;
         var17 /= var19;
         var18 /= var19;
         float var20 = var11;
         float var21 = var12;
         float var22 = var13;
         float var23 = (float)Math.sqrt(var20 * var20 + var21 * var21 + var22 * var22);
         if (var23 < 1.0E-5F) {
            var20 = 0.0F;
            var21 = 0.0F;
            var22 = 1.0F;
         } else {
            var20 /= var23;
            var21 /= var23;
            var22 /= var23;
         }

         float var24 = var17 * var22 - var18 * var21;
         float var25 = var18 * var20 - var16 * var22;
         float var26 = var16 * var21 - var17 * var20;
         float var27 = (float)Math.sqrt(var24 * var24 + var25 * var25 + var26 * var26);
         if (var27 < 1.0E-5F) {
            var24 = 1.0F;
            var25 = 0.0F;
            var26 = 0.0F;
            var27 = 1.0F;
         }

         float var28 = hw[var10] / var27 * sideSign;
         var24 *= var28;
         var25 *= var28;
         var26 *= var28;
         int var29 = col[var10] & 16777215;
         if (sideSign < 0) {
            var9.vertex(m, var11 + var24, var12 + var25, var13 + var26).color(var29);
            var9.vertex(m, var11, var12, var13).color(col[var10]);
         } else {
            var9.vertex(m, var11, var12, var13).color(col[var10]);
            var9.vertex(m, var11 + var24, var12 + var25, var13 + var26).color(var29);
         }
      }

      BufferRenderer.drawWithGlobalProgram(var9.end());
   }

   private void helper22(
      MatrixStack matrices, double ox, double oy, double oz, float camYaw, float camPitch, float radius, int centerColor, int edgeColorUnused
   ) {
      int var13 = centerColor >>> 24;
      if (!(radius <= 0.001F) && var13 != 0) {
         int var14 = centerColor & 16777215;
         matrices.push();
         matrices.translate(ox, oy, oz);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camYaw));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camPitch));
         Matrix4f var15 = matrices.peek().getPositionMatrix();

         for (int var16 = 0; var16 < VOLUME16.length - 1; var16++) {
            float var17 = radius * VOLUME16[var16];
            float var18 = radius * VOLUME16[var16 + 1];
            int var19 = Math.round(var13 * VOLUME17[var16]) << 24 | var14;
            int var20 = Math.round(var13 * VOLUME17[var16 + 1]) << 24 | var14;
            BufferBuilder var21 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

            for (int var22 = 0; var22 <= 20; var22++) {
               float var23 = var22 / 20.0F * 6.2832F;
               float var24 = (float)Math.cos(var23);
               float var25 = (float)Math.sin(var23);
               var21.vertex(var15, var24 * var17, var25 * var17, 0.0F).color(var19);
               var21.vertex(var15, var24 * var18, var25 * var18, 0.0F).color(var20);
            }

            BufferRenderer.drawWithGlobalProgram(var21.end());
         }

         matrices.pop();
      }
   }

   private int helper23(int color, float m, float a) {
      int var4 = color >> 16 & 0xFF;
      int var5 = color >> 8 & 0xFF;
      int var6 = color & 0xFF;
      m = MathHelper.clamp(m, 0.0F, 1.0F);
      var4 = Math.round(var4 + (255 - var4) * m);
      var5 = Math.round(var5 + (255 - var5) * m);
      var6 = Math.round(var6 + (255 - var6) * m);
      int var7 = MathHelper.clamp((int)(a * 255.0F), 0, 255);
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   private boolean helper24() {
      return this.modeSetting.is("Вихрь")
         || this.modeSetting.is("Сфера")
         || this.modeSetting.is("ДНК")
         || this.modeSetting.is("Руны")
         || this.modeSetting.is("Орбиты")
         || this.modeSetting.is("Ромб");
   }

   private Vec3d helper25(float partialTicks) {
      LivingEntity var2 = this.livingEntity;
      return var2 != null && var2.isAlive()
         ? new Vec3d(
            MathHelper.lerp(partialTicks, var2.lastRenderX, var2.getX()),
            MathHelper.lerp(partialTicks, var2.lastRenderY, var2.getY()),
            MathHelper.lerp(partialTicks, var2.lastRenderZ, var2.getZ())
         )
         : this.vec3d;
   }

   private float helper26() {
      LivingEntity var1 = this.livingEntity;
      return var1 != null && var1.isAlive() ? var1.getHeight() : this.volume6;
   }

   private int helper27() {
      return this.overCol(this.resolveInt(), ColorUtils.rgb(255, 3, 3), this.helper40(this.livingEntity));
   }

   private float helper28() {
      return (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.floatSetting4.get();
   }

   private void helper29() {
      RenderSystem.depthMask(false);
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(770, 1);
      RenderSystem.disableCull();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
   }

   private void helper30() {
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.defaultBlendFunc();
   }

   private void helper31(Event3DRender event) {
      if (!(this.volume <= 0.001F)) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            float var4 = this.helper26();
            Vec3d var5 = mc.gameRenderer.getCamera().getPos();
            double var6 = var3.x - var5.x;
            double var8 = var3.y - var5.y;
            double var10 = var3.z - var5.z;
            double var12 = this.floatSetting5.get();
            float var14 = this.helper28();
            int var15 = this.helper27();
            Matrix4f var16 = event.getMatrices().peek().getPositionMatrix();
            this.helper29();
            RenderSystem.lineWidth(1.7F);
            byte var17 = 4;
            byte var18 = 48;

            for (int var19 = 0; var19 < var17; var19++) {
               double var20 = (Math.PI * 2) / var17 * var19;
               BufferBuilder var22 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

               for (int var23 = 0; var23 <= var18; var23++) {
                  double var24 = (double)var23 / var18;
                  double var26 = var20 + var24 * Math.PI * 3.0 + var14;
                  double var28 = var12 * (0.2 + 0.8 * Math.sin(var24 * Math.PI));
                  float var30 = (float)(var6 + Math.cos(var26) * var28);
                  float var31 = (float)(var10 + Math.sin(var26) * var28);
                  float var32 = (float)(var8 + var24 * var4);
                  var22.vertex(var16, var30, var32, var31).color(this.helper37(var15, this.volume * (float)(1.0 - Math.abs(var24 - 0.5))));
               }

               BufferRenderer.drawWithGlobalProgram(var22.end());
            }

            this.helper30();
         }
      }
   }

   private void helper32(Event3DRender event) {
      if (!(this.volume <= 0.001F)) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            float var4 = this.helper26();
            Vec3d var5 = mc.gameRenderer.getCamera().getPos();
            double var6 = var3.x - var5.x;
            double var8 = var3.y - var5.y + var4 * 0.5;
            double var10 = var3.z - var5.z;
            double var12 = Math.max(this.floatSetting5.get(), var4 * 0.55);
            float var14 = this.helper28();
            int var15 = this.helper27();
            Matrix4f var16 = event.getMatrices().peek().getPositionMatrix();
            this.helper29();
            RenderSystem.lineWidth(1.4F);
            byte var17 = 6;
            byte var18 = 7;
            byte var19 = 40;

            for (int var20 = 1; var20 < var17; var20++) {
               double var21 = Math.PI * var20 / var17;
               double var23 = Math.cos(var21) * var12;
               double var25 = Math.sin(var21) * var12;
               BufferBuilder var27 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

               for (int var28 = 0; var28 <= var19; var28++) {
                  double var29 = (Math.PI * 2) * var28 / var19 + var14 * 0.4;
                  var27.vertex(var16, (float)(var6 + Math.cos(var29) * var25), (float)(var8 + var23), (float)(var10 + Math.sin(var29) * var25))
                     .color(this.helper37(var15, this.volume * 0.85F));
               }

               BufferRenderer.drawWithGlobalProgram(var27.end());
            }

            for (int var35 = 0; var35 < var18; var35++) {
               double var36 = Math.PI * var35 / var18 + var14;
               double var37 = Math.cos(var36);
               double var38 = Math.sin(var36);
               BufferBuilder var39 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

               for (int var40 = 0; var40 <= var19; var40++) {
                  double var41 = (Math.PI * 2) * var40 / var19;
                  double var31 = Math.sin(var41) * var12;
                  double var33 = Math.cos(var41) * var12;
                  var39.vertex(var16, (float)(var6 + var37 * var31), (float)(var8 + var33), (float)(var10 + var38 * var31))
                     .color(this.helper37(var15, this.volume * 0.85F));
               }

               BufferRenderer.drawWithGlobalProgram(var39.end());
            }

            this.helper30();
         }
      }
   }

   private void helper33(Event3DRender event) {
      if (!(this.volume <= 0.001F)) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            float var4 = this.helper26();
            Vec3d var5 = mc.gameRenderer.getCamera().getPos();
            double var6 = var3.x - var5.x;
            double var8 = var3.y - var5.y;
            double var10 = var3.z - var5.z;
            double var12 = this.floatSetting5.get() * 0.6;
            float var14 = this.helper28();
            int var15 = this.helper27();
            Matrix4f var16 = event.getMatrices().peek().getPositionMatrix();
            byte var17 = 44;
            double var18 = 2.5;
            this.helper29();
            RenderSystem.lineWidth(1.8F);

            for (double var20 = 0.0; var20 < Math.PI * 2; var20 += Math.PI) {
               BufferBuilder var22 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

               for (int var23 = 0; var23 <= var17; var23++) {
                  double var24 = (double)var23 / var17;
                  double var26 = var24 * var18 * Math.PI * 2.0 + var14 + var20;
                  var22.vertex(var16, (float)(var6 + Math.cos(var26) * var12), (float)(var8 + var24 * var4), (float)(var10 + Math.sin(var26) * var12))
                     .color(this.helper37(var15, this.volume));
               }

               BufferRenderer.drawWithGlobalProgram(var22.end());
            }

            RenderSystem.lineWidth(1.4F);
            BufferBuilder var28 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            for (byte var21 = 0; var21 <= var17; var21 += 3) {
               double var29 = (double)var21 / var17;
               double var30 = var29 * var18 * Math.PI * 2.0 + var14;
               float var31 = (float)(var8 + var29 * var4);
               int var27 = this.helper37(var15, this.volume * 0.7F);
               var28.vertex(var16, (float)(var6 + Math.cos(var30) * var12), var31, (float)(var10 + Math.sin(var30) * var12)).color(var27);
               var28.vertex(var16, (float)(var6 + Math.cos(var30 + Math.PI) * var12), var31, (float)(var10 + Math.sin(var30 + Math.PI) * var12)).color(var27);
            }

            BufferRenderer.drawWithGlobalProgram(var28.end());
            this.helper30();
         }
      }
   }

   private void helper34(Event3DRender event) {
      if (!(this.volume <= 0.001F)) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            Vec3d var4 = mc.gameRenderer.getCamera().getPos();
            double var5 = var3.x - var4.x;
            double var7 = var3.y - var4.y + 0.03;
            double var9 = var3.z - var4.z;
            double var11 = this.floatSetting5.get();
            float var13 = this.helper28();
            int var14 = this.helper27();
            Matrix4f var15 = event.getMatrices().peek().getPositionMatrix();
            this.helper29();
            RenderSystem.lineWidth(1.6F);
            this.helper35(var15, var5, var7, var9, var11, 0.0, 0, this.helper37(var14, this.volume * 0.9F));
            this.helper35(var15, var5, var7, var9, var11 * 0.82, var13, 2, this.helper37(var14, this.volume));
            this.helper35(var15, var5, var7, var9, var11 * 0.6, -var13 * 1.3, 0, this.helper37(var14, this.volume * 0.8F));
            BufferBuilder var16 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            byte var17 = 12;

            for (int var18 = 0; var18 < var17; var18++) {
               double var19 = (Math.PI * 2) * var18 / var17 - var13 * 1.3;
               int var21 = this.helper37(var14, this.volume * 0.7F);
               var16.vertex(var15, (float)(var5 + Math.cos(var19) * var11 * 0.6), (float)var7, (float)(var9 + Math.sin(var19) * var11 * 0.6)).color(var21);
               var16.vertex(var15, (float)(var5 + Math.cos(var19) * var11 * 0.82), (float)var7, (float)(var9 + Math.sin(var19) * var11 * 0.82)).color(var21);
            }

            BufferRenderer.drawWithGlobalProgram(var16.end());
            RenderSystem.lineWidth(1.8F);
            BufferBuilder var22 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

            for (int var23 = 0; var23 <= 3; var23++) {
               double var20 = (Math.PI * 2) * var23 / 3.0 + var13 * 0.6;
               var22.vertex(var15, (float)(var5 + Math.cos(var20) * var11 * 0.7), (float)var7, (float)(var9 + Math.sin(var20) * var11 * 0.7))
                  .color(this.helper37(var14, this.volume * 0.85F));
            }

            BufferRenderer.drawWithGlobalProgram(var22.end());
            this.helper30();
         }
      }
   }

   private void helper35(Matrix4f m, double x, double y, double z, double radius, double rot, int dash, int color) {
      byte var14 = 72;
      if (dash > 0) {
         BufferBuilder var15 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

         for (int var16 = 0; var16 < var14; var16++) {
            if (var16 / dash % 2 != 1) {
               double var17 = (Math.PI * 2) * var16 / var14 + rot;
               double var19 = (Math.PI * 2) * (var16 + 1) / var14 + rot;
               var15.vertex(m, (float)(x + Math.cos(var17) * radius), (float)y, (float)(z + Math.sin(var17) * radius)).color(color);
               var15.vertex(m, (float)(x + Math.cos(var19) * radius), (float)y, (float)(z + Math.sin(var19) * radius)).color(color);
            }
         }

         BufferRenderer.drawWithGlobalProgram(var15.end());
      } else {
         BufferBuilder var21 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

         for (int var22 = 0; var22 <= var14; var22++) {
            double var23 = (Math.PI * 2) * var22 / var14 + rot;
            var21.vertex(m, (float)(x + Math.cos(var23) * radius), (float)y, (float)(z + Math.sin(var23) * radius)).color(color);
         }

         BufferRenderer.drawWithGlobalProgram(var21.end());
      }
   }

   private void helper36(Event3DRender event) {
      if (!(this.volume <= 0.001F) && this.vec3d != null) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         LivingEntity var5 = this.livingEntity;
         Vec3d var3;
         float var4;
         if (var5 != null && var5.isAlive()) {
            var3 = new Vec3d(
               MathHelper.lerp(var2, var5.lastRenderX, var5.getX()),
               MathHelper.lerp(var2, var5.lastRenderY, var5.getY()),
               MathHelper.lerp(var2, var5.lastRenderZ, var5.getZ())
            );
            var4 = var5.getHeight();
         } else {
            var3 = this.vec3d;
            var4 = this.volume6;
         }

         Vec3d var6 = mc.gameRenderer.getCamera().getPos();
         double var7 = var3.x - var6.x;
         double var9 = var3.y - var6.y;
         double var11 = var3.z - var6.z;
         double var13 = 2000.0 / this.floatSetting3.get();
         double var15 = System.currentTimeMillis() % (long)var13;
         boolean var17 = var15 > var13 / 2.0;
         double var18 = var15 / (var13 / 2.0);
         if (var17) {
            var18--;
         } else {
            var18 = 1.0 - var18;
         }

         var18 = var18 < 0.5 ? 2.0 * var18 * var18 : 1.0 - Math.pow(-2.0 * var18 + 2.0, 2.0) / 2.0;
         double var20 = var4 / 1.2 * (var18 > 0.5 ? 1.0 - var18 : var18) * (var17 ? -1 : 1);
         int var22 = this.resolveInt();
         float var23 = this.helper40(var5);
         int var24 = ColorUtils.rgb(255, 3, 3);
         int var25 = this.overCol(var22, var24, var23);
         int var26 = this.helper37(var25, 0.88235295F * this.volume);
         int var27 = this.helper37(var25, 0.003921569F * this.volume);
         int var28 = this.helper37(var25, this.volume);
         double var29 = this.floatSetting2.get();
         if (this.modeSetting.is("Кольцо 2")) {
            this.helper38(var3, var3.y + var4 * var18, var20, var29);
         }

         MatrixStack var31 = event.getMatrices();
         Matrix4f var32 = var31.peek().getPositionMatrix();
         RenderSystem.depthMask(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         if (!this.modeSetting.is("Кольцо 2")) {
            BufferBuilder var33 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

            for (int var34 = 0; var34 <= 360; var34++) {
               double var35 = Math.toRadians(var34);
               float var37 = (float)(var7 + Math.cos(var35) * var29);
               float var38 = (float)(var11 + Math.sin(var35) * var29);
               float var39 = (float)(var9 + var4 * var18);
               float var40 = (float)(var9 + var4 * var18 + var20);
               var33.vertex(var32, var37, var39, var38).color(var26);
               var33.vertex(var32, var37, var40, var38).color(var27);
            }

            BufferRenderer.drawWithGlobalProgram(var33.end());
         }

         if (this.modeSetting.is("Кольцо 2")) {
            float var45 = 0.1F;

            for (int var47 = 0; var47 < 2; var47++) {
               BufferBuilder var49 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

               for (int var36 = 0; var36 <= 360; var36++) {
                  double var51 = Math.toRadians(var36);
                  float var54 = (float)(var7 + Math.cos(var51) * var29);
                  float var56 = (float)(var11 + Math.sin(var51) * var29);
                  float var41 = (float)(var9 + var4 * var18);
                  float var42 = var41 + (var47 == 0 ? -var45 : var45);
                  var49.vertex(var32, var54, var41, var56).color(var28);
                  var49.vertex(var32, var54, var42, var56).color(var27);
               }

               BufferRenderer.drawWithGlobalProgram(var49.end());
            }
         }

         RenderSystem.lineWidth(1.5F);
         BufferBuilder var46 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

         for (int var48 = 0; var48 <= 360; var48++) {
            double var50 = Math.toRadians(var48);
            float var52 = (float)(var7 + Math.cos(var50) * var29);
            float var53 = (float)(var11 + Math.sin(var50) * var29);
            float var55 = (float)(var9 + var4 * var18);
            var46.vertex(var32, var52, var55, var53).color(var28);
         }

         BufferRenderer.drawWithGlobalProgram(var46.end());
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
      }
   }

   private int helper37(int color, float alpha) {
      alpha = Math.max(0.0F, Math.min(1.0F, alpha));
      return color & 16777215 | (int)(alpha * 255.0F) << 24;
   }

   private void helper38(Vec3d center, double ringY, double trail, double radius) {
      ThreadLocalRandom var8 = ThreadLocalRandom.current();
      this.volume24 = this.volume24 + this.volume23 * 500.0F * this.volume;
      int var9 = (int)this.volume24;
      this.volume24 -= var9;

      for (int var10 = 0; var10 < var9 && this.doubles.size() < 350; var10++) {
         double var11 = var8.nextDouble() * Math.PI * 2.0;
         double var13 = var8.nextDouble();
         double var15 = 0.35 + var8.nextDouble() * 0.45;
         this.doubles
            .add(
               new double[]{
                  center.x + Math.cos(var11) * radius,
                  ringY + trail * var13,
                  center.z + Math.sin(var11) * radius,
                  0.0,
                  Math.signum(trail) * (0.35 + var8.nextDouble() * 0.5),
                  0.0,
                  var13 * var15 * 0.75,
                  var15,
                  0.04 + var8.nextDouble() * 0.055
               }
            );
      }
   }

   private void helper39(Event3DRender event) {
      if (this.doubles.isEmpty()) {
         this.timestamp3 = 0L;
      } else {
         long var2 = System.nanoTime();
         this.volume23 = this.timestamp3 == 0L ? 0.016666668F : MathHelper.clamp((float)(var2 - this.timestamp3) / 1.0E9F, 0.0F, 0.05F);
         this.timestamp3 = var2;
         float var4 = this.volume23;
         double var5 = Math.exp(-var4 * 1.6);
         Iterator var7 = this.doubles.iterator();

         while (var7.hasNext()) {
            double[] var8 = (double[])var7.next();
            var8[6] += var4;
            if (var8[6] >= var8[7]) {
               var7.remove();
            } else {
               var8[3] *= var5;
               var8[4] *= var5;
               var8[5] *= var5;
               var8[0] += var8[3] * var4;
               var8[1] += var8[4] * var4;
               var8[2] += var8[5] * var4;
            }
         }

         if (!this.doubles.isEmpty()) {
            Camera var30 = mc.gameRenderer.getCamera();
            Vec3d var31 = var30.getPos();
            Vector3f var9 = new Vector3f(1.0F, 0.0F, 0.0F).rotate(var30.getRotation());
            Vector3f var10 = new Vector3f(0.0F, 1.0F, 0.0F).rotate(var30.getRotation());
            int var11 = this.resolveInt();
            if (this.livingEntity != null) {
               var11 = this.overCol(var11, ColorUtils.rgb(255, 3, 3), this.helper40(this.livingEntity));
            }

            int var12 = var11 >> 16 & 0xFF;
            int var13 = var11 >> 8 & 0xFF;
            int var14 = var11 & 0xFF;
            Matrix4f var15 = event.getMatrices().peek().getPositionMatrix();
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.disableCull();
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder var16 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

            for (double[] var18 : this.doubles) {
               float var19 = 1.0F - (float)(var18[6] / var18[7]);
               int var20 = (int)(MathHelper.clamp(var19 * 0.95F, 0.0F, 1.0F) * 255.0F);
               if (var20 > 2) {
                  float var21 = (float)var18[8] * (0.35F + 0.65F * var19);
                  float var22 = (float)(var18[0] - var31.x);
                  float var23 = (float)(var18[1] - var31.y);
                  float var24 = (float)(var18[2] - var31.z);

                  for (int var25 = 0; var25 < 8; var25++) {
                     float var26 = VOLUME18[var25] * var21;
                     float var27 = VOLUME19[var25] * var21;
                     float var28 = VOLUME18[var25 + 1] * var21;
                     float var29 = VOLUME19[var25 + 1] * var21;
                     var16.vertex(var15, var22, var23, var24).color(var12, var13, var14, var20);
                     var16.vertex(
                           var15, var22 + var9.x * var26 + var10.x * var27, var23 + var9.y * var26 + var10.y * var27, var24 + var9.z * var26 + var10.z * var27
                        )
                        .color(var12, var13, var14, 0);
                     var16.vertex(
                           var15, var22 + var9.x * var28 + var10.x * var29, var23 + var9.y * var28 + var10.y * var29, var24 + var9.z * var28 + var10.z * var29
                        )
                        .color(var12, var13, var14, 0);
                  }
               }
            }

            BuiltBuffer var32 = var16.endNullable();
            if (var32 != null) {
               BufferRenderer.drawWithGlobalProgram(var32);
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
         }
      }
   }

   @EventLink(priority = -100)
   public void onRender2D(EventRender.Default event) {
      if (this.modeSetting.is("Кристаллы") && !(this.volume9 <= 0.001F) && this.vec3d != null) {
         LivingEntity var2 = this.livingEntity != null && this.livingEntity.isAlive() ? this.livingEntity : null;
         this.helper51(event.getContext().getMatrices(), var2);
      }
   }

   private int resolveInt2(int color, float mult) {
      int var3 = (int)((color >> 24 & 0xFF) * mult);
      var3 = Math.max(0, Math.min(255, var3));
      return var3 << 24 | color & 16777215;
   }

   private int resolveInt3(int color, int alpha) {
      alpha = Math.max(0, Math.min(255, alpha));
      return alpha << 24 | color & 16777215;
   }

   int overCol(int color1, int color2, float factor) {
      factor = Math.max(0.0F, Math.min(1.0F, factor));
      int var4 = color1 >> 16 & 0xFF;
      int var5 = color1 >> 8 & 0xFF;
      int var6 = color1 & 0xFF;
      int var7 = color1 >> 24 & 0xFF;
      int var8 = color2 >> 16 & 0xFF;
      int var9 = color2 >> 8 & 0xFF;
      int var10 = color2 & 0xFF;
      int var11 = color2 >> 24 & 0xFF;
      int var12 = (int)(var4 + (var8 - var4) * factor);
      int var13 = (int)(var5 + (var9 - var5) * factor);
      int var14 = (int)(var6 + (var10 - var6) * factor);
      int var15 = (int)(var7 + (var11 - var7) * factor);
      return var15 << 24 | var12 << 16 | var13 << 8 | var14;
   }

   private float helper40(LivingEntity target) {
      if (this.booleanSetting.isState() && target != null) {
         float var2 = mc != null ? mc.getRenderTickCounter().getTickDelta(true) : 0.0F;
         float var3 = MathHelper.clamp(target.hurtTime - var2, 0.0F, 10.0F);
         float var4 = var3 / 10.0F;
         return var4 * var4 * (3.0F - 2.0F * var4);
      } else {
         return 0.0F;
      }
   }

   private void updateState3(
      MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float baseScreenSize, int color, float rotation
   ) {
      float var12 = this.helper7(cameraPos, worldX, worldY, worldZ);
      float var13 = baseScreenSize * var12 * 0.5F;
      this.helper41(matrices, cameraPos, worldX, worldY, worldZ, var13, color, rotation);
   }

   private void updateState4(
      MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float worldSize, int color, float rotation
   ) {
      float var12 = worldSize * 0.5F;
      this.helper41(matrices, cameraPos, worldX, worldY, worldZ, var12, color, rotation);
   }

   private void helper41(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float half, int color, float rotation) {
      int var12 = color >> 16 & 0xFF;
      int var13 = color >> 8 & 0xFF;
      int var14 = color & 0xFF;
      int var15 = color >> 24 & 0xFF;
      if (var15 > 0) {
         matrices.push();
         matrices.translate(worldX - cameraPos.x, worldY - cameraPos.y, worldZ - cameraPos.z);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
         if (rotation != 0.0F) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
         }

         Matrix4f var16 = matrices.peek().getPositionMatrix();
         BufferBuilder var17 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var17.vertex(var16, -half, -half, 0.0F).texture(0.0F, 1.0F).color(var12, var13, var14, var15);
         var17.vertex(var16, -half, half, 0.0F).texture(0.0F, 0.0F).color(var12, var13, var14, var15);
         var17.vertex(var16, half, half, 0.0F).texture(1.0F, 0.0F).color(var12, var13, var14, var15);
         var17.vertex(var16, half, -half, 0.0F).texture(1.0F, 1.0F).color(var12, var13, var14, var15);
         BufferRenderer.drawWithGlobalProgram(var17.end());
         matrices.pop();
      }
   }

   private void helper42(Event3DRender event) {
      if (this.vec3d != null && !(this.volume <= 0.001F)) {
         Vec3d var2 = mc.gameRenderer.getCamera().getPos();
         double var3 = this.vec3d.x;
         double var5 = this.vec3d.y + (this.volume6 + 0.4F) * 0.5F;
         double var7 = this.vec3d.z;
         float var9 = this.floatSetting.getValue().floatValue() * 12.0F;
         float var10 = var9 * this.volume2;
         long var11 = System.currentTimeMillis();
         float var13 = Math.max(0.001F, (float)(var11 - this.timestamp) / 1000.0F);
         this.timestamp = var11;
         float var14 = Math.max(0.35F, 2.2F / this.floatSetting4.getValue().floatValue());

         for (this.volume3 += var13 / var14; this.volume3 >= 1.0F; this.volume5 = this.volume5 > 0.0F ? -280.0F : 280.0F) {
            this.volume3--;
            this.volume4 = this.volume5;
         }

         float var15 = (float)Easings.SINE_IN_OUT.ease(this.volume3);
         float var16 = MathHelper.lerp(var15, this.volume4, this.volume5);
         boolean var17 = this.helper2();
         float var18 = this.helper40(this.livingEntity);
         int var19 = this.resolveInt2(var17 ? -1 : this.resolveInt(), this.volume);
         int var20 = this.resolveInt2(ColorUtils.rgb(255, 3, 3), this.volume);
         int var21 = this.overCol(var19, var20, var18);
         RenderSystem.enableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, var17 ? 771 : 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.helper3());
         this.updateState3(event.getMatrices(), var2, var3, var5, var7, var10, var21, var16);
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private int resolveInt4(int color, float m, float a) {
      int var4 = Math.round((color >> 16 & 0xFF) * m);
      int var5 = Math.round((color >> 8 & 0xFF) * m);
      int var6 = Math.round((color & 0xFF) * m);
      int var7 = MathHelper.clamp((int)(a * 255.0F), 0, 255);
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   private void helper43(Event3DRender event) {
      if (!(this.volume <= 0.001F) && this.vec3d != null) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         Vec3d var3 = this.helper25(var2);
         if (var3 != null) {
            float var4 = this.helper26();
            Vec3d var5 = mc.gameRenderer.getCamera().getPos();
            float var6 = this.floatSetting5.get() * 1.35F;
            float var7 = this.volume;
            float var8 = this.helper40(this.livingEntity);
            int var9 = this.resolveInt2(this.resolveInt(), var7);
            int var10 = this.resolveInt2(ColorUtils.rgb(255, 3, 3), var7);
            int var11 = this.overCol(var9, var10, var8);
            float var12 = this.helper28() * 45.0F % 360.0F;
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, TEXTURE_ID);
            this.updateState4(event.getMatrices(), var5, var3.x, var3.y + var4 * 0.55, var3.z, var6 * 2.0F, var11, var12);
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
         }
      }
   }

   private void updateState5(Matrix4f m, float[][] v, float scale, int colTop, int colBottom, int colSides) {
      BufferBuilder var7 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

      for (int var8 = 0; var8 < 4; var8++) {
         int var9 = (var8 + 1) % 4;
         var7.vertex(m, 0.0F, 0.0F, 0.0F).color(colTop);
         var7.vertex(m, v[var8][0] * scale, v[var8][1] * scale, 0.0F).color(var8 != 0 && var8 != 2 ? colSides : (var8 == 0 ? colTop : colBottom));
         var7.vertex(m, v[var9][0] * scale, v[var9][1] * scale, 0.0F).color(var9 != 0 && var9 != 2 ? colSides : (var9 == 0 ? colTop : colBottom));
      }

      BufferRenderer.drawWithGlobalProgram(var7.end());
   }

   private void updateState6(Matrix4f m, float[][] v, float scale, int color) {
      BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

      for (int var6 = 0; var6 < 4; var6++) {
         int var7 = (var6 + 1) % 4;
         var5.vertex(m, 0.0F, 0.0F, 0.0F).color(color);
         var5.vertex(m, v[var6][0] * scale, v[var6][1] * scale, 0.0F).color(color);
         var5.vertex(m, v[var7][0] * scale, v[var7][1] * scale, 0.0F).color(color);
      }

      BufferRenderer.drawWithGlobalProgram(var5.end());
   }

   private void updateState7(Matrix4f m, float[][] v, float scale, int color) {
      BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

      for (int var6 = 0; var6 <= 4; var6++) {
         int var7 = var6 % 4;
         var5.vertex(m, v[var7][0] * scale, v[var7][1] * scale, 0.0F).color(color);
      }

      BufferRenderer.drawWithGlobalProgram(var5.end());
   }

   private void helper44(Event3DRender event) {
      if (!(this.volume <= 0.001F) && this.vec3d != null) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         LivingEntity var5 = this.livingEntity;
         Vec3d var3;
         float var4;
         if (var5 != null && var5.isAlive()) {
            var3 = new Vec3d(
               MathHelper.lerp(var2, var5.lastRenderX, var5.getX()),
               MathHelper.lerp(var2, var5.lastRenderY, var5.getY()),
               MathHelper.lerp(var2, var5.lastRenderZ, var5.getZ())
            );
            var4 = var5.getHeight();
         } else {
            var3 = this.vec3d;
            var4 = this.volume6;
         }

         Vec3d var6 = mc.gameRenderer.getCamera().getPos();
         double var7 = var3.x;
         double var9 = var3.y + var4 / 2.0F;
         double var11 = var3.z;
         double var13 = 0.7;
         float var15 = 2.0F;
         long var16 = System.currentTimeMillis();
         float var18 = this.helper40(var5);
         int var19 = this.resolveInt();
         int var20 = ColorUtils.rgb(255, 3, 3);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.helper4());
         MatrixStack var21 = event.getMatrices();

         for (int var22 = 0; var22 < 20; var22++) {
            float var23 = 1.0F - var22 / 20.0F * 0.7F;
            double var24 = 0.15 * (var16 - var22 * 10.0) / 25.0;
            double var26 = Math.sin(var24) * var13;
            double var28 = Math.cos(var24) * var13;
            double var30 = var7 + var26;
            double var32 = var9 + var28;
            double var34 = var11 - var28;
            float var36 = var15 * var23;
            float var37 = this.volume * 0.6F;
            int var38 = this.resolveInt2(var19, var37 * this.volume);
            int var39 = this.resolveInt2(var20, var37 * this.volume);
            int var40 = this.overCol(var38, var39, var18);
            this.updateState4(var21, var6, var30, var32, var34, var36 * 0.12F, var40, 0.0F);
            int var41 = this.resolveInt2(var40, 0.45F);
            this.updateState4(var21, var6, var30, var32, var34, var36 * 0.21F, var41, 0.0F);
         }

         for (int var42 = 0; var42 < 20; var42++) {
            float var44 = 1.0F - var42 / 20.0F * 0.7F;
            double var46 = 0.15 * (var16 - var42 * 10.0) / 25.0;
            double var48 = Math.sin(var46) * var13;
            double var50 = Math.cos(var46) * var13;
            double var52 = var7 - var48;
            double var54 = var9 + var48;
            double var56 = var11 - var50;
            float var58 = var15 * var44;
            float var60 = this.volume * 0.6F;
            int var62 = this.resolveInt2(var19, var60 * this.volume);
            int var64 = this.resolveInt2(ColorUtils.rgb(235, 7, 7), var60 * this.volume);
            int var66 = this.overCol(var62, var64, var18);
            this.updateState4(var21, var6, var52, var54, var56, var58 * 0.12F, var66, 0.0F);
            int var68 = this.resolveInt2(var66, 0.45F);
            this.updateState4(var21, var6, var52, var54, var56, var58 * 0.21F, var68, 0.0F);
         }

         for (int var43 = 0; var43 < 20; var43++) {
            float var45 = 1.0F - var43 / 20.0F * 0.7F;
            double var47 = 0.15 * (var16 - var43 * 10.0) / 25.0;
            double var49 = Math.sin(var47) * var13;
            double var51 = Math.cos(var47) * var13;
            double var53 = var7 - var49;
            double var55 = var9 - var49;
            double var57 = var11 + var51;
            float var59 = var15 * var45;
            float var61 = this.volume * 0.6F;
            int var63 = this.resolveInt2(var19, var61 * this.volume);
            int var65 = this.resolveInt2(var20, var61 * this.volume);
            int var67 = this.overCol(var63, var65, var18);
            this.updateState4(var21, var6, var53, var55, var57, var59 * 0.12F, var67, 0.0F);
            int var69 = this.resolveInt2(var67, 0.45F);
            this.updateState4(var21, var6, var53, var55, var57, var59 * 0.21F, var69, 0.0F);
         }

         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
      }
   }

   private void helper45(LivingEntity entity, float partialTicks, int cornersCount, int maxTime, int colorBase) {
      float var6 = 0.2F;
      float var7 = entity.getHeight();
      int var8 = (int)this.xZSetting.getValue().floatValue();
      int var9 = (int)this.ySetting.getValue().floatValue();
      long var10 = System.currentTimeMillis();
      float var12 = (float)(var10 % var8) / var8;
      float var13 = var12 * 60.0F;
      float var14 = (float)(var10 % var9) / var9;
      float var15 = 0.5F - 0.5F * MathHelper.cos(var14 * (float) Math.PI);

      for (int var16 = 0; var16 < cornersCount; var16++) {
         float var17 = (float)var16 / cornersCount;
         double var18 = Math.toRadians(MathHelper.wrapDegrees(var17 * 360.0F + var13));
         float var20 = -((float)Math.sin(var18)) * var6;
         float var21 = var7 * var15;
         float var22 = (float)Math.cos(var18) * var6;
         this.targetESPs.add(new TargetESP.GlowPoint(var20, var21, var22, maxTime, colorBase));
      }
   }

   private void helper46(Event3DRender event) {
      if (!this.targetESPs.isEmpty() && !(this.volume <= 0.001F)) {
         LivingEntity var2 = this.livingEntity != null ? this.livingEntity : this.livingEntity2;
         if (var2 != null || this.vec3d != null) {
            float var3 = mc.getRenderTickCounter().getTickDelta(true);
            Vec3d var4;
            if (var2 != null && var2.isAlive()) {
               var4 = new Vec3d(
                  MathHelper.lerp(var3, var2.lastRenderX, var2.getX()),
                  MathHelper.lerp(var3, var2.lastRenderY, var2.getY()),
                  MathHelper.lerp(var3, var2.lastRenderZ, var2.getZ())
               );
            } else {
               var4 = this.vec3d;
            }

            if (var4 != null) {
               Vec3d var5 = mc.gameRenderer.getCamera().getPos();
               float var6 = this.helper40(var2);
               float var7 = 6.0F;
               RenderSystem.disableDepthTest();
               RenderSystem.enableBlend();
               RenderSystem.depthMask(false);
               RenderSystem.disableCull();
               RenderSystem.blendFunc(770, 1);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
               RenderSystem.setShaderTexture(0, this.helper4());
               MatrixStack var8 = event.getMatrices();

               for (TargetESP.GlowPoint var10 : this.targetESPs) {
                  float var11 = var10.getTimeProgress();
                  float var12 = 1.0F - var11 * 0.6F;
                  double var13 = var4.x + var10.x;
                  double var15 = var4.y + var10.y;
                  double var17 = var4.z + var10.z;
                  float var19 = var7 * var12;
                  int var20 = (int)(255.0F * this.volume * var12 * 0.8F);
                  var20 = Math.max(0, Math.min(255, var20));
                  int var21 = this.resolveInt3(var10.baseColor, var20);
                  int var22 = this.resolveInt3(ColorUtils.rgb(255, 3, 3), var20);
                  int var23 = this.overCol(var21, var22, var6);
                  this.updateState3(var8, var5, var13, var15, var17, var19, var23, 0.0F);
               }

               RenderSystem.enableCull();
               RenderSystem.enableDepthTest();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
               RenderSystem.depthMask(true);
            }
         }
      }
   }

   private void helper47(Event3DRender event) {
      if (!(this.volume <= 0.001F) && this.vec3d != null) {
         float var2 = mc.getRenderTickCounter().getTickDelta(true);
         LivingEntity var5 = this.livingEntity;
         Vec3d var3;
         if (var5 != null && var5.isAlive()) {
            var3 = new Vec3d(
               MathHelper.lerp(var2, var5.lastRenderX, var5.getX()),
               MathHelper.lerp(var2, var5.lastRenderY, var5.getY()),
               MathHelper.lerp(var2, var5.lastRenderZ, var5.getZ())
            );
            float var49 = var5.getHeight();
         } else {
            var3 = this.vec3d;
            float var4 = this.volume6;
         }

         Vec3d var6 = mc.gameRenderer.getCamera().getPos();
         double var7 = var3.x;
         double var9 = var3.y;
         double var11 = var3.z;
         double var13 = System.currentTimeMillis() / 384.61539872299335 * 1.2F;
         double var15 = System.currentTimeMillis() / 666.6666666666666 * 1.2F;
         int var17 = this.resolveInt();
         float var18 = 4.0F;
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.helper4());
         MatrixStack var19 = event.getMatrices();
         float var20 = 0.65F;

         for (int var21 = 0; var21 < 4; var21++) {
            for (int var22 = 0; var22 < 20; var22++) {
               float var23 = var22 / 20.0F;
               float var24 = 1.0F - var23 * 0.55F;
               double var25 = var13 - var22 * 0.05;
               double var27 = var15 - var22 * 0.05;
               double var29 = (Math.sin(var27) + 1.0) * 0.5;
               double var31 = Math.toRadians(var21 * 90.0 + var25 * 50.0 % 360.0);
               double var33 = Math.cos(var31) * var20;
               double var35 = Math.sin(var31) * var20;
               double var37 = var21 % 2 == 0 ? 0.1 + 1.7 * var29 : 1.8 - 1.7 * var29;
               double var39 = var7 + var33;
               double var41 = var9 + var37;
               double var43 = var11 + var35;
               float var45 = var18 * var24;
               int var46 = (int)(255.0F * this.volume * 0.6F);
               int var47 = this.resolveInt3(var17, var46);
               this.updateState3(var19, var6, var39, var41, var43, var45, var47, 0.0F);
               int var48 = this.resolveInt2(var47, 0.45F);
               this.updateState3(var19, var6, var39, var41, var43, var45 * 1.75F, var48, 0.0F);
            }

            var20 *= -1.0F;
         }

         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
      }
   }

   private void helper48(MatrixStack ms, LivingEntity target, float partialTicks) {
      if (this.vec3d != null && !(this.volume9 <= 0.01F)) {
         Vec3d var4 = mc.gameRenderer.getCamera().getPos();
         int var5 = ColorUtils.getThemeColor();
         int var6 = this.resolveInt2(var5, this.volume9);
         int var7 = this.resolveInt2(var5, this.volume9 * 0.28F);
         float var8 = this.helper40(target);
         if (var8 > 0.0F) {
            int var9 = this.resolveInt2(ColorUtils.rgb(255, 3, 3), this.volume9);
            var6 = this.overCol(var6, var9, var8);
            var7 = this.overCol(var7, this.resolveInt2(var9, 0.65F), var8);
         }

         float var35 = target != null ? target.getWidth() : this.volume7;
         float var10 = target != null ? target.getHeight() : this.volume6;
         float var11 = var35 * 1.5F;
         Vec3d var12;
         if (target != null && target.isAlive()) {
            var12 = new Vec3d(
               MathHelper.lerp(partialTicks, target.lastRenderX, target.getX()),
               MathHelper.lerp(partialTicks, target.lastRenderY, target.getY()),
               MathHelper.lerp(partialTicks, target.lastRenderZ, target.getZ())
            );
         } else {
            var12 = this.vec3d;
         }

         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         float var13 = 1.2F - 0.5F * this.volume9;
         ms.push();
         ms.translate(var12.x - var4.x, var12.y - var4.y, var12.z - var4.z);
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

         for (byte var15 = 0; var15 < 360; var15 += 20) {
            float var16 = (float)Math.toRadians(var15 + this.volume8);
            float var17 = (float)(Math.sin(var16) * var11 * var13);
            float var18 = (float)(Math.cos(var16) * var11 * var13);
            float var19 = 0.1F;
            float var20 = 0.1F + var10 * Math.abs(MathHelper.sin(var15));
            float var21 = var17;
            float var22 = var20;
            float var23 = var18;
            float var24 = var10 / 2.0F;
            float var25 = -var21;
            float var26 = var24 - var22;
            float var27 = -var23;
            float var28 = (float)Math.sqrt(var25 * var25 + var26 * var26 + var27 * var27);
            if (!(var28 < 0.001F)) {
               var25 /= var28;
               var26 /= var28;
               var27 /= var28;
               ms.push();
               ms.translate(var21, var22, var23);
               Vector3f var29 = new Vector3f(0.0F, 1.0F, 0.0F);
               Vector3f var30 = new Vector3f(var25, var26, var27);
               Vector3f var31 = new Vector3f();
               var29.cross(var30, var31);
               float var32 = var31.length();
               if (var32 >= 0.001F) {
                  var31.div(var32);
                  float var33 = Math.max(-1.0F, Math.min(1.0F, var29.dot(var30)));
                  float var34 = (float)Math.acos(var33);
                  ms.multiply(new Quaternionf().setAngleAxis(var34, var31.x, var31.y, var31.z));
               }

               this.helper49(var14, ms.peek().getPositionMatrix(), var19, var6);
               ms.pop();
            }
         }

         BufferRenderer.drawWithGlobalProgram(var14.end());
         ms.pop();
         float var36 = 4.5F + var35 * 3.0F;
         float var37 = var36 * 1.28F;
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.helper4());

         for (byte var38 = 0; var38 < 360; var38 += 20) {
            float var39 = (float)Math.toRadians(var38 + this.volume8);
            float var40 = (float)(Math.sin(var39) * var11 * var13);
            float var41 = (float)(Math.cos(var39) * var11 * var13);
            float var42 = 0.1F + var10 * Math.abs(MathHelper.sin(var38));
            double var43 = var12.x + var40;
            double var44 = var12.y + var42;
            double var47 = var12.z + var41;
            this.updateState3(ms, var4, var43, var44, var47, var37, this.resolveInt2(var7, 0.24F), this.volume8 + var38);
            this.updateState3(ms, var4, var43, var44, var47, var36, var7, -(this.volume8 + var38 * 0.5F));
         }

         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
      }
   }

   private void helper49(BufferBuilder buffer, Matrix4f matrix, float size, int color) {
      int var5 = color >> 16 & 0xFF;
      int var6 = color >> 8 & 0xFF;
      int var7 = color & 0xFF;
      int var8 = color >> 24 & 0xFF;
      float var9 = 0.34F * size / 0.1F;
      float var10 = 1.15F * size / 0.1F;
      var9 = 0.06F;
      var10 = 0.2F;
      this.helper52(buffer, matrix, 0.0F, var10, 0.0F, var9, 0.0F, 0.0F, 0.0F, 0.0F, var9, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, var10, 0.0F, 0.0F, 0.0F, var9, -var9, 0.0F, 0.0F, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, var10, 0.0F, -var9, 0.0F, 0.0F, 0.0F, 0.0F, -var9, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, var10, 0.0F, 0.0F, 0.0F, -var9, var9, 0.0F, 0.0F, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, -var10, 0.0F, var9, 0.0F, 0.0F, 0.0F, 0.0F, var9, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, -var10, 0.0F, 0.0F, 0.0F, var9, -var9, 0.0F, 0.0F, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, -var10, 0.0F, -var9, 0.0F, 0.0F, 0.0F, 0.0F, -var9, var5, var6, var7, var8);
      this.helper52(buffer, matrix, 0.0F, -var10, 0.0F, 0.0F, 0.0F, -var9, var9, 0.0F, 0.0F, var5, var6, var7, var8);
   }

   private float[] helper50(double worldX, double worldY, double worldZ) {
      return null;
   }

   private double resolveDouble(double worldX, double worldY, double worldZ) {
      Vec3d var7 = mc.gameRenderer.getCamera().getPos();
      double var8 = worldX - var7.x;
      double var10 = worldY - var7.y;
      double var12 = worldZ - var7.z;
      double var14 = Math.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
      return Math.max(0.5, 8.0 / Math.max(0.1, var14));
   }

   private void updateState8(MatrixStack matrix, float x, float y, float width, float height, int color) {
      int var7 = color >> 16 & 0xFF;
      int var8 = color >> 8 & 0xFF;
      int var9 = color & 0xFF;
      int var10 = color >> 24 & 0xFF;
      if (var10 > 0) {
         Matrix4f var11 = matrix.peek().getPositionMatrix();
         BufferBuilder var12 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var12.vertex(var11, x, y, 0.0F).texture(0.0F, 0.0F).color(var7, var8, var9, var10);
         var12.vertex(var11, x, y + height, 0.0F).texture(0.0F, 1.0F).color(var7, var8, var9, var10);
         var12.vertex(var11, x + width, y + height, 0.0F).texture(1.0F, 1.0F).color(var7, var8, var9, var10);
         var12.vertex(var11, x + width, y, 0.0F).texture(1.0F, 0.0F).color(var7, var8, var9, var10);
         BufferRenderer.drawWithGlobalProgram(var12.end());
      }
   }

   private void helper51(MatrixStack matrix, LivingEntity target) {
   }

   private void helper52(
      BufferBuilder buffer,
      Matrix4f matrix,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      int r,
      int g,
      int b,
      int a
   ) {
      buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
      buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
      buffer.vertex(matrix, x3, y3, z3).color(r, g, b, a);
   }

   static {
      if (index == 0) {
         index = 1;
      }

      for (int var0 = 0; var0 < 28; var0++) {
         VOLUME5[var0] = 0.6F + helper17() * 0.6F;
         VOLUME6[var0] = 0.3F + helper17() * 2.5F;
         VOLUME7[var0] = helper17() * 6.2832F;
         VOLUME8[var0] = (helper17() < 0.5F ? -1.0F : 1.0F) * (0.04F + helper17() * 0.13F);
         VOLUME9[var0] = (helper17() < 0.35F ? -1.0F : 1.0F) * (0.45F + helper17() * 0.9F);
         VOLUME10[var0] = 1.5F + helper17() * 2.3F;
         VOLUME11[var0] = helper17() * 6.2832F;
         VOLUME12[var0] = 0.65F + helper17() * 0.95F;
      }

      for (int var3 = 0; var3 < 90; var3++) {
         float var1 = 0.25F + helper17() * 2.6F;
         float var2 = helper17() * 6.2832F;
         updateState(var1, var2, VALUE[var3], VALUE2[var3]);
         VOLUME13[var3] = helper17() * 6.2832F;
         VOLUME14[var3] = 0.2F + helper17() * 1.1F;
         VOLUME15[var3] = 0.55F + helper17() * 0.9F;
      }

      VOLUME16 = new float[]{0.0F, 0.28F, 0.52F, 0.74F, 1.0F};
      VOLUME17 = new float[]{1.0F, 0.62F, 0.32F, 0.12F, 0.0F};
      VOLUME18 = new float[9];
      VOLUME19 = new float[9];

      for (int var4 = 0; var4 <= 8; var4++) {
         double var5 = (Math.PI * 2) * var4 / 8.0;
         VOLUME18[var4] = (float)Math.cos(var5);
         VOLUME19[var4] = (float)Math.sin(var5);
      }

      TEXTURE_ID = Identifier.of("lumen", "textures/targetesp/diamond.png");
   }

   private static class GlowPoint {
      final float x;
      final float y;
      final float z;
      final long startTime;
      final int maxLife;
      final int baseColor;

      GlowPoint(float x, float y, float z, int maxLife, int baseColor) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.startTime = System.currentTimeMillis();
         this.maxLife = maxLife;
         this.baseColor = baseColor;
      }

      boolean shouldRemove() {
         return System.currentTimeMillis() - this.startTime >= this.maxLife;
      }

      float getTimeProgress() {
         return MathHelper.clamp((float)(System.currentTimeMillis() - this.startTime) / this.maxLife, 0.0F, 1.0F);
      }

      int getColor(float timePC) {
         int var2 = (int)((this.baseColor >> 24 & 0xFF) * (1.0F - timePC));
         var2 = Math.max(0, Math.min(255, var2));
         return var2 << 24 | this.baseColor & 16777215;
      }
   }

   private static class LightningBolt {
      final float[] xs;
      final float[] ys;
      final float[] zs;
      final long spawnTime;
      final long lifeMs;
      final float flickerSeed;
      final int depth;
      boolean hasSplit;

      LightningBolt(float[] xs, float[] ys, float[] zs, long lifeMs, float flickerSeed, int depth) {
         this.xs = xs;
         this.ys = ys;
         this.zs = zs;
         this.spawnTime = System.currentTimeMillis();
         this.lifeMs = lifeMs;
         this.flickerSeed = flickerSeed;
         this.depth = depth;
      }
   }

   private static class TrailGhost {
      final double x;
      final double y;
      final double z;
      final long spawnTime;

      TrailGhost(double x, double y, double z, long spawnTime) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.spawnTime = spawnTime;
      }
   }
}
