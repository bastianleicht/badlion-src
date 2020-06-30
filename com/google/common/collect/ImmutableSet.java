package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.EmptyImmutableSet;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableEnumSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.SingletonImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableSet extends ImmutableCollection implements Set {
   static final int MAX_TABLE_SIZE = 1073741824;
   private static final double DESIRED_LOAD_FACTOR = 0.7D;
   private static final int CUTOFF = 751619276;

   public static ImmutableSet of() {
      return EmptyImmutableSet.INSTANCE;
   }

   public static ImmutableSet of(Object element) {
      return new SingletonImmutableSet(element);
   }

   public static ImmutableSet of(Object e1, Object e2) {
      return construct(2, new Object[]{e1, e2});
   }

   public static ImmutableSet of(Object e1, Object e2, Object e3) {
      return construct(3, new Object[]{e1, e2, e3});
   }

   public static ImmutableSet of(Object e1, Object e2, Object e3, Object e4) {
      return construct(4, new Object[]{e1, e2, e3, e4});
   }

   public static ImmutableSet of(Object e1, Object e2, Object e3, Object e4, Object e5) {
      return construct(5, new Object[]{e1, e2, e3, e4, e5});
   }

   public static ImmutableSet of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object... others) {
      int paramCount = 6;
      Object[] elements = new Object[6 + others.length];
      elements[0] = e1;
      elements[1] = e2;
      elements[2] = e3;
      elements[3] = e4;
      elements[4] = e5;
      elements[5] = e6;
      System.arraycopy(others, 0, elements, 6, others.length);
      return construct(elements.length, elements);
   }

   private static ImmutableSet construct(int n, Object... elements) {
      switch(n) {
      case 0:
         return of();
      case 1:
         E elem = elements[0];
         return of(elem);
      default:
         int tableSize = chooseTableSize(n);
         Object[] table = new Object[tableSize];
         int mask = tableSize - 1;
         int hashCode = 0;
         int uniques = 0;
         int i = 0;

         for(; i < n; ++i) {
            Object element = ObjectArrays.checkElementNotNull(elements[i], i);
            int hash = element.hashCode();
            int j = Hashing.smear(hash);

            while(true) {
               int index = j & mask;
               Object value = table[index];
               if(value == null) {
                  elements[uniques++] = element;
                  table[index] = element;
                  hashCode += hash;
                  break;
               }

               if(value.equals(element)) {
                  break;
               }

               ++j;
            }
         }

         Arrays.fill(elements, uniques, n, (Object)null);
         if(uniques == 1) {
            E element = elements[0];
            return new SingletonImmutableSet(element, hashCode);
         } else if(tableSize != chooseTableSize(uniques)) {
            return construct(uniques, elements);
         } else {
            Object[] uniqueElements = uniques < elements.length?ObjectArrays.arraysCopyOf(elements, uniques):elements;
            return new RegularImmutableSet(uniqueElements, hashCode, table, mask);
         }
      }
   }

   @VisibleForTesting
   static int chooseTableSize(int setSize) {
      if(setSize >= 751619276) {
         Preconditions.checkArgument(setSize < 1073741824, "collection too large");
         return 1073741824;
      } else {
         int tableSize;
         for(tableSize = Integer.highestOneBit(setSize - 1) << 1; (double)tableSize * 0.7D < (double)setSize; tableSize <<= 1) {
            ;
         }

         return tableSize;
      }
   }

   public static ImmutableSet copyOf(Object[] elements) {
      switch(elements.length) {
      case 0:
         return of();
      case 1:
         return of(elements[0]);
      default:
         return construct(elements.length, (Object[])elements.clone());
      }
   }

   public static ImmutableSet copyOf(Iterable elements) {
      return elements instanceof Collection?copyOf(Collections2.cast(elements)):copyOf(elements.iterator());
   }

   public static ImmutableSet copyOf(Iterator elements) {
      if(!elements.hasNext()) {
         return of();
      } else {
         E first = elements.next();
         return !elements.hasNext()?of(first):(new ImmutableSet.Builder()).add(first).addAll(elements).build();
      }
   }

   public static ImmutableSet copyOf(Collection elements) {
      if(elements instanceof ImmutableSet && !(elements instanceof ImmutableSortedSet)) {
         ImmutableSet<E> set = (ImmutableSet)elements;
         if(!set.isPartialView()) {
            return set;
         }
      } else if(elements instanceof EnumSet) {
         return copyOfEnumSet((EnumSet)elements);
      }

      Object[] array = elements.toArray();
      return construct(array.length, array);
   }

   private static ImmutableSet copyOfEnumSet(EnumSet enumSet) {
      return ImmutableEnumSet.asImmutable(EnumSet.copyOf(enumSet));
   }

   boolean isHashCodeFast() {
      return false;
   }

   public boolean equals(@Nullable Object object) {
      return object == this?true:(object instanceof ImmutableSet && this.isHashCodeFast() && ((ImmutableSet)object).isHashCodeFast() && this.hashCode() != object.hashCode()?false:Sets.equalsImpl(this, object));
   }

   public int hashCode() {
      return Sets.hashCodeImpl(this);
   }

   public abstract UnmodifiableIterator iterator();

   Object writeReplace() {
      return new ImmutableSet.SerializedForm(this.toArray());
   }

   public static ImmutableSet.Builder builder() {
      return new ImmutableSet.Builder();
   }

   public static class Builder extends ImmutableCollection.ArrayBasedBuilder {
      public Builder() {
         this(4);
      }

      Builder(int capacity) {
         super(capacity);
      }

      public ImmutableSet.Builder add(Object element) {
         super.add(element);
         return this;
      }

      public ImmutableSet.Builder add(Object... elements) {
         super.add(elements);
         return this;
      }

      public ImmutableSet.Builder addAll(Iterable elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableSet.Builder addAll(Iterator elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableSet build() {
         ImmutableSet<E> result = ImmutableSet.construct(this.size, this.contents);
         this.size = result.size();
         return result;
      }
   }

   private static class SerializedForm implements Serializable {
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      SerializedForm(Object[] elements) {
         this.elements = elements;
      }

      Object readResolve() {
         return ImmutableSet.copyOf(this.elements);
      }
   }
}
