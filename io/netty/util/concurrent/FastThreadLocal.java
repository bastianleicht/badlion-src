package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class FastThreadLocal {
   private static final int variablesToRemoveIndex = InternalThreadLocalMap.nextVariableIndex();
   private final int index = InternalThreadLocalMap.nextVariableIndex();

   public static void removeAll() {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
      if(threadLocalMap != null) {
         try {
            Object v = threadLocalMap.indexedVariable(variablesToRemoveIndex);
            if(v != null && v != InternalThreadLocalMap.UNSET) {
               Set<FastThreadLocal<?>> variablesToRemove = (Set)v;
               FastThreadLocal<?>[] variablesToRemoveArray = (FastThreadLocal[])variablesToRemove.toArray(new FastThreadLocal[variablesToRemove.size()]);

               for(FastThreadLocal<?> tlv : variablesToRemoveArray) {
                  tlv.remove(threadLocalMap);
               }
            }
         } finally {
            InternalThreadLocalMap.remove();
         }

      }
   }

   public static int size() {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
      return threadLocalMap == null?0:threadLocalMap.size();
   }

   public static void destroy() {
      InternalThreadLocalMap.destroy();
   }

   private static void addToVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal variable) {
      Object v = threadLocalMap.indexedVariable(variablesToRemoveIndex);
      Set<FastThreadLocal<?>> variablesToRemove;
      if(v != InternalThreadLocalMap.UNSET && v != null) {
         variablesToRemove = (Set)v;
      } else {
         variablesToRemove = Collections.newSetFromMap(new IdentityHashMap());
         threadLocalMap.setIndexedVariable(variablesToRemoveIndex, variablesToRemove);
      }

      variablesToRemove.add(variable);
   }

   private static void removeFromVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal variable) {
      Object v = threadLocalMap.indexedVariable(variablesToRemoveIndex);
      if(v != InternalThreadLocalMap.UNSET && v != null) {
         Set<FastThreadLocal<?>> variablesToRemove = (Set)v;
         variablesToRemove.remove(variable);
      }
   }

   public final Object get() {
      return this.get(InternalThreadLocalMap.get());
   }

   public final Object get(InternalThreadLocalMap threadLocalMap) {
      Object v = threadLocalMap.indexedVariable(this.index);
      return v != InternalThreadLocalMap.UNSET?v:this.initialize(threadLocalMap);
   }

   private Object initialize(InternalThreadLocalMap threadLocalMap) {
      V v = null;

      try {
         v = this.initialValue();
      } catch (Exception var4) {
         PlatformDependent.throwException(var4);
      }

      threadLocalMap.setIndexedVariable(this.index, v);
      addToVariablesToRemove(threadLocalMap, this);
      return v;
   }

   public final void set(Object value) {
      if(value != InternalThreadLocalMap.UNSET) {
         this.set(InternalThreadLocalMap.get(), value);
      } else {
         this.remove();
      }

   }

   public final void set(InternalThreadLocalMap threadLocalMap, Object value) {
      if(value != InternalThreadLocalMap.UNSET) {
         if(threadLocalMap.setIndexedVariable(this.index, value)) {
            addToVariablesToRemove(threadLocalMap, this);
         }
      } else {
         this.remove(threadLocalMap);
      }

   }

   public final boolean isSet() {
      return this.isSet(InternalThreadLocalMap.getIfSet());
   }

   public final boolean isSet(InternalThreadLocalMap threadLocalMap) {
      return threadLocalMap != null && threadLocalMap.isIndexedVariableSet(this.index);
   }

   public final void remove() {
      this.remove(InternalThreadLocalMap.getIfSet());
   }

   public final void remove(InternalThreadLocalMap threadLocalMap) {
      if(threadLocalMap != null) {
         Object v = threadLocalMap.removeIndexedVariable(this.index);
         removeFromVariablesToRemove(threadLocalMap, this);
         if(v != InternalThreadLocalMap.UNSET) {
            try {
               this.onRemoval(v);
            } catch (Exception var4) {
               PlatformDependent.throwException(var4);
            }
         }

      }
   }

   protected Object initialValue() throws Exception {
      return null;
   }

   protected void onRemoval(Object value) throws Exception {
   }
}
