package com.google.common.reflect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeCapture;
import com.google.common.reflect.TypeVisitor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

final class Types {
   private static final Function TYPE_TO_STRING = new Function() {
      public String apply(Type from) {
         return Types.toString(from);
      }
   };
   private static final Joiner COMMA_JOINER = Joiner.on(", ").useForNull("null");

   static Type newArrayType(Type componentType) {
      if(componentType instanceof WildcardType) {
         WildcardType wildcard = (WildcardType)componentType;
         Type[] lowerBounds = wildcard.getLowerBounds();
         Preconditions.checkArgument(lowerBounds.length <= 1, "Wildcard cannot have more than one lower bounds.");
         if(lowerBounds.length == 1) {
            return supertypeOf(newArrayType(lowerBounds[0]));
         } else {
            Type[] upperBounds = wildcard.getUpperBounds();
            Preconditions.checkArgument(upperBounds.length == 1, "Wildcard should have only one upper bound.");
            return subtypeOf(newArrayType(upperBounds[0]));
         }
      } else {
         return Types.JavaVersion.CURRENT.newArrayType(componentType);
      }
   }

   static ParameterizedType newParameterizedTypeWithOwner(@Nullable Type ownerType, Class rawType, Type... arguments) {
      if(ownerType == null) {
         return newParameterizedType(rawType, arguments);
      } else {
         Preconditions.checkNotNull(arguments);
         Preconditions.checkArgument(rawType.getEnclosingClass() != null, "Owner type for unenclosed %s", new Object[]{rawType});
         return new Types.ParameterizedTypeImpl(ownerType, rawType, arguments);
      }
   }

   static ParameterizedType newParameterizedType(Class rawType, Type... arguments) {
      return new Types.ParameterizedTypeImpl(Types.ClassOwnership.JVM_BEHAVIOR.getOwnerType(rawType), rawType, arguments);
   }

   static TypeVariable newArtificialTypeVariable(GenericDeclaration declaration, String name, Type... bounds) {
      return new Types.TypeVariableImpl(declaration, name, bounds.length == 0?new Type[]{Object.class}:bounds);
   }

   @VisibleForTesting
   static WildcardType subtypeOf(Type upperBound) {
      return new Types.WildcardTypeImpl(new Type[0], new Type[]{upperBound});
   }

   @VisibleForTesting
   static WildcardType supertypeOf(Type lowerBound) {
      return new Types.WildcardTypeImpl(new Type[]{lowerBound}, new Type[]{Object.class});
   }

   static String toString(Type type) {
      return type instanceof Class?((Class)type).getName():type.toString();
   }

   @Nullable
   static Type getComponentType(Type type) {
      Preconditions.checkNotNull(type);
      final AtomicReference<Type> result = new AtomicReference();
      (new TypeVisitor() {
         void visitTypeVariable(TypeVariable t) {
            result.set(Types.subtypeOfComponentType(t.getBounds()));
         }

         void visitWildcardType(WildcardType t) {
            result.set(Types.subtypeOfComponentType(t.getUpperBounds()));
         }

         void visitGenericArrayType(GenericArrayType t) {
            result.set(t.getGenericComponentType());
         }

         void visitClass(Class t) {
            result.set(t.getComponentType());
         }
      }).visit(new Type[]{type});
      return (Type)result.get();
   }

   @Nullable
   private static Type subtypeOfComponentType(Type[] bounds) {
      for(Type bound : bounds) {
         Type componentType = getComponentType(bound);
         if(componentType != null) {
            if(componentType instanceof Class) {
               Class<?> componentClass = (Class)componentType;
               if(componentClass.isPrimitive()) {
                  return componentClass;
               }
            }

            return subtypeOf(componentType);
         }
      }

      return null;
   }

   private static Type[] toArray(Collection types) {
      return (Type[])types.toArray(new Type[types.size()]);
   }

   private static Iterable filterUpperBounds(Iterable bounds) {
      return Iterables.filter(bounds, Predicates.not(Predicates.equalTo(Object.class)));
   }

   private static void disallowPrimitiveType(Type[] types, String usedAs) {
      for(Type type : types) {
         if(type instanceof Class) {
            Class<?> cls = (Class)type;
            Preconditions.checkArgument(!cls.isPrimitive(), "Primitive type \'%s\' used as %s", new Object[]{cls, usedAs});
         }
      }

   }

