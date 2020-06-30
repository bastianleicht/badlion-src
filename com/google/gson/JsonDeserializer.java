package com.google.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public interface JsonDeserializer {
   Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException;
}
