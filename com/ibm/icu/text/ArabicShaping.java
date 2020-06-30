package com.ibm.icu.text;

import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.text.ArabicShapingException;

public final class ArabicShaping {
   private final int options;
   private boolean isLogical;
   private boolean spacesRelativeToTextBeginEnd;
   private char tailChar;
   public static final int SEEN_TWOCELL_NEAR = 2097152;
   public static final int SEEN_MASK = 7340032;
   public static final int YEHHAMZA_TWOCELL_NEAR = 16777216;
   public static final int YEHHAMZA_MASK = 58720256;
   public static final int TASHKEEL_BEGIN = 262144;
   public static final int TASHKEEL_END = 393216;
   public static final int TASHKEEL_RESIZE = 524288;
   public static final int TASHKEEL_REPLACE_BY_TATWEEL = 786432;
   public static final int TASHKEEL_MASK = 917504;
   public static final int SPACES_RELATIVE_TO_TEXT_BEGIN_END = 67108864;
   public static final int SPACES_RELATIVE_TO_TEXT_MASK = 67108864;
   public static final int SHAPE_TAIL_NEW_UNICODE = 134217728;
   public static final int SHAPE_TAIL_TYPE_MASK = 134217728;
   public static final int LENGTH_GROW_SHRINK = 0;
   public static final int LAMALEF_RESIZE = 0;
   public static final int LENGTH_FIXED_SPACES_NEAR = 1;
   public static final int LAMALEF_NEAR = 1;
   public static final int LENGTH_FIXED_SPACES_AT_END = 2;
   public static final int LAMALEF_END = 2;
   public static final int LENGTH_FIXED_SPACES_AT_BEGINNING = 3;
   public static final int LAMALEF_BEGIN = 3;
   public static final int LAMALEF_AUTO = 65536;
   public static final int LENGTH_MASK = 65539;
   public static final int LAMALEF_MASK = 65539;
   public static final int TEXT_DIRECTION_LOGICAL = 0;
   public static final int TEXT_DIRECTION_VISUAL_RTL = 0;
   public static final int TEXT_DIRECTION_VISUAL_LTR = 4;
   public static final int TEXT_DIRECTION_MASK = 4;
   public static final int LETTERS_NOOP = 0;
   public static final int LETTERS_SHAPE = 8;
   public static final int LETTERS_UNSHAPE = 16;
   public static final int LETTERS_SHAPE_TASHKEEL_ISOLATED = 24;
   public static final int LETTERS_MASK = 24;
   public static final int DIGITS_NOOP = 0;
   public static final int DIGITS_EN2AN = 32;
   public static final int DIGITS_AN2EN = 64;
   public static final int DIGITS_EN2AN_INIT_LR = 96;
   public static final int DIGITS_EN2AN_INIT_AL = 128;
   public static final int DIGITS_MASK = 224;
   public static final int DIGIT_TYPE_AN = 0;
   public static final int DIGIT_TYPE_AN_EXTENDED = 256;
   public static final int DIGIT_TYPE_MASK = 256;
   private static final char HAMZAFE_CHAR = 'ﺀ';
   private static final char HAMZA06_CHAR = 'ء';
   private static final char YEH_HAMZA_CHAR = 'ئ';
   private static final char YEH_HAMZAFE_CHAR = 'ﺉ';
   private static final char LAMALEF_SPACE_SUB = '\uffff';
   private static final char TASHKEEL_SPACE_SUB = '\ufffe';
   private static final char LAM_CHAR = 'ل';
   private static final char SPACE_CHAR = ' ';
   private static final char SHADDA_CHAR = 'ﹼ';
   private static final char SHADDA06_CHAR = 'ّ';
   private static final char TATWEEL_CHAR = 'ـ';
   private static final char SHADDA_TATWEEL_CHAR = 'ﹽ';
   private static final char NEW_TAIL_CHAR = 'ﹳ';
   private static final char OLD_TAIL_CHAR = '\u200b';
   private static final int SHAPE_MODE = 0;
   private static final int DESHAPE_MODE = 1;
   private static final int IRRELEVANT = 4;
   private static final int LAMTYPE = 16;
   private static final int ALEFTYPE = 32;
   private static final int LINKR = 1;
   private static final int LINKL = 2;
   private static final int LINK_MASK = 3;
   private static final int[] irrelevantPos = new int[]{0, 2, 4, 6, 8, 10, 12, 14};
   private static final int[] tailFamilyIsolatedFinal = new int[]{1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
   private static final int[] tashkeelMedial = new int[]{0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};
   private static final char[] yehHamzaToYeh = new char[]{'ﻯ', 'ﻰ'};
   private static final char[] convertNormalizedLamAlef = new char[]{'آ', 'أ', 'إ', 'ا'};
   private static final int[] araLink = new int[]{4385, 4897, 5377, 5921, 6403, 7457, 7939, 8961, 9475, 10499, 11523, 12547, 13571, 14593, 15105, 15617, 16129, 16643, 17667, 18691, 19715, 20739, 21763, 22787, 23811, 0, 0, 0, 0, 0, 3, 24835, 25859, 26883, 27923, 28931, 29955, 30979, 32001, 32513, '脃', 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, '蔁', '蜁', '褁', '謁', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 33, 33, 0, 33, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1, 1};
   private static final int[] presLink = new int[]{3, 3, 3, 0, 3, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 32, 33, 32, 33, 0, 1, 32, 33, 0, 2, 3, 1, 32, 33, 0, 2, 3, 1, 0, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 16, 18, 19, 17, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 1, 0, 1};
   private static int[] convertFEto06 = new int[]{1611, 1611, 1612, 1612, 1613, 1613, 1614, 1614, 1615, 1615, 1616, 1616, 1617, 1617, 1618, 1618, 1569, 1570, 1570, 1571, 1571, 1572, 1572, 1573, 1573, 1574, 1574, 1574, 1574, 1575, 1575, 1576, 1576, 1576, 1576, 1577, 1577, 1578, 1578, 1578, 1578, 1579, 1579, 1579, 1579, 1580, 1580, 1580, 1580, 1581, 1581, 1581, 1581, 1582, 1582, 1582, 1582, 1583, 1583, 1584, 1584, 1585, 1585, 1586, 1586, 1587, 1587, 1587, 1587, 1588, 1588, 1588, 1588, 1589, 1589, 1589, 1589, 1590, 1590, 1590, 1590, 1591, 1591, 1591, 1591, 1592, 1592, 1592, 1592, 1593, 1593, 1593, 1593, 1594, 1594, 1594, 1594, 1601, 1601, 1601, 1601, 1602, 1602, 1602, 1602, 1603, 1603, 1603, 1603, 1604, 1604, 1604, 1604, 1605, 1605, 1605, 1605, 1606, 1606, 1606, 1606, 1607, 1607, 1607, 1607, 1608, 1608, 1609, 1609, 1610, 1610, 1610, 1610, 1628, 1628, 1629, 1629, 1630, 1630, 1631, 1631};
   private static final int[][][] shapeTable = new int[][][]{{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 1, 0, 3}, {0, 1, 0, 1}}, {{0, 0, 2, 2}, {0, 0, 1, 2}, {0, 1, 1, 2}, {0, 1, 1, 3}}, {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 1, 0, 3}, {0, 1, 0, 3}}, {{0, 0, 1, 2}, {0, 0, 1, 2}, {0, 1, 1, 2}, {0, 1, 1, 3}}};

   public int shape(char[] source, int sourceStart, int sourceLength, char[] dest, int destStart, int destSize) throws ArabicShapingException {
      if(source == null) {
         throw new IllegalArgumentException("source can not be null");
      } else if(sourceStart >= 0 && sourceLength >= 0 && sourceStart + sourceLength <= source.length) {
         if(dest == null && destSize != 0) {
            throw new IllegalArgumentException("null dest requires destSize == 0");
         } else if(destSize == 0 || destStart >= 0 && destSize >= 0 && destStart + destSize <= dest.length) {
            if((this.options & 917504) > 0 && (this.options & 917504) != 262144 && (this.options & 917504) != 393216 && (this.options & 917504) != 524288 && (this.options & 917504) != 786432) {
               throw new IllegalArgumentException("Wrong Tashkeel argument");
            } else if((this.options & 65539) > 0 && (this.options & 65539) != 3 && (this.options & 65539) != 2 && (this.options & 65539) != 0 && (this.options & 65539) != 65536 && (this.options & 65539) != 1) {
               throw new IllegalArgumentException("Wrong Lam Alef argument");
            } else if((this.options & 917504) > 0 && (this.options & 24) == 16) {
               throw new IllegalArgumentException("Tashkeel replacement should not be enabled in deshaping mode ");
            } else {
               return this.internalShape(source, sourceStart, sourceLength, dest, destStart, destSize);
            }
         } else {
            throw new IllegalArgumentException("bad dest start (" + destStart + ") or size (" + destSize + ") for buffer of length " + dest.length);
         }
      } else {
         throw new IllegalArgumentException("bad source start (" + sourceStart + ") or length (" + sourceLength + ") for buffer of length " + source.length);
      }
   }

   public void shape(char[] source, int start, int length) throws ArabicShapingException {
      if((this.options & 65539) == 0) {
         throw new ArabicShapingException("Cannot shape in place with length option resize.");
      } else {
         this.shape(source, start, length, source, start, length);
      }
   }

   public String shape(String text) throws ArabicShapingException {
      char[] src = text.toCharArray();
      char[] dest = src;
      if((this.options & 65539) == 0 && (this.options & 24) == 16) {
         dest = new char[src.length * 2];
      }

      int len = this.shape(src, 0, src.length, dest, 0, dest.length);
      return new String(dest, 0, len);
   }

   public ArabicShaping(int options) {
      this.options = options;
      if((options & 224) > 128) {
         throw new IllegalArgumentException("bad DIGITS options");
      } else {
         this.isLogical = (options & 4) == 0;
         this.spacesRelativeToTextBeginEnd = (options & 67108864) == 67108864;
         if((options & 134217728) == 134217728) {
            this.tailChar = 'ﹳ';
         } else {
            this.tailChar = 8203;
         }

      }
   }

   public boolean equals(Object rhs) {
      return rhs != null && rhs.getClass() == ArabicShaping.class && this.options == ((ArabicShaping)rhs).options;
   }

   public int hashCode() {
      return this.options;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder(super.toString());
      buf.append('[');
      switch(this.options & 65539) {
      case 0:
         buf.append("LamAlef resize");
         break;
      case 1:
         buf.append("LamAlef spaces at near");
         break;
      case 2:
         buf.append("LamAlef spaces at end");
         break;
      case 3:
         buf.append("LamAlef spaces at begin");
         break;
      case 65536:
         buf.append("lamAlef auto");
      }

      switch(this.options & 4) {
      case 0:
         buf.append(", logical");
         break;
      case 4:
         buf.append(", visual");
      }

      switch(this.options & 24) {
      case 0:
         buf.append(", no letter shaping");
         break;
      case 8:
         buf.append(", shape letters");
         break;
      case 16:
         buf.append(", unshape letters");
         break;
      case 24:
         buf.append(", shape letters tashkeel isolated");
      }

      switch(this.options & 7340032) {
      case 2097152:
         buf.append(", Seen at near");
      default:
         switch(this.options & 58720256) {
         case 16777216:
            buf.append(", Yeh Hamza at near");
         default:
            switch(this.options & 917504) {
            case 262144:
               buf.append(", Tashkeel at begin");
               break;
            case 393216:
               buf.append(", Tashkeel at end");
               break;
            case 524288:
               buf.append(", Tashkeel resize");
               break;
            case 786432:
               buf.append(", Tashkeel replace with tatweel");
            }

            switch(this.options & 224) {
            case 0:
               buf.append(", no digit shaping");
               break;
            case 32:
               buf.append(", shape digits to AN");
               break;
            case 64:
               buf.append(", shape digits to EN");
               break;
            case 96:
               buf.append(", shape digits to AN contextually: default EN");
               break;
            case 128:
               buf.append(", shape digits to AN contextually: default AL");
            }

            switch(this.options & 256) {
            case 0:
               buf.append(", standard Arabic-Indic digits");
               break;
            case 256:
               buf.append(", extended Arabic-Indic digits");
            }

            buf.append("]");
            return buf.toString();
         }
      }
   }

   private void shapeToArabicDigitsWithContext(char[] dest, int start, int length, char digitBase, boolean lastStrongWasAL) {
      UBiDiProps bdp = UBiDiProps.INSTANCE;
      digitBase = (char)(digitBase - 48);
      int i = start + length;

      while(true) {
         --i;
         if(i < start) {
            return;
         }

         char ch = dest[i];
         switch(bdp.getClass(ch)) {
         case 0:
         case 1:
            lastStrongWasAL = false;
            break;
         case 2:
            if(lastStrongWasAL && ch <= 57) {
               dest[i] = (char)(ch + digitBase);
            }
            break;
         case 13:
            lastStrongWasAL = true;
         }
      }
   }

   private static void invertBuffer(char[] buffer, int start, int length) {
      int i = start;

      for(int j = start + length - 1; i < j; --j) {
         char temp = buffer[i];
         buffer[i] = buffer[j];
         buffer[j] = temp;
         ++i;
      }

   }

   private static char changeLamAlef(char ch) {
      switch(ch) {
      case 'آ':
         return 'ٜ';
      case 'أ':
         return 'ٝ';
      case 'ؤ':
      case 'ئ':
      default:
         return '\u0000';
      case 'إ':
         return 'ٞ';
      case 'ا':
         return 'ٟ';
      }
   }

   private static int specialChar(char ch) {
      return (ch <= 1569 || ch >= 1574) && ch != 1575 && (ch <= 1582 || ch >= 1587) && (ch <= 1607 || ch >= 1610) && ch != 1577?(ch >= 1611 && ch <= 1618?2:((ch < 1619 || ch > 1621) && ch != 1648 && (ch < 'ﹰ' || ch > 'ﹿ')?0:3)):1;
   }

   private static int getLink(char ch) {
      return ch >= 1570 && ch <= 1747?araLink[ch - 1570]:(ch == 8205?3:(ch >= 8301 && ch <= 8303?4:(ch >= 'ﹰ' && ch <= 'ﻼ'?presLink[ch - 'ﹰ']:0)));
   }

   private static int countSpacesLeft(char[] dest, int start, int count) {
      int i = start;

      for(int e = start + count; i < e; ++i) {
         if(dest[i] != 32) {
            return i - start;
         }
      }

      return count;
   }

   private static int countSpacesRight(char[] dest, int start, int count) {
      int i = start + count;

      while(true) {
         --i;
         if(i < start) {
            return count;
         }

         if(dest[i] != 32) {
            break;
         }
      }

      return start + count - 1 - i;
   }

   private static boolean isTashkeelChar(char ch) {
      return ch >= 1611 && ch <= 1618;
   }

   private static int isSeenTailFamilyChar(char ch) {
      return ch >= 'ﺱ' && ch < 'ﺿ'?tailFamilyIsolatedFinal[ch - 'ﺱ']:0;
   }

   private static int isSeenFamilyChar(char ch) {
      return ch >= 1587 && ch <= 1590?1:0;
   }

   private static boolean isTailChar(char ch) {
      return ch == 8203 || ch == 'ﹳ';
   }

   private static boolean isAlefMaksouraChar(char ch) {
      return ch == 'ﻯ' || ch == 'ﻰ' || ch == 1609;
   }

   private static boolean isYehHamzaChar(char ch) {
      return ch == 'ﺉ' || ch == 'ﺊ';
   }

   private static boolean isTashkeelCharFE(char ch) {
      return ch != '\ufe75' && ch >= 'ﹰ' && ch <= 'ﹿ';
   }

   private static int isTashkeelOnTatweelChar(char ch) {
      return ch >= 'ﹰ' && ch <= 'ﹿ' && ch != 'ﹳ' && ch != '\ufe75' && ch != 'ﹽ'?tashkeelMedial[ch - 'ﹰ']:((ch < 'ﳲ' || ch > 'ﳴ') && ch != 'ﹽ'?0:2);
   }

   private static int isIsolatedTashkeelChar(char ch) {
      return ch >= 'ﹰ' && ch <= 'ﹿ' && ch != 'ﹳ' && ch != '\ufe75'?1 - tashkeelMedial[ch - 'ﹰ']:(ch >= 'ﱞ' && ch <= 'ﱣ'?1:0);
   }

   private static boolean isAlefChar(char ch) {
      return ch == 1570 || ch == 1571 || ch == 1573 || ch == 1575;
   }

   private static boolean isLamAlefChar(char ch) {
      return ch >= 'ﻵ' && ch <= 'ﻼ';
   }

   private static boolean isNormalizedLamAlefChar(char ch) {
      return ch >= 1628 && ch <= 1631;
   }

   private int calculateSize(char[] source, int sourceStart, int sourceLength) {
      int destSize = sourceLength;
      switch(this.options & 24) {
      case 8:
      case 24:
         if(this.isLogical) {
            int i = sourceStart;

            for(int e = sourceStart + sourceLength - 1; i < e; ++i) {
               if(source[i] == 1604 && isAlefChar(source[i + 1]) || isTashkeelCharFE(source[i])) {
                  --destSize;
               }
            }

            return destSize;
         } else {
            int i = sourceStart + 1;

            for(int e = sourceStart + sourceLength; i < e; ++i) {
               if(source[i] == 1604 && isAlefChar(source[i - 1]) || isTashkeelCharFE(source[i])) {
                  --destSize;
               }
            }

            return destSize;
         }
      case 16:
         int i = sourceStart;

         for(int e = sourceStart + sourceLength; i < e; ++i) {
            if(isLamAlefChar(source[i])) {
               ++destSize;
            }
         }
      }

      return destSize;
   }

   private static int countSpaceSub(char[] dest, int length, char subChar) {
      int i = 0;

      int count;
      for(count = 0; i < length; ++i) {
         if(dest[i] == subChar) {
            ++count;
         }
      }

      return count;
   }

   private static void shiftArray(char[] dest, int start, int e, char subChar) {
      int w = e;
      int r = e;

      while(true) {
         --r;
         if(r < start) {
            return;
         }

         char ch = dest[r];
         if(ch != subChar) {
            --w;
            if(w != r) {
               dest[w] = ch;
            }
         }
      }
   }

   private static int flipArray(char[] dest, int start, int e, int w) {
      if(w > start) {
         int r = w;

         for(w = start; r < e; dest[w++] = dest[r++]) {
            ;
         }
      } else {
         w = e;
      }

      return w;
   }

   private static int handleTashkeelWithTatweel(char[] dest, int sourceLength) {
      for(int i = 0; i < sourceLength; ++i) {
         if(isTashkeelOnTatweelChar(dest[i]) == 1) {
            dest[i] = 1600;
         } else if(isTashkeelOnTatweelChar(dest[i]) == 2) {
            dest[i] = 'ﹽ';
         } else if(isIsolatedTashkeelChar(dest[i]) == 1 && dest[i] != 'ﹼ') {
            dest[i] = 32;
         }
      }

      return sourceLength;
   }

   private int handleGeneratedSpaces(char[] dest, int start, int length) {
      int lenOptionsLamAlef = this.options & 65539;
      int lenOptionsTashkeel = this.options & 917504;
      boolean lamAlefOn = false;
      boolean tashkeelOn = false;
      if(!this.isLogical & !this.spacesRelativeToTextBeginEnd) {
         switch(lenOptionsLamAlef) {
         case 2:
            lenOptionsLamAlef = 3;
            break;
         case 3:
            lenOptionsLamAlef = 2;
         }

         switch(lenOptionsTashkeel) {
         case 262144:
            lenOptionsTashkeel = 393216;
            break;
         case 393216:
            lenOptionsTashkeel = 262144;
         }
      }

      if(lenOptionsLamAlef == 1) {
         int i = start;

         for(int e = start + length; i < e; ++i) {
            if(dest[i] == '\uffff') {
               dest[i] = 32;
            }
         }
      } else {
         int e = start + length;
         int wL = countSpaceSub(dest, length, '\uffff');
         int wT = countSpaceSub(dest, length, '\ufffe');
         if(lenOptionsLamAlef == 2) {
            lamAlefOn = true;
         }

         if(lenOptionsTashkeel == 393216) {
            tashkeelOn = true;
         }

         if(lamAlefOn && lenOptionsLamAlef == 2) {
            shiftArray(dest, start, e, '\uffff');

            while(wL > start) {
               --wL;
               dest[wL] = 32;
            }
         }

         if(tashkeelOn && lenOptionsTashkeel == 393216) {
            shiftArray(dest, start, e, '\ufffe');

            while(wT > start) {
               --wT;
               dest[wT] = 32;
            }
         }

         lamAlefOn = false;
         tashkeelOn = false;
         if(lenOptionsLamAlef == 0) {
            lamAlefOn = true;
         }

         if(lenOptionsTashkeel == 524288) {
            tashkeelOn = true;
         }

         if(lamAlefOn && lenOptionsLamAlef == 0) {
            shiftArray(dest, start, e, '\uffff');
            wL = flipArray(dest, start, e, wL);
            length = wL - start;
         }

         if(tashkeelOn && lenOptionsTashkeel == 524288) {
            shiftArray(dest, start, e, '\ufffe');
            wT = flipArray(dest, start, e, wT);
            length = wT - start;
         }

         lamAlefOn = false;
         tashkeelOn = false;
         if(lenOptionsLamAlef == 3 || lenOptionsLamAlef == 65536) {
            lamAlefOn = true;
         }

         if(lenOptionsTashkeel == 262144) {
            tashkeelOn = true;
         }

         if(lamAlefOn && (lenOptionsLamAlef == 3 || lenOptionsLamAlef == 65536)) {
            shiftArray(dest, start, e, '\uffff');

            for(wL = flipArray(dest, start, e, wL); wL < e; dest[wL++] = 32) {
               ;
            }
         }

         if(tashkeelOn && lenOptionsTashkeel == 262144) {
            shiftArray(dest, start, e, '\ufffe');

            for(wT = flipArray(dest, start, e, wT); wT < e; dest[wT++] = 32) {
               ;
            }
         }
      }

      return length;
   }

   private boolean expandCompositCharAtBegin(char[] dest, int start, int length, int lacount) {
      boolean spaceNotFound = false;
      if(lacount > countSpacesRight(dest, start, length)) {
         spaceNotFound = true;
         return spaceNotFound;
      } else {
         int r = start + length - lacount;
         int w = start + length;

         while(true) {
            --r;
            if(r < start) {
               return spaceNotFound;
            }

            char ch = dest[r];
            if(isNormalizedLamAlefChar(ch)) {
               --w;
               dest[w] = 1604;
               --w;
               dest[w] = convertNormalizedLamAlef[ch - 1628];
            } else {
               --w;
               dest[w] = ch;
            }
         }
      }
   }

   private boolean expandCompositCharAtEnd(char[] dest, int start, int length, int lacount) {
      boolean spaceNotFound = false;
      if(lacount > countSpacesLeft(dest, start, length)) {
         spaceNotFound = true;
         return spaceNotFound;
      } else {
         int r = start + lacount;
         int w = start;

         for(int e = start + length; r < e; ++r) {
            char ch = dest[r];
            if(isNormalizedLamAlefChar(ch)) {
               dest[w++] = convertNormalizedLamAlef[ch - 1628];
               dest[w++] = 1604;
            } else {
               dest[w++] = ch;
            }
         }

         return spaceNotFound;
      }
   }

   private boolean expandCompositCharAtNear(char[] dest, int start, int length, int yehHamzaOption, int seenTailOption, int lamAlefOption) {
      boolean spaceNotFound = false;
      if(isNormalizedLamAlefChar(dest[start])) {
         spaceNotFound = true;
         return spaceNotFound;
      } else {
         int i = start + length;

         while(true) {
            --i;
            if(i < start) {
               return false;
            }

            char ch = dest[i];
            if(lamAlefOption == 1 && isNormalizedLamAlefChar(ch)) {
               if(i <= start || dest[i - 1] != 32) {
                  spaceNotFound = true;
                  return spaceNotFound;
               }

               dest[i] = 1604;
               --i;
               dest[i] = convertNormalizedLamAlef[ch - 1628];
            } else if(seenTailOption == 1 && isSeenTailFamilyChar(ch) == 1) {
               if(i <= start || dest[i - 1] != 32) {
                  spaceNotFound = true;
                  return spaceNotFound;
               }

               dest[i - 1] = this.tailChar;
            } else if(yehHamzaOption == 1 && isYehHamzaChar(ch)) {
               if(i <= start || dest[i - 1] != 32) {
                  spaceNotFound = true;
                  return spaceNotFound;
               }

               dest[i] = yehHamzaToYeh[ch - 'ﺉ'];
               dest[i - 1] = 'ﺀ';
            }
         }
      }
   }

   private int expandCompositChar(char[] dest, int start, int length, int lacount, int shapingMode) throws ArabicShapingException {
      int lenOptionsLamAlef = this.options & 65539;
      int lenOptionsSeen = this.options & 7340032;
      int lenOptionsYehHamza = this.options & 58720256;
      boolean spaceNotFound = false;
      if(!this.isLogical && !this.spacesRelativeToTextBeginEnd) {
         switch(lenOptionsLamAlef) {
         case 2:
            lenOptionsLamAlef = 3;
            break;
         case 3:
            lenOptionsLamAlef = 2;
         }
      }

      if(shapingMode == 1) {
         if(lenOptionsLamAlef == 65536) {
            if(this.isLogical) {
               spaceNotFound = this.expandCompositCharAtEnd(dest, start, length, lacount);
               if(spaceNotFound) {
                  spaceNotFound = this.expandCompositCharAtBegin(dest, start, length, lacount);
               }

               if(spaceNotFound) {
                  spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 0, 1);
               }

               if(spaceNotFound) {
                  throw new ArabicShapingException("No spacefor lamalef");
               }
            } else {
               spaceNotFound = this.expandCompositCharAtBegin(dest, start, length, lacount);
               if(spaceNotFound) {
                  spaceNotFound = this.expandCompositCharAtEnd(dest, start, length, lacount);
               }

               if(spaceNotFound) {
                  spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 0, 1);
               }

               if(spaceNotFound) {
                  throw new ArabicShapingException("No spacefor lamalef");
               }
            }
         } else if(lenOptionsLamAlef == 2) {
            spaceNotFound = this.expandCompositCharAtEnd(dest, start, length, lacount);
            if(spaceNotFound) {
               throw new ArabicShapingException("No spacefor lamalef");
            }
         } else if(lenOptionsLamAlef == 3) {
            spaceNotFound = this.expandCompositCharAtBegin(dest, start, length, lacount);
            if(spaceNotFound) {
               throw new ArabicShapingException("No spacefor lamalef");
            }
         } else if(lenOptionsLamAlef == 1) {
            spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 0, 1);
            if(spaceNotFound) {
               throw new ArabicShapingException("No spacefor lamalef");
            }
         } else if(lenOptionsLamAlef == 0) {
            int r = start + length;
            int w = r + lacount;

            while(true) {
               --r;
               if(r < start) {
                  length += lacount;
                  break;
               }

               char ch = dest[r];
               if(isNormalizedLamAlefChar(ch)) {
                  --w;
                  dest[w] = 1604;
                  --w;
                  dest[w] = convertNormalizedLamAlef[ch - 1628];
               } else {
                  --w;
                  dest[w] = ch;
               }
            }
         }
      } else {
         if(lenOptionsSeen == 2097152) {
            spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 1, 0);
            if(spaceNotFound) {
               throw new ArabicShapingException("No space for Seen tail expansion");
            }
         }

         if(lenOptionsYehHamza == 16777216) {
            spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 1, 0, 0);
            if(spaceNotFound) {
               throw new ArabicShapingException("No space for YehHamza expansion");
            }
         }
      }

      return length;
   }

   private int normalize(char[] dest, int start, int length) {
      int lacount = 0;
      int i = start;

      for(int e = start + length; i < e; ++i) {
         char ch = dest[i];
         if(ch >= 'ﹰ' && ch <= 'ﻼ') {
            if(isLamAlefChar(ch)) {
               ++lacount;
            }

            dest[i] = (char)convertFEto06[ch - 'ﹰ'];
         }
      }

      return lacount;
   }

   private int deshapeNormalize(char[] dest, int start, int length) {
      int lacount = 0;
      int yehHamzaComposeEnabled = 0;
      int seenComposeEnabled = 0;
      yehHamzaComposeEnabled = (this.options & 58720256) == 16777216;
      seenComposeEnabled = (this.options & 7340032) == 2097152;
      int i = start;

      for(int e = start + length; i < e; ++i) {
         char ch = dest[i];
         if(yehHamzaComposeEnabled && (ch == 1569 || ch == 'ﺀ') && i < length - 1 && isAlefMaksouraChar(dest[i + 1])) {
            dest[i] = 32;
            dest[i + 1] = 1574;
         } else if(seenComposeEnabled && isTailChar(ch) && i < length - 1 && isSeenTailFamilyChar(dest[i + 1]) == 1) {
            dest[i] = 32;
         } else if(ch >= 'ﹰ' && ch <= 'ﻼ') {
            if(isLamAlefChar(ch)) {
               ++lacount;
            }

            dest[i] = (char)convertFEto06[ch - 'ﹰ'];
         }
      }

      return lacount;
   }

   private int shapeUnicode(char[] dest, int start, int length, int destSize, int tashkeelFlag) throws ArabicShapingException {
      int lamalef_count = this.normalize(dest, start, length);
      boolean lamalef_found = false;
      boolean seenfam_found = false;
      boolean yehhamza_found = false;
      boolean tashkeel_found = false;
      int i = start + length - 1;
      int currLink = getLink(dest[i]);
      int nextLink = 0;
      int prevLink = 0;
      int lastLink = 0;
      int lastPos = i;
      int nx = -2;
      int nw = 0;

      while(i >= 0) {
         if((currLink & '\uff00') > 0 || isTashkeelChar(dest[i])) {
            nw = i - 1;
            nx = -2;

            while(nx < 0) {
               if(nw == -1) {
                  nextLink = 0;
                  nx = Integer.MAX_VALUE;
               } else {
                  nextLink = getLink(dest[nw]);
                  if((nextLink & 4) == 0) {
                     nx = nw;
                  } else {
                     --nw;
                  }
               }
            }

            if((currLink & 32) > 0 && (lastLink & 16) > 0) {
               lamalef_found = true;
               char wLamalef = changeLamAlef(dest[i]);
               if(wLamalef != 0) {
                  dest[i] = '\uffff';
                  dest[lastPos] = wLamalef;
                  i = lastPos;
               }

               lastLink = prevLink;
               currLink = getLink(wLamalef);
            }

            if(i > 0 && dest[i - 1] == 32) {
               if(isSeenFamilyChar(dest[i]) == 1) {
                  seenfam_found = true;
               } else if(dest[i] == 1574) {
                  yehhamza_found = true;
               }
            } else if(i == 0) {
               if(isSeenFamilyChar(dest[i]) == 1) {
                  seenfam_found = true;
               } else if(dest[i] == 1574) {
                  yehhamza_found = true;
               }
            }

            int flag = specialChar(dest[i]);
            int shape = shapeTable[nextLink & 3][lastLink & 3][currLink & 3];
            if(flag == 1) {
               shape &= 1;
            } else if(flag == 2) {
               if(tashkeelFlag != 0 || (lastLink & 2) == 0 || (nextLink & 1) == 0 || dest[i] == 1612 || dest[i] == 1613 || (nextLink & 32) == 32 && (lastLink & 16) == 16) {
                  if(tashkeelFlag == 2 && dest[i] == 1617) {
                     shape = 1;
                  } else {
                     shape = 0;
                  }
               } else {
                  shape = 1;
               }
            }

            if(flag == 2) {
               if(tashkeelFlag == 2 && dest[i] != 1617) {
                  dest[i] = '\ufffe';
                  tashkeel_found = true;
               } else {
                  dest[i] = (char)('ﹰ' + irrelevantPos[dest[i] - 1611] + shape);
               }
            } else {
               dest[i] = (char)('ﹰ' + (currLink >> 8) + shape);
            }
         }

         if((currLink & 4) == 0) {
            prevLink = lastLink;
            lastLink = currLink;
            lastPos = i;
         }

         --i;
         if(i == nx) {
            currLink = nextLink;
            nx = -2;
         } else if(i != -1) {
            currLink = getLink(dest[i]);
         }
      }

      destSize = length;
      if(lamalef_found || tashkeel_found) {
         destSize = this.handleGeneratedSpaces(dest, start, length);
      }

      if(seenfam_found || yehhamza_found) {
         destSize = this.expandCompositChar(dest, start, destSize, lamalef_count, 0);
      }

      return destSize;
   }

   private int deShapeUnicode(char[] dest, int start, int length, int destSize) throws ArabicShapingException {
      int lamalef_count = this.deshapeNormalize(dest, start, length);
      if(lamalef_count != 0) {
         destSize = this.expandCompositChar(dest, start, length, lamalef_count, 1);
      } else {
         destSize = length;
      }

      return destSize;
   }

   private int internalShape(char[] source, int sourceStart, int sourceLength, char[] dest, int destStart, int destSize) throws ArabicShapingException {
      if(sourceLength == 0) {
         return 0;
      } else if(destSize == 0) {
         return (this.options & 24) != 0 && (this.options & 65539) == 0?this.calculateSize(source, sourceStart, sourceLength):sourceLength;
      } else {
         char[] temp = new char[sourceLength * 2];
         System.arraycopy(source, sourceStart, temp, 0, sourceLength);
         if(this.isLogical) {
            invertBuffer(temp, 0, sourceLength);
         }

         int outputSize = sourceLength;
         switch(this.options & 24) {
         case 8:
            if((this.options & 917504) > 0 && (this.options & 917504) != 786432) {
               outputSize = this.shapeUnicode(temp, 0, sourceLength, destSize, 2);
            } else {
               outputSize = this.shapeUnicode(temp, 0, sourceLength, destSize, 0);
               if((this.options & 917504) == 786432) {
                  outputSize = handleTashkeelWithTatweel(temp, sourceLength);
               }
            }
            break;
         case 16:
            outputSize = this.deShapeUnicode(temp, 0, sourceLength, destSize);
            break;
         case 24:
            outputSize = this.shapeUnicode(temp, 0, sourceLength, destSize, 1);
         }

         if(outputSize > destSize) {
            throw new ArabicShapingException("not enough room for result data");
         } else {
            if((this.options & 224) != 0) {
               char digitBase = 48;
               switch(this.options & 256) {
               case 0:
                  digitBase = 1632;
                  break;
               case 256:
                  digitBase = 1776;
               }

               label0:
               switch(this.options & 224) {
               case 32:
                  int digitDelta = digitBase - 48;
                  int i = 0;

                  while(true) {
                     if(i >= outputSize) {
                        break label0;
                     }

                     char ch = temp[i];
                     if(ch <= 57 && ch >= 48) {
                        temp[i] = (char)(temp[i] + digitDelta);
                     }

                     ++i;
                  }
               case 64:
                  char digitTop = (char)(digitBase + 9);
                  int digitDelta = 48 - digitBase;
                  int i = 0;

                  while(true) {
                     if(i >= outputSize) {
                        break label0;
                     }

                     char ch = temp[i];
                     if(ch <= digitTop && ch >= digitBase) {
                        temp[i] = (char)(temp[i] + digitDelta);
                     }

                     ++i;
                  }
               case 96:
                  this.shapeToArabicDigitsWithContext(temp, 0, outputSize, digitBase, false);
                  break;
               case 128:
                  this.shapeToArabicDigitsWithContext(temp, 0, outputSize, digitBase, true);
               }
            }

            if(this.isLogical) {
               invertBuffer(temp, 0, outputSize);
            }

            System.arraycopy(temp, 0, dest, destStart, outputSize);
            return outputSize;
         }
      }
   }
}
