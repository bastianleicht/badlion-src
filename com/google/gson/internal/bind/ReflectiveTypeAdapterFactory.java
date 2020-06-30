package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.bind.TypeAdapterRuntimeTypeWrapper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;
   private final FieldNamingStrategy fieldNamingPolicy;
   private final Excluder excluder;

   public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder) {
      this.constructorConstructor = constructorConstructor;
      this.fieldNamingPolicy = fieldNamingPolicy;
      this.excluder = excluder;
   }

   public boolean excludeField(Field f, boolean serialize) {
      return !this.excluder.excludeClass(f.getType(), serialize) && !this.excluder.excludeField(f, serialize);
   }

   private String getFieldName(Field f) {
      SerializedName serializedName = (SerializedName)f.getAnnotation(SerializedName.class);
      return serializedName == null?this.fieldNamingPolicy.translateName(f):serializedName.value();
   }

   public TypeAdapter create(Gson gson, TypeToken type) {
      Class<? super T> raw = type.getRawType();
      if(!Object.class.isAssignableFrom(raw)) {
         return null;
      } else {
         ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
         return new ReflectiveTypeAdapterFactory.Adapter(constructor, this.getBoundFields(gson, type, raw));
      }
   }

   private ReflectiveTypeAdapterFactory.BoundField createBoundField(final Gson context, final Field field, final String name, final TypeToken fieldType, final boolean serialize, final boolean deserialize) {
      final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
      return new ReflectiveTypeAdapterFactory.BoundField(name, serialize, deserialize) {
         final TypeAdapter typeAdapter = context.getAdapter(fieldType);

         void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
            Object fieldValue = field.get(value);
            TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(context, this.typeAdapter, fieldType.getType());
            t.write(writer, fieldValue);
         }

         void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
            Object fieldValue = this.typeAdapter.read(reader);
            if(fieldValue != null || !isPrimitive) {
               field.set(value, fieldValue);
            }

         }
      };
   }

   private Map getBoundFields(Gson context, TypeToken type, Class raw) {
      Map<String, ReflectiveTypeAdapterFactory.BoundField> result = new LinkedHashMap();
      if(raw.isInterface()) {
         return result;
      } else {
         for(Type declaredType = type.getType(); raw != Object.class; raw = type.getRawType()) {
            Field[] fields = raw.getDeclaredFields();

            for(Field field : fields) {
               boolean serialize = this.excludeField(field, true);
               boolean deserialize = this.excludeField(field, false);
               if(serialize || deserialize) {
                  field.setAccessible(true);
                  Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                  ReflectiveTypeAdapterFactory.BoundField boundField = this.createBoundField(context, field, this.getFieldName(field), TypeToken.get(fieldType), serialize, deserialize);
                  ReflectiveTypeAdapterFactory.BoundField previous = (ReflectiveTypeAdapterFactory.BoundField)result.put(boundField.name, boundField);
                  if(previous != null) {
                     throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
                  }
               }
            }

            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
         }

         return result;
      }
   }

   public static final class Adapter extends TypeAdapter {
      private final ObjectConstructor constructor;
      private final Map boundFields;

      private Adapter(ObjectConstructor constructor, Map boundFields) {
         this.constructor = constructor;
         this.boundFields = boundFields;
      }

      public Object read(JsonReader in) throws IOException {
         if(in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            T instance = this.constructor.construct();

            try {
               in.beginObject();

               while(in.hasNext()) {
                  String name = in.nextName();
                  ReflectiveTypeAdapterFactory.BoundField field = (ReflectiveTypeAdapterFactory.BoundField)this.boundFields.get(name);
                  if(field != null && field.deserialized) {
                     field.read(in, instance);
                  } else {
                     in.skipValue();
                  }
               }
            } catch (IllegalStateException var5) {
               throw new JsonSyntaxException(var5);
            } catch (IllegalAccessException var6) {
               throw new AssertionError(var6);
            }

            in.endObject();
            return instance;
         }
      }

      public void write(JsonWriter out, Object value) throws IOException {
         if(value == null) {
            out.nullValue();
         } else {
            out.beginObject();

            try {
               for(ReflectiveTypeAdapterFactory.BoundField boundField : this.boundFields.values()) {
                  if(boundField.serialized) {
                     out.name(boundField.name);
                     boundField.write(out, value);
                  }
               }
            } catch (IllegalAccessException var5) {
               throw new AssertionError();
            }

            out.endObject();
         }
      }
   }

   abstract static class BoundField {
      final String name;
      final boolean serialized;
      final boolean deserialized;

      protected BoundField(String name, boolean serialized, boolean deserialized) {
         this.name = name;
         this.serialized = serialized;
         this.deserialized = deserialized;
      }

      abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

      abstract void read(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
   }
}
