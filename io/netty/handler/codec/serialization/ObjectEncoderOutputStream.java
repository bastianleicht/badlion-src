package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.serialization.CompactObjectOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectEncoderOutputStream extends OutputStream implements ObjectOutput {
   private final DataOutputStream out;
   private final int estimatedLength;

   public ObjectEncoderOutputStream(OutputStream out) {
      this(out, 512);
   }

   public ObjectEncoderOutputStream(OutputStream out, int estimatedLength) {
      if(out == null) {
         throw new NullPointerException("out");
      } else if(estimatedLength < 0) {
         throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
      } else {
         if(out instanceof DataOutputStream) {
            this.out = (DataOutputStream)out;
         } else {
            this.out = new DataOutputStream(out);
         }

         this.estimatedLength = estimatedLength;
      }
   }

   public void writeObject(Object obj) throws IOException {
      ByteBufOutputStream bout = new ByteBufOutputStream(Unpooled.buffer(this.estimatedLength));
      ObjectOutputStream oout = new CompactObjectOutputStream(bout);
      oout.writeObject(obj);
      oout.flush();
      oout.close();
      ByteBuf buffer = bout.buffer();
      int objectSize = buffer.readableBytes();
      this.writeInt(objectSize);
      buffer.getBytes(0, (OutputStream)this, objectSize);
   }

   public void write(int b) throws IOException {
      this.out.write(b);
   }

   public void close() throws IOException {
      this.out.close();
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public final int size() {
      return this.out.size();
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.out.write(b, off, len);
   }

   public void write(byte[] b) throws IOException {
      this.out.write(b);
   }

   public final void writeBoolean(boolean v) throws IOException {
      this.out.writeBoolean(v);
   }

   public final void writeByte(int v) throws IOException {
      this.out.writeByte(v);
   }

   public final void writeBytes(String s) throws IOException {
      this.out.writeBytes(s);
   }

   public final void writeChar(int v) throws IOException {
      this.out.writeChar(v);
   }

   public final void writeChars(String s) throws IOException {
      this.out.writeChars(s);
   }

   public final void writeDouble(double v) throws IOException {
      this.out.writeDouble(v);
   }

   public final void writeFloat(float v) throws IOException {
      this.out.writeFloat(v);
   }

   public final void writeInt(int v) throws IOException {
      this.out.writeInt(v);
   }

   public final void writeLong(long v) throws IOException {
      this.out.writeLong(v);
   }

   public final void writeShort(int v) throws IOException {
      this.out.writeShort(v);
   }

   public final void writeUTF(String str) throws IOException {
      this.out.writeUTF(str);
   }
}
