package dlc.lumen.api.utils.rpc;

import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.rpc.utils.DiscordEventHandlers;
import dlc.lumen.api.utils.rpc.utils.DiscordRPC;
import dlc.lumen.api.utils.rpc.utils.DiscordRichPresence;
import lombok.Generated;

public class DiscordManager implements QClient {
   private DiscordManager.DiscordDaemonThread discordDaemonThread;
   private long application_id;
   private boolean running;
   private String image;
   private String site;
   private String telegram;
   private String discord;
   String state = "";
   public static DiscordRichPresence discordRichPresence = new DiscordRichPresence();
   public static DiscordRPC discordRPC = DiscordRPC.INSTANCE;

   private void handleAction() {
      this.discordDaemonThread = new DiscordManager.DiscordDaemonThread();
      this.application_id = 1517196165815472198L;
      this.running = true;
      this.site = "https://lumendlc.net";
      this.telegram = "https://t.me/lumenDLC";
      this.discord = "https://discord.gg/nwDR2fn3VV";
   }

   public void init() {
      this.handleAction();
      DiscordEventHandlers var1 = new DiscordEventHandlers.Builder().build();
      DiscordRPC.INSTANCE.Discord_Initialize(String.valueOf(this.application_id), var1, true, "");
      discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
      discordRPC.Discord_UpdatePresence(discordRichPresence);
      new Thread(() -> {
         while (this.running) {
            try {
               String var1x = mc.player != null ? mc.player.getName().getString() : (mc.getSession() != null ? mc.getSession().getUsername() : "Player");
               discordRichPresence.details = "Name » " + var1x;
               discordRichPresence.state = "UID » 1";
               discordRichPresence.largeImageKey = this.image;
               discordRichPresence.button_label_1 = "Telegram";
               discordRichPresence.button_url_1 = this.telegram;
               discordRichPresence.button_label_2 = "Discord";
               discordRichPresence.button_url_2 = this.discord;
               DiscordRPC.INSTANCE.Discord_UpdatePresence(discordRichPresence);
               Thread.sleep(2000L);
            } catch (InterruptedException var4) {
               Thread.currentThread().interrupt();
               return;
            } catch (Throwable var5) {
               try {
                  Thread.sleep(2000L);
               } catch (InterruptedException var3) {
                  Thread.currentThread().interrupt();
                  return;
               }
            }
         }
      }, "Discord-RPC-Updater").start();
      this.discordDaemonThread.start();
   }

   public DiscordManager start() {
      this.init();
      return this;
   }

   public void stopRPC() {
      this.running = false;
      DiscordRPC.INSTANCE.Discord_Shutdown();
      if (this.discordDaemonThread != null) {
         this.discordDaemonThread.interrupt();
      }
   }

   @Generated
   public DiscordManager.DiscordDaemonThread getDiscordDaemonThread() {
      return this.discordDaemonThread;
   }

   @Generated
   public long getAPPLICATION_ID() {
      return this.application_id;
   }

   @Generated
   public boolean isRunning() {
      return this.running;
   }

   @Generated
   public String getImage() {
      return this.image;
   }

   @Generated
   public String getSite() {
      return this.site;
   }

   @Generated
   public String getTelegram() {
      return this.telegram;
   }

   @Generated
   public String getDiscord() {
      return this.discord;
   }

   @Generated
   public String getState() {
      return this.state;
   }

   private class DiscordDaemonThread extends Thread {
      @Override
      public void run() {
         this.setName("Discord-RPC");

         try {
            while (DiscordManager.this.running) {
               DiscordRPC.INSTANCE.Discord_RunCallbacks();
               Thread.sleep(15000L);
            }
         } catch (Exception var2) {
            DiscordManager.this.stopRPC();
         }

         super.run();
      }
   }
}
