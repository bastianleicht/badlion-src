package com.ibm.icu.impl;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import java.util.ArrayList;

public class UnicodeSetStringSpan {
   public static final int FWD = 32;
   public static final int BACK = 16;
   public static final int UTF16 = 8;
   public static final int CONTAINED = 2;
   public static final int NOT_CONTAINED = 1;
   public static final int ALL = 63;
   public static final int FWD_UTF16_CONTAINED = 42;
   public static final int FWD_UTF16_NOT_CONTAINED = 41;
   public static final int BACK_UTF16_CONTAINED = 26;
   public static final int BACK_UTF16_NOT_CONTAINED = 25;
   static final short ALL_CP_CONTAINED = 255;
   static final short LONG_SPAN = 254;
   private UnicodeSet spanSet;
   private UnicodeSet spanNotSet;
   private ArrayList strings;
   private short[] spanLengths;
   private int maxLength16;
   private boolean all;
   private UnicodeSetStringSpan.OffsetList offsets;

   public UnicodeSetStringSpan(UnicodeSet set, ArrayList setStrings, int which) {
      this.spanSet = new UnicodeSet(0, 1114111);
      this.strings = setStrings;
      this.all = which == 63;
      this.spanSet.retainAll(set);
      if(0 != (which & 1)) {
         this.spanNotSet = this.spanSet;
      }

      this.offsets = new UnicodeSetStringSpan.OffsetList();
      int stringsLength = this.strings.size();
      boolean someRelevant = false;

      for(int i = 0; i < stringsLength; ++i) {
         String string = (String)this.strings.get(i);
         int length16 = string.length();
         int spanLength = this.spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
         if(spanLength < length16) {
            someRelevant = true;
         }

         if(0 != (which & 8) && length16 > this.maxLength16) {
            this.maxLength16 = length16;
         }
      }

      if(!someRelevant) {
         this.maxLength16 = 0;
      } else {
         if(this.all) {
            this.spanSet.freeze();
         }

         int allocSize;
         if(this.all) {
            allocSize = stringsLength * 2;
         } else {
            allocSize = stringsLength;
         }

         this.spanLengths = new short[allocSize];
         int spanBackLengthsOffset;
         if(this.all) {
            spanBackLengthsOffset = stringsLength;
         } else {
            spanBackLengthsOffset = 0;
         }

         for(int var13 = 0; var13 < stringsLength; ++var13) {
            String string = (String)this.strings.get(var13);
            int length16 = string.length();
            int spanLength = this.spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
            if(spanLength < length16) {
               if(0 != (which & 8)) {
                  if(0 != (which & 2)) {
                     if(0 != (which & 32)) {
                        this.spanLengths[var13] = makeSpanLengthByte(spanLength);
                     }

                     if(0 != (which & 16)) {
                        spanLength = length16 - this.spanSet.spanBack(string, length16, UnicodeSet.SpanCondition.CONTAINED);
                        this.spanLengths[spanBackLengthsOffset + var13] = makeSpanLengthByte(spanLength);
                     }
                  } else {
                     this.spanLengths[var13] = this.spanLengths[spanBackLengthsOffset + var13] = 0;
                  }
               }

               if(0 != (which & 1)) {
                  if(0 != (which & 32)) {
                     int c = string.codePointAt(0);
                     this.addToSpanNotSet(c);
                  }

                  if(0 != (which & 16)) {
                     int c = string.codePointBefore(length16);
                     this.addToSpanNotSet(c);
                  }
               }
            } else if(this.all) {
               this.spanLengths[var13] = this.spanLengths[spanBackLengthsOffset + var13] = 255;
            } else {
               this.spanLengths[var13] = 255;
            }
         }

         if(this.all) {
            this.spanNotSet.freeze();
         }

      }
   }

   public UnicodeSetStringSpan(UnicodeSetStringSpan otherStringSpan, ArrayList newParentSetStrings) {
      this.spanSet = otherStringSpan.spanSet;
      this.strings = newParentSetStrings;
      this.maxLength16 = otherStringSpan.maxLength16;
      this.all = true;
      if(otherStringSpan.spanNotSet == otherStringSpan.spanSet) {
         this.spanNotSet = this.spanSet;
      } else {
         this.spanNotSet = (UnicodeSet)otherStringSpan.spanNotSet.clone();
      }

      this.offsets = new UnicodeSetStringSpan.OffsetList();
      this.spanLengths = (short[])otherStringSpan.spanLengths.clone();
   }

