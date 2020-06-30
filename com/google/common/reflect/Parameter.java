package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import javax.annotation.Nullable;

@Beta
public final class Parameter implements AnnotatedElement {
   private final Invokable declaration;
   private final int position;
   private final TypeToken type;
   private final ImmutableList annotations;

   Parameter(Invokable declaration, int position, TypeToken type, Annotation[] annotations) {
      this.declaration = declaration;
      this.position = position;
      this.type = type;
      this.annotations = ImmutableList.copyOf((Object[])annotations);
   }

   public TypeToken getType() {
      return this.type;
   }

   public Invokable getDeclaringInvokable() {
      return this.declaration;
   }

   public boolean isAnnotationPresent(Class annotationType) {
      return this.getAnnotation(annotationType) != null;
   }

   @Nullable
   public Annotation getAnnotation(Class annotationType) {
      Preconditions.checkNotNull(annotationType);

      for(Annotation annotation : this.annotations) {
         if(annotationType.isInstance(annotation)) {
            return (Annotation)annotationType.cast(annotation);
         }
      }

      return null;
   }

   public Annotation[] getAnnotations() {
      return this.getDeclaredAnnotations();
   }

   public Annotation[] getDeclaredAnnotations() {
      return (Annotation[])this.annotations.toArray(new Annotation[this.annotations.size()]);
   }

   public boolean equals(@Nullable Object obj) {
      if(!(obj instanceof Parameter)) {
         return false;
      } else {
         Parameter that = (Parameter)obj;
         return this.position == that.position && this.declaration.equals(that.declaration);
      }
   }

   public int hashCode() {
      return this.position;
   }

   public String toString() {
      return this.type + " arg" + this.position;
   }
}
