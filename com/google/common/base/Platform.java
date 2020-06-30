package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.CharMatcher;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import java.lang.ref.WeakReference;

@GwtCompatible(
   emulated = true
)
final class Platform {
   static long systemNanoTime() {
      return System.nanoTime();
   }

   static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
      return matcher.precomputedInternal();
   }

   static Optional getEnumIfPresent(Class enumClass, String value) {
      WeakReference<? extends Enum<?>> ref = (WeakReference)Enums.getEnumConstants(enumClass).get(value);
      return ref == null?Optional.absent():Optional.of(enumClass.cast(ref.get()));
   }
}
