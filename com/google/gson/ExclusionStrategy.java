package com.google.gson;

import com.google.gson.FieldAttributes;

public interface ExclusionStrategy {
   boolean shouldSkipField(FieldAttributes var1);

   boolean shouldSkipClass(Class var1);
}
