package dlc.lumen.client.voice.call;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public final class MediaLink implements AutoCloseable {
   public static final int TYPE_AUDIO = 1;
   public static final int TYPE_VIDEO = 2;
   public static final int TYPE_KEEPALIVE = 3;
   public static final int MAX_PAYLOAD_BYTES = 1100;
   private static final byte[] BYTE = new byte[]{87, 76, 78, 68};
   private static final int INDEX = 16;
   private static final int INDEX2 = BYTE.length + 16;
   private static final int INDEX3 = 4096;
   private static final int INDEX4 = 1048576;
   private static final long TIMESTAMP = 200L;
   private static final long TIMESTAMP2 = 3000L;
   private final DatagramSocket localPort2;
   private final byte[] byte2;
   private final InetSocketAddress inetSocketAddress;
   private final Consumer<byte[]> consumer;
   private final Thread thread;
   private final Thread thread2;
   private final AtomicLong bytesSent = new AtomicLong();
   private final AtomicLong bytesReceived = new AtomicLong();
   private volatile boolean flag = true;
   private volatile InetSocketAddress inetSocketAddress2;
   private volatile InetSocketAddress direct;
   private volatile long timestamp;
   private volatile long lastPacketAt;

   public MediaLink(String relayHost, int relayPort, String tokenHex, Consumer<byte[]> onPayload) throws SocketException {
      this.byte2 = helper5(tokenHex);
      this.inetSocketAddress = new InetSocketAddress(relayHost, relayPort);
      this.consumer = onPayload;
      this.localPort2 = new DatagramSocket();
      this.localPort2.setSoTimeout(500);
      this.localPort2.setTrafficClass(16);
      this.helper3(true, 1048576);
      this.helper3(false, 1048576);
      this.thread = new Thread(this::helper, "lumen-call-rx");
      this.thread.setDaemon(true);
      this.thread.start();
      this.thread2 = new Thread(this::helper2, "lumen-call-keepalive");
      this.thread2.setDaemon(true);
      this.thread2.start();
   }

   public int getLocalPort() {
      return this.localPort2.getLocalPort();
   }

   public boolean isDirect() {
      return this.direct != null;
   }

   public long getBytesSent() {
      return this.bytesSent.get();
   }

   public long getBytesReceived() {
      return this.bytesReceived.get();
   }

   public long getLastPacketAt() {
      return this.lastPacketAt;
   }

   public void updatePeerCandidates(String localHost, int localPort, String publicHost, int publicPort) {
      InetSocketAddress var5 = helper4(localHost, localPort);
      if (var5 == null) {
         var5 = helper4(publicHost, publicPort);
      }

      if (!Objects.equals(var5, this.inetSocketAddress2)) {
         this.inetSocketAddress2 = var5;
      }
   }

   public void sendAudio(int sequence, byte[] audio, int length) {
      byte[] var4 = new byte[5 + length];
      var4[0] = 1;
      onMediaPacket2(var4, 1, sequence);
      System.arraycopy(audio, 0, var4, 5, length);
      this.onMediaPacket(var4);
   }

   public void sendVideoFragment(int frameId, int fragmentIndex, int fragmentCount, int width, int height, byte[] data, int offset, int length) {
      byte[] var9 = new byte[13 + length];
      var9[0] = 2;
      onMediaPacket2(var9, 1, frameId);
      onMediaPacket3(var9, 5, fragmentIndex);
      onMediaPacket3(var9, 7, fragmentCount);
      onMediaPacket3(var9, 9, width);
      onMediaPacket3(var9, 11, height);
      System.arraycopy(data, offset, var9, 13, length);
      this.onMediaPacket(var9);
   }

   private void onMediaPacket(byte[] payload) {
      if (this.flag) {
         InetSocketAddress var2 = this.direct;
         if (var2 != null && System.currentTimeMillis() - this.timestamp > 3000L) {
            this.direct = null;
            var2 = null;
         }

         try {
            if (var2 != null) {
               this.localPort2.send(new DatagramPacket(payload, payload.length, var2));
            } else {
               byte[] var3 = new byte[INDEX2 + payload.length];
               System.arraycopy(BYTE, 0, var3, 0, BYTE.length);
               System.arraycopy(this.byte2, 0, var3, BYTE.length, 16);
               System.arraycopy(payload, 0, var3, INDEX2, payload.length);
               this.localPort2.send(new DatagramPacket(var3, var3.length, this.inetSocketAddress));
            }

            this.bytesSent.addAndGet(payload.length);
         } catch (Exception var4) {
         }
      }
   }

   private void helper() {
      byte[] var1 = new byte[4096];

      while (this.flag) {
         DatagramPacket var2 = new DatagramPacket(var1, var1.length);

         try {
            this.localPort2.receive(var2);
         } catch (SocketTimeoutException var9) {
            continue;
         } catch (Exception var10) {
            if (this.flag) {
               continue;
            }

            return;
         }

         InetSocketAddress var3 = (InetSocketAddress)var2.getSocketAddress();
         long var4 = System.currentTimeMillis();
         this.lastPacketAt = var4;
         this.bytesReceived.addAndGet(var2.getLength());
         if (!this.inetSocketAddress.equals(var3)) {
            this.timestamp = var4;
            if (this.direct == null) {
               this.direct = var3;
            }
         }

         if (var2.getLength() > 0) {
            byte[] var6 = new byte[var2.getLength()];
            System.arraycopy(var1, 0, var6, 0, var2.getLength());
            if (var6[0] != 3) {
               try {
                  this.consumer.accept(var6);
               } catch (Throwable var8) {
               }
            }
         }
      }
   }

   private void helper2() {
      byte[] var1 = new byte[]{3};

      while (this.flag) {
         this.onMediaPacket(var1);
         InetSocketAddress var2 = this.inetSocketAddress2;
         if (var2 != null && this.direct == null) {
            try {
               this.localPort2.send(new DatagramPacket(var1, var1.length, var2));
            } catch (Exception var4) {
            }
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
            return;
         }
      }
   }

   @Override
   public void close() {
      this.flag = false;
      this.localPort2.close();
      this.thread.interrupt();
      this.thread2.interrupt();
   }

   public static int packetType(byte[] payload) {
      return payload.length == 0 ? -1 : payload[0];
   }

   public static int audioSequence(byte[] payload) {
      return helper6(payload, 1);
   }

   public static byte[] audioData(byte[] payload) {
      byte[] var1 = new byte[payload.length - 5];
      System.arraycopy(payload, 5, var1, 0, var1.length);
      return var1;
   }

   public static int videoFrameId(byte[] payload) {
      return helper6(payload, 1);
   }

   public static int videoFragmentIndex(byte[] payload) {
      return helper7(payload, 5);
   }

   public static int videoFragmentCount(byte[] payload) {
      return helper7(payload, 7);
   }

   public static int videoWidth(byte[] payload) {
      return helper7(payload, 9);
   }

   public static int videoHeight(byte[] payload) {
      return helper7(payload, 11);
   }

   public static int videoDataOffset() {
      return 13;
   }

   private void helper3(boolean receive, int bytes) {
      try {
         if (receive) {
            this.localPort2.setReceiveBufferSize(bytes);
         } else {
            this.localPort2.setSendBufferSize(bytes);
         }
      } catch (Exception var4) {
      }
   }

   private static InetSocketAddress helper4(String host, int port) {
      if (host != null && !host.isBlank() && port > 0 && port <= 65535) {
         try {
            return new InetSocketAddress(InetAddress.getByName(host), port);
         } catch (Exception var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static byte[] helper5(String hex) {
      byte[] var1 = new byte[16];
      if (hex == null) {
         return var1;
      }

      int var2 = Math.min(16, hex.length() / 2);

      for (int var3 = 0; var3 < var2; var3++) {
         try {
            var1[var3] = (byte)Integer.parseInt(hex.substring(var3 * 2, var3 * 2 + 2), 16);
         } catch (NumberFormatException var5) {
            return var1;
         }
      }

      return var1;
   }

   private static void onMediaPacket2(byte[] target, int offset, int value) {
      target[offset] = (byte)(value >>> 24);
      target[offset + 1] = (byte)(value >>> 16);
      target[offset + 2] = (byte)(value >>> 8);
      target[offset + 3] = (byte)value;
   }

   private static void onMediaPacket3(byte[] target, int offset, int value) {
      target[offset] = (byte)(value >>> 8);
      target[offset + 1] = (byte)value;
   }

   private static int helper6(byte[] source, int offset) {
      return (source[offset] & 0xFF) << 24 | (source[offset + 1] & 0xFF) << 16 | (source[offset + 2] & 0xFF) << 8 | source[offset + 3] & 0xFF;
   }

   private static int helper7(byte[] source, int offset) {
      return (source[offset] & 0xFF) << 8 | source[offset + 1] & 0xFF;
   }
}
