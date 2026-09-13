package dlc.lumen.client.voice.call;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;

public final class CallAudio implements AutoCloseable {
   private static final AudioFormat AUDIO_FORMAT = new AudioFormat(48000.0F, 16, 1, true, false);
   private static final int INDEX = 960;
   private static final int INDEX2 = 1920;
   private static final int INDEX3 = 48000;
   private static final int INDEX4 = 48000;
   private static final int INDEX5 = 12;
   private static final int INDEX6 = 25;
   private static final byte BYTE = 1;
   private static final int INDEX7 = 6;
   private static final double LEVEL = 900.0;
   private static final double LEVEL2 = 420.0;
   private static final long TIMESTAMP = 220L;
   private static final float VOLUME = 0.06F;
   private final MediaLink mediaLink;
   private final OpusCodec.Encoder encoder;
   private final OpusCodec.Decoder decoder;
   private final PcmRingBuffer underruns = new PcmRingBuffer(48000);
   private final AudioSafety safetyMuted = new AudioSafety();
   private final TreeMap<Integer, CallAudio.EncodedFrame> treeMap = new TreeMap<>();
   private final Map<Integer, OpusCodec.Decoder> integers = new HashMap<>();
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private TargetDataLine targetDataLine;
   private SourceDataLine sourceDataLine;
   private Thread thread;
   private Thread thread2;
   private volatile boolean flag = true;
   private volatile boolean muted2;
   private volatile boolean deafened2;
   private volatile float outputVolume = 0.55F;
   private volatile boolean localSpeaking;
   private volatile boolean peerSpeaking;
   private volatile long peerSpeaking2;
   private volatile String failure = "";
   private volatile long timestamp;
   private volatile long sentPackets;
   private volatile long receivedPackets;
   private volatile long decodeFailures;
   private volatile long recoveredFrames;
   private volatile long lostFrames;
   private volatile long versionMismatches;
   private volatile long unsupportedCodecPackets;
   private volatile double micLevel;
   private volatile double peerLevel;
   private int index = -1;
   private byte[] byte2;

   public CallAudio(MediaLink link) {
      this.mediaLink = link;
      this.encoder = OpusCodec.createEncoder();
      this.decoder = OpusCodec.createDecoder();
      this.helper();
      this.helper6();
   }

   public static AudioFormat format() {
      return AUDIO_FORMAT;
   }

   public OpusCodec.Backend backend() {
      return this.encoder.backend();
   }

   public boolean isMuted() {
      return this.muted2;
   }

   public void setMuted(boolean muted) {
      this.muted2 = muted;
      if (muted) {
         this.localSpeaking = false;
      }
   }

   public boolean isDeafened() {
      return this.deafened2;
   }

   public void setDeafened(boolean deafened) {
      this.deafened2 = deafened;
      if (deafened) {
         this.underruns.clear();
         synchronized (this.treeMap) {
            this.treeMap.clear();
         }
      }
   }

   public void setOutputVolume(float volume) {
      this.outputVolume = AudioSafety.clampGain(volume);
   }

   public float getOutputVolume() {
      return this.outputVolume;
   }

   public boolean isLocalSpeaking() {
      return this.localSpeaking;
   }

   public boolean isPeerSpeaking() {
      return this.peerSpeaking && System.currentTimeMillis() - this.peerSpeaking2 < 350L;
   }

   public boolean isSafetyMuted() {
      return this.safetyMuted.isMuted();
   }

   public void resetSafety() {
      this.safetyMuted.reset();
   }

   public String getFailure() {
      return this.failure;
   }

   public long getSentPackets() {
      return this.sentPackets;
   }

   public long getReceivedPackets() {
      return this.receivedPackets;
   }

   public long getDecodeFailures() {
      return this.decodeFailures;
   }

   public long getRecoveredFrames() {
      return this.recoveredFrames;
   }

   public long getLostFrames() {
      return this.lostFrames;
   }

   public long getVersionMismatches() {
      return this.versionMismatches;
   }

   public long getUnsupportedCodecPackets() {
      return this.unsupportedCodecPackets;
   }

   public String getCompatibilityProblem() {
      if (this.versionMismatches > 20L) {
         return "у собеседника другая версия мода — обновите оба клиента";
      } else {
         return this.unsupportedCodecPackets > 20L
            ? "собеседник использует Opus, а в этой сборке его нет (Simple Voice Chat не попал в classpath) — соберите прод-джарник"
            : "";
      }
   }

   public long getUnderruns() {
      return this.underruns.getUnderruns();
   }

   public double getMicLevel() {
      return this.micLevel;
   }

   public double getPeerLevel() {
      return this.peerLevel;
   }

