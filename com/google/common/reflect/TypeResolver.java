package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeVisitor;
import com.google.common.reflect.Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@Beta
public final class TypeResolver {
   private final TypeResolver.TypeTable typeTable;

   public TypeResolver() {
      this.typeTable = new TypeResolver.TypeTable();
   }

   private TypeResolver(TypeResolver.TypeTable typeTable) {
      this.typeTable = typeTable;
   }

   static TypeResolver accordingTo(Type type) {
      return (new TypeResolver()).where(TypeResolver.TypeMappingIntrospector.getTypeMappings(type));
   }

   public TypeResolver where(Type formal, Type actual) {
      Map<TypeResolver.TypeVariableKey, Type> mappings = Maps.newHashMap();
      populateTypeMappings(mappings, (Type)Preconditions.checkNotNull(formal), (Type)Preconditions.checkNotNull(actual));
      return this.where(mappings);
   }

   TypeResolver where(Map mappings) {
      return new TypeResolver(this.typeTable.where(mappings));
   }

   private static void populateTypeMappings(final Map mappings, Type from, final Type to) {
      if(!from.equals(to)) {
         (new TypeVisitor() {
            void visitTypeVariable(TypeVariable typeVariable) {
               mappings.put(new TypeResolver.TypeVariableKey(typeVariable), to);
            }

            void visitWildcardType(WildcardType fromWildcardType) {
               WildcardType toWildcardType = (WildcardType)TypeResolver.expectArgument(WildcardType.class, to);
               Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
               Type[] toUpperBounds = toWildcardType.getUpperBounds();
               Type[] fromLowerBounds = fromWildcardType.getLowerBounds();
               Type[] toLowerBounds = toWildcardType.getLowerBounds();
               Preconditions.checkArgument(fromUpperBounds.length == toUpperBounds.length && fromLowerBounds.length == toLowerBounds.length, "Incompatible type: %s vs. %s", new Object[]{fromWildcardType, to});

               for(int i = 0; i < fromUpperBounds.length; ++i) {
                  TypeResolver.populateTypeMappings(mappings, fromUpperBounds[i], toUpperBounds[i]);
               }

               for(int i = 0; i < fromLowerBounds.length; ++i) {
                  TypeResolver.populateTypeMappings(mappings, fromLowerBounds[i], toLowerBounds[i]);
               }

            }

            void visitParameterizedType(ParameterizedType fromParameterizedType) {
               ParameterizedType toParameterizedType = (ParameterizedType)TypeResolver.expectArgument(ParameterizedType.class, to);
               Preconditions.checkArgument(fromParameterizedType.getRawType().equals(toParameterizedType.getRawType()), "Inconsistent raw type: %s vs. %s", new Object[]{fromParameterizedType, to});
               Type[] fromArgs = fromParameterizedType.getActualTypeArguments();
               Type[] toArgs = toParameterizedType.getActualTypeArguments();
               Preconditions.checkArgument(fromArgs.length == toArgs.length, "%s not compatible with %s", new Object[]{fromParameterizedType, toParameterizedType});

               for(int i = 0; i < fromArgs.length; ++i) {
                  TypeResolver.populateTypeMappings(mappings, fromArgs[i], toArgs[i]);
               }

            }

            void visitGenericArrayType(GenericArrayType fromArrayType) {
               Type componentType = Types.getComponentType(to);
               Preconditions.checkArgument(componentType != null, "%s is not an array type.", new Object[]{to});
               TypeResolver.populateTypeMappings(mappings, fromArrayType.getGenericComponentType(), componentType);
            }

            void visitClass(Class fromClass) {
               throw new IllegalArgumentException("No type mapping from " + fromClass);
            }
         }).visit(new Type[]{from});
      }
   }

   public Type resolveType(Type type) {
      Preconditions.checkNotNull(type);
      return (Type)(type instanceof TypeVariable?this.typeTable.resolve((TypeVariable)type):(type instanceof ParameterizedType?this.resolveParameterizedType((ParameterizedType)type):(type instanceof GenericArrayType?this.resolveGenericArrayType((GenericArrayType)type):(type instanceof WildcardType?this.resolveWildcardType((WildcardType)type):type))));
   }

