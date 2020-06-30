package com.google.gson.reflect;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeToken {
   final Class rawType;
   final Type type;
   final int hashCode;

   protected TypeToken() {
      this.type = getSuperclassTypeParameter(this.getClass());
      this.rawType = $Gson$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   TypeToken(Type type) {
      this.type = $Gson$Types.canonicalize((Type)$Gson$Preconditions.checkNotNull(type));
      this.rawType = $Gson$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   static Type getSuperclassTypeParameter(Class subclass) {
      Type superclass = subclass.getGenericSuperclass();
      if(superclass instanceof Class) {
         throw new RuntimeException("Missing type parameter.");
      } else {
         ParameterizedType parameterized = (ParameterizedType)superclass;
         return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
      }
   }

   public final Class getRawType() {
      return this.rawType;
   }

   public final Type getType() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(Class cls) {
      return this.isAssignableFrom((Type)cls);
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(Type from) {
      if(from == null) {
         return false;
      } else if(this.type.equals(from)) {
         return true;
      } else if(this.type instanceof Class) {
         return this.rawType.isAssignableFrom($Gson$Types.getRawType(from));
      } else if(this.type instanceof ParameterizedType) {
         return isAssignableFrom(from, (ParameterizedType)this.type, new HashMap());
      } else if(!(this.type instanceof GenericArrayType)) {
         throw buildUnexpectedTypeError(this.type, new Class[]{Class.class, ParameterizedType.class, GenericArrayType.class});
      } else {
         return this.rawType.isAssignableFrom($Gson$Types.getRawType(from)) && isAssignableFrom(from, (GenericArrayType)this.type);
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(TypeToken token) {
      return this.isAssignableFrom(token.getType());
   }

   private static boolean isAssignableFrom(Type from, GenericArrayType to) {
      Type toGenericComponentType = to.getGenericComponentType();
      if(!(toGenericComponentType instanceof ParameterizedType)) {
         return true;
      } else {
         Type t = from;
         if(from instanceof GenericArrayType) {
            t = ((GenericArrayType)from).getGenericComponentType();
         } else if(from instanceof Class) {
            Class<?> classType;
            for(classType = (Class)from; classType.isArray(); classType = classType.getComponentType()) {
               ;
            }

            t = classType;
         }

         return isAssignableFrom(t, (ParameterizedType)toGenericComponentType, new HashMap());
      }
   }

   private static boolean isAssignableFrom(Type from, ParameterizedType to, Map typeVarMap) {
      if(from == null) {
         return false;
      } else if(to.equals(from)) {
         return true;
      } else {
         Class<?> clazz = $Gson$Types.getRawType(from);
         ParameterizedType ptype = null;
         if(from instanceof ParameterizedType) {
            ptype = (ParameterizedType)from;
         }

         if(ptype != null) {
            Type[] tArgs = ptype.getActualTypeArguments();
            TypeVariable<?>[] tParams = clazz.getTypeParameters();

            for(int i = 0; i < tArgs.length; ++i) {
               Type arg = tArgs[i];

               TypeVariable<?> var;
               TypeVariable<?> v;
               for(var = tParams[i]; arg instanceof TypeVariable; arg = (Type)typeVarMap.get(v.getName())) {
                  v = (TypeVariable)arg;
               }

               typeVarMap.put(var.getName(), arg);
            }

            if(typeEquals(ptype, to, typeVarMap)) {
               return true;
            }
         }

         for(Type itype : clazz.getGenericInterfaces()) {
            if(isAssignableFrom(itype, to, new HashMap(typeVarMap))) {
               return true;
            }
         }

         Type sType = clazz.getGenericSuperclass();
         return isAssignableFrom(sType, to, new HashMap(typeVarMap));
      }
   }

   private static boolean typeEquals(ParameterizedType from, ParameterizedType to, Map typeVarMap) {
      if(from.getRawType().equals(to.getRawType())) {
         Type[] fromArgs = from.getActualTypeArguments();
         Type[] toArgs = to.getActualTypeArguments();

         for(int i = 0; i < fromArgs.length; ++i) {
            if(!matches(fromArgs[i], toArgs[i], typeVarMap)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static AssertionError buildUnexpectedTypeError(Type token, Class... expected) {
      StringBuilder exceptionMessage = new StringBuilder("Unexpected type. Expected one of: ");

      for(Class<?> clazz : expected) {
         exceptionMessage.append(clazz.getName()).append(", ");
      }

      exceptionMessage.append("but got: ").append(token.getClass().getName()).append(", for type token: ").append(token.toString()).append('.');
      return new AssertionError(exceptionMessage.toString());
   }

   private static boolean matches(Type from, Type to, Map typeMap) {
      return to.equals(from) || from instanceof TypeVariable && to.equals(typeMap.get(((TypeVariable)from).getName()));
   }

   public final int hashCode() {
      return this.hashCode;
   }

   public final boolean equals(Object o) {
      return o instanceof TypeToken && $Gson$Types.equals(this.type, ((TypeToken)o).type);
   }

   public final String toString() {
      return $Gson$Types.typeToString(this.type);
   }

   public static TypeToken get(Type type) {
      return new TypeToken(type);
   }

   public static TypeToken get(Class type) {
      return new TypeToken(type);
   }
}
