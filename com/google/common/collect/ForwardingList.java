package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingList extends ForwardingCollection implements List {
   protected abstract List delegate();

   public void add(int index, Object element) {
      this.delegate().add(index, element);
   }

   public boolean addAll(int index, Collection elements) {
      return this.delegate().addAll(index, elements);
   }

   public Object get(int index) {
      return this.delegate().get(index);
   }

   public int indexOf(Object element) {
      return this.delegate().indexOf(element);
   }

   public int lastIndexOf(Object element) {
      return this.delegate().lastIndexOf(element);
   }

   public ListIterator listIterator() {
      return this.delegate().listIterator();
   }

   public ListIterator listIterator(int index) {
      return this.delegate().listIterator(index);
   }

   public Object remove(int index) {
      return this.delegate().remove(index);
   }

   public Object set(int index, Object element) {
      return this.delegate().set(index, element);
   }

   public List subList(int fromIndex, int toIndex) {
      return this.delegate().subList(fromIndex, toIndex);
   }

   public boolean equals(@Nullable Object object) {
      return object == this || this.delegate().equals(object);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }

   protected boolean standardAdd(Object element) {
      this.add(this.size(), element);
      return true;
   }

   protected boolean standardAddAll(int index, Iterable elements) {
      return Lists.addAllImpl(this, index, elements);
   }

   protected int standardIndexOf(@Nullable Object element) {
      return Lists.indexOfImpl(this, element);
   }

   protected int standardLastIndexOf(@Nullable Object element) {
      return Lists.lastIndexOfImpl(this, element);
   }

   protected Iterator standardIterator() {
      return this.listIterator();
   }

   protected ListIterator standardListIterator() {
      return this.listIterator(0);
   }

   @Beta
   protected ListIterator standardListIterator(int start) {
      return Lists.listIteratorImpl(this, start);
   }

   @Beta
   protected List standardSubList(int fromIndex, int toIndex) {
      return Lists.subListImpl(this, fromIndex, toIndex);
   }

   @Beta
   protected boolean standardEquals(@Nullable Object object) {
      return Lists.equalsImpl(this, object);
   }

   @Beta
   protected int standardHashCode() {
      return Lists.hashCodeImpl(this);
   }
}
