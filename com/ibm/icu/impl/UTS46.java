package com.ibm.icu.impl;

import com.ibm.icu.impl.Punycode;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.IDNA;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.StringPrepParseException;
import java.io.InputStream;
import java.util.EnumSet;

public final class UTS46 extends IDNA {
   private static final Normalizer2 uts46Norm2 = Normalizer2.getInstance((InputStream)null, "uts46", Normalizer2.Mode.COMPOSE);
   final int options;
   private static final EnumSet severeErrors = EnumSet.of(IDNA.Error.LEADING_COMBINING_MARK, IDNA.Error.DISALLOWED, IDNA.Error.PUNYCODE, IDNA.Error.LABEL_HAS_DOT, IDNA.Error.INVALID_ACE_LABEL);
   private static final byte[] asciiData = new byte[]{(byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)0, (byte)0, (byte)-1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1};
   private static final int L_MASK = U_MASK(0);
   private static final int R_AL_MASK = U_MASK(1) | U_MASK(13);
   private static final int L_R_AL_MASK = L_MASK | R_AL_MASK;
   private static final int R_AL_AN_MASK = R_AL_MASK | U_MASK(5);
   private static final int EN_AN_MASK = U_MASK(2) | U_MASK(5);
   private static final int R_AL_EN_AN_MASK = R_AL_MASK | EN_AN_MASK;
   private static final int L_EN_MASK = L_MASK | U_MASK(2);
   private static final int ES_CS_ET_ON_BN_NSM_MASK = U_MASK(3) | U_MASK(6) | U_MASK(4) | U_MASK(10) | U_MASK(18) | U_MASK(17);
   private static final int L_EN_ES_CS_ET_ON_BN_NSM_MASK = L_EN_MASK | ES_CS_ET_ON_BN_NSM_MASK;
   private static final int R_AL_AN_EN_ES_CS_ET_ON_BN_NSM_MASK = R_AL_MASK | EN_AN_MASK | ES_CS_ET_ON_BN_NSM_MASK;
   private static int U_GC_M_MASK = U_MASK(6) | U_MASK(7) | U_MASK(8);

   public UTS46(int options) {
      this.options = options;
   }

   public StringBuilder labelToASCII(CharSequence label, StringBuilder dest, IDNA.Info info) {
      return this.process(label, true, true, dest, info);
   }

   public StringBuilder labelToUnicode(CharSequence label, StringBuilder dest, IDNA.Info info) {
      return this.process(label, true, false, dest, info);
   }

   public StringBuilder nameToASCII(CharSequence name, StringBuilder dest, IDNA.Info info) {
      this.process(name, false, true, dest, info);
      if(dest.length() >= 254 && !info.getErrors().contains(IDNA.Error.DOMAIN_NAME_TOO_LONG) && isASCIIString(dest) && (dest.length() > 254 || dest.charAt(253) != 46)) {
         addError(info, IDNA.Error.DOMAIN_NAME_TOO_LONG);
      }

      return dest;
   }

   public StringBuilder nameToUnicode(CharSequence name, StringBuilder dest, IDNA.Info info) {
      return this.process(name, false, false, dest, info);
   }

   private static boolean isASCIIString(CharSequence dest) {
      int length = dest.length();

      for(int i = 0; i < length; ++i) {
         if(dest.charAt(i) > 127) {
            return false;
         }
      }

      return true;
   }

