package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUDebug;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class StringPrepDataReader implements ICUBinary.Authenticate {
   private static final boolean debug = ICUDebug.enabled("NormalizerDataReader");
   private DataInputStream dataInputStream;
   private byte[] unicodeVersion;
   private static final byte[] DATA_FORMAT_ID = new byte[]{(byte)83, (byte)80, (byte)82, (byte)80};
   private static final byte[] DATA_FORMAT_VERSION = new byte[]{(byte)3, (byte)2, (byte)5, (byte)2};

   public StringPrepDataReader(InputStream inputStream) throws IOException {
      if(debug) {
         System.out.println("Bytes in inputStream " + inputStream.available());
      }

      this.unicodeVersion = ICUBinary.readHeader(inputStream, DATA_FORMAT_ID, this);
      if(debug) {
         System.out.println("Bytes left in inputStream " + inputStream.available());
      }

      this.dataInputStream = new DataInputStream(inputStream);
      if(debug) {
         System.out.println("Bytes left in dataInputStream " + this.dataInputStream.available());
      }

   }

   public void read(byte[] idnaBytes, char[] mappingTable) throws IOException {
      this.dataInputStream.readFully(idnaBytes);

      for(int i = 0; i < mappingTable.length; ++i) {
         mappingTable[i] = this.dataInputStream.readChar();
      }

   }

   public byte[] getDataFormatVersion() {
      return DATA_FORMAT_VERSION;
   }

   public boolean isDataVersionAcceptable(byte[] version) {
      return version[0] == DATA_FORMAT_VERSION[0] && version[2] == DATA_FORMAT_VERSION[2] && version[3] == DATA_FORMAT_VERSION[3];
   }

   public int[] readIndexes(int length) throws IOException {
      int[] indexes = new int[length];

      for(int i = 0; i < length; ++i) {
         indexes[i] = this.dataInputStream.readInt();
      }

      return indexes;
   }

   public byte[] getUnicodeVersion() {
      return this.unicodeVersion;
   }
}
