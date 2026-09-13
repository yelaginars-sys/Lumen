package dlc.lumen.client.modules.impl.render;

import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.chams.ChamsRenderer;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.misc.ServerHelper;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ShaderEsp extends Module {
   public static final ShaderEsp INSTANCE = new ShaderEsp();
   private static final int INDEX = -11141291;
   private static final int INDEX2 = -12934913;
   private static final int INDEX3 = -1;
   private final BooleanSetting booleanSetting = new BooleanSetting("Локальный игрок", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Игроки", true);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Игнорировать голых", false).visible(() -> this.booleanSetting2.getValue());
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Мобы", true);
   private final BooleanSetting booleanSetting5 = new BooleanSetting("Предметы", false);
   private final ModeSetting modeSetting = new ModeSetting("Режим цвета", "Тема", "Тема", "Свой");
   private final FloatSetting floatSetting = new FloatSetting("Красный", 255.0F, 0.0F, 255.0F, 1.0F).visible(() -> this.modeSetting.is("Свой"));
   private final FloatSetting floatSetting2 = new FloatSetting("Зелёный", 51.0F, 0.0F, 255.0F, 1.0F).visible(() -> this.modeSetting.is("Свой"));
   private final FloatSetting floatSetting3 = new FloatSetting("Синий", 85.0F, 0.0F, 255.0F, 1.0F).visible(() -> this.modeSetting.is("Свой"));
   private final FloatSetting floatSetting4 = new FloatSetting("Насыщенность", 1.15F, 0.0F, 2.0F, 0.05F);
   private final FloatSetting floatSetting5 = new FloatSetting("Прозрачность", 1.0F, 0.05F, 1.0F, 0.01F);
   private final BooleanSetting booleanSetting6 = new BooleanSetting("Цвет друзей", true);
   private final BooleanSetting booleanSetting7 = new BooleanSetting("Свечение", true);
   private final FloatSetting floatSetting6 = new FloatSetting("Радиус свечения", 0.55F, 0.0F, 1.0F, 0.01F).visible(() -> this.booleanSetting7.getValue());
   private final FloatSetting floatSetting7 = new FloatSetting("Сила свечения", 1.6F, 0.0F, 4.0F, 0.05F).visible(() -> this.booleanSetting7.getValue());
   private final FloatSetting floatSetting8 = new FloatSetting("Спад свечения", 1.1F, 0.2F, 4.0F, 0.05F).visible(() -> this.booleanSetting7.getValue());
   private final BooleanSetting booleanSetting8 = new BooleanSetting("Заливка", true);
   private final FloatSetting floatSetting9 = new FloatSetting("Плотность заливки", 0.45F, 0.0F, 1.5F, 0.01F)
      .visible(() -> this.booleanSetting8.getValue());
   private final FloatSetting floatSetting10 = new FloatSetting("Внутреннее свечение", 0.6F, 0.0F, 2.0F, 0.05F)
      .visible(() -> this.booleanSetting8.getValue());
   private final BooleanSetting booleanSetting9 = new BooleanSetting("Обводка", true);
   private final ModeSetting modeSetting2 = new ModeSetting("Тип обводки", "Снаружи", "Снаружи", "Внутри", "Обе")
      .visible(() -> this.booleanSetting9.getValue());
   private final FloatSetting floatSetting11 = new FloatSetting("Ширина обводки", 2.0F, 1.0F, 5.0F, 1.0F).visible(() -> this.booleanSetting9.getValue());
   private final FloatSetting floatSetting12 = new FloatSetting("Яркость обводки", 1.8F, 0.0F, 3.0F, 0.05F).visible(() -> this.booleanSetting9.getValue());
   private final FloatSetting floatSetting13 = new FloatSetting("Белизна обводки", 0.45F, 0.0F, 1.0F, 0.01F).visible(() -> this.booleanSetting9.getValue());
   private final BooleanSetting booleanSetting10 = new BooleanSetting("Пульсация", false);
   private final FloatSetting floatSetting14 = new FloatSetting("Скорость пульсации", 1.0F, 0.1F, 5.0F, 0.1F).visible(() -> this.booleanSetting10.getValue());
   private final FloatSetting floatSetting15 = new FloatSetting("Сила пульсации", 0.3F, 0.0F, 1.0F, 0.05F).visible(() -> this.booleanSetting10.getValue());
   private final BooleanSetting booleanSetting11 = new BooleanSetting("Шиммер", false);
   private final FloatSetting floatSetting16 = new FloatSetting("Ширина шиммера", 0.04F, 0.01F, 0.15F, 0.01F).visible(() -> this.booleanSetting11.getValue());
   private final FloatSetting floatSetting17 = new FloatSetting("Период шиммера", 5.0F, 1.0F, 15.0F, 0.5F).visible(() -> this.booleanSetting11.getValue());
   private final FloatSetting floatSetting18 = new FloatSetting("Яркость шиммера", 0.8F, 0.0F, 2.0F, 0.05F).visible(() -> this.booleanSetting11.getValue());
   private final BooleanSetting chamsEnabled = new BooleanSetting("Chams", false);
   private final BooleanSetting booleanSetting12 = new BooleanSetting("Диагн. сырая маска", false);
   private final Set<Entity> targets = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
   private final ShaderEspPipeline shaderEspPipeline = new ShaderEspPipeline();
   private final ShaderEspPipeline.Params shaderEspPipeline2 = new ShaderEspPipeline.Params();
   private Framebuffer framebuffer2;
   private int index;

   private ShaderEsp() {
      super("Shader ESP", "Шейдерный ESP с ореолом свечения для сущностей", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.booleanSetting,
         this.booleanSetting2,
         this.booleanSetting3,
         this.booleanSetting4,
         this.booleanSetting5,
         this.modeSetting,
         this.floatSetting,
         this.floatSetting2,
         this.floatSetting3,
         this.floatSetting4,
         this.floatSetting5,
         this.booleanSetting6,
         this.booleanSetting7,
         this.floatSetting6,
         this.floatSetting7,
         this.floatSetting8,
         this.booleanSetting8,
         this.floatSetting9,
         this.floatSetting10,
         this.booleanSetting9,
         this.modeSetting2,
         this.floatSetting11,
         this.floatSetting12,
         this.floatSetting13,
         this.booleanSetting10,
         this.floatSetting14,
         this.floatSetting15,
         this.booleanSetting11,
         this.floatSetting16,
         this.floatSetting17,
         this.floatSetting18,
         this.chamsEnabled,
         this.booleanSetting12
      );
   }

   @Override
   public void onDisable() {
      this.targets.clear();
      this.framebuffer2 = null;
      this.shaderEspPipeline.close();
      ChamsRenderer.getInstance().invalidate();
      super.onDisable();
   }

   public boolean isChamsEnabled() {
      return this.chamsEnabled.getValue();
   }

   public static boolean isEspTarget(Entity entity) {
      return INSTANCE.targets.contains(entity);
   }

   public boolean hasTargets() {
      return !this.targets.isEmpty();
   }

   public void setOutlineFramebuffer(Framebuffer framebuffer) {
      this.framebuffer2 = framebuffer;
   }

   public int resolveMaskColor(Entity entity) {
      if (entity instanceof PlayerEntity var2 && this.helper5(var2)) {
         return -11141291;
      } else {
         return entity instanceof PlayerEntity && ServerHelper.INSTANCE != null && ServerHelper.INSTANCE.isSnowGlowing(entity) ? -12934913 : -1;
      }
   }

   @EventLink
   public void onClientTick(EventTickPre event) {
      if (this.isEnable()) {
         if (++this.index % 3 == 0) {
            this.helper();
         }
      }
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      if (this.isEnable() && mc.world != null && mc.player != null) {
         ChamsRenderer.getInstance().render(event);
         if (!this.targets.isEmpty()) {
            Framebuffer var2 = this.framebuffer2;
            if (var2 != null && var2.getColorAttachment() != 0) {
               this.helper2(this.shaderEspPipeline2);
               this.shaderEspPipeline.render(var2, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), this.shaderEspPipeline2);
            }
         }
      }
   }

   private void helper() {
      this.targets.clear();
      if (mc.world != null && mc.player != null) {
         for (Entity var2 : mc.world.getEntities()) {
            if (this.helper3(var2)) {
               this.targets.add(var2);
            }
         }
      }
   }

   private void helper2(ShaderEspPipeline.Params p) {
      p.color = this.modeSetting.is("Свой")
         ? 0xFF000000 | (int)this.floatSetting.get() << 16 | (int)this.floatSetting2.get() << 8 | (int)this.floatSetting3.get()
         : ColorUtils.getThemeColor();
      p.friendEnabled = this.booleanSetting6.getValue();
      p.friendColor = -11141291;
      p.opacity = this.floatSetting5.get();
      p.saturation = this.floatSetting4.get();
      p.glowEnabled = this.booleanSetting7.getValue();
      p.glowRadius = this.floatSetting6.get();
      p.glowStrength = this.floatSetting7.get();
      p.glowFalloff = this.floatSetting8.get();
      p.fillEnabled = this.booleanSetting8.getValue();
      p.fillOpacity = this.floatSetting9.get();
      p.innerGlow = this.floatSetting10.get();
      p.outlineEnabled = this.booleanSetting9.getValue();
      p.outlineMode = this.modeSetting2.getIndex();
      p.outlineWidth = (int)this.floatSetting11.get();
      p.outlineStrength = this.floatSetting12.get();
      p.outlineWhite = this.floatSetting13.get();
      p.pulseEnabled = this.booleanSetting10.getValue();
      p.pulseSpeed = this.floatSetting14.get();
      p.pulseAmount = this.floatSetting15.get();
      p.shimmerEnabled = this.booleanSetting11.getValue();
      p.shimmerWidth = this.floatSetting16.get();
      p.shimmerPeriodSec = this.floatSetting17.get();
      p.shimmerBrightness = this.floatSetting18.get();
      p.debugRawMask = this.booleanSetting12.getValue();
   }

   private boolean helper3(Entity entity) {
      if (entity instanceof PlayerEntity var2) {
         if (var2.getCustomName() != null && var2.getCustomName().getString().startsWith("Ghost_")) {
            return false;
         } else {
            return !this.booleanSetting2.getValue() ? false : !this.booleanSetting3.getValue() || helper4(var2);
         }
      } else if (entity instanceof ClientPlayerEntity) {
         return this.booleanSetting.getValue();
      } else if (entity instanceof LivingEntity) {
         return this.booleanSetting4.getValue();
      } else {
         return entity instanceof ItemEntity ? this.booleanSetting5.getValue() : false;
      }
   }

   private static boolean helper4(PlayerEntity p) {
      return !p.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
         || !p.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
         || !p.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
         || !p.getEquippedStack(EquipmentSlot.FEET).isEmpty();
   }

   private boolean helper5(PlayerEntity player) {
      return Lumen.INSTANCE != null && Lumen.INSTANCE.friendStorage != null && Lumen.INSTANCE.friendStorage.isFriend(player.getName().getString());
   }
}
