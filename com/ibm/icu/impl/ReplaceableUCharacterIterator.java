package com.ibm.icu.impl;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.ReplaceableString;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UTF16;

public class ReplaceableUCharacterIterator extends UCharacterIterator {
   private Replaceable replaceable;
   private int currentIndex;

   public ReplaceableUCharacterIterator(Replaceable replaceable) {
      if(replaceable == null) {
         throw new IllegalArgumentException();
      } else {
         this.replaceable = replaceable;
         this.currentIndex = 0;
      }
   }

   public ReplaceableUCharacterIterator(String str) {
      if(str == null) {
         throw new IllegalArgumentException();
      } else {
         this.replaceable = new ReplaceableString(str);
         this.currentIndex = 0;
      }
   }

   public ReplaceableUCharacterIterator(StringBuffer buf) {
      if(buf == null) {
         throw new IllegalArgumentException();
      } else {
         this.replaceable = new ReplaceableString(buf);
         this.currentIndex = 0;
      }
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public int current() {
      return this.currentIndex < this.replaceable.length()?this.replaceable.charAt(this.currentIndex):-1;
   }

   public int currentCodePoint() {
      int ch = this.current();
      if(UTF16.isLeadSurrogate((char)ch)) {
         this.next();
         int ch2 = this.current();
         this.previous();
         if(UTF16.isTrailSurrogate((char)ch2)) {
            return UCharacterProperty.getRawSupplementary((char)ch, (char)ch2);
         }
      }

      return ch;
   }

   public int getLength() {
      return this.replaceable.length();
   }

   public int getIndex() {
      return this.currentIndex;
   }

   public int next() {
      return this.currentIndex < this.replaceable.length()?this.replaceable.charAt(this.currentIndex++):-1;
   }

   public int previous() {
      return this.currentIndex > 0?this.replaceable.charAt(--this.currentIndex):-1;
   }

   public void setIndex(int currentIndex) throws IndexOutOfBoundsException {
      if(currentIndex >= 0 && currentIndex <= this.replaceable.length()) {
         this.currentIndex = currentIndex;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getText(char[] fillIn, int offset) {
      int length = this.replaceable.length();
      if(offset >= 0 && offset + length <= fillIn.length) {
         this.replaceable.getChars(0, length, fillIn, offset);
         return length;
      } else {
         throw new IndexOutOfBoundsException(Integer.toString(length));
      }
   }
}
