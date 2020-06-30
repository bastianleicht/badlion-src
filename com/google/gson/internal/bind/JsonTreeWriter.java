package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class JsonTreeWriter extends JsonWriter {
   private static final Writer UNWRITABLE_WRITER = new Writer() {
      public void write(char[] buffer, int offset, int counter) {
         throw new AssertionError();
      }

      public void flush() throws IOException {
         throw new AssertionError();
      }

      public void close() throws IOException {
         throw new AssertionError();
      }
   };
   private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
   private final List stack = new ArrayList();
   private String pendingName;
   private JsonElement product = JsonNull.INSTANCE;

   public JsonTreeWriter() {
      super(UNWRITABLE_WRITER);
   }

   public JsonElement get() {
      if(!this.stack.isEmpty()) {
         throw new IllegalStateException("Expected one JSON element but was " + this.stack);
      } else {
         return this.product;
      }
   }

   private JsonElement peek() {
      return (JsonElement)this.stack.get(this.stack.size() - 1);
   }

   private void put(JsonElement value) {
      if(this.pendingName != null) {
         if(!value.isJsonNull() || this.getSerializeNulls()) {
            JsonObject object = (JsonObject)this.peek();
            object.add(this.pendingName, value);
         }

         this.pendingName = null;
      } else if(this.stack.isEmpty()) {
         this.product = value;
      } else {
         JsonElement element = this.peek();
         if(!(element instanceof JsonArray)) {
            throw new IllegalStateException();
         }

         ((JsonArray)element).add(value);
      }

   }

   public JsonWriter beginArray() throws IOException {
      JsonArray array = new JsonArray();
      this.put(array);
      this.stack.add(array);
      return this;
   }

   public JsonWriter endArray() throws IOException {
      if(!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement element = this.peek();
         if(element instanceof JsonArray) {
            this.stack.remove(this.stack.size() - 1);
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   public JsonWriter beginObject() throws IOException {
      JsonObject object = new JsonObject();
      this.put(object);
      this.stack.add(object);
      return this;
   }

   public JsonWriter endObject() throws IOException {
      if(!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement element = this.peek();
         if(element instanceof JsonObject) {
            this.stack.remove(this.stack.size() - 1);
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   public JsonWriter name(String name) throws IOException {
      if(!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement element = this.peek();
         if(element instanceof JsonObject) {
            this.pendingName = name;
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   public JsonWriter value(String value) throws IOException {
      if(value == null) {
         return this.nullValue();
      } else {
         this.put(new JsonPrimitive(value));
         return this;
      }
   }

   public JsonWriter nullValue() throws IOException {
      this.put(JsonNull.INSTANCE);
      return this;
   }

   public JsonWriter value(boolean value) throws IOException {
      this.put(new JsonPrimitive(Boolean.valueOf(value)));
      return this;
   }

   public JsonWriter value(double value) throws IOException {
      if(this.isLenient() || !Double.isNaN(value) && !Double.isInfinite(value)) {
         this.put(new JsonPrimitive(Double.valueOf(value)));
         return this;
      } else {
         throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
      }
   }

   public JsonWriter value(long value) throws IOException {
      this.put(new JsonPrimitive(Long.valueOf(value)));
      return this;
   }

   public JsonWriter value(Number value) throws IOException {
      if(value == null) {
         return this.nullValue();
      } else {
         if(!this.isLenient()) {
            double d = value.doubleValue();
            if(Double.isNaN(d) || Double.isInfinite(d)) {
               throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
            }
         }

         this.put(new JsonPrimitive(value));
         return this;
      }
   }

   public void flush() throws IOException {
   }

   public void close() throws IOException {
      if(!this.stack.isEmpty()) {
         throw new IOException("Incomplete document");
      } else {
         this.stack.add(SENTINEL_CLOSED);
      }
   }
}
