package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventBinding;
import dlc.lumen.api.events.implement.EventMoveInput;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.api.utils.player.SwapManager;
import dlc.lumen.api.utils.render.ItemCircleIndicator;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.movement.InventoryWalk;
import dlc.lumen.client.modules.impl.movement.Sprint;
import dlc.lumen.client.modules.settings.implement.BindSetting;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.mixin.IClientInteractionManagerAccessor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ServerHelper extends Module {
   public static ServerHelper INSTANCE = new ServerHelper();
   private final ModeSetting spookyMode = new ModeSetting("Режим", "HolyWorld", "Funtime", "HolyWorld");
   private final BindSetting bindSetting = new BindSetting("Исцеление", -1);
   private final BindSetting bindSetting2 = new BindSetting("Хорус", -1);
   private final BindSetting bindSetting3 = new BindSetting("Дезориентация", -1).visible(() -> this.spookyMode.is("Funtime"));
   private final BindSetting bindSetting4 = new BindSetting("Трапка", -1).visible(() -> this.spookyMode.is("Funtime"));
   private final BindSetting bindSetting5 = new BindSetting("Пласт", -1).visible(() -> this.spookyMode.is("Funtime"));
   private final BindSetting bindSetting6 = new BindSetting("Явная пыль", -1).visible(() -> this.spookyMode.is("Funtime"));
   private final BindSetting bindSetting7 = new BindSetting("Снег заморозки", -1).visible(() -> this.spookyMode.is("Funtime"));
   private final BindSetting bindSetting8 = new BindSetting("Заряд ветра", -1).visible(() -> this.spookyMode.is("Funtime"));
   private final BindSetting bindSetting9 = new BindSetting("стан", -1).visible(() -> this.spookyMode.is("HolyWorld"));
   private final BindSetting bindSetting10 = new BindSetting("взрывная трапка", -1).visible(() -> this.spookyMode.is("HolyWorld"));
   private final BindSetting bindSetting11 = new BindSetting("трапка", -1).visible(() -> this.spookyMode.is("HolyWorld"));
   private final BindSetting bindSetting12 = new BindSetting("взрывная штучка", -1).visible(() -> this.spookyMode.is("HolyWorld"));
   private final BindSetting bindSetting13 = new BindSetting("ком снега", -1).visible(() -> this.spookyMode.is("HolyWorld"));
   private final BooleanSetting booleanSetting = new BooleanSetting("Эффект снежка", false).visible(() -> this.spookyMode.is("HolyWorld"));
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private int index = 0;
   private int index2 = -1;
   private int index3 = -1;
   private boolean flag4 = false;
   private int index4 = -1;
   private int index5 = -1;
   private int index6 = 0;
   private boolean flag5 = false;
   private boolean flag6 = false;
   private Item item2 = null;
   private Predicate<ItemStack> predicate = null;
   private boolean flag7 = false;
   private Item item3 = null;
   private boolean flag8 = false;
   private Predicate<ItemStack> predicate2 = null;
   private static final long TIMESTAMP = 10000L;
   private static final int INDEX = 3842303;
   private final Map<Integer, Long> integers = new ConcurrentHashMap<>();
   private final Map<Integer, Vec3d> integers2 = new HashMap<>();
   private int index7;
   private final Matrix4f matrix4f = new Matrix4f();
   private final Quaternionf quaternionf = new Quaternionf();
   private Vec3d vec3d = Vec3d.ZERO;
   private int index8;
   private int index9;
   private float volume;
   private boolean flag9 = false;
   private final Vector3f vector3f = new Vector3f();
   private final Vector4f vector4f = new Vector4f();

   public ServerHelper() {
      super("ServerHelper", "Помощник для серверов", Module.ModuleCategory.MISC);
      this.addSettings(
         this.spookyMode,
         this.bindSetting,
         this.bindSetting2,
         this.bindSetting3,
         this.bindSetting4,
         this.bindSetting5,
         this.bindSetting6,
         this.bindSetting7,
         this.bindSetting8,
         this.bindSetting9,
         this.bindSetting10,
         this.bindSetting11,
         this.bindSetting12,
         this.bindSetting13,
         this.booleanSetting
      );
   }

   public boolean isSpookyMode() {
      return this.spookyMode.is("Funtime");
   }

   public boolean isHolyWorldMode() {
      return this.spookyMode.is("HolyWorld");
   }

   public List<ServerHelper.HelperBind> getSpookyHelperBinds() {
      return List.of(
         new ServerHelper.HelperBind("Дезориентация", Items.ENDER_EYE, this.bindSetting3),
         new ServerHelper.HelperBind("Трапка", Items.NETHERITE_SCRAP, this.bindSetting4),
         new ServerHelper.HelperBind("Пласт", Items.DRIED_KELP, this.bindSetting5),
         new ServerHelper.HelperBind("Явная пыль", Items.SUGAR, this.bindSetting6),
         new ServerHelper.HelperBind("Снег заморозки", Items.SNOWBALL, this.bindSetting7),
         new ServerHelper.HelperBind("Заряд ветра", Items.WIND_CHARGE, this.bindSetting8)
      );
   }

   public List<ServerHelper.HelperBind> getHolyWorldHelperBinds() {
      return List.of(
         new ServerHelper.HelperBind("стан", Items.NETHER_STAR, this.bindSetting9),
         new ServerHelper.HelperBind("взрывная трапка", Items.PRISMARINE_SHARD, this.bindSetting10),
         new ServerHelper.HelperBind("трапка", Items.POPPED_CHORUS_FRUIT, this.bindSetting11),
         new ServerHelper.HelperBind("взрывная штучка", Items.FIRE_CHARGE, this.bindSetting12),
         new ServerHelper.HelperBind("ком снега", Items.SNOWBALL, this.bindSetting13)
      );
   }

   public boolean isSnowGlowing(Entity entity) {
      if (entity != null && this.isEnable() && !this.integers.isEmpty()) {
         Long var2 = this.integers.get(entity.getId());
         return var2 != null && System.currentTimeMillis() < var2;
      } else {
         return false;
      }
   }

   public int snowGlowColor() {
      return 3842303;
   }

   private void updateState() {
      if (++this.index7 % 3 == 0) {
         long var1 = System.currentTimeMillis();
         this.integers.values().removeIf(until -> var1 >= until);
         if (this.isEnable() && this.isHolyWorldMode() && this.booleanSetting.isState()) {
            if (!this.integers.isEmpty()) {
               this.helper2();
            }

            HashSet<Integer> var3 = new HashSet<>();

            for (Entity var5 : mc.world.getEntities()) {
               if (!(var5 instanceof SnowballEntity var6)) {
                  continue;
               }

               int var7 = var6.getId();
               Entity var9 = var6.getOwner();
               boolean var10 = this.integers2.containsKey(var7)
                  || var9 == mc.player
                  || var9 == null && var6.squaredDistanceTo(mc.player) < 12.25;
               if (!var10) {
                  continue;
               }

               var3.add(var7);
               Vec3d var14 = var6.getPos();
               Vec3d var15 = this.integers2.getOrDefault(var7, var14);

               for (PlayerEntity var12 : mc.world.getPlayers()) {
                  if (var12 != mc.player) {
                     Box var13 = var12.getBoundingBox().expand(0.4);
                     if (var13.contains(var14) || var13.raycast(var15, var14).isPresent()) {
                        this.integers.put(var12.getId(), var1 + 10000L);
                     }
                  }
               }

               this.integers2.put(var7, var14);
            }

            this.integers2.keySet().retainAll(var3);
            this.helper3();
         } else {
            this.integers2.clear();
            this.integers.clear();
         }
      }
   }

   @EventLink
   public void onSnowProjection(Event3DRender event) {
      this.flag9 = true;
      this.matrix4f.set(event.getProjectionMatrix());
      this.vec3d = event.getCamera().getPos();
      this.quaternionf.set(event.getCamera().getRotation()).conjugate();
      this.volume = event.getTickDelta();
      this.index8 = mc.getWindow().getScaledWidth();
      this.index9 = mc.getWindow().getScaledHeight();
   }

   @EventLink
   public void onSnowRender2D(EventRender.Default event) {
      if (this.flag9 && !this.integers.isEmpty() && mc.world != null && mc.player != null) {
         if (this.isEnable() && this.isHolyWorldMode() && this.booleanSetting.isState()) {
            long var2 = System.currentTimeMillis();
            ItemStack var4 = new ItemStack(Items.SNOWBALL);
            int var5 = -12934913;

            for (Entry var7 : this.integers.entrySet()) {
               if (mc.world.getEntityById((Integer)var7.getKey()) instanceof PlayerEntity var9) {
                  float var10 = MathHelper.clamp((float)((Long)var7.getValue() - var2) / 10000.0F, 0.0F, 1.0F);
                  double var11 = MathHelper.lerp(this.volume, var9.lastRenderX, var9.getX());
                  double var13 = MathHelper.lerp(this.volume, var9.lastRenderY, var9.getY()) + var9.getHeight() * 0.5;
                  double var15 = MathHelper.lerp(this.volume, var9.lastRenderZ, var9.getZ());
                  float[] var17 = this.helper(var11, var13, var15);
                  if (var17 != null) {
                     ItemCircleIndicator.draw(event.getContext(), var17[0], var17[1], 9.0F, var4, var10, var5);
                  }
               }
            }
         }
      }
   }

   private float[] helper(double wx, double wy, double wz) {
      this.vector3f.set((float)(wx - this.vec3d.x), (float)(wy - this.vec3d.y), (float)(wz - this.vec3d.z));
      this.vector3f.rotate(this.quaternionf);
      this.vector4f.set(this.vector3f.x, this.vector3f.y, this.vector3f.z, 1.0F);
      this.matrix4f.transform(this.vector4f);
      float var7 = this.vector4f.w;
      if (var7 <= 1.0E-5F) {
         return null;
      }

      float var8 = (this.vector4f.x / var7 * 0.5F + 0.5F) * this.index8;
      float var9 = (1.0F - (this.vector4f.y / var7 * 0.5F + 0.5F)) * this.index9;
      return !Float.isNaN(var8) && !Float.isNaN(var9) && !Float.isInfinite(var8) && !Float.isInfinite(var9) ? new float[]{var8, var9} : null;
   }

   private void helper2() {
      if (mc.worldRenderer != null) {
         if (mc.worldRenderer.getEntityOutlinesFramebuffer() == null) {
            try {
               mc.worldRenderer.loadEntityOutlinePostProcessor();
            } catch (Exception var2) {
            }
         }
      }
   }

   private void helper3() {
      if (!this.integers.isEmpty()) {
         ThreadLocalRandom var1 = ThreadLocalRandom.current();

         for (Integer var3 : this.integers.keySet()) {
            if (mc.world.getEntityById(var3) instanceof PlayerEntity var5) {
               Box var6 = var5.getBoundingBox();
               double var7 = var6.maxX - var6.minX;
               double var9 = var6.maxZ - var6.minZ;
               double var11 = var6.maxY - var6.minY;

               for (int var13 = 0; var13 < 4; var13++) {
                  double var14 = var6.minX - 0.15 + var1.nextDouble() * (var7 + 0.3);
                  double var16 = var6.minZ - 0.15 + var1.nextDouble() * (var9 + 0.3);
                  double var18 = var6.minY + var1.nextDouble() * (var11 + 0.4);
                  mc.world.addParticle(ParticleTypes.SNOWFLAKE, true, true, var14, var18, var16, 0.0, -0.05, 0.0);
               }
            }
         }
      }
   }

   @EventLink
   public void onBinding(EventBinding event) {
      if (mc.currentScreen == null && !this.flag && !this.flag7 && mc.player != null) {
         Item var2 = null;
         boolean var3 = false;
         Predicate<ItemStack> var4 = null;
         if (this.bindSetting.getKey() != -1 && event.getKey() == this.bindSetting.getKey()) {
            var2 = Items.POTION;
            var3 = true;
            var4 = ServerHelper::helper13;
         } else if (this.bindSetting2.getKey() != -1 && event.getKey() == this.bindSetting2.getKey()) {
            var2 = Items.CHORUS_FRUIT;
            var3 = true;
         } else if (this.spookyMode.is("Funtime")) {
            if (event.getKey() == this.bindSetting3.getKey()) {
               var2 = Items.ENDER_EYE;
            } else if (event.getKey() == this.bindSetting4.getKey()) {
               var2 = Items.NETHERITE_SCRAP;
            } else if (event.getKey() == this.bindSetting5.getKey()) {
               var2 = Items.DRIED_KELP;
            } else if (event.getKey() == this.bindSetting6.getKey()) {
               var2 = Items.SUGAR;
            } else if (event.getKey() == this.bindSetting7.getKey()) {
               var2 = Items.SNOWBALL;
            } else if (event.getKey() == this.bindSetting8.getKey()) {
               var2 = Items.WIND_CHARGE;
            }
         } else if (this.spookyMode.is("HolyWorld")) {
            if (event.getKey() == this.bindSetting9.getKey()) {
               var2 = Items.NETHER_STAR;
            } else if (event.getKey() == this.bindSetting10.getKey()) {
               var2 = Items.PRISMARINE_SHARD;
            } else if (event.getKey() == this.bindSetting11.getKey()) {
               var2 = Items.POPPED_CHORUS_FRUIT;
            } else if (event.getKey() == this.bindSetting12.getKey()) {
               var2 = Items.FIRE_CHARGE;
            } else if (event.getKey() == this.bindSetting13.getKey()) {
               var2 = Items.SNOWBALL;
            }
         }

         if (var2 != null) {
            boolean var5 = this.helper4(var2, var4);
            this.flag3 = var3 || !var5;
            if (this.flag3) {
               this.helper11();
               this.helper10();
               InventoryWalk.stopTick(8);
            }

            this.flag7 = true;
            this.item3 = var2;
            this.flag8 = var3;
            this.predicate2 = var4;
            this.index = 0;
         }
      }
   }

   private boolean helper4(Item item, Predicate<ItemStack> pred) {
      for (int var3 = 0; var3 < 9; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && (pred != null ? pred.test(var4) : var4.getItem() == item)) {
            return true;
         }
      }

      return false;
   }

   @EventLink
   public void onMoveInput(EventMoveInput event) {
      if (mc.player != null && this.flag3 && (this.flag || this.flag7)) {
         mc.player.setSprinting(false);
         event.setForward(0.0F);
         event.setStrafe(0.0F);
         event.setJump(false);
         event.setSneak(false);
         event.setSprint(false);
      }
   }

   private boolean checkCondition(Item item, boolean consume, Predicate<ItemStack> pred) {
      if (mc.player != null && mc.interactionManager != null) {
         this.index3 = mc.player.getInventory().selectedSlot;
         this.flag4 = false;
         this.index4 = -1;
         this.index5 = -1;
         this.index6 = 1;
         this.index = 0;
         this.flag5 = false;
         this.flag6 = consume;
         this.item2 = item;
         this.predicate = pred;
         int var4 = -1;

         for (int var5 = 0; var5 < 9; var5++) {
            ItemStack var6 = mc.player.getInventory().getStack(var5);
            if (!var6.isEmpty()) {
               if (pred != null && pred.test(var6)) {
                  var4 = var5;
                  break;
               }

               if (pred == null && var6.getItem() == item) {
                  var4 = var5;
                  break;
               }
            }
         }

         if (var4 == -1) {
            int var8 = -1;

            for (int var9 = 9; var9 < 36; var9++) {
               ItemStack var7 = mc.player.getInventory().getStack(var9);
               if (!var7.isEmpty()) {
                  if (pred != null && pred.test(var7)) {
                     var8 = var9;
                     break;
                  }

                  if (pred == null && var7.getItem() == item) {
                     var8 = var9;
                     break;
                  }
               }
            }

            if (var8 == -1) {
               ChatUtils.sendMessage("Предмет не найден!");
               return false;
            }

            int var10 = this.index3;
            SwapManager.swapInventorySlotHidden(var8, var10);
            var4 = var10;
            this.flag4 = true;
            this.index4 = var8;
            this.index5 = var10;
            InventoryWalk.stopTick(5);
         }

         this.index2 = var4;
         this.helper9(var4);
         return true;
      } else {
         return false;
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.updateState();
         if (this.flag7) {
            if (this.flag3) {
               this.helper10();
               InventoryWalk.stopTick(3);
            }

            this.index++;
            if (this.index >= 2) {
               this.flag7 = false;
               if (!this.checkCondition(this.item3, this.flag8, this.predicate2)) {
                  this.helper8();
                  return;
               }

               this.flag = true;
               this.item3 = null;
               this.predicate2 = null;
            }
         } else if (this.flag) {
            if (this.flag3) {
               InventoryWalk.stopTick(2);
               this.helper10();
            }

            this.index++;
            if (mc.player.getInventory().selectedSlot != this.index2) {
               this.helper9(this.index2);
            }

            if (this.flag6) {
               this.helper5();
            } else {
               this.helper6();
            }
         }
      } else {
         if (this.flag || this.flag7) {
            this.flag = false;
            this.flag7 = false;
            this.helper8();
         }
      }
   }

   private void helper5() {
      if (this.index6 == 1) {
         if (this.index < 2) {
            return;
         }

         mc.options.useKey.setPressed(true);
         if (!mc.player.isUsingItem()) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         }

         if (mc.player.isUsingItem()) {
            this.flag5 = true;
            this.index6 = 2;
            this.index = 0;
         }
      }

      if (this.index6 == 2 && !mc.player.isUsingItem() && this.flag5) {
         this.helper7();
      }

      if (this.index > 40) {
         this.helper7();
      }
   }

   private void helper6() {
      if (this.index6 == 1) {
         if (this.index >= 2) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            this.flag5 = true;
            this.index6 = 2;
            this.index = 0;
         }
      } else {
         if (this.index6 == 2) {
            if (this.index < 2) {
               return;
            }

            this.helper7();
         }
      }
   }

   private void helper7() {
      if (this.flag) {
         if (mc.options != null) {
            mc.options.useKey.setPressed(false);
         }

         this.flag = false;
         this.helper8();
      }
   }

   private void helper8() {
      if (this.flag4 && this.index4 != -1 && this.index5 != -1 && mc.interactionManager != null && mc.player != null) {
         SwapManager.swapInventorySlotHidden(this.index4, this.index5);
      }

      if (this.index3 >= 0 && this.index3 <= 8 && mc.player != null) {
         this.helper9(this.index3);
      }

      if (this.flag3) {
         this.helper12();
         this.helper10();
         InventoryWalk.stopTick(3);
      }

      this.flag4 = false;
      this.index4 = -1;
      this.index5 = -1;
      this.index3 = -1;
      this.index2 = -1;
      this.flag3 = false;
   }

   private void helper9(int slot) {
      if (mc.player != null && slot >= 0 && slot <= 8) {
         if (mc.player.getInventory().selectedSlot != slot) {
            mc.player.getInventory().selectedSlot = slot;
            if (mc.player.networkHandler != null) {
               mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }

            if (mc.interactionManager instanceof IClientInteractionManagerAccessor var2) {
               var2.lumen$setLastSelectedSlot(slot);
            }
         }
      }
   }

   private void helper10() {
      if (mc.player != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.backKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
         mc.options.sprintKey.setPressed(false);
         mc.player.setSprinting(false);
      }
   }

   private void helper11() {
      if (!this.flag2) {
         Sprint.pushPause(0L);
         this.flag2 = true;
      }
   }

   private void helper12() {
      if (this.flag2) {
         Sprint.popPause();
         this.flag2 = false;
      }
   }

   @Override
   public void onDisable() {
      this.helper8();
      this.flag = false;
      this.flag7 = false;
      this.item3 = null;
      this.predicate2 = null;
      if (this.flag2) {
         this.helper12();
      }

      this.helper10();
      this.flag3 = false;
      super.onDisable();
   }

   private static boolean helper13(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() == Items.POTION) {
         PotionContentsComponent var1 = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (var1 == null) {
            return false;
         }

         for (StatusEffectInstance var3 : var1.getEffects()) {
            if (var3.getEffectType().equals(StatusEffects.INSTANT_HEALTH) || var3.getEffectType().equals(StatusEffects.REGENERATION)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public record HelperBind(String name, Item item, BindSetting bind) {

      public HelperBind(String name, Item item, BindSetting bind) {
         this.name = name;
         this.item = item;
         this.bind = bind;
      }

      public String name() {
         return this.name;
      }

      public Item item() {
         return this.item;
      }

      public BindSetting bind() {
         return this.bind;
      }
   }
}
