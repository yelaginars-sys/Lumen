package dlc.lumen.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Trajectories extends Module {
   public static Trajectories INSTANCE = new Trajectories();
   private static final Identifier TEXTURE_ID = Identifier.of("lumen", "textures/particle/bloom.png");
   private static final Identifier TEXTURE_ID2 = Identifier.of("lumen", "textures/cross/hit.png");
   private final ModeSetting modeSetting = new ModeSetting("Режим сервера", "HolyWorld", "HolyWorld", "SpookyTime");
   private final FloatSetting floatSetting = new FloatSetting("Размер маркера", 1.2F, 0.6F, 2.4F, 0.1F);
   private static final int INDEX = 440;
   private static final double LEVEL = 0.5;
   private static final double LEVEL2 = 4.0;
   private static final Identifier TEXTURE_ID3 = Identifier.of("lumen", "textures/trajectories/glow.png");
   private static final int INDEX2 = 30;
   private static final int INDEX3 = 300;
   private static final int INDEX4 = 28;
   private static final int INDEX5 = 4;
   private static final int INDEX6 = 220;
   private static final long TIMESTAMP = 350L;
   private static final int INDEX7 = 40;
   private static final double LEVEL3 = 1.5;
   private final Map<UUID, List<Trajectories.PearlGhost>> uUIDs = new ConcurrentHashMap<>();
   private final Map<UUID, Long> uUIDs2 = new ConcurrentHashMap<>();
   private int index2;
   private final Map<BlockPos, Trajectories.StunCube> blockPoss = new ConcurrentHashMap<>();
   private final Map<UUID, Trajectories.StunMarker> uUIDs3 = new ConcurrentHashMap<>();
   private final Map<Long, Trajectories.ActiveTrapZone> longs = new ConcurrentHashMap<>();
   private final Map<UUID, Trajectories.PlayerCustomSnapshot> uUIDs4 = new ConcurrentHashMap<>();
   private final Map<String, Long> strings = new ConcurrentHashMap<>();
   private final List<Trajectories.ImpactRenderInfo> trajectoriess = new ArrayList<>();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Quaternionf quaternionf = new Quaternionf();
   private final Matrix4f matrix4f2 = new Matrix4f();
   private Vec3d vec3d = Vec3d.ZERO;
   private boolean flag;
   private final Map<UUID, Integer> uUIDs5 = new ConcurrentHashMap<>();
   private final Map<UUID, Integer> uUIDs6 = new ConcurrentHashMap<>();
   private final Map<UUID, Integer> uUIDs7 = new ConcurrentHashMap<>();
   private final Map<UUID, Integer> uUIDs8 = new ConcurrentHashMap<>();
   private final Map<UUID, Integer> uUIDs9 = new ConcurrentHashMap<>();
   private final Map<UUID, Long> uUIDs10 = new ConcurrentHashMap<>();
   private final AtomicLong atomicLong = new AtomicLong();
   private Trajectories.PlayerCustomSnapshot playerCustomSnapshot;
   private Trajectories.CustomZoneType customZoneType;
   private long timestamp;

   public Trajectories() {
      super("Trajectories", "Показывает точку падения снаряда", Module.ModuleCategory.RENDER);
      this.addSettings(this.modeSetting, this.floatSetting);
   }

   private String resolveString() {
      return this.modeSetting.getCurrent();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.blockPoss.clear();
      this.uUIDs3.clear();
      this.longs.clear();
      this.uUIDs4.clear();
      this.uUIDs5.clear();
      this.uUIDs6.clear();
      this.uUIDs7.clear();
      this.uUIDs8.clear();
      this.uUIDs9.clear();
      this.uUIDs10.clear();
      this.strings.clear();
      this.playerCustomSnapshot = null;
      this.customZoneType = null;
      this.timestamp = 0L;
      this.trajectoriess.clear();
      this.flag = false;
      this.uUIDs.clear();
      this.uUIDs2.clear();
   }

   public void onStunUsed(PlayerEntity player) {
      if (player != null && mc.world != null) {
         this.activateStunCube(player.getBlockPos(), player.getUuid());
         Vec3d var2 = player.getPos().add(0.0, 1.5, 0.0);
         this.uUIDs3.put(UUID.randomUUID(), new Trajectories.StunMarker(var2, 40));
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.world != null) {
         this.helper7();
         this.helper();
         this.blockPoss.values().removeIf(cube -> {
            if (cube.dissolveTicks > 0) {
               cube.dissolveTicks--;
               return cube.dissolveTicks <= 0;
            }

            cube.remainingTicks--;
            if (cube.remainingTicks <= 0) {
               cube.dissolveTicks = 28;
               return false;
            }

            Box var2x = this.helper16(cube.center);

            for (PlayerEntity var4x : mc.world.getPlayers()) {
               if (var4x != null && var2x.contains(var4x.getBoundingBox().getCenter()) && !var4x.getUuid().equals(cube.activatorId)) {
                  var4x.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 4, false, true, true));
               }
            }

            return false;
         });
         this.longs.values().removeIf(zone -> --zone.remainingTicks <= 0);
         this.uUIDs3.values().removeIf(marker -> {
            marker.remainingTicks--;
            return marker.remainingTicks <= 0;
         });
         if (++this.index2 % 3 == 0) {
            HashSet var2 = new HashSet();

            for (Entity var4 : mc.world.getEntities()) {
               if (var4 instanceof EnderPearlEntity var5 && var5.isAlive()) {
                  var2.add(var5.getUuid());
               }
            }

            this.uUIDs.keySet().removeIf(id -> !var2.contains(id));
         }
      }
   }

   private void helper() {
      if (mc.world != null) {
         String var1 = this.resolveString();

         for (PlayerEntity var3 : mc.world.getPlayers()) {
            if (var3 != mc.player) {
               UUID var4 = var3.getUuid();
               if (var1.equals("HolyWorld")) {
                  this.helper2(var3, var4);
               } else if (var1.equals("SpookyTime")) {
                  this.helper3(var3, var4);
               }
            }
         }
      }
   }

   private void helper2(PlayerEntity player, UUID playerId) {
      int var3 = this.helper5(player, Trajectories.CustomZoneType.STUN);
      Integer var4 = this.uUIDs5.get(playerId);
      if (var4 != null && var4 > var3 && this.helper6(player, Trajectories.CustomZoneType.STUN)) {
         this.onStunUsed(player);
      }

      this.uUIDs5.put(playerId, var3);
      int var5 = this.helper5(player, Trajectories.CustomZoneType.EXPLOSIVE_TRAP);
      Integer var6 = this.uUIDs6.get(playerId);
      if (var6 != null && var6 > var5 && this.helper6(player, Trajectories.CustomZoneType.EXPLOSIVE_TRAP)) {
         this.helper11(player, Trajectories.CustomZoneType.EXPLOSIVE_TRAP);
      }

      this.uUIDs6.put(playerId, var5);
      int var7 = this.helper5(player, Trajectories.CustomZoneType.TRAP);
      Integer var8 = this.uUIDs7.get(playerId);
      if (var8 != null && var8 > var7 && this.helper6(player, Trajectories.CustomZoneType.TRAP)) {
         this.helper11(player, Trajectories.CustomZoneType.TRAP);
      }

      this.uUIDs7.put(playerId, var7);
   }

   private void helper3(PlayerEntity player, UUID playerId) {
      int var3 = this.helper5(player, Trajectories.CustomZoneType.SPOOKY_TRAP);
      Integer var4 = this.uUIDs8.get(playerId);
      if (var4 != null && var4 > var3 && this.helper6(player, Trajectories.CustomZoneType.SPOOKY_TRAP)) {
         this.helper11(player, Trajectories.CustomZoneType.SPOOKY_TRAP);
      }

      this.uUIDs8.put(playerId, var3);
      int var5 = this.helper5(player, Trajectories.CustomZoneType.SPOOKY_PLAST_VERTICAL);
      Integer var6 = this.uUIDs9.get(playerId);
      if (var6 != null && var6 > var5 && this.helper6(player, Trajectories.CustomZoneType.SPOOKY_PLAST_VERTICAL)) {
         Trajectories.CustomZoneType var7 = this.helper4(player);
         this.helper11(player, var7);
      }

      this.uUIDs9.put(playerId, var5);
   }

   private Trajectories.CustomZoneType helper4(PlayerEntity player) {
      float var2 = player.getPitch();
      return Math.abs(var2) > 45.0F ? Trajectories.CustomZoneType.SPOOKY_PLAST_HORIZONTAL : Trajectories.CustomZoneType.SPOOKY_PLAST_VERTICAL;
   }

   private int helper5(PlayerEntity player, Trajectories.CustomZoneType type) {
      String var3 = this.resolveString();
      int var4 = 0;
      if (type.resolveString(player.getMainHandStack(), var3)) {
         var4 += player.getMainHandStack().getCount();
      }

      if (type.resolveString(player.getOffHandStack(), var3)) {
         var4 += player.getOffHandStack().getCount();
      }

      for (int var5 = 0; var5 < player.getInventory().size(); var5++) {
         ItemStack var6 = player.getInventory().getStack(var5);
         if (type.resolveString(var6, var3)) {
            var4 += var6.getCount();
         }
      }

      return var4;
   }

   private boolean helper6(PlayerEntity player, Trajectories.CustomZoneType type) {
      String var3 = this.resolveString();
      return type.resolveString(player.getMainHandStack(), var3) || type.resolveString(player.getOffHandStack(), var3);
   }

   private void helper7() {
      if (mc.player == null) {
         this.playerCustomSnapshot = null;
         this.customZoneType = null;
         this.timestamp = 0L;
      } else {
         Trajectories.PlayerCustomSnapshot var1 = this.helper8(mc.player);
         Trajectories.PlayerCustomSnapshot var2 = this.playerCustomSnapshot;
         this.playerCustomSnapshot = var1;
         if (this.customZoneType != null && var2 != null) {
            long var3 = System.currentTimeMillis();
            if (var3 - this.timestamp > 750L) {
               this.customZoneType = null;
               this.timestamp = 0L;
            } else {
               if (this.helper12(var2, var1, this.customZoneType)) {
                  this.helper11(mc.player, this.customZoneType);
                  this.customZoneType = null;
                  this.timestamp = 0L;
               }
            }
         }
      }
   }

   private Trajectories.PlayerCustomSnapshot helper8(PlayerEntity player) {
      HashMap<Trajectories.CustomZoneType, Integer> var2 = new HashMap<>();
      String var3 = this.resolveString();

      for (int var4 = 0; var4 < player.getInventory().size(); var4++) {
         ItemStack var5 = player.getInventory().getStack(var4);
         Trajectories.CustomZoneType var6 = Trajectories.CustomZoneType.helper(var5, var3);
         if (var6 != null) {
            var2.merge(var6, var5.getCount(), Integer::sum);
         }
      }

      return new Trajectories.PlayerCustomSnapshot(
         var2,
         Trajectories.CustomZoneType.helper(player.getMainHandStack(), var3),
         Trajectories.CustomZoneType.helper(player.getOffHandStack(), var3),
         player.getMainHandStack().getCount(),
         player.getOffHandStack().getCount()
      );
   }

   @EventLink
   public void onPacket(EventPacket event) {
      if (mc.world != null && mc.player != null) {
         if (event.getType() == EventPacket.Type.RECEIVE) {
            this.helper9(event.getPacket());
         }

         if (event.getType() == EventPacket.Type.SEND) {
            this.helper10(event.getPacket());
         }
      }
   }

   private void helper9(Packet<?> packet) {
      if (packet instanceof EntityEquipmentUpdateS2CPacket var2) {
         int var3 = var2.getEntityId();

         for (PlayerEntity var5 : mc.world.getPlayers()) {
            if (var5.getId() == var3 && var5 != mc.player) {
               String var6 = this.resolveString();
               this.uUIDs5.put(var5.getUuid(), this.helper5(var5, Trajectories.CustomZoneType.STUN));
               this.uUIDs6.put(var5.getUuid(), this.helper5(var5, Trajectories.CustomZoneType.EXPLOSIVE_TRAP));
               this.uUIDs7.put(var5.getUuid(), this.helper5(var5, Trajectories.CustomZoneType.TRAP));
               this.uUIDs8.put(var5.getUuid(), this.helper5(var5, Trajectories.CustomZoneType.SPOOKY_TRAP));
               this.uUIDs9.put(var5.getUuid(), this.helper5(var5, Trajectories.CustomZoneType.SPOOKY_PLAST_VERTICAL));
               break;
            }
         }
      }
   }

   private void helper10(Packet<?> packet) {
      if (packet instanceof PlayerInteractItemC2SPacket var2 && mc.player != null) {
         Hand var3 = var2.getHand();
         ItemStack var4 = var3 == Hand.MAIN_HAND ? mc.player.getMainHandStack() : mc.player.getOffHandStack();
         String var5 = this.resolveString();
         Trajectories.CustomZoneType var6 = Trajectories.CustomZoneType.helper(var4, var5);
         if (var6 != null) {
            if (var6 == Trajectories.CustomZoneType.STUN) {
               this.helper11(mc.player, var6);
            } else if (var6 != Trajectories.CustomZoneType.SPOOKY_PLAST_VERTICAL && var6 != Trajectories.CustomZoneType.SPOOKY_PLAST_HORIZONTAL) {
               this.customZoneType = var6;
               this.timestamp = System.currentTimeMillis();
            } else {
               Trajectories.CustomZoneType var7 = this.helper4(mc.player);
               this.customZoneType = var7;
               this.timestamp = System.currentTimeMillis();
            }
         }
      }
   }

   private void helper11(PlayerEntity player, Trajectories.CustomZoneType type) {
      if (player != null && type != null && mc.world != null) {
         if (this.helper14(player.getUuid(), type)) {
            if (type == Trajectories.CustomZoneType.STUN) {
               this.onStunUsed(player);
            } else {
               this.helper15(type, Vec3d.ofCenter(player.getBlockPos()), player.getUuid());
            }
         }
      }
   }

   private boolean helper12(
      Trajectories.PlayerCustomSnapshot previousSnapshot, Trajectories.PlayerCustomSnapshot currentSnapshot, Trajectories.CustomZoneType type
   ) {
      int var4 = previousSnapshot.getCount(type) - currentSnapshot.getCount(type);
      return var4 != 1
         ? false
         : this.helper13(
               previousSnapshot.mainHandType, previousSnapshot.mainHandCount, currentSnapshot.mainHandType, currentSnapshot.mainHandCount, type
            )
            || this.helper13(
               previousSnapshot.offHandType, previousSnapshot.offHandCount, currentSnapshot.offHandType, currentSnapshot.offHandCount, type
            );
   }

   private boolean helper13(
      Trajectories.CustomZoneType previousType,
      int previousCount,
      Trajectories.CustomZoneType currentType,
      int currentCount,
      Trajectories.CustomZoneType targetType
   ) {
      if (previousType != targetType) {
         return false;
      } else {
         return currentType == targetType ? currentCount == previousCount - 1 : previousCount == 1;
      }
   }

   private boolean helper14(UUID playerId, Trajectories.CustomZoneType type) {
      long var3 = System.currentTimeMillis();
      String var5 = playerId + ":" + type.name();
      Long var6 = this.strings.get(var5);
      if (var6 != null && var3 - var6 < 350L) {
         return false;
      }

      this.strings.put(var5, var3);
      return true;
   }

   private void helper15(Trajectories.CustomZoneType type, Vec3d center, UUID activatorId) {
      this.longs.entrySet().removeIf(entry -> entry.getValue().type == type && entry.getValue().center.squaredDistanceTo(center) <= 2.25);
      this.longs.put(this.atomicLong.incrementAndGet(), new Trajectories.ActiveTrapZone(type, center, activatorId, type.floatSetting));
   }

   private static String resolveString2(String value) {
      return value == null ? "" : value.toLowerCase(Locale.ROOT).replace('ё', 'е');
   }

   public List<Trajectories.TrapRenderInfo> getActiveTrapRenderInfo() {
      ArrayList var1 = new ArrayList();

      for (Trajectories.StunCube var3 : this.blockPoss.values()) {
         if (var3.remainingTicks > 0) {
            var1.add(new Trajectories.TrapRenderInfo(Vec3d.ofCenter(var3.center), Items.NETHER_STAR, "Стан", var3.remainingTicks / 20.0F));
         }
      }

      for (Trajectories.ActiveTrapZone var5 : this.longs.values()) {
         if (var5.remainingTicks > 0) {
            var1.add(new Trajectories.TrapRenderInfo(var5.center, var5.type.TEXTURE_ID, var5.type.modeSetting, var5.remainingTicks / 20.0F));
         }
      }

      return var1;
   }

   public void activateStunCube(BlockPos center, UUID activatorId) {
      if (mc.world != null) {
         this.blockPoss.remove(center);
         this.blockPoss.put(center, new Trajectories.StunCube(center, activatorId, 300));
         Box var3 = this.helper16(center);

         for (PlayerEntity var5 : mc.world.getPlayers()) {
            if (var5 != null && var3.contains(var5.getBoundingBox().getCenter()) && !var5.getUuid().equals(activatorId)) {
               var5.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300, 4, false, true, true));
            }
         }
      }
   }

   public boolean isInsideStunCube(PlayerEntity player) {
      if (player != null && !this.blockPoss.isEmpty()) {
         Vec3d var2 = player.getPos();

         for (Trajectories.StunCube var4 : this.blockPoss.values()) {
            Box var5 = this.helper16(var4.center);
            if (var5.contains(var2)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private Box helper16(BlockPos center) {
      return new Box(center.getX() - 30, center.getY() - 30, center.getZ() - 30, center.getX() + 30 + 1, center.getY() + 30 + 1, center.getZ() + 30 + 1);
   }

   private void updateState(MatrixStack matrices, Camera camera, int themeColor) {
      if (!this.uUIDs3.isEmpty()) {
         Vec3d var4 = camera.getPos();
         matrices.push();
         matrices.translate(-var4.x, -var4.y, -var4.z);
         Matrix4f var5 = matrices.peek().getPositionMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);

         for (Trajectories.StunMarker var7 : this.uUIDs3.values()) {
            float var8 = 1.0F - var7.remainingTicks / 40.0F;
            float var9 = (float)(0.8F + Math.sin(System.currentTimeMillis() * 0.012) * 0.2F);
            float var10 = 1.2F * (1.0F - var8 * 0.5F) * var9;
            float var11 = (1.0F - var8) * 0.9F;
            int var12 = ColorUtils.setAlphaColor(themeColor, (int)(var11 * 255.0F));
            this.helper17(var5, var7.position, var10, var12);
            this.helper18(var5, var7.position, var10 * 1.2F, themeColor, var8);
         }

         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         matrices.pop();
      }
   }

   private void helper17(Matrix4f matrix, Vec3d pos, float radius, int color) {
      int var5 = color >> 16 & 0xFF;
      int var6 = color >> 8 & 0xFF;
      int var7 = color & 0xFF;
      int var8 = color >> 24 & 0xFF;
      BufferBuilder var9 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
      byte var10 = 24;

      for (int var11 = 0; var11 <= var10; var11++) {
         double var12 = (Math.PI * 2) * var11 / var10;
         double var14 = Math.cos(var12) * radius;
         double var16 = Math.sin(var12) * radius;
         var9.vertex(matrix, (float)(pos.x + var14), (float)pos.y, (float)(pos.z + var16)).color(var5, var6, var7, var8 / 2);
         var9.vertex(matrix, (float)pos.x, (float)(pos.y + radius), (float)pos.z).color(var5, var6, var7, var8);
         var9.vertex(matrix, (float)(pos.x + var14), (float)(pos.y + radius * 2.0F), (float)(pos.z + var16)).color(var5, var6, var7, var8 / 3);
      }

      BufferRenderer.drawWithGlobalProgram(var9.end());
   }

   private void helper18(Matrix4f matrix, Vec3d pos, float size, int themeColor, float progress) {
      float var6 = (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F;
      int var7 = (int)(255.0F * (1.0F - progress) * 0.8F);
      MatrixStack var8 = new MatrixStack();
      var8.push();
      var8.translate(pos.x, pos.y + size * 0.3, pos.z);
      var8.multiply(new Quaternionf().rotateY(var6 * (float) Math.PI / 180.0F));
      Matrix4f var9 = var8.peek().getPositionMatrix();
      BufferBuilder var10 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      float var11 = size * 0.5F;
      int var12 = ColorUtils.setAlphaColor(themeColor, var7);
      int var13 = var12 >> 16 & 0xFF;
      int var14 = var12 >> 8 & 0xFF;
      int var15 = var12 & 0xFF;
      int var16 = var12 >> 24 & 0xFF;
      var10.vertex(var9, -var11, 0.0F, 0.0F).color(var13, var14, var15, var16);
      var10.vertex(var9, var11, 0.0F, 0.0F).color(var13, var14, var15, var16);
      var10.vertex(var9, 0.0F, 0.0F, -var11).color(var13, var14, var15, var16);
      var10.vertex(var9, 0.0F, 0.0F, var11).color(var13, var14, var15, var16);
      var10.vertex(var9, 0.0F, -var11, 0.0F).color(var13, var14, var15, var16);
      var10.vertex(var9, 0.0F, var11, 0.0F).color(var13, var14, var15, var16);
      BufferRenderer.drawWithGlobalProgram(var10.end());
      var8.pop();
   }

   private void updateState2(MatrixStack matrices, Camera camera, int themeColor) {
      if (!this.blockPoss.isEmpty()) {
         Vec3d var4 = camera.getPos();
         Quaternionf var5 = camera.getRotation();
         matrices.push();
         matrices.translate(-var4.x, -var4.y, -var4.z);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, TEXTURE_ID);

         for (Trajectories.StunCube var7 : this.blockPoss.values()) {
            boolean var8 = var7.dissolveTicks > 0;
            float var9 = var8 ? var7.dissolveTicks / 28.0F : Math.min(1.0F, var7.remainingTicks / 40.0F);
            float var10 = var8 ? 1.0F - var7.dissolveTicks / 28.0F : 0.0F;
            float var11 = 0.85F + (float)Math.sin(System.currentTimeMillis() * 0.008 + var7.center.asLong()) * 0.15F;
            int var12 = (int)(90.0F * var9);
            int var13 = ColorUtils.setAlphaColor(themeColor, var12);
            Box var14 = this.helper16(var7.center);
            this.helper19(matrices, var14, var5, var13, 0.38F * var11, var10, var7.center.asLong());
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         matrices.pop();
      }
   }

   private void helper19(MatrixStack matrices, Box box, Quaternionf cameraRotation, int color, float quadSize, float crumbleProgress, long seed) {
      int var9 = color >> 16 & 0xFF;
      int var10 = color >> 8 & 0xFF;
      int var11 = color & 0xFF;
      int var12 = color >> 24 & 0xFF;
      if (var12 >= 4) {
         BufferBuilder var13 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         Vec3d[] var14 = new Vec3d[]{
            new Vec3d(box.minX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.maxZ)
         };
         this.helper20(var13, matrices, cameraRotation, var14[0], var14[1], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 1L);
         this.helper20(var13, matrices, cameraRotation, var14[1], var14[2], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 2L);
         this.helper20(var13, matrices, cameraRotation, var14[2], var14[3], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 3L);
         this.helper20(var13, matrices, cameraRotation, var14[3], var14[0], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 4L);
         this.helper20(var13, matrices, cameraRotation, var14[4], var14[5], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 5L);
         this.helper20(var13, matrices, cameraRotation, var14[5], var14[6], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 6L);
         this.helper20(var13, matrices, cameraRotation, var14[6], var14[7], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 7L);
         this.helper20(var13, matrices, cameraRotation, var14[7], var14[4], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 8L);
         this.helper20(var13, matrices, cameraRotation, var14[0], var14[4], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 9L);
         this.helper20(var13, matrices, cameraRotation, var14[1], var14[5], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 10L);
         this.helper20(var13, matrices, cameraRotation, var14[2], var14[6], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 11L);
         this.helper20(var13, matrices, cameraRotation, var14[3], var14[7], quadSize, var9, var10, var11, var12, crumbleProgress, seed + 12L);
         BufferRenderer.drawWithGlobalProgram(var13.end());
      }
   }

   private void helper20(
      BufferBuilder buffer,
      MatrixStack matrices,
      Quaternionf cameraRotation,
      Vec3d start,
      Vec3d end,
      float quadSize,
      int r,
      int g,
      int b,
      int a,
      float crumbleProgress,
      long edgeSeed
   ) {
      double var14 = start.distanceTo(end);
      int var16 = Math.max(160, Math.min(1920, (int)Math.ceil(var14 / 0.055)));

      for (int var17 = 0; var17 <= var16; var17++) {
         Vec3d var18 = start.lerp(end, (double)var17 / var16);
         if (crumbleProgress > 0.0F) {
            double var19 = edgeSeed * 0.017 + var17 * 0.91;
            double var21 = crumbleProgress * crumbleProgress * (3.0 - 2.0 * crumbleProgress);
            double var23 = Math.sin(var19) * 0.095 * var21;
            double var25 = Math.cos(var19 * 1.11) * 0.095 * var21;
            double var27 = var21 * (1.55 + (Math.sin(var19 * 0.67) * 0.5 + 0.5) * 1.35);
            var18 = new Vec3d(var18.x + var23, var18.y - var27, var18.z + var25);
         }

         matrices.push();
         matrices.translate(var18.x, var18.y, var18.z);
         matrices.multiply(cameraRotation);
         Matrix4f var29 = matrices.peek().getPositionMatrix();
         buffer.vertex(var29, -quadSize, -quadSize, 0.0F).texture(0.0F, 0.0F).color(r, g, b, a);
         buffer.vertex(var29, -quadSize, quadSize, 0.0F).texture(0.0F, 1.0F).color(r, g, b, a);
         buffer.vertex(var29, quadSize, quadSize, 0.0F).texture(1.0F, 1.0F).color(r, g, b, a);
         buffer.vertex(var29, quadSize, -quadSize, 0.0F).texture(1.0F, 0.0F).color(r, g, b, a);
         matrices.pop();
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (mc.player != null && mc.world != null) {
         this.flag = true;
         this.trajectoriess.clear();
         this.matrix4f.set(event.getProjectionMatrix());
         this.vec3d = event.getCamera().getPos();
         this.quaternionf.set(event.getCamera().getRotation());
         int var2 = ColorUtils.getThemeColor();
         this.updateState2(event.getMatrices(), event.getCamera(), var2);
         this.updateState(event.getMatrices(), event.getCamera(), var2);
         this.helper21(event, var2);
         this.helper24(event, var2);
         this.helper22(event);
      }
   }

   public List<Trajectories.ImpactRenderInfo> getImpactRenderInfo() {
      return new ArrayList<>(this.trajectoriess);
   }

   private void updateState3(Event3DRender event, Vec3d position, int themeColor) {
      MatrixStack var4 = event.getMatrices();
      Vec3d var5 = event.getCamera().getPos();
      Quaternionf var6 = event.getCamera().getRotation();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(770, 1);
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, TEXTURE_ID2);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      float var7 = 1.0F + (float)Math.sin(System.currentTimeMillis() * 0.015) * 0.1F;
      float var8 = this.floatSetting.get() * 0.45F * var7;
      int var9 = ColorUtils.setAlphaColor(themeColor, 220);
      int var10 = var9 >> 16 & 0xFF;
      int var11 = var9 >> 8 & 0xFF;
      int var12 = var9 & 0xFF;
      int var13 = var9 >> 24 & 0xFF;
      var4.push();
      var4.translate(position.x - var5.x, position.y - var5.y + 0.05, position.z - var5.z);
      var4.multiply(var6);
      float var14 = (float)(System.currentTimeMillis() % 3000L) / 3000.0F * 360.0F;
      var4.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var14));
      Matrix4f var15 = var4.peek().getPositionMatrix();
      BufferBuilder var16 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var16.vertex(var15, -var8, -var8, 0.0F).texture(0.0F, 1.0F).color(var10, var11, var12, var13);
      var16.vertex(var15, -var8, var8, 0.0F).texture(0.0F, 0.0F).color(var10, var11, var12, var13);
      var16.vertex(var15, var8, var8, 0.0F).texture(1.0F, 0.0F).color(var10, var11, var12, var13);
      var16.vertex(var15, var8, -var8, 0.0F).texture(1.0F, 1.0F).color(var10, var11, var12, var13);
      BufferRenderer.drawWithGlobalProgram(var16.end());
      var4.pop();
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }

   private void helper21(Event3DRender event, int themeColor) {
      ItemStack var3 = this.helper26();
      if (!var3.isEmpty()) {
         Trajectories.ProjectileParams var4 = this.helper27(var3);
         if (var4 != null) {
            float var5 = event.getTickDelta();
            Vec3d var6 = mc.player.getCameraPosVec(var5);
            Trajectories.PredictionResult var7 = this.helper28(mc.player, var4, var6, mc.player.getRotationVec(var5).normalize());
            if (var7 != null && var7.points().length >= 2) {
               Vec3d var8 = var7.hitPos() != null ? var7.hitPos() : var7.points()[var7.points().length - 1];
               this.updateState3(event, var8, themeColor);
               if (var3.isOf(Items.SPLASH_POTION)) {
                  MatrixStack var9 = event.getMatrices();
                  Vec3d var10 = event.getCamera().getPos();
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableCull();
                  RenderSystem.depthMask(false);
                  var9.push();
                  var9.translate(-var10.x, -var10.y, -var10.z);
                  this.helper31(var9, var8, themeColor);
                  var9.pop();
                  RenderSystem.depthMask(true);
                  RenderSystem.enableCull();
                  RenderSystem.disableBlend();
               }
            }
         }
      }
   }

   private void helper22(Event3DRender event) {
      Box var2 = mc.player.getBoundingBox().expand(128.0);
      ArrayList<Entity> var3 = new ArrayList<>(mc.world.getEntitiesByClass(ThrownItemEntity.class, var2, e -> e.isAlive() && !(e instanceof EnderPearlEntity)));
      var3.addAll(mc.world.getEntitiesByClass(TridentEntity.class, var2, Entity::isAlive));
      if (!var3.isEmpty()) {
         float var4 = event.getTickDelta();

         for (Entity var6 : var3) {
            double var7 = var6 instanceof ExperienceBottleEntity ? 0.07 : (!(var6 instanceof PotionEntity) && !(var6 instanceof TridentEntity) ? 0.03 : 0.05);
            List var9 = this.helper33(var6, var7, var4);
            if (var9.size() >= 2) {
               float var10 = (var9.size() - 1) / 20.0F;
               this.trajectoriess.add(new Trajectories.ImpactRenderInfo((Vec3d)var9.get(var9.size() - 1), var10, this.helper23(var6)));
            }
         }
      }
   }

   private Item helper23(Entity e) {
      if (e instanceof ThrownItemEntity var2) {
         ItemStack var3 = var2.getStack();
         if (var3 != null && !var3.isEmpty()) {
            return var3.getItem();
         }
      }

      return e instanceof TridentEntity ? Items.TRIDENT : Items.SNOWBALL;
   }

   private void helper24(Event3DRender event, int themeColor) {
      Box var3 = mc.player.getBoundingBox().expand(128.0);
      List<EnderPearlEntity> var4 = mc.world.getEntitiesByClass(EnderPearlEntity.class, var3, Entity::isAlive);
      if (!var4.isEmpty()) {
         MatrixStack var5 = event.getMatrices();
         Camera var6 = event.getCamera();
         float var7 = event.getTickDelta();
         float var8 = (float)(System.currentTimeMillis() % 10000L) / 1000.0F;

         for (EnderPearlEntity var10 : var4) {
            UUID var11 = var10.getUuid();
            if (!this.uUIDs.containsKey(var11)) {
               ArrayList<Trajectories.PearlGhost> var12 = new ArrayList<>();

               for (int var13 = 0; var13 < 3; var13++) {
                  var12.add(new Trajectories.PearlGhost(var13, 3));
               }

               this.uUIDs.put(var11, var12);
               this.uUIDs2.put(var11, System.currentTimeMillis());
            }

            List<Trajectories.PearlGhost> var24 = this.uUIDs.get(var11);
            long var26 = System.currentTimeMillis();
            Long var15 = this.uUIDs2.get(var11);
            float var16 = var15 != null ? (float)(var26 - var15) / 1000.0F : 0.016F;
            var16 = Math.min(var16, 0.05F);

            for (Trajectories.PearlGhost var18 : var24) {
               var18.update(var16, var8);
            }

            this.uUIDs2.put(var11, var26);
         }

         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, TEXTURE_ID);
         ArrayList<Vec3d> var19 = new ArrayList<>();

         for (EnderPearlEntity var22 : var4) {
            List var25 = this.helper32(var22, var7);
            if (var25.size() >= 2) {
               float var27 = (var25.size() - 1) / 20.0F;
               Vec3d var14 = (Vec3d)var25.get(var25.size() - 1);
               this.trajectoriess.add(new Trajectories.ImpactRenderInfo(var14, var27, Items.ENDER_PEARL));
               var19.add(var14);
               this.helper25(var5, var6, var22, var7, themeColor);
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();

         for (Vec3d var23 : var19) {
            this.updateState3(event, var23, themeColor);
         }
      }
   }

   private void helper25(MatrixStack matrices, Camera camera, EnderPearlEntity pearl, float tickDelta, int themeColor) {
      UUID var6 = pearl.getUuid();
      List var7 = this.uUIDs.get(var6);
      if (var7 != null && !var7.isEmpty()) {
         Vec3d var8 = new Vec3d(
            MathHelper.lerp(tickDelta, pearl.prevX, pearl.getX()),
            MathHelper.lerp(tickDelta, pearl.prevY, pearl.getY()),
            MathHelper.lerp(tickDelta, pearl.prevZ, pearl.getZ())
         );
         Vec3d var9 = camera.getPos();
         Quaternionf var10 = camera.getRotation();
         float var11 = (float)(System.currentTimeMillis() % 10000L) / 1000.0F;
         float var12 = 0.18F + this.floatSetting.get() * 0.05F;
         int var13 = ColorUtils.setAlphaColor(themeColor, 200);
         int var14 = ColorUtils.setAlphaColor(themeColor, 50);
         int var15 = ColorUtils.setAlphaColor(themeColor, 30);
         int var16 = var13 >> 16 & 0xFF;
         int var17 = var13 >> 8 & 0xFF;
         int var18 = var13 & 0xFF;
         int var19 = var13 >> 24 & 0xFF;
         int var20 = var14 >> 16 & 0xFF;
         int var21 = var14 >> 8 & 0xFF;
         int var22 = var14 & 0xFF;
         int var23 = var14 >> 24 & 0xFF;
         int var24 = var15 >> 16 & 0xFF;
         int var25 = var15 >> 8 & 0xFF;
         int var26 = var15 & 0xFF;
         int var27 = var15 >> 24 & 0xFF;
         matrices.push();
         matrices.translate(-var9.x, -var9.y, -var9.z);

         for (int var28 = 0; var28 < var7.size(); var28++) {
            Trajectories.PearlGhost var29 = (Trajectories.PearlGhost)var7.get(var28);
            Vec3d var30 = var29.getPosition(var8, var11);
            float var31 = 0.85F + 0.15F * (float)Math.sin(var11 * 1.5F + var28 * 2.0F + var29.angleOffset);
            float var32 = var12 * var31;
            float var33 = 0.9F + 0.2F * ((float)var28 / var7.size());
            float var34 = var32 * var33;
            matrices.push();
            matrices.translate(var30.x, var30.y, var30.z);
            matrices.multiply(var10);
            float var35 = var11 * (0.5F + var28 * 0.2F) * 30.0F;
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var35));
            Matrix4f var36 = matrices.peek().getPositionMatrix();
            float var37 = var34 * 3.5F;
            BufferBuilder var38 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            var38.vertex(var36, -var37, -var37, 0.0F).texture(0.0F, 0.0F).color(var20, var21, var22, var23);
            var38.vertex(var36, -var37, var37, 0.0F).texture(0.0F, 1.0F).color(var20, var21, var22, var23);
            var38.vertex(var36, var37, var37, 0.0F).texture(1.0F, 1.0F).color(var20, var21, var22, var23);
            var38.vertex(var36, var37, -var37, 0.0F).texture(1.0F, 0.0F).color(var20, var21, var22, var23);
            BufferRenderer.drawWithGlobalProgram(var38.end());
            BufferBuilder var39 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            var39.vertex(var36, -var34, -var34, 0.0F).texture(0.0F, 0.0F).color(var16, var17, var18, var19);
            var39.vertex(var36, -var34, var34, 0.0F).texture(0.0F, 1.0F).color(var16, var17, var18, var19);
            var39.vertex(var36, var34, var34, 0.0F).texture(1.0F, 1.0F).color(var16, var17, var18, var19);
            var39.vertex(var36, var34, -var34, 0.0F).texture(1.0F, 0.0F).color(var16, var17, var18, var19);
            BufferRenderer.drawWithGlobalProgram(var39.end());
            matrices.pop();

            for (int var40 = 1; var40 <= 3; var40++) {
               float var41 = var11 - var40 * 0.04F;
               if (!(var41 < 0.0F)) {
                  Vec3d var42 = var29.getPosition(var8, var41);
                  float var43 = var34 * (1.0F - var40 * 0.2F) * 0.5F;
                  float var44 = 0.3F - var40 * 0.08F;
                  if (!(var44 <= 0.0F)) {
                     int var45 = ColorUtils.setAlphaColor(themeColor, (int)(var44 * 80.0F));
                     int var46 = var45 >> 16 & 0xFF;
                     int var47 = var45 >> 8 & 0xFF;
                     int var48 = var45 & 0xFF;
                     int var49 = var45 >> 24 & 0xFF;
                     matrices.push();
                     matrices.translate(var42.x, var42.y, var42.z);
                     matrices.multiply(var10);
                     Matrix4f var50 = matrices.peek().getPositionMatrix();
                     BufferBuilder var51 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                     var51.vertex(var50, -var43, -var43, 0.0F).texture(0.0F, 0.0F).color(var46, var47, var48, var49);
                     var51.vertex(var50, -var43, var43, 0.0F).texture(0.0F, 1.0F).color(var46, var47, var48, var49);
                     var51.vertex(var50, var43, var43, 0.0F).texture(1.0F, 1.0F).color(var46, var47, var48, var49);
                     var51.vertex(var50, var43, -var43, 0.0F).texture(1.0F, 0.0F).color(var46, var47, var48, var49);
                     BufferRenderer.drawWithGlobalProgram(var51.end());
                     matrices.pop();
                  }
               }
            }
         }

         if (var7.size() >= 2) {
            int var52 = ColorUtils.setAlphaColor(themeColor, 25);
            int var53 = var52 >> 16 & 0xFF;
            int var54 = var52 >> 8 & 0xFF;
            int var55 = var52 & 0xFF;
            int var56 = var52 >> 24 & 0xFF;
            BufferBuilder var57 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            Vec3d[] var58 = new Vec3d[var7.size()];

            for (int var59 = 0; var59 < var7.size(); var59++) {
               var58[var59] = ((Trajectories.PearlGhost)var7.get(var59)).getPosition(var8, var11);
            }

            for (int var60 = 0; var60 < var7.size(); var60++) {
               Vec3d var61 = var58[var60];
               Vec3d var62 = var58[(var60 + 1) % var7.size()];
               var57.vertex(this.matrix4f2, (float)var61.x, (float)var61.y, (float)var61.z).color(var53, var54, var55, var56);
               var57.vertex(this.matrix4f2, (float)var62.x, (float)var62.y, (float)var62.z).color(var53, var54, var55, var56);
               Vec3d var63 = var8;
               Vec3d var64 = var61.add(var62).multiply(0.5);
               var57.vertex(this.matrix4f2, (float)var64.x, (float)var64.y, (float)var64.z).color(var53, var54, var55, var56 / 2);
               var57.vertex(this.matrix4f2, (float)var63.x, (float)(var63.y + 0.1F), (float)var63.z).color(var53, var54, var55, var56 / 2);
            }

            BufferRenderer.drawWithGlobalProgram(var57.end());
         }

         matrices.pop();
      }
   }

   private ItemStack helper26() {
      ItemStack var1 = mc.player.getMainHandStack();
      if (!var1.isEmpty() && this.helper27(var1) != null) {
         return var1;
      }

      ItemStack var2 = mc.player.getOffHandStack();
      return !var2.isEmpty() && this.helper27(var2) != null ? var2 : ItemStack.EMPTY;
   }

   private Trajectories.ProjectileParams helper27(ItemStack stack) {
      Item var2 = stack.getItem();
      if (var2 == Items.ENDER_PEARL || var2 == Items.SNOWBALL || var2 == Items.EGG) {
         return new Trajectories.ProjectileParams(1.5, 0.03, 0.99);
      }

      if (var2 == Items.SPLASH_POTION || var2 == Items.LINGERING_POTION) {
         return new Trajectories.ProjectileParams(0.5, 0.05, 0.99);
      }

      if (var2 instanceof BowItem) {
         float var3 = 1.0F;
         if (mc.player.isUsingItem() && mc.player.getActiveItem() == stack) {
            float var4 = mc.player.getItemUseTime();
            float var5 = var4 / 20.0F;
            var5 = (var5 * var5 + var5 * 2.0F) / 3.0F;
            var3 = Math.min(var5, 1.0F);
         }

         double var6 = 3.0 * var3;
         return var6 <= 0.01 ? null : new Trajectories.ProjectileParams(var6, 0.05, 0.99);
      } else if (var2 instanceof CrossbowItem) {
         return !CrossbowItem.isCharged(stack) ? null : new Trajectories.ProjectileParams(3.15, 0.05, 0.99);
      } else {
         return var2 instanceof TridentItem ? new Trajectories.ProjectileParams(2.5, 0.05, 0.99) : null;
      }
   }

   private Trajectories.PredictionResult helper28(PlayerEntity player, Trajectories.ProjectileParams params, Vec3d startPos, Vec3d direction) {
      Vec3d var5 = startPos;
      Vec3d var6 = direction.normalize().multiply(params.velocity());
      Vec3d[] var7 = new Vec3d[441];
      int var8 = 0;
      var7[var8++] = var5;
      Entity var9 = null;
      Vec3d var10 = null;

      for (int var11 = 0; var11 < 440; var11++) {
         Vec3d var12 = var5;
         Vec3d var13 = var5.add(var6.multiply(0.5));
         if (var9 == null) {
            Trajectories.EntityHit var14 = this.helper30(var12, var13, player);
            if (var14 != null) {
               var9 = var14.entity();
               var10 = var14.hitPos();
            }
         }

         BlockHitResult var21 = mc.world.raycast(new RaycastContext(var12, var13, ShapeType.COLLIDER, FluidHandling.NONE, player));
         if (var21.getType() == Type.BLOCK) {
            var7[var8++] = var21.getPos();
            return new Trajectories.PredictionResult(this.helper29(var7, var8), var21, var21.getPos(), var9, var10);
         }

         var7[var8++] = var13;
         var5 = var13;
         boolean var15 = mc.world.getBlockState(BlockPos.ofFloored(var5)).isOf(Blocks.WATER);
         double var16 = Math.pow(var15 ? 0.8 : params.drag(), 0.5);
         var6 = var6.multiply(var16).subtract(0.0, params.gravity() * 0.5, 0.0);
         if (var5.y <= mc.world.getBottomY()) {
            break;
         }
      }

      Vec3d var20 = var10 != null ? var10 : var7[var8 - 1];
      return new Trajectories.PredictionResult(this.helper29(var7, var8), null, var20, var9, var10);
   }

   private Vec3d[] helper29(Vec3d[] points, int count) {
      Vec3d[] var3 = new Vec3d[count];
      System.arraycopy(points, 0, var3, 0, count);
      return var3;
   }

   private Trajectories.EntityHit helper30(Vec3d from, Vec3d to, Entity owner) {
      Box var4 = new Box(from, to).expand(1.0);
      Entity var5 = null;
      Vec3d var6 = null;
      double var7 = Double.MAX_VALUE;

      for (Entity var10 : mc.world.getOtherEntities(owner, var4, entity -> entity != null && entity.isAlive() && entity.canHit())) {
         Optional var11 = var10.getBoundingBox().expand(0.3).raycast(from, to);
         if (!var11.isEmpty()) {
            double var12 = from.squaredDistanceTo((Vec3d)var11.get());
            if (var12 < var7) {
               var7 = var12;
               var5 = var10;
               var6 = (Vec3d)var11.get();
            }
         }
      }

      return var5 == null ? null : new Trajectories.EntityHit(var5, var6);
   }

   private void helper31(MatrixStack matrices, Vec3d pos, int themeColor) {
      int var4 = ColorUtils.setAlphaColor(themeColor, 82);
      int var5 = var4 >> 16 & 0xFF;
      int var6 = var4 >> 8 & 0xFF;
      int var7 = var4 & 0xFF;
      int var8 = var4 >> 24 & 0xFF;
      float var9 = 4.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, TEXTURE_ID3);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      matrices.push();
      matrices.translate(pos.x, pos.y + 0.012, pos.z);
      Matrix4f var10 = matrices.peek().getPositionMatrix();
      BufferBuilder var11 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var11.vertex(var10, -var9, 0.0F, -var9).texture(0.0F, 0.0F).color(var5, var6, var7, var8);
      var11.vertex(var10, -var9, 0.0F, var9).texture(0.0F, 1.0F).color(var5, var6, var7, var8);
      var11.vertex(var10, var9, 0.0F, var9).texture(1.0F, 1.0F).color(var5, var6, var7, var8);
      var11.vertex(var10, var9, 0.0F, -var9).texture(1.0F, 0.0F).color(var5, var6, var7, var8);
      BufferRenderer.drawWithGlobalProgram(var11.end());
      matrices.pop();
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
   }

   private List<Vec3d> helper32(EnderPearlEntity pearl, float tickDelta) {
      return this.helper33(pearl, 0.03, tickDelta);
   }

   private List<Vec3d> helper33(Entity entity, double gravity, float tickDelta) {
      ArrayList var5 = new ArrayList();
      Vec3d var6 = new Vec3d(
         MathHelper.lerp(tickDelta, entity.prevX, entity.getX()),
         MathHelper.lerp(tickDelta, entity.prevY, entity.getY()),
         MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ())
      );
      Vec3d var7 = entity.getVelocity();
      var5.add(var6);

      for (int var8 = 0; var8 < 300; var8++) {
         Vec3d var9 = var6;
         Vec3d var10 = var6.add(var7);
         BlockHitResult var11 = mc.world.raycast(new RaycastContext(var9, var10, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
         if (var11.getType() == Type.BLOCK) {
            var5.add(var11.getPos());
            break;
         }

         var5.add(var10);
         var6 = var10;
         boolean var12 = mc.world.getBlockState(BlockPos.ofFloored(var6)).isOf(Blocks.WATER);
         double var13 = var12 ? 0.8 : 0.99;
         var7 = var7.multiply(var13).subtract(0.0, gravity, 0.0);
         if (var6.y <= mc.world.getBottomY()) {
            break;
         }
      }

      return var5;
   }

   private static class ActiveTrapZone {
      final Trajectories.CustomZoneType type;
      final Vec3d center;
      final UUID activatorId;
      int remainingTicks;

      ActiveTrapZone(Trajectories.CustomZoneType type, Vec3d center, UUID activatorId, int remainingTicks) {
         this.type = type;
         this.center = center;
         this.activatorId = activatorId;
         this.remainingTicks = remainingTicks;
      }
   }

   private enum CustomZoneType {
      STUN(Items.NETHER_STAR, "стан", "Стан", 300, "HolyWorld"),
      EXPLOSIVE_TRAP(Items.PRISMARINE_SHARD, "взрывная трапка", "Взрывная трапка", 220, "HolyWorld"),
      TRAP(Items.POPPED_CHORUS_FRUIT, "трапка", "Трапка", 220, "HolyWorld"),
      SPOOKY_TRAP(Items.NETHERITE_SCRAP, "трапка", "Трапка", 300, "SpookyTime"),
      SPOOKY_PLAST_VERTICAL(Items.DRIED_KELP, "пласт", "Пласт (верт)", 400, "SpookyTime"),
      SPOOKY_PLAST_HORIZONTAL(Items.DRIED_KELP, "пласт", "Пласт (гор)", 1200, "SpookyTime");

      private final Item TEXTURE_ID;
      private final String TEXTURE_ID2;
      private final String modeSetting;
      private final int floatSetting;
      private final String INDEX;

      CustomZoneType(Item item, String keyword, String displayName, int durationTicks, String serverMode) {
         this.TEXTURE_ID = item;
         this.TEXTURE_ID2 = keyword;
         this.modeSetting = displayName;
         this.floatSetting = durationTicks;
         this.INDEX = serverMode;
      }

      private boolean resolveString(ItemStack stack, String currentServerMode) {
         if (stack == null || stack.isEmpty() || !stack.isOf(this.TEXTURE_ID)) {
            return false;
         } else {
            return !this.INDEX.equals(currentServerMode)
               ? false
               : Trajectories.resolveString2(stack.getName().getString()).contains(this.TEXTURE_ID2);
         }
      }

      private static Trajectories.CustomZoneType helper(ItemStack stack, String serverMode) {
         for (Trajectories.CustomZoneType var5 : values()) {
            if (var5.resolveString(stack, serverMode)) {
               return var5;
            }
         }

         return null;
      }
   }

   private record EntityHit(Entity entity, Vec3d hitPos) {

      private EntityHit(Entity entity, Vec3d hitPos) {
         this.entity = entity;
         this.hitPos = hitPos;
      }

      public Entity entity() {
         return this.entity;
      }

      public Vec3d hitPos() {
         return this.hitPos;
      }
   }

   public record ImpactRenderInfo(Vec3d pos, float seconds, Item icon) {

      public ImpactRenderInfo(Vec3d pos, float seconds, Item icon) {
         this.pos = pos;
         this.seconds = seconds;
         this.icon = icon;
      }

      public Vec3d pos() {
         return this.pos;
      }

      public float seconds() {
         return this.seconds;
      }

      public Item icon() {
         return this.icon;
      }
   }

   private static class PearlGhost {
      final float angleOffset;
      final float speed;
      final float baseRadius;
      final float yOffset;
      final float spiralOffset;
      float currentAngle;
      float verticalOscillation;
      private static final double TEXTURE_ID = Math.toRadians(55.0);
      private static final float TEXTURE_ID2 = 0.32F;
      private static final float modeSetting = 2.2F;

      PearlGhost(int index, int total) {
         this.angleOffset = (float)((Math.PI * 2) * index / total);
         this.speed = 2.8F + (float)Math.random() * 0.4F;
         this.baseRadius = 0.22F + (float)Math.random() * 0.08F;
         this.yOffset = -0.15F + (float)Math.random() * 0.3F;
         this.spiralOffset = (float)(Math.random() * Math.PI * 2.0);
         this.currentAngle = (float)(Math.random() * Math.PI * 2.0);
         this.verticalOscillation = 0.0F;
      }

      void update(float delta, float time) {
         this.currentAngle = this.currentAngle + this.speed * delta;
         this.verticalOscillation = this.verticalOscillation + delta * (1.2F + (float)Math.random() * 0.3F);
      }

      Vec3d getPosition(Vec3d center, float time) {
         double var3 = this.angleOffset + time * 2.2F;
         double var5 = Math.cos(var3) * 0.32F;
         double var7 = Math.sin(var3) * 0.32F;
         double var9 = var5;
         double var11 = var7 * Math.cos(TEXTURE_ID);
         double var13 = var7 * Math.sin(TEXTURE_ID);
         return center.add(var9, var11, var13);
      }
   }

   private static class PlayerCustomSnapshot {
      final Map<Trajectories.CustomZoneType, Integer> counts;
      final Trajectories.CustomZoneType mainHandType;
      final Trajectories.CustomZoneType offHandType;
      final int mainHandCount;
      final int offHandCount;

      PlayerCustomSnapshot(
         Map<Trajectories.CustomZoneType, Integer> counts,
         Trajectories.CustomZoneType mainHandType,
         Trajectories.CustomZoneType offHandType,
         int mainHandCount,
         int offHandCount
      ) {
         this.counts = counts;
         this.mainHandType = mainHandType;
         this.offHandType = offHandType;
         this.mainHandCount = mainHandCount;
         this.offHandCount = offHandCount;
      }

      int getCount(Trajectories.CustomZoneType type) {
         return this.counts.getOrDefault(type, 0);
      }

      boolean isHolding(Trajectories.CustomZoneType type) {
         return this.mainHandType == type || this.offHandType == type;
      }
   }

   private record PredictionResult(Vec3d[] points, BlockHitResult blockHit, Vec3d hitPos, Entity entityHit, Vec3d entityHitPos) {

      private PredictionResult(Vec3d[] points, BlockHitResult blockHit, Vec3d hitPos, Entity entityHit, Vec3d entityHitPos) {
         this.points = points;
         this.blockHit = blockHit;
         this.hitPos = hitPos;
         this.entityHit = entityHit;
         this.entityHitPos = entityHitPos;
      }

      public Vec3d[] points() {
         return this.points;
      }

      public BlockHitResult blockHit() {
         return this.blockHit;
      }

      public Vec3d hitPos() {
         return this.hitPos;
      }

      public Entity entityHit() {
         return this.entityHit;
      }

      public Vec3d entityHitPos() {
         return this.entityHitPos;
      }
   }

   private record ProjectileParams(double velocity, double gravity, double drag) {

      private ProjectileParams(double velocity, double gravity, double drag) {
         this.velocity = velocity;
         this.gravity = gravity;
         this.drag = drag;
      }

      public double velocity() {
         return this.velocity;
      }

      public double gravity() {
         return this.gravity;
      }

      public double drag() {
         return this.drag;
      }
   }

   private static class StunCube {
      final BlockPos center;
      final UUID activatorId;
      int remainingTicks;
      int dissolveTicks;

      StunCube(BlockPos center, UUID activatorId, int remainingTicks) {
         this.center = center;
         this.activatorId = activatorId;
         this.remainingTicks = remainingTicks;
         this.dissolveTicks = 0;
      }
   }

   private static class StunMarker {
      final Vec3d position;
      int remainingTicks;

      StunMarker(Vec3d position, int remainingTicks) {
         this.position = position;
         this.remainingTicks = remainingTicks;
      }
   }

   public record TrapRenderInfo(Vec3d center, Item iconItem, String displayName, float secondsLeft) {

      public TrapRenderInfo(Vec3d center, Item iconItem, String displayName, float secondsLeft) {
         this.center = center;
         this.iconItem = iconItem;
         this.displayName = displayName;
         this.secondsLeft = secondsLeft;
      }

      public Vec3d center() {
         return this.center;
      }

      public Item iconItem() {
         return this.iconItem;
      }

      public String displayName() {
         return this.displayName;
      }

      public float secondsLeft() {
         return this.secondsLeft;
      }
   }
}
