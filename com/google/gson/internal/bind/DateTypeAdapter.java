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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTypeAdapter extends TypeAdapter {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public TypeAdapter create(Gson gson, TypeToken typeToken) {
         return typeToken.getRawType() == Date.class?new DateTypeAdapter():null;
      }
   };
   private final DateFormat enUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
   private final DateFormat localFormat = DateFormat.getDateTimeInstance(2, 2);
   private final DateFormat iso8601Format = buildIso8601Format();

   private static DateFormat buildIso8601Format() {
      DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'", Locale.US);
      iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
      return iso8601Format;
   }

   public Date read(JsonReader in) throws IOException {
      if(in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         return this.deserializeToDate(in.nextString());
      }
   }

   private synchronized Date deserializeToDate(String json) {
      try {
         return this.localFormat.parse(json);
      } catch (ParseException var5) {
         try {
            return this.enUsFormat.parse(json);
         } catch (ParseException var4) {
            try {
               return this.iso8601Format.parse(json);
            } catch (ParseException var3) {
               throw new JsonSyntaxException(json, var3);
            }
         }
      }
   }

   public synchronized void write(JsonWriter out, Date value) throws IOException {
      if(value == null) {
         out.nullValue();
      } else {
         String dateFormatAsString = this.enUsFormat.format(value);
         out.value(dateFormatAsString);
      }
   }
}
