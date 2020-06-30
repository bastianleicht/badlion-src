package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.text.UnicodeSet;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public final class UBiDiProps {
   private int[] indexes;
   private int[] mirrors;
   private byte[] jgArray;
   private Trie2_16 trie;
   private static final String DATA_NAME = "ubidi";
   private static final String DATA_TYPE = "icu";
   private static final String DATA_FILE_NAME = "ubidi.icu";
   private static final byte[] FMT = new byte[]{(byte)66, (byte)105, (byte)68, (byte)105};
   private static final int IX_TRIE_SIZE = 2;
   private static final int IX_MIRROR_LENGTH = 3;
   private static final int IX_JG_START = 4;
   private static final int IX_JG_LIMIT = 5;
   private static final int IX_MAX_VALUES = 15;
   private static final int IX_TOP = 16;
   private static final int JT_SHIFT = 5;
   private static final int JOIN_CONTROL_SHIFT = 10;
   private static final int BIDI_CONTROL_SHIFT = 11;
   private static final int IS_MIRRORED_SHIFT = 12;
   private static final int MIRROR_DELTA_SHIFT = 13;
   private static final int MAX_JG_SHIFT = 16;
   private static final int CLASS_MASK = 31;
   private static final int JT_MASK = 224;
   private static final int MAX_JG_MASK = 16711680;
   private static final int ESC_MIRROR_DELTA = -4;
   private static final int MIRROR_INDEX_SHIFT = 21;
   public static final UBiDiProps INSTANCE;

   private UBiDiProps() throws IOException {
      InputStream is = ICUData.getStream("data/icudt51b/ubidi.icu");
      BufferedInputStream b = new BufferedInputStream(is, 4096);
      this.readData(b);
      b.close();
      is.close();
   }

   private void readData(InputStream is) throws IOException {
      DataInputStream inputStream = new DataInputStream(is);
      ICUBinary.readHeader(inputStream, FMT, new UBiDiProps.IsAcceptable());
      int count = inputStream.readInt();
      if(count < 16) {
         throw new IOException("indexes[0] too small in ubidi.icu");
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
            throw new IOException("ubidi.icu: not enough bytes for the trie");
         } else {
            inputStream.skipBytes(expectedTrieLength - trieLength);
            count = this.indexes[3];
            if(count > 0) {
               this.mirrors = new int[count];

               for(int var7 = 0; var7 < count; ++var7) {
                  this.mirrors[var7] = inputStream.readInt();
               }
            }

            count = this.indexes[5] - this.indexes[4];
            this.jgArray = new byte[count];

            for(int var8 = 0; var8 < count; ++var8) {
               this.jgArray[var8] = inputStream.readByte();
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

      int length = this.indexes[3];

      for(int i = 0; i < length; ++i) {
         int c = getMirrorCodePoint(this.mirrors[i]);
         set.add(c, c + 1);
      }

      int start = this.indexes[4];
      int limit = this.indexes[5];
      length = limit - start;
      byte prev = 0;

      for(int var11 = 0; var11 < length; ++var11) {
         byte jg = this.jgArray[var11];
         if(jg != prev) {
            set.add(start);
            prev = jg;
         }

         ++start;
      }

      if(prev != 0) {
         set.add(limit);
      }

   }

   public final int getMaxValue(int which) {
      int max = this.indexes[15];
      switch(which) {
      case 4096:
         return max & 31;
      case 4102:
         return (max & 16711680) >> 16;
      case 4103:
         return (max & 224) >> 5;
      default:
         return -1;
      }
   }

   public final int getClass(int c) {
      return getClassFromProps(this.trie.get(c));
   }

   public final boolean isMirrored(int c) {
      return getFlagFromProps(this.trie.get(c), 12);
   }

   public final int getMirror(int c) {
      int props = this.trie.get(c);
      int delta = (short)props >> 13;
      if(delta != -4) {
         return c + delta;
      } else {
         int length = this.indexes[3];

         for(int i = 0; i < length; ++i) {
            int m = this.mirrors[i];
            int c2 = getMirrorCodePoint(m);
            if(c == c2) {
               return getMirrorCodePoint(this.mirrors[getMirrorIndex(m)]);
            }

            if(c < c2) {
               break;
            }
         }

         return c;
      }
   }

   public final boolean isBidiControl(int c) {
      return getFlagFromProps(this.trie.get(c), 11);
   }

   public final boolean isJoinControl(int c) {
      return getFlagFromProps(this.trie.get(c), 10);
   }

   public final int getJoiningType(int c) {
      return (this.trie.get(c) & 224) >> 5;
   }

   public final int getJoiningGroup(int c) {
      int start = this.indexes[4];
      int limit = this.indexes[5];
      return start <= c && c < limit?this.jgArray[c - start] & 255:0;
   }

   private static final int getClassFromProps(int props) {
      return props & 31;
   }

   private static final boolean getFlagFromProps(int props, int shift) {
      return (props >> shift & 1) != 0;
   }

   private static final int getMirrorCodePoint(int m) {
      return m & 2097151;
   }

   private static final int getMirrorIndex(int m) {
      return m >>> 21;
   }

   static {
      try {
         INSTANCE = new UBiDiProps();
      } catch (IOException var1) {
         throw new RuntimeException(var1);
      }
   }

   private static final class IsAcceptable implements ICUBinary.Authenticate {
      private IsAcceptable() {
      }

      public boolean isDataVersionAcceptable(byte[] version) {
         return version[0] == 2;
      }
   }
}
