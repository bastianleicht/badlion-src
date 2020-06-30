package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableList;
import com.google.common.collect.SingletonImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.UnmodifiableListIterator;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableList extends ImmutableCollection implements List, RandomAccess {
   private static final ImmutableList EMPTY = new RegularImmutableList(ObjectArrays.EMPTY_ARRAY);

   public static ImmutableList of() {
      return EMPTY;
   }

   public static ImmutableList of(Object element) {
      return new SingletonImmutableList(element);
   }

   public static ImmutableList of(Object e1, Object e2) {
      return construct(new Object[]{e1, e2});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3) {
      return construct(new Object[]{e1, e2, e3});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4) {
      return construct(new Object[]{e1, e2, e3, e4});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5) {
      return construct(new Object[]{e1, e2, e3, e4, e5});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6) {
      return construct(new Object[]{e1, e2, e3, e4, e5, e6});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7) {
      return construct(new Object[]{e1, e2, e3, e4, e5, e6, e7});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7, Object e8) {
      return construct(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7, Object e8, Object e9) {
      return construct(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7, Object e8, Object e9, Object e10) {
      return construct(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9, e10});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7, Object e8, Object e9, Object e10, Object e11) {
      return construct(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11});
   }

   public static ImmutableList of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7, Object e8, Object e9, Object e10, Object e11, Object e12, Object... others) {
      Object[] array = new Object[12 + others.length];
      array[0] = e1;
      array[1] = e2;
      array[2] = e3;
      array[3] = e4;
      array[4] = e5;
      array[5] = e6;
      array[6] = e7;
      array[7] = e8;
      array[8] = e9;
      array[9] = e10;
      array[10] = e11;
      array[11] = e12;
      System.arraycopy(others, 0, array, 12, others.length);
      return construct(array);
   }

   public static ImmutableList copyOf(Iterable elements) {
      Preconditions.checkNotNull(elements);
      return elements instanceof Collection?copyOf(Collections2.cast(elements)):copyOf(elements.iterator());
   }

   public static ImmutableList copyOf(Collection elements) {
      if(elements instanceof ImmutableCollection) {
         ImmutableList<E> list = ((ImmutableCollection)elements).asList();
         return list.isPartialView()?asImmutableList(list.toArray()):list;
      } else {
         return construct(elements.toArray());
      }
   }

   public static ImmutableList copyOf(Iterator elements) {
      if(!elements.hasNext()) {
         return of();
      } else {
         E first = elements.next();
         return !elements.hasNext()?of(first):(new ImmutableList.Builder()).add(first).addAll(elements).build();
      }
   }

   public static ImmutableList copyOf(Object[] elements) {
      switch(elements.length) {
      case 0:
         return of();
      case 1:
         return new SingletonImmutableList(elements[0]);
      default:
         return new RegularImmutableList(ObjectArrays.checkElementsNotNull((Object[])elements.clone()));
      }
   }

   private static ImmutableList construct(Object... elements) {
      return asImmutableList(ObjectArrays.checkElementsNotNull(elements));
   }

   static ImmutableList asImmutableList(Object[] elements) {
      return asImmutableList(elements, elements.length);
   }

   static ImmutableList asImmutableList(Object[] elements, int length) {
      switch(length) {
      case 0:
         return of();
      case 1:
         ImmutableList<E> list = new SingletonImmutableList(elements[0]);
         return list;
      default:
         if(length < elements.length) {
            elements = ObjectArrays.arraysCopyOf(elements, length);
         }

         return new RegularImmutableList(elements);
      }
   }

   public UnmodifiableIterator iterator() {
      return this.listIterator();
   }

   public UnmodifiableListIterator listIterator() {
      return this.listIterator(0);
   }

   public UnmodifiableListIterator listIterator(final int index) {
      return new AbstractIndexedListIterator(this.size(), index) {
         protected Object get(int index) {
            return ImmutableList.this.get(index);
         }
      };
   }

   public int indexOf(@Nullable Object object) {
      return object == null?-1:Lists.indexOfImpl(this, object);
   }

   public int lastIndexOf(@Nullable Object object) {
      return object == null?-1:Lists.lastIndexOfImpl(this, object);
   }

   public boolean contains(@Nullable Object object) {
      return this.indexOf(object) >= 0;
   }

   public ImmutableList subList(int fromIndex, int toIndex) {
      Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size());
      int length = toIndex - fromIndex;
      switch(length) {
      case 0:
         return of();
      case 1:
         return of(this.get(fromIndex));
      default:
         return this.subListUnchecked(fromIndex, toIndex);
      }
   }

   ImmutableList subListUnchecked(int fromIndex, int toIndex) {
      return new ImmutableList.SubList(fromIndex, toIndex - fromIndex);
   }

   /** @deprecated */
   @Deprecated
   public final boolean addAll(int index, Collection newElements) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Object set(int index, Object element) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void add(int index, Object element) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Object remove(int index) {
      throw new UnsupportedOperationException();
   }

   public final ImmutableList asList() {
      return this;
   }

   int copyIntoArray(Object[] dst, int offset) {
      int size = this.size();

      for(int i = 0; i < size; ++i) {
         dst[offset + i] = this.get(i);
      }

      return offset + size;
   }

   public ImmutableList reverse() {
      return new ImmutableList.ReverseImmutableList(this);
   }

   public boolean equals(@Nullable Object obj) {
      return Lists.equalsImpl(this, obj);
   }

   public int hashCode() {
      int hashCode = 1;
      int n = this.size();

      for(int i = 0; i < n; ++i) {
         hashCode = 31 * hashCode + this.get(i).hashCode();
         hashCode = ~(~hashCode);
      }

      return hashCode;
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializedForm");
   }

   Object writeReplace() {
      return new ImmutableList.SerializedForm(this.toArray());
   }

   public static ImmutableList.Builder builder() {
      return new ImmutableList.Builder();
   }

   public static final class Builder extends ImmutableCollection.ArrayBasedBuilder {
      public Builder() {
         this(4);
      }

      Builder(int capacity) {
         super(capacity);
      }

      public ImmutableList.Builder add(Object element) {
         super.add(element);
         return this;
      }

      public ImmutableList.Builder addAll(Iterable elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableList.Builder add(Object... elements) {
         super.add(elements);
         return this;
      }

      public ImmutableList.Builder addAll(Iterator elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableList build() {
         return ImmutableList.asImmutableList(this.contents, this.size);
      }
   }

   private static class ReverseImmutableList extends ImmutableList {
      private final transient ImmutableList forwardList;

      ReverseImmutableList(ImmutableList backingList) {
         this.forwardList = backingList;
      }

      private int reverseIndex(int index) {
         return this.size() - 1 - index;
      }

      private int reversePosition(int index) {
         return this.size() - index;
      }

      public ImmutableList reverse() {
         return this.forwardList;
      }

      public boolean contains(@Nullable Object object) {
         return this.forwardList.contains(object);
      }

      public int indexOf(@Nullable Object object) {
         int index = this.forwardList.lastIndexOf(object);
         return index >= 0?this.reverseIndex(index):-1;
      }

      public int lastIndexOf(@Nullable Object object) {
         int index = this.forwardList.indexOf(object);
         return index >= 0?this.reverseIndex(index):-1;
      }

      public ImmutableList subList(int fromIndex, int toIndex) {
         Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size());
         return this.forwardList.subList(this.reversePosition(toIndex), this.reversePosition(fromIndex)).reverse();
      }

      public Object get(int index) {
         Preconditions.checkElementIndex(index, this.size());
         return this.forwardList.get(this.reverseIndex(index));
      }

      public int size() {
         return this.forwardList.size();
      }

      boolean isPartialView() {
         return this.forwardList.isPartialView();
      }
   }

   static class SerializedForm implements Serializable {
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      SerializedForm(Object[] elements) {
         this.elements = elements;
      }

      Object readResolve() {
         return ImmutableList.copyOf(this.elements);
      }
   }

   class SubList extends ImmutableList {
      final transient int offset;
      final transient int length;

      SubList(int offset, int length) {
         this.offset = offset;
         this.length = length;
      }

      public int size() {
         return this.length;
      }

      public Object get(int index) {
         Preconditions.checkElementIndex(index, this.length);
         return ImmutableList.this.get(index + this.offset);
      }

      public ImmutableList subList(int fromIndex, int toIndex) {
         Preconditions.checkPositionIndexes(fromIndex, toIndex, this.length);
         return ImmutableList.this.subList(fromIndex + this.offset, toIndex + this.offset);
      }

      boolean isPartialView() {
         return true;
      }
   }
}
