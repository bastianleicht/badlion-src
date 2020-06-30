package com.ibm.icu.text;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.ibm.icu.text.CharsetRecognizer;

abstract class CharsetRecog_2022 extends CharsetRecognizer {
   int match(byte[] text, int textLen, byte[][] escapeSequences) {
      int hits = 0;
      int misses = 0;
      int shifts = 0;

      label18:
      for(int i = 0; i < textLen; ++i) {
         if(text[i] == 27) {
            label37:
            for(int escN = 0; escN < escapeSequences.length; ++escN) {
               byte[] seq = escapeSequences[escN];
               if(textLen - i >= seq.length) {
                  for(int j = 1; j < seq.length; ++j) {
                     if(seq[j] != text[i + j]) {
                        continue label37;
                     }
                  }

                  ++hits;
                  i += seq.length - 1;
                  continue label18;
               }
            }

            ++misses;
         }

         if(text[i] == 14 || text[i] == 15) {
            ++shifts;
         }
      }

      if(hits == 0) {
         return 0;
      } else {
         int quality = (100 * hits - 100 * misses) / (hits + misses);
         if(hits + shifts < 5) {
            quality -= (5 - (hits + shifts)) * 10;
         }

         if(quality < 0) {
            quality = 0;
         }

         return quality;
      }
   }

   static class CharsetRecog_2022CN extends CharsetRecog_2022 {
      private byte[][] escapeSequences = new byte[][]{{(byte)27, (byte)36, (byte)41, (byte)65}, {(byte)27, (byte)36, (byte)41, (byte)71}, {(byte)27, (byte)36, (byte)42, (byte)72}, {(byte)27, (byte)36, (byte)41, (byte)69}, {(byte)27, (byte)36, (byte)43, (byte)73}, {(byte)27, (byte)36, (byte)43, (byte)74}, {(byte)27, (byte)36, (byte)43, (byte)75}, {(byte)27, (byte)36, (byte)43, (byte)76}, {(byte)27, (byte)36, (byte)43, (byte)77}, {(byte)27, (byte)78}, {(byte)27, (byte)79}};

      String getName() {
         return "ISO-2022-CN";
      }

      CharsetMatch match(CharsetDetector det) {
         int confidence = this.match(det.fInputBytes, det.fInputLen, this.escapeSequences);
         return confidence == 0?null:new CharsetMatch(det, this, confidence);
      }
   }

   static class CharsetRecog_2022JP extends CharsetRecog_2022 {
      private byte[][] escapeSequences = new byte[][]{{(byte)27, (byte)36, (byte)40, (byte)67}, {(byte)27, (byte)36, (byte)40, (byte)68}, {(byte)27, (byte)36, (byte)64}, {(byte)27, (byte)36, (byte)65}, {(byte)27, (byte)36, (byte)66}, {(byte)27, (byte)38, (byte)64}, {(byte)27, (byte)40, (byte)66}, {(byte)27, (byte)40, (byte)72}, {(byte)27, (byte)40, (byte)73}, {(byte)27, (byte)40, (byte)74}, {(byte)27, (byte)46, (byte)65}, {(byte)27, (byte)46, (byte)70}};

      String getName() {
         return "ISO-2022-JP";
      }

      CharsetMatch match(CharsetDetector det) {
         int confidence = this.match(det.fInputBytes, det.fInputLen, this.escapeSequences);
         return confidence == 0?null:new CharsetMatch(det, this, confidence);
      }
   }

   static class CharsetRecog_2022KR extends CharsetRecog_2022 {
      private byte[][] escapeSequences = new byte[][]{{(byte)27, (byte)36, (byte)41, (byte)67}};

      String getName() {
         return "ISO-2022-KR";
      }

      CharsetMatch match(CharsetDetector det) {
         int confidence = this.match(det.fInputBytes, det.fInputLen, this.escapeSequences);
         return confidence == 0?null:new CharsetMatch(det, this, confidence);
      }
   }
}
