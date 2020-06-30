package com.ibm.icu.text;

import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BidiClassifier;
import com.ibm.icu.text.BidiLine;
import com.ibm.icu.text.BidiRun;
import com.ibm.icu.text.BidiWriter;
import com.ibm.icu.text.UTF16;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;

public class Bidi {
   public static final byte LEVEL_DEFAULT_LTR = 126;
   public static final byte LEVEL_DEFAULT_RTL = 127;
   public static final byte MAX_EXPLICIT_LEVEL = 61;
   public static final byte LEVEL_OVERRIDE = -128;
   public static final int MAP_NOWHERE = -1;
   public static final byte LTR = 0;
   public static final byte RTL = 1;
   public static final byte MIXED = 2;
   public static final byte NEUTRAL = 3;
   public static final short KEEP_BASE_COMBINING = 1;
   public static final short DO_MIRRORING = 2;
   public static final short INSERT_LRM_FOR_NUMERIC = 4;
   public static final short REMOVE_BIDI_CONTROLS = 8;
   public static final short OUTPUT_REVERSE = 16;
   public static final short REORDER_DEFAULT = 0;
   public static final short REORDER_NUMBERS_SPECIAL = 1;
   public static final short REORDER_GROUP_NUMBERS_WITH_R = 2;
   public static final short REORDER_RUNS_ONLY = 3;
   public static final short REORDER_INVERSE_NUMBERS_AS_L = 4;
   public static final short REORDER_INVERSE_LIKE_DIRECT = 5;
   public static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6;
   static final short REORDER_COUNT = 7;
   static final short REORDER_LAST_LOGICAL_TO_VISUAL = 1;
   public static final int OPTION_DEFAULT = 0;
   public static final int OPTION_INSERT_MARKS = 1;
   public static final int OPTION_REMOVE_CONTROLS = 2;
   public static final int OPTION_STREAMING = 4;
   static final byte L = 0;
   static final byte R = 1;
   static final byte EN = 2;
   static final byte ES = 3;
   static final byte ET = 4;
   static final byte AN = 5;
   static final byte CS = 6;
   static final byte B = 7;
   static final byte S = 8;
   static final byte WS = 9;
   static final byte ON = 10;
   static final byte LRE = 11;
   static final byte LRO = 12;
   static final byte AL = 13;
   static final byte RLE = 14;
   static final byte RLO = 15;
   static final byte PDF = 16;
   static final byte NSM = 17;
   static final byte BN = 18;
   static final int MASK_R_AL = 8194;
   public static final int CLASS_DEFAULT = 19;
   private static final char CR = '\r';
   private static final char LF = '\n';
   static final int LRM_BEFORE = 1;
   static final int LRM_AFTER = 2;
   static final int RLM_BEFORE = 4;
   static final int RLM_AFTER = 8;
   Bidi paraBidi;
   final UBiDiProps bdp;
   char[] text;
   int originalLength;
   int length;
   int resultLength;
   boolean mayAllocateText;
   boolean mayAllocateRuns;
   byte[] dirPropsMemory;
   byte[] levelsMemory;
   byte[] dirProps;
   byte[] levels;
   boolean isInverse;
   int reorderingMode;
   int reorderingOptions;
   boolean orderParagraphsLTR;
   byte paraLevel;
   byte defaultParaLevel;
   String prologue;
   String epilogue;
   Bidi.ImpTabPair impTabPair;
   byte direction;
   int flags;
   int lastArabicPos;
   int trailingWSStart;
   int paraCount;
   int[] parasMemory;
   int[] paras;
   int[] simpleParas;
   int runCount;
   BidiRun[] runsMemory;
   BidiRun[] runs;
   BidiRun[] simpleRuns;
   int[] logicalToVisualRunsMap;
   boolean isGoodLogicalToVisualRunsMap;
   BidiClassifier customClassifier;
   Bidi.InsertPoints insertPoints;
   int controlCount;
   static final byte CONTEXT_RTL_SHIFT = 6;
   static final byte CONTEXT_RTL = 64;
   static final int DirPropFlagMultiRuns = DirPropFlag((byte)31);
   static final int[] DirPropFlagLR = new int[]{DirPropFlag((byte)0), DirPropFlag((byte)1)};
   static final int[] DirPropFlagE = new int[]{DirPropFlag((byte)11), DirPropFlag((byte)14)};
   static final int[] DirPropFlagO = new int[]{DirPropFlag((byte)12), DirPropFlag((byte)15)};
   static final int MASK_LTR = DirPropFlag((byte)0) | DirPropFlag((byte)2) | DirPropFlag((byte)5) | DirPropFlag((byte)11) | DirPropFlag((byte)12);
   static final int MASK_RTL = DirPropFlag((byte)1) | DirPropFlag((byte)13) | DirPropFlag((byte)14) | DirPropFlag((byte)15);
   static final int MASK_LRX = DirPropFlag((byte)11) | DirPropFlag((byte)12);
   static final int MASK_RLX = DirPropFlag((byte)14) | DirPropFlag((byte)15);
   static final int MASK_OVERRIDE = DirPropFlag((byte)12) | DirPropFlag((byte)15);
   static final int MASK_EXPLICIT = MASK_LRX | MASK_RLX | DirPropFlag((byte)16);
   static final int MASK_BN_EXPLICIT = DirPropFlag((byte)18) | MASK_EXPLICIT;
   static final int MASK_B_S = DirPropFlag((byte)7) | DirPropFlag((byte)8);
   static final int MASK_WS = MASK_B_S | DirPropFlag((byte)9) | MASK_BN_EXPLICIT;
   static final int MASK_N = DirPropFlag((byte)10) | MASK_WS;
   static final int MASK_ET_NSM_BN = DirPropFlag((byte)4) | DirPropFlag((byte)17) | MASK_BN_EXPLICIT;
   static final int MASK_POSSIBLE_N = DirPropFlag((byte)6) | DirPropFlag((byte)3) | DirPropFlag((byte)4) | MASK_N;
   static final int MASK_EMBEDDING = DirPropFlag((byte)17) | MASK_POSSIBLE_N;
   private static final int IMPTABPROPS_COLUMNS = 14;
   private static final int IMPTABPROPS_RES = 13;
   private static final short[] groupProp = new short[]{(short)0, (short)1, (short)2, (short)7, (short)8, (short)3, (short)9, (short)6, (short)5, (short)4, (short)4, (short)10, (short)10, (short)12, (short)10, (short)10, (short)10, (short)11, (short)10};
   private static final short _L = 0;
   private static final short _R = 1;
   private static final short _EN = 2;
   private static final short _AN = 3;
   private static final short _ON = 4;
   private static final short _S = 5;
   private static final short _B = 6;
   private static final short[][] impTabProps = new short[][]{{(short)1, (short)2, (short)4, (short)5, (short)7, (short)15, (short)17, (short)7, (short)9, (short)7, (short)0, (short)7, (short)3, (short)4}, {(short)1, (short)34, (short)36, (short)37, (short)39, (short)47, (short)49, (short)39, (short)41, (short)39, (short)1, (short)1, (short)35, (short)0}, {(short)33, (short)2, (short)36, (short)37, (short)39, (short)47, (short)49, (short)39, (short)41, (short)39, (short)2, (short)2, (short)35, (short)1}, {(short)33, (short)34, (short)38, (short)38, (short)40, (short)48, (short)49, (short)40, (short)40, (short)40, (short)3, (short)3, (short)3, (short)1}, {(short)33, (short)34, (short)4, (short)37, (short)39, (short)47, (short)49, (short)74, (short)11, (short)74, (short)4, (short)4, (short)35, (short)2}, {(short)33, (short)34, (short)36, (short)5, (short)39, (short)47, (short)49, (short)39, (short)41, (short)76, (short)5, (short)5, (short)35, (short)3}, {(short)33, (short)34, (short)6, (short)6, (short)40, (short)48, (short)49, (short)40, (short)40, (short)77, (short)6, (short)6, (short)35, (short)3}, {(short)33, (short)34, (short)36, (short)37, (short)7, (short)47, (short)49, (short)7, (short)78, (short)7, (short)7, (short)7, (short)35, (short)4}, {(short)33, (short)34, (short)38, (short)38, (short)8, (short)48, (short)49, (short)8, (short)8, (short)8, (short)8, (short)8, (short)35, (short)4}, {(short)33, (short)34, (short)4, (short)37, (short)7, (short)47, (short)49, (short)7, (short)9, (short)7, (short)9, (short)9, (short)35, (short)4}, {(short)97, (short)98, (short)4, (short)101, (short)135, (short)111, (short)113, (short)135, (short)142, (short)135, (short)10, (short)135, (short)99, (short)2}, {(short)33, (short)34, (short)4, (short)37, (short)39, (short)47, (short)49, (short)39, (short)11, (short)39, (short)11, (short)11, (short)35, (short)2}, {(short)97, (short)98, (short)100, (short)5, (short)135, (short)111, (short)113, (short)135, (short)142, (short)135, (short)12, (short)135, (short)99, (short)3}, {(short)97, (short)98, (short)6, (short)6, (short)136, (short)112, (short)113, (short)136, (short)136, (short)136, (short)13, (short)136, (short)99, (short)3}, {(short)33, (short)34, (short)132, (short)37, (short)7, (short)47, (short)49, (short)7, (short)14, (short)7, (short)14, (short)14, (short)35, (short)4}, {(short)33, (short)34, (short)36, (short)37, (short)39, (short)15, (short)49, (short)39, (short)41, (short)39, (short)15, (short)39, (short)35, (short)5}, {(short)33, (short)34, (short)38, (short)38, (short)40, (short)16, (short)49, (short)40, (short)40, (short)40, (short)16, (short)40, (short)35, (short)5}, {(short)33, (short)34, (short)36, (short)37, (short)39, (short)47, (short)17, (short)39, (short)41, (short)39, (short)17, (short)39, (short)35, (short)6}};
   private static final int IMPTABLEVELS_COLUMNS = 8;
   private static final int IMPTABLEVELS_RES = 7;
   private static final byte[][] impTabL_DEFAULT = new byte[][]{{(byte)0, (byte)1, (byte)0, (byte)2, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)0, (byte)1, (byte)3, (byte)3, (byte)20, (byte)20, (byte)0, (byte)1}, {(byte)0, (byte)1, (byte)0, (byte)2, (byte)21, (byte)21, (byte)0, (byte)2}, {(byte)0, (byte)1, (byte)3, (byte)3, (byte)20, (byte)20, (byte)0, (byte)2}, {(byte)32, (byte)1, (byte)3, (byte)3, (byte)4, (byte)4, (byte)32, (byte)1}, {(byte)32, (byte)1, (byte)32, (byte)2, (byte)5, (byte)5, (byte)32, (byte)1}};
   private static final byte[][] impTabR_DEFAULT = new byte[][]{{(byte)1, (byte)0, (byte)2, (byte)2, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)1, (byte)0, (byte)1, (byte)3, (byte)20, (byte)20, (byte)0, (byte)1}, {(byte)1, (byte)0, (byte)2, (byte)2, (byte)0, (byte)0, (byte)0, (byte)1}, {(byte)1, (byte)0, (byte)1, (byte)3, (byte)5, (byte)5, (byte)0, (byte)1}, {(byte)33, (byte)0, (byte)33, (byte)3, (byte)4, (byte)4, (byte)0, (byte)0}, {(byte)1, (byte)0, (byte)1, (byte)3, (byte)5, (byte)5, (byte)0, (byte)0}};
   private static final short[] impAct0 = new short[]{(short)0, (short)1, (short)2, (short)3, (short)4, (short)5, (short)6};
   private static final Bidi.ImpTabPair impTab_DEFAULT = new Bidi.ImpTabPair(impTabL_DEFAULT, impTabR_DEFAULT, impAct0, impAct0);
   private static final byte[][] impTabL_NUMBERS_SPECIAL = new byte[][]{{(byte)0, (byte)2, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)0, (byte)2, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)2}, {(byte)0, (byte)2, (byte)4, (byte)4, (byte)19, (byte)0, (byte)0, (byte)1}, {(byte)32, (byte)2, (byte)4, (byte)4, (byte)3, (byte)3, (byte)32, (byte)1}, {(byte)0, (byte)2, (byte)4, (byte)4, (byte)19, (byte)19, (byte)0, (byte)2}};
   private static final Bidi.ImpTabPair impTab_NUMBERS_SPECIAL = new Bidi.ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_DEFAULT, impAct0, impAct0);
   private static final byte[][] impTabL_GROUP_NUMBERS_WITH_R = new byte[][]{{(byte)0, (byte)3, (byte)17, (byte)17, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)32, (byte)3, (byte)1, (byte)1, (byte)2, (byte)32, (byte)32, (byte)2}, {(byte)32, (byte)3, (byte)1, (byte)1, (byte)2, (byte)32, (byte)32, (byte)1}, {(byte)0, (byte)3, (byte)5, (byte)5, (byte)20, (byte)0, (byte)0, (byte)1}, {(byte)32, (byte)3, (byte)5, (byte)5, (byte)4, (byte)32, (byte)32, (byte)1}, {(byte)0, (byte)3, (byte)5, (byte)5, (byte)20, (byte)0, (byte)0, (byte)2}};
   private static final byte[][] impTabR_GROUP_NUMBERS_WITH_R = new byte[][]{{(byte)2, (byte)0, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)2, (byte)0, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)1}, {(byte)2, (byte)0, (byte)20, (byte)20, (byte)19, (byte)0, (byte)0, (byte)1}, {(byte)34, (byte)0, (byte)4, (byte)4, (byte)3, (byte)0, (byte)0, (byte)0}, {(byte)34, (byte)0, (byte)4, (byte)4, (byte)3, (byte)0, (byte)0, (byte)1}};
   private static final Bidi.ImpTabPair impTab_GROUP_NUMBERS_WITH_R = new Bidi.ImpTabPair(impTabL_GROUP_NUMBERS_WITH_R, impTabR_GROUP_NUMBERS_WITH_R, impAct0, impAct0);
   private static final byte[][] impTabL_INVERSE_NUMBERS_AS_L = new byte[][]{{(byte)0, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)0, (byte)1, (byte)0, (byte)0, (byte)20, (byte)20, (byte)0, (byte)1}, {(byte)0, (byte)1, (byte)0, (byte)0, (byte)21, (byte)21, (byte)0, (byte)2}, {(byte)0, (byte)1, (byte)0, (byte)0, (byte)20, (byte)20, (byte)0, (byte)2}, {(byte)32, (byte)1, (byte)32, (byte)32, (byte)4, (byte)4, (byte)32, (byte)1}, {(byte)32, (byte)1, (byte)32, (byte)32, (byte)5, (byte)5, (byte)32, (byte)1}};
   private static final byte[][] impTabR_INVERSE_NUMBERS_AS_L = new byte[][]{{(byte)1, (byte)0, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)1, (byte)0, (byte)1, (byte)1, (byte)20, (byte)20, (byte)0, (byte)1}, {(byte)1, (byte)0, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)1}, {(byte)1, (byte)0, (byte)1, (byte)1, (byte)5, (byte)5, (byte)0, (byte)1}, {(byte)33, (byte)0, (byte)33, (byte)33, (byte)4, (byte)4, (byte)0, (byte)0}, {(byte)1, (byte)0, (byte)1, (byte)1, (byte)5, (byte)5, (byte)0, (byte)0}};
   private static final Bidi.ImpTabPair impTab_INVERSE_NUMBERS_AS_L = new Bidi.ImpTabPair(impTabL_INVERSE_NUMBERS_AS_L, impTabR_INVERSE_NUMBERS_AS_L, impAct0, impAct0);
   private static final byte[][] impTabR_INVERSE_LIKE_DIRECT = new byte[][]{{(byte)1, (byte)0, (byte)2, (byte)2, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)1, (byte)0, (byte)1, (byte)2, (byte)19, (byte)19, (byte)0, (byte)1}, {(byte)1, (byte)0, (byte)2, (byte)2, (byte)0, (byte)0, (byte)0, (byte)1}, {(byte)33, (byte)48, (byte)6, (byte)4, (byte)3, (byte)3, (byte)48, (byte)0}, {(byte)33, (byte)48, (byte)6, (byte)4, (byte)5, (byte)5, (byte)48, (byte)3}, {(byte)33, (byte)48, (byte)6, (byte)4, (byte)5, (byte)5, (byte)48, (byte)2}, {(byte)33, (byte)48, (byte)6, (byte)4, (byte)3, (byte)3, (byte)48, (byte)1}};
   private static final short[] impAct1 = new short[]{(short)0, (short)1, (short)11, (short)12};
   private static final Bidi.ImpTabPair impTab_INVERSE_LIKE_DIRECT = new Bidi.ImpTabPair(impTabL_DEFAULT, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
   private static final byte[][] impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS = new byte[][]{{(byte)0, (byte)99, (byte)0, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)0, (byte)99, (byte)0, (byte)1, (byte)18, (byte)48, (byte)0, (byte)4}, {(byte)32, (byte)99, (byte)32, (byte)1, (byte)2, (byte)48, (byte)32, (byte)3}, {(byte)0, (byte)99, (byte)85, (byte)86, (byte)20, (byte)48, (byte)0, (byte)3}, {(byte)48, (byte)67, (byte)85, (byte)86, (byte)4, (byte)48, (byte)48, (byte)3}, {(byte)48, (byte)67, (byte)5, (byte)86, (byte)20, (byte)48, (byte)48, (byte)4}, {(byte)48, (byte)67, (byte)85, (byte)6, (byte)20, (byte)48, (byte)48, (byte)4}};
   private static final byte[][] impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS = new byte[][]{{(byte)19, (byte)0, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)35, (byte)0, (byte)1, (byte)1, (byte)2, (byte)64, (byte)0, (byte)1}, {(byte)35, (byte)0, (byte)1, (byte)1, (byte)2, (byte)64, (byte)0, (byte)0}, {(byte)3, (byte)0, (byte)3, (byte)54, (byte)20, (byte)64, (byte)0, (byte)1}, {(byte)83, (byte)64, (byte)5, (byte)54, (byte)4, (byte)64, (byte)64, (byte)0}, {(byte)83, (byte)64, (byte)5, (byte)54, (byte)4, (byte)64, (byte)64, (byte)1}, {(byte)83, (byte)64, (byte)6, (byte)6, (byte)4, (byte)64, (byte)64, (byte)3}};
   private static final short[] impAct2 = new short[]{(short)0, (short)1, (short)7, (short)8, (short)9, (short)10};
   private static final Bidi.ImpTabPair impTab_INVERSE_LIKE_DIRECT_WITH_MARKS = new Bidi.ImpTabPair(impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
   private static final Bidi.ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL = new Bidi.ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
   private static final byte[][] impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new byte[][]{{(byte)0, (byte)98, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}, {(byte)0, (byte)98, (byte)1, (byte)1, (byte)0, (byte)48, (byte)0, (byte)4}, {(byte)0, (byte)98, (byte)84, (byte)84, (byte)19, (byte)48, (byte)0, (byte)3}, {(byte)48, (byte)66, (byte)84, (byte)84, (byte)3, (byte)48, (byte)48, (byte)3}, {(byte)48, (byte)66, (byte)4, (byte)4, (byte)19, (byte)48, (byte)48, (byte)4}};
   private static final Bidi.ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new Bidi.ImpTabPair(impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
   static final int FIRSTALLOC = 10;
   public static final int DIRECTION_LEFT_TO_RIGHT = 0;
   public static final int DIRECTION_RIGHT_TO_LEFT = 1;
   public static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126;
   public static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127;

   static int DirPropFlag(byte dir) {
      return 1 << dir;
   }

   boolean testDirPropFlagAt(int flag, int index) {
      return (DirPropFlag((byte)(this.dirProps[index] & -65)) & flag) != 0;
   }

   static byte NoContextRTL(byte dir) {
      return (byte)(dir & -65);
   }

   static int DirPropFlagNC(byte dir) {
      return 1 << (dir & -65);
   }

   static final int DirPropFlagLR(byte level) {
      return DirPropFlagLR[level & 1];
   }

   static final int DirPropFlagE(byte level) {
      return DirPropFlagE[level & 1];
   }

   static final int DirPropFlagO(byte level) {
      return DirPropFlagO[level & 1];
   }

   static byte GetLRFromLevel(byte level) {
      return (byte)(level & 1);
   }

   static boolean IsDefaultLevel(byte level) {
      return (level & 126) == 126;
   }

   byte GetParaLevelAt(int index) {
      return this.defaultParaLevel != 0?(byte)(this.dirProps[index] >> 6):this.paraLevel;
   }

   static boolean IsBidiControlChar(int c) {
      return (c & -4) == 8204 || c >= 8234 && c <= 8238;
   }

   void verifyValidPara() {
      if(this != this.paraBidi) {
         throw new IllegalStateException();
      }
   }

   void verifyValidParaOrLine() {
      Bidi para = this.paraBidi;
      if(this != para) {
         if(para == null || para != para.paraBidi) {
            throw new IllegalStateException();
         }
      }
   }

   void verifyRange(int index, int start, int limit) {
      if(index < start || index >= limit) {
         throw new IllegalArgumentException("Value " + index + " is out of range " + start + " to " + limit);
      }
   }

   public Bidi() {
      this(0, 0);
   }

   public Bidi(int maxLength, int maxRunCount) {
      this.dirPropsMemory = new byte[1];
      this.levelsMemory = new byte[1];
      this.parasMemory = new int[1];
      this.simpleParas = new int[]{0};
      this.runsMemory = new BidiRun[0];
      this.simpleRuns = new BidiRun[]{new BidiRun()};
      this.customClassifier = null;
      this.insertPoints = new Bidi.InsertPoints();
      if(maxLength >= 0 && maxRunCount >= 0) {
         this.bdp = UBiDiProps.INSTANCE;
         if(maxLength > 0) {
            this.getInitialDirPropsMemory(maxLength);
            this.getInitialLevelsMemory(maxLength);
         } else {
            this.mayAllocateText = true;
         }

         if(maxRunCount > 0) {
            if(maxRunCount > 1) {
               this.getInitialRunsMemory(maxRunCount);
            }
         } else {
            this.mayAllocateRuns = true;
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   private Object getMemory(String label, Object array, Class arrayClass, boolean mayAllocate, int sizeNeeded) {
      int len = Array.getLength(array);
      if(sizeNeeded == len) {
         return array;
      } else if(!mayAllocate) {
         if(sizeNeeded <= len) {
            return array;
         } else {
            throw new OutOfMemoryError("Failed to allocate memory for " + label);
         }
      } else {
         try {
            return Array.newInstance(arrayClass, sizeNeeded);
         } catch (Exception var8) {
            throw new OutOfMemoryError("Failed to allocate memory for " + label);
         }
      }
   }

   private void getDirPropsMemory(boolean mayAllocate, int len) {
      Object array = this.getMemory("DirProps", this.dirPropsMemory, Byte.TYPE, mayAllocate, len);
      this.dirPropsMemory = (byte[])((byte[])array);
   }

   void getDirPropsMemory(int len) {
      this.getDirPropsMemory(this.mayAllocateText, len);
   }

   private void getLevelsMemory(boolean mayAllocate, int len) {
      Object array = this.getMemory("Levels", this.levelsMemory, Byte.TYPE, mayAllocate, len);
      this.levelsMemory = (byte[])((byte[])array);
   }

   void getLevelsMemory(int len) {
      this.getLevelsMemory(this.mayAllocateText, len);
   }

   private void getRunsMemory(boolean mayAllocate, int len) {
      Object array = this.getMemory("Runs", this.runsMemory, BidiRun.class, mayAllocate, len);
      this.runsMemory = (BidiRun[])((BidiRun[])array);
   }

   void getRunsMemory(int len) {
      this.getRunsMemory(this.mayAllocateRuns, len);
   }

   private void getInitialDirPropsMemory(int len) {
      this.getDirPropsMemory(true, len);
   }

   private void getInitialLevelsMemory(int len) {
      this.getLevelsMemory(true, len);
   }

   private void getInitialParasMemory(int len) {
      Object array = this.getMemory("Paras", this.parasMemory, Integer.TYPE, true, len);
      this.parasMemory = (int[])((int[])array);
   }

   private void getInitialRunsMemory(int len) {
      this.getRunsMemory(true, len);
   }

   public void setInverse(boolean isInverse) {
      this.isInverse = isInverse;
      this.reorderingMode = isInverse?4:0;
   }

   public boolean isInverse() {
      return this.isInverse;
   }

   public void setReorderingMode(int reorderingMode) {
      if(reorderingMode >= 0 && reorderingMode < 7) {
         this.reorderingMode = reorderingMode;
         this.isInverse = reorderingMode == 4;
      }
   }

   public int getReorderingMode() {
      return this.reorderingMode;
   }

   public void setReorderingOptions(int options) {
      if((options & 2) != 0) {
         this.reorderingOptions = options & -2;
      } else {
         this.reorderingOptions = options;
      }

   }

   public int getReorderingOptions() {
      return this.reorderingOptions;
   }

   private byte firstL_R_AL() {
      byte result = 10;
      int i = 0;

      while(i < this.prologue.length()) {
         int uchar = this.prologue.codePointAt(i);
         i += Character.charCount(uchar);
         byte dirProp = (byte)this.getCustomizedClass(uchar);
         if(result == 10) {
            if(dirProp == 0 || dirProp == 1 || dirProp == 13) {
               result = dirProp;
            }
         } else if(dirProp == 7) {
            result = 10;
         }
      }

      return result;
   }

   private void getDirProps() {
      int i = 0;
      this.flags = 0;
      byte paraDirDefault = 0;
      boolean isDefaultLevel = IsDefaultLevel(this.paraLevel);
      boolean isDefaultLevelInverse = isDefaultLevel && (this.reorderingMode == 5 || this.reorderingMode == 6);
      this.lastArabicPos = -1;
      this.controlCount = 0;
      boolean removeBidiControls = (this.reorderingOptions & 2) != 0;
      int NOT_CONTEXTUAL = 0;
      int LOOKING_FOR_STRONG = 1;
      int FOUND_STRONG_CHAR = 2;
      int paraStart = 0;
      byte lastStrongDir = 0;
      int lastStrongLTR = 0;
      if((this.reorderingOptions & 4) > 0) {
         this.length = 0;
         lastStrongLTR = 0;
      }

      byte paraDir;
      int state;
      if(isDefaultLevel) {
         paraDirDefault = (byte)((this.paraLevel & 1) != 0?64:0);
         byte lastStrong;
         if(this.prologue != null && (lastStrong = this.firstL_R_AL()) != 10) {
            paraDir = (byte)(lastStrong == 0?0:64);
            state = 2;
         } else {
            paraDir = paraDirDefault;
            state = 1;
         }

         state = 1;
      } else {
         state = 0;
         paraDir = 0;
      }

      i = 0;

      while(i < this.originalLength) {
         int i0 = i;
         int uchar = UTF16.charAt(this.text, 0, this.originalLength, i);
         i += UTF16.getCharCount(uchar);
         int i1 = i - 1;
         byte dirProp = (byte)this.getCustomizedClass(uchar);
         this.flags |= DirPropFlag(dirProp);
         this.dirProps[i1] = (byte)(dirProp | paraDir);
         if(i1 > i0) {
            this.flags |= DirPropFlag((byte)18);

            while(true) {
               --i1;
               this.dirProps[i1] = (byte)(18 | paraDir);
               if(i1 <= i0) {
                  break;
               }
            }
         }

         if(state == 1) {
            if(dirProp == 0) {
               state = 2;
               if(paraDir != 0) {
                  paraDir = 0;

                  for(i1 = paraStart; i1 < i; ++i1) {
                     this.dirProps[i1] &= -65;
                  }
               }
               continue;
            }

            if(dirProp == 1 || dirProp == 13) {
               state = 2;
               if(paraDir == 0) {
                  paraDir = 64;

                  for(i1 = paraStart; i1 < i; ++i1) {
                     this.dirProps[i1] = (byte)(this.dirProps[i1] | 64);
                  }
               }
               continue;
            }
         }

         if(dirProp == 0) {
            lastStrongDir = 0;
            lastStrongLTR = i;
         } else if(dirProp == 1) {
            lastStrongDir = 64;
         } else if(dirProp == 13) {
            lastStrongDir = 64;
            this.lastArabicPos = i - 1;
         } else if(dirProp == 7) {
            if((this.reorderingOptions & 4) != 0) {
               this.length = i;
            }

            if(isDefaultLevelInverse && lastStrongDir == 64 && paraDir != lastStrongDir) {
               while(paraStart < i) {
                  this.dirProps[paraStart] = (byte)(this.dirProps[paraStart] | 64);
                  ++paraStart;
               }
            }

            if(i < this.originalLength) {
               if(uchar != 13 || this.text[i] != 10) {
                  ++this.paraCount;
               }

               if(isDefaultLevel) {
                  state = 1;
                  paraStart = i;
                  paraDir = paraDirDefault;
                  lastStrongDir = paraDirDefault;
               }
            }
         }

         if(removeBidiControls && IsBidiControlChar(uchar)) {
            ++this.controlCount;
         }
      }

      if(isDefaultLevelInverse && lastStrongDir == 64 && paraDir != lastStrongDir) {
         for(int i1 = paraStart; i1 < this.originalLength; ++i1) {
            this.dirProps[i1] = (byte)(this.dirProps[i1] | 64);
         }
      }

      if(isDefaultLevel) {
         this.paraLevel = this.GetParaLevelAt(0);
      }

      if((this.reorderingOptions & 4) > 0) {
         if(lastStrongLTR > this.length && this.GetParaLevelAt(lastStrongLTR) == 0) {
            this.length = lastStrongLTR;
         }

         if(this.length < this.originalLength) {
            --this.paraCount;
         }
      }

      this.flags |= DirPropFlagLR(this.paraLevel);
      if(this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0) {
         this.flags |= DirPropFlag((byte)0);
      }

   }

   private byte directionFromFlags() {
      return (byte)((this.flags & MASK_RTL) != 0 || (this.flags & DirPropFlag((byte)5)) != 0 && (this.flags & MASK_POSSIBLE_N) != 0?((this.flags & MASK_LTR) == 0?1:2):0);
   }

   private byte resolveExplicitLevels() {
      int i = 0;
      byte level = this.GetParaLevelAt(0);
      int paraIndex = 0;
      byte dirct = this.directionFromFlags();
      if(dirct == 2 || this.paraCount != 1) {
         if(this.paraCount == 1 && ((this.flags & MASK_EXPLICIT) == 0 || this.reorderingMode > 1)) {
            for(i = 0; i < this.length; ++i) {
               this.levels[i] = level;
            }
         } else {
            byte embeddingLevel = level;
            byte stackTop = 0;
            byte[] stack = new byte[61];
            int countOver60 = 0;
            int countOver61 = 0;
            this.flags = 0;

            for(i = 0; i < this.length; ++i) {
               byte dirProp = NoContextRTL(this.dirProps[i]);
               switch(dirProp) {
               case 7:
                  stackTop = 0;
                  countOver60 = 0;
                  countOver61 = 0;
                  level = this.GetParaLevelAt(i);
                  if(i + 1 < this.length) {
                     embeddingLevel = this.GetParaLevelAt(i + 1);
                     if(this.text[i] != 13 || this.text[i + 1] != 10) {
                        this.paras[paraIndex++] = i + 1;
                     }
                  }

                  this.flags |= DirPropFlag((byte)7);
                  break;
               case 8:
               case 9:
               case 10:
               case 13:
               case 17:
               default:
                  if(level != embeddingLevel) {
                     level = embeddingLevel;
                     if((embeddingLevel & -128) != 0) {
                        this.flags |= DirPropFlagO(embeddingLevel) | DirPropFlagMultiRuns;
                     } else {
                        this.flags |= DirPropFlagE(embeddingLevel) | DirPropFlagMultiRuns;
                     }
                  }

                  if((level & -128) == 0) {
                     this.flags |= DirPropFlag(dirProp);
                  }
                  break;
               case 11:
               case 12:
                  byte newLevel = (byte)(embeddingLevel + 2 & 126);
                  if(newLevel <= 61) {
                     stack[stackTop] = embeddingLevel;
                     ++stackTop;
                     embeddingLevel = newLevel;
                     if(dirProp == 12) {
                        embeddingLevel = (byte)(newLevel | -128);
                     }
                  } else if((embeddingLevel & 127) == 61) {
                     ++countOver61;
                  } else {
                     ++countOver60;
                  }

                  this.flags |= DirPropFlag((byte)18);
                  break;
               case 14:
               case 15:
                  byte newLevel = (byte)((embeddingLevel & 127) + 1 | 1);
                  if(newLevel <= 61) {
                     stack[stackTop] = embeddingLevel;
                     ++stackTop;
                     embeddingLevel = newLevel;
                     if(dirProp == 15) {
                        embeddingLevel = (byte)(newLevel | -128);
                     }
                  } else {
                     ++countOver61;
                  }

                  this.flags |= DirPropFlag((byte)18);
                  break;
               case 16:
                  if(countOver61 > 0) {
                     --countOver61;
                  } else if(countOver60 > 0 && (embeddingLevel & 127) != 61) {
                     --countOver60;
                  } else if(stackTop > 0) {
                     --stackTop;
                     embeddingLevel = stack[stackTop];
                  }

                  this.flags |= DirPropFlag((byte)18);
                  break;
               case 18:
                  this.flags |= DirPropFlag((byte)18);
               }

               this.levels[i] = level;
            }

            if((this.flags & MASK_EMBEDDING) != 0) {
               this.flags |= DirPropFlagLR(this.paraLevel);
            }

            if(this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0) {
               this.flags |= DirPropFlag((byte)0);
            }

            dirct = this.directionFromFlags();
         }
      }

      return dirct;
   }

   private byte checkExplicitLevels() {
      this.flags = 0;
      int paraIndex = 0;

      for(int i = 0; i < this.length; ++i) {
         byte level = this.levels[i];
         byte dirProp = NoContextRTL(this.dirProps[i]);
         if((level & -128) != 0) {
            level = (byte)(level & 127);
            this.flags |= DirPropFlagO(level);
         } else {
            this.flags |= DirPropFlagE(level) | DirPropFlag(dirProp);
         }

         if(level < this.GetParaLevelAt(i) && (0 != level || dirProp != 7) || 61 < level) {
            throw new IllegalArgumentException("level " + level + " out of bounds at " + i);
         }

         if(dirProp == 7 && i + 1 < this.length && (this.text[i] != 13 || this.text[i + 1] != 10)) {
            this.paras[paraIndex++] = i + 1;
         }
      }

      if((this.flags & MASK_EMBEDDING) != 0) {
         this.flags |= DirPropFlagLR(this.paraLevel);
      }

      return this.directionFromFlags();
   }

   private static short GetStateProps(short cell) {
      return (short)(cell & 31);
   }

   private static short GetActionProps(short cell) {
      return (short)(cell >> 5);
   }

   private static short GetState(byte cell) {
      return (short)(cell & 15);
   }

   private static short GetAction(byte cell) {
      return (short)(cell >> 4);
   }

   private void addPoint(int pos, int flag) {
      Bidi.Point point = new Bidi.Point();
      int len = this.insertPoints.points.length;
      if(len == 0) {
         this.insertPoints.points = new Bidi.Point[10];
         len = 10;
      }

      if(this.insertPoints.size >= len) {
         Bidi.Point[] savePoints = this.insertPoints.points;
         this.insertPoints.points = new Bidi.Point[len * 2];
         System.arraycopy(savePoints, 0, this.insertPoints.points, 0, len);
      }

      point.pos = pos;
      point.flag = flag;
      this.insertPoints.points[this.insertPoints.size] = point;
      ++this.insertPoints.size;
   }

   private void processPropertySeq(Bidi.LevState levState, short _prop, int start, int limit) {
      byte[][] impTab = levState.impTab;
      short[] impAct = levState.impAct;
      int start0 = start;
      short oldStateSeq = levState.state;
      byte cell = impTab[oldStateSeq][_prop];
      levState.state = GetState(cell);
      short actionSeq = impAct[GetAction(cell)];
      byte addLevel = impTab[levState.state][7];
      if(actionSeq != 0) {
         label0:
         switch(actionSeq) {
         case 1:
            levState.startON = start;
            break;
         case 2:
            start = levState.startON;
            break;
         case 3:
            if(levState.startL2EN >= 0) {
               this.addPoint(levState.startL2EN, 1);
            }

            levState.startL2EN = -1;
            if(this.insertPoints.points.length != 0 && this.insertPoints.size > this.insertPoints.confirmed) {
               for(int k = levState.lastStrongRTL + 1; k < start0; ++k) {
                  this.levels[k] = (byte)(this.levels[k] - 2 & -2);
               }

               this.insertPoints.confirmed = this.insertPoints.size;
               levState.lastStrongRTL = -1;
               if(_prop == 5) {
                  this.addPoint(start0, 1);
                  this.insertPoints.confirmed = this.insertPoints.size;
               }
            } else {
               levState.lastStrongRTL = -1;
               byte level = impTab[oldStateSeq][7];
               if((level & 1) != 0 && levState.startON > 0) {
                  start = levState.startON;
               }

               if(_prop == 5) {
                  this.addPoint(start, 1);
                  this.insertPoints.confirmed = this.insertPoints.size;
               }
            }
            break;
         case 4:
            if(this.insertPoints.points.length > 0) {
               this.insertPoints.size = this.insertPoints.confirmed;
            }

            levState.startON = -1;
            levState.startL2EN = -1;
            levState.lastStrongRTL = limit - 1;
            break;
         case 5:
            if(_prop == 3 && NoContextRTL(this.dirProps[start]) == 5 && this.reorderingMode != 6) {
               if(levState.startL2EN == -1) {
                  levState.lastStrongRTL = limit - 1;
               } else {
                  if(levState.startL2EN >= 0) {
                     this.addPoint(levState.startL2EN, 1);
                     levState.startL2EN = -2;
                  }

                  this.addPoint(start, 1);
               }
            } else if(levState.startL2EN == -1) {
               levState.startL2EN = start;
            }
            break;
         case 6:
            levState.lastStrongRTL = limit - 1;
            levState.startON = -1;
            break;
         case 7:
            int var20;
            for(var20 = start - 1; var20 >= 0 && (this.levels[var20] & 1) == 0; --var20) {
               ;
            }

            if(var20 >= 0) {
               this.addPoint(var20, 4);
               this.insertPoints.confirmed = this.insertPoints.size;
            }

            levState.startON = start;
            break;
         case 8:
            this.addPoint(start, 1);
            this.addPoint(start, 2);
            break;
         case 9:
            this.insertPoints.size = this.insertPoints.confirmed;
            if(_prop == 5) {
               this.addPoint(start, 4);
               this.insertPoints.confirmed = this.insertPoints.size;
            }
            break;
         case 10:
            byte level = (byte)(levState.runLevel + addLevel);

            for(int var19 = levState.startON; var19 < start0; ++var19) {
               if(this.levels[var19] < level) {
                  this.levels[var19] = level;
               }
            }

            this.insertPoints.confirmed = this.insertPoints.size;
            levState.startON = start0;
            break;
         case 11:
            byte level = levState.runLevel;
            int var18 = start - 1;

            while(true) {
               if(var18 < levState.startON) {
                  break label0;
               }

               if(this.levels[var18] == level + 3) {
                  while(this.levels[var18] == level + 3) {
                     int var10001 = var18--;
                     this.levels[var10001] = (byte)(this.levels[var10001] - 2);
                  }

                  while(this.levels[var18] == level) {
                     --var18;
                  }
               }

               if(this.levels[var18] == level + 2) {
                  this.levels[var18] = level;
               } else {
                  this.levels[var18] = (byte)(level + 1);
               }

               --var18;
            }
         case 12:
            byte level = (byte)(levState.runLevel + 1);
            int k = start - 1;

            while(true) {
               if(k < levState.startON) {
                  break label0;
               }

               if(this.levels[k] > level) {
                  this.levels[k] = (byte)(this.levels[k] - 2);
               }

               --k;
            }
         default:
            throw new IllegalStateException("Internal ICU error in processPropertySeq");
         }
      }

      if(addLevel != 0 || start < start0) {
         byte var17 = (byte)(levState.runLevel + addLevel);

         for(int var22 = start; var22 < limit; ++var22) {
            this.levels[var22] = var17;
         }
      }

   }

   private byte lastL_R_AL() {
      int i = this.prologue.length();

      while(i > 0) {
         int uchar = this.prologue.codePointBefore(i);
         i -= Character.charCount(uchar);
         byte dirProp = (byte)this.getCustomizedClass(uchar);
         if(dirProp == 0) {
            return (byte)0;
         }

         if(dirProp == 1 || dirProp == 13) {
            return (byte)1;
         }

         if(dirProp == 7) {
            return (byte)4;
         }
      }

      return (byte)4;
   }

   private byte firstL_R_AL_EN_AN() {
      int i = 0;

      while(i < this.epilogue.length()) {
         int uchar = this.epilogue.codePointAt(i);
         i += Character.charCount(uchar);
         byte dirProp = (byte)this.getCustomizedClass(uchar);
         if(dirProp == 0) {
            return (byte)0;
         }

         if(dirProp == 1 || dirProp == 13) {
            return (byte)1;
         }

         if(dirProp == 2) {
            return (byte)2;
         }

         if(dirProp == 5) {
            return (byte)3;
         }
      }

      return (byte)4;
   }

   private void resolveImplicitLevels(int start, int limit, short sor, short eor) {
      Bidi.LevState levState = new Bidi.LevState();
      short nextStrongProp = 1;
      int nextStrongPos = -1;
      boolean inverseRTL = start < this.lastArabicPos && (this.GetParaLevelAt(start) & 1) > 0 && (this.reorderingMode == 5 || this.reorderingMode == 6);
      levState.startL2EN = -1;
      levState.lastStrongRTL = -1;
      levState.state = 0;
      levState.runLevel = this.levels[start];
      levState.impTab = this.impTabPair.imptab[levState.runLevel & 1];
      levState.impAct = this.impTabPair.impact[levState.runLevel & 1];
      if(start == 0 && this.prologue != null) {
         byte lastStrong = this.lastL_R_AL();
         if(lastStrong != 4) {
            sor = (short)lastStrong;
         }
      }

      this.processPropertySeq(levState, sor, start, start);
      short stateImp;
      if(NoContextRTL(this.dirProps[start]) == 17) {
         stateImp = (short)(1 + sor);
      } else {
         stateImp = 0;
      }

      int start1 = start;
      int start2 = 0;

      for(int i = start; i <= limit; ++i) {
         short gprop;
         if(i >= limit) {
            gprop = eor;
         } else {
            short prop = (short)NoContextRTL(this.dirProps[i]);
            if(inverseRTL) {
               if(prop == 13) {
                  prop = 1;
               } else if(prop == 2) {
                  if(nextStrongPos <= i) {
                     nextStrongProp = 1;
                     nextStrongPos = limit;

                     for(int j = i + 1; j < limit; ++j) {
                        short prop1 = (short)NoContextRTL(this.dirProps[j]);
                        if(prop1 == 0 || prop1 == 1 || prop1 == 13) {
                           nextStrongProp = prop1;
                           nextStrongPos = j;
                           break;
                        }
                     }
                  }

                  if(nextStrongProp == 13) {
                     prop = 5;
                  }
               }
            }

            gprop = groupProp[prop];
         }

         short oldStateImp = stateImp;
         short cell = impTabProps[stateImp][gprop];
         stateImp = GetStateProps(cell);
         short actionImp = GetActionProps(cell);
         if(i == limit && actionImp == 0) {
            actionImp = 1;
         }

         if(actionImp != 0) {
            short resProp = impTabProps[oldStateImp][13];
            switch(actionImp) {
            case 1:
               this.processPropertySeq(levState, resProp, start1, i);
               start1 = i;
               break;
            case 2:
               start2 = i;
               break;
            case 3:
               this.processPropertySeq(levState, resProp, start1, start2);
               this.processPropertySeq(levState, (short)4, start2, i);
               start1 = i;
               break;
            case 4:
               this.processPropertySeq(levState, resProp, start1, start2);
               start1 = start2;
               start2 = i;
               break;
            default:
               throw new IllegalStateException("Internal ICU error in resolveImplicitLevels");
            }
         }
      }

      if(limit == this.length && this.epilogue != null) {
         byte firstStrong = this.firstL_R_AL_EN_AN();
         if(firstStrong != 4) {
            eor = (short)firstStrong;
         }
      }

      this.processPropertySeq(levState, eor, limit, limit);
   }

   private void adjustWSLevels() {
      if((this.flags & MASK_WS) != 0) {
         int i = this.trailingWSStart;

         while(i > 0) {
            while(i > 0) {
               --i;
               int flag;
               if(((flag = DirPropFlagNC(this.dirProps[i])) & MASK_WS) == 0) {
                  break;
               }

               if(this.orderParagraphsLTR && (flag & DirPropFlag((byte)7)) != 0) {
                  this.levels[i] = 0;
               } else {
                  this.levels[i] = this.GetParaLevelAt(i);
               }
            }

            while(i > 0) {
               --i;
               int flag = DirPropFlagNC(this.dirProps[i]);
               if((flag & MASK_BN_EXPLICIT) != 0) {
                  this.levels[i] = this.levels[i + 1];
               } else {
                  if(this.orderParagraphsLTR && (flag & DirPropFlag((byte)7)) != 0) {
                     this.levels[i] = 0;
                     break;
                  }

                  if((flag & MASK_B_S) != 0) {
                     this.levels[i] = this.GetParaLevelAt(i);
                     break;
                  }
               }
            }
         }
      }

   }

   int Bidi_Min(int x, int y) {
      return x < y?x:y;
   }

   int Bidi_Abs(int x) {
      return x >= 0?x:-x;
   }

   void setParaRunsOnly(char[] parmText, byte parmParaLevel) {
      this.reorderingMode = 0;
      int parmLength = parmText.length;
      if(parmLength == 0) {
         this.setPara((char[])parmText, parmParaLevel, (byte[])null);
         this.reorderingMode = 3;
      } else {
         int saveOptions = this.reorderingOptions;
         if((saveOptions & 1) > 0) {
            this.reorderingOptions &= -2;
            this.reorderingOptions |= 2;
         }

         parmParaLevel = (byte)(parmParaLevel & 1);
         this.setPara((char[])parmText, parmParaLevel, (byte[])null);
         byte[] saveLevels = new byte[this.length];
         System.arraycopy(this.getLevels(), 0, saveLevels, 0, this.length);
         int saveTrailingWSStart = this.trailingWSStart;
         String visualText = this.writeReordered(2);
         int[] visualMap = this.getVisualMap();
         this.reorderingOptions = saveOptions;
         int saveLength = this.length;
         byte saveDirection = this.direction;
         this.reorderingMode = 5;
         parmParaLevel = (byte)(parmParaLevel ^ 1);
         this.setPara((String)visualText, parmParaLevel, (byte[])null);
         BidiLine.getRuns(this);
         int addedRuns = 0;
         int oldRunCount = this.runCount;
         int visualStart = 0;

         int runLength;
         for(int i = 0; i < oldRunCount; visualStart += runLength) {
            runLength = this.runs[i].limit - visualStart;
            if(runLength >= 2) {
               int logicalStart = this.runs[i].start;

               for(int j = logicalStart + 1; j < logicalStart + runLength; ++j) {
                  int index = visualMap[j];
                  int index1 = visualMap[j - 1];
                  if(this.Bidi_Abs(index - index1) != 1 || saveLevels[index] != saveLevels[index1]) {
                     ++addedRuns;
                  }
               }
            }

            ++i;
         }

         if(addedRuns > 0) {
            this.getRunsMemory(oldRunCount + addedRuns);
            if(this.runCount == 1) {
               this.runsMemory[0] = this.runs[0];
            } else {
               System.arraycopy(this.runs, 0, this.runsMemory, 0, this.runCount);
            }

            this.runs = this.runsMemory;
            this.runCount += addedRuns;

            for(int var29 = oldRunCount; var29 < this.runCount; ++var29) {
               if(this.runs[var29] == null) {
                  this.runs[var29] = new BidiRun(0, 0, (byte)0);
               }
            }
         }

         for(int var30 = oldRunCount - 1; var30 >= 0; --var30) {
            int newI = var30 + addedRuns;
            runLength = var30 == 0?this.runs[0].limit:this.runs[var30].limit - this.runs[var30 - 1].limit;
            int logicalStart = this.runs[var30].start;
            int indexOddBit = this.runs[var30].level & 1;
            if(runLength < 2) {
               if(addedRuns > 0) {
                  this.runs[newI].copyFrom(this.runs[var30]);
               }

               int logicalPos = visualMap[logicalStart];
               this.runs[newI].start = logicalPos;
               this.runs[newI].level = (byte)(saveLevels[logicalPos] ^ indexOddBit);
            } else {
               int start;
               int limit;
               int step;
               if(indexOddBit > 0) {
                  start = logicalStart;
                  limit = logicalStart + runLength - 1;
                  step = 1;
               } else {
                  start = logicalStart + runLength - 1;
                  limit = logicalStart;
                  step = -1;
               }

               for(int j = start; j != limit; j += step) {
                  int index = visualMap[j];
                  int index1 = visualMap[j + step];
                  if(this.Bidi_Abs(index - index1) != 1 || saveLevels[index] != saveLevels[index1]) {
                     int logicalPos = this.Bidi_Min(visualMap[start], index);
                     this.runs[newI].start = logicalPos;
                     this.runs[newI].level = (byte)(saveLevels[logicalPos] ^ indexOddBit);
                     this.runs[newI].limit = this.runs[var30].limit;
                     this.runs[var30].limit -= this.Bidi_Abs(j - start) + 1;
                     int insertRemove = this.runs[var30].insertRemove & 10;
                     this.runs[newI].insertRemove = insertRemove;
                     this.runs[var30].insertRemove &= ~insertRemove;
                     start = j + step;
                     --addedRuns;
                     --newI;
                  }
               }

               if(addedRuns > 0) {
                  this.runs[newI].copyFrom(this.runs[var30]);
               }

               int logicalPos = this.Bidi_Min(visualMap[start], visualMap[limit]);
               this.runs[newI].start = logicalPos;
               this.runs[newI].level = (byte)(saveLevels[logicalPos] ^ indexOddBit);
            }
         }

         this.paraLevel = (byte)(this.paraLevel ^ 1);
         this.text = parmText;
         this.length = saveLength;
         this.originalLength = parmLength;
         this.direction = saveDirection;
         this.levels = saveLevels;
         this.trailingWSStart = saveTrailingWSStart;
         if(this.runCount > 1) {
            this.direction = 2;
         }

         this.reorderingMode = 3;
      }
   }

   private void setParaSuccess() {
      this.prologue = null;
      this.epilogue = null;
      this.paraBidi = this;
   }

   public void setContext(String prologue, String epilogue) {
      this.prologue = prologue != null && prologue.length() > 0?prologue:null;
      this.epilogue = epilogue != null && epilogue.length() > 0?epilogue:null;
   }

   public void setPara(String text, byte paraLevel, byte[] embeddingLevels) {
      if(text == null) {
         this.setPara(new char[0], paraLevel, embeddingLevels);
      } else {
         this.setPara(text.toCharArray(), paraLevel, embeddingLevels);
      }

   }

   public void setPara(char[] chars, byte paraLevel, byte[] embeddingLevels) {
      if(paraLevel < 126) {
         this.verifyRange(paraLevel, 0, 62);
      }

      if(chars == null) {
         chars = new char[0];
      }

      if(this.reorderingMode == 3) {
         this.setParaRunsOnly(chars, paraLevel);
      } else {
         this.paraBidi = null;
         this.text = chars;
         this.length = this.originalLength = this.resultLength = this.text.length;
         this.paraLevel = paraLevel;
         this.direction = 0;
         this.paraCount = 1;
         this.dirProps = new byte[0];
         this.levels = new byte[0];
         this.runs = new BidiRun[0];
         this.isGoodLogicalToVisualRunsMap = false;
         this.insertPoints.size = 0;
         this.insertPoints.confirmed = 0;
         if(IsDefaultLevel(paraLevel)) {
            this.defaultParaLevel = paraLevel;
         } else {
            this.defaultParaLevel = 0;
         }

         if(this.length == 0) {
            if(IsDefaultLevel(paraLevel)) {
               this.paraLevel = (byte)(this.paraLevel & 1);
               this.defaultParaLevel = 0;
            }

            if((this.paraLevel & 1) != 0) {
               this.flags = DirPropFlag((byte)1);
               this.direction = 1;
            } else {
               this.flags = DirPropFlag((byte)0);
               this.direction = 0;
            }

            this.runCount = 0;
            this.paraCount = 0;
            this.setParaSuccess();
         } else {
            this.runCount = -1;
            this.getDirPropsMemory(this.length);
            this.dirProps = this.dirPropsMemory;
            this.getDirProps();
            this.trailingWSStart = this.length;
            if(this.paraCount > 1) {
               this.getInitialParasMemory(this.paraCount);
               this.paras = this.parasMemory;
               this.paras[this.paraCount - 1] = this.length;
            } else {
               this.paras = this.simpleParas;
               this.simpleParas[0] = this.length;
            }

            if(embeddingLevels == null) {
               this.getLevelsMemory(this.length);
               this.levels = this.levelsMemory;
               this.direction = this.resolveExplicitLevels();
            } else {
               this.levels = embeddingLevels;
               this.direction = this.checkExplicitLevels();
            }

            switch(this.direction) {
            case 0:
               paraLevel = (byte)(paraLevel + 1 & -2);
               this.trailingWSStart = 0;
               break;
            case 1:
               paraLevel = (byte)(paraLevel | 1);
               this.trailingWSStart = 0;
               break;
            default:
               switch(this.reorderingMode) {
               case 0:
                  this.impTabPair = impTab_DEFAULT;
                  break;
               case 1:
                  this.impTabPair = impTab_NUMBERS_SPECIAL;
                  break;
               case 2:
                  this.impTabPair = impTab_GROUP_NUMBERS_WITH_R;
                  break;
               case 3:
                  throw new InternalError("Internal ICU error in setPara");
               case 4:
                  this.impTabPair = impTab_INVERSE_NUMBERS_AS_L;
                  break;
               case 5:
                  if((this.reorderingOptions & 1) != 0) {
                     this.impTabPair = impTab_INVERSE_LIKE_DIRECT_WITH_MARKS;
                  } else {
                     this.impTabPair = impTab_INVERSE_LIKE_DIRECT;
                  }
                  break;
               case 6:
                  if((this.reorderingOptions & 1) != 0) {
                     this.impTabPair = impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS;
                  } else {
                     this.impTabPair = impTab_INVERSE_FOR_NUMBERS_SPECIAL;
                  }
               }

               if(embeddingLevels == null && this.paraCount <= 1 && (this.flags & DirPropFlagMultiRuns) == 0) {
                  this.resolveImplicitLevels(0, this.length, (short)GetLRFromLevel(this.GetParaLevelAt(0)), (short)GetLRFromLevel(this.GetParaLevelAt(this.length - 1)));
               } else {
                  int limit = 0;
                  byte level = this.GetParaLevelAt(0);
                  byte nextLevel = this.levels[0];
                  short eor;
                  if(level < nextLevel) {
                     eor = (short)GetLRFromLevel(nextLevel);
                  } else {
                     eor = (short)GetLRFromLevel(level);
                  }

                  while(true) {
                     int start = limit;
                     level = nextLevel;
                     short sor;
                     if(limit > 0 && NoContextRTL(this.dirProps[limit - 1]) == 7) {
                        sor = (short)GetLRFromLevel(this.GetParaLevelAt(limit));
                     } else {
                        sor = eor;
                     }

                     while(true) {
                        ++limit;
                        if(limit >= this.length || this.levels[limit] != level) {
                           break;
                        }
                     }

                     if(limit < this.length) {
                        nextLevel = this.levels[limit];
                     } else {
                        nextLevel = this.GetParaLevelAt(this.length - 1);
                     }

                     if((level & 127) < (nextLevel & 127)) {
                        eor = (short)GetLRFromLevel(nextLevel);
                     } else {
                        eor = (short)GetLRFromLevel(level);
                     }

                     if((level & -128) == 0) {
                        this.resolveImplicitLevels(start, limit, sor, eor);
                     } else {
                        while(true) {
                           int var10001 = start++;
                           this.levels[var10001] = (byte)(this.levels[var10001] & 127);
                           if(start >= limit) {
                              break;
                           }
                        }
                     }

                     if(limit >= this.length) {
                        break;
                     }
                  }
               }

               this.adjustWSLevels();
            }

            if(this.defaultParaLevel > 0 && (this.reorderingOptions & 1) != 0 && (this.reorderingMode == 5 || this.reorderingMode == 6)) {
               for(int i = 0; i < this.paraCount; ++i) {
                  int last = this.paras[i] - 1;
                  if((this.dirProps[last] & 64) != 0) {
                     int start = i == 0?0:this.paras[i - 1];

                     for(int j = last; j >= start; --j) {
                        byte dirProp = NoContextRTL(this.dirProps[j]);
                        if(dirProp == 0) {
                           if(j < last) {
                              while(NoContextRTL(this.dirProps[last]) == 7) {
                                 --last;
                              }
                           }

                           this.addPoint(last, 4);
                           break;
                        }

                        if((DirPropFlag(dirProp) & 8194) != 0) {
                           break;
                        }
                     }
                  }
               }
            }

            if((this.reorderingOptions & 2) != 0) {
               this.resultLength -= this.controlCount;
            } else {
               this.resultLength += this.insertPoints.size;
            }

            this.setParaSuccess();
         }
      }
   }

   public void setPara(AttributedCharacterIterator paragraph) {
      Boolean runDirection = (Boolean)paragraph.getAttribute(TextAttribute.RUN_DIRECTION);
      byte paraLvl;
      if(runDirection == null) {
         paraLvl = 126;
      } else {
         paraLvl = (byte)(runDirection.equals(TextAttribute.RUN_DIRECTION_LTR)?0:1);
      }

      byte[] lvls = null;
      int len = paragraph.getEndIndex() - paragraph.getBeginIndex();
      byte[] embeddingLevels = new byte[len];
      char[] txt = new char[len];
      int i = 0;

      for(char ch = paragraph.first(); ch != '\uffff'; ++i) {
         txt[i] = ch;
         Integer embedding = (Integer)paragraph.getAttribute(TextAttribute.BIDI_EMBEDDING);
         if(embedding != null) {
            byte level = embedding.byteValue();
            if(level != 0) {
               if(level < 0) {
                  lvls = embeddingLevels;
                  embeddingLevels[i] = (byte)(0 - level | -128);
               } else {
                  lvls = embeddingLevels;
                  embeddingLevels[i] = level;
               }
            }
         }

         ch = paragraph.next();
      }

      NumericShaper shaper = (NumericShaper)paragraph.getAttribute(TextAttribute.NUMERIC_SHAPING);
      if(shaper != null) {
         shaper.shape(txt, 0, len);
      }

      this.setPara(txt, paraLvl, lvls);
   }

   public void orderParagraphsLTR(boolean ordarParaLTR) {
      this.orderParagraphsLTR = ordarParaLTR;
   }

   public boolean isOrderParagraphsLTR() {
      return this.orderParagraphsLTR;
   }

   public byte getDirection() {
      this.verifyValidParaOrLine();
      return this.direction;
   }

   public String getTextAsString() {
      this.verifyValidParaOrLine();
      return new String(this.text);
   }

   public char[] getText() {
      this.verifyValidParaOrLine();
      return this.text;
   }

   public int getLength() {
      this.verifyValidParaOrLine();
      return this.originalLength;
   }

   public int getProcessedLength() {
      this.verifyValidParaOrLine();
      return this.length;
   }

   public int getResultLength() {
      this.verifyValidParaOrLine();
      return this.resultLength;
   }

   public byte getParaLevel() {
      this.verifyValidParaOrLine();
      return this.paraLevel;
   }

   public int countParagraphs() {
      this.verifyValidParaOrLine();
      return this.paraCount;
   }

   public BidiRun getParagraphByIndex(int paraIndex) {
      this.verifyValidParaOrLine();
      this.verifyRange(paraIndex, 0, this.paraCount);
      Bidi bidi = this.paraBidi;
      int paraStart;
      if(paraIndex == 0) {
         paraStart = 0;
      } else {
         paraStart = bidi.paras[paraIndex - 1];
      }

      BidiRun bidiRun = new BidiRun();
      bidiRun.start = paraStart;
      bidiRun.limit = bidi.paras[paraIndex];
      bidiRun.level = this.GetParaLevelAt(paraStart);
      return bidiRun;
   }

   public BidiRun getParagraph(int charIndex) {
      this.verifyValidParaOrLine();
      Bidi bidi = this.paraBidi;
      this.verifyRange(charIndex, 0, bidi.length);

      int paraIndex;
      for(paraIndex = 0; charIndex >= bidi.paras[paraIndex]; ++paraIndex) {
         ;
      }

      return this.getParagraphByIndex(paraIndex);
   }

   public int getParagraphIndex(int charIndex) {
      this.verifyValidParaOrLine();
      Bidi bidi = this.paraBidi;
      this.verifyRange(charIndex, 0, bidi.length);

      int paraIndex;
      for(paraIndex = 0; charIndex >= bidi.paras[paraIndex]; ++paraIndex) {
         ;
      }

      return paraIndex;
   }

   public void setCustomClassifier(BidiClassifier classifier) {
      this.customClassifier = classifier;
   }

   public BidiClassifier getCustomClassifier() {
      return this.customClassifier;
   }

   public int getCustomizedClass(int c) {
      int dir;
      return this.customClassifier != null && (dir = this.customClassifier.classify(c)) != 19?dir:this.bdp.getClass(c);
   }

   public Bidi setLine(int start, int limit) {
      this.verifyValidPara();
      this.verifyRange(start, 0, limit);
      this.verifyRange(limit, 0, this.length + 1);
      if(this.getParagraphIndex(start) != this.getParagraphIndex(limit - 1)) {
         throw new IllegalArgumentException();
      } else {
         return BidiLine.setLine(this, start, limit);
      }
   }

   public byte getLevelAt(int charIndex) {
      this.verifyValidParaOrLine();
      this.verifyRange(charIndex, 0, this.length);
      return BidiLine.getLevelAt(this, charIndex);
   }

   public byte[] getLevels() {
      this.verifyValidParaOrLine();
      return this.length <= 0?new byte[0]:BidiLine.getLevels(this);
   }

   public BidiRun getLogicalRun(int logicalPosition) {
      this.verifyValidParaOrLine();
      this.verifyRange(logicalPosition, 0, this.length);
      return BidiLine.getLogicalRun(this, logicalPosition);
   }

   public int countRuns() {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      return this.runCount;
   }

   public BidiRun getVisualRun(int runIndex) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      this.verifyRange(runIndex, 0, this.runCount);
      return BidiLine.getVisualRun(this, runIndex);
   }

   public int getVisualIndex(int logicalIndex) {
      this.verifyValidParaOrLine();
      this.verifyRange(logicalIndex, 0, this.length);
      return BidiLine.getVisualIndex(this, logicalIndex);
   }

   public int getLogicalIndex(int visualIndex) {
      this.verifyValidParaOrLine();
      this.verifyRange(visualIndex, 0, this.resultLength);
      if(this.insertPoints.size == 0 && this.controlCount == 0) {
         if(this.direction == 0) {
            return visualIndex;
         }

         if(this.direction == 1) {
            return this.length - visualIndex - 1;
         }
      }

      BidiLine.getRuns(this);
      return BidiLine.getLogicalIndex(this, visualIndex);
   }

   public int[] getLogicalMap() {
      this.countRuns();
      return this.length <= 0?new int[0]:BidiLine.getLogicalMap(this);
   }

   public int[] getVisualMap() {
      this.countRuns();
      return this.resultLength <= 0?new int[0]:BidiLine.getVisualMap(this);
   }

   public static int[] reorderLogical(byte[] levels) {
      return BidiLine.reorderLogical(levels);
   }

   public static int[] reorderVisual(byte[] levels) {
      return BidiLine.reorderVisual(levels);
   }

   public static int[] invertMap(int[] srcMap) {
      return srcMap == null?null:BidiLine.invertMap(srcMap);
   }

   public Bidi(String paragraph, int flags) {
      this(paragraph.toCharArray(), 0, (byte[])null, 0, paragraph.length(), flags);
   }

   public Bidi(AttributedCharacterIterator paragraph) {
      this();
      this.setPara(paragraph);
   }

   public Bidi(char[] text, int textStart, byte[] embeddings, int embStart, int paragraphLength, int flags) {
      this();
      byte paraLvl;
      switch(flags) {
      case 0:
      default:
         paraLvl = 0;
         break;
      case 1:
         paraLvl = 1;
         break;
      case 126:
         paraLvl = 126;
         break;
      case 127:
         paraLvl = 127;
      }

      byte[] paraEmbeddings;
      if(embeddings == null) {
         paraEmbeddings = null;
      } else {
         paraEmbeddings = new byte[paragraphLength];

         for(int i = 0; i < paragraphLength; ++i) {
            byte lev = embeddings[i + embStart];
            if(lev < 0) {
               lev = (byte)(-lev | -128);
            } else if(lev == 0) {
               lev = paraLvl;
               if(paraLvl > 61) {
                  lev = (byte)(paraLvl & 1);
               }
            }

            paraEmbeddings[i] = lev;
         }
      }

      if(textStart == 0 && embStart == 0 && paragraphLength == text.length) {
         this.setPara(text, paraLvl, paraEmbeddings);
      } else {
         char[] paraText = new char[paragraphLength];
         System.arraycopy(text, textStart, paraText, 0, paragraphLength);
         this.setPara(paraText, paraLvl, paraEmbeddings);
      }

   }

   public Bidi createLineBidi(int lineStart, int lineLimit) {
      return this.setLine(lineStart, lineLimit);
   }

   public boolean isMixed() {
      return !this.isLeftToRight() && !this.isRightToLeft();
   }

   public boolean isLeftToRight() {
      return this.getDirection() == 0 && (this.paraLevel & 1) == 0;
   }

   public boolean isRightToLeft() {
      return this.getDirection() == 1 && (this.paraLevel & 1) == 1;
   }

   public boolean baseIsLeftToRight() {
      return this.getParaLevel() == 0;
   }

   public int getBaseLevel() {
      return this.getParaLevel();
   }

   public int getRunCount() {
      return this.countRuns();
   }

   void getLogicalToVisualRunsMap() {
      if(!this.isGoodLogicalToVisualRunsMap) {
         int count = this.countRuns();
         if(this.logicalToVisualRunsMap == null || this.logicalToVisualRunsMap.length < count) {
            this.logicalToVisualRunsMap = new int[count];
         }

         long[] keys = new long[count];

         for(int i = 0; i < count; ++i) {
            keys[i] = ((long)this.runs[i].start << 32) + (long)i;
         }

         Arrays.sort(keys);

         for(int var4 = 0; var4 < count; ++var4) {
            this.logicalToVisualRunsMap[var4] = (int)(keys[var4] & -1L);
         }

         this.isGoodLogicalToVisualRunsMap = true;
      }
   }

   public int getRunLevel(int run) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      this.verifyRange(run, 0, this.runCount);
      this.getLogicalToVisualRunsMap();
      return this.runs[this.logicalToVisualRunsMap[run]].level;
   }

   public int getRunStart(int run) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      this.verifyRange(run, 0, this.runCount);
      this.getLogicalToVisualRunsMap();
      return this.runs[this.logicalToVisualRunsMap[run]].start;
   }

   public int getRunLimit(int run) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      this.verifyRange(run, 0, this.runCount);
      this.getLogicalToVisualRunsMap();
      int idx = this.logicalToVisualRunsMap[run];
      int len = idx == 0?this.runs[idx].limit:this.runs[idx].limit - this.runs[idx - 1].limit;
      return this.runs[idx].start + len;
   }

   public static boolean requiresBidi(char[] text, int start, int limit) {
      int RTLMask = '\ue022';

      for(int i = start; i < limit; ++i) {
         if((1 << UCharacter.getDirection(text[i]) & '\ue022') != 0) {
            return true;
         }
      }

      return false;
   }

   public static void reorderVisually(byte[] levels, int levelStart, Object[] objects, int objectStart, int count) {
      byte[] reorderLevels = new byte[count];
      System.arraycopy(levels, levelStart, reorderLevels, 0, count);
      int[] indexMap = reorderVisual(reorderLevels);
      Object[] temp = new Object[count];
      System.arraycopy(objects, objectStart, temp, 0, count);

      for(int i = 0; i < count; ++i) {
         objects[objectStart + i] = temp[indexMap[i]];
      }

   }

   public String writeReordered(int options) {
      this.verifyValidParaOrLine();
      return this.length == 0?"":BidiWriter.writeReordered(this, options);
   }

   public static String writeReverse(String src, int options) {
      if(src == null) {
         throw new IllegalArgumentException();
      } else {
         return src.length() > 0?BidiWriter.writeReverse(src, options):"";
      }
   }

   public static byte getBaseDirection(CharSequence paragraph) {
      if(paragraph != null && paragraph.length() != 0) {
         int length = paragraph.length();

         for(int i = 0; i < length; i = UCharacter.offsetByCodePoints(paragraph, i, 1)) {
            int c = UCharacter.codePointAt(paragraph, i);
            byte direction = UCharacter.getDirectionality(c);
            if(direction == 0) {
               return (byte)0;
            }

            if(direction == 1 || direction == 13) {
               return (byte)1;
            }
         }

         return (byte)3;
      } else {
         return (byte)3;
      }
   }

   private static class ImpTabPair {
      byte[][][] imptab;
      short[][] impact;

      ImpTabPair(byte[][] table1, byte[][] table2, short[] act1, short[] act2) {
         this.imptab = new byte[][][]{table1, table2};
         this.impact = new short[][]{act1, act2};
      }
   }

   static class InsertPoints {
      int size;
      int confirmed;
      Bidi.Point[] points = new Bidi.Point[0];
   }

   private static class LevState {
      byte[][] impTab;
      short[] impAct;
      int startON;
      int startL2EN;
      int lastStrongRTL;
      short state;
      byte runLevel;

      private LevState() {
      }
   }

   static class Point {
      int pos;
      int flag;
   }
}
