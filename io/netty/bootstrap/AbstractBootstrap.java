package io.netty.bootstrap;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractBootstrap implements Cloneable {
   private volatile EventLoopGroup group;
   private volatile ChannelFactory channelFactory;
   private volatile SocketAddress localAddress;
   private final Map options = new LinkedHashMap();
   private final Map attrs = new LinkedHashMap();
   private volatile ChannelHandler handler;

   AbstractBootstrap() {
   }

   AbstractBootstrap(AbstractBootstrap bootstrap) {
      this.group = bootstrap.group;
      this.channelFactory = bootstrap.channelFactory;
      this.handler = bootstrap.handler;
      this.localAddress = bootstrap.localAddress;
      synchronized(bootstrap.options) {
         this.options.putAll(bootstrap.options);
      }

      synchronized(bootstrap.attrs) {
         this.attrs.putAll(bootstrap.attrs);
      }
   }

   public AbstractBootstrap group(EventLoopGroup group) {
      if(group == null) {
         throw new NullPointerException("group");
      } else if(this.group != null) {
         throw new IllegalStateException("group set already");
      } else {
         this.group = group;
         return this;
      }
   }

   public AbstractBootstrap channel(Class channelClass) {
      if(channelClass == null) {
         throw new NullPointerException("channelClass");
      } else {
         return this.channelFactory(new AbstractBootstrap.BootstrapChannelFactory(channelClass));
      }
   }

   public AbstractBootstrap channelFactory(ChannelFactory channelFactory) {
      if(channelFactory == null) {
         throw new NullPointerException("channelFactory");
      } else if(this.channelFactory != null) {
         throw new IllegalStateException("channelFactory set already");
      } else {
         this.channelFactory = channelFactory;
         return this;
      }
   }

   public AbstractBootstrap localAddress(SocketAddress localAddress) {
      this.localAddress = localAddress;
      return this;
   }

   public AbstractBootstrap localAddress(int inetPort) {
      return this.localAddress(new InetSocketAddress(inetPort));
   }

   public AbstractBootstrap localAddress(String inetHost, int inetPort) {
      return this.localAddress(new InetSocketAddress(inetHost, inetPort));
   }

   public AbstractBootstrap localAddress(InetAddress inetHost, int inetPort) {
      return this.localAddress(new InetSocketAddress(inetHost, inetPort));
   }

   public AbstractBootstrap option(ChannelOption option, Object value) {
      if(option == null) {
         throw new NullPointerException("option");
      } else {
         if(value == null) {
            synchronized(this.options) {
               this.options.remove(option);
            }
         } else {
            synchronized(this.options) {
               this.options.put(option, value);
            }
         }

         return this;
      }
   }

   public AbstractBootstrap attr(AttributeKey key, Object value) {
      if(key == null) {
         throw new NullPointerException("key");
      } else {
         if(value == null) {
            synchronized(this.attrs) {
               this.attrs.remove(key);
            }
         } else {
            synchronized(this.attrs) {
               this.attrs.put(key, value);
            }
         }

         return this;
      }
   }

   public AbstractBootstrap validate() {
      if(this.group == null) {
         throw new IllegalStateException("group not set");
      } else if(this.channelFactory == null) {
         throw new IllegalStateException("channel or channelFactory not set");
      } else {
         return this;
      }
   }

   public abstract AbstractBootstrap clone();

   public ChannelFuture register() {
      this.validate();
      return this.initAndRegister();
   }

   public ChannelFuture bind() {
      this.validate();
      SocketAddress localAddress = this.localAddress;
      if(localAddress == null) {
         throw new IllegalStateException("localAddress not set");
      } else {
         return this.doBind(localAddress);
      }
   }

   public ChannelFuture bind(int inetPort) {
      return this.bind(new InetSocketAddress(inetPort));
   }

   public ChannelFuture bind(String inetHost, int inetPort) {
      return this.bind(new InetSocketAddress(inetHost, inetPort));
   }

   public ChannelFuture bind(InetAddress inetHost, int inetPort) {
      return this.bind(new InetSocketAddress(inetHost, inetPort));
   }

   public ChannelFuture bind(SocketAddress localAddress) {
      this.validate();
      if(localAddress == null) {
         throw new NullPointerException("localAddress");
      } else {
         return this.doBind(localAddress);
      }
   }

   private ChannelFuture doBind(final SocketAddress localAddress) {
      final ChannelFuture regFuture = this.initAndRegister();
      final Channel channel = regFuture.channel();
      if(regFuture.cause() != null) {
         return regFuture;
      } else {
         final ChannelPromise promise;
         if(regFuture.isDone()) {
            promise = channel.newPromise();
            doBind0(regFuture, channel, localAddress, promise);
         } else {
            promise = new AbstractBootstrap.PendingRegistrationPromise(channel);
            regFuture.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  AbstractBootstrap.doBind0(regFuture, channel, localAddress, promise);
               }
            });
         }

         return promise;
      }
   }

   final ChannelFuture initAndRegister() {
      Channel channel = this.channelFactory().newChannel();

      try {
         this.init(channel);
      } catch (Throwable var3) {
         channel.unsafe().closeForcibly();
         return (new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE)).setFailure(var3);
      }

      ChannelFuture regFuture = this.group().register(channel);
      if(regFuture.cause() != null) {
         if(channel.isRegistered()) {
            channel.close();
         } else {
            channel.unsafe().closeForcibly();
         }
      }

      return regFuture;
   }

   abstract void init(Channel var1) throws Exception;

   private static void doBind0(final ChannelFuture regFuture, final Channel channel, final SocketAddress localAddress, final ChannelPromise promise) {
      channel.eventLoop().execute(new Runnable() {
         public void run() {
            if(regFuture.isSuccess()) {
               channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
               promise.setFailure(regFuture.cause());
            }

         }
      });
   }

   public AbstractBootstrap handler(ChannelHandler handler) {
      if(handler == null) {
         throw new NullPointerException("handler");
      } else {
         this.handler = handler;
         return this;
      }
   }

   final SocketAddress localAddress() {
      return this.localAddress;
   }

   final ChannelFactory channelFactory() {
      return this.channelFactory;
   }

   final ChannelHandler handler() {
      return this.handler;
   }

   public final EventLoopGroup group() {
      return this.group;
   }

   final Map options() {
      return this.options;
   }

   final Map attrs() {
      return this.attrs;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append('(');
      if(this.group != null) {
         buf.append("group: ");
         buf.append(StringUtil.simpleClassName((Object)this.group));
         buf.append(", ");
      }

      if(this.channelFactory != null) {
         buf.append("channelFactory: ");
         buf.append(this.channelFactory);
         buf.append(", ");
      }

      if(this.localAddress != null) {
         buf.append("localAddress: ");
         buf.append(this.localAddress);
         buf.append(", ");
      }

      synchronized(this.options) {
         if(!this.options.isEmpty()) {
            buf.append("options: ");
            buf.append(this.options);
            buf.append(", ");
         }
      }

      synchronized(this.attrs) {
         if(!this.attrs.isEmpty()) {
            buf.append("attrs: ");
            buf.append(this.attrs);
            buf.append(", ");
         }
      }

      if(this.handler != null) {
         buf.append("handler: ");
         buf.append(this.handler);
         buf.append(", ");
      }

      if(buf.charAt(buf.length() - 1) == 40) {
         buf.append(')');
      } else {
         buf.setCharAt(buf.length() - 2, ')');
         buf.setLength(buf.length() - 1);
      }

      return buf.toString();
   }

   private static final class BootstrapChannelFactory implements ChannelFactory {
      private final Class clazz;

      BootstrapChannelFactory(Class clazz) {
         this.clazz = clazz;
      }

      public Channel newChannel() {
         try {
            return (Channel)this.clazz.newInstance();
         } catch (Throwable var2) {
            throw new ChannelException("Unable to create Channel from class " + this.clazz, var2);
         }
      }

      public String toString() {
         return StringUtil.simpleClassName(this.clazz) + ".class";
      }
   }

   private static final class PendingRegistrationPromise extends DefaultChannelPromise {
      private PendingRegistrationPromise(Channel channel) {
         super(channel);
      }

      protected EventExecutor executor() {
         return (EventExecutor)(this.channel().isRegistered()?super.executor():GlobalEventExecutor.INSTANCE);
      }
   }
}
