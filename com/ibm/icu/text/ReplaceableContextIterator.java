package com.ibm.icu.text;

import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.UTF16;

class ReplaceableContextIterator implements UCaseProps.ContextIterator {
   protected Replaceable rep = null;
   protected int index;
   protected int limit;
   protected int cpStart;
   protected int cpLimit;
   protected int contextStart;
   protected int contextLimit;
   protected int dir;
   protected boolean reachedLimit;

   ReplaceableContextIterator() {
      this.limit = this.cpStart = this.cpLimit = this.index = this.contextStart = this.contextLimit = 0;
      this.dir = 0;
      this.reachedLimit = false;
   }

   public void setText(Replaceable rep) {
      this.rep = rep;
      this.limit = this.contextLimit = rep.length();
      this.cpStart = this.cpLimit = this.index = this.contextStart = 0;
      this.dir = 0;
      this.reachedLimit = false;
   }

   public void setIndex(int index) {
      this.cpStart = this.cpLimit = index;
      this.index = 0;
      this.dir = 0;
      this.reachedLimit = false;
   }

   public int getCaseMapCPStart() {
      return this.cpStart;
   }

   public void setLimit(int lim) {
      if(0 <= lim && lim <= this.rep.length()) {
         this.limit = lim;
      } else {
         this.limit = this.rep.length();
      }

      this.reachedLimit = false;
   }

   public void setContextLimits(int contextStart, int contextLimit) {
      if(contextStart < 0) {
         this.contextStart = 0;
      } else if(contextStart <= this.rep.length()) {
         this.contextStart = contextStart;
      } else {
         this.contextStart = this.rep.length();
      }

      if(contextLimit < this.contextStart) {
         this.contextLimit = this.contextStart;
      } else if(contextLimit <= this.rep.length()) {
         this.contextLimit = contextLimit;
      } else {
         this.contextLimit = this.rep.length();
      }

      this.reachedLimit = false;
   }

   public int nextCaseMapCP() {
      if(this.cpLimit < this.limit) {
         this.cpStart = this.cpLimit;
         int c = this.rep.char32At(this.cpLimit);
         this.cpLimit += UTF16.getCharCount(c);
         return c;
      } else {
         return -1;
      }
   }

   public int replace(String text) {
      int delta = text.length() - (this.cpLimit - this.cpStart);
      this.rep.replace(this.cpStart, this.cpLimit, text);
      this.cpLimit += delta;
      this.limit += delta;
      this.contextLimit += delta;
      return delta;
   }

   public boolean didReachLimit() {
      return this.reachedLimit;
   }

   public void reset(int direction) {
      if(direction > 0) {
         this.dir = 1;
         this.index = this.cpLimit;
      } else if(direction < 0) {
         this.dir = -1;
         this.index = this.cpStart;
      } else {
         this.dir = 0;
         this.index = 0;
      }

      this.reachedLimit = false;
   }

   public int next() {
      if(this.dir > 0) {
         if(this.index < this.contextLimit) {
            int c = this.rep.char32At(this.index);
            this.index += UTF16.getCharCount(c);
            return c;
         }

         this.reachedLimit = true;
      } else if(this.dir < 0 && this.index > this.contextStart) {
         int c = this.rep.char32At(this.index - 1);
         this.index -= UTF16.getCharCount(c);
         return c;
      }

      return -1;
   }
}
