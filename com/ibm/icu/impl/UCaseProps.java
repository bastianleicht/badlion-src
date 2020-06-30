package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.ULocale;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public final class UCaseProps {
   private static final byte[] flagsOffset = new byte[]{(byte)0, (byte)1, (byte)1, (byte)2, (byte)1, (byte)2, (byte)2, (byte)3, (byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4, (byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7, (byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7, (byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7, (byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6, (byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7, (byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7, (byte)5, (byte)6, (byte)6, (byte)7, (byte)6, (byte)7, (byte)7, (byte)8};
   public static final int MAX_STRING_LENGTH = 31;
   private static final int LOC_UNKNOWN = 0;
   private static final int LOC_ROOT = 1;
   private static final int LOC_TURKISH = 2;
   private static final int LOC_LITHUANIAN = 3;
   private static final String iDot = "i̇";
   private static final String jDot = "j̇";
   private static final String iOgonekDot = "į̇";
   private static final String iDotGrave = "i̇̀";
   private static final String iDotAcute = "i̇́";
   private static final String iDotTilde = "i̇̃";
   private static final int FOLD_CASE_OPTIONS_MASK = 255;
   private static final int[] rootLocCache = new int[]{1};
   public static final StringBuilder dummyStringBuilder = new StringBuilder();
   private int[] indexes;
   private char[] exceptions;
   private char[] unfold;
   private Trie2_16 trie;
   private static final String DATA_NAME = "ucase";
   private static final String DATA_TYPE = "icu";
   private static final String DATA_FILE_NAME = "ucase.icu";
   private static final byte[] FMT = new byte[]{(byte)99, (byte)65, (byte)83, (byte)69};
   private static final int IX_TRIE_SIZE = 2;
   private static final int IX_EXC_LENGTH = 3;
   private static final int IX_UNFOLD_LENGTH = 4;
   private static final int IX_TOP = 16;
   public static final int TYPE_MASK = 3;
   public static final int NONE = 0;
   public static final int LOWER = 1;
   public static final int UPPER = 2;
   public static final int TITLE = 3;
   private static final int SENSITIVE = 8;
   private static final int EXCEPTION = 16;
   private static final int DOT_MASK = 96;
   private static final int SOFT_DOTTED = 32;
   private static final int ABOVE = 64;
   private static final int OTHER_ACCENT = 96;
   private static final int DELTA_SHIFT = 7;
   private static final int EXC_SHIFT = 5;
   private static final int EXC_LOWER = 0;
   private static final int EXC_FOLD = 1;
   private static final int EXC_UPPER = 2;
   private static final int EXC_TITLE = 3;
   private static final int EXC_CLOSURE = 6;
   private static final int EXC_FULL_MAPPINGS = 7;
   private static final int EXC_DOUBLE_SLOTS = 256;
   private static final int EXC_DOT_SHIFT = 7;
   private static final int EXC_CONDITIONAL_SPECIAL = 16384;
   private static final int EXC_CONDITIONAL_FOLD = 32768;
   private static final int FULL_LOWER = 15;
   private static final int CLOSURE_MAX_LENGTH = 15;
   private static final int UNFOLD_ROWS = 0;
   private static final int UNFOLD_ROW_WIDTH = 1;
   private static final int UNFOLD_STRING_WIDTH = 2;
   public static final UCaseProps INSTANCE;

   private UCaseProps() throws IOException {
      InputStream is = ICUData.getRequiredStream("data/icudt51b/ucase.icu");
      BufferedInputStream b = new BufferedInputStream(is, 4096);
      this.readData(b);
      b.close();
      is.close();
   }

   private final void readData(InputStream is) throws IOException {
      DataInputStream inputStream = new DataInputStream(is);
      ICUBinary.readHeader(inputStream, FMT, new UCaseProps.IsAcceptable());
      int count = inputStream.readInt();
      if(count < 16) {
         throw new IOException("indexes[0] too small in ucase.icu");
      } else {
         this.indexes = new int[count];
         this.indexes[0] = count;

         for(int i = 1; i < count; ++i) {
            this.indexes[i] = inputStream.readInt();
         }

         this.trie = Trie2_16.createFromSerialized(inputStream);
         int expectedTrieLength = this.indexes[2];
         int trieLength = this.trie.getSerializedLength();
         if(trieLength > expectedTrieLength) {
            throw new IOException("ucase.icu: not enough bytes for the trie");
         } else {
            inputStream.skipBytes(expectedTrieLength - trieLength);
            count = this.indexes[3];
            if(count > 0) {
               this.exceptions = new char[count];

               for(int var7 = 0; var7 < count; ++var7) {
                  this.exceptions[var7] = inputStream.readChar();
               }
            }

            count = this.indexes[4];
            if(count > 0) {
               this.unfold = new char[count];

               for(int var8 = 0; var8 < count; ++var8) {
                  this.unfold[var8] = inputStream.readChar();
               }
            }

         }
      }
   }

   public final void addPropertyStarts(UnicodeSet set) {
      Iterator<Trie2.Range> trieIterator = this.trie.iterator();

      Trie2.Range range;
      while(trieIterator.hasNext() && !(range = (Trie2.Range)trieIterator.next()).leadSurrogate) {
         set.add(range.startCodePoint);
      }

   }

   private static final int getExceptionsOffset(int props) {
      return props >> 5;
   }

   private static final boolean propsHasException(int props) {
      return (props & 16) != 0;
   }

   private static final boolean hasSlot(int flags, int index) {
      return (flags & 1 << index) != 0;
   }

   private static final byte slotOffset(int flags, int index) {
      return flagsOffset[flags & (1 << index) - 1];
   }

   private final long getSlotValueAndOffset(int excWord, int index, int excOffset) {
      long value;
      if((excWord & 256) == 0) {
         excOffset = excOffset + slotOffset(excWord, index);
         value = (long)this.exceptions[excOffset];
      } else {
         excOffset = excOffset + 2 * slotOffset(excWord, index);
         value = (long)this.exceptions[excOffset++];
         value = value << 16 | (long)this.exceptions[excOffset];
      }

      return value | (long)excOffset << 32;
   }

   private final int getSlotValue(int excWord, int index, int excOffset) {
      int value;
      if((excWord & 256) == 0) {
         excOffset = excOffset + slotOffset(excWord, index);
         value = this.exceptions[excOffset];
      } else {
         excOffset = excOffset + 2 * slotOffset(excWord, index);
         value = this.exceptions[excOffset++];
         value = value << 16 | this.exceptions[excOffset];
      }

      return value;
   }

   public final int tolower(int c) {
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) >= 2) {
            c += getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         if(hasSlot(excWord, 0)) {
            c = this.getSlotValue(excWord, 0, excOffset);
         }
      }

      return c;
   }

   public final int toupper(int c) {
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) == 1) {
            c += getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         if(hasSlot(excWord, 2)) {
            c = this.getSlotValue(excWord, 2, excOffset);
         }
      }

      return c;
   }

   public final int totitle(int c) {
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) == 1) {
            c += getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         int index;
         if(hasSlot(excWord, 3)) {
            index = 3;
         } else {
            if(!hasSlot(excWord, 2)) {
               return c;
            }

            index = 2;
         }

         c = this.getSlotValue(excWord, index, excOffset);
      }

      return c;
   }

   public final void addCaseClosure(int c, UnicodeSet set) {
      switch(c) {
      case 73:
         set.add(105);
         return;
      case 105:
         set.add(73);
         return;
      case 304:
         set.add((CharSequence)"i̇");
         return;
      case 305:
         return;
      default:
         int props = this.trie.get(c);
         if(!propsHasException(props)) {
            if(getTypeFromProps(props) != 0) {
               int delta = getDelta(props);
               if(delta != 0) {
                  set.add(c + delta);
               }
            }
         } else {
            int excOffset = getExceptionsOffset(props);
            int excWord = this.exceptions[excOffset++];
            int excOffset0 = excOffset;

            for(int index = 0; index <= 3; ++index) {
               if(hasSlot(excWord, index)) {
                  c = this.getSlotValue(excWord, index, excOffset0);
                  set.add(c);
               }
            }

            int closureOffset;
            int closureLength;
            if(hasSlot(excWord, 6)) {
               long value = this.getSlotValueAndOffset(excWord, 6, excOffset0);
               closureLength = (int)value & 15;
               closureOffset = (int)(value >> 32) + 1;
            } else {
               closureLength = 0;
               closureOffset = 0;
            }

            if(hasSlot(excWord, 7)) {
               long value = this.getSlotValueAndOffset(excWord, 7, excOffset0);
               int fullLength = (int)value;
               excOffset = (int)(value >> 32) + 1;
               fullLength = fullLength & '\uffff';
               excOffset = excOffset + (fullLength & 15);
               fullLength = fullLength >> 4;
               int length = fullLength & 15;
               if(length != 0) {
                  set.add((CharSequence)(new String(this.exceptions, excOffset, length)));
                  excOffset += length;
               }

               fullLength = fullLength >> 4;
               excOffset = excOffset + (fullLength & 15);
               fullLength = fullLength >> 4;
               excOffset = excOffset + fullLength;
               closureOffset = excOffset;
            }

            for(int var22 = 0; var22 < closureLength; var22 += UTF16.getCharCount(c)) {
               c = UTF16.charAt(this.exceptions, closureOffset, this.exceptions.length, var22);
               set.add(c);
            }
         }

      }
   }

   private final int strcmpMax(String s, int unfoldOffset, int max) {
      int length = s.length();
      max = max - length;
      int i1 = 0;

      while(true) {
         int c1 = s.charAt(i1++);
         int c2 = this.unfold[unfoldOffset++];
         if(c2 == 0) {
            return 1;
         }

         c1 = c1 - c2;
         if(c1 != 0) {
            return c1;
         }

         --length;
         if(length <= 0) {
            break;
         }
      }

      if(max != 0 && this.unfold[unfoldOffset] != 0) {
         return -max;
      } else {
         return 0;
      }
   }

   public final boolean addStringCaseClosure(String s, UnicodeSet set) {
      if(this.unfold != null && s != null) {
         int length = s.length();
         if(length <= 1) {
            return false;
         } else {
            int unfoldRows = this.unfold[0];
            int unfoldRowWidth = this.unfold[1];
            int unfoldStringWidth = this.unfold[2];
            if(length > unfoldStringWidth) {
               return false;
            } else {
               int start = 0;
               int limit = unfoldRows;

               while(start < limit) {
                  int i = (start + limit) / 2;
                  int unfoldOffset = (i + 1) * unfoldRowWidth;
                  int result = this.strcmpMax(s, unfoldOffset, unfoldStringWidth);
                  if(result == 0) {
                     int c;
                     for(i = unfoldStringWidth; i < unfoldRowWidth && this.unfold[unfoldOffset + i] != 0; i += UTF16.getCharCount(c)) {
                        c = UTF16.charAt(this.unfold, unfoldOffset, this.unfold.length, i);
                        set.add(c);
                        this.addCaseClosure(c, set);
                     }

                     return true;
                  }

                  if(result < 0) {
                     limit = i;
                  } else {
                     start = i + 1;
                  }
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   public final int getType(int c) {
      return getTypeFromProps(this.trie.get(c));
   }

   public final int getTypeOrIgnorable(int c) {
      return getTypeAndIgnorableFromProps(this.trie.get(c));
   }

   public final int getDotType(int c) {
      int props = this.trie.get(c);
      return !propsHasException(props)?props & 96:this.exceptions[getExceptionsOffset(props)] >> 7 & 96;
   }

   public final boolean isSoftDotted(int c) {
      return this.getDotType(c) == 32;
   }

   public final boolean isCaseSensitive(int c) {
      return (this.trie.get(c) & 8) != 0;
   }

   private static final int getCaseLocale(ULocale locale, int[] locCache) {
      int result;
      if(locCache != null && (result = locCache[0]) != 0) {
         return result;
      } else {
         result = 1;
         String language = locale.getLanguage();
         if(!language.equals("tr") && !language.equals("tur") && !language.equals("az") && !language.equals("aze")) {
            if(language.equals("lt") || language.equals("lit")) {
               result = 3;
            }
         } else {
            result = 2;
         }

         if(locCache != null) {
            locCache[0] = result;
         }

         return result;
      }
   }

   private final boolean isFollowedByCasedLetter(UCaseProps.ContextIterator iter, int dir) {
      if(iter == null) {
         return false;
      } else {
         iter.reset(dir);

         int c;
         while((c = iter.next()) >= 0) {
            int type = this.getTypeOrIgnorable(c);
            if((type & 4) == 0) {
               if(type != 0) {
                  return true;
               }

               return false;
            }
         }

         return false;
      }
   }

   private final boolean isPrecededBySoftDotted(UCaseProps.ContextIterator iter) {
      if(iter == null) {
         return false;
      } else {
         iter.reset(-1);

         int c;
         while((c = iter.next()) >= 0) {
            int dotType = this.getDotType(c);
            if(dotType == 32) {
               return true;
            }

            if(dotType != 96) {
               return false;
            }
         }

         return false;
      }
   }

   private final boolean isPrecededBy_I(UCaseProps.ContextIterator iter) {
      if(iter == null) {
         return false;
      } else {
         iter.reset(-1);

         int c;
         while((c = iter.next()) >= 0) {
            if(c == 73) {
               return true;
            }

            int dotType = this.getDotType(c);
            if(dotType != 96) {
               return false;
            }
         }

         return false;
      }
   }

   private final boolean isFollowedByMoreAbove(UCaseProps.ContextIterator iter) {
      if(iter == null) {
         return false;
      } else {
         iter.reset(1);

         int c;
         while((c = iter.next()) >= 0) {
            int dotType = this.getDotType(c);
            if(dotType == 64) {
               return true;
            }

            if(dotType != 96) {
               return false;
            }
         }

         return false;
      }
   }

   private final boolean isFollowedByDotAbove(UCaseProps.ContextIterator iter) {
      if(iter == null) {
         return false;
      } else {
         iter.reset(1);

         int c;
         while((c = iter.next()) >= 0) {
            if(c == 775) {
               return true;
            }

            int dotType = this.getDotType(c);
            if(dotType != 96) {
               return false;
            }
         }

         return false;
      }
   }

   public final int toFullLower(int c, UCaseProps.ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache) {
      int result = c;
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) >= 2) {
            result = c + getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         if((excWord & 16384) != 0) {
            int loc = getCaseLocale(locale, locCache);
            if(loc == 3 && ((c == 73 || c == 74 || c == 302) && this.isFollowedByMoreAbove(iter) || c == 204 || c == 205 || c == 296)) {
               switch(c) {
               case 73:
                  out.append("i̇");
                  return 2;
               case 74:
                  out.append("j̇");
                  return 2;
               case 204:
                  out.append("i̇̀");
                  return 3;
               case 205:
                  out.append("i̇́");
                  return 3;
               case 296:
                  out.append("i̇̃");
                  return 3;
               case 302:
                  out.append("į̇");
                  return 2;
               default:
                  return 0;
               }
            }

            if(loc == 2 && c == 304) {
               return 105;
            }

            if(loc == 2 && c == 775 && this.isPrecededBy_I(iter)) {
               return 0;
            }

            if(loc == 2 && c == 73 && !this.isFollowedByDotAbove(iter)) {
               return 305;
            }

            if(c == 304) {
               out.append("i̇");
               return 2;
            }

            if(c == 931 && !this.isFollowedByCasedLetter(iter, 1) && this.isFollowedByCasedLetter(iter, -1)) {
               return 962;
            }
         } else if(hasSlot(excWord, 7)) {
            long value = this.getSlotValueAndOffset(excWord, 7, excOffset);
            int full = (int)value & 15;
            if(full != 0) {
               excOffset = (int)(value >> 32) + 1;
               out.append(this.exceptions, excOffset, full);
               return full;
            }
         }

         if(hasSlot(excWord, 0)) {
            result = this.getSlotValue(excWord, 0, excOffset);
         }
      }

      return result == c?~result:result;
   }

   private final int toUpperOrTitle(int c, UCaseProps.ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache, boolean upperNotTitle) {
      int result = c;
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) == 1) {
            result = c + getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         if((excWord & 16384) != 0) {
            int loc = getCaseLocale(locale, locCache);
            if(loc == 2 && c == 105) {
               return 304;
            }

            if(loc == 3 && c == 775 && this.isPrecededBySoftDotted(iter)) {
               return 0;
            }
         } else if(hasSlot(excWord, 7)) {
            long value = this.getSlotValueAndOffset(excWord, 7, excOffset);
            int full = (int)value & '\uffff';
            excOffset = (int)(value >> 32) + 1;
            excOffset = excOffset + (full & 15);
            full = full >> 4;
            excOffset = excOffset + (full & 15);
            full = full >> 4;
            if(upperNotTitle) {
               full = full & 15;
            } else {
               excOffset += full & 15;
               full = full >> 4 & 15;
            }

            if(full != 0) {
               out.append(this.exceptions, excOffset, full);
               return full;
            }
         }

         int index;
         if(!upperNotTitle && hasSlot(excWord, 3)) {
            index = 3;
         } else {
            if(!hasSlot(excWord, 2)) {
               return ~c;
            }

            index = 2;
         }

         result = this.getSlotValue(excWord, index, excOffset);
      }

      return result == c?~result:result;
   }

   public final int toFullUpper(int c, UCaseProps.ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache) {
      return this.toUpperOrTitle(c, iter, out, locale, locCache, true);
   }

   public final int toFullTitle(int c, UCaseProps.ContextIterator iter, StringBuilder out, ULocale locale, int[] locCache) {
      return this.toUpperOrTitle(c, iter, out, locale, locCache, false);
   }

   public final int fold(int c, int options) {
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) >= 2) {
            c += getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         if((excWord & '耀') != 0) {
            if((options & 255) == 0) {
               if(c == 73) {
                  return 105;
               }

               if(c == 304) {
                  return c;
               }
            } else {
               if(c == 73) {
                  return 305;
               }

               if(c == 304) {
                  return 105;
               }
            }
         }

         int index;
         if(hasSlot(excWord, 1)) {
            index = 1;
         } else {
            if(!hasSlot(excWord, 0)) {
               return c;
            }

            index = 0;
         }

         c = this.getSlotValue(excWord, index, excOffset);
      }

      return c;
   }

   public final int toFullFolding(int c, StringBuilder out, int options) {
      int result = c;
      int props = this.trie.get(c);
      if(!propsHasException(props)) {
         if(getTypeFromProps(props) >= 2) {
            result = c + getDelta(props);
         }
      } else {
         int excOffset = getExceptionsOffset(props);
         int excWord = this.exceptions[excOffset++];
         if((excWord & '耀') != 0) {
            if((options & 255) == 0) {
               if(c == 73) {
                  return 105;
               }

               if(c == 304) {
                  out.append("i̇");
                  return 2;
               }
            } else {
               if(c == 73) {
                  return 305;
               }

               if(c == 304) {
                  return 105;
               }
            }
         } else if(hasSlot(excWord, 7)) {
            long value = this.getSlotValueAndOffset(excWord, 7, excOffset);
            int full = (int)value & '\uffff';
            excOffset = (int)(value >> 32) + 1;
            excOffset = excOffset + (full & 15);
            full = full >> 4 & 15;
            if(full != 0) {
               out.append(this.exceptions, excOffset, full);
               return full;
            }
         }

         int index;
         if(hasSlot(excWord, 1)) {
            index = 1;
         } else {
            if(!hasSlot(excWord, 0)) {
               return ~c;
            }

            index = 0;
         }

         result = this.getSlotValue(excWord, index, excOffset);
      }

      return result == c?~result:result;
   }

   public final boolean hasBinaryProperty(int c, int which) {
      switch(which) {
      case 22:
         return 1 == this.getType(c);
      case 23:
      case 24:
      case 25:
      case 26:
      case 28:
      case 29:
      case 31:
      case 32:
      case 33:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 54:
      default:
         return false;
      case 27:
         return this.isSoftDotted(c);
      case 30:
         return 2 == this.getType(c);
      case 34:
         return this.isCaseSensitive(c);
      case 49:
         return 0 != this.getType(c);
      case 50:
         return this.getTypeOrIgnorable(c) >> 2 != 0;
      case 51:
         dummyStringBuilder.setLength(0);
         return this.toFullLower(c, (UCaseProps.ContextIterator)null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
      case 52:
         dummyStringBuilder.setLength(0);
         return this.toFullUpper(c, (UCaseProps.ContextIterator)null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
      case 53:
         dummyStringBuilder.setLength(0);
         return this.toFullTitle(c, (UCaseProps.ContextIterator)null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
      case 55:
         dummyStringBuilder.setLength(0);
         return this.toFullLower(c, (UCaseProps.ContextIterator)null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0 || this.toFullUpper(c, (UCaseProps.ContextIterator)null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0 || this.toFullTitle(c, (UCaseProps.ContextIterator)null, dummyStringBuilder, ULocale.ROOT, rootLocCache) >= 0;
      }
   }

   private static final int getTypeFromProps(int props) {
      return props & 3;
   }

   private static final int getTypeAndIgnorableFromProps(int props) {
      return props & 7;
   }

   private static final int getDelta(int props) {
      return (short)props >> 7;
   }

   static {
      try {
         INSTANCE = new UCaseProps();
      } catch (IOException var1) {
         throw new RuntimeException(var1);
      }
   }

   public interface ContextIterator {
      void reset(int var1);

      int next();
   }

   private static final class IsAcceptable implements ICUBinary.Authenticate {
      private IsAcceptable() {
      }

      public boolean isDataVersionAcceptable(byte[] version) {
         return version[0] == 3;
      }
   }
}
