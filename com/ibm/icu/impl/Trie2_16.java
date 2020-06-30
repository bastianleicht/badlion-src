package com.ibm.icu.impl;

import com.ibm.icu.impl.Trie2;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Trie2_16 extends Trie2 {
   public static Trie2_16 createFromSerialized(InputStream is) throws IOException {
      return (Trie2_16)Trie2.createFromSerialized(is);
   }

   public final int get(int codePoint) {
      if(codePoint >= 0) {
         if(codePoint < '\ud800' || codePoint > '\udbff' && codePoint <= '\uffff') {
            int ix = this.index[codePoint >> 5];
            ix = (ix << 2) + (codePoint & 31);
            int value = this.index[ix];
            return value;
         }

         if(codePoint <= '\uffff') {
            int ix = this.index[2048 + (codePoint - '\ud800' >> 5)];
            ix = (ix << 2) + (codePoint & 31);
            int value = this.index[ix];
            return value;
         }

         if(codePoint < this.highStart) {
            int ix = 2080 + (codePoint >> 11);
            ix = this.index[ix];
            ix = ix + (codePoint >> 5 & 63);
            ix = this.index[ix];
            ix = (ix << 2) + (codePoint & 31);
            int value = this.index[ix];
            return value;
         }

         if(codePoint <= 1114111) {
            int value = this.index[this.highValueIndex];
            return value;
         }
      }

      return this.errorValue;
   }

   public int getFromU16SingleLead(char codeUnit) {
      int ix = this.index[codeUnit >> 5];
      ix = (ix << 2) + (codeUnit & 31);
      int value = this.index[ix];
      return value;
   }

   public int serialize(OutputStream os) throws IOException {
      DataOutputStream dos = new DataOutputStream(os);
      int bytesWritten = 0;
      bytesWritten = bytesWritten + this.serializeHeader(dos);

      for(int i = 0; i < this.dataLength; ++i) {
         dos.writeChar(this.index[this.data16 + i]);
      }

      bytesWritten = bytesWritten + this.dataLength * 2;
      return bytesWritten;
   }

   public int getSerializedLength() {
      return 16 + (this.header.indexLength + this.dataLength) * 2;
   }

   int rangeEnd(int startingCP, int limit, int value) {
      int cp = startingCP;
      int block = 0;
      int index2Block = 0;

      label18:
      while(cp < limit) {
         if(cp >= '\ud800' && (cp <= '\udbff' || cp > '\uffff')) {
            if(cp < '\uffff') {
               index2Block = 2048;
               block = this.index[index2Block + (cp - '\ud800' >> 5)] << 2;
            } else {
               if(cp >= this.highStart) {
                  if(value == this.index[this.highValueIndex]) {
                     cp = limit;
                  }
                  break;
               }

               int ix = 2080 + (cp >> 11);
               index2Block = this.index[ix];
               block = this.index[index2Block + (cp >> 5 & 63)] << 2;
            }
         } else {
            index2Block = 0;
            block = this.index[cp >> 5] << 2;
         }

         if(index2Block == this.index2NullOffset) {
            if(value != this.initialValue) {
               break;
            }

            cp += 2048;
         } else if(block == this.dataNullOffset) {
            if(value != this.initialValue) {
               break;
            }

            cp += 32;
         } else {
            int startIx = block + (cp & 31);
            int limitIx = block + 32;

            for(int ix = startIx; ix < limitIx; ++ix) {
               if(this.index[ix] != value) {
                  cp += ix - startIx;
                  break label18;
               }
            }

            cp += limitIx - startIx;
         }
      }

      if(cp > limit) {
         cp = limit;
      }

      return cp - 1;
   }
}
