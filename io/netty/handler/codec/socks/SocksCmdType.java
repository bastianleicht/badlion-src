package io.netty.handler.codec.socks;

public enum SocksCmdType {
   CONNECT((byte)1),
   BIND((byte)2),
   UDP((byte)3),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksCmdType(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksCmdType fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksCmdType valueOf(byte b) {
      for(SocksCmdType code : values()) {
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
