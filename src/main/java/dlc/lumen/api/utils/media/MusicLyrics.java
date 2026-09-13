package dlc.lumen.api.utils.media;

import java.util.List;

/**
 * Offline lyrics facade. Remote lyric lookup was intentionally removed so the
 * client never sends the current track metadata to a third-party service.
 */
public final class MusicLyrics {
   private MusicLyrics() {
   }

   public static MusicLyrics.Result resolve(MusicSession.Snapshot snapshot) {
      MusicLyrics.Status status = snapshot != null && snapshot.isPresent() ? MusicLyrics.Status.EMPTY : MusicLyrics.Status.IDLE;
      return new MusicLyrics.Result(status, MusicLyrics.Track.EMPTY);
   }

   public record Line(long timeMs, String text) {
      public Line(long timeMs, String text) {
         timeMs = Math.max(0L, timeMs);
         text = text == null ? "" : text.trim();
         this.timeMs = timeMs;
         this.text = text;
      }
   }

   public record Result(MusicLyrics.Status status, MusicLyrics.Track track) {
      public Result(MusicLyrics.Status status, MusicLyrics.Track track) {
         status = status == null ? MusicLyrics.Status.IDLE : status;
         track = track == null ? MusicLyrics.Track.EMPTY : track;
         this.status = status;
         this.track = track;
      }
   }

   public enum Status {
      IDLE,
      LOADING,
      READY,
      EMPTY,
      ERROR
   }

   public record Track(List<MusicLyrics.Line> lines, boolean synced) {
      public static final MusicLyrics.Track EMPTY = new MusicLyrics.Track(List.of(), false);

      public Track(List<MusicLyrics.Line> lines, boolean synced) {
         lines = lines == null ? List.of() : List.copyOf(lines);
         this.lines = lines;
         this.synced = synced;
      }

      public boolean isPresent() {
         return !this.lines.isEmpty();
      }

      public int currentIndex(long positionMs) {
         if (this.lines.isEmpty()) {
            return 0;
         }

         int low = 0;
         int high = this.lines.size() - 1;
         while (low <= high) {
            int middle = low + high >>> 1;
            if (this.lines.get(middle).timeMs() <= positionMs) {
               low = middle + 1;
            } else {
               high = middle - 1;
            }
         }

         return Math.max(0, Math.min(this.lines.size() - 1, high));
      }

      public String textAt(int index) {
         return index >= 0 && index < this.lines.size() ? this.lines.get(index).text() : "";
      }
   }
}
