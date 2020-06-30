package com.ibm.icu.impl;

import com.ibm.icu.impl.Trie;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CharTrie extends Trie {
   private char m_initialValue_;
   private char[] m_data_;

   public CharTrie(InputStream inputStream, Trie.DataManipulate dataManipulate) throws IOException {
      super(inputStream, dataManipulate);
      if(!this.isCharTrie()) {
         throw new IllegalArgumentException("Data given does not belong to a char trie.");
      }
   }

   public CharTrie(int initialValue, int leadUnitValue, Trie.DataManipulate dataManipulate) {
      super(new char[2080], 512, dataManipulate);
      int latin1Length = 256;
      int dataLength = 256;
      if(leadUnitValue != initialValue) {
         dataLength += 32;
      }

      this.m_data_ = new char[dataLength];
      this.m_dataLength_ = dataLength;
      this.m_initialValue_ = (char)initialValue;

      for(int i = 0; i < latin1Length; ++i) {
         this.m_data_[i] = (char)initialValue;
      }

      if(leadUnitValue != initialValue) {
         char block = (char)(latin1Length >> 2);
         int var9 = 1728;

         for(int limit = 1760; var9 < limit; ++var9) {
            this.m_index_[var9] = block;
         }

         int var11 = latin1Length + 32;

         for(var9 = latin1Length; var9 < var11; ++var9) {
            this.m_data_[var9] = (char)leadUnitValue;
         }
      }

   }

   public final char getCodePointValue(int ch) {
      if(0 <= ch && ch < '\ud800') {
         int offset = (this.m_index_[ch >> 5] << 2) + (ch & 31);
         return this.m_data_[offset];
      } else {
         int offset = this.getCodePointOffset(ch);
         return offset >= 0?this.m_data_[offset]:this.m_initialValue_;
      }
   }

   public final char getLeadValue(char ch) {
      return this.m_data_[this.getLeadOffset(ch)];
   }

   public final char getBMPValue(char ch) {
      return this.m_data_[this.getBMPOffset(ch)];
   }

   public final char getSurrogateValue(char lead, char trail) {
      int offset = this.getSurrogateOffset(lead, trail);
      return offset > 0?this.m_data_[offset]:this.m_initialValue_;
   }

   public final char getTrailValue(int leadvalue, char trail) {
      if(this.m_dataManipulate_ == null) {
         throw new NullPointerException("The field DataManipulate in this Trie is null");
      } else {
         int offset = this.m_dataManipulate_.getFoldingOffset(leadvalue);
         return offset > 0?this.m_data_[this.getRawOffset(offset, (char)(trail & 1023))]:this.m_initialValue_;
      }
   }

   public final char getLatin1LinearValue(char ch) {
      return this.m_data_[32 + this.m_dataOffset_ + ch];
   }

   public boolean equals(Object other) {
      boolean result = super.equals(other);
      if(result && other instanceof CharTrie) {
         CharTrie othertrie = (CharTrie)other;
         return this.m_initialValue_ == othertrie.m_initialValue_;
      } else {
         return false;
      }
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 42;
   }

   protected final void unserialize(InputStream inputStream) throws IOException {
      DataInputStream input = new DataInputStream(inputStream);
      int indexDataLength = this.m_dataOffset_ + this.m_dataLength_;
      this.m_index_ = new char[indexDataLength];

      for(int i = 0; i < indexDataLength; ++i) {
         this.m_index_[i] = input.readChar();
      }

      this.m_data_ = this.m_index_;
      this.m_initialValue_ = this.m_data_[this.m_dataOffset_];
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
}
