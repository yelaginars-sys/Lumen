package dlc.lumen.client.ui.mainmenu.account;

import dlc.lumen.Lumen;
import dlc.lumen.api.QClient;
import dlc.lumen.mixin.IMinecraftClientAccessor;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;

public final class AccountManager extends CopyOnWriteArrayList<Account> implements QClient {
   private final AccountFile accountFile;
   private String text;

   public AccountManager() {
      File var1 = Lumen.INSTANCE.globalsDir == null ? new File("C:\\lumenClient", "lumen") : Lumen.INSTANCE.globalsDir;
      this.accountFile = new AccountFile(new File(var1, "accounts.json"));
      this.accountFile.read(this);
      this.helper();
   }

   private void helper() {
      Thread var1 = new Thread(() -> {
         try {
            if (this.text != null) {
               this.accountFile.writeLastSelected(this, this.text);
            } else {
               this.accountFile.write(this);
            }
         } catch (Exception var2) {
         }
      }, "lumen-Accounts-Shutdown");
      var1.setDaemon(false);
      Runtime.getRuntime().addShutdownHook(var1);
   }

   public void saveLastSelected(String name) {
      this.text = name;
      this.accountFile.writeLastSelected(this, name);
   }

   public void restoreLastSession() {
      String var1 = this.accountFile.getLast();
      if (var1 != null && !var1.isEmpty()) {
         this.getAccount(var1)
            .ifPresent(
               account -> {
                  try {
                     Constructor var1x = Session.class
                        .getDeclaredConstructor(String.class, UUID.class, String.class, Optional.class, Optional.class, AccountType.class);
                     var1x.setAccessible(true);
                     Session var2 = (Session)var1x.newInstance(
                        account.name(),
                        UUID.nameUUIDFromBytes(("OfflinePlayer:" + account.name()).getBytes()),
                        mc.getSession() == null ? "" : mc.getSession().getAccessToken(),
                        Optional.empty(),
                        Optional.empty(),
                        AccountType.MOJANG
                     );
                     ((IMinecraftClientAccessor)mc).setSession(var2);
                  } catch (Exception var3) {
                  }
               }
            );
      }
   }

   public AccountFile file() {
      return this.accountFile;
   }

   public void save() {
      this.accountFile.write(this);
   }

   public void addAccount(Account account) {
      if (account != null && !this.isAccount(account.name())) {
         this.add(account);
         this.save();
      }
   }

   public Optional<Account> getAccount(String name) {
      return name == null ? Optional.empty() : this.stream().filter(account -> account.name().equalsIgnoreCase(name)).findFirst();
   }

   public boolean isAccount(String name) {
      return this.getAccount(name).isPresent();
   }

   public void removeAccount(String name) {
      if (name != null) {
         this.removeIf(account -> account.name().equalsIgnoreCase(name));
         this.save();
      }
   }

   public void clearAccounts() {
      this.clear();
      this.save();
   }

   public List<Account> getFavoriteAccountsSorted() {
      return this.stream().filter(Account::favorite).toList();
   }
}
