package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.UCharacterName;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

final class UCharacterNameReader implements ICUBinary.Authenticate {
   private DataInputStream m_dataInputStream_;
   private static final int GROUP_INFO_SIZE_ = 3;
   private int m_tokenstringindex_;
   private int m_groupindex_;
   private int m_groupstringindex_;
   private int m_algnamesindex_;
   private static final int ALG_INFO_SIZE_ = 12;
   private static final byte[] DATA_FORMAT_VERSION_ = new byte[]{(byte)1, (byte)0, (byte)0, (byte)0};
   private static final byte[] DATA_FORMAT_ID_ = new byte[]{(byte)117, (byte)110, (byte)97, (byte)109};

   public boolean isDataVersionAcceptable(byte[] version) {
      return version[0] == DATA_FORMAT_VERSION_[0];
   }

   protected UCharacterNameReader(InputStream inputStream) throws IOException {
      ICUBinary.readHeader(inputStream, DATA_FORMAT_ID_, this);
      this.m_dataInputStream_ = new DataInputStream(inputStream);
   }

   protected void read(UCharacterName data) throws IOException {
      this.m_tokenstringindex_ = this.m_dataInputStream_.readInt();
      this.m_groupindex_ = this.m_dataInputStream_.readInt();
      this.m_groupstringindex_ = this.m_dataInputStream_.readInt();
      this.m_algnamesindex_ = this.m_dataInputStream_.readInt();
      int count = this.m_dataInputStream_.readChar();
      char[] token = new char[count];

      for(char i = 0; i < count; ++i) {
         token[i] = this.m_dataInputStream_.readChar();
      }

      int size = this.m_groupindex_ - this.m_tokenstringindex_;
      byte[] tokenstr = new byte[size];
      this.m_dataInputStream_.readFully(tokenstr);
      data.setToken(token, tokenstr);
      count = this.m_dataInputStream_.readChar();
      data.setGroupCountSize(count, 3);
      count = count * 3;
      char[] group = new char[count];

      for(int i = 0; i < count; ++i) {
         group[i] = this.m_dataInputStream_.readChar();
      }

      size = this.m_algnamesindex_ - this.m_groupstringindex_;
      byte[] groupstring = new byte[size];
      this.m_dataInputStream_.readFully(groupstring);
      data.setGroup(group, groupstring);
      count = this.m_dataInputStream_.readInt();
      UCharacterName.AlgorithmName[] alg = new UCharacterName.AlgorithmName[count];

      for(int i = 0; i < count; ++i) {
         UCharacterName.AlgorithmName an = this.readAlg();
         if(an == null) {
            throw new IOException("unames.icu read error: Algorithmic names creation error");
         }

         alg[i] = an;
      }

      data.setAlgorithm(alg);
   }

   protected boolean authenticate(byte[] dataformatid, byte[] dataformatversion) {
      return Arrays.equals(DATA_FORMAT_ID_, dataformatid) && Arrays.equals(DATA_FORMAT_VERSION_, dataformatversion);
   }

   private UCharacterName.AlgorithmName readAlg() throws IOException {
      UCharacterName.AlgorithmName result = new UCharacterName.AlgorithmName();
      int rangestart = this.m_dataInputStream_.readInt();
      int rangeend = this.m_dataInputStream_.readInt();
      byte type = this.m_dataInputStream_.readByte();
      byte variant = this.m_dataInputStream_.readByte();
      if(!result.setInfo(rangestart, rangeend, type, variant)) {
         return null;
      } else {
         int size = this.m_dataInputStream_.readChar();
         if(type == 1) {
            char[] factor = new char[variant];

            for(int j = 0; j < variant; ++j) {
               factor[j] = this.m_dataInputStream_.readChar();
            }

            result.setFactor(factor);
            size -= variant << 1;
         }

         StringBuilder prefix = new StringBuilder();

         for(char c = (char)(this.m_dataInputStream_.readByte() & 255); c != 0; c = (char)(this.m_dataInputStream_.readByte() & 255)) {
            prefix.append(c);
         }

         result.setPrefix(prefix.toString());
         size = size - (12 + prefix.length() + 1);
         if(size > 0) {
            byte[] string = new byte[size];
            this.m_dataInputStream_.readFully(string);
            result.setFactorString(string);
         }

         return result;
      }
   }
}
