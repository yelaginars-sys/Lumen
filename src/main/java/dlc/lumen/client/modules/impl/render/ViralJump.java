package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction.Axis;
import org.joml.Matrix4f;

public class ViralJump extends Module {
   public static ViralJump INSTANCE = new ViralJump();
   private static final int INDEX = 4;
   private static final int INDEX2 = 3;
   public final ModeSetting mode = new ModeSetting("Режим", "Вектор", "Космос", "Слайм", "Вектор");
   private final FloatSetting floatSetting = new FloatSetting("Радиус", 6.0F, 2.0F, 16.0F, 0.5F);
   private final FloatSetting floatSetting2 = new FloatSetting("Время жизни", 1.6F, 0.4F, 5.0F, 0.1F);
   private final FloatSetting floatSetting3 = new FloatSetting("Скорость волны", 1.4F, 0.2F, 4.0F, 0.1F);
   private final FloatSetting floatSetting4 = new FloatSetting("Мягкость края", 0.6F, 0.05F, 2.0F, 0.05F);
   private final FloatSetting floatSetting5 = new FloatSetting("Ширина кольца", 1.2F, 0.3F, 4.0F, 0.1F);
   private final FloatSetting floatSetting6 = new FloatSetting("Плотность", 1.0F, 0.3F, 2.0F, 0.05F);
   private final FloatSetting floatSetting7 = new FloatSetting("Яркость", 1.0F, 0.2F, 2.5F, 0.05F);
   private final List<ViralJump.Wave> viralJumps = new ArrayList<>();
   private boolean flag = true;

   public ViralJump() {
      super("ViralJump", "Подсветка верхов блоков при прыжке", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.mode,
         this.floatSetting,
         this.floatSetting2,
         this.floatSetting3,
         this.floatSetting4,
         this.floatSetting6,
         this.floatSetting7,
         this.floatSetting5
      );
   }

