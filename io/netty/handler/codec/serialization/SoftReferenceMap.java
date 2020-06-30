package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ReferenceMap;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;

final class SoftReferenceMap extends ReferenceMap {
   SoftReferenceMap(Map delegate) {
      super(delegate);
   }

   Reference fold(Object value) {
      return new SoftReference(value);
   }
}
