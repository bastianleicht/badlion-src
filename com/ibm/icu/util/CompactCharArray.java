package com.ibm.icu.util;

import com.ibm.icu.impl.Utility;

/** @deprecated */
public final class CompactCharArray implements Cloneable {
   /** @deprecated */
   public static final int UNICODECOUNT = 65536;
   /** @deprecated */
   public static final int BLOCKSHIFT = 5;
   static final int BLOCKCOUNT = 32;
   static final int INDEXSHIFT = 11;
   static final int INDEXCOUNT = 2048;
   static final int BLOCKMASK = 31;
   private char[] values;
   private char[] indices;
   private int[] hashes;
   private boolean isCompact;
   char defaultValue;

   /** @deprecated */
   public CompactCharArray() {
      this('\u0000');
   }

   /** @deprecated */
   public CompactCharArray(char defaultValue) {
      this.values = new char[65536];
      this.indices = new char[2048];
      this.hashes = new int[2048];

      for(int i = 0; i < 65536; ++i) {
         this.values[i] = defaultValue;
      }

      for(int var3 = 0; var3 < 2048; ++var3) {
         this.indices[var3] = (char)(var3 << 5);
         this.hashes[var3] = 0;
      }

      this.isCompact = false;
      this.defaultValue = defaultValue;
   }

   /** @deprecated */
   public CompactCharArray(char[] indexArray, char[] newValues) {
      if(indexArray.length != 2048) {
         throw new IllegalArgumentException("Index out of bounds.");
      } else {
         for(int i = 0; i < 2048; ++i) {
            char index = indexArray[i];
            if(index < 0 || index >= newValues.length + 32) {
               throw new IllegalArgumentException("Index out of bounds.");
            }
         }

         this.indices = indexArray;
         this.values = newValues;
         this.isCompact = true;
      }
   }

   /** @deprecated */
   public CompactCharArray(String indexArray, String valueArray) {
      this(Utility.RLEStringToCharArray(indexArray), Utility.RLEStringToCharArray(valueArray));
   }

   /** @deprecated */
   public char elementAt(char index) {
      int ix = (this.indices[index >> 5] & '\uffff') + (index & 31);
      return ix >= this.values.length?this.defaultValue:this.values[ix];
   }

   /** @deprecated */
   public void setElementAt(char index, char value) {
      if(this.isCompact) {
         this.expand();
      }

      this.values[index] = value;
      this.touchBlock(index >> 5, value);
   }

   /** @deprecated */
   public void setElementAt(char start, char end, char value) {
      if(this.isCompact) {
         this.expand();
      }

      for(int i = start; i <= end; ++i) {
         this.values[i] = value;
         this.touchBlock(i >> 5, value);
      }

   }

   /** @deprecated */
   public void compact() {
      this.compact(true);
   }

   /** @deprecated */
   public void compact(boolean exhaustive) {
      if(!this.isCompact) {
         int iBlockStart = 0;
         char iUntouched = '\uffff';
         int newSize = 0;
         char[] target = exhaustive?new char[65536]:this.values;

         for(int i = 0; i < this.indices.length; iBlockStart += 32) {
            this.indices[i] = '\uffff';
            boolean touched = this.blockTouched(i);
            if(!touched && iUntouched != '\uffff') {
               this.indices[i] = iUntouched;
            } else {
               int jBlockStart = 0;

               for(int j = 0; j < i; jBlockStart += 32) {
                  if(this.hashes[i] == this.hashes[j] && arrayRegionMatches(this.values, iBlockStart, this.values, jBlockStart, 32)) {
                     this.indices[i] = this.indices[j];
                  }

                  ++j;
               }

               if(this.indices[i] == '\uffff') {
                  int dest;
                  if(exhaustive) {
                     dest = this.FindOverlappingPosition(iBlockStart, target, newSize);
                  } else {
                     dest = newSize;
                  }

                  int limit = dest + 32;
                  if(limit > newSize) {
                     for(int j = newSize; j < limit; ++j) {
                        target[j] = this.values[iBlockStart + j - dest];
                     }

                     newSize = limit;
                  }

                  this.indices[i] = (char)dest;
                  if(!touched) {
                     iUntouched = (char)jBlockStart;
                  }
               }
            }

            ++i;
         }

         char[] result = new char[newSize];
         System.arraycopy(target, 0, result, 0, newSize);
         this.values = result;
         this.isCompact = true;
         this.hashes = null;
      }

   }

   private int FindOverlappingPosition(int start, char[] tempValues, int tempCount) {
      for(int i = 0; i < tempCount; ++i) {
         int currentCount = 32;
         if(i + 32 > tempCount) {
            currentCount = tempCount - i;
         }

         if(arrayRegionMatches(this.values, start, tempValues, i, currentCount)) {
            return i;
         }
      }

      return tempCount;
   }

   static final boolean arrayRegionMatches(char[] source, int sourceStart, char[] target, int targetStart, int len) {
      int sourceEnd = sourceStart + len;
      int delta = targetStart - sourceStart;

      for(int i = sourceStart; i < sourceEnd; ++i) {
         if(source[i] != target[i + delta]) {
            return false;
         }
      }

      return true;
   }

   private final void touchBlock(int i, int value) {
      this.hashes[i] = this.hashes[i] + (value << 1) | 1;
   }

   private final boolean blockTouched(int i) {
      return this.hashes[i] != 0;
   }

   /** @deprecated */
   public char[] getIndexArray() {
      return this.indices;
   }

   /** @deprecated */
   public char[] getValueArray() {
      return this.values;
   }

   /** @deprecated */
   public Object clone() {
      try {
         CompactCharArray other = (CompactCharArray)super.clone();
         other.values = (char[])this.values.clone();
         other.indices = (char[])this.indices.clone();
         if(this.hashes != null) {
            other.hashes = (int[])this.hashes.clone();
         }

         return other;
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException();
      }
   }

   /** @deprecated */
   public boolean equals(Object obj) {
      if(obj == null) {
         return false;
      } else if(this == obj) {
         return true;
      } else if(this.getClass() != obj.getClass()) {
         return false;
      } else {
         CompactCharArray other = (CompactCharArray)obj;

         for(int i = 0; i < 65536; ++i) {
            if(this.elementAt((char)i) != other.elementAt((char)i)) {
               return false;
            }
         }

         return true;
      }
   }

   /** @deprecated */
   public int hashCode() {
      int result = 0;
      int increment = Math.min(3, this.values.length / 16);

      for(int i = 0; i < this.values.length; i += increment) {
         result = result * 37 + this.values[i];
      }

      return result;
   }

   private void expand() {
      if(this.isCompact) {
         this.hashes = new int[2048];
         char[] tempArray = new char[65536];

         for(int i = 0; i < 65536; ++i) {
            tempArray[i] = this.elementAt((char)i);
         }

         for(int var3 = 0; var3 < 2048; ++var3) {
            this.indices[var3] = (char)(var3 << 5);
         }

         this.values = null;
         this.values = tempArray;
         this.isCompact = false;
      }

   }
}
