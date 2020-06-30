package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.RegularImmutableMultiset;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableMultiset extends ImmutableCollection implements Multiset {
   private static final ImmutableMultiset EMPTY = new RegularImmutableMultiset(ImmutableMap.of(), 0);
   private transient ImmutableSet entrySet;

   public static ImmutableMultiset of() {
      return EMPTY;
   }

   public static ImmutableMultiset of(Object element) {
      return copyOfInternal(new Object[]{element});
   }

   public static ImmutableMultiset of(Object e1, Object e2) {
      return copyOfInternal(new Object[]{e1, e2});
   }

   public static ImmutableMultiset of(Object e1, Object e2, Object e3) {
      return copyOfInternal(new Object[]{e1, e2, e3});
   }

   public static ImmutableMultiset of(Object e1, Object e2, Object e3, Object e4) {
      return copyOfInternal(new Object[]{e1, e2, e3, e4});
   }

   public static ImmutableMultiset of(Object e1, Object e2, Object e3, Object e4, Object e5) {
      return copyOfInternal(new Object[]{e1, e2, e3, e4, e5});
   }

   public static ImmutableMultiset of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object... others) {
      return (new ImmutableMultiset.Builder()).add(e1).add(e2).add(e3).add(e4).add(e5).add(e6).add(others).build();
   }

   public static ImmutableMultiset copyOf(Object[] elements) {
      return copyOf((Iterable)Arrays.asList(elements));
   }

   public static ImmutableMultiset copyOf(Iterable elements) {
      if(elements instanceof ImmutableMultiset) {
         ImmutableMultiset<E> result = (ImmutableMultiset)elements;
         if(!result.isPartialView()) {
            return result;
         }
      }

      Multiset<? extends E> multiset = (Multiset)(elements instanceof Multiset?Multisets.cast(elements):LinkedHashMultiset.create(elements));
      return copyOfInternal(multiset);
   }

   private static ImmutableMultiset copyOfInternal(Object... elements) {
      return copyOf((Iterable)Arrays.asList(elements));
   }

   private static ImmutableMultiset copyOfInternal(Multiset multiset) {
      return copyFromEntries(multiset.entrySet());
   }

   static ImmutableMultiset copyFromEntries(Collection entries) {
      long size = 0L;
      ImmutableMap.Builder<E, Integer> builder = ImmutableMap.builder();

      for(Multiset.Entry<? extends E> entry : entries) {
         int count = entry.getCount();
         if(count > 0) {
            builder.put(entry.getElement(), Integer.valueOf(count));
            size += (long)count;
         }
      }

      if(size == 0L) {
         return of();
      } else {
         return new RegularImmutableMultiset(builder.build(), Ints.saturatedCast(size));
      }
   }

   public static ImmutableMultiset copyOf(Iterator elements) {
      Multiset<E> multiset = LinkedHashMultiset.create();
      Iterators.addAll(multiset, elements);
      return copyOfInternal(multiset);
   }

   public UnmodifiableIterator iterator() {
      final Iterator<Multiset.Entry<E>> entryIterator = this.entrySet().iterator();
      return new UnmodifiableIterator() {
         int remaining;
         Object element;

         public boolean hasNext() {
            return this.remaining > 0 || entryIterator.hasNext();
         }

         public Object next() {
            if(this.remaining <= 0) {
               Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
               this.element = entry.getElement();
               this.remaining = entry.getCount();
            }

            --this.remaining;
            return this.element;
         }
      };
   }

   public boolean contains(@Nullable Object object) {
      return this.count(object) > 0;
   }

   public boolean containsAll(Collection targets) {
      return this.elementSet().containsAll(targets);
   }

   /** @deprecated */
   @Deprecated
   public final int add(Object element, int occurrences) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final int remove(Object element, int occurrences) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final int setCount(Object element, int count) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final boolean setCount(Object element, int oldCount, int newCount) {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible("not present in emulated superclass")
   int copyIntoArray(Object[] dst, int offset) {
      for(Multiset.Entry<E> entry : this.entrySet()) {
         Arrays.fill(dst, offset, offset + entry.getCount(), entry.getElement());
         offset += entry.getCount();
      }

      return offset;
   }

   public boolean equals(@Nullable Object object) {
      return Multisets.equalsImpl(this, object);
   }

   public int hashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   public String toString() {
      return this.entrySet().toString();
   }

   public ImmutableSet entrySet() {
      ImmutableSet<Multiset.Entry<E>> es = this.entrySet;
      return es == null?(this.entrySet = this.createEntrySet()):es;
   }

   private final ImmutableSet createEntrySet() {
      return (ImmutableSet)(this.isEmpty()?ImmutableSet.of():new ImmutableMultiset.EntrySet());
   }

   abstract Multiset.Entry getEntry(int var1);

   Object writeReplace() {
      return new ImmutableMultiset.SerializedForm(this);
   }

   public static ImmutableMultiset.Builder builder() {
      return new ImmutableMultiset.Builder();
   }

   public static class Builder extends ImmutableCollection.Builder {
      final Multiset contents;

      public Builder() {
         this(LinkedHashMultiset.create());
      }

      Builder(Multiset contents) {
         this.contents = contents;
      }

      public ImmutableMultiset.Builder add(Object element) {
         this.contents.add(Preconditions.checkNotNull(element));
         return this;
      }

      public ImmutableMultiset.Builder addCopies(Object element, int occurrences) {
         this.contents.add(Preconditions.checkNotNull(element), occurrences);
         return this;
      }

      public ImmutableMultiset.Builder setCount(Object element, int count) {
         this.contents.setCount(Preconditions.checkNotNull(element), count);
         return this;
      }

      public ImmutableMultiset.Builder add(Object... elements) {
         super.add(elements);
         return this;
      }

      public ImmutableMultiset.Builder addAll(Iterable elements) {
         if(elements instanceof Multiset) {
            Multiset<? extends E> multiset = Multisets.cast(elements);

            for(Multiset.Entry<? extends E> entry : multiset.entrySet()) {
               this.addCopies(entry.getElement(), entry.getCount());
            }
         } else {
            super.addAll(elements);
         }

         return this;
      }

      public ImmutableMultiset.Builder addAll(Iterator elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableMultiset build() {
         return ImmutableMultiset.copyOf((Iterable)this.contents);
      }
   }

   private final class EntrySet extends ImmutableSet {
      private static final long serialVersionUID = 0L;

      private EntrySet() {
      }

      boolean isPartialView() {
         return ImmutableMultiset.this.isPartialView();
      }

      public UnmodifiableIterator iterator() {
         return this.asList().iterator();
      }

      ImmutableList createAsList() {
         return new ImmutableAsList() {
            public Multiset.Entry get(int index) {
               return ImmutableMultiset.this.getEntry(index);
            }

            ImmutableCollection delegateCollection() {
               return EntrySet.this;
            }
         };
      }

      public int size() {
         return ImmutableMultiset.this.elementSet().size();
      }

      public boolean contains(Object o) {
         if(o instanceof Multiset.Entry) {
            Multiset.Entry<?> entry = (Multiset.Entry)o;
            if(entry.getCount() <= 0) {
               return false;
            } else {
               int count = ImmutableMultiset.this.count(entry.getElement());
               return count == entry.getCount();
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return ImmutableMultiset.this.hashCode();
      }

      Object writeReplace() {
         return new ImmutableMultiset.EntrySetSerializedForm(ImmutableMultiset.this);
      }
   }

   static class EntrySetSerializedForm implements Serializable {
      final ImmutableMultiset multiset;

      EntrySetSerializedForm(ImmutableMultiset multiset) {
         this.multiset = multiset;
      }

      Object readResolve() {
         return this.multiset.entrySet();
      }
   }

   private static class SerializedForm implements Serializable {
      final Object[] elements;
      final int[] counts;
      private static final long serialVersionUID = 0L;

      SerializedForm(Multiset multiset) {
         int distinct = multiset.entrySet().size();
         this.elements = new Object[distinct];
         this.counts = new int[distinct];
         int i = 0;

         for(Multiset.Entry<?> entry : multiset.entrySet()) {
            this.elements[i] = entry.getElement();
            this.counts[i] = entry.getCount();
            ++i;
         }

      }

      Object readResolve() {
         LinkedHashMultiset<Object> multiset = LinkedHashMultiset.create(this.elements.length);

         for(int i = 0; i < this.elements.length; ++i) {
            multiset.add(this.elements[i], this.counts[i]);
         }

         return ImmutableMultiset.copyOf((Iterable)multiset);
      }
   }
}
