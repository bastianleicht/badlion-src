package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeCapture;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

@Beta
public abstract class TypeParameter extends TypeCapture {
   final TypeVariable typeVariable;

   protected TypeParameter() {
      Type type = this.capture();
      Preconditions.checkArgument(type instanceof TypeVariable, "%s should be a type variable.", new Object[]{type});
      this.typeVariable = (TypeVariable)type;
   }

   public final int hashCode() {
      return this.typeVariable.hashCode();
   }

   public final boolean equals(@Nullable Object o) {
      if(o instanceof TypeParameter) {
         TypeParameter<?> that = (TypeParameter)o;
         return this.typeVariable.equals(that.typeVariable);
      } else {
         return false;
      }
   }

   public String toString() {
      return this.typeVariable.toString();
   }
}
