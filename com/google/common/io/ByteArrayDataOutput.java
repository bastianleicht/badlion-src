package com.google.common.io;

import java.io.DataOutput;

public interface ByteArrayDataOutput extends DataOutput {
   void write(int var1);

   void write(byte[] var1);

   void write(byte[] var1, int var2, int var3);

   void writeBoolean(boolean var1);

   void writeByte(int var1);

   void writeShort(int var1);

   void writeChar(int var1);

   void writeInt(int var1);

   void writeLong(long var1);

   void writeFloat(float var1);

   void writeDouble(double var1);

   void writeChars(String var1);

   void writeUTF(String var1);

   /** @deprecated */
   @Deprecated
   void writeBytes(String var1);

   byte[] toByteArray();
}