   private StringBuilder process(CharSequence src, boolean isLabel, boolean toASCII, StringBuilder dest, IDNA.Info info) {
      if(dest == src) {
         throw new IllegalArgumentException();
      } else {
         dest.delete(0, Integer.MAX_VALUE);
         resetInfo(info);
         int srcLength = src.length();
         if(srcLength == 0) {
            if(toASCII) {
               addError(info, IDNA.Error.EMPTY_LABEL);
            }

            return dest;
         } else {
            boolean disallowNonLDHDot = (this.options & 2) != 0;
            int labelStart = 0;
            int i = 0;

            while(true) {
               if(i == srcLength) {
                  if(toASCII) {
                     if(i - labelStart > 63) {
                        addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
                     }

                     if(!isLabel && i >= 254 && (i > 254 || labelStart < i)) {
                        addError(info, IDNA.Error.DOMAIN_NAME_TOO_LONG);
                     }
                  }

                  promoteAndResetLabelErrors(info);
                  return dest;
               }

               char c = src.charAt(i);
               if(c > 127) {
                  break;
               }

               int cData = asciiData[c];
               if(cData > 0) {
                  dest.append((char)(c + 32));
               } else {
                  if(cData < 0 && disallowNonLDHDot) {
                     break;
                  }

                  dest.append(c);
                  if(c == 45) {
                     if(i == labelStart + 3 && src.charAt(i - 1) == 45) {
                        ++i;
                        break;
                     }

                     if(i == labelStart) {
                        addLabelError(info, IDNA.Error.LEADING_HYPHEN);
                     }

                     if(i + 1 == srcLength || src.charAt(i + 1) == 46) {
                        addLabelError(info, IDNA.Error.TRAILING_HYPHEN);
                     }
                  } else if(c == 46) {
                     if(isLabel) {
                        ++i;
                        break;
                     }

                     if(toASCII) {
                        if(i == labelStart && i < srcLength - 1) {
                           addLabelError(info, IDNA.Error.EMPTY_LABEL);
                        } else if(i - labelStart > 63) {
                           addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
                        }
                     }

                     promoteAndResetLabelErrors(info);
                     labelStart = i + 1;
                  }
               }

               ++i;
            }

            promoteAndResetLabelErrors(info);
            this.processUnicode(src, labelStart, i, isLabel, toASCII, dest, info);
            if(isBiDi(info) && !hasCertainErrors(info, severeErrors) && (!isOkBiDi(info) || labelStart > 0 && !isASCIIOkBiDi(dest, labelStart))) {
               addError(info, IDNA.Error.BIDI);
            }

            return dest;
         }
      }
   }

   private StringBuilder processUnicode(CharSequence src, int labelStart, int mappingStart, boolean isLabel, boolean toASCII, StringBuilder dest, IDNA.Info info) {
      if(mappingStart == 0) {
         uts46Norm2.normalize(src, dest);
      } else {
         uts46Norm2.normalizeSecondAndAppend(dest, src.subSequence(mappingStart, src.length()));
      }

      boolean doMapDevChars = toASCII?(this.options & 16) == 0:(this.options & 32) == 0;
      int destLength = dest.length();
      int labelLimit = labelStart;

      while(labelLimit < destLength) {
         char c = dest.charAt(labelLimit);
         if(c == 46 && !isLabel) {
            int labelLength = labelLimit - labelStart;
            int newLength = this.processLabel(dest, labelStart, labelLength, toASCII, info);
            promoteAndResetLabelErrors(info);
            destLength += newLength - labelLength;
            labelLimit = labelStart += newLength + 1;
         } else if(223 <= c && c <= 8205 && (c == 223 || c == 962 || c >= 8204)) {
            setTransitionalDifferent(info);
            if(doMapDevChars) {
               destLength = this.mapDevChars(dest, labelStart, labelLimit);
               doMapDevChars = false;
            } else {
               ++labelLimit;
            }
         } else {
            ++labelLimit;
         }
      }

      if(0 == labelStart || labelStart < labelLimit) {
         this.processLabel(dest, labelStart, labelLimit - labelStart, toASCII, info);
         promoteAndResetLabelErrors(info);
      }

      return dest;
   }

   private int mapDevChars(StringBuilder dest, int labelStart, int mappingStart) {
      int length = dest.length();
      boolean didMapDevChars = false;
      int i = mappingStart;

      while(i < length) {
         char c = dest.charAt(i);
         switch(c) {
         case 'ß':
            didMapDevChars = true;
            dest.setCharAt(i++, 's');
            dest.insert(i++, 's');
            ++length;
            break;
         case 'ς':
            didMapDevChars = true;
            dest.setCharAt(i++, 'σ');
            break;
         case '\u200c':
         case '\u200d':
            didMapDevChars = true;
            dest.delete(i, i + 1);
            --length;
            break;
         default:
            ++i;
         }
      }

      if(didMapDevChars) {
         String normalized = uts46Norm2.normalize(dest.subSequence(labelStart, dest.length()));
         dest.replace(labelStart, Integer.MAX_VALUE, normalized);
         return dest.length();
      } else {
         return length;
      }
   }

   private static boolean isNonASCIIDisallowedSTD3Valid(int c) {
      return c == 8800 || c == 8814 || c == 8815;
   }