   static Class getArrayClass(Class componentType) {
      return Array.newInstance(componentType, 0).getClass();
   }

   private static enum ClassOwnership {
      OWNED_BY_ENCLOSING_CLASS {
         @Nullable
         Class getOwnerType(Class rawType) {
            return rawType.getEnclosingClass();
         }
      },
      LOCAL_CLASS_HAS_NO_OWNER {
         @Nullable
         Class getOwnerType(Class rawType) {
            return rawType.isLocalClass()?null:rawType.getEnclosingClass();
         }
      };

      static final Types.ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();

      private ClassOwnership() {
      }

      @Nullable
      abstract Class getOwnerType(Class var1);

      private static Types.ClassOwnership detectJvmBehavior() {
         Class<?> subclass = (new LocalClass() {
         }).getClass();
         ParameterizedType parameterizedType = (ParameterizedType)subclass.getGenericSuperclass();

         for(Types.ClassOwnership behavior : values()) {
            if(behavior.getOwnerType(LocalClass.class) == parameterizedType.getOwnerType()) {
               return behavior;
            }
         }

         throw new AssertionError();
      }
   }

   private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
      private final Type componentType;
      private static final long serialVersionUID = 0L;

      GenericArrayTypeImpl(Type componentType) {
         this.componentType = Types.JavaVersion.CURRENT.usedInGenericType(componentType);
      }

      public Type getGenericComponentType() {
         return this.componentType;
      }

      public String toString() {
         return Types.toString(this.componentType) + "[]";
      }

      public int hashCode() {
         return this.componentType.hashCode();
      }

