package dlc.lumen.client.ui.mainmenu.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dlc.lumen.api.QClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public record AccountFile(File file) implements QClient {
   private static final Gson GSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();

   public AccountFile(File file) {
      Objects.requireNonNull(file, "file");
      this.file = file;
   }

   public boolean read(AccountManager accounts) {
      if (!this.file.exists()) {
         return false;
      }

      try (BufferedReader var2 = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8)) {
         JsonObject var3 = (JsonObject)GSON_BUILDER.fromJson(var2, JsonObject.class);
         if (var3 == null) {
            return false;
         }

         JsonArray var4 = var3.getAsJsonArray("accounts");
         if (var4 == null) {
            return true;
         }

         for (JsonElement var6 : var4) {
            if (var6 instanceof JsonObject var7) {
               String var8 = var7.has("name") ? var7.get("name").getAsString() : "";
               String var9 = var7.has("creationDate") ? var7.get("creationDate").getAsString() : "";
               boolean var10 = var7.has("favorite") && var7.get("favorite").getAsBoolean();
               if (!var8.isBlank()) {
                  LocalDateTime var11;
                  try {
                     var11 = var9.isBlank() ? LocalDateTime.now() : LocalDateTime.parse(var9);
                  } catch (DateTimeParseException var14) {
                     var11 = LocalDateTime.now();
                  }

                  if (!accounts.isAccount(var8)) {
                     Account var12 = new Account(var11, var8);
                     var12.favorite(var10);
                     accounts.add(var12);
                  }
               }
            }
         }

         return true;
      } catch (IOException var16) {
         return false;
      }
   }

   public boolean write(AccountManager accounts) {
      return this.writeLastSelected(accounts, mc.getSession() == null ? "" : mc.getSession().getUsername());
   }

   public boolean writeLastSelected(AccountManager accounts, String lastName) {
      File var3 = this.file.getParentFile();
      if (var3 != null && !var3.exists() && !var3.mkdirs()) {
         return false;
      }

      JsonObject var4 = new JsonObject();
      JsonArray var5 = new JsonArray();

      for (Account var7 : accounts) {
         JsonObject var8 = new JsonObject();
         var8.addProperty("name", var7.name());
         var8.addProperty("creationDate", var7.creationDate().toString());
         var8.addProperty("favorite", var7.favorite());
         var5.add(var8);
      }

      var4.add("accounts", var5);
      var4.addProperty("last", lastName == null ? "" : lastName);

      try (BufferedWriter var12 = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8)) {
         GSON_BUILDER.toJson(var4, var12);
         return true;
      } catch (IOException var11) {
         return false;
      }
   }

   public String getLast() {
      if (!this.file.exists()) {
         return "";
      }

      try (BufferedReader var1 = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8)) {
         JsonObject var2 = (JsonObject)GSON_BUILDER.fromJson(var1, JsonObject.class);
         return var2 != null && var2.has("last") ? var2.get("last").getAsString() : "";
      } catch (Exception var6) {
         return "";
      }
   }

   public File file() {
      return this.file;
   }
}
