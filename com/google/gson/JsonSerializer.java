package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;

public interface JsonSerializer {
   JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3);
}
