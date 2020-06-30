package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.DictionaryData;
import com.ibm.icu.text.DictionaryMatcher;
import com.ibm.icu.text.LanguageBreakEngine;
import com.ibm.icu.text.UnicodeSet;
import java.io.IOException;
import java.text.CharacterIterator;
import java.util.Stack;

class ThaiBreakEngine implements LanguageBreakEngine {
   private static final byte THAI_LOOKAHEAD = 3;
   private static final byte THAI_ROOT_COMBINE_THRESHOLD = 3;
   private static final byte THAI_PREFIX_COMBINE_THRESHOLD = 3;
   private static final char THAI_PAIYANNOI = 'ฯ';
   private static final char THAI_MAIYAMOK = 'ๆ';
   private static final byte THAI_MIN_WORD = 2;
   private DictionaryMatcher fDictionary = DictionaryData.loadDictionaryFor("Thai");
   private static UnicodeSet fThaiWordSet = new UnicodeSet();
   private static UnicodeSet fEndWordSet = new UnicodeSet();
   private static UnicodeSet fBeginWordSet = new UnicodeSet();
   private static UnicodeSet fSuffixSet = new UnicodeSet();
   private static UnicodeSet fMarkSet = new UnicodeSet();

   public ThaiBreakEngine() throws IOException {
   }

   public boolean handles(int c, int breakType) {
      if(breakType != 1 && breakType != 2) {
         return false;
      } else {
         int script = UCharacter.getIntPropertyValue(c, 4106);
         return script == 38;
      }
   }

   public int findBreaks(CharacterIterator fIter, int rangeStart, int rangeEnd, boolean reverse, int breakType, Stack foundBreaks) {
      if(rangeEnd - rangeStart < 2) {
         return 0;
      } else {
         int wordsFound = 0;
         ThaiBreakEngine.PossibleWord[] words = new ThaiBreakEngine.PossibleWord[3];

         for(int i = 0; i < 3; ++i) {
            words[i] = new ThaiBreakEngine.PossibleWord();
         }

         fIter.setIndex(rangeStart);

         int current;
         while((current = fIter.getIndex()) < rangeEnd) {
            int wordLength = 0;
            int candidates = words[wordsFound % 3].candidates(fIter, this.fDictionary, rangeEnd);
            if(candidates == 1) {
               wordLength = words[wordsFound % 3].acceptMarked(fIter);
               ++wordsFound;
            } else if(candidates > 1) {
               boolean foundBest = false;
               if(fIter.getIndex() < rangeEnd) {
                  while(true) {
                     int wordsMatched = 1;
                     if(words[(wordsFound + 1) % 3].candidates(fIter, this.fDictionary, rangeEnd) > 0) {
                        if(wordsMatched < 2) {
                           words[wordsFound % 3].markCurrent();
                           wordsMatched = 2;
                        }

                        if(fIter.getIndex() >= rangeEnd) {
                           break;
                        }

                        while(true) {
                           if(words[(wordsFound + 2) % 3].candidates(fIter, this.fDictionary, rangeEnd) > 0) {
                              words[wordsFound % 3].markCurrent();
                              foundBest = true;
                              break;
                           }

                           if(!words[(wordsFound + 1) % 3].backUp(fIter)) {
                              break;
                           }
                        }
                     }

                     if(!words[wordsFound % 3].backUp(fIter) || foundBest) {
                        break;
                     }
                  }
               }

               wordLength = words[wordsFound % 3].acceptMarked(fIter);
               ++wordsFound;
            }

            if(fIter.getIndex() < rangeEnd && wordLength < 3) {
               if(words[wordsFound % 3].candidates(fIter, this.fDictionary, rangeEnd) <= 0 && (wordLength == 0 || words[wordsFound % 3].longestPrefix() < 3)) {
                  int remaining = rangeEnd - (current + wordLength);
                  int pc = fIter.current();
                  int chars = 0;

                  while(true) {
                     fIter.next();
                     int uc = fIter.current();
                     ++chars;
                     --remaining;
                     if(remaining <= 0) {
                        break;
                     }

                     if(fEndWordSet.contains(pc) && fBeginWordSet.contains(uc)) {
                        int candidate = words[(wordsFound + 1) % 3].candidates(fIter, this.fDictionary, rangeEnd);
                        fIter.setIndex(current + wordLength + chars);
                        if(candidate > 0) {
                           break;
                        }
                     }

                     pc = uc;
                  }

                  if(wordLength <= 0) {
                     ++wordsFound;
                  }

                  wordLength += chars;
               } else {
                  fIter.setIndex(current + wordLength);
               }
            }

            int currPos;
            while((currPos = fIter.getIndex()) < rangeEnd && fMarkSet.contains(fIter.current())) {
               fIter.next();
               wordLength += fIter.getIndex() - currPos;
            }

            if(fIter.getIndex() < rangeEnd && wordLength > 0) {
               int uc;
               if(words[wordsFound % 3].candidates(fIter, this.fDictionary, rangeEnd) <= 0 && fSuffixSet.contains(uc = fIter.current())) {
                  if(uc == 3631) {
                     if(!fSuffixSet.contains(fIter.previous())) {
                        fIter.next();
                        fIter.next();
                        ++wordLength;
                        uc = fIter.current();
                     } else {
                        fIter.next();
                     }
                  }

                  if(uc == 3654) {
                     if(fIter.previous() != 3654) {
                        fIter.next();
                        fIter.next();
                        ++wordLength;
                     } else {
                        fIter.next();
                     }
                  }
               } else {
                  fIter.setIndex(current + wordLength);
               }
            }

            if(wordLength > 0) {
               foundBreaks.push(Integer.valueOf(current + wordLength));
            }
         }

         if(((Integer)foundBreaks.peek()).intValue() >= rangeEnd) {
            foundBreaks.pop();
            --wordsFound;
         }

         return wordsFound;
      }
   }

