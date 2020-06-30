package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class SqlDateTypeAdapter extends TypeAdapter {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public TypeAdapter create(Gson gson, TypeToken typeToken) {
         return typeToken.getRawType() == Date.class?new SqlDateTypeAdapter():null;
      }
   };
   private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

   public synchronized Date read(JsonReader in) throws IOException {
      if(in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         try {
            long utilDate = this.format.parse(in.nextString()).getTime();
            return new Date(utilDate);
         } catch (ParseException var4) {
            throw new JsonSyntaxException(var4);
         }
      }
   }

   public synchronized void write(JsonWriter out, Date value) throws IOException {
      out.value(value == null?null:this.format.format(value));
   }
}
