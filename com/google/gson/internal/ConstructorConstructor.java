package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.UnsafeAllocator;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class ConstructorConstructor {
   private final Map instanceCreators;

   public ConstructorConstructor(Map instanceCreators) {
      this.instanceCreators = instanceCreators;
   }

   public ObjectConstructor get(TypeToken typeToken) {
      final Type type = typeToken.getType();
      Class<? super T> rawType = typeToken.getRawType();
      final InstanceCreator<T> typeCreator = (InstanceCreator)this.instanceCreators.get(type);
      if(typeCreator != null) {
         return new ObjectConstructor() {
            public Object construct() {
               return typeCreator.createInstance(type);
            }
         };
      } else {
         final InstanceCreator<T> rawTypeCreator = (InstanceCreator)this.instanceCreators.get(rawType);
         if(rawTypeCreator != null) {
            return new ObjectConstructor() {
               public Object construct() {
                  return rawTypeCreator.createInstance(type);
               }
            };
         } else {
            ObjectConstructor<T> defaultConstructor = this.newDefaultConstructor(rawType);
            if(defaultConstructor != null) {
               return defaultConstructor;
            } else {
               ObjectConstructor<T> defaultImplementation = this.newDefaultImplementationConstructor(type, rawType);
               return defaultImplementation != null?defaultImplementation:this.newUnsafeAllocator(type, rawType);
            }
         }
      }
   }

   private ObjectConstructor newDefaultConstructor(Class rawType) {
      try {
         final Constructor<? super T> constructor = rawType.getDeclaredConstructor(new Class[0]);
         if(!constructor.isAccessible()) {
            constructor.setAccessible(true);
         }

         return new ObjectConstructor() {
            public Object construct() {
               try {
                  Object[] args = null;
                  return constructor.newInstance(args);
               } catch (InstantiationException var2) {
                  throw new RuntimeException("Failed to invoke " + constructor + " with no args", var2);
               } catch (InvocationTargetException var3) {
                  throw new RuntimeException("Failed to invoke " + constructor + " with no args", var3.getTargetException());
               } catch (IllegalAccessException var4) {
                  throw new AssertionError(var4);
               }
            }
         };
      } catch (NoSuchMethodException var3) {
         return null;
      }
   }

   private ObjectConstructor newDefaultImplementationConstructor(final Type type, Class rawType) {
      return Collection.class.isAssignableFrom(rawType)?(SortedSet.class.isAssignableFrom(rawType)?new ObjectConstructor() {
         public Object construct() {
            return new TreeSet();
         }
      }:(EnumSet.class.isAssignableFrom(rawType)?new ObjectConstructor() {
         public Object construct() {
            if(type instanceof ParameterizedType) {
               Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
               if(elementType instanceof Class) {
                  return EnumSet.noneOf((Class)elementType);
               } else {
                  throw new JsonIOException("Invalid EnumSet type: " + type.toString());
               }
            } else {
               throw new JsonIOException("Invalid EnumSet type: " + type.toString());
            }
         }
      }:(Set.class.isAssignableFrom(rawType)?new ObjectConstructor() {
         public Object construct() {
            return new LinkedHashSet();
         }
      }:(Queue.class.isAssignableFrom(rawType)?new ObjectConstructor() {
         public Object construct() {
            return new LinkedList();
         }
      }:new ObjectConstructor() {
         public Object construct() {
            return new ArrayList();
         }
      })))):(Map.class.isAssignableFrom(rawType)?(SortedMap.class.isAssignableFrom(rawType)?new ObjectConstructor() {
         public Object construct() {
            return new TreeMap();
         }
      }:(type instanceof ParameterizedType && !String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType())?new ObjectConstructor() {
         public Object construct() {
            return new LinkedHashMap();
         }
      }:new ObjectConstructor() {
         public Object construct() {
            return new LinkedTreeMap();
         }
      })):null);
   }

   private ObjectConstructor newUnsafeAllocator(final Type type, final Class rawType) {
      return new ObjectConstructor() {
         private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

         public Object construct() {
            try {
               Object newInstance = this.unsafeAllocator.newInstance(rawType);
               return newInstance;
            } catch (Exception var2) {
               throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", var2);
            }
         }
      };
   }

   public String toString() {
      return this.instanceCreators.toString();
   }
}