   public boolean needsStringSpanUTF16() {
      return this.maxLength16 != 0;
   }

   public boolean contains(int c) {
      return this.spanSet.contains(c);
   }

   private void addToSpanNotSet(int c) {
      if(this.spanNotSet == null || this.spanNotSet == this.spanSet) {
         if(this.spanSet.contains(c)) {
            return;
         }

         this.spanNotSet = this.spanSet.cloneAsThawed();
      }

      this.spanNotSet.add(c);
   }

   public synchronized int span(CharSequence s, int start, int length, UnicodeSet.SpanCondition spanCondition) {
      if(spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
         return this.spanNot(s, start, length);
      } else {
         int spanLength = this.spanSet.span(s.subSequence(start, start + length), UnicodeSet.SpanCondition.CONTAINED);
         if(spanLength == length) {
            return length;
         } else {
            int initSize = 0;
            if(spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
               initSize = this.maxLength16;
            }

            this.offsets.setMaxLength(initSize);
            int pos = start + spanLength;
            int rest = length - spanLength;
            int stringsLength = this.strings.size();

            while(true) {
               if(spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
                  for(int i = 0; i < stringsLength; ++i) {
                     int overlap = this.spanLengths[i];
                     if(overlap != 255) {
                        String string = (String)this.strings.get(i);
                        int length16 = string.length();
                        if(overlap >= 254) {
                           overlap = string.offsetByCodePoints(length16, -1);
                        }

                        if(overlap > spanLength) {
                           overlap = spanLength;
                        }

                        for(int inc = length16 - overlap; inc <= rest; ++inc) {
                           if(!this.offsets.containsOffset(inc) && matches16CPB(s, pos - overlap, length, string, length16)) {
                              if(inc == rest) {
                                 return length;
                              }

                              this.offsets.addOffset(inc);
                           }

                           if(overlap == 0) {
                              break;
                           }

                           --overlap;
                        }
                     }
                  }
               } else {
                  int maxInc = 0;
                  int maxOverlap = 0;

                  for(int i = 0; i < stringsLength; ++i) {
                     int overlap = this.spanLengths[i];
                     String string = (String)this.strings.get(i);
                     int length16 = string.length();
                     if(overlap >= 254) {
                        overlap = length16;
                     }

                     if(overlap > spanLength) {
                        overlap = spanLength;
                     }

                     for(int inc = length16 - overlap; inc <= rest && overlap >= maxOverlap; ++inc) {
                        if((overlap > maxOverlap || inc > maxInc) && matches16CPB(s, pos - overlap, length, string, length16)) {
                           maxInc = inc;
                           maxOverlap = overlap;
                           break;
                        }

                        --overlap;
                     }
                  }

                  if(maxInc != 0 || maxOverlap != 0) {
                     pos += maxInc;
                     rest -= maxInc;
                     if(rest == 0) {
                        return length;
                     }

                     spanLength = 0;
                     continue;
                  }
               }

               if(spanLength == 0 && pos != 0) {
                  if(this.offsets.isEmpty()) {
                     spanLength = this.spanSet.span(s.subSequence(pos, pos + rest), UnicodeSet.SpanCondition.CONTAINED);
                     if(spanLength == rest || spanLength == 0) {
                        return pos + spanLength - start;
                     }

                     pos += spanLength;
                     rest -= spanLength;
                     continue;
                  }

                  spanLength = spanOne(this.spanSet, s, pos, rest);
                  if(spanLength > 0) {
                     if(spanLength == rest) {
                        return length;
                     }

                     pos += spanLength;
                     rest -= spanLength;
                     this.offsets.shift(spanLength);
                     spanLength = 0;
                     continue;
                  }
               } else if(this.offsets.isEmpty()) {
                  return pos - start;
               }

               int minOffset = this.offsets.popMinimum();
               pos += minOffset;
               rest -= minOffset;
               spanLength = 0;
            }
         }
      }
   }

