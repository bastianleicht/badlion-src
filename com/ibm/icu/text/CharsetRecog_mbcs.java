package com.ibm.icu.text;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.ibm.icu.text.CharsetRecognizer;
import java.util.Arrays;

abstract class CharsetRecog_mbcs extends CharsetRecognizer {
   abstract String getName();

   int match(CharsetDetector det, int[] commonChars) {
      int singleByteCharCount = 0;
      int doubleByteCharCount = 0;
      int commonCharCount = 0;
      int badCharCount = 0;
      int totalCharCount = 0;
      int confidence = 0;
      CharsetRecog_mbcs.iteratedChar iter = new CharsetRecog_mbcs.iteratedChar();
      iter.reset();

      while(true) {
         if(!this.nextChar(iter, det)) {
            if(doubleByteCharCount <= 10 && badCharCount == 0) {
               if(doubleByteCharCount == 0 && totalCharCount < 10) {
                  confidence = 0;
               } else {
                  confidence = 10;
               }
               break;
            }

            if(doubleByteCharCount < 20 * badCharCount) {
               confidence = 0;
            } else if(commonChars == null) {
               confidence = 30 + doubleByteCharCount - 20 * badCharCount;
               if(confidence > 100) {
                  confidence = 100;
               }
            } else {
               double maxVal = Math.log((double)((float)doubleByteCharCount / 4.0F));
               double scaleFactor = 90.0D / maxVal;
               confidence = (int)(Math.log((double)(commonCharCount + 1)) * scaleFactor + 10.0D);
               confidence = Math.min(confidence, 100);
            }
            break;
         }

         ++totalCharCount;
         if(iter.error) {
            ++badCharCount;
         } else {
            long cv = (long)iter.charValue & 4294967295L;
            if(cv <= 255L) {
               ++singleByteCharCount;
            } else {
               ++doubleByteCharCount;
               if(commonChars != null && Arrays.binarySearch(commonChars, (int)cv) >= 0) {
                  ++commonCharCount;
               }
            }
         }

         if(badCharCount >= 2 && badCharCount * 5 >= doubleByteCharCount) {
            break;
         }
      }

      return confidence;
   }

   abstract boolean nextChar(CharsetRecog_mbcs.iteratedChar var1, CharsetDetector var2);

   static class CharsetRecog_big5 extends CharsetRecog_mbcs {
      static int[] commonChars = new int[]{'ꅀ', 'ꅁ', 'ꅂ', 'ꅃ', 'ꅇ', 'ꅉ', 'ꅵ', 'ꅶ', 'ꑀ', 'ꑆ', 'ꑇ', 'ꑈ', 'ꑑ', 'ꑔ', 'ꑗ', 'ꑤ', 'ꑪ', 'ꑬ', 'ꑷ', '꒣', '꒤', '꒧', '꓁', '\ua4ce', 'ꓑ', 'ꓟ', 'ꓨ', 'ꓽ', 'ꕀ', 'ꕈ', 'ꕘ', 'ꕩ', 'ꗍ', 'ꗧ', 'ꙗ', 'ꙡ', 'Ꙣ', 'Ꙩ', '꙰', 'ꚨ', 'ꚳ', 'ꚹ', 'ꛓ', 'ꛛ', 'ꛦ', '꛲', 'Ꝁ', 'ꝑ', 'ꝙ', '\ua7da', 'ꢣ', 'ꢥ', 'ꢭ', '꣑', '꣓', '꣤', '\ua8fc', '꧀', '꧒', '\ua9f3', 'ꩫ', 'ꪺ', 'ꪾ', '\uaacc', '\uaafc', '걇', '걏', '결', '곒', '굙', '껉', '꿠', '냪', '녯', '늳', '닄', '덯', '둌', '둎', '때', '떥', '떽', '뗐', '뗘', '뙱', '럭', '롧', '륄', '뫘', '뭄', '뮡', '뷑', '싄', '쎹', '쑀', '쑟'};

      boolean nextChar(CharsetRecog_mbcs.iteratedChar it, CharsetDetector det) {
         it.index = it.nextIndex;
         it.error = false;
         int firstByte = it.charValue = it.nextByte(det);
         if(firstByte < 0) {
            return false;
         } else if(firstByte > 127 && firstByte != 255) {
            int secondByte = it.nextByte(det);
            if(secondByte < 0) {
               return false;
            } else {
               it.charValue = it.charValue << 8 | secondByte;
               if(secondByte < 64 || secondByte == 127 || secondByte == 255) {
                  it.error = true;
               }

               return true;
            }
         } else {
            return true;
         }
      }

      CharsetMatch match(CharsetDetector det) {
         int confidence = this.match(det, commonChars);
         return confidence == 0?null:new CharsetMatch(det, this, confidence);
      }

      String getName() {
         return "Big5";
      }

      public String getLanguage() {
         return "zh";
      }
   }

