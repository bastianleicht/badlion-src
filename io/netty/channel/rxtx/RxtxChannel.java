package io.netty.channel.rxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.OioByteStreamChannel;
import io.netty.channel.rxtx.DefaultRxtxChannelConfig;
import io.netty.channel.rxtx.RxtxChannelConfig;
import io.netty.channel.rxtx.RxtxChannelOption;
import io.netty.channel.rxtx.RxtxDeviceAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class RxtxChannel extends OioByteStreamChannel {
   private static final RxtxDeviceAddress LOCAL_ADDRESS = new RxtxDeviceAddress("localhost");
   private final RxtxChannelConfig config = new DefaultRxtxChannelConfig(this);
   private boolean open = true;
   private RxtxDeviceAddress deviceAddress;
   private SerialPort serialPort;

   public RxtxChannel() {
      super((Channel)null);
   }

   public RxtxChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return this.open;
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new RxtxChannel.RxtxUnsafe();
   }

   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      RxtxDeviceAddress remote = (RxtxDeviceAddress)remoteAddress;
      CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(remote.value());
      CommPort commPort = cpi.open(this.getClass().getName(), 1000);
      commPort.enableReceiveTimeout(((Integer)this.config().getOption(RxtxChannelOption.READ_TIMEOUT)).intValue());
      this.deviceAddress = remote;
      this.serialPort = (SerialPort)commPort;
   }

   protected void doInit() throws Exception {
      this.serialPort.setSerialPortParams(((Integer)this.config().getOption(RxtxChannelOption.BAUD_RATE)).intValue(), ((RxtxChannelConfig.Databits)this.config().getOption(RxtxChannelOption.DATA_BITS)).value(), ((RxtxChannelConfig.Stopbits)this.config().getOption(RxtxChannelOption.STOP_BITS)).value(), ((RxtxChannelConfig.Paritybit)this.config().getOption(RxtxChannelOption.PARITY_BIT)).value());
      this.serialPort.setDTR(((Boolean)this.config().getOption(RxtxChannelOption.DTR)).booleanValue());
      this.serialPort.setRTS(((Boolean)this.config().getOption(RxtxChannelOption.RTS)).booleanValue());
      this.activate(this.serialPort.getInputStream(), this.serialPort.getOutputStream());
   }

   public RxtxDeviceAddress localAddress() {
      return (RxtxDeviceAddress)super.localAddress();
   }

   public RxtxDeviceAddress remoteAddress() {
      return (RxtxDeviceAddress)super.remoteAddress();
   }

   protected RxtxDeviceAddress localAddress0() {
      return LOCAL_ADDRESS;
   }

   protected RxtxDeviceAddress remoteAddress0() {
      return this.deviceAddress;
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      this.open = false;

      try {
         super.doClose();
      } finally {
         if(this.serialPort != null) {
            this.serialPort.removeEventListener();
            this.serialPort.close();
            this.serialPort = null;
         }

      }

   }

   private final class RxtxUnsafe extends AbstractChannel.AbstractUnsafe {
      private RxtxUnsafe() {
         super();
      }

      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, final ChannelPromise promise) {
         if(promise.setUncancellable() && this.ensureOpen(promise)) {
            try {
               final boolean wasActive = RxtxChannel.this.isActive();
               RxtxChannel.this.doConnect(remoteAddress, localAddress);
               int waitTime = ((Integer)RxtxChannel.this.config().getOption(RxtxChannelOption.WAIT_TIME)).intValue();
               if(waitTime > 0) {
                  RxtxChannel.this.eventLoop().schedule(new Runnable() {
                     public void run() {
                        try {
                           RxtxChannel.this.doInit();
                           RxtxUnsafe.this.safeSetSuccess(promise);
                           if(!wasActive && RxtxChannel.this.isActive()) {
                              RxtxChannel.this.pipeline().fireChannelActive();
                           }
                        } catch (Throwable var2) {
                           RxtxUnsafe.this.safeSetFailure(promise, var2);
                           RxtxUnsafe.this.closeIfClosed();
                        }

                     }
                  }, (long)waitTime, TimeUnit.MILLISECONDS);
               } else {
                  RxtxChannel.this.doInit();
                  this.safeSetSuccess(promise);
                  if(!wasActive && RxtxChannel.this.isActive()) {
                     RxtxChannel.this.pipeline().fireChannelActive();
                  }
               }
            } catch (Throwable var6) {
               this.safeSetFailure(promise, var6);
               this.closeIfClosed();
            }

         }
      }
   }
}
