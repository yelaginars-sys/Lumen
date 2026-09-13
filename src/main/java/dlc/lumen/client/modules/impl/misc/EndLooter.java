package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.baritone.BaritoneControl;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.rotate.HumanBreakRotation;
import dlc.lumen.api.utils.rotate.Rotation;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.dimension.DimensionTypes;

public class EndLooter extends Module {
   public static final EndLooter INSTANCE = new EndLooter();
   private static final long TIMESTAMP = 50L;
   private static final long TIMESTAMP2 = 70L;
   private static final long TIMESTAMP3 = 5000L;
   private static final long TIMESTAMP4 = 1000L;
   private static final long TIMESTAMP5 = 900L;
   private static final long POLL_INTERVAL_MS = 6000L;
   private static final long TIMESTAMP6 = 90000L;
   private static final long TIMESTAMP7 = 25000L;
   private static final long TIMESTAMP8 = 400L;
   private static final double LEVEL = 3.6;
   private static final double LEVEL2 = 3.5;
   private static final int INDEX = 24;
   private static final int INDEX2 = 3;
   private static final int INDEX3 = 1;
   private static final int INDEX4 = 1;
   private static final int INDEX5 = 20;
   private final FloatSetting floatSetting = new FloatSetting("Радиус поиска", 96.0F, 16.0F, 192.0F, 8.0F);
   private final TextSetting textSetting = new TextSetting("Хранилище", "", 24);
   private final TextSetting xyzSetting = new TextSetting("Сундук (x y z)", "", 24);
   private final TextSetting xyzSetting2 = new TextSetting("Портал (x y z)", "", 24);
   private final TextSetting xyzSetting3 = new TextSetting("Точка в Энде (x y z)", "2 64 0", 24);
   private final BooleanSetting booleanSetting = new BooleanSetting("Отладка в чат", true);
   private final HumanBreakRotation humanBreakRotation = new HumanBreakRotation();
   private EndLooter.Stage stage = EndLooter.Stage.PICK_CHEST;
   private long timestamp;
   private long timestamp2;
   private long timestamp3 = 50L;
   private long timestamp4;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private long timestamp5;
   private long timestamp6;
   private BlockPos blockPos4;
   private double level = Double.MAX_VALUE;
   private long timestamp7;
   private long timestamp8;
   private int index = Integer.MIN_VALUE;
   private BlockPos blockPos5;
   private BlockPos blockPos6;
   private BlockPos blockPos7;
   private final Set<Long> longs = new HashSet<>();
   private BlockPos blockPos8;
   private BlockPos blockPos9;
   private BlockPos blockPos10;
   private final Set<Long> longs2 = new HashSet<>();
   private BlockPos blockPos11;
   private BlockPos blockPos12;
   private boolean flag;
   private long timestamp9;
   private volatile BlockPos blockPos13;
   private int index2;
   private final Set<Integer> integers = new HashSet<>();
   private int index3 = -1;
   private int index4 = -1;
   private int index5;
   private int index6 = -1;
   private boolean flag2;
   private long timestamp10;
   private long timestamp11;
   private int index7;
   private Vec3d vec3d;
   private Vec3d vec3d2;

   public EndLooter() {
      super("EndLooter", "Забирает расходники, уходит в Энд и бьёт горшки", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting, this.textSetting, this.xyzSetting, this.xyzSetting2, this.xyzSetting3, this.booleanSetting);
   }

   @Override
   public void onEnable() {
      this.timestamp = System.currentTimeMillis();
      this.blockPos2 = this.helper46(this.xyzSetting2.getText());
      this.blockPos3 = null;
      this.helper40();
      this.blockPos11 = null;
      this.index = Integer.MIN_VALUE;
      this.blockPos5 = null;
      this.blockPos6 = null;
      this.integers.clear();
      this.humanBreakRotation.reset();
      this.helper56();
      this.blockPos = this.helper46(this.xyzSetting.getText());
      this.timestamp11 = 0L;
      this.index7 = 0;
      this.vec3d = null;
      this.vec3d2 = null;
      String var1 = this.helper3();
      if (!var1.isEmpty()) {
         this.stage = EndLooter.Stage.TP_HOME;
         this.helper64("Сначала в хранилище: /home " + var1);
      } else if (this.blockPos != null) {
         this.stage = EndLooter.Stage.GO_CHEST;
         this.helper64("Помню сундук: " + this.blockPos.getX() + " " + this.blockPos.getY() + " " + this.blockPos.getZ());
      } else {
         this.stage = EndLooter.Stage.PICK_CHEST;
         this.helper64("Ткни ПКМ по сундуку с расходниками — я его запомню");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      BaritoneControl.stop();
      this.blockPos3 = null;
      this.helper57();
      this.helper44();
      this.helper21();
      this.helper35();
      this.helper18();
      this.helper13();
      this.humanBreakRotation.reset();
      this.blockPos = null;
      this.blockPos2 = null;
      this.blockPos6 = null;
      this.blockPos7 = null;
      this.blockPos9 = null;
      this.blockPos10 = null;
      this.blockPos12 = null;
      super.onDisable();
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.SEND) {
         if (event.getPacket() instanceof PlayerInteractBlockC2SPacket var2) {
            if (this.stage == EndLooter.Stage.PICK_CHEST) {
               this.blockPos13 = var2.getBlockHitResult().getBlockPos().toImmutable();
            }
         }
      }
   }

