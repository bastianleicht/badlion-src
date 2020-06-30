package io.netty.util.internal;

import java.util.Arrays;

public final class AppendableCharSequence implements CharSequence, Appendable {
   private char[] chars;
   private int pos;

   public AppendableCharSequence(int length) {
      if(length < 1) {
         throw new IllegalArgumentException("length: " + length + " (length: >= 1)");
      } else {
         this.chars = new char[length];
      }
   }

   private AppendableCharSequence(char[] chars) {
      this.chars = chars;
      this.pos = chars.length;
   }

   public int length() {
      return this.pos;
   }

   public char charAt(int index) {
      if(index > this.pos) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.chars[index];
      }
   }

   public AppendableCharSequence subSequence(int start, int end) {
      return new AppendableCharSequence(Arrays.copyOfRange(this.chars, start, end));
   }

   public AppendableCharSequence append(char c) {
      if(this.pos == this.chars.length) {
         char[] old = this.chars;
         int len = old.length << 1;
         if(len < 0) {
            throw new IllegalStateException();
         }

         this.chars = new char[len];
         System.arraycopy(old, 0, this.chars, 0, old.length);
      }

      this.chars[this.pos++] = c;
      return this;
   }

   public AppendableCharSequence append(CharSequence csq) {
      return this.append(csq, 0, csq.length());
   }

   public AppendableCharSequence append(CharSequence csq, int start, int end) {
      if(csq.length() < end) {
         throw new IndexOutOfBoundsException();
      } else {
         int length = end - start;
         if(length > this.chars.length - this.pos) {
            this.chars = expand(this.chars, this.pos + length, this.pos);
         }

         if(csq instanceof AppendableCharSequence) {
            AppendableCharSequence seq = (AppendableCharSequence)csq;
            char[] src = seq.chars;
            System.arraycopy(src, start, this.chars, this.pos, length);
            this.pos += length;
            return this;
         } else {
            for(int i = start; i < end; ++i) {
               this.chars[this.pos++] = csq.charAt(i);
            }

            return this;
         }
      }
   }

   public void reset() {
      this.pos = 0;
   }

   public String toString() {
      return new String(this.chars, 0, this.pos);
   }

   public String substring(int start, int end) {
      int length = end - start;
      if(start <= this.pos && length <= this.pos) {
         return new String(this.chars, start, length);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private static char[] expand(char[] array, int neededSpace, int size) {
      int newCapacity = array.length;

      while(true) {
         newCapacity <<= 1;
         if(newCapacity < 0) {
            throw new IllegalStateException();
         }

         if(neededSpace <= newCapacity) {
            break;
         }
      }

      char[] newArray = new char[newCapacity];
      System.arraycopy(array, 0, newArray, 0, size);
      return newArray;
   }
}
