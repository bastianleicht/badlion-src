package io.netty.handler.codec.socks;

public enum SocksSubnegotiationVersion {
   AUTH_PASSWORD((byte)1),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksSubnegotiationVersion(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksSubnegotiationVersion fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksSubnegotiationVersion valueOf(byte b) {
      for(SocksSubnegotiationVersion code : values()) {
         if(code.b == b) {
            return code;
         }
      }

      return UNKNOWN;
   }

   public byte byteValue() {
      return this.b;
   }
}
