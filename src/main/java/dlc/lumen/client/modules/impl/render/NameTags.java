package dlc.lumen.client.modules.impl.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dlc.lumen.Lumen;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.events.implement.EventPacket;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.events.implement.EventTickPost;
import dlc.lumen.api.storages.implement.helpertstorages.Theme;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.item.NbtDumpManager;
import dlc.lumen.api.utils.render.ItemCircleIndicator;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import dlc.lumen.api.utils.render.font.ReplaceSymbols;
import dlc.lumen.api.utils.render.fonts.msdf.Font;
import dlc.lumen.api.utils.render.fonts.msdf.Fonts;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.impl.misc.NameProtect;
import dlc.lumen.client.modules.impl.render.base.InterfaceProcessing;
import dlc.lumen.client.modules.settings.implement.BooleanSetting;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import dlc.lumen.client.modules.settings.implement.ListSetting;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.social.GlobalSocialManager;
import dlc.lumen.client.ui.modern.ModernTheme;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class NameTags extends Module {
   public static NameTags INSTANCE = new NameTags();
   private static final float VOLUME = 0.0F;
   private static final int INDEX = 13;
   private static final int INDEX2 = -1;
   private static final int INDEX3 = -43691;
   private static final int INDEX4 = -11141291;
   private static final float VOLUME2 = 1.1F;
   private static final int INDEX5 = 204;
   private static final int INDEX6 = 11;
   private static final float VOLUME3 = 8.4F;
   private static final float VOLUME4 = 0.46F;
   private static final float VOLUME5 = 1.0F;
   private static final float VOLUME6 = 7.5F;
   private static final float VOLUME7 = 3.0F;
   private static final float VOLUME8 = 1.5F;
   private static final float VOLUME9 = 0.23F;
   private static final float VOLUME10 = 0.001F;
   private static final long TIMESTAMP = 1000L;
   private static final long TIMESTAMP2 = 2000L;
   private static final int INDEX7 = 48;
   private final ListSetting listSetting = new ListSetting("Элементы", new BooleanSetting("Теги", true), new BooleanSetting("Броня", true));
   private final BooleanSetting booleanSetting = new BooleanSetting("Показывать Сферы/Талики", true);
   private final BooleanSetting booleanSetting2 = new BooleanSetting("Теги над невидимками", true);
   private final BooleanSetting booleanSetting3 = new BooleanSetting("Боксы", true);
   private final BooleanSetting booleanSetting4 = new BooleanSetting("Заполнить бокс", true);
   private final ModeSetting modeSetting = new ModeSetting("Мод заливки", "Обычный", "Обычный", "Волны", "Нитки");
   private final FloatSetting floatSetting = new FloatSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F).visible(() -> this.modeSetting.is("Волны"));
   private final FloatSetting floatSetting2 = new FloatSetting("Размер волн", 1.0F, 1.0F, 3.0F, 0.1F).visible(() -> this.modeSetting.is("Волны"));
   private final FloatSetting floatSetting3 = new FloatSetting("Скорость линий", 1.4F, 0.1F, 5.0F, 0.1F)
      .visible(() -> this.modeSetting.getIndex() == 2);
   private final FloatSetting floatSetting4 = new FloatSetting("Прыжки линий", 0.55F, 0.0F, 1.5F, 0.01F)
      .visible(() -> this.modeSetting.getIndex() == 2);
   private final FloatSetting floatSetting5 = new FloatSetting("Обводка", 1.1F, 0.1F, 5.0F, 0.1F).visible(this::helper61);
   private final FloatSetting floatSetting6 = new FloatSetting("Свечение", 1.0F, 0.0F, 5.0F, 0.1F).visible(this::helper61);
   private final FloatSetting floatSetting7 = new FloatSetting("Сила заливки", 0.6F, 0.0F, 1.0F, 0.01F).visible(this::helper61);
   private final FloatSetting floatSetting8 = new FloatSetting("Прозрачность", 1.0F, 0.0F, 4.0F, 0.01F).visible(this::helper61);
   private final BooleanSetting booleanSetting5 = new BooleanSetting("Краснеть при ударе", true);
   private final BooleanSetting booleanSetting6 = new BooleanSetting("Используемый предмет", true);
   private final Matrix4f matrix4f = new Matrix4f();
   private final Quaternionf quaternionf = new Quaternionf();
   private final Quaternionf quaternionf2 = new Quaternionf();
   private Vec3d vec3d = Vec3d.ZERO;
   private float volume;
   private final HashSet<Integer> integers = new HashSet<>();
   private final Map<Integer, String> integers2 = new HashMap<>();
   private final Map<Integer, UUID> integers3 = new HashMap<>();
   private final Map<UUID, String> uUIDs = new HashMap<>();
   private final Map<Integer, Map<EquipmentSlot, ItemStack>> integers4 = new HashMap<>();
   private int index2;
   private int index3;
   private boolean flag;
   private Framebuffer framebuffer;
   private final List<Framebuffer> framebuffers = new ArrayList<>();
   private final Map<UUID, NameTags.DonateCache> uUIDs2 = new HashMap<>();
   private final Map<Integer, Float> integers5 = new HashMap<>();
   private long timestamp;
   private int index4 = -1;
   private int index5 = -1;
   private boolean flag2;
   private final Vector3f vector3f = new Vector3f();
   private final Vector4f vector4f = new Vector4f();
   private final NameTags.ProjectedPoint nameTags = new NameTags.ProjectedPoint();
   private final ItemStack[] itemStack = new ItemStack[6];
   private final boolean[] flag3 = new boolean[6];
   private int index6 = -1;
   private final BooleanSetting booleanSetting7 = new BooleanSetting("Игроки", true);
   private final BooleanSetting booleanSetting8 = new BooleanSetting("Мобы", true);
   private final BooleanSetting booleanSetting9 = new BooleanSetting("Животные", true);
   private final BooleanSetting booleanSetting10 = new BooleanSetting("Предметы", true);
   private final ListSetting listSetting2 = new ListSetting(
      "Отображать", this.booleanSetting7, this.booleanSetting8, this.booleanSetting9, this.booleanSetting10
   );
   private static final ScoreboardDisplaySlot[] SCOREBOARD_DISPLAY_SLOT = new ScoreboardDisplaySlot[]{ScoreboardDisplaySlot.BELOW_NAME, ScoreboardDisplaySlot.LIST};
   private final BooleanSetting entityFinderSetting = new BooleanSetting("EntityFinder", false);
   private final FloatSetting eFSetting = new FloatSetting("EF Время", 5.0F, 2.0F, 20.0F, 0.5F).visible(this.entityFinderSetting::isState);
   private final List<NameTags.EFEntity> nameTagss = new CopyOnWriteArrayList<>();
   private List<Entity> entitys = List.of();
   private long timestamp2 = -1L;
   private List<Entity> entitys2 = List.of();
   private long timestamp3 = -1L;

   public NameTags() {
      super("NameTags", "Показывает игроков через стену", Module.ModuleCategory.RENDER);
      this.addSettings(this.listSetting2, this.listSetting, this.booleanSetting, this.booleanSetting2);
      this.addSettings(this.booleanSetting3, this.booleanSetting4, this.booleanSetting5, this.booleanSetting6);
      this.addSettings(this.entityFinderSetting, this.eFSetting);
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.flag2 = false;
      this.uUIDs2.clear();
      this.integers5.clear();
      this.nameTagss.clear();
      this.timestamp = 0L;
      if (this.framebuffer != null) {
         this.framebuffer.delete();
         this.framebuffer = null;
      }

      for (Framebuffer var2 : this.framebuffers) {
         var2.delete();
      }

      this.framebuffers.clear();
      super.onDisable();
   }

   private List<Entity> helper() {
      long var1 = mc.world.getTime() / 3L;
      if (var1 != this.timestamp2) {
         this.timestamp2 = var1;
         ArrayList var3 = new ArrayList();

         for (Entity var5 : mc.world.getEntities()) {
            if (this.helper38(var5)) {
               var3.add(var5);
            }
         }

         this.entitys = var3;
      }

      return this.entitys;
   }

   private List<Entity> helper2() {
      long var1 = mc.world.getTime() / 3L;
      if (var1 != this.timestamp3) {
         this.timestamp3 = var1;
         ArrayList var3 = new ArrayList();

         for (Entity var5 : mc.world.getEntities()) {
            if (var5 instanceof PlayerEntity var6) {
               if (this.helper39(var6)) {
                  var3.add(var5);
               }
            } else if (var5 instanceof ItemEntity var7) {
               if (this.helper41(var7)) {
                  var3.add(var5);
               }
            } else if (var5 instanceof LivingEntity var8 && this.helper40(var8)) {
               var3.add(var5);
            }
         }

         this.entitys2 = var3;
      }

      return this.entitys2;
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      this.flag = true;
      this.matrix4f.set(event.getProjectionMatrix());
      this.vec3d = event.getCamera().getPos();
      this.quaternionf.set(event.getCamera().getRotation());
      this.quaternionf2.set(this.quaternionf).conjugate();
      this.volume = event.getTickDelta();
      this.index2 = mc.getWindow().getScaledWidth();
      this.index3 = mc.getWindow().getScaledHeight();
      this.index6 = this.helper64();
      this.flag2 = false;
      if (this.booleanSetting3.isState() && mc.world != null && mc.player != null) {
         MatrixStack var2 = event.getMatrices();
         float var3 = event.getTickDelta();
         boolean var4 = this.helper61();
         boolean var5 = this.helper62();
         if (var4) {
            this.helper53();
            if (this.framebuffer != null) {
               this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               this.framebuffer.clear();
               this.helper55();
               this.framebuffer.beginWrite(false);
               RenderSystem.disableBlend();
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.disableCull();
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            }
         }

         for (Entity var7 : this.helper()) {
            if (this.helper38(var7)) {
               if (var4 && this.framebuffer != null) {
                  this.helper49(var2, var7, var3);
                  this.flag2 = true;
               } else {
                  this.helper47(var2, var7, var3);
               }
            }
         }

         if (var4 && this.framebuffer != null) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            mc.getFramebuffer().beginWrite(true);
            if (this.booleanSetting3.isState()) {
               this.helper51();
            }
         }

         if (var5) {
            for (Entity var9 : this.helper()) {
               if (this.helper38(var9)) {
                  this.helper65(var2, var9, var3);
               }
            }
         }
      }
   }

   @EventLink(priority = 100)
   public void onRender2D(EventRender.Default event) {
      if (this.flag && mc.world != null && mc.player != null) {
         this.index6 = this.helper64();
         boolean var2 = !this.listSetting.getSettings().isEmpty() && this.listSetting.getSettings().get(0).isState();
         boolean var3 = this.listSetting.getSettings().size() > 1 && this.listSetting.getSettings().get(1).isState();
         boolean var4 = this.booleanSetting.isState();
         if (var2 || var3 || var4 || this.booleanSetting6.isState()) {
            Font var5 = var2 ? Fonts.getFont("sf_regular", 13) : null;
            int var6 = 0;
            this.integers.clear();

            for (Entity var8 : this.helper2()) {
               if (var8 instanceof PlayerEntity var9) {
                  if (this.helper39(var9)) {
                     Box var10 = this.helper34(var9, this.volume);
                     NameTags.ScreenRect var11 = this.helper35(var10);
                     if (var11 != null) {
                        if (var2 && var5 != null) {
                           this.helper9(event, var9, var11, var5);
                        }

                        if (var3) {
                           this.helper10(event, var9, var11, var2);
                        }

                        if (var4) {
                           this.helper11(event, var9, var11, var2, var3);
                        }

                        if (this.booleanSetting6.isState()) {
                           this.helper8(event, var9, var11);
                        }

                        this.integers.add(var9.getId());
                     }
                  }
               } else if (var2 && var5 != null) {
                  if (var8 instanceof ItemEntity var14) {
                     if (this.helper41(var14) && var6 < 48 && this.helper37(var14, var14.getHeight() + 0.25, this.nameTags)) {
                        this.helper13(event, var14, this.nameTags.VOLUME, this.nameTags.INDEX, var5);
                        var6++;
                     }
                  } else if (var8 instanceof LivingEntity var15 && this.helper40(var15)) {
                     Box var17 = this.helper34(var15, this.volume);
                     NameTags.ScreenRect var18 = this.helper35(var17);
                     if (var18 != null) {
                        this.helper12(event, var15, var18, var5);
                     }
                  }
               }
            }

            if (this.entityFinderSetting.isState() && !this.nameTagss.isEmpty() && var2 && var5 != null) {
               for (NameTags.EFEntity var13 : this.nameTagss) {
                  if (!(var13.progress <= 0.01F) && !this.integers.contains(var13.id)) {
                     if (mc.world != null) {
                        Entity var16 = mc.world.getEntityById(var13.id);
                        if (var16 != null) {
                           var13.x = var16.getX();
                           var13.y = var16.getY();
                           var13.z = var16.getZ();
                        }
                     }

                     this.helper4(event, var13, var5);
                     if (var3) {
                        this.helper6(event, var13, var5);
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink(priority = 100)
   public void onEFPacket(EventPacket event) {
      if (!this.entityFinderSetting.isState()) {
         this.nameTagss.clear();
      } else if (event.getPacket() instanceof EntityPositionSyncS2CPacket var2) {
         if (mc.world != null) {
            Entity var8 = mc.world.getEntityById(var2.id());
            if (var8 == null || var8 instanceof PlayerEntity) {
               if (var8 instanceof PlayerEntity var4) {
                  this.integers2.put(var4.getId(), var4.getNameForScoreboard());
                  this.integers3.put(var4.getId(), var4.getUuid());
               }

               Vec3d var9 = var2.values().position();

               for (NameTags.EFEntity var6 : this.nameTagss) {
                  if (var6.id == var2.id()) {
                     var6.updatePos(var9.x, var9.y, var9.z);
                     if (var8 instanceof PlayerEntity) {
                        var6.lastSeen = System.currentTimeMillis();
                     }

                     return;
                  }
               }

               String var10 = this.integers2.get(var2.id());
               if (var10 == null) {
                  UUID var11 = this.integers3.get(var2.id());
                  if (var11 != null) {
                     var10 = this.uUIDs.get(var11);
                  }
               }

               if (var10 == null) {
                  var10 = var8 instanceof PlayerEntity var12 ? var12.getNameForScoreboard() : "Player";
               }

               NameTags.EFEntity var13 = new NameTags.EFEntity(var2.id(), var9.x, var9.y, var9.z, var10);
               Map var7 = this.integers4.get(var2.id());
               if (var7 != null) {
                  var13.equipment.putAll(var7);
               }

               this.nameTagss.add(var13);
            }
         }
      }
   }

   @EventLink
   public void onEFEquipment(EventPacket event) {
      if (this.entityFinderSetting.isState()) {
         if (event.getPacket() instanceof EntityEquipmentUpdateS2CPacket var2) {
            for (NameTags.EFEntity var4 : this.nameTagss) {
               if (var4.id == var2.getEntityId()) {
                  var4.equipment.clear();

                  for (Pair var6 : var2.getEquipmentList()) {
                     var4.equipment.put((EquipmentSlot)var6.getFirst(), (ItemStack)var6.getSecond());
                  }

                  this.integers4.put(var4.id, new EnumMap<>(var4.equipment));
                  return;
               }
            }
         }
      }
   }

   @EventLink
   public void onEFPlayerList(EventPacket event) {
      if (this.entityFinderSetting.isState()) {
         if (event.getPacket() instanceof PlayerListS2CPacket var2) {
            for (Entry var4 : var2.getEntries()) {
               GameProfile var5 = var4.profile();
               if (var5 != null && var5.getId() != null && var5.getName() != null) {
                  this.uUIDs.put(var5.getId(), var5.getName());
               }
            }
         }
      }
   }

   @EventLink
   public void onEFTick(EventTickPost event) {
      if (!this.entityFinderSetting.isState()) {
         this.nameTagss.clear();
      } else {
         if (mc.world != null) {
            boolean var2 = mc.world.getTime() % 20L == 0L;

            for (PlayerEntity var4 : mc.world.getPlayers()) {
               this.integers2.put(var4.getId(), var4.getNameForScoreboard());
               this.integers3.put(var4.getId(), var4.getUuid());
               if (var2) {
                  EnumMap var5 = new EnumMap<>(EquipmentSlot.class);
                  var5.put(EquipmentSlot.MAINHAND, var4.getMainHandStack().copy());
                  var5.put(EquipmentSlot.OFFHAND, var4.getOffHandStack().copy());
                  int var6 = 0;

                  for (ItemStack var8 : var4.getArmorItems()) {
                     EquipmentSlot var9 = var6 == 0
                        ? EquipmentSlot.FEET
                        : (var6 == 1 ? EquipmentSlot.LEGS : (var6 == 2 ? EquipmentSlot.CHEST : EquipmentSlot.HEAD));
                     var5.put(var9, var8.copy());
                     var6++;
                  }

                  this.integers4.put(var4.getId(), var5);
               }
            }

            if (var2 && mc.getNetworkHandler() != null) {
               for (PlayerListEntry var12 : mc.getNetworkHandler().getPlayerList()) {
                  this.uUIDs.put(var12.getProfile().getId(), var12.getProfile().getName());
               }
            }
         }

         long var10 = System.currentTimeMillis();
         float var13 = this.eFSetting.get() * 1000.0F;
         this.nameTagss.removeIf(ef -> {
            if (mc.world != null) {
               Entity var4x = mc.world.getEntityById(ef.id);
               if (var4x != null && !(var4x instanceof PlayerEntity)) {
                  return true;
               }

               if (var4x instanceof PlayerEntity var5x) {
                  ef.name = var5x.getNameForScoreboard();
                  ef.x = var5x.getX();
                  ef.y = var5x.getY();
                  ef.z = var5x.getZ();
                  ef.lastSeen = var10;
               }
            }

            long var6x = var10 - ef.bornAt;
            if (!ef.fading && (float)var6x > var13) {
               ef.fading = true;
            }

            if (ef.fading) {
               ef.progress = Math.max(0.0F, ef.progress - 0.15F);
            } else {
               ef.progress = Math.min(1.0F, ef.progress + 0.15F);
            }

            return ef.fading && ef.progress <= 0.0F;
         });
      }
   }

   private float helper3(String playerName) {
      if (mc.world != null) {
         Scoreboard var2 = mc.world.getScoreboard();
         ScoreHolder var3 = ScoreHolder.fromName(playerName);

         for (ScoreboardDisplaySlot var7 : SCOREBOARD_DISPLAY_SLOT) {
            ScoreboardObjective var8 = var2.getObjectiveForSlot(var7);
            if (var8 != null) {
               ReadableScoreboardScore var9 = var2.getScore(var3, var8);
               if (var9 != null && var9.getScore() > 0) {
                  return var9.getScore();
               }
            }
         }
      }

      return 20.0F;
   }

   private void helper4(EventRender.Default event, NameTags.EFEntity ef, Font font) {
      NameTags.ScreenRect var4 = this.helper5(ef);
      if (var4 != null) {
         MatrixStack var5 = event.getContext().getMatrices();
         double var6 = Math.sqrt(
            Math.pow(ef.x - this.vec3d.x, 2.0) + Math.pow(ef.y - this.vec3d.y, 2.0) + Math.pow(ef.z - this.vec3d.z, 2.0)
         );
         float var8 = var6 <= 8.0 ? 1.0F : (float)MathHelper.clamp(8.0 / var6, 0.4, 1.0);
         this.helper16(var5, var4.centerX(), var4.minY(), var8);
         float var9 = this.helper3(ef.name);
         String var10 = this.helper25(ef.name);
         String var11 = Math.round(var9) + " hp";
         boolean var12 = Lumen.INSTANCE.friendStorage != null && Lumen.INSTANCE.friendStorage.isFriend(ef.name);
         String var13 = var12 ? " [F]" : "";
         UUID var14 = this.integers3.get(ef.id);
         boolean var15 = var14 != null;
         float var16 = font.getStringWidth(var10);
         float var17 = font.getStringWidth(var11);
         float var18 = font.getStringWidth(var13);
         float var19 = font.getStringWidth(" ");
         float var20 = (var15 ? 10.5F : 0.0F) + var16 + var17 + var18 + 4.0F;
         float var21 = 16.0F;
         float var22 = var4.centerX() - var20 * 0.5F;
         float var23 = this.helper17(var4, var21);
         int var24 = Math.max(20, (int)(255.0F * ef.progress));
         InterfaceProcessing.drawHudBg(var5, var22 - 1.0F, var23 - 0.5F, var20 + 2.0F, var21 - 4.0F);
         float var25 = var22;
         if (var15) {
            float var26 = var23 + 1.7F;
            RenderUtils.drawPlayerHead(var5, var14, var25 + 1.0F, var26, 7.5F, 1.0F, 1.0F, 0.0F);
            var25 += 12.0F;
         }

         font.drawString(var5, var10, var25, var23 + 4.0F, ColorUtils.setAlphaColor(-1, var24));
         var25 += var16;
         font.drawString(var5, " ", var25, var23 + 4.0F, ColorUtils.setAlphaColor(-1, var24));
         var25 += var19;
         font.drawString(var5, var11, var25, var23 + 4.0F, ColorUtils.setAlphaColor(-43691, var24));
         var25 += var17;
         if (var12) {
            font.drawString(var5, var13, var25, var23 + 4.0F, ColorUtils.setAlphaColor(-11141291, var24));
         }

         var5.pop();
      }
   }

   private NameTags.ScreenRect helper5(NameTags.EFEntity ef) {
      Entity var2 = mc.world != null ? mc.world.getEntityById(ef.id) : null;
      if (var2 != null) {
         Box var4 = this.helper34(var2, this.volume);
         return this.helper35(var4);
      } else {
         Box var3 = new Box(ef.x - 0.3, ef.y, ef.z - 0.3, ef.x + 0.3, ef.y + 1.8, ef.z + 0.3);
         return this.helper35(var3);
      }
   }

   private void helper6(EventRender.Default event, NameTags.EFEntity ef, Font font) {
      NameTags.ScreenRect var4 = this.helper5(ef);
      if (var4 != null) {
         Map<EquipmentSlot, ItemStack> var5 = ef.equipment.isEmpty() ? this.integers4.get(ef.id) : ef.equipment;
         if (var5 != null && !var5.isEmpty()) {
            int var6 = 0;
            ItemStack var7 = var5.getOrDefault(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            if (!var7.isEmpty()) {
               this.itemStack[var6] = var7;
               this.flag3[var6++] = true;
            }

            for (EquipmentSlot var11 : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
               ItemStack var12 = var5.getOrDefault(var11, ItemStack.EMPTY);
               if (!var12.isEmpty()) {
                  this.itemStack[var6] = var12;
                  this.flag3[var6++] = false;
               }
            }

            ItemStack var26 = var5.getOrDefault(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            if (!var26.isEmpty()) {
               this.itemStack[var6] = var26;
               this.flag3[var6++] = true;
            }

            if (var6 != 0) {
               MatrixStack var27 = event.getContext().getMatrices();
               double var28 = Math.sqrt(
                  Math.pow(ef.x - this.vec3d.x, 2.0) + Math.pow(ef.y - this.vec3d.y, 2.0) + Math.pow(ef.z - this.vec3d.z, 2.0)
               );
               float var29 = var28 <= 8.0 ? 1.0F : (float)MathHelper.clamp(8.0 / var28, 0.4, 1.0);
               this.helper16(var27, var4.centerX(), var4.minY(), var29);
               float var13 = 9.4F;
               float var14 = var6 * 8.4F + Math.max(0, var6 - 1) * 1.0F;
               float var15 = var4.centerX() - var14 * 0.5F;
               float var16 = this.helper18(var4, true);
               int var17 = Math.max(20, (int)(255.0F * ef.progress));

               for (int var18 = 0; var18 < var6; var18++) {
                  float var19 = var15 + var18 * var13;
                  InterfaceProcessing.drawHudBg(var27, var19, var16, 8.4F, 8.4F);
               }

               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);

               for (int var30 = 0; var30 < var6; var30++) {
                  float var32 = var15 + var30 * var13;
                  int var20 = var6 - 1 - var30;
                  ItemStack var21 = this.itemStack[var20];
                  boolean var22 = this.flag3[var20];
                  var27.push();
                  float var23 = 7.36F;
                  float var24 = var32 + (8.4F - var23) * 0.5F;
                  float var25 = var16 + (8.4F - var23) * 0.5F;
                  var27.translate(var24, var25, 0.0F);
                  var27.scale(0.46F, 0.46F, 1.0F);
                  event.getContext().drawItem(var21, 0, 0);
                  if (!var22) {
                     event.getContext().drawStackOverlay(mc.textRenderer, var21, 0, 0, null);
                  }

                  var27.pop();
               }

               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.disableBlend();

               for (int var31 = 0; var31 < var6; var31++) {
                  this.itemStack[var31] = ItemStack.EMPTY;
                  this.flag3[var31] = false;
               }

               var27.pop();
            }
         }
      }
   }

   private float helper7(PlayerEntity player) {
      if (mc.world != null) {
         Scoreboard var2 = mc.world.getScoreboard();
         ScoreHolder var3 = ScoreHolder.fromName(player.getNameForScoreboard());

         for (ScoreboardDisplaySlot var7 : SCOREBOARD_DISPLAY_SLOT) {
            ScoreboardObjective var8 = var2.getObjectiveForSlot(var7);
            if (var8 != null) {
               ReadableScoreboardScore var9 = var2.getScore(var3, var8);
               if (var9 != null && var9.getScore() > 0) {
                  return var9.getScore();
               }
            }
         }
      }

      return player.getHealth();
   }

   private boolean checkCondition(PlayerEntity player) {
      if (player == null) {
         return false;
      }

      String var2 = player.getNameForScoreboard();
      if (GlobalSocialManager.INSTANCE.getKnownNicknames().contains(var2)) {
         return true;
      }

      UUID var3 = player.getUuid();
      GlobalSocialManager.PartyMemberSnapshot var4 = GlobalSocialManager.INSTANCE.getPartyMember(var3);
      return var4 != null && var4.figuraId() != null && !var4.figuraId().isBlank();
   }

   private void helper8(EventRender.Default event, PlayerEntity player, NameTags.ScreenRect rect) {
      if (player.isUsingItem()) {
         ItemStack var4 = player.getActiveItem();
         if (var4 != null && !var4.isEmpty()) {
            UseAction var5 = var4.getUseAction();
            if (var5 == UseAction.EAT || var5 == UseAction.DRINK) {
               int var6 = var4.getMaxUseTime(player);
               if (var6 > 0) {
                  float var7 = MathHelper.clamp((float)player.getItemUseTimeLeft() / var6, 0.0F, 1.0F);
                  float var8 = rect.centerX();
                  float var9 = rect.centerY();
                  int var10 = 0xFF000000 | this.index6 & 16777215;
                  ItemCircleIndicator.draw(event.getContext(), var8, var9, 8.0F, var4, var7, var10);
               }
            }
         }
      }
   }

   private static void updateState(MatrixStack matrices, float x, float y, float h, int color) {
      float var5 = h * 0.22F;
      float var6 = h * 0.18F;
      float var7 = h;
      float var8 = h * 0.55F;
      float var9 = var5 * 0.5F;
      RenderUtils.drawRoundedRect(matrices, x + 1.0F + 0.5F, y + 1.0F - var7 / 2.0F, var5, var7 - 3.0F - 1.0F, var9, color);
      RenderUtils.drawRoundedRect(matrices, x + var5 + var6, y - var8 / 2.0F, var5, var8 + 2.0F - 1.0F, var9, color);
      RenderUtils.drawRoundedRect(matrices, x - 0.5F - 1.0F + 2.0F * (var5 + var6), y + 1.0F - var7 / 2.0F, var5, var7 - 3.0F - 1.0F, var9, color);
   }

   private void helper9(EventRender.Default event, PlayerEntity player, NameTags.ScreenRect rect, Font font) {
      MatrixStack var5 = event.getContext().getMatrices();
      this.helper16(var5, rect.centerX(), rect.minY(), this.helper15(player));
      boolean var6 = this.checkCondition(player);
      List<NameTags.DonateSegment> var7 = this.helper27(player);
      String var8 = this.helper25(player.getNameForScoreboard());
      float var9 = this.helper7(player);
      String var10 = "";
      String var11 = Math.round(var9) + " hp";
      String var12 = "";
      boolean var13 = Lumen.INSTANCE.friendStorage != null && Lumen.INSTANCE.friendStorage.isFriend(player.getName().getString());
      String var14 = var13 ? " [F]" : "";
      if (var13) {
         String var15 = Lumen.INSTANCE.friendStorage.getCustomName(player.getName().getString());
         if (var15 != null) {
            var8 = var15;
         }
      }

      if (var6) {
         float var29 = 15.0F;
         float var16 = 11.0F;
         float var17 = font.getStringWidth("Lumen");
         float var18 = var16 + 4.0F + var17 + 10.0F;
         float var19 = rect.centerX() - var18 / 2.0F;
         float var20 = this.helper17(rect, 16.0F) - var29 - 2.0F;
         InterfaceProcessing.drawHudBg(var5, var19 - 1.0F, var20 + 1.0F, var18 + 2.0F, var29 - 3.0F);
         float var21 = var20 + var29 / 2.0F;
         updateState(var5, var19 + 3.0F, var21, var16, ModernTheme.accent());
         float var22 = var19 + 3.0F + var16 + 4.0F;
         font.drawString(var5, "Lumen", var22, var20 + 3.5F + 2.0F, ModernTheme.accent());
      }

      float var30 = 0.0F;

      for (NameTags.DonateSegment var33 : var7) {
         var30 += font.getStringWidth(var33.text());
      }

      float var32 = font.getStringWidth(var8);
      float var34 = font.getStringWidth(var10);
      float var35 = font.getStringWidth(var11);
      float var36 = font.getStringWidth(var12);
      float var37 = font.getStringWidth(var14);
      float var38 = var30 + var32 + var34 + var35 + var36 + var37 + 7.5F + 3.0F + 2.0F;
      float var39 = 16.0F;
      float var23 = rect.centerX() - var38 * 0.5F;
      float var24 = this.helper17(rect, var39);
      InterfaceProcessing.drawHudBg(var5, var23 - 1.0F, var24 - 0.5F, var38 + 2.0F, var39 - 4.0F);
      float var25 = var24 + 1.7F;
      RenderUtils.drawPlayerHead(var5, player.getUuid(), var23 + 1.0F, var25, 7.5F, 1.0F, 1.0F, 0.0F);
      float var26 = var23 + 1.5F + 7.5F + 3.0F;

      for (NameTags.DonateSegment var28 : var7) {
         font.drawString(var5, var28.text(), var26, var24 + 4.0F, var28.color());
         var26 += font.getStringWidth(var28.text());
      }

      font.drawString(var5, var8, var26, var24 + 4.0F, -1);
      var26 += var32;
      font.drawString(var5, var10, var26, var24 + 4.0F, -1);
      var26 += var34;
      font.drawString(var5, var11, var26, var24 + 4.0F, -43691);
      var26 += var35;
      font.drawString(var5, var12, var26, var24 + 4.0F, -1);
      var26 += var36;
      if (var13) {
         font.drawString(var5, var14, var26, var24 + 4.0F, -11141291);
      }

      var5.pop();
   }

   private void helper10(EventRender.Default event, PlayerEntity player, NameTags.ScreenRect rect, boolean tagsEnabled) {
      MatrixStack var5 = event.getContext().getMatrices();
      int var6 = 0;
      ItemStack var7 = player.getOffHandStack();
      if (!var7.isEmpty()) {
         this.itemStack[var6] = var7;
         this.flag3[var6++] = true;
      }

      for (ItemStack var9 : player.getArmorItems()) {
         if (!var9.isEmpty()) {
            this.itemStack[var6] = var9;
            this.flag3[var6++] = false;
         }
      }

      ItemStack var22 = player.getMainHandStack();
      if (!var22.isEmpty()) {
         this.itemStack[var6] = var22;
         this.flag3[var6++] = true;
      }

      if (var6 != 0) {
         this.helper16(var5, rect.centerX(), rect.minY(), this.helper15(player));
         float var23 = 9.4F;
         float var10 = var6 * 8.4F + Math.max(0, var6 - 1) * 1.0F;
         float var11 = rect.centerX() - var10 * 0.5F;
         float var12 = this.helper18(rect, tagsEnabled);

         for (int var13 = 0; var13 < var6; var13++) {
            float var14 = var11 + var13 * var23;
            float var15 = var12;
            InterfaceProcessing.drawHudBg(var5, var14, var15, 8.4F, 8.4F);
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);

         for (int var24 = 0; var24 < var6; var24++) {
            float var26 = var11 + var24 * var23;
            float var27 = var12;
            int var16 = var6 - 1 - var24;
            ItemStack var17 = this.itemStack[var16];
            boolean var18 = this.flag3[var16];
            var5.push();
            float var19 = 7.36F;
            float var20 = var26 + (8.4F - var19) * 0.5F;
            float var21 = var27 + (8.4F - var19) * 0.5F;
            var5.translate(var20, var21, 0.0F);
            var5.scale(0.46F, 0.46F, 1.0F);
            event.getContext().drawItem(var17, 0, 0);
            if (!var18) {
               event.getContext().drawStackOverlay(mc.textRenderer, var17, 0, 0, null);
            }

            var5.pop();
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();

         for (int var25 = 0; var25 < var6; var25++) {
            this.itemStack[var25] = ItemStack.EMPTY;
            this.flag3[var25] = false;
         }

         var5.pop();
      }
   }

   private void helper11(EventRender.Default event, PlayerEntity player, NameTags.ScreenRect rect, boolean tagsEnabled, boolean armorEnabled) {
      List<NameTags.DonateSegment> var6 = this.helper19(player);
      if (!var6.isEmpty()) {
         MatrixStack var7 = event.getContext().getMatrices();
         this.helper16(var7, rect.centerX(), rect.maxY(), this.helper15(player));
         Font var8 = Fonts.getFont("sf_regular", 11);
         float var9 = 0.0F;

         for (NameTags.DonateSegment var11 : var6) {
            var9 += var8.getStringWidth(var11.text());
         }

         float var16 = 12.0F;
         float var17 = rect.centerX() - var9 * 0.5F;
         float var12 = rect.maxY() + 4.0F;
         InterfaceProcessing.drawHudBg(var7, var17 - 2.0F, var12 - 0.5F, var9 + 8.0F, var16);
         float var13 = var17;

         for (NameTags.DonateSegment var15 : var6) {
            var8.drawString(var7, var15.text(), var13 + 3.5F, var12 + 4.0F, var15.color());
            var13 += var8.getStringWidth(var15.text());
         }

         var7.pop();
      }
   }

   private void helper12(EventRender.Default event, LivingEntity entity, NameTags.ScreenRect rect, Font font) {
      MatrixStack var5 = event.getContext().getMatrices();
      this.helper16(var5, rect.centerX(), rect.minY(), this.helper15(entity));
      String var6 = entity instanceof PlayerEntity var7 ? this.helper25(var7.getDisplayName().getString()) : entity.getDisplayName().getString();
      float var16 = entity instanceof PlayerEntity var8 ? this.helper7(var8) : entity.getHealth();
      String var17 = Math.round(var16) + " hp";
      float var9 = font.getStringWidth(var6);
      float var10 = font.getStringWidth(" ");
      float var11 = font.getStringWidth(var17);
      float var12 = var9 + var10 + var11;
      float var13 = 14.0F;
      float var14 = rect.centerX() - var12 * 0.5F;
      float var15 = this.helper17(rect, var13);
      InterfaceProcessing.drawHudBg(var5, var14 - 1.0F, var15 - 0.5F, var12 + 2.0F, var13 - 4.0F);
      font.drawString(var5, var6, var14, var15 + 3.0F, -1);
      font.drawString(var5, var17, var14 + var9 + var10, var15 + 3.0F, -43691);
      var5.pop();
   }

   private void helper13(EventRender.Default event, ItemEntity itemEntity, float anchorX, float anchorY, Font font) {
      MatrixStack var6 = event.getContext().getMatrices();
      this.helper16(var6, anchorX, anchorY, this.helper15(itemEntity));
      ItemStack var7 = itemEntity.getStack();
      String var8 = var7.getCount() + "x";
      String var9 = NbtDumpManager.getCustomDumpedName(var7);
      Text var10 = var9 != null ? Text.literal(var9) : var7.getName();
      List<NameTags.DonateSegment> var11 = this.helper29(var10, this.helper14(var7));
      int var12 = ColorUtils.rgba(155, 155, 155, 255);
      float var13 = 0.0F;

      for (NameTags.DonateSegment var15 : var11) {
         var13 += font.getStringWidth(var15.text());
      }

      float var23 = font.getStringWidth(" ");
      float var24 = font.getStringWidth(var8);
      float var16 = var13 + var23 + var24;
      float var17 = 14.0F;
      float var18 = anchorX - var16 * 0.5F;
      float var19 = anchorY - var17 - 2.0F;
      InterfaceProcessing.drawHudBg(var6, var18 - 2.0F, var19 - 0.5F, var16 + 4.0F, var17 - 3.0F);
      float var20 = var18;

      for (NameTags.DonateSegment var22 : var11) {
         font.drawString(var6, var22.text(), var20, var19 + 3.5F, var22.color());
         var20 += font.getStringWidth(var22.text());
      }

      font.drawString(var6, var8, var20 + var23, var19 + 3.5F, var12);
      var6.pop();
   }

   private int resolveInt(ItemStack stack) {
      Text var2 = stack.getName();
      if (var2 != null) {
         int[] var3 = new int[]{0};
         boolean[] var4 = new boolean[]{false};
         var2.visit((style, string) -> {
            if (!var4[0] && style != null && style.getColor() != null) {
               var3[0] = 0xFF000000 | style.getColor().getRgb();
               var4[0] = true;
            }

            return var4[0] ? Optional.of(string) : Optional.empty();
         }, Style.EMPTY);
         if (var4[0]) {
            return var3[0];
         }
      }
      return switch (stack.getRarity()) {
         case UNCOMMON -> ColorUtils.rgba(255, 255, 85, 255);
         case RARE -> ColorUtils.rgba(85, 255, 255, 255);
         case EPIC -> ColorUtils.rgba(255, 85, 255, 255);
         case COMMON -> -1;
      };
   }

   private int helper14(ItemStack stack) {
      return this.resolveInt(stack);
   }

   private boolean checkCondition2(Item item) {
      return Registries.ITEM.getId(item).getPath().contains("netherite");
   }

   private float helper15(Entity entity) {
      double var2 = Math.sqrt(entity.squaredDistanceTo(this.vec3d));
      return var2 <= 8.0 ? 1.0F : (float)MathHelper.clamp(8.0 / var2, 0.4, 1.0);
   }

   private void helper16(MatrixStack matrices, float anchorX, float anchorY, float scale) {
      matrices.push();
      matrices.translate(anchorX, anchorY, 0.0F);
      matrices.scale(scale, scale, 1.0F);
      matrices.translate(-anchorX, -anchorY, 0.0F);
   }

   private void updateState2(MatrixStack matrices, float x, float y, float width, float height) {
      int var6 = this.index6;
      if (ClickGuiTheme.shadowEnabled()) {
         RenderUtils.drawShadow(matrices, x, y, width, height, 4.0F, 5.0F, ColorUtils.getThemeColor());
         RenderUtils.drawShadow(matrices, x, y, width, height, 4.0F, 5.0F, ColorUtils.rgba(0, 0, 0, 220));
      }

      RenderUtils.drawBlur(matrices, x, y, width, height, 2.0F, 5.0F, ColorUtils.getThemeColor());
      RenderUtils.drawBlur(matrices, x, y, width, height, 2.0F, 5.0F, ColorUtils.rgba(0, 0, 0, 160));
   }

   public boolean shouldHideVanillaTags() {
      return this.isEnable() && !this.listSetting.getSettings().isEmpty() && this.listSetting.getSettings().get(0).isState();
   }

   public boolean shouldHideVanillaLabel(Entity entity) {
      return this.shouldHideVanillaTags() && entity != null ? this.helper38(entity) : false;
   }

   private float helper17(NameTags.ScreenRect rect, float tagHeight) {
      return rect.minY() - tagHeight - 0.0F;
   }

   private float helper18(NameTags.ScreenRect rect, boolean tagsEnabled) {
      return tagsEnabled ? this.helper17(rect, 14.0F) - 13.0F : rect.minY() - 13.0F;
   }

   private List<NameTags.DonateSegment> helper19(PlayerEntity player) {
      ArrayList var2 = new ArrayList();
      ItemStack var3 = player.getEquippedStack(EquipmentSlot.HEAD);
      if (var3.isOf(Items.PLAYER_HEAD)) {
         this.helper20(var2, var3);
      }

      this.helper20(var2, player.getOffHandStack());
      return var2;
   }

   private void helper20(List<NameTags.DonateSegment> segments, ItemStack stack) {
      if (this.helper21(stack)) {
         if (!segments.isEmpty()) {
            segments.add(new NameTags.DonateSegment(" | ", ColorUtils.rgba(170, 170, 170, 255)));
         }

         String var3 = NbtDumpManager.getCustomDumpedName(stack);
         Text var4 = var3 != null ? Text.literal(var3) : stack.getName();
         segments.addAll(this.helper29(var4, this.resolveInt(stack)));
      }
   }

   private boolean helper21(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      } else if (stack.getItem() instanceof ArmorItem) {
         return false;
      } else {
         return NbtDumpManager.getCustomDumpedName(stack) == null || !this.helper22(stack) && !this.helper23(stack)
            ? this.helper22(stack) || this.helper23(stack)
            : true;
      }
   }

   private boolean helper22(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isOf(Items.PLAYER_HEAD);
   }

   private boolean helper23(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isOf(Items.TOTEM_OF_UNDYING);
   }

   private String[] helper24(PlayerEntity player) {
      String var2 = player.getGameProfile() != null ? player.getGameProfile().getName() : "";
      String var3 = player.getNameForScoreboard();
      String var4 = this.helper25(var3);
      String var5 = this.helper25(var2);
      String var6 = this.helper25(player.getName().getString());
      return new String[]{player.getName().getString(), var6, var3, var4, var2, var5};
   }

   private String helper25(String input) {
      NameProtect var2 = ModuleClass.INSTANCE != null ? ModuleClass.nameProtect : null;
      return var2 != null && var2.isEnable() ? var2.patch(input) : input;
   }

   private int resolveInt2(String text, String[] names) {
      if (text != null && !text.isEmpty() && names != null) {
         int var3 = -1;

         for (String var7 : names) {
            if (var7 != null && !var7.isEmpty()) {
               int var8 = this.helper26(text, var7);
               if (var8 >= 0 && (var3 == -1 || var8 < var3)) {
                  var3 = var8;
               }
            }
         }

         return var3;
      } else {
         return -1;
      }
   }

   private int helper26(String text, String search) {
      if (text != null && search != null && !search.isEmpty()) {
         int var3 = text.length() - search.length();

         for (int var4 = 0; var4 <= var3; var4++) {
            if (text.regionMatches(true, var4, search, 0, search.length())) {
               return var4;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void updateState3(List<NameTags.DonateSegment> segments, int maxLength) {
      int var3 = Math.max(0, maxLength);
      ArrayList var4 = new ArrayList();

      for (NameTags.DonateSegment var6 : segments) {
         if (var3 <= 0) {
            break;
         }

         String var7 = var6.text();
         if (var7.length() <= var3) {
            var4.add(var6);
            var3 -= var7.length();
         } else {
            var4.add(new NameTags.DonateSegment(var7.substring(0, var3), var6.color()));
            var3 = 0;
         }
      }

      segments.clear();
      segments.addAll(var4);
   }

   private List<NameTags.DonateSegment> helper27(PlayerEntity player) {
      long var2 = System.currentTimeMillis();
      NameTags.DonateCache var4 = this.uUIDs2.computeIfAbsent(player.getUuid(), uuid -> new NameTags.DonateCache());
      if (var2 < var4.INDEX) {
         return var4.VOLUME;
      }

      ArrayList<NameTags.DonateSegment> var5 = new ArrayList<>();
      if (mc.getNetworkHandler() == null) {
         var4.VOLUME = Collections.emptyList();
         var4.INDEX = var2 + 1000L;
         return var4.VOLUME;
      }

      PlayerListEntry var6 = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
      if (var6 == null) {
         var4.VOLUME = Collections.emptyList();
         var4.INDEX = var2 + 1000L;
         return var4.VOLUME;
      }

      Text var7 = var6.getDisplayName();
      if (var7 == null) {
         var7 = player.getDisplayName();
      }

      if (var7 == null) {
         var4.VOLUME = Collections.emptyList();
         var4.INDEX = var2 + 1000L;
         return var4.VOLUME;
      }

      String[] var8 = this.helper24(player);
      boolean[] var9 = new boolean[]{false};
      var7.visit((style, string) -> {
         if (!var9[0] && string != null && !string.isEmpty()) {
            String var6x = string.replace('\n', ' ').replace('\r', ' ');
            int var7x = this.resolveInt2(var6x, var8);
            String var8x = var7x >= 0 ? var6x.substring(0, var7x) : var6x;
            if (!var8x.isEmpty()) {
               int var9x = style.getColor() != null ? style.getColor().getRgb() : 16777215;
               this.helper30(var5, var8x, var9x);
            }

            if (var7x >= 0) {
               var9[0] = true;
            }

            return Optional.empty();
         } else {
            return Optional.empty();
         }
      }, Style.EMPTY);
      if (!var9[0]) {
         var5.clear();
         Team var10 = player.getScoreboardTeam();
         if (var10 != null && var10.getPrefix() != null) {
            this.helper28(var5, var10.getPrefix());
         }
      }

      if (var5.isEmpty()) {
         var4.VOLUME = Collections.emptyList();
         var4.INDEX = var2 + 1000L;
         this.helper33(var2);
         return var4.VOLUME;
      }

      StringBuilder var15 = new StringBuilder();

      for (NameTags.DonateSegment var12 : var5) {
         var15.append(var12.text());
      }

      int var16 = this.resolveInt2(var15.toString(), var8);
      if (var16 >= 0) {
         if (var16 == 0) {
            var4.VOLUME = Collections.emptyList();
            var4.INDEX = var2 + 1000L;
            this.helper33(var2);
            return var4.VOLUME;
         }

         this.updateState3(var5, var16);
      }

      if (var5.isEmpty()) {
         var4.VOLUME = Collections.emptyList();
         var4.INDEX = var2 + 1000L;
         this.helper33(var2);
         return var4.VOLUME;
      }

      StringBuilder var17 = new StringBuilder();

      for (NameTags.DonateSegment var14 : var5) {
         var17.append(var14.text());
      }

      if (var17.toString().trim().isEmpty()) {
         var4.VOLUME = Collections.emptyList();
         var4.INDEX = var2 + 1000L;
         this.helper33(var2);
         return var4.VOLUME;
      }

      NameTags.DonateSegment var18 = (NameTags.DonateSegment)var5.get(var5.size() - 1);
      if (!var18.text().endsWith(" ")) {
         var5.set(var5.size() - 1, new NameTags.DonateSegment(var18.text() + " ", var18.color()));
      }

      var4.VOLUME = List.copyOf(var5);
      var4.INDEX = var2 + 1000L;
      this.helper33(var2);
      return var4.VOLUME;
   }

   private void helper28(List<NameTags.DonateSegment> out, Text text) {
      text.visit((style, string) -> {
         if (string != null && !string.isEmpty()) {
            int var4 = style.getColor() != null ? style.getColor().getRgb() : 16777215;
            this.helper30(out, string.replace('\n', ' ').replace('\r', ' '), var4);
            return Optional.empty();
         } else {
            return Optional.empty();
         }
      }, Style.EMPTY);
   }

   private List<NameTags.DonateSegment> helper29(Text text, int fallbackColor) {
      ArrayList<NameTags.DonateSegment> var3 = new ArrayList<>();
      if (text != null) {
         this.helper28(var3, text);
      }

      if (var3.isEmpty() && text != null && !text.getString().isEmpty()) {
         var3.add(new NameTags.DonateSegment(text.getString(), fallbackColor));
      }

      return var3;
   }

   private void helper30(List<NameTags.DonateSegment> out, String text, int baseColor) {
      if (text != null && !text.isEmpty()) {
         text = text.replaceAll("&(?=[0-9a-fA-FK-ORk-or#])", "§");
         int var4 = baseColor;
         StringBuilder var5 = new StringBuilder();
         int var6 = var4;
         int var7 = 0;

         while (true) {
            int var8;
            int var9;
            while (true) {
               while (true) {
                  if (var7 >= text.length()) {
                     this.helper31(out, var5, var6);
                     return;
                  }

                  var8 = text.codePointAt(var7);
                  var9 = Character.charCount(var8);
                  if (var8 != 167 || var7 + var9 >= text.length() || text.charAt(var7 + var9) != '#' || var7 + var9 + 6 >= text.length()) {
                     break;
                  }

                  String var10 = text.substring(var7 + var9 + 1, var7 + var9 + 7);

                  try {
                     this.helper31(out, var5, var6);
                     var4 = 0xFF000000 | Integer.parseInt(var10, 16);
                     var6 = var4;
                     var7 += var9 + 7;
                  } catch (NumberFormatException var14) {
                     break;
                  }
               }

               if (var8 != 167 || var7 + var9 >= text.length() || Character.toLowerCase(text.charAt(var7 + var9)) != 'x' || var7 + 13 >= text.length()) {
                  break;
               }

               StringBuilder var17 = new StringBuilder();
               boolean var11 = true;

               for (int var12 = 0; var12 < 6; var12++) {
                  if (text.charAt(var7 + 2 + var12 * 2) != 167) {
                     var11 = false;
                     break;
                  }

                  var17.append(text.charAt(var7 + 3 + var12 * 2));
               }

               if (!var11) {
                  break;
               }

               try {
                  this.helper31(out, var5, var6);
                  var4 = 0xFF000000 | Integer.parseInt(var17.toString(), 16);
                  var6 = var4;
                  var7 += 14;
               } catch (NumberFormatException var15) {
                  break;
               }
            }

            if (var8 == 167 && var7 + var9 < text.length()) {
               this.helper31(out, var5, var6);
               char var19 = Character.toLowerCase(text.charAt(var7 + var9));
               Integer var21 = this.helper32(var19);
               if (var21 != null) {
                  var4 = var21;
               } else if (var19 == 'r') {
                  var4 = baseColor;
               }

               var6 = var4;
               var7 += var9 + 1;
            } else {
               String var18 = ReplaceSymbols.replaceCodePoint(var8);
               if (var18 == null) {
                  if (var5.length() > 0 && var6 != var4) {
                     this.helper31(out, var5, var6);
                  }

                  var6 = var4;
                  var5.appendCodePoint(var8);
                  var7 += var9;
               } else {
                  this.helper31(out, var5, var6);
                  int var20 = Math.max(1, var18.length());

                  for (int var22 = 0; var22 < var18.length(); var22++) {
                     int var13 = ReplaceSymbols.getGradientColorForReplacement(var8, var22, var20, 1.0F, var4);
                     if (var5.length() > 0 && var6 != var13) {
                        this.helper31(out, var5, var6);
                     }

                     var6 = var13;
                     var5.append(var18.charAt(var22));
                  }

                  var7 += var9;
               }
            }
         }
      }
   }

   private void helper31(List<NameTags.DonateSegment> out, StringBuilder chunk, int color) {
      if (!chunk.isEmpty()) {
         out.add(new NameTags.DonateSegment(chunk.toString(), color));
         chunk.setLength(0);
      }
   }

   private Integer helper32(char code) {
      return switch (code) {
         case '0' -> 0;
         case '1' -> 170;
         case '2' -> 43520;
         case '3' -> 43690;
         case '4' -> 11141120;
         case '5' -> 11141290;
         case '6' -> 16755200;
         case '7' -> 11184810;
         case '8' -> 5592405;
         case '9' -> 5592575;
         default -> null;
         case 'a' -> 5635925;
         case 'b' -> 5636095;
         case 'c' -> 16733525;
         case 'd' -> 16733695;
         case 'e' -> 16777045;
         case 'f' -> 16777215;
      };
   }

   private void helper33(long now) {
      if (now >= this.timestamp && mc.world != null) {
         this.timestamp = now + 2000L;
         this.uUIDs2.entrySet().removeIf(entry -> mc.world.getPlayerByUuid(entry.getKey()) == null);
      }
   }

   private Box helper34(Entity entity, float tickDelta) {
      double var3 = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
      double var5 = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
      double var7 = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
      double var9 = var3 - entity.getX();
      double var11 = var5 - entity.getY();
      double var13 = var7 - entity.getZ();
      return entity.getBoundingBox().offset(var9, var11, var13).expand(0.05);
   }

   private NameTags.ScreenRect helper35(Box box) {
      double var2 = Double.POSITIVE_INFINITY;
      double var4 = Double.POSITIVE_INFINITY;
      double var6 = Double.NEGATIVE_INFINITY;
      double var8 = Double.NEGATIVE_INFINITY;
      boolean var10 = false;

      for (int var11 = 0; var11 < 2; var11++) {
         for (int var12 = 0; var12 < 2; var12++) {
            for (int var13 = 0; var13 < 2; var13++) {
               if (this.helper36(
                  var11 == 0 ? box.minX : box.maxX, var12 == 0 ? box.minY : box.maxY, var13 == 0 ? box.minZ : box.maxZ, this.nameTags
               )) {
                  var10 = true;
                  var2 = Math.min(var2, this.nameTags.VOLUME);
                  var4 = Math.min(var4, this.nameTags.INDEX);
                  var6 = Math.max(var6, this.nameTags.VOLUME);
                  var8 = Math.max(var8, this.nameTags.INDEX);
               }
            }
         }
      }

      if (!var10) {
         return null;
      } else if (var2 > mc.getWindow().getScaledWidth() + 300 || var6 < -300.0) {
         return null;
      } else if (var4 > mc.getWindow().getScaledHeight() + 300 || var8 < -300.0) {
         return null;
      } else {
         return !(var6 - var2 < 2.0) && !(var8 - var4 < 2.0) ? new NameTags.ScreenRect((float)var2, (float)var4, (float)var6, (float)var8) : null;
      }
   }

   private boolean helper36(double worldX, double worldY, double worldZ, NameTags.ProjectedPoint out) {
      this.vector3f
         .set((float)(worldX - this.vec3d.x), (float)(worldY - this.vec3d.y), (float)(worldZ - this.vec3d.z));
      this.vector3f.rotate(this.quaternionf2);
      this.vector4f.set(this.vector3f.x, this.vector3f.y, this.vector3f.z, 1.0F);
      this.matrix4f.transform(this.vector4f);
      float var8 = this.vector4f.w;
      if (var8 <= 1.0E-5F) {
         return false;
      } else {
         float var9 = this.vector4f.x / var8;
         float var10 = this.vector4f.y / var8;
         float var11 = this.vector4f.z / var8;
         float var12 = (var9 * 0.5F + 0.5F) * this.index2;
         float var13 = (1.0F - (var10 * 0.5F + 0.5F)) * this.index3;
         if (Float.isNaN(var12) || Float.isNaN(var13)) {
            return false;
         } else if (!Float.isInfinite(var12) && !Float.isInfinite(var13)) {
            out.VOLUME = var12;
            out.INDEX = var13;
            out.INDEX2 = var11;
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean helper37(Entity entity, double yOffset, NameTags.ProjectedPoint out) {
      double var5 = MathHelper.lerp(this.volume, entity.lastRenderX, entity.getX());
      double var7 = MathHelper.lerp(this.volume, entity.lastRenderY, entity.getY()) + yOffset;
      double var9 = MathHelper.lerp(this.volume, entity.lastRenderZ, entity.getZ());
      return this.helper36(var5, var7, var9, out);
   }

   private boolean checkCondition3() {
      return mc != null && mc.gameRenderer != null && !mc.gameRenderer.getCamera().isThirdPerson();
   }

   private boolean helper38(Entity entity) {
      if (entity == null || entity.isRemoved() || entity instanceof ArmorStandEntity) {
         return false;
      } else if (entity instanceof PlayerEntity var4) {
         return this.helper42(var4, false);
      } else if (!(entity instanceof ItemEntity var2)) {
         if (!(entity instanceof LivingEntity var3 && var3.isAlive())) {
            return false;
         } else if (this.helper44(entity)) {
            return this.booleanSetting9.isState();
         } else {
            return this.helper45(entity) ? this.booleanSetting8.isState() : false;
         }
      } else {
         return this.booleanSetting10.isState() && var2.isAlive();
      }
   }

   private boolean helper39(PlayerEntity player) {
      return this.helper42(player, true);
   }

   private boolean helper40(LivingEntity entity) {
      return this.helper38(entity);
   }

   private boolean helper41(ItemEntity itemEntity) {
      return this.booleanSetting10.isState() && itemEntity.isAlive();
   }

   private boolean helper42(PlayerEntity player, boolean skipInvisible) {
      if (!this.booleanSetting7.isState()) {
         return false;
      } else if (player == null || !player.isAlive()) {
         return false;
      } else {
         return player == mc.player && this.checkCondition3()
            ? false
            : !skipInvisible || !player.isInvisible() || this.helper46(player) || this.booleanSetting2.isState();
      }
   }

   private boolean checkCondition4(int index) {
      return this.listSetting2.getSettings().size() > index && this.listSetting2.getSettings().get(index).isState();
   }

   public boolean shouldReplaceVanillaLabel(EntityRenderState state) {
      if (!this.isEnable()
         || state == null
         || this.listSetting.getSettings().isEmpty()
         || !this.listSetting.getSettings().get(0).isState()
         || state instanceof ArmorStandEntityRenderState) {
         return false;
      }

      if (state instanceof PlayerEntityRenderState) {
         return this.booleanSetting7.isState();
      }

      Entity var2 = this.helper43(state);
      return var2 != null && this.helper38(var2);
   }

   private Entity helper43(EntityRenderState state) {
      if (mc.world == null) {
         return null;
      }

      Entity var2 = null;
      double var3 = 0.04;

      for (Entity var6 : mc.world.getEntities()) {
         double var7 = var6.squaredDistanceTo(state.x, state.y, state.z);
         if (var7 < var3) {
            var2 = var6;
            var3 = var7;
         }
      }

      return var2;
   }

   private boolean helper44(Entity entity) {
      return entity instanceof AnimalEntity || entity instanceof WaterAnimalEntity || entity instanceof AmbientEntity;
   }

   private boolean helper45(Entity entity) {
      return entity instanceof MobEntity && !this.helper44(entity) && !(entity instanceof PlayerEntity);
   }

   private boolean helper46(PlayerEntity player) {
      SeeInvisibles var2 = ModuleClass.seeInvisibles;
      return var2 != null && var2.shouldRenderInvisible(player);
   }

   private boolean checkCondition5(Entity entity) {
      int var2 = mc.options.getViewDistance().getValue();
      double var3 = Math.max(48.0, var2 * 16.0 + 16.0);
      return entity.squaredDistanceTo(this.vec3d) > var3 * var3;
   }

   private void helper47(MatrixStack matrices, Entity entity, float tickDelta) {
      Vec3d var4 = mc.gameRenderer.getCamera().getPos();
      double var5 = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - var4.x;
      double var7 = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - var4.y;
      double var9 = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - var4.z;
      Box var11 = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
      matrices.push();
      matrices.translate(var5, var7, var9);
      boolean var12 = entity instanceof PlayerEntity var13
         && Lumen.INSTANCE.friendStorage != null
         && Lumen.INSTANCE.friendStorage.isFriend(var13.getName().getString());
      int var19;
      if (var12) {
         var19 = ColorUtils.rgba(84, 255, 84, 255);
      } else {
         var19 = this.helper64();
      }

      var19 = this.helper48(entity, var19);
      float var14 = ColorUtils.redf(var19);
      float var15 = ColorUtils.greenf(var19);
      float var16 = ColorUtils.bluef(var19);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      Matrix4f var17 = matrices.peek().getPositionMatrix();
      Tessellator var18 = Tessellator.getInstance();
      if (this.booleanSetting4.isState()) {
         this.helper79(var18, var17, var11, var14, var15, var16, 0.23F);
      }

      this.helper80(var18, var17, var11, var14, var15, var16, 1.0F);
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      matrices.pop();
   }

   private int helper48(Entity entity, int baseColor) {
      if (entity instanceof LivingEntity var3 && this.booleanSetting5.isState()) {
         float var4 = MathHelper.clamp(var3.hurtTime / 10.0F, 0.0F, 1.0F);
         float var5 = this.integers5.getOrDefault(entity.getId(), 0.0F);
         float var6 = var4 > var5 ? 0.38F : 0.16F;
         var5 += (var4 - var5) * var6;
         if (var5 <= 0.003F && var4 <= 0.0F) {
            this.integers5.remove(entity.getId());
            return baseColor;
         } else {
            this.integers5.put(entity.getId(), var5);
            int var7 = ColorUtils.rgba(255, 70, 70, 255);
            return ColorUtils.interpolateColor(baseColor, var7, var5);
         }
      } else {
         this.integers5.remove(entity.getId());
         return baseColor;
      }
   }

   private void helper49(MatrixStack matrices, Entity entity, float tickDelta) {
      Vec3d var4 = mc.gameRenderer.getCamera().getPos();
      double var5 = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - var4.x;
      double var7 = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - var4.y;
      double var9 = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - var4.z;
      Box var11 = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
      matrices.push();
      matrices.translate(var5, var7, var9);
      this.helper50(Tessellator.getInstance(), matrices.peek().getPositionMatrix(), var11);
      matrices.pop();
   }

   private void helper50(Tessellator tessellator, Matrix4f matrix, Box box) {
      BufferBuilder var4 = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float var5 = (float)box.minX;
      float var6 = (float)box.minY;
      float var7 = (float)box.minZ;
      float var8 = (float)box.maxX;
      float var9 = (float)box.maxY;
      float var10 = (float)box.maxZ;
      byte var11 = -1;
      var4.vertex(matrix, var5, var6, var7).color(var11);
      var4.vertex(matrix, var8, var6, var7).color(var11);
      var4.vertex(matrix, var8, var6, var10).color(var11);
      var4.vertex(matrix, var5, var6, var10).color(var11);
      var4.vertex(matrix, var5, var9, var7).color(var11);
      var4.vertex(matrix, var5, var9, var10).color(var11);
      var4.vertex(matrix, var8, var9, var10).color(var11);
      var4.vertex(matrix, var8, var9, var7).color(var11);
      var4.vertex(matrix, var5, var6, var7).color(var11);
      var4.vertex(matrix, var5, var9, var7).color(var11);
      var4.vertex(matrix, var8, var9, var7).color(var11);
      var4.vertex(matrix, var8, var6, var7).color(var11);
      var4.vertex(matrix, var5, var6, var10).color(var11);
      var4.vertex(matrix, var8, var6, var10).color(var11);
      var4.vertex(matrix, var8, var9, var10).color(var11);
      var4.vertex(matrix, var5, var9, var10).color(var11);
      var4.vertex(matrix, var5, var6, var7).color(var11);
      var4.vertex(matrix, var5, var6, var10).color(var11);
      var4.vertex(matrix, var5, var9, var10).color(var11);
      var4.vertex(matrix, var5, var9, var7).color(var11);
      var4.vertex(matrix, var8, var6, var7).color(var11);
      var4.vertex(matrix, var8, var9, var7).color(var11);
      var4.vertex(matrix, var8, var9, var10).color(var11);
      var4.vertex(matrix, var8, var6, var10).color(var11);
      BufferRenderer.drawWithGlobalProgram(var4.end());
   }

   private void updateState4() {
      if (this.flag2 && this.framebuffer != null) {
         boolean var1 = this.helper62();
         ShaderProgram var2 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blockOverlay);
         if (var2 != null) {
            int var3 = this.helper64();
            int var4 = this.helper63() ? ColorUtils.getThemeColor(180) : var3;
            mc.getFramebuffer().beginWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(ShaderUtils.blockOverlay);
            RenderSystem.setShaderTexture(0, this.framebuffer.getColorAttachment());
            this.helper57(
               var2, "texelSize", 1.0F / Math.max(1, mc.getWindow().getFramebufferWidth()), 1.0F / Math.max(1, mc.getWindow().getFramebufferHeight())
            );
            this.helper58(var2, "color", ColorUtils.redf(var3), ColorUtils.greenf(var3), ColorUtils.bluef(var3));
            this.helper58(var2, "color2", ColorUtils.redf(var4), ColorUtils.greenf(var4), ColorUtils.bluef(var4));
            this.helper56(var2, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
            this.helper56(var2, "speed", this.floatSetting.get());
            this.helper56(var2, "scale", this.floatSetting2.get());
            this.helper56(var2, "outline", this.floatSetting5.get());
            this.helper56(var2, "glow", var1 ? 0.0F : this.floatSetting6.get());
            this.helper56(var2, "fill", var1 ? 0.0F : this.floatSetting7.get());
            this.helper56(var2, "alpha", var1 ? 1.0F : this.floatSetting8.get());
            this.helper56(var2, "outlineOnly", var1 ? 1.0F : 0.0F);
            this.helper60();
            if (this.floatSetting6.get() > 0.001F) {
               int var5 = this.helper52(Math.max(3, Math.min(8, 4 + Math.round(this.floatSetting5.get() * 0.7F))));
               ShaderProgram var6 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsGlow);
               if (var6 != null) {
                  RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
                  RenderSystem.setShader(ShaderUtils.shaderHandsGlow);
                  RenderSystem.setShaderTexture(0, var5);
                  RenderSystem.setShaderTexture(1, this.framebuffer.getColorAttachment());
                  this.helper58(var6, "color", ColorUtils.redf(var3), ColorUtils.greenf(var3), ColorUtils.bluef(var3));
                  this.helper58(var6, "color2", ColorUtils.redf(var4), ColorUtils.greenf(var4), ColorUtils.bluef(var4));
                  this.helper56(var6, "exposure", 1.0F + this.floatSetting6.get() * 1.8F);
                  this.helper60();
               }
            }

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.setShaderTexture(1, 0);
            mc.getFramebuffer().beginWrite(true);
         }
      }
   }

   private void helper51() {
      if (this.helper61()) {
         Matrix4f var1 = new Matrix4f(RenderSystem.getProjectionMatrix());
         float var2 = Math.max(mc.getWindow().getScaledWidth(), 1);
         float var3 = Math.max(mc.getWindow().getScaledHeight(), 1);
         Matrix4f var4 = new Matrix4f().setOrtho(0.0F, var2, var3, 0.0F, -1000.0F, 1000.0F);
         RenderSystem.setProjectionMatrix(var4, ProjectionType.ORTHOGRAPHIC);

         try {
            this.updateState4();
         } finally {
            RenderSystem.setProjectionMatrix(var1, ProjectionType.ORTHOGRAPHIC);
         }
      }
   }

   private int helper52(int iterations) {
      this.helper54(iterations);
      if (this.framebuffers.isEmpty()) {
         return this.framebuffer.getColorAttachment();
      }

      int var2 = this.framebuffer.getColorAttachment();
      ShaderProgram var3 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsKawaseDown);
      ShaderProgram var4 = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shaderHandsKawaseUp);
      if (var3 != null && var4 != null) {
         for (int var5 = 0; var5 < iterations; var5++) {
            Framebuffer var6 = this.framebuffers.get(var5);
            var6.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var6.clear();
            var6.beginWrite(true);
            RenderSystem.setShader(ShaderUtils.shaderHandsKawaseDown);
            RenderSystem.setShaderTexture(0, var2);
            this.helper59(var3, var6.textureWidth, var6.textureHeight, 1.0F + var5);
            this.helper60();
            var2 = var6.getColorAttachment();
         }

         for (int var7 = iterations - 1; var7 >= 1; var7--) {
            Framebuffer var8 = this.framebuffers.get(var7 - 1);
            var8.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var8.clear();
            var8.beginWrite(true);
            RenderSystem.setShader(ShaderUtils.shaderHandsKawaseUp);
            RenderSystem.setShaderTexture(0, var2);
            this.helper59(var4, var8.textureWidth, var8.textureHeight, 1.0F + var7);
            this.helper58(var4, "color", 1.0F, 1.0F, 1.0F);
            this.helper60();
            var2 = var8.getColorAttachment();
         }

         mc.getFramebuffer().beginWrite(true);
         return var2;
      } else {
         return var2;
      }
   }

   private void helper53() {
      int var1 = mc.getWindow().getFramebufferWidth();
      int var2 = mc.getWindow().getFramebufferHeight();
      if (this.framebuffer == null || this.index4 != var1 || this.index5 != var2) {
         if (this.framebuffer != null) {
            this.framebuffer.delete();
         }

         this.framebuffer = new SimpleFramebuffer(var1, var2, true);
         this.index4 = var1;
         this.index5 = var2;

         for (Framebuffer var4 : this.framebuffers) {
            var4.delete();
         }

         this.framebuffers.clear();
      }
   }

   private void helper54(int iterations) {
      while (this.framebuffers.size() > iterations) {
         int var2 = this.framebuffers.size() - 1;
         this.framebuffers.get(var2).delete();
         this.framebuffers.remove(var2);
      }

      for (int var6 = 0; var6 < iterations; var6++) {
         int var3 = Math.max(2, this.index4 >> var6 + 1);
         int var4 = Math.max(2, this.index5 >> var6 + 1);
         if (var6 >= this.framebuffers.size()) {
            this.framebuffers.add(new SimpleFramebuffer(var3, var4, false));
         } else {
            Framebuffer var5 = this.framebuffers.get(var6);
            if (var5.textureWidth != var3 || var5.textureHeight != var4) {
               var5.delete();
               this.framebuffers.set(var6, new SimpleFramebuffer(var3, var4, false));
            }
         }
      }
   }

   private void helper55() {
      if (this.framebuffer != null) {
         int var1 = GL11.glGetInteger(36010);
         int var2 = GL11.glGetInteger(36006);
         int var3 = mc.getWindow().getFramebufferWidth();
         int var4 = mc.getWindow().getFramebufferHeight();
         GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
         GL30.glBindFramebuffer(36009, this.framebuffer.fbo);
         GL30.glBlitFramebuffer(0, 0, var3, var4, 0, 0, var3, var4, 256, 9728);
         GL30.glBindFramebuffer(36008, var1);
         GL30.glBindFramebuffer(36009, var2);
      }
   }

   private void helper56(ShaderProgram shader, String name, float value) {
      GlUniform var4 = shader.getUniform(name);
      if (var4 != null) {
         var4.set(value);
      }
   }

   private void helper57(ShaderProgram shader, String name, float x, float y) {
      GlUniform var5 = shader.getUniform(name);
      if (var5 != null) {
         var5.set(x, y);
      }
   }

   private void helper58(ShaderProgram shader, String name, float x, float y, float z) {
      GlUniform var6 = shader.getUniform(name);
      if (var6 != null) {
         var6.set(x, y, z);
      }
   }

   private void helper59(ShaderProgram shader, int texWidth, int texHeight, float offset) {
      this.helper57(shader, "uSize", Math.max(1, texWidth), Math.max(1, texHeight));
      this.helper57(shader, "uOffset", offset, offset);
      this.helper57(shader, "uHalfPixel", 0.5F / Math.max(1, texWidth), 0.5F / Math.max(1, texHeight));
   }

   private void helper60() {
      float var1 = Math.max(mc.getWindow().getScaledWidth(), 1);
      float var2 = Math.max(mc.getWindow().getScaledHeight(), 1);
      BufferBuilder var3 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      var3.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(0.0F, var2, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(var1, var2, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      var3.vertex(var1, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(var3.end());
   }

   private boolean helper61() {
      return false;
   }

   private boolean helper62() {
      return false;
   }

   private boolean helper63() {
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.themeStorage != null && Lumen.INSTANCE.themeStorage.getThemes() != null) {
         Theme var1 = Lumen.INSTANCE.themeStorage.getThemes().getTheme();
         return var1 != null && "Rainbow".equals(var1.getName());
      } else {
         return false;
      }
   }

   private int helper64() {
      if (Lumen.INSTANCE != null && Lumen.INSTANCE.themeStorage != null && Lumen.INSTANCE.themeStorage.getThemes() != null) {
         Theme var1 = Lumen.INSTANCE.themeStorage.getThemes().getTheme();
         return var1 != null && var1.color != null && var1.color.length != 0 ? var1.color[0] : ColorUtils.getThemeColor(0);
      } else {
         return ColorUtils.getThemeColor(0);
      }
   }

   private void helper65(MatrixStack matrices, Entity entity, float tickDelta) {
      Vec3d var4 = mc.gameRenderer.getCamera().getPos();
      double var5 = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - var4.x;
      double var7 = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - var4.y;
      double var9 = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - var4.z;
      Box var11 = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
      matrices.push();
      matrices.translate(var5, var7, var9);
      this.helper66(matrices.peek().getPositionMatrix(), var11, entity.getId());
      matrices.pop();
   }

   private void helper66(Matrix4f matrix, Box box, long seedBase) {
      byte var5 = 5;
      byte var6 = 18;
      float var7 = (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.floatSetting3.get();
      float var8 = 0.0025F;
      float var9 = 0.06F + this.floatSetting4.get() * 0.2F;
      int var10 = Math.max(20, Math.min(255, (int)(this.floatSetting8.get() * 210.0F)));
      int var11 = this.helper64();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      this.helper78(matrix, box, ColorUtils.setAlphaColor(var11, (int)(this.floatSetting8.get() * this.floatSetting7.get() * 170.0F)));

      for (int var12 = 0; var12 < 6; var12++) {
         int[] var13 = this.helper69(var12);

         for (int var14 = 0; var14 < var5; var14++) {
            int var15 = var12 * 1000 + var14 * 53;
            int var16 = var13[var14 % var13.length];
            double var17 = var7 * (0.95 + this.helper75(seedBase, var15 + 1) * 0.55) + var14 * 0.83 + var12 * 1.11;
            double var19 = this.helper77(0.5 + Math.sin(var17 * 1.37 + this.helper75(seedBase, var15 + 2) * 6.2831853) * 0.38);
            Vec3d var21 = this.helper72(box, var12, var16, var19, 0.0015);
            Vec3d var22 = this.helper74(
               box,
               var12,
               this.helper77(0.5 + (this.helper75(seedBase, var15 + 3) - 0.5) * 0.46),
               this.helper77(0.5 + (this.helper75(seedBase, var15 + 4) - 0.5) * 0.46),
               0.0015
            );
            Vec3d var23 = this.helper74(
               box,
               var16,
               this.helper77(0.5 + (this.helper75(seedBase, var15 + 5) - 0.5) * 0.46),
               this.helper77(0.5 + (this.helper75(seedBase, var15 + 6) - 0.5) * 0.46),
               0.0015
            );
            Vec3d[] var24 = this.helper70(var12);
            Vec3d[] var25 = this.helper70(var16);
            Vec3d var26 = this.helper71(var12);
            Vec3d var27 = this.helper71(var16);
            double var28 = var9
               * (0.7 + this.helper75(seedBase, var15 + 7))
               * Math.sin(var17 * 1.9 + this.helper75(seedBase, var15 + 8) * 6.2831853);
            double var30 = var9
               * (0.7 + this.helper75(seedBase, var15 + 9))
               * Math.cos(var17 * 1.7 + this.helper75(seedBase, var15 + 10) * 6.2831853);
            Vec3d var32 = var21.subtract(var22);
            Vec3d var33 = var22.add(var32.multiply(0.38)).add(var24[0].multiply(var28)).add(var24[1].multiply(-var28 * 0.55));
            Vec3d var34 = var22.add(var32.multiply(0.76)).add(var24[0].multiply(-var28 * 0.65)).add(var24[1].multiply(var28 * 0.4));
            Vec3d var35 = var23.subtract(var21);
            Vec3d var36 = var21.add(var35.multiply(0.24)).add(var25[0].multiply(var30)).add(var25[1].multiply(var30 * 0.45));
            Vec3d var37 = var21.add(var35.multiply(0.62)).add(var25[0].multiply(-var30 * 0.7)).add(var25[1].multiply(-var30 * 0.35));
            int var38 = Math.max(18, Math.min(255, (int)(var10 * (0.74 + 0.26 * Math.sin(var17 * 2.6)))));
            int var39 = ColorUtils.setAlphaColor(var11, var38);
            this.helper68(matrix, var22, var33, var34, var21, var26, var6, var39, var8);
            this.helper68(matrix, var21, var36, var37, var23, var27, var6, var39, var8);
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private Vec3d helper67(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, float t) {
      double var6 = 1.0 - t;
      double var8 = var6 * var6;
      double var10 = t * t;
      return p0.multiply(var8 * var6).add(p1.multiply(3.0 * var8 * t)).add(p2.multiply(3.0 * var6 * var10)).add(p3.multiply(var10 * t));
   }

   private void helper68(Matrix4f matrix, Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d faceNormal, int samples, int color, float halfWidth) {
      Vec3d[] var10 = new Vec3d[samples + 1];

      for (int var11 = 0; var11 <= samples; var11++) {
         float var12 = (float)var11 / samples;
         var10[var11] = this.helper67(p0, p1, p2, p3, var12);
      }

      BufferBuilder var21 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int var22 = 0; var22 < samples; var22++) {
         Vec3d var13 = var10[var22];
         Vec3d var14 = var10[var22 + 1];
         Vec3d var15 = var14.subtract(var13);
         if (!(var15.lengthSquared() < 1.0E-6)) {
            Vec3d var16 = faceNormal.crossProduct(var15).normalize().multiply(halfWidth);
            Vec3d var17 = var13.add(var16);
            Vec3d var18 = var13.subtract(var16);
            Vec3d var19 = var14.add(var16);
            Vec3d var20 = var14.subtract(var16);
            var21.vertex(matrix, (float)var17.x, (float)var17.y, (float)var17.z).color(color);
            var21.vertex(matrix, (float)var18.x, (float)var18.y, (float)var18.z).color(color);
            var21.vertex(matrix, (float)var20.x, (float)var20.y, (float)var20.z).color(color);
            var21.vertex(matrix, (float)var19.x, (float)var19.y, (float)var19.z).color(color);
         }
      }

      BufferRenderer.drawWithGlobalProgram(var21.end());
   }

   private int[] helper69(int face) {
      return switch (face) {
         case 0, 1 -> new int[]{2, 3, 4, 5};
         case 2, 3 -> new int[]{0, 1, 4, 5};
         default -> new int[]{0, 1, 2, 3};
      };
   }

   private Vec3d[] helper70(int face) {
      return switch (face) {
         case 0, 1 -> new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0)};
         case 2, 3 -> new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, 0.0)};
         default -> new Vec3d[]{new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 0.0)};
      };
   }

   private Vec3d helper71(int face) {
      return switch (face) {
         case 0 -> new Vec3d(0.0, 1.0, 0.0);
         case 1 -> new Vec3d(0.0, -1.0, 0.0);
         case 2 -> new Vec3d(0.0, 0.0, -1.0);
         case 3 -> new Vec3d(0.0, 0.0, 1.0);
         case 4 -> new Vec3d(-1.0, 0.0, 0.0);
         default -> new Vec3d(1.0, 0.0, 0.0);
      };
   }

   private Vec3d helper72(Box box, int faceA, int faceB, double t, double inset) {
      double var8 = Double.NaN;
      double var10 = Double.NaN;
      double var12 = Double.NaN;
      double[] var14 = this.helper73(box, faceA, inset);
      if (!Double.isNaN(var14[0])) {
         var8 = var14[0];
      }

      if (!Double.isNaN(var14[1])) {
         var10 = var14[1];
      }

      if (!Double.isNaN(var14[2])) {
         var12 = var14[2];
      }

      double[] var15 = this.helper73(box, faceB, inset);
      if (!Double.isNaN(var15[0])) {
         var8 = var15[0];
      }

      if (!Double.isNaN(var15[1])) {
         var10 = var15[1];
      }

      if (!Double.isNaN(var15[2])) {
         var12 = var15[2];
      }

      double var16 = this.helper77(t);
      if (Double.isNaN(var8)) {
         var8 = this.helper76(box.minX, box.maxX, var16);
      }

      if (Double.isNaN(var10)) {
         var10 = this.helper76(box.minY, box.maxY, var16);
      }

      if (Double.isNaN(var12)) {
         var12 = this.helper76(box.minZ, box.maxZ, var16);
      }

      return new Vec3d(var8, var10, var12);
   }

   private double[] helper73(Box box, int face, double inset) {
      return switch (face) {
         case 0 -> new double[]{Double.NaN, box.maxY - inset, Double.NaN};
         case 1 -> new double[]{Double.NaN, box.minY + inset, Double.NaN};
         case 2 -> new double[]{Double.NaN, Double.NaN, box.minZ + inset};
         case 3 -> new double[]{Double.NaN, Double.NaN, box.maxZ - inset};
         case 4 -> new double[]{box.minX + inset, Double.NaN, Double.NaN};
         default -> new double[]{box.maxX - inset, Double.NaN, Double.NaN};
      };
   }

   private Vec3d helper74(Box box, int face, double u, double v, double inset) {
      u = this.helper77(u);
      v = this.helper77(v);

      return switch (face) {
         case 0 -> new Vec3d(this.helper76(box.minX, box.maxX, u), box.maxY - inset, this.helper76(box.minZ, box.maxZ, v));
         case 1 -> new Vec3d(this.helper76(box.minX, box.maxX, u), box.minY + inset, this.helper76(box.minZ, box.maxZ, v));
         case 2 -> new Vec3d(this.helper76(box.minX, box.maxX, u), this.helper76(box.minY, box.maxY, v), box.minZ + inset);
         case 3 -> new Vec3d(this.helper76(box.minX, box.maxX, u), this.helper76(box.minY, box.maxY, v), box.maxZ - inset);
         case 4 -> new Vec3d(box.minX + inset, this.helper76(box.minY, box.maxY, v), this.helper76(box.minZ, box.maxZ, u));
         default -> new Vec3d(box.maxX - inset, this.helper76(box.minY, box.maxY, v), this.helper76(box.minZ, box.maxZ, u));
      };
   }

   private double helper75(long seed, int salt) {
      long var4 = seed + -7046029254386353131L * (salt + 1L);
      var4 ^= var4 >>> 30;
      var4 *= -4658895280553007687L;
      var4 ^= var4 >>> 27;
      var4 *= -7723592293110705685L;
      var4 ^= var4 >>> 31;
      return (var4 & 16777215L) / 1.6777216E7;
   }

   private double helper76(double a, double b, double t) {
      return a + (b - a) * t;
   }

   private double helper77(double v) {
      return Math.max(0.0, Math.min(1.0, v));
   }

   private void helper78(Matrix4f matrix, Box box, int color) {
      BufferBuilder var4 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color);
      var4.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color);
      BufferRenderer.drawWithGlobalProgram(var4.end());
   }

   private void helper79(Tessellator tessellator, Matrix4f matrix, Box box, float r, float g, float b, float a) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder var8 = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float var9 = (float)box.minX;
      float var10 = (float)box.minY;
      float var11 = (float)box.minZ;
      float var12 = (float)box.maxX;
      float var13 = (float)box.maxY;
      float var14 = (float)box.maxZ;
      var8.vertex(matrix, var9, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var14).color(r, g, b, a);
      BufferRenderer.drawWithGlobalProgram(var8.end());
   }

   private void helper80(Tessellator tessellator, Matrix4f matrix, Box box, float r, float g, float b, float a) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      BufferBuilder var8 = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      float var9 = (float)box.minX;
      float var10 = (float)box.minY;
      float var11 = (float)box.minZ;
      float var12 = (float)box.maxX;
      float var13 = (float)box.maxY;
      float var14 = (float)box.maxZ;
      var8.vertex(matrix, var9, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var11).color(r, g, b, a);
      var8.vertex(matrix, var12, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var12, var13, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var10, var14).color(r, g, b, a);
      var8.vertex(matrix, var9, var13, var14).color(r, g, b, a);
      BufferRenderer.drawWithGlobalProgram(var8.end());
   }

   private static class DonateCache {
      private List<NameTags.DonateSegment> VOLUME = Collections.emptyList();
      private long INDEX;
   }

   private record DonateSegment(String text, int color) {

      private DonateSegment(String text, int color) {
         this.text = text;
         this.color = color;
      }

      public String text() {
         return this.text;
      }

      public int color() {
         return this.color;
      }
   }

   private static class EFEntity {
      final int id;
      double x;
      double y;
      double z;
      String name;
      long lastSeen;
      long bornAt;
      boolean fading;
      float progress;
      final Map<EquipmentSlot, ItemStack> equipment = new EnumMap<>(EquipmentSlot.class);

      EFEntity(int id, double x, double y, double z, String name) {
         this.id = id;
         this.x = x;
         this.y = y;
         this.z = z;
         this.name = name;
         this.lastSeen = System.currentTimeMillis();
         this.bornAt = this.lastSeen;
         this.fading = false;
         this.progress = 0.0F;
      }

      void updatePos(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.lastSeen = System.currentTimeMillis();
      }
   }

   private static class ProjectedPoint {
      private float VOLUME;
      private float INDEX;
      private float INDEX2;
   }

   private record ScreenRect(float minX, float minY, float maxX, float maxY) {

      private ScreenRect(float minX, float minY, float maxX, float maxY) {
         this.minX = minX;
         this.minY = minY;
         this.maxX = maxX;
         this.maxY = maxY;
      }

      float centerX() {
         return (this.minX + this.maxX) * 0.5F;
      }

      float centerY() {
         return (this.minY + this.maxY) * 0.5F;
      }

      public float minX() {
         return this.minX;
      }

      public float minY() {
         return this.minY;
      }

      public float maxX() {
         return this.maxX;
      }

      public float maxY() {
         return this.maxY;
      }
   }
}
