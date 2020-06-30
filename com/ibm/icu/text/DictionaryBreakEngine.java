package com.ibm.icu.text;

import com.ibm.icu.text.LanguageBreakEngine;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UnicodeSet;
import java.text.CharacterIterator;
import java.util.Stack;

abstract class DictionaryBreakEngine implements LanguageBreakEngine {
   protected UnicodeSet fSet = new UnicodeSet();
   private final int fTypes;

   public DictionaryBreakEngine(int breakTypes) {
      this.fTypes = breakTypes;
   }

   public boolean handles(int c, int breakType) {
      return breakType >= 0 && breakType < 32 && (1 << breakType & this.fTypes) != 0 && this.fSet.contains(c);
   }

   public int findBreaks(CharacterIterator text_, int startPos, int endPos, boolean reverse, int breakType, Stack foundBreaks) {
      if(breakType >= 0 && breakType < 32 && (1 << breakType & this.fTypes) != 0) {
         int result = 0;
         UCharacterIterator text = UCharacterIterator.getInstance(text_);
         int start = text.getIndex();
         int c = text.current();
         int current;
         int rangeStart;
         int rangeEnd;
         if(reverse) {
            boolean isDict;
            for(isDict = this.fSet.contains(c); (current = text.getIndex()) > startPos && isDict; isDict = this.fSet.contains(c)) {
               c = text.previous();
            }

            rangeStart = current < startPos?startPos:current + (isDict?0:1);
            rangeEnd = start + 1;
         } else {
            while((current = text.getIndex()) < endPos && this.fSet.contains(c)) {
               c = text.next();
            }

            rangeStart = start;
            rangeEnd = current;
         }

         result = this.divideUpDictionaryRange(text, rangeStart, rangeEnd, foundBreaks);
         text.setIndex(current);
         return result;
      } else {
         return 0;
      }
   }

   protected abstract int divideUpDictionaryRange(UCharacterIterator var1, int var2, int var3, Stack var4);
}
