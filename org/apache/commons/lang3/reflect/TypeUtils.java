package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.reflect.Typed;

public class TypeUtils {
   public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(new Type[]{Object.class}).build();

   public static boolean isAssignable(Type type, Type toType) {
      return isAssignable(type, (Type)toType, (Map)null);
   }

   private static boolean isAssignable(Type type, Type toType, Map typeVarAssigns) {
      if(toType != null && !(toType instanceof Class)) {
         if(toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType)toType, typeVarAssigns);
         } else if(toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType)toType, typeVarAssigns);
         } else if(toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType)toType, typeVarAssigns);
         } else if(toType instanceof TypeVariable) {
            return isAssignable(type, (TypeVariable)toType, typeVarAssigns);
         } else {
            throw new IllegalStateException("found an unhandled type: " + toType);
         }
      } else {
         return isAssignable(type, (Class)toType);
      }
   }

   private static boolean isAssignable(Type type, Class toClass) {
      if(type == null) {
         return toClass == null || !toClass.isPrimitive();
      } else if(toClass == null) {
         return false;
      } else if(toClass.equals(type)) {
         return true;
      } else if(type instanceof Class) {
         return ClassUtils.isAssignable((Class)type, toClass);
      } else if(type instanceof ParameterizedType) {
         return isAssignable(getRawType((ParameterizedType)type), (Class)toClass);
      } else if(type instanceof TypeVariable) {
         for(Type bound : ((TypeVariable)type).getBounds()) {
            if(isAssignable(bound, toClass)) {
               return true;
            }
         }

         return false;
      } else if(!(type instanceof GenericArrayType)) {
         if(type instanceof WildcardType) {
            return false;
         } else {
            throw new IllegalStateException("found an unhandled type: " + type);
         }
      } else {
         return toClass.equals(Object.class) || toClass.isArray() && isAssignable(((GenericArrayType)type).getGenericComponentType(), toClass.getComponentType());
      }
   }

   private static boolean isAssignable(Type type, ParameterizedType toParameterizedType, Map typeVarAssigns) {
      if(type == null) {
         return true;
      } else if(toParameterizedType == null) {
         return false;
      } else if(toParameterizedType.equals(type)) {
         return true;
      } else {
         Class<?> toClass = getRawType(toParameterizedType);
         Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments((Type)type, toClass, (Map)null);
         if(fromTypeVarAssigns == null) {
            return false;
         } else if(fromTypeVarAssigns.isEmpty()) {
            return true;
         } else {
            Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, typeVarAssigns);

            for(TypeVariable<?> var : toTypeVarAssigns.keySet()) {
               Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
               Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
               if(fromTypeArg != null && !toTypeArg.equals(fromTypeArg) && (!(toTypeArg instanceof WildcardType) || !isAssignable(fromTypeArg, toTypeArg, typeVarAssigns))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static Type unrollVariableAssignments(TypeVariable var, Map typeVarAssigns) {
      while(true) {
         Type result = (Type)typeVarAssigns.get(var);
         if(!(result instanceof TypeVariable) || result.equals(var)) {
            return result;
         }

         var = (TypeVariable)result;
      }
   }

   private static boolean isAssignable(Type type, GenericArrayType toGenericArrayType, Map typeVarAssigns) {
      if(type == null) {
         return true;
      } else if(toGenericArrayType == null) {
         return false;
      } else if(toGenericArrayType.equals(type)) {
         return true;
      } else {
         Type toComponentType = toGenericArrayType.getGenericComponentType();
         if(!(type instanceof Class)) {
            if(type instanceof GenericArrayType) {
               return isAssignable(((GenericArrayType)type).getGenericComponentType(), toComponentType, typeVarAssigns);
            } else if(type instanceof WildcardType) {
               for(Type bound : getImplicitUpperBounds((WildcardType)type)) {
                  if(isAssignable(bound, (Type)toGenericArrayType)) {
                     return true;
                  }
               }

               return false;
            } else if(type instanceof TypeVariable) {
               for(Type bound : getImplicitBounds((TypeVariable)type)) {
                  if(isAssignable(bound, (Type)toGenericArrayType)) {
                     return true;
                  }
               }

               return false;
            } else if(type instanceof ParameterizedType) {
               return false;
            } else {
               throw new IllegalStateException("found an unhandled type: " + type);
            }
         } else {
            Class<?> cls = (Class)type;
            return cls.isArray() && isAssignable(cls.getComponentType(), (Type)toComponentType, typeVarAssigns);
         }
      }
   }

   private static boolean isAssignable(Type type, WildcardType toWildcardType, Map typeVarAssigns) {
      if(type == null) {
         return true;
      } else if(toWildcardType == null) {
         return false;
      } else if(toWildcardType.equals(type)) {
         return true;
      } else {
         Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
         Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
         if(!(type instanceof WildcardType)) {
            for(Type toBound : toUpperBounds) {
               if(!isAssignable(type, substituteTypeVariables(toBound, typeVarAssigns), typeVarAssigns)) {
                  return false;
               }
            }

            for(Type toBound : toLowerBounds) {
               if(!isAssignable(substituteTypeVariables(toBound, typeVarAssigns), type, typeVarAssigns)) {
                  return false;
               }
            }

            return true;
         } else {
            WildcardType wildcardType = (WildcardType)type;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType);

            for(Type toBound : toUpperBounds) {
               toBound = substituteTypeVariables(toBound, typeVarAssigns);

               for(Type bound : upperBounds) {
                  if(!isAssignable(bound, toBound, typeVarAssigns)) {
                     return false;
                  }
               }
            }

            for(Type toBound : toLowerBounds) {
               toBound = substituteTypeVariables(toBound, typeVarAssigns);

               for(Type bound : lowerBounds) {
                  if(!isAssignable(toBound, bound, typeVarAssigns)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   private static boolean isAssignable(Type type, TypeVariable toTypeVariable, Map typeVarAssigns) {
      if(type == null) {
         return true;
      } else if(toTypeVariable == null) {
         return false;
      } else if(toTypeVariable.equals(type)) {
         return true;
      } else {
         if(type instanceof TypeVariable) {
            Type[] bounds = getImplicitBounds((TypeVariable)type);

            for(Type bound : bounds) {
               if(isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                  return true;
               }
            }
         }

         if(!(type instanceof Class) && !(type instanceof ParameterizedType) && !(type instanceof GenericArrayType) && !(type instanceof WildcardType)) {
            throw new IllegalStateException("found an unhandled type: " + type);
         } else {
            return false;
         }
      }
   }

   private static Type substituteTypeVariables(Type type, Map typeVarAssigns) {
      if(type instanceof TypeVariable && typeVarAssigns != null) {
         Type replacementType = (Type)typeVarAssigns.get(type);
         if(replacementType == null) {
            throw new IllegalArgumentException("missing assignment type for type variable " + type);
         } else {
            return replacementType;
         }
      } else {
         return type;
      }
   }

   public static Map getTypeArguments(ParameterizedType type) {
      return getTypeArguments((ParameterizedType)type, getRawType(type), (Map)null);
   }

   public static Map getTypeArguments(Type type, Class toClass) {
      return getTypeArguments((Type)type, toClass, (Map)null);
   }

   private static Map getTypeArguments(Type type, Class toClass, Map subtypeVarAssigns) {
      if(type instanceof Class) {
         return getTypeArguments((Class)type, toClass, subtypeVarAssigns);
      } else if(type instanceof ParameterizedType) {
         return getTypeArguments((ParameterizedType)type, toClass, subtypeVarAssigns);
      } else if(type instanceof GenericArrayType) {
         return getTypeArguments(((GenericArrayType)type).getGenericComponentType(), toClass.isArray()?toClass.getComponentType():toClass, subtypeVarAssigns);
      } else if(type instanceof WildcardType) {
         for(Type bound : getImplicitUpperBounds((WildcardType)type)) {
            if(isAssignable(bound, toClass)) {
               return getTypeArguments(bound, toClass, subtypeVarAssigns);
            }
         }

         return null;
      } else if(type instanceof TypeVariable) {
         for(Type bound : getImplicitBounds((TypeVariable)type)) {
            if(isAssignable(bound, toClass)) {
               return getTypeArguments(bound, toClass, subtypeVarAssigns);
            }
         }

         return null;
      } else {
         throw new IllegalStateException("found an unhandled type: " + type);
      }
   }

   private static Map getTypeArguments(ParameterizedType parameterizedType, Class toClass, Map subtypeVarAssigns) {
      Class<?> cls = getRawType(parameterizedType);
      if(!isAssignable(cls, (Class)toClass)) {
         return null;
      } else {
         Type ownerType = parameterizedType.getOwnerType();
         Map<TypeVariable<?>, Type> typeVarAssigns;
         if(ownerType instanceof ParameterizedType) {
            ParameterizedType parameterizedOwnerType = (ParameterizedType)ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType, getRawType(parameterizedOwnerType), subtypeVarAssigns);
         } else {
            typeVarAssigns = subtypeVarAssigns == null?new HashMap():new HashMap(subtypeVarAssigns);
         }

         Type[] typeArgs = parameterizedType.getActualTypeArguments();
         TypeVariable<?>[] typeParams = cls.getTypeParameters();

         for(int i = 0; i < typeParams.length; ++i) {
            Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.containsKey(typeArg)?(Type)typeVarAssigns.get(typeArg):typeArg);
         }

         if(toClass.equals(cls)) {
            return typeVarAssigns;
         } else {
            return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
         }
      }
   }

   private static Map getTypeArguments(Class cls, Class toClass, Map subtypeVarAssigns) {
      if(!isAssignable(cls, (Class)toClass)) {
         return null;
      } else {
         if(cls.isPrimitive()) {
            if(toClass.isPrimitive()) {
               return new HashMap();
            }

            cls = ClassUtils.primitiveToWrapper(cls);
         }

         HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null?new HashMap():new HashMap(subtypeVarAssigns);
         return (Map)(toClass.equals(cls)?typeVarAssigns:getTypeArguments((Type)getClosestParentType(cls, toClass), toClass, typeVarAssigns));
      }
   }

   public static Map determineTypeArguments(Class cls, ParameterizedType superType) {
      Validate.notNull(cls, "cls is null", new Object[0]);
      Validate.notNull(superType, "superType is null", new Object[0]);
      Class<?> superClass = getRawType(superType);
      if(!isAssignable(cls, (Class)superClass)) {
         return null;
      } else if(cls.equals(superClass)) {
         return getTypeArguments((ParameterizedType)superType, superClass, (Map)null);
      } else {
         Type midType = getClosestParentType(cls, superClass);
         if(midType instanceof Class) {
            return determineTypeArguments((Class)midType, superType);
         } else {
            ParameterizedType midParameterizedType = (ParameterizedType)midType;
            Class<?> midClass = getRawType(midParameterizedType);
            Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superType);
            mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
            return typeVarAssigns;
         }
      }
   }

   private static void mapTypeVariablesToArguments(Class cls, ParameterizedType parameterizedType, Map typeVarAssigns) {
      Type ownerType = parameterizedType.getOwnerType();
      if(ownerType instanceof ParameterizedType) {
         mapTypeVariablesToArguments(cls, (ParameterizedType)ownerType, typeVarAssigns);
      }

      Type[] typeArgs = parameterizedType.getActualTypeArguments();
      TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
      List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());

      for(int i = 0; i < typeArgs.length; ++i) {
         TypeVariable<?> typeVar = typeVars[i];
         Type typeArg = typeArgs[i];
         if(typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
            typeVarAssigns.put((TypeVariable)typeArg, typeVarAssigns.get(typeVar));
         }
      }

   }

   private static Type getClosestParentType(Class cls, Class superClass) {
      if(superClass.isInterface()) {
         Type[] interfaceTypes = cls.getGenericInterfaces();
         Type genericInterface = null;

         for(Type midType : interfaceTypes) {
            Class<?> midClass = null;
            if(midType instanceof ParameterizedType) {
               midClass = getRawType((ParameterizedType)midType);
            } else {
               if(!(midType instanceof Class)) {
                  throw new IllegalStateException("Unexpected generic interface type found: " + midType);
               }

               midClass = (Class)midType;
            }

            if(isAssignable(midClass, (Class)superClass) && isAssignable(genericInterface, (Type)midClass)) {
               genericInterface = midType;
            }
         }

         if(genericInterface != null) {
            return genericInterface;
         }
      }

      return cls.getGenericSuperclass();
   }

   public static boolean isInstance(Object value, Type type) {
      return type == null?false:(value == null?!(type instanceof Class) || !((Class)type).isPrimitive():isAssignable(value.getClass(), (Type)type, (Map)null));
   }

   public static Type[] normalizeUpperBounds(Type[] bounds) {
      Validate.notNull(bounds, "null value specified for bounds array", new Object[0]);
      if(bounds.length < 2) {
         return bounds;
      } else {
         Set<Type> types = new HashSet(bounds.length);

         for(Type type1 : bounds) {
            boolean subtypeFound = false;

            for(Type type2 : bounds) {
               if(type1 != type2 && isAssignable(type2, (Type)type1, (Map)null)) {
                  subtypeFound = true;
                  break;
               }
            }

            if(!subtypeFound) {
               types.add(type1);
            }
         }

         return (Type[])types.toArray(new Type[types.size()]);
      }
   }

   public static Type[] getImplicitBounds(TypeVariable typeVariable) {
      Validate.notNull(typeVariable, "typeVariable is null", new Object[0]);
      Type[] bounds = typeVariable.getBounds();
      return bounds.length == 0?new Type[]{Object.class}:normalizeUpperBounds(bounds);
   }

   public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
      Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
      Type[] bounds = wildcardType.getUpperBounds();
      return bounds.length == 0?new Type[]{Object.class}:normalizeUpperBounds(bounds);
   }

   public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
      Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
      Type[] bounds = wildcardType.getLowerBounds();
      return bounds.length == 0?new Type[]{null}:bounds;
   }

   public static boolean typesSatisfyVariables(Map typeVarAssigns) {
      Validate.notNull(typeVarAssigns, "typeVarAssigns is null", new Object[0]);

      for(Entry<TypeVariable<?>, Type> entry : typeVarAssigns.entrySet()) {
         TypeVariable<?> typeVar = (TypeVariable)entry.getKey();
         Type type = (Type)entry.getValue();

         for(Type bound : getImplicitBounds(typeVar)) {
            if(!isAssignable(type, substituteTypeVariables(bound, typeVarAssigns), typeVarAssigns)) {
               return false;
            }
         }
      }

      return true;
   }

   private static Class getRawType(ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      if(!(rawType instanceof Class)) {
         throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
      } else {
         return (Class)rawType;
      }
   }

   public static Class getRawType(Type type, Type assigningType) {
      if(type instanceof Class) {
         return (Class)type;
      } else if(type instanceof ParameterizedType) {
         return getRawType((ParameterizedType)type);
      } else if(type instanceof TypeVariable) {
         if(assigningType == null) {
            return null;
         } else {
            Object genericDeclaration = ((TypeVariable)type).getGenericDeclaration();
            if(!(genericDeclaration instanceof Class)) {
               return null;
            } else {
               Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType, (Class)genericDeclaration);
               if(typeVarAssigns == null) {
                  return null;
               } else {
                  Type typeArgument = (Type)typeVarAssigns.get(type);
                  return typeArgument == null?null:getRawType(typeArgument, assigningType);
               }
            }
         }
      } else if(type instanceof GenericArrayType) {
         Class<?> rawComponentType = getRawType(((GenericArrayType)type).getGenericComponentType(), assigningType);
         return Array.newInstance(rawComponentType, 0).getClass();
      } else if(type instanceof WildcardType) {
         return null;
      } else {
         throw new IllegalArgumentException("unknown type: " + type);
      }
   }

   public static boolean isArrayType(Type type) {
      return type instanceof GenericArrayType || type instanceof Class && ((Class)type).isArray();
   }

   public static Type getArrayComponentType(Type type) {
      if(type instanceof Class) {
         Class<?> clazz = (Class)type;
         return clazz.isArray()?clazz.getComponentType():null;
      } else {
         return type instanceof GenericArrayType?((GenericArrayType)type).getGenericComponentType():null;
      }
   }

   public static Type unrollVariables(Map typeArguments, Type type) {
      if(typeArguments == null) {
         typeArguments = Collections.emptyMap();
      }

      if(containsTypeVariables(type)) {
         if(type instanceof TypeVariable) {
            return unrollVariables(typeArguments, (Type)typeArguments.get(type));
         }

         if(type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)type;
            Map<TypeVariable<?>, Type> parameterizedTypeArguments;
            if(p.getOwnerType() == null) {
               parameterizedTypeArguments = typeArguments;
            } else {
               parameterizedTypeArguments = new HashMap(typeArguments);
               parameterizedTypeArguments.putAll(getTypeArguments(p));
            }

            Type[] args = p.getActualTypeArguments();

            for(int i = 0; i < args.length; ++i) {
               Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
               if(unrolled != null) {
                  args[i] = unrolled;
               }
            }

            return parameterizeWithOwner(p.getOwnerType(), (Class)p.getRawType(), args);
         }

         if(type instanceof WildcardType) {
            WildcardType wild = (WildcardType)type;
            return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds())).withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
         }
      }

      return type;
   }

   private static Type[] unrollBounds(Map typeArguments, Type[] bounds) {
      Type[] result = bounds;

      for(int i = 0; i < result.length; ++i) {
         Type unrolled = unrollVariables(typeArguments, result[i]);
         if(unrolled == null) {
            result = (Type[])ArrayUtils.remove((Object[])result, i--);
         } else {
            result[i] = unrolled;
         }
      }

      return result;
   }

   public static boolean containsTypeVariables(Type type) {
      if(type instanceof TypeVariable) {
         return true;
      } else if(type instanceof Class) {
         return ((Class)type).getTypeParameters().length > 0;
      } else if(type instanceof ParameterizedType) {
         for(Type arg : ((ParameterizedType)type).getActualTypeArguments()) {
            if(containsTypeVariables(arg)) {
               return true;
            }
         }

         return false;
      } else if(!(type instanceof WildcardType)) {
         return false;
      } else {
         WildcardType wild = (WildcardType)type;
         return containsTypeVariables(getImplicitLowerBounds(wild)[0]) || containsTypeVariables(getImplicitUpperBounds(wild)[0]);
      }
   }

   public static final ParameterizedType parameterize(Class raw, Type... typeArguments) {
      return parameterizeWithOwner((Type)null, raw, (Type[])typeArguments);
   }

   public static final ParameterizedType parameterize(Class raw, Map typeArgMappings) {
      Validate.notNull(raw, "raw class is null", new Object[0]);
      Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
      return parameterizeWithOwner((Type)null, raw, (Type[])extractTypeArgumentsFrom(typeArgMappings, raw.getTypeParameters()));
   }

   public static final ParameterizedType parameterizeWithOwner(Type owner, Class raw, Type... typeArguments) {
      Validate.notNull(raw, "raw class is null", new Object[0]);
      Type useOwner;
      if(raw.getEnclosingClass() == null) {
         Validate.isTrue(owner == null, "no owner allowed for top-level %s", new Object[]{raw});
         useOwner = null;
      } else if(owner == null) {
         useOwner = raw.getEnclosingClass();
      } else {
         Validate.isTrue(isAssignable(owner, raw.getEnclosingClass()), "%s is invalid owner type for parameterized %s", new Object[]{owner, raw});
         useOwner = owner;
      }

      Validate.noNullElements((Object[])typeArguments, "null type argument at index %s", new Object[0]);
      Validate.isTrue(raw.getTypeParameters().length == typeArguments.length, "invalid number of type parameters specified: expected %s, got %s", new Object[]{Integer.valueOf(raw.getTypeParameters().length), Integer.valueOf(typeArguments.length)});
      return new TypeUtils.ParameterizedTypeImpl(raw, useOwner, typeArguments);
   }

   public static final ParameterizedType parameterizeWithOwner(Type owner, Class raw, Map typeArgMappings) {
      Validate.notNull(raw, "raw class is null", new Object[0]);
      Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
      return parameterizeWithOwner(owner, raw, extractTypeArgumentsFrom(typeArgMappings, raw.getTypeParameters()));
   }

   private static Type[] extractTypeArgumentsFrom(Map mappings, TypeVariable[] variables) {
      Type[] result = new Type[variables.length];
      int index = 0;

      for(TypeVariable<?> var : variables) {
         Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", new Object[]{toString(var)});
         result[index++] = (Type)mappings.get(var);
      }

      return result;
   }

   public static TypeUtils.WildcardTypeBuilder wildcardType() {
      return new TypeUtils.WildcardTypeBuilder();
   }

   public static GenericArrayType genericArrayType(Type componentType) {
      return new TypeUtils.GenericArrayTypeImpl((Type)Validate.notNull(componentType, "componentType is null", new Object[0]));
   }

   public static boolean equals(Type t1, Type t2) {
      return ObjectUtils.equals(t1, t2)?true:(t1 instanceof ParameterizedType?equals((ParameterizedType)t1, t2):(t1 instanceof GenericArrayType?equals((GenericArrayType)t1, t2):(t1 instanceof WildcardType?equals((WildcardType)t1, t2):false)));
   }

   private static boolean equals(ParameterizedType p, Type t) {
      if(t instanceof ParameterizedType) {
         ParameterizedType other = (ParameterizedType)t;
         if(equals(p.getRawType(), other.getRawType()) && equals(p.getOwnerType(), other.getOwnerType())) {
            return equals(p.getActualTypeArguments(), other.getActualTypeArguments());
         }
      }

      return false;
   }

   private static boolean equals(GenericArrayType a, Type t) {
      return t instanceof GenericArrayType && equals(a.getGenericComponentType(), ((GenericArrayType)t).getGenericComponentType());
   }

   private static boolean equals(WildcardType w, Type t) {
      if(!(t instanceof WildcardType)) {
         return true;
      } else {
         WildcardType other = (WildcardType)t;
         return equals(w.getLowerBounds(), other.getLowerBounds()) && equals(getImplicitUpperBounds(w), getImplicitUpperBounds(other));
      }
   }

   private static boolean equals(Type[] t1, Type[] t2) {
      if(t1.length == t2.length) {
         for(int i = 0; i < t1.length; ++i) {
            if(!equals(t1[i], t2[i])) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static String toString(Type type) {
      Validate.notNull(type);
      if(type instanceof Class) {
         return classToString((Class)type);
      } else if(type instanceof ParameterizedType) {
         return parameterizedTypeToString((ParameterizedType)type);
      } else if(type instanceof WildcardType) {
         return wildcardTypeToString((WildcardType)type);
      } else if(type instanceof TypeVariable) {
         return typeVariableToString((TypeVariable)type);
      } else if(type instanceof GenericArrayType) {
         return genericArrayTypeToString((GenericArrayType)type);
      } else {
         throw new IllegalArgumentException(ObjectUtils.identityToString(type));
      }
   }

   public static String toLongString(TypeVariable var) {
      Validate.notNull(var, "var is null", new Object[0]);
      StringBuilder buf = new StringBuilder();
      GenericDeclaration d = var.getGenericDeclaration();
      if(d instanceof Class) {
         Class<?> c;
         for(c = (Class)d; c.getEnclosingClass() != null; c = c.getEnclosingClass()) {
            buf.insert(0, c.getSimpleName()).insert(0, '.');
         }

         buf.insert(0, c.getName());
      } else if(d instanceof Type) {
         buf.append(toString((Type)d));
      } else {
         buf.append(d);
      }

      return buf.append(':').append(typeVariableToString(var)).toString();
   }

   public static Typed wrap(final Type type) {
      return new Typed() {
         public Type getType() {
            return type;
         }
      };
   }

   public static Typed wrap(Class type) {
      return wrap((Type)type);
   }

   private static String classToString(Class c) {
      StringBuilder buf = new StringBuilder();
      if(c.getEnclosingClass() != null) {
         buf.append(classToString(c.getEnclosingClass())).append('.').append(c.getSimpleName());
      } else {
         buf.append(c.getName());
      }

      if(c.getTypeParameters().length > 0) {
         buf.append('<');
         appendAllTo(buf, ", ", c.getTypeParameters());
         buf.append('>');
      }

      return buf.toString();
   }

   private static String typeVariableToString(TypeVariable v) {
      StringBuilder buf = new StringBuilder(v.getName());
      Type[] bounds = v.getBounds();
      if(bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
         buf.append(" extends ");
         appendAllTo(buf, " & ", v.getBounds());
      }

      return buf.toString();
   }

   private static String parameterizedTypeToString(ParameterizedType p) {
      StringBuilder buf = new StringBuilder();
      Type useOwner = p.getOwnerType();
      Class<?> raw = (Class)p.getRawType();
      Type[] typeArguments = p.getActualTypeArguments();
      if(useOwner == null) {
         buf.append(raw.getName());
      } else {
         if(useOwner instanceof Class) {
            buf.append(((Class)useOwner).getName());
         } else {
            buf.append(useOwner.toString());
         }

         buf.append('.').append(raw.getSimpleName());
      }

      appendAllTo(buf.append('<'), ", ", typeArguments).append('>');
      return buf.toString();
   }

   private static String wildcardTypeToString(WildcardType w) {
      StringBuilder buf = (new StringBuilder()).append('?');
      Type[] lowerBounds = w.getLowerBounds();
      Type[] upperBounds = w.getUpperBounds();
      if(lowerBounds.length > 0) {
         appendAllTo(buf.append(" super "), " & ", lowerBounds);
      } else if(upperBounds.length != 1 || !Object.class.equals(upperBounds[0])) {
         appendAllTo(buf.append(" extends "), " & ", upperBounds);
      }

      return buf.toString();
   }

   private static String genericArrayTypeToString(GenericArrayType g) {
      return String.format("%s[]", new Object[]{toString(g.getGenericComponentType())});
   }

   private static StringBuilder appendAllTo(StringBuilder buf, String sep, Type... types) {
      Validate.notEmpty(Validate.noNullElements((Object[])types));
      if(types.length > 0) {
         buf.append(toString(types[0]));

         for(int i = 1; i < types.length; ++i) {
            buf.append(sep).append(toString(types[i]));
         }
      }

      return buf;
   }

   private static final class GenericArrayTypeImpl implements GenericArrayType {
      private final Type componentType;

      private GenericArrayTypeImpl(Type componentType) {
         this.componentType = componentType;
      }

      public Type getGenericComponentType() {
         return this.componentType;
      }

      public String toString() {
         return TypeUtils.toString(this);
      }

      public boolean equals(Object obj) {
         return obj == this || obj instanceof GenericArrayType && TypeUtils.equals((GenericArrayType)this, (Type)((GenericArrayType)obj));
      }

      public int hashCode() {
         int result = 1072;
         result = result | this.componentType.hashCode();
         return result;
      }
   }

   private static final class ParameterizedTypeImpl implements ParameterizedType {
      private final Class raw;
      private final Type useOwner;
      private final Type[] typeArguments;

      private ParameterizedTypeImpl(Class raw, Type useOwner, Type[] typeArguments) {
         this.raw = raw;
         this.useOwner = useOwner;
         this.typeArguments = typeArguments;
      }

      public Type getRawType() {
         return this.raw;
      }

      public Type getOwnerType() {
         return this.useOwner;
      }

      public Type[] getActualTypeArguments() {
         return (Type[])this.typeArguments.clone();
      }

      public String toString() {
         return TypeUtils.toString(this);
      }

      public boolean equals(Object obj) {
         return obj == this || obj instanceof ParameterizedType && TypeUtils.equals((ParameterizedType)this, (Type)((ParameterizedType)obj));
      }

      public int hashCode() {
         int result = 1136;
         result = result | this.raw.hashCode();
         result = result << 4;
         result = result | ObjectUtils.hashCode(this.useOwner);
         result = result << 8;
         result = result | Arrays.hashCode(this.typeArguments);
         return result;
      }
   }

   public static class WildcardTypeBuilder implements Builder {
      private Type[] upperBounds;
      private Type[] lowerBounds;

      private WildcardTypeBuilder() {
      }

      public TypeUtils.WildcardTypeBuilder withUpperBounds(Type... bounds) {
         this.upperBounds = bounds;
         return this;
      }

      public TypeUtils.WildcardTypeBuilder withLowerBounds(Type... bounds) {
         this.lowerBounds = bounds;
         return this;
      }

      public WildcardType build() {
         return new TypeUtils.WildcardTypeImpl(this.upperBounds, this.lowerBounds);
      }
   }

   private static final class WildcardTypeImpl implements WildcardType {
      private static final Type[] EMPTY_BOUNDS = new Type[0];
      private final Type[] upperBounds;
      private final Type[] lowerBounds;

      private WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
         this.upperBounds = (Type[])ObjectUtils.defaultIfNull(upperBounds, EMPTY_BOUNDS);
         this.lowerBounds = (Type[])ObjectUtils.defaultIfNull(lowerBounds, EMPTY_BOUNDS);
      }

      public Type[] getUpperBounds() {
         return (Type[])this.upperBounds.clone();
      }

      public Type[] getLowerBounds() {
         return (Type[])this.lowerBounds.clone();
      }

      public String toString() {
         return TypeUtils.toString(this);
      }

      public boolean equals(Object obj) {
         return obj == this || obj instanceof WildcardType && TypeUtils.equals((WildcardType)this, (Type)((WildcardType)obj));
      }

      public int hashCode() {
         int result = 18688;
         result = result | Arrays.hashCode(this.upperBounds);
         result = result << 8;
         result = result | Arrays.hashCode(this.lowerBounds);
         return result;
      }
   }
}
