package org.apache.commons.logging.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class WeakHashtable extends Hashtable {
   private static final long serialVersionUID = -1546036869799732453L;
   private static final int MAX_CHANGES_BEFORE_PURGE = 100;
   private static final int PARTIAL_PURGE_COUNT = 10;
   private final ReferenceQueue queue = new ReferenceQueue();
   private int changeCount = 0;

   public boolean containsKey(Object key) {
      WeakHashtable.Referenced referenced = new WeakHashtable.Referenced(key);
      return super.containsKey(referenced);
   }

   public Enumeration elements() {
      this.purge();
      return super.elements();
   }

   public Set entrySet() {
      this.purge();
      Set referencedEntries = super.entrySet();
      Set unreferencedEntries = new HashSet();

      for(java.util.Map.Entry entry : referencedEntries) {
         WeakHashtable.Referenced referencedKey = (WeakHashtable.Referenced)entry.getKey();
         Object key = referencedKey.getValue();
         Object value = entry.getValue();
         if(key != null) {
            WeakHashtable.Entry dereferencedEntry = new WeakHashtable.Entry(key, value);
            unreferencedEntries.add(dereferencedEntry);
         }
      }

      return unreferencedEntries;
   }

   public Object get(Object key) {
      WeakHashtable.Referenced referenceKey = new WeakHashtable.Referenced(key);
      return super.get(referenceKey);
   }

   public Enumeration keys() {
      this.purge();
      final Enumeration enumer = super.keys();
      return new Enumeration() {
         public boolean hasMoreElements() {
            return enumer.hasMoreElements();
         }

         public Object nextElement() {
            WeakHashtable.Referenced nextReference = (WeakHashtable.Referenced)enumer.nextElement();
            return nextReference.getValue();
         }
      };
   }

   public Set keySet() {
      this.purge();
      Set referencedKeys = super.keySet();
      Set unreferencedKeys = new HashSet();

      for(WeakHashtable.Referenced referenceKey : referencedKeys) {
         Object keyValue = referenceKey.getValue();
         if(keyValue != null) {
            unreferencedKeys.add(keyValue);
         }
      }

      return unreferencedKeys;
   }

   public synchronized Object put(Object key, Object value) {
      if(key == null) {
         throw new NullPointerException("Null keys are not allowed");
      } else if(value == null) {
         throw new NullPointerException("Null values are not allowed");
      } else {
         if(this.changeCount++ > 100) {
            this.purge();
            this.changeCount = 0;
         } else if(this.changeCount % 10 == 0) {
            this.purgeOne();
         }

         WeakHashtable.Referenced keyRef = new WeakHashtable.Referenced(key, this.queue);
         return super.put(keyRef, value);
      }
   }

   public void putAll(Map t) {
      if(t != null) {
         for(java.util.Map.Entry entry : t.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
         }
      }

   }

   public Collection values() {
      this.purge();
      return super.values();
   }

   public synchronized Object remove(Object key) {
      if(this.changeCount++ > 100) {
         this.purge();
         this.changeCount = 0;
      } else if(this.changeCount % 10 == 0) {
         this.purgeOne();
      }

      return super.remove(new WeakHashtable.Referenced(key));
   }

   public boolean isEmpty() {
      this.purge();
      return super.isEmpty();
   }

   public int size() {
      this.purge();
      return super.size();
   }

   public String toString() {
      this.purge();
      return super.toString();
   }

   protected void rehash() {
      this.purge();
      super.rehash();
   }

   private void purge() {
      List toRemove = new ArrayList();
      WeakHashtable.WeakKey key;
      synchronized(this.queue) {
         while((key = (WeakHashtable.WeakKey)this.queue.poll()) != null) {
            toRemove.add(key.getReferenced());
         }
      }

      int size = toRemove.size();

      for(int i = 0; i < size; ++i) {
         super.remove(toRemove.get(i));
      }

   }

   private void purgeOne() {
      synchronized(this.queue) {
         WeakHashtable.WeakKey key = (WeakHashtable.WeakKey)this.queue.poll();
         if(key != null) {
            super.remove(key.getReferenced());
         }

      }
   }

   private static final class Entry implements java.util.Map.Entry {
      private final Object key;
      private final Object value;

      private Entry(Object key, Object value) {
         this.key = key;
         this.value = value;
      }

      public boolean equals(Object o) {
         boolean result = false;
         if(o != null && o instanceof java.util.Map.Entry) {
            boolean var10000;
            label13: {
               label13: {
                  label13: {
                     java.util.Map.Entry entry = (java.util.Map.Entry)o;
                     if(this.getKey() == null) {
                        if(entry.getKey() != null) {
                           break label13;
                        }
                     } else if(!this.getKey().equals(entry.getKey())) {
                        break label13;
                     }

                     if(this.getValue() == null) {
                        if(entry.getValue() == null) {
                           break label13;
                        }
                     } else if(this.getValue().equals(entry.getValue())) {
                        break label13;
                     }
                  }

                  var10000 = false;
                  break label13;
               }

               var10000 = true;
            }

            result = var10000;
         }

         return result;
      }

      public int hashCode() {
         return (this.getKey() == null?0:this.getKey().hashCode()) ^ (this.getValue() == null?0:this.getValue().hashCode());
      }

      public Object setValue(Object value) {
         throw new UnsupportedOperationException("Entry.setValue is not supported.");
      }

      public Object getValue() {
         return this.value;
      }

      public Object getKey() {
         return this.key;
      }
   }

   private static final class Referenced {
      private final WeakReference reference;
      private final int hashCode;

      private Referenced(Object referant) {
         this.reference = new WeakReference(referant);
         this.hashCode = referant.hashCode();
      }

      private Referenced(Object key, ReferenceQueue queue) {
         this.reference = new WeakHashtable.WeakKey(key, queue, this);
         this.hashCode = key.hashCode();
      }

      public int hashCode() {
         return this.hashCode;
      }

      private Object getValue() {
         return this.reference.get();
      }

      public boolean equals(Object o) {
         boolean result = false;
         if(o instanceof WeakHashtable.Referenced) {
            WeakHashtable.Referenced otherKey = (WeakHashtable.Referenced)o;
            Object thisKeyValue = this.getValue();
            Object otherKeyValue = otherKey.getValue();
            if(thisKeyValue == null) {
               result = otherKeyValue == null;
               result = result && this.hashCode() == otherKey.hashCode();
            } else {
               result = thisKeyValue.equals(otherKeyValue);
            }
         }

         return result;
      }
   }

   private static final class WeakKey extends WeakReference {
      private final WeakHashtable.Referenced referenced;

      private WeakKey(Object key, ReferenceQueue queue, WeakHashtable.Referenced referenced) {
         super(key, queue);
         this.referenced = referenced;
      }

      private WeakHashtable.Referenced getReferenced() {
         return this.referenced;
      }
   }
}
