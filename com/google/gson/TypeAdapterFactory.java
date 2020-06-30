package com.google.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

public interface TypeAdapterFactory {
   TypeAdapter create(Gson var1, TypeToken var2);
}
