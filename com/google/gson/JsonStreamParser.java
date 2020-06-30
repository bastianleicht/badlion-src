package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JsonStreamParser implements Iterator {
   private final JsonReader parser;
   private final Object lock;

   public JsonStreamParser(String json) {
      this((Reader)(new StringReader(json)));
   }

   public JsonStreamParser(Reader reader) {
      this.parser = new JsonReader(reader);
      this.parser.setLenient(true);
      this.lock = new Object();
   }

   public JsonElement next() throws JsonParseException {
      if(!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         try {
            return Streams.parse(this.parser);
         } catch (StackOverflowError var2) {
            throw new JsonParseException("Failed parsing JSON source to Json", var2);
         } catch (OutOfMemoryError var3) {
            throw new JsonParseException("Failed parsing JSON source to Json", var3);
         } catch (JsonParseException var4) {
            throw var4.getCause() instanceof EOFException?new NoSuchElementException():var4;
         }
      }
   }

   public boolean hasNext() {
      synchronized(this.lock) {
         boolean var10000;
         try {
            var10000 = this.parser.peek() != JsonToken.END_DOCUMENT;
         } catch (MalformedJsonException var4) {
            throw new JsonSyntaxException(var4);
         } catch (IOException var5) {
            throw new JsonIOException(var5);
         }

         return var10000;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
