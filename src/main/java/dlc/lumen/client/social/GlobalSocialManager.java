package dlc.lumen.client.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventTickPost;
import dlc.lumen.api.utils.chat.ChatUtils;
import dlc.lumen.client.render.models.CosmeticPet;
import dlc.lumen.client.render.models.CosmeticSword;
import dlc.lumen.client.render.models.CustomModelManager;
import dlc.lumen.client.render.models.CustomModelType;
import dlc.lumen.client.voice.call.VoiceCallManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public enum GlobalSocialManager implements QClient {
   INSTANCE;

   private static final long TIMESTAMP = 1200L;
   private static final long TIMESTAMP2 = 300000L;
   private static final int INDEX = 1;
   private static final int INDEX2 = 800;
   private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
   private final Set<String> set = ConcurrentHashMap.newKeySet();
   private final List<GlobalSocialManager.LocalPoint> globalSocialManagers = Collections.synchronizedList(new ArrayList<>());
   private final Map<UUID, GlobalSocialManager.PartyMemberSnapshot> uUIDs = new ConcurrentHashMap<>();
   private final Set<String> set2 = ConcurrentHashMap.newKeySet();
   private volatile GlobalSocialStorage nickname2;
   private volatile long timestamp;
   private volatile long timestamp2;
   private volatile long timestamp3;
   private volatile boolean flag = true;
   private volatile String inParty;
   private volatile List<String> knownNicknames2 = List.of();
   private volatile Map<String, GlobalSocialManager.PartyInvite> pendingInviteSenders = Map.of();

   public void initialize(GlobalSocialStorage storage) {
      this.nickname2 = storage;
      this.flag = true;
   }

   public boolean hasNickname() {
      return this.nickname2 != null && this.nickname2.hasNickname();
   }

   public String getNickname() {
      return this.nickname2 == null ? "" : this.nickname2.getNickname();
   }

   public String getDisplayName() {
      String var1 = this.getNickname();
      if (!var1.isBlank()) {
         return var1;
      } else {
         return mc != null && mc.getSession() != null && mc.getSession().getUsername() != null && !mc.getSession().getUsername().isBlank()
            ? mc.getSession().getUsername()
            : "Player";
      }
   }

   public String getProfileId() {
      return this.nickname2 == null ? "" : this.nickname2.getProfileId();
   }

   public String getApiBaseUrl() {
      return this.nickname2 == null ? "" : this.nickname2.getApiBaseUrl();
   }

   public void setApiBaseUrl(String apiBaseUrl) {
      if (this.nickname2 != null) {
         this.nickname2.setApiBaseUrl(apiBaseUrl);
         this.forceNextSync();
         ChatUtils.sendMessage("Global API: " + this.nickname2.getApiBaseUrl());
      }
   }

   public boolean isInParty() {
      return this.inParty != null && !this.inParty.isBlank();
   }

   public int getPartySize() {
      return this.getVisiblePartyMembers().size() + (this.isInParty() ? 1 : 0);
   }

   public List<String> getKnownNicknames() {
      return this.knownNicknames2;
   }

   public List<String> getPendingInviteSenders() {
      return new ArrayList<>(this.pendingInviteSenders.keySet());
   }

   public Map<String, GlobalSocialManager.PartyInvite> getPendingInvites() {
      return this.pendingInviteSenders;
   }

   public void forceNextSync() {
      this.flag = true;
   }

   public void requestNickname(String nickname) {
      if (this.nickname2 != null) {
         String var2 = helper19(nickname);
         if (var2.isBlank()) {
            ChatUtils.sendMessage("Использование: .global name \"ник\"");
         } else if (var2.length() >= 2 && var2.length() <= 24) {
            JsonObject var3 = this.helper10();
            var3.addProperty("nickname", var2);
            this.helper11("/api/profile/name", var3, response -> {
               String var3x = helper21(response, "nickname");
               this.nickname2.setNickname(var3x.isBlank() ? var2 : var3x);
               ChatUtils.sendMessage("Глобальный ник установлен: " + this.nickname2.getNickname());
               this.forceNextSync();
            }, message -> ChatUtils.sendMessage("Не удалось установить ник: " + message));
         } else {
            ChatUtils.sendMessage("Глобальный ник должен быть от 2 до 24 символов.");
         }
      }
   }

   public void requestNickname(String nickname, Consumer<String> onSuccess, Consumer<String> onError) {
      if (this.nickname2 == null) {
         if (onError != null) {
            onError.accept("Хранилище профиля ещё не готово.");
         }
      } else {
         String var4 = helper19(nickname);
         if (var4.isBlank()) {
            if (onError != null) {
               onError.accept("Введите имя пользователя.");
            }
         } else if (var4.length() >= 2 && var4.length() <= 24) {
            JsonObject var5 = this.helper10();
            var5.addProperty("nickname", var4);
            this.helper11("/api/profile/name", var5, response -> {
               String var4x = helper21(response, "nickname");
               this.nickname2.setNickname(var4x.isBlank() ? var4 : var4x);
               this.forceNextSync();
               if (onSuccess != null) {
                  onSuccess.accept(this.nickname2.getNickname());
               }
            }, message -> {
               if (onError != null) {
                  onError.accept(helper20(message));
               }
            });
         } else {
            if (onError != null) {
               onError.accept("Имя должно быть от 2 до 24 символов.");
            }
         }
      }
   }

   public void sendGlobalChat(String message) {
      if (this.helper9("Сначала укажите глобальный ник через .global name \"ник\".")) {
         String var2 = helper19(message);
         if (var2.isBlank()) {
            ChatUtils.sendMessage("Использование: .irc <сообщение>");
         } else {
            JsonObject var3 = this.helper10();
            var3.addProperty("message", var2);
            this.helper11("/api/chat/send", var3, response -> {
               ChatUtils.sendMessage("[IRC] " + this.getNickname() + ": " + var2, 800);
               this.forceNextSync();
            }, messageText -> ChatUtils.sendMessage("Не удалось отправить сообщение: " + messageText));
         }
      }
   }

   public void invite(String nickname) {
      if (this.helper9("Сначала укажите глобальный ник через .global name \"ник\".")) {
         String var2 = helper19(nickname);
         if (var2.isBlank()) {
            ChatUtils.sendMessage("Использование: .global invite <ник>");
         } else {
            JsonObject var3 = this.helper10();
            var3.addProperty("nickname", var2);
            this.helper11("/api/party/invite", var3, response -> {
               ChatUtils.sendMessage("Приглашение отправлено игроку " + var2 + ".");
               this.forceNextSync();
            }, message -> ChatUtils.sendMessage("Не удалось отправить инвайт: " + message));
         }
      }
   }

   public void acceptInvite(String nickname) {
      if (this.helper9("Сначала укажите глобальный ник через .global name \"ник\".")) {
         String var2 = helper19(nickname);
         if (var2.isBlank()) {
            ChatUtils.sendMessage("Использование: .global accept <ник>");
         } else {
            JsonObject var3 = this.helper10();
            var3.addProperty("nickname", var2);
            this.helper11("/api/party/accept", var3, response -> {
               ChatUtils.sendMessage("Вы приняли приглашение от " + var2 + ".");
               this.forceNextSync();
            }, message -> ChatUtils.sendMessage("Не удалось принять приглашение: " + message));
         }
      }
   }

   public void denyInvite(String nickname) {
      if (this.helper9("Сначала укажите глобальный ник через .global name \"ник\".")) {
         String var2 = helper19(nickname);
         if (var2.isBlank()) {
            ChatUtils.sendMessage("Использование: .global deny <ник>");
         } else {
            JsonObject var3 = this.helper10();
            var3.addProperty("nickname", var2);
            this.helper11("/api/party/deny", var3, response -> {
               ChatUtils.sendMessage("Приглашение от " + var2 + " отклонено.");
               this.forceNextSync();
            }, message -> ChatUtils.sendMessage("Не удалось отклонить приглашение: " + message));
         }
      }
   }

   public void leaveParty() {
      if (this.helper9("Сначала укажите глобальный ник через .global name \"ник\".")) {
         JsonObject var1 = this.helper10();
         this.helper11("/api/party/leave", var1, response -> {
            ChatUtils.sendMessage("Вы вышли из пати.");
            this.forceNextSync();
         }, message -> ChatUtils.sendMessage("Не удалось выйти из пати: " + message));
      }
   }

   public boolean createPointAtPlayer() {
      if (mc.player != null && mc.world != null && this.helper9("Сначала укажите глобальный ник через .global name \"ник\".")) {
         synchronized (this.globalSocialManagers) {
            this.globalSocialManagers.clear();
         }

         GlobalSocialManager.LocalPoint var6 = new GlobalSocialManager.LocalPoint(
            UUID.randomUUID().toString().substring(0, 8), mc.player.getX(), mc.player.getY(), mc.player.getZ(), System.currentTimeMillis()
         );
         synchronized (this.globalSocialManagers) {
            this.globalSocialManagers.add(var6);
         }

         this.forceNextSync();
         return true;
      } else {
         return false;
      }
   }

   public List<GlobalSocialManager.PartyPoint> getVisiblePoints() {
      ArrayList var1 = new ArrayList();
      long var2 = System.currentTimeMillis();
      String var4 = this.getNickname();
      synchronized (this.globalSocialManagers) {
         if (!this.globalSocialManagers.isEmpty()) {
            GlobalSocialManager.LocalPoint var6 = this.globalSocialManagers.get(this.globalSocialManagers.size() - 1);
            if (var2 - var6.createdAt() <= 300000L) {
               var1.add(new GlobalSocialManager.PartyPoint(var6.id(), this.getProfileId(), var4, var6.x(), var6.y(), var6.z(), var6.createdAt(), true));
            }
         }
      }

      LinkedHashMap var11 = new LinkedHashMap();

      for (GlobalSocialManager.PartyMemberSnapshot var7 : this.uUIDs.values()) {
         if (var7 != null && var2 - var7.updatedAt() <= 60000L) {
            List var8 = var7.points();
            if (!var8.isEmpty()) {
               GlobalSocialManager.PartyPoint var9 = (GlobalSocialManager.PartyPoint)var8.get(var8.size() - 1);
               if (var2 - var9.createdAt() <= 300000L) {
                  var11.put(var7.nickname(), var9);
               }
            }
         }
      }

      var1.addAll(var11.values());
      var1.sort(Comparator.comparingLong(GlobalSocialManager.PartyPoint::createdAt).reversed());
      return var1;
   }

   public List<GlobalSocialManager.PartyMemberSnapshot> getAllPartyMembers() {
      if (mc.player != null && mc.world != null) {
         long var1 = System.currentTimeMillis();
         ArrayList var3 = new ArrayList();

         for (GlobalSocialManager.PartyMemberSnapshot var5 : this.uUIDs.values()) {
            if (var5 != null && var1 - var5.updatedAt() <= 30000L) {
               var3.add(var5);
            }
         }

         var3.sort(Comparator.comparing(GlobalSocialManager.PartyMemberSnapshot::nickname, String.CASE_INSENSITIVE_ORDER));
         return var3;
      } else {
         return List.of();
      }
   }

   public boolean isSameWorldAs(GlobalSocialManager.PartyMemberSnapshot member) {
      return member != null && mc.player != null
         ? Objects.equals(this.helper16(), member.serverId()) && Objects.equals(this.helper17(), member.dimension())
         : false;
   }

   public List<GlobalSocialManager.PartyMemberSnapshot> getVisiblePartyMembers() {
      if (mc.player != null && mc.world != null) {
         String var1 = this.helper16();
         String var2 = this.helper17();
         long var3 = System.currentTimeMillis();
         ArrayList var5 = new ArrayList();

         for (GlobalSocialManager.PartyMemberSnapshot var7 : this.uUIDs.values()) {
            if (var7 != null && var3 - var7.updatedAt() <= 15000L && Objects.equals(var1, var7.serverId()) && Objects.equals(var2, var7.dimension())) {
               var5.add(var7);
            }
         }

         var5.sort(Comparator.comparing(GlobalSocialManager.PartyMemberSnapshot::nickname, String.CASE_INSENSITIVE_ORDER));
         return var5;
      } else {
         return List.of();
      }
   }

   public GlobalSocialManager.PartyMemberSnapshot getPartyMember(UUID playerUuid) {
      return playerUuid == null ? null : this.uUIDs.get(playerUuid);
   }

   public String getPartyFiguraModelId(UUID playerUuid) {
      GlobalSocialManager.PartyMemberSnapshot var2 = this.getPartyMember(playerUuid);
      return var2 == null ? null : helper23(var2.figuraId());
   }

   public CosmeticSword getPartySword(UUID playerUuid) {
      GlobalSocialManager.PartyMemberSnapshot var2 = this.getPartyMember(playerUuid);
      return var2 == null ? CosmeticSword.NONE : CosmeticSword.fromId(var2.swordId());
   }

   public CosmeticPet getPartyPet(UUID playerUuid) {
      GlobalSocialManager.PartyMemberSnapshot var2 = this.getPartyMember(playerUuid);
      return var2 == null ? CosmeticPet.NONE : CosmeticPet.fromId(var2.petId());
   }

   public Vec3d getRenderPosition(GlobalSocialManager.PartyMemberSnapshot member) {
      if (member != null && mc.world != null) {
         if (member.playerUuid() != null) {
            PlayerEntity var2 = this.helper15(member.playerUuid());
            if (var2 != null) {
               return var2.getPos();
            }
         }

         return new Vec3d(member.x(), member.y(), member.z());
      } else {
         return null;
      }
   }

   @EventLink
   public void onTick(EventTickPost event) {
      this.helper8();
   }

   private void helper(JsonObject response) {
      this.inParty = helper23(helper21(response, "partyId"));
      long var2 = response.has("cursor") ? response.get("cursor").getAsLong() : this.timestamp3;
      if (var2 > this.timestamp3) {
         this.timestamp3 = var2;
      }

      VoiceCallManager.INSTANCE.onSyncPayload(response.has("call") && response.get("call").isJsonObject() ? response.getAsJsonObject("call") : null);
      this.knownNicknames2 = Collections.unmodifiableList(this.helper12(response.getAsJsonArray("knownNicknames")));
      this.pendingInviteSenders = Collections.unmodifiableMap(this.helper13(response.getAsJsonArray("pendingInvites")));
      this.helper6();
      this.helper5(response.getAsJsonArray("chat"));
      this.helper2(response.getAsJsonArray("partyMembers"));
   }

   private void helper2(JsonArray membersArray) {
      LinkedHashMap var2 = new LinkedHashMap();
      LinkedHashMap var3 = new LinkedHashMap();
      if (membersArray != null) {
         for (JsonElement var5 : membersArray) {
            if (var5.isJsonObject()) {
               JsonObject var6 = var5.getAsJsonObject();
               String var7 = helper21(var6, "profileId");
               if (!var7.equalsIgnoreCase(this.getProfileId())) {
                  UUID var8 = helper18(helper21(var6, "playerUuid"));
                  if (var8 != null) {
                     List var9 = this.helper14(var6.getAsJsonArray("points"), var7, helper21(var6, "nickname"));
                     String var10 = helper21(var6, "nickname");
                     GlobalSocialManager.PartyMemberSnapshot var11 = new GlobalSocialManager.PartyMemberSnapshot(
                        var7,
                        var8,
                        var10,
                        helper21(var6, "mcName"),
                        helper21(var6, "serverId"),
                        helper21(var6, "dimension"),
                        helper22(var6, "x"),
                        helper22(var6, "y"),
                        helper22(var6, "z"),
                        (float)helper22(var6, "yaw"),
                        (float)helper22(var6, "pitch"),
                        helper21(var6, "customModelId"),
                        helper21(var6, "swordId"),
                        helper21(var6, "petId"),
                        helper21(var6, "figuraId"),
                        var9,
                        var6.has("updatedAt") ? var6.get("updatedAt").getAsLong() : System.currentTimeMillis()
                     );
                     var2.put(var8, var11);
                     CustomModelType var12 = CustomModelType.fromId(var11.customModelId());
                     if (var12.isCustom()) {
                        var3.put(var8, var12);
                     }
                  }
               }
            }
         }
      }

      this.uUIDs.clear();
      this.uUIDs.putAll(var2);
      CustomModelManager.setRemoteSelections(var3);
      this.helper3(var2.values());
   }

   private void helper3(Iterable<GlobalSocialManager.PartyMemberSnapshot> members) {
      if (Lumen.INSTANCE.friendStorage != null) {
         LinkedHashSet<String> var2 = new LinkedHashSet<>();

         for (GlobalSocialManager.PartyMemberSnapshot var4 : members) {
            if (var4 != null) {
               helper4(var2, var4.nickname());
               helper4(var2, var4.mcName());
            }
         }

         for (String var7 : var2) {
            if (!Lumen.INSTANCE.friendStorage.isFriend(var7)) {
               Lumen.INSTANCE.friendStorage.add(var7);
            }

            this.set2.add(var7);
         }

         for (String var8 : new ArrayList<>(this.set2)) {
            if (!var2.contains(var8)) {
               Lumen.INSTANCE.friendStorage.remove(var8);
               this.set2.remove(var8);
            }
         }
      }
   }

   private static void helper4(Set<String> target, String value) {
      if (value != null) {
         String var2 = value.trim();
         if (!var2.isEmpty()) {
            target.add(var2);
         }
      }
   }

   private void helper5(JsonArray messages) {
      if (messages != null) {
         for (JsonElement var3 : messages) {
            if (var3.isJsonObject()) {
               JsonObject var4 = var3.getAsJsonObject();
               long var5 = var4.has("id") ? var4.get("id").getAsLong() : 0L;
               if (var5 > this.timestamp3) {
                  this.timestamp3 = var5;
               }

               String var7 = helper21(var4, "profileId");
               if (!var7.equalsIgnoreCase(this.getProfileId())) {
                  String var8 = helper21(var4, "nickname");
                  String var9 = helper21(var4, "message");
                  if (!var8.isBlank() && !var9.isBlank()) {
                     ChatUtils.sendMessage("[IRC] " + var8 + ": " + var9, 800);
                  }
               }
            }
         }
      }
   }

   private void helper6() {
      LinkedHashSet<String> var1 = new LinkedHashSet<>(this.pendingInviteSenders.keySet());
      this.set.retainAll(var1);

      for (String var3 : var1) {
         if (this.set.add(var3)) {
            ChatUtils.sendMessage("Новый инвайт в пати от " + var3 + ". Принять: .global accept \"" + var3 + "\"");
         }
      }
   }

   private void updateState(String message) {
      CustomModelManager.setRemoteSelections(Map.of());
      this.uUIDs.clear();
      this.inParty = null;
      long var2 = System.currentTimeMillis();
      if (var2 - this.timestamp2 >= 12000L) {
         this.timestamp2 = var2;
         ChatUtils.sendMessage("Global API недоступен: " + message);
      }
   }

   private JsonArray helper7() {
      JsonArray var1 = new JsonArray();
      long var2 = System.currentTimeMillis();
      synchronized (this.globalSocialManagers) {
         for (GlobalSocialManager.LocalPoint var6 : this.globalSocialManagers) {
            if (var2 - var6.createdAt() <= 300000L) {
               JsonObject var7 = new JsonObject();
               var7.addProperty("id", var6.id());
               var7.addProperty("x", var6.x());
               var7.addProperty("y", var6.y());
               var7.addProperty("z", var6.z());
               var7.addProperty("createdAt", var6.createdAt());
               var1.add(var7);
            }
         }

         return var1;
      }
   }

   private void helper8() {
      long var1 = System.currentTimeMillis();
      synchronized (this.globalSocialManagers) {
         this.globalSocialManagers.removeIf(point -> var1 - point.createdAt() > 300000L);
      }
   }

   private boolean readStringOrEmpty() {
      return this.nickname2 != null && this.hasNickname() && mc.player != null && mc.world != null && !this.getApiBaseUrl().isBlank();
   }

   private boolean helper9(String messageIfNoNickname) {
      if (this.nickname2 == null) {
         ChatUtils.sendMessage("Global storage ещё не инициализирован.");
         return false;
      } else if (!this.hasNickname()) {
         ChatUtils.sendMessage(messageIfNoNickname);
         return false;
      } else if (this.getApiBaseUrl().isBlank()) {
         ChatUtils.sendMessage("Укажите API через .global api <url>.");
         return false;
      } else {
         return true;
      }
   }

   private JsonObject helper10() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("profileId", this.getProfileId());
      return var1;
   }

   private void helper11(String path, JsonObject body, Consumer<JsonObject> onSuccess, Consumer<String> onError) {
      this.readStringOrEmpty2(path, body, onSuccess, onError, null);
   }

   private void readStringOrEmpty2(String path, JsonObject body, Consumer<JsonObject> onSuccess, Consumer<String> onError, Runnable onFinally) {
      String var6 = this.getApiBaseUrl();
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
               if (throwable != null) {
                  if (onError != null) {
                     onError.accept(throwable.getMessage() == null ? "network error" : throwable.getMessage());
                  }

                  return;
               }

               if (response == null) {
                  if (onError != null) {
                     onError.accept("empty response");
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
                  onError.accept(helper21(var5, "error"));
               }
            } catch (Exception var10) {
               if (onError != null) {
                  onError.accept(var10.getMessage() == null ? "parse error" : var10.getMessage());
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

   private List<String> helper12(JsonArray array) {
      if (array == null) {
         return List.of();
      }

      ArrayList var2 = new ArrayList();

      for (JsonElement var4 : array) {
         String var5 = var4.getAsString().trim();
         if (!var5.isBlank()) {
            var2.add(var5);
         }
      }

      var2.sort(String.CASE_INSENSITIVE_ORDER);
      return var2;
   }

   private Map<String, GlobalSocialManager.PartyInvite> helper13(JsonArray array) {
      LinkedHashMap var2 = new LinkedHashMap();
      if (array == null) {
         return var2;
      }

      for (JsonElement var4 : array) {
         if (var4.isJsonObject()) {
            JsonObject var5 = var4.getAsJsonObject();
            String var6 = helper21(var5, "nickname");
            if (!var6.isBlank()) {
               var2.put(var6, new GlobalSocialManager.PartyInvite(var6, var5.has("createdAt") ? var5.get("createdAt").getAsLong() : 0L));
            }
         }
      }

      return var2;
   }

   private List<GlobalSocialManager.PartyPoint> helper14(JsonArray array, String ownerProfileId, String ownerNickname) {
      ArrayList var4 = new ArrayList();
      if (array == null) {
         return var4;
      }

      long var5 = System.currentTimeMillis();

      for (JsonElement var8 : array) {
         if (var8.isJsonObject()) {
            JsonObject var9 = var8.getAsJsonObject();
            long var10 = var9.has("createdAt") ? var9.get("createdAt").getAsLong() : var5;
            if (var5 - var10 <= 300000L) {
               var4.add(
                  new GlobalSocialManager.PartyPoint(
                     helper21(var9, "id"),
                     ownerProfileId,
                     ownerNickname,
                     helper22(var9, "x"),
                     helper22(var9, "y"),
                     helper22(var9, "z"),
                     var10,
                     false
                  )
               );
            }
         }
      }

      return var4;
   }

   private PlayerEntity helper15(UUID uuid) {
      if (uuid != null && mc.world != null) {
         for (PlayerEntity var3 : mc.world.getPlayers()) {
            if (uuid.equals(var3.getUuid())) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private String helper16() {
      if (mc == null) {
         return "unknown";
      } else {
         ServerInfo var1 = mc.getCurrentServerEntry();
         if (var1 != null && var1.address != null && !var1.address.isBlank()) {
            return var1.address.toLowerCase(Locale.ROOT);
         } else {
            return mc.isInSingleplayer() ? "singleplayer" : "unknown";
         }
      }
   }

   private String helper17() {
      return mc.world == null ? "unknown" : mc.world.getRegistryKey().getValue().toString();
   }

   private static UUID helper18(String value) {
      try {
         return value != null && !value.isBlank() ? UUID.fromString(value) : null;
      } catch (Exception var2) {
         return null;
      }
   }

   private static String helper19(String text) {
      return text == null ? "" : text.trim().replace('\n', ' ').replace('\r', ' ');
   }

   private static String helper20(String message) {
      String var1 = helper19(message);
      String var2 = var1.toLowerCase(Locale.ROOT);
      if (var2.contains("nickname is already taken")) {
         return "Это имя уже занято.";
      } else if (var2.contains("profileid is required")) {
         return "Профиль клиента не найден.";
      } else if (!var2.contains("network") && !var2.contains("connect") && !var2.contains("connection")) {
         return var1.isBlank() ? "Неизвестная ошибка." : var1;
      } else {
         return "Сервер глобального профиля недоступен.";
      }
   }

   private static String helper21(JsonObject object, String key) {
      if (object != null && key != null && object.has(key)) {
         JsonElement var2 = object.get(key);
         return var2 != null && !var2.isJsonNull() ? var2.getAsString() : "";
      } else {
         return "";
      }
   }

   private static double helper22(JsonObject object, String key) {
      if (object != null && key != null && object.has(key)) {
         JsonElement var2 = object.get(key);
         return var2 != null && !var2.isJsonNull() ? var2.getAsDouble() : 0.0;
      } else {
         return 0.0;
      }
   }

   private static String helper23(String value) {
      return value != null && !value.isBlank() ? value : null;
   }

   private static String resolveString(String value) {
      return value == null ? "" : value;
   }

   private record LocalPoint(String id, double x, double y, double z, long createdAt) {

      private LocalPoint(String id, double x, double y, double z, long createdAt) {
         this.id = id;
         this.x = x;
         this.y = y;
         this.z = z;
         this.createdAt = createdAt;
      }

      public String id() {
         return this.id;
      }

      public double x() {
         return this.x;
      }

      public double y() {
         return this.y;
      }

      public double z() {
         return this.z;
      }

      public long createdAt() {
         return this.createdAt;
      }
   }

   public record PartyInvite(String nickname, long createdAt) {

      public PartyInvite(String nickname, long createdAt) {
         this.nickname = nickname;
         this.createdAt = createdAt;
      }

      public String nickname() {
         return this.nickname;
      }

      public long createdAt() {
         return this.createdAt;
      }
   }

   public record PartyMemberSnapshot(
      String profileId,
      UUID playerUuid,
      String nickname,
      String mcName,
      String serverId,
      String dimension,
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      String customModelId,
      String swordId,
      String petId,
      String figuraId,
      List<GlobalSocialManager.PartyPoint> points,
      long updatedAt
   ) {

      public PartyMemberSnapshot(
         String profileId,
         UUID playerUuid,
         String nickname,
         String mcName,
         String serverId,
         String dimension,
         double x,
         double y,
         double z,
         float yaw,
         float pitch,
         String customModelId,
         String swordId,
         String petId,
         String figuraId,
         List<GlobalSocialManager.PartyPoint> points,
         long updatedAt
      ) {
         this.profileId = profileId;
         this.playerUuid = playerUuid;
         this.nickname = nickname;
         this.mcName = mcName;
         this.serverId = serverId;
         this.dimension = dimension;
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
         this.pitch = pitch;
         this.customModelId = customModelId;
         this.swordId = swordId;
         this.petId = petId;
         this.figuraId = figuraId;
         this.points = points;
         this.updatedAt = updatedAt;
      }

      public String profileId() {
         return this.profileId;
      }

      public UUID playerUuid() {
         return this.playerUuid;
      }

      public String nickname() {
         return this.nickname;
      }

      public String mcName() {
         return this.mcName;
      }

      public String serverId() {
         return this.serverId;
      }

      public String dimension() {
         return this.dimension;
      }

      public double x() {
         return this.x;
      }

      public double y() {
         return this.y;
      }

      public double z() {
         return this.z;
      }

      public float yaw() {
         return this.yaw;
      }

      public float pitch() {
         return this.pitch;
      }

      public String customModelId() {
         return this.customModelId;
      }

      public String swordId() {
         return this.swordId;
      }

      public String petId() {
         return this.petId;
      }

      public String figuraId() {
         return this.figuraId;
      }

      public List<GlobalSocialManager.PartyPoint> points() {
         return this.points;
      }

      public long updatedAt() {
         return this.updatedAt;
      }
   }

   public record PartyPoint(String id, String ownerProfileId, String ownerNickname, double x, double y, double z, long createdAt, boolean local) {

      public PartyPoint(String id, String ownerProfileId, String ownerNickname, double x, double y, double z, long createdAt, boolean local) {
         this.id = id;
         this.ownerProfileId = ownerProfileId;
         this.ownerNickname = ownerNickname;
         this.x = x;
         this.y = y;
         this.z = z;
         this.createdAt = createdAt;
         this.local = local;
      }

      public String id() {
         return this.id;
      }

      public String ownerProfileId() {
         return this.ownerProfileId;
      }

      public String ownerNickname() {
         return this.ownerNickname;
      }

      public double x() {
         return this.x;
      }

      public double y() {
         return this.y;
      }

      public double z() {
         return this.z;
      }

      public long createdAt() {
         return this.createdAt;
      }

      public boolean local() {
         return this.local;
      }
   }
}
