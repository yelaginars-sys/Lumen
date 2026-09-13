package dlc.lumen.client.modules.impl.misc;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dlc.lumen.Lumen;
import dlc.lumen.api.commands.impl.RCTCommand;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventTickPre;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.bots.BotTasks;
import dlc.lumen.client.bots.core.BotContext;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.Setting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import dlc.lumen.client.ui.modern.ModernGui;
import dlc.lumen.mixin.IClientInteractionManagerAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AutoMine extends Module {
   private static final AutoMine.Shaft AUTO_MINE = new AutoMine.Shaft("Адская", "warp mine2", 43, 70, 74, 61, 82, 92);
   private static final AutoMine.Shaft AUTO_MINE2 = new AutoMine.Shaft("Обычная", "warp mine1", 43, 72, 38, 61, 82, 56);
   private static final AutoMine.Shaft[] AUTO_MINE3 = new AutoMine.Shaft[]{AUTO_MINE, AUTO_MINE2};
   private static final String TEXT = "Авто";
   public static final AutoMine INSTANCE = new AutoMine();
   private static final String[] value = new String[]{
      "крюк", "крючок", "hook", "кошк", "грэпл", "граппл", "grapp", "гарпун", "harpoon", "ключ", "универсальн", "key"
   };
   private static final String[] STRING = new String[]{"tripwire_hook", "lead", "fishing_rod", "carrot_on_a_stick", "warped_fungus_on_a_stick"};
   private final ModeSetting modeSetting = new ModeSetting("Шахта", "Авто", resolveString());
   private AutoMine.Shaft shaft = AUTO_MINE;
   private boolean flag = false;
   private static final double LEVEL = 0.6;
   private static final double LEVEL2 = 0.7;
   private static final double LEVEL3 = 0.7;
   private static final double LEVEL4 = 4.5;
   private static final double LEVEL5 = 2.0;
   private static final String TEXT2 = "1-74";
   private static final String TEXT3 = "2,14,28,41";
   private static final String TEXT4 = "крюк,опыт";
   private static final boolean FLAG = false;
   private static final int INDEX = 53;
   private static final long TIMESTAMP = 350L;
   private static final long TIMESTAMP2 = 40L;
   private static final long TIMESTAMP3 = 3500L;
   private static final long TIMESTAMP4 = 5000L;
   private static final int INDEX2 = 3;
   private static final double LEVEL6 = 60.0;
   private static final long TIMESTAMP5 = 120000L;
   private static final float VOLUME = 900.0F;
   private static final float VOLUME2 = 600.0F;
   private static final float VOLUME3 = 2600.0F;
   private static final float VOLUME4 = 2000.0F;
   private static final float VOLUME5 = 900.0F;
   private static final float VOLUME6 = 700.0F;
   private static final double LEVEL7 = 0.8;
   private static final double LEVEL8 = 0.5;
   private static final long TIMESTAMP6 = 60L;
   private long timestamp = 0L;
   private long timestamp2 = 0L;
   private long timestamp3 = 0L;
   private Vec3d vec3d = null;
   private BlockPos blockPos = null;
   private int index = 0;
   private int index2 = 0;
   private int index3 = -1;
   private long timestamp4 = 0L;
   private long timestamp5 = 0L;
   private final Set<Long> hashSet = new HashSet<>();
   private static final long TIMESTAMP7 = 180000L;
   private static final long TIMESTAMP8 = 300000L;
   private final Set<String> hashSet2 = new HashSet<>();
   private boolean flag2 = false;
   private long timestamp6 = 0L;
   private String text2 = null;
   private String text3 = null;
   private long timestamp7 = 0L;
   private static final Pattern PATTERN = Pattern.compile("осталось\\s*:?\\s*(?:(\\d+)\\s*мин)?[^0-9]*(?:(\\d+)\\s*сек)?");
   private static final Pattern PATTERN2 = Pattern.compile("Текущая\\s+шахта\\s*:?\\s*(.+)");
   private static final Pattern PATTERN3 = Pattern.compile("Следующая\\s+шахта\\s*:?\\s*(.+)");
   private AutoMine.State state2 = AutoMine.State.WARP;
   private long timestamp8 = 0L;
   private long timestamp9 = 0L;
   private long timestamp10 = 0L;
   private long timestamp11 = 0L;
   private int index4 = 0;
   private AutoMine.State state3 = AutoMine.State.MINE;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private long timestamp12 = 0L;
   private long timestamp13 = 0L;
   private int index5 = 0;
   private BlockPos blockPos2 = null;
   private long timestamp14 = 0L;
   private final Map<Long, Long> hashMap = new HashMap<>();
   private static final long TIMESTAMP9 = 1700L;
   private static final long TIMESTAMP10 = 30000L;
   private static final long TIMESTAMP11 = 120000L;
   private static final long TIMESTAMP12 = 45000L;
   private static final int INDEX3 = 3;
   private static final int INDEX4 = 5;
   private static final long TIMESTAMP13 = 5000L;
   private static final long TIMESTAMP14 = 20000L;
   private static final long TIMESTAMP15 = 8000L;
   private ServerInfo serverInfo = null;
   private long timestamp15 = 0L;
   private long timestamp16 = 0L;
   private long timestamp17 = 0L;
   private long timestamp18 = 0L;
   private int index6 = 0;
   private long timestamp19 = 0L;
   private volatile boolean volatileboolean = false;
   private volatile boolean volatileboolean2 = false;
   private volatile boolean volatileboolean3 = false;
   private volatile boolean volatileboolean4 = false;
   private long timestamp20 = 0L;
   private BlockPos blockPos3 = null;
   private BlockPos blockPos4 = null;
   private final MinePathNavigator minePathNavigator = new MinePathNavigator();
   private float volume;
   private float volume2;
   private int index7;
   private int index8 = Integer.MIN_VALUE;
   private float volume3 = 16.0F;
   private float volume4 = 16.0F;
   private long timestamp21 = 0L;
   private int index9 = 0;
   private long timestamp22 = 0L;
   private long timestamp23 = 0L;
   private long timestamp24 = 0L;
   private double level;
   private double level2;
   private float volume5;
   private float volume6;
   private long timestamp25;
   private long timestamp26 = 0L;
   private float volume7 = 0.0F;
   private long timestamp27 = 0L;
   private long timestamp28 = 0L;
   private int index10 = 0;
   private long timestamp29 = 0L;
   private long timestamp30 = 0L;
   private long timestamp31 = 320L;
   private final Random random = new Random();
   private long timestamp32 = 0L;
   private static final long TIMESTAMP16 = 5000L;
   private boolean flag5 = false;
   private long timestamp33 = 0L;
   private Vec3d vec3d2 = null;
   private final List<Integer> integers = new ArrayList<>();
   private int index11 = 0;
   private boolean flag6 = false;
   private long timestamp34 = 0L;
   private long timestamp35 = 0L;
   private long timestamp36 = 0L;
   private long timestamp37 = 0L;
   private long timestamp38 = 0L;
   private static final int INDEX5 = 3;
   private static final long TIMESTAMP17 = 50L;
   private static final long TIMESTAMP18 = 500L;
   private static final long TIMESTAMP19 = 12000L;
   private static final long TIMESTAMP20 = 5000L;
   private long timestamp39 = 0L;
   private long timestamp40 = 0L;
   private long timestamp41 = 0L;
   private long timestamp42 = 0L;
   private boolean flag7 = false;
   private boolean flag8 = false;
   private boolean flag9 = false;
   private boolean flag10 = false;
   private long timestamp43 = 0L;
   private long timestamp44 = 0L;
   private long timestamp45 = 0L;
   private volatile boolean volatileboolean5 = false;
   private long timestamp46 = 0L;
   private static final long TIMESTAMP21 = 60000L;
   private long timestamp47 = 0L;
   private int hooksObtained = 0;
   private int index12 = -1;
   private int index13 = -1;
   private boolean flag11 = false;
   private long timestamp48 = 0L;
   private World world2 = null;
   private static final long TIMESTAMP22 = 6000L;
   private static final String[] value2 = new String[]{
      "крюк", "крючок", "hook", "кошк", "грэпл", "граппл", "grapp", "гарпун", "harpoon", "универсальныи ключ", "универсальный ключ", "unique key"
   };
   private static final Pattern PATTERN4 = Pattern.compile("[.,](\\d{1,2})$");
   private volatile long hooksObtained2 = 0L;
   private static final Pattern PATTERN5 = Pattern.compile("продано за\\s+([0-9][0-9.,\\s\\u00A0\\u2007\\u202F]*[0-9]|[0-9])\\s*монет");
   private static final Pattern PATTERN6 = Pattern.compile("списано\\s+([0-9][0-9.,\\s\\u00A0\\u2007\\u202F]*[0-9]|[0-9])");
   private final BooleanSetting booleanSetting = new BooleanSetting("Быстрая долбёжка", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Складывать уники", false);
   private final TextSetting textSetting = new TextSetting("Дом для уников", "home", 32);
   private final TextSetting textSetting2 = new TextSetting("Анархия для уников", "", 3);
   private final FloatSetting floatSetting = new FloatSetting("Уников для сдачи", 3.0F, 1.0F, 64.0F, 1.0F);
   private final FloatSetting floatSetting2 = new FloatSetting("Радиус поиска сундука", 6.0F, 2.0F, 16.0F, 1.0F);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Беречь стартовые вещи", true);
   private static final float[][] value3 = new float[][]{
      {1.0F, 0.0F}, {1.0F, 1.0F}, {0.0F, 1.0F}, {-1.0F, 1.0F}, {-1.0F, 0.0F}, {-1.0F, -1.0F}, {0.0F, -1.0F}, {1.0F, -1.0F}
   };
   private static final Item[] value4 = new Item[]{
      Items.COAL,
      Items.RAW_IRON,
      Items.RAW_COPPER,
      Items.RAW_GOLD,
      Items.IRON_INGOT,
      Items.COPPER_INGOT,
      Items.GOLD_INGOT,
      Items.IRON_NUGGET,
      Items.GOLD_NUGGET,
      Items.DIAMOND,
      Items.EMERALD,
      Items.LAPIS_LAZULI,
      Items.REDSTONE,
      Items.QUARTZ,
      Items.AMETHYST_SHARD,
      Items.ANCIENT_DEBRIS,
      Items.NETHERITE_SCRAP
   };

   public AutoMine() {
      super("AutoMine", "Автофарм на автошахте (обычная / адская)", Module.ModuleCategory.PLAYER);
      this.addSettings(
         this.modeSetting,
         this.booleanSetting,
         this.booleanSetting3,
         this.booleanSetting2,
         this.textSetting,
         this.textSetting2,
         this.floatSetting,
         this.floatSetting2
      );
   }

   private static String[] resolveString() {
      String[] var0 = new String[AUTO_MINE3.length + 1];
      var0[0] = "Авто";

      for (int var1 = 0; var1 < AUTO_MINE3.length; var1++) {
         var0[var1 + 1] = AUTO_MINE3[var1].name();
      }

      return var0;
   }

   private static AutoMine.Shaft computeShaft(String name) {
      if (name != null && !name.isBlank()) {
         String var1 = name.toLowerCase(Locale.ROOT);

         for (AutoMine.Shaft var5 : AUTO_MINE3) {
            if (var1.contains(var5.name().toLowerCase(Locale.ROOT))) {
               return var5;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private AutoMine.Shaft computeShaft2() {
      if (!this.modeSetting.is("Авто")) {
         AutoMine.Shaft var2 = computeShaft(this.modeSetting.getCurrent());
         return var2 != null ? var2 : AUTO_MINE3[0];
      } else {
         AutoMine.Shaft var1 = computeShaft(this.text2);
         if (var1 != null) {
            return var1;
         } else {
            return this.flag ? null : AUTO_MINE3[0];
         }
      }
   }

   private boolean checkState() {
      AutoMine.Shaft var1 = this.computeShaft2();
      if (var1 != null && (var1 != this.shaft || !this.flag)) {
         boolean var2 = this.flag;
         this.shaft = var1;
         this.flag = true;
         return var2;
      } else {
         return false;
      }
   }

   @Override
   public void onEnable() {
      this.updateState();
      super.onEnable();
   }

   private void updateState() {
      this.updateState10();
      this.checkState();
      this.flag3 = false;
      this.index5 = 0;
      this.timestamp13 = 0L;
      this.timestamp32 = 0L;
      this.flag8 = false;
      this.flag7 = false;
      this.timestamp41 = 0L;
      this.timestamp42 = 0L;
      this.timestamp46 = 0L;
      this.volatileboolean5 = false;
      this.flag9 = false;
      this.flag10 = false;
      this.timestamp18 = 0L;
      this.index6 = 0;
      this.timestamp19 = 0L;
      this.timestamp15 = 0L;
      this.timestamp16 = 0L;
      this.timestamp17 = 0L;
      this.timestamp40 = 0L;
      this.flag6 = false;
      this.timestamp34 = 0L;
      this.timestamp35 = 0L;
      this.timestamp36 = 0L;
      this.timestamp37 = 0L;
      this.timestamp38 = 0L;
      this.index9 = 0;
      this.timestamp22 = 0L;
      this.timestamp23 = 0L;
      this.timestamp24 = 0L;
      this.minePathNavigator.reset();
      this.index8 = Integer.MIN_VALUE;
      this.index7 = 0;
      this.volume5 = 0.0F;
      this.volume6 = 0.0F;
      this.timestamp25 = 0L;
      this.volume3 = 16.0F;
      this.volume4 = 16.0F;
      this.timestamp21 = 0L;
      this.timestamp26 = 0L;
      this.volume7 = 0.0F;
      this.timestamp27 = 0L;
      this.timestamp28 = 0L;
      this.index10 = 0;
      this.timestamp29 = 0L;
      this.timestamp30 = 0L;
      this.timestamp31 = 320L;
      this.timestamp47 = System.currentTimeMillis();
      this.hooksObtained = 0;
      this.index12 = -1;
      this.index13 = -1;
      this.flag11 = false;
      this.timestamp48 = 0L;
      this.world2 = null;
      this.hooksObtained2 = 0L;
      ChatUtils.sendMessage("§e§lВключите пожалуйста автоскупку и настройте категории в ней");
      this.timestamp6 = 0L;
      this.text2 = null;
      this.text3 = null;
      this.timestamp7 = 0L;
      this.timestamp5 = 0L;
      this.hashSet.clear();
      this.updateState37();
      this.hashSet2.clear();
      this.flag2 = false;
      this.updateState36();
      this.updateState4(AutoMine.State.WARP);
   }

   public void startAsBotWorker() {
      this.setEnable(true);
      this.updateState();
   }

   public void stopAsBotWorker() {
      this.updateState17();
      this.flag3 = false;
      this.updateState2();
      this.setEnable(false);
   }

   @Override
   public void onDisable() {
      this.updateState17();
      this.flag3 = false;
      this.updateState2();
      super.onDisable();
   }

   private void updateState2() {
      if (!BotContext.isBot()) {
         RotationStorage.instance.stopRotation();
      }
   }

   private void updateState3() {
      this.timestamp9 = System.currentTimeMillis();
   }

   private void updateState4(AutoMine.State next) {
      if (next != AutoMine.State.MINE && this.flag6) {
         this.flag6 = false;
         this.timestamp36 = 0L;
         this.timestamp37 = 0L;
         if (mc.currentScreen != null) {
            this.updateState34();
         }
      }

      this.state2 = next;
      this.timestamp8 = System.currentTimeMillis();
      this.updateState3();
      if (next == AutoMine.State.DUMP) {
         this.timestamp39 = 0L;
         this.flag7 = false;
         this.flag8 = false;
      }
   }

   private void updateState5(String msg) {
   }

   private void updateState6() {
      this.updateState17();
      this.updateState22();
      this.updateState34();
      this.flag3 = false;
      this.flag4 = false;
      this.timestamp12 = 0L;
      this.timestamp13 = 0L;
      this.blockPos2 = null;
      this.vec3d2 = null;
      this.flag7 = false;
      this.flag8 = false;
      this.timestamp39 = 0L;
      this.timestamp43 = 0L;
      this.timestamp44 = 0L;
      this.index4 = 0;
      this.timestamp46 = 0L;
      this.timestamp18 = 0L;
      this.flag6 = false;
      this.timestamp38 = 0L;
      this.updateState37();
      this.updateState4(AutoMine.State.WARP);
   }

   private boolean checkCondition(long now) {
      if (this.timestamp41 == 0L) {
         this.timestamp41 = now;
         return false;
      } else if (now - this.timestamp41 > 180000L) {
         this.updateState5("§cПочинка не удаётся 3 мин — копаем как есть, ретрай позже");
         this.timestamp42 = now + 60000L;
         this.timestamp41 = 0L;
         this.updateState34();
         this.updateState17();
         this.updateState4(AutoMine.State.MINE);
         return true;
      } else {
         return false;
      }
   }

   @EventLink
   public void onClientTick(EventTickPre event) {
      if (this.isEnable()) {
         if (!(mc.currentScreen instanceof ModernGui)) {
            long var2 = System.currentTimeMillis();
            if (mc.world != null && mc.player != null && mc.getNetworkHandler() != null) {
               ServerInfo var5 = mc.getCurrentServerEntry();
               if (var5 != null) {
                  this.serverInfo = var5;
               }

               this.timestamp15 = 0L;
               if (this.timestamp17 != 0L && var2 >= this.timestamp17) {
                  this.timestamp17 = 0L;
                  this.updateState5("§aВернулись на сервер — заходим на лайт и продолжаем");
                  this.updateState6();
                  this.updateState4(AutoMine.State.SWITCH_LIGHT);
               }
            } else if (!RCTCommand.RUNNING) {
               if (mc.currentScreen instanceof DisconnectedScreen) {
                  if (this.serverInfo != null) {
                     if (this.timestamp15 == 0L) {
                        this.timestamp15 = var2;
                        this.updateState5("§cКикнуло с сервера — переподключаемся");
                     } else if (var2 - this.timestamp15 >= 5000L) {
                        if (var2 - this.timestamp16 >= 20000L) {
                           this.timestamp16 = var2;
                           this.timestamp17 = var2 + 8000L;
                           ServerInfo var4 = this.serverInfo;
                           mc.execute(() -> {
                              try {
                                 ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc, ServerAddress.parse(var4.address), var4, false, null);
                              } catch (Exception var2x) {
                              }
                           });
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null && mc.interactionManager != null) {
         if (mc.currentScreen instanceof ModernGui) {
            this.updateState17();
            this.updateState22();
         } else {
            long var2 = System.currentTimeMillis();
            if (!this.flag2) {
               this.updateState36();
            }

            if (RCTCommand.RUNNING) {
               this.updateState17();
               this.timestamp8 = var2;
               this.updateState3();
            } else if (!this.checkState7() && this.checkState()) {
               ChatUtils.sendMessage(
                  "§eШахта переключена на §f"
                     + this.shaft.name()
                     + (this.modeSetting.is("Авто") ? " §7(авто по голограмме)" : "")
                     + " §e— летим на §f/"
                     + this.shaft.warp()
               );
               this.hashMap.clear();
               this.timestamp6 = 0L;
               this.text2 = null;
               this.text3 = null;
               this.updateState6();
            } else {
               this.toBooleanOrDefault2(var2);
               if (!mc.player.isDead() && !(mc.player.getHealth() <= 0.0F)) {
                  if (this.checkState2()
                     || this.state2 != AutoMine.State.MINE
                        && this.state2 != AutoMine.State.DUMP
                        && this.state2 != AutoMine.State.GO_ENTRY
                        && this.state2 != AutoMine.State.CHECK_REGION
                        && this.state2 != AutoMine.State.CHECK_PICK) {
                     if (var2 - this.timestamp8 > 60000L
                        && this.state2 != AutoMine.State.MINE
                        && this.state2 != AutoMine.State.DUMP
                        && this.state2 != AutoMine.State.REPAIR_MEND
                        && !this.checkState7()) {
                        this.updateState5("§cЗастряли в " + this.state2 + " (" + (var2 - this.timestamp8) / 1000L + "с) — сброс на варп");
                        this.updateState6();
                     } else if (this.timestamp9 != 0L && var2 - this.timestamp9 > 120000L) {
                        this.updateState5("§cНет прогресса " + (var2 - this.timestamp9) / 1000L + "с — мастер-сброс на варп");
                        this.updateState6();
                     } else {
                        boolean var4 = this.state2 == AutoMine.State.REPAIR_MEND
                           || this.state2 == AutoMine.State.DUMP
                           || this.state2 == AutoMine.State.STASH_CHEST
                           || this.flag6;
                        if (!var4 && mc.currentScreen != null && !(mc.currentScreen instanceof DisconnectedScreen)) {
                           if (this.timestamp40 == 0L) {
                              this.timestamp40 = var2;
                           }

                           if (var2 - this.timestamp10 >= 350L) {
                              this.updateState34();
                              this.timestamp10 = var2;
                           }

                           if (var2 - this.timestamp40 > 10000L) {
                              this.timestamp40 = 0L;
                              this.updateState5("§cЧужое меню не закрывается — сброс на варп");
                              this.updateState6();
                           }
                        } else {
                           this.timestamp40 = 0L;
                        }

                        if (this.volatileboolean5) {
                           this.volatileboolean5 = false;
                           this.updateState46(var2);
                        }

                        if (mc.currentScreen == null && !RCTCommand.RUNNING && !this.checkState7()) {
                           this.updateState29(var2);
                        }

                        if (!this.checkState7()) {
                           this.updateState28(var2);
                        }

                        switch (this.state2) {
                           case WARP:
                              this.updateState32(this.shaft.warp());
                              this.updateState5("§e/" + this.shaft.warp());
                              this.updateState4(AutoMine.State.WAIT_WARP);
                              break;
                           case WAIT_WARP:
                              if (this.checkState2()) {
                                 this.vec3d2 = null;
                                 this.updateState17();
                                 this.updateState5("§eПрибыли на варп — заходим в шахту");
                                 this.updateState4(AutoMine.State.GO_ENTRY);
                              } else if (var2 - this.timestamp8 >= 5000L) {
                                 if (this.index4 < 3) {
                                    this.index4++;
                                    this.updateState5("§eНе прибыли на шахту — повтор /" + this.shaft.warp() + " (" + this.index4 + ")");
                                    this.updateState4(AutoMine.State.WARP);
                                 } else {
                                    this.index4 = 0;
                                    this.updateState4(AutoMine.State.SWITCH_LIGHT);
                                 }
                              }
                              break;
                           case GO_ENTRY:
                              this.updateState8(var2);
                              break;
                           case CHECK_REGION:
                              this.updateState7(var2);
                              break;
                           case SWITCH_LIGHT:
                              this.updateState9(var2);
                              break;
                           case WAIT_RCT:
                              if (var2 - this.timestamp8 >= 3500L) {
                                 this.updateState4(AutoMine.State.WARP);
                              }
                              break;
                           case CHECK_PICK:
                              this.updateState11(var2);
                              break;
                           case REPAIR_MEND:
                              this.updateState12(var2);
                              break;
                           case MINE:
                              this.updateState13(var2);
                              break;
                           case DUMP:
                              this.updateState27(var2);
                              break;
                           case STASH_RCT:
                              this.updateState39(var2);
                              break;
                           case STASH_HOME:
                              this.updateState40(var2);
                              break;
                           case STASH_CHEST:
                              this.updateState41(var2);
                        }
                     }
                  } else {
                     this.updateState5("§cМы не у шахты — сброс на варп");
                     this.updateState6();
                  }
               } else {
                  this.updateState17();
                  this.updateState22();
                  if (this.state2 != AutoMine.State.WARP && this.state2 != AutoMine.State.WAIT_WARP) {
                     this.updateState4(AutoMine.State.WARP);
                  }
               }
            }
         }
      }
   }

   private void updateState7(long now) {
      this.index4 = 0;
      double var3 = this.resolveDouble();
      double var5 = 0.6;
      this.updateState5(String.format(Locale.ROOT, "Заполненность шахты: %.0f%% (порог %.0f%%)", var3 * 100.0, var5 * 100.0));
      if (var3 >= var5) {
         this.updateState4(AutoMine.State.CHECK_PICK);
      } else {
         this.updateState4(AutoMine.State.SWITCH_LIGHT);
      }
   }

   private void updateState8(long now) {
      if (this.vec3d2 == null) {
         this.vec3d2 = mc.player.getPos();
      }

      double var3 = mc.player.getX() - this.vec3d2.x;
      double var5 = mc.player.getZ() - this.vec3d2.z;
      double var7 = Math.sqrt(var3 * var3 + var5 * var5);
      if (!(var7 >= 2.0) && now - this.timestamp8 <= 8000L) {
         BlockPos var9 = BlockPos.ofFloored(this.shaft.cx(), mc.player.getY(), this.shaft.cz());
         MinePathNavigator.Output var10 = this.computeOutput(var9, 1.4, now);
         if (var10 != null && var10.hasLook) {
            this.updateState18(var10, now);
         } else {
            this.updateState25(var9);
         }
      } else {
         this.updateState17();
         this.vec3d2 = null;
         this.updateState5("Вход завершён — начинаем копать");
         this.updateState4(AutoMine.State.CHECK_REGION);
      }
   }

   private boolean checkState2() {
      AutoMine.Shaft var1 = this.shaft;
      double var2 = mc.player.getX() - var1.cx();
      double var4 = mc.player.getY() - var1.cy();
      double var6 = mc.player.getZ() - var1.cz();
      if (Math.abs(var4) > 40.0) {
         return false;
      }

      double var8 = var2 * var2 + var6 * var6;
      if (var8 > 3600.0) {
         return false;
      }

      for (AutoMine.Shaft var13 : AUTO_MINE3) {
         if (var13 != var1) {
            double var14 = mc.player.getX() - var13.cx();
            double var16 = mc.player.getZ() - var13.cz();
            if (var14 * var14 + var16 * var16 < var8) {
               return false;
            }
         }
      }

      return true;
   }

   private double resolveDouble() {
      long var1 = 0L;
      long var3 = 0L;
      AutoMine.Shaft var5 = this.shaft;
      Mutable var6 = new Mutable();

      for (int var7 = var5.x1(); var7 <= var5.x2(); var7++) {
         for (int var8 = var5.y1(); var8 <= var5.y2(); var8++) {
            for (int var9 = var5.z1(); var9 <= var5.z2(); var9++) {
               var6.set(var7, var8, var9);
               var1++;
               if (!mc.world.getBlockState(var6).isAir()) {
                  var3++;
               }
            }
         }
      }

      return var1 == 0L ? 0.0 : (double)var3 / var1;
   }

   private void updateState9(long now) {
      int var3 = this.resolveInt();
      if (var3 <= 0) {
         this.updateState5("§cСписок анархий пуст — укажи их в настройке");
      } else {
         this.updateState17();
         this.flag3 = false;
         this.index5 = 0;
         this.timestamp13 = 0L;
         this.updateState17();
         this.updateState2();
         this.flag8 = false;
         this.flag7 = false;
         this.blockPos2 = null;
         this.hashMap.clear();
         this.updateState34();
         this.updateState5("§eМало руды — переключаемся на анархию §6#" + var3);
         if (BotContext.isBot()) {
            if (!BotTasks.INSTANCE.switchAnarchy(BotContext.current().getName(), var3)) {
               this.updateState5("§cПереход не начался — копаем здесь");
               this.updateState4(AutoMine.State.MINE);
            } else {
               this.updateState4(AutoMine.State.WAIT_RCT);
            }
         } else {
            try {
               Lumen.INSTANCE.commandStorage.getDispatcher().execute("rct " + var3, Lumen.INSTANCE.commandStorage.getSource());
            } catch (CommandSyntaxException var5) {
               this.updateState5("§cНе удалось .rct: " + var5.getMessage());
            }

            this.updateState4(AutoMine.State.WAIT_RCT);
         }
      }
   }

   private void updateState10() {
      this.integers.clear();
      HashSet var1 = new HashSet();
      String var2 = "2,14,28,41";
      if (var2 != null) {
         for (String var6 : var2.split(",")) {
            try {
               var1.add(Integer.parseInt(var6.trim()));
            } catch (NumberFormatException var15) {
            }
         }
      }

      LinkedHashSet<Integer> var17 = new LinkedHashSet<>();
      String var18 = "1-74";
      if (var18 != null && !var18.isBlank()) {
         for (String var8 : var18.split(",")) {
            String var9 = var8.trim();
            if (!var9.isEmpty()) {
               if (var9.contains("-")) {
                  String[] var25 = var9.split("-");
                  if (var25.length == 2) {
                     try {
                        int var11 = Integer.parseInt(var25[0].trim());
                        int var12 = Integer.parseInt(var25[1].trim());

                        for (int var13 = Math.min(var11, var12); var13 <= Math.max(var11, var12); var13++) {
                           if (var13 >= 1 && var13 <= 74) {
                              var17.add(var13);
                           }
                        }
                     } catch (NumberFormatException var16) {
                     }
                  }
               } else {
                  try {
                     int var10 = Integer.parseInt(var9);
                     if (var10 >= 1 && var10 <= 74) {
                        var17.add(var10);
                     }
                  } catch (NumberFormatException var14) {
                  }
               }
            }
         }
      } else {
         for (int var19 = 1; var19 <= 74; var19++) {
            var17.add(var19);
         }
      }

      if (var17.isEmpty()) {
         for (int var21 = 1; var21 <= 74; var21++) {
            var17.add(var21);
         }
      }

      for (int var24 : var17) {
         if (!var1.contains(var24)) {
            this.integers.add(var24);
         }
      }
   }

   private int resolveInt() {
      if (this.integers.isEmpty()) {
         this.updateState10();
      }

      if (this.integers.isEmpty()) {
         return -1;
      }

      int var1 = this.integers.get(this.index11 % this.integers.size());
      this.index11 = (this.index11 + 1) % this.integers.size();
      return var1;
   }

   public int currentAnarchy() {
      if (this.integers.isEmpty()) {
         this.updateState10();
      }

      return !this.integers.isEmpty() && this.index11 > 0
         ? this.integers.get((this.index11 - 1) % this.integers.size())
         : -1;
   }

   private void updateState11(long now) {
      if (this.checkState3() && now >= this.timestamp42) {
         this.state3 = AutoMine.State.MINE;
         this.flag9 = false;
         this.updateState5("§eКирка изношена — чиним");
         this.updateState4(AutoMine.State.REPAIR_MEND);
      } else {
         this.updateState4(AutoMine.State.MINE);
      }
   }

   private boolean checkState3() {
      ItemStack var1 = this.computeItemStack();
      if (var1 != null && var1.isDamageable() && var1.getMaxDamage() > 0) {
         double var2 = 1.0 - (double)var1.getDamage() / var1.getMaxDamage();
         return var2 < 0.7;
      } else {
         return false;
      }
   }

   private boolean checkState4() {
      ItemStack var1 = this.computeItemStack();
      if (var1 != null && var1.isDamageable() && var1.getMaxDamage() > 0) {
         double var2 = 1.0 - (double)var1.getDamage() / var1.getMaxDamage();
         return var2 >= 0.7;
      } else {
         return true;
      }
   }

   private int resolveInt2(GenericContainerScreenHandler handler, int containerSlots) {
      int var3 = -1;

      for (int var4 = 0; var4 < containerSlots; var4++) {
         String var5 = Registries.ITEM.getId(handler.getSlot(var4).getStack().getItem()).toString();
         if (var5.contains("stained_glass")) {
            if (var5.contains("lime") || var5.contains("green")) {
               return var4;
            }

            if (var3 < 0) {
               var3 = var4;
            }
         }
      }

      return var3;
   }

   private void updateState12(long now) {
      if (!this.checkCondition(now)) {
         if (this.checkState4()) {
            this.timestamp41 = 0L;
            this.updateState17();
            if (this.checkState6()) {
               this.updateState31(now);
            } else {
               this.updateState5("§aКирка починена до 70%");
               this.updateState4(this.state3);
            }
         } else if (this.resolveInt7(Items.EXPERIENCE_BOTTLE) <= 0) {
            this.updateState17();
            this.timestamp41 = 0L;
            this.timestamp42 = now + 60000L;
            this.updateState5("§eПузырьков опыта нет — копаем без починки, автопокупка отключена");
            this.updateState4(this.state3);
         } else if (this.checkCondition2(now)) {
            if (this.checkCondition3(now)) {
               this.updateState23(mc.player.getYaw(), 74.0F + this.random.nextFloat() * 10.0F);
               if (!(mc.player.getPitch() < 55.0F)) {
                  if (now - this.timestamp30 >= this.timestamp31) {
                     mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
                     mc.player.swingHand(Hand.OFF_HAND);
                     this.timestamp30 = now;
                     this.timestamp31 = 260L + this.random.nextInt(240);
                  }
               }
            }
         }
      }
   }

   private boolean checkCondition2(long now) {
      if (this.checkState6()) {
         this.updateState31(now);
         return false;
      }

      for (int var3 = 0; var3 < 9; var3++) {
         if (this.checkCondition18(mc.player.getInventory().getStack(var3))) {
            mc.player.getInventory().selectedSlot = var3;
            return true;
         }
      }

      int var4 = this.resolveInt9();
      if (var4 != -1 && now - this.timestamp10 >= 350L) {
         this.updateState33(mc.player.playerScreenHandler.syncId, var4, 8, SlotActionType.SWAP);
         this.timestamp10 = now;
      }

      return false;
   }

   private boolean checkCondition3(long now) {
      ItemStack var3 = mc.player.getOffHandStack();
      if (!var3.isEmpty() && var3.getItem() == Items.EXPERIENCE_BOTTLE) {
         return true;
      }

      if (now - this.timestamp10 < 350L) {
         return false;
      }

      if (this.checkCondition20(var3)) {
         this.flag10 = true;
      }

      int var4 = -1;
      int var5 = this.resolveInt10(Items.EXPERIENCE_BOTTLE);
      if (var5 != -1 && var5 != mc.player.getInventory().selectedSlot) {
         var4 = this.resolveInt13(var5);
      } else {
         var4 = this.resolveInt11(Items.EXPERIENCE_BOTTLE);
      }

      if (var4 == -1) {
         return false;
      }

      this.updateState33(mc.player.playerScreenHandler.syncId, var4, 40, SlotActionType.SWAP);
      this.timestamp10 = now;
      return false;
   }

   private void updateState13(long now) {
      if (now < this.timestamp46) {
         this.updateState22();
         this.volatileboolean = false;
         this.volatileboolean2 = false;
      } else {
         boolean var3 = mc.currentScreen instanceof GenericContainerScreen var4 && this.checkCondition9(var4);
         boolean var10 = this.flag6 && var3;
         if (mc.currentScreen != null && !var10) {
            this.updateState22();
            this.volatileboolean = false;
            if (this.timestamp44 == 0L) {
               this.timestamp44 = now;
            }

            if (now - this.timestamp10 > 350L) {
               this.updateState34();
               this.timestamp10 = now;
            }

            if (now - this.timestamp44 > 15000L) {
               this.updateState5("§cМеню не закрывается — сброс на варп");
               this.timestamp44 = 0L;
               this.updateState6();
            }
         } else {
            this.timestamp44 = 0L;
            boolean var11 = mc.currentScreen instanceof GenericContainerScreen var6 && this.checkCondition9(var6);
            if (this.flag6 && !var11 && this.timestamp36 != 0L && now - this.timestamp36 > 12000L) {
               this.flag6 = false;
               this.timestamp36 = 0L;
               this.timestamp37 = 0L;
               this.timestamp38 = now + 5000L;
               this.updateState5("§7Фоновая сдача не открылась — продолжаем копать");
            }

            if (this.checkState3() && now >= this.timestamp42) {
               this.flag6 = false;
               if (mc.currentScreen != null) {
                  this.updateState34();
               }

               this.updateState17();
               this.updateState4(AutoMine.State.CHECK_PICK);
            } else if (!this.checkCondition8(now)) {
               this.updateState17();
            } else {
               this.updateState16(now);
               if (!this.flag3) {
                  this.flag3 = true;
                  this.flag4 = false;
                  this.timestamp12 = 0L;
                  this.timestamp13 = 0L;
                  this.timestamp18 = now;
                  this.updateState5("§aКопаем шахту");
               }

               if (this.blockPos2 != null && mc.world.getBlockState(this.blockPos2).isAir()) {
                  this.timestamp18 = now;
                  this.blockPos2 = null;
               }

               if (mc.player.fallDistance > 3.0F) {
                  BlockPos var13 = this.blockPos2 != null ? this.blockPos2 : this.blockPos3;
                  if (var13 != null) {
                     this.updateState47(var13, 3, 120000L, now);
                  }

                  this.blockPos2 = null;
                  this.updateState22();
                  this.updateState17();
                  if (now - this.timestamp19 > 60000L) {
                     this.timestamp19 = now;
                     this.index6 = 0;
                  }

                  if (++this.index6 >= 3) {
                     this.updateState5("§cПостоянно падаем — меняем анархию");
                     this.index6 = 0;
                     this.updateState14();
                  }
               } else if (now - this.timestamp18 > 45000L) {
                  this.updateState5("§eНичего не сломали 45с — шахта выкопана, меняем анархию");
                  this.updateState14();
               } else {
                  BlockPos var12 = this.checkCondition5(this.blockPos2) ? this.blockPos2 : this.computeBlockPos();
                  if (var12 != null) {
                     BlockState var15 = mc.world.getBlockState(var12);
                     if (this.checkCondition23(var15) || !this.checkCondition22(var15)) {
                        this.updateState35(var12, now);
                        this.blockPos2 = null;
                        this.updateState22();
                     } else if (this.checkCondition24(var12)) {
                        this.updateState35(var12, now);
                        this.blockPos2 = null;
                        this.updateState22();
                     } else {
                        this.timestamp12 = 0L;
                        this.timestamp13 = now;
                        this.blockPos4 = null;
                        if (this.blockPos2 != null && this.blockPos2.equals(var12)) {
                           if (now - this.timestamp14 > 1700L) {
                              this.updateState35(var12, now);
                              this.blockPos2 = null;
                              this.updateState22();
                              this.updateState5("§7Блок не ломается — пропускаем");
                              return;
                           }
                        } else {
                           this.blockPos2 = var12;
                           this.timestamp14 = now;
                        }

                        this.updateState24(var12);
                        this.volatileboolean = this.resolveDouble2(var12) > 1.4;
                        this.volatileboolean2 = false;
                        this.volatileboolean3 = false;
                        this.volatileboolean4 = false;
                        this.updateState20(var12);
                        this.updateState3();
                        this.updateState26(now);
                     }
                  } else {
                     BlockPos var14 = this.computeBlockPos2();
                     if (var14 == null) {
                        this.blockPos2 = null;
                        this.updateState22();
                        this.volatileboolean2 = false;
                        this.updateState15("§eКопать нечего — меняем анархию");
                     } else {
                        BlockPos var8 = this.computeBlockPos3(var14);
                        if (var8 != null) {
                           if (this.blockPos2 != null && this.blockPos2.equals(var8)) {
                              if (now - this.timestamp14 > 1700L) {
                                 this.updateState35(var8, now);
                                 this.blockPos2 = null;
                                 this.updateState22();
                                 return;
                              }
                           } else {
                              this.blockPos2 = var8;
                              this.timestamp14 = now;
                           }

                           this.updateState24(var8);
                           this.volatileboolean = this.resolveDouble2(var8) > 1.4;
                           this.volatileboolean2 = false;
                           this.volatileboolean3 = false;
                           this.volatileboolean4 = false;
                           this.updateState20(var8);
                           this.updateState3();
                           this.timestamp12 = 0L;
                           this.timestamp13 = now;
                        } else {
                           this.blockPos2 = null;
                           this.updateState22();
                           MinePathNavigator.Output var9 = this.computeOutput(var14, 3.9, now);
                           if (var9 != null && var9.hasLook && this.resolveDouble2(var14) > 5.5) {
                              this.updateState18(var9, now);
                           } else {
                              this.updateState25(var14);
                           }

                           if (this.timestamp12 == 0L) {
                              this.timestamp12 = now;
                           } else if (now - this.timestamp12 > 12000L) {
                              this.updateState15("§eНе достаём руду 12 сек — меняем анархию");
                              return;
                           }

                           if (this.timestamp13 == 0L) {
                              this.timestamp13 = now;
                           } else if (now - this.timestamp13 > 5000L) {
                              this.updateState5("§cНичего не копаем 5 секунд — переключаем анархию");
                              this.updateState14();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void updateState14() {
      this.flag4 = true;
      this.updateState17();
      this.updateState22();
      this.flag3 = false;
      this.timestamp12 = 0L;
      this.timestamp13 = 0L;
      this.timestamp18 = 0L;
      this.blockPos2 = null;
      this.updateState4(AutoMine.State.SWITCH_LIGHT);
   }

   private void updateState15(String msg) {
      this.updateState5(msg);
      this.updateState14();
   }

   private void updateState16(long now) {
      this.flag6 = false;
      this.timestamp36 = 0L;
      this.timestamp37 = 0L;
      this.timestamp38 = 0L;
   }

   private BlockPos computeBlockPos() {
      double var1 = 4.5;
      double var3 = var1 * var1 + 0.01;
      Vec3d var5 = mc.player.getEyePos();
      Vec3d var6 = mc.player.getRotationVec(1.0F);
      int var7 = (int)Math.ceil(var1);
      int var8 = (int)Math.floor(mc.player.getX());
      int var9 = (int)Math.floor(var5.y);
      int var10 = (int)Math.floor(mc.player.getZ());
      AutoMine.Shaft var11 = this.shaft;
      int var12 = Math.max(var11.x1(), var8 - var7);
      int var13 = Math.min(var11.x2(), var8 + var7);
      int var14 = Math.max(var11.y1(), var9 - var7);
      int var15 = Math.min(var11.y2(), var9 + var7);
      int var16 = Math.max(var11.z1(), var10 - var7);
      int var17 = Math.min(var11.z2(), var10 + var7);
      double var18 = Double.MAX_VALUE;
      BlockPos var20 = null;
      Mutable var21 = new Mutable();

      for (int var22 = var12; var22 <= var13; var22++) {
         for (int var23 = var14; var23 <= var15; var23++) {
            for (int var24 = var16; var24 <= var17; var24++) {
               var21.set(var22, var23, var24);
               BlockState var25 = mc.world.getBlockState(var21);
               if (!this.checkCondition23(var25) && this.checkCondition22(var25) && !this.checkCondition29(var22, var23, var24)) {
                  double var26 = var22 + 0.5 - var5.x;
                  double var28 = var23 + 0.5 - var5.y;
                  double var30 = var24 + 0.5 - var5.z;
                  double var32 = var26 * var26 + var28 * var28 + var30 * var30;
                  if (!(var32 > var3)) {
                     double var34 = Math.sqrt(var32);
                     double var36 = (var6.x * var26 + var6.y * var28 + var6.z * var30) / Math.max(1.0E-4, var34);
                     double var38 = Math.acos(MathHelper.clamp(var36, -1.0, 1.0));
                     double var40 = 0.08 * var38 + 0.35 * var34 - 0.22 * this.resolveInt3(var22, var23, var24);
                     if (var40 < var18) {
                        var18 = var40;
                        var20 = new BlockPos(var22, var23, var24);
                     }
                  }
               }
            }
         }
      }

      return var20;
   }

   private boolean checkCondition4(BlockPos p) {
      AutoMine.Shaft var2 = this.shaft;
      return p.getX() >= var2.x1() && p.getX() <= var2.x2() && p.getY() >= var2.y1() && p.getY() <= var2.y2() && p.getZ() >= var2.z1() && p.getZ() <= var2.z2();
   }

   private boolean checkCondition5(BlockPos p) {
      if (p == null || mc.player == null || mc.world == null) {
         return false;
      }

      if (!this.checkCondition4(p)) {
         return false;
      }

      BlockState var2 = mc.world.getBlockState(p);
      if (!this.checkCondition23(var2) && this.checkCondition22(var2)) {
         if (this.checkCondition29(p.getX(), p.getY(), p.getZ())) {
            return false;
         }

         Vec3d var3 = mc.player.getEyePos();
         double var4 = p.getX() + 0.5 - var3.x;
         double var6 = p.getY() + 0.5 - var3.y;
         double var8 = p.getZ() + 0.5 - var3.z;
         return var4 * var4 + var6 * var6 + var8 * var8 > 20.26 ? false : !this.checkCondition24(p);
      } else {
         return false;
      }
   }

   private int resolveInt3(int x, int y, int z) {
      int var4 = 0;
      Mutable var5 = new Mutable();

      for (int var6 = -1; var6 <= 1; var6++) {
         for (int var7 = -1; var7 <= 1; var7++) {
            for (int var8 = -1; var8 <= 1; var8++) {
               if (var6 != 0 || var7 != 0 || var8 != 0) {
                  var5.set(x + var6, y + var7, z + var8);
                  BlockState var9 = mc.world.getBlockState(var5);
                  if (!this.checkCondition23(var9)) {
                     var4++;
                  }
               }
            }
         }
      }

      return var4;
   }

   private BlockPos computeBlockPos2() {
      double var1 = mc.player.getX();
      double var3 = mc.player.getY();
      double var5 = mc.player.getZ();
      double var7 = Double.MAX_VALUE;
      BlockPos var9 = null;
      AutoMine.Shaft var10 = this.shaft;
      Mutable var11 = new Mutable();

      for (int var12 = var10.x1(); var12 <= var10.x2(); var12++) {
         for (int var13 = var10.y1(); var13 <= var10.y2(); var13++) {
            for (int var14 = var10.z1(); var14 <= var10.z2(); var14++) {
               var11.set(var12, var13, var14);
               BlockState var15 = mc.world.getBlockState(var11);
               if (!this.checkCondition23(var15)
                  && this.checkCondition22(var15)
                  && !this.checkCondition29(var12, var13, var14)
                  && !(Math.abs(var13 + 0.5 - var3) > 5.0)) {
                  double var16 = var12 + 0.5 - var1;
                  double var18 = var13 + 0.5 - var3;
                  double var20 = var14 + 0.5 - var5;
                  double var22 = var16 * var16 + var18 * var18 + var20 * var20;
                  if (var22 < var7) {
                     var7 = var22;
                     var9 = new BlockPos(var12, var13, var14);
                  }
               }
            }
         }
      }

      return var9;
   }

   private BlockPos computeBlockPos3(BlockPos ore) {
      if (mc.player != null && mc.world != null && ore != null) {
         Vec3d var2 = mc.player.getEyePos();
         double var3 = ore.getX() + 0.5 - var2.x;
         double var5 = ore.getY() + 0.5 - var2.y;
         double var7 = ore.getZ() + 0.5 - var2.z;
         double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
         if (var9 < 1.0E-4) {
            return null;
         }

         Vec3d var11 = new Vec3d(var3 / var9, var5 / var9, var7 / var9);
         Vec3d var12 = var2.add(var11.multiply(4.5));
         BlockHitResult var13 = mc.world.raycast(new RaycastContext(var2, var12, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
         if (var13 != null && var13.getType() == Type.BLOCK) {
            BlockPos var14 = var13.getBlockPos();
            if (!this.checkCondition4(var14)) {
               return null;
            } else {
               BlockState var15 = mc.world.getBlockState(var14);
               if (this.checkCondition23(var15) || !this.checkCondition22(var15) || this.checkCondition29(var14.getX(), var14.getY(), var14.getZ())) {
                  return null;
               } else {
                  return this.checkCondition24(var14) ? null : var14;
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @EventLink
   public void onMoveInput(EventMoveInput e) {
      if (this.isEnable()
         && (
            this.state2 == AutoMine.State.MINE
               || this.state2 == AutoMine.State.GO_ENTRY
               || this.state2 == AutoMine.State.REPAIR_MEND
               || this.state2 == AutoMine.State.STASH_CHEST
         )) {
         boolean var2 = mc.player != null && mc.player.horizontalCollision;
         long var3 = System.currentTimeMillis();
         if (var2) {
            this.timestamp33 = var3;
         }

         boolean var5 = var3 - this.timestamp33 < 60L;
         int var6 = mc.player != null ? mc.player.age : Integer.MIN_VALUE;
         boolean var7 = this.volatileboolean
            && this.index8 != Integer.MIN_VALUE
            && var6 >= this.index8
            && var6 - this.index8 <= 1;
         float var8 = 0.0F;
         float var9 = 0.0F;
         if (this.volatileboolean) {
            double var10;
            if (this.state2 == AutoMine.State.REPAIR_MEND) {
               var10 = 8.0;
            } else if (this.blockPos2 != null) {
               var10 = this.resolveDouble2(this.blockPos2);
            } else {
               var10 = 1.8;
            }

            if (var10 > 0.8 && !var5) {
               this.flag5 = true;
            } else if (var10 < 0.5 || var5) {
               this.flag5 = false;
            }

            var8 = 1.0F;
            if (var7) {
               float var12 = (float)Math.toDegrees(Math.atan2(this.volume2, this.volume));
               float[] var13 = this.computefloat(var12, var3);
               var8 = var13[0];
               var9 = var13[1];
            }

            boolean var14 = this.flag5 && var3 >= this.timestamp28 && var8 > 0.0F;
            if (var14 && this.random.nextFloat() < 0.004F) {
               this.timestamp28 = var3 + 280L + this.random.nextInt(320);
               var14 = false;
            }

            e.setSprint(var14);
         }

         if (this.volatileboolean3) {
            var9 = 1.0F;
         } else if (this.volatileboolean4) {
            var9 = -1.0F;
         } else if (this.volatileboolean && var9 == 0.0F) {
            if (var3 < this.timestamp27) {
               var9 = this.volume7;
            } else if (var3 >= this.timestamp26) {
               if (this.random.nextFloat() < 0.35F) {
                  if (this.index7 != 0 && this.random.nextFloat() < 0.7F) {
                     this.volume7 = this.index7;
                  } else {
                     this.volume7 = this.random.nextBoolean() ? 1.0F : -1.0F;
                  }

                  this.timestamp27 = var3 + 100L + this.random.nextInt(150);
               }

               this.timestamp26 = var3 + 500L + this.random.nextInt(700);
            }
         }

         if (var8 != 0.0F) {
            e.setForward(var8);
         }

         if (var9 != 0.0F) {
            e.setStrafe(var9);
         }

         if (this.volatileboolean2 || this.state2 == AutoMine.State.GO_ENTRY && this.volatileboolean && var2) {
            e.setJump(true);
         }
      }
   }

   private float[] computefloat(float desiredDeg, long now) {
      int var4 = Math.floorMod(Math.round(desiredDeg / 45.0F), 8);
      if (var4 != this.index10) {
         float var5 = this.index10 * 45.0F;
         float var6 = Math.abs(MathHelper.wrapDegrees(desiredDeg - var5));
         if (now - this.timestamp29 >= 140L || var6 > 60.0F) {
            this.index10 = var4;
            this.timestamp29 = now;
         }
      }

      return value3[this.index10];
   }

   private void updateState17() {
      this.volatileboolean = false;
      this.volatileboolean2 = false;
      this.volatileboolean3 = false;
      this.volatileboolean4 = false;
      this.flag5 = false;
      this.index9 = 0;
      this.timestamp22 = 0L;
      this.timestamp23 = 0L;
      this.timestamp24 = 0L;
      this.minePathNavigator.reset();
      this.index8 = Integer.MIN_VALUE;
      this.volume = 0.0F;
      this.volume2 = 0.0F;
      this.index7 = 0;
      this.volume5 = 0.0F;
      this.volume6 = 0.0F;
      this.timestamp25 = 0L;
      this.updateState22();
      this.updateState2();
   }

   private MinePathNavigator.Output computeOutput(BlockPos goal, double acceptRadius, long now) {
      MinePathNavigator.Output var6 = mc.player != null && mc.world != null ? this.minePathNavigator.tick(mc.player, mc.world, goal, acceptRadius, now) : null;
      if (var6 == null) {
         this.updateState19(now);
         this.index7 = 0;
         return null;
      } else {
         this.volatileboolean = true;
         this.volatileboolean2 = var6.jump;
         this.volatileboolean3 = false;
         this.volatileboolean4 = false;
         this.volume = var6.forward;
         this.volume2 = var6.strafe;
         this.index7 = var6.bendSign;
         this.index8 = mc.player.age;
         return var6;
      }
   }

   private void updateState18(MinePathNavigator.Output nav, long now) {
      if (now >= this.timestamp21) {
         this.volume4 = 10.0F + this.random.nextFloat() * 12.0F;
         this.timestamp21 = now + 800L + this.random.nextInt(900);
      }

      this.volume3 = this.volume3 + (this.volume4 - this.volume3) * 0.12F;
      double var4 = nav.lookX - mc.player.getX();
      double var6 = nav.lookZ - mc.player.getZ();
      double var8 = Math.hypot(var4, var6);
      if (!(var8 < 0.001)) {
         float var10 = (float)(Math.toDegrees(Math.atan2(var6, var4)) - 90.0);
         double var11 = nav.lookY - mc.player.getY();
         float var13 = (float)(-Math.toDegrees(Math.atan2(var11, Math.max(1.5, var8))));
         float var14 = MathHelper.clamp(this.volume3 + var13 * 0.6F, -35.0F, 50.0F);
         this.updateState23(var10, var14);
      }
   }

   private void updateState19(long now) {
      this.volatileboolean = true;
      this.volatileboolean2 = false;
      Vec3d var3 = mc.player.getRotationVec(1.0F);
      double var4 = var3.x;
      double var6 = var3.z;
      double var8 = Math.sqrt(var4 * var4 + var6 * var6);
      if (!(var8 < 1.0E-4)) {
         var4 /= var8;
         var6 /= var8;
         if (this.timestamp24 == 0L) {
            this.timestamp24 = now;
            this.level = mc.player.getX();
            this.level2 = mc.player.getZ();
         } else if (now - this.timestamp24 >= 700L) {
            double var10 = Math.hypot(mc.player.getX() - this.level, mc.player.getZ() - this.level2);
            this.timestamp24 = now;
            this.level = mc.player.getX();
            this.level2 = mc.player.getZ();
            if (var10 < 0.12) {
               if (this.timestamp23 == 0L) {
                  this.timestamp23 = now;
               }

               this.index9 = this.index9 == 0 ? (this.random.nextBoolean() ? 1 : -1) : -this.index9;
               this.timestamp22 = now + 600L + this.random.nextInt(500);
            } else {
               this.timestamp23 = 0L;
            }
         }

         double var20 = mc.player.getX() + var4 * 0.8;
         double var12 = mc.player.getZ() + var6 * 0.8;
         BlockPos var14 = BlockPos.ofFloored(var20, mc.player.getY() + 0.05, var12);
         boolean var15 = !this.checkCondition7(var14);
         boolean var16 = !this.checkCondition7(var14.up());
         if (var15 && !var16 && this.checkCondition7(var14.up(2))) {
            this.volatileboolean2 = true;
         } else if (!var15 && !var16 || this.index9 != 0 && now < this.timestamp22) {
            if (!var15 && !var16 && this.timestamp23 == 0L && mc.player.isOnGround() && this.random.nextFloat() < 0.006F) {
               this.volatileboolean2 = true;
            }
         } else {
            this.index9 = this.resolveInt4(var4, var6);
            this.timestamp22 = now + 500L + this.random.nextInt(400);
         }

         if (this.timestamp23 != 0L && now - this.timestamp23 > 2500L) {
            this.volatileboolean2 = true;
         }

         boolean var17 = this.index9 != 0 && now < this.timestamp22;
         this.volatileboolean3 = var17 && this.index9 < 0;
         this.volatileboolean4 = var17 && this.index9 > 0;
         if (!var17) {
            this.index9 = 0;
         }
      }
   }

   private int resolveInt4(double lx, double lz) {
      boolean var5 = this.checkCondition6(lz, -lx);
      boolean var6 = this.checkCondition6(-lz, lx);
      if (var5 == var6) {
         return this.random.nextBoolean() ? 1 : -1;
      } else {
         return var5 ? -1 : 1;
      }
   }

   private boolean checkCondition6(double dx, double dz) {
      double var5 = mc.player.getX() + dx * 0.9;
      double var7 = mc.player.getZ() + dz * 0.9;
      BlockPos var9 = BlockPos.ofFloored(var5, mc.player.getY() + 0.05, var7);
      return this.checkCondition7(var9) && this.checkCondition7(var9.up());
   }

   private boolean checkCondition7(BlockPos p) {
      return mc.world.getBlockState(p).getCollisionShape(mc.world, p).isEmpty();
   }

   private void updateState20(BlockPos pos) {
      this.blockPos3 = pos;
      if (mc.options != null) {
         mc.options.attackKey.setPressed(true);
      }

      Direction var2 = this.computeDirection(pos);
      mc.crosshairTarget = new BlockHitResult(Vec3d.ofCenter(pos), var2, pos, false);
      if (this.booleanSetting.getValue()) {
         mc.attackCooldown = 0;
         if (mc.interactionManager instanceof IClientInteractionManagerAccessor var3) {
            var3.lumen$setBlockBreakingCooldown(0);
         }
      }
   }

   public boolean shouldForceBreakingWithGui() {
      return this.isEnable() && this.blockPos3 != null && mc.currentScreen != null;
   }

   private void updateState21(long now) {
      this.flag6 = false;
      this.timestamp36 = 0L;
      this.timestamp37 = 0L;
      this.timestamp38 = 0L;
   }

   private int resolveInt5() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (mc.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   public boolean isMiningActive() {
      return this.isEnable() && this.blockPos3 != null;
   }

   private void updateState22() {
      this.blockPos3 = null;
      if (mc.options != null) {
         mc.options.attackKey.setPressed(false);
      }

      if (mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }
   }

   private Direction computeDirection(BlockPos pos) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = var2.x - (pos.getX() + 0.5);
      double var5 = var2.y - (pos.getY() + 0.5);
      double var7 = var2.z - (pos.getZ() + 0.5);
      double var9 = Math.abs(var3);
      double var11 = Math.abs(var5);
      double var13 = Math.abs(var7);
      if (var9 >= var11 && var9 >= var13) {
         return var3 > 0.0 ? Direction.EAST : Direction.WEST;
      } else if (var11 >= var13) {
         return var5 > 0.0 ? Direction.UP : Direction.DOWN;
      } else {
         return var7 > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private void updateState23(float targetYaw, float targetPitch) {
      if (mc.player != null) {
         long var3 = System.nanoTime();
         double var5 = this.timestamp25 == 0L ? 0.05 : (var3 - this.timestamp25) / 1.0E9;
         this.timestamp25 = var3;
         var5 = Math.max(0.005, Math.min(0.1, var5));
         float var7 = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
         float var8 = MathHelper.wrapDegrees(targetPitch - mc.player.getPitch());
         this.volume5 = this.computefloat2(this.volume5, var7, 900.0F, 2600.0F, 900.0F, var5);
         this.volume6 = this.computefloat2(this.volume6, var8, 600.0F, 2000.0F, 700.0F, var5);
         float var9 = (float)(this.volume5 * var5);
         float var10 = (float)(this.volume6 * var5);
         if (var9 * var7 >= 0.0F && Math.abs(var9) > Math.abs(var7)) {
            var9 = var7;
            this.volume5 *= 0.2F;
         }

         if (var10 * var8 >= 0.0F && Math.abs(var10) > Math.abs(var8)) {
            var10 = var8;
            this.volume6 *= 0.2F;
         }

         if (BotContext.isBot()) {
            mc.player.setYaw(mc.player.getYaw() + var9);
            mc.player.setPitch(MathHelper.clamp(mc.player.getPitch() + var10, -90.0F, 90.0F));
         } else {
            float var11 = Math.max(0.05F, Math.abs(var9));
            float var12 = Math.max(0.05F, Math.abs(var10));
            RotationStorage.update(new Rotation(targetYaw, targetPitch), var11, var12, var11, var12, 0, 1, false);
         }
      }
   }

   private float computefloat2(float vel, float error, float maxSpeed, float accel, float brake, double dt) {
      float var8 = Math.abs(error);
      float var9 = 1.0F + MathHelper.clamp((var8 - 25.0F) / 60.0F, 0.0F, 1.2F);
      float var10;
      if (var8 < 0.001F) {
         var10 = 0.0F;
      } else {
         var10 = (float)Math.copySign(Math.min(maxSpeed, Math.sqrt(2.0 * brake * var9 * var8)), error);
      }

      float var11 = var10 - vel;
      float var12 = (float)(accel * var9 * dt);
      if (var11 > var12) {
         var11 = var12;
      } else if (var11 < -var12) {
         var11 = -var12;
      }

      return vel + var11;
   }

   private void updateState24(BlockPos pos) {
      if (mc.player != null) {
         Vec3d var2 = mc.player.getEyePos();
         double var3 = pos.getX() + 0.5 - var2.x;
         double var5 = pos.getY() + 0.5 - var2.y;
         double var7 = pos.getZ() + 0.5 - var2.z;
         double var9 = Math.sqrt(var3 * var3 + var7 * var7);
         float var11 = (float)(Math.toDegrees(Math.atan2(var7, var3)) - 90.0);
         float var12 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var5, var9)), -89.0, 89.0);
         this.updateState23(var11, var12);
      }
   }

   private void updateState25(BlockPos pos) {
      if (mc.player != null) {
         double var2 = pos.getX() + 0.5 - mc.player.getX();
         double var4 = pos.getZ() + 0.5 - mc.player.getZ();
         float var6 = (float)(Math.toDegrees(Math.atan2(var4, var2)) - 90.0);
         float var7 = 18.0F;
         this.updateState23(var6, var7);
      }
   }

   private double resolveDouble2(BlockPos pos) {
      double var2 = pos.getX() + 0.5 - mc.player.getX();
      double var4 = pos.getZ() + 0.5 - mc.player.getZ();
      return Math.sqrt(var2 * var2 + var4 * var4);
   }

   private boolean checkCondition8(long now) {
      if (this.checkState6()) {
         this.updateState31(now);
         return false;
      }

      if (!this.checkCondition21(now)) {
         return false;
      }

      for (int var3 = 0; var3 < 9; var3++) {
         if (this.checkCondition18(mc.player.getInventory().getStack(var3))) {
            mc.player.getInventory().selectedSlot = var3;
            return true;
         }
      }

      int var4 = this.resolveInt9();
      if (var4 != -1 && now - this.timestamp10 >= 350L) {
         this.updateState33(mc.player.playerScreenHandler.syncId, var4, 8, SlotActionType.SWAP);
         this.timestamp10 = now;
      }

      return false;
   }

   private void updateState26(long now) {
      if (mc.player != null) {
         if (this.timestamp32 == 0L) {
            this.timestamp32 = now;
         } else if (now - this.timestamp32 >= 5000L) {
            int var3 = mc.player.getInventory().selectedSlot;
            if (!this.checkCondition18(mc.player.getInventory().getStack(var3))) {
               this.timestamp32 = now;
            } else {
               int var4;
               do {
                  var4 = this.random.nextInt(9);
               } while (var4 == var3);

               mc.player.getInventory().selectedSlot = var4;
               this.timestamp32 = now;
            }
         }
      }
   }

   private void updateState27(long now) {
      if (this.timestamp39 == 0L) {
         this.timestamp39 = now;
      }

      if (now - this.timestamp39 > 60000L) {
         this.updateState5("§cТаймаут сдачи — не залипаем, уходим дальше");
         this.updateState34();
         this.flag7 = false;
         this.flag8 = false;
         this.timestamp39 = 0L;
         this.timestamp43 = 0L;
         this.timestamp44 = 0L;
         this.timestamp10 = now;
         if (this.flag9) {
            this.flag9 = false;
            this.updateState4(AutoMine.State.REPAIR_MEND);
         } else {
            this.updateState4(AutoMine.State.SWITCH_LIGHT);
         }
      } else {
         if (!this.flag8) {
            if (this.checkCondition17(now)) {
               return;
            }

            this.flag8 = true;
         }

         this.flag7 = true;
         this.timestamp43 = 0L;
         if (this.flag7 && this.flag8) {
            this.flag7 = false;
            this.flag8 = false;
            this.timestamp39 = 0L;
            this.timestamp44 = 0L;
            this.index5++;
            if (this.flag9) {
               this.flag9 = false;
               this.updateState4(AutoMine.State.REPAIR_MEND);
            } else if (this.flag4) {
               this.index5 = 0;
               this.updateState4(AutoMine.State.SWITCH_LIGHT);
            } else {
               this.updateState4(AutoMine.State.MINE);
            }
         }
      }
   }

   private boolean checkCondition9(GenericContainerScreen gcs) {
      return gcs.getTitle().getString().toLowerCase(Locale.ROOT).contains("продаж");
   }

   private void updateState28(long now) {
      if (mc.player != null && mc.world != null) {
         if (now - this.timestamp7 >= 1000L) {
            this.timestamp7 = now;
            Box var3 = mc.player.getBoundingBox().expand(24.0);
            long var4 = -1L;
            String var6 = null;
            String var7 = null;

            for (Entity var9 : mc.world.getOtherEntities(mc.player, var3)) {
               Text var10 = var9.getCustomName();
               if (var10 != null) {
                  String var11 = var10.getString();
                  if (!var11.isBlank()) {
                     if (var4 < 0L) {
                        Matcher var12 = PATTERN.matcher(var11);
                        if (var11.toLowerCase(Locale.ROOT).contains("осталось") && var12.find()) {
                           long var13 = var12.group(1) == null ? 0L : Long.parseLong(var12.group(1));
                           long var15 = var12.group(2) == null ? 0L : Long.parseLong(var12.group(2));
                           if (var13 > 0L || var15 > 0L) {
                              var4 = (var13 * 60L + var15) * 1000L;
                           }
                        }
                     }

                     Matcher var17 = PATTERN2.matcher(var11);
                     if (var6 == null && var17.find()) {
                        var6 = var17.group(1).trim();
                     }

                     Matcher var18 = PATTERN3.matcher(var11);
                     if (var7 == null && var18.find()) {
                        var7 = var18.group(1).trim();
                     }
                  }
               }
            }

            if (var4 >= 0L) {
               this.timestamp6 = now + var4;
            }

            if (var6 != null) {
               this.text2 = var6;
            }

            if (var7 != null) {
               this.text3 = var7;
            }

            if (var4 >= 0L || var6 != null || var7 != null) {
               this.updateState5(
                  "§7Шахта: §f"
                     + (this.text2 == null ? "?" : this.text2)
                     + " §7→ §f"
                     + (this.text3 == null ? "?" : this.text3)
                     + " §7через §f"
                     + resolveString2(this.shaftLeftMs())
               );
            }
         }
      }
   }

   public long shaftLeftMs() {
      return this.timestamp6 <= 0L ? 0L : Math.max(0L, this.timestamp6 - System.currentTimeMillis());
   }

   public long shaftLeftTicks() {
      return this.shaftLeftMs() / 50L;
   }

   public String shaftCurrentName() {
      return this.text2;
   }

   public String shaftNextName() {
      return this.text3;
   }

   private static String resolveString2(long ms) {
      long var2 = ms / 1000L;
      return var2 / 60L + " мин. " + var2 % 60L + " сек.";
   }

   private boolean checkState5() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private boolean checkCondition10(ItemStack stack) {
      String var2 = stack.getName().getString().toLowerCase(Locale.ROOT);
      String var3 = Registries.ITEM.getId(stack.getItem()).getPath();
      if (!this.checkCondition18(stack) && !this.checkCondition13(stack) && stack.getItem() != Items.EXPERIENCE_BOTTLE && stack.getItem() != Items.COMPASS) {
         String var4 = "крюк,опыт";
         if (var4 != null) {
            for (String var8 : var4.split(",")) {
               String var9 = var8.trim().toLowerCase(Locale.ROOT);
               if (!var9.isEmpty() && (var2.contains(var9) || var3.contains(var9))) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean checkCondition11(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         for (Item var5 : value4) {
            if (stack.getItem() == var5) {
               return true;
            }
         }

         String var6 = Registries.ITEM.getId(stack.getItem()).getPath();
         if (var6.startsWith("raw_")) {
            return true;
         }

         String var7 = stack.getName().getString().toLowerCase(Locale.ROOT).replace('ё', 'е');
         return var7.contains("dust") || var7.contains("пыль") || var7.contains("lazuli") || var7.contains("лазурит");
      } else {
         return false;
      }
   }

   private boolean checkCondition12(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String var2 = Registries.ITEM.getId(stack.getItem()).getPath();
         return var2.endsWith("_pickaxe") || var2.endsWith("_axe") || var2.endsWith("_shovel") || var2.endsWith("_hoe") || var2.endsWith("_sword");
      } else {
         return false;
      }
   }

   private boolean checkCondition13(ItemStack stack) {
      if (!this.checkCondition12(stack)) {
         return false;
      }

      String var2 = Registries.ITEM.getId(stack.getItem()).getPath();
      return var2.startsWith("diamond_") || var2.startsWith("netherite_");
   }

   private boolean checkCondition14(ItemStack stack) {
      if (stack.isEmpty()) {
         return true;
      } else if (this.checkCondition25(stack)) {
         return true;
      } else if (this.checkCondition12(stack)) {
         return this.checkCondition13(stack);
      } else if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
         return true;
      } else if (stack.getItem() == Items.GOLD_NUGGET) {
         return false;
      } else if (this.checkCondition15(stack)) {
         return true;
      } else {
         return this.checkCondition20(stack) ? true : this.checkCondition11(stack);
      }
   }

   private boolean checkCondition15(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String var2 = Registries.ITEM.getId(stack.getItem()).getPath();

         for (String var6 : STRING) {
            if (var2.equals(var6)) {
               return true;
            }
         }

         StringBuilder var10 = new StringBuilder(stack.getName().getString());
         LoreComponent var11 = stack.get(DataComponentTypes.LORE);
         if (var11 != null) {
            for (Text var14 : var11.lines()) {
               var10.append(' ').append(var14.getString());
            }
         }

         String var13 = var10.toString().toLowerCase(Locale.ROOT).replace('ё', 'е');

         for (String var9 : value) {
            if (var13.contains(var9)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void updateState29(long now) {
      if (now - this.timestamp45 >= 50L) {
         boolean var3 = false;

         for (int var4 = 0; var4 < 36; var4++) {
            ItemStack var5 = mc.player.getInventory().getStack(var4);
            if (!var5.isEmpty() && !this.checkCondition14(var5)) {
               this.updateState33(mc.player.playerScreenHandler.syncId, this.resolveInt13(var4), 1, SlotActionType.THROW);
               var3 = true;
            }
         }

         ItemStack var6 = mc.player.getOffHandStack();
         if (!var6.isEmpty() && !this.checkCondition14(var6)) {
            this.updateState33(mc.player.playerScreenHandler.syncId, 45, 1, SlotActionType.THROW);
            var3 = true;
         }

         if (var3) {
            this.timestamp45 = now;
         }

         this.updateState30(now);
      }
   }

   private void updateState30(long now) {
      if (mc.currentScreen == null) {
         if (now - this.timestamp10 >= 350L) {
            for (int var3 = 0; var3 < 9; var3++) {
               ItemStack var4 = mc.player.getInventory().getStack(var3);
               if (!var4.isEmpty() && !this.checkCondition16(var4)) {
                  if (this.resolveInt6() == -1) {
                     return;
                  }

                  this.updateState33(mc.player.playerScreenHandler.syncId, this.resolveInt13(var3), 0, SlotActionType.QUICK_MOVE);
                  this.timestamp10 = now;
                  return;
               }
            }
         }
      }
   }

   private boolean checkCondition16(ItemStack s) {
      return this.checkCondition18(s)
         || this.checkCondition26(s)
         || this.checkCondition15(s)
         || this.checkCondition20(s)
         || s.getItem() == Items.COMPASS
         || s.getItem() == Items.EXPERIENCE_BOTTLE;
   }

   private int resolveInt6() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private boolean checkCondition17(long now) {
      if (now - this.timestamp10 < 40L) {
         return true;
      }

      boolean var3 = false;

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && !this.checkCondition14(var5)) {
            this.updateState33(mc.player.playerScreenHandler.syncId, this.resolveInt13(var4), 1, SlotActionType.THROW);
            var3 = true;
         }
      }

      if (var3) {
         this.timestamp10 = now;
      }

      return var3;
   }

   private int resolveInt7(Item item) {
      int var2 = 0;

      for (int var3 = 0; var3 < mc.player.getInventory().size(); var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && var4.getItem() == item) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private ItemStack computeItemStack() {
      for (int var1 = 0; var1 < mc.player.getInventory().size(); var1++) {
         ItemStack var2 = mc.player.getInventory().getStack(var1);
         if (this.checkCondition19(var2)) {
            return var2;
         }
      }

      for (int var3 = 0; var3 < mc.player.getInventory().size(); var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (this.checkCondition18(var4)) {
            return var4;
         }
      }

      return null;
   }

   private boolean checkCondition18(ItemStack stack) {
      return stack != null && !stack.isEmpty() ? Registries.ITEM.getId(stack.getItem()).getPath().contains("pickaxe") : false;
   }

   private boolean checkCondition19(ItemStack stack) {
      return stack != null && !stack.isEmpty() ? Registries.ITEM.getId(stack.getItem()).getPath().equals("netherite_pickaxe") : false;
   }

   private boolean checkState6() {
      return this.checkCondition18(mc.player.getOffHandStack());
   }

   private boolean checkCondition20(ItemStack stack) {
      return stack != null && !stack.isEmpty() ? Registries.ITEM.getId(stack.getItem()).getPath().equals("player_head") : false;
   }

   private int resolveInt8() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.checkCondition20(mc.player.getInventory().getStack(var1))) {
            return this.resolveInt13(var1);
         }
      }

      return -1;
   }

   private boolean checkCondition21(long now) {
      if (!this.flag10) {
         return true;
      }

      if (this.checkCondition20(mc.player.getOffHandStack())) {
         this.flag10 = false;
         return true;
      }

      if (this.checkState6()) {
         return false;
      }

      int var3 = this.resolveInt8();
      if (var3 == -1) {
         this.flag10 = false;
         return true;
      }

      if (now - this.timestamp10 < 350L) {
         return false;
      }

      this.updateState33(mc.player.playerScreenHandler.syncId, var3, 40, SlotActionType.SWAP);
      this.timestamp10 = now;
      return false;
   }

   private int resolveInt9() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.checkCondition18(mc.player.getInventory().getStack(var1))) {
            return this.resolveInt13(var1);
         }
      }

      return -1;
   }

   private int resolveInt10(Item item) {
      for (int var2 = 0; var2 < 9; var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && var3.getItem() == item) {
            return var2;
         }
      }

      return -1;
   }

   private int resolveInt11(Item item) {
      for (int var2 = 9; var2 < 36; var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && var3.getItem() == item) {
            return var2;
         }
      }

      return -1;
   }

   private void updateState31(long now) {
      if (this.checkState6()) {
         if (now - this.timestamp10 >= 350L) {
            int var3 = this.resolveInt12();
            this.updateState33(mc.player.playerScreenHandler.syncId, 45, var3, SlotActionType.SWAP);
            this.timestamp10 = now;
         }
      }
   }

   private int resolveInt12() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return mc.player.getInventory().selectedSlot;
   }

   private int resolveInt13(int idx) {
      return idx >= 9 ? idx : idx + 36;
   }

   private void updateState32(String command) {
      mc.getNetworkHandler().sendChatCommand(command);
   }

   private void updateState33(int syncId, int slotId, int button, SlotActionType action) {
      mc.interactionManager.clickSlot(syncId, slotId, button, action, mc.player);
   }

   private void updateState34() {
      if (mc.player != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }

      if (mc.currentScreen != null) {
         mc.setScreen(null);
      }
   }

   private boolean checkCondition22(BlockState state) {
      return !state.isOf(Blocks.STONE)
            && !state.isOf(Blocks.COBBLESTONE)
            && !state.isOf(Blocks.MOSSY_COBBLESTONE)
            && !state.isOf(Blocks.SMOOTH_STONE)
            && !state.isOf(Blocks.DEEPSLATE)
            && !state.isOf(Blocks.COBBLED_DEEPSLATE)
            && !state.isOf(Blocks.ANCIENT_DEBRIS)
         ? Registries.BLOCK.getId(state.getBlock()).getPath().endsWith("_ore")
         : true;
   }

   private boolean checkCondition23(BlockState state) {
      return state.isAir()
         || state.isOf(Blocks.OBSIDIAN)
         || state.isOf(Blocks.CRYING_OBSIDIAN)
         || state.isOf(Blocks.BEDROCK)
         || state.isOf(Blocks.BARRIER)
         || state.isOf(Blocks.COMMAND_BLOCK)
         || state.isOf(Blocks.CHAIN_COMMAND_BLOCK)
         || state.isOf(Blocks.REPEATING_COMMAND_BLOCK)
         || state.isOf(Blocks.STRUCTURE_BLOCK)
         || state.isOf(Blocks.JIGSAW)
         || state.isOf(Blocks.END_PORTAL_FRAME)
         || state.isOf(Blocks.END_PORTAL)
         || state.isOf(Blocks.NETHER_PORTAL)
         || state.isOf(Blocks.END_GATEWAY)
         || state.getHardness(mc.world, BlockPos.ORIGIN) < 0.0F;
   }

   private boolean checkCondition24(BlockPos target) {
      if (mc.player != null && mc.world != null) {
         Vec3d var2 = mc.player.getEyePos();
         Vec3d var3 = Vec3d.ofCenter(target);
         BlockHitResult var4 = mc.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
         if (var4.getType() != Type.BLOCK) {
            return false;
         }

         BlockPos var5 = var4.getBlockPos();
         if (var5.equals(target)) {
            return false;
         }

         BlockState var6 = mc.world.getBlockState(var5);
         return var6.isOf(Blocks.OBSIDIAN) || var6.isOf(Blocks.CRYING_OBSIDIAN);
      } else {
         return false;
      }
   }

   private void updateState35(BlockPos pos, long now) {
      this.hashMap.put(pos.asLong(), now + 30000L);
   }

   private void updateState36() {
      if (mc.player != null) {
         this.hashSet2.clear();
         PlayerInventory var1 = mc.player.getInventory();

         for (int var2 = 0; var2 < var1.size(); var2++) {
            ItemStack var3 = var1.getStack(var2);
            if (!var3.isEmpty()) {
               this.hashSet2.add(this.resolveString3(var3));
            }
         }

         this.flag2 = true;
      }
   }

   private String resolveString3(ItemStack stack) {
      return Registries.ITEM.getId(stack.getItem()) + "|" + stack.getName().getString().toLowerCase(Locale.ROOT).replace('ё', 'е');
   }

   private boolean checkCondition25(ItemStack stack) {
      if (!this.booleanSetting3.getValue() || !this.flag2) {
         return false;
      } else {
         return stack != null && !stack.isEmpty() ? this.hashSet2.contains(this.resolveString3(stack)) : false;
      }
   }

   private boolean checkCondition26(ItemStack stack) {
      return !this.checkCondition25(stack) ? false : stack.contains(DataComponentTypes.CUSTOM_NAME) || stack.contains(DataComponentTypes.LORE);
   }

   private boolean checkState7() {
      return this.state2 == AutoMine.State.STASH_RCT
         || this.state2 == AutoMine.State.STASH_HOME
         || this.state2 == AutoMine.State.STASH_CHEST;
   }

   private void updateState37() {
      this.timestamp = 0L;
      this.timestamp2 = 0L;
      this.timestamp3 = 0L;
      this.vec3d = null;
      this.blockPos = null;
      this.index = 0;
      this.index2 = 0;
      this.index3 = -1;
      this.timestamp4 = 0L;
   }

   private boolean checkCondition27(long now) {
      if (!this.booleanSetting2.getValue()) {
         return false;
      } else if (now < this.timestamp5) {
         return false;
      } else {
         return this.textSetting.get() != null && !this.textSetting.get().isBlank()
            ? this.resolveInt15() >= Math.max(1, (int)this.floatSetting.get())
            : false;
      }
   }

   private void updateState38(long now) {
      this.updateState17();
      this.updateState22();
      if (mc.currentScreen != null) {
         this.updateState34();
      }

      this.flag6 = false;
      this.flag3 = false;
      this.blockPos2 = null;
      this.updateState37();
      this.hashSet.clear();
      this.timestamp = now;
      ChatUtils.sendMessage("§eУники набрались (§f" + this.resolveInt15() + "§e) — везём их домой");
      this.updateState4(this.resolveInt14() > 0 ? AutoMine.State.STASH_RCT : AutoMine.State.STASH_HOME);
   }

   private int resolveInt14() {
      String var1 = this.textSetting2.get();
      if (var1 == null) {
         return 0;
      }

      String var2 = var1.replaceAll("[^0-9]", "");
      if (var2.isEmpty()) {
         return 0;
      }

      try {
         int var3 = Integer.parseInt(var2);
         return var3 >= 1 && var3 <= 74 ? var3 : 0;
      } catch (NumberFormatException var4) {
         return 0;
      }
   }

   private String resolveString4() {
      String var1 = this.textSetting.get().trim();

      while (var1.startsWith("/")) {
         var1 = var1.substring(1).trim();
      }

      return var1.toLowerCase(Locale.ROOT).startsWith("home") ? var1 : "home " + var1;
   }

   private void updateState39(long now) {
      this.updateState3();
      if (now - this.timestamp > 180000L) {
         this.updateState44("§cНе доехали до анархии с домом — вернёмся к этому позже");
      } else if (mc.currentScreen != null) {
         if (now - this.timestamp10 >= 350L) {
            this.updateState34();
            this.timestamp10 = now;
         }
      } else if (this.timestamp2 == 0L) {
         int var3 = this.resolveInt14();
         if (BotContext.isBot()) {
            if (BotTasks.INSTANCE.switchAnarchy(BotContext.current().getName(), var3)) {
               this.timestamp2 = now;
            } else {
               this.updateState44("§cНе удалось начать переход на анархию уников");
            }
         } else {
            try {
               Lumen.INSTANCE.commandStorage.getDispatcher().execute("rct " + var3, Lumen.INSTANCE.commandStorage.getSource());
               this.timestamp2 = now;
            } catch (CommandSyntaxException var5) {
               this.updateState44("§cНе удалось перейти на анархию уников: " + var5.getMessage());
            }
         }
      } else {
         if (now - this.timestamp2 > 6000L) {
            this.updateState4(AutoMine.State.STASH_HOME);
         }
      }
   }

   private void updateState40(long now) {
      this.updateState3();
      if (now - this.timestamp > 180000L) {
         this.updateState44("§cНе получилось попасть на /home — вернёмся к этому позже");
      } else if (mc.currentScreen != null) {
         if (now - this.timestamp10 >= 350L) {
            this.updateState34();
            this.timestamp10 = now;
         }
      } else if (this.timestamp3 == 0L) {
         this.vec3d = mc.player.getPos();
         this.updateState32(this.resolveString4());
         this.timestamp3 = now;
      } else {
         boolean var3 = this.vec3d == null || mc.player.getPos().squaredDistanceTo(this.vec3d) > 16.0;
         if (var3 && now - this.timestamp3 > 1500L) {
            this.blockPos = null;
            this.index = 0;
            this.updateState4(AutoMine.State.STASH_CHEST);
         } else {
            if (now - this.timestamp3 > 8000L) {
               this.timestamp3 = 0L;
               if (++this.index2 > 3) {
                  this.updateState44("§cДом §f/" + this.resolveString4() + " §cне отвечает");
               }
            }
         }
      }
   }

   private void updateState41(long now) {
      this.updateState3();
      if (now - this.timestamp > 180000L) {
         this.updateState44("§cНе смогли сложить уники — вернёмся к этому позже");
      } else if (mc.currentScreen instanceof GenericContainerScreen var3 && !this.checkCondition9(var3)) {
         int var11 = this.resolveInt15();
         if (var11 != this.index3) {
            this.index3 = var11;
            this.timestamp4 = now;
         } else if (this.timestamp4 != 0L && now - this.timestamp4 > 4000L) {
            if (this.blockPos != null) {
               this.hashSet.add(this.blockPos.asLong());
            }

            this.blockPos = null;
            this.index = 0;
            this.index3 = -1;
            this.timestamp4 = 0L;
            this.updateState34();
            this.timestamp10 = now;
            this.updateState5("§7Сундук забит — ищем другой");
            return;
         }

         if (now - this.timestamp10 >= 350L) {
            GenericContainerScreenHandler var13 = var3.getScreenHandler();
            int var6 = var13.getRows() * 9;

            for (Slot var8 : var13.slots) {
               if (var8.id >= var6) {
                  ItemStack var9 = var8.getStack();
                  if (!var9.isEmpty() && this.toBooleanOrDefault(var9)) {
                     this.updateState33(var13.syncId, var8.id, 0, SlotActionType.QUICK_MOVE);
                     this.timestamp10 = now;
                     return;
                  }
               }
            }

            if (this.resolveInt15() > 0) {
               if (this.blockPos != null) {
                  this.hashSet.add(this.blockPos.asLong());
               }

               this.blockPos = null;
               this.index = 0;
               this.updateState5("§7Сундук не принял уники — ищем другой");
            }

            this.updateState34();
            this.timestamp10 = now;
            if (this.resolveInt15() <= 0) {
               this.updateState43();
            }
         }
      } else if (mc.currentScreen != null) {
         if (now - this.timestamp10 >= 350L) {
            this.updateState34();
            this.timestamp10 = now;
         }
      } else if (this.resolveInt15() <= 0) {
         this.updateState43();
      } else {
         if (this.blockPos != null && !this.checkCondition28(mc.world.getBlockState(this.blockPos))) {
            this.blockPos = null;
         }

         if (this.blockPos == null) {
            this.blockPos = this.computeBlockPos4();
            this.index = 0;
         }

         if (this.blockPos == null) {
            this.updateState17();
            if (now - this.timestamp8 > 20000L) {
               this.updateState44("§cРядом с домом нет сундука — некуда складывать уники");
            }
         } else {
            double var10 = this.resolveDouble2(this.blockPos);
            if (!(var10 > 3.0)) {
               this.volatileboolean = false;
               this.volatileboolean2 = false;
               this.volatileboolean3 = false;
               this.volatileboolean4 = false;
               this.blockPos2 = null;
               this.updateState24(this.blockPos);
               if (now - this.timestamp10 >= 350L) {
                  this.updateState42();
                  Direction var12 = this.computeDirection(this.blockPos);
                  mc.interactionManager
                     .interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(this.blockPos), var12, this.blockPos, false));
                  mc.player.swingHand(Hand.MAIN_HAND);
                  this.timestamp10 = now;
                  if (++this.index > 8) {
                     this.hashSet.add(this.blockPos.asLong());
                     this.blockPos = null;
                     this.index = 0;
                     this.updateState5("§7Сундук не открывается — берём следующий");
                  }
               }
            } else {
               this.blockPos2 = this.blockPos;
               MinePathNavigator.Output var5 = this.computeOutput(this.blockPos, 2.6, now);
               if (var5 != null && var5.hasLook && var10 > 5.0) {
                  this.updateState18(var5, now);
               } else {
                  this.updateState25(this.blockPos);
               }
            }
         }
      }
   }

   private void updateState42() {
      if (mc.player != null) {
         PlayerInventory var1 = mc.player.getInventory();

         for (int var2 = 0; var2 < 9; var2++) {
            ItemStack var3 = var1.getStack(var2);
            if (var3.isEmpty() || var3.getItem() != Items.PAPER) {
               if (var1.selectedSlot != var2) {
                  var1.selectedSlot = var2;
               }

               return;
            }
         }

         if (var1.selectedSlot != 0) {
            var1.selectedSlot = 0;
         }
      }
   }

   private void updateState43() {
      this.updateState17();
      this.updateState37();
      ChatUtils.sendMessage("§aУники сложены — возвращаемся копать");
      this.updateState4(AutoMine.State.SWITCH_LIGHT);
   }

   private void updateState44(String msg) {
      this.updateState17();
      if (mc.currentScreen != null) {
         this.updateState34();
      }

      this.updateState37();
      this.timestamp5 = System.currentTimeMillis() + 300000L;
      ChatUtils.sendMessage("[AutoMine] " + msg);
      this.updateState4(AutoMine.State.SWITCH_LIGHT);
   }

   private boolean checkCondition28(BlockState state) {
      return state.getBlock() instanceof ChestBlock || state.getBlock() instanceof BarrelBlock;
   }

   private BlockPos computeBlockPos4() {
      if (mc.player != null && mc.world != null) {
         int var1 = (int)Math.ceil(this.floatSetting2.get());
         double var2 = this.floatSetting2.get() * this.floatSetting2.get();
         BlockPos var4 = mc.player.getBlockPos();
         BlockPos var5 = null;
         double var6 = Double.MAX_VALUE;
         Mutable var8 = new Mutable();

         for (int var9 = -var1; var9 <= var1; var9++) {
            for (int var10 = -3; var10 <= 3; var10++) {
               for (int var11 = -var1; var11 <= var1; var11++) {
                  var8.set(var4.getX() + var9, var4.getY() + var10, var4.getZ() + var11);
                  if (!this.hashSet.contains(var8.asLong()) && this.checkCondition28(mc.world.getBlockState(var8))) {
                     double var12 = var8.getX() + 0.5 - mc.player.getX();
                     double var14 = var8.getY() + 0.5 - mc.player.getY();
                     double var16 = var8.getZ() + 0.5 - mc.player.getZ();
                     double var18 = var12 * var12 + var14 * var14 + var16 * var16;
                     if (!(var18 > var2) && var18 < var6) {
                        var6 = var18;
                        var5 = var8.toImmutable();
                     }
                  }
               }
            }
         }

         return var5;
      } else {
         return null;
      }
   }

   private int resolveInt15() {
      if (mc.player == null) {
         return 0;
      }

      int var1 = 0;
      PlayerInventory var2 = mc.player.getInventory();

      for (int var3 = 0; var3 < var2.size(); var3++) {
         ItemStack var4 = var2.getStack(var3);
         if (!var4.isEmpty() && this.toBooleanOrDefault(var4)) {
            var1 += var4.getCount();
         }
      }

      return var1;
   }

   private boolean toBooleanOrDefault(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         StringBuilder var2 = new StringBuilder(stack.getName().getString());
         LoreComponent var3 = stack.get(DataComponentTypes.LORE);
         if (var3 != null) {
            for (Text var5 : var3.lines()) {
               var2.append(' ').append(var5.getString());
            }
         }

         String var6 = var2.toString().toLowerCase(Locale.ROOT).replace('ё', 'е');
         return var6.contains("unique key") ? true : var6.contains("универсальн") && var6.contains("ключ");
      } else {
         return false;
      }
   }

   private void toBooleanOrDefault2(long now) {
      int var3 = this.toBooleanOrDefault3();
      if (mc.world != this.world2) {
         this.world2 = mc.world;
         this.timestamp48 = now + 6000L;
      }

      if (RCTCommand.RUNNING) {
         this.timestamp48 = now + 6000L;
      }

      boolean var4 = this.flag11 && now >= this.timestamp48 && this.state2 == AutoMine.State.MINE && mc.currentScreen == null;
      if (!this.flag11) {
         this.index13 = var3;
         this.index12 = var3;
         this.flag11 = now >= this.timestamp48;
      } else if (!var4) {
         this.index12 = var3;
      } else if (var3 > this.index12) {
         int var5 = var3 - this.index12;
         if (var5 <= 3) {
            this.hooksObtained += var5;
         }

         this.index12 = var3;
      } else if (var3 < this.index12) {
         this.index12 = var3;
      }
   }

   private int toBooleanOrDefault3() {
      int var1 = 0;
      PlayerInventory var2 = mc.player.getInventory();

      for (int var3 = 0; var3 < var2.size(); var3++) {
         ItemStack var4 = var2.getStack(var3);
         if (!var4.isEmpty() && this.toBooleanOrDefault4(var4)) {
            var1 += var4.getCount();
         }
      }

      return var1;
   }

   private boolean toBooleanOrDefault4(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String var2 = Registries.ITEM.getId(stack.getItem()).getPath();

         for (String var6 : STRING) {
            if (var2.equals(var6)) {
               return true;
            }
         }

         StringBuilder var10 = new StringBuilder(stack.getName().getString());
         LoreComponent var11 = stack.get(DataComponentTypes.LORE);
         if (var11 != null) {
            for (Text var14 : var11.lines()) {
               var10.append(' ').append(var14.getString());
            }
         }

         String var13 = var10.toString().toLowerCase(Locale.ROOT).replace('ё', 'е');

         for (String var9 : value2) {
            if (var13.contains(var9)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static void onGameMessage(String content) {
      if (INSTANCE != null && INSTANCE.isEnable() && content != null) {
         INSTANCE.updateState45(content);
      }
   }

   public void onBotMessage(String content) {
      if (content != null && this.isEnable()) {
         this.updateState45(content);
      }
   }

   public void copySettingsFrom(AutoMine source) {
      toBooleanOrDefault5(source.getSettings(), this.getSettings());
   }

   private static void toBooleanOrDefault5(List<Setting> from, List<Setting> to) {
      int var2 = Math.min(from.size(), to.size());

      for (int var3 = 0; var3 < var2; var3++) {
         Setting var4 = (Setting)from.get(var3);
         Setting var5 = (Setting)to.get(var3);
         if (var4 instanceof BooleanSetting var6 && var5 instanceof BooleanSetting var7) {
            var7.setState(var6.isState());
         } else if (var4 instanceof FloatSetting var8 && var5 instanceof FloatSetting var9) {
            var9.setValue(var8.get());
         } else if (var4 instanceof ModeSetting var10 && var5 instanceof ModeSetting var11) {
            var11.set(var10.getCurrent());
         } else if (var4 instanceof TextSetting var12 && var5 instanceof TextSetting var13) {
            var13.setText(var12.get());
         } else if (var4 instanceof ListSetting var14 && var5 instanceof ListSetting var15) {
            toBooleanOrDefault5(new ArrayList<>(var14.getSettings()), new ArrayList<>(var15.getSettings()));
         }
      }
   }

   private void updateState45(String content) {
      String var2 = content.toLowerCase(Locale.ROOT).replace('ё', 'е');
      Matcher var3 = PATTERN5.matcher(var2);
      if (var3.find()) {
         long var7 = this.resolveLong(var3.group(1));
         if (var7 > 0L) {
            this.hooksObtained2 += var7;
         }
      } else {
         Matcher var4 = PATTERN6.matcher(var2);
         if (var4.find()) {
            long var5 = this.resolveLong(var4.group(1));
            if (var5 > 0L) {
               this.hooksObtained2 -= var5;
            }
         }
      }
   }

   private long resolveLong(String raw) {
      String var2 = raw.replaceAll("[\\s\\u00A0\\u2007\\u202F]", "");
      long var3 = 0L;
      Matcher var5 = PATTERN4.matcher(var2);
      if (var5.find()) {
         String var6 = var5.group(1);
         if (var6.length() == 1) {
            var6 = var6 + "0";
         }

         try {
            var3 = Long.parseLong(var6);
         } catch (NumberFormatException var9) {
            var3 = 0L;
         }

         var2 = var2.substring(0, var5.start());
      }

      String var10 = var2.replaceAll("[^0-9]", "");
      if (var10.isEmpty()) {
         return -1L;
      }

      try {
         return Long.parseLong(var10) * 100L + var3;
      } catch (NumberFormatException var8) {
         return -1L;
      }
   }

   public String statusLine() {
      return switch (this.state2) {
         case WARP, WAIT_WARP -> "варп на шахту";
         case GO_ENTRY -> "заходит в шахту";
         case CHECK_REGION -> "оценивает шахту";
         case SWITCH_LIGHT, WAIT_RCT -> "меняет анархию";
         case CHECK_PICK -> "проверяет кирку";
         case REPAIR_MEND -> "чинит кирку";
         case MINE -> "копает";
         case DUMP -> "сдаёт лут";
         case STASH_RCT, STASH_HOME, STASH_CHEST -> "складывает уники";
      };
   }

   public int getHooksObtained() {
      return this.hooksObtained;
   }

   public int getTotalHookCount() {
      return (this.index13 < 0 ? 0 : this.index13) + this.hooksObtained;
   }

   public long getSessionElapsedMs() {
      return this.timestamp47 == 0L ? 0L : System.currentTimeMillis() - this.timestamp47;
   }

   public boolean hasProfit() {
      return this.isEnable();
   }

   public long getProfit() {
      return this.hooksObtained2;
   }

   public static void onProtectedRegionHit() {
      if (INSTANCE != null && INSTANCE.isEnable()) {
         INSTANCE.volatileboolean5 = true;
      }
   }

   private void updateState46(long now) {
      BlockPos var3 = this.blockPos3 != null ? this.blockPos3 : this.blockPos2;
      this.updateState22();
      this.blockPos2 = null;
      if (var3 != null) {
         this.updateState47(var3, 2, 60000L, now);
         this.updateState2();
         if (mc.player != null) {
            mc.player.setYaw(mc.player.getYaw() + 160.0F);
         }

         this.timestamp46 = now + 600L;
         this.updateState5("§cБлок в привате — резко отворачиваемся и пропускаем область");
      }
   }

   private void updateState47(BlockPos center, int radius, long durationMs, long now) {
      long var7 = now + durationMs;

      for (int var9 = -radius; var9 <= radius; var9++) {
         for (int var10 = -radius; var10 <= radius; var10++) {
            for (int var11 = -radius; var11 <= radius; var11++) {
               this.hashMap.put(BlockPos.asLong(center.getX() + var9, center.getY() + var10, center.getZ() + var11), var7);
            }
         }
      }
   }

   private boolean checkCondition29(int x, int y, int z) {
      long var4 = BlockPos.asLong(x, y, z);
      Long var6 = this.hashMap.get(var4);
      if (var6 == null) {
         return false;
      } else if (System.currentTimeMillis() >= var6) {
         this.hashMap.remove(var4);
         return false;
      } else {
         return true;
      }
   }

   private record Shaft(String name, String warp, int x1, int y1, int z1, int x2, int y2, int z2) {

      private Shaft(String name, String warp, int x1, int y1, int z1, int x2, int y2, int z2) {
         this.name = name;
         this.warp = warp;
         this.x1 = x1;
         this.y1 = y1;
         this.z1 = z1;
         this.x2 = x2;
         this.y2 = y2;
         this.z2 = z2;
      }

      double cx() {
         return (this.x1 + this.x2) / 2.0;
      }

      double cy() {
         return (this.y1 + this.y2) / 2.0;
      }

      double cz() {
         return (this.z1 + this.z2) / 2.0;
      }

      public String name() {
         return this.name;
      }

      public String warp() {
         return this.warp;
      }

      public int x1() {
         return this.x1;
      }

      public int y1() {
         return this.y1;
      }

      public int z1() {
         return this.z1;
      }

      public int x2() {
         return this.x2;
      }

      public int y2() {
         return this.y2;
      }

      public int z2() {
         return this.z2;
      }
   }

   private enum State {
      WARP,
      WAIT_WARP,
      GO_ENTRY,
      CHECK_REGION,
      SWITCH_LIGHT,
      WAIT_RCT,
      CHECK_PICK,
      REPAIR_MEND,
      MINE,
      DUMP,
      STASH_RCT,
      STASH_HOME,
      STASH_CHEST;
   }
}
