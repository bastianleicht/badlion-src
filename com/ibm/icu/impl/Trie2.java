package com.ibm.icu.impl;

import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.impl.Trie2_32;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Trie2 implements Iterable {
   private static Trie2.ValueMapper defaultValueMapper = new Trie2.ValueMapper() {
      public int map(int in) {
         return in;
      }
   };
   Trie2.UTrie2Header header;
   char[] index;
   int data16;
   int[] data32;
   int indexLength;
   int dataLength;
   int index2NullOffset;
   int initialValue;
   int errorValue;
   int highStart;
   int highValueIndex;
   int dataNullOffset;
   int fHash;
   static final int UTRIE2_OPTIONS_VALUE_BITS_MASK = 15;
   static final int UTRIE2_SHIFT_1 = 11;
   static final int UTRIE2_SHIFT_2 = 5;
   static final int UTRIE2_SHIFT_1_2 = 6;
   static final int UTRIE2_OMITTED_BMP_INDEX_1_LENGTH = 32;
   static final int UTRIE2_CP_PER_INDEX_1_ENTRY = 2048;
   static final int UTRIE2_INDEX_2_BLOCK_LENGTH = 64;
   static final int UTRIE2_INDEX_2_MASK = 63;
   static final int UTRIE2_DATA_BLOCK_LENGTH = 32;
   static final int UTRIE2_DATA_MASK = 31;
   static final int UTRIE2_INDEX_SHIFT = 2;
   static final int UTRIE2_DATA_GRANULARITY = 4;
   static final int UTRIE2_INDEX_2_OFFSET = 0;
   static final int UTRIE2_LSCP_INDEX_2_OFFSET = 2048;
   static final int UTRIE2_LSCP_INDEX_2_LENGTH = 32;
   static final int UTRIE2_INDEX_2_BMP_LENGTH = 2080;
   static final int UTRIE2_UTF8_2B_INDEX_2_OFFSET = 2080;
   static final int UTRIE2_UTF8_2B_INDEX_2_LENGTH = 32;
   static final int UTRIE2_INDEX_1_OFFSET = 2112;
   static final int UTRIE2_MAX_INDEX_1_LENGTH = 512;
   static final int UTRIE2_BAD_UTF8_DATA_OFFSET = 128;
   static final int UTRIE2_DATA_START_OFFSET = 192;
   static final int UNEWTRIE2_INDEX_GAP_OFFSET = 2080;
   static final int UNEWTRIE2_INDEX_GAP_LENGTH = 576;
   static final int UNEWTRIE2_MAX_INDEX_2_LENGTH = 35488;
   static final int UNEWTRIE2_INDEX_1_LENGTH = 544;
   static final int UNEWTRIE2_MAX_DATA_LENGTH = 1115264;

   public static Trie2 createFromSerialized(InputStream is) throws IOException {
      DataInputStream dis = new DataInputStream(is);
      boolean needByteSwap = false;
      Trie2.UTrie2Header header = new Trie2.UTrie2Header();
      header.signature = dis.readInt();
      switch(header.signature) {
      case 845771348:
         needByteSwap = true;
         header.signature = Integer.reverseBytes(header.signature);
         break;
      case 1416784178:
         needByteSwap = false;
         break;
      default:
         throw new IllegalArgumentException("Stream does not contain a serialized UTrie2");
      }

      header.options = swapShort(needByteSwap, dis.readUnsignedShort());
      header.indexLength = swapShort(needByteSwap, dis.readUnsignedShort());
      header.shiftedDataLength = swapShort(needByteSwap, dis.readUnsignedShort());
      header.index2NullOffset = swapShort(needByteSwap, dis.readUnsignedShort());
      header.dataNullOffset = swapShort(needByteSwap, dis.readUnsignedShort());
      header.shiftedHighStart = swapShort(needByteSwap, dis.readUnsignedShort());
      if((header.options & 15) > 1) {
         throw new IllegalArgumentException("UTrie2 serialized format error.");
      } else {
         Trie2.ValueWidth width;
         Trie2 This;
         if((header.options & 15) == 0) {
            width = Trie2.ValueWidth.BITS_16;
            This = new Trie2_16();
         } else {
            width = Trie2.ValueWidth.BITS_32;
            This = new Trie2_32();
         }

         This.header = header;
         This.indexLength = header.indexLength;
         This.dataLength = header.shiftedDataLength << 2;
         This.index2NullOffset = header.index2NullOffset;
         This.dataNullOffset = header.dataNullOffset;
         This.highStart = header.shiftedHighStart << 11;
         This.highValueIndex = This.dataLength - 4;
         if(width == Trie2.ValueWidth.BITS_16) {
            This.highValueIndex += This.indexLength;
         }

         int indexArraySize = This.indexLength;
         if(width == Trie2.ValueWidth.BITS_16) {
            indexArraySize += This.dataLength;
         }

         This.index = new char[indexArraySize];

         for(int i = 0; i < This.indexLength; ++i) {
            This.index[i] = swapChar(needByteSwap, dis.readChar());
         }

         if(width == Trie2.ValueWidth.BITS_16) {
            This.data16 = This.indexLength;

            for(int var9 = 0; var9 < This.dataLength; ++var9) {
               This.index[This.data16 + var9] = swapChar(needByteSwap, dis.readChar());
            }
         } else {
            This.data32 = new int[This.dataLength];

            for(int var10 = 0; var10 < This.dataLength; ++var10) {
               This.data32[var10] = swapInt(needByteSwap, dis.readInt());
            }
         }

         switch(width) {
         case BITS_16:
            This.data32 = null;
            This.initialValue = This.index[This.dataNullOffset];
            This.errorValue = This.index[This.data16 + 128];
            break;
         case BITS_32:
            This.data16 = 0;
            This.initialValue = This.data32[This.dataNullOffset];
            This.errorValue = This.data32[128];
            break;
         default:
            throw new IllegalArgumentException("UTrie2 serialized format error.");
         }

         return This;
      }
   }

   private static int swapShort(boolean needSwap, int value) {
      return needSwap?Short.reverseBytes((short)value) & '\uffff':value;
   }

   private static char swapChar(boolean needSwap, char value) {
      return needSwap?(char)Short.reverseBytes((short)value):value;
   }

   private static int swapInt(boolean needSwap, int value) {
      return needSwap?Integer.reverseBytes(value):value;
   }

   public static int getVersion(InputStream is, boolean littleEndianOk) throws IOException {
      if(!is.markSupported()) {
         throw new IllegalArgumentException("Input stream must support mark().");
      } else {
         is.mark(4);
         byte[] sig = new byte[4];
         int read = is.read(sig);
         is.reset();
         if(read != sig.length) {
            return 0;
         } else if(sig[0] == 84 && sig[1] == 114 && sig[2] == 105 && sig[3] == 101) {
            return 1;
         } else if(sig[0] == 84 && sig[1] == 114 && sig[2] == 105 && sig[3] == 50) {
            return 2;
         } else {
            if(littleEndianOk) {
               if(sig[0] == 101 && sig[1] == 105 && sig[2] == 114 && sig[3] == 84) {
                  return 1;
               }

               if(sig[0] == 50 && sig[1] == 105 && sig[2] == 114 && sig[3] == 84) {
                  return 2;
               }
            }

            return 0;
         }
      }
   }

   public abstract int get(int var1);

   public abstract int getFromU16SingleLead(char var1);

   public final boolean equals(Object other) {
      if(!(other instanceof Trie2)) {
         return false;
      } else {
         Trie2 OtherTrie = (Trie2)other;
         Iterator<Trie2.Range> otherIter = OtherTrie.iterator();

         for(Trie2.Range rangeFromThis : this) {
            if(!otherIter.hasNext()) {
               return false;
            }

            Trie2.Range rangeFromOther = (Trie2.Range)otherIter.next();
            if(!rangeFromThis.equals(rangeFromOther)) {
               return false;
            }
         }

         if(otherIter.hasNext()) {
            return false;
         } else if(this.errorValue == OtherTrie.errorValue && this.initialValue == OtherTrie.initialValue) {
            return true;
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      if(this.fHash == 0) {
         int hash = initHash();

         for(Trie2.Range r : this) {
            hash = hashInt(hash, r.hashCode());
         }

         if(hash == 0) {
            hash = 1;
         }

         this.fHash = hash;
      }

      return this.fHash;
   }

   public Iterator iterator() {
      return this.iterator(defaultValueMapper);
   }

   public Iterator iterator(Trie2.ValueMapper mapper) {
      return new Trie2.Trie2Iterator(mapper);
   }

   public Iterator iteratorForLeadSurrogate(char lead, Trie2.ValueMapper mapper) {
      return new Trie2.Trie2Iterator(lead, mapper);
   }

   public Iterator iteratorForLeadSurrogate(char lead) {
      return new Trie2.Trie2Iterator(lead, defaultValueMapper);
   }

   protected int serializeHeader(DataOutputStream dos) throws IOException {
      int bytesWritten = 0;
      dos.writeInt(this.header.signature);
      dos.writeShort(this.header.options);
      dos.writeShort(this.header.indexLength);
      dos.writeShort(this.header.shiftedDataLength);
      dos.writeShort(this.header.index2NullOffset);
      dos.writeShort(this.header.dataNullOffset);
      dos.writeShort(this.header.shiftedHighStart);
      bytesWritten = bytesWritten + 16;

      for(int i = 0; i < this.header.indexLength; ++i) {
         dos.writeChar(this.index[i]);
      }

      bytesWritten = bytesWritten + this.header.indexLength;
      return bytesWritten;
   }

   public Trie2.CharSequenceIterator charSequenceIterator(CharSequence text, int index) {
      return new Trie2.CharSequenceIterator(text, index);
   }

   int rangeEnd(int start, int limitp, int val) {
      int limit = Math.min(this.highStart, limitp);

      int c;
      for(c = start + 1; c < limit && this.get(c) == val; ++c) {
         ;
      }

      if(c >= this.highStart) {
         c = limitp;
      }

      return c - 1;
   }

   private static int initHash() {
      return -2128831035;
   }

   private static int hashByte(int h, int b) {
      h = h * 16777619;
      h = h ^ b;
      return h;
   }

   private static int hashUChar32(int h, int c) {
      h = hashByte(h, c & 255);
      h = hashByte(h, c >> 8 & 255);
      h = hashByte(h, c >> 16);
      return h;
   }

   private static int hashInt(int h, int i) {
      h = hashByte(h, i & 255);
      h = hashByte(h, i >> 8 & 255);
      h = hashByte(h, i >> 16 & 255);
      h = hashByte(h, i >> 24 & 255);
      return h;
   }

   public class CharSequenceIterator implements Iterator {
      private CharSequence text;
      private int textLength;
      private int index;
      private Trie2.CharSequenceValues fResults = new Trie2.CharSequenceValues();

      CharSequenceIterator(CharSequence t, int index) {
         this.text = t;
         this.textLength = this.text.length();
         this.set(index);
      }

      public void set(int i) {
         if(i >= 0 && i <= this.textLength) {
            this.index = i;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final boolean hasNext() {
         return this.index < this.textLength;
      }

      public final boolean hasPrevious() {
         return this.index > 0;
      }

      public Trie2.CharSequenceValues next() {
         int c = Character.codePointAt(this.text, this.index);
         int val = Trie2.this.get(c);
         this.fResults.index = this.index;
         this.fResults.codePoint = c;
         this.fResults.value = val;
         ++this.index;
         if(c >= 65536) {
            ++this.index;
         }

         return this.fResults;
      }

      public Trie2.CharSequenceValues previous() {
         int c = Character.codePointBefore(this.text, this.index);
         int val = Trie2.this.get(c);
         --this.index;
         if(c >= 65536) {
            --this.index;
         }

         this.fResults.index = this.index;
         this.fResults.codePoint = c;
         this.fResults.value = val;
         return this.fResults;
      }

      public void remove() {
         throw new UnsupportedOperationException("Trie2.CharSequenceIterator does not support remove().");
      }
   }

   public static class CharSequenceValues {
      public int index;
      public int codePoint;
      public int value;
   }

   public static class Range {
      public int startCodePoint;
      public int endCodePoint;
      public int value;
      public boolean leadSurrogate;

      public boolean equals(Object other) {
         if(other != null && other.getClass().equals(this.getClass())) {
            Trie2.Range tother = (Trie2.Range)other;
            return this.startCodePoint == tother.startCodePoint && this.endCodePoint == tother.endCodePoint && this.value == tother.value && this.leadSurrogate == tother.leadSurrogate;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int h = Trie2.initHash();
         h = Trie2.hashUChar32(h, this.startCodePoint);
         h = Trie2.hashUChar32(h, this.endCodePoint);
         h = Trie2.hashInt(h, this.value);
         h = Trie2.hashByte(h, this.leadSurrogate?1:0);
         return h;
      }
   }

   class Trie2Iterator implements Iterator {
      private Trie2.ValueMapper mapper;
      private Trie2.Range returnValue = new Trie2.Range();
      private int nextStart;
      private int limitCP;
      private boolean doingCodePoints = true;
      private boolean doLeadSurrogates = true;

      Trie2Iterator(Trie2.ValueMapper vm) {
         this.mapper = vm;
         this.nextStart = 0;
         this.limitCP = 1114112;
         this.doLeadSurrogates = true;
      }

      Trie2Iterator(char leadSurrogate, Trie2.ValueMapper vm) {
         if(leadSurrogate >= '\ud800' && leadSurrogate <= '\udbff') {
            this.mapper = vm;
            this.nextStart = leadSurrogate - 'ퟀ' << 10;
            this.limitCP = this.nextStart + 1024;
            this.doLeadSurrogates = false;
         } else {
            throw new IllegalArgumentException("Bad lead surrogate value.");
         }
      }

      public Trie2.Range next() {
         if(!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            if(this.nextStart >= this.limitCP) {
               this.doingCodePoints = false;
               this.nextStart = '\ud800';
            }

            int endOfRange = 0;
            int val = 0;
            int mappedVal = 0;
            if(this.doingCodePoints) {
               val = Trie2.this.get(this.nextStart);
               mappedVal = this.mapper.map(val);

               for(endOfRange = Trie2.this.rangeEnd(this.nextStart, this.limitCP, val); endOfRange < this.limitCP - 1; endOfRange = Trie2.this.rangeEnd(endOfRange + 1, this.limitCP, val)) {
                  val = Trie2.this.get(endOfRange + 1);
                  if(this.mapper.map(val) != mappedVal) {
                     break;
                  }
               }
            } else {
               val = Trie2.this.getFromU16SingleLead((char)this.nextStart);
               mappedVal = this.mapper.map(val);

               for(endOfRange = this.rangeEndLS((char)this.nextStart); endOfRange < '\udbff'; endOfRange = this.rangeEndLS((char)(endOfRange + 1))) {
                  val = Trie2.this.getFromU16SingleLead((char)(endOfRange + 1));
                  if(this.mapper.map(val) != mappedVal) {
                     break;
                  }
               }
            }

            this.returnValue.startCodePoint = this.nextStart;
            this.returnValue.endCodePoint = endOfRange;
            this.returnValue.value = mappedVal;
            this.returnValue.leadSurrogate = !this.doingCodePoints;
            this.nextStart = endOfRange + 1;
            return this.returnValue;
         }
      }

      public boolean hasNext() {
         return this.doingCodePoints && (this.doLeadSurrogates || this.nextStart < this.limitCP) || this.nextStart < '\udc00';
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      private int rangeEndLS(char startingLS) {
         if(startingLS >= '\udbff') {
            return '\udbff';
         } else {
            int val = Trie2.this.getFromU16SingleLead(startingLS);

            int c;
            for(c = startingLS + 1; c <= '\udbff' && Trie2.this.getFromU16SingleLead((char)c) == val; ++c) {
               ;
            }

            return c - 1;
         }
      }
   }

   static class UTrie2Header {
      int signature;
      int options;
      int indexLength;
      int shiftedDataLength;
      int index2NullOffset;
      int dataNullOffset;
      int shiftedHighStart;
   }

   public interface ValueMapper {
      int map(int var1);
   }

   static enum ValueWidth {
      BITS_16,
      BITS_32;
   }
}
