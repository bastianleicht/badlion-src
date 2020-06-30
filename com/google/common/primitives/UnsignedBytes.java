package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedLongs;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Comparator;
import sun.misc.Unsafe;

public final class UnsignedBytes {
   public static final byte MAX_POWER_OF_TWO = -128;
   public static final byte MAX_VALUE = -1;
   private static final int UNSIGNED_MASK = 255;

   public static int toInt(byte value) {
      return value & 255;
   }

   public static byte checkedCast(long value) {
      if(value >> 8 != 0L) {
         throw new IllegalArgumentException("Out of range: " + value);
      } else {
         return (byte)((int)value);
      }
   }

   public static byte saturatedCast(long value) {
      return value > (long)toInt((byte)-1)?-1:(value < 0L?0:(byte)((int)value));
   }

   public static int compare(byte a, byte b) {
      return toInt(a) - toInt(b);
   }

   public static byte min(byte... array) {
      Preconditions.checkArgument(array.length > 0);
      int min = toInt(array[0]);

      for(int i = 1; i < array.length; ++i) {
         int next = toInt(array[i]);
         if(next < min) {
            min = next;
         }
      }

      return (byte)min;
   }

   public static byte max(byte... array) {
      Preconditions.checkArgument(array.length > 0);
      int max = toInt(array[0]);

      for(int i = 1; i < array.length; ++i) {
         int next = toInt(array[i]);
         if(next > max) {
            max = next;
         }
      }

      return (byte)max;
   }

   @Beta
   public static String toString(byte x) {
      return toString(x, 10);
   }

   @Beta
   public static String toString(byte x, int radix) {
      Preconditions.checkArgument(radix >= 2 && radix <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[]{Integer.valueOf(radix)});
      return Integer.toString(toInt(x), radix);
   }

   @Beta
   public static byte parseUnsignedByte(String string) {
      return parseUnsignedByte(string, 10);
   }

   @Beta
   public static byte parseUnsignedByte(String string, int radix) {
      int parse = Integer.parseInt((String)Preconditions.checkNotNull(string), radix);
      if(parse >> 8 == 0) {
         return (byte)parse;
      } else {
         throw new NumberFormatException("out of range: " + parse);
      }
   }

   public static String join(String separator, byte... array) {
      Preconditions.checkNotNull(separator);
      if(array.length == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(array.length * (3 + separator.length()));
         builder.append(toInt(array[0]));

         for(int i = 1; i < array.length; ++i) {
            builder.append(separator).append(toString(array[i]));
         }

         return builder.toString();
      }
   }

   public static Comparator lexicographicalComparator() {
      return UnsignedBytes.LexicographicalComparatorHolder.BEST_COMPARATOR;
   }

   @VisibleForTesting
   static Comparator lexicographicalComparatorJavaImpl() {
      return UnsignedBytes.LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
   }

   @VisibleForTesting
   static class LexicographicalComparatorHolder {
      static final String UNSAFE_COMPARATOR_NAME = UnsignedBytes.LexicographicalComparatorHolder.class.getName() + "$UnsafeComparator";
      static final Comparator BEST_COMPARATOR = getBestComparator();

      static Comparator getBestComparator() {
         try {
            Class<?> theClass = Class.forName(UNSAFE_COMPARATOR_NAME);
            Comparator<byte[]> comparator = (Comparator)theClass.getEnumConstants()[0];
            return comparator;
         } catch (Throwable var2) {
            return UnsignedBytes.lexicographicalComparatorJavaImpl();
         }
      }

      static enum PureJavaComparator implements Comparator {
         INSTANCE;

         public int compare(byte[] left, byte[] right) {
            int minLength = Math.min(left.length, right.length);

            for(int i = 0; i < minLength; ++i) {
               int result = UnsignedBytes.compare(left[i], right[i]);
               if(result != 0) {
                  return result;
               }
            }

            return left.length - right.length;
         }
      }

      @VisibleForTesting
      static enum UnsafeComparator implements Comparator {
         INSTANCE;

         static final boolean BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
         static final Unsafe theUnsafe = getUnsafe();
         static final int BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);

         private static Unsafe getUnsafe() {
            try {
               return Unsafe.getUnsafe();
            } catch (SecurityException var2) {
               try {
                  return (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                     public Unsafe run() throws Exception {
                        Class<Unsafe> k = Unsafe.class;

                        for(Field f : k.getDeclaredFields()) {
                           f.setAccessible(true);
                           Object x = f.get((Object)null);
                           if(k.isInstance(x)) {
                              return (Unsafe)k.cast(x);
                           }
                        }

                        throw new NoSuchFieldError("the Unsafe");
                     }
                  });
               } catch (PrivilegedActionException var1) {
                  throw new RuntimeException("Could not initialize intrinsics", var1.getCause());
               }
            }
         }

         public int compare(byte[] left, byte[] right) {
            int minLength = Math.min(left.length, right.length);
            int minWords = minLength / 8;

            for(int i = 0; i < minWords * 8; i += 8) {
               long lw = theUnsafe.getLong(left, (long)BYTE_ARRAY_BASE_OFFSET + (long)i);
               long rw = theUnsafe.getLong(right, (long)BYTE_ARRAY_BASE_OFFSET + (long)i);
               if(lw != rw) {
                  if(BIG_ENDIAN) {
                     return UnsignedLongs.compare(lw, rw);
                  }

                  int n = Long.numberOfTrailingZeros(lw ^ rw) & -8;
                  return (int)((lw >>> n & 255L) - (rw >>> n & 255L));
               }
            }

            for(int i = minWords * 8; i < minLength; ++i) {
               int result = UnsignedBytes.compare(left[i], right[i]);
               if(result != 0) {
                  return result;
               }
            }

            return left.length - right.length;
         }

         static {
            if(theUnsafe.arrayIndexScale(byte[].class) != 1) {
               throw new AssertionError();
            }
         }
      }
   }
}
