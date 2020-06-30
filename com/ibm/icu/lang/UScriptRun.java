package com.ibm.icu.lang;

import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.UTF16;

/** @deprecated */
public final class UScriptRun {
   private char[] emptyCharArray = new char[0];
   private char[] text;
   private int textIndex;
   private int textStart;
   private int textLimit;
   private int scriptStart;
   private int scriptLimit;
   private int scriptCode;
   private static int PAREN_STACK_DEPTH = 32;
   private static UScriptRun.ParenStackEntry[] parenStack = new UScriptRun.ParenStackEntry[PAREN_STACK_DEPTH];
   private int parenSP = -1;
   private int pushCount = 0;
   private int fixupCount = 0;
   private static int[] pairedChars = new int[]{40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315};
   private static int pairedCharPower = 1 << highBit(pairedChars.length);
   private static int pairedCharExtra = pairedChars.length - pairedCharPower;

   /** @deprecated */
   public UScriptRun() {
      char[] nullChars = null;
      this.reset((char[])nullChars, 0, 0);
   }

   /** @deprecated */
   public UScriptRun(String text) {
      this.reset(text);
   }

   /** @deprecated */
   public UScriptRun(String text, int start, int count) {
      this.reset(text, start, count);
   }

   /** @deprecated */
   public UScriptRun(char[] chars) {
      this.reset(chars);
   }

   /** @deprecated */
   public UScriptRun(char[] chars, int start, int count) {
      this.reset(chars, start, count);
   }

   /** @deprecated */
   public final void reset() {
      while(this.stackIsNotEmpty()) {
         this.pop();
      }

      this.scriptStart = this.textStart;
      this.scriptLimit = this.textStart;
      this.scriptCode = -1;
      this.parenSP = -1;
      this.pushCount = 0;
      this.fixupCount = 0;
      this.textIndex = this.textStart;
   }

   /** @deprecated */
   public final void reset(int start, int count) throws IllegalArgumentException {
      int len = 0;
      if(this.text != null) {
         len = this.text.length;
      }

      if(start >= 0 && count >= 0 && start <= len - count) {
         this.textStart = start;
         this.textLimit = start + count;
         this.reset();
      } else {
         throw new IllegalArgumentException();
      }
   }

   /** @deprecated */
   public final void reset(char[] chars, int start, int count) {
      if(chars == null) {
         chars = this.emptyCharArray;
      }

      this.text = chars;
      this.reset(start, count);
   }

   /** @deprecated */
   public final void reset(char[] chars) {
      int length = 0;
      if(chars != null) {
         length = chars.length;
      }

      this.reset((char[])chars, 0, length);
   }

   /** @deprecated */
   public final void reset(String str, int start, int count) {
      char[] chars = null;
      if(str != null) {
         chars = str.toCharArray();
      }

      this.reset(chars, start, count);
   }

   /** @deprecated */
   public final void reset(String str) {
      int length = 0;
      if(str != null) {
         length = str.length();
      }

      this.reset((String)str, 0, length);
   }

   /** @deprecated */
   public final int getScriptStart() {
      return this.scriptStart;
   }

   /** @deprecated */
   public final int getScriptLimit() {
      return this.scriptLimit;
   }

   /** @deprecated */
   public final int getScriptCode() {
      return this.scriptCode;
   }

   /** @deprecated */
   public final boolean next() {
      if(this.scriptLimit >= this.textLimit) {
         return false;
      } else {
         this.scriptCode = 0;
         this.scriptStart = this.scriptLimit;
         this.syncFixup();

         while(this.textIndex < this.textLimit) {
            int ch = UTF16.charAt(this.text, this.textStart, this.textLimit, this.textIndex - this.textStart);
            int codePointCount = UTF16.getCharCount(ch);
            int sc = UScript.getScript(ch);
            int pairIndex = getPairIndex(ch);
            this.textIndex += codePointCount;
            if(pairIndex >= 0) {
               if((pairIndex & 1) == 0) {
                  this.push(pairIndex, this.scriptCode);
               } else {
                  int pi = pairIndex & -2;

                  while(this.stackIsNotEmpty() && this.top().pairIndex != pi) {
                     this.pop();
                  }

                  if(this.stackIsNotEmpty()) {
                     sc = this.top().scriptCode;
                  }
               }
            }

            if(!sameScript(this.scriptCode, sc)) {
               this.textIndex -= codePointCount;
               break;
            }

            if(this.scriptCode <= 1 && sc > 1) {
               this.scriptCode = sc;
               this.fixup(this.scriptCode);
            }

            if(pairIndex >= 0 && (pairIndex & 1) != 0) {
               this.pop();
            }
         }

         this.scriptLimit = this.textIndex;
         return true;
      }
   }

