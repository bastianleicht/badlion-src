package io.netty.handler.codec.spdy;

public class SpdySessionStatus implements Comparable {
   public static final SpdySessionStatus OK = new SpdySessionStatus(0, "OK");
   public static final SpdySessionStatus PROTOCOL_ERROR = new SpdySessionStatus(1, "PROTOCOL_ERROR");
   public static final SpdySessionStatus INTERNAL_ERROR = new SpdySessionStatus(2, "INTERNAL_ERROR");
   private final int code;
   private final String statusPhrase;

   public static SpdySessionStatus valueOf(int code) {
      switch(code) {
      case 0:
         return OK;
      case 1:
         return PROTOCOL_ERROR;
      case 2:
         return INTERNAL_ERROR;
      default:
         return new SpdySessionStatus(code, "UNKNOWN (" + code + ')');
      }
   }

   public SpdySessionStatus(int code, String statusPhrase) {
      if(statusPhrase == null) {
         throw new NullPointerException("statusPhrase");
      } else {
         this.code = code;
         this.statusPhrase = statusPhrase;
      }
   }

   public int code() {
      return this.code;
   }

   public String statusPhrase() {
      return this.statusPhrase;
   }

   public int hashCode() {
      return this.code();
   }

   public boolean equals(Object o) {
      return !(o instanceof SpdySessionStatus)?false:this.code() == ((SpdySessionStatus)o).code();
   }

   public String toString() {
      return this.statusPhrase();
   }

   public int compareTo(SpdySessionStatus o) {
      return this.code() - o.code();
   }
}
