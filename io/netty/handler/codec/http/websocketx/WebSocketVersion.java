package io.netty.handler.codec.http.websocketx;

public enum WebSocketVersion {
   UNKNOWN,
   V00,
   V07,
   V08,
   V13;

   public String toHttpHeaderValue() {
      if(this == V00) {
         return "0";
      } else if(this == V07) {
         return "7";
      } else if(this == V08) {
         return "8";
      } else if(this == V13) {
         return "13";
      } else {
         throw new IllegalStateException("Unknown web socket version: " + this);
      }
   }
}
