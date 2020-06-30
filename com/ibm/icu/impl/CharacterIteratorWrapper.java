package com.ibm.icu.impl;

import com.ibm.icu.text.UCharacterIterator;
import java.text.CharacterIterator;

public class CharacterIteratorWrapper extends UCharacterIterator {
   private CharacterIterator iterator;

   public CharacterIteratorWrapper(CharacterIterator iter) {
      if(iter == null) {
         throw new IllegalArgumentException();
      } else {
         this.iterator = iter;
      }
   }

   public int current() {
      int c = this.iterator.current();
      return c == '\uffff'?-1:c;
   }

   public int getLength() {
      return this.iterator.getEndIndex() - this.iterator.getBeginIndex();
   }

   public int getIndex() {
      return this.iterator.getIndex();
   }

   public int next() {
      int i = this.iterator.current();
      this.iterator.next();
      return i == '\uffff'?-1:i;
   }

   public int previous() {
      int i = this.iterator.previous();
      return i == '\uffff'?-1:i;
   }

   public void setIndex(int index) {
      try {
         this.iterator.setIndex(index);
      } catch (IllegalArgumentException var3) {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setToLimit() {
      this.iterator.setIndex(this.iterator.getEndIndex());
   }

   public int getText(char[] fillIn, int offset) {
      int length = this.iterator.getEndIndex() - this.iterator.getBeginIndex();
      int currentIndex = this.iterator.getIndex();
      if(offset >= 0 && offset + length <= fillIn.length) {
         for(char ch = this.iterator.first(); ch != '\uffff'; ch = this.iterator.next()) {
            fillIn[offset++] = ch;
         }

         this.iterator.setIndex(currentIndex);
         return length;
      } else {
         throw new IndexOutOfBoundsException(Integer.toString(length));
      }
   }

   public Object clone() {
      try {
         CharacterIteratorWrapper result = (CharacterIteratorWrapper)super.clone();
         result.iterator = (CharacterIterator)this.iterator.clone();
         return result;
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public int moveIndex(int delta) {
      int length = this.iterator.getEndIndex() - this.iterator.getBeginIndex();
      int idx = this.iterator.getIndex() + delta;
      if(idx < 0) {
         idx = 0;
      } else if(idx > length) {
         idx = length;
      }

      return this.iterator.setIndex(idx);
   }

   public CharacterIterator getCharacterIterator() {
      return (CharacterIterator)this.iterator.clone();
   }
}
