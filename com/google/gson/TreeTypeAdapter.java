package com.google.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

final class TreeTypeAdapter extends TypeAdapter {
   private final JsonSerializer serializer;
   private final JsonDeserializer deserializer;
   private final Gson gson;
   private final TypeToken typeToken;
   private final TypeAdapterFactory skipPast;
   private TypeAdapter delegate;

   private TreeTypeAdapter(JsonSerializer serializer, JsonDeserializer deserializer, Gson gson, TypeToken typeToken, TypeAdapterFactory skipPast) {
      this.serializer = serializer;
      this.deserializer = deserializer;
      this.gson = gson;
      this.typeToken = typeToken;
      this.skipPast = skipPast;
   }

   public Object read(JsonReader in) throws IOException {
      if(this.deserializer == null) {
         return this.delegate().read(in);
      } else {
         JsonElement value = Streams.parse(in);
         return value.isJsonNull()?null:this.deserializer.deserialize(value, this.typeToken.getType(), this.gson.deserializationContext);
      }
   }

   public void write(JsonWriter out, Object value) throws IOException {
      if(this.serializer == null) {
         this.delegate().write(out, value);
      } else if(value == null) {
         out.nullValue();
      } else {
         JsonElement tree = this.serializer.serialize(value, this.typeToken.getType(), this.gson.serializationContext);
         Streams.write(tree, out);
      }
   }

   private TypeAdapter delegate() {
      TypeAdapter<T> d = this.delegate;
      return d != null?d:(this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
   }

   public static TypeAdapterFactory newFactory(TypeToken exactType, Object typeAdapter) {
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, exactType, false, (Class)null);
   }

   public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken exactType, Object typeAdapter) {
      boolean matchRawType = exactType.getType() == exactType.getRawType();
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, exactType, matchRawType, (Class)null);
   }

   public static TypeAdapterFactory newTypeHierarchyFactory(Class hierarchyType, Object typeAdapter) {
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, (TypeToken)null, false, hierarchyType);
   }

   private static class SingleTypeFactory implements TypeAdapterFactory {
      private final TypeToken exactType;
      private final boolean matchRawType;
      private final Class hierarchyType;
      private final JsonSerializer serializer;
      private final JsonDeserializer deserializer;

      private SingleTypeFactory(Object typeAdapter, TypeToken exactType, boolean matchRawType, Class hierarchyType) {
         this.serializer = typeAdapter instanceof JsonSerializer?(JsonSerializer)typeAdapter:null;
         this.deserializer = typeAdapter instanceof JsonDeserializer?(JsonDeserializer)typeAdapter:null;
         $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
         this.exactType = exactType;
         this.matchRawType = matchRawType;
         this.hierarchyType = hierarchyType;
      }

      public TypeAdapter create(Gson gson, TypeToken type) {
         boolean matches = this.exactType != null?this.exactType.equals(type) || this.matchRawType && this.exactType.getType() == type.getRawType():this.hierarchyType.isAssignableFrom(type.getRawType());
         return matches?new TreeTypeAdapter(this.serializer, this.deserializer, gson, type, this):null;
      }
   }
}
