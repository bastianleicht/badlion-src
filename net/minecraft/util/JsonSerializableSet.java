package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;
import net.minecraft.util.IJsonSerializable;

public class JsonSerializableSet extends ForwardingSet implements IJsonSerializable {
   private final Set underlyingSet = Sets.newHashSet();

   public void fromJson(JsonElement json) {
      if(json.isJsonArray()) {
         for(JsonElement jsonelement : json.getAsJsonArray()) {
            this.add(jsonelement.getAsString());
         }
      }

   }

   public JsonElement getSerializableElement() {
      JsonArray jsonarray = new JsonArray();

      for(String s : this) {
         jsonarray.add(new JsonPrimitive(s));
      }

      return jsonarray;
   }

   protected Set delegate() {
      return this.underlyingSet;
   }
}
