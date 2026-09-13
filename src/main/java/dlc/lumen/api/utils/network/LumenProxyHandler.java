package dlc.lumen.api.utils.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LumenProxyHandler extends ChannelDuplexHandler {
   private final ProxyManager.Type type;
   private final InetSocketAddress proxyAddress;
   private final String username;
   private final String password;
   private InetSocketAddress inetSocketAddress;
   private ChannelPromise promise;
   private ByteBuf byteBuf;
   private boolean flag;
   private static final int count = 0;
   private static final int count2 = 1;
   private static final int count3 = 2;
   private int count4 = 0;

   public LumenProxyHandler(ProxyManager.Type type, InetSocketAddress proxyAddress, String username, String password) {
      this.type = type;
      this.proxyAddress = proxyAddress;
      this.username = username;
      this.password = password;
   }

   private boolean getUsername() {
      return this.username != null && !this.username.isEmpty();
   }

   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      if (!(remoteAddress instanceof InetSocketAddress)) {
         ctx.connect(remoteAddress, localAddress, promise);
      } else {
         this.inetSocketAddress = (InetSocketAddress)remoteAddress;
         this.promise = promise;
         ChannelPromise var5 = ctx.newPromise();
         var5.addListener((ChannelFutureListener)f -> {
            if (!f.isSuccess()) {
               this.handleCtx10(ctx, f.cause());
            }
         });
         ctx.connect(this.proxyAddress, localAddress, var5);
      }
   }

   public void channelActive(ChannelHandlerContext ctx) {
      try {
         this.handleCtx(ctx);
      } catch (Exception var3) {
         this.handleCtx10(ctx, var3);
      }
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if (!this.flag) {
         this.handleCtx10(ctx, new RuntimeException("Proxy closed connection during handshake"));
      } else {
         super.channelInactive(ctx);
      }
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (!this.flag) {
         this.handleCtx10(ctx, cause);
      } else {
         ctx.fireExceptionCaught(cause);
      }
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) {
      if (!this.flag && msg instanceof ByteBuf var3) {
         if (this.byteBuf == null) {
            this.byteBuf = ctx.alloc().buffer();
         }

         this.byteBuf.writeBytes(var3);
         var3.release();

         try {
            switch (this.type) {
               case SOCKS5:
                  this.handleCtx2(ctx);
                  break;
               case SOCKS4:
                  this.handleCtx6(ctx);
                  break;
               case HTTP:
                  this.handleCtx8(ctx);
            }
         } catch (Exception var5) {
            this.handleCtx10(ctx, var5);
         }
      } else {
         ctx.fireChannelRead(msg);
      }
   }

   public void handlerRemoved(ChannelHandlerContext ctx) {
      if (this.byteBuf != null) {
         this.byteBuf.release();
         this.byteBuf = null;
      }
   }

   private void handleCtx(ChannelHandlerContext ctx) {
      switch (this.type) {
         case SOCKS5:
            ByteBuf var2 = ctx.alloc().buffer();
            var2.writeByte(5);
            if (this.getUsername()) {
               var2.writeByte(2).writeByte(0).writeByte(2);
            } else {
               var2.writeByte(1).writeByte(0);
            }

            ctx.writeAndFlush(var2);
            this.count4 = 0;
            break;
         case SOCKS4:
            this.handleCtx5(ctx);
            break;
         case HTTP:
            this.handleCtx7(ctx);
      }
   }

   private void handleCtx2(ChannelHandlerContext ctx) {
      if (this.count4 == 0) {
         if (this.byteBuf.readableBytes() < 2) {
            return;
         }

         this.byteBuf.readUnsignedByte();
         short var2 = this.byteBuf.readUnsignedByte();
         if (var2 == 2) {
            this.handleCtx3(ctx);
            this.count4 = 1;
         } else {
            if (var2 != 0) {
               throw new RuntimeException("SOCKS5: прокси не принял метод авторизации (0x" + Integer.toHexString(var2) + ")");
            }

            this.handleCtx4(ctx);
            this.count4 = 2;
         }
      }

      if (this.count4 == 1) {
         if (this.byteBuf.readableBytes() < 2) {
            return;
         }

         this.byteBuf.readUnsignedByte();
         short var5 = this.byteBuf.readUnsignedByte();
         if (var5 != 0) {
            throw new RuntimeException("SOCKS5: неверный логин/пароль прокси");
         }

         this.handleCtx4(ctx);
         this.count4 = 2;
      }

      if (this.count4 == 2) {
         if (this.byteBuf.readableBytes() < 4) {
            return;
         }

         this.byteBuf.markReaderIndex();
         this.byteBuf.readUnsignedByte();
         short var6 = this.byteBuf.readUnsignedByte();
         this.byteBuf.readUnsignedByte();
         short var3 = this.byteBuf.readUnsignedByte();
         short var4;
         if (var3 == 1) {
            var4 = 4;
         } else if (var3 == 4) {
            var4 = 16;
         } else {
            if (var3 != 3) {
               throw new RuntimeException("SOCKS5: неизвестный тип адреса " + var3);
            }

            if (this.byteBuf.readableBytes() < 1) {
               this.byteBuf.resetReaderIndex();
               return;
            }

            var4 = this.byteBuf.readUnsignedByte();
         }

         if (this.byteBuf.readableBytes() < var4 + 2) {
            this.byteBuf.resetReaderIndex();
            return;
         }

         this.byteBuf.skipBytes(var4 + 2);
         if (var6 != 0) {
            throw new RuntimeException("SOCKS5: сервер отклонил подключение (код " + var6 + ")");
         }

         this.handleCtx9(ctx);
      }
   }

   private void handleCtx3(ChannelHandlerContext ctx) {
      byte[] var2 = this.username == null ? new byte[0] : this.username.getBytes(StandardCharsets.UTF_8);
      byte[] var3 = this.password == null ? new byte[0] : this.password.getBytes(StandardCharsets.UTF_8);
      ByteBuf var4 = ctx.alloc().buffer();
      var4.writeByte(1);
      var4.writeByte(var2.length).writeBytes(var2);
      var4.writeByte(var3.length).writeBytes(var3);
      ctx.writeAndFlush(var4);
   }

   private void handleCtx4(ChannelHandlerContext ctx) {
      ByteBuf var2 = ctx.alloc().buffer();
      var2.writeByte(5).writeByte(1).writeByte(0);
      InetAddress var3 = this.inetSocketAddress.isUnresolved() ? null : this.inetSocketAddress.getAddress();
      if (var3 instanceof Inet4Address) {
         var2.writeByte(1).writeBytes(var3.getAddress());
      } else if (var3 instanceof Inet6Address) {
         var2.writeByte(4).writeBytes(var3.getAddress());
      } else {
         byte[] var4 = this.inetSocketAddress.getHostString().getBytes(StandardCharsets.US_ASCII);
         var2.writeByte(3).writeByte(var4.length).writeBytes(var4);
      }

      var2.writeShort(this.inetSocketAddress.getPort());
      ctx.writeAndFlush(var2);
   }

   private void handleCtx5(ChannelHandlerContext ctx) {
      InetAddress var2 = this.inetSocketAddress.isUnresolved() ? null : this.inetSocketAddress.getAddress();
      boolean var3 = var2 instanceof Inet4Address;
      byte[] var4 = this.username == null ? new byte[0] : this.username.getBytes(StandardCharsets.US_ASCII);
      ByteBuf var5 = ctx.alloc().buffer();
      var5.writeByte(4).writeByte(1);
      var5.writeShort(this.inetSocketAddress.getPort());
      if (var3) {
         var5.writeBytes(var2.getAddress());
         var5.writeBytes(var4).writeByte(0);
      } else {
         var5.writeByte(0).writeByte(0).writeByte(0).writeByte(1);
         var5.writeBytes(var4).writeByte(0);
         byte[] var6 = this.inetSocketAddress.getHostString().getBytes(StandardCharsets.US_ASCII);
         var5.writeBytes(var6).writeByte(0);
      }

      ctx.writeAndFlush(var5);
   }

   private void handleCtx6(ChannelHandlerContext ctx) {
      if (this.byteBuf.readableBytes() >= 8) {
         this.byteBuf.readUnsignedByte();
         short var2 = this.byteBuf.readUnsignedByte();
         this.byteBuf.skipBytes(6);
         if (var2 != 90) {
            throw new RuntimeException("SOCKS4: сервер отклонил подключение (код " + var2 + ")");
         }

         this.handleCtx9(ctx);
      }
   }

   private void handleCtx7(ChannelHandlerContext ctx) {
      String var2 = this.inetSocketAddress.getHostString() + ":" + this.inetSocketAddress.getPort();
      StringBuilder var3 = new StringBuilder();
      var3.append("CONNECT ").append(var2).append(" HTTP/1.1\r\n");
      var3.append("Host: ").append(var2).append("\r\n");
      if (this.getUsername()) {
         String var4 = (this.username == null ? "" : this.username) + ":" + (this.password == null ? "" : this.password);
         String var5 = Base64.getEncoder().encodeToString(var4.getBytes(StandardCharsets.UTF_8));
         var3.append("Proxy-Authorization: Basic ").append(var5).append("\r\n");
      }

      var3.append("Proxy-Connection: keep-alive\r\n\r\n");
      ByteBuf var6 = ctx.alloc().buffer();
      var6.writeCharSequence(var3.toString(), StandardCharsets.US_ASCII);
      ctx.writeAndFlush(var6);
   }

   private void handleCtx8(ChannelHandlerContext ctx) {
      int var2 = calcBuf(this.byteBuf);
      if (var2 < 0) {
         if (this.byteBuf.readableBytes() > 16384) {
            throw new RuntimeException("HTTP-прокси: слишком большой ответ на CONNECT");
         }
      } else {
         int var3 = this.byteBuf.readerIndex();
         int var4 = var2 + 4 - var3;
         String var5 = this.byteBuf.toString(var3, var4, StandardCharsets.US_ASCII);
         this.byteBuf.readerIndex(var2 + 4);
         String var6 = var5.split("\r\n", 2)[0].trim();
         String[] var7 = var6.split("\\s+");
         int var8 = -1;
         if (var7.length >= 2) {
            try {
               var8 = Integer.parseInt(var7[1]);
            } catch (NumberFormatException var10) {
            }
         }

         if (var8 >= 200 && var8 < 300) {
            this.handleCtx9(ctx);
         } else {
            throw new RuntimeException("HTTP-прокси отклонил CONNECT: " + var6);
         }
      }
   }

   private static int calcBuf(ByteBuf buf) {
      int var1 = buf.writerIndex();

      for (int var2 = buf.readerIndex(); var2 + 3 < var1; var2++) {
         if (buf.getByte(var2) == 13 && buf.getByte(var2 + 1) == 10 && buf.getByte(var2 + 2) == 13 && buf.getByte(var2 + 3) == 10) {
            return var2;
         }
      }

      return -1;
   }

   private void handleCtx9(ChannelHandlerContext ctx) {
      this.flag = true;
      ByteBuf var2 = this.byteBuf;
      this.byteBuf = null;
      ctx.fireChannelActive();
      this.promise.trySuccess();
      if (var2 != null) {
         if (var2.isReadable()) {
            ctx.fireChannelRead(var2);
         } else {
            var2.release();
         }
      }

      ctx.pipeline().remove(this);
   }

   private void handleCtx10(ChannelHandlerContext ctx, Throwable cause) {
      if (!this.flag) {
         this.flag = true;
         if (this.byteBuf != null) {
            this.byteBuf.release();
            this.byteBuf = null;
         }

         if (this.promise != null) {
            this.promise.tryFailure(cause);
         }

         ctx.close();
      }
   }
}
