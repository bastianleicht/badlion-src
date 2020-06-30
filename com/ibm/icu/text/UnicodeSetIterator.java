package com.ibm.icu.text;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import java.util.Iterator;

public class UnicodeSetIterator {
   public static int IS_STRING = -1;
   public int codepoint;
   public int codepointEnd;
   public String string;
   private UnicodeSet set;
   private int endRange = 0;
   private int range = 0;
   /** @deprecated */
   protected int endElement;
   /** @deprecated */
   protected int nextElement;
   private Iterator stringIterator = null;

   public UnicodeSetIterator(UnicodeSet set) {
      this.reset(set);
   }

   public UnicodeSetIterator() {
      this.reset(new UnicodeSet());
   }

   public boolean next() {
      if(this.nextElement <= this.endElement) {
         this.codepoint = this.codepointEnd = this.nextElement++;
         return true;
      } else if(this.range < this.endRange) {
         this.loadRange(++this.range);
         this.codepoint = this.codepointEnd = this.nextElement++;
         return true;
      } else if(this.stringIterator == null) {
         return false;
      } else {
         this.codepoint = IS_STRING;
         this.string = (String)this.stringIterator.next();
         if(!this.stringIterator.hasNext()) {
            this.stringIterator = null;
         }

         return true;
      }
   }

   public boolean nextRange() {
      if(this.nextElement <= this.endElement) {
         this.codepointEnd = this.endElement;
         this.codepoint = this.nextElement;
         this.nextElement = this.endElement + 1;
         return true;
      } else if(this.range < this.endRange) {
         this.loadRange(++this.range);
         this.codepointEnd = this.endElement;
         this.codepoint = this.nextElement;
         this.nextElement = this.endElement + 1;
         return true;
      } else if(this.stringIterator == null) {
         return false;
      } else {
         this.codepoint = IS_STRING;
         this.string = (String)this.stringIterator.next();
         if(!this.stringIterator.hasNext()) {
            this.stringIterator = null;
         }

         return true;
      }
   }

   public void reset(UnicodeSet uset) {
      this.set = uset;
      this.reset();
   }

   public void reset() {
      this.endRange = this.set.getRangeCount() - 1;
      this.range = 0;
      this.endElement = -1;
      this.nextElement = 0;
      if(this.endRange >= 0) {
         this.loadRange(this.range);
      }

      this.stringIterator = null;
      if(this.set.strings != null) {
         this.stringIterator = this.set.strings.iterator();
         if(!this.stringIterator.hasNext()) {
            this.stringIterator = null;
         }
      }

   }

   public String getString() {
      return this.codepoint != IS_STRING?UTF16.valueOf(this.codepoint):this.string;
   }

   /** @deprecated */
   public UnicodeSet getSet() {
      return this.set;
   }

   /** @deprecated */
   protected void loadRange(int aRange) {
      this.nextElement = this.set.getRangeStart(aRange);
      this.endElement = this.set.getRangeEnd(aRange);
   }
}
