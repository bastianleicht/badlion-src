package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {
   public static JsonElement parse(JsonReader reader) throws JsonParseException {
      boolean isEmpty = true;

      try {
         reader.peek();
         isEmpty = false;
         return (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
      } catch (EOFException var3) {
         if(isEmpty) {
            return JsonNull.INSTANCE;
         } else {
            throw new JsonSyntaxException(var3);
         }
      } catch (MalformedJsonException var4) {
         throw new JsonSyntaxException(var4);
      } catch (IOException var5) {
         throw new JsonIOException(var5);
      } catch (NumberFormatException var6) {
         throw new JsonSyntaxException(var6);
      }
   }

   public static void write(JsonElement element, JsonWriter writer) throws IOException {
      TypeAdapters.JSON_ELEMENT.write(writer, element);
   }

   public static Writer writerForAppendable(Appendable appendable) {
      return (Writer)(appendable instanceof Writer?(Writer)appendable:new Streams.AppendableWriter(appendable));
   }

   private static final class AppendableWriter extends Writer {
      private final Appendable appendable;
      private final Streams.AppendableWriter.CurrentWrite currentWrite;

      private AppendableWriter(Appendable appendable) {
         this.currentWrite = new Streams.AppendableWriter.CurrentWrite();
         this.appendable = appendable;
      }

      public void write(char[] chars, int offset, int length) throws IOException {
         this.currentWrite.chars = chars;
         this.appendable.append(this.currentWrite, offset, offset + length);
      }

      public void write(int i) throws IOException {
         this.appendable.append((char)i);
      }

      public void flush() {
      }

      public void close() {
      }

      static class CurrentWrite implements CharSequence {
         char[] chars;

         public int length() {
            return this.chars.length;
         }

         public char charAt(int i) {
            return this.chars[i];
         }

         public CharSequence subSequence(int start, int end) {
            return new String(this.chars, start, end - start);
         }
      }
   }
}
