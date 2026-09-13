package dlc.lumen.client.voice.call;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public final class CallVideo implements AutoCloseable {
   private static final int INDEX = 1087;
   private static final int INDEX2 = 512;
   private static final int INDEX3 = 4;
   private static final long TIMESTAMP = 700L;
   private static final float VOLUME = 0.3F;
   private static final float VOLUME2 = 0.85F;
   private static final int IDLE_TIMEOUT_MS = 14000;
   private static final float VOLUME3 = 0.7F;
   private final MediaLink mediaLink;
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private final AtomicReference<BufferedImage> atomicReference = new AtomicReference<>();
   private final AtomicReference<CallVideo.DecodedFrame> atomicReference2 = new AtomicReference<>();
   private final Map<Integer, CallVideo.FrameAssembly> integers = new ConcurrentHashMap<>();
   private final BlockingQueue<CallVideo.PendingFrame> arrayBlockingQueue = new ArrayBlockingQueue<>(3);
   private final Thread thread;
   private final Thread thread2;
   private volatile boolean flag = true;
   private volatile boolean enabled2 = true;
   private volatile int targetFps = 20;
   private volatile float quality2 = 0.5F;
   private volatile int encodedFps;
   private volatile int lastFrameBytes;
   private volatile long receivedFrames;
   private volatile long droppedFrames;

   public CallVideo(MediaLink link) {
      this.mediaLink = link;
      this.thread = new Thread(this::helper, "lumen-call-video-tx");
      this.thread.setDaemon(true);
      this.thread.start();
      this.thread2 = new Thread(this::helper5, "lumen-call-video-rx");
      this.thread2.setDaemon(true);
      this.thread2.start();
   }

   public boolean isEnabled() {
      return this.enabled2;
   }

   public void setEnabled(boolean enabled) {
      this.enabled2 = enabled;
      if (!enabled) {
         this.atomicReference.set(null);
      }
   }

   public void setTargetFps(int fps) {
      this.targetFps = Math.max(5, Math.min(60, fps));
   }

   public int getTargetFps() {
      return this.targetFps;
   }

   public int getEncodedFps() {
      return this.encodedFps;
   }

   public int getLastFrameBytes() {
      return this.lastFrameBytes;
   }

   public float getQuality() {
      return this.quality2;
   }

   public long getReceivedFrames() {
      return this.receivedFrames;
   }

   public long getDroppedFrames() {
      return this.droppedFrames;
   }

   public void offerFrame(BufferedImage image) {
      if (this.enabled2 && image != null) {
         this.atomicReference.set(image);
      }
   }

   public CallVideo.DecodedFrame latestFrame() {
      return this.atomicReference2.get();
   }

   private void helper() {
      int var1 = 0;
      long var2 = System.currentTimeMillis();

      while (this.flag) {
         long var4 = System.currentTimeMillis();
         long var6 = 1000L / Math.max(1, this.targetFps);
         BufferedImage var8 = this.atomicReference.getAndSet(null);
         if (var8 != null && this.enabled2) {
            byte[] var9 = helper4(var8, this.quality2);
            if (var9 != null) {
               this.helper3(var9, var8.getWidth(), var8.getHeight());
               this.lastFrameBytes = var9.length;
               this.helper2(var9.length);
               var1++;
            }
         }

         long var13 = System.currentTimeMillis();
         if (var13 - var2 >= 1000L) {
            this.encodedFps = var1;
            var1 = 0;
            var2 = var13;
         }

         long var11 = var13 - var4;
         if (var11 < var6) {
            helper8(var6 - var11);
         }
      }
   }

   private void helper2(int frameBytes) {
      if (frameBytes > 14000 && this.quality2 > 0.3F) {
         this.quality2 = Math.max(0.3F, this.quality2 - 0.05F);
      } else if (frameBytes < 7000 && this.quality2 < 0.85F) {
         this.quality2 = Math.min(0.85F, this.quality2 + 0.02F);
      }
   }

   private void helper3(byte[] jpeg, int width, int height) {
      int var4 = (jpeg.length + 1087 - 1) / 1087;
      if (var4 > 0 && var4 <= 512) {
         int var5 = this.atomicInteger.incrementAndGet();
         long var6 = 1000000000L / Math.max(1, this.targetFps);
         long var8 = var4 > 1 ? (long)((float)var6 * 0.7F) / (var4 - 1) : 0L;
         long var10 = System.nanoTime();

         for (int var12 = 0; var12 < var4; var12++) {
            int var13 = var12 * 1087;
            int var14 = Math.min(1087, jpeg.length - var13);
            this.mediaLink.sendVideoFragment(var5, var12, var4, width, height, jpeg, var13, var14);
            if (var8 > 0L && var12 != var4 - 1) {
               var10 += var8;
               long var15 = var10 - System.nanoTime();
               if (var15 > 0L) {
                  LockSupport.parkNanos(var15);
               }
            }
         }
      }
   }

   private static byte[] helper4(BufferedImage image, float quality) {
      Iterator var2 = ImageIO.getImageWritersByFormatName("jpg");
      if (!var2.hasNext()) {
         return null;
      }

      ImageWriter var3 = (ImageWriter)var2.next();

      try {
         ByteArrayOutputStream var4 = new ByteArrayOutputStream(49152);

         try (MemoryCacheImageOutputStream var5 = new MemoryCacheImageOutputStream(var4)) {
            var3.setOutput(var5);
            ImageWriteParam var6 = var3.getDefaultWriteParam();
            if (var6.canWriteCompressed()) {
               var6.setCompressionMode(2);
               var6.setCompressionQuality(quality);
            }

            var3.write(null, new IIOImage(image, null, null), var6);
         }

         return var4.toByteArray();
      } catch (Exception var15) {
         return null;
      } finally {
         var3.dispose();
      }
   }

   public void onVideoPacket(byte[] payload) {
      if (payload.length > MediaLink.videoDataOffset()) {
         int var2 = MediaLink.videoFrameId(payload);
         int var3 = MediaLink.videoFragmentIndex(payload);
         int var4 = MediaLink.videoFragmentCount(payload);
         if (var4 > 0 && var4 <= 512 && var3 >= 0 && var3 < var4) {
            this.helper6(var2);
            CallVideo.FrameAssembly var5 = this.integers
               .computeIfAbsent(var2, id -> new CallVideo.FrameAssembly(var4, MediaLink.videoWidth(payload), MediaLink.videoHeight(payload)));
            if (var5.INDEX == var4) {
               int var6 = MediaLink.videoDataOffset();
               byte[] var7 = new byte[payload.length - var6];
               System.arraycopy(payload, var6, var7, 0, var7.length);
               if (var5.put(var3, var7)) {
                  if (var5.isComplete()) {
                     this.integers.remove(var2);
                     if (!this.arrayBlockingQueue.offer(new CallVideo.PendingFrame(var2, var5.assemble()))) {
                        this.arrayBlockingQueue.poll();
                        this.arrayBlockingQueue.offer(new CallVideo.PendingFrame(var2, var5.assemble()));
                        this.droppedFrames++;
                     }
                  }
               }
            }
         }
      }
   }

   private void helper5() {
      while (this.flag) {
         CallVideo.PendingFrame var1;
         try {
            var1 = this.arrayBlockingQueue.poll(200L, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            return;
         }

         if (var1 != null) {
            CallVideo.DecodedFrame var2 = helper7(var1.jpeg());
            if (var2 == null) {
               this.droppedFrames++;
            } else {
               CallVideo.DecodedFrame var3 = this.atomicReference2.get();
               if (var3 == null || var3.frameId() <= var1.frameId()) {
                  this.atomicReference2.set(new CallVideo.DecodedFrame(var1.frameId(), var2.width(), var2.height(), var2.pixels(), System.currentTimeMillis()));
                  this.receivedFrames++;
               }
            }
         }
      }
   }

   private void helper6(int currentFrameId) {
      if (this.integers.size() > 4) {
         long var2 = System.currentTimeMillis();
         this.integers.entrySet().removeIf(entry -> {
            boolean var5 = var2 - entry.getValue().VOLUME > 700L || entry.getKey() < currentFrameId - 4;
            if (var5) {
               this.droppedFrames++;
            }

            return var5;
         });
      }
   }

   private static CallVideo.DecodedFrame helper7(byte[] jpeg) {
      try {
         BufferedImage var1 = ImageIO.read(new ByteArrayInputStream(jpeg));
         if (var1 == null) {
            return null;
         }

         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         int[] var4 = var1.getRGB(0, 0, var2, var3, null, 0, var2);
         return new CallVideo.DecodedFrame(0, var2, var3, var4, 0L);
      } catch (Exception var5) {
         return null;
      }
   }

   @Override
   public void close() {
      this.flag = false;
      this.thread.interrupt();
      this.thread2.interrupt();
      this.arrayBlockingQueue.clear();
      this.integers.clear();
      this.atomicReference.set(null);
      this.atomicReference2.set(null);
   }

   private static void helper8(long millis) {
      try {
         Thread.sleep(Math.max(1L, millis));
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   public record DecodedFrame(int frameId, int width, int height, int[] pixels, long receivedAt) {

      public DecodedFrame(int frameId, int width, int height, int[] pixels, long receivedAt) {
         this.frameId = frameId;
         this.width = width;
         this.height = height;
         this.pixels = pixels;
         this.receivedAt = receivedAt;
      }

      public int frameId() {
         return this.frameId;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public int[] pixels() {
         return this.pixels;
      }

      public long receivedAt() {
         return this.receivedAt;
      }
   }

   private static final class FrameAssembly {
      private final int INDEX;
      private final int INDEX2;
      private final int INDEX3;
      private final byte[][] TIMESTAMP;
      private final long VOLUME = System.currentTimeMillis();
      private int VOLUME2;

      FrameAssembly(int fragmentCount, int width, int height) {
         this.INDEX = fragmentCount;
         this.INDEX2 = width;
         this.INDEX3 = height;
         this.TIMESTAMP = new byte[fragmentCount][];
      }

      synchronized boolean put(int index, byte[] data) {
         if (this.TIMESTAMP[index] != null) {
            return false;
         }

         this.TIMESTAMP[index] = data;
         this.VOLUME2++;
         return true;
      }

      synchronized boolean isComplete() {
         return this.VOLUME2 == this.INDEX;
      }

      synchronized byte[] assemble() {
         int var1 = 0;

         for (byte[] var5 : this.TIMESTAMP) {
            var1 += var5.length;
         }

         byte[] var8 = new byte[var1];
         int var9 = 0;

         for (byte[] var7 : this.TIMESTAMP) {
            System.arraycopy(var7, 0, var8, var9, var7.length);
            var9 += var7.length;
         }

         return var8;
      }
   }

   private record PendingFrame(int frameId, byte[] jpeg) {

      private PendingFrame(int frameId, byte[] jpeg) {
         this.frameId = frameId;
         this.jpeg = jpeg;
      }

      public int frameId() {
         return this.frameId;
      }

      public byte[] jpeg() {
         return this.jpeg;
      }
   }
}
