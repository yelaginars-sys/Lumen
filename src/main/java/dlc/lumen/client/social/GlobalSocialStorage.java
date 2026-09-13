package dlc.lumen.client.social;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;

public final class GlobalSocialStorage {
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
   private static final String TEXT = "http://okamy5kq.beget.tech";
   private final File file2;
   private String text = UUID.randomUUID().toString();
   private String nickname2 = "";
   private String apiBaseUrl2 = "http://okamy5kq.beget.tech";

   public GlobalSocialStorage() {
      this.file2 = new File(readStringOrEmpty(), "global-social.json");
      this.readStringOrEmpty2();
      Runtime.getRuntime().addShutdownHook(new Thread(this::save));
   }

   private static File readStringOrEmpty() {
      File var0 = null;

      try {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null) {
            var0 = var1.runDirectory;
         }
      } catch (Throwable var2) {
      }

      if (var0 == null) {
         var0 = new File(".");
      }

      return new File(var0, "lumen");
   }

   public synchronized String getProfileId() {
      return this.text;
   }

   public synchronized String getNickname() {
      return this.nickname2;
   }

   public synchronized boolean hasNickname() {
      String var1 = readStringOrEmpty3(this.nickname2);
      return var1.length() >= 2 && var1.length() <= 24;
   }

   public synchronized void setNickname(String nickname) {
      this.nickname2 = readStringOrEmpty3(nickname);
      this.save();
   }

   public synchronized String getApiBaseUrl() {
      return this.apiBaseUrl2;
   }

   public synchronized void setApiBaseUrl(String apiBaseUrl) {
      this.apiBaseUrl2 = readStringOrEmpty4(apiBaseUrl);
      this.save();
   }

   public synchronized void save() {
      try {
         JsonObject var1 = new JsonObject();
         var1.addProperty("profileId", this.text);
         var1.addProperty("nickname", this.nickname2);
         var1.addProperty("apiBaseUrl", this.apiBaseUrl2);
         File var2 = this.file2.getParentFile();
         if (var2 != null) {
            var2.mkdirs();
         }

         try (OutputStreamWriter var3 = new OutputStreamWriter(new FileOutputStream(this.file2, false), StandardCharsets.UTF_8)) {
            GSON_BUILDER.toJson(var1, var3);
         }
      } catch (Exception var8) {
      }
   }

   private synchronized void readStringOrEmpty2() {
      try {
         if (!Files.exists(this.file2.toPath())) {
            this.save();
            return;
         }

         try (
            InputStream var1 = Files.newInputStream(this.file2.toPath());
            InputStreamReader var2 = new InputStreamReader(var1, StandardCharsets.UTF_8);
         ) {
            JsonObject var3 = JsonParser.parseReader(var2).getAsJsonObject();
            if (var3.has("profileId")) {
               String var4 = var3.get("profileId").getAsString();
               if (!var4.isBlank()) {
                  this.text = var4;
               }
            }

            if (var3.has("nickname")) {
               this.nickname2 = readStringOrEmpty3(var3.get("nickname").getAsString());
            }

            if (var3.has("apiBaseUrl")) {
               this.apiBaseUrl2 = readStringOrEmpty4(var3.get("apiBaseUrl").getAsString());
            }
         }
      } catch (Exception var9) {
      }

      if (this.text == null || this.text.isBlank()) {
         this.text = UUID.randomUUID().toString();
      }

      if (this.apiBaseUrl2 == null || this.apiBaseUrl2.isBlank()) {
         this.apiBaseUrl2 = "http://okamy5kq.beget.tech";
      }
   }

   private static String readStringOrEmpty3(String nickname) {
      return nickname == null ? "" : nickname.trim().replace('\n', ' ').replace('\r', ' ');
   }

   private static String readStringOrEmpty4(String apiBaseUrl) {
      String var1 = apiBaseUrl == null ? "" : apiBaseUrl.trim();
      if (var1.isBlank()) {
         return "http://okamy5kq.beget.tech";
      }

      if (!var1.startsWith("http://") && !var1.startsWith("https://")) {
         var1 = "http://" + var1;
      }

      while (var1.endsWith("/")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1;
   }
}
