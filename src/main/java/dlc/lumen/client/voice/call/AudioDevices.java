package dlc.lumen.client.voice.call;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;

public final class AudioDevices {
   private static volatile String text = "";
   private static volatile String text2 = "";

   private AudioDevices() {
   }

   public static String getInputName() {
      return text.isBlank() ? "по умолчанию" : text;
   }

   public static String getOutputName() {
      return text2.isBlank() ? "по умолчанию" : text2;
   }

   public static List<String> listInputs(AudioFormat format) {
      return helper(format, true);
   }

   public static List<String> listOutputs(AudioFormat format) {
      return helper(format, false);
   }

   private static List<String> helper(AudioFormat format, boolean input) {
      Class<? extends DataLine> var2 = input ? TargetDataLine.class : SourceDataLine.class;
      Info var3 = new Info(input ? TargetDataLine.class : SourceDataLine.class, format);
      ArrayList<String> var4 = new ArrayList<>();

      for (javax.sound.sampled.Mixer.Info var8 : AudioSystem.getMixerInfo()) {
         try {
            Mixer var9 = AudioSystem.getMixer(var8);
            javax.sound.sampled.Line.Info[] var10 = input ? var9.getTargetLineInfo() : var9.getSourceLineInfo();
            if (var10.length != 0 && var9.isLineSupported(var3)) {
               var4.add(var8.getName());
            }
         } catch (Throwable var11) {
         }
      }

      return var4;
   }

   public static boolean selectInput(AudioFormat format, int index) {
      List var2 = listInputs(format);
      if (index >= 0 && index < var2.size()) {
         text = (String)var2.get(index);
         return true;
      } else {
         return false;
      }
   }

   public static boolean selectOutput(AudioFormat format, int index) {
      List var2 = listOutputs(format);
      if (index >= 0 && index < var2.size()) {
         text2 = (String)var2.get(index);
         return true;
      } else {
         return false;
      }
   }

   public static void resetInput() {
      text = "";
   }

   public static void resetOutput() {
      text2 = "";
   }

   public static TargetDataLine openInput(AudioFormat format, int bufferBytes) throws Exception {
      TargetDataLine var2 = (TargetDataLine)helper2(format, text, true);
      if (var2 == null) {
         var2 = (TargetDataLine)AudioSystem.getLine(new Info(TargetDataLine.class, format));
      }

      var2.open(format, bufferBytes);
      var2.start();
      return var2;
   }

   public static SourceDataLine openOutput(AudioFormat format, int bufferBytes) throws Exception {
      SourceDataLine var2 = (SourceDataLine)helper2(format, text2, false);
      if (var2 == null) {
         var2 = (SourceDataLine)AudioSystem.getLine(new Info(SourceDataLine.class, format));
      }

      var2.open(format, bufferBytes);
      var2.start();
      return var2;
   }

   private static Line helper2(AudioFormat format, String name, boolean input) {
      if (name != null && !name.isBlank()) {
         Info var3 = new Info(input ? TargetDataLine.class : SourceDataLine.class, format);

         for (javax.sound.sampled.Mixer.Info var7 : AudioSystem.getMixerInfo()) {
            if (var7.getName().equals(name)) {
               try {
                  Mixer var8 = AudioSystem.getMixer(var7);
                  if (var8.isLineSupported(var3)) {
                     return var8.getLine(var3);
                  }
               } catch (Throwable var9) {
                  return null;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