   abstract static class CharsetRecog_euc extends CharsetRecog_mbcs {
      boolean nextChar(CharsetRecog_mbcs.iteratedChar it, CharsetDetector det) {
         it.index = it.nextIndex;
         it.error = false;
         int firstByte = 0;
         int secondByte = 0;
         int thirdByte = 0;
         firstByte = it.charValue = it.nextByte(det);
         if(firstByte < 0) {
            it.done = true;
         } else if(firstByte > 141) {
            secondByte = it.nextByte(det);
            it.charValue = it.charValue << 8 | secondByte;
            if(firstByte >= 161 && firstByte <= 254) {
               if(secondByte < 161) {
                  it.error = true;
               }
            } else if(firstByte == 142) {
               if(secondByte < 161) {
                  it.error = true;
               }
            } else if(firstByte == 143) {
               thirdByte = it.nextByte(det);
               it.charValue = it.charValue << 8 | thirdByte;
               if(thirdByte < 161) {
                  it.error = true;
               }
            }
         }

         return !it.done;
      }

      static class CharsetRecog_euc_jp extends CharsetRecog_mbcs.CharsetRecog_euc {
         static int[] commonChars = new int[]{'ꆡ', 'ꆢ', 'ꆣ', 'ꆦ', 'ꆼ', 'ꇊ', 'ꇋ', 'ꇖ', 'ꇗ', '꒢', '꒤', '꒦', '꒨', '꒪', '꒫', '꒬', '꒭', '꒯', '꒱', '꒳', '꒵', '꒷', '꒹', '꒻', '꒽', '꒿', '꓀', '꓁', '꓃', '꓄', '꓆', '\ua4c7', '\ua4c8', '\ua4c9', '\ua4ca', '\ua4cb', '\ua4ce', '\ua4cf', 'ꓐ', 'ꓞ', 'ꓟ', 'ꓡ', 'ꓢ', 'ꓤ', 'ꓨ', 'ꓩ', 'ꓪ', 'ꓫ', 'ꓬ', 'ꓯ', 'ꓲ', 'ꓳ', 'ꖢ', 'ꖣ', 'ꖤ', 'ꖦ', 'ꖧ', 'ꖪ', 'ꖭ', 'ꖯ', 'ꖰ', 'ꖳ', 'ꖵ', 'ꖷ', 'ꖸ', 'ꖹ', 'ꖿ', 'ꗃ', 'ꗆ', 'ꗇ', 'ꗈ', 'ꗉ', 'ꗋ', 'ꗐ', 'ꗕ', 'ꗖ', 'ꗗ', 'ꗞ', 'ꗠ', 'ꗡ', 'ꗥ', 'ꗩ', 'ꗪ', 'ꗫ', 'ꗬ', 'ꗭ', 'ꗳ', '뢩', '맔', '뫮', '믈', '뻰', '뾷', '쓪', '웼', '잽', '쪸', '쫳', '쯜', '췑'};

         String getName() {
            return "EUC-JP";
         }

         CharsetMatch match(CharsetDetector det) {
            int confidence = this.match(det, commonChars);
            return confidence == 0?null:new CharsetMatch(det, this, confidence);
         }

         public String getLanguage() {
            return "ja";
         }
      }

      static class CharsetRecog_euc_kr extends CharsetRecog_mbcs.CharsetRecog_euc {
         static int[] commonChars = new int[]{'낡', '낳', '냅', '냍', '냔', '냦', '냭', '냸', '냺', '냼', '놸', '놹', '뇇', '뇗', '뇢', '뎪', '뎻', '듂', '듏', '듙', '듫', '떥', '떵', '떿', '뗇', '뗩', '뛳', '랯', '럂', '럎', '뢦', '뢮', '뢶', '뢸', '뢻', '룩', '릫', '릮', '만', '많', '맽', '몸', '뫎', '뫐', '뫱', '믧', '믳', '믽', '벭', '벺', '볒', '볶', '붺', '뷀', '뷃', '뷅', '뻆', '뻈', '뻟', '뻮', '뻸', '뻺', '뾡', '뾩', '뿀', '뿤', '뿫', '뿬', '뿸', '삧', '삯', '삸', '삺', '삻', '삽', '샇', '샌', '샎', '샏', '샖', '샚', '샥', '샻', '샼', '솤', '솦', '솶', '쇖', '쇟', '쇶', '쇸', '쒡', '엍', '욮', '쟏', '쟑', '쟒', '쟘', '쟥', '좭'};

         String getName() {
            return "EUC-KR";
         }

         CharsetMatch match(CharsetDetector det) {
            int confidence = this.match(det, commonChars);
            return confidence == 0?null:new CharsetMatch(det, this, confidence);
         }

         public String getLanguage() {
            return "ko";
         }
      }
   }

