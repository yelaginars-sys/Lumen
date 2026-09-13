package dlc.lumen.api.utils.network;

import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

public final class ProxyManager {
   private static volatile Supplier<ChannelHandler> volatileValue;

   private ProxyManager() {
   }

   public static void setProvider(Supplier<ChannelHandler> p) {
      volatileValue = p;
   }

   public static ChannelHandler buildHandler() {
      Supplier var0 = volatileValue;
      if (var0 == null) {
         return null;
      }

      try {
         return (ChannelHandler)var0.get();
      } catch (Throwable var2) {
         return null;
      }
   }

   public static ProxyManager.Parsed parse(String raw, ProxyManager.Type defaultType, String fallbackUser, String fallbackPass) {
      if (raw == null) {
         return null;
      }

      String var4 = raw.trim();
      if (var4.isEmpty()) {
         return null;
      }

      ProxyManager.Type var5 = defaultType == null ? ProxyManager.Type.SOCKS5 : defaultType;
      int var6 = var4.indexOf("://");
      if (var6 >= 0) {
         String var7 = var4.substring(0, var6).toLowerCase();
         var4 = var4.substring(var6 + 3);
         switch (var7) {
            case "socks":
            case "socks5":
            case "socks5h":
               var5 = ProxyManager.Type.SOCKS5;
               break;
            case "socks4":
            case "socks4a":
               var5 = ProxyManager.Type.SOCKS4;
               break;
            case "http":
            case "https":
               var5 = ProxyManager.Type.HTTP;
         }
      }

      String var14 = null;
      String var15 = null;
      int var10 = var4.lastIndexOf(64);
      String var16;
      if (var10 >= 0) {
         String var11 = var4.substring(0, var10);
         var16 = var4.substring(var10 + 1);
         String[] var12 = var11.split(":", 2);
         var14 = var12[0];
         var15 = var12.length > 1 ? var12[1] : "";
      } else {
         var16 = var4;
      }

      String[] var17 = var16.split(":");
      int var13;
      String var18;
      if (var14 == null && var17.length == 4) {
         var18 = var17[0];
         var13 = calcValue(var17[1]);
         var14 = var17[2];
         var15 = var17[3];
      } else {
         if (var17.length < 2) {
            return null;
         }

         var18 = var17[0];
         var13 = calcValue(var17[1]);
      }

      if (!var18.isEmpty() && var13 > 0 && var13 <= 65535) {
         if (var14 == null || var14.isEmpty()) {
            var14 = fallbackUser;
            var15 = fallbackPass;
         }

         return new ProxyManager.Parsed(var5, var18, var13, var14, var15);
      } else {
         return null;
      }
   }

   private static int calcValue(String s) {
      try {
         return Integer.parseInt(s.trim());
      } catch (NumberFormatException var2) {
         return -1;
      }
   }

   private static String resolveText(String s) {
      return s != null && !s.isEmpty() ? s : null;
   }

   public record Parsed(ProxyManager.Type type, String host, int port, String username, String password) {

      public Parsed(ProxyManager.Type type, String host, int port, String username, String password) {
         this.type = type;
         this.host = host;
         this.port = port;
         this.username = username;
         this.password = password;
      }

      public LumenProxyHandler toHandler() {
         return new LumenProxyHandler(
            this.type,
            new InetSocketAddress(this.host, this.port),
            ProxyManager.resolveText(this.username),
            ProxyManager.resolveText(this.password)
         );
      }

      public ProxyManager.Type type() {
         return this.type;
      }

      public String host() {
         return this.host;
      }

      public int port() {
         return this.port;
      }

      public String username() {
         return this.username;
      }

      public String password() {
         return this.password;
      }
   }

   public enum Type {
      SOCKS5,
      SOCKS4,
      HTTP;
   }
}
