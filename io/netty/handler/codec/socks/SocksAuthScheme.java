package io.netty.handler.codec.socks;

public enum SocksAuthScheme {
   NO_AUTH((byte)0),
   AUTH_GSSAPI((byte)1),
   AUTH_PASSWORD((byte)2),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksAuthScheme(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksAuthScheme fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksAuthScheme valueOf(byte b) {
      for(SocksAuthScheme code : values()) {
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