   static class CharsetRecog_gb_18030 extends CharsetRecog_mbcs {
      static int[] commonChars = new int[]{'ꆡ', 'ꆢ', 'ꆣ', 'ꆤ', 'ꆰ', 'ꆱ', 'ꇱ', 'ꇳ', 'ꎡ', 'ꎬ', 'ꎺ', '놨', '놸', '놾', '늻', '돉', '돶', '듳', '떽', '뗄', '뗣', '뚯', '뛔', '뛠', '랢', '램', '랽', '럖', '럝', '뢴', '룟', '룶', '릫', '막', '맘', '맺', '맽', '뫍', '뮧', '믖', '믡', '믺', '벼', '볛', '볾', '뷌', '뻍', '뻝', '뾴', '뿆', '뿉', '살', '샭', '쇋', '싛', '쏇', '쓜', '쓪', '엌', '웷', '쟸', '좫', '죋', '죕', '죧', '짏', '짺', '쪱', '쪵', '쫇', '쫐', '쫖', '쫵', '쫽', '쳬', '췸', '캪', '컄', '컒', '컥', '쾵', '쿂', '쿖', '탂', '탅', '탐', '탔', '톧', '튪', '튲', '튵', '튻', '틔', '폃', '폐', '폽', '퓂', '퓚', '헢', '훐'};

      boolean nextChar(CharsetRecog_mbcs.iteratedChar it, CharsetDetector det) {
         it.index = it.nextIndex;
         it.error = false;
         int firstByte = 0;
         int secondByte = 0;
         int thirdByte = 0;
         int fourthByte = 0;
         firstByte = it.charValue = it.nextByte(det);
         if(firstByte < 0) {
            it.done = true;
         } else if(firstByte > 128) {
            secondByte = it.nextByte(det);
            it.charValue = it.charValue << 8 | secondByte;
            if(firstByte >= 129 && firstByte <= 254 && (secondByte < 64 || secondByte > 126) && (secondByte < 80 || secondByte > 254)) {
               if(secondByte >= 48 && secondByte <= 57) {
                  thirdByte = it.nextByte(det);
                  if(thirdByte >= 129 && thirdByte <= 254) {
                     fourthByte = it.nextByte(det);
                     if(fourthByte >= 48 && fourthByte <= 57) {
                        it.charValue = it.charValue << 16 | thirdByte << 8 | fourthByte;
                        return !it.done;
                     }
                  }
               }

               it.error = true;
            }
         }

         return !it.done;
      }

      String getName() {
         return "GB18030";
      }

      CharsetMatch match(CharsetDetector det) {
         int confidence = this.match(det, commonChars);
         return confidence == 0?null:new CharsetMatch(det, this, confidence);
      }

      public String getLanguage() {
         return "zh";
      }
   }

   static class CharsetRecog_sjis extends CharsetRecog_mbcs {
      static int[] commonChars = new int[]{'腀', '腁', '腂', '腅', '腛', '腩', '腪', '腵', '腶', '芠', '芢', '芤', '芩', '芪', '芫', '芭', '芯', '花', '芳', '芵', '芷', '芽', '芾', '苁', '苄', '苅', '苆', '苈', '苉', '苌', '苍', '苜', '苠', '苧', '苨', '苩', '苪', '苰', '英', '荁', '荃', '荎', '荏', '荘', '荞', '荢', '荧', '荵', '荶', '莉', '莊', '莋', '莍', '莓', '躖', '鏺', '閪'};

      boolean nextChar(CharsetRecog_mbcs.iteratedChar it, CharsetDetector det) {
         it.index = it.nextIndex;
         it.error = false;
         int firstByte = it.charValue = it.nextByte(det);
         if(firstByte < 0) {
            return false;
         } else if(firstByte > 127 && (firstByte <= 160 || firstByte > 223)) {
            int secondByte = it.nextByte(det);
            if(secondByte < 0) {
               return false;
            } else {
               it.charValue = firstByte << 8 | secondByte;
               if((secondByte < 64 || secondByte > 127) && (secondByte < 128 || secondByte > 255)) {
                  it.error = true;
               }

               return true;
            }
         } else {
            return true;
         }
      }

      CharsetMatch match(CharsetDetector det) {
         int confidence = this.match(det, commonChars);
         return confidence == 0?null:new CharsetMatch(det, this, confidence);
      }

      String getName() {
         return "Shift_JIS";
      }

      public String getLanguage() {
         return "ja";
      }
   }

   static class iteratedChar {
      int charValue = 0;
      int index = 0;
      int nextIndex = 0;
      boolean error = false;
      boolean done = false;

      void reset() {
         this.charValue = 0;
         this.index = -1;
         this.nextIndex = 0;
         this.error = false;
         this.done = false;
      }

      int nextByte(CharsetDetector det) {
         if(this.nextIndex >= det.fRawLength) {
            this.done = true;
            return -1;
         } else {
            int byteValue = det.fRawInput[this.nextIndex++] & 255;
            return byteValue;
         }
      }
   }
}