   private static int replaceLabel(StringBuilder dest, int destLabelStart, int destLabelLength, CharSequence label, int labelLength) {
      if(label != dest) {
         dest.delete(destLabelStart, destLabelStart + destLabelLength).insert(destLabelStart, label);
      }

      return labelLength;
   }

   private int processLabel(StringBuilder dest, int labelStart, int labelLength, boolean toASCII, IDNA.Info info) {
      int destLabelStart = labelStart;
      int destLabelLength = labelLength;
      StringBuilder labelString;
      boolean wasPunycode;
      if(labelLength >= 4 && dest.charAt(labelStart) == 120 && dest.charAt(labelStart + 1) == 110 && dest.charAt(labelStart + 2) == 45 && dest.charAt(labelStart + 3) == 45) {
         wasPunycode = true;

         StringBuilder fromPunycode;
         try {
            fromPunycode = Punycode.decode(dest.subSequence(labelStart + 4, labelStart + labelLength), (boolean[])null);
         } catch (StringPrepParseException var19) {
            addLabelError(info, IDNA.Error.PUNYCODE);
            return this.markBadACELabel(dest, labelStart, labelLength, toASCII, info);
         }

         boolean isValid = uts46Norm2.isNormalized(fromPunycode);
         if(!isValid) {
            addLabelError(info, IDNA.Error.INVALID_ACE_LABEL);
            return this.markBadACELabel(dest, labelStart, labelLength, toASCII, info);
         }

         labelString = fromPunycode;
         labelStart = 0;
         labelLength = fromPunycode.length();
      } else {
         wasPunycode = false;
         labelString = dest;
      }

      if(labelLength == 0) {
         if(toASCII) {
            addLabelError(info, IDNA.Error.EMPTY_LABEL);
         }

         return replaceLabel(dest, labelStart, destLabelLength, labelString, labelLength);
      } else {
         if(labelLength >= 4 && labelString.charAt(labelStart + 2) == 45 && labelString.charAt(labelStart + 3) == 45) {
            addLabelError(info, IDNA.Error.HYPHEN_3_4);
         }

         if(labelString.charAt(labelStart) == 45) {
            addLabelError(info, IDNA.Error.LEADING_HYPHEN);
         }

         if(labelString.charAt(labelStart + labelLength - 1) == 45) {
            addLabelError(info, IDNA.Error.TRAILING_HYPHEN);
         }

         int i = labelStart;
         int limit = labelStart + labelLength;
         char oredChars = 0;
         boolean disallowNonLDHDot = (this.options & 2) != 0;

         while(true) {
            char c = labelString.charAt(i);
            if(c <= 127) {
               if(c == 46) {
                  addLabelError(info, IDNA.Error.LABEL_HAS_DOT);
                  labelString.setCharAt(i, '�');
               } else if(disallowNonLDHDot && asciiData[c] < 0) {
                  addLabelError(info, IDNA.Error.DISALLOWED);
                  labelString.setCharAt(i, '�');
               }
            } else {
               oredChars |= c;
               if(disallowNonLDHDot && isNonASCIIDisallowedSTD3Valid(c)) {
                  addLabelError(info, IDNA.Error.DISALLOWED);
                  labelString.setCharAt(i, '�');
               } else if(c == '�') {
                  addLabelError(info, IDNA.Error.DISALLOWED);
               }
            }

            ++i;
            if(i >= limit) {
               break;
            }
         }

         int c = labelString.codePointAt(labelStart);
         if((U_GET_GC_MASK(c) & U_GC_M_MASK) != 0) {
            addLabelError(info, IDNA.Error.LEADING_COMBINING_MARK);
            labelString.setCharAt(labelStart, '�');
            if(c > '\uffff') {
               labelString.deleteCharAt(labelStart + 1);
               --labelLength;
               if(labelString == dest) {
                  --destLabelLength;
               }
            }
         }

         if(!hasCertainLabelErrors(info, severeErrors)) {
            if((this.options & 4) != 0 && (!isBiDi(info) || isOkBiDi(info))) {
               this.checkLabelBiDi(labelString, labelStart, labelLength, info);
            }

            if((this.options & 8) != 0 && (oredChars & 8204) == 8204 && !this.isLabelOkContextJ(labelString, labelStart, labelLength)) {
               addLabelError(info, IDNA.Error.CONTEXTJ);
            }

            if((this.options & 64) != 0 && oredChars >= 183) {
               this.checkLabelContextO(labelString, labelStart, labelLength, info);
            }

            if(toASCII) {
               if(wasPunycode) {
                  if(destLabelLength > 63) {
                     addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
                  }

                  return destLabelLength;
               }

               if(oredChars >= 128) {
                  StringBuilder punycode;
                  try {
                     punycode = Punycode.encode(labelString.subSequence(labelStart, labelStart + labelLength), (boolean[])null);
                  } catch (StringPrepParseException var18) {
                     throw new RuntimeException(var18);
                  }

                  punycode.insert(0, "xn--");
                  if(punycode.length() > 63) {
                     addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
                  }

                  return replaceLabel(dest, destLabelStart, destLabelLength, punycode, punycode.length());
               }

               if(labelLength > 63) {
                  addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
               }
            }
         } else if(wasPunycode) {
            addLabelError(info, IDNA.Error.INVALID_ACE_LABEL);
            return this.markBadACELabel(dest, destLabelStart, destLabelLength, toASCII, info);
         }

         return replaceLabel(dest, destLabelStart, destLabelLength, labelString, labelLength);
      }
   }

