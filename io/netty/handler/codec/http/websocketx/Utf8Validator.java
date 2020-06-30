package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.CorruptedFrameException;

final class Utf8Validator implements ByteBufProcessor {
   private static final int UTF8_ACCEPT = 0;
   private static final int UTF8_REJECT = 12;
   private static final byte[] TYPES = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)9, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)7, (byte)8, (byte)8, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)10, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)4, (byte)3, (byte)3, (byte)11, (byte)6, (byte)6, (byte)6, (byte)5, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8};
   private static final byte[] STATES = new byte[]{(byte)0, (byte)12, (byte)24, (byte)36, (byte)60, (byte)96, (byte)84, (byte)12, (byte)12, (byte)12, (byte)48, (byte)72, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)0, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)0, (byte)12, (byte)0, (byte)12, (byte)12, (byte)12, (byte)24, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)24, (byte)12, (byte)24, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)24, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)24, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)24, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)36, (byte)12, (byte)36, (byte)12, (byte)12, (byte)12, (byte)36, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)36, (byte)12, (byte)36, (byte)12, (byte)12, (byte)12, (byte)36, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12, (byte)12};
   private int state = 0;
   private int codep;
   private boolean checking;

   public void check(ByteBuf buffer) {
      this.checking = true;
      buffer.forEachByte(this);
   }

   public void finish() {
      this.checking = false;
      this.codep = 0;
      if(this.state != 0) {
         this.state = 0;
         throw new CorruptedFrameException("bytes are not UTF-8");
      }
   }

   public boolean process(byte b) throws Exception {
      byte type = TYPES[b & 255];
      this.codep = this.state != 0?b & 63 | this.codep << 6:255 >> type & b;
      this.state = STATES[this.state + type];
      if(this.state == 12) {
         this.checking = false;
         throw new CorruptedFrameException("bytes are not UTF-8");
      } else {
         return true;
      }
   }

   public boolean isChecking() {
      return this.checking;
   }
}
