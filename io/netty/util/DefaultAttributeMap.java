package io.netty.util;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap implements AttributeMap {
   private static final AtomicReferenceFieldUpdater updater;
   private static final int BUCKET_SIZE = 4;
   private static final int MASK = 3;
   private volatile AtomicReferenceArray attributes;

   public Attribute attr(AttributeKey key) {
      if(key == null) {
         throw new NullPointerException("key");
      } else {
         AtomicReferenceArray<DefaultAttributeMap.DefaultAttribute<?>> attributes = this.attributes;
         if(attributes == null) {
            attributes = new AtomicReferenceArray(4);
            if(!updater.compareAndSet(this, (Object)null, attributes)) {
               attributes = this.attributes;
            }
         }

         int i = index(key);
         DefaultAttributeMap.DefaultAttribute<?> head = (DefaultAttributeMap.DefaultAttribute)attributes.get(i);
         if(head == null) {
            head = new DefaultAttributeMap.DefaultAttribute(key);
            if(attributes.compareAndSet(i, (Object)null, head)) {
               return head;
            }

            head = (DefaultAttributeMap.DefaultAttribute)attributes.get(i);
         }

         synchronized(head) {
            DefaultAttributeMap.DefaultAttribute<?> curr;
            DefaultAttributeMap.DefaultAttribute<?> next;
            for(curr = head; curr.removed || curr.key != key; curr = next) {
               next = curr.next;
               if(next == null) {
                  DefaultAttributeMap.DefaultAttribute<T> attr = new DefaultAttributeMap.DefaultAttribute(head, key);
                  curr.next = attr;
                  attr.prev = curr;
                  return attr;
               }
            }

            return curr;
         }
      }
   }

   private static int index(AttributeKey key) {
      return key.id() & 3;
   }

   static {
      AtomicReferenceFieldUpdater<DefaultAttributeMap, AtomicReferenceArray> referenceFieldUpdater = PlatformDependent.newAtomicReferenceFieldUpdater(DefaultAttributeMap.class, "attributes");
      if(referenceFieldUpdater == null) {
         referenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, AtomicReferenceArray.class, "attributes");
      }

      updater = referenceFieldUpdater;
   }

   private static final class DefaultAttribute extends AtomicReference implements Attribute {
      private static final long serialVersionUID = -2661411462200283011L;
      private final DefaultAttributeMap.DefaultAttribute head;
      private final AttributeKey key;
      private DefaultAttributeMap.DefaultAttribute prev;
      private DefaultAttributeMap.DefaultAttribute next;
      private volatile boolean removed;

      DefaultAttribute(DefaultAttributeMap.DefaultAttribute head, AttributeKey key) {
         this.head = head;
         this.key = key;
      }

      DefaultAttribute(AttributeKey key) {
         this.head = this;
         this.key = key;
      }

      public AttributeKey key() {
         return this.key;
      }

      public Object setIfAbsent(Object value) {
         while(true) {
            if(!this.compareAndSet((Object)null, value)) {
               T old = this.get();
               if(old == null) {
                  continue;
               }

               return old;
            }

            return null;
         }
      }

      public Object getAndRemove() {
         this.removed = true;
         T oldValue = this.getAndSet((Object)null);
         this.remove0();
         return oldValue;
      }

      public void remove() {
         this.removed = true;
         this.set((Object)null);
         this.remove0();
      }

      private void remove0() {
         synchronized(this.head) {
            if(this.prev != null) {
               this.prev.next = this.next;
               if(this.next != null) {
                  this.next.prev = this.prev;
               }
            }

         }
      }
   }
}