   public synchronized int spanBack(CharSequence s, int length, UnicodeSet.SpanCondition spanCondition) {
      if(spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
         return this.spanNotBack(s, length);
      } else {
         int pos = this.spanSet.spanBack(s, length, UnicodeSet.SpanCondition.CONTAINED);
         if(pos == 0) {
            return 0;
         } else {
            int spanLength = length - pos;
            int initSize = 0;
            if(spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
               initSize = this.maxLength16;
            }

            this.offsets.setMaxLength(initSize);
            int stringsLength = this.strings.size();
            int spanBackLengthsOffset = 0;
            if(this.all) {
               spanBackLengthsOffset = stringsLength;
            }

            while(true) {
               if(spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
                  for(int i = 0; i < stringsLength; ++i) {
                     int overlap = this.spanLengths[spanBackLengthsOffset + i];
                     if(overlap != 255) {
                        String string = (String)this.strings.get(i);
                        int length16 = string.length();
                        if(overlap >= 254) {
                           int len1 = 0;
                           len1 = string.offsetByCodePoints(0, 1);
                           overlap = length16 - len1;
                        }

                        if(overlap > spanLength) {
                           overlap = spanLength;
                        }

                        for(int dec = length16 - overlap; dec <= pos; ++dec) {
                           if(!this.offsets.containsOffset(dec) && matches16CPB(s, pos - dec, length, string, length16)) {
                              if(dec == pos) {
                                 return 0;
                              }

                              this.offsets.addOffset(dec);
                           }

                           if(overlap == 0) {
                              break;
                           }

                           --overlap;
                        }
                     }
                  }
               } else {
                  int maxDec = 0;
                  int maxOverlap = 0;

                  for(int i = 0; i < stringsLength; ++i) {
                     int overlap = this.spanLengths[spanBackLengthsOffset + i];
                     String string = (String)this.strings.get(i);
                     int length16 = string.length();
                     if(overlap >= 254) {
                        overlap = length16;
                     }

                     if(overlap > spanLength) {
                        overlap = spanLength;
                     }

                     for(int dec = length16 - overlap; dec <= pos && overlap >= maxOverlap; ++dec) {
                        if((overlap > maxOverlap || dec > maxDec) && matches16CPB(s, pos - dec, length, string, length16)) {
                           maxDec = dec;
                           maxOverlap = overlap;
                           break;
                        }

                        --overlap;
                     }
                  }

                  if(maxDec != 0 || maxOverlap != 0) {
                     pos -= maxDec;
                     if(pos == 0) {
                        return 0;
                     }

                     spanLength = 0;
                     continue;
                  }
               }

               if(spanLength == 0 && pos != length) {
                  if(this.offsets.isEmpty()) {
                     int oldPos = pos;
                     pos = this.spanSet.spanBack(s, pos, UnicodeSet.SpanCondition.CONTAINED);
                     spanLength = oldPos - pos;
                     if(pos == 0 || spanLength == 0) {
                        return pos;
                     }
                     continue;
                  }

                  spanLength = spanOneBack(this.spanSet, s, pos);
                  if(spanLength > 0) {
                     if(spanLength == pos) {
                        return 0;
                     }

                     pos -= spanLength;
                     this.offsets.shift(spanLength);
                     spanLength = 0;
                     continue;
                  }
               } else if(this.offsets.isEmpty()) {
                  return pos;
               }

               pos -= this.offsets.popMinimum();
               spanLength = 0;
            }
         }
      }
   }

   private int spanNot(CharSequence s, int start, int length) {
      int pos = start;
      int rest = length;
      int stringsLength = this.strings.size();

      while(true) {
         int i = this.spanNotSet.span(s.subSequence(pos, pos + rest), UnicodeSet.SpanCondition.NOT_CONTAINED);
         if(i == rest) {
            return length;
         }

         pos = pos + i;
         rest = rest - i;
         int cpLength = spanOne(this.spanSet, s, pos, rest);
         if(cpLength > 0) {
            return pos - start;
         }

         for(i = 0; i < stringsLength; ++i) {
            if(this.spanLengths[i] != 255) {
               String string = (String)this.strings.get(i);
               int length16 = string.length();
               if(length16 <= rest && matches16CPB(s, pos, length, string, length16)) {
                  return pos - start;
               }
            }
         }

         pos = pos - cpLength;
         rest = rest + cpLength;
         if(rest == 0) {
            break;
         }
      }

      return length;
   }

