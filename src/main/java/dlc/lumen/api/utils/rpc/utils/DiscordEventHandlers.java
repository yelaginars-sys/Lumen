package dlc.lumen.api.utils.rpc.utils;

import com.sun.jna.Structure;
import dlc.lumen.api.utils.rpc.callbacks.DisconnectedCallback;
import dlc.lumen.api.utils.rpc.callbacks.ErroredCallback;
import dlc.lumen.api.utils.rpc.callbacks.JoinGameCallback;
import dlc.lumen.api.utils.rpc.callbacks.JoinRequestCallback;
import dlc.lumen.api.utils.rpc.callbacks.ReadyCallback;
import dlc.lumen.api.utils.rpc.callbacks.SpectateGameCallback;
import java.util.Arrays;
import java.util.List;

public class DiscordEventHandlers extends Structure {
   public DisconnectedCallback disconnected;
   public JoinRequestCallback joinRequest;
   public SpectateGameCallback spectateGame;
   public ReadyCallback ready;
   public ErroredCallback errored;
   public JoinGameCallback joinGame;

   protected List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }

   public static class Builder {
      private final DiscordEventHandlers discordEventHandlers = new DiscordEventHandlers();

      public DiscordEventHandlers build() {
         return this.discordEventHandlers;
      }

      public DiscordEventHandlers.Builder disconnected(DisconnectedCallback var1) {
         this.discordEventHandlers.disconnected = var1;
         return this;
      }

      public DiscordEventHandlers.Builder errored(ErroredCallback var1) {
         this.discordEventHandlers.errored = var1;
         return this;
      }

      public DiscordEventHandlers.Builder ready(ReadyCallback var1) {
         this.discordEventHandlers.ready = var1;
         return this;
      }

      public DiscordEventHandlers.Builder joinRequest(JoinRequestCallback var1) {
         this.discordEventHandlers.joinRequest = var1;
         return this;
      }

      public DiscordEventHandlers.Builder joinGame(JoinGameCallback var1) {
         this.discordEventHandlers.joinGame = var1;
         return this;
      }

      public DiscordEventHandlers.Builder spectateGame(SpectateGameCallback var1) {
         this.discordEventHandlers.spectateGame = var1;
         return this;
      }
   }
}
