package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioChannel;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

public abstract class AbstractOioByteChannel extends AbstractOioChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';
   private RecvByteBufAllocator.Handle allocHandle;
   private volatile boolean inputShutdown;

   protected AbstractOioByteChannel(Channel parent) {
      super(parent);
   }

   protected boolean isInputShutdown() {
      return this.inputShutdown;
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected boolean checkInputShutdown() {
      if(this.inputShutdown) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var2) {
            ;
         }

         return true;
      } else {
         return false;
      }
   }

   protected void doRead() {
      if(!this.checkInputShutdown()) {
         ChannelConfig config = this.config();
         ChannelPipeline pipeline = this.pipeline();
         RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
         if(allocHandle == null) {
            this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
         }

         ByteBuf byteBuf = allocHandle.allocate(this.alloc());
         boolean closed = false;
         boolean read = false;
         Throwable exception = null;
         int localReadAmount = 0;

         try {
            int totalReadAmount = 0;

            while(true) {
               localReadAmount = this.doReadBytes(byteBuf);
               if(localReadAmount > 0) {
                  read = true;
               } else if(localReadAmount < 0) {
                  closed = true;
               }

               int available = this.available();
               if(available <= 0) {
                  break;
               }

               if(!byteBuf.isWritable()) {
                  int capacity = byteBuf.capacity();
                  int maxCapacity = byteBuf.maxCapacity();
                  if(capacity == maxCapacity) {
                     if(read) {
                        read = false;
                        pipeline.fireChannelRead(byteBuf);
                        byteBuf = this.alloc().buffer();
                     }
                  } else {
                     int writerIndex = byteBuf.writerIndex();
                     if(writerIndex + available > maxCapacity) {
                        byteBuf.capacity(maxCapacity);
                     } else {
                        byteBuf.ensureWritable(available);
                     }
                  }
               }

               if(totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                  totalReadAmount = Integer.MAX_VALUE;
                  break;
               }

               totalReadAmount += localReadAmount;
               if(!config.isAutoRead()) {
                  break;
               }
            }

            allocHandle.record(totalReadAmount);
         } catch (Throwable var17) {
            exception = var17;
         } finally {
            if(read) {
               pipeline.fireChannelRead(byteBuf);
            } else {
               byteBuf.release();
            }

            pipeline.fireChannelReadComplete();
            if(exception != null) {
               if(exception instanceof IOException) {
                  closed = true;
                  this.pipeline().fireExceptionCaught(exception);
               } else {
                  pipeline.fireExceptionCaught(exception);
                  this.unsafe().close(this.voidPromise());
               }
            }

            if(closed) {
               this.inputShutdown = true;
               if(this.isOpen()) {
                  if(Boolean.TRUE.equals(this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                     pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                  } else {
                     this.unsafe().close(this.unsafe().voidPromise());
                  }
               }
            }

            if(localReadAmount == 0 && this.isActive()) {
               this.read();
            }

         }

      }
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while(true) {
         Object msg = in.current();
         if(msg == null) {
            return;
         }

         if(!(msg instanceof ByteBuf)) {
            if(msg instanceof FileRegion) {
               FileRegion region = (FileRegion)msg;
               long transfered = region.transfered();
               this.doWriteFileRegion(region);
               in.progress(region.transfered() - transfered);
               in.remove();
            } else {
               in.remove(new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg)));
            }
         } else {
            ByteBuf buf = (ByteBuf)msg;

            int newReadableBytes;
            for(int readableBytes = buf.readableBytes(); readableBytes > 0; readableBytes = newReadableBytes) {
               this.doWriteBytes(buf);
               newReadableBytes = buf.readableBytes();
               in.progress((long)(readableBytes - newReadableBytes));
            }

            in.remove();
         }
      }
   }

   protected final Object filterOutboundMessage(Object msg) throws Exception {
      if(!(msg instanceof ByteBuf) && !(msg instanceof FileRegion)) {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      } else {
         return msg;
      }
   }

   protected abstract int available();

   protected abstract int doReadBytes(ByteBuf var1) throws Exception;

   protected abstract void doWriteBytes(ByteBuf var1) throws Exception;

   protected abstract void doWriteFileRegion(FileRegion var1) throws Exception;
}
