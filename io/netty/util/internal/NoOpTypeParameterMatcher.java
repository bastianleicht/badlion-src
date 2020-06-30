package io.netty.util.internal;

import io.netty.util.internal.TypeParameterMatcher;

public final class NoOpTypeParameterMatcher extends TypeParameterMatcher {
   public boolean match(Object msg) {
      return true;
   }
}
