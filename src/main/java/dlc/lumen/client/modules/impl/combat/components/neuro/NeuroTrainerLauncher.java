package dlc.lumen.client.modules.impl.combat.components.neuro;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.chat.ChatUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public final class NeuroTrainerLauncher implements QClient {
   private static NeuroTrainerLauncher neuroTrainerLauncher;
   private volatile Process volatileProcess;
   private volatile String running;

   public static NeuroTrainerLauncher get() {
      if (neuroTrainerLauncher == null) {
         neuroTrainerLauncher = new NeuroTrainerLauncher();
      }

      return neuroTrainerLauncher;
   }

   private NeuroTrainerLauncher() {
   }

   public boolean isRunning() {
      Process var1 = this.volatileProcess;
      return var1 != null && var1.isAlive();
   }

   public String trainingStyle() {
      return this.running;
   }

   public synchronized void start(String rawName) {
      if (this.isRunning()) {
         ChatUtils.sendMessage("§cNeuro: обучение §f" + this.running + "§c ещё идёт — дождись или убей python.");
      } else {
         String var2 = NeuroStyles.sanitize(rawName);
         Path var3 = NeuroStyles.trackDir(var2);
         if (!Files.isDirectory(var3)) {
            ChatUtils.sendMessage("§cNeuro: нет дорожек стиля §f" + var2 + "§c. Сначала §f.neuro record " + var2);
         } else {
            Path var4 = computePath();
            if (var4 == null) {
               ChatUtils.sendMessage("§cNeuro: не нашёл python/train_style.py рядом с проектом.");
            } else {
               ProcessBuilder var5 = new ProcessBuilder("py", "-u", var4.toString(), "--name", var2, "--styles-dir", NeuroStyles.stylesDir().toString());
               var5.directory(var4.getParent().getParent().toFile());
               var5.redirectErrorStream(true);
               var5.environment().put("PYTHONIOENCODING", "utf-8");
               var5.environment().put("PYTHONUTF8", "1");

               try {
                  this.volatileProcess = var5.start();
               } catch (IOException var7) {
                  ChatUtils.sendMessage("§cNeuro: python не стартовал: " + var7.getMessage());
                  this.volatileProcess = null;
                  return;
               }

               this.running = var2;
               ChatUtils.sendMessage("§aNeuro: обучение стиля §f" + var2 + "§a запущено через §ftrain_style.py§a.");
               Thread var6 = new Thread(() -> this.updateState(this.volatileProcess), "neuro-train-pump");
               var6.setDaemon(true);
               var6.start();
            }
         }
      }
   }

   private static Path computePath() {
      Path var0 = FabricLoader.getInstance().getGameDir();
      Path[] var1 = new Path[]{var0.resolve("../python/train_style.py").normalize(), var0.resolve("python/train_style.py")};

      for (Path var5 : var1) {
         if (Files.isRegularFile(var5)) {
            return var5;
         }
      }

      return null;
   }

   private void updateState(Process p) {
      String var3;
      try (BufferedReader var2 = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
         while ((var3 = var2.readLine()) != null) {
            String var4 = var3.trim();
            if (!var4.isEmpty()
               && (
                  var4.startsWith("дорожек:")
                     || var4.startsWith("сверка по свежей дорожке:")
                     || var4.startsWith("политика:")
                     || var4.startsWith("подхвачено +")
                     || var4.startsWith("кривая преследования:")
                     || var4.startsWith("готово:")
                     || var4.startsWith("ОШИБКА:")
                     || var4.startsWith("эпоха ")
                     || var4.startsWith("Traceback")
                     || var4.startsWith("ModuleNotFoundError")
                     || var4.startsWith("ImportError")
                     || var4.contains("No module named")
               )) {
               handleEvent("§7[train] §f" + var4);
            }
         }
      } catch (IOException var8) {
      }

      int var9;
      try {
         var9 = p.waitFor();
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
         return;
      }

      var3 = this.running;
      if (var9 == 0) {
         if (var3 != null && var3.equals(NeuroStyles.activeName()) && NeuroStyles.activate(var3) != null) {
            handleEvent("§aNeuro: стиль §f" + var3 + "§a перезагружен новым .wnn.");
         }

         handleEvent("§aNeuro: обучение §f" + var3 + "§a завершено.");
      } else {
         handleEvent("§cNeuro: тренер вышел с кодом " + var9 + " — смотри консоль.");
      }

      this.volatileProcess = null;
   }

   private static void handleEvent(String message) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null) {
         var1.execute(() -> {
            try {
               ChatUtils.sendMessage(message);
            } catch (Throwable var2) {
            }
         });
      }
   }
}
