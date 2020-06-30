package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {
   private static final char[] NON_EXECUTE_PREFIX = ")]}\'\n".toCharArray();
   private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
   private static final int PEEKED_NONE = 0;
   private static final int PEEKED_BEGIN_OBJECT = 1;
   private static final int PEEKED_END_OBJECT = 2;
   private static final int PEEKED_BEGIN_ARRAY = 3;
   private static final int PEEKED_END_ARRAY = 4;
   private static final int PEEKED_TRUE = 5;
   private static final int PEEKED_FALSE = 6;
   private static final int PEEKED_NULL = 7;
   private static final int PEEKED_SINGLE_QUOTED = 8;
   private static final int PEEKED_DOUBLE_QUOTED = 9;
   private static final int PEEKED_UNQUOTED = 10;
   private static final int PEEKED_BUFFERED = 11;
   private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
   private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
   private static final int PEEKED_UNQUOTED_NAME = 14;
   private static final int PEEKED_LONG = 15;
   private static final int PEEKED_NUMBER = 16;
   private static final int PEEKED_EOF = 17;
   private static final int NUMBER_CHAR_NONE = 0;
   private static final int NUMBER_CHAR_SIGN = 1;
   private static final int NUMBER_CHAR_DIGIT = 2;
   private static final int NUMBER_CHAR_DECIMAL = 3;
   private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
   private static final int NUMBER_CHAR_EXP_E = 5;
   private static final int NUMBER_CHAR_EXP_SIGN = 6;
   private static final int NUMBER_CHAR_EXP_DIGIT = 7;
   private final Reader in;
   private boolean lenient = false;
   private final char[] buffer = new char[1024];
   private int pos = 0;
   private int limit = 0;
   private int lineNumber = 0;
   private int lineStart = 0;
   private int peeked = 0;
   private long peekedLong;
   private int peekedNumberLength;
   private String peekedString;
   private int[] stack = new int[32];
   private int stackSize = 0;

   public JsonReader(Reader in) {
      this.stack[this.stackSize++] = 6;
      if(in == null) {
         throw new NullPointerException("in == null");
      } else {
         this.in = in;
      }
   }

   public final void setLenient(boolean lenient) {
      this.lenient = lenient;
   }

   public final boolean isLenient() {
      return this.lenient;
   }

   public void beginArray() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 3) {
         this.push(1);
         this.peeked = 0;
      } else {
         throw new IllegalStateException("Expected BEGIN_ARRAY but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
      }
   }

   public void endArray() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 4) {
         --this.stackSize;
         this.peeked = 0;
      } else {
         throw new IllegalStateException("Expected END_ARRAY but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
      }
   }

   public void beginObject() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 1) {
         this.push(3);
         this.peeked = 0;
      } else {
         throw new IllegalStateException("Expected BEGIN_OBJECT but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
      }
   }

   public void endObject() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 2) {
         --this.stackSize;
         this.peeked = 0;
      } else {
         throw new IllegalStateException("Expected END_OBJECT but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
      }
   }

   public boolean hasNext() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      return p != 2 && p != 4;
   }

   public JsonToken peek() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      switch(p) {
      case 1:
         return JsonToken.BEGIN_OBJECT;
      case 2:
         return JsonToken.END_OBJECT;
      case 3:
         return JsonToken.BEGIN_ARRAY;
      case 4:
         return JsonToken.END_ARRAY;
      case 5:
      case 6:
         return JsonToken.BOOLEAN;
      case 7:
         return JsonToken.NULL;
      case 8:
      case 9:
      case 10:
      case 11:
         return JsonToken.STRING;
      case 12:
      case 13:
      case 14:
         return JsonToken.NAME;
      case 15:
      case 16:
         return JsonToken.NUMBER;
      case 17:
         return JsonToken.END_DOCUMENT;
      default:
         throw new AssertionError();
      }
   }

   private int doPeek() throws IOException {
      int peekStack = this.stack[this.stackSize - 1];
      if(peekStack == 1) {
         this.stack[this.stackSize - 1] = 2;
      } else if(peekStack == 2) {
         int c = this.nextNonWhitespace(true);
         switch(c) {
         case 44:
            break;
         case 59:
            this.checkLenient();
            break;
         case 93:
            return this.peeked = 4;
         default:
            throw this.syntaxError("Unterminated array");
         }
      } else {
         if(peekStack == 3 || peekStack == 5) {
            this.stack[this.stackSize - 1] = 4;
            if(peekStack == 5) {
               int c = this.nextNonWhitespace(true);
               switch(c) {
               case 44:
                  break;
               case 59:
                  this.checkLenient();
                  break;
               case 125:
                  return this.peeked = 2;
               default:
                  throw this.syntaxError("Unterminated object");
               }
            }

            int c = this.nextNonWhitespace(true);
            switch(c) {
            case 34:
               return this.peeked = 13;
            case 39:
               this.checkLenient();
               return this.peeked = 12;
            case 125:
               if(peekStack != 5) {
                  return this.peeked = 2;
               }

               throw this.syntaxError("Expected name");
            default:
               this.checkLenient();
               --this.pos;
               if(this.isLiteral((char)c)) {
                  return this.peeked = 14;
               }

               throw this.syntaxError("Expected name");
            }
         }

         if(peekStack == 4) {
            this.stack[this.stackSize - 1] = 5;
            int c = this.nextNonWhitespace(true);
            switch(c) {
            case 58:
               break;
            case 61:
               this.checkLenient();
               if((this.pos < this.limit || this.fillBuffer(1)) && this.buffer[this.pos] == 62) {
                  ++this.pos;
               }
               break;
            default:
               throw this.syntaxError("Expected \':\'");
            }
         } else if(peekStack == 6) {
            if(this.lenient) {
               this.consumeNonExecutePrefix();
            }

            this.stack[this.stackSize - 1] = 7;
         } else if(peekStack == 7) {
            int c = this.nextNonWhitespace(false);
            if(c == -1) {
               return this.peeked = 17;
            }

            this.checkLenient();
            --this.pos;
         } else if(peekStack == 8) {
            throw new IllegalStateException("JsonReader is closed");
         }
      }

      int c = this.nextNonWhitespace(true);
      switch(c) {
      case 34:
         if(this.stackSize == 1) {
            this.checkLenient();
         }

         return this.peeked = 9;
      case 39:
         this.checkLenient();
         return this.peeked = 8;
      case 91:
         return this.peeked = 3;
      case 93:
         if(peekStack == 1) {
            return this.peeked = 4;
         }
      case 44:
      case 59:
         if(peekStack != 1 && peekStack != 2) {
            throw this.syntaxError("Unexpected value");
         }

         this.checkLenient();
         --this.pos;
         return this.peeked = 7;
      case 123:
         return this.peeked = 1;
      default:
         --this.pos;
         if(this.stackSize == 1) {
            this.checkLenient();
         }

         int result = this.peekKeyword();
         if(result != 0) {
            return result;
         } else {
            result = this.peekNumber();
            if(result != 0) {
               return result;
            } else if(!this.isLiteral(this.buffer[this.pos])) {
               throw this.syntaxError("Expected value");
            } else {
               this.checkLenient();
               return this.peeked = 10;
            }
         }
      }
   }

   private int peekKeyword() throws IOException {
      char c = this.buffer[this.pos];
      String keyword;
      String keywordUpper;
      int peeking;
      if(c != 116 && c != 84) {
         if(c != 102 && c != 70) {
            if(c != 110 && c != 78) {
               return 0;
            }

            keyword = "null";
            keywordUpper = "NULL";
            peeking = 7;
         } else {
            keyword = "false";
            keywordUpper = "FALSE";
            peeking = 6;
         }
      } else {
         keyword = "true";
         keywordUpper = "TRUE";
         peeking = 5;
      }

      int length = keyword.length();

      for(int i = 1; i < length; ++i) {
         if(this.pos + i >= this.limit && !this.fillBuffer(i + 1)) {
            return 0;
         }

         c = this.buffer[this.pos + i];
         if(c != keyword.charAt(i) && c != keywordUpper.charAt(i)) {
            return 0;
         }
      }

      if((this.pos + length < this.limit || this.fillBuffer(length + 1)) && this.isLiteral(this.buffer[this.pos + length])) {
         return 0;
      } else {
         this.pos += length;
         return this.peeked = peeking;
      }
   }

   private int peekNumber() throws IOException {
      char[] buffer = this.buffer;
      int p = this.pos;
      int l = this.limit;
      long value = 0L;
      boolean negative = false;
      boolean fitsInLong = true;
      int last = 0;
      int i = 0;

      label30:
      while(true) {
         if(p + i == l) {
            if(i == buffer.length) {
               return 0;
            }

            if(!this.fillBuffer(i + 1)) {
               break;
            }

            p = this.pos;
            l = this.limit;
         }

         char c = buffer[p + i];
         switch(c) {
         case '+':
            if(last != 5) {
               return 0;
            }

            last = 6;
            break;
         case '-':
            if(last == 0) {
               negative = true;
               last = 1;
            } else {
               if(last != 5) {
                  return 0;
               }

               last = 6;
            }
            break;
         case '.':
            if(last != 2) {
               return 0;
            }

            last = 3;
            break;
         case 'E':
         case 'e':
            if(last != 2 && last != 4) {
               return 0;
            }

            last = 5;
            break;
         default:
            if(c < 48 || c > 57) {
               if(this.isLiteral(c)) {
                  return 0;
               }
               break label30;
            }

            if(last != 1 && last != 0) {
               if(last == 2) {
                  if(value == 0L) {
                     return 0;
                  }

                  long newValue = value * 10L - (long)(c - 48);
                  fitsInLong &= value > -922337203685477580L || value == -922337203685477580L && newValue < value;
                  value = newValue;
               } else if(last == 3) {
                  last = 4;
               } else if(last == 5 || last == 6) {
                  last = 7;
               }
            } else {
               value = (long)(-(c - 48));
               last = 2;
            }
         }

         ++i;
      }

      if(last != 2 || !fitsInLong || value == Long.MIN_VALUE && !negative) {
         if(last != 2 && last != 4 && last != 7) {
            return 0;
         } else {
            this.peekedNumberLength = i;
            return this.peeked = 16;
         }
      } else {
         this.peekedLong = negative?value:-value;
         this.pos += i;
         return this.peeked = 15;
      }
   }

   private boolean isLiteral(char c) throws IOException {
      switch(c) {
      case '#':
      case '/':
      case ';':
      case '=':
      case '\\':
         this.checkLenient();
      case '\t':
      case '\n':
      case '\f':
      case '\r':
      case ' ':
      case ',':
      case ':':
      case '[':
      case ']':
      case '{':
      case '}':
         return false;
      default:
         return true;
      }
   }

   public String nextName() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      String result;
      if(p == 14) {
         result = this.nextUnquotedValue();
      } else if(p == 12) {
         result = this.nextQuotedValue('\'');
      } else {
         if(p != 13) {
            throw new IllegalStateException("Expected a name but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
         }

         result = this.nextQuotedValue('\"');
      }

      this.peeked = 0;
      return result;
   }

   public String nextString() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      String result;
      if(p == 10) {
         result = this.nextUnquotedValue();
      } else if(p == 8) {
         result = this.nextQuotedValue('\'');
      } else if(p == 9) {
         result = this.nextQuotedValue('\"');
      } else if(p == 11) {
         result = this.peekedString;
         this.peekedString = null;
      } else if(p == 15) {
         result = Long.toString(this.peekedLong);
      } else {
         if(p != 16) {
            throw new IllegalStateException("Expected a string but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
         }

         result = new String(this.buffer, this.pos, this.peekedNumberLength);
         this.pos += this.peekedNumberLength;
      }

      this.peeked = 0;
      return result;
   }

   public boolean nextBoolean() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 5) {
         this.peeked = 0;
         return true;
      } else if(p == 6) {
         this.peeked = 0;
         return false;
      } else {
         throw new IllegalStateException("Expected a boolean but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
      }
   }

   public void nextNull() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 7) {
         this.peeked = 0;
      } else {
         throw new IllegalStateException("Expected null but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
      }
   }

   public double nextDouble() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 15) {
         this.peeked = 0;
         return (double)this.peekedLong;
      } else {
         if(p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
         } else if(p != 8 && p != 9) {
            if(p == 10) {
               this.peekedString = this.nextUnquotedValue();
            } else if(p != 11) {
               throw new IllegalStateException("Expected a double but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            }
         } else {
            this.peekedString = this.nextQuotedValue((char)(p == 8?'\'':'\"'));
         }

         this.peeked = 11;
         double result = Double.parseDouble(this.peekedString);
         if(this.lenient || !Double.isNaN(result) && !Double.isInfinite(result)) {
            this.peekedString = null;
            this.peeked = 0;
            return result;
         } else {
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + result + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
         }
      }
   }

   public long nextLong() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 15) {
         this.peeked = 0;
         return this.peekedLong;
      } else {
         if(p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
         } else {
            if(p != 8 && p != 9) {
               throw new IllegalStateException("Expected a long but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            }

            this.peekedString = this.nextQuotedValue((char)(p == 8?'\'':'\"'));

            try {
               long result = Long.parseLong(this.peekedString);
               this.peeked = 0;
               return result;
            } catch (NumberFormatException var6) {
               ;
            }
         }

         this.peeked = 11;
         double asDouble = Double.parseDouble(this.peekedString);
         long result = (long)asDouble;
         if((double)result != asDouble) {
            throw new NumberFormatException("Expected a long but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
         } else {
            this.peekedString = null;
            this.peeked = 0;
            return result;
         }
      }
   }

   private String nextQuotedValue(char quote) throws IOException {
      char[] buffer = this.buffer;
      StringBuilder builder = new StringBuilder();

      while(true) {
         int p = this.pos;
         int l = this.limit;
         int start = p;

         while(p < l) {
            int c = buffer[p++];
            if(c == quote) {
               this.pos = p;
               builder.append(buffer, start, p - start - 1);
               return builder.toString();
            }

            if(c == 92) {
               this.pos = p;
               builder.append(buffer, start, p - start - 1);
               builder.append(this.readEscapeCharacter());
               p = this.pos;
               l = this.limit;
               start = p;
            } else if(c == 10) {
               ++this.lineNumber;
               this.lineStart = p;
            }
         }

         builder.append(buffer, start, p - start);
         this.pos = p;
         if(!this.fillBuffer(1)) {
            break;
         }
      }

      throw this.syntaxError("Unterminated string");
   }

   private String nextUnquotedValue() throws IOException {
      StringBuilder builder = null;
      int i = 0;

      label4:
      while(true) {
         if(this.pos + i < this.limit) {
            switch(this.buffer[this.pos + i]) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}':
               break label4;
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\':
               this.checkLenient();
               break label4;
            default:
               ++i;
            }
         } else if(i < this.buffer.length) {
            if(this.fillBuffer(i + 1)) {
               continue;
            }
            break;
         } else {
            if(builder == null) {
               builder = new StringBuilder();
            }

            builder.append(this.buffer, this.pos, i);
            this.pos += i;
            i = 0;
            if(!this.fillBuffer(1)) {
               break;
            }
         }
      }

      String result;
      if(builder == null) {
         result = new String(this.buffer, this.pos, i);
      } else {
         builder.append(this.buffer, this.pos, i);
         result = builder.toString();
      }

      this.pos += i;
      return result;
   }

   private void skipQuotedValue(char quote) throws IOException {
      char[] buffer = this.buffer;

      while(true) {
         int p = this.pos;
         int l = this.limit;

         while(p < l) {
            int c = buffer[p++];
            if(c == quote) {
               this.pos = p;
               return;
            }

            if(c == 92) {
               this.pos = p;
               this.readEscapeCharacter();
               p = this.pos;
               l = this.limit;
            } else if(c == 10) {
               ++this.lineNumber;
               this.lineStart = p;
            }
         }

         this.pos = p;
         if(!this.fillBuffer(1)) {
            break;
         }
      }

      throw this.syntaxError("Unterminated string");
   }

   private void skipUnquotedValue() throws IOException {
      // $FF: Couldn't be decompiled
   }

   public int nextInt() throws IOException {
      int p = this.peeked;
      if(p == 0) {
         p = this.doPeek();
      }

      if(p == 15) {
         int result = (int)this.peekedLong;
         if(this.peekedLong != (long)result) {
            throw new NumberFormatException("Expected an int but was " + this.peekedLong + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
         } else {
            this.peeked = 0;
            return result;
         }
      } else {
         if(p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
         } else {
            if(p != 8 && p != 9) {
               throw new IllegalStateException("Expected an int but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            }

            this.peekedString = this.nextQuotedValue((char)(p == 8?'\'':'\"'));

            try {
               int result = Integer.parseInt(this.peekedString);
               this.peeked = 0;
               return result;
            } catch (NumberFormatException var5) {
               ;
            }
         }

         this.peeked = 11;
         double asDouble = Double.parseDouble(this.peekedString);
         int result = (int)asDouble;
         if((double)result != asDouble) {
            throw new NumberFormatException("Expected an int but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
         } else {
            this.peekedString = null;
            this.peeked = 0;
            return result;
         }
      }
   }

   public void close() throws IOException {
      this.peeked = 0;
      this.stack[0] = 8;
      this.stackSize = 1;
      this.in.close();
   }

   public void skipValue() throws IOException {
      int count = 0;

      while(true) {
         int p = this.peeked;
         if(p == 0) {
            p = this.doPeek();
         }

         if(p == 3) {
            this.push(1);
            ++count;
         } else if(p == 1) {
            this.push(3);
            ++count;
         } else if(p == 4) {
            --this.stackSize;
            --count;
         } else if(p == 2) {
            --this.stackSize;
            --count;
         } else if(p != 14 && p != 10) {
            if(p != 8 && p != 12) {
               if(p != 9 && p != 13) {
                  if(p == 16) {
                     this.pos += this.peekedNumberLength;
                  }
               } else {
                  this.skipQuotedValue('\"');
               }
            } else {
               this.skipQuotedValue('\'');
            }
         } else {
            this.skipUnquotedValue();
         }

         this.peeked = 0;
         if(count == 0) {
            break;
         }
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

   private boolean fillBuffer(int minimum) throws IOException {
      char[] buffer = this.buffer;
      this.lineStart -= this.pos;
      if(this.limit != this.pos) {
         this.limit -= this.pos;
         System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
      } else {
         this.limit = 0;
      }

      this.pos = 0;

      int total;
      while((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
         this.limit += total;
         if(this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer[0] == '\ufeff') {
            ++this.pos;
            ++this.lineStart;
            ++minimum;
         }

         if(this.limit >= minimum) {
            return true;
         }
      }

      return false;
   }

   private int getLineNumber() {
      return this.lineNumber + 1;
   }

   private int getColumnNumber() {
      return this.pos - this.lineStart + 1;
   }

   private int nextNonWhitespace(boolean throwOnEof) throws IOException {
      char[] buffer = this.buffer;
      int p = this.pos;
      int l = this.limit;

      while(true) {
         if(p == l) {
            this.pos = p;
            if(!this.fillBuffer(1)) {
               if(throwOnEof) {
                  throw new EOFException("End of input at line " + this.getLineNumber() + " column " + this.getColumnNumber());
               }

               return -1;
            }

            p = this.pos;
            l = this.limit;
         }

         int c = buffer[p++];
         if(c == 10) {
            ++this.lineNumber;
            this.lineStart = p;
         } else if(c != 32 && c != 13 && c != 9) {
            if(c == 47) {
               this.pos = p;
               if(p == l) {
                  --this.pos;
                  boolean charsLoaded = this.fillBuffer(2);
                  ++this.pos;
                  if(!charsLoaded) {
                     return c;
                  }
               }

               this.checkLenient();
               char peek = buffer[this.pos];
               switch(peek) {
               case '*':
                  ++this.pos;
                  if(!this.skipTo("*/")) {
                     throw this.syntaxError("Unterminated comment");
                  }

                  p = this.pos + 2;
                  l = this.limit;
                  break;
               case '/':
                  ++this.pos;
                  this.skipToEndOfLine();
                  p = this.pos;
                  l = this.limit;
                  break;
               default:
                  return c;
               }
            } else {
               if(c != 35) {
                  this.pos = p;
                  return c;
               }

               this.pos = p;
               this.checkLenient();
               this.skipToEndOfLine();
               p = this.pos;
               l = this.limit;
            }
         }
      }
   }

   private void checkLenient() throws IOException {
      if(!this.lenient) {
         throw this.syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
      }
   }

   private void skipToEndOfLine() throws IOException {
      while(this.pos < this.limit || this.fillBuffer(1)) {
         char c = this.buffer[this.pos++];
         if(c == 10) {
            ++this.lineNumber;
            this.lineStart = this.pos;
         } else if(c != 13) {
            continue;
         }
         break;
      }

   }

   private boolean skipTo(String toFind) throws IOException {
      for(; this.pos + toFind.length() <= this.limit || this.fillBuffer(toFind.length()); ++this.pos) {
         if(this.buffer[this.pos] == 10) {
            ++this.lineNumber;
            this.lineStart = this.pos + 1;
         } else {
            int c = 0;

            while(true) {
               if(c >= toFind.length()) {
                  return true;
               }

               if(this.buffer[this.pos + c] != toFind.charAt(c)) {
                  break;
               }

               ++c;
            }
         }
      }

      return false;
   }

   public String toString() {
      return this.getClass().getSimpleName() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber();
   }

   private char readEscapeCharacter() throws IOException {
      if(this.pos == this.limit && !this.fillBuffer(1)) {
         throw this.syntaxError("Unterminated escape sequence");
      } else {
         char escaped = this.buffer[this.pos++];
         switch(escaped) {
         case '\n':
            ++this.lineNumber;
            this.lineStart = this.pos;
         case '\"':
         case '\'':
         case '\\':
         default:
            return escaped;
         case 'b':
            return '\b';
         case 'f':
            return '\f';
         case 'n':
            return '\n';
         case 'r':
            return '\r';
         case 't':
            return '\t';
         case 'u':
            if(this.pos + 4 > this.limit && !this.fillBuffer(4)) {
               throw this.syntaxError("Unterminated escape sequence");
            } else {
               char result = '\u0000';
               int i = this.pos;

               for(int end = i + 4; i < end; ++i) {
                  char c = this.buffer[i];
                  result = (char)(result << 4);
                  if(c >= 48 && c <= 57) {
                     result = (char)(result + (c - 48));
                  } else if(c >= 97 && c <= 102) {
                     result = (char)(result + c - 97 + 10);
                  } else {
                     if(c < 65 || c > 70) {
                        throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                     }

                     result = (char)(result + c - 65 + 10);
                  }
               }

               this.pos += 4;
               return result;
            }
         }
      }
   }

   private IOException syntaxError(String message) throws IOException {
      throw new MalformedJsonException(message + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
   }

   private void consumeNonExecutePrefix() throws IOException {
      this.nextNonWhitespace(true);
      --this.pos;
      if(this.pos + NON_EXECUTE_PREFIX.length <= this.limit || this.fillBuffer(NON_EXECUTE_PREFIX.length)) {
         for(int i = 0; i < NON_EXECUTE_PREFIX.length; ++i) {
            if(this.buffer[this.pos + i] != NON_EXECUTE_PREFIX[i]) {
               return;
            }
         }

         this.pos += NON_EXECUTE_PREFIX.length;
      }
   }

   static {
      JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
         public void promoteNameToValue(JsonReader reader) throws IOException {
            if(reader instanceof JsonTreeReader) {
               ((JsonTreeReader)reader).promoteNameToValue();
            } else {
               int p = reader.peeked;
               if(p == 0) {
                  p = reader.doPeek();
               }

               if(p == 13) {
                  reader.peeked = 9;
               } else if(p == 12) {
                  reader.peeked = 8;
               } else {
                  if(p != 14) {
                     throw new IllegalStateException("Expected a name but was " + reader.peek() + " " + " at line " + reader.getLineNumber() + " column " + reader.getColumnNumber());
                  }

                  reader.peeked = 10;
               }

            }
         }
      };
   }
}
