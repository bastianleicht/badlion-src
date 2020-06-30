package joptsimple.internal;

import java.lang.reflect.Constructor;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;

class ConstructorInvokingValueConverter implements ValueConverter {
   private final Constructor ctor;

   ConstructorInvokingValueConverter(Constructor ctor) {
      this.ctor = ctor;
   }

   public Object convert(String value) {
      return Reflection.instantiate(this.ctor, new Object[]{value});
   }

   public Class valueType() {
      return this.ctor.getDeclaringClass();
   }

   public String valuePattern() {
      return null;
   }
}
