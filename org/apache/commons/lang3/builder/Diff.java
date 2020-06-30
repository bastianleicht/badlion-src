package org.apache.commons.lang3.builder;

import java.lang.reflect.Type;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.tuple.Pair;

public abstract class Diff extends Pair {
   private static final long serialVersionUID = 1L;
   private final Type type = (Type)ObjectUtils.defaultIfNull(TypeUtils.getTypeArguments(this.getClass(), Diff.class).get(Diff.class.getTypeParameters()[0]), Object.class);
   private final String fieldName;

   protected Diff(String fieldName) {
      this.fieldName = fieldName;
   }

   public final Type getType() {
      return this.type;
   }

   public final String getFieldName() {
      return this.fieldName;
   }

   public final String toString() {
      return String.format("[%s: %s, %s]", new Object[]{this.fieldName, this.getLeft(), this.getRight()});
   }

   public final Object setValue(Object value) {
      throw new UnsupportedOperationException("Cannot alter Diff object.");
   }
}
