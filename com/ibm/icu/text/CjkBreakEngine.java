package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.CharacterIteration;
import com.ibm.icu.text.DictionaryData;
import com.ibm.icu.text.DictionaryMatcher;
import com.ibm.icu.text.LanguageBreakEngine;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.UnicodeSet;
import java.io.IOException;
import java.text.CharacterIterator;
import java.util.Stack;

class CjkBreakEngine implements LanguageBreakEngine {
   private static final UnicodeSet fHangulWordSet = new UnicodeSet();
   private static final UnicodeSet fHanWordSet = new UnicodeSet();
   private static final UnicodeSet fKatakanaWordSet = new UnicodeSet();
   private static final UnicodeSet fHiraganaWordSet = new UnicodeSet();
   private final UnicodeSet fWordSet;
   private DictionaryMatcher fDictionary = null;
   private static final int kMaxKatakanaLength = 8;
   private static final int kMaxKatakanaGroupLength = 20;
   private static final int maxSnlp = 255;
   private static final int kint32max = Integer.MAX_VALUE;

   public CjkBreakEngine(boolean korean) throws IOException {
      this.fDictionary = DictionaryData.loadDictionaryFor("Hira");
      if(korean) {
         this.fWordSet = fHangulWordSet;
      } else {
         this.fWordSet = new UnicodeSet();
         this.fWordSet.addAll(fHanWordSet);
         this.fWordSet.addAll(fKatakanaWordSet);
         this.fWordSet.addAll(fHiraganaWordSet);
         this.fWordSet.add((CharSequence)"\\uff70\\u30fc");
      }

   }

   public boolean handles(int c, int breakType) {
      return breakType == 1 && this.fWordSet.contains(c);
   }

   private static int getKatakanaCost(int wordlength) {
      int[] katakanaCost = new int[]{8192, 984, 408, 240, 204, 252, 300, 372, 480};
      return wordlength > 8?8192:katakanaCost[wordlength];
   }

   private static boolean isKatakana(int value) {
      return value >= 12449 && value <= 12542 && value != 12539 || value >= 'ｦ' && value <= 'ﾟ';
   }

   public int findBreaks(CharacterIterator inText, int startPos, int endPos, boolean reverse, int breakType, Stack foundBreaks) {
      if(startPos >= endPos) {
         return 0;
      } else {
         inText.setIndex(startPos);
         int inputLength = endPos - startPos;
         int[] charPositions = new int[inputLength + 1];
         StringBuffer s = new StringBuffer("");
         inText.setIndex(startPos);

         while(inText.getIndex() < endPos) {
            s.append(inText.current());
            inText.next();
         }

         String prenormstr = s.toString();
         boolean isNormalized = Normalizer.quickCheck(prenormstr, Normalizer.NFKC) == Normalizer.YES || Normalizer.isNormalized(prenormstr, Normalizer.NFKC, 0);
         CharacterIterator text = inText;
         int numChars = 0;
         if(isNormalized) {
            int index = 0;

            for(charPositions[0] = 0; index < prenormstr.length(); charPositions[numChars] = index) {
               int codepoint = prenormstr.codePointAt(index);
               index += Character.charCount(codepoint);
               ++numChars;
            }
         } else {
            String normStr = Normalizer.normalize(prenormstr, Normalizer.NFKC);
            text = new java.text.StringCharacterIterator(normStr);
            charPositions = new int[normStr.length() + 1];
            Normalizer normalizer = new Normalizer(prenormstr, Normalizer.NFKC, 0);
            int index = 0;

            for(charPositions[0] = 0; index < normalizer.endIndex(); charPositions[numChars] = index) {
               normalizer.next();
               ++numChars;
               index = normalizer.getIndex();
            }
         }

         int[] bestSnlp = new int[numChars + 1];
         bestSnlp[0] = 0;

         for(int i = 1; i <= numChars; ++i) {
            bestSnlp[i] = Integer.MAX_VALUE;
         }

         int[] prev = new int[numChars + 1];

         for(int i = 0; i <= numChars; ++i) {
            prev[i] = -1;
         }

         int maxWordSize = 20;
         int[] values = new int[numChars];
         int[] lengths = new int[numChars];
         boolean is_prev_katakana = false;

         for(int i = 0; i < numChars; ++i) {
            text.setIndex(i);
            if(bestSnlp[i] != Integer.MAX_VALUE) {
               int maxSearchLength = i + 20 < numChars?20:numChars - i;
               int[] count_ = new int[1];
               this.fDictionary.matches(text, maxSearchLength, lengths, count_, maxSearchLength, values);
               int count = count_[0];
               if((count == 0 || lengths[0] != 1) && CharacterIteration.current32(text) != Integer.MAX_VALUE && !fHangulWordSet.contains(CharacterIteration.current32(text))) {
                  values[count] = 255;
                  lengths[count] = 1;
                  ++count;
               }

               for(int j = 0; j < count; ++j) {
                  int newSnlp = bestSnlp[i] + values[j];
                  if(newSnlp < bestSnlp[lengths[j] + i]) {
                     bestSnlp[lengths[j] + i] = newSnlp;
                     prev[lengths[j] + i] = i;
                  }
               }

               text.setIndex(i);
               boolean is_katakana = isKatakana(CharacterIteration.current32(text));
               if(!is_prev_katakana && is_katakana) {
                  int j = i + 1;
                  CharacterIteration.next32(text);

                  while(j < numChars && j - i < 20 && isKatakana(CharacterIteration.current32(text))) {
                     CharacterIteration.next32(text);
                     ++j;
                  }

                  if(j - i < 20) {
                     int newSnlp = bestSnlp[i] + getKatakanaCost(j - i);
                     if(newSnlp < bestSnlp[j]) {
                        bestSnlp[j] = newSnlp;
                        prev[j] = i;
                     }
                  }
               }

               is_prev_katakana = is_katakana;
            }
         }

         int[] t_boundary = new int[numChars + 1];
         int numBreaks = 0;
         if(bestSnlp[numChars] == Integer.MAX_VALUE) {
            t_boundary[numBreaks] = numChars;
            ++numBreaks;
         } else {
            for(int i = numChars; i > 0; i = prev[i]) {
               t_boundary[numBreaks] = i;
               ++numBreaks;
            }

            Assert.assrt(prev[t_boundary[numBreaks - 1]] == 0);
         }

         if(foundBreaks.size() == 0 || ((Integer)foundBreaks.peek()).intValue() < startPos) {
            t_boundary[numBreaks++] = 0;
         }

         for(int i = numBreaks - 1; i >= 0; --i) {
            int pos = charPositions[t_boundary[i]] + startPos;
            if(!foundBreaks.contains(Integer.valueOf(pos)) && pos != startPos) {
               foundBreaks.push(Integer.valueOf(charPositions[t_boundary[i]] + startPos));
            }
         }

         if(!foundBreaks.empty() && ((Integer)foundBreaks.peek()).intValue() == endPos) {
            foundBreaks.pop();
         }

         if(!foundBreaks.empty()) {
            inText.setIndex(((Integer)foundBreaks.peek()).intValue());
         }

         return 0;
      }
   }

   static {
      fHangulWordSet.applyPattern("[\\uac00-\\ud7a3]");
      fHanWordSet.applyPattern("[:Han:]");
      fKatakanaWordSet.applyPattern("[[:Katakana:]\\uff9e\\uff9f]");
      fHiraganaWordSet.applyPattern("[:Hiragana:]");
      fHangulWordSet.freeze();
      fHanWordSet.freeze();
      fKatakanaWordSet.freeze();
      fHiraganaWordSet.freeze();
   }
}
