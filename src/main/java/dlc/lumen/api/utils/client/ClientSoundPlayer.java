package dlc.lumen.api.utils.client;

import dlc.lumen.client.modules.impl.misc.ClientSounds;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;
import lombok.Generated;

public final class ClientSoundPlayer {
   private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
      Thread var1 = new Thread(r, "lumen-ClientSounds");
      var1.setDaemon(true);
      return var1;
   });

   public static void playSound(String fileName, double volume, float pitch) {
      executor.execute(() -> handleFileName(fileName, volume, pitch));
   }

   public static void playGuiOpen() {
      playGuiSound(true);
   }

   public static void playGuiClose() {
      playGuiSound(false);
   }

   private static void playGuiSound(boolean open) {
      ClientSounds sounds = ClientSounds.INSTANCE;
      if (sounds != null && sounds.isEnable()) {
         String mode = sounds.stateSounds.getCurrent();
         if (mode != null && !"Нет".equals(mode)) {
            playSound(mode + ".wav", sounds.volume.get() / sounds.volume.getMax(), 1.0F);
            return;
         }
      }

      playSound(open ? "opengui.wav" : "closegui.wav", 0.5, 1.0F);
   }

   private static void handleFileName(String fileName, double volume, float pitch) {
      String var4 = "/assets/lumen/sounds/" + fileName;

      try (InputStream var5 = ClientSoundPlayer.class.getResourceAsStream(var4)) {
         if (var5 != null) {
            try (
               BufferedInputStream var6 = new BufferedInputStream(var5);
               AudioInputStream var7 = AudioSystem.getAudioInputStream(var6);
               AudioInputStream var8 = getOriginalStream(var7, pitch);
            ) {
               Clip var9 = AudioSystem.getClip();
               var9.addLineListener(event -> {
                  if (event.getType() == Type.STOP) {
                     var9.close();
                  }
               });
               var9.open(var8);
               handleClip(var9, volume);
               var9.start();
            }
         }
      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException var19) {
      }
   }

   private static AudioInputStream getOriginalStream(AudioInputStream originalStream, float pitch) throws IOException {
      AudioFormat var2 = originalStream.getFormat();
      byte[] var3 = originalStream.readAllBytes();
      float var4 = var2.getSampleRate() * Math.max(0.5F, Math.min(2.0F, pitch));
      AudioFormat var5 = new AudioFormat(var4, var2.getSampleSizeInBits(), var2.getChannels(), true, var2.isBigEndian());
      return new AudioInputStream(new ByteArrayInputStream(var3), var5, var3.length / var5.getFrameSize());
   }

   private static void handleClip(Clip clip, double volume) {
      if (clip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
         double var3 = Math.max(0.0, Math.min(1.0, volume));
         FloatControl var5 = (FloatControl)clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
         float var6 = (float)(Math.log10(var3 <= 0.0 ? 1.0E-4 : var3) * 20.0);
         var5.setValue(var6);
      }
   }

   @Generated
   private ClientSoundPlayer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
