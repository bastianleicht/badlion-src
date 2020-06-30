package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.group.CombinedIterator;
import io.netty.channel.group.DefaultChannelGroupFuture;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ConcurrentSet;
import io.netty.util.internal.StringUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup extends AbstractSet implements ChannelGroup {
   private static final AtomicInteger nextId = new AtomicInteger();
   private final String name;
   private final EventExecutor executor;
   private final ConcurrentSet serverChannels;
   private final ConcurrentSet nonServerChannels;
   private final ChannelFutureListener remover;

   public DefaultChannelGroup(EventExecutor executor) {
      this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor);
   }

   public DefaultChannelGroup(String name, EventExecutor executor) {
      this.serverChannels = new ConcurrentSet();
      this.nonServerChannels = new ConcurrentSet();
      this.remover = new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) throws Exception {
            DefaultChannelGroup.this.remove(future.channel());
         }
      };
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         this.name = name;
         this.executor = executor;
      }
   }

   public String name() {
      return this.name;
   }

   public boolean isEmpty() {
      return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
   }

   public int size() {
      return this.nonServerChannels.size() + this.serverChannels.size();
   }

   public boolean contains(Object o) {
      if(o instanceof Channel) {
         Channel c = (Channel)o;
         return o instanceof ServerChannel?this.serverChannels.contains(c):this.nonServerChannels.contains(c);
      } else {
         return false;
      }
   }

   public boolean add(Channel channel) {
      ConcurrentSet<Channel> set = channel instanceof ServerChannel?this.serverChannels:this.nonServerChannels;
      boolean added = set.add(channel);
      if(added) {
         channel.closeFuture().addListener(this.remover);
      }

      return added;
   }

   public boolean remove(Object o) {
      if(!(o instanceof Channel)) {
         return false;
      } else {
         Channel c = (Channel)o;
         boolean removed;
         if(c instanceof ServerChannel) {
            removed = this.serverChannels.remove(c);
         } else {
            removed = this.nonServerChannels.remove(c);
         }

         if(!removed) {
            return false;
         } else {
            c.closeFuture().removeListener(this.remover);
            return true;
         }
      }
   }

   public void clear() {
      this.nonServerChannels.clear();
      this.serverChannels.clear();
   }

   public Iterator iterator() {
      return new CombinedIterator(this.serverChannels.iterator(), this.nonServerChannels.iterator());
   }

   public Object[] toArray() {
      Collection<Channel> channels = new ArrayList(this.size());
      channels.addAll(this.serverChannels);
      channels.addAll(this.nonServerChannels);
      return channels.toArray();
   }

   public Object[] toArray(Object[] a) {
      Collection<Channel> channels = new ArrayList(this.size());
      channels.addAll(this.serverChannels);
      channels.addAll(this.nonServerChannels);
      return channels.toArray(a);
   }

   public ChannelGroupFuture close() {
      return this.close(ChannelMatchers.all());
   }

   public ChannelGroupFuture disconnect() {
      return this.disconnect(ChannelMatchers.all());
   }

   public ChannelGroupFuture deregister() {
      return this.deregister(ChannelMatchers.all());
   }

   public ChannelGroupFuture write(Object message) {
      return this.write(message, ChannelMatchers.all());
   }

   private static Object safeDuplicate(Object message) {
      return message instanceof ByteBuf?((ByteBuf)message).duplicate().retain():(message instanceof ByteBufHolder?((ByteBufHolder)message).duplicate().retain():ReferenceCountUtil.retain(message));
   }

   public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
      if(message == null) {
         throw new NullPointerException("message");
      } else if(matcher == null) {
         throw new NullPointerException("matcher");
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap(this.size());

         for(Channel c : this.nonServerChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.write(safeDuplicate(message)));
            }
         }

         ReferenceCountUtil.release(message);
         return new DefaultChannelGroupFuture(this, futures, this.executor);
      }
   }

   public ChannelGroup flush() {
      return this.flush(ChannelMatchers.all());
   }

   public ChannelGroupFuture flushAndWrite(Object message) {
      return this.writeAndFlush(message);
   }

   public ChannelGroupFuture writeAndFlush(Object message) {
      return this.writeAndFlush(message, ChannelMatchers.all());
   }

   public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
      if(matcher == null) {
         throw new NullPointerException("matcher");
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap(this.size());

         for(Channel c : this.serverChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.disconnect());
            }
         }

         for(Channel c : this.nonServerChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.disconnect());
            }
         }

         return new DefaultChannelGroupFuture(this, futures, this.executor);
      }
   }

   public ChannelGroupFuture close(ChannelMatcher matcher) {
      if(matcher == null) {
         throw new NullPointerException("matcher");
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap(this.size());

         for(Channel c : this.serverChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.close());
            }
         }

         for(Channel c : this.nonServerChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.close());
            }
         }

         return new DefaultChannelGroupFuture(this, futures, this.executor);
      }
   }

   public ChannelGroupFuture deregister(ChannelMatcher matcher) {
      if(matcher == null) {
         throw new NullPointerException("matcher");
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap(this.size());

         for(Channel c : this.serverChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.deregister());
            }
         }

         for(Channel c : this.nonServerChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.deregister());
            }
         }

         return new DefaultChannelGroupFuture(this, futures, this.executor);
      }
   }

   public ChannelGroup flush(ChannelMatcher matcher) {
      for(Channel c : this.nonServerChannels) {
         if(matcher.matches(c)) {
            c.flush();
         }
      }

      return this;
   }

   public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
      return this.writeAndFlush(message, matcher);
   }

   public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
      if(message == null) {
         throw new NullPointerException("message");
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap(this.size());

         for(Channel c : this.nonServerChannels) {
            if(matcher.matches(c)) {
               futures.put(c, c.writeAndFlush(safeDuplicate(message)));
            }
         }

         ReferenceCountUtil.release(message);
         return new DefaultChannelGroupFuture(this, futures, this.executor);
      }
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   public boolean equals(Object o) {
      return this == o;
   }

   public int compareTo(ChannelGroup o) {
      int v = this.name().compareTo(o.name());
      return v != 0?v:System.identityHashCode(this) - System.identityHashCode(o);
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(name: " + this.name() + ", size: " + this.size() + ')';
   }
}
