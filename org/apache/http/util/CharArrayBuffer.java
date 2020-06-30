package org.apache.http.util;

import java.io.Serializable;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;

@NotThreadSafe
public final class CharArrayBuffer implements Serializable {
   private static final long serialVersionUID = -6208952725094867135L;
   private char[] buffer;
   private int len;

   public CharArrayBuffer(int capacity) {
      Args.notNegative(capacity, "Buffer capacity");
      this.buffer = new char[capacity];
   }

   private void expand(int newlen) {
      char[] newbuffer = new char[Math.max(this.buffer.length << 1, newlen)];
      System.arraycopy(this.buffer, 0, newbuffer, 0, this.len);
      this.buffer = newbuffer;
   }

   public void append(char[] b, int off, int len) {
      if(b != null) {
         if(off >= 0 && off <= b.length && len >= 0 && off + len >= 0 && off + len <= b.length) {
            if(len != 0) {
               int newlen = this.len + len;
               if(newlen > this.buffer.length) {
                  this.expand(newlen);
               }

               System.arraycopy(b, off, this.buffer, this.len, len);
               this.len = newlen;
            }
         } else {
            throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
         }
      }
   }

   public void append(String str) {
      String s = str != null?str:"null";
      int strlen = s.length();
      int newlen = this.len + strlen;
      if(newlen > this.buffer.length) {
         this.expand(newlen);
      }

      s.getChars(0, strlen, this.buffer, this.len);
      this.len = newlen;
   }

   public void append(CharArrayBuffer b, int off, int len) {
      if(b != null) {
         this.append(b.buffer, off, len);
      }
   }

   public void append(CharArrayBuffer b) {
      if(b != null) {
         this.append((char[])b.buffer, 0, b.len);
      }
   }

   public void append(char ch) {
      int newlen = this.len + 1;
      if(newlen > this.buffer.length) {
         this.expand(newlen);
      }

      this.buffer[this.len] = ch;
      this.len = newlen;
   }

   public void append(byte[] b, int off, int len) {
      if(b != null) {
         if(off >= 0 && off <= b.length && len >= 0 && off + len >= 0 && off + len <= b.length) {
            if(len != 0) {
               int oldlen = this.len;
               int newlen = oldlen + len;
               if(newlen > this.buffer.length) {
                  this.expand(newlen);
               }

               int i1 = off;

               for(int i2 = oldlen; i2 < newlen; ++i2) {
                  this.buffer[i2] = (char)(b[i1] & 255);
                  ++i1;
               }

               this.len = newlen;
            }
         } else {
            throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
         }
      }
   }

   public void append(ByteArrayBuffer b, int off, int len) {
      if(b != null) {
         this.append(b.buffer(), off, len);
      }
   }

   public void append(Object obj) {
      this.append(String.valueOf(obj));
   }

   public void clear() {
      this.len = 0;
   }

   public char[] toCharArray() {
      char[] b = new char[this.len];
      if(this.len > 0) {
         System.arraycopy(this.buffer, 0, b, 0, this.len);
      }

      return b;
   }

   public char charAt(int i) {
      return this.buffer[i];
   }

   public char[] buffer() {
      return this.buffer;
   }

   public int capacity() {
      return this.buffer.length;
   }

   public int length() {
      return this.len;
   }

   public void ensureCapacity(int required) {
      if(required > 0) {
         int available = this.buffer.length - this.len;
         if(required > available) {
            this.expand(this.len + required);
         }

      }
   }

   public void setLength(int len) {
      if(len >= 0 && len <= this.buffer.length) {
         this.len = len;
      } else {
         throw new IndexOutOfBoundsException("len: " + len + " < 0 or > buffer len: " + this.buffer.length);
      }
   }

   public boolean isEmpty() {
      return this.len == 0;
   }

   public boolean isFull() {
      return this.len == this.buffer.length;
   }

   public int indexOf(int ch, int from, int to) {
      int beginIndex = from;
      if(from < 0) {
         beginIndex = 0;
      }

      int endIndex = to;
      if(to > this.len) {
         endIndex = this.len;
      }

      if(beginIndex > endIndex) {
         return -1;
      } else {
         for(int i = beginIndex; i < endIndex; ++i) {
            if(this.buffer[i] == ch) {
               return i;
            }
         }

         return -1;
      }
   }

   public int indexOf(int ch) {
      return this.indexOf(ch, 0, this.len);
   }

   public String substring(int beginIndex, int endIndex) {
      return new String(this.buffer, beginIndex, endIndex - beginIndex);
   }

   public String substringTrimmed(int from, int to) {
      int beginIndex = from;
      int endIndex = to;
      if(from < 0) {
         throw new IndexOutOfBoundsException("Negative beginIndex: " + from);
      } else if(to > this.len) {
         throw new IndexOutOfBoundsException("endIndex: " + to + " > length: " + this.len);
      } else if(from > to) {
         throw new IndexOutOfBoundsException("beginIndex: " + from + " > endIndex: " + to);
      } else {
         while(beginIndex < endIndex && HTTP.isWhitespace(this.buffer[beginIndex])) {
            ++beginIndex;
         }

         while(endIndex > beginIndex && HTTP.isWhitespace(this.buffer[endIndex - 1])) {
            --endIndex;
         }

         return new String(this.buffer, beginIndex, endIndex - beginIndex);
      }
   }

   public String toString() {
      return new String(this.buffer, 0, this.len);
   }
}
