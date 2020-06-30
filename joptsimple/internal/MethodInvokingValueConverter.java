package joptsimple.internal;

import java.lang.reflect.Method;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;

class MethodInvokingValueConverter implements ValueConverter {
   private final Method method;
   private final Class clazz;

   MethodInvokingValueConverter(Method method, Class clazz) {
      this.method = method;
      this.clazz = clazz;
   }

   public Object convert(String value) {
      return this.clazz.cast(Reflection.invoke(this.method, new Object[]{value}));
   }

   public Class valueType() {
      return this.clazz;
   }

   public String valuePattern() {
      return null;
   }
}
