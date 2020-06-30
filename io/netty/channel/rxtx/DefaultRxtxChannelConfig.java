package io.netty.channel.rxtx;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxChannelConfig;
import io.netty.channel.rxtx.RxtxChannelOption;
import java.util.Map;

final class DefaultRxtxChannelConfig extends DefaultChannelConfig implements RxtxChannelConfig {
   private volatile int baudrate = 115200;
   private volatile boolean dtr;
   private volatile boolean rts;
   private volatile RxtxChannelConfig.Stopbits stopbits = RxtxChannelConfig.Stopbits.STOPBITS_1;
   private volatile RxtxChannelConfig.Databits databits = RxtxChannelConfig.Databits.DATABITS_8;
   private volatile RxtxChannelConfig.Paritybit paritybit = RxtxChannelConfig.Paritybit.NONE;
   private volatile int waitTime;
   private volatile int readTimeout = 1000;

   DefaultRxtxChannelConfig(RxtxChannel channel) {
      super(channel);
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{RxtxChannelOption.BAUD_RATE, RxtxChannelOption.DTR, RxtxChannelOption.RTS, RxtxChannelOption.STOP_BITS, RxtxChannelOption.DATA_BITS, RxtxChannelOption.PARITY_BIT, RxtxChannelOption.WAIT_TIME});
   }

   public Object getOption(ChannelOption option) {
      return option == RxtxChannelOption.BAUD_RATE?Integer.valueOf(this.getBaudrate()):(option == RxtxChannelOption.DTR?Boolean.valueOf(this.isDtr()):(option == RxtxChannelOption.RTS?Boolean.valueOf(this.isRts()):(option == RxtxChannelOption.STOP_BITS?this.getStopbits():(option == RxtxChannelOption.DATA_BITS?this.getDatabits():(option == RxtxChannelOption.PARITY_BIT?this.getParitybit():(option == RxtxChannelOption.WAIT_TIME?Integer.valueOf(this.getWaitTimeMillis()):(option == RxtxChannelOption.READ_TIMEOUT?Integer.valueOf(this.getReadTimeout()):super.getOption(option))))))));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == RxtxChannelOption.BAUD_RATE) {
         this.setBaudrate(((Integer)value).intValue());
      } else if(option == RxtxChannelOption.DTR) {
         this.setDtr(((Boolean)value).booleanValue());
      } else if(option == RxtxChannelOption.RTS) {
         this.setRts(((Boolean)value).booleanValue());
      } else if(option == RxtxChannelOption.STOP_BITS) {
         this.setStopbits((RxtxChannelConfig.Stopbits)value);
      } else if(option == RxtxChannelOption.DATA_BITS) {
         this.setDatabits((RxtxChannelConfig.Databits)value);
      } else if(option == RxtxChannelOption.PARITY_BIT) {
         this.setParitybit((RxtxChannelConfig.Paritybit)value);
      } else if(option == RxtxChannelOption.WAIT_TIME) {
         this.setWaitTimeMillis(((Integer)value).intValue());
      } else {
         if(option != RxtxChannelOption.READ_TIMEOUT) {
            return super.setOption(option, value);
         }

         this.setReadTimeout(((Integer)value).intValue());
      }

      return true;
   }

   public RxtxChannelConfig setBaudrate(int baudrate) {
      this.baudrate = baudrate;
      return this;
   }

   public RxtxChannelConfig setStopbits(RxtxChannelConfig.Stopbits stopbits) {
      this.stopbits = stopbits;
      return this;
   }

   public RxtxChannelConfig setDatabits(RxtxChannelConfig.Databits databits) {
      this.databits = databits;
      return this;
   }

   public RxtxChannelConfig setParitybit(RxtxChannelConfig.Paritybit paritybit) {
      this.paritybit = paritybit;
      return this;
   }

   public int getBaudrate() {
      return this.baudrate;
   }

   public RxtxChannelConfig.Stopbits getStopbits() {
      return this.stopbits;
   }

   public RxtxChannelConfig.Databits getDatabits() {
      return this.databits;
   }

   public RxtxChannelConfig.Paritybit getParitybit() {
      return this.paritybit;
   }

   public boolean isDtr() {
      return this.dtr;
   }

   public RxtxChannelConfig setDtr(boolean dtr) {
      this.dtr = dtr;
      return this;
   }

   public boolean isRts() {
      return this.rts;
   }

   public RxtxChannelConfig setRts(boolean rts) {
      this.rts = rts;
      return this;
   }

   public int getWaitTimeMillis() {
      return this.waitTime;
   }

   public RxtxChannelConfig setWaitTimeMillis(int waitTimeMillis) {
      if(waitTimeMillis < 0) {
         throw new IllegalArgumentException("Wait time must be >= 0");
      } else {
         this.waitTime = waitTimeMillis;
         return this;
      }
   }

   public RxtxChannelConfig setReadTimeout(int readTimeout) {
      if(readTimeout < 0) {
         throw new IllegalArgumentException("readTime must be >= 0");
      } else {
         this.readTimeout = readTimeout;
         return this;
      }
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   public RxtxChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public RxtxChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public RxtxChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public RxtxChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public RxtxChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public RxtxChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public RxtxChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public RxtxChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public RxtxChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public RxtxChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
