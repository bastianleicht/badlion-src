package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.CompactObjectInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.StreamCorruptedException;

public class ObjectDecoderInputStream extends InputStream implements ObjectInput {
   private final DataInputStream in;
   private final int maxObjectSize;
   private final ClassResolver classResolver;

   public ObjectDecoderInputStream(InputStream in) {
      this(in, (ClassLoader)null);
   }

   public ObjectDecoderInputStream(InputStream in, ClassLoader classLoader) {
      this(in, classLoader, 1048576);
   }

   public ObjectDecoderInputStream(InputStream in, int maxObjectSize) {
      this(in, (ClassLoader)null, maxObjectSize);
   }

   public ObjectDecoderInputStream(InputStream in, ClassLoader classLoader, int maxObjectSize) {
      if(in == null) {
         throw new NullPointerException("in");
      } else if(maxObjectSize <= 0) {
         throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
      } else {
         if(in instanceof DataInputStream) {
            this.in = (DataInputStream)in;
         } else {
            this.in = new DataInputStream(in);
         }

         this.classResolver = ClassResolvers.weakCachingResolver(classLoader);
         this.maxObjectSize = maxObjectSize;
      }
   }

   public Object readObject() throws ClassNotFoundException, IOException {
      int dataLen = this.readInt();
      if(dataLen <= 0) {
         throw new StreamCorruptedException("invalid data length: " + dataLen);
      } else if(dataLen > this.maxObjectSize) {
         throw new StreamCorruptedException("data length too big: " + dataLen + " (max: " + this.maxObjectSize + ')');
      } else {
         return (new CompactObjectInputStream(this.in, this.classResolver)).readObject();
      }
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   public void mark(int readlimit) {
      this.in.mark(readlimit);
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public final int read(byte[] b, int off, int len) throws IOException {
      return this.in.read(b, off, len);
   }

   public final int read(byte[] b) throws IOException {
      return this.in.read(b);
   }

   public final boolean readBoolean() throws IOException {
      return this.in.readBoolean();
   }

   public final byte readByte() throws IOException {
      return this.in.readByte();
   }

   public final char readChar() throws IOException {
      return this.in.readChar();
   }

   public final double readDouble() throws IOException {
      return this.in.readDouble();
   }

   public final float readFloat() throws IOException {
      return this.in.readFloat();
   }

   public final void readFully(byte[] b, int off, int len) throws IOException {
      this.in.readFully(b, off, len);
   }

   public final void readFully(byte[] b) throws IOException {
      this.in.readFully(b);
   }

   public final int readInt() throws IOException {
      return this.in.readInt();
   }

   /** @deprecated */
   @Deprecated
   public final String readLine() throws IOException {
      return this.in.readLine();
   }

   public final long readLong() throws IOException {
      return this.in.readLong();
   }

   public final short readShort() throws IOException {
      return this.in.readShort();
   }

   public final int readUnsignedByte() throws IOException {
      return this.in.readUnsignedByte();
   }

   public final int readUnsignedShort() throws IOException {
      return this.in.readUnsignedShort();
   }

   public final String readUTF() throws IOException {
      return this.in.readUTF();
   }

   public void reset() throws IOException {
      this.in.reset();
   }

   public long skip(long n) throws IOException {
      return this.in.skip(n);
   }

   public final int skipBytes(int n) throws IOException {
      return this.in.skipBytes(n);
   }
}