   private void helper() {
      try {
         if (!AudioSystem.isLineSupported(new Info(TargetDataLine.class, AUDIO_FORMAT))) {
            this.failure = "микрофон не поддерживает 48 кГц моно";
            return;
         }

         this.targetDataLine = AudioDevices.openInput(AUDIO_FORMAT, 48000);
      } catch (Exception var2) {
         this.failure = "микрофон недоступен: " + helper13(var2);
         this.targetDataLine = null;
         return;
      }

      this.thread = new Thread(this::helper2, "lumen-call-mic");
      this.thread.setDaemon(true);
      this.thread.setPriority(10);
      this.thread.start();
   }

   private void helper2() {
      byte[] var1 = new byte[1920];
      short[] var2 = new short[960];

      while (this.flag) {
         int var3 = 0;

         while (var3 < 1920 && this.flag) {
            int var4 = this.targetDataLine.read(var1, var3, 1920 - var3);
            if (var4 <= 0) {
               break;
            }

            var3 += var4;
         }

         if (var3 >= 1920) {
            double var9 = 0.0;

            for (int var6 = 0; var6 < var2.length; var6++) {
               var2[var6] = (short)(var1[var6 * 2] & 0xFF | var1[var6 * 2 + 1] << 8);
               var9 += (double)var2[var6] * var2[var6];
            }

            double var10 = Math.sqrt(var9 / var2.length);
            this.micLevel = var10;
            this.localSpeaking = !this.muted2 && var10 > 900.0;
            if (!this.muted2) {
               if (this.helper10()) {
                  for (int var8 = 0; var8 < var2.length; var8++) {
                     var2[var8] = (short)(var2[var8] * 0.06F);
                  }
               }

               if (var10 < 420.0) {
                  for (int var11 = 0; var11 < var2.length; var11++) {
                     var2[var11] = (short)(var2[var11] * 0.15F);
                  }
               }

               byte[] var12 = this.encoder.encode(var2);
               if (var12 != null && var12.length != 0) {
                  this.helper3(var12);
                  this.byte2 = var12;
               }
            }
         }
      }
   }

   private void helper3(byte[] current) {
      byte[] var2 = this.byte2;
      int var3 = var2 == null ? 0 : var2.length;
      int var4 = 6 + current.length + var3;
      if (var4 > 1100) {
         var2 = null;
         var3 = 0;
         var4 = 6 + current.length;
         if (var4 > 1100) {
            return;
         }
      }

      byte[] var5 = new byte[var4];
      var5[0] = 1;
      var5[1] = (byte)this.encoder.backend().wireId();
      var5[2] = (byte)(current.length >>> 8);
      var5[3] = (byte)current.length;
      var5[4] = (byte)(var3 >>> 8);
      var5[5] = (byte)var3;
      System.arraycopy(current, 0, var5, 6, current.length);
      if (var2 != null) {
         System.arraycopy(var2, 0, var5, 6 + current.length, var3);
      }

      this.mediaLink.sendAudio(this.atomicInteger.incrementAndGet(), var5, var5.length);
      this.sentPackets++;
   }

   public void onAudioPacket(byte[] packet) {
      if (!this.deafened2 && packet.length > 11) {
         int var2 = MediaLink.audioSequence(packet);
         byte[] var3 = MediaLink.audioData(packet);
         if (var3.length >= 6) {
            if (var3[0] != 1) {
               this.versionMismatches++;
            } else {
               int var4 = var3[1] & 255;
               OpusCodec.Decoder var5 = this.helper4(var4);
               if (var5 == null) {
                  this.unsupportedCodecPackets++;
               } else {
                  int var6 = (var3[2] & 255) << 8 | var3[3] & 255;
                  int var7 = (var3[4] & 255) << 8 | var3[5] & 255;
                  if (var6 > 0 && 6 + var6 + var7 <= var3.length) {
                     this.receivedPackets++;
                     this.helper5(var2, var4, var3, 6, var6, false);
                     if (var7 > 0) {
                        this.helper5(var2 - 1, var4, var3, 6 + var6, var7, true);
                     }
                  } else {
                     this.decodeFailures++;
                  }
               }
            }
         }
      }
   }

   private OpusCodec.Decoder helper4(int wireId) {
      OpusCodec.Decoder var2 = this.integers.get(wireId);
      if (var2 != null) {
         return var2;
      }

      synchronized (this.integers) {
         return this.integers.computeIfAbsent(wireId, OpusCodec::createDecoderFor);
      }
   }

   private void helper5(int sequence, int wireId, byte[] source, int offset, int length, boolean redundant) {
      synchronized (this.treeMap) {
         if (this.index < 0 || sequence >= this.index) {
            if (!this.treeMap.containsKey(sequence)) {
               byte[] var8 = new byte[length];
               System.arraycopy(source, offset, var8, 0, length);
               this.treeMap.put(sequence, new CallAudio.EncodedFrame(wireId, var8));
               if (redundant) {
                  this.recoveredFrames++;
               }

               while (this.treeMap.size() > 50) {
                  this.treeMap.pollFirstEntry();
               }
            }
         }
      }
   }

