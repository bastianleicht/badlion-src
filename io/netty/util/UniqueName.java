package io.netty.util;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/** @deprecated */
@Deprecated
public class UniqueName implements Comparable {
   private static final AtomicInteger nextId = new AtomicInteger();
   private final int id;
   private final String name;

   public UniqueName(ConcurrentMap map, String name, Object... args) {
      if(map == null) {
         throw new NullPointerException("map");
      } else if(name == null) {
         throw new NullPointerException("name");
      } else {
         if(args != null && args.length > 0) {
            this.validateArgs(args);
         }

         if(map.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new IllegalArgumentException(String.format("\'%s\' is already in use", new Object[]{name}));
         } else {
            this.id = nextId.incrementAndGet();
            this.name = name;
         }
      }
   }

   protected void validateArgs(Object... args) {
   }

   public final String name() {
      return this.name;
   }

   public final int id() {
      return this.id;
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final boolean equals(Object o) {
      return super.equals(o);
   }

   public int compareTo(UniqueName other) {
      if(this == other) {
         return 0;
      } else {
         int returnCode = this.name.compareTo(other.name);
         return returnCode != 0?returnCode:Integer.valueOf(this.id).compareTo(Integer.valueOf(other.id));
      }
   }

   public String toString() {
      return this.name();
   }
}
