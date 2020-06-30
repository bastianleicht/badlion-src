package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public enum LongSerializationPolicy {
   DEFAULT {
      public JsonElement serialize(Long value) {
         return new JsonPrimitive(value);
      }
   },
   STRING {
      public JsonElement serialize(Long value) {
         return new JsonPrimitive(String.valueOf(value));
      }
   };

   private LongSerializationPolicy() {
   }

   public abstract JsonElement serialize(Long var1);
}
