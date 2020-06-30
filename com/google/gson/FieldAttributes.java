package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

public final class FieldAttributes {
   private final Field field;

   public FieldAttributes(Field f) {
      $Gson$Preconditions.checkNotNull(f);
      this.field = f;
   }

   public Class getDeclaringClass() {
      return this.field.getDeclaringClass();
   }

   public String getName() {
      return this.field.getName();
   }

   public Type getDeclaredType() {
      return this.field.getGenericType();
   }

   public Class getDeclaredClass() {
      return this.field.getType();
   }

   public Annotation getAnnotation(Class annotation) {
      return this.field.getAnnotation(annotation);
   }

   public Collection getAnnotations() {
      return Arrays.asList(this.field.getAnnotations());
   }

   public boolean hasModifier(int modifier) {
      return (this.field.getModifiers() & modifier) != 0;
   }

   Object get(Object instance) throws IllegalAccessException {
      return this.field.get(instance);
   }

   boolean isSynthetic() {
      return this.field.isSynthetic();
   }
}