   private Type[] resolveTypes(Type[] types) {
      Type[] result = new Type[types.length];

      for(int i = 0; i < types.length; ++i) {
         result[i] = this.resolveType(types[i]);
      }

      return result;
   }

   private WildcardType resolveWildcardType(WildcardType type) {
      Type[] lowerBounds = type.getLowerBounds();
      Type[] upperBounds = type.getUpperBounds();
      return new Types.WildcardTypeImpl(this.resolveTypes(lowerBounds), this.resolveTypes(upperBounds));
   }

   private Type resolveGenericArrayType(GenericArrayType type) {
      Type componentType = type.getGenericComponentType();
      Type resolvedComponentType = this.resolveType(componentType);
      return Types.newArrayType(resolvedComponentType);
   }

   private ParameterizedType resolveParameterizedType(ParameterizedType type) {
      Type owner = type.getOwnerType();
      Type resolvedOwner = owner == null?null:this.resolveType(owner);
      Type resolvedRawType = this.resolveType(type.getRawType());
      Type[] args = type.getActualTypeArguments();
      Type[] resolvedArgs = this.resolveTypes(args);
      return Types.newParameterizedTypeWithOwner(resolvedOwner, (Class)resolvedRawType, resolvedArgs);
   }

   private static Object expectArgument(Class type, Object arg) {
      try {
         return type.cast(arg);
      } catch (ClassCastException var3) {
         throw new IllegalArgumentException(arg + " is not a " + type.getSimpleName());
      }
   }

   private static final class TypeMappingIntrospector extends TypeVisitor {
      private static final TypeResolver.WildcardCapturer wildcardCapturer = new TypeResolver.WildcardCapturer();
      private final Map mappings = Maps.newHashMap();

      static ImmutableMap getTypeMappings(Type contextType) {
         TypeResolver.TypeMappingIntrospector introspector = new TypeResolver.TypeMappingIntrospector();
         introspector.visit(new Type[]{wildcardCapturer.capture(contextType)});
         return ImmutableMap.copyOf(introspector.mappings);
      }

      void visitClass(Class clazz) {
         this.visit(new Type[]{clazz.getGenericSuperclass()});
         this.visit(clazz.getGenericInterfaces());
      }

      void visitParameterizedType(ParameterizedType parameterizedType) {
         Class<?> rawClass = (Class)parameterizedType.getRawType();
         TypeVariable<?>[] vars = rawClass.getTypeParameters();
         Type[] typeArgs = parameterizedType.getActualTypeArguments();
         Preconditions.checkState(vars.length == typeArgs.length);

         for(int i = 0; i < vars.length; ++i) {
            this.map(new TypeResolver.TypeVariableKey(vars[i]), typeArgs[i]);
         }

         this.visit(new Type[]{rawClass});
         this.visit(new Type[]{parameterizedType.getOwnerType()});
      }

      void visitTypeVariable(TypeVariable t) {
         this.visit(t.getBounds());
      }

      void visitWildcardType(WildcardType t) {
         this.visit(t.getUpperBounds());
      }

      private void map(TypeResolver.TypeVariableKey var, Type arg) {
         if(!this.mappings.containsKey(var)) {
            for(Type t = arg; t != null; t = (Type)this.mappings.get(TypeResolver.TypeVariableKey.forLookup(t))) {
               if(var.equalsType(t)) {
                  for(Type x = arg; x != null; x = (Type)this.mappings.remove(TypeResolver.TypeVariableKey.forLookup(x))) {
                     ;
                  }

                  return;
               }
            }

            this.mappings.put(var, arg);
         }
      }
   }

   private static class TypeTable {
      private final ImmutableMap map;

      TypeTable() {
         this.map = ImmutableMap.of();
      }

      private TypeTable(ImmutableMap map) {
         this.map = map;
      }

