package com.google.common.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
abstract class TypeVisitor {
   private final Set visited = Sets.newHashSet();

   public final void visit(Type... types) {
      for(Type type : types) {
         if(type != null && this.visited.add(type)) {
            boolean succeeded = false;

            try {
               if(type instanceof TypeVariable) {
                  this.visitTypeVariable((TypeVariable)type);
               } else if(type instanceof WildcardType) {
                  this.visitWildcardType((WildcardType)type);
               } else if(type instanceof ParameterizedType) {
                  this.visitParameterizedType((ParameterizedType)type);
               } else if(type instanceof Class) {
                  this.visitClass((Class)type);
               } else {
                  if(!(type instanceof GenericArrayType)) {
                     throw new AssertionError("Unknown type: " + type);
                  }

                  this.visitGenericArrayType((GenericArrayType)type);
               }

               succeeded = true;
            } finally {
               if(!succeeded) {
                  this.visited.remove(type);
               }

            }
         }
      }

   }

   void visitClass(Class t) {
   }

   void visitGenericArrayType(GenericArrayType t) {
   }

   void visitParameterizedType(ParameterizedType t) {
   }

   void visitTypeVariable(TypeVariable t) {
   }

   void visitWildcardType(WildcardType t) {
   }
}
