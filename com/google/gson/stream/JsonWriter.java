package com.google.gson.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class JsonWriter implements Closeable, Flushable {
   private static final String[] REPLACEMENT_CHARS = new String[128];
   private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
   private final Writer out;
   private int[] stack = new int[32];
   private int stackSize = 0;
   private String indent;
   private String separator;
   private boolean lenient;
   private boolean htmlSafe;
   private String deferredName;
   private boolean serializeNulls;

   public JsonWriter(Writer out) {
      this.push(6);
      this.separator = ":";
      this.serializeNulls = true;
      if(out == null) {
         throw new NullPointerException("out == null");
      } else {
         this.out = out;
      }
   }

   public final void setIndent(String indent) {
      if(indent.length() == 0) {
         this.indent = null;
         this.separator = ":";
      } else {
         this.indent = indent;
         this.separator = ": ";
      }

   }

   public final void setLenient(boolean lenient) {
      this.lenient = lenient;
   }

   public boolean isLenient() {
      return this.lenient;
   }

   public final void setHtmlSafe(boolean htmlSafe) {
      this.htmlSafe = htmlSafe;
   }

   public final boolean isHtmlSafe() {
      return this.htmlSafe;
   }

   public final void setSerializeNulls(boolean serializeNulls) {
      this.serializeNulls = serializeNulls;
   }

   public final boolean getSerializeNulls() {
      return this.serializeNulls;
   }

   public JsonWriter beginArray() throws IOException {
      this.writeDeferredName();
      return this.open(1, "[");
   }

   public JsonWriter endArray() throws IOException {
      return this.close(1, 2, "]");
   }

   public JsonWriter beginObject() throws IOException {
      this.writeDeferredName();
      return this.open(3, "{");
   }

   public JsonWriter endObject() throws IOException {
      return this.close(3, 5, "}");
   }

   private JsonWriter open(int empty, String openBracket) throws IOException {
      this.beforeValue(true);
      this.push(empty);
      this.out.write(openBracket);
      return this;
   }

   private JsonWriter close(int empty, int nonempty, String closeBracket) throws IOException {
      int context = this.peek();
      if(context != nonempty && context != empty) {
         throw new IllegalStateException("Nesting problem.");
      } else if(this.deferredName != null) {
         throw new IllegalStateException("Dangling name: " + this.deferredName);
      } else {
         --this.stackSize;
         if(context == nonempty) {
            this.newline();
         }

         this.out.write(closeBracket);
         return this;
      }
   }

   private void push(int newTop) {
      if(this.stackSize == this.stack.length) {
         int[] newStack = new int[this.stackSize * 2];
         System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
         this.stack = newStack;
      }

      this.stack[this.stackSize++] = newTop;
   }

   private int peek() {
      if(this.stackSize == 0) {
         throw new IllegalStateException("JsonWriter is closed.");
      } else {
         return this.stack[this.stackSize - 1];
      }
   }

   private void replaceTop(int topOfStack) {
      this.stack[this.stackSize - 1] = topOfStack;
   }

   public JsonWriter name(String name) throws IOException {
      if(name == null) {
         throw new NullPointerException("name == null");
      } else if(this.deferredName != null) {
         throw new IllegalStateException();
      } else if(this.stackSize == 0) {
         throw new IllegalStateException("JsonWriter is closed.");
      } else {
         this.deferredName = name;
         return this;
      }
   }

   private void writeDeferredName() throws IOException {
      if(this.deferredName != null) {
         this.beforeName();
         this.string(this.deferredName);
         this.deferredName = null;
      }

   }

   public JsonWriter value(String value) throws IOException {
      if(value == null) {
         return this.nullValue();
      } else {
         this.writeDeferredName();
         this.beforeValue(false);
         this.string(value);
         return this;
      }
   }

   public JsonWriter nullValue() throws IOException {
      if(this.deferredName != null) {
         if(!this.serializeNulls) {
            this.deferredName = null;
            return this;
         }

         this.writeDeferredName();
      }

      this.beforeValue(false);
      this.out.write("null");
      return this;
   }

   public JsonWriter value(boolean value) throws IOException {
      this.writeDeferredName();
      this.beforeValue(false);
      this.out.write(value?"true":"false");
      return this;
   }

   public JsonWriter value(double value) throws IOException {
      if(!Double.isNaN(value) && !Double.isInfinite(value)) {
         this.writeDeferredName();
         this.beforeValue(false);
         this.out.append(Double.toString(value));
         return this;
      } else {
         throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
      }
   }

   public JsonWriter value(long value) throws IOException {
      this.writeDeferredName();
      this.beforeValue(false);
      this.out.write(Long.toString(value));
      return this;
   }

   public JsonWriter value(Number value) throws IOException {
      if(value == null) {
         return this.nullValue();
      } else {
         this.writeDeferredName();
         String string = value.toString();
         if(this.lenient || !string.equals("-Infinity") && !string.equals("Infinity") && !string.equals("NaN")) {
            this.beforeValue(false);
            this.out.append(string);
            return this;
         } else {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
         }
      }
   }

   public void flush() throws IOException {
      if(this.stackSize == 0) {
         throw new IllegalStateException("JsonWriter is closed.");
      } else {
         this.out.flush();
      }
   }

   public void close() throws IOException {
      this.out.close();
      int size = this.stackSize;
      if(size <= 1 && (size != 1 || this.stack[size - 1] == 7)) {
         this.stackSize = 0;
      } else {
         throw new IOException("Incomplete document");
      }
   }

   private void string(String value) throws IOException {
      String[] replacements = this.htmlSafe?HTML_SAFE_REPLACEMENT_CHARS:REPLACEMENT_CHARS;
      this.out.write("\"");
      int last = 0;
      int length = value.length();

      for(int i = 0; i < length; ++i) {
         char c = value.charAt(i);
         String replacement;
         if(c < 128) {
            replacement = replacements[c];
            if(replacement == null) {
               continue;
            }
         } else if(c == 8232) {
            replacement = "\\u2028";
         } else {
            if(c != 8233) {
               continue;
            }

            replacement = "\\u2029";
         }

         if(last < i) {
            this.out.write(value, last, i - last);
         }

         this.out.write(replacement);
         last = i + 1;
      }

      if(last < length) {
         this.out.write(value, last, length - last);
      }

      this.out.write("\"");
   }

   private void newline() throws IOException {
      if(this.indent != null) {
         this.out.write("\n");
         int i = 1;

         for(int size = this.stackSize; i < size; ++i) {
            this.out.write(this.indent);
         }

      }
   }

   private void beforeName() throws IOException {
      int context = this.peek();
      if(context == 5) {
         this.out.write(44);
      } else if(context != 3) {
         throw new IllegalStateException("Nesting problem.");
      }

      this.newline();
      this.replaceTop(4);
   }

   private void beforeValue(boolean root) throws IOException {
      switch(this.peek()) {
      case 1:
         this.replaceTop(2);
         this.newline();
         break;
      case 2:
         this.out.append(',');
         this.newline();
         break;
      case 3:
      case 5:
      default:
         throw new IllegalStateException("Nesting problem.");
      case 4:
         this.out.append(this.separator);
         this.replaceTop(5);
         break;
      case 7:
         if(!this.lenient) {
            throw new IllegalStateException("JSON must have only one top-level value.");
         }
      case 6:
         if(!this.lenient && !root) {
            throw new IllegalStateException("JSON must start with an array or an object.");
         }

         this.replaceTop(7);
      }

   }

   static {
      for(int i = 0; i <= 31; ++i) {
         REPLACEMENT_CHARS[i] = String.format("\\u%04x", new Object[]{Integer.valueOf(i)});
      }

      REPLACEMENT_CHARS[34] = "\\\"";
      REPLACEMENT_CHARS[92] = "\\\\";
      REPLACEMENT_CHARS[9] = "\\t";
      REPLACEMENT_CHARS[8] = "\\b";
      REPLACEMENT_CHARS[10] = "\\n";
      REPLACEMENT_CHARS[13] = "\\r";
      REPLACEMENT_CHARS[12] = "\\f";
      HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
      HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
      HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
      HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
      HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
      HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
   }
}
