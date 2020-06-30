package io.netty.channel.rxtx;

import io.netty.channel.ChannelOption;

public final class RxtxChannelOption extends ChannelOption {
   public static final RxtxChannelOption BAUD_RATE = new RxtxChannelOption("BAUD_RATE");
   public static final RxtxChannelOption DTR = new RxtxChannelOption("DTR");
   public static final RxtxChannelOption RTS = new RxtxChannelOption("RTS");
   public static final RxtxChannelOption STOP_BITS = new RxtxChannelOption("STOP_BITS");
   public static final RxtxChannelOption DATA_BITS = new RxtxChannelOption("DATA_BITS");
   public static final RxtxChannelOption PARITY_BIT = new RxtxChannelOption("PARITY_BIT");
   public static final RxtxChannelOption WAIT_TIME = new RxtxChannelOption("WAIT_TIME");
   public static final RxtxChannelOption READ_TIMEOUT = new RxtxChannelOption("READ_TIMEOUT");

   private RxtxChannelOption(String name) {
      super(name);
   }
}
