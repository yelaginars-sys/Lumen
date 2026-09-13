package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.render.hands.ShaderHandsRenderer;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;

public class ShaderHands extends Module {
   public static ShaderHands INSTANCE = new ShaderHands();
   private static final ShaderHandsRenderer SHADER_HANDS_RENDERER = ShaderHandsRenderer.getInstance();
   public final ModeSetting mode = new ModeSetting("Режим", "Свечение", "Свечение", "Отсталый", "Космос", "Slime");
   public final FloatSetting waveSpeed = new FloatSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F).visible(() -> this.mode.is("Красивый"));
   public final FloatSetting waveScale = new FloatSetting("Частота волн", 1.0F, 1.0F, 3.0F, 0.1F).visible(() -> this.mode.is("Красивый"));
   public final FloatSetting trailLife = new FloatSetting("Время следа", 0.5F, 0.1F, 2.0F, 0.05F).visible(() -> this.mode.is("Отсталый"));
   public final FloatSetting trailRise = new FloatSetting("Подъём следа", 0.35F, 0.0F, 1.0F, 0.05F).visible(() -> this.mode.is("Отсталый"));
   public final FloatSetting trailDissolve = new FloatSetting("Распад следа", 1.0F, 0.2F, 3.0F, 0.05F).visible(() -> this.mode.is("Отсталый"));
   public final FloatSetting trailGlow = new FloatSetting("Глоу следа", 1.0F, 0.0F, 2.0F, 0.05F).visible(() -> this.mode.is("Отсталый"));
   public final FloatSetting nebulaSpeed = new FloatSetting("Скорость космоса", 1.0F, 0.1F, 3.0F, 0.05F).visible(() -> this.mode.is("Космос"));
   public final FloatSetting nebulaDensity = new FloatSetting("Плотность туманности", 1.0F, 0.3F, 2.5F, 0.05F).visible(() -> this.mode.is("Космос"));
   public final FloatSetting nebulaStars = new FloatSetting("Яркость звёзд", 1.0F, 0.0F, 2.0F, 0.05F).visible(() -> this.mode.is("Космос"));
   public final FloatSetting nebulaGlow = new FloatSetting("Свечение космоса", 1.0F, 0.0F, 2.0F, 0.05F).visible(() -> this.mode.is("Космос"));
   public final FloatSetting nebulaBlend = new FloatSetting("Перекрытие руки", 0.85F, 0.1F, 1.0F, 0.05F).visible(() -> this.mode.is("Космос"));
   public final FloatSetting slimeSpeed = new FloatSetting("Скорость течения", 1.0F, 0.1F, 3.0F, 0.05F).visible(() -> this.mode.is("Slime"));
   public final FloatSetting slimeGoo = new FloatSetting("Густота", 1.0F, 0.2F, 2.0F, 0.05F).visible(() -> this.mode.is("Slime"));

   public ShaderHands() {
      super("ShaderHands", "Красивый Шейдер на руки и предметы", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.mode,
         this.waveSpeed,
         this.waveScale,
         this.trailLife,
         this.trailRise,
         this.trailDissolve,
         this.trailGlow,
         this.nebulaSpeed,
         this.nebulaDensity,
         this.nebulaStars,
         this.nebulaGlow,
         this.nebulaBlend,
         this.slimeSpeed,
         this.slimeGoo
      );
   }

   @Override
   public void onDisable() {
      SHADER_HANDS_RENDERER.invalidateState();
      super.onDisable();
   }

   @EventLink(priority = 0)
   public void onRender2D(EventRender.Default event) {
      if (this.isEnable()) {
         SHADER_HANDS_RENDERER.renderOverlayIfPending();
      }
   }
}