   private void helper6() {
      try {
         if (!AudioSystem.isLineSupported(new Info(SourceDataLine.class, AUDIO_FORMAT))) {
            this.helper12("динамик не поддерживает 48 кГц моно");
            return;
         }

         this.sourceDataLine = AudioDevices.openOutput(AUDIO_FORMAT, 48000);
      } catch (Exception var2) {
         this.helper12("динамик недоступен: " + helper13(var2));
         this.sourceDataLine = null;
         return;
      }

      this.thread2 = new Thread(this::helper7, "lumen-call-speaker");
      this.thread2.setDaemon(true);
      this.thread2.setPriority(10);
      this.thread2.start();
   }

   private void helper7() {
      short[] var1 = new short[960];
      byte[] var2 = new byte[1920];

      while (this.flag) {
         this.helper8();
         this.underruns.read(var1);
         if (!this.safetyMuted.isGarbage(var1) && !this.deafened2) {
            float var3 = AudioSafety.clampGain(this.outputVolume);

            for (int var4 = 0; var4 < var1.length; var4++) {
               int var5 = AudioSafety.limit(var1[var4] * var3);
               var2[var4 * 2] = (byte)var5;
               var2[var4 * 2 + 1] = (byte)(var5 >> 8);
            }

            this.sourceDataLine.write(var2, 0, var2.length);
            this.helper9(var1);
         } else {
            Arrays.fill(var2, (byte)0);
            this.sourceDataLine.write(var2, 0, var2.length);
         }
      }
   }

   private void helper8() {
      while (true) {
         CallAudio.EncodedFrame var1;
         synchronized (this.treeMap) {
            if (this.treeMap.isEmpty()) {
               return;
            }

            if (this.index < 0) {
               if (this.treeMap.size() < 12) {
                  return;
               }

               this.index = this.treeMap.firstKey();
            }

            var1 = this.treeMap.remove(this.index);
            if (var1 == null) {
               if (this.treeMap.lastKey() - this.index < 25) {
                  return;
               }

               this.lostFrames++;
               this.index++;
               continue;
            }

            this.index++;
         }

         OpusCodec.Decoder var11 = this.helper4(var1.wireId());
         if (var11 == null) {
            this.unsupportedCodecPackets++;
         } else {
            short[] var3 = var11.decode(var1.data());
            if (var3 == null) {
               this.decodeFailures++;
            } else {
               double var4 = 0.0;

               for (short var9 : var3) {
                  var4 += (double)var9 * var9;
               }

               this.peerLevel = Math.sqrt(var4 / var3.length);
               if (this.peerLevel > 900.0) {
                  this.peerSpeaking = true;
                  this.peerSpeaking2 = System.currentTimeMillis();
               }

               this.underruns.write(var3);
               if (this.underruns.available() >= 11520) {
                  return;
               }
            }
         }
      }
   }

   private void helper9(short[] frame) {
      double var2 = 0.0;

      for (short var7 : frame) {
         var2 += (double)var7 * var7;
      }

      if (Math.sqrt(var2 / frame.length) > 420.0) {
         this.timestamp = System.currentTimeMillis();
      }
   }

   private boolean helper10() {
      return !this.deafened2 && System.currentTimeMillis() - this.timestamp < 220L;
   }

   @Override
   public void close() {
      this.flag = false;
      if (this.thread != null) {
         this.thread.interrupt();
      }

      if (this.thread2 != null) {
         this.thread2.interrupt();
      }

      this.helper11(this.targetDataLine);
      this.helper11(this.sourceDataLine);
      this.encoder.close();
      this.decoder.close();
      synchronized (this.integers) {
         for (OpusCodec.Decoder var3 : this.integers.values()) {
            if (var3 != null) {
               var3.close();
            }
         }

         this.integers.clear();
      }

      this.underruns.clear();
      synchronized (this.treeMap) {
         this.treeMap.clear();
      }
   }

   private void helper11(DataLine line) {
      if (line != null) {
         try {
            line.stop();
            line.close();
         } catch (Exception var3) {
         }
      }
   }

   private void helper12(String message) {
      this.failure = this.failure.isEmpty() ? message : this.failure + "; " + message;
   }

   private static String helper13(Exception exception) {
      String var1 = exception.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : exception.getClass().getSimpleName();
   }

   private record EncodedFrame(int wireId, byte[] data) {

      private EncodedFrame(int wireId, byte[] data) {
         this.wireId = wireId;
         this.data = data;
      }

      public int wireId() {
         return this.wireId;
      }

      public byte[] data() {
         return this.data;
      }
   }
}
