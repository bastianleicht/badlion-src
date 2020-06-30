package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ByteBufInputStream extends InputStream implements DataInput {
   private final ByteBuf buffer;
   private final int startIndex;
   private final int endIndex;
   private final StringBuilder lineBuf;

   public ByteBufInputStream(ByteBuf buffer) {
      this(buffer, buffer.readableBytes());
   }

   public ByteBufInputStream(ByteBuf buffer, int length) {
      this.lineBuf = new StringBuilder();
      if(buffer == null) {
         throw new NullPointerException("buffer");
      } else if(length < 0) {
         throw new IllegalArgumentException("length: " + length);
      } else if(length > buffer.readableBytes()) {
         throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + length + ", maximum is " + buffer.readableBytes());
      } else {
         this.buffer = buffer;
         this.startIndex = buffer.readerIndex();
         this.endIndex = this.startIndex + length;
         buffer.markReaderIndex();
      }
   }

   public int readBytes() {
      return this.buffer.readerIndex() - this.startIndex;
   }

   public int available() throws IOException {
      return this.endIndex - this.buffer.readerIndex();
   }

   public void mark(int readlimit) {
      this.buffer.markReaderIndex();
   }

   public boolean markSupported() {
      return true;
   }

   public int read() throws IOException {
      return !this.buffer.isReadable()?-1:this.buffer.readByte() & 255;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int available = this.available();
      if(available == 0) {
         return -1;
      } else {
         len = Math.min(available, len);
         this.buffer.readBytes(b, off, len);
         return len;
      }
   }

   public void reset() throws IOException {
      this.buffer.resetReaderIndex();
   }

   public long skip(long n) throws IOException {
      return n > 2147483647L?(long)this.skipBytes(Integer.MAX_VALUE):(long)this.skipBytes((int)n);
   }

   public boolean readBoolean() throws IOException {
      this.checkAvailable(1);
      return this.read() != 0;
   }

   public byte readByte() throws IOException {
      if(!this.buffer.isReadable()) {
         throw new EOFException();
      } else {
         return this.buffer.readByte();
      }
   }

   public char readChar() throws IOException {
      return (char)this.readShort();
   }

   public double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   public float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   public void readFully(byte[] b) throws IOException {
      this.readFully(b, 0, b.length);
   }

   public void readFully(byte[] b, int off, int len) throws IOException {
      this.checkAvailable(len);
      this.buffer.readBytes(b, off, len);
   }

   public int readInt() throws IOException {
      this.checkAvailable(4);
      return this.buffer.readInt();
   }

   public String readLine() throws IOException {
      this.lineBuf.setLength(0);

      while(this.buffer.isReadable()) {
         int c = this.buffer.readUnsignedByte();
         switch(c) {
         case 13:
            if(this.buffer.isReadable() && (char)this.buffer.getUnsignedByte(this.buffer.readerIndex()) == 10) {
               this.buffer.skipBytes(1);
            }
         case 10:
            return this.lineBuf.toString();
         default:
            this.lineBuf.append((char)c);
         }
      }

      return this.lineBuf.length() > 0?this.lineBuf.toString():null;
   }

   public long readLong() throws IOException {
      this.checkAvailable(8);
      return this.buffer.readLong();
   }

   public short readShort() throws IOException {
      this.checkAvailable(2);
      return this.buffer.readShort();
   }

   public String readUTF() throws IOException {
      return DataInputStream.readUTF(this);
   }

   public int readUnsignedByte() throws IOException {
      return this.readByte() & 255;
   }

   public int readUnsignedShort() throws IOException {
      return this.readShort() & '\uffff';
   }

   public int skipBytes(int n) throws IOException {
      int nBytes = Math.min(this.available(), n);
      this.buffer.skipBytes(nBytes);
      return nBytes;
   }

   private void checkAvailable(int fieldSize) throws IOException {
      if(fieldSize < 0) {
         throw new IndexOutOfBoundsException("fieldSize cannot be a negative number");
      } else if(fieldSize > this.available()) {
         throw new EOFException("fieldSize is too long! Length is " + fieldSize + ", but maximum is " + this.available());
      }
   }
}
