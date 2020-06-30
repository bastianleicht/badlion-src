package com.ibm.icu.impl;

import com.ibm.icu.text.UCharacterIterator;

public final class StringUCharacterIterator extends UCharacterIterator {
   private String m_text_;
   private int m_currentIndex_;

   public StringUCharacterIterator(String str) {
      if(str == null) {
         throw new IllegalArgumentException();
      } else {
         this.m_text_ = str;
         this.m_currentIndex_ = 0;
      }
   }

   public StringUCharacterIterator() {
      this.m_text_ = "";
      this.m_currentIndex_ = 0;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public int current() {
      return this.m_currentIndex_ < this.m_text_.length()?this.m_text_.charAt(this.m_currentIndex_):-1;
   }

   public int getLength() {
      return this.m_text_.length();
   }

   public int getIndex() {
      return this.m_currentIndex_;
   }

   public int next() {
      return this.m_currentIndex_ < this.m_text_.length()?this.m_text_.charAt(this.m_currentIndex_++):-1;
   }

   public int previous() {
      return this.m_currentIndex_ > 0?this.m_text_.charAt(--this.m_currentIndex_):-1;
   }

   public void setIndex(int currentIndex) throws IndexOutOfBoundsException {
      if(currentIndex >= 0 && currentIndex <= this.m_text_.length()) {
         this.m_currentIndex_ = currentIndex;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getText(char[] fillIn, int offset) {
      int length = this.m_text_.length();
      if(offset >= 0 && offset + length <= fillIn.length) {
         this.m_text_.getChars(0, length, fillIn, offset);
         return length;
      } else {
         throw new IndexOutOfBoundsException(Integer.toString(length));
      }
   }

   public String getText() {
      return this.m_text_;
   }

   public void setText(String text) {
      if(text == null) {
         throw new NullPointerException();
      } else {
         this.m_text_ = text;
         this.m_currentIndex_ = 0;
      }
   }
}