   private void helper() {
      BlockPos var1 = this.blockPos13;
      if (var1 != null) {
         this.blockPos13 = null;
         if (this.stage == EndLooter.Stage.PICK_CHEST) {
            if (!this.helper55(mc.world.getBlockState(var1))) {
               this.helper64("Это не сундук/бочка/шалкер — ткни в контейнер с расходниками");
            } else {
               this.blockPos = var1;
               this.xyzSetting.setText(var1.getX() + " " + var1.getY() + " " + var1.getZ());
               this.blockPos11 = null;
               this.helper40();
               this.integers.clear();
               this.humanBreakRotation.reset();
               this.helper63(EndLooter.Stage.GO_CHEST);
               this.helper64("Сундук запомнен: " + var1.getX() + " " + var1.getY() + " " + var1.getZ());
            }
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null && mc.getNetworkHandler() != null) {
         long var2 = System.currentTimeMillis();
         if (!this.helper45()) {
            this.helper();
            if (this.helper12(var2)) {
               this.helper35();
            } else {
               this.helper34();
               switch (this.stage) {
                  case TP_HOME:
                     this.helper2(var2);
                  case PICK_CHEST:
                  case DONE:
                  default:
                     break;
                  case GO_CHEST:
                     this.helper5(var2);
                     break;
                  case OPEN_CHEST:
                     this.helper6(var2);
                     break;
                  case LOOT:
                     this.helper7(var2);
                     break;
                  case USE_ITEMS:
                     this.helper10(var2);
                     break;
                  case GO_PORTAL:
                     this.helper19(var2);
                     break;
                  case ENTER_PORTAL:
                     this.helper20(var2);
                     break;
                  case GO_END_POINT:
                     this.helper22(var2);
                     break;
                  case GO_POT:
                     this.helper23(var2);
                     break;
                  case BREAK_POT:
                     this.helper24(var2);
                     break;
                  case COLLECT_DROPS:
                     this.helper25(var2);
                     break;
                  case GO_SUS:
                     this.helper26(var2);
                     break;
                  case BRUSH_SUS:
                     this.helper27(var2);
               }
            }
         }
      }
   }

   private void helper2(long now) {
      this.helper58();
      this.helper21();
      String var3 = this.helper3();
      if (var3.isEmpty()) {
         this.helper63(this.helper4());
      } else {
         Vec3d var4 = mc.player.getPos();
         boolean var5 = this.timestamp11 != 0L
            && (
               this.vec3d2 != null && var4.squaredDistanceTo(this.vec3d2) > 64.0
                  || this.vec3d != null && var4.squaredDistanceTo(this.vec3d) > 400.0
            );
         this.vec3d2 = var4;
         if (var5) {
            this.helper64("Телепортировался — работаю");
            this.helper63(this.helper4());
         } else if (this.timestamp11 == 0L) {
            this.vec3d = var4;
            this.index7++;
            mc.getNetworkHandler().sendChatCommand("home " + var3);
            this.timestamp11 = now;
            this.helper64("Телепорт: /home " + var3 + (this.index7 > 1 ? " (попытка " + this.index7 + ")" : ""));
         } else {
            if (now - this.timestamp11 > 12000L) {
               if (this.index7 >= 3) {
                  this.helper64("Телепорт не сработал — работаю отсюда");
                  this.helper63(this.helper4());
                  return;
               }

               this.timestamp11 = 0L;
            }
         }
      }
   }

   private String helper3() {
      String var1 = this.textSetting.getText();
      return var1 == null ? "" : var1.trim();
   }

   private EndLooter.Stage helper4() {
      return this.blockPos != null ? EndLooter.Stage.GO_CHEST : EndLooter.Stage.PICK_CHEST;
   }

   private void helper5(long now) {
      if (this.blockPos == null) {
         this.helper63(EndLooter.Stage.PICK_CHEST);
      } else if (!this.helper55(mc.world.getBlockState(this.blockPos))) {
         this.helper64("На отмеченном месте больше нет сундука");
         this.blockPos = null;
         this.helper63(EndLooter.Stage.PICK_CHEST);
      } else if (this.helper61(this.blockPos) <= 3.6) {
         this.helper58();
         this.helper63(EndLooter.Stage.OPEN_CHEST);
      } else if (now - this.timestamp9 > 90000L) {
         this.helper64("Не могу дойти до сундука — иду к порталу");
         this.helper58();
         this.helper40();
         this.helper63(EndLooter.Stage.GO_PORTAL);
      } else {
         BlockPos var3 = this.blockPos11 != null ? this.blockPos11 : (this.blockPos11 = this.helper36(this.blockPos));
         if (var3 == null) {
            this.helper38(this.blockPos, 2.25, false, now);
         } else {
            EndLooter.Nav var4 = this.helper38(var3, 2.25, true, now);
            if (var4 == EndLooter.Nav.ARRIVED) {
               this.helper58();
               this.helper63(EndLooter.Stage.OPEN_CHEST);
            } else if (var4 == EndLooter.Nav.STUCK) {
               this.helper40();
            }
         }
      }
   }

   private void helper6(long now) {
      if (this.blockPos == null) {
         this.helper63(EndLooter.Stage.PICK_CHEST);
      } else {
         ScreenHandler var3 = mc.player.currentScreenHandler;
         if (var3 != null && var3 != mc.player.playerScreenHandler) {
            this.helper63(EndLooter.Stage.LOOT);
         } else if (this.helper61(this.blockPos) > 4.8) {
            this.helper63(EndLooter.Stage.GO_CHEST);
         } else if (now - this.timestamp > 12000L) {
            this.helper64("Не удалось открыть сундук, иду к порталу");
            this.helper63(EndLooter.Stage.GO_PORTAL);
         } else {
            this.helper58();
            Direction var4 = this.helper62(this.blockPos);
            this.humanBreakRotation.aimAt(this.blockPos, var4);
            if (this.humanBreakRotation.isOnTarget()) {
               if (now - this.timestamp4 >= 900L) {
                  this.timestamp4 = now;
                  Vec3d var5 = Vec3d.ofCenter(this.blockPos).add(Vec3d.of(var4.getVector()).multiply(0.5));
                  mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var5, var4, this.blockPos, false));
                  mc.player.swingHand(Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   private void helper7(long now) {
      ScreenHandler var3 = mc.player.currentScreenHandler;
      if (var3 != null && var3 != mc.player.playerScreenHandler) {
         if (now - this.timestamp > 25000L) {
            this.helper64("Лут затянулся — пью что взял");
            this.helper60();
            this.helper63(EndLooter.Stage.USE_ITEMS);
         } else if (now - this.timestamp2 >= this.timestamp3) {
            if (!this.helper8(var3, now)) {
               if (this.index3 >= 0) {
                  if (this.helper50(this::helper9) <= this.index4) {
                     this.integers.add(this.index3);
                  }

                  this.index3 = -1;
               }

               Slot var4 = null;
               boolean var5 = false;
               if (this.helper50(this::helper51) < 1) {
                  var4 = this.helper49(var3, this::helper51);
                  var5 = true;
               }

               if (var4 == null && this.helper50(this::helper52) < 1) {
                  var4 = this.helper49(var3, this::helper52);
                  var5 = true;
               }

               if (var4 == null) {
                  var5 = false;
                  if (this.helper50(this::helper54) < 20) {
                     var4 = this.helper49(var3, this::helper54);
                  }
               }

               if (var4 == null) {
                  this.helper64(
                     "Набрал: инвиз "
                        + this.helper50(this::helper51)
                        + ", скорость "
                        + this.helper50(this::helper52)
                        + ", еда "
                        + this.helper50(this::helper54)
                  );
                  this.helper60();
                  this.helper63(EndLooter.Stage.USE_ITEMS);
               } else if (var5 && var4.getStack().getCount() > 1) {
                  this.index5 = 1;
                  this.index6 = var4.id;
                  this.index3 = -1;
                  mc.interactionManager.clickSlot(var3.syncId, var4.id, 0, SlotActionType.PICKUP, mc.player);
                  this.helper59(now);
               } else {
                  this.index3 = var4.id;
                  this.index4 = this.helper50(this::helper9);
                  mc.interactionManager.clickSlot(var3.syncId, var4.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                  this.helper59(now);
               }
            }
         }
      } else {
         if (now - this.timestamp > 2500L) {
            this.helper63(EndLooter.Stage.USE_ITEMS);
         }
      }
   }

   private boolean helper8(ScreenHandler handler, long now) {
      if (this.index5 == 0) {
         return false;
      }

      if (this.index5 != 1) {
         mc.interactionManager.clickSlot(handler.syncId, this.index6, 0, SlotActionType.PICKUP, mc.player);
         this.index5 = 0;
         this.helper59(now);
         return true;
      }

      Slot var4 = null;

      for (Slot var6 : handler.slots) {
         if (var6.inventory == mc.player.getInventory() && !var6.hasStack()) {
            var4 = var6;
            break;
         }
      }

      if (var4 == null) {
         mc.interactionManager.clickSlot(handler.syncId, this.index6, 0, SlotActionType.PICKUP, mc.player);
         this.integers.add(this.index6);
         this.index5 = 0;
         this.helper59(now);
         return true;
      } else {
         mc.interactionManager.clickSlot(handler.syncId, var4.id, 1, SlotActionType.PICKUP, mc.player);
         this.index5 = 2;
         this.helper59(now);
         return true;
      }
   }

   private boolean helper9(ItemStack stack) {
      return this.helper51(stack) || this.helper52(stack) || this.helper54(stack);
   }

   private void helper10(long now) {
      this.helper58();
      if (now - this.timestamp > 20000L) {
         this.helper18();
         this.helper64("Не всё выпил — иду так");
         this.helper63(EndLooter.Stage.GO_PORTAL);
      } else if (mc.player.hasStatusEffect(StatusEffects.INVISIBILITY) || !this.helper11(this::helper51, now)) {
         if (mc.player.hasStatusEffect(StatusEffects.SPEED) || !this.helper11(this::helper52, now)) {
            this.helper18();
            this.helper64("Зелья выпиты — иду к порталу");
            this.helper63(EndLooter.Stage.GO_PORTAL);
         }
      }
   }

   private boolean helper11(Predicate<ItemStack> filter, long now) {
      int var4 = this.helper14(filter);
      if (var4 == -1) {
         return false;
      } else if (var4 > 8) {
         this.helper16(var4, now);
         return true;
      } else {
         this.helper15(var4);
         this.helper17();
         return true;
      }
   }

   private boolean helper12(long now) {
      boolean var3 = this.stage == EndLooter.Stage.GO_CHEST
         || this.stage == EndLooter.Stage.GO_PORTAL
         || this.stage == EndLooter.Stage.GO_POT;
      if (!var3) {
         this.helper13();
         return false;
      }

      int var4 = mc.player.getHungerManager().getFoodLevel();
      if (!this.flag2 && var4 > 10) {
         return false;
      }

      if (this.flag2 && (var4 >= 18 || now - this.timestamp10 > 12000L) && !mc.player.isUsingItem()) {
         this.helper13();
         return false;
      }

      int var5 = this.helper14(this::helper54);
      if (var5 == -1) {
         this.helper13();
         return false;
      }

      if (!this.flag2) {
         this.flag2 = true;
         this.timestamp10 = now;
         this.helper64("Перекус");
      }

      this.helper58();
      this.helper35();
      if (var5 > 8) {
         this.helper16(var5, now);
         return true;
      } else {
         this.helper15(var5);
         this.helper17();
         return true;
      }
   }

   private void helper13() {
      if (this.flag2) {
         this.flag2 = false;
         this.helper18();
      }
   }

   private int helper14(Predicate<ItemStack> filter) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (filter.test(mc.player.getInventory().getStack(var2))) {
            return var2;
         }
      }

      for (int var3 = 9; var3 < 36; var3++) {
         if (filter.test(mc.player.getInventory().getStack(var3))) {
            return var3;
         }
      }

      return -1;
   }

   private void helper15(int slot) {
      if (slot >= 0 && slot <= 8 && mc.player.getInventory().selectedSlot != slot) {
         mc.player.getInventory().selectedSlot = slot;
         mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
      }
   }

   private void helper16(int slot, long now) {
      if (now - this.timestamp2 >= this.timestamp3) {
         mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slot, 8, SlotActionType.SWAP, mc.player);
         this.helper59(now);
      }
   }

   private void helper17() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(true);
         if (!mc.player.isUsingItem()) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         }
      }
   }

