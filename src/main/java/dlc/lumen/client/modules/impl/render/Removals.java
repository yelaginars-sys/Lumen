package dlc.lumen.client.modules.impl.render;

import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;

public class Removals extends Module {
   public static final Removals INSTANCE = new Removals();
   public static final String FIRE = "Огонь";
   public static final String NEGATIVE_EFFECTS = "Плохие эффекты";
   public static final String BLOCK_OVERLAY = "Оверлей в блоке";
   public static final String PARTICLES = "Частицы";
   public static final String WEATHER = "Погода";
   public static final String CLOUDS = "Облака";
   public static final String BLOCK_ENTITIES = "Блок-сущности";
   public static final String SHADOWS = "Тени";
   public static final String TOTEM_ANIMATION = "Анимацию тотема";
   public static final String HURT_CAM = "Тряску при уроне";
   public static final String VIGNETTE = "Виньетка";
   private final BooleanSetting fireDisabled = new BooleanSetting("Огонь", false);
   private final BooleanSetting negativeEffectsDisabled = new BooleanSetting("Плохие эффекты", false);
   private final BooleanSetting blockOverlayDisabled = new BooleanSetting("Оверлей в блоке", false);
   private final BooleanSetting particlesDisabled = new BooleanSetting("Частицы", false);
   private final BooleanSetting weatherDisabled = new BooleanSetting("Погода", false);
   private final BooleanSetting cloudsDisabled = new BooleanSetting("Облака", false);
   private final BooleanSetting blockEntitiesDisabled = new BooleanSetting("Блок-сущности", false);
   private final BooleanSetting shadowsDisabled = new BooleanSetting("Тени", false);
   private final BooleanSetting totemAnimationDisabled = new BooleanSetting("Анимацию тотема", false);
   private final BooleanSetting hurtCamDisabled = new BooleanSetting("Тряску при уроне", false);
   private final BooleanSetting vignetteDisabled = new BooleanSetting("Виньетка", false);
   private final ListSetting listSetting = new ListSetting(
      "Элементы",
      this.fireDisabled,
      this.negativeEffectsDisabled,
      this.blockOverlayDisabled,
      this.particlesDisabled,
      this.weatherDisabled,
      this.cloudsDisabled,
      this.blockEntitiesDisabled,
      this.shadowsDisabled,
      this.totemAnimationDisabled,
      this.hurtCamDisabled,
      this.vignetteDisabled
   );

   public Removals() {
      super("Removals", "Убирает выбранные элементы рендера", Module.ModuleCategory.RENDER);
      this.addSettings(this.listSetting);
   }

   public boolean isEnabled(String element) {
      return this.isEnable() && this.listSetting.is(element);
   }

   private boolean checkCondition(BooleanSetting setting) {
      return this.isEnable() && setting != null && setting.isState();
   }

   public boolean isFireDisabled() {
      return this.checkCondition(this.fireDisabled);
   }

   public boolean isNegativeEffectsDisabled() {
      return this.checkCondition(this.negativeEffectsDisabled);
   }

   public boolean isBlockOverlayDisabled() {
      return this.checkCondition(this.blockOverlayDisabled);
   }

   public boolean isParticlesDisabled() {
      return this.checkCondition(this.particlesDisabled);
   }

   public boolean isWeatherDisabled() {
      return this.checkCondition(this.weatherDisabled);
   }

   public boolean isCloudsDisabled() {
      return this.checkCondition(this.cloudsDisabled);
   }

   public boolean isBlockEntitiesDisabled() {
      return this.checkCondition(this.blockEntitiesDisabled);
   }

   public boolean isShadowsDisabled() {
      return this.checkCondition(this.shadowsDisabled);
   }

   public boolean isTotemAnimationDisabled() {
      return this.checkCondition(this.totemAnimationDisabled);
   }

   public boolean isHurtCamDisabled() {
      return this.checkCondition(this.hurtCamDisabled);
   }

   public boolean isVignetteDisabled() {
      return this.checkCondition(this.vignetteDisabled);
   }
}
