package dlc.lumen.client.bots;

public class BotStatus {
   public String name = "";
   public String activity = "";
   public String server = "";
   public int anarchy;
   public float health;
   public int food;
   public float inventory;
   public long profit;
   public int hooks;
   public long uptimeMs;
   public boolean inGame;
   public boolean needsHelp;
   public String helpReason = "";
   public long stamp;
   public transient boolean local;
   public transient boolean self;
   public transient boolean pending;

   public boolean isStale(long now) {
      return now - this.stamp > 6000L;
   }
}