   private static boolean sameScript(int scriptOne, int scriptTwo) {
      return scriptOne <= 1 || scriptTwo <= 1 || scriptOne == scriptTwo;
   }

   private static final int mod(int sp) {
      return sp % PAREN_STACK_DEPTH;
   }

   private static final int inc(int sp, int count) {
      return mod(sp + count);
   }

   private static final int inc(int sp) {
      return inc(sp, 1);
   }

   private static final int dec(int sp, int count) {
      return mod(sp + PAREN_STACK_DEPTH - count);
   }

   private static final int dec(int sp) {
      return dec(sp, 1);
   }

   private static final int limitInc(int count) {
      if(count < PAREN_STACK_DEPTH) {
         ++count;
      }

      return count;
   }

   private final boolean stackIsEmpty() {
      return this.pushCount <= 0;
   }

   private final boolean stackIsNotEmpty() {
      return !this.stackIsEmpty();
   }

   private final void push(int pairIndex, int scrptCode) {
      this.pushCount = limitInc(this.pushCount);
      this.fixupCount = limitInc(this.fixupCount);
      this.parenSP = inc(this.parenSP);
      parenStack[this.parenSP] = new UScriptRun.ParenStackEntry(pairIndex, scrptCode);
   }

   private final void pop() {
      if(!this.stackIsEmpty()) {
         parenStack[this.parenSP] = null;
         if(this.fixupCount > 0) {
            --this.fixupCount;
         }

         --this.pushCount;
         this.parenSP = dec(this.parenSP);
         if(this.stackIsEmpty()) {
            this.parenSP = -1;
         }

      }
   }

   private final UScriptRun.ParenStackEntry top() {
      return parenStack[this.parenSP];
   }

   private final void syncFixup() {
      this.fixupCount = 0;
   }

   private final void fixup(int scrptCode) {
      for(int fixupSP = dec(this.parenSP, this.fixupCount); this.fixupCount-- > 0; parenStack[fixupSP].scriptCode = scrptCode) {
         fixupSP = inc(fixupSP);
      }

   }

   private static final byte highBit(int n) {
      if(n <= 0) {
         return (byte)-32;
      } else {
         byte bit = 0;
         if(n >= 65536) {
            n >>= 16;
            bit = (byte)(bit + 16);
         }

         if(n >= 256) {
            n >>= 8;
            bit = (byte)(bit + 8);
         }

         if(n >= 16) {
            n >>= 4;
            bit = (byte)(bit + 4);
         }

         if(n >= 4) {
            n >>= 2;
            bit = (byte)(bit + 2);
         }

         if(n >= 2) {
            n = n >> 1;
            ++bit;
         }

         return bit;
      }
   }

   private static int getPairIndex(int ch) {
      int probe = pairedCharPower;
      int index = 0;
      if(ch >= pairedChars[pairedCharExtra]) {
         index = pairedCharExtra;
      }

      while(probe > 1) {
         probe >>= 1;
         if(ch >= pairedChars[index + probe]) {
            index += probe;
         }
      }

      if(pairedChars[index] != ch) {
         index = -1;
      }

      return index;
   }

   private static final class ParenStackEntry {
      int pairIndex;
      int scriptCode;

      public ParenStackEntry(int thePairIndex, int theScriptCode) {
         this.pairIndex = thePairIndex;
         this.scriptCode = theScriptCode;
      }
   }
}
