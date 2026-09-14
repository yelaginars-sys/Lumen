package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.utils.animation.AnimationUtils;
import dlc.lumen.api.utils.animation.Easings;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.math.HoveringUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.api.utils.scissor.ScissorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.misc.AutoMine;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import dlc.lumen.client.modules.impl.render.base.implement.ArmorHUD;
import dlc.lumen.client.modules.impl.render.base.implement.AutoMineInfo;
import dlc.lumen.client.modules.impl.render.base.implement.BotMonitor;
import dlc.lumen.client.modules.impl.render.base.implement.CalculatorHUD;
import dlc.lumen.client.modules.impl.render.base.implement.Cooldown;
import dlc.lumen.client.modules.impl.render.base.implement.DynamicIslandHUD;
import dlc.lumen.client.modules.impl.render.base.implement.HelperBinds;
import dlc.lumen.client.modules.impl.render.base.implement.HotbarHUD;
import dlc.lumen.client.modules.impl.render.base.implement.InventoryHUD;
import dlc.lumen.client.modules.impl.render.base.implement.KeyBinds;
import dlc.lumen.client.modules.impl.render.base.implement.Potions;
import dlc.lumen.client.modules.impl.render.base.implement.ScoreBoardHUD;
import dlc.lumen.client.modules.impl.render.base.implement.TargetHud;
import dlc.lumen.client.modules.impl.render.base.implement.WaterMark;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class Interface extends Module {
   public static Interface INSTANCE = new Interface();
   private static final ConcurrentHashMap<String, Long> STRINGS = new ConcurrentHashMap<>();
   private static final boolean FLAG = Boolean.parseBoolean(System.getProperty("lumen.perf.debug", "false"));
   private static final long TIMESTAMP = Long.getLong("lumen.perf.hudMs", 5L) * 1000000L;
   private static final long TIMESTAMP2 = Long.getLong("lumen.perf.cooldownMs", 1000L) * 1000000L;
   private final WaterMark waterMarkSegmentDragging;
   private final KeyBinds keyBinds2;
   private final HelperBinds helperBinds2;
   private final Potions potions2;
   private final TargetHud targetHud2;
   private final AutoMineInfo autoMineInfo;
   private final ArmorHUD armorHUD2;
   private final Cooldown cooldown2;
   private final InventoryHUD inventoryHUD2;
   private final HotbarHUD hotbarHUD2;
   private final ScoreBoardHUD scoreBoardHUD2;
   private final DynamicIslandHUD dynamicIslandBottomY;
   private final CalculatorHUD calculatorHUD2;
   private final BotMonitor botMonitor2;
   private boolean flag;
   private float volume;
   private float volume2;
   private InterfaceProcessing interfaceProcessing;
   private InterfaceProcessing interfaceProcessing2;
   private float volume3;
   private float volume4;
   private final AnimationUtils animationUtils = new AnimationUtils(0.0F, 12.5F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils2 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils3 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils4 = new AnimationUtils(0.0F, 7.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils5 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils6 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils7 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils8 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils9 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils10 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils11 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils12 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils13 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils14 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils15 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils16 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils17 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils18 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils19 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils20 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils21 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
   private final AnimationUtils animationUtils22 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
   private final AnimationUtils animationUtils23 = new AnimationUtils(0.0F, 7.0F, Easings.CUBIC_OUT);
   private static final String TEXT = "ПКМ - по элементу для открытия настроек";
   public ModeSetting style = new ModeSetting("Фон", "LiquidGlass", "LiquidGlass", "Minimalism");
   public final ModeSetting appearEffect = new ModeSetting("Эффект", "Обычный", "Обычный", "Глитч", "Соединение", "Раскрытие");
    private final ListSetting waterMarkShown = new ListSetting(
       "Элементы",
       new BooleanSetting("Ватермарка", true),
       new BooleanSetting("Горячие клавиши", true),
       new BooleanSetting("Серверные бинды", true),
       new BooleanSetting("Зелья", true),
       new BooleanSetting("Таргет худ", true),
       new BooleanSetting("Watermark+", true),
       new BooleanSetting("Броня", true),
       new BooleanSetting("Кулдауны", true),
       new BooleanSetting("Inventory HUD", true),
       new BooleanSetting("Hotbar HUD", true),
       new BooleanSetting("ScoreBoard HUD", true),
       new BooleanSetting("DinamicIsland", true),
       new BooleanSetting("Калькулятор", false),
       new BooleanSetting("Боты", true)
    );
   private final BooleanSetting targetHudHoverEnabled = new BooleanSetting("Показывать при наведении", true);
   private final ListSetting listSetting = new ListSetting("Таргет худ", this.targetHudHoverEnabled);
   private final FloatSetting floatSetting = new FloatSetting("Размер таргет худа", 1.0F, 0.5F, 2.0F, 0.05F);
   private final FloatSetting scoreBoardScale = new FloatSetting("Размер скорборда", 1.0F, 0.5F, 2.5F, 0.05F);
   private int index = 0;
    private final AnimationUtils animationUtils24 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
    private final AnimationUtils animationUtils25 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);
    private final AnimationUtils animationUtils26 = new AnimationUtils(1.0F, 15.0F, Easings.CUBIC_OUT);
    private final AnimationUtils animationUtils27 = new AnimationUtils(1.0F, 8.2F, Easings.BACK_OUT);

   public Interface() {
      super("Interface", "Интерфейс клиента", Module.ModuleCategory.RENDER);
      this.addSettings(this.style, this.appearEffect, this.waterMarkShown, this.listSetting, this.floatSetting, this.scoreBoardScale);
      this.waterMarkSegmentDragging = new WaterMark(Lumen.draggable(this, "WaterMark", 10.0F, 10.0F));
      this.keyBinds2 = new KeyBinds(Lumen.draggable(this, "KeyBinds", 30.0F, 30.0F));
      this.helperBinds2 = new HelperBinds(Lumen.draggable(this, "HelperBinds", 90.0F, 30.0F));
      this.potions2 = new Potions(Lumen.draggable(this, "Potions", 30.0F, 60.0F));
      this.autoMineInfo = new AutoMineInfo(Lumen.draggable(this, "AutoMineInfo", 50.0F, 115.0F));
      this.targetHud2 = new TargetHud(Lumen.draggable(this, "TargetHud", 30.0F, 90.0F));
      this.armorHUD2 = new ArmorHUD(Lumen.draggable(this, "ArmorHUD", 10.0F, 120.0F));
      this.cooldown2 = new Cooldown(Lumen.draggable(this, "Cooldown", 10.0F, 180.0F));
      this.inventoryHUD2 = new InventoryHUD(Lumen.draggable(this, "InventoryHUD", 10.0F, 210.0F));
      this.hotbarHUD2 = new HotbarHUD(Lumen.draggable(this, "HotbarHUD", 10.0F, 275.0F));
      this.scoreBoardHUD2 = new ScoreBoardHUD(Lumen.draggable(this, "ScoreBoardHUD", 220.0F, 40.0F));
      this.dynamicIslandBottomY = new DynamicIslandHUD(Lumen.draggable(this, "DynamicIslandHUD", 10.0F, 300.0F));
      this.waterMarkSegmentDragging.setInterfaceModule(this);
       this.calculatorHUD2 = new CalculatorHUD(Lumen.draggable(this, "CalculatorHUD", 150.0F, 40.0F));
       this.botMonitor2 = new BotMonitor(Lumen.draggable(this, "BotMonitor", 220.0F, 120.0F));
   }

   private Font helper(int size) {
      return Fonts.getFont("inter_medium", size);
   }

   private int resolveInt(int color, float progress, int minAlpha) {
      int var4 = ColorUtils.applyAlpha(color, progress);
      int var5 = ColorUtils.getAlpha(var4);
      return var5 == 0 && progress > 0.001F ? ColorUtils.setAlphaColor(var4, minAlpha) : var4;
   }

   private int resolveInt2(float progress, int maxAlpha, int minAlpha) {
      int var4 = MathHelper.clamp((int)(maxAlpha * progress), 0, maxAlpha);
      return var4 == 0 && progress > 0.001F ? minAlpha : var4;
   }

   private int resolveInt3() {
      return !Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? Lumen.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtils.getThemeColor();
   }

   private boolean checkCondition(InterfaceProcessing element) {
      return element == null
         ? false
         : element == this.targetHud2 || element == this.waterMarkSegmentDragging || element == this.armorHUD2 || element == this.scoreBoardHUD2;
   }

   private boolean checkCondition2(InterfaceProcessing element, double mouseX, double mouseY) {
      float var6 = element.draggable.getWidth();
      float var7 = element.draggable.getHeight();
      return !(var6 <= 1.0F) && !(var7 <= 1.0F)
         ? HoveringUtils.isHovered(mouseX, mouseY, element.draggable.getX(), element.draggable.getY(), var6, var7)
         : false;
   }

   private boolean checkCondition3(InterfaceProcessing element) {
      return element != null && element.draggable.getWidth() > 1.0F && element.draggable.getHeight() > 1.0F;
   }

   private InterfaceProcessing helper2(double mouseX, double mouseY) {
      if (this.checkCondition3(this.targetHud2) && this.checkCondition2(this.targetHud2, mouseX, mouseY)) {
         return this.targetHud2;
      } else if (this.checkCondition3(this.waterMarkSegmentDragging) && this.checkCondition2(this.waterMarkSegmentDragging, mouseX, mouseY)) {
         return this.waterMarkSegmentDragging;
      } else if (this.checkCondition3(this.keyBinds2) && this.checkCondition2(this.keyBinds2, mouseX, mouseY)) {
         return this.keyBinds2;
      } else if (this.checkCondition3(this.helperBinds2) && this.checkCondition2(this.helperBinds2, mouseX, mouseY)) {
         return this.helperBinds2;
      } else if (this.checkCondition3(this.potions2) && this.checkCondition2(this.potions2, mouseX, mouseY)) {
         return this.potions2;
      } else if (this.checkCondition3(this.armorHUD2) && this.checkCondition2(this.armorHUD2, mouseX, mouseY)) {
         return this.armorHUD2;
      } else if (this.checkCondition3(this.cooldown2) && this.checkCondition2(this.cooldown2, mouseX, mouseY)) {
         return this.cooldown2;
      } else if (this.checkCondition3(this.inventoryHUD2) && this.checkCondition2(this.inventoryHUD2, mouseX, mouseY)) {
         return this.inventoryHUD2;
      } else if (this.checkCondition3(this.scoreBoardHUD2) && this.checkCondition2(this.scoreBoardHUD2, mouseX, mouseY)) {
         return this.scoreBoardHUD2;
      } else if (this.checkCondition3(this.dynamicIslandBottomY) && this.checkCondition2(this.dynamicIslandBottomY, mouseX, mouseY)) {
         return this.dynamicIslandBottomY;
      } else if (this.checkCondition3(this.calculatorHUD2) && this.checkCondition2(this.calculatorHUD2, mouseX, mouseY)) {
         return this.calculatorHUD2;
      } else {
         return this.checkCondition3(this.botMonitor2) && this.checkCondition2(this.botMonitor2, mouseX, mouseY) ? this.botMonitor2 : null;
      }
   }

    public boolean handleMusicClick(double mouseX, double mouseY, int button) {
       return this.waterMarkShown.is("Боты") && this.botMonitor2.handleClick(mouseX, mouseY, button)
          ? true
          : this.waterMarkShown.is("DinamicIsland") && this.dynamicIslandBottomY.handleClick(mouseX, mouseY, button);
    }

   public boolean handleWidgetClick(double mouseX, double mouseY, int button) {
      if (!this.waterMarkShown.is("Калькулятор")) {
         this.calculatorHUD2.blur();
         return false;
      } else {
         return this.calculatorHUD2.handleClick(mouseX, mouseY, button);
      }
   }

   public boolean handleWidgetKey(int keyCode, int scanCode, int modifiers) {
      return this.waterMarkShown.is("Калькулятор") && this.calculatorHUD2.handleKey(keyCode);
   }

   public boolean handleWidgetChar(char character) {
      return this.waterMarkShown.is("Калькулятор") && this.calculatorHUD2.handleChar(character);
   }

   public void releaseWidgetFocus() {
      this.calculatorHUD2.blur();
   }

   private float helper3() {
      return 100.0F;
   }

   private float helper4(InterfaceProcessing element) {
      if (element == this.targetHud2) {
         return 70.0F;
      } else if (element == this.waterMarkSegmentDragging) {
         return 45.0F;
      } else if (element == this.armorHUD2) {
         return 55.0F;
      } else {
         return element == this.scoreBoardHUD2 ? 28.0F : 19.0F;
      }
   }

   private float helper5() {
      return this.helper4(this.interfaceProcessing);
   }

   private void updateState(float menuWidth, float menuHeight) {
      if (mc != null && mc.getWindow() != null) {
         float var3 = Math.max(2.0F, mc.getWindow().getScaledWidth() - menuWidth - 2.0F);
         float var4 = Math.max(2.0F, mc.getWindow().getScaledHeight() - menuHeight - 2.0F);
         this.volume = MathHelper.clamp(this.volume, 2.0F, var3);
         this.volume2 = MathHelper.clamp(this.volume2, 2.0F, var4);
      }
   }

   public boolean handleHudContextClick(double mouseX, double mouseY, int button) {
      InterfaceProcessing var6 = this.helper2(mouseX, mouseY);
      if (button == 1 && var6 != null && !this.checkCondition(var6)) {
         if (this.flag) {
            this.flag = false;
            this.interfaceProcessing2 = null;
         }

         return false;
      } else if (button == 1 && var6 != null) {
         if (this.flag && this.interfaceProcessing == var6) {
            this.flag = false;
            this.interfaceProcessing2 = null;
         } else if (this.flag && this.interfaceProcessing != null && this.interfaceProcessing != var6) {
            this.interfaceProcessing2 = var6;
            float var25 = this.helper3();
            float var27 = this.helper4(var6);
            this.volume3 = var6.draggable.getX() + var6.draggable.getWidth() + 4.0F;
            this.volume4 = var6.draggable.getY() + 1.5F;
            float var28 = this.volume;
            float var29 = this.volume2;
            this.volume = this.volume3;
            this.volume2 = this.volume4;
            this.updateState(var25, var27);
            this.volume3 = this.volume;
            this.volume4 = this.volume2;
            this.volume = var28;
            this.volume2 = var29;
            this.flag = false;
         } else {
            this.interfaceProcessing = var6;
            this.interfaceProcessing2 = null;
            this.flag = true;
            float var24 = this.helper3();
            float var26 = this.helper5();
            this.volume = var6.draggable.getX() + var6.draggable.getWidth() + 4.0F;
            this.volume2 = var6.draggable.getY() + 1.5F;
            this.updateState(var24, var26);
         }

         return true;
      } else if (this.flag && this.interfaceProcessing != null) {
         float var7 = this.helper3();
         float var8 = this.helper5();
         this.updateState(var7, var8);
         float var9 = 3.0F;
         float var10 = this.volume + 5.0F;
         float var11 = (var7 - 10.0F - var9) / 2.0F;
         float var12 = 10.0F;
         float var13 = var10;
         float var14 = var10 + var11 + var9;
         boolean var15 = HoveringUtils.isHovered(mouseX, mouseY, this.volume, this.volume2, var7, var8);
         if (button == 0 && !var15 && var6 == this.interfaceProcessing) {
            this.flag = false;
            this.interfaceProcessing2 = null;
            return false;
         }

         if (this.interfaceProcessing == this.targetHud2) {
            float var16 = this.volume2 + 25.0F;
            float var17 = this.volume + var7 - 21.0F;
            float var18 = this.volume2 + 4.0F;
            boolean var19 = HoveringUtils.isHovered(mouseX, mouseY, var13, var16, var11, var12);
            boolean var20 = HoveringUtils.isHovered(mouseX, mouseY, var14, var16, var11, var12);
            boolean var21 = HoveringUtils.isHovered(mouseX, mouseY, var17, var18, 16.0, 9.0);
            if (button == 0 && var19) {
               this.targetHud2.setHealthBarStyleEnabled(false);
               return true;
            }

            if (button == 0 && var20) {
               this.targetHud2.setHealthBarStyleEnabled(true);
               return true;
            }

            if (button == 0 && var21) {
               this.targetHud2.setHeadParticlesEnabled(!this.targetHud2.isHeadParticlesEnabled());
               return true;
            }

            float[] var22 = this.helper17();
            if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var22[0], var22[1], var22[2], var22[3])) {
               this.targetHudHoverEnabled.setState(!this.targetHudHoverEnabled.isState());
               return true;
            }

            float[] var23 = this.helper16();
            if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var23[0] - 4.0F, var23[1] - 5.0F, var23[2] + 8.0F, var23[3] + 10.0F)) {
               this.index = 1;
               this.helper20(this.floatSetting, var23, mouseX);
               return true;
            }
         } else if (this.interfaceProcessing == this.scoreBoardHUD2) {
            float[] var30 = this.helper18();
            if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, var30[0] - 4.0F, var30[1] - 5.0F, var30[2] + 8.0F, var30[3] + 10.0F)) {
               this.index = 2;
               this.helper20(this.scoreBoardScale, var30, mouseX);
               return true;
            }
         } else if (this.interfaceProcessing == this.waterMarkSegmentDragging) {
            float var31 = this.volume2 + 3.5F;
            float var33 = this.volume + var7 - 21.0F;
            ArrayList var35 = new ArrayList<>(this.waterMarkSegmentDragging.getSegmentOrder());

            for (int var37 = 0; var37 < var35.size() && button == 0; var37++) {
               String var39 = (String)var35.get(var37);
               float var41 = var31 + var37 * 10.0F;
               if (HoveringUtils.isHovered(mouseX, mouseY, var33, var41, 16.0, 9.0)) {
                  if (!this.waterMarkSegmentDragging.isSegmentShown(var39)) {
                     this.waterMarkSegmentDragging.setSegmentShown(var39, true);
                  }

                  return true;
               }
            }
         } else if (this.interfaceProcessing == this.armorHUD2) {
            float var32 = this.volume + var7 - 21.0F;
            float var34 = this.volume2 + 4.5F;
            boolean var36 = HoveringUtils.isHovered(mouseX, mouseY, var32, var34, 16.0, 9.0);
            boolean var38 = HoveringUtils.isHovered(mouseX, mouseY, var32, var34 + 10.0F, 16.0, 9.0);
            boolean var40 = HoveringUtils.isHovered(mouseX, mouseY, var32, var34 + 20.0F, 16.0, 9.0);
            boolean var42 = HoveringUtils.isHovered(mouseX, mouseY, var32, var34 + 30.0F, 16.0, 9.0);
            boolean var43 = HoveringUtils.isHovered(mouseX, mouseY, var32, var34 + 40.0F, 16.0, 9.0);
            if (button == 0 && var36) {
               this.armorHUD2.setShowHelmet(!this.armorHUD2.isShowHelmet());
               return true;
            }

            if (button == 0 && var38) {
               this.armorHUD2.setShowChestplate(!this.armorHUD2.isShowChestplate());
               return true;
            }

            if (button == 0 && var40) {
               this.armorHUD2.setShowLeggings(!this.armorHUD2.isShowLeggings());
               return true;
            }

            if (button == 0 && var42) {
               this.armorHUD2.setShowBoots(!this.armorHUD2.isShowBoots());
               return true;
            }

            if (button == 0 && var43) {
               this.armorHUD2.setShowDurability(!this.armorHUD2.isShowDurability());
               return true;
            }
         }

         if (button == 0 || button == 1) {
            if (var15) {
               return true;
            }

            if (var6 != this.interfaceProcessing) {
               this.flag = false;
               this.interfaceProcessing2 = null;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void renderHudContextMenu(DrawContext context, int mouseX, int mouseY) {
      if (this.interfaceProcessing != null && !this.checkCondition3(this.interfaceProcessing)) {
         this.flag = false;
         this.interfaceProcessing = null;
         this.interfaceProcessing2 = null;
      }

      this.animationUtils.update(this.flag ? 1.0F : 0.0F);
      float var4 = MathHelper.clamp(this.animationUtils.getValue(), 0.0F, 1.0F);
      if (!this.flag && var4 <= 0.01F) {
         if (this.interfaceProcessing2 != null) {
            this.interfaceProcessing = this.interfaceProcessing2;
            this.interfaceProcessing2 = null;
            this.volume = this.volume3;
            this.volume2 = this.volume4;
            this.flag = true;
         } else {
            this.interfaceProcessing = null;
         }
      }

      if (!this.flag && var4 <= 0.01F && this.interfaceProcessing == null) {
         this.interfaceProcessing = null;
      } else if (this.interfaceProcessing != null) {
         boolean var5 = this.interfaceProcessing == this.targetHud2;
         boolean var6 = this.interfaceProcessing == this.waterMarkSegmentDragging;
         boolean var7 = this.interfaceProcessing == this.armorHUD2;
         boolean var8 = this.interfaceProcessing == this.scoreBoardHUD2;
         float var9 = this.helper3();
         float var10 = this.helper5();
         this.updateState(var9, var10);
         if (this.index == 1) {
            this.helper20(this.floatSetting, this.helper16(), mouseX);
         } else if (this.index == 2) {
            this.helper20(this.scoreBoardScale, this.helper18(), mouseX);
         }

         float var11 = this.volume;
         float var12 = this.volume2;
         int var13 = this.resolveInt3();
         float var14 = MathHelper.clamp((var4 - 0.06F) / 0.94F, 0.0F, 1.0F);
         int var15 = this.resolveInt2(var14, 255, 2);
         MatrixStack var16 = context.getMatrices();
         var16.push();
         if (!Lumen.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            int var17 = ColorUtils.rgba(40, 40, 40, 255);
         } else {
            int var45 = ColorUtils.rgba(40, 40, 40, 255);
         }

         InterfaceProcessing.drawHudBg(var16, var11 + 3.0F, var12 + 3.0F, var9 - 6.0F, var10 - 6.0F);
         if (var14 <= 0.02F) {
            var16.pop();
         } else {
            float var18 = 3.0F;
            float var19 = var11 + 5.0F;
            float var20 = (var9 - 10.0F - var18) / 2.0F;
            float var21 = 10.0F;
            float var22 = var19;
            float var23 = var19 + var20 + var18;
            int var24 = ColorUtils.applyAlpha(ColorUtils.rgba(70, 70, 70, 255), var14);
            int var25 = this.resolveInt(ColorUtils.darken(var13, 0.4F), var14, 2);
            int var26 = this.resolveInt(var13, var14, 2);
            if (var5) {
               this.helper(12)
                  .drawStringWithFade(var16, "Партиклы с головы", var11 + 4.7F, var12 + 7.5F, var9 - 28.0F, ColorUtils.rgba(255, 255, 255, var15));
               this.animationUtils2.update(this.targetHud2.isHeadParticlesEnabled() ? 1.0F : 0.0F);
               this.animationUtils3.update(this.targetHud2.isHeadParticlesEnabled() ? 1.0F : 0.0F);
               float var27 = this.animationUtils2.getValue();
               float var28 = this.animationUtils3.getValue();
               int var29 = ColorUtils.darken(var13, 0.05F);
               int var30 = ColorUtils.interpolateColor(var29, var13, var27);
               float var31 = var11 + var9 - 21.0F;
               float var32 = var12 + 4.5F;
               RenderUtils.drawGradientRect(
                  var16,
                  var31,
                  var32,
                  16.0F,
                  9.0F,
                  3.0F,
                  this.resolveInt(var30, var14, 2),
                  this.resolveInt(ColorUtils.darken(var30, 0.65F), var14, 2)
               );
               float var33 = var31 + 4.5F + var28 * 6.2F;
               RenderUtils.drawRoundCircle(var16, var33 + 0.5F, var32 + 4.5F, 6.85F, ColorUtils.rgba(255, 255, 255, var15));
               this.helper(12)
                  .draw(var16, "Вид полоски", var11 + 4.7F, var12 + 18.0F, ColorUtils.rgba(255, 255, 255, this.resolveInt2(var14, 225, 2)));
               float var34 = var12 + 25.0F;
               boolean var35 = this.targetHud2.isHealthBarStyleEnabled();
               this.animationUtils4.update(var35 ? 1.0F : 0.0F);
               float var36 = MathHelper.clamp(this.animationUtils4.getValue(), 0.0F, 1.0F);
               RenderUtils.drawRoundedRect(var16, var22, var34, var20, var21, 1.5F, var24);
               RenderUtils.drawRoundedRect(var16, var23, var34, var20, var21, 1.5F, var24);
               float var37 = MathHelper.lerp(var36, var22, var23);
               RenderUtils.drawGradientRect(var16, var37, var34, var20, var21, 1.5F, var25, var26, true);
               String var38 = "Клиентский";
               String var39 = "Здоровье";
               float var40 = var22 + (var20 - this.helper(12).getWidth(var38)) * 0.5F;
               float var41 = var23 + (var20 - this.helper(12).getWidth(var39)) * 0.55F;
               int var42 = MathHelper.clamp((int)(var15 * (0.65F + 0.35F * (1.0F - var36))), 0, 255);
               int var43 = MathHelper.clamp((int)(var15 * (0.65F + 0.35F * var36)), 0, 255);
               this.helper(12).draw(var16, var38, var40, var34 + 3.8F, ColorUtils.rgba(255, 255, 255, var42));
               this.helper(12).draw(var16, var39, var41, var34 + 3.8F, ColorUtils.rgba(255, 255, 255, var43));
               float[] var44 = this.helper17();
               this.helper15(
                  var16,
                  "При наводке",
                  var11 + 5.0F,
                  var44[0],
                  var44[1],
                  this.targetHudHoverEnabled.isState(),
                  this.animationUtils24,
                  this.animationUtils25,
                  var13,
                  var14,
                  var15
               );
               this.helper19(var16, "Размер", this.helper16(), this.floatSetting, var13, var14, var15);
            } else if (var8) {
               this.helper19(var16, "Размер", this.helper18(), this.scoreBoardScale, var13, var14, var15);
            } else if (var6) {
               float var46 = var11 + var9 - 21.0F;
               float var48 = var12 + 3.5F;
               float var50 = var11 + 5.0F;
               List var52 = this.waterMarkSegmentDragging.getSegmentOrder();

               for (int var53 = 0; var53 < var52.size(); var53++) {
                  String var54 = (String)var52.get(var53);
                  float var55 = var48 + var53 * 10.0F;
                  AnimationUtils[] var56 = this.helper14(var54);
                  this.helper15(
                     var16,
                     this.helper13(var54),
                     var50,
                     var46,
                     var55,
                     this.waterMarkSegmentDragging.isSegmentShown(var54),
                     var56[0],
                     var56[1],
                     var13,
                     var14,
                     var15
                  );
               }
            } else if (var7) {
               float var47 = var11 + var9 - 21.0F;
               float var49 = var12 + 3.5F;
               float var51 = var11 + 5.0F;
               this.helper15(
                  var16, "Шлем", var51, var47, var49, this.armorHUD2.isShowHelmet(), this.animationUtils13, this.animationUtils14, var13, var14, var15
               );
               this.helper15(
                  var16,
                  "Нагрудник",
                  var51,
                  var47,
                  var49 + 10.0F,
                  this.armorHUD2.isShowChestplate(),
                  this.animationUtils15,
                  this.animationUtils16,
                  var13,
                  var14,
                  var15
               );
               this.helper15(
                  var16,
                  "Поножи",
                  var51,
                  var47,
                  var49 + 20.0F,
                  this.armorHUD2.isShowLeggings(),
                  this.animationUtils17,
                  this.animationUtils18,
                  var13,
                  var14,
                  var15
               );
               this.helper15(
                  var16,
                  "Ботинки",
                  var51,
                  var47,
                  var49 + 30.0F,
                  this.armorHUD2.isShowBoots(),
                  this.animationUtils19,
                  this.animationUtils20,
                  var13,
                  var14,
                  var15
               );
               this.helper15(
                  var16,
                  "Прочность",
                  var51,
                  var47,
                  var49 + 40.0F,
                  this.armorHUD2.isShowDurability(),
                  this.animationUtils21,
                  this.animationUtils22,
                  var13,
                  var14,
                  var15
               );
            }

            var16.pop();
         }
      }
   }

   private void updateState2(InterfaceProcessing element, EventRender.Default event) {
      long var3 = FLAG ? System.nanoTime() : 0L;
      element.draggable.beginRenderTilt(event.getContext().getMatrices());

      try {
         this.helper6(element, event);
         element.updateAppearState();
      } finally {
         element.draggable.endRenderTilt(event.getContext().getMatrices());
         if (FLAG) {
            long var8 = System.nanoTime() - var3;
            if (var8 >= TIMESTAMP) {
               this.helper12(element, var8);
            }
         }
      }
   }

   private void helper6(InterfaceProcessing element, EventRender.Default event) {
      if (this.appearEffect.is("Обычный")) {
         element.onRender(event);
      } else {
         float var3 = element.appearProgress(0.5F);
         float var4 = element.draggable.getWidth();
         float var5 = element.draggable.getHeight();
         if (!(var3 >= 1.0F) && !(var4 <= 2.0F) && !(var5 <= 2.0F)) {
            this.renderBlockWithAppearEffect(
               event.getContext().getMatrices(),
               element.draggable.getX(),
               element.draggable.getY(),
               var4,
               var5,
               var3,
               element.hashCode(),
               () -> element.onRender(event)
            );
         } else {
            element.onRender(event);
         }
      }
   }

   public void renderBlockWithAppearEffect(MatrixStack matrices, float x, float y, float w, float h, float progress, long seed, Runnable draw) {
      if (!this.appearEffect.is("Обычный") && !(progress >= 1.0F) && !(w <= 4.0F) && !(h <= 2.0F)) {
         if (this.appearEffect.is("Глитч")) {
            float var29 = (float)Math.pow(1.0F - progress, 1.5);
            long var31 = System.nanoTime() / 60000000L;
            float var36 = y + h * (0.05F + 0.35F * this.helper8(var31 * 19L + seed));
            float var39 = Math.max(2.0F, h * (0.1F + 0.1F * this.helper8(var31 * 23L + seed)));
            float var40 = y + h * (0.55F + 0.3F * this.helper8(var31 * 29L + seed));
            float var41 = Math.max(2.0F, h * (0.1F + 0.1F * this.helper8(var31 * 31L + seed)));
            float var42 = (this.helper8(var31 * 37L + seed) > 0.5F ? 1.0F : -1.0F)
               * (0.2F + 0.4F * this.helper8(var31 * 41L + seed))
               * w
               * var29;
            float var43 = (this.helper8(var31 * 43L + seed) > 0.5F ? 1.0F : -1.0F)
               * (0.2F + 0.4F * this.helper8(var31 * 47L + seed))
               * w
               * var29;
            float var44 = (this.helper8(var31 * 3L + seed) - 0.5F) * 3.0F * var29;
            float var45 = 40.0F;
            float[][] var46 = new float[][]{{y - var45, var36}, {var36 + var39, var40}, {var40 + var41, y + h + var45}};

            for (float[] var25 : var46) {
               float var26 = var25[0];
               float var27 = var25[1];
               if (!(var27 - var26 < 0.5F)) {
                  ScissorUtils.push();
                  ScissorUtils.setFromComponentCoordinates(x - var45, var26, w + var45 * 2.0F, var27 - var26);
                  matrices.push();
                  matrices.translate(var44, 0.0F, 0.0F);
                  draw.run();
                  matrices.pop();
                  ScissorUtils.pop();
               }
            }

            this.helper7(draw, matrices, x, w, var36, var39, var42 + var44);
            this.helper7(draw, matrices, x, w, var40, var41, var43 + var44);
         } else if (this.appearEffect.is("Раскрытие")) {
            float var28 = helper11(helper9(progress, 0.0F, 0.68F));
            float var30 = helper11(helper9(progress, 0.08F, 0.8F));
            float var33 = helper10(helper9(progress, 0.18F, 1.0F));
            float var35 = Math.min(w, Math.max(18.0F, Math.min(48.0F, w * 0.34F)));
            float var38 = Math.min(h, Math.max(10.0F, Math.min(16.0F, h * 0.38F)));
            float var15 = MathHelper.lerp(var28, var35, w);
            float var16 = MathHelper.lerp(var30, var38, h);
            float var17 = x + (w - var15) * 0.5F;
            float var18 = y + (h - var16) * 0.5F;
            float var19 = x + w * 0.5F;
            float var20 = y + h * 0.5F;
            float var21 = MathHelper.lerp(var33, 0.84F, 1.0F);
            float var22 = MathHelper.lerp(var33, 0.74F, 1.0F);
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(var17 - 2.0F, var18 - 2.0F, var15 + 4.0F, var16 + 4.0F);
            matrices.push();
            matrices.translate(var19, var20, 0.0F);
            matrices.scale(var21, var22, 1.0F);
            matrices.translate(-var19, -var20, 0.0F);
            draw.run();
            matrices.pop();
            ScissorUtils.pop();
         } else {
            float var10 = x + w / 2.0F;
            float var11 = 0.55F;
            if (progress < var11) {
               float var32 = progress / var11;
               float var34 = var32 * var32 * var32;
               float var37 = (1.0F - var34) * (w * 1.1F + 60.0F);
               ScissorUtils.push();
               ScissorUtils.setFromComponentCoordinates(x - var37 - 2.0F, y - 30.0F, w / 2.0F + 2.0F, h + 60.0F);
               matrices.push();
               matrices.translate(-var37, 0.0F, 0.0F);
               draw.run();
               matrices.pop();
               ScissorUtils.pop();
               ScissorUtils.push();
               ScissorUtils.setFromComponentCoordinates(var10 + var37, y - 30.0F, w / 2.0F + 2.0F, h + 60.0F);
               matrices.push();
               matrices.translate(var37, 0.0F, 0.0F);
               draw.run();
               matrices.pop();
               ScissorUtils.pop();
            } else {
               float var12 = (progress - var11) / (1.0F - var11);
               float var13 = (float)(Math.sin(Math.PI * Math.min(var12 * 1.35F, 1.0F)) * (1.0F - var12 * 0.3F));
               float var14 = y + h / 2.0F;
               matrices.push();
               matrices.translate(var10, var14, 0.0F);
               matrices.scale(1.0F + 0.18F * var13, 1.0F - 0.14F * var13, 1.0F);
               matrices.translate(-var10, -var14, 0.0F);
               draw.run();
               matrices.pop();
            }
         }
      } else {
         draw.run();
      }
   }

   private void helper7(Runnable draw, MatrixStack matrices, float x, float w, float bandY, float bandH, float shift) {
      ScissorUtils.push();
      ScissorUtils.setFromComponentCoordinates(x - 60.0F + shift, bandY, w + 120.0F, bandH);
      matrices.push();
      matrices.translate(shift, 0.0F, 0.0F);
      draw.run();
      matrices.pop();
      ScissorUtils.pop();
   }

   private float helper8(long v) {
      v ^= v >>> 33;
      v *= -7046029254386353131L;
      v ^= v >>> 29;
      v *= -4658895280553007687L;
      v ^= v >>> 32;
      return (float)(v & 16777215L) / 1.6777215E7F;
   }

   private static float helper9(float value, float start, float end) {
      if (end <= start) {
         return value >= end ? 1.0F : 0.0F;
      } else {
         return MathHelper.clamp((value - start) / (end - start), 0.0F, 1.0F);
      }
   }

   private static float helper10(float value) {
      float var1 = MathHelper.clamp(value, 0.0F, 1.0F);
      return (float)Math.sin(var1 * Math.PI * 0.5);
   }

   private static float helper11(float value) {
      float var1 = MathHelper.clamp(value, 0.0F, 1.0F);
      float var2 = 1.70158F;
      float var3 = var1 - 1.0F;
      return 1.0F + (var2 + 1.0F) * var3 * var3 * var3 + var2 * var3 * var3;
   }

   private void helper12(InterfaceProcessing element, long elapsedNanos) {
      String var4 = element.getClass().getSimpleName();
      long var5 = System.nanoTime();
      Long var7 = STRINGS.get(var4);
      if (var7 == null || var5 - var7 >= TIMESTAMP2) {
         STRINGS.put(var4, var5);
         System.out.println(String.format(Locale.ROOT, "[PerfDebug] Slow HUD element: Interface -> %s took %.2f ms", var4, elapsedNanos / 1000000.0));
      }
   }

   private String helper13(String key) {
      return switch (key) {
         case "fps" -> "Фпс";
         case "ms" -> "Пинг";
         case "server" -> "Сервер";
         case "tps" -> "Тпс";
         case "pvp" -> "Пвп";
         default -> key;
      };
   }

   private AnimationUtils[] helper14(String key) {
      return switch (key) {
         case "fps" -> new AnimationUtils[]{this.animationUtils5, this.animationUtils6};
         case "ms" -> new AnimationUtils[]{this.animationUtils7, this.animationUtils8};
         case "server" -> new AnimationUtils[]{this.animationUtils9, this.animationUtils10};
         case "pvp" -> new AnimationUtils[]{this.animationUtils26, this.animationUtils27};
         default -> new AnimationUtils[]{this.animationUtils11, this.animationUtils12};
      };
   }

   public boolean handleWaterMarkSegmentGrab(double mouseX, double mouseY, int button) {
      return button == 0 && this.waterMarkShown.is("Ватермарка") ? this.waterMarkSegmentDragging.beginSegmentDrag(mouseX, mouseY) : false;
   }

   public void updateWaterMarkSegmentDrag(double mouseX) {
      this.waterMarkSegmentDragging.updateSegmentDrag(mouseX);
   }

   public void releaseWaterMarkSegmentDrag() {
      this.waterMarkSegmentDragging.endSegmentDrag();
   }

   public boolean isWaterMarkSegmentDragging() {
      return this.waterMarkSegmentDragging.isSegmentDragging();
   }

    public boolean isWaterMarkShown() {
      return this.waterMarkShown.is("Ватермарка");
    }

   public float getWaterMarkBottomY() {
      return this.waterMarkSegmentDragging.draggable.getY() + this.waterMarkSegmentDragging.draggable.getHeight();
   }

   public float getDynamicIslandBottomY() {
      return !this.waterMarkShown.is("DinamicIsland") ? 7.0F : this.dynamicIslandBottomY.draggable.getY() + this.dynamicIslandBottomY.draggable.getHeight();
   }

   private void helper15(
      MatrixStack matrices,
      String label,
      float labelX,
      float toggleX,
      float toggleY,
      boolean enabled,
      AnimationUtils bgAnimation,
      AnimationUtils circleAnimation,
      int themeColor,
      float contentProgress,
      int textAlpha
   ) {
      this.helper(12).draw(matrices, label, labelX, toggleY + 3.0F, ColorUtils.rgba(255, 255, 255, textAlpha));
      bgAnimation.update(enabled ? 1.0F : 0.0F);
      circleAnimation.update(enabled ? 1.0F : 0.0F);
      float var12 = bgAnimation.getValue();
      float var13 = circleAnimation.getValue();
      int var14 = ColorUtils.darken(themeColor, 0.05F);
      int var15 = ColorUtils.interpolateColor(var14, themeColor, var12);
      RenderUtils.drawGradientRect(
         matrices,
         toggleX,
         toggleY,
         16.0F,
         9.0F,
         3.0F,
         this.resolveInt(var15, contentProgress, 2),
         this.resolveInt(ColorUtils.darken(var15, 0.65F), contentProgress, 2)
      );
      float var16 = toggleX + 4.5F + var13 * 6.2F;
      RenderUtils.drawRoundCircle(matrices, var16 + 0.5F, toggleY + 4.5F, 6.85F, ColorUtils.rgba(255, 255, 255, textAlpha));
   }

   private float[] helper16() {
      return new float[]{this.volume + 6.0F, this.volume2 + 58.0F, this.helper3() - 12.0F, 4.0F};
   }

   private float[] helper17() {
      return new float[]{this.volume + this.helper3() - 21.0F, this.volume2 + 38.5F, 16.0F, 9.0F};
   }

   private float[] helper18() {
      return new float[]{this.volume + 6.0F, this.volume2 + 15.0F, this.helper3() - 12.0F, 4.0F};
   }

   private void helper19(MatrixStack matrices, String label, float[] rect, FloatSetting setting, int themeColor, float contentProgress, int textAlpha) {
      float var8 = rect[0];
      float var9 = rect[1];
      float var10 = rect[2];
      float var11 = rect[3];
      float var12 = var9 - 10.5F;
      this.helper(12).draw(matrices, label, var8 - 1.0F, var12 + 5.0F, ColorUtils.rgba(255, 255, 255, textAlpha));
      String var13 = String.format(Locale.ROOT, "%.2f", setting.get());
      float var14 = var8 + var10 - this.helper(12).getWidth(var13);
      this.helper(12).draw(matrices, var13, var14, var12 + 5.0F, ColorUtils.rgba(255, 255, 255, textAlpha));
      float var15 = MathHelper.clamp((setting.get() - setting.getMin()) / Math.max(1.0E-4F, setting.getMax() - setting.getMin()), 0.0F, 1.0F);
      int var16 = this.resolveInt(ColorUtils.rgba(70, 70, 70, 255), contentProgress, 2);
      int var17 = this.resolveInt(ColorUtils.darken(themeColor, 0.4F), contentProgress, 2);
      int var18 = this.resolveInt(themeColor, contentProgress, 2);
      RenderUtils.drawRoundedRect(matrices, var8, var9, var10, var11, var11 / 2.0F, var16);
      if (var15 > 0.001F) {
         RenderUtils.drawGradientRect(matrices, var8, var9, var10 * var15, var11, var11 / 2.0F, var17, var18, true);
      }

      float var19 = var8 + var10 * var15;
      RenderUtils.drawRoundCircle(matrices, var19, var9 + var11 / 2.0F, 4.5F, ColorUtils.rgba(255, 255, 255, textAlpha));
   }

   private void helper20(FloatSetting setting, float[] rect, double mouseX) {
      float var5 = MathHelper.clamp((float)((mouseX - rect[0]) / rect[2]), 0.0F, 1.0F);
      float var6 = setting.getMin() + var5 * (setting.getMax() - setting.getMin());
      float var7 = setting.getIncrement();
      if (var7 > 0.0F) {
         var6 = Math.round(var6 / var7) * var7;
      }

      setting.setValue(MathHelper.clamp(var6, setting.getMin(), setting.getMax()));
   }

   public void releaseHudContextSlider() {
      this.index = 0;
   }

   public Map<String, InterfaceProcessing> getConfigurableHudElements() {
      LinkedHashMap var1 = new LinkedHashMap();
      var1.put("waterMark", this.waterMarkSegmentDragging);
      var1.put("keyBinds", this.keyBinds2);
      var1.put("helperBinds", this.helperBinds2);
      var1.put("potions", this.potions2);
      var1.put("targetHud", this.targetHud2);
      var1.put("armorHUD", this.armorHUD2);
      var1.put("cooldown", this.cooldown2);
      var1.put("inventoryHUD", this.inventoryHUD2);
      var1.put("scoreBoardHUD", this.scoreBoardHUD2);
      var1.put("dynamicIslandHUD", this.dynamicIslandBottomY);
       var1.put("calculatorHUD", this.calculatorHUD2);
       var1.put("botMonitor", this.botMonitor2);
      return var1;
   }

   public boolean isTargetHudHoverEnabled() {
      return this.targetHudHoverEnabled.isState();
   }

   public float getScoreBoardScale() {
      return this.scoreBoardScale.get();
   }

   public boolean isHudElementVisible(String key) {
      int var2 = this.helper21(key);
      return var2 >= 0 && var2 < this.waterMarkShown.getSettings().size() ? this.waterMarkShown.getSettings().get(var2).isState() : false;
   }

   public void setHudElementVisible(String key, boolean visible) {
      int var3 = this.helper21(key);
      if (var3 >= 0 && var3 < this.waterMarkShown.getSettings().size()) {
         this.waterMarkShown.getSettings().get(var3).setState(visible);
      }
   }

   private int helper21(String key) {
      return switch (key) {
         case "waterMark" -> 0;
         case "keyBinds" -> 1;
         case "helperBinds" -> 2;
         case "potions" -> 3;
         case "targetHud" -> 4;
         case "armorHUD" -> 6;
         case "cooldown" -> 7;
         case "inventoryHUD" -> 8;
         case "hotbarHUD" -> 9;
         case "scoreBoardHUD" -> 10;
         case "dynamicIslandHUD" -> 11;
          case "calculatorHUD" -> 12;
          case "botMonitor" -> 13;
         default -> -1;
      };
   }

   @EventLink(priority = -200)
   public void onEvent(EventRender.Default event) {
      boolean var2 = this.waterMarkShown.is("Ватермарка");
      boolean var3 = this.waterMarkShown.is("Горячие клавиши");
      boolean var4 = this.waterMarkShown.is("Серверные бинды");
      boolean var5 = this.waterMarkShown.is("Зелья");
      boolean var6 = this.waterMarkShown.is("Таргет худ");
      boolean var7 = this.waterMarkShown.is("Броня");
      boolean var8 = this.waterMarkShown.is("Кулдауны");
      boolean var9 = this.waterMarkShown.is("Inventory HUD");
      boolean var10 = this.waterMarkShown.is("Hotbar HUD");
      boolean var11 = this.waterMarkShown.is("ScoreBoard HUD");
      boolean var12 = this.waterMarkShown.is("DinamicIsland");
      if (mc != null && mc.getWindow() != null && mc.currentScreen instanceof ChatScreen) {
         Font var13 = this.helper(18);
         float var14 = mc.getWindow().getScaledWidth() * 0.5F - var13.getWidth("ПКМ - по элементу для открытия настроек") * 0.5F;
         var13.draw(event.getContext().getMatrices(), "ПКМ - по элементу для открытия настроек", var14, 40.0F, -1);
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);

      try {
         if (var2) {
            this.updateState2(this.waterMarkSegmentDragging, event);
         }

         if (var3) {
            float var18 = 1.1F;
            this.keyBinds2.draggable.setScaleX(var18);
            this.keyBinds2.draggable.setScaleY(var18);
            this.updateState2(this.keyBinds2, event);
         }

         if (var4) {
            this.updateState2(this.helperBinds2, event);
         }

         if (var5) {
            float var19 = 1.1F;
            this.potions2.draggable.setScaleX(var19);
            this.potions2.draggable.setScaleY(var19);
            this.updateState2(this.potions2, event);
         }

         if (AutoMine.INSTANCE.isEnable()) {
            this.updateState2(this.autoMineInfo, event);
         }

         if (var6) {
            float var20 = this.floatSetting.get();
            this.targetHud2.draggable.setScaleX(var20);
            this.targetHud2.draggable.setScaleY(var20);
            this.updateState2(this.targetHud2, event);
         }

         if (var7) {
            this.updateState2(this.armorHUD2, event);
         }

         if (var8) {
            float var21 = 1.1F;
            this.cooldown2.draggable.setScaleX(var21);
            this.cooldown2.draggable.setScaleY(var21);
            this.updateState2(this.cooldown2, event);
         }

         if (var9) {
            this.updateState2(this.inventoryHUD2, event);
         }

         if (var10) {
            this.updateState2(this.hotbarHUD2, event);
         }

         if (var11) {
            this.updateState2(this.scoreBoardHUD2, event);
         }

         if (var12) {
            this.updateState2(this.dynamicIslandBottomY, event);
         }

          if (this.waterMarkShown.is("Боты")) {
             this.updateState2(this.botMonitor2, event);
          }

         if (this.waterMarkShown.is("Калькулятор")) {
            this.updateState2(this.calculatorHUD2, event);
         } else {
            this.calculatorHUD2.blur();
         }
      } finally {
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
      }

      if (!(mc.currentScreen instanceof ChatScreen)) {
         this.flag = false;
         this.interfaceProcessing2 = null;
      }
   }
}
