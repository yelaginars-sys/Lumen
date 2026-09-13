package dlc.lumen.client.voice.call;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dlc.lumen.client.render.portal.PortalScreenCapture;
import java.awt.image.BufferedImage;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;

public final class PortalMedia {
   private static final int[][] RESOLUTIONS = new int[][]{{480, 270}, {640, 360}, {854, 480}, {1280, 720}};
   private final PortalScreenCapture screenCapture = new PortalScreenCapture();
   private volatile MediaLink mediaLink;
   private volatile CallAudio callAudio;
   private volatile CallVideo callVideo;
   private volatile int resolutionIndex = 0;
   private volatile boolean videoEnabled = true;
   private volatile boolean muted;
   private volatile boolean deafened;
   private volatile float volume = 0.55F;
   private volatile int targetFps = 20;
   private volatile boolean running;

   public synchronized String start(String relayHost, int relayPort, String token) {
      this.stop();
      if (relayHost != null && !relayHost.isBlank() && relayPort > 0) {
         try {
            MediaLink var4 = new MediaLink(relayHost, relayPort, token, this::onMediaPacket);
            CallVideo var7 = new CallVideo(var4);
            var7.setEnabled(this.videoEnabled);
            var7.setTargetFps(this.targetFps);
            this.mediaLink = var4;
            this.callAudio = null;
            this.callVideo = var7;
            this.running = true;
            return "";
         } catch (Exception var6) {
            this.stop();
            String var5 = var6.getMessage();
            return "не удалось открыть медиа-канал: " + (var5 == null ? var6.getClass().getSimpleName() : var5);
         }
      } else {
         return "сервер не сообщил адрес медиа-релея";
      }
   }

