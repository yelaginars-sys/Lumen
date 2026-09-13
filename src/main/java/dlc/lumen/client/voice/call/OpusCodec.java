package dlc.lumen.client.voice.call;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class OpusCodec {
   public static final int SAMPLE_RATE = 48000;
   public static final int CHANNELS = 1;
   public static final int FRAME_MS = 20;
   public static final int FRAME_SAMPLES = 960;
   private static final int INDEX = 32000;
   private static final int INDEX2 = 1275;
   private static final String OPUS_ENCODER_CLASS_NAME = "de.maxhenkel.opus4j.OpusEncoder";
   private static final String OPUS_DECODER_CLASS_NAME = "de.maxhenkel.opus4j.OpusDecoder";
   private static final String TEXT = "de.maxhenkel.opus4j.OpusEncoder$Application";
   private static final String OPUS_ENCODER_CLASS_NAME2 = "de.maxhenkel.voicechat.concentus.OpusEncoder";
   private static final String OPUS_DECODER_CLASS_NAME2 = "de.maxhenkel.voicechat.concentus.OpusDecoder";
   private static final String OPUS_APPLICATION_CLASS_NAME = "de.maxhenkel.voicechat.concentus.OpusApplication";
   public static final int WIRE_OPUS = 1;
   public static final int WIRE_ADPCM = 2;
   private static final int POLL_INTERVAL_MS = 16000;
   private static final int INDEX3 = 3;
   private static final int INDEX4 = 320;
   private static final int INDEX5 = 4;
   private static final int[] value2 = new int[]{
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      16,
      17,
      19,
      21,
      23,
      25,
      28,
      31,
      34,
      37,
      41,
      45,
      50,
      55,
      60,
      66,
      73,
      80,
      88,
      97,
      107,
      118,
      130,
      143,
      157,
      173,
      190,
      209,
      230,
      253,
      279,
      307,
      337,
      371,
      408,
      449,
      494,
      544,
      598,
      658,
      724,
      796,
      876,
      963,
      1060,
      1166,
      1282,
      1411,
      1552,
      1707,
      1878,
      2066,
      2272,
      2499,
      2749,
      3024,
      3327,
      3660,
      4026,
      4428,
      4871,
      5358,
      5894,
      6484,
      7132,
      7845,
      8630,
      9493,
      10442,
      11487,
      12635,
      13899,
      15289,
      16818,
      18500,
      20350,
      22385,
      24623,
      27086,
      29794,
      32767
   };
   private static final int[] INDEX6 = new int[]{-1, -1, -1, -1, 2, 4, 6, 8, -1, -1, -1, -1, 2, 4, 6, 8};

   private OpusCodec() {
   }

   public static OpusCodec.Decoder createDecoderFor(int wireId) {
      if (wireId == 2) {
         return new OpusCodec.AdpcmDecoder();
      }

      if (wireId != 1) {
         return null;
      }

      OpusCodec.Decoder var1 = helper2();
      return var1 != null ? var1 : helper4();
   }

   public static OpusCodec.Encoder createEncoder() {
      OpusCodec.Encoder var0 = helper();
      if (var0 != null) {
         return var0;
      }

      var0 = helper3();
      return var0 != null ? var0 : new OpusCodec.AdpcmEncoder();
   }

   public static OpusCodec.Decoder createDecoder() {
      OpusCodec.Decoder var0 = helper2();
      if (var0 != null) {
         return var0;
      }

      var0 = helper4();
      return var0 != null ? var0 : new OpusCodec.AdpcmDecoder();
   }

   private static OpusCodec.Encoder helper() {
      try {
         Class var0 = Class.forName("de.maxhenkel.opus4j.OpusEncoder");
         Class var1 = Class.forName("de.maxhenkel.opus4j.OpusEncoder$Application");
         Object var2 = helper9(var1, "VOIP");
         if (var2 == null) {
            return null;
         }

         Constructor var3 = var0.getConstructor(int.class, int.class, var1);
         final Object var4 = var3.newInstance(48000, 1, var2);
         final Method var5 = var0.getMethod("encode", short[].class);
         Method var6 = var0.getMethod("close");
         return new OpusCodec.ReflectiveEncoder(OpusCodec.Backend.NATIVE_OPUS, var4, var6) {
            @Override
            public byte[] encode(short[] pcm) {
               try {
                  return (byte[])var5.invoke(var4, pcm);
               } catch (Throwable var3x) {
                  return null;
               }
            }
         };
      } catch (Throwable var7) {
         return null;
      }
   }

   private static OpusCodec.Decoder helper2() {
      try {
         Class var0 = Class.forName("de.maxhenkel.opus4j.OpusDecoder");
         final Object var1 = var0.getConstructor(int.class, int.class).newInstance(48000, 1);
         var0.getMethod("setFrameSize", int.class).invoke(var1, 960);
         final Method var2 = var0.getMethod("decode", byte[].class);
         Method var3 = var0.getMethod("close");
         return new OpusCodec.ReflectiveDecoder(OpusCodec.Backend.NATIVE_OPUS, var1, var3) {
            @Override
            public short[] decode(byte[] packet) {
               try {
                  return (short[])var2.invoke(var1, packet);
               } catch (Throwable var3x) {
                  return null;
               }
            }
         };
      } catch (Throwable var4) {
         return null;
      }
   }

   private static OpusCodec.Encoder helper3() {
      try {
         Class var0 = Class.forName("de.maxhenkel.voicechat.concentus.OpusEncoder");
         Class var1 = Class.forName("de.maxhenkel.voicechat.concentus.OpusApplication");
         Object var2 = helper9(var1, "OPUS_APPLICATION_VOIP");
         if (var2 == null) {
            return null;
         }

         final Object var3 = var0.getConstructor(int.class, int.class, var1).newInstance(48000, 1, var2);
         helper10(var0, var3, "setBitrate", int.class, 32000);
         helper10(var0, var3, "setComplexity", int.class, 5);
         helper10(var0, var3, "setUseInbandFEC", boolean.class, true);
         helper10(var0, var3, "setPacketLossPercent", int.class, 10);
         final Method var4 = var0.getMethod("encode", short[].class, int.class, int.class, byte[].class, int.class, int.class);
         return new OpusCodec.ReflectiveEncoder(OpusCodec.Backend.JAVA_OPUS, var3, null) {
            private final byte[] INDEX = new byte[1275];

            @Override
            public byte[] encode(short[] pcm) {
               try {
                  int var2x = (Integer)var4.invoke(var3, pcm, 0, 960, this.INDEX, 0, this.INDEX.length);
                  if (var2x <= 0) {
                     return null;
                  }

                  byte[] var3x = new byte[var2x];
                  System.arraycopy(this.INDEX, 0, var3x, 0, var2x);
                  return var3x;
               } catch (Throwable var4x) {
                  return null;
               }
            }
         };
      } catch (Throwable var5) {
         return null;
      }
   }

   private static OpusCodec.Decoder helper4() {
      try {
         Class var0 = Class.forName("de.maxhenkel.voicechat.concentus.OpusDecoder");
         final Object var1 = var0.getConstructor(int.class, int.class).newInstance(48000, 1);
         final Method var2 = var0.getMethod("decode", byte[].class, int.class, int.class, short[].class, int.class, int.class, boolean.class);
         return new OpusCodec.ReflectiveDecoder(OpusCodec.Backend.JAVA_OPUS, var1, null) {
            @Override
            public short[] decode(byte[] packet) {
               try {
                  short[] var2x = new short[960];
                  int var3 = (Integer)var2.invoke(var1, packet, 0, packet.length, var2x, 0, 960, false);
                  return var3 <= 0 ? null : var2x;
               } catch (Throwable var4) {
                  return null;
               }
            }
         };
      } catch (Throwable var3) {
         return null;
      }
   }

   private static int resolveInt(short sample, int[] state) {
      int var2 = state[0];
      int var3 = state[1];
      int var4 = value2[var3];
      int var5 = sample - var2;
      byte var6 = 0;
      if (var5 < 0) {
         var6 = 8;
         var5 = -var5;
      }

      int var7 = var4 >> 3;
      if (var5 >= var4) {
         var6 |= 4;
         var5 -= var4;
         var7 += var4;
      }

      if (var5 >= var4 >> 1) {
         var6 |= 2;
         var5 -= var4 >> 1;
         var7 += var4 >> 1;
      }

      if (var5 >= var4 >> 2) {
         var6 |= 1;
         var7 += var4 >> 2;
      }

      state[0] = helper8((var6 & 8) != 0 ? var2 - var7 : var2 + var7);
      state[1] = helper7(var3 + INDEX6[var6]);
      return var6;
   }

   private static short[] helper5(short[] pcm) {
      short[] var1 = new short[320];

      for (int var2 = 0; var2 < var1.length; var2++) {
         int var3 = var2 * 3;
         short var4 = 0;

         for (int var5 = 0; var5 < 3; var5++) {
            var4 += pcm[var3 + var5];
         }

         var1[var2] = (short)(var4 / 3);
      }

      return var1;
   }

   private static short[] helper6(short[] narrow) {
      short[] var1 = new short[960];

      for (int var2 = 0; var2 < narrow.length; var2++) {
         short var3 = narrow[var2];
         short var4 = var2 + 1 < narrow.length ? narrow[var2 + 1] : var3;

         for (int var5 = 0; var5 < 3; var5++) {
            int var6 = var2 * 3 + var5;
            if (var6 >= var1.length) {
               break;
            }

            var1[var6] = (short)(var3 + (var4 - var3) * var5 / 3);
         }
      }

      return var1;
   }

   private static int helper7(int index) {
      return Math.max(0, Math.min(value2.length - 1, index));
   }

   private static int helper8(int sample) {
      return Math.max(-32768, Math.min(32767, sample));
   }

   private static void updateState(Method method, Object instance) {
      if (method != null) {
         try {
            method.invoke(instance);
         } catch (Throwable var3) {
         }
      }
   }

   private static Object helper9(Class<?> enumClass, String name) {
      for (Object var5 : enumClass.getEnumConstants()) {
         if (((Enum)var5).name().equals(name)) {
            return var5;
         }
      }

      return null;
   }

   private static void helper10(Class<?> owner, Object instance, String method, Class<?> type, Object value) {
      try {
         owner.getMethod(method, type).invoke(instance, value);
      } catch (Throwable var6) {
      }
   }

   private static final class AdpcmDecoder implements OpusCodec.Decoder {
      @Override
      public short[] decode(byte[] packet) {
         if (packet != null && packet.length > 4) {
            int var2 = (short)((packet[0] & 255) << 8 | packet[1] & 0xFF);
            int var3 = OpusCodec.helper7(packet[2]);
            int var4 = Math.min(320, (packet.length - 4) * 2);
            short[] var5 = new short[var4];

            for (int var6 = 0; var6 < var4; var6++) {
               int var7 = packet[4 + var6 / 2] & 255;
               int var8 = (var6 & 1) == 0 ? var7 & 15 : var7 >> 4 & 15;
               int var9 = OpusCodec.value2[var3];
               int var10 = var9 >> 3;
               if ((var8 & 4) != 0) {
                  var10 += var9;
               }

               if ((var8 & 2) != 0) {
                  var10 += var9 >> 1;
               }

               if ((var8 & 1) != 0) {
                  var10 += var9 >> 2;
               }

               var2 = OpusCodec.helper8((var8 & 8) != 0 ? var2 - var10 : var2 + var10);
               var3 = OpusCodec.helper7(var3 + OpusCodec.INDEX6[var8]);
               var5[var6] = (short)var2;
            }

            return OpusCodec.helper6(var5);
         } else {
            return null;
         }
      }

      @Override
      public OpusCodec.Backend backend() {
         return OpusCodec.Backend.PCM_ADPCM;
      }

      @Override
      public void close() {
      }
   }

   private static final class AdpcmEncoder implements OpusCodec.Encoder {
      private int INDEX;
      private final int[] INDEX2 = new int[2];

      @Override
      public byte[] encode(short[] pcm) {
         if (pcm != null && pcm.length >= 960) {
            short[] var2 = OpusCodec.helper5(pcm);
            byte[] var3 = new byte[4 + (var2.length + 1) / 2];
            this.INDEX2[0] = var2[0];
            this.INDEX2[1] = this.INDEX;
            var3[0] = (byte)(this.INDEX2[0] >> 8);
            var3[1] = (byte)this.INDEX2[0];
            var3[2] = (byte)this.INDEX2[1];
            var3[3] = 0;
            int var4 = 4;

            for (byte var5 = 0; var5 < var2.length; var5 += 2) {
               int var6 = OpusCodec.resolveInt(var2[var5], this.INDEX2);
               int var7 = var5 + 1 < var2.length ? OpusCodec.resolveInt(var2[var5 + 1], this.INDEX2) : 0;
               var3[var4++] = (byte)(var7 << 4 | var6);
            }

            this.INDEX = this.INDEX2[1];
            return var3;
         } else {
            return null;
         }
      }

      @Override
      public OpusCodec.Backend backend() {
         return OpusCodec.Backend.PCM_ADPCM;
      }

      @Override
      public void close() {
      }
   }

   public enum Backend {
      NATIVE_OPUS("Opus (нативный)", 1),
      JAVA_OPUS("Opus (Java)", 1),
      PCM_ADPCM("ADPCM 16 кГц", 2);

      private final String INDEX;
      private final int INDEX2;

      Backend(String label, int wireId) {
         this.INDEX = label;
         this.INDEX2 = wireId;
      }

      public String label() {
         return this.INDEX;
      }

      public int wireId() {
         return this.INDEX2;
      }
   }

   public interface Decoder extends AutoCloseable {
      short[] decode(byte[] var1);

      OpusCodec.Backend backend();

      @Override
      void close();
   }

   public interface Encoder extends AutoCloseable {
      byte[] encode(short[] var1);

      OpusCodec.Backend backend();

      @Override
      void close();
   }

   private abstract static class ReflectiveDecoder implements OpusCodec.Decoder {
      private final OpusCodec.Backend INDEX;
      private final Object INDEX2;
      private final Method OPUS_ENCODER_CLASS_NAME;

      ReflectiveDecoder(OpusCodec.Backend backend, Object instance, Method closeMethod) {
         this.INDEX = backend;
         this.INDEX2 = instance;
         this.OPUS_ENCODER_CLASS_NAME = closeMethod;
      }

      @Override
      public OpusCodec.Backend backend() {
         return this.INDEX;
      }

      @Override
      public void close() {
         OpusCodec.updateState(this.OPUS_ENCODER_CLASS_NAME, this.INDEX2);
      }
   }

   private abstract static class ReflectiveEncoder implements OpusCodec.Encoder {
      private final OpusCodec.Backend INDEX;
      private final Object INDEX2;
      private final Method OPUS_ENCODER_CLASS_NAME;

      ReflectiveEncoder(OpusCodec.Backend backend, Object instance, Method closeMethod) {
         this.INDEX = backend;
         this.INDEX2 = instance;
         this.OPUS_ENCODER_CLASS_NAME = closeMethod;
      }

      @Override
      public OpusCodec.Backend backend() {
         return this.INDEX;
      }

      @Override
      public void close() {
         OpusCodec.updateState(this.OPUS_ENCODER_CLASS_NAME, this.INDEX2);
      }
   }
}
