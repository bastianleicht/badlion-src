package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public final class ConcurrentSet extends AbstractSet implements Serializable {
   private static final long serialVersionUID = -6761513279741915432L;
   private final ConcurrentMap map = PlatformDependent.newConcurrentHashMap();

   public int size() {
      return this.map.size();
   }

   public boolean contains(Object o) {
      return this.map.containsKey(o);
   }

   public boolean add(Object o) {
      return this.map.putIfAbsent(o, Boolean.TRUE) == null;
   }

   public boolean remove(Object o) {
      return this.map.remove(o) != null;
   }

   public void clear() {
      this.map.clear();
   }

   public Iterator iterator() {
      return this.map.keySet().iterator();
   }
}
