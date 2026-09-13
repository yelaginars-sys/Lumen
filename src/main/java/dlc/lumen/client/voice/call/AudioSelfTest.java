package dlc.lumen.client.voice.call;

import dlc.lumen.api.utils.chat.ChatUtils;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;

public final class AudioSelfTest {
   private static final int INDEX = 3;
   private static volatile boolean flag;

   private AudioSelfTest() {
   }

   public static void run() {
      if (flag) {
         ChatUtils.sendMessage("Проверка звука уже идёт.");
      } else if (VoiceCallManager.INSTANCE.isBusy()) {
         ChatUtils.sendMessage("Нельзя проверять звук во время звонка — микрофон занят.");
      } else {
         flag = true;
         Thread var0 = new Thread(AudioSelfTest::helper, "lumen-audio-selftest");
         var0.setDaemon(true);
         var0.start();
      }
   }

   private static void helper() {
      try {
         AudioFormat var0 = new AudioFormat(48000.0F, 16, 1, true, false);
         helper3();
         OpusCodec.Encoder var1 = OpusCodec.createEncoder();
         OpusCodec.Decoder var2 = OpusCodec.createDecoder();
         ChatUtils.sendMessage("Кодек: " + var1.backend().label());
         short var3 = 960;
         int var4 = var3 * 2;
         short var5 = 150;
         short[][] var6 = new short[var5][];
         double var7 = 0.0;
         double var9 = 0.0;
         int var11 = 0;
         int var12 = 0;
         int var13 = 0;
         TargetDataLine var14 = null;

         try {
            Info var15 = new Info(TargetDataLine.class, var0);
            if (!AudioSystem.isLineSupported(var15)) {
               ChatUtils.sendMessage("Микрофон не поддерживает 48 кГц моно 16 бит.");
               return;
            }

            var14 = AudioDevices.openInput(var0, var4 * 32);
            ChatUtils.sendMessage("Говорите 3 секунды...");
            byte[] var16 = new byte[var4];
            short[] var17 = new short[var3];

            for (int var18 = 0; var18 < var5; var18++) {
               int var19 = 0;

               while (true) {
                  if (var19 < var4) {
                     int var20 = var14.read(var16, var19, var4 - var19);
                     if (var20 > 0) {
                        var19 += var20;
                        continue;
                     }
                  }

                  double var38 = 0.0;

                  for (int var22 = 0; var22 < var17.length; var22++) {
                     var17[var22] = (short)(var16[var22 * 2] & 0xFF | var16[var22 * 2 + 1] << 8);
                     var38 += (double)var17[var22] * var17[var22];
                  }

                  double var39 = Math.sqrt(var38 / var17.length);
                  var7 = Math.max(var7, var39);
                  var9 += var39;
                  byte[] var24 = var1.encode(var17);
                  if (var24 == null) {
                     var11++;
                  } else {
                     var13 += var24.length;
                     short[] var25 = var2.decode(var24);
                     if (var25 == null) {
                        var12++;
                     } else {
                        var6[var18] = var25;
                     }
                  }
                  break;
               }
            }
         } finally {
            helper4(var14);
            var1.close();
            var2.close();
         }

         double var37 = var9 / var5;
         ChatUtils.sendMessage(String.format(Locale.ROOT, "Микрофон: средний %.0f, пик %.0f (тишина <100, речь 1000-8000)", var37, var7));
         ChatUtils.sendMessage(
            String.format(
               Locale.ROOT,
               "Кодек: %d байт/пакет, ~%.0f кбит/с, сбоев кодирования %d, декодирования %d",
               var13 / Math.max(1, var5),
               var13 * 8.0 / 3.0 / 1000.0,
               var11,
               var12
            )
         );
         if (!(var7 < 100.0)) {
            if (var37 > 3000.0 && var7 < var37 * 1.4) {
               ChatUtils.sendMessage("Уровень ровный и высокий — похоже, это шум, а не речь. Скорее всего выбран не тот вход (например, Stereo Mix).");
            }

            helper2(var0, var6, var4);
            return;
         }

         ChatUtils.sendMessage("Микрофон молчит — проверьте устройство ввода в Windows.");
      } catch (Throwable var35) {
         ChatUtils.sendMessage("Проверка звука не удалась: " + helper5(var35));
         return;
      } finally {
         flag = false;
      }
   }

   private static void helper2(AudioFormat format, short[][] decoded, int frameBytes) {
      SourceDataLine var3 = null;

      try {
         Info var4 = new Info(SourceDataLine.class, format);
         if (AudioSystem.isLineSupported(var4)) {
            var3 = AudioDevices.openOutput(format, frameBytes * 32);
            ChatUtils.sendMessage("Воспроизвожу запись. Если здесь шум — дело в микрофоне, если чисто — дело в сети.");
            byte[] var5 = new byte[frameBytes];

            for (short[] var9 : decoded) {
               if (var9 != null) {
                  for (int var10 = 0; var10 < var9.length && var10 * 2 + 1 < var5.length; var10++) {
                     var5[var10 * 2] = (byte)var9[var10];
                     var5[var10 * 2 + 1] = (byte)(var9[var10] >> 8);
                  }

                  var3.write(var5, 0, var5.length);
               }
            }

            var3.drain();
            return;
         }

         ChatUtils.sendMessage("Динамик не поддерживает 48 кГц моно 16 бит.");
      } catch (Exception var14) {
         ChatUtils.sendMessage("Воспроизведение не удалось: " + helper5(var14));
         return;
      } finally {
         helper4(var3);
      }
   }

   private static void helper3() {
      ChatUtils.sendMessage("Микрофон: " + AudioDevices.getInputName() + " | Динамик: " + AudioDevices.getOutputName());
      ChatUtils.sendMessage("Сменить: .voice devices, затем .voice mic <номер>");
   }

   private static void helper4(DataLine line) {
      if (line != null) {
         try {
            line.stop();
            line.close();
         } catch (Exception var2) {
         }
      }
   }

   private static String helper5(Throwable throwable) {
      String var1 = throwable.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : throwable.getClass().getSimpleName();
   }
}
