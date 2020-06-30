package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class CompatibleObjectEncoder extends MessageToByteEncoder {
   private static final AttributeKey OOS = AttributeKey.valueOf(CompatibleObjectEncoder.class.getName() + ".OOS");
   private final int resetInterval;
   private int writtenObjects;

   public CompatibleObjectEncoder() {
      this(16);
   }

   public CompatibleObjectEncoder(int resetInterval) {
      if(resetInterval < 0) {
         throw new IllegalArgumentException("resetInterval: " + resetInterval);
      } else {
         this.resetInterval = resetInterval;
      }
   }

   protected ObjectOutputStream newObjectOutputStream(OutputStream out) throws Exception {
      return new ObjectOutputStream(out);
   }

   protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
      Attribute<ObjectOutputStream> oosAttr = ctx.attr(OOS);
      ObjectOutputStream oos = (ObjectOutputStream)oosAttr.get();
      if(oos == null) {
         oos = this.newObjectOutputStream(new ByteBufOutputStream(out));
         ObjectOutputStream newOos = (ObjectOutputStream)oosAttr.setIfAbsent(oos);
         if(newOos != null) {
            oos = newOos;
         }
      }

      synchronized(oos) {
         if(this.resetInterval != 0) {
            ++this.writtenObjects;
            if(this.writtenObjects % this.resetInterval == 0) {
               oos.reset();
            }
         }

         oos.writeObject(msg);
         oos.flush();
      }
   }
}