   private int markBadACELabel(StringBuilder dest, int labelStart, int labelLength, boolean toASCII, IDNA.Info info) {
      boolean disallowNonLDHDot = (this.options & 2) != 0;
      boolean isASCII = true;
      boolean onlyLDH = true;
      int i = labelStart + 4;
      int limit = labelStart + labelLength;

      while(true) {
         char c = dest.charAt(i);
         if(c <= 127) {
            if(c == 46) {
               addLabelError(info, IDNA.Error.LABEL_HAS_DOT);
               dest.setCharAt(i, '�');
               onlyLDH = false;
               isASCII = false;
            } else if(asciiData[c] < 0) {
               onlyLDH = false;
               if(disallowNonLDHDot) {
                  dest.setCharAt(i, '�');
                  isASCII = false;
               }
            }
         } else {
            onlyLDH = false;
            isASCII = false;
         }

         ++i;
         if(i >= limit) {
            break;
         }
      }

      if(onlyLDH) {
         dest.insert(labelStart + labelLength, '�');
         ++labelLength;
      } else if(toASCII && isASCII && labelLength > 63) {
         addLabelError(info, IDNA.Error.LABEL_TOO_LONG);
      }

      return labelLength;
   }

   private void checkLabelBiDi(CharSequence label, int labelStart, int labelLength, IDNA.Info info) {
      int c = Character.codePointAt(label, labelStart);
      int i = labelStart + Character.charCount(c);
      int firstMask = U_MASK(UBiDiProps.INSTANCE.getClass(c));
      if((firstMask & ~L_R_AL_MASK) != 0) {
         setNotOkBiDi(info);
      }

      int labelLimit = labelStart + labelLength;

      int lastMask;
      while(true) {
         if(i >= labelLimit) {
            lastMask = firstMask;
            break;
         }

         c = Character.codePointBefore(label, labelLimit);
         labelLimit -= Character.charCount(c);
         int dir = UBiDiProps.INSTANCE.getClass(c);
         if(dir != 17) {
            lastMask = U_MASK(dir);
            break;
         }
      }

      label117: {
         if((firstMask & L_MASK) != 0) {
            if((lastMask & ~L_EN_MASK) == 0) {
               break label117;
            }
         } else if((lastMask & ~R_AL_EN_AN_MASK) == 0) {
            break label117;
         }

         setNotOkBiDi(info);
      }

      int mask;
      for(mask = 0; i < labelLimit; mask |= U_MASK(UBiDiProps.INSTANCE.getClass(c))) {
         c = Character.codePointAt(label, i);
         i += Character.charCount(c);
      }

      if((firstMask & L_MASK) != 0) {
         if((mask & ~L_EN_ES_CS_ET_ON_BN_NSM_MASK) != 0) {
            setNotOkBiDi(info);
         }
      } else {
         if((mask & ~R_AL_AN_EN_ES_CS_ET_ON_BN_NSM_MASK) != 0) {
            setNotOkBiDi(info);
         }

         if((mask & EN_AN_MASK) == EN_AN_MASK) {
            setNotOkBiDi(info);
         }
      }

      if(((firstMask | mask | lastMask) & R_AL_AN_MASK) != 0) {
         setBiDi(info);
      }

   }

