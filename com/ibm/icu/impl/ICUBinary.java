package com.ibm.icu.impl;

import com.ibm.icu.util.VersionInfo;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class ICUBinary {
   private static final byte MAGIC1 = -38;
   private static final byte MAGIC2 = 39;
   private static final byte BIG_ENDIAN_ = 1;
   private static final byte CHAR_SET_ = 0;
   private static final byte CHAR_SIZE_ = 2;
   private static final String MAGIC_NUMBER_AUTHENTICATION_FAILED_ = "ICU data file error: Not an ICU data file";
   private static final String HEADER_AUTHENTICATION_FAILED_ = "ICU data file error: Header authentication failed, please check if you have a valid ICU data file";

   public static final byte[] readHeader(InputStream inputStream, byte[] dataFormatIDExpected, ICUBinary.Authenticate authenticate) throws IOException {
      DataInputStream input = new DataInputStream(inputStream);
      char headersize = input.readChar();
      int readcount = 2;
      byte magic1 = input.readByte();
      ++readcount;
      byte magic2 = input.readByte();
      ++readcount;
      if(magic1 == -38 && magic2 == 39) {
         input.readChar();
         readcount = readcount + 2;
         input.readChar();
         readcount = readcount + 2;
         byte bigendian = input.readByte();
         ++readcount;
         byte charset = input.readByte();
         ++readcount;
         byte charsize = input.readByte();
         ++readcount;
         input.readByte();
         ++readcount;
         byte[] dataFormatID = new byte[4];
         input.readFully(dataFormatID);
         readcount = readcount + 4;
         byte[] dataVersion = new byte[4];
         input.readFully(dataVersion);
         readcount = readcount + 4;
         byte[] unicodeVersion = new byte[4];
         input.readFully(unicodeVersion);
         readcount = readcount + 4;
         if(headersize < readcount) {
            throw new IOException("Internal Error: Header size error");
         } else {
            input.skipBytes(headersize - readcount);
            if(bigendian == 1 && charset == 0 && charsize == 2 && Arrays.equals(dataFormatIDExpected, dataFormatID) && (authenticate == null || authenticate.isDataVersionAcceptable(dataVersion))) {
               return unicodeVersion;
            } else {
               throw new IOException("ICU data file error: Header authentication failed, please check if you have a valid ICU data file");
            }
         }
      } else {
         throw new IOException("ICU data file error: Not an ICU data file");
      }
   }

   public static final VersionInfo readHeaderAndDataVersion(InputStream inputStream, byte[] dataFormatIDExpected, ICUBinary.Authenticate authenticate) throws IOException {
      byte[] dataVersion = readHeader(inputStream, dataFormatIDExpected, authenticate);
      return VersionInfo.getInstance(dataVersion[0], dataVersion[1], dataVersion[2], dataVersion[3]);
   }

   public interface Authenticate {
      boolean isDataVersionAcceptable(byte[] var1);
   }
}
