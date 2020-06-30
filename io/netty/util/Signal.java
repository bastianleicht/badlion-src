package io.netty.util;

import io.netty.util.UniqueName;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;

public final class Signal extends Error {
   private static final long serialVersionUID = -221145131122459977L;
   private static final ConcurrentMap map = PlatformDependent.newConcurrentHashMap();
   private final UniqueName uname;

   public static Signal valueOf(String name) {
      return new Signal(name);
   }

   /** @deprecated */
   @Deprecated
   public Signal(String name) {
      super(name);
      this.uname = new UniqueName(map, name, new Object[0]);
   }

   public void expect(Signal signal) {
      if(this != signal) {
         throw new IllegalStateException("unexpected signal: " + signal);
      }
   }

   public Throwable initCause(Throwable cause) {
      return this;
   }

   public Throwable fillInStackTrace() {
      return this;
   }

   public String toString() {
      return this.uname.name();
   }
}
