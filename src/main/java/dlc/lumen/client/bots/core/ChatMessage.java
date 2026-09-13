package dlc.lumen.client.bots.core;

public record ChatMessage(String time, String text) {

   public ChatMessage(String time, String text) {
      this.time = time;
      this.text = text;
   }

   public String time() {
      return this.time;
   }

   public String text() {
      return this.text;
   }
}
