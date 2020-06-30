package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {
   private static final double IGNORE_VERSIONS = -1.0D;
   public static final Excluder DEFAULT = new Excluder();
   private double version = -1.0D;
   private int modifiers = 136;
   private boolean serializeInnerClasses = true;
   private boolean requireExpose;
   private List serializationStrategies = Collections.emptyList();
   private List deserializationStrategies = Collections.emptyList();

   protected Excluder clone() {
      try {
         return (Excluder)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }

   public Excluder withVersion(double ignoreVersionsAfter) {
      Excluder result = this.clone();
      result.version = ignoreVersionsAfter;
      return result;
   }

   public Excluder withModifiers(int... modifiers) {
      Excluder result = this.clone();
      result.modifiers = 0;

      for(int modifier : modifiers) {
         result.modifiers |= modifier;
      }

      return result;
   }

   public Excluder disableInnerClassSerialization() {
      Excluder result = this.clone();
      result.serializeInnerClasses = false;
      return result;
   }

   public Excluder excludeFieldsWithoutExposeAnnotation() {
      Excluder result = this.clone();
      result.requireExpose = true;
      return result;
   }

   public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization) {
      Excluder result = this.clone();
      if(serialization) {
         result.serializationStrategies = new ArrayList(this.serializationStrategies);
         result.serializationStrategies.add(exclusionStrategy);
      }

      if(deserialization) {
         result.deserializationStrategies = new ArrayList(this.deserializationStrategies);
         result.deserializationStrategies.add(exclusionStrategy);
      }

      return result;
   }

   public TypeAdapter create(final Gson gson, final TypeToken type) {
      Class<?> rawType = type.getRawType();
      final boolean skipSerialize = this.excludeClass(rawType, true);
      final boolean skipDeserialize = this.excludeClass(rawType, false);
      return !skipSerialize && !skipDeserialize?null:new TypeAdapter() {
         private TypeAdapter delegate;

         public Object read(JsonReader in) throws IOException {
            if(skipDeserialize) {
               in.skipValue();
               return null;
            } else {
               return this.delegate().read(in);
            }
         }

         public void write(JsonWriter out, Object value) throws IOException {
            if(skipSerialize) {
               out.nullValue();
            } else {
               this.delegate().write(out, value);
            }
         }

         private TypeAdapter delegate() {
            TypeAdapter<T> d = this.delegate;
            return d != null?d:(this.delegate = gson.getDelegateAdapter(Excluder.this, type));
         }
      };
   }

   public boolean excludeField(Field field, boolean serialize) {
      if((this.modifiers & field.getModifiers()) != 0) {
         return true;
      } else if(this.version != -1.0D && !this.isValidVersion((Since)field.getAnnotation(Since.class), (Until)field.getAnnotation(Until.class))) {
         return true;
      } else if(field.isSynthetic()) {
         return true;
      } else {
         if(this.requireExpose) {
            Expose annotation = (Expose)field.getAnnotation(Expose.class);
            if(annotation == null) {
               return true;
            }

            if(serialize) {
               if(!annotation.serialize()) {
                  return true;
               }
            } else if(!annotation.deserialize()) {
               return true;
            }
         }

         if(!this.serializeInnerClasses && this.isInnerClass(field.getType())) {
            return true;
         } else if(this.isAnonymousOrLocal(field.getType())) {
            return true;
         } else {
            List<ExclusionStrategy> list = serialize?this.serializationStrategies:this.deserializationStrategies;
            if(!list.isEmpty()) {
               FieldAttributes fieldAttributes = new FieldAttributes(field);

               for(ExclusionStrategy exclusionStrategy : list) {
                  if(exclusionStrategy.shouldSkipField(fieldAttributes)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public boolean excludeClass(Class clazz, boolean serialize) {
      if(this.version != -1.0D && !this.isValidVersion((Since)clazz.getAnnotation(Since.class), (Until)clazz.getAnnotation(Until.class))) {
         return true;
      } else if(!this.serializeInnerClasses && this.isInnerClass(clazz)) {
         return true;
      } else if(this.isAnonymousOrLocal(clazz)) {
         return true;
      } else {
         for(ExclusionStrategy exclusionStrategy : serialize?this.serializationStrategies:this.deserializationStrategies) {
            if(exclusionStrategy.shouldSkipClass(clazz)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isAnonymousOrLocal(Class clazz) {
      return !Enum.class.isAssignableFrom(clazz) && (clazz.isAnonymousClass() || clazz.isLocalClass());
   }

   private boolean isInnerClass(Class clazz) {
      return clazz.isMemberClass() && !this.isStatic(clazz);
   }

   private boolean isStatic(Class clazz) {
      return (clazz.getModifiers() & 8) != 0;
   }

   private boolean isValidVersion(Since since, Until until) {
      return this.isValidSince(since) && this.isValidUntil(until);
   }

   private boolean isValidSince(Since annotation) {
      if(annotation != null) {
         double annotationVersion = annotation.value();
         if(annotationVersion > this.version) {
            return false;
         }
      }

      return true;
   }

   private boolean isValidUntil(Until annotation) {
      if(annotation != null) {
         double annotationVersion = annotation.value();
         if(annotationVersion <= this.version) {
            return false;
         }
      }

      return true;
   }
}
