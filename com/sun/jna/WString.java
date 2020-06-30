package com.sun.jna;

import java.nio.CharBuffer;

public final class WString implements CharSequence, Comparable {
   private String string;

   public WString(String s) {
      if(s == null) {
         throw new NullPointerException("String initializer must be non-null");
      } else {
         this.string = s;
      }
   }

   public String toString() {
      return this.string;
   }

   public boolean equals(Object o) {
      return o instanceof WString && this.toString().equals(o.toString());
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   public int compareTo(Object o) {
      return this.toString().compareTo(o.toString());
   }

   public int length() {
      return this.toString().length();
   }

   public char charAt(int index) {
      return this.toString().charAt(index);
   }

   public CharSequence subSequence(int start, int end) {
      return CharBuffer.wrap(this.toString()).subSequence(start, end);
   }
}
