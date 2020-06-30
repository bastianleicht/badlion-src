package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Platform;
import java.lang.reflect.Array;
import java.util.Collection;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class ObjectArrays {
   static final Object[] EMPTY_ARRAY = new Object[0];

   @GwtIncompatible("Array.newInstance(Class, int)")
   public static Object[] newArray(Class type, int length) {
      return (Object[])((Object[])Array.newInstance(type, length));
   }

   public static Object[] newArray(Object[] reference, int length) {
      return Platform.newArray(reference, length);
   }

   @GwtIncompatible("Array.newInstance(Class, int)")
   public static Object[] concat(Object[] first, Object[] second, Class type) {
      T[] result = newArray(type, first.length + second.length);
      System.arraycopy(first, 0, result, 0, first.length);
      System.arraycopy(second, 0, result, first.length, second.length);
      return result;
   }

   public static Object[] concat(@Nullable Object element, Object[] array) {
      T[] result = newArray(array, array.length + 1);
      result[0] = element;
      System.arraycopy(array, 0, result, 1, array.length);
      return result;
   }

   public static Object[] concat(Object[] array, @Nullable Object element) {
      T[] result = arraysCopyOf(array, array.length + 1);
      result[array.length] = element;
      return result;
   }

   static Object[] arraysCopyOf(Object[] original, int newLength) {
      T[] copy = newArray(original, newLength);
      System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
      return copy;
   }

   static Object[] toArrayImpl(Collection c, Object[] array) {
      int size = c.size();
      if(array.length < size) {
         array = newArray(array, size);
      }

      fillArray(c, array);
      if(array.length > size) {
         array[size] = null;
      }

      return array;
   }

   static Object[] toArrayImpl(Object[] src, int offset, int len, Object[] dst) {
      Preconditions.checkPositionIndexes(offset, offset + len, src.length);
      if(dst.length < len) {
         dst = newArray(dst, len);
      } else if(dst.length > len) {
         dst[len] = null;
      }

      System.arraycopy(src, offset, dst, 0, len);
      return dst;
   }

   static Object[] toArrayImpl(Collection c) {
      return fillArray(c, new Object[c.size()]);
   }

   static Object[] copyAsObjectArray(Object[] elements, int offset, int length) {
      Preconditions.checkPositionIndexes(offset, offset + length, elements.length);
      if(length == 0) {
         return EMPTY_ARRAY;
      } else {
         Object[] result = new Object[length];
         System.arraycopy(elements, offset, result, 0, length);
         return result;
      }
   }

   private static Object[] fillArray(Iterable elements, Object[] array) {
      int i = 0;

      for(Object element : elements) {
         array[i++] = element;
      }

      return array;
   }

   static void swap(Object[] array, int i, int j) {
      Object temp = array[i];
      array[i] = array[j];
      array[j] = temp;
   }

   static Object[] checkElementsNotNull(Object... array) {
      return checkElementsNotNull(array, array.length);
   }

   static Object[] checkElementsNotNull(Object[] array, int length) {
      for(int i = 0; i < length; ++i) {
         checkElementNotNull(array[i], i);
      }

      return array;
   }

   static Object checkElementNotNull(Object element, int index) {
      if(element == null) {
         throw new NullPointerException("at index " + index);
      } else {
         return element;
      }
   }
}
