package io.netty.channel.sctp;

import com.sun.nio.sctp.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.DefaultByteBufHolder;

public final class SctpMessage extends DefaultByteBufHolder {
   private final int streamIdentifier;
   private final int protocolIdentifier;
   private final MessageInfo msgInfo;

   public SctpMessage(int protocolIdentifier, int streamIdentifier, ByteBuf payloadBuffer) {
      super(payloadBuffer);
      this.protocolIdentifier = protocolIdentifier;
      this.streamIdentifier = streamIdentifier;
      this.msgInfo = null;
   }

   public SctpMessage(MessageInfo msgInfo, ByteBuf payloadBuffer) {
      super(payloadBuffer);
      if(msgInfo == null) {
         throw new NullPointerException("msgInfo");
      } else {
         this.msgInfo = msgInfo;
         this.streamIdentifier = msgInfo.streamNumber();
         this.protocolIdentifier = msgInfo.payloadProtocolID();
      }
   }

   public int streamIdentifier() {
      return this.streamIdentifier;
   }

   public int protocolIdentifier() {
      return this.protocolIdentifier;
   }

   public MessageInfo messageInfo() {
      return this.msgInfo;
   }

   public boolean isComplete() {
      return this.msgInfo != null?this.msgInfo.isComplete():true;
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         SctpMessage sctpFrame = (SctpMessage)o;
         return this.protocolIdentifier != sctpFrame.protocolIdentifier?false:(this.streamIdentifier != sctpFrame.streamIdentifier?false:this.content().equals(sctpFrame.content()));
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.streamIdentifier;
      result = 31 * result + this.protocolIdentifier;
      result = 31 * result + this.content().hashCode();
      return result;
   }

   public SctpMessage copy() {
      return this.msgInfo == null?new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.content().copy()):new SctpMessage(this.msgInfo, this.content().copy());
   }

   public SctpMessage duplicate() {
      return this.msgInfo == null?new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.content().duplicate()):new SctpMessage(this.msgInfo, this.content().copy());
   }

   public SctpMessage retain() {
      super.retain();
      return this;
   }

   public SctpMessage retain(int increment) {
      super.retain(increment);
      return this;
   }

   public String toString() {
      return this.refCnt() == 0?"SctpFrame{streamIdentifier=" + this.streamIdentifier + ", protocolIdentifier=" + this.protocolIdentifier + ", data=(FREED)}":"SctpFrame{streamIdentifier=" + this.streamIdentifier + ", protocolIdentifier=" + this.protocolIdentifier + ", data=" + ByteBufUtil.hexDump(this.content()) + '}';
   }
}
