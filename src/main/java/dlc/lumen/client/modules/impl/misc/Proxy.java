package dlc.lumen.client.modules.impl.misc;

import dlc.lumen.api.utils.network.ProxyManager;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.ModeSetting;
import dlc.lumen.client.modules.settings.implement.TextSetting;
import io.netty.channel.ChannelHandler;
import java.util.List;

public class Proxy extends Module {
   public static final Proxy INSTANCE = new Proxy();
   private static final String TEXT = "Авто";
   private static final String TEXT2 = "SOCKS5";
   private static final String TEXT3 = "SOCKS4";
   private static final String TEXT4 = "HTTP";
   private final ModeSetting modeSetting = new ModeSetting("Тип", "Авто", "Авто", "SOCKS5", "SOCKS4", "HTTP");
   private final TextSetting hostportSetting = new TextSetting("Прокси (host:port)", "", 128);
   private final TextSetting textSetting = new TextSetting("Логин (опц.)", "", 64);
   private final TextSetting textSetting2 = new TextSetting("Пароль (опц.)", "", 64);

   public Proxy() {
      super("Proxy", "Заход на сервера через прокси (SOCKS5/SOCKS4/HTTP)", Module.ModuleCategory.MISC);
      this.addSettings(this.modeSetting, this.hostportSetting, this.textSetting, this.textSetting2);
      ProxyManager.setProvider(this::helper);
   }

   public boolean cfgEnabled() {
      return this.isEnable();
   }

   public void cfgSetEnabled(boolean state) {
      if (state != this.isEnable()) {
         this.setEnabled(state);
      }
   }

   public String cfgAddress() {
      return this.hostportSetting.get();
   }

   public void cfgSetAddress(String value) {
      this.hostportSetting.setText(value == null ? "" : value);
   }

   public String cfgUser() {
      return this.textSetting.get();
   }

   public void cfgSetUser(String value) {
      this.textSetting.setText(value == null ? "" : value);
   }

   public String cfgPass() {
      return this.textSetting2.get();
   }

   public void cfgSetPass(String value) {
      this.textSetting2.setText(value == null ? "" : value);
   }

   public String cfgType() {
      return this.modeSetting.getCurrent();
   }

   public void cfgSetType(String value) {
      this.modeSetting.set(value);
   }

   public List<String> cfgTypes() {
      return this.modeSetting.getMods();
   }

   public String buttonLabel() {
      if (!this.isEnable()) {
         return "Proxy: выкл";
      }

      String var1 = this.hostportSetting.get();
      if (var1 != null && !var1.isBlank()) {
         int var2 = var1.indexOf("://");
         if (var2 >= 0) {
            var1 = var1.substring(var2 + 3);
         }

         int var3 = var1.lastIndexOf(64);
         if (var3 >= 0) {
            var1 = var1.substring(var3 + 1);
         }

         return "Proxy: " + var1;
      } else {
         return "Proxy: вкл";
      }
   }

   private ChannelHandler helper() {
      if (!this.isEnable()) {
         return null;
      }

      ProxyManager.Type var1 = switch (this.modeSetting.getCurrent()) {
         case "SOCKS4" -> ProxyManager.Type.SOCKS4;
         case "HTTP" -> ProxyManager.Type.HTTP;
         default -> ProxyManager.Type.SOCKS5;
      };
      ProxyManager.Parsed var4 = ProxyManager.parse(this.hostportSetting.get(), var1, this.textSetting.get(), this.textSetting2.get());
      return var4 == null ? null : var4.toHandler();
   }
}
