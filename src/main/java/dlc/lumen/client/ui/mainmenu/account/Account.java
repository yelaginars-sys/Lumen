package dlc.lumen.client.ui.mainmenu.account;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Account {
   private final LocalDateTime localDateTime;
   private final String text;
   private boolean flag;

   public Account(LocalDateTime creationDate, String name) {
      this.localDateTime = Objects.requireNonNull(creationDate, "creationDate");
      this.text = Objects.requireNonNull(name, "name").trim();
      if (this.text.isEmpty()) {
         throw new IllegalArgumentException("Account name cannot be empty");
      }
   }

   public LocalDateTime creationDate() {
      return this.localDateTime;
   }

   public String name() {
      return this.text;
   }

   public boolean favorite() {
      return this.flag;
   }

   public void favorite(boolean favorite) {
      this.flag = favorite;
   }

   public void toggleFavorite() {
      this.flag = !this.flag;
   }
}
