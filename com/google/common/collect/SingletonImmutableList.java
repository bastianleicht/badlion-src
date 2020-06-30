package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableList extends ImmutableList {
   final transient Object element;

   SingletonImmutableList(Object element) {
      this.element = Preconditions.checkNotNull(element);
   }

   public Object get(int index) {
      Preconditions.checkElementIndex(index, 1);
      return this.element;
   }

   public int indexOf(@Nullable Object object) {
      return this.element.equals(object)?0:-1;
   }

   public UnmodifiableIterator iterator() {
      return Iterators.singletonIterator(this.element);
   }

   public int lastIndexOf(@Nullable Object object) {
      return this.indexOf(object);
   }

   public int size() {
      return 1;
   }

   public ImmutableList subList(int fromIndex, int toIndex) {
      Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
      return (ImmutableList)(fromIndex == toIndex?ImmutableList.of():this);
   }

   public ImmutableList reverse() {
      return this;
   }

   public boolean contains(@Nullable Object object) {
      return this.element.equals(object);
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(!(object instanceof List)) {
         return false;
      } else {
         List<?> that = (List)object;
         return that.size() == 1 && this.element.equals(that.get(0));
      }
   }

   public int hashCode() {
      return 31 + this.element.hashCode();
   }

   public String toString() {
      String elementToString = this.element.toString();
      return (new StringBuilder(elementToString.length() + 2)).append('[').append(elementToString).append(']').toString();
   }

   public boolean isEmpty() {
      return false;
   }

   boolean isPartialView() {
      return false;
   }

   int copyIntoArray(Object[] dst, int offset) {
      dst[offset] = this.element;
      return offset + 1;
   }
}