   public synchronized void stop() {
      this.running = false;
      closeQuietly(this.callVideo);
      closeQuietly(this.callAudio);
      closeQuietly(this.mediaLink);
      this.callVideo = null;
      this.callAudio = null;
      this.mediaLink = null;
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null) {
         var1.execute(this.screenCapture::close);
      }
   }

   public boolean isRunning() {
      return this.running && this.mediaLink != null;
   }

   public void captureFrame() {
      CallVideo var1 = this.callVideo;
      if (this.running && var1 != null && this.videoEnabled) {
         int[] var2 = RESOLUTIONS[this.resolutionIndex];
         long var3 = 1000L / Math.max(1, this.targetFps);
         BufferedImage var5 = this.screenCapture.capture(var2[0], var2[1], var3);
         if (var5 != null) {
            var1.offerFrame(var5);
         }
      }
   }

   public String getCaptureFailure() {
      return this.screenCapture.getFailure();
   }

   private void onMediaPacket(byte[] payload) {
      switch (MediaLink.packetType(payload)) {
         case 1:
            CallAudio var3 = this.callAudio;
            if (var3 != null) {
               var3.onAudioPacket(payload);
            }
            break;
         case 2:
            CallVideo var2 = this.callVideo;
            if (var2 != null) {
               var2.onVideoPacket(payload);
            }
      }
   }

   public void updatePeer(JsonObject call) {
      MediaLink var2 = this.mediaLink;
      if (var2 != null && call != null) {
         var2.updatePeerCandidates(
            readStringOrEmpty(call, "peerLocalHost"),
            readIntOrZero(call, "peerLocalPort"),
            readStringOrEmpty(call, "peerRelayHost"),
            readIntOrZero(call, "peerRelayPort")
         );
      }
   }

   public boolean isMuted() {
      return this.muted;
   }

   public void setMuted(boolean muted) {
      this.muted = muted;
      CallAudio var2 = this.callAudio;
      if (var2 != null) {
         var2.setMuted(muted);
      }
   }

   public boolean isDeafened() {
      return this.deafened;
   }

   public void setDeafened(boolean deafened) {
      this.deafened = deafened;
      CallAudio var2 = this.callAudio;
      if (var2 != null) {
         var2.setDeafened(deafened);
      }
   }

   public void setVolume(float volume) {
      this.volume = volume;
      CallAudio var2 = this.callAudio;
      if (var2 != null) {
         var2.setOutputVolume(volume);
      }
   }

   public void resetSafety() {
      CallAudio var1 = this.callAudio;
      if (var1 != null) {
         var1.resetSafety();
      }
   }

   public boolean isVideoEnabled() {
      return this.videoEnabled;
   }

   public void setVideoEnabled(boolean videoEnabled) {
      this.videoEnabled = videoEnabled;
      CallVideo var2 = this.callVideo;
      if (var2 != null) {
         var2.setEnabled(videoEnabled);
      }
   }

   public void setTargetFps(int fps) {
      this.targetFps = Math.max(5, Math.min(60, fps));
      CallVideo var2 = this.callVideo;
      if (var2 != null) {
         var2.setTargetFps(this.targetFps);
      }
   }

   public int getTargetFps() {
      return this.targetFps;
   }

   public void setResolutionIndex(int index) {
      this.resolutionIndex = Math.max(0, Math.min(RESOLUTIONS.length - 1, index));
   }

   public String getResolutionLabel() {
      int[] var1 = RESOLUTIONS[this.resolutionIndex];
      return var1[0] + "x" + var1[1];
   }

   public CallVideo.DecodedFrame latestPeerFrame() {
      CallVideo var1 = this.callVideo;
      return var1 == null ? null : var1.latestFrame();
   }

   public int getLocalPort() {
      MediaLink var1 = this.mediaLink;
      return var1 == null ? 0 : var1.getLocalPort();
   }

   public boolean isDirectPath() {
      MediaLink var1 = this.mediaLink;
      return var1 != null && var1.isDirect();
   }

   public boolean isPeerSpeaking() {
      CallAudio var1 = this.callAudio;
      return var1 != null && var1.isPeerSpeaking();
   }

   public boolean isLocalSpeaking() {
      CallAudio var1 = this.callAudio;
      return var1 != null && var1.isLocalSpeaking();
   }

   public int getEncodedFps() {
      CallVideo var1 = this.callVideo;
      return var1 == null ? 0 : var1.getEncodedFps();
   }

   public int getLastFrameBytes() {
      CallVideo var1 = this.callVideo;
      return var1 == null ? 0 : var1.getLastFrameBytes();
   }

   public String getAudioBackendLabel() {
      CallAudio var1 = this.callAudio;
      return var1 == null ? "нет" : var1.backend().label();
   }

   public String getAudioStats() {
      CallAudio var1 = this.callAudio;
      return var1 == null
         ? "звук не поднят"
         : String.format(
            Locale.ROOT,
            "отпр %d, прин %d, спасено %d, потеряно %d, сбоев %d, разрывов %d, микр %.0f, пир %.0f%s",
            var1.getSentPackets(),
            var1.getReceivedPackets(),
            var1.getRecoveredFrames(),
            var1.getLostFrames(),
            var1.getDecodeFailures(),
            var1.getUnderruns(),
            var1.getMicLevel(),
            var1.getPeerLevel(),
            var1.isSafetyMuted() ? " [ЗАГЛУШЕНО ЗАЩИТОЙ: идёт шум]" : ""
         );
   }

   public String getCompatibilityProblem() {
      CallAudio var1 = this.callAudio;
      return var1 == null ? "" : var1.getCompatibilityProblem();
   }

   public boolean hasPeerSignal() {
      MediaLink var1 = this.mediaLink;
      return var1 != null && var1.getLastPacketAt() > 0L && System.currentTimeMillis() - var1.getLastPacketAt() < 3000L;
   }

   private static void closeQuietly(AutoCloseable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (Exception var2) {
         }
      }
   }

   private static String readStringOrEmpty(JsonObject object, String key) {
      if (object != null && object.has(key)) {
         JsonElement var2 = object.get(key);
         return var2 != null && !var2.isJsonNull() ? var2.getAsString() : "";
      } else {
         return "";
      }
   }

   private static int readIntOrZero(JsonObject object, String key) {
      if (object != null && object.has(key)) {
         JsonElement var2 = object.get(key);
         return var2 != null && !var2.isJsonNull() ? var2.getAsInt() : 0;
      } else {
         return 0;
      }
   }
}
