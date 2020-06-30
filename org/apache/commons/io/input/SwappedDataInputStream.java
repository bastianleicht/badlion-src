package org.apache.commons.io.input;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.input.ProxyInputStream;

public class SwappedDataInputStream extends ProxyInputStream implements DataInput {
   public SwappedDataInputStream(InputStream input) {
      super(input);
   }

   public boolean readBoolean() throws IOException, EOFException {
      return 0 != this.readByte();
   }

   public byte readByte() throws IOException, EOFException {
      return (byte)this.in.read();
   }

   public char readChar() throws IOException, EOFException {
      return (char)this.readShort();
   }

   public double readDouble() throws IOException, EOFException {
      return EndianUtils.readSwappedDouble(this.in);
   }

   public float readFloat() throws IOException, EOFException {
      return EndianUtils.readSwappedFloat(this.in);
   }

   public void readFully(byte[] data) throws IOException, EOFException {
      this.readFully(data, 0, data.length);
   }

   public void readFully(byte[] data, int offset, int length) throws IOException, EOFException {
      int count;
      for(int remaining = length; remaining > 0; remaining -= count) {
         int location = offset + length - remaining;
         count = this.read(data, location, remaining);
         if(-1 == count) {
            throw new EOFException();
         }
      }

   }

   public int readInt() throws IOException, EOFException {
      return EndianUtils.readSwappedInteger(this.in);
   }

   public String readLine() throws IOException, EOFException {
      throw new UnsupportedOperationException("Operation not supported: readLine()");
   }

   public long readLong() throws IOException, EOFException {
      return EndianUtils.readSwappedLong(this.in);
   }

   public short readShort() throws IOException, EOFException {
      return EndianUtils.readSwappedShort(this.in);
   }

   public int readUnsignedByte() throws IOException, EOFException {
      return this.in.read();
   }

   public int readUnsignedShort() throws IOException, EOFException {
      return EndianUtils.readSwappedUnsignedShort(this.in);
   }

   public String readUTF() throws IOException, EOFException {
      throw new UnsupportedOperationException("Operation not supported: readUTF()");
   }

   public int skipBytes(int count) throws IOException, EOFException {
      return (int)this.in.skip((long)count);
   }
}
