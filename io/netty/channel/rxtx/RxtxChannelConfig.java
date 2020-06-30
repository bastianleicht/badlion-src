package io.netty.channel.rxtx;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface RxtxChannelConfig extends ChannelConfig {
   RxtxChannelConfig setBaudrate(int var1);

   RxtxChannelConfig setStopbits(RxtxChannelConfig.Stopbits var1);

   RxtxChannelConfig setDatabits(RxtxChannelConfig.Databits var1);

   RxtxChannelConfig setParitybit(RxtxChannelConfig.Paritybit var1);

   int getBaudrate();

   RxtxChannelConfig.Stopbits getStopbits();

   RxtxChannelConfig.Databits getDatabits();

   RxtxChannelConfig.Paritybit getParitybit();

   boolean isDtr();

   RxtxChannelConfig setDtr(boolean var1);

   boolean isRts();

   RxtxChannelConfig setRts(boolean var1);

   int getWaitTimeMillis();

   RxtxChannelConfig setWaitTimeMillis(int var1);

   RxtxChannelConfig setReadTimeout(int var1);

   int getReadTimeout();

   RxtxChannelConfig setConnectTimeoutMillis(int var1);

   RxtxChannelConfig setMaxMessagesPerRead(int var1);

   RxtxChannelConfig setWriteSpinCount(int var1);

   RxtxChannelConfig setAllocator(ByteBufAllocator var1);

   RxtxChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   RxtxChannelConfig setAutoRead(boolean var1);

   RxtxChannelConfig setAutoClose(boolean var1);

   RxtxChannelConfig setWriteBufferHighWaterMark(int var1);

   RxtxChannelConfig setWriteBufferLowWaterMark(int var1);

   RxtxChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   public static enum Databits {
      DATABITS_5(5),
      DATABITS_6(6),
      DATABITS_7(7),
      DATABITS_8(8);

      private final int value;

      private Databits(int value) {
         this.value = value;
      }

      public int value() {
         return this.value;
      }

      public static RxtxChannelConfig.Databits valueOf(int value) {
         for(RxtxChannelConfig.Databits databit : values()) {
            if(databit.value == value) {
               return databit;
            }
         }

         throw new IllegalArgumentException("unknown " + RxtxChannelConfig.Databits.class.getSimpleName() + " value: " + value);
      }
   }

   public static enum Paritybit {
      NONE(0),
      ODD(1),
      EVEN(2),
      MARK(3),
      SPACE(4);

      private final int value;

      private Paritybit(int value) {
         this.value = value;
      }

      public int value() {
         return this.value;
      }

      public static RxtxChannelConfig.Paritybit valueOf(int value) {
         for(RxtxChannelConfig.Paritybit paritybit : values()) {
            if(paritybit.value == value) {
               return paritybit;
            }
         }

         throw new IllegalArgumentException("unknown " + RxtxChannelConfig.Paritybit.class.getSimpleName() + " value: " + value);
      }
   }

   public static enum Stopbits {
      STOPBITS_1(1),
      STOPBITS_2(2),
      STOPBITS_1_5(3);

      private final int value;

      private Stopbits(int value) {
         this.value = value;
      }

      public int value() {
         return this.value;
      }

      public static RxtxChannelConfig.Stopbits valueOf(int value) {
         for(RxtxChannelConfig.Stopbits stopbit : values()) {
            if(stopbit.value == value) {
               return stopbit;
            }
         }

         throw new IllegalArgumentException("unknown " + RxtxChannelConfig.Stopbits.class.getSimpleName() + " value: " + value);
      }
   }
}
