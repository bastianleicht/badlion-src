package io.netty.handler.codec.socks;

public enum SocksAddressType {
   IPv4((byte)1),
   DOMAIN((byte)3),
   IPv6((byte)4),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksAddressType(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksAddressType fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksAddressType valueOf(byte b) {
      for(SocksAddressType code : values()) {
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
