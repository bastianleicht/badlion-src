package com.ibm.icu.text;

import com.ibm.icu.impl.CharTrie;
import com.ibm.icu.impl.Trie;
import com.ibm.icu.text.RuleBasedBreakIterator;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class RBBIDataWrapper {
   RBBIDataWrapper.RBBIDataHeader fHeader;
   short[] fFTable;
   short[] fRTable;
   short[] fSFTable;
   short[] fSRTable;
   CharTrie fTrie;
   String fRuleSource;
   int[] fStatusTable;
   static final int DH_SIZE = 24;
   static final int DH_MAGIC = 0;
   static final int DH_FORMATVERSION = 1;
   static final int DH_LENGTH = 2;
   static final int DH_CATCOUNT = 3;
   static final int DH_FTABLE = 4;
   static final int DH_FTABLELEN = 5;
   static final int DH_RTABLE = 6;
   static final int DH_RTABLELEN = 7;
   static final int DH_SFTABLE = 8;
   static final int DH_SFTABLELEN = 9;
   static final int DH_SRTABLE = 10;
   static final int DH_SRTABLELEN = 11;
   static final int DH_TRIE = 12;
   static final int DH_TRIELEN = 13;
   static final int DH_RULESOURCE = 14;
   static final int DH_RULESOURCELEN = 15;
   static final int DH_STATUSTABLE = 16;
   static final int DH_STATUSTABLELEN = 17;
   static final int ACCEPTING = 0;
   static final int LOOKAHEAD = 1;
   static final int TAGIDX = 2;
   static final int RESERVED = 3;
   static final int NEXTSTATES = 4;
   static final int NUMSTATES = 0;
   static final int ROWLEN = 2;
   static final int FLAGS = 4;
   static final int RESERVED_2 = 6;
   static final int ROW_DATA = 8;
   static final int RBBI_LOOKAHEAD_HARD_BREAK = 1;
   static final int RBBI_BOF_REQUIRED = 2;
   static RBBIDataWrapper.TrieFoldingFunc fTrieFoldingFunc = new RBBIDataWrapper.TrieFoldingFunc();

   int getRowIndex(int state) {
      return 8 + state * (this.fHeader.fCatCount + 4);
   }

   static RBBIDataWrapper get(InputStream is) throws IOException {
      DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
      RBBIDataWrapper This = new RBBIDataWrapper();
      dis.skip(128L);
      This.fHeader = new RBBIDataWrapper.RBBIDataHeader();
      This.fHeader.fMagic = dis.readInt();
      This.fHeader.fVersion = dis.readInt();
      This.fHeader.fFormatVersion[0] = (byte)(This.fHeader.fVersion >> 24);
      This.fHeader.fFormatVersion[1] = (byte)(This.fHeader.fVersion >> 16);
      This.fHeader.fFormatVersion[2] = (byte)(This.fHeader.fVersion >> 8);
      This.fHeader.fFormatVersion[3] = (byte)This.fHeader.fVersion;
      This.fHeader.fLength = dis.readInt();
      This.fHeader.fCatCount = dis.readInt();
      This.fHeader.fFTable = dis.readInt();
      This.fHeader.fFTableLen = dis.readInt();
      This.fHeader.fRTable = dis.readInt();
      This.fHeader.fRTableLen = dis.readInt();
      This.fHeader.fSFTable = dis.readInt();
      This.fHeader.fSFTableLen = dis.readInt();
      This.fHeader.fSRTable = dis.readInt();
      This.fHeader.fSRTableLen = dis.readInt();
      This.fHeader.fTrie = dis.readInt();
      This.fHeader.fTrieLen = dis.readInt();
      This.fHeader.fRuleSource = dis.readInt();
      This.fHeader.fRuleSourceLen = dis.readInt();
      This.fHeader.fStatusTable = dis.readInt();
      This.fHeader.fStatusTableLen = dis.readInt();
      dis.skip(24L);
      if(This.fHeader.fMagic == '놠' && (This.fHeader.fVersion == 1 || This.fHeader.fFormatVersion[0] == 3)) {
         int pos = 96;
         if(This.fHeader.fFTable >= pos && This.fHeader.fFTable <= This.fHeader.fLength) {
            dis.skip((long)(This.fHeader.fFTable - pos));
            pos = This.fHeader.fFTable;
            This.fFTable = new short[This.fHeader.fFTableLen / 2];

            for(int i = 0; i < This.fFTable.length; ++i) {
               This.fFTable[i] = dis.readShort();
               pos += 2;
            }

            dis.skip((long)(This.fHeader.fRTable - pos));
            pos = This.fHeader.fRTable;
            This.fRTable = new short[This.fHeader.fRTableLen / 2];

            for(int var6 = 0; var6 < This.fRTable.length; ++var6) {
               This.fRTable[var6] = dis.readShort();
               pos += 2;
            }

            if(This.fHeader.fSFTableLen > 0) {
               dis.skip((long)(This.fHeader.fSFTable - pos));
               pos = This.fHeader.fSFTable;
               This.fSFTable = new short[This.fHeader.fSFTableLen / 2];

               for(int var7 = 0; var7 < This.fSFTable.length; ++var7) {
                  This.fSFTable[var7] = dis.readShort();
                  pos += 2;
               }
            }

            if(This.fHeader.fSRTableLen > 0) {
               dis.skip((long)(This.fHeader.fSRTable - pos));
               pos = This.fHeader.fSRTable;
               This.fSRTable = new short[This.fHeader.fSRTableLen / 2];

               for(int var8 = 0; var8 < This.fSRTable.length; ++var8) {
                  This.fSRTable[var8] = dis.readShort();
                  pos += 2;
               }
            }

            dis.skip((long)(This.fHeader.fTrie - pos));
            pos = This.fHeader.fTrie;
            dis.mark(This.fHeader.fTrieLen + 100);
            This.fTrie = new CharTrie(dis, fTrieFoldingFunc);
            dis.reset();
            if(pos > This.fHeader.fStatusTable) {
               throw new IOException("Break iterator Rule data corrupt");
            } else {
               dis.skip((long)(This.fHeader.fStatusTable - pos));
               pos = This.fHeader.fStatusTable;
               This.fStatusTable = new int[This.fHeader.fStatusTableLen / 4];

               for(int var9 = 0; var9 < This.fStatusTable.length; ++var9) {
                  This.fStatusTable[var9] = dis.readInt();
                  pos += 4;
               }

               if(pos > This.fHeader.fRuleSource) {
                  throw new IOException("Break iterator Rule data corrupt");
               } else {
                  dis.skip((long)(This.fHeader.fRuleSource - pos));
                  pos = This.fHeader.fRuleSource;
                  StringBuilder sb = new StringBuilder(This.fHeader.fRuleSourceLen / 2);

                  for(int var10 = 0; var10 < This.fHeader.fRuleSourceLen; var10 += 2) {
                     sb.append(dis.readChar());
                     pos += 2;
                  }

                  This.fRuleSource = sb.toString();
                  if(RuleBasedBreakIterator.fDebugEnv != null && RuleBasedBreakIterator.fDebugEnv.indexOf("data") >= 0) {
                     This.dump();
                  }

                  return This;
               }
            }
         } else {
            throw new IOException("Break iterator Rule data corrupt");
         }
      } else {
         throw new IOException("Break Iterator Rule Data Magic Number Incorrect, or unsupported data version.");
      }
   }

   static final int getNumStates(short[] table) {
      int hi = table[0];
      int lo = table[1];
      int val = (hi << 16) + (lo & '\uffff');
      return val;
   }

   void dump() {
      if(this.fFTable.length == 0) {
         throw new NullPointerException();
      } else {
         System.out.println("RBBI Data Wrapper dump ...");
         System.out.println();
         System.out.println("Forward State Table");
         this.dumpTable(this.fFTable);
         System.out.println("Reverse State Table");
         this.dumpTable(this.fRTable);
         System.out.println("Forward Safe Points Table");
         this.dumpTable(this.fSFTable);
         System.out.println("Reverse Safe Points Table");
         this.dumpTable(this.fSRTable);
         this.dumpCharCategories();
         System.out.println("Source Rules: " + this.fRuleSource);
      }
   }

   public static String intToString(int n, int width) {
      StringBuilder dest = new StringBuilder(width);
      dest.append(n);

      while(dest.length() < width) {
         dest.insert(0, ' ');
      }

      return dest.toString();
   }

   public static String intToHexString(int n, int width) {
      StringBuilder dest = new StringBuilder(width);
      dest.append(Integer.toHexString(n));

      while(dest.length() < width) {
         dest.insert(0, ' ');
      }

      return dest.toString();
   }

   private void dumpTable(short[] table) {
      if(table == null) {
         System.out.println("  -- null -- ");
      } else {
         StringBuilder header = new StringBuilder(" Row  Acc Look  Tag");

         for(int n = 0; n < this.fHeader.fCatCount; ++n) {
            header.append(intToString(n, 5));
         }

         System.out.println(header.toString());

         for(int var5 = 0; var5 < header.length(); ++var5) {
            System.out.print("-");
         }

         System.out.println();

         for(int state = 0; state < getNumStates(table); ++state) {
            this.dumpRow(table, state);
         }

         System.out.println();
      }

   }

   private void dumpRow(short[] table, int state) {
      StringBuilder dest = new StringBuilder(this.fHeader.fCatCount * 5 + 20);
      dest.append(intToString(state, 4));
      int row = this.getRowIndex(state);
      if(table[row + 0] != 0) {
         dest.append(intToString(table[row + 0], 5));
      } else {
         dest.append("     ");
      }

      if(table[row + 1] != 0) {
         dest.append(intToString(table[row + 1], 5));
      } else {
         dest.append("     ");
      }

      dest.append(intToString(table[row + 2], 5));

      for(int col = 0; col < this.fHeader.fCatCount; ++col) {
         dest.append(intToString(table[row + 4 + col], 5));
      }

      System.out.println(dest);
   }

   private void dumpCharCategories() {
      int n = this.fHeader.fCatCount;
      String[] catStrings = new String[n + 1];
      int rangeStart = 0;
      int rangeEnd = 0;
      int lastCat = -1;
      int[] lastNewline = new int[n + 1];

      for(int category = 0; category <= this.fHeader.fCatCount; ++category) {
         catStrings[category] = "";
      }

      System.out.println("\nCharacter Categories");
      System.out.println("--------------------");

      for(int char32 = 0; char32 <= 1114111; ++char32) {
         int var9 = this.fTrie.getCodePointValue(char32);
         var9 = var9 & -16385;
         if(var9 < 0 || var9 > this.fHeader.fCatCount) {
            System.out.println("Error, bad category " + Integer.toHexString(var9) + " for char " + Integer.toHexString(char32));
            break;
         }

         if(var9 == lastCat) {
            rangeEnd = char32;
         } else {
            if(lastCat >= 0) {
               if(catStrings[lastCat].length() > lastNewline[lastCat] + 70) {
                  lastNewline[lastCat] = catStrings[lastCat].length() + 10;
                  catStrings[lastCat] = catStrings[lastCat] + "\n       ";
               }

               catStrings[lastCat] = catStrings[lastCat] + " " + Integer.toHexString(rangeStart);
               if(rangeEnd != rangeStart) {
                  catStrings[lastCat] = catStrings[lastCat] + "-" + Integer.toHexString(rangeEnd);
               }
            }

            lastCat = var9;
            rangeEnd = char32;
            rangeStart = char32;
         }
      }

      catStrings[lastCat] = catStrings[lastCat] + " " + Integer.toHexString(rangeStart);
      if(rangeEnd != rangeStart) {
         catStrings[lastCat] = catStrings[lastCat] + "-" + Integer.toHexString(rangeEnd);
      }

      for(int var11 = 0; var11 <= this.fHeader.fCatCount; ++var11) {
         System.out.println(intToString(var11, 5) + "  " + catStrings[var11]);
      }

      System.out.println();
   }

   static final class RBBIDataHeader {
      int fMagic = 0;
      int fVersion;
      byte[] fFormatVersion = new byte[4];
      int fLength;
      int fCatCount;
      int fFTable;
      int fFTableLen;
      int fRTable;
      int fRTableLen;
      int fSFTable;
      int fSFTableLen;
      int fSRTable;
      int fSRTableLen;
      int fTrie;
      int fTrieLen;
      int fRuleSource;
      int fRuleSourceLen;
      int fStatusTable;
      int fStatusTableLen;
   }

   static class TrieFoldingFunc implements Trie.DataManipulate {
      public int getFoldingOffset(int data) {
         return (data & '耀') != 0?data & 32767:0;
      }
   }
}
