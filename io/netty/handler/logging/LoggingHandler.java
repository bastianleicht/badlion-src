package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler {
   private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
   private static final String NEWLINE = StringUtil.NEWLINE;
   private static final String[] BYTE2HEX = new String[256];
   private static final String[] HEXPADDING = new String[16];
   private static final String[] BYTEPADDING = new String[16];
   private static final char[] BYTE2CHAR = new char[256];
   protected final InternalLogger logger;
   protected final InternalLogLevel internalLevel;
   private final LogLevel level;

   public LoggingHandler() {
      this(DEFAULT_LEVEL);
   }

   public LoggingHandler(LogLevel level) {
      if(level == null) {
         throw new NullPointerException("level");
      } else {
         this.logger = InternalLoggerFactory.getInstance(this.getClass());
         this.level = level;
         this.internalLevel = level.toInternalLevel();
      }
   }

   public LoggingHandler(Class clazz) {
      this(clazz, DEFAULT_LEVEL);
   }

   public LoggingHandler(Class clazz, LogLevel level) {
      if(clazz == null) {
         throw new NullPointerException("clazz");
      } else if(level == null) {
         throw new NullPointerException("level");
      } else {
         this.logger = InternalLoggerFactory.getInstance(clazz);
         this.level = level;
         this.internalLevel = level.toInternalLevel();
      }
   }

   public LoggingHandler(String name) {
      this(name, DEFAULT_LEVEL);
   }

   public LoggingHandler(String name, LogLevel level) {
      if(name == null) {
         throw new NullPointerException("name");
      } else if(level == null) {
         throw new NullPointerException("level");
      } else {
         this.logger = InternalLoggerFactory.getInstance(name);
         this.level = level;
         this.internalLevel = level.toInternalLevel();
      }
   }

   public LogLevel level() {
      return this.level;
   }

   protected String format(ChannelHandlerContext ctx, String message) {
      String chStr = ctx.channel().toString();
      StringBuilder buf = new StringBuilder(chStr.length() + message.length() + 1);
      buf.append(chStr);
      buf.append(' ');
      buf.append(message);
      return buf.toString();
   }

   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "REGISTERED"));
      }

      super.channelRegistered(ctx);
   }

   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "UNREGISTERED"));
      }

      super.channelUnregistered(ctx);
   }

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "ACTIVE"));
      }

      super.channelActive(ctx);
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "INACTIVE"));
      }

      super.channelInactive(ctx);
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "EXCEPTION: " + cause), cause);
      }

      super.exceptionCaught(ctx, cause);
   }

   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "USER_EVENT: " + evt));
      }

      super.userEventTriggered(ctx, evt);
   }

   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "BIND(" + localAddress + ')'));
      }

      super.bind(ctx, localAddress, promise);
   }

   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "CONNECT(" + remoteAddress + ", " + localAddress + ')'));
      }

      super.connect(ctx, remoteAddress, localAddress, promise);
   }

   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "DISCONNECT()"));
      }

      super.disconnect(ctx, promise);
   }

   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "CLOSE()"));
      }

      super.close(ctx, promise);
   }

   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "DEREGISTER()"));
      }

      super.deregister(ctx, promise);
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      this.logMessage(ctx, "RECEIVED", msg);
      ctx.fireChannelRead(msg);
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      this.logMessage(ctx, "WRITE", msg);
      ctx.write(msg, promise);
   }

   public void flush(ChannelHandlerContext ctx) throws Exception {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "FLUSH"));
      }

      ctx.flush();
   }

   private void logMessage(ChannelHandlerContext ctx, String eventName, Object msg) {
      if(this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, this.formatMessage(eventName, msg)));
      }

   }

   protected String formatMessage(String eventName, Object msg) {
      return msg instanceof ByteBuf?this.formatByteBuf(eventName, (ByteBuf)msg):(msg instanceof ByteBufHolder?this.formatByteBufHolder(eventName, (ByteBufHolder)msg):this.formatNonByteBuf(eventName, msg));
   }

   protected String formatByteBuf(String eventName, ByteBuf buf) {
      int length = buf.readableBytes();
      int rows = length / 16 + (length % 15 == 0?0:1) + 4;
      StringBuilder dump = new StringBuilder(rows * 80 + eventName.length() + 16);
      dump.append(eventName).append('(').append(length).append('B').append(')');
      dump.append(NEWLINE + "         +-------------------------------------------------+" + NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + NEWLINE + "+--------+-------------------------------------------------+----------------+");
      int startIndex = buf.readerIndex();
      int endIndex = buf.writerIndex();

      int i;
      for(i = startIndex; i < endIndex; ++i) {
         int relIdx = i - startIndex;
         int relIdxMod16 = relIdx & 15;
         if(relIdxMod16 == 0) {
            dump.append(NEWLINE);
            dump.append(Long.toHexString((long)relIdx & 4294967295L | 4294967296L));
            dump.setCharAt(dump.length() - 9, '|');
            dump.append('|');
         }

         dump.append(BYTE2HEX[buf.getUnsignedByte(i)]);
         if(relIdxMod16 == 15) {
            dump.append(" |");

            for(int j = i - 15; j <= i; ++j) {
               dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
            }

            dump.append('|');
         }
      }

      if((i - startIndex & 15) != 0) {
         int remainder = length & 15;
         dump.append(HEXPADDING[remainder]);
         dump.append(" |");

         for(int j = i - remainder; j < i; ++j) {
            dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
         }

         dump.append(BYTEPADDING[remainder]);
         dump.append('|');
      }

      dump.append(NEWLINE + "+--------+-------------------------------------------------+----------------+");
      return dump.toString();
   }

   protected String formatNonByteBuf(String eventName, Object msg) {
      return eventName + ": " + msg;
   }

   protected String formatByteBufHolder(String eventName, ByteBufHolder msg) {
      return this.formatByteBuf(eventName, msg.content());
   }

   static {
      for(int i = 0; i < BYTE2HEX.length; ++i) {
         BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);
      }

      for(int var4 = 0; var4 < HEXPADDING.length; ++var4) {
         int padding = HEXPADDING.length - var4;
         StringBuilder buf = new StringBuilder(padding * 3);

         for(int j = 0; j < padding; ++j) {
            buf.append("   ");
         }

         HEXPADDING[var4] = buf.toString();
      }

      for(int var5 = 0; var5 < BYTEPADDING.length; ++var5) {
         int padding = BYTEPADDING.length - var5;
         StringBuilder buf = new StringBuilder(padding);

         for(int j = 0; j < padding; ++j) {
            buf.append(' ');
         }

         BYTEPADDING[var5] = buf.toString();
      }

      for(int var6 = 0; var6 < BYTE2CHAR.length; ++var6) {
         if(var6 > 31 && var6 < 127) {
            BYTE2CHAR[var6] = (char)var6;
         } else {
            BYTE2CHAR[var6] = 46;
         }
      }

   }
}
