package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.CheckForNull;

@GwtCompatible(
   emulated = true
)
public final class Ints {
   public static final int BYTES = 4;
   public static final int MAX_POWER_OF_TWO = 1073741824;
   private static final byte[] asciiDigits = new byte[128];

   public static int hashCode(int value) {
      return value;
   }

   public static int checkedCast(long value) {
      int result = (int)value;
      if((long)result != value) {
         throw new IllegalArgumentException("Out of range: " + value);
      } else {
         return result;
      }
   }

   public static int saturatedCast(long value) {
      return value > 2147483647L?Integer.MAX_VALUE:(value < -2147483648L?Integer.MIN_VALUE:(int)value);
   }

   public static int compare(int a, int b) {
      return a < b?-1:(a > b?1:0);
   }

   public static boolean contains(int[] array, int target) {
      for(int value : array) {
         if(value == target) {
            return true;
         }
      }

      return false;
   }

   public static int indexOf(int[] array, int target) {
      return indexOf(array, target, 0, array.length);
   }

   private static int indexOf(int[] array, int target, int start, int end) {
      for(int i = start; i < end; ++i) {
         if(array[i] == target) {
            return i;
         }
      }

      return -1;
   }

   public static int indexOf(int[] array, int[] target) {
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

   public static int lastIndexOf(int[] array, int target) {
      return lastIndexOf(array, target, 0, array.length);
   }

   private static int lastIndexOf(int[] array, int target, int start, int end) {
      for(int i = end - 1; i >= start; --i) {
         if(array[i] == target) {
            return i;
         }
      }

      return -1;
   }

   public static int min(int... array) {
      Preconditions.checkArgument(array.length > 0);
      int min = array[0];

      for(int i = 1; i < array.length; ++i) {
         if(array[i] < min) {
            min = array[i];
         }
      }

      return min;
   }

   public static int max(int... array) {
      Preconditions.checkArgument(array.length > 0);
      int max = array[0];

      for(int i = 1; i < array.length; ++i) {
         if(array[i] > max) {
            max = array[i];
         }
      }

      return max;
   }

   public static int[] concat(int[]... arrays) {
      int length = 0;

      for(int[] array : arrays) {
         length += array.length;
      }

      int[] result = new int[length];
      int pos = 0;

      for(int[] array : arrays) {
         System.arraycopy(array, 0, result, pos, array.length);
         pos += array.length;
      }

      return result;
   }

   @GwtIncompatible("doesn\'t work")
   public static byte[] toByteArray(int value) {
      return new byte[]{(byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value};
   }

   @GwtIncompatible("doesn\'t work")
   public static int fromByteArray(byte[] bytes) {
      Preconditions.checkArgument(bytes.length >= 4, "array too small: %s < %s", new Object[]{Integer.valueOf(bytes.length), Integer.valueOf(4)});
      return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
   }

   @GwtIncompatible("doesn\'t work")
   public static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
      return b1 << 24 | (b2 & 255) << 16 | (b3 & 255) << 8 | b4 & 255;
   }

   @Beta
   public static Converter stringConverter() {
      return Ints.IntConverter.INSTANCE;
   }

   public static int[] ensureCapacity(int[] array, int minLength, int padding) {
      Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[]{Integer.valueOf(minLength)});
      Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[]{Integer.valueOf(padding)});
      return array.length < minLength?copyOf(array, minLength + padding):array;
   }

   private static int[] copyOf(int[] original, int length) {
      int[] copy = new int[length];
      System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
      return copy;
   }

   public static String join(String separator, int... array) {
      Preconditions.checkNotNull(separator);
      if(array.length == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(array.length * 5);
         builder.append(array[0]);

         for(int i = 1; i < array.length; ++i) {
            builder.append(separator).append(array[i]);
         }

         return builder.toString();
      }
   }

   public static Comparator lexicographicalComparator() {
      return Ints.LexicographicalComparator.INSTANCE;
   }

   public static int[] toArray(Collection collection) {
      if(collection instanceof Ints.IntArrayAsList) {
         return ((Ints.IntArrayAsList)collection).toIntArray();
      } else {
         Object[] boxedArray = collection.toArray();
         int len = boxedArray.length;
         int[] array = new int[len];

         for(int i = 0; i < len; ++i) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).intValue();
         }

         return array;
      }
   }

   public static List asList(int... backingArray) {
      return (List)(backingArray.length == 0?Collections.emptyList():new Ints.IntArrayAsList(backingArray));
   }

   private static int digit(char c) {
      return c < 128?asciiDigits[c]:-1;
   }

   @CheckForNull
   @Beta
   @GwtIncompatible("TODO")
   public static Integer tryParse(String string) {
      return tryParse(string, 10);
   }

   @CheckForNull
   @GwtIncompatible("TODO")
   static Integer tryParse(String string, int radix) {
      if(((String)Preconditions.checkNotNull(string)).isEmpty()) {
         return null;
      } else if(radix >= 2 && radix <= 36) {
         boolean negative = string.charAt(0) == 45;
         int index = negative?1:0;
         if(index == string.length()) {
            return null;
         } else {
            int digit = digit(string.charAt(index++));
            if(digit >= 0 && digit < radix) {
               int accum = -digit;

               for(int cap = Integer.MIN_VALUE / radix; index < string.length(); accum = accum - digit) {
                  digit = digit(string.charAt(index++));
                  if(digit < 0 || digit >= radix || accum < cap) {
                     return null;
                  }

                  accum = accum * radix;
                  if(accum < Integer.MIN_VALUE + digit) {
                     return null;
                  }
               }

               if(negative) {
                  return Integer.valueOf(accum);
               } else if(accum == Integer.MIN_VALUE) {
                  return null;
               } else {
                  return Integer.valueOf(-accum);
               }
            } else {
               return null;
            }
         }
      } else {
         throw new IllegalArgumentException("radix must be between MIN_RADIX and MAX_RADIX but was " + radix);
      }
   }

   static {
      Arrays.fill(asciiDigits, (byte)-1);

      for(int i = 0; i <= 9; ++i) {
         asciiDigits[48 + i] = (byte)i;
      }

      for(int i = 0; i <= 26; ++i) {
         asciiDigits[65 + i] = (byte)(10 + i);
         asciiDigits[97 + i] = (byte)(10 + i);
      }

   }

   @GwtCompatible
   private static class IntArrayAsList extends AbstractList implements RandomAccess, Serializable {
      final int[] array;
      final int start;
      final int end;
      private static final long serialVersionUID = 0L;

      IntArrayAsList(int[] array) {
         this(array, 0, array.length);
      }

      IntArrayAsList(int[] array, int start, int end) {
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

      public Integer get(int index) {
         Preconditions.checkElementIndex(index, this.size());
         return Integer.valueOf(this.array[this.start + index]);
      }

      public boolean contains(Object target) {
         return target instanceof Integer && Ints.indexOf(this.array, ((Integer)target).intValue(), this.start, this.end) != -1;
      }

      public int indexOf(Object target) {
         if(target instanceof Integer) {
            int i = Ints.indexOf(this.array, ((Integer)target).intValue(), this.start, this.end);
            if(i >= 0) {
               return i - this.start;
            }
         }

         return -1;
      }

      public int lastIndexOf(Object target) {
         if(target instanceof Integer) {
            int i = Ints.lastIndexOf(this.array, ((Integer)target).intValue(), this.start, this.end);
            if(i >= 0) {
               return i - this.start;
            }
         }

         return -1;
      }

      public Integer set(int index, Integer element) {
         Preconditions.checkElementIndex(index, this.size());
         int oldValue = this.array[this.start + index];
         this.array[this.start + index] = ((Integer)Preconditions.checkNotNull(element)).intValue();
         return Integer.valueOf(oldValue);
      }

      public List subList(int fromIndex, int toIndex) {
         int size = this.size();
         Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
         return (List)(fromIndex == toIndex?Collections.emptyList():new Ints.IntArrayAsList(this.array, this.start + fromIndex, this.start + toIndex));
      }

      public boolean equals(Object object) {
         if(object == this) {
            return true;
         } else if(object instanceof Ints.IntArrayAsList) {
            Ints.IntArrayAsList that = (Ints.IntArrayAsList)object;
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
            result = 31 * result + Ints.hashCode(this.array[i]);
         }

         return result;
      }

      public String toString() {
         StringBuilder builder = new StringBuilder(this.size() * 5);
         builder.append('[').append(this.array[this.start]);

         for(int i = this.start + 1; i < this.end; ++i) {
            builder.append(", ").append(this.array[i]);
         }

         return builder.append(']').toString();
      }

      int[] toIntArray() {
         int size = this.size();
         int[] result = new int[size];
         System.arraycopy(this.array, this.start, result, 0, size);
         return result;
      }
   }

   private static final class IntConverter extends Converter implements Serializable {
      static final Ints.IntConverter INSTANCE = new Ints.IntConverter();
      private static final long serialVersionUID = 1L;

      protected Integer doForward(String value) {
         return Integer.decode(value);
      }

      protected String doBackward(Integer value) {
         return value.toString();
      }

      public String toString() {
         return "Ints.stringConverter()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static enum LexicographicalComparator implements Comparator {
      INSTANCE;

      public int compare(int[] left, int[] right) {
         int minLength = Math.min(left.length, right.length);

         for(int i = 0; i < minLength; ++i) {
            int result = Ints.compare(left[i], right[i]);
            if(result != 0) {
               return result;
            }
         }

         return left.length - right.length;
      }
   }
}
