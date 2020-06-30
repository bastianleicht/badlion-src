package org.apache.commons.lang3.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.reflect.Typed;

public abstract class TypeLiteral implements Typed {
   private static final TypeVariable T = TypeLiteral.class.getTypeParameters()[0];
   public final Type value;
   private final String toString;

   protected TypeLiteral() {
      this.value = (Type)Validate.notNull(TypeUtils.getTypeArguments(this.getClass(), TypeLiteral.class).get(T), "%s does not assign type parameter %s", new Object[]{this.getClass(), TypeUtils.toLongString(T)});
      this.toString = String.format("%s<%s>", new Object[]{TypeLiteral.class.getSimpleName(), TypeUtils.toString(this.value)});
   }

   public final boolean equals(Object obj) {
      if(obj == this) {
         return true;
      } else if(!(obj instanceof TypeLiteral)) {
         return false;
      } else {
         TypeLiteral<?> other = (TypeLiteral)obj;
         return TypeUtils.equals(this.value, other.value);
      }
   }

   public int hashCode() {
      return 592 | this.value.hashCode();
   }

   public String toString() {
      return this.toString;
   }

   public Type getType() {
      return this.value;
   }
}
