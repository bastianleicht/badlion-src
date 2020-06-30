package io.netty.channel;

import io.netty.channel.AbstractChannel;
import io.netty.channel.AbstractChannelHandlerContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final class DefaultChannelPipeline implements ChannelPipeline {
   static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
   private static final WeakHashMap[] nameCaches = new WeakHashMap[Runtime.getRuntime().availableProcessors()];
   final AbstractChannel channel;
   final AbstractChannelHandlerContext head;
   final AbstractChannelHandlerContext tail;
   private final Map name2ctx = new HashMap(4);
   final Map childExecutors = new IdentityHashMap();
   // $FF: synthetic field
   static final boolean $assertionsDisabled = !DefaultChannelPipeline.class.desiredAssertionStatus();

   public DefaultChannelPipeline(AbstractChannel channel) {
      if(channel == null) {
         throw new NullPointerException("channel");
      } else {
         this.channel = channel;
         this.tail = new DefaultChannelPipeline.TailContext(this);
         this.head = new DefaultChannelPipeline.HeadContext(this);
         this.head.next = this.tail;
         this.tail.prev = this.head;
      }
   }

   public Channel channel() {
      return this.channel;
   }

   public ChannelPipeline addFirst(String name, ChannelHandler handler) {
      return this.addFirst((EventExecutorGroup)null, name, handler);
   }

   public ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
      synchronized(this) {
         this.checkDuplicateName(name);
         AbstractChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
         this.addFirst0(name, newCtx);
         return this;
      }
   }

   private void addFirst0(String name, AbstractChannelHandlerContext newCtx) {
      checkMultiplicity(newCtx);
      AbstractChannelHandlerContext nextCtx = this.head.next;
      newCtx.prev = this.head;
      newCtx.next = nextCtx;
      this.head.next = newCtx;
      nextCtx.prev = newCtx;
      this.name2ctx.put(name, newCtx);
      this.callHandlerAdded(newCtx);
   }

   public ChannelPipeline addLast(String name, ChannelHandler handler) {
      return this.addLast((EventExecutorGroup)null, name, handler);
   }

   public ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
      synchronized(this) {
         this.checkDuplicateName(name);
         AbstractChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
         this.addLast0(name, newCtx);
         return this;
      }
   }

   private void addLast0(String name, AbstractChannelHandlerContext newCtx) {
      checkMultiplicity(newCtx);
      AbstractChannelHandlerContext prev = this.tail.prev;
      newCtx.prev = prev;
      newCtx.next = this.tail;
      prev.next = newCtx;
      this.tail.prev = newCtx;
      this.name2ctx.put(name, newCtx);
      this.callHandlerAdded(newCtx);
   }

   public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
      return this.addBefore((EventExecutorGroup)null, baseName, name, handler);
   }

   public ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
      synchronized(this) {
         AbstractChannelHandlerContext ctx = this.getContextOrDie(baseName);
         this.checkDuplicateName(name);
         AbstractChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
         this.addBefore0(name, ctx, newCtx);
         return this;
      }
   }

   private void addBefore0(String name, AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
      checkMultiplicity(newCtx);
      newCtx.prev = ctx.prev;
      newCtx.next = ctx;
      ctx.prev.next = newCtx;
      ctx.prev = newCtx;
      this.name2ctx.put(name, newCtx);
      this.callHandlerAdded(newCtx);
   }

   public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
      return this.addAfter((EventExecutorGroup)null, baseName, name, handler);
   }

   public ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
      synchronized(this) {
         AbstractChannelHandlerContext ctx = this.getContextOrDie(baseName);
         this.checkDuplicateName(name);
         AbstractChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
         this.addAfter0(name, ctx, newCtx);
         return this;
      }
   }

   private void addAfter0(String name, AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
      this.checkDuplicateName(name);
      checkMultiplicity(newCtx);
      newCtx.prev = ctx;
      newCtx.next = ctx.next;
      ctx.next.prev = newCtx;
      ctx.next = newCtx;
      this.name2ctx.put(name, newCtx);
      this.callHandlerAdded(newCtx);
   }

   public ChannelPipeline addFirst(ChannelHandler... handlers) {
      return this.addFirst((EventExecutorGroup)null, (ChannelHandler[])handlers);
   }

   public ChannelPipeline addFirst(EventExecutorGroup executor, ChannelHandler... handlers) {
      if(handlers == null) {
         throw new NullPointerException("handlers");
      } else if(handlers.length != 0 && handlers[0] != null) {
         int size;
         for(size = 1; size < handlers.length && handlers[size] != null; ++size) {
            ;
         }

         for(int i = size - 1; i >= 0; --i) {
            ChannelHandler h = handlers[i];
            this.addFirst(executor, this.generateName(h), h);
         }

         return this;
      } else {
         return this;
      }
   }

   public ChannelPipeline addLast(ChannelHandler... handlers) {
      return this.addLast((EventExecutorGroup)null, (ChannelHandler[])handlers);
   }

   public ChannelPipeline addLast(EventExecutorGroup executor, ChannelHandler... handlers) {
      if(handlers == null) {
         throw new NullPointerException("handlers");
      } else {
         for(ChannelHandler h : handlers) {
            if(h == null) {
               break;
            }

            this.addLast(executor, this.generateName(h), h);
         }

         return this;
      }
   }

   private String generateName(ChannelHandler handler) {
      WeakHashMap<Class<?>, String> cache = nameCaches[(int)(Thread.currentThread().getId() % (long)nameCaches.length)];
      Class<?> handlerType = handler.getClass();
      String name;
      synchronized(cache) {
         name = (String)cache.get(handlerType);
         if(name == null) {
            name = generateName0(handlerType);
            cache.put(handlerType, name);
         }
      }

      synchronized(this) {
         if(this.name2ctx.containsKey(name)) {
            String baseName = name.substring(0, name.length() - 1);
            int i = 1;

            while(true) {
               String newName = baseName + i;
               if(!this.name2ctx.containsKey(newName)) {
                  name = newName;
                  break;
               }

               ++i;
            }
         }

         return name;
      }
   }

   private static String generateName0(Class handlerType) {
      return StringUtil.simpleClassName(handlerType) + "#0";
   }

   public ChannelPipeline remove(ChannelHandler handler) {
      this.remove(this.getContextOrDie(handler));
      return this;
   }

   public ChannelHandler remove(String name) {
      return this.remove(this.getContextOrDie(name)).handler();
   }

   public ChannelHandler remove(Class handlerType) {
      return this.remove(this.getContextOrDie(handlerType)).handler();
   }

   private AbstractChannelHandlerContext remove(final AbstractChannelHandlerContext ctx) {
      if($assertionsDisabled || ctx != this.head && ctx != this.tail) {
         AbstractChannelHandlerContext context;
         Future<?> future;
         synchronized(this) {
            if(!ctx.channel().isRegistered() || ctx.executor().inEventLoop()) {
               this.remove0(ctx);
               return ctx;
            }

            future = ctx.executor().submit(new Runnable() {
               public void run() {
                  synchronized(DefaultChannelPipeline.this) {
                     DefaultChannelPipeline.this.remove0(ctx);
                  }
               }
            });
            context = ctx;
         }

         waitForFuture(future);
         return context;
      } else {
         throw new AssertionError();
      }
   }

   void remove0(AbstractChannelHandlerContext ctx) {
      AbstractChannelHandlerContext prev = ctx.prev;
      AbstractChannelHandlerContext next = ctx.next;
      prev.next = next;
      next.prev = prev;
      this.name2ctx.remove(ctx.name());
      this.callHandlerRemoved(ctx);
   }

   public ChannelHandler removeFirst() {
      if(this.head.next == this.tail) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.head.next).handler();
      }
   }

   public ChannelHandler removeLast() {
      if(this.head.next == this.tail) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.tail.prev).handler();
      }
   }

   public ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
      this.replace(this.getContextOrDie(oldHandler), newName, newHandler);
      return this;
   }

   public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
      return this.replace(this.getContextOrDie(oldName), newName, newHandler);
   }

   public ChannelHandler replace(Class oldHandlerType, String newName, ChannelHandler newHandler) {
      return this.replace(this.getContextOrDie(oldHandlerType), newName, newHandler);
   }

   private ChannelHandler replace(final AbstractChannelHandlerContext ctx, final String newName, ChannelHandler newHandler) {
      if($assertionsDisabled || ctx != this.head && ctx != this.tail) {
         Future<?> future;
         synchronized(this) {
            boolean sameName = ctx.name().equals(newName);
            if(!sameName) {
               this.checkDuplicateName(newName);
            }

            final AbstractChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, ctx.executor, newName, newHandler);
            if(!newCtx.channel().isRegistered() || newCtx.executor().inEventLoop()) {
               this.replace0(ctx, newName, newCtx);
               return ctx.handler();
            }

            future = newCtx.executor().submit(new Runnable() {
               public void run() {
                  synchronized(DefaultChannelPipeline.this) {
                     DefaultChannelPipeline.this.replace0(ctx, newName, newCtx);
                  }
               }
            });
         }

         waitForFuture(future);
         return ctx.handler();
      } else {
         throw new AssertionError();
      }
   }

   private void replace0(AbstractChannelHandlerContext oldCtx, String newName, AbstractChannelHandlerContext newCtx) {
      checkMultiplicity(newCtx);
      AbstractChannelHandlerContext prev = oldCtx.prev;
      AbstractChannelHandlerContext next = oldCtx.next;
      newCtx.prev = prev;
      newCtx.next = next;
      prev.next = newCtx;
      next.prev = newCtx;
      if(!oldCtx.name().equals(newName)) {
         this.name2ctx.remove(oldCtx.name());
      }

      this.name2ctx.put(newName, newCtx);
      oldCtx.prev = newCtx;
      oldCtx.next = newCtx;
      this.callHandlerAdded(newCtx);
      this.callHandlerRemoved(oldCtx);
   }

   private static void checkMultiplicity(ChannelHandlerContext ctx) {
      ChannelHandler handler = ctx.handler();
      if(handler instanceof ChannelHandlerAdapter) {
         ChannelHandlerAdapter h = (ChannelHandlerAdapter)handler;
         if(!h.isSharable() && h.added) {
            throw new ChannelPipelineException(h.getClass().getName() + " is not a @Sharable handler, so can\'t be added or removed multiple times.");
         }

         h.added = true;
      }

   }

   private void callHandlerAdded(final ChannelHandlerContext ctx) {
      if(ctx.channel().isRegistered() && !ctx.executor().inEventLoop()) {
         ctx.executor().execute(new Runnable() {
            public void run() {
               DefaultChannelPipeline.this.callHandlerAdded0(ctx);
            }
         });
      } else {
         this.callHandlerAdded0(ctx);
      }
   }

   private void callHandlerAdded0(ChannelHandlerContext ctx) {
      try {
         ctx.handler().handlerAdded(ctx);
      } catch (Throwable var6) {
         boolean removed = false;

         try {
            this.remove((AbstractChannelHandlerContext)ctx);
            removed = true;
         } catch (Throwable var5) {
            if(logger.isWarnEnabled()) {
               logger.warn("Failed to remove a handler: " + ctx.name(), var5);
            }
         }

         if(removed) {
            this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", var6));
         } else {
            this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", var6));
         }
      }

   }

   private void callHandlerRemoved(final AbstractChannelHandlerContext ctx) {
      if(ctx.channel().isRegistered() && !ctx.executor().inEventLoop()) {
         ctx.executor().execute(new Runnable() {
            public void run() {
               DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
            }
         });
      } else {
         this.callHandlerRemoved0(ctx);
      }
   }

   private void callHandlerRemoved0(AbstractChannelHandlerContext ctx) {
      try {
         ctx.handler().handlerRemoved(ctx);
         ctx.setRemoved();
      } catch (Throwable var3) {
         this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", var3));
      }

   }

   private static void waitForFuture(Future future) {
      try {
         future.get();
      } catch (ExecutionException var2) {
         PlatformDependent.throwException(var2.getCause());
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }

   }

   public ChannelHandler first() {
      ChannelHandlerContext first = this.firstContext();
      return first == null?null:first.handler();
   }

   public ChannelHandlerContext firstContext() {
      AbstractChannelHandlerContext first = this.head.next;
      return first == this.tail?null:this.head.next;
   }

   public ChannelHandler last() {
      AbstractChannelHandlerContext last = this.tail.prev;
      return last == this.head?null:last.handler();
   }

   public ChannelHandlerContext lastContext() {
      AbstractChannelHandlerContext last = this.tail.prev;
      return last == this.head?null:last;
   }

   public ChannelHandler get(String name) {
      ChannelHandlerContext ctx = this.context(name);
      return ctx == null?null:ctx.handler();
   }

   public ChannelHandler get(Class handlerType) {
      ChannelHandlerContext ctx = this.context(handlerType);
      return ctx == null?null:ctx.handler();
   }

   public ChannelHandlerContext context(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         synchronized(this) {
            return (ChannelHandlerContext)this.name2ctx.get(name);
         }
      }
   }

   public ChannelHandlerContext context(ChannelHandler handler) {
      if(handler == null) {
         throw new NullPointerException("handler");
      } else {
         for(AbstractChannelHandlerContext ctx = this.head.next; ctx != null; ctx = ctx.next) {
            if(ctx.handler() == handler) {
               return ctx;
            }
         }

         return null;
      }
   }

   public ChannelHandlerContext context(Class handlerType) {
      if(handlerType == null) {
         throw new NullPointerException("handlerType");
      } else {
         for(AbstractChannelHandlerContext ctx = this.head.next; ctx != null; ctx = ctx.next) {
            if(handlerType.isAssignableFrom(ctx.handler().getClass())) {
               return ctx;
            }
         }

         return null;
      }
   }

   public List names() {
      List<String> list = new ArrayList();

      for(AbstractChannelHandlerContext ctx = this.head.next; ctx != null; ctx = ctx.next) {
         list.add(ctx.name());
      }

      return list;
   }

   public Map toMap() {
      Map<String, ChannelHandler> map = new LinkedHashMap();

      for(AbstractChannelHandlerContext ctx = this.head.next; ctx != this.tail; ctx = ctx.next) {
         map.put(ctx.name(), ctx.handler());
      }

      return map;
   }

   public Iterator iterator() {
      return this.toMap().entrySet().iterator();
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append('{');
      AbstractChannelHandlerContext ctx = this.head.next;

      while(ctx != this.tail) {
         buf.append('(');
         buf.append(ctx.name());
         buf.append(" = ");
         buf.append(ctx.handler().getClass().getName());
         buf.append(')');
         ctx = ctx.next;
         if(ctx == this.tail) {
            break;
         }

         buf.append(", ");
      }

      buf.append('}');
      return buf.toString();
   }

   public ChannelPipeline fireChannelRegistered() {
      this.head.fireChannelRegistered();
      return this;
   }

   public ChannelPipeline fireChannelUnregistered() {
      this.head.fireChannelUnregistered();
      if(!this.channel.isOpen()) {
         this.teardownAll();
      }

      return this;
   }

   private void teardownAll() {
      this.tail.prev.teardown();
   }

   public ChannelPipeline fireChannelActive() {
      this.head.fireChannelActive();
      if(this.channel.config().isAutoRead()) {
         this.channel.read();
      }

      return this;
   }

   public ChannelPipeline fireChannelInactive() {
      this.head.fireChannelInactive();
      return this;
   }

   public ChannelPipeline fireExceptionCaught(Throwable cause) {
      this.head.fireExceptionCaught(cause);
      return this;
   }

   public ChannelPipeline fireUserEventTriggered(Object event) {
      this.head.fireUserEventTriggered(event);
      return this;
   }

   public ChannelPipeline fireChannelRead(Object msg) {
      this.head.fireChannelRead(msg);
      return this;
   }

   public ChannelPipeline fireChannelReadComplete() {
      this.head.fireChannelReadComplete();
      if(this.channel.config().isAutoRead()) {
         this.read();
      }

      return this;
   }

   public ChannelPipeline fireChannelWritabilityChanged() {
      this.head.fireChannelWritabilityChanged();
      return this;
   }

   public ChannelFuture bind(SocketAddress localAddress) {
      return this.tail.bind(localAddress);
   }

   public ChannelFuture connect(SocketAddress remoteAddress) {
      return this.tail.connect(remoteAddress);
   }

   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.tail.connect(remoteAddress, localAddress);
   }

   public ChannelFuture disconnect() {
      return this.tail.disconnect();
   }

   public ChannelFuture close() {
      return this.tail.close();
   }

   public ChannelFuture deregister() {
      return this.tail.deregister();
   }

   public ChannelPipeline flush() {
      this.tail.flush();
      return this;
   }

   public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
      return this.tail.bind(localAddress, promise);
   }

   public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      return this.tail.connect(remoteAddress, promise);
   }

   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      return this.tail.connect(remoteAddress, localAddress, promise);
   }

   public ChannelFuture disconnect(ChannelPromise promise) {
      return this.tail.disconnect(promise);
   }

   public ChannelFuture close(ChannelPromise promise) {
      return this.tail.close(promise);
   }

   public ChannelFuture deregister(ChannelPromise promise) {
      return this.tail.deregister(promise);
   }

   public ChannelPipeline read() {
      this.tail.read();
      return this;
   }

   public ChannelFuture write(Object msg) {
      return this.tail.write(msg);
   }

   public ChannelFuture write(Object msg, ChannelPromise promise) {
      return this.tail.write(msg, promise);
   }

   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      return this.tail.writeAndFlush(msg, promise);
   }

   public ChannelFuture writeAndFlush(Object msg) {
      return this.tail.writeAndFlush(msg);
   }

   private void checkDuplicateName(String name) {
      if(this.name2ctx.containsKey(name)) {
         throw new IllegalArgumentException("Duplicate handler name: " + name);
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(String name) {
      AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(name);
      if(ctx == null) {
         throw new NoSuchElementException(name);
      } else {
         return ctx;
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(ChannelHandler handler) {
      AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handler);
      if(ctx == null) {
         throw new NoSuchElementException(handler.getClass().getName());
      } else {
         return ctx;
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(Class handlerType) {
      AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handlerType);
      if(ctx == null) {
         throw new NoSuchElementException(handlerType.getName());
      } else {
         return ctx;
      }
   }

   static {
      for(int i = 0; i < nameCaches.length; ++i) {
         nameCaches[i] = new WeakHashMap();
      }

   }

   static final class HeadContext extends AbstractChannelHandlerContext implements ChannelOutboundHandler {
      private static final String HEAD_NAME = DefaultChannelPipeline.generateName0(DefaultChannelPipeline.HeadContext.class);
      protected final Channel.Unsafe unsafe;

      HeadContext(DefaultChannelPipeline pipeline) {
         super(pipeline, (EventExecutorGroup)null, HEAD_NAME, false, true);
         this.unsafe = pipeline.channel().unsafe();
      }

      public ChannelHandler handler() {
         return this;
      }

      public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      }

      public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      }

      public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
         this.unsafe.bind(localAddress, promise);
      }

      public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
         this.unsafe.connect(remoteAddress, localAddress, promise);
      }

      public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
         this.unsafe.disconnect(promise);
      }

      public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
         this.unsafe.close(promise);
      }

      public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
         this.unsafe.deregister(promise);
      }

      public void read(ChannelHandlerContext ctx) {
         this.unsafe.beginRead();
      }

      public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
         this.unsafe.write(msg, promise);
      }

      public void flush(ChannelHandlerContext ctx) throws Exception {
         this.unsafe.flush();
      }

      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         ctx.fireExceptionCaught(cause);
      }
   }

   static final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {
      private static final String TAIL_NAME = DefaultChannelPipeline.generateName0(DefaultChannelPipeline.TailContext.class);

      TailContext(DefaultChannelPipeline pipeline) {
         super(pipeline, (EventExecutorGroup)null, TAIL_NAME, true, false);
      }

      public ChannelHandler handler() {
         return this;
      }

      public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      }

      public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      }

      public void channelActive(ChannelHandlerContext ctx) throws Exception {
      }

      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      }

      public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
      }

      public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      }

      public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      }

      public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      }

      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         DefaultChannelPipeline.logger.warn("An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.", cause);
      }

      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
         try {
            DefaultChannelPipeline.logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", msg);
         } finally {
            ReferenceCountUtil.release(msg);
         }

      }

      public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      }
   }
}
