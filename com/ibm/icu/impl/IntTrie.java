package com.ibm.icu.impl;

import com.ibm.icu.impl.Trie;
import com.ibm.icu.text.UTF16;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IntTrie extends Trie {
   private int m_initialValue_;
   private int[] m_data_;

   public IntTrie(InputStream inputStream, Trie.DataManipulate dataManipulate) throws IOException {
      super(inputStream, dataManipulate);
      if(!this.isIntTrie()) {
         throw new IllegalArgumentException("Data given does not belong to a int trie.");
      }
   }

   public IntTrie(int initialValue, int leadUnitValue, Trie.DataManipulate dataManipulate) {
      super(new char[2080], 512, dataManipulate);
      int latin1Length = 256;
      int dataLength = 256;
      if(leadUnitValue != initialValue) {
         dataLength += 32;
      }

      this.m_data_ = new int[dataLength];
      this.m_dataLength_ = dataLength;
      this.m_initialValue_ = initialValue;

      for(int i = 0; i < latin1Length; ++i) {
         this.m_data_[i] = initialValue;
      }

      if(leadUnitValue != initialValue) {
         char block = (char)(latin1Length >> 2);
         int var9 = 1728;

         for(int limit = 1760; var9 < limit; ++var9) {
            this.m_index_[var9] = block;
         }

         int var11 = latin1Length + 32;

         for(var9 = latin1Length; var9 < var11; ++var9) {
            this.m_data_[var9] = leadUnitValue;
         }
      }

   }

   public final int getCodePointValue(int ch) {
      if(0 <= ch && ch < '\ud800') {
         int offset = (this.m_index_[ch >> 5] << 2) + (ch & 31);
         return this.m_data_[offset];
      } else {
         int offset = this.getCodePointOffset(ch);
         return offset >= 0?this.m_data_[offset]:this.m_initialValue_;
      }
   }

   public final int getLeadValue(char ch) {
      return this.m_data_[this.getLeadOffset(ch)];
   }

   public final int getBMPValue(char ch) {
      return this.m_data_[this.getBMPOffset(ch)];
   }

   public final int getSurrogateValue(char lead, char trail) {
      if(UTF16.isLeadSurrogate(lead) && UTF16.isTrailSurrogate(trail)) {
         int offset = this.getSurrogateOffset(lead, trail);
         return offset > 0?this.m_data_[offset]:this.m_initialValue_;
      } else {
         throw new IllegalArgumentException("Argument characters do not form a supplementary character");
      }
   }

   public final int getTrailValue(int leadvalue, char trail) {
      if(this.m_dataManipulate_ == null) {
         throw new NullPointerException("The field DataManipulate in this Trie is null");
      } else {
         int offset = this.m_dataManipulate_.getFoldingOffset(leadvalue);
         return offset > 0?this.m_data_[this.getRawOffset(offset, (char)(trail & 1023))]:this.m_initialValue_;
      }
   }

   public final int getLatin1LinearValue(char ch) {
      return this.m_data_[32 + ch];
   }

   public boolean equals(Object other) {
      boolean result = super.equals(other);
      if(result && other instanceof IntTrie) {
         IntTrie othertrie = (IntTrie)other;
         return this.m_initialValue_ == othertrie.m_initialValue_ && Arrays.equals(this.m_data_, othertrie.m_data_);
      } else {
         return false;
      }
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 42;
   }

   protected final void unserialize(InputStream inputStream) throws IOException {
      super.unserialize(inputStream);
      this.m_data_ = new int[this.m_dataLength_];
      DataInputStream input = new DataInputStream(inputStream);

      for(int i = 0; i < this.m_dataLength_; ++i) {
         this.m_data_[i] = input.readInt();
      }

      this.m_initialValue_ = this.m_data_[0];
   }

   protected final int getSurrogateOffset(char lead, char trail) {
      if(this.m_dataManipulate_ == null) {
         throw new NullPointerException("The field DataManipulate in this Trie is null");
      } else {
         int offset = this.m_dataManipulate_.getFoldingOffset(this.getLeadValue(lead));
         return offset > 0?this.getRawOffset(offset, (char)(trail & 1023)):-1;
      }
   }

   protected final int getValue(int index) {
      return this.m_data_[index];
   }

   protected final int getInitialValue() {
      return this.m_initialValue_;
   }

   IntTrie(char[] index, int[] data, int initialvalue, int options, Trie.DataManipulate datamanipulate) {
      super(index, options, datamanipulate);
      this.m_data_ = data;
      this.m_dataLength_ = this.m_data_.length;
      this.m_initialValue_ = initialvalue;
   }
}
