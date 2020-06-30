package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public abstract class Trie {
   protected static final int LEAD_INDEX_OFFSET_ = 320;
   protected static final int INDEX_STAGE_1_SHIFT_ = 5;
   protected static final int INDEX_STAGE_2_SHIFT_ = 2;
   protected static final int DATA_BLOCK_LENGTH = 32;
   protected static final int INDEX_STAGE_3_MASK_ = 31;
   protected static final int SURROGATE_BLOCK_BITS = 5;
   protected static final int SURROGATE_BLOCK_COUNT = 32;
   protected static final int BMP_INDEX_LENGTH = 2048;
   protected static final int SURROGATE_MASK_ = 1023;
   protected char[] m_index_;
   protected Trie.DataManipulate m_dataManipulate_;
   protected int m_dataOffset_;
   protected int m_dataLength_;
   protected static final int HEADER_LENGTH_ = 16;
   protected static final int HEADER_OPTIONS_LATIN1_IS_LINEAR_MASK_ = 512;
   protected static final int HEADER_SIGNATURE_ = 1416784229;
   private static final int HEADER_OPTIONS_SHIFT_MASK_ = 15;
   protected static final int HEADER_OPTIONS_INDEX_SHIFT_ = 4;
   protected static final int HEADER_OPTIONS_DATA_IS_32_BIT_ = 256;
   private boolean m_isLatin1Linear_;
   private int m_options_;

   public final boolean isLatin1Linear() {
      return this.m_isLatin1Linear_;
   }

   public boolean equals(Object other) {
      if(other == this) {
         return true;
      } else if(!(other instanceof Trie)) {
         return false;
      } else {
         Trie othertrie = (Trie)other;
         return this.m_isLatin1Linear_ == othertrie.m_isLatin1Linear_ && this.m_options_ == othertrie.m_options_ && this.m_dataLength_ == othertrie.m_dataLength_ && Arrays.equals(this.m_index_, othertrie.m_index_);
      }
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 42;
   }

   public int getSerializedDataSize() {
      int result = 16;
      result = result + (this.m_dataOffset_ << 1);
      if(this.isCharTrie()) {
         result += this.m_dataLength_ << 1;
      } else if(this.isIntTrie()) {
         result += this.m_dataLength_ << 2;
      }

      return result;
   }

   protected Trie(InputStream inputStream, Trie.DataManipulate dataManipulate) throws IOException {
      DataInputStream input = new DataInputStream(inputStream);
      int signature = input.readInt();
      this.m_options_ = input.readInt();
      if(!this.checkHeader(signature)) {
         throw new IllegalArgumentException("ICU data file error: Trie header authentication failed, please check if you have the most updated ICU data file");
      } else {
         if(dataManipulate != null) {
            this.m_dataManipulate_ = dataManipulate;
         } else {
            this.m_dataManipulate_ = new Trie.DefaultGetFoldingOffset();
         }

         this.m_isLatin1Linear_ = (this.m_options_ & 512) != 0;
         this.m_dataOffset_ = input.readInt();
         this.m_dataLength_ = input.readInt();
         this.unserialize(inputStream);
      }
   }

   protected Trie(char[] index, int options, Trie.DataManipulate dataManipulate) {
      this.m_options_ = options;
      if(dataManipulate != null) {
         this.m_dataManipulate_ = dataManipulate;
      } else {
         this.m_dataManipulate_ = new Trie.DefaultGetFoldingOffset();
      }

      this.m_isLatin1Linear_ = (this.m_options_ & 512) != 0;
      this.m_index_ = index;
      this.m_dataOffset_ = this.m_index_.length;
   }

   protected abstract int getSurrogateOffset(char var1, char var2);

   protected abstract int getValue(int var1);

   protected abstract int getInitialValue();

   protected final int getRawOffset(int offset, char ch) {
      return (this.m_index_[offset + (ch >> 5)] << 2) + (ch & 31);
   }

   protected final int getBMPOffset(char ch) {
      return ch >= '\ud800' && ch <= '\udbff'?this.getRawOffset(320, ch):this.getRawOffset(0, ch);
   }

   protected final int getLeadOffset(char ch) {
      return this.getRawOffset(0, ch);
   }

   protected final int getCodePointOffset(int ch) {
      return ch < 0?-1:(ch < '\ud800'?this.getRawOffset(0, (char)ch):(ch < 65536?this.getBMPOffset((char)ch):(ch <= 1114111?this.getSurrogateOffset(UTF16.getLeadSurrogate(ch), (char)(ch & 1023)):-1)));
   }

   protected void unserialize(InputStream inputStream) throws IOException {
      this.m_index_ = new char[this.m_dataOffset_];
      DataInputStream input = new DataInputStream(inputStream);

      for(int i = 0; i < this.m_dataOffset_; ++i) {
         this.m_index_[i] = input.readChar();
      }

   }

   protected final boolean isIntTrie() {
      return (this.m_options_ & 256) != 0;
   }

   protected final boolean isCharTrie() {
      return (this.m_options_ & 256) == 0;
   }

   private final boolean checkHeader(int signature) {
      return signature != 1416784229?false:(this.m_options_ & 15) == 5 && (this.m_options_ >> 4 & 15) == 2;
   }

   public interface DataManipulate {
      int getFoldingOffset(int var1);
   }

   private static class DefaultGetFoldingOffset implements Trie.DataManipulate {
      private DefaultGetFoldingOffset() {
      }

      public int getFoldingOffset(int value) {
         return value;
      }
   }
}