      final TypeResolver.TypeTable where(Map mappings) {
         ImmutableMap.Builder<TypeResolver.TypeVariableKey, Type> builder = ImmutableMap.builder();
         builder.putAll(this.map);

         for(Entry<TypeResolver.TypeVariableKey, ? extends Type> mapping : mappings.entrySet()) {
            TypeResolver.TypeVariableKey variable = (TypeResolver.TypeVariableKey)mapping.getKey();
            Type type = (Type)mapping.getValue();
            Preconditions.checkArgument(!variable.equalsType(type), "Type variable %s bound to itself", new Object[]{variable});
            builder.put(variable, type);
         }

         return new TypeResolver.TypeTable(builder.build());
      }

      final Type resolve(final TypeVariable var) {
         TypeResolver.TypeTable guarded = new TypeResolver.TypeTable() {
            public Type resolveInternal(TypeVariable intermediateVar, TypeResolver.TypeTable forDependent) {
               return (Type)(intermediateVar.getGenericDeclaration().equals(var.getGenericDeclaration())?intermediateVar:TypeTable.this.resolveInternal(intermediateVar, forDependent));
            }
         };
         return this.resolveInternal(var, guarded);
      }

      Type resolveInternal(TypeVariable var, TypeResolver.TypeTable forDependants) {
         Type type = (Type)this.map.get(new TypeResolver.TypeVariableKey(var));
         if(type == null) {
            Type[] bounds = var.getBounds();
            if(bounds.length == 0) {
               return var;
            } else {
               Type[] resolvedBounds = (new TypeResolver(forDependants)).resolveTypes(bounds);
               return Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY && Arrays.equals(bounds, resolvedBounds)?var:Types.newArtificialTypeVariable(var.getGenericDeclaration(), var.getName(), resolvedBounds);
            }
         } else {
            return (new TypeResolver(forDependants)).resolveType(type);
         }
      }
   }

   static final class TypeVariableKey {
      private final TypeVariable var;

      TypeVariableKey(TypeVariable var) {
         this.var = (TypeVariable)Preconditions.checkNotNull(var);
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.var.getGenericDeclaration(), this.var.getName()});
      }

      public boolean equals(Object obj) {
         if(obj instanceof TypeResolver.TypeVariableKey) {
            TypeResolver.TypeVariableKey that = (TypeResolver.TypeVariableKey)obj;
            return this.equalsTypeVariable(that.var);
         } else {
            return false;
         }
      }

      public String toString() {
         return this.var.toString();
      }

      static Object forLookup(Type t) {
         return t instanceof TypeVariable?new TypeResolver.TypeVariableKey((TypeVariable)t):null;
      }

      boolean equalsType(Type type) {
         return type instanceof TypeVariable?this.equalsTypeVariable((TypeVariable)type):false;
      }

      private boolean equalsTypeVariable(TypeVariable that) {
         return this.var.getGenericDeclaration().equals(that.getGenericDeclaration()) && this.var.getName().equals(that.getName());
      }
   }

   private static final class WildcardCapturer {
      private final AtomicInteger id;

      private WildcardCapturer() {
         this.id = new AtomicInteger();
      }

      Type capture(Type type) {
         Preconditions.checkNotNull(type);
         if(type instanceof Class) {
            return type;
         } else if(type instanceof TypeVariable) {
            return type;
         } else if(type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)type;
            return Types.newArrayType(this.capture(arrayType.getGenericComponentType()));
         } else if(type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            return Types.newParameterizedTypeWithOwner(this.captureNullable(parameterizedType.getOwnerType()), (Class)parameterizedType.getRawType(), this.capture(parameterizedType.getActualTypeArguments()));
         } else if(type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if(lowerBounds.length == 0) {
               Type[] upperBounds = wildcardType.getUpperBounds();
               String name = "capture#" + this.id.incrementAndGet() + "-of ? extends " + Joiner.on('&').join((Object[])upperBounds);
               return Types.newArtificialTypeVariable(TypeResolver.WildcardCapturer.class, name, wildcardType.getUpperBounds());
            } else {
               return type;
            }
         } else {
            throw new AssertionError("must have been one of the known types");
         }
      }

      private Type captureNullable(@Nullable Type type) {
         return type == null?null:this.capture(type);
      }

      private Type[] capture(Type[] types) {
         Type[] result = new Type[types.length];

         for(int i = 0; i < types.length; ++i) {
            result[i] = this.capture(types[i]);
         }

         return result;
      }
   }
}