      public boolean equals(Object obj) {
         if(obj instanceof GenericArrayType) {
            GenericArrayType that = (GenericArrayType)obj;
            return Objects.equal(this.getGenericComponentType(), that.getGenericComponentType());
         } else {
            return false;
         }
      }
   }

   static enum JavaVersion {
      JAVA6 {
         GenericArrayType newArrayType(Type componentType) {
            return new Types.GenericArrayTypeImpl(componentType);
         }

         Type usedInGenericType(Type type) {
            Preconditions.checkNotNull(type);
            if(type instanceof Class) {
               Class<?> cls = (Class)type;
               if(cls.isArray()) {
                  return new Types.GenericArrayTypeImpl(cls.getComponentType());
               }
            }

            return type;
         }
      },
      JAVA7 {
         Type newArrayType(Type componentType) {
            return (Type)(componentType instanceof Class?Types.getArrayClass((Class)componentType):new Types.GenericArrayTypeImpl(componentType));
         }

         Type usedInGenericType(Type type) {
            return (Type)Preconditions.checkNotNull(type);
         }
      };

      static final Types.JavaVersion CURRENT = (new TypeCapture() {
      }).capture() instanceof Class?JAVA7:JAVA6;

      private JavaVersion() {
      }

      abstract Type newArrayType(Type var1);

      abstract Type usedInGenericType(Type var1);

      final ImmutableList usedInGenericType(Type[] types) {
         ImmutableList.Builder<Type> builder = ImmutableList.builder();

         for(Type type : types) {
            builder.add((Object)this.usedInGenericType(type));
         }

         return builder.build();
      }
   }

   static final class NativeTypeVariableEquals {
      static final boolean NATIVE_TYPE_VARIABLE_ONLY = !Types.NativeTypeVariableEquals.class.getTypeParameters()[0].equals(Types.newArtificialTypeVariable(Types.NativeTypeVariableEquals.class, "X", new Type[0]));
   }

   private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
      private final Type ownerType;
      private final ImmutableList argumentsList;
      private final Class rawType;
      private static final long serialVersionUID = 0L;

      ParameterizedTypeImpl(@Nullable Type ownerType, Class rawType, Type[] typeArguments) {
         Preconditions.checkNotNull(rawType);
         Preconditions.checkArgument(typeArguments.length == rawType.getTypeParameters().length);
         Types.disallowPrimitiveType(typeArguments, "type parameter");
         this.ownerType = ownerType;
         this.rawType = rawType;
         this.argumentsList = Types.JavaVersion.CURRENT.usedInGenericType(typeArguments);
      }

      public Type[] getActualTypeArguments() {
         return Types.toArray(this.argumentsList);
      }

      public Type getRawType() {
         return this.rawType;
      }

      public Type getOwnerType() {
         return this.ownerType;
      }

      public String toString() {
         StringBuilder builder = new StringBuilder();
         if(this.ownerType != null) {
            builder.append(Types.toString(this.ownerType)).append('.');
         }

         builder.append(this.rawType.getName()).append('<').append(Types.COMMA_JOINER.join(Iterables.transform(this.argumentsList, Types.TYPE_TO_STRING))).append('>');
         return builder.toString();
      }

      public int hashCode() {
         return (this.ownerType == null?0:this.ownerType.hashCode()) ^ this.argumentsList.hashCode() ^ this.rawType.hashCode();
      }

      public boolean equals(Object other) {
         if(!(other instanceof ParameterizedType)) {
            return false;
         } else {
            ParameterizedType that = (ParameterizedType)other;
            return this.getRawType().equals(that.getRawType()) && Objects.equal(this.getOwnerType(), that.getOwnerType()) && Arrays.equals(this.getActualTypeArguments(), that.getActualTypeArguments());
         }
      }
   }

   private static final class TypeVariableImpl implements TypeVariable {
      private final GenericDeclaration genericDeclaration;
      private final String name;
      private final ImmutableList bounds;

      TypeVariableImpl(GenericDeclaration genericDeclaration, String name, Type[] bounds) {
         Types.disallowPrimitiveType(bounds, "bound for type variable");
         this.genericDeclaration = (GenericDeclaration)Preconditions.checkNotNull(genericDeclaration);
         this.name = (String)Preconditions.checkNotNull(name);
         this.bounds = ImmutableList.copyOf((Object[])bounds);
      }

      public Type[] getBounds() {
         return Types.toArray(this.bounds);
      }

      public GenericDeclaration getGenericDeclaration() {
         return this.genericDeclaration;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public int hashCode() {
         return this.genericDeclaration.hashCode() ^ this.name.hashCode();
      }

      public boolean equals(Object obj) {
         if(Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY) {
            if(!(obj instanceof Types.TypeVariableImpl)) {
               return false;
            } else {
               Types.TypeVariableImpl<?> that = (Types.TypeVariableImpl)obj;
               return this.name.equals(that.getName()) && this.genericDeclaration.equals(that.getGenericDeclaration()) && this.bounds.equals(that.bounds);
            }
         } else if(!(obj instanceof TypeVariable)) {
            return false;
         } else {
            TypeVariable<?> that = (TypeVariable)obj;
            return this.name.equals(that.getName()) && this.genericDeclaration.equals(that.getGenericDeclaration());
         }
      }
   }

   static final class WildcardTypeImpl implements WildcardType, Serializable {
      private final ImmutableList lowerBounds;
      private final ImmutableList upperBounds;
      private static final long serialVersionUID = 0L;

      WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
         Types.disallowPrimitiveType(lowerBounds, "lower bound for wildcard");
         Types.disallowPrimitiveType(upperBounds, "upper bound for wildcard");
         this.lowerBounds = Types.JavaVersion.CURRENT.usedInGenericType(lowerBounds);
         this.upperBounds = Types.JavaVersion.CURRENT.usedInGenericType(upperBounds);
      }

      public Type[] getLowerBounds() {
         return Types.toArray(this.lowerBounds);
      }

      public Type[] getUpperBounds() {
         return Types.toArray(this.upperBounds);
      }

      public boolean equals(Object obj) {
         if(!(obj instanceof WildcardType)) {
            return false;
         } else {
            WildcardType that = (WildcardType)obj;
            return this.lowerBounds.equals(Arrays.asList(that.getLowerBounds())) && this.upperBounds.equals(Arrays.asList(that.getUpperBounds()));
         }
      }

      public int hashCode() {
         return this.lowerBounds.hashCode() ^ this.upperBounds.hashCode();
      }

      public String toString() {
         StringBuilder builder = new StringBuilder("?");

         for(Type lowerBound : this.lowerBounds) {
            builder.append(" super ").append(Types.toString(lowerBound));
         }

         for(Type upperBound : Types.filterUpperBounds(this.upperBounds)) {
            builder.append(" extends ").append(Types.toString(upperBound));
         }

         return builder.toString();
      }
   }
}