   private int spanNotBack(CharSequence s, int length) {
      int pos = length;
      int stringsLength = this.strings.size();

      while(true) {
         pos = this.spanNotSet.spanBack(s, pos, UnicodeSet.SpanCondition.NOT_CONTAINED);
         if(pos == 0) {
            return 0;
         }

         int cpLength = spanOneBack(this.spanSet, s, pos);
         if(cpLength > 0) {
            return pos;
         }

         for(int i = 0; i < stringsLength; ++i) {
            if(this.spanLengths[i] != 255) {
               String string = (String)this.strings.get(i);
               int length16 = string.length();
               if(length16 <= pos && matches16CPB(s, pos - length16, length, string, length16)) {
                  return pos;
               }
            }
         }

         pos = pos + cpLength;
         if(pos == 0) {
            break;
         }
      }

      return 0;
   }

   static short makeSpanLengthByte(int spanLength) {
      return spanLength < 254?(short)spanLength:254;
   }

   private static boolean matches16(CharSequence s, int start, String t, int length) {
      int end = start + length;

      while(length-- > 0) {
         --end;
         if(s.charAt(end) != t.charAt(length)) {
            return false;
         }
      }

      return true;
   }

   static boolean matches16CPB(CharSequence s, int start, int slength, String t, int tlength) {
      return (0 >= start || !UTF16.isLeadSurrogate(s.charAt(start - 1)) || !UTF16.isTrailSurrogate(s.charAt(start + 0))) && (tlength >= slength || !UTF16.isLeadSurrogate(s.charAt(start + tlength - 1)) || !UTF16.isTrailSurrogate(s.charAt(start + tlength))) && matches16(s, start, t, tlength);
   }

   static int spanOne(UnicodeSet set, CharSequence s, int start, int length) {
      char c = s.charAt(start);
      if(c >= '\ud800' && c <= '\udbff' && length >= 2) {
         char c2 = s.charAt(start + 1);
         if(UTF16.isTrailSurrogate(c2)) {
            int supplementary = UCharacterProperty.getRawSupplementary(c, c2);
            return set.contains(supplementary)?2:-2;
         }
      }

      return set.contains(c)?1:-1;
   }

   static int spanOneBack(UnicodeSet set, CharSequence s, int length) {
      char c = s.charAt(length - 1);
      if(c >= '\udc00' && c <= '\udfff' && length >= 2) {
         char c2 = s.charAt(length - 2);
         if(UTF16.isLeadSurrogate(c2)) {
            int supplementary = UCharacterProperty.getRawSupplementary(c2, c);
            return set.contains(supplementary)?2:-2;
         }
      }

      return set.contains(c)?1:-1;
   }

   static class OffsetList {
      private boolean[] list = new boolean[16];
      private int length;
      private int start;

      public void setMaxLength(int maxLength) {
         if(maxLength > this.list.length) {
            this.list = new boolean[maxLength];
         }

         this.clear();
      }

      public void clear() {
         for(int i = this.list.length; i-- > 0; this.list[i] = false) {
            ;
         }

         this.start = this.length = 0;
      }

      public boolean isEmpty() {
         return this.length == 0;
      }

      public void shift(int delta) {
         int i = this.start + delta;
         if(i >= this.list.length) {
            i -= this.list.length;
         }

         if(this.list[i]) {
            this.list[i] = false;
            --this.length;
         }

         this.start = i;
      }

      public void addOffset(int offset) {
         int i = this.start + offset;
         if(i >= this.list.length) {
            i -= this.list.length;
         }

         this.list[i] = true;
         ++this.length;
      }

      public boolean containsOffset(int offset) {
         int i = this.start + offset;
         if(i >= this.list.length) {
            i -= this.list.length;
         }

         return this.list[i];
      }

      public int popMinimum() {
         int i = this.start;

         while(true) {
            ++i;
            if(i >= this.list.length) {
               int result = this.list.length - this.start;

               for(i = 0; !this.list[i]; ++i) {
                  ;
               }

               this.list[i] = false;
               --this.length;
               this.start = i;
               return result + i;
            }

            if(this.list[i]) {
               break;
            }
         }

         this.list[i] = false;
         --this.length;
         int result = i - this.start;
         this.start = i;
         return result;
      }
   }
}
