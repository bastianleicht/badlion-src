package com.google.common.collect;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MapConstraint;
import com.google.common.collect.MapConstraints;
import com.google.common.primitives.Primitives;
import java.util.HashMap;
import java.util.Map;

public final class MutableClassToInstanceMap extends MapConstraints.ConstrainedMap implements ClassToInstanceMap {
   private static final MapConstraint VALUE_CAN_BE_CAST_TO_KEY = new MapConstraint() {
      public void checkKeyValue(Class key, Object value) {
         MutableClassToInstanceMap.cast(key, value);
      }
   };
   private static final long serialVersionUID = 0L;

   public static MutableClassToInstanceMap create() {
      return new MutableClassToInstanceMap(new HashMap());
   }

   public static MutableClassToInstanceMap create(Map backingMap) {
      return new MutableClassToInstanceMap(backingMap);
   }

   private MutableClassToInstanceMap(Map delegate) {
      super(delegate, VALUE_CAN_BE_CAST_TO_KEY);
   }

   public Object putInstance(Class type, Object value) {
      return cast(type, this.put(type, value));
   }

   public Object getInstance(Class type) {
      return cast(type, this.get(type));
   }

   private static Object cast(Class type, Object value) {
      return Primitives.wrap(type).cast(value);
   }
}
