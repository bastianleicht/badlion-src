package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.TransformedListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public class LinkedListMultimap extends AbstractMultimap implements ListMultimap, Serializable {
   private transient LinkedListMultimap.Node head;
   private transient LinkedListMultimap.Node tail;
   private transient Map keyToKeyList;
   private transient int size;
   private transient int modCount;
   @GwtIncompatible("java serialization not supported")
   private static final long serialVersionUID = 0L;

   public static LinkedListMultimap create() {
      return new LinkedListMultimap();
   }

   public static LinkedListMultimap create(int expectedKeys) {
      return new LinkedListMultimap(expectedKeys);
   }

   public static LinkedListMultimap create(Multimap multimap) {
      return new LinkedListMultimap(multimap);
   }

   LinkedListMultimap() {
      this.keyToKeyList = Maps.newHashMap();
   }

   private LinkedListMultimap(int expectedKeys) {
      this.keyToKeyList = new HashMap(expectedKeys);
   }

   private LinkedListMultimap(Multimap multimap) {
      this(multimap.keySet().size());
      this.putAll(multimap);
   }

   private LinkedListMultimap.Node addNode(@Nullable Object key, @Nullable Object value, @Nullable LinkedListMultimap.Node nextSibling) {
      LinkedListMultimap.Node<K, V> node = new LinkedListMultimap.Node(key, value);
      if(this.head == null) {
         this.head = this.tail = node;
         this.keyToKeyList.put(key, new LinkedListMultimap.KeyList(node));
         ++this.modCount;
      } else if(nextSibling == null) {
         this.tail.next = node;
         node.previous = this.tail;
         this.tail = node;
         LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)this.keyToKeyList.get(key);
         if(keyList == null) {
            this.keyToKeyList.put(key, new LinkedListMultimap.KeyList(node));
            ++this.modCount;
         } else {
            ++keyList.count;
            LinkedListMultimap.Node<K, V> keyTail = keyList.tail;
            keyTail.nextSibling = node;
            node.previousSibling = keyTail;
            keyList.tail = node;
         }
      } else {
         LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)this.keyToKeyList.get(key);
         ++keyList.count;
         node.previous = nextSibling.previous;
         node.previousSibling = nextSibling.previousSibling;
         node.next = nextSibling;
         node.nextSibling = nextSibling;
         if(nextSibling.previousSibling == null) {
            ((LinkedListMultimap.KeyList)this.keyToKeyList.get(key)).head = node;
         } else {
            nextSibling.previousSibling.nextSibling = node;
         }

         if(nextSibling.previous == null) {
            this.head = node;
         } else {
            nextSibling.previous.next = node;
         }

         nextSibling.previous = node;
         nextSibling.previousSibling = node;
      }

      ++this.size;
      return node;
   }

   private void removeNode(LinkedListMultimap.Node node) {
      if(node.previous != null) {
         node.previous.next = node.next;
      } else {
         this.head = node.next;
      }

      if(node.next != null) {
         node.next.previous = node.previous;
      } else {
         this.tail = node.previous;
      }

      if(node.previousSibling == null && node.nextSibling == null) {
         LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)this.keyToKeyList.remove(node.key);
         keyList.count = 0;
         ++this.modCount;
      } else {
         LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)this.keyToKeyList.get(node.key);
         --keyList.count;
         if(node.previousSibling == null) {
            keyList.head = node.nextSibling;
         } else {
            node.previousSibling.nextSibling = node.nextSibling;
         }

         if(node.nextSibling == null) {
            keyList.tail = node.previousSibling;
         } else {
            node.nextSibling.previousSibling = node.previousSibling;
         }
      }

      --this.size;
   }

   private void removeAllNodes(@Nullable Object key) {
      Iterators.clear(new LinkedListMultimap.ValueForKeyIterator(key));
   }

   private static void checkElement(@Nullable Object node) {
      if(node == null) {
         throw new NoSuchElementException();
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.head == null;
   }

   public boolean containsKey(@Nullable Object key) {
      return this.keyToKeyList.containsKey(key);
   }

   public boolean containsValue(@Nullable Object value) {
      return this.values().contains(value);
   }

   public boolean put(@Nullable Object key, @Nullable Object value) {
      this.addNode(key, value, (LinkedListMultimap.Node)null);
      return true;
   }

   public List replaceValues(@Nullable Object key, Iterable values) {
      List<V> oldValues = this.getCopy(key);
      ListIterator<V> keyValues = new LinkedListMultimap.ValueForKeyIterator(key);
      Iterator<? extends V> newValues = values.iterator();

      while(((ListIterator)keyValues).hasNext() && newValues.hasNext()) {
         keyValues.next();
         keyValues.set(newValues.next());
      }

      while(((ListIterator)keyValues).hasNext()) {
         keyValues.next();
         keyValues.remove();
      }

      while(newValues.hasNext()) {
         keyValues.add(newValues.next());
      }

      return oldValues;
   }

   private List getCopy(@Nullable Object key) {
      return Collections.unmodifiableList(Lists.newArrayList((Iterator)(new LinkedListMultimap.ValueForKeyIterator(key))));
   }

   public List removeAll(@Nullable Object key) {
      List<V> oldValues = this.getCopy(key);
      this.removeAllNodes(key);
      return oldValues;
   }

   public void clear() {
      this.head = null;
      this.tail = null;
      this.keyToKeyList.clear();
      this.size = 0;
      ++this.modCount;
   }

   public List get(@Nullable final Object key) {
      return new AbstractSequentialList() {
         public int size() {
            LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)LinkedListMultimap.this.keyToKeyList.get(key);
            return keyList == null?0:keyList.count;
         }

         public ListIterator listIterator(int index) {
            return LinkedListMultimap.this.new ValueForKeyIterator(key, index);
         }
      };
   }

   Set createKeySet() {
      return new Sets.ImprovedAbstractSet() {
         public int size() {
            return LinkedListMultimap.this.keyToKeyList.size();
         }

         public Iterator iterator() {
            return LinkedListMultimap.this.new DistinctKeyIterator();
         }

         public boolean contains(Object key) {
            return LinkedListMultimap.this.containsKey(key);
         }

         public boolean remove(Object o) {
            return !LinkedListMultimap.this.removeAll(o).isEmpty();
         }
      };
   }

   public List values() {
      return (List)super.values();
   }

   List createValues() {
      return new AbstractSequentialList() {
         public int size() {
            return LinkedListMultimap.this.size;
         }

         public ListIterator listIterator(int index) {
            final LinkedListMultimap<K, V>.NodeIterator nodeItr = LinkedListMultimap.this.new NodeIterator(index);
            return new TransformedListIterator(nodeItr) {
               Object transform(Entry entry) {
                  return entry.getValue();
               }

               public void set(Object value) {
                  nodeItr.setValue(value);
               }
            };
         }
      };
   }

   public List entries() {
      return (List)super.entries();
   }

   List createEntries() {
      return new AbstractSequentialList() {
         public int size() {
            return LinkedListMultimap.this.size;
         }

         public ListIterator listIterator(int index) {
            return LinkedListMultimap.this.new NodeIterator(index);
         }
      };
   }

   Iterator entryIterator() {
      throw new AssertionError("should never be called");
   }

   Map createAsMap() {
      return new Multimaps.AsMap(this);
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeInt(this.size());

      for(Entry<K, V> entry : this.entries()) {
         stream.writeObject(entry.getKey());
         stream.writeObject(entry.getValue());
      }

   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.keyToKeyList = Maps.newLinkedHashMap();
      int size = stream.readInt();

      for(int i = 0; i < size; ++i) {
         K key = stream.readObject();
         V value = stream.readObject();
         this.put(key, value);
      }

   }

   private class DistinctKeyIterator implements Iterator {
      final Set seenKeys;
      LinkedListMultimap.Node next;
      LinkedListMultimap.Node current;
      int expectedModCount;

      private DistinctKeyIterator() {
         this.seenKeys = Sets.newHashSetWithExpectedSize(LinkedListMultimap.this.keySet().size());
         this.next = LinkedListMultimap.this.head;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }

      private void checkForConcurrentModification() {
         if(LinkedListMultimap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public boolean hasNext() {
         this.checkForConcurrentModification();
         return this.next != null;
      }

      public Object next() {
         this.checkForConcurrentModification();
         LinkedListMultimap.checkElement(this.next);
         this.current = this.next;
         this.seenKeys.add(this.current.key);

         while(true) {
            this.next = this.next.next;
            if(this.next == null || this.seenKeys.add(this.next.key)) {
               break;
            }
         }

         return this.current.key;
      }

      public void remove() {
         this.checkForConcurrentModification();
         CollectPreconditions.checkRemove(this.current != null);
         LinkedListMultimap.this.removeAllNodes(this.current.key);
         this.current = null;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }
   }

   private static class KeyList {
      LinkedListMultimap.Node head;
      LinkedListMultimap.Node tail;
      int count;

      KeyList(LinkedListMultimap.Node firstNode) {
         this.head = firstNode;
         this.tail = firstNode;
         firstNode.previousSibling = null;
         firstNode.nextSibling = null;
         this.count = 1;
      }
   }

   private static final class Node extends AbstractMapEntry {
      final Object key;
      Object value;
      LinkedListMultimap.Node next;
      LinkedListMultimap.Node previous;
      LinkedListMultimap.Node nextSibling;
      LinkedListMultimap.Node previousSibling;

      Node(@Nullable Object key, @Nullable Object value) {
         this.key = key;
         this.value = value;
      }

      public Object getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }

      public Object setValue(@Nullable Object newValue) {
         V result = this.value;
         this.value = newValue;
         return result;
      }
   }

   private class NodeIterator implements ListIterator {
      int nextIndex;
      LinkedListMultimap.Node next;
      LinkedListMultimap.Node current;
      LinkedListMultimap.Node previous;
      int expectedModCount;

      NodeIterator(int index) {
         this.expectedModCount = LinkedListMultimap.this.modCount;
         int size = LinkedListMultimap.this.size();
         Preconditions.checkPositionIndex(index, size);
         if(index >= size / 2) {
            this.previous = LinkedListMultimap.this.tail;
            this.nextIndex = size;

            while(index++ < size) {
               this.previous();
            }
         } else {
            this.next = LinkedListMultimap.this.head;

            while(index-- > 0) {
               this.next();
            }
         }

         this.current = null;
      }

      private void checkForConcurrentModification() {
         if(LinkedListMultimap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public boolean hasNext() {
         this.checkForConcurrentModification();
         return this.next != null;
      }

      public LinkedListMultimap.Node next() {
         this.checkForConcurrentModification();
         LinkedListMultimap.checkElement(this.next);
         this.previous = this.current = this.next;
         this.next = this.next.next;
         ++this.nextIndex;
         return this.current;
      }

      public void remove() {
         this.checkForConcurrentModification();
         CollectPreconditions.checkRemove(this.current != null);
         if(this.current != this.next) {
            this.previous = this.current.previous;
            --this.nextIndex;
         } else {
            this.next = this.current.next;
         }

         LinkedListMultimap.this.removeNode(this.current);
         this.current = null;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }

      public boolean hasPrevious() {
         this.checkForConcurrentModification();
         return this.previous != null;
      }

      public LinkedListMultimap.Node previous() {
         this.checkForConcurrentModification();
         LinkedListMultimap.checkElement(this.previous);
         this.next = this.current = this.previous;
         this.previous = this.previous.previous;
         --this.nextIndex;
         return this.current;
      }

      public int nextIndex() {
         return this.nextIndex;
      }

      public int previousIndex() {
         return this.nextIndex - 1;
      }

      public void set(Entry e) {
         throw new UnsupportedOperationException();
      }

      public void add(Entry e) {
         throw new UnsupportedOperationException();
      }

      void setValue(Object value) {
         Preconditions.checkState(this.current != null);
         this.current.value = value;
      }
   }

   private class ValueForKeyIterator implements ListIterator {
      final Object key;
      int nextIndex;
      LinkedListMultimap.Node next;
      LinkedListMultimap.Node current;
      LinkedListMultimap.Node previous;

      ValueForKeyIterator(@Nullable Object key) {
         this.key = key;
         LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)LinkedListMultimap.this.keyToKeyList.get(key);
         this.next = keyList == null?null:keyList.head;
      }

      public ValueForKeyIterator(@Nullable Object key, int index) {
         LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList)LinkedListMultimap.this.keyToKeyList.get(key);
         int size = keyList == null?0:keyList.count;
         Preconditions.checkPositionIndex(index, size);
         if(index >= size / 2) {
            this.previous = keyList == null?null:keyList.tail;
            this.nextIndex = size;

            while(index++ < size) {
               this.previous();
            }
         } else {
            this.next = keyList == null?null:keyList.head;

            while(index-- > 0) {
               this.next();
            }
         }

         this.key = key;
         this.current = null;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public Object next() {
         LinkedListMultimap.checkElement(this.next);
         this.previous = this.current = this.next;
         this.next = this.next.nextSibling;
         ++this.nextIndex;
         return this.current.value;
      }

      public boolean hasPrevious() {
         return this.previous != null;
      }

      public Object previous() {
         LinkedListMultimap.checkElement(this.previous);
         this.next = this.current = this.previous;
         this.previous = this.previous.previousSibling;
         --this.nextIndex;
         return this.current.value;
      }

      public int nextIndex() {
         return this.nextIndex;
      }

      public int previousIndex() {
         return this.nextIndex - 1;
      }

      public void remove() {
         CollectPreconditions.checkRemove(this.current != null);
         if(this.current != this.next) {
            this.previous = this.current.previousSibling;
            --this.nextIndex;
         } else {
            this.next = this.current.nextSibling;
         }

         LinkedListMultimap.this.removeNode(this.current);
         this.current = null;
      }

      public void set(Object value) {
         Preconditions.checkState(this.current != null);
         this.current.value = value;
      }

      public void add(Object value) {
         this.previous = LinkedListMultimap.this.addNode(this.key, value, this.next);
         ++this.nextIndex;
         this.current = null;
      }
   }
}
