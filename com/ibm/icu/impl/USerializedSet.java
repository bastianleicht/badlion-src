package com.ibm.icu.impl;

public final class USerializedSet {
   private char[] array = new char[8];
   private int arrayOffset;
   private int bmpLength;
   private int length;

   public final boolean getSet(char[] src, int srcStart) {
      this.array = null;
      this.arrayOffset = this.bmpLength = this.length = 0;
      this.length = src[srcStart++];
      if((this.length & '耀') > 0) {
         this.length &= 32767;
         if(src.length < srcStart + 1 + this.length) {
            this.length = 0;
            throw new IndexOutOfBoundsException();
         }

         this.bmpLength = src[srcStart++];
      } else {
         if(src.length < srcStart + this.length) {
            this.length = 0;
            throw new IndexOutOfBoundsException();
         }

         this.bmpLength = this.length;
      }

      this.array = new char[this.length];
      System.arraycopy(src, srcStart, this.array, 0, this.length);
      return true;
   }

   public final void setToOne(int c) {
      if(1114111 >= c) {
         if(c < '\uffff') {
            this.bmpLength = this.length = 2;
            this.array[0] = (char)c;
            this.array[1] = (char)(c + 1);
         } else if(c == '\uffff') {
            this.bmpLength = 1;
            this.length = 3;
            this.array[0] = '\uffff';
            this.array[1] = 1;
            this.array[2] = 0;
         } else if(c < 1114111) {
            this.bmpLength = 0;
            this.length = 4;
            this.array[0] = (char)(c >> 16);
            this.array[1] = (char)c;
            ++c;
            this.array[2] = (char)(c >> 16);
            this.array[3] = (char)c;
         } else {
            this.bmpLength = 0;
            this.length = 2;
            this.array[0] = 16;
            this.array[1] = '\uffff';
         }

      }
   }

   public final boolean getRange(int rangeIndex, int[] range) {
      if(rangeIndex < 0) {
         return false;
      } else {
         if(this.array == null) {
            this.array = new char[8];
         }

         if(range != null && range.length >= 2) {
            rangeIndex = rangeIndex * 2;
            if(rangeIndex < this.bmpLength) {
               range[0] = this.array[rangeIndex++];
               if(rangeIndex < this.bmpLength) {
                  range[1] = this.array[rangeIndex] - 1;
               } else if(rangeIndex < this.length) {
                  range[1] = (this.array[rangeIndex] << 16 | this.array[rangeIndex + 1]) - 1;
               } else {
                  range[1] = 1114111;
               }

               return true;
            } else {
               rangeIndex = rangeIndex - this.bmpLength;
               rangeIndex = rangeIndex * 2;
               int suppLength = this.length - this.bmpLength;
               if(rangeIndex < suppLength) {
                  int offset = this.arrayOffset + this.bmpLength;
                  range[0] = this.array[offset + rangeIndex] << 16 | this.array[offset + rangeIndex + 1];
                  rangeIndex = rangeIndex + 2;
                  if(rangeIndex < suppLength) {
                     range[1] = (this.array[offset + rangeIndex] << 16 | this.array[offset + rangeIndex + 1]) - 1;
                  } else {
                     range[1] = 1114111;
                  }

                  return true;
               } else {
                  return false;
               }
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public final boolean contains(int c) {
      if(c > 1114111) {
         return false;
      } else if(c <= '\uffff') {
         int i;
         for(i = 0; i < this.bmpLength && (char)c >= this.array[i]; ++i) {
            ;
         }

         return (i & 1) != 0;
      } else {
         char high = (char)(c >> 16);
         char low = (char)c;

         int i;
         for(i = this.bmpLength; i < this.length && (high > this.array[i] || high == this.array[i] && low >= this.array[i + 1]); i += 2) {
            ;
         }

         return (i + this.bmpLength & 2) != 0;
      }
   }

   public final int countRanges() {
      return (this.bmpLength + (this.length - this.bmpLength) / 2 + 1) / 2;
   }
}
