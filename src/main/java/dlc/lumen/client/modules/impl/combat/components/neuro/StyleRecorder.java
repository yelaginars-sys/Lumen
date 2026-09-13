package dlc.lumen.client.modules.impl.combat.components.neuro;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventAttackEntity;
import dlc.lumen.api.events.implement.EventLook;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.api.storages.implement.RotationStorage;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.modules.impl.combat.Aura;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class StyleRecorder implements QClient {
   private static final int INDEX = 4;
   private static final double LEVEL = 7.0;
   private static final long TIMESTAMP = 5000L;
   private static final int INDEX2 = 60;
   private static StyleRecorder styleRecorder;
   private volatile boolean recording;
   private String recording2;
   private Path path;
   private BufferedWriter bufferedWriter;
   private long timestamp;
   private long timestamp2;
   private int index;
   private int index2;
   private int index3;
   private final StringBuilder stringBuilder = new StringBuilder(512);
   private final List<double[]> items = new ArrayList<>();
   private long timestamp3;
   private float volume;
   private float volume2;
   private boolean flag;
   private int index4 = Integer.MIN_VALUE;
   private boolean flag2;
   private boolean flag3;
   private int index5;
   private LivingEntity livingEntity;
   private int index6 = 40;

   public static StyleRecorder get() {
      if (styleRecorder == null) {
         styleRecorder = new StyleRecorder();
      }

      return styleRecorder;
   }

   private StyleRecorder() {
      EventInvoker.register(this);
   }

   public void noteModuleAttack() {
      this.flag3 = true;
   }

   public boolean isRecording() {
      return this.recording;
   }

   public String styleName() {
      return this.recording2;
   }

   public synchronized String start(String name) {
      if (this.recording) {
         return "уже пишу стиль «" + this.recording2 + "» — сначала .neuro stop";
      }

      String var2 = NeuroStyles.sanitize(name);

      try {
         Path var3 = NeuroStyles.trackDir(var2);
         Files.createDirectories(var3);
         String var4 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
         this.path = var3.resolve("track-" + var4 + ".jsonl");
         this.bufferedWriter = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         double var5 = mc.options != null ? mc.options.getMouseSensitivity().getValue() : 0.5;
         this.bufferedWriter
            .write(
               String.format(
                  Locale.ROOT,
                  "{\"type\":\"header\",\"version\":%d,\"name\":\"%s\",\"startedAt\":%d,\"tickRate\":20,\"sens\":%.4f,\"player\":\"%s\"}%n",
                  4,
                  var2,
                  System.currentTimeMillis(),
                  var5,
                  mc.player != null ? mc.player.getGameProfile().getName() : "?"
               )
            );
      } catch (IOException var7) {
         this.bufferedWriter = null;
         return "не открылся файл записи: " + var7.getMessage();
      }

      this.recording2 = var2;
      this.timestamp = System.currentTimeMillis();
      this.timestamp2 = this.timestamp;
      this.index = 0;
      this.index2 = 0;
      this.index3 = 0;
      this.flag = false;
      this.items.clear();
      this.livingEntity = null;
      this.index6 = 40;
      this.recording = true;
      return null;
   }

   public synchronized String stop() {
      if (!this.recording) {
         return "запись не идёт";
      }

      this.recording = false;

      try {
         this.bufferedWriter.flush();
         this.bufferedWriter.close();
      } catch (IOException var4) {
      }

      this.bufferedWriter = null;
      long var1 = Math.max(1L, (System.currentTimeMillis() - this.timestamp) / 1000L);
      return String.format(
         Locale.ROOT,
         "стиль «%s»: %d тиков (%d:%02d), ударов %d, файл %s",
         this.recording2,
         this.index,
         var1 / 60L,
         var1 % 60L,
         this.index3,
         this.path.getFileName()
      );
   }

   public static long totalRows(String name) {
      Path var1 = NeuroStyles.trackDir(name);
      if (!Files.isDirectory(var1)) {
         return 0L;
      }

      long var2 = 0L;

      try (Stream<Path> var4 = Files.list(var1)) {
         for (Path var6 : (Iterable<Path>)var4::iterator) {
            if (var6.getFileName().toString().endsWith(".jsonl")) {
               try (Stream<String> var7 = Files.lines(var6, StandardCharsets.UTF_8)) {
                  var2 += var7.filter(l -> l.startsWith("{\"t\"")).count();
               } catch (IOException var13) {
               }
            }
         }
      } catch (IOException var15) {
      }

      return var2;
   }

   @EventLink(priority = -100)
   public void onLook(EventLook event) {
      if (this.recording) {
         long var2 = System.nanoTime();
         long var4 = this.timestamp3 == 0L ? 0L : (var2 - this.timestamp3) / 1000L;
         this.timestamp3 = var2;
         double var6 = event.getYaw() * 0.15;
         double var8 = event.getPitch() * 0.15;
         if (var6 != 0.0 || var8 != 0.0) {
            synchronized (this.items) {
               if (this.items.size() < 64) {
                  this.items.add(new double[]{var4, var6, var8});
               }
            }
         }
      }
   }

   @EventLink(priority = -100)
   public void onAttack(EventAttackEntity event) {
      if (this.recording) {
         this.flag2 = true;
      }
   }

   @EventLink(priority = -100)
   public void onTick(EventUpdate event) {
      if (this.recording && mc.player != null && mc.world != null) {
         int var2 = mc.player.age;
         if (var2 != this.index4) {
            this.index4 = var2;
            float var3 = mc.player.getYaw();
            float var4 = mc.player.getPitch();
            float var5 = 0.0F;
            float var6 = 0.0F;
            if (this.flag) {
               var5 = MathHelper.wrapDegrees(var3 - this.volume);
               var6 = var4 - this.volume2;
            }

            this.volume = var3;
            this.volume2 = var4;
            boolean var7 = !this.flag;
            this.flag = true;
            boolean var8 = this.flag2;
            boolean var9 = var8 && !this.flag3;
            this.flag2 = false;
            this.flag3 = false;
            if (var8) {
               this.index3++;
            }

            LivingEntity var10 = this.computeLivingEntity();
            if (var10 == null) {
               if (++this.index5 == 60) {
                  this.updateState();
               }

               synchronized (this.items) {
                  this.items.clear();
               }
            } else {
               this.index5 = 0;
               if (var10 != this.livingEntity) {
                  this.livingEntity = var10;
                  this.index6 = 0;
               } else if (this.index6 < 40) {
                  this.index6++;
               }

               if (!var7) {
                  RotationStorage var11 = RotationStorage.instance;
                  boolean var12 = var11 != null && var11.currentPriority() >= 1 && var11.targetRotation() != null;
                  if (var12) {
                     this.index2++;
                  }

                  this.updateState2(var10, var5, var6, var8, var9, var12);
                  long var13 = System.currentTimeMillis();
                  if (var13 - this.timestamp2 >= 5000L) {
                     this.timestamp2 = var13;
                     long var15 = (var13 - this.timestamp) / 1000L;
                     ChatUtils.sendMessage(
                        String.format(
                           Locale.ROOT,
                           "§7Neuro: пишу «%s» — %d тиков (%d:%02d), ударов %d%s",
                           this.recording2,
                           this.index,
                           var15 / 60L,
                           var15 % 60L,
                           this.index3,
                           this.index2 > 0 ? " §8(отравлено ротацией: " + this.index2 + ")" : ""
                        )
                     );
                  }
               }
            }
         }
      }
   }

   private LivingEntity computeLivingEntity() {
      Aura var1 = Aura.INSTANCE;
      LivingEntity var2 = var1 != null && var1.isEnable() ? var1.getTarget() : null;
      if (var2 != null && var2.isAlive()) {
         return var2;
      }

      LivingEntity var3 = null;
      double var4 = 49.0;

      for (Entity var7 : mc.world.getEntities()) {
         if (var7 instanceof LivingEntity var8 && var8 != mc.player && (var8 instanceof PlayerEntity || var8.isAttackable()) && var8.isAlive()) {
            double var9 = var8.squaredDistanceTo(mc.player);
            if (var9 < var4) {
               var4 = var9;
               var3 = var8;
            }
         }
      }

      return var3;
   }

   private void updateState() {
      try {
         this.bufferedWriter.write("{\"type\":\"break\"}\n");
      } catch (IOException var2) {
      }
   }

   private void updateState2(LivingEntity target, float dy, float dp, boolean attacked, boolean manual, boolean foreign) {
      Box var7 = target.getBoundingBox();
      Vec3d var8 = var7.getCenter();
      Vec3d var9 = mc.player.getEyePos();
      double var10 = var8.x - var9.x;
      double var12 = var8.y - var9.y;
      double var14 = var8.z - var9.z;
      double var16 = Math.sqrt(var10 * var10 + var14 * var14);
      double var18 = Math.toDegrees(Math.atan2(var14, var10)) - 90.0;
      double var20 = -Math.toDegrees(Math.atan2(var12, var16));
      double var22 = Math.sqrt(var16 * var16 + var12 * var12);
      ClientPlayerEntity var24 = mc.player;
      double var25 = var24.getVelocity().horizontalLength();
      double var27 = var24.getVelocity().y;
      float var29 = var24.getAttackCooldownProgress(0.0F);
      int var30 = target.hurtTime;
      this.stringBuilder.setLength(0);
      this.stringBuilder.append("{\"t\":").append(var24.age);
      handleEvent(this.stringBuilder, "dy", dy);
      handleEvent(this.stringBuilder, "dp", dp);
      handleEvent(this.stringBuilder, "ay", var18);
      handleEvent(this.stringBuilder, "ap", var20);
      handleEvent(this.stringBuilder, "py", MathHelper.wrapDegrees(var24.getYaw()));
      handleEvent(this.stringBuilder, "pp", var24.getPitch());
      handleEvent(this.stringBuilder, "d", var22);
      handleEvent(this.stringBuilder, "ts", target.getVelocity().horizontalLength());
      this.stringBuilder.append(",\"sw\":").append(this.index6);
      handleEvent(this.stringBuilder, "os", var25);
      handleEvent(this.stringBuilder, "ov", var27);
      handleEvent(this.stringBuilder, "cd", var29);
      this.stringBuilder.append(",\"ht\":").append(var30);
      this.stringBuilder.append(",\"g\":").append(var24.isOnGround() ? 1 : 0);
      this.stringBuilder.append(",\"w\":").append(var24.isTouchingWater() ? 1 : 0);
      this.stringBuilder.append(",\"sp\":").append(var24.isSprinting() ? 1 : 0);
      this.stringBuilder.append(",\"sn\":").append(var24.isSneaking() ? 1 : 0);
      this.stringBuilder.append(",\"atk\":").append(attacked ? 1 : 0);
      this.stringBuilder.append(",\"man\":").append(manual ? 1 : 0);
      this.stringBuilder.append(",\"rot\":").append(foreign ? 1 : 0);
      this.stringBuilder.append(",\"m\":[");
      synchronized (this.items) {
         for (int var32 = 0; var32 < this.items.size(); var32++) {
            double[] var33 = this.items.get(var32);
            if (var32 > 0) {
               this.stringBuilder.append(',');
            }

            this.stringBuilder.append('[').append((long)var33[0]).append(',');
            handleEvent2(this.stringBuilder, var33[1]);
            this.stringBuilder.append(',');
            handleEvent2(this.stringBuilder, var33[2]);
            this.stringBuilder.append(']');
         }

         this.items.clear();
      }

      this.stringBuilder.append("]}\n");

      try {
         this.bufferedWriter.write(this.stringBuilder.toString());
         this.index++;
      } catch (IOException var35) {
         this.recording = false;
         ChatUtils.sendMessage("§cNeuro: запись оборвалась — " + var35.getMessage());
      }
   }

   private static void handleEvent(StringBuilder sb, String key, double value) {
      sb.append(",\"").append(key).append("\":");
      handleEvent2(sb, value);
   }

   private static void handleEvent2(StringBuilder sb, double value) {
      if (!Double.isFinite(value)) {
         value = 0.0;
      }

      sb.append(String.format(Locale.ROOT, "%.4f", value));
   }
}
