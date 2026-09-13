package dlc.lumen.client.voice.call;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventRender;
import dlc.lumen.api.events.implement.EventTickPost;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.social.GlobalSocialManager;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public enum VoiceCallManager implements QClient {
   INSTANCE;

   private static final long POLL_INTERVAL_MS = 600L;
   private static final long IDLE_TIMEOUT_MS = 4000L;
   private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
   private final AtomicBoolean requestInFlight = new AtomicBoolean(false);
   private final PortalMedia media = new PortalMedia();
   private volatile VoiceCallManager.State state = VoiceCallManager.State.IDLE;
   private volatile String callId = "";
   private volatile String peerNickname = "";
   private volatile boolean incoming;
   private volatile String lastError = "";
   private volatile long lastPollAt;
   private volatile long lastSeq;
   private volatile long stateChangedAt = System.currentTimeMillis();
   private volatile long callStartedAt;

   public VoiceCallManager.State getState() {
      return this.state;
   }

   public boolean isBusy() {
      return this.state == VoiceCallManager.State.OUTGOING
         || this.state == VoiceCallManager.State.INCOMING
         || this.state == VoiceCallManager.State.ACTIVE;
   }

   public boolean isActive() {
      return this.state == VoiceCallManager.State.ACTIVE;
   }

   public boolean isRinging() {
      return this.state == VoiceCallManager.State.OUTGOING || this.state == VoiceCallManager.State.INCOMING;
   }

   public boolean isIncoming() {
      return this.incoming;
   }

   public String getPeerNickname() {
      return this.peerNickname;
   }

   public String getLastError() {
      return this.lastError;
   }

   public PortalMedia getMedia() {
      return this.media;
   }

   public long getCallSeconds() {
      return this.callStartedAt == 0L ? 0L : (System.currentTimeMillis() - this.callStartedAt) / 1000L;
   }

   public long getStateAgeMillis() {
      return System.currentTimeMillis() - this.stateChangedAt;
   }

   public void invite(String nickname) {
      String var2 = sanitizeNickname(nickname);
      if (var2.isBlank()) {
         ChatUtils.sendMessage("Использование: .voice invite <ник>");
      } else if (this.ensureVoiceReady()) {
         if (this.isBusy()) {
            ChatUtils.sendMessage("Вы уже в звонке. Сначала завершите текущий: .voice hangup");
         } else {
            JsonObject var3 = this.baseRequestBody();
            var3.addProperty("nickname", var2);
            this.postApi("/api/voice/invite", var3, response -> {
               this.applyCallState(extractCall(response));
               ChatUtils.sendMessage("Звоним игроку " + var2 + "... Отменить: .voice hangup");
            }, error -> {
               this.lastError = error;
               ChatUtils.sendMessage("Не удалось позвонить: " + translateError(error));
            });
         }
      }
   }

   public void accept() {
      if (this.state != VoiceCallManager.State.INCOMING) {
         ChatUtils.sendMessage("Входящих звонков нет.");
      } else if (this.ensureVoiceReady()) {
         JsonObject var1 = this.baseRequestBody();
         var1.addProperty("callId", this.callId);
         this.postApi("/api/voice/accept", var1, response -> {
            this.applyCallState(extractCall(response));
            ChatUtils.sendMessage("Звонок принят.");
         }, error -> {
            this.lastError = error;
            ChatUtils.sendMessage("Не удалось принять звонок: " + translateError(error));
            this.resetCall();
         });
      }
   }

   public void reject() {
      if (this.state != VoiceCallManager.State.INCOMING) {
         ChatUtils.sendMessage("Входящих звонков нет.");
      } else {
         JsonObject var1 = this.baseRequestBody();
         var1.addProperty("callId", this.callId);
         String var2 = this.peerNickname;
         this.postApi("/api/voice/reject", var1, response -> {
            ChatUtils.sendMessage("Звонок от " + var2 + " отклонён.");
            this.resetCall();
         }, error -> {
            ChatUtils.sendMessage("Не удалось отклонить звонок: " + translateError(error));
            this.resetCall();
         });
      }
   }

   public void hangup() {
      if (!this.isBusy()) {
         ChatUtils.sendMessage("Активного звонка нет.");
      } else {
         JsonObject var1 = this.baseRequestBody();
         var1.addProperty("callId", this.callId);
         String var2 = this.peerNickname;
         this.postApi(
            "/api/voice/hangup",
            var1,
            response -> ChatUtils.sendMessage("Звонок с " + var2 + " завершён."),
            error -> ChatUtils.sendMessage("Звонок завершён локально: " + translateError(error))
         );
         this.resetCall();
      }
   }

   @EventLink
   public void onRender(EventRender.Default event) {
      if (this.state == VoiceCallManager.State.ACTIVE) {
         this.media.captureFrame();
      }
   }

   @EventLink
   public void onTick(EventTickPost event) {
      if (this.state == VoiceCallManager.State.ENDED && this.getStateAgeMillis() > 4000L) {
         this.resetCall();
      } else if (this.isBusy()) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastPollAt >= 600L) {
            if (this.requestInFlight.compareAndSet(false, true)) {
               this.lastPollAt = var2;
               JsonObject var4 = this.baseRequestBody();
               var4.addProperty("callId", this.callId);
               var4.addProperty("afterSeq", this.lastSeq);
               var4.addProperty("localHost", localAddress());
               var4.addProperty("localPort", this.media.getLocalPort());
               var4.addProperty("videoEnabled", this.media.isVideoEnabled());
               var4.addProperty("micMuted", this.media.isMuted());
               this.postApiWithFinally("/api/voice/poll", var4, response -> {
                  this.handleSignals(response.getAsJsonArray("signals"));
                  this.applyCallState(extractCall(response));
               }, error -> this.lastError = error, () -> this.requestInFlight.set(false));
            }
         }
      }
   }

   public void onSyncPayload(JsonObject callObject) {
      if (callObject != null && !callObject.isJsonNull()) {
         this.applyCallState(callObject);
      } else {
         if (this.state == VoiceCallManager.State.INCOMING || this.state == VoiceCallManager.State.OUTGOING) {
            this.resetCall();
         }
      }
   }

   private void handleSignals(JsonArray signals) {
      if (signals != null) {
         for (JsonElement var3 : signals) {
            if (var3.isJsonObject()) {
               JsonObject var4 = var3.getAsJsonObject();
               long var5 = var4.has("seq") ? var4.get("seq").getAsLong() : 0L;
               if (var5 > this.lastSeq) {
                  this.lastSeq = var5;
               }
            }
         }
      }
   }

   private static JsonObject extractCall(JsonObject response) {
      if (response != null && response.has("call")) {
         JsonElement var1 = response.get("call");
         return var1 != null && var1.isJsonObject() ? var1.getAsJsonObject() : null;
      } else {
         return null;
      }
   }

   private void applyCallState(JsonObject call) {
      if (call != null && !call.isJsonNull()) {
         String var2 = readStringOrEmpty(call, "state");
         String var3 = readStringOrEmpty(call, "callId");
         String var4 = readStringOrEmpty(call, "peerNickname");
         boolean var5 = call.has("incoming") && call.get("incoming").getAsBoolean();
         if (!var4.isBlank()) {
            this.peerNickname = var4;
         }

         if (!var3.isBlank()) {
            this.callId = var3;
         }

         this.incoming = var5;
         switch (var2) {
            case "ringing":
               VoiceCallManager.State var8 = var5 ? VoiceCallManager.State.INCOMING : VoiceCallManager.State.OUTGOING;
               if (this.state != var8) {
                  this.setState(var8);
                  if (var5) {
                     this.notifyIncoming();
                  }
               }
               break;
            case "active":
               if (this.state != VoiceCallManager.State.ACTIVE) {
                  this.setState(VoiceCallManager.State.ACTIVE);
                  this.callStartedAt = System.currentTimeMillis();
                  this.startMedia(call);
               } else {
                  this.media.updatePeer(call);
               }
               break;
            case "ended":
               if (this.isBusy()) {
                  this.notifyEnded(readStringOrEmpty(call, "endReason"));
                  this.media.stop();
                  this.setState(VoiceCallManager.State.ENDED);
               }
         }
      }
   }

   private void startMedia(JsonObject call) {
      String var2 = readStringOrEmpty(call, "relayHost");
      int var3 = call.has("relayPort") ? call.get("relayPort").getAsInt() : 0;
      String var4 = readStringOrEmpty(call, "mediaToken");
      if (var2.isBlank()) {
         var2 = this.apiHost();
      }

      String var5 = this.media.start(var2, var3, var4);
      if (var5.isEmpty()) {
         ChatUtils.sendMessage("Звонок с " + this.peerNickname + " начался. Портал: .voice portal");
      } else {
         this.lastError = var5;
         ChatUtils.sendMessage("Звонок начался, но с проблемой: " + var5);
      }

      this.media.updatePeer(call);
   }

   private void notifyIncoming() {
      ChatUtils.sendMessage("Входящий звонок от " + this.peerNickname + ". Принять: .voice accept | Отклонить: .voice reject");
   }

   private void notifyEnded(String reason) {
      String var2 = switch (reason) {
         case "declined" -> " — звонок отклонён";
         case "timeout" -> " — никто не ответил";
         case "peer timeout" -> " — связь потеряна";
         default -> "";
      };
      ChatUtils.sendMessage("Звонок с " + this.peerNickname + " завершён" + var2 + ".");
   }

   private void setState(VoiceCallManager.State next) {
      this.state = next;
      this.stateChangedAt = System.currentTimeMillis();
   }

   private void resetCall() {
      this.media.stop();
      this.setState(VoiceCallManager.State.IDLE);
      this.callId = "";
      this.peerNickname = "";
      this.incoming = false;
      this.callStartedAt = 0L;
      this.lastSeq = 0L;
   }

   private boolean ensureVoiceReady() {
      if (!GlobalSocialManager.INSTANCE.hasNickname()) {
         ChatUtils.sendMessage("Сначала укажите глобальный ник: .global name \"ник\"");
         return false;
      } else if (GlobalSocialManager.INSTANCE.getApiBaseUrl().isBlank()) {
         ChatUtils.sendMessage("Укажите API: .global api <url>");
         return false;
      } else {
         return true;
      }
   }

   private JsonObject baseRequestBody() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("profileId", GlobalSocialManager.INSTANCE.getProfileId());
      return var1;
   }

   private void postApi(String path, JsonObject body, Consumer<JsonObject> onSuccess, Consumer<String> onError) {
      this.postApiWithFinally(path, body, onSuccess, onError, null);
   }

   private void postApiWithFinally(String path, JsonObject body, Consumer<JsonObject> onSuccess, Consumer<String> onError, Runnable onFinally) {
      String var6 = GlobalSocialManager.INSTANCE.getApiBaseUrl();
      if (var6.isBlank()) {
         if (onError != null) {
            onError.accept("не задан API");
         }

         if (onFinally != null) {
            onFinally.run();
         }
      } else {
         HttpRequest var7 = HttpRequest.newBuilder(URI.create(var6 + path))
            .timeout(Duration.ofSeconds(7L))
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
            .build();
         this.httpClient.sendAsync(var7, BodyHandlers.ofString(StandardCharsets.UTF_8)).whenComplete((response, throwable) -> mc.execute(() -> {
            try {
               if (throwable != null || response == null) {
                  if (onError != null) {
                     onError.accept(throwable != null && throwable.getMessage() != null ? throwable.getMessage() : "нет связи с API");
                  }

                  return;
               }

               JsonObject var5 = JsonParser.parseString((String)response.body()).getAsJsonObject();
               boolean var6x = !var5.has("ok") || var5.get("ok").getAsBoolean();
               if (var6x) {
                  if (onSuccess != null) {
                     onSuccess.accept(var5);
                  }

                  return;
               }

               if (onError != null) {
                  onError.accept(readStringOrEmpty(var5, "error"));
               }
            } catch (Exception var10) {
               if (onError != null) {
                  onError.accept(var10.getMessage() == null ? "ошибка разбора ответа" : var10.getMessage());
               }

               return;
            } finally {
               if (onFinally != null) {
                  onFinally.run();
               }
            }
         }));
      }
   }

   private String apiHost() {
      try {
         return URI.create(GlobalSocialManager.INSTANCE.getApiBaseUrl()).getHost();
      } catch (Exception var2) {
         return "";
      }
   }

   private static String localAddress() {
      try {
         return InetAddress.getLocalHost().getHostAddress();
      } catch (Exception var1) {
         return "";
      }
   }

   private static String sanitizeNickname(String text) {
      return text == null ? "" : text.trim().replace('\n', ' ').replace('\r', ' ');
   }

   private static String readStringOrEmpty(JsonObject object, String key) {
      if (object != null && object.has(key)) {
         JsonElement var2 = object.get(key);
         return var2 != null && !var2.isJsonNull() ? var2.getAsString() : "";
      } else {
         return "";
      }
   }

   private static String translateError(String error) {
      String var1 = error == null ? "" : error.toLowerCase(Locale.ROOT);
      if (var1.contains("player not found")) {
         return "игрок не найден";
      } else if (var1.contains("player is busy")) {
         return "игрок уже в звонке";
      } else if (var1.contains("already in a call") || var1.contains("already have a pending call")) {
         return "вы уже в звонке";
      } else if (var1.contains("cannot call yourself")) {
         return "себе позвонить нельзя";
      } else if (var1.contains("call not found")) {
         return "звонок не найден";
      } else if (var1.contains("set a global nickname")) {
         return "сначала задайте глобальный ник";
      } else {
         return error != null && !error.isBlank() ? error : "неизвестная ошибка";
      }
   }

   public enum State {
      IDLE,
      OUTGOING,
      INCOMING,
      ACTIVE,
      ENDED;
   }
}