   @Override
   public void onEnable() {
      if (mc.player != null) {
         this.flag = mc.player.isOnGround();
      }

      this.viralJumps.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.viralJumps.clear();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         boolean var2 = mc.player.isOnGround();
         if (this.flag && !var2 && mc.player.getVelocity().y > 0.0) {
            this.viralJumps.add(this.helper4(mc.player.getPos()));

            while (this.viralJumps.size() > 4) {
               this.viralJumps.remove(0);
            }
         }

         this.flag = var2;
         long var3 = System.currentTimeMillis();
         long var5 = (long)(this.floatSetting2.get() * 1000.0F);
         Iterator var7 = this.viralJumps.iterator();

         while (var7.hasNext()) {
            if (var3 - ((ViralJump.Wave)var7.next()).floatSetting > var5) {
               var7.remove();
            }
         }
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (!this.viralJumps.isEmpty() && mc.world != null) {
         ShaderProgramKey var2 = this.helper3();
         ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(var2);
         if (var3 != null) {
            long var4 = System.currentTimeMillis();
            float var6 = Math.max(this.floatSetting2.get() * 1000.0F, 1.0F);
            Vec3d var7 = event.getCamera().getPos();
            MatrixStack var8 = event.getMatrices();
            int var9 = ColorUtils.getThemeColor();
            float var10 = ColorUtils.redf(var9);
            float var11 = ColorUtils.greenf(var9);
            float var12 = ColorUtils.bluef(var9);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShader(var2);
            this.helper5(var3, "uTime", (float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
            this.helper5(var3, "uPulse", this.floatSetting7.get());
            this.helper5(var3, "uDensity", this.floatSetting6.get());
            Matrix4f var13 = var8.peek().getPositionMatrix();
            BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            boolean var15 = false;

            for (ViralJump.Wave var17 : this.viralJumps) {
               float var18 = (float)(var4 - var17.floatSetting) / var6;
               if (!(var18 >= 1.0F)) {
                  float var19 = this.floatSetting.get() * helper(MathHelper.clamp(var18 * this.floatSetting3.get(), 0.0F, 1.0F));
                  float var20 = this.floatSetting5.get() * 0.5F;
                  float var21 = this.floatSetting4.get();
                  float var22 = 1.0F - var18 * var18;

                  for (ViralJump.Tile var24 : var17.floatSetting2) {
                     float var25 = MathHelper.sqrt(
                        (float)(
                           (var24.INDEX + 0.5 - var17.INDEX) * (var24.INDEX + 0.5 - var17.INDEX)
                              + (var24.floatSetting + 0.5 - var17.INDEX2) * (var24.floatSetting + 0.5 - var17.INDEX2)
                        )
                     );
                     float var26 = Math.abs(var25 - var19);
                     if (!(var26 > var20 + var21)) {
                        float var27 = 1.0F - helper2(var20 - var21, var20 + var21, var26);
                        float var28 = var22 * MathHelper.clamp(var27 * 1.25F, 0.0F, 1.0F);
                        if (!(var28 <= 0.01F)) {
                           float var29 = (float)(var24.INDEX - var7.x);
                           float var30 = (float)(var24.INDEX + 1 - var7.x);
                           float var31 = (float)(var24.INDEX2 - var7.y) + 0.012F;
                           float var32 = (float)(var24.floatSetting - var7.z);
                           float var33 = (float)(var24.floatSetting + 1 - var7.z);
                           var14.vertex(var13, var29, var31, var32).texture(var24.INDEX, var24.floatSetting).color(var10, var11, var12, var28);
                           var14.vertex(var13, var29, var31, var33)
                              .texture(var24.INDEX, var24.floatSetting + 1.0F)
                              .color(var10, var11, var12, var28);
                           var14.vertex(var13, var30, var31, var33)
                              .texture(var24.INDEX + 1.0F, var24.floatSetting + 1.0F)
                              .color(var10, var11, var12, var28);
                           var14.vertex(var13, var30, var31, var32)
                              .texture(var24.INDEX + 1.0F, var24.floatSetting)
                              .color(var10, var11, var12, var28);
                           var15 = true;
                        }
                     }
                  }
               }
            }

            if (var15) {
               BuiltBuffer var34 = var14.endNullable();
               if (var34 != null) {
                  BufferRenderer.drawWithGlobalProgram(var34);
               }
            } else {
               var14.endNullable();
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
         }
      }
   }

   private static float helper(float t) {
      float var1 = 1.0F - MathHelper.clamp(t, 0.0F, 1.0F);
      return 1.0F - var1 * var1 * var1;
   }

   private static float helper2(float edge0, float edge1, float x) {
      if (edge1 - edge0 < 1.0E-5F) {
         return x >= edge1 ? 1.0F : 0.0F;
      }

      float var3 = MathHelper.clamp((x - edge0) / (edge1 - edge0), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private ShaderProgramKey helper3() {
      if (this.mode.is("Космос")) {
         return ShaderUtils.viralJumpSpace;
      } else {
         return this.mode.is("Слайм") ? ShaderUtils.viralJumpSlime : ShaderUtils.viralJumpVector;
      }
   }

   private ViralJump.Wave helper4(Vec3d origin) {
      ViralJump.Wave var2 = new ViralJump.Wave(origin.x, origin.z, System.currentTimeMillis());
      int var3 = (int)Math.ceil(this.floatSetting.get());
      int var4 = MathHelper.floor(origin.x);
      int var5 = MathHelper.floor(origin.y);
      int var6 = MathHelper.floor(origin.z);
      float var7 = this.floatSetting.get() * this.floatSetting.get();
      Mutable var8 = new Mutable();

      for (int var9 = -var3; var9 <= var3; var9++) {
         for (int var10 = -var3; var10 <= var3; var10++) {
            double var11 = var9 + 0.5 + var4 - origin.x;
            double var13 = var10 + 0.5 + var6 - origin.z;
            if (!(var11 * var11 + var13 * var13 > var7)) {
               for (int var15 = 0; var15 >= -3; var15--) {
                  var8.set(var4 + var9, var5 + var15, var6 + var10);
                  if (!mc.world.getBlockState(var8).isAir()) {
                     if (mc.world.getBlockState(var8.up()).isAir()) {
                        double var16 = var8.getY() + mc.world.getBlockState(var8).getOutlineShape(mc.world, var8).getMax(Axis.Y);
                        var2.floatSetting2.add(new ViralJump.Tile(var8.getX(), var16, var8.getZ()));
                     }
                     break;
                  }
               }
            }
         }
      }

      return var2;
   }

   private void helper5(ShaderProgram shader, String name, float value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   private static final class Tile {
      private final int INDEX;
      private final double INDEX2;
      private final int floatSetting;

      private Tile(int x, double y, int z) {
         this.INDEX = x;
         this.INDEX2 = y;
         this.floatSetting = z;
      }
   }

   private static final class Wave {
      private final double INDEX;
      private final double INDEX2;
      private final long floatSetting;
      private final List<ViralJump.Tile> floatSetting2 = new ArrayList<>();

      private Wave(double originX, double originZ, long born) {
         this.INDEX = originX;
         this.INDEX2 = originZ;
         this.floatSetting = born;
      }
   }
}