   private static boolean isASCIIOkBiDi(CharSequence s, int length) {
      int labelStart = 0;

      for(int i = 0; i < length; ++i) {
         char c = s.charAt(i);
         if(c != 46) {
            if(i == labelStart) {
               if(97 > c || c > 122) {
                  return false;
               }
            } else if(c <= 32 && (c >= 28 || 9 <= c && c <= 13)) {
               return false;
            }
         } else {
            if(i > labelStart) {
               c = s.charAt(i - 1);
               if((97 > c || c > 122) && (48 > c || c > 57)) {
                  return false;
               }
            }

            labelStart = i + 1;
         }
      }

      return true;
   }

   private boolean isLabelOkContextJ(CharSequence label, int labelStart, int labelLength) {
      int labelLimit = labelStart + labelLength;

      for(int i = labelStart; i < labelLimit; ++i) {
         if(label.charAt(i) != 8204) {
            if(label.charAt(i) == 8205) {
               if(i == labelStart) {
                  return false;
               }

               int c = Character.codePointBefore(label, i);
               if(uts46Norm2.getCombiningClass(c) != 9) {
                  return false;
               }
            }
         } else {
            if(i == labelStart) {
               return false;
            }

            int c = Character.codePointBefore(label, i);
            int j = i - Character.charCount(c);
            if(uts46Norm2.getCombiningClass(c) != 9) {
               while(true) {
                  int type = UBiDiProps.INSTANCE.getJoiningType(c);
                  if(type != 5) {
                     if(type != 3 && type != 2) {
                        return false;
                     }

                     j = i + 1;

                     while(true) {
                        if(j == labelLimit) {
                           return false;
                        }

                        c = Character.codePointAt(label, j);
                        j += Character.charCount(c);
                        type = UBiDiProps.INSTANCE.getJoiningType(c);
                        if(type != 5) {
                           break;
                        }
                     }

                     if(type != 4 && type != 2) {
                        return false;
                     }
                     break;
                  }

                  if(j == 0) {
                     return false;
                  }

                  c = Character.codePointBefore(label, j);
                  j -= Character.charCount(c);
               }
            }
         }
      }

      return true;
   }

   private void checkLabelContextO(CharSequence label, int labelStart, int labelLength, IDNA.Info info) {
      int labelEnd = labelStart + labelLength - 1;
      int arabicDigits = 0;

      for(int i = labelStart; i <= labelEnd; ++i) {
         int c = label.charAt(i);
         if(c >= 183) {
            if(c <= 1785) {
               if(c == 183) {
                  if(labelStart >= i || label.charAt(i - 1) != 108 || i >= labelEnd || label.charAt(i + 1) != 108) {
                     addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
                  }
               } else if(c == 885) {
                  if(i >= labelEnd || 14 != UScript.getScript(Character.codePointAt(label, i + 1))) {
                     addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
                  }
               } else if(c != 1523 && c != 1524) {
                  if(1632 <= c) {
                     if(c <= 1641) {
                        if(arabicDigits > 0) {
                           addLabelError(info, IDNA.Error.CONTEXTO_DIGITS);
                        }

                        arabicDigits = -1;
                     } else if(1776 <= c) {
                        if(arabicDigits < 0) {
                           addLabelError(info, IDNA.Error.CONTEXTO_DIGITS);
                        }

                        arabicDigits = 1;
                     }
                  }
               } else if(labelStart >= i || 19 != UScript.getScript(Character.codePointBefore(label, i))) {
                  addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
               }
            } else if(c == 12539) {
               int j = labelStart;

               while(true) {
                  if(j > labelEnd) {
                     addLabelError(info, IDNA.Error.CONTEXTO_PUNCTUATION);
                     break;
                  }

                  c = Character.codePointAt(label, j);
                  int script = UScript.getScript(c);
                  if(script == 20 || script == 22 || script == 17) {
                     break;
                  }

                  j += Character.charCount(c);
               }
            }
         }
      }

   }

   private static int U_MASK(int x) {
      return 1 << x;
   }

   private static int U_GET_GC_MASK(int c) {
      return 1 << UCharacter.getType(c);
   }
}
