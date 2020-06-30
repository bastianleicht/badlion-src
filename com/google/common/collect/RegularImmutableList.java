package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableListIterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
class RegularImmutableList extends ImmutableList {
   private final transient int offset;
   private final transient int size;
   private final transient Object[] array;

   RegularImmutableList(Object[] array, int offset, int size) {
      this.offset = offset;
      this.size = size;
      this.array = array;
   }

   RegularImmutableList(Object[] array) {
      this(array, 0, array.length);
   }

   public int size() {
      return this.size;
   }

   boolean isPartialView() {
      return this.size != this.array.length;
   }

   int copyIntoArray(Object[] dst, int dstOff) {
      System.arraycopy(this.array, this.offset, dst, dstOff, this.size);
      return dstOff + this.size;
   }

   public Object get(int index) {
      Preconditions.checkElementIndex(index, this.size);
      return this.array[index + this.offset];
   }

   public int indexOf(@Nullable Object object) {
      if(object == null) {
         return -1;
      } else {
         for(int i = 0; i < this.size; ++i) {
            if(this.array[this.offset + i].equals(object)) {
               return i;
            }
         }

         return -1;
      }
   }

   public int lastIndexOf(@Nullable Object object) {
      if(object == null) {
         return -1;
      } else {
         for(int i = this.size - 1; i >= 0; --i) {
            if(this.array[this.offset + i].equals(object)) {
               return i;
            }
         }

         return -1;
      }
   }

   ImmutableList subListUnchecked(int fromIndex, int toIndex) {
      return new RegularImmutableList(this.array, this.offset + fromIndex, toIndex - fromIndex);
   }

   public UnmodifiableListIterator listIterator(int index) {
      return Iterators.forArray(this.array, this.offset, this.size, index);
   }
}
