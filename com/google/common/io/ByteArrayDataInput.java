package com.google.common.io;

import java.io.DataInput;

public interface ByteArrayDataInput extends DataInput {
   void readFully(byte[] var1);

   void readFully(byte[] var1, int var2, int var3);

   int skipBytes(int var1);

   boolean readBoolean();

   byte readByte();

   int readUnsignedByte();

   short readShort();

   int readUnsignedShort();

   char readChar();

   int readInt();

   long readLong();

   float readFloat();

   double readDouble();

   String readLine();

   String readUTF();
}
