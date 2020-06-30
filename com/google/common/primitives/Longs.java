package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
public final class Longs {
   public static final int BYTES = 8;
   public static final long MAX_POWER_OF_TWO = 4611686018427387904L;

   public static int hashCode(long value) {
      return (int)(value ^ value >>> 32);
   }

   public static int compare(long a, long b) {
      return a < b?-1:(a > b?1:0);
   }

   public static boolean contains(long[] array, long target) {
      for(long value : array) {
         if(value == target) {
            return true;
         }
      }

      return false;
   }

   public static int indexOf(long[] array, long target) {
      return indexOf(array, target, 0, array.length);
   }

   private static int indexOf(long[] array, long target, int start, int end) {
      for(int i = start; i < end; ++i) {
         if(array[i] == target) {
            return i;
         }
      }

      return -1;
   }

   public static int indexOf(long[] array, long[] target) {
      Preconditions.checkNotNull(array, "array");
      Preconditions.checkNotNull(target, "target");
      if(target.length == 0) {
         return 0;
      } else {
         for(int i = 0; i < array.length - target.length + 1; ++i) {
            int j = 0;

            while(true) {
               if(j >= target.length) {
                  return i;
               }

               if(array[i + j] != target[j]) {
                  break;
               }

               ++j;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(long[] array, long target) {
      return lastIndexOf(array, target, 0, array.length);
   }

   private static int lastIndexOf(long[] array, long target, int start, int end) {
      for(int i = end - 1; i >= start; --i) {
         if(array[i] == target) {
            return i;
         }
      }

      return -1;
   }

   public static long min(long... array) {
      Preconditions.checkArgument(array.length > 0);
      long min = array[0];

      for(int i = 1; i < array.length; ++i) {
         if(array[i] < min) {
            min = array[i];
         }
      }

      return min;
   }

   public static long max(long... array) {
      Preconditions.checkArgument(array.length > 0);
      long max = array[0];

      for(int i = 1; i < array.length; ++i) {
         if(array[i] > max) {
            max = array[i];
         }
      }

      return max;
   }

   public static long[] concat(long[]... arrays) {
      int length = 0;

      for(long[] array : arrays) {
         length += array.length;
      }

      long[] result = new long[length];
      int pos = 0;

      for(long[] array : arrays) {
         System.arraycopy(array, 0, result, pos, array.length);
         pos += array.length;
      }

      return result;
   }

   public static byte[] toByteArray(long value) {
      byte[] result = new byte[8];

      for(int i = 7; i >= 0; --i) {
         result[i] = (byte)((int)(value & 255L));
         value >>= 8;
      }

      return result;
   }

   public static long fromByteArray(byte[] bytes) {
      Preconditions.checkArgument(bytes.length >= 8, "array too small: %s < %s", new Object[]{Integer.valueOf(bytes.length), Integer.valueOf(8)});
      return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
   }

   public static long fromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
      return ((long)b1 & 255L) << 56 | ((long)b2 & 255L) << 48 | ((long)b3 & 255L) << 40 | ((long)b4 & 255L) << 32 | ((long)b5 & 255L) << 24 | ((long)b6 & 255L) << 16 | ((long)b7 & 255L) << 8 | (long)b8 & 255L;
   }

   @Beta
   public static Long tryParse(String string) {
      if(((String)Preconditions.checkNotNull(string)).isEmpty()) {
         return null;
      } else {
         boolean negative = string.charAt(0) == 45;
         int index = negative?1:0;
         if(index == string.length()) {
            return null;
         } else {
            int digit = string.charAt(index++) - 48;
            if(digit >= 0 && digit <= 9) {
               long accum;
               for(accum = (long)(-digit); index < string.length(); accum = accum - (long)digit) {
                  digit = string.charAt(index++) - 48;
                  if(digit < 0 || digit > 9 || accum < -922337203685477580L) {
                     return null;
                  }

                  accum = accum * 10L;
                  if(accum < Long.MIN_VALUE + (long)digit) {
                     return null;
                  }
               }

               if(negative) {
                  return Long.valueOf(accum);
               } else if(accum == Long.MIN_VALUE) {
                  return null;
               } else {
                  return Long.valueOf(-accum);
               }
            } else {
               return null;
            }
         }
      }
   }

   @Beta
   public static Converter stringConverter() {
      return Longs.LongConverter.INSTANCE;
   }

   public static long[] ensureCapacity(long[] array, int minLength, int padding) {
      Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[]{Integer.valueOf(minLength)});
      Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[]{Integer.valueOf(padding)});
      return array.length < minLength?copyOf(array, minLength + padding):array;
   }

   private static long[] copyOf(long[] original, int length) {
      long[] copy = new long[length];
      System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
      return copy;
   }

   public static String join(String separator, long... array) {
      Preconditions.checkNotNull(separator);
      if(array.length == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(array.length * 10);
         builder.append(array[0]);

         for(int i = 1; i < array.length; ++i) {
            builder.append(separator).append(array[i]);
         }

         return builder.toString();
      }
   }

   public static Comparator lexicographicalComparator() {
      return Longs.LexicographicalComparator.INSTANCE;
   }

   public static long[] toArray(Collection collection) {
      if(collection instanceof Longs.LongArrayAsList) {
         return ((Longs.LongArrayAsList)collection).toLongArray();
      } else {
         Object[] boxedArray = collection.toArray();
         int len = boxedArray.length;
         long[] array = new long[len];

         for(int i = 0; i < len; ++i) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).longValue();
         }

         return array;
      }
   }

   public static List asList(long... backingArray) {
      return (List)(backingArray.length == 0?Collections.emptyList():new Longs.LongArrayAsList(backingArray));
   }

   // $FF: synthetic method
   static int access$000(long[] x0, long x1, int x2, int x3) {
      return indexOf(x0, x1, x2, x3);
   }

   // $FF: synthetic method
   static int access$100(long[] x0, long x1, int x2, int x3) {
      return lastIndexOf(x0, x1, x2, x3);
   }

   private static enum LexicographicalComparator implements Comparator {
      INSTANCE;

      public int compare(long[] left, long[] right) {
         int minLength = Math.min(left.length, right.length);

         for(int i = 0; i < minLength; ++i) {
            int result = Longs.compare(left[i], right[i]);
            if(result != 0) {
               return result;
            }
         }

         return left.length - right.length;
      }
   }

   @GwtCompatible
   private static class LongArrayAsList extends AbstractList implements RandomAccess, Serializable {
      final long[] array;
      final int start;
      final int end;
      private static final long serialVersionUID = 0L;

      LongArrayAsList(long[] array) {
         this(array, 0, array.length);
      }

      LongArrayAsList(long[] array, int start, int end) {
         this.array = array;
         this.start = start;
         this.end = end;
      }

      public int size() {
         return this.end - this.start;
      }

      public boolean isEmpty() {
         return false;
      }

      public Long get(int index) {
         Preconditions.checkElementIndex(index, this.size());
         return Long.valueOf(this.array[this.start + index]);
      }

      public boolean contains(Object target) {
         return target instanceof Long && Longs.access$000(this.array, ((Long)target).longValue(), this.start, this.end) != -1;
      }

      public int indexOf(Object target) {
         if(target instanceof Long) {
            int i = Longs.access$000(this.array, ((Long)target).longValue(), this.start, this.end);
            if(i >= 0) {
               return i - this.start;
            }
         }

         return -1;
      }

      public int lastIndexOf(Object target) {
         if(target instanceof Long) {
            int i = Longs.access$100(this.array, ((Long)target).longValue(), this.start, this.end);
            if(i >= 0) {
               return i - this.start;
            }
         }

         return -1;
      }

      public Long set(int index, Long element) {
         Preconditions.checkElementIndex(index, this.size());
         long oldValue = this.array[this.start + index];
         this.array[this.start + index] = ((Long)Preconditions.checkNotNull(element)).longValue();
         return Long.valueOf(oldValue);
      }

      public List subList(int fromIndex, int toIndex) {
         int size = this.size();
         Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
         return (List)(fromIndex == toIndex?Collections.emptyList():new Longs.LongArrayAsList(this.array, this.start + fromIndex, this.start + toIndex));
      }

      public boolean equals(Object object) {
         if(object == this) {
            return true;
         } else if(object instanceof Longs.LongArrayAsList) {
            Longs.LongArrayAsList that = (Longs.LongArrayAsList)object;
            int size = this.size();
            if(that.size() != size) {
               return false;
            } else {
               for(int i = 0; i < size; ++i) {
                  if(this.array[this.start + i] != that.array[that.start + i]) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return super.equals(object);
         }
      }

      public int hashCode() {
         int result = 1;

         for(int i = this.start; i < this.end; ++i) {
            result = 31 * result + Longs.hashCode(this.array[i]);
         }

         return result;
      }

      public String toString() {
         StringBuilder builder = new StringBuilder(this.size() * 10);
         builder.append('[').append(this.array[this.start]);

         for(int i = this.start + 1; i < this.end; ++i) {
            builder.append(", ").append(this.array[i]);
         }

         return builder.append(']').toString();
      }

      long[] toLongArray() {
         int size = this.size();
         long[] result = new long[size];
         System.arraycopy(this.array, this.start, result, 0, size);
         return result;
      }
   }

   private static final class LongConverter extends Converter implements Serializable {
      static final Longs.LongConverter INSTANCE = new Longs.LongConverter();
      private static final long serialVersionUID = 1L;

      protected Long doForward(String value) {
         return Long.decode(value);
      }

      protected String doBackward(Long value) {
         return value.toString();
      }

      public String toString() {
         return "Longs.stringConverter()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }
}
