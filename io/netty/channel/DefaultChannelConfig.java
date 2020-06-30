package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioByteChannel;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultChannelConfig implements ChannelConfig {
   private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = AdaptiveRecvByteBufAllocator.DEFAULT;
   private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
   private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
   protected final Channel channel;
   private volatile ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
   private volatile RecvByteBufAllocator rcvBufAllocator;
   private volatile MessageSizeEstimator msgSizeEstimator;
   private volatile int connectTimeoutMillis;
   private volatile int maxMessagesPerRead;
   private volatile int writeSpinCount;
   private volatile boolean autoRead;
   private volatile boolean autoClose;
   private volatile int writeBufferHighWaterMark;
   private volatile int writeBufferLowWaterMark;

   public DefaultChannelConfig(Channel channel) {
      this.rcvBufAllocator = DEFAULT_RCVBUF_ALLOCATOR;
      this.msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
      this.connectTimeoutMillis = 30000;
      this.writeSpinCount = 16;
      this.autoRead = true;
      this.autoClose = true;
      this.writeBufferHighWaterMark = 65536;
      this.writeBufferLowWaterMark = '耀';
      if(channel == null) {
         throw new NullPointerException("channel");
      } else {
         this.channel = channel;
         if(!(channel instanceof ServerChannel) && !(channel instanceof AbstractNioByteChannel)) {
            this.maxMessagesPerRead = 1;
         } else {
            this.maxMessagesPerRead = 16;
         }

      }
   }

   public Map getOptions() {
      return this.getOptions((Map)null, new ChannelOption[]{ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.AUTO_CLOSE, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR});
   }

   protected Map getOptions(Map result, ChannelOption... options) {
      if(result == null) {
         result = new IdentityHashMap();
      }

      for(ChannelOption<?> o : options) {
         ((Map)result).put(o, this.getOption(o));
      }

      return (Map)result;
   }

   public boolean setOptions(Map options) {
      if(options == null) {
         throw new NullPointerException("options");
      } else {
         boolean setAllOptions = true;

         for(Entry<ChannelOption<?>, ?> e : options.entrySet()) {
            if(!this.setOption((ChannelOption)e.getKey(), e.getValue())) {
               setAllOptions = false;
            }
         }

         return setAllOptions;
      }
   }

   public Object getOption(ChannelOption option) {
      if(option == null) {
         throw new NullPointerException("option");
      } else {
         return option == ChannelOption.CONNECT_TIMEOUT_MILLIS?Integer.valueOf(this.getConnectTimeoutMillis()):(option == ChannelOption.MAX_MESSAGES_PER_READ?Integer.valueOf(this.getMaxMessagesPerRead()):(option == ChannelOption.WRITE_SPIN_COUNT?Integer.valueOf(this.getWriteSpinCount()):(option == ChannelOption.ALLOCATOR?this.getAllocator():(option == ChannelOption.RCVBUF_ALLOCATOR?this.getRecvByteBufAllocator():(option == ChannelOption.AUTO_READ?Boolean.valueOf(this.isAutoRead()):(option == ChannelOption.AUTO_CLOSE?Boolean.valueOf(this.isAutoClose()):(option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK?Integer.valueOf(this.getWriteBufferHighWaterMark()):(option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK?Integer.valueOf(this.getWriteBufferLowWaterMark()):(option == ChannelOption.MESSAGE_SIZE_ESTIMATOR?this.getMessageSizeEstimator():null)))))))));
      }
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
         this.setConnectTimeoutMillis(((Integer)value).intValue());
      } else if(option == ChannelOption.MAX_MESSAGES_PER_READ) {
         this.setMaxMessagesPerRead(((Integer)value).intValue());
      } else if(option == ChannelOption.WRITE_SPIN_COUNT) {
         this.setWriteSpinCount(((Integer)value).intValue());
      } else if(option == ChannelOption.ALLOCATOR) {
         this.setAllocator((ByteBufAllocator)value);
      } else if(option == ChannelOption.RCVBUF_ALLOCATOR) {
         this.setRecvByteBufAllocator((RecvByteBufAllocator)value);
      } else if(option == ChannelOption.AUTO_READ) {
         this.setAutoRead(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.AUTO_CLOSE) {
         this.setAutoClose(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
         this.setWriteBufferHighWaterMark(((Integer)value).intValue());
      } else if(option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
         this.setWriteBufferLowWaterMark(((Integer)value).intValue());
      } else {
         if(option != ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            return false;
         }

         this.setMessageSizeEstimator((MessageSizeEstimator)value);
      }

      return true;
   }

   protected void validate(ChannelOption option, Object value) {
      if(option == null) {
         throw new NullPointerException("option");
      } else {
         option.validate(value);
      }
   }

   public int getConnectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      if(connectTimeoutMillis < 0) {
         throw new IllegalArgumentException(String.format("connectTimeoutMillis: %d (expected: >= 0)", new Object[]{Integer.valueOf(connectTimeoutMillis)}));
      } else {
         this.connectTimeoutMillis = connectTimeoutMillis;
         return this;
      }
   }

   public int getMaxMessagesPerRead() {
      return this.maxMessagesPerRead;
   }

   public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      if(maxMessagesPerRead <= 0) {
         throw new IllegalArgumentException("maxMessagesPerRead: " + maxMessagesPerRead + " (expected: > 0)");
      } else {
         this.maxMessagesPerRead = maxMessagesPerRead;
         return this;
      }
   }

   public int getWriteSpinCount() {
      return this.writeSpinCount;
   }

   public ChannelConfig setWriteSpinCount(int writeSpinCount) {
      if(writeSpinCount <= 0) {
         throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
      } else {
         this.writeSpinCount = writeSpinCount;
         return this;
      }
   }

   public ByteBufAllocator getAllocator() {
      return this.allocator;
   }

   public ChannelConfig setAllocator(ByteBufAllocator allocator) {
      if(allocator == null) {
         throw new NullPointerException("allocator");
      } else {
         this.allocator = allocator;
         return this;
      }
   }

   public RecvByteBufAllocator getRecvByteBufAllocator() {
      return this.rcvBufAllocator;
   }

   public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      if(allocator == null) {
         throw new NullPointerException("allocator");
      } else {
         this.rcvBufAllocator = allocator;
         return this;
      }
   }

   public boolean isAutoRead() {
      return this.autoRead;
   }

   public ChannelConfig setAutoRead(boolean autoRead) {
      boolean oldAutoRead = this.autoRead;
      this.autoRead = autoRead;
      if(autoRead && !oldAutoRead) {
         this.channel.read();
      } else if(!autoRead && oldAutoRead) {
         this.autoReadCleared();
      }

      return this;
   }

   protected void autoReadCleared() {
   }

   public boolean isAutoClose() {
      return this.autoClose;
   }

   public ChannelConfig setAutoClose(boolean autoClose) {
      this.autoClose = autoClose;
      return this;
   }

   public int getWriteBufferHighWaterMark() {
      return this.writeBufferHighWaterMark;
   }

   public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      if(writeBufferHighWaterMark < this.getWriteBufferLowWaterMark()) {
         throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + this.getWriteBufferLowWaterMark() + "): " + writeBufferHighWaterMark);
      } else if(writeBufferHighWaterMark < 0) {
         throw new IllegalArgumentException("writeBufferHighWaterMark must be >= 0");
      } else {
         this.writeBufferHighWaterMark = writeBufferHighWaterMark;
         return this;
      }
   }

   public int getWriteBufferLowWaterMark() {
      return this.writeBufferLowWaterMark;
   }

   public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      if(writeBufferLowWaterMark > this.getWriteBufferHighWaterMark()) {
         throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + this.getWriteBufferHighWaterMark() + "): " + writeBufferLowWaterMark);
      } else if(writeBufferLowWaterMark < 0) {
         throw new IllegalArgumentException("writeBufferLowWaterMark must be >= 0");
      } else {
         this.writeBufferLowWaterMark = writeBufferLowWaterMark;
         return this;
      }
   }

   public MessageSizeEstimator getMessageSizeEstimator() {
      return this.msgSizeEstimator;
   }

   public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      if(estimator == null) {
         throw new NullPointerException("estimator");
      } else {
         this.msgSizeEstimator = estimator;
         return this;
      }
   }
}
