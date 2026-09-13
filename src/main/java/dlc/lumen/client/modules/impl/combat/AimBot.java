package dlc.lumen.client.modules.impl.combat;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventGameUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.combat.components.gcd.GCDUtil;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class AimBot extends Module {
   public static AimBot INSTANCE = new AimBot();
   private final ListSetting value = new ListSetting(
      "Типы целей",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("В броне", true),
      new BooleanSetting("Без брони", false),
      new BooleanSetting("Мобы", false),
      new BooleanSetting("Зомби", false)
   );
   private final FloatSetting floatSetting = new FloatSetting("Дистанция", 40.0F, 10.0F, 100.0F, 1.0F);
   private final FloatSetting floatSetting2 = new FloatSetting("Время наводки (тики)", 10.0F, 0.0F, 40.0F, 1.0F);
   private final BooleanSetting booleanSetting = new BooleanSetting("Тихие повороты", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Показать прицел", true);
   private final FloatSetting floatSetting3 = new FloatSetting("Размер прицела", 1.0F, 0.3F, 3.0F, 0.1F);
   private LivingEntity target2 = null;
   private boolean flag = false;
   private float volume = 0.0F;
   private Rotation rotation = null;

   public AimBot() {
      super("AimBot", "Авто-наведение для лука и арбалета", Module.ModuleCategory.COMBAT);
      this.addSettings(this.value, this.floatSetting, this.floatSetting2, this.booleanSetting, this.booleanSetting2, this.floatSetting3);
   }

   private Identifier computeIdentifier() {
      return Identifier.of("lumen", "textures/cross/hit.png");
   }

   private boolean checkState() {
      ItemStack var1 = mc.player.getMainHandStack();
      ItemStack var2 = mc.player.getOffHandStack();
      return var1.getItem() instanceof BowItem
         || var1.getItem() instanceof CrossbowItem
         || var2.getItem() instanceof BowItem
         || var2.getItem() instanceof CrossbowItem;
   }

   private boolean checkState2() {
      return mc.player.isUsingItem() && this.checkState();
   }

   private boolean checkCondition(LivingEntity entity) {
      if (entity == mc.player) {
         return false;
      }

      if (!entity.isAlive() || entity.getHealth() <= 0.0F) {
         return false;
      }

      if (!(entity instanceof PlayerEntity)) {
         if (entity instanceof ZombieEntity) {
            return this.value.is("Зомби");
         } else {
            return entity instanceof HostileEntity ? this.value.is("Мобы") : false;
         }
      } else {
         if (!this.value.is("Игроки")) {
            return false;
         }

         if (Lumen.INSTANCE.friendStorage.isFriend(entity.getName().getString())) {
            return false;
         }

         boolean var2 = false;
         PlayerEntity var3 = (PlayerEntity)entity;

         for (ItemStack var5 : var3.getArmorItems()) {
            if (!var5.isEmpty()) {
               var2 = true;
               break;
            }
         }

         if (this.value.is("В броне") && var2) {
            return true;
         } else {
            return this.value.is("Без брони") && !var2 ? true : !this.value.is("В броне") && !this.value.is("Без брони");
         }
      }
   }

   private LivingEntity computeLivingEntity() {
      ArrayList<LivingEntity> var1 = new ArrayList<>();
      Box var2 = mc.player.getBoundingBox().expand(this.floatSetting.getValue().floatValue());

      for (LivingEntity var4 : mc.world.getEntitiesByClass(LivingEntity.class, var2, e -> true)) {
         if (this.checkCondition(var4)) {
            double var5 = mc.player.distanceTo(var4);
            if (!(var5 > this.floatSetting.getValue().floatValue())) {
               var1.add(var4);
            }
         }
      }

      if (var1.isEmpty()) {
         return null;
      }

      var1.sort(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity)));
      return (LivingEntity)var1.get(0);
   }

   private Rotation computeRotation(LivingEntity target) {
      Vec3d var2 = mc.player.getEyePos();
      Vec3d var3 = target.getBoundingBox().getCenter();
      double var4 = var3.x - var2.x;
      double var6 = var3.y - var2.y;
      double var8 = var3.z - var2.z;
      double var10 = Math.sqrt(var4 * var4 + var8 * var8);
      float var12 = (float)Math.toDegrees(Math.atan2(var8, var4)) - 90.0F;
      float var13 = (float)(-Math.toDegrees(Math.atan2(var6, var10)));
      return new Rotation(var12, var13);
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (this.booleanSetting2.isState() && this.target2 != null && this.flag) {
         float var2 = event.getTickDelta();
         Vec3d var3 = new Vec3d(
            MathHelper.lerp(var2, this.target2.lastRenderX, this.target2.getX()),
            MathHelper.lerp(var2, this.target2.lastRenderY, this.target2.getY()) + this.target2.getHeight() / 2.0,
            MathHelper.lerp(var2, this.target2.lastRenderZ, this.target2.getZ())
         );
         Vec3d var4 = mc.gameRenderer.getCamera().getPos();
         MatrixStack var5 = event.getMatrices();
         double var6 = var3.x - var4.x;
         double var8 = var3.y - var4.y;
         double var10 = var3.z - var4.z;
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShaderTexture(0, this.computeIdentifier());
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         var5.push();
         var5.translate(var6, var8, var10);
         var5.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
         var5.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
         float var12 = this.floatSetting3.get() * 0.5F;
         int var13 = (int)(255.0F * this.volume);
         int var14 = ColorUtils.getThemeColor();
         int var15 = var14 >> 16 & 0xFF;
         int var16 = var14 >> 8 & 0xFF;
         int var17 = var14 & 0xFF;
         Matrix4f var18 = var5.peek().getPositionMatrix();
         BufferBuilder var19 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var19.vertex(var18, -var12, -var12, 0.0F).texture(0.0F, 1.0F).color(var15, var16, var17, var13);
         var19.vertex(var18, -var12, var12, 0.0F).texture(0.0F, 0.0F).color(var15, var16, var17, var13);
         var19.vertex(var18, var12, var12, 0.0F).texture(1.0F, 0.0F).color(var15, var16, var17, var13);
         var19.vertex(var18, var12, -var12, 0.0F).texture(1.0F, 1.0F).color(var15, var16, var17, var13);
         BufferRenderer.drawWithGlobalProgram(var19.end());
         var5.pop();
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   @EventLink
   public void onGameUpdate(EventGameUpdate e) {
      if (mc.player != null && mc.world != null) {
         this.flag = this.checkState2();
         if (this.flag) {
            LivingEntity var2 = this.computeLivingEntity();
            if (var2 != null) {
               if (this.target2 != var2) {
                  this.target2 = var2;
                  this.volume = 0.0F;
               }

               Rotation var3 = this.computeRotation(this.target2);
               float var4 = 1.0F / Math.max(1.0F, this.floatSetting2.getValue().floatValue());
               this.volume = Math.min(this.volume + var4, 1.0F);
               float var5 = mc.player.getYaw();
               float var6 = mc.player.getPitch();
               float var7 = var3.getYaw();
               float var8 = var3.getPitch();
               float var9 = MathHelper.wrapDegrees(var7 - var5);
               float var10 = var8 - var6;
               float var11 = var9 * this.volume;
               float var12 = var10 * this.volume;
               this.rotation = new Rotation(var5 + var11, var6 + var12);
            }
         } else {
            this.target2 = null;
            this.rotation = null;
            this.volume = 0.0F;
         }
      }
   }

   @EventLink
   public void onUpdate(EventGameUpdate ignoredghj) {
      if (this.target2 != null && this.flag && this.rotation != null) {
         if (this.booleanSetting.isState()) {
            float var2 = GCDUtil.getGCD();
            float var3 = this.rotation.getYaw();
            float var4 = this.rotation.getPitch();
            var3 -= (var3 - mc.player.getYaw()) % var2;
            var4 -= (var4 - mc.player.getPitch()) % var2;
            RotationStorage.update(new Rotation(var3, var4), 180.0F, 180.0F, 45.0F, 45.0F, 0, 2, false);
         } else {
            mc.player.setYaw(this.rotation.getYaw());
            mc.player.setPitch(this.rotation.getPitch());
         }
      }
   }

   public LivingEntity getTarget() {
      return this.target2;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.target2 = null;
      this.flag = false;
      this.volume = 0.0F;
      this.rotation = null;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.target2 = null;
      this.flag = false;
      this.volume = 0.0F;
      this.rotation = null;
   }
}
