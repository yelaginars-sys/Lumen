package dlc.lumen.api.utils.media;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MusicSession {
   private static final long timeMs = 500L;
   private static final int count = 3;
   private static final long timeMs2 = 5000000000L;
   private static volatile long volatileValue;
   private static final AtomicBoolean activeFlag = new AtomicBoolean(false);
   private static volatile boolean volatileValue2;
   private static volatile MusicSession.Snapshot volatileValue3 = MusicSession.Snapshot.EMPTY;
   private static final ConcurrentLinkedQueue<MusicSession.Command> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

   private MusicSession() {
   }

   public static MusicSession.Snapshot get() {
      return volatileValue3;
   }

   public static boolean isUnavailable() {
      return volatileValue2;
   }

   public static void ensureStarted() {
      if (!volatileValue2) {
         volatileValue = System.nanoTime();
         if (activeFlag.compareAndSet(false, true)) {
            Thread var0 = new Thread(MusicSession::handleAction, "lumen-music-poll");
            var0.setDaemon(true);
            var0.setPriority(1);
            var0.start();
         }
      }
   }

   public static void playPause() {
      concurrentLinkedQueue.add(MusicSession.Command.PLAY_PAUSE);
   }

   public static void next() {
      concurrentLinkedQueue.add(MusicSession.Command.NEXT);
   }

   public static void previous() {
      concurrentLinkedQueue.add(MusicSession.Command.PREVIOUS);
   }

   private static void handleAction() {
      int var0 = 0;

      while (!volatileValue2) {
         try {
            IMediaSession var1 = getIMediaSession();
            handleSession(var1);
            volatileValue3 = getSession(var1);
            var0 = 0;
         } catch (Throwable var3) {
            concurrentLinkedQueue.clear();
            volatileValue3 = MusicSession.Snapshot.EMPTY;
            if (++var0 >= 3) {
               volatileValue2 = true;
               System.err.println("[MusicSession] Системный плеер недоступен, опрос выключен: " + var3);
               return;
            }
         }

         try {
            Thread.sleep(500L);
         } catch (InterruptedException var2) {
            Thread.currentThread().interrupt();
            activeFlag.set(false);
            return;
         }

         if (System.nanoTime() - volatileValue > 5000000000L) {
            volatileValue3 = MusicSession.Snapshot.EMPTY;
            concurrentLinkedQueue.clear();
            activeFlag.set(false);
            return;
         }
      }
   }

   private static IMediaSession getIMediaSession() {
      List<IMediaSession> var0 = MediaPlayerInfo.Instance.getMediaSessions();
      if (var0 != null && !var0.isEmpty()) {
         IMediaSession var1 = null;

         for (IMediaSession var3 : var0) {
            MediaInfo var4 = var3.getMedia();
            if (var4 != null) {
               if (var4.getPlaying()) {
                  return var3;
               }

               if (var1 == null) {
                  var1 = var3;
               }
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   private static void handleSession(IMediaSession session) {
      MusicSession.Command var1;
      while ((var1 = concurrentLinkedQueue.poll()) != null) {
         if (session != null) {
            switch (var1) {
               case PLAY_PAUSE:
                  session.playPause();
                  break;
               case NEXT:
                  session.next();
                  break;
               case PREVIOUS:
                  session.previous();
            }
         }
      }
   }

   private static MusicSession.Snapshot getSession(IMediaSession session) {
      if (session == null) {
         return MusicSession.Snapshot.EMPTY;
      }

      MediaInfo var1 = session.getMedia();
      if (var1 == null) {
         return MusicSession.Snapshot.EMPTY;
      }

      byte[] var2 = var1.getArtworkPng();
      if (var2 != null && var2.length == 0) {
         var2 = null;
      }

      return new MusicSession.Snapshot(
         resolveValue(var1.getTitle()),
         resolveValue(var1.getArtist()),
         var1.getPlaying(),
         var1.getPosition(),
         var1.getDuration(),
         var2,
         var2 == null ? 0 : Arrays.hashCode(var2)
      );
   }

   private static String resolveValue(String value) {
      return value == null ? "" : value;
   }

   private enum Command {
      PLAY_PAUSE,
      NEXT,
      PREVIOUS;
   }

   public record Snapshot(String title, String artist, boolean playing, long position, long duration, byte[] artwork, int artworkId) {
      public static final MusicSession.Snapshot EMPTY = new MusicSession.Snapshot("", "", false, 0L, 0L, null, 0);

      public Snapshot(String title, String artist, boolean playing, long position, long duration, byte[] artwork, int artworkId) {
         this.title = title;
         this.artist = artist;
         this.playing = playing;
         this.position = position;
         this.duration = duration;
         this.artwork = artwork;
         this.artworkId = artworkId;
      }

      public boolean isPresent() {
         return !this.title.isBlank() || !this.artist.isBlank();
      }

      public float progress() {
         if (this.duration <= 0L) {
            return 0.0F;
         }

         float var1 = (float)this.position / (float)this.duration;
         return var1 < 0.0F ? 0.0F : Math.min(var1, 1.0F);
      }

      public String title() {
         return this.title;
      }

      public String artist() {
         return this.artist;
      }

      public boolean playing() {
         return this.playing;
      }

      public long position() {
         return this.position;
      }

      public long duration() {
         return this.duration;
      }

      public byte[] artwork() {
         return this.artwork;
      }

      public int artworkId() {
         return this.artworkId;
      }
   }
}