   private void helper18() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }
   }

   private void helper19(long now) {
      if (this.blockPos2 == null) {
         this.blockPos2 = this.helper46(this.xyzSetting2.getText());
      }

      if (this.blockPos2 == null) {
         this.helper47(now);
      }

      if (this.blockPos2 == null) {
         if (now - this.timestamp > 60000L) {
            this.helper64("Портал не найден — укажи координаты в настройках");
            this.helper63(EndLooter.Stage.DONE);
         }
      } else if (now - this.timestamp9 > 90000L) {
         this.helper64("Не могу дойти до портала");
         this.helper58();
         this.helper63(EndLooter.Stage.DONE);
      } else {
         EndLooter.Nav var3 = this.helper38(this.blockPos2, 12.25, false, now);
         if (var3 == EndLooter.Nav.ARRIVED) {
            this.helper58();
            this.helper64("У портала — захожу");
            this.helper40();
            this.helper63(EndLooter.Stage.ENTER_PORTAL);
         } else {
            if (var3 == EndLooter.Nav.STUCK) {
               this.helper40();
            }
         }
      }
   }

   private void helper20(long now) {
      this.helper58();
      if (mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.END_PORTAL)) {
         this.helper21();
      } else {
         BlockPos var3 = this.helper33(Blocks.END_PORTAL, this.blockPos2, 6);
         if (var3 == null) {
            if (now - this.timestamp > 8000L) {
               this.helper64("Портал не активен — рядом только рамка");
               this.helper21();
               this.helper63(EndLooter.Stage.DONE);
            }
         } else {
            double var4 = var3.getX() + 0.5 - mc.player.getX();
            double var6 = var3.getZ() + 0.5 - mc.player.getZ();
            double var8 = Math.sqrt(var4 * var4 + var6 * var6);
            this.humanBreakRotation.aimAt(var3, Direction.UP);
            float var10 = (float)(Math.toDegrees(Math.atan2(var6, var4)) - 90.0);
            float var11 = Math.abs(MathHelper.wrapDegrees(var10 - mc.player.getYaw()));
            boolean var12 = var11 < 35.0F && var8 > 0.35;
            if (mc.options != null) {
               mc.options.forwardKey.setPressed(var12);
               boolean var13 = mc.player.isOnGround() && (mc.player.horizontalCollision || var8 < 2.0) && mc.player.getY() < var3.getY() + 0.9;
               mc.options.jumpKey.setPressed(var12 && var13);
            }

            if (now - this.timestamp > 15000L) {
               this.helper64("Не могу зайти в портал");
               this.helper21();
               this.helper63(EndLooter.Stage.DONE);
            }
         }
      }
   }

   private boolean checkCondition() {
      try {
         return mc.world.getDimensionEntry().matchesKey(DimensionTypes.THE_END);
      } catch (Throwable var2) {
         return false;
      }
   }

   private void helper21() {
      if (mc.options != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
      }
   }

   private void helper22(long now) {
      if (this.blockPos8 == null) {
         this.helper63(EndLooter.Stage.GO_POT);
      } else {
         BlockPos var3 = mc.player.getBlockPos();
         boolean var4 = var3.getX() == this.blockPos8.getX()
            && var3.getZ() == this.blockPos8.getZ()
            && Math.abs(var3.getY() - this.blockPos8.getY()) <= 1;
         EndLooter.Nav var5 = this.helper38(this.blockPos8, 1.0, true, now);
         if (!var4 && var5 != EndLooter.Nav.ARRIVED) {
            if (var5 == EndLooter.Nav.STUCK || now - this.timestamp9 > 90000L) {
               this.helper64("Не дошёл до точки — ищу горшки отсюда");
               this.helper58();
               BaritoneControl.resetSettingByCommand("maxFallHeightNoWater");
               this.helper40();
               this.helper63(EndLooter.Stage.GO_POT);
            }
         } else {
            this.helper58();
            BaritoneControl.resetSettingByCommand("maxFallHeightNoWater");
            this.helper64("Встал на точку — ищу горшки");
            this.helper40();
            this.helper63(EndLooter.Stage.GO_POT);
         }
      }
   }

   private void helper23(long now) {
      if (this.blockPos6 == null || !mc.world.getBlockState(this.blockPos6).isOf(Blocks.DECORATED_POT)) {
         this.blockPos6 = null;
         this.blockPos7 = null;
         BlockPos var3 = this.helper48(now, st -> st.isOf(Blocks.DECORATED_POT), this.longs);
         if (var3 != null) {
            BlockPos var4 = this.helper36(var3);
            if (var4 == null) {
               this.longs.add(var3.asLong());
               this.helper64("К горшку не подойти — пропускаю");
            } else {
               this.blockPos6 = var3;
               this.blockPos7 = var4;
               this.helper64("Нашёл горшок: " + var3.getX() + " " + var3.getY() + " " + var3.getZ());
            }
         }
      }

      if (this.blockPos6 == null) {
         if (now - this.timestamp > 60000L) {
            this.helper64("Горшков рядом нет");
            this.helper63(EndLooter.Stage.DONE);
         }
      } else if (this.helper61(this.blockPos6) <= 3.6) {
         this.helper58();
         this.helper63(EndLooter.Stage.BREAK_POT);
      } else {
         EndLooter.Nav var5 = this.helper38(this.blockPos7, 2.25, true, now);
         if (var5 == EndLooter.Nav.ARRIVED) {
            this.helper58();
            this.helper63(EndLooter.Stage.BREAK_POT);
         } else {
            if (var5 == EndLooter.Nav.STUCK) {
               this.helper64("Не дойти до горшка — ищу другой");
               this.longs.add(this.blockPos6.asLong());
               this.blockPos6 = null;
               this.blockPos7 = null;
               this.helper58();
               this.helper40();
               this.helper63(EndLooter.Stage.GO_POT);
            }
         }
      }
   }

   private void helper24(long now) {
      if (this.blockPos6 == null) {
         this.helper63(EndLooter.Stage.GO_POT);
      } else if (!mc.world.getBlockState(this.blockPos6).isOf(Blocks.DECORATED_POT)) {
         this.helper64("Горшок разбит — собираю дроп");
         this.helper44();
         this.longs.add(this.blockPos6.asLong());
         this.blockPos12 = this.blockPos6;
         this.blockPos6 = null;
         this.blockPos7 = null;
         this.helper63(EndLooter.Stage.COLLECT_DROPS);
      } else if (now - this.timestamp <= 12000L && !(this.helper61(this.blockPos6) > 4.4)) {
         this.helper58();
         if (this.helper42()) {
            this.helper44();
         } else {
            this.humanBreakRotation.aimAt(this.blockPos6, this.helper62(this.blockPos6));
            boolean var3 = this.humanBreakRotation.isOnTarget() && this.helper43(this.blockPos6);
            if (mc.options != null) {
               mc.options.attackKey.setPressed(var3);
            }
         }
      } else {
         this.helper44();
         this.longs.add(this.blockPos6.asLong());
         this.blockPos6 = null;
         this.blockPos7 = null;
         this.helper63(EndLooter.Stage.GO_POT);
      }
   }

   private void helper25(long now) {
      this.helper58();
      ItemEntity var3 = this.helper28();
      boolean var4 = now - this.timestamp < 2500L;
      if (now - this.timestamp <= 10000L && (var3 != null || var4)) {
         if (var3 == null) {
            this.helper21();
         } else {
            this.helper29(var3.getPos());
            double var5 = var3.getX() - mc.player.getX();
            double var7 = var3.getZ() - mc.player.getZ();
            double var9 = Math.sqrt(var5 * var5 + var7 * var7);
            float var11 = (float)(Math.toDegrees(Math.atan2(var7, var5)) - 90.0);
            boolean var12 = Math.abs(MathHelper.wrapDegrees(var11 - mc.player.getYaw())) < 45.0F;
            if (mc.options != null) {
               mc.options.forwardKey.setPressed(var12 && var9 > 0.25);
               mc.options.jumpKey.setPressed(var12 && mc.player.horizontalCollision && mc.player.isOnGround());
            }
         }
      } else {
         this.helper21();
         this.blockPos12 = null;
         if (this.helper32()) {
            this.helper64("Есть кисточка — ищу песок под чистку");
            this.helper63(EndLooter.Stage.GO_SUS);
         } else {
            this.helper63(EndLooter.Stage.GO_POT);
         }
      }
   }

   private void helper26(long now) {
      if (this.blockPos9 == null || !this.helper30(mc.world.getBlockState(this.blockPos9))) {
         this.blockPos9 = null;
         this.blockPos10 = null;
         BlockPos var3 = this.helper48(now, this::helper30, this.longs2);
         if (var3 != null) {
            BlockPos var4 = this.helper36(var3);
            if (var4 == null) {
               this.longs2.add(var3.asLong());
               this.helper64("К песку не подойти — пропускаю");
            } else {
               this.blockPos9 = var3;
               this.blockPos10 = var4;
               this.helper64("Нашёл песок: " + var3.getX() + " " + var3.getY() + " " + var3.getZ());
            }
         }
      }

      if (this.blockPos9 == null) {
         if (now - this.timestamp > 20000L) {
            this.helper64("Песка под чистку рядом нет — возвращаюсь к горшкам");
            this.helper63(EndLooter.Stage.GO_POT);
         }
      } else if (this.helper61(this.blockPos9) <= 3.4) {
         this.helper58();
         this.helper63(EndLooter.Stage.BRUSH_SUS);
      } else {
         EndLooter.Nav var5 = this.helper38(this.blockPos10, 2.25, true, now);
         if (var5 == EndLooter.Nav.ARRIVED) {
            this.helper58();
            this.helper63(EndLooter.Stage.BRUSH_SUS);
         } else {
            if (var5 == EndLooter.Nav.STUCK) {
               this.helper64("Не дойти до песка — дальше горшки");
               this.longs2.add(this.blockPos9.asLong());
               this.blockPos9 = null;
               this.blockPos10 = null;
               this.helper58();
               this.helper40();
               this.helper63(EndLooter.Stage.GO_POT);
            }
         }
      }
   }

   private void helper27(long now) {
      if (this.blockPos9 == null) {
         this.helper63(EndLooter.Stage.GO_SUS);
      } else if (!this.helper30(mc.world.getBlockState(this.blockPos9))) {
         this.helper64("Дочистил — собираю");
         this.helper18();
         this.longs2.add(this.blockPos9.asLong());
         this.blockPos12 = this.blockPos9;
         this.blockPos9 = null;
         this.blockPos10 = null;
         this.helper63(EndLooter.Stage.COLLECT_DROPS);
      } else if (now - this.timestamp <= 25000L && !(this.helper61(this.blockPos9) > 4.4)) {
         this.helper58();
         if (this.helper42()) {
            this.helper18();
         } else {
            int var3 = this.helper14(this::helper31);
            if (var3 == -1) {
               this.helper64("Кисточка пропала");
               this.helper18();
               this.helper63(EndLooter.Stage.GO_POT);
            } else if (var3 > 8) {
               this.helper16(var3, now);
            } else {
               this.helper15(var3);
               this.humanBreakRotation.aimAt(this.blockPos9, this.helper62(this.blockPos9));
               boolean var4 = this.humanBreakRotation.isOnTarget() && this.helper43(this.blockPos9);
               if (mc.options != null) {
                  mc.options.useKey.setPressed(var4);
               }
            }
         }
      } else {
         this.helper18();
         this.longs2.add(this.blockPos9.asLong());
         this.blockPos9 = null;
         this.blockPos10 = null;
         this.helper63(EndLooter.Stage.GO_POT);
      }
   }

   private ItemEntity helper28() {
      if (this.blockPos12 == null) {
         return null;
      }

      Vec3d var1 = Vec3d.ofCenter(this.blockPos12);
      ItemEntity var2 = null;
      double var3 = Double.MAX_VALUE;

      for (Entity var6 : mc.world.getEntities()) {
         if (var6 instanceof ItemEntity var7 && var7.isAlive() && !(var7.getPos().squaredDistanceTo(var1) > 36.0)) {
            double var8 = var7.squaredDistanceTo(mc.player);
            if (!(var8 > 144.0) && var8 < var3) {
               var3 = var8;
               var2 = var7;
            }
         }
      }

      return var2;
   }

   private void helper29(Vec3d point) {
      double var2 = point.x - mc.player.getX();
      double var4 = point.z - mc.player.getZ();
      if (!(var2 * var2 + var4 * var4 < 0.01)) {
         float var6 = (float)(Math.toDegrees(Math.atan2(var4, var2)) - 90.0);
         RotationStorage.update(new Rotation(MathHelper.wrapDegrees(var6), mc.player.getPitch()), 24.0F, 18.0F, 24.0F, 18.0F, 2, 1, true);
      }
   }

   private boolean helper30(BlockState state) {
      return state.isOf(Blocks.SUSPICIOUS_SAND) || state.isOf(Blocks.SUSPICIOUS_GRAVEL);
   }

   private boolean helper31(ItemStack stack) {
      return stack != null && stack.isOf(Items.BRUSH);
   }

   private boolean helper32() {
      return this.helper14(this::helper31) != -1;
   }

   private BlockPos helper33(Block block, BlockPos around, int radius) {
      if (around == null) {
         return null;
      }

      Mutable var4 = new Mutable();
      BlockPos var5 = null;
      double var6 = Double.MAX_VALUE;

      for (int var8 = -radius; var8 <= radius; var8++) {
         for (int var9 = -radius; var9 <= radius; var9++) {
            for (int var10 = -radius; var10 <= radius; var10++) {
               var4.set(around.getX() + var8, around.getY() + var9, around.getZ() + var10);
               if (mc.world.getBlockState(var4).isOf(block)) {
                  double var11 = mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(var4));
                  if (var11 < var6) {
                     var6 = var11;
                     var5 = var4.toImmutable();
                  }
               }
            }
         }
      }

      return var5;
   }

   private void updateState() {
      if (mc.options != null) {
         mc.options.jumpKey.setPressed(false);
      }
   }

   private void helper34() {
      if (mc.options != null) {
         boolean var1 = (
               this.stage == EndLooter.Stage.GO_CHEST
                  || this.stage == EndLooter.Stage.GO_PORTAL
                  || this.stage == EndLooter.Stage.GO_END_POINT
                  || this.stage == EndLooter.Stage.GO_POT
                  || this.stage == EndLooter.Stage.GO_SUS
            )
            && !mc.player.isTouchingWater()
            && mc.player.getHungerManager().getFoodLevel() > 6;
         mc.options.sprintKey.setPressed(var1);
      }
   }

   private void helper35() {
      if (mc.options != null) {
         mc.options.sprintKey.setPressed(false);
      }
   }

   private BlockPos helper36(BlockPos around) {
      Mutable var2 = new Mutable();
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;

      for (int var6 = -2; var6 <= 2; var6++) {
         for (int var7 = -2; var7 <= 2; var7++) {
            if (var6 != 0 || var7 != 0) {
               for (int var8 = -2; var8 <= 1; var8++) {
                  var2.set(around.getX() + var6, around.getY() + var8, around.getZ() + var7);
                  if (this.helper37(var2)) {
                     double var9 = var2.getX() + 0.5;
                     double var11 = var2.getY() + 1.62;
                     double var13 = var2.getZ() + 0.5;
                     double var15 = Vec3d.ofCenter(around).squaredDistanceTo(var9, var11, var13);
                     if (!(var15 > 14.44)) {
                        double var17 = mc.player.getPos().squaredDistanceTo(var9, var2.getY(), var13);
                        if (var17 < var4) {
                           var4 = var17;
                           var3 = var2.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }

      return var3;
   }

   private boolean helper37(BlockPos feet) {
      return mc.world.getBlockState(feet).getCollisionShape(mc.world, feet).isEmpty()
         && mc.world.getBlockState(feet.up()).getCollisionShape(mc.world, feet.up()).isEmpty()
         && !mc.world.getBlockState(feet.down()).getCollisionShape(mc.world, feet.down()).isEmpty();
   }

   private EndLooter.Nav helper38(BlockPos target, double arriveSq, boolean exact, long now) {
      if (target == null) {
         return EndLooter.Nav.STUCK;
      }

      Vec3d var7 = mc.player.getPos();
      double var8 = exact
         ? mc.player.getBlockPos().getSquaredDistance(target)
         : this.helper41(var7.x, var7.z, target.getX() + 0.5, target.getZ() + 0.5);
      if (var8 <= arriveSq) {
         return EndLooter.Nav.ARRIVED;
      }

      if (!target.equals(this.blockPos4)) {
         this.blockPos4 = target;
         this.level = var8;
         this.timestamp7 = now;
         this.helper39(target, exact, now);
         return EndLooter.Nav.MOVING;
      }

      if (var8 < this.level - 0.4) {
         this.level = var8;
         this.timestamp7 = now;
      }

      if (now - this.timestamp7 > 6000L) {
         return EndLooter.Nav.STUCK;
      }

      if (now - this.timestamp5 >= 5000L) {
         this.helper39(target, exact, now);
      }

      return EndLooter.Nav.MOVING;
   }

   private void helper39(BlockPos target, boolean exact, long now) {
      if (exact) {
         BaritoneControl.gotoPos(target);
      } else {
         BaritoneControl.gotoXZ(target.getX(), target.getZ());
      }

      this.blockPos3 = target;
      this.timestamp5 = now;
   }

   private void helper40() {
      this.blockPos4 = null;
      this.level = Double.MAX_VALUE;
      this.timestamp7 = 0L;
   }

   private double helper41(double ax, double az, double bx, double bz) {
      double var9 = ax - bx;
      double var11 = az - bz;
      return var9 * var9 + var11 * var11;
   }

   private boolean helper42() {
      Vec3d var1 = mc.player.getVelocity();
      return var1.x * var1.x + var1.z * var1.z > 0.0025;
   }

   private boolean helper43(BlockPos pos) {
      return mc.crosshairTarget instanceof BlockHitResult var2 && var2.getType() == Type.BLOCK && var2.getBlockPos().equals(pos);
   }

   private void helper44() {
      if (mc.options != null) {
         mc.options.attackKey.setPressed(false);
      }

      if (mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }
   }

   private boolean helper45() {
      int var1 = System.identityHashCode(mc.world);
      if (this.index2 == 0) {
         this.index2 = var1;
         return false;
      }

      if (this.index2 == var1) {
         return false;
      }

      this.index2 = var1;
      this.helper40();
      this.blockPos11 = null;
      this.blockPos5 = null;
      this.index = Integer.MIN_VALUE;
      this.integers.clear();
      this.blockPos6 = null;
      this.blockPos7 = null;
      this.longs.clear();
      this.blockPos9 = null;
      this.blockPos10 = null;
      this.longs2.clear();
      this.blockPos12 = null;
      this.helper21();
      this.helper35();
      this.helper13();
      this.helper18();
      this.helper44();
      this.humanBreakRotation.reset();
      if (!this.checkCondition() && this.stage != EndLooter.Stage.ENTER_PORTAL) {
         this.blockPos = this.helper46(this.xyzSetting.getText());
         this.blockPos2 = this.helper46(this.xyzSetting2.getText());
         this.helper63(this.blockPos != null ? EndLooter.Stage.GO_CHEST : EndLooter.Stage.PICK_CHEST);
         this.helper64("Сменился мир — начинаю заново");
         return true;
      }

      this.blockPos2 = null;
      this.blockPos8 = this.helper46(this.xyzSetting3.getText());
      if (this.blockPos8 != null) {
         BaritoneControl.setSettingByCommand("maxFallHeightNoWater", 25);
         this.helper63(EndLooter.Stage.GO_END_POINT);
         this.helper64(
            "Я в Энде — иду на точку " + this.blockPos8.getX() + " " + this.blockPos8.getY() + " " + this.blockPos8.getZ()
         );
      } else {
         this.helper63(EndLooter.Stage.GO_POT);
         this.helper64("Я в Энде — ищу горшки");
      }

      return true;
   }

   private BlockPos helper46(String raw) {
      if (raw != null && !raw.isBlank()) {
         String[] var2 = raw.trim().split("[\\s,]+");
         if (var2.length < 3) {
            return null;
         }

         try {
            return new BlockPos(
               (int)Math.floor(Double.parseDouble(var2[0])), (int)Math.floor(Double.parseDouble(var2[1])), (int)Math.floor(Double.parseDouble(var2[2]))
            );
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   private void helper47(long now) {
      if (now - this.timestamp8 >= 400L) {
         this.timestamp8 = now;
         int var3 = (int)this.floatSetting.get();
         BlockPos var4 = mc.player.getBlockPos();
         if (this.index == Integer.MIN_VALUE || this.index > var3) {
            this.index = 0;
            if (this.blockPos5 != null && this.blockPos2 == null) {
               this.blockPos2 = this.blockPos5;
               this.helper64(
                  "Нашёл рамку портала: " + this.blockPos2.getX() + " " + this.blockPos2.getY() + " " + this.blockPos2.getZ()
               );
               return;
            }
         }

         Mutable var5 = new Mutable();
         int var6 = Math.min(var3, this.index + 3 - 1);

         for (int var7 = this.index; var7 <= var6; var7++) {
            for (int var8 = -var7; var8 <= var7; var8++) {
               for (int var9 = -var7; var9 <= var7; var9++) {
                  if (var7 <= 0 || Math.abs(var8) == var7 || Math.abs(var9) == var7) {
                     int var10 = var4.getX() + var8;
                     int var11 = var4.getZ() + var9;
                     if (mc.world.getChunkManager().getWorldChunk(var10 >> 4, var11 >> 4, false) != null) {
                        for (int var12 = -24; var12 <= 24; var12++) {
                           var5.set(var10, var4.getY() + var12, var11);
                           BlockState var13 = mc.world.getBlockState(var5);
                           if (var13.isOf(Blocks.END_PORTAL)) {
                              this.blockPos2 = var5.toImmutable();
                              this.helper64(
                                 "Нашёл портал: " + this.blockPos2.getX() + " " + this.blockPos2.getY() + " " + this.blockPos2.getZ()
                              );
                              return;
                           }

                           if (this.blockPos5 == null && var13.isOf(Blocks.END_PORTAL_FRAME)) {
                              this.blockPos5 = var5.toImmutable();
                           }
                        }
                     }
                  }
               }
            }
         }

         this.index = var6 + 1;
      }
   }

   private BlockPos helper48(long now, Predicate<BlockState> match, Set<Long> blacklist) {
      if (now - this.timestamp8 < 400L) {
         return null;
      }

      this.timestamp8 = now;
      int var5 = (int)this.floatSetting.get();
      BlockPos var6 = mc.player.getBlockPos();
      if (this.index == Integer.MIN_VALUE || this.index > var5) {
         this.index = 0;
      }

      Mutable var7 = new Mutable();
      int var8 = Math.min(var5, this.index + 3 - 1);

      for (int var9 = this.index; var9 <= var8; var9++) {
         for (int var10 = -var9; var10 <= var9; var10++) {
            for (int var11 = -var9; var11 <= var9; var11++) {
               if (var9 <= 0 || Math.abs(var10) == var9 || Math.abs(var11) == var9) {
                  int var12 = var6.getX() + var10;
                  int var13 = var6.getZ() + var11;
                  if (mc.world.getChunkManager().getWorldChunk(var12 >> 4, var13 >> 4, false) != null) {
                     for (int var14 = -24; var14 <= 24; var14++) {
                        var7.set(var12, var6.getY() + var14, var13);
                        if (match.test(mc.world.getBlockState(var7)) && !blacklist.contains(var7.asLong())) {
                           this.index = Integer.MIN_VALUE;
                           return var7.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }

      this.index = var8 + 1;
      return null;
   }

   private Slot helper49(ScreenHandler handler, Predicate<ItemStack> filter) {
      for (Slot var4 : handler.slots) {
         if (var4.inventory != mc.player.getInventory() && !this.integers.contains(var4.id) && var4.hasStack() && filter.test(var4.getStack())) {
            return var4;
         }
      }

      return null;
   }

   private int helper50(Predicate<ItemStack> filter) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && filter.test(var4)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private boolean helper51(ItemStack stack) {
      return this.helper53(stack, StatusEffects.INVISIBILITY, "невид", "invis");
   }

   private boolean helper52(ItemStack stack) {
      return this.helper53(stack, StatusEffects.SPEED, "скорост", "swift", "speed");
   }

   private boolean helper53(ItemStack stack, RegistryEntry<StatusEffect> effect, String... nameHints) {
      if (stack != null && !stack.isEmpty()) {
         if (!stack.isOf(Items.POTION)) {
            return false;
         }

         PotionContentsComponent var4 = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (var4 != null) {
            for (StatusEffectInstance var6 : var4.getEffects()) {
               if (var6.getEffectType().matches(effect)) {
                  return true;
               }
            }
         }

         String var10 = stack.getName().getString().toLowerCase(Locale.ROOT);

         for (String var9 : nameHints) {
            if (var10.contains(var9)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean helper54(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      }

      if (stack.isOf(Items.CHORUS_FRUIT)) {
         return false;
      }

      if (stack.getUseAction() != UseAction.EAT) {
         return false;
      }

      FoodComponent var2 = stack.get(DataComponentTypes.FOOD);
      if (var2 != null && var2.nutrition() >= 4) {
         ConsumableComponent var3 = stack.get(DataComponentTypes.CONSUMABLE);
         if (var3 != null) {
            for (ConsumeEffect var5 : var3.onConsumeEffects()) {
               if (var5 instanceof ApplyEffectsConsumeEffect var6) {
                  for (StatusEffectInstance var8 : var6.effects()) {
                     if (!var8.getEffectType().value().isBeneficial()) {
                        return false;
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean helper55(BlockState state) {
      return state.getBlock() instanceof ChestBlock
         || state.getBlock() instanceof BarrelBlock
         || state.getBlock() instanceof ShulkerBoxBlock
         || state.getBlock() instanceof EnderChestBlock;
   }

   private void helper56() {
      if (!BaritoneControl.isPresent()) {
         this.helper64("§cBaritone не найден — ходить не смогу");
      } else {
         BaritoneControl.setSettingByCommand("allowBreak", false);
         BaritoneControl.setSettingByCommand("allowPlace", false);
         BaritoneControl.setSettingByCommand("allowParkour", true);
         BaritoneControl.setSettingByCommand("freeLook", false);
         BaritoneControl.setSettingByCommand("smoothLook", true);
         BaritoneControl.setSettingByCommand("smoothLookTicks", 6);
         this.flag = true;
      }
   }

   private void helper57() {
      if (this.flag) {
         this.flag = false;
         BaritoneControl.resetSettingByCommand("freeLook");
         BaritoneControl.resetSettingByCommand("smoothLook");
         BaritoneControl.resetSettingByCommand("smoothLookTicks");
         BaritoneControl.resetSettingByCommand("allowBreak");
         BaritoneControl.resetSettingByCommand("allowPlace");
         BaritoneControl.resetSettingByCommand("allowParkour");
         BaritoneControl.resetSettingByCommand("maxFallHeightNoWater");
      }
   }

   private void helper58() {
      long var1 = System.currentTimeMillis();
      if (this.blockPos3 != null) {
         if (var1 - this.timestamp6 >= 1000L) {
            BaritoneControl.stop();
            this.timestamp6 = var1;
            this.blockPos3 = null;
         }
      }
   }

   private void helper59(long now) {
      this.timestamp2 = now;
      this.timestamp3 = 50L + ThreadLocalRandom.current().nextLong(70L);
   }

   private void helper60() {
      if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }
   }

   private double helper61(BlockPos pos) {
      return mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos));
   }

   private Direction helper62(BlockPos pos) {
      Vec3d var2 = mc.player.getEyePos().subtract(Vec3d.ofCenter(pos));
      double var3 = Math.abs(var2.x);
      double var5 = Math.abs(var2.y);
      double var7 = Math.abs(var2.z);
      if (var3 >= var5 && var3 >= var7) {
         return var2.x >= 0.0 ? Direction.EAST : Direction.WEST;
      } else if (var7 >= var3 && var7 >= var5) {
         return var2.z >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      } else {
         return var2.y >= 0.0 ? Direction.UP : Direction.DOWN;
      }
   }

   private void helper63(EndLooter.Stage next) {
      long var2 = System.currentTimeMillis();
      this.stage = next;
      this.timestamp = var2;
      this.timestamp9 = var2;
      this.blockPos3 = null;
      this.index3 = -1;
      this.index4 = -1;
      if (next != EndLooter.Stage.LOOT) {
         this.integers.clear();
      }

      this.index5 = 0;
      this.index6 = -1;
      this.index = Integer.MIN_VALUE;
      this.helper40();
      this.humanBreakRotation.reset();
   }

   private void helper64(String message) {
      if (this.booleanSetting.isState()) {
         ChatUtils.sendMessage("[EndLooter] " + message);
      }
   }

   private enum Nav {
      MOVING,
      ARRIVED,
      STUCK;
   }

   private enum Stage {
      TP_HOME,
      PICK_CHEST,
      GO_CHEST,
      OPEN_CHEST,
      LOOT,
      USE_ITEMS,
      GO_PORTAL,
      ENTER_PORTAL,
      GO_END_POINT,
      GO_POT,
      BREAK_POT,
      COLLECT_DROPS,
      GO_SUS,
      BRUSH_SUS,
      DONE;
   }
}