   static {
      fThaiWordSet.applyPattern("[[:Thai:]&[:LineBreak=SA:]]");
      fThaiWordSet.compact();
      fMarkSet.applyPattern("[[:Thai:]&[:LineBreak=SA:]&[:M:]]");
      fMarkSet.add(32);
      fEndWordSet = fThaiWordSet;
      fEndWordSet.remove(3633);
      fEndWordSet.remove(3648, 3652);
      fBeginWordSet.add(3585, 3630);
      fBeginWordSet.add(3648, 3652);
      fSuffixSet.add(3631);
      fSuffixSet.add(3654);
      fMarkSet.compact();
      fEndWordSet.compact();
      fBeginWordSet.compact();
      fSuffixSet.compact();
      fThaiWordSet.freeze();
      fMarkSet.freeze();
      fEndWordSet.freeze();
      fBeginWordSet.freeze();
      fSuffixSet.freeze();
   }

   static class PossibleWord {
      private static final int POSSIBLE_WORD_LIST_MAX = 20;
      private int[] lengths = new int[20];
      private int[] count = new int[1];
      private int prefix;
      private int offset = -1;
      private int mark;
      private int current;

      public int candidates(CharacterIterator fIter, DictionaryMatcher dict, int rangeEnd) {
         int start = fIter.getIndex();
         if(start != this.offset) {
            this.offset = start;
            this.prefix = dict.matches(fIter, rangeEnd - start, this.lengths, this.count, this.lengths.length);
            if(this.count[0] <= 0) {
               fIter.setIndex(start);
            }
         }

         if(this.count[0] > 0) {
            fIter.setIndex(start + this.lengths[this.count[0] - 1]);
         }

         this.current = this.count[0] - 1;
         this.mark = this.current;
         return this.count[0];
      }

      public int acceptMarked(CharacterIterator fIter) {
         fIter.setIndex(this.offset + this.lengths[this.mark]);
         return this.lengths[this.mark];
      }

      public boolean backUp(CharacterIterator fIter) {
         if(this.current > 0) {
            fIter.setIndex(this.offset + this.lengths[--this.current]);
            return true;
         } else {
            return false;
         }
      }

      public int longestPrefix() {
         return this.prefix;
      }

      public void markCurrent() {
         this.mark = this.current;
      }
   }
}
