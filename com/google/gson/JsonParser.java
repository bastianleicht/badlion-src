package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {
   public JsonElement parse(String json) throws JsonSyntaxException {
      return this.parse((Reader)(new StringReader(json)));
   }

   public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
      try {
         JsonReader jsonReader = new JsonReader(json);
         JsonElement element = this.parse(jsonReader);
         if(!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonSyntaxException("Did not consume the entire document.");
         } else {
            return element;
         }
      } catch (MalformedJsonException var4) {
         throw new JsonSyntaxException(var4);
      } catch (IOException var5) {
         throw new JsonIOException(var5);
      } catch (NumberFormatException var6) {
         throw new JsonSyntaxException(var6);
      }
   }

   public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
      boolean lenient = json.isLenient();
      json.setLenient(true);

      JsonElement e;
      try {
         e = Streams.parse(json);
      } catch (StackOverflowError var8) {
         throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", var8);
      } catch (OutOfMemoryError var9) {
         throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", var9);
      } finally {
         json.setLenient(lenient);
      }

      return e;
   }
}
