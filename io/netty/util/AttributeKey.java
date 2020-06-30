package io.netty.util;

import io.netty.util.UniqueName;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;

public final class AttributeKey extends UniqueName {
   private static final ConcurrentMap names = PlatformDependent.newConcurrentHashMap();

   public static AttributeKey valueOf(String name) {
      return new AttributeKey(name);
   }

   /** @deprecated */
   @Deprecated
   public AttributeKey(String name) {
      super(names, name, new Object[0]);
   }
}
