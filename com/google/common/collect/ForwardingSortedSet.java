package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSet extends ForwardingSet implements SortedSet {
   protected abstract SortedSet delegate();

   public Comparator comparator() {
      return this.delegate().comparator();
   }

   public Object first() {
      return this.delegate().first();
   }

   public SortedSet headSet(Object toElement) {
      return this.delegate().headSet(toElement);
   }

   public Object last() {
      return this.delegate().last();
   }

   public SortedSet subSet(Object fromElement, Object toElement) {
      return this.delegate().subSet(fromElement, toElement);
   }

   public SortedSet tailSet(Object fromElement) {
      return this.delegate().tailSet(fromElement);
   }

   private int unsafeCompare(Object o1, Object o2) {
      Comparator<? super E> comparator = this.comparator();
      return comparator == null?((Comparable)o1).compareTo(o2):comparator.compare(o1, o2);
   }

   @Beta
   protected boolean standardContains(@Nullable Object object) {
      try {
         Object ceiling = this.tailSet(object).first();
         return this.unsafeCompare(ceiling, object) == 0;
      } catch (ClassCastException var4) {
         return false;
      } catch (NoSuchElementException var5) {
         return false;
      } catch (NullPointerException var6) {
         return false;
      }
   }

   @Beta
   protected boolean standardRemove(@Nullable Object object) {
      try {
         Iterator<Object> iterator = this.tailSet(object).iterator();
         if(iterator.hasNext()) {
            Object ceiling = iterator.next();
            if(this.unsafeCompare(ceiling, object) == 0) {
               iterator.remove();
               return true;
            }
         }

         return false;
      } catch (ClassCastException var5) {
         return false;
      } catch (NullPointerException var6) {
         return false;
      }
   }

   @Beta
   protected SortedSet standardSubSet(Object fromElement, Object toElement) {
      return this.tailSet(fromElement).headSet(toElement);
   }
}
