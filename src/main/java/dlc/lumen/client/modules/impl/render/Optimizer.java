package dlc.lumen.client.modules.impl.render;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.client.option.GraphicsMode;

public class Optimizer extends Module {
   public static final Optimizer INSTANCE = new Optimizer();
   private final BooleanSetting booleanSetting = new BooleanSetting("Быстрая графика", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Плавное освещение", true);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Тени сущностей", true);
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Эффекты движения", true);
   private final BooleanSetting booleanSetting5 = new BooleanSetting("Тряска при уроне", true);
   private final BooleanSetting booleanSetting6 = new BooleanSetting("Частицы", true);
   private final BooleanSetting booleanSetting7 = new BooleanSetting("Погода", true);
   private final BooleanSetting booleanSetting8 = new BooleanSetting("Облака", true);
   private final BooleanSetting booleanSetting9 = new BooleanSetting("Качество листвы", true);
   private final BooleanSetting booleanSetting10 = new BooleanSetting("Снизить дальность", true);
   private final FloatSetting floatSetting = new FloatSetting("Дальность мира", 8.0F, 2.0F, 16.0F, 1.0F).visible(() -> this.booleanSetting10.isState());
   private final BooleanSetting booleanSetting11 = new BooleanSetting("Снизить дальность сущностей", true);
   private final FloatSetting floatSetting2 = new FloatSetting("Дальность сущностей", 0.5F, 0.5F, 1.0F, 0.25F)
      .visible(() -> this.booleanSetting11.isState());
   private GraphicsMode graphicsMode;
   private boolean flag;
   private boolean flag2;
   private double level;
   private double level2;
   private double level3;
   private double level4;
   private int index;
   private double level5;
   private int index2;
   private int index3;

   private Optimizer() {
      super("Optimizer", "Сильно оптимизирует игру для повышения FPS", Module.ModuleCategory.RENDER);
      this.addSettings(
         this.booleanSetting,
         this.booleanSetting2,
         this.booleanSetting3,
         this.booleanSetting4,
         this.booleanSetting5,
         this.booleanSetting6,
         this.booleanSetting7,
         this.booleanSetting8,
         this.booleanSetting9,
         this.booleanSetting10,
         this.floatSetting,
         this.booleanSetting11,
         this.floatSetting2
      );
   }

   public boolean removesParticles() {
      return this.isEnable() && this.booleanSetting6.isState();
   }

   public boolean removesWeather() {
      return this.isEnable() && this.booleanSetting7.isState();
   }

   public boolean removesClouds() {
      return this.isEnable() && this.booleanSetting8.isState();
   }

   @Override
   public void onEnable() {
      this.helper();
      this.helper2();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.helper3();
      super.onDisable();
   }

   @EventLink
   public void onTick(EventTickPre event) {
      if (++this.index3 >= 40) {
         this.index3 = 0;
         this.helper2();
      }
   }

   private void helper() {
      if (mc.options != null) {
         this.graphicsMode = mc.options.getGraphicsMode().getValue();
         this.flag = mc.options.getAo().getValue();
         this.flag2 = mc.options.getEntityShadows().getValue();
         this.level = mc.options.getFovEffectScale().getValue();
         this.level2 = mc.options.getDistortionEffectScale().getValue();
         this.level3 = mc.options.getDarknessEffectScale().getValue();
         this.level4 = mc.options.getDamageTiltStrength().getValue();
         this.index = mc.options.getViewDistance().getValue();
         this.level5 = mc.options.getEntityDistanceScaling().getValue();
         this.index2 = mc.options.getBiomeBlendRadius().getValue();
      }
   }

   private void helper2() {
      if (mc.options != null) {
         if (this.booleanSetting.isState()) {
            mc.options.getGraphicsMode().setValue(GraphicsMode.FAST);
         }

         if (this.booleanSetting2.isState()) {
            mc.options.getAo().setValue(false);
         }

         if (this.booleanSetting3.isState()) {
            mc.options.getEntityShadows().setValue(false);
         }

         if (this.booleanSetting4.isState()) {
            mc.options.getFovEffectScale().setValue(0.0);
            mc.options.getDistortionEffectScale().setValue(0.0);
            mc.options.getDarknessEffectScale().setValue(0.0);
         }

         if (this.booleanSetting5.isState()) {
            mc.options.getDamageTiltStrength().setValue(0.0);
         }

         if (this.booleanSetting10.isState()) {
            mc.options.getViewDistance().setValue((int)this.floatSetting.get());
         }

         if (this.booleanSetting11.isState()) {
            mc.options.getEntityDistanceScaling().setValue(Math.round(this.floatSetting2.get() * 4.0) / 4.0);
         }

         if (this.booleanSetting9.isState()) {
            mc.options.getBiomeBlendRadius().setValue(0);
         }
      }
   }

   private void helper3() {
      if (mc.options != null) {
         if (this.graphicsMode != null) {
            mc.options.getGraphicsMode().setValue(this.graphicsMode);
         }

         mc.options.getAo().setValue(this.flag);
         mc.options.getEntityShadows().setValue(this.flag2);
         mc.options.getFovEffectScale().setValue(this.level);
         mc.options.getDistortionEffectScale().setValue(this.level2);
         mc.options.getDarknessEffectScale().setValue(this.level3);
         mc.options.getDamageTiltStrength().setValue(this.level4);
         mc.options.getViewDistance().setValue(this.index);
         mc.options.getEntityDistanceScaling().setValue(this.level5);
         mc.options.getBiomeBlendRadius().setValue(this.index2);
      }
   }
}
